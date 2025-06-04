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

public class ReviewHistoryPanel extends JPanel {
    private JTable reviewTable;
    
    public ReviewHistoryPanel() {
        setLayout(new BorderLayout());
        
        // 搜索组件
        JPanel searchPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("搜索");
        searchPanel.add(new JLabel("员工编号/姓名:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // 结果表格
        reviewTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(reviewTable);
        
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        searchButton.addActionListener(e -> loadReviewHistory());
    }
    
    private void loadReviewHistory() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT r.*, e.name FROM performance_reviews r " +
                 "JOIN employee_info e ON r.emp_id = e.emp_id")) {
            
            ResultSet rs = pstmt.executeQuery();
            reviewTable.setModel(DBUtil.resultSetToTableModel(rs));
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载考核历史失败: " + ex.getMessage());
        }
    }
}