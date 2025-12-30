-- 创建用户喜欢表
CREATE TABLE IF NOT EXISTS user_like (
    id INT AUTO_INCREMENT PRIMARY KEY,
    from_user_id INT NOT NULL COMMENT '发送喜欢的用户ID',
    to_user_id INT NOT NULL COMMENT '接收喜欢的用户ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_from_user (from_user_id),
    INDEX idx_to_user (to_user_id),
    INDEX idx_from_to (from_user_id, to_user_id),
    INDEX idx_created_at (created_at),
    
    FOREIGN KEY (from_user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (to_user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    
    UNIQUE KEY uk_from_to (from_user_id, to_user_id) COMMENT '防止重复喜欢'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户喜欢记录表';
