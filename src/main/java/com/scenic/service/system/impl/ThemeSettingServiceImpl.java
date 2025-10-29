package com.scenic.service.system.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scenic.common.dto.Result;
import com.scenic.dto.system.ThemeSettingDTO;
import com.scenic.entity.system.ThemeSetting;
import com.scenic.mapper.system.ThemeSettingMapper;
import com.scenic.service.system.ThemeSettingService;

/**
 * 主题设置服务实现类（使用文件存储替代数据库）
 */
@Service
public class ThemeSettingServiceImpl implements ThemeSettingService {
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    private static final String CONFIG_FILE_PATH = "src/main/resources/config/theme-config.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 获取所有主题设置（文件存储版本）
     * @return 主题设置列表
     */
    @Override
    public Result<List<ThemeSettingDTO>> getAllThemeSettings() {
        try {
            // 从配置文件读取主题设置
            ObjectNode config = readConfigFromFile();
            
            // 获取themes数组
            JsonNode themesNode = config.get("themes");
            if (themesNode != null && themesNode.isArray()) {
                List<ThemeSettingDTO> themeList = new ArrayList<>();
                for (JsonNode themeNode : themesNode) {
                    ThemeSettingDTO themeSettingDTO = new ThemeSettingDTO();
                    themeSettingDTO.setId(themeNode.get("themeName").asText().hashCode() & 0xFFFFL); // 基于主题名称生成ID
                    themeSettingDTO.setThemeName(themeNode.get("themeName").asText());
                    themeSettingDTO.setIsDefault(1); // 默认所有主题都是可用的
                    
                    // 将主题信息转换为JSON字符串存储在colorConfig字段中
                    String colorConfig = objectMapper.writeValueAsString(themeNode);
                    themeSettingDTO.setColorConfig(colorConfig);
                    
                    themeSettingDTO.setCreateTime(LocalDateTime.now());
                    themeSettingDTO.setUpdateTime(LocalDateTime.now());
                    themeList.add(themeSettingDTO);
                }
                return Result.success(themeList);
            } else {
                // 兼容旧格式
                ObjectNode themeSettings = (ObjectNode) config.get("themeSettings");
                ThemeSettingDTO themeSettingDTO = new ThemeSettingDTO();
                themeSettingDTO.setId(1L); // 固定ID
                themeSettingDTO.setThemeName("default");
                themeSettingDTO.setIsDefault(1);
                
                // 将主题设置转换为JSON字符串存储在colorConfig字段中
                String colorConfig = objectMapper.writeValueAsString(themeSettings);
                themeSettingDTO.setColorConfig(colorConfig);
                
                themeSettingDTO.setCreateTime(LocalDateTime.now());
                themeSettingDTO.setUpdateTime(LocalDateTime.parse(config.get("lastUpdated").asText()));
                
                // 包装成列表返回
                return Result.success(List.of(themeSettingDTO));
            }
        } catch (Exception e) {
            return Result.error("查询主题设置列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取主题设置详情（文件存储版本）
     * @param id 主题设置ID
     * @return 主题设置详情
     */
    @Override
    public Result<ThemeSettingDTO> getThemeSettingById(Long id) {
        try {
            return getDefaultThemeSetting();
        } catch (Exception e) {
            return Result.error("查询主题设置详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取默认主题设置（文件存储版本）
     * @return 默认主题设置
     */
    @Override
    public Result<ThemeSettingDTO> getDefaultThemeSetting() {
        try {
            // 从配置文件读取主题设置
            ObjectNode config = readConfigFromFile();

            // 创建主题设置DTO
            ThemeSettingDTO themeSettingDTO = new ThemeSettingDTO();
            themeSettingDTO.setId(1L); // 固定ID
            themeSettingDTO.setThemeName("default");
            themeSettingDTO.setIsDefault(1);

            // 直接设置themes和images
            JsonNode themesNode = config.get("themes");
            if (themesNode != null && themesNode.isArray()) {
                themeSettingDTO.setThemes(objectMapper.writeValueAsString(themesNode));
            } else {
                // 兼容旧格式，如果只有themeSettings，则将其转换为themes数组
                JsonNode themeSettingsNode = config.get("themeSettings");
                if (themeSettingsNode != null && themeSettingsNode.isObject()) {
                    List<ObjectNode> oldThemes = new ArrayList<>();
                    ObjectNode defaultTheme = objectMapper.createObjectNode();
                    defaultTheme.put("themeName", "default");
                    defaultTheme.put("colorName", "默认主题");
                    defaultTheme.put("colorCode", themeSettingsNode.get("primaryColor").asText());
                    defaultTheme.put("colorConfigEnabled", true);
                    oldThemes.add(defaultTheme);
                    themeSettingDTO.setThemes(objectMapper.writeValueAsString(oldThemes));
                }
            }

            JsonNode imageSettingsNode = config.get("imageSettings");
            if (imageSettingsNode != null && imageSettingsNode.isObject()) {
                themeSettingDTO.setImages(objectMapper.writeValueAsString(imageSettingsNode));
            }

            themeSettingDTO.setCreateTime(LocalDateTime.now());
            themeSettingDTO.setUpdateTime(LocalDateTime.now());

            return Result.success(themeSettingDTO);
        } catch (Exception e) {
            return Result.error("查询默认主题设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建主题设置（文件存储版本）
     * @param themeSettingDTO 主题设置信息
     * @return 创建结果
     */
    @Override
    public Result<String> createThemeSetting(ThemeSettingDTO themeSettingDTO) {
        try {
            // 直接调用更新方法，因为只有一个主题配置
            return updateThemeSetting(1L, themeSettingDTO);
        } catch (Exception e) {
            return Result.error("创建主题设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新主题设置（文件存储版本）
     * @param id 主题设置ID
     * @param themeSettingDTO 主题设置信息
     * @return 更新结果
     */
    @Override
    public Result<String> updateThemeSetting(Long id, ThemeSettingDTO themeSettingDTO) {
        try {
            // 读取现有配置
            ObjectNode config = readConfigFromFile();
            
            // 处理themes配置
            if (themeSettingDTO.getThemes() != null && !themeSettingDTO.getThemes().isEmpty()) {
                // 解析themes JSON字符串并更新配置
                JsonNode themesNode = objectMapper.readTree(themeSettingDTO.getThemes());
                config.set("themes", themesNode);
            } else if (themeSettingDTO.getColorConfig() != null && !themeSettingDTO.getColorConfig().isEmpty()) {
                // 兼容旧格式，解析colorConfig中的主题设置
                JsonNode colorConfigNode = objectMapper.readTree(themeSettingDTO.getColorConfig());
                
                // 检查是否是新的themes数组格式
                if (colorConfigNode.has("themes") && colorConfigNode.get("themes").isArray()) {
                    // 使用新的themes数组格式
                    config.set("themes", colorConfigNode.get("themes"));
                } else {
                    // 兼容旧格式，解析单个主题设置
                    ObjectNode themeSettings = objectMapper.createObjectNode();
                    if (colorConfigNode.isObject()) {
                        ObjectNode colorConfigObject = (ObjectNode) colorConfigNode;
                        themeSettings.setAll(colorConfigObject);
                    }
                    config.set("themeSettings", themeSettings);
                }
            }
            
            // 处理images配置
            if (themeSettingDTO.getImages() != null && !themeSettingDTO.getImages().isEmpty()) {
                // 解析images JSON字符串并更新配置
                JsonNode imagesNode = objectMapper.readTree(themeSettingDTO.getImages());
                config.set("imageSettings", imagesNode);
            }
            
            // 更新时间戳
            config.put("lastUpdated", LocalDateTime.now().toString());
            
            // 写入配置文件
            writeConfigToFile(config);
            
            return Result.success("主题设置更新成功");
        } catch (Exception e) {
            return Result.error("更新主题设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除主题设置（文件存储版本）
     * @param id 主题设置ID
     * @return 删除结果
     */
    @Override
    public Result<String> deleteThemeSetting(Long id) {
        try {
            // 重置为主题默认设置
            ObjectNode config = objectMapper.createObjectNode();
            
            ObjectNode themeSettings = objectMapper.createObjectNode();
            themeSettings.put("primaryColor", "#409eff");
            themeSettings.put("secondaryColor", "#67c23a");
            themeSettings.put("backgroundColor", "#f5f5f5");
            themeSettings.put("textColor", "#333333");
            themeSettings.put("headerColor", "#ffffff");
            themeSettings.put("sidebarColor", "#2d3a4b");
            
            ObjectNode imageSettings = objectMapper.createObjectNode();
            imageSettings.put("logoPath", "");
            imageSettings.put("faviconPath", "");
            imageSettings.put("backgroundImagePath", "");
            
            config.set("themeSettings", themeSettings);
            config.set("imageSettings", imageSettings);
            config.put("lastUpdated", LocalDateTime.now().toString());
            
            writeConfigToFile(config);
            
            return Result.success("主题设置已重置为默认值");
        } catch (Exception e) {
            return Result.error("重置主题设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置默认主题（文件存储版本）
     * @param id 主题设置ID
     * @return 设置结果
     */
    @Override
    public Result<String> setDefaultTheme(Long id) {
        try {
            // 在文件存储模式下，所有设置都是默认的
            return Result.success("默认主题设置成功");
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
        // 在文件存储模式下，返回固定的主题设置
        ThemeSetting themeSetting = new ThemeSetting();
        themeSetting.setId(1L);
        themeSetting.setThemeName("default");
        
        try {
            ObjectNode config = readConfigFromFile();
            ObjectNode themeSettings = (ObjectNode) config.get("themeSettings");
            String colorConfig = objectMapper.writeValueAsString(themeSettings);
            themeSetting.setColorConfig(colorConfig);
        } catch (Exception e) {
            // 如果读取失败，使用默认配置
            ObjectNode defaultThemeSettings = objectMapper.createObjectNode();
            defaultThemeSettings.put("primaryColor", "#409eff");
            defaultThemeSettings.put("secondaryColor", "#67c23a");
            defaultThemeSettings.put("backgroundColor", "#f5f5f5");
            defaultThemeSettings.put("textColor", "#333333");
            defaultThemeSettings.put("headerColor", "#ffffff");
            defaultThemeSettings.put("sidebarColor", "#2d3a4b");
            
            try {
                String colorConfig = objectMapper.writeValueAsString(defaultThemeSettings);
                themeSetting.setColorConfig(colorConfig);
            } catch (Exception ex) {
                // ignore
            }
        }
        
        themeSetting.setIsDefault(1);
        themeSetting.setCreateTime(LocalDateTime.now());
        themeSetting.setUpdateTime(LocalDateTime.now());
        return themeSetting;
    }
    
    /**
     * 从配置文件读取配置
     * @return 配置对象
     * @throws IOException 读取文件异常
     */
    private ObjectNode readConfigFromFile() throws IOException {
        File configFile = new File(CONFIG_FILE_PATH);
        if (!configFile.exists()) {
            // 如果配置文件不存在，创建默认配置
            ObjectNode defaultConfig = objectMapper.createObjectNode();
            
            ObjectNode themeSettings = objectMapper.createObjectNode();
            themeSettings.put("primaryColor", "#409eff");
            themeSettings.put("secondaryColor", "#67c23a");
            themeSettings.put("backgroundColor", "#f5f5f5");
            themeSettings.put("textColor", "#333333");
            themeSettings.put("headerColor", "#ffffff");
            themeSettings.put("sidebarColor", "#2d3a4b");
            
            ObjectNode imageSettings = objectMapper.createObjectNode();
            imageSettings.put("logoPath", "");
            imageSettings.put("faviconPath", "");
            imageSettings.put("backgroundImagePath", "");
            
            defaultConfig.set("themeSettings", themeSettings);
            defaultConfig.set("imageSettings", imageSettings);
            defaultConfig.put("lastUpdated", LocalDateTime.now().toString());
            
            writeConfigToFile(defaultConfig);
            return defaultConfig;
        }
        
        String content = new String(Files.readAllBytes(Paths.get(CONFIG_FILE_PATH)));
        return (ObjectNode) objectMapper.readTree(content);
    }
    
    /**
     * 将配置写入文件
     * @param config 配置对象
     * @throws IOException 写入文件异常
     */
    private void writeConfigToFile(ObjectNode config) throws IOException {
        String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(config);
        Files.write(Paths.get(CONFIG_FILE_PATH), jsonContent.getBytes());
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
    
    // 图片处理方法保持不变
    
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
            
            // 更新配置文件中的图片路径
            updateImageSetting("splashImagePath", "/theme/splash_screen.jpg");
            
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
            
            // 更新配置文件中的图片路径
            updateImageSetting("backgroundImagePath", "/theme/home_background.jpg");
            
            return Result.success("首页背景图上传成功");
        } catch (IOException e) {
            return Result.error("首页背景图上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public void saveThemeSettings(String colorConfig) {
        try {
            // 解析传入的主题配置JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode themeConfig = objectMapper.readTree(colorConfig);
            
            // 创建新的配置对象，只保留themes数组
            ObjectNode newConfig = objectMapper.createObjectNode();
            
            // 如果传入的配置包含themes数组，则直接使用
            if (themeConfig.has("themes")) {
                newConfig.set("themes", themeConfig.get("themes"));
            } else {
                // 如果没有themes数组，保持原有逻辑或创建默认配置
                File configFile = new File(CONFIG_FILE_PATH);
                if (configFile.exists()) {
                    JsonNode existingConfig = objectMapper.readTree(configFile);
                    if (existingConfig.has("themes")) {
                        newConfig.set("themes", existingConfig.get("themes"));
                    }
                }
            }
            
            // 写回配置文件
            writeConfigToFile(newConfig);
                       
        } catch (IOException e) {
            throw new RuntimeException("保存主题配置失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Result<String> deleteSplashScreenImage() {
        File splashScreen = new File(uploadPath + "/theme/splash_screen.jpg");
        if (splashScreen.exists()) {
            if (splashScreen.delete()) {
                // 更新配置文件中的图片路径
                updateImageSetting("splashImagePath", "");
                return Result.success("开屏页图片删除成功");
            } else {
                return Result.error("开屏页图片删除失败");
            }
        } else {
            return Result.success("开屏页图片不存在");
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
    public Result<String> deleteHomeBackgroundImage() {
        File homeBackground = new File(uploadPath + "/theme/home_background.jpg");
        if (homeBackground.exists()) {
            if (homeBackground.delete()) {
                // 更新配置文件中的图片路径
                updateImageSetting("backgroundImagePath", "");
                return Result.success("首页背景图删除成功");
            } else {
                return Result.error("首页背景图删除失败");
            }
        } else {
            return Result.success("首页背景图不存在");
        }
    }
    
    /**
     * 更新配置文件中的图片设置
     * @param imageType 图片类型
     * @param imagePath 图片路径
     */
    private void updateImageSetting(String imageType, String imagePath) {
        try {
            ObjectNode config = readConfigFromFile();
            ObjectNode imageSettings = (ObjectNode) config.get("imageSettings");
            if (imageSettings == null) {
                imageSettings = objectMapper.createObjectNode();
            }
            imageSettings.put(imageType, imagePath);
            config.set("imageSettings", imageSettings);
            config.put("lastUpdated", LocalDateTime.now().toString());
            writeConfigToFile(config);
        } catch (Exception e) {
            // 忽略更新配置文件的错误
        }
    }
}
