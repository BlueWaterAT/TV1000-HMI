package com.bwat.hmi.page;

import com.bwat.hmi.Constants;
import com.bwat.hmi.ui.StatusSwitchItem;
import com.bwat.hmi.util.JSONUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class StatusPage extends HMIPage {
    public StatusPage() {
        super("Status");

        setLayout(new BorderLayout());

        JSONArray json = JSONUtils.loadArrayFromFile(Constants.STATUS.PATH);
        JPanel statusPanel = new JPanel(new GridLayout((int) Math.ceil(json.length() / 3.0), 3, Constants.HV_GAP, Constants.HV_GAP));

        for (int i = 0; i < json.length(); i++) {
            JSONObject status = json.getJSONObject(i);
            statusPanel.add(new StatusSwitchItem(status.getString(Constants.STATUS.KEY_NAME), status.getString(Constants.STATUS.KEY_DATAPATH)));
        }
        add(statusPanel, BorderLayout.CENTER);

        add(getButtonPanel(), BorderLayout.EAST);
    }
}
