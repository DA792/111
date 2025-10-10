package com.scenic.mapper.interaction;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.interaction.ArContent;

@Mapper
public interface ArContentMapper {
    
    /**
     * 根据ID查询AR内容
     * @param id AR内容ID
     * @return AR内容信息
     */
    @Select("SELECT * FROM ar_content WHERE id = #{id}")
    ArContent selectById(Long id);
    
    /**
     * 插入AR内容
     * @param arContent AR内容信息
     * @return 插入结果
     */
    @Insert("INSERT INTO ar_content(title, description, content_url, content_type, target_id, target_type, latitude, longitude, enabled, create_time, update_time) " +
            "VALUES(#{title}, #{description}, #{contentUrl}, #{contentType}, #{targetId}, #{targetType}, #{latitude}, #{longitude}, #{enabled}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ArContent arContent);
    
    /**
     * 更新AR内容信息
     * @param arContent AR内容信息
     * @return 更新结果
     */
    @Update("UPDATE ar_content SET title = #{title}, description = #{description}, content_url = #{contentUrl}, content_type = #{contentType}, " +
            "target_id = #{targetId}, target_type = #{targetType}, latitude = #{latitude}, longitude = #{longitude}, enabled = #{enabled}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(ArContent arContent);
    
    /**
     * 根据ID删除AR内容
     * @param id AR内容ID
     * @return 删除结果
     */
    @Delete("DELETE FROM ar_content WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据目标ID和类型查询AR内容列表
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return AR内容列表
     */
    @Select("SELECT * FROM ar_content WHERE target_id = #{targetId} AND target_type = #{targetType} AND enabled = 1 ORDER BY create_time DESC")
    List<ArContent> selectByTarget(@Param("targetId") String targetId, @Param("targetType") String targetType);
    
    /**
     * 根据内容类型查询AR内容列表
     * @param contentType 内容类型
     * @param offset 偏移量
     * @param limit 限制数量
     * @return AR内容列表
     */
    @Select("SELECT * FROM ar_content WHERE content_type = #{contentType} AND enabled = 1 ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<ArContent> selectByContentType(@Param("contentType") String contentType, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询AR内容列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @return AR内容列表
     */
    @Select("SELECT * FROM ar_content ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<ArContent> selectList(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询AR内容总数
     * @return AR内容总数
     */
    @Select("SELECT COUNT(*) FROM ar_content")
    int selectCount();
    
    /**
     * 管理员查询AR内容列表
     * @param title 标题（可选）
     * @param contentType 内容类型（可选）
     * @param enabled 是否启用（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return AR内容列表
     */
    List<ArContent> selectForAdmin(@Param("title") String title, @Param("contentType") String contentType, @Param("enabled") Boolean enabled, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 管理员查询AR内容总数
     * @param title 标题（可选）
     * @param contentType 内容类型（可选）
     * @param enabled 是否启用（可选）
     * @return AR内容总数
     */
    int selectCountForAdmin(@Param("title") String title, @Param("contentType") String contentType, @Param("enabled") Boolean enabled);
    
    /**
     * 查询所有启用的AR内容
     * @return AR内容列表
     */
    @Select("SELECT * FROM ar_content WHERE enabled = 1 ORDER BY create_time DESC")
    List<ArContent> selectAllEnabled();
}
