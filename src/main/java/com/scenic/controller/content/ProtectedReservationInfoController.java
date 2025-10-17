package com.scenic.controller.content;

import com.scenic.common.dto.Result;
import com.scenic.dto.content.ProtectedReservationInfoDTO;
import com.scenic.dto.content.ProtectedReservationInfoEnhancedDTO;
import com.scenic.service.content.ProtectedReservationInfoService;
import com.scenic.common.dto.PageResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 保护区介绍控制器
 */
@RestController
@RequestMapping("/api/manage/content/protected-reservation")
public class ProtectedReservationInfoController {
    
    @Resource
    private ProtectedReservationInfoService protectedReservationInfoService;
    
    /**
     * 新增保护区介绍
     */
    @PostMapping
    public Result<Boolean> saveProtectedReservationInfo(@RequestBody ProtectedReservationInfoDTO dto) {
        boolean result = protectedReservationInfoService.saveProtectedReservationInfo(dto);
        return Result.success(result);
    }
    
    /**
     * 更新保护区介绍
     */
    @PutMapping
    public Result<Boolean> updateProtectedReservationInfo(@RequestBody ProtectedReservationInfoDTO dto) {
        boolean result = protectedReservationInfoService.updateProtectedReservationInfo(dto);
        return Result.success(result);
    }
    
    /**
     * 删除保护区介绍（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteProtectedReservationInfo(@PathVariable Long id) {
        boolean result = protectedReservationInfoService.deleteProtectedReservationInfo(id);
        return Result.success(result);
    }
    
    /**
     * 根据ID获取保护区介绍
     */
    @GetMapping("/{id}")
    public Result<ProtectedReservationInfoDTO> getProtectedReservationInfoById(@PathVariable Long id) {
        ProtectedReservationInfoDTO dto = protectedReservationInfoService.getProtectedReservationInfoById(id);
        return Result.success(dto);
    }
    
    /**
     * 分页查询保护区介绍列表
     */
    @GetMapping("/page")
    public Result<PageResult<ProtectedReservationInfoDTO>> getProtectedReservationInfoPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Byte contentType,
            @RequestParam(required = false) Byte contentCategory) {
        
        PageResult<ProtectedReservationInfoDTO> pageResult = protectedReservationInfoService.getProtectedReservationInfoPage(
                page, size, title, contentType, contentCategory);
        return Result.success(pageResult);
    }
    
    /**
     * 获取所有未删除的保护区介绍列表
     */
    @GetMapping("/list")
    public Result<List<ProtectedReservationInfoDTO>> getAllProtectedReservationInfo() {
        List<ProtectedReservationInfoDTO> list = protectedReservationInfoService.getAllProtectedReservationInfo();
        return Result.success(list);
    }
    
    /**
     * 根据内容类型获取保护区介绍列表
     */
    @GetMapping("/list-by-type")
    public Result<List<ProtectedReservationInfoDTO>> getProtectedReservationInfoByContentType(
            @RequestParam Byte contentType) {
        List<ProtectedReservationInfoDTO> list = protectedReservationInfoService.getProtectedReservationInfoByContentType(contentType);
        return Result.success(list);
    }
    
    /**
     * 根据内容分类获取保护区介绍列表
     */
    @GetMapping("/list-by-category")
    public Result<List<ProtectedReservationInfoDTO>> getProtectedReservationInfoByContentCategory(
            @RequestParam Byte contentCategory) {
        List<ProtectedReservationInfoDTO> list = protectedReservationInfoService.getProtectedReservationInfoByContentCategory(contentCategory);
        return Result.success(list);
    }
    
    /**
     * 分页查询保护区介绍列表（增强版，支持发布人、发布时间、内容类型搜索）
     */
    @GetMapping("/page-enhanced")
    public Result<PageResult<ProtectedReservationInfoEnhancedDTO>> getProtectedReservationInfoPageEnhanced(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String creatorName,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Byte contentType) {
        
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        
        if (startTime != null && !startTime.isEmpty()) {
            startDateTime = LocalDateTime.parse(startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            endDateTime = LocalDateTime.parse(endTime);
        }
        
        PageResult<ProtectedReservationInfoEnhancedDTO> pageResult = protectedReservationInfoService.getProtectedReservationInfoPageEnhanced(
                page, size, title, creatorName, startDateTime, endDateTime, contentType);
        return Result.success(pageResult);
    }
}