package com.scenic.service.content.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scenic.entity.content.ProtectedReservationInfo;
import com.scenic.mapper.content.ProtectedReservationInfoMapper;
import com.scenic.service.content.ProtectedReservationInfoService;
import com.scenic.dto.content.ProtectedReservationInfoDTO;
import com.scenic.dto.content.ProtectedReservationInfoEnhancedDTO;
import com.scenic.common.dto.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 保护区介绍服务实现类
 */
@Service
public class ProtectedReservationInfoServiceImpl extends ServiceImpl<ProtectedReservationInfoMapper, ProtectedReservationInfo> implements ProtectedReservationInfoService {
    
    @Resource
    private ProtectedReservationInfoMapper protectedReservationInfoMapper;
    
    @Resource
    private com.scenic.mapper.user.UserMapper userMapper;
    
    @Override
    @Transactional
    public boolean saveProtectedReservationInfo(ProtectedReservationInfoDTO dto) {
        ProtectedReservationInfo entity = new ProtectedReservationInfo();
        BeanUtils.copyProperties(dto, entity);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setDeleted((byte) 0); // 默认未删除
        return this.save(entity);
    }
    
    @Override
    @Transactional
    public boolean updateProtectedReservationInfo(ProtectedReservationInfoDTO dto) {
        ProtectedReservationInfo entity = new ProtectedReservationInfo();
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        return this.updateById(entity);
    }
    
    @Override
    @Transactional
    public boolean deleteProtectedReservationInfo(Long id) {
        ProtectedReservationInfo entity = new ProtectedReservationInfo();
        entity.setId(id);
        entity.setDeleted((byte) 1); // 逻辑删除
        entity.setUpdateTime(LocalDateTime.now());
        return this.updateById(entity);
    }
    
    @Override
    public ProtectedReservationInfoDTO getProtectedReservationInfoById(Long id) {
        ProtectedReservationInfo entity = this.getById(id);
        if (entity == null || entity.getDeleted() == 1) {
            return null;
        }
        ProtectedReservationInfoDTO dto = new ProtectedReservationInfoDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
    
    @Override
    public PageResult<ProtectedReservationInfoDTO> getProtectedReservationInfoPage(
            Integer page, Integer size, String title, Byte contentType, Byte contentCategory) {
        
        LambdaQueryWrapper<ProtectedReservationInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProtectedReservationInfo::getDeleted, (byte) 0); // 只查询未删除的
        
        if (title != null && !title.isEmpty()) {
            queryWrapper.like(ProtectedReservationInfo::getTitle, title);
        }
        if (contentType != null) {
            queryWrapper.eq(ProtectedReservationInfo::getContentType, contentType);
        }
        if (contentCategory != null) {
            queryWrapper.eq(ProtectedReservationInfo::getContentCategory, contentCategory);
        }
        
        queryWrapper.orderByDesc(ProtectedReservationInfo::getCreateTime);
        
        IPage<ProtectedReservationInfo> pageResult = this.page(new Page<>(page, size), queryWrapper);
        
        List<ProtectedReservationInfoDTO> dtoList = pageResult.getRecords().stream().map(entity -> {
            ProtectedReservationInfoDTO dto = new ProtectedReservationInfoDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
        
        return new PageResult<ProtectedReservationInfoDTO>(pageResult.getTotal(), (int)pageResult.getSize(), (int)pageResult.getCurrent(), dtoList);
    }
    
    @Override
    public List<ProtectedReservationInfoDTO> getAllProtectedReservationInfo() {
        LambdaQueryWrapper<ProtectedReservationInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProtectedReservationInfo::getDeleted, (byte) 0);
        queryWrapper.orderByDesc(ProtectedReservationInfo::getCreateTime);
        
        return this.list(queryWrapper).stream().map(entity -> {
            ProtectedReservationInfoDTO dto = new ProtectedReservationInfoDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<ProtectedReservationInfoDTO> getProtectedReservationInfoByContentType(Byte contentType) {
        List<ProtectedReservationInfo> entityList = protectedReservationInfoMapper.selectByContentType(contentType);
        return entityList.stream().map(entity -> {
            ProtectedReservationInfoDTO dto = new ProtectedReservationInfoDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<ProtectedReservationInfoDTO> getProtectedReservationInfoByContentCategory(Byte contentCategory) {
        List<ProtectedReservationInfo> entityList = protectedReservationInfoMapper.selectByContentCategory(contentCategory);
        return entityList.stream().map(entity -> {
            ProtectedReservationInfoDTO dto = new ProtectedReservationInfoDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    public PageResult<ProtectedReservationInfoEnhancedDTO> getProtectedReservationInfoPageEnhanced(
            Integer page, Integer size, String title, String creatorName, 
            LocalDateTime startTime, LocalDateTime endTime, Byte contentType) {
        
        int offset = (page - 1) * size;
        
        // 查询数据
        List<ProtectedReservationInfoEnhancedDTO> dataList = protectedReservationInfoMapper.selectPageEnhanced(
                offset, size, title, creatorName, startTime, endTime, contentType);
        
        // 查询总数
        int total = protectedReservationInfoMapper.selectCountEnhanced(
                title, creatorName, startTime, endTime, contentType);
        
        return new PageResult<>(total, size, page, dataList);
    }
}