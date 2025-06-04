package ui;

import javax.swing.*;

import dao.AssetDAO;
import dao.AssetDAOImpl;

import java.awt.GridLayout;
import java.sql.SQLException;

public class OperationDialog extends JDialog {
    private JComboBox<String> assetComboBox;
    private JTextField operatorField;
    
    public OperationDialog(JFrame parent, String operationType) {
        super(parent, operationType, true);
        setSize(400, 300);
        
        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("选择资产："));
        assetComboBox = new JComboBox<>();
        panel.add(assetComboBox);
        panel.add(new JLabel("操作人："));
        operatorField = new JTextField();
        panel.add(operatorField);
        panel.add(new JLabel("操作时间："));
        panel.add(new JTextField());
        panel.add(new JButton("确认"));
        panel.add(new JButton("取消"));
        
        add(panel);
        
        // 获取确认按钮并添加事件监听
        JButton confirmBtn = (JButton) panel.getComponent(6); 
        confirmBtn.addActionListener(e -> performOperation(operationType));
        
        // 获取取消按钮并添加事件监听
        JButton cancelBtn = (JButton) panel.getComponent(7);
        cancelBtn.addActionListener(e -> dispose());
    }

// 将方法改为 public，以便外部可以调用
public void performOperation(String type) {
        try {
            AssetDAO dao = new AssetDAOImpl();
            String assetId = ((String) assetComboBox.getSelectedItem()).split("-")[0];
            
            switch(type) {
                case "资产领用":
                    dao.borrowAsset(assetId, operatorField.getText());
                    break;
                case "资产归还":
                    dao.returnAsset(assetId);
                    break;
                case "资产报废":
                    dao.scrapAsset(assetId);
                    break;
            }
            
            JOptionPane.showMessageDialog(this, "操作执行成功");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "操作失败：" + ex.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}