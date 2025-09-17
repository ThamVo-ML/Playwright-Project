package com.icon.sct.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtils {
    // Function to connect to Oracle DB
    public static Connection connectToOracle(String host, String port, String serviceName, String user, String password) {
        Connection connection = null;
        try {
            // Load Oracle JDBC Driver
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // JDBC URL format for Oracle DB
            String jdbcUrl = "jdbc:oracle:thin:@" + host + ":" + port + "/" + serviceName;

            // Create connection
            connection = DriverManager.getConnection(jdbcUrl, user, password);

            System.out.println("Connected to Oracle database successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("Oracle JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }
        return connection;
    }
}
