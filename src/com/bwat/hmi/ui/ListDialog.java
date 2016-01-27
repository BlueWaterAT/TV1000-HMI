package com.bwat.hmi.ui;

import com.bwat.hmi.Constants;
import com.bwat.hmi.HMI;
import com.bwat.hmi.util.SwingUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A popup input dialog that select a value from a list
 *
 * @author Kareem El-Faramawi
 */
public class ListDialog extends JDialog {
    private static ListDialog dialog;
    private static String value = "";
    private JList list;


    private static Color bg;
    private static Color fg;
    private static Color buttonColor;

    /**
     * Displays the List Dialog with the given parameters. The code that calls this is halted until a value is
     * returned
     *
     * @param title          Title of the dialog
     * @param possibleValues All the possible values to choose from
     * @param initialValue   Initially selected value, can be null
     * @param font           Font settings
     * @param fontSize       Font size
     * @return The resulting value selection
     */
    public static String showDialog(String title, String[] possibleValues, String initialValue, Font font, float fontSize) {
        Frame frame = JOptionPane.getFrameForComponent(HMI.getInstance());
        dialog = new ListDialog(frame, null, title, possibleValues, initialValue, font, fontSize);
        dialog.setVisible(true);
        return value;
    }

    /**
     * Selects the given value in the list
     *
     * @param newValue Value to select
     */
    private void setValue(String newValue) {
        value = newValue;
        list.setSelectedValue(value, true);
    }

    /**
     * Creates a ListDialog with the given parameters
     *
     * @param frame        Owner frame
     * @param locationComp Reference component for positioning
     * @param title        Title of dialog
     * @param data         All possible selections
     *                     * @param initialValue Initially selected value
     * @param font         Font settings
     * @param fontSize     Font size
     */
    private ListDialog(Frame frame, Component locationComp, String title, Object[] data, String initialValue, Font font, float fontSize) {
        super(frame, title, true);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ListDialog.dialog.setVisible(false);
            }
        });

        final JButton setButton = new JButton("Set");
        setButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ListDialog.value = (String) (list.getSelectedValue());
                ListDialog.dialog.setVisible(false);
            }
        });

        list = new JList<Object>(data);
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    setButton.doClick();
                }
            }
        });
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setAlignmentX(LEFT_ALIGNMENT);

        JPanel panel = new JPanel(new BorderLayout(Constants.HV_GAP, Constants.HV_GAP));
        panel.add(listScroller, BorderLayout.CENTER);
        panel.add(SwingUtils.createGridJPanel(1, 2, Constants.HV_GAP, Constants.HV_GAP, cancelButton, setButton), BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createEmptyBorder(Constants.HV_GAP, Constants.HV_GAP, Constants.HV_GAP, Constants.HV_GAP));
        add(panel);

        setValue(initialValue);
        SwingUtils.setFont_r(this, font.deriveFont(fontSize));
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
        ListDialog.bg = bg;
        ListDialog.fg = fg;
        ListDialog.buttonColor = button;
    }
}
