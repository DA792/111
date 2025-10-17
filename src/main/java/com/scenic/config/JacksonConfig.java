package com.scenic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson配置类
 * 用于处理Java 8日期时间API的序列化
 */
@Configuration
public class JacksonConfig {

    /**
     * 配置ObjectMapper以支持Java 8日期时间类型
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        
        // 注册JavaTimeModule以支持LocalDateTime等Java 8日期时间类型
        objectMapper.registerModule(new JavaTimeModule());
        
        // 禁用将日期写为时间戳的功能，使用ISO-8601格式
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return objectMapper;
    }
}
