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
import com.scenic.dto.intelligence.KnowledgeGraphDTO;
import com.scenic.service.intelligence.KnowledgeGraphService;
import com.scenic.utils.FileUploadUtil;

import java.io.IOException;
import java.util.List;

/**
 * 知识图谱控制器
 */
@RestController
@RequestMapping("/api/manage/intelligence/knowledge-graph")
public class KnowledgeGraphController {
    
    @Autowired
    private KnowledgeGraphService knowledgeGraphService;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;
    
    /**
     * 分页查询知识图谱列表
     * @param page 页码
     * @param size 每页大小
     * @param speciesName 物种名称（可选）
     * @param category 分类（可选）
     * @param status 状态（可选）
     * @return 知识图谱列表
     */
    @GetMapping
    public Result<PageResult<KnowledgeGraphDTO>> getKnowledgeGraphs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String speciesName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        return knowledgeGraphService.getKnowledgeGraphs(page, size, speciesName, category, status);
    }
    
    /**
     * 根据ID获取知识图谱详情
     * @param id 知识图谱ID
     * @return 知识图谱详情
     */
    @GetMapping("/{id}")
    public Result<KnowledgeGraphDTO> getKnowledgeGraphById(@PathVariable Long id) {
        return knowledgeGraphService.getKnowledgeGraphById(id);
    }
    
    /**
     * 创建知识图谱
     * @param knowledgeGraphDTO 知识图谱信息
     * @return 创建结果
     */
    @PostMapping
    public Result<String> createKnowledgeGraph(@RequestBody KnowledgeGraphDTO knowledgeGraphDTO) {
        return knowledgeGraphService.createKnowledgeGraph(knowledgeGraphDTO);
    }
    
    /**
     * 更新知识图谱
     * @param id 知识图谱ID
     * @param knowledgeGraphDTO 知识图谱信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<String> updateKnowledgeGraph(@PathVariable Long id, @RequestBody KnowledgeGraphDTO knowledgeGraphDTO) {
        return knowledgeGraphService.updateKnowledgeGraph(id, knowledgeGraphDTO);
    }
    
    /**
     * 删除知识图谱
     * @param id 知识图谱ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteKnowledgeGraph(@PathVariable Long id) {
        return knowledgeGraphService.deleteKnowledgeGraph(id);
    }
    
    /**
     * 上传知识图谱图片
     * @param file 图片文件
     * @param speciesName 物种名称
     * @return 上传结果
     */
    @PostMapping("/upload-image")
    public Result<String> uploadKnowledgeGraphImage(@RequestParam("file") MultipartFile file,
                                                  @RequestParam("speciesName") String speciesName) {
        try {
            // 上传文件
            String filename = fileUploadUtil.uploadFile(file);
            
            // 更新知识图谱记录
            KnowledgeGraphDTO knowledgeGraphDTO = new KnowledgeGraphDTO();
            knowledgeGraphDTO.setSpeciesName(speciesName);
            knowledgeGraphDTO.setImageUrl(filename);
            knowledgeGraphDTO.setStatus(1); // 默认启用
            
            // 保存到数据库
            return knowledgeGraphService.createKnowledgeGraph(knowledgeGraphDTO);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 小程序端获取所有启用的知识图谱（分页）
     * @param page 页码
     * @param size 每页大小
     * @return 知识图谱列表
     */
    @GetMapping("/enabled")
    public Result<PageResult<KnowledgeGraphDTO>> getEnabledKnowledgeGraphs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            return knowledgeGraphService.getEnabledKnowledgeGraphs(page, size);
        } catch (Exception e) {
            return Result.error("查询启用的知识图谱列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 小程序端根据物种名称获取知识图谱
     * @param speciesName 物种名称
     * @return 知识图谱
     */
    @GetMapping("/species/{speciesName}")
    public Result<KnowledgeGraphDTO> getKnowledgeGraphBySpeciesName(@PathVariable String speciesName) {
        return knowledgeGraphService.getKnowledgeGraphBySpeciesName(speciesName);
    }
    
    /**
     * 小程序端根据分类获取知识图谱列表
     * @param category 分类
     * @return 知识图谱列表
     */
    @GetMapping("/category/{category}")
    public Result<List<KnowledgeGraphDTO>> getKnowledgeGraphsByCategory(@PathVariable String category) {
        return knowledgeGraphService.getKnowledgeGraphsByCategory(category);
    }
}
