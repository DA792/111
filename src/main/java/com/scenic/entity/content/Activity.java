package com.scenic.entity.content;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * 活动实体类
 */
public class Activity {
    private Long id;
    private String title; // 活动标题
    private String content; // 活动详情
    private String location; // 活动地点
    private String suitableCrowd; // 适用人群
    private Boolean enabled; // 是否启用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    // 添加startTime和endTime字段
    private LocalDate startTime;
    private LocalDate endTime;
    private String teamLimit; // 团队人数上限

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSuitableCrowd() {
        return suitableCrowd;
    }

    public void setSuitableCrowd(String suitableCrowd) {
        this.suitableCrowd = suitableCrowd;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

    public String getTeamLimit() {
        return teamLimit;
    }

    public void setTeamLimit(String teamLimit) {
        this.teamLimit = teamLimit;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", location='" + location + '\'' +
                ", suitableCrowd='" + suitableCrowd + '\'' +
                ", enabled=" + enabled +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", teamLimit='" + teamLimit + '\'' +
                '}';
    }
}
