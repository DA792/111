package com.scenic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * JWT配置类
 */
@Configuration
public class JwtConfig {
    
    @Value("${miniapp.security.jwt.secret}")
    private String miniappSecret;
    
    @Value("${miniapp.security.jwt.expiration}")
    private long miniappExpiration;
    
    @Value("${admin.security.jwt.secret}")
    private String adminSecret;
    
    @Value("${admin.security.jwt.expiration}")
    private long adminExpiration;
    
    public String getMiniappSecret() {
        return miniappSecret;
    }
    
    public long getMiniappExpiration() {
        return miniappExpiration;
    }
    
    public String getAdminSecret() {
        return adminSecret;
    }
    
    public long getAdminExpiration() {
        return adminExpiration;
    }
}
