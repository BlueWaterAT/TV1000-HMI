package com.bwat.hmi;

import com.bwat.hmi.util.ArrayUtils;
import com.bwat.hmi.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.UIManager;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class HMIDriver {
    static Logger log = LoggerFactory.getLogger(HMIDriver.class);

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
            log.error("FATAL: HMI has crashed. Exception details: \n{}\n{}\n", e.getMessage(), ArrayUtils.join(e.getStackTrace(), "\n"));
            System.exit(1); //Make sure the app closes completely
        }
    }
}
