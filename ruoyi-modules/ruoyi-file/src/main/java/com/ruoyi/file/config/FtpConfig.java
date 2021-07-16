package com.ruoyi.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * ftp config
 * vsftpd 服务器搭建、ftp客户端filezilla使、ceph分布式文件系统
 * https://blog.csdn.net/ab601026460/article/details/105928311
 * @author dazer
 */
@RefreshScope
@Configuration
@ConfigurationProperties(
        prefix = "ftp"
)
public class FtpConfig {
    /**
     * ftp访问地址
     * eg1: www.ourslook.com
     * eg2: 192.168.0.1
     */
    private String hostName;
    /**
     * ftp端口，默认21
     */
    private Integer port = 21;
    /**
     * ftp访问密码
     * 有可能是匿名，就不需要账号和密码
     * eg: ftpuser
     */
    private String userName;
    /**
     * ftp账号密码
     * eg: ftpx123.pwd..1
     */
    private String password;
    /**
     * filezila server: 就是空
     * vsftpd：使用的 系统用户的目录，这里往往都是不是根目录，如：/home/ftpuser/
     */
    private String rootFtpPath = "";

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRootFtpPath() {
        return rootFtpPath;
    }

    public void setRootFtpPath(String rootFtpPath) {
        this.rootFtpPath = rootFtpPath;
    }
}