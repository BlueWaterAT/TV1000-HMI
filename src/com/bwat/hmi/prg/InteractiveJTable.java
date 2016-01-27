package com.bwat.hmi.prg;

import com.bwat.hmi.Constants;
import com.bwat.hmi.HMI;
import com.bwat.hmi.file.FileListenerAdapter;
import com.bwat.hmi.file.FileWatcher;
import com.bwat.hmi.ui.KeypadDialog;
import com.bwat.hmi.ui.ListDialog;
import com.bwat.hmi.util.FileUtils;
import com.bwat.hmi.util.JSONUtils;
import com.bwat.hmi.util.StringUtils;
import org.json.JSONObject;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

public class InteractiveJTable extends JPanel {
    private Vector<CellType> columnTypes;
    private final JTable table = new JTable(2, 14);
    private JSpinner indexSelector = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
    private JButton insert = new JButton("Insert Row");
    private JButton delete = new JButton("Delete Row");
    private final static String EXTENSION = ".jtb";
    private final static String PROGRAM_EXTENSION = ".prg";
    private final static String COMMA = ",";
    private final static String COMMENT = ";";
    private String openFilePath = null;
    private Vector<String> tooltips = new Vector<String>();
    private Vector<Vector<Object>> uneditedData;
    private JSONObject settings;
    public boolean loadingTable = false;
    // boolean savingTable = false;

    long lastSaveTime = 0;
    final long SAVE_RELOAD_TIME = 3000;

    public InteractiveJTable() {
        // Load settings
        settings = JSONUtils.loadObjectFromFile(Constants.PROGRAM.PATH);
        setFont(HMI.getInstance().getFont());
        setBackground(HMI.getInstance().getBackground());
        setForeground(HMI.getInstance().getForeground());

        table.setModel(new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (!loadingTable) {
                    CellType type = columnTypes.get(columnIndex);
                    if (type == CellType.COMBO) {
                        // JComboBox<String> combo = (JComboBox<String>) table.getCellRenderer( 0, columnIndex
                        // );
                        JComboBox<String> combo = ((JComboBoxCellEditor) table.getCellEditor(0, columnIndex)).jcb;
                        // Convert combo entries to an array because this method doesn't exist for some god
                        // forsaken reason
                        String[] entries = new String[combo.getItemCount()];
                        for (int i = 0; i < combo.getItemCount(); i++) {
                            entries[i] = combo.getItemAt(i);
                        }
                        return columnTypes.get(columnIndex).getCellClass(entries);
                    }
                    return columnTypes.get(columnIndex).getCellClass();
                }
                return super.getColumnClass(columnIndex);
            }
        });

        // Intial setup
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // table.setRowSelectionAllowed( false );
        int rowHeight = settings.has(Constants.PROGRAM.KEY_ROW_H) ? settings.getInt(Constants.PROGRAM.KEY_ROW_H) : 50;
        table.setRowHeight(rowHeight);

        // Initialize column data types
        columnTypes = new Vector<CellType>(table.getColumnCount());
        for (int i = 0; i < columnTypes.capacity(); i++) {
            columnTypes.add(null);
            setColumnType(i, CellType.TEXT);
            tooltips.add("Col " + i);
        }

        // CONTROLS
        // INDEX SELECTOR
        uneditedData = exportTableData();
        indexSelector.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                loadProgram((int) indexSelector.getValue());
            }
        });
        // INSERT
        insert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                insertRow();
            }
        });
        // DELETE
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteRow(table.getSelectedRow());
            }
        });

        // JPanel controls = new JPanel( new GridLayout( 1, 5 ) );
        JPanel controls = new JPanel(new GridLayout(1, 3));
        controls.setPreferredSize(new Dimension(getWidth(), 75));
        // controls.add( indexSelector );
        // controls.add( save );
        controls.add(insert);
        controls.add(delete);
        controls.add(HMI.getInstance().generateReturnButton());

        // Tooltip display
        table.getTableHeader().addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                int col = table.getTableHeader().columnAtPoint(e.getPoint());
                table.getTableHeader().setToolTipText(tooltips.get(col));
            }
        });
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setBackground(getBackground());
        table.getTableHeader().setForeground(getForeground());

        setLayout(new BorderLayout());
        JScrollPane scroll = new JScrollPane(table);
        scroll.setRowHeaderView(new RowNumberHeader(table, getBackground(), getForeground()));
        // scroll.getRowHeader().setBackground( getBackground() );
        // scroll.getRowHeader().setForeground( getForeground() );
        add(scroll);
        add(controls, BorderLayout.SOUTH);

        loadTable();

        table.setAutoCreateRowSorter(true);
        DefaultRowSorter sorter = ((DefaultRowSorter) table.getRowSorter());
        table.setRowSorter(sorter);
        ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(settings.has(Constants.PROGRAM.KEY_PRIMARY_SORT) ? settings.getInt(Constants.PROGRAM.KEY_PRIMARY_SORT) : 0, SortOrder.ASCENDING));

        sorter.setSortKeys(sortKeys);
        sorter.setSortsOnUpdates(true);

        table.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                // savingTable = true;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (uneditedData != null && !uneditedData.equals(exportTableData())) {
                            if (openFilePath != null) {
                                lastSaveTime = System.currentTimeMillis();
                                saveProgram();
                            }
                        }
                    }
                });
            }
        });

        FileWatcher.watchFile("prg", new FileListenerAdapter() {
            public void fileCreated(File f) {
                fileChanged(f);
            }

            ;

            public void fileChanged(final File f) {
                // Whenever the table is saved, it will trigger the file watcher
                // This is here to prevent the table from reloading every time a change is made
                if (System.currentTimeMillis() - lastSaveTime > SAVE_RELOAD_TIME) {
                    // savingTable = false;
                    // } else {
                    System.out.println("LOADING ON CHANGE");
                    // Logger.logLine( "LOADING ON CHANGE" );
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            String name = f.getName();
                            if (name.endsWith(PROGRAM_EXTENSION)) {
                                int prg = Integer.parseInt(name.substring(name.lastIndexOf("-") + 1, name.lastIndexOf(PROGRAM_EXTENSION)));
                                if (!indexSelector.getValue().equals(prg)) {
                                    indexSelector.setValue(prg);
                                } else {
                                    loadProgram(prg);
                                }
                            }
                        }
                    });
                }
            }

            ;
        });
    }

    // timing crap
    long start = 0;

    //
    // void tstart() {
    // start = System.currentTimeMillis();
    // }
    //
    // void tend() {
    // long time = System.currentTimeMillis() - start;
    // System.out.println( "Time: " + time + " ms" );
    // // Logger.logLine( "Time: " + time + " ms" );
    // }

    public void loadProgram(int prog) {
        if (prog > 0) {
            uneditedData = null;
            ((DefaultTableModel) table.getModel()).setRowCount(2);
            clearTable();
            if (openFilePath != null) {
                String progPath = openFilePath.substring(0, openFilePath.endsWith(EXTENSION) ? openFilePath.lastIndexOf(EXTENSION) : openFilePath.length()) + "-" + prog + PROGRAM_EXTENSION;
                if (FileUtils.exists(progPath)) {
                    loadTableData(progPath);
                }
            }
            uneditedData = exportTableData();
        }
    }

    public void clearTable() {
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int col = 0; col < table.getColumnCount(); col++) {
                table.setValueAt(null, row, col);
            }
        }
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
        if (column >= 0 && column < table.getColumnCount()) {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.setValueAt(null, i, column);
            }
            columnTypes.set(column, type);
            switch (type) {
                case TEXT:
                    table.getColumnModel().getColumn(column).setCellEditor(new DefaultCellEditor(new JTextField()));
                    table.getColumnModel().getColumn(column).setCellRenderer(new DefaultTableCellRenderer());
                    break;
                case COMBO:
                    JComboBox<String> combo = new JComboBox<String>(comboEntries);
                    combo.addPopupMenuListener(new PopupMenuAdapter() {
                        @Override
                        public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    JComboBox<String> c = (JComboBox<String>) e.getSource();
                                    c.hidePopup();

                                    // If this combobox contains only numeric entries, we sould open up a
                                    // keypad instead of a selection screen
                                    boolean numeric = true;
                                    for (int i = 0; i < c.getItemCount(); i++) {
                                        if (!StringUtils.isNumber(c.getItemAt(i))) {
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
                                        if (!StringUtils.isNumber(newValue) || ((DefaultComboBoxModel) c.getModel()).getIndexOf(newValue) == -1) {
                                            newValue = (String) table.getValueAt(table.rowAtPoint(c.getLocation()), column);
                                        }
                                    } else {
                                        // Get a new value with an enlarged combo box selection screen
                                        newValue = ListDialog.showDialog("Select a Value", comboEntries, (String) c.getSelectedItem(), getFont(), 24f);
                                    }

                                    // Set the new value and make it visible
                                    table.setValueAt(newValue, table.rowAtPoint(c.getLocation()), column);
                                    ((DefaultTableModel) table.getModel()).fireTableDataChanged();
                                }
                            });
                        }
                    });
                    combo.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            ((JComboBox<String>) e.getSource()).hidePopup();
                        }
                    });
                    table.getColumnModel().getColumn(column).setCellEditor(new JComboBoxCellEditor(combo, getFont()));
                    // JComboBoxCellRenderer renderer = new JComboBoxCellRenderer( getFont(), getBackground(),
                    // getForeground() );
                    // renderer.setModel( new DefaultComboBoxModel<String>( comboEntries ) );
                    // table.getColumnModel().getColumn( column ).setCellRenderer( renderer );
                    table.getColumnModel().getColumn(column).setCellRenderer(new DefaultTableCellRenderer());
                    break;
                case CHECK:
                    table.getColumnModel().getColumn(column).setCellEditor(table.getDefaultEditor(Boolean.class));
                    table.getColumnModel().getColumn(column).setCellRenderer(table.getDefaultRenderer(Boolean.class));
                    break;
                case NUMBER:
                    // Uses a dummy combo box only as an editor to allow for easy popup control
                    JComboBox<Integer> dummyCombo = new JComboBox<Integer>();
                    dummyCombo.addPopupMenuListener(new PopupMenuAdapter() {
                        @Override
                        public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    JComboBox<Integer> c = (JComboBox<Integer>) e.getSource();
                                    c.hidePopup();
                                    String newValue;
                                    // Get a new value using the keypad interface
                                    newValue = KeypadDialog.showDialog("Enter Value", "");
                                    if (!StringUtils.isNumber(newValue)) {
                                        Object tval = table.getValueAt(table.rowAtPoint(c.getLocation()), column);
                                        newValue = tval == null ? "0" : tval.toString();
                                    }
                                    // Set the new value and make it visible
                                    table.setValueAt(StringUtils.isNumber(newValue) ? Integer.parseInt(newValue) : null, table.rowAtPoint(c.getLocation()), column);
                                    ((DefaultTableModel) table.getModel()).fireTableDataChanged();
                                }
                            });
                        }
                    });
                    dummyCombo.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            ((JComboBox<String>) e.getSource()).hidePopup();
                        }
                    });
                    table.getColumnModel().getColumn(column).setCellEditor(new JComboBoxCellEditor(dummyCombo, getFont()));
                    table.getColumnModel().getColumn(column).setCellRenderer(new DefaultTableCellRenderer());
            }
        }
    }

    public void insertRow( /* boolean force */) {
        // if ( force ) {
        ((DefaultTableModel) table.getModel()).addRow(new Vector<Object>());
        // } else {
        // SwingUtilities.invokeLater( new Runnable() {
        // @Override
        // public void run() {
        // ( (DefaultTableModel) table.getModel() ).addRow( new Vector<Object>() );
        // }
        // } );
        // }
    }

    public void deleteRow(int row) {
        if (row >= 0 && row < table.getRowCount()) {
            row = table.convertRowIndexToModel(row);
            ((DefaultTableModel) table.getModel()).removeRow(row);
        }
    }

    public Vector<Vector<Object>> exportTableData() {
        Vector<Vector<Object>> target = new Vector<Vector<Object>>(table.getRowCount());
        for (int row = 0; row < table.getRowCount(); row++) {
            target.add(new Vector<Object>(table.getColumnCount()));
            for (int col = 0; col < table.getColumnCount(); col++) {
                target.get(row).add(table.getValueAt(row, col));
            }
        }
        return target;
    }

    public void saveProgram() {
        try {
            // Save current program
            int index = (int) indexSelector.getValue();
            if (index > 0) {
                String path = openFilePath.substring(0, openFilePath.lastIndexOf(EXTENSION)) + "-" + index + PROGRAM_EXTENSION;
                PrintWriter pw = new PrintWriter(new FileOutputStream(FileUtils.getFile(path)));
                for (int row = 0; row < table.getRowCount(); row++) {
                    for (int col = 0; col < table.getColumnCount(); col++) {
                        pw.print(table.getValueAt(row, col) + (col == table.getColumnCount() - 1 ? "\n" : COMMA));
                    }
                }
                pw.flush();
                pw.close();
            }
            uneditedData = exportTableData();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadTable() {
        System.out.println("LOAD TABLE");
        loadingTable = true;
        // Some initial setup
        // save.setEnabled( true );
        indexSelector.setValue(((SpinnerNumberModel) indexSelector.getModel()).getMinimum());
        try {
            openFilePath = settings.getString(Constants.PROGRAM.KEY_JTB_PATH);
            if (!FileUtils.exists(openFilePath) || !FileUtils.isFile(openFilePath)) {
                return;
            }
            uneditedData = null;
            Scanner scan = new Scanner(FileUtils.getFile(openFilePath));
            String[] data;
            // Get Column Headers
            data = nextAvailableLine(scan).split(COMMA);
            final int cols = data.length;
            ((DefaultTableModel) table.getModel()).setColumnCount(cols);
            ((DefaultTableModel) table.getModel()).setColumnIdentifiers(data);
            data = nextAvailableLine(scan).split(COMMA);
            tooltips = new Vector<String>(Arrays.asList(data));
            // Get Column Editor Types
            columnTypes = new Vector<CellType>(cols);
            for (int i = 0; i < table.getColumnCount(); i++) {
                columnTypes.add(null);
                data = nextAvailableLine(scan).split(COMMA);
                String type = data[0];
                if (type.equals(CellType.TEXT.getTypeName())) {
                    setColumnType(i, CellType.TEXT);
                } else if (type.equals(CellType.CHECK.getTypeName())) {
                    setColumnType(i, CellType.CHECK);
                } else if (type.equals(CellType.COMBO.getTypeName())) {
                    setColumnType(i, CellType.COMBO, Arrays.copyOfRange(data, 1, data.length));
                } else if (type.equals(CellType.NUMBER.getTypeName())) {
                    setColumnType(i, CellType.NUMBER);
                }
            }
            indexSelector.setValue(1);
            loadProgram(1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        loadingTable = false;
    }

    public void loadTableData(final String path) {
        try {
            System.out.println("LOAD");
            // Logger.logLine( "LOAD" );
            Scanner scan = new Scanner(FileUtils.getFile(path));
            String[] data;
            String line;
            ((DefaultTableModel) table.getModel()).setRowCount(0);
            for (int row = 0; scan.hasNext(); row++) {
                line = scan.nextLine();
                insertRow();
                data = line.split(COMMA);
                for (int col = 0; col < table.getColumnCount(); col++) {
                    switch (columnTypes.get(col)) {
                        case TEXT:
                        case COMBO:
                            table.getModel().setValueAt(data[col].equals("null") ? null : data[col], row, col);
                            break;
                        case CHECK:
                            String d = data[col].toLowerCase();
                            table.getModel().setValueAt(!(d.equals("null") || d.equals("false")), row, col);
                            break;
                        case NUMBER:
                            table.getModel().setValueAt(data[col].equals("null") ? null : Integer.parseInt(data[col]), row, col);
                            break;
                    }
                }
            }
            scan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String nextAvailableLine(Scanner scan) {
        String line;
        while ((line = scan.nextLine()).startsWith(COMMENT) || line.length() == 0) {
        }
        return line;
    }
}
