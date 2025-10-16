package com.scenic.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.MainConfig;

@Mapper
public interface MainConfigMapper {
    
    /**
     * 根据配置名称查询配置
     * @param configName 配置名称
     * @return 配置信息
     */
    @Select("SELECT * FROM main_config WHERE config_name = #{configName}")
    MainConfig selectByConfigName(String configName);
    
    /**
     * 插入配置
     * @param mainConfig 配置信息
     * @return 插入结果
     */
    @Insert("INSERT INTO main_config(config_name, config_json, create_time, update_time, create_by, update_by) " +
            "VALUES(#{configName}, #{configJson}, #{createTime}, #{updateTime}, #{createBy}, #{updateBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MainConfig mainConfig);
    
    /**
     * 更新配置信息
     * @param mainConfig 配置信息
     * @return 更新结果
     */
    @Update("UPDATE main_config SET config_json = #{configJson}, update_time = #{updateTime}, update_by = #{updateBy} " +
            "WHERE config_name = #{configName}")
    int updateByConfigName(MainConfig mainConfig);
    
    /**
     * 根据配置名称删除配置
     * @param configName 配置名称
     * @return 删除结果
     */
    @Delete("DELETE FROM main_config WHERE config_name = #{configName}")
    int deleteByConfigName(String configName);
    
    /**
     * 查询所有配置
     * @return 配置列表
     */
    @Select("SELECT * FROM main_config")
    List<MainConfig> selectAll();
}
