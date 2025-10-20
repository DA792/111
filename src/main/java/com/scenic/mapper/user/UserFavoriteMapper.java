package com.scenic.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.user.UserFavorite;

@Mapper
public interface UserFavoriteMapper {
    
    /**
     * 根据ID查询用户收藏
     * @param id 收藏ID
     * @return 收藏信息
     */
    @Select("SELECT * FROM user_favorite WHERE id = #{id}")
    UserFavorite selectById(Long id);
    
    /**
     * 根据用户ID和内容ID查询收藏记录
     * @param userId 用户ID
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 收藏信息
     */
    @Select("SELECT * FROM user_favorite WHERE user_id = #{userId} AND content_id = #{contentId} AND content_type = #{contentType}")
    UserFavorite selectByUserAndContent(@Param("userId") Long userId, @Param("contentId") Long contentId, @Param("contentType") Integer contentType);
    
    /**
     * 根据用户ID查询收藏列表（分页）
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 收藏列表
     */
    @Select("SELECT * FROM user_favorite WHERE user_id = #{userId} AND content_type = #{contentType} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<UserFavorite> selectByUserId(@Param("userId") Long userId, @Param("contentType") Integer contentType, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据用户ID查询收藏总数
     * @param userId 用户ID
     * @param contentType 内容类型
     * @return 收藏总数
     */
    @Select("SELECT COUNT(*) FROM user_favorite WHERE user_id = #{userId} AND content_type = #{contentType}")
    int selectCountByUserId(@Param("userId") Long userId, @Param("contentType") Integer contentType);
    
    /**
     * 批量查询用户对多个内容的互动状态
     * @param userId 用户ID
     * @param contentIds 内容ID列表
     * @return 互动记录列表
     */
    @Select({
        "<script>",
        "SELECT * FROM user_favorite WHERE user_id = #{userId} AND content_id IN",
        "<foreach item='id' collection='contentIds' open='(' separator=',' close=')'>",
        "#{id}",
        "</foreach>",
        "</script>"
    })
    List<UserFavorite> selectByUserAndContentIds(@Param("userId") Long userId, @Param("contentIds") List<Long> contentIds);
    
    /**
     * 插入用户收藏
     * @param userFavorite 收藏信息
     * @return 插入结果
     */
    @Insert("INSERT INTO user_favorite(user_id, content_id, content_type, version, create_time, update_time, create_by, update_by, category_id) " +
            "VALUES(#{userId}, #{contentId}, #{contentType}, #{version}, #{createTime}, #{updateTime}, #{createBy}, #{updateBy}, #{categoryId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserFavorite userFavorite);
    
    /**
     * 根据ID删除用户收藏（硬删除）
     * @param id 收藏ID
     * @return 删除结果
     */
    @Delete("DELETE FROM user_favorite WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据用户ID和内容ID删除收藏记录（硬删除）
     * @param userId 用户ID
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 删除结果
     */
    @Delete("DELETE FROM user_favorite WHERE user_id = #{userId} AND content_id = #{contentId} AND content_type = #{contentType}")
    int deleteByUserAndContent(@Param("userId") Long userId, @Param("contentId") Long contentId, @Param("contentType") Integer contentType);
    
    /**
     * 根据用户ID删除所有收藏
     * @param userId 用户ID
     * @return 删除结果
     */
    @Delete("DELETE FROM user_favorite WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
}