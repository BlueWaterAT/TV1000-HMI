package com.bwat.hmi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Simple message logger that writes to a text file
 *
 * @author Kareem El-Faramawi
 */
public class Logger {
    private static boolean fileCreated;
    private static final String LOG_PATH = "/log" + System.currentTimeMillis() + ".txt";
    private static final File LOG_FILE = new File(LOG_PATH);
    private static BufferedWriter out;

    /**
     * Creates the log file if it doesn't already exist
     */
    private static void createLogFile() {
        if (!LOG_FILE.exists()) {
            try {
                LOG_FILE.getParentFile().mkdirs();
                LOG_FILE.createNewFile();
                fileCreated = true;
                out = new BufferedWriter(new FileWriter(LOG_FILE));
            } catch (IOException e) {
                System.err.println("FAILED TO CREATE LOG FILE");
                // e.printStackTrace();
            }
        } else {
            fileCreated = true;
        }
    }

    /**
     * Logs a message and terminates with a newline
     *
     * @param message Message to log
     */
    public static void logLine(String message) {
        System.out.println(message);
        if (!fileCreated) {
            createLogFile();
        }
        if (fileCreated) {
            try {
                for (String line : message.split("\n")) {
                    out.write(line);
                    out.newLine();
                }
                out.flush();
            } catch (IOException e) {
                // e.printStackTrace();
            }
        }
    }
}
