package com.bwat.hmi.ui;

import java.awt.Color;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

public class ColorLockedJButton extends JButton {
	public ColorLockedJButton() {
		super();
	}

	public ColorLockedJButton( Action a ) {
		super( a );
	}

	public ColorLockedJButton( Icon icon ) {
		super( icon );
	}

	public ColorLockedJButton( String text, Icon icon ) {
		super( text, icon );
	}

	public ColorLockedJButton( String text ) {
		super( text );
	}

	@Override
	public void setBackground( Color bg ) {}
	
	@Override
	public void setForeground( Color fg ) {}
	
	public void setLockedBackground( Color bg ) {
		super.setBackground( bg );
	}
	
	public void setLockedForeground( Color fg ) {
		super.setForeground( fg );
	}
}
