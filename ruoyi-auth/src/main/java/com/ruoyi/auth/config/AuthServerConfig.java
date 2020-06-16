package com.ruoyi.auth.config;

import java.util.*;
import javax.sql.DataSource;

import com.ruoyi.common.security.granter.PasswordCustomTokenGranter;
import com.ruoyi.common.security.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import com.ruoyi.auth.exception.CustomWebResponseExceptionTranslator;
import com.ruoyi.common.core.constant.CacheConstants;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.security.domain.LoginUser;
import com.ruoyi.common.security.service.RedisClientDetailsService;

/**
 * OAuth2 认证服务配置
 *
 * @author ruoyi
 */
@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter
{
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

//    @Autowired
//    private UserDetailsService userDetailsService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private TokenEnhancer tokenEnhancer;

    /**
     * 定义授权和令牌端点以及令牌服务
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints)
    {
        endpoints
                .tokenGranter(new CompositeTokenGranter(getTokenGranters(endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory())))
                // 请求方式
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
                // 指定token存储位置
                .tokenStore(tokenStore())
                // 自定义生成令牌
                .tokenEnhancer(tokenEnhancer)
                // 用户账号密码认证
//                .userDetailsService(userDetailsService)
                // 指定认证管理器
                .authenticationManager(authenticationManager)
                // 是否重复使用 refresh_token
                .reuseRefreshTokens(false);
                // 自定义异常处理
//                .exceptionTranslator(new CustomWebResponseExceptionTranslator());
    }

    private List<TokenGranter> getTokenGranters(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        return new ArrayList<>(Arrays.asList(
                new PasswordCustomTokenGranter(customUserDetailsService,tokenServices, clientDetailsService, requestFactory)
        ));
    }
    /**
     * 配置令牌端点(Token Endpoint)的安全约束
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer)
    {
        oauthServer.allowFormAuthenticationForClients().checkTokenAccess("permitAll()");
    }

    /**
     * 声明 ClientDetails实现
     */
    public RedisClientDetailsService clientDetailsService()
    {
        RedisClientDetailsService clientDetailsService = new RedisClientDetailsService(dataSource);
        return clientDetailsService;
    }

    /**
     * 配置客户端详情
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception
    {
        clients.withClientDetails(clientDetailsService());
    }

    /**
     * 基于 Redis 实现，令牌保存到缓存
     */
    @Bean
    public TokenStore tokenStore()
    {
        RedisTokenStore tokenStore = new RedisTokenStore(redisConnectionFactory);
        tokenStore.setPrefix(CacheConstants.OAUTH_ACCESS);
        return tokenStore;
    }

    /**
     * 自定义生成令牌
     */
    @Bean
    public TokenEnhancer tokenEnhancer()
    {
        return new TokenEnhancer()
        {
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication)
            {
                if (accessToken instanceof DefaultOAuth2AccessToken)
                {
                    DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
                    LoginUser user = (LoginUser) authentication.getUserAuthentication().getPrincipal();
                    Map<String, Object> additionalInformation = new LinkedHashMap<String, Object>();
                    additionalInformation.put(SecurityConstants.DETAILS_USERNAME, authentication.getName());
                    additionalInformation.put(SecurityConstants.DETAILS_USER_ID, user.getUserId());
                    token.setAdditionalInformation(additionalInformation);
                }
                return accessToken;
            };
        };
    }
}
