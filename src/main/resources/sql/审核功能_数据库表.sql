-- 审核功能数据库表创建脚本
-- 包含：审核日志表、人工复审队列表
USE linkme;

-- 1. 审核日志表 - 记录所有审核操作和结果
CREATE TABLE IF NOT EXISTS `audit_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '发布者ID',
    `content_type` VARCHAR(20) NOT NULL COMMENT '内容类型: post/comment/message',
    `content_id` BIGINT COMMENT '内容ID（帖子ID/评论ID/消息ID）',
    `content` TEXT COMMENT '原始内容（截断存储，建议前500字）',
    `is_violation` TINYINT(1) DEFAULT 0 COMMENT '是否违规: 0-否,1-是',
    `matched_words` VARCHAR(500) COMMENT '命中的敏感词（逗号分隔）',
    `categories` VARCHAR(200) COMMENT '命中分类（逗号分隔）',
    `audit_result` TINYINT DEFAULT 0 COMMENT '审核结果: 0-自动通过,1-送人工,2-人工通过,3-人工拒绝,4-事后下架',
    `auditor_id` BIGINT COMMENT '审核员ID（人工审核时记录）',
    `audit_remark` VARCHAR(500) COMMENT '审核备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `audit_time` DATETIME COMMENT '审核完成时间',
    INDEX idx_user_id (`user_id`),
    INDEX idx_content (`content_type`, `content_id`),
    INDEX idx_create_time (`create_time`),
    INDEX idx_audit_result (`audit_result`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核日志表';

-- 2. 人工复审队列表 - 存放需要人工审核的内容
CREATE TABLE IF NOT EXISTS `manual_review_queue` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '发布者ID',
    `content_type` VARCHAR(20) NOT NULL COMMENT '内容类型: post/comment/message',
    `content_id` BIGINT COMMENT '内容ID（如果已创建）',
    `content` TEXT NOT NULL COMMENT '原始完整内容',
    `matched_words` VARCHAR(500) COMMENT '算法命中的敏感词',
    `categories` VARCHAR(200) COMMENT '命中分类',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-待审核,1-已通过,2-已拒绝',
    `reviewer_id` BIGINT COMMENT '审核员ID',
    `review_remark` VARCHAR(500) COMMENT '审核备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    `review_time` DATETIME COMMENT '审核时间',
    INDEX idx_status (`status`),
    INDEX idx_create_time (`create_time`),
    INDEX idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人工复审队列表';