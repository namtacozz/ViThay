package com.vithay.libman.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final String DB_URL = "jdbc:sqlite:libman.db";

    static {
        initializeDatabase();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static synchronized void initializeDatabase() {
        try (Connection conn = getConnection()) {
            logger.info("Initializing SQLite database: {}", DB_URL);
            executeSqlScript(conn, "/com/vithay/libman/database/schema.sql");
            executeSqlScript(conn, "/com/vithay/libman/database/seed_data.sql");
            logger.info("Database initialized successfully.");
        } catch (Exception e) {
            logger.error("Error initializing database", e);
        }
    }

    private static void executeSqlScript(Connection conn, String scriptPath) {
        try (InputStream is = DatabaseConfig.class.getResourceAsStream(scriptPath)) {
            if (is == null) {
                logger.warn("SQL script not found at path: {}", scriptPath);
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                 Statement stmt = conn.createStatement()) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                        continue;
                    }
                    sb.append(line).append("\n");
                    if (trimmed.endsWith(";")) {
                        stmt.execute(sb.toString());
                        sb.setLength(0);
                    }
                }
                if (sb.length() > 0 && !sb.toString().trim().isEmpty()) {
                    stmt.execute(sb.toString());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to execute SQL script: {}", scriptPath, e);
        }
    }
}
