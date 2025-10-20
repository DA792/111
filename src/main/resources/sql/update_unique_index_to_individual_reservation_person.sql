-- 更新individual_reservation_person表的复合唯一索引
-- 删除旧索引：idx_unique_person_visit (id_type, id_number, visit_date, time_slot)
-- 添加新索引：idx_unique_person_visit_deleted (id_type, id_number, visit_date, time_slot, deleted)
-- 作用：确保同一个人在同一天的同一个时间段只能预约一次（考虑deleted状态）

-- 删除旧的复合唯一索引
ALTER TABLE individual_reservation_person
DROP INDEX idx_unique_person_visit;

-- 添加新的复合唯一索引，包含deleted字段
ALTER TABLE individual_reservation_person
ADD CONSTRAINT idx_unique_person_visit_deleted
UNIQUE (id_type, id_number, visit_date, time_slot, deleted);