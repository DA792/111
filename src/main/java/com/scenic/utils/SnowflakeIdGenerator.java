package com.scenic.utils;

import org.springframework.stereotype.Component;

/**
 * 雪花ID生成器
 * Twitter Snowflake算法实现
 * 生成64位的Long型ID，结构如下：
 * 1位符号位 + 41位时间戳 + 10位工作机器ID + 12位序列号
 */
@Component
public class SnowflakeIdGenerator {
    
    // 起始时间戳 (2025-01-01)
    private final static long START_TIMESTAMP = 1735689600000L;
    
    // 各部分位数
    private final static long SEQUENCE_BIT = 12;  // 序列号位数
    private final static long MACHINE_BIT = 10;   // 机器ID位数
    
    // 最大值
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    
    // 位移量
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long TIMESTAMP_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    
    private long machineId;     // 机器ID
    private long sequence = 0L; // 序列号
    private long lastTimestamp = -1L; // 上次时间戳
    
    /**
     * 构造函数
     * @param machineId 机器ID (0-1023)
     */
    public SnowflakeIdGenerator() {
        this.machineId = 1L; // 默认机器ID为1
    }
    
    /**
     * 构造函数
     * @param machineId 机器ID (0-1023)
     */
    public SnowflakeIdGenerator(long machineId) {
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId must be between 0 and " + MAX_MACHINE_NUM);
        }
        this.machineId = machineId;
    }
    
    /**
     * 生成下一个ID
     * @return 雪花ID
     */
    public synchronized long nextId() {
        long currentTimestamp = getCurrentTimestamp();
        
        // 如果当前时间小于上次时间戳，说明系统时钟回退，抛出异常
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }
        
        // 如果是同一毫秒内生成的，则序列号自增
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 如果序列号溢出，阻塞到下一毫秒
            if (sequence == 0) {
                currentTimestamp = getNextTimestamp();
            }
        } else {
            // 如果是新的毫秒，序列号重置为0
            sequence = 0L;
        }
        
        lastTimestamp = currentTimestamp;
        
        // 生成ID
        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_LEFT) 
                | (machineId << MACHINE_LEFT) 
                | sequence;
    }
    
    /**
     * 获取下一个毫秒数
     * @return 下一个毫秒数
     */
    private long getNextTimestamp() {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }
    
    /**
     * 获取当前时间戳
     * @return 当前时间戳
     */
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
}