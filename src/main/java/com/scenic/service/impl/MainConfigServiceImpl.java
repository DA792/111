package com.scenic.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scenic.common.dto.Result;
import com.scenic.entity.MainConfig;
import com.scenic.mapper.MainConfigMapper;
import com.scenic.service.MainConfigService;

/**
 * 主配置服务实现类
 */
@Service
public class MainConfigServiceImpl implements MainConfigService {
    
    @Autowired
    private MainConfigMapper mainConfigMapper;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // 配置名称常量
    private static final String APPOINTMENT_PROJECT_CONFIG_NAME = "预约项目开放状态";
    private static final String APPOINTMENT_RULE_CONFIG_NAME = "全局预约规则";
    
    /**
     * 根据配置名称获取配置
     * @param configName 配置名称
     * @return 配置信息
     */
    @Override
    public Result<MainConfig> getConfigByName(String configName) {
        try {
            MainConfig config = mainConfigMapper.selectByConfigName(configName);
            return Result.success(config);
        } catch (Exception e) {
            return Result.error("查询配置失败：" + e.getMessage());
        }
    }
    
    /**
     * 保存或更新配置
     * @param configName 配置名称
     * @param configJson 配置JSON内容
     * @param userId 操作用户ID
     * @return 操作结果
     */
    @Override
    public Result<String> saveConfig(String configName, String configJson, Long userId) {
        try {
            MainConfig existingConfig = mainConfigMapper.selectByConfigName(configName);
            
            MainConfig config = new MainConfig();
            config.setConfigName(configName);
            config.setConfigJson(configJson);
            config.setUpdateTime(LocalDateTime.now());
            config.setUpdateBy(userId);
            
            if (existingConfig == null) {
                // 新增
                config.setCreateTime(LocalDateTime.now());
                config.setCreateBy(userId);
                mainConfigMapper.insert(config);
            } else {
                // 更新
                config.setId(existingConfig.getId());
                mainConfigMapper.updateByConfigName(config);
            }
            return Result.success("配置已保存");
        } catch (Exception e) {
            return Result.error("保存配置失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量保存或更新配置
     * @param configs 配置映射
     * @param userId 操作用户ID
     * @return 操作结果
     */
    @Override
    public Result<String> saveConfigs(Map<String, String> configs, Long userId) {
        try {
            for (Map.Entry<String, String> entry : configs.entrySet()) {
                saveConfig(entry.getKey(), entry.getValue(), userId);
            }
            return Result.success("配置已批量保存");
        } catch (Exception e) {
            return Result.error("批量保存配置失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取预约项目配置
     * @return 预约项目配置
     */
    @Override
    public Result<Map<String, Object>> getAppointmentProjectConfig() {
        try {
            MainConfig config = mainConfigMapper.selectByConfigName(APPOINTMENT_PROJECT_CONFIG_NAME);
            Map<String, Object> result = new HashMap<>();
            
            if (config != null && config.getConfigJson() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> configMap = objectMapper.readValue(config.getConfigJson(), Map.class);
                result.putAll(configMap);
            } else {
                // 返回默认配置
                result.put("individual_reserve_status", 1);
                result.put("team_reserve_status", 1);
                result.put("activity_reserve_status", 1);
            }
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取预约项目配置失败：" + e.getMessage());
        }
    }
    
    /**
     * 保存预约项目配置
     * @param configs 配置映射
     * @param userId 操作用户ID
     * @return 操作结果
     */
    @Override
    public Result<String> saveAppointmentProjectConfig(Map<String, String> configs, Long userId) {
        try {
            // 构造JSON配置
            Map<String, Object> configMap = new HashMap<>();
            configMap.put("individual_reserve_status", 
                configs.getOrDefault("individual_reserve_status", "1"));
            configMap.put("team_reserve_status", 
                configs.getOrDefault("team_reserve_status", "1"));
            configMap.put("activity_reserve_status", 
                configs.getOrDefault("activity_reserve_status", "1"));
            
            String configJson = objectMapper.writeValueAsString(configMap);
            return saveConfig(APPOINTMENT_PROJECT_CONFIG_NAME, configJson, userId);
        } catch (Exception e) {
            return Result.error("保存预约项目配置失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取预约规则配置
     * @return 预约规则配置
     */
    @Override
    public Result<Map<String, Object>> getAppointmentRuleConfig() {
        try {
            MainConfig config = mainConfigMapper.selectByConfigName(APPOINTMENT_RULE_CONFIG_NAME);
            Map<String, Object> result = new HashMap<>();
            
            if (config != null && config.getConfigJson() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> configMap = objectMapper.readValue(config.getConfigJson(), Map.class);
                result.putAll(configMap);
            } else {
                // 返回默认配置
                result.put("advance_reserve_hours", 2);
                result.put("daily_reserve_limit", 1500);
                result.put("same_time_slot_limit", 300);
                result.put("allow_cancel", 1);
                result.put("cancel_deadline_hours", 1);
            }
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取预约规则配置失败：" + e.getMessage());
        }
    }
    
    /**
     * 保存预约规则配置
     * @param configJson 配置JSON内容
     * @param userId 操作用户ID
     * @return 操作结果
     */
    @Override
    public Result<String> saveAppointmentRuleConfig(String configJson, Long userId) {
        try {
            // 验证JSON格式
            objectMapper.readTree(configJson);
            return saveConfig(APPOINTMENT_RULE_CONFIG_NAME, configJson, userId);
        } catch (Exception e) {
            return Result.error("保存预约规则配置失败：" + e.getMessage());
        }
    }
}
