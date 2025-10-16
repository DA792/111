package com.scenic.controller.appointment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scenic.common.dto.Result;
import com.scenic.entity.appointment.ParkOpenTime;
import com.scenic.service.appointment.ParkOpenTimeService;

/**
 * 公园开放时间控制器
 * 提供管理后台端的API接口
 */
@RestController
@RequestMapping("/api")
public class ParkOpenTimeController {
    
    // 管理后台端API接口前缀
    private static final String ADMIN_PREFIX = "/manage";
    
    @Autowired
    private ParkOpenTimeService parkOpenTimeService;
    
    /**
     * 管理后台端 - 获取指定月份的开放时间配置
     * @param year 年份
     * @param month 月份
     * @return 月份开放时间配置列表
     */
    @GetMapping(ADMIN_PREFIX + "/park-open-time")
    public Result<List<ParkOpenTime>> getMonthlyOpenTime(
            @RequestParam int year,
            @RequestParam int month) {
        return parkOpenTimeService.getMonthlyOpenTime(year, month);
    }
    
    /**
     * 管理后台端 - 批量保存开放时间配置
     * @param openTimeList 开放时间配置列表
     * @return 保存结果
     */
    @PostMapping(ADMIN_PREFIX + "/park-open-time/batch")
    public Result<String> saveMonthlyOpenTime(@RequestBody List<ParkOpenTime> openTimeList) {
        return parkOpenTimeService.saveMonthlyOpenTime(openTimeList);
    }
    
    /**
     * 管理后台端 - 更新单个日期的开放时间配置
     * @param openTime 开放时间配置
     * @return 更新结果
     */
    @PutMapping(ADMIN_PREFIX + "/park-open-time")
    public Result<String> updateOpenTime(@RequestBody ParkOpenTime openTime) {
        return parkOpenTimeService.updateOpenTime(openTime);
    }
    
    /**
     * 管理后台端 - 批量更新开放时间配置
     * @param openTimeList 开放时间配置列表
     * @return 更新结果
     */
    @PutMapping(ADMIN_PREFIX + "/park-open-time/batch")
    public Result<String> updateOpenTimeBatch(@RequestBody List<ParkOpenTime> openTimeList) {
        return parkOpenTimeService.updateOpenTimeBatch(openTimeList);
    }
    
    /**
     * 管理后台端 - 获取指定日期的开放时间配置
     * @param date 日期 (格式: yyyy-MM-dd)
     * @return 日期开放时间配置
     */
    @GetMapping(ADMIN_PREFIX + "/park-open-time/date")
    public Result<ParkOpenTime> getOpenTimeByDate(@RequestParam String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return parkOpenTimeService.getOpenTimeByDate(date);
        } catch (Exception e) {
            return Result.error("日期格式错误，应为 yyyy-MM-dd 格式");
        }
    }
}
