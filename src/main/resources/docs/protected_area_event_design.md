# 保护区大事件模块数据库设计

## 1. 数据库表设计

### 1.1 保护区大事件表 (protected_area_event)

| 字段名 | 数据类型 | 是否为空 | 默认值 | 说明 |
|-------|---------|---------|-------|------|
| id | BIGINT | 否 | 自增 | 主键ID |
| title | VARCHAR(255) | 否 | 无 | 大事件标题 |
| summary | VARCHAR(500) | 是 | NULL | 大事件摘要 |
| content | TEXT | 是 | NULL | 大事件详细内容 |
| event_date | DATE | 否 | 无 | 大事件日期 |
| event_year | INT | 否 | 自动生成 | 大事件年份（由数据库生成） |
| event_month | INT | 否 | 自动生成 | 大事件月份（由数据库生成） |
| cover_image_url | VARCHAR(255) | 是 | NULL | 封面图片URL |
| cover_video_url | VARCHAR(255) | 是 | NULL | 封面视频URL |
| location | VARCHAR(255) | 是 | NULL | 事件发生地点 |
| view_count | INT | 是 | 0 | 浏览次数 |
| enabled | TINYINT(1) | 是 | 1 | 是否启用：0-禁用，1-启用 |
| sort_order | INT | 是 | 0 | 排序顺序 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- INDEX `idx_event_date` (`event_date`)
- INDEX `idx_event_year_month` (`event_year`, `event_month`)
- INDEX `idx_enabled` (`enabled`)

### 1.2 保护区大事件媒体表 (protected_area_event_media)

| 字段名 | 数据类型 | 是否为空 | 默认值 | 说明 |
|-------|---------|---------|-------|------|
| id | BIGINT | 否 | 自增 | 主键ID |
| event_id | BIGINT | 否 | 无 | 大事件ID（外键） |
| media_type | TINYINT | 否 | 无 | 媒体类型：1-图片，2-视频，3-音频 |
| media_url | VARCHAR(255) | 否 | 无 | 媒体URL |
| thumbnail_url | VARCHAR(255) | 是 | NULL | 缩略图URL（针对视频） |
| title | VARCHAR(255) | 是 | NULL | 媒体标题 |
| description | VARCHAR(500) | 是 | NULL | 媒体描述 |
| sort_order | INT | 是 | 0 | 排序顺序 |
| is_highlight | TINYINT(1) | 是 | 0 | 是否为精彩瞬间：0-否，1-是 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- INDEX `idx_event_id` (`event_id`)
- INDEX `idx_media_type` (`media_type`)
- INDEX `idx_is_highlight` (`is_highlight`)

**外键约束**：
- FOREIGN KEY (`event_id`) REFERENCES `protected_area_event` (`id`)


### 1.4 保护区大事件讲解表 (protected_area_event_explanation)

| 字段名 | 数据类型 | 是否为空 | 默认值 | 说明 |
|-------|---------|---------|-------|------|
| id | BIGINT | 否 | 自增 | 主键ID |
| event_id | BIGINT | 否 | 无 | 大事件ID（外键） |
| title | VARCHAR(255) | 否 | 无 | 讲解标题 |
| content | TEXT | 否 | 无 | 讲解内容 |
| author | VARCHAR(100) | 是 | NULL | 作者 |
| image_url | VARCHAR(255) | 是 | NULL | 配图URL |
| sort_order | INT | 是 | 0 | 排序顺序 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |


### 1.4.1 保护区大事件讲解对象表 (protected_area_event_explanation_object)

| 字段名 | 数据类型 | 是否为空 | 默认值 | 说明 |
|-------|---------|---------|-------|------|
| id | BIGINT | 否 | 自增 | 主键ID |
| explanation_id | BIGINT | 否 | 无 | 讲解ID（外键） |
| object_name | VARCHAR(255) | 否 | 无 | 对象名称（如：黑嘴天鹅） |
| object_description | TEXT | 是 | NULL | 对象描述 |
| image_url | VARCHAR(255) | 是 | NULL | 对象图片URL |
| sort_order | INT | 是 | 0 | 排序顺序 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- INDEX `idx_explanation_id` (`explanation_id`)

**外键约束**：
- FOREIGN KEY (`explanation_id`) REFERENCES `protected_area_event_explanation` (`id`)

### 1.4.2 保护区大事件讲解对象音频表 (protected_area_event_explanation_audio)

| 字段名 | 数据类型 | 是否为空 | 默认值 | 说明 |
|-------|---------|---------|-------|------|
| id | BIGINT | 否 | 自增 | 主键ID |
| object_id | BIGINT | 否 | 无 | 讲解对象ID（外键） |
| language | VARCHAR(50) | 否 | 'zh_CN' | 语言代码：zh_CN-中文，en_US-英语，ja_JP-日语，ko_KR-韩语等 |
| audio_url | VARCHAR(255) | 否 | 无 | 音频URL |
| audio_duration | INT | 是 | NULL | 音频时长（秒） |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- INDEX `idx_object_id` (`object_id`)
- INDEX `idx_language` (`language`)
- UNIQUE KEY `uk_object_language` (`object_id`, `language`)

**外键约束**：
- FOREIGN KEY (`object_id`) REFERENCES `protected_area_event_explanation_object` (`id`)


### 1.5 保护区大事件标签表 (protected_area_event_tag)

| 字段名 | 数据类型 | 是否为空 | 默认值 | 说明 |
|-------|---------|---------|-------|------|
| id | BIGINT | 否 | 自增 | 主键ID |
| name | VARCHAR(50) | 否 | 无 | 标签名称 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_name` (`name`)

### 1.6 保护区大事件-标签关联表 (protected_area_event_tag_relation)

| 字段名 | 数据类型 | 是否为空 | 默认值 | 说明 |
|-------|---------|---------|-------|------|
| id | BIGINT | 否 | 自增 | 主键ID |
| event_id | BIGINT | 否 | 无 | 大事件ID（外键） |
| tag_id | BIGINT | 否 | 无 | 标签ID（外键） |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |

**索引**：
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_event_tag` (`event_id`, `tag_id`)
- INDEX `idx_event_id` (`event_id`)
- INDEX `idx_tag_id` (`tag_id`)

**外键约束**：
- FOREIGN KEY (`event_id`) REFERENCES `protected_area_event` (`id`)
- FOREIGN KEY (`tag_id`) REFERENCES `protected_area_event_tag` (`id`)

### 1.7 用户搜索历史表 (user_search_history)

| 字段名 | 数据类型 | 是否为空 | 默认值 | 说明 |
|-------|---------|---------|-------|------|
| id | BIGINT | 否 | 自增 | 主键ID |
| user_id | BIGINT | 否 | 无 | 用户ID |
| keyword | VARCHAR(100) | 否 | 无 | 搜索关键词 |
| search_count | INT | 是 | 1 | 搜索次数 |
| last_search_time | DATETIME | 是 | CURRENT_TIMESTAMP | 最后搜索时间 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |

**索引**：
- PRIMARY KEY (`id`)
- INDEX `idx_user_id` (`user_id`)
- INDEX `idx_keyword` (`keyword`)
- INDEX `idx_last_search_time` (`last_search_time`)
- UNIQUE KEY `uk_user_keyword` (`user_id`, `keyword`)

## 2. 表关系说明

1. **保护区大事件表(protected_area_event)** 与 **保护区大事件媒体表(protected_area_event_media)** 是一对多关系，一个大事件可以包含多个媒体资源。

2. **保护区大事件表(protected_area_event)** 与 **保护区大事件讲解表(protected_area_event_explanation)** 是一对多关系，一个大事件可以有多条讲解内容。

3. **保护区大事件表(protected_area_event)** 与 **保护区大事件标签表(protected_area_event_tag)** 是多对多关系，通过 **保护区大事件-标签关联表(protected_area_event_tag_relation)** 建立关联。

## 3. 数据库设计考虑因素

1. **性能优化**：
   - 为常用查询条件（如日期、年份、月份、启用状态等）创建索引
   - 使用数据库生成的计算列（event_year, event_month）优化按年月查询和统计
   - 为外键关系创建索引，提高关联查询性能

2. **数据完整性**：
   - 使用外键约束确保数据引用的完整性
   - 为必填字段设置NOT NULL约束
   - 为唯一值（如标签名称）设置唯一索引

3. **扩展性**：
   - 媒体表设计支持多种媒体类型（图片、视频、音频）
   - 讲解表支持多语言内容（language字段）
   - 标签系统支持灵活的内容分类

4. **用户体验**：
   - 支持内容排序（sort_order字段）
   - 支持内容状态管理（enabled字段）
   - 记录浏览次数，可用于热门内容推荐
   - 支持多语言语音讲解，提升国际化体验

5. **数据追踪**：
   - 所有表都包含创建时间和更新时间字段，便于数据变更追踪

## 4. 查询示例

### 4.1 获取最新大事件列表

```sql
SELECT * FROM protected_area_event 
WHERE enabled = 1 
ORDER BY event_date DESC 
LIMIT 10;
```

### 4.2 按年月查询大事件

```sql
SELECT * FROM protected_area_event 
WHERE event_year = 2023 AND event_month = 6 
AND enabled = 1 
ORDER BY event_date DESC;
```

### 4.3 获取大事件详情及其媒体资源

```sql
SELECT e.*, m.* 
FROM protected_area_event e 
LEFT JOIN protected_area_event_media m ON e.id = m.event_id 
WHERE e.id = 123 AND e.enabled = 1 
ORDER BY m.sort_order;
```

### 4.3.1 获取大事件的图片轮播/流动播放资源

```sql
SELECT * FROM protected_area_event_media 
WHERE event_id = 123 AND media_type = 1 
ORDER BY sort_order;
```

### 4.4 获取大事件的精彩瞬间

```sql
SELECT * FROM protected_area_event_media 
WHERE event_id = 123 AND is_highlight = 1 
ORDER BY sort_order;
```


### 4.6 按标签查询大事件

```sql
SELECT e.* 
FROM protected_area_event e 
JOIN protected_area_event_tag_relation r ON e.id = r.event_id 
JOIN protected_area_event_tag t ON r.tag_id = t.id 
WHERE t.name = '迁徙' AND e.enabled = 1 
ORDER BY e.event_date DESC;
```

### 4.7 获取大事件的讲解对象列表

```sql
SELECT o.* 
FROM protected_area_event_explanation e
JOIN protected_area_event_explanation_object o ON e.id = o.explanation_id
WHERE e.event_id = 123
ORDER BY o.sort_order;
```

### 4.8 获取讲解对象的多语言音频

```sql
SELECT o.object_name, o.object_description, o.image_url, a.language, a.audio_url, a.audio_duration
FROM protected_area_event_explanation_object o
JOIN protected_area_event_explanation_audio a ON o.id = a.object_id
WHERE o.id = 456
ORDER BY a.language;

-- 获取特定语言的讲解对象音频
SELECT o.object_name, o.object_description, o.image_url, a.audio_url, a.audio_duration
FROM protected_area_event_explanation_object o
JOIN protected_area_event_explanation_audio a ON o.id = a.object_id
WHERE o.id = 456 AND a.language = 'zh_CN';

-- 获取大事件下所有讲解对象的中文音频
SELECT o.object_name, o.object_description, o.image_url, a.audio_url, a.audio_duration
FROM protected_area_event_explanation e
JOIN protected_area_event_explanation_object o ON e.id = o.explanation_id
JOIN protected_area_event_explanation_audio a ON o.id = a.object_id
WHERE e.event_id = 123 AND a.language = 'zh_CN'
ORDER BY o.sort_order;
```

### 4.9 搜索大事件

```sql
SELECT * FROM protected_area_event 
WHERE title LIKE '%小天鹅%' AND enabled = 1 
ORDER BY event_date DESC;
```

### 4.10 获取用户搜索历史

```sql
-- 获取用户最近的搜索历史，按最后搜索时间倒序排列
SELECT keyword, search_count, last_search_time 
FROM user_search_history 
WHERE user_id = 123 
ORDER BY last_search_time DESC 
LIMIT 10;

-- 获取用户搜索次数最多的关键词
SELECT keyword, search_count 
FROM user_search_history 
WHERE user_id = 123 
ORDER BY search_count DESC 
LIMIT 5;

-- 记录用户搜索历史（新增或更新）
INSERT INTO user_search_history (user_id, keyword) 
VALUES (123, '天鹅') 
ON DUPLICATE KEY UPDATE 
search_count = search_count + 1, 
last_search_time = CURRENT_TIMESTAMP;
