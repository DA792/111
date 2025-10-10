package com.scenic.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.appointment.AppointmentResponseDTO;
import com.scenic.dto.user.UserAppointmentPersonDTO;
import com.scenic.dto.user.UserMessageDTO;
import com.scenic.dto.user.UserPhotoDTO;
import com.scenic.entity.user.User;
import com.scenic.service.user.UserService;

/**
 * 用户管理控制器
 * 提供小程序端和管理后台端的用户相关API接口
 */
@RestController
@RequestMapping("/api")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // 小程序端API接口前缀
    private static final String MINIAPP_PREFIX = "/uniapp";
    
    // 管理后台端API接口前缀
    private static final String ADMIN_PREFIX = "/manage";
    
    /**
     * 小程序端 - 微信登录
     * @param code 微信登录凭证
     * @return 登录结果
     */
    @PostMapping(MINIAPP_PREFIX + "/login/wechat")
    public Result<String> loginWithWeChatForMiniapp(@RequestParam String code) {
        return userService.loginWithWeChat(code);
    }
    
    /**
     * 小程序端 - 用户注册
     * @param user 用户信息
     * @return 注册结果
     */
    @PostMapping(MINIAPP_PREFIX + "/register")
    public Result<String> registerForMiniapp(@RequestBody User user) {
        return userService.register(user);
    }
    
    /**
     * 小程序端 - 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping(MINIAPP_PREFIX + "/users/{userId}")
    public Result<User> getUserInfoForMiniapp(@PathVariable Long userId) {
        return userService.getUserInfo(userId);
    }
    
    /**
     * 小程序端 - 更新用户信息
     * @param userId 用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping(MINIAPP_PREFIX + "/users/{userId}")
    public Result<String> updateUserInfoForMiniapp(
            @PathVariable Long userId,
            @RequestBody User user) {
        return userService.updateUserInfo(userId, user);
    }
    
    /**
     * 小程序端 - 获取用户个人信息（个人中心）
     * 用户进入个人中心时调用此接口获取个人信息
     * @param userId 用户ID
     * @return 用户个人信息
     */
    @GetMapping(MINIAPP_PREFIX + "/user/profile/{userId}")
    public Result<User> getUserProfileForMiniapp(@PathVariable Long userId) {
        return userService.getUserProfile(userId);
    }
    
    /**
     * 小程序端 - 更新用户个人信息（个人中心）
     * 用户编辑头像/昵称/手机号后调用此接口更新用户信息
     * @param userId 用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping(MINIAPP_PREFIX + "/user/profile/{userId}")
    public Result<String> updateUserProfileForMiniapp(
            @PathVariable Long userId,
            @RequestBody User user) {
        return userService.updateUserProfile(userId, user);
    }
    
    /**
     * 小程序端 - 获取用户的预约记录（我的预约）
     * 用户查看预约记录时调用此接口获取预约列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 预约记录列表
     */
    @GetMapping(MINIAPP_PREFIX + "/user/appointments/{userId}")
    public Result<PageResult<AppointmentResponseDTO>> getUserAppointmentsForMiniapp(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userService.getUserAppointments(userId, page, size);
    }
    
    /**
     * 小程序端 - 取消预约（我的预约）
     * 用户取消未开始的预约时调用此接口
     * @param appointmentId 预约ID
     * @return 取消结果
     */
    @PutMapping(MINIAPP_PREFIX + "/user/appointments/{appointmentId}/cancel")
    public Result<String> cancelAppointmentForMiniapp(@PathVariable Long appointmentId) {
        return userService.cancelAppointment(appointmentId);
    }
    
    /**
     * 小程序端 - 获取用户发布的照片（我的发布）
     * 用户查看自己发布的照片时调用此接口获取照片列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 照片列表
     */
    @GetMapping(MINIAPP_PREFIX + "/user/photos/{userId}")
    public Result<PageResult<UserPhotoDTO>> getUserPhotosForMiniapp(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userService.getUserPhotos(userId, page, size);
    }
    
    /**
     * 小程序端 - 获取用户的消息（我的消息）
     * 用户进入消息页时调用此接口获取消息列表
     * @param userId 用户ID
     * @param type 消息类型（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 消息列表
     */
    @GetMapping(MINIAPP_PREFIX + "/user/messages/{userId}")
    public Result<PageResult<UserMessageDTO>> getUserMessagesForMiniapp(
            @PathVariable Long userId,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userService.getUserMessages(userId, type, page, size);
    }
    
    /**
     * 小程序端 - 标记消息为已读
     * 用户查看消息后调用此接口标记消息为已读
     * @param messageId 消息ID
     * @return 更新结果
     */
    @PutMapping(MINIAPP_PREFIX + "/user/messages/{messageId}/read")
    public Result<String> markMessageAsReadForMiniapp(@PathVariable Long messageId) {
        return userService.markMessageAsRead(messageId);
    }
    
    /**
     * 小程序端 - 获取用户的预约人（人员管理）
     * 用户新增/编辑预约人时调用此接口获取预约人列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 预约人列表
     */
    @GetMapping(MINIAPP_PREFIX + "/user/appointment-persons/{userId}")
    public Result<PageResult<UserAppointmentPersonDTO>> getUserAppointmentPersonsForMiniapp(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userService.getUserAppointmentPersons(userId, page, size);
    }
    
    /**
     * 小程序端 - 添加预约人（人员管理）
     * 用户新增预约人时调用此接口
     * @param userId 用户ID
     * @param person 预约人信息
     * @return 添加结果
     */
    @PostMapping(MINIAPP_PREFIX + "/user/appointment-persons/{userId}")
    public Result<String> addUserAppointmentPersonForMiniapp(@PathVariable Long userId, @RequestBody UserAppointmentPersonDTO person) {
        return userService.addUserAppointmentPerson(userId, person);
    }
    
    /**
     * 小程序端 - 更新预约人（人员管理）
     * 用户编辑预约人时调用此接口
     * @param userId 用户ID
     * @param personId 预约人ID
     * @param person 预约人信息
     * @return 更新结果
     */
    @PutMapping(MINIAPP_PREFIX + "/user/appointment-persons/{userId}/{personId}")
    public Result<String> updateUserAppointmentPersonForMiniapp(@PathVariable Long userId, @PathVariable Long personId, @RequestBody UserAppointmentPersonDTO person) {
        return userService.updateUserAppointmentPerson(userId, personId, person);
    }
    
    /**
     * 小程序端 - 删除预约人（人员管理）
     * 用户删除预约人时调用此接口
     * @param userId 用户ID
     * @param personId 预约人ID
     * @return 删除结果
     */
    @DeleteMapping(MINIAPP_PREFIX + "/user/appointment-persons/{userId}/{personId}")
    public Result<String> deleteUserAppointmentPersonForMiniapp(@PathVariable Long userId, @PathVariable Long personId) {
        return userService.deleteUserAppointmentPerson(userId, personId);
    }
    
    /**
     * 管理后台端 - 查询用户列表
     * @param page 页码
     * @param size 每页大小
     * @param username 用户名（可选）
     * @param phone 电话（可选）
     * @return 用户列表
     */
    @GetMapping(ADMIN_PREFIX + "/users")
    public Result<PageResult<User>> getUsersForAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone) {
        return userService.getUsers(page, size, username, phone);
    }
    
    /**
     * 管理后台端 - 获取用户详情
     * @param userId 用户ID
     * @return 用户详情
     */
    @GetMapping(ADMIN_PREFIX + "/users/{userId}")
    public Result<User> getUserDetailForAdmin(@PathVariable Long userId) {
        return userService.getUserDetail(userId);
    }
    
    /**
     * 管理后台端 - 创建用户
     * @param user 用户信息
     * @return 创建结果
     */
    @PostMapping(ADMIN_PREFIX + "/users")
    public Result<String> createUserForAdmin(@RequestBody User user) {
        return userService.createUser(user);
    }
    
    /**
     * 管理后台端 - 重置用户密码
     * @param userId 用户ID
     * @return 重置结果
     */
    @PutMapping(ADMIN_PREFIX + "/users/{userId}/reset-password")
    public Result<String> resetPasswordForAdmin(@PathVariable Long userId) {
        return userService.resetPassword(userId);
    }
}
