# 个人预约创建接口文档 (createReservation)

## 1. 接口概述

个人预约创建接口用于创建新的个人预约记录，支持预约主信息和预约人员信息的批量创建。该接口具备防重复预约、数据完整性保障、事务一致性等特性。

### 1.1 接口功能
- 创建个人预约主记录
- 批量创建预约人员信息
- 防重复预约检查
- 预约编号自动生成
- 数据完整性验证

### 1.2 适用场景
- 用户通过管理后台创建个人预约
- 管理员代用户创建预约
- 系统批量预约导入

## 2. 请求说明

### 2.1 请求URL
```
POST /api/manage/individual-reservations
```

### 2.2 请求方法
```
POST
```

### 2.3 请求头
```
Content-Type: application/json
Authorization: Bearer <token> (可选，根据权限配置)
```

### 2.4 请求参数

#### 2.4.1 查询参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| createBy | Long | 否 | 创建人ID，用于指定创建者 |

#### 2.4.2 请求体参数 (IndividualReservation对象)
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 否 | 预约ID（创建时通常为空） |
| reservationNo | String | 否 | 预约编号（系统自动生成） |
| userId | Long | 是 | 用户ID |
| scenicId | Long | 是 | 景区ID |
| visitDate | Date | 是 | 入区日期 |
| timeSlot | Integer | 是 | 时间段 |
| adultCount | Integer | 否 | 成人数量 |
| childCount | Integer | 否 | 未成年人数量 |
| totalCount | Integer | 否 | 总人数 |
| status | Integer | 否 | 状态（0-未开始，1-已取消，10-已核销，11-已过期） |
| contactName | String | 是 | 主联系人姓名 |
| contactIdType | Integer | 是 | 主联系人证件类型 |
| contactIdNumber | String | 是 | 主联系人证件号码 |
| contactPhone | String | 是 | 主联系人联系电话 |
| reservationPersons | List<IndividualReservationPerson> | 是 | 预约人员列表 |

#### 2.4.3 预约人员对象 (IndividualReservationPerson)
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 否 | 人员ID |
| name | String | 是 | 姓名 |
| idType | Integer | 是 | 证件类型（1-身份证，2-护照，3-其他） |
| idNumber | String | 是 | 证件号码 |
| phone | String | 否 | 手机号 |
| personType | Integer | 否 | 人员类型（1-成人，2-未成年人） |
| isContact | Integer | 否 | 是否为联系人（0-否，1-是） |

## 3. 响应说明

### 3.1 响应格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "预约编号"
}
```

### 3.2 响应参数
| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应码 |
| message | String | 响应消息 |
| data | String | 预约编号 |

### 3.3 响应码说明
| 响应码 | 说明 |
|--------|------|
| 200 | 创建成功 |
| 409 | 重复预约 |
| 500 | 系统异常 |
| 503 | 数据库异常/该订单中已经有人预约 |

### 3.4 响应示例

#### 3.4.1 成功响应
```json
{
  "code": 200,
  "message": "预约创建成功",
  "data": "RES202510201530451234"
}
```

#### 3.4.2 重复预约异常
```json
{
  "code": 409,
  "message": "操作过于频繁，请稍后再试",
  "data": null
}
```

#### 3.4.3 系统异常
```json
{
  "code": 500,
  "message": "预约创建异常：数据库连接失败",
  "data": null
}
```

#### 3.4.3 数据库异常

```json
{
  "code": 503,
  "message": "该预约订单中已经有人预约过了",
  "data": null
}
```



## 4. 业务逻辑详解

### 4.1 防重复预约检查

#### 4.1.1 Redis防重机制
1. **生成Redis键**: 使用`visitDate`、`timeSlot`、`contactIdType`、`contactIdNumber`、`userId`生成唯一键
2. **设置分布式锁**: 使用`setIfAbsent`方法设置30秒超时锁
3. **重复检测**: 如果键已存在，抛出`DuplicateReservationException`异常

#### 4.1.2 数据库防重机制（降级处理）
当Redis不可用时，直接查询数据库：
```sql
SELECT ir.* FROM individual_reservation ir 
JOIN individual_reservation_person irp ON ir.id = irp.reservation_id 
WHERE ir.visit_date = #{visitDate} 
AND ir.time_slot = #{timeSlot} 
AND irp.id_type = #{idType} 
AND irp.id_number = #{idNumber} 
AND ir.deleted = 0 
AND irp.deleted = 0 
AND irp.is_contact = 1 
AND ir.status != 1
```

### 4.2 预约编号生成
- 格式：`RES` + `yyyyMMddHHmmss` + 4位随机数
- 示例：`RES202510201530451234`

### 4.3 数据完整性保障

#### 4.3.1 默认值设置
```java
private void setDefaultValues(IndividualReservation reservation) {
    if (reservation.getVersion() == null) {
        reservation.setVersion(0);
    }
    if (reservation.getDeleted() == null) {
        reservation.setDeleted(0);
    }
    if (reservation.getCreateTime() == null) {
        reservation.setCreateTime(LocalDateTime.now());
    }
    if (reservation.getUpdateTime() == null) {
        reservation.setUpdateTime(LocalDateTime.now());
    }
    if (reservation.getStatus() == null) {
        reservation.setStatus(0); // 0-未开始
    }
    // ... 其他默认值设置
}
```

#### 4.3.2 预约人员信息处理
```java
private void insertReservationPersons(IndividualReservation reservation) {
    if (reservation.getReservationPersons() != null && !reservation.getReservationPersons().isEmpty()) {
        // 设置预约人员的公共字段
        for (IndividualReservationPerson person : reservation.getReservationPersons()) {
            person.setReservationId(reservation.getId());
            person.setVisitDate(reservation.getVisitDate());
            person.setTimeSlot(reservation.getTimeSlot());
            // ... 设置其他字段
        }
        // 批量插入预约人员
        individualReservationMapper.insertPersonsBatch(reservation.getReservationPersons());
    }
}
```

### 4.4 事务处理机制
使用`@Transactional(rollbackFor = Exception.class)`注解确保事务一致性：
- 预约主记录插入失败时回滚
- 预约人员信息插入失败时回滚
- 异常发生时释放Redis锁

## 5. 异常处理

### 5.1 重复预约异常 (DuplicateReservationException)
- **HTTP状态码**: 409
- **触发条件**: 同一用户在同一时间段重复预约
- **处理逻辑**: 释放Redis锁，返回友好提示

### 5.2 Redis连接异常 (RedisConnectionFailureException)
- **HTTP状态码**: 500
- **触发条件**: Redis服务不可用
- **处理逻辑**: 降级到数据库检查，返回服务不可用提示

### 5.3 数据库异常 (DataAccessException)
- **HTTP状态码**: 503
- **触发条件**: 数据库访问失败/有重复的数据插入数据库中
- **处理逻辑**: 释放Redis锁，返回异常信息

### 5.4 通用异常处理
```java
try {
    // 业务逻辑
} catch (DuplicateReservationException e) {
    // 处理重复预约异常
    reservationUtil.releaseReservationLock(reservation);
    throw e;
} catch (RedisConnectionFailureException e) {
    // 处理Redis连接异常
    handleRedisFailure(reservation);
    return Result.error("服务暂时不可用");
} catch (DataAccessException e) {
    // 处理数据库异常
    reservationUtil.releaseReservationLock(reservation);
    throw new ServiceUnavailableException("该预约订单中已经有人预约过了");
} catch (Exception e) {
    // 处理其他异常
    reservationUtil.releaseReservationLock(reservation);
    return Result.error("预约创建异常：" + e.getMessage());
}
```

## 6. 数据库设计

### 6.1 individual_reservation表
```sql
CREATE TABLE `individual_reservation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `reservation_no` varchar(32) NOT NULL COMMENT '预约编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `scenic_id` bigint NOT NULL COMMENT '景区ID',
  `visit_date` date NOT NULL COMMENT '入区日期',
  `time_slot` tinyint NOT NULL COMMENT '入区时间段',
  `adult_count` int NOT NULL DEFAULT 0 COMMENT '成人数量',
  `child_count` int NOT NULL DEFAULT 0 COMMENT '未成年人数量',
  `total_count` int NOT NULL DEFAULT 0 COMMENT '总人数',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态：0-未开始，1-已取消，10-已核销，11-已过期',
  `verification_time` datetime COMMENT '验证时间',
  `operator_id` bigint COMMENT '操作员ID',
  `verification_location` varchar(100) COMMENT '验证地点',
  `device_info` varchar(255) COMMENT '设备信息',
  `verification_remark` varchar(255) COMMENT '验证备注',
  `cancel_time` datetime COMMENT '取消时间',
  `cancel_reason` varchar(255) COMMENT '取消原因',
  `version` int NOT NULL DEFAULT 0 COMMENT '版本号（乐观锁）',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint COMMENT '创建人',
  `update_by` bigint COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_reservation_no` (`reservation_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_scenic_id` (`scenic_id`),
  KEY `idx_visit_date` (`visit_date`),
  KEY `idx_time_slot` (`time_slot`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个人预约表';
```

### 6.2 individual_reservation_person表
```sql
CREATE TABLE `individual_reservation_person` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约人员ID',
  `reservation_id` bigint NOT NULL COMMENT '关联个人预约ID',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `id_type` tinyint NOT NULL DEFAULT 1 COMMENT '证件类型：1-身份证，2-护照，3-其他',
  `id_number` varchar(50) NOT NULL COMMENT '证件号码',
  `phone` varchar(20) COMMENT '手机号',
  `person_type` tinyint NOT NULL DEFAULT 1 COMMENT '人员类型：1-成人，2-未成年人',
  `is_contact` tinyint NOT NULL DEFAULT 0 COMMENT '是否为联系人：0-否，1-是',
  `visit_date` date NOT NULL COMMENT '访问日期',
  `time_slot` tinyint NOT NULL COMMENT '时间段',
  `version` int NOT NULL DEFAULT 0 COMMENT '版本号（乐观锁）',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint COMMENT '创建人',
  `update_by` bigint COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_reservation_id` (`reservation_id`),
  KEY `idx_id_number` (`id_number`),
  KEY `idx_is_contact` (`is_contact`),
  UNIQUE KEY `idx_unique_person_visit` (`id_type`, `id_number`, `visit_date`, `time_slot`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个人预约人员表';
```

## 7. 使用示例

### 7.1 正常请求示例

#### 7.1.1 请求
```http
POST /api/manage/individual-reservations HTTP/1.1
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

{
  "userId": 123456,
  "scenicId": 1,
  "visitDate": "2025-10-25",
  "timeSlot": 2,
  "adultCount": 2,
  "childCount": 1,
  "contactName": "张三",
  "contactIdType": 1,
  "contactIdNumber": "110101199001011234",
  "contactPhone": "13800138000",
  "reservationPersons": [
    {
      "name": "张三",
      "idType": 1,
      "idNumber": "110101199001011234",
      "phone": "13800138000",
      "personType": 1,
      "isContact": 1
    },
    {
      "name": "李四",
      "idType": 1,
      "idNumber": "110101199001011235",
      "phone": "13800138001",
      "personType": 1,
      "isContact": 0
    },
    {
      "name": "张小明",
      "idType": 1,
      "idNumber": "110101201501011234",
      "phone": "13800138002",
      "personType": 2,
      "isContact": 0
    }
  ]
}
```

#### 7.1.2 响应
```json
{
  "code": 200,
  "message": "预约创建成功",
  "data": "RES202510201530451234"
}
```

## 8. 性能优化

### 8.1 Redis缓存优化
- 使用分布式锁防止并发重复预约
- 设置合理的锁超时时间（30秒）
- 异常时及时释放锁资源

### 8.2 数据库优化
- 合理的索引设计
- 批量插入操作
- 连接池配置优化

### 8.3 事务优化
- 最小化事务范围
- 合理的异常处理
- 及时释放资源

## 9. 安全考虑

### 9.1 数据验证
- 输入参数合法性验证
- 业务规则验证
- 防SQL注入

### 9.2 权限控制
- 用户身份验证
- 操作权限检查
- 数据访问控制

### 9.3 日志记录
- 关键操作日志
- 异常详细日志
- 性能监控日志

## 10. 版本历史

### 1.0.0 (2025-10-20)
- 初始版本
- 基础预约创建功能
- 防重复预约机制
- 事务一致性保障