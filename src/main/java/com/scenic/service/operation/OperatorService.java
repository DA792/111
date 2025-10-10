package com.scenic.service.operation;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.operation.OperatorDTO;
import com.scenic.entity.operation.Operator;

/**
 * 操作员服务接口
 */
public interface OperatorService {
    
    /**
     * 分页查询操作员列表
     * @param page 页码
     * @param size 每页大小
     * @param username 用户名（可选）
     * @param realName 真实姓名（可选）
     * @param status 状态（可选）
     * @return 操作员列表
     */
    Result<PageResult<OperatorDTO>> getOperators(int page, int size, String username, String realName, Integer status);
    
    /**
     * 根据ID获取操作员详情
     * @param id 操作员ID
     * @return 操作员详情
     */
    Result<OperatorDTO> getOperatorById(Long id);
    
    /**
     * 创建操作员
     * @param operatorDTO 操作员信息
     * @return 创建结果
     */
    Result<String> createOperator(OperatorDTO operatorDTO);
    
    /**
     * 更新操作员
     * @param id 操作员ID
     * @param operatorDTO 操作员信息
     * @return 更新结果
     */
    Result<String> updateOperator(Long id, OperatorDTO operatorDTO);
    
    /**
     * 删除操作员
     * @param id 操作员ID
     * @return 删除结果
     */
    Result<String> deleteOperator(Long id);
    
    /**
     * 启用/禁用操作员
     * @param id 操作员ID
     * @param status 状态（0-禁用，1-启用）
     * @return 更新结果
     */
    Result<String> updateOperatorStatus(Long id, Integer status);
    
    /**
     * 根据用户名查询操作员
     * @param username 用户名
     * @return 操作员实体
     */
    Operator getOperatorByUsername(String username);
}
