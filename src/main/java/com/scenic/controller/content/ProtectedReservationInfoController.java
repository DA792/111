package com.scenic.controller.content;

import com.scenic.common.dto.Result;
import com.scenic.dto.content.ProtectedReservationInfoDTO;
import com.scenic.dto.content.ProtectedReservationInfoEnhancedDTO;
import com.scenic.service.content.ProtectedReservationInfoService;
import com.scenic.common.dto.PageResult;
import com.scenic.utils.FileUploadUtil;
import com.scenic.mapper.ResourceFileMapper;
import com.scenic.entity.ResourceFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.io.InputStream;

/**
 * 保护区介绍控制器
 */
@RestController
@RequestMapping("/api/manage/content/protected-reservation")
public class ProtectedReservationInfoController {
    
    @Resource
    private ProtectedReservationInfoService protectedReservationInfoService;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;
    
    @Autowired
    private ResourceFileMapper resourceFileMapper;
    
    @Autowired
    private com.scenic.mapper.content.ProtectedReservationInfoMapper protectedReservationInfoMapper;
    
    @Autowired
    private com.scenic.utils.JwtUtil jwtUtil;
    
    /**
     * 新增保护区介绍
     * 根据内容类型处理不同的上传方式：
     * 1. 文章类型：处理富文本内容和图片
     * 2. 视频类型：处理视频文件上传
     */
    @PostMapping
    public Result<Boolean> saveProtectedReservationInfo(
            @RequestParam(value = "videoFiles", required = false) MultipartFile[] videoFiles,
            @RequestParam(value = "photoFiles", required = false) MultipartFile[] photoFiles,
            @RequestParam(value = "carouselFiles", required = false) MultipartFile[] carouselFiles,
            @RequestParam(value = "galleryFiles", required = false) MultipartFile[] galleryFiles,
            @RequestParam(value = "audioFiles", required = false) MultipartFile[] audioFiles,
            @RequestParam(value = "data", required = false) String data,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // 从Authorization头中获取当前登录用户ID
            Long currentUserId = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                System.out.println("JWT令牌: " + token);
                try {
                    // 验证令牌是否有效
                    boolean isValid = jwtUtil.validateAdminToken(token);
                    System.out.println("令牌是否有效: " + isValid);
                    
                    if (isValid) {
                        // 使用公共方法从令牌中获取userId
                        currentUserId = jwtUtil.getClaimFromToken(token, claims -> {
                            Object userId = claims.get("userId");
                            System.out.println("从令牌中获取的userId类型: " + (userId != null ? userId.getClass().getName() : "null"));
                            System.out.println("从令牌中获取的userId值: " + userId);
                            
                            // 如果userId是Number类型但不是Long类型，手动转换
                            if (userId instanceof Number && !(userId instanceof Long)) {
                                return ((Number) userId).longValue();
                            }
                            
                            return claims.get("userId", Long.class);
                        }, jwtUtil.getAdminSecret());
                        System.out.println("解析后的当前登录用户ID: " + currentUserId);
                    } else {
                        System.err.println("令牌无效");
                    }
                } catch (Exception e) {
                    System.err.println("获取用户ID失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            ProtectedReservationInfoDTO dto;
            
            // 判断数据来源：表单提交还是JSON请求体
            if (data != null) {
                // 表单提交方式，解析JSON数据
                dto = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(data, ProtectedReservationInfoDTO.class);
            } else {
                // 如果没有data参数，返回错误
                return Result.error("无效的请求数据");
            }
            
            if (dto == null) {
                return Result.error("无效的请求数据");
            }
            
            // 设置创建者和更新者ID
            if (currentUserId != null) {
                dto.setCreateBy(currentUserId);
                dto.setUpdateBy(currentUserId);
            }
            
            // 检查是否有视频文件
            if (videoFiles != null && videoFiles.length > 0) {
                System.out.println("收到视频文件数量: " + videoFiles.length);
                for (int i = 0; i < videoFiles.length; i++) {
                    MultipartFile file = videoFiles[i];
                    if (file != null && !file.isEmpty()) {
                        System.out.println("视频文件 " + i + ": " + file.getOriginalFilename() + ", 大小: " + file.getSize() + " 字节");
                    } else {
                        System.out.println("视频文件 " + i + " 为空或无效");
                    }
                }
            } else {
                System.out.println("未收到视频文件");
            }
            
            // 检查富文本内容
            if (dto.getRichContent() != null && dto.getRichContent().contains("<iframe")) {
                System.out.println("富文本内容包含嵌入视频");
            }
            
            // 记录文件数量信息，但不在Controller层处理文件
            // 文件处理将在Service层进行，避免重复处理
            if (dto.getContentType() != null) {
                if (dto.getContentType() == 1) { // 文章类型
                    // 记录文件数量
                    if (photoFiles != null && photoFiles.length > 0) {
                        System.out.println("收到文章内容图片，数量: " + photoFiles.length);
                    }
                    
                    if (carouselFiles != null && carouselFiles.length > 0) {
                        System.out.println("收到轮播图，数量: " + carouselFiles.length);
                    }
                    
                    if (galleryFiles != null && galleryFiles.length > 0) {
                        System.out.println("收到画廊图片，数量: " + galleryFiles.length);
                    }
                    
                    if (audioFiles != null && audioFiles.length > 0) {
                        System.out.println("收到音频文件，数量: " + audioFiles.length);
                    }
                } else if (dto.getContentType() == 2) { // 视频类型
                    // 记录文件数量
                    if (videoFiles != null && videoFiles.length > 0) {
                        System.out.println("收到视频文件，数量: " + videoFiles.length);
                    }
                    
                    if (carouselFiles != null && carouselFiles.length > 0) {
                        System.out.println("收到视频轮播图，数量: " + carouselFiles.length);
                    }
                }
            }
            
            // 委托给Service层处理业务逻辑
            boolean result = protectedReservationInfoService.saveProtectedReservationInfoWithFiles(dto, videoFiles, photoFiles, carouselFiles, galleryFiles, audioFiles);
            System.out.println("保存结果: " + result);
            
            if (dto.getVideoFileIds() != null) {
                System.out.println("关联的视频文件ID: " + dto.getVideoFileIds());
            } else {
                System.out.println("没有关联的视频文件ID");
            }
            
            if (dto.getContentImageIds() != null) {
                System.out.println("关联的文章内容图片ID: " + dto.getContentImageIds());
            } else {
                System.out.println("没有关联的文章内容图片ID");
            }
            
            if (dto.getCarouselFileIds() != null) {
                System.out.println("关联的轮播图ID: " + dto.getCarouselFileIds());
            } else {
                System.out.println("没有关联的轮播图ID");
            }
            
            if (dto.getGalleryFileIds() != null) {
                System.out.println("关联的画廊图片ID: " + dto.getGalleryFileIds());
            } else {
                System.out.println("没有关联的画廊图片ID");
            }
            
            if (dto.getAudioFileIds() != null) {
                System.out.println("关联的音频文件ID: " + dto.getAudioFileIds());
            } else {
                System.out.println("没有关联的音频文件ID");
            }
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("保存失败: " + e.getMessage());
            return Result.error("保存失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新保护区介绍
     */
    @PutMapping
    public Result<Boolean> updateProtectedReservationInfo(
            @RequestParam(value = "videoFiles", required = false) MultipartFile[] videoFiles,
            @RequestParam(value = "photoFiles", required = false) MultipartFile[] photoFiles,
            @RequestParam(value = "carouselFiles", required = false) MultipartFile[] carouselFiles,
            @RequestParam(value = "galleryFiles", required = false) MultipartFile[] galleryFiles,
            @RequestParam(value = "audioFiles", required = false) MultipartFile[] audioFiles,
            @RequestParam(value = "data", required = false) String data,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // 从Authorization头中获取当前登录用户ID
            Long currentUserId = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                System.out.println("JWT令牌: " + token);
                try {
                    // 验证令牌是否有效
                    boolean isValid = jwtUtil.validateAdminToken(token);
                    System.out.println("令牌是否有效: " + isValid);
                    
                    if (isValid) {
                        // 使用公共方法从令牌中获取userId
                        currentUserId = jwtUtil.getClaimFromToken(token, claims -> {
                            Object userId = claims.get("userId");
                            System.out.println("从令牌中获取的userId类型: " + (userId != null ? userId.getClass().getName() : "null"));
                            System.out.println("从令牌中获取的userId值: " + userId);
                            
                            // 如果userId是Number类型但不是Long类型，手动转换
                            if (userId instanceof Number && !(userId instanceof Long)) {
                                return ((Number) userId).longValue();
                            }
                            
                            return claims.get("userId", Long.class);
                        }, jwtUtil.getAdminSecret());
                        System.out.println("解析后的当前登录用户ID: " + currentUserId);
                    } else {
                        System.err.println("令牌无效");
                    }
                } catch (Exception e) {
                    System.err.println("获取用户ID失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            ProtectedReservationInfoDTO dto;
            
            // 判断数据来源：表单提交还是JSON请求体
            if (data != null) {
                // 表单提交方式，解析JSON数据
                dto = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(data, ProtectedReservationInfoDTO.class);
            } else {
                // 如果没有data参数，返回错误
                return Result.error("无效的请求数据");
            }
            
            if (dto == null) {
                return Result.error("无效的请求数据");
            }
            
            // 调试信息
            System.out.println("接收到的DTO ID: " + dto.getId());
            System.out.println("DTO类型: " + dto.getClass().getName());
            
            // 设置更新者ID和创建者ID
            if (currentUserId != null) {
                dto.setUpdateBy(currentUserId);
                // 同时设置创建者ID，确保文件上传时能使用正确的用户ID
                dto.setCreateBy(currentUserId);
                System.out.println("设置DTO的用户ID: updateBy=" + dto.getUpdateBy() + ", createBy=" + dto.getCreateBy());
            }
            
            // 检查是否有视频文件
            if (videoFiles != null && videoFiles.length > 0) {
                System.out.println("收到视频文件数量: " + videoFiles.length);
                for (int i = 0; i < videoFiles.length; i++) {
                    MultipartFile file = videoFiles[i];
                    if (file != null && !file.isEmpty()) {
                        System.out.println("视频文件 " + i + ": " + file.getOriginalFilename() + ", 大小: " + file.getSize() + " 字节");
                    } else {
                        System.out.println("视频文件 " + i + " 为空或无效");
                    }
                }
            } else {
                System.out.println("未收到视频文件");
            }
            
            // 检查富文本内容
            if (dto.getRichContent() != null && dto.getRichContent().contains("<iframe")) {
                System.out.println("富文本内容包含嵌入视频");
            }
            
            // 记录文件数量信息，但不在Controller层处理文件
            // 文件处理将在Service层进行，避免重复处理
            if (dto.getContentType() != null) {
                if (dto.getContentType() == 1) { // 文章类型
                    // 记录文件数量
                    if (photoFiles != null && photoFiles.length > 0) {
                        System.out.println("收到文章内容图片，数量: " + photoFiles.length);
                    }
                    
                    if (carouselFiles != null && carouselFiles.length > 0) {
                        System.out.println("收到轮播图，数量: " + carouselFiles.length);
                    }
                    
                    if (galleryFiles != null && galleryFiles.length > 0) {
                        System.out.println("收到画廊图片，数量: " + galleryFiles.length);
                    }
                    
                    if (audioFiles != null && audioFiles.length > 0) {
                        System.out.println("收到音频文件，数量: " + audioFiles.length);
                    }
                } else if (dto.getContentType() == 2) { // 视频类型
                    // 记录文件数量
                    if (videoFiles != null && videoFiles.length > 0) {
                        System.out.println("收到视频文件，数量: " + videoFiles.length);
                    }
                    
                    if (carouselFiles != null && carouselFiles.length > 0) {
                        System.out.println("收到视频轮播图，数量: " + carouselFiles.length);
                    }
                }
            }
            
            boolean result = protectedReservationInfoService.saveProtectedReservationInfoWithFiles(dto, videoFiles, photoFiles, carouselFiles, galleryFiles, audioFiles);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除保护区介绍（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteProtectedReservationInfo(@PathVariable Long id) {
        System.out.println("=== 开始删除保护区介绍，ID: " + id + " ===");
        
        // 获取保护区介绍信息，用于删除关联的文件
        try {
            // 查询原始数据，获取JSON字符串形式的文件ID
            Map<String, Object> rawData = protectedReservationInfoMapper.selectRawById(id);
            if (rawData != null) {
                System.out.println("获取到原始数据: " + rawData);
                
                // 通用方法：提取ID并删除文件
                BiFunction<Object, String, List<String>> extractAndDeleteFiles = (fileIdsObj, fileType) -> {
                    if (fileIdsObj == null || fileIdsObj.toString().isEmpty()) {
                        System.out.println("没有" + fileType + "文件需要删除");
                        return new java.util.ArrayList<>();
                    }
                    
                    System.out.println("原始" + fileType + "ID JSON: " + fileIdsObj);
                    
                    // 直接从JSON字符串中提取ID
                    String fileIdsStr = fileIdsObj.toString();
                    List<String> fileIds = new java.util.ArrayList<>();
                    
                    // 使用正则表达式提取数字
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d+");
                    java.util.regex.Matcher matcher = pattern.matcher(fileIdsStr);
                    
                    while (matcher.find()) {
                        String idStr = matcher.group();
                        fileIds.add(idStr);
                        System.out.println("提取到" + fileType + "ID: " + idStr);
                    }
                    
                    // 直接删除文件
                    if (!fileIds.isEmpty()) {
                        for (String fileIdStr : fileIds) {
                            try {
                                Long fileId = Long.parseLong(fileIdStr);
                                ResourceFile resourceFile = resourceFileMapper.selectById(fileId);
                                
                                if (resourceFile != null) {
                                    System.out.println("找到" + fileType + "文件记录: ID=" + resourceFile.getId() + ", 文件名=" + resourceFile.getFileName());
                                    
                                    // 删除MinIO中的文件
                                    try {
                                        fileUploadUtil.removeObject(resourceFile.getBucketName(), resourceFile.getFileKey());
                                        System.out.println("已删除MinIO中的" + fileType + "文件: " + resourceFile.getBucketName() + "/" + resourceFile.getFileKey());
                                    } catch (Exception e) {
                                        System.err.println("删除MinIO中的" + fileType + "文件失败: " + e.getMessage());
                                    }
                                    
                                    // 删除数据库中的文件记录
                                    int result = resourceFileMapper.deleteById(fileId);
                                    System.out.println("删除" + fileType + "文件记录结果: " + result);
                                } else {
                                    System.out.println("未找到ID为" + fileId + "的" + fileType + "文件记录");
                                }
                            } catch (Exception e) {
                                System.err.println("处理" + fileType + "文件ID时发生异常: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                    
                    return fileIds;
                };
                
                // 处理视频文件
                extractAndDeleteFiles.apply(rawData.get("video_file_ids"), "视频");
                
                // 处理画廊图片
                extractAndDeleteFiles.apply(rawData.get("gallery_file_ids"), "画廊图片");
                
                // 处理音频文件
                extractAndDeleteFiles.apply(rawData.get("audio_file_ids"), "音频");
                
                // 处理轮播图
                extractAndDeleteFiles.apply(rawData.get("carousel_file_ids"), "轮播图");
                
                // 处理内容图片
                extractAndDeleteFiles.apply(rawData.get("content_image_ids"), "内容图片");
            }
        } catch (Exception e) {
            System.err.println("删除关联文件时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
        
        boolean result = protectedReservationInfoService.deleteProtectedReservationInfo(id);
        return Result.success(result);
    }
    
    /**
     * 根据ID获取保护区介绍（包含原始IDs信息）
     */
    @GetMapping("/{id}")
    public Result<com.scenic.dto.content.ProtectedReservationInfoWithIdsDTO> getProtectedReservationInfoById(@PathVariable Long id) {
        com.scenic.dto.content.ProtectedReservationInfoWithIdsDTO dto = protectedReservationInfoService.getProtectedReservationInfoWithIdsById(id);
        return Result.success(dto);
    }
    
    /**
     * 分页查询保护区介绍列表（增强版，支持发布人、发布时间、内容类型搜索）
     */
    @GetMapping("/page-enhanced")
    public Result<PageResult<java.util.Map<String, Object>>> getProtectedReservationInfoPageEnhanced(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String creatorName,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Byte contentType) {
        
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        
        if (startTime != null && !startTime.isEmpty()) {
            startDateTime = LocalDateTime.parse(startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            endDateTime = LocalDateTime.parse(endTime);
        }
        
        PageResult<java.util.Map<String, Object>> pageResult = protectedReservationInfoService.getProtectedReservationInfoPageEnhanced(
                page, size, title, creatorName, startDateTime, endDateTime, contentType);
        return Result.success(pageResult);
    }
}