-- 从resource_file表中删除sha256列
ALTER TABLE resource_file DROP COLUMN sha256;

-- 删除相关索引（如果存在）
DROP INDEX IF EXISTS idx_resource_file_sha256 ON resource_file;
