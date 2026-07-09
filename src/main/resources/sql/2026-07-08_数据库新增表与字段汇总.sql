USE `linkme`;

SET NAMES utf8mb4;

-- ============================================================
-- LinkMe 2026-07-08 数据库新增表与字段汇总
-- 说明：
-- 1. 本文件用于旧库升级，按“缺什么补什么”的方式执行
-- 2. 所有 CREATE TABLE 都使用 IF NOT EXISTS，可重复执行
-- 3. 字段补丁使用 information_schema 判断后再执行
-- 4. 不包含演示数据初始化，只补结构
-- ============================================================

-- ============================================================
-- 一、账号处罚与内容审核相关字段
-- ============================================================

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'account_status'
    ),
    'SELECT ''user.account_status exists''',
    'ALTER TABLE `user`
        ADD COLUMN `account_status` VARCHAR(32) NOT NULL DEFAULT ''normal''
        COMMENT ''normal|warned|restricted_post|restricted_comment|temp_banned|perm_banned'' AFTER `role`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'ban_until'
    ),
    'SELECT ''user.ban_until exists''',
    'ALTER TABLE `user`
        ADD COLUMN `ban_until` DATETIME NULL COMMENT ''临时封禁截止时间'' AFTER `account_status`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'status_reason'
    ),
    'SELECT ''user.status_reason exists''',
    'ALTER TABLE `user`
        ADD COLUMN `status_reason` VARCHAR(255) NULL COMMENT ''处罚原因'' AFTER `ban_until`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'post' AND COLUMN_NAME = 'topic'
    ),
    'SELECT ''post.topic exists''',
    'ALTER TABLE `post`
        ADD COLUMN `topic` VARCHAR(100) NULL COMMENT ''帖子话题'' AFTER `content`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'post' AND COLUMN_NAME = 'moderation_status'
    ),
    'SELECT ''post.moderation_status exists''',
    'ALTER TABLE `post`
        ADD COLUMN `moderation_status` VARCHAR(16) NOT NULL DEFAULT ''visible''
        COMMENT ''visible|hidden|deleted'' AFTER `topic`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'comment' AND COLUMN_NAME = 'moderation_status'
    ),
    'SELECT ''comment.moderation_status exists''',
    'ALTER TABLE `comment`
        ADD COLUMN `moderation_status` VARCHAR(16) NOT NULL DEFAULT ''visible''
        COMMENT ''visible|hidden|deleted'' AFTER `content`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- AI 助手默认关闭
SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'ai_assistant_enabled'
    ),
    'ALTER TABLE `user` ALTER COLUMN `ai_assistant_enabled` SET DEFAULT 0',
    'SELECT ''user.ai_assistant_enabled not found'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

UPDATE `user`
SET `ai_assistant_enabled` = 0
WHERE `ai_assistant_enabled` IS NULL OR `ai_assistant_enabled` <> 0;

-- ============================================================
-- 二、聊天与通知相关新增字段/表
-- ============================================================

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'conversation' AND COLUMN_NAME = 'user1_muted'
    ),
    'SELECT ''conversation.user1_muted exists''',
    'ALTER TABLE `conversation`
        ADD COLUMN `user1_muted` BOOLEAN DEFAULT FALSE COMMENT ''用户1是否免打扰'' AFTER `user2_id`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'conversation' AND COLUMN_NAME = 'user2_muted'
    ),
    'SELECT ''conversation.user2_muted exists''',
    'ALTER TABLE `conversation`
        ADD COLUMN `user2_muted` BOOLEAN DEFAULT FALSE COMMENT ''用户2是否免打扰'' AFTER `user1_muted`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'conversation' AND COLUMN_NAME = 'user1_pinned'
    ),
    'SELECT ''conversation.user1_pinned exists''',
    'ALTER TABLE `conversation`
        ADD COLUMN `user1_pinned` BOOLEAN DEFAULT FALSE COMMENT ''用户1是否置顶'' AFTER `user2_muted`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'conversation' AND COLUMN_NAME = 'user2_pinned'
    ),
    'SELECT ''conversation.user2_pinned exists''',
    'ALTER TABLE `conversation`
        ADD COLUMN `user2_pinned` BOOLEAN DEFAULT FALSE COMMENT ''用户2是否置顶'' AFTER `user1_pinned`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'message' AND COLUMN_NAME = 'hidden_for_user_id'
    ),
    'SELECT ''message.hidden_for_user_id exists''',
    'ALTER TABLE `message`
        ADD COLUMN `hidden_for_user_id` INT NULL COMMENT ''对指定用户隐藏'' AFTER `sender_id`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

CREATE INDEX IF NOT EXISTS `idx_message_hidden_for_user_id` ON `message` (`hidden_for_user_id`);

CREATE TABLE IF NOT EXISTS `block` (
    `blocker_id` INT NOT NULL COMMENT '拉黑人ID',
    `blocked_id` INT NOT NULL COMMENT '被拉黑人ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '拉黑时间',
    PRIMARY KEY (`blocker_id`, `blocked_id`),
    INDEX `idx_blocked_id` (`blocked_id`),
    INDEX `idx_blocker_id` (`blocker_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='拉黑关系表';

-- ============================================================
-- 三、举报、审核、处罚相关新增表
-- ============================================================

CREATE TABLE IF NOT EXISTS `admin_operation_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `admin_id` INT NOT NULL COMMENT '管理员ID',
    `target_user_id` INT NULL COMMENT '目标用户ID',
    `target_id` BIGINT NULL COMMENT '目标资源ID',
    `target_type` TINYINT NULL COMMENT '0帖子 1评论 2用户 3私信',
    `action` VARCHAR(64) NOT NULL COMMENT '操作类型',
    `reason` VARCHAR(255) NULL COMMENT '处理原因',
    `detail` VARCHAR(500) NULL COMMENT '补充说明',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '处理时间',
    INDEX `idx_admin_operation_admin_id` (`admin_id`),
    INDEX `idx_admin_operation_target_user` (`target_user_id`),
    INDEX `idx_admin_operation_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员操作日志';

CREATE TABLE IF NOT EXISTS `audit_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '内容发布者ID',
    `content_type` VARCHAR(20) NOT NULL COMMENT '内容类型：post/comment/message/user',
    `content_id` BIGINT NULL COMMENT '内容ID',
    `content` TEXT NULL COMMENT '原始内容',
    `is_violation` TINYINT(1) DEFAULT 0 COMMENT '是否违规：0否 1是',
    `matched_words` VARCHAR(500) NULL COMMENT '命中的敏感词',
    `categories` VARCHAR(200) NULL COMMENT '命中分类',
    `audit_result` TINYINT DEFAULT 0 COMMENT '0自动通过 1待人工审核 2人工通过 3人工驳回 4事后下架',
    `auditor_id` BIGINT NULL COMMENT '审核员ID',
    `audit_remark` VARCHAR(500) NULL COMMENT '审核备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `audit_time` DATETIME NULL COMMENT '审核完成时间',
    INDEX `idx_audit_log_user_id` (`user_id`),
    INDEX `idx_audit_log_content` (`content_type`, `content_id`),
    INDEX `idx_audit_log_create_time` (`create_time`),
    INDEX `idx_audit_log_audit_result` (`audit_result`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审核日志表';

CREATE TABLE IF NOT EXISTS `manual_review_queue` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '内容所属用户ID',
    `content_type` VARCHAR(20) NOT NULL COMMENT '内容类型：post/comment/message/user',
    `content_id` BIGINT NULL COMMENT '内容ID',
    `content` TEXT NOT NULL COMMENT '原始完整内容',
    `matched_words` VARCHAR(500) NULL COMMENT '命中的敏感词',
    `categories` VARCHAR(200) NULL COMMENT '命中分类',
    `source_type` VARCHAR(20) DEFAULT 'system' COMMENT '来源类型：system/user_report',
    `reporter_id` BIGINT NULL COMMENT '举报人ID',
    `report_reason` VARCHAR(255) NULL COMMENT '举报原因',
    `target_user_id` BIGINT NULL COMMENT '被举报用户ID',
    `process_action` VARCHAR(50) NULL COMMENT '处理动作',
    `status` TINYINT DEFAULT 0 COMMENT '0待审核 1已处理 2已驳回',
    `reviewer_id` BIGINT NULL COMMENT '审核员ID',
    `review_remark` VARCHAR(500) NULL COMMENT '审核备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    `review_time` DATETIME NULL COMMENT '审核时间',
    INDEX `idx_manual_review_status` (`status`),
    INDEX `idx_manual_review_create_time` (`create_time`),
    INDEX `idx_manual_review_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='举报与人工审核队列表';

-- ============================================================
-- 四、匹配问卷相关新增表
-- ============================================================

CREATE TABLE IF NOT EXISTS `hobby_category` (
    `category_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `icon` VARCHAR(20) NULL COMMENT '图标',
    `display_order` INT DEFAULT 0 COMMENT '显示顺序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_hobby_category_name` (`name`),
    INDEX `idx_hobby_category_display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='爱好分类表';

CREATE TABLE IF NOT EXISTS `hobby` (
    `hobby_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '爱好ID',
    `category_id` INT NOT NULL COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '爱好名称',
    `display_order` INT DEFAULT 0 COMMENT '显示顺序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_hobby_category_name` (`category_id`, `name`),
    INDEX `idx_hobby_category_id` (`category_id`),
    INDEX `idx_hobby_display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='爱好表';

CREATE TABLE IF NOT EXISTS `user_hobby` (
    `user_id` INT NOT NULL COMMENT '用户ID',
    `hobby_id` INT NOT NULL COMMENT '爱好ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`user_id`, `hobby_id`),
    INDEX `idx_user_hobby_hobby_id` (`hobby_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户爱好关联表';

CREATE TABLE IF NOT EXISTS `personality_trait_category` (
    `category_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `description` VARCHAR(255) NULL COMMENT '分类描述',
    `trait_type` ENUM('self', 'ideal') NOT NULL COMMENT 'trait 类型',
    `display_order` INT DEFAULT 0 COMMENT '显示顺序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_personality_category_name_type` (`name`, `trait_type`),
    INDEX `idx_personality_trait_type` (`trait_type`),
    INDEX `idx_personality_display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='性格维度分类表';

CREATE TABLE IF NOT EXISTS `personality_trait_option` (
    `option_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '选项ID',
    `category_id` INT NOT NULL COMMENT '分类ID',
    `name` VARCHAR(100) NOT NULL COMMENT '选项名称',
    `display_order` INT DEFAULT 0 COMMENT '显示顺序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_personality_option_name` (`category_id`, `name`),
    INDEX `idx_personality_option_category_id` (`category_id`),
    INDEX `idx_personality_option_display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='性格选项表';

CREATE TABLE IF NOT EXISTS `user_personality` (
    `user_id` INT NOT NULL COMMENT '用户ID',
    `option_id` INT NOT NULL COMMENT '选项ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`user_id`, `option_id`),
    INDEX `idx_user_personality_option_id` (`option_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户性格关联表';

CREATE TABLE IF NOT EXISTS `relationship_quality` (
    `quality_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '品质ID',
    `name` VARCHAR(50) NOT NULL COMMENT '品质名称',
    `display_order` INT DEFAULT 0 COMMENT '显示顺序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_relationship_quality_name` (`name`),
    INDEX `idx_relationship_quality_display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关系品质表';

CREATE TABLE IF NOT EXISTS `user_relationship_quality` (
    `user_id` INT NOT NULL COMMENT '用户ID',
    `quality_id` INT NOT NULL COMMENT '品质ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`user_id`, `quality_id`),
    INDEX `idx_user_relationship_quality_quality_id` (`quality_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关系品质关联表';

CREATE TABLE IF NOT EXISTS `relationship_mode` (
    `mode_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '模式ID',
    `name` VARCHAR(50) NOT NULL COMMENT '模式名称',
    `description` VARCHAR(255) NULL COMMENT '模式描述',
    `display_order` INT DEFAULT 0 COMMENT '显示顺序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_relationship_mode_name` (`name`),
    INDEX `idx_relationship_mode_display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关系模式表';

CREATE TABLE IF NOT EXISTS `communication_expectation` (
    `expectation_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '期望ID',
    `name` VARCHAR(50) NOT NULL COMMENT '期望名称',
    `description` VARCHAR(255) NULL COMMENT '期望描述',
    `display_order` INT DEFAULT 0 COMMENT '显示顺序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_communication_expectation_name` (`name`),
    INDEX `idx_communication_expectation_display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='沟通期待表';

CREATE TABLE IF NOT EXISTS `matching_dimension` (
    `dimension_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '维度ID',
    `name` VARCHAR(50) NOT NULL COMMENT '维度名称',
    `code` VARCHAR(50) NOT NULL COMMENT '维度编码',
    `display_order` INT DEFAULT 0 COMMENT '显示顺序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_matching_dimension_name` (`name`),
    UNIQUE KEY `uk_matching_dimension_code` (`code`),
    INDEX `idx_matching_dimension_display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='匹配维度表';

CREATE TABLE IF NOT EXISTS `user_matching_preference` (
    `preference_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '偏好ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `age_min` INT NULL COMMENT '最小年龄',
    `age_max` INT NULL COMMENT '最大年龄',
    `age_unlimited` BOOLEAN DEFAULT FALSE COMMENT '是否不限年龄',
    `distance_preference` ENUM('same_city', 'same_city_or_remote', 'unlimited') DEFAULT 'same_city' COMMENT '距离偏好',
    `relationship_mode_id` INT NULL COMMENT '关系模式ID',
    `communication_expectation_id` INT NULL COMMENT '沟通期待ID',
    `additional_requirements` TEXT NULL COMMENT '附加要求',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_user_matching_preference_user_id` (`user_id`),
    INDEX `idx_user_matching_preference_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户匹配偏好表';

CREATE TABLE IF NOT EXISTS `user_matching_must_dimension` (
    `user_id` INT NOT NULL COMMENT '用户ID',
    `dimension_id` INT NOT NULL COMMENT '维度ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`user_id`, `dimension_id`),
    INDEX `idx_user_matching_must_dimension_dimension_id` (`dimension_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户匹配必选维度表';

CREATE TABLE IF NOT EXISTS `user_matching_priority_dimension` (
    `user_id` INT NOT NULL COMMENT '用户ID',
    `dimension_id` INT NOT NULL COMMENT '维度ID',
    `priority_order` INT DEFAULT 0 COMMENT '优先级顺序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`user_id`, `dimension_id`),
    INDEX `idx_user_matching_priority_dimension_dimension_id` (`dimension_id`),
    INDEX `idx_user_matching_priority_dimension_priority_order` (`priority_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户匹配优先维度表';

CREATE TABLE IF NOT EXISTS `user_questionnaire_completion` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `first_completed_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '首次完成时间',
    `last_submitted_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最近提交时间',
    `submission_count` INT DEFAULT 1 COMMENT '提交次数',
    UNIQUE KEY `uk_user_questionnaire_completion_user_id` (`user_id`),
    INDEX `idx_user_questionnaire_completion_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问卷完成记录表';

CREATE TABLE IF NOT EXISTS `user_like` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `from_user_id` INT NOT NULL COMMENT '发起喜欢的用户ID',
    `to_user_id` INT NOT NULL COMMENT '被喜欢的用户ID',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_user_like_from_to` (`from_user_id`, `to_user_id`),
    INDEX `idx_user_like_from_user` (`from_user_id`),
    INDEX `idx_user_like_to_user` (`to_user_id`),
    INDEX `idx_user_like_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户喜欢记录表';

-- ============================================================
-- 五、兼容当前代码命名的表
-- ============================================================

CREATE TABLE IF NOT EXISTS `like_post` (
    `like_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `post_id` INT NOT NULL COMMENT '帖子ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_like_post_user_post` (`user_id`, `post_id`),
    INDEX `idx_like_post_post_id_created_at` (`post_id`, `created_at`),
    INDEX `idx_like_post_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子点赞表';

CREATE TABLE IF NOT EXISTS `match_user` (
    `match_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '匹配ID',
    `user1_id` INT NOT NULL COMMENT '用户1ID',
    `user2_id` INT NOT NULL COMMENT '用户2ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `status` INT DEFAULT 0 COMMENT '状态：0进行中 1已结束',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_match_user_pair` (`user1_id`, `user2_id`),
    INDEX `idx_match_user_user1_id` (`user1_id`),
    INDEX `idx_match_user_user2_id` (`user2_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='匹配关系表';

-- ============================================================
-- 六、统一编码，修复中文乱码
-- ============================================================

ALTER DATABASE `linkme` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE `notification` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `audit_log` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `manual_review_queue` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `admin_operation_log` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE `notification`
    MODIFY COLUMN `title` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '通知标题',
    MODIFY COLUMN `content` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '通知内容';

-- ============================================================
-- 七、历史数据兼容
-- ============================================================

UPDATE `user`
SET `account_status` = 'perm_banned', `role` = 'customer'
WHERE `role` = 'banned';

-- ============================================================
-- 八、检查用语句
-- ============================================================

-- SHOW TABLES LIKE 'admin_operation_log';
-- SHOW TABLES LIKE 'audit_log';
-- SHOW TABLES LIKE 'manual_review_queue';
-- SHOW TABLES LIKE 'block';
-- SHOW TABLES LIKE 'hobby_category';
-- SHOW TABLES LIKE 'user_matching_preference';
-- SHOW FULL COLUMNS FROM `user`;
-- SHOW FULL COLUMNS FROM `post`;
-- SHOW FULL COLUMNS FROM `comment`;
-- SHOW FULL COLUMNS FROM `conversation`;
-- SHOW FULL COLUMNS FROM `message`;
