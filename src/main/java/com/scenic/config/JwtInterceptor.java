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
        String method = request.getMethod();
        
        System.out.println("JWT拦截器处理请求: " + method + " " + requestURI);
        
        // 放行管理后台登录接口和注册接口
        if (requestURI.equals("/api/manage/login") || 
            requestURI.contains("/register")) {
            System.out.println("放行登录或注册接口: " + requestURI);
            return true;
        }
        
        // 小程序端请求不需要JWT验证，直接放行
        if (requestURI.startsWith(miniappPrefix)) {
            System.out.println("放行小程序接口: " + requestURI);
            return true;
        }
        
        // 管理后台的公开接口（如公园开放时间接口）是否需要特殊处理？
        // 如果管理后台有不需要认证的接口，应该在这里添加排除逻辑
        if (requestURI.startsWith("/api/manage/park-open-time")) {
            System.out.println("放行公园开放时间接口: " + requestURI);
            return true;
        }
        
        // 获取请求头中的Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("缺少Authorization头或格式不正确: " + authHeader);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"timestamp\":\"" + java.time.Instant.now() + "\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"未授权：缺少有效的认证令牌\",\"path\":\"" + requestURI + "\"}");
            return false;
        }
        
        // 提取JWT令牌
        String token = authHeader.substring(7);
        
        // 验证管理后台JWT令牌
        if (requestURI.startsWith(adminPrefix)) {
            System.out.println("验证管理后台令牌: " + requestURI);
            if (!jwtUtil.validateAdminToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"timestamp\":\"" + java.time.Instant.now() + "\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"未授权：管理后台认证令牌无效或已过期\",\"path\":\"" + requestURI + "\"}");
                return false;
            }
        } else {
            // 其他路径，默认需要认证
            System.out.println("无法识别的请求路径: " + requestURI);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"timestamp\":\"" + java.time.Instant.now() + "\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"未授权：无法识别的请求路径\",\"path\":\"" + requestURI + "\"}");
            return false;
        }
        
        System.out.println("JWT验证通过: " + requestURI);
        return true;
    }
}
