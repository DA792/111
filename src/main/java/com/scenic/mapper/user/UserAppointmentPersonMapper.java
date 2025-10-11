package com.scenic.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.user.UserAppointmentPerson;

@Mapper
public interface UserAppointmentPersonMapper {
    
    /**
     * 根据ID查询用户预约人
     * @param id 预约人ID
     * @return 预约人信息
     */
    @Select("SELECT * FROM user_appointment_person WHERE id = #{id}")
    UserAppointmentPerson selectById(Long id);
    
    /**
     * 根据用户ID查询预约人列表
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 预约人列表
     */
    @Select("SELECT * FROM user_appointment_person WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<UserAppointmentPerson> selectByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 插入用户预约人
     * @param userAppointmentPerson 预约人信息
     * @return 插入结果
     */
    @Insert("INSERT INTO user_appointment_person(user_id, name, id_card, phone, age, relationship, is_default, create_time, update_time) " +
            "VALUES(#{userId}, #{name}, #{idCard}, #{phone}, #{age}, #{relationship}, #{isDefault}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserAppointmentPerson userAppointmentPerson);
    
    /**
     * 更新用户预约人信息
     * @param userAppointmentPerson 预约人信息
     * @return 更新结果
     */
    @Update("UPDATE user_appointment_person SET name = #{name}, id_card = #{idCard}, phone = #{phone}, " +
            "age = #{age}, relationship = #{relationship}, is_default = #{isDefault}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(UserAppointmentPerson userAppointmentPerson);
    
    /**
     * 根据ID删除用户预约人
     * @param id 预约人ID
     * @return 删除结果
     */
    @Delete("DELETE FROM user_appointment_person WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据用户ID删除预约人
     * @param userId 用户ID
     * @return 删除结果
     */
    @Delete("DELETE FROM user_appointment_person WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
    
    /**
     * 查询预约人总数
     * @param userId 用户ID
     * @return 预约人总数
     */
    @Select("SELECT COUNT(*) FROM user_appointment_person WHERE user_id = #{userId}")
    int selectCountByUserId(Long userId);
}
