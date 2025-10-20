-- 为individual_reservation_person表添加复合唯一索引
-- 索引字段：id_type, id_number, visit_date, time_slot
-- 作用：确保同一个人在同一天的同一个时间段只能预约一次

ALTER TABLE individual_reservation_person
ADD CONSTRAINT idx_unique_person_visit
UNIQUE (id_type, id_number, visit_date, time_slot);