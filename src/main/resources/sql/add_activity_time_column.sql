-- 添加activity_time字段到activity_registration表
ALTER TABLE activity_registration
ADD COLUMN activity_time VARCHAR(50) COMMENT '活动时间' AFTER registration_time;
