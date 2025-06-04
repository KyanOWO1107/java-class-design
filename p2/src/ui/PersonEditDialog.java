package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PersonEditDialog extends JDialog {
    private JTextField idField = new JTextField(15);
    private JTextField nameField = new JTextField(15);
    private JTextField deptField = new JTextField(15);
    private JTextField positionField = new JTextField(15);
    private JTextField phoneField = new JTextField(15);
    private boolean saved = false;

    // Modified constructor to accept JInternalFrame parent and title
    public PersonEditDialog(JInternalFrame parent, String title) {
        super(SwingUtilities.getWindowAncestor(parent.getDesktopPane().getParent()), 
              title, 
              Dialog.ModalityType.APPLICATION_MODAL);
        setupUI();
        setSize(400, 250);
    }

    private void setupUI() {
        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("工号："));
        panel.add(idField);
        panel.add(new JLabel("姓名："));
        panel.add(nameField);
        panel.add(new JLabel("部门："));
        panel.add(deptField);
        panel.add(new JLabel("职位："));
        panel.add(positionField);
        panel.add(new JLabel("联系电话："));
        panel.add(phoneField);

        JButton saveBtn = new JButton("保存");
        saveBtn.addActionListener(this::saveAction);
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dispose());

        panel.add(saveBtn);
        panel.add(cancelBtn);
        add(panel);
    }

    private void saveAction(ActionEvent e) {
        if (validateFields()) {
            saved = true;
            dispose();
        }
    }

    private boolean validateFields() {
        if (idField.getText().trim().isEmpty() || 
            nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "工号和姓名不能为空", "输入错误", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (!phoneField.getText().matches("\\d{11}")) {
            JOptionPane.showMessageDialog(this, "联系电话必须是11位数字", "输入错误", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean isSaved() { return saved; }
    public String getStaffId() { return idField.getText(); }
    public String getName() { return nameField.getText(); }
    public String getDepartment() { return deptField.getText(); }
    public String getPosition() { return positionField.getText(); }
    public String getPhone() { return phoneField.getText(); }

    public void setPersonData(String[] data) {
        idField.setText(data[0]);
        nameField.setText(data[1]);
        deptField.setText(data[2]);
        positionField.setText(data[3]);
        phoneField.setText(data[4]);
    }
}