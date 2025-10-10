package com.scenic.service.user;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.appointment.AppointmentResponseDTO;
import com.scenic.dto.user.UserAppointmentPersonDTO;
import com.scenic.dto.user.UserMessageDTO;
import com.scenic.dto.user.UserPhotoDTO;
import com.scenic.entity.user.User;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 微信登录
     * @param code 微信登录凭证
     * @return 登录结果
     */
    Result<String> loginWithWeChat(String code);
    
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册结果
     */
    Result<String> register(User user);
    
    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    Result<User> getUserInfo(Long userId);
    
    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    Result<String> updateUserInfo(Long userId, User user);
    
    /**
     * 获取用户个人信息（个人中心）
     * @param userId 用户ID
     * @return 用户个人信息
     */
    Result<User> getUserProfile(Long userId);
    
    /**
     * 更新用户个人信息（个人中心）
     * @param userId 用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    Result<String> updateUserProfile(Long userId, User user);
    
    /**
     * 获取用户的预约记录（我的预约）
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 预约记录列表
     */
    Result<PageResult<AppointmentResponseDTO>> getUserAppointments(Long userId, int page, int size);
    
    /**
     * 取消预约（我的预约）
     * @param appointmentId 预约ID
     * @return 取消结果
     */
    Result<String> cancelAppointment(Long appointmentId);
    
    /**
     * 查询用户列表
     * @param page 页码
     * @param size 每页大小
     * @param username 用户名（可选）
     * @param phone 电话（可选）
     * @return 用户列表
     */
    Result<PageResult<User>> getUsers(int page, int size, String username, String phone);
    
    /**
     * 获取用户详情
     * @param userId 用户ID
     * @return 用户详情
     */
    Result<User> getUserDetail(Long userId);
    
    /**
     * 创建用户
     * @param user 用户信息
     * @return 创建结果
     */
    Result<String> createUser(User user);
    
    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    Result<String> updateUser(Long userId, User user);
    
    /**
     * 删除用户
     * @param userId 用户ID
     * @return 删除结果
     */
    Result<String> deleteUser(Long userId);
    
    /**
     * 重置用户密码
     * @param userId 用户ID
     * @return 重置结果
     */
    Result<String> resetPassword(Long userId);
    
    /**
     * 获取用户发布的照片（我的发布）
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 照片列表
     */
    Result<PageResult<UserPhotoDTO>> getUserPhotos(Long userId, int page, int size);
    
    /**
     * 获取用户的消息（我的消息）
     * @param userId 用户ID
     * @param type 消息类型（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 消息列表
     */
    Result<PageResult<UserMessageDTO>> getUserMessages(Long userId, String type, int page, int size);
    
    /**
     * 标记消息为已读
     * @param messageId 消息ID
     * @return 更新结果
     */
    Result<String> markMessageAsRead(Long messageId);
    
    /**
     * 获取用户的预约人（人员管理）
     * @param userId 用户ID
     * @return 预约人列表
     */
    Result<PageResult<UserAppointmentPersonDTO>> getUserAppointmentPersons(Long userId, int page, int size);
    
    /**
     * 添加预约人（人员管理）
     * @param userId 用户ID
     * @param person 预约人信息
     * @return 添加结果
     */
    Result<String> addUserAppointmentPerson(Long userId, UserAppointmentPersonDTO person);
    
    /**
     * 更新预约人（人员管理）
     * @param userId 用户ID
     * @param personId 预约人ID
     * @param person 预约人信息
     * @return 更新结果
     */
    Result<String> updateUserAppointmentPerson(Long userId, Long personId, UserAppointmentPersonDTO person);
    
    /**
     * 删除预约人（人员管理）
     * @param userId 用户ID
     * @param personId 预约人ID
     * @return 删除结果
     */
    Result<String> deleteUserAppointmentPerson(Long userId, Long personId);
}
