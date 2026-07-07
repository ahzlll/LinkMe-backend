USE `linkme`;

SET NAMES utf8mb4;

-- ============================================================
-- LinkMe 2026-07-08 审核、举报、处罚与通知编码修复补丁
-- 建议执行前先备份数据库
-- ============================================================

-- 1. 用户处罚相关字段
SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'account_status'
    ),
    'SELECT ''user.account_status already exists''',
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
    'SELECT ''user.ban_until already exists''',
    'ALTER TABLE `user`
        ADD COLUMN `ban_until` DATETIME NULL COMMENT ''临时封禁截止时间'' AFTER `account_status`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'status_reason'
    ),
    'SELECT ''user.status_reason already exists''',
    'ALTER TABLE `user`
        ADD COLUMN `status_reason` VARCHAR(255) NULL COMMENT ''处罚原因'' AFTER `ban_until`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2. 帖子、评论可见性字段
SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'post' AND COLUMN_NAME = 'moderation_status'
    ),
    'SELECT ''post.moderation_status already exists''',
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
    'SELECT ''comment.moderation_status already exists''',
    'ALTER TABLE `comment`
        ADD COLUMN `moderation_status` VARCHAR(16) NOT NULL DEFAULT ''visible''
        COMMENT ''visible|hidden|deleted'' AFTER `content`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3. 审核日志表
CREATE TABLE IF NOT EXISTS `audit_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '发布者ID',
    `content_type` VARCHAR(20) NOT NULL COMMENT '内容类型：post/comment/message/user',
    `content_id` BIGINT NULL COMMENT '内容ID',
    `content` TEXT NULL COMMENT '原始内容',
    `is_violation` TINYINT(1) DEFAULT 0 COMMENT '是否违规：0否 1是',
    `matched_words` VARCHAR(500) NULL COMMENT '命中的敏感词',
    `categories` VARCHAR(200) NULL COMMENT '命中分类',
    `audit_result` TINYINT DEFAULT 0 COMMENT '审核结果：0自动通过 1送人工审核 2人工通过 3人工驳回 4事后下架',
    `auditor_id` BIGINT NULL COMMENT '审核员ID',
    `audit_remark` VARCHAR(500) NULL COMMENT '审核备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `audit_time` DATETIME NULL COMMENT '审核完成时间',
    INDEX `idx_audit_log_user_id` (`user_id`),
    INDEX `idx_audit_log_content` (`content_type`, `content_id`),
    INDEX `idx_audit_log_create_time` (`create_time`),
    INDEX `idx_audit_log_audit_result` (`audit_result`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审核日志表';

-- 4. 举报/人工审核队列表
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
    `status` TINYINT DEFAULT 0 COMMENT '状态：0待审核 1已处理 2已驳回',
    `reviewer_id` BIGINT NULL COMMENT '审核员ID',
    `review_remark` VARCHAR(500) NULL COMMENT '审核备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    `review_time` DATETIME NULL COMMENT '审核时间',
    INDEX `idx_manual_review_status` (`status`),
    INDEX `idx_manual_review_create_time` (`create_time`),
    INDEX `idx_manual_review_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='举报与人工审核队列表';

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'manual_review_queue' AND COLUMN_NAME = 'source_type'
    ),
    'SELECT ''manual_review_queue.source_type already exists''',
    'ALTER TABLE `manual_review_queue`
        ADD COLUMN `source_type` VARCHAR(20) DEFAULT ''system'' COMMENT ''来源类型：system/user_report'' AFTER `categories`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'manual_review_queue' AND COLUMN_NAME = 'reporter_id'
    ),
    'SELECT ''manual_review_queue.reporter_id already exists''',
    'ALTER TABLE `manual_review_queue`
        ADD COLUMN `reporter_id` BIGINT NULL COMMENT ''举报人ID'' AFTER `source_type`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'manual_review_queue' AND COLUMN_NAME = 'report_reason'
    ),
    'SELECT ''manual_review_queue.report_reason already exists''',
    'ALTER TABLE `manual_review_queue`
        ADD COLUMN `report_reason` VARCHAR(255) NULL COMMENT ''举报原因'' AFTER `reporter_id`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'manual_review_queue' AND COLUMN_NAME = 'target_user_id'
    ),
    'SELECT ''manual_review_queue.target_user_id already exists''',
    'ALTER TABLE `manual_review_queue`
        ADD COLUMN `target_user_id` BIGINT NULL COMMENT ''被举报用户ID'' AFTER `report_reason`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'manual_review_queue' AND COLUMN_NAME = 'process_action'
    ),
    'SELECT ''manual_review_queue.process_action already exists''',
    'ALTER TABLE `manual_review_queue`
        ADD COLUMN `process_action` VARCHAR(50) NULL COMMENT ''处理动作'' AFTER `target_user_id`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 5. 管理员操作日志
CREATE TABLE IF NOT EXISTS `admin_operation_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `admin_id` INT NOT NULL,
    `target_user_id` INT NULL,
    `target_id` BIGINT NULL,
    `target_type` TINYINT NULL,
    `action` VARCHAR(64) NOT NULL,
    `reason` VARCHAR(255) NULL,
    `detail` VARCHAR(500) NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_admin_operation_admin_id` (`admin_id`),
    INDEX `idx_admin_operation_target_user` (`target_user_id`),
    INDEX `idx_admin_operation_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员操作日志';

-- 6. 兼容历史错误状态
UPDATE `user`
SET `account_status` = 'perm_banned', `role` = 'customer'
WHERE `role` = 'banned';

-- 7. 统一通知与审核相关表编码，修复中文乱码风险
ALTER DATABASE `linkme` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE `notification` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `notification`
    MODIFY COLUMN `title` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '通知标题',
    MODIFY COLUMN `content` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '通知内容';

ALTER TABLE `audit_log` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `manual_review_queue` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `admin_operation_log` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 8. 可选检查
-- SHOW FULL COLUMNS FROM `user`;
-- SHOW FULL COLUMNS FROM `post`;
-- SHOW FULL COLUMNS FROM `comment`;
-- SHOW FULL COLUMNS FROM `notification`;
-- SHOW FULL COLUMNS FROM `manual_review_queue`;
