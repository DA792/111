package com.scenic.service.interaction.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.scenic.common.dto.Result;
import com.scenic.dto.interaction.PhotoCheckInDTO;
import com.scenic.entity.interaction.PhotoCheckIn;
import com.scenic.mapper.interaction.PhotoCheckInMapper;
import com.scenic.service.interaction.PhotoCheckInService;

/**
 * 拍照打卡服务实现类
 */
@Service
public class PhotoCheckInServiceImpl implements PhotoCheckInService {
    
    @Autowired
    private PhotoCheckInMapper photoCheckInMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // Redis缓存键前缀
    private static final String PHOTO_CHECK_IN_CACHE_PREFIX = "photo_check_in:";
    private static final String ALL_PHOTOS_CACHE_KEY = "all_photos";
    private static final String PHOTOS_BY_CATEGORY_CACHE_PREFIX = "photos_category:";
    private static final String PHOTOS_BY_USER_ID_CACHE_PREFIX = "photos_user_id:";
    
    /**
     * 上传照片打卡
     * @param photoCheckInDTO 照片打卡信息
     * @return 操作结果
     */
    @Override
    public Result<String> uploadPhotoCheckIn(PhotoCheckInDTO photoCheckInDTO) {
        try {
            PhotoCheckIn photo = new PhotoCheckIn();
            photo.setUserId(photoCheckInDTO.getUserId());
            photo.setUserName(photoCheckInDTO.getUserName());
            photo.setDescription(photoCheckInDTO.getDescription());
            photo.setCategory(photoCheckInDTO.getCategory());
            photo.setLikes(0);
            photo.setLatitude(photoCheckInDTO.getLatitude());
            photo.setLongitude(photoCheckInDTO.getLongitude());
            photo.setPhotoUrl(photoCheckInDTO.getPhotoUrl());
            photo.setCreateTime(LocalDateTime.now());
            photo.setUpdateTime(LocalDateTime.now());
            
            photoCheckInMapper.insert(photo);
            
            // 清除相关缓存
            redisTemplate.delete(ALL_PHOTOS_CACHE_KEY);
            redisTemplate.delete(PHOTOS_BY_CATEGORY_CACHE_PREFIX + photoCheckInDTO.getCategory());
            redisTemplate.delete(PHOTOS_BY_USER_ID_CACHE_PREFIX + photoCheckInDTO.getUserId());
            
            return Result.success("操作成功", "照片上传成功");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取所有照片打卡记录
     * @return 照片打卡记录列表
     */
    @Override
    public Result<List<PhotoCheckInDTO>> getAllPhotoCheckIns() {
        try {
            // 先从Redis缓存中获取
            List<PhotoCheckInDTO> cachedPhotos = (List<PhotoCheckInDTO>) redisTemplate.opsForValue().get(ALL_PHOTOS_CACHE_KEY);
            if (cachedPhotos != null) {
                return Result.success("查询成功", cachedPhotos);
            }
            
            // 缓存中没有则从数据库查询
            List<PhotoCheckIn> photos = photoCheckInMapper.selectAll();
            List<PhotoCheckInDTO> photoDTOs = photos.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // 将结果存入Redis缓存，过期时间1小时
            redisTemplate.opsForValue().set(ALL_PHOTOS_CACHE_KEY, photoDTOs, 1, TimeUnit.HOURS);
            
            return Result.success("查询成功", photoDTOs);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据分类获取照片打卡记录
     * @param category 分类
     * @return 照片打卡记录列表
     */
    @Override
    public Result<List<PhotoCheckInDTO>> getPhotoCheckInsByCategory(String category) {
        try {
            // 先从Redis缓存中获取
            String cacheKey = PHOTOS_BY_CATEGORY_CACHE_PREFIX + category;
            List<PhotoCheckInDTO> cachedPhotos = (List<PhotoCheckInDTO>) redisTemplate.opsForValue().get(cacheKey);
            if (cachedPhotos != null) {
                return Result.success("查询成功", cachedPhotos);
            }
            
            // 缓存中没有则从数据库查询
            List<PhotoCheckIn> photos = photoCheckInMapper.selectByCategory(category, 0, 1000);
            List<PhotoCheckInDTO> photoDTOs = photos.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // 将结果存入Redis缓存，过期时间1小时
            redisTemplate.opsForValue().set(cacheKey, photoDTOs, 1, TimeUnit.HOURS);
            
            return Result.success("查询成功", photoDTOs);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据用户ID获取照片打卡记录
     * @param userId 用户ID
     * @return 照片打卡记录列表
     */
    @Override
    public Result<List<PhotoCheckInDTO>> getPhotoCheckInsByUserId(Long userId) {
        try {
            // 先从Redis缓存中获取
            String cacheKey = PHOTOS_BY_USER_ID_CACHE_PREFIX + userId;
            List<PhotoCheckInDTO> cachedPhotos = (List<PhotoCheckInDTO>) redisTemplate.opsForValue().get(cacheKey);
            if (cachedPhotos != null) {
                return Result.success("查询成功", cachedPhotos);
            }
            
            // 缓存中没有则从数据库查询
            List<PhotoCheckIn> photos = photoCheckInMapper.selectByUserId(userId, 0, 1000);
            List<PhotoCheckInDTO> photoDTOs = photos.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // 将结果存入Redis缓存，过期时间1小时
            redisTemplate.opsForValue().set(cacheKey, photoDTOs, 1, TimeUnit.HOURS);
            
            return Result.success("查询成功", photoDTOs);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
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
                photo.setLikes(photo.getLikes() + 1);
                photo.setUpdateTime(LocalDateTime.now());
                photoCheckInMapper.updateById(photo);
                
                // 更新缓存
                String cacheKey = PHOTO_CHECK_IN_CACHE_PREFIX + photoCheckInId;
                PhotoCheckInDTO updatedDTO = convertToDTO(photo);
                redisTemplate.opsForValue().set(cacheKey, updatedDTO, 1, TimeUnit.HOURS);
                
                // 清除相关缓存
                redisTemplate.delete(ALL_PHOTOS_CACHE_KEY);
                redisTemplate.delete(PHOTOS_BY_CATEGORY_CACHE_PREFIX + photo.getCategory());
                redisTemplate.delete(PHOTOS_BY_USER_ID_CACHE_PREFIX + photo.getUserId());
                
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
                if (photo.getLikes() > 0) {
                    photo.setLikes(photo.getLikes() - 1);
                    photo.setUpdateTime(LocalDateTime.now());
                    photoCheckInMapper.updateById(photo);
                    
                    // 更新缓存
                    String cacheKey = PHOTO_CHECK_IN_CACHE_PREFIX + photoCheckInId;
                    PhotoCheckInDTO updatedDTO = convertToDTO(photo);
                    redisTemplate.opsForValue().set(cacheKey, updatedDTO, 1, TimeUnit.HOURS);
                    
                    // 清除相关缓存
                    redisTemplate.delete(ALL_PHOTOS_CACHE_KEY);
                    redisTemplate.delete(PHOTOS_BY_CATEGORY_CACHE_PREFIX + photo.getCategory());
                    redisTemplate.delete(PHOTOS_BY_USER_ID_CACHE_PREFIX + photo.getUserId());
                    
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
                redisTemplate.delete(PHOTOS_BY_CATEGORY_CACHE_PREFIX + photo.getCategory());
                redisTemplate.delete(PHOTOS_BY_USER_ID_CACHE_PREFIX + photo.getUserId());
                
                return Result.success("操作成功", "照片已删除");
            }
            return Result.error("照片不存在");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 修改照片打卡分类
     * @param photoCheckInId 照片打卡ID
     * @param category 新分类
     * @return 操作结果
     */
    @Override
    public Result<String> updatePhotoCheckInCategory(Long photoCheckInId, String category) {
        try {
            PhotoCheckIn photo = photoCheckInMapper.selectById(photoCheckInId);
            if (photo != null) {
                // 获取更新前的分类
                String oldCategory = photo.getCategory();
                
                photo.setCategory(category);
                photo.setUpdateTime(LocalDateTime.now());
                photoCheckInMapper.updateById(photo);
                
                // 更新缓存
                String cacheKey = PHOTO_CHECK_IN_CACHE_PREFIX + photoCheckInId;
                PhotoCheckInDTO updatedDTO = convertToDTO(photo);
                redisTemplate.opsForValue().set(cacheKey, updatedDTO, 1, TimeUnit.HOURS);
                
                // 清除相关缓存
                redisTemplate.delete(ALL_PHOTOS_CACHE_KEY);
                redisTemplate.delete(PHOTOS_BY_CATEGORY_CACHE_PREFIX + oldCategory);
                redisTemplate.delete(PHOTOS_BY_CATEGORY_CACHE_PREFIX + category);
                redisTemplate.delete(PHOTOS_BY_USER_ID_CACHE_PREFIX + photo.getUserId());
                
                return Result.success("操作成功", "分类更新成功");
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
        dto.setId(photo.getId());
        dto.setUserId(photo.getUserId());
        dto.setUserName(photo.getUserName());
        dto.setDescription(photo.getDescription());
        dto.setCategory(photo.getCategory());
        dto.setLikes(photo.getLikes());
        dto.setLatitude(photo.getLatitude());
        dto.setLongitude(photo.getLongitude());
        dto.setPhotoUrl(photo.getPhotoUrl());
        dto.setCreateTime(photo.getCreateTime());
        dto.setUpdateTime(photo.getUpdateTime());
        return dto;
    }
}
