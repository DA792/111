package com.scenic.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.user.UserFrequentMember;

@Mapper
public interface UserFrequentMemberMapper {
    
    /**
     * 根据ID查询用户常用预约人
     * @param id 常用预约人ID
     * @return 常用预约人信息
     */
    @Select("SELECT * FROM user_frequent_member WHERE id = #{id} AND deleted = 0")
    UserFrequentMember selectById(Long id);
    
    /**
     * 根据用户ID查询常用预约人列表
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 常用预约人列表
     */
    @Select("SELECT * FROM user_frequent_member WHERE user_id = #{userId} AND deleted = 0 ORDER BY is_default DESC, create_time DESC LIMIT #{offset}, #{limit}")
    List<UserFrequentMember> selectByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据用户ID查询默认预约人
     * @param userId 用户ID
     * @return 默认预约人信息
     */
    @Select("SELECT * FROM user_frequent_member WHERE user_id = #{userId} AND is_default = 1 AND status = 1 AND deleted = 0 LIMIT 1")
    UserFrequentMember selectDefaultByUserId(Long userId);
    
    /**
     * 根据用户ID和证件号码查询常用预约人
     * @param userId 用户ID
     * @param idNumber 证件号码
     * @return 常用预约人信息
     */
    @Select("SELECT * FROM user_frequent_member WHERE user_id = #{userId} AND id_number = #{idNumber} AND deleted = 0 LIMIT 1")
    UserFrequentMember selectByIdNumber(@Param("userId") Long userId, @Param("idNumber") String idNumber);
    
    /**
     * 插入用户常用预约人
     * @param userFrequentMember 常用预约人信息
     * @return 插入结果
     */
    @Insert("INSERT INTO user_frequent_member(user_id, name, id_type, id_number, phone, gender, is_default, status, version, deleted, create_time, update_time, create_by, update_by) " +
            "VALUES(#{userId}, #{name}, #{idType}, #{idNumber}, #{phone}, #{gender}, #{isDefault}, #{status}, #{version}, #{deleted}, #{createTime}, #{updateTime}, #{createBy}, #{updateBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserFrequentMember userFrequentMember);
    
    /**
     * 更新用户常用预约人信息
     * @param userFrequentMember 常用预约人信息
     * @return 更新结果
     */
    @Update("UPDATE user_frequent_member SET name = #{name}, id_type = #{idType}, id_number = #{idNumber}, phone = #{phone}, " +
            "gender = #{gender}, is_default = #{isDefault}, status = #{status}, version = version + 1, update_time = #{updateTime}, update_by = #{updateBy} " +
            "WHERE id = #{id} AND version = #{version}")
    int updateById(UserFrequentMember userFrequentMember);
    
    /**
     * 更新用户常用预约人状态
     * @param id 常用预约人ID
     * @param status 状态
     * @param updateBy 更新人
     * @return 更新结果
     */
    @Update("UPDATE user_frequent_member SET status = #{status}, update_time = NOW(), update_by = #{updateBy} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Byte status, @Param("updateBy") Long updateBy);
    
    /**
     * 设置默认预约人
     * @param userId 用户ID
     * @param id 常用预约人ID
     * @param updateBy 更新人
     * @return 更新结果
     */
    @Update("UPDATE user_frequent_member SET is_default = CASE WHEN id = #{id} THEN 1 ELSE 0 END, update_time = NOW(), update_by = #{updateBy} " +
            "WHERE user_id = #{userId}")
    int setDefault(@Param("userId") Long userId, @Param("id") Long id, @Param("updateBy") Long updateBy);
    
    /**
     * 逻辑删除用户常用预约人
     * @param id 常用预约人ID
     * @param updateBy 更新人
     * @return 删除结果
     */
    @Update("UPDATE user_frequent_member SET deleted = 1, update_time = NOW(), update_by = #{updateBy} WHERE id = #{id}")
    int deleteById(@Param("id") Long id, @Param("updateBy") Long updateBy);
    
    /**
     * 根据用户ID删除常用预约人
     * @param userId 用户ID
     * @param updateBy 更新人
     * @return 删除结果
     */
    @Update("UPDATE user_frequent_member SET deleted = 1, update_time = NOW(), update_by = #{updateBy} WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId, @Param("updateBy") Long updateBy);
    
    /**
     * 查询常用预约人总数
     * @param userId 用户ID
     * @return 常用预约人总数
     */
    @Select("SELECT COUNT(*) FROM user_frequent_member WHERE user_id = #{userId} AND deleted = 0")
    int selectCountByUserId(Long userId);
    
    /**
     * 根据用户ID和姓名查询常用预约人列表
     * @param userId 用户ID
     * @param name 姓名
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 常用预约人列表
     */
    @Select("SELECT * FROM user_frequent_member WHERE user_id = #{userId} AND name LIKE CONCAT('%', #{name}, '%') AND deleted = 0 ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<UserFrequentMember> selectByName(@Param("userId") Long userId, @Param("name") String name, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据用户ID和姓名查询常用预约人总数
     * @param userId 用户ID
     * @param name 姓名
     * @return 常用预约人总数
     */
    @Select("SELECT COUNT(*) FROM user_frequent_member WHERE user_id = #{userId} AND name LIKE CONCAT('%', #{name}, '%') AND deleted = 0")
    int selectCountByName(@Param("userId") Long userId, @Param("name") String name);
}