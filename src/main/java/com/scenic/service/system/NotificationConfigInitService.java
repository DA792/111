package com.scenic.service.system;

import com.scenic.entity.system.NotificationConfig;
import com.scenic.mapper.system.NotificationConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 通知配置初始化服务
 */
@Service
public class NotificationConfigInitService {
    
    @Autowired
    private NotificationConfigMapper notificationConfigMapper;
    
    /**
     * 应用启动时初始化通知配置
     */
    @PostConstruct
    public void initNotificationConfigs() {
        // 检查是否已存在配置数据
        List<NotificationConfig> existingConfigs = notificationConfigMapper.selectAll();
        
        // 如果没有配置数据，则初始化默认配置
        if (existingConfigs.isEmpty()) {
            initDefaultConfigs();
        }
    }
    
    /**
     * 初始化默认通知配置
     */
    private void initDefaultConfigs() {
        List<NotificationConfig> defaultConfigs = Arrays.asList(
            createConfig("sms", "短信通知", 0),
            createConfig("email", "邮件通知", 0),
            createConfig("wechat", "微信小程序", 0),
            createConfig("official_account", "微信服务号", 0)
        );
        
        // 批量插入默认配置
        for (NotificationConfig config : defaultConfigs) {
            notificationConfigMapper.insert(config);
        }
    }
    
    /**
     * 创建通知配置对象
     * @param type 通知类型
     * @param name 通知名称
     * @param enabled 是否启用
     * @return 通知配置对象
     */
    private NotificationConfig createConfig(String type, String name, Integer enabled) {
        NotificationConfig config = new NotificationConfig();
        config.setType(type);
        config.setName(name);
        config.setEnabled(enabled);
        config.setConfigData(null);
        config.setLastUpdate(LocalDateTime.now());
        config.setCreateTime(LocalDateTime.now());
        return config;
    }
}
