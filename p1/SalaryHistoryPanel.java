import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class SalaryHistoryPanel extends JPanel {
    private JTable salaryTable;
    
    public SalaryHistoryPanel() {
        setLayout(new BorderLayout());
        
        JPanel searchPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("搜索");
        searchPanel.add(new JLabel("员工编号/姓名:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        salaryTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(salaryTable);
        
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        searchButton.addActionListener(e -> loadSalaryHistory());
    }
    
    private void loadSalaryHistory() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT s.*, e.name FROM salary_history s " +
                 "JOIN employee_info e ON s.emp_id = e.emp_id")) {
            
            ResultSet rs = pstmt.executeQuery();
            salaryTable.setModel(DBUtil.resultSetToTableModel(rs));
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载薪资历史失败: " + ex.getMessage());
        }
    }
}