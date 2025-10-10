package com.scenic.mapper.map;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.map.ScenicSpot;

@Mapper
public interface ScenicSpotMapper {
    
    /**
     * 根据ID查询景点
     * @param id 景点ID
     * @return 景点信息
     */
    @Select("SELECT * FROM scenic_spot WHERE id = #{id}")
    ScenicSpot selectById(Long id);
    
    /**
     * 插入景点
     * @param scenicSpot 景点信息
     * @return 插入结果
     */
    @Insert("INSERT INTO scenic_spot(name, description, location, latitude, longitude, image_url, recommended_visit_time, category, enabled, create_time, update_time) " +
            "VALUES(#{name}, #{description}, #{location}, #{latitude}, #{longitude}, #{imageUrl}, #{recommendedVisitTime}, #{category}, #{enabled}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ScenicSpot scenicSpot);
    
    /**
     * 更新景点信息
     * @param scenicSpot 景点信息
     * @return 更新结果
     */
    @Update("UPDATE scenic_spot SET name = #{name}, description = #{description}, location = #{location}, latitude = #{latitude}, longitude = #{longitude}, " +
            "image_url = #{imageUrl}, recommended_visit_time = #{recommendedVisitTime}, category = #{category}, enabled = #{enabled}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(ScenicSpot scenicSpot);
    
    /**
     * 根据ID删除景点
     * @param id 景点ID
     * @return 删除结果
     */
    @Delete("DELETE FROM scenic_spot WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据分类查询景点列表
     * @param category 分类
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 景点列表
     */
    @Select("SELECT * FROM scenic_spot WHERE category = #{category} AND enabled = 1 ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<ScenicSpot> selectByCategory(@Param("category") String category, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询景点列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 景点列表
     */
    @Select("SELECT * FROM scenic_spot WHERE enabled = 1 ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<ScenicSpot> selectList(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询景点总数
     * @return 景点总数
     */
    @Select("SELECT COUNT(*) FROM scenic_spot WHERE enabled = 1")
    int selectCount();
    
    /**
     * 根据分类查询景点总数
     * @param category 分类
     * @return 景点总数
     */
    @Select("SELECT COUNT(*) FROM scenic_spot WHERE category = #{category} AND enabled = 1")
    int selectCountByCategory(@Param("category") String category);
    
    /**
     * 管理员查询景点列表
     * @param name 名称（可选）
     * @param category 分类（可选）
     * @param enabled 是否启用（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 景点列表
     */
    List<ScenicSpot> selectForAdmin(@Param("name") String name, @Param("category") String category, @Param("enabled") Boolean enabled, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 管理员查询景点总数
     * @param name 名称（可选）
     * @param category 分类（可选）
     * @param enabled 是否启用（可选）
     * @return 景点总数
     */
    int selectCountForAdmin(@Param("name") String name, @Param("category") String category, @Param("enabled") Boolean enabled);
}
