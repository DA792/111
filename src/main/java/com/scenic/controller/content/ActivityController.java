package com.scenic.controller.content;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.scenic.common.dto.Result;
import com.scenic.dto.content.ActivityDTO;
import com.scenic.service.content.ActivityService;
import com.scenic.utils.FileUploadUtil;

/**
 * 活动控制器
 * 提供小程序端和管理后台端的API接口
 */
@RestController
@RequestMapping("/api")
public class ActivityController {
    
    // 小程序端API接口前缀
    private static final String MINIAPP_PREFIX = "/uniapp";
    
    // 管理后台端API接口前缀
    private static final String ADMIN_PREFIX = "/manage";
    
    @Autowired
    private ActivityService activityService;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;
    
    /**
     * 小程序端 - 获取所有活动
     * @return 活动列表
     */
    @GetMapping(MINIAPP_PREFIX + "/activity/list")
    public Result<List<ActivityDTO>> getAllActivitiesForMiniapp() {
        return activityService.getAllActivities();
    }
    
    /**
     * 小程序端 - 根据ID获取活动详情
     * @param id 活动ID
     * @return 活动详情
     */
    @GetMapping(MINIAPP_PREFIX + "/activity/detail/{id}")
    public Result<ActivityDTO> getActivityById(@PathVariable Long id) {
        return activityService.getActivityById(id);
    }
    
    /**
     * 管理后台端 - 新增活动
     * @param file 活动图片文件
     * @param activityDTO 活动信息
     * @return 操作结果
     */
    @PostMapping(ADMIN_PREFIX + "/activity/add")
    public Result<String> addActivity(@RequestParam(value = "imageFile", required = false) MultipartFile file,
                                      @RequestBody ActivityDTO activityDTO) {
        try {
            // 处理文件上传
            if (file != null && !file.isEmpty()) {
                String imageUrl = fileUploadUtil.uploadFile(file);
                activityDTO.setCoverImageId(Long.valueOf(imageUrl)); // 这里需要根据实际情况调整
            }
            
            return activityService.addActivity(activityDTO);
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            return Result.error("新增失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理后台端 - 获取所有活动
     * @return 活动列表
     */
    @GetMapping(ADMIN_PREFIX + "/activity/list")
    public Result<List<ActivityDTO>> getAllActivitiesForAdmin() {
        return activityService.getAllActivities();
    }
    
    /**
     * 管理后台端 - 根据ID获取活动详情
     * @param id 活动ID
     * @return 活动详情
     */
    @GetMapping(ADMIN_PREFIX + "/activity/detail/{id}")
    public Result<ActivityDTO> getActivityByIdForAdmin(@PathVariable Long id) {
        return activityService.getActivityById(id);
    }
    
    /**
     * 管理后台端 - 分页查询活动列表
     * @param title 活动标题（可选）
     * @param status 活动状态（可选）
     * @param startTime 开始时间（可选）
     * @param suitableCrowd 适合人群（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 活动列表
     */
    @GetMapping(ADMIN_PREFIX + "/activity/page")
    public Result<List<ActivityDTO>> getActivityList(@RequestParam(required = false) String title,
                                                     @RequestParam(required = false) Byte status,
                                                     @RequestParam(required = false) String startTime,
                                                     @RequestParam(required = false) String suitableCrowd,
                                                     @RequestParam(defaultValue = "1") int pageNum,
                                                     @RequestParam(defaultValue = "10") int pageSize) {
        return activityService.getActivityList(title, status, startTime, suitableCrowd, pageNum, pageSize);
    }
    
    /**
     * 管理后台端 - 获取活动总数
     * @param title 活动标题（可选）
     * @param status 活动状态（可选）
     * @param startTime 开始时间（可选）
     * @param suitableCrowd 适合人群（可选）
     * @return 活动总数
     */
    @GetMapping(ADMIN_PREFIX + "/activity/count")
    public Result<Integer> getActivityCount(@RequestParam(required = false) String title,
                                            @RequestParam(required = false) Byte status,
                                            @RequestParam(required = false) String startTime,
                                            @RequestParam(required = false) String suitableCrowd) {
        return activityService.getActivityCount(title, status, startTime, suitableCrowd);
    }
    
    /**
     * 管理后台端 - 更新活动
     * @param id 活动ID
     * @param activityDTO 活动信息
     * @return 操作结果
     */
    @PutMapping(ADMIN_PREFIX + "/activity/update/{id}")
    public Result<String> updateActivity(@PathVariable Long id, @RequestBody ActivityDTO activityDTO) {
        return activityService.updateActivity(id, activityDTO);
    }
    
    /**
     * 管理后台端 - 删除活动
     * @param id 活动ID
     * @return 操作结果
     */
    @DeleteMapping(ADMIN_PREFIX + "/activity/delete/{id}")
    public Result<String> deleteActivity(@PathVariable Long id) {
        return activityService.deleteActivity(id);
    }
}