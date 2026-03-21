import java.sql.*;

public class CheckDatabase {
    public static void main(String[] args) {
        // 数据库连接信息
        String url = "jdbc:mysql://localhost:3306/library?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai";
        String username = "Narcisuss";
        String password = "688376";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("成功连接到数据库");

            // 检查users表是否存在
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "users", null);
            if (tables.next()) {
                System.out.println("users表存在");
            } else {
                System.out.println("users表不存在");
                return;
            }

            // 查询admin用户
            String query = "SELECT * FROM users WHERE username = 'admin'";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("admin用户存在");
                    System.out.println("用户名: " + rs.getString("username"));
                    System.out.println("密码: " + rs.getString("password"));
                    System.out.println("角色: " + rs.getString("role"));
                    System.out.println("状态: " + rs.getString("status"));
                } else {
                    System.out.println("admin用户不存在");
                }
            }
        } catch (SQLException e) {
            System.err.println("数据库操作失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}