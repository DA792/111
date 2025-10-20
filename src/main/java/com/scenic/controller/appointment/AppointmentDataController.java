package com.scenic.controller.appointment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scenic.common.dto.Result;
import com.scenic.dto.appointment.AppointmentDataDTO;
import com.scenic.service.appointment.AppointmentDataService;

/**
 * 预约数据控制器
 */
@RestController
@RequestMapping("/api")
public class AppointmentDataController {
    
    // 小程序端API接口前缀
    private static final String MINIAPP_PREFIX = "/uniapp";
    
    // 管理后台端API接口前缀
    private static final String ADMIN_PREFIX = "/manage";
    
    @Autowired
    private AppointmentDataService appointmentDataService;
    
    /**
     * 管理后台 - 获取入园预约数据（个人 + 团队预约聚合）
     * 
     * @param year 年份
     * @param month 月份
     * @return 入园预约数据列表
     */
    @GetMapping(ADMIN_PREFIX + "/appointment-data/enter")
    public Result<List<AppointmentDataDTO>> getEnterReservationData(
            @RequestParam(required = true) Integer year,
            @RequestParam(required = true) Integer month) {
        return appointmentDataService.getEnterReservationData(year, month);
    }
    
    /**
     * 管理后台 - 获取活动预约数据
     * 
     * @param year 年份
     * @param month 月份
     * @return 活动预约数据列表
     */
    @GetMapping(ADMIN_PREFIX + "/appointment-data/activity")
    public Result<List<AppointmentDataDTO>> getActivityReservationData(
            @RequestParam(required = true) Integer year,
            @RequestParam(required = true) Integer month) {
        return appointmentDataService.getActivityReservationData(year, month);
    }
    
    /**
     * 小程序端 - 获取入园预约数据（个人 + 团队预约聚合）
     * 
     * @param year 年份
     * @param month 月份
     * @return 入园预约数据列表
     */
    @GetMapping(MINIAPP_PREFIX + "/appointment-data/enter")
    public Result<List<AppointmentDataDTO>> getEnterReservationDataForMiniapp(
            @RequestParam(required = true) Integer year,
            @RequestParam(required = true) Integer month) {
        return appointmentDataService.getEnterReservationData(year, month);
    }
    
    /**
     * 小程序端 - 获取活动预约数据
     * 
     * @param year 年份
     * @param month 月份
     * @return 活动预约数据列表
     */
    @GetMapping(MINIAPP_PREFIX + "/appointment-data/activity")
    public Result<List<AppointmentDataDTO>> getActivityReservationDataForMiniapp(
            @RequestParam(required = true) Integer year,
            @RequestParam(required = true) Integer month) {
        return appointmentDataService.getActivityReservationData(year, month);
    }
}
