package com.scenic.entity.map;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 导览路线实体类
 */
public class GuideRoute {
    private Long id;
    private String name;
    private String description;
    private Double totalDistance; // 总距离（米）
    private Integer estimatedTime; // 预估游览时间（分钟）
    private String category; // 路线分类
    private Boolean enabled; // 是否启用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联的路线节点列表
    private List<RouteNode> routeNodes;

    // 构造函数
    public GuideRoute() {}

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Integer getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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
    
    public List<RouteNode> getRouteNodes() {
        return routeNodes;
    }
    
    public void setRouteNodes(List<RouteNode> routeNodes) {
        this.routeNodes = routeNodes;
    }

    @Override
    public String toString() {
        return "GuideRoute{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", totalDistance=" + totalDistance +
                ", estimatedTime=" + estimatedTime +
                ", category='" + category + '\'' +
                ", enabled=" + enabled +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", routeNodes=" + routeNodes +
                '}';
    }
}
