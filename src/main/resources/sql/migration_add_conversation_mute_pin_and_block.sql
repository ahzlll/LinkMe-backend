-- 迁移脚本：为conversation表添加免打扰和置顶字段，创建block表

USE linkme;

-- 1. 为conversation表添加免打扰和置顶字段
ALTER TABLE conversation 
ADD COLUMN user1_muted BOOLEAN DEFAULT FALSE COMMENT '用户1是否免打扰',
ADD COLUMN user2_muted BOOLEAN DEFAULT FALSE COMMENT '用户2是否免打扰',
ADD COLUMN user1_pinned BOOLEAN DEFAULT FALSE COMMENT '用户1是否置顶',
ADD COLUMN user2_pinned BOOLEAN DEFAULT FALSE COMMENT '用户2是否置顶';

-- 2. 创建屏蔽表（Block）
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

