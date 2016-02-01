package com.bwat.hmi.page;

import com.bwat.hmi.Constants;
import com.bwat.hmi.HMIDriver;
import com.bwat.hmi.Logger;
import com.bwat.hmi.data.BindedJButton;
import com.bwat.hmi.data.BindedString;
import com.bwat.hmi.data.BindedStringListener;
import com.bwat.hmi.ui.KeyboardDialog;
import com.bwat.hmi.util.JSONUtils;
import org.json.JSONArray;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class NetSettingsPage extends HMIPage {
    BindedString interfacesContent;

    private final static String INTERFACES_PATH = "/etc/network/interfaces";
    private final static String INTERFACES_START = "auto lo\niface lo inet loopback\n\nauto wlan0\niface wlan0 inet static\n";
    private final static String INTERFACES_INDENT = "  ";

    private final static String INTERFACES_SETTINGS[] = {"address", "netmask", "gateway", "wpa-ssid", "wpa-psk"};
    private JButton settingVals[] = new JButton[INTERFACES_SETTINGS.length];

    public NetSettingsPage() {
        super("Network Settings");
        setLayout(new BorderLayout(Constants.HV_GAP, Constants.HV_GAP));

        if (HMIDriver.dev) {
            add(new JLabel("UNSUPPORTED IN DEV MODE"));
            add(getButtonPanel(), BorderLayout.EAST);
        } else {
            interfacesContent = new BindedString(new File(INTERFACES_PATH), new BindedStringListener() {
                @Override
                public void stringChanged(String content) {
                    updateValues();
                }
            });


            JPanel settingsPanel = new JPanel(new BorderLayout(Constants.HV_GAP, Constants.HV_GAP));
            JSONArray values = JSONUtils.loadObjectFromFile(Constants.NET.PATH).getJSONArray(Constants.NET.KEY_VALUES);

            JPanel labelPanel = new JPanel(new GridLayout(INTERFACES_SETTINGS.length + values.length(), 1));
            JPanel valPanel = new JPanel(new GridLayout(INTERFACES_SETTINGS.length + values.length(), 1));
            for (int i = 0; i < INTERFACES_SETTINGS.length; i++) {
                labelPanel.add(new JLabel(INTERFACES_SETTINGS[i]));
                valPanel.add(settingVals[i] = new SettingsValueButton());
            }
            updateValues();

            for (int i = 0; i < values.length(); i++) {
                // StatusNumberItem value = new StatusNumberItem( jsonValue.getString( Constants.NET.KEY_NAME ),
                // jsonValue.getString( Constants.NET.KEY_DATAPATH ));
                labelPanel.add(new JLabel(values.getJSONObject(i).getString(Constants.NET.KEY_NAME)));
                final BindedJButton val = new BindedJButton(values.getJSONObject(i).getString(Constants.NET.KEY_DATAPATH));
                val.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String v = KeyboardDialog.showDialog("Enter value: ", "");
                        if (v.length() > 0) {
                            val.setData(v);
                        }
                    }
                });
                valPanel.add(val);
            }

            settingsPanel.add(labelPanel, BorderLayout.WEST);
            settingsPanel.add(valPanel, BorderLayout.CENTER);

            add(settingsPanel);
            add(getButtonPanel(), BorderLayout.EAST);
        }
    }

    public void updateValues() {
        String c = interfacesContent.getContent();
        Logger.logLine(String.format("READ:\n%s\nEND", c));
        for (int i = 0; i < INTERFACES_SETTINGS.length; i++) {
            String key = INTERFACES_SETTINGS[i];
            if (c.contains(key)) {
                String sub = c.substring(c.indexOf(key));
                int newlineI = sub.indexOf("\n");
                settingVals[i].setText((sub.substring(key.length() + 1, newlineI >= 0 ? newlineI : sub.length())).replaceAll("\"", "").trim());
            }
        }
    }

    public void rebuildInterfaces() {
        String interfaces = "" + INTERFACES_START;
        for (int i = 0; i < INTERFACES_SETTINGS.length; i++) {
            String text = settingVals[i].getText();
            interfaces += String.format("%s%s %s\n", INTERFACES_INDENT, INTERFACES_SETTINGS[i], text.contains(" ") ? "\"" + text + "\"" : text);
        }
        Logger.logLine(interfaces);
        // Set file content
        interfacesContent.setContent(interfaces);
    }

    class SettingsValueButton extends JButton {
        public SettingsValueButton() {
            super();
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    setText(KeyboardDialog.showDialog("Enter value", ""));
                    rebuildInterfaces();
                }
            });
        }
    }
}
