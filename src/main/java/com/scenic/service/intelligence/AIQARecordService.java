package com.scenic.service.intelligence;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.intelligence.AIQARecordDTO;

/**
 * AI问答记录服务接口
 */
public interface AIQARecordService {
    
    /**
     * 分页查询问答记录列表
     * @param page 页码
     * @param size 每页大小
     * @param userId 用户ID（可选）
     * @param status 状态（可选）
     * @return 问答记录列表
     */
    Result<PageResult<AIQARecordDTO>> getAIQARecords(int page, int size, Long userId, Integer status);
    
    /**
     * 根据ID获取问答记录详情
     * @param id 问答记录ID
     * @return 问答记录详情
     */
    Result<AIQARecordDTO> getAIQARecordById(Long id);
    
    /**
     * 创建问答记录
     * @param aiqaRecordDTO 问答记录信息
     * @return 创建结果
     */
    Result<String> createAIQARecord(AIQARecordDTO aiqaRecordDTO);
    
    /**
     * 更新问答记录
     * @param id 问答记录ID
     * @param aiqaRecordDTO 问答记录信息
     * @return 更新结果
     */
    Result<String> updateAIQARecord(Long id, AIQARecordDTO aiqaRecordDTO);
    
    /**
     * 删除问答记录
     * @param id 问答记录ID
     * @return 删除结果
     */
    Result<String> deleteAIQARecord(Long id);
    
    /**
     * 根据用户ID获取问答记录列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 问答记录列表
     */
    Result<PageResult<AIQARecordDTO>> getAIQARecordsByUserId(Long userId, int page, int size);
    
    /**
     * AI问答
     * @param userId 用户ID
     * @param question 问题
     * @return 回答结果
     */
    Result<AIQARecordDTO> askAI(Long userId, String question);
}
