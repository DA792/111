package com.scenic.service.appointment.impl;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.entity.appointment.IndividualReservation;
import com.scenic.entity.appointment.IndividualReservationPerson;
import com.scenic.exception.reservation.DuplicateReservationException;
import com.scenic.exception.reservation.ServiceUnavailableException;
import com.scenic.mapper.appointment.IndividualReservationMapper;
import com.scenic.service.appointment.IndividualReservationService;
import com.scenic.utils.RedisUtil;
import com.scenic.utils.ReservationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 个人预约服务实现类
 */
@Service
public class IndividualReservationServiceImpl implements IndividualReservationService {
    
    private static final Logger log = LoggerFactory.getLogger(IndividualReservationServiceImpl.class);
    
    @Autowired
    private IndividualReservationMapper individualReservationMapper;
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Autowired
    private ReservationUtil reservationUtil;
    
    /**
     * 创建个人预约
     * @param reservation 个人预约信息
     * @return 创建结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> createReservation(IndividualReservation reservation) {
        log.info("开始处理预约请求，用户:{}, 时段:{}", 
            reservation.getContactIdNumber(), reservation.getTimeSlot());
        
        try {
            // 1. 防重复检查
            reservationUtil.checkDuplicateReservation(reservation);
            
            // 2. 生成预约编号
            if (reservation.getReservationNo() == null || reservation.getReservationNo().isEmpty()) {
                reservation.setReservationNo(reservationUtil.generateReservationNo());
            }
            
            // 3. 设置默认值
            setDefaultValues(reservation);
            
            // 4. 插入预约记录
            int result = individualReservationMapper.insert(reservation);
            
            // 5. 再插入主预约人和同行预约人信息到individual_reservation_person表
            if (result > 0) {
                insertReservationPersons(reservation);
            }
            
            if (result > 0) {
                log.info("预约创建成功，预约编号: {}", reservation.getReservationNo());
                return Result.success("预约创建成功", reservation.getReservationNo());
            } else {
                log.error("预约创建失败，数据库插入返回0");
                return Result.error("预约创建失败");
            }
        } catch (DuplicateReservationException e) {
            log.warn("重复预约异常: {}", e.getMessage());
            // 释放Redis锁
            reservationUtil.releaseReservationLock(reservation);
            throw e;
        } catch (RedisConnectionFailureException e) {
            log.error("Redis连接失败，降级到数据库检查", e);
            // Redis故障降级处理
            handleRedisFailure(reservation);
            return Result.error("服务暂时不可用");
        } catch (DataAccessException e) {
            log.error("数据库访问异常", e);
            // 释放Redis锁
            reservationUtil.releaseReservationLock(reservation);
            throw new ServiceUnavailableException("数据访问异常");
        } catch (Exception e) {
            log.error("预约创建异常", e);
            // 释放Redis锁
            reservationUtil.releaseReservationLock(reservation);
            return Result.error("预约创建异常：" + e.getMessage());
        }
    }
    
    /**
     * 设置默认值
     */
    private void setDefaultValues(IndividualReservation reservation) {
        if (reservation.getVersion() == null) {
            reservation.setVersion(0);
        }
        if (reservation.getDeleted() == null) {
            reservation.setDeleted(0);
        }
        if (reservation.getCreateTime() == null) {
            reservation.setCreateTime(LocalDateTime.now());
        }
        if (reservation.getUpdateTime() == null) {
            reservation.setUpdateTime(LocalDateTime.now());
        }
        if (reservation.getAdultCount() == null) {
            reservation.setAdultCount(0);
        }
        if (reservation.getChildCount() == null) {
            reservation.setChildCount(0);
        }
        if (reservation.getTotalCount() == null) {
            reservation.setTotalCount(reservation.getAdultCount() + reservation.getChildCount());
        }
        if (reservation.getStatus() == null) {
            reservation.setStatus(0); // 0-未开始
        }
    }
    
    /**
     * 插入预约人员信息（主预约人和同行预约人）
     * @param reservation 预约信息
     */
    private void insertReservationPersons(IndividualReservation reservation) {
        if (reservation.getReservationPersons() != null && !reservation.getReservationPersons().isEmpty()) {
            // 设置预约人员的公共字段
            for (IndividualReservationPerson person : reservation.getReservationPersons()) {
                person.setReservationId(reservation.getId());
                person.setVisitDate(reservation.getVisitDate());
                person.setTimeSlot(reservation.getTimeSlot());
                if (person.getVersion() == null) {
                    person.setVersion(0);
                }
                if (person.getDeleted() == null) {
                    person.setDeleted(0);
                }
                if (person.getCreateTime() == null) {
                    person.setCreateTime(LocalDateTime.now());
                }
                if (person.getUpdateTime() == null) {
                    person.setUpdateTime(LocalDateTime.now());
                }
                if (person.getCreateBy() == null) {
                    person.setCreateBy(reservation.getCreateBy());
                }
                if (person.getUpdateBy() == null) {
                    person.setUpdateBy(reservation.getUpdateBy());
                }
            }
            
            // 批量插入预约人员
            individualReservationMapper.insertPersonsBatch(reservation.getReservationPersons());
        }
    }
    
    /**
     * Redis故障降级处理
     */
    private void handleRedisFailure(IndividualReservation reservation) {
        try {
            // Redis不可用时直接查数据库
            IndividualReservation existing = individualReservationMapper.selectByConditions(
                reservation.getVisitDate(),
                reservation.getTimeSlot(),
                reservation.getContactIdType(),
                reservation.getContactIdNumber());
            
            // 只有非取消状态的预约才算重复
            if (existing != null && existing.getStatus() != null && existing.getStatus() != 1) {
                // 如果是同一用户的预约，提供更明确的错误信息
                if (existing.getUserId() != null && existing.getUserId().equals(reservation.getUserId())) {
                    throw new DuplicateReservationException("您已在该时段预约，请勿重复预约");
                } else {
                    throw new DuplicateReservationException("该时段已被预约，请选择其他时间");
                }
            }
        } catch (DataAccessException e) {
            log.error("数据库检查重复预约失败", e);
            throw new ServiceUnavailableException("服务暂时不可用");
        }
    }
    
    /**
     * 根据ID查询个人预约
     * @param id 预约ID
     * @return 个人预约信息
     */
    @Override
    public Result<IndividualReservation> getReservationById(Long id) {
        try {
            IndividualReservation reservation = individualReservationMapper.selectById(id);
            
            if (reservation != null) {
                // 获取主联系人信息
                IndividualReservationPerson mainContact = individualReservationMapper.selectMainContactByReservationId(id);
                if (mainContact != null) {
                    reservation.setContactName(mainContact.getName());
                    reservation.setContactIdType(mainContact.getIdType());
                    reservation.setContactIdNumber(mainContact.getIdNumber());
                    reservation.setContactPhone(mainContact.getPhone());
                }
                
                // 获取所有预约人员信息
                List<IndividualReservationPerson> reservationPersons = individualReservationMapper.selectReservationPersonsByReservationId(id);
                reservation.setReservationPersons(reservationPersons);
                
                return Result.success("查询成功", reservation);
            } else {
                return Result.error("预约记录不存在");
            }
        } catch (Exception e) {
            return Result.error("查询异常：" + e.getMessage());
        }
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
    @Override
    public Result<PageResult<IndividualReservation>> getAllReservations(String applicant, String appointmentTime, String phone, Integer status, int page, int size) {
        try {
            // 分页查询
            List<IndividualReservation> reservations = individualReservationMapper.selectAll(applicant, appointmentTime, phone, status, (page - 1) * size, size);
            
            // 获取总数
            int total = individualReservationMapper.selectCount(applicant, appointmentTime, phone, status);
            
            PageResult<IndividualReservation> pageResult = PageResult.of(total, size, page, reservations);
            return Result.success("查询成功", pageResult);
        } catch (Exception e) {
            return Result.error("查询异常：" + e.getMessage());
        }
    }
    
    /**
     * 根据用户ID查询个人预约（分页）
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 个人预约列表
     */
    @Override
    public Result<PageResult<IndividualReservation>> getReservationsByUserId(Long userId, int page, int size) {
        try {
            // 分页查询
            List<IndividualReservation> reservations = individualReservationMapper.selectByUserId(userId, (page - 1) * size, size);
            
            // 获取总数
            int total = individualReservationMapper.selectCountByUserId(userId);
            
            PageResult<IndividualReservation> pageResult = PageResult.of(total, size, page, reservations);
            return Result.success("查询成功", pageResult);
        } catch (Exception e) {
            return Result.error("查询异常：" + e.getMessage());
        }
    }
    
    /**
     * 更新个人预约信息
     * @param reservation 个人预约信息
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateReservation(IndividualReservation reservation) {
        try {
            // 检查预约是否存在
            IndividualReservation existingReservation = individualReservationMapper.selectById(reservation.getId());
            if (existingReservation == null) {
                return Result.error("预约记录不存在");
            }
            
            // 更新时间
            reservation.setUpdateTime(LocalDateTime.now());
            // 版本号增加
            reservation.setVersion(existingReservation.getVersion() + 1);
            
            // 更新预约记录
            int result = individualReservationMapper.updateById(reservation);
            
            if (result > 0) {
                return Result.success("预约更新成功");
            } else {
                return Result.error("预约更新失败");
            }
        } catch (Exception e) {
            return Result.error("预约更新异常：" + e.getMessage());
        }
    }
    
    /**
     * 删除个人预约（逻辑删除）
     * @param id 预约ID
     * @param updateBy 更新人
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteReservation(Long id, Long updateBy) {
        try {
            // 检查预约是否存在
            IndividualReservation existingReservation = individualReservationMapper.selectById(id);
            if (existingReservation == null) {
                return Result.error("预约记录不存在");
            }
            
            // 逻辑删除
            int result = individualReservationMapper.deleteById(id, updateBy);
            
            if (result > 0) {
                return Result.success("预约删除成功");
            } else {
                return Result.error("预约删除失败");
            }
        } catch (Exception e) {
            return Result.error("预约删除异常：" + e.getMessage());
        }
    }
    
    /**
     * 根据预约编号查询个人预约
     * @param reservationNo 预约编号
     * @return 个人预约信息
     */
    @Override
    public Result<IndividualReservation> getReservationByNo(String reservationNo) {
        try {
            IndividualReservation reservation = individualReservationMapper.selectByReservationNo(reservationNo);
            
            if (reservation != null) {
                // 获取主联系人信息
                IndividualReservationPerson mainContact = individualReservationMapper.selectMainContactByReservationId(reservation.getId());
                if (mainContact != null) {
                    reservation.setContactName(mainContact.getName());
                    reservation.setContactIdType(mainContact.getIdType());
                    reservation.setContactIdNumber(mainContact.getIdNumber());
                    reservation.setContactPhone(mainContact.getPhone());
                }
                
                // 获取所有预约人员信息
                List<IndividualReservationPerson> reservationPersons = individualReservationMapper.selectReservationPersonsByReservationId(reservation.getId());
                reservation.setReservationPersons(reservationPersons);
                
                return Result.success("查询成功", reservation);
            } else {
                return Result.error("预约记录不存在");
            }
        } catch (Exception e) {
            return Result.error("查询异常：" + e.getMessage());
        }
    }
    
    /**
     * 取消个人预约
     * @param id 预约ID
     * @param cancelReason 取消原因
     * @param updateBy 更新人
     * @return 取消结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> cancelReservation(Long id, String cancelReason, Long updateBy) {
        try {
            // 检查预约是否存在
            IndividualReservation existingReservation = individualReservationMapper.selectById(id);
            if (existingReservation == null) {
                return Result.error("预约记录不存在");
            }
            
            // 检查预约状态是否可以取消
            if (existingReservation.getStatus() != null && existingReservation.getStatus() == 10) {
                return Result.error("已核销的预约无法取消");
            }
            if (existingReservation.getStatus() != null && existingReservation.getStatus() == 1) {
                return Result.error("预约已取消");
            }
            
            // 更新预约状态为取消
            existingReservation.setStatus(1); // 1-已取消
            existingReservation.setCancelTime(new java.util.Date());
            existingReservation.setCancelReason(cancelReason);
            existingReservation.setUpdateBy(updateBy);
            existingReservation.setUpdateTime(LocalDateTime.now());
            existingReservation.setVersion(existingReservation.getVersion() + 1);
            
            // 更新预约记录
            int result = individualReservationMapper.updateById(existingReservation);
            
            if (result > 0) {
                return Result.success("预约取消成功");
            } else {
                return Result.error("预约取消失败");
            }
        } catch (Exception e) {
            return Result.error("预约取消异常：" + e.getMessage());
        }
    }
    
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
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> verifyReservation(Long id, Long operatorId, String verificationLocation, String deviceInfo, String verificationRemark, Long updateBy) {
        try {
            // 检查预约是否存在
            IndividualReservation existingReservation = individualReservationMapper.selectById(id);
            if (existingReservation == null) {
                return Result.error("预约记录不存在");
            }
            
            // 检查预约状态是否可以核销
            if (existingReservation.getStatus() != null && existingReservation.getStatus() == 1) {
                return Result.error("已取消的预约无法核销");
            }
            if (existingReservation.getStatus() != null && existingReservation.getStatus() == 10) {
                return Result.error("预约已核销");
            }
            
            // 更新预约状态为已核销
            existingReservation.setStatus(10); // 10-已核销
            existingReservation.setVerificationTime(new java.util.Date());
            existingReservation.setOperatorId(operatorId);
            existingReservation.setVerificationLocation(verificationLocation);
            existingReservation.setDeviceInfo(deviceInfo);
            existingReservation.setVerificationRemark(verificationRemark);
            existingReservation.setUpdateBy(updateBy);
            existingReservation.setUpdateTime(LocalDateTime.now());
            existingReservation.setVersion(existingReservation.getVersion() + 1);
            
            // 更新预约记录
            int result = individualReservationMapper.updateById(existingReservation);
            
            if (result > 0) {
                return Result.success("预约核销成功");
            } else {
                return Result.error("预约核销失败");
            }
        } catch (Exception e) {
            return Result.error("预约核销异常：" + e.getMessage());
        }
    }
    
    /**
     * 根据证件号码和状态查询个人预约（分页）
     * @param idNumber 证件号码
     * @param status 状态（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 个人预约列表
     */
    @Override
    public Result<PageResult<IndividualReservation>> getReservationsByIdNumber(String idNumber, Integer status, int page, int size) {
        try {
            // 分页查询
            List<IndividualReservation> reservations = individualReservationMapper.selectByIdNumberAndStatus(idNumber, status, (page - 1) * size, size);
            
            // 获取总数
            int total = individualReservationMapper.selectCountByIdNumberAndStatus(idNumber, status);
            
            // 为每个预约记录填充详细信息
            for (IndividualReservation reservation : reservations) {
                // 获取主联系人信息
                IndividualReservationPerson mainContact = individualReservationMapper.selectMainContactByReservationId(reservation.getId());
                if (mainContact != null) {
                    reservation.setContactName(mainContact.getName());
                    reservation.setContactIdType(mainContact.getIdType());
                    reservation.setContactIdNumber(mainContact.getIdNumber());
                    reservation.setContactPhone(mainContact.getPhone());
                }
                
                // 获取所有预约人员信息
                List<IndividualReservationPerson> reservationPersons = individualReservationMapper.selectReservationPersonsByReservationId(reservation.getId());
                reservation.setReservationPersons(reservationPersons);
            }
            
            PageResult<IndividualReservation> pageResult = PageResult.of(total, size, page, reservations);
            return Result.success("查询成功", pageResult);
        } catch (Exception e) {
            return Result.error("查询异常：" + e.getMessage());
        }
    }
}