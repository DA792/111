package com.scenic.dto.intelligence;

import java.time.LocalDateTime;

/**
 * AI问答记录DTO类
 */
public class AIQARecordDTO {
    
    /**
     * 问答记录ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 问题
     */
    private String question;
    
    /**
     * 回答
     */
    private String answer;
    
    /**
     * 引用的知识库ID
     */
    private Long knowledgeBaseId;
    
    /**
     * 引用的知识图谱ID
     */
    private Long knowledgeGraphId;
    
    /**
     * 状态（0-失败，1-成功）
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // Getter和Setter方法
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public void setQuestion(String question) {
        this.question = question;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    
    public Long getKnowledgeBaseId() {
        return knowledgeBaseId;
    }
    
    public void setKnowledgeBaseId(Long knowledgeBaseId) {
        this.knowledgeBaseId = knowledgeBaseId;
    }
    
    public Long getKnowledgeGraphId() {
        return knowledgeGraphId;
    }
    
    public void setKnowledgeGraphId(Long knowledgeGraphId) {
        this.knowledgeGraphId = knowledgeGraphId;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
