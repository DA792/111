package com.scenic.service.content.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.scenic.common.dto.Result;
import com.scenic.dto.content.ProtectedAreaIntroductionDTO;
import com.scenic.entity.content.ProtectedAreaIntroduction;
import com.scenic.mapper.content.ProtectedAreaIntroductionMapper;
import com.scenic.service.content.ProtectedAreaIntroductionService;

/**
 * 保护区介绍服务实现类
 */
@Service
public class ProtectedAreaIntroductionServiceImpl implements ProtectedAreaIntroductionService {
    
    @Autowired
    private ProtectedAreaIntroductionMapper protectedAreaIntroductionMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // Redis缓存键前缀
    private static final String INTRODUCTION_CACHE_PREFIX = "introduction:";
    private static final String ALL_INTRODUCTIONS_CACHE_KEY = "all_introductions";
    private static final String INTRODUCTIONS_BY_LANGUAGE_CACHE_PREFIX = "introductions_language:";
    
    /**
     * 新增保护区介绍
     * @param protectedAreaIntroductionDTO 保护区介绍信息
     * @return 操作结果
     */
    @Override
    public Result<String> addProtectedAreaIntroduction(ProtectedAreaIntroductionDTO protectedAreaIntroductionDTO) {
        try {
            ProtectedAreaIntroduction introduction = new ProtectedAreaIntroduction();
            introduction.setTitle(protectedAreaIntroductionDTO.getTitle());
            introduction.setContent(protectedAreaIntroductionDTO.getContent());
            introduction.setLanguage(protectedAreaIntroductionDTO.getLanguage());
            introduction.setImageUrl(protectedAreaIntroductionDTO.getImageUrl());
            introduction.setAudioUrl(protectedAreaIntroductionDTO.getAudioUrl());
            introduction.setVideoUrl(protectedAreaIntroductionDTO.getVideoUrl());
            introduction.setSortOrder(protectedAreaIntroductionDTO.getSortOrder());
            introduction.setEnabled(true);
            introduction.setCreateTime(LocalDateTime.now());
            introduction.setUpdateTime(LocalDateTime.now());
            
            protectedAreaIntroductionMapper.insert(introduction);
            
            // 清除相关缓存
            redisTemplate.delete(ALL_INTRODUCTIONS_CACHE_KEY);
            redisTemplate.delete(INTRODUCTIONS_BY_LANGUAGE_CACHE_PREFIX + protectedAreaIntroductionDTO.getLanguage());
            
            return Result.success("操作成功", "保护区介绍新增成功");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取所有保护区介绍
     * @return 保护区介绍列表
     */
    @Override
    public Result<List<ProtectedAreaIntroductionDTO>> getAllProtectedAreaIntroductions() {
        try {
            // 先从Redis缓存中获取
            List<ProtectedAreaIntroductionDTO> cachedIntroductions = (List<ProtectedAreaIntroductionDTO>) redisTemplate.opsForValue().get(ALL_INTRODUCTIONS_CACHE_KEY);
            if (cachedIntroductions != null) {
                return Result.success("查询成功", cachedIntroductions);
            }
            
            // 缓存中没有则从数据库查询
            List<ProtectedAreaIntroduction> introductions = protectedAreaIntroductionMapper.selectAllEnabled();
            List<ProtectedAreaIntroductionDTO> introductionDTOs = introductions.stream()
                    .map(this::convertToDTO)
                    .sorted((a, b) -> a.getSortOrder().compareTo(b.getSortOrder()))
                    .collect(Collectors.toList());
            
            // 将结果存入Redis缓存，过期时间1小时
            redisTemplate.opsForValue().set(ALL_INTRODUCTIONS_CACHE_KEY, introductionDTOs, 1, TimeUnit.HOURS);
            
            return Result.success("查询成功", introductionDTOs);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据语言获取保护区介绍
     * @param language 语言
     * @return 保护区介绍列表
     */
    @Override
    public Result<List<ProtectedAreaIntroductionDTO>> getProtectedAreaIntroductionsByLanguage(String language) {
        try {
            // 先从Redis缓存中获取
            String cacheKey = INTRODUCTIONS_BY_LANGUAGE_CACHE_PREFIX + language;
            List<ProtectedAreaIntroductionDTO> cachedIntroductions = (List<ProtectedAreaIntroductionDTO>) redisTemplate.opsForValue().get(cacheKey);
            if (cachedIntroductions != null) {
                return Result.success("查询成功", cachedIntroductions);
            }
            
            // 缓存中没有则从数据库查询
            List<ProtectedAreaIntroduction> introductions = protectedAreaIntroductionMapper.selectByLanguage(language, 0, 1000);
            List<ProtectedAreaIntroductionDTO> introductionDTOs = introductions.stream()
                    .map(this::convertToDTO)
                    .sorted((a, b) -> a.getSortOrder().compareTo(b.getSortOrder()))
                    .collect(Collectors.toList());
            
            // 将结果存入Redis缓存，过期时间1小时
            redisTemplate.opsForValue().set(cacheKey, introductionDTOs, 1, TimeUnit.HOURS);
            
            return Result.success("查询成功", introductionDTOs);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取保护区介绍详情
     * @param id 保护区介绍ID
     * @return 保护区介绍详情
     */
    @Override
    public Result<ProtectedAreaIntroductionDTO> getProtectedAreaIntroductionById(Long id) {
        try {
            // 先从Redis缓存中获取
            String cacheKey = INTRODUCTION_CACHE_PREFIX + id;
            ProtectedAreaIntroductionDTO cachedIntroduction = (ProtectedAreaIntroductionDTO) redisTemplate.opsForValue().get(cacheKey);
            if (cachedIntroduction != null) {
                return Result.success("查询成功", cachedIntroduction);
            }
            
            // 缓存中没有则从数据库查询
            ProtectedAreaIntroduction introduction = protectedAreaIntroductionMapper.selectById(id);
            
            if (introduction != null && introduction.getEnabled()) {
                ProtectedAreaIntroductionDTO introductionDTO = convertToDTO(introduction);
                
                // 将结果存入Redis缓存，过期时间1小时
                redisTemplate.opsForValue().set(cacheKey, introductionDTO, 1, TimeUnit.HOURS);
                return Result.success("查询成功", introductionDTO);
            } else {
                return Result.error("保护区介绍不存在");
            }
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理端 - 更新保护区介绍
     * @param id 保护区介绍ID
     * @param protectedAreaIntroductionDTO 保护区介绍信息
     * @return 操作结果
     */
    @Override
    public Result<String> updateProtectedAreaIntroduction(Long id, ProtectedAreaIntroductionDTO protectedAreaIntroductionDTO) {
        try {
            ProtectedAreaIntroduction introduction = protectedAreaIntroductionMapper.selectById(id);
            if (introduction != null && introduction.getEnabled()) {
                // 获取更新前的语言
                String oldLanguage = introduction.getLanguage();
                
                introduction.setTitle(protectedAreaIntroductionDTO.getTitle());
                introduction.setContent(protectedAreaIntroductionDTO.getContent());
                introduction.setLanguage(protectedAreaIntroductionDTO.getLanguage());
                introduction.setImageUrl(protectedAreaIntroductionDTO.getImageUrl());
                introduction.setAudioUrl(protectedAreaIntroductionDTO.getAudioUrl());
                introduction.setVideoUrl(protectedAreaIntroductionDTO.getVideoUrl());
                introduction.setSortOrder(protectedAreaIntroductionDTO.getSortOrder());
                introduction.setUpdateTime(LocalDateTime.now());
                
                protectedAreaIntroductionMapper.updateById(introduction);
                
                // 更新缓存
                String cacheKey = INTRODUCTION_CACHE_PREFIX + id;
                ProtectedAreaIntroductionDTO updatedDTO = convertToDTO(introduction);
                redisTemplate.opsForValue().set(cacheKey, updatedDTO, 1, TimeUnit.HOURS);
                
                // 清除相关缓存
                redisTemplate.delete(ALL_INTRODUCTIONS_CACHE_KEY);
                redisTemplate.delete(INTRODUCTIONS_BY_LANGUAGE_CACHE_PREFIX + oldLanguage);
                redisTemplate.delete(INTRODUCTIONS_BY_LANGUAGE_CACHE_PREFIX + protectedAreaIntroductionDTO.getLanguage());
                
                return Result.success("操作成功", "保护区介绍更新成功");
            }
            return Result.error("保护区介绍不存在");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理端 - 删除保护区介绍
     * @param id 保护区介绍ID
     * @return 操作结果
     */
    @Override
    public Result<String> deleteProtectedAreaIntroduction(Long id) {
        try {
            ProtectedAreaIntroduction introduction = protectedAreaIntroductionMapper.selectById(id);
            if (introduction != null) {
                // 获取删除前的语言
                String language = introduction.getLanguage();
                
                introduction.setEnabled(false);
                introduction.setUpdateTime(LocalDateTime.now());
                protectedAreaIntroductionMapper.updateById(introduction);
                
                // 删除缓存
                String cacheKey = INTRODUCTION_CACHE_PREFIX + id;
                redisTemplate.delete(cacheKey);
                
                // 清除相关缓存
                redisTemplate.delete(ALL_INTRODUCTIONS_CACHE_KEY);
                redisTemplate.delete(INTRODUCTIONS_BY_LANGUAGE_CACHE_PREFIX + language);
                
                return Result.success("操作成功", "保护区介绍已删除");
            }
            return Result.error("保护区介绍不存在");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 将ProtectedAreaIntroduction实体转换为ProtectedAreaIntroductionDTO
     * @param introduction ProtectedAreaIntroduction实体
     * @return ProtectedAreaIntroductionDTO
     */
    private ProtectedAreaIntroductionDTO convertToDTO(ProtectedAreaIntroduction introduction) {
        ProtectedAreaIntroductionDTO dto = new ProtectedAreaIntroductionDTO();
        dto.setId(introduction.getId());
        dto.setTitle(introduction.getTitle());
        dto.setContent(introduction.getContent());
        dto.setLanguage(introduction.getLanguage());
        dto.setImageUrl(introduction.getImageUrl());
        dto.setAudioUrl(introduction.getAudioUrl());
        dto.setVideoUrl(introduction.getVideoUrl());
        dto.setSortOrder(introduction.getSortOrder());
        dto.setEnabled(introduction.getEnabled());
        dto.setCreateTime(introduction.getCreateTime());
        dto.setUpdateTime(introduction.getUpdateTime());
        return dto;
    }
}
