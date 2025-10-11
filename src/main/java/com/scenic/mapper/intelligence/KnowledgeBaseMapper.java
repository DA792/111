package com.scenic.mapper.intelligence;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.intelligence.KnowledgeBase;

/**
 * 知识库Mapper接口
 */
@Mapper
public interface KnowledgeBaseMapper {
    
    /**
     * 根据ID查询知识库
     * @param id 知识库ID
     * @return 知识库实体
     */
    @Select("SELECT * FROM knowledge_base WHERE id = #{id}")
    KnowledgeBase selectById(Long id);
    
    /**
     * 查询所有知识库
     * @return 知识库列表
     */
    @Select("SELECT * FROM knowledge_base ORDER BY create_time DESC")
    List<KnowledgeBase> selectAll();
    
    /**
     * 根据条件查询知识库列表
     * @param title 知识库标题（可选）
     * @param version 版本号（可选）
     * @param status 状态（可选）
     * @return 知识库列表
     */
    List<KnowledgeBase> selectByCondition(@Param("title") String title, 
                                        @Param("version") String version, 
                                        @Param("status") Integer status);
    
    /**
     * 插入知识库
     * @param knowledgeBase 知识库实体
     * @return 影响行数
     */
    @Insert("INSERT INTO knowledge_base(title, content, file_path, file_name, file_size, file_type, version, status, create_time, update_time) " +
            "VALUES(#{title}, #{content}, #{filePath}, #{fileName}, #{fileSize}, #{fileType}, #{version}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(KnowledgeBase knowledgeBase);
    
    /**
     * 更新知识库
     * @param knowledgeBase 知识库实体
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE knowledge_base " +
            "<set>" +
            "  <if test='title != null and title != \"\"'>" +
            "    title = #{title}," +
            "  </if>" +
            "  <if test='content != null and content != \"\"'>" +
            "    content = #{content}," +
            "  </if>" +
            "  <if test='filePath != null and filePath != \"\"'>" +
            "    file_path = #{filePath}," +
            "  </if>" +
            "  <if test='fileName != null and fileName != \"\"'>" +
            "    file_name = #{fileName}," +
            "  </if>" +
            "  <if test='fileSize != null'>" +
            "    file_size = #{fileSize}," +
            "  </if>" +
            "  <if test='fileType != null and fileType != \"\"'>" +
            "    file_type = #{fileType}," +
            "  </if>" +
            "  <if test='version != null and version != \"\"'>" +
            "    version = #{version}," +
            "  </if>" +
            "  <if test='status != null'>" +
            "    status = #{status}," +
            "  </if>" +
            "  update_time = #{updateTime}" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(KnowledgeBase knowledgeBase);
    
    /**
     * 根据ID删除知识库
     * @param id 知识库ID
     * @return 影响行数
     */
    @Delete("DELETE FROM knowledge_base WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据版本号查询知识库
     * @param version 版本号
     * @return 知识库实体
     */
    @Select("SELECT * FROM knowledge_base WHERE version = #{version} ORDER BY create_time DESC LIMIT 1")
    KnowledgeBase selectByVersion(String version);
}
