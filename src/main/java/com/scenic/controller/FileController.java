package com.scenic.controller;

import com.scenic.common.dto.Result;
import com.scenic.entity.ResourceFile;
import com.scenic.entity.user.User;
import com.scenic.mapper.ResourceFileMapper;
import com.scenic.mapper.user.UserMapper;
import com.scenic.utils.FileUploadUtil;
import com.scenic.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件控制器
 * 提供文件上传、下载和管理功能
 */
@RestController
@RequestMapping("/api")
public class FileController {

    @Autowired
    private FileUploadUtil fileUploadUtil;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ResourceFileMapper resourceFileMapper;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 上传文件（后端上传）
     *
     * @param file 文件
     * @param type 文件类型（可选，默认为0-其他）
     * @return 包含文件ID和URL的对象
     */
    @PostMapping("/upload")
    public Result<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file, 
                                                 @RequestParam(value = "type", defaultValue = "0") String typeStr,
                                                 HttpServletRequest request) {
        // 将字符串类型转换为整数类型
        Integer type = 0; // 默认为0-其他
        if (typeStr != null && !typeStr.isEmpty()) {
            try {
                type = Integer.parseInt(typeStr);
            } catch (NumberFormatException e) {
                // 如果无法解析为整数，则根据字符串值设置相应的类型
                switch (typeStr.toLowerCase()) {
                    case "image":
                        type = 1; // 1-图片
                        break;
                    case "video":
                        type = 2; // 2-视频
                        break;
                    case "audio":
                        type = 3; // 3-音频
                        break;
                    default:
                        type = 0; // 0-其他
                }
            }
        }
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            // 获取当前登录用户ID
            Long currentUserId = null;
            String authHeader = request.getHeader("Authorization");
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
            
            // 如果无法获取用户ID，使用默认测试用户ID
            if (currentUserId == null) {
                currentUserId = 1741502342987124736L; // 使用测试用户ID
                System.out.println("使用默认测试用户ID: " + currentUserId);
            }
            
            // 获取文件原始名称
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return Result.error("文件名不能为空");
            }
            
            // 获取文件扩展名
            String extension = "";
            if (originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 生成唯一文件名
            String fileKey = UUID.randomUUID().toString() + extension;
            
            // 根据文件类型选择不同的存储桶
            String bucketName = "files"; // 默认存储桶
            switch (type) {
                case 1: // 图片文件
                    bucketName = "content-management-photo";
                    break;
                case 2: // 视频文件
                    bucketName = "content-management";
                    break;
                case 3: // 音频文件
                    bucketName = "content-management-audio";
                    break;
                default: // 其他文件
                    bucketName = "files";
                    break;
            }
            
            // 兼容字符串类型的type参数
            if ("image".equalsIgnoreCase(typeStr)) {
                bucketName = "content-management-photo";
            } else if ("video".equalsIgnoreCase(typeStr)) {
                bucketName = "content-management";
            } else if ("audio".equalsIgnoreCase(typeStr)) {
                bucketName = "content-management-audio";
            }
            
            // 上传到MinIO
            try (InputStream inputStream = file.getInputStream()) {
                fileUploadUtil.putObject(bucketName, fileKey, inputStream, file.getSize(), file.getContentType());
            }
            
            // 保存文件信息到数据库
            ResourceFile resourceFile = new ResourceFile();
            resourceFile.setFileName(originalFilename);
            resourceFile.setFileKey(fileKey);
            resourceFile.setBucketName(bucketName);
            resourceFile.setFileSize(file.getSize());
            resourceFile.setMimeType(file.getContentType());
            resourceFile.setFileType(type); // 文件类型：0-其他，1-图片，2-视频，3-音频
            resourceFile.setUploadUserId(currentUserId); // 设置上传用户ID
            resourceFile.setIsTemp(0);
            resourceFile.setCreateTime(LocalDateTime.now());
            resourceFile.setUpdateTime(LocalDateTime.now());
            resourceFile.setCreateBy(currentUserId); // 设置创建者ID
            resourceFile.setUpdateBy(currentUserId); // 设置更新者ID
            
            // 保存文件记录
            resourceFileMapper.insert(resourceFile);
            Long fileId = resourceFile.getId();
            
            // 获取文件URL
            String fileUrl = fileUploadUtil.getPresignedUrl(bucketName, fileKey, 3600);
            
            // 返回文件ID和URL
            Map<String, Object> result = new HashMap<>();
            result.put("fileId", fileId);
            result.put("url", fileUrl);
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取临时访问URL
     *
     * @param objectName 对象名称
     * @param expiry 过期时间（秒，默认3600秒）
     * @return 临时访问URL
     */
    @GetMapping("/presigned-url")
    public Result<String> getPresignedUrl(@RequestParam String objectName,
                                          @RequestParam(defaultValue = "3600") int expiry) {
        try {
            String presignedUrl = fileUploadUtil.getPresignedUrl(objectName, expiry);
            return Result.success(presignedUrl);
        } catch (Exception e) {
            return Result.error("获取临时访问URL失败: " + e.getMessage());
        }
    }

    /**
     * 获取临时上传URL
     *
     * @param objectName 对象名称
     * @param expiry 过期时间（秒，默认3600秒）
     * @return 临时上传URL
     */
    @PostMapping("/presigned-upload-url")
    public Result<String> getPresignedUploadUrl(@RequestParam String objectName,
                                                @RequestParam(defaultValue = "3600") int expiry) {
        try {
            String presignedUrl = fileUploadUtil.getPresignedUploadUrl(objectName, expiry);
            return Result.success(presignedUrl);
        } catch (Exception e) {
            return Result.error("获取临时上传URL失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param filename 文件名
     * @return 删除结果
     */
    @DeleteMapping("/delete/{filename}")
    public Result<Boolean> deleteFile(@PathVariable String filename) {
        boolean result = fileUploadUtil.deleteFile(filename);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("文件删除失败");
        }
    }
    
    /**
     * 根据文件ID获取文件的临时URL
     *
     * @param fileId 文件ID
     * @return 文件的临时URL
     */
    @GetMapping("/file/{fileId}")
    public Result<String> getFileUrlById(@PathVariable String fileId) {
        System.out.println("获取文件URL，文件ID: " + fileId);
        try {
            if (fileId == null || fileId.isEmpty()) {
                System.err.println("文件ID为null或空");
                return Result.error("文件ID不能为空");
            }
            
            // 将字符串ID转换为Long类型
            Long fileIdLong;
            try {
                fileIdLong = Long.parseLong(fileId);
            } catch (NumberFormatException e) {
                System.err.println("文件ID格式不正确: " + fileId);
                return Result.error("文件ID格式不正确");
            }
            
            // 根据文件ID查询文件信息
            ResourceFile resourceFile = resourceFileMapper.selectById(fileIdLong);
            System.out.println("查询文件结果: " + (resourceFile != null ? "找到文件" : "文件不存在"));
            
            if (resourceFile == null) {
                System.err.println("文件不存在，ID: " + fileId);
                return Result.error("文件不存在");
            }
            
            System.out.println("文件信息: bucketName=" + resourceFile.getBucketName() + ", fileKey=" + resourceFile.getFileKey());
            
            // 获取文件的临时URL，设置更长的有效期（7天）
            String fileUrl = fileUploadUtil.getPresignedUrl(resourceFile.getBucketName(), resourceFile.getFileKey(), 604800);
            System.out.println("生成的文件URL: " + fileUrl);
            
            return Result.success(fileUrl);
        } catch (Exception e) {
            System.err.println("获取文件URL失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取文件URL失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取图片URL（短期有效）
     *
     * @param fileId 文件ID
     * @return 短期有效的预签名URL
     */
    @GetMapping("/get-image-url")
    public Result<String> getImageUrl(@RequestParam Long fileId) {
        try {
            // 根据ID查询文件信息
            ResourceFile resourceFile = resourceFileMapper.selectById(fileId);
            
            if (resourceFile == null) {
                return Result.error("文件不存在");
            }
            
            // 生成短期有效的预签名URL（5分钟）
            String presignedUrl = fileUploadUtil.getPresignedUrl(
                resourceFile.getBucketName(),
                resourceFile.getFileKey(),
                300 // 5分钟有效期
            );
            
            return Result.success(presignedUrl);
        } catch (Exception e) {
            System.err.println("获取图片URL失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取图片URL失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户头像URL
     *
     * @param userId 用户ID
     * @return 用户头像URL
     */
    @GetMapping("/avatar/{userId}")
    public Result<String> getUserAvatarUrl(@PathVariable Long userId, HttpServletRequest request) {
        try {
            // 根据用户ID获取用户头像文件信息
            ResourceFile avatarFile = resourceFileMapper.selectUserAvatar(userId);
            
            // 如果用户头像不存在，返回默认头像
            if (avatarFile == null) {
                // 使用硬编码的默认头像
                String bucketName = "user-avatars";
                String objectName = "user-image1.jpeg";
                
                // 使用Presigned URL技术获取临时访问URL
                String avatarUrl = fileUploadUtil.getPresignedUrl(bucketName, objectName, 3600);
                
                return Result.success(avatarUrl);
            }
            
            // 使用Presigned URL技术获取临时访问URL
            String avatarUrl = fileUploadUtil.getPresignedUrl(avatarFile.getBucketName(), avatarFile.getFileKey(), 3600);
            
            return Result.success(avatarUrl);
        } catch (Exception e) {
            // 发生异常时返回默认头像
            try {
                String defaultAvatarUrl = fileUploadUtil.getPresignedUrl("user-avatars", "default-avatar.png", 3600);
                return Result.success(defaultAvatarUrl);
            } catch (Exception ex) {
                return Result.error("获取用户头像URL失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 上传用户头像
     *
     * @param userId 用户ID
     * @param file 头像文件
     * @return 头像URL
     */
    @PostMapping("/avatar/{userId}/upload")
    public Result<String> uploadUserAvatar(@PathVariable Long userId, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            // 验证用户身份
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.error("未授权：缺少有效的认证令牌");
            }
            
            String token = authHeader.substring(7);
            
            // 验证token有效性
            // 先尝试验证管理员令牌
            boolean isValidToken = jwtUtil.validateAdminToken(token);
            
            // 如果管理员令牌验证失败，再尝试验证小程序用户令牌
            if (!isValidToken) {
                isValidToken = jwtUtil.validateMiniappToken(token);
                if (!isValidToken) {
                    return Result.error("未授权：认证令牌无效或已过期");
                }
            }
            
            // 验证文件
            if (file.isEmpty()) {
                return Result.error("头像文件不能为空");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只支持上传图片文件");
            }
            
            // 验证文件大小
            if (file.getSize() > 5 * 1024 * 1024) { // 5MB
                return Result.error("头像文件大小不能超过5MB");
            }
            
            // 获取文件扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 使用固定文件名格式，不包含时间戳，确保URL不变
            String fileKey = "avatar_" + userId + extension;
            String bucketName = "user-avatars";
            
            // 上传到MinIO
            try (InputStream inputStream = file.getInputStream()) {
                fileUploadUtil.putObject(bucketName, fileKey, inputStream, file.getSize(), file.getContentType());
            }
            
            // 保存文件信息到数据库
            ResourceFile resourceFile = new ResourceFile();
            resourceFile.setFileName(originalFilename);
            resourceFile.setFileKey(fileKey);
            resourceFile.setBucketName(bucketName);
            resourceFile.setFileSize(file.getSize());
            resourceFile.setMimeType(file.getContentType());
            resourceFile.setFileType(1); // 1-图片
            resourceFile.setUploadUserId(userId);
            resourceFile.setIsTemp(0);
            resourceFile.setCreateTime(LocalDateTime.now());
            resourceFile.setUpdateTime(LocalDateTime.now());
            resourceFile.setCreateBy(userId);
            
            // 保存文件记录
            resourceFileMapper.insert(resourceFile);
            Long fileId = resourceFile.getId();
            
            // 更新用户头像ID（删除原来的头像信息）
            User user = userMapper.selectById(userId);
            if (user != null) {
                // 获取用户当前的头像ID
                Long oldAvatarFileId = user.getAvatarFileId();
                
                // 如果用户已经有头像，则删除旧头像记录和文件
                if (oldAvatarFileId != null) {
                    ResourceFile oldAvatarFile = resourceFileMapper.selectById(oldAvatarFileId);
                    if (oldAvatarFile != null) {
                        // 删除MinIO中的旧头像文件
                        fileUploadUtil.removeObject(oldAvatarFile.getBucketName(), oldAvatarFile.getFileKey());
                    }
                    // 删除resource_file表中的旧头像记录
                    resourceFileMapper.deleteById(oldAvatarFileId);
                }
                
                // 更新用户头像ID为新头像
                user.setAvatarFileId(fileId);
                userMapper.updateById(user);
            }
            
            // 返回头像URL
            String avatarUrl = fileUploadUtil.getPresignedUrl(bucketName, fileKey, 3600);
            
            return Result.success(avatarUrl);
        } catch (Exception e) {
            return Result.error("上传头像失败: " + e.getMessage());
        }
    }
}
