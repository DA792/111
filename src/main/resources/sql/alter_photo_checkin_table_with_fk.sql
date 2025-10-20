-- 1. 先删除外键约束
ALTER TABLE photo_checkin DROP FOREIGN KEY fk_photo_checkin_category;

-- 2. 修改表结构，移除自增属性
ALTER TABLE checkin_category MODIFY COLUMN id BIGINT NOT NULL;
ALTER TABLE photo_checkin MODIFY COLUMN id BIGINT NOT NULL;
ALTER TABLE resource_file MODIFY COLUMN id BIGINT NOT NULL;

-- 3. 重新添加外键约束
ALTER TABLE photo_checkin ADD CONSTRAINT fk_photo_checkin_category 
FOREIGN KEY (category_id) REFERENCES checkin_category(id);