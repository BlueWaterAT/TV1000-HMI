package com.bwat.hmi.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.bwat.hmi.Constants;
import com.bwat.hmi.HMI;
import com.bwat.hmi.data.BindedString;
import com.bwat.hmi.data.BindedStringListener;
import com.bwat.hmi.util.MathUtils;
import com.bwat.hmi.util.StringUtils;

public class StatusNumberItem extends JPanel {
	// Raw value that gets auto updated
	private BindedString valueSource;
	
	// Value display
	private JButton valueText;
	private JLabel label;
	
	// Value parameters
	private int value;
	private int min;
	private int max;
	
	/**
	 * Creates a status item with the given parameters
	 * 
	 * @param name Name of the item
	 * @param path Path to the data file
	 * @param min Minimum allowed value
	 * @param max Maximum allowed value
	 */
	public StatusNumberItem( String name, String path, int min, int max ) {
		this.min = min;
		this.max = max;
		
		// Listen for changes to the value file and update
		valueSource = new BindedString( path, new BindedStringListener() {
			@Override
			public void stringChanged( String content ) {
				content = content.trim();
				try {
					setValue( Integer.parseInt( content ) );
				} catch ( NumberFormatException e ) {
					JOptionPane.showMessageDialog( StatusNumberItem.this, "Invalid file content \"" + content + "\", cannot be parsed as int" );
					System.err.println( "Invalid file content, cannot be parsed as int" );
				}
			}
		} );
		
		valueText = new JButton();
		valueText.setHorizontalAlignment( JTextField.CENTER );
		valueText.setFocusable( false );
		// Open up a keypad dialog to manually update the value
		valueText.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent arg0 ) {
				String val = KeypadDialog.showDialog( "Enter Value", "");
				if ( StringUtils.isNumber( val ) ) {
					setValue( Integer.parseInt( val ) );
				}
			}
		} );
		
		// Try setting the initial value
		try {
			setValue( Integer.parseInt( valueSource.getContent().trim() ) );
		} catch ( NumberFormatException e ) {
			setValue( 0 );
		}
		
		label = new JLabel( name );
		
		setLayout( new BorderLayout( Constants.HV_GAP, 0 ) );
		add( label, BorderLayout.WEST );
		add( valueText, BorderLayout.CENTER );
	}
	
	// These get the individual components for manual layout
	public JLabel getLabel() {
		return label;
	}
	
	public JButton getValueButton() {
		return valueText;
	}
	
	public StatusNumberItem( String name, String path ) {
		this( name, path, 0, 100 );
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue( int val ) {
		this.value = MathUtils.clamp_i( val, min, max );
		valueText.setText( String.valueOf( value ) );
		valueSource.setContent( String.valueOf( value ) );
	}
	
	public int getMin() {
		return min;
	}
	
	public void setMin( int min ) {
		this.min = min;
	}
	
	public int getMax() {
		return max;
	}
	
	public void setMax( int max ) {
		this.max = max;
	}
}

/*
 * Not deleting the old version quite yet just in case
 * 
 * package com.bwat.hmi;
 * 
 * import java.awt.BorderLayout;
 * import java.awt.event.ActionEvent;
 * import java.awt.event.ActionListener;
 * 
 * import javax.swing.JButton;
 * import javax.swing.JLabel;
 * import javax.swing.JOptionPane;
 * import javax.swing.JPanel;
 * import javax.swing.JTextField;
 * 
 * import com.bwat.hmi.data.BindedString;
 * import com.bwat.hmi.data.BindedStringListener;
 * import com.bwat.hmi.util.MathUtils;
 * import com.bwat.hmi.util.SwingUtils;
 * 
 * public class StatusSpinnerItem extends JPanel {
 * private BindedString valueSource;
 * private JTextField valueText;
 * private JLabel label;
 * private JButton incr;
 * private JButton decr;
 * 
 * private int value;
 * private int step;
 * private int min;
 * private int max;
 * 
 * public StatusSpinnerItem( String name, String path, int valStep, int min, int max ) {
 * this.step = valStep;
 * this.min = min;
 * this.max = max;
 * valueSource = new BindedString( path, new BindedStringListener() {
 * 
 * @Override
 * public void stringChanged( String content ) {
 * content = content.trim();
 * try {
 * setValue( Integer.parseInt( content ) );
 * } catch ( NumberFormatException e ) {
 * JOptionPane.showMessageDialog( StatusSpinnerItem.this, "Invalid file content \"" + content +
 * "\", cannot be parsed as int" );
 * System.err.println( "Invalid file content, cannot be parsed as int" );
 * }
 * }
 * } );
 * valueText = new JTextField();
 * valueText.setHorizontalAlignment( JTextField.CENTER );
 * valueText.setEditable( false );
 * valueText.setFocusable( false );
 * try {
 * setValue( Integer.parseInt( valueSource.getContent().trim() ) );
 * } catch ( NumberFormatException e ) {
 * setValue( 0 );
 * }
 * 
 * incr = new JButton( "+" );
 * incr.addActionListener( new ActionListener() {
 * 
 * @Override
 * public void actionPerformed( ActionEvent e ) {
 * setValue( getValue() + step );
 * }
 * } );
 * 
 * decr = new JButton( "-" );
 * decr.addActionListener( new ActionListener() {
 * 
 * @Override
 * public void actionPerformed( ActionEvent e ) {
 * setValue( getValue() - step );
 * }
 * } );
 * 
 * label = new JLabel( name );
 * 
 * setLayout( new BorderLayout( Constants.HV_GAP, 0 ) );
 * add( label, BorderLayout.WEST );
 * add( SwingUtils.createGridJPanel( 1, 2, valueText, SwingUtils.createGridJPanel( 1, 2, incr, decr ) ),
 * BorderLayout.CENTER );
 * }
 * 
 * public StatusSpinnerItem( String name, String path ) {
 * this( name, path, 1, 0, 100 );
 * }
 * 
 * public int getValue() {
 * return value;
 * }
 * 
 * public void setValue( int val ) {
 * this.value = MathUtils.clamp_i( val, min, max );
 * valueText.setText( String.valueOf( value ) );
 * valueSource.setContent( String.valueOf( value ) );
 * }
 * 
 * public int getStep() {
 * return step;
 * }
 * 
 * public void setStep( int step ) {
 * this.step = step;
 * }
 * 
 * public int getMin() {
 * return min;
 * }
 * 
 * public void setMin( int min ) {
 * this.min = min;
 * }
 * 
 * public int getMax() {
 * return max;
 * }
 * 
 * public void setMax( int max ) {
 * this.max = max;
 * }
 * }
 */
