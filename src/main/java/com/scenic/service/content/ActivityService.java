package com.scenic.service.content;

import java.util.List;

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
}
