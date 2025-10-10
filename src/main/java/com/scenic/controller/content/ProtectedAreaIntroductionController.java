package com.scenic.controller.content;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.scenic.common.dto.Result;
import com.scenic.dto.content.ProtectedAreaIntroductionDTO;
import com.scenic.service.content.ProtectedAreaIntroductionService;
import com.scenic.utils.FileUploadUtil;

/**
 * 保护区介绍控制器
 * 提供小程序端和管理后台端的API接口
 */
@RestController
@RequestMapping("/api")
public class ProtectedAreaIntroductionController {
    
    // 小程序端API接口前缀
    private static final String MINIAPP_PREFIX = "/uniapp";
    
    // 管理后台端API接口前缀
    private static final String ADMIN_PREFIX = "/manage";
    
    @Autowired
    private ProtectedAreaIntroductionService protectedAreaIntroductionService;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;
    
    /**
     * 小程序端 - 获取所有保护区介绍（按排序字段排序）
     * @return 保护区介绍列表
     */
    @GetMapping(MINIAPP_PREFIX + "/protected-area-introduction/list")
    public Result<List<ProtectedAreaIntroductionDTO>> getAllProtectedAreaIntroductionsForMiniapp() {
        return protectedAreaIntroductionService.getAllProtectedAreaIntroductions();
    }
    
    /**
     * 小程序端 - 根据语言获取保护区介绍（按排序字段排序）
     * @param language 语言
     * @return 保护区介绍列表
     */
    @GetMapping(MINIAPP_PREFIX + "/protected-area-introduction/language/{language}")
    public Result<List<ProtectedAreaIntroductionDTO>> getProtectedAreaIntroductionsByLanguageForMiniapp(@PathVariable String language) {
        return protectedAreaIntroductionService.getProtectedAreaIntroductionsByLanguage(language);
    }
    
    /**
     * 小程序端 - 根据ID获取保护区介绍详情
     * @param id 保护区介绍ID
     * @return 保护区介绍详情
     */
    @GetMapping(MINIAPP_PREFIX + "/protected-area-introduction/detail/{id}")
    public Result<ProtectedAreaIntroductionDTO> getProtectedAreaIntroductionById(@PathVariable Long id) {
        return protectedAreaIntroductionService.getProtectedAreaIntroductionById(id);
    }
    
    /**
     * 管理后台端 - 新增保护区介绍
     * @param imageFile 图片文件
     * @param audioFile 音频文件
     * @param videoFile 视频文件
     * @param title 标题
     * @param content 内容
     * @param language 语言
     * @param sortOrder 排序
     * @return 操作结果
     */
    @PostMapping(ADMIN_PREFIX + "/protected-area-introduction/add")
    public Result<String> addProtectedAreaIntroduction(
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "audioFile", required = false) MultipartFile audioFile,
            @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("language") String language,
            @RequestParam("sortOrder") Integer sortOrder) {
        
        try {
            // 处理文件上传
            String imageUrl = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                imageUrl = fileUploadUtil.uploadFile(imageFile);
            }
            
            String audioUrl = null;
            if (audioFile != null && !audioFile.isEmpty()) {
                audioUrl = fileUploadUtil.uploadFile(audioFile);
            }
            
            String videoUrl = null;
            if (videoFile != null && !videoFile.isEmpty()) {
                videoUrl = fileUploadUtil.uploadFile(videoFile);
            }
            
            ProtectedAreaIntroductionDTO introductionDTO = new ProtectedAreaIntroductionDTO();
            introductionDTO.setTitle(title);
            introductionDTO.setContent(content);
            introductionDTO.setLanguage(language);
            introductionDTO.setImageUrl(imageUrl);
            introductionDTO.setAudioUrl(audioUrl);
            introductionDTO.setVideoUrl(videoUrl);
            introductionDTO.setSortOrder(sortOrder);
            
            return protectedAreaIntroductionService.addProtectedAreaIntroduction(introductionDTO);
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            return Result.error("新增失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理后台端 - 获取所有保护区介绍
     * @return 保护区介绍列表
     */
    @GetMapping(ADMIN_PREFIX + "/protected-area-introduction/list")
    public Result<List<ProtectedAreaIntroductionDTO>> getAllProtectedAreaIntroductionsForAdmin() {
        return protectedAreaIntroductionService.getAllProtectedAreaIntroductions();
    }
    
    /**
     * 管理后台端 - 根据语言获取保护区介绍
     * @param language 语言
     * @return 保护区介绍列表
     */
    @GetMapping(ADMIN_PREFIX + "/protected-area-introduction/language/{language}")
    public Result<List<ProtectedAreaIntroductionDTO>> getProtectedAreaIntroductionsByLanguageForAdmin(@PathVariable String language) {
        return protectedAreaIntroductionService.getProtectedAreaIntroductionsByLanguage(language);
    }
    
    /**
     * 管理后台端 - 更新保护区介绍
     * @param id 保护区介绍ID
     * @param introductionDTO 保护区介绍信息
     * @return 操作结果
     */
    @PutMapping(ADMIN_PREFIX + "/protected-area-introduction/update/{id}")
    public Result<String> updateProtectedAreaIntroduction(@PathVariable Long id, @RequestBody ProtectedAreaIntroductionDTO introductionDTO) {
        return protectedAreaIntroductionService.updateProtectedAreaIntroduction(id, introductionDTO);
    }
    
    /**
     * 管理后台端 - 删除保护区介绍
     * @param id 保护区介绍ID
     * @return 操作结果
     */
    @DeleteMapping(ADMIN_PREFIX + "/protected-area-introduction/delete/{id}")
    public Result<String> deleteProtectedAreaIntroduction(@PathVariable Long id) {
        return protectedAreaIntroductionService.deleteProtectedAreaIntroduction(id);
    }
}
