package com.scenic.mapper.appointment;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.appointment.TeamMember;

@Mapper
public interface TeamMemberMapper {
    
    /**
     * 根据ID查询团队成员
     * @param id 团队成员ID
     * @return 团队成员信息
     */
    @Select("SELECT * FROM team_member WHERE id = #{id}")
    TeamMember selectById(Long id);
    
    /**
     * 根据团队预约ID查询团队成员列表
     * @param teamAppointmentId 团队预约ID
     * @return 团队成员列表
     */
    @Select("SELECT * FROM team_member WHERE team_appointment_id = #{teamAppointmentId}")
    List<TeamMember> selectByTeamAppointmentId(Long teamAppointmentId);
    
    /**
     * 插入团队成员
     * @param teamMember 团队成员信息
     * @return 插入结果
     */
    @Insert("INSERT INTO team_member(team_appointment_id, name, id_card, phone, age, gender, remark, create_time, update_time) " +
            "VALUES(#{teamAppointmentId}, #{name}, #{idCard}, #{phone}, #{age}, #{gender}, #{remark}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TeamMember teamMember);
    
    /**
     * 批量插入团队成员
     * @param teamMembers 团队成员列表
     * @return 插入结果
     */
    @Insert("<script>" +
            "INSERT INTO team_member(team_appointment_id, name, id_card, phone, age, gender, remark, create_time, update_time) VALUES " +
            "<foreach collection='list' item='member' separator=','>" +
            "(#{member.teamAppointmentId}, #{member.name}, #{member.idCard}, #{member.phone}, #{member.age}, #{member.gender}, #{member.remark}, #{member.createTime}, #{member.updateTime})" +
            "</foreach>" +
            "</script>")
    int insertBatch(@Param("list") List<TeamMember> teamMembers);
    
    /**
     * 更新团队成员
     * @param teamMember 团队成员信息
     * @return 更新结果
     */
    @Update("UPDATE team_member SET name = #{name}, id_card = #{idCard}, phone = #{phone}, age = #{age}, gender = #{gender}, remark = #{remark}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(TeamMember teamMember);
    
    /**
     * 根据ID删除团队成员
     * @param id 团队成员ID
     * @return 删除结果
     */
    @Delete("DELETE FROM team_member WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据团队预约ID删除团队成员
     * @param teamAppointmentId 团队预约ID
     * @return 删除结果
     */
    @Delete("DELETE FROM team_member WHERE team_appointment_id = #{teamAppointmentId}")
    int deleteByTeamAppointmentId(Long teamAppointmentId);
}
