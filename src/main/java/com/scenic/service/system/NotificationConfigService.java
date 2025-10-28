package com.scenic.service.system;

import com.scenic.common.dto.Result;
import com.scenic.entity.system.NotificationConfig;

import java.util.List;

/**
 * 通知配置服务接口
 */
public interface NotificationConfigService {
    /**
     * 获取所有通知配置
     * @return 通知配置列表
     */
    Result<List<NotificationConfig>> getAllConfigs();
    
    /**
     * 根据ID获取通知配置
     * @param id 配置ID
     * @return 通知配置
     */
    Result<NotificationConfig> getConfigById(Long id);
    
    /**
     * 根据类型获取通知配置
     * @param type 通知类型
     * @return 通知配置
     */
    Result<NotificationConfig> getConfigByType(String type);
    
    /**
     * 保存或更新通知配置
     * @param notificationConfig 通知配置
     * @return 操作结果
     */
    Result<String> saveConfig(NotificationConfig notificationConfig);
    
    /**
     * 更新通知配置状态
     * @param type 通知类型
     * @param enabled 是否启用
     * @return 操作结果
     */
    Result<String> updateConfigStatus(String type, Integer enabled);
    
    /**
     * 删除通知配置
     * @param id 配置ID
     * @return 操作结果
     */
    Result<String> deleteConfig(Long id);
}
