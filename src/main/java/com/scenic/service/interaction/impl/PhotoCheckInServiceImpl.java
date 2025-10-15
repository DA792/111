package com.scenic.service.interaction.impl;

import java.time.LocalDateTime;
import java.util.List;
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
import com.scenic.entity.system.ResourceFile;
import com.scenic.mapper.interaction.CheckinCategoryMapper;
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
    private CheckinCategoryMapper checkinCategoryMapper;
    
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
    private static final String CHECKIN_CATEGORY_LIST_CACHE_KEY = "checkin_category:list";
    
    // 缓存过期时间（分钟）
    private static final int PAGE_CACHE_EXPIRE_MINUTES = 5;
    private static final int EMPTY_RESULT_CACHE_EXPIRE_MINUTES = 2;
    private static final int CATEGORY_LIST_CACHE_EXPIRE_MINUTES = 30;
    
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
            System.out.println("--------------------cache-----------------------");
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
            newCategory.setCreateBy(1L); // 默认创建人ID为1
            newCategory.setUpdateBy(1L); // 默认更新人ID为1
            
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
            
            // 上传文件
            String originalFilename = photo.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return Result.error("文件名不能为空");
            }
            
            // 生成唯一文件名
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = java.util.UUID.randomUUID().toString() + fileExtension;
            
            // 保存文件到上传目录
            java.nio.file.Path uploadPath = java.nio.file.Paths.get("./uploads", uniqueFilename);
            java.nio.file.Files.createDirectories(uploadPath.getParent());
            java.nio.file.Files.write(uploadPath, photo.getBytes());
            
            // 保存文件信息到resource_file表
            ResourceFile resourceFile = new ResourceFile();
            resourceFile.setFileName(originalFilename);
            resourceFile.setFileKey(uniqueFilename);
            resourceFile.setBucketName("local"); // 本地存储
            resourceFile.setFileSize(photo.getSize());
            resourceFile.setMimeType(photo.getContentType());
            resourceFile.setFileType(1); // 1表示图片
            resourceFile.setCreateTime(java.time.LocalDateTime.now());
            resourceFile.setUpdateTime(java.time.LocalDateTime.now());
            resourceFile.setCreateBy(1L); // 管理员ID
            resourceFile.setUpdateBy(1L);
            
            resourceFileMapper.insert(resourceFile);
            
            // 创建照片打卡记录
            PhotoCheckIn photoCheckIn = new PhotoCheckIn();
            photoCheckIn.setTitle(title.trim());
            photoCheckIn.setCategoryId(categoryId);
            photoCheckIn.setPhotoId(resourceFile.getId());
            photoCheckIn.setUserId(1L); // 管理员ID
            photoCheckIn.setUserName("管理员");
            photoCheckIn.setUserAvatar(""); // 默认头像
            photoCheckIn.setContent(""); // 默认内容为空
            photoCheckIn.setLikeCount(0);
            photoCheckIn.setViewCount(0);
            photoCheckIn.setStatus(1); // 启用状态
            photoCheckIn.setVersion(0);
            photoCheckIn.setDeleted(false);
            photoCheckIn.setCreateTime(java.time.LocalDateTime.now());
            photoCheckIn.setUpdateTime(java.time.LocalDateTime.now());
            photoCheckIn.setCreateBy(1L);
            photoCheckIn.setUpdateBy(1L);
            
            // 保存到数据库
            photoCheckInMapper.insert(photoCheckIn);
            
            // 清除相关缓存
            redisTemplate.delete(ALL_PHOTOS_CACHE_KEY);
            redisTemplate.delete(PHOTOS_BY_CATEGORY_CACHE_PREFIX + categoryId);
            redisTemplate.delete(CHECKIN_CATEGORY_LIST_CACHE_KEY);
            // 清除分页缓存
            clearPageCaches();
            
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
                // 删除旧图片文件（如果需要的话，这里可以根据业务需求决定是否删除）
                // 上传新文件
                String originalFilename = photo.getOriginalFilename();
                if (originalFilename == null || originalFilename.isEmpty()) {
                    return Result.error("文件名不能为空");
                }
                
                // 生成唯一文件名
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String uniqueFilename = java.util.UUID.randomUUID().toString() + fileExtension;
                
                // 保存文件到上传目录
                java.nio.file.Path uploadPath = java.nio.file.Paths.get("./uploads", uniqueFilename);
                java.nio.file.Files.createDirectories(uploadPath.getParent());
                java.nio.file.Files.write(uploadPath, photo.getBytes());
                
                // 保存文件信息到resource_file表
                ResourceFile resourceFile = new ResourceFile();
                resourceFile.setFileName(originalFilename);
                resourceFile.setFileKey(uniqueFilename);
                resourceFile.setBucketName("local"); // 本地存储
                resourceFile.setFileSize(photo.getSize());
                resourceFile.setMimeType(photo.getContentType());
                resourceFile.setFileType(1); // 1表示图片
                resourceFile.setCreateTime(java.time.LocalDateTime.now());
                resourceFile.setUpdateTime(java.time.LocalDateTime.now());
                resourceFile.setCreateBy(1L); // 管理员ID
                resourceFile.setUpdateBy(1L);
                
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
            existingPhotoCheckIn.setUpdateBy(1L);
            
            // 保存到数据库
            photoCheckInMapper.updateById(existingPhotoCheckIn);
            
            // 清除相关缓存
            redisTemplate.delete(ALL_PHOTOS_CACHE_KEY);
            redisTemplate.delete(PHOTOS_BY_CATEGORY_CACHE_PREFIX + oldCategoryId);
            redisTemplate.delete(PHOTOS_BY_CATEGORY_CACHE_PREFIX + categoryId);
            redisTemplate.delete(CHECKIN_CATEGORY_LIST_CACHE_KEY);
            redisTemplate.delete(PHOTO_CHECK_IN_CACHE_PREFIX + id);
            // 清除分页缓存
            clearPageCaches();
            
            return Result.success("操作成功", "照片打卡记录更新成功，ID: " + id);
        } catch (Exception e) {
            return Result.error("更新照片打卡记录失败：" + e.getMessage());
        }
    }
}