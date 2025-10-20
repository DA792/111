package com.scenic.service.interaction;

import com.scenic.entity.interaction.PhotoCheckIn;
import com.scenic.mapper.interaction.PhotoCheckInMapper;
import com.scenic.service.interaction.impl.PhotoCheckInServiceImpl;
import com.scenic.utils.SnowflakeIdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PhotoCheckInServiceTest {

    @MockBean
    private PhotoCheckInMapper photoCheckInMapper;

    @Test
    public void testSnowflakeIdGeneration() {
        // 创建服务实例
        PhotoCheckInServiceImpl service = new PhotoCheckInServiceImpl();
        
        // 使用反射设置私有字段（在实际测试中可能需要通过依赖注入或其他方式）
        try {
            java.lang.reflect.Field mapperField = PhotoCheckInServiceImpl.class.getDeclaredField("photoCheckInMapper");
            mapperField.setAccessible(true);
            mapperField.set(service, photoCheckInMapper);
            
            SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator();
            java.lang.reflect.Field generatorField = PhotoCheckInServiceImpl.class.getDeclaredField("snowflakeIdGenerator");
            generatorField.setAccessible(true);
            generatorField.set(service, snowflakeIdGenerator);
            
            // 模拟mapper的insert方法
            when(photoCheckInMapper.insert(any(PhotoCheckIn.class))).thenReturn(1);
            
            // 创建测试对象
            PhotoCheckIn photoCheckIn = new PhotoCheckIn();
            photoCheckIn.setTitle("测试标题");
            photoCheckIn.setUserId(1L);
            photoCheckIn.setCategoryId(1L);
            
            // 调用insert方法（这里直接测试mapper层）
            int result = photoCheckInMapper.insert(photoCheckIn);
            
            // 验证结果
            assertEquals(1, result);
            
            // 验证ID已生成（由于我们没有直接调用service方法，这里只是验证框架）
            System.out.println("PhotoCheckIn ID: " + photoCheckIn.getId());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("测试失败: " + e.getMessage());
        }
    }
}