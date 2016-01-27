package com.bwat.hmi.ui;

import com.bwat.hmi.Constants;
import com.bwat.hmi.HMI;
import com.bwat.hmi.util.StringUtils;
import com.bwat.hmi.util.SwingUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KeyboardDialog extends JDialog {

    private static KeyboardDialog dialog;
    private static String value = ""; // Static return value for the dialog
    private JTextField valueText; // The displayed input field

    private boolean capsLock = false;
    private boolean shift = false;
    private CardLayout cards = null;
    private JPanel keyPanel = null;
    private final static String PAGE_REG = "REG";
    private final static String PAGE_SHIFT = "SHIFT";

    // Regular rows
    private final static String R1 = "`1234567890-=";
    private final static String R2 = "qwertyuiop[]\\";
    private final static String R3 = "asdfghjkl;'";
    private final static String R4 = "zxcvbnm,./";

    // Shift rows
    private final static String R1S = "~!@#$%^&*()_+";
    private final static String R2S = "QWERTYUIOP{}|";
    private final static String R3S = "ASDFGHJKL:\"";
    private final static String R4S = "ZXCVBNM<>?";

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
        dialog = new KeyboardDialog(frame, null, title, initialValue, HMI.getInstance().getFont());
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


    private void addChar(String chr) {
        setValue(getValue() + chr);
    }

    private void setShift(boolean shift) {
        cards.show(keyPanel, shift ? PAGE_SHIFT : PAGE_REG);
        this.shift = shift;
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
    private KeyboardDialog(Frame frame, Component locationComp, String title, String initialValue, Font font) {
        super(frame, title, true);

        valueText = new JTextField(initialValue != null && StringUtils.isNumber(initialValue) ? initialValue : "");
        valueText.setEditable(false);
        valueText.setFocusable(false);
        value = getValue(); // Set to initial value

        cards = new CardLayout();
        keyPanel = new JPanel(cards);

        JPanel regularPanel = new JPanel(new GridBagLayout());
        JPanel shiftPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        // gbc.gridwidth = 15;
        // gbc.gridheight = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.weightx = 1;

        // First row
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        for (int i = 0; i < R1.length(); i++, gbc.gridx++) {
            regularPanel.add(new CharJButton(R1.charAt(i)), gbc);
            shiftPanel.add(new CharJButton(R1S.charAt(i), true), gbc);
        }

        gbc.gridwidth = 2;
        regularPanel.add(new BackButton(), gbc);
        shiftPanel.add(new BackButton(), gbc);

        // Second row
        gbc.gridy++;
        gbc.gridx = 0;
        regularPanel.add(new TabButton(), gbc);
        shiftPanel.add(new TabButton(), gbc);

        gbc.gridx += gbc.gridwidth;
        gbc.gridwidth = 1;
        for (int i = 0; i < R2.length(); i++, gbc.gridx++) {
            regularPanel.add(new CharJButton(R2.charAt(i)), gbc);
            shiftPanel.add(new CharJButton(R2S.charAt(i), true), gbc);
        }

        // Third row
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        regularPanel.add(new CapsButton(), gbc);
        shiftPanel.add(new CapsButton(), gbc);

        gbc.gridx += gbc.gridwidth;
        gbc.gridwidth = 1;
        for (int i = 0; i < R3.length(); i++, gbc.gridx++) {
            regularPanel.add(new CharJButton(R3.charAt(i)), gbc);
            shiftPanel.add(new CharJButton(R3S.charAt(i), true), gbc);
        }

        gbc.gridwidth = 2;
        regularPanel.add(new EnterButton(), gbc);
        shiftPanel.add(new EnterButton(), gbc);

        // Fourth row
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        regularPanel.add(new ShiftButton(), gbc);
        shiftPanel.add(new ShiftButton(), gbc);

        gbc.gridx += gbc.gridwidth;
        gbc.gridwidth = 1;
        for (int i = 0; i < R4.length(); i++, gbc.gridx++) {
            regularPanel.add(new CharJButton(R4.charAt(i)), gbc);
            shiftPanel.add(new CharJButton(R4S.charAt(i), true), gbc);
        }

        gbc.gridwidth = 3;
        regularPanel.add(new ShiftButton(), gbc);
        shiftPanel.add(new ShiftButton(), gbc);

        // Fifth row
        gbc.gridy++;
        gbc.gridx = 0;

        gbc.gridwidth = 3;
        regularPanel.add(new ClearButton(), gbc);
        shiftPanel.add(new ClearButton(), gbc);

        gbc.gridx += gbc.gridwidth;
        gbc.gridwidth = 2;
        regularPanel.add(new JLabel(), gbc);
        shiftPanel.add(new JLabel(), gbc);

        gbc.gridx += gbc.gridwidth;
        gbc.gridwidth = 5;
        regularPanel.add(new CharJButton(' '), gbc);
        shiftPanel.add(new CharJButton(' '), gbc);

        gbc.gridx += gbc.gridwidth;
        gbc.gridwidth = 2;
        regularPanel.add(new JLabel(), gbc);
        shiftPanel.add(new JLabel(), gbc);

        gbc.gridx += gbc.gridwidth;
        gbc.gridwidth = 3;
        regularPanel.add(new CancelButton(), gbc);
        shiftPanel.add(new CancelButton(), gbc);

        keyPanel.add(regularPanel, PAGE_REG);
        keyPanel.add(shiftPanel, PAGE_SHIFT);
        setShift(false);

        JPanel panel = new JPanel(new BorderLayout(Constants.HV_GAP, Constants.HV_GAP));
        panel.add(valueText, BorderLayout.NORTH);
        panel.add(keyPanel, BorderLayout.CENTER);
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
        KeyboardDialog.bg = bg;
        KeyboardDialog.fg = fg;
        KeyboardDialog.buttonColor = button;
    }

    class CharJButton extends JButton {
        public CharJButton(char name) {
            this(name, false);
        }

        public CharJButton(final char name, final boolean shift) {
            super("" + name);
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    addChar("" + name);
                    if (shift && !capsLock) {
                        setShift(false);
                    }
                }
            });
        }
    }

    class BackButton extends JButton {
        public BackButton() {
            super("\u2190");
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Delete one character off the end, make sure not to pass an index < 0
                    setValue(getValue().substring(0, Math.max(0, getValue().length() - 1)));
                }
            });
        }
    }

    class TabButton extends CharJButton {
        public TabButton() {
            super('\t');
            setText("Tab");
        }
    }

    class CapsButton extends JButton {
        public CapsButton() {
            super("Caps Lock");
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    capsLock = !capsLock;
                    setShift(capsLock);
                }
            });
        }
    }

    class EnterButton extends JButton {
        public EnterButton() {
            super("Enter");
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    value = getValue(); // Set the static variable to the new value
                    KeyboardDialog.dialog.setVisible(false);
                }
            });
        }
    }

    class ShiftButton extends JButton {
        public ShiftButton() {
            super("Shift");
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    capsLock = false;
                    setShift(!KeyboardDialog.this.shift);
                }
            });
        }
    }

    class CancelButton extends JButton {
        public CancelButton() {
            super("Cancel");
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    KeyboardDialog.dialog.setVisible(false); // Quit without setting the new value
                }
            });
        }
    }

    class ClearButton extends JButton {
        public ClearButton() {
            super("Clear");
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setValue("");
                }
            });
        }
    }
}
