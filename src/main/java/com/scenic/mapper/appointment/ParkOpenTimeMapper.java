package com.scenic.mapper.appointment;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.appointment.ParkOpenTime;

/**
 * 公园开放时间Mapper接口
 */
@Mapper
public interface ParkOpenTimeMapper {
    
    /**
     * 插入或更新开放时间配置
     * @param openTime 开放时间配置
     * @return 影响行数
     */
    @Insert("INSERT INTO park_open_time_config(config_date, is_closed, day_type, create_time, update_time, create_by, update_by) " +
            "VALUES(#{configDate}, #{isClosed}, #{dayType}, #{createTime}, #{updateTime}, #{createBy}, #{updateBy}) " +
            "ON DUPLICATE KEY UPDATE " +
            "is_closed = VALUES(is_closed), day_type = VALUES(day_type), update_time = VALUES(update_time), update_by = VALUES(update_by)")
    int insertOrUpdate(ParkOpenTime openTime);
    
    /**
     * 根据ID更新开放时间配置
     * @param openTime 开放时间配置
     * @return 影响行数
     */
    @Update("UPDATE park_open_time_config SET " +
            "config_date = #{configDate}, is_closed = #{isClosed}, day_type = #{dayType}, " +
            "update_time = #{updateTime}, update_by = #{updateBy} " +
            "WHERE id = #{id}")
    int updateById(ParkOpenTime openTime);
    
    /**
     * 根据ID删除开放时间配置
     * @param id 开放时间配置ID
     * @return 影响行数
     */
    @Delete("DELETE FROM park_open_time_config WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据日期查询开放时间配置
     * @param configDate 配置日期
     * @return 开放时间配置
     */
    @Select("SELECT id, config_date, is_closed, day_type, create_time, update_time, create_by, update_by " +
            "FROM park_open_time_config WHERE config_date = #{configDate}")
    ParkOpenTime selectByDate(@Param("configDate") LocalDate configDate);
    
    /**
     * 根据日期范围查询开放时间配置
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 开放时间配置列表
     */
    @Select("SELECT id, config_date, is_closed, day_type, create_time, update_time, create_by, update_by " +
            "FROM park_open_time_config " +
            "WHERE config_date >= #{startDate} AND config_date <= #{endDate} " +
            "ORDER BY config_date")
    List<ParkOpenTime> selectByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * 查询所有开放时间配置
     * @return 开放时间配置列表
     */
    @Select("SELECT id, config_date, is_closed, day_type, create_time, update_time, create_by, update_by " +
            "FROM park_open_time_config ORDER BY config_date")
    List<ParkOpenTime> selectAll();
}
