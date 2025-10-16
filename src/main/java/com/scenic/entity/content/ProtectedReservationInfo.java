package com.scenic.entity.content;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 保护区介绍实体类
 */
public class ProtectedReservationInfo {
    private Long id;
    private String title; // 标题
    private Byte contentType; // 内容类型
    private Byte contentCategory; // 内容分类
    private String richContent; // 富文本内容
    private String plainText; // 纯文本内容
    private List<Long> contentImageIds; // 内容图片ID列表
    private List<Long> carouselFileIds; // 轮播图文件ID列表
    private List<Long> galleryFileIds; // 画廊文件ID列表
    private List<Long> audioFileIds; // 音频文件ID列表
    private Byte deleted; // 删除标记
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
    private Long createBy; // 创建人
    private Long updateBy; // 更新人

    // 构造函数
    public ProtectedReservationInfo() {}

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Byte getContentType() {
        return contentType;
    }

    public void setContentType(Byte contentType) {
        this.contentType = contentType;
    }

    public Byte getContentCategory() {
        return contentCategory;
    }

    public void setContentCategory(Byte contentCategory) {
        this.contentCategory = contentCategory;
    }

    public String getRichContent() {
        return richContent;
    }

    public void setRichContent(String richContent) {
        this.richContent = richContent;
    }

    public String getPlainText() {
        return plainText;
    }

    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }

    public List<Long> getContentImageIds() {
        return contentImageIds;
    }

    public void setContentImageIds(List<Long> contentImageIds) {
        this.contentImageIds = contentImageIds;
    }

    public List<Long> getCarouselFileIds() {
        return carouselFileIds;
    }

    public void setCarouselFileIds(List<Long> carouselFileIds) {
        this.carouselFileIds = carouselFileIds;
    }

    public List<Long> getGalleryFileIds() {
        return galleryFileIds;
    }

    public void setGalleryFileIds(List<Long> galleryFileIds) {
        this.galleryFileIds = galleryFileIds;
    }

    public List<Long> getAudioFileIds() {
        return audioFileIds;
    }

    public void setAudioFileIds(List<Long> audioFileIds) {
        this.audioFileIds = audioFileIds;
    }

    public Byte getDeleted() {
        return deleted;
    }

    public void setDeleted(Byte deleted) {
        this.deleted = deleted;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    @Override
    public String toString() {
        return "ProtectedReservationInfo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", contentType=" + contentType +
                ", contentCategory=" + contentCategory +
                ", richContent='" + richContent + '\'' +
                ", plainText='" + plainText + '\'' +
                ", contentImageIds=" + contentImageIds +
                ", carouselFileIds=" + carouselFileIds +
                ", galleryFileIds=" + galleryFileIds +
                ", audioFileIds=" + audioFileIds +
                ", deleted=" + deleted +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createBy=" + createBy +
                ", updateBy=" + updateBy +
                '}';
    }
}