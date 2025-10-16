                                                                                                                                            package com.scenic.entity.appointment;

import java.time.LocalDateTime;

/**
 * 团队预约实体类
 */
public class TeamAppointment {
    private Long id;
    private String appointmentNo;
    private Long userId;
    private String teamName;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private Integer numberOfPeople;
    private Long scenicSpotId;
    private String scenicSpotName;
    private LocalDateTime appointmentDate;
    private LocalDateTime appointmentTime;
    private String remark;
    private Integer status; // 预约状态：0-已取消，1-待审核，2-已完成
    private Long formFileId; // 表单文件ID
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createBy;

    // 构造函数
    public TeamAppointment() {}

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public String getAppointmentNo() {
        return appointmentNo;
    }

    public void setAppointmentNo(String appointmentNo) {
        this.appointmentNo = appointmentNo;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Long getFormFileId() {
        return formFileId;
    }

    public void setFormFileId(Long formFileId) {
        this.formFileId = formFileId;
    }

    @Override
    public String toString() {
        return "TeamAppointment{" +
                "id=" + id +
                ", appointmentNo='" + appointmentNo + '\'' +
                ", userId=" + userId +
                ", teamName='" + teamName + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", numberOfPeople=" + numberOfPeople +
                ", scenicSpotId=" + scenicSpotId +
                ", scenicSpotName='" + scenicSpotName + '\'' +
                ", appointmentDate=" + appointmentDate +
                ", appointmentTime=" + appointmentTime +
                ", remark='" + remark + '\'' +
                ", status='" + status + '\'' +
                ", formFileId=" + formFileId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createBy=" + createBy +
                '}';
    }
}
