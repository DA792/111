package com.scenic.mapper.appointment;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.appointment.AppointmentSetting;

@Mapper
public interface AppointmentSettingMapper {
    
    /**
     * 根据ID查询预约设置
     * @param id 预约设置ID
     * @return 预约设置信息
     */
    @Select("SELECT * FROM appointment_setting WHERE id = #{id}")
    AppointmentSetting selectById(Long id);
    
    /**
     * 根据设置键查询预约设置
     * @param settingKey 设置键
     * @return 预约设置信息
     */
    @Select("SELECT * FROM appointment_setting WHERE setting_key = #{settingKey}")
    AppointmentSetting selectBySettingKey(String settingKey);
    
    /**
     * 插入预约设置
     * @param appointmentSetting 预约设置信息
     * @return 插入结果
     */
    @Insert("INSERT INTO appointment_setting(setting_key, setting_value, description, enabled, create_time, update_time) " +
            "VALUES(#{settingKey}, #{settingValue}, #{description}, #{enabled}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AppointmentSetting appointmentSetting);
    
    /**
     * 更新预约设置信息
     * @param appointmentSetting 预约设置信息
     * @return 更新结果
     */
    @Update("UPDATE appointment_setting SET setting_key = #{settingKey}, setting_value = #{settingValue}, " +
            "description = #{description}, enabled = #{enabled}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(AppointmentSetting appointmentSetting);
    
    /**
     * 根据ID删除预约设置
     * @param id 预约设置ID
     * @return 删除结果
     */
    @Delete("DELETE FROM appointment_setting WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 查询所有预约设置
     * @return 预约设置列表
     */
    @Select("SELECT * FROM appointment_setting")
    List<AppointmentSetting> selectAll();
    
    /**
     * 根据启用状态查询预约设置
     * @param enabled 启用状态
     * @return 预约设置列表
     */
    @Select("SELECT * FROM appointment_setting WHERE enabled = #{enabled}")
    List<AppointmentSetting> selectByEnabled(Boolean enabled);
}
