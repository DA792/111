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
        String method = request.getMethod();
        
        System.out.println("JWT拦截器处理请求: " + method + " " + requestURI);
        
        // 放行管理后台登录接口、注册接口
        if (requestURI.equals("/api/manage/login") || 
            requestURI.contains("/register")) {
            System.out.println("放行登录/注册接口: " + requestURI);
            return true;
        }
        
        // 放行头像API的GET请求 - 无需验证JWT
        if ((requestURI.startsWith("/api/avatar/") || requestURI.startsWith("/api/files/avatar/")) && method.equals("GET")) {
            System.out.println("放行头像API的GET请求: " + requestURI);
            return true;
        }
        
        // 放行文件获取API的GET请求
        if (requestURI.startsWith("/api/file/") && method.equals("GET")) {
            System.out.println("放行文件API的GET请求: " + requestURI);
            return true;
        }
        
        
        // 小程序端请求不需要JWT验证，直接放行
        if (requestURI.startsWith(miniappPrefix)) {
            System.out.println("放行小程序接口: " + requestURI);
            // 从小程序请求参数中获取userId并设置到用户上下文
            // 支持两种格式：userId 和 params[userId]
            String userIdParam = request.getParameter("userId");
            if (userIdParam == null || userIdParam.isEmpty()) {
                userIdParam = request.getParameter("params[userId]");
            }
            
            if (userIdParam != null && !userIdParam.isEmpty() && !"undefined".equals(userIdParam)) {
                try {
                    Long userId = Long.parseLong(userIdParam);
                    User user = new User();
                    user.setId(userId);
                    userContextUtil.setCurrentUser(user);
                    System.out.println("设置小程序用户上下文，userId: " + userId);
                } catch (NumberFormatException e) {
                    System.out.println("无效的userId参数: " + userIdParam);
                }
            } else {
                System.out.println("小程序请求未携带userId参数，允许匿名访问");
            }
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
        if (requestURI.startsWith(adminPrefix) || requestURI.startsWith("/api/content/") || requestURI.equals("/api/upload") || requestURI.matches("/api/avatar/\\d+/upload")) {
            if (!jwtUtil.validateAdminToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"timestamp\":\"" + java.time.Instant.now() + "\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"未授权：管理后台认证令牌无效或已过期\",\"path\":\"" + requestURI + "\"}");
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
            System.out.println("无法识别的请求路径: " + requestURI);
            // 如果是头像API的GET请求，放行
            if ((requestURI.startsWith("/api/avatar/") || requestURI.startsWith("/api/files/avatar/")) && method.equals("GET")) {
                System.out.println("放行头像API的GET请求: " + requestURI);
                return true;
            }
            
            // 如果是文件API的GET请求，放行
            if (requestURI.startsWith("/api/file/") && method.equals("GET")) {
                System.out.println("放行文件API的GET请求: " + requestURI);
                return true;
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"timestamp\":\"" + java.time.Instant.now() + "\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"未授权：无法识别的请求路径\",\"path\":\"" + requestURI + "\"}");
            return false;
        }
        
        System.out.println("JWT验证通过: " + requestURI);
        return true;
    }

    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后清除用户上下文，防止内存泄漏
        userContextUtil.clear();
    }

}