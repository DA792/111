package com.scenic.mapper.interaction;

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
    @Select("SELECT * FROM photo_check_in WHERE id = #{id}")
    PhotoCheckIn selectById(Long id);
    
    /**
     * 插入照片打卡
     * @param photoCheckIn 照片打卡信息
     * @return 插入结果
     */
    @Insert("INSERT INTO photo_check_in(user_id, user_name, photo_url, description, category, latitude, longitude, likes, enabled, create_time, update_time) " +
            "VALUES(#{userId}, #{userName}, #{photoUrl}, #{description}, #{category}, #{latitude}, #{longitude}, #{likes}, #{enabled}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PhotoCheckIn photoCheckIn);
    
    /**
     * 更新照片打卡信息
     * @param photoCheckIn 照片打卡信息
     * @return 更新结果
     */
    @Update("UPDATE photo_check_in SET user_name = #{userName}, photo_url = #{photoUrl}, description = #{description}, category = #{category}, " +
            "latitude = #{latitude}, longitude = #{longitude}, likes = #{likes}, enabled = #{enabled}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(PhotoCheckIn photoCheckIn);
    
    /**
     * 根据ID删除照片打卡
     * @param id 照片打卡ID
     * @return 删除结果
     */
    @Delete("DELETE FROM photo_check_in WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据用户ID删除照片打卡
     * @param userId 用户ID
     * @return 删除结果
     */
    @Delete("DELETE FROM photo_check_in WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
    
    /**
     * 查询照片打卡列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 照片打卡列表
     */
    @Select("SELECT * FROM photo_check_in WHERE enabled = 1 ORDER BY likes DESC, create_time DESC LIMIT #{offset}, #{limit}")
    List<PhotoCheckIn> selectList(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询照片打卡总数
     * @return 照片打卡总数
     */
    @Select("SELECT COUNT(*) FROM photo_check_in WHERE enabled = 1")
    int selectCount();
    
    /**
     * 根据分类查询照片打卡总数
     * @param category 分类
     * @return 照片打卡总数
     */
    @Select("SELECT COUNT(*) FROM photo_check_in WHERE category = #{category} AND enabled = 1")
    int selectCountByCategory(@Param("category") String category);
    
    /**
     * 根据用户ID查询照片打卡总数
     * @param userId 用户ID
     * @return 照片打卡总数
     */
    @Select("SELECT COUNT(*) FROM photo_check_in WHERE user_id = #{userId}")
    int selectCountByUserId(@Param("userId") Long userId);
    
    /**
     * 管理员查询照片打卡列表
     * @param userName 用户名（可选）
     * @param category 分类（可选）
     * @param enabled 是否启用（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 照片打卡列表
     */
    List<PhotoCheckIn> selectForAdmin(@Param("userName") String userName, @Param("category") String category, @Param("enabled") Boolean enabled, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 管理员查询照片打卡总数
     * @param userName 用户名（可选）
     * @param category 分类（可选）
     * @param enabled 是否启用（可选）
     * @return 照片打卡总数
     */
    int selectCountForAdmin(@Param("userName") String userName, @Param("category") String category, @Param("enabled") Boolean enabled);
    
    /**
     * 查询所有照片打卡
     * @return 照片打卡列表
     */
    @Select("SELECT * FROM photo_check_in WHERE enabled = 1 ORDER BY likes DESC, create_time DESC")
    List<PhotoCheckIn> selectAll();
    
    /**
     * 增加点赞数
     * @param id 照片打卡ID
     * @return 更新结果
     */
    @Update("UPDATE photo_check_in SET likes = likes + 1, update_time = #{updateTime} WHERE id = #{id}")
    int incrementLikes(@Param("id") Long id, @Param("updateTime") java.time.LocalDateTime updateTime);
    
    /**
     * 减少点赞数
     * @param id 照片打卡ID
     * @return 更新结果
     */
    @Update("UPDATE photo_check_in SET likes = likes - 1, update_time = #{updateTime} WHERE id = #{id} AND likes > 0")
    int decrementLikes(@Param("id") Long id, @Param("updateTime") java.time.LocalDateTime updateTime);
}
