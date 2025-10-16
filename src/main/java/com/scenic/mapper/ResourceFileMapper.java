package com.scenic.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.scenic.entity.ResourceFile;

@Mapper
public interface ResourceFileMapper {
    
    /**
     * 根据ID查询文件资源
     * @param id 文件资源ID
     * @return 文件资源信息
     */
    @Select("SELECT * FROM resource_file WHERE id = #{id}")
    ResourceFile selectById(Long id);
    
    /**
     * 根据文件路径和存储桶名称查询文件资源
     * @param bucketName 存储桶名称
     * @param fileKey 文件路径
     * @return 文件资源信息
     */
    @Select("SELECT * FROM resource_file WHERE bucket_name = #{bucketName} AND file_key = #{fileKey}")
    ResourceFile selectByBucketAndKey(@Param("bucketName") String bucketName, @Param("fileKey") String fileKey);
    
    /**
     * 根据用户ID查询用户头像
     * @param userId 用户ID
     * @return 文件资源信息
     */
    @Select("SELECT r.* FROM resource_file r " +
            "JOIN user u ON u.avatar_file_id = r.id " +
            "WHERE u.id = #{userId}")
    ResourceFile selectUserAvatar(Long userId);
    
    /**
     * 查询默认头像
     * @return 文件资源信息
     */
    @Select("SELECT * FROM resource_file WHERE file_key = 'user-image1.jpeg' AND bucket_name = 'user-avatars' LIMIT 1")
    ResourceFile selectDefaultAvatar();
    
    /**
     * 插入文件资源记录
     * @param resourceFile 文件资源信息
     * @return 插入结果
     */
    @Insert("INSERT INTO resource_file(file_name, file_key, bucket_name, file_size, mime_type, file_type, width, height, " +
            "duration, sha256, upload_user_id, is_temp, create_time, update_time, create_by, update_by) " +
            "VALUES(#{fileName}, #{fileKey}, #{bucketName}, #{fileSize}, #{mimeType}, #{fileType}, #{width}, #{height}, " +
            "#{duration}, #{sha256}, #{uploadUserId}, #{isTemp}, #{createTime}, #{updateTime}, #{createBy}, #{updateBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ResourceFile resourceFile);
    
    /**
     * 根据ID删除文件资源记录
     * @param id 文件资源ID
     * @return 删除结果
     */
    @Update("DELETE FROM resource_file WHERE id = #{id}")
    int deleteById(Long id);
}
