package com.scenic.schedule;

import com.scenic.common.constant.AppointmentConstants;
import com.scenic.entity.appointment.IndividualReservation;
import com.scenic.mapper.appointment.IndividualReservationMapper;
import com.scenic.service.appointment.IndividualReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 个人预约过期检查定时任务
 * 检查并更新过期的个人预约状态
 */
@Component
public class IndividualReservationExpireSchedule {
    
    private static final Logger log = LoggerFactory.getLogger(IndividualReservationExpireSchedule.class);
    
    @Autowired
    private IndividualReservationMapper individualReservationMapper;
    
    /**
     * 每小时检查一次过期的个人预约
     * 每小时的第5分钟执行
     */
    @Scheduled(cron = "0 5 * * * ?")
    public void checkExpiredReservations() {
        try {
            log.info("开始执行个人预约过期检查任务");
            
            // 查询所有未开始且未取消的预约（status = 0）
            List<IndividualReservation> reservations = individualReservationMapper.selectByStatus(0, 0, 1000);
            
            int expiredCount = 0;
            LocalDateTime now = LocalDateTime.now();
            
            for (IndividualReservation reservation : reservations) {
                // 检查预约是否已过期
                if (isReservationExpired(reservation, now)) {
                    // 更新预约状态为已过期（11）
                    reservation.setStatus(11);
                    reservation.setUpdateTime(now);
                    reservation.setVersion(reservation.getVersion() + 1);
                    
                    int result = individualReservationMapper.updateById(reservation);
                    if (result > 0) {
                        expiredCount++;
                        log.info("预约ID {} 已过期，状态已更新", reservation.getId());
                    }
                }
            }
            
            log.info("个人预约过期检查任务执行完成，共处理 {} 个过期预约", expiredCount);
        } catch (Exception e) {
            log.error("个人预约过期检查任务执行异常", e);
        }
    }
    
    /**
     * 判断预约是否已过期
     * @param reservation 预约信息
     * @param now 当前时间
     * @return 是否过期
     */
    private boolean isReservationExpired(IndividualReservation reservation, LocalDateTime now) {
        if (reservation.getVisitDate() == null || reservation.getTimeSlot() == null) {
            return false;
        }
        
        // 将java.util.Date转换为LocalDate
        LocalDate visitDate = new java.sql.Date(reservation.getVisitDate().getTime()).toLocalDate();
        LocalDate currentDate = now.toLocalDate();
        
        // 如果访问日期是未来的日期，则不处理
        if (visitDate.isAfter(currentDate)) {
            return false;
        }
        
        // 如果访问日期是过去的日期，则直接过期
        if (visitDate.isBefore(currentDate)) {
            return true;
        }
        
        // 如果是今天，检查时间段是否过期
        LocalTime currentTime = now.toLocalTime();
        
        // 根据时间段判断过期时间
        // 1-上午时段：12点后过期
        // 2-下午时段：16点后过期
        if (reservation.getTimeSlot() == 1) {
            // 上午时段：12点后过期
            return currentTime.isAfter(LocalTime.of(12, 0));
        } else if (reservation.getTimeSlot() == 2) {
            // 下午时段：16点后过期
            return currentTime.isAfter(LocalTime.of(16, 0));
        }
        
        // 其他时间段默认不过期
        return false;
    }
}