import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class SalaryManagementPanel extends JPanel {
    private JComboBox<String> employeeCombo;
    private JTextField salaryField;
    private JTextField effectiveDateField;
    private JTable salaryHistoryTable;
    
    public SalaryManagementPanel() {
        setLayout(new GridLayout(0, 2, 10, 10));
        
        employeeCombo = new JComboBox<>();
        salaryField = new JTextField(10);
        effectiveDateField = new JTextField(15);
        JButton saveButton = new JButton("保存调整");
        
        loadEmployees();
        
        add(new JLabel("选择员工:"));
        add(employeeCombo);
        add(new JLabel("新工资:"));
        add(salaryField);
        add(new JLabel("生效日期(yyyy-MM-dd):"));
        add(effectiveDateField);
        add(new JLabel());
        add(saveButton);
        
        saveButton.addActionListener(e -> saveSalaryAdjustment());
    
        // Add employee selection listener
        employeeCombo.addActionListener(e -> {
            String selected = (String) employeeCombo.getSelectedItem();
            if (selected != null) {
                String empId = selected.substring(selected.indexOf("(") + 1, selected.indexOf(")"));
                loadSalaryHistory(empId);
            }
        });
        
        // Add history table
        salaryHistoryTable = new JTable();
        add(new JScrollPane(salaryHistoryTable), BorderLayout.CENTER);
    }

    private void loadSalaryHistory(String empId) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT * FROM salary_history WHERE emp_id = ?")) {
            
            pstmt.setString(1, empId);
            ResultSet rs = pstmt.executeQuery();
            salaryHistoryTable.setModel(DBUtil.resultSetToTableModel(rs));
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载薪资历史失败: " + ex.getMessage());
        }
    }
    
    private void loadEmployees() {
        // 需要实现完整的员工加载逻辑（当前仅为注释）
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT emp_id, name FROM employee_info")) {
        
            employeeCombo.removeAllItems();
            while (rs.next()) {
                employeeCombo.addItem(rs.getString("name") + " (" + rs.getString("emp_id") + ")");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载员工数据失败: " + ex.getMessage());
        }
    }
    
    private void saveSalaryAdjustment() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO salary_history (emp_id, salary, effective_date) VALUES (?, ?, ?)")) {
            
            // Get selected employee ID
            String selected = (String) employeeCombo.getSelectedItem();
            String empId = selected.substring(selected.indexOf("(") + 1, selected.indexOf(")"));
            
            // Set parameters
            pstmt.setString(1, empId);
            pstmt.setBigDecimal(2, new BigDecimal(salaryField.getText()));
            pstmt.setDate(3, Date.valueOf(effectiveDateField.getText()));
            
            // Execute and get affected rows
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                // Refresh history using existing variables
                loadSalaryHistory(empId);
            }
        } catch (SQLException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "保存失败: " + ex.getMessage());
        }
    }
}