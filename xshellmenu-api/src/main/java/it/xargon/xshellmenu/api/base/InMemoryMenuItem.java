package it.xargon.xshellmenu.api.base;

import java.util.ArrayList;

import javax.swing.Icon;

import it.xargon.xshellmenu.api.XSMenuItem;

public class InMemoryMenuItem implements XSMenuItem {
	private String label;
	private String tooltip;
	private Icon icon;
	private Runnable action;
	
	private ArrayList<XSMenuItem> children;
	
	public InMemoryMenuItem(String label, Icon icon) {
		this(label, icon, null, null);
	}
	
	public InMemoryMenuItem(String label, Icon icon, String tooltip, Runnable action) {
		this.label = label;
		this.icon = icon;
		this.tooltip = tooltip;
		this.action = action;
		this.children = new ArrayList<>();
	}
	
	public InMemoryMenuItem addChild(String label, Icon icon) {
		return addChild(label, icon, null, null);
	}
	
	public InMemoryMenuItem addChild(String label, Icon icon, String tooltip, Runnable action) {
		InMemoryMenuItem result = new InMemoryMenuItem(label, icon, tooltip, action);
		children.add(result);
		return result;
	}
	
	public void addSeparator() {
		children.add(new SeparatorMenuItem());
	}

	@Override
	public String getLabel() {return label;}

	@Override
	public Icon getIcon() {return icon;}
	
	@Override
	public Runnable getAction() {return action;}
	
	@Override
	public int countChildren(MenuType menuType) {
		if (MenuType.PRIMARY.equals(menuType)) return children.size();
		return 0;
	}

	@Override
	public XSMenuItem getChild(MenuType menuType, int index) {
		if (MenuType.PRIMARY.equals(menuType)) return children.get(index);
		return null;
	}
	
	@Override
	public boolean isEnabled() {return true;}
	
	@Override
	public String getTooltip() {return tooltip;}
}
