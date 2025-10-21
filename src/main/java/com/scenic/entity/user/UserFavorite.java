package com.scenic.entity.user;

import java.time.LocalDateTime;

/**
 * 用户收藏实体类
 */
public class UserFavorite {
    private Long id;
    private Long userId; // 用户ID
    private Long contentId; // 内容ID
    private Integer contentType; // 内容类型：1-收藏，2-喜欢
    private Integer version; // 版本号（乐观锁）
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createBy;
    private Long updateBy;
    private Integer categoryId; // 分类ID

    // 构造函数
    public UserFavorite() {}

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

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Integer getContentType() {
        return contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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
    
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "UserFavorite{" +
                "id=" + id +
                ", userId=" + userId +
                ", contentId=" + contentId +
                ", contentType=" + contentType +
                ", version=" + version +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createBy=" + createBy +
                ", updateBy=" + updateBy +
                ", categoryId=" + categoryId +
                '}';
    }
}