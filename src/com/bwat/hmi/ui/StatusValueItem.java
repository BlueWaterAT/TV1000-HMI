package com.bwat.hmi.ui;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.bwat.hmi.data.BindedJTextField;

/**
 * A single value display that updates automatically from a file
 * 
 * @author Kareem El-Faramawi
 *
 */
public class StatusValueItem extends JPanel {
	private JLabel label;
	
	// Automaticaly updating value display
	private BindedJTextField value;
	
	/**
	 * Creates a StatusValueItem with the given name, updated from a given path
	 * 
	 * @param name Name of this item
	 * @param path Path to data file
	 */
	public StatusValueItem( String name, String path ) {
		label = new JLabel( name, JLabel.CENTER );
		value = new BindedJTextField( path );
		value.setHorizontalAlignment( JTextField.CENTER );
		
		setLayout( new GridLayout( 1, 2 ) );
		add( label );
		add( value );
	}
	
	public JTextField getJTextField() {
		return value;
	}
}
