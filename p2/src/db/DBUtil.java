package db;

import java.sql.*;

public class DBUtil {
    // 修改为SQLite连接配置
    private static final String URL = "jdbc:sqlite:asset_db.db";
    static {
        try {
            Class.forName("org.sqlite.JDBC"); // 已正确配置驱动类
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        // SQLite不需要用户名和密码
        return DriverManager.getConnection(URL);
    }
}