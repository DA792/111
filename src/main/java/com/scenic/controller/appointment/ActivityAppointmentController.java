package com.scenic.controller.appointment;

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
import com.scenic.dto.appointment.ActivityAppointmentDTO;
import com.scenic.entity.appointment.ActivityAppointment;
import com.scenic.service.appointment.AppointmentService;

/**
 * 活动预约控制器
 * 提供小程序端和管理后台端的API接口
 */
@RestController
@RequestMapping("/api")
public class ActivityAppointmentController {
    
    // 小程序端API接口前缀
    private static final String MINIAPP_PREFIX = "/uniapp";
    
    // 管理后台端API接口前缀
    private static final String ADMIN_PREFIX = "/manage";
    
    @Autowired
    private AppointmentService appointmentService;
    
    /**
     * 小程序端 - 创建活动预约
     * @param appointmentDTO 活动预约信息
     * @return 预约结果
     */
    @PostMapping(MINIAPP_PREFIX + "/activity-appointments")
    public Result<String> createActivityAppointment(@RequestBody ActivityAppointmentDTO appointmentDTO) {
        return appointmentService.createActivityAppointment(appointmentDTO);
    }
    
    /**
     * 小程序端 - 获取用户活动预约列表
     * @param page 页码
     * @param size 每页大小
     * @return 活动预约列表
     */
    @GetMapping(MINIAPP_PREFIX + "/activity-appointments")
    public Result<PageResult<ActivityAppointment>> getActivityAppointmentsForMiniapp(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 这里应该根据用户ID查询，简化处理
        return appointmentService.getActivityAppointments(page, size);
    }
    
    /**
     * 小程序端 - 获取活动预约详情
     * @param id 活动预约ID
     * @return 活动预约详情
     */
    @GetMapping(MINIAPP_PREFIX + "/activity-appointments/{id}")
    public Result<ActivityAppointment> getActivityAppointmentDetailForMiniapp(@PathVariable Long id) {
        return appointmentService.getActivityAppointmentDetail(id);
    }
    
    /**
     * 小程序端 - 取消活动预约
     * @param id 活动预约ID
     * @return 取消结果
     */
    @PutMapping(MINIAPP_PREFIX + "/activity-appointments/{id}/cancel")
    public Result<String> cancelActivityAppointmentForMiniapp(@PathVariable Long id) {
        return appointmentService.cancelActivityAppointment(id);
    }
    
    /**
     * 管理后台端 - 获取活动预约列表
     * @param page 页码
     * @param size 每页大小
     * @param activityName 活动名称（可选）
     * @param contactPerson 联系人（可选）
     * @param status 预约状态（可选）
     * @return 活动预约列表
     */
    @GetMapping(ADMIN_PREFIX + "/activity-appointments")
    public Result<PageResult<ActivityAppointment>> getActivityAppointmentsForAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String activityName,
            @RequestParam(required = false) String contactPerson,
            @RequestParam(required = false) String status) {
        return appointmentService.getAdminActivityAppointments(page, size, activityName, contactPerson, status);
    }
    
    /**
     * 管理后台端 - 获取活动预约详情
     * @param id 活动预约ID
     * @return 活动预约详情
     */
    @GetMapping(ADMIN_PREFIX + "/activity-appointments/{id}")
    public Result<ActivityAppointment> getActivityAppointmentDetailForAdmin(@PathVariable Long id) {
        return appointmentService.getActivityAppointmentDetail(id);
    }
    
    /**
     * 管理后台端 - 审核活动预约
     * @param id 活动预约ID
     * @param status 审核状态
     * @return 审核结果
     */
    @PutMapping(ADMIN_PREFIX + "/activity-appointments/{id}/review")
    public Result<String> reviewActivityAppointmentForAdmin(@PathVariable Long id, @RequestParam String status) {
        return appointmentService.reviewActivityAppointment(id, status);
    }
    
    /**
     * 管理后台端 - 删除活动预约
     * @param id 活动预约ID
     * @return 删除结果
     */
    @DeleteMapping(ADMIN_PREFIX + "/activity-appointments/{id}")
    public Result<String> deleteActivityAppointmentForAdmin(@PathVariable Long id) {
        return appointmentService.deleteActivityAppointment(id);
    }
    
    /**
     * 管理后台端 - 导出活动预约数据
     * @param id 活动预约ID
     * @return 导出结果
     */
    @PostMapping(ADMIN_PREFIX + "/activity-appointments/{id}/export")
    public Result<String> exportActivityAppointmentForAdmin(@PathVariable Long id) {
        return appointmentService.exportActivityAppointment(id);
    }
}
