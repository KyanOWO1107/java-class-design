package ui;

import javax.swing.*;
import java.awt.*;
import entity.Asset;

public class AssetEditDialog extends JDialog {
    private JTextField[] fields = new JTextField[5];
    private boolean saved = false;
    private Asset asset;

    public AssetEditDialog(JInternalFrame parent, Asset asset) {
        super(SwingUtilities.getWindowAncestor(parent.getDesktopPane().getParent()), 
              asset == null ? "新增资产" : "编辑资产", 
              Dialog.ModalityType.APPLICATION_MODAL);
        this.asset = asset != null ? asset : new Asset();
        initComponents();
    }

    private void initComponents() {
        setSize(500, 300);
        setLayout(new GridLayout(6, 2));
        
        String[] labels = {"资产名称:", "资产类型:", "型号规格:", "购置价格:", "购置日期:"};
        for (int i = 0; i < labels.length; i++) {
            add(new JLabel(labels[i]));
            fields[i] = new JTextField();
            if (asset != null && i < 4) fields[i].setText(getFieldValue(i));
            add(fields[i]);
        }
        
        JButton saveBtn = new JButton("保存");
        saveBtn.addActionListener(e -> save());
        add(saveBtn);
        
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dispose());
        add(cancelBtn);
    }

    private String getFieldValue(int index) {
        switch(index) {
            case 0: return asset.getAssetName();
            case 1: return asset.getType();
            case 2: return asset.getModel();
            case 3: return String.valueOf(asset.getPrice());
            default: return "";
        }
    }

    private void save() {
        asset.setAssetName(fields[0].getText());
        asset.setType(fields[1].getText());
        asset.setModel(fields[2].getText());
        asset.setPrice(Double.parseDouble(fields[3].getText()));
        saved = true;
        dispose();
    }

    public boolean isSaved() { return saved; }
    public Asset getAsset() { return asset; }
}
