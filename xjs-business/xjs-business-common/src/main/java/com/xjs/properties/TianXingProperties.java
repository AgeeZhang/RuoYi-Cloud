package com.xjs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 天行数据配置
 * @author xiejs
 * @since  2021-12-27
 */
@Component
@ConfigurationProperties(prefix = "tianxing.open")
@Data
public class TianXingProperties {

    /**
     * key密钥
     */
    private String key;
}