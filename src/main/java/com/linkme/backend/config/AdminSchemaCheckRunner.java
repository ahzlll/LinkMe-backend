package com.linkme.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动时检查管理端依赖的数据库字段，缺失时在日志中打印可执行 SQL 路径提示。
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
}
