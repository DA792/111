package com.scenic.service.user;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.user.UserFrequentMemberDTO;
import com.scenic.entity.user.UserFrequentMember;

import java.util.List;

/**
 * 用户常用预约人服务接口
 */
public interface UserFrequentMemberService {
    
    /**
     * 获取用户常用预约人列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 常用预约人列表
     */
    Result<PageResult<UserFrequentMemberDTO>> getUserFrequentMembers(Long userId, int page, int size);
    
    /**
     * 根据姓名搜索用户常用预约人
     * @param userId 用户ID
     * @param name 姓名
     * @param page 页码
     * @param size 每页大小
     * @return 常用预约人列表
     */
    Result<PageResult<UserFrequentMemberDTO>> searchUserFrequentMembersByName(Long userId, String name, int page, int size);
    
    /**
     * 获取用户常用预约人详情
     * @param id 常用预约人ID
     * @return 常用预约人详情
     */
    Result<UserFrequentMemberDTO> getUserFrequentMemberById(Long id);
    
    /**
     * 获取用户默认预约人
     * @param userId 用户ID
     * @return 默认预约人
     */
    Result<UserFrequentMemberDTO> getDefaultUserFrequentMember(Long userId);
    
    /**
     * 添加用户常用预约人
     * @param userId 用户ID
     * @param memberDTO 常用预约人信息
     * @param createBy 创建人
     * @return 添加结果
     */
    Result<String> addUserFrequentMember(Long userId, UserFrequentMemberDTO memberDTO, Long createBy);
    
    /**
     * 更新用户常用预约人信息
     * @param userId 用户ID
     * @param id 常用预约人ID
     * @param memberDTO 常用预约人信息
     * @param updateBy 更新人
     * @return 更新结果
     */
    Result<String> updateUserFrequentMember(Long userId, Long id, UserFrequentMemberDTO memberDTO, Long updateBy);
    
    /**
     * 更新用户常用预约人状态
     * @param userId 用户ID
     * @param id 常用预约人ID
     * @param status 状态
     * @param updateBy 更新人
     * @return 更新结果
     */
    Result<String> updateUserFrequentMemberStatus(Long userId, Long id, Byte status, Long updateBy);
    
    /**
     * 设置默认预约人
     * @param userId 用户ID
     * @param id 常用预约人ID
     * @param updateBy 更新人
     * @return 设置结果
     */
    Result<String> setDefaultUserFrequentMember(Long userId, Long id, Long updateBy);
    
    /**
     * 删除用户常用预约人
     * @param userId 用户ID
     * @param id 常用预约人ID
     * @param updateBy 更新人
     * @return 删除结果
     */
    Result<String> deleteUserFrequentMember(Long userId, Long id, Long updateBy);
    
    /**
     * 根据证件号码检查常用预约人是否存在
     * @param userId 用户ID
     * @param idNumber 证件号码
     * @return 是否存在
     */
    boolean isUserFrequentMemberExistByIdNumber(Long userId, String idNumber);
    
    /**
     * 获取用户所有启用的常用预约人列表
     * @param userId 用户ID
     * @return 常用预约人列表
     */
    List<UserFrequentMember> getEnabledUserFrequentMembers(Long userId);
}