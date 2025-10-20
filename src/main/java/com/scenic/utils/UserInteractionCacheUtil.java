package com.scenic.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户互动状态缓存工具类
 * 管理用户对照片的收藏/点赞状态缓存
 */
@Component
public class UserInteractionCacheUtil {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // 缓存过期时间（分钟）
    private static final int CACHE_EXPIRE_MINUTES = 30;
    
    /**
     * 缓存用户对照片的互动状态
     * @param userId 用户ID
     * @param photoCheckInId 照片打卡ID
     * @param isCollected 是否收藏
     * @param isLiked 是否点赞
     */
    public void cacheUserInteractionStatus(Long userId, Long photoCheckInId, Boolean isCollected, Boolean isLiked) {
        String cacheKey = buildCacheKey(userId, photoCheckInId);
        
        Map<String, Boolean> status = new HashMap<>();
        status.put("collected", isCollected != null ? isCollected : false);
        status.put("liked", isLiked != null ? isLiked : false);
        
        redisTemplate.opsForValue().set(cacheKey, status, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }
    
    /**
     * 批量缓存用户对多个照片的互动状态
     * @param userId 用户ID
     * @param interactionStatus 互动状态映射 {photoCheckInId: {collected: true/false, liked: true/false}}
     */
    public void batchCacheUserInteractionStatus(Long userId, Map<Long, Map<String, Boolean>> interactionStatus) {
        interactionStatus.forEach((photoCheckInId, status) -> {
            String cacheKey = buildCacheKey(userId, photoCheckInId);
            redisTemplate.opsForValue().set(cacheKey, status, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        });
    }
    
    /**
     * 获取用户对照片的互动状态
     * @param userId 用户ID
     * @param photoCheckInId 照片打卡ID
     * @return 互动状态 {collected: true/false, liked: true/false}
     */
    public Map<String, Boolean> getUserInteractionStatus(Long userId, Long photoCheckInId) {
        String cacheKey = buildCacheKey(userId, photoCheckInId);
        return (Map<String, Boolean>) redisTemplate.opsForValue().get(cacheKey);
    }
    
    /**
     * 批量获取用户对多个照片的互动状态
     * @param userId 用户ID
     * @param photoCheckInIds 照片打卡ID列表
     * @return 互动状态映射 {photoCheckInId: {collected: true/false, liked: true/false}}
     */
    public Map<Long, Map<String, Boolean>> batchGetUserInteractionStatus(Long userId, List<Long> photoCheckInIds) {
        Map<Long, Map<String, Boolean>> result = new HashMap<>();
        
        for (Long photoCheckInId : photoCheckInIds) {
            String cacheKey = buildCacheKey(userId, photoCheckInId);
            Map<String, Boolean> status = (Map<String, Boolean>) redisTemplate.opsForValue().get(cacheKey);
            if (status != null) {
                result.put(photoCheckInId, status);
            }
        }
        
        return result;
    }
    
    /**
     * 构建缓存键
     * @param userId 用户ID
     * @param photoCheckInId 照片打卡ID
     * @return 缓存键
     */
    private String buildCacheKey(Long userId, Long photoCheckInId) {
        return "user_interactions:" + userId + ":" + photoCheckInId;
    }
    
    /**
     * 清除用户对照片的互动状态缓存
     * @param userId 用户ID
     * @param photoCheckInId 照片打卡ID
     */
    public void clearUserInteractionCache(Long userId, Long photoCheckInId) {
        String cacheKey = buildCacheKey(userId, photoCheckInId);
        redisTemplate.delete(cacheKey);
    }
    
    /**
     * 批量清除用户对多个照片的互动状态缓存
     * @param userId 用户ID
     * @param photoCheckInIds 照片打卡ID列表
     */
    public void batchClearUserInteractionCache(Long userId, List<Long> photoCheckInIds) {
        photoCheckInIds.forEach(photoCheckInId -> clearUserInteractionCache(userId, photoCheckInId));
    }
}