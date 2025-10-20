package com.scenic.controller.appointment;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.entity.appointment.IndividualReservation;
import com.scenic.service.appointment.IndividualReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 个人预约控制器
 * 提供管理后台端的API接口
 */
@RestController
@RequestMapping("/api/manage/individual-reservations")
public class IndividualReservationController {
    
    @Autowired
    private IndividualReservationService individualReservationService;
    
    /**
     * 创建个人预约
     * @param reservation 个人预约信息
     * @return 创建结果
     */
    @PostMapping
    public Result<String> createReservation(@RequestBody IndividualReservation reservation,
                                          @RequestParam(required = false) Long createBy) {
        if (createBy != null) {
            reservation.setCreateBy(createBy);
            reservation.setUpdateBy(createBy);
        }
        return individualReservationService.createReservation(reservation);
    }
    
    /**
     * 根据ID查询个人预约
     * @param id 预约ID
     * @return 个人预约信息
     */
    @GetMapping("/{id}")
    public Result<IndividualReservation> getReservationById(@PathVariable Long id) {
        return individualReservationService.getReservationById(id);
    }
    
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
    @GetMapping
    public Result<PageResult<IndividualReservation>> getAllReservations(
            @RequestParam(required = false) String applicant,
            @RequestParam(required = false) String appointmentTime,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return individualReservationService.getAllReservations(applicant, appointmentTime, phone, status, page, size);
    }
    
    /**
     * 更新个人预约信息
     * @param id 预约ID
     * @param reservation 个人预约信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<String> updateReservation(@PathVariable Long id,
                                          @RequestBody IndividualReservation reservation,
                                          @RequestParam(required = false) Long updateBy) {
        reservation.setId(id);
        reservation.setUpdateBy(id);
        if (updateBy != null) {
            reservation.setUpdateBy(updateBy);
        }
        return individualReservationService.updateReservation(reservation);
    }
    
    /**
     * 删除个人预约（逻辑删除）
     * @param id 预约ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteReservation(@PathVariable Long id,
                                          @RequestParam Long updateBy) {
        return individualReservationService.deleteReservation(id, updateBy);
    }
    
    /**
     * 根据预约编号查询个人预约
     * @param reservationNo 预约编号
     * @return 个人预约信息
     */
    @GetMapping("/by-no/{reservationNo}")
    public Result<IndividualReservation> getReservationByNo(@PathVariable String reservationNo) {
        return individualReservationService.getReservationByNo(reservationNo);
    }
    
    /**
     * 取消个人预约
     * @param id 预约ID
     * @param cancelReason 取消原因
     * @return 取消结果
     */
    @PutMapping("/{id}/cancel")
    public Result<String> cancelReservation(@PathVariable Long id,
                                          @RequestParam String cancelReason,
                                          @RequestParam Long updateBy) {
        return individualReservationService.cancelReservation(id, cancelReason, updateBy);
    }
    
    /**
     * 核销个人预约
     * @param id 预约ID
     * @param operatorId 操作员ID
     * @param verificationLocation 验证地点
     * @param deviceInfo 设备信息
     * @param verificationRemark 验证备注
     * @return 核销结果
     */
    @PutMapping("/{id}/verify")
    public Result<String> verifyReservation(@PathVariable Long id,
                                          @RequestParam Long operatorId,
                                          @RequestParam String verificationLocation,
                                          @RequestParam(required = false) String deviceInfo,
                                          @RequestParam(required = false) String verificationRemark,
                                          @RequestParam Long updateBy) {
        return individualReservationService.verifyReservation(id, operatorId, verificationLocation, deviceInfo, verificationRemark, updateBy);
    }
}