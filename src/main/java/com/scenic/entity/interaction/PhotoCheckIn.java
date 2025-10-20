package com.scenic.entity.interaction;

import java.time.LocalDateTime;
import javax.persistence.*;

@Entity
@Table(name = "photo_checkin")
public class PhotoCheckIn {

    @Id
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", length = 100)
    private String userName;

    @Column(name = "user_avatar", length = 1024)
    private String userAvatar;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "content", length = 1000)
    private String content;

    @Column(name = "category_id")
    private Long categoryId;
    
    @Transient
    private String categoryName;

    @Column(name = "like_count", nullable = false, columnDefinition = "int default 0")
    private Integer likeCount = 0;

    @Column(name = "view_count", nullable = false, columnDefinition = "int default 0")
    private Integer viewCount = 0;

    @Column(name = "status", nullable = false, columnDefinition = "tinyint default 1")
    private Integer status = 1;

    @Version
    @Column(name = "version", nullable = false, columnDefinition = "int default 0")
    private Integer version = 0;

    @Column(name = "deleted", nullable = false, columnDefinition = "tinyint default 0")
    private Boolean deleted = false;

    @Column(name = "create_time", nullable = false, updatable = false, columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false, columnDefinition = "datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updateTime;

    @Column(name = "create_by")
    private Long createBy;

    @Column(name = "update_by")
    private Long updateBy;

    @Column(name = "photo_id", columnDefinition = "bigint COMMENT '照片ID'")
    private Long photoId;

    // Getters and Setters
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
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

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    // 实体生命周期回调（可选）
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}