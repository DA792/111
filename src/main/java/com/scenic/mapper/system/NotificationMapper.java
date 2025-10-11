package com.scenic.mapper.system;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.system.Notification;

/**
 * 通知Mapper接口
 */
@Mapper
public interface NotificationMapper {
    
    /**
     * 根据ID查询通知
     * @param id 通知ID
     * @return 通知实体
     */
    @Select("SELECT * FROM notification WHERE id = #{id}")
    Notification selectById(Long id);
    
    /**
     * 查询所有通知
     * @return 通知列表
     */
    @Select("SELECT * FROM notification ORDER BY create_time DESC")
    List<Notification> selectAll();
    
    /**
     * 根据条件查询通知列表
     * @param title 通知标题（可选）
     * @param channel 通知渠道（可选）
     * @param sendStatus 发送状态（可选）
     * @return 通知列表
     */
    List<Notification> selectByCondition(@Param("title") String title, 
                                       @Param("channel") String channel, 
                                       @Param("sendStatus") Integer sendStatus);
    
    /**
     * 插入通知
     * @param notification 通知实体
     * @return 影响行数
     */
    @Insert("INSERT INTO notification(title, content, channel, receiver_id, receiver_type, send_status, send_time, create_time, update_time) " +
            "VALUES(#{title}, #{content}, #{channel}, #{receiverId}, #{receiverType}, #{sendStatus}, #{sendTime}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notification notification);
    
    /**
     * 更新通知
     * @param notification 通知实体
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE notification " +
            "<set>" +
            "  <if test='title != null and title != \"\"'>" +
            "    title = #{title}," +
            "  </if>" +
            "  <if test='content != null and content != \"\"'>" +
            "    content = #{content}," +
            "  </if>" +
            "  <if test='channel != null and channel != \"\"'>" +
            "    channel = #{channel}," +
            "  </if>" +
            "  <if test='receiverId != null'>" +
            "    receiver_id = #{receiverId}," +
            "  </if>" +
            "  <if test='receiverType != null and receiverType != \"\"'>" +
            "    receiver_type = #{receiverType}," +
            "  </if>" +
            "  <if test='sendStatus != null'>" +
            "    send_status = #{sendStatus}," +
            "  </if>" +
            "  <if test='sendTime != null'>" +
            "    send_time = #{sendTime}," +
            "  </if>" +
            "  update_time = #{updateTime}" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(Notification notification);
    
    /**
     * 根据ID删除通知
     * @param id 通知ID
     * @return 影响行数
     */
    @Delete("DELETE FROM notification WHERE id = #{id}")
    int deleteById(Long id);
}
