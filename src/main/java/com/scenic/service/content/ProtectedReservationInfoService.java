package com.scenic.service.content;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scenic.entity.content.ProtectedReservationInfo;
import com.scenic.dto.content.ProtectedReservationInfoDTO;
import com.scenic.dto.content.ProtectedReservationInfoEnhancedDTO;
import com.scenic.common.dto.PageResult;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * 保护区介绍服务接口
 */
public interface ProtectedReservationInfoService extends IService<ProtectedReservationInfo> {
    
    /**
     * 更新保护区介绍
     * @param dto 保护区介绍DTO
     * @return 是否更新成功
     */
    boolean updateProtectedReservationInfo(ProtectedReservationInfoDTO dto);
    
    /**
     * 更新保护区介绍（支持视频文件上传）
     * @param dto 保护区介绍DTO
     * @param videoFiles 视频文件数组
     * @return 是否更新成功
     */
    boolean updateProtectedReservationInfo(ProtectedReservationInfoDTO dto, MultipartFile[] videoFiles);
    
    /**
     * 根据ID删除保护区介绍
     * @param id 保护区介绍ID
     * @return 是否删除成功
     */
    boolean deleteProtectedReservationInfo(Long id);
    
    /**
     * 根据ID获取保护区介绍
     * @param id 保护区介绍ID
     * @return 保护区介绍DTO
     */
    ProtectedReservationInfoDTO getProtectedReservationInfoById(Long id);
    
    /**
     * 分页查询保护区介绍列表
     * @param page 页码
     * @param size 每页大小
     * @param title 标题（可选）
     * @param contentType 内容类型（可选）
     * @param contentCategory 内容分类（可选）
     * @return 分页结果
     */
    PageResult<ProtectedReservationInfoDTO> getProtectedReservationInfoPage(
            Integer page, Integer size, String title, Byte contentType, Byte contentCategory);
    
    /**
     * 分页查询保护区介绍列表（增强版，支持发布人、发布时间、内容类型搜索）
     * @param page 页码
     * @param size 每页大小
     * @param title 标题（模糊搜索）
     * @param creatorName 发布人姓名（模糊搜索）
     * @param startTime 发布时间开始
     * @param endTime 发布时间结束
     * @param contentType 内容类型（精确匹配）
     * @return 分页结果
     */
    PageResult<ProtectedReservationInfoEnhancedDTO> getProtectedReservationInfoPageEnhanced(
            Integer page, Integer size, String title, String creatorName, 
            LocalDateTime startTime, LocalDateTime endTime, Byte contentType);
    
    /**
     * 获取所有未删除的保护区介绍列表
     * @return 保护区介绍列表
     */
    List<ProtectedReservationInfoDTO> getAllProtectedReservationInfo();
    
    /**
     * 根据内容类型获取保护区介绍列表
     * @param contentType 内容类型
     * @return 保护区介绍列表
     */
    List<ProtectedReservationInfoDTO> getProtectedReservationInfoByContentType(Byte contentType);
    
    /**
     * 根据内容分类获取保护区介绍列表
     * @param contentCategory 内容分类
     * @return 保护区介绍列表
     */
    List<ProtectedReservationInfoDTO> getProtectedReservationInfoByContentCategory(Byte contentCategory);
    
    /**
     * 保存保护区介绍（包含文件上传处理）
     * @param dto 保护区介绍DTO
     * @param videoFiles 视频文件数组
     * @return 是否保存成功
     * @throws Exception 异常
     */
    boolean saveProtectedReservationInfoWithFiles(ProtectedReservationInfoDTO dto, MultipartFile[] videoFiles) throws Exception;
    
    /**
     * 处理视频文件上传
     * @param videoFiles 视频文件数组
     * @return 视频文件ID列表
     * @throws Exception 异常
     */
    List<Long> processVideoFiles(MultipartFile[] videoFiles) throws Exception;
    
    /**
     * 处理视频文件上传（带用户ID）
     * @param videoFiles 视频文件数组
     * @param userId 上传用户ID
     * @return 视频文件ID列表
     * @throws Exception 异常
     */
    List<Long> processVideoFiles(MultipartFile[] videoFiles, Long userId) throws Exception;
    
    /**
     * 处理音频文件上传
     * @param audioFiles 音频文件数组
     * @param userId 上传用户ID
     * @return 音频文件ID列表
     * @throws Exception 异常
     */
    List<Long> processAudioFiles(MultipartFile[] audioFiles, Long userId) throws Exception;
    
    /**
     * 处理照片文件上传
     * @param photoFiles 照片文件数组
     * @param userId 上传用户ID
     * @return 照片文件ID列表
     * @throws Exception 异常
     */
    List<Long> processPhotoFiles(MultipartFile[] photoFiles, Long userId) throws Exception;
    
}
