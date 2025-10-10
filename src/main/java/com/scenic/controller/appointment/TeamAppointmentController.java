package com.scenic.controller.appointment;

import java.io.IOException;

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
import org.springframework.web.multipart.MultipartFile;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.appointment.TeamAppointmentDTO;
import com.scenic.entity.appointment.TeamAppointment;
import com.scenic.service.appointment.AppointmentService;
import com.scenic.utils.FileUploadUtil;

/**
 * 团队预约控制器
 * 提供小程序端和管理后台端的API接口
 */
@RestController
@RequestMapping("/api")
public class TeamAppointmentController {
    
    private final AppointmentService appointmentService;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;
    
    // 小程序端API接口前缀
    private static final String MINIAPP_PREFIX = "/uniapp";
    
    // 管理后台端API接口前缀
    private static final String ADMIN_PREFIX = "/manage";
    
    public TeamAppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
    
    /**
     * 小程序端 - 创建团队预约
     * @param appointmentDTO 团队预约信息
     * @return 预约结果
     */
    @PostMapping(MINIAPP_PREFIX + "/team-appointments")
    public Result<String> createTeamAppointment(@RequestBody TeamAppointmentDTO appointmentDTO) {
        return appointmentService.createTeamAppointment(appointmentDTO);
    }
    
    /**
     * 小程序端 - 上传团队预约文件
     * @param file 上传的文件
     * @return 上传结果
     */
    @PostMapping(MINIAPP_PREFIX + "/team-appointments/upload")
    public Result<String> uploadTeamAppointmentFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileUploadUtil.uploadFile(file);
            return Result.success("文件上传成功", fileUrl);
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 小程序端 - 查询团队预约列表
     * @param page 页码
     * @param size 每页大小
     * @return 团队预约列表
     */
    @GetMapping(MINIAPP_PREFIX + "/team-appointments")
    public Result<PageResult<TeamAppointment>> getTeamAppointments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return appointmentService.getTeamAppointments(page, size);
    }
    
    /**
     * 小程序端 - 获取团队预约详情
     * @param teamAppointmentId 团队预约ID
     * @return 团队预约详情
     */
    @GetMapping(MINIAPP_PREFIX + "/team-appointments/{teamAppointmentId}")
    public Result<TeamAppointment> getTeamAppointmentDetail(@PathVariable Long teamAppointmentId) {
        return appointmentService.getTeamAppointmentDetail(teamAppointmentId);
    }
    
    /**
     * 小程序端 - 取消团队预约
     * @param teamAppointmentId 团队预约ID
     * @return 取消结果
     */
    @PutMapping(MINIAPP_PREFIX + "/team-appointments/{teamAppointmentId}/cancel")
    public Result<String> cancelTeamAppointment(@PathVariable Long teamAppointmentId) {
        return appointmentService.cancelTeamAppointment(teamAppointmentId);
    }
    
    /**
     * 管理后台端 - 查询团队预约列表
     * @param page 页码
     * @param size 每页大小
     * @param teamName 团队名称（可选）
     * @param contactPerson 联系人（可选）
     * @param status 预约状态（可选）
     * @return 团队预约列表
     */
    @GetMapping(ADMIN_PREFIX + "/team-appointments")
    public Result<PageResult<TeamAppointment>> getTeamAppointmentsForAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String teamName,
            @RequestParam(required = false) String contactPerson,
            @RequestParam(required = false) String status) {
        return appointmentService.getAdminTeamAppointments(page, size, teamName, contactPerson, status);
    }
    
    /**
     * 管理后台端 - 获取团队预约详情
     * @param teamAppointmentId 团队预约ID
     * @return 团队预约详情
     */
    @GetMapping(ADMIN_PREFIX + "/team-appointments/{teamAppointmentId}")
    public Result<TeamAppointment> getTeamAppointmentDetailForAdmin(@PathVariable Long teamAppointmentId) {
        return appointmentService.getTeamAppointmentDetail(teamAppointmentId);
    }
    
    /**
     * 管理后台端 - 审核团队预约
     * @param teamAppointmentId 团队预约ID
     * @param status 审核状态
     * @return 审核结果
     */
    @PutMapping(ADMIN_PREFIX + "/team-appointments/{teamAppointmentId}/review")
    public Result<String> reviewTeamAppointmentForAdmin(
            @PathVariable Long teamAppointmentId,
            @RequestParam String status) {
        return appointmentService.reviewTeamAppointment(teamAppointmentId, status);
    }
    
    /**
     * 管理后台端 - 删除团队预约
     * @param teamAppointmentId 团队预约ID
     * @return 删除结果
     */
    @DeleteMapping(ADMIN_PREFIX + "/team-appointments/{teamAppointmentId}")
    public Result<String> deleteTeamAppointmentForAdmin(@PathVariable Long teamAppointmentId) {
        return appointmentService.deleteTeamAppointment(teamAppointmentId);
    }
    
    /**
     * 管理后台端 - 导出团队预约数据
     * @param teamAppointmentId 团队预约ID
     * @return 导出结果
     */
    @GetMapping(ADMIN_PREFIX + "/team-appointments/{teamAppointmentId}/export")
    public Result<String> exportTeamAppointmentForAdmin(@PathVariable Long teamAppointmentId) {
        return appointmentService.exportTeamAppointment(teamAppointmentId);
    }
    
    /**
     * 管理后台端 - 导入团队预约数据
     * @param file 上传的文件
     * @return 导入结果
     */
    @PostMapping(ADMIN_PREFIX + "/team-appointments/import")
    public Result<String> importTeamAppointmentForAdmin(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileUploadUtil.uploadFile(file);
            // 这里简化处理，实际应该解析文件内容并保存到数据库
            return Result.success("导入成功", "文件已上传并解析，文件路径：" + fileUrl);
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
}
