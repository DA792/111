package com.scenic.service.appointment.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scenic.common.dto.Result;
import com.scenic.entity.appointment.AppointmentSetting;
import com.scenic.mapper.appointment.AppointmentSettingMapper;
import com.scenic.mapper.appointment.AppointmentMapper;
import com.scenic.service.appointment.AppointmentSettingService;

/**
 * 预约设置服务实现类
 */
@Service
public class AppointmentSettingServiceImpl implements AppointmentSettingService {
    
    @Autowired
    private AppointmentSettingMapper appointmentSettingMapper;
    
    @Autowired
    private AppointmentMapper appointmentMapper;
    
    /**
     * 获取所有预约设置
     * @return 预约设置列表
     */
    @Override
    public Result<List<AppointmentSetting>> getAllSettings() {
        try {
            List<AppointmentSetting> settings = appointmentSettingMapper.selectAll();
            return Result.success(settings);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据键获取预约设置
     * @param key 设置键
     * @return 预约设置
     */
    @Override
    public Result<AppointmentSetting> getSettingByKey(String key) {
        try {
            AppointmentSetting setting = appointmentSettingMapper.selectBySettingKey(key);
            if (setting == null) {
                return Result.error("设置不存在");
            }
            return Result.success(setting);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量获取预约设置
     * @param keys 设置键列表
     * @return 预约设置映射
     */
    @Override
    public Result<Map<String, AppointmentSetting>> getSettingsByKeys(List<String> keys) {
        try {
            Map<String, AppointmentSetting> settings = new HashMap<>();
            for (String key : keys) {
                AppointmentSetting setting = appointmentSettingMapper.selectBySettingKey(key);
                if (setting != null) {
                    settings.put(key, setting);
                }
            }
            return Result.success(settings);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新预约设置
     * @param setting 预约设置
     * @return 更新结果
     */
    @Override
    public Result<String> updateSetting(AppointmentSetting setting) {
        try {
            if (setting.getSettingKey() == null || setting.getSettingKey().isEmpty()) {
                return Result.error("设置键不能为空");
            }
            
            setting.setUpdateTime(java.time.LocalDateTime.now());
            
            if (setting.getId() == null) {
                // 新增
                setting.setCreateTime(java.time.LocalDateTime.now());
                appointmentSettingMapper.insert(setting);
            } else {
                // 更新
                appointmentSettingMapper.updateById(setting);
            }
            return Result.success("设置已更新");
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量更新预约设置
     * @param settings 预约设置列表
     * @return 更新结果
     */
    @Override
    public Result<String> updateSettings(List<AppointmentSetting> settings) {
        try {
            for (AppointmentSetting setting : settings) {
                if (setting.getSettingKey() == null || setting.getSettingKey().isEmpty()) {
                    return Result.error("设置键不能为空");
                }
                setting.setUpdateTime(java.time.LocalDateTime.now());
                
                if (setting.getId() == null) {
                    // 新增
                    setting.setCreateTime(java.time.LocalDateTime.now());
                    appointmentSettingMapper.insert(setting);
                } else {
                    // 更新
                    appointmentSettingMapper.updateById(setting);
                }
            }
            return Result.success("设置已批量更新");
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取每日预约统计
     * @param date 日期
     * @return 预约统计信息
     */
    @Override
    public Result<Map<String, Object>> getDailyAppointmentStats(String date) {
        try {
            // 验证日期格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(date, formatter);
            
            // 获取当日预约人数
            int count = appointmentMapper.countByDate(date);
            
            // 获取每日最大预约人数设置
            AppointmentSetting maxSetting = appointmentSettingMapper.selectBySettingKey("max_daily_appointments");
            int maxAppointments = maxSetting != null ? 
                Integer.parseInt(maxSetting.getSettingValue()) : 100;
            
            // 构造返回结果
            Map<String, Object> stats = new HashMap<>();
            stats.put("date", date);
            stats.put("currentCount", count);
            stats.put("maxCount", maxAppointments);
            stats.put("isFull", count >= maxAppointments);
            stats.put("availableCount", Math.max(0, maxAppointments - count));
            
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
}
