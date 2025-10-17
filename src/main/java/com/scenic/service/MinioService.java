package com.scenic.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * 检查存储桶是否存在，不存在则创建
     */
    public void createBucketIfNotExists() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 检查存储桶是否存在，不存在则创建
     */
    public void createBucketIfNotExists(String bucketName) throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 上传文件到默认桶
     *
     * @param file 文件
     * @param objectName 对象名称
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String objectName) throws Exception {
        return uploadFileToBucket(bucketName, file, objectName);
    }
    
    /**
     * 上传文件到指定桶
     *
     * @param bucketName 存储桶名称
     * @param file 文件
     * @param objectName 对象名称
     * @return 文件访问URL
     */
    public String uploadFileToBucket(String bucketName, MultipartFile file, String objectName) throws Exception {
        createBucketIfNotExists(bucketName);
        
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }
        
        return getObjectUrl(bucketName, objectName);
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
        createBucketIfNotExists(bucketName);
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, size, -1)
                        .contentType(contentType)
                        .build()
        );
    }

    /**
     * 删除文件
     *
     * @param objectName 对象名称
     */
    public void deleteFile(String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }
    
    /**
     * 删除指定存储桶中的文件
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     */
    public void removeObject(String bucketName, String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    /**
     * 获取文件访问URL（永久URL）
     *
     * @param objectName 对象名称
     * @return 文件访问URL
     */
    public String getObjectUrl(String objectName) throws Exception {
        return getObjectUrl(bucketName, objectName);
    }
    
    /**
     * 获取指定桶中文件的访问URL（永久URL）
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 文件访问URL
     */
    public String getObjectUrl(String bucketName, String objectName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(7, java.util.concurrent.TimeUnit.DAYS) // 7天有效期
                        .build()
        );
    }

    /**
     * 生成预签名URL（临时访问）
     *
     * @param objectName 对象名称
     * @param expiry 过期时间（秒）
     * @return 预签名URL
     */
    public String getPresignedObjectUrl(String objectName, int expiry) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(expiry, java.util.concurrent.TimeUnit.SECONDS)
                        .build()
        );
    }
    
    /**
     * 生成预签名URL（临时访问），指定存储桶
     *
     * @param bucket 存储桶名称
     * @param objectName 对象名称
     * @param expiry 过期时间（秒）
     * @return 预签名URL
     */
    public String getPresignedObjectUrl(String bucket, String objectName, int expiry) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucket)
                        .object(objectName)
                        .expiry(expiry, java.util.concurrent.TimeUnit.SECONDS)
                        .build()
        );
    }

    /**
     * 生成预签名上传URL（前端直接上传）
     *
     * @param objectName 对象名称
     * @param expiry 过期时间（秒）
     * @return 预签名上传URL
     */
    public String getPresignedUploadUrl(String objectName, int expiry) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(expiry, java.util.concurrent.TimeUnit.SECONDS)
                        .build()
        );
    }
}
