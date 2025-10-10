package com.scenic.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.system.NotificationDTO;
import com.scenic.service.system.NotificationService;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/api/manage/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * 分页查询通知列表
     * @param page 页码
     * @param size 每页大小
     * @param title 通知标题（可选）
     * @param channel 通知渠道（可选）
     * @param sendStatus 发送状态（可选）
     * @return 通知列表
     */
    @GetMapping
    public Result<PageResult<NotificationDTO>> getNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) Integer sendStatus) {
        return notificationService.getNotifications(page, size, title, channel, sendStatus);
    }
    
    /**
     * 根据ID获取通知详情
     * @param id 通知ID
     * @return 通知详情
     */
    @GetMapping("/{id}")
    public Result<NotificationDTO> getNotificationById(@PathVariable Long id) {
        return notificationService.getNotificationById(id);
    }
    
    /**
     * 创建通知
     * @param notificationDTO 通知信息
     * @return 创建结果
     */
    @PostMapping
    public Result<String> createNotification(@RequestBody NotificationDTO notificationDTO) {
        return notificationService.createNotification(notificationDTO);
    }
    
    /**
     * 更新通知
     * @param id 通知ID
     * @param notificationDTO 通知信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<String> updateNotification(@PathVariable Long id, @RequestBody NotificationDTO notificationDTO) {
        return notificationService.updateNotification(id, notificationDTO);
    }
    
    /**
     * 删除通知
     * @param id 通知ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteNotification(@PathVariable Long id) {
        return notificationService.deleteNotification(id);
    }
    
    /**
     * 发送通知
     * @param id 通知ID
     * @return 发送结果
     */
    @PostMapping("/{id}/send")
    public Result<String> sendNotification(@PathVariable Long id) {
        return notificationService.sendNotification(id);
    }
}
