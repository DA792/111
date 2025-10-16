package com.scenic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Jackson配置类
 * 解决雪花算法生成的Long类型ID在前端JavaScript中精度丢失的问题
 * 并配置日期格式化
 */
@Configuration
public class JacksonConfig {

    /**
     * 配置ObjectMapper，将Long类型序列化为String类型
     * 避免前端JavaScript处理大整数时的精度丢失问题
     * 同时配置日期格式化
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        
        // 设置日期格式和时区
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        
        // 创建自定义模块
        SimpleModule module = new SimpleModule();
        
        // 为Long类型添加序列化器
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        
        // 注册模块
        objectMapper.registerModule(module);
        
        // 注册JavaTimeModule用于处理新的时间API
        objectMapper.registerModule(new JavaTimeModule());
        
        return objectMapper;
    }
}