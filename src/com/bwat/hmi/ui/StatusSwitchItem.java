package com.bwat.hmi.ui;

import com.bwat.hmi.Constants;
import com.bwat.hmi.data.BindedString;
import com.bwat.hmi.data.BindedStringListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;

/**
 * ON/OFF display that automatically updates from a file
 *
 * @author Kareem El-Faramawi
 */
public class StatusSwitchItem extends JPanel {
    ColorLockedJButton toggle;
    JLabel label;

    // Data that updates automatically
    BindedString statusString;

    public static final String STATUS_ON = "[ON]";
    public static final String STATUS_OFF = "[OFF]";
    public static final String STATUS_DISABLED = "[]";

    public StatusSwitchItem(String name, String path) {
        label = new JLabel(name, JLabel.LEFT);
        toggle = new ColorLockedJButton();
        toggle.setLockedForeground(Color.BLACK);

        statusString = new BindedString(path, new BindedStringListener() {
            @Override
            public void stringChanged(String content) {
                setStatus(content);
            }
        });
        setStatus(statusString.getContent().equals("") ? STATUS_DISABLED : statusString.getContent());

        setLayout(new BorderLayout(Constants.HV_GAP, Constants.HV_GAP));
        add(toggle, BorderLayout.WEST);
        add(label, BorderLayout.CENTER);
    }

    public void setStatus(String status) {
        status = status.replaceAll("\\s+", "");
        if (status.equals(STATUS_ON)) {
            toggle.setEnabled(true);
            //			toggle.setSelected( true );
            toggle.setText("ON");
            toggle.setLockedBackground(Color.GREEN);
        } else if (status.equals(STATUS_OFF)) {
            toggle.setEnabled(true);
            //			toggle.setSelected( false );
            toggle.setText("OFF");
            toggle.setLockedBackground(Color.RED);
        } else if (status.equals(STATUS_DISABLED)) {
            toggle.setEnabled(false);
        }
    }

}
