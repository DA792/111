package com.scenic.service.user.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.appointment.AppointmentResponseDTO;
import com.scenic.dto.user.UserAppointmentPersonDTO;
import com.scenic.dto.user.UserMessageDTO;
import com.scenic.dto.user.UserPhotoDTO;
import com.scenic.entity.appointment.Appointment;
import com.scenic.entity.user.User;
import com.scenic.entity.user.UserAppointmentPerson;
import com.scenic.entity.user.UserMessage;
import com.scenic.entity.user.UserPhoto;
import com.scenic.mapper.appointment.AppointmentMapper;
import com.scenic.mapper.user.UserAppointmentPersonMapper;
import com.scenic.mapper.user.UserMapper;
import com.scenic.mapper.user.UserMessageMapper;
import com.scenic.mapper.user.UserPhotoMapper;
import com.scenic.service.user.UserService;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserPhotoMapper userPhotoMapper;
    
    @Autowired
    private UserMessageMapper userMessageMapper;
    
    @Autowired
    private UserAppointmentPersonMapper userAppointmentPersonMapper;
    
    @Autowired
    private AppointmentMapper appointmentMapper;
    
    @Override
    public Result<String> loginWithWeChat(String code) {
        // 实际项目中需要调用微信接口验证code并获取用户信息
        // 这里简化处理，直接返回成功
        // 注意：实际项目中需要实现具体的业务逻辑
        return Result.success("登录成功", "token123456");
    }
    
    @Override
    public Result<String> register(User user) {
        try {
            // 检查用户是否已存在
            User existingUser = userMapper.selectByOpenId(user.getOpenId());
            if (existingUser != null) {
                return Result.error("用户已存在");
            }
            
            // 设置默认值
            user.setCreateTime(java.time.LocalDateTime.now());
            user.setUpdateTime(java.time.LocalDateTime.now());
            
            // 插入用户
            int result = userMapper.insert(user);
            if (result > 0) {
                return Result.success("注册成功", "用户注册成功");
            } else {
                return Result.error("注册失败");
            }
        } catch (Exception e) {
            return Result.error("注册失败：" + e.getMessage());
        }
    }
    
    @Override
    public Result<User> getUserInfo(Long userId) {
        try {
            User user = userMapper.selectById(userId);
            if (user != null) {
                return Result.success("获取成功", user);
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            return Result.error("获取失败：" + e.getMessage());
        }
    }
    
    @Override
    public Result<String> updateUserInfo(Long userId, User user) {
        try {
            User existingUser = userMapper.selectById(userId);
            if (existingUser == null) {
                return Result.error("用户不存在");
            }
            
            // 更新用户信息
            user.setId(userId);
            user.setUpdateTime(java.time.LocalDateTime.now());
            int result = userMapper.updateById(user);
            if (result > 0) {
                return Result.success("更新成功", "用户信息更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    @Override
    public Result<User> getUserProfile(Long userId) {
        // 实际项目中需要从数据库查询用户信息
        User user = userMapper.selectById(userId);
        return Result.success("获取成功", user);
    }
    
    @Override
    public Result<String> updateUserProfile(Long userId, User user) {
        try {
            User existingUser = userMapper.selectById(userId);
            if (existingUser == null) {
                return Result.error("用户不存在");
            }
            
            // 更新用户信息
            user.setId(userId);
            user.setUpdateTime(java.time.LocalDateTime.now());
            int result = userMapper.updateById(user);
            if (result > 0) {
                return Result.success("更新成功", "用户信息更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    @Override
    public Result<PageResult<AppointmentResponseDTO>> getUserAppointments(Long userId, int page, int size) {
        try {
            // 实际项目中需要从数据库查询用户的预约记录
            int offset = (page - 1) * size;
            List<Appointment> appointments = appointmentMapper.selectByUserId(userId, offset, size);
            
            // 转换为AppointmentResponseDTO
            List<AppointmentResponseDTO> appointmentDTOs = new ArrayList<>();
            for (Appointment appointment : appointments) {
                AppointmentResponseDTO dto = new AppointmentResponseDTO();
                dto.setId(appointment.getId());
                dto.setScenicSpotName(appointment.getScenicSpotName());
                dto.setStatus(appointment.getStatus());
                // 这里可以根据需要设置其他字段
                appointmentDTOs.add(dto);
            }
            
            // 查询总数
            int total = appointmentMapper.selectCountByUserId(userId);
            
            return Result.success("获取成功", PageResult.of(total, size, page, appointmentDTOs));
        } catch (Exception e) {
            return Result.error("获取失败：" + e.getMessage());
        }
    }
    
    @Override
    public Result<String> cancelAppointment(Long appointmentId) {
        try {
            Appointment appointment = appointmentMapper.selectById(appointmentId);
            if (appointment == null) {
                return Result.error("预约不存在");
            }
            
            // 更新预约状态为取消
            appointment.setStatus("CANCELLED");
            appointment.setUpdateTime(java.time.LocalDateTime.now());
            int result = appointmentMapper.updateById(appointment);
            if (result > 0) {
                return Result.success("取消成功", "预约取消成功");
            } else {
                return Result.error("取消失败");
            }
        } catch (Exception e) {
            return Result.error("取消失败：" + e.getMessage());
        }
    }
    
    @Override
    public Result<PageResult<User>> getUsers(int page, int size, String username, String phone) {
        try {
            // 实际项目中需要从数据库查询用户列表
            int offset = (page - 1) * size;
            List<User> users = userMapper.selectList(offset, size, username, phone);
            
            // 查询总数
            int total = userMapper.selectCount(username, phone);
            
            return Result.success("查询成功", PageResult.of(total, size, page, users));
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    @Override
    public Result<User> getUserDetail(Long userId) {
        try {
            User user = userMapper.selectById(userId);
            if (user != null) {
                return Result.success("获取成功", user);
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            return Result.error("获取失败：" + e.getMessage());
        }
    }
    
    @Override
    public Result<String> createUser(User user) {
        try {
            // 检查用户是否已存在
            User existingUser = userMapper.selectByOpenId(user.getOpenId());
            if (existingUser != null) {
                return Result.error("用户已存在");
            }
            
            // 设置默认值
            user.setCreateTime(java.time.LocalDateTime.now());
            user.setUpdateTime(java.time.LocalDateTime.now());
            // user.setEnabled(true);
            
            // 插入用户
            int result = userMapper.insert(user);
            if (result > 0) {
                return Result.success("创建成功", "用户创建成功");
            } else {
                return Result.error("创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建失败：" + e.getMessage());
        }
    }
    
    @Override
    public Result<String> updateUser(Long userId, User user) {
        try {
            User existingUser = userMapper.selectById(userId);
            if (existingUser == null) {
                return Result.error("用户不存在");
            }
            
            // 更新用户信息
            user.setId(userId);
            user.setUpdateTime(java.time.LocalDateTime.now());
            int result = userMapper.updateById(user);
            if (result > 0) {
                return Result.success("更新成功", "用户信息更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    @Override
    public Result<String> deleteUser(Long userId) {
        try {
            User existingUser = userMapper.selectById(userId);
            if (existingUser == null) {
                return Result.error("用户不存在");
            }
            
            // 删除用户
            int result = userMapper.deleteById(userId);
            if (result > 0) {
                return Result.success("删除成功", "用户删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
    
    @Override
    public Result<String> resetPassword(Long userId) {
        try {
            User existingUser = userMapper.selectById(userId);
            if (existingUser == null) {
                return Result.error("用户不存在");
            }
            
            // 重置密码为默认密码
            existingUser.setPassword("123456");
            existingUser.setUpdateTime(java.time.LocalDateTime.now());
            int result = userMapper.updateById(existingUser);
            if (result > 0) {
                return Result.success("重置成功", "密码重置成功");
            } else {
                return Result.error("重置失败");
            }
        } catch (Exception e) {
            return Result.error("重置失败：" + e.getMessage());
        }
    }
    
    @Override
    public Result<PageResult<UserPhotoDTO>> getUserPhotos(Long userId, int page, int size) {
        // 实际项目中需要从数据库查询用户发布的照片
        int offset = (page - 1) * size;
        List<UserPhoto> photos = userPhotoMapper.selectByUserId(userId, offset, size);
        
        // 转换为DTO
        List<UserPhotoDTO> photoDTOs = new ArrayList<>();
        for (UserPhoto photo : photos) {
            UserPhotoDTO dto = new UserPhotoDTO();
            dto.setId(photo.getId());
            dto.setUserId(photo.getUserId());
            dto.setPhotoUrl(photo.getPhotoUrl());
            dto.setDescription(photo.getDescription());
            dto.setCategory(photo.getCategory());
            dto.setLatitude(photo.getLatitude());
            dto.setLongitude(photo.getLongitude());
            dto.setLikes(photo.getLikes());
            dto.setEnabled(photo.getEnabled());
            dto.setCreateTime(photo.getCreateTime());
            dto.setUpdateTime(photo.getUpdateTime());
            photoDTOs.add(dto);
        }
        
        // 查询总数
        int total = userPhotoMapper.selectCountByUserId(userId);
        
        return Result.success("获取成功", PageResult.of(total, size, page, photoDTOs));
    }
    
    @Override
    public Result<PageResult<UserMessageDTO>> getUserMessages(Long userId, String type, int page, int size) {
        // 实际项目中需要从数据库查询用户的消息
        int offset = (page - 1) * size;
        List<UserMessage> messages = userMessageMapper.selectByUserId(userId, type, offset, size);
        
        // 转换为DTO
        List<UserMessageDTO> messageDTOs = new ArrayList<>();
        for (UserMessage message : messages) {
            UserMessageDTO dto = new UserMessageDTO();
            dto.setId(message.getId());
            dto.setUserId(message.getUserId());
            dto.setRelatedUserId(message.getRelatedUserId());
            dto.setRelatedUserName(message.getRelatedUserName());
            dto.setType(message.getType());
            dto.setContent(message.getContent());
            dto.setRelatedId(message.getRelatedId());
            dto.setIsRead(message.getIsRead());
            dto.setCreateTime(message.getCreateTime());
            dto.setUpdateTime(message.getUpdateTime());
            messageDTOs.add(dto);
        }
        
        // 查询总数
        int total = userMessageMapper.selectCountByUserId(userId, type);
        
        return Result.success("获取成功", PageResult.of(total, size, page, messageDTOs));
    }
    
    @Override
    public Result<String> markMessageAsRead(Long messageId) {
        // 实际项目中需要更新数据库中的消息状态
        UserMessage message = userMessageMapper.selectById(messageId);
        if (message != null) {
            message.setIsRead(true);
            userMessageMapper.updateById(message);
            return Result.success("更新成功", "消息标记为已读");
        }
        return Result.error("消息不存在");
    }
    
    @Override
    public Result<PageResult<UserAppointmentPersonDTO>> getUserAppointmentPersons(Long userId, int page, int size) {
        // 实际项目中需要从数据库查询用户的预约人
        int offset = (page - 1) * size;
        List<UserAppointmentPerson> persons = userAppointmentPersonMapper.selectByUserId(userId, offset, size);
        
        // 转换为DTO
        List<UserAppointmentPersonDTO> personDTOs = new ArrayList<>();
        for (UserAppointmentPerson person : persons) {
            UserAppointmentPersonDTO dto = new UserAppointmentPersonDTO();
            dto.setId(person.getId());
            dto.setUserId(person.getUserId());
            dto.setName(person.getName());
            dto.setIdCard(person.getIdCard());
            dto.setPhone(person.getPhone());
            dto.setAge(person.getAge());
            dto.setRelationship(person.getRelationship());
            dto.setIsDefault(person.getIsDefault());
            dto.setCreateTime(person.getCreateTime());
            dto.setUpdateTime(person.getUpdateTime());
            personDTOs.add(dto);
        }
        
        // 查询总数
        int total = userAppointmentPersonMapper.selectCountByUserId(userId);
        
        return Result.success("获取成功", PageResult.of(total, size, page, personDTOs));
    }
    
    @Override
    public Result<String> addUserAppointmentPerson(Long userId, UserAppointmentPersonDTO person) {
        // 实际项目中需要向数据库添加预约人
        UserAppointmentPerson userAppointmentPerson = new UserAppointmentPerson();
        userAppointmentPerson.setUserId(person.getUserId());
        userAppointmentPerson.setName(person.getName());
        userAppointmentPerson.setIdCard(person.getIdCard());
        userAppointmentPerson.setPhone(person.getPhone());
        userAppointmentPerson.setAge(person.getAge());
        userAppointmentPerson.setRelationship(person.getRelationship());
        userAppointmentPerson.setIsDefault(person.getIsDefault());
        userAppointmentPerson.setCreateTime(person.getCreateTime());
        userAppointmentPerson.setUpdateTime(person.getUpdateTime());
        
        int result = userAppointmentPersonMapper.insert(userAppointmentPerson);
        if (result > 0) {
            return Result.success("添加成功", "预约人添加成功");
        }
        return Result.error("添加失败");
    }
    
    @Override
    public Result<String> updateUserAppointmentPerson(Long userId, Long personId, UserAppointmentPersonDTO person) {
        // 实际项目中需要更新数据库中的预约人信息
        UserAppointmentPerson userAppointmentPerson = userAppointmentPersonMapper.selectById(personId);
        if (userAppointmentPerson != null) {
            userAppointmentPerson.setName(person.getName());
            userAppointmentPerson.setIdCard(person.getIdCard());
            userAppointmentPerson.setPhone(person.getPhone());
            userAppointmentPerson.setAge(person.getAge());
            userAppointmentPerson.setRelationship(person.getRelationship());
            userAppointmentPerson.setIsDefault(person.getIsDefault());
            userAppointmentPerson.setUpdateTime(person.getUpdateTime());
            
            int result = userAppointmentPersonMapper.updateById(userAppointmentPerson);
            if (result > 0) {
                return Result.success("更新成功", "预约人信息更新成功");
            }
        }
        return Result.error("更新失败");
    }
    
    @Override
    public Result<String> deleteUserAppointmentPerson(Long userId, Long personId) {
        // 实际项目中需要从数据库删除预约人
        int result = userAppointmentPersonMapper.deleteById(personId);
        if (result > 0) {
            return Result.success("删除成功", "预约人删除成功");
        }
        return Result.error("删除失败");
    }
}
