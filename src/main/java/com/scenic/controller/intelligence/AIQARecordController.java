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

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.intelligence.AIQARecordDTO;
import com.scenic.service.intelligence.AIQARecordService;

/**
 * AI问答记录控制器
 */
@RestController
@RequestMapping("/api/manage/intelligence/ai-qa")
public class AIQARecordController {
    
    @Autowired
    private AIQARecordService aiqaRecordService;
    
    /**
     * 分页查询问答记录列表
     * @param page 页码
     * @param size 每页大小
     * @param userId 用户ID（可选）
     * @param status 状态（可选）
     * @return 问答记录列表
     */
    @GetMapping
    public Result<PageResult<AIQARecordDTO>> getAIQARecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status) {
        return aiqaRecordService.getAIQARecords(page, size, userId, status);
    }
    
    /**
     * 根据ID获取问答记录详情
     * @param id 问答记录ID
     * @return 问答记录详情
     */
    @GetMapping("/{id}")
    public Result<AIQARecordDTO> getAIQARecordById(@PathVariable Long id) {
        return aiqaRecordService.getAIQARecordById(id);
    }
    
    /**
     * 创建问答记录
     * @param aiqaRecordDTO 问答记录信息
     * @return 创建结果
     */
    @PostMapping
    public Result<String> createAIQARecord(@RequestBody AIQARecordDTO aiqaRecordDTO) {
        return aiqaRecordService.createAIQARecord(aiqaRecordDTO);
    }
    
    /**
     * 更新问答记录
     * @param id 问答记录ID
     * @param aiqaRecordDTO 问答记录信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<String> updateAIQARecord(@PathVariable Long id, @RequestBody AIQARecordDTO aiqaRecordDTO) {
        return aiqaRecordService.updateAIQARecord(id, aiqaRecordDTO);
    }
    
    /**
     * 删除问答记录
     * @param id 问答记录ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteAIQARecord(@PathVariable Long id) {
        return aiqaRecordService.deleteAIQARecord(id);
    }
    
    /**
     * 根据用户ID获取问答记录列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 问答记录列表
     */
    @GetMapping("/user/{userId}")
    public Result<PageResult<AIQARecordDTO>> getAIQARecordsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return aiqaRecordService.getAIQARecordsByUserId(userId, page, size);
    }
    
    /**
     * 小程序端AI问答
     * @param userId 用户ID
     * @param question 问题
     * @return 回答结果
     */
    @PostMapping("/ask")
    public Result<AIQARecordDTO> askAI(@RequestParam Long userId, @RequestParam String question) {
        return aiqaRecordService.askAI(userId, question);
    }
}
