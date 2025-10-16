package com.scenic.mapper.content;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scenic.entity.content.ProtectedReservationInfo;
import com.scenic.dto.content.ProtectedReservationInfoEnhancedDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 保护区介绍Mapper接口
 */
@Mapper
public interface ProtectedReservationInfoMapper extends BaseMapper<ProtectedReservationInfo> {
    
    /**
     * 根据删除标记查询保护区介绍列表
     * @param deleted 删除标记
     * @return 保护区介绍列表
     */
    List<ProtectedReservationInfo> selectByDeleted(Byte deleted);
    
    /**
     * 根据标题模糊查询保护区介绍列表
     * @param title 标题
     * @return 保护区介绍列表
     */
    List<ProtectedReservationInfo> selectByTitle(String title);
    
    /**
     * 根据内容类型查询保护区介绍列表
     * @param contentType 内容类型
     * @return 保护区介绍列表
     */
    List<ProtectedReservationInfo> selectByContentType(Byte contentType);
    
    /**
     * 根据内容分类查询保护区介绍列表
     * @param contentCategory 内容分类
     * @return 保护区介绍列表
     */
    List<ProtectedReservationInfo> selectByContentCategory(Byte contentCategory);
    
    /**
     * 分页查询保护区介绍列表（增强版，支持发布人、发布时间、内容类型搜索）
     * @param offset 偏移量
     * @param limit 限制数量
     * @param title 标题（模糊搜索）
     * @param creatorName 发布人姓名（模糊搜索）
     * @param startTime 发布时间开始
     * @param endTime 发布时间结束
     * @param contentType 内容类型（精确匹配）
     * @return 保护区介绍增强DTO列表
     */
    List<ProtectedReservationInfoEnhancedDTO> selectPageEnhanced(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("title") String title,
            @Param("creatorName") String creatorName,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("contentType") Byte contentType);
    
    /**
     * 查询保护区介绍总数（增强版，支持发布人、发布时间、内容类型搜索）
     * @param title 标题（模糊搜索）
     * @param creatorName 发布人姓名（模糊搜索）
     * @param startTime 发布时间开始
     * @param endTime 发布时间结束
     * @param contentType 内容类型（精确匹配）
     * @return 总数
     */
    int selectCountEnhanced(
            @Param("title") String title,
            @Param("creatorName") String creatorName,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("contentType") Byte contentType);
}