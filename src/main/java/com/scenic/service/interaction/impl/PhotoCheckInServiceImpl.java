package com.scenic.service.interaction.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.interaction.PhotoCheckInDTO;
import com.scenic.dto.interaction.PhotoCheckInQueryDTO;
import com.scenic.entity.interaction.PhotoCheckIn;
import com.scenic.entity.interaction.vo.PhotoCheckInVO;
import com.scenic.entity.system.ResourceFile;
import com.scenic.mapper.interaction.PhotoCheckInMapper;
import com.scenic.mapper.system.ResourceFileMapper;
import com.scenic.service.interaction.PhotoCheckInService;

/**
 * 拍照打卡服务实现类
 */
@Service
public class PhotoCheckInServiceImpl implements PhotoCheckInService {
    
    @Autowired
    private PhotoCheckInMapper photoCheckInMapper;
    
    @Autowired
    private ResourceFileMapper resourceFileMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // Redis缓存键前缀
    private static final String PHOTO_CHECK_IN_CACHE_PREFIX = "photo_check_in:";
    private static final String ALL_PHOTOS_CACHE_KEY = "all_photos";
    private static final String PHOTOS_BY_CATEGORY_CACHE_PREFIX = "photos_category:";
    private static final String PHOTOS_BY_USER_ID_CACHE_PREFIX = "photos_user_id:";
    private static final String PHOTO_CHECK_IN_PAGE_CACHE_PREFIX = "photo_checkin:page:";
    
    // 缓存过期时间（分钟）
    private static final int PAGE_CACHE_EXPIRE_MINUTES = 5;
    private static final int EMPTY_RESULT_CACHE_EXPIRE_MINUTES = 2;
    
    /**
     * 上传照片打卡
     * @param photoCheckInDTO 照片打卡信息
     * @return 操作结果
     */
    @Override
    public Result<String> uploadPhotoCheckIn(PhotoCheckInDTO photoCheckInDTO) {
        return null;
    }
    
    /**
     * 获取所有照片打卡记录 - 管理员端分页查询
     * @param photoCheckInQueryDTO 查询条件
     * @return 照片打卡记录分页结果
     */
    @Override
    public PageResult<PhotoCheckInVO> getAllPhotoCheckIns(PhotoCheckInQueryDTO photoCheckInQueryDTO) {
        // 生成缓存键
        String cacheKey = generatePageCacheKey(photoCheckInQueryDTO);
        
        // 尝试从Redis获取缓存
        PageResult<PhotoCheckInVO> cachedResult = (PageResult<PhotoCheckInVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // 设置分页参数
        PageHelper.startPage(photoCheckInQueryDTO.getPageNum(), photoCheckInQueryDTO.getPageSize());
        
        // 构造查询参数
        String title = photoCheckInQueryDTO.getTitle();
        String userName = photoCheckInQueryDTO.getUserName();
        Long categoryId = photoCheckInQueryDTO.getCategoryId();
        LocalDateTime createTime = photoCheckInQueryDTO.getCreateTime();
        
        // 计算偏移量
        int offset = (photoCheckInQueryDTO.getPageNum() - 1) * photoCheckInQueryDTO.getPageSize();
        
        // 执行分页查询
        List<PhotoCheckIn> photoCheckIns = photoCheckInMapper.selectForAdmin(
            title, userName, categoryId, createTime, offset, photoCheckInQueryDTO.getPageSize()
        );
        
        // 查询总数
        int totalCount = photoCheckInMapper.selectCountForAdmin(title, userName, categoryId, createTime);
        
        // 转换为VO对象
        List<PhotoCheckInVO> photoCheckInVOs = photoCheckIns.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        // 构造分页结果
        PageResult<PhotoCheckInVO> pageResult = PageResult.of(totalCount, photoCheckInQueryDTO.getPageSize(), photoCheckInQueryDTO.getPageNum(), photoCheckInVOs);
        
        // 缓存结果
        if (photoCheckInVOs.isEmpty()) {
            // 空结果缓存较短时间
            redisTemplate.opsForValue().set(cacheKey, pageResult, EMPTY_RESULT_CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        } else {
            // 非空结果缓存较长时间
            redisTemplate.opsForValue().set(cacheKey, pageResult, PAGE_CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        }
        
        return pageResult;
    }
    

    /**
     * 点赞照片打卡
     * @param photoCheckInId 照片打卡ID
     * @return 操作结果
     */
    @Override
    public Result<String> likePhotoCheckIn(Long photoCheckInId) {
        try {
            PhotoCheckIn photo = photoCheckInMapper.selectById(photoCheckInId);
            if (photo != null) {
                photo.setLikeCount(photo.getLikeCount() + 1);
                photo.setUpdateTime(LocalDateTime.now());
                photoCheckInMapper.updateById(photo);
                
                // 更新缓存
                String cacheKey = PHOTO_CHECK_IN_CACHE_PREFIX + photoCheckInId;
                PhotoCheckInDTO updatedDTO = convertToDTO(photo);
                redisTemplate.opsForValue().set(cacheKey, updatedDTO, 1, TimeUnit.HOURS);
                
                // 清除相关缓存
                redisTemplate.delete(ALL_PHOTOS_CACHE_KEY);
                redisTemplate.delete(PHOTOS_BY_CATEGORY_CACHE_PREFIX + photo.getCategoryId());
                redisTemplate.delete(PHOTOS_BY_USER_ID_CACHE_PREFIX + photo.getUserId());
                // 清除分页缓存
                clearPageCaches();
                
                return Result.success("操作成功", "点赞成功");
            }
            return Result.error("照片不存在");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 取消点赞照片打卡
     * @param photoCheckInId 照片打卡ID
     * @return 操作结果
     */
    @Override
    public Result<String> unlikePhotoCheckIn(Long photoCheckInId) {
        try {
            PhotoCheckIn photo = photoCheckInMapper.selectById(photoCheckInId);
            if (photo != null) {
                if (photo.getLikeCount() > 0) {
                    photo.setLikeCount(photo.getLikeCount() - 1);
                    photo.setUpdateTime(LocalDateTime.now());
                    photoCheckInMapper.updateById(photo);
                    
                    // 更新缓存
                    String cacheKey = PHOTO_CHECK_IN_CACHE_PREFIX + photoCheckInId;
                    PhotoCheckInDTO updatedDTO = convertToDTO(photo);
                    redisTemplate.opsForValue().set(cacheKey, updatedDTO, 1, TimeUnit.HOURS);
                    
                    // 清除相关缓存
                    redisTemplate.delete(ALL_PHOTOS_CACHE_KEY);
                    redisTemplate.delete(PHOTOS_BY_CATEGORY_CACHE_PREFIX + photo.getCategoryId());
                    redisTemplate.delete(PHOTOS_BY_USER_ID_CACHE_PREFIX + photo.getUserId());
                    // 清除分页缓存
                    clearPageCaches();
                    
                    return Result.success("操作成功", "取消点赞成功");
                } else {
                    return Result.error("点赞数已为0");
                }
            }
            return Result.error("照片不存在");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除照片打卡记录
     * @param photoCheckInId 照片打卡ID
     * @return 操作结果
     */
    @Override
    public Result<String> deletePhotoCheckIn(Long photoCheckInId) {
        try {
            PhotoCheckIn photo = photoCheckInMapper.selectById(photoCheckInId);
            if (photo != null) {
                photoCheckInMapper.deleteById(photoCheckInId);
                
                // 删除缓存
                String cacheKey = PHOTO_CHECK_IN_CACHE_PREFIX + photoCheckInId;
                redisTemplate.delete(cacheKey);
                
                // 清除相关缓存
                redisTemplate.delete(ALL_PHOTOS_CACHE_KEY);
                redisTemplate.delete(PHOTOS_BY_CATEGORY_CACHE_PREFIX + photo.getCategoryId());
                redisTemplate.delete(PHOTOS_BY_USER_ID_CACHE_PREFIX + photo.getUserId());
                // 清除分页缓存
                clearPageCaches();
                
                return Result.success("操作成功", "照片已删除");
            }
            return Result.error("照片不存在");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
    
    
    /**
     * 将PhotoCheckIn实体转换为PhotoCheckInDTO
     * @param photo PhotoCheckIn实体
     * @return PhotoCheckInDTO
     */
    private PhotoCheckInDTO convertToDTO(PhotoCheckIn photo) {
        PhotoCheckInDTO dto = new PhotoCheckInDTO();
        BeanUtils.copyProperties(photo, dto);
        return dto;
    }
    
    /**
     * 将PhotoCheckIn实体转换为PhotoCheckInVO
     * @param photo PhotoCheckIn实体
     * @return PhotoCheckInVO
     */
    private PhotoCheckInVO convertToVO(PhotoCheckIn photo) {
        PhotoCheckInVO vo = new PhotoCheckInVO();
        BeanUtils.copyProperties(photo, vo);
        // 特别处理photoId字段，根据photoId获取文件路径
        Long photoId = photo.getPhotoId();
        if (photoId != null) {
            ResourceFile resourceFile = resourceFileMapper.selectById(photoId);
            if (resourceFile != null) {
                vo.setPhotoPath(resourceFile.getFileKey());
            }
        }
        // 设置分类名称
        vo.setCategoryName(photo.getCategoryName());
        return vo;
    }

    /**
     * 生成分页查询缓存键
     * @param queryDTO 查询条件
     * @return 缓存键
     */
    private String generatePageCacheKey(PhotoCheckInQueryDTO queryDTO) {
        StringBuilder key = new StringBuilder(PHOTO_CHECK_IN_PAGE_CACHE_PREFIX);
        key.append(queryDTO.getPageNum()).append(":")
           .append(queryDTO.getPageSize()).append(":")
           .append("title:").append(queryDTO.getTitle() != null ? queryDTO.getTitle() : "").append(":")
           .append("user:").append(queryDTO.getUserName() != null ? queryDTO.getUserName() : "").append(":")
           .append("category:").append(queryDTO.getCategoryId() != null ? queryDTO.getCategoryId() : "").append(":")
           .append("createTime:").append(queryDTO.getCreateTime() != null ? queryDTO.getCreateTime().toString() : "");
        return key.toString();
    }
    
    /**
     * 清除分页缓存
     * 使用模式匹配清除所有分页相关的缓存
     */
    private void clearPageCaches() {
        // 清除所有分页缓存
        redisTemplate.delete(PHOTO_CHECK_IN_PAGE_CACHE_PREFIX + "*");
    }

    @Override
    public Result<String> updatePhotoCheckInCategory(Long photoCheckInId, String category) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatePhotoCheckInCategory'");
    }
}
