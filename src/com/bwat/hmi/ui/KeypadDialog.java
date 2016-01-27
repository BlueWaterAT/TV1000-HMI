package com.bwat.hmi.ui;

import com.bwat.hmi.Constants;
import com.bwat.hmi.HMI;
import com.bwat.hmi.util.ArrayUtils;
import com.bwat.hmi.util.StringUtils;
import com.bwat.hmi.util.SwingUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * A popup input dialog using a 0-9 keypad
 *
 * @author Kareem El-Faramawi
 */
public class KeypadDialog extends JDialog {
    private static final String NEG = "-";

    private static KeypadDialog dialog;
    private static String value = ""; // Static return value for the dialog
    private JTextField valueText; // The displayed input field

    private static Color bg;
    private static Color fg;
    private static Color buttonColor;

    /**
     * Displays the Keypad Dialog with the given parameters. The code that calls this is halted until a value
     * is returned
     *
     * @param title        Title of the dialog
     * @param initialValue Initial value as a String to be displayed
     * @return The resulting value that is entered in the keypad
     */
    public static String showDialog(String title, String initialValue) {
        Frame frame = JOptionPane.getFrameForComponent(HMI.getInstance());
        dialog = new KeypadDialog(frame, null, title, initialValue, HMI.getInstance().getFont());
        dialog.setVisible(true);
        return value;
    }

    /**
     * @return The text contained in the displayed field
     */
    private String getValue() {
        return valueText != null ? valueText.getText() : "";

    }

    /**
     * Sets the text of the displayed field
     *
     * @param newValue New text
     */
    private void setValue(String newValue) {
        valueText.setText(newValue != null ? newValue : "");
    }

    /**
     * Adds a digit to the end of the current input
     *
     * @param dig Digit to append
     */
    private void addDigit(String dig) {
        setValue(getValue() + dig);
    }

    /**
     * Creates a KeypadDialog with the given parameters
     *
     * @param frame        Owner frame
     * @param locationComp Reference component for positioning
     * @param title        Title of dialog
     * @param initialValue Initial value of input field
     * @param font         Font settings
     */
    private KeypadDialog(Frame frame, Component locationComp, String title, String initialValue, Font font) {
        super(frame, title, true);

        valueText = new JTextField(initialValue != null && StringUtils.isNumber(initialValue) ? initialValue : "");
        valueText.setEditable(false);
        valueText.setFocusable(false);
        value = getValue(); // Set to initial value

        final JButton[] digits = new JButton[10];
        for (int i = 0; i < digits.length; i++) {
            digits[i] = new JButton("" + i);
            digits[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addDigit(((JButton) e.getSource()).getText());
                }
            });
        }

        JButton neg = new JButton("+/-");
        neg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String val = getValue();
                setValue(val.startsWith(NEG) ? val.substring(1) : NEG + val);
            }
        });

        JButton back = new JButton("\u2190");
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Delete one character off the end, make sure not to pass an index < 0
                setValue(getValue().substring(0, Math.max(0, getValue().length() - 1)));
            }
        });

        JButton clear = new JButton("CLEAR");
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setValue("");
            }
        });

        JButton enter = new JButton("ENTER");
        enter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                value = getValue(); // Set the static variable to the new value
                KeypadDialog.dialog.setVisible(false);
            }
        });

        JButton cancel = new JButton("CANCEL");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KeypadDialog.dialog.setVisible(false); // Quit without setting the new value
            }
        });

        JPanel panel = new JPanel(new BorderLayout(Constants.HV_GAP, Constants.HV_GAP));
        panel.add(valueText, BorderLayout.NORTH);
        panel.add(SwingUtils.createGridJPanel(5, 3, Constants.HV_GAP, Constants.HV_GAP, ArrayUtils.append(Arrays.copyOfRange(digits, 1, digits.length), back, digits[0], neg, cancel, clear, enter)), BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(Constants.HV_GAP, Constants.HV_GAP, Constants.HV_GAP, Constants.HV_GAP));
        add(panel);

        // Initialize values.
        setValue(initialValue);
        SwingUtils.setFont_r(this, font);
        pack();
        setSize(frame.getWidth(), frame.getHeight());
        setLocationRelativeTo(locationComp);

        SwingUtils.setBG_r(this, bg);
        SwingUtils.setFG_r(this, fg);
        for (Component c : SwingUtils.getAllComponents_r(this)) {
            if (c instanceof JButton) {
                c.setBackground(buttonColor);
            }
        }
    }

    public static void setColors(Color bg, Color fg, Color button) {
        KeypadDialog.bg = bg;
        KeypadDialog.fg = fg;
        KeypadDialog.buttonColor = button;
    }
}
