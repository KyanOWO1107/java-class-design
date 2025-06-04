package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JDesktopPane desktopPane;
    private JToolBar toolBar;
    private JPopupMenu systemMenu;

    public MainFrame() {
        initComponents();
        setupWindow();
    }

    private void initComponents() {
        desktopPane = new JDesktopPane();
        desktopPane.setBackground(new Color(240, 240, 240));
        
        createToolbar();
        createSystemMenu();
    }

    private void createToolbar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        // 系统管理按钮
        JButton sysBtn = createToolButton("系统管理");
        sysBtn.addActionListener(e -> showSystemMenu(sysBtn));
        toolBar.add(sysBtn);

        // 主要功能按钮
        String[] buttons = {
            "资产信息管理", "人员信息管理", 
            "资产领用", "资产归还", "资产报废"
        };
        
        for (String text : buttons) {
            JButton btn = createToolButton(text);
            if (text.equals("资产信息管理")) {
                btn.addActionListener(e -> showAssetManagement());
            } else if (text.equals("人员信息管理")) {  // 新增人员管理功能绑定
                btn.addActionListener(e -> {
                    PersonManagementFrame frame = new PersonManagementFrame();
                    desktopPane.add(frame);
                    frame.setVisible(true);
                });
            } else if (text.startsWith("资产")) {
                btn.addActionListener(e -> showOperationDialog(text));
            } else {
                btn.addActionListener(e -> showUnderConstruction());
            }
            toolBar.add(btn);
        }
    }

    private void createSystemMenu() {
        systemMenu = new JPopupMenu();
        JMenuItem categoryItem = new JMenuItem("分类管理");
        categoryItem.addActionListener(e -> showCategoryManagement());
        systemMenu.add(categoryItem);
    }

    private JButton createToolButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(5, 15, 5, 15));
        return btn;
    }

    private void showSystemMenu(Component invoker) {
        systemMenu.show(invoker, 0, invoker.getHeight());
    }

    private void setupWindow() {
        setTitle("资产管理系统 v1.0");
        setSize(1200, 800);
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(desktopPane, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // 功能方法
    private void showAssetManagement() {
        AssetManagementFrame frame = new AssetManagementFrame();
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private void showCategoryManagement() {
        CategoryManagementFrame frame = new CategoryManagementFrame(); // 使用已实现的分类管理界面
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private void showOperationDialog(String title) {
        OperationDialog dialog = new OperationDialog(this, title);
        dialog.setVisible(true); // 替换原有的提示消息，直接显示对话框
    }

    private void showUnderConstruction() {
        JOptionPane.showMessageDialog(this,
            "功能正在开发中，敬请期待",  // 改为简体
            "系统提示",
            JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}