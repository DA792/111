package com.scenic.service.appointment;

import java.util.List;

import com.scenic.common.dto.Result;
import com.scenic.dto.appointment.AppointmentDataDTO;

/**
 * 预约数据服务接口
 */
public interface AppointmentDataService {
    
    /**
     * 获取入园预约数据（个人 + 团队预约聚合）
     * 核心逻辑：先通过main_config获取 "单日预约上限"，再关联park_open_time_config判断日期开放状态，
     * 最后聚合个人 + 团队预约人数，判断 "不开放 / 已满 / 已预约 X/Y"
     * 
     * @param year 年份
     * @param month 月份
     * @return 入园预约数据列表
     */
    Result<List<AppointmentDataDTO>> getEnterReservationData(Integer year, Integer month);
    
    /**
     * 获取活动预约数据
     * 核心逻辑：通过main_config判断活动预约是否开放，关联park_open_time_config判断日期开放状态，
     * 再按活动分组统计预约人数（对比活动自身上限）
     * 
     * @param year 年份
     * @param month 月份
     * @return 活动预约数据列表
     */
    Result<List<AppointmentDataDTO>> getActivityReservationData(Integer year, Integer month);
}
