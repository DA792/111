package com.scenic.service.intelligence.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.intelligence.AIQARecordDTO;
import com.scenic.entity.intelligence.AIQARecord;
import com.scenic.mapper.intelligence.AIQARecordMapper;
import com.scenic.service.intelligence.AIQARecordService;

/**
 * AI问答记录服务实现类
 */
@Service
public class AIQARecordServiceImpl implements AIQARecordService {
    
    @Autowired
    private AIQARecordMapper aiqaRecordMapper;
    
    /**
     * 分页查询问答记录列表
     * @param page 页码
     * @param size 每页大小
     * @param userId 用户ID（可选）
     * @param status 状态（可选）
     * @return 问答记录列表
     */
    @Override
    public Result<PageResult<AIQARecordDTO>> getAIQARecords(int page, int size, Long userId, Integer status) {
        try {
            // 计算偏移量
            int offset = (page - 1) * size;
            
            // 查询总记录数
            List<AIQARecord> allAIQARecords = aiqaRecordMapper.selectByCondition(userId, status);
            int total = allAIQARecords.size();
            
            // 分页查询
            List<AIQARecord> aiqaRecords = allAIQARecords.stream()
                    .skip(offset)
                    .limit(size)
                    .collect(Collectors.toList());
            
            // 转换为DTO列表
            List<AIQARecordDTO> aiqaRecordDTOs = aiqaRecords.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // 封装分页结果
            PageResult<AIQARecordDTO> pageResult = new PageResult<>();
            pageResult.setTotal(total);
            pageResult.setRecords(aiqaRecordDTOs);
            pageResult.setCurrentPage(page);
            pageResult.setPageSize(size);
            
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询问答记录列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取问答记录详情
     * @param id 问答记录ID
     * @return 问答记录详情
     */
    @Override
    public Result<AIQARecordDTO> getAIQARecordById(Long id) {
        try {
            AIQARecord aiqaRecord = aiqaRecordMapper.selectById(id);
            if (aiqaRecord == null) {
                return Result.error("问答记录不存在");
            }
            return Result.success(convertToDTO(aiqaRecord));
        } catch (Exception e) {
            return Result.error("查询问答记录详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建问答记录
     * @param aiqaRecordDTO 问答记录信息
     * @return 创建结果
     */
    @Override
    public Result<String> createAIQARecord(AIQARecordDTO aiqaRecordDTO) {
        try {
            // 转换为实体类
            AIQARecord aiqaRecord = convertToEntity(aiqaRecordDTO);
            aiqaRecord.setCreateTime(LocalDateTime.now());
            aiqaRecord.setUpdateTime(LocalDateTime.now());
            
            // 插入数据库
            int result = aiqaRecordMapper.insert(aiqaRecord);
            if (result > 0) {
                return Result.success("问答记录创建成功");
            } else {
                return Result.error("问答记录创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建问答记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新问答记录
     * @param id 问答记录ID
     * @param aiqaRecordDTO 问答记录信息
     * @return 更新结果
     */
    @Override
    public Result<String> updateAIQARecord(Long id, AIQARecordDTO aiqaRecordDTO) {
        try {
            // 检查问答记录是否存在
            AIQARecord existingAIQARecord = aiqaRecordMapper.selectById(id);
            if (existingAIQARecord == null) {
                return Result.error("问答记录不存在");
            }
            
            // 转换为实体类
            AIQARecord aiqaRecord = convertToEntity(aiqaRecordDTO);
            aiqaRecord.setId(id);
            aiqaRecord.setUpdateTime(LocalDateTime.now());
            
            // 更新数据库
            int result = aiqaRecordMapper.update(aiqaRecord);
            if (result > 0) {
                return Result.success("问答记录更新成功");
            } else {
                return Result.error("问答记录更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新问答记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除问答记录
     * @param id 问答记录ID
     * @return 删除结果
     */
    @Override
    public Result<String> deleteAIQARecord(Long id) {
        try {
            // 检查问答记录是否存在
            AIQARecord existingAIQARecord = aiqaRecordMapper.selectById(id);
            if (existingAIQARecord == null) {
                return Result.error("问答记录不存在");
            }
            
            // 删除数据库记录
            int result = aiqaRecordMapper.deleteById(id);
            if (result > 0) {
                return Result.success("问答记录删除成功");
            } else {
                return Result.error("问答记录删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除问答记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据用户ID获取问答记录列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 问答记录列表
     */
    @Override
    public Result<PageResult<AIQARecordDTO>> getAIQARecordsByUserId(Long userId, int page, int size) {
        try {
            // 计算偏移量
            int offset = (page - 1) * size;
            
            // 查询总记录数
            List<AIQARecord> allAIQARecords = aiqaRecordMapper.selectByUserId(userId);
            int total = allAIQARecords.size();
            
            // 分页查询
            List<AIQARecord> aiqaRecords = allAIQARecords.stream()
                    .skip(offset)
                    .limit(size)
                    .collect(Collectors.toList());
            
            // 转换为DTO列表
            List<AIQARecordDTO> aiqaRecordDTOs = aiqaRecords.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // 封装分页结果
            PageResult<AIQARecordDTO> pageResult = new PageResult<>();
            pageResult.setTotal(total);
            pageResult.setRecords(aiqaRecordDTOs);
            pageResult.setCurrentPage(page);
            pageResult.setPageSize(size);
            
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询用户问答记录列表失败: " + e.getMessage());
        }
    }
    
    /**
     * AI问答
     * @param userId 用户ID
     * @param question 问题
     * @return 回答结果
     */
    @Override
    public Result<AIQARecordDTO> askAI(Long userId, String question) {
        try {
            // 这里应该调用实际的AI服务来获取答案
            // 为了演示，我们使用模拟的答案
            String answer = generateAIAnswer(question);
            
            // 创建问答记录
            AIQARecord aiqaRecord = new AIQARecord();
            aiqaRecord.setUserId(userId);
            aiqaRecord.setQuestion(question);
            aiqaRecord.setAnswer(answer);
            aiqaRecord.setStatus(1); // 成功状态
            aiqaRecord.setCreateTime(LocalDateTime.now());
            aiqaRecord.setUpdateTime(LocalDateTime.now());
            
            // 插入数据库
            int result = aiqaRecordMapper.insert(aiqaRecord);
            if (result > 0) {
                return Result.success(convertToDTO(aiqaRecord));
            } else {
                return Result.error("AI问答失败");
            }
        } catch (Exception e) {
            // 创建失败记录
            AIQARecord aiqaRecord = new AIQARecord();
            aiqaRecord.setUserId(userId);
            aiqaRecord.setQuestion(question);
            aiqaRecord.setAnswer("AI服务暂时不可用，请稍后再试");
            aiqaRecord.setStatus(0); // 失败状态
            aiqaRecord.setCreateTime(LocalDateTime.now());
            aiqaRecord.setUpdateTime(LocalDateTime.now());
            
            // 插入数据库
            aiqaRecordMapper.insert(aiqaRecord);
            
            return Result.error("AI问答失败: " + e.getMessage());
        }
    }
    
    /**
     * 模拟AI回答生成
     * @param question 问题
     * @return 回答
     */
    private String generateAIAnswer(String question) {
        // 这里应该调用实际的AI服务
        // 为了演示，我们使用简单的规则匹配
        if (question.contains("保护区")) {
            return "我们的保护区致力于保护珍稀动植物，维护生态平衡。";
        } else if (question.contains("动物")) {
            return "保护区内有多种珍稀动物，包括大熊猫、金丝猴等。";
        } else if (question.contains("植物")) {
            return "保护区内有丰富的植物资源，包括珙桐、银杉等珍稀植物。";
        } else {
            return "感谢您的提问，我们的工作人员会尽快为您提供更详细的解答。";
        }
    }
    
    /**
     * 将实体类转换为DTO
     * @param aiqaRecord 问答记录实体
     * @return 问答记录DTO
     */
    private AIQARecordDTO convertToDTO(AIQARecord aiqaRecord) {
        AIQARecordDTO aiqaRecordDTO = new AIQARecordDTO();
        BeanUtils.copyProperties(aiqaRecord, aiqaRecordDTO);
        return aiqaRecordDTO;
    }
    
    /**
     * 将DTO转换为实体类
     * @param aiqaRecordDTO 问答记录DTO
     * @return 问答记录实体
     */
    private AIQARecord convertToEntity(AIQARecordDTO aiqaRecordDTO) {
        AIQARecord aiqaRecord = new AIQARecord();
        BeanUtils.copyProperties(aiqaRecordDTO, aiqaRecord);
        return aiqaRecord;
    }
}
