package com.scc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    public static void initialize() {
        String createUsers = """
                CREATE TABLE IF NOT EXISTS users (
                    id       INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    roles    TEXT NOT NULL,
                    status   TEXT NOT NULL DEFAULT 'PENDING'
                )
                """;
        // Starter admin so someone can log in and approve everyone else.
        String seedAdmin = "INSERT OR IGNORE INTO users (username, password, roles, status) "
                + "VALUES (?, ?, 'ADMIN', 'APPROVED')";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsers);
            try (PreparedStatement ps = conn.prepareStatement(seedAdmin)) {
                ps.setString(1, "admin");
                ps.setString(2, "admin123");
                ps.executeUpdate();
            }
            System.out.println("Database initialized.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage(), e);
        }
    }
}