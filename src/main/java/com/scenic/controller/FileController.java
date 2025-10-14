package com.scenic.controller;

import com.scenic.common.dto.Result;
import com.scenic.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件控制器
 * 提供文件上传、下载和管理功能
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileUploadUtil fileUploadUtil;

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
}
