import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;

import javax.swing.*;

public class EditEmployeePanel extends JPanel {
    private JTextField searchField;
    private JTable resultTable;
    private JTextField nameField, deptField, salaryField;

    public EditEmployeePanel() {
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
        
        // 编辑面板
        JPanel editPanel = new JPanel(new GridLayout(0, 2));
        nameField = new JTextField(20);
        deptField = new JTextField(15);
        salaryField = new JTextField(10);
        
        editPanel.add(new JLabel("姓名:"));
        editPanel.add(nameField);
        editPanel.add(new JLabel("部门:"));
        editPanel.add(deptField);
        editPanel.add(new JLabel("薪资:"));
        editPanel.add(salaryField);
        
        JButton saveButton = new JButton("保存修改");
        saveButton.addActionListener(e -> saveChanges());
        editPanel.add(saveButton);

        // 添加组件到主面板
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(editPanel, BorderLayout.SOUTH);
        
        // Add search button listener inside constructor
        searchButton.addActionListener(e -> loadSearchResults());
    }
    
    private void saveChanges() {
        // 需要实现保存逻辑（当前为空）
        // 建议增加以下功能：
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "UPDATE employee_info SET name=?, department_id=?, salary=? WHERE emp_id=?")) {
            
            // 获取选择的员工ID
            int selectedRow = resultTable.getSelectedRow();
            String empId = resultTable.getValueAt(selectedRow, 0).toString();
            
            // 解析部门ID（需要与AddEmployeePanel相同的逻辑）
            String deptInfo = deptField.getText();
            String deptId = deptInfo.substring(deptInfo.indexOf("(")+1, deptInfo.indexOf(")"));
            
            pstmt.setString(1, nameField.getText());
            pstmt.setString(2, deptId);
            pstmt.setBigDecimal(3, new BigDecimal(salaryField.getText()));
            pstmt.setString(4, empId);
            
            int rows = pstmt.executeUpdate();
            if(rows > 0) {
                JOptionPane.showMessageDialog(this, "修改保存成功");
                loadSearchResults(); // 需要实现该方法
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "保存失败: " + ex.getMessage());
        }
    }
    

    // 新增搜索方法
    private void loadSearchResults() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT emp_id, name, department_id, salary FROM employee_info WHERE emp_id LIKE ? OR name LIKE ?")) {
            
            String keyword = "%" + searchField.getText() + "%";
            pstmt.setString(1, keyword);
            pstmt.setString(2, keyword);
            
            ResultSet rs = pstmt.executeQuery();
            resultTable.setModel(DBUtil.resultSetToTableModel(rs));
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "查询失败: " + ex.getMessage());
        }
    }
}
