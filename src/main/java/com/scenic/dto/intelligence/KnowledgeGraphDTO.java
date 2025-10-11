package com.scenic.dto.intelligence;

import java.time.LocalDateTime;

/**
 * 知识图谱DTO类
 */
public class KnowledgeGraphDTO {
    
    /**
     * 知识图谱ID
     */
    private Long id;
    
    /**
     * 物种名称
     */
    private String speciesName;
    
    /**
     * 学名
     */
    private String scientificName;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 分类
     */
    private String category;
    
    /**
     * 栖息地
     */
    private String habitat;
    
    /**
     * 状态（0-禁用，1-启用）
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
    
    public String getSpeciesName() {
        return speciesName;
    }
    
    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }
    
    public String getScientificName() {
        return scientificName;
    }
    
    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getHabitat() {
        return habitat;
    }
    
    public void setHabitat(String habitat) {
        this.habitat = habitat;
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
