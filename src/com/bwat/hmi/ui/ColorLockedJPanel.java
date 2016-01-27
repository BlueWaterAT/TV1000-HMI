package com.bwat.hmi.ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.LayoutManager;

public class ColorLockedJPanel extends JPanel {
    public ColorLockedJPanel() {
        super();
    }

    public ColorLockedJPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    public ColorLockedJPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    public ColorLockedJPanel(LayoutManager layout) {
        super(layout);
    }

    @Override
    public void setBackground(Color bg) {
    }

    @Override
    public void setForeground(Color fg) {
    }

    public void setLockedBackground(Color bg) {
        super.setBackground(bg);
    }

    public void setLockedForeground(Color fg) {
        super.setForeground(fg);
    }
}
