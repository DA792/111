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
            @RequestBody(required = false) ProtectedReservationInfoDTO bodyDto,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // 从Authorization头中获取当前登录用户ID
            Long currentUserId = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    // 验证令牌是否有效
                    if (jwtUtil.validateAdminToken(token)) {
                        // 使用公共方法从令牌中获取userId
                        currentUserId = jwtUtil.getClaimFromToken(token, claims -> claims.get("userId", Long.class), jwtUtil.getAdminSecret());
                        System.out.println("当前登录用户ID: " + currentUserId);
                    } else {
                        System.err.println("令牌无效");
                    }
                } catch (Exception e) {
                    System.err.println("获取用户ID失败: " + e.getMessage());
                }
            }
            
            ProtectedReservationInfoDTO dto;
            
            // 判断数据来源：表单提交还是JSON请求体
            if (data != null) {
                // 表单提交方式，解析JSON数据
                dto = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(data, ProtectedReservationInfoDTO.class);
            } else {
                // JSON请求体方式
                dto = bodyDto;
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
            
            // 根据内容类型处理不同的文件
            if (dto.getContentType() != null) {
                if (dto.getContentType() == 1) { // 文章类型
                    // 处理文章内容图片
                    if (photoFiles != null && photoFiles.length > 0) {
                        System.out.println("开始处理文章内容图片，数量: " + photoFiles.length);
                        List<Long> photoFileIds = protectedReservationInfoService.processPhotoFiles(photoFiles, currentUserId);
                        System.out.println("处理完成，获取到文章内容图片ID: " + photoFileIds);
                        dto.setContentImageIds(photoFileIds);
                    }
                    
                    // 处理轮播图
                    if (carouselFiles != null && carouselFiles.length > 0) {
                        System.out.println("开始处理轮播图，数量: " + carouselFiles.length);
                        List<Long> carouselFileIds = protectedReservationInfoService.processPhotoFiles(carouselFiles, currentUserId);
                        System.out.println("处理完成，获取到轮播图ID: " + carouselFileIds);
                        dto.setCarouselFileIds(carouselFileIds);
                    }
                    
                    // 处理画廊图片
                    if (galleryFiles != null && galleryFiles.length > 0) {
                        System.out.println("开始处理画廊图片，数量: " + galleryFiles.length);
                        List<Long> galleryFileIds = protectedReservationInfoService.processPhotoFiles(galleryFiles, currentUserId);
                        System.out.println("处理完成，获取到画廊图片ID: " + galleryFileIds);
                        dto.setGalleryFileIds(galleryFileIds);
                    }
                    
                    // 处理音频文件
                    if (audioFiles != null && audioFiles.length > 0) {
                        System.out.println("开始处理音频文件，数量: " + audioFiles.length);
                        List<Long> audioFileIds = protectedReservationInfoService.processAudioFiles(audioFiles, currentUserId);
                        System.out.println("处理完成，获取到音频文件ID: " + audioFileIds);
                        dto.setAudioFileIds(audioFileIds);
                    }
                } else if (dto.getContentType() == 2) { // 视频类型
                    // 处理视频文件
                    if (videoFiles != null && videoFiles.length > 0) {
                        System.out.println("开始处理视频文件，数量: " + videoFiles.length);
                    }
                    
                    // 视频类型也可能有轮播图
                    if (carouselFiles != null && carouselFiles.length > 0) {
                        System.out.println("开始处理视频轮播图，数量: " + carouselFiles.length);
                        List<Long> carouselFileIds = protectedReservationInfoService.processPhotoFiles(carouselFiles, currentUserId);
                        System.out.println("处理完成，获取到轮播图ID: " + carouselFileIds);
                        dto.setCarouselFileIds(carouselFileIds);
                    }
                }
            }
            
            // 委托给Service层处理业务逻辑
            boolean result = protectedReservationInfoService.saveProtectedReservationInfoWithFiles(dto, videoFiles);
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
            @RequestBody(required = false) ProtectedReservationInfoDTO bodyDto,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // 从Authorization头中获取当前登录用户ID
            Long currentUserId = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    // 验证令牌是否有效
                    if (jwtUtil.validateAdminToken(token)) {
                        // 使用公共方法从令牌中获取userId
                        currentUserId = jwtUtil.getClaimFromToken(token, claims -> claims.get("userId", Long.class), jwtUtil.getAdminSecret());
                        System.out.println("当前登录用户ID: " + currentUserId);
                    } else {
                        System.err.println("令牌无效");
                    }
                } catch (Exception e) {
                    System.err.println("获取用户ID失败: " + e.getMessage());
                }
            }
            
            ProtectedReservationInfoDTO dto;
            
            // 判断数据来源：表单提交还是JSON请求体
            if (data != null) {
                // 表单提交方式，解析JSON数据
                dto = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(data, ProtectedReservationInfoDTO.class);
            } else {
                // JSON请求体方式
                dto = bodyDto;
            }
            
            if (dto == null) {
                return Result.error("无效的请求数据");
            }
            
            // 设置更新者ID
            if (currentUserId != null) {
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
            
            // 根据内容类型处理不同的文件
            if (dto.getContentType() != null) {
                if (dto.getContentType() == 1) { // 文章类型
                    // 处理文章内容图片
                    if (photoFiles != null && photoFiles.length > 0) {
                        System.out.println("开始处理文章内容图片，数量: " + photoFiles.length);
                        List<Long> photoFileIds = protectedReservationInfoService.processPhotoFiles(photoFiles, currentUserId);
                        System.out.println("处理完成，获取到文章内容图片ID: " + photoFileIds);
                        dto.setContentImageIds(photoFileIds);
                    }
                    
                    // 处理轮播图
                    if (carouselFiles != null && carouselFiles.length > 0) {
                        System.out.println("开始处理轮播图，数量: " + carouselFiles.length);
                        List<Long> carouselFileIds = protectedReservationInfoService.processPhotoFiles(carouselFiles, currentUserId);
                        System.out.println("处理完成，获取到轮播图ID: " + carouselFileIds);
                        dto.setCarouselFileIds(carouselFileIds);
                    }
                    
                    // 处理画廊图片
                    if (galleryFiles != null && galleryFiles.length > 0) {
                        System.out.println("开始处理画廊图片，数量: " + galleryFiles.length);
                        List<Long> galleryFileIds = protectedReservationInfoService.processPhotoFiles(galleryFiles, currentUserId);
                        System.out.println("处理完成，获取到画廊图片ID: " + galleryFileIds);
                        dto.setGalleryFileIds(galleryFileIds);
                    }
                    
                    // 处理音频文件
                    if (audioFiles != null && audioFiles.length > 0) {
                        System.out.println("开始处理音频文件，数量: " + audioFiles.length);
                        List<Long> audioFileIds = protectedReservationInfoService.processAudioFiles(audioFiles, currentUserId);
                        System.out.println("处理完成，获取到音频文件ID: " + audioFileIds);
                        dto.setAudioFileIds(audioFileIds);
                    }
                } else if (dto.getContentType() == 2) { // 视频类型
                    // 处理视频文件已在updateProtectedReservationInfo方法中实现
                    
                    // 视频类型也可能有轮播图
                    if (carouselFiles != null && carouselFiles.length > 0) {
                        System.out.println("开始处理视频轮播图，数量: " + carouselFiles.length);
                        List<Long> carouselFileIds = protectedReservationInfoService.processPhotoFiles(carouselFiles, currentUserId);
                        System.out.println("处理完成，获取到轮播图ID: " + carouselFileIds);
                        dto.setCarouselFileIds(carouselFileIds);
                    }
                }
            }
            
            boolean result = protectedReservationInfoService.updateProtectedReservationInfo(dto, videoFiles);
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
        boolean result = protectedReservationInfoService.deleteProtectedReservationInfo(id);
        return Result.success(result);
    }
    
    /**
     * 根据ID获取保护区介绍
     */
    @GetMapping("/{id}")
    public Result<ProtectedReservationInfoDTO> getProtectedReservationInfoById(@PathVariable Long id) {
        ProtectedReservationInfoDTO dto = protectedReservationInfoService.getProtectedReservationInfoById(id);
        return Result.success(dto);
    }
    
    /**
     * 分页查询保护区介绍列表
     */
    @GetMapping("/page")
    public Result<PageResult<ProtectedReservationInfoDTO>> getProtectedReservationInfoPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Byte contentType,
            @RequestParam(required = false) Byte contentCategory) {
        
        PageResult<ProtectedReservationInfoDTO> pageResult = protectedReservationInfoService.getProtectedReservationInfoPage(
                page, size, title, contentType, contentCategory);
        return Result.success(pageResult);
    }
    
    /**
     * 获取所有未删除的保护区介绍列表
     */
    @GetMapping("/list")
    public Result<List<ProtectedReservationInfoDTO>> getAllProtectedReservationInfo() {
        List<ProtectedReservationInfoDTO> list = protectedReservationInfoService.getAllProtectedReservationInfo();
        return Result.success(list);
    }
    
    /**
     * 根据内容类型获取保护区介绍列表
     */
    @GetMapping("/list-by-type")
    public Result<List<ProtectedReservationInfoDTO>> getProtectedReservationInfoByContentType(
            @RequestParam Byte contentType) {
        List<ProtectedReservationInfoDTO> list = protectedReservationInfoService.getProtectedReservationInfoByContentType(contentType);
        return Result.success(list);
    }
    
    /**
     * 根据内容分类获取保护区介绍列表
     */
    @GetMapping("/list-by-category")
    public Result<List<ProtectedReservationInfoDTO>> getProtectedReservationInfoByContentCategory(
            @RequestParam Byte contentCategory) {
        List<ProtectedReservationInfoDTO> list = protectedReservationInfoService.getProtectedReservationInfoByContentCategory(contentCategory);
        return Result.success(list);
    }
    
    /**
     * 分页查询保护区介绍列表（增强版，支持发布人、发布时间、内容类型搜索）
     */
    @GetMapping("/page-enhanced")
    public Result<PageResult<ProtectedReservationInfoEnhancedDTO>> getProtectedReservationInfoPageEnhanced(
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
        
        PageResult<ProtectedReservationInfoEnhancedDTO> pageResult = protectedReservationInfoService.getProtectedReservationInfoPageEnhanced(
                page, size, title, creatorName, startDateTime, endDateTime, contentType);
        return Result.success(pageResult);
    }
}
