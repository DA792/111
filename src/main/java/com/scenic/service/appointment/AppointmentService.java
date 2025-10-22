package com.scenic.service.appointment;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.appointment.ActivityAppointmentDTO;
import com.scenic.dto.appointment.PersonalAppointmentDTO;
import com.scenic.dto.appointment.TeamAppointmentDTO;
import com.scenic.entity.appointment.ActivityAppointment;
import com.scenic.entity.appointment.Appointment;
import com.scenic.entity.appointment.IndividualReservation;
import com.scenic.entity.appointment.TeamAppointment;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约服务接口
 */
public interface AppointmentService {
    
    /**
     * 创建个人预约
     * @param appointmentDTO 预约信息
     * @return 预约结果
     */
    Result<String> createPersonalAppointment(PersonalAppointmentDTO appointmentDTO);
    
    /**
     * 创建团队预约
     * @param appointmentDTO 预约信息
     * @return 预约结果
     */
    Result<String> createTeamAppointment(TeamAppointmentDTO appointmentDTO);
    
    /**
     * 管理后台创建团队预约
     * @param appointmentDTO 预约信息
     * @return 预约结果
     */
    Result<String> createTeamAppointmentForAdmin(TeamAppointmentDTO appointmentDTO);
    
    /**
     * 批量保存团队预约
     * @param teamAppointments 团队预约列表
     * @return 保存结果
     */
    Result<String> batchSaveTeamAppointments(List<TeamAppointment> teamAppointments);
    
    /**
     * 批量保存活动预约
     * @param activityAppointments 活动预约列表
     * @return 保存结果
     */
    Result<String> batchSaveActivityAppointments(List<ActivityAppointment> activityAppointments);
    
    /**
     * 更新团队预约
     * @param teamAppointmentId 团队预约ID
     * @param appointmentDTO 预约信息
     * @return 更新结果
     */
    Result<String> updateTeamAppointment(Long teamAppointmentId, TeamAppointmentDTO appointmentDTO);
    
    /**
     * 创建活动预约
     * @param appointmentDTO 预约信息
     * @return 预约结果
     */
    Result<String> createActivityAppointment(ActivityAppointmentDTO appointmentDTO);
    
    /**
     * 管理后台端新增活动预约
     * @param appointmentDTO 活动预约信息
     * @return 新增结果
     */
    Result<String> createActivityAppointmentForAdmin(ActivityAppointmentDTO appointmentDTO);
    
    /**
     * 上传团队预约文件
     * @param file 上传的文件
     * @return 上传结果
     */
    Result<String> uploadTeamAppointmentFile(MultipartFile file);
    
    /**
     * 取消预约
     * @param appointmentId 预约ID
     * @return 取消结果
     */
    Result<String> cancelAppointment(Long appointmentId);
    
    /**
     * 取消团队预约
     * @param teamAppointmentId 团队预约ID
     * @return 取消结果
     */
    Result<String> cancelTeamAppointment(Long teamAppointmentId);
    
    /**
     * 取消活动预约
     * @param activityAppointmentId 活动预约ID
     * @return 取消结果
     */
    Result<String> cancelActivityAppointment(Long activityAppointmentId);
    
    /**
     * 获取个人预约列表（小程序端）
     * @param page 页码
     * @param size 每页大小
     * @return 个人预约列表
     */
    Result<PageResult<IndividualReservation>> getIndividualReservations(int page, int size);
    
    /**
     * 获取用户预约列表（小程序端）
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 预约列表
     */
    Result<PageResult<IndividualReservation>> getUserAppointments(Long userId, int page, int size);
    
    /**
     * 获取团队预约列表
     * @param page 页码
     * @param size 每页大小
     * @return 团队预约列表
     */
    Result<PageResult<TeamAppointment>> getTeamAppointments(int page, int size);
    
    /**
     * 获取活动预约列表
     * @param page 页码
     * @param size 每页大小
     * @return 活动预约列表
     */
    Result<PageResult<ActivityAppointment>> getActivityAppointments(int page, int size);
    
    /**
     * 获取预约详情
     * @param appointmentId 预约ID
     * @return 预约详情
     */
    Result<Appointment> getAppointmentDetail(Long appointmentId);
    
    /**
     * 获取团队预约详情
     * @param teamAppointmentId 团队预约ID
     * @return 团队预约详情
     */
    Result<TeamAppointment> getTeamAppointmentDetail(Long teamAppointmentId);
    
    /**
     * 获取团队预约详情（包含团队成员信息）
     * @param teamAppointmentId 团队预约ID
     * @return 团队预约详情（包含团队成员信息）
     */
    Result<TeamAppointmentDTO> getTeamAppointmentDetailWithMembers(Long teamAppointmentId);
    
    /**
     * 获取活动预约详情
     * @param activityAppointmentId 活动预约ID
     * @return 活动预约详情
     */
    Result<ActivityAppointment> getActivityAppointmentDetail(Long activityAppointmentId);
    
    /**
     * 获取活动预约详情（包含团队成员信息）
     * @param activityAppointmentId 活动预约ID
     * @return 活动预约详情（包含团队成员信息）
     */
    Result<ActivityAppointmentDTO> getActivityAppointmentDetailWithMembers(Long activityAppointmentId);
    
    /**
     * 管理员审核预约
     * @param appointmentId 预约ID
     * @param status 审核状态
     * @return 审核结果
     */
    Result<String> reviewAppointment(Long appointmentId, String status);
    
    /**
     * 管理员审核团队预约
     * @param teamAppointmentId 团队预约ID
     * @param status 审核状态
     * @return 审核结果
     */
    Result<String> reviewTeamAppointment(Long teamAppointmentId, String status);
    
    /**
     * 管理员审核活动预约
     * @param activityAppointmentId 活动预约ID
     * @param status 审核状态
     * @return 审核结果
     */
    Result<String> reviewActivityAppointment(Long activityAppointmentId, String status);
    
    /**
     * 管理员更新活动预约
     * @param activityAppointmentId 活动预约ID
     * @param appointmentDTO 活动预约信息
     * @return 更新结果
     */
    Result<String> updateActivityAppointment(Long activityAppointmentId, ActivityAppointmentDTO appointmentDTO);
    
    /**
     * 管理员查询预约列表
     * @param page 页码
     * @param size 每页大小
     * @param userName 用户姓名（可选）
     * @param scenicSpotName 景点名称（可选）
     * @param status 预约状态（可选）
     * @return 预约列表
     */
    Result<PageResult<Appointment>> getAdminAppointments(
            int page, int size, String userName, String scenicSpotName, String status);
    
    /**
     * 管理员查询团队预约列表
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
    Result<PageResult<TeamAppointment>> getAdminTeamAppointments(
            int page, int size, String teamName, String contactPerson, String contactPhone, 
            String status, String startTime, String endTime);
    
    /**
     * 管理员查询活动预约列表
     * @param page 页码
     * @param size 每页大小
     * @param activityName 活动名称（可选）
     * @param contactPerson 联系人（可选）
     * @param contactPhone 联系电话（可选）
     * @param status 预约状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 活动预约列表
     */
    Result<PageResult<ActivityAppointment>> getAdminActivityAppointments(
            int page, int size, String activityName, String contactPerson, String contactPhone, String status, String startTime, String endTime);
    
    /**
     * 管理员删除预约
     * @param appointmentId 预约ID
     * @return 删除结果
     */
    Result<String> deleteAppointment(Long appointmentId);
    
    /**
     * 管理员删除团队预约
     * @param teamAppointmentId 团队预约ID
     * @return 删除结果
     */
    Result<String> deleteTeamAppointment(Long teamAppointmentId);
    
    /**
     * 管理员删除活动预约
     * @param activityAppointmentId 活动预约ID
     * @return 删除结果
     */
    Result<String> deleteActivityAppointment(Long activityAppointmentId);
    
    /**
     * 导出团队预约数据
     * @param teamAppointmentId 团队预约ID
     * @return 导出结果
     */
    Result<String> exportTeamAppointment(Long teamAppointmentId);
    
    /**
     * 导出活动预约数据
     * @param activityAppointmentId 活动预约ID
     * @return 导出结果
     */
    Result<String> exportActivityAppointment(Long activityAppointmentId);
}
