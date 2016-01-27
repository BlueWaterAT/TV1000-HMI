package com.bwat.hmi.ui;

import com.bwat.hmi.data.BindedString;
import com.bwat.hmi.data.BindedStringListener;
import com.bwat.hmi.util.ArrayUtils;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import java.util.Arrays;

/**
 * A JList that has its data binded to a CSV file. As the file is modified, the list will update
 *
 * @author Kareem El-Faramawi
 */
public class CSVJList extends JList<String> {
    // Used for displaying the list in reverse order
    private boolean reversed = false;
    private BindedString listData; // Data updates automatically
    static int sizeLimit = Integer.MAX_VALUE; // Default limit to "unlimited"

    /**
     * Creates the JList in non-reversed order with data at the given CSV file path
     *
     * @param path Path to CSV file
     */
    public CSVJList(String path) {
        this(path, false);
    }

    /**
     * Creates the JList with data at the given CSV file path
     *
     * @param path     Path to CSV file
     * @param reversed If the data should be displayed in reverse order
     */
    public CSVJList(String path, boolean reversed) {
        this.reversed = reversed;
        // Listen for changes to the CSV data String and update accordingly
        listData = new BindedString(path, new BindedStringListener() {
            @Override
            public void stringChanged(String content) {
                parseCSV(content);
            }
        });
        parseCSV(listData.getContent());
    }

    public static void setCSVLimit(int max) {
        if (max > 0) {
            sizeLimit = max;
        }
    }

    /**
     * @return Gets the list model for the JList
     */
    private DefaultListModel<String> getDefaultListModel() {
        // "Cast" ListModel into DefaultListModel by copying elements over
        DefaultListModel<String> lm = new DefaultListModel<String>();
        for (int i = 0; i < getModel().getSize(); i++) {
            lm.addElement(getModel().getElementAt(i));
        }
        return lm;
    }

    /**
     * Parses and displays the CSV String read directly from the file
     *
     * @param csv Raw CSV data
     */
    private void parseCSV(String csv) {
        String[] items = csv.split("\\s*,\\s*");
        if (reversed) {
            items = ArrayUtils.reverse(items);
        }

        if (items.length > sizeLimit) {
            items = Arrays.copyOf(items, sizeLimit);
            listData.setContent(ArrayUtils.join(reversed ? ArrayUtils.reverse(items) : items, ",\n"));
        }

        DefaultListModel<String> lm = getDefaultListModel();
        lm.clear();
        for (String item : items) {
            lm.add(lm.size(), item);
        }
        setModel(lm);
    }

    /**
     * @return If the JList is displayed in reverse order
     */
    public boolean isReversed() {
        return reversed;
    }

    /**
     * Sets how the JList should be displayed
     *
     * @param reversed If the JList is displayed in reverse order
     */
    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }
}
