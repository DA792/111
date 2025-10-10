package com.scenic.service.map.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scenic.common.dto.Result;
import com.scenic.dto.map.GuideRouteDTO;
import com.scenic.dto.map.RouteNodeDTO;
import com.scenic.dto.map.ScenicSpotDTO;
import com.scenic.entity.map.GuideRoute;
import com.scenic.entity.map.RouteNode;
import com.scenic.entity.map.ScenicSpot;
import com.scenic.mapper.map.GuideRouteMapper;
import com.scenic.mapper.map.RouteNodeMapper;
import com.scenic.mapper.map.ScenicSpotMapper;
import com.scenic.service.map.MapService;

/**
 * 地图导览服务实现类
 */
@Service
public class MapServiceImpl implements MapService {
    
    @Autowired
    private ScenicSpotMapper scenicSpotMapper;
    
    @Autowired
    private GuideRouteMapper guideRouteMapper;
    
    @Autowired
    private RouteNodeMapper routeNodeMapper;
    
    /**
     * 获取所有启用的景点信息
     * @return 景点列表
     */
    @Override
    public Result<List<ScenicSpotDTO>> getAllScenicSpots() {
        try {
            List<ScenicSpot> scenicSpots = scenicSpotMapper.selectList(0, 1000);
            // 过滤出启用的景点
            List<ScenicSpot> enabledScenicSpots = scenicSpots.stream()
                    .filter(ScenicSpot::getEnabled)
                    .collect(Collectors.toList());
            
            // 转换为DTO
            List<ScenicSpotDTO> scenicSpotDTOs = enabledScenicSpots.stream()
                    .map(this::convertToScenicSpotDTO)
                    .collect(Collectors.toList());
            
            return Result.success(scenicSpotDTOs);
        } catch (Exception e) {
            return Result.error("获取景点信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据分类获取景点信息
     * @param category 景点分类
     * @return 景点列表
     */
    @Override
    public Result<List<ScenicSpotDTO>> getScenicSpotsByCategory(String category) {
        try {
            List<ScenicSpot> scenicSpots = scenicSpotMapper.selectByCategory(category, 0, 1000);
            // 过滤出启用的景点
            List<ScenicSpot> enabledScenicSpots = scenicSpots.stream()
                    .filter(ScenicSpot::getEnabled)
                    .collect(Collectors.toList());
            
            // 转换为DTO
            List<ScenicSpotDTO> scenicSpotDTOs = enabledScenicSpots.stream()
                    .map(this::convertToScenicSpotDTO)
                    .collect(Collectors.toList());
            
            return Result.success(scenicSpotDTOs);
        } catch (Exception e) {
            return Result.error("根据分类获取景点信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有启用的导览路线
     * @return 导览路线列表
     */
    @Override
    public Result<List<GuideRouteDTO>> getAllGuideRoutes() {
        try {
            List<GuideRoute> guideRoutes = guideRouteMapper.selectList(0, 1000);
            // 过滤出启用的路线
            List<GuideRoute> enabledGuideRoutes = guideRoutes.stream()
                    .filter(GuideRoute::getEnabled)
                    .collect(Collectors.toList());
            
            // 转换为DTO
            List<GuideRouteDTO> guideRouteDTOs = enabledGuideRoutes.stream()
                    .map(this::convertToGuideRouteDTO)
                    .collect(Collectors.toList());
            
            return Result.success(guideRouteDTOs);
        } catch (Exception e) {
            return Result.error("获取导览路线失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据分类获取导览路线
     * @param category 路线分类
     * @return 导览路线列表
     */
    @Override
    public Result<List<GuideRouteDTO>> getGuideRoutesByCategory(String category) {
        try {
            List<GuideRoute> guideRoutes = guideRouteMapper.selectByCategory(category, 0, 1000);
            // 过滤出启用的路线
            List<GuideRoute> enabledGuideRoutes = guideRoutes.stream()
                    .filter(GuideRoute::getEnabled)
                    .collect(Collectors.toList());
            
            // 转换为DTO
            List<GuideRouteDTO> guideRouteDTOs = enabledGuideRoutes.stream()
                    .map(this::convertToGuideRouteDTO)
                    .collect(Collectors.toList());
            
            return Result.success(guideRouteDTOs);
        } catch (Exception e) {
            return Result.error("根据分类获取导览路线失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据路线ID获取路线详情及节点信息
     * @param routeId 路线ID
     * @return 导览路线详情
     */
    @Override
    public Result<GuideRouteDTO> getGuideRouteDetail(Long routeId) {
        try {
            GuideRoute guideRoute = guideRouteMapper.selectById(routeId);
            if (guideRoute == null || !guideRoute.getEnabled()) {
                return Result.error("路线不存在或已禁用");
            }
            
            // 获取路线节点
            List<RouteNode> routeNodes = routeNodeMapper.selectByRouteId(routeId);
            guideRoute.setRouteNodes(routeNodes);
            
            // 转换为DTO
            GuideRouteDTO guideRouteDTO = convertToGuideRouteDTO(guideRoute);
            
            return Result.success(guideRouteDTO);
        } catch (Exception e) {
            return Result.error("获取路线详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有景点信息（包括已禁用的）
     * @return 景点列表
     */
    @Override
    public Result<List<ScenicSpotDTO>> getAllScenicSpotsForAdmin() {
        try {
            List<ScenicSpot> scenicSpots = scenicSpotMapper.selectList(0, 1000);
            
            // 转换为DTO
            List<ScenicSpotDTO> scenicSpotDTOs = scenicSpots.stream()
                    .map(this::convertToScenicSpotDTO)
                    .collect(Collectors.toList());
            
            return Result.success(scenicSpotDTOs);
        } catch (Exception e) {
            return Result.error("获取所有景点信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存景点信息
     * @param scenicSpotDTO 景点信息
     * @return 操作结果
     */
    @Override
    public Result<String> saveScenicSpot(ScenicSpotDTO scenicSpotDTO) {
        try {
            ScenicSpot scenicSpot = convertToScenicSpot(scenicSpotDTO);
            
            if (scenicSpot.getId() == null) {
                // 新增
                scenicSpot.setCreateTime(LocalDateTime.now());
                scenicSpot.setUpdateTime(LocalDateTime.now());
                scenicSpotMapper.insert(scenicSpot);
                return Result.success("景点信息创建成功");
            } else {
                // 更新
                scenicSpot.setUpdateTime(LocalDateTime.now());
                scenicSpotMapper.updateById(scenicSpot);
                return Result.success("景点信息更新成功");
            }
        } catch (Exception e) {
            return Result.error("保存景点信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除景点信息
     * @param id 景点ID
     * @return 操作结果
     */
    @Override
    public Result<String> deleteScenicSpot(Long id) {
        try {
            scenicSpotMapper.deleteById(id);
            return Result.success("景点信息删除成功");
        } catch (Exception e) {
            return Result.error("删除景点信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有导览路线（包括已禁用的）
     * @return 导览路线列表
     */
    @Override
    public Result<List<GuideRouteDTO>> getAllGuideRoutesForAdmin() {
        try {
            List<GuideRoute> guideRoutes = guideRouteMapper.selectList(0, 1000);
            
            // 转换为DTO
            List<GuideRouteDTO> guideRouteDTOs = guideRoutes.stream()
                    .map(this::convertToGuideRouteDTO)
                    .collect(Collectors.toList());
            
            return Result.success(guideRouteDTOs);
        } catch (Exception e) {
            return Result.error("获取所有导览路线失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存导览路线
     * @param guideRouteDTO 导览路线信息
     * @return 操作结果
     */
    @Override
    public Result<String> saveGuideRoute(GuideRouteDTO guideRouteDTO) {
        try {
            GuideRoute guideRoute = convertToGuideRoute(guideRouteDTO);
            
            if (guideRoute.getId() == null) {
                // 新增
                guideRoute.setCreateTime(LocalDateTime.now());
                guideRoute.setUpdateTime(LocalDateTime.now());
                guideRouteMapper.insert(guideRoute);
                return Result.success("导览路线创建成功");
            } else {
                // 更新
                guideRoute.setUpdateTime(LocalDateTime.now());
                guideRouteMapper.updateById(guideRoute);
                return Result.success("导览路线更新成功");
            }
        } catch (Exception e) {
            return Result.error("保存导览路线失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除导览路线
     * @param id 路线ID
     * @return 操作结果
     */
    @Override
    public Result<String> deleteGuideRoute(Long id) {
        try {
            // 先删除关联的路线节点
            routeNodeMapper.deleteByRouteId(id);
            // 再删除路线
            guideRouteMapper.deleteById(id);
            return Result.success("导览路线删除成功");
        } catch (Exception e) {
            return Result.error("删除导览路线失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存路线节点
     * @param routeNodeDTO 路线节点信息
     * @return 操作结果
     */
    @Override
    public Result<String> saveRouteNode(RouteNodeDTO routeNodeDTO) {
        try {
            RouteNode routeNode = convertToRouteNode(routeNodeDTO);
            
            if (routeNode.getId() == null) {
                // 新增
                routeNode.setCreateTime(LocalDateTime.now());
                routeNode.setUpdateTime(LocalDateTime.now());
                routeNodeMapper.insert(routeNode);
                return Result.success("路线节点创建成功");
            } else {
                // 更新
                routeNode.setUpdateTime(LocalDateTime.now());
                routeNodeMapper.updateById(routeNode);
                return Result.success("路线节点更新成功");
            }
        } catch (Exception e) {
            return Result.error("保存路线节点失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除路线节点
     * @param id 节点ID
     * @return 操作结果
     */
    @Override
    public Result<String> deleteRouteNode(Long id) {
        try {
            routeNodeMapper.deleteById(id);
            return Result.success("路线节点删除成功");
        } catch (Exception e) {
            return Result.error("删除路线节点失败: " + e.getMessage());
        }
    }
    
    /**
     * 将ScenicSpot转换为ScenicSpotDTO
     * @param scenicSpot 景点实体
     * @return 景点DTO
     */
    private ScenicSpotDTO convertToScenicSpotDTO(ScenicSpot scenicSpot) {
        ScenicSpotDTO dto = new ScenicSpotDTO();
        BeanUtils.copyProperties(scenicSpot, dto);
        return dto;
    }
    
    /**
     * 将ScenicSpotDTO转换为ScenicSpot
     * @param scenicSpotDTO 景点DTO
     * @return 景点实体
     */
    private ScenicSpot convertToScenicSpot(ScenicSpotDTO scenicSpotDTO) {
        ScenicSpot scenicSpot = new ScenicSpot();
        BeanUtils.copyProperties(scenicSpotDTO, scenicSpot);
        return scenicSpot;
    }
    
    /**
     * 将GuideRoute转换为GuideRouteDTO
     * @param guideRoute 导览路线实体
     * @return 导览路线DTO
     */
    private GuideRouteDTO convertToGuideRouteDTO(GuideRoute guideRoute) {
        GuideRouteDTO dto = new GuideRouteDTO();
        BeanUtils.copyProperties(guideRoute, dto);
        
        // 转换路线节点
        if (guideRoute.getRouteNodes() != null) {
            List<RouteNodeDTO> routeNodeDTOs = guideRoute.getRouteNodes().stream()
                    .map(this::convertToRouteNodeDTO)
                    .collect(Collectors.toList());
            dto.setRouteNodes(routeNodeDTOs);
        }
        
        return dto;
    }
    
    /**
     * 将GuideRouteDTO转换为GuideRoute
     * @param guideRouteDTO 导览路线DTO
     * @return 导览路线实体
     */
    private GuideRoute convertToGuideRoute(GuideRouteDTO guideRouteDTO) {
        GuideRoute guideRoute = new GuideRoute();
        BeanUtils.copyProperties(guideRouteDTO, guideRoute);
        return guideRoute;
    }
    
    /**
     * 将RouteNode转换为RouteNodeDTO
     * @param routeNode 路线节点实体
     * @return 路线节点DTO
     */
    private RouteNodeDTO convertToRouteNodeDTO(RouteNode routeNode) {
        RouteNodeDTO dto = new RouteNodeDTO();
        BeanUtils.copyProperties(routeNode, dto);
        return dto;
    }
    
    /**
     * 将RouteNodeDTO转换为RouteNode
     * @param routeNodeDTO 路线节点DTO
     * @return 路线节点实体
     */
    private RouteNode convertToRouteNode(RouteNodeDTO routeNodeDTO) {
        RouteNode routeNode = new RouteNode();
        BeanUtils.copyProperties(routeNodeDTO, routeNode);
        return routeNode;
    }
}
