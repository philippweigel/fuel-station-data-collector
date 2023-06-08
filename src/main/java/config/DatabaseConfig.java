package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private final static String DRIVER = "postgresql";
    private final static String HOST = "localhost";
    private final static int PORT = 30002;
    private final static String DATABASE_NAME = "stationdb";
    private final static String USERNAME = "postgres";
    private final static String PASSWORD = "postgres";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl());
    }
    private static String getUrl() {
        //jdbc:DRIVER://HOST:PORT/DATABASE_NAME?username=USERNAME?password=PASSWORD
        return String.format(
                "jdbc:%s://%s:%s/%s?user=%s&password=%s", DRIVER, HOST, PORT, DATABASE_NAME, USERNAME, PASSWORD
        );
    }

    public static Connection getConnectionWithGivenUrlFromMessage(String dbUrl) throws SQLException {
        return DriverManager.getConnection(getUrlWithGivenUrlFromMessage(dbUrl));
    }

    private static String getUrlWithGivenUrlFromMessage(String dbUrl) {
        //jdbc:DRIVER://HOST:PORT/DATABASE_NAME?username=USERNAME?password=PASSWORD
        return String.format(
                "jdbc:%s://%s/%s?user=%s&password=%s", DRIVER, dbUrl, DATABASE_NAME, USERNAME, PASSWORD
        );
    }
}
