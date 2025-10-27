package com.scenic.service.system.impl;

import com.scenic.common.dto.Result;
import com.scenic.entity.system.NotificationConfig;
import com.scenic.mapper.system.NotificationConfigMapper;
import com.scenic.service.system.NotificationConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知配置服务实现类
 */
@Service
public class NotificationConfigServiceImpl implements NotificationConfigService {
    
    @Autowired
    private NotificationConfigMapper notificationConfigMapper;
    
    /**
     * 获取所有通知配置
     * @return 通知配置列表
     */
    @Override
    public Result<List<NotificationConfig>> getAllConfigs() {
        try {
            List<NotificationConfig> configs = notificationConfigMapper.selectAll();
            return Result.success(configs);
        } catch (Exception e) {
            return Result.error("查询通知配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取通知配置
     * @param id 配置ID
     * @return 通知配置
     */
    @Override
    public Result<NotificationConfig> getConfigById(Long id) {
        try {
            NotificationConfig config = notificationConfigMapper.selectById(id);
            if (config == null) {
                return Result.error("通知配置不存在");
            }
            return Result.success(config);
        } catch (Exception e) {
            return Result.error("查询通知配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据类型获取通知配置
     * @param type 通知类型
     * @return 通知配置
     */
    @Override
    public Result<NotificationConfig> getConfigByType(String type) {
        try {
            NotificationConfig config = notificationConfigMapper.selectByType(type);
            if (config == null) {
                return Result.error("通知配置不存在");
            }
            return Result.success(config);
        } catch (Exception e) {
            return Result.error("查询通知配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存或更新通知配置
     * @param notificationConfig 通知配置
     * @return 操作结果
     */
    @Override
    public Result<String> saveConfig(NotificationConfig notificationConfig) {
        try {
            // 检查配置是否存在
            NotificationConfig existingConfig = notificationConfigMapper.selectByType(notificationConfig.getType());
            
            // 设置更新时间
            notificationConfig.setLastUpdate(LocalDateTime.now());
            
            if (existingConfig == null) {
                // 新增配置
                notificationConfig.setCreateTime(LocalDateTime.now());
                int result = notificationConfigMapper.insert(notificationConfig);
                if (result > 0) {
                    return Result.success("通知配置创建成功");
                } else {
                    return Result.error("通知配置创建失败");
                }
            } else {
                // 更新配置
                notificationConfig.setId(existingConfig.getId());
                int result = notificationConfigMapper.update(notificationConfig);
                if (result > 0) {
                    return Result.success("通知配置更新成功");
                } else {
                    return Result.error("通知配置更新失败");
                }
            }
        } catch (Exception e) {
            return Result.error("保存通知配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新通知配置状态
     * @param type 通知类型
     * @param enabled 是否启用
     * @return 操作结果
     */
    @Override
    public Result<String> updateConfigStatus(String type, Integer enabled) {
        try {
            // 检查配置是否存在
            NotificationConfig existingConfig = notificationConfigMapper.selectByType(type);
            if (existingConfig == null) {
                return Result.error("通知配置不存在");
            }
            
            // 更新状态
            existingConfig.setEnabled(enabled);
            existingConfig.setLastUpdate(LocalDateTime.now());
            
            int result = notificationConfigMapper.update(existingConfig);
            if (result > 0) {
                return Result.success("通知配置状态更新成功");
            } else {
                return Result.error("通知配置状态更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新通知配置状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除通知配置
     * @param id 配置ID
     * @return 操作结果
     */
    @Override
    public Result<String> deleteConfig(Long id) {
        try {
            // 检查配置是否存在
            NotificationConfig existingConfig = notificationConfigMapper.selectById(id);
            if (existingConfig == null) {
                return Result.error("通知配置不存在");
            }
            
            // 删除配置
            int result = notificationConfigMapper.deleteById(id);
            if (result > 0) {
                return Result.success("通知配置删除成功");
            } else {
                return Result.error("通知配置删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除通知配置失败: " + e.getMessage());
        }
    }
}
