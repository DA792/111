package com.scenic.dto.appointment;

import java.time.LocalDateTime;
import java.util.List;

import com.scenic.entity.appointment.Appointment;
import com.scenic.entity.appointment.AppointmentPerson;

/**
 * 预约响应DTO类
 * 用于向客户端返回预约信息，包含脱敏数据
 */
public class AppointmentResponseDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userPhone; // 脱敏后的电话号码
    private Long scenicSpotId;
    private String scenicSpotName;
    private LocalDateTime appointmentTime;
    private Integer numberOfPeople;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<AppointmentPerson> persons; // 预约人员列表

    // 构造函数
    public AppointmentResponseDTO() {}

    // 从Appointment实体创建AppointmentResponseDTO
    public AppointmentResponseDTO(Appointment appointment) {
        this.id = appointment.getId();
        this.userId = appointment.getUserId();
        this.userName = appointment.getUserName();
        this.userPhone = maskPhone(appointment.getUserPhone()); // 脱敏处理
        this.scenicSpotId = appointment.getScenicSpotId();
        this.scenicSpotName = appointment.getScenicSpotName();
        this.appointmentTime = appointment.getAppointmentTime();
        this.numberOfPeople = appointment.getNumberOfPeople();
        this.status = appointment.getStatus();
        this.remark = appointment.getRemark();
        this.createTime = appointment.getCreateTime();
        this.updateTime = appointment.getUpdateTime();
    }

    // 电话号码脱敏处理
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
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

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public List<AppointmentPerson> getPersons() {
        return persons;
    }

    public void setPersons(List<AppointmentPerson> persons) {
        this.persons = persons;
    }

    @Override
    public String toString() {
        return "AppointmentResponseDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", scenicSpotId=" + scenicSpotId +
                ", scenicSpotName='" + scenicSpotName + '\'' +
                ", appointmentTime=" + appointmentTime +
                ", numberOfPeople=" + numberOfPeople +
                ", status='" + status + '\'' +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", persons=" + persons +
                '}';
    }
}
