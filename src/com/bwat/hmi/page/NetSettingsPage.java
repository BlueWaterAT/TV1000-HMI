package com.bwat.hmi.page;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.bwat.hmi.Constants;
import com.bwat.hmi.data.BindedString;
import com.bwat.hmi.data.BindedStringListener;
import com.bwat.hmi.ui.KeyboardDialog;
import com.bwat.hmi.util.SwingUtils;

public class NetSettingsPage extends HMIPage {
	BindedString interfacesContent;
	
	private final static String INTERFACES_START = "auto lo\niface lo inet loopback\n\nauto wlan0\niface wlan0 inet static\n";
	private final static String INTERFACES_INDENT = "  ";
	
	private final static String INTERFACES_SETTINGS[] = { "address", "netmask", "gateway", "wpa-ssid", "wpa-psk" };
	private JButton settingVals[] = new JButton[INTERFACES_SETTINGS.length];
	
	public NetSettingsPage() {
		super( "Network Settings" );
		setLayout( new BorderLayout( Constants.HV_GAP, Constants.HV_GAP ) );
		
		interfacesContent = new BindedString( "/etc/networks/interfaces", new BindedStringListener() {
			// interfacesContent = new BindedString( "interfaces", new BindedStringListener() {
			@Override
			public void stringChanged( String content ) {
				updateValues();
			}
		} );
		
		JPanel settingsPanel = new JPanel( new BorderLayout( Constants.HV_GAP, Constants.HV_GAP ) );
		
		JPanel labelPanel = new JPanel( new GridLayout( INTERFACES_SETTINGS.length, 1 ) );
		JPanel valPanel = new JPanel( new GridLayout( INTERFACES_SETTINGS.length, 1 ) );
		for ( int i = 0; i < INTERFACES_SETTINGS.length; i++ ) {
			labelPanel.add( new JLabel( INTERFACES_SETTINGS[i] ) );
			valPanel.add( settingVals[i] = new SettingsValueButton() );
		}
		updateValues();
		
		settingsPanel.add( labelPanel, BorderLayout.WEST );
		settingsPanel.add( valPanel, BorderLayout.CENTER );
		
		add( settingsPanel );
		add( getButtonPanel(), BorderLayout.EAST );
	}
	
	public void updateValues() {
		String c = interfacesContent.getContent();
		for ( int i = 0; i < INTERFACES_SETTINGS.length; i++ ) {
			String key = INTERFACES_SETTINGS[i];
			if ( c.contains( key ) ) {
				String sub = c.substring( c.indexOf( key ) );
				int newlineI = sub.indexOf( "\n" );
				settingVals[i].setText( ( sub.substring( key.length() + 1, newlineI >= 0 ? newlineI : sub.length() ) ).replaceAll( "\"", "" ).trim() );
			}
		}
	}
	
	public void rebuildInterfaces() {
		String interfaces = "" + INTERFACES_START;
		for ( int i = 0; i < INTERFACES_SETTINGS.length; i++ ) {
			String text = settingVals[i].getText();
			interfaces += String.format( "%s%s %s\n", INTERFACES_INDENT, INTERFACES_SETTINGS[i], text.contains( " " ) ? "\"" + text + "\"" : text );
		}
		System.out.println( interfaces );
		// Set file content
		interfacesContent.setContent( interfaces );
	}
	
	class SettingsValueButton extends JButton {
		public SettingsValueButton() {
			super();
			addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent arg0 ) {
					setText( KeyboardDialog.showDialog( "Enter value", "" ) );
					rebuildInterfaces();
				}
			} );
		}
	}
}
