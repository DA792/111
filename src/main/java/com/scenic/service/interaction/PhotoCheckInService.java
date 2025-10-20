package com.scenic.service.interaction;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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
     * @param photoCheckInQueryDTO 查询条件
     * @return 照片打卡记录列表
     */
    PageResult<PhotoCheckInVO> getAllPhotoCheckIns(PhotoCheckInQueryDTO photoCheckInQueryDTO);
    
    /**
     * 获取所有照片打卡记录（带用户互动状态）
     * @param photoCheckInQueryDTO 查询条件
     * @param userId 用户ID，用于判断互动状态
     * @return 照片打卡记录列表
     */
    PageResult<PhotoCheckInVO> getAllPhotoCheckIns(PhotoCheckInQueryDTO photoCheckInQueryDTO, Long userId);
    
    
    /**
     * 点赞照片打卡
     * @param photoCheckInId 照片打卡ID
     * @param userId 用户ID
     * @return 操作结果
     */
    Result<String> likePhotoCheckIn(Long photoCheckInId, Long userId);
    
    /**
     * 取消点赞照片打卡
     * @param photoCheckInId 照片打卡ID
     * @return 操作结果
     */
    Result<String> unlikePhotoCheckIn(Long photoCheckInId);
    
    /**
     * 收藏照片打卡
     * @param photoCheckInId 照片打卡ID
     * @param userId 用户ID
     * @return 操作结果
     */
    Result<String> favoritePhotoCheckIn(Long photoCheckInId, Long userId);
    
    /**
     * 取消收藏照片打卡
     * @param photoCheckInId 照片打卡ID
     * @param userId 用户ID
     * @return 操作结果
     */
    Result<String> unfavoritePhotoCheckIn(Long photoCheckInId, Long userId);
    
    /**
     * 获取用户收藏的照片打卡记录
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 照片打卡记录列表
     */
    PageResult<PhotoCheckInVO> getFavoritePhotoCheckIns(Long userId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取用户点赞的照片打卡记录
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 照片打卡记录列表
     */
    PageResult<PhotoCheckInVO> getLikedPhotoCheckIns(Long userId, Integer pageNum, Integer pageSize);
    
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
    
    /**
     * 管理后台端 - 新增照片打卡记录
     * @param title 标题
     * @param categoryId 分类ID
     * @param photo 照片文件
     * @return 操作结果
     */
    Result<String> addPhotoCheckInForAdmin(String title, Long categoryId, MultipartFile photo);
    
    /**
     * 管理后台端 - 更新照片打卡记录
     * @param id 照片打卡记录ID
     * @param title 标题
     * @param categoryId 分类ID
     * @param photo 照片文件（可选）
     * @return 操作结果
     */
    Result<String> updatePhotoCheckInForAdmin(Long id, String title, Long categoryId, MultipartFile photo);
    
    /**
     * 小程序端 - 根据用户ID和分类ID获取用户的发布打卡记录
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 照片打卡记录列表
     */
    PageResult<PhotoCheckInVO> getPhotoCheckInsByUserAndCategory(Long userId, Long categoryId, Integer pageNum, Integer pageSize);
    
    /**
     * 小程序端 - 根据用户ID和分类ID获取用户的发布打卡记录（带用户互动状态）
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param currentUserId 当前用户ID，用于判断互动状态
     * @return 照片打卡记录列表
     */
    PageResult<PhotoCheckInVO> getPhotoCheckInsByUserAndCategory(Long userId, Long categoryId, Integer pageNum, Integer pageSize, Long currentUserId);
}