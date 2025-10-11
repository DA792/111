package com.scenic.dto.appointment;

import java.time.LocalDateTime;

/**
 * 活动预约DTO类
 */
public class ActivityAppointmentDTO {
    private String activityName;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private Long activityId;
    private LocalDateTime activityDate;
    private String activityTime;
    private Integer numberOfPeople;
    private String remark;

    // 构造函数
    public ActivityAppointmentDTO() {}

    // Getter 和 Setter 方法
    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
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

    @Override
    public String toString() {
        return "ActivityAppointmentDTO{" +
                "activityName='" + activityName + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", activityId=" + activityId +
                ", activityDate=" + activityDate +
                ", activityTime='" + activityTime + '\'' +
                ", numberOfPeople=" + numberOfPeople +
                ", remark='" + remark + '\'' +
                '}';
    }
}
