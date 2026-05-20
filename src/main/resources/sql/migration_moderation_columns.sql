-- 仅补充审核相关字段（若 migration_admin_module.sql 未完整执行）
USE linkme;

ALTER TABLE post
    ADD COLUMN moderation_status VARCHAR(16) NOT NULL DEFAULT 'visible'
        COMMENT 'visible|hidden|deleted' AFTER topic;

ALTER TABLE comment
    ADD COLUMN moderation_status VARCHAR(16) NOT NULL DEFAULT 'visible'
        COMMENT 'visible|hidden|deleted' AFTER content;
