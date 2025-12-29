-- 问卷完成记录表（UserQuestionnaireCompletion）
CREATE TABLE IF NOT EXISTS user_questionnaire_completion (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id INT NOT NULL COMMENT '用户ID',
    first_completed_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '首次完成时间',
    last_submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上次提交时间',
    submission_count INT DEFAULT 1 COMMENT '提交次数',
    UNIQUE KEY uk_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户问卷完成记录表';
