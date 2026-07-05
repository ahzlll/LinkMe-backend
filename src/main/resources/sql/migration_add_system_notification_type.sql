ALTER TABLE notification
MODIFY COLUMN type ENUM('message', 'follow', 'heart', 'like', 'comment', 'match', 'system')
NOT NULL COMMENT '通知类型';
