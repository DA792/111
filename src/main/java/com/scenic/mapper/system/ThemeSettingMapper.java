package com.scenic.mapper.system;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.system.ThemeSetting;

/**
 * 主题设置Mapper接口
 */
@Mapper
public interface ThemeSettingMapper {
    
    /**
     * 根据ID查询主题设置
     * @param id 主题设置ID
     * @return 主题设置实体
     */
    @Select("SELECT * FROM theme_setting WHERE id = #{id}")
    ThemeSetting selectById(Long id);
    
    /**
     * 查询所有主题设置
     * @return 主题设置列表
     */
    @Select("SELECT * FROM theme_setting ORDER BY create_time DESC")
    List<ThemeSetting> selectAll();
    
    /**
     * 查询默认主题设置
     * @return 默认主题设置
     */
    @Select("SELECT * FROM theme_setting WHERE is_default = 1")
    ThemeSetting selectDefault();
    
    /**
     * 根据主题名称查询主题设置
     * @param themeName 主题名称
     * @return 主题设置实体
     */
    @Select("SELECT * FROM theme_setting WHERE theme_name = #{themeName}")
    ThemeSetting selectByThemeName(String themeName);
    
    /**
     * 插入主题设置
     * @param themeSetting 主题设置实体
     * @return 影响行数
     */
    @Insert("INSERT INTO theme_setting(theme_name, color_config, is_default, create_time, update_time) " +
            "VALUES(#{themeName}, #{colorConfig}, #{isDefault}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ThemeSetting themeSetting);
    
    /**
     * 更新主题设置
     * @param themeSetting 主题设置实体
     * @return 影响行数
     */
    int update(ThemeSetting themeSetting);
    
    /**
     * 根据ID删除主题设置
     * @param id 主题设置ID
     * @return 影响行数
     */
    @Delete("DELETE FROM theme_setting WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 设置默认主题
     * @param id 主题设置ID
     * @return 影响行数
     */
    @Update("UPDATE theme_setting SET is_default = CASE WHEN id = #{id} THEN 1 ELSE 0 END")
    int setDefault(Long id);
}
