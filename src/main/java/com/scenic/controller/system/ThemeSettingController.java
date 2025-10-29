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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
     * 更新主题设置（不带ID）
     * @param themeSettingDTO 主题设置信息
     * @return 更新结果
     */
    @PutMapping
    public Result<String> updateThemeSettingWithoutId(@RequestBody ThemeSettingDTO themeSettingDTO) {
        // 对于主题设置，使用固定的ID 1，因为我们只有一个主题配置
        return themeSettingService.updateThemeSetting(1L, themeSettingDTO);
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

    /**
     * 上传开屏页图片
     * @param splashScreenImage 图片文件
     * @return 上传结果
     */
    @PostMapping("/splash-screen/upload")
    public Result<String> uploadSplashScreenImage(@RequestParam("splashScreenImage") MultipartFile splashScreenImage) {
        if (splashScreenImage == null || splashScreenImage.isEmpty()) {
            return Result.error("开屏页图片不能为空");
        }
        try {
            String imageUrl = themeSettingService.updateSplashScreenImage(splashScreenImage);
            return Result.success(imageUrl);
        } catch (Exception e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传首页背景图
     * @param file 图片文件
     * @return 上传结果
     */
    @PostMapping("/home-background/upload")
    public Result<String> uploadHomeBackgroundImage(@RequestParam("file") MultipartFile file) {
        // TODO: 实现首页背景图上传逻辑
        return Result.success("首页背景图上传成功");
    }
    
    /**
     * 获取开屏页图片
     * @return 图片路径
     */
    @GetMapping("/splash-screen")
    public Result<String> getSplashScreenImage() {
        // TODO: 实现获取开屏页图片逻辑
        return Result.success("splash_screen.jpg");
    }
    
    /**
     * 获取首页背景图
     * @return 图片路径
     */
    @GetMapping("/home-background")
    public Result<String> getHomeBackgroundImage() {
        // TODO: 实现获取首页背景图逻辑
        return Result.success("home_background.jpg");
    }
    
    /**
     * 删除开屏页图片
     * @return 删除结果
     */
    @DeleteMapping("/splash-screen")
    public Result<String> deleteSplashScreenImage() {
        // TODO: 实现删除开屏页图片逻辑
        return Result.success("开屏页图片删除成功");
    }
    
    /**
     * 删除首页背景图
     * @return 删除结果
     */
    @DeleteMapping("/home-background")
    public Result<String> deleteHomeBackgroundImage() {
        // TODO: 实现删除首页背景图逻辑
        return Result.success("首页背景图删除成功");
    }
}
