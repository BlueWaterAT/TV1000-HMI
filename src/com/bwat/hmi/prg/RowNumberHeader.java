package com.bwat.hmi.prg;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;


public class RowNumberHeader extends JTable {
	private JTable parent;
	
	public RowNumberHeader(JTable table, Color bg, Color fg) {
		super();
		parent = table;
		setAutoCreateColumnsFromModel( false );
		setModel( parent.getModel() );
		setSelectionModel( parent.getSelectionModel() );
		setAutoscrolls( false );
		getTableHeader().setBackground( bg );
		getTableHeader().setForeground( fg );
		
		TableColumn col = new TableColumn();
		addColumn( col );
//		TableCellRenderer r  = parent.getTableHeader()bg.;
		col.setCellRenderer( parent.getTableHeader().getDefaultRenderer() );
		col.setPreferredWidth( 50 );
		setPreferredScrollableViewportSize( getPreferredSize() );
		
	}
	
	public boolean isCellEditable( int row, int column ) {
		return false;
	}
	
	public int getColumnCount() {
		return 1;
	}
	
	public Object getValueAt( int row, int column ) {
		return new Integer( row + 1 );
	}
	
	public int getRowHeight() {
		return parent.getRowHeight();
	}
}
