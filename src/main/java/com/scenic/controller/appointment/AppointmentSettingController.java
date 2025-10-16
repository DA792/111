package com.scenic.controller.appointment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import com.scenic.common.dto.Result;
import com.scenic.entity.appointment.AppointmentSetting;
import com.scenic.service.appointment.AppointmentSettingService;
import com.scenic.service.MainConfigService;

/**
 * 预约设置控制器
 * 提供小程序端和管理后台端的API接口
 */
@RestController
@RequestMapping("/api")
public class AppointmentSettingController {
    
    // 小程序端API接口前缀
    private static final String MINIAPP_PREFIX = "/uniapp";
    
    // 管理后台端API接口前缀
    private static final String ADMIN_PREFIX = "/manage";
    
    @Autowired
    private AppointmentSettingService appointmentSettingService;
    
    @Autowired
    private MainConfigService mainConfigService;
    
    /**
     * 小程序端 - 获取预约所需的关键设置
     * @return 预约关键设置
     */
    @GetMapping(MINIAPP_PREFIX + "/appointment-settings/key-settings")
    public Result<Map<String, Object>> getKeySettings() {
        // 获取预约所需的关键设置
        List<String> keys = Arrays.asList(
            "max_people_per_appointment",
            "allow_cancel_hours"
        );
        
        Result<Map<String, AppointmentSetting>> settingsResult = appointmentSettingService.getSettingsByKeys(keys);
        if (settingsResult.getCode() != 200) {
            return Result.error("获取预约设置失败");
        }
        
        // 转换数据格式
        Map<String, Object> resultMap = new java.util.HashMap<>();
        Map<String, AppointmentSetting> settingsMap = settingsResult.getData();
        if (settingsMap != null) {
            for (Map.Entry<String, AppointmentSetting> entry : settingsMap.entrySet()) {
                resultMap.put(entry.getKey(), entry.getValue());
            }
        }
        
        return Result.success(resultMap);
    }
    
    /**
     * 小程序端 - 获取指定日期的预约统计信息
     * @param date 日期
     * @return 预约统计信息
     */
    @GetMapping(MINIAPP_PREFIX + "/appointment-settings/daily-stats")
    public Result<Map<String, Object>> getDailyAppointmentStatsForMiniapp(@RequestParam String date) {
        return appointmentSettingService.getDailyAppointmentStats(date);
    }
    
    /**
     * 管理后台端 - 获取所有预约设置
     * @return 预约设置列表
     */
    @GetMapping(ADMIN_PREFIX + "/appointment-settings")
    public Result<List<AppointmentSetting>> getAllSettingsForAdmin() {
        return appointmentSettingService.getAllSettings();
    }
    
    /**
     * 管理后台端 - 根据键获取预约设置
     * @param key 设置键
     * @return 预约设置
     */
    @GetMapping(ADMIN_PREFIX + "/appointment-settings/{key}")
    public Result<AppointmentSetting> getSettingByKeyForAdmin(@PathVariable String key) {
        return appointmentSettingService.getSettingByKey(key);
    }
    
    /**
     * 管理后台端 - 批量获取预约设置
     * @param keys 设置键列表
     * @return 预约设置映射
     */
    @PostMapping(ADMIN_PREFIX + "/appointment-settings/batch")
    public Result<Map<String, AppointmentSetting>> getSettingsByKeysForAdmin(@RequestBody List<String> keys) {
        return appointmentSettingService.getSettingsByKeys(keys);
    }
    
    /**
     * 管理后台端 - 更新预约设置
     * @param setting 预约设置
     * @return 更新结果
     */
    @PutMapping(ADMIN_PREFIX + "/appointment-settings")
    public Result<String> updateSettingForAdmin(@RequestBody AppointmentSetting setting) {
        return appointmentSettingService.updateSetting(setting);
    }
    
    /**
     * 管理后台端 - 批量更新预约设置
     * @param settings 预约设置列表
     * @return 更新结果
     */
    @PutMapping(ADMIN_PREFIX + "/appointment-settings/batch")
    public Result<String> updateSettingsForAdmin(@RequestBody List<AppointmentSetting> settings) {
        return appointmentSettingService.updateSettings(settings);
    }
    
    /**
     * 管理后台端 - 获取每日预约统计
     * @param date 日期
     * @return 预约统计信息
     */
    @GetMapping(ADMIN_PREFIX + "/appointment-settings/daily-stats")
    public Result<Map<String, Object>> getDailyAppointmentStatsForAdmin(@RequestParam String date) {
        return appointmentSettingService.getDailyAppointmentStats(date);
    }
    
    /**
     * 管理后台端 - 获取预约项目配置
     * @return 预约项目配置
     */
    @GetMapping(ADMIN_PREFIX + "/appointment-settings/project-config")
    public Result<Map<String, Object>> getAppointmentProjectConfig() {
        return mainConfigService.getAppointmentProjectConfig();
    }
    
    /**
     * 管理后台端 - 保存预约项目配置
     * @param configs 配置映射
     * @return 保存结果
     */
    @PutMapping(ADMIN_PREFIX + "/appointment-settings/project-config")
    public Result<String> saveAppointmentProjectConfig(@RequestBody Map<String, String> configs) {
        // 默认用户ID为1，实际应用中应该从JWT token中获取
        return mainConfigService.saveAppointmentProjectConfig(configs, 1L);
    }
    
    /**
     * 管理后台端 - 获取预约规则配置
     * @return 预约规则配置
     */
    @GetMapping(ADMIN_PREFIX + "/appointment-settings/rule-config")
    public Result<Map<String, Object>> getAppointmentRuleConfig() {
        return mainConfigService.getAppointmentRuleConfig();
    }
    
    /**
     * 管理后台端 - 保存预约规则配置
     * @param configJson 配置JSON
     * @return 保存结果
     */
    @PutMapping(ADMIN_PREFIX + "/appointment-settings/rule-config")
    public Result<String> saveAppointmentRuleConfig(@RequestBody String configJson) {
        // 默认用户ID为1，实际应用中应该从JWT token中获取
        return mainConfigService.saveAppointmentRuleConfig(configJson, 1L);
    }
}
