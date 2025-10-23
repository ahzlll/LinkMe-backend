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
    privacy_level ENUM('public', 'followers', 'intimate', 'private') DEFAULT 'public' COMMENT '隐私级别',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_privacy_level` (`privacy_level`)
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

-- 15. 消息表（Message）
CREATE TABLE IF NOT EXISTS message (
    message_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    conversation_id INT NOT NULL COMMENT '会话ID',
    sender_id INT NOT NULL COMMENT '发送者ID',
    content_type ENUM('text', 'image', 'video', 'file') DEFAULT 'text' COMMENT '内容类型',
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


-- 插入一些初始数据
-- 插入系统标签
INSERT INTO tag_def (name, created_by, tag_type) VALUES
('运动', NULL, 'user'),
('音乐', NULL, 'user'),
('旅行', NULL, 'user'),
('美食', NULL, 'user'),
('电影', NULL, 'user'),
('读书', NULL, 'user'),
('游戏', NULL, 'user'),
('摄影', NULL, 'user'),
('时尚', NULL, 'user'),
('科技', NULL, 'user'),
('生活', NULL, 'post'),
('心情', NULL, 'post'),
('分享', NULL, 'post'),
('求助', NULL, 'post'),
('讨论', NULL, 'post');

-- 创建测试用户
INSERT INTO user (username, email, phone, password_hash, nickname, gender, birthday, region, avatar_url, bio) VALUES
('admin', 'admin@linkme.com', '13800138000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '管理员', '男', '1990-01-01', '北京', '', '系统管理员'),
('testuser1', 'test1@linkme.com', '13800138001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '测试用户1', '女', '1995-05-15', '上海', '', '测试用户1'),
('testuser2', 'test2@linkme.com', '13800138002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '测试用户2', '男', '1992-08-20', '广州', '', '测试用户2');

-- 为测试用户设置隐私设置
INSERT INTO privacy_setting (user_id, allow_match, allow_private_messages, allow_profile_view) VALUES
(1, TRUE, TRUE, TRUE),
(2, TRUE, TRUE, TRUE),
(3, TRUE, TRUE, TRUE);
