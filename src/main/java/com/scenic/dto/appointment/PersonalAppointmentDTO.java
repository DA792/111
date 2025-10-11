package com.scenic.dto.appointment;

import com.scenic.entity.appointment.AppointmentPerson;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 个人预约DTO类
 */
public class PersonalAppointmentDTO {
    private Long userId;
    private String userName;
    private String userPhone;
    private Long scenicSpotId;
    private String scenicSpotName;
    private LocalDateTime appointmentDate;
    private String appointmentTime;
    private Integer numberOfPeople;
    private String remark;
    private List<AppointmentPerson> persons;

    // 构造函数
    public PersonalAppointmentDTO() {}

    // Getter 和 Setter 方法
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
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

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
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

    public List<AppointmentPerson> getPersons() {
        return persons;
    }

    public void setPersons(List<AppointmentPerson> persons) {
        this.persons = persons;
    }

    @Override
    public String toString() {
        return "PersonalAppointmentDTO{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", scenicSpotId=" + scenicSpotId +
                ", scenicSpotName='" + scenicSpotName + '\'' +
                ", appointmentDate=" + appointmentDate +
                ", appointmentTime='" + appointmentTime + '\'' +
                ", numberOfPeople=" + numberOfPeople +
                ", remark='" + remark + '\'' +
                ", persons=" + persons +
                '}';
    }
}
