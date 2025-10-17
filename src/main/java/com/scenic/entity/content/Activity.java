package com.scenic.entity.content;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 活动实体类
 */
public class Activity {
    private Long id;
    private String title; // 活动标题（限制150字符）
    private LocalDate startTime; // 活动开始时间
    private LocalDate endTime; // 活动结束时间
    private String suitableCrowd; // 适合人群
    private String location; // 活动地点
    private String price; // 票价
    private String teamLimit; // 报名团队限制
    private String content; // 活动详情内容（富文本HTML格式）
    private List<Long> contentImageIds; // 详情内容中的图片ID数组
    private Long coverImageId; // 封面图片ID
    private Byte status; // 活动状态：0-未结束，1-已结束
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
    private Long createBy; // 创建人
    private Long updateBy; // 更新人
    private Byte reservationPriority; // 预约优先级（1=高于全局规则，0=遵循全局规则）
    private Integer deleted; // 1-删除，0-未删除

    // 构造函数
    public Activity() {}

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    public LocalDate getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDate startTime) {
        this.startTime = startTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    public LocalDate getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDate endTime) {
        this.endTime = endTime;
    }

    public String getSuitableCrowd() {
        return suitableCrowd;
    }

    public void setSuitableCrowd(String suitableCrowd) {
        this.suitableCrowd = suitableCrowd;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTeamLimit() {
        return teamLimit;
    }

    public void setTeamLimit(String teamLimit) {
        this.teamLimit = teamLimit;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Long> getContentImageIds() {
        return contentImageIds;
    }

    public void setContentImageIds(List<Long> contentImageIds) {
        this.contentImageIds = contentImageIds;
    }

    public Long getCoverImageId() {
        return coverImageId;
    }

    public void setCoverImageId(Long coverImageId) {
        this.coverImageId = coverImageId;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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

    public Byte getReservationPriority() {
        return reservationPriority;
    }

    public void setReservationPriority(Byte reservationPriority) {
        this.reservationPriority = reservationPriority;
    }
    
    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", suitableCrowd='" + suitableCrowd + '\'' +
                ", location='" + location + '\'' +
                ", price='" + price + '\'' +
                ", teamLimit='" + teamLimit + '\'' +
                ", content='" + content + '\'' +
                ", contentImageIds=" + contentImageIds +
                ", coverImageId=" + coverImageId +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createBy=" + createBy +
                ", updateBy=" + updateBy +
                ", reservationPriority=" + reservationPriority +
                ", deleted=" + deleted +
                '}';
    }
}