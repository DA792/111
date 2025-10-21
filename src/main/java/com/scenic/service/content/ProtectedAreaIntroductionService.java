package com.scenic.service.content;

import com.scenic.common.dto.Result;
import com.scenic.dto.content.ProtectedAreaIntroductionDTO;

import java.util.List;

/**
 * 保护区介绍服务接口
 */
public interface ProtectedAreaIntroductionService {
    
    /**
     * 获取所有保护区介绍
     * @return 保护区介绍列表
     */
    Result<List<ProtectedAreaIntroductionDTO>> getAllProtectedAreaIntroductions();
    
    /**
     * 根据语言获取保护区介绍
     * @param language 语言
     * @return 保护区介绍列表
     */
    Result<List<ProtectedAreaIntroductionDTO>> getProtectedAreaIntroductionsByLanguage(String language);
    
    /**
     * 根据ID获取保护区介绍详情
     * @param id 保护区介绍ID
     * @return 保护区介绍详情
     */
    Result<ProtectedAreaIntroductionDTO> getProtectedAreaIntroductionById(Long id);
    
    /**
     * 更新保护区介绍
     * @param id 保护区介绍ID
     * @param introductionDTO 保护区介绍信息
     * @return 操作结果
     */
    Result<String> updateProtectedAreaIntroduction(Long id, ProtectedAreaIntroductionDTO introductionDTO);
    
    /**
     * 删除保护区介绍
     * @param id 保护区介绍ID
     * @return 操作结果
     */
    Result<String> deleteProtectedAreaIntroduction(Long id);
}