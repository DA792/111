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
 * 管理后台团队预约DTO类
 */
public class AdminTeamAppointmentDTO {
    private String appointmentNo;
    
    private Long userId;
    
    @NotBlank(message = "团队名称不能为空")
    @Size(max = 100, message = "团队名称长度不能超过100个字符")
    private String teamName;
    
    @NotBlank(message = "团队负责人不能为空")
    @Size(max = 50, message = "团队负责人长度不能超过50个字符")
    private String teamLeader;
    
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号码")
    private String contactPhone;
    
    @Email(message = "请输入正确的邮箱地址")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String contactEmail;
    
    @Min(value = 1, message = "团队人数必须大于0")
    private Integer teamSize;
    
    private Long scenicSpotId;
    private String scenicSpotName;
    
    @NotNull(message = "预约日期不能为空")
    private String appointmentDate;
    
    private String appointmentTime;
    
    private String formFileId;
    
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remarks;
    
    @Size(max = 500, message = "管理员备注长度不能超过500个字符")
    private String adminRemarks;
    
    private Integer status;
    
    private List<TeamMember> members;
    
    private String createBy;

    // 构造函数
    public AdminTeamAppointmentDTO() {}

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

    public String getTeamLeader() {
        return teamLeader;
    }

    public void setTeamLeader(String teamLeader) {
        this.teamLeader = teamLeader;
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

    public Integer getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(Integer teamSize) {
        this.teamSize = teamSize;
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

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getFormFileId() {
        return formFileId;
    }

    public void setFormFileId(String formFileId) {
        this.formFileId = formFileId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAdminRemarks() {
        return adminRemarks;
    }

    public void setAdminRemarks(String adminRemarks) {
        this.adminRemarks = adminRemarks;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
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

    @Override
    public String toString() {
        return "AdminTeamAppointmentDTO{" +
                "appointmentNo='" + appointmentNo + '\'' +
                ", userId=" + userId +
                ", teamName='" + teamName + '\'' +
                ", teamLeader='" + teamLeader + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", teamSize=" + teamSize +
                ", scenicSpotId=" + scenicSpotId +
                ", scenicSpotName='" + scenicSpotName + '\'' +
                ", appointmentDate='" + appointmentDate + '\'' +
                ", appointmentTime='" + appointmentTime + '\'' +
                ", formFileId='" + formFileId + '\'' +
                ", remarks='" + remarks + '\'' +
                ", adminRemarks='" + adminRemarks + '\'' +
                ", status=" + status +
                ", members=" + members +
                ", createBy='" + createBy + '\'' +
                '}';
    }
}
