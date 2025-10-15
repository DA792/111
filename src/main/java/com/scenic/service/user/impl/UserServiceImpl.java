package com.scenic.service.user.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.config.WechatConfig;
import com.scenic.dto.appointment.AppointmentResponseDTO;
import com.scenic.dto.user.UserAppointmentPersonDTO;
import com.scenic.dto.user.UserMessageDTO;
import com.scenic.dto.user.UserPhotoDTO;
import com.scenic.dto.user.WechatLoginRequestDTO;
import com.scenic.dto.user.WechatLoginResponseDTO;
import com.scenic.dto.user.WechatSessionDTO;
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
import com.scenic.utils.JwtUtil;
import com.scenic.utils.PasswordUtil;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
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
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordUtil passwordUtil;
    
    @Autowired
    private WechatConfig wechatConfig;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // Redis中存储微信会话信息的key前缀
    private static final String WECHAT_SESSION_KEY_PREFIX = "wechat:session:";
    private static final String WECHAT_TOKEN_KEY_PREFIX = "wechat:token:";
    
    @Override
    public Result<Object> loginWithUsernameAndPassword(String username, String password) {
        try {
            // 从数据库查询用户
            User user = userMapper.selectByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 使用BCrypt验证密码
            if (!passwordUtil.matches(password, user.getPassword())) {
                return Result.error("密码错误");
            }
            
            // 验证用户是否为管理员
            if (user.getUserType() == null || user.getUserType() != 2) {
                return Result.error("非管理员用户，无权登录管理后台");
            }
            
            // 更新最后登录时间
            user.setLastLoginTime(java.time.LocalDateTime.now());
            userMapper.updateById(user);
            
            // 生成JWT token
            String token = jwtUtil.generateAdminToken(username, user.getId());
            
            // 构建返回对象，包含token和用户信息
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("token", token);
            resultMap.put("user", user);
            
            return Result.success("登录成功", resultMap);
        } catch (Exception e) {
            return Result.error("登录失败：" + e.getMessage());
        }
    }
    
    @Override
    public Result<Object> loginWithWeChat(String code) {
        try {
            // 创建微信登录请求DTO
            WechatLoginRequestDTO loginRequest = new WechatLoginRequestDTO();
            loginRequest.setCode(code);
            
            // 调用微信登录
            WechatLoginResponseDTO loginResponse = loginWithWeChat(loginRequest);
            
            if (loginResponse == null) {
                return Result.error("微信登录失败");
            }
            
            // 构建返回对象，包含token和用户信息
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("token", loginResponse.getToken());
            resultMap.put("userId", loginResponse.getUserId());
            resultMap.put("userName", loginResponse.getUserName());
            resultMap.put("realName", loginResponse.getRealName());
            resultMap.put("userType", loginResponse.getUserType());
            resultMap.put("status", loginResponse.getStatus());
            resultMap.put("avatarUrl", loginResponse.getAvatarUrl());
            resultMap.put("isNewUser", loginResponse.getIsNewUser());
            
            return Result.success("登录成功", resultMap);
        } catch (Exception e) {
            return Result.error("微信登录失败：" + e.getMessage());
        }
    }
    
    @Override
    public WechatSessionDTO getWechatSession(String code) {
        try {
            // 构建请求URL
            String url = wechatConfig.getCode2SessionUrl() +
                    "?appid=" + wechatConfig.getAppId() +
                    "&secret=" + wechatConfig.getAppSecret() +
                    "&js_code=" + code +
                    "&grant_type=authorization_code";
            
            // 发送请求
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            // 解析响应
            WechatSessionDTO sessionDTO = objectMapper.readValue(response.getBody(), WechatSessionDTO.class);
            
            if (!sessionDTO.isSuccess()) {
                logger.error("获取微信会话失败: {}", sessionDTO.getErrmsg());
            }
            
            return sessionDTO;
        } catch (Exception e) {
            logger.error("获取微信会话异常", e);
            WechatSessionDTO errorSession = new WechatSessionDTO();
            errorSession.setErrcode(-1);
            errorSession.setErrmsg("系统异常: " + e.getMessage());
            return errorSession;
        }
    }
    
    @Override
    @Transactional
    public WechatLoginResponseDTO loginWithWeChat(WechatLoginRequestDTO loginRequest) {
        // 获取微信会话信息
        WechatSessionDTO sessionDTO = getWechatSession(loginRequest.getCode());
        
        if (!sessionDTO.isSuccess()) {
            WechatLoginResponseDTO errorResponse = new WechatLoginResponseDTO();
            errorResponse.setIsNewUser(false);
            return errorResponse;
        }
        
        String openid = sessionDTO.getOpenid();
        
        try {
            // 先从Redis中查询用户会话信息
            Map<String, Object> sessionInfo = getUserSessionFromRedis(openid);
            
            if (sessionInfo != null) {
                logger.info("从Redis中获取到用户会话信息: {}", openid);
                
                // 从会话信息中获取用户ID
                Long userId = (Long) sessionInfo.get("userId");
                
                // 从数据库中获取最新的用户信息，确保数据一致性
                User user = userMapper.selectById(userId);
                if (user == null) {
                    // Redis中有记录但数据库中没有，说明数据不一致
                    // 清除Redis中的记录
                    clearUserSessionFromRedis(userId, openid);
                    // 重新走数据库查询流程
                    return handleDatabaseLogin(openid, sessionDTO, loginRequest);
                }
                
                // 获取用户信息
                WechatLoginResponseDTO existingUserResponse = convertUserToResponseDTO(user);
                existingUserResponse.setIsNewUser(false);
                
                // 生成新的JWT令牌
                String token = jwtUtil.generateMiniAppToken(userId, openid);
                existingUserResponse.setToken(token);
                
                // 更新Redis中的会话信息
                saveSessionToRedis(userId, openid, sessionDTO.getSessionKey(), token);
                
                // 更新用户最后登录时间
                user.setLastLoginTime(LocalDateTime.now());
                userMapper.updateById(user);
                
                return existingUserResponse;
            }
            
            // Redis中不存在，走数据库查询流程
            return handleDatabaseLogin(openid, sessionDTO, loginRequest);
            
        } catch (Exception e) {
            logger.error("微信登录处理异常", e);
            throw new RuntimeException("微信登录处理异常: " + e.getMessage());
        }
    }
    
    /**
     * 处理数据库登录流程
     */
    @Transactional
    private WechatLoginResponseDTO handleDatabaseLogin(String openid, WechatSessionDTO sessionDTO, WechatLoginRequestDTO loginRequest) {
        // 检查数据库中用户是否存在
        boolean isExist = isUserExistByOpenid(openid);
        
        if (!isExist) {
            // 创建新用户
            Long userId = createUser(openid, loginRequest);
            
            // 获取用户信息
            User user = userMapper.selectById(userId);
            WechatLoginResponseDTO newUserResponse = convertUserToResponseDTO(user);
            newUserResponse.setIsNewUser(true);
            
            // 生成JWT令牌
            String token = jwtUtil.generateMiniAppToken(userId, openid);
            newUserResponse.setToken(token);
            
            // 将会话信息存储到Redis中
            saveSessionToRedis(userId, openid, sessionDTO.getSessionKey(), token);
            
            return newUserResponse;
        } else {
            // 获取用户信息
            User user = userMapper.selectByOpenId(openid);
            WechatLoginResponseDTO existingUserResponse = convertUserToResponseDTO(user);
            existingUserResponse.setIsNewUser(false);
            
            // 生成JWT令牌
            String token = jwtUtil.generateMiniAppToken(user.getId(), openid);
            existingUserResponse.setToken(token);
            
            // 将会话信息存储到Redis中
            saveSessionToRedis(user.getId(), openid, sessionDTO.getSessionKey(), token);
            
            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            userMapper.updateById(user);
            
            return existingUserResponse;
        }
    }
    
    /**
     * 将User对象转换为WechatLoginResponseDTO
     */
    private WechatLoginResponseDTO convertUserToResponseDTO(User user) {
        if (user == null) {
            return null;
        }
        
        WechatLoginResponseDTO responseDTO = new WechatLoginResponseDTO();
        responseDTO.setUserId(user.getId());
        responseDTO.setUserName(user.getUserName());
        responseDTO.setRealName(user.getRealName());
        responseDTO.setUserType(user.getUserType());
        responseDTO.setStatus(user.getStatus());
        
        // 获取头像URL
        if (user.getAvatarFileId() != null) {
            responseDTO.setAvatarUrl("/api/files/avatar/" + user.getId());
        }
        
        return responseDTO;
    }
    
    @Override
    public boolean isUserExistByOpenid(String openid) {
        return userMapper.countByOpenId(openid) > 0;
    }
    
    @Override
    @Transactional
    public Long createUser(String openid, WechatLoginRequestDTO loginRequest) {
        User user = new User();
        
        // 设置基本信息
        user.setOpenId(openid);
        user.setUserName("wx_user_" + openid.substring(openid.length() - 8));
        user.setRealName(loginRequest.getNickName());
        user.setUserType(1); // 普通用户
        user.setStatus(1); // 正常状态
        user.setRegisterTime(LocalDateTime.now());
        user.setLastLoginTime(LocalDateTime.now());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setDeleted(0);
        user.setVersion(0);
        
        // 保存用户
        userMapper.insert(user);
        
        return user.getId();
    }
    
    @Override
    public WechatLoginResponseDTO getUserInfo(String openid) {
        User user = userMapper.selectByOpenId(openid);
        
        if (user == null) {
            return null;
        }
        
        WechatLoginResponseDTO responseDTO = new WechatLoginResponseDTO();
        responseDTO.setUserId(user.getId());
        responseDTO.setUserName(user.getUserName());
        responseDTO.setRealName(user.getRealName());
        responseDTO.setUserType(user.getUserType());
        responseDTO.setStatus(user.getStatus());
        
        // 获取头像URL
        if (user.getAvatarFileId() != null) {
            // 这里需要根据实际情况获取头像URL
            // 可以调用FileController中的方法获取
            responseDTO.setAvatarUrl("/api/files/avatar/" + user.getId());
        }
        
        return responseDTO;
    }
    
    /**
     * 从Redis中获取用户会话信息
     *
     * @param openid 微信openid
     * @return 会话信息Map，不存在则返回null
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getUserSessionFromRedis(String openid) {
        try {
            String sessionKey = WECHAT_SESSION_KEY_PREFIX + openid;
            Object sessionObj = redisTemplate.opsForValue().get(sessionKey);
            
            if (sessionObj != null) {
                return (Map<String, Object>) sessionObj;
            }
            
            return null;
        } catch (Exception e) {
            logger.error("从Redis获取用户会话信息失败", e);
            return null;
        }
    }
    
    /**
     * 清除Redis中的用户会话信息
     *
     * @param userId 用户ID
     * @param openid 微信openid
     */
    private void clearUserSessionFromRedis(Long userId, String openid) {
        try {
            // 获取旧的会话信息，以便清除旧的token
            Map<String, Object> oldSessionInfo = getUserSessionFromRedis(openid);
            if (oldSessionInfo != null && oldSessionInfo.containsKey("token")) {
                String oldToken = (String) oldSessionInfo.get("token");
                String oldTokenKey = WECHAT_TOKEN_KEY_PREFIX + oldToken;
                redisTemplate.delete(oldTokenKey);
            }
            
            // 清除会话信息
            String sessionKey1 = WECHAT_SESSION_KEY_PREFIX + userId;
            String sessionKey2 = WECHAT_SESSION_KEY_PREFIX + openid;
            redisTemplate.delete(sessionKey1);
            redisTemplate.delete(sessionKey2);
            
            logger.info("用户 {} 的微信会话信息已从Redis中清除", userId);
        } catch (Exception e) {
            logger.error("清除Redis中的用户会话信息失败", e);
        }
    }
    
    /**
     * 将微信会话信息存储到Redis中
     *
     * @param userId 用户ID
     * @param openid 微信openid
     * @param sessionKey 微信会话密钥
     * @param token JWT令牌
     */
    private void saveSessionToRedis(Long userId, String openid, String sessionKey, String token) {
        try {
            // 先清除旧的会话信息
            clearUserSessionFromRedis(userId, openid);
            
            // 存储会话信息
            String sessionKey1 = WECHAT_SESSION_KEY_PREFIX + userId;
            String sessionKey2 = WECHAT_SESSION_KEY_PREFIX + openid;
            
            // 创建会话信息Map
            Map<String, Object> sessionInfo = new HashMap<>();
            sessionInfo.put("userId", userId);
            sessionInfo.put("openid", openid);
            sessionInfo.put("sessionKey", sessionKey);
            sessionInfo.put("token", token);
            sessionInfo.put("loginTime", System.currentTimeMillis());
            
            // 存储会话信息，有效期24小时
            redisTemplate.opsForValue().set(sessionKey1, sessionInfo, 24, TimeUnit.HOURS);
            redisTemplate.opsForValue().set(sessionKey2, sessionInfo, 24, TimeUnit.HOURS);
            
            // 存储token，有效期24小时
            String tokenKey = WECHAT_TOKEN_KEY_PREFIX + token;
            redisTemplate.opsForValue().set(tokenKey, userId, 24, TimeUnit.HOURS);
            
            logger.info("用户 {} 的微信会话信息已存储到Redis", userId);
        } catch (Exception e) {
            logger.error("存储微信会话信息到Redis失败", e);
        }
    }
    
    @Override
    public Result<String> register(User user) {
        try {
            // 检查用户是否已存在
            User existingUser = userMapper.selectByOpenId(user.getOpenId());
            if (existingUser != null) {
                return Result.error("用户已存在");
            }
            
            // 对密码进行BCrypt加密
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                String encodedPassword = passwordUtil.encodePassword(user.getPassword());
                user.setPassword(encodedPassword);
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
            
            // 对密码进行BCrypt加密
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                String encodedPassword = passwordUtil.encodePassword(user.getPassword());
                user.setPassword(encodedPassword);
            }
            
            // 设置默认值
            user.setCreateTime(java.time.LocalDateTime.now());
            user.setUpdateTime(java.time.LocalDateTime.now());
            user.setStatus(1); // 设置状态为启用
            
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
            
            // 重置密码为默认密码，并使用BCrypt加密
            String encodedPassword = passwordUtil.encodePassword("123456");
            existingUser.setPassword(encodedPassword);
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