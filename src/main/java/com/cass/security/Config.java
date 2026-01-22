package com.cass.security;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class Config extends ConfigProperties {

    private Connection connection;
    protected PreparedStatement prepare;
    protected ResultSet resultSet;

    private final HikariConfig config = new HikariConfig();

    private final DataSource dataSource;
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    public Config() {
        this.dataSource = initializeDataSource();
    }

    private DataSource initializeDataSource() {
        config.setJdbcUrl(loadProps().getProperty("spring.datasource.url"));
        config.setUsername(loadProps().getProperty("spring.datasource.username"));
        config.setPassword(loadProps().getProperty("spring.datasource.password"));
        config.setMaximumPoolSize(Integer.parseInt(loadProps().getProperty("spring.datasource.hikari.maximum-pool-size")));
        config.setMinimumIdle(Integer.parseInt(loadProps().getProperty("spring.datasource.hikari.minimum-idle")));
        return new HikariDataSource(config); // Singleton DataSource instance
    }

    public Connection getCon() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Unable to retrieve a connection from the pool", e);
            return null;
        }
    }


//    protected Connection getCon(){
//        try {
//            String URL = loadProps().getProperty("spring.datasource.url");
//            String USERNAME = loadProps().getProperty("spring.datasource.username");
//            String PASSWORD = loadProps().getProperty("spring.datasource.password");
//

    /// /            String URL = "jdbc:mysql://69.10.38.195:3308/class_attendance";
    /// /            String USERNAME = "attendance";
    /// /            String PASSWORD = "1244656800";
//            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
//            // connection.setAutoCommit(false);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            // TODO Auto-generated catch block
//        }
//        return connection;
//    }
//    public static void main(String[] args) {
//        Config coon = new Config();
//        try {
//            String q = "SELECT COUNT(*) FROM studentslist";
//            coon.prepare = coon.getCon().prepareStatement(q);
//            coon.resultSet = coon.prepare.executeQuery();
//            if (coon.resultSet.next()) {
//                System.out.println("Student Count: " + coon.resultSet.getInt(1));
//            }
//            System.out.println("connected");
//        } catch (Exception e) {
//            e.printStackTrace();
//            // TODO: handle exception
//        }
//    }
}
