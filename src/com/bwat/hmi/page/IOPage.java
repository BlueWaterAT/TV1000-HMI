package com.bwat.hmi.page;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bwat.hmi.Constants;
import com.bwat.hmi.HMI;
import com.bwat.hmi.ui.IOItem;
import com.bwat.hmi.util.JSONUtils;

public class IOPage extends HMIPage {
	public IOPage() {
		super( "I/O" );
		
		setLayout( new BorderLayout() );
		
		final CardLayout cl = new CardLayout();
		final JPanel ioPanel = new JPanel( cl );
		JSONObject json = JSONUtils.loadObjectFromFile( Constants.IO.PATH );
		
		final JRadioButton inSwitch = new JRadioButton( "Inputs", true );
		final JRadioButton outSwitch = new JRadioButton( "Outputs" );
		ChangeListener listener = new ChangeListener() {
			@Override
			public void stateChanged( ChangeEvent e ) {
				if ( inSwitch.isSelected() ) {
					cl.show( ioPanel, Constants.IO.KEY_INPUTS );
				} else {
					cl.show( ioPanel, Constants.IO.KEY_OUTPUTS );
				}
			}
		};
		inSwitch.addChangeListener( listener );
		inSwitch.addChangeListener( listener );
		
		// INPUTS
		JSONArray inputs = json.getJSONArray( Constants.IO.KEY_INPUTS );
		JPanel inputsPanel = new JPanel( new GridLayout( 1, inputs.length(), Constants.HV_GAP, 0 ) );
		for ( int i = 0; i < inputs.length(); i++ ) {
			JPanel input = new JPanel( new BorderLayout() );
			input.add( new JLabel( "Input Card " + i, JLabel.CENTER ), BorderLayout.NORTH );
			
			JPanel ioListPanel = createIOPanel( inputs.getJSONArray( i ) );
			input.add( ioListPanel, BorderLayout.CENTER );
			
			inputsPanel.add( input );
		}
		ioPanel.add( inputsPanel, Constants.IO.KEY_INPUTS );
		
		// OUTPUTS
		JSONArray outputs = json.getJSONArray( Constants.IO.KEY_OUTPUTS );
		JPanel outputsPanel = new JPanel( new GridLayout( 1, outputs.length(), Constants.HV_GAP, 0 ) );
		for ( int i = 0; i < outputs.length(); i++ ) {
			JPanel output = new JPanel( new BorderLayout() );
			output.add( new JLabel( "Output Card " + i, JLabel.CENTER ), BorderLayout.NORTH );
			
			JPanel ioListPanel = createIOPanel( outputs.getJSONArray( i ) );
			output.add( ioListPanel, BorderLayout.CENTER );
			
			outputsPanel.add( output );
			
		}
		ioPanel.add( outputsPanel, Constants.IO.KEY_OUTPUTS );
		
		add( ioPanel, BorderLayout.CENTER );
		
		JPanel right = new JPanel( new BorderLayout() );
		
		ButtonGroup bg = new ButtonGroup();
		bg.add( inSwitch );
		bg.add( outSwitch );
		JPanel ioSwitchPanel = new JPanel( new GridLayout( 2, 1, 0, Constants.HV_GAP ) );
		ioSwitchPanel.add( inSwitch );
		ioSwitchPanel.add( outSwitch );
		
		right.add( ioSwitchPanel, BorderLayout.NORTH );
		
//		right.add( HMI.getInstance().generateReturnButton(), BorderLayout.SOUTH );
		add( right, BorderLayout.EAST );
		add(getButtonPanel( ioSwitchPanel ), BorderLayout.EAST);
		
		cl.show( ioPanel, Constants.IO.KEY_INPUTS );
	}
	
	private JPanel createIOPanel( JSONArray ioList ) {
		JPanel ios = new JPanel( new GridLayout( ioList.length(), 1, 0, Constants.HV_GAP ) );
		for ( int i = 0; i < ioList.length(); i++ ) {
			JSONObject ioEntry = ioList.getJSONObject( i );
			ios.add( new IOItem( ioEntry.getString( Constants.IO.KEY_NAME ), ioEntry.getString( Constants.IO.KEY_DATAPATH ), "" + i ) );
		}
		return ios;
	}
}
