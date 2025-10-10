# 界面与数据库表映射关系

根据提供的图片，以下是各个界面与数据库表的对应关系：

## 1. 保护区大事件列表

**对应表**：
- `protected_area_event` (主表)：提供大事件的基本信息，如标题、日期、封面图等

**界面元素与字段映射**：
- 大事件卡片标题 → `protected_area_event.title`
- 大事件日期 → `protected_area_event.event_date`
- 大事件封面图 → `protected_area_event.cover_image_url`
- 大事件视频 → `protected_area_event.cover_video_url`
- 年份筛选 → `protected_area_event.event_year`
- 月份筛选 → `protected_area_event.event_month`

## 2. 保护区大事件搜索

**对应表**：
- `protected_area_event` (主表)：搜索的主要目标表
- `user_search_history` (关联表)：记录用户的搜索历史

**界面元素与字段映射**：
- 搜索框 → 主要搜索 `protected_area_event.title` 字段
- 历史搜索标签 → `user_search_history.keyword` 字段

## 3. 保护区大事件搜索结果

**对应表**：
- `protected_area_event` (主表)：提供搜索结果的基本信息

**界面元素与字段映射**：
- 搜索结果列表项 → `protected_area_event` 表中符合搜索条件的记录
- 结果项标题 → `protected_area_event.title`
- 结果项日期 → `protected_area_event.event_date`
- 结果项图片 → `protected_area_event.cover_image_url`

## 4. 保护区大事件详情

**对应表**：
- `protected_area_event` (主表)：提供大事件的详细信息
- `protected_area_event_media` (关联表)：提供大事件的媒体资源

**界面元素与字段映射**：
- 大事件标题 → `protected_area_event.title`
- 大事件日期 → `protected_area_event.event_date`
- 大事件内容 → `protected_area_event.content`
- 大事件地点 → `protected_area_event.location`
- 大事件图片轮播/流动播放 → `protected_area_event_media` 表中与该事件关联的多个媒体记录，按 `sort_order` 字段排序
- 大事件视频 → `protected_area_event_media` 表中 `media_type = 2` 的记录

## 5. 保护区大事件详情-精彩瞬间

**对应表**：
- `protected_area_event_media` (主表)：提供大事件的精彩瞬间媒体资源

**界面元素与字段映射**：
- 精彩瞬间图片/视频 → `protected_area_event_media` 表中 `is_highlight = 1` 的记录
- 媒体标题 → `protected_area_event_media.title`
- 媒体描述 → `protected_area_event_media.description`
- 媒体URL → `protected_area_event_media.media_url`
- 缩略图URL → `protected_area_event_media.thumbnail_url`

## 6. 保护区大事件详情-相关讲解

**对应表**：
- `protected_area_event_explanation` (主表)：提供大事件的相关讲解内容

**界面元素与字段映射**：
- 讲解标题 → `protected_area_event_explanation.title`
- 讲解内容 → `protected_area_event_explanation.content`
- 讲解作者 → `protected_area_event_explanation.author`
- 讲解配图 → `protected_area_event_explanation.image_url`
- 讲解音频 → `protected_area_event_explanation.audio_url`
- 讲解视频 → `protected_area_event_explanation.video_url`

## 6.1 保护区大事件详情-语音讲解

**对应表**：
- `protected_area_event_explanation` (主表)：提供大事件的讲解内容
- `protected_area_event_explanation_object` (关联表)：提供讲解对象信息
- `protected_area_event_explanation_audio` (关联表)：提供讲解对象的多语言音频

**界面元素与字段映射**：
- 讲解对象列表 → `protected_area_event_explanation_object` 表中与该讲解关联的对象记录
- 讲解对象名称 → `protected_area_event_explanation_object.object_name`
- 讲解对象描述 → `protected_area_event_explanation_object.object_description`
- 讲解对象图片 → `protected_area_event_explanation_object.image_url`
- 语音讲解音频播放器 → `protected_area_event_explanation_audio.audio_url`
- 语音讲解时长 → `protected_area_event_explanation_audio.audio_duration`
- 语音讲解语言选择 → `protected_area_event_explanation_audio.language`


## 表与界面功能的对应关系总结

| 数据库表 | 对应界面/功能 |
|---------|-------------|
| `protected_area_event` | 大事件列表、搜索、详情页的基本信息 |
| `protected_area_event_media` | 大事件详情页的媒体展示、精彩瞬间 |
| `protected_area_event_explanation` | 大事件详情页的相关讲解 |
| `protected_area_event_explanation_object` | 大事件详情页的讲解对象列表 |
| `protected_area_event_explanation_audio` | 大事件详情页的多语言语音讲解 |
| `protected_area_event_tag` | 大事件的标签信息、标签筛选 |
| `protected_area_event_tag_relation` | 大事件与标签的关联关系 |
| `user_search_history` | 搜索页面的历史搜索标签 |
