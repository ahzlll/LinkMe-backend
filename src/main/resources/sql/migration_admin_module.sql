-- 管理端模块：账号状态、内容审核状态、管理员操作日志
USE linkme;

-- 用户账号状态（与 role 权限分离）
ALTER TABLE user
    ADD COLUMN account_status VARCHAR(32) NOT NULL DEFAULT 'normal'
        COMMENT 'normal|warned|restricted_post|restricted_comment|temp_banned|perm_banned' AFTER role,
    ADD COLUMN ban_until DATETIME NULL COMMENT '临时封禁截止时间' AFTER account_status,
    ADD COLUMN status_reason VARCHAR(255) NULL COMMENT '处罚/状态原因' AFTER ban_until;

ALTER TABLE post
    ADD COLUMN moderation_status VARCHAR(16) NOT NULL DEFAULT 'visible'
        COMMENT 'visible|hidden|deleted' AFTER topic;

ALTER TABLE comment
    ADD COLUMN moderation_status VARCHAR(16) NOT NULL DEFAULT 'visible'
        COMMENT 'visible|hidden|deleted' AFTER content;

-- 管理员操作日志（用户处罚、解封等）
CREATE TABLE IF NOT EXISTS admin_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id INT NOT NULL COMMENT '管理员ID',
    target_user_id INT NULL COMMENT '目标用户ID',
    target_id BIGINT NULL COMMENT '目标资源ID',
    target_type TINYINT NULL COMMENT '0-帖子,1-评论,2-用户',
    action VARCHAR(64) NOT NULL COMMENT '操作类型',
    reason VARCHAR(255) NULL,
    detail VARCHAR(500) NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_admin_id (admin_id),
    INDEX idx_target_user (target_user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员操作日志';

-- 若曾误将 role 设为 banned，迁回 customer 并标记永久封禁
UPDATE user SET account_status = 'perm_banned', role = 'customer' WHERE role = 'banned';
