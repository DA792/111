package com.scenic.mapper.system;

import com.scenic.entity.system.NotificationConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知配置Mapper接口
 */
@Mapper
public interface NotificationConfigMapper {
    /**
     * 查询所有通知配置
     * @return 通知配置列表
     */
    List<NotificationConfig> selectAll();
    
    /**
     * 根据ID查询通知配置
     * @param id 配置ID
     * @return 通知配置
     */
    NotificationConfig selectById(@Param("id") Long id);
    
    /**
     * 根据类型查询通知配置
     * @param type 通知类型
     * @return 通知配置
     */
    NotificationConfig selectByType(@Param("type") String type);
    
    /**
     * 插入通知配置
     * @param notificationConfig 通知配置
     * @return 影响行数
     */
    int insert(NotificationConfig notificationConfig);
    
    /**
     * 更新通知配置
     * @param notificationConfig 通知配置
     * @return 影响行数
     */
    int update(NotificationConfig notificationConfig);
    
    /**
     * 根据ID删除通知配置
     * @param id 配置ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}
