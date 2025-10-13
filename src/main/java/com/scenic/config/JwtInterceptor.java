package com.scenic.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.scenic.utils.JwtUtil;

/**
 * JWT拦截器，用于验证请求中的JWT令牌
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Value("${miniapp.api.prefix}")
    private String miniappPrefix;
    
    @Value("${admin.api.prefix}")
    private String adminPrefix;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        
        // 放行登录接口和注册接口
        if (requestURI.equals("/api/manage/login") || 
            requestURI.startsWith("/api/uniapp/login") || 
            requestURI.contains("/register")) {
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
        
        // 根据请求路径判断是小程序请求还是管理后台请求
        if (requestURI.startsWith(miniappPrefix)) {
            // 验证小程序JWT令牌
            if (!jwtUtil.validateMiniappToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("未授权：小程序认证令牌无效或已过期");
                return false;
            }
        } else if (requestURI.startsWith(adminPrefix)) {
            // 验证管理后台JWT令牌
            if (!jwtUtil.validateAdminToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("未授权：管理后台认证令牌无效或已过期");
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
}