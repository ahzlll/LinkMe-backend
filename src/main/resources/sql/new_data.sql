-- LinkMe交友聊天社交软件数据库完整初始化脚本
-- 包含所有迁移更新和匹配机制相关表结构
-- 创建数据库
CREATE DATABASE IF NOT EXISTS linkme CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE linkme;

-- ============================================
-- 基础表结构
-- ============================================

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
    avatar_url LONGTEXT COMMENT '头像Base64编码字符串',
    bio TEXT COMMENT '简介',
    role ENUM('customer', 'admin', 'moderator') DEFAULT 'customer' COMMENT '用户角色',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    matching_questionnaire_completed BOOLEAN DEFAULT FALSE COMMENT '是否完成匹配问卷',
    matching_questionnaire_completed_at DATETIME DEFAULT NULL COMMENT '问卷完成时间',
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
    image_url LONGTEXT NOT NULL COMMENT '图片Base64编码字符串',
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
CREATE TABLE IF NOT EXISTS like_post (
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
CREATE TABLE IF NOT EXISTS match_user (
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
    user1_muted BOOLEAN DEFAULT FALSE COMMENT '用户1是否免打扰',
    user2_muted BOOLEAN DEFAULT FALSE COMMENT '用户2是否免打扰',
    user1_pinned BOOLEAN DEFAULT FALSE COMMENT '用户1是否置顶',
    user2_pinned BOOLEAN DEFAULT FALSE COMMENT '用户2是否置顶',
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

-- 20. 屏蔽表（Block）
CREATE TABLE IF NOT EXISTS block (
    blocker_id INT NOT NULL COMMENT '屏蔽者ID',
    blocked_id INT NOT NULL COMMENT '被屏蔽者ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '屏蔽时间',
    PRIMARY KEY (blocker_id, blocked_id),
    FOREIGN KEY (blocker_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (blocked_id) REFERENCES user(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_block_not_self CHECK (blocker_id != blocked_id),
    INDEX idx_blocked_id (blocked_id),
    INDEX idx_blocker_id (blocker_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='屏蔽表';

-- ============================================
-- 匹配机制相关表结构
-- ============================================

-- 21. 爱好分类表（HobbyCategory）
CREATE TABLE IF NOT EXISTS hobby_category (
    category_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    icon VARCHAR(20) DEFAULT NULL COMMENT '分类图标',
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_name` (`name`),
    INDEX `idx_display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='爱好分类表';

-- 22. 爱好表（Hobby）
CREATE TABLE IF NOT EXISTS hobby (
    hobby_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '爱好ID',
    category_id INT NOT NULL COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '爱好名称',
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (category_id) REFERENCES hobby_category(category_id) ON DELETE CASCADE,
    UNIQUE KEY `uk_category_name` (`category_id`, `name`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='爱好表';

-- 23. 用户爱好关联表（UserHobby）
CREATE TABLE IF NOT EXISTS user_hobby (
    user_id INT NOT NULL COMMENT '用户ID',
    hobby_id INT NOT NULL COMMENT '爱好ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (user_id, hobby_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (hobby_id) REFERENCES hobby(hobby_id) ON DELETE CASCADE,
    INDEX `idx_hobby_id` (`hobby_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户爱好关联表';

-- 24. 性格特质分类表（PersonalityTraitCategory）
CREATE TABLE IF NOT EXISTS personality_trait_category (
    category_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    description VARCHAR(255) DEFAULT NULL COMMENT '分类描述',
    trait_type ENUM('self', 'ideal') NOT NULL COMMENT '特质类型：self-自身特质，ideal-理想对象特质',
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_name_type` (`name`, `trait_type`),
    INDEX `idx_trait_type` (`trait_type`),
    INDEX `idx_display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='性格特质分类表';

-- 25. 性格特质选项表（PersonalityTraitOption）
CREATE TABLE IF NOT EXISTS personality_trait_option (
    option_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '选项ID',
    category_id INT NOT NULL COMMENT '分类ID',
    name VARCHAR(100) NOT NULL COMMENT '选项名称',
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (category_id) REFERENCES personality_trait_category(category_id) ON DELETE CASCADE,
    UNIQUE KEY `uk_category_name` (`category_id`, `name`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='性格特质选项表';

-- 26. 用户性格特质表（UserPersonality）
CREATE TABLE IF NOT EXISTS user_personality (
    user_id INT NOT NULL COMMENT '用户ID',
    option_id INT NOT NULL COMMENT '选项ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (user_id, option_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (option_id) REFERENCES personality_trait_option(option_id) ON DELETE CASCADE,
    INDEX `idx_option_id` (`option_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户性格特质表';

-- 27. 关系品质表（RelationshipQuality）
CREATE TABLE IF NOT EXISTS relationship_quality (
    quality_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '品质ID',
    name VARCHAR(50) NOT NULL COMMENT '品质名称',
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_name` (`name`),
    INDEX `idx_display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关系品质表';

-- 28. 用户关系品质关联表（UserRelationshipQuality）
CREATE TABLE IF NOT EXISTS user_relationship_quality (
    user_id INT NOT NULL COMMENT '用户ID',
    quality_id INT NOT NULL COMMENT '品质ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (user_id, quality_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (quality_id) REFERENCES relationship_quality(quality_id) ON DELETE CASCADE,
    INDEX `idx_quality_id` (`quality_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关系品质关联表';

-- 29. 关系模式表（RelationshipMode）
CREATE TABLE IF NOT EXISTS relationship_mode (
    mode_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '模式ID',
    name VARCHAR(50) NOT NULL COMMENT '模式名称',
    description VARCHAR(255) DEFAULT NULL COMMENT '模式描述',
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_name` (`name`),
    INDEX `idx_display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关系模式表';

-- 30. 沟通期待表（CommunicationExpectation）
CREATE TABLE IF NOT EXISTS communication_expectation (
    expectation_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '期待ID',
    name VARCHAR(50) NOT NULL COMMENT '期待名称',
    description VARCHAR(255) DEFAULT NULL COMMENT '期待描述',
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_name` (`name`),
    INDEX `idx_display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='沟通期待表';

-- 31. 匹配维度表（MatchingDimension）
CREATE TABLE IF NOT EXISTS matching_dimension (
    dimension_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '维度ID',
    name VARCHAR(50) NOT NULL COMMENT '维度名称',
    code VARCHAR(50) NOT NULL COMMENT '维度代码',
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_name` (`name`),
    UNIQUE KEY `uk_code` (`code`),
    INDEX `idx_display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='匹配维度表';

-- 32. 用户匹配偏好表（UserMatchingPreference）
CREATE TABLE IF NOT EXISTS user_matching_preference (
    preference_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '偏好ID',
    user_id INT NOT NULL COMMENT '用户ID',
    age_min INT DEFAULT NULL COMMENT '最小年龄要求',
    age_max INT DEFAULT NULL COMMENT '最大年龄要求',
    age_unlimited BOOLEAN DEFAULT FALSE COMMENT '是否无年龄限制',
    distance_preference ENUM('same_city', 'same_city_or_remote', 'unlimited') DEFAULT 'same_city' COMMENT '关系距离要求：same_city-同城优先，same_city_or_remote-同城/异地均可，unlimited-不限距离',
    relationship_mode_id INT DEFAULT NULL COMMENT '理想关系模式ID',
    communication_expectation_id INT DEFAULT NULL COMMENT '沟通期待ID',
    additional_requirements TEXT DEFAULT NULL COMMENT '其他未被覆盖的交友要求',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (relationship_mode_id) REFERENCES relationship_mode(mode_id) ON DELETE SET NULL,
    FOREIGN KEY (communication_expectation_id) REFERENCES communication_expectation(expectation_id) ON DELETE SET NULL,
    UNIQUE KEY `uk_user_id` (`user_id`),
    INDEX `idx_user_id` (`user_id`),
    CONSTRAINT `chk_age_range` CHECK (`age_min` IS NULL OR `age_max` IS NULL OR `age_min` <= `age_max`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户匹配偏好表';

-- 33. 用户匹配必须维度关联表（UserMatchingMustDimension）
CREATE TABLE IF NOT EXISTS user_matching_must_dimension (
    user_id INT NOT NULL COMMENT '用户ID',
    dimension_id INT NOT NULL COMMENT '维度ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (user_id, dimension_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (dimension_id) REFERENCES matching_dimension(dimension_id) ON DELETE CASCADE,
    INDEX `idx_dimension_id` (`dimension_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户匹配必须维度关联表';

-- 34. 用户匹配优先维度关联表（UserMatchingPriorityDimension）
CREATE TABLE IF NOT EXISTS user_matching_priority_dimension (
    user_id INT NOT NULL COMMENT '用户ID',
    dimension_id INT NOT NULL COMMENT '维度ID',
    priority_order INT DEFAULT 0 COMMENT '优先级顺序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (user_id, dimension_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (dimension_id) REFERENCES matching_dimension(dimension_id) ON DELETE CASCADE,
    INDEX `idx_dimension_id` (`dimension_id`),
    INDEX `idx_priority_order` (`priority_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户匹配优先维度关联表';



-- 输出初始化完成信息
SELECT 'LinkMe数据库初始化完成！' AS '初始化状态';

