package ui;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import dao.AssetDAO;
import dao.AssetDAOImpl;
import entity.Asset;

import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;  // 添加List泛型导入

public class AssetManagementFrame extends JInternalFrame {
    private JTable assetTable;
    private AssetDAO assetDAO = new AssetDAOImpl();  // 添加DAO实例
    
    public AssetManagementFrame() {
        super("资产信息管理", true, true, true, true);
        initComponents();
        setSize(800, 600);
    }

    private void initComponents() {
        // 初始化资产表格
        AssetTableModel model = new AssetTableModel();
        assetTable = new JTable(model);
        
        JScrollPane scrollPane = new JScrollPane(assetTable);
        add(scrollPane, BorderLayout.CENTER);

        // 操作按钮面板
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("新增资产");
        addBtn.addActionListener(e -> showAddDialog());
        buttonPanel.add(addBtn);
        
        // 修复：提前声明和初始化editBtn
        JButton editBtn = new JButton("修改信息");
        editBtn.addActionListener(e -> editSelected());
        buttonPanel.add(editBtn);
        
        // 修复：提前声明和初始化delBtn
        JButton delBtn = new JButton("删除资产");
        delBtn.addActionListener(e -> deleteSelected());
        buttonPanel.add(delBtn);
        add(buttonPanel, BorderLayout.SOUTH);
        // 添加查询面板
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("搜索");
        // In the search button action listener:
        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText();
            try {
                List<Asset> results = assetDAO.searchAssets(keyword);
                model.setData(results);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(AssetManagementFrame.this,
                    "搜索失败: " + ex.getMessage(),
                    "数据库错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
        
        // In refreshTableData():
        private void refreshTableData() {
            try {
                AssetTableModel model = (AssetTableModel) assetTable.getModel();
                model.setData(assetDAO.getAllAssets()); // Use existing DAO instance
                assetTable.updateUI();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "数据加载失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        
        
        
        // In showAddDialog():
        private void showAddDialog() {
            AssetEditDialog dialog = new AssetEditDialog(this, null);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                try {
                    assetDAO.addAsset(dialog.getAsset());
                    refreshTableData();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                        "保存失败：" + ex.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    
        
        // Apply similar changes to editSelected() and deleteSelected()
        // Replace all new AssetDAOImpl() with assetDAO
    

    private void editSelected() {
        int selectedRow = assetTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要修改的资产");
            return;
        }
        
        Asset selected = ((AssetTableModel)assetTable.getModel()).getData().get(selectedRow);
        AssetEditDialog dialog = new AssetEditDialog(this, selected);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            try {
                new AssetDAOImpl().updateAsset(dialog.getAsset());
                refreshTableData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "更新失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelected() {
        int selectedRow = assetTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的资产");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "确定要删除该资产记录吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String assetId = (String) assetTable.getValueAt(selectedRow, 0);
                new AssetDAOImpl().deleteAsset(assetId);
                refreshTableData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "删除失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

class AssetTableModel extends AbstractTableModel {
    private String[] columnNames = {"资产编号", "资产名称", "类型", "型号", "价格", "购置日期", "状态"};
    private List<Asset> data = new ArrayList<>();  // 添加泛型参数声明
    
    public List<Asset> getData() {
        return data;
    }

    public void setData(List<Asset> data) {
        this.data = data;
        fireTableDataChanged();
    }
    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Asset asset = data.get(row);
        switch (col) {
            case 0: return asset.getAssetId();
            case 1: return asset.getAssetName();
            case 2: return asset.getType();
            case 3: return asset.getModel();
            case 4: return asset.getPrice();
            case 5: return new SimpleDateFormat("yyyy-MM-dd").format(asset.getPurchaseDate());
            case 6: return getStatusText(asset.getStatus());
            default: return null;
        }
    }

    private String getStatusText(int status) {
        return switch (status) {
            case 0 -> "正常";
            case 1 -> "领用中";
            case 2 -> "已报废";
            default -> "未知";
        };
    }
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}

// 将StatusCellRenderer移动至正确的位置
class StatusCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
    boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, 
            isSelected, hasFocus, row, column);
        if (column == 6) {
            // 保留选中状态颜色
            if (!isSelected) {
                setBackground(getStatusColor(value.toString()));
            }
            setHorizontalAlignment(SwingConstants.CENTER);
        }
        return c;
    }

    private Color getStatusColor(String status) {
        return switch (status) {
            case "正常" -> new Color(144, 238, 144);
            case "领用中" -> new Color(255, 218, 185);
            case "已报废" -> new Color(240, 128, 128);
            default -> Color.WHITE;
        };
    }
}