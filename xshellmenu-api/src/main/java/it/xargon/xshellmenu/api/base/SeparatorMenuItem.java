package it.xargon.xshellmenu.api.base;

import javax.swing.Icon;

import it.xargon.xshellmenu.api.XSMenuItem;

public class SeparatorMenuItem implements XSMenuItem {
	@Override
	public boolean isSeparator() {return true;}
	
	@Override
	public String getLabel() {return "(separator)";}

	@Override
	public Icon getIcon() {return null;}
	
	@Override
	public Runnable getAction() {return null;}
	
	@Override
	public int countChildren(MenuType menuType) {return 0;}

	@Override
	public XSMenuItem getChild(MenuType menuType, int index) {throw new UnsupportedOperationException();}
	
	@Override
	public boolean isEnabled() {return true;}
	
	@Override
	public String getTooltip() {return null;}
}
