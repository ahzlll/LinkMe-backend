package com.linkme.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动时检查管理端依赖的数据库字段，缺失时自动创建或在日志中打印可执行 SQL 路径提示。
 */
@Component
public class AdminSchemaCheckRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSchemaCheckRunner.class);

    private final JdbcTemplate jdbcTemplate;

    public AdminSchemaCheckRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        checkColumn("user", "account_status");
        checkColumn("post", "moderation_status");
        checkColumn("comment", "moderation_status");
        checkTable("admin_operation_log");
        
        // 检查并自动创建审核相关表
        checkAndCreateAuditTables();
    }

    private void checkColumn(String table, String column) {
        if (!columnExists(table, column)) {
            log.error("【管理端数据库未就绪】表 {} 缺少字段 {}。请执行: src/main/resources/sql/请先执行-管理端数据库补丁.sql",
                    table, column);
        }
    }

    private void checkTable(String table) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
                Integer.class, table);
        if (count == null || count == 0) {
            log.error("【管理端数据库未就绪】缺少表 {}。请执行: src/main/resources/sql/请先执行-管理端数据库补丁.sql", table);
        }
    }

    private boolean columnExists(String table, String column) {
        List<String> names = jdbcTemplate.queryForList(
                "SELECT column_name FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?",
                String.class, table, column);
        return names != null && !names.isEmpty();
    }

    private void checkAndCreateAuditTables() {
        // 检查并创建 audit_log 表
        if (!tableExists("audit_log")) {
            log.info("【审核功能】audit_log 表不存在，正在自动创建...");
            createAuditLogTable();
            log.info("【审核功能】audit_log 表创建成功");
        } else {
            // 检查必要字段
            if (!columnExists("audit_log", "user_id")) {
                log.info("【审核功能】audit_log 表缺少 user_id 字段，正在自动添加...");
                jdbcTemplate.execute("ALTER TABLE audit_log ADD COLUMN user_id BIGINT NOT NULL COMMENT '发布者ID'");
            }
            if (!columnExists("audit_log", "content_type")) {
                jdbcTemplate.execute("ALTER TABLE audit_log ADD COLUMN content_type VARCHAR(20) NOT NULL COMMENT '内容类型'");
            }
            if (!columnExists("audit_log", "content_id")) {
                jdbcTemplate.execute("ALTER TABLE audit_log ADD COLUMN content_id BIGINT COMMENT '内容ID'");
            }
            if (!columnExists("audit_log", "content")) {
                jdbcTemplate.execute("ALTER TABLE audit_log ADD COLUMN content TEXT COMMENT '原始内容'");
            }
            if (!columnExists("audit_log", "is_violation")) {
                jdbcTemplate.execute("ALTER TABLE audit_log ADD COLUMN is_violation TINYINT(1) DEFAULT 0 COMMENT '是否违规'");
            }
            if (!columnExists("audit_log", "matched_words")) {
                jdbcTemplate.execute("ALTER TABLE audit_log ADD COLUMN matched_words VARCHAR(500) COMMENT '命中的敏感词'");
            }
            if (!columnExists("audit_log", "categories")) {
                jdbcTemplate.execute("ALTER TABLE audit_log ADD COLUMN categories VARCHAR(200) COMMENT '命中分类'");
            }
            if (!columnExists("audit_log", "audit_result")) {
                jdbcTemplate.execute("ALTER TABLE audit_log ADD COLUMN audit_result TINYINT DEFAULT 0 COMMENT '审核结果'");
            }
            if (!columnExists("audit_log", "auditor_id")) {
                jdbcTemplate.execute("ALTER TABLE audit_log ADD COLUMN auditor_id BIGINT COMMENT '审核员ID'");
            }
            if (!columnExists("audit_log", "audit_remark")) {
                jdbcTemplate.execute("ALTER TABLE audit_log ADD COLUMN audit_remark VARCHAR(500) COMMENT '审核备注'");
            }
            if (!columnExists("audit_log", "create_time")) {
                jdbcTemplate.execute("ALTER TABLE audit_log ADD COLUMN create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'");
            }
            if (!columnExists("audit_log", "audit_time")) {
                jdbcTemplate.execute("ALTER TABLE audit_log ADD COLUMN audit_time DATETIME COMMENT '审核完成时间'");
            }
        }

        // 检查并创建 manual_review_queue 表
        if (!tableExists("manual_review_queue")) {
            log.info("【审核功能】manual_review_queue 表不存在，正在自动创建...");
            createManualReviewQueueTable();
            log.info("【审核功能】manual_review_queue 表创建成功");
        }
    }

    private boolean tableExists(String table) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
                Integer.class, table);
        return count != null && count > 0;
    }

    private void createAuditLogTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS `audit_log` (
                `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                `user_id` BIGINT NOT NULL COMMENT '发布者ID',
                `content_type` VARCHAR(20) NOT NULL COMMENT '内容类型: post/comment/message',
                `content_id` BIGINT COMMENT '内容ID',
                `content` TEXT COMMENT '原始内容',
                `is_violation` TINYINT(1) DEFAULT 0 COMMENT '是否违规: 0-否,1-是',
                `matched_words` VARCHAR(500) COMMENT '命中的敏感词',
                `categories` VARCHAR(200) COMMENT '命中分类',
                `audit_result` TINYINT DEFAULT 0 COMMENT '审核结果: 0-自动通过,1-送人工,2-人工通过,3-人工拒绝,4-事后下架',
                `auditor_id` BIGINT COMMENT '审核员ID',
                `audit_remark` VARCHAR(500) COMMENT '审核备注',
                `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                `audit_time` DATETIME COMMENT '审核完成时间',
                INDEX idx_user_id (`user_id`),
                INDEX idx_content (`content_type`, `content_id`),
                INDEX idx_create_time (`create_time`),
                INDEX idx_audit_result (`audit_result`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核日志表'
            """;
        jdbcTemplate.execute(sql);
    }

    private void createManualReviewQueueTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS `manual_review_queue` (
                `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                `user_id` BIGINT NOT NULL COMMENT '发布者ID',
                `content_type` VARCHAR(20) NOT NULL COMMENT '内容类型',
                `content_id` BIGINT COMMENT '内容ID',
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
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人工复审队列表'
            """;
        jdbcTemplate.execute(sql);
    }
}
