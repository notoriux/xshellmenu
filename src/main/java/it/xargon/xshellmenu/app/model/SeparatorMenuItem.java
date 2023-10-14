package it.xargon.xshellmenu.app.model;

import javax.swing.Icon;

public class SeparatorMenuItem implements XSMenuItem {
	@Override
	public Type getType() {return Type.SEPARATOR;}
	
	@Override
	public String getLabel() {return "(separator)";}

	@Override
	public Icon getIcon(Runnable iconReadyListener) {return null;}
	
	@Override
	public Runnable getAction() {return null;}
	
	@Override
	public int countChildren(int menuId) {return 0;}

	@Override
	public XSMenuItem getChild(int menuId, int index) {throw new UnsupportedOperationException();}
	
	@Override
	public boolean isEnabled() {return true;}
	
	@Override
	public String getTooltip() {return null;}
}
