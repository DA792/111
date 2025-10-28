package com.scenic.controller.system;

import com.scenic.common.dto.Result;
import com.scenic.entity.system.NotificationConfig;
import com.scenic.service.system.NotificationConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知配置控制器
 */
@RestController
@RequestMapping("/api/manage/notification-configs")
public class NotificationConfigController {
    
    @Autowired
    private NotificationConfigService notificationConfigService;
    
    /**
     * 获取所有通知配置
     * @return 通知配置列表
     */
    @GetMapping
    public Result<List<NotificationConfig>> getAllConfigs() {
        return notificationConfigService.getAllConfigs();
    }
    
    /**
     * 根据ID获取通知配置
     * @param id 配置ID
     * @return 通知配置
     */
    @GetMapping("/{id}")
    public Result<NotificationConfig> getConfigById(@PathVariable Long id) {
        return notificationConfigService.getConfigById(id);
    }
    
    /**
     * 根据类型获取通知配置
     * @param type 通知类型
     * @return 通知配置
     */
    @GetMapping("/type/{type}")
    public Result<NotificationConfig> getConfigByType(@PathVariable String type) {
        return notificationConfigService.getConfigByType(type);
    }
    
    /**
     * 保存或更新通知配置
     * @param notificationConfig 通知配置
     * @return 操作结果
     */
    @PostMapping
    public Result<String> saveConfig(@RequestBody NotificationConfig notificationConfig) {
        return notificationConfigService.saveConfig(notificationConfig);
    }
    
    /**
     * 更新通知配置状态
     * @param type 通知类型
     * @param enabled 是否启用
     * @return 操作结果
     */
    @PutMapping("/{type}/status")
    public Result<String> updateConfigStatus(@PathVariable String type, @RequestParam Integer enabled) {
        return notificationConfigService.updateConfigStatus(type, enabled);
    }
    
}