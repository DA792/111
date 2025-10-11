package com.scenic.service.intelligence;

import org.springframework.web.multipart.MultipartFile;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.intelligence.KnowledgeBaseDTO;

/**
 * 知识库服务接口
 */
public interface KnowledgeBaseService {
    
    /**
     * 分页查询知识库列表
     * @param page 页码
     * @param size 每页大小
     * @param title 知识库标题（可选）
     * @param version 版本号（可选）
     * @param status 状态（可选）
     * @return 知识库列表
     */
    Result<PageResult<KnowledgeBaseDTO>> getKnowledgeBases(int page, int size, String title, String version, Integer status);
    
    /**
     * 根据ID获取知识库详情
     * @param id 知识库ID
     * @return 知识库详情
     */
    Result<KnowledgeBaseDTO> getKnowledgeBaseById(Long id);
    
    /**
     * 创建知识库
     * @param knowledgeBaseDTO 知识库信息
     * @return 创建结果
     */
    Result<String> createKnowledgeBase(KnowledgeBaseDTO knowledgeBaseDTO);
    
    /**
     * 更新知识库
     * @param id 知识库ID
     * @param knowledgeBaseDTO 知识库信息
     * @return 更新结果
     */
    Result<String> updateKnowledgeBase(Long id, KnowledgeBaseDTO knowledgeBaseDTO);
    
    /**
     * 删除知识库
     * @param id 知识库ID
     * @return 删除结果
     */
    Result<String> deleteKnowledgeBase(Long id);
    
    /**
     * 上传知识库文件
     * @param file 知识库文件
     * @param title 知识库标题
     * @return 上传结果
     */
    Result<String> uploadKnowledgeBaseFile(MultipartFile file, String title);
    
    /**
     * 获取所有启用的知识库（分页）
     * @param page 页码
     * @param size 每页大小
     * @return 知识库列表
     */
    Result<PageResult<KnowledgeBaseDTO>> getEnabledKnowledgeBases(int page, int size);
    
    /**
     * 根据ID获取启用的知识库详情
     * @param id 知识库ID
     * @return 知识库详情
     */
    Result<KnowledgeBaseDTO> getEnabledKnowledgeBaseById(Long id);
    
    /**
     * 根据版本号获取知识库
     * @param version 版本号
     * @return 知识库
     */
    Result<KnowledgeBaseDTO> getKnowledgeBaseByVersion(String version);
}
