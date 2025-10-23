# LinkMe API接口文档

## 接口规范

### 1. API路径规范
- 使用下划线连接多个字符
- 不加前缀，直接使用功能路径
- 示例：`/get_user_info`、`/create_post`

### 2. 传输字段规范
- 变量名使用下划线命名
- 数据库字段使用下划线命名
- 前后端变量使用小驼峰命名

### 3. 响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

## 用户管理接口

### 1. 获取用户信息
- **接口**: `GET /user/{user_id}/info`
- **描述**: 根据用户ID获取用户详细信息
- **参数**:
  - `user_id` (路径参数): 用户ID
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "testuser",
    "email": "test@example.com",
    "phone": "13800138000",
    "nickname": "测试用户",
    "gender": "男",
    "birthday": "1990-01-01",
    "region": "北京",
    "avatarUrl": "https://example.com/avatar.jpg",
    "bio": "个人简介",
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

### 2. 更新用户信息
- **接口**: `PUT /user/{user_id}/info`
- **描述**: 更新用户的基本信息
- **参数**:
  - `user_id` (路径参数): 用户ID
- **请求体**:
```json
{
  "nickname": "新昵称",
  "email": "new@example.com",
  "phone": "13900139000",
  "gender": "女",
  "birthday": "1995-05-15",
  "region": "上海",
  "bio": "更新后的简介"
}
```

### 3. 用户注册
- **接口**: `POST /user/register`
- **描述**: 新用户注册
- **请求体**:
```json
{
  "username": "newuser",
  "email": "new@example.com",
  "phone": "13800138000",
  "password": "password123",
  "nickname": "新用户",
  "gender": "男",
  "birthday": "1990-01-01"
}
```

### 4. 用户登录
- **接口**: `POST /user/login`
- **描述**: 用户登录验证
- **请求体**:
```json
{
  "loginName": "test@example.com",
  "password": "password123"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "user": {
      "userId": 1,
      "username": "testuser",
      "nickname": "测试用户"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

## 帖子管理接口

### 1. 获取帖子列表
- **接口**: `GET /posts`
- **描述**: 获取帖子列表，支持分页和过滤
- **参数**:
  - `page` (查询参数): 页码，默认1
  - `limit` (查询参数): 每页数量，默认10
  - `tag` (查询参数): 标签ID，可选
  - `user_id` (查询参数): 用户ID，可选
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "postId": 1,
      "userId": 1,
      "content": "帖子内容",
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

### 2. 创建帖子
- **接口**: `POST /posts`
- **描述**: 发布新帖子
- **请求体**:
```json
{
  "userId": 1,
  "content": "帖子内容",
  "images": ["https://example.com/image1.jpg"],
  "tags": [1, 2, 3]
}
```

### 3. 获取帖子详情
- **接口**: `GET /posts/{post_id}`
- **描述**: 根据帖子ID获取帖子详细信息
- **参数**:
  - `post_id` (路径参数): 帖子ID
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "postId": 1,
    "userId": 1,
    "content": "帖子内容",
    "createdAt": "2024-01-01T00:00:00",
    "images": ["https://example.com/image1.jpg"],
    "tags": [1, 2, 3],
    "comments": [],
    "likes": 5
  }
}
```

### 4. 编辑帖子
- **接口**: `PUT /posts/{post_id}`
- **描述**: 更新帖子内容
- **参数**:
  - `post_id` (路径参数): 帖子ID
- **请求体**:
```json
{
  "content": "更新后的帖子内容"
}
```

### 5. 删除帖子
- **接口**: `DELETE /posts/{post_id}`
- **描述**: 根据帖子ID删除帖子
- **参数**:
  - `post_id` (路径参数): 帖子ID

## 评论与互动接口

### 1. 发表评论
- **接口**: `POST /posts/{post_id}/comments`
- **描述**: 对帖子发表评论
- **参数**:
  - `post_id` (路径参数): 帖子ID
- **请求体**:
```json
{
  "userId": 1,
  "content": "评论内容",
  "parentId": null
}
```

### 2. 获取评论列表
- **接口**: `GET /posts/{post_id}/comments`
- **描述**: 获取帖子的评论列表
- **参数**:
  - `post_id` (路径参数): 帖子ID
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "commentId": 1,
      "userId": 1,
      "content": "评论内容",
      "createdAt": "2024-01-01T00:00:00",
      "parentId": null
    }
  ]
}
```

### 3. 点赞帖子
- **接口**: `POST /posts/{post_id}/like`
- **描述**: 对帖子进行点赞
- **参数**:
  - `post_id` (路径参数): 帖子ID
- **请求体**:
```json
{
  "userId": 1
}
```

### 4. 取消点赞
- **接口**: `DELETE /posts/{post_id}/like`
- **描述**: 取消对帖子的点赞
- **参数**:
  - `post_id` (路径参数): 帖子ID
- **请求体**:
```json
{
  "userId": 1
}
```

## 关注与红心接口

### 1. 关注用户
- **接口**: `POST /follow/{user_id}`
- **描述**: 关注指定用户
- **参数**:
  - `user_id` (路径参数): 被关注用户ID
- **请求体**:
```json
{
  "followerId": 1
}
```

### 2. 取消关注
- **接口**: `DELETE /follow/{user_id}`
- **描述**: 取消关注指定用户
- **参数**:
  - `user_id` (路径参数): 被关注用户ID
- **请求体**:
```json
{
  "followerId": 1
}
```

### 3. 给用户点红心
- **接口**: `POST /heart/{user_id}`
- **描述**: 给指定用户点红心（自动关注）
- **参数**:
  - `user_id` (路径参数): 被红心用户ID
- **请求体**:
```json
{
  "fromUserId": 1
}
```

### 4. 取消红心
- **接口**: `DELETE /heart/{user_id}`
- **描述**: 取消对指定用户的红心
- **参数**:
  - `user_id` (路径参数): 被红心用户ID
- **请求体**:
```json
{
  "fromUserId": 1
}
```

## 匹配与推荐接口

### 1. 获取匹配列表
- **接口**: `GET /matches`
- **描述**: 获取当前用户的匹配列表
- **参数**:
  - `user_id` (查询参数): 当前用户ID
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "matchId": 1,
      "user1Id": 1,
      "user2Id": 2,
      "status": 0,
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

### 2. 获取推荐用户
- **接口**: `GET /recommended_users`
- **描述**: 获取潜在匹配用户推荐列表
- **参数**:
  - `user_id` (查询参数): 当前用户ID
  - `tags` (查询参数): 用户选择的标签
  - `birthday` (查询参数): 生日
  - `region` (查询参数): 地区
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "userId": 2,
      "nickname": "推荐用户",
      "tags": ["运动", "音乐"],
      "region": "北京"
    }
  ]
}
```

## 聊天与通知接口

### 1. 获取会话列表
- **接口**: `GET /conversations`
- **描述**: 获取当前用户的会话列表
- **参数**:
  - `user_id` (查询参数): 用户ID
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "conversationId": 1,
      "user1Id": 1,
      "user2Id": 2,
      "lastMessage": "最后一条消息",
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

### 2. 创建会话
- **接口**: `POST /conversations`
- **描述**: 发起新的会话
- **请求体**:
```json
{
  "user1Id": 1,
  "user2Id": 2
}
```

### 3. 获取消息记录
- **接口**: `GET /conversations/{conversation_id}/messages`
- **描述**: 获取指定会话的消息记录
- **参数**:
  - `conversation_id` (路径参数): 会话ID
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "messageId": 1,
      "senderId": 1,
      "contentType": "text",
      "content": "消息内容",
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

### 4. 发送消息
- **接口**: `POST /conversations/{conversation_id}/messages`
- **描述**: 发送消息到指定会话
- **参数**:
  - `conversation_id` (路径参数): 会话ID
- **请求体**:
```json
{
  "senderId": 1,
  "contentType": "text",
  "content": "消息内容"
}
```

### 5. 获取通知列表
- **接口**: `GET /notifications`
- **描述**: 获取用户通知列表
- **参数**:
  - `user_id` (查询参数): 用户ID
  - `is_read` (查询参数): 是否已读，可选
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "notificationId": 1,
      "type": "follow",
      "actorId": 2,
      "relatedId": 1,
      "createdAt": "2024-01-01T00:00:00",
      "isRead": false
    }
  ]
}
```

## 错误响应格式

### 统一错误格式
```json
{
  "code": 400,
  "message": "请求参数错误",
  "data": null
}
```

### 常见错误码
- `400`: 请求参数错误
- `401`: 未授权访问
- `403`: 禁止访问
- `404`: 资源不存在
- `500`: 服务器内部错误

### 错误示例
```json
{
  "code": 404,
  "message": "用户不存在",
  "data": null
}
```

```json
{
  "code": 401,
  "message": "token已过期",
  "data": null
}
```

```json
{
  "code": 400,
  "message": "验证码错误",
  "data": null
}
```
