import java.awt.*;

import javax.swing.*;

public class EditEmployeePanel extends JPanel {
    private JTextField searchField;
    private JTable resultTable;
    
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
        
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
}