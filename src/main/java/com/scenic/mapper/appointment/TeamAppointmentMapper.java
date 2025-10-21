package com.scenic.mapper.appointment;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.appointment.TeamAppointment;
import com.scenic.entity.appointment.TeamMember;

@Mapper
public interface TeamAppointmentMapper {
    
    /**
     * 根据ID查询团队预约
     * @param id 团队预约ID
     * @return 团队预约信息
     */
    @Select("SELECT * FROM team_appointment WHERE id = #{id}")
    TeamAppointment selectById(Long id);
    
    /**
     * 插入团队预约
     * @param teamAppointment 团队预约信息
     * @return 插入结果
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TeamAppointment teamAppointment);
    
    /**
     * 批量插入团队成员
     * @param members 团队成员列表
     * @return 插入结果
     */
    @Insert("<script>" +
            "INSERT INTO team_member(team_appointment_id, name, id_card, phone, age, gender, remark, create_time, update_time) VALUES " +
            "<foreach collection='members' item='member' separator=','>" +
            "(#{member.teamAppointmentId}, #{member.name}, #{member.idCard}, #{member.phone}, #{member.age}, #{member.gender}, #{member.remark}, #{member.createTime}, #{member.updateTime})" +
            "</foreach>" +
            "</script>")
    int insertTeamMembers(@Param("members") List<TeamMember> members);
    
    /**
     * 更新团队预约信息
     * @param teamAppointment 团队预约信息
     * @return 更新结果
     */
    @Update("UPDATE team_appointment SET team_name = #{teamName}, team_leader = #{contactPerson}, " +
            "contact_phone = #{contactPhone}, contact_email = #{contactEmail}, " +
            "team_size = #{numberOfPeople}, scenic_spot_id = #{scenicSpotId}, " +
            "scenic_spot_name = #{scenicSpotName}, appointment_date = #{appointmentDate}, " +
            "appointment_time = #{appointmentTime}, remarks = #{remark}, status = #{status}, " +
            "form_file_id = #{formFileId}, admin_remarks = #{adminRemarks}, check_in_time = #{checkInTime}, " +
            "update_time = #{updateTime}, update_by = #{updateBy} WHERE id = #{id}")
    int updateById(TeamAppointment teamAppointment);
    
    /**
     * 根据ID删除团队预约
     * @param id 团队预约ID
     * @return 删除结果
     */
    @Delete("DELETE FROM team_appointment WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 查询团队预约列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 团队预约列表
     */
    List<TeamAppointment> selectList(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询团队预约总数
     * @return 团队预约总数
     */
    int selectCount();
    
    /**
     * 管理员查询团队预约列表
     * @param teamName 团队名称（可选）
     * @param contactPerson 联系人（可选）
     * @param contactPhone 联系电话（可选）
     * @param status 团队预约状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 团队预约列表
     */
    List<TeamAppointment> selectForAdmin(@Param("teamName") String teamName, @Param("contactPerson") String contactPerson, 
                                         @Param("contactPhone") String contactPhone, @Param("status") String status,
                                         @Param("startTime") String startTime, @Param("endTime") String endTime,
                                         @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 管理员查询团队预约总数
     * @param teamName 团队名称（可选）
     * @param contactPerson 联系人（可选）
     * @param contactPhone 联系电话（可选）
     * @param status 团队预约状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 团队预约总数
     */
    int selectCountForAdmin(@Param("teamName") String teamName, @Param("contactPerson") String contactPerson, 
                            @Param("contactPhone") String contactPhone, @Param("status") String status,
                            @Param("startTime") String startTime, @Param("endTime") String endTime);
}
