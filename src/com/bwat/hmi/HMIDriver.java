package com.bwat.hmi;

import com.bwat.hmi.util.FileUtils;

import javax.swing.JFrame;
import javax.swing.UIManager;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class HMIDriver {
    public static boolean dev = false; // Just so I don't have to deal with fullscreen

    public static void main(String[] args) {
        //Checks if a file called dev exists. This will enable dev mode
        dev = FileUtils.exists("dev");
        System.out.println(dev);

        try {
            UIManager.put("ScrollBar.width", 30); // TODO: Make scrollbar width modifiable from json

            // Create window
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Create the HMI
            HMI hmi = HMI.getInstance();
            hmi.buildGUI();
            frame.add(hmi);

            // Screen settings
            if (dev) { // Windowed version
                hmi.setPreferredSize(new Dimension(800, 600));
                frame.pack();
            } else { // Fullscreen
                GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                hmi.setPreferredSize(device.getDefaultConfiguration().getBounds().getSize());
                frame.setUndecorated(true);
                device.setFullScreenWindow(frame);
            }

            // Open window
            frame.setResizable(false);
            frame.setVisible(true);

        } catch (Exception e) {
            // just so we get some kind of response if the HMI doesn't run
            Logger.logLine("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
