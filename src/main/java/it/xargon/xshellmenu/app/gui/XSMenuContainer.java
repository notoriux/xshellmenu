package it.xargon.xshellmenu.app.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JSeparator;

public class XSMenuContainer extends JPanel {
	private static final long serialVersionUID = -7748555866723605523L;
	
	private int itemCount = 0;

	public XSMenuContainer() {
		setLayout(new GridBagLayout());
	}
	
	public void addMenuComponent(XSMenuItemComponent menuComponent, boolean lastElement) {
		GridBagConstraints gbc_menuItemComponent = new GridBagConstraints();
		if (!lastElement) gbc_menuItemComponent.insets = new Insets(0, 0, 5, 0);
		gbc_menuItemComponent.anchor = GridBagConstraints.NORTH;
		gbc_menuItemComponent.weightx = 1.0;
		gbc_menuItemComponent.fill = GridBagConstraints.HORIZONTAL;
		gbc_menuItemComponent.gridx = 0;
		gbc_menuItemComponent.gridy = itemCount;
		add(menuComponent, gbc_menuItemComponent);
		
		itemCount++;
	}
	
	public void addSeparator() {
		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.insets = new Insets(0, 0, 5, 0);
		gbc_separator.anchor = GridBagConstraints.NORTH;
		gbc_separator.weightx = 1.0;
		gbc_separator.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator.gridx = 0;
		gbc_separator.gridy = itemCount;
		add(separator, gbc_separator);
		
		itemCount++;
	}
}
