package com.scenic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序配置
 */
@Configuration
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WechatConfig {
    
    /**
     * 小程序AppID
     */
    private String appId;
    
    /**
     * 小程序AppSecret
     */
    private String appSecret;
    
    /**
     * 微信接口调用凭证获取URL
     */
    private String code2SessionUrl = "https://api.weixin.qq.com/sns/jscode2session";
    
    public String getAppId() {
        return appId;
    }
    
    public void setAppId(String appId) {
        this.appId = appId;
    }
    
    public String getAppSecret() {
        return appSecret;
    }
    
    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
    
    public String getCode2SessionUrl() {
        return code2SessionUrl;
    }
    
    public void setCode2SessionUrl(String code2SessionUrl) {
        this.code2SessionUrl = code2SessionUrl;
    }
}
