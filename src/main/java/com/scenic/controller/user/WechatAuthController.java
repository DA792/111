package com.scenic.controller.user;

import com.scenic.common.dto.Result;
import com.scenic.dto.user.WechatLoginRequestDTO;
import com.scenic.dto.user.WechatLoginResponseDTO;
import com.scenic.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信认证控制器
 */
@RestController
@RequestMapping("/api/uniapp/auth")
public class WechatAuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(WechatAuthController.class);
    
    @Autowired
    private UserService userService;
    
    /**
     * 微信小程序登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @PostMapping("/wechat/login")
    public Result<WechatLoginResponseDTO> login(@RequestBody WechatLoginRequestDTO loginRequest) {
        try {
            logger.info("微信小程序登录请求开始，code: {}", loginRequest.getCode());
            
            if (loginRequest.getCode() == null || loginRequest.getCode().isEmpty()) {
                logger.warn("登录失败: code不能为空");
                return Result.error("登录失败: code不能为空");
            }
            
            long startTime = System.currentTimeMillis();
            WechatLoginResponseDTO loginResponse = userService.loginWithWeChat(loginRequest);
            long endTime = System.currentTimeMillis();
            
            logger.info("微信小程序登录处理完成，耗时: {}ms", (endTime - startTime));
            
            if (loginResponse == null) {
                logger.error("登录失败: 获取微信用户信息失败");
                return Result.error("登录失败: 获取微信用户信息失败");
            }
            
            if (loginResponse.getToken() == null) {
                logger.error("登录失败: 生成令牌失败");
                return Result.error("登录失败: 生成令牌失败");
            }
            
            logger.info("微信小程序登录成功，用户ID: {}, 是否新用户: {}", 
                       loginResponse.getUserId(), loginResponse.getIsNewUser());
            
            return Result.success(loginResponse);
        } catch (Exception e) {
            logger.error("微信小程序登录异常", e);
            return Result.error("登录失败: " + e.getMessage());
        }
    }
}