package com.scenic.utils;

import com.scenic.entity.user.User;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户上下文工具类
 * 用于在服务层获取当前登录用户信息
 */
@Component
public class UserContextUtil {
    
    private static final ThreadLocal<User> userContext = new ThreadLocal<>();
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 设置当前用户信息
     * @param user 用户信息
     */
    public void setCurrentUser(User user) {
        userContext.set(user);
    }
    
    /**
     * 获取当前用户信息
     * @return 当前用户信息
     */
    public User getCurrentUser() {
        return userContext.get();
    }
    
    /**
     * 获取当前用户ID
     * @return 当前用户ID
     */
    public Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
    
    /**
     * 获取当前用户名
     * @return 当前用户名
     */
    public String getCurrentUsername() {
        User user = getCurrentUser();
        return user != null ? user.getUserName() : null;
    }
    
    /**
     * 清除当前用户信息
     */
    public void clear() {
        userContext.remove();
    }
    
    /**
     * 从JWT Claims中创建用户对象
     * @param claims JWT Claims
     * @return 用户对象
     */
    public User createUserFromClaims(Claims claims) {
        User user = new User();
        user.setId(claims.get("userId", Long.class));
        user.setUserName(claims.get("username", String.class));
        return user;
    }
}