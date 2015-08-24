package com.bwat.hmi.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.bwat.hmi.Constants;
import com.bwat.hmi.util.MathUtils;
import com.bwat.hmi.util.SwingUtils;

/**
 * A JPanel with multiple pages and navigation (prev/next) butttons
 * 
 * @author Kareem El-Faramawi
 */
public class PagedJPanel extends JPanel {
	private CardLayout layout = new CardLayout( Constants.HV_GAP, Constants.HV_GAP );
	private ArrayList<JPanel> pages = new ArrayList<JPanel>();
	
	private static final String PAGE_NUM_FORMAT = "Page %d/%d";
	private int currentPage = 0;
	private JPanel pageDisplay;
	private JButton prev = new JButton( "Previous" );
	private JButton next = new JButton( "Next" );
	private JLabel pageNumber = new JLabel( "", JLabel.CENTER );
	
	public PagedJPanel() {
		setLayout( new BorderLayout( Constants.HV_GAP, Constants.HV_GAP ) );
		JPanel mainPanel = new JPanel( new GridBagLayout() );
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 9;
		c.weightx = 1;
		
		pageDisplay = new JPanel( layout );
		mainPanel.add( pageDisplay, c );
		
		prev.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent arg0 ) {
				goToPage( currentPage - 1 );
			}
		} );
		
		next.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent arg0 ) {
				goToPage( currentPage + 1 );
			}
		} );
		
		c.gridy = 1;
		c.weighty = 1;
		JPanel controls = new JPanel( new BorderLayout( Constants.HV_GAP, Constants.HV_GAP ) );
		controls.add( pageNumber, BorderLayout.WEST );
		controls.add( SwingUtils.createGridJPanel( 1, 2, Constants.HV_GAP, 0, prev, next ), BorderLayout.CENTER );
		mainPanel.add( controls, c );
		add( mainPanel, BorderLayout.CENTER );
		goToPage( 0 );
	}
	
	public void addPage( JPanel page ) {
		pageDisplay.add( page, getPageName( pages.size() ) );
		pages.add( page );
		goToPage( 0 );
	}
	
	public void goToPage( int page ) {
		if ( MathUtils.inRange_in_ex( page, 0, pages.size() ) ) {
			currentPage = page;
			layout.show( pageDisplay, getPageName( currentPage ) );
			pageNumber.setText( String.format( PAGE_NUM_FORMAT, currentPage + 1, pages.size() ) );
			prev.setEnabled( currentPage > 0 );
			next.setEnabled( currentPage < pages.size() - 1 );
		}
	}
	
	private String getPageName( int page ) {
		return "Page " + page;
	}
	
}
