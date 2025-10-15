package com.scenic.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.scenic.entity.user.User;
import com.scenic.utils.JwtUtil;
import com.scenic.utils.UserContextUtil;

import io.jsonwebtoken.Claims;

/**
 * JWT拦截器，用于验证请求中的JWT令牌
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserContextUtil userContextUtil;
    
    @Value("${miniapp.api.prefix}")
    private String miniappPrefix;
    
    @Value("${admin.api.prefix}")
    private String adminPrefix;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        
        // 放行管理后台登录接口、注册接口和头像API
        if (requestURI.equals("/api/manage/login") || 
            requestURI.contains("/register") ||
            requestURI.startsWith("/api/files/avatar/")) {
            return true;
        }
        
        // 小程序端请求不需要JWT验证，直接放行
        if (requestURI.startsWith(miniappPrefix)) {
            return true;
        }
        
        // 获取请求头中的Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("未授权：缺少有效的认证令牌");
            return false;
        }
        
        // 提取JWT令牌
        String token = authHeader.substring(7);
        
        // 验证管理后台JWT令牌
        if (requestURI.startsWith(adminPrefix)) {
            if (!jwtUtil.validateAdminToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("未授权：管理后台认证令牌无效或已过期");
                return false;
            }
            
            // 解析token并设置当前用户信息
            try {
                Claims claims = jwtUtil.getAllClaimsFromToken(token, jwtUtil.getAdminSecret());
                User user = userContextUtil.createUserFromClaims(claims);
                userContextUtil.setCurrentUser(user);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("未授权：无法解析用户信息");
                return false;
            }
        } else {
            // 其他路径，默认需要认证
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("未授权：无法识别的请求路径");
            return false;
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后清除用户上下文，防止内存泄漏
        userContextUtil.clear();
    }
}