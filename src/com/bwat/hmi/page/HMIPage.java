package com.bwat.hmi.page;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.bwat.hmi.HMI;

public class HMIPage extends JPanel {
	private String name;
	
	protected HMIPage( String name ) {
		this.name = name;
	}
	
	public String getPageName() {
		return name;
	}
	
	protected JPanel getButtonPanel(Component filler) {
		JPanel buttonPanel = new JPanel( new GridBagLayout() );
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.weighty = .9;
		buttonPanel.add( filler, c );

		c.fill = GridBagConstraints.BOTH;
		c.weighty = .1;
		c.gridy = 1;
		buttonPanel.add( HMI.getInstance().generateReturnButton(), c );
		
		return buttonPanel;
	}
	
	protected JPanel getButtonPanel() {
		return getButtonPanel( new JLabel() );
	}
}
