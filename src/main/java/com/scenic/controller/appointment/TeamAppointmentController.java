package com.scenic.controller.appointment;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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
import com.scenic.dto.appointment.AdminTeamAppointmentDTO;
import com.scenic.dto.appointment.TeamAppointmentRequestDTO;
import com.scenic.entity.appointment.TeamAppointment;
import com.scenic.mapper.appointment.TeamAppointmentMapper;
import com.scenic.service.appointment.AppointmentService;
import com.scenic.utils.ExcelParserUtil;
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
    
    @Autowired
    private ExcelParserUtil excelParserUtil;
    
    @Autowired
    private TeamAppointmentMapper teamAppointmentMapper;
    
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
    public Result<String> createTeamAppointment(@Valid @RequestBody TeamAppointmentRequestDTO appointmentDTO,
                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            StringBuilder errorMsg = new StringBuilder();
            for (ObjectError error : errors) {
                errorMsg.append(error.getDefaultMessage()).append("; ");
            }
            return Result.error("参数验证失败: " + errorMsg.toString());
        }
        
        // 将TeamAppointmentRequestDTO转换为TeamAppointmentDTO
        TeamAppointmentDTO teamAppointmentDTO = new TeamAppointmentDTO();
        // 生成预约编号 TA + 当前日期 + 3位随机数
        String appointmentNo = "TA" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) 
                             + String.format("%03d", (int)(Math.random() * 1000));
        teamAppointmentDTO.setAppointmentNo(appointmentNo);
        teamAppointmentDTO.setTeamName(appointmentDTO.getTeamName());
        teamAppointmentDTO.setContactPerson(appointmentDTO.getContactPerson());
        teamAppointmentDTO.setContactPhone(appointmentDTO.getContactPhone());
        teamAppointmentDTO.setContactEmail(appointmentDTO.getContactEmail());
        teamAppointmentDTO.setScenicSpotId(appointmentDTO.getScenicSpotId());
        teamAppointmentDTO.setScenicSpotName(appointmentDTO.getScenicSpotName());
        teamAppointmentDTO.setRemark(appointmentDTO.getRemark());
        teamAppointmentDTO.setMembers(appointmentDTO.getMembers());
        teamAppointmentDTO.setCreateBy(appointmentDTO.getCreateBy());
        
        // 设置用户ID，如果没有提供则使用默认值1
        Long userId = 1L;
        if (appointmentDTO.getCreateBy() != null && !appointmentDTO.getCreateBy().isEmpty()) {
            try {
                userId = Long.parseLong(appointmentDTO.getCreateBy());
            } catch (NumberFormatException e) {
                // 如果转换失败，使用默认值
                userId = 1L;
            }
        }
        teamAppointmentDTO.setUserId(userId);
        
        // 处理预约日期和时间
        teamAppointmentDTO.setAppointmentDate(appointmentDTO.getAppointmentDateTime());
        // appointmentTime字段在数据库中是datetime类型，需要转换
        if (appointmentDTO.getAppointmentTime() != null && !appointmentDTO.getAppointmentTime().isEmpty()) {
            // 如果是时间段描述（如"上午"、"下午"），则设置为null或默认值
            // 实际应用中应该根据业务需求进行转换
            teamAppointmentDTO.setAppointmentTime(null);
        }
        
        return appointmentService.createTeamAppointment(teamAppointmentDTO);
    }
    
    /**
     * 管理后台端 - 新增团队预约
     * @param appointmentDTO 团队预约信息
     * @return 新增结果
     */
    @PostMapping(ADMIN_PREFIX + "/team-appointments")
    public Result<String> createTeamAppointmentForAdmin(@Valid @RequestBody AdminTeamAppointmentDTO appointmentDTO,
                                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            StringBuilder errorMsg = new StringBuilder();
            for (ObjectError error : errors) {
                errorMsg.append(error.getDefaultMessage()).append("; ");
            }
            return Result.error("参数验证失败: " + errorMsg.toString());
        }
        
        // 将AdminTeamAppointmentDTO转换为TeamAppointmentDTO
        TeamAppointmentDTO teamAppointmentDTO = new TeamAppointmentDTO();
        // 如果没有提供预约编号，则生成一个
        String appointmentNo = appointmentDTO.getAppointmentNo();
        if (appointmentNo == null || appointmentNo.isEmpty()) {
            appointmentNo = "TA" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) 
                          + String.format("%03d", (int)(Math.random() * 1000));
        }
        teamAppointmentDTO.setAppointmentNo(appointmentNo);
        teamAppointmentDTO.setUserId(appointmentDTO.getUserId());
        teamAppointmentDTO.setTeamName(appointmentDTO.getTeamName());
        teamAppointmentDTO.setContactPerson(appointmentDTO.getTeamLeader());
        teamAppointmentDTO.setContactPhone(appointmentDTO.getContactPhone());
        teamAppointmentDTO.setContactEmail(appointmentDTO.getContactEmail());
        teamAppointmentDTO.setScenicSpotId(appointmentDTO.getScenicSpotId());
        teamAppointmentDTO.setScenicSpotName(appointmentDTO.getScenicSpotName());
        teamAppointmentDTO.setRemark(appointmentDTO.getRemarks());
        teamAppointmentDTO.setMembers(appointmentDTO.getMembers());
        // 处理团队人数字段 - 从teamSize转换为numberOfPeople
        teamAppointmentDTO.setTeamSize(appointmentDTO.getTeamSize());
        
        // 处理formFileId类型转换
        if (appointmentDTO.getFormFileId() != null && !appointmentDTO.getFormFileId().isEmpty()) {
            try {
                teamAppointmentDTO.setFormFileId(Long.valueOf(appointmentDTO.getFormFileId()));
            } catch (NumberFormatException e) {
                System.err.println("formFileId转换失败: " + e.getMessage());
            }
        }
        
        // 处理预约日期
        if (appointmentDTO.getAppointmentDate() != null) {
            try {
                // 将字符串转换为LocalDateTime
                LocalDateTime appointmentDate;
                if (appointmentDTO.getAppointmentDate().contains(" ")) {
                    // 包含时间的完整日期时间字符串
                    appointmentDate = LocalDateTime.parse(appointmentDTO.getAppointmentDate().replace(" ", "T"));
                } else {
                    // 只有日期的字符串
                    java.time.LocalDate date = java.time.LocalDate.parse(appointmentDTO.getAppointmentDate());
                    appointmentDate = date.atStartOfDay();
                }
                teamAppointmentDTO.setAppointmentDate(appointmentDate);
            } catch (Exception e) {
                // 如果解析失败，记录日志但不抛出异常
                System.err.println("日期解析失败: " + e.getMessage());
            }
        }
        
        // 处理预约时间
        if (appointmentDTO.getAppointmentTime() != null && !appointmentDTO.getAppointmentTime().isEmpty()) {
            try {
                // 尝试将字符串转换为LocalDateTime
                LocalDateTime appointmentTime = LocalDateTime.parse(appointmentDTO.getAppointmentTime());
                teamAppointmentDTO.setAppointmentTime(appointmentTime);
            } catch (Exception e) {
                // 如果解析失败，设置为null
                teamAppointmentDTO.setAppointmentTime(null);
            }
        } else {
            teamAppointmentDTO.setAppointmentTime(null);
        }
        
        teamAppointmentDTO.setCreateBy(appointmentDTO.getCreateBy());
        
        // 设置状态，如果未提供则使用默认值"1"（待审核）
        if (appointmentDTO.getStatus() != null) {
            teamAppointmentDTO.setStatus(String.valueOf(appointmentDTO.getStatus()));
        } else {
            teamAppointmentDTO.setStatus("1"); // 1表示待审核
        }
        
        return appointmentService.createTeamAppointment(teamAppointmentDTO);
    }
    
    /**
     * 管理后台端 - 更新团队预约
     * @param teamAppointmentId 团队预约ID
     * @param appointmentDTO 团队预约信息
     * @param request HTTP请求对象，用于获取用户信息
     * @return 更新结果
     */
    @PutMapping(ADMIN_PREFIX + "/team-appointments/{teamAppointmentId}")
    public Result<String> updateTeamAppointmentForAdmin(
            @PathVariable Long teamAppointmentId,
            @Valid @RequestBody TeamAppointmentDTO appointmentDTO,
            BindingResult bindingResult,
            HttpServletRequest request) {
        // 参数验证
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            StringBuilder errorMsg = new StringBuilder();
            for (ObjectError error : errors) {
                errorMsg.append(error.getDefaultMessage()).append("; ");
            }
            return Result.error("参数验证失败: " + errorMsg.toString());
        }
        // 从请求中获取用户ID作为updateBy
        String userInfoStr = request.getHeader("user-info");
        if (userInfoStr != null && !userInfoStr.isEmpty()) {
            try {
                // 解析JSON字符串获取用户ID
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode userInfo = objectMapper.readTree(userInfoStr);
                Long userId = userInfo.get("id").asLong();
                appointmentDTO.setUpdateBy(userId);
            } catch (Exception e) {
                System.err.println("解析用户信息失败: " + e.getMessage());
            }
        }
        return appointmentService.updateTeamAppointment(teamAppointmentId, appointmentDTO);
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
        } catch (Exception e) {
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
     * @param contactPhone 联系电话（可选）
     * @param status 预约状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 团队预约列表
     */
    @GetMapping(ADMIN_PREFIX + "/team-appointments")
    public Result<PageResult<TeamAppointment>> getTeamAppointmentsForAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String teamName,
            @RequestParam(required = false) String contactPerson,
            @RequestParam(required = false) String contactPhone,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return appointmentService.getAdminTeamAppointments(page, size, teamName, contactPerson, contactPhone, status, startTime, endTime);
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



            // 解析Excel文件
            List<TeamAppointment> teamAppointments = excelParserUtil.parseTeamAppointmentExcel(file);
            
            // 调用服务层批量保存
            return appointmentService.batchSaveTeamAppointments(teamAppointments);
        } catch (Exception e) {
            return Result.error("文件解析失败：" + e.getMessage());

        }
    }
}
