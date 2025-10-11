package com.scenic.mapper.map;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.map.RouteNode;

@Mapper
public interface RouteNodeMapper {
    
    /**
     * 根据ID查询路线节点
     * @param id 路线节点ID
     * @return 路线节点信息
     */
    @Select("SELECT * FROM route_node WHERE id = #{id}")
    RouteNode selectById(Long id);
    
    /**
     * 根据路线ID查询路线节点列表
     * @param routeId 路线ID
     * @return 路线节点列表
     */
    @Select("SELECT * FROM route_node WHERE route_id = #{routeId} AND enabled = 1 ORDER BY order_num ASC")
    List<RouteNode> selectByRouteId(Long routeId);
    
    /**
     * 插入路线节点
     * @param routeNode 路线节点信息
     * @return 插入结果
     */
    @Insert("INSERT INTO route_node(route_id, name, description, latitude, longitude, image_url, order_num, node_type, enabled, create_time, update_time) " +
            "VALUES(#{routeId}, #{name}, #{description}, #{latitude}, #{longitude}, #{imageUrl}, #{orderNum}, #{nodeType}, #{enabled}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RouteNode routeNode);
    
    /**
     * 更新路线节点信息
     * @param routeNode 路线节点信息
     * @return 更新结果
     */
    @Update("UPDATE route_node SET name = #{name}, description = #{description}, latitude = #{latitude}, longitude = #{longitude}, image_url = #{imageUrl}, " +
            "order_num = #{orderNum}, node_type = #{nodeType}, enabled = #{enabled}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(RouteNode routeNode);
    
    /**
     * 根据ID删除路线节点
     * @param id 路线节点ID
     * @return 删除结果
     */
    @Delete("DELETE FROM route_node WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据路线ID删除路线节点
     * @param routeId 路线ID
     * @return 删除结果
     */
    @Delete("DELETE FROM route_node WHERE route_id = #{routeId}")
    int deleteByRouteId(Long routeId);
    
    /**
     * 查询路线节点列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 路线节点列表
     */
    @Select("SELECT * FROM route_node ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<RouteNode> selectList(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询路线节点总数
     * @return 路线节点总数
     */
    @Select("SELECT COUNT(*) FROM route_node")
    int selectCount();
    
    /**
     * 管理员查询路线节点列表
     * @param name 名称（可选）
     * @param nodeType 节点类型（可选）
     * @param enabled 是否启用（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 路线节点列表
     */
    List<RouteNode> selectForAdmin(@Param("name") String name, @Param("nodeType") String nodeType, @Param("enabled") Boolean enabled, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 管理员查询路线节点总数
     * @param name 名称（可选）
     * @param nodeType 节点类型（可选）
     * @param enabled 是否启用（可选）
     * @return 路线节点总数
     */
    int selectCountForAdmin(@Param("name") String name, @Param("nodeType") String nodeType, @Param("enabled") Boolean enabled);
}
