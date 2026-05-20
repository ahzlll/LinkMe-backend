-- ============================================================
-- LinkMe 管理端：请在 Navicat / MySQL Workbench 中执行本文件
-- 数据库名一般为 linkme，执行后重启后端
-- ============================================================
USE linkme;

-- 1) 用户账号状态（处罚、封禁）— 解决 Unknown column 'account_status'
ALTER TABLE user
    ADD COLUMN account_status VARCHAR(32) NOT NULL DEFAULT 'normal'
        COMMENT 'normal|warned|restricted_post|restricted_comment|temp_banned|perm_banned' AFTER role;

ALTER TABLE user
    ADD COLUMN ban_until DATETIME NULL COMMENT '临时封禁截止时间' AFTER account_status;

ALTER TABLE user
    ADD COLUMN status_reason VARCHAR(255) NULL COMMENT '处罚原因' AFTER ban_until;

-- 2) 帖子/评论审核状态 — 解决 Unknown column 'moderation_status'
ALTER TABLE post
    ADD COLUMN moderation_status VARCHAR(16) NOT NULL DEFAULT 'visible'
        COMMENT 'visible|hidden|deleted' AFTER topic;

ALTER TABLE comment
    ADD COLUMN moderation_status VARCHAR(16) NOT NULL DEFAULT 'visible'
        COMMENT 'visible|hidden|deleted' AFTER content;

-- 3) 管理员操作日志表
CREATE TABLE IF NOT EXISTS admin_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id INT NOT NULL,
    target_user_id INT NULL,
    target_id BIGINT NULL,
    target_type TINYINT NULL,
    action VARCHAR(64) NOT NULL,
    reason VARCHAR(255) NULL,
    detail VARCHAR(500) NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_admin_id (admin_id),
    INDEX idx_target_user (target_user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4) 历史错误数据修正（若曾把 role 写成 banned）
UPDATE user SET account_status = 'perm_banned', role = 'customer' WHERE role = 'banned';

-- 验证（应能看到各列）
-- SHOW COLUMNS FROM user LIKE 'account_status';
-- SHOW COLUMNS FROM post LIKE 'moderation_status';
