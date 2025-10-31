# 用户管理API接口测试请求体示例

本文档包含所有用户管理API接口的Request Body示例，用于测试各种场景。

## 基础信息

- **Base URL**: `http://localhost:8080`
- **JWT Token**: 需要登录后获取，格式为 `Bearer {token}`

---

## 1. 用户注册接口

**接口**: `POST /user/register`

### 说明
- **无需JWT Token认证**（公开接口）
- **Headers**: 
  - `Content-Type: application/json`

### 1.1 正常情况 - 使用邮箱注册

**完整请求示例：**

**URL**: `POST http://localhost:8080/user/register`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "testuser001",
  "email": "testuser001@example.com",
  "password_hash": "Password_123456",
  "nickname": "测试用户001",
  "gender": "male",
  "birthday": "1995-05-15",
  "region": "北京市",
  "bio": "这是一个测试用户"
}
```

### 1.2 正常情况 - 使用手机号注册

**URL**: `POST http://localhost:8080/user/register`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "testuser002",
  "phone": "13800138001",
  "passwordHash": "TestPass_123",
  "nickname": "测试用户002",
  "gender": "female",
  "birthday": "1998-08-20",
  "region": "上海市",
  "bio": "手机号注册测试"
}
```

### 1.3 正常情况 - 邮箱和手机号同时注册

**URL**: `POST http://localhost:8080/user/register`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "testuser003",
  "email": "testuser003@example.com",
  "phone": "13800138002",
  "passwordHash": "MyPass_456",
  "nickname": "测试用户003",
  "gender": "other",
  "birthday": "1990-01-10",
  "region": "广州市",
  "avatarUrl": "https://example.com/avatar.jpg",
  "bio": "完整信息注册测试"
}
```

### 1.4 边界情况 - 最小必填字段（只有邮箱和密码）注册失败，用户名不能为空

**URL**: `POST http://localhost:8080/user/register`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "minimal@example.com",
  "passwordHash": "MinPass123"
}
```

### 1.5 边界情况 - 最小必填字段（只有手机号和密码）

**URL**: `POST http://localhost:8080/user/register`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "phone": "13800138003",
  "passwordHash": "Phone_789"
}
```

### 1.6 错误情况 - 密码为空

**URL**: `POST http://localhost:8080/user/register`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "test@example.com",
  "passwordHash": ""
}
```

### 1.7 错误情况 - 密码长度不足

**URL**: `POST http://localhost:8080/user/register`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "test@example.com",
  "passwordHash": "Pass1"
}
```

### 1.8 错误情况 - 密码复杂度不足（只有小写和数字）

**URL**: `POST http://localhost:8080/user/register`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "test@example.com",
  "passwordHash": "password123"
}
```

### 1.9 错误情况 - 邮箱和手机号都为空（邮箱或手机号至少填写一个）

**URL**: `POST http://localhost:8080/user/register`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "testuser",
  "passwordHash": "ValidPass_123"
}
```

### 1.10 错误情况 - 邮箱格式错误（测试时需要验证）

**URL**: `POST http://localhost:8080/user/register`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "invalid-email",
  "username": "testuser",
  "passwordHash": "ValidPass_123"
}
```

### 1.11 正常情况 - 复杂密码示例（包含所有字符类型）

**URL**: `POST http://localhost:8080/user/register`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "complex@example.com",
  "username": "testuser111",
  "passwordHash": "Complex_Pass123",
  "nickname": "复杂密码测试"
}
```

---

## 2. 用户登录接口

**接口**: `POST /user/login`

### 说明
- **无需JWT Token认证**（公开接口）
- **Headers**: 
  - `Content-Type: application/json`

### 2.1 正常情况 - 使用邮箱登录

**完整请求示例：**

**URL**: `POST http://localhost:8080/user/login`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "loginName": "testuser003@example.com",
  "password": "MyPass_456"
}
```

### 2.2 正常情况 - 使用手机号登录

**URL**: `POST http://localhost:8080/user/login`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "loginName": "13800138001",
  "password": "TestPass_123"
}
```

### 2.3 正常情况 - 使用用户名登录

**URL**: `POST http://localhost:8080/user/login`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "loginName": "testuser001",
  "password": "Password123"
}
```

### 2.4 错误情况 - 登录名为空

**URL**: `POST http://localhost:8080/user/login`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "loginName": "",
  "password": "Password123"
}
```

### 2.5 错误情况 - 密码为空

**URL**: `POST http://localhost:8080/user/login`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "loginName": "testuser001@example.com",
  "password": ""
}
```

### 2.6 错误情况 - 密码错误

**URL**: `POST http://localhost:8080/user/login`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "loginName": "testuser001@example.com",
  "password": "WrongPassword123"
}
```

### 2.7 错误情况 - 用户不存在

**URL**: `POST http://localhost:8080/user/login`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "loginName": "nonexistent@example.com",
  "password": "SomePass_123"
}
```

---

## 3. 获取用户信息接口

**接口**: `GET /user/{userId}/info`

### 说明
- **无需Request Body**
- **需要JWT Token认证**
- **路径参数**: `userId` (例如: 1, 2, 3)
- **Headers**: 
  - `Authorization: Bearer {your_token}`
  - `Content-Type: application/json`

### 3.1 正常情况 - 获取用户信息

**完整请求示例：**

**URL**: `GET http://localhost:8080/user/1/info`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlcjAwMSIsInVzZXJJZCI6MSwiaWF0IjoxNzA5MjE0NTY3LCJleHAiOjE3MDkyMTgxNjd9.example-token
Content-Type: application/json
```

**Request Body**: 无（GET请求）

### 3.2 测试URL示例
- `GET /user/1/info` (替换1为实际用户ID)
- `GET /user/999/info` (不存在的用户ID)

**注意**: 所有请求都需要在Headers中添加 `Authorization: Bearer {token}`

---

## 4. 更新用户信息接口

**接口**: `PUT /user/{userId}/info`

### 说明
- **需要JWT Token认证**
- **路径参数**: `userId` (例如: 1, 2, 3)
- **Headers**: 
  - `Authorization: Bearer {your_token}`
  - `Content-Type: application/json`

### 4.1 正常情况 - 更新昵称和简介

**完整请求示例：**

**URL**: `PUT http://localhost:8080/user/1/info`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlcjAwMSIsInVzZXJJZCI6MSwiaWF0IjoxNzA5MjE0NTY3LCJleHAiOjE3MDkyMTgxNjd9.example-token
Content-Type: application/json
```

**Request Body:**
```json
{
  "nickname": "新昵称",
  "bio": "更新后的个人简介",
  "gender": "女",
  "region": "深圳市"
}
```

### 4.2 正常情况 - 更新生日

**URL**: `PUT http://localhost:8080/user/1/info`

**Headers:**
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "birthday": "1992-03-15"
}
```

### 4.3 正常情况 - 更新头像

**URL**: `PUT http://localhost:8080/user/1/info`

**Headers:**
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "avatarUrl": "https://example.com/new-avatar.jpg"
}
```

### 4.4 正常情况 - 完整更新所有字段

**URL**: `PUT http://localhost:8080/user/1/info`

**Headers:**
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "newusername",
  "email": "newemail@example.com",
  "phone": "13900139000",
  "nickname": "新的昵称",
  "gender": "男",
  "birthday": "1995-06-20",
  "region": "杭州市",
  "avatarUrl": "https://example.com/avatar.jpg",
  "bio": "更新后的完整简介"
}
```

### 4.5 边界情况 - 只更新部分字段

**URL**: `PUT http://localhost:8080/user/1/info`

**Headers:**
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "nickname": "仅更新昵称"
}
```

### 4.6 边界情况 - 清空可选字段

**URL**: `PUT http://localhost:8080/user/1/info`

**Headers:**
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "nickname": null,
  "bio": null,
  "avatarUrl": null
}
```

---

## 5. 获取用户列表接口

**接口**: `GET /user/list`

### 说明
- **无需Request Body**
- **需要JWT Token认证**
- **查询参数**:
  - `page`: 页码（默认1）
  - `size`: 每页数量（默认10）
- **Headers**: 
  - `Authorization: Bearer {your_token}`
  - `Content-Type: application/json`

### 5.1 正常情况 - 获取第一页用户列表（默认）

**完整请求示例：**

**URL**: `GET http://localhost:8080/user/list`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlcjAwMSIsInVzZXJJZCI6MSwiaWF0IjoxNzA5MjE0NTY3LCJleHAiOjE3MDkyMTgxNjd9.example-token
Content-Type: application/json
```

**Request Body**: 无（GET请求）

### 5.2 正常情况 - 获取指定页码和数量

**URL**: `GET http://localhost:8080/user/list?page=1&size=10`

**Headers:**
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

### 5.3 测试URL示例
- `GET http://localhost:8080/user/list`
- `GET http://localhost:8080/user/list?page=1&size=10`
- `GET http://localhost:8080/user/list?page=2&size=5`
- `GET http://localhost:8080/user/list?page=0&size=-1` (边界测试)

**注意**: 所有请求都需要在Headers中添加 `Authorization: Bearer {token}`

---

## 6. 修改密码接口

**接口**: `PUT /user/{userId}/password`

### 说明
- **需要JWT Token认证**
- **路径参数**: `userId` (例如: 1, 2, 3)
- **Headers**: 
  - `Authorization: Bearer {your_token}`
  - `Content-Type: application/json`

### 6.1 正常情况 - 修改密码

**完整请求示例：**

**URL**: `PUT http://localhost:8080/user/1/password`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlcjAwMSIsInVzZXJJZCI6MSwiaWF0IjoxNzA5MjE0NTY3LCJleHAiOjE3MDkyMTgxNjd9.example-token
Content-Type: application/json
```

**Request Body:**
```json
{
  "oldPassword": "Password123",
  "newPassword": "NewPass_456"
}
```

### 6.2 正常情况 - 使用复杂密码

**URL**: `PUT http://localhost:8080/user/1/password`

**Headers:**
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "oldPassword": "TestPass_123",
  "newPassword": "Complex_NewPass123"
}
```

### 6.3 错误情况 - 旧密码错误

**URL**: `PUT http://localhost:8080/user/1/password`

**Headers:**
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "oldPassword": "WrongOldPass",
  "newPassword": "NewPass_456"
}
```

### 6.4 错误情况 - 旧密码为空

**URL**: `PUT http://localhost:8080/user/1/password`

**Headers:**
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "oldPassword": "",
  "newPassword": "NewPass_456"
}
```

### 6.5 错误情况 - 新密码为空

**URL**: `PUT http://localhost:8080/user/1/password`

**Headers:**
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "oldPassword": "Password123",
  "newPassword": ""
}
```

### 6.6 错误情况 - 新密码长度不足

**URL**: `PUT http://localhost:8080/user/1/password`

**Headers:**
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "oldPassword": "Password123",
  "newPassword": "Short1"
}
```

### 6.7 错误情况 - 新密码复杂度不足

**URL**: `PUT http://localhost:8080/user/1/password`

**Headers:**
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "oldPassword": "Password123",
  "newPassword": "onlylowercase"
}
```

### 6.8 边界情况 - 新旧密码相同

**URL**: `PUT http://localhost:8080/user/1/password`

**Headers:**
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "oldPassword": "Password123",
  "newPassword": "Password123"
}
```

---

## 7. 发送重置密码验证码接口

**接口**: `POST /user/forgot-password/send-code`

### 说明
- **无需JWT Token认证**（公开接口）
- **Headers**: 
  - `Content-Type: application/json`

### 7.1 正常情况 - 使用邮箱发送

**完整请求示例：**

**URL**: `POST http://localhost:8080/user/forgot-password/send-code`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "testuser001@example.com"
}
```

### 7.2 正常情况 - 使用手机号发送

**URL**: `POST http://localhost:8080/user/forgot-password/send-code`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "phone": "13800138001"
}
```

### 7.3 错误情况 - 邮箱和手机号都为空

**URL**: `POST http://localhost:8080/user/forgot-password/send-code`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{}
```

### 7.4 错误情况 - 邮箱为空字符串

**URL**: `POST http://localhost:8080/user/forgot-password/send-code`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "",
  "phone": ""
}
```

### 7.5 错误情况 - 用户不存在（测试时需要先创建用户）

**URL**: `POST http://localhost:8080/user/forgot-password/send-code`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "nonexistent@example.com"
}
```

---

## 8. 重置密码接口（忘记密码）

**接口**: `POST /user/forgot-password/reset`

### 说明
- **无需JWT Token认证**（公开接口）
- **Headers**: 
  - `Content-Type: application/json`

### 8.1 正常情况 - 使用邮箱重置

**完整请求示例：**

**URL**: `POST http://localhost:8080/user/forgot-password/reset`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "testuser001@example.com",
  "code": "123456",
  "newPassword": "ResetPass_789"
}
```

### 8.2 正常情况 - 使用手机号重置

**URL**: `POST http://localhost:8080/user/forgot-password/reset`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "phone": "13800138001",
  "code": "654321",
  "newPassword": "ResetPass_012"
}
```

### 8.3 错误情况 - 验证码为空

**URL**: `POST http://localhost:8080/user/forgot-password/reset`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "testuser001@example.com",
  "code": "",
  "newPassword": "ResetPass_789"
}
```

### 8.4 错误情况 - 验证码错误

**URL**: `POST http://localhost:8080/user/forgot-password/reset`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "testuser001@example.com",
  "code": "000000",
  "newPassword": "ResetPass_789"
}
```

### 8.5 错误情况 - 新密码为空

**URL**: `POST http://localhost:8080/user/forgot-password/reset`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "testuser001@example.com",
  "code": "123456",
  "newPassword": ""
}
```

### 8.6 错误情况 - 新密码不符合规则

**URL**: `POST http://localhost:8080/user/forgot-password/reset`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "testuser001@example.com",
  "code": "123456",
  "newPassword": "weak"
}
```

### 8.7 错误情况 - 邮箱和手机号都为空

**URL**: `POST http://localhost:8080/user/forgot-password/reset`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "code": "123456",
  "newPassword": "ResetPass_789"
}
```

### 8.8 错误情况 - 验证码过期（需要等待10分钟后测试）

**URL**: `POST http://localhost:8080/user/forgot-password/reset`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "testuser001@example.com",
  "code": "123456",
  "newPassword": "ResetPass_789"
}
```

---

## 9. 删除用户接口

**接口**: `DELETE /user/{userId}`

### 说明
- **无需Request Body**
- **需要JWT Token认证**
- **路径参数**: `userId` (例如: 1, 2, 3)
- **Headers**: 
  - `Authorization: Bearer {your_token}`
  - `Content-Type: application/json`

### 9.1 正常情况 - 删除用户

**完整请求示例：**

**URL**: `DELETE http://localhost:8080/user/1`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlcjAwMSIsInVzZXJJZCI6MSwiaWF0IjoxNzA5MjE0NTY3LCJleHAiOjE3MDkyMTgxNjd9.example-token
Content-Type: application/json
```

**Request Body**: 无（DELETE请求）

### 9.2 测试URL示例
- `DELETE http://localhost:8080/user/1` (替换1为实际用户ID)
- `DELETE http://localhost:8080/user/999` (不存在的用户ID)

**注意**: 所有请求都需要在Headers中添加 `Authorization: Bearer {token}`

---

## 密码规则说明

所有涉及密码的接口都需要遵循以下规则：

1. **最小长度**: 不少于8个字符
2. **复杂度要求**: 必须同时包含以下字符类型中的**至少三种**：
   - 大写字母 (A-Z)
   - 小写字母 (a-z)
   - 数字 (0-9)
   - 下划线 (_)

### 符合要求的密码示例：
- `Password123` ✓ (大写、小写、数字)
- `password_123` ✓ (小写、数字、下划线)
- `PASSWORD_123` ✓ (大写、数字、下划线)
- `Pass_123` ✓ (包含所有类型)

### 不符合要求的密码示例：
- `pass123` ✗ (长度不足)
- `password` ✗ (只有小写)
- `PASSWORD` ✗ (只有大写)
- `12345678` ✗ (只有数字)
- `password123` ✗ (只有两种类型)

---

## 测试流程建议

### 1. 注册新用户
**URL**: `POST http://localhost:8080/user/register`

**Headers:**
```
Content-Type: application/json
```

**Request Body**: 使用示例 1.1 或 1.2 的请求体
```json
{
  "username": "testuser001",
  "email": "testuser001@example.com",
  "password_hash": "Password_123456",
  "nickname": "测试用户001"
}
```

**响应**: 保存返回的 `token` 和 `user` 信息，后续接口需要使用 token

### 2. 登录获取Token
**URL**: `POST http://localhost:8080/user/login`

**Headers:**
```
Content-Type: application/json
```

**Request Body**: 使用示例 2.1 的请求体
```json
{
  "loginName": "testuser001@example.com",
  "password": "Password_123456"
}
```

**响应**: 保存返回的 `token`，后续接口需要使用

### 3. 获取用户信息
**URL**: `GET http://localhost:8080/user/1/info`

**Headers:**
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

**注意**: 将 `{your_token}` 替换为登录或注册时获取的实际token

### 4. 更新用户信息
**URL**: `PUT http://localhost:8080/user/1/info`

**Headers:**
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

**Request Body**: 使用示例 4.1 的请求体
```json
{
  "nickname": "新昵称",
  "bio": "更新后的个人简介"
}
```

### 5. 修改密码
**URL**: `PUT http://localhost:8080/user/1/password`

**Headers:**
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

**Request Body**: 使用示例 6.1 的请求体
```json
{
  "oldPassword": "Password_123456",
  "newPassword": "NewPass_456"
}
```

### 6. 忘记密码流程
**步骤1: 发送验证码**
**URL**: `POST http://localhost:8080/user/forgot-password/send-code`

**Headers:**
```
Content-Type: application/json
```

**Request Body**: 使用示例 7.1 的请求体
```json
{
  "email": "testuser001@example.com"
}
```

**注意**: 验证码会打印在服务器控制台

**步骤2: 重置密码**
**URL**: `POST http://localhost:8080/user/forgot-password/reset`

**Headers:**
```
Content-Type: application/json
```

**Request Body**: 使用示例 8.1 的请求体（code从控制台获取）
```json
{
  "email": "testuser001@example.com",
  "code": "123456",
  "newPassword": "ResetPass_789"
}
```

### 7. 获取用户列表
**URL**: `GET http://localhost:8080/user/list?page=1&size=10`

**Headers:**
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

### 8. 删除用户
**URL**: `DELETE http://localhost:8080/user/1`

**Headers:**
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

---

## 常见错误码说明

- `200`: 成功
- `400`: 请求参数错误（密码不符合规则、必填字段为空等）
- `401`: 未授权（登录失败、token无效等）
- `404`: 资源不存在（用户不存在等）

---

## 注意事项

1. **JWT Token**: 需要在请求头中添加 `Authorization: Bearer {token}`
2. **验证码**: 发送验证码后，验证码会打印在服务器控制台（生产环境需要配置邮件/短信服务）
3. **日期格式**: 生日字段使用 `YYYY-MM-DD` 格式
4. **字符编码**: 所有请求使用 UTF-8 编码
5. **Content-Type**: 所有POST/PUT请求需要设置 `Content-Type: application/json`

---

## Postman/Insomnia 导入说明

可以将上述JSON示例直接复制到Postman或Insomnia中进行测试：

1. **创建新的请求**
   - 设置请求方法（GET/POST/PUT/DELETE）
   - 输入完整的URL（例如：`http://localhost:8080/user/register`）

2. **设置Headers**
   - 对于所有POST/PUT请求，添加：
     - Key: `Content-Type`
     - Value: `application/json`
   - 对于需要认证的接口（获取用户信息、更新用户信息、修改密码、获取用户列表、删除用户），添加：
     - Key: `Authorization`
     - Value: `Bearer {你的token}`（注意Bearer后面有空格）
   
3. **设置Request Body**
   - 对于POST/PUT请求，选择Body标签页
   - 选择 `raw` 和 `JSON` 格式
   - 粘贴对应的JSON示例
   - 修改必要的数据（如userId、email等）以匹配你的测试环境

4. **获取Token**
   - 首先调用注册或登录接口获取token
   - 从响应的 `data.token` 字段中复制token
   - 在后续需要认证的接口中使用该token

5. **测试建议**
   - 先测试注册接口，获取token和userId
   - 然后使用获取的token测试其他需要认证的接口
   - 对于GET和DELETE请求，不需要设置Body，只需要在Headers中添加Authorization

## 快速测试示例（使用curl）

### 1. 注册用户
```bash
curl -X POST http://localhost:8080/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser001",
    "email": "testuser001@example.com",
    "password_hash": "Password_123456",
    "nickname": "测试用户001"
  }'
```

### 2. 登录获取Token
```bash
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginName": "testuser001@example.com",
    "password": "Password_123456"
  }'
```

### 3. 获取用户信息（使用token）
```bash
curl -X GET http://localhost:8080/user/1/info \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json"
```

### 4. 更新用户信息（使用token）
```bash
curl -X PUT http://localhost:8080/user/1/info \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "新昵称",
    "bio": "更新后的个人简介"
  }'
```

