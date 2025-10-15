package com.scenic.entity.appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 公园开放时间实体类
 */
public class ParkOpenTime {
    private Long id;
    private LocalDate configDate;
    private Boolean isClosed;
    private Integer dayType;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createBy;
    private Long updateBy;

    // 构造函数
    public ParkOpenTime() {}

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getConfigDate() {
        return configDate;
    }

    public void setConfigDate(LocalDate configDate) {
        this.configDate = configDate;
    }

    public Boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }

    public Integer getDayType() {
        return dayType;
    }

    public void setDayType(Integer dayType) {
        this.dayType = dayType;
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
        return "ParkOpenTime{" +
                "id=" + id +
                ", configDate=" + configDate +
                ", isClosed=" + isClosed +
                ", dayType=" + dayType +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createBy=" + createBy +
                ", updateBy=" + updateBy +
                '}';
    }
}
