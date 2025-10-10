package com.scenic.dto.system;

import java.time.LocalDateTime;

/**
 * 通知DTO类
 */
public class NotificationDTO {
    /**
     * 通知ID
     */
    private Long id;
    
    /**
     * 通知标题
     */
    private String title;
    
    /**
     * 通知内容
     */
    private String content;
    
    /**
     * 通知渠道（短信/邮件/小程序/服务号）
     */
    private String channel;
    
    /**
     * 接收人ID（用户ID或角色ID）
     */
    private Long receiverId;
    
    /**
     * 接收人类型（用户/角色）
     */
    private String receiverType;
    
    /**
     * 发送状态（0-未发送，1-已发送，2-发送失败）
     */
    private Integer sendStatus;
    
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
    
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
    
    public String getChannel() {
        return channel;
    }
    
    public void setChannel(String channel) {
        this.channel = channel;
    }
    
    public Long getReceiverId() {
        return receiverId;
    }
    
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
    
    public String getReceiverType() {
        return receiverType;
    }
    
    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType;
    }
    
    public Integer getSendStatus() {
        return sendStatus;
    }
    
    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }
    
    public LocalDateTime getSendTime() {
        return sendTime;
    }
    
    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
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
