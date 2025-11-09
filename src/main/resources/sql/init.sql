-- LinkMe交友聊天社交软件数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS linkme CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE linkme;

-- 1. 用户表（User）
CREATE TABLE IF NOT EXISTS user (
    user_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    password_hash CHAR(60) NOT NULL COMMENT '密码哈希',   # 使用bcrypt加密
    nickname VARCHAR(50) COMMENT '昵称',
    gender ENUM('male', 'female', 'other')  COMMENT '性别',
    birthday DATE COMMENT '生日',
    region VARCHAR(100) DEFAULT '' COMMENT '地区',
    avatar_url VARCHAR(255) DEFAULT '' COMMENT '头像URL',
    bio TEXT COMMENT '简介',
    role ENUM('customer', 'admin', 'moderator') DEFAULT 'customer' COMMENT '用户角色',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_created_at (created_at),
    INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';


-- 2. 标签定义表（TagDef）
CREATE TABLE IF NOT EXISTS tag_def (
    tag_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
    name VARCHAR(50) UNIQUE NOT NULL COMMENT '标签名称',
    created_by INT DEFAULT NULL COMMENT '创建者ID，NULL为系统设定',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    tag_type ENUM('post', 'user')  NOT NULL COMMENT '标签类型',
    FOREIGN KEY (`created_by`) REFERENCES `user`(`user_id`) ON DELETE SET NULL,
    UNIQUE KEY `uk_name_type` (`name`, `tag_type`),
    INDEX `idx_tag_type` (`tag_type`),
    INDEX `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签定义表';

-- 3. 用户标签关联表（UserTag）
CREATE TABLE IF NOT EXISTS user_tag (
    user_id INT NOT NULL COMMENT '用户ID',
    tag_id INT NOT NULL COMMENT '标签ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (user_id, tag_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag_def(tag_id) ON DELETE CASCADE,
    INDEX `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户标签关联表';

-- 4. 帖子表（Post）
CREATE TABLE IF NOT EXISTS post (
    post_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '帖子ID',
    user_id INT NOT NULL COMMENT '用户ID',
    content TEXT NOT NULL COMMENT '内容',
    topic VARCHAR(100) DEFAULT NULL COMMENT '主题',
    privacy_level ENUM('public', 'followers', 'intimate', 'private') DEFAULT 'public' COMMENT '隐私级别',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_privacy_level` (`privacy_level`),
    INDEX `idx_topic` (`topic`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子表';

-- 5. 帖子图片表（PostImage）
CREATE TABLE IF NOT EXISTS post_image (
    image_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '图片ID',
    post_id INT NOT NULL COMMENT '帖子ID',
    image_url VARCHAR(255) NOT NULL COMMENT '图片URL',
    image_order INT DEFAULT 0 COMMENT '图片顺序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (post_id) REFERENCES post(post_id) ON DELETE CASCADE,
    INDEX `idx_post_id` (`post_id`),
    INDEX `idx_image_order` (`image_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子图片表';

-- 6. 帖子标签关联表（PostTag）
CREATE TABLE IF NOT EXISTS post_tag (
    post_id INT NOT NULL COMMENT '帖子ID',
    tag_id INT NOT NULL COMMENT '标签ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES post(post_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag_def(tag_id) ON DELETE CASCADE,
    INDEX `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子标签关联表';

-- 7. 评论表（Comment）
CREATE TABLE IF NOT EXISTS comment (
    comment_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    post_id INT NOT NULL COMMENT '帖子ID',
    user_id INT NOT NULL COMMENT '用户ID',
    content TEXT NOT NULL COMMENT '内容',
    parent_id INT DEFAULT NULL COMMENT '父评论ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (post_id) REFERENCES post(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES comment(comment_id) ON DELETE CASCADE,
    INDEX idx_post_id_created_at (post_id, created_at),
    INDEX idx_user_id_created_at (user_id, created_at),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 8. 点赞表（Like）
CREATE TABLE IF NOT EXISTS `like` (
    like_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
    user_id INT NOT NULL COMMENT '用户ID',
    post_id INT NOT NULL COMMENT '帖子ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES post(post_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_post (user_id, post_id),
    INDEX idx_post_id_created_at (post_id, created_at),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞表';

-- 9. 收藏夹表（FavoriteFolder）
CREATE TABLE IF NOT EXISTS favorite_folder (
    folder_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏夹ID',
    user_id INT NOT NULL COMMENT '用户ID',
    name VARCHAR(50) NOT NULL COMMENT '收藏夹名称',
    is_public BOOLEAN DEFAULT FALSE COMMENT '是否公开',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    UNIQUE KEY `uk_user_folder_name` (`user_id`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏夹表';

-- 10. 收藏表（Favorite）
CREATE TABLE IF NOT EXISTS favorite (
    favorite_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    user_id INT NOT NULL COMMENT '用户ID',
    post_id INT NOT NULL COMMENT '帖子ID',
    folder_id INT NOT NULL COMMENT '收藏夹ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES post(post_id) ON DELETE CASCADE,
    FOREIGN KEY (folder_id) REFERENCES favorite_folder(folder_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_post_folder (user_id, post_id, folder_id),
    INDEX idx_post_id (post_id),
    INDEX idx_folder_id (folder_id),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- 11. 关注表（Follow）
CREATE TABLE IF NOT EXISTS follow (
    follower_id INT NOT NULL COMMENT '关注者ID',
    followee_id INT NOT NULL COMMENT '被关注者ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    PRIMARY KEY (follower_id, followee_id),
    FOREIGN KEY (follower_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (followee_id) REFERENCES user(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_follow_not_self CHECK (follower_id != followee_id),
    INDEX idx_followee_id (followee_id),
    INDEX idx_follower_id (follower_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注表';

-- 12. 红心表（Heart）
CREATE TABLE IF NOT EXISTS heart (
    heart_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '红心ID',
    from_user_id INT NOT NULL COMMENT '发送者ID',
    to_user_id INT NOT NULL COMMENT '接收者ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (from_user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (to_user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    UNIQUE KEY `uk_from_to_user` (`from_user_id`, `to_user_id`),
    INDEX `idx_to_user_id` (`to_user_id`),
    INDEX `idx_from_user_id` (`from_user_id`),
    CONSTRAINT `chk_heart_not_self` CHECK (`from_user_id` != `to_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='红心表';

-- 13. 匹配表（Match）
CREATE TABLE IF NOT EXISTS `match` (
    match_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '匹配ID',
    user1_id INT NOT NULL COMMENT '用户1ID',
    user2_id INT NOT NULL COMMENT '用户2ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '匹配时间',
    status INT DEFAULT 0 COMMENT '状态：0-进行中，1-已结束',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user1_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (user2_id) REFERENCES user(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_user_order CHECK (user1_id < user2_id),
    UNIQUE KEY uk_user_pair (user1_id, user2_id),
    INDEX idx_user1_id (user1_id),
    INDEX idx_user2_id (user2_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='匹配表';

-- 14. 会话表（Conversation）
CREATE TABLE IF NOT EXISTS conversation (
    conversation_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
    user1_id INT NOT NULL COMMENT '用户1ID',
    user2_id INT NOT NULL COMMENT '用户2ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user1_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (user2_id) REFERENCES user(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_conversation_user_order CHECK (user1_id < user2_id),
    UNIQUE KEY uk_user_pair (user1_id, user2_id),
    INDEX `idx_user1_id` (`user1_id`),
    INDEX `idx_user2_id` (`user2_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话表';

-- 15. 消息表（Message） 1.2.1更新了content_type字段，增加了voice和file类型
CREATE TABLE IF NOT EXISTS message (
    message_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    conversation_id INT NOT NULL COMMENT '会话ID',
    sender_id INT NOT NULL COMMENT '发送者ID',
    content_type ENUM('text', 'image', 'video', 'voice', 'file') DEFAULT 'text' COMMENT '内容类型',
    content TEXT COMMENT '内容',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    FOREIGN KEY (conversation_id) REFERENCES conversation(conversation_id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES user(user_id) ON DELETE CASCADE,
    INDEX `idx_conversation_created` (`conversation_id`, `created_at`),
    INDEX `idx_sender_id` (`sender_id`),
    INDEX `idx_is_read` (`is_read`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- 16. 通知表（Notification）
CREATE TABLE IF NOT EXISTS notification (
    notification_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    user_id INT NOT NULL COMMENT '用户ID',
    type ENUM('message', 'follow', 'heart', 'like', 'comment', 'match') NOT NULL COMMENT '通知类型',
    actor_id INT NOT NULL COMMENT '操作者ID',
    related_id INT DEFAULT NULL COMMENT '关联实体ID',
    related_type VARCHAR(50) DEFAULT NULL COMMENT '关联实体类型',
    title VARCHAR(255) DEFAULT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '通知时间',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (actor_id) REFERENCES user(user_id) ON DELETE CASCADE,
    INDEX idx_user_created (user_id, created_at),
    INDEX idx_user_read (user_id, is_read, created_at),
    INDEX idx_actor_id (actor_id),
    INDEX `idx_type` (`type`),
    INDEX `idx_related` (`related_id`, `related_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- 17. 验证码表（VerificationCode）
CREATE TABLE IF NOT EXISTS verification_code (
    code_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '验证码ID',
    user_id INT DEFAULT NULL COMMENT '用户ID',
    code VARCHAR(10) NOT NULL COMMENT '验证码',
    type ENUM('email', 'phone') NOT NULL COMMENT '类型',
    purpose ENUM('register', 'login', 'reset_password') DEFAULT 'register' COMMENT '用途',
    expire_at DATETIME NOT NULL COMMENT '过期时间',
    is_used BOOLEAN DEFAULT FALSE COMMENT '是否已使用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    INDEX `idx_code_type` (`code`, `type`),
    INDEX `idx_expire_at` (`expire_at`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_is_used` (`is_used`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='验证码表';

-- 18. 隐私设置表（PrivacySetting）
CREATE TABLE IF NOT EXISTS privacy_setting (
    privacy_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '隐私设置ID',
    user_id INT NOT NULL COMMENT '用户ID',
    allow_match BOOLEAN DEFAULT TRUE COMMENT '是否允许匹配',
    allow_private_messages ENUM('all', 'followed', 'none') DEFAULT 'followed' COMMENT '允许私聊',
    allow_profile_view ENUM('all', 'followed', 'none') DEFAULT 'all' COMMENT '允许查看个人资料',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_id (user_id),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='隐私设置表';

-- 19. 内容审核日志表 (AuditLog)
CREATE TABLE IF NOT EXISTS `audit_log` (
   `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
   `target_id` BIGINT NOT NULL COMMENT '被审核内容ID',
   `target_type` TINYINT NOT NULL COMMENT '被审核内容类型：0-帖子, 1-评论, 2-用户资料',
   `auditor_id` BIGINT NOT NULL DEFAULT 0 COMMENT '审核员ID，0代表系统自动审核',
   `action` ENUM('PASS', 'BLOCK', 'DELETE') NOT NULL COMMENT '执行操作',
   `reason` VARCHAR(255) DEFAULT NULL COMMENT '原因备注',
   `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   INDEX `idx_target` (`target_id`, `target_type`),
   INDEX `idx_auditor_id` (`auditor_id`),
   INDEX `idx_create_time` (`create_time`),
   INDEX `idx_action` (`action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容审核日志表';


-- LinkMe交友聊天社交软件初始数据
USE linkme;

-- 1. 插入用户数据
INSERT INTO user (username, email, phone, password_hash, nickname, gender, birthday, region, avatar_url, bio, role) VALUES
('admin1', 'admin@linkme.com', '13800000000', '$2b$10$examplehashedpassword123456789012', '系统管理员', 'male', '1990-01-01', '北京', '/avatars/admin.jpg', '系统管理员账号', 'admin'),
('xiaoming', 'xiaoming@linkme.com', '13800000001', '$2b$10$examplehashedpassword123456789012', '小明', 'male', '1995-05-15', '上海', '/avatars/xiaoming.jpg', '喜欢旅游和摄影的程序员', 'customer'),
('xiaohong', 'xiaohong@linkme.com', '13800000002', '$2b$10$examplehashedpassword123456789012', '小红', 'female', '1998-08-20', '广州', '/avatars/xiaohong.jpg', '热爱音乐和美食的设计师', 'customer'),
('xiaoli', 'xiaoli@linkme.com', '13800000003', '$2b$10$examplehashedpassword123456789012', '小丽', 'female', '1996-03-10', '深圳', '/avatars/xiaoli.jpg', '健身达人和读书爱好者', 'customer'),
('david', 'david@linkme.com', '13800000004', '$2b$10$examplehashedpassword123456789012', '大卫', 'male', '1993-11-25', '杭州', '/avatars/david.jpg', '创业者和投资人', 'customer'),
('lily', 'lily@linkme.com', '13800000005', '$2b$10$examplehashedpassword123456789012', '莉莉', 'female', '1997-07-07', '成都', '/avatars/lily.jpg', '宠物医生和动物保护者', 'customer');

-- 2. 插入标签定义
INSERT INTO tag_def (name, created_by, tag_type) VALUES
-- 用户标签
('程序员', 1, 'user'),
('设计师', 1, 'user'),
('摄影师', 1, 'user'),
('音乐爱好者', 1, 'user'),
('美食家', 1, 'user'),
('旅行达人', 1, 'user'),
('健身爱好者', 1, 'user'),
('读书人', 1, 'user'),
('创业者', 1, 'user'),
('投资人', 1, 'user'),
('技术分享', 1, 'post'),
('生活感悟', 1, 'post'),
('美食推荐', 1, 'post'),
('旅行见闻', 1, 'post'),
('摄影作品', 1, 'post'),
('音乐推荐', 1, 'post'),
('健身心得', 1, 'post'),
('读书笔记', 1, 'post');

-- 3. 插入用户标签关联
INSERT INTO user_tag (user_id, tag_id) VALUES
(2, 1), (2, 3), (2, 6),  -- 小明：程序员、摄影师、旅行达人
(3, 2), (3, 4), (3, 5),  -- 小红：设计师、音乐爱好者、美食家
(4, 7), (4, 8),          -- 小丽：健身爱好者、读书人
(5, 9), (5, 10),         -- 大卫：创业者、投资人
(6, 5), (6, 8);          -- 莉莉：美食家、读书人

-- 4. 插入帖子数据
INSERT INTO post (user_id, content, privacy_level) VALUES
(2, '今天分享一个编程小技巧：使用Python处理数据时，pandas库真的超级好用！', 'public'),
(3, '刚刚设计了一款新的UI界面，大家觉得怎么样？简约而不简单！', 'public'),
(4, '坚持健身一个月了，感觉整个人都精神了很多！继续加油！', 'public'),
(5, '创业路上的思考：产品定位和市场需求的匹配有多重要？', 'public'),
(6, '推荐一家超棒的意大利餐厅，他们家的提拉米苏绝了！', 'public'),
(2, '最近去西藏旅行，布达拉宫的景色真的太震撼了！', 'public'),
(3, '发现了一首超好听的歌，单曲循环一整天都不腻！', 'public');

-- 5. 插入帖子图片
INSERT INTO post_image (post_id, image_url, image_order) VALUES
(2, '/images/design1.jpg', 1),
(2, '/images/design2.jpg', 2),
(6, '/images/tibet1.jpg', 1),
(6, '/images/tibet2.jpg', 2);

-- 6. 插入帖子标签关联
INSERT INTO post_tag (post_id, tag_id) VALUES
(1, 11), -- 技术分享
(2, 12), -- 生活感悟
(3, 17), -- 健身心得
(4, 12), -- 生活感悟
(5, 13), -- 美食推荐
(6, 14), -- 旅行见闻
(7, 16); -- 音乐推荐

-- 7. 插入评论数据
INSERT INTO comment (post_id, user_id, content, parent_id) VALUES
(1, 3, 'Python确实很好用，我最近也在学习！', NULL),
(1, 4, '能推荐一些学习资源吗？', NULL),
(1, 2, '@小丽 推荐你看《利用Python进行数据分析》这本书', 3),
(2, 2, '设计得很棒！配色很舒服', NULL),
(3, 3, '健身真的会让人上瘾，一起坚持！', NULL),
(5, 4, '在哪家餐厅？求地址！', NULL),
(5, 6, '@小丽 在春熙路的「意式风情」餐厅', 7);

-- 8. 插入点赞数据
INSERT INTO `like` (user_id, post_id) VALUES
(3, 1), (4, 1), (5, 1),
(2, 2), (4, 2),
(2, 3), (3, 3), (5, 3),
(2, 4), (6, 4),
(2, 5), (3, 5), (4, 5);

-- 9. 插入收藏夹
INSERT INTO favorite_folder (user_id, name, is_public) VALUES
(2, '技术收藏', true),
(2, '旅行灵感', false),
(3, '设计参考', true),
(4, '健身计划', false),
(5, '创业思考', true);

-- 10. 插入收藏数据
INSERT INTO favorite (user_id, post_id, folder_id) VALUES
(2, 2, 1), -- 小明收藏小红的设计帖子到技术收藏
(2, 6, 2), -- 小明收藏自己的旅行帖子到旅行灵感
(3, 1, 3), -- 小红收藏小明的技术帖子到设计参考
(4, 3, 4); -- 小丽收藏自己的健身帖子到健身计划

-- 11. 插入关注数据
INSERT INTO follow (follower_id, followee_id) VALUES
(2, 3), (2, 4), -- 小明关注小红和小丽
(3, 2), (3, 6), -- 小红关注小明和莉莉
(4, 2), (4, 3), -- 小丽关注小明和小红
(5, 2), (5, 3), (5, 4), -- 大卫关注小明、小红、小丽
(6, 3), (6, 5); -- 莉莉关注小红和大卫

-- 12. 插入红心数据
INSERT INTO heart (from_user_id, to_user_id) VALUES
(2, 3), (2, 4), -- 小明给小红和小丽发送红心
(3, 2),         -- 小红给小明发送红心
(4, 2),         -- 小丽给小明发送红心
(5, 3), (5, 6), -- 大卫给小红和莉莉发送红心
(6, 5);         -- 莉莉给大卫发送红心

-- 13. 插入匹配数据
INSERT INTO `match` (user1_id, user2_id, status) VALUES
(2, 3, 1), -- 小明和小红匹配成功
(2, 4, 0), -- 小明和小丽匹配中
(5, 6, 1); -- 大卫和莉莉匹配成功

-- 14. 插入会话数据
INSERT INTO conversation (user1_id, user2_id) VALUES
(2, 3), -- 小明和小红的会话
(2, 4), -- 小明和小丽的会话
(5, 6); -- 大卫和莉莉的会话

-- 15. 插入消息数据
INSERT INTO message (conversation_id, sender_id, content_type, content, is_read) VALUES
(1, 2, 'text', '你好小红，你的设计作品很棒！', true), -- 小明和小红的对话
(1, 3, 'text', '谢谢小明！你的编程分享也很有帮助', true),
(1, 2, 'text', '有机会可以合作项目', false),
(2, 2, 'text', '小丽，看到你坚持健身很有感触', true), -- 小明和小丽的对话
(2, 4, 'text', '是啊，健身让我变得更自信了', true),
(2, 4, 'image', '/images/gym1.jpg', false),
(3, 5, 'text', '莉莉，你推荐的餐厅真的很不错', true), -- 大卫和莉莉的对话
(3, 6, 'text', '很高兴你喜欢！我经常去那里', true),
(3, 5, 'text', '下次可以一起去吗？', false);

-- 16. 插入通知数据
INSERT INTO notification (user_id, type, actor_id, related_id, related_type, title, content, is_read) VALUES
(3, 'like', 2, 2, 'post', '新的点赞', '小明喜欢了你的设计作品', true),
(2, 'follow', 3, 3, 'user', '新的关注', '小红关注了你', true),
(4, 'comment', 2, 3, 'post', '新的评论', '小明评论了你的健身动态', true),
(6, 'heart', 5, 5, 'user', '新的红心', '大卫给你发送了红心', false),
(3, 'message', 2, 1, 'conversation', '新消息', '小明给你发送了新消息', false);

-- 17. 插入验证码数据（示例，实际使用时应该用真实场景）
INSERT INTO verification_code (user_id, code, type, purpose, expire_at, is_used) VALUES
(2, '123456', 'email', 'register', DATE_ADD(NOW(), INTERVAL 10 MINUTE), false),
(3, '654321', 'phone', 'login', DATE_ADD(NOW(), INTERVAL 10 MINUTE), true);

-- 18. 插入隐私设置
INSERT INTO privacy_setting (user_id, allow_match, allow_private_messages, allow_profile_view) VALUES
(1, true, 'all', 'all'),
(2, true, 'followed', 'all'),
(3, true, 'followed', 'followed'),
(4, true, 'all', 'all'),
(5, false, 'followed', 'all'),  -- 大卫不允许匹配
(6, true, 'all', 'followed');

-- 19. 插入审核日志
INSERT INTO audit_log (target_id, target_type, auditor_id, action, reason) VALUES
(1, 0, 1, 'PASS', '内容符合社区规范'),
(2, 0, 1, 'PASS', '设计作品很优秀'),
(3, 1, 0, 'PASS', '自动审核通过'),
(4, 2, 1, 'PASS', '用户资料完整真实');

-- 输出初始化完成信息
SELECT 'LinkMe数据库初始化完成！' AS '初始化状态';























DROP DATABASE IF EXISTS linkme;