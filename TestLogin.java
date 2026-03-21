import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TestLogin {
    public static void main(String[] args) {
        // 测试数据库连接和用户信息
        testDatabaseConnection();
        
        // 测试密码加密和验证
        testPasswordEncryption();
    }
    
    private static void testDatabaseConnection() {
        String url = "jdbc:mysql://localhost:3306/library?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
        String username = "Narcisuss";
        String password = "688376";
        
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            
            stmt.setString(1, "admin");
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("数据库中的admin用户信息：");
                System.out.println("ID: " + rs.getLong("id"));
                System.out.println("用户名: " + rs.getString("username"));
                System.out.println("密码: " + rs.getString("password"));
                System.out.println("角色: " + rs.getString("role"));
                System.out.println("状态: " + rs.getString("status"));
            } else {
                System.out.println("数据库中未找到admin用户");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void testPasswordEncryption() {
        String rawPassword = "admin123";
        String encryptedPassword = encryptPassword(rawPassword);
        
        System.out.println("\n密码加密测试：");
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密后的密码: " + encryptedPassword);
        System.out.println("是否匹配: " + encryptedPassword.equals("240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9"));
    }
    
    private static String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败: " + e.getMessage());
        }
    }
}