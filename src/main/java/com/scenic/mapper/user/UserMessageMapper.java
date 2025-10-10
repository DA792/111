package com.scenic.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.user.UserMessage;

@Mapper
public interface UserMessageMapper {
    
    /**
     * 根据ID查询用户消息
     * @param id 消息ID
     * @return 消息信息
     */
    @Select("SELECT * FROM user_message WHERE id = #{id}")
    UserMessage selectById(Long id);
    
    /**
     * 根据用户ID查询消息列表
     * @param userId 用户ID
     * @param type 消息类型（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 消息列表
     */
    List<UserMessage> selectByUserId(@Param("userId") Long userId, @Param("type") String type, 
                                     @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 插入用户消息
     * @param userMessage 消息信息
     * @return 插入结果
     */
    @Insert("INSERT INTO user_message(user_id, related_user_id, related_user_name, type, content, related_id, is_read, create_time, update_time) " +
            "VALUES(#{userId}, #{relatedUserId}, #{relatedUserName}, #{type}, #{content}, #{relatedId}, #{isRead}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserMessage userMessage);
    
    /**
     * 更新用户消息信息
     * @param userMessage 消息信息
     * @return 更新结果
     */
    @Update("UPDATE user_message SET is_read = #{isRead}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(UserMessage userMessage);
    
    /**
     * 根据ID删除用户消息
     * @param id 消息ID
     * @return 删除结果
     */
    @Delete("DELETE FROM user_message WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据用户ID删除消息
     * @param userId 用户ID
     * @return 删除结果
     */
    @Delete("DELETE FROM user_message WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
    
    /**
     * 查询消息总数
     * @param userId 用户ID
     * @param type 消息类型（可选）
     * @return 消息总数
     */
    int selectCountByUserId(@Param("userId") Long userId, @Param("type") String type);
}
