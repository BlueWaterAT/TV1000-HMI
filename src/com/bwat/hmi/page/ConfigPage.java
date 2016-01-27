package com.bwat.hmi.page;

import com.bwat.hmi.Constants;
import com.bwat.hmi.HMI;
import com.bwat.hmi.data.BindedJButton;
import com.bwat.hmi.ui.KeyboardDialog;
import com.bwat.hmi.ui.PagedJPanel;
import com.bwat.hmi.ui.StatusNumberItem;
import com.bwat.hmi.util.JSONUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfigPage extends HMIPage {
    public ConfigPage() {
        super("Config");
        setLayout(new BorderLayout(Constants.HV_GAP, Constants.HV_GAP));

        JSONObject json = JSONUtils.loadObjectFromFile(Constants.CONFIG.PATH);

        JSONArray values = json.getJSONArray(Constants.CONFIG.KEY_VALUES);
        PagedJPanel configPanel = new PagedJPanel();

        JSONObject settings = HMI.getInstance().getSettings();
        int spinnersPerPage = settings.has(Constants.SETTINGS.KEY_PAGE_SPINNERS) ? settings.getInt(Constants.SETTINGS.KEY_PAGE_SPINNERS) : Constants.DEFAULT_PAGE_SPINNERS;

        for (int i = 0; i < values.length(); /* increment handled by inner loop */) {
            JPanel labels = new JPanel(new GridLayout(spinnersPerPage, 1, 0, Constants.HV_GAP));
            JPanel valueButtons = new JPanel(new GridLayout(spinnersPerPage, 1, 0, Constants.HV_GAP));
            for (int j = 0; j < spinnersPerPage && i < values.length(); j++, i++) {
                JSONObject jsonValue = values.getJSONObject(i);
                String type = Constants.DATA_TYPE_NUM;
                if (jsonValue.has(Constants.KEY_DATA_TYPE)) {
                    type = jsonValue.getString(Constants.KEY_DATA_TYPE);
                }

                if (type.equals(Constants.DATA_TYPE_NUM)) {
                    int min = jsonValue.has(Constants.CONFIG.KEY_MIN) ? jsonValue.getInt(Constants.CONFIG.KEY_MIN) : 0;
                    int max = jsonValue.has(Constants.CONFIG.KEY_MAX) ? jsonValue.getInt(Constants.CONFIG.KEY_MAX) : 100;
                    StatusNumberItem value = new StatusNumberItem(jsonValue.getString(Constants.CONFIG.KEY_NAME), jsonValue.getString(Constants.CONFIG.KEY_DATAPATH), min, max);
                    labels.add(value.getLabel());
                    valueButtons.add(value.getValueButton());
                } else if (type.equals(Constants.DATA_TYPE_STR)) {
                    labels.add(new JLabel(jsonValue.getString(Constants.CONFIG.KEY_NAME)));
                    final BindedJButton val = new BindedJButton(jsonValue.getString(Constants.CONFIG.KEY_DATAPATH));
                    val.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String v = KeyboardDialog.showDialog("Enter value: ", "");
                            if (v.length() > 0) {
                                val.setData(v);
                            }
                        }
                    });
                    valueButtons.add(val);
                }
            }
            JPanel page = new JPanel(new BorderLayout(Constants.HV_GAP, Constants.HV_GAP));
            page.add(labels, BorderLayout.WEST);
            page.add(valueButtons, BorderLayout.CENTER);
            configPanel.addPage(page);
        }
        add(configPanel, BorderLayout.CENTER);
        add(getButtonPanel(), BorderLayout.EAST);
    }
}
