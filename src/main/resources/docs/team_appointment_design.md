# 团队预约模块数据库设计文档

## 1. 概述

团队预约模块用于管理保护区内的团队预约功能，包括团队预约表格下载、团队预约申请、团队预约审核等功能。本文档详细描述团队预约模块的数据库设计和界面对应关系。

## 2. 数据库表结构

### 2.1 团队预约表格模板表（team_appointment_template）

存储团队预约表格的模板文件。

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|-------|------|------|-------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 模板ID |
| template_name | VARCHAR(255) | 是 | | 模板名称 |
| template_file_id | BIGINT | 是 | | 模板文件ID（关联resource_file表） |
| description | VARCHAR(500) | 否 | NULL | 模板描述 |
| is_default | TINYINT(1) | 否 | 0 | 是否默认模板：0-否，1-是 |
| enabled | TINYINT(1) | 否 | 1 | 是否启用：0-禁用，1-启用 |
| create_time | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- INDEX `idx_is_default` (`is_default`)
- INDEX `idx_enabled` (`enabled`)

### 2.2 团队预约表（team_appointment）

存储用户的团队预约信息，包含从表格中提取的关键信息。

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|-------|------|------|-------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 预约ID |
| appointment_no | VARCHAR(50) | 是 | | 预约编号 |
| user_id | BIGINT | 是 | | 用户ID |
| appointment_time | DATETIME | 是 | CURRENT_TIMESTAMP | 预约时间 |
| form_file_id | BIGINT | 是 | | 填写后的表格文件ID（关联resource_file表） |
| team_name | VARCHAR(100) | 否 | NULL | 团队名称（从表格中提取） |
| team_leader | VARCHAR(50) | 否 | NULL | 团队负责人（从表格中提取） |
| contact_phone | VARCHAR(20) | 否 | NULL | 联系电话（从表格中提取） |
| team_size | INT | 否 | NULL | 团队人数（从表格中提取） |
| status | TINYINT | 否 | 1 | 预约状态：0-已取消，1-未开始，2-已完成 |
| contact_email | VARCHAR(100) | 否 | NULL | 联系人邮箱 |
| remarks | VARCHAR(500) | 否 | NULL | 备注 |
| admin_remarks | VARCHAR(500) | 否 | NULL | 管理员备注 |
| check_in_time | DATETIME | 否 | NULL | 签到时间 |
| create_time | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_appointment_no` (`appointment_no`)
- INDEX `idx_user_id` (`user_id`)
- INDEX `idx_status` (`status`)
- INDEX `idx_appointment_time` (`appointment_time`)
- INDEX `idx_team_name` (`team_name`)
- INDEX `idx_team_leader` (`team_leader`)
- INDEX `idx_contact_phone` (`contact_phone`)

### 2.3 团队预约设置表（team_appointment_setting）

存储团队预约的相关设置。

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|-------|------|------|-------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 设置ID |
| min_advance_days | INT | 否 | 3 | 最少提前预约天数 |
| max_team_size_small | INT | 否 | 10 | 小团队最大人数 |
| min_team_size_large | INT | 否 | 10 | 大团队最小人数 |
| appointment_notes | TEXT | 否 | NULL | 预约须知 |
| enabled | TINYINT(1) | 否 | 1 | 是否启用：0-禁用，1-启用 |
| create_time | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)

## 3. 表与界面的对应关系

### 3.1 团队预约须知页面

**对应表**：
- `team_appointment_setting` (主表)：提供预约设置信息
- `team_appointment_template` (关联表)：提供预约表格模板

**界面元素与字段映射**：
- 预约对象 → `team_appointment_setting.max_team_size_small` 和 `team_appointment_setting.min_team_size_large`
- 预约时间 → `team_appointment_setting.min_advance_days`
- 预约须知 → `team_appointment_setting.appointment_notes`
- "下载表格"按钮 → 通过 `team_appointment_template.template_file_id` 关联到 `resource_file` 表获取表格模板

### 3.2 团队预约列表页面

**对应表**：
- `team_appointment` (主表)：提供团队预约信息

**界面元素与字段映射**：
- 预约编号 → `team_appointment.appointment_no`
- 预约团队名称 → `team_appointment.team_name`
- 预约时间 → `team_appointment.appointment_time`
- 团队负责人 → `team_appointment.team_leader`
- 联系电话 → `team_appointment.contact_phone`
- 团队人数 → `team_appointment.team_size`
- 状态 → `team_appointment.status`

## 4. 查询示例

### 4.1 获取团队预约设置和表格模板

```sql
-- 获取团队预约设置
SELECT *
FROM team_appointment_setting
WHERE enabled = 1
LIMIT 1;

-- 获取团队预约表格模板
SELECT tat.*, rf.file_key, rf.bucket_name, rf.file_name
FROM team_appointment_template tat
LEFT JOIN resource_file rf ON tat.template_file_id = rf.id
WHERE tat.enabled = 1
ORDER BY tat.is_default DESC, tat.id ASC
LIMIT 1;
```

### 4.2 创建团队预约（表格上传方式）

```sql
-- 首先上传填写好的表格文件到resource_file表
INSERT INTO resource_file (file_name, file_key, bucket_name, file_size, mime_type, file_type, upload_user_id)
VALUES ('团队预约表-已填写.xlsx', 'teams/appointments/20251010/user_1001_form.xlsx', 'forms', 25600, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 3, 1001);

-- 然后创建团队预约记录，关联上传的表格文件（此时团队信息尚未提取）
INSERT INTO team_appointment (
    appointment_no, 
    user_id, 
    form_file_id, 
    status
)
VALUES (
    '5433986943600297', 
    1001, 
    LAST_INSERT_ID(), 
    1  -- 未开始状态
);
```

### 4.3 从表格中提取团队信息并存储

```sql
-- 管理员审核表格后，提取团队信息并更新团队预约表
UPDATE team_appointment
SET 
    team_name = '上海复旦大学团队',
    team_leader = '吴美玉',
    contact_phone = '18012341234',
    team_size = 30,
    status = 2,  -- 已完成
    admin_remarks = '表格信息完整，已通过审核'
WHERE id = 1;
```

### 4.4 查询团队预约列表

```sql
-- 查询团队预约列表（管理员视图）
SELECT 
    ta.id,
    ta.appointment_no AS '预约编号',
    ta.team_name AS '预约团队名称',
    ta.appointment_time AS '预约时间',
    ta.team_leader AS '团队负责人',
    ta.contact_phone AS '联系电话',
    ta.team_size AS '团队人数',
    CASE 
        WHEN ta.status = 0 THEN '已取消'
        WHEN ta.status = 1 THEN '未开始'
        WHEN ta.status = 2 THEN '已完成'
        ELSE '未知状态'
    END AS '状态'
FROM team_appointment ta
ORDER BY ta.appointment_time DESC
LIMIT 10 OFFSET 0;

-- 查询特定用户的团队预约记录
SELECT 
    ta.appointment_no,
    ta.team_name,
    ta.appointment_time,
    ta.team_leader,
    ta.contact_phone,
    ta.team_size,
    ta.status,
    rf.file_key,
    rf.bucket_name,
    rf.file_name
FROM team_appointment ta
LEFT JOIN resource_file rf ON ta.form_file_id = rf.id
WHERE ta.user_id = 1001
ORDER BY ta.appointment_time DESC;
```

## 5. 注意事项

1. 使用逻辑外键而非物理外键约束，提高数据库的灵活性
2. 所有媒体资源通过关联 `resource_file` 表统一管理
3. 团队预约状态（未开始、已完成、已取消）可以通过定时任务自动更新
4. 团队预约表格模板可以根据不同的团队类型（大团队、小团队）提供不同的模板
5. 团队预约须知可以通过管理后台进行配置，支持富文本编辑
