package com.scenic.service.appointment.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.scenic.common.constant.AppointmentConstants;
import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.appointment.ActivityAppointmentDTO;
import com.scenic.dto.appointment.PersonalAppointmentDTO;
import com.scenic.dto.appointment.TeamAppointmentDTO;
import com.scenic.entity.appointment.ActivityAppointment;
import com.scenic.entity.appointment.Appointment;
import com.scenic.entity.appointment.AppointmentPerson;
import com.scenic.entity.appointment.TeamAppointment;
import com.scenic.entity.appointment.TeamMember;
import com.scenic.mapper.appointment.ActivityAppointmentMapper;
import com.scenic.mapper.appointment.AppointmentMapper;
import com.scenic.mapper.appointment.AppointmentPersonMapper;
import com.scenic.mapper.appointment.TeamAppointmentMapper;
import com.scenic.mapper.appointment.TeamMemberMapper;
import com.scenic.service.appointment.AppointmentService;
import com.scenic.utils.UserContextUtil;

/**
 * 预约服务实现类
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {
    
    @Autowired
    private AppointmentMapper appointmentMapper;
    
    @Autowired
    private AppointmentPersonMapper appointmentPersonMapper;
    
    @Autowired
    private TeamAppointmentMapper teamAppointmentMapper;
    
    @Autowired
    private TeamMemberMapper teamMemberMapper;
    
    @Autowired
    private ActivityAppointmentMapper activityAppointmentMapper;
    
    @Autowired
    private UserContextUtil userContextUtil;
    
    /**
     * 创建个人预约
     * @param appointmentDTO 预约信息
     * @return 预约结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> createPersonalAppointment(PersonalAppointmentDTO appointmentDTO) {
        try {
            // 校验人数上限
            if (appointmentDTO.getNumberOfPeople() > AppointmentConstants.DEFAULT_MAX_PEOPLE_PER_APPOINTMENT) {
                return Result.error("预约人数超过上限");
            }
            
            // 校验证件号/电话合法性（简化处理）
            if (appointmentDTO.getUserPhone() == null || appointmentDTO.getUserPhone().isEmpty()) {
                return Result.error("联系电话不能为空");
            }
            
            // 创建预约主表记录
            Appointment appointment = new Appointment();
            appointment.setUserId(appointmentDTO.getUserId());
            appointment.setUserName(appointmentDTO.getUserName());
            appointment.setUserPhone(appointmentDTO.getUserPhone());
            appointment.setScenicSpotId(appointmentDTO.getScenicSpotId());
            appointment.setScenicSpotName(appointmentDTO.getScenicSpotName());
            appointment.setAppointmentTime(appointmentDTO.getAppointmentDate());
            appointment.setNumberOfPeople(appointmentDTO.getNumberOfPeople());
            appointment.setRemark(appointmentDTO.getRemark());
            appointment.setStatus(AppointmentConstants.STATUS_CONFIRMED);
            appointment.setCreateTime(LocalDateTime.now());
            appointment.setUpdateTime(LocalDateTime.now());
            
            // 保存预约主表记录
            appointmentMapper.insert(appointment);
            
            // 保存预约人信息
            if (appointmentDTO.getPersons() != null && !appointmentDTO.getPersons().isEmpty()) {
                List<AppointmentPerson> persons = appointmentDTO.getPersons().stream()
                    .map(dto -> {
                        AppointmentPerson person = new AppointmentPerson();
                        person.setAppointmentId(appointment.getId());
                        person.setName(dto.getName());
                        person.setIdCard(dto.getIdCard());
                        person.setPhone(dto.getPhone());
                        person.setAge(dto.getAge());
                        person.setRelationship(dto.getRelationship());
                        person.setCreateTime(LocalDateTime.now());
                        person.setUpdateTime(LocalDateTime.now());
                        return person;
                    })
                    .collect(Collectors.toList());
                
                // 批量插入预约人信息
                appointmentPersonMapper.insertBatch(persons);
            }
            
            return Result.success("预约成功", "APPT" + appointment.getId());
        } catch (Exception e) {
            return Result.error("预约失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建团队预约
     * @param appointmentDTO 预约信息
     * @return 预约结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> createTeamAppointment(TeamAppointmentDTO appointmentDTO) {
        try {
            // 校验联系人电话合法性（简化处理）
            if (appointmentDTO.getContactPhone() == null || appointmentDTO.getContactPhone().isEmpty()) {
                return Result.error("联系电话不能为空");
            }
            
            // 从请求参数中获取createBy
            Long createBy = null;
            String createByStr = appointmentDTO.getCreateBy();
            if (createByStr != null && !createByStr.isEmpty()) {
                try {
                    createBy = Long.valueOf(createByStr);
                } catch (NumberFormatException e) {
                    // 如果转换失败，使用默认值
                    createBy = userContextUtil.getCurrentUserId();
                }
            }
            if (createBy == null) {
                // 从JWT token中获取用户ID
                createBy = userContextUtil.getCurrentUserId();
            }
            
            // 创建团队预约主表记录
            TeamAppointment teamAppointment = new TeamAppointment();
            // 确保预约编号不为null
            String appointmentNo = appointmentDTO.getAppointmentNo();
            System.out.println("DEBUG: appointmentNo from DTO = " + appointmentNo);
            if (appointmentNo == null || appointmentNo.isEmpty()) {
                appointmentNo = "TA" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) 
                              + String.format("%03d", (int)(Math.random() * 1000));
                System.out.println("DEBUG: Generated appointmentNo = " + appointmentNo);
            }
            teamAppointment.setAppointmentNo(appointmentNo);
            System.out.println("DEBUG: Final appointmentNo in TeamAppointment = " + teamAppointment.getAppointmentNo());
            
            // 设置用户ID，如果DTO中提供了则使用，否则使用默认值1
            if (appointmentDTO.getUserId() != null) {
                teamAppointment.setUserId(appointmentDTO.getUserId());
            } else {
                // 使用默认值1，确保不为null
                teamAppointment.setUserId(1L);
            }
            
            teamAppointment.setTeamName(appointmentDTO.getTeamName());
            teamAppointment.setContactPerson(appointmentDTO.getContactPerson());
            teamAppointment.setContactPhone(appointmentDTO.getContactPhone());
            teamAppointment.setContactEmail(appointmentDTO.getContactEmail());
            teamAppointment.setScenicSpotId(appointmentDTO.getScenicSpotId());
            teamAppointment.setScenicSpotName(appointmentDTO.getScenicSpotName());
            teamAppointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
            // 确保appointment_time字段不为null
            if (appointmentDTO.getAppointmentTime() != null) {
                teamAppointment.setAppointmentTime(appointmentDTO.getAppointmentTime());
            } else {
                // 如果没有提供预约时间，则使用当前时间
                teamAppointment.setAppointmentTime(LocalDateTime.now());
            }
            teamAppointment.setRemark(appointmentDTO.getRemark());
            
            // 设置表单文件ID，如果为null则设置为0
            if (appointmentDTO.getFormFileId() != null) {
                teamAppointment.setFormFileId(appointmentDTO.getFormFileId());
            } else {
                teamAppointment.setFormFileId(0L);
            }
            
            // 设置状态，如果DTO中提供了状态则使用，否则使用默认的1(待审核)
            String statusStr = appointmentDTO.getStatus();
            Integer statusValue = null;
            if (statusStr != null && !statusStr.isEmpty()) {
                try {
                    statusValue = Integer.valueOf(statusStr);
                } catch (NumberFormatException e) {
                    // 如果转换失败，使用默认值
                    statusValue = 1; // 1表示待审核
                }
            }
            if (statusValue != null) {
                teamAppointment.setStatus(statusValue);
            } else {
                teamAppointment.setStatus(1); // 1表示待审核
            }
            
            teamAppointment.setCreateTime(LocalDateTime.now());
            teamAppointment.setUpdateTime(LocalDateTime.now());
            teamAppointment.setCreateBy(createBy);
            
            // 保存团队预约主表记录
            teamAppointmentMapper.insert(teamAppointment);
            
            // 保存团队成员信息
            if (appointmentDTO.getMembers() != null && !appointmentDTO.getMembers().isEmpty()) {
                List<TeamMember> members = appointmentDTO.getMembers().stream()
                    .map(dto -> {
                        TeamMember member = new TeamMember();
                        member.setTeamAppointmentId(teamAppointment.getId());
                        member.setName(dto.getName());
                        member.setIdCard(dto.getIdCard());
                        member.setPhone(dto.getPhone());
                        member.setAge(dto.getAge());
                        member.setGender(dto.getGender());
                        member.setRemark(dto.getRemark());
                        member.setCreateTime(LocalDateTime.now());
                        member.setUpdateTime(LocalDateTime.now());
                        return member;
                    })
                    .collect(Collectors.toList());
                
                // 批量插入团队成员信息
                teamMemberMapper.insertBatch(members);
            }
            
            return Result.success("预约成功", "TEAM" + teamAppointment.getId());
        } catch (Exception e) {
            return Result.error("预约失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量保存团队预约
     * @param teamAppointments 团队预约列表
     * @return 保存结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> batchSaveTeamAppointments(List<TeamAppointment> teamAppointments) {
        try {
            if (teamAppointments == null || teamAppointments.isEmpty()) {
                return Result.error("团队预约数据不能为空");
            }
            
            // 批量保存到数据库
            int successCount = 0;
            int failCount = 0;
            StringBuilder errorMsg = new StringBuilder();
            
            for (TeamAppointment teamAppointment : teamAppointments) {
                try {
                    // 设置默认值
                    if (teamAppointment.getStatus() == null) {
                        teamAppointment.setStatus(1); // 1表示待审核
                    }
                    if (teamAppointment.getCreateTime() == null) {
                        teamAppointment.setCreateTime(LocalDateTime.now());
                    }
                    teamAppointment.setUpdateTime(LocalDateTime.now());
                    
                    teamAppointmentMapper.insert(teamAppointment);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    errorMsg.append("第").append(successCount + failCount).append("条数据保存失败: ")
                           .append(e.getMessage()).append("; ");
                }
            }
            
            String message = String.format("批量保存完成，成功%d条，失败%d条", successCount, failCount);
            if (failCount > 0) {
                message += "。错误信息: " + errorMsg.toString();
            }
            
            return Result.success(message, "批量保存成功");
        } catch (Exception e) {
            return Result.error("批量保存失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量保存活动预约
     * @param activityAppointments 活动预约列表
     * @return 保存结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> batchSaveActivityAppointments(List<ActivityAppointment> activityAppointments) {
        try {
            if (activityAppointments == null || activityAppointments.isEmpty()) {
                return Result.error("活动预约数据不能为空");
            }
            
            // 批量保存到数据库
            int successCount = 0;
            int failCount = 0;
            StringBuilder errorMsg = new StringBuilder();
            
            for (ActivityAppointment activityAppointment : activityAppointments) {
                try {
            // 设置默认值
            if (activityAppointment.getStatus() == null) {
                activityAppointment.setStatus(AppointmentConstants.STATUS_PENDING);
            }
                    // 生成预约编号
                    if (activityAppointment.getRegistrationNo() == null || activityAppointment.getRegistrationNo().isEmpty()) {
                        String registrationNo = "ACT" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) 
                              + String.format("%03d", (int)(Math.random() * 1000));
                        activityAppointment.setRegistrationNo(registrationNo);
                    }
                    if (activityAppointment.getCreateTime() == null) {
                        activityAppointment.setCreateTime(LocalDateTime.now());
                    }
                    activityAppointment.setUpdateTime(LocalDateTime.now());
                    
                    activityAppointmentMapper.insert(activityAppointment);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    errorMsg.append("第").append(successCount + failCount).append("条数据保存失败: ")
                           .append(e.getMessage()).append("; ");
                }
            }
            
            String message = String.format("批量保存完成，成功%d条，失败%d条", successCount, failCount);
            if (failCount > 0) {
                message += "。错误信息: " + errorMsg.toString();
            }
            
            return Result.success(message, "批量保存成功");
        } catch (Exception e) {
            return Result.error("批量保存失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新团队预约
     * @param teamAppointmentId 团队预约ID
     * @param appointmentDTO 预约信息
     * @return 更新结果
     */
    @Override
    public Result<String> updateTeamAppointment(Long teamAppointmentId, TeamAppointmentDTO appointmentDTO) {
        try {
            // 校验联系人电话合法性（简化处理）
            if (appointmentDTO.getContactPhone() == null || appointmentDTO.getContactPhone().isEmpty()) {
                return Result.error("联系电话不能为空");
            }
            
            // 检查团队预约是否存在
            TeamAppointment existingTeamAppointment = teamAppointmentMapper.selectById(teamAppointmentId);
            if (existingTeamAppointment == null) {
                return Result.error("团队预约记录不存在");
            }
            
            // 更新团队预约主表记录
            existingTeamAppointment.setTeamName(appointmentDTO.getTeamName());
            existingTeamAppointment.setContactPerson(appointmentDTO.getContactPerson());
            existingTeamAppointment.setContactPhone(appointmentDTO.getContactPhone());
            existingTeamAppointment.setContactEmail(appointmentDTO.getContactEmail());
            existingTeamAppointment.setScenicSpotId(appointmentDTO.getScenicSpotId());
            existingTeamAppointment.setScenicSpotName(appointmentDTO.getScenicSpotName());
            existingTeamAppointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
            existingTeamAppointment.setAppointmentTime(appointmentDTO.getAppointmentTime());
            existingTeamAppointment.setRemark(appointmentDTO.getRemark());
            existingTeamAppointment.setAdminRemarks(appointmentDTO.getAdminRemarks());
            existingTeamAppointment.setCheckInTime(appointmentDTO.getCheckInTime());
            // 处理团队人数字段
            if (appointmentDTO.getTeamSize() != null) {
                existingTeamAppointment.setNumberOfPeople(appointmentDTO.getTeamSize());
            }
            existingTeamAppointment.setUpdateTime(LocalDateTime.now());
            
            // 处理状态字段
            String statusStr = appointmentDTO.getStatus();
            Integer statusValue = null;
            if (statusStr != null && !statusStr.isEmpty()) {
                try {
                    statusValue = Integer.valueOf(statusStr);
                } catch (NumberFormatException e) {
                    // 如果转换失败，保持原有状态
                    statusValue = existingTeamAppointment.getStatus();
                }
            }
            if (statusValue != null) {
                existingTeamAppointment.setStatus(statusValue);
            }
            
            // 保存团队预约主表记录
            teamAppointmentMapper.updateById(existingTeamAppointment);
            
            return Result.success("更新成功", "TEAM" + existingTeamAppointment.getId());
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理后台端新增活动预约
     * @param appointmentDTO 预约信息
     * @return 新增结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> createActivityAppointmentForAdmin(ActivityAppointmentDTO appointmentDTO) {
        try {
            System.out.println("DEBUG: 接收到的预约数据: " + appointmentDTO);
            
            // 校验联系人电话合法性（简化处理）
            if (appointmentDTO.getContactPhone() == null || appointmentDTO.getContactPhone().isEmpty()) {
                return Result.error("联系电话不能为空");
            }
            
            // 校验活动名称不能为空
            if (appointmentDTO.getActivityName() == null || appointmentDTO.getActivityName().isEmpty()) {
                return Result.error("活动名称不能为空");
            }
            
            // 从请求参数中获取createBy
            Long createBy = null;
            Long createByLong = appointmentDTO.getCreateBy();
            if (createByLong != null) {
                createBy = createByLong;
            }
            if (createBy == null) {
                // 从JWT token中获取用户ID
                createBy = userContextUtil.getCurrentUserId();
            }
            
            // 创建活动预约主表记录
            ActivityAppointment activityAppointment = new ActivityAppointment();
            // 生成预约编号
            String registrationNo = "ACT" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) 
                          + String.format("%03d", (int)(Math.random() * 1000));
            activityAppointment.setRegistrationNo(registrationNo);
            activityAppointment.setActivityName(appointmentDTO.getActivityName());
            activityAppointment.setTeamName(appointmentDTO.getTeamName());
            activityAppointment.setContactPerson(appointmentDTO.getContactPerson());
            activityAppointment.setContactPhone(appointmentDTO.getContactPhone());
            activityAppointment.setContactEmail(appointmentDTO.getContactEmail());
            activityAppointment.setActivityId(appointmentDTO.getActivityId());
            // 设置用户ID
            activityAppointment.setUserId(appointmentDTO.getUserId() != null ? appointmentDTO.getUserId() : 1L);
            // 设置表单文件ID，默认为0
            activityAppointment.setFormFileId(0L);
            activityAppointment.setActivityDate(appointmentDTO.getActivityDate());
            activityAppointment.setActivityTime(appointmentDTO.getActivityTime());
            activityAppointment.setNumberOfPeople(appointmentDTO.getNumberOfPeople());
            activityAppointment.setRemark(appointmentDTO.getRemark());
            // 管理后台新增默认状态为待审核
            activityAppointment.setStatus(AppointmentConstants.STATUS_PENDING);
            activityAppointment.setCreateTime(LocalDateTime.now());
            activityAppointment.setUpdateTime(LocalDateTime.now());
            activityAppointment.setCreateBy(createBy);
            
            System.out.println("DEBUG: 准备插入的活动预约数据: " + activityAppointment);
            
            // 保存活动预约主表记录
            int result = activityAppointmentMapper.insert(activityAppointment);
            System.out.println("DEBUG: 插入结果: " + result);
            
            return Result.success("预约成功", "ACT" + activityAppointment.getId());
        } catch (Exception e) {
            System.err.println("预约失败异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("预约失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建活动预约
     * @param appointmentDTO 预约信息
     * @return 预约结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> createActivityAppointment(ActivityAppointmentDTO appointmentDTO) {
        try {
            // 校验联系人电话合法性（简化处理）
            if (appointmentDTO.getContactPhone() == null || appointmentDTO.getContactPhone().isEmpty()) {
                return Result.error("联系电话不能为空");
            }
            
            // 校验活动名称不能为空
            if (appointmentDTO.getActivityName() == null || appointmentDTO.getActivityName().isEmpty()) {
                return Result.error("活动名称不能为空");
            }
            
            // 创建活动预约主表记录
            ActivityAppointment activityAppointment = new ActivityAppointment();
            // 生成预约编号
            String registrationNo = "ACT" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) 
                          + String.format("%03d", (int)(Math.random() * 1000));
            activityAppointment.setRegistrationNo(registrationNo);
            activityAppointment.setActivityName(appointmentDTO.getActivityName());
            activityAppointment.setTeamName(appointmentDTO.getTeamName());
            activityAppointment.setContactPerson(appointmentDTO.getContactPerson());
            activityAppointment.setContactPhone(appointmentDTO.getContactPhone());
            activityAppointment.setContactEmail(appointmentDTO.getContactEmail());
            activityAppointment.setActivityId(appointmentDTO.getActivityId());
            // 设置用户ID
            activityAppointment.setUserId(appointmentDTO.getUserId() != null ? appointmentDTO.getUserId() : 1L);
            // 设置表单文件ID，默认为0
            activityAppointment.setFormFileId(0L);
            activityAppointment.setActivityDate(appointmentDTO.getActivityDate());
            activityAppointment.setActivityTime(appointmentDTO.getActivityTime());
            activityAppointment.setNumberOfPeople(appointmentDTO.getNumberOfPeople());
            activityAppointment.setRemark(appointmentDTO.getRemark());
            
            // 处理状态字段
            if (appointmentDTO.getStatus() != null) {
                activityAppointment.setStatus(appointmentDTO.getStatus());
            } else {
                activityAppointment.setStatus(AppointmentConstants.STATUS_PENDING);
            }
            
            activityAppointment.setCreateTime(LocalDateTime.now());
            activityAppointment.setUpdateTime(LocalDateTime.now());
            
            // 保存活动预约主表记录
            activityAppointmentMapper.insert(activityAppointment);
            
            return Result.success("预约成功", "ACT" + activityAppointment.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("预约失败：" + e.getMessage());
        }
    }
    
    /**
     * 上传团队预约文件
     * @param file 上传的文件
     * @return 上传结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> uploadTeamAppointmentFile(MultipartFile file) {
        try {
            // 这里简化处理，实际应该解析文件内容
            if (file == null || file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            // 模拟文件解析和保存过程
            TeamAppointment teamAppointment = new TeamAppointment();
            teamAppointment.setTeamName("上传团队-" + System.currentTimeMillis());
            teamAppointment.setContactPerson("联系人");
            teamAppointment.setContactPhone("13800138000");
            teamAppointment.setStatus(1); // 1表示待审核
            teamAppointment.setCreateTime(LocalDateTime.now());
            teamAppointment.setUpdateTime(LocalDateTime.now());
            
            teamAppointmentMapper.insert(teamAppointment);
            
            return Result.success("上传成功", "文件已上传并解析，团队预约ID: " + teamAppointment.getId());
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 取消预约
     * @param appointmentId 预约ID
     * @return 取消结果
     */
    @Override
    public Result<String> cancelAppointment(Long appointmentId) {
        try {
            Appointment appointment = appointmentMapper.selectById(appointmentId);
            
            if (appointment == null) {
                return Result.error("预约记录不存在");
            }
            
            // 检查是否可以取消（简化处理，实际应根据配置的时间限制）
            if (appointment.getStatus() != null && appointment.getStatus().equals(AppointmentConstants.STATUS_COMPLETED)) {
                return Result.error("已完成的预约无法取消");
            }
            
            appointment.setStatus(AppointmentConstants.STATUS_CANCELLED);
            appointment.setUpdateTime(LocalDateTime.now());
            appointmentMapper.updateById(appointment);
            
            return Result.success("取消成功", "预约已取消");
        } catch (Exception e) {
            return Result.error("取消失败：" + e.getMessage());
        }
    }
    
    /**
     * 取消团队预约
     * @param teamAppointmentId 团队预约ID
     * @return 取消结果
     */
    @Override
    public Result<String> cancelTeamAppointment(Long teamAppointmentId) {
        try {
            TeamAppointment teamAppointment = teamAppointmentMapper.selectById(teamAppointmentId);
            
            if (teamAppointment == null) {
                return Result.error("团队预约记录不存在");
            }
            
            // 检查是否可以取消（简化处理，实际应根据配置的时间限制）
            if (teamAppointment.getStatus() != null && teamAppointment.getStatus().equals(AppointmentConstants.STATUS_COMPLETED)) {
                return Result.error("已完成的团队预约无法取消");
            }
            
            teamAppointment.setStatus(3); // 3表示已取消
            teamAppointment.setUpdateTime(LocalDateTime.now());
            teamAppointmentMapper.updateById(teamAppointment);
            
            return Result.success("取消成功", "团队预约已取消");
        } catch (Exception e) {
            return Result.error("取消失败：" + e.getMessage());
        }
    }
    
    /**
     * 取消活动预约
     * @param activityAppointmentId 活动预约ID
     * @return 取消结果
     */
    @Override
    public Result<String> cancelActivityAppointment(Long activityAppointmentId) {
        try {
            ActivityAppointment activityAppointment = activityAppointmentMapper.selectById(activityAppointmentId);
            
            if (activityAppointment == null) {
                return Result.error("活动预约记录不存在");
            }
            
            // 检查是否可以取消（简化处理，实际应根据配置的时间限制）
            if (activityAppointment.getStatus() != null && activityAppointment.getStatus().equals(AppointmentConstants.STATUS_COMPLETED)) {
                return Result.error("已完成的活动预约无法取消");
            }
            
            activityAppointment.setStatus(3); // 3表示已取消
            activityAppointment.setUpdateTime(LocalDateTime.now());
            activityAppointmentMapper.updateById(activityAppointment);
            
            return Result.success("取消成功", "活动预约已取消");
        } catch (Exception e) {
            return Result.error("取消失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户预约列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 预约列表
     */
    @Override
    public Result<PageResult<Appointment>> getUserAppointments(Long userId, int page, int size) {
        try {
            // 分页获取用户预约记录
            List<Appointment> userAppointments = appointmentMapper.selectByUserId(userId, (page - 1) * size, size);
            
            // 获取总数
            int total = appointmentMapper.selectCountByUserId(userId);
            
            PageResult<Appointment> pageResult = PageResult.of(total, size, page, userAppointments);
            return Result.success("查询成功", pageResult);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取团队预约列表
     * @param page 页码
     * @param size 每页大小
     * @return 团队预约列表
     */
    @Override
    public Result<PageResult<TeamAppointment>> getTeamAppointments(int page, int size) {
        try {
            // 分页获取团队预约记录
            List<TeamAppointment> teamAppointments = teamAppointmentMapper.selectList((page - 1) * size, size);
            
            // 获取总数
            int total = teamAppointmentMapper.selectCount();
            
            PageResult<TeamAppointment> pageResult = PageResult.of(total, size, page, teamAppointments);
            return Result.success("查询成功", pageResult);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取活动预约列表
     * @param page 页码
     * @param size 每页大小
     * @return 活动预约列表
     */
    @Override
    public Result<PageResult<ActivityAppointment>> getActivityAppointments(int page, int size) {
        try {
            // 分页获取活动预约记录
            List<ActivityAppointment> activityAppointments = activityAppointmentMapper.selectList((page - 1) * size, size);
            
            // 获取总数
            int total = activityAppointmentMapper.selectCount();
            
            PageResult<ActivityAppointment> pageResult = PageResult.of(total, size, page, activityAppointments);
            return Result.success("查询成功", pageResult);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取预约详情
     * @param appointmentId 预约ID
     * @return 预约详情
     */
    @Override
    public Result<Appointment> getAppointmentDetail(Long appointmentId) {
        try {
            Appointment appointment = appointmentMapper.selectById(appointmentId);
            
            if (appointment == null) {
                return Result.error("预约记录不存在");
            }
            
            return Result.success("查询成功", appointment);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取团队预约详情
     * @param teamAppointmentId 团队预约ID
     * @return 团队预约详情
     */
    @Override
    public Result<TeamAppointment> getTeamAppointmentDetail(Long teamAppointmentId) {
        try {
            TeamAppointment teamAppointment = teamAppointmentMapper.selectById(teamAppointmentId);
            
            if (teamAppointment == null) {
                return Result.error("团队预约记录不存在");
            }
            
            return Result.success("查询成功", teamAppointment);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取活动预约详情
     * @param activityAppointmentId 活动预约ID
     * @return 活动预约详情
     */
    @Override
    public Result<ActivityAppointment> getActivityAppointmentDetail(Long activityAppointmentId) {
        try {
            ActivityAppointment activityAppointment = activityAppointmentMapper.selectById(activityAppointmentId);
            
            if (activityAppointment == null) {
                return Result.error("活动预约记录不存在");
            }
            
            return Result.success("查询成功", activityAppointment);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理员审核预约
     * @param appointmentId 预约ID
     * @param status 审核状态
     * @return 审核结果
     */
    @Override
    public Result<String> reviewAppointment(Long appointmentId, String status) {
        try {
            Appointment appointment = appointmentMapper.selectById(appointmentId);
            
            if (appointment == null) {
                return Result.error("预约记录不存在");
            }
            
            // 更新预约状态
            Integer statusValue = null;
            if (status != null && !status.isEmpty()) {
                try {
                    statusValue = Integer.valueOf(status);
                } catch (NumberFormatException e) {
                    return Result.error("状态值格式不正确");
                }
            } else {
                // 如果状态为空，保持原有状态
                statusValue = appointment.getStatus();
            }
            appointment.setStatus(statusValue);
            appointment.setUpdateTime(LocalDateTime.now());
            appointmentMapper.updateById(appointment);
            
            return Result.success("审核成功", "预约状态已更新");
        } catch (Exception e) {
            return Result.error("审核失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理员审核团队预约
     * @param teamAppointmentId 团队预约ID
     * @param status 审核状态
     * @return 审核结果
     */
    @Override
    public Result<String> reviewTeamAppointment(Long teamAppointmentId, String status) {
        try {
            TeamAppointment teamAppointment = teamAppointmentMapper.selectById(teamAppointmentId);
            
            if (teamAppointment == null) {
                return Result.error("团队预约记录不存在");
            }
            
            // 更新团队预约状态
            Integer statusValue = null;
            if (status != null && !status.isEmpty()) {
                try {
                    statusValue = Integer.valueOf(status);
                } catch (NumberFormatException e) {
                    return Result.error("状态值格式不正确");
                }
            } else {
                // 如果状态为空，保持原有状态
                statusValue = teamAppointment.getStatus();
            }
            teamAppointment.setStatus(statusValue);
            teamAppointment.setUpdateTime(LocalDateTime.now());
            teamAppointmentMapper.updateById(teamAppointment);
            
            return Result.success("审核成功", "团队预约状态已更新");
        } catch (Exception e) {
            return Result.error("审核失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理员审核活动预约
     * @param activityAppointmentId 活动预约ID
     * @param status 审核状态
     * @return 审核结果
     */
    @Override
    public Result<String> reviewActivityAppointment(Long activityAppointmentId, String status) {
        try {
            ActivityAppointment activityAppointment = activityAppointmentMapper.selectById(activityAppointmentId);
            
            if (activityAppointment == null) {
                return Result.error("活动预约记录不存在");
            }
            
            // 更新活动预约状态
            Integer statusValue = null;
            if (status != null && !status.isEmpty()) {
                try {
                    statusValue = Integer.valueOf(status);
                } catch (NumberFormatException e) {
                    return Result.error("状态值格式不正确");
                }
            } else {
                // 如果状态为空，保持原有状态
                statusValue = activityAppointment.getStatus();
            }
            activityAppointment.setStatus(statusValue);
            activityAppointment.setUpdateTime(LocalDateTime.now());
            activityAppointmentMapper.updateById(activityAppointment);
            
            return Result.success("审核成功", "活动预约状态已更新");
        } catch (Exception e) {
            return Result.error("审核失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理员更新活动预约
     * @param activityAppointmentId 活动预约ID
     * @param appointmentDTO 活动预约信息
     * @return 更新结果
     */
    @Override
    public Result<String> updateActivityAppointment(Long activityAppointmentId, ActivityAppointmentDTO appointmentDTO) {
        try {
            // 校验联系人电话合法性（简化处理）
            if (appointmentDTO.getContactPhone() == null || appointmentDTO.getContactPhone().isEmpty()) {
                return Result.error("联系电话不能为空");
            }
            
            // 检查活动预约是否存在
            ActivityAppointment existingActivityAppointment = activityAppointmentMapper.selectById(activityAppointmentId);
            if (existingActivityAppointment == null) {
                return Result.error("活动预约记录不存在");
            }
            
            // 更新活动预约主表记录
            existingActivityAppointment.setActivityName(appointmentDTO.getActivityName());
            existingActivityAppointment.setTeamName(appointmentDTO.getTeamName());
            existingActivityAppointment.setContactPerson(appointmentDTO.getContactPerson());
            existingActivityAppointment.setContactPhone(appointmentDTO.getContactPhone());
            existingActivityAppointment.setContactEmail(appointmentDTO.getContactEmail());
            existingActivityAppointment.setActivityId(appointmentDTO.getActivityId());
            existingActivityAppointment.setActivityDate(appointmentDTO.getActivityDate());
            existingActivityAppointment.setActivityTime(appointmentDTO.getActivityTime());
            existingActivityAppointment.setNumberOfPeople(appointmentDTO.getNumberOfPeople());
            existingActivityAppointment.setRemark(appointmentDTO.getRemark());
            existingActivityAppointment.setUpdateTime(LocalDateTime.now());
            
            // 保存活动预约主表记录
            activityAppointmentMapper.updateById(existingActivityAppointment);
            
            return Result.success("更新成功", "ACT" + existingActivityAppointment.getId());
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理员查询预约列表
     * @param page 页码
     * @param size 每页大小
     * @param userName 用户姓名（可选）
     * @param scenicSpotName 景点名称（可选）
     * @param status 预约状态（可选）
     * @return 预约列表
     */
    @Override
    public Result<PageResult<Appointment>> getAdminAppointments(
            int page, int size, String userName, String scenicSpotName, String status) {
        try {
            // 分页获取预约记录
            List<Appointment> filteredAppointments = appointmentMapper.selectForAdmin(userName, scenicSpotName, status, (page - 1) * size, size);
            
            // 获取总数
            int total = appointmentMapper.selectCountForAdmin(userName, scenicSpotName, status);
            
            PageResult<Appointment> pageResult = PageResult.of(total, size, page, filteredAppointments);
            return Result.success("查询成功", pageResult);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
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
    @Override
    public Result<PageResult<TeamAppointment>> getAdminTeamAppointments(
            int page, int size, String teamName, String contactPerson, String contactPhone, 
            String status, String startTime, String endTime) {
        try {
            // 分页获取团队预约记录
            List<TeamAppointment> filteredTeamAppointments = teamAppointmentMapper.selectForAdmin(
                teamName, contactPerson, contactPhone, status, startTime, endTime, (page - 1) * size, size);
            
            // 获取总数
            int total = teamAppointmentMapper.selectCountForAdmin(
                teamName, contactPerson, contactPhone, status, startTime, endTime);
            
            PageResult<TeamAppointment> pageResult = PageResult.of(total, size, page, filteredTeamAppointments);
            return Result.success("查询成功", pageResult);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
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
    @Override
    public Result<PageResult<ActivityAppointment>> getAdminActivityAppointments(
            int page, int size, String activityName, String contactPerson, String contactPhone, String status, String startTime, String endTime) {
        try {
            // 分页获取活动预约记录
            List<ActivityAppointment> filteredActivityAppointments = activityAppointmentMapper.selectForAdmin(activityName, contactPerson, contactPhone, status, startTime, endTime, (page - 1) * size, size);
            
            // 获取总数
            int total = activityAppointmentMapper.selectCountForAdmin(activityName, contactPerson, contactPhone, status, startTime, endTime);
            
            PageResult<ActivityAppointment> pageResult = PageResult.of(total, size, page, filteredActivityAppointments);
            return Result.success("查询成功", pageResult);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理员删除预约
     * @param appointmentId 预约ID
     * @return 删除结果
     */
    @Override
    public Result<String> deleteAppointment(Long appointmentId) {
        try {
            Appointment appointment = appointmentMapper.selectById(appointmentId);
            
            if (appointment == null) {
                return Result.error("预约记录不存在");
            }
            
            // 删除预约主表记录
            appointmentMapper.deleteById(appointmentId);
            
            // 删除预约人信息
            appointmentPersonMapper.deleteByAppointmentId(appointmentId);
            
            return Result.success("删除成功", "预约已删除");
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理员删除团队预约
     * @param teamAppointmentId 团队预约ID
     * @return 删除结果
     */
    @Override
    public Result<String> deleteTeamAppointment(Long teamAppointmentId) {
        try {
            TeamAppointment teamAppointment = teamAppointmentMapper.selectById(teamAppointmentId);
            
            if (teamAppointment == null) {
                return Result.error("团队预约记录不存在");
            }
            
            // 删除团队预约主表记录
            teamAppointmentMapper.deleteById(teamAppointmentId);
            
            // 删除团队成员信息
            teamMemberMapper.deleteByTeamAppointmentId(teamAppointmentId);
            
            return Result.success("删除成功", "团队预约已删除");
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理员删除活动预约
     * @param activityAppointmentId 活动预约ID
     * @return 删除结果
     */
    @Override
    public Result<String> deleteActivityAppointment(Long activityAppointmentId) {
        try {
            ActivityAppointment activityAppointment = activityAppointmentMapper.selectById(activityAppointmentId);
            
            if (activityAppointment == null) {
                return Result.error("活动预约记录不存在");
            }
            
            // 删除活动预约主表记录
            activityAppointmentMapper.deleteById(activityAppointmentId);
            
            return Result.success("删除成功", "活动预约已删除");
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 导出团队预约数据
     * @param teamAppointmentId 团队预约ID
     * @return 导出结果
     */
    @Override
    public Result<String> exportTeamAppointment(Long teamAppointmentId) {
        try {
            TeamAppointment teamAppointment = teamAppointmentMapper.selectById(teamAppointmentId);
            
            if (teamAppointment == null) {
                return Result.error("团队预约记录不存在");
            }
            
            // 这里简化处理，实际应该生成文件并提供下载链接
            return Result.success("导出成功", "团队预约数据已导出，文件名: team_appointment_" + teamAppointmentId + ".xlsx");
        } catch (Exception e) {
            return Result.error("导出失败：" + e.getMessage());
        }
    }
    
    /**
     * 导出活动预约数据
     * @param activityAppointmentId 活动预约ID
     * @return 导出结果
     */
    @Override
    public Result<String> exportActivityAppointment(Long activityAppointmentId) {
        try {
            ActivityAppointment activityAppointment = activityAppointmentMapper.selectById(activityAppointmentId);
            
            if (activityAppointment == null) {
                return Result.error("活动预约记录不存在");
            }
            
            // 这里简化处理，实际应该生成文件并提供下载链接
            return Result.success("导出成功", "活动预约数据已导出，文件名: activity_appointment_" + activityAppointmentId + ".xlsx");
        } catch (Exception e) {
            return Result.error("导出失败：" + e.getMessage());
        }
    }
}
