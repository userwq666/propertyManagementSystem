package com.lsy.propertymanagementsystem.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
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
        System.out.println("==========================================");
        System.out.println("寮€濮嬪垵濮嬪寲鏁版嵁搴?..");
        System.out.println("==========================================");

        createDatabaseIfNotExists();

        try (Connection connection = dataSource.getConnection()) {
            // 鎸夋ā鍧楅『搴忔墽琛孲QL鏂囦欢
            String[] sqlFiles = {
                "sql/00_init.sql",
                "sql/01_system.sql",
                "sql/02_community.sql",
                "sql/03_fee.sql",
                "sql/04_repair.sql",
                "sql/05_complaint.sql",
                "sql/06_equipment.sql",
                "sql/07_inspection.sql",
                "sql/08_announcement.sql",
                "sql/99_stats.sql"
            };
            for (String file : sqlFiles) {
                executeSqlFile(connection, file, file);
            }
        } catch (SQLException e) {
            System.out.println("鏁版嵁搴撳垵濮嬪寲澶辫触: " + e.getMessage());
            throw new RuntimeException("鏁版嵁搴撳垵濮嬪寲澶辫触", e);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        System.out.println("==========================================");
        System.out.println("鏁版嵁搴撳垵濮嬪寲鎴愬姛锛?);
        System.out.println("==========================================");
        System.out.println("瓒呯骇绠＄悊鍛樿处鍙凤細root / 123456");
        System.out.println("鐗╀笟绠＄悊鍛樿处鍙凤細admin / 123456");
        System.out.println("==========================================");
    }

    private void createDatabaseIfNotExists() {
        String urlWithoutDb = datasourceUrl.replaceFirst("/\\w+(\\?)", "/?");
        try (Connection conn = DriverManager.getConnection(urlWithoutDb, username, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE DATABASE IF NOT EXISTS property_management_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
        } catch (SQLException e) {
            System.out.println("鍒涘缓鏁版嵁搴撳け璐? " + e.getMessage());
            throw new RuntimeException("鍒涘缓鏁版嵁搴撳け璐?, e);
        }
    }

    private void executeSqlFile(Connection connection, String resourcePath, String description) {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            if (!resource.exists()) {
                System.out.println("SQL鏂囦欢涓嶅瓨鍦紝璺宠繃: " + resourcePath);
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

            System.out.println(description + "鍒濆鍖栧畬鎴?(鎵ц: " + successCount + ", 璺宠繃: " + skipCount + ")");
        } catch (Exception e) {
            System.out.println("鎵цSQL澶辫触 [" + description + "]: " + e.getMessage());
            throw new RuntimeException("鎵цSQL澶辫触: " + description, e);
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
