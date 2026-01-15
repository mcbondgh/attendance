package com.cass.security;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Config extends ConfigProperties{

    private Connection connection;
    protected PreparedStatement prepare;
    protected ResultSet resultSet;
    
    protected Connection getCon(){
        try {
//            String URL = loadProps().getProperty("database.url");
//            String USERNAME = loadProps().getProperty("username");
//            String PASSWORD = loadProps().getProperty("password");

            String URL = "jdbc:mysql://69.10.38.195:3308/class_attendance";
            String USERNAME = "attendance";
            String PASSWORD = "1244656800";
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            // connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
        }
        return connection;
    }

    // public static void main(String[] args) {
    //     Config coon = new Config();
    //     try {
    //         String q = "SELECT COUNT(*) FROM StudentsList";
    //         coon.prepare = coon.getCon().prepareStatement(q);
    //         coon.resultSet  = coon.prepare.executeQuery();
    //         if(coon.resultSet.next()) {
    //             System.out.println(coon.resultSet.getInt(1));
    //         }
    //         System.out.println("connected");
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         // TODO: handle exception
    //     }

        //API CONSUMPTION 

    // }

}
