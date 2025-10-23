# LinkMe 交友聊天社交软件后端

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

### 核心表结构

1. **用户表 (user)**: 存储用户基本信息
2. **帖子表 (post)**: 存储帖子内容
3. **评论表 (comment)**: 存储评论信息
4. **点赞表 (like)**: 存储点赞关系
5. **关注表 (follow)**: 存储关注关系
6. **红心表 (heart)**: 存储红心关系
7. **匹配表 (match)**: 存储匹配关系
8. **会话表 (conversation)**: 存储聊天会话
9. **消息表 (message)**: 存储聊天消息
10. **通知表 (notification)**: 存储通知信息

## API 接口设计

### 用户管理接口

- `GET /user/{user_id}/info` - 获取用户信息
- `PUT /user/{user_id}/info` - 更新用户信息
- `POST /user/register` - 用户注册
- `POST /user/login` - 用户登录

### 帖子管理接口

- `GET /posts` - 获取帖子列表
- `POST /posts` - 创建帖子
- `GET /posts/{post_id}` - 获取帖子详情
- `PUT /posts/{post_id}` - 编辑帖子
- `DELETE /posts/{post_id}` - 删除帖子

### 评论与互动接口

- `POST /posts/{post_id}/comments` - 发表评论
- `GET /posts/{post_id}/comments` - 获取评论列表
- `POST /posts/{post_id}/like` - 点赞帖子
- `DELETE /posts/{post_id}/like` - 取消点赞

### 关注与红心接口

- `POST /follow/{user_id}` - 关注用户
- `DELETE /follow/{user_id}` - 取消关注
- `POST /heart/{user_id}` - 给用户点红心
- `DELETE /heart/{user_id}` - 取消红心

### 匹配与推荐接口

- `GET /matches` - 获取匹配列表
- `GET /recommended_users` - 获取推荐用户

### 聊天与通知接口

- `GET /conversations` - 获取会话列表
- `POST /conversations` - 创建会话
- `GET /conversations/{conversation_id}/messages` - 获取消息记录
- `POST /conversations/{conversation_id}/messages` - 发送消息
- `GET /notifications` - 获取通知列表

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 安装步骤

1. **克隆项目**

```bash
git clone <repository-url>
cd LinkMe-backend
```

2. **配置数据库**

```bash
# 创建数据库
mysql -u root -p < src/main/resources/sql/init.sql
```

3. **修改配置文件**

```yaml
# src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/linkme?useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379

jwt:
  secret: your_jwt_secret_key
  expire-seconds: 86400
```

4. **运行项目**

```bash
mvn clean install
mvn spring-boot:run
```

5. **访问 API 文档**

```
http://localhost:8080/swagger-ui.html
```

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
- 统一响应格式
- 完善的错误处理
- 详细的 API 文档

## 部署说明

### 生产环境配置

1. 修改数据库连接配置
2. 配置 Redis 连接
3. 设置 JWT 密钥
4. 配置日志级别
5. 设置服务器端口

### Docker 部署

```dockerfile
FROM openjdk:17-jre-slim
COPY target/linkme-backend-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交代码
4. 创建 Pull Request

## 许可证

MIT License

## 联系方式

- 项目地址: https://github.com/linkme/
- 问题反馈:
- 邮箱:

## 更新日志

### v1.0.0 (2024-01-01)

- 初始版本发布
- 完成核心功能开发
- 完善 API 文档
- 添加单元测试
