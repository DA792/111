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
    @Select("SELECT * FROM activity WHERE id = #{id} AND (deleted IS NULL OR deleted = 0)")
    Activity selectById(Long id);
    
    /**
     * 插入活动
     * @param activity 活动信息
     * @return 插入结果
     */
    @Insert("INSERT INTO activity(title, start_time, end_time, suitable_crowd, location, price, team_limit, content, cover_image_id, status, create_time, update_time, create_by, update_by, reservation_priority, deleted) " +
            "VALUES(#{title}, #{startTime}, #{endTime}, #{suitableCrowd}, #{location}, #{price}, #{teamLimit}, #{content}, #{coverImageId}, #{status}, #{createTime}, #{updateTime}, #{createBy}, #{updateBy}, #{reservationPriority}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Activity activity);
    
    /**
     * 更新活动信息
     * @param activity 活动信息
     * @return 更新结果
     */
    @Update("UPDATE activity SET title = #{title}, start_time = #{startTime}, end_time = #{endTime}, suitable_crowd = #{suitableCrowd}, " +
            "location = #{location}, price = #{price}, team_limit = #{teamLimit}, content = #{content}, cover_image_id = #{coverImageId}, " +
            "status = #{status}, update_time = #{updateTime}, update_by = #{updateBy}, reservation_priority = #{reservationPriority} WHERE id = #{id}")
    int updateById(Activity activity);
    
    /**
     * 根据ID删除活动（逻辑删除，设置deleted为1）
     * @param id 活动ID
     * @return 删除结果
     */
    @Update("UPDATE activity SET deleted = 1, update_time = NOW() WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 查询活动列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 活动列表
     */
    @Select("SELECT * FROM activity WHERE (deleted IS NULL OR deleted = 0) ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Activity> selectList(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询活动总数
     * @return 活动总数
     */
    @Select("SELECT COUNT(*) FROM activity WHERE (deleted IS NULL OR deleted = 0)")
    int selectCount();
    
    /**
     * 管理员查询活动列表
     * @param title 活动标题（可选）
     * @param status 活动状态（可选）
     * @param startTime 开始时间（可选）
     * @param suitableCrowd 适合人群（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 活动列表
     */
    List<Activity> selectForAdmin(@Param("title") String title, @Param("status") Byte status, @Param("startTime") String startTime, @Param("suitableCrowd") String suitableCrowd, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 管理员查询活动总数
     * @param title 活动标题（可选）
     * @param status 活动状态（可选）
     * @param startTime 开始时间（可选）
     * @param suitableCrowd 适合人群（可选）
     * @return 活动总数
     */
    int selectCountForAdmin(@Param("title") String title, @Param("status") Byte status, @Param("startTime") String startTime, @Param("suitableCrowd") String suitableCrowd);
    
    /**
     * 查询所有未结束的活动
     * @return 活动列表
     */
    @Select("SELECT * FROM activity WHERE status = 0 AND (deleted IS NULL OR deleted = 0) ORDER BY create_time DESC")
    List<Activity> selectAllEnabled();
    
    /**
     * 根据启用状态查询活动列表
     * @param title 活动标题（可选）
     * @param enabled 是否启用（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 活动列表
     */
    @Select("<script>SELECT * FROM activity WHERE (deleted IS NULL OR deleted = 0) " +
            "<if test='title != null and title != \"\"'>AND title LIKE CONCAT('%', #{title}, '%') </if>" +
            "<if test='enabled != null'>AND enabled = #{enabled} </if>" +
            "ORDER BY create_time DESC LIMIT #{offset}, #{limit}</script>")
    List<Activity> selectByEnabledStatus(@Param("title") String title, @Param("enabled") Integer enabled, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据启用状态查询活动总数
     * @param title 活动标题（可选）
     * @param enabled 是否启用（可选）
     * @return 活动总数
     */
    @Select("<script>SELECT COUNT(*) FROM activity WHERE (deleted IS NULL OR deleted = 0) " +
            "<if test='title != null and title != \"\"'>AND title LIKE CONCAT('%', #{title}, '%') </if>" +
            "<if test='enabled != null'>AND enabled = #{enabled} </if></script>")
    int selectCountByEnabledStatus(@Param("title") String title, @Param("enabled") Integer enabled);
}