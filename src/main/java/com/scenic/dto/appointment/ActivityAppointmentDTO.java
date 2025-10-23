package com.scenic.dto.appointment;

import java.time.LocalDateTime;
import java.util.List;

import com.scenic.entity.appointment.TeamMember;

/**
 * 活动预约DTO类
 * 与activity_registration表字段对应
 */
public class ActivityAppointmentDTO {
    private String activityName;      // 对应 activity_title
    private String teamName;          // 团队名称
    private String contactPerson;     // 对应 team_leader
    private String contactPhone;      // 对应 contact_phone
    private String contactEmail;      // 对应 contact_email
    private Long activityId;          // 对应 activity_id
    private Long userId;              // 对应 user_id
    private Long formFileId;          // 对应 form_file_id
    private LocalDateTime activityDate; // 对应 registration_time
    private String activityTime;      // 对应 activity_time
    private Integer numberOfPeople;   // 对应 team_size
    private String remark;            // 对应 remarks
    private Integer status;           // 状态
    private Long createBy;            // 创建者ID
    private String registrationNo;    // 预约编号
    private List<TeamMember> members; // 团队成员列表

    // 构造函数
    public ActivityAppointmentDTO() {}

    // Getter 和 Setter 方法
    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
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

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFormFileId() {
        return formFileId;
    }

    public void setFormFileId(Long formFileId) {
        this.formFileId = formFileId;
    }

    public LocalDateTime getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(LocalDateTime activityDate) {
        this.activityDate = activityDate;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(String activityTime) {
        this.activityTime = activityTime;
    }

    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public List<TeamMember> getMembers() {
        return members;
    }

    public void setMembers(List<TeamMember> members) {
        this.members = members;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    @Override
    public String toString() {
        return "ActivityAppointmentDTO{" +
                "activityName='" + activityName + '\'' +
                ", teamName='" + teamName + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", activityId=" + activityId +
                ", userId=" + userId +
                ", formFileId=" + formFileId +
                ", activityDate=" + activityDate +
                ", activityTime='" + activityTime + '\'' +
                ", numberOfPeople=" + numberOfPeople +
                ", remark='" + remark + '\'' +
                ", status=" + status +
                ", createBy=" + createBy +
                ", registrationNo='" + registrationNo + '\'' +
                ", members=" + members +
                '}';
    }
}
