package com.scenic.service.intelligence.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.dto.intelligence.KnowledgeBaseDTO;
import com.scenic.entity.intelligence.KnowledgeBase;
import com.scenic.mapper.intelligence.KnowledgeBaseMapper;
import com.scenic.service.intelligence.KnowledgeBaseService;
import com.scenic.utils.FileUploadUtil;

/**
 * 知识库服务实现类
 */
@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {
    
    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;
    
    /**
     * 分页查询知识库列表
     * @param page 页码
     * @param size 每页大小
     * @param title 知识库标题（可选）
     * @param version 版本号（可选）
     * @param status 状态（可选）
     * @return 知识库列表
     */
    @Override
    public Result<PageResult<KnowledgeBaseDTO>> getKnowledgeBases(int page, int size, String title, String version, Integer status) {
        try {
            // 计算偏移量
            int offset = (page - 1) * size;
            
            // 查询总记录数
            List<KnowledgeBase> allKnowledgeBases = knowledgeBaseMapper.selectByCondition(title, version, status);
            int total = allKnowledgeBases.size();
            
            // 分页查询
            List<KnowledgeBase> knowledgeBases = allKnowledgeBases.stream()
                    .skip(offset)
                    .limit(size)
                    .collect(Collectors.toList());
            
            // 转换为DTO列表
            List<KnowledgeBaseDTO> knowledgeBaseDTOs = knowledgeBases.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // 封装分页结果
            PageResult<KnowledgeBaseDTO> pageResult = new PageResult<>();
            pageResult.setTotal(total);
            pageResult.setRecords(knowledgeBaseDTOs);
            pageResult.setCurrentPage(page);
            pageResult.setPageSize(size);
            
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询知识库列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取知识库详情
     * @param id 知识库ID
     * @return 知识库详情
     */
    @Override
    public Result<KnowledgeBaseDTO> getKnowledgeBaseById(Long id) {
        try {
            KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(id);
            if (knowledgeBase == null) {
                return Result.error("知识库不存在");
            }
            return Result.success(convertToDTO(knowledgeBase));
        } catch (Exception e) {
            return Result.error("查询知识库详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建知识库
     * @param knowledgeBaseDTO 知识库信息
     * @return 创建结果
     */
    @Override
    public Result<String> createKnowledgeBase(KnowledgeBaseDTO knowledgeBaseDTO) {
        try {
            // 转换为实体类
            KnowledgeBase knowledgeBase = convertToEntity(knowledgeBaseDTO);
            knowledgeBase.setCreateTime(LocalDateTime.now());
            knowledgeBase.setUpdateTime(LocalDateTime.now());
            
            // 插入数据库
            int result = knowledgeBaseMapper.insert(knowledgeBase);
            if (result > 0) {
                return Result.success("知识库创建成功");
            } else {
                return Result.error("知识库创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建知识库失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新知识库
     * @param id 知识库ID
     * @param knowledgeBaseDTO 知识库信息
     * @return 更新结果
     */
    @Override
    public Result<String> updateKnowledgeBase(Long id, KnowledgeBaseDTO knowledgeBaseDTO) {
        try {
            // 检查知识库是否存在
            KnowledgeBase existingKnowledgeBase = knowledgeBaseMapper.selectById(id);
            if (existingKnowledgeBase == null) {
                return Result.error("知识库不存在");
            }
            
            // 转换为实体类
            KnowledgeBase knowledgeBase = convertToEntity(knowledgeBaseDTO);
            knowledgeBase.setId(id);
            knowledgeBase.setUpdateTime(LocalDateTime.now());
            
            // 更新数据库
            int result = knowledgeBaseMapper.update(knowledgeBase);
            if (result > 0) {
                return Result.success("知识库更新成功");
            } else {
                return Result.error("知识库更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新知识库失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除知识库
     * @param id 知识库ID
     * @return 删除结果
     */
    @Override
    public Result<String> deleteKnowledgeBase(Long id) {
        try {
            // 检查知识库是否存在
            KnowledgeBase existingKnowledgeBase = knowledgeBaseMapper.selectById(id);
            if (existingKnowledgeBase == null) {
                return Result.error("知识库不存在");
            }
            
            // 删除数据库记录
            int result = knowledgeBaseMapper.deleteById(id);
            if (result > 0) {
                return Result.success("知识库删除成功");
            } else {
                return Result.error("知识库删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除知识库失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传知识库文件
     * @param file 知识库文件
     * @param title 知识库标题
     * @return 上传结果
     */
    @Override
    public Result<String> uploadKnowledgeBaseFile(MultipartFile file, String title) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            // 上传文件
            String filename = fileUploadUtil.uploadFile(file);
            
            // 创建知识库记录
            KnowledgeBase knowledgeBase = new KnowledgeBase();
            knowledgeBase.setTitle(title);
            knowledgeBase.setFilePath(filename);
            knowledgeBase.setFileName(file.getOriginalFilename());
            knowledgeBase.setFileSize(file.getSize());
            knowledgeBase.setFileType(file.getContentType());
            knowledgeBase.setVersion("v1.0"); // 默认版本号
            knowledgeBase.setStatus(1); // 默认启用
            knowledgeBase.setCreateTime(LocalDateTime.now());
            knowledgeBase.setUpdateTime(LocalDateTime.now());
            
            // 插入数据库
            int result = knowledgeBaseMapper.insert(knowledgeBase);
            if (result > 0) {
                return Result.success("知识库文件上传成功");
            } else {
                return Result.error("知识库文件上传失败");
            }
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("上传知识库文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有启用的知识库（分页）
     * @param page 页码
     * @param size 每页大小
     * @return 知识库列表
     */
    @Override
    public Result<PageResult<KnowledgeBaseDTO>> getEnabledKnowledgeBases(int page, int size) {
        try {
            return getKnowledgeBases(page, size, null, null, 1);
        } catch (Exception e) {
            return Result.error("查询启用的知识库列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取启用的知识库详情
     * @param id 知识库ID
     * @return 知识库详情
     */
    @Override
    public Result<KnowledgeBaseDTO> getEnabledKnowledgeBaseById(Long id) {
        try {
            KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(id);
            if (knowledgeBase == null) {
                return Result.error("知识库不存在");
            }
            if (knowledgeBase.getStatus() != 1) {
                return Result.error("知识库未启用");
            }
            return Result.success(convertToDTO(knowledgeBase));
        } catch (Exception e) {
            return Result.error("查询知识库详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据版本号获取知识库
     * @param version 版本号
     * @return 知识库
     */
    @Override
    public Result<KnowledgeBaseDTO> getKnowledgeBaseByVersion(String version) {
        try {
            KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectByVersion(version);
            if (knowledgeBase == null) {
                return Result.error("知识库不存在");
            }
            return Result.success(convertToDTO(knowledgeBase));
        } catch (Exception e) {
            return Result.error("查询知识库失败: " + e.getMessage());
        }
    }
    
    /**
     * 将实体类转换为DTO
     * @param knowledgeBase 知识库实体
     * @return 知识库DTO
     */
    private KnowledgeBaseDTO convertToDTO(KnowledgeBase knowledgeBase) {
        KnowledgeBaseDTO knowledgeBaseDTO = new KnowledgeBaseDTO();
        BeanUtils.copyProperties(knowledgeBase, knowledgeBaseDTO);
        return knowledgeBaseDTO;
    }
    
    /**
     * 将DTO转换为实体类
     * @param knowledgeBaseDTO 知识库DTO
     * @return 知识库实体
     */
    private KnowledgeBase convertToEntity(KnowledgeBaseDTO knowledgeBaseDTO) {
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        BeanUtils.copyProperties(knowledgeBaseDTO, knowledgeBase);
        return knowledgeBase;
    }
}
