-- 修正 park_open_time_config 表中错误的 day_type 字段
-- 本脚本将根据日期正确设置 day_type 字段：
-- 0: 工作日 (周一到周五)
-- 1: 节假日 (周六、周日)

-- 注意：执行此脚本前请先备份数据

-- 更新工作日（周一到周五）的记录
UPDATE park_open_time_config 
SET day_type = 0 
WHERE DAYOFWEEK(config_date) BETWEEN 2 AND 6 
AND (day_type != 0 OR day_type IS NULL);

-- 更新周末（周六、周日）的记录
UPDATE park_open_time_config 
SET day_type = 1 
WHERE (DAYOFWEEK(config_date) = 1 OR DAYOFWEEK(config_date) = 7)
AND (day_type != 1 OR day_type IS NULL);

-- 查询修正结果
SELECT '工作日(周一到周五)' as type, COUNT(*) as count 
FROM park_open_time_config 
WHERE day_type = 0 
AND DAYOFWEEK(config_date) BETWEEN 2 AND 6
UNION ALL
SELECT '节假日(周六、周日)' as type, COUNT(*) as count 
FROM park_open_time_config 
WHERE day_type = 1
AND (DAYOFWEEK(config_date) = 1 OR DAYOFWEEK(config_date) = 7);
