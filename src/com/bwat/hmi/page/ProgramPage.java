package com.bwat.hmi.page;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.bwat.hmi.HMI;
import com.bwat.hmi.prg.InteractiveJTable;

public class ProgramPage extends HMIPage {
	public ProgramPage() {
		super( "Path Program" );
		setLayout( new BorderLayout() );
		
		add( new InteractiveJTable(), BorderLayout.CENTER );
	}
}
