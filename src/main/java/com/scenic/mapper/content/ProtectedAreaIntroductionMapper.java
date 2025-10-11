package com.scenic.mapper.content;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.content.ProtectedAreaIntroduction;

@Mapper
public interface ProtectedAreaIntroductionMapper {
    
    /**
     * 根据ID查询保护区介绍
     * @param id 保护区介绍ID
     * @return 保护区介绍信息
     */
    @Select("SELECT * FROM protected_area_introduction WHERE id = #{id}")
    ProtectedAreaIntroduction selectById(Long id);
    
    /**
     * 插入保护区介绍
     * @param introduction 保护区介绍信息
     * @return 插入结果
     */
    @Insert("INSERT INTO protected_area_introduction(title, content, language, image_url, audio_url, video_url, sort_order, enabled, create_time, update_time) " +
            "VALUES(#{title}, #{content}, #{language}, #{imageUrl}, #{audioUrl}, #{videoUrl}, #{sortOrder}, #{enabled}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProtectedAreaIntroduction introduction);
    
    /**
     * 更新保护区介绍信息
     * @param introduction 保护区介绍信息
     * @return 更新结果
     */
    @Update("UPDATE protected_area_introduction SET title = #{title}, content = #{content}, language = #{language}, image_url = #{imageUrl}, " +
            "audio_url = #{audioUrl}, video_url = #{videoUrl}, sort_order = #{sortOrder}, enabled = #{enabled}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(ProtectedAreaIntroduction introduction);
    
    /**
     * 根据ID删除保护区介绍
     * @param id 保护区介绍ID
     * @return 删除结果
     */
    @Delete("DELETE FROM protected_area_introduction WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 查询保护区介绍列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 保护区介绍列表
     */
    @Select("SELECT * FROM protected_area_introduction ORDER BY sort_order ASC, create_time DESC LIMIT #{offset}, #{limit}")
    List<ProtectedAreaIntroduction> selectList(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据语言查询保护区介绍列表
     * @param language 语言
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 保护区介绍列表
     */
    @Select("SELECT * FROM protected_area_introduction WHERE language = #{language} AND enabled = 1 ORDER BY sort_order ASC, create_time DESC LIMIT #{offset}, #{limit}")
    List<ProtectedAreaIntroduction> selectByLanguage(@Param("language") String language, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询保护区介绍总数
     * @return 保护区介绍总数
     */
    @Select("SELECT COUNT(*) FROM protected_area_introduction")
    int selectCount();
    
    /**
     * 根据语言查询保护区介绍总数
     * @param language 语言
     * @return 保护区介绍总数
     */
    @Select("SELECT COUNT(*) FROM protected_area_introduction WHERE language = #{language} AND enabled = 1")
    int selectCountByLanguage(@Param("language") String language);
    
    /**
     * 管理员查询保护区介绍列表
     * @param title 标题（可选）
     * @param language 语言（可选）
     * @param enabled 是否启用（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 保护区介绍列表
     */
    List<ProtectedAreaIntroduction> selectForAdmin(@Param("title") String title, @Param("language") String language, @Param("enabled") Boolean enabled, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 管理员查询保护区介绍总数
     * @param title 标题（可选）
     * @param language 语言（可选）
     * @param enabled 是否启用（可选）
     * @return 保护区介绍总数
     */
    int selectCountForAdmin(@Param("title") String title, @Param("language") String language, @Param("enabled") Boolean enabled);
    
    /**
     * 查询所有启用的保护区介绍
     * @return 保护区介绍列表
     */
    @Select("SELECT * FROM protected_area_introduction WHERE enabled = 1 ORDER BY sort_order ASC, create_time DESC")
    List<ProtectedAreaIntroduction> selectAllEnabled();
}
