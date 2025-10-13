package com.scenic.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.user.User;

@Mapper
public interface UserMapper {
    
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Long id);
    
    /**
     * 根据微信OpenID查询用户
     * @param openId 微信OpenID
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE open_id = #{openId}")
    User selectByOpenId(String openId);
    
    /**
     * 根据用户名查询用户
     * @param userName 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE user_name = #{userName}")
    User selectByUsername(String userName);
    
    /**
     * 插入用户
     * @param user 用户信息
     * @return 插入结果
     */
    @Insert("INSERT INTO user(user_name, password, real_name, id_type, id_number, nickname, phone, email, avatar, open_id, user_type, status, register_time, last_login_time, role, version, deleted, create_time, update_time) " +
            "VALUES(#{userName}, #{password}, #{realName}, #{idType}, #{idNumber}, #{nickname}, #{phone}, #{email}, #{avatar}, #{openId}, #{userType}, #{status}, #{registerTime}, #{lastLoginTime}, #{role}, #{version}, #{deleted}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新结果
     */
    @Update("UPDATE user SET user_name = #{userName}, password = #{password}, real_name = #{realName}, id_type = #{idType}, id_number = #{idNumber}, nickname = #{nickname}, phone = #{phone}, email = #{email}, avatar = #{avatar}, " +
            "open_id = #{openId}, user_type = #{userType}, status = #{status}, register_time = #{registerTime}, last_login_time = #{lastLoginTime}, role = #{role}, version = #{version}, deleted = #{deleted}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(User user);
    
    /**
     * 根据ID删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 查询用户列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @param username 用户名（可选）
     * @param phone 电话（可选）
     * @return 用户列表
     */
    List<User> selectList(@Param("offset") int offset, @Param("limit") int limit, 
                          @Param("username") String username, @Param("phone") String phone);
    
    /**
     * 查询用户总数
     * @param username 用户名（可选）
     * @param phone 电话（可选）
     * @return 用户总数
     */
    int selectCount(@Param("username") String username, @Param("phone") String phone);
}
