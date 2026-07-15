package com.bankmanagement;

import java.sql.*;

public class DBConnect implements AutoCloseable {
    public Connection connection;
    public Statement statement;

    public DBConnect() {
        try {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(
                    environmentOrDefault("SMARTBANK_DB_URL", "jdbc:postgresql://localhost:5432/bankmanagement"),
                    environmentOrDefault("SMARTBANK_DB_USER", "postgres"),
                    environmentOrDefault("SMARTBANK_DB_PASSWORD", "123456")
            );

            statement = connection.createStatement();

        } catch (Exception e) {
            connection = null;
            statement = null;
        }
    }

    private static String environmentOrDefault(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    @Override
    public void close() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ignored) {
        }
    }
}
