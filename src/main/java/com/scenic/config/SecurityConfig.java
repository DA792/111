package com.scenic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security配置类
 * 禁用默认的安全配置，使用自定义的JWT拦截器进行认证
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()  // 禁用CSRF保护
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 不使用session
            .and()
            .authorizeRequests()
            .antMatchers("/**").permitAll()  // 允许所有请求通过，认证由JWT拦截器处理
            .anyRequest().authenticated();
    }
    
    /**
     * 配置BCrypt密码编码器
     * @return PasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}