package com.scenic.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis会话工具类
 * 专门处理用户会话相关的Redis操作
 */
@Component
public class RedisSessionUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(RedisSessionUtil.class);
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // Redis中存储微信会话信息的key前缀
    private static final String WECHAT_SESSION_KEY_PREFIX = "wechat:session:";
    private static final String WECHAT_TOKEN_KEY_PREFIX = "wechat:token:";
    
    // Redis操作超时时间（毫秒）
    private static final long REDIS_OPERATION_TIMEOUT = 5000;
    
    /**
     * 从Redis中获取用户会话信息
     *
     * @param openid 微信openid
     * @return 会话信息Map，不存在则返回null
     */
    public Map<String, Object> getUserSessionFromRedis(String openid) {
        try {
            String sessionKey = WECHAT_SESSION_KEY_PREFIX + openid;
            Object sessionObj = redisTemplate.opsForValue().get(sessionKey);
            
            if (sessionObj != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> sessionInfo = (Map<String, Object>) sessionObj;
                return sessionInfo;
            }
            
            return null;
        } catch (Exception e) {
            logger.error("从Redis获取用户会话信息失败，openid: {}", openid, e);
            return null;
        }
    }
    
    /**
     * 清除Redis中的用户会话信息
     *
     * @param userId 用户ID
     * @param openid 微信openid
     */
    public void clearUserSessionFromRedis(Long userId, String openid) {
        try {
            // 获取旧的会话信息，以便清除旧的token
            Map<String, Object> oldSessionInfo = getUserSessionFromRedis(openid);
            if (oldSessionInfo != null && oldSessionInfo.containsKey("token")) {
                String oldToken = (String) oldSessionInfo.get("token");
                String oldTokenKey = WECHAT_TOKEN_KEY_PREFIX + oldToken;
                redisTemplate.delete(oldTokenKey);
            }
            
            // 清除会话信息
            String sessionKey1 = WECHAT_SESSION_KEY_PREFIX + userId;
            String sessionKey2 = WECHAT_SESSION_KEY_PREFIX + openid;
            redisTemplate.delete(sessionKey1);
            redisTemplate.delete(sessionKey2);
            
            logger.info("用户 {} 的微信会话信息已从Redis中清除", userId);
        } catch (Exception e) {
            logger.error("清除Redis中的用户会话信息失败，用户ID: {}, openid: {}", userId, openid, e);
        }
    }
    
    /**
     * 将微信会话信息存储到Redis中
     *
     * @param userId 用户ID
     * @param openid 微信openid
     * @param sessionKey 微信会话密钥
     * @param token JWT令牌
     * @return 是否存储成功
     */
    public boolean saveSessionToRedis(Long userId, String openid, String sessionKey, String token) {
        try {
            // 先清除旧的会话信息
            clearUserSessionFromRedis(userId, openid);
            
            // 存储会话信息
            String sessionKey1 = WECHAT_SESSION_KEY_PREFIX + userId;
            String sessionKey2 = WECHAT_SESSION_KEY_PREFIX + openid;
            
            // 创建会话信息Map
            Map<String, Object> sessionInfo = new HashMap<>();
            sessionInfo.put("userId", userId);
            sessionInfo.put("openid", openid);
            sessionInfo.put("sessionKey", sessionKey);
            sessionInfo.put("token", token);
            sessionInfo.put("loginTime", System.currentTimeMillis());
            
            // 存储会话信息，有效期24小时
            redisTemplate.opsForValue().set(sessionKey1, sessionInfo, 24, TimeUnit.HOURS);
            redisTemplate.opsForValue().set(sessionKey2, sessionInfo, 24, TimeUnit.HOURS);
            
            // 存储token，有效期24小时
            String tokenKey = WECHAT_TOKEN_KEY_PREFIX + token;
            redisTemplate.opsForValue().set(tokenKey, userId, 24, TimeUnit.HOURS);
            
            logger.info("用户 {} 的微信会话信息已存储到Redis", userId);
            return true;
        } catch (Exception e) {
            logger.error("存储微信会话信息到Redis失败，用户ID: {}, openid: {}", userId, openid, e);
            return false;
        }
    }
    
    /**
     * 检查Redis连接是否正常
     *
     * @return Redis是否可用
     */
    public boolean isRedisAvailable() {
        try {
            String testKey = "redis_health_check";
            redisTemplate.opsForValue().set(testKey, "test", 1, TimeUnit.SECONDS);
            redisTemplate.delete(testKey);
            return true;
        } catch (Exception e) {
            logger.error("Redis连接检查失败", e);
            return false;
        }
    }
}