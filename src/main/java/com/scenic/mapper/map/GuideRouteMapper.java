package com.scenic.mapper.map;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.map.GuideRoute;

@Mapper
public interface GuideRouteMapper {
    
    /**
     * 根据ID查询导览路线
     * @param id 导览路线ID
     * @return 导览路线信息
     */
    @Select("SELECT * FROM guide_route WHERE id = #{id}")
    GuideRoute selectById(Long id);
    
    /**
     * 插入导览路线
     * @param guideRoute 导览路线信息
     * @return 插入结果
     */
    @Insert("INSERT INTO guide_route(name, description, total_distance, estimated_time, category, enabled, create_time, update_time) " +
            "VALUES(#{name}, #{description}, #{totalDistance}, #{estimatedTime}, #{category}, #{enabled}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(GuideRoute guideRoute);
    
    /**
     * 更新导览路线信息
     * @param guideRoute 导览路线信息
     * @return 更新结果
     */
    @Update("UPDATE guide_route SET name = #{name}, description = #{description}, total_distance = #{totalDistance}, estimated_time = #{estimatedTime}, " +
            "category = #{category}, enabled = #{enabled}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(GuideRoute guideRoute);
    
    /**
     * 根据ID删除导览路线
     * @param id 导览路线ID
     * @return 删除结果
     */
    @Delete("DELETE FROM guide_route WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据分类查询导览路线列表
     * @param category 分类
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 导览路线列表
     */
    @Select("SELECT * FROM guide_route WHERE category = #{category} AND enabled = 1 ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<GuideRoute> selectByCategory(@Param("category") String category, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询导览路线列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 导览路线列表
     */
    @Select("SELECT * FROM guide_route WHERE enabled = 1 ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<GuideRoute> selectList(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询导览路线总数
     * @return 导览路线总数
     */
    @Select("SELECT COUNT(*) FROM guide_route WHERE enabled = 1")
    int selectCount();
    
    /**
     * 根据分类查询导览路线总数
     * @param category 分类
     * @return 导览路线总数
     */
    @Select("SELECT COUNT(*) FROM guide_route WHERE category = #{category} AND enabled = 1")
    int selectCountByCategory(@Param("category") String category);
    
    /**
     * 管理员查询导览路线列表
     * @param name 名称（可选）
     * @param category 分类（可选）
     * @param enabled 是否启用（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 导览路线列表
     */
    List<GuideRoute> selectForAdmin(@Param("name") String name, @Param("category") String category, @Param("enabled") Boolean enabled, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 管理员查询导览路线总数
     * @param name 名称（可选）
     * @param category 分类（可选）
     * @param enabled 是否启用（可选）
     * @return 导览路线总数
     */
    int selectCountForAdmin(@Param("name") String name, @Param("category") String category, @Param("enabled") Boolean enabled);
}
