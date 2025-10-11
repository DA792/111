package com.scenic.controller.interaction;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.scenic.common.dto.Result;
import com.scenic.dto.interaction.PhotoCheckInDTO;
import com.scenic.service.interaction.PhotoCheckInService;
import com.scenic.utils.FileUploadUtil;

/**
 * 照片打卡控制器
 * 提供小程序端和管理后台端的API接口
 */
@RestController
@RequestMapping("/api")
public class PhotoCheckInController {
    
    // 小程序端API接口前缀
    private static final String MINIAPP_PREFIX = "/uniapp";
    
    // 管理后台端API接口前缀
    private static final String ADMIN_PREFIX = "/manage";
    
    @Autowired
    private PhotoCheckInService photoCheckInService;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;
    
    /**
     * 小程序端 - 上传照片打卡
     * @param photo 照片文件
     * @param description 描述
     * @param category 分类
     * @param latitude 纬度
     * @param longitude 经度
     * @return 操作结果
     */
    @PostMapping(MINIAPP_PREFIX + "/photo-check-in/upload")
    public Result<String> uploadPhotoCheckIn(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("userId") Long userId,
            @RequestParam("userName") String userName,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("category") String category,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude) {
        
        try {
            // 处理文件上传
            String photoUrl = fileUploadUtil.uploadFile(photo);
            
            PhotoCheckInDTO photoCheckInDTO = new PhotoCheckInDTO();
            photoCheckInDTO.setUserId(userId);
            photoCheckInDTO.setUserName(userName);
            photoCheckInDTO.setPhotoUrl(photoUrl);
            photoCheckInDTO.setDescription(description);
            photoCheckInDTO.setCategory(category);
            photoCheckInDTO.setLatitude(latitude);
            photoCheckInDTO.setLongitude(longitude);
            
            return photoCheckInService.uploadPhotoCheckIn(photoCheckInDTO);
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 小程序端 - 获取所有照片打卡记录
     * @return 照片打卡记录列表
     */
    @GetMapping(MINIAPP_PREFIX + "/photo-check-in/list")
    public Result<List<PhotoCheckInDTO>> getAllPhotoCheckInsForMiniapp() {
        return photoCheckInService.getAllPhotoCheckIns();
    }
    
    /**
     * 小程序端 - 根据分类获取照片打卡记录
     * @param category 分类
     * @return 照片打卡记录列表
     */
    @GetMapping(MINIAPP_PREFIX + "/photo-check-in/category/{category}")
    public Result<List<PhotoCheckInDTO>> getPhotoCheckInsByCategoryForMiniapp(@PathVariable String category) {
        return photoCheckInService.getPhotoCheckInsByCategory(category);
    }
    
    /**
     * 小程序端 - 点赞照片打卡
     * @param photoCheckInId 照片打卡ID
     * @return 操作结果
     */
    @PostMapping(MINIAPP_PREFIX + "/photo-check-in/like/{photoCheckInId}")
    public Result<String> likePhotoCheckIn(@PathVariable Long photoCheckInId) {
        return photoCheckInService.likePhotoCheckIn(photoCheckInId);
    }
    
    /**
     * 小程序端 - 取消点赞照片打卡
     * @param photoCheckInId 照片打卡ID
     * @return 操作结果
     */
    @PostMapping(MINIAPP_PREFIX + "/photo-check-in/unlike/{photoCheckInId}")
    public Result<String> unlikePhotoCheckIn(@PathVariable Long photoCheckInId) {
        return photoCheckInService.unlikePhotoCheckIn(photoCheckInId);
    }
    
    /**
     * 管理后台端 - 获取所有照片打卡记录
     * @return 照片打卡记录列表
     */
    @GetMapping(ADMIN_PREFIX + "/photo-check-in/list")
    public Result<List<PhotoCheckInDTO>> getAllPhotoCheckInsForAdmin() {
        return photoCheckInService.getAllPhotoCheckIns();
    }
    
    /**
     * 管理后台端 - 删除照片打卡记录
     * @param photoCheckInId 照片打卡ID
     * @return 操作结果
     */
    @DeleteMapping(ADMIN_PREFIX + "/photo-check-in/delete/{photoCheckInId}")
    public Result<String> deletePhotoCheckIn(@PathVariable Long photoCheckInId) {
        return photoCheckInService.deletePhotoCheckIn(photoCheckInId);
    }
    
    /**
     * 管理后台端 - 修改照片打卡分类
     * @param photoCheckInId 照片打卡ID
     * @param category 新分类
     * @return 操作结果
     */
    @PutMapping(ADMIN_PREFIX + "/photo-check-in/update-category/{photoCheckInId}")
    public Result<String> updatePhotoCheckInCategory(
            @PathVariable Long photoCheckInId,
            @RequestParam("category") String category) {
        return photoCheckInService.updatePhotoCheckInCategory(photoCheckInId, category);
    }
}