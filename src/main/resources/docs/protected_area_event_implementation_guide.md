# 保护区大事件模块实现指南

## 1. 概述

保护区大事件模块用于记录和展示保护区内发生的重要事件，如物种迁徙、生态变化、保护成果等。本模块包括大事件的基本信息管理、媒体资源管理、讲解内容管理以及标签分类等功能。

## 2. 技术架构

- **后端**：Spring Boot + MyBatis
- **数据库**：MySQL
- **前端**：Vue.js + Element UI
- **文件存储**：阿里云OSS

## 3. 数据库表结构

本模块包含以下数据库表：

1. `protected_area_event`：保护区大事件表
2. `protected_area_event_media`：保护区大事件媒体表
3. `protected_area_event_explanation`：保护区大事件讲解表
4. `protected_area_event_tag`：保护区大事件标签表
5. `protected_area_event_tag_relation`：保护区大事件-标签关联表

详细的表结构设计请参考 [保护区大事件模块数据库设计](protected_area_event_design.md)。

## 4. 接口设计

### 4.1 大事件管理接口

#### 4.1.1 获取大事件列表

```
GET /api/protected-area/events
```

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| page | Integer | 否 | 页码，默认1 |
| size | Integer | 否 | 每页条数，默认10 |
| year | Integer | 否 | 筛选年份 |
| month | Integer | 否 | 筛选月份 |
| keyword | String | 否 | 搜索关键词 |
| tagId | Long | 否 | 标签ID |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "list": [
      {
        "id": 1,
        "title": "小天鹅迁徙",
        "summary": "2023年春季小天鹅迁徙观测记录",
        "eventDate": "2023-03-15",
        "coverImageUrl": "https://example.com/images/swan1.jpg",
        "coverVideoUrl": null,
        "location": "湿地保护区北区",
        "viewCount": 1250,
        "tags": [
          {"id": 1, "name": "迁徙"},
          {"id": 3, "name": "小天鹅"}
        ]
      },
      // 更多记录...
    ]
  }
}
```

#### 4.1.2 获取大事件详情

```
GET /api/protected-area/events/{id}
```

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | Long | 是 | 大事件ID |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "小天鹅迁徙",
    "summary": "2023年春季小天鹅迁徙观测记录",
    "content": "详细内容...",
    "eventDate": "2023-03-15",
    "coverImageUrl": "https://example.com/images/swan1.jpg",
    "coverVideoUrl": null,
    "location": "湿地保护区北区",
    "viewCount": 1250,
    "tags": [
      {"id": 1, "name": "迁徙"},
      {"id": 3, "name": "小天鹅"}
    ],
    "createTime": "2023-03-16T10:30:00",
    "updateTime": "2023-03-16T15:45:00"
  }
}
```

#### 4.1.3 创建大事件

```
POST /api/protected-area/events
```

**请求参数**：

```json
{
  "title": "小天鹅迁徙",
  "summary": "2023年春季小天鹅迁徙观测记录",
  "content": "详细内容...",
  "eventDate": "2023-03-15",
  "coverImageUrl": "https://example.com/images/swan1.jpg",
  "coverVideoUrl": null,
  "location": "湿地保护区北区",
  "tagIds": [1, 3],
  "enabled": true,
  "sortOrder": 0
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1
  }
}
```

#### 4.1.4 更新大事件

```
PUT /api/protected-area/events/{id}
```

**请求参数**：与创建大事件相同

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.1.5 删除大事件

```
DELETE /api/protected-area/events/{id}
```

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | Long | 是 | 大事件ID |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.1.6 获取用户搜索历史

```
GET /api/protected-area/events/search-history
```

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| limit | Integer | 否 | 返回记录数量，默认10 |
| orderBy | String | 否 | 排序方式：time-按时间，count-按次数，默认time |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "keyword": "天鹅",
      "searchCount": 5,
      "lastSearchTime": "2023-10-08T15:30:00"
    },
    {
      "keyword": "候鸟",
      "searchCount": 3,
      "lastSearchTime": "2023-10-07T10:15:00"
    },
    {
      "keyword": "小天鹅",
      "searchCount": 2,
      "lastSearchTime": "2023-10-05T09:20:00"
    }
  ]
}
```

#### 4.1.7 记录搜索历史

```
POST /api/protected-area/events/search-history
```

**请求参数**：

```json
{
  "keyword": "天鹅"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.1.8 清除搜索历史

```
DELETE /api/protected-area/events/search-history
```

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| keyword | String | 否 | 指定关键词，不传则清除所有历史 |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 4.2 大事件媒体管理接口

#### 4.2.1 获取大事件媒体列表

```
GET /api/protected-area/events/{eventId}/media
```

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| eventId | Long | 是 | 大事件ID |
| isHighlight | Boolean | 否 | 是否为精彩瞬间 |
| mediaType | Integer | 否 | 媒体类型：1-图片，2-视频，3-音频 |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "eventId": 1,
      "mediaType": 1,
      "mediaUrl": "https://example.com/images/swan1.jpg",
      "thumbnailUrl": null,
      "title": "小天鹅群像",
      "description": "小天鹅在湿地觅食",
      "isHighlight": true,
      "sortOrder": 0
    },
    // 更多记录...
  ]
}
```

#### 4.2.2 添加大事件媒体

```
POST /api/protected-area/events/{eventId}/media
```

**请求参数**：

```json
{
  "mediaType": 1,
  "mediaUrl": "https://example.com/images/swan2.jpg",
  "thumbnailUrl": null,
  "title": "小天鹅飞行",
  "description": "小天鹅起飞瞬间",
  "isHighlight": true,
  "sortOrder": 1
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 2
  }
}
```

#### 4.2.3 更新大事件媒体

```
PUT /api/protected-area/events/{eventId}/media/{id}
```

**请求参数**：与添加大事件媒体相同

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.2.4 删除大事件媒体

```
DELETE /api/protected-area/events/{eventId}/media/{id}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 4.3 大事件讲解管理接口

#### 4.3.1 获取大事件讲解列表

```
GET /api/protected-area/events/{eventId}/explanations
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "eventId": 1,
      "title": "小天鹅的迁徙习性",
      "content": "讲解内容...",
      "author": "张生态",
      "imageUrl": "https://example.com/images/explanation1.jpg",
      "audioUrl": "https://example.com/audio/explanation1.mp3",
      "videoUrl": null,
      "sortOrder": 0
    },
    // 更多记录...
  ]
}
```

#### 4.3.2 添加大事件讲解

```
POST /api/protected-area/events/{eventId}/explanations
```

**请求参数**：

```json
{
  "title": "小天鹅的迁徙路线",
  "content": "讲解内容...",
  "author": "李研究",
  "imageUrl": "https://example.com/images/explanation2.jpg",
  "audioUrl": "https://example.com/audio/explanation2.mp3",
  "videoUrl": null,
  "sortOrder": 1
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 2
  }
}
```

#### 4.3.3 更新大事件讲解

```
PUT /api/protected-area/events/{eventId}/explanations/{id}
```

**请求参数**：与添加大事件讲解相同

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.3.4 删除大事件讲解

```
DELETE /api/protected-area/events/{eventId}/explanations/{id}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```


### 4.5 标签管理接口

#### 4.5.1 获取标签列表

```
GET /api/protected-area/event-tags
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "迁徙"
    },
    {
      "id": 2,
      "name": "繁殖"
    },
    {
      "id": 3,
      "name": "小天鹅"
    },
    // 更多记录...
  ]
}
```

#### 4.5.2 创建标签

```
POST /api/protected-area/event-tags
```

**请求参数**：

```json
{
  "name": "保护成果"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 4
  }
}
```

#### 4.5.3 更新标签

```
PUT /api/protected-area/event-tags/{id}
```

**请求参数**：

```json
{
  "name": "保护项目"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.5.4 删除标签

```
DELETE /api/protected-area/event-tags/{id}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

## 5. 前端页面设计

### 5.1 大事件列表页

- 展示大事件列表，支持分页
- 提供年份、月份筛选功能
- 提供标签筛选功能
- 提供搜索功能
- 每个大事件项显示标题、日期、封面图/视频、简介等信息

### 5.2 大事件详情页

- 展示大事件的详细信息，包括标题、日期、地点、内容等
- 展示大事件的媒体资源，支持多张图片轮播/流动播放功能
- 支持视频播放功能
- 展示大事件的精彩瞬间
- 展示大事件的相关讲解和多语言语音讲解
- 显示相关标签

### 5.3 管理后台

- 大事件管理：支持创建、编辑、删除大事件
- 媒体资源管理：支持上传、编辑、删除媒体资源
- 讲解内容管理：支持创建、编辑、删除讲解内容，包括多语言语音讲解
- 标签管理：支持创建、编辑、删除标签

## 6. 实现步骤

1. 创建数据库表
2. 实现实体类和DAO层
3. 实现Service层业务逻辑
4. 实现Controller层接口
5. 实现前端页面
6. 进行单元测试和集成测试
7. 部署上线

## 7. 注意事项

1. 媒体资源上传需要进行文件类型和大小的验证
2. 大事件内容支持富文本编辑
3. 需要实现缓存机制，提高访问性能
4. 需要实现权限控制，区分普通用户和管理员权限
5. 多语言语音讲解需要考虑不同语言的音频文件管理和切换机制
