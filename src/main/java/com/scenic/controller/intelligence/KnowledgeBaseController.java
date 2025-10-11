package com.scenic.controller.intelligence;

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

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.intelligence.KnowledgeBaseDTO;
import com.scenic.service.intelligence.KnowledgeBaseService;
import com.scenic.utils.FileUploadUtil;

import java.io.IOException;
import java.util.List;

/**
 * 知识库控制器
 */
@RestController
@RequestMapping("/api/manage/intelligence/knowledge-base")
public class KnowledgeBaseController {
    
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;
    
    /**
     * 分页查询知识库列表
     * @param page 页码
     * @param size 每页大小
     * @param title 知识库标题（可选）
     * @param version 版本号（可选）
     * @param status 状态（可选）
     * @return 知识库列表
     */
    @GetMapping
    public Result<PageResult<KnowledgeBaseDTO>> getKnowledgeBases(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String version,
            @RequestParam(required = false) Integer status) {
        return knowledgeBaseService.getKnowledgeBases(page, size, title, version, status);
    }
    
    /**
     * 根据ID获取知识库详情
     * @param id 知识库ID
     * @return 知识库详情
     */
    @GetMapping("/{id}")
    public Result<KnowledgeBaseDTO> getKnowledgeBaseById(@PathVariable Long id) {
        return knowledgeBaseService.getKnowledgeBaseById(id);
    }
    
    /**
     * 创建知识库
     * @param knowledgeBaseDTO 知识库信息
     * @return 创建结果
     */
    @PostMapping
    public Result<String> createKnowledgeBase(@RequestBody KnowledgeBaseDTO knowledgeBaseDTO) {
        return knowledgeBaseService.createKnowledgeBase(knowledgeBaseDTO);
    }
    
    /**
     * 更新知识库
     * @param id 知识库ID
     * @param knowledgeBaseDTO 知识库信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<String> updateKnowledgeBase(@PathVariable Long id, @RequestBody KnowledgeBaseDTO knowledgeBaseDTO) {
        return knowledgeBaseService.updateKnowledgeBase(id, knowledgeBaseDTO);
    }
    
    /**
     * 删除知识库
     * @param id 知识库ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteKnowledgeBase(@PathVariable Long id) {
        return knowledgeBaseService.deleteKnowledgeBase(id);
    }
    
    /**
     * 上传知识库文件
     * @param file 知识库文件
     * @param title 知识库标题
     * @return 上传结果
     */
    @PostMapping("/upload")
    public Result<String> uploadKnowledgeBaseFile(@RequestParam("file") MultipartFile file,
                                                @RequestParam("title") String title) {
        try {
            // 上传文件
            String filename = fileUploadUtil.uploadFile(file);
            
            // 创建知识库记录
            KnowledgeBaseDTO knowledgeBaseDTO = new KnowledgeBaseDTO();
            knowledgeBaseDTO.setTitle(title);
            knowledgeBaseDTO.setFileName(file.getOriginalFilename());
            knowledgeBaseDTO.setFileSize(file.getSize());
            knowledgeBaseDTO.setFileType(file.getContentType());
            knowledgeBaseDTO.setFilePath(filename);
            knowledgeBaseDTO.setStatus(1); // 默认启用
            
            // 保存到数据库
            return knowledgeBaseService.createKnowledgeBase(knowledgeBaseDTO);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 小程序端获取所有启用的知识库（分页）
     * @param page 页码
     * @param size 每页大小
     * @return 启用的知识库列表
     */
    @GetMapping("/enabled")
    public Result<PageResult<KnowledgeBaseDTO>> getEnabledKnowledgeBases(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return knowledgeBaseService.getEnabledKnowledgeBases(page, size);
    }
    
    /**
     * 小程序端根据ID获取启用的知识库详情
     * @param id 知识库ID
     * @return 知识库详情
     */
    @GetMapping("/enabled/{id}")
    public Result<KnowledgeBaseDTO> getEnabledKnowledgeBaseById(@PathVariable Long id) {
        return knowledgeBaseService.getEnabledKnowledgeBaseById(id);
    }
}
