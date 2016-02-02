package com.bwat.hmi.prg;

import com.bwat.hmi.HMI;
import com.bwat.hmi.file.FileListenerAdapter;
import com.bwat.hmi.file.FileWatcher;
import com.bwat.hmi.util.FileUtils;
import com.bwat.hmi.util.JSONUtils;
import com.bwat.hmi.util.SwingUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static com.bwat.hmi.Constants.PROGRAM.*;

/**
 * Main interface for the TV1000 Programmer.
 * Includes all functionality for manipulating row data,
 * and sending/downloading data over SFTP
 *
 * @author Kareem ElFaramawi
 */
public class Programmer extends JPanel {
    Logger log = LoggerFactory.getLogger(getClass());

    // The paged table interface
    PagedProgramTable paged;

    // The table contained in PagedProgramTable
    ProgramTable table;

    // Holds a copy of a row's data
    private ArrayList<Object> rowCopy = new ArrayList<Object>();

    // File IO
    private String openFilePath = null;

    // GUI
    private JButton insert = new JButton("Insert Row");
    private JButton delete = new JButton("Delete Row");

    long lastSaveTime = 0;
    final long SAVE_RELOAD_TIME = 3000;
    private JSONObject settings;

    public Programmer() {
        // Load settings
        settings = JSONUtils.loadObjectFromFile(PATH);
        setFont(HMI.getInstance().getFont());
        setBackground(HMI.getInstance().getBackground());
        setForeground(HMI.getInstance().getForeground());

        paged = new PagedProgramTable();
        table = paged.getTable();
        initGUI();
        loadTable();
        SwingUtils.setColor_r(this, getBackground(), getForeground());

        // Save on change listener
        table.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(final TableModelEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (!paged.isSelfChanging() && openFilePath != null && (System.currentTimeMillis() - lastSaveTime) > SAVE_RELOAD_TIME) {
                            log.info("Table change detected, saving...");
                            lastSaveTime = System.currentTimeMillis();
                            //                            table.saveTableToPath(openFilePath);
                            paged.savePage();
                        }
                    }
                });
            }
        });

        //Create the SFTP update notifier file
        if (!FileUtils.exists("prg/" + SFTP_ALERT_FILE)) {
            try {
                new PrintWriter(FileUtils.getFile("prg/" + SFTP_ALERT_FILE)).close();
            } catch (FileNotFoundException e) {
                log.warn("Unable to clear SFTP alert file: {}", e.getMessage());
            }
        }

        FileWatcher.watchFile("prg", new FileListenerAdapter() {
            public void fileCreated(File f) {
                fileChanged(f);
            }

            public void fileChanged(final File f) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (f.getName().equals(SFTP_ALERT_FILE) && f.length() > 0) {
                            try {
                                new PrintWriter(FileUtils.getFile("prg/" + SFTP_ALERT_FILE)).close(); // Clear the file
                            } catch (FileNotFoundException e) {
                                log.warn("Unable to clear SFTP alert file: {}", e.getMessage());
                            }
                            log.info("SFTP Notification received, reloading...");
                            lastSaveTime = System.currentTimeMillis();
                            paged.fullReload();
                        }
                    }
                });
            }
        });
    }

    /**
     * Initializes all of the GUI components in the TV1000 Programmer
     */
    private void initGUI() {
        // Programmer Controls

        // Initialize the control panel
        // Button action listeners

        // INSERT
        insert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paged.insertRow();
            }
        });

        // DELETE
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paged.deleteRow(table.getSelectedRow());
            }
        });

        // Assemble the control panel
        JPanel controls = SwingUtils.createGridJPanel(1, 3, insert, delete, HMI.getInstance().generateReturnButton());
        controls.setPreferredSize(new Dimension(getWidth(), 75));
        SwingUtils.setFont_r(controls, controls.getFont().deriveFont(FONT_SIZE).deriveFont(Font.BOLD));

        // Add everything
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1050, 850));
        add(paged, BorderLayout.CENTER);
        add(controls, BorderLayout.SOUTH);
    }

    /**
     * @return Get the base name of the working file (no extension)
     */
    private String getProgramBaseFileName() {
        return openFilePath.substring(0, openFilePath.lastIndexOf(EXTENSION));
    }

    /**
     * @return Gets the path to the open JTB file
     */
    private String getTablePath() {
        return String.format("%s%s", getProgramBaseFileName(), EXTENSION);
    }

    /**
     * NOTE: The ID feature has been removed, but the file still uses this format
     *
     * @param program Program ID
     * @return Path to the PRG file with the given ID
     */
    private String getProgramPath(int program) {
        return String.format("%s-%d%s", getProgramBaseFileName(), program, PROGRAM_EXTENSION);
    }

    /**
     * Prompts the user to browse to a JTB file and loads it
     */
    public void loadTable() {
        // Load the JTB and PRG
        openFilePath = settings.getString(KEY_JTB_PATH);
        paged.loadTableFromFile(openFilePath);
        paged.loadProgram(getProgramPath(PROGRAM_DEFAULT));
    }
}
