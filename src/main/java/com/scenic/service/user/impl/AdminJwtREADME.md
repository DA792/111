# 管理端JWT Token存储Redis实现文档

## 1. 概述

本文档详细说明了管理端JWT Token存储到Redis的实现方案，包括Token生成、验证、存储和失效机制。

## 2. 验证结果

**管理端Token已成功存储到Redis中**：

1. **UserService实现**：
   - `UserServiceImpl.loginWithUsernameAndPassword()`方法调用`jwtUtil.generateAdminToken()`
   - 生成的Token通过JWT工具类存储到Redis

2. **JWT工具类实现**：
   - `JwtUtil.generateAdminToken()`方法将Token存储到Redis
   - 使用键格式：`admin:token:{userId}`
   - 设置与JWT相同的过期时间

3. **验证流程**：
   - `JwtUtil.validateAdminToken()`从Redis获取存储的Token进行验证
   - 确保请求中的Token与Redis中存储的Token一致

## 2. 实现原理

### 2.1 整体架构

```mermaid
sequenceDiagram
    participant 客户端 as 管理端客户端
    participant 服务端 as 服务端
    participant Redis as Redis缓存
    participant 数据库 as 数据库

    客户端->>服务端: POST /api/manage/login 发送用户名密码
    服务端->>数据库: 查询用户信息
    数据库-->>服务端: 返回用户信息
    服务端->>Redis: 检查是否存在有效Token
    alt Redis中存在有效Token
        Redis-->>服务端: 返回现有Token
        服务端-->>客户端: 返回现有Token
    else Redis中无有效Token
        服务端->>服务端: 生成新JWT Token
        服务端->>Redis: 存储Token到Redis
        Redis-->>服务端: 存储成功
        服务端-->>客户端: 返回新Token
    end
    客户端->>服务端: 后续请求携带Authorization头
    服务端->>Redis: 验证Token有效性
    Redis-->>服务端: 返回验证结果
    alt Token有效
        服务端->>服务端: 处理业务请求
        服务端-->>客户端: 返回业务结果
    else Token无效
        服务端-->>客户端: 401未授权
    end
```

### 2.2 缓存策略

采用"读缓存→无则生成→回填缓存；失效时删除缓存"的策略：

1. **登录时**：
   - 先检查Redis中是否存在有效的Token
   - 如果存在且有效，直接返回现有Token
   - 如果不存在或已失效，生成新Token并存储到Redis

2. **验证时**：
   - 从Redis中获取存储的Token
   - 验证Token是否与请求中的Token一致
   - 验证Token是否未过期

3. **失效时**：
   - 从Redis中删除对应的Token

## 3. 核心实现

### 3.1 JwtUtil工具类

**Redis键设计**：
- 管理端Token：`admin:token:{userId}`
- 小程序端Token：`miniapp:token:{userId}`

**主要方法**：

1. `generateAdminToken(String username, Long userId)` - 生成管理端Token
2. `validateAdminToken(String token)` - 验证管理端Token
3. `invalidateAdminToken(Long userId)` - 使管理端Token失效

### 3.2 Token生成流程

```java
public String generateAdminToken(String username, Long userId) {
    String redisKey = ADMIN_TOKEN_KEY_PREFIX + userId;
    
    // 检查是否已存在有效的Token
    String existingToken = redisTemplate.opsForValue().get(redisKey);
    if (existingToken != null) {
        // 验证现有Token是否仍然有效
        if (validateAdminToken(existingToken)) {
            // 如果现有Token仍然有效，直接返回
            return existingToken;
        }
    }
    
    // 如果没有有效Token或Token已失效，则生成新Token
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", username);
    claims.put("userId", userId);
    claims.put("role", "admin");
    
    // 生成JWT令牌
    String token = createToken(claims, username, jwtConfig.getAdminSecret(), jwtConfig.getAdminExpiration());
    
    // 将token存储到Redis中
    redisTemplate.opsForValue().set(redisKey, token, jwtConfig.getAdminExpiration(), TimeUnit.MILLISECONDS);
    
    return token;
}
```

### 3.3 Token验证流程

```java
public boolean validateAdminToken(String token) {
    try {
        // 从JWT令牌中获取用户ID
        Claims claims = getAllClaimsFromToken(token, jwtConfig.getAdminSecret());
        Long userId = claims.get("userId", Long.class);
        
        // 从Redis中获取token
        String redisKey = ADMIN_TOKEN_KEY_PREFIX + userId;
        String storedToken = redisTemplate.opsForValue().get(redisKey);
        
        // 验证token是否存在于Redis中，并且与传入的token一致
        if (storedToken == null || !storedToken.equals(token)) {
            return false;
        }
        
        // 验证token是否过期
        return !isTokenExpired(token, jwtConfig.getAdminSecret());
    } catch (Exception e) {
        return false;
    }
}
```

## 4. 接口设计

### 4.1 登录接口

**接口地址**: `POST /api/manage/login`

**请求参数**:
```json
{
  "username": "管理员用户名",
  "password": "管理员密码"
}
```

**响应参数**:
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "生成的JWT令牌",
    "user": {
      "id": 1,
      "userName": "admin",
      "realName": "管理员",
      "userType": 2,
      "status": 1
    }
  }
}
```

### 4.2 后续请求

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 5. 配置说明

### 5.1 application.yml配置

```yaml
# 管理后台端配置
admin:
  api:
    prefix: /api/manage
  security:
    jwt:
      secret: admin_secret_key_with_at_least_256_bits_for_hmac_sha_algorithm_security_requirement_rfc7518
      expiration: 7200000 # 2小时
```

### 5.2 Redis配置

```yaml
spring:
  redis:
    host: 192.168.77.2
    port: 10228
    password: 
    jedis:
      pool:
        max-active: 8
        max-wait: -1
        max-idle: 8
        min-idle: 0
    timeout: 3000
```

## 6. 安全性考虑

### 6.1 Token安全
- 使用强密钥进行JWT签名
- 设置合理的过期时间（2小时）
- Token存储在Redis中，支持快速失效

### 6.2 Redis安全
- Token键设置与JWT相同的过期时间
- 支持手动失效Token
- 防止Token泄露和重放攻击

## 7. 性能优化

### 7.1 缓存优化
- Redis缓存热点Token数据
- 自动过期机制减少内存占用
- 快速的Token验证响应

### 7.2 数据库优化
- 减少重复的用户查询
- Token验证不访问数据库
- 提高系统整体响应速度

## 8. 使用示例

### 8.1 登录示例

```javascript
// 管理端登录
fetch('/api/manage/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'admin',
    password: 'password123'
  })
})
.then(response => response.json())
.then(data => {
  if (data.code === 200) {
    // 存储token
    localStorage.setItem('admin_token', data.data.token);
  }
});
```

### 8.2 后续请求示例

```javascript
// 带Token的后续请求
fetch('/api/manage/users', {
  headers: {
    'Authorization': 'Bearer ' + localStorage.getItem('admin_token')
  }
})
.then(response => response.json())
.then(data => {
  // 处理响应数据
});
```

## 9. 部署说明

### 9.1 环境要求
- Redis 3.0+
- 正确配置Redis连接信息

### 9.2 配置验证
1. 确认Redis服务正常运行
2. 测试Token生成和验证功能
3. 验证Token过期和失效机制

## 10. 故障排除

### 10.1 常见问题

1. **Token验证失败**：
   - 检查Redis连接是否正常
   - 确认Token是否已过期
   - 验证JWT密钥配置是否正确

2. **Redis存储失败**：
   - 检查Redis内存使用情况
   - 确认Redis配置是否正确
   - 查看系统日志定位问题

### 10.2 监控建议

1. 监控Redis内存使用情况
2. 统计Token生成和验证的响应时间
3. 记录Token失效操作日志
