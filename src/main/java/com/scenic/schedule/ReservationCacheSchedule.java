package com.scenic.schedule;

import com.scenic.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 预约缓存预热定时任务
 */
@Component
public class ReservationCacheSchedule {
    
    private static final Logger log = LoggerFactory.getLogger(ReservationCacheSchedule.class);
    
    @Autowired
    private RedisUtil redisUtil;
    
    /**
     * 每天6点执行缓存预热
     */
    @Scheduled(cron = "0 0 6 * * ?")
    public void preloadHotReservations() {
        try {
            log.info("开始执行预约缓存预热任务");
            
            // 加载当天热门时段的预约数据到Redis
            LocalDate today = LocalDate.now();
            String dateStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            // 预热当天的热门时段（这里可以根据实际业务需求调整）
            for (int timeSlot = 1; timeSlot <= 4; timeSlot++) {
                String key = String.format("reservation:hot:%s:%s", dateStr, timeSlot);
                // 设置默认值，表示该时段可预约
                redisUtil.set(key, "0", 24 * 60 * 60L); // 24小时过期
            }
            
            log.info("预约缓存预热任务执行完成");
        } catch (Exception e) {
            log.error("预约缓存预热任务执行异常", e);
        }
    }
    
    /**
     * 每小时清理过期的预约锁
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanExpiredReservationLocks() {
        try {
            log.info("开始执行过期预约锁清理任务");
            // 这里可以添加具体的清理逻辑
            // 由于Redis的过期机制会自动清理，这里主要是记录日志
            log.info("过期预约锁清理任务执行完成");
        } catch (Exception e) {
            log.error("过期预约锁清理任务执行异常", e);
        }
    }
}