package com.bwat.hmi.prg;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public class JComboBoxCellRenderer extends JComboBox implements TableCellRenderer {
	public JComboBoxCellRenderer(Font f, Color bg, Color fg) {
		super();
		setFont( f );
		setBackground( bg );
		setForeground( fg );
	}
	
	public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
		setSelectedItem( value );
		return this;
	}
}