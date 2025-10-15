package com.scenic.entity.appointment;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 个人预约人员实体类
 * 对应数据库表: individual_reservation_person
 */
public class IndividualReservationPerson {
    private Long id;
    private Long reservationId;
    private String name;
    private Integer idType;
    private String idNumber;
    private String phone;
    private Integer personType;
    private Integer isContact;
    private Date visitDate;
    private Integer timeSlot;
    private Integer version;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createBy;
    private Long updateBy;

    // 构造函数
    public IndividualReservationPerson() {}

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIdType() {
        return idType;
    }

    public void setIdType(Integer idType) {
        this.idType = idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getPersonType() {
        return personType;
    }

    public void setPersonType(Integer personType) {
        this.personType = personType;
    }

    public Integer getIsContact() {
        return isContact;
    }

    public void setIsContact(Integer isContact) {
        this.isContact = isContact;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public Integer getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(Integer timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
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

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    @Override
    public String toString() {
        return "IndividualReservationPerson{" +
                "id=" + id +
                ", reservationId=" + reservationId +
                ", name='" + name + '\'' +
                ", idType=" + idType +
                ", idNumber='" + idNumber + '\'' +
                ", phone='" + phone + '\'' +
                ", personType=" + personType +
                ", isContact=" + isContact +
                ", visitDate=" + visitDate +
                ", timeSlot=" + timeSlot +
                ", version=" + version +
                ", deleted=" + deleted +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createBy=" + createBy +
                ", updateBy=" + updateBy +
                '}';
    }
}