package com.scenic.entity.appointment;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 个人预约实体类
 * 对应数据库表: individual_reservation
 */
public class IndividualReservation {
    private Long id;
    private String reservationNo;
    private Long userId;
    private Long scenicId;
    private Date visitDate;
    private Integer timeSlot;
    private Integer adultCount;
    private Integer childCount;
    private Integer totalCount;
    private Integer status;
    private Date verificationTime;
    private Long operatorId;
    private String verificationLocation;
    private String deviceInfo;
    private String verificationRemark;
    private Date cancelTime;
    private String cancelReason;
    private Integer version;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createBy;
    private Long updateBy;
    
    // 主联系人信息
    private String contactName;     // 主联系人姓名
    private Integer contactIdType;  // 主联系人证件类型
    private String contactIdNumber; // 主联系人证件号码
    private String contactPhone;    // 主联系人联系电话
    
    // 预约人员列表
    private List<IndividualReservationPerson> reservationPersons;

    // 构造函数
    public IndividualReservation() {}

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReservationNo() {
        return reservationNo;
    }

    public void setReservationNo(String reservationNo) {
        this.reservationNo = reservationNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getScenicId() {
        return scenicId;
    }

    public void setScenicId(Long scenicId) {
        this.scenicId = scenicId;
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

    public Integer getAdultCount() {
        return adultCount;
    }

    public void setAdultCount(Integer adultCount) {
        this.adultCount = adultCount;
    }

    public Integer getChildCount() {
        return childCount;
    }

    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getVerificationTime() {
        return verificationTime;
    }

    public void setVerificationTime(Date verificationTime) {
        this.verificationTime = verificationTime;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getVerificationLocation() {
        return verificationLocation;
    }

    public void setVerificationLocation(String verificationLocation) {
        this.verificationLocation = verificationLocation;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getVerificationRemark() {
        return verificationRemark;
    }

    public void setVerificationRemark(String verificationRemark) {
        this.verificationRemark = verificationRemark;
    }

    public Date getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
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

    // 主联系人信息的Getter和Setter方法
    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Integer getContactIdType() {
        return contactIdType;
    }

    public void setContactIdType(Integer contactIdType) {
        this.contactIdType = contactIdType;
    }

    public String getContactIdNumber() {
        return contactIdNumber;
    }

    public void setContactIdNumber(String contactIdNumber) {
        this.contactIdNumber = contactIdNumber;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    // 预约人员列表的Getter和Setter方法
    public List<IndividualReservationPerson> getReservationPersons() {
        return reservationPersons;
    }

    public void setReservationPersons(List<IndividualReservationPerson> reservationPersons) {
        this.reservationPersons = reservationPersons;
    }

    @Override
    public String toString() {
        return "IndividualReservation{" +
                "id=" + id +
                ", reservationNo='" + reservationNo + '\'' +
                ", userId=" + userId +
                ", scenicId=" + scenicId +
                ", visitDate=" + visitDate +
                ", timeSlot=" + timeSlot +
                ", adultCount=" + adultCount +
                ", childCount=" + childCount +
                ", totalCount=" + totalCount +
                ", status=" + status +
                ", verificationTime=" + verificationTime +
                ", operatorId=" + operatorId +
                ", verificationLocation='" + verificationLocation + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", verificationRemark='" + verificationRemark + '\'' +
                ", cancelTime=" + cancelTime +
                ", cancelReason='" + cancelReason + '\'' +
                ", version=" + version +
                ", deleted=" + deleted +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createBy=" + createBy +
                ", updateBy=" + updateBy +
                ", contactName='" + contactName + '\'' +
                ", contactIdType=" + contactIdType +
                ", contactIdNumber='" + contactIdNumber + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", reservationPersons=" + reservationPersons +
                '}';
    }
}