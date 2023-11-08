package it.xargon.xshellmenu.api;

import javax.swing.Icon;

public interface XSMenuItem {
	public enum MenuType {
		PRIMARY, AUXILIARY;
	}
	
	default boolean isSeparator() {return false;}
	public String getLabel();
	public Icon getIcon();
	public Runnable getAction();
	public int countChildren(MenuType menuType);
	public XSMenuItem getChild(MenuType menuType, int index);
	public boolean isEnabled();
	public String getTooltip();
}
