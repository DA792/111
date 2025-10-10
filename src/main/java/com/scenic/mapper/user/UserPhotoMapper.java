package com.scenic.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.user.UserPhoto;

@Mapper
public interface UserPhotoMapper {
    
    /**
     * 根据ID查询用户照片
     * @param id 照片ID
     * @return 照片信息
     */
    @Select("SELECT * FROM user_photo WHERE id = #{id}")
    UserPhoto selectById(Long id);
    
    /**
     * 根据用户ID查询照片列表
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 照片列表
     */
    @Select("SELECT * FROM user_photo WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<UserPhoto> selectByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 插入用户照片
     * @param userPhoto 照片信息
     * @return 插入结果
     */
    @Insert("INSERT INTO user_photo(user_id, photo_url, description, category, latitude, longitude, likes, enabled, create_time, update_time) " +
            "VALUES(#{userId}, #{photoUrl}, #{description}, #{category}, #{latitude}, #{longitude}, #{likes}, #{enabled}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserPhoto userPhoto);
    
    /**
     * 更新用户照片信息
     * @param userPhoto 照片信息
     * @return 更新结果
     */
    @Update("UPDATE user_photo SET description = #{description}, category = #{category}, " +
            "update_time = #{updateTime} WHERE id = #{id}")
    int updateById(UserPhoto userPhoto);
    
    /**
     * 根据ID删除用户照片
     * @param id 照片ID
     * @return 删除结果
     */
    @Delete("DELETE FROM user_photo WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据用户ID删除照片
     * @param userId 用户ID
     * @return 删除结果
     */
    @Delete("DELETE FROM user_photo WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
    
    /**
     * 查询照片总数
     * @param userId 用户ID
     * @return 照片总数
     */
    @Select("SELECT COUNT(*) FROM user_photo WHERE user_id = #{userId}")
    int selectCountByUserId(Long userId);
}
