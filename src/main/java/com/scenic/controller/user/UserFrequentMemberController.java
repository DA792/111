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
import com.scenic.dto.user.UserFrequentMemberDTO;
import com.scenic.service.user.UserFrequentMemberService;

/**
 * 用户常用预约人管理控制器
 * 提供小程序端的常用预约人增删查改API接口
 */
@RestController
@RequestMapping("/api/uniapp/user-frequent-members")
public class UserFrequentMemberController {
    
    @Autowired
    private UserFrequentMemberService userFrequentMemberService;
    
    /**
     * 小程序端 - 获取用户常用预约人列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 常用预约人列表
     */
    @GetMapping("/user/{userId}")
    public Result<PageResult<UserFrequentMemberDTO>> getUserFrequentMembersForMiniapp(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userFrequentMemberService.getUserFrequentMembers(userId, page, size);
    }
    
    /**
     * 小程序端 - 搜索用户常用预约人
     * @param userId 用户ID
     * @param name 姓名
     * @param page 页码
     * @param size 每页大小
     * @return 常用预约人列表
     */
    @GetMapping("/user/{userId}/search")
    public Result<PageResult<UserFrequentMemberDTO>> searchUserFrequentMembersForMiniapp(
            @PathVariable Long userId,
            @RequestParam String name,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userFrequentMemberService.searchUserFrequentMembersByName(userId, name, page, size);
    }
    
    /**
     * 小程序端 - 获取用户常用预约人详情
     * @param userId 用户ID
     * @param id 常用预约人ID
     * @return 常用预约人详情
     */
    @GetMapping("/user/{userId}/{id}")
    public Result<UserFrequentMemberDTO> getUserFrequentMemberForMiniapp(
            @PathVariable Long userId,
            @PathVariable Long id) {
        return userFrequentMemberService.getUserFrequentMemberById(id);
    }
    
    /**
     * 小程序端 - 获取用户默认预约人
     * @param userId 用户ID
     * @return 默认预约人
     */
    @GetMapping("/user/{userId}/default")
    public Result<UserFrequentMemberDTO> getDefaultUserFrequentMemberForMiniapp(
            @PathVariable Long userId) {
        return userFrequentMemberService.getDefaultUserFrequentMember(userId);
    }
    
    /**
     * 小程序端 - 添加用户常用预约人
     * @param userId 用户ID
     * @param memberDTO 常用预约人信息
     * @param createBy 创建人ID（从请求参数中获取）
     * @return 添加结果
     */
    @PostMapping("/user/{userId}")
    public Result<String> addUserFrequentMemberForMiniapp(
            @PathVariable Long userId,
            @RequestBody UserFrequentMemberDTO memberDTO,
            @RequestParam Long createBy) {
        return userFrequentMemberService.addUserFrequentMember(userId, memberDTO, createBy);
    }
    
    /**
     * 小程序端 - 更新用户常用预约人信息
     * @param userId 用户ID
     * @param id 常用预约人ID
     * @param memberDTO 常用预约人信息
     * @param updateBy 更新人ID（从请求参数中获取）
     * @return 更新结果
     */
    @PutMapping("/user/{userId}/{id}")
    public Result<String> updateUserFrequentMemberForMiniapp(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestBody UserFrequentMemberDTO memberDTO,
            @RequestParam Long updateBy) {
        return userFrequentMemberService.updateUserFrequentMember(userId, id, memberDTO, updateBy);
    }
    
    /**
     * 小程序端 - 更新用户常用预约人状态
     * @param userId 用户ID
     * @param id 常用预约人ID
     * @param status 状态：0-禁用，1-启用
     * @param updateBy 更新人ID（从请求参数中获取）
     * @return 更新结果
     */
    @PutMapping("/user/{userId}/{id}/status")
    public Result<String> updateUserFrequentMemberStatusForMiniapp(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestParam Byte status,
            @RequestParam Long updateBy) {
        return userFrequentMemberService.updateUserFrequentMemberStatus(userId, id, status, updateBy);
    }
    
    /**
     * 小程序端 - 设置默认预约人
     * @param userId 用户ID
     * @param id 常用预约人ID
     * @param updateBy 更新人ID（从请求参数中获取）
     * @return 设置结果
     */
    @PutMapping("/user/{userId}/{id}/default")
    public Result<String> setDefaultUserFrequentMemberForMiniapp(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestParam Long updateBy) {
        return userFrequentMemberService.setDefaultUserFrequentMember(userId, id, updateBy);
    }
    
    /**
     * 小程序端 - 删除用户常用预约人
     * @param userId 用户ID
     * @param id 常用预约人ID
     * @param updateBy 更新人ID（从请求参数中获取）
     * @return 删除结果
     */
    @DeleteMapping("/user/{userId}/{id}")
    public Result<String> deleteUserFrequentMemberForMiniapp(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestParam Long updateBy) {
        return userFrequentMemberService.deleteUserFrequentMember(userId, id, updateBy);
    }
}