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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import com.scenic.utils.FileUploadUtil;
import com.scenic.mapper.ResourceFileMapper;
import com.scenic.entity.ResourceFile;
import java.io.InputStream;

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
            ProtectedReservationInfo existingEntity = this.getById(dto.getId());
            if (existingEntity == null) {
                throw new RuntimeException("未找到ID为" + dto.getId() + "的保护区介绍");
            }
            
            // 删除原有的所有类型文件记录
            deleteFilesByType(existingEntity.getVideoFileIds(), "视频");
            deleteFilesByType(existingEntity.getContentImageIds(), "内容图片");
            deleteFilesByType(existingEntity.getCarouselFileIds(), "轮播图");
            deleteFilesByType(existingEntity.getGalleryFileIds(), "图库");
            deleteFilesByType(existingEntity.getAudioFileIds(), "音频");
            
            // 处理上传的视频文件
            if (videoFiles != null && videoFiles.length > 0) {
                System.out.println("开始处理上传的视频文件，数量: " + videoFiles.length);
                List<Long> uploadedVideoFileIds = processVideoFiles(videoFiles, currentUserId);
                System.out.println("处理完成，获取到上传视频文件ID: " + uploadedVideoFileIds);
                
                // 设置视频文件ID到DTO
                dto.setVideoFileIds(uploadedVideoFileIds);
                System.out.println("更新后的视频文件ID列表: " + dto.getVideoFileIds());
            } else {
                // 如果没有上传新视频，则查询最新上传的视频文件
                ResourceFile latestVideo = resourceFileMapper.selectLatestVideoByType(2); // 2-视频类型
                if (latestVideo != null) {
                    List<Long> videoFileIds = new java.util.ArrayList<>();
                    videoFileIds.add(latestVideo.getId());
                    dto.setVideoFileIds(videoFileIds);
                    System.out.println("没有上传新视频，使用最新上传的视频文件ID: " + latestVideo.getId());
                } else {
                    // 如果没有找到视频文件，则设置为空列表
                    dto.setVideoFileIds(new java.util.ArrayList<>());
                    System.out.println("没有找到视频文件，设置为空列表");
                }
            }
            
            // 更新保护区介绍信息
            ProtectedReservationInfo entity = new ProtectedReservationInfo();
            BeanUtils.copyProperties(dto, entity);
            entity.setUpdateTime(LocalDateTime.now());
            
            // 确保视频文件ID被正确设置
            if (dto.getVideoFileIds() != null) {
                System.out.println("设置视频文件ID到实体: " + dto.getVideoFileIds());
                
                try {
                    // 使用Jackson将List<Long>转换为JSON字符串
                    String videoFileIdsJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(dto.getVideoFileIds());
                    System.out.println("转换后的JSON字符串: " + videoFileIdsJson);
                    
                    // 手动设置videoFileIds，确保不会被BeanUtils.copyProperties忽略
                    entity.setVideoFileIds(dto.getVideoFileIds());
                } catch (Exception e) {
                    System.err.println("转换视频文件ID为JSON字符串时发生异常: " + e.getMessage());
                }
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
     * 删除保护区介绍（逻辑删除）
     */
    @Override
    @Transactional
    public boolean deleteProtectedReservationInfo(Long id) {
        ProtectedReservationInfo entity = new ProtectedReservationInfo();
        entity.setId(id);
        entity.setDeleted((byte) 1); // 逻辑删除
        entity.setUpdateTime(LocalDateTime.now());
        return this.updateById(entity);
    }
    
    /**
     * 根据ID获取保护区介绍
     */
    @Override
    public ProtectedReservationInfoDTO getProtectedReservationInfoById(Long id) {
        ProtectedReservationInfo entity = this.getById(id);
        if (entity == null || entity.getDeleted() == 1) {
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
        queryWrapper.eq(ProtectedReservationInfo::getDeleted, (byte) 0); // 只查询未删除的
        
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
     * 获取所有未删除的保护区介绍列表
     */
    @Override
    public List<ProtectedReservationInfoDTO> getAllProtectedReservationInfo() {
        LambdaQueryWrapper<ProtectedReservationInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProtectedReservationInfo::getDeleted, (byte) 0);
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
    public PageResult<ProtectedReservationInfoEnhancedDTO> getProtectedReservationInfoPageEnhanced(
            Integer page, Integer size, String title, String creatorName, 
            LocalDateTime startTime, LocalDateTime endTime, Byte contentType) {
        
        int offset = (page - 1) * size;
        
        // 查询数据
        List<ProtectedReservationInfoEnhancedDTO> dataList = protectedReservationInfoMapper.selectPageEnhanced(
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
    public boolean saveProtectedReservationInfoWithFiles(ProtectedReservationInfoDTO dto, MultipartFile[] videoFiles) throws Exception {
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
            
            // 处理视频文件上传
            if (videoFiles != null && videoFiles.length > 0) {
                System.out.println("开始处理视频文件，数量: " + videoFiles.length);
                List<Long> videoFileIds = processVideoFiles(videoFiles, currentUserId);
                System.out.println("处理完成，获取到视频文件ID: " + videoFileIds);
                
                // 设置视频文件ID到DTO
                dto.setVideoFileIds(videoFileIds);
                System.out.println("更新后的视频文件ID列表: " + dto.getVideoFileIds());
            } else {
                // 如果没有上传视频，则查询最新上传的视频文件
                ResourceFile latestVideo = resourceFileMapper.selectLatestVideoByType(2); // 2-视频类型
                if (latestVideo != null) {
                    List<Long> videoFileIds = new java.util.ArrayList<>();
                    videoFileIds.add(latestVideo.getId());
                    dto.setVideoFileIds(videoFileIds);
                    System.out.println("没有上传视频，使用最新上传的视频文件ID: " + latestVideo.getId());
                } else {
                    // 如果没有找到视频文件，则设置为空列表
                    dto.setVideoFileIds(new java.util.ArrayList<>());
                    System.out.println("没有找到视频文件，设置为空列表");
                }
            }
            
            // 保存保护区介绍信息
            ProtectedReservationInfo entity = new ProtectedReservationInfo();
            BeanUtils.copyProperties(dto, entity);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            entity.setDeleted((byte) 0); // 默认未删除
            
            // 确保视频文件ID被正确设置
            if (dto.getVideoFileIds() != null && !dto.getVideoFileIds().isEmpty()) {
                System.out.println("设置视频文件ID到实体: " + dto.getVideoFileIds());
                
                try {
                    // 使用Jackson将List<Long>转换为JSON字符串
                    String videoFileIdsJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(dto.getVideoFileIds());
                    System.out.println("转换后的JSON字符串: " + videoFileIdsJson);
                    
                    // 手动设置videoFileIds，确保不会被BeanUtils.copyProperties忽略
                    entity.setVideoFileIds(dto.getVideoFileIds());
                } catch (Exception e) {
                    System.err.println("转换视频文件ID为JSON字符串时发生异常: " + e.getMessage());
                }
                
                // 打印SQL语句前的实体状态
                System.out.println("保存前的实体: " + entity);
                System.out.println("视频文件ID: " + entity.getVideoFileIds());
            }
            
            boolean saveResult = this.save(entity);
            System.out.println("保存保护区介绍结果: " + saveResult + ", ID: " + entity.getId());
            
            return saveResult;
        } catch (Exception e) {
            System.err.println("保存保护区介绍时发生异常: " + e.getMessage());
            e.printStackTrace();
            throw e; // 重新抛出异常，确保事务回滚
        }
    }
    
    /**
     * 根据文件ID列表删除文件
     * @param fileIds 文件ID列表
     * @param fileType 文件类型描述（用于日志）
     */
    private void deleteFilesByType(List<Long> fileIds, String fileType) {
        if (fileIds != null && !fileIds.isEmpty()) {
            System.out.println("删除原有的" + fileType + "文件ID: " + fileIds);
            
            System.out.println("文件ID列表类型: " + fileIds.getClass().getName());
            System.out.println("文件ID列表内容: " + fileIds);
            
            // 先查询所有文件记录，用于删除MinIO中的文件
            for (Long fileId : fileIds) {
                if (fileId != null) {
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
            }
            
            // 使用新的批量删除方法删除数据库中的文件记录
            try {
                int result = resourceFileMapper.deleteByIds(fileIds);
                System.out.println("已批量删除数据库中的" + fileType + "文件记录，数量: " + result);
            } catch (Exception e) {
                System.err.println("批量删除" + fileType + "文件记录时发生异常: " + e.getMessage());
                e.printStackTrace();
            }
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
        List<Long> videoFileIds = new java.util.ArrayList<>();
        
        if (videoFiles != null && videoFiles.length > 0) {
            for (MultipartFile videoFile : videoFiles) {
                if (videoFile != null && !videoFile.isEmpty()) {
                    try {
                        // 根据文件类型选择正确的桶名称
                        String bucketName = "content-management"; // 视频文件存储在content-management桶中
                        
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
    public List<Long> processAudioFiles(MultipartFile[] audioFiles, Long userId) throws Exception {
        List<Long> audioFileIds = new java.util.ArrayList<>();
        
        if (audioFiles != null && audioFiles.length > 0) {
            for (MultipartFile audioFile : audioFiles) {
                if (audioFile != null && !audioFile.isEmpty()) {
                    try {
                        // 根据文件类型选择正确的桶名称
                        String bucketName = "content-management-audio"; // 音频文件存储在content-management-audio桶中
                        
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
     * 处理照片文件上传
     */
    public List<Long> processPhotoFiles(MultipartFile[] photoFiles, Long userId) throws Exception {
        List<Long> photoFileIds = new java.util.ArrayList<>();
        
        if (photoFiles != null && photoFiles.length > 0) {
            for (MultipartFile photoFile : photoFiles) {
                if (photoFile != null && !photoFile.isEmpty()) {
                    try {
                        // 根据文件类型选择正确的桶名称
                        String bucketName = "content-management-photo"; // 照片文件存储在content-management-photo桶中
                        
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
