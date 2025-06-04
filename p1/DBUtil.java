import java.sql.*;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class DBUtil {

    // 修改数据库连接配置（for sqlite）
    private static final String URL = "jdbc:sqlite:e:/hrms.db";
    // 删除USER和PASSWORD字段
    // Remove these lines
    // private static final String USER = "root";
    // private static final String PASSWORD = "kyanite7";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "SQLite驱动加载失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            pstmt.executeUpdate();
        }
    }

    public static TableModel resultSetToTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        Vector<String> columnNames = new Vector<>();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i));
        }
        
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }
            data.add(row);
        }
        
        return new DefaultTableModel(data, columnNames);
    }

    public static void logOperation(String operationType, String empId, String oldValue, String newValue) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO operation_history (operation_type, emp_id, old_value, new_value) VALUES (?,?,?,?)")) {
            
            pstmt.setString(1, operationType);
            pstmt.setString(2, empId);
            pstmt.setString(3, oldValue);
            pstmt.setString(4, newValue);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void initDatabase() {
        try (Connection conn = DriverManager.getConnection(URL); // Remove USER/PASSWORD parameters
             Statement stmt = conn.createStatement()) {
            
            // 添加操作日志表
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS operation_log ("
                + "log_id INTEGER PRIMARY KEY AUTOINCREMENT,"  // 修改AUTO_INCREMENT为AUTOINCREMENT
                + "emp_id VARCHAR(20),"
                + "operation_type VARCHAR(50),"
                + "operation_date DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "details TEXT)");
                
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void beginTransaction(Connection conn) {
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to start transaction", e);
        }
    }

    public static void commitTransaction(Connection conn) {
        try {
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to commit transaction", e);
        }
    }

    public static void rollbackTransaction(Connection conn) {
        try {
            conn.rollback();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to rollback transaction", e);
        }
    }
}