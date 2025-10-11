# 保护区大事件模块ER图

以下是保护区大事件模块的实体关系图，展示了各个表之间的关系。

```mermaid
erDiagram
    PROTECTED_AREA_EVENT ||--o{ PROTECTED_AREA_EVENT_MEDIA : "包含"
    PROTECTED_AREA_EVENT ||--o{ PROTECTED_AREA_EVENT_COMMENT : "评论"
    PROTECTED_AREA_EVENT ||--o{ PROTECTED_AREA_EVENT_EXPLANATION : "讲解"
    PROTECTED_AREA_EVENT ||--o{ PROTECTED_AREA_EVENT_TAG_RELATION : "标记"
    PROTECTED_AREA_EVENT_TAG_RELATION }|--|| PROTECTED_AREA_EVENT_TAG : "使用"
    USER ||--o{ PROTECTED_AREA_EVENT_COMMENT : "发表"
    PROTECTED_AREA_EVENT_COMMENT ||--o{ PROTECTED_AREA_EVENT_COMMENT : "回复"

    PROTECTED_AREA_EVENT {
        BIGINT id PK "大事件ID"
        VARCHAR(255) title "大事件标题"
        VARCHAR(500) summary "大事件摘要"
        TEXT content "大事件详细内容"
        DATE event_date "大事件日期"
        INT event_year "大事件年份"
        INT event_month "大事件月份"
        VARCHAR(255) cover_image_url "封面图片URL"
        VARCHAR(255) cover_video_url "封面视频URL"
        VARCHAR(255) location "事件发生地点"
        INT view_count "浏览次数"
        TINYINT(1) enabled "是否启用"
        INT sort_order "排序顺序"
        DATETIME create_time "创建时间"
        DATETIME update_time "更新时间"
    }

    PROTECTED_AREA_EVENT_MEDIA {
        BIGINT id PK "媒体ID"
        BIGINT event_id FK "大事件ID"
        TINYINT media_type "媒体类型"
        VARCHAR(255) media_url "媒体URL"
        VARCHAR(255) thumbnail_url "缩略图URL"
        VARCHAR(255) title "媒体标题"
        VARCHAR(500) description "媒体描述"
        INT sort_order "排序顺序"
        TINYINT(1) is_highlight "是否为精彩瞬间"
        DATETIME create_time "创建时间"
        DATETIME update_time "更新时间"
    }

    PROTECTED_AREA_EVENT_COMMENT {
        BIGINT id PK "评论ID"
        BIGINT event_id FK "大事件ID"
        BIGINT user_id FK "用户ID"
        VARCHAR(1000) content "评论内容"
        BIGINT parent_id FK "父评论ID"
        INT likes "点赞数"
        TINYINT status "状态"
        DATETIME create_time "创建时间"
        DATETIME update_time "更新时间"
    }

    PROTECTED_AREA_EVENT_EXPLANATION {
        BIGINT id PK "讲解ID"
        BIGINT event_id FK "大事件ID"
        VARCHAR(255) title "讲解标题"
        TEXT content "讲解内容"
        VARCHAR(100) author "作者"
        VARCHAR(255) image_url "配图URL"
        VARCHAR(255) audio_url "音频URL"
        VARCHAR(255) video_url "视频URL"
        INT sort_order "排序顺序"
        DATETIME create_time "创建时间"
        DATETIME update_time "更新时间"
    }

    PROTECTED_AREA_EVENT_TAG {
        BIGINT id PK "标签ID"
        VARCHAR(50) name "标签名称"
        DATETIME create_time "创建时间"
        DATETIME update_time "更新时间"
    }

    PROTECTED_AREA_EVENT_TAG_RELATION {
        BIGINT id PK "关联ID"
        BIGINT event_id FK "大事件ID"
        BIGINT tag_id FK "标签ID"
        DATETIME create_time "创建时间"
    }

    USER {
        BIGINT id PK "用户ID"
        VARCHAR(255) user_name "用户名"
        VARCHAR(255) nickname "昵称"
        VARCHAR(255) avatar "头像"
    }
```

## 表关系说明

1. **保护区大事件表(PROTECTED_AREA_EVENT)** 与 **保护区大事件媒体表(PROTECTED_AREA_EVENT_MEDIA)** 是一对多关系，一个大事件可以包含多个媒体资源。

2. **保护区大事件表(PROTECTED_AREA_EVENT)** 与 **保护区大事件评论表(PROTECTED_AREA_EVENT_COMMENT)** 是一对多关系，一个大事件可以有多条评论。

3. **保护区大事件表(PROTECTED_AREA_EVENT)** 与 **保护区大事件讲解表(PROTECTED_AREA_EVENT_EXPLANATION)** 是一对多关系，一个大事件可以有多条讲解内容。

4. **保护区大事件表(PROTECTED_AREA_EVENT)** 与 **保护区大事件标签表(PROTECTED_AREA_EVENT_TAG)** 是多对多关系，通过 **保护区大事件-标签关联表(PROTECTED_AREA_EVENT_TAG_RELATION)** 建立关联。

5. **用户表(USER)** 与 **保护区大事件评论表(PROTECTED_AREA_EVENT_COMMENT)** 是一对多关系，一个用户可以发表多条评论。

6. **保护区大事件评论表(PROTECTED_AREA_EVENT_COMMENT)** 自关联，实现评论回复功能，一条评论可以有多条回复评论。
