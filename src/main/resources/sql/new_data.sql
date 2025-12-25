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

-- ============================================
-- 初始数据插入
-- ============================================

USE linkme;

-- 1. 插入用户数据
-- 所有测试用户的密码均为：123456
INSERT INTO user (username, email, phone, password_hash, nickname, gender, birthday, region, avatar_url, bio, role) VALUES
('admin1', 'admin@linkme.com', '13800000000', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '系统管理员', 'male', '1990-01-01', '北京', '', '系统管理员账号', 'admin'),
('xiaoming', 'xiaoming@linkme.com', '13800000001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '小明', 'male', '1995-05-15', '上海', '', '喜欢旅游和摄影的程序员', 'customer'),
('xiaohong', 'xiaohong@linkme.com', '13800000002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '小红', 'female', '1998-08-20', '广州', '', '热爱音乐和美食的设计师', 'customer'),
('xiaoli', 'xiaoli@linkme.com', '13800000003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '小丽', 'female', '1996-03-10', '深圳', '', '健身达人和读书爱好者', 'customer'),
('david', 'david@linkme.com', '13800000004', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '大卫', 'male', '1993-11-25', '杭州', '', '创业者和投资人', 'customer'),
('lily', 'lily@linkme.com', '13800000005', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '莉莉', 'female', '1997-07-07', '成都', '', '宠物医生和动物保护者', 'customer');

-- 2. 插入标签定义
INSERT INTO tag_def (name, created_by, tag_type) VALUES
-- 用户标签（系统设定）
('程序员', NULL, 'user'),
('设计师', NULL, 'user'),
('摄影师', NULL, 'user'),
('音乐爱好者', NULL, 'user'),
('美食家', NULL, 'user'),
('旅行达人', NULL, 'user'),
('健身爱好者', NULL, 'user'),
('读书人', NULL, 'user'),
('创业者', NULL, 'user'),
('投资人', NULL, 'user'),
-- 帖子标签（系统设定）
('技术分享', NULL, 'post'),
('生活感悟', NULL, 'post'),
('美食推荐', NULL, 'post'),
('旅行见闻', NULL, 'post'),
('摄影作品', NULL, 'post'),
('音乐推荐', NULL, 'post'),
('健身心得', NULL, 'post'),
('读书笔记', NULL, 'post');

-- 3. 插入用户标签关联
INSERT INTO user_tag (user_id, tag_id) VALUES
(2, 1), (2, 3), (2, 6),  -- 小明：程序员、摄影师、旅行达人
(3, 2), (3, 4), (3, 5),  -- 小红：设计师、音乐爱好者、美食家
(4, 7), (4, 8),          -- 小丽：健身爱好者、读书人
(5, 9), (5, 10),         -- 大卫：创业者、投资人
(6, 5), (6, 8);          -- 莉莉：美食家、读书人

-- 4. 插入帖子数据
INSERT INTO post (user_id, content, topic, privacy_level) VALUES
(2, '今天分享一个编程小技巧：使用Python处理数据时，pandas库真的超级好用！', NULL, 'public'),
(3, '刚刚设计了一款新的UI界面，大家觉得怎么样？简约而不简单！', NULL, 'public'),
(4, '坚持健身一个月了，感觉整个人都精神了很多！继续加油！', NULL, 'public'),
(5, '创业路上的思考：产品定位和市场需求的匹配有多重要？', NULL, 'public'),
(6, '推荐一家超棒的意大利餐厅，他们家的提拉米苏绝了！', NULL, 'public'),
(2, '最近去西藏旅行，布达拉宫的景色真的太震撼了！', NULL, 'public'),
(3, '发现了一首超好听的歌，单曲循环一整天都不腻！', NULL, 'public');

-- 5. 插入帖子图片
INSERT INTO post_image (post_id, image_url, image_order) VALUES
(2, '', 1),
(2, '', 2),
(6, '', 1),
(6, '', 2);

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
(1, 2, '@小丽 推荐你看《利用Python进行数据分析》这本书', 2),
(2, 2, '设计得很棒！配色很舒服', NULL),
(3, 3, '健身真的会让人上瘾，一起坚持！', NULL),
(5, 4, '在哪家餐厅？求地址！', NULL),
(5, 6, '@小丽 在春熙路的「意式风情」餐厅', 6);

-- 8. 插入点赞数据
INSERT INTO like_post (user_id, post_id) VALUES
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
INSERT INTO match_user (user1_id, user2_id, status) VALUES
(2, 3, 1), -- 小明和小红匹配成功
(2, 4, 0), -- 小明和小丽匹配中
(5, 6, 1); -- 大卫和莉莉匹配成功

-- 14. 插入会话数据
INSERT INTO conversation (user1_id, user2_id, user1_muted, user2_muted, user1_pinned, user2_pinned) VALUES
(2, 3, FALSE, FALSE, FALSE, FALSE), -- 小明和小红的会话
(2, 4, FALSE, FALSE, FALSE, FALSE), -- 小明和小丽的会话
(5, 6, FALSE, FALSE, FALSE, FALSE); -- 大卫和莉莉的会话

-- 15. 插入消息数据
INSERT INTO message (conversation_id, sender_id, content_type, content, is_read) VALUES
(1, 2, 'text', '你好小红，你的设计作品很棒！', true), -- 小明和小红的对话
(1, 3, 'text', '谢谢小明！你的编程分享也很有帮助', true),
(1, 2, 'text', '有机会可以合作项目', false),
(2, 2, 'text', '小丽，看到你坚持健身很有感触', true), -- 小明和小丽的对话
(2, 4, 'text', '是啊，健身让我变得更自信了', true),
(2, 4, 'image', '', false),
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

-- 17. 插入验证码数据

-- 18. 插入隐私设置
INSERT INTO privacy_setting (user_id, allow_match, allow_private_messages, allow_profile_view) VALUES
(1, true, 'all', 'all'),
(2, true, 'followed', 'all'),
(3, true, 'followed', 'followed'),
(4, true, 'all', 'all'),
(5, false, 'followed', 'all'),  -- 大卫不允许匹配
(6, true, 'all', 'followed');

-- 19. 插入审核日志

-- ============================================
-- 匹配机制初始数据
-- ============================================

-- 20. 插入爱好分类
INSERT INTO hobby_category (name, icon, display_order) VALUES
('艺术文娱类', '🎵', 1),
('学习知识类', '📚', 2),
('运动户外类', '🏃‍♂', 3),
('休闲娱乐类', '🎮', 4),
('生活技能类', '🍳', 5),
('社交体验类', '✈️', 6);

-- 21. 插入爱好数据
INSERT INTO hobby (category_id, name, display_order) VALUES
-- 艺术文娱类
(1, '绘画', 1),
(1, '摄影', 2),
(1, '书法', 3),
(1, '写作', 4),
(1, '歌唱', 5),
(1, '舞蹈', 6),
(1, '戏剧', 7),
(1, '乐器演奏', 8),
(1, '平面设计', 9),
(1, '视频剪辑', 10),
-- 学习知识类
(2, '阅读', 1),
(2, '编程', 2),
(2, '教学', 3),
(2, '心理学', 4),
(2, '语言学习', 5),
(2, '哲学思考', 6),
(2, '历史研究', 7),
(2, '投资理财', 8),
(2, '公开演讲', 9),
(2, '创业项目', 10),
-- 运动户外类
(3, '跑步', 1),
(3, '健身', 2),
(3, '游泳', 3),
(3, '骑行', 4),
(3, '钓鱼', 5),
(3, '瑜伽', 6),
(3, '露营', 7),
(3, '武术', 8),
(3, '登山', 9),
(3, '攀岩', 10),
(3, '飞盘', 11),
(3, '球类运动', 12),
-- 休闲娱乐类
(4, '桌游', 1),
(4, '棋牌', 2),
(4, '魔术', 3),
(4, '收藏', 4),
(4, '追剧', 5),
(4, '看电影', 6),
(4, '听音乐', 7),
(4, '剧本杀', 8),
(4, '密室逃脱', 9),
(4, '电子游戏', 10),
-- 生活技能类
(5, '烹饪/烘焙', 1),
(5, '咖啡/茶艺/调酒', 2),
(5, '手工 DIY', 3),
(5, '缝纫', 4),
(5, '家居装饰', 5),
(5, '收纳整理', 6),
(5, '花艺绿植', 7),
-- 社交体验类
(6, '旅行', 1),
(6, '观鸟', 2),
(6, '音乐节', 3),
(6, '演唱会', 4),
(6, '探店打卡', 5),
(6, '展览打卡', 6),
(6, '天文观测', 7),
(6, '公益志愿', 8),
(6, '撸猫撸狗', 9),
(6, 'city walk', 10);

-- 22. 插入性格特质分类
INSERT INTO personality_trait_category (name, description, trait_type, display_order) VALUES
-- 自身特质
('社交能量来源', '描述个人社交能量的获取方式', 'self', 1),
('决策方式', '描述个人做决策时的倾向', 'self', 2),
('生活节奏', '描述个人生活方式的节奏偏好', 'self', 3),
('沟通风格', '描述个人在沟通中的风格特点', 'self', 4),
-- 理想对象特质
('希望对方的社交风格', '描述对理想对象社交风格的期望', 'ideal', 1),
('希望对方的处事风格', '描述对理想对象处事风格的期望', 'ideal', 2),
('希望对方的情绪特质', '描述对理想对象情绪特质的期望', 'ideal', 3);

-- 23. 插入性格特质选项
INSERT INTO personality_trait_option (category_id, name, display_order) VALUES
-- 社交能量来源
(1, '外向型（社交充电）', 1),
(1, '内向型（独处充电）', 2),
(1, '中间型（看情况）', 3),
-- 决策方式
(2, '理性型（逻辑优先）', 1),
(2, '感性型（感受优先）', 2),
(2, '平衡型', 3),
-- 生活节奏
(3, '计划型（凡事按规划）', 1),
(3, '随性型（走一步看一步）', 2),
(3, '弹性型', 3),
-- 沟通风格
(4, '直接坦率型', 1),
(4, '委婉体贴型', 2),
(4, '幽默风趣型', 3),
(4, '倾听为主型', 4),
(4, '偶尔沉默型', 5),
-- 希望对方的社交风格
(5, '热情健谈', 1),
(5, '沉稳内敛', 2),
(5, '同频即可', 3),
-- 希望对方的处事风格
(6, '严谨细致', 1),
(6, '高效行动', 2),
(6, '灵活变通', 3),
(6, '踏实靠谱', 4),
-- 希望对方的情绪特质
(7, '乐观积极', 1),
(7, '冷静理智', 2),
(7, '敏感共情', 3),
(7, '情绪稳定', 4);

-- 24. 插入关系品质
INSERT INTO relationship_quality (name, display_order) VALUES
('真诚坦率', 1),
('相互理解', 2),
('彼此信任', 3),
('包容尊重', 4),
('有趣合拍', 5),
('三观一致', 6);

-- 25. 插入关系模式
INSERT INTO relationship_mode (name, description, display_order) VALUES
('高频互动型（日常分享琐事）', '喜欢日常分享生活中的琐事，保持高频互动', 1),
('深度交流型（走心探讨观点）', '更倾向于深度交流，探讨观点和想法', 2),
('佛系陪伴型（有事才聊，互不打扰）', '保持一定距离，有事才联系，互不打扰', 3),
('兴趣搭子型（只聊共同爱好）', '主要围绕共同兴趣展开交流', 4);

-- 26. 插入沟通期待
INSERT INTO communication_expectation (name, description, display_order) VALUES
('消息秒回型', '希望对方能够及时回复消息', 1),
('随缘回复型', '对回复时间没有严格要求，随缘即可', 2),
('遇事及时沟通型', '平时不要求频繁联系，但重要事情需要及时沟通', 3);

-- 27. 插入匹配维度
INSERT INTO matching_dimension (name, code, display_order) VALUES
('年龄范围', 'age_range', 1),
('关系距离', 'distance', 2),
('兴趣重合度', 'interest_overlap', 3),
('性格特质契合', 'personality_match', 4),
('关系模式一致', 'relationship_mode', 5),
('沟通风格匹配', 'communication_style', 6);

-- 输出初始化完成信息
SELECT 'LinkMe数据库初始化完成！包含所有迁移更新和匹配机制相关表结构。' AS '初始化状态';

