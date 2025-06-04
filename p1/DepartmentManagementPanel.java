import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DepartmentManagementPanel extends JPanel {
    private JTree deptTree;
    private JTextField deptIdField, deptNameField, parentDeptField;
    private JComboBox<String> deptCombo; // Add combo box declaration

    public DepartmentManagementPanel() {
        setLayout(new BorderLayout());
        
        // 部门树形结构
        deptTree = new JTree(createDeptTreeModel());
        add(new JScrollPane(deptTree), BorderLayout.WEST);
        
        // 编辑面板
        JPanel editPanel = new JPanel(new GridLayout(0, 2));
        deptIdField = new JTextField(15);
        deptNameField = new JTextField(20);
        
        editPanel.add(new JLabel("部门编号:"));
        editPanel.add(deptIdField);
        editPanel.add(new JLabel("部门名称:"));
        editPanel.add(deptNameField);
        
        // 在编辑面板添加父部门输入字段
        parentDeptField = new JTextField(15);
        editPanel.add(new JLabel("上级部门ID:"));
        editPanel.add(parentDeptField);
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> saveDepartment());
        JButton deleteButton = new JButton("删除部门");
        deleteButton.addActionListener(e -> deleteDepartment());
        
        // Add delete button next to save button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        JButton refreshButton = new JButton("刷新部门树");
        buttonPanel.add(refreshButton);
        refreshButton.addActionListener(e -> refreshDepartmentTree());
    }
    
    private void saveDepartment() {
        try (Connection conn = DBUtil.getConnection()) {
            DBUtil.beginTransaction(conn);
            try (PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO department (dept_id, dept_name, parent_dept_id) VALUES (?, ?, ?)")) {
                
                pstmt.setString(1, deptIdField.getText());
                pstmt.setString(2, deptNameField.getText());
                pstmt.setString(3, parentDeptField.getText().isEmpty() ? null : parentDeptField.getText());
                pstmt.executeUpdate();
                
                refreshDepartmentTree();
                String newDeptInfo = deptIdField.getText() + " - " + deptNameField.getText();
                DBUtil.logOperation("部门新增", deptIdField.getText(), null, newDeptInfo);
                
                DBUtil.commitTransaction(conn);
                JOptionPane.showMessageDialog(this, "部门保存成功");
                
            } catch (SQLException ex) {
                DBUtil.rollbackTransaction(conn);
                throw ex; // Re-throw to outer catch
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "操作失败: " + ex.getMessage());
        }
    }


    // 新增部门树刷新方法
    private void refreshDepartmentTree() {
        deptTree.setModel(createDeptTreeModel());
        ((DefaultTreeModel)deptTree.getModel()).reload();
        expandAllNodes(deptTree);
    }

    private void expandAllNodes(JTree tree) {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }
    
    // 修改部门树创建方法
    private DefaultTreeModel createDeptTreeModel() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("所有部门");
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT d.dept_id, d.dept_name, d.parent_dept_id FROM department d")) {
            
            // 使用Map暂存节点以处理层级关系
            Map<String, DefaultMutableTreeNode> nodeMap = new HashMap<>();
            
            while (rs.next()) {
                String deptId = rs.getString("dept_id");
                String parentId = rs.getString("parent_dept_id");
                
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(
                    rs.getString("dept_name") + " (" + deptId + ")");
                nodeMap.put(deptId, node);
                
                if (parentId == null) {
                    root.add(node);
                } else {
                    DefaultMutableTreeNode parentNode = nodeMap.get(parentId);
                    if (parentNode != null) {
                        parentNode.add(node);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new DefaultTreeModel(root);
    }
    
    // 补充部门删除功能
    private void deleteDepartment() {
        String selected = (String) deptCombo.getSelectedItem();
        String deptId = selected.substring(selected.indexOf("(")+1, selected.indexOf(")"));
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "DELETE FROM department WHERE dept_id = ?")) {
            
            pstmt.setString(1, deptId);
            int rows = pstmt.executeUpdate();
            if(rows > 0) {
                JOptionPane.showMessageDialog(this, "部门删除成功");
                loadDepartments(); // 刷新部门列表
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "删除失败: " + ex.getMessage());
        }
    }

    // Add loadDepartments method
    private void loadDepartments() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT dept_id, dept_name FROM department")) {
            
            deptCombo.removeAllItems();
            while (rs.next()) {
                deptCombo.addItem(rs.getString("dept_name") + " (" + rs.getString("dept_id") + ")");
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载部门数据失败: " + ex.getMessage());
        }
    }
}