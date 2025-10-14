# 智佳鸟类保护区管理系统

## 项目概述

本项目是一个基于Spring Boot的鸟类保护区管理系统，同时支持微信小程序端和管理后台端的访问。通过统一的后端API服务，为不同端提供相应的功能接口。系统主要用于管理鸟类保护区的各种资源和活动，包括保护区大事件、物种信息、活动管理、用户互动等功能。

## 项目仓库

```
# 添加远程仓库
git remote add origin http://192.168.77.2:30080/yuzhou.lin/zhijia-birdreserve-backend.git

# 或使用SSH方式
git remote add origin ssh://git@192.168.77.2:30022/yuzhou.lin/zhijia-birdreserve-backend.git

# 推送代码
git push -u origin master
```

## 功能模块说明

### 1. 保护区大事件模块

保护区大事件模块用于记录和展示保护区内发生的重要事件，如小天鹅迁徙、繁殖等自然现象或保护区内的重要活动。

#### 主要功能：
- **保护区大事件列表**：展示保护区大事件列表，支持分页、排序，可按年份、月份、物种等条件筛选
- **保护区大事件搜索**：支持按标题、内容、物种等关键词搜索
- **保护区大事件详情**：展示大事件的详细信息，包括标题、日期、内容、图片、视频等
- **精彩瞬间**：展示大事件的精彩图片和视频
- **相关讲解**：提供大事件的专业讲解内容
- **评论功能**：用户可对大事件发表评论，支持评论回复、点赞等互动功能

#### 数据库表：
- `protected_area_event`：保护区大事件表
- `protected_area_event_image`：保护区大事件图片表
- `protected_area_species`：保护区物种表
- `protected_area_species_image`：保护区物种图片表
- `user_favorite`：用户收藏表
- `user_comment`：用户评论表
- `user_like`：用户点赞表

### 2. 活动管理模块

活动管理模块用于管理保护区内的各种活动，包括活动发布、预约、评价等功能。

#### 主要功能：
- **活动列表**：展示活动列表，支持分页、排序，可按类别、时间等条件筛选
- **活动详情**：展示活动的详细信息，包括标题、时间、地点、内容等
- **活动预约**：用户可预约参加活动，支持个人预约和团队预约
- **活动评价**：用户可对参加过的活动进行评价
- **活动通知**：系统可向预约用户发送活动相关通知

#### 数据库表：
- `activity`：活动表
- `activity_category`：活动类别表
- `activity_category_relation`：活动-类别关联表
- `activity_registration_template`：活动预约表格模板表
- `activity_registration`：活动预约表
- `activity_review`：活动评价表
- `activity_notification`：活动通知表
- `activity_user_notification`：活动用户通知关联表
- `activity_registration_setting`：活动预约设置表

### 3. 预约管理模块

预约管理模块是本系统的核心功能模块，实现了完整的个人预约和团队预约流程。

#### 小程序端预约流程：
1. 用户选择预约日期和入园时间
2. 用户添加预约人信息
3. 提交预约请求
4. 系统校验人数上限、证件号/电话合法性
5. 成功写入数据库并返回预约编号
6. 弹窗提示"预约成功"，显示预约详情
7. 超限或错误时提示失败原因

#### 小程序端团队预约流程：
1. 用户下载团队预约表格模板
2. 填写团队预约信息并上传表格
3. 系统解析文件，校验字段并写入数据库
4. 返回成功状态，页面提示"提交成功"
5. 团队预约进入待审核状态

#### 管理端预约管理：
1. 管理员可通过姓名、电话、时间等条件搜索预约记录
2. 查看预约详情
3. 审核预约（通过/拒绝）- 仅适用于需要审核的预约类型
4. 删除预约记录
5. 编辑预约信息

### 4. 拍照打卡模块

拍照打卡模块为用户提供拍照打卡和分享功能。

#### 小程序端拍照打卡功能：
1. 用户打开拍照打卡页面
2. 用户上传照片并发布
3. 图片存储成功，生成数据库记录
4. 上传后显示在前端列表，可预览

#### 小程序端拍照分享功能：
1. 用户进入分享区
2. 用户按分类搜索照片
3. 搜索接口返回匹配结果
4. 用户可对照片进行点赞操作
5. 点赞操作写入数据库并同步
6. 可正确搜索和点赞图片，点赞数实时更新

#### 管理端拍照打卡管理功能：
1. 管理员查看用户上传的图片
2. 管理员可对图片进行分类管理
3. 图片数据同步数据库
4. 删除后小程序端更新
5. 后台能看到用户上传的图片，可修改分类或删除

### 5. 知识库管理模块

知识库管理模块用于管理保护区相关的知识内容，包括知识库、知识文档、知识图谱等。

#### 主要功能：
- **知识库管理**：创建、编辑、删除知识库
- **知识文档管理**：上传、编辑、删除知识文档
- **知识图谱管理**：创建、编辑、删除知识图谱
- **AI问答服务**：基于知识库提供智能问答服务

#### 数据库表：
- `knowledge_base`：知识库表
- `knowledge_document`：知识文档表
- `knowledge_document_attachment`：知识文档附件表
- `knowledge_graph`：知识图谱表
- `knowledge_graph_node`：知识图谱节点表
- `knowledge_graph_relation`：知识图谱关系表
- `ai_qa_record`：AI问答记录表

### 6. 用户管理模块

用户管理模块用于管理系统用户，包括用户注册、登录、个人信息管理等功能。

#### 主要功能：
- **用户注册**：用户通过微信小程序注册
- **用户登录**：用户通过微信小程序登录
- **个人信息管理**：用户可管理个人信息，包括头像、昵称、手机号等
- **预约人管理**：用户可管理预约人信息，包括姓名、证件号、手机号等
- **消息管理**：用户可查看系统消息

## 架构设计

### 整体架构
```
智佳鸟类保护区管理系统
├── 微信小程序端 (Miniapp)
├── 管理后台端 (Admin)
└── 共享服务层
    ├── 保护区大事件模块
    ├── 活动管理模块
    ├── 预约管理模块
    ├── 拍照打卡模块
    ├── 知识库管理模块
    ├── 用户管理模块
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

## 技术栈

- 后端框架：Spring Boot 2.x
- 数据库：MySQL 8.x
- 安全认证：JWT + Redis
- 缓存：Redis
- 文件存储：MinIO
- ORM框架：MyBatis

## Redis缓存策略

本系统采用Redis作为缓存层，以提升系统性能和减轻数据库压力。主要缓存策略如下：

### 缓存设计原则
1. **读缓存**：优先从Redis中读取数据，缓存未命中时查询数据库并写入缓存
2. **写穿透**：数据更新时同时更新数据库和缓存
3. **缓存失效**：数据删除或更新时主动清除相关缓存
4. **过期策略**：设置合理的缓存过期时间，避免数据不一致

### 微信小程序后端Redis使用场景

1. **微信用户认证与会话管理**：
   - 存储用户的微信登录态信息（session_key, openid）
   - 生成自定义登录态标识（3rd_session）并与微信登录态关联

2. **微信接口调用凭证管理**：
   - 缓存小程序全局接口调用凭证（access_token）
   - 自动处理过期刷新

3. **预约系统功能**：
   - 使用Redis原子计数器跟踪各景点/活动剩余名额
   - 实现高并发下的预约抢票功能
   - 缓存用户最近的预约状态

4. **内容缓存**：
   - 缓存小程序首页展示的热门景点、活动等信息
   - 缓存保护区大事件、物种信息等不常变化的内容

5. **统计与计数功能**：
   - 记录小程序页面访问量、用户活跃度等数据
   - 使用Redis存储用户行为日志，用于后续分析

6. **消息通知**：
   - 记录已发送的模板消息，避免重复发送
   - 存储用户订阅的消息类型

7. **限流与安全控制**：
   - 基于用户openid的接口调用频率控制
   - 记录用户操作，防止恶意刷单、刷票

### 缓存键设计
- 活动缓存：`activity:{id}`、`all_activities`
- 保护区大事件缓存：`event:{id}`、`all_events`、`events_year:{year}`、`events_month:{year}_{month}`
- 物种缓存：`species:{id}`、`all_species`、`species_category:{category}`
- 用户缓存：`user:{id}`、`user_openid:{openid}`
- 预约缓存：`appointment:{id}`、`user_appointments:{userId}`
- 微信会话缓存：`wx:session:{3rd_session}`、`wx:user:{openid}`、`wx:access_token`

## 文件存储策略

本系统使用MinIO作为文件存储服务，主要存储以下类型的文件：

1. **图片文件**：
   - 保护区大事件图片
   - 物种图片
   - 活动图片
   - 用户上传的打卡图片

2. **文档文件**：
   - 知识库文档
   - 活动预约表格模板
   - 用户上传的团队预约表格

3. **音频文件**：
   - 物种讲解音频
   - 保护区大事件讲解音频

4. **视频文件**：
   - 保护区大事件视频
   - 活动宣传视频

### 文件存储结构

```
minio/
├── images/
│   ├── events/          # 保护区大事件图片
│   ├── species/         # 物种图片
│   ├── activities/      # 活动图片
│   ├── checkins/        # 用户打卡图片
│   └── avatars/         # 用户头像
├── documents/
│   ├── knowledge/       # 知识库文档
│   ├── templates/       # 预约表格模板
│   └── registrations/   # 用户上传的预约表格
├── audios/
│   ├── explanations/    # 讲解音频
│   └── others/          # 其他音频
└── videos/
    ├── events/          # 保护区大事件视频
    └── activities/      # 活动宣传视频
```

### MinIO配置

本系统使用MinIO作为对象存储服务，提供高性能、可扩展的文件存储解决方案。

#### MinIO服务配置

MinIO服务通过Docker容器运行，配置如下：

```bash
# 启动MinIO容器
docker run -d \
  -p 10029:9000 \
  -p 10030:9001 \
  --name minio-v3 \
  -v /data/minio-v3:/data \
  -e "MINIO_ROOT_USER=admin" \
  -e "MINIO_ROOT_PASSWORD=zssg0325" \
  minio/minio server /data --console-address ":9001"
```

- API端点端口：10029
- 控制台端口：10030
- 数据存储路径：/data/minio-v3
- 访问凭证：admin/zssg0325

#### 项目集成配置

在`application.yml`中配置MinIO连接信息：

```yaml
# MinIO配置
minio:
  endpoint: http://192.168.77.2:10029
  access-key: admin
  secret-key: zssg0325
  bucket-name: scenic-bucket
```

#### 核心组件

1. **MinioConfig**：配置类，负责创建MinioClient实例
2. **MinioService**：服务类，提供文件上传、删除和URL生成等功能
3. **FileUploadUtil**：工具类，封装文件上传逻辑，使用MinIO存储文件

#### 文件上传流程

1. 客户端上传文件到服务器
2. 服务器接收文件并生成唯一文件名
3. 调用MinioService将文件上传到MinIO服务器
4. 返回文件的访问URL给客户端
5. 客户端通过URL访问文件

#### 存储桶管理

系统使用名为"scenic-bucket"的存储桶存储所有文件，并按照上述文件存储结构组织文件。在系统初始化时，会自动检查存储桶是否存在，不存在则创建。

#### 文件访问URL

MinIO生成的文件访问URL包含签名和过期时间，默认有效期为7天。URL格式如下：

```
http://192.168.77.2:10029/scenic-bucket/images/events/1234567890.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=...
```

## 部署说明

1. 确保已安装Java 17和MySQL 8.0+
2. 创建数据库：`birdReserveDatabase`
3. 修改`application.yml`中的数据库连接配置
4. 启动MinIO服务：
   ```bash
   docker run -d \
     -p 10029:9000 \
     -p 10030:9001 \
     --name minio-v3 \
     -v /data/minio-v3:/data \
     -e "MINIO_ROOT_USER=admin" \
     -e "MINIO_ROOT_PASSWORD=zssg0325" \
     minio/minio server /data --console-address ":9001"
   ```
5. 访问MinIO控制台（http://192.168.77.2:10030）并创建存储桶：`scenic-bucket`
6. 运行项目：
   ```bash
   mvn spring-boot:run
   ```
7. 访问应用地址：`http://localhost:10032`

## 技术环境

- Java版本：17
- 数据库名称：birdReserveDatabase
- 数据库地址：192.168.77.2:10227

## 容器端口配置

本项目运行在以下端口：
- 应用服务端口：10032
- MySQL数据库端口：10227
- Redis端口：10228
- MinIO API端点端口：10029
- MinIO控制台端口：10030

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

## 项目结构

```
src/main/java/com/scenic
├── Application.java              # 启动类
├── config/                      # 配置类
│   ├── DataSourceConfig.java    # 数据源配置
│   ├── MinioConfig.java         # MinIO配置
│   ├── RedisConfig.java         # Redis配置
│   ├── SecurityConfig.java      # 安全配置
│   └── WebConfig.java           # Web配置
├── common/                      # 公共模块
│   ├── constant/                # 常量定义
│   └── dto/                     # 数据传输对象
├── controller/                  # 控制器
│   ├── appointment/             # 预约控制器
│   ├── content/                 # 内容控制器
│   ├── intelligence/            # 智能化控制器
│   ├── interaction/             # 互动控制器
│   ├── map/                     # 地图控制器
│   ├── operation/               # 运营管理控制器
│   ├── system/                  # 系统配置控制器
│   └── user/                    # 用户控制器
├── dto/                         # 数据传输对象
├── entity/                      # 实体类
│   ├── appointment/             # 预约实体
│   ├── content/                 # 内容实体
│   ├── intelligence/            # 智能化实体
│   ├── interaction/             # 互动实体
│   ├── map/                     # 地图实体
│   ├── operation/               # 运营管理实体
│   ├── system/                  # 系统配置实体
│   └── user/                    # 用户实体
├── mapper/                      # 数据访问层
├── service/                     # 业务逻辑层
│   ├── MinioService.java        # MinIO服务
│   ├── appointment/             # 预约服务
│   ├── content/                 # 内容服务
│   ├── intelligence/            # 智能化服务
│   ├── interaction/             # 互动服务
│   ├── map/                     # 地图服务
│   ├── operation/               # 运营管理服务
│   ├── system/                  # 系统配置服务
│   └── user/                    # 用户服务
└── utils/                       # 工具类
```

## 安全认证机制

本系统采用 JWT (JSON Web Token) + Redis 的组合方式实现安全认证，具有高效、安全、可扩展的特点。

### JWT + Redis 认证流程

1. **用户登录**：
   - 用户提交用户名/密码（管理后台）或微信登录凭证（小程序）
   - 服务器验证身份信息
   - 生成包含用户ID、角色等信息的JWT令牌
   - 将令牌存储在Redis中，键为用户ID，值为令牌，设置过期时间
   - 返回JWT令牌给客户端

2. **请求认证**：
   - 客户端在请求头中携带JWT令牌（Authorization: Bearer {token}）
   - 服务器从请求头中提取令牌
   - 验证令牌的有效性（签名、过期时间）
   - 从Redis中获取存储的令牌，与请求中的令牌比对
   - 验证通过后处理请求，否则返回401未授权错误

3. **令牌刷新**：
   - 客户端可在令牌即将过期时请求刷新
   - 服务器验证旧令牌，生成新令牌
   - 更新Redis中存储的令牌和过期时间
   - 返回新令牌给客户端

4. **注销登录**：
   - 客户端请求注销
   - 服务器从Redis中删除对应的令牌
   - 客户端删除本地存储的令牌

### 安全优势

1. **无状态认证**：JWT令牌包含用户信息，减少数据库查询
2. **防止令牌被盗用**：Redis存储有效令牌，可随时撤销特定用户的访问权限
3. **双重过期机制**：JWT自带过期时间 + Redis键过期时间
4. **不同端独立认证**：小程序端和管理后台端使用不同的密钥和过期时间
5. **集中式会话管理**：可实现强制下线、限制登录设备数等功能

## 注意事项

1. 小程序端和管理后台端使用同一套数据库，通过不同的API接口和权限控制来区分访问
2. 安全认证采用JWT + Redis机制，不同端使用不同的密钥和过期时间
3. 所有接口均需进行参数校验和异常处理
4. 建议使用HTTPS协议保障数据传输安全
5. 个人预约默认状态为"已通过"，无需审核；团队预约和活动预约需要管理员审核
6. 项目使用MyBatis作为ORM框架，通过Mapper接口访问数据库
7. 文件存储使用MinIO对象存储服务，数据库仅存储文件引用，提高系统性能
8. MinIO服务需要在应用启动前确保正常运行，并创建好相应的存储桶
9. 文件上传前会自动检查存储桶是否存在，不存在则创建，但建议提前手动创建以避免权限问题
10. MinIO生成的文件访问URL默认有效期为7天，如需调整可修改MinioService中的配置
