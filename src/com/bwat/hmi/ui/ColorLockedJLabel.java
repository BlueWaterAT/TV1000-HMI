package com.bwat.hmi.ui;

import javax.swing.Icon;
import javax.swing.JLabel;
import java.awt.Color;

public class ColorLockedJLabel extends JLabel {
    public ColorLockedJLabel() {
        super();
    }

    public ColorLockedJLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
    }

    public ColorLockedJLabel(Icon image) {
        super(image);
    }

    public ColorLockedJLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
    }

    public ColorLockedJLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }

    public ColorLockedJLabel(String text) {
        super(text);
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
