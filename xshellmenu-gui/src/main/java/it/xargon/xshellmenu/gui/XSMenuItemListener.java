package it.xargon.xshellmenu.gui;

import it.xargon.xshellmenu.api.XSMenuItem;

public interface XSMenuItemListener {
	public void mouseEntered(XSMenuItem item);
	
	public void mouseExited(XSMenuItem item);
	
	public void mouseActionClicked(XSMenuItem item, int buttonIndex);
}
