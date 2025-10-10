package com.scenic.service.interaction;

import java.util.List;

import com.scenic.common.dto.Result;
import com.scenic.dto.interaction.ArContentDTO;

/**
 * AR内容服务接口
 */
public interface ArContentService {
    
    /**
     * 上传AR内容
     * @param arContentDTO AR内容信息
     * @return 操作结果
     */
    Result<String> uploadArContent(ArContentDTO arContentDTO);
    
    /**
     * 获取所有AR内容
     * @return AR内容列表
     */
    Result<List<ArContentDTO>> getAllArContents();
    
    /**
     * 根据目标ID和类型获取AR内容
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return AR内容列表
     */
    Result<List<ArContentDTO>> getArContentsByTarget(String targetId, String targetType);
    
    /**
     * 根据内容类型获取AR内容
     * @param contentType 内容类型
     * @return AR内容列表
     */
    Result<List<ArContentDTO>> getArContentsByContentType(String contentType);
    
    /**
     * 管理端 - 删除AR内容
     * @param arContentId AR内容ID
     * @return 操作结果
     */
    Result<String> deleteArContent(Long arContentId);
    
    /**
     * 管理端 - 修改AR内容
     * @param arContentId AR内容ID
     * @param arContentDTO AR内容信息
     * @return 操作结果
     */
    Result<String> updateArContent(Long arContentId, ArContentDTO arContentDTO);
}
