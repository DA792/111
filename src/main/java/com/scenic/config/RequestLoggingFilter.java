package com.scenic.config;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 请求日志过滤器
 * 用于记录所有传入的HTTP请求
 */
@Component
public class RequestLoggingFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("RequestLoggingFilter 初始化");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // 记录请求信息
        logger.info("收到请求: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
        logger.info("请求来源: {}", httpRequest.getRemoteAddr());
        logger.info("User-Agent: {}", httpRequest.getHeader("User-Agent"));
        
        // 继续处理请求
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        logger.info("RequestLoggingFilter 销毁");
    }
}