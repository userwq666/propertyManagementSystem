package com.lsy.propertymanagementsystem.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class DatabaseInitializer {

    private final DataSource dataSource;

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    @Value("${spring.datasource.username:}")
    private String username;

    @Value("${spring.datasource.password:}")
    private String password;

    @PostConstruct
    public void init() {
        log.info("==========================================");
        log.info("开始初始化数据库...");
        log.info("==========================================");

        createDatabaseIfNotExists();

        try (Connection connection = dataSource.getConnection()) {
            executeSqlFile(connection, "sql/01_schema.sql", "数据库结构");
            executeSqlFile(connection, "sql/02_data.sql", "基础数据");

            log.info("==========================================");
            log.info("数据库初始化成功！");
            log.info("==========================================");
            log.info("超级管理员账号：root / 123456");
            log.info("物业管理员账号：admin / 123456");
            log.info("==========================================");
        } catch (SQLException e) {
            log.error("数据库初始化失败: {}", e.getMessage(), e);
            throw new RuntimeException("数据库初始化失败", e);
        }
    }

    private void createDatabaseIfNotExists() {
        String urlWithoutDb = datasourceUrl.replaceFirst("/\\w+(\\?)", "/?");
        try (Connection conn = DriverManager.getConnection(urlWithoutDb, username, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE DATABASE IF NOT EXISTS property_management_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
        } catch (SQLException e) {
            log.error("创建数据库失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建数据库失败", e);
        }
    }

    private void executeSqlFile(Connection connection, String resourcePath, String description) {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            if (!resource.exists()) {
                log.warn("SQL文件不存在，跳过: {}", resourcePath);
                return;
            }

            List<String> statements = parseSqlFile(resource);
            int successCount = 0;
            int skipCount = 0;

            for (String sql : statements) {
                sql = sql.trim();
                if (sql.isEmpty()) continue;

                if (shouldSkip(sql)) {
                    skipCount++;
                    continue;
                }

                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(sql);
                    successCount++;
                } catch (SQLException e) {
                    if (isNonFatalError(e)) {
                        skipCount++;
                    } else {
                        throw e;
                    }
                }
            }

            log.info("{}初始化完成 (执行: {}, 跳过: {})", description, successCount, skipCount);
        } catch (Exception e) {
            log.error("执行SQL失败 [{}]: {}", description, e.getMessage(), e);
            throw new RuntimeException("执行SQL失败: " + description, e);
        }
    }

    private List<String> parseSqlFile(ClassPathResource resource) throws Exception {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--")) continue;

                current.append(line).append("\n");

                if (trimmed.endsWith(";")) {
                    String sql = current.toString().trim();
                    if (!sql.isEmpty()) {
                        sql = sql.substring(0, sql.length() - 1).trim();
                        statements.add(sql);
                    }
                    current = new StringBuilder();
                }
            }

            String remaining = current.toString().trim();
            if (!remaining.isEmpty() && !remaining.endsWith(";")) {
                statements.add(remaining);
            }
        }

        return statements;
    }

    private boolean shouldSkip(String sql) {
        String upper = sql.toUpperCase().trim();
        return upper.startsWith("CREATE DATABASE") || upper.startsWith("USE ");
    }

    private boolean isNonFatalError(SQLException e) {
        Throwable cause = e;
        while (cause != null) {
            String msg = cause.getMessage();
            if (msg != null && (msg.contains("already exists") || msg.contains("Duplicate entry") || msg.contains("Duplicate key"))) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
