# LinkMe 交友聊天社交软件后端

## 📚 文档导航

- **[启动指南](./启动指南.md)** - 详细的环境配置和启动步骤
- **[API 文档](./API.md)** - 完整的 API 接口文档和使用说明
- **[数据库设计文档](./SQL.md)** - 数据库表结构说明和建库指南

## 项目简介

LinkMe 是一个现代化的交友聊天社交软件后端系统，基于 Spring Boot 3 开发，提供完整的社交功能包括用户管理、帖子发布、匹配交友、聊天通信等核心功能。

## 技术栈

- **框架**: Spring Boot 3.1.6
- **数据库**: MySQL 8.0+
- **ORM**: MyBatis 3.0.1
- **缓存**: Redis
- **认证**: JWT
- **文档**: Swagger/OpenAPI 3.0
- **构建工具**: Maven
- **Java 版本**: 17

## 核心功能

### 1. 用户管理

- 用户注册/登录
- 个人信息管理
- 头像上传
- 隐私设置

### 2. 帖子系统

- 发布帖子（支持图片、标签）
- 帖子浏览和搜索
- 评论和点赞
- 收藏功能（支持自定义收藏夹）

### 3. 匹配交友

- 基于标签的智能匹配
- 红心机制
- 关注系统
- 亲密关系管理

### 4. 聊天通信

- 私聊功能
- 多媒体消息（文字、图片、视频）
- 语音/视频通话
- 消息通知

### 5. 通知系统

- 实时通知推送
- 多种通知类型
- 未读消息管理

## 项目结构

```
src/main/java/com/linkme/backend/
├── LinkMeApplication.java          # 启动类
├── common/                         # 公共类
│   ├── R.java                      # 统一响应格式
│   ├── GlobalExceptionHandler.java # 全局异常处理
│   └── JwtUtil.java                # JWT工具类
├── config/                         # 配置类
│   ├── JwtInterceptor.java         # JWT拦截器
│   ├── WebMvcConfig.java           # Web配置
│   └── OpenApiConfig.java          # API文档配置
├── controller/                     # 控制器层
│   ├── UserController.java         # 用户控制器
│   └── PostController.java          # 帖子控制器
├── service/                        # 服务层
│   ├── UserService.java            # 用户服务接口
│   ├── PostService.java            # 帖子服务接口
│   └── impl/                       # 服务实现类
├── mapper/                         # 数据访问层
│   ├── UserMapper.java             # 用户Mapper
│   └── PostMapper.java             # 帖子Mapper
└── entity/                         # 实体类
    ├── User.java                   # 用户实体
    ├── Post.java                   # 帖子实体
    └── ...
```

## 数据库设计

详细的数据库表结构说明、建库步骤和每个表的作用，请参考 [数据库设计文档](./SQL.md)。

## 快速开始

详细的启动步骤请参考 [启动指南](./启动指南.md)。

**快速命令：**

```bash
# 进入项目目录
cd LinkMe-backend

# 启动服务
mvn spring-boot:run
```

**访问 API 文档：**

- Swagger UI: <http://localhost:8080/swagger-ui/index.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>

完整的 API 接口文档请参考 [API 文档](./API.md)。

### 在 Swagger UI 中使用 Token 认证

1. **获取 Token**

   - 首先调用 `/user/register`（注册）或 `/user/login`（登录）接口
   - 从响应的 `data.token` 字段中复制 token

2. **设置 Token**

   - 在 Swagger UI 页面右上角找到 🔒 **Authorize** 按钮并点击
   - 在弹出的对话框中：
     - 找到 `bearerAuth` 输入框
     - **输入 token（不需要包含 'Bearer' 前缀）**
     - 点击 **Authorize** 按钮
     - 点击 **Close** 关闭对话框

3. **测试需要认证的接口**
   - 现在所有标记了 🔒 图标的接口都会自动在请求头中包含 token
   - token 会被持久化保存，刷新页面后仍然有效

**注意事项：**

- 注册和登录接口不需要 token（它们是公开接口）
- 其他接口（如获取用户信息、更新用户信息等）都需要 token
- token 有效期由配置文件中的 `jwt.expire-seconds` 设置（默认 24 小时）

## 开发规范

### 1. 代码规范

- 使用小驼峰命名法
- 每个函数都要有注释
- 统一使用下划线命名数据库字段
- API 路径使用下划线连接

### 2. 数据库规范

- 表名使用下划线命名
- 字段名使用下划线命名
- 主键统一使用自增 ID
- 外键约束完整

### 3. API 规范

- 遵循 RESTful API 设计
- 统一响应格式 `{code, message, data}`
- 完善的错误处理
- 详细的 API 文档（见 [API 文档](./API.md)）

## 部署说明

### 生产环境配置

1. 修改数据库连接配置
2. 配置 Redis 连接
3. 设置 JWT 密钥
4. 配置日志级别
5. 设置服务器端口

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交代码
4. 创建 Pull Request

## 许可证

MIT License

## 联系方式

- 项目地址: <https://github.com/linkme/>
- 问题反馈:
- 邮箱:

## 更新日志

### v1.0.0 (2025-10-23)

- 初始版本发布
- 完成数据库设计
- 完善 API 文档
- 功能初始搭建

### v1.1.0 (2025.10.31)

**用户管理功能**

- 新增密码修改和忘记密码功能
- 新增密码、邮箱、手机号验证器
- 新增验证码服务
- 新增用户列表和删除用户接口
- 完善 Swagger API 文档和 Token 认证配置
- 优化参数验证和异常处理

**帖子管理功能**

- 新增完整的帖子 CRUD 功能（创建、查询、编辑、删除）
- 新增支持图片和标签的帖子创建功能
- 新增帖子详情聚合功能（图片、标签、点赞数）
- 新增评论功能（发表评论、获取评论列表）
- 新增点赞功能（点赞、取消点赞）
- 新增帖子列表查询功能（支持分页、按用户 ID、按标签筛选）
- 新增 PostCreateRequest 和 PostDetailResponse DTO 类

### v1.2.0 (2025-11-02)

- 新增 WebSocket 实时通信支持
- 新增初步会话管理和消息发送功能
- 新增初步消息已读/未读状态管理

### v1.2.1 (2025-11-04)

- 完善聊天模块会话管理
- 完善消息管理，支持未读消息数量统计
- 实现 JWT 认证和实时消息推送

### v1.2.2 (2025-12-04)

- 新增关注、屏蔽用户接口
- 新增取消、检查关注状态接口
- 新增 Block 实体类和 BlockMapper 数据访问层
- 新增消息免打扰、置顶聊天、清空聊天记录功能
- 完善会话管理，支持用户级别的免打扰和置顶设置
- 创建 block 表用于存储用户屏蔽关系
- 为 conversation 表添加免打扰和置顶字段
- 完善 API 文档
