package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/skillswap";
    private static final String USER = "root";
    private static final String PASSWORD = "Enchanted@_51";

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println(" Database connected successfully!");
            } catch (SQLException e) {
                System.out.println(" Failed to connect to database!");
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println(" Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
