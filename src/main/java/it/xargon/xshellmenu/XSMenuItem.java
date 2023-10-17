package it.xargon.xshellmenu;

import java.awt.event.MouseEvent;

import javax.swing.Icon;

public interface XSMenuItem {
	public enum Type {
		ITEM, SEPARATOR;
	}
	
	public final static int PRIMARY_MENU = 0;
	public final static int AUXILARY_MENU = MouseEvent.BUTTON3;
	
	public Type getType();
	public String getLabel();
	public Icon getIcon(Runnable iconReadyListener);
	public Runnable getAction();
	public int countChildren(int menuId);
	public XSMenuItem getChild(int menuId, int index);
	public boolean isEnabled();
	public String getTooltip();
}
