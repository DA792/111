package com.scenic.service.intelligence;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.intelligence.KnowledgeGraphDTO;

/**
 * 知识图谱服务接口
 */
public interface KnowledgeGraphService {
    
    /**
     * 分页查询知识图谱列表
     * @param page 页码
     * @param size 每页大小
     * @param speciesName 物种名称（可选）
     * @param category 分类（可选）
     * @param status 状态（可选）
     * @return 知识图谱列表
     */
    Result<PageResult<KnowledgeGraphDTO>> getKnowledgeGraphs(int page, int size, String speciesName, String category, Integer status);
    
    /**
     * 根据ID获取知识图谱详情
     * @param id 知识图谱ID
     * @return 知识图谱详情
     */
    Result<KnowledgeGraphDTO> getKnowledgeGraphById(Long id);
    
    /**
     * 创建知识图谱
     * @param knowledgeGraphDTO 知识图谱信息
     * @return 创建结果
     */
    Result<String> createKnowledgeGraph(KnowledgeGraphDTO knowledgeGraphDTO);
    
    /**
     * 更新知识图谱
     * @param id 知识图谱ID
     * @param knowledgeGraphDTO 知识图谱信息
     * @return 更新结果
     */
    Result<String> updateKnowledgeGraph(Long id, KnowledgeGraphDTO knowledgeGraphDTO);
    
    /**
     * 删除知识图谱
     * @param id 知识图谱ID
     * @return 删除结果
     */
    Result<String> deleteKnowledgeGraph(Long id);
    
    /**
     * 上传知识图谱图片
     * @param file 图片文件
     * @param speciesName 物种名称
     * @return 上传结果
     */
    Result<String> uploadKnowledgeGraphImage(MultipartFile file, String speciesName);
    
    /**
     * 获取所有启用的知识图谱（分页）
     * @param page 页码
     * @param size 每页大小
     * @return 知识图谱列表
     */
    Result<PageResult<KnowledgeGraphDTO>> getEnabledKnowledgeGraphs(int page, int size);
    
    /**
     * 根据物种名称获取知识图谱
     * @param speciesName 物种名称
     * @return 知识图谱
     */
    Result<KnowledgeGraphDTO> getKnowledgeGraphBySpeciesName(String speciesName);
    
    /**
     * 根据分类获取知识图谱列表
     * @param category 分类
     * @return 知识图谱列表
     */
    Result<List<KnowledgeGraphDTO>> getKnowledgeGraphsByCategory(String category);
}
