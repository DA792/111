package com.scenic.service.appointment;

import java.util.List;

import com.scenic.common.dto.Result;
import com.scenic.entity.appointment.ParkOpenTime;

/**
 * 公园开放时间服务接口
 */
public interface ParkOpenTimeService {
    
    /**
     * 获取指定月份的开放时间配置
     * @param year 年份
     * @param month 月份
     * @return 月份开放时间配置列表
     */
    Result<List<ParkOpenTime>> getMonthlyOpenTime(int year, int month);
    
    /**
     * 批量保存开放时间配置
     * @param openTimeList 开放时间配置列表
     * @return 保存结果
     */
    Result<String> saveMonthlyOpenTime(List<ParkOpenTime> openTimeList);
    
    /**
     * 更新单个日期的开放时间配置
     * @param openTime 开放时间配置
     * @return 更新结果
     */
    Result<String> updateOpenTime(ParkOpenTime openTime);
    
    /**
     * 批量更新开放时间配置
     * @param openTimeList 开放时间配置列表
     * @return 更新结果
     */
    Result<String> updateOpenTimeBatch(List<ParkOpenTime> openTimeList);
    
    /**
     * 获取指定日期的开放时间配置
     * @param date 日期 (格式: yyyy-MM-dd)
     * @return 日期开放时间配置
     */
    Result<ParkOpenTime> getOpenTimeByDate(String date);
}
