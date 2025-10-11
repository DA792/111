package com.scenic.mapper.intelligence;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.intelligence.KnowledgeGraph;

/**
 * 知识图谱Mapper接口
 */
@Mapper
public interface KnowledgeGraphMapper {
    
    /**
     * 根据ID查询知识图谱
     * @param id 知识图谱ID
     * @return 知识图谱实体
     */
    @Select("SELECT * FROM knowledge_graph WHERE id = #{id}")
    KnowledgeGraph selectById(Long id);
    
    /**
     * 查询所有知识图谱
     * @return 知识图谱列表
     */
    @Select("SELECT * FROM knowledge_graph ORDER BY create_time DESC")
    List<KnowledgeGraph> selectAll();
    
    /**
     * 根据条件查询知识图谱列表
     * @param speciesName 物种名称（可选）
     * @param category 分类（可选）
     * @param status 状态（可选）
     * @return 知识图谱列表
     */
    List<KnowledgeGraph> selectByCondition(@Param("speciesName") String speciesName, 
                                         @Param("category") String category, 
                                         @Param("status") Integer status);
    
    /**
     * 插入知识图谱
     * @param knowledgeGraph 知识图谱实体
     * @return 影响行数
     */
    @Insert("INSERT INTO knowledge_graph(species_name, scientific_name, description, image_url, category, habitat, status, create_time, update_time) " +
            "VALUES(#{speciesName}, #{scientificName}, #{description}, #{imageUrl}, #{category}, #{habitat}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(KnowledgeGraph knowledgeGraph);
    
    /**
     * 更新知识图谱
     * @param knowledgeGraph 知识图谱实体
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE knowledge_graph " +
            "<set>" +
            "  <if test='speciesName != null and speciesName != \"\"'>" +
            "    species_name = #{speciesName}," +
            "  </if>" +
            "  <if test='scientificName != null and scientificName != \"\"'>" +
            "    scientific_name = #{scientificName}," +
            "  </if>" +
            "  <if test='description != null and description != \"\"'>" +
            "    description = #{description}," +
            "  </if>" +
            "  <if test='imageUrl != null and imageUrl != \"\"'>" +
            "    image_url = #{imageUrl}," +
            "  </if>" +
            "  <if test='category != null and category != \"\"'>" +
            "    category = #{category}," +
            "  </if>" +
            "  <if test='habitat != null and habitat != \"\"'>" +
            "    habitat = #{habitat}," +
            "  </if>" +
            "  <if test='status != null'>" +
            "    status = #{status}," +
            "  </if>" +
            "  update_time = #{updateTime}" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(KnowledgeGraph knowledgeGraph);
    
    /**
     * 根据ID删除知识图谱
     * @param id 知识图谱ID
     * @return 影响行数
     */
    @Delete("DELETE FROM knowledge_graph WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据物种名称查询知识图谱
     * @param speciesName 物种名称
     * @return 知识图谱实体
     */
    @Select("SELECT * FROM knowledge_graph WHERE species_name = #{speciesName}")
    KnowledgeGraph selectBySpeciesName(String speciesName);
}
