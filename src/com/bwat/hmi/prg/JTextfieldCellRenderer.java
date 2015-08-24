package com.bwat.hmi.prg;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

public class JTextfieldCellRenderer extends JTextField implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
		setText( (String) value );
		return this;
	}
	
}
