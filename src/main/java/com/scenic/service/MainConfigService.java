package com.scenic.service;

import java.util.Map;

import com.scenic.common.dto.Result;
import com.scenic.entity.MainConfig;

/**
 * 主配置服务接口
 */
public interface MainConfigService {
    
    /**
     * 根据配置名称获取配置
     * @param configName 配置名称
     * @return 配置信息
     */
    Result<MainConfig> getConfigByName(String configName);
    
    /**
     * 保存或更新配置
     * @param configName 配置名称
     * @param configJson 配置JSON内容
     * @param userId 操作用户ID
     * @return 操作结果
     */
    Result<String> saveConfig(String configName, String configJson, Long userId);
    
    /**
     * 批量保存或更新配置
     * @param configs 配置映射
     * @param userId 操作用户ID
     * @return 操作结果
     */
    Result<String> saveConfigs(Map<String, String> configs, Long userId);
    
    /**
     * 获取预约项目配置
     * @return 预约项目配置
     */
    Result<Map<String, Object>> getAppointmentProjectConfig();
    
    /**
     * 保存预约项目配置
     * @param configs 配置映射
     * @param userId 操作用户ID
     * @return 操作结果
     */
    Result<String> saveAppointmentProjectConfig(Map<String, String> configs, Long userId);
    
    /**
     * 获取预约规则配置
     * @return 预约规则配置
     */
    Result<Map<String, Object>> getAppointmentRuleConfig();
    
    /**
     * 保存预约规则配置
     * @param configJson 配置JSON内容
     * @param userId 操作用户ID
     * @return 操作结果
     */
    Result<String> saveAppointmentRuleConfig(String configJson, Long userId);
}
