package com.scenic.schedule;

import com.scenic.entity.system.Notification;
import com.scenic.entity.user.User;
import com.scenic.mapper.system.NotificationMapper;
import com.scenic.mapper.user.UserMapper;
import com.scenic.service.system.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时通知发送任务
 * 定时扫描待发送的通知，通过4种方式（短信、邮件、小程序、服务号）发送给所有用户
 */
@Component
public class NotificationScheduleTask {
    
    private static final Logger log = LoggerFactory.getLogger(NotificationScheduleTask.class);
    
    @Autowired
    private NotificationMapper notificationMapper;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 定时扫描并发送待发送的通知
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void sendScheduledNotifications() {
        try {
            log.info("开始执行定时通知发送任务");
            
            // 查询所有未发送的通知（sendStatus = 0）
            List<Notification> pendingNotifications = notificationMapper.selectBySendStatus(0);
            
            if (pendingNotifications.isEmpty()) {
                log.info("没有待发送的通知");
                return;
            }
            
            log.info("找到 {} 条待发送的通知", pendingNotifications.size());
            
            for (Notification notification : pendingNotifications) {
                try {
                    // 根据通知的接收人类型决定发送范围
                    if (notification.getReceiverType() != null && "all".equalsIgnoreCase(notification.getReceiverType())) {
                        // 发送给所有用户
                        sendToAllUsers(notification);
                    } else if (notification.getReceiverId() != null) {
                        // 发送给指定用户
                        sendToSpecificUser(notification, notification.getReceiverId());
                    }
                    
                    // 更新发送状态
                    notification.setSendStatus(1);
                    notification.setSendTime(LocalDateTime.now());
                    notification.setUpdateTime(LocalDateTime.now());
                    notificationMapper.update(notification);
                    
                    log.info("通知ID {} 发送完成", notification.getId());
                } catch (Exception e) {
                    log.error("通知ID {} 发送失败: {}", notification.getId(), e.getMessage(), e);
                    
                    // 更新发送状态为失败
                    notification.setSendStatus(2);
                    notification.setUpdateTime(LocalDateTime.now());
                    notificationMapper.update(notification);
                }
            }
            
            log.info("定时通知发送任务执行完成");
        } catch (Exception e) {
            log.error("定时通知发送任务执行异常", e);
        }
    }
    
    /**
     * 发送通知给所有用户
     * @param notification 通知实体
     */
    private void sendToAllUsers(Notification notification) {
        try {
            log.info("开始发送通知给所有用户，通知ID: {}, 渠道: {}", notification.getId(), notification.getChannel());
            
            // 查询所有启用状态的普通用户（user_type = 1, status = 1, deleted = 0）
            List<User> allUsers = userMapper.selectAllActiveUsers();
            
            if (allUsers.isEmpty()) {
                log.warn("没有找到可用的用户");
                return;
            }
            
            log.info("找到 {} 个用户，开始批量发送", allUsers.size());
            
            int successCount = 0;
            int failCount = 0;
            
            // 批量发送通知
            for (User user : allUsers) {
                try {
                    // 创建针对该用户的通知副本
                    Notification userNotification = createUserNotification(notification, user);
                    
                    // 根据渠道发送通知
                    notificationService.sendNotificationByChannel(userNotification);
                    
                    successCount++;
                    
                    // 添加小延迟，避免请求过于频繁
                    Thread.sleep(100);
                } catch (Exception e) {
                    failCount++;
                    log.error("向用户ID {} 发送通知失败: {}", user.getId(), e.getMessage());
                }
            }
            
            log.info("通知发送完成，成功: {}, 失败: {}", successCount, failCount);
        } catch (Exception e) {
            log.error("批量发送通知给所有用户失败", e);
            throw e;
        }
    }
    
    /**
     * 发送通知给指定用户
     * @param notification 通知实体
     * @param userId 用户ID
     */
    private void sendToSpecificUser(Notification notification, Long userId) {
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                log.warn("用户ID {} 不存在", userId);
                return;
            }
            
            Notification userNotification = createUserNotification(notification, user);
            notificationService.sendNotificationByChannel(userNotification);
            
            log.info("通知已发送给用户ID {}", userId);
        } catch (Exception e) {
            log.error("向用户ID {} 发送通知失败", userId, e);
            throw e;
        }
    }
    
    /**
     * 创建针对特定用户的通知副本
     * @param notification 原始通知
     * @param user 用户信息
     * @return 用户通知副本
     */
    private Notification createUserNotification(Notification notification, User user) {
        Notification userNotification = new Notification();
        userNotification.setTitle(notification.getTitle());
        userNotification.setContent(notification.getContent());
        userNotification.setChannel(notification.getChannel());
        userNotification.setReceiverId(user.getId());
        userNotification.setReceiverType("user");
        userNotification.setSendStatus(0);
        userNotification.setCreateTime(LocalDateTime.now());
        userNotification.setUpdateTime(LocalDateTime.now());
        return userNotification;
    }
}

