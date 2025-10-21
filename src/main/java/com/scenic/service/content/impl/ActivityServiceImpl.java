package com.scenic.service.content.impl;

import com.scenic.common.dto.Result;
import com.scenic.dto.content.ActivityDTO;
import com.scenic.entity.content.Activity;
import com.scenic.entity.user.User;
import com.scenic.mapper.content.ActivityMapper;
import com.scenic.mapper.user.UserMapper;
import com.scenic.service.content.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.scenic.utils.FileUploadUtil;
import com.scenic.mapper.ResourceFileMapper;
import com.scenic.entity.ResourceFile;

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
    private FileUploadUtil fileUploadUtil;
    
    @Autowired
    private ResourceFileMapper resourceFileMapper;
    
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
            // 直接从数据库查询所有活动（包括禁用的）
            List<Activity> activities = activityMapper.selectList(0, 1000); // 获取前1000个活动
            List<ActivityDTO> activityDTOs = activities.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
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
            Activity activity = activityMapper.selectById(id);
            
            if (activity != null && activity.getStatus() == 0) {
                ActivityDTO activityDTO = convertToDTO(activity);
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
            Activity existingActivity = activityMapper.selectById(id);
            if (existingActivity != null && existingActivity.getStatus() == 0) {
                // 只更新非空字段，避免未传输字段被置空
                if (activityDTO.getTitle() != null) {
                    existingActivity.setTitle(activityDTO.getTitle());
                }
                if (activityDTO.getStartTime() != null) {
                    existingActivity.setStartTime(activityDTO.getStartTime());
                }
                if (activityDTO.getEndTime() != null) {
                    existingActivity.setEndTime(activityDTO.getEndTime());
                }
                if (activityDTO.getSuitableCrowd() != null) {
                    existingActivity.setSuitableCrowd(activityDTO.getSuitableCrowd());
                }
                if (activityDTO.getLocation() != null) {
                    existingActivity.setLocation(activityDTO.getLocation());
                }
                if (activityDTO.getPrice() != null) {
                    existingActivity.setPrice(activityDTO.getPrice());
                }
                if (activityDTO.getTeamLimit() != null) {
                    existingActivity.setTeamLimit(activityDTO.getTeamLimit());
                }
                if (activityDTO.getContent() != null) {
                    existingActivity.setContent(activityDTO.getContent());
                }
                
                // 处理详情内容图片
                if (activityDTO.getContentImageIds() != null) {
                    // 删除原有内容图片
                    if (existingActivity.getContentImageIds() != null && !existingActivity.getContentImageIds().isEmpty()) {
                        deleteFilesByType(existingActivity.getContentImageIds(), "详情内容图片");
                    }
                    // 设置新的内容图片
                    existingActivity.setContentImageIds(activityDTO.getContentImageIds());
                }
                
                // 处理封面图片
                if (activityDTO.getCoverImageId() != null) {
                    // 删除原有封面图片
                    if (existingActivity.getCoverImageId() != null) {
                        deleteFilesByType(java.util.Arrays.asList(existingActivity.getCoverImageId()), "封面图片");
                    }
                    // 设置新的封面图片
                    existingActivity.setCoverImageId(activityDTO.getCoverImageId());
                } else if (activityDTO.getCoverImageId() == null && existingActivity.getCoverImageId() != null) {
                    // 如果传入null且原有封面不为null，删除原有封面
                    deleteFilesByType(java.util.Arrays.asList(existingActivity.getCoverImageId()), "封面图片");
                    existingActivity.setCoverImageId(null);
                }
                
                if (activityDTO.getUpdateBy() != null) {
                    existingActivity.setUpdateBy(activityDTO.getUpdateBy());
                }
                if (activityDTO.getReservationPriority() != null) {
                    existingActivity.setReservationPriority(activityDTO.getReservationPriority());
                }
                
                existingActivity.setUpdateTime(LocalDateTime.now());
                
                activityMapper.updateById(existingActivity);
                
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
    @Transactional
    public Result<String> deleteActivity(Long id) {
        try {
            Activity activity = activityMapper.selectById(id);
            if (activity != null) {
                // 删除关联的文件
                deleteActivityFiles(activity);
                
                // 物理删除活动记录
                activityMapper.deleteById(id);
                
                return Result.success("操作成功", "活动已删除");
            }
            return Result.error("活动不存在");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除活动关联的所有文件
     * @param activity 活动实体
     */
    private void deleteActivityFiles(Activity activity) {
        System.out.println("=== 开始删除活动关联的文件，ID: " + activity.getId() + " ===");
        
        try {
            // 删除详情内容中的图片
            if (activity.getContentImageIds() != null && !activity.getContentImageIds().isEmpty()) {
                deleteFilesByType(activity.getContentImageIds(), "详情内容图片");
            }
            
            // 删除封面图片
            if (activity.getCoverImageId() != null) {
                deleteFilesByType(java.util.Arrays.asList(activity.getCoverImageId()), "封面图片");
            }
            
        } catch (Exception e) {
            System.err.println("删除活动关联文件时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 根据文件ID列表删除文件
     * @param fileIds 文件ID列表
     * @param fileType 文件类型描述（用于日志）
     */
    private void deleteFilesByType(List<Long> fileIds, String fileType) {
        try {
            if (fileIds == null || fileIds.isEmpty()) {
                System.out.println("没有" + fileType + "文件需要删除");
                return;
            }
            
            System.out.println("=== 开始删除" + fileType + "文件 ===");
            System.out.println("文件ID列表: " + fileIds);
            
            // 先查询所有文件记录，用于删除MinIO中的文件
            for (Long fileId : fileIds) {
                try {
                    if (fileId == null) {
                        System.out.println("文件ID为null，跳过");
                        continue;
                    }
                    
                    // 使用MyBatis Mapper查询文件信息
                    ResourceFile resourceFile = resourceFileMapper.selectById(fileId);
                    
                    if (resourceFile != null) {
                        System.out.println("找到" + fileType + "文件记录: ID=" + resourceFile.getId() + ", 文件名=" + resourceFile.getFileName() + ", 文件Key=" + resourceFile.getFileKey());
                        
                        // 删除MinIO中的文件
                        try {
                            fileUploadUtil.removeObject(resourceFile.getBucketName(), resourceFile.getFileKey());
                            System.out.println("已删除MinIO中的" + fileType + "文件: " + resourceFile.getBucketName() + "/" + resourceFile.getFileKey());
                        } catch (Exception e) {
                            System.err.println("删除MinIO中的" + fileType + "文件失败: " + e.getMessage());
                        }
                    } else {
                        System.out.println("未找到ID为" + fileId + "的" + fileType + "文件记录");
                    }
                } catch (Exception e) {
                    System.err.println("处理" + fileType + "文件记录时发生异常: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // 批量删除数据库中的文件记录
            try {
                int result = resourceFileMapper.deleteByIds(fileIds);
                System.out.println("批量删除数据库中的" + fileType + "文件记录，数量: " + result);
            } catch (Exception e) {
                System.err.println("批量删除" + fileType + "文件记录时发生异常: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("删除" + fileType + "文件时发生异常: " + e.getMessage());
            e.printStackTrace();
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
     * 管理端 - 分页获取活动列表
     * @param title 活动标题（可选）
     * @param enabled 是否启用（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页活动列表
     */
    @Override
    public Result<Map<String, Object>> getActivityPage(String title, Integer enabled, Integer pageNum, Integer pageSize) {
        try {
            int offset = (pageNum - 1) * pageSize;
            
            // 查询活动列表
            List<Activity> activities = activityMapper.selectByEnabledStatus(title, enabled, offset, pageSize);
            
            // 查询总数
            int total = activityMapper.selectCountByEnabledStatus(title, enabled);
            
            // 转换为DTO
            List<ActivityDTO> activityDTOs = activities.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // 构造返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("total", total);
            result.put("list", activityDTOs);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);
            
            return Result.success("查询成功", result);
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
