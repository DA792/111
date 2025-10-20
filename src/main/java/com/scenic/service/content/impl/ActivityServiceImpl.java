package com.scenic.service.content.impl;

import com.scenic.common.dto.Result;
import com.scenic.dto.content.ActivityDTO;
import com.scenic.entity.content.Activity;
import com.scenic.entity.user.User;
import com.scenic.mapper.content.ActivityMapper;
import com.scenic.mapper.user.UserMapper;
import com.scenic.service.content.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private UserMapper userMapper;
    
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
            activity.setStartTime(activityDTO.getStartTime());
            activity.setEndTime(activityDTO.getEndTime());
            activity.setSuitableCrowd(activityDTO.getSuitableCrowd());
            activity.setLocation(activityDTO.getLocation());
            activity.setPrice(activityDTO.getPrice());
            activity.setTeamLimit(activityDTO.getTeamLimit());
            activity.setContent(activityDTO.getContent());
            activity.setContentImageIds(activityDTO.getContentImageIds());
            activity.setCoverImageId(activityDTO.getCoverImageId());
            activity.setStatus((byte) 0); // 默认未结束
            activity.setCreateTime(LocalDateTime.now());
            activity.setUpdateTime(LocalDateTime.now());
            activity.setCreateBy(activityDTO.getCreateBy());
            activity.setUpdateBy(activityDTO.getUpdateBy());
            activity.setReservationPriority(activityDTO.getReservationPriority());
            
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
            
            // 缓存中没有则从数据库查询所有活动（包括禁用的）
            List<Activity> activities = activityMapper.selectList(0, 1000); // 获取前1000个活动
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
            // 清除缓存，避免使用旧的缓存数据
            String cacheKey = ACTIVITY_CACHE_PREFIX + id;
            redisTemplate.delete(cacheKey);
            redisTemplate.delete(ALL_ACTIVITIES_CACHE_KEY);
            
            // 从数据库查询
            
            // 缓存中没有则从数据库查询
            Activity activity = activityMapper.selectById(id);
            
            if (activity != null && activity.getStatus() == 0) {
                ActivityDTO activityDTO = convertToDTO(activity);
                
                // 将结果存入Redis缓存，过期时间1小时
                redisTemplate.opsForValue().set(cacheKey, activityDTO, 1, TimeUnit.HOURS);
                return Result.success("查询成功", activityDTO);
            } else {
                return Result.error("活动不存在或已结束");
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
            if (activity != null && activity.getStatus() == 0) {
                // 只更新非空字段，避免未传输字段被置空
                if (activityDTO.getTitle() != null) {
                    activity.setTitle(activityDTO.getTitle());
                }
                if (activityDTO.getStartTime() != null) {
                    activity.setStartTime(activityDTO.getStartTime());
                }
                if (activityDTO.getEndTime() != null) {
                    activity.setEndTime(activityDTO.getEndTime());
                }
                if (activityDTO.getSuitableCrowd() != null) {
                    activity.setSuitableCrowd(activityDTO.getSuitableCrowd());
                }
                if (activityDTO.getLocation() != null) {
                    activity.setLocation(activityDTO.getLocation());
                }
                if (activityDTO.getPrice() != null) {
                    activity.setPrice(activityDTO.getPrice());
                }
                if (activityDTO.getTeamLimit() != null) {
                    activity.setTeamLimit(activityDTO.getTeamLimit());
                }
                if (activityDTO.getContent() != null) {
                    activity.setContent(activityDTO.getContent());
                }
                if (activityDTO.getContentImageIds() != null) {
                    activity.setContentImageIds(activityDTO.getContentImageIds());
                }
                if (activityDTO.getCoverImageId() != null) {
                    activity.setCoverImageId(activityDTO.getCoverImageId());
                }
                if (activityDTO.getUpdateBy() != null) {
                    activity.setUpdateBy(activityDTO.getUpdateBy());
                }
                if (activityDTO.getReservationPriority() != null) {
                    activity.setReservationPriority(activityDTO.getReservationPriority());
                }
                
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
            return Result.error("活动不存在或已结束");
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
                // 使用逻辑删除，设置deleted为1
                activityMapper.deleteById(id);
                
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
     * 管理端 - 分页查询活动列表
     * @param title 活动标题（可选）
     * @param status 活动状态（可选）
     * @param startTime 开始时间（可选）
     * @param suitableCrowd 适合人群（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 活动列表
     */
    @Override
    public Result<List<ActivityDTO>> getActivityList(String title, Byte status, String startTime, String suitableCrowd, int pageNum, int pageSize) {
        try {
            int offset = (pageNum - 1) * pageSize;
            List<Activity> activities = activityMapper.selectForAdmin(title, status, startTime, suitableCrowd, offset, pageSize);
            List<ActivityDTO> activityDTOs = activities.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return Result.success("查询成功", activityDTOs);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理端 - 获取活动总数
     * @param title 活动标题（可选）
     * @param status 活动状态（可选）
     * @param startTime 开始时间（可选）
     * @param suitableCrowd 适合人群（可选）
     * @return 活动总数
     */
    @Override
    public Result<Integer> getActivityCount(String title, Byte status, String startTime, String suitableCrowd) {
        try {
            int count = activityMapper.selectCountForAdmin(title, status, startTime, suitableCrowd);
            return Result.success("查询成功", count);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
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
        dto.setStartTime(activity.getStartTime());
        dto.setEndTime(activity.getEndTime());
        dto.setSuitableCrowd(activity.getSuitableCrowd());
        dto.setLocation(activity.getLocation());
        dto.setPrice(activity.getPrice());
        dto.setTeamLimit(activity.getTeamLimit());
        dto.setContent(activity.getContent());
        dto.setContentImageIds(activity.getContentImageIds());
        dto.setCoverImageId(activity.getCoverImageId());
        dto.setStatus(activity.getStatus());
        dto.setCreateTime(activity.getCreateTime());
        dto.setUpdateTime(activity.getUpdateTime());
        dto.setCreateBy(activity.getCreateBy());
        dto.setUpdateBy(activity.getUpdateBy());
        dto.setReservationPriority(activity.getReservationPriority());
        
        // 根据createBy查询用户信息，设置publisher字段
        if (activity.getCreateBy() != null) {
            User user = userMapper.selectById(activity.getCreateBy());
            if (user != null) {
                dto.setPublisher(user.getRealName());
            }
        }
        
        return dto;
    }
}
