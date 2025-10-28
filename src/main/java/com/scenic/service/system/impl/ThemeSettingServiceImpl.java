package com.scenic.service.system.impl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.scenic.common.dto.Result;
import com.scenic.dto.system.ThemeSettingDTO;
import com.scenic.entity.system.ThemeSetting;
import com.scenic.mapper.system.ThemeSettingMapper;
import com.scenic.service.system.ThemeSettingService;

/**
 * 主题设置服务实现类
 */
@Service
public class ThemeSettingServiceImpl implements ThemeSettingService {
    
    @Autowired
    private ThemeSettingMapper themeSettingMapper;
    
    /**
     * 查询所有主题设置
     * @return 主题设置列表
     */
    @Override
    public Result<List<ThemeSettingDTO>> getAllThemeSettings() {
        try {
            // 查询所有主题设置
            List<ThemeSetting> themeSettings = themeSettingMapper.selectAll();
            
            // 转换为DTO列表
            List<ThemeSettingDTO> themeSettingDTOs = themeSettings.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            return Result.success(themeSettingDTOs);
        } catch (Exception e) {
            return Result.error("查询主题设置列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取主题设置详情
     * @param id 主题设置ID
     * @return 主题设置详情
     */
    @Override
    public Result<ThemeSettingDTO> getThemeSettingById(Long id) {
        try {
            ThemeSetting themeSetting = themeSettingMapper.selectById(id);
            if (themeSetting == null) {
                return Result.error("主题设置不存在");
            }
            return Result.success(convertToDTO(themeSetting));
        } catch (Exception e) {
            return Result.error("查询主题设置详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取默认主题设置
     * @return 默认主题设置
     */
    @Override
    public Result<ThemeSettingDTO> getDefaultThemeSetting() {
        try {
            ThemeSetting themeSetting = themeSettingMapper.selectDefault();
            if (themeSetting == null) {
                return Result.error("默认主题设置不存在");
            }
            return Result.success(convertToDTO(themeSetting));
        } catch (Exception e) {
            return Result.error("查询默认主题设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建主题设置
     * @param themeSettingDTO 主题设置信息
     * @return 创建结果
     */
    @Override
    public Result<String> createThemeSetting(ThemeSettingDTO themeSettingDTO) {
        try {
            // 转换为实体类
            ThemeSetting themeSetting = convertToEntity(themeSettingDTO);
            themeSetting.setCreateTime(LocalDateTime.now());
            themeSetting.setUpdateTime(LocalDateTime.now());
            
            // 插入数据库
            int result = themeSettingMapper.insert(themeSetting);
            if (result > 0) {
                return Result.success("主题设置创建成功");
            } else {
                return Result.error("主题设置创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建主题设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新主题设置
     * @param id 主题设置ID
     * @param themeSettingDTO 主题设置信息
     * @return 更新结果
     */
    @Override
    public Result<String> updateThemeSetting(Long id, ThemeSettingDTO themeSettingDTO) {
        try {
            // 检查主题设置是否存在
            ThemeSetting existingThemeSetting = themeSettingMapper.selectById(id);
            if (existingThemeSetting == null) {
                return Result.error("主题设置不存在");
            }
            
            // 转换为实体类
            ThemeSetting themeSetting = convertToEntity(themeSettingDTO);
            themeSetting.setId(id);
            themeSetting.setUpdateTime(LocalDateTime.now());
            
            // 更新数据库
            int result = themeSettingMapper.update(themeSetting);
            if (result > 0) {
                return Result.success("主题设置更新成功");
            } else {
                return Result.error("主题设置更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新主题设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除主题设置
     * @param id 主题设置ID
     * @return 删除结果
     */
    @Override
    public Result<String> deleteThemeSetting(Long id) {
        try {
            // 检查主题设置是否存在
            ThemeSetting existingThemeSetting = themeSettingMapper.selectById(id);
            if (existingThemeSetting == null) {
                return Result.error("主题设置不存在");
            }
            
            // 删除数据库记录
            int result = themeSettingMapper.deleteById(id);
            if (result > 0) {
                return Result.success("主题设置删除成功");
            } else {
                return Result.error("主题设置删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除主题设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置默认主题
     * @param id 主题设置ID
     * @return 设置结果
     */
    @Override
    public Result<String> setDefaultTheme(Long id) {
        try {
            // 检查主题设置是否存在
            ThemeSetting existingThemeSetting = themeSettingMapper.selectById(id);
            if (existingThemeSetting == null) {
                return Result.error("主题设置不存在");
            }
            
            // 设置默认主题
            int result = themeSettingMapper.setDefault(id);
            if (result > 0) {
                return Result.success("默认主题设置成功");
            } else {
                return Result.error("默认主题设置失败");
            }
        } catch (Exception e) {
            return Result.error("设置默认主题失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据主题名称获取主题设置
     * @param themeName 主题名称
     * @return 主题设置
     */
    @Override
    public ThemeSetting getThemeSettingByName(String themeName) {
        return themeSettingMapper.selectByThemeName(themeName);
    }
    
    /**
     * 将实体类转换为DTO
     * @param themeSetting 主题设置实体
     * @return 主题设置DTO
     */
    private ThemeSettingDTO convertToDTO(ThemeSetting themeSetting) {
        ThemeSettingDTO themeSettingDTO = new ThemeSettingDTO();
        BeanUtils.copyProperties(themeSetting, themeSettingDTO);
        return themeSettingDTO;
    }
    
    /**
     * 将DTO转换为实体类
     * @param themeSettingDTO 主题设置DTO
     * @return 主题设置实体
     */
    private ThemeSetting convertToEntity(ThemeSettingDTO themeSettingDTO) {
        ThemeSetting themeSetting = new ThemeSetting();
        BeanUtils.copyProperties(themeSettingDTO, themeSetting);
        return themeSetting;
    }
    
    // 以下为新增的图片处理方法
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Override
    public Result<String> uploadSplashScreenImage(MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        
        try {
            // 创建上传目录
            File uploadDir = new File(uploadPath + "/theme");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = "splash_screen_" + UUID.randomUUID().toString() + fileExtension;
            
            // 保存文件
            File dest = new File(uploadDir.getAbsolutePath() + "/" + newFilename);
            file.transferTo(dest);
            
            // 删除旧的开屏页图片（如果存在）
            File oldSplashScreen = new File(uploadDir.getAbsolutePath() + "/splash_screen.jpg");
            if (oldSplashScreen.exists()) {
                oldSplashScreen.delete();
            }
            
            // 重命名新文件为固定名称
            File newFile = new File(uploadDir.getAbsolutePath() + "/splash_screen.jpg");
            dest.renameTo(newFile);
            
            return Result.success("开屏页图片上传成功");
        } catch (IOException e) {
            return Result.error("开屏页图片上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> uploadHomeBackgroundImage(MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        
        try {
            // 创建上传目录
            File uploadDir = new File(uploadPath + "/theme");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = "home_background_" + UUID.randomUUID().toString() + fileExtension;
            
            // 保存文件
            File dest = new File(uploadDir.getAbsolutePath() + "/" + newFilename);
            file.transferTo(dest);
            
            // 删除旧的首页背景图（如果存在）
            File oldHomeBackground = new File(uploadDir.getAbsolutePath() + "/home_background.jpg");
            if (oldHomeBackground.exists()) {
                oldHomeBackground.delete();
            }
            
            // 重命名新文件为固定名称
            File newFile = new File(uploadDir.getAbsolutePath() + "/home_background.jpg");
            dest.renameTo(newFile);
            
            return Result.success("首页背景图上传成功");
        } catch (IOException e) {
            return Result.error("首页背景图上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> getSplashScreenImage() {
        File splashScreen = new File(uploadPath + "/theme/splash_screen.jpg");
        if (splashScreen.exists()) {
            return Result.success("/theme/splash_screen.jpg");
        } else {
            return Result.success(""); // 返回空字符串表示没有设置开屏页图片
        }
    }
    
    @Override
    public Result<String> getHomeBackgroundImage() {
        File homeBackground = new File(uploadPath + "/theme/home_background.jpg");
        if (homeBackground.exists()) {
            return Result.success("/theme/home_background.jpg");
        } else {
            return Result.success(""); // 返回空字符串表示没有设置首页背景图
        }
    }
    
    @Override
    public Result<String> deleteSplashScreenImage() {
        File splashScreen = new File(uploadPath + "/theme/splash_screen.jpg");
        if (splashScreen.exists()) {
            if (splashScreen.delete()) {
                return Result.success("开屏页图片删除成功");
            } else {
                return Result.error("开屏页图片删除失败");
            }
        } else {
            return Result.success("开屏页图片不存在");
        }
    }
    
    @Override
    public Result<String> deleteHomeBackgroundImage() {
        File homeBackground = new File(uploadPath + "/theme/home_background.jpg");
        if (homeBackground.exists()) {
            if (homeBackground.delete()) {
                return Result.success("首页背景图删除成功");
            } else {
                return Result.error("首页背景图删除失败");
            }
        } else {
            return Result.success("首页背景图不存在");
        }
    }
}
