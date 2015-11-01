package com.bwat.hmi.prg;

import java.awt.Font;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;

public class JComboBoxCellEditor extends DefaultCellEditor implements TableCellEditor {
	public JComboBox jcb;
	
	public JComboBoxCellEditor( JComboBox jcb, Font f ) {
		super( jcb );
		jcb.setFont( f );
		this.jcb = jcb;
	}
}
