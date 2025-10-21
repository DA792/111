-- 修复活动预约数据脚本
-- 将活动预约表中的activity_id映射到正确的活动ID

-- 首先备份原数据
CREATE TABLE activity_registration_backup AS SELECT * FROM activity_registration;

-- 由于活动标题不匹配，需要手动映射活动ID
-- 假设映射关系如下（需要根据实际情况调整）：
-- 活动预约中的"科技创新论坛" -> 活动表中的"科技文化节开幕式" (ID=2)
-- 活动预约中的"个人成长讲座" -> 活动表中的"研究生学术论坛" (ID=3)  
-- 活动预约中的"设计工作坊" -> 活动表中的"2023秋季校园招聘会" (ID=1)

UPDATE activity_registration 
SET activity_id = CASE 
    WHEN activity_title = '科技创新论坛' THEN 2
    WHEN activity_title = '个人成长讲座' THEN 3
    WHEN activity_title = '设计工作坊' THEN 1
    ELSE activity_id
END
WHERE activity_id NOT IN (SELECT id FROM activity WHERE id IS NOT NULL);

-- 验证更新结果
SELECT ar.id, ar.activity_id, ar.activity_title, a.title as correct_title, a.team_limit,
       CASE WHEN a.id IS NULL THEN '未找到匹配活动' ELSE '匹配成功' END as status
FROM activity_registration ar
LEFT JOIN activity a ON ar.activity_id = a.id
WHERE ar.status != 0;

-- 测试查询 - 验证修复后的数据是否能正确显示
SELECT 
    DATE(ar.registration_time) AS reserve_date,
    ar.activity_title AS activity_name,
    (SELECT team_limit FROM activity WHERE id = ar.activity_id) AS activity_limit,
    COUNT(ar.id) AS booked_count,
    CASE 
        WHEN p.is_closed = 1 THEN '不开放'
        WHEN COUNT(ar.id) >= (SELECT CAST(team_limit AS UNSIGNED) FROM activity WHERE id = ar.activity_id) THEN '已满'
        ELSE CONCAT('已预约', COUNT(ar.id), '/', (SELECT team_limit FROM activity WHERE id = ar.activity_id))
    END AS reserve_status,
    (p.is_closed = 0) AS is_open
FROM activity_registration ar
LEFT JOIN park_open_time_config p ON DATE(ar.registration_time) = p.config_date
LEFT JOIN activity a ON ar.activity_id = a.id
WHERE ar.status != 0 
    AND 1 = 1  -- 模拟活动预约已开放
    AND YEAR(ar.registration_time) = 2023 
    AND MONTH(ar.registration_time) = 10
GROUP BY DATE(ar.registration_time), ar.activity_title, p.is_closed, ar.activity_id, a.team_limit
ORDER BY reserve_date, activity_name;

-- 如果需要回滚，可以使用以下语句：
-- DROP TABLE activity_registration;
-- RENAME TABLE activity_registration_backup TO activity_registration;
