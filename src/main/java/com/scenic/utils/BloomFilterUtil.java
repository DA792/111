package com.scenic.utils;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Bloom Filter工具类
 * 用于快速判断用户是否可能对某些照片有过互动（收藏/点赞）
 */
@Component
public class BloomFilterUtil {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // Bloom Filter预期插入数量
    private static final int EXPECTED_INSERTIONS = 100000;
    
    // 误判率
    private static final double FALSE_PROBABILITY = 0.01;
    
    // 互动记录缓存时间（小时）
    private static final int INTERACTION_CACHE_HOURS = 24;
    
    // 本地缓存的BloomFilter，避免序列化问题
    private final ThreadLocal<BloomFilter<String>> localBloomFilter = ThreadLocal.withInitial(() -> 
        BloomFilter.create(
            Funnels.stringFunnel(StandardCharsets.UTF_8),
            EXPECTED_INSERTIONS,
            FALSE_PROBABILITY
        )
    );
    
    /**
     * 获取用户互动Bloom Filter
     * @param userId 用户ID
     * @return BloomFilter
     */
    public BloomFilter<String> getUserInteractionsBloomFilter(Long userId) {
        String interactionsKey = "user_interactions:" + userId;
        
        // 获取本地BloomFilter
        BloomFilter<String> bloomFilter = localBloomFilter.get();
        
        // 从Redis获取用户互动记录集合
        Set<String> interactions = (Set<String>) redisTemplate.opsForValue().get(interactionsKey);
        
        if (interactions != null) {
            // 将互动记录添加到BloomFilter
            for (String interaction : interactions) {
                bloomFilter.put(interaction);
            }
        }
        
        return bloomFilter;
    }
    
    /**
     * 向用户的Bloom Filter中添加互动记录
     * @param userId 用户ID
     * @param photoCheckInId 照片打卡ID
     */
    public void addUserInteractionToBloomFilter(Long userId, Long photoCheckInId) {
        String interactionsKey = "user_interactions:" + userId;
        String interactionKey = userId + ":" + photoCheckInId;
        
        // 获取本地BloomFilter并添加记录
        BloomFilter<String> bloomFilter = localBloomFilter.get();
        bloomFilter.put(interactionKey);
        
        // 从Redis获取用户互动记录集合
        Set<String> interactions = (Set<String>) redisTemplate.opsForValue().get(interactionsKey);
        
        if (interactions == null) {
            interactions = new HashSet<>();
        }
        
        // 添加新的互动记录
        interactions.add(interactionKey);
        
        // 更新Redis中的互动记录集合
        try {
            redisTemplate.opsForValue().set(
                interactionsKey, 
                interactions, 
                INTERACTION_CACHE_HOURS, 
                TimeUnit.HOURS
            );
        } catch (Exception e) {
            System.err.println("缓存用户互动记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查用户是否可能与照片有过互动
     * @param userId 用户ID
     * @param photoCheckInId 照片打卡ID
     * @return 可能有互动返回true，肯定没有互动返回false
     */
    public boolean mightContainUserInteraction(Long userId, Long photoCheckInId) {
        try {
            String interactionKey = userId + ":" + photoCheckInId;
            BloomFilter<String> bloomFilter = getUserInteractionsBloomFilter(userId);
            return bloomFilter.mightContain(interactionKey);
        } catch (Exception e) {
            System.err.println("检查用户互动状态失败: " + e.getMessage());
            // 出错时返回true，让系统继续查询数据库
            return true;
        }
    }
}