package com.scenic.utils;

import com.scenic.service.MinioService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 文件上传工具类（仅支持MinIO）
 */
@Component
public class FileUploadUtil {

    @Autowired
    private MinioService minioService;

    /**
     * 上传文件到默认桶并返回永久访问URL
     *
     * @param file 文件
     * @return 文件访问URL
     * @throws Exception 异常
     */
    public String uploadFile(MultipartFile file) throws Exception {
        return uploadFileToBucket(null, file);
    }
    
    /**
     * 上传文件到指定桶并返回永久访问URL
     *
     * @param bucketName 存储桶名称，如果为null则使用默认桶
     * @param file 文件
     * @return 文件访问URL
     * @throws Exception 异常
     */
    public String uploadFileToBucket(String bucketName, MultipartFile file) throws Exception {
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
        if (bucketName != null && !bucketName.isEmpty()) {
            return minioService.uploadFileToBucket(bucketName, file, uniqueFilename);
        } else {
            return minioService.uploadFile(file, uniqueFilename);
        }
    }

    /**
     * 上传对象到指定存储桶
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param inputStream 输入流
     * @param size 文件大小
     * @param contentType 内容类型
     */
    public void putObject(String bucketName, String objectName, InputStream inputStream, long size, String contentType) throws Exception {
        minioService.putObject(bucketName, objectName, inputStream, size, contentType);
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
     * 生成临时访问URL，指定存储桶
     *
     * @param bucket 存储桶名称
     * @param objectName 对象名称
     * @param expiry 过期时间（秒）
     * @return 临时访问URL
     * @throws Exception 异常
     */
    public String getPresignedUrl(String bucket, String objectName, int expiry) throws Exception {
        return minioService.getPresignedObjectUrl(bucket, objectName, expiry);
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
    
    /**
     * 删除指定存储桶中的文件
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 是否删除成功
     */
    public boolean removeObject(String bucketName, String objectName) {
        try {
            // 使用MinIO删除
            minioService.removeObject(bucketName, objectName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
