import java.sql.*;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class DBUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/hrms_db?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "MySQL驱动加载失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
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

    public static DefaultTableModel resultSetToTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        Vector<String> columnNames = new Vector<>();
        Vector<Vector<Object>> data = new Vector<>();

        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i));
        }

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
}
