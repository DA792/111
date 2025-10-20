package com.scenic.dto.appointment;

import java.util.Date;

/**
 * 预约数据DTO
 */
public class AppointmentDataDTO {
    
    /**
     * 预约日期
     */
    private Date reserveDate;
    
    /**
     * 预约状态：不开放/已满/已预约 X/Y
     */
    private String reserveStatus;
    
    /**
     * 活动名称（仅活动预约数据使用）
     */
    private String activityName;
    
    /**
     * 活动预约上限（仅活动预约数据使用）
     */
    private Integer activityLimit;
    
    /**
     * 已预约人数
     */
    private Integer bookedCount;
    
    /**
     * 预约上限
     */
    private Integer totalLimit;
    
    /**
     * 是否开放
     */
    private Boolean isOpen;

    public Date getReserveDate() {
        return reserveDate;
    }

    public void setReserveDate(Date reserveDate) {
        this.reserveDate = reserveDate;
    }

    public String getReserveStatus() {
        return reserveStatus;
    }

    public void setReserveStatus(String reserveStatus) {
        this.reserveStatus = reserveStatus;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Integer getActivityLimit() {
        return activityLimit;
    }

    public void setActivityLimit(Integer activityLimit) {
        this.activityLimit = activityLimit;
    }

    public Integer getBookedCount() {
        return bookedCount;
    }

    public void setBookedCount(Integer bookedCount) {
        this.bookedCount = bookedCount;
    }

    public Integer getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(Integer totalLimit) {
        this.totalLimit = totalLimit;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }
}
