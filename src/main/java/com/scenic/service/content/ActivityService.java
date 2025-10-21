package com.scenic.service.content;

import java.util.List;
import java.util.Map;

import com.scenic.common.dto.Result;
import com.scenic.dto.content.ActivityDTO;

/**
 * 活动服务接口
 */
public interface ActivityService {
    
    /**
     * 新增活动
     * @param activityDTO 活动信息
     * @return 操作结果
     */
    Result<String> addActivity(ActivityDTO activityDTO);
    
    /**
     * 获取所有活动
     * @return 活动列表
     */
    Result<List<ActivityDTO>> getAllActivities();
    
    /**
     * 根据ID获取活动详情
     * @param id 活动ID
     * @return 活动详情
     */
    Result<ActivityDTO> getActivityById(Long id);
    
    /**
     * 管理端 - 更新活动
     * @param id 活动ID
     * @param activityDTO 活动信息
     * @return 操作结果
     */
    Result<String> updateActivity(Long id, ActivityDTO activityDTO);
    
    /**
     * 管理端 - 删除活动
     * @param id 活动ID
     * @return 操作结果
     */
    Result<String> deleteActivity(Long id);
    
    /**
     * 管理端 - 分页查询活动列表
     * @param title 活动标题（可选）
     * @param status 活动状态（可选）
     * @param startTime 开始时间（可选）
     * @param suitableCrowd 适合人群（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 活动列表
     */
    Result<List<ActivityDTO>> getActivityList(String title, Byte status, String startTime, String suitableCrowd, int pageNum, int pageSize);
    
    /**
     * 管理端 - 获取活动总数
     * @param title 活动标题（可选）
     * @param status 活动状态（可选）
     * @param startTime 开始时间（可选）
     * @param suitableCrowd 适合人群（可选）
     * @return 活动总数
     */
    Result<Integer> getActivityCount(String title, Byte status, String startTime, String suitableCrowd);
    
    /**
     * 管理端 - 分页获取活动列表
     * @param title 活动标题（可选）
     * @param enabled 是否启用（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页活动列表
     */
    Result<Map<String, Object>> getActivityPage(String title, Integer enabled, Integer pageNum, Integer pageSize);
    
    /**
     * 小程序端 - 分页获取活动列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页活动列表
     */
    Result<Map<String, Object>> getActivityPageForMiniapp(int pageNum, int pageSize);
}