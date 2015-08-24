package com.bwat.hmi;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.json.JSONObject;

import com.bwat.hmi.data.BindedJTextField;
import com.bwat.hmi.file.FileWatcher;
import com.bwat.hmi.page.ConfigPage;
import com.bwat.hmi.page.HMIPage;
import com.bwat.hmi.page.IOPage;
import com.bwat.hmi.page.LogPage;
import com.bwat.hmi.page.MainPage;
import com.bwat.hmi.page.ProgramPage;
import com.bwat.hmi.page.RFIDPage;
import com.bwat.hmi.page.StatusPage;
import com.bwat.hmi.ui.CSVJList;
import com.bwat.hmi.ui.KeypadDialog;
import com.bwat.hmi.ui.ListDialog;
import com.bwat.hmi.util.JSONUtils;
import com.bwat.hmi.util.StringUtils;
import com.bwat.hmi.util.SwingUtils;

/**
 * The main HMI panel that handles the displaying of multiple pages and status bars
 * 
 * @author Kareem El-Faramawi
 */
public class HMI extends JPanel {
	// Singleton instance of the HMI
	static HMI INSTANCE = null;
	// JPanel and layout for handling multiple pages
	CardLayout layout = new CardLayout( Constants.HV_GAP, Constants.HV_GAP );
	JPanel cardPanel = null;
	
	// Status bar at the bottom of the screen
	BindedJTextField statusBar = null;
	// Name at the top of the screen
	JLabel nameBar = null;
	
	// All page instances
	MainPage main;
	LogPage log;
	StatusPage status;
	IOPage io;
	ProgramPage prg;
	RFIDPage rfid;
	ConfigPage config;
	
	// Stores the name of the page currently being displayed
	String currentPage;
	
	// Settings for the HMI
	JSONObject settings;
	
	Color buttonColor;
	
	/**
	 * @return Singleton instance of the HMI
	 */
	public static HMI getInstance() {
		if ( INSTANCE == null ) {
			INSTANCE = new HMI();
		}
		return INSTANCE;
	}
	
	/**
	 * Initializes HMI with some settings
	 */
	private HMI() {
		settings = JSONUtils.loadObjectFromFile( Constants.SETTINGS.PATH );
		if ( settings.has( Constants.SETTINGS.KEY_FILE_POLL_DELAY ) ) {
			FileWatcher.setPollDelay( settings.getLong( Constants.SETTINGS.KEY_FILE_POLL_DELAY ) );
		}
		if ( settings.has( Constants.SETTINGS.KEY_CSV_LIST_LIMIT ) ) {
			CSVJList.setCSVLimit( settings.getInt( Constants.SETTINGS.KEY_CSV_LIST_LIMIT ) );
		}
	}
	
	/**
	 * Creates the HMI display
	 */
	public void buildGUI() {
		// Read font size from settings
		setFont( getFont().deriveFont( settings.has( Constants.SETTINGS.KEY_FONT_SIZE ) ? (float) settings.getDouble( Constants.SETTINGS.KEY_FONT_SIZE ) : 20f ) );
		if ( settings.has( Constants.SETTINGS.KEY_BG_COLOR ) ) {
			String col = settings.getString( Constants.SETTINGS.KEY_BG_COLOR );
			if ( StringUtils.isHexColor( col ) ) {
				setBackground( Color.decode( col ) );
			}
		}
		if ( settings.has( Constants.SETTINGS.KEY_FG_COLOR ) ) {
			String col = settings.getString( Constants.SETTINGS.KEY_FG_COLOR );
			if ( StringUtils.isHexColor( col ) ) {
				setForeground( Color.decode( col ) );
			}
		}
		
		if ( settings.has( Constants.SETTINGS.KEY_BUTTON_COLOR ) ) {
			String col = settings.getString( Constants.SETTINGS.KEY_BUTTON_COLOR );
			if ( StringUtils.isHexColor( col ) ) {
				buttonColor = Color.decode( col );
			}
		}
		// Add all pages to the JPanel layout
		cardPanel = new JPanel( layout );
		addCard( cardPanel, main = new MainPage() );
		addCard( cardPanel, log = new LogPage() );
		addCard( cardPanel, status = new StatusPage() );
		addCard( cardPanel, io = new IOPage() );
		addCard( cardPanel, prg = new ProgramPage() );
		addCard( cardPanel, rfid = new RFIDPage() );
		addCard( cardPanel, config = new ConfigPage() );
		
		setLayout( new BorderLayout() );
		add( cardPanel, BorderLayout.CENTER );
		
		// Create and display status bar
		statusBar = new BindedJTextField( settings.getString( Constants.SETTINGS.KEY_STATUS_BAR_PATH ) );
		statusBar.setBorder( null );
		add( statusBar, BorderLayout.SOUTH );
		
		// Create and display name
		nameBar = new JLabel( settings.getString( Constants.SETTINGS.KEY_VEHICLE_NAME ), JLabel.CENTER );
		add( nameBar, BorderLayout.NORTH );
		
		// Recursively set the font size of all components in the HMI to the size found in the settings
		SwingUtils.setFontSize_r( this, getFont().getSize2D() );
		
		// Set HMI colors
		if ( buttonColor == null ) {
			buttonColor = new JButton().getBackground();
		}
		ListDialog.setColors( getBackground(), getForeground(), buttonColor );
		KeypadDialog.setColors( getBackground(), getForeground(), buttonColor );
		SwingUtils.setBG_r( this, getBackground() );
		SwingUtils.setFG_r( this, getForeground() );
		for ( Component c : SwingUtils.getAllComponents_r( this ) ) {
			if ( c instanceof JButton ) {
				c.setBackground( buttonColor );
			}
		}
		
		// Jump to the main page at startup
		toMainPage();
	}
	
	/**
	 * Adds a HMIPage to the CardLayout using the page's name as the label
	 * 
	 * @param panel
	 * @param page
	 */
	private void addCard( JPanel panel, HMIPage page ) {
		panel.add( page, page.getPageName() );
	}
	
	/**
	 * Make the chosen page visible
	 */
	public void updateDisplay() {
		layout.show( cardPanel, currentPage );
	}
	
	/**
	 * Jump to and display the page with the given name
	 * 
	 * @param page Name of page
	 */
	public void goToPage( String page ) {
		currentPage = page;
		updateDisplay();
	}
	
	/**
	 * Jump to the main home page
	 */
	public void toMainPage() {
		goToPage( main.getPageName() );
	}
	
	/**
	 * @return Loaded JSON settings for the HMI
	 */
	public JSONObject getSettings() {
		return settings;
	}
	
	/**
	 * @return A generated button to send you back to this main page
	 */
	public JButton generateReturnButton() {
		JButton b = new JButton( "Main Menu" );
		b.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				toMainPage();
			}
		} );
		return b;
	}
}
