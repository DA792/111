-- 添加create_by字段到activity_registration表
ALTER TABLE activity_registration ADD COLUMN create_by BIGINT;

-- 更新注释
COMMENT ON COLUMN activity_registration.create_by IS '创建者ID';
