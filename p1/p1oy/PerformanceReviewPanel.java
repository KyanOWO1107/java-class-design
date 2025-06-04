import java.awt.GridLayout;
import java.sql.*;

import javax.swing.*;

public class PerformanceReviewPanel extends JPanel {
    private JComboBox<String> employeeCombo;
    private JTextField reviewDateField;
    private JTextArea reviewContentArea;
    private JComboBox<String> reviewTypeCombo;
    private JTextField scoreField;
    
    public PerformanceReviewPanel() {
        setLayout(new GridLayout(0, 2, 10, 10));
        
        // 初始化组件
        employeeCombo = new JComboBox<>();
        reviewDateField = new JTextField(15);
        reviewContentArea = new JTextArea(5, 20);
        JButton saveButton = new JButton("保存考核");
        saveButton.addActionListener(e -> saveReview());  // Add this line
        reviewTypeCombo = new JComboBox<>(new String[]{"年度考核", "晋升考核"});
        scoreField = new JTextField(5);
        
        loadEmployees();
        
        add(new JLabel("选择员工:"));
        add(employeeCombo);
        add(new JLabel("考核日期:"));
        add(reviewDateField);
        add(new JLabel("考核内容:"));
        add(new JScrollPane(reviewContentArea));
        add(new JLabel());
        add(saveButton);
        
    }
    
    private void loadEmployees() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT emp_id, name FROM employee_info")) {
            
            employeeCombo.removeAllItems();
            while (rs.next()) {
                employeeCombo.addItem(rs.getString("name") + " (" + rs.getString("emp_id") + ")");
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载员工数据失败: " + ex.getMessage(),
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveReview() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO performance_reviews (emp_id, review_date, review_type, score, content) VALUES (?, ?, ?, ?, ?)")) {
            
            // Get selected employee ID
            String selected = (String) employeeCombo.getSelectedItem();
            String empId = selected.substring(selected.indexOf("(") + 1, selected.indexOf(")"));
            
            // Set parameters using all fields
            pstmt.setString(1, empId);
            pstmt.setDate(2, Date.valueOf(reviewDateField.getText()));
            pstmt.setString(3, (String) reviewTypeCombo.getSelectedItem());
            pstmt.setInt(4, Integer.parseInt(scoreField.getText()));
            pstmt.setString(5, reviewContentArea.getText());
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "考核保存成功");
            }
        } catch (SQLException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "保存失败: " + ex.getMessage());
        }
    }
}