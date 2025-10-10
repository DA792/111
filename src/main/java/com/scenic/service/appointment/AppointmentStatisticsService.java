package com.scenic.service.appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.scenic.common.dto.Result;

/**
 * 预约统计服务接口
 */
public interface AppointmentStatisticsService {
    
    /**
     * 获取指定日期范围的预约统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预约统计信息
     */
    Result<List<Map<String, Object>>> getAppointmentStatistics(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取热门景点统计
     * @param limit 返回记录数限制
     * @return 热门景点统计信息
     */
    Result<List<Map<String, Object>>> getPopularScenicSpots(int limit);
    
    /**
     * 获取用户预约统计
     * @param userId 用户ID
     * @return 用户预约统计信息
     */
    Result<Map<String, Object>> getUserAppointmentStatistics(Long userId);
    
    /**
     * 获取预约趋势统计
     * @param days 天数
     * @return 预约趋势统计信息
     */
    Result<List<Map<String, Object>>> getAppointmentTrends(int days);
}
