package com.scenic.entity.content;

import java.time.LocalDateTime;

/**
 * 保护区物种实体类
 */
public class ProtectedAreaSpecies {
    private Long id;
    private String name; // 物种名称
    private String scientificName; // 学名
    private String category; // 物种分类
    private String protectionLevel; // 保护级别
    private String description; // 物种描述
    private String habitat; // 栖息地
    private String imageUrl; // 物种图片URL
    private String iconUrl; // 物种图标URL
    private Boolean enabled; // 是否启用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 构造函数
    public ProtectedAreaSpecies() {}

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProtectionLevel() {
        return protectionLevel;
    }

    public void setProtectionLevel(String protectionLevel) {
        this.protectionLevel = protectionLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHabitat() {
        return habitat;
    }

    public void setHabitat(String habitat) {
        this.habitat = habitat;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
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
        return "ProtectedAreaSpecies{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", scientificName='" + scientificName + '\'' +
                ", category='" + category + '\'' +
                ", protectionLevel='" + protectionLevel + '\'' +
                ", description='" + description + '\'' +
                ", habitat='" + habitat + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", enabled=" + enabled +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
