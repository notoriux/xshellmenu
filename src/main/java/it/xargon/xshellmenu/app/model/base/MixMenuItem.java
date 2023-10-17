package it.xargon.xshellmenu.app.model.base;

import java.util.ArrayList;

import javax.swing.Icon;

import it.xargon.xshellmenu.XSMenuItem;
import it.xargon.xshellmenu.app.res.Resources;

public class MixMenuItem implements XSMenuItem {
	private String label;
	
	private ArrayList<XSMenuItem> children = new ArrayList<>();
	
	private final static SeparatorMenuItem SEPARATOR_ITEM = new SeparatorMenuItem();  
	
	public MixMenuItem(String label) {
		this.label = label;
	}
	
	public MixMenuItem separator() {
		children.add(SEPARATOR_ITEM);
		return this;
	}
	
	public MixMenuItem item(XSMenuItem source) {
		children.add(source);
		return this;
	}
	
	public MixMenuItem children(XSMenuItem source, int menuId) {
		int cnt = source.countChildren(menuId);
		for(int i = 0; i < cnt ; i++) {
			children.add(source.getChild(menuId, i));
		}
		return this;
	}

	@Override
	public Type getType() {
		return Type.ITEM;
	}
	
	@Override
	public String getLabel() {return label;}

	@Override
	public Icon getIcon(Runnable iconReadyListener) {return Resources.genericIcon;}

	@Override
	public Runnable getAction() {return null;}

	@Override
	public int countChildren(int menuId) {
		if (menuId == XSMenuItem.PRIMARY_MENU) return children.size();
		return 0;
	}

	@Override
	public XSMenuItem getChild(int menuId, int index) {
		if (menuId == XSMenuItem.PRIMARY_MENU) return children.get(index);
		return null;
	}
	
	@Override
	public boolean isEnabled() {return true;}
	
	@Override
	public String getTooltip() {return null;}
}
