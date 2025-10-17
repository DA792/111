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
            
            // 查询预约数据
            List<Map<String, Object>> dataList = appointmentDataMapper.getEnterReservationData(
                    year, month, dailyLimit, individualReserveStatus, teamReserveStatus);
            
            // 转换为DTO对象
            List<AppointmentDataDTO> resultList = new ArrayList<>();
            for (Map<String, Object> data : dataList) {
                AppointmentDataDTO dto = new AppointmentDataDTO();
                dto.setReserveDate((java.util.Date) data.get("reserve_date"));
                dto.setReserveStatus((String) data.get("reserve_status"));
                dto.setBookedCount(((Number) data.get("booked_count")).intValue());
                dto.setTotalLimit(((Number) data.get("total_limit")).intValue());
                dto.setIsOpen((Boolean) data.get("is_open"));
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
                dto.setReserveDate((java.util.Date) data.get("reserve_date"));
                dto.setReserveStatus((String) data.get("reserve_status"));
                dto.setActivityName((String) data.get("activity_name"));
                
                // 活动上限可能为null
                Object activityLimitObj = data.get("activity_limit");
                if (activityLimitObj != null) {
                    dto.setActivityLimit(((Number) activityLimitObj).intValue());
                }
                
                dto.setBookedCount(((Number) data.get("booked_count")).intValue());
                dto.setIsOpen((Boolean) data.get("is_open"));
                resultList.add(dto);
            }
            
            return Result.success(resultList);
        } catch (Exception e) {
            return Result.error("获取活动预约数据失败：" + e.getMessage());
        }
    }
}
