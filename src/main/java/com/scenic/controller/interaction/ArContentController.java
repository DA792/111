package com.scenic.controller.interaction;

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
import com.scenic.dto.interaction.ArContentDTO;
import com.scenic.service.interaction.ArContentService;
import com.scenic.utils.FileUploadUtil;

/**
 * AR内容控制器
 * 提供小程序端和管理后台端的API接口
 */
@RestController
@RequestMapping("/api")
public class ArContentController {
    
    // 小程序端API接口前缀
    private static final String MINIAPP_PREFIX = "/uniapp";
    
    // 管理后台端API接口前缀
    private static final String ADMIN_PREFIX = "/manage";
    
    @Autowired
    private ArContentService arContentService;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;
    
    /**
     * 小程序端 - 获取所有AR内容
     * @return AR内容列表
     */
    @GetMapping(MINIAPP_PREFIX + "/ar-content/list")
    public Result<List<ArContentDTO>> getAllArContentsForMiniapp() {
        return arContentService.getAllArContents();
    }
    
    /**
     * 小程序端 - 根据目标ID和类型获取AR内容
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return AR内容列表
     */
    @GetMapping(MINIAPP_PREFIX + "/ar-content/target")
    public Result<List<ArContentDTO>> getArContentsByTarget(
            @RequestParam("targetId") String targetId,
            @RequestParam("targetType") String targetType) {
        return arContentService.getArContentsByTarget(targetId, targetType);
    }
    
    /**
     * 小程序端 - 根据内容类型获取AR内容
     * @param contentType 内容类型
     * @return AR内容列表
     */
    @GetMapping(MINIAPP_PREFIX + "/ar-content/content-type/{contentType}")
    public Result<List<ArContentDTO>> getArContentsByContentType(@PathVariable String contentType) {
        return arContentService.getArContentsByContentType(contentType);
    }
    
    /**
     * 管理后台端 - 上传AR内容
     * @param file AR内容文件
     * @param title 标题
     * @param description 描述
     * @param contentType 内容类型
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param latitude 纬度
     * @param longitude 经度
     * @return 操作结果
     */
    @PostMapping(ADMIN_PREFIX + "/ar-content/upload")
    public Result<String> uploadArContent(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("contentType") String contentType,
            @RequestParam("targetId") String targetId,
            @RequestParam("targetType") String targetType,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude) {
        
        try {
            // 处理文件上传
            String contentUrl = fileUploadUtil.uploadFile(file);
            
            ArContentDTO arContentDTO = new ArContentDTO();
            arContentDTO.setTitle(title);
            arContentDTO.setDescription(description);
            arContentDTO.setContentUrl(contentUrl);
            arContentDTO.setContentType(contentType);
            arContentDTO.setTargetId(targetId);
            arContentDTO.setTargetType(targetType);
            arContentDTO.setLatitude(latitude);
            arContentDTO.setLongitude(longitude);
            
            return arContentService.uploadArContent(arContentDTO);
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理后台端 - 获取所有AR内容
     * @return AR内容列表
     */
    @GetMapping(ADMIN_PREFIX + "/ar-content/list")
    public Result<List<ArContentDTO>> getAllArContentsForAdmin() {
        return arContentService.getAllArContents();
    }
    
    /**
     * 管理后台端 - 删除AR内容
     * @param arContentId AR内容ID
     * @return 操作结果
     */
    @DeleteMapping(ADMIN_PREFIX + "/ar-content/delete/{arContentId}")
    public Result<String> deleteArContent(@PathVariable Long arContentId) {
        return arContentService.deleteArContent(arContentId);
    }
    
    /**
     * 管理后台端 - 修改AR内容
     * @param arContentId AR内容ID
     * @param arContentDTO AR内容信息
     * @return 操作结果
     */
    @PutMapping(ADMIN_PREFIX + "/ar-content/update/{arContentId}")
    public Result<String> updateArContent(@PathVariable Long arContentId, @RequestBody ArContentDTO arContentDTO) {
        return arContentService.updateArContent(arContentId, arContentDTO);
    }
}
