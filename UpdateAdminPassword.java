import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateAdminPassword {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/library?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
        String username = "Narcisuss";
        String password = "688376";
        
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement("UPDATE users SET password = ? WHERE username = ?")) {
            
            stmt.setString(1, "admin123");
            stmt.setString(2, "admin");
            
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("成功将admin用户的密码更新为明文：admin123");
            } else {
                System.out.println("未找到admin用户或密码未更新");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("更新密码失败：" + e.getMessage());
        }
    }
}