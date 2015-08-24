package com.bwat.hmi.prg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.bwat.hmi.Constants;
import com.bwat.hmi.util.FileUtils;
import com.bwat.hmi.util.SwingUtils;

public class ParameterEditor extends JDialog {
	private JButton cancel = new JButton( "Close" );
	private JButton saveExit = new JButton( "Save & Close" );
	private JTable parameters = new JTable( 100, 2 );
	private String path;
	public static final String PARAM_EXTENSION = ".prm";
	
	public ParameterEditor( int size, Color bg, Color fg ) {
		JPanel mainPanel = new JPanel( new BorderLayout( Constants.HV_GAP, Constants.HV_GAP ) );
		
		
		// Control buttons
		cancel.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				close();
			}
		} );
		saveExit.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				saveParameters();
				close();
			}
		} );
		JPanel controls = SwingUtils.createGridJPanel( 1, 2, Constants.HV_GAP, Constants.HV_GAP, cancel, saveExit );
		SwingUtils.setFont_r( controls, controls.getFont().deriveFont( 18f ).deriveFont( Font.BOLD ) );
		mainPanel.add( controls, BorderLayout.SOUTH );
		
		// Editor
		JPanel editorPanel = new JPanel( new BorderLayout( Constants.HV_GAP, Constants.HV_GAP ) );
		if ( size > 0 ) {
			( (DefaultTableModel) parameters.getModel() ).setRowCount( size );
		}
		( (DefaultTableModel) parameters.getModel() ).setColumnIdentifiers( new String[] { "Data", "Comment" } );
		parameters.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		parameters.getColumnModel().getColumn( 0 ).setMaxWidth( 100 );
		parameters.setBackground( bg );
		parameters.setForeground( fg );
		parameters.getTableHeader().setBackground( bg );
		parameters.getTableHeader().setForeground( fg );
		
		JScrollPane scroll = new JScrollPane( parameters );
		scroll.setRowHeaderView( new RowNumberHeader( parameters, bg, fg ) );
		editorPanel.add( scroll, BorderLayout.CENTER );
		mainPanel.add( editorPanel, BorderLayout.CENTER );
		mainPanel.setBorder( BorderFactory.createEmptyBorder( Constants.HV_GAP, Constants.HV_GAP, Constants.HV_GAP, Constants.HV_GAP ) );
		
		add( mainPanel, BorderLayout.CENTER );
		setTitle( "Parameter Editor" );
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent arg0 ) {
				if ( JOptionPane.showConfirmDialog( null, "Save Parameters?", "Unsaved Data", JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION ) {
					saveParameters();
				}
			};
		} );
		
		SwingUtils.setBG_r( this, bg );
		SwingUtils.setFG_r( this, fg );
		pack();
	}
	
	public void setRowHeight( int h ) {
		parameters.setRowHeight( h );
	}
	
	public void close() {
		setVisible( false );
		dispose();
	}
	
	public void clearAll() {
		for ( int row = 0; row < parameters.getRowCount(); row++ ) {
			for ( int col = 0; col < parameters.getColumnCount(); col++ ) {
				parameters.setValueAt( null, row, col );
			}
		}
	}
	
	public void setPath( String path ) {
		this.path = path;
	}
	
	public void loadParameters() {
		if ( path != null && path.length() > 0 ) {
			File file = FileUtils.getFile( path );
			if ( file.exists() ) {
				clearAll();
				try {
					Scanner scan = new Scanner( file );
					String line;
					while ( scan.hasNext() ) {
						line = scan.nextLine();
						if ( line.length() > 0 ) {
							String[] csv = line.split( "," );
							int row = Integer.parseInt( csv[0] );
							Object data = csv[1].equals( "null" ) ? null : csv[1];
							Object comment = csv[2].equals( "null" ) ? null : csv[2];
							parameters.setValueAt( data, row, 0 );
							parameters.setValueAt( comment, row, 1 );
						}
					}
					scan.close();
				} catch ( FileNotFoundException e ) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void saveParameters() {
		if ( path != null && path.length() > 0 ) {
			try {
				PrintWriter pw = new PrintWriter( new FileOutputStream( FileUtils.getFile( path ) ) );
				for ( int row = 0, rows = parameters.getRowCount(); row < rows; row++ ) {
					Object data = parameters.getValueAt( row, 0 );
					Object comment = parameters.getValueAt( row, 1 );
					if ( data != null || comment != null ) {
						pw.println( row + "," + data + "," + comment );
					}
				}
				pw.flush();
				pw.close();
			} catch ( FileNotFoundException e ) {
				e.printStackTrace();
			}
		}
	}
}
