# LinkMe 数据库设计文档

## 📋 目录

- [快速开始](#快速开始)
- [数据库表结构说明](#数据库表结构说明)
  - [基础表结构](#基础表结构)
  - [匹配机制相关表结构](#匹配机制相关表结构)

## 快速开始

### 环境要求

- MySQL 8.0 或更高版本
- 确保 MySQL 服务已启动

### 建库步骤

1. **执行初始化脚本**

   使用 MySQL 客户端（如 MySQL Workbench、Navicat、命令行等）执行以下 SQL 文件：

   ```bash
   # 方式1：使用命令行
   mysql -u root -p < src/main/resources/sql/new_data.sql

   # 方式2：使用 MySQL Workbench
   # 打开 MySQL Workbench -> File -> Open SQL Script -> 选择 new_data.sql -> 执行
   ```

2. **验证数据库创建**

   执行以下 SQL 验证数据库是否创建成功：

   ```sql
   USE linkme;
   SHOW TABLES;
   ```

   应该能看到所有表（共 34 张表）。

3. **测试数据**

   `new_data.sql` 文件已包含测试数据，包括：

   - 6 个测试用户（密码均为：123456）
   - 示例帖子、评论、点赞等数据
   - 匹配机制相关的初始数据

### 注意事项

- 如果数据库已存在，脚本会使用 `CREATE TABLE IF NOT EXISTS` 确保不会重复创建
- 执行脚本前请确保有足够的数据库权限
- 建议在开发环境使用，生产环境请根据实际情况调整

## 数据库表结构说明

### 基础表结构

#### 1. 用户表 (user)

**作用**: 存储用户基本信息

**主要字段**:

- `user_id`: 用户 ID（主键）
- `username`: 用户名（唯一）
- `email`: 邮箱（唯一）
- `phone`: 手机号（唯一）
- `password_hash`: 密码哈希（bcrypt 加密）
- `nickname`: 昵称
- `gender`: 性别（male/female/other）
- `birthday`: 生日
- `region`: 地区
- `avatar_url`: 头像 Base64 编码字符串
- `bio`: 个人简介
- `role`: 用户角色（customer/admin/moderator）
- `matching_questionnaire_completed`: 是否完成匹配问卷（默认 FALSE）
- `matching_questionnaire_completed_at`: 问卷完成时间（NULL 表示未完成）

#### 2. 标签定义表 (tag_def)

**作用**: 存储标签定义，支持用户标签和帖子标签

**主要字段**:

- `tag_id`: 标签 ID（主键）
- `name`: 标签名称（唯一）
- `tag_type`: 标签类型（post/user）
- `created_by`: 创建者 ID（NULL 为系统设定）

#### 3. 用户标签关联表 (user_tag)

**作用**: 关联用户和标签，实现多对多关系

**主要字段**:

- `user_id`: 用户 ID
- `tag_id`: 标签 ID
- 联合主键：`(user_id, tag_id)`

#### 4. 帖子表 (post)

**作用**: 存储帖子内容

**主要字段**:

- `post_id`: 帖子 ID（主键）
- `user_id`: 发布者 ID
- `content`: 帖子内容
- `topic`: 主题
- `privacy_level`: 隐私级别（public/followers/intimate/private）
- `created_at`: 创建时间
- `updated_at`: 更新时间

#### 5. 帖子图片表 (post_image)

**作用**: 存储帖子关联的图片

**主要字段**:

- `image_id`: 图片 ID（主键）
- `post_id`: 帖子 ID
- `image_url`: 图片 Base64 编码字符串
- `image_order`: 图片顺序

#### 6. 帖子标签关联表 (post_tag)

**作用**: 关联帖子和标签，实现多对多关系

**主要字段**:

- `post_id`: 帖子 ID
- `tag_id`: 标签 ID
- 联合主键：`(post_id, tag_id)`

#### 7. 评论表 (comment)

**作用**: 存储评论信息，支持回复评论（父子评论）

**主要字段**:

- `comment_id`: 评论 ID（主键）
- `post_id`: 帖子 ID
- `user_id`: 评论者 ID
- `content`: 评论内容
- `parent_id`: 父评论 ID（NULL 表示顶级评论）

#### 8. 点赞表 (like_post)

**作用**: 存储用户对帖子的点赞关系

**主要字段**:

- `like_id`: 点赞 ID（主键）
- `user_id`: 点赞用户 ID
- `post_id`: 被点赞的帖子 ID
- `created_at`: 点赞时间
- 唯一约束：`(user_id, post_id)` 确保同一用户不能重复点赞同一帖子

#### 9. 收藏夹表 (favorite_folder)

**作用**: 存储用户创建的收藏夹

**主要字段**:

- `folder_id`: 收藏夹 ID（主键）
- `user_id`: 用户 ID
- `name`: 收藏夹名称
- `is_public`: 是否公开
- 唯一约束：`(user_id, name)` 确保同一用户不能创建同名收藏夹

#### 10. 收藏表 (favorite)

**作用**: 存储用户收藏的帖子，支持分类到不同收藏夹

**主要字段**:

- `favorite_id`: 收藏 ID（主键）
- `user_id`: 用户 ID
- `post_id`: 帖子 ID
- `folder_id`: 收藏夹 ID
- 唯一约束：`(user_id, post_id, folder_id)` 确保同一帖子不能重复收藏到同一收藏夹

#### 11. 关注表 (follow)

**作用**: 存储用户之间的关注关系

**主要字段**:

- `follower_id`: 关注者 ID
- `followee_id`: 被关注者 ID
- `created_at`: 关注时间
- 联合主键：`(follower_id, followee_id)`
- 约束：不能关注自己

#### 12. 红心表 (heart)

**作用**: 存储用户之间的红心关系（用于匹配机制）

**主要字段**:

- `heart_id`: 红心 ID（主键）
- `from_user_id`: 发送者 ID
- `to_user_id`: 接收者 ID
- `created_at`: 发送时间
- 唯一约束：`(from_user_id, to_user_id)` 确保不能重复发送
- 约束：不能给自己发送红心

#### 13. 匹配表 (match_user)

**作用**: 存储用户之间的匹配关系（双方互相发送红心后形成匹配）

**主要字段**:

- `match_id`: 匹配 ID（主键）
- `user1_id`: 用户 1ID（较小的 ID）
- `user2_id`: 用户 2ID（较大的 ID）
- `status`: 状态（0-进行中，1-已结束）
- `created_at`: 匹配时间
- `updated_at`: 更新时间
- 约束：`user1_id < user2_id` 确保 ID 顺序一致

#### 14. 会话表 (conversation)

**作用**: 存储用户之间的聊天会话

**主要字段**:

- `conversation_id`: 会话 ID（主键）
- `user1_id`: 用户 1ID（较小的 ID）
- `user2_id`: 用户 2ID（较大的 ID）
- `user1_muted`: 用户 1 是否免打扰
- `user2_muted`: 用户 2 是否免打扰
- `user1_pinned`: 用户 1 是否置顶
- `user2_pinned`: 用户 2 是否置顶
- `created_at`: 创建时间
- 约束：`user1_id < user2_id` 确保 ID 顺序一致

#### 15. 消息表 (message)

**作用**: 存储聊天消息

**主要字段**:

- `message_id`: 消息 ID（主键）
- `conversation_id`: 会话 ID
- `sender_id`: 发送者 ID
- `content_type`: 内容类型（text/image/video/voice/file）
- `content`: 消息内容
- `is_read`: 是否已读
- `created_at`: 发送时间

#### 16. 通知表 (notification)

**作用**: 存储系统通知

**主要字段**:

- `notification_id`: 通知 ID（主键）
- `user_id`: 接收用户 ID
- `type`: 通知类型（message/follow/heart/like/comment/match）
- `actor_id`: 操作者 ID
- `related_id`: 关联实体 ID
- `related_type`: 关联实体类型
- `title`: 通知标题
- `content`: 通知内容
- `is_read`: 是否已读
- `created_at`: 通知时间

#### 17. 验证码表 (verification_code)

**作用**: 存储验证码信息，用于注册、登录、重置密码等场景

**主要字段**:

- `code_id`: 验证码 ID（主键）
- `user_id`: 用户 ID（可为 NULL）
- `code`: 验证码
- `type`: 类型（email/phone）
- `purpose`: 用途（register/login/reset_password）
- `expire_at`: 过期时间
- `is_used`: 是否已使用
- `created_at`: 发送时间

#### 18. 隐私设置表 (privacy_setting)

**作用**: 存储用户的隐私设置

**主要字段**:

- `privacy_id`: 隐私设置 ID（主键）
- `user_id`: 用户 ID（唯一）
- `allow_match`: 是否允许匹配
- `allow_private_messages`: 允许私聊（all/followed/none）
- `allow_profile_view`: 允许查看个人资料（all/followed/none）

#### 19. 内容审核日志表 (audit_log)

**作用**: 存储内容审核日志

**主要字段**:

- `id`: 日志 ID（主键）
- `target_id`: 被审核内容 ID
- `target_type`: 被审核内容类型（0-帖子, 1-评论, 2-用户资料）
- `auditor_id`: 审核员 ID（0 代表系统自动审核）
- `action`: 执行操作（PASS/BLOCK/DELETE）
- `reason`: 原因备注
- `create_time`: 创建时间

#### 20. 屏蔽表 (block)

**作用**: 存储用户之间的屏蔽关系

**主要字段**:

- `blocker_id`: 屏蔽者 ID
- `blocked_id`: 被屏蔽者 ID
- `created_at`: 屏蔽时间
- 联合主键：`(blocker_id, blocked_id)`
- 约束：不能屏蔽自己

### 匹配机制相关表结构

#### 21. 爱好分类表 (hobby_category)

**作用**: 存储爱好分类，用于匹配机制

**主要字段**:

- `category_id`: 分类 ID（主键）
- `name`: 分类名称（唯一）
- `icon`: 分类图标
- `display_order`: 显示顺序

#### 22. 爱好表 (hobby)

**作用**: 存储具体的爱好选项

**主要字段**:

- `hobby_id`: 爱好 ID（主键）
- `category_id`: 分类 ID
- `name`: 爱好名称
- `display_order`: 显示顺序
- 唯一约束：`(category_id, name)` 确保同一分类下不能有同名爱好

#### 23. 用户爱好关联表 (user_hobby)

**作用**: 关联用户和爱好，实现多对多关系

**主要字段**:

- `user_id`: 用户 ID
- `hobby_id`: 爱好 ID
- 联合主键：`(user_id, hobby_id)`

#### 24. 性格特质分类表 (personality_trait_category)

**作用**: 存储性格特质分类

**主要字段**:

- `category_id`: 分类 ID（主键）
- `name`: 分类名称
- `description`: 分类描述
- `trait_type`: 特质类型（self-自身特质，ideal-理想对象特质）
- `display_order`: 显示顺序
- 唯一约束：`(name, trait_type)` 确保同一类型下不能有同名分类

#### 25. 性格特质选项表 (personality_trait_option)

**作用**: 存储性格特质的具体选项

**主要字段**:

- `option_id`: 选项 ID（主键）
- `category_id`: 分类 ID
- `name`: 选项名称
- `display_order`: 显示顺序
- 唯一约束：`(category_id, name)` 确保同一分类下不能有同名选项

#### 26. 用户性格特质表 (user_personality)

**作用**: 关联用户和性格特质选项

**主要字段**:

- `user_id`: 用户 ID
- `option_id`: 选项 ID
- 联合主键：`(user_id, option_id)`

#### 27. 关系品质表 (relationship_quality)

**作用**: 存储关系品质选项，用于匹配机制

**主要字段**:

- `quality_id`: 品质 ID（主键）
- `name`: 品质名称（唯一）
- `display_order`: 显示顺序

#### 28. 用户关系品质关联表 (user_relationship_quality)

**作用**: 关联用户和关系品质

**主要字段**:

- `user_id`: 用户 ID
- `quality_id`: 品质 ID
- 联合主键：`(user_id, quality_id)`

#### 29. 关系模式表 (relationship_mode)

**作用**: 存储关系模式选项

**主要字段**:

- `mode_id`: 模式 ID（主键）
- `name`: 模式名称（唯一）
- `description`: 模式描述
- `display_order`: 显示顺序

#### 30. 沟通期待表 (communication_expectation)

**作用**: 存储沟通期待选项

**主要字段**:

- `expectation_id`: 期待 ID（主键）
- `name`: 期待名称（唯一）
- `description`: 期待描述
- `display_order`: 显示顺序

#### 31. 匹配维度表 (matching_dimension)

**作用**: 存储匹配维度定义

**主要字段**:

- `dimension_id`: 维度 ID（主键）
- `name`: 维度名称（唯一）
- `code`: 维度代码（唯一）
- `display_order`: 显示顺序

#### 32. 用户匹配偏好表 (user_matching_preference)

**作用**: 存储用户的匹配偏好设置

**主要字段**:

- `preference_id`: 偏好 ID（主键）
- `user_id`: 用户 ID（唯一）
- `age_min`: 最小年龄要求
- `age_max`: 最大年龄要求
- `age_unlimited`: 是否无年龄限制
- `distance_preference`: 关系距离要求（same_city/same_city_or_remote/unlimited）
- `relationship_mode_id`: 理想关系模式 ID
- `communication_expectation_id`: 沟通期待 ID
- `additional_requirements`: 其他未被覆盖的交友要求

#### 33. 用户匹配必须维度关联表 (user_matching_must_dimension)

**作用**: 关联用户和必须匹配的维度

**主要字段**:

- `user_id`: 用户 ID
- `dimension_id`: 维度 ID
- 联合主键：`(user_id, dimension_id)`

#### 34. 用户匹配优先维度关联表 (user_matching_priority_dimension)

**作用**: 关联用户和优先匹配的维度（带优先级）

**主要字段**:

- `user_id`: 用户 ID
- `dimension_id`: 维度 ID
- `priority_order`: 优先级顺序
- 联合主键：`(user_id, dimension_id)`

## 表关系说明

### 核心关系

1. **用户系统**

   - `user` ← `user_tag` → `tag_def`
   - `user` ← `privacy_setting`
   - `user` ← `verification_code`

2. **帖子系统**

   - `user` ← `post` → `post_tag` → `tag_def`
   - `post` ← `post_image`
   - `post` ← `comment` ← `user`
   - `post` ← `like_post` ← `user`
   - `post` ← `favorite` ← `favorite_folder` ← `user`

3. **社交关系**

   - `user` ← `follow` → `user`（关注关系）
   - `user` ← `heart` → `user`（红心关系）
   - `user` ← `match_user` → `user`（匹配关系）
   - `user` ← `block` → `user`（屏蔽关系）

4. **聊天系统**

   - `user` ← `conversation` → `user`
   - `conversation` ← `message` ← `user`

5. **匹配机制**
   - `user` ← `user_hobby` → `hobby` → `hobby_category`
   - `user` ← `user_personality` → `personality_trait_option` → `personality_trait_category`
   - `user` ← `user_relationship_quality` → `relationship_quality`
   - `user` ← `user_matching_preference` → `relationship_mode`
   - `user` ← `user_matching_preference` → `communication_expectation`
   - `user` ← `user_matching_must_dimension` → `matching_dimension`
   - `user` ← `user_matching_priority_dimension` → `matching_dimension`

## 注意事项

1. **表名规范**

   - 所有表名使用小写下划线命名
   - `like_post` 和 `match_user` 表名避免了 MySQL 保留字冲突

2. **外键约束**

   - 所有外键都设置了 `ON DELETE CASCADE`，删除主表记录时会自动删除关联记录
   - 部分外键设置了 `ON DELETE SET NULL`，删除主表记录时会将外键设为 NULL

3. **索引优化**

   - 所有外键字段都建立了索引
   - 常用查询字段（如 `created_at`、`user_id` 等）都建立了索引
   - 联合索引用于优化多条件查询

4. **数据完整性**
   - 使用唯一约束防止重复数据
   - 使用检查约束（CHECK）确保数据有效性
   - 使用枚举类型（ENUM）限制字段取值范围
