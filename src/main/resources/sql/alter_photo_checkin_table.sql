-- 修改photo_checkin表，将自增ID改为普通ID
ALTER TABLE photo_checkin MODIFY COLUMN id BIGINT NOT NULL;

-- 修改resource_file表，将自增ID改为普通ID
ALTER TABLE resource_file MODIFY COLUMN id BIGINT NOT NULL;

-- 修改checkin_category表，将自增ID改为普通ID
ALTER TABLE checkin_category MODIFY COLUMN id BIGINT NOT NULL;