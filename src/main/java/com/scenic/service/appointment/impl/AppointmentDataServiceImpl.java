package com.scenic.service.appointment.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scenic.common.dto.Result;
import com.scenic.dto.appointment.AppointmentDataDTO;
import com.scenic.entity.MainConfig;
import com.scenic.mapper.MainConfigMapper;
import com.scenic.mapper.appointment.AppointmentDataMapper;
import com.scenic.service.appointment.AppointmentDataService;

/**
 * 预约数据服务实现类
 */
@Service
public class AppointmentDataServiceImpl implements AppointmentDataService {
    
    @Autowired
    private AppointmentDataMapper appointmentDataMapper;
    
    @Autowired
    private MainConfigMapper mainConfigMapper;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 获取入园预约数据（个人 + 团队预约聚合）
     */
    @Override
    public Result<List<AppointmentDataDTO>> getEnterReservationData(Integer year, Integer month) {
        try {
            // 获取全局预约规则配置
            MainConfig ruleConfig = mainConfigMapper.selectByConfigName("全局预约规则");
            if (ruleConfig == null) {
                return Result.error("全局预约规则配置不存在");
            }
            
            // 获取预约项目开放状态配置
            MainConfig projectConfig = mainConfigMapper.selectByConfigName("预约项目开放状态");
            if (projectConfig == null) {
                return Result.error("预约项目开放状态配置不存在");
            }
            
            // 解析配置JSON
            Map<String, Object> ruleConfigMap = objectMapper.readValue(ruleConfig.getConfigJson().toString(), Map.class);
            Map<String, Object> projectConfigMap = objectMapper.readValue(projectConfig.getConfigJson().toString(), Map.class);
            
            // 获取单日预约上限
            Integer dailyLimit = Integer.parseInt(ruleConfigMap.get("daily_reserve_limit").toString());
            
            // 获取个人预约和团队预约开放状态
            Integer individualReserveStatus = Integer.parseInt(projectConfigMap.get("individual_reserve_status").toString());
            Integer teamReserveStatus = Integer.parseInt(projectConfigMap.get("team_reserve_status").toString());
            
            // 构造年月字符串
            String yearMonth = String.format("%04d-%02d-01", year, month);
            
            // 查询预约数据 - 使用配置中的预约项目状态
            List<Map<String, Object>> dataList = appointmentDataMapper.getEnterReservationData(
                    year, month, yearMonth, dailyLimit, individualReserveStatus, teamReserveStatus);
            
            // 转换为DTO对象
            List<AppointmentDataDTO> resultList = new ArrayList<>();
            for (Map<String, Object> data : dataList) {
                AppointmentDataDTO dto = new AppointmentDataDTO();
                
                // 修复reserveDate类型转换问题
                Object reserveDateObj = data.get("reserve_date");
                if (reserveDateObj instanceof java.util.Date) {
                    dto.setReserveDate((java.util.Date) reserveDateObj);
                } else if (reserveDateObj instanceof String) {
                    // 如果是字符串，尝试解析为Date
                    try {
                        // 假设日期格式为 "yyyy-MM-dd"
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                        dto.setReserveDate(sdf.parse((String) reserveDateObj));
                    } catch (Exception e) {
                        // 如果解析失败，设置为null
                        dto.setReserveDate(null);
                    }
                } else {
                    dto.setReserveDate(null);
                }
                
                dto.setReserveStatus((String) data.get("reserve_status"));
                
                // 安全处理booked_count字段
                Object bookedCountObj = data.get("booked_count");
                if (bookedCountObj instanceof Number) {
                    dto.setBookedCount(((Number) bookedCountObj).intValue());
                } else {
                    dto.setBookedCount(0);
                }
                
                // 安全处理total_limit字段
                Object totalLimitObj = data.get("total_limit");
                if (totalLimitObj instanceof Number) {
                    dto.setTotalLimit(((Number) totalLimitObj).intValue());
                } else {
                    dto.setTotalLimit(0);
                }
                
                // 修复类型转换问题：将数字类型转换为Boolean类型
                Object isOpenObj = data.get("is_open");
                if (isOpenObj instanceof Boolean) {
                    dto.setIsOpen((Boolean) isOpenObj);
                } else if (isOpenObj instanceof Number) {
                    dto.setIsOpen(((Number) isOpenObj).intValue() == 1);
                } else {
                    dto.setIsOpen(Boolean.FALSE);
                }
                resultList.add(dto);
            }
            
            return Result.success(resultList);
        } catch (Exception e) {
            return Result.error("获取入园预约数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取活动预约数据
     */
    @Override
    public Result<List<AppointmentDataDTO>> getActivityReservationData(Integer year, Integer month) {
        try {
            // 获取预约项目开放状态配置
            MainConfig projectConfig = mainConfigMapper.selectByConfigName("预约项目开放状态");
            if (projectConfig == null) {
                return Result.error("预约项目开放状态配置不存在");
            }
            
            // 解析配置JSON
            Map<String, Object> projectConfigMap = objectMapper.readValue(projectConfig.getConfigJson().toString(), Map.class);
            
            // 获取活动预约开放状态
            Integer activityReserveStatus = Integer.parseInt(projectConfigMap.get("activity_reserve_status").toString());
            
            // 查询预约数据
            List<Map<String, Object>> dataList = appointmentDataMapper.getActivityReservationData(
                    year, month, activityReserveStatus);
            
            // 转换为DTO对象
            List<AppointmentDataDTO> resultList = new ArrayList<>();
            for (Map<String, Object> data : dataList) {
                AppointmentDataDTO dto = new AppointmentDataDTO();
                
                // 修复reserveDate类型转换问题
                Object reserveDateObj = data.get("reserve_date");
                if (reserveDateObj instanceof java.util.Date) {
                    dto.setReserveDate((java.util.Date) reserveDateObj);
                } else if (reserveDateObj instanceof String) {
                    // 如果是字符串，尝试解析为Date
                    try {
                        // 假设日期格式为 "yyyy-MM-dd"
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                        dto.setReserveDate(sdf.parse((String) reserveDateObj));
                    } catch (Exception e) {
                        // 如果解析失败，设置为null
                        dto.setReserveDate(null);
                    }
                } else {
                    dto.setReserveDate(null);
                }
                
                dto.setReserveStatus((String) data.get("reserve_status"));
                dto.setActivityName((String) data.get("activity_name"));
                
                // 活动上限可能为null
                Object activityLimitObj = data.get("activity_limit");
                if (activityLimitObj != null) {
                    dto.setActivityLimit(((Number) activityLimitObj).intValue());
                }
                
                // 安全处理booked_count字段
                Object bookedCountObj = data.get("booked_count");
                if (bookedCountObj instanceof Number) {
                    dto.setBookedCount(((Number) bookedCountObj).intValue());
                } else {
                    dto.setBookedCount(0);
                }
                
                // 修复类型转换问题：将数字类型转换为Boolean类型
                Object isOpenObj = data.get("is_open");
                if (isOpenObj instanceof Boolean) {
                    dto.setIsOpen((Boolean) isOpenObj);
                } else if (isOpenObj instanceof Number) {
                    dto.setIsOpen(((Number) isOpenObj).intValue() == 1);
                } else {
                    dto.setIsOpen(Boolean.FALSE);
                }
                resultList.add(dto);
            }
            
            return Result.success(resultList);
        } catch (Exception e) {
            return Result.error("获取活动预约数据失败：" + e.getMessage());
        }
    }
}
