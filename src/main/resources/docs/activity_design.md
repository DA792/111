# 活动模块数据库设计文档

## 1. 概述

活动模块用于管理保护区内的各类活动，包括活动发布、活动预约、活动评价等功能。本文档详细描述活动模块的数据库设计和界面对应关系。

## 2. 数据库表结构

### 2.1 活动表（activity）

存储活动的基本信息。

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|-------|------|------|-------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 活动ID |
| title | VARCHAR(255) | 是 | | 活动标题 |
| summary | VARCHAR(500) | 否 | NULL | 活动摘要 |
| content_type | TINYINT | 否 | 1 | 内容类型：1-纯文本，2-Markdown |
| content | TEXT | 否 | NULL | 活动详细内容（纯文本或Markdown格式） |
| content_file_id | BIGINT | 否 | NULL | Markdown文件ID（关联resource_file表） |
| start_time | DATETIME | 是 | | 活动开始时间 |
| end_time | DATETIME | 是 | | 活动结束时间 |
| location | VARCHAR(255) | 否 | NULL | 活动地点 |
| location_detail | VARCHAR(500) | 否 | NULL | 活动地点详情 |
| min_participants | INT | 否 | 1 | 最少参与人数 |
| max_participants | INT | 否 | NULL | 最大参与人数 |
| current_participants | INT | 否 | 0 | 当前参与人数 |
| age_requirement | VARCHAR(255) | 否 | NULL | 年龄要求（如：10岁及以上） |
| registration_deadline | DATETIME | 否 | NULL | 报名截止时间 |
| cover_image_id | BIGINT | 否 | NULL | 封面图片ID（高清，用于详情页面） |
| cover_thumbnail_id | BIGINT | 否 | NULL | 封面缩略图ID（用于列表页面） |
| status | TINYINT | 否 | 1 | 活动状态：0-已取消，1-未开始，2-进行中，3-已结束 |
| is_free | TINYINT(1) | 否 | 1 | 是否免费：0-收费，1-免费（默认免费） |
| is_featured | TINYINT(1) | 否 | 0 | 是否推荐：0-普通，1-推荐 |
| view_count | INT | 否 | 0 | 浏览次数 |
| enabled | TINYINT(1) | 否 | 1 | 是否启用：0-禁用，1-启用 |
| sort_order | INT | 否 | 0 | 排序顺序 |
| create_time | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- INDEX `idx_start_time` (`start_time`)
- INDEX `idx_end_time` (`end_time`)
- INDEX `idx_status` (`status`)
- INDEX `idx_is_free` (`is_free`)
- INDEX `idx_is_featured` (`is_featured`)
- INDEX `idx_enabled` (`enabled`)

### 2.3 活动类别表（activity_category）

存储活动的类别信息。

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|-------|------|------|-------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 类别ID |
| name | VARCHAR(50) | 是 | | 类别名称 |
| description | VARCHAR(255) | 否 | NULL | 类别描述 |
| icon_id | BIGINT | 否 | NULL | 图标文件ID |
| sort_order | INT | 否 | 0 | 排序顺序 |
| enabled | TINYINT(1) | 否 | 1 | 是否启用：0-禁用，1-启用 |
| create_time | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_name` (`name`)
- INDEX `idx_enabled` (`enabled`)

### 2.4 活动-类别关联表（activity_category_relation）

实现活动和类别的多对多关系。

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|-------|------|------|-------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 关联ID |
| activity_id | BIGINT | 是 | | 活动ID |
| category_id | BIGINT | 是 | | 类别ID |
| create_time | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |

**索引**：
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_activity_category` (`activity_id`, `category_id`)
- INDEX `idx_activity_id` (`activity_id`)
- INDEX `idx_category_id` (`category_id`)

### 2.5 活动预约表格模板表（activity_registration_template）

存储活动预约表格的模板文件。

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|-------|------|------|-------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 模板ID |
| activity_id | BIGINT | 是 | | 活动ID |
| template_name | VARCHAR(255) | 是 | | 模板名称 |
| template_file_id | BIGINT | 是 | | 模板文件ID（关联resource_file表） |
| description | VARCHAR(500) | 否 | NULL | 模板描述 |
| is_default | TINYINT(1) | 否 | 0 | 是否默认模板：0-否，1-是 |
| enabled | TINYINT(1) | 否 | 1 | 是否启用：0-禁用，1-启用 |
| create_time | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- INDEX `idx_activity_id` (`activity_id`)
- INDEX `idx_is_default` (`is_default`)
- INDEX `idx_enabled` (`enabled`)

### 2.6 活动预约表（activity_registration）

存储用户的活动预约信息，包含从表格中提取的关键信息。

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|-------|------|------|-------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 预约ID |
| registration_no | VARCHAR(50) | 是 | | 预约编号 |
| activity_id | BIGINT | 是 | | 活动ID |
| activity_title | VARCHAR(255) | 是 | | 活动名称 |
| user_id | BIGINT | 是 | | 用户ID |
| registration_time | DATETIME | 是 | CURRENT_TIMESTAMP | 预约时间 |
| form_file_id | BIGINT | 是 | | 填写后的表格文件ID（关联resource_file表） |
| team_name | VARCHAR(100) | 否 | NULL | 团队名称（从表格中提取） |
| team_leader | VARCHAR(50) | 否 | NULL | 团队负责人（从表格中提取） |
| contact_phone | VARCHAR(20) | 否 | NULL | 联系电话（从表格中提取） |
| team_size | INT | 否 | NULL | 团队人数（从表格中提取） |
| status | TINYINT | 否 | 1 | 预约状态：0-已取消，1-未开始，2-已完成 |
| payment_status | TINYINT | 否 | 0 | 支付状态：0-未支付，1-已支付，2-已退款 |
| payment_amount | DECIMAL(10,2) | 否 | 0.00 | 支付金额 |
| payment_time | DATETIME | 否 | NULL | 支付时间 |
| contact_email | VARCHAR(100) | 否 | NULL | 联系人邮箱 |
| remarks | VARCHAR(500) | 否 | NULL | 备注 |
| admin_remarks | VARCHAR(500) | 否 | NULL | 管理员备注 |
| check_in_time | DATETIME | 否 | NULL | 签到时间 |
| create_time | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_registration_no` (`registration_no`)
- INDEX `idx_activity_id` (`activity_id`)
- INDEX `idx_user_id` (`user_id`)
- INDEX `idx_status` (`status`)
- INDEX `idx_payment_status` (`payment_status`)
- INDEX `idx_registration_time` (`registration_time`)
- INDEX `idx_team_name` (`team_name`)
- INDEX `idx_team_leader` (`team_leader`)
- INDEX `idx_contact_phone` (`contact_phone`)

### 2.7 活动评价表（activity_review）

存储用户对活动的评价。

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|-------|------|------|-------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 评价ID |
| activity_id | BIGINT | 是 | | 活动ID |
| user_id | BIGINT | 是 | | 用户ID |
| registration_id | BIGINT | 否 | NULL | 预约ID |
| rating | TINYINT | 是 | | 评分（1-5） |
| content | TEXT | 否 | NULL | 评价内容 |
| is_anonymous | TINYINT(1) | 否 | 0 | 是否匿名：0-否，1-是 |
| status | TINYINT | 否 | 1 | 状态：0-已删除，1-正常，2-已屏蔽 |
| create_time | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- INDEX `idx_activity_id` (`activity_id`)
- INDEX `idx_user_id` (`user_id`)
- INDEX `idx_registration_id` (`registration_id`)
- INDEX `idx_rating` (`rating`)
- INDEX `idx_status` (`status`)

### 2.8 活动通知表（activity_notification）

存储活动相关的通知。

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|-------|------|------|-------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 通知ID |
| activity_id | BIGINT | 是 | | 活动ID |
| title | VARCHAR(255) | 是 | | 通知标题 |
| content | TEXT | 是 | | 通知内容 |
| notification_time | DATETIME | 是 | CURRENT_TIMESTAMP | 通知时间 |
| notification_type | TINYINT | 否 | 1 | 通知类型：1-活动提醒，2-活动变更，3-活动取消 |
| status | TINYINT | 否 | 1 | 状态：0-已删除，1-正常 |
| create_time | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- INDEX `idx_activity_id` (`activity_id`)
- INDEX `idx_notification_time` (`notification_time`)
- INDEX `idx_notification_type` (`notification_type`)
- INDEX `idx_status` (`status`)

### 2.9 活动用户通知关联表（activity_user_notification）

实现通知和用户的多对多关系。

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|-------|------|------|-------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 关联ID |
| notification_id | BIGINT | 是 | | 通知ID |
| user_id | BIGINT | 是 | | 用户ID |
| is_read | TINYINT(1) | 否 | 0 | 是否已读：0-未读，1-已读 |
| read_time | DATETIME | 否 | NULL | 阅读时间 |
| create_time | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- INDEX `idx_notification_id` (`notification_id`)
- INDEX `idx_user_id` (`user_id`)
- INDEX `idx_is_read` (`is_read`)

### 2.10 活动预约设置表（activity_registration_setting）

存储活动预约的相关设置。

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|-------|------|------|-------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 设置ID |
| activity_id | BIGINT | 是 | | 活动ID |
| registration_start_time | DATETIME | 是 | | 预约开始时间 |
| registration_end_time | DATETIME | 是 | | 预约结束时间 |
| max_registrations_per_user | INT | 否 | 1 | 每个用户最大预约次数 |
| min_participants_per_registration | INT | 否 | 1 | 每次预约最少参与人数 |
| max_participants_per_registration | INT | 否 | NULL | 每次预约最大参与人数 |
| cancellation_deadline | DATETIME | 否 | NULL | 取消截止时间 |
| refund_policy | VARCHAR(500) | 否 | NULL | 退款政策 |
| registration_notes | TEXT | 否 | NULL | 预约须知 |
| create_time | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_activity_id` (`activity_id`)
- INDEX `idx_registration_start_time` (`registration_start_time`)
- INDEX `idx_registration_end_time` (`registration_end_time`)

## 3. 表与界面的对应关系

### 3.1 活动列表页面

**对应表**：
- `activity` (主表)：提供活动的基本信息
- `activity_category` 和 `activity_category_relation` (关联表)：提供活动分类信息

**界面元素与字段映射**：
- 活动卡片标题 → `activity.title`
- 活动日期 → `activity.start_time` 和 `activity.end_time`
- 活动地点 → `activity.location`
- 活动人数限制 → `activity.current_participants` 和 `activity.max_participants`
- 免费标签 → `activity.is_free`
- 活动封面图（列表页面缩略图） → 通过 `activity.cover_thumbnail_id` 关联到 `resource_file` 表

### 3.2 活动详情页面

**对应表**：
- `activity` (主表)：提供活动的详细信息

**界面元素与字段映射**：
- 活动标题 → `activity.title`
- 活动时间 → `activity.start_time` 和 `activity.end_time`
- 活动地点 → `activity.location`
- 适合人群 → `activity.age_requirement`（如"18岁及以上人士"）
- 活动费用 → `activity.is_free`（显示"免费"）
- 活动详情内容 → `activity.content` 或通过 `activity.content_file_id` 关联的Markdown文件
- 活动图片 → 存储在Markdown文件中的MinIO路径（如`![图片](https://minio.example.com/bucket/path/to/image.jpg)`）
- 活动封面图（详情页面高清图） → 通过 `activity.cover_image_id` 关联到 `resource_file` 表

### 3.3 活动预约页面

**对应表**：
- `activity_registration_setting` (主表)：提供预约设置信息
- `activity_registration_template` (关联表)：提供预约表格模板
- `activity_registration` (关联表)：存储用户的预约信息

**界面元素与字段映射**：
- 预约须知 → `activity_registration_setting.registration_notes`
- 预约时间限制 → `activity_registration_setting.registration_start_time` 和 `activity_registration_setting.registration_end_time`
- 预约人数限制 → `activity_registration_setting.min_participants_per_registration` 和 `activity_registration_setting.max_participants_per_registration`
- "下载表格"按钮 → 通过 `activity_registration_template.template_file_id` 关联到 `resource_file` 表获取表格模板
- 表格上传 → 上传后存储到 `activity_registration.form_file_id` 关联的 `resource_file` 表

## 4. 查询示例

### 4.1 获取活动列表

```sql
SELECT a.*, rf.file_key, rf.bucket_name
FROM activity a
LEFT JOIN resource_file rf ON a.cover_thumbnail_id = rf.id
WHERE a.enabled = 1 AND a.status IN (1, 2)
ORDER BY a.is_featured DESC, a.start_time ASC
LIMIT 10 OFFSET 0;
```

### 4.2 获取活动详情

```sql
SELECT a.*, rf.file_key, rf.bucket_name
FROM activity a
LEFT JOIN resource_file rf ON a.cover_image_id = rf.id
WHERE a.id = 1 AND a.enabled = 1;
```

### 4.4 获取活动预约设置和表格模板

```sql
-- 获取活动预约设置
SELECT *
FROM activity_registration_setting
WHERE activity_id = 1;

-- 获取活动预约表格模板
SELECT art.*, rf.file_key, rf.bucket_name, rf.file_name
FROM activity_registration_template art
LEFT JOIN resource_file rf ON art.template_file_id = rf.id
WHERE art.activity_id = 1 AND art.enabled = 1
ORDER BY art.is_default DESC, art.id ASC
LIMIT 1;
```

### 4.5 创建活动预约（表格上传方式）

```sql
-- 首先上传填写好的表格文件到resource_file表
INSERT INTO resource_file (file_name, file_key, bucket_name, file_size, mime_type, file_type, upload_user_id)
VALUES ('活动报名表-已填写.xlsx', 'activities/registrations/20251010/user_1001_form.xlsx', 'forms', 25600, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 3, 1001);

-- 然后创建活动预约记录，关联上传的表格文件（此时团队信息尚未提取）
INSERT INTO activity_registration (
    registration_no, 
    activity_id, 
    activity_title, 
    user_id, 
    form_file_id, 
    status
)
VALUES (
    '5433986943600297', 
    1, 
    '活动名称标题', 
    1001, 
    LAST_INSERT_ID(), 
    1  -- 未开始状态
);
```

### 4.6 从表格中提取团队信息并存储

```sql
-- 管理员审核表格后，提取团队信息并更新活动预约表
UPDATE activity_registration
SET 
    team_name = '上海复旦大学团队',
    team_leader = '吴美玉',
    contact_phone = '18012341234',
    team_size = 30,
    status = 2,  -- 已完成
    admin_remarks = '表格信息完整，已通过审核'
WHERE id = 1;
```

### 4.7 查询活动预约列表

```sql
-- 查询活动预约列表（管理员视图）
SELECT 
    ar.id,
    ar.registration_no AS '预约编号',
    ar.activity_title AS '预约活动名称',
    ar.team_name AS '预约团队名称',
    ar.registration_time AS '预约时间',
    ar.team_leader AS '团队负责人',
    ar.contact_phone AS '联系电话',
    ar.team_size AS '团队人数',
    CASE 
        WHEN ar.status = 0 THEN '已取消'
        WHEN ar.status = 1 THEN '未开始'
        WHEN ar.status = 2 THEN '已完成'
        ELSE '未知状态'
    END AS '状态'
FROM activity_registration ar
ORDER BY ar.registration_time DESC
LIMIT 10 OFFSET 0;

-- 查询特定用户的活动预约记录
SELECT 
    ar.registration_no,
    ar.activity_title,
    ar.team_name,
    ar.registration_time,
    ar.team_leader,
    ar.contact_phone,
    ar.team_size,
    ar.status,
    rf.file_key,
    rf.bucket_name,
    rf.file_name
FROM activity_registration ar
LEFT JOIN resource_file rf ON ar.form_file_id = rf.id
WHERE ar.user_id = 1001
ORDER BY ar.registration_time DESC;
```

## 5. 注意事项

1. 使用逻辑外键而非物理外键约束，提高数据库的灵活性
2. 所有媒体资源通过关联 `resource_file` 表统一管理
3. 活动内容支持纯文本和Markdown格式，可以通过 `content_type` 字段区分
4. 活动状态（未开始、进行中、已结束、已取消）可以通过定时任务自动更新
5. 预约人数限制和当前参与人数需要在业务逻辑中进行校验
6. 活动评价功能仅对已参加过活动的用户开放
7. 通知系统可以通过定时任务自动发送活动提醒
