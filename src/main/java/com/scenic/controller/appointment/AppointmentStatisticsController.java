package com.scenic.controller.appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scenic.common.dto.Result;
import com.scenic.service.appointment.AppointmentStatisticsService;

/**
 * 预约统计控制器
 * 提供管理后台端的API接口
 */
@RestController
@RequestMapping("/api/manage/appointment-statistics")
public class AppointmentStatisticsController {
    
    @Autowired
    private AppointmentStatisticsService appointmentStatisticsService;
    
    /**
     * 管理后台端 - 获取指定日期范围的预约统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预约统计信息
     */
    @GetMapping("/date-range")
    public Result<List<Map<String, Object>>> getAppointmentStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            return appointmentStatisticsService.getAppointmentStatistics(start, end);
        } catch (Exception e) {
            return Result.error("日期格式不正确");
        }
    }
    
    /**
     * 管理后台端 - 获取热门景点统计
     * @param limit 返回记录数限制
     * @return 热门景点统计信息
     */
    @GetMapping("/popular-scenic-spots")
    public Result<List<Map<String, Object>>> getPopularScenicSpots(
            @RequestParam(defaultValue = "10") int limit) {
        return appointmentStatisticsService.getPopularScenicSpots(limit);
    }
    
    /**
     * 管理后台端 - 获取用户预约统计
     * @param userId 用户ID
     * @return 用户预约统计信息
     */
    @GetMapping("/user/{userId}")
    public Result<Map<String, Object>> getUserAppointmentStatistics(@PathVariable Long userId) {
        return appointmentStatisticsService.getUserAppointmentStatistics(userId);
    }
    
    /**
     * 管理后台端 - 获取预约趋势统计
     * @param days 天数
     * @return 预约趋势统计信息
     */
    @GetMapping("/trends")
    public Result<List<Map<String, Object>>> getAppointmentTrends(
            @RequestParam(defaultValue = "7") int days) {
        return appointmentStatisticsService.getAppointmentTrends(days);
    }
}
