package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL =
            "jdbc:mysql://localhost:3306/biblioteca?useSSL=false&serverTimezone=UTC";
    private static final String USER = "biblioteca_user";
    private static final String PASS = "biblioteca_pass";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
