import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import javax.swing.*;

public class AddEmployeePanel extends JPanel {
    private JTextField empIdField, nameField, genderField, birthField, nationalityField;
    private JTextField addressField, salaryField, phoneField; 
    private JComboBox<String> deptComboBox;
    
    public AddEmployeePanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 初始化组件
        empIdField = new JTextField(20);
        nameField = new JTextField(20);
        genderField = new JTextField(10);
        birthField = new JTextField(10);
        nationalityField = new JTextField(20);
        addressField = new JTextField(30);
        salaryField = new JTextField(10);
        phoneField = new JTextField(15);
        
        // 部门下拉框
        deptComboBox = new JComboBox<>();
        loadDepartments();
        
        // 添加组件
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("员工编号:"), gbc);
        gbc.gridx = 1;
        add(empIdField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("姓名:"), gbc);
        gbc.gridx = 1;
        add(nameField, gbc);
        
        
        // 添加其他字段...
        
        // 添加按钮
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> saveEmployee());
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(saveButton, gbc);
    }
    
    private void loadDepartments() {
        // 从数据库加载部门数据
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT dept_id, dept_name FROM department")) {
            
            deptComboBox.removeAllItems();
            while (rs.next()) {
                deptComboBox.addItem(rs.getString("dept_name") + " (" + rs.getString("dept_id") + ")");
            }
            
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "加载部门数据失败: " + e.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    private void saveEmployee() {
        
        try {
            Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO employee_info (emp_id, name, gender, birth_date, nationality, " +
                "address, department_id, salary, phone, hire_date) " +  
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURDATE())");  
            
            pstmt.setString(1, empIdField.getText());
            pstmt.setString(2, nameField.getText());
            pstmt.setString(3, genderField.getText());
            pstmt.setString(4, birthField.getText());
            pstmt.setString(5, nationalityField.getText());
            pstmt.setString(6, addressField.getText());
            
            // 解析部门ID
            String selectedDept = (String) deptComboBox.getSelectedItem();
            String deptId = selectedDept.substring(selectedDept.indexOf("(") + 1, selectedDept.indexOf(")"));
            pstmt.setString(7, deptId);
            
            pstmt.setBigDecimal(8, new BigDecimal(salaryField.getText()));
            pstmt.setString(9, phoneField.getText());

            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "员工信息保存成功", 
                    "成功", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            }
            
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "保存员工信息失败: " + e.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearFields() {
        empIdField.setText("");
        nameField.setText("");
        genderField.setText("");
        birthField.setText("");
        nationalityField.setText("");
        addressField.setText("");
        salaryField.setText("");
        phoneField.setText("");
    }
}
