package com.bwat.hmi.prg;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;
import java.awt.Font;

public class JComboBoxCellEditor extends DefaultCellEditor implements TableCellEditor {
    public JComboBox jcb;

    public JComboBoxCellEditor(JComboBox jcb, Font f) {
        super(jcb);
        jcb.setFont(f);
        this.jcb = jcb;
    }
}
