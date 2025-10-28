package com.scenic.mapper.appointment;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 预约数据Mapper接口
 */
@Mapper
public interface AppointmentDataMapper {
    
    /**
     * 获取入园预约数据（个人 + 团队预约聚合）
     * 
     * @param year 年份
     * @param month 月份
     * @param dailyLimit 单日预约上限
     * @param individualReserveStatus 个人预约开放状态
     * @param teamReserveStatus 团队预约开放状态
     * @return 入园预约数据列表
     */
    List<Map<String, Object>> getEnterReservationData(
            @Param("year") Integer year, 
            @Param("month") Integer month,
            @Param("yearMonth") String yearMonth,
            @Param("dailyLimit") Integer dailyLimit,
            @Param("individualReserveStatus") Integer individualReserveStatus,
            @Param("teamReserveStatus") Integer teamReserveStatus);
    
    /**
     * 获取活动预约数据
     * 
     * @param year 年份
     * @param month 月份
     * @param activityReserveStatus 活动预约开放状态
     * @return 活动预约数据列表
     */
    List<Map<String, Object>> getActivityReservationData(
            @Param("year") Integer year, 
            @Param("month") Integer month,
            @Param("activityReserveStatus") Integer activityReserveStatus);
}
