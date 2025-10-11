package com.scenic.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传工具类
 */
@Component
public class FileUploadUtil {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        // 确保上传目录存在
        File directory = new File(uploadPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件路径
     * @throws IOException IO异常
     */
    public String uploadFile(MultipartFile file) throws IOException {
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

        // 构建文件保存路径
        Path filePath = Paths.get(uploadPath, uniqueFilename);

        // 保存文件
        Files.write(filePath, file.getBytes());

        // 返回相对路径
        return uniqueFilename;
    }

    /**
     * 删除文件
     *
     * @param filename 文件名
     * @return 是否删除成功
     */
    public boolean deleteFile(String filename) {
        try {
            Path filePath = Paths.get(uploadPath, filename);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 获取文件完整路径
     *
     * @param filename 文件名
     * @return 文件完整路径
     */
    public String getFilePath(String filename) {
        return Paths.get(uploadPath, filename).toString();
    }
}
