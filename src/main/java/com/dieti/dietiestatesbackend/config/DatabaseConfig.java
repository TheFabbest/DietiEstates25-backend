package com.dieti.dietiestatesbackend.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {
    private static final Logger logger = Logger.getLogger(DatabaseConfig.class.getName());

    @Bean
    public Connection databaseConnection() {
        return openConnection();
    }

    private Connection openConnection() {
        String url = "jdbc:postgresql://google/postgres?currentSchema=DietiEstates2025&socketFactory=com.google.cloud.sql.postgres.SocketFactory&cloudSqlInstance=third-oarlock-449614-m8:europe-west8:dietiestates2025";
        Properties info = new Properties();
        info.setProperty("user", "postgres");
        info.setProperty("password", System.getenv("DATABASE_CREDENTIALS"));
        try {
            return DriverManager.getConnection(url, info);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Connessione al database fallita! {0}", e.getMessage());
            throw new RuntimeException("Failed to connect to database", e);
        }
    }
}