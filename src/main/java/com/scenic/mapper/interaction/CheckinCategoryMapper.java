package com.scenic.mapper.interaction;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.interaction.CheckinCategory;

@Mapper
public interface CheckinCategoryMapper {
    
    /**
     * 查询所有启用的打卡分类
     * @return 打卡分类列表
     */
    @Select("SELECT id, name FROM checkin_category WHERE status = 1 ORDER BY sort_order ASC")
    List<CheckinCategory> selectAllEnabled();
    
    /**
     * 根据ID查询打卡分类
     * @param id 分类ID
     * @return 打卡分类
     */
    @Select("SELECT * FROM checkin_category WHERE id = #{id}")
    CheckinCategory selectById(Long id);
    
    /**
     * 根据名称查询启用的打卡分类
     * @param name 分类名称
     * @return 打卡分类
     */
    @Select("SELECT * FROM checkin_category WHERE name = #{name} AND status = 1")
    CheckinCategory selectByName(String name);
    
    /**
     * 更新打卡分类状态
     * @param id 分类ID
     * @param status 状态
     * @return 更新结果
     */
    @Update("UPDATE checkin_category SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(Long id, Integer status);
    
    /**
     * 插入打卡分类
     * @param category 打卡分类
     * @return 插入结果
     */
    @Insert("INSERT INTO checkin_category(id, name, description, sort_order, status, version, create_time, update_time, create_by, update_by) " +
            "VALUES(#{id}, #{name}, #{description}, #{sortOrder}, #{status}, #{version}, #{createTime}, #{updateTime}, #{createBy}, #{updateBy})")
    int insert(CheckinCategory category);
}