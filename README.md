<<<<<<< HEAD
# 景区管理系统

## 项目概述

本项目是一个基于Spring Boot的一体化景区管理系统，同时支持小程序端和管理后台端的访问。通过统一的后端API服务，为不同端提供相应的功能接口。

## 预约模块功能说明

预约模块是本系统的核心功能模块，实现了完整的个人预约流程：

### 小程序端预约流程
1. 用户选择预约日期和入园时间
2. 用户添加预约人信息
3. 提交预约请求
4. 系统校验人数上限、证件号/电话合法性
5. 成功写入数据库并返回预约编号
6. 弹窗提示"预约成功"，显示预约详情
7. 超限或错误时提示失败原因

### 小程序端团队预约流程
1. 用户下载团队预约表格模板
2. 填写团队预约信息并上传表格
3. 系统解析文件，校验字段并写入数据库
4. 返回成功状态，页面提示"提交成功"
5. 团队预约进入待审核状态

### 小程序端活动预约流程
1. 用户选择活动并填写预约信息
2. 提交活动预约请求
3. 系统校验联系人信息合法性
4. 成功写入数据库并返回预约编号
5. 弹窗提示"预约成功"，显示预约详情
6. 错误时提示失败原因

### 管理端预约管理
1. 管理员可通过姓名、电话、时间等条件搜索预约记录
2. 查看预约详情
3. 审核预约（通过/拒绝）- 仅适用于需要审核的预约类型
4. 删除预约记录
5. 编辑预约信息

### 管理端团队预约管理
1. 管理员可导入/导出团队预约文件
2. 查看团队预约详情
3. 审核团队预约（通过/拒绝）
4. 删除团队预约记录（需二次确认）
5. 批量导入团队预约信息

### 管理端活动预约管理
1. 管理员可通过活动名称、联系人等条件搜索活动预约记录
2. 查看活动预约详情
3. 审核活动预约（通过/拒绝）
4. 删除活动预约记录
5. 导出活动预约数据

### 预约统计功能
1. 管理员可查看指定日期范围的预约统计图表
2. 查看热门景点预约排行
3. 查看用户个人预约统计信息
4. 查看预约趋势变化图表
5. 小程序端用户可查看个人预约统计和趋势信息

## 地图导览模块功能说明

地图导览模块为用户提供景区地图浏览和导览路线功能：

## 拍照打卡模块功能说明

拍照打卡模块为用户提供拍照打卡和分享功能：

### 小程序端拍照打卡功能
1. 用户打开拍照打卡页面
2. 用户上传照片并发布
3. 图片存储成功，生成数据库记录
4. 上传后显示在前端列表，可预览

### 小程序端拍照分享功能
1. 用户进入分享区
2. 用户按分类搜索照片
3. 搜索接口返回匹配结果
4. 用户可对照片进行点赞操作
5. 点赞操作写入数据库并同步
6. 可正确搜索和点赞图片，点赞数实时更新

### 管理端拍照打卡管理功能
1. 管理员查看用户上传的图片
2. 管理员可对图片进行分类管理
3. 图片数据同步数据库
4. 删除后小程序端更新
5. 后台能看到用户上传的图片，可修改分类或删除

### 小程序端VR/AR打卡功能
1. 用户选择 VR/AR 模式
2. 用户查看内容
3. 前端调用外部接口，返回内容可渲染
4. 页面展示虚拟内容

### 管理端AR内容管理功能
1. 管理员上传 AR 图片/内容
2. 上传文件写入数据库
3. 前端接口可读取
4. 小程序端能正常加载

### 小程序端地图导览功能
1. 用户打开地图页面
2. 可切换显示景点/展馆/站点/路线等不同类型的地图元素
3. 地图调用接口成功返回点位/路线数据，定位精度可用
4. 可正确显示定位点与路线
5. 用户可查看景点详细信息
6. 用户可查看导览路线详情及路线上的节点信息

## 服务管理模块功能说明

### 小程序端 - 个人信息
1. 用户进入个人中心
2. 编辑头像/昵称/手机号
3. 用户信息更新接口写入数据库
4. 修改后页面实时更新

具体实现：
- UserServiceImpl.getUserProfile()方法从数据库查询用户信息并返回
- UserServiceImpl.updateUserProfile()方法更新数据库中的用户信息

### 小程序端 - 我的预约
1. 用户查看预约记录
2. 取消未开始的预约
3. 取消操作写入数据库，二维码状态更新
4. 未开始可取消，已结束二维码灰显不可用

具体实现：
- UserServiceImpl.getUserAppointments()方法从数据库查询用户的预约记录并返回
- UserServiceImpl.cancelAppointment()方法更新数据库中的预约状态

## 系统配置模块功能说明

系统配置模块为管理员提供系统级别的配置管理功能：

### 通知管理功能
1. 管理员可创建、编辑、删除系统通知
2. 支持多种通知渠道：短信、邮件、小程序推送、服务号推送
3. 可设置通知的接收人（用户或角色）
4. 支持手动发送和定时发送通知
5. 可查看通知发送状态和历史记录

### 主题设置功能
1. 管理员可创建、编辑、删除系统主题
2. 支持自定义主题色彩配置
3. 可设置默认主题
4. 小程序端可根据主题设置动态调整界面样式

## 保护区介绍模块功能说明

保护区介绍模块为用户提供保护区相关信息的浏览功能：

### 小程序端保护区介绍功能
1. 用户查看轮播图/文章列表
2. 用户点击进入详情页面
3. 文章、语音数据正确加载
4. 切换语言正常，页面展示文字、图片、语音
5. 多语言切换功能正常

### 管理端保护区介绍管理功能
1. 管理员新增文章/视频
2. 上传图库/语音文件
3. 新增内容写入数据库并同步接口
4. 小程序端实时显示新增内容


## 架构设计

### 整体架构
```
景区管理系统
├── 小程序端 (Miniapp)
├── 管理后台端 (Admin)
└── 共享服务层
    ├── 预约管理模块
    ├── 用户管理模块
    ├── 内容管理模块
    ├── 地图导览模块
    ├── 互动管理模块
    ├── 服务管理模块
    ├── 运营管理模块
    └── 系统配置模块
```

### API接口设计

本系统采用统一的API接口设计，通过不同的URL前缀来区分小程序端和管理后台端的访问：

- 小程序端API前缀：`/api/uniapp`
- 管理后台端API前缀：`/api/manage`

#### 示例：

1. 小程序端用户微信登录：
   ```
   POST /api/uniapp/login/wechat
   ```

2. 管理后台端查询用户列表：
   ```
   GET /api/manage/users
   ```

### 模块划分

系统共分为9个功能模块：

1. **全面的预约管理能力**
   - 个人预约、团队预约、活动预约
   - 预约审核、预约统计

2. **灵活的地图导览配置**
   - 景点信息管理
   - 导览路线规划

3. **增强游客互动与参与感**
   - 评论、点赞、分享功能
   - 用户反馈收集
   - 拍照打卡功能
   - VR/AR打卡功能
   - 保护区介绍功能

4. **丰富多样的内容展示**
   - 新闻资讯发布
   - 活动信息展示

5. **便捷的游客服务体验**
   - 在线客服
   - 常见问题解答

6. **高效的景区运营管理**
   - 数据统计分析
   - 运营报表生成
   - 用户管理
   - 操作员管理

7. **系统可配置性与可维护性强**
   - 参数配置管理
   - 系统日志记录
   - 通知管理
   - 主题设置

8. **智能化的信息查询支持**
   - 智能搜索
   - 个性化推荐

9. **安全的权限管理体系**
   - 操作员权限控制
   - 用户角色管理

10. **智能化的信息查询支持**
    - 知识库管理
    - 知识图谱构建
    - AI问答服务

## 技术栈

- 后端框架：Spring Boot 2.x
- 数据库：MySQL 8.x
- 安全框架：Spring Security
- API文档：Swagger (可选)
- 缓存：Redis (必需，用于提升系统性能和数据缓存)
- 消息队列：RabbitMQ (可选)

## Redis缓存策略

本系统采用Redis作为缓存层，以提升系统性能和减轻数据库压力。主要缓存策略如下：

### 缓存设计原则
1. **读缓存**：优先从Redis中读取数据，缓存未命中时查询数据库并写入缓存
2. **写穿透**：数据更新时同时更新数据库和缓存
3. **缓存失效**：数据删除或更新时主动清除相关缓存
4. **过期策略**：设置合理的缓存过期时间，避免数据不一致

### 缓存实现模块
1. **活动管理模块**：缓存活动列表和活动详情
2. **保护区介绍模块**：缓存介绍列表、按语言分类的介绍和介绍详情
3. **地图导览模块**：缓存景点列表、按分类的景点、导览路线列表、按分类的路线和路线详情
4. **互动管理模块**：缓存AR内容、照片打卡记录等

### 缓存键设计
- 活动缓存：`activity:{id}`、`all_activities`
- 保护区介绍缓存：`introduction:{id}`、`all_introductions`、`introductions_language:{language}`
- 景点缓存：`scenic_spot:{id}`、`all_scenic_spots`、`scenic_spots_category:{category}`
- 导览路线缓存：`guide_route:{id}`、`all_guide_routes`、`guide_routes_category:{category}`
- AR内容缓存：`ar_content:{id}`、`all_ar_contents`、`ar_contents_target:{targetId}_{targetType}`、`ar_contents_content_type:{contentType}`
- 照片打卡缓存：`photo_check_in:{id}`、`all_photos`、`photos_category:{category}`、`photos_user_id:{userId}`

### 缓存更新机制
1. 数据新增时：清除相关列表缓存
2. 数据更新时：更新具体数据缓存，清除相关列表缓存
3. 数据删除时：清除具体数据缓存和相关列表缓存

## 部署说明

1. 确保已安装Java 8+和MySQL 8.0+
2. 创建数据库：`scenic_db`
3. 修改`application.yml`中的数据库连接配置
4. 运行项目：
   ```bash
   mvn spring-boot:run
   ```
5. 访问地址：`http://localhost:8080`

## API访问示例

### 小程序端API

1. 用户微信登录
   ```
   POST /api/uniapp/login/wechat
   参数：code
   ```

2. 创建预约
   ```
   POST /api/uniapp/appointments
   参数：预约信息JSON
   ```

3. 获取用户个人信息
   ```
   GET /api/uniapp/user/profile/{userId}
   ```

4. 更新用户个人信息
   ```
   PUT /api/uniapp/user/profile/{userId}
   参数：用户信息JSON
   ```

5. 获取用户的预约记录
   ```
   GET /api/uniapp/user/appointments/{userId}
   参数：page, size
   ```

6. 取消预约
   ```
   PUT /api/uniapp/user/appointments/{appointmentId}/cancel
   ```

### 管理后台端API

1. 查询预约列表
   ```
   GET /api/manage/appointments
   参数：page, size, userName, scenicSpotName, status
   ```

2. 审核预约（仅适用于需要审核的预约类型，如团队预约、活动预约）
   ```
   PUT /api/manage/appointments/{appointmentId}/review
   参数：status
   ```

### 预约统计API

1. 管理端获取指定日期范围预约统计
   ```
   GET /api/manage/appointment-statistics/date-range
   参数：startDate, endDate
   ```

2. 管理端获取热门景点统计
   ```
   GET /api/manage/appointment-statistics/popular-scenic-spots
   参数：limit
   ```

3. 管理端获取用户预约统计
   ```
   GET /api/manage/appointment-statistics/user/{userId}
   ```

4. 管理端获取预约趋势统计
   ```
   GET /api/manage/appointment-statistics/trends
   参数：days
   ```

### 地图导览API

1. 小程序端获取所有启用的景点信息
   ```
   GET /api/uniapp/map/scenic-spots
   返回：ScenicSpotDTO列表
   ```

2. 小程序端根据分类获取景点信息
   ```
   GET /api/uniapp/map/scenic-spots/category
   参数：category
   返回：ScenicSpotDTO列表
   ```

3. 小程序端获取所有启用的导览路线
   ```
   GET /api/uniapp/map/guide-routes
   返回：GuideRouteDTO列表
   ```

4. 小程序端根据分类获取导览路线
   ```
   GET /api/uniapp/map/guide-routes/category
   参数：category
   返回：GuideRouteDTO列表
   ```

5. 小程序端根据路线ID获取路线详情及节点信息
   ```
   GET /api/uniapp/map/guide-routes/{routeId}
   返回：GuideRouteDTO
   ```

### 管理端地图导览API

1. 管理端获取所有景点信息（包括已禁用的）
   ```
   GET /api/manage/map/scenic-spots
   返回：ScenicSpotDTO列表
   ```

### 拍照打卡API

1. 小程序端上传照片打卡
   ```
   POST /api/uniapp/photo-check-in/upload
   参数：photo, userId, userName, description, category, latitude, longitude
   ```

2. 小程序端获取所有照片打卡记录
   ```
   GET /api/uniapp/photo-check-in/list
   返回：PhotoCheckInDTO列表
   ```

3. 小程序端根据分类获取照片打卡记录
   ```
   GET /api/uniapp/photo-check-in/category/{category}
   返回：PhotoCheckInDTO列表
   ```

4. 小程序端点赞照片打卡
   ```
   POST /api/uniapp/photo-check-in/like/{photoCheckInId}
   ```

5. 小程序端取消点赞照片打卡
   ```
   POST /api/uniapp/photo-check-in/unlike/{photoCheckInId}
   ```

6. 管理端获取所有照片打卡记录
   ```
   GET /api/manage/photo-check-in/list
   返回：PhotoCheckInDTO列表
   ```

7. 管理端根据分类获取照片打卡记录
   ```
   GET /api/manage/photo-check-in/category/{category}
   返回：PhotoCheckInDTO列表
   ```

8. 管理端根据用户ID获取照片打卡记录
   ```
   GET /api/manage/photo-check-in/user/{userId}
   返回：PhotoCheckInDTO列表
   ```

9. 管理端删除照片打卡记录
   ```
   DELETE /api/manage/photo-check-in/delete/{photoCheckInId}
   ```

10. 管理端修改照片打卡分类
    ```
    PUT /api/manage/photo-check-in/update-category/{photoCheckInId}
    参数：category
    ```

### VR/AR打卡API

1. 小程序端获取所有AR内容
   ```
   GET /api/uniapp/ar-content/list
   返回：ArContentDTO列表
   ```

2. 小程序端根据目标ID和类型获取AR内容
   ```
   GET /api/uniapp/ar-content/target
   参数：targetId, targetType
   返回：ArContentDTO列表
   ```

3. 小程序端根据内容类型获取AR内容
   ```
   GET /api/uniapp/ar-content/content-type/{contentType}
   返回：ArContentDTO列表
   ```

4. 管理端上传AR内容
   ```
   POST /api/manage/ar-content/upload
   参数：file, title, description, contentType, targetId, targetType, latitude, longitude
   ```

5. 管理端获取所有AR内容
   ```
   GET /api/manage/ar-content/list
   返回：ArContentDTO列表
   ```

6. 管理端删除AR内容
   ```
   DELETE /api/manage/ar-content/delete/{arContentId}
   ```

7. 管理端修改AR内容
   ```
   PUT /api/manage/ar-content/update/{arContentId}
   参数：ArContentDTO JSON
   ```

2. 管理端创建或更新景点信息
   ```
   POST /api/manage/map/scenic-spots
   参数：ScenicSpotDTO JSON
   ```

3. 管理端删除景点信息
   ```
   DELETE /api/manage/map/scenic-spots/{id}
   ```

4. 管理端获取所有导览路线（包括已禁用的）
   ```
   GET /api/manage/map/guide-routes
   返回：GuideRouteDTO列表
   ```

5. 管理端创建或更新导览路线
   ```
   POST /api/manage/map/guide-routes
   参数：GuideRouteDTO JSON
   ```

6. 管理端删除导览路线
   ```
   DELETE /api/manage/map/guide-routes/{id}
   ```

7. 管理端创建或更新路线节点
   ```
   POST /api/manage/map/route-nodes
   参数：RouteNodeDTO JSON
   ```

8. 管理端删除路线节点
   ```
   DELETE /api/manage/map/route-nodes/{id}
   ```

### 保护区介绍API

1. 小程序端获取所有保护区介绍
   ```
   GET /api/uniapp/protected-area-introduction/list
   返回：ProtectedAreaIntroductionDTO列表
   ```

2. 小程序端根据语言获取保护区介绍
   ```
   GET /api/uniapp/protected-area-introduction/language/{language}
   返回：ProtectedAreaIntroductionDTO列表
   ```

3. 小程序端根据ID获取保护区介绍详情
   ```
   GET /api/uniapp/protected-area-introduction/detail/{id}
   返回：ProtectedAreaIntroductionDTO
   ```

4. 管理端新增保护区介绍
   ```
   POST /api/manage/protected-area-introduction/add
   参数：imageFile, audioFile, videoFile, title, content, language, sortOrder
   ```

5. 管理端获取所有保护区介绍
   ```
   GET /api/manage/protected-area-introduction/list
   返回：ProtectedAreaIntroductionDTO列表
   ```

6. 管理端根据语言获取保护区介绍
   ```
   GET /api/manage/protected-area-introduction/language/{language}
   返回：ProtectedAreaIntroductionDTO列表
   ```

7. 管理端更新保护区介绍
   ```
   PUT /api/manage/protected-area-introduction/update/{id}
   参数：ProtectedAreaIntroductionDTO JSON
   ```

8. 管理端删除保护区介绍
   ```
   DELETE /api/manage/protected-area-introduction/delete/{id}
   ```

### 活动列表API

1. 小程序端获取所有活动
   ```
   GET /api/uniapp/activity/list
   返回：ActivityDTO列表
   ```

2. 小程序端根据ID获取活动详情
   ```
   GET /api/uniapp/activity/detail/{id}
   返回：ActivityDTO
   ```

3. 管理端新增活动
   ```
   POST /api/manage/activity/add
   参数：imageFile, ActivityDTO JSON
   ```

4. 管理端获取所有活动
   ```
   GET /api/manage/activity/list
   返回：ActivityDTO列表
   ```

5. 管理端更新活动
   ```
   PUT /api/manage/activity/update/{id}
   参数：ActivityDTO JSON
   ```

6. 管理端删除活动
   ```
   DELETE /api/manage/activity/delete/{id}
   ```

### 运营管理API

1. 管理端分页查询操作员列表
   ```
   GET /api/manage/operators
   参数：page, size, username, realName, status
   返回：PageResult<OperatorDTO>
   ```

2. 管理端根据ID获取操作员详情
   ```
   GET /api/manage/operators/{id}
   返回：OperatorDTO
   ```

3. 管理端创建操作员
   ```
   POST /api/manage/operators
   参数：OperatorDTO JSON
   ```

4. 管理端更新操作员
   ```
   PUT /api/manage/operators/{id}
   参数：OperatorDTO JSON
   ```

5. 管理端删除操作员
   ```
   DELETE /api/manage/operators/{id}
   ```

6. 管理端启用/禁用操作员
   ```
   PUT /api/manage/operators/{id}/status
   参数：status
   ```

### 系统配置API

1. 管理端分页查询通知列表
   ```
   GET /api/manage/notifications
   参数：page, size, title, channel, sendStatus
   返回：PageResult<NotificationDTO>
   ```

2. 管理端根据ID获取通知详情
   ```
   GET /api/manage/notifications/{id}
   返回：NotificationDTO
   ```

3. 管理端创建通知
   ```
   POST /api/manage/notifications
   参数：NotificationDTO JSON
   ```

4. 管理端更新通知
   ```
   PUT /api/manage/notifications/{id}
   参数：NotificationDTO JSON
   ```

5. 管理端删除通知
   ```
   DELETE /api/manage/notifications/{id}
   ```

6. 管理端发送通知
   ```
   POST /api/manage/notifications/{id}/send
   ```

7. 管理端查询所有主题设置
   ```
   GET /api/manage/theme-settings
   返回：List<ThemeSettingDTO>
   ```

8. 管理端根据ID获取主题设置详情
   ```
   GET /api/manage/theme-settings/{id}
   返回：ThemeSettingDTO
   ```

9. 管理端获取默认主题设置
   ```
   GET /api/manage/theme-settings/default
   返回：ThemeSettingDTO
   ```

10. 管理端创建主题设置
    ```
    POST /api/manage/theme-settings
    参数：ThemeSettingDTO JSON
    ```

11. 管理端更新主题设置
    ```
    PUT /api/manage/theme-settings/{id}
    参数：ThemeSettingDTO JSON
    ```

12. 管理端删除主题设置
    ```
    DELETE /api/manage/theme-settings/{id}
    ```

13. 管理端设置默认主题
    ```
    POST /api/manage/theme-settings/{id}/default
    ```

## 配置说明

在`application.yml`中可以配置不同端的API前缀和安全参数：

```yaml
# 小程序端配置
miniapp:
  api:
    prefix: /api/uniapp
  security:
    jwt:
      secret: miniapp_secret_key
      expiration: 86400000 # 24小时

# 管理后台端配置
admin:
  api:
    prefix: /api/manage
  security:
    jwt:
      secret: admin_secret_key
      expiration: 7200000 # 2小时
```

## 开发规范

1. 所有API接口均以`/api`开头
2. 小程序端接口使用`/uniapp`作为二级路径
3. 管理后台端接口使用`/manage`作为二级路径
4. 使用统一的响应格式：
   ```json
   {
     "code": 200,
     "message": "操作成功",
     "data": {}
   }
   ```

## 项目结构

```
src/main/java/com/scenic
├── Application.java              # 启动类
├── config/                      # 配置类
│   ├── DataSourceConfig.java    # 数据源配置
│   ├── RedisConfig.java         # Redis配置
│   ├── SecurityConfig.java      # 安全配置
│   └── WebConfig.java           # Web配置
├── common/                      # 公共模块
│   ├── constant/                # 常量定义
│   │   ├── AppointmentConstants.java
│   │   └── SystemConstants.java
│   └── dto/                     # 数据传输对象
│       ├── PageResult.java      # 分页结果
│       └── Result.java          # 统一返回结果
├── controller/                  # 控制器
│   ├── appointment/             # 预约控制器
│   │   ├── ActivityAppointmentController.java
│   │   ├── AppointmentController.java
│   │   ├── AppointmentSettingController.java
│   │   ├── AppointmentStatisticsController.java
│   │   └── TeamAppointmentController.java
│   ├── content/                 # 内容控制器
│   │   ├── ActivityController.java
│   │   └── ProtectedAreaIntroductionController.java
│   ├── interaction/             # 互动控制器
│   │   ├── ArContentController.java
│   │   └── PhotoCheckInController.java
│   ├── map/                     # 地图控制器
│   │   └── MapController.java
│   ├── operation/               # 运营管理控制器
│   │   └── OperatorController.java
│   ├── intelligence/            # 智能化控制器
│   │   ├── KnowledgeBaseController.java
│   │   ├── KnowledgeGraphController.java
│   │   └── AIQARecordController.java
│   └── user/                    # 用户控制器
│       └── UserController.java
│   └── system/                  # 系统配置控制器
│       ├── NotificationController.java
│       └── ThemeSettingController.java
├── dto/                         # 数据传输对象
│   ├── appointment/             # 预约DTO
│   │   ├── ActivityAppointmentDTO.java
│   │   ├── AppointmentResponseDTO.java
│   │   ├── PersonalAppointmentDTO.java
│   │   └── TeamAppointmentDTO.java
│   ├── content/                 # 内容DTO
│   │   ├── ActivityDTO.java
│   │   └── ProtectedAreaIntroductionDTO.java
│   ├── interaction/             # 互动DTO
│   │   ├── ArContentDTO.java
│   │   └── PhotoCheckInDTO.java
│   ├── map/                     # 地图DTO
│   │   ├── GuideRouteDTO.java
│   │   ├── RouteNodeDTO.java
│   │   └── ScenicSpotDTO.java
│   ├── intelligence/            # 智能化DTO
│   │   ├── KnowledgeBaseDTO.java
│   │   ├── KnowledgeGraphDTO.java
│   │   └── AIQARecordDTO.java
│   └── operation/               # 运营管理DTO
│       └── OperatorDTO.java
│   └── system/                  # 系统配置DTO
│       ├── NotificationDTO.java
│       └── ThemeSettingDTO.java
├── entity/                      # 实体类
│   ├── appointment/             # 预约实体
│   │   ├── ActivityAppointment.java
│   │   ├── Appointment.java
│   │   ├── AppointmentPerson.java
│   │   ├── AppointmentSetting.java
│   │   ├── TeamAppointment.java
│   │   └── TeamMember.java
│   ├── content/                 # 内容实体
│   │   ├── Activity.java
│   │   └── ProtectedAreaIntroduction.java
│   ├── interaction/             # 互动实体
│   │   ├── ArContent.java
│   │   └── PhotoCheckIn.java
│   ├── map/                     # 地图实体
│   │   ├── GuideRoute.java
│   │   ├── RouteNode.java
│   │   └── ScenicSpot.java
│   ├── intelligence/            # 智能化实体
│   │   ├── KnowledgeBase.java
│   │   ├── KnowledgeGraph.java
│   │   └── AIQARecord.java
│   ├── operation/               # 运营管理实体
│   │   └── Operator.java
│   └── user/                    # 用户实体
│       ├── User.java
│       ├── UserAppointmentPerson.java
│       ├── UserMessage.java
│       └── UserPhoto.java
│   └── system/                  # 系统配置实体
│       ├── Notification.java
│       └── ThemeSetting.java
├── mapper/                      # 数据访问层
│   ├── UserMapper.java          # 用户Mapper接口
│   ├── UserAppointmentPersonMapper.java  # 用户预约人Mapper接口
│   ├── UserMessageMapper.java   # 用户消息Mapper接口
│   ├── UserPhotoMapper.java     # 用户照片Mapper接口
│   ├── intelligence/            # 智能化Mapper接口
│   │   ├── KnowledgeBaseMapper.java
│   │   ├── KnowledgeGraphMapper.java
│   │   └── AIQARecordMapper.java
│   └── operation/               # 运营管理Mapper接口
│       └── OperatorMapper.java
│   └── system/                  # 系统配置Mapper接口
│       ├── NotificationMapper.java
│       └── ThemeSettingMapper.java
├── service/                     # 业务逻辑层
│   ├── appointment/             # 预约服务
│   │   ├── AppointmentService.java
│   │   ├── AppointmentSettingService.java
│   │   ├── AppointmentStatisticsService.java
│   │   └── impl/                # 预约服务实现
│   │       ├── AppointmentServiceImpl.java
│   │       ├── AppointmentSettingServiceImpl.java
│   │       └── AppointmentStatisticsServiceImpl.java
│   ├── content/                 # 内容服务
│   │   ├── ActivityService.java
│   │   ├── ProtectedAreaIntroductionService.java
│   │   └── impl/                # 内容服务实现
│   │       ├── ActivityServiceImpl.java
│   │       └── ProtectedAreaIntroductionServiceImpl.java
│   ├── interaction/             # 互动服务
│   │   ├── ArContentService.java
│   │   ├── PhotoCheckInService.java
│   │   └── impl/                # 互动服务实现
│   │       ├── ArContentServiceImpl.java
│   │       └── PhotoCheckInServiceImpl.java
│   ├── map/                     # 地图服务
│   │   ├── MapService.java
│   │   └── impl/                # 地图服务实现
│   │       └── MapServiceImpl.java
│   ├── intelligence/            # 智能化服务
│   │   ├── KnowledgeBaseService.java
│   │   ├── KnowledgeGraphService.java
│   │   ├── AIQARecordService.java
│   │   └── impl/                # 智能化服务实现
│   │       ├── KnowledgeBaseServiceImpl.java
│   │       ├── KnowledgeGraphServiceImpl.java
│   │       └── AIQARecordServiceImpl.java
│   ├── operation/               # 运营管理服务
│   │   ├── OperatorService.java
│   │   └── impl/                # 运营管理服务实现
│   │       └── OperatorServiceImpl.java
│   └── user/                    # 用户服务
│       ├── UserService.java
│       └── impl/                # 用户服务实现
│           └── UserServiceImpl.java
│   └── system/                  # 系统配置服务
│       ├── NotificationService.java
│       ├── ThemeSettingService.java
│       └── impl/                # 系统配置服务实现
│           ├── NotificationServiceImpl.java
│           └── ThemeSettingServiceImpl.java
└── utils/                       # 工具类
```

## 注意事项

1. 小程序端和管理后台端使用同一套数据库，通过不同的API接口和权限控制来区分访问
2. 安全认证采用JWT Token机制，不同端使用不同的密钥和过期时间
3. 所有接口均需进行参数校验和异常处理
4. 建议使用HTTPS协议保障数据传输安全
5. 个人预约默认状态为"已通过"，无需审核；团队预约和活动预约需要管理员审核
6. 项目使用MyBatis作为ORM框架，通过Mapper接口访问数据库

## 智能化模块API

### 知识库管理API

1. 小程序端获取所有启用的知识库
   ```
   GET /api/uniapp/intelligence/knowledge-base/enabled
   返回：List<KnowledgeBaseDTO>
   ```

2. 小程序端根据ID获取知识库详情
   ```
   GET /api/uniapp/intelligence/knowledge-base/{id}
   返回：KnowledgeBaseDTO
   ```

3. 管理端分页查询知识库列表
   ```
   GET /api/manage/intelligence/knowledge-base
   参数：page, size, title, version, status
   返回：PageResult<KnowledgeBaseDTO>
   ```

4. 管理端根据ID获取知识库详情
   ```
   GET /api/manage/intelligence/knowledge-base/{id}
   返回：KnowledgeBaseDTO
   ```

5. 管理端创建知识库
   ```
   POST /api/manage/intelligence/knowledge-base
   参数：KnowledgeBaseDTO JSON
   ```

6. 管理端更新知识库
   ```
   PUT /api/manage/intelligence/knowledge-base/{id}
   参数：KnowledgeBaseDTO JSON
   ```

7. 管理端删除知识库
   ```
   DELETE /api/manage/intelligence/knowledge-base/{id}
   ```

8. 管理端上传知识库文件
   ```
   POST /api/manage/intelligence/knowledge-base/upload
   参数：file, title
   ```

### 知识图谱管理API

1. 小程序端获取所有启用的知识图谱
   ```
   GET /api/uniapp/intelligence/knowledge-graph/enabled
   返回：List<KnowledgeGraphDTO>
   ```

2. 小程序端根据物种名称获取知识图谱
   ```
   GET /api/uniapp/intelligence/knowledge-graph/species/{speciesName}
   返回：KnowledgeGraphDTO
   ```

3. 小程序端根据分类获取知识图谱列表
   ```
   GET /api/uniapp/intelligence/knowledge-graph/category/{category}
   返回：List<KnowledgeGraphDTO>
   ```

4. 管理端分页查询知识图谱列表
   ```
   GET /api/manage/intelligence/knowledge-graph
   参数：page, size, speciesName, category, status
   返回：PageResult<KnowledgeGraphDTO>
   ```

5. 管理端根据ID获取知识图谱详情
   ```
   GET /api/manage/intelligence/knowledge-graph/{id}
   返回：KnowledgeGraphDTO
   ```

6. 管理端创建知识图谱
   ```
   POST /api/manage/intelligence/knowledge-graph
   参数：KnowledgeGraphDTO JSON
   ```

7. 管理端更新知识图谱
   ```
   PUT /api/manage/intelligence/knowledge-graph/{id}
   参数：KnowledgeGraphDTO JSON
   ```

8. 管理端删除知识图谱
   ```
   DELETE /api/manage/intelligence/knowledge-graph/{id}
   ```

9. 管理端上传知识图谱图片
   ```
   POST /api/manage/intelligence/knowledge-graph/upload-image
   参数：file, speciesName
   ```

### AI问答服务API

1. 小程序端进行AI问答
   ```
   POST /api/uniapp/intelligence/ai-qa/ask
   参数：userId, question
   返回：AIQARecordDTO
   ```

2. 管理端分页查询问答记录列表
   ```
   GET /api/manage/intelligence/ai-qa
   参数：page, size, userId, status
   返回：PageResult<AIQARecordDTO>
   ```

3. 管理端根据ID获取问答记录详情
   ```
   GET /api/manage/intelligence/ai-qa/{id}
   返回：AIQARecordDTO
   ```

4. 管理端根据用户ID获取问答记录列表
   ```
   GET /api/manage/intelligence/ai-qa/user/{userId}
   参数：page, size
   返回：PageResult<AIQARecordDTO>
   ```
=======
# zhijia-birdReserve-backend



## Getting started

To make it easy for you to get started with GitLab, here's a list of recommended next steps.

Already a pro? Just edit this README.md and make it your own. Want to make it easy? [Use the template at the bottom](#editing-this-readme)!

## Add your files

- [ ] [Create](https://docs.gitlab.com/ee/user/project/repository/web_editor.html#create-a-file) or [upload](https://docs.gitlab.com/ee/user/project/repository/web_editor.html#upload-a-file) files
- [ ] [Add files using the command line](https://docs.gitlab.com/topics/git/add_files/#add-files-to-a-git-repository) or push an existing Git repository with the following command:

```
cd existing_repo
git remote add origin http://192.168.77.2:30080/yuzhou.lin/zhijia-birdreserve-backend.git
git branch -M main
git push -uf origin main
```

## Integrate with your tools

- [ ] [Set up project integrations](http://192.168.77.2:30080/yuzhou.lin/zhijia-birdreserve-backend/-/settings/integrations)

## Collaborate with your team

- [ ] [Invite team members and collaborators](https://docs.gitlab.com/ee/user/project/members/)
- [ ] [Create a new merge request](https://docs.gitlab.com/ee/user/project/merge_requests/creating_merge_requests.html)
- [ ] [Automatically close issues from merge requests](https://docs.gitlab.com/ee/user/project/issues/managing_issues.html#closing-issues-automatically)
- [ ] [Enable merge request approvals](https://docs.gitlab.com/ee/user/project/merge_requests/approvals/)
- [ ] [Set auto-merge](https://docs.gitlab.com/user/project/merge_requests/auto_merge/)

## Test and Deploy

Use the built-in continuous integration in GitLab.

- [ ] [Get started with GitLab CI/CD](https://docs.gitlab.com/ee/ci/quick_start/)
- [ ] [Analyze your code for known vulnerabilities with Static Application Security Testing (SAST)](https://docs.gitlab.com/ee/user/application_security/sast/)
- [ ] [Deploy to Kubernetes, Amazon EC2, or Amazon ECS using Auto Deploy](https://docs.gitlab.com/ee/topics/autodevops/requirements.html)
- [ ] [Use pull-based deployments for improved Kubernetes management](https://docs.gitlab.com/ee/user/clusters/agent/)
- [ ] [Set up protected environments](https://docs.gitlab.com/ee/ci/environments/protected_environments.html)

***

# Editing this README

When you're ready to make this README your own, just edit this file and use the handy template below (or feel free to structure it however you want - this is just a starting point!). Thanks to [makeareadme.com](https://www.makeareadme.com/) for this template.

## Suggestions for a good README

Every project is different, so consider which of these sections apply to yours. The sections used in the template are suggestions for most open source projects. Also keep in mind that while a README can be too long and detailed, too long is better than too short. If you think your README is too long, consider utilizing another form of documentation rather than cutting out information.

## Name
Choose a self-explaining name for your project.

## Description
Let people know what your project can do specifically. Provide context and add a link to any reference visitors might be unfamiliar with. A list of Features or a Background subsection can also be added here. If there are alternatives to your project, this is a good place to list differentiating factors.

## Badges
On some READMEs, you may see small images that convey metadata, such as whether or not all the tests are passing for the project. You can use Shields to add some to your README. Many services also have instructions for adding a badge.

## Visuals
Depending on what you are making, it can be a good idea to include screenshots or even a video (you'll frequently see GIFs rather than actual videos). Tools like ttygif can help, but check out Asciinema for a more sophisticated method.

## Installation
Within a particular ecosystem, there may be a common way of installing things, such as using Yarn, NuGet, or Homebrew. However, consider the possibility that whoever is reading your README is a novice and would like more guidance. Listing specific steps helps remove ambiguity and gets people to using your project as quickly as possible. If it only runs in a specific context like a particular programming language version or operating system or has dependencies that have to be installed manually, also add a Requirements subsection.

## Usage
Use examples liberally, and show the expected output if you can. It's helpful to have inline the smallest example of usage that you can demonstrate, while providing links to more sophisticated examples if they are too long to reasonably include in the README.

## Support
Tell people where they can go to for help. It can be any combination of an issue tracker, a chat room, an email address, etc.

## Roadmap
If you have ideas for releases in the future, it is a good idea to list them in the README.

## Contributing
State if you are open to contributions and what your requirements are for accepting them.

For people who want to make changes to your project, it's helpful to have some documentation on how to get started. Perhaps there is a script that they should run or some environment variables that they need to set. Make these steps explicit. These instructions could also be useful to your future self.

You can also document commands to lint the code or run tests. These steps help to ensure high code quality and reduce the likelihood that the changes inadvertently break something. Having instructions for running tests is especially helpful if it requires external setup, such as starting a Selenium server for testing in a browser.

## Authors and acknowledgment
Show your appreciation to those who have contributed to the project.

## License
For open source projects, say how it is licensed.

## Project status
If you have run out of energy or time for your project, put a note at the top of the README saying that development has slowed down or stopped completely. Someone may choose to fork your project or volunteer to step in as a maintainer or owner, allowing your project to keep going. You can also make an explicit request for maintainers.
>>>>>>> 0d06d8857802fbeb2a56466894ac608a7990e37b
