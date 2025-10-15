package com.scenic.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtUtilTest {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    private static final String ADMIN_TOKEN_KEY_PREFIX = "admin:token:";
    
    @Test
    public void testGenerateAndValidateAdminToken() {
        // 测试生成管理端Token
        String username = "test_admin";
        Long userId = 1L;
        
        String token = jwtUtil.generateAdminToken(username, userId);
        assertNotNull(token, "生成的Token不应为null");
        assertFalse(token.isEmpty(), "生成的Token不应为空");
        
        // 验证Token存储到Redis
        String redisKey = ADMIN_TOKEN_KEY_PREFIX + userId;
        String storedToken = redisTemplate.opsForValue().get(redisKey);
        assertNotNull(storedToken, "Redis中应存储Token");
        assertEquals(token, storedToken, "Redis中存储的Token应与生成的Token一致");
        
        // 验证Token有效性
        boolean isValid = jwtUtil.validateAdminToken(token);
        assertTrue(isValid, "生成的Token应为有效");
        
        // 测试Token失效
        jwtUtil.invalidateAdminToken(userId);
        String deletedToken = redisTemplate.opsForValue().get(redisKey);
        assertNull(deletedToken, "失效后Redis中不应存在Token");
        
        // 验证失效后的Token
        boolean isInvalid = jwtUtil.validateAdminToken(token);
        assertFalse(isInvalid, "失效后的Token应为无效");
    }
}
