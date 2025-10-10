package com.scenic.service.interaction;

import java.util.List;

import com.scenic.common.dto.Result;
import com.scenic.dto.interaction.PhotoCheckInDTO;

/**
 * 拍照打卡服务接口
 */
public interface PhotoCheckInService {
    
    /**
     * 上传照片打卡
     * @param photoCheckInDTO 照片打卡信息
     * @return 操作结果
     */
    Result<String> uploadPhotoCheckIn(PhotoCheckInDTO photoCheckInDTO);
    
    /**
     * 获取所有照片打卡记录
     * @return 照片打卡记录列表
     */
    Result<List<PhotoCheckInDTO>> getAllPhotoCheckIns();
    
    /**
     * 根据分类获取照片打卡记录
     * @param category 分类
     * @return 照片打卡记录列表
     */
    Result<List<PhotoCheckInDTO>> getPhotoCheckInsByCategory(String category);
    
    /**
     * 根据用户ID获取照片打卡记录
     * @param userId 用户ID
     * @return 照片打卡记录列表
     */
    Result<List<PhotoCheckInDTO>> getPhotoCheckInsByUserId(Long userId);
    
    /**
     * 点赞照片打卡
     * @param photoCheckInId 照片打卡ID
     * @return 操作结果
     */
    Result<String> likePhotoCheckIn(Long photoCheckInId);
    
    /**
     * 取消点赞照片打卡
     * @param photoCheckInId 照片打卡ID
     * @return 操作结果
     */
    Result<String> unlikePhotoCheckIn(Long photoCheckInId);
    
    /**
     * 管理端 - 删除照片打卡记录
     * @param photoCheckInId 照片打卡ID
     * @return 操作结果
     */
    Result<String> deletePhotoCheckIn(Long photoCheckInId);
    
    /**
     * 管理端 - 修改照片打卡分类
     * @param photoCheckInId 照片打卡ID
     * @param category 新分类
     * @return 操作结果
     */
    Result<String> updatePhotoCheckInCategory(Long photoCheckInId, String category);
}
