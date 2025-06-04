import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SearchEmployeePanel extends JPanel {
    private JTextField searchField;
    private JTable resultTable;
    private JButton nextButton, prevButton;
    private int currentPage = 1;
    private static final int PAGE_SIZE = 50;

    public SearchEmployeePanel() {
        setLayout(new BorderLayout());
        
        // 搜索栏
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        JButton searchButton = new JButton("搜索");
        searchPanel.add(new JLabel("员工编号/姓名:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // 结果表格
        resultTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultTable);
        
        // Add action listener to search button
        searchButton.addActionListener(e -> searchEmployees());
        
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    
        JPanel pagePanel = new JPanel();
        prevButton = new JButton("上一页");
        nextButton = new JButton("下一页");
        pagePanel.add(prevButton);
        pagePanel.add(nextButton);
        
        add(pagePanel, BorderLayout.SOUTH);
        
        prevButton.addActionListener(e -> {
            currentPage = Math.max(1, currentPage - 1);
            searchEmployees();
        });
        nextButton.addActionListener(e -> {
            currentPage++;
            searchEmployees();
        });
    }
    
    private void searchEmployees() {
        String sql = "SELECT e.*, d.dept_name FROM employee_info e " +
                     "LEFT JOIN department d ON e.department_id = d.dept_id " +
                     "WHERE e.emp_id LIKE ? OR e.name LIKE ? " +
                     "LIMIT ? OFFSET ?";
                     
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String keyword = "%" + searchField.getText() + "%";
            pstmt.setString(1, keyword);
            pstmt.setString(2, keyword);
            pstmt.setInt(3, PAGE_SIZE);
            pstmt.setInt(4, (currentPage - 1) * PAGE_SIZE);
            
            ResultSet rs = pstmt.executeQuery();
// 由于 DBUtil 中未定义 resultSetToTableModel 方法，我们需要手动实现将 ResultSet 转换为 TableModel
try {
    java.util.Vector<String> columnNames = new java.util.Vector<>();
    java.util.Vector<java.util.Vector<Object>> data = new java.util.Vector<>();

    ResultSetMetaData metaData = rs.getMetaData();
    int columnCount = metaData.getColumnCount();

    // 获取列名
    for (int i = 1; i <= columnCount; i++) {
        columnNames.add(metaData.getColumnName(i));
    }

    // 获取数据
    while (rs.next()) {
        java.util.Vector<Object> row = new java.util.Vector<>();
        for (int i = 1; i <= columnCount; i++) {
            row.add(rs.getObject(i));
        }
        data.add(row);
    }

    resultTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
} catch (SQLException ex) {
    JOptionPane.showMessageDialog(this, "处理查询结果失败: " + ex.getMessage(),
        "错误", JOptionPane.ERROR_MESSAGE);
}
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "查询失败: " + ex.getMessage(),
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}