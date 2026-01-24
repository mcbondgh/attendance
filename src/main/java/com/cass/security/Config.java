package com.cass.security;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;


@Component
public class Config extends ConfigProperties {

    private static HikariDataSource dataSource;
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    public Config() { dataSource = initializeDataSource();}

    protected static final String DB_URL =
                    "jdbc:mysql://69.10.38.195:3308/class_attendance"
                    + "?useSSL=false"
                    + "&useUnicode=true"
                    + "&characterEncoding=UTF-8"
                    + "&serverTimezone=UTC"
                    + "&cachePrepStmts=true"
                    + "&prepStmtCacheSize=250"
                    + "&prepStmtCacheSqlLimit=2048"
                    + "&useServerPrepStmts=true";

    private static HikariDataSource initializeDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername("attendance");
        config.setPassword("1244656800");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);

        // Additional HikariCP settings can be configured here
        config.setConnectionTimeout(30_000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        return new HikariDataSource(config); // Singleton DataSource instance
    }

    public static Connection getDataSource() throws SQLException {
        return dataSource.getConnection();
    }



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
