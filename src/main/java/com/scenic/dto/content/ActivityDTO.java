package com.scenic.dto.content;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 活动DTO
 */
public class ActivityDTO {
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
    private Map<String, List<Long>> deletedFileIds; // 要删除的文件ID映射
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime; // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime; // 更新时间
    private Long createBy; // 创建人
    private Long updateBy; // 更新人
    private String publisher; // 发布人姓名
    private Byte reservationPriority; // 预约优先级（1=高于全局规则，0=遵循全局规则）
    private Boolean enabled; // 是否启用

    // 构造函数
    public ActivityDTO() {}

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

    public LocalDate getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDate startTime) {
        this.startTime = startTime;
    }

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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

    public Map<String, List<Long>> getDeletedFileIds() {
        return deletedFileIds;
    }

    public void setDeletedFileIds(Map<String, List<Long>> deletedFileIds) {
        this.deletedFileIds = deletedFileIds;
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

    public Byte getReservationPriority() {
        return reservationPriority;
    }

    public void setReservationPriority(Byte reservationPriority) {
        this.reservationPriority = reservationPriority;
    }
    
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
