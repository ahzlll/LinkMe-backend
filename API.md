# LinkMe API 接口文档

## 接口规范

### 1. API 路径规范

- RESTful 风格，使用路径参数和查询参数
- 资源使用复数形式，如 `/conversations`、`/posts`
- 示例：`/user/{userId}/info`、`/conversations/{id}/messages`

### 2. 传输字段规范

- 请求体和响应体使用小驼峰命名（camelCase）
- 数据库字段使用下划线命名（snake_case）
- 路径参数和查询参数使用小驼峰命名

### 3. 响应格式

所有接口统一使用以下响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 4. 认证方式

- 需要认证的接口在请求头中携带：`Authorization: Bearer <token>`
- Token 通过登录接口获取
- 未认证的请求将返回 401 错误

### 5. 分页参数

- `page`: 页码，从 1 开始，默认 1
- `size` 或 `limit`: 每页数量，默认 10 或 20

## 用户管理接口

### 1. 获取用户信息

- **接口**: `GET /user/{user_id}/info`
- **描述**: 根据用户 ID 获取用户详细信息
- **参数**:
  - `user_id` (路径参数): 用户 ID
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
  - `user_id` (路径参数): 用户 ID
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
- **描述**: 用户登录验证，支持邮箱、手机号或用户名登录
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
      "nickname": "测试用户",
      "email": "test@example.com",
      "avatarUrl": "https://example.com/avatar.jpg"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 5. 获取用户列表

- **接口**: `GET /user/list`
- **描述**: 分页获取用户列表
- **认证**: 需要 Bearer Token
- **参数**:
  - `page` (查询参数): 页码，默认 1
  - `size` (查询参数): 每页数量，默认 10
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "userId": 1,
      "username": "testuser",
      "nickname": "测试用户",
      "email": "test@example.com"
    }
  ]
}
```

### 6. 修改密码

- **接口**: `PUT /user/{userId}/password`
- **描述**: 用户修改密码，需要提供旧密码
- **认证**: 需要 Bearer Token
- **参数**:
  - `userId` (路径参数): 用户 ID
- **请求体**:

```json
{
  "oldPassword": "oldpassword123",
  "newPassword": "newpassword123"
}
```

- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": "密码修改成功"
}
```

### 7. 发送重置密码验证码

- **接口**: `POST /user/forgot-password/send-code`
- **描述**: 向邮箱或手机号发送重置密码验证码
- **请求体**:

```json
{
  "email": "test@example.com"
}
```

或

```json
{
  "phone": "13800138000"
}
```

- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": "验证码已发送"
}
```

### 8. 重置密码

- **接口**: `POST /user/forgot-password/reset`
- **描述**: 通过验证码重置密码，用于忘记密码场景
- **请求体**:

```json
{
  "email": "test@example.com",
  "code": "123456",
  "newPassword": "newpassword123"
}
```

- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": "密码重置成功"
}
```

### 9. 删除用户

- **接口**: `DELETE /user/{userId}`
- **描述**: 根据用户 ID 删除用户
- **认证**: 需要 Bearer Token
- **参数**:
  - `userId` (路径参数): 用户 ID
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": "用户删除成功"
}
```

### 10. 获取用户点赞的帖子列表

- **接口**: `GET /user/{userId}/likes`
- **描述**: 获取指定用户点赞的所有帖子
- **认证**: 需要 Bearer Token
- **参数**:
  - `userId` (路径参数): 用户 ID
  - `page` (查询参数): 页码，默认 1
  - `limit` (查询参数): 每页数量，默认 100
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "likeId": 1,
      "userId": 1,
      "postId": 1,
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

### 11. 获取用户收藏的帖子列表

- **接口**: `GET /user/{userId}/favorites`
- **描述**: 获取指定用户收藏的所有帖子，可指定收藏夹
- **认证**: 需要 Bearer Token
- **参数**:
  - `userId` (路径参数): 用户 ID
  - `folderId` (查询参数): 收藏夹 ID，可选
  - `page` (查询参数): 页码，默认 1
  - `limit` (查询参数): 每页数量，默认 100
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "favoriteId": 1,
      "userId": 1,
      "postId": 1,
      "folderId": 1,
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

### 12. 获取用户的收藏夹列表

- **接口**: `GET /user/{userId}/favorite-folders`
- **描述**: 获取指定用户的所有收藏夹
- **认证**: 需要 Bearer Token
- **参数**:
  - `userId` (路径参数): 用户 ID
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "folderId": 1,
      "userId": 1,
      "name": "我的收藏",
      "isPublic": false,
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

### 13. 创建收藏夹

- **接口**: `POST /user/{userId}/favorite-folders`
- **描述**: 为用户创建新的收藏夹
- **认证**: 需要 Bearer Token
- **参数**:
  - `userId` (路径参数): 用户 ID
- **请求体**:

```json
{
  "name": "新收藏夹"
}
```

- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "folderId": 1,
    "userId": 1,
    "name": "新收藏夹",
    "isPublic": false,
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

### 14. 删除收藏夹

- **接口**: `DELETE /user/{userId}/favorite-folders/{folderId}`
- **描述**: 删除指定的收藏夹
- **认证**: 需要 Bearer Token
- **参数**:
  - `userId` (路径参数): 用户 ID
  - `folderId` (路径参数): 收藏夹 ID
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": "收藏夹删除成功"
}
```

## 帖子管理接口

### 1. 获取帖子列表

- **接口**: `GET /posts`
- **描述**: 获取帖子列表，支持分页和过滤
- **参数**:
  - `page` (查询参数): 页码，默认 1
  - `limit` (查询参数): 每页数量，默认 10
  - `tag` (查询参数): 标签 ID，可选
  - `user_id` (查询参数): 用户 ID，可选
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
- **描述**: 根据帖子 ID 获取帖子详细信息
- **参数**:
  - `post_id` (路径参数): 帖子 ID
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
  - `post_id` (路径参数): 帖子 ID
- **请求体**:

```json
{
  "content": "更新后的帖子内容"
}
```

### 5. 删除帖子

- **接口**: `DELETE /posts/{post_id}`
- **描述**: 根据帖子 ID 删除帖子
- **参数**:
  - `post_id` (路径参数): 帖子 ID

## 评论与互动接口

### 1. 发表评论

- **接口**: `POST /posts/{post_id}/comments`
- **描述**: 对帖子发表评论
- **参数**:
  - `post_id` (路径参数): 帖子 ID
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
  - `post_id` (路径参数): 帖子 ID
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
  - `post_id` (路径参数): 帖子 ID
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
  - `post_id` (路径参数): 帖子 ID
- **请求体**:

```json
{
  "userId": 1
}
```

## 关注与红心接口

### 1. 关注用户

- **接口**: `POST /user/follow/{userId}`
- **描述**: 当前用户关注指定用户
- **认证**: 需要 Bearer Token
- **参数**:
  - `userId` (路径参数): 被关注用户 ID
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": "关注成功"
}
```

- **错误响应**:

```json
{
  "code": 400,
  "message": "不能关注自己",
  "data": null
}
```

### 2. 取消关注

- **接口**: `DELETE /user/unfollow/{userId}`
- **描述**: 当前用户取消关注指定用户
- **认证**: 需要 Bearer Token
- **参数**:
  - `userId` (路径参数): 被关注用户 ID
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": "取消关注成功"
}
```

### 3. 检查关注状态

- **接口**: `GET /user/follow/{userId}/check`
- **描述**: 检查当前用户是否关注了指定用户
- **认证**: 需要 Bearer Token
- **参数**:
  - `userId` (路径参数): 被检查的用户 ID
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "isFollowing": true
  }
}
```

### 4. 屏蔽用户

- **接口**: `POST /user/block/{userId}`
- **描述**: 当前用户屏蔽指定用户
- **认证**: 需要 Bearer Token
- **参数**:
  - `userId` (路径参数): 被屏蔽者 ID
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": "屏蔽成功"
}
```

- **错误响应**:

```json
{
  "code": 400,
  "message": "不能屏蔽自己",
  "data": null
}
```

### 5. 给用户点红心

- **接口**: `POST /heart/{user_id}`
- **描述**: 给指定用户点红心（自动关注）
- **参数**:
  - `user_id` (路径参数): 被红心用户 ID
- **请求体**:

```json
{
  "fromUserId": 1
}
```

### 6. 取消红心

- **接口**: `DELETE /heart/{user_id}`
- **描述**: 取消对指定用户的红心
- **参数**:
  - `user_id` (路径参数): 被红心用户 ID
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
  - `user_id` (查询参数): 当前用户 ID
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
  - `user_id` (查询参数): 当前用户 ID
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
- **认证**: 需要 Bearer Token
- **参数**:
  - `page` (查询参数): 页码，默认 1
  - `size` (查询参数): 每页数量，默认 20
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "conversationId": 1,
      "otherUserId": 2,
      "otherUserNickname": "对方昵称",
      "otherUserAvatar": "https://example.com/avatar.jpg",
      "lastMessage": "最后一条消息",
      "lastMessageType": "text",
      "lastMessageTime": "2024-01-01T00:00:00",
      "unreadCount": 3,
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

### 2. 创建会话

- **接口**: `POST /conversations`
- **描述**: 与指定用户创建或获取会话
- **认证**: 需要 Bearer Token
- **请求体**:

```json
{
  "userId": 2
}
```

- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "conversationId": 1,
    "otherUserId": 2,
    "otherUserNickname": "对方昵称",
    "otherUserAvatar": "https://example.com/avatar.jpg"
  }
}
```

### 3. 获取会话详情

- **接口**: `GET /conversations/{conversationId}`
- **描述**: 根据会话 ID 获取会话详细信息
- **认证**: 需要 Bearer Token
- **参数**:
  - `conversationId` (路径参数): 会话 ID
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "conversationId": 1,
    "otherUserId": 2,
    "otherUserNickname": "对方昵称",
    "otherUserAvatar": "https://example.com/avatar.jpg",
    "lastMessage": "最后一条消息",
    "unreadCount": 3
  }
}
```

### 4. 获取消息记录

- **接口**: `GET /conversations/{conversationId}/messages`
- **描述**: 获取指定会话的消息记录
- **认证**: 需要 Bearer Token
- **参数**:
  - `conversationId` (路径参数): 会话 ID
  - `page` (查询参数): 页码，默认 1
  - `size` (查询参数): 每页数量，默认 50
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "messageId": 1,
      "conversationId": 1,
      "senderId": 1,
      "senderNickname": "发送者昵称",
      "senderAvatar": "https://example.com/avatar.jpg",
      "contentType": "text",
      "content": "消息内容",
      "isRead": true,
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

### 5. 发送消息

- **接口**: `POST /conversations/{conversationId}/messages`
- **描述**: 在指定会话中发送消息
- **认证**: 需要 Bearer Token
- **参数**:
  - `conversationId` (路径参数): 会话 ID
- **请求体**:

```json
{
  "receiverId": 2,
  "contentType": "text",
  "content": "消息内容"
}
```

- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "messageId": 1,
    "conversationId": 1,
    "senderId": 1,
    "contentType": "text",
    "content": "消息内容",
    "isRead": false,
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

### 6. 标记消息为已读

- **接口**: `PUT /conversations/{conversationId}/read`
- **描述**: 标记指定会话的所有消息为已读
- **认证**: 需要 Bearer Token
- **参数**:
  - `conversationId` (路径参数): 会话 ID
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": "标记已读成功"
}
```

### 7. 获取未读消息数量

- **接口**: `GET /conversations/{conversationId}/unread-count`
- **描述**: 获取指定会话的未读消息数量
- **认证**: 需要 Bearer Token
- **参数**:
  - `conversationId` (路径参数): 会话 ID
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "unreadCount": 5
  }
}
```

### 8. 获取总未读消息数量

- **接口**: `GET /conversations/unread-count/total`
- **描述**: 获取当前用户所有会话的总未读消息数量
- **认证**: 需要 Bearer Token
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalUnreadCount": 10
  }
}
```

### 9. 设置消息免打扰

- **接口**: `PUT /conversations/{id}/mute`
- **描述**: 设置或取消会话的消息免打扰状态
- **认证**: 需要 Bearer Token
- **参数**:
  - `id` (路径参数): 会话 ID
- **请求体**:

```json
{
  "muted": true
}
```

- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": "已开启消息免打扰"
}
```

### 10. 设置置顶聊天

- **接口**: `PUT /conversations/{id}/pin`
- **描述**: 设置或取消会话的置顶状态
- **认证**: 需要 Bearer Token
- **参数**:
  - `id` (路径参数): 会话 ID
- **请求体**:

```json
{
  "pinned": true
}
```

- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": "已置顶聊天"
}
```

### 11. 清空聊天记录

- **接口**: `DELETE /conversations/{id}/messages`
- **描述**: 清空指定会话的所有消息
- **认证**: 需要 Bearer Token
- **参数**:
  - `id` (路径参数): 会话 ID
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": "聊天记录已清空"
}
```

### 12. 获取通知列表

- **接口**: `GET /notifications`
- **描述**: 获取用户通知列表
- **认证**: 需要 Bearer Token
- **参数**:
  - `page` (查询参数): 页码，默认 1
  - `size` (查询参数): 每页数量，默认 20
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

### 13. 获取未读通知数量

- **接口**: `GET /notifications/unread-count`
- **描述**: 获取当前用户的未读通知数量
- **认证**: 需要 Bearer Token
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "unreadCount": 5
  }
}
```

### 14. 标记通知为已读

- **接口**: `PUT /notifications/{notificationId}/read`
- **描述**: 标记指定通知为已读
- **认证**: 需要 Bearer Token
- **参数**:
  - `notificationId` (路径参数): 通知 ID
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": "标记已读成功"
}
```

### 15. 标记所有通知为已读

- **接口**: `PUT /notifications/read-all`
- **描述**: 标记当前用户的所有通知为已读
- **认证**: 需要 Bearer Token
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": "全部标记为已读"
}
```

### 16. 删除通知

- **接口**: `DELETE /notifications/{notificationId}`
- **描述**: 删除指定通知
- **认证**: 需要 Bearer Token
- **参数**:
  - `notificationId` (路径参数): 通知 ID
- **响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": "删除成功"
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
