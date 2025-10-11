package com.scenic.entity.content;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 保护区大事件实体类
 */
public class ProtectedAreaEvent {
    private Long id;
    private String title; // 大事件标题
    private String summary; // 大事件摘要
    private String content; // 大事件详细内容
    private LocalDate eventDate; // 大事件日期
    private Integer eventYear; // 大事件年份（由数据库生成）
    private Integer eventMonth; // 大事件月份（由数据库生成）
    private String coverImageUrl; // 封面图片URL
    private String coverVideoUrl; // 封面视频URL
    private String location; // 事件发生地点
    private Integer viewCount; // 浏览次数
    private Boolean enabled; // 是否启用
    private Integer sortOrder; // 排序顺序
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 构造函数
    public ProtectedAreaEvent() {}

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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public Integer getEventYear() {
        return eventYear;
    }

    public void setEventYear(Integer eventYear) {
        this.eventYear = eventYear;
    }

    public Integer getEventMonth() {
        return eventMonth;
    }

    public void setEventMonth(Integer eventMonth) {
        this.eventMonth = eventMonth;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getCoverVideoUrl() {
        return coverVideoUrl;
    }

    public void setCoverVideoUrl(String coverVideoUrl) {
        this.coverVideoUrl = coverVideoUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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


    @Override
    public String toString() {
        return "ProtectedAreaEvent{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", content='" + content + '\'' +
                ", eventDate=" + eventDate +
                ", eventYear=" + eventYear +
                ", eventMonth=" + eventMonth +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                ", coverVideoUrl='" + coverVideoUrl + '\'' +
                ", location='" + location + '\'' +
                ", viewCount=" + viewCount +
                ", enabled=" + enabled +
                ", sortOrder=" + sortOrder +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
