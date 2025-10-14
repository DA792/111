package com.scenic.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码工具类
 */
@Component
public class PasswordUtil {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 对密码进行加密
     * @param rawPassword 明文密码
     * @return 加密后的密码
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    /**
     * 验证明文密码与加密密码是否匹配
     * @param rawPassword 明文密码
     * @param encodedPassword 加密密码
     * @return 是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}