package com.scenic.dto.system;

import java.time.LocalDateTime;

/**
 * 主题设置DTO类
 */
public class ThemeSettingDTO {
    /**
     * 主题设置ID
     */
    private Long id;
    
    /**
     * 主题名称
     */
    private String themeName;
    
    /**
     * 主题色彩配置（JSON格式）
     */
    private String colorConfig;
    
    /**
     * 是否为默认主题（0-否，1-是）
     */
    private Integer isDefault;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // getter和setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getThemeName() {
        return themeName;
    }
    
    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }
    
    public String getColorConfig() {
        return colorConfig;
    }
    
    public void setColorConfig(String colorConfig) {
        this.colorConfig = colorConfig;
    }
    
    public Integer getIsDefault() {
        return isDefault;
    }
    
    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
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
}
