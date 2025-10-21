package com.scenic.service.content.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scenic.entity.content.ProtectedReservationInfo;
import com.scenic.mapper.content.ProtectedReservationInfoMapper;
import com.scenic.service.content.ProtectedReservationInfoService;
import com.scenic.dto.content.ProtectedReservationInfoDTO;
import com.scenic.dto.content.ProtectedReservationInfoEnhancedDTO;
import com.scenic.common.dto.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.scenic.utils.FileUploadUtil;
import com.scenic.mapper.ResourceFileMapper;
import com.scenic.entity.ResourceFile;
import com.scenic.service.MinioService;

/**
 * 保护区介绍服务实现类
 */
@Service
public class ProtectedReservationInfoServiceImpl extends ServiceImpl<ProtectedReservationInfoMapper, ProtectedReservationInfo> implements ProtectedReservationInfoService {
    
    @Resource
    private ProtectedReservationInfoMapper protectedReservationInfoMapper;
    
    @Resource
    private com.scenic.mapper.user.UserMapper userMapper;
    
    @Resource
    private FileUploadUtil fileUploadUtil;
    
    @Resource
    private ResourceFileMapper resourceFileMapper;
    
    @Autowired
    private MinioService minioService;
    
    
    /**
     * 更新保护区介绍
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProtectedReservationInfo(ProtectedReservationInfoDTO dto, MultipartFile[] videoFiles) {
        try {
            // 获取当前登录用户ID
            Long currentUserId = dto.getUpdateBy();
            System.out.println("当前登录用户ID: " + currentUserId);
            
            // 确保用户ID不为null，如果为null则使用默认值1L
            if (currentUserId == null) {
                currentUserId = 1L;
                System.out.println("用户ID为null，使用默认值: " + currentUserId);
            }
            
            // 获取原有的保护区介绍信息
            System.out.println("尝试获取ID为 " + dto.getId() + " 的保护区介绍");
            ProtectedReservationInfo existingEntity = this.getById(dto.getId());
            System.out.println("获取到的实体: " + existingEntity);
            if (existingEntity == null) {
                throw new RuntimeException("未找到ID为" + dto.getId() + "的保护区介绍");
            }
            
            // 只有当有新文件上传时才删除原有文件
            if (videoFiles != null && videoFiles.length > 0) {
                // 删除原有的所有文件
                System.out.println("检测到新视频文件上传，删除原有文件");
                deleteProtectedReservationInfoFiles(dto.getId());
                
                // 处理上传的视频文件
                System.out.println("开始处理上传的视频文件，数量: " + videoFiles.length);
                List<Long> uploadedVideoFileIds = processVideoFiles(videoFiles, currentUserId);
                System.out.println("处理完成，获取到上传视频文件ID: " + uploadedVideoFileIds);
                
                // 设置视频文件ID到DTO
                dto.setVideoFileIds(uploadedVideoFileIds);
                System.out.println("更新后的视频文件ID列表: " + dto.getVideoFileIds());
            } else {
                // 如果没有上传新视频，保持原有视频文件不变
                dto.setVideoFileIds(existingEntity.getVideoFileIds());
                System.out.println("没有上传新视频，保持原有视频文件不变: " + existingEntity.getVideoFileIds());
            }
            
            // 保持其他类型文件不变
            dto.setContentImageIds(existingEntity.getContentImageIds());
            dto.setCarouselFileIds(existingEntity.getCarouselFileIds());
            dto.setGalleryFileIds(existingEntity.getGalleryFileIds());
            dto.setAudioFileIds(existingEntity.getAudioFileIds());
            
            // 更新保护区介绍信息
            ProtectedReservationInfo entity = new ProtectedReservationInfo();
            BeanUtils.copyProperties(dto, entity);
            entity.setUpdateTime(LocalDateTime.now());
            
            // 确保所有文件ID被正确设置
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            
            if (dto.getVideoFileIds() != null) {
                System.out.println("设置视频文件ID到实体: " + dto.getVideoFileIds());
                try {
                    String videoFileIdsJson = objectMapper.writeValueAsString(dto.getVideoFileIds());
                    System.out.println("转换后的JSON字符串: " + videoFileIdsJson);
                    entity.setVideoFileIds(dto.getVideoFileIds());
                } catch (Exception e) {
                    System.err.println("转换视频文件ID为JSON字符串时发生异常: " + e.getMessage());
                }
            }
            
            if (dto.getContentImageIds() != null) {
                entity.setContentImageIds(dto.getContentImageIds());
            }
            
            if (dto.getCarouselFileIds() != null) {
                entity.setCarouselFileIds(dto.getCarouselFileIds());
            }
            
            if (dto.getGalleryFileIds() != null) {
                entity.setGalleryFileIds(dto.getGalleryFileIds());
            }
            
            if (dto.getAudioFileIds() != null) {
                entity.setAudioFileIds(dto.getAudioFileIds());
            }
            
            boolean updateResult = this.updateById(entity);
            System.out.println("更新保护区介绍结果: " + updateResult + ", ID: " + entity.getId());
            
            return updateResult;
        } catch (Exception e) {
            System.err.println("更新保护区介绍时发生异常: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("更新保护区介绍失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新保护区介绍（兼容旧版本）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProtectedReservationInfo(ProtectedReservationInfoDTO dto) {
        return updateProtectedReservationInfo(dto, null);
    }
    
    /**
     * 删除保护区介绍关联的所有文件，但不删除保护区介绍记录本身
     * @param id 保护区介绍ID
     * @return 是否删除成功
     */
    public boolean deleteProtectedReservationInfoFiles(Long id) {
        System.out.println("=== 开始删除保护区介绍关联的文件，ID: " + id + " ===");
        
        try {
            // 查询原始数据，获取JSON字符串形式的文件ID
            Map<String, Object> rawData = protectedReservationInfoMapper.selectRawById(id);
            if (rawData != null) {
                System.out.println("获取到原始数据: " + rawData);
                
                // 处理视频文件
                Object videoFileIdsObj = rawData.get("video_file_ids");
                if (videoFileIdsObj != null && !videoFileIdsObj.toString().isEmpty()) {
                    System.out.println("原始视频文件ID JSON: " + videoFileIdsObj);
                    try {
                        // 直接使用字符串形式的ID列表
                        List<String> videoFileIdStrings = extractIdsFromJson(videoFileIdsObj.toString());
                        System.out.println("提取的视频文件ID: " + videoFileIdStrings);
                        if (!videoFileIdStrings.isEmpty()) {
                            deleteFilesByStringIds(videoFileIdStrings, "视频");
                        }
                    } catch (Exception e) {
                        System.err.println("处理视频文件ID时发生异常: " + e.getMessage());
                    }
                }
                
                // 处理画廊图片
                Object galleryFileIdsObj = rawData.get("gallery_file_ids");
                if (galleryFileIdsObj != null && !galleryFileIdsObj.toString().isEmpty()) {
                    System.out.println("原始画廊图片ID JSON: " + galleryFileIdsObj);
                    try {
                        List<String> galleryFileIdStrings = extractIdsFromJson(galleryFileIdsObj.toString());
                        System.out.println("提取的画廊图片ID: " + galleryFileIdStrings);
                        if (!galleryFileIdStrings.isEmpty()) {
                            deleteFilesByStringIds(galleryFileIdStrings, "画廊图片");
                        }
                    } catch (Exception e) {
                        System.err.println("处理画廊图片ID时发生异常: " + e.getMessage());
                    }
                }
                
                // 处理音频文件
                Object audioFileIdsObj = rawData.get("audio_file_ids");
                if (audioFileIdsObj != null && !audioFileIdsObj.toString().isEmpty()) {
                    System.out.println("原始音频文件ID JSON: " + audioFileIdsObj);
                    try {
                        List<String> audioFileIdStrings = extractIdsFromJson(audioFileIdsObj.toString());
                        System.out.println("提取的音频文件ID: " + audioFileIdStrings);
                        if (!audioFileIdStrings.isEmpty()) {
                            deleteFilesByStringIds(audioFileIdStrings, "音频");
                        }
                    } catch (Exception e) {
                        System.err.println("处理音频文件ID时发生异常: " + e.getMessage());
                    }
                }
                
                // 处理轮播图
                Object carouselFileIdsObj = rawData.get("carousel_file_ids");
                if (carouselFileIdsObj != null && !carouselFileIdsObj.toString().isEmpty()) {
                    System.out.println("原始轮播图ID JSON: " + carouselFileIdsObj);
                    try {
                        List<String> carouselFileIdStrings = extractIdsFromJson(carouselFileIdsObj.toString());
                        System.out.println("提取的轮播图ID: " + carouselFileIdStrings);
                        if (!carouselFileIdStrings.isEmpty()) {
                            deleteFilesByStringIds(carouselFileIdStrings, "轮播图");
                        }
                    } catch (Exception e) {
                        System.err.println("处理轮播图ID时发生异常: " + e.getMessage());
                    }
                }
                
                // 处理内容图片
                Object contentImageIdsObj = rawData.get("content_image_ids");
                if (contentImageIdsObj != null && !contentImageIdsObj.toString().isEmpty()) {
                    System.out.println("原始内容图片ID JSON: " + contentImageIdsObj);
                    try {
                        List<String> contentImageIdStrings = extractIdsFromJson(contentImageIdsObj.toString());
                        System.out.println("提取的内容图片ID: " + contentImageIdStrings);
                        if (!contentImageIdStrings.isEmpty()) {
                            deleteFilesByStringIds(contentImageIdStrings, "内容图片");
                        }
                    } catch (Exception e) {
                        System.err.println("处理内容图片ID时发生异常: " + e.getMessage());
                    }
                }
                
                return true;
            } else {
                System.err.println("未找到ID为" + id + "的保护区介绍原始数据");
                return false;
            }
        } catch (Exception e) {
            System.err.println("删除保护区介绍关联文件时发生异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 删除保护区介绍（物理删除）
     */
    @Override
    @Transactional
    public boolean deleteProtectedReservationInfo(Long id) {
        System.out.println("=== 开始删除保护区介绍，ID: " + id + " ===");
        
        // 获取保护区介绍信息
        ProtectedReservationInfo entity = this.getById(id);
        if (entity == null) {
            System.err.println("未找到ID为" + id + "的保护区介绍");
            return false;
        }
        
        System.out.println("找到保护区介绍: " + entity.getTitle());
        
        // 打印所有文件ID列表
        System.out.println("视频文件ID: " + entity.getVideoFileIds());
        System.out.println("画廊图片ID: " + entity.getGalleryFileIds());
        System.out.println("音频文件ID: " + entity.getAudioFileIds());
        System.out.println("轮播图ID: " + entity.getCarouselFileIds());
        System.out.println("内容图片ID: " + entity.getContentImageIds());
        
        // 删除关联的所有文件
        deleteProtectedReservationInfoFiles(id);
        
        // 物理删除记录
        boolean result = this.removeById(id);
        System.out.println("删除保护区介绍记录结果: " + result);
        
        return result;
    }
    
    /**
     * 根据ID获取保护区介绍
     */
    @Override
    public ProtectedReservationInfoDTO getProtectedReservationInfoById(Long id) {
        ProtectedReservationInfo entity = this.getById(id);
        if (entity == null) {
            return null;
        }
        ProtectedReservationInfoDTO dto = new ProtectedReservationInfoDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
    
    /**
     * 分页查询保护区介绍列表
     */
    @Override
    public PageResult<ProtectedReservationInfoDTO> getProtectedReservationInfoPage(
            Integer page, Integer size, String title, Byte contentType, Byte contentCategory) {
        
        LambdaQueryWrapper<ProtectedReservationInfo> queryWrapper = new LambdaQueryWrapper<>();
        
        if (title != null && !title.isEmpty()) {
            queryWrapper.like(ProtectedReservationInfo::getTitle, title);
        }
        if (contentType != null) {
            queryWrapper.eq(ProtectedReservationInfo::getContentType, contentType);
        }
        if (contentCategory != null) {
            queryWrapper.eq(ProtectedReservationInfo::getContentCategory, contentCategory);
        }
        
        queryWrapper.orderByDesc(ProtectedReservationInfo::getCreateTime);
        
        IPage<ProtectedReservationInfo> pageResult = this.page(new Page<>(page, size), queryWrapper);
        
        List<ProtectedReservationInfoDTO> dtoList = pageResult.getRecords().stream().map(entity -> {
            ProtectedReservationInfoDTO dto = new ProtectedReservationInfoDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
        
        return new PageResult<ProtectedReservationInfoDTO>(pageResult.getTotal(), (int)pageResult.getSize(), (int)pageResult.getCurrent(), dtoList);
    }
    
    /**
     * 获取所有保护区介绍列表
     */
    @Override
    public List<ProtectedReservationInfoDTO> getAllProtectedReservationInfo() {
        LambdaQueryWrapper<ProtectedReservationInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(ProtectedReservationInfo::getCreateTime);
        
        return this.list(queryWrapper).stream().map(entity -> {
            ProtectedReservationInfoDTO dto = new ProtectedReservationInfoDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 根据内容类型获取保护区介绍列表
     */
    @Override
    public List<ProtectedReservationInfoDTO> getProtectedReservationInfoByContentType(Byte contentType) {
        List<ProtectedReservationInfo> entityList = protectedReservationInfoMapper.selectByContentType(contentType);
        return entityList.stream().map(entity -> {
            ProtectedReservationInfoDTO dto = new ProtectedReservationInfoDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 根据内容分类获取保护区介绍列表
     */
    @Override
    public List<ProtectedReservationInfoDTO> getProtectedReservationInfoByContentCategory(Byte contentCategory) {
        List<ProtectedReservationInfo> entityList = protectedReservationInfoMapper.selectByContentCategory(contentCategory);
        return entityList.stream().map(entity -> {
            ProtectedReservationInfoDTO dto = new ProtectedReservationInfoDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 分页查询保护区介绍列表（增强版，支持发布人、发布时间、内容类型搜索）
     */
    @Override
    public PageResult<Map<String, Object>> getProtectedReservationInfoPageEnhanced(
            Integer page, Integer size, String title, String creatorName, 
            LocalDateTime startTime, LocalDateTime endTime, Byte contentType) {
        
        int offset = (page - 1) * size;
        
        // 查询数据
        List<Map<String, Object>> dataList = protectedReservationInfoMapper.selectPageEnhanced(
                offset, size, title, creatorName, startTime, endTime, contentType);
        
        // 查询总数
        int total = protectedReservationInfoMapper.selectCountEnhanced(
                title, creatorName, startTime, endTime, contentType);
        
        return new PageResult<>(total, size, page, dataList);
    }
    
    /**
     * 保存保护区介绍（包含文件上传处理）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveProtectedReservationInfoWithFiles(ProtectedReservationInfoDTO dto, MultipartFile[] videoFiles, MultipartFile[] photoFiles, MultipartFile[] carouselFiles, MultipartFile[] galleryFiles, MultipartFile[] audioFiles) throws Exception {
        System.out.println("开始保存保护区介绍，内容类型: " + dto.getContentType());
        
        try {
            // 获取当前登录用户ID
            Long currentUserId = dto.getCreateBy();
            System.out.println("当前登录用户ID: " + currentUserId);
            
            // 确保用户ID不为null，如果为null则使用默认值1L
            if (currentUserId == null) {
                currentUserId = 1L;
                System.out.println("用户ID为null，使用默认值: " + currentUserId);
            }
            
            // 判断是新增还是更新
            boolean isUpdate = dto.getId() != null;
            
            // 如果是更新操作，删除原有的所有文件
            if (isUpdate) {
                // 检查是否有任何新文件上传
                boolean hasNewFiles = (videoFiles != null && videoFiles.length > 0) ||
                                     (photoFiles != null && photoFiles.length > 0) ||
                                     (carouselFiles != null && carouselFiles.length > 0) ||
                                     (galleryFiles != null && galleryFiles.length > 0) ||
                                     (audioFiles != null && audioFiles.length > 0);
                
                // 只有当有新文件上传时才删除原有文件
                if (hasNewFiles) {
                    System.out.println("检测到新文件上传，删除原有文件");
                    deleteProtectedReservationInfoFiles(dto.getId());
                } else {
                    System.out.println("没有检测到新文件上传，保持原有文件不变");
                    // 获取原有的保护区介绍信息
                    ProtectedReservationInfo existingEntity = this.getById(dto.getId());
                    if (existingEntity == null) {
                        throw new RuntimeException("未找到ID为" + dto.getId() + "的保护区介绍");
                    }
                    
                    // 保持原有文件不变
                    dto.setVideoFileIds(existingEntity.getVideoFileIds());
                    dto.setContentImageIds(existingEntity.getContentImageIds());
                    dto.setCarouselFileIds(existingEntity.getCarouselFileIds());
                    dto.setGalleryFileIds(existingEntity.getGalleryFileIds());
                    dto.setAudioFileIds(existingEntity.getAudioFileIds());
                }
            }
            
            // 处理文章内容图片
            if (photoFiles != null && photoFiles.length > 0) {
                System.out.println("开始处理文章内容图片，数量: " + photoFiles.length);
                List<Long> photoFileIds = processPhotoFiles(photoFiles, currentUserId);
                System.out.println("处理完成，获取到文章内容图片ID: " + photoFileIds);
                dto.setContentImageIds(photoFileIds);
            }
            
            // 处理轮播图
            if (carouselFiles != null && carouselFiles.length > 0) {
                System.out.println("开始处理轮播图，数量: " + carouselFiles.length);
                List<Long> carouselFileIds = processPhotoFiles(carouselFiles, currentUserId);
                System.out.println("处理完成，获取到轮播图ID: " + carouselFileIds);
                dto.setCarouselFileIds(carouselFileIds);
            }
            
            // 处理画廊图片
            if (galleryFiles != null && galleryFiles.length > 0) {
                System.out.println("开始处理画廊图片，数量: " + galleryFiles.length);
                List<Long> galleryFileIds = processPhotoFiles(galleryFiles, currentUserId);
                System.out.println("处理完成，获取到画廊图片ID: " + galleryFileIds);
                dto.setGalleryFileIds(galleryFileIds);
            }
            
            // 处理音频文件
            if (audioFiles != null && audioFiles.length > 0) {
                System.out.println("开始处理音频文件，数量: " + audioFiles.length);
                List<Long> audioFileIds = processAudioFiles(audioFiles, currentUserId);
                System.out.println("处理完成，获取到音频文件ID: " + audioFileIds);
                dto.setAudioFileIds(audioFileIds);
            }
            
            // 处理视频文件上传
            if (videoFiles != null && videoFiles.length > 0) {
                System.out.println("开始处理视频文件，数量: " + videoFiles.length);
                List<Long> videoFileIds = processVideoFiles(videoFiles, currentUserId);
                System.out.println("处理完成，获取到视频文件ID: " + videoFileIds);

                // 设置视频文件ID到DTO
                dto.setVideoFileIds(videoFileIds);
                System.out.println("更新后的视频文件ID列表: " + dto.getVideoFileIds());
            } else if (!isUpdate) {
                // 如果是新增操作且没有上传视频，则设置为空列表
                dto.setVideoFileIds(new java.util.ArrayList<>());
                System.out.println("没有上传视频，设置为空列表");
            }
            
            // 创建新的实体对象，并从DTO复制属性
            ProtectedReservationInfo entity = new ProtectedReservationInfo();
            BeanUtils.copyProperties(dto, entity);
            
            if (isUpdate) {
                // 更新操作
                entity.setUpdateTime(LocalDateTime.now());
            } else {
                // 新增操作
                entity.setCreateTime(LocalDateTime.now());
                entity.setUpdateTime(LocalDateTime.now());
                entity.setDeleted((byte) 0); // 默认未删除
            }
            
            // 确保所有文件ID被正确设置 - 使用Jackson将List<Long>转换为JSON字符串
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            
            if (dto.getContentImageIds() != null) {
                System.out.println("设置文章内容图片ID到实体: " + dto.getContentImageIds());
                try {
                    String contentImageIdsJson = objectMapper.writeValueAsString(dto.getContentImageIds());
                    System.out.println("转换后的JSON字符串: " + contentImageIdsJson);
                    entity.setContentImageIds(dto.getContentImageIds());
                } catch (Exception e) {
                    System.err.println("转换文章内容图片ID为JSON字符串时发生异常: " + e.getMessage());
                }
            }
            
            if (dto.getCarouselFileIds() != null) {
                System.out.println("设置轮播图ID到实体: " + dto.getCarouselFileIds());
                try {
                    String carouselFileIdsJson = objectMapper.writeValueAsString(dto.getCarouselFileIds());
                    System.out.println("转换后的JSON字符串: " + carouselFileIdsJson);
                    entity.setCarouselFileIds(dto.getCarouselFileIds());
                } catch (Exception e) {
                    System.err.println("转换轮播图ID为JSON字符串时发生异常: " + e.getMessage());
                }
            }
            
            if (dto.getGalleryFileIds() != null) {
                System.out.println("设置画廊图片ID到实体: " + dto.getGalleryFileIds());
                try {
                    String galleryFileIdsJson = objectMapper.writeValueAsString(dto.getGalleryFileIds());
                    System.out.println("转换后的JSON字符串: " + galleryFileIdsJson);
                    entity.setGalleryFileIds(dto.getGalleryFileIds());
                } catch (Exception e) {
                    System.err.println("转换画廊图片ID为JSON字符串时发生异常: " + e.getMessage());
                }
            }
            
            if (dto.getAudioFileIds() != null) {
                System.out.println("设置音频文件ID到实体: " + dto.getAudioFileIds());
                try {
                    String audioFileIdsJson = objectMapper.writeValueAsString(dto.getAudioFileIds());
                    System.out.println("转换后的JSON字符串: " + audioFileIdsJson);
                    entity.setAudioFileIds(dto.getAudioFileIds());
                } catch (Exception e) {
                    System.err.println("转换音频文件ID为JSON字符串时发生异常: " + e.getMessage());
                }
            }
            
            if (dto.getVideoFileIds() != null && !dto.getVideoFileIds().isEmpty()) {
                System.out.println("设置视频文件ID到实体: " + dto.getVideoFileIds());
                try {
                    String videoFileIdsJson = objectMapper.writeValueAsString(dto.getVideoFileIds());
                    System.out.println("转换后的JSON字符串: " + videoFileIdsJson);
                    entity.setVideoFileIds(dto.getVideoFileIds());
                } catch (Exception e) {
                    System.err.println("转换视频文件ID为JSON字符串时发生异常: " + e.getMessage());
                }
            }
            
            try {
                if (isUpdate) {
                    // 更新操作
                    System.out.println("执行更新操作，ID: " + entity.getId());
                    boolean updateResult = this.updateById(entity);
                    System.out.println("更新保护区介绍结果: " + updateResult + ", ID: " + entity.getId());
                    return updateResult;
                } else {
                    // 新增操作
                    System.out.println("执行新增操作");
                    boolean saveResult = this.save(entity);
                    System.out.println("保存保护区介绍结果: " + saveResult + ", ID: " + entity.getId());
                    return saveResult;
                }
            } catch (Exception e) {
                System.err.println("保存/更新保护区介绍时发生异常: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        } catch (Exception e) {
            System.err.println("保存保护区介绍时发生异常: " + e.getMessage());
            e.printStackTrace();
            throw e; // 重新抛出异常，确保事务回滚
        }
    }
    
    /**
     * 保存保护区介绍（包含文件上传处理）- 兼容旧版本
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveProtectedReservationInfoWithFiles(ProtectedReservationInfoDTO dto, MultipartFile[] videoFiles) throws Exception {
        return saveProtectedReservationInfoWithFiles(dto, videoFiles, null, null, null, null);
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
            System.out.println("原始文件ID列表: " + fileIds);
            System.out.println("文件ID列表类型: " + fileIds.getClass().getName());
            System.out.println("文件ID列表大小: " + fileIds.size());
            
            // 打印每个原始ID的类型和值
            System.out.println("原始ID详情:");
            for (int i = 0; i < fileIds.size(); i++) {
                Object fileIdObj = fileIds.get(i);
                System.out.println("  ID[" + i + "] = " + fileIdObj + ", 类型: " + (fileIdObj != null ? fileIdObj.getClass().getName() : "null"));
            }
            
            // 创建一个新的列表，先将所有ID转换为字符串，再转换为Long，避免精度问题
            List<String> fileIdStrings = new java.util.ArrayList<>();
            List<Long> validFileIds = new java.util.ArrayList<>();
            
            // 第一步：将所有ID转换为字符串
            System.out.println("第一步：将所有ID转换为字符串");
            for (Object fileIdObj : fileIds) {
                try {
                    String fileIdStr = null;
                    
                    // 处理不同类型的fileId，全部转换为字符串
                    if (fileIdObj instanceof Long) {
                        fileIdStr = fileIdObj.toString();
                        System.out.println("  Long类型ID: " + fileIdObj + " -> " + fileIdStr);
                    } else if (fileIdObj instanceof Integer) {
                        fileIdStr = fileIdObj.toString();
                        System.out.println("  Integer类型ID: " + fileIdObj + " -> " + fileIdStr);
                    } else if (fileIdObj instanceof String) {
                        fileIdStr = (String) fileIdObj;
                        System.out.println("  String类型ID: " + fileIdObj);
                    } else if (fileIdObj instanceof Number) {
                        fileIdStr = fileIdObj.toString();
                        System.out.println("  Number类型ID: " + fileIdObj + " -> " + fileIdStr);
                    } else if (fileIdObj != null) {
                        fileIdStr = fileIdObj.toString();
                        System.out.println("  其他类型ID: " + fileIdObj + ", 类型: " + fileIdObj.getClass().getName() + " -> " + fileIdStr);
                    } else {
                        System.err.println("  文件ID为null，跳过");
                        continue;
                    }
                    
                    if (fileIdStr != null && !fileIdStr.isEmpty()) {
                        // 确保字符串只包含数字
                        if (fileIdStr.matches("\\d+")) {
                            fileIdStrings.add(fileIdStr);
                            System.out.println("  添加有效ID字符串: " + fileIdStr);
                        } else {
                            System.err.println("  文件ID不是有效的数字: " + fileIdStr);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("  处理文件ID时发生异常: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("转换后的文件ID字符串列表: " + fileIdStrings);
            System.out.println("字符串列表大小: " + fileIdStrings.size());
            
            // 第二步：将字符串ID转换为Long
            System.out.println("第二步：将字符串ID转换为Long");
            for (String fileIdStr : fileIdStrings) {
                try {
                    // 使用BigInteger处理大整数，避免精度问题
                    java.math.BigInteger bigInt = new java.math.BigInteger(fileIdStr);
                    // 检查是否超出Long范围
                    if (bigInt.compareTo(java.math.BigInteger.valueOf(Long.MAX_VALUE)) > 0 ||
                        bigInt.compareTo(java.math.BigInteger.valueOf(Long.MIN_VALUE)) < 0) {
                        System.err.println("  ID超出Long范围: " + fileIdStr);
                        continue;
                    }
                    
                    Long fileId = Long.parseLong(fileIdStr);
                    validFileIds.add(fileId);
                    System.out.println("  转换为Long: " + fileIdStr + " -> " + fileId);
                } catch (NumberFormatException e) {
                    System.err.println("  无法将字符串转换为Long: " + fileIdStr + ", 错误: " + e.getMessage());
                }
            }
            
            if (validFileIds.isEmpty()) {
                System.out.println("没有有效的" + fileType + "文件ID需要删除");
                return;
            }
            
            System.out.println("有效的" + fileType + "文件ID列表: " + validFileIds);
            
            // 先查询所有文件记录，用于删除MinIO中的文件
            for (Long fileId : validFileIds) {
                try {
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
            
            // 使用新的批量删除方法删除数据库中的文件记录
            try {
                if (!validFileIds.isEmpty()) {
                    // 尝试使用Long类型ID删除
                    try {
                        int result = resourceFileMapper.deleteByIds(validFileIds);
                        System.out.println("使用Long类型ID批量删除数据库中的" + fileType + "文件记录，数量: " + result);
                    } catch (Exception e) {
                        System.err.println("使用Long类型ID批量删除失败: " + e.getMessage());
                    }
                }
                
                // 同时尝试使用字符串ID删除，避免精度问题
                if (!fileIdStrings.isEmpty()) {
                    try {
                        int result = resourceFileMapper.deleteByStringIds(fileIdStrings);
                        System.out.println("使用String类型ID批量删除数据库中的" + fileType + "文件记录，数量: " + result);
                    } catch (Exception e) {
                        System.err.println("使用String类型ID批量删除失败: " + e.getMessage());
                    }
                }
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
     * 处理视频文件上传
     */
    @Override
    public List<Long> processVideoFiles(MultipartFile[] videoFiles) throws Exception {
        // 使用默认用户ID 1L
        return processVideoFiles(videoFiles, 1L);
    }
    
    /**
     * 处理视频文件上传（带用户ID）
     */
    @Override
    public List<Long> processVideoFiles(MultipartFile[] videoFiles, Long userId) throws Exception {
        System.out.println("processVideoFiles方法接收到的用户ID: " + userId); // 添加日志
        
        List<Long> videoFileIds = new java.util.ArrayList<>();
        
        if (videoFiles != null && videoFiles.length > 0) {
            for (MultipartFile videoFile : videoFiles) {
                if (videoFile != null && !videoFile.isEmpty()) {
                    try {
                        // 根据文件类型选择正确的桶名称
                        String bucketName = minioService.getContentManagementVideoBucket(); // 视频文件存储在content-management桶中
                        
                        String fileKey = java.util.UUID.randomUUID().toString() + "_" + videoFile.getOriginalFilename();
                        fileUploadUtil.putObject(bucketName, fileKey, videoFile.getInputStream(), videoFile.getSize(), videoFile.getContentType());
                        
                        // 保存文件信息到数据库
                        ResourceFile resourceFile = new ResourceFile();
                        resourceFile.setFileName(videoFile.getOriginalFilename());
                        resourceFile.setFileKey(fileKey); // 存储文件key而不是URL
                        resourceFile.setBucketName(bucketName);
                        resourceFile.setFileSize(videoFile.getSize());
                        resourceFile.setMimeType(videoFile.getContentType());
                        resourceFile.setFileType(2); // 2-视频类型
                        
                        // 设置其他必填字段的默认值
                        resourceFile.setWidth(0);
                        resourceFile.setHeight(0);
                        
                        // 确保用户ID不为null
                        if (userId == null) {
                            System.out.println("警告：用户ID为null，使用默认值1L");
                            userId = 1L;
                        }
                        
                        System.out.println("设置到ResourceFile的用户ID: " + userId); // 添加日志
                        resourceFile.setUploadUserId(userId); // 使用传入的用户ID
                        resourceFile.setIsTemp(0); // 非临时文件
                        resourceFile.setCreateBy(userId); // 使用传入的用户ID
                        resourceFile.setUpdateBy(userId); // 使用传入的用户ID
                        
                        resourceFile.setCreateTime(LocalDateTime.now());
                        resourceFile.setUpdateTime(LocalDateTime.now());
                        
                        int result = resourceFileMapper.insert(resourceFile);
                        if (result > 0) {
                            System.out.println("成功插入视频文件记录，ID: " + resourceFile.getId() + ", 上传用户ID: " + userId);
                            videoFileIds.add(resourceFile.getId());
                        } else {
                            System.err.println("插入视频文件记录失败");
                        }
                    } catch (Exception e) {
                        System.err.println("处理视频文件时发生异常: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return videoFileIds;
    }
    
    /**
     * 处理音频文件上传
     */
    @Override
    public List<Long> processAudioFiles(MultipartFile[] audioFiles, Long userId) throws Exception {
        System.out.println("processAudioFiles方法接收到的用户ID: " + userId); // 添加日志
        
        List<Long> audioFileIds = new java.util.ArrayList<>();
        
        if (audioFiles != null && audioFiles.length > 0) {
            for (MultipartFile audioFile : audioFiles) {
                if (audioFile != null && !audioFile.isEmpty()) {
                    try {
                        // 根据文件类型选择正确的桶名称
                        String bucketName = minioService.getContentManagementAudioBucket(); // 音频文件存储在content-management-audio桶中
                        
                        String fileKey = java.util.UUID.randomUUID().toString() + "_" + audioFile.getOriginalFilename();
                        fileUploadUtil.putObject(bucketName, fileKey, audioFile.getInputStream(), audioFile.getSize(), audioFile.getContentType());
                        
                        // 保存文件信息到数据库
                        ResourceFile resourceFile = new ResourceFile();
                        resourceFile.setFileName(audioFile.getOriginalFilename());
                        resourceFile.setFileKey(fileKey);
                        resourceFile.setBucketName(bucketName);
                        resourceFile.setFileSize(audioFile.getSize());
                        resourceFile.setMimeType(audioFile.getContentType());
                        resourceFile.setFileType(3); // 3-音频类型
                        
                        // 设置其他必填字段的默认值
                        resourceFile.setWidth(0);
                        resourceFile.setHeight(0);
                        
                        // 确保用户ID不为null
                        if (userId == null) {
                            System.out.println("警告：用户ID为null，使用默认值1L");
                            userId = 1L;
                        }
                        
                        System.out.println("设置到ResourceFile的用户ID: " + userId); // 添加日志
                        resourceFile.setUploadUserId(userId);
                        resourceFile.setIsTemp(0);
                        resourceFile.setCreateBy(userId);
                        resourceFile.setUpdateBy(userId);
                        
                        resourceFile.setCreateTime(LocalDateTime.now());
                        resourceFile.setUpdateTime(LocalDateTime.now());
                        
                        int result = resourceFileMapper.insert(resourceFile);
                        if (result > 0) {
                            System.out.println("成功插入音频文件记录，ID: " + resourceFile.getId() + ", 上传用户ID: " + userId);
                            audioFileIds.add(resourceFile.getId());
                        } else {
                            System.err.println("插入音频文件记录失败");
                        }
                    } catch (Exception e) {
                        System.err.println("处理音频文件时发生异常: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return audioFileIds;
    }
    
    /**
     * 从JSON字符串中提取ID列表
     * @param jsonStr JSON字符串
     * @return ID字符串列表
     */
    private List<String> extractIdsFromJson(String jsonStr) {
        List<String> idStrings = new java.util.ArrayList<>();
        
        try {
            // 首先尝试使用正则表达式提取数字
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d+");
            java.util.regex.Matcher matcher = pattern.matcher(jsonStr);
            
            while (matcher.find()) {
                String idStr = matcher.group();
                idStrings.add(idStr);
                System.out.println("  使用正则表达式提取ID: " + idStr);
            }
            
            // 如果正则表达式没有提取到任何ID，尝试使用Jackson解析
            if (idStrings.isEmpty()) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    // 尝试解析为数组
                    Object[] array = objectMapper.readValue(jsonStr, Object[].class);
                    
                    for (Object item : array) {
                        String idStr = String.valueOf(item);
                        if (idStr.matches("\\d+")) {
                            idStrings.add(idStr);
                            System.out.println("  使用Jackson提取ID: " + idStr);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("  Jackson解析失败: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("提取ID时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
        
        return idStrings;
    }
    
    /**
     * 根据字符串ID列表删除文件
     * @param fileIdStrings 文件ID字符串列表
     * @param fileType 文件类型描述（用于日志）
     */
    private void deleteFilesByStringIds(List<String> fileIdStrings, String fileType) {
        try {
            if (fileIdStrings == null || fileIdStrings.isEmpty()) {
                System.out.println("没有" + fileType + "文件需要删除");
                return;
            }
            
            System.out.println("=== 开始删除" + fileType + "文件（使用字符串ID） ===");
            System.out.println("文件ID字符串列表: " + fileIdStrings);
            System.out.println("文件ID列表大小: " + fileIdStrings.size());
            
            // 先查询所有文件记录，用于删除MinIO中的文件
            for (String fileIdStr : fileIdStrings) {
                try {
                    // 转换为Long，用于查询
                    Long fileId = Long.parseLong(fileIdStr);
                    
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
            
            // 使用字符串ID批量删除数据库中的文件记录
            try {
                int result = resourceFileMapper.deleteByStringIds(fileIdStrings);
                System.out.println("使用String类型ID批量删除数据库中的" + fileType + "文件记录，数量: " + result);
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
     * 处理照片文件上传
     */
    @Override
    public List<Long> processPhotoFiles(MultipartFile[] photoFiles, Long userId) throws Exception {
        System.out.println("processPhotoFiles方法接收到的用户ID: " + userId); // 添加日志
        
        List<Long> photoFileIds = new java.util.ArrayList<>();
        
        if (photoFiles != null && photoFiles.length > 0) {
            for (MultipartFile photoFile : photoFiles) {
                if (photoFile != null && !photoFile.isEmpty()) {
                    try {
                        // 根据文件类型选择正确的桶名称
                        String bucketName = minioService.getContentManagementPhotoBucket(); // 照片文件存储在content-management-photo桶中
                        
                        String fileKey = java.util.UUID.randomUUID().toString() + "_" + photoFile.getOriginalFilename();
                        fileUploadUtil.putObject(bucketName, fileKey, photoFile.getInputStream(), photoFile.getSize(), photoFile.getContentType());
                        
                        // 保存文件信息到数据库
                        ResourceFile resourceFile = new ResourceFile();
                        resourceFile.setFileName(photoFile.getOriginalFilename());
                        resourceFile.setFileKey(fileKey);
                        resourceFile.setBucketName(bucketName);
                        resourceFile.setFileSize(photoFile.getSize());
                        resourceFile.setMimeType(photoFile.getContentType());
                        resourceFile.setFileType(1); // 1-图片类型
                        
                        // 设置其他必填字段的默认值
                        resourceFile.setWidth(0);
                        resourceFile.setHeight(0);
                        
                        // 确保用户ID不为null
                        if (userId == null) {
                            System.out.println("警告：用户ID为null，使用默认值1L");
                            userId = 1L;
                        }
                        
                        System.out.println("设置到ResourceFile的用户ID: " + userId); // 添加日志
                        resourceFile.setUploadUserId(userId);
                        resourceFile.setIsTemp(0);
                        resourceFile.setCreateBy(userId);
                        resourceFile.setUpdateBy(userId);
                        
                        resourceFile.setCreateTime(LocalDateTime.now());
                        resourceFile.setUpdateTime(LocalDateTime.now());
                        
                        int result = resourceFileMapper.insert(resourceFile);
                        if (result > 0) {
                            System.out.println("成功插入照片文件记录，ID: " + resourceFile.getId() + ", 上传用户ID: " + userId);
                            photoFileIds.add(resourceFile.getId());
                        } else {
                            System.err.println("插入照片文件记录失败");
                        }
                    } catch (Exception e) {
                        System.err.println("处理照片文件时发生异常: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return photoFileIds;
    }
    
}
