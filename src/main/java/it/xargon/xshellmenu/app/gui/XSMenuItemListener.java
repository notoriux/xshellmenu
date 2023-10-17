package it.xargon.xshellmenu.app.gui;

import it.xargon.xshellmenu.XSMenuItem;

public interface XSMenuItemListener {
	public void mouseEntered(XSMenuItem item);
	
	public void mouseExited(XSMenuItem item);
	
	public void mouseActionClicked(XSMenuItem item);
	
	public void mouseAuxClicked(XSMenuItem item, int menuIndex);
}
