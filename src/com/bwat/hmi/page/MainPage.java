package com.bwat.hmi.page;

import com.bwat.hmi.Constants;
import com.bwat.hmi.HMI;
import com.bwat.hmi.ui.StatusValueItem;
import com.bwat.hmi.util.JSONUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainPage extends HMIPage {
    public MainPage() {
        super("MAIN");
        setLayout(new GridLayout(1, 3, Constants.HV_GAP, 0));
        // Load the setup JSON
        JSONObject json = JSONUtils.loadObjectFromFile(Constants.MAIN.PATH);

        // Create the status displays
        JSONArray statusDefs = json.getJSONArray(Constants.MAIN.KEY_STATUS);
        JPanel status = new JPanel(new GridLayout(statusDefs.length(), 1, 0, Constants.HV_GAP));
        for (int i = 0; i < statusDefs.length(); i++) {
            JSONObject s = statusDefs.getJSONObject(i);
            status.add(new StatusValueItem(s.getString(Constants.MAIN.KEY_NAME), s.getString(Constants.MAIN.KEY_DATAPATH)));
        }
        add(status);

        // Create timers display panel
        JSONArray timerDefs = json.getJSONArray(Constants.MAIN.KEY_TIMERS);
        JPanel timers = new JPanel(new GridLayout(timerDefs.length(), 1, 0, Constants.HV_GAP));
        for (int i = 0; i < timerDefs.length(); i++) {
            JSONObject s = timerDefs.getJSONObject(i);
            timers.add(new StatusValueItem(s.getString(Constants.MAIN.KEY_NAME), s.getString(Constants.MAIN.KEY_DATAPATH)));
        }
        add(timers);

        // Create the menu
//		JPanel right = new JPanel( new BorderLayout( 0, Constants.HV_GAP ) );

        // Create battery display
//		final JProgressBar battery = new JProgressBar( 0, 100 );
//		battery.setIndeterminate( false );
//		battery.setStringPainted( true );
//		battery.addChangeListener( new ChangeListener() {
//			public void stateChanged( ChangeEvent arg0 ) {
//				battery.setString( "Battery: " + String.format( "%d%%", (int) ( battery.getPercentComplete() * 100 ) ) );
//			}
//		} );
//		battery.setValue( 100 );
//		right.add( battery, BorderLayout.NORTH );

        // Load and generate navigation buttons
        JSONArray pages = json.getJSONArray(Constants.MAIN.KEY_PAGES);
        JPanel menu = new JPanel(new GridLayout(pages.length(), 1, 0, Constants.HV_GAP));
        for (int i = 0; i < pages.length(); i++) {
            final String page = pages.getString(i);
            JButton b = new JButton(page);
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    HMI.getInstance().goToPage(page);
                }
            });
            menu.add(b);
        }
//		right.add( menu, BorderLayout.CENTER );

        add(menu);
    }
}
