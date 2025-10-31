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
    @Insert("INSERT INTO user(user_name, password, real_name, id_type, id_number, phone, email, avatar_file_id, open_id, user_type, status, register_time, last_login_time, version, deleted, create_time, update_time) " +
            "VALUES(#{userName}, #{password}, #{realName}, #{idType}, #{idNumber}, #{phone}, #{email}, #{avatarFileId}, #{openId}, #{userType}, #{status}, #{registerTime}, #{lastLoginTime}, #{version}, #{deleted}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新结果
     */
    @Update("UPDATE user SET user_name = #{userName}, password = #{password}, real_name = #{realName}, id_type = #{idType}, id_number = #{idNumber}, phone = #{phone}, email = #{email}, avatar_file_id = #{avatarFileId}, " +
            "open_id = #{openId}, user_type = #{userType}, status = #{status}, register_time = #{registerTime}, last_login_time = #{lastLoginTime}, version = #{version}, deleted = #{deleted}, update_time = #{updateTime} WHERE id = #{id}")
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
     * @param userType 用户类型（可选）
     * @return 用户列表
     */
    List<User> selectList(@Param("offset") int offset, @Param("limit") int limit, 
                          @Param("username") String username, @Param("phone") String phone, @Param("userType") Integer userType);
    
    /**
     * 小程序端查询用户列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @param nickname 昵称（可选）
     * @return 用户列表
     */
    List<User> selectListForMiniapp(@Param("offset") int offset, @Param("limit") int limit, 
                                    @Param("nickname") String nickname);
    
    /**
     * 查询用户总数
     * @param username 用户名（可选）
     * @param phone 电话（可选）
     * @param userType 用户类型（可选）
     * @return 用户总数
     */
    int selectCount(@Param("username") String username, @Param("phone") String phone, @Param("userType") Integer userType);
    
    /**
     * 小程序端查询用户总数
     * @param nickname 昵称（可选）
     * @return 用户总数
     */
    int selectCountForMiniapp(@Param("nickname") String nickname);
    
    /**
     * 根据微信OpenID统计用户数量
     * @param openId 微信OpenID
     * @return 用户数量
     */
    @Select("SELECT COUNT(*) FROM user WHERE open_id = #{openId}")
    int countByOpenId(String openId);
    
    /**
     * 根据ID列表查询用户
     * @param ids 用户ID列表
     * @return 用户列表
     */
    List<User> selectByIds(@Param("ids") List<Long> ids);
    
    /**
     * 查询所有启用状态的普通用户（用于批量发送通知）
     * @return 用户列表
     */
    @Select("SELECT * FROM user WHERE user_type = 1 AND status = 1 AND deleted = 0")
    List<User> selectAllActiveUsers();
}