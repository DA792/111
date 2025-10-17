package com.scenic.service.content.impl;

import com.scenic.common.dto.Result;
import com.scenic.dto.content.ActivityDTO;
import com.scenic.entity.content.Activity;
import com.scenic.mapper.content.ActivityMapper;
import com.scenic.service.content.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 活动服务实现类
 */
@Service
public class ActivityServiceImpl implements ActivityService {
    
    @Autowired
    private ActivityMapper activityMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // Redis缓存键前缀
    private static final String ACTIVITY_CACHE_PREFIX = "activity:";
    private static final String ALL_ACTIVITIES_CACHE_KEY = "all_activities";
    
    /**
     * 新增活动
     * @param activityDTO 活动信息
     * @return 操作结果
     */
    @Override
    public Result<String> addActivity(ActivityDTO activityDTO) {
        try {
            Activity activity = new Activity();
            activity.setTitle(activityDTO.getTitle());
            activity.setContent(activityDTO.getContent());
            activity.setLocation(activityDTO.getLocation());
            activity.setEnabled(true);
            activity.setStartTime(activityDTO.getStartTime());
            activity.setEndTime(activityDTO.getEndTime());
            activity.setCreateTime(LocalDateTime.now());
            activity.setUpdateTime(LocalDateTime.now());
            
            activityMapper.insert(activity);
            
            // 清除所有活动列表缓存
            redisTemplate.delete(ALL_ACTIVITIES_CACHE_KEY);
            
            return Result.success("操作成功", "活动新增成功");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取所有活动
     * @return 活动列表
     */
    @Override
    public Result<List<ActivityDTO>> getAllActivities() {
        try {
            // 先从Redis缓存中获取
            List<ActivityDTO> cachedActivities = (List<ActivityDTO>) redisTemplate.opsForValue().get(ALL_ACTIVITIES_CACHE_KEY);
            if (cachedActivities != null) {
                return Result.success("查询成功", cachedActivities);
            }
            
            // 缓存中没有则从数据库查询
            List<Activity> activities = activityMapper.selectAllEnabled();
            List<ActivityDTO> activityDTOs = activities.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // 将结果存入Redis缓存，过期时间1小时
            redisTemplate.opsForValue().set(ALL_ACTIVITIES_CACHE_KEY, activityDTOs, 1, TimeUnit.HOURS);
            
            return Result.success("查询成功", activityDTOs);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取活动详情
     * @param id 活动ID
     * @return 活动详情
     */
    @Override
    public Result<ActivityDTO> getActivityById(Long id) {
        try {
            // 先从Redis缓存中获取
            String cacheKey = ACTIVITY_CACHE_PREFIX + id;
            ActivityDTO cachedActivity = (ActivityDTO) redisTemplate.opsForValue().get(cacheKey);
            if (cachedActivity != null) {
                return Result.success("查询成功", cachedActivity);
            }
            
            // 缓存中没有则从数据库查询
            Activity activity = activityMapper.selectById(id);
            
            if (activity != null && activity.getEnabled()) {
                ActivityDTO activityDTO = convertToDTO(activity);
                
                // 将结果存入Redis缓存，过期时间1小时
                redisTemplate.opsForValue().set(cacheKey, activityDTO, 1, TimeUnit.HOURS);
                return Result.success("查询成功", activityDTO);
            } else {
                return Result.error("活动不存在");
            }
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理端 - 更新活动
     * @param id 活动ID
     * @param activityDTO 活动信息
     * @return 操作结果
     */
    @Override
    public Result<String> updateActivity(Long id, ActivityDTO activityDTO) {
        try {
            Activity activity = activityMapper.selectById(id);
            if (activity != null && activity.getEnabled()) {
                activity.setTitle(activityDTO.getTitle());
                activity.setContent(activityDTO.getContent());
                activity.setLocation(activityDTO.getLocation());
                activity.setStartTime(activityDTO.getStartTime());
                activity.setEndTime(activityDTO.getEndTime());
                activity.setUpdateTime(LocalDateTime.now());
                
                activityMapper.updateById(activity);
                
                // 更新缓存
                String cacheKey = ACTIVITY_CACHE_PREFIX + id;
                ActivityDTO updatedDTO = convertToDTO(activity);
                redisTemplate.opsForValue().set(cacheKey, updatedDTO, 1, TimeUnit.HOURS);
                
                // 清除所有活动列表缓存
                redisTemplate.delete(ALL_ACTIVITIES_CACHE_KEY);
                
                return Result.success("操作成功", "活动更新成功");
            }
            return Result.error("活动不存在");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理端 - 删除活动
     * @param id 活动ID
     * @return 操作结果
     */
    @Override
    public Result<String> deleteActivity(Long id) {
        try {
            Activity activity = activityMapper.selectById(id);
            if (activity != null) {
                activity.setEnabled(false);
                activity.setUpdateTime(LocalDateTime.now());
                activityMapper.updateById(activity);
                
                // 删除缓存
                String cacheKey = ACTIVITY_CACHE_PREFIX + id;
                redisTemplate.delete(cacheKey);
                
                // 清除所有活动列表缓存
                redisTemplate.delete(ALL_ACTIVITIES_CACHE_KEY);
                
                return Result.success("操作成功", "活动已删除");
            }
            return Result.error("活动不存在");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 将Activity实体转换为ActivityDTO
     * @param activity Activity实体
     * @return ActivityDTO
     */
    private ActivityDTO convertToDTO(Activity activity) {
        ActivityDTO dto = new ActivityDTO();
        dto.setId(activity.getId());
        dto.setTitle(activity.getTitle());
        dto.setContent(activity.getContent());
        dto.setLocation(activity.getLocation());
        dto.setSuitableCrowd(activity.getSuitableCrowd());
        dto.setEnabled(activity.getEnabled());
        dto.setCreateTime(activity.getCreateTime());
        dto.setUpdateTime(activity.getUpdateTime());
        dto.setStartTime(activity.getStartTime());
        dto.setEndTime(activity.getEndTime());
        return dto;
    }
}
