package com.scenic.service.system;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.system.NotificationDTO;
import com.scenic.entity.system.Notification;

import java.util.List;

/**
 * 通知服务接口
 */
public interface NotificationService {
    
    /**
     * 分页查询通知列表
     * @param page 页码
     * @param size 每页大小
     * @param title 通知标题（可选）
     * @param channel 通知渠道（可选）
     * @param sendStatus 发送状态（可选）
     * @return 通知列表
     */
    Result<PageResult<NotificationDTO>> getNotifications(int page, int size, String title, String channel, Integer sendStatus);
    
    /**
     * 根据ID获取通知详情
     * @param id 通知ID
     * @return 通知详情
     */
    Result<NotificationDTO> getNotificationById(Long id);
    
    /**
     * 创建通知
     * @param notificationDTO 通知信息
     * @return 创建结果
     */
    Result<String> createNotification(NotificationDTO notificationDTO);
    
    /**
     * 更新通知
     * @param id 通知ID
     * @param notificationDTO 通知信息
     * @return 更新结果
     */
    Result<String> updateNotification(Long id, NotificationDTO notificationDTO);
    
    /**
     * 删除通知
     * @param id 通知ID
     * @return 删除结果
     */
    Result<String> deleteNotification(Long id);
    
    /**
     * 发送通知
     * @param id 通知ID
     * @return 发送结果
     */
    Result<String> sendNotification(Long id);
    
    /**
     * 根据渠道发送通知
     * @param notification 通知实体
     * @return 发送结果
     */
    Result<String> sendNotificationByChannel(Notification notification);
}
