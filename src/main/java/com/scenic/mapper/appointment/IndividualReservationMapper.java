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
    @Select("SELECT * FROM individual_reservation WHERE id = #{id}")
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
    @Select("SELECT * FROM individual_reservation WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<IndividualReservation> selectByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据状态查询个人预约（分页）
     * @param status 状态
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 个人预约列表
     */
    @Select("SELECT * FROM individual_reservation WHERE status = #{status} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<IndividualReservation> selectByStatus(@Param("status") Integer status, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 插入个人预约
     * @param reservation 个人预约信息
     * @return 插入结果
     */
    @Insert("INSERT INTO individual_reservation(reservation_no, user_id, scenic_id, visit_date, time_slot, " +
            "adult_count, child_count, total_count, status, verification_time, operator_id, verification_location, " +
            "device_info, verification_remark, cancel_time, cancel_reason, version, deleted, create_time, update_time, create_by, update_by) " +
            "VALUES(#{reservationNo}, #{userId}, #{scenicId}, #{visitDate}, #{timeSlot}, " +
            "#{adultCount}, #{childCount}, #{totalCount}, #{status}, #{verificationTime}, #{operatorId}, #{verificationLocation}, " +
            "#{deviceInfo}, #{verificationRemark}, #{cancelTime}, #{cancelReason}, #{version}, #{deleted}, #{createTime}, #{updateTime}, #{createBy}, #{updateBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
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
    @Update("UPDATE individual_reservation SET deleted = 1, update_time = NOW(), update_by = #{updateBy} WHERE id = #{id}")
    int deleteById(@Param("id") Long id, @Param("updateBy") Long updateBy);
    
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