package com.bwat.hmi.data;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A JTextfield linked to a BindedString that updates automatically
 * 
 * @author Kareem El-Faramawi
 */
public class BindedJTextField extends JTextField implements DocumentListener {
	BindedString data;
	
	public BindedJTextField( String path ) {
		data = new BindedString( path, new BindedStringListener() {
			@Override
			public void stringChanged( String content ) {
				if ( !content.equals( getText() ) ) {
					setText( content );
				}
			}
		} );
		setText( data.getContent() );
		setEditable( false );
		setSelectedTextColor( new Color( 0, 0, 0, 0 ) );
		setFocusable( false );
		getDocument().addDocumentListener( this );
	}
	
	@Override
	public void changedUpdate( DocumentEvent e ) {}
	
	@Override
	public void insertUpdate( DocumentEvent e ) {
		data.setContent( getText() );
	}
	
	@Override
	public void removeUpdate( DocumentEvent e ) {
		data.setContent( getText() );
	}
}
