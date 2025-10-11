package com.scenic.service.appointment;

import java.util.List;
import java.util.Map;

import com.scenic.common.dto.Result;
import com.scenic.entity.appointment.AppointmentSetting;

/**
 * 预约设置服务接口
 */
public interface AppointmentSettingService {
    
    /**
     * 获取所有预约设置
     * @return 预约设置列表
     */
    Result<List<AppointmentSetting>> getAllSettings();
    
    /**
     * 根据键获取预约设置
     * @param key 设置键
     * @return 预约设置
     */
    Result<AppointmentSetting> getSettingByKey(String key);
    
    /**
     * 批量获取预约设置
     * @param keys 设置键列表
     * @return 预约设置映射
     */
    Result<Map<String, AppointmentSetting>> getSettingsByKeys(List<String> keys);
    
    /**
     * 更新预约设置
     * @param setting 预约设置
     * @return 更新结果
     */
    Result<String> updateSetting(AppointmentSetting setting);
    
    /**
     * 批量更新预约设置
     * @param settings 预约设置列表
     * @return 更新结果
     */
    Result<String> updateSettings(List<AppointmentSetting> settings);
    
    /**
     * 获取每日预约统计
     * @param date 日期
     * @return 预约统计信息
     */
    Result<Map<String, Object>> getDailyAppointmentStats(String date);
}
