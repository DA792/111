package com.scenic.controller.operation;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.operation.OperatorDTO;
import com.scenic.entity.operation.Operator;
import com.scenic.service.operation.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 操作员管理控制器
 * 提供管理后台端的操作员管理API接口
 */
@RestController
@RequestMapping("/api/manage/operators")
public class OperatorController {
    
    @Autowired
    private OperatorService operatorService;
    
    /**
     * 管理后台端 - 分页查询操作员列表
     * @param page 页码
     * @param size 每页大小
     * @param username 用户名（可选）
     * @param realName 真实姓名（可选）
     * @param status 状态（可选）
     * @return 操作员列表
     */
    @GetMapping
    public Result<PageResult<OperatorDTO>> getOperators(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer status) {
        return operatorService.getOperators(page, size, username, realName, status);
    }
    
    /**
     * 管理后台端 - 根据ID获取操作员详情
     * @param id 操作员ID
     * @return 操作员详情
     */
    @GetMapping("/{id}")
    public Result<OperatorDTO> getOperatorById(@PathVariable Long id) {
        return operatorService.getOperatorById(id);
    }
    
    /**
     * 管理后台端 - 创建操作员
     * @param operatorDTO 操作员信息
     * @return 创建结果
     */
    @PostMapping
    public Result<String> createOperator(@RequestBody OperatorDTO operatorDTO) {
        return operatorService.createOperator(operatorDTO);
    }
    
    /**
     * 管理后台端 - 更新操作员
     * @param id 操作员ID
     * @param operatorDTO 操作员信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<String> updateOperator(@PathVariable Long id, @RequestBody OperatorDTO operatorDTO) {
        return operatorService.updateOperator(id, operatorDTO);
    }
    
    /**
     * 管理后台端 - 删除操作员
     * @param id 操作员ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteOperator(@PathVariable Long id) {
        return operatorService.deleteOperator(id);
    }
    
    /**
     * 管理后台端 - 启用/禁用操作员
     * @param id 操作员ID
     * @param status 状态（0-禁用，1-启用）
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public Result<String> updateOperatorStatus(@PathVariable Long id, @RequestParam Integer status) {
        return operatorService.updateOperatorStatus(id, status);
    }
}
