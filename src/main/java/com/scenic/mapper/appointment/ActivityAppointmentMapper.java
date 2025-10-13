package com.scenic.mapper.appointment;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.appointment.ActivityAppointment;

@Mapper
public interface ActivityAppointmentMapper {
    
    /**
     * 根据ID查询活动预约
     * @param id 活动预约ID
     * @return 活动预约信息
     */
    @Select("SELECT * FROM activity_appointment WHERE id = #{id}")
    ActivityAppointment selectById(Long id);
    
    /**
     * 插入活动预约
     * @param activityAppointment 活动预约信息
     * @return 插入结果
     */
    @Insert("INSERT INTO activity_appointment(activity_name, contact_person, contact_phone, contact_email, " +
            "activity_id, activity_date, activity_time, number_of_people, remark, status, create_time, update_time) " +
            "VALUES(#{activityName}, #{contactPerson}, #{contactPhone}, #{contactEmail}, " +
            "#{activityId}, #{activityDate}, #{activityTime}, #{numberOfPeople}, #{remark}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ActivityAppointment activityAppointment);
    
    /**
     * 更新活动预约信息
     * @param activityAppointment 活动预约信息
     * @return 更新结果
     */
    @Update("UPDATE activity_appointment SET activity_name = #{activityName}, contact_person = #{contactPerson}, " +
            "contact_phone = #{contactPhone}, contact_email = #{contactEmail}, " +
            "activity_id = #{activityId}, activity_date = #{activityDate}, activity_time = #{activityTime}, " +
            "number_of_people = #{numberOfPeople}, remark = #{remark}, status = #{status}, " +
            "update_time = #{updateTime} WHERE id = #{id}")
    int updateById(ActivityAppointment activityAppointment);
    
    /**
     * 根据ID删除活动预约
     * @param id 活动预约ID
     * @return 删除结果
     */
    @Delete("DELETE FROM activity_appointment WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 查询活动预约列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 活动预约列表
     */
    @Select("SELECT * FROM activity_appointment ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<ActivityAppointment> selectList(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询活动预约总数
     * @return 活动预约总数
     */
    @Select("SELECT COUNT(*) FROM activity_appointment")
    int selectCount();
    
    /**
     * 管理员查询活动预约列表
     * @param activityName 活动名称（可选）
     * @param contactPerson 联系人（可选）
     * @param contactPhone 联系电话（可选）
     * @param status 活动预约状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 活动预约列表
     */
    List<ActivityAppointment> selectForAdmin(@Param("activityName") String activityName, @Param("contactPerson") String contactPerson, 
                                             @Param("contactPhone") String contactPhone, @Param("status") String status,
                                             @Param("startTime") String startTime, @Param("endTime") String endTime,
                                             @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 管理员查询活动预约总数
     * @param activityName 活动名称（可选）
     * @param contactPerson 联系人（可选）
     * @param contactPhone 联系电话（可选）
     * @param status 活动预约状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 活动预约总数
     */
    int selectCountForAdmin(@Param("activityName") String activityName, @Param("contactPerson") String contactPerson, 
                            @Param("contactPhone") String contactPhone, @Param("status") String status,
                            @Param("startTime") String startTime, @Param("endTime") String endTime);
}
