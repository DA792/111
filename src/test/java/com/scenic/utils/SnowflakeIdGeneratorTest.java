package com.scenic.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SnowflakeIdGeneratorTest {

    @Test
    public void testNextId() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator();
        
        // 生成多个ID测试
        long id1 = generator.nextId();
        long id2 = generator.nextId();
        long id3 = generator.nextId();
        
        // 验证ID不为0
        assertNotEquals(0L, id1);
        assertNotEquals(0L, id2);
        assertNotEquals(0L, id3);
        
        // 验证ID递增
        assertTrue(id2 > id1);
        assertTrue(id3 > id2);
        
        System.out.println("Generated IDs:");
        System.out.println("ID1: " + id1);
        System.out.println("ID2: " + id2);
        System.out.println("ID3: " + id3);
    }
    
    @Test
    public void testConcurrentGeneration() throws InterruptedException {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator();
        
        // 并发测试
        int threadCount = 10;
        int idsPerThread = 100;
        Thread[] threads = new Thread[threadCount];
        long[][] results = new long[threadCount][idsPerThread];
        
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < idsPerThread; j++) {
                    results[threadIndex][j] = generator.nextId();
                }
            });
        }
        
        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
        
        // 验证所有生成的ID都不为0且不重复
        java.util.Set<Long> uniqueIds = new java.util.HashSet<>();
        for (int i = 0; i < threadCount; i++) {
            for (int j = 0; j < idsPerThread; j++) {
                long id = results[i][j];
                assertNotEquals(0L, id, "Generated ID should not be zero");
                assertTrue(uniqueIds.add(id), "ID should be unique: " + id);
            }
        }
        
        System.out.println("Generated " + uniqueIds.size() + " unique IDs in concurrent test");
    }
}