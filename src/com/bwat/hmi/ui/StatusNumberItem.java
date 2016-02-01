package com.bwat.hmi.ui;

import com.bwat.hmi.Constants;
import com.bwat.hmi.data.BindedString;
import com.bwat.hmi.data.BindedStringListener;
import com.bwat.hmi.util.MathUtils;
import com.bwat.hmi.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StatusNumberItem extends JPanel {
    Logger log = LoggerFactory.getLogger(getClass());

    // Raw value that gets auto updated
    private BindedString valueSource;

    // Value display
    private JButton valueText;
    private JLabel label;

    // Value parameters
    private int value;
    private int min;
    private int max;

    /**
     * Creates a status item with the given parameters
     *
     * @param name Name of the item
     * @param path Path to the data file
     * @param min  Minimum allowed value
     * @param max  Maximum allowed value
     */
    public StatusNumberItem(String name, String path, int min, int max) {
        this.min = min;
        this.max = max;

        // Listen for changes to the value file and update
        valueSource = new BindedString(path, new BindedStringListener() {
            @Override
            public void stringChanged(String content) {
                content = content.trim();
                try {
                    setValue(Integer.parseInt(content));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(StatusNumberItem.this, "Invalid file content \"" + content + "\", cannot be parsed as int");
                   log.error("Invalid file content, cannot be parsed as int");
                }
            }
        });

        valueText = new JButton();
        valueText.setHorizontalAlignment(JTextField.CENTER);
        valueText.setFocusable(false);
        // Open up a keypad dialog to manually update the value
        valueText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String val = KeypadDialog.showDialog("Enter Value", "");
                if (StringUtils.isNumber(val)) {
                    setValue(Integer.parseInt(val));
                }
            }
        });

        // Try setting the initial value
        try {
            setValue(Integer.parseInt(valueSource.getContent().trim()));
        } catch (NumberFormatException e) {
            setValue(0);
        }

        label = new JLabel(name);

        setLayout(new BorderLayout(Constants.HV_GAP, 0));
        add(label, BorderLayout.WEST);
        add(valueText, BorderLayout.CENTER);
    }

    // These get the individual components for manual layout
    public JLabel getLabel() {
        return label;
    }

    public JButton getValueButton() {
        return valueText;
    }

    public StatusNumberItem(String name, String path) {
        this(name, path, 0, 100);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int val) {
        this.value = MathUtils.clamp_i(val, min, max);
        valueText.setText(String.valueOf(value));
        valueSource.setContent(String.valueOf(value));
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
