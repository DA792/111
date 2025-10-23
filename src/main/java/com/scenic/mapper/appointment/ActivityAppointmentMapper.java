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
    @Select("SELECT * FROM activity_registration WHERE id = #{id}")
    ActivityAppointment selectById(Long id);
    
    /**
     * 根据ID查询活动预约（包含团队成员信息）
     * @param id 活动预约ID
     * @return 活动预约信息（包含团队成员）
     */
    ActivityAppointment selectByIdWithMembers(Long id);
    
    /**
     * 插入活动预约
     * @param activityAppointment 活动预约信息
     * @return 插入结果
     */
    @Insert("INSERT INTO activity_registration(registration_no, activity_title, team_name, team_leader, contact_phone, contact_email, activity_id, user_id, form_file_id, registration_time, activity_time, team_size, remarks, status, create_time, update_time, create_by) VALUES(#{registrationNo}, #{activityName}, #{teamName}, #{contactPerson}, #{contactPhone}, #{contactEmail}, #{activityId}, #{userId}, #{formFileId}, #{activityDate}, #{activityTime}, #{numberOfPeople}, #{remark}, #{status}, #{createTime}, #{updateTime}, #{createBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ActivityAppointment activityAppointment);
    
    /**
     * 更新活动预约信息
     * @param activityAppointment 活动预约信息
     * @return 更新结果
     */
    int updateById(ActivityAppointment activityAppointment);
    
    /**
     * 根据ID删除活动预约
     * @param id 活动预约ID
     * @return 删除结果
     */
    @Delete("DELETE FROM activity_registration WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 查询活动预约列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 活动预约列表
     */
    List<ActivityAppointment> selectList(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询活动预约总数
     * @return 活动预约总数
     */
    int selectCount();
    
    /**
     * 管理员查询活动预约列表
     * @param activityName 活动名称（可选）
     * @param teamName 团队名称（可选）
     * @param contactPerson 联系人（可选）
     * @param contactPhone 联系电话（可选）
     * @param status 活动预约状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 活动预约列表
     */
    List<ActivityAppointment> selectForAdmin(@Param("activityName") String activityName, @Param("teamName") String teamName, @Param("contactPerson") String contactPerson, 
                                             @Param("contactPhone") String contactPhone, @Param("status") String status,
                                             @Param("startTime") String startTime, @Param("endTime") String endTime,
                                             @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 管理员查询活动预约总数
     * @param activityName 活动名称（可选）
     * @param teamName 团队名称（可选）
     * @param contactPerson 联系人（可选）
     * @param contactPhone 联系电话（可选）
     * @param status 活动预约状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 活动预约总数
     */
    int selectCountForAdmin(@Param("activityName") String activityName, @Param("teamName") String teamName, @Param("contactPerson") String contactPerson, 
                            @Param("contactPhone") String contactPhone, @Param("status") String status,
                            @Param("startTime") String startTime, @Param("endTime") String endTime);
}
