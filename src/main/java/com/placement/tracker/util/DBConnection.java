package com.placement.tracker.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || url.isBlank() || username == null || username.isBlank()) {
            throw new SQLException("Database is not configured. Set DB_URL and DB_USERNAME.");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            throw new SQLException("MySQL JDBC driver is not available to the application.", exception);
        }

        return DriverManager.getConnection(url, username, password == null ? "" : password);
    }
}
