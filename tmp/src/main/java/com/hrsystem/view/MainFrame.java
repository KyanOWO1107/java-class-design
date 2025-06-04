import javax.swing.*;


public class MainFrame extends JFrame {
    // 左侧树形菜单
    JTree menuTree = new JTree(createTreeModel());
    menuTree.addTreeSelectionListener(e -> {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
        showFunctionPanel(node.getUserObject().toString());
    });
    
    // 右侧功能面板
    JPanel contentPanel = new JPanel(new CardLayout());
    // 添加功能面板映射
    private Map<String, JPanel> functionPanels = new HashMap<>();
    private CardLayout cardLayout;

    private void createUI() {
        // 左侧树形菜单
        JTree menuTree = new JTree(createTreeModel());
        menuTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
            showFunctionPanel(node.getUserObject().toString());
        });
        
        // 右侧功能面板
        JPanel contentPanel = new JPanel(new CardLayout());
        // 初始化功能面板
        initFunctionPanels();
        contentPanel.add(functionPanels.get("default"), "default");
        
        // 添加分割布局
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
            new JScrollPane(menuTree), contentPanel);
        add(splitPane, BorderLayout.CENTER);
    }

    private void initFunctionPanels() {
        // 添加人员信息面板
        functionPanels.put("添加人员信息", createAddEmployeePanel());
        functionPanels.put("部门管理", createDepartmentPanel());
        // 其他功能面板初始化...
    }

    private JPanel createAddEmployeePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 姓名输入
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("姓名:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        panel.add(nameField, gbc);

        // 性别选择
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("性别:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"男", "女"});
        panel.add(genderCombo, gbc);

        // 部门选择（需要从数据库加载）
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("部门:"), gbc);
        gbc.gridx = 1;
        JComboBox<Department> deptCombo = new JComboBox<>();
        loadDepartmentsToCombo(deptCombo); // 需实现部门数据加载
        panel.add(deptCombo, gbc);

        // 提交按钮
        gbc.gridy++;
        gbc.gridx = 1;
        JButton submitBtn = new JButton("提交");
        submitBtn.addActionListener(e -> saveEmployee(
            nameField.getText(),
            genderCombo.getSelectedItem().toString(),
            (Department) deptCombo.getSelectedItem()
        ));
        panel.add(submitBtn, gbc);

        return panel;
    }

    private void showFunctionPanel(String functionName) {
        JPanel targetPanel = functionPanels.getOrDefault(functionName, 
            functionPanels.get("default"));
        cardLayout.show(contentPanel, functionName);
    }

    private void loadDepartmentsToCombo(JComboBox<Department> combo) {
        combo.removeAllItems();
        try {
            List<Department> departments = DepartmentDaoFactory.getInstance().getAllDepartments();
            departments.forEach(combo::addItem);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "部门数据加载失败: " + e.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createDepartmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 部门树形展示
        JTree deptTree = new JTree();
        deptTree.setModel(new DefaultTreeModel(buildDepartmentTree()));
        
        // 操作按钮组
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("新增部门");
        JButton editBtn = new JButton("修改部门");
        JButton delBtn = new JButton("删除部门");
        
        addBtn.addActionListener(e -> showAddDepartmentDialog());
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        
        panel.add(new JScrollPane(deptTree), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private DefaultMutableTreeNode buildDepartmentTree() {
        // 需要实现从数据库加载部门层级结构
    }
}