package com.bwat.hmi.ui;

import com.bwat.hmi.Constants;
import com.bwat.hmi.data.BindedString;
import com.bwat.hmi.data.BindedStringListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;

/**
 * A simple I/O display using a 1/0 label and color
 *
 * @author Kareem El-Faramawi
 */
public class IOItem extends JPanel {
    public static final String IO_ON = "[ON]";
    public static final String IO_OFF = "[OFF]";

    ColorLockedJPanel ioStatusPanel;
    BindedString ioString;

    public IOItem(String name, String path, String ioName) {
        setLayout(new BorderLayout(Constants.HV_GAP, 0));

        ioStatusPanel = new ColorLockedJPanel(new BorderLayout());
        ColorLockedJLabel ioLabel = new ColorLockedJLabel(ioName);
        ioLabel.setLockedForeground(Color.BLACK);
        ioStatusPanel.add(ioLabel, BorderLayout.CENTER);

        ioString = new BindedString(path, new BindedStringListener() {
            @Override
            public void stringChanged(String content) {
                setIOStatus(content);
            }
        });
        setIOStatus(ioString.getContent().equals("") ? IO_OFF : ioString.getContent());

        add(ioStatusPanel, BorderLayout.WEST);
        add(new JLabel(name), BorderLayout.CENTER);
    }

    public void setIOStatus(String status) {
        status = status.replaceAll("\\s+", "");
        if (status.equals(IO_ON)) {
            ioStatusPanel.setLockedBackground(Color.GREEN);
        } else if (status.equals(IO_OFF)) {
            ioStatusPanel.setLockedBackground(Color.RED);
        }
    }
}
