package com.scenic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Spring MVC配置类
 * 配置HTTP消息转换器
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 配置消息转换器
     * 确保Jackson能够正确处理Java 8日期时间类型
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建新的ObjectMapper，确保它包含JavaTimeModule
        ObjectMapper messageObjectMapper = objectMapper.copy();
        messageObjectMapper.registerModule(new JavaTimeModule());
        messageObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // 创建新的消息转换器并添加到转换器列表中
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(messageObjectMapper);
        converters.add(0, converter); // 添加到列表开头，确保它优先于默认转换器
    }
}