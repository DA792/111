package com.scenic.mapper.appointment;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.appointment.Appointment;

@Mapper
public interface AppointmentMapper {
    
    /**
     * 根据ID查询预约
     * @param id 预约ID
     * @return 预约信息
     */
    @Select("SELECT * FROM appointment WHERE id = #{id}")
    Appointment selectById(Long id);
    
    /**
     * 插入预约
     * @param appointment 预约信息
     * @return 插入结果
     */
    @Insert("INSERT INTO appointment(user_id, user_name, user_phone, scenic_spot_id, scenic_spot_name, " +
            "appointment_time, number_of_people, status, remark, create_time, update_time) " +
            "VALUES(#{userId}, #{userName}, #{userPhone}, #{scenicSpotId}, #{scenicSpotName}, " +
            "#{appointmentTime}, #{numberOfPeople}, #{status}, #{remark}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Appointment appointment);
    
    /**
     * 更新预约信息
     * @param appointment 预约信息
     * @return 更新结果
     */
    @Update("UPDATE appointment SET user_name = #{userName}, user_phone = #{userPhone}, " +
            "scenic_spot_id = #{scenicSpotId}, scenic_spot_name = #{scenicSpotName}, " +
            "appointment_time = #{appointmentTime}, number_of_people = #{numberOfPeople}, " +
            "status = #{status}, remark = #{remark}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(Appointment appointment);
    
    /**
     * 根据ID删除预约
     * @param id 预约ID
     * @return 删除结果
     */
    @Delete("DELETE FROM appointment WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 查询用户预约列表
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 预约列表
     */
    @Select("SELECT * FROM appointment WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Appointment> selectByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询预约总数
     * @param userId 用户ID
     * @return 预约总数
     */
    @Select("SELECT COUNT(*) FROM appointment WHERE user_id = #{userId}")
    int selectCountByUserId(Long userId);
    
    /**
     * 管理员查询预约列表
     * @param userName 用户姓名（可选）
     * @param scenicSpotName 景点名称（可选）
     * @param status 预约状态（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 预约列表
     */
    List<Appointment> selectForAdmin(@Param("userName") String userName, @Param("scenicSpotName") String scenicSpotName, 
                                     @Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 管理员查询预约总数
     * @param userName 用户姓名（可选）
     * @param scenicSpotName 景点名称（可选）
     * @param status 预约状态（可选）
     * @return 预约总数
     */
    int selectCountForAdmin(@Param("userName") String userName, @Param("scenicSpotName") String scenicSpotName, 
                            @Param("status") String status);
    
    /**
     * 根据日期查询预约人数
     * @param date 日期
     * @return 预约人数
     */
    @Select("SELECT COUNT(*) FROM appointment WHERE DATE(appointment_time) = #{date} AND status != 'CANCELLED'")
    int countByDate(String date);
}
