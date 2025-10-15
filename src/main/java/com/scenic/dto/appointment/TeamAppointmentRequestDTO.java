package com.scenic.dto.appointment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.scenic.entity.appointment.TeamMember;

/**
 * 团队预约请求DTO类 - 用于处理前端JSON请求
 */
public class TeamAppointmentRequestDTO {
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
    private String appointmentDate;
    
    private String appointmentTime;
    
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
    
    private List<TeamMember> members;
    
    private String createBy;

    // 构造函数
    public TeamAppointmentRequestDTO() {}

    // Getter 和 Setter 方法
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    /**
     * 将字符串格式的日期转换为LocalDateTime
     * @return LocalDateTime对象
     */
    public LocalDateTime getAppointmentDateTime() {
        if (appointmentDate == null) {
            return null;
        }
        
        try {
            if (appointmentDate.contains(" ")) {
                // 包含时间的完整日期时间字符串
                return LocalDateTime.parse(appointmentDate.replace(" ", "T"));
            } else {
                // 只有日期的字符串
                return LocalDateTime.parse(appointmentDate + "T00:00:00");
            }
        } catch (Exception e) {
            // 尝试其他常见格式
            try {
                return java.time.LocalDate.parse(appointmentDate).atStartOfDay();
            } catch (Exception ex) {
                System.err.println("日期解析失败: " + ex.getMessage());
                return null;
            }
        }
    }

    @Override
    public String toString() {
        return "TeamAppointmentRequestDTO{" +
                "teamName='" + teamName + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", scenicSpotId=" + scenicSpotId +
                ", scenicSpotName='" + scenicSpotName + '\'' +
                ", appointmentDate='" + appointmentDate + '\'' +
                ", appointmentTime='" + appointmentTime + '\'' +
                ", remark='" + remark + '\'' +
                ", members=" + members +
                ", createBy='" + createBy + '\'' +
                '}';
    }
}
