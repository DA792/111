package com.scenic.entity.user;

import java.time.LocalDateTime;

/**
 * 用户消息实体类
 * 用于"我的消息"功能
 */
public class UserMessage {
    private Long id;
    private Long userId; // 用户ID
    private Long relatedUserId; // 相关用户ID
    private String relatedUserName; // 相关用户名
    private String type; // 消息类型（如：like_my_photo, my_like）
    private String content; // 消息内容
    private Long relatedId; // 相关ID（如：照片ID）
    private Boolean isRead; // 是否已读
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 构造函数
    public UserMessage() {}

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

    public Long getRelatedUserId() {
        return relatedUserId;
    }

    public void setRelatedUserId(Long relatedUserId) {
        this.relatedUserId = relatedUserId;
    }

    public String getRelatedUserName() {
        return relatedUserName;
    }

    public void setRelatedUserName(String relatedUserName) {
        this.relatedUserName = relatedUserName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(Long relatedId) {
        this.relatedId = relatedId;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
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
        return "UserMessage{" +
                "id=" + id +
                ", userId=" + userId +
                ", relatedUserId=" + relatedUserId +
                ", relatedUserName='" + relatedUserName + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", relatedId=" + relatedId +
                ", isRead=" + isRead +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
