package com.scenic.service.appointment.impl;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scenic.common.dto.Result;
import com.scenic.entity.appointment.ParkOpenTime;
import com.scenic.mapper.appointment.ParkOpenTimeMapper;
import com.scenic.service.appointment.ParkOpenTimeService;

/**
 * 公园开放时间服务实现类
 */
@Service
public class ParkOpenTimeServiceImpl implements ParkOpenTimeService {
    
    @Autowired
    private ParkOpenTimeMapper parkOpenTimeMapper;
    
    /**
     * 获取指定月份的开放时间配置
     * @param year 年份
     * @param month 月份
     * @return 月份开放时间配置列表
     */
    @Override
    public Result<List<ParkOpenTime>> getMonthlyOpenTime(int year, int month) {
        try {
            // 验证月份参数
            if (month < 1 || month > 12) {
                return Result.error("月份参数错误，应在1-12之间");
            }
            
            // 计算月份的开始和结束日期
            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();
            
            // 查询数据库
            List<ParkOpenTime> openTimeList = parkOpenTimeMapper.selectByDateRange(startDate, endDate);
            
            return Result.success(openTimeList);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量保存开放时间配置（新增或更新）
     * @param openTimeList 开放时间配置列表
     * @return 保存结果
     */
    @Override
    public Result<String> saveMonthlyOpenTime(List<ParkOpenTime> openTimeList) {
        try {
            if (openTimeList == null || openTimeList.isEmpty()) {
                return Result.error("开放时间配置列表不能为空");
            }
            
            for (ParkOpenTime openTime : openTimeList) {
                if (openTime.getConfigDate() == null) {
                    return Result.error("配置日期不能为空");
                }
                
                // 自动修正dayType字段
                openTime.setDayType(getDayType(openTime.getConfigDate()));
                
                // 设置创建时间和更新时间
                openTime.setCreateTime(java.time.LocalDateTime.now());
                openTime.setUpdateTime(java.time.LocalDateTime.now());
                
                // 插入或更新数据
                parkOpenTimeMapper.insertOrUpdate(openTime);
            }
            
            return Result.success("开放时间配置保存成功");
        } catch (Exception e) {
            return Result.error("保存失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新单个日期的开放时间配置
     * @param openTime 开放时间配置
     * @return 更新结果
     */
    @Override
    public Result<String> updateOpenTime(ParkOpenTime openTime) {
        try {
            if (openTime.getId() == null) {
                return Result.error("开放时间配置ID不能为空");
            }
            
            if (openTime.getConfigDate() == null) {
                return Result.error("配置日期不能为空");
            }
            
            // 自动修正dayType字段
            openTime.setDayType(getDayType(openTime.getConfigDate()));
            
            // 设置更新时间
            openTime.setUpdateTime(java.time.LocalDateTime.now());
            
            // 更新数据
            parkOpenTimeMapper.updateById(openTime);
            
            return Result.success("开放时间配置更新成功");
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量更新开放时间配置
     * @param openTimeList 开放时间配置列表
     * @return 更新结果
     */
    @Override
    public Result<String> updateOpenTimeBatch(List<ParkOpenTime> openTimeList) {
        try {
            if (openTimeList == null || openTimeList.isEmpty()) {
                return Result.error("开放时间配置列表不能为空");
            }
            
            for (ParkOpenTime openTime : openTimeList) {
                if (openTime.getId() == null) {
                    return Result.error("开放时间配置ID不能为空");
                }
                
                if (openTime.getConfigDate() == null) {
                    return Result.error("配置日期不能为空");
                }
                
                // 自动修正dayType字段
                openTime.setDayType(getDayType(openTime.getConfigDate()));
                
                // 设置更新时间
                openTime.setUpdateTime(java.time.LocalDateTime.now());
                
                // 更新数据
                parkOpenTimeMapper.updateById(openTime);
            }
            
            return Result.success("开放时间配置批量更新成功");
        } catch (Exception e) {
            return Result.error("批量更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取指定日期的开放时间配置
     * @param date 日期 (格式: yyyy-MM-dd)
     * @return 日期开放时间配置
     */
    @Override
    public Result<ParkOpenTime> getOpenTimeByDate(String date) {
        try {
            LocalDate configDate = LocalDate.parse(date);
            
            // 查询数据库
            ParkOpenTime openTime = parkOpenTimeMapper.selectByDate(configDate);
            
            if (openTime == null) {
                // 如果数据库中没有配置，则创建默认配置（开放）
                openTime = new ParkOpenTime();
                openTime.setConfigDate(configDate);
                openTime.setIsClosed(false);
                openTime.setDayType(getDayType(configDate));
                openTime.setCreateTime(java.time.LocalDateTime.now());
                openTime.setUpdateTime(java.time.LocalDateTime.now());
            } else {
                // 如果数据库中有配置，自动修正dayType字段
                openTime.setDayType(getDayType(configDate));
            }
            
            return Result.success(openTime);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据日期获取日类型（0-工作日，1-节假日）
     * @param date 日期
     * @return 日类型 (0-工作日, 1-节假日)
     */
    private int getDayType(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        // 周一到周五为工作日(0)，周六周日为节假日(1)
        return (dayOfWeek >= 1 && dayOfWeek <= 5) ? 0 : 1;
    }
    
    /**
     * 修正数据库中所有记录的day_type字段
     * @return 修正结果
     */
    @Override
    public Result<String> fixDayTypeData() {
        try {
            // 查询所有记录
            List<ParkOpenTime> allRecords = parkOpenTimeMapper.selectAll();
            
            int updateCount = 0;
            for (ParkOpenTime record : allRecords) {
                LocalDate configDate = record.getConfigDate();
                if (configDate != null) {
                    // 根据日期重新计算day_type
                    int correctDayType = getDayType(configDate);
                    
                    // 如果当前day_type不正确，则更新
                    if (record.getDayType() == null || record.getDayType() != correctDayType) {
                        record.setDayType(correctDayType);
                        record.setUpdateTime(java.time.LocalDateTime.now());
                        parkOpenTimeMapper.updateById(record);
                        updateCount++;
                    }
                }
            }
            
            return Result.success("成功修正" + updateCount + "条记录的day_type字段");
        } catch (Exception e) {
            return Result.error("修正day_type字段失败：" + e.getMessage());
        }
    }
}
