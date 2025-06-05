package ui;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

import dao.PersonDAOImpl;
import entity.Person;

// 需创建人员管理窗口框架
public class PersonManagementFrame extends JInternalFrame {
    // 实现人员增删改查方法
    private void addPerson() {
        PersonEditDialog dialog = new PersonEditDialog(this, "新增人员");
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            try {
                Person person = new Person();
                person.setStaffId(dialog.getStaffId());
                person.setName(dialog.getName());
                person.setDepartment(dialog.getDepartment());
                person.setPosition(dialog.getPosition());
                person.setPhone(dialog.getPhone());
                
                new PersonDAOImpl().add(person);
                refreshTableData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "保存失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }


private JTable personTable;

public PersonManagementFrame() {
    super("人员信息管理", true, true, true, true);
    initComponents();
    setSize(800, 400);
}

private void initComponents() {
    // 初始化人员表格
    PersonTableModel model = new PersonTableModel();
    personTable = new JTable(model);
    
    JScrollPane scrollPane = new JScrollPane(personTable);
    add(scrollPane, BorderLayout.CENTER);

    // 操作按钮面板
    JPanel buttonPanel = new JPanel();
    JButton addBtn = new JButton("新增人员");
    addBtn.addActionListener(e -> addPerson());  // 直接调用新增方法
    buttonPanel.add(addBtn);
    
    JButton editBtn = new JButton("修改信息");
    editBtn.addActionListener(e -> editSelected());
    buttonPanel.add(editBtn);
    
    JButton delBtn = new JButton("删除人员");
    delBtn.addActionListener(e -> deleteSelected());
    buttonPanel.add(delBtn);
    add(buttonPanel, BorderLayout.SOUTH);
    
    // 添加人员查询面板
    JPanel searchPanel = new JPanel();
    JTextField searchField = new JTextField(20);
    JButton searchBtn = new JButton("搜索");
    
    searchBtn.addActionListener(e -> {
        String keyword = searchField.getText();
        try {
            List<Object[]> results = new PersonDAOImpl().searchPersons(keyword);
            ((PersonTableModel)personTable.getModel()).setData(results);
            personTable.updateUI();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "搜索失败: " + ex.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    });
    
    searchPanel.add(new JLabel("姓名/工号:"));
    searchPanel.add(searchField);
    searchPanel.add(searchBtn);
    add(searchPanel, BorderLayout.NORTH);
    
    refreshTableData();
}

private void editSelected() {
    int selectedRow = personTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "请先选择要修改的人员");
        return;
    }
    
    // 获取选中人员数据
    String[] personData = {
        (String) personTable.getValueAt(selectedRow, 0), // 工号
        (String) personTable.getValueAt(selectedRow, 1), // 姓名
        (String) personTable.getValueAt(selectedRow, 2), // 部门
        (String) personTable.getValueAt(selectedRow, 3), // 职位
        (String) personTable.getValueAt(selectedRow, 4)  // 联系方式
    };
    
    // Updated constructor call matches new signature
    PersonEditDialog dialog = new PersonEditDialog(this, "编辑人员信息");
    dialog.setPersonData(personData);
    dialog.setVisible(true);
}

private void deleteSelected() {
    int selectedRow = personTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "请先选择要删除的人员");
        return;
    }
    
    int confirm = JOptionPane.showConfirmDialog(this, 
        "确定要删除该人员记录吗？", "确认删除", JOptionPane.YES_NO_OPTION);
    
    if (confirm == JOptionPane.YES_OPTION) {
        try {
            String staffId = (String) personTable.getValueAt(selectedRow, 0);
            new PersonDAOImpl().delete(staffId);
            refreshTableData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "数据库操作失败：" + ex.getMessage(),
                "错误", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

private void refreshTableData() {
    try {
        PersonTableModel model = (PersonTableModel) personTable.getModel();
        model.setData(new PersonDAOImpl().getAll());
        personTable.updateUI();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this,
            "数据加载失败：" + ex.getMessage(),
            "错误",
            JOptionPane.ERROR_MESSAGE);
    }
}
}

class PersonTableModel extends AbstractTableModel {
    private String[] columnNames = {"工号", "姓名", "部门", "职位", "联系电话"};
    private List<Object[]> data;
    
    public void setData(List<Object[]> data) {
        this.data = data;
        fireTableDataChanged();
    }
    
    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return columnNames.length; }
    @Override public Object getValueAt(int row, int col) { return data.get(row)[col]; }
    @Override
    public String getColumnName(int column) { return columnNames[column]; }

// ▼▼▼ Remove this residual method declaration ▼▼▼
// 在PersonDAOImpl中添加搜索方法
// public List<Object[]> searchPersons(String keyword) throws SQLException {
//     String sql = "SELECT * FROM staff WHERE name LIKE ? OR staff_id LIKE ?";
//     List<Object[]> results = new ArrayList<>();
//     // ... 实现搜索逻辑 ...
// }
// ▲▲▲ Remove up to here ▲▲▲
}