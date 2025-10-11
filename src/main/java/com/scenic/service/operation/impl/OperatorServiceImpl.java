package com.scenic.service.operation.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.operation.OperatorDTO;
import com.scenic.entity.operation.Operator;
import com.scenic.mapper.operation.OperatorMapper;
import com.scenic.service.operation.OperatorService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作员服务实现类
 */
@Service
public class OperatorServiceImpl implements OperatorService {
    
    @Autowired
    private OperatorMapper operatorMapper;
    
    /**
     * 分页查询操作员列表
     * @param page 页码
     * @param size 每页大小
     * @param username 用户名（可选）
     * @param realName 真实姓名（可选）
     * @param status 状态（可选）
     * @return 操作员列表
     */
    @Override
    public Result<PageResult<OperatorDTO>> getOperators(int page, int size, String username, String realName, Integer status) {
        try {
            // 使用PageHelper进行分页
            PageHelper.startPage(page, size);
            
            // 查询操作员列表
            List<Operator> operators = operatorMapper.selectByCondition(username, realName, status);
            
            // 转换为PageInfo
            PageInfo<Operator> pageInfo = new PageInfo<>(operators);
            
            // 转换为DTO列表
            List<OperatorDTO> operatorDTOs = operators.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // 构造PageResult
            PageResult<OperatorDTO> pageResult = new PageResult<>();
            pageResult.setTotal(pageInfo.getTotal());
            pageResult.setRecords(operatorDTOs);
            pageResult.setCurrentPage(pageInfo.getPageNum());
            pageResult.setPageSize(pageInfo.getPageSize());
            
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询操作员列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取操作员详情
     * @param id 操作员ID
     * @return 操作员详情
     */
    @Override
    public Result<OperatorDTO> getOperatorById(Long id) {
        try {
            Operator operator = operatorMapper.selectById(id);
            if (operator == null) {
                return Result.error("操作员不存在");
            }
            return Result.success(convertToDTO(operator));
        } catch (Exception e) {
            return Result.error("查询操作员详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建操作员
     * @param operatorDTO 操作员信息
     * @return 创建结果
     */
    @Override
    public Result<String> createOperator(OperatorDTO operatorDTO) {
        try {
            // 检查用户名是否已存在
            Operator existingOperator = operatorMapper.selectByUsername(operatorDTO.getUsername());
            if (existingOperator != null) {
                return Result.error("用户名已存在");
            }
            
            // 转换为实体类
            Operator operator = convertToEntity(operatorDTO);
            operator.setCreateTime(LocalDateTime.now());
            operator.setUpdateTime(LocalDateTime.now());
            
            // 设置默认状态为启用
            if (operator.getStatus() == null) {
                operator.setStatus(1);
            }
            
            // 插入数据库
            int result = operatorMapper.insert(operator);
            if (result > 0) {
                return Result.success("操作员创建成功");
            } else {
                return Result.error("操作员创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建操作员失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新操作员
     * @param id 操作员ID
     * @param operatorDTO 操作员信息
     * @return 更新结果
     */
    @Override
    public Result<String> updateOperator(Long id, OperatorDTO operatorDTO) {
        try {
            // 检查操作员是否存在
            Operator existingOperator = operatorMapper.selectById(id);
            if (existingOperator == null) {
                return Result.error("操作员不存在");
            }
            
            // 检查用户名是否已存在（排除当前操作员）
            if (operatorDTO.getUsername() != null && !operatorDTO.getUsername().equals(existingOperator.getUsername())) {
                Operator operatorWithSameUsername = operatorMapper.selectByUsername(operatorDTO.getUsername());
                if (operatorWithSameUsername != null) {
                    return Result.error("用户名已存在");
                }
            }
            
            // 转换为实体类
            Operator operator = convertToEntity(operatorDTO);
            operator.setId(id);
            operator.setUpdateTime(LocalDateTime.now());
            
            // 更新数据库
            int result = operatorMapper.update(operator);
            if (result > 0) {
                return Result.success("操作员更新成功");
            } else {
                return Result.error("操作员更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新操作员失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除操作员
     * @param id 操作员ID
     * @return 删除结果
     */
    @Override
    public Result<String> deleteOperator(Long id) {
        try {
            // 检查操作员是否存在
            Operator existingOperator = operatorMapper.selectById(id);
            if (existingOperator == null) {
                return Result.error("操作员不存在");
            }
            
            // 删除数据库记录
            int result = operatorMapper.deleteById(id);
            if (result > 0) {
                return Result.success("操作员删除成功");
            } else {
                return Result.error("操作员删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除操作员失败: " + e.getMessage());
        }
    }
    
    /**
     * 启用/禁用操作员
     * @param id 操作员ID
     * @param status 状态（0-禁用，1-启用）
     * @return 更新结果
     */
    @Override
    public Result<String> updateOperatorStatus(Long id, Integer status) {
        try {
            // 检查操作员是否存在
            Operator existingOperator = operatorMapper.selectById(id);
            if (existingOperator == null) {
                return Result.error("操作员不存在");
            }
            
            // 更新状态
            existingOperator.setStatus(status);
            existingOperator.setUpdateTime(LocalDateTime.now());
            
            // 更新数据库
            int result = operatorMapper.update(existingOperator);
            if (result > 0) {
                return Result.success("操作员状态更新成功");
            } else {
                return Result.error("操作员状态更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新操作员状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据用户名查询操作员
     * @param username 用户名
     * @return 操作员实体
     */
    @Override
    public Operator getOperatorByUsername(String username) {
        return operatorMapper.selectByUsername(username);
    }
    
    /**
     * 将实体类转换为DTO
     * @param operator 操作员实体
     * @return 操作员DTO
     */
    private OperatorDTO convertToDTO(Operator operator) {
        OperatorDTO operatorDTO = new OperatorDTO();
        BeanUtils.copyProperties(operator, operatorDTO);
        return operatorDTO;
    }
    
    /**
     * 将DTO转换为实体类
     * @param operatorDTO 操作员DTO
     * @return 操作员实体
     */
    private Operator convertToEntity(OperatorDTO operatorDTO) {
        Operator operator = new Operator();
        BeanUtils.copyProperties(operatorDTO, operator);
        return operator;
    }
}
