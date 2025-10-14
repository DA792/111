package com.scenic.dto.interaction;

import java.time.LocalDateTime;

public class PhotoCheckInQueryDTO {
    
    /**
     * 页码数
     */
    private Integer pageNum = 1;
    
    /**
     * 每页条数
     */
    private Integer pageSize = 10;
    
    /**
     * 标题 - 模糊搜索
     */
    private String title;
    
    /**
     * 发布用户 - 模糊搜索
     */
    private String userName;
    
    /**
     * 发布时间
     */
    private LocalDateTime createTime;
    
    /**
     * 分类
     */
    private Long categoryId;

    // Getter和Setter方法
    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}