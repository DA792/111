package com.scenic.service.system;

import com.scenic.common.dto.Result;
import com.scenic.dto.system.ThemeSettingDTO;
import com.scenic.entity.system.ThemeSetting;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 主题设置服务接口
 */
public interface ThemeSettingService {
    
    /**
     * 查询所有主题设置
     * @return 主题设置列表
     */
    Result<List<ThemeSettingDTO>> getAllThemeSettings();
    
    /**
     * 根据ID获取主题设置详情
     * @param id 主题设置ID
     * @return 主题设置详情
     */
    Result<ThemeSettingDTO> getThemeSettingById(Long id);
    
    /**
     * 获取默认主题设置
     * @return 默认主题设置
     */
    Result<ThemeSettingDTO> getDefaultThemeSetting();
    
    /**
     * 创建主题设置
     * @param themeSettingDTO 主题设置信息
     * @return 创建结果
     */
    Result<String> createThemeSetting(ThemeSettingDTO themeSettingDTO);
    
    /**
     * 更新主题设置
     * @param id 主题设置ID
     * @param themeSettingDTO 主题设置信息
     * @return 更新结果
     */
    Result<String> updateThemeSetting(Long id, ThemeSettingDTO themeSettingDTO);
    
    /**
     * 删除主题设置
     * @param id 主题设置ID
     * @return 删除结果
     */
    Result<String> deleteThemeSetting(Long id);
    
    /**
     * 设置默认主题
     * @param id 主题设置ID
     * @return 设置结果
     */
    Result<String> setDefaultTheme(Long id);
    
    /**
     * 根据主题名称获取主题设置
     * @param themeName 主题名称
     * @return 主题设置
     */
    ThemeSetting getThemeSettingByName(String themeName);
    
    /**
     * 保存主题设置
     * @param colorConfig 主题配置JSON字符串
     */
    void saveThemeSettings(String colorConfig);
    
    /**
     * 上传开屏页图片
     * @param file 图片文件
     * @return 上传结果
     */
    Result<String> uploadSplashScreenImage(MultipartFile file);
    
    /**
     * 上传首页背景图
     * @param file 图片文件
     * @return 上传结果
     */
    Result<String> uploadHomeBackgroundImage(MultipartFile file);
    
    /**
     * 更新开屏页图片
     * @param splashScreenImage 图片文件
     * @return 图片路径
     */
    String updateSplashScreenImage(MultipartFile splashScreenImage);
    
    /**
     * 获取开屏页图片
     * @return 图片路径
     */
    Result<String> getSplashScreenImage();
    
    /**
     * 获取首页背景图
     * @return 图片路径
     */
    Result<String> getHomeBackgroundImage();
    
    /**
     * 删除开屏页图片
     * @return 删除结果
     */
    Result<String> deleteSplashScreenImage();
    
    /**
     * 删除首页背景图
     * @return 删除结果
     */
    Result<String> deleteHomeBackgroundImage();
}
