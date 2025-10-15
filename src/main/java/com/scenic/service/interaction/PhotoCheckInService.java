package com.scenic.service.interaction;

import java.util.List;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.interaction.PhotoCheckInDTO;
import com.scenic.dto.interaction.PhotoCheckInQueryDTO;
import com.scenic.entity.interaction.vo.CheckinCategoryVO;
import com.scenic.entity.interaction.vo.PhotoCheckInVO;

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
    PageResult<PhotoCheckInVO> getAllPhotoCheckIns(PhotoCheckInQueryDTO photoCheckInQueryDTO);
    
    
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
     * 获取打卡分类列表
     * @return 分类列表
     */
    Result<List<CheckinCategoryVO>> getCategoryList();
    
    /**
     * 删除打卡分类（软删除）
     * @param categoryId 分类ID
     * @return 操作结果
     */
    Result<String> deleteCategory(Long categoryId);
    
    /**
     * 新增打卡分类
     * @param categoryName 分类名称
     * @return 操作结果
     */
    Result<String> addCategory(String categoryName);
    
    /**
     * 管理后台端 - 获取当前照片打卡记录详情
     * @param photoCheckInId 照片打卡ID
     * @return 照片打卡记录详情
     */
    Result<PhotoCheckInVO> getPhotoCheckInsInfoForAdmin(Long photoCheckInId);
}