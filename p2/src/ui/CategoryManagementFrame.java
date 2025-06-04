package ui;

import javax.swing.*;

import dao.CategoryDAO;

import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.awt.event.ActionListener;

public class CategoryManagementFrame extends JInternalFrame {
    private JTable categoryTable;
    
    public CategoryManagementFrame() {
        super("资产分类管理", true, true, true, true);
        initComponents();
        setSize(600, 400);
    }

    private void initComponents() {
        // 初始化表格模型
        CategoryTableModel model = new CategoryTableModel();
        // 添加测试数据
// 原代码中 List.of 方法使用有误，在 Java 中，需要先导入 java.util.Arrays 类，使用 Arrays.asList 来创建列表
model.setData(Arrays.asList(
            new Object[]{"C001", "电子设备"},
            new Object[]{"C002", "办公家具"}
        ));
        categoryTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(categoryTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // 操作按钮面板
        JPanel buttonPanel = new JPanel();
// 导入 ActionListener 类
buttonPanel.add(createButton("新增", (ActionListener) e -> addCategory()));
        buttonPanel.add(createButton("修改", e -> editCategory())); 
        buttonPanel.add(createButton("删除", e -> deleteCategory()));
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.addActionListener(action);
        return btn;
    }

    private void addCategory() {
        String categoryName = JOptionPane.showInputDialog(this, "请输入分类名称：");
        if (categoryName != null && !categoryName.trim().isEmpty()) {
            try {
                new CategoryDAO().addCategory(categoryName);
                // 替换为数据库查询
                refreshTableData();
                JOptionPane.showMessageDialog(this, "分类添加成功");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "数据库操作失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editCategory() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要修改的分类");
            return;
        }
        
        String oldName = (String) categoryTable.getValueAt(selectedRow, 1);
        String newName = JOptionPane.showInputDialog(this, "修改分类名称", oldName);
        
        if (newName != null && !newName.trim().isEmpty() && !newName.equals(oldName)) {
            try {
                new CategoryDAO().updateCategory(oldName, newName);  // 需要DAO实现update方法
                refreshTableData();
                JOptionPane.showMessageDialog(this, "分类修改成功");
            } catch (SQLException ex) {
                showDatabaseError(ex);
            }
        }
    }

    private void deleteCategory() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的分类");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "确定要删除该分类吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String categoryName = (String) categoryTable.getValueAt(selectedRow, 1);
                new CategoryDAO().deleteCategory(categoryName);  // 需要DAO实现delete方法
                refreshTableData();
                JOptionPane.showMessageDialog(this, "分类删除成功");
            } catch (SQLException ex) {
                showDatabaseError(ex);
            }
        }
    }

    // 新增辅助方法
    private void refreshTableData() throws SQLException {
        CategoryTableModel model = (CategoryTableModel) categoryTable.getModel();
        model.setData(new CategoryDAO().getAllCategoriesWithID());  // 需要DAO实现获取带ID的数据
        categoryTable.updateUI();
    }

    private void showDatabaseError(SQLException ex) {
        JOptionPane.showMessageDialog(this, 
            "数据库错误：" + ex.getMessage(),
            "操作失败", 
            JOptionPane.ERROR_MESSAGE);
    }
}
