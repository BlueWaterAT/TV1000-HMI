package com.bwat.hmi.prg;

import com.bwat.hmi.HMI;
import com.bwat.hmi.ui.KeyboardDialog;
import com.bwat.hmi.ui.KeypadDialog;
import com.bwat.hmi.ui.ListDialog;
import com.bwat.hmi.util.ArrayUtils;
import com.bwat.hmi.util.FileUtils;
import com.bwat.hmi.util.MathUtils;
import com.bwat.hmi.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

import static com.bwat.hmi.Constants.PROGRAM.*;

/**
 * A wrapper around a JTable that gives functionality for loading and saving the table,
 * as well as editing the column types
 *
 * @author Kareem ElFaramawi
 */
public class ProgramTable extends JTable {
    Logger log = LoggerFactory.getLogger(getClass());

    // List of all column types
    private ArrayList<CellType> columnTypes = new ArrayList<CellType>();
    private HashMap<Integer, String[]> comboValues = new HashMap<Integer, String[]>();

    // List of all mouseover tooltip messages
    private ArrayList<String> tooltips = new ArrayList<String>();

    // Used for keeping track of which column header was clicked
    private int popupCol = 0;

    public ProgramTable(int numRows, int numColumns) {
        super(numRows, numColumns);
        initGUI();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();
                int r = rowAtPoint(p), c = columnAtPoint(p);
                if (MathUtils.inRange_in_ex(r, 0, getRowCount()) && MathUtils.inRange_in_ex(c, 0, getColumnCount())) {
                    switch (getColumnType(c)) {
                        case TEXT:
                            setValueAt(KeyboardDialog.showDialog("Enter value: ", ""), r, c);
                            break;
                        case COMBO:
                            String[] vals = comboValues.get(c);
                            if (vals != null) {
                                // If this combobox contains only numeric entries, we sould open up a
                                // keypad instead of a selection screen
                                boolean numeric = true;
                                for (String val : vals) {
                                    if (!StringUtils.isNumber(val)) {
                                        numeric = false;
                                        break;
                                    }
                                }
                                String newValue;
                                if (numeric) {
                                    // Get a new value using the keypad interface
                                    newValue = KeypadDialog.showDialog("Enter Value", "");

                                    // Revert back to the original value if an invalid number is entered,
                                    // or the number entered is not an option
                                    if (!StringUtils.isNumber(newValue) || ArrayUtils.contains(vals, newValue)) {
                                        newValue = (String) getValueAt(r, c);
                                    }
                                } else {
                                    // Get a new value with an enlarged combo box selection screen
                                    newValue = ListDialog.showDialog("Select a Value", vals, (String) getValueAt(r, c), getFont(), 24f);
                                }

                                // Set the new value and make it visible
                                setValueAt(newValue, r, c);
                            }
                            break;
                        case NUMBER:
                            // Get a new value using the keypad interface
                            String newValue;
                            newValue = KeypadDialog.showDialog("Enter Value", "");
                            if (!StringUtils.isNumber(newValue)) {
                                Object tval = getValueAt(r, c);
                                newValue = tval == null ? "0" : tval.toString();
                            }
                            // Set the new value and make it visible
                            setValueAt(StringUtils.isNumber(newValue) ? Integer.parseInt(newValue) : null, r, c);
                            repaint();
                            //                            ( (DefaultTableModel) getModel() ).fireTableDataChanged();
                            break;
                    }
                }
            }
        });
    }

    /**
     * Initializes all GUI components contained in the table
     */
    void initGUI() {
        // Set all columns to TEXT and set a default tooltip
        for (int i = 0; i < getColumnCount(); i++) {
            columnTypes.add(null);
            setColumnType(i, CellType.TEXT);
            tooltips.add("Col " + i);
        }

        // Initialize all settings for the right click popup menu
        final JPopupMenu headerMenu = new JPopupMenu(); // Actual popup menu

        // Add a listener to show the menu on right click
        getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupCol = getTableHeader().columnAtPoint(e.getPoint());
                    headerMenu.show(getTableHeader(), e.getX(), e.getY());
                }
            }
        });

        // Add a listener to display tooltips on mouse movement over the column header
        getTableHeader().addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                int col = getTableHeader().columnAtPoint(e.getPoint());
                getTableHeader().setToolTipText(tooltips.get(col));
            }
        });
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setResizingAllowed(false);
        getTableHeader().setBackground(getBackground());
        getTableHeader().setForeground(getForeground());

        // Initialize everything inside the popup menu
        JMenu jmi_type = new JMenu("Column Type"); // Column type submenu

        // Column data type choices
        JMenuItem jmi_text = new JMenuItem("Text");
        JMenuItem jmi_check = new JMenuItem("Checkbox");
        JMenuItem jmi_combo = new JMenuItem("Combo Box");
        JMenuItem jmi_num = new JMenuItem("Number");

        // Text type
        jmi_text.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setColumnType(popupCol, CellType.TEXT);
                repaint();
            }
        });

        // Checkbox type
        jmi_check.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setColumnType(popupCol, CellType.CHECK);
                repaint();
            }
        });

        // Combo box type
        jmi_combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Ask how many choices should be in the combobox
                JSpinner numEntries = new JSpinner();
                if (JOptionPane.showConfirmDialog(null, new Object[]{"How many entries?", numEntries}, "Combo Box Options", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                    // Generate that many JTextFields and ask for all of the choices
                    int ents = (int) numEntries.getValue();
                    if (ents > 0) {
                        // Array of JTextFields
                        JTextField[] inputs = new JTextField[ents];
                        for (int i = 0; i < ents; i++) {
                            inputs[i] = new JTextField();
                        }
                        // Show prompt
                        if (JOptionPane.showConfirmDialog(null, inputs, "Enter Combo Box Choices", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                            // Collect all the combo box choices
                            String[] entries = new String[ents];
                            for (int i = 0; i < ents; i++) {
                                entries[i] = inputs[i].getText();
                            }
                            // Set the column type
                            setColumnType(popupCol, CellType.COMBO, entries);
                        }
                    }
                }
                repaint();
            }
        });

        // Number type
        jmi_num.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setColumnType(popupCol, CellType.NUMBER);
                repaint();
            }
        });
        jmi_type.add(jmi_text);
        jmi_type.add(jmi_check);
        jmi_type.add(jmi_combo);
        jmi_type.add(jmi_num);

        // Option to rename header
        JMenuItem jmi_rename = new JMenuItem("Rename");
        jmi_rename.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPanel dialog = new JPanel(new GridLayout(2, 2));
                JTextField name = new JTextField(), tooltip = new JTextField();
                dialog.add(new JLabel("Name:"));
                dialog.add(name);
                dialog.add(new JLabel("Tooltip:"));
                dialog.add(tooltip);
                if (JOptionPane.showConfirmDialog(null, dialog, "Column Settings", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                    setColumnHeader(popupCol, name.getText().length() > 0 ? name.getText() : " ");
                    tooltips.set(popupCol, tooltip.getText().length() > 0 ? tooltip.getText() : " ");
                }
                repaint();
            }
        });

        // Option to add column
        JMenuItem jmi_add = new JMenuItem("Add Column");
        final JMenuItem jmi_delete = new JMenuItem("Delete Column");
        jmi_add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPanel dialog = new JPanel(new GridLayout(2, 2));
                JTextField name = new JTextField(), tooltip = new JTextField();
                dialog.add(new JLabel("Name:"));
                dialog.add(name);
                dialog.add(new JLabel("Tooltip:"));
                dialog.add(tooltip);
                if (JOptionPane.showConfirmDialog(null, dialog, "Column Settings", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                    getColumnModel().addColumn(new TableColumn());
                    columnTypes.add(null);
                    setColumnType(getColumnCount() - 1, CellType.TEXT);
                    setColumnHeader(getColumnCount() - 1, name.getText());
                    tooltips.add(tooltip.getText());
                    if (!jmi_delete.isEnabled()) {
                        jmi_delete.setEnabled(true);
                    }
                }
                repaint();
            }
        });

        // Option to delete column
        jmi_delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getColumnModel().removeColumn(getColumnModel().getColumn(popupCol));
                columnTypes.remove(popupCol);
                tooltips.remove(popupCol);
                if (getColumnCount() == 1) {
                    jmi_delete.setEnabled(false);
                }
                repaint();
            }
        });
        headerMenu.add(jmi_type);
        headerMenu.add(jmi_rename);
        headerMenu.add(jmi_add);
        headerMenu.add(jmi_delete);

        // Disallow column reordering
        getTableHeader().setReorderingAllowed(false);
    }

    /**
     * Copies all of the table data into a 2D list
     *
     * @return List of all table data
     */
    public ArrayList<ArrayList<Object>> exportTableData() {
        ArrayList<ArrayList<Object>> copy = new ArrayList<ArrayList<Object>>(getRowCount());
        for (int row = 0; row < getRowCount(); row++) {
            copy.add(new ArrayList<Object>(getColumnCount()));
            for (int col = 0; col < getColumnCount(); col++) {
                copy.get(row).add(getValueAt(row, col));
            }
        }
        return copy;
    }

    /**
     * Sets the name of a column
     *
     * @param column Column index
     * @param header Name of the column
     */
    public void setColumnHeader(int column, String header) {
        getColumnModel().getColumn(column).setHeaderValue(header);
    }

    /**
     * Sets a columns to a specific type
     *
     * @param column       Column index
     * @param type         Column type
     * @param comboEntries OPTIONAL, fill only if the type is COMBO, then this is the entries of the that
     *                     combo box
     */
    public void setColumnType(final int column, CellType type, final String... comboEntries) {
        if (MathUtils.inRange_in_ex(column, 0, getColumnCount())) {
            for (int i = 0; i < getRowCount(); i++) {
                setValueAt(null, i, column);
            }
            columnTypes.set(column, type);
            DefaultCellEditor dummy = new DefaultCellEditor(new JTextField());
            dummy.setClickCountToStart(1000); //Nobody's doing that, let the MouseEvent take over
            switch (type) {
                case COMBO:
                    //Add entries into type
                    comboValues.put(column, comboEntries);

                    //Only set a renderer, editing handled by MouseEvent
                    JComboBoxCellRenderer renderer = new JComboBoxCellRenderer();
                    renderer.setModel(new DefaultComboBoxModel<String>(comboEntries));
                    renderer.setBackground(HMI.getInstance().getBackground());
                    renderer.setForeground(HMI.getInstance().getForeground());
                    renderer.setFont(HMI.getInstance().getFont());
                    getColumnModel().getColumn(column).setCellRenderer(renderer);
                    break;
                case NUMBER:
                case TEXT:
                    //These will all be handled by a MouseEvent
                    getColumnModel().getColumn(column).setCellEditor(dummy);
                    getColumnModel().getColumn(column).setCellRenderer(new DefaultTableCellRenderer());
                    break;
                // CHECK uses a Boolean, which gets represented as a checkbox
                case CHECK:
                    getColumnModel().getColumn(column).setCellEditor(getDefaultEditor(Boolean.class));
                    getColumnModel().getColumn(column).setCellRenderer(getDefaultRenderer(Boolean.class));
                    break;
            }
        }
    }

    /**
     * @param col Column index
     * @return The CellType of a column
     */
    public CellType getColumnType(int col) {
        // Range validation
        if (!MathUtils.inRange_in_ex(col, 0, columnTypes.size())) {
            log.error("Invalid column index: ", col);
            return null;
        }

        return columnTypes.get(col);
    }

    /**
     * Inserts a blank row at the end of the table
     */
    public void insertRow() {
        ((DefaultTableModel) getModel()).addRow(new Vector<Object>());
    }

    /**
     * Deletes all rows from the table
     */
    public void deleteAllRows() {
        ((DefaultTableModel) getModel()).setRowCount(0);
    }

    /**
     * Sets the value of a cell
     *
     * @param val Cell value
     * @param row Row index
     * @param col Column index
     */
    public void setValueAt(Object val, int row, int col) {
        getModel().setValueAt(val, convertRowIndexToModel(row), convertColumnIndexToModel(col));
    }

    /**
     * @param row Row index
     * @param col Column index
     * @return Cell value at the given location
     */
    public Object getValueAt(int row, int col) {
        return getModel().getValueAt(row, col);
    }

    /**
     * Loads the JTB data and formats the table
     *
     * @param path Path to the JTB file
     */
    public void loadTableFromFile(String path) {
        try {
            Scanner scan = new Scanner(FileUtils.getFile(path)); // Open file stream
            String[] data; // Holds temp data
            // Read and set the column headers
            data = nextAvailableLine(scan).split(COMMA);
            ((DefaultTableModel) getModel()).setColumnCount(data.length); // Set the # of columns
            ((DefaultTableModel) getModel()).setColumnIdentifiers(data);

            // Read and set the column tooltips
            data = nextAvailableLine(scan).split(COMMA);
            tooltips = new ArrayList<String>(Arrays.asList(data));

            // Read and set all the column types
            columnTypes.clear();
            for (int i = 0; i < getColumnCount(); i++) {
                columnTypes.add(null); // Add type placeholder
                // Read the next line
                data = nextAvailableLine(scan).split(COMMA);

                // Set column type based on what was read
                String type = data[0];
                if (type.equals(CellType.TEXT.getTypeName())) {
                    setColumnType(i, CellType.TEXT);
                } else if (type.equals(CellType.CHECK.getTypeName())) {
                    setColumnType(i, CellType.CHECK);
                } else if (type.equals(CellType.COMBO.getTypeName())) {
                    // The line for COMBO contains all the entries, so these are set as well
                    setColumnType(i, CellType.COMBO, Arrays.copyOfRange(data, 1, data.length));
                } else if (type.equals(CellType.NUMBER.getTypeName())) {
                    setColumnType(i, CellType.NUMBER);
                }
            }
            log.info("JTB file \"{}\" successfully loaded", path);
        } catch (FileNotFoundException e) {
            log.info("JTB file \"{}\" not found", path);
            e.printStackTrace();
        }
    }

    /**
     * Reads the file until a line that is not a comment and not blank is found
     *
     * @param scan File Scanner
     * @return The next line that has any content
     */
    private String nextAvailableLine(Scanner scan) {
        String line;
        // Keep reading until a line is found
        while ((line = scan.nextLine()).startsWith(COMMENT) || line.length() == 0) ;
        return line;
    }

    /**
     * Saves the JTB table and creates a blank PRG file
     *
     * @param path Path to save the JTB
     */
    public void saveTableToPath(String path) {
        // Extension fix
        if (!path.endsWith(EXTENSION)) {
            path += EXTENSION;
        }

        try {
            // Save table settings
            PrintWriter pw = new PrintWriter(new FileOutputStream(FileUtils.getFile(path)));
            pw.println(COMMENT + "Interactive JTable Save Data");
            pw.println("\n" + COMMENT + "Column Headers and Tooltips, the number of headers sets the number of columns:");

            // Print out all the column headers and tooltips
            for (int i = 0; i < getColumnCount(); i++) {
                pw.print(getColumnModel().getColumn(i).getHeaderValue() + (i == getColumnCount() - 1 ? "\n" : COMMA));
            }
            for (int i = 0; i < getColumnCount(); i++) {
                pw.print(tooltips.get(i) + (i == getColumnCount() - 1 ? "\n" : COMMA));
            }

            pw.println("\n" + COMMENT + "The following lines are all the data types of the columns");
            pw.println(COMMENT + "There are 4 types: Text, Checkbox, Combo Box, and Number. Their syntax is as follows:");
            pw.printf("%s\"%s\"\n", COMMENT, CellType.TEXT.getTypeName());
            pw.printf("%s\"%s\"\n", COMMENT, CellType.CHECK.getTypeName());
            pw.printf("%s\"%s,choice,choice,choice,...\"\n", COMMENT, CellType.COMBO.getTypeName());
            pw.printf("%s\"%s\"\n", COMMENT, CellType.NUMBER.getTypeName());
            pw.println(COMMENT + "The number of lines MUST equal the number of columns");

            // Print out all of the column types
            for (int i = 0; i < getColumnCount(); i++) {
                switch (columnTypes.get(i)) {
                    case TEXT:
                        pw.println("text");
                        break;
                    case CHECK:
                        pw.println("check");
                        break;
                    case COMBO:
                        pw.print("combo,");
                        // Print all of the combo box entries on the same line
                        JComboBox<String> combo = (JComboBox<String>) getColumnModel().getColumn(i).getCellEditor().getTableCellEditorComponent(null, null, false, -1, i);
                        for (int j = 0; j < combo.getItemCount(); j++) {
                            pw.print(combo.getItemAt(j) + (j == combo.getItemCount() - 1 ? "\n" : COMMA));
                        }
                        break;
                    case NUMBER:
                        pw.println(CellType.NUMBER.getTypeName());
                        break;
                }
            }
            pw.flush();
            pw.close();
            log.info("JTB file \"{}\" successfully saved", path);

            // Create a blank PRG file if it doesn't exist
            int index = PROGRAM_DEFAULT;
            if (index > 0) {
                path = path.substring(0, path.lastIndexOf(EXTENSION)) + "-" + index + PROGRAM_EXTENSION;
                if (!(FileUtils.exists(path))) {
                    pw = new PrintWriter(new FileOutputStream(FileUtils.getFile(path)));
                    pw.close();
                    log.info("Blank PRG file successfully saved to \"{}\"", path);
                }
            }
        } catch (FileNotFoundException e) {
            log.error("Error creating JTB file");
            e.printStackTrace();
        }
    }
}
