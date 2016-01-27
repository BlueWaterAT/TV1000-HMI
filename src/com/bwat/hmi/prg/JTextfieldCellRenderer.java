package com.bwat.hmi.prg;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

public class JTextfieldCellRenderer extends JTextField implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((String) value);
        return this;
    }

}
