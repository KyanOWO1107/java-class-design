import java.awt.BorderLayout;
import javax.swing.tree.DefaultMutableTreeNode;

import javax.swing.*;


public class MainFrame extends JFrame {
    private JTree menuTree;
    private JPanel contentPanel;
    
    // 新增静态启动方法
    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
    
    public MainFrame() {
        // 初始化界面
        setTitle("人事管理系统");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 创建分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(200);
        
        // 左侧菜单树
        // Replace menu tree initialization
        menuTree = createNavigationTree();  // Changed from createMenuTree()
        splitPane.setLeftComponent(new JScrollPane(menuTree));
        
        // 右侧内容面板
        contentPanel = new JPanel(new BorderLayout());
        splitPane.setRightComponent(contentPanel);
        
        add(splitPane);
        
        // 添加树选择监听器
        menuTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                menuTree.getLastSelectedPathComponent();
            if (node != null && node.isLeaf()) {
                showSelectedPanel(node.getUserObject().toString());
            }
        });
    }
    
    // Remove the following unused method:
    // private JTree createMenuTree() {
    //     // 创建菜单树结构
    //     DefaultMutableTreeNode root = new DefaultMutableTreeNode("人事管理系统");
    //     
    //     // 基本信息管理
    //     DefaultMutableTreeNode basicInfoNode = new DefaultMutableTreeNode("基本信息管理");
    //     basicInfoNode.add(new DefaultMutableTreeNode("添加人员信息"));
    //     basicInfoNode.add(new DefaultMutableTreeNode("修改人员信息"));
    //     basicInfoNode.add(new DefaultMutableTreeNode("删除人员信息"));
    //     basicInfoNode.add(new DefaultMutableTreeNode("查询人员信息"));
    //     basicInfoNode.add(new DefaultMutableTreeNode("部门管理"));
    //     root.add(basicInfoNode);
    //     
    //     // 人员调动管理
    //     DefaultMutableTreeNode transferNode = new DefaultMutableTreeNode("人员调动管理");
    //     transferNode.add(new DefaultMutableTreeNode("人员调动"));
    //     transferNode.add(new DefaultMutableTreeNode("调动历史查询"));
    //     root.add(transferNode);
    //     
    //     // 人员考核管理
    //     DefaultMutableTreeNode reviewNode = new DefaultMutableTreeNode("人员考核管理");
    //     reviewNode.add(new DefaultMutableTreeNode("人员考核"));
    //     reviewNode.add(new DefaultMutableTreeNode("考核历史查询"));
    //     root.add(reviewNode);
    //     
    //     // 劳资管理
    //     DefaultMutableTreeNode salaryNode = new DefaultMutableTreeNode("劳资管理");
    //     salaryNode.add(new DefaultMutableTreeNode("劳资分配管理"));
    //     salaryNode.add(new DefaultMutableTreeNode("劳资历史查询"));
    //     root.add(salaryNode);
    //     
    //     return new JTree(root);
    // }
    
    private void showSelectedPanel(String panelName) {
        contentPanel.removeAll();
        
        switch (panelName) {
            case "添加人员信息":
                contentPanel.add(new AddEmployeePanel());
                break;
            case "修改人员信息":
                contentPanel.add(new EditEmployeePanel());
                break;
            case "删除人员信息":
                contentPanel.add(new DeleteEmployeePanel());
                break;
            case "人员调动":
                contentPanel.add(new EmployeeTransferPanel()); 
                break;
            case "查询人员信息":
                contentPanel.add(new SearchEmployeePanel());
                break;
            case "部门管理":
                contentPanel.add(new DepartmentManagementPanel());
                break;
            case "调动历史查询":
                contentPanel.add(new TransferHistoryPanel());
                break;
            case "人员考核":
                contentPanel.add(new PerformanceReviewPanel());
                break;
            case "考核历史查询":
                contentPanel.add(new ReviewHistoryPanel());
                break;
            case "劳资分配管理":
                contentPanel.add(new SalaryManagementPanel());
                break;
            case "劳资历史查询":
                contentPanel.add(new SalaryHistoryPanel());
                break;
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JTree createNavigationTree() {  // Changed return type from void to JTree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("人事管理系统");
        
        DefaultMutableTreeNode baseInfo = new DefaultMutableTreeNode("基本信息管理");
        baseInfo.add(new DefaultMutableTreeNode("新增员工"));
        baseInfo.add(new DefaultMutableTreeNode("编辑员工"));
        baseInfo.add(new DefaultMutableTreeNode("删除员工"));
        
        DefaultMutableTreeNode transfer = new DefaultMutableTreeNode("人员调动");
        transfer.add(new DefaultMutableTreeNode("调动记录"));
        
        root.add(baseInfo);
        root.add(new DefaultMutableTreeNode("考核管理"));
        root.add(transfer);
        root.add(new DefaultMutableTreeNode("薪资管理"));
        
        JTree tree = new JTree(root);
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
            switch(node.getUserObject().toString()) {
                case "新增员工": showSelectedPanel("添加人员信息"); break;
                case "编辑员工": showSelectedPanel("修改人员信息"); break;
                case "删除员工": showSelectedPanel("删除人员信息"); break;
            }
        });
        
        return tree;  // Add return statement
    }
}

