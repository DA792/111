package com.scenic.service.interaction.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.scenic.common.dto.Result;
import com.scenic.dto.interaction.ArContentDTO;
import com.scenic.entity.interaction.ArContent;
import com.scenic.mapper.interaction.ArContentMapper;
import com.scenic.service.interaction.ArContentService;

/**
 * AR内容服务实现类
 */
@Service
public class ArContentServiceImpl implements ArContentService {
    
    @Autowired
    private ArContentMapper arContentMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // Redis缓存键前缀
    private static final String AR_CONTENT_CACHE_PREFIX = "ar_content:";
    private static final String ALL_AR_CONTENTS_CACHE_KEY = "all_ar_contents";
    private static final String AR_CONTENTS_BY_TARGET_CACHE_PREFIX = "ar_contents_target:";
    private static final String AR_CONTENTS_BY_CONTENT_TYPE_CACHE_PREFIX = "ar_contents_content_type:";
    
    /**
     * 上传AR内容
     * @param arContentDTO AR内容信息
     * @return 操作结果
     */
    @Override
    public Result<String> uploadArContent(ArContentDTO arContentDTO) {
        try {
            ArContent content = new ArContent();
            content.setTitle(arContentDTO.getTitle());
            content.setDescription(arContentDTO.getDescription());
            content.setContentUrl(arContentDTO.getContentUrl());
            content.setContentType(arContentDTO.getContentType());
            content.setTargetId(arContentDTO.getTargetId());
            content.setTargetType(arContentDTO.getTargetType());
            content.setLatitude(arContentDTO.getLatitude());
            content.setLongitude(arContentDTO.getLongitude());
            content.setEnabled(true);
            content.setCreateTime(LocalDateTime.now());
            content.setUpdateTime(LocalDateTime.now());
            
            arContentMapper.insert(content);
            
            // 清除相关缓存
            redisTemplate.delete(ALL_AR_CONTENTS_CACHE_KEY);
            redisTemplate.delete(AR_CONTENTS_BY_TARGET_CACHE_PREFIX + arContentDTO.getTargetId() + "_" + arContentDTO.getTargetType());
            redisTemplate.delete(AR_CONTENTS_BY_CONTENT_TYPE_CACHE_PREFIX + arContentDTO.getContentType());
            
            return Result.success("操作成功", "AR内容上传成功");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取所有AR内容
     * @return AR内容列表
     */
    @Override
    public Result<List<ArContentDTO>> getAllArContents() {
        try {
            // 先从Redis缓存中获取
            List<ArContentDTO> cachedContents = (List<ArContentDTO>) redisTemplate.opsForValue().get(ALL_AR_CONTENTS_CACHE_KEY);
            if (cachedContents != null) {
                return Result.success("查询成功", cachedContents);
            }
            
            // 缓存中没有则从数据库查询
            List<ArContent> contents = arContentMapper.selectAllEnabled();
            List<ArContentDTO> contentDTOs = contents.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // 将结果存入Redis缓存，过期时间1小时
            redisTemplate.opsForValue().set(ALL_AR_CONTENTS_CACHE_KEY, contentDTOs, 1, TimeUnit.HOURS);
            
            return Result.success("查询成功", contentDTOs);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据目标ID和类型获取AR内容
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return AR内容列表
     */
    @Override
    public Result<List<ArContentDTO>> getArContentsByTarget(String targetId, String targetType) {
        try {
            // 先从Redis缓存中获取
            String cacheKey = AR_CONTENTS_BY_TARGET_CACHE_PREFIX + targetId + "_" + targetType;
            List<ArContentDTO> cachedContents = (List<ArContentDTO>) redisTemplate.opsForValue().get(cacheKey);
            if (cachedContents != null) {
                return Result.success("查询成功", cachedContents);
            }
            
            // 缓存中没有则从数据库查询
            List<ArContent> contents = arContentMapper.selectByTarget(targetId, targetType);
            List<ArContentDTO> contentDTOs = contents.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // 将结果存入Redis缓存，过期时间1小时
            redisTemplate.opsForValue().set(cacheKey, contentDTOs, 1, TimeUnit.HOURS);
            
            return Result.success("查询成功", contentDTOs);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据内容类型获取AR内容
     * @param contentType 内容类型
     * @return AR内容列表
     */
    @Override
    public Result<List<ArContentDTO>> getArContentsByContentType(String contentType) {
        try {
            // 先从Redis缓存中获取
            String cacheKey = AR_CONTENTS_BY_CONTENT_TYPE_CACHE_PREFIX + contentType;
            List<ArContentDTO> cachedContents = (List<ArContentDTO>) redisTemplate.opsForValue().get(cacheKey);
            if (cachedContents != null) {
                return Result.success("查询成功", cachedContents);
            }
            
            // 缓存中没有则从数据库查询
            List<ArContent> contents = arContentMapper.selectByContentType(contentType, 0, 1000);
            List<ArContentDTO> contentDTOs = contents.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // 将结果存入Redis缓存，过期时间1小时
            redisTemplate.opsForValue().set(cacheKey, contentDTOs, 1, TimeUnit.HOURS);
            
            return Result.success("查询成功", contentDTOs);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除AR内容
     * @param id AR内容ID
     * @return 操作结果
     */
    @Override
    public Result<String> deleteArContent(Long id) {
        try {
            ArContent content = arContentMapper.selectById(id);
            if (content != null) {
                content.setEnabled(false);
                content.setUpdateTime(LocalDateTime.now());
                arContentMapper.updateById(content);
                
                // 删除缓存
                String cacheKey = AR_CONTENT_CACHE_PREFIX + id;
                redisTemplate.delete(cacheKey);
                
                // 清除相关缓存
                redisTemplate.delete(ALL_AR_CONTENTS_CACHE_KEY);
                redisTemplate.delete(AR_CONTENTS_BY_TARGET_CACHE_PREFIX + content.getTargetId() + "_" + content.getTargetType());
                redisTemplate.delete(AR_CONTENTS_BY_CONTENT_TYPE_CACHE_PREFIX + content.getContentType());
                
                return Result.success("操作成功", "AR内容已删除");
            }
            return Result.error("AR内容不存在");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 修改AR内容
     * @param id AR内容ID
     * @param arContentDTO AR内容信息
     * @return 操作结果
     */
    @Override
    public Result<String> updateArContent(Long id, ArContentDTO arContentDTO) {
        try {
            ArContent content = arContentMapper.selectById(id);
            if (content != null && content.getEnabled()) {
                // 获取更新前的属性
                String oldTargetId = content.getTargetId();
                String oldTargetType = content.getTargetType();
                String oldContentType = content.getContentType();
                
                content.setTitle(arContentDTO.getTitle());
                content.setDescription(arContentDTO.getDescription());
                content.setContentUrl(arContentDTO.getContentUrl());
                content.setContentType(arContentDTO.getContentType());
                content.setTargetId(arContentDTO.getTargetId());
                content.setTargetType(arContentDTO.getTargetType());
                content.setLatitude(arContentDTO.getLatitude());
                content.setLongitude(arContentDTO.getLongitude());
                content.setUpdateTime(LocalDateTime.now());
                
                arContentMapper.updateById(content);
                
                // 更新缓存
                String cacheKey = AR_CONTENT_CACHE_PREFIX + id;
                ArContentDTO updatedDTO = convertToDTO(content);
                redisTemplate.opsForValue().set(cacheKey, updatedDTO, 1, TimeUnit.HOURS);
                
                // 清除相关缓存
                redisTemplate.delete(ALL_AR_CONTENTS_CACHE_KEY);
                redisTemplate.delete(AR_CONTENTS_BY_TARGET_CACHE_PREFIX + oldTargetId + "_" + oldTargetType);
                redisTemplate.delete(AR_CONTENTS_BY_TARGET_CACHE_PREFIX + arContentDTO.getTargetId() + "_" + arContentDTO.getTargetType());
                redisTemplate.delete(AR_CONTENTS_BY_CONTENT_TYPE_CACHE_PREFIX + oldContentType);
                redisTemplate.delete(AR_CONTENTS_BY_CONTENT_TYPE_CACHE_PREFIX + arContentDTO.getContentType());
                
                return Result.success("操作成功", "AR内容更新成功");
            }
            return Result.error("AR内容不存在");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 将ArContent实体转换为ArContentDTO
     * @param content ArContent实体
     * @return ArContentDTO
     */
    private ArContentDTO convertToDTO(ArContent content) {
        ArContentDTO dto = new ArContentDTO();
        dto.setId(content.getId());
        dto.setTitle(content.getTitle());
        dto.setDescription(content.getDescription());
        dto.setContentUrl(content.getContentUrl());
        dto.setContentType(content.getContentType());
        dto.setTargetId(content.getTargetId());
        dto.setTargetType(content.getTargetType());
        dto.setLatitude(content.getLatitude());
        dto.setLongitude(content.getLongitude());
        dto.setEnabled(content.getEnabled());
        dto.setCreateTime(content.getCreateTime());
        dto.setUpdateTime(content.getUpdateTime());
        return dto;
    }
}
