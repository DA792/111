package com.scenic.mapper.appointment;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.scenic.entity.appointment.AppointmentPerson;

@Mapper
public interface AppointmentPersonMapper {
    
    /**
     * 根据ID查询预约人
     * @param id 预约人ID
     * @return 预约人信息
     */
    @Select("SELECT * FROM appointment_person WHERE id = #{id}")
    AppointmentPerson selectById(Long id);
    
    /**
     * 根据预约ID查询预约人列表
     * @param appointmentId 预约ID
     * @return 预约人列表
     */
    @Select("SELECT * FROM appointment_person WHERE appointment_id = #{appointmentId}")
    List<AppointmentPerson> selectByAppointmentId(Long appointmentId);
    
    /**
     * 插入预约人
     * @param appointmentPerson 预约人信息
     * @return 插入结果
     */
    @Insert("INSERT INTO appointment_person(appointment_id, name, id_card, phone, age, relationship, create_time, update_time) " +
            "VALUES(#{appointmentId}, #{name}, #{idCard}, #{phone}, #{age}, #{relationship}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AppointmentPerson appointmentPerson);
    
    /**
     * 批量插入预约人
     * @param appointmentPersons 预约人列表
     * @return 插入结果
     */
    @Insert("<script>" +
            "INSERT INTO appointment_person(appointment_id, name, id_card, phone, age, relationship, create_time, update_time) VALUES " +
            "<foreach collection='list' item='person' separator=','>" +
            "(#{person.appointmentId}, #{person.name}, #{person.idCard}, #{person.phone}, #{person.age}, #{person.relationship}, #{person.createTime}, #{person.updateTime})" +
            "</foreach>" +
            "</script>")
    int insertBatch(@Param("list") List<AppointmentPerson> appointmentPersons);
    
    /**
     * 根据ID删除预约人
     * @param id 预约人ID
     * @return 删除结果
     */
    @Delete("DELETE FROM appointment_person WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据预约ID删除预约人
     * @param appointmentId 预约ID
     * @return 删除结果
     */
    @Delete("DELETE FROM appointment_person WHERE appointment_id = #{appointmentId}")
    int deleteByAppointmentId(Long appointmentId);
}
