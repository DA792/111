package com.scenic.mapper.intelligence;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.intelligence.AIQARecord;

/**
 * AI问答记录Mapper接口
 */
@Mapper
public interface AIQARecordMapper {
    
    /**
     * 根据ID查询问答记录
     * @param id 问答记录ID
     * @return 问答记录实体
     */
    @Select("SELECT * FROM ai_qa_record WHERE id = #{id}")
    AIQARecord selectById(Long id);
    
    /**
     * 根据用户ID查询问答记录列表
     * @param userId 用户ID
     * @return 问答记录列表
     */
    @Select("SELECT * FROM ai_qa_record WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<AIQARecord> selectByUserId(Long userId);
    
    /**
     * 根据条件查询问答记录列表
     * @param userId 用户ID（可选）
     * @param status 状态（可选）
     * @return 问答记录列表
     */
    List<AIQARecord> selectByCondition(@Param("userId") Long userId, 
                                     @Param("status") Integer status);
    
    /**
     * 插入问答记录
     * @param aiqaRecord 问答记录实体
     * @return 影响行数
     */
    @Insert("INSERT INTO ai_qa_record(user_id, question, answer, knowledge_base_id, knowledge_graph_id, status, create_time, update_time) " +
            "VALUES(#{userId}, #{question}, #{answer}, #{knowledgeBaseId}, #{knowledgeGraphId}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AIQARecord aiqaRecord);
    
    /**
     * 更新问答记录
     * @param aiqaRecord 问答记录实体
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE ai_qa_record " +
            "<set>" +
            "  <if test='question != null and question != \"\"'>" +
            "    question = #{question}," +
            "  </if>" +
            "  <if test='answer != null and answer != \"\"'>" +
            "    answer = #{answer}," +
            "  </if>" +
            "  <if test='knowledgeBaseId != null'>" +
            "    knowledge_base_id = #{knowledgeBaseId}," +
            "  </if>" +
            "  <if test='knowledgeGraphId != null'>" +
            "    knowledge_graph_id = #{knowledgeGraphId}," +
            "  </if>" +
            "  <if test='status != null'>" +
            "    status = #{status}," +
            "  </if>" +
            "  update_time = #{updateTime}" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(AIQARecord aiqaRecord);
    
    /**
     * 根据ID删除问答记录
     * @param id 问答记录ID
     * @return 影响行数
     */
    @Delete("DELETE FROM ai_qa_record WHERE id = #{id}")
    int deleteById(Long id);
}
