package com.bankmanagement;

import java.sql.*;

public class DBConnect implements AutoCloseable {
    public Connection connection;
    public Statement statement;

    public DBConnect() {
        try {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/bankmanagement",
                    "postgres",
                    "123456"
            );

            statement = connection.createStatement();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}