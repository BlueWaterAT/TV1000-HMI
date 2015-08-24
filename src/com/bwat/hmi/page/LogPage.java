package com.bwat.hmi.page;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bwat.hmi.Constants;
import com.bwat.hmi.HMI;
import com.bwat.hmi.ui.CSVJList;
import com.bwat.hmi.util.JSONUtils;

public class LogPage extends HMIPage {
	public LogPage() {
		super( "Log" );
		
		setLayout( new BorderLayout() );
		JSONObject json = JSONUtils.loadObjectFromFile( Constants.LOG.PATH );
		
		JTabbedPane tabPane = new JTabbedPane();
		JSONArray tabs = json.getJSONArray( Constants.LOG.KEY_TABS );
		for ( int i = 0; i < tabs.length(); i++ ) {
			JSONObject tab = tabs.getJSONObject( i );
			JScrollPane scroll =  new JScrollPane( new CSVJList( tab.getString( Constants.LOG.KEY_DATAPATH ), true ), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
			tabPane.addTab( tab.getString( Constants.LOG.KEY_NAME ), scroll);
		}
		
		add( tabPane, BorderLayout.CENTER );
		
		add(getButtonPanel(), BorderLayout.EAST);
	}
}
