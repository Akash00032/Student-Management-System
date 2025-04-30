package SMS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/StudentDB";
    private static final String USER = "root";
    private static final String PASSWORD = "Mysql@0032";

    // Private constructor to prevent instantiation
    private DatabaseConnection() {}

    // Singleton Connection instance
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Log driver loading (optional for modern drivers)
                System.out.println("Connecting to database...");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected successfully.");
            } catch (SQLException e) {
                System.err.println("Database connection failed: " + e.getMessage());
                throw e; // Rethrow exception for the caller to handle
            }
        }
        return connection;
    }
}
