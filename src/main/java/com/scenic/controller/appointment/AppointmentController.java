package com.scenic.controller.appointment;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.appointment.PersonalAppointmentDTO;
import com.scenic.entity.appointment.Appointment;
import com.scenic.entity.appointment.AppointmentSetting;
import com.scenic.service.appointment.AppointmentService;
import com.scenic.service.appointment.AppointmentSettingService;

/**
 * 预约控制器
 * 提供小程序端和管理后台端的API接口
 */
@RestController
@RequestMapping("/api")
public class AppointmentController {
    
    // 小程序端API接口前缀
    private static final String MINIAPP_PREFIX = "/uniapp";
    
    // 管理后台端API接口前缀
    private static final String ADMIN_PREFIX = "/manage";
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private AppointmentSettingService appointmentSettingService;
    
    /**
     * 小程序端 - 创建个人预约
     * @param appointmentDTO 预约信息
     * @return 预约结果
     */
    @PostMapping(MINIAPP_PREFIX + "/appointments/personal")
    public Result<String> createPersonalAppointment(@RequestBody PersonalAppointmentDTO appointmentDTO) {
        return appointmentService.createPersonalAppointment(appointmentDTO);
    }
    
    /**
     * 小程序端 - 查询我的预约列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 预约列表
     */
    @GetMapping(MINIAPP_PREFIX + "/users/{userId}/appointments")
    public Result<PageResult<Appointment>> getMyAppointments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return appointmentService.getUserAppointments(userId, page, size);
    }
    
    /**
     * 小程序端 - 获取预约详情
     * @param appointmentId 预约ID
     * @return 预约详情
     */
    @GetMapping(MINIAPP_PREFIX + "/appointments/{appointmentId}")
    public Result<Appointment> getAppointmentDetail(@PathVariable Long appointmentId) {
        return appointmentService.getAppointmentDetail(appointmentId);
    }
    
    /**
     * 小程序端 - 取消预约
     * @param appointmentId 预约ID
     * @return 取消结果
     */
    @PutMapping(MINIAPP_PREFIX + "/appointments/{appointmentId}/cancel")
    public Result<String> cancelAppointment(@PathVariable Long appointmentId) {
        return appointmentService.cancelAppointment(appointmentId);
    }
    
    /**
     * 小程序端 - 校验预约日期和人数
     * @param appointmentDate 预约日期
     * @param numberOfPeople 预约人数
     * @return 校验结果
     */
    @GetMapping(MINIAPP_PREFIX + "/appointments/validate")
    public Result<Map<String, Object>> validateAppointment(
            @RequestParam String appointmentDate,
            @RequestParam Integer numberOfPeople) {
        // 校验预约日期
        try {
            LocalDateTime date = LocalDateTime.parse(appointmentDate);
            // 检查是否是过去的日期
            if (date.isBefore(LocalDateTime.now())) {
                return Result.error("不能选择过去的日期");
            }
        } catch (Exception e) {
            return Result.error("日期格式不正确");
        }
        
        // 校验人数
        if (numberOfPeople == null || numberOfPeople <= 0) {
            return Result.error("预约人数必须大于0");
        }
        
        // 获取每日最大预约人数设置
        Result<Map<String, Object>> statsResult = appointmentSettingService.getDailyAppointmentStats(appointmentDate.substring(0, 10));
        if (statsResult.getCode() != 200) {
            return Result.error("获取预约统计信息失败");
        }
        
        Map<String, Object> stats = statsResult.getData();
        Boolean isFull = (Boolean) stats.get("isFull");
        if (isFull != null && isFull) {
            return Result.error("该日期预约已满");
        }
        
        // 获取每次预约最大人数设置
        Result<Map<String, AppointmentSetting>> settingsResult = appointmentSettingService.getSettingsByKeys(
            Arrays.asList("max_people_per_appointment"));
        if (settingsResult.getCode() != 200) {
            return Result.error("获取预约设置失败");
        }
        
        Map<String, AppointmentSetting> settings = settingsResult.getData();
        AppointmentSetting maxPeopleSetting = settings.get("max_people_per_appointment");
        if (maxPeopleSetting != null) {
            try {
                int maxPeople = Integer.parseInt(maxPeopleSetting.getSettingValue());
                if (numberOfPeople > maxPeople) {
                    return Result.error("预约人数不能超过" + maxPeople + "人");
                }
            } catch (NumberFormatException e) {
                // 如果设置值不是数字，使用默认值
                if (numberOfPeople > 10) {
                    return Result.error("预约人数不能超过10人");
                }
            }
        } else {
            // 如果没有设置，使用默认值
            if (numberOfPeople > 10) {
                return Result.error("预约人数不能超过10人");
            }
        }
        
        return Result.success("校验通过", stats);
    }
    
    /**
     * 管理后台端 - 查询预约列表
     * @param page 页码
     * @param size 每页大小
     * @param userName 用户姓名（可选）
     * @param scenicSpotName 景点名称（可选）
     * @param status 预约状态（可选）
     * @return 预约列表
     */
    @GetMapping(ADMIN_PREFIX + "/appointments")
    public Result<PageResult<Appointment>> getAppointments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String scenicSpotName,
            @RequestParam(required = false) String status) {
        return appointmentService.getAdminAppointments(page, size, userName, scenicSpotName, status);
    }
    
    /**
     * 管理后台端 - 获取预约详情
     * @param appointmentId 预约ID
     * @return 预约详情
     */
    @GetMapping(ADMIN_PREFIX + "/appointments/{appointmentId}")
    public Result<Appointment> getAppointmentDetailForAdmin(@PathVariable Long appointmentId) {
        return appointmentService.getAppointmentDetail(appointmentId);
    }
    
    /**
     * 管理后台端 - 审核预约
     * @param appointmentId 预约ID
     * @param status 审核状态
     * @return 审核结果
     */
    @PutMapping(ADMIN_PREFIX + "/appointments/{appointmentId}/review")
    public Result<String> reviewAppointment(
            @PathVariable Long appointmentId,
            @RequestParam String status) {
        return appointmentService.reviewAppointment(appointmentId, status);
    }
    
    /**
     * 管理后台端 - 删除预约
     * @param appointmentId 预约ID
     * @return 删除结果
     */
    @DeleteMapping(ADMIN_PREFIX + "/appointments/{appointmentId}")
    public Result<String> deleteAppointment(@PathVariable Long appointmentId) {
        return appointmentService.deleteAppointment(appointmentId);
    }
    
    /**
     * 管理后台端 - 获取指定日期的预约统计信息
     * @param date 日期
     * @return 预约统计信息
     */
    @GetMapping(ADMIN_PREFIX + "/appointments/daily-stats")
    public Result<Map<String, Object>> getDailyAppointmentStats(@RequestParam String date) {
        return appointmentSettingService.getDailyAppointmentStats(date);
    }
}
