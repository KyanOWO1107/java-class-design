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

public class TransferHistoryPanel extends JPanel {
    private JTable transferTable;
    
    public TransferHistoryPanel() {
        setLayout(new BorderLayout());
        
        // 搜索组件
        JPanel searchPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("搜索");
        searchPanel.add(new JLabel("员工编号/姓名:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // 结果表格
        transferTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(transferTable);
        
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        searchButton.addActionListener(e -> loadTransferHistory());
    }
    
    private void loadTransferHistory() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT t.*, e.name, d.dept_name FROM transfer_history t " +
                 "JOIN employee_info e ON t.emp_id = e.emp_id " +
                 "JOIN department d ON t.new_dept_id = d.dept_id")) {
            
            ResultSet rs = pstmt.executeQuery();
            transferTable.setModel(DBUtil.resultSetToTableModel(rs));
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载调动历史失败: " + ex.getMessage());
        }
    }
}