package com.scenic.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 */
@Component
public class RedisUtil {
    
    private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private volatile boolean redisAvailable = true;
    
    /**
     * 设置缓存
     * @param key 键
     * @param value 值
     * @param timeout 时间(秒)
     */
    public boolean set(String key, Object value, long timeout) {
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value, timeout, TimeUnit.SECONDS);
            redisAvailable = true;
            return true;
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis连接失败，设置缓存失败: {}", key);
            redisAvailable = false;
            return false;
        } catch (Exception e) {
            log.error("设置缓存异常: {}", key, e);
            return false;
        }
    }
    
    /**
     * 设置缓存（无过期时间）
     * @param key 键
     * @param value 值
     */
    public boolean set(String key, Object value) {
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisAvailable = true;
            return true;
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis连接失败，设置缓存失败: {}", key);
            redisAvailable = false;
            return false;
        } catch (Exception e) {
            log.error("设置缓存异常: {}", key, e);
            return false;
        }
    }
    
    /**
     * 获取缓存
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            Object value = operations.get(key);
            redisAvailable = true;
            return value;
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis连接失败，获取缓存失败: {}", key);
            redisAvailable = false;
            return null;
        } catch (Exception e) {
            log.error("获取缓存异常: {}", key, e);
            return null;
        }
    }
    
    /**
     * 删除缓存
     * @param key 键
     * @return 是否删除成功
     */
    public boolean delete(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            redisAvailable = true;
            return result != null && result;
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis连接失败，删除缓存失败: {}", key);
            redisAvailable = false;
            return false;
        } catch (Exception e) {
            log.error("删除缓存异常: {}", key, e);
            return false;
        }
    }
    
    /**
     * 判断key是否存在
     * @param key 键
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        try {
            Boolean result = redisTemplate.hasKey(key);
            redisAvailable = true;
            return result != null && result;
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis连接失败，检查key失败: {}", key);
            redisAvailable = false;
            return false;
        } catch (Exception e) {
            log.error("检查key异常: {}", key, e);
            return false;
        }
    }
    
    /**
     * 设置缓存并设置过期时间（原子操作）
     * @param key 键
     * @param value 值
     * @param timeout 时间(秒)
     * @return 是否设置成功
     */
    public boolean setIfAbsent(String key, Object value, long timeout) {
        try {
            // 使用RedisTemplate的setIfAbsent方法直接设置键值和过期时间
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.SECONDS);
            redisAvailable = true;
            return result != null && result;
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis连接失败，设置缓存失败: {}", key);
            redisAvailable = false;
            return false;
        } catch (Exception e) {
            log.error("设置缓存异常: {}", key, e);
            return false;
        }
    }
    
    /**
     * Redis是否可用
     * @return 是否可用
     */
    public boolean isRedisAvailable() {
        return redisAvailable;
    }
    
    /**
     * 设置Redis可用状态
     * @param available 是否可用
     */
    public void setRedisAvailable(boolean available) {
        this.redisAvailable = available;
    }
}
