-- 迁移脚本：为消息表添加 hidden_for_user_id 字段
-- 用于实现屏蔽期间发送的消息永久隐藏功能
-- 执行时间：2025-12-25

USE linkme;

-- 添加 hidden_for_user_id 字段到 message 表
-- 该字段记录消息对哪个用户隐藏（当接收者屏蔽了发送者时设置）
ALTER TABLE message 
ADD COLUMN hidden_for_user_id INT DEFAULT NULL COMMENT '对哪个用户隐藏（屏蔽功能使用）';

-- 添加索引以提高查询效率
CREATE INDEX idx_hidden_for_user_id ON message(hidden_for_user_id);
