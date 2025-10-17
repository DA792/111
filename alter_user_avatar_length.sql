-- 修改photo_checkin表中user_avatar字段的长度为1024
ALTER TABLE photo_checkin MODIFY COLUMN user_avatar VARCHAR(1024);