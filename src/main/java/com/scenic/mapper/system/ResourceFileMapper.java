package com.scenic.mapper.system;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.system.ResourceFile;

@Mapper
public interface ResourceFileMapper {
    
    /**
     * 根据ID查询资源文件
     * @param id 资源文件ID
     * @return 资源文件信息
     */
    @Select("SELECT * FROM resource_file WHERE id = #{id}")
    ResourceFile selectById(Long id);
    
    /**
     * 根据ID列表查询资源文件列表
     * @param ids 资源文件ID列表
     * @return 资源文件列表
     */
    List<ResourceFile> selectByIds(@Param("ids") List<Long> ids);
    
    /**
     * 插入资源文件
     * @param resourceFile 资源文件信息
     * @return 插入结果
     */
    @Insert("INSERT INTO resource_file(file_name, file_key, bucket_name, file_size, mime_type, file_type, width, height, duration, sha256, upload_user_id, is_temp, create_time, update_time, create_by, update_by) " +
            "VALUES(#{fileName}, #{fileKey}, #{bucketName}, #{fileSize}, #{mimeType}, #{fileType}, #{width}, #{height}, #{duration}, #{sha256}, #{uploadUserId}, #{isTemp}, #{createTime}, #{updateTime}, #{createBy}, #{updateBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ResourceFile resourceFile);
    
    /**
     * 更新资源文件信息
     * @param resourceFile 资源文件信息
     * @return 更新结果
     */
    @Update("UPDATE resource_file SET file_name = #{fileName}, file_key = #{fileKey}, bucket_name = #{bucketName}, file_size = #{fileSize}, mime_type = #{mimeType}, file_type = #{fileType}, width = #{width}, height = #{height}, duration = #{duration}, sha256 = #{sha256}, upload_user_id = #{uploadUserId}, is_temp = #{isTemp}, update_time = #{updateTime}, update_by = #{updateBy} WHERE id = #{id}")
    int updateById(ResourceFile resourceFile);
    
    /**
     * 根据ID删除资源文件
     * @param id 资源文件ID
     * @return 删除结果
     */
    @Delete("DELETE FROM resource_file WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 查询所有资源文件
     * @return 资源文件列表
     */
    @Select("SELECT * FROM resource_file ORDER BY create_time DESC")
    List<ResourceFile> selectAll();
}
