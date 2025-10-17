package com.scenic.dto.appointment;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import com.scenic.entity.appointment.TeamMember;

/**
 * 团队预约DTO类
 */
public class TeamAppointmentDTO {
    @Size(max = 50, message = "预约编号长度不超过50字符")
    private String appointmentNo;
    
    private Long userId;
    
    @NotBlank(message = "团队名称不能为空")
    @Size(max = 100, message = "团队名称长度不能超过100个字符")
    private String teamName;
    
    @NotBlank(message = "联系人不能为空")
    @Size(max = 50, message = "联系人长度不能超过50个字符")
    private String contactPerson;
    
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号码")
    private String contactPhone;
    
    @Email(message = "请输入正确的邮箱地址")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String contactEmail;
    
    private Long scenicSpotId;
    private String scenicSpotName;
    
    @NotNull(message = "预约日期不能为空")
    private LocalDateTime appointmentDate;
    
    private LocalDateTime appointmentTime;
    
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
    
    private String status; // 用于存储数字字符串，如"1"表示待审核
    
    private Long formFileId; // 表单文件ID
    
    private Integer teamSize; // 团队人数
    
    private String adminRemarks; // 管理员备注
    
    private LocalDateTime checkInTime; // 签到时间
    
    private List<TeamMember> members;
    
    private String createBy;

    // 构造函数
    public TeamAppointmentDTO() {}

    // Getter 和 Setter 方法
    public String getAppointmentNo() {
        return appointmentNo;
    }

    public void setAppointmentNo(String appointmentNo) {
        this.appointmentNo = appointmentNo;
    }
    
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Long getScenicSpotId() {
        return scenicSpotId;
    }

    public void setScenicSpotId(Long scenicSpotId) {
        this.scenicSpotId = scenicSpotId;
    }

    public String getScenicSpotName() {
        return scenicSpotName;
    }

    public void setScenicSpotName(String scenicSpotName) {
        this.scenicSpotName = scenicSpotName;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TeamMember> getMembers() {
        return members;
    }

    public void setMembers(List<TeamMember> members) {
        this.members = members;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Long getFormFileId() {
        return formFileId;
    }

    public void setFormFileId(Long formFileId) {
        this.formFileId = formFileId;
    }

    public Integer getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(Integer teamSize) {
        this.teamSize = teamSize;
    }

    public Integer getNumberOfPeople() {
        return this.teamSize;
    }

    public void setNumberOfPeople(Integer numberOfPeople) {
        this.teamSize = numberOfPeople;
    }

    public String getAdminRemarks() {
        return adminRemarks;
    }

    public void setAdminRemarks(String adminRemarks) {
        this.adminRemarks = adminRemarks;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    @Override
    public String toString() {
        return "TeamAppointmentDTO{" +
                "appointmentNo='" + appointmentNo + '\'' +
                ", userId=" + userId +
                ", teamName='" + teamName + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", scenicSpotId=" + scenicSpotId +
                ", scenicSpotName='" + scenicSpotName + '\'' +
                ", appointmentDate=" + appointmentDate +
                ", appointmentTime=" + appointmentTime +
                ", remark='" + remark + '\'' +
                ", status='" + status + '\'' +
                ", formFileId=" + formFileId +
                ", teamSize=" + teamSize +
                ", adminRemarks='" + adminRemarks + '\'' +
                ", checkInTime=" + checkInTime +
                ", members=" + members +
                ", createBy='" + createBy + '\'' +
                '}';
    }
}
