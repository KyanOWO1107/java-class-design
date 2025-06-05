import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class DeleteEmployeePanel extends JPanel {
    private JTextField searchField;
    private JTable resultTable;
    
    public DeleteEmployeePanel() {
        setLayout(new BorderLayout(10, 10));
        
        // 搜索面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("搜索");
        searchPanel.add(new JLabel("员工编号/姓名:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // 结果表格
        resultTable = new JTable();
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        
        // 操作按钮
        JButton deleteButton = new JButton("删除选中员工");
        deleteButton.addActionListener(e -> deleteEmployee());
        
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(deleteButton, BorderLayout.SOUTH);
        
        searchButton.addActionListener(e -> loadSearchResults());
    }
    
    private void loadSearchResults() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT emp_id, name, department_id FROM employee_info WHERE emp_id LIKE ? OR name LIKE ?")) {
            
            String keyword = "%" + searchField.getText() + "%";
            pstmt.setString(1, keyword);
            pstmt.setString(2, keyword);
            
            ResultSet rs = pstmt.executeQuery();
            resultTable.setModel(DBUtil.resultSetToTableModel(rs));
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "查询失败: " + ex.getMessage(),
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteEmployee() {
        int selectedRow = resultTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的员工", 
                "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String empId = (String) resultTable.getValueAt(selectedRow, 0);
        
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            // 删除员工前应检查关联数据
            try (PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM attendance WHERE emp_id = ?")) {
                checkStmt.setString(1, empId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "请先删除该员工的考勤记录");
                    return;
                }
            }
            // 删除员工主记录
            try (PreparedStatement pstmt = conn.prepareStatement(
                "DELETE FROM employee_info WHERE emp_id = ?")) {
                pstmt.setString(1, empId);
                int rows = pstmt.executeUpdate();
                
                if (rows > 0) {
                    // 删除关联记录（示例：删除考勤记录）
                    try (PreparedStatement deleteAtt = conn.prepareStatement(
                        "DELETE FROM attendance WHERE emp_id = ?")) {
                        deleteAtt.setString(1, empId);
                        deleteAtt.executeUpdate();
                    }
                    
                    conn.commit();
                    JOptionPane.showMessageDialog(this, "员工删除成功");
                    loadSearchResults();  // 刷新搜索结果
                }
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "删除失败: " + ex.getMessage(),
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}