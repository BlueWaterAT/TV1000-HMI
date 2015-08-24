package com.bwat.hmi.prg;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class PopupMenuAdapter implements PopupMenuListener {
	@Override
	public void popupMenuCanceled( PopupMenuEvent e ) {}
	
	@Override
	public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {}
	
	@Override
	public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {}
}
