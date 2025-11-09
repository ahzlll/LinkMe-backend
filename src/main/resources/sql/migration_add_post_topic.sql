-- 数据库迁移脚本：为 post 表添加 topic（主题）字段
-- 用途：为帖子添加主题属性，方便分类和搜索

USE linkme;

-- 为 post 表添加 topic 字段
ALTER TABLE post 
ADD COLUMN topic VARCHAR(100) DEFAULT NULL COMMENT '主题' AFTER content;

-- 为 topic 字段添加索引，方便按主题查询
ALTER TABLE post 
ADD INDEX `idx_topic` (`topic`);

