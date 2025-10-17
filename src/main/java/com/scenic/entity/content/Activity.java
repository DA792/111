package com.scenic.entity.content;
import java.time.LocalDateTime;

/**
 * 活动实体类
 */
public class Activity {
    private Long id;
    private String title; // 活动标题
    private String content; // 活动详情
    private String location; // 活动地点
    private Boolean enabled; // 是否启用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

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

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", location='" + location + '\'' +
                ", enabled=" + enabled +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
