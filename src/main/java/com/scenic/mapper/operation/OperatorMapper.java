package com.scenic.mapper.operation;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.operation.Operator;

/**
 * 操作员Mapper接口
 */
@Mapper
public interface OperatorMapper {
    
    /**
     * 根据ID查询操作员
     * @param id 操作员ID
     * @return 操作员实体
     */
    @Select("SELECT * FROM operator WHERE id = #{id}")
    Operator selectById(Long id);
    
    /**
     * 根据用户名查询操作员
     * @param username 用户名
     * @return 操作员实体
     */
    @Select("SELECT * FROM operator WHERE username = #{username}")
    Operator selectByUsername(String username);
    
    /**
     * 查询所有操作员
     * @return 操作员列表
     */
    @Select("SELECT * FROM operator")
    List<Operator> selectAll();
    
    /**
     * 根据条件查询操作员列表
     * @param username 用户名（可选）
     * @param realName 真实姓名（可选）
     * @param status 状态（可选）
     * @return 操作员列表
     */
    List<Operator> selectByCondition(@Param("username") String username, 
                                   @Param("realName") String realName, 
                                   @Param("status") Integer status);
    
    /**
     * 插入操作员
     * @param operator 操作员实体
     * @return 影响行数
     */
    @Insert("INSERT INTO operator(username, password, real_name, phone, email, status, role, create_time, update_time) " +
            "VALUES(#{username}, #{password}, #{realName}, #{phone}, #{email}, #{status}, #{role}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Operator operator);
    
    /**
     * 更新操作员
     * @param operator 操作员实体
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE operator " +
            "<set>" +
            "  <if test='username != null and username != \"\"'>" +
            "    username = #{username}," +
            "  </if>" +
            "  <if test='password != null and password != \"\"'>" +
            "    password = #{password}," +
            "  </if>" +
            "  <if test='realName != null and realName != \"\"'>" +
            "    real_name = #{realName}," +
            "  </if>" +
            "  <if test='phone != null and phone != \"\"'>" +
            "    phone = #{phone}," +
            "  </if>" +
            "  <if test='email != null and email != \"\"'>" +
            "    email = #{email}," +
            "  </if>" +
            "  <if test='status != null'>" +
            "    status = #{status}," +
            "  </if>" +
            "  <if test='role != null and role != \"\"'>" +
            "    role = #{role}," +
            "  </if>" +
            "  update_time = #{updateTime}" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(Operator operator);
    
    /**
     * 根据ID删除操作员
     * @param id 操作员ID
     * @return 影响行数
     */
    @Delete("DELETE FROM operator WHERE id = #{id}")
    int deleteById(Long id);
}
