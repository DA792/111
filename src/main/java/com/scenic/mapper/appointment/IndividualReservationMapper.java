package com.scenic.mapper.appointment;

import com.scenic.entity.appointment.IndividualReservation;
import com.scenic.entity.appointment.IndividualReservationPerson;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface IndividualReservationMapper {
    
    /**
     * 根据ID查询个人预约
     * @param id 预约ID
     * @return 个人预约信息
     */
    IndividualReservation selectById(Long id);
    
    /**
     * 查询所有个人预约（分页，带条件查询）
     * @param applicant 预约人（模糊查询）
     * @param appointmentTime 预约时间
     * @param phone 电话（模糊查询）
     * @param status 状态（精确查询）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 个人预约列表
     */
    List<IndividualReservation> selectAll(@Param("applicant") String applicant, 
                                         @Param("appointmentTime") String appointmentTime,
                                         @Param("phone") String phone,
                                         @Param("status") Integer status,
                                         @Param("offset") int offset, 
                                         @Param("limit") int limit);
    
    /**
     * 根据用户ID查询个人预约（分页）
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 个人预约列表
     */
    List<IndividualReservation> selectByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据状态查询个人预约（分页）
     * @param status 状态
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 个人预约列表
     */
    List<IndividualReservation> selectByStatus(@Param("status") Integer status, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据状态查询个人预约（无分页）
     * @param status 状态
     * @return 个人预约列表
     */
    List<IndividualReservation> selectByStatus(@Param("status") Integer status);
    
    /**
     * 查询个人预约列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 个人预约列表
     */
    List<IndividualReservation> selectList(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询个人预约总数
     * @return 个人预约总数
     */
    int selectCount();
    
    /**
     * 插入个人预约
     * @param reservation 个人预约信息
     * @return 插入结果
     */
    int insert(IndividualReservation reservation);
    
    /**
     * 更新个人预约信息
     * @param reservation 个人预约信息
     * @return 更新结果
     */
    int updateById(IndividualReservation reservation);
    
    /**
     * 根据ID删除个人预约（逻辑删除）
     * @param id 预约ID
     * @param updateBy 更新人
     * @return 删除结果
     */
    int deleteById(@Param("id") Long id, @Param("updateBy") Long updateBy);
    
    /**
     * 根据预约ID逻辑删除预约人员（设置deleted = 1）
     * @param reservationId 预约ID
     * @param updateBy 更新人
     * @return 删除结果
     */
    @Update("UPDATE individual_reservation_person SET deleted = 1, update_time = NOW(), update_by = #{updateBy} WHERE reservation_id = #{reservationId}")
    int deletePersonsByReservationId(@Param("reservationId") Long reservationId, @Param("updateBy") Long updateBy);
    
    /**
     * 根据ID查询个人预约人员
     * @param id 预约人员ID
     * @return 个人预约人员信息
     */
    @Select("SELECT * FROM individual_reservation_person WHERE id = #{id} AND deleted = 0")
    IndividualReservationPerson selectPersonById(Long id);
    
    /**
     * 更新个人预约人员信息
     * @param person 个人预约人员信息
     * @return 更新结果
     */
    int updatePersonById(IndividualReservationPerson person);
    
    /**
     * 批量更新个人预约人员信息
     * @param persons 个人预约人员列表
     * @return 更新结果
     */
    int updatePersonsBatch(@Param("persons") List<IndividualReservationPerson> persons);
    
    /**
     * 查询个人预约总数（带条件查询）
     * @param applicant 预约人（模糊查询）
     * @param appointmentTime 预约时间
     * @param phone 电话（模糊查询）
     * @param status 状态（精确查询）
     * @return 总数
     */
    int selectCount(@Param("applicant") String applicant, 
                   @Param("appointmentTime") String appointmentTime,
                   @Param("phone") String phone,
                   @Param("status") Integer status);
    
    /**
     * 根据用户ID查询个人预约总数
     * @param userId 用户ID
     * @return 总数
     */
    @Select("SELECT COUNT(*) FROM individual_reservation WHERE user_id = #{userId} AND deleted = 0")
    int selectCountByUserId(Long userId);
    
    /**
     * 根据状态查询个人预约总数
     * @param status 状态
     * @return 总数
     */
    @Select("SELECT COUNT(*) FROM individual_reservation WHERE status = #{status} AND deleted = 0")
    int selectCountByStatus(Integer status);
    
    /**
     * 根据预约编号查询个人预约
     * @param reservationNo 预约编号
     * @return 个人预约信息
     */
    @Select("SELECT * FROM individual_reservation WHERE reservation_no = #{reservationNo} AND deleted = 0")
    IndividualReservation selectByReservationNo(String reservationNo);
    
    /**
     * 根据条件查询预约记录（用于防重复检查）
     * @param visitDate 入区日期
     * @param timeSlot 时间段
     * @param idType 证件类型
     * @param idNumber 证件号码
     * @return 预约记录
     */
    @Select("SELECT ir.* FROM individual_reservation ir " +
            "JOIN individual_reservation_person irp ON ir.id = irp.reservation_id " +
            "WHERE ir.visit_date = #{visitDate} " +
            "AND ir.time_slot = #{timeSlot} " +
            "AND irp.id_type = #{idType} " +
            "AND irp.id_number = #{idNumber} " +
            "AND ir.deleted = 0 " +
            "AND irp.deleted = 0 " +
            "AND irp.is_contact = 1 " +
            "AND ir.status != 1 " +  // 排除已取消的预约
            "LIMIT 1")
    IndividualReservation selectByConditions(@Param("visitDate") Date visitDate, 
                                           @Param("timeSlot") Integer timeSlot,
                                           @Param("idType") Integer idType,
                                           @Param("idNumber") String idNumber);
    
    /**
     * 根据预约ID查询主联系人信息
     * @param reservationId 预约ID
     * @return 主联系人信息
     */
    @Select("SELECT * FROM individual_reservation_person WHERE reservation_id = #{reservationId} AND is_contact = 1 AND deleted = 0")
    IndividualReservationPerson selectMainContactByReservationId(Long reservationId);
    
    /**
     * 插入个人预约人员
     * @param person 个人预约人员信息
     * @return 插入结果
     */
    @Insert("INSERT INTO individual_reservation_person(reservation_id, name, id_type, id_number, phone, " +
            "person_type, is_contact, visit_date, time_slot, version, deleted, create_time, update_time, create_by, update_by) " +
            "VALUES(#{reservationId}, #{name}, #{idType}, #{idNumber}, #{phone}, " +
            "#{personType}, #{isContact}, #{visitDate}, #{timeSlot}, #{version}, #{deleted}, #{createTime}, #{updateTime}, #{createBy}, #{updateBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertPerson(IndividualReservationPerson person);
    
    /**
     * 批量插入个人预约人员
     * @param persons 个人预约人员列表
     * @return 插入结果
     */
    int insertPersonsBatch(@Param("persons") List<IndividualReservationPerson> persons);
    
    /**
     * 根据预约ID查询所有预约人员信息
     * @param reservationId 预约ID
     * @return 预约人员列表
     */
    @Select("SELECT * FROM individual_reservation_person WHERE reservation_id = #{reservationId} AND deleted = 0 ORDER BY is_contact DESC, create_time ASC")
    List<IndividualReservationPerson> selectReservationPersonsByReservationId(Long reservationId);
    
    /**
     * 管理员查询个人预约列表（带条件查询）
     * @param userId 用户ID（可选）
     * @param scenicId 景区ID（可选）
     * @param status 状态（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 个人预约列表
     */
    List<IndividualReservation> selectForAdmin(@Param("userId") Long userId, @Param("scenicId") Long scenicId, 
                                              @Param("status") Integer status, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 管理员查询个人预约总数（带条件查询）
     * @param userId 用户ID（可选）
     * @param scenicId 景区ID（可选）
     * @param status 状态（可选）
     * @return 总数
     */
    int selectCountForAdmin(@Param("userId") Long userId, @Param("scenicId") Long scenicId, @Param("status") Integer status);
    
    /**
     * 根据证件号码和状态查询个人预约（分页）
     * @param idNumber 证件号码
     * @param status 状态（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 个人预约列表
     */
    List<IndividualReservation> selectByIdNumberAndStatus(@Param("idNumber") String idNumber, 
                                                         @Param("status") Integer status,
                                                         @Param("offset") int offset, 
                                                         @Param("limit") int limit);
    
    /**
     * 根据证件号码和状态查询个人预约总数
     * @param idNumber 证件号码
     * @param status 状态（可选）
     * @return 总数
     */
    int selectCountByIdNumberAndStatus(@Param("idNumber") String idNumber, @Param("status") Integer status);
}