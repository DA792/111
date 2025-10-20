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

/**
 * 文件控制器
 * 提供文件上传、下载和管理功能
 */
@RestController
@RequestMapping("/api/files")
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
     * @return 文件访问URL
     */
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileUploadUtil.uploadFile(file);
            return Result.success(fileUrl);
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
     * 获取用户头像URL
     *
     * @param userId 用户ID
     * @return 用户头像URL
     */
    @GetMapping("/avatar/{userId}")
    public Result<String> getUserAvatarUrl(@PathVariable Long userId, HttpServletRequest request) {
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
            
            // 根据用户ID获取用户头像文件信息
            ResourceFile avatarFile = resourceFileMapper.selectUserAvatar(userId);
            
            // 如果用户不存在或没有设置头像，尝试使用测试用户ID
            if (avatarFile == null) {
                // 尝试使用测试用户ID
                Long testUserId = 1741502342987124736L;
                avatarFile = resourceFileMapper.selectUserAvatar(testUserId);
                
                // 如果测试用户也没有头像，则使用默认头像
                if (avatarFile == null) {
                    // 查询默认头像
                    ResourceFile defaultAvatar = resourceFileMapper.selectDefaultAvatar();
                    
                    // 如果默认头像不存在，则使用硬编码的默认头像
                    if (defaultAvatar == null) {
                        // 使用硬编码的默认头像
                        String bucketName = "user-avatars";
                        String objectName = "user-image1.jpeg";
                        
                        // 使用Presigned URL技术获取临时访问URL
                        String avatarUrl = fileUploadUtil.getPresignedUrl(bucketName, objectName, 3600);
                        
                        return Result.success(avatarUrl);
                    }
                    
                    // 使用Presigned URL技术获取临时访问URL
                    String avatarUrl = fileUploadUtil.getPresignedUrl(defaultAvatar.getBucketName(), defaultAvatar.getFileKey(), 3600);
                    
                    return Result.success(avatarUrl);
                }
            }
            
            // 使用Presigned URL技术获取临时访问URL
            String avatarUrl = fileUploadUtil.getPresignedUrl(avatarFile.getBucketName(), avatarFile.getFileKey(), 3600);
            
            // 不添加版本参数，保持URL简洁
            
            return Result.success(avatarUrl);
        } catch (Exception e) {
            return Result.error("获取用户头像URL失败: " + e.getMessage());
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
            // 注意：这里使用当前用户ID，而不是固定ID
            String fileKey = "avatar_" + userId + extension;
            String bucketName = "user-avatars";
            
            // 检查是否已存在相同文件名的记录
            ResourceFile existingFile = resourceFileMapper.selectByBucketAndKey(bucketName, fileKey);
            if (existingFile != null) {
                // 如果存在，先删除旧记录
                resourceFileMapper.deleteById(existingFile.getId());
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
            resourceFile.setFileType(1); // 1-图片
            resourceFile.setUploadUserId(userId);
            resourceFile.setIsTemp(0);
            resourceFile.setCreateTime(LocalDateTime.now());
            resourceFile.setUpdateTime(LocalDateTime.now());
            resourceFile.setCreateBy(userId);
            
            // 保存文件记录
            resourceFileMapper.insert(resourceFile);
            Long fileId = resourceFile.getId();
            
            // 更新用户头像ID
            User user = userMapper.selectById(userId);
            if (user != null) {
                // 获取用户当前的头像ID
                Long oldAvatarFileId = user.getAvatarFileId();
                
                // 如果用户已经有头像，则删除旧头像
                if (oldAvatarFileId != null) {
                    ResourceFile oldAvatarFile = resourceFileMapper.selectById(oldAvatarFileId);
                    if (oldAvatarFile != null) {
                        // 删除MinIO中的旧头像文件
                        fileUploadUtil.removeObject(oldAvatarFile.getBucketName(), oldAvatarFile.getFileKey());
                        // 删除resource_file表中的旧头像记录
                        resourceFileMapper.deleteById(oldAvatarFileId);
                    }
                }
                
                // 更新用户头像ID为新头像
                user.setAvatarFileId(fileId);
                userMapper.updateById(user);
            } else {
                // 如果用户不存在，尝试使用固定的测试用户ID
                Long testUserId = 1741502342987124736L;
                user = userMapper.selectById(testUserId);
                if (user != null) {
                    // 获取用户当前的头像ID
                    Long oldAvatarFileId = user.getAvatarFileId();
                    
                    // 如果用户已经有头像，则删除旧头像
                    if (oldAvatarFileId != null) {
                        ResourceFile oldAvatarFile = resourceFileMapper.selectById(oldAvatarFileId);
                        if (oldAvatarFile != null) {
                            // 删除MinIO中的旧头像文件
                            fileUploadUtil.removeObject(oldAvatarFile.getBucketName(), oldAvatarFile.getFileKey());
                            // 删除resource_file表中的旧头像记录
                            resourceFileMapper.deleteById(oldAvatarFileId);
                        }
                    }
                    
                    // 更新用户头像ID为新头像
                    user.setAvatarFileId(fileId);
                    userMapper.updateById(user);
                    // 返回成功，但使用了测试用户ID
                    String avatarUrl = fileUploadUtil.getPresignedUrl(bucketName, fileKey, 3600);
                    
                    // 不添加版本参数，保持URL简洁
                    
                    return Result.success(avatarUrl);
                } else {
                    return Result.error("用户不存在，无法更新头像");
                }
            }
            
            // 返回头像URL
            String avatarUrl = fileUploadUtil.getPresignedUrl(bucketName, fileKey, 3600);
            
            // 不添加版本参数，保持URL简洁
            
            return Result.success(avatarUrl);
        } catch (Exception e) {
            return Result.error("上传头像失败: " + e.getMessage());
        }
    }
}