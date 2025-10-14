package com.scenic.utils;

import com.scenic.service.MinioService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传工具类（仅支持MinIO）
 */
@Component
public class FileUploadUtil {

    @Autowired
    private MinioService minioService;

    /**
     * 上传文件并返回永久访问URL
     *
     * @param file 文件
     * @return 文件访问URL
     * @throws Exception 异常
     */
    public String uploadFile(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IOException("文件不能为空");
        }

        // 获取文件原始名称
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IOException("文件名不能为空");
        }

        // 生成唯一文件名
        String extension = FilenameUtils.getExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + "." + extension;

        // 使用MinIO存储并返回永久访问URL
        return minioService.uploadFile(file, uniqueFilename);
    }

    /**
     * 生成临时访问URL
     *
     * @param objectName 对象名称
     * @param expiry 过期时间（秒）
     * @return 临时访问URL
     * @throws Exception 异常
     */
    public String getPresignedUrl(String objectName, int expiry) throws Exception {
        return minioService.getPresignedObjectUrl(objectName, expiry);
    }

    /**
     * 生成临时上传URL
     *
     * @param objectName 对象名称
     * @param expiry 过期时间（秒）
     * @return 临时上传URL
     * @throws Exception 异常
     */
    public String getPresignedUploadUrl(String objectName, int expiry) throws Exception {
        return minioService.getPresignedUploadUrl(objectName, expiry);
    }

    /**
     * 删除文件
     *
     * @param filename 文件名
     * @return 是否删除成功
     */
    public boolean deleteFile(String filename) {
        try {
            // 使用MinIO删除
            minioService.deleteFile(filename);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
