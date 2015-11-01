package com.bwat.hmi.data;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class BindedJButton extends JButton	 {
	BindedString data;
	
	public BindedJButton(String path) {
		data = new BindedString( path, new BindedStringListener() {
			@Override
			public void stringChanged( String content ) {
				if ( !content.equals( getText() ) ) {
					setData( content );
				}
			}
		} );
		setData( data.getContent() );
	}
	
	public void setData(String text) {
		System.out.println("BUTTON TEXT");
		super.setText( text );
		data.setContent( text );
	}
	
}
