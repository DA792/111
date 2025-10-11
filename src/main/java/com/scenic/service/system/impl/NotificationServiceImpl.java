package com.scenic.service.system.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.system.NotificationDTO;
import com.scenic.entity.system.Notification;
import com.scenic.mapper.system.NotificationMapper;
import com.scenic.service.system.NotificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 */
@Service
public class NotificationServiceImpl implements NotificationService {
    
    @Autowired
    private NotificationMapper notificationMapper;
    
    /**
     * 分页查询通知列表
     * @param page 页码
     * @param size 每页大小
     * @param title 通知标题（可选）
     * @param channel 通知渠道（可选）
     * @param sendStatus 发送状态（可选）
     * @return 通知列表
     */
    @Override
    public Result<PageResult<NotificationDTO>> getNotifications(int page, int size, String title, String channel, Integer sendStatus) {
        try {
            // 使用PageHelper进行分页
            PageHelper.startPage(page, size);
            
            // 查询通知列表
            List<Notification> notifications = notificationMapper.selectByCondition(title, channel, sendStatus);
            
            // 转换为PageInfo
            PageInfo<Notification> pageInfo = new PageInfo<>(notifications);
            
            // 转换为DTO列表
            List<NotificationDTO> notificationDTOs = notifications.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // 构造PageResult
            PageResult<NotificationDTO> pageResult = new PageResult<>();
            pageResult.setTotal(pageInfo.getTotal());
            pageResult.setRecords(notificationDTOs);
            pageResult.setCurrentPage(pageInfo.getPageNum());
            pageResult.setPageSize(pageInfo.getPageSize());
            
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询通知列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取通知详情
     * @param id 通知ID
     * @return 通知详情
     */
    @Override
    public Result<NotificationDTO> getNotificationById(Long id) {
        try {
            Notification notification = notificationMapper.selectById(id);
            if (notification == null) {
                return Result.error("通知不存在");
            }
            return Result.success(convertToDTO(notification));
        } catch (Exception e) {
            return Result.error("查询通知详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建通知
     * @param notificationDTO 通知信息
     * @return 创建结果
     */
    @Override
    public Result<String> createNotification(NotificationDTO notificationDTO) {
        try {
            // 转换为实体类
            Notification notification = convertToEntity(notificationDTO);
            notification.setCreateTime(LocalDateTime.now());
            notification.setUpdateTime(LocalDateTime.now());
            
            // 设置默认发送状态为未发送
            if (notification.getSendStatus() == null) {
                notification.setSendStatus(0);
            }
            
            // 插入数据库
            int result = notificationMapper.insert(notification);
            if (result > 0) {
                return Result.success("通知创建成功");
            } else {
                return Result.error("通知创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新通知
     * @param id 通知ID
     * @param notificationDTO 通知信息
     * @return 更新结果
     */
    @Override
    public Result<String> updateNotification(Long id, NotificationDTO notificationDTO) {
        try {
            // 检查通知是否存在
            Notification existingNotification = notificationMapper.selectById(id);
            if (existingNotification == null) {
                return Result.error("通知不存在");
            }
            
            // 转换为实体类
            Notification notification = convertToEntity(notificationDTO);
            notification.setId(id);
            notification.setUpdateTime(LocalDateTime.now());
            
            // 更新数据库
            int result = notificationMapper.update(notification);
            if (result > 0) {
                return Result.success("通知更新成功");
            } else {
                return Result.error("通知更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除通知
     * @param id 通知ID
     * @return 删除结果
     */
    @Override
    public Result<String> deleteNotification(Long id) {
        try {
            // 检查通知是否存在
            Notification existingNotification = notificationMapper.selectById(id);
            if (existingNotification == null) {
                return Result.error("通知不存在");
            }
            
            // 删除数据库记录
            int result = notificationMapper.deleteById(id);
            if (result > 0) {
                return Result.success("通知删除成功");
            } else {
                return Result.error("通知删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送通知
     * @param id 通知ID
     * @return 发送结果
     */
    @Override
    public Result<String> sendNotification(Long id) {
        try {
            // 检查通知是否存在
            Notification existingNotification = notificationMapper.selectById(id);
            if (existingNotification == null) {
                return Result.error("通知不存在");
            }
            
            // 调用发送接口
            Result<String> sendResult = sendNotificationByChannel(existingNotification);
            if (sendResult.getCode() != 200) {
                return sendResult;
            }
            
            // 更新发送状态
            existingNotification.setSendStatus(1);
            existingNotification.setSendTime(LocalDateTime.now());
            existingNotification.setUpdateTime(LocalDateTime.now());
            
            // 更新数据库
            int result = notificationMapper.update(existingNotification);
            if (result > 0) {
                return Result.success("通知发送成功");
            } else {
                return Result.error("通知发送失败");
            }
        } catch (Exception e) {
            return Result.error("发送通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据渠道发送通知
     * @param notification 通知实体
     * @return 发送结果
     */
    @Override
    public Result<String> sendNotificationByChannel(Notification notification) {
        try {
            // 根据不同的渠道调用不同的发送接口
            switch (notification.getChannel()) {
                case "短信":
                    // 调用短信发送接口
                    return sendSmsNotification(notification);
                case "邮件":
                    // 调用邮件发送接口
                    return sendEmailNotification(notification);
                case "小程序":
                    // 调用小程序推送接口
                    return sendMiniappNotification(notification);
                case "服务号":
                    // 调用服务号推送接口
                    return sendServiceNotification(notification);
                default:
                    return Result.error("不支持的通知渠道");
            }
        } catch (Exception e) {
            // 更新发送状态为发送失败
            notification.setSendStatus(2);
            notification.setUpdateTime(LocalDateTime.now());
            notificationMapper.update(notification);
            return Result.error("发送通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送短信通知
     * @param notification 通知实体
     * @return 发送结果
     */
    private Result<String> sendSmsNotification(Notification notification) {
        // TODO: 实现短信发送逻辑
        // 这里应该调用短信服务提供商的API
        return Result.success("短信发送成功");
    }
    
    /**
     * 发送邮件通知
     * @param notification 通知实体
     * @return 发送结果
     */
    private Result<String> sendEmailNotification(Notification notification) {
        // TODO: 实现邮件发送逻辑
        // 这里应该调用邮件服务提供商的API
        return Result.success("邮件发送成功");
    }
    
    /**
     * 发送小程序通知
     * @param notification 通知实体
     * @return 发送结果
     */
    private Result<String> sendMiniappNotification(Notification notification) {
        // TODO: 实现小程序推送逻辑
        // 这里应该调用微信小程序推送API
        return Result.success("小程序推送成功");
    }
    
    /**
     * 发送服务号通知
     * @param notification 通知实体
     * @return 发送结果
     */
    private Result<String> sendServiceNotification(Notification notification) {
        // TODO: 实现服务号推送逻辑
        // 这里应该调用微信服务号推送API
        return Result.success("服务号推送成功");
    }
    
    /**
     * 将实体类转换为DTO
     * @param notification 通知实体
     * @return 通知DTO
     */
    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        return notificationDTO;
    }
    
    /**
     * 将DTO转换为实体类
     * @param notificationDTO 通知DTO
     * @return 通知实体
     */
    private Notification convertToEntity(NotificationDTO notificationDTO) {
        Notification notification = new Notification();
        BeanUtils.copyProperties(notificationDTO, notification);
        return notification;
    }
}
