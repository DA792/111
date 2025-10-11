package com.scenic.controller.map;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scenic.common.dto.Result;
import com.scenic.dto.map.GuideRouteDTO;
import com.scenic.dto.map.RouteNodeDTO;
import com.scenic.dto.map.ScenicSpotDTO;
import com.scenic.service.map.MapService;

/**
 * 地图导览控制器
 * 提供小程序端和管理后台端的API接口
 */
@RestController
@RequestMapping("/api")
public class MapController {
    
    // 小程序端API接口前缀
    private static final String MINIAPP_PREFIX = "/uniapp";
    
    // 管理后台端API接口前缀
    private static final String ADMIN_PREFIX = "/manage";
    
    @Autowired
    private MapService mapService;
    
    /**
     * 小程序端 - 获取所有启用的景点信息
     * @return 景点列表
     */
    @GetMapping(MINIAPP_PREFIX + "/map/scenic-spots")
    public Result<List<ScenicSpotDTO>> getAllScenicSpotsForMiniapp() {
        return mapService.getAllScenicSpots();
    }
    
    /**
     * 小程序端 - 根据分类获取景点信息
     * @param category 景点分类
     * @return 景点列表
     */
    @GetMapping(MINIAPP_PREFIX + "/map/scenic-spots/category")
    public Result<List<ScenicSpotDTO>> getScenicSpotsByCategory(@RequestParam String category) {
        return mapService.getScenicSpotsByCategory(category);
    }
    
    /**
     * 小程序端 - 获取所有启用的导览路线
     * @return 导览路线列表
     */
    @GetMapping(MINIAPP_PREFIX + "/map/guide-routes")
    public Result<List<GuideRouteDTO>> getAllGuideRoutesForMiniapp() {
        return mapService.getAllGuideRoutes();
    }
    
    /**
     * 小程序端 - 根据分类获取导览路线
     * @param category 路线分类
     * @return 导览路线列表
     */
    @GetMapping(MINIAPP_PREFIX + "/map/guide-routes/category")
    public Result<List<GuideRouteDTO>> getGuideRoutesByCategory(@RequestParam String category) {
        return mapService.getGuideRoutesByCategory(category);
    }
    
    /**
     * 小程序端 - 根据路线ID获取路线详情及节点信息
     * @param routeId 路线ID
     * @return 导览路线详情
     */
    @GetMapping(MINIAPP_PREFIX + "/map/guide-routes/{routeId}")
    public Result<GuideRouteDTO> getGuideRouteDetail(@PathVariable Long routeId) {
        return mapService.getGuideRouteDetail(routeId);
    }
    
    /**
     * 管理后台端 - 获取所有景点信息（包括已禁用的）
     * @return 景点列表
     */
    @GetMapping(ADMIN_PREFIX + "/map/scenic-spots")
    public Result<List<ScenicSpotDTO>> getAllScenicSpotsForAdmin() {
        // 管理端需要显示所有景点，包括已禁用的
        return mapService.getAllScenicSpotsForAdmin();
    }
    
    /**
     * 管理后台端 - 创建或更新景点信息
     * @param scenicSpotDTO 景点信息
     * @return 操作结果
     */
    @PostMapping(ADMIN_PREFIX + "/map/scenic-spots")
    public Result<String> createOrUpdateScenicSpot(@RequestBody ScenicSpotDTO scenicSpotDTO) {
        return mapService.saveScenicSpot(scenicSpotDTO);
    }
    
    /**
     * 管理后台端 - 删除景点信息
     * @param id 景点ID
     * @return 操作结果
     */
    @DeleteMapping(ADMIN_PREFIX + "/map/scenic-spots/{id}")
    public Result<String> deleteScenicSpot(@PathVariable Long id) {
        return mapService.deleteScenicSpot(id);
    }
    
    /**
     * 管理后台端 - 获取所有导览路线（包括已禁用的）
     * @return 导览路线列表
     */
    @GetMapping(ADMIN_PREFIX + "/map/guide-routes")
    public Result<List<GuideRouteDTO>> getAllGuideRoutesForAdmin() {
        // 管理端需要显示所有路线，包括已禁用的
        return mapService.getAllGuideRoutesForAdmin();
    }
    
    /**
     * 管理后台端 - 创建或更新导览路线
     * @param guideRouteDTO 导览路线信息
     * @return 操作结果
     */
    @PostMapping(ADMIN_PREFIX + "/map/guide-routes")
    public Result<String> createOrUpdateGuideRoute(@RequestBody GuideRouteDTO guideRouteDTO) {
        return mapService.saveGuideRoute(guideRouteDTO);
    }
    
    /**
     * 管理后台端 - 删除导览路线
     * @param id 路线ID
     * @return 操作结果
     */
    @DeleteMapping(ADMIN_PREFIX + "/map/guide-routes/{id}")
    public Result<String> deleteGuideRoute(@PathVariable Long id) {
        return mapService.deleteGuideRoute(id);
    }
    
    /**
     * 管理后台端 - 创建或更新路线节点
     * @param routeNodeDTO 路线节点信息
     * @return 操作结果
     */
    @PostMapping(ADMIN_PREFIX + "/map/route-nodes")
    public Result<String> createOrUpdateRouteNode(@RequestBody RouteNodeDTO routeNodeDTO) {
        return mapService.saveRouteNode(routeNodeDTO);
    }
    
    /**
     * 管理后台端 - 删除路线节点
     * @param id 节点ID
     * @return 操作结果
     */
    @DeleteMapping(ADMIN_PREFIX + "/map/route-nodes/{id}")
    public Result<String> deleteRouteNode(@PathVariable Long id) {
        return mapService.deleteRouteNode(id);
    }
}
