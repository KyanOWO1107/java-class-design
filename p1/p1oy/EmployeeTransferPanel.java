import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class EmployeeTransferPanel extends JPanel {
    private JComboBox<String> employeeCombo;
    private JComboBox<String> departmentCombo;
    private JTextField transferDateField;
    
    public EmployeeTransferPanel() {
        setLayout(new GridLayout(0, 2, 10, 10));
        
        // 初始化组件
        employeeCombo = new JComboBox<>();
        departmentCombo = new JComboBox<>();
        transferDateField = new JTextField(15);
        JButton transferButton = new JButton("执行调动");
        
        // 加载数据
        loadEmployees();
        loadDepartments();
        
        // 添加组件
        add(new JLabel("选择员工:"));
        add(employeeCombo);
        add(new JLabel("目标部门:"));
        add(departmentCombo);
        add(new JLabel("调动日期(yyyy-MM-dd):"));
        add(transferDateField);
        add(new JLabel());
        add(transferButton);
        
        transferButton.addActionListener(e -> processTransfer());
    }
    
    private void loadEmployees() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT emp_id, name FROM employee_info")) {
            
            while (rs.next()) {
                employeeCombo.addItem(rs.getString("name") + " (" + rs.getString("emp_id") + ")");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载员工数据失败: " + ex.getMessage());
        }
    }
    
    private void loadDepartments() {
        // 复用AddEmployeePanel中的部门加载逻辑
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT dept_id, dept_name FROM department")) {
            
            while (rs.next()) {
                departmentCombo.addItem(rs.getString("dept_name") + " (" + rs.getString("dept_id") + ")");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载部门数据失败: " + ex.getMessage());
        }
    }
    
    private void processTransfer() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "UPDATE employee_info SET department_id = ? WHERE emp_id = ?")) {
            
            // 获取员工ID
            String empInfo = (String) employeeCombo.getSelectedItem();
            String empId = empInfo.substring(empInfo.indexOf("(")+1, empInfo.indexOf(")"));
            
            // 获取新部门ID
            String deptInfo = (String) departmentCombo.getSelectedItem();
            String newDeptId = deptInfo.substring(deptInfo.indexOf("(")+1, deptInfo.indexOf(")"));
            
            // 更新部门
            pstmt.setString(1, newDeptId);
            pstmt.setString(2, empId);
            int rows = pstmt.executeUpdate();
            
            if(rows > 0) {
                // 记录调动历史
                try(PreparedStatement historyStmt = conn.prepareStatement(
                    "INSERT INTO transfer_history (emp_id, transfer_date, new_dept_id) VALUES (?,?,?)")) {
                    
                    historyStmt.setString(1, empId);
                    historyStmt.setDate(2, Date.valueOf(transferDateField.getText()));
                    historyStmt.setString(3, newDeptId);
                    historyStmt.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "调动操作成功");
            }
        } catch (SQLException | StringIndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(this, "操作失败: " + ex.getMessage());
        }
    }
}