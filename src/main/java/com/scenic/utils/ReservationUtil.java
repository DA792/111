package com.scenic.utils;

import com.scenic.entity.appointment.IndividualReservation;
import com.scenic.exception.reservation.DuplicateReservationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 预约工具类
 */
@Component
public class ReservationUtil {
    
    private static final Logger log = LoggerFactory.getLogger(ReservationUtil.class);
    
    @Autowired
    private RedisUtil redisUtil;
    
    private static final String RESERVATION_LOCK_PREFIX = "reservation:lock:";
    private static final long LOCK_TIMEOUT = 30L; // 锁超时时间30秒
    
    /**
     * 生成预约防重Redis键
     * @param reservation 预约信息
     * @return Redis键
     */
    public String generateReservationRedisKey(IndividualReservation reservation) {
        if (reservation == null || reservation.getVisitDate() == null) {
            return null;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(reservation.getVisitDate());
        
        // 生成更具体的键，包含用户ID以避免不同用户之间的冲突
        return String.format("%s%s:%s:%s:%s:%s", 
            RESERVATION_LOCK_PREFIX,
            dateStr,
            reservation.getTimeSlot(),
            reservation.getContactIdType(),
            reservation.getContactIdNumber(),
            reservation.getUserId() == null ? "unknown" : reservation.getUserId());
    }
    
    /**
     * 生成预约防重Redis键（重载方法）
     * @param visitDate 入区日期
     * @param timeSlot 时间段
     * @param idType 证件类型
     * @param idNumber 证件号码
     * @param userId 用户ID（可选）
     * @return Redis键
     */
    public String generateReservationRedisKey(Date visitDate, Integer timeSlot, Integer idType, String idNumber, Long userId) {
        if (visitDate == null || timeSlot == null || idType == null || idNumber == null) {
            return null;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(visitDate);
        
        return String.format("%s%s:%s:%s:%s:%s", 
            RESERVATION_LOCK_PREFIX,
            dateStr,
            timeSlot,
            idType,
            idNumber,
            userId == null ? "unknown" : userId);
    }
    
    /**
     * 生成预约防重Redis键（重载方法，向后兼容）
     * @param visitDate 入区日期
     * @param timeSlot 时间段
     * @param idType 证件类型
     * @param idNumber 证件号码
     * @return Redis键
     */
    public String generateReservationRedisKey(Date visitDate, Integer timeSlot, Integer idType, String idNumber) {
        return generateReservationRedisKey(visitDate, timeSlot, idType, idNumber, null);
    }
    
    /**
     * 检查是否重复预约（使用Redis防重）
     * @param reservation 预约信息
     * @throws DuplicateReservationException 重复预约异常
     */
    public void checkDuplicateReservation(IndividualReservation reservation) throws DuplicateReservationException {
        String redisKey = generateReservationRedisKey(reservation);
        if (redisKey == null) {
            log.warn("生成Redis键失败，预约信息不完整");
            return;
        }
        
        // 先尝试Redis检查
        if (!redisUtil.isRedisAvailable()) {
            log.warn("Redis不可用，跳过Redis防重检查");
            return;
        }
        
        // Redis可用时的检查逻辑
        boolean locked = redisUtil.setIfAbsent(redisKey, "1", LOCK_TIMEOUT);
        if (!locked) {
            log.warn("检测到重复预约，Redis键已存在: {}", redisKey);
            throw new DuplicateReservationException("操作过于频繁，请稍后再试");
        }
        
        log.debug("Redis加锁成功，key: {}", redisKey);
    }
    
    /**
     * 释放预约锁
     * @param reservation 预约信息
     */
    public void releaseReservationLock(IndividualReservation reservation) {
        String redisKey = generateReservationRedisKey(reservation);
        if (redisKey != null) {
            redisUtil.delete(redisKey);
            log.debug("释放预约锁，key: {}", redisKey);
        }
    }
    
    /**
     * 生成预约编号
     * @return 预约编号
     */
    public String generateReservationNo() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String random = String.format("%04d", (int)(Math.random() * 10000));
        return "RES" + timestamp + random;
    }
}