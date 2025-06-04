package ui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class PersonTableModel extends AbstractTableModel {
    private final String[] columnNames = {"工号", "姓名", "部门", "职位", "联系方式"};
    private List<Object[]> data = new ArrayList<>();

    public void setData(List<Object[]> data) {
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
        return data.get(row)[col];
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}