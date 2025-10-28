package com.scenic.entity.system;

import java.time.LocalDateTime;

/**
 * 通知配置实体类
 */
public class NotificationConfig {
    /**
     * 配置ID
     */
    private Long id;
    
    /**
     * 通知类型（email-邮件, sms-短信, wechat-微信小程序, official_account-微信服务号）
     */
    private String type;
    
    /**
     * 通知名称
     */
    private String name;
    
    /**
     * 是否启用（0-禁用，1-启用）
     */
    private Integer enabled;
    
    /**
     * 配置数据（JSON格式，存储各通知类型的特定配置）
     */
    private String configData;
    
    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdate;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    // getter和setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }
    
    public String getConfigData() {
        return configData;
    }
    
    public void setConfigData(String configData) {
        this.configData = configData;
    }
    
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
    
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
