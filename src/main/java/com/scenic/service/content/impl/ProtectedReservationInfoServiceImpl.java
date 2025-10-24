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
        
        // 物理删除记录（不删除关联的文件）
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
     * 根据ID获取保护区介绍（包含原始IDs信息）
     */
    @Override
    public com.scenic.dto.content.ProtectedReservationInfoWithIdsDTO getProtectedReservationInfoWithIdsById(Long id) {
        // 获取实体对象
        ProtectedReservationInfo entity = this.getById(id);
        if (entity == null) {
            return null;
        }
        
        // 查询原始数据（包含原始JSON字符串）
        java.util.Map<String, Object> rawData = protectedReservationInfoMapper.selectRawById(id);
        
        // 创建DTO对象
        com.scenic.dto.content.ProtectedReservationInfoWithIdsDTO dto = new com.scenic.dto.content.ProtectedReservationInfoWithIdsDTO();
        BeanUtils.copyProperties(entity, dto);
        
        // 设置原始JSON字符串
        if (rawData != null) {
            dto.setContentImageIdsRaw((String) rawData.get("content_image_ids"));
            dto.setCarouselFileIdsRaw((String) rawData.get("carousel_file_ids"));
            dto.setGalleryFileIdsRaw((String) rawData.get("gallery_file_ids"));
            dto.setAudioFileIdsRaw((String) rawData.get("audio_file_ids"));
            dto.setVideoFileIdsRaw((String) rawData.get("video_file_ids"));
        }
        
        return dto;
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
            
            if (isUpdate) {
                // 更新操作 - 处理文件的精确删除和新增
                handleUpdateFiles(dto, videoFiles, photoFiles, carouselFiles, galleryFiles, audioFiles, currentUserId);
            } else {
                // 新增操作 - 处理文件上传
                handleCreateFiles(dto, videoFiles, photoFiles, carouselFiles, galleryFiles, audioFiles, currentUserId);
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
    
    /**
     * 处理更新操作的文件 - 根据内容类型处理不同逻辑
     */
    private void handleUpdateFiles(ProtectedReservationInfoDTO dto, MultipartFile[] videoFiles, MultipartFile[] photoFiles, MultipartFile[] carouselFiles, MultipartFile[] galleryFiles, MultipartFile[] audioFiles, Long currentUserId) throws Exception {
        System.out.println("=== 开始处理更新操作的文件 ===");
        System.out.println("接收到的DTO: " + dto);
        System.out.println("内容类型: " + dto.getContentType());
        System.out.println("DTO中的删除文件映射: " + dto.getDeletedFileIds());
        
        // 根据内容类型处理不同的文件更新逻辑
        if (dto.getContentType() != null && dto.getContentType() == 2) {
            // 视频类型：直接删除原有文件，新增上传的文件
            System.out.println("处理视频类型内容更新");
            handleVideoUpdateFiles(dto, videoFiles, currentUserId);
        } else {
            // 文章类型：保持原有的精确删除和新增逻辑
            System.out.println("处理文章类型内容更新");
            handleArticleUpdateFiles(dto, photoFiles, carouselFiles, galleryFiles, audioFiles, currentUserId);
        }
        
        System.out.println("=== 完成处理更新操作的文件 ===");
    }
    
    /**
     * 处理视频类型更新操作的文件
     */
    private void handleVideoUpdateFiles(ProtectedReservationInfoDTO dto, MultipartFile[] videoFiles, Long currentUserId) throws Exception {
        System.out.println("=== 开始处理视频类型更新操作的文件 ===");
        
        // 删除原有的视频文件
        if (dto.getVideoFileIds() != null && !dto.getVideoFileIds().isEmpty()) {
            System.out.println("删除原有的视频文件: " + dto.getVideoFileIds());
            deleteSpecificFilesByIds(dto.getVideoFileIds(), "视频");
        }
        
        // 处理新上传的视频文件
        List<Long> newVideoFileIds = new java.util.ArrayList<>();
        if (videoFiles != null && videoFiles.length > 0) {
            System.out.println("处理新上传的视频文件，数量: " + videoFiles.length);
            newVideoFileIds = processVideoFiles(videoFiles, currentUserId);
            System.out.println("新上传的视频文件ID: " + newVideoFileIds);
        }
        
        // 设置视频文件ID
        dto.setVideoFileIds(newVideoFileIds);
        System.out.println("最终的视频文件ID: " + newVideoFileIds);
        
        System.out.println("=== 完成处理视频类型更新操作的文件 ===");
    }
    
    /**
     * 处理文章类型更新操作的文件 - 精确删除和新增
     */
    private void handleArticleUpdateFiles(ProtectedReservationInfoDTO dto, MultipartFile[] photoFiles, MultipartFile[] carouselFiles, MultipartFile[] galleryFiles, MultipartFile[] audioFiles, Long currentUserId) throws Exception {
        System.out.println("=== 开始处理文章类型更新操作的文件 ===");
        System.out.println("DTO中的删除文件映射: " + dto.getDeletedFileIds());
        
        // 处理删除指定的文件
        if (dto.getDeletedFileIds() != null && !dto.getDeletedFileIds().isEmpty()) {
            System.out.println("处理删除指定的文件: " + dto.getDeletedFileIds());
            
            // 删除轮播图文件
            List<Long> deletedCarouselIds = dto.getDeletedFileIds().get("carousel");
            if (deletedCarouselIds != null && !deletedCarouselIds.isEmpty()) {
                System.out.println("删除轮播图文件: " + deletedCarouselIds);
                deleteSpecificFilesByIds(deletedCarouselIds, "轮播图");
            }
            
            // 删除画廊文件
            List<Long> deletedGalleryIds = dto.getDeletedFileIds().get("gallery");
            if (deletedGalleryIds != null && !deletedGalleryIds.isEmpty()) {
                System.out.println("删除画廊文件: " + deletedGalleryIds);
                deleteSpecificFilesByIds(deletedGalleryIds, "画廊图片");
            }
            
            // 删除音频文件
            List<Long> deletedAudioIds = dto.getDeletedFileIds().get("audio");
            if (deletedAudioIds != null && !deletedAudioIds.isEmpty()) {
                System.out.println("删除音频文件: " + deletedAudioIds);
                deleteSpecificFilesByIds(deletedAudioIds, "音频");
            }
            
            // 删除内容图片文件
            List<Long> deletedContentImageIds = dto.getDeletedFileIds().get("contentImage");
            if (deletedContentImageIds != null && !deletedContentImageIds.isEmpty()) {
                System.out.println("删除内容图片文件: " + deletedContentImageIds);
                deleteSpecificFilesByIds(deletedContentImageIds, "内容图片");
            }
        }
        
        // 处理新增的文件
        List<Long> newContentImageIds = new java.util.ArrayList<>();
        List<Long> newCarouselFileIds = new java.util.ArrayList<>();
        List<Long> newGalleryFileIds = new java.util.ArrayList<>();
        List<Long> newAudioFileIds = new java.util.ArrayList<>();
        
        // 处理新增的内容图片
        if (photoFiles != null && photoFiles.length > 0) {
            System.out.println("处理新增的内容图片，数量: " + photoFiles.length);
            newContentImageIds = processPhotoFiles(photoFiles, currentUserId);
            System.out.println("新增内容图片ID: " + newContentImageIds);
        }
        
        // 处理新增的轮播图
        if (carouselFiles != null && carouselFiles.length > 0) {
            System.out.println("处理新增的轮播图，数量: " + carouselFiles.length);
            newCarouselFileIds = processPhotoFiles(carouselFiles, currentUserId);
            System.out.println("新增轮播图ID: " + newCarouselFileIds);
        }
        
        // 处理新增的画廊图片
        if (galleryFiles != null && galleryFiles.length > 0) {
            System.out.println("处理新增的画廊图片，数量: " + galleryFiles.length);
            newGalleryFileIds = processPhotoFiles(galleryFiles, currentUserId);
            System.out.println("新增画廊图片ID: " + newGalleryFileIds);
        }
        
        // 处理新增的音频文件
        if (audioFiles != null && audioFiles.length > 0) {
            System.out.println("处理新增的音频文件，数量: " + audioFiles.length);
            newAudioFileIds = processAudioFiles(audioFiles, currentUserId);
            System.out.println("新增音频文件ID: " + newAudioFileIds);
        }
        
        // 合并文件ID列表：先从原有列表中移除被删除的文件，然后添加新上传的文件
        // 内容图片
        List<Long> finalContentImageIds = new java.util.ArrayList<>();
        if (dto.getContentImageIds() != null) {
            finalContentImageIds.addAll(dto.getContentImageIds());
        }
        // 移除被删除的内容图片
        List<Long> deletedContentImageIds = dto.getDeletedFileIds() != null ? dto.getDeletedFileIds().get("contentImage") : null;
        if (deletedContentImageIds != null && !deletedContentImageIds.isEmpty()) {
            finalContentImageIds.removeAll(deletedContentImageIds);
        }
        // 添加新上传的内容图片
        finalContentImageIds.addAll(newContentImageIds);
        dto.setContentImageIds(finalContentImageIds);
        System.out.println("最终的内容图片ID: " + finalContentImageIds);
        
        // 轮播图
        List<Long> finalCarouselFileIds = new java.util.ArrayList<>();
        if (dto.getCarouselFileIds() != null) {
            finalCarouselFileIds.addAll(dto.getCarouselFileIds());
        }
        // 移除被删除的轮播图
        List<Long> deletedCarouselIds = dto.getDeletedFileIds() != null ? dto.getDeletedFileIds().get("carousel") : null;
        if (deletedCarouselIds != null && !deletedCarouselIds.isEmpty()) {
            finalCarouselFileIds.removeAll(deletedCarouselIds);
        }
        // 添加新上传的轮播图
        finalCarouselFileIds.addAll(newCarouselFileIds);
        dto.setCarouselFileIds(finalCarouselFileIds);
        System.out.println("最终的轮播图ID: " + finalCarouselFileIds);
        
        // 画廊图片
        List<Long> finalGalleryFileIds = new java.util.ArrayList<>();
        if (dto.getGalleryFileIds() != null) {
            finalGalleryFileIds.addAll(dto.getGalleryFileIds());
        }
        // 移除被删除的画廊图片
        List<Long> deletedGalleryIds = dto.getDeletedFileIds() != null ? dto.getDeletedFileIds().get("gallery") : null;
        if (deletedGalleryIds != null && !deletedGalleryIds.isEmpty()) {
            finalGalleryFileIds.removeAll(deletedGalleryIds);
        }
        // 添加新上传的画廊图片
        finalGalleryFileIds.addAll(newGalleryFileIds);
        dto.setGalleryFileIds(finalGalleryFileIds);
        System.out.println("最终的画廊图片ID: " + finalGalleryFileIds);
        
        // 音频文件
        List<Long> finalAudioFileIds = new java.util.ArrayList<>();
        if (dto.getAudioFileIds() != null) {
            finalAudioFileIds.addAll(dto.getAudioFileIds());
        }
        // 移除被删除的音频文件
        List<Long> deletedAudioIds = dto.getDeletedFileIds() != null ? dto.getDeletedFileIds().get("audio") : null;
        if (deletedAudioIds != null && !deletedAudioIds.isEmpty()) {
            finalAudioFileIds.removeAll(deletedAudioIds);
        }
        // 添加新上传的音频文件
        finalAudioFileIds.addAll(newAudioFileIds);
        dto.setAudioFileIds(finalAudioFileIds);
        System.out.println("最终的音频文件ID: " + finalAudioFileIds);
        
        System.out.println("=== 完成处理文章类型更新操作的文件 ===");
    }
    
    /**
     * 处理新增操作的文件
     */
    private void handleCreateFiles(ProtectedReservationInfoDTO dto, MultipartFile[] videoFiles, MultipartFile[] photoFiles, MultipartFile[] carouselFiles, MultipartFile[] galleryFiles, MultipartFile[] audioFiles, Long currentUserId) throws Exception {
        System.out.println("=== 开始处理新增操作的文件 ===");
        
        // 处理文章内容图片
        if (photoFiles != null && photoFiles.length > 0) {
            System.out.println("处理文章内容图片，数量: " + photoFiles.length);
            List<Long> photoFileIds = processPhotoFiles(photoFiles, currentUserId);
            System.out.println("处理完成，获取到文章内容图片ID: " + photoFileIds);
            dto.setContentImageIds(photoFileIds);
        }
        
        // 处理轮播图
        if (carouselFiles != null && carouselFiles.length > 0) {
            System.out.println("处理轮播图，数量: " + carouselFiles.length);
            List<Long> carouselFileIds = processPhotoFiles(carouselFiles, currentUserId);
            System.out.println("处理完成，获取到轮播图ID: " + carouselFileIds);
            dto.setCarouselFileIds(carouselFileIds);
        }
        
        // 处理画廊图片
        if (galleryFiles != null && galleryFiles.length > 0) {
            System.out.println("处理画廊图片，数量: " + galleryFiles.length);
            List<Long> galleryFileIds = processPhotoFiles(galleryFiles, currentUserId);
            System.out.println("处理完成，获取到画廊图片ID: " + galleryFileIds);
            dto.setGalleryFileIds(galleryFileIds);
        }
        
        // 处理音频文件
        if (audioFiles != null && audioFiles.length > 0) {
            System.out.println("处理音频文件，数量: " + audioFiles.length);
            List<Long> audioFileIds = processAudioFiles(audioFiles, currentUserId);
            System.out.println("处理完成，获取到音频文件ID: " + audioFileIds);
            dto.setAudioFileIds(audioFileIds);
        }
        
        // 处理视频文件上传
        if (videoFiles != null && videoFiles.length > 0) {
            System.out.println("处理视频文件，数量: " + videoFiles.length);
            List<Long> videoFileIds = processVideoFiles(videoFiles, currentUserId);
            System.out.println("处理完成，获取到视频文件ID: " + videoFileIds);
            dto.setVideoFileIds(videoFileIds);
            System.out.println("更新后的视频文件ID列表: " + dto.getVideoFileIds());
        } else {
            // 如果没有上传视频，则设置为空列表
            dto.setVideoFileIds(new java.util.ArrayList<>());
            System.out.println("没有上传视频，设置为空列表");
        }
        
        System.out.println("=== 完成处理新增操作的文件 ===");
    }
    
    /**
     * 根据文件ID列表删除指定的文件
     * @param fileIds 要删除的文件ID列表
     * @param fileType 文件类型描述（用于日志）
     */
    private void deleteSpecificFilesByIds(List<Long> fileIds, String fileType) {
        try {
            if (fileIds == null || fileIds.isEmpty()) {
                System.out.println("没有" + fileType + "文件需要删除");
                return;
            }
            
            System.out.println("=== 开始删除指定的" + fileType + "文件 ===");
            System.out.println("要删除的文件ID列表: " + fileIds);
            
            for (Long fileId : fileIds) {
                try {
                    if (fileId == null) {
                        System.out.println("文件ID为null，跳过删除");
                        continue;
                    }
                    
                    System.out.println("尝试删除" + fileType + "文件，ID: " + fileId);
                    
                    // 查询文件记录
                    ResourceFile resourceFile = resourceFileMapper.selectById(fileId);
                    if (resourceFile != null) {
                        System.out.println("找到文件记录: " + resourceFile.getFileName() + ", Bucket: " + resourceFile.getBucketName() + ", Key: " + resourceFile.getFileKey());
                        
                        // 从MinIO删除文件
                        try {
                            minioService.removeObject(resourceFile.getBucketName(), resourceFile.getFileKey());
                            System.out.println("从MinIO删除文件成功: " + resourceFile.getBucketName() + "/" + resourceFile.getFileKey());
                        } catch (Exception e) {
                            System.err.println("从MinIO删除文件失败: " + e.getMessage());
                        }
                        
                        // 从数据库删除记录
                        int result = resourceFileMapper.deleteById(fileId);
                        System.out.println("从数据库删除文件记录结果: " + (result > 0 ? "成功" : "失败"));
                    } else {
                        System.err.println("未找到ID为" + fileId + "的文件记录");
                    }
                } catch (Exception e) {
                    System.err.println("删除文件时发生异常: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("删除指定的" + fileType + "文件完成");
        } catch (Exception e) {
            System.err.println("删除指定的" + fileType + "文件时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}
