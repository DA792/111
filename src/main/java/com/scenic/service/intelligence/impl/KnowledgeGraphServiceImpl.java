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
import com.scenic.dto.intelligence.KnowledgeGraphDTO;
import com.scenic.entity.intelligence.KnowledgeGraph;
import com.scenic.mapper.intelligence.KnowledgeGraphMapper;
import com.scenic.service.intelligence.KnowledgeGraphService;
import com.scenic.utils.FileUploadUtil;

/**
 * 知识图谱服务实现类
 */
@Service
public class KnowledgeGraphServiceImpl implements KnowledgeGraphService {
    
    @Autowired
    private KnowledgeGraphMapper knowledgeGraphMapper;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;
    
    /**
     * 分页查询知识图谱列表
     * @param page 页码
     * @param size 每页大小
     * @param speciesName 物种名称（可选）
     * @param category 分类（可选）
     * @param status 状态（可选）
     * @return 知识图谱列表
     */
    @Override
    public Result<PageResult<KnowledgeGraphDTO>> getKnowledgeGraphs(int page, int size, String speciesName, String category, Integer status) {
        try {
            // 计算偏移量
            int offset = (page - 1) * size;
            
            // 查询总记录数
            List<KnowledgeGraph> allKnowledgeGraphs = knowledgeGraphMapper.selectByCondition(speciesName, category, status);
            int total = allKnowledgeGraphs.size();
            
            // 分页查询
            List<KnowledgeGraph> knowledgeGraphs = allKnowledgeGraphs.stream()
                    .skip(offset)
                    .limit(size)
                    .collect(Collectors.toList());
            
            // 转换为DTO列表
            List<KnowledgeGraphDTO> knowledgeGraphDTOs = knowledgeGraphs.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // 封装分页结果
            PageResult<KnowledgeGraphDTO> pageResult = new PageResult<>();
            pageResult.setTotal(total);
            pageResult.setRecords(knowledgeGraphDTOs);
            pageResult.setCurrentPage(page);
            pageResult.setPageSize(size);
            
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询知识图谱列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取知识图谱详情
     * @param id 知识图谱ID
     * @return 知识图谱详情
     */
    @Override
    public Result<KnowledgeGraphDTO> getKnowledgeGraphById(Long id) {
        try {
            KnowledgeGraph knowledgeGraph = knowledgeGraphMapper.selectById(id);
            if (knowledgeGraph == null) {
                return Result.error("知识图谱不存在");
            }
            return Result.success(convertToDTO(knowledgeGraph));
        } catch (Exception e) {
            return Result.error("查询知识图谱详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建知识图谱
     * @param knowledgeGraphDTO 知识图谱信息
     * @return 创建结果
     */
    @Override
    public Result<String> createKnowledgeGraph(KnowledgeGraphDTO knowledgeGraphDTO) {
        try {
            // 转换为实体类
            KnowledgeGraph knowledgeGraph = convertToEntity(knowledgeGraphDTO);
            knowledgeGraph.setCreateTime(LocalDateTime.now());
            knowledgeGraph.setUpdateTime(LocalDateTime.now());
            
            // 插入数据库
            int result = knowledgeGraphMapper.insert(knowledgeGraph);
            if (result > 0) {
                return Result.success("知识图谱创建成功");
            } else {
                return Result.error("知识图谱创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建知识图谱失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新知识图谱
     * @param id 知识图谱ID
     * @param knowledgeGraphDTO 知识图谱信息
     * @return 更新结果
     */
    @Override
    public Result<String> updateKnowledgeGraph(Long id, KnowledgeGraphDTO knowledgeGraphDTO) {
        try {
            // 检查知识图谱是否存在
            KnowledgeGraph existingKnowledgeGraph = knowledgeGraphMapper.selectById(id);
            if (existingKnowledgeGraph == null) {
                return Result.error("知识图谱不存在");
            }
            
            // 转换为实体类
            KnowledgeGraph knowledgeGraph = convertToEntity(knowledgeGraphDTO);
            knowledgeGraph.setId(id);
            knowledgeGraph.setUpdateTime(LocalDateTime.now());
            
            // 更新数据库
            int result = knowledgeGraphMapper.update(knowledgeGraph);
            if (result > 0) {
                return Result.success("知识图谱更新成功");
            } else {
                return Result.error("知识图谱更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新知识图谱失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除知识图谱
     * @param id 知识图谱ID
     * @return 删除结果
     */
    @Override
    public Result<String> deleteKnowledgeGraph(Long id) {
        try {
            // 检查知识图谱是否存在
            KnowledgeGraph existingKnowledgeGraph = knowledgeGraphMapper.selectById(id);
            if (existingKnowledgeGraph == null) {
                return Result.error("知识图谱不存在");
            }
            
            // 删除数据库记录
            int result = knowledgeGraphMapper.deleteById(id);
            if (result > 0) {
                return Result.success("知识图谱删除成功");
            } else {
                return Result.error("知识图谱删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除知识图谱失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传知识图谱图片
     * @param file 图片文件
     * @param speciesName 物种名称
     * @return 上传结果
     */
    @Override
    public Result<String> uploadKnowledgeGraphImage(MultipartFile file, String speciesName) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            // 上传文件
            String filename = fileUploadUtil.uploadFile(file);
            
            // 更新知识图谱记录
            KnowledgeGraph knowledgeGraph = knowledgeGraphMapper.selectBySpeciesName(speciesName);
            if (knowledgeGraph == null) {
                // 如果知识图谱不存在，则创建新的
                knowledgeGraph = new KnowledgeGraph();
                knowledgeGraph.setSpeciesName(speciesName);
                knowledgeGraph.setImageUrl(filename);
                knowledgeGraph.setStatus(1); // 默认启用
                knowledgeGraph.setCreateTime(LocalDateTime.now());
                knowledgeGraph.setUpdateTime(LocalDateTime.now());
                knowledgeGraphMapper.insert(knowledgeGraph);
            } else {
                // 如果知识图谱存在，则更新图片
                knowledgeGraph.setImageUrl(filename);
                knowledgeGraph.setUpdateTime(LocalDateTime.now());
                knowledgeGraphMapper.update(knowledgeGraph);
            }
            
            return Result.success("知识图谱图片上传成功");
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("上传知识图谱图片失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有启用的知识图谱（分页）
     * @param page 页码
     * @param size 每页大小
     * @return 知识图谱列表
     */
    @Override
    public Result<PageResult<KnowledgeGraphDTO>> getEnabledKnowledgeGraphs(int page, int size) {
        try {
            return getKnowledgeGraphs(page, size, null, null, 1);
        } catch (Exception e) {
            return Result.error("查询启用的知识图谱列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据物种名称获取知识图谱
     * @param speciesName 物种名称
     * @return 知识图谱
     */
    @Override
    public Result<KnowledgeGraphDTO> getKnowledgeGraphBySpeciesName(String speciesName) {
        try {
            KnowledgeGraph knowledgeGraph = knowledgeGraphMapper.selectBySpeciesName(speciesName);
            if (knowledgeGraph == null) {
                return Result.error("知识图谱不存在");
            }
            return Result.success(convertToDTO(knowledgeGraph));
        } catch (Exception e) {
            return Result.error("查询知识图谱失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据分类获取知识图谱列表
     * @param category 分类
     * @return 知识图谱列表
     */
    @Override
    public Result<List<KnowledgeGraphDTO>> getKnowledgeGraphsByCategory(String category) {
        try {
            // 查询指定分类的知识图谱
            List<KnowledgeGraph> knowledgeGraphs = knowledgeGraphMapper.selectByCondition(null, category, null);
            
            // 转换为DTO列表
            List<KnowledgeGraphDTO> knowledgeGraphDTOs = knowledgeGraphs.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            return Result.success(knowledgeGraphDTOs);
        } catch (Exception e) {
            return Result.error("查询知识图谱列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 将实体类转换为DTO
     * @param knowledgeGraph 知识图谱实体
     * @return 知识图谱DTO
     */
    private KnowledgeGraphDTO convertToDTO(KnowledgeGraph knowledgeGraph) {
        KnowledgeGraphDTO knowledgeGraphDTO = new KnowledgeGraphDTO();
        BeanUtils.copyProperties(knowledgeGraph, knowledgeGraphDTO);
        return knowledgeGraphDTO;
    }
    
    /**
     * 将DTO转换为实体类
     * @param knowledgeGraphDTO 知识图谱DTO
     * @return 知识图谱实体
     */
    private KnowledgeGraph convertToEntity(KnowledgeGraphDTO knowledgeGraphDTO) {
        KnowledgeGraph knowledgeGraph = new KnowledgeGraph();
        BeanUtils.copyProperties(knowledgeGraphDTO, knowledgeGraph);
        return knowledgeGraph;
    }
}
