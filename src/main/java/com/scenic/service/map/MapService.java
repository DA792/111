package com.scenic.service.map;

import java.util.List;

import com.scenic.common.dto.Result;
import com.scenic.dto.map.GuideRouteDTO;
import com.scenic.dto.map.RouteNodeDTO;
import com.scenic.dto.map.ScenicSpotDTO;

/**
 * 地图导览服务接口
 */
public interface MapService {
    
    /**
     * 获取所有启用的景点信息
     * @return 景点列表
     */
    Result<List<ScenicSpotDTO>> getAllScenicSpots();
    
    /**
     * 根据分类获取景点信息
     * @param category 景点分类
     * @return 景点列表
     */
    Result<List<ScenicSpotDTO>> getScenicSpotsByCategory(String category);
    
    /**
     * 获取所有启用的导览路线
     * @return 导览路线列表
     */
    Result<List<GuideRouteDTO>> getAllGuideRoutes();
    
    /**
     * 根据分类获取导览路线
     * @param category 路线分类
     * @return 导览路线列表
     */
    Result<List<GuideRouteDTO>> getGuideRoutesByCategory(String category);
    
    /**
     * 根据路线ID获取路线详情及节点信息
     * @param routeId 路线ID
     * @return 导览路线详情
     */
    Result<GuideRouteDTO> getGuideRouteDetail(Long routeId);
    
    /**
     * 获取所有景点信息（包括已禁用的）
     * @return 景点列表
     */
    Result<List<ScenicSpotDTO>> getAllScenicSpotsForAdmin();
    
    /**
     * 保存景点信息
     * @param scenicSpotDTO 景点信息
     * @return 操作结果
     */
    Result<String> saveScenicSpot(ScenicSpotDTO scenicSpotDTO);
    
    /**
     * 删除景点信息
     * @param id 景点ID
     * @return 操作结果
     */
    Result<String> deleteScenicSpot(Long id);
    
    /**
     * 获取所有导览路线（包括已禁用的）
     * @return 导览路线列表
     */
    Result<List<GuideRouteDTO>> getAllGuideRoutesForAdmin();
    
    /**
     * 保存导览路线
     * @param guideRouteDTO 导览路线信息
     * @return 操作结果
     */
    Result<String> saveGuideRoute(GuideRouteDTO guideRouteDTO);
    
    /**
     * 删除导览路线
     * @param id 路线ID
     * @return 操作结果
     */
    Result<String> deleteGuideRoute(Long id);
    
    /**
     * 保存路线节点
     * @param routeNodeDTO 路线节点信息
     * @return 操作结果
     */
    Result<String> saveRouteNode(RouteNodeDTO routeNodeDTO);
    
    /**
     * 删除路线节点
     * @param id 节点ID
     * @return 操作结果
     */
    Result<String> deleteRouteNode(Long id);
}
