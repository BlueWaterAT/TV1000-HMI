package com.bwat.hmi.util;

import com.bwat.hmi.HMIDriver;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * Utility file I/O functions that help dealing with the UNIX filesystem paths
 *
 * @author Kareem El-Faramawi
 */
public class FileUtils {

    // There was an issue with the path during runtime not being the path to the jar file,
    // so no files could be accessed. This finds and saves the actual runtime path.
    private static String HOME = "";

    static {
        try {
            File homeFile = new File(HMIDriver.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            HOME = (homeFile.isFile() ? homeFile.getParent() : homeFile.getAbsolutePath()) + File.separator;
//			HOME = new File( HMIDriver.class.getProtectionDomain().getCodeSource().getLocation().toURI() ).getParent() + File.separator;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param filename Relative path to a file from the HMI
     * @return Reference to the File at the given path
     */
    public static File getFile(String filename) {
        return new File(HOME + filename);
    }

    /**
     * @param path Relative path to a file from the HMI
     * @return If the file exists
     */
    public static boolean exists(String path) {
        return getFile(path).exists();
    }

    /**
     * @param path Relative path to a file from the HMI
     * @return If the path refers to a file
     */
    public static boolean isFile(String path) {
        return getFile(path).isFile();
    }

    /**
     * @param path Relative path to a file from the HMI
     * @return If the path refers to a directory
     */
    public static boolean isDirectory(String path) {
        return getFile(path).isDirectory();
    }

    /**
     * Reads the contents of a file as a String
     *
     * @param file File to read from
     * @return Contents of the file
     */
    public static String readToString(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Saves a string to a file, overwriting it
     *
     * @param str  String to save
     * @param file File to save to String to
     */
    public static void writeStringAsFile(String str, File file) {
        if (file != null && file.isFile()) {
            try {
                Files.write(file.toPath(), str.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
