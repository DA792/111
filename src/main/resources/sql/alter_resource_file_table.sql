-- 修改resource_file表的id字段为自动递增
ALTER TABLE resource_file MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
