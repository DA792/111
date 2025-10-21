package com.scenic.controller.content;

import com.scenic.common.dto.Result;
import com.scenic.common.dto.PageResult;
import com.scenic.dto.content.ProtectedReservationInfoWithIdsDTO;
import com.scenic.service.content.ProtectedReservationInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 小程序端保护区介绍控制器
 * 提供小程序端的API接口，无需token鉴权
 */
@RestController
@RequestMapping("/api/uniapp/protected-reservation")
public class MiniProgramProtectedReservationInfoController {
    
    @Resource
    private ProtectedReservationInfoService protectedReservationInfoService;
    
    /**
     * 小程序端根据ID获取保护区介绍详情
     * @param id 保护区介绍ID
     * @return 保护区介绍详情
     */
    @GetMapping("/{id}")
    public Result<ProtectedReservationInfoWithIdsDTO> getProtectedReservationInfoById(@PathVariable Long id) {
        ProtectedReservationInfoWithIdsDTO dto = protectedReservationInfoService.getProtectedReservationInfoWithIdsById(id);
        return Result.success(dto);
    }
    
    /**
     * 小程序端分页查询保护区介绍列表（只支持标题模糊搜索）
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @param title 标题（模糊搜索，可选）
     * @return 保护区介绍列表
     */
    @GetMapping("/page")
    public Result<PageResult<Map<String, Object>>> getProtectedReservationInfoPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title) {
        
        // 调用增强版分页查询方法，只传入title参数，其他参数设为null
        PageResult<Map<String, Object>> pageResult = protectedReservationInfoService.getProtectedReservationInfoPageEnhanced(
                page, size, title, null, null, null, null);
        return Result.success(pageResult);
    }
}