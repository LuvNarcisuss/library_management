import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckAdminStatus {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/library?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
        String username = "Narcisuss";
        String password = "688376";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                String sql = "SELECT id, username, password, status FROM users WHERE username = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, "admin");
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            System.out.println("Admin User Info:");
                            System.out.println("ID: " + rs.getLong("id"));
                            System.out.println("Username: " + rs.getString("username"));
                            System.out.println("Password: " + rs.getString("password"));
                            System.out.println("Status: " + rs.getString("status"));
                        } else {
                            System.out.println("Admin user not found!");
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}