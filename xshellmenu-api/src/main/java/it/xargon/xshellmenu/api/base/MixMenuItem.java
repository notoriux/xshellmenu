package it.xargon.xshellmenu.api.base;

import java.util.ArrayList;

import javax.swing.Icon;

import it.xargon.xshellmenu.api.XSMenuItem;

public class MixMenuItem implements XSMenuItem {
	private String label;
	private String tooltip;
	private Icon icon;
	
	private ArrayList<XSMenuItem> children = new ArrayList<>();
	
	private final static SeparatorMenuItem SEPARATOR_ITEM = new SeparatorMenuItem();  
	
	public MixMenuItem(String label, Icon icon) {
		this(label, icon, null);
	}
	
	public MixMenuItem(String label, Icon icon, String tooltip) {
		this.label = label;
		this.icon = icon;
		this.tooltip = tooltip;
	}
	
	public MixMenuItem separator() {
		children.add(SEPARATOR_ITEM);
		return this;
	}
	
	public MixMenuItem item(XSMenuItem source) {
		children.add(source);
		return this;
	}
	
	public MixMenuItem children(MenuType menuType, XSMenuItem source) {
		int cnt = source.countChildren(menuType);
		for(int i = 0; i < cnt ; i++) {
			children.add(source.getChild(menuType, i));
		}
		return this;
	}

	@Override
	public String getLabel() {return label;}

	@Override
	public Icon getIcon() {return icon;}

	@Override
	public Runnable getAction() {return null;}

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
