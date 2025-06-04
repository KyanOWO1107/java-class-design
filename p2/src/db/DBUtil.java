package db;

import java.sql.*;

public class DBUtil {
    // 确认以下配置与本地MySQL一致
    private static final String URL = "jdbc:mysql://localhost:3306/asset_db?useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "123456"; 

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}