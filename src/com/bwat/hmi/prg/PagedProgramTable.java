package com.bwat.hmi.prg;

import com.bwat.hmi.Constants;
import com.bwat.hmi.HMI;
import com.bwat.hmi.ui.KeypadDialog;
import com.bwat.hmi.util.FileUtils;
import com.bwat.hmi.util.JSONUtils;
import com.bwat.hmi.util.MathUtils;
import com.bwat.hmi.util.StringUtils;
import com.bwat.hmi.util.SwingUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import static com.bwat.hmi.Constants.PROGRAM.*;

/**
 * A JTable interface that dynamically loads and saves data at certain sections of a PRG file at a time
 * NOTE: If the PRG file is ever changed the file MUST be reindexed
 *
 * @author Kareem El-Faramawi
 */
public class PagedProgramTable extends JPanel {
    Logger log = LoggerFactory.getLogger(getClass());

    // GUI
    final ProgramTable table = new ProgramTable(2, 14);

    // Path to the PRG file
    String programPath;

    // Number of elements to display on a page
    int pageSize = 10;

    // Currently displayed page number
    int currentPage;

    // Maps row numbers to byte indices in the PRG file
    ArrayList<Long> idxs = new ArrayList<Long>();

    // Cache of the data for the currently displayed rows
    ArrayList<ArrayList<Object>> displayedRows = new ArrayList<ArrayList<Object>>();

    // Indicates if this is currently making changes to the ProgramTable
    boolean selfChanging = false;

    //Program table settings
    private JSONObject settings;

    // Page controls
    JPanel ctrlPanel = new JPanel(new BorderLayout());
    JButton jumpPrev = new JButton("\u2190");
    JButton jumpPage = new JButton("");
    JButton jumpNext = new JButton("\u2192");
    JButton jumpRow = new JButton("Jump to Row");
    JButton updatePageSize = new JButton("Set Page Size");
    RowNumberHeader rowHeader;

    public PagedProgramTable() {
        initGUI();
        jumpToPage(1);
    }


    /**
     * Initializes the GUI components for this PagedProgramTable
     */
    private void initGUI() {
        // Load settings
        settings = JSONUtils.loadObjectFromFile(PATH);
        setFont(HMI.getInstance().getFont());
        setBackground(HMI.getInstance().getBackground());
        setForeground(HMI.getInstance().getForeground());
        pageSize = settings.has(KEY_PAGE_SIZE) ? settings.getInt(KEY_PAGE_SIZE) : pageSize;

        // JTable settings
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(settings.has(Constants.PROGRAM.KEY_ROW_H) ? settings.getInt(Constants.PROGRAM.KEY_ROW_H) : 50);


        // Action listeners for all buttons

        // Previous page
        jumpPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prevPage();
            }
        });

        // Next page
        jumpNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextPage();
            }
        });

        // Jump to a page
        jumpPage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Prompt the user for the page # they want to jump to
                String num = KeypadDialog.showDialog("Enter page number:", "");
                if (num.length() > 0 && StringUtils.isNumber(num)) {
                    jumpToPage(Integer.parseInt(num)); // Jump to the selected page
                }
            }
        });

        // Jump to a page containing a certain row
        jumpRow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getNumRows() > 0) { // Nothing to jump to
                    // Prompt the user for the row # they want to jump to
                    String num = KeypadDialog.showDialog("Enter row number:", "");
                    if (num.length() > 0 && StringUtils.isNumber(num)) {
                        jumpToRow(Integer.parseInt(num)); // Jump to the selected page
                    }
                }
            }
        });

        //Set the number of rows displayed on a page
        updatePageSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Prompt the user for the new pageSize
                String num = KeypadDialog.showDialog("Enter new page size:", "");
                if (num.length() > 0 && StringUtils.isNumber(num)) {
                    setPageSize(MathUtils.clamp_i(Integer.parseInt(num), 1, 100)); // Jump to the selected page
                }
            }
        });

        // Assemble control panel
        ctrlPanel.add(SwingUtils.createGridJPanel(1, 3, jumpPrev, jumpPage, jumpNext), BorderLayout.WEST);
        ctrlPanel.add(SwingUtils.createGridJPanel(2, 1, updatePageSize, jumpRow), BorderLayout.EAST);

        // Scroll panel for the table with headers
        JScrollPane scroll = new JScrollPane(table);
        rowHeader = new RowNumberHeader(table, getBackground(), getForeground());
        scroll.setRowHeaderView(rowHeader);

        // Add everything
        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
        add(ctrlPanel, BorderLayout.SOUTH);
    }

    /**
     * @return If this is making changes to the internal ProgramTable
     */
    public boolean isSelfChanging() {
        return selfChanging;
    }

    /**
     * @return The number of pages that the program has been divided into
     */
    public int getNumPages() {
        return (int) Math.max(1, Math.ceil((double) getNumRows() / pageSize));
    }

    /**
     * @return The total number of rows in the program
     */
    public int getNumRows() {
        return idxs.size() - 1; //-1 because the end index is included in idxs, which doesn't count as a row
    }

    /**
     * @param page Page number
     * @return The row number of the first row on a page
     */
    public int getFirstRowOnPage(int page) {
        return pageSize * (page - 1);
    }

    /**
     * Completely reloads and displays the PRG file from the start
     */
    public void fullReload() {
        selfChanging = true;
        reloadProgram();
        jumpToPage(1);
    }

    /**
     * Repopulates the table with the data from the cache, and updates displayed information.
     * Assumes that currentPage and displayedRows have been updated
     */
    public void reloadDisplay() {
        selfChanging = true;
        // Update the JTable data
        table.deleteAllRows(); // Delete all displayed rows
        // Iterate through the row cache
        for (int row = 0; row < displayedRows.size(); row++) {
            table.insertRow(); // Add the new row
            // Fill the table data with the cached data
            ArrayList<Object> rowData = displayedRows.get(row);
            for (int col = 0; col < rowData.size(); col++) {
                table.setValueAt(rowData.get(col), row, col);
            }
        }

        // Update page size
        jumpPage.setText(String.format("Page %d / %d", currentPage, getNumPages()));

        // Update row header numbers
        rowHeader.setOffset(getFirstRowOnPage(currentPage));

        // Disable the selfChanging flag in the Swing handler
        // This will run after all of the table changes have been made
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                selfChanging = false;
            }
        });
    }

    /**
     * Reindexes the program file
     */
    public void reloadProgram() {
        // Clear old indices
        idxs.clear();
        try {
            // Load file
            RandomAccessFile f = getFile(false);
            if (f != null) {
                // Read line by line, saving every stream position
                long len = f.length();
                while (f.getFilePointer() != len) {
                    idxs.add(f.getFilePointer());
                    f.readLine();
                }
                // Save the index for the end of the file
                idxs.add(f.getFilePointer());
                f.close();
            }
        } catch (IOException e) {
            log.error("Error while reading PRG file \"{}\"", programPath);
            e.printStackTrace();
        }
        log.info("PRG file \"{}\" successfully indexed", programPath);
    }

    /**
     * Loads, indexes, and displays a PRG file
     *
     * @param path Path to the PRG file
     */
    public void loadProgram(String path) {
        if (FileUtils.exists(path)) {
            programPath = path;
            reloadProgram();
            jumpToPage(1);
        } else {
            log.error("PRG file \"{}\" not found", path);
        }
    }

    /**
     * Pulls the newest data from the currently displayed page and saves it to the PRG file
     *
     * This is done by:
     * 1. Reading the PRG file into a buffer up to the beginning of the first line on the page being updated
     * 2. Skipping all of the lines on the page
     * 3. Appending the updated data to the buffer, filling in the skipped lines
     * 4. Reading the rest of the file into the buffer
     * 5. Saving this buffer as the new PRG file
     */
    public void savePage() {
        // Grab the page data from the table
        ArrayList<ArrayList<Object>> rowData = table.exportTableData();
        if (!rowData.equals(displayedRows)) {
            // Build a string of the page data to put in the PRG file
            String rowString = "";
            for (int row = 0; row < rowData.size(); row++) {
                for (int col = 0, len = rowData.get(row).size(); col < len; col++) {
                    rowString += String.valueOf(rowData.get(row).get(col)) + (col == len - 1 ? "\n" : COMMA);
                }
            }

            // Load the file
            RandomAccessFile f = getFile(true);
            try {
                if (f != null && f.length() > 0) {
                    // Get the starting index of the first row of the page
                    long endIdx = getRowByteIdx(getFirstRowOnPage(currentPage));

                    // Read everything before the row into a buffer
                    String buf = "";
                    while (f.getFilePointer() != endIdx) {
                        buf += f.readLine() + "\n";
                    }

                    // Skip all of the lines on the page
                    for (int i = getFirstRowOnPage(currentPage), stop = Math.min(getNumRows(), i + pageSize); i < stop; i++) {
                        f.readLine();
                    }

                    // Add the new page data to the buffer
                    buf += rowString;

                    // Read the rest of the file into the buffer
                    while (f.getFilePointer() != f.length()) {
                        buf += f.readLine() + "\n";
                    }

                    // Seek back to the beginning and write out the buffer
                    f.seek(0);
                    f.setLength(buf.length());
                    f.write(buf.getBytes());
                    f.close();
                    log.info("PRG file \"{}\" successfully saved", programPath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Reload the program
            reloadProgram();
            jumpToPage(Math.min(currentPage, getNumPages()));
        }
    }

    /**
     * Deletes the visually selected row index from the PRG file
     *
     * This is done by:
     * 1. Reading the PRG file into a buffer up to the beginning of the deleted line
     * 2. Skipping the deleted line
     * 3. Reading the rest of the PRG into the buffer
     * 4. Saving this buffer as the new PRG file
     *
     * @param tableIndex JTable row index
     */
    public void deleteRow(int tableIndex) {
        // Load the file
        RandomAccessFile f = getFile(true);
        try {
            if (f != null && f.length() > 0) {
                // Get the  starting index of the row to be deleted
                long endIdx = getRowByteIdx(getRowNumber(tableIndex));

                // Read everything before the row into a buffer
                String buf = "";
                while (f.getFilePointer() != endIdx) {
                    buf += f.readLine() + "\n";
                }

                // Skip the row to be deleted
                f.readLine();

                // Read the rest of the file into the buffer
                while (f.getFilePointer() != f.length()) {
                    buf += f.readLine() + "\n";
                }

                // Seek back to the beginning and write out the buffer
                f.seek(0);
                f.setLength(buf.length());
                f.write(buf.getBytes());
                f.close();
                log.info("Row deleted from \"{}\"", programPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Reload the program
        reloadProgram();
        jumpToPage(Math.min(currentPage, getNumPages()));
    }

    /**
     * Inserts a blank new row at the very end of the program and saves the PRG file
     */
    public void insertRow() {
        // Load the file
        RandomAccessFile f = getFile(true);
        if (f != null) {
            try {
                // Create a blank row string ("null,null,null,null,....")
                String newRow = new String(new char[table.getColumnCount() - 1]).replace("\0", "null,") + "null\n";

                // Check to make sure the file ending is correct
                if (f.length() > 0) {
                    // Seek to the last character in the file
                    f.seek(idxs.get(idxs.size() - 1) - 1);

                    // Make sure there's a newline before the new row
                    if (f.read() != (byte) '\n') {
                        newRow = "\n" + newRow;
                    }
                }

                // Write the new row to the end of the file
                f.setLength(f.length() + newRow.length());
                f.write(newRow.getBytes());
                f.close();

                log.info("New row inserted at the end of \"{}\"", programPath);
            } catch (IOException e) {
                log.info("Error inserting new row in \"{}\"", programPath);
                e.printStackTrace();
            }

            // Reload the program
            reloadProgram();
            jumpToPage(currentPage);
        }
    }

    /**
     * Sets the number of elements to display on every page and updates the display
     *
     * @param size Number of elements per page
     */
    public void setPageSize(int size) {
        // Size validation
        if (size < 1) {
            throw new IllegalArgumentException("Page size cannot be < 1");
        }

        // Reload display at the first page
        pageSize = size;
        jumpToPage(1);
    }

    /**
     * Loads the row information from the requested page and displays it
     * NOTE: This will force the requested page to be in the range [1, getNumPages()]
     *
     * @param p Page number to display
     */
    public void jumpToPage(int p) {
        // Auto clamp the given page number into an allowed range
        if (!MathUtils.inRange_in(p, 1, getNumPages())) {
            currentPage = MathUtils.clamp_i(p, 1, getNumPages());
            log.info("Page number was auto-clamped from {} to {}", p, currentPage);
        } else {
            currentPage = p;
        }

        // Load the page data
        loadPage(currentPage);

        // Show the page data and update display
        reloadDisplay();
    }

    /**
     * Jumps to the page containing a certain row number
     *
     * @param r Requested row number
     */
    public void jumpToRow(int r) {
        jumpToPage((int) Math.ceil((double) r / pageSize));
    }

    /**
     * Loads the next page
     */
    public void nextPage() {
        jumpToPage(currentPage + 1);
    }

    /**
     * Loads the previous page
     */
    public void prevPage() {
        jumpToPage(currentPage - 1);
    }

    /**
     * @return The internally used ProgramTable
     */
    public ProgramTable getTable() {
        return table;
    }

    /**
     * Loads a JTB file to format the JTable
     *
     * @param path Path to the JTB file
     */
    public void loadTableFromFile(String path) {
        selfChanging = true;
        table.loadTableFromFile(path);
        selfChanging = false;
    }

    /**
     * Gets the true row number for the visually selected row index
     *
     * @param displayed JTable index
     * @return Real row number in the whole program
     */
    public int getRowNumber(int displayed) {
        return displayed + pageSize * (currentPage - 1);
    }

    /**
     * Gets the byte index in the program file that a row starts at
     *
     * @param r Requested row number
     * @return Starting index of the row data
     */
    private Long getRowByteIdx(int r) {
        // Row index validation
        if (!MathUtils.inRange_in_ex(r, 0, getNumRows())) {
            throw new IllegalArgumentException("The requested row (" + r + ") does not exist in the program");
        }

        // Get row's position index in the file
        return idxs.get(r);
    }

    /**
     * Loads the data of all the rows on a page into memory
     *
     * @param page Page number
     */
    private void loadPage(int page) {
        // Clear the old cache
        displayedRows.clear();

        // Load the file
        RandomAccessFile f = getFile(false);
        if (f != null && getNumRows() > 0) { // Don't attempt to load if nothing is indexed
            try {
                long len = f.length(); // Cache the file length
                f.seek(getRowByteIdx(getFirstRowOnPage(page))); // Jump to the starting row

                // These hold data while parsing lines
                String line;
                String[] data;
                for (int i = 0; i < pageSize && f.getFilePointer() != len; i++) {
                    line = f.readLine(); // Read a row
                    data = line.split(COMMA); // Split it
                    ArrayList<Object> rowData = new ArrayList<Object>(); // Temp array
                    // Parse the data depending on the type of the column it belongs in
                    for (int col = 0; col < table.getColumnCount(); col++) {
                        switch (table.getColumnType(col)) {
                            // Simple text
                            case TEXT:
                            case COMBO:
                                rowData.add(data[col].equals("null") ? null : data[col]);
                                break;
                            // Boolean
                            case CHECK:
                                String d = data[col].toLowerCase();
                                rowData.add(!(d.equals("null") || d.equals("false")));
                                break;
                            // Integer
                            case NUMBER:
                                rowData.add(data[col].equals("null") ? null : Integer.parseInt(data[col]));
                                break;
                        }
                    }
                    displayedRows.add(rowData); // Add the row to the cache
                }
                f.close();
            } catch (IOException e) {
                log.error("Error while reading PRG file \"{}\"", programPath);
                e.printStackTrace();
            }
        }
    }

    /**
     * Attempts to load the file located at programPath
     *
     * @return The file handle if it was loaded, null otherwise
     */
    private RandomAccessFile getFile(boolean write) {
        // Verify a file path has been set
        if (programPath == null || programPath.equals("")) {
            log.error("ERROR: No program loaded!");
            return null;
        }

        // Attempt to load the file
        try {
            return new RandomAccessFile(FileUtils.HOME + programPath, write ? "rw" : "r");
        } catch (FileNotFoundException e) {
            log.error("PRG file \"{}\" not found", programPath);
            e.printStackTrace();
        }

        // File was not found
        return null;
    }
}
