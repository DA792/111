package com.scenic.mapper.interaction;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.interaction.PhotoCheckIn;

@Mapper
public interface PhotoCheckInMapper {
    
    /**
     * 根据ID查询照片打卡
     * @param id 照片打卡ID
     * @return 照片打卡信息
     */
    PhotoCheckIn selectById(Long id);
    
    /**
     * 插入照片打卡
     * @param photoCheckIn 照片打卡信息
     * @return 插入结果
     */
    @Insert("INSERT INTO photo_checkin(user_id, user_name, user_avatar, title, content, category_id, photo_id, like_count, view_count, status, version, deleted, create_time, update_time, create_by, update_by) " +
            "VALUES(#{userId}, #{userName}, #{userAvatar}, #{title}, #{content}, #{categoryId}, #{photoId}, #{likeCount}, #{viewCount}, #{status}, #{version}, #{deleted}, #{createTime}, #{updateTime}, #{createBy}, #{updateBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PhotoCheckIn photoCheckIn);
    
    /**
     * 更新照片打卡信息
     * @param photoCheckIn 照片打卡信息
     * @return 更新结果
     */
    @Update("UPDATE photo_checkin SET user_name = #{userName}, user_avatar = #{userAvatar}, title = #{title}, content = #{content}, " +
            "category_id = #{categoryId}, photo_id = #{photoId}, like_count = #{likeCount}, view_count = #{viewCount}, " +
            "status = #{status}, version = #{version}, deleted = #{deleted}, update_time = #{updateTime}, update_by = #{updateBy} WHERE id = #{id}")
    int updateById(PhotoCheckIn photoCheckIn);
    
    /**
     * 根据ID删除照片打卡（逻辑删除）
     * @param id 照片打卡ID
     * @return 删除结果
     */
    @Update("UPDATE photo_checkin SET deleted = 1, update_time = NOW() WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据用户ID删除照片打卡
     * @param userId 用户ID
     * @return 删除结果
     */
    @Delete("DELETE FROM photo_checkin WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
    
    /**
     * 查询照片打卡列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 照片打卡列表
     */
    @Select("SELECT * FROM photo_checkin WHERE status = 1 AND deleted = 0 ORDER BY like_count DESC, create_time DESC LIMIT #{offset}, #{limit}")
    List<PhotoCheckIn> selectList(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询照片打卡总数
     * @return 照片打卡总数
     */
    @Select("SELECT COUNT(*) FROM photo_checkin WHERE status = 1 AND deleted = 0")
    int selectCount();
    
    /**
     * 根据分类查询照片打卡总数
     * @param category 分类
     * @return 照片打卡总数
     */
    @Select("SELECT COUNT(*) FROM photo_checkin WHERE category_id = #{category} AND status = 1 AND deleted = 0")
    int selectCountByCategory(@Param("category") String category);
    
    /**
     * 根据用户ID查询照片打卡总数
     * @param userId 用户ID
     * @return 照片打卡总数
     */
    @Select("SELECT COUNT(*) FROM photo_checkin WHERE user_id = #{userId} AND deleted = 0")
    int selectCountByUserId(@Param("userId") Long userId);
    
    /**
     * 管理员查询照片打卡列表
     * @param title 标题（可选）
     * @param userName 用户名（可选）
     * @param categoryId 分类ID（可选）
     * @param createTime 创建时间（可选）
     * @return 照片打卡列表
     */
    List<PhotoCheckIn> selectForAdmin(@Param("title") String title, @Param("userName") String userName, @Param("categoryId") Long categoryId, @Param("createTime") LocalDateTime createTime);
    
    /**
     * 管理员查询照片打卡总数
     * @param title 标题（可选）
     * @param userName 用户名（可选）
     * @param categoryId 分类ID（可选）
     * @param createTime 创建时间（可选）
     * @return 照片打卡总数
     */
    int selectCountForAdmin(@Param("title") String title, @Param("userName") String userName, @Param("categoryId") Long categoryId, @Param("createTime") LocalDateTime createTime);
    
    /**
     * 小程序端查询照片打卡列表
     * @param title 标题（可选）
     * @param categoryId 分类ID（可选）
     * @return 照片打卡列表
     */
    List<PhotoCheckIn> selectForMiniapp(@Param("title") String title, @Param("categoryId") Long categoryId);
    
    /**
     * 小程序端查询照片打卡总数
     * @param title 标题（可选）
     * @param categoryId 分类ID（可选）
     * @return 照片打卡总数
     */
    int selectCountForMiniapp(@Param("title") String title, @Param("categoryId") Long categoryId);
    
    /**
     * 查询所有照片打卡
     * @return 照片打卡列表
     */
    @Select("SELECT * FROM photo_checkin WHERE status = 1 AND deleted = 0 ORDER BY like_count DESC, create_time DESC")
    List<PhotoCheckIn> selectAll();
    
    /**
     * 根据ID列表查询照片打卡记录
     * @param ids ID列表
     * @return 照片打卡记录列表
     */
    @Select({
        "<script>",
        "SELECT * FROM photo_checkin WHERE id IN",
        "<foreach item='id' collection='ids' open='(' separator=',' close=')'>",
        "#{id}",
        "</foreach>",
        "AND status = 1 AND deleted = 0",
        "ORDER BY create_time DESC",
        "</script>"
    })
    List<PhotoCheckIn> selectByIds(@Param("ids") List<Long> ids);
    
    /**
     * 增加点赞数
     * @param id 照片打卡ID
     * @return 更新结果
     */
    @Update("UPDATE photo_checkin SET like_count = like_count + 1, update_time = #{updateTime} WHERE id = #{id}")
    int incrementLikes(@Param("id") Long id, @Param("updateTime") java.time.LocalDateTime updateTime);
    
    /**
     * 减少点赞数
     * @param id 照片打卡ID
     * @return 更新结果
     */
    @Update("UPDATE photo_checkin SET like_count = like_count - 1, update_time = #{updateTime} WHERE id = #{id} AND like_count > 0")
    int decrementLikes(@Param("id") Long id, @Param("updateTime") java.time.LocalDateTime updateTime);
    
    /**
     * 根据用户ID和分类ID查询照片打卡列表（分页）
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 照片打卡列表
     */
    @Select("SELECT * FROM photo_checkin WHERE user_id = #{userId} AND category_id = #{categoryId} AND deleted = 0 ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<PhotoCheckIn> selectByUserAndCategory(@Param("userId") Long userId, @Param("categoryId") Long categoryId, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据用户ID和分类ID查询照片打卡总数
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @return 照片打卡总数
     */
    @Select("SELECT COUNT(*) FROM photo_checkin WHERE user_id = #{userId} AND category_id = #{categoryId} AND deleted = 0")
    int selectCountByUserAndCategory(@Param("userId") Long userId, @Param("categoryId") Long categoryId);
}