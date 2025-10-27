package com.scenic.service.interaction.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageHelper;
import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.interaction.PhotoCheckInDTO;
import com.scenic.dto.interaction.PhotoCheckInQueryDTO;
import com.scenic.entity.interaction.CheckinCategory;
import com.scenic.entity.interaction.PhotoCheckIn;
import com.scenic.entity.interaction.vo.CheckinCategoryVO;
import com.scenic.entity.interaction.vo.PhotoCheckInVO;
import com.scenic.entity.ResourceFile;
import com.scenic.entity.user.UserFavorite;
import com.scenic.mapper.interaction.CheckinCategoryMapper;
import com.scenic.mapper.interaction.PhotoCheckInMapper;
import com.scenic.mapper.ResourceFileMapper;
import com.scenic.mapper.user.UserMapper;
import com.scenic.mapper.user.UserFavoriteMapper;
import com.scenic.service.MinioService;
import com.scenic.service.interaction.PhotoCheckInService;
import com.scenic.utils.BloomFilterUtil;
import com.scenic.utils.IdGenerator;
import com.scenic.utils.UserContextUtil;
import com.scenic.utils.UserInteractionCacheUtil;

/**
 * 拍照打卡服务实现类
 */
@Service
public class PhotoCheckInServiceImpl implements PhotoCheckInService {
    
    @Autowired
    private PhotoCheckInMapper photoCheckInMapper;
    
    @Autowired
    private CheckinCategoryMapper checkinCategoryMapper;
    
    @Autowired
    private ResourceFileMapper resourceFileMapper;
    
    @Autowired
    private MinioService minioService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private UserContextUtil userContextUtil;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserFavoriteMapper userFavoriteMapper;
    
    @Autowired
    private BloomFilterUtil bloomFilterUtil;
    
    @Autowired
    private UserInteractionCacheUtil userInteractionCacheUtil;
    
    @Autowired
    private IdGenerator idGenerator;
    
    // Redis缓存键前缀
    private static final String PHOTO_CHECK_IN_CACHE_PREFIX = "photo_check_in:";
    private static final String ALL_PHOTOS_CACHE_KEY = "all_photos";
    private static final String PHOTOS_BY_CATEGORY_CACHE_PREFIX = "photos_category:";
    private static final String PHOTOS_BY_USER_ID_CACHE_PREFIX = "photos_user_id:";
    private static final String PHOTO_CHECK_IN_PAGE_CACHE_PREFIX = "photo_checkin:page:";
    private static final String CHECKIN_CATEGORY_LIST_CACHE_KEY = "checkin_category:list";
    
    // 缓存过期时间（分钟）
    private static final int PAGE_CACHE_EXPIRE_MINUTES = 1; // 减少页面缓存过期时间，使新增或编辑的数据更快显示
    private static final int EMPTY_RESULT_CACHE_EXPIRE_MINUTES = 2;
    private static final int CATEGORY_LIST_CACHE_EXPIRE_MINUTES = 10; // 减少分类列表缓存过期时间
    
    /**
     * 上传照片打卡
     * @param photoCheckInDTO 照片打卡信息
     * @return 操作结果
     */
    @Override
    public Result<String> uploadPhotoCheckIn(PhotoCheckInDTO photoCheckInDTO) {
        try {
            // 参数验证
            if (photoCheckInDTO.getUserId() == null) {
                return Result.error("用户ID不能为空");
            }
            if (photoCheckInDTO.getUserName() == null || photoCheckInDTO.getUserName().trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            if (photoCheckInDTO.getPhotoUrl() == null || photoCheckInDTO.getPhotoUrl().trim().isEmpty()) {
                return Result.error("照片URL不能为空");
            }
            // 验证分类ID
            if (photoCheckInDTO.getCategoryId() == null) {
                return Result.error("分类ID不能为空");
            }
            
            // 验证分类是否存在且启用
            CheckinCategory category = checkinCategoryMapper.selectById(photoCheckInDTO.getCategoryId());
            if (category == null || category.getStatus() != 1) {
                return Result.error("指定的分类不存在或已禁用");
            }
            
            // 从照片URL中提取文件名和文件键
            String photoUrl = photoCheckInDTO.getPhotoUrl();
            // 提取基本文件名（去除查询参数）
            String fullFileName = photoUrl.substring(photoUrl.lastIndexOf("/") + 1);
            String fileName = fullFileName;
            // 如果文件名包含查询参数，只保留文件名部分
            if (fileName.contains("?")) {
                fileName = fileName.substring(0, fileName.indexOf("?"));
            }
            // 确保文件名不超过数据库字段长度限制（假设为255）
            if (fileName.length() > 250) {
                fileName = fileName.substring(0, 250);
            }
            String fileKey = fileName;
            
            // 检查文件是否已存在
            ResourceFile existingFile = resourceFileMapper.selectByFileKey(fileKey);
            Long photoId;
            
            if (existingFile != null) {
                // 文件已存在，使用现有记录
                photoId = existingFile.getId();
            } else {
                // 创建新的文件记录
                ResourceFile resourceFile = new ResourceFile();
                // 使用雪花算法生成ID
                resourceFile.setId(idGenerator.nextId());
                resourceFile.setFileName(fileName);
                resourceFile.setFileKey(fileKey);
                resourceFile.setBucketName("photo-checkin");
                resourceFile.setFileSize(0L); // 文件大小暂时设为0
                resourceFile.setMimeType("image/jpeg"); // 默认MIME类型
                resourceFile.setFileType(1); // 1表示图片
                resourceFile.setCreateTime(LocalDateTime.now());
                resourceFile.setUpdateTime(LocalDateTime.now());
                resourceFile.setCreateBy(photoCheckInDTO.getUserId());
                resourceFile.setUpdateBy(photoCheckInDTO.getUserId());
                resourceFile.setUploadUserId(photoCheckInDTO.getUserId());
                
                resourceFileMapper.insert(resourceFile);
                photoId = resourceFile.getId();
            }
            
            // 使用传入的分类ID
            Long categoryId = photoCheckInDTO.getCategoryId();
            
            // 创建照片打卡记录
            PhotoCheckIn photoCheckIn = new PhotoCheckIn();
            // 使用雪花算法生成ID
            photoCheckIn.setId(idGenerator.nextId());
            photoCheckIn.setUserId(photoCheckInDTO.getUserId());
            photoCheckIn.setUserName(photoCheckInDTO.getUserName());
            photoCheckIn.setTitle(photoCheckInDTO.getTitle() != null ? photoCheckInDTO.getTitle() : "照片打卡");
            photoCheckIn.setContent(photoCheckInDTO.getTitle() != null ? photoCheckInDTO.getTitle() : "");
            photoCheckIn.setCategoryId(categoryId);
            photoCheckIn.setPhotoId(photoId);
            photoCheckIn.setLikeCount(0);
            photoCheckIn.setViewCount(0);
            photoCheckIn.setStatus(1); // 启用状态
            photoCheckIn.setVersion(0);
            photoCheckIn.setDeleted(false);
            photoCheckIn.setCreateTime(LocalDateTime.now());
            photoCheckIn.setUpdateTime(LocalDateTime.now());
            photoCheckIn.setCreateBy(photoCheckInDTO.getUserId());
            photoCheckIn.setUpdateBy(photoCheckInDTO.getUserId());
            
            // 设置用户头像为用户的头像文件ID
            try {
                com.scenic.entity.user.User user = userMapper.selectById(photoCheckInDTO.getUserId());
                if (user != null && user.getAvatarFileId() != null) {
                    // 直接存储头像文件ID，而不是URL
                    photoCheckIn.setUserAvatar(user.getAvatarFileId().toString());
                }
            } catch (Exception e) {
                System.err.println("获取用户头像ID失败：" + e.getMessage());
            }
            
            // 保存到数据库
            photoCheckInMapper.insert(photoCheckIn);
            
            // 清除相关缓存
            redisTemplate.delete(ALL_PHOTOS_CACHE_KEY);
            redisTemplate.delete(CHECKIN_CATEGORY_LIST_CACHE_KEY);
            // 清除分页缓存
            clearPageCaches();
            
            return Result.success("操作成功", "照片打卡上传成功，ID: " + photoCheckIn.getId());
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 收藏照片打卡
     * @param photoCheckInId 照片打卡ID
     * @param userId 用户ID
     * @return 操作结果
     */
    @Override
    public Result<String> favoritePhotoCheckIn(Long photoCheckInId, Long userId) {
        try {
            // 检查照片打卡是否存在
            PhotoCheckIn photo = photoCheckInMapper.selectById(photoCheckInId);
            if (photo == null) {
                return Result.error("照片打卡不存在");
            }
            
            // 检查是否已经收藏
            com.scenic.entity.user.UserFavorite existingFavorite = 
                userFavoriteMapper.selectByUserAndContent(userId, photoCheckInId, 1);
            if (existingFavorite != null) {
                return Result.success("操作成功", "已收藏");
            }
            
            // 创建收藏记录
            com.scenic.entity.user.UserFavorite userFavorite = new com.scenic.entity.user.UserFavorite();
            userFavorite.setUserId(userId);
            userFavorite.setContentId(photoCheckInId);
            userFavorite.setContentType(1); // 1表示收藏
            userFavorite.setVersion(0);
            userFavorite.setCreateTime(LocalDateTime.now());
            userFavorite.setUpdateTime(LocalDateTime.now());
            userFavorite.setCreateBy(userId);
            userFavorite.setUpdateBy(userId);
            userFavorite.setCategoryId(photo.getCategoryId().intValue()); // 设置分类ID
            
            // 插入收藏记录
            userFavoriteMapper.insert(userFavorite);
            
            return Result.success("操作成功", "收藏成功");
        } catch (Exception e) {
            return Result.error("收藏失败：" + e.getMessage());
        }
    }
    
    /**
     * 取消收藏照片打卡
     * @param photoCheckInId 照片打卡ID
     * @param userId 用户ID
     * @return 操作结果
     */
    @Override
    public Result<String> unfavoritePhotoCheckIn(Long photoCheckInId, Long userId) {
        try {
            // 检查照片打卡是否存在
            PhotoCheckIn photo = photoCheckInMapper.selectById(photoCheckInId);
            if (photo == null) {
                return Result.error("照片打卡不存在");
            }
            
            // 删除收藏记录（硬删除）
            int result = userFavoriteMapper.deleteByUserAndContent(userId, photoCheckInId, 1);
            if (result > 0) {
                return Result.success("操作成功", "取消收藏成功");
            } else {
                return Result.success("操作成功", "未收藏或已取消收藏");
            }
        } catch (Exception e) {
            return Result.error("取消收藏失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户收藏的照片打卡记录
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 照片打卡记录列表
     */
    @Override
    public PageResult<PhotoCheckInVO> getFavoritePhotoCheckIns(Long userId, Integer pageNum, Integer pageSize) {
        try {
            // 计算偏移量
            int offset = (pageNum - 1) * pageSize;
            
            // 查询用户收藏的照片打卡ID列表
            List<com.scenic.entity.user.UserFavorite> favorites = 
                userFavoriteMapper.selectByUserId(userId, 1, offset, pageSize);
            
            if (favorites.isEmpty()) {
                return PageResult.of(0, pageSize, pageNum, java.util.Collections.emptyList());
            }
            
            // 提取照片打卡ID列表
            List<Long> photoCheckInIds = favorites.stream()
                .map(com.scenic.entity.user.UserFavorite::getContentId)
                .collect(java.util.stream.Collectors.toList());
            
            // 查询照片打卡记录
            List<PhotoCheckIn> photos = photoCheckInMapper.selectByIds(photoCheckInIds);
            
            // 转换为VO对象，包含用户互动状态
            List<PhotoCheckInVO> photoCheckInVOs = convertToVOsWithUserInteraction(photos, userId);
            
            // 查询总数
            int totalCount = userFavoriteMapper.selectCountByUserId(userId, 1);
            
            return PageResult.of(totalCount, pageSize, pageNum, photoCheckInVOs);
        } catch (Exception e) {
            // 发生异常时返回空结果
            return PageResult.of(0, pageSize, pageNum, java.util.Collections.emptyList());
        }
    }
    
    /**
     * 获取用户点赞的照片打卡记录
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 照片打卡记录列表
     */
    @Override
    public PageResult<PhotoCheckInVO> getLikedPhotoCheckIns(Long userId, Integer pageNum, Integer pageSize) {
        try {
            // 计算偏移量
            int offset = (pageNum - 1) * pageSize;
            
            // 查询用户点赞的照片打卡记录（这里假设点赞也存储在user_favorite表中，contentType=2）
            // 或者如果有专门的点赞表，则从点赞表查询
            // 目前按照需求文档，点赞和收藏都使用user_favorite表，contentType区分
            List<com.scenic.entity.user.UserFavorite> likes = 
                userFavoriteMapper.selectByUserId(userId, 2, offset, pageSize);
            
            if (likes.isEmpty()) {
                return PageResult.of(0, pageSize, pageNum, java.util.Collections.emptyList());
            }
            
            // 提取照片打卡ID列表
            List<Long> photoCheckInIds = likes.stream()
                .map(com.scenic.entity.user.UserFavorite::getContentId)
                .collect(java.util.stream.Collectors.toList());
            
            // 查询照片打卡记录
            List<PhotoCheckIn> photos = photoCheckInMapper.selectByIds(photoCheckInIds);
            
            // 转换为VO对象，包含用户互动状态
            List<PhotoCheckInVO> photoCheckInVOs = convertToVOsWithUserInteraction(photos, userId);
            
            // 查询总数
            int totalCount = userFavoriteMapper.selectCountByUserId(userId, 2);
            
            return PageResult.of(totalCount, pageSize, pageNum, photoCheckInVOs);
        } catch (Exception e) {
            // 发生异常时返回空结果
            return PageResult.of(0, pageSize, pageNum, java.util.Collections.emptyList());
        }
    }
    
    /**
     * 获取所有照片打卡记录 - 分页查询
     * @param photoCheckInQueryDTO 查询条件
     * @return 照片打卡记录分页结果
     */
    @Override
    public PageResult<PhotoCheckInVO> getAllPhotoCheckIns(PhotoCheckInQueryDTO photoCheckInQueryDTO) {
        // 使用当前登录用户ID
        Long currentUserId = userContextUtil.getCurrentUserId();
        return getAllPhotoCheckIns(photoCheckInQueryDTO, currentUserId);
    }
    
    /**
     * 获取所有照片打卡记录 - 分页查询（带用户互动状态）
     * @param photoCheckInQueryDTO 查询条件
     * @param userId 用户ID，用于判断互动状态
     * @return 照片打卡记录分页结果
     */
    @Override
    public PageResult<PhotoCheckInVO> getAllPhotoCheckIns(PhotoCheckInQueryDTO photoCheckInQueryDTO, Long userId) {
        // 生成缓存键
        String cacheKey = generatePageCacheKey(photoCheckInQueryDTO);
        
        // 清除缓存，确保每次都从数据库获取最新数据
        redisTemplate.delete(cacheKey);
        
        // 尝试从Redis获取缓存 - 这里实际上不会命中，因为我们已经删除了缓存
        PageResult<PhotoCheckInVO> cachedResult = (PageResult<PhotoCheckInVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            System.out.println("--------------------cache-----------------------");
            return cachedResult;
        }
        
        // 构造查询参数
        String title = photoCheckInQueryDTO.getTitle();
        String userName = photoCheckInQueryDTO.getUserName();
        Long categoryId = photoCheckInQueryDTO.getCategoryId();
        LocalDateTime createTime = photoCheckInQueryDTO.getCreateTime();
        
        // 设置分页参数 - 必须在查询之前设置，且在获取查询参数之后设置
        PageHelper.startPage(photoCheckInQueryDTO.getPageNum(), photoCheckInQueryDTO.getPageSize());
        
        List<PhotoCheckIn> photoCheckIns;
        int totalCount;
        
        // 根据请求来源选择不同的查询方法
        // 判断是否是管理员请求，通过userName参数判断
        // 如果userName不为空，说明是管理员端查询（因为小程序端不会传userName参数）
        if (userName != null && !userName.isEmpty()) {
            // 管理员端查询
            photoCheckIns = photoCheckInMapper.selectForAdmin(
                title, userName, categoryId, createTime
            );
            
            // 查询总数
            totalCount = photoCheckInMapper.selectCountForAdmin(title, userName, categoryId, createTime);
        } else {
            // 小程序端查询
            photoCheckIns = photoCheckInMapper.selectForMiniapp(
                title, categoryId
            );
            
            // 查询总数
            totalCount = photoCheckInMapper.selectCountForMiniapp(title, categoryId);
        }
        
        // 转换为VO对象，包含用户互动状态
        // 使用传入的userId而不是从上下文获取
        List<PhotoCheckInVO> photoCheckInVOs = convertToVOsWithUserInteraction(photoCheckIns, userId);
        
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
     * @param userId 用户ID
     * @return 操作结果
     */
    @Override
    public Result<String> likePhotoCheckIn(Long photoCheckInId, Long userId) {
        try {
            PhotoCheckIn photo = photoCheckInMapper.selectById(photoCheckInId);
            if (photo != null) {
                // 检查是否已经点赞
                com.scenic.entity.user.UserFavorite existingLike = 
                    userFavoriteMapper.selectByUserAndContent(userId, photoCheckInId, 2);
                if (existingLike != null) {
                    return Result.success("操作成功", "已点赞");
                }
                
                // 增加点赞数
                photo.setLikeCount(photo.getLikeCount() + 1);
                photo.setUpdateTime(LocalDateTime.now());
                photoCheckInMapper.updateById(photo);
                
                // 创建点赞记录
                com.scenic.entity.user.UserFavorite userLike = new com.scenic.entity.user.UserFavorite();
                userLike.setUserId(userId);
                userLike.setContentId(photoCheckInId);
                userLike.setContentType(2); // 2表示点赞
                userLike.setVersion(0);
                userLike.setCreateTime(LocalDateTime.now());
                userLike.setUpdateTime(LocalDateTime.now());
                userLike.setCreateBy(userId);
                userLike.setUpdateBy(userId);
                userLike.setCategoryId(photo.getCategoryId().intValue()); // 设置分类ID
                
                // 插入点赞记录
                userFavoriteMapper.insert(userLike);
                
                // 更新缓存
                String cacheKey = PHOTO_CHECK_IN_CACHE_PREFIX + photoCheckInId;
                PhotoCheckInDTO updatedDTO = convertToDTO(photo);
                redisTemplate.opsForValue().set(cacheKey, updatedDTO, 1, TimeUnit.HOURS);
                
                // 清除相关缓存
                redisTemplate.delete(ALL_PHOTOS_CACHE_KEY);
                redisTemplate.delete(PHOTOS_BY_CATEGORY_CACHE_PREFIX + photo.getCategoryId());
                redisTemplate.delete(PHOTOS_BY_USER_ID_CACHE_PREFIX + photo.getUserId());
                redisTemplate.delete(CHECKIN_CATEGORY_LIST_CACHE_KEY);
                // 清除分页缓存
                clearPageCaches();
                
                System.out.println("点赞照片打卡后清除缓存完成");
                
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
        // 注意：当前接口设计存在问题，缺少userId参数
        // 在实际使用中应该传入userId来确保只能取消自己的点赞
        // 这里暂时使用默认逻辑，但在实际应用中需要修改接口设计
        try {
            PhotoCheckIn photo = photoCheckInMapper.selectById(photoCheckInId);
            if (photo != null) {
                // 减少点赞数
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
                    redisTemplate.delete(CHECKIN_CATEGORY_LIST_CACHE_KEY);
                    // 清除分页缓存
                    clearPageCaches();
                    
                    System.out.println("取消点赞照片打卡后清除缓存完成");
                    
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
                // 逻辑删除：将deleted字段设置为true
                photo.setDeleted(true);
                photo.setUpdateTime(LocalDateTime.now());
                photoCheckInMapper.updateById(photo);
                
                // 删除缓存
                String cacheKey = PHOTO_CHECK_IN_CACHE_PREFIX + photoCheckInId;
                redisTemplate.delete(cacheKey);
                
                // 清除相关缓存
                redisTemplate.delete(ALL_PHOTOS_CACHE_KEY);
                redisTemplate.delete(PHOTOS_BY_CATEGORY_CACHE_PREFIX + photo.getCategoryId());
                redisTemplate.delete(PHOTOS_BY_USER_ID_CACHE_PREFIX + photo.getUserId());
                redisTemplate.delete(CHECKIN_CATEGORY_LIST_CACHE_KEY);
                // 清除分页缓存
                clearPageCaches();
                
                System.out.println("删除照片打卡记录后清除缓存完成");
                
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
        // 特别处理photoId字段，根据photoId获取文件URL
        Long photoId = photo.getPhotoId();
        if (photoId != null) {
            ResourceFile resourceFile = resourceFileMapper.selectById(photoId);
            if (resourceFile != null) {
                try {
                    // 根据存储桶名称生成对应的预签名URL
                    String fileUrl;
                    if ("photo-checkin".equals(resourceFile.getBucketName())) {
                        // 对于photo-checkin存储桶，使用7天有效期的URL
                        fileUrl = minioService.getPresignedObjectUrl(resourceFile.getBucketName(), resourceFile.getFileKey(), 7 * 24 * 3600);
                    } else {
                        // 对于其他存储桶，使用1小时有效期的URL
                        fileUrl = minioService.getPresignedObjectUrl(resourceFile.getFileKey(), 3600);
                    }
                    dto.setPhotoUrl(fileUrl);
                } catch (Exception e) {
                    // 如果获取URL失败，使用文件键作为备用
                    dto.setPhotoUrl(resourceFile.getFileKey());
                }
            }
        }
        return dto;
    }
    
    /**
     * 小程序端 - 根据用户ID和分类ID获取用户的发布打卡记录
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 照片打卡记录列表
     */
    @Override
    public PageResult<PhotoCheckInVO> getPhotoCheckInsByUserAndCategory(Long userId, Long categoryId, Integer pageNum, Integer pageSize) {
        // 使用当前登录用户ID
        Long currentUserId = userContextUtil.getCurrentUserId();
        return getPhotoCheckInsByUserAndCategory(userId, categoryId, pageNum, pageSize, currentUserId);
    }
    
    /**
     * 小程序端 - 根据用户ID和分类ID获取用户的发布打卡记录（带用户互动状态）
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param currentUserId 当前用户ID，用于判断互动状态
     * @return 照片打卡记录列表
     */
    @Override
    public PageResult<PhotoCheckInVO> getPhotoCheckInsByUserAndCategory(Long userId, Long categoryId, Integer pageNum, Integer pageSize, Long currentUserId) {
        try {
            // 参数验证
            if (userId == null) {
                return PageResult.of(0, pageSize, pageNum, java.util.Collections.emptyList());
            }
            
            // 计算偏移量
            int offset = (pageNum - 1) * pageSize;
            
            // 查询照片打卡记录
            List<PhotoCheckIn> photos = photoCheckInMapper.selectByUserAndCategory(userId, categoryId, offset, pageSize);
            
            // 转换为VO对象，包含用户互动状态
            // 使用传入的currentUserId而不是从上下文获取
            List<PhotoCheckInVO> photoCheckInVOs = convertToVOsWithUserInteraction(photos, currentUserId);
            
            // 查询总数
            int totalCount = photoCheckInMapper.selectCountByUserAndCategory(userId, categoryId);
            
            return PageResult.of(totalCount, pageSize, pageNum, photoCheckInVOs);
        } catch (Exception e) {
            // 发生异常时返回空结果
            return PageResult.of(0, pageSize, pageNum, java.util.Collections.emptyList());
        }
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
                try {
                    // 根据存储桶名称生成对应的预签名URL
                    String fileUrl;
                    if ("photo-checkin".equals(resourceFile.getBucketName())) {
                        // 对于photo-checkin存储桶，使用7天有效期的URL
                        fileUrl = minioService.getPresignedObjectUrl(resourceFile.getBucketName(), resourceFile.getFileKey(), 7 * 24 * 3600);
                    } else {
                        // 对于其他存储桶，使用1小时有效期的URL
                        fileUrl = minioService.getPresignedObjectUrl(resourceFile.getFileKey(), 3600);
                    }
                    vo.setPhotoPath(fileUrl);
                } catch (Exception e) {
                    // 如果获取URL失败，使用文件键作为备用
                    vo.setPhotoPath(resourceFile.getFileKey());
                }
            }
        }
        
        // 处理用户头像URL
        String userAvatarStr = photo.getUserAvatar();
        if (userAvatarStr != null && !userAvatarStr.isEmpty()) {
            try {
                // 尝试将userAvatar转换为Long类型的文件ID
                Long userAvatarFileId = Long.parseLong(userAvatarStr);
                ResourceFile avatarFile = resourceFileMapper.selectById(userAvatarFileId);
                if (avatarFile != null) {
                    try {
                        // 生成头像URL
                        String avatarUrl;
                        if (avatarFile.getBucketName() != null) {
                            // 使用指定的存储桶名称
                            avatarUrl = minioService.getPresignedObjectUrl(
                                avatarFile.getBucketName(),
                                avatarFile.getFileKey(),
                                7 * 24 * 3600 // 7天有效期
                            );
                        } else {
                            // 默认使用user-avatars存储桶
                            avatarUrl = minioService.getPresignedObjectUrl(
                                "user-avatars",
                                avatarFile.getFileKey(),
                                7 * 24 * 3600 // 7天有效期
                            );
                        }
                        vo.setUserAvatarUrl(avatarUrl);
                    } catch (Exception e) {
                        // 如果获取URL失败，使用文件键作为备用
                        vo.setUserAvatarUrl(avatarFile.getFileKey());
                        System.err.println("获取用户头像URL失败：" + e.getMessage());
                    }
                }
            } catch (NumberFormatException e) {
                // 如果userAvatar不是数字，可能是直接存储的URL
                vo.setUserAvatarUrl(userAvatarStr);
                System.err.println("用户头像不是有效的文件ID：" + e.getMessage());
            }
        }
        
        // 设置分类名称
        vo.setCategoryName(photo.getCategoryName());
        return vo;
    }
    
    /**
     * 将PhotoCheckIn实体转换为PhotoCheckInVO，并包含用户互动状态
     * @param photo PhotoCheckIn实体
     * @param userId 用户ID
     * @return PhotoCheckInVO
     */
    private PhotoCheckInVO convertToVOWithUserInteraction(PhotoCheckIn photo, Long userId) {
        PhotoCheckInVO vo = convertToVO(photo);
        
        if (userId != null) {
            // 使用Bloom Filter快速判断用户是否可能有互动记录
            if (bloomFilterUtil.mightContainUserInteraction(userId, photo.getId())) {
                // Bloom Filter可能存在，进一步检查缓存或数据库
                Map<String, Boolean> interactionStatus = userInteractionCacheUtil.getUserInteractionStatus(userId, photo.getId());
                
                if (interactionStatus != null) {
                    // 从缓存获取状态
                    vo.setIsCollected(interactionStatus.getOrDefault("collected", false));
                    vo.setIsLiked(interactionStatus.getOrDefault("liked", false));
                } else {
                    // 从数据库查询并缓存
                    UserFavorite collectedFavorite = userFavoriteMapper.selectByUserAndContent(userId, photo.getId(), 1);
                    UserFavorite likedFavorite = userFavoriteMapper.selectByUserAndContent(userId, photo.getId(), 2);
                    
                    boolean isCollected = collectedFavorite != null;
                    boolean isLiked = likedFavorite != null;
                    
                    vo.setIsCollected(isCollected);
                    vo.setIsLiked(isLiked);
                    
                    // 缓存结果
                    userInteractionCacheUtil.cacheUserInteractionStatus(userId, photo.getId(), isCollected, isLiked);
                    
                    // 更新Bloom Filter
                    if (isCollected || isLiked) {
                        bloomFilterUtil.addUserInteractionToBloomFilter(userId, photo.getId());
                    }
                }
            } else {
                // Bloom Filter确定没有互动记录
                vo.setIsCollected(false);
                vo.setIsLiked(false);
            }
        }
        
        return vo;
    }
    
    /**
     * 批量将PhotoCheckIn实体列表转换为PhotoCheckInVO列表，并包含用户互动状态
     * @param photos PhotoCheckIn实体列表
     * @param userId 用户ID（可以为null，表示未登录用户）
     * @return PhotoCheckInVO列表
     */
    private List<PhotoCheckInVO> convertToVOsWithUserInteraction(List<PhotoCheckIn> photos, Long userId) {
        // 如果 userId 为 null 或照片列表为空，直接返回不包含用户互动状态的 VO 列表
        if (userId == null || photos == null || photos.isEmpty()) {
            return photos == null ? new ArrayList<>() : photos.stream().map(this::convertToVO).collect(Collectors.toList());
        }
        
        // 提取照片ID列表
        List<Long> photoIds = photos.stream().map(PhotoCheckIn::getId).collect(Collectors.toList());
        
        Map<Long, PhotoCheckInVO> result = new HashMap<>();
        
        // 先从缓存获取已有的状态
        Map<Long, Map<String, Boolean>> cachedStatuses = userInteractionCacheUtil.batchGetUserInteractionStatus(userId, photoIds);
        
        // 所有未缓存的照片ID都需要查询数据库
        List<Long> needQueryPhotoIds = photoIds.stream()
            .filter(photoId -> !cachedStatuses.containsKey(photoId))
            .collect(Collectors.toList());
        
        // 从数据库批量查询需要查询的照片互动状态
        if (!needQueryPhotoIds.isEmpty()) {
            List<UserFavorite> userFavorites = userFavoriteMapper.selectByUserAndContentIds(userId, needQueryPhotoIds);
            
            // 按照片ID分组
            Map<Long, List<UserFavorite>> favoritesByPhotoId = userFavorites.stream()
                .collect(Collectors.groupingBy(UserFavorite::getContentId));
            
            // 处理每个需要查询的照片
            for (Long photoId : needQueryPhotoIds) {
                PhotoCheckIn photo = photos.stream().filter(p -> p.getId().equals(photoId)).findFirst().orElse(null);
                if (photo != null) {
                    List<UserFavorite> favorites = favoritesByPhotoId.getOrDefault(photoId, java.util.Collections.emptyList());
                    
                    boolean isCollected = favorites.stream().anyMatch(fav -> Integer.valueOf(1).equals(fav.getContentType()));
                    boolean isLiked = favorites.stream().anyMatch(fav -> Integer.valueOf(2).equals(fav.getContentType()));
                    
                    PhotoCheckInVO vo = convertToVO(photo);
                    vo.setIsCollected(isCollected);
                    vo.setIsLiked(isLiked);
                    result.put(photoId, vo);
                    
                    // 缓存结果
                    userInteractionCacheUtil.cacheUserInteractionStatus(userId, photoId, isCollected, isLiked);
                    
                    // 更新Bloom Filter
                    if (isCollected || isLiked) {
                        bloomFilterUtil.addUserInteractionToBloomFilter(userId, photoId);
                    }
                }
            }
        }
        
        // 处理缓存中的状态
        cachedStatuses.forEach((photoId, status) -> {
            PhotoCheckIn photo = photos.stream().filter(p -> p.getId().equals(photoId)).findFirst().orElse(null);
            if (photo != null) {
                PhotoCheckInVO vo = convertToVO(photo);
                vo.setIsCollected(status.getOrDefault("collected", false));
                vo.setIsLiked(status.getOrDefault("liked", false));
                result.put(photoId, vo);
            }
        });
        
        // 按原始顺序返回结果
        return photos.stream()
            .map(photo -> result.getOrDefault(photo.getId(), convertToVO(photo)))
            .collect(Collectors.toList());
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
        try {
            // 获取所有匹配的键
            Set<String> keys = redisTemplate.keys(PHOTO_CHECK_IN_PAGE_CACHE_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                // 批量删除匹配的键
                redisTemplate.delete(keys);
                System.out.println("已清除" + keys.size() + "个分页缓存");
            }
        } catch (Exception e) {
            System.err.println("清除分页缓存失败：" + e.getMessage());
        }
    }

    
    /**
     * 获取打卡分类列表
     * @return 分类列表
     */
    @Override
    public Result<List<CheckinCategoryVO>> getCategoryList() {
        try {
            // 尝试从Redis获取缓存
            List<CheckinCategoryVO> cachedList = (List<CheckinCategoryVO>) redisTemplate.opsForValue().get(CHECKIN_CATEGORY_LIST_CACHE_KEY);
            if (cachedList != null) {
                return Result.success("获取成功", cachedList);
            }
            
            // 从数据库查询所有启用的分类
            List<CheckinCategory> categories = checkinCategoryMapper.selectAllEnabled();
            
            // 转换为VO对象
            List<CheckinCategoryVO> categoryVOs = categories.stream()
                    .map(category -> {
                        CheckinCategoryVO vo = new CheckinCategoryVO();
                        vo.setId(category.getId());
                        vo.setName(category.getName());
                        return vo;
                    })
                    .collect(Collectors.toList());
            
            // 缓存结果
            redisTemplate.opsForValue().set(CHECKIN_CATEGORY_LIST_CACHE_KEY, categoryVOs, CATEGORY_LIST_CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            
            return Result.success("获取成功", categoryVOs);
        } catch (Exception e) {
            return Result.error("获取分类列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除打卡分类（软删除）
     * @param categoryId 分类ID
     * @return 操作结果
     */
    @Override
    public Result<String> deleteCategory(Long categoryId) {
        try {
            // 检查分类是否存在
            CheckinCategory category = checkinCategoryMapper.selectById(categoryId);
            if (category == null) {
                return Result.error("分类不存在");
            }
            
            // 检查分类状态，如果已经是禁用状态则无需重复删除
            if (category.getStatus() == 0) {
                return Result.success("操作成功", "分类已处于禁用状态");
            }
            
            // 更新分类状态为禁用
            int result = checkinCategoryMapper.updateStatus(categoryId, 0);
            if (result > 0) {
                // 清除分类列表缓存
                redisTemplate.delete(CHECKIN_CATEGORY_LIST_CACHE_KEY);
                
                // 清除分页缓存（因为分类状态改变可能影响查询结果）
                clearPageCaches();
                
                return Result.success("操作成功", "分类删除成功");
            } else {
                return Result.error("删除分类失败");
            }
        } catch (Exception e) {
            return Result.error("删除分类失败：" + e.getMessage());
        }
    }
    
    /**
     * 新增打卡分类
     * @param categoryName 分类名称
     * @return 操作结果
     */
    @Override
    public Result<String> addCategory(String categoryName) {
        try {
            // 参数验证
            if (categoryName == null || categoryName.trim().isEmpty()) {
                return Result.error("分类名称不能为空");
            }
            
            // 去除首尾空格
            categoryName = categoryName.trim();
            
            // 检查分类名长度
            if (categoryName.length() > 50) {
                return Result.error("分类名称长度不能超过50个字符");
            }
            
            // 检查同名分类是否已存在（启用状态的）
            CheckinCategory existingCategory = checkinCategoryMapper.selectByName(categoryName);
            if (existingCategory != null) {
                return Result.error("分类名称已存在");
            }
            
            // 构造新的分类对象
            CheckinCategory newCategory = new CheckinCategory();
            newCategory.setName(categoryName);
            newCategory.setDescription(""); // 默认描述为空
            newCategory.setSortOrder(0); // 默认排序为0
            newCategory.setStatus(1); // 默认状态为启用
            newCategory.setVersion(0); // 默认版本号为0
            newCategory.setCreateTime(java.time.LocalDateTime.now());
            newCategory.setUpdateTime(java.time.LocalDateTime.now());
            // 使用当前用户信息
            Long currentUserId = userContextUtil.getCurrentUserId();
            newCategory.setCreateBy(currentUserId != null ? currentUserId : 1L);
            newCategory.setUpdateBy(currentUserId != null ? currentUserId : 1L);
            
            // 插入数据库
            int result = checkinCategoryMapper.insert(newCategory);
            if (result > 0) {
                // 清除分类列表缓存
                redisTemplate.delete(CHECKIN_CATEGORY_LIST_CACHE_KEY);
                
                // 清除分页缓存
                clearPageCaches();
                
                return Result.success("操作成功", "分类添加成功，ID: " + newCategory.getId());
            } else {
                return Result.error("添加分类失败");
            }
        } catch (Exception e) {
            return Result.error("添加分类失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理后台端 - 获取当前照片打卡记录详情
     * @param photoCheckInId 照片打卡ID
     * @return 照片打卡记录详情
     */
    @Override
    public Result<PhotoCheckInVO> getPhotoCheckInsInfoForAdmin(Long photoCheckInId) {
        try {
            // 从数据库查询照片打卡记录
            PhotoCheckIn photoCheckIn = photoCheckInMapper.selectById(photoCheckInId);
            if (photoCheckIn == null) {
                return Result.error("照片打卡记录不存在");
            }
            
            // 转换为VO对象
            PhotoCheckInVO photoCheckInVO = convertToVO(photoCheckIn);
            
            return Result.success("获取成功", photoCheckInVO);
        } catch (Exception e) {
            return Result.error("获取照片打卡详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理后台端 - 新增照片打卡记录
     * @param title 标题
     * @param categoryId 分类ID
     * @param photo 照片文件
     * @return 操作结果
     */
    @Override
    public Result<String> addPhotoCheckInForAdmin(String title, Long categoryId, MultipartFile photo) {
        try {
            // 参数验证
            if (title == null || title.trim().isEmpty()) {
                return Result.error("标题不能为空");
            }
            if (categoryId == null) {
                return Result.error("分类不能为空");
            }
            if (photo == null || photo.isEmpty()) {
                return Result.error("照片不能为空");
            }
            
            // 检查分类是否存在且启用
            CheckinCategory category = checkinCategoryMapper.selectById(categoryId);
            if (category == null || category.getStatus() != 1) {
                return Result.error("指定的分类不存在或已禁用");
            }
            
            // 上传文件到MinIO
            String originalFilename = photo.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return Result.error("文件名不能为空");
            }
            
            // 生成唯一文件名
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = java.util.UUID.randomUUID().toString() + fileExtension;
            
            // 上传到MinIO的photo-checkin存储桶
            String fileUrl = minioService.uploadPhotoCheckinFile(photo, uniqueFilename);
            
            // 保存文件信息到resource_file表
            ResourceFile resourceFile = new ResourceFile();
            resourceFile.setId(idGenerator.nextId()); // 生成雪花ID
            resourceFile.setFileName(originalFilename);
            resourceFile.setFileKey(uniqueFilename);
            resourceFile.setBucketName("photo-checkin"); // MinIO存储桶名称
            resourceFile.setFileSize(photo.getSize());
            resourceFile.setMimeType(photo.getContentType());
            resourceFile.setFileType(1); // 1表示图片
            resourceFile.setCreateTime(java.time.LocalDateTime.now());
            resourceFile.setUpdateTime(java.time.LocalDateTime.now());
            // 使用当前用户信息
            Long currentUserId = userContextUtil.getCurrentUserId();
            resourceFile.setCreateBy(currentUserId != null ? currentUserId : 1L);
            resourceFile.setUpdateBy(currentUserId != null ? currentUserId : 1L);
            resourceFile.setUploadUserId(currentUserId != null ? currentUserId : 1L);
            
            resourceFileMapper.insert(resourceFile);
            
            // 创建照片打卡记录
            PhotoCheckIn photoCheckIn = new PhotoCheckIn();
            photoCheckIn.setId(idGenerator.nextId()); // 生成雪花ID
            photoCheckIn.setTitle(title.trim());
            photoCheckIn.setCategoryId(categoryId);
            photoCheckIn.setPhotoId(resourceFile.getId());
            // 使用当前用户ID
            Long userId = currentUserId != null ? currentUserId : 1L;
            photoCheckIn.setUserId(userId);
            
            // 根据用户ID获取用户信息
            String userName = "管理员";
            String userAvatar = "";
            try {
                // 查询用户信息
                com.scenic.entity.user.User user = userMapper.selectById(userId);
                if (user != null) {
                    // 设置用户名
                    userName = user.getUserName() != null ? user.getUserName() : "管理员";
                    
                    // 获取用户头像
                    Long avatarFileId = user.getAvatarFileId();
                    if (avatarFileId != null) {
                        ResourceFile avatarFile = resourceFileMapper.selectById(avatarFileId);
                        if (avatarFile != null) {
                            try {
                                // 生成头像URL
                                String fullUrl = minioService.getPresignedObjectUrl(
                                    avatarFile.getBucketName() != null ? avatarFile.getBucketName() : "user-avatars",
                                    avatarFile.getFileKey(),
                                    7 * 24 * 3600 // 7天有效期
                                );
                                
                                // 检查URL长度，确保不超过数据库字段限制（1024个字符）
                                if (fullUrl.length() > 1024) {
                                    System.err.println("警告：用户头像URL长度超过1024个字符，将被截断");
                                    userAvatar = fullUrl.substring(0, 1020) + "...";
                                } else {
                                    userAvatar = fullUrl;
                                }
                            } catch (Exception e) {
                                // 如果获取URL失败，使用文件键作为备用
                                userAvatar = avatarFile.getFileKey();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // 如果获取用户信息失败，使用默认值
                System.err.println("获取用户信息失败：" + e.getMessage());
            }
            
            photoCheckIn.setUserName(userName);
            photoCheckIn.setUserAvatar(userAvatar);
            photoCheckIn.setContent(""); // 默认内容为空
            photoCheckIn.setLikeCount(0);
            photoCheckIn.setViewCount(0);
            photoCheckIn.setStatus(1); // 启用状态
            photoCheckIn.setVersion(0);
            photoCheckIn.setDeleted(false);
            photoCheckIn.setCreateTime(java.time.LocalDateTime.now());
            photoCheckIn.setUpdateTime(java.time.LocalDateTime.now());
            photoCheckIn.setCreateBy(currentUserId != null ? currentUserId : 1L);
            photoCheckIn.setUpdateBy(currentUserId != null ? currentUserId : 1L);
            
            // 保存到数据库
            photoCheckInMapper.insert(photoCheckIn);
            
            // 清除相关缓存
            redisTemplate.delete(ALL_PHOTOS_CACHE_KEY);
            redisTemplate.delete(PHOTOS_BY_CATEGORY_CACHE_PREFIX + categoryId);
            redisTemplate.delete(PHOTOS_BY_USER_ID_CACHE_PREFIX + userId);
            redisTemplate.delete(CHECKIN_CATEGORY_LIST_CACHE_KEY);
            // 清除分页缓存
            clearPageCaches();
            
            System.out.println("新增照片打卡记录后清除缓存完成");
            
            return Result.success("操作成功", "照片打卡记录添加成功，ID: " + photoCheckIn.getId());
        } catch (Exception e) {
            return Result.error("添加照片打卡记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理后台端 - 更新照片打卡记录
     * @param id 照片打卡记录ID
     * @param title 标题
     * @param categoryId 分类ID
     * @param photo 照片文件（可选）
     * @return 操作结果
     */
    @Override
    public Result<String> updatePhotoCheckInForAdmin(Long id, String title, Long categoryId, MultipartFile photo) {
        try {
            // 参数验证
            if (id == null) {
                return Result.error("记录ID不能为空");
            }
            if (title == null || title.trim().isEmpty()) {
                return Result.error("标题不能为空");
            }
            if (categoryId == null) {
                return Result.error("分类不能为空");
            }
            
            // 检查记录是否存在
            PhotoCheckIn existingPhotoCheckIn = photoCheckInMapper.selectById(id);
            if (existingPhotoCheckIn == null) {
                return Result.error("指定的照片打卡记录不存在");
            }
            
            // 检查分类是否存在且启用
            CheckinCategory category = checkinCategoryMapper.selectById(categoryId);
            if (category == null || category.getStatus() != 1) {
                return Result.error("指定的分类不存在或已禁用");
            }
            
            Long oldCategoryId = existingPhotoCheckIn.getCategoryId();
            
            // 如果上传了新图片，处理图片上传
            Long photoId = existingPhotoCheckIn.getPhotoId();
            if (photo != null && !photo.isEmpty()) {
                // 上传新文件到MinIO
                String originalFilename = photo.getOriginalFilename();
                if (originalFilename == null || originalFilename.isEmpty()) {
                    return Result.error("文件名不能为空");
                }
                
                // 生成唯一文件名
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String uniqueFilename = java.util.UUID.randomUUID().toString() + fileExtension;
                
                // 上传到MinIO的photo-checkin存储桶
                String fileUrl = minioService.uploadPhotoCheckinFile(photo, uniqueFilename);
                
                // 保存文件信息到resource_file表
                ResourceFile resourceFile = new ResourceFile();
                resourceFile.setId(idGenerator.nextId()); // 生成雪花ID
                resourceFile.setFileName(originalFilename);
                resourceFile.setFileKey(uniqueFilename);
                resourceFile.setBucketName("photo-checkin"); // MinIO存储桶名称
                resourceFile.setFileSize(photo.getSize());
                resourceFile.setMimeType(photo.getContentType());
                resourceFile.setFileType(1); // 1表示图片
                resourceFile.setCreateTime(java.time.LocalDateTime.now());
                resourceFile.setUpdateTime(java.time.LocalDateTime.now());
                // 使用当前用户信息
                Long currentUserId = userContextUtil.getCurrentUserId();
                resourceFile.setCreateBy(currentUserId != null ? currentUserId : 1L);
                resourceFile.setUpdateBy(currentUserId != null ? currentUserId : 1L);
                resourceFile.setUploadUserId(currentUserId != null ? currentUserId : 1L);
                
                resourceFileMapper.insert(resourceFile);
                
                photoId = resourceFile.getId();
            }
            
            // 更新照片打卡记录
            existingPhotoCheckIn.setTitle(title.trim());
            existingPhotoCheckIn.setCategoryId(categoryId);
            if (photoId != null && photoId != existingPhotoCheckIn.getPhotoId()) {
                existingPhotoCheckIn.setPhotoId(photoId);
            }
            existingPhotoCheckIn.setUpdateTime(java.time.LocalDateTime.now());
            // 使用当前用户ID
            Long currentUserId = userContextUtil.getCurrentUserId();
            existingPhotoCheckIn.setUpdateBy(currentUserId != null ? currentUserId : 1L);
            
            // 根据用户ID获取用户信息并更新用户名和头像
            Long userId = existingPhotoCheckIn.getUserId();
            if (userId != null) {
                try {
                    // 查询用户信息
                    com.scenic.entity.user.User user = userMapper.selectById(userId);
                    if (user != null) {
                        // 设置用户名
                        String userName = user.getUserName() != null ? user.getUserName() : "管理员";
                        existingPhotoCheckIn.setUserName(userName);
                        
                        // 获取用户头像
                        Long avatarFileId = user.getAvatarFileId();
                        if (avatarFileId != null) {
                            ResourceFile avatarFile = resourceFileMapper.selectById(avatarFileId);
                            if (avatarFile != null) {
                                try {
                                    // 生成头像URL
                                    String fullUrl = minioService.getPresignedObjectUrl(
                                        avatarFile.getBucketName() != null ? avatarFile.getBucketName() : "user-avatars",
                                        avatarFile.getFileKey(),
                                        7 * 24 * 3600 // 7天有效期
                                    );
                                    
                                    // 检查URL长度，确保不超过数据库字段限制（1024个字符）
                                    if (fullUrl.length() > 1024) {
                                        System.err.println("警告：用户头像URL长度超过1024个字符，将被截断");
                                        existingPhotoCheckIn.setUserAvatar(fullUrl.substring(0, 1020) + "...");
                                    } else {
                                        existingPhotoCheckIn.setUserAvatar(fullUrl);
                                    }
                                } catch (Exception e) {
                                    // 如果获取URL失败，使用文件键作为备用
                                    existingPhotoCheckIn.setUserAvatar(avatarFile.getFileKey());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // 如果获取用户信息失败，记录错误但不影响更新操作
                    System.err.println("获取用户信息失败：" + e.getMessage());
                }
            }
            
            // 保存到数据库
            photoCheckInMapper.updateById(existingPhotoCheckIn);
            
            // 清除相关缓存
            redisTemplate.delete(ALL_PHOTOS_CACHE_KEY);
            redisTemplate.delete(PHOTOS_BY_CATEGORY_CACHE_PREFIX + oldCategoryId);
            redisTemplate.delete(PHOTOS_BY_CATEGORY_CACHE_PREFIX + categoryId);
            redisTemplate.delete(PHOTOS_BY_USER_ID_CACHE_PREFIX + userId);
            redisTemplate.delete(CHECKIN_CATEGORY_LIST_CACHE_KEY);
            redisTemplate.delete(PHOTO_CHECK_IN_CACHE_PREFIX + id);
            // 清除分页缓存
            clearPageCaches();
            
            System.out.println("更新照片打卡记录后清除缓存完成");
            
            return Result.success("操作成功", "照片打卡记录更新成功，ID: " + id);
        } catch (Exception e) {
            return Result.error("更新照片打卡记录失败：" + e.getMessage());
        }
    }
}