package com.bankmanagement;

import java.sql.*;

public class DBConnect {
    Connection connection;
    Statement statement;
    public DBConnect(){
        try{
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/bankmanagement", "postgres", "123456");
            statement = connection.createStatement();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
