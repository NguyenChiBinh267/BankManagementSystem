package com.bankmanagement;

import java.sql.*;

public class DBConnect implements AutoCloseable {
    Connection connection;
    Statement statement;
    String password = System.getenv("SUPABASE_DB_PASSWORD");

    public DBConnect(){
        try{
            connection = DriverManager.getConnection("jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:5432/postgres?sslmode=require", "postgres.mhhbetnwdpigljqnnthu", password);
            statement = connection.createStatement();
        } catch (Exception e){
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
