package com.scenic.service.appointment;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.entity.appointment.IndividualReservation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 个人预约服务接口
 */
public interface IndividualReservationService {
    
    /**
     * 创建个人预约
     * @param reservation 个人预约信息
     * @return 创建结果
     */
    @Transactional(rollbackFor = Exception.class)
    Result<String> createReservation(IndividualReservation reservation);
    
    /**
     * 根据ID查询个人预约
     * @param id 预约ID
     * @return 个人预约信息
     */
    Result<IndividualReservation> getReservationById(Long id);
    
    /**
     * 查询所有个人预约（分页，带条件查询）
     * @param applicant 预约人（模糊查询）
     * @param appointmentTime 预约时间
     * @param phone 电话（模糊查询）
     * @param status 状态（精确查询）
     * @param page 页码
     * @param size 每页大小
     * @return 个人预约列表
     */
    Result<PageResult<IndividualReservation>> getAllReservations(String applicant, String appointmentTime, String phone, Integer status, int page, int size);
    
    /**
     * 根据用户ID查询个人预约（分页）
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 个人预约列表
     */
    Result<PageResult<IndividualReservation>> getReservationsByUserId(Long userId, int page, int size);
    
    /**
     * 更新个人预约信息
     * @param reservation 个人预约信息
     * @return 更新结果
     */
    @Transactional(rollbackFor = Exception.class)
    Result<String> updateReservation(IndividualReservation reservation);
    
    /**
     * 删除个人预约（逻辑删除）
     * @param id 预约ID
     * @param updateBy 更新人
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    Result<String> deleteReservation(Long id, Long updateBy);
    
    /**
     * 根据预约编号查询个人预约
     * @param reservationNo 预约编号
     * @return 个人预约信息
     */
    Result<IndividualReservation> getReservationByNo(String reservationNo);
    
    /**
     * 取消个人预约
     * @param id 预约ID
     * @param cancelReason 取消原因
     * @param updateBy 更新人
     * @return 取消结果
     */
    @Transactional(rollbackFor = Exception.class)
    Result<String> cancelReservation(Long id, String cancelReason, Long updateBy);
    
    /**
     * 核销个人预约
     * @param id 预约ID
     * @param operatorId 操作员ID
     * @param verificationLocation 验证地点
     * @param deviceInfo 设备信息
     * @param verificationRemark 验证备注
     * @param updateBy 更新人
     * @return 核销结果
     */
    @Transactional(rollbackFor = Exception.class)
    Result<String> verifyReservation(Long id, Long operatorId, String verificationLocation, String deviceInfo, String verificationRemark, Long updateBy);
    
    /**
     * 根据证件号码和状态查询个人预约（分页）
     * @param idNumber 证件号码
     * @param status 状态（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 个人预约列表
     */
    Result<PageResult<IndividualReservation>> getReservationsByIdNumber(String idNumber, Integer status, int page, int size);
}