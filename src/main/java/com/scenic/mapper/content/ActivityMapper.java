package com.scenic.mapper.content;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.content.Activity;

@Mapper
public interface ActivityMapper {
    
    /**
     * 根据ID查询活动
     * @param id 活动ID
     * @return 活动信息
     */
    @Select("SELECT * FROM activity WHERE id = #{id}")
    Activity selectById(Long id);
    
    /**
     * 插入活动
     * @param activity 活动信息
     * @return 插入结果
     */
    @Insert("INSERT INTO activity(title, summary, content, image_url, start_time, end_time, price, team_limit, current_participants, location, status, create_time, update_time) " +
            "VALUES(#{title}, #{summary}, #{content}, #{imageUrl}, #{startTime}, #{endTime}, #{price}, #{maxParticipants}, #{currentParticipants}, #{location}, #{enabled}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Activity activity);
    
    /**
     * 更新活动信息
     * @param activity 活动信息
     * @return 更新结果
     */
    @Update("UPDATE activity SET title = #{title}, summary = #{summary}, content = #{content}, image_url = #{imageUrl}, " +
            "start_time = #{startTime}, end_time = #{endTime}, price = #{price}, team_limit = #{maxParticipants}, " +
            "current_participants = #{currentParticipants}, location = #{location}, status = #{enabled}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(Activity activity);
    
    /**
     * 根据ID删除活动
     * @param id 活动ID
     * @return 删除结果
     */
    @Delete("DELETE FROM activity WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 查询活动列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 活动列表
     */
    @Select("SELECT * FROM activity ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Activity> selectList(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询活动总数
     * @return 活动总数
     */
    @Select("SELECT COUNT(*) FROM activity")
    int selectCount();
    
    /**
     * 管理员查询活动列表
     * @param title 活动标题（可选）
     * @param enabled 是否启用（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 活动列表
     */
    List<Activity> selectForAdmin(@Param("title") String title, @Param("enabled") Boolean enabled, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 管理员查询活动总数
     * @param title 活动标题（可选）
     * @param enabled 是否启用（可选）
     * @return 活动总数
     */
    int selectCountForAdmin(@Param("title") String title, @Param("enabled") Boolean enabled);
    
    /**
     * 查询所有启用的活动
     * @return 活动列表
     */
    @Select("SELECT * FROM activity WHERE status = 1 ORDER BY create_time DESC")
    List<Activity> selectAllEnabled();
}
