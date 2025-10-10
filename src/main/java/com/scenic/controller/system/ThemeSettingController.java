package com.scenic.controller.system;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scenic.common.dto.Result;
import com.scenic.dto.system.ThemeSettingDTO;
import com.scenic.service.system.ThemeSettingService;

/**
 * 主题设置控制器
 */
@RestController
@RequestMapping("/api/manage/theme-settings")
public class ThemeSettingController {
    
    @Autowired
    private ThemeSettingService themeSettingService;
    
    /**
     * 查询所有主题设置
     * @return 主题设置列表
     */
    @GetMapping
    public Result<List<ThemeSettingDTO>> getAllThemeSettings() {
        return themeSettingService.getAllThemeSettings();
    }
    
    /**
     * 根据ID获取主题设置详情
     * @param id 主题设置ID
     * @return 主题设置详情
     */
    @GetMapping("/{id}")
    public Result<ThemeSettingDTO> getThemeSettingById(@PathVariable Long id) {
        return themeSettingService.getThemeSettingById(id);
    }
    
    /**
     * 获取默认主题设置
     * @return 默认主题设置
     */
    @GetMapping("/default")
    public Result<ThemeSettingDTO> getDefaultThemeSetting() {
        return themeSettingService.getDefaultThemeSetting();
    }
    
    /**
     * 创建主题设置
     * @param themeSettingDTO 主题设置信息
     * @return 创建结果
     */
    @PostMapping
    public Result<String> createThemeSetting(@RequestBody ThemeSettingDTO themeSettingDTO) {
        return themeSettingService.createThemeSetting(themeSettingDTO);
    }
    
    /**
     * 更新主题设置
     * @param id 主题设置ID
     * @param themeSettingDTO 主题设置信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<String> updateThemeSetting(@PathVariable Long id, @RequestBody ThemeSettingDTO themeSettingDTO) {
        return themeSettingService.updateThemeSetting(id, themeSettingDTO);
    }
    
    /**
     * 删除主题设置
     * @param id 主题设置ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteThemeSetting(@PathVariable Long id) {
        return themeSettingService.deleteThemeSetting(id);
    }
    
    /**
     * 设置默认主题
     * @param id 主题设置ID
     * @return 设置结果
     */
    @PostMapping("/{id}/default")
    public Result<String> setDefaultTheme(@PathVariable Long id) {
        return themeSettingService.setDefaultTheme(id);
    }
}
