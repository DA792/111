package com.scenic.service.user.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.user.UserFrequentMemberDTO;
import com.scenic.entity.user.UserFrequentMember;
import com.scenic.mapper.user.UserFrequentMemberMapper;
import com.scenic.service.user.UserFrequentMemberService;

/**
 * 用户常用预约人服务实现类
 */
@Service
public class UserFrequentMemberServiceImpl implements UserFrequentMemberService {
    
    @Autowired
    private UserFrequentMemberMapper userFrequentMemberMapper;
    
    @Override
    public Result<PageResult<UserFrequentMemberDTO>> getUserFrequentMembers(Long userId, int page, int size) {
        try {
            // 参数校验
            if (userId == null || userId <= 0) {
                return Result.error("用户ID不能为空");
            }
            if (page <= 0) {
                page = 1;
            }
            if (size <= 0 || size > 100) {
                size = 10;
            }
            
            int offset = (page - 1) * size;
            List<UserFrequentMember> members = userFrequentMemberMapper.selectByUserId(userId, offset, size);
            
            // 转换为DTO
            List<UserFrequentMemberDTO> memberDTOs = new ArrayList<>();
            for (UserFrequentMember member : members) {
                UserFrequentMemberDTO dto = new UserFrequentMemberDTO();
                BeanUtils.copyProperties(member, dto);
                memberDTOs.add(dto);
            }
            
            // 查询总数
            int total = userFrequentMemberMapper.selectCountByUserId(userId);
            
            return Result.success("获取成功", PageResult.of(total, size, page, memberDTOs));
        } catch (Exception e) {
            return Result.error("获取失败：" + e.getMessage());
        }
    }
    
    @Override
    public Result<PageResult<UserFrequentMemberDTO>> searchUserFrequentMembersByName(Long userId, String name, int page, int size) {
        try {
            // 参数校验
            if (userId == null || userId <= 0) {
                return Result.error("用户ID不能为空");
            }
            if (name == null || name.trim().isEmpty()) {
                return Result.error("姓名不能为空");
            }
            if (page <= 0) {
                page = 1;
            }
            if (size <= 0 || size > 100) {
                size = 10;
            }
            
            int offset = (page - 1) * size;
            List<UserFrequentMember> members = userFrequentMemberMapper.selectByName(userId, name, offset, size);
            
            // 转换为DTO
            List<UserFrequentMemberDTO> memberDTOs = new ArrayList<>();
            for (UserFrequentMember member : members) {
                UserFrequentMemberDTO dto = new UserFrequentMemberDTO();
                BeanUtils.copyProperties(member, dto);
                memberDTOs.add(dto);
            }
            
            // 查询总数
            int total = userFrequentMemberMapper.selectCountByName(userId, name);
            
            return Result.success("搜索成功", PageResult.of(total, size, page, memberDTOs));
        } catch (Exception e) {
            return Result.error("搜索失败：" + e.getMessage());
        }
    }
    
    @Override
    public Result<UserFrequentMemberDTO> getUserFrequentMemberById(Long id) {
        try {
            // 参数校验
            if (id == null || id <= 0) {
                return Result.error("常用预约人ID不能为空");
            }
            
            UserFrequentMember member = userFrequentMemberMapper.selectById(id);
            if (member == null) {
                return Result.error("常用预约人不存在");
            }
            
            UserFrequentMemberDTO dto = new UserFrequentMemberDTO();
            BeanUtils.copyProperties(member, dto);
            
            return Result.success("获取成功", dto);
        } catch (Exception e) {
            return Result.error("获取失败：" + e.getMessage());
        }
    }
    
    @Override
    public Result<UserFrequentMemberDTO> getDefaultUserFrequentMember(Long userId) {
        try {
            // 参数校验
            if (userId == null || userId <= 0) {
                return Result.error("用户ID不能为空");
            }
            
            UserFrequentMember member = userFrequentMemberMapper.selectDefaultByUserId(userId);
            if (member == null) {
                return Result.error("默认预约人不存在");
            }
            
            UserFrequentMemberDTO dto = new UserFrequentMemberDTO();
            BeanUtils.copyProperties(member, dto);
            
            return Result.success("获取成功", dto);
        } catch (Exception e) {
            return Result.error("获取失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<String> addUserFrequentMember(Long userId, UserFrequentMemberDTO memberDTO, Long createBy) {
        try {
            // 参数校验
            if (userId == null || userId <= 0) {
                return Result.error("用户ID不能为空");
            }
            if (memberDTO == null) {
                return Result.error("常用预约人信息不能为空");
            }
            if (memberDTO.getName() == null || memberDTO.getName().trim().isEmpty()) {
                return Result.error("姓名不能为空");
            }
            if (memberDTO.getIdNumber() == null || memberDTO.getIdNumber().trim().isEmpty()) {
                return Result.error("证件号码不能为空");
            }
            if (memberDTO.getPhone() == null || memberDTO.getPhone().trim().isEmpty()) {
                return Result.error("手机号不能为空");
            }
            
            // 检查是否已存在相同的证件号码
            UserFrequentMember existingMember = userFrequentMemberMapper.selectByIdNumber(userId, memberDTO.getIdNumber());
            if (existingMember != null) {
                return Result.error("该证件号码的预约人已存在");
            }
            
            // 创建实体对象
            UserFrequentMember member = new UserFrequentMember();
            BeanUtils.copyProperties(memberDTO, member);
            member.setUserId(userId);
            member.setCreateTime(LocalDateTime.now());
            member.setUpdateTime(LocalDateTime.now());
            member.setCreateBy(createBy);
            member.setUpdateBy(createBy);
            
            // 设置默认值
            if (member.getIdType() == null) {
                member.setIdType((byte) 1); // 默认身份证
            }
            if (member.getStatus() == null) {
                member.setStatus((byte) 1); // 默认启用
            }
            if (member.getVersion() == null) {
                member.setVersion(0);
            }
            if (member.getDeleted() == null) {
                member.setDeleted((byte) 0);
            }
            
            // 如果是第一个预约人或设置为默认，则设为默认预约人
            int totalCount = userFrequentMemberMapper.selectCountByUserId(userId);
            if (totalCount == 0 || (member.getIsDefault() != null && member.getIsDefault() == 1)) {
                member.setIsDefault((byte) 1);
                // 取消其他预约人的默认状态
                userFrequentMemberMapper.setDefault(userId, 0L, createBy);
            } else {
                member.setIsDefault((byte) 0);
            }
            
            int result = userFrequentMemberMapper.insert(member);
            if (result > 0) {
                return Result.success("添加成功", "常用预约人添加成功");
            } else {
                return Result.error("添加失败");
            }
        } catch (Exception e) {
            return Result.error("添加失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<String> updateUserFrequentMember(Long userId, Long id, UserFrequentMemberDTO memberDTO, Long updateBy) {
        try {
            // 参数校验
            if (userId == null || userId <= 0) {
                return Result.error("用户ID不能为空");
            }
            if (id == null || id <= 0) {
                return Result.error("常用预约人ID不能为空");
            }
            if (memberDTO == null) {
                return Result.error("常用预约人信息不能为空");
            }
            
            // 查询现有记录
            UserFrequentMember existingMember = userFrequentMemberMapper.selectById(id);
            if (existingMember == null) {
                return Result.error("常用预约人不存在");
            }
            
            // 检查用户权限
            if (!existingMember.getUserId().equals(userId)) {
                return Result.error("无权操作该预约人");
            }
            
            // 检查证件号码是否重复（排除自己）
            if (memberDTO.getIdNumber() != null && !memberDTO.getIdNumber().equals(existingMember.getIdNumber())) {
                UserFrequentMember duplicateMember = userFrequentMemberMapper.selectByIdNumber(userId, memberDTO.getIdNumber());
                if (duplicateMember != null) {
                    return Result.error("该证件号码的预约人已存在");
                }
            }
            
            // 更新实体对象
            BeanUtils.copyProperties(memberDTO, existingMember);
            existingMember.setId(id);
            existingMember.setUserId(userId);
            existingMember.setUpdateTime(LocalDateTime.now());
            existingMember.setUpdateBy(updateBy);
            // 保留原有的version值，用于乐观锁
            existingMember.setVersion(existingMember.getVersion());
            
            int result = userFrequentMemberMapper.updateById(existingMember);
            if (result > 0) {
                return Result.success("更新成功", "常用预约人信息更新成功");
            } else if (result == 0) {
                return Result.error("数据已被其他用户修改，请刷新后重试");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<String> updateUserFrequentMemberStatus(Long userId, Long id, Byte status, Long updateBy) {
        try {
            // 参数校验
            if (userId == null || userId <= 0) {
                return Result.error("用户ID不能为空");
            }
            if (id == null || id <= 0) {
                return Result.error("常用预约人ID不能为空");
            }
            if (status == null || (status != 0 && status != 1)) {
                return Result.error("状态值不正确");
            }
            
            // 查询现有记录
            UserFrequentMember existingMember = userFrequentMemberMapper.selectById(id);
            if (existingMember == null) {
                return Result.error("常用预约人不存在");
            }
            
            // 检查用户权限
            if (!existingMember.getUserId().equals(userId)) {
                return Result.error("无权操作该预约人");
            }
            
            int result = userFrequentMemberMapper.updateStatus(id, status, updateBy);
            if (result > 0) {
                return Result.success("更新成功", "常用预约人状态更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<String> setDefaultUserFrequentMember(Long userId, Long id, Long updateBy) {
        try {
            // 参数校验
            if (userId == null || userId <= 0) {
                return Result.error("用户ID不能为空");
            }
            if (id == null || id <= 0) {
                return Result.error("常用预约人ID不能为空");
            }
            
            // 查询现有记录
            UserFrequentMember existingMember = userFrequentMemberMapper.selectById(id);
            if (existingMember == null) {
                return Result.error("常用预约人不存在");
            }
            
            // 检查用户权限
            if (!existingMember.getUserId().equals(userId)) {
                return Result.error("无权操作该预约人");
            }
            
            // 检查预约人状态
            if (existingMember.getStatus() != 1) {
                return Result.error("预约人已被禁用，无法设为默认");
            }
            
            int result = userFrequentMemberMapper.setDefault(userId, id, updateBy);
            if (result > 0) {
                return Result.success("设置成功", "默认预约人设置成功");
            } else {
                return Result.error("设置失败");
            }
        } catch (Exception e) {
            return Result.error("设置失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<String> deleteUserFrequentMember(Long userId, Long id, Long updateBy) {
        try {
            // 参数校验
            if (userId == null || userId <= 0) {
                return Result.error("用户ID不能为空");
            }
            if (id == null || id <= 0) {
                return Result.error("常用预约人ID不能为空");
            }
            
            // 查询现有记录
            UserFrequentMember existingMember = userFrequentMemberMapper.selectById(id);
            if (existingMember == null) {
                return Result.error("常用预约人不存在");
            }
            
            // 检查用户权限
            if (!existingMember.getUserId().equals(userId)) {
                return Result.error("无权操作该预约人");
            }
            
            int result = userFrequentMemberMapper.deleteById(id, updateBy);
            if (result > 0) {
                return Result.success("删除成功", "常用预约人删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
    
    @Override
    public boolean isUserFrequentMemberExistByIdNumber(Long userId, String idNumber) {
        if (userId == null || userId <= 0 || idNumber == null || idNumber.trim().isEmpty()) {
            return false;
        }
        
        UserFrequentMember member = userFrequentMemberMapper.selectByIdNumber(userId, idNumber);
        return member != null;
    }
    
    @Override
    public List<UserFrequentMember> getEnabledUserFrequentMembers(Long userId) {
        if (userId == null || userId <= 0) {
            return new ArrayList<>();
        }
        
        try {
            int offset = 0;
            int size = 100; // 假设最多100个常用预约人
            return userFrequentMemberMapper.selectByUserId(userId, offset, size);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}