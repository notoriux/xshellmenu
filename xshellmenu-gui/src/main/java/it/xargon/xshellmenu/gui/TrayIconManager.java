package it.xargon.xshellmenu.gui;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.event.MouseInputAdapter;

import it.xargon.xshellmenu.api.XSMenuItem;
import it.xargon.xshellmenu.api.XSMenuRootProvider;
import it.xargon.xshellmenu.XShellMenuMainClass;
import it.xargon.xshellmenu.res.Resources;
import it.xargon.xshellmenu.api.base.InMemoryMenuItem;
import it.xargon.xshellmenu.api.base.MixMenuItem;

public class TrayIconManager {
	private XSMenuRootProvider menuProvider;
	private String[] provArgs;

	private SystemTray systemTray;
	private TrayIcon trayIcon;
	
	private XSPopupMenu currentMenu;

	private MouseInputAdapter mouseInputHandler = new MouseInputAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) showMenu(e);
		}
	};
		
	public WindowAdapter popupWindowEventsHandler = new WindowAdapter() {
		public void windowClosed(WindowEvent e) {
			if (currentMenu != null && e.getSource() == currentMenu) {
				currentMenu = null;
			}
		}
	};

	public TrayIconManager(String[] args) {
		Map<String, XSMenuRootProvider> rootProviders = XSMenuRootProvider.getInstances();

		if (rootProviders.isEmpty()) {
			Resources.xsGuiPlatform.abortApplication("Error while fetching root menu", new IllegalStateException("No root provider found"));
		}
		
		String provName = args[0];
		provArgs = new String[args.length-1];
		System.arraycopy(args, 1, provArgs, 0, provArgs.length);

		this.menuProvider = rootProviders.get(provName);
		
		if (this.menuProvider == null) {
			Resources.xsGuiPlatform.abortApplication("Error while fetching root menu", new IllegalStateException("Provider \"" + provName + "\" not found"));
		}
		
		this.systemTray = SystemTray.getSystemTray();
		
		this.trayIcon = new TrayIcon(Resources.appIconImage, "XShellMenu 0.0.6 - " + this.menuProvider.getName() + " - " + provArgs[0]);
		this.trayIcon.setImageAutoSize(true);
		this.trayIcon.addMouseListener(mouseInputHandler);
	}
	
	private XSPopupMenu generateRootMenu() {
		XSMenuItem rootItem = null;
		
		try {
			rootItem = menuProvider.getRootItem(Resources.xsGuiPlatform, provArgs);
		} catch (IllegalArgumentException ex) {
			Resources.xsGuiPlatform.abortApplication("Error while fetching root menu", ex);
		}
		
		MixMenuItem rootMenu = new MixMenuItem(rootItem.getLabel(), Resources.appIcon);
		
		int auxCnt = rootItem.countChildren(XSMenuItem.MenuType.AUXILIARY);
		if (auxCnt > 0) { //Brings all the aux items as header in the root menu
			for (int i = 0; i < auxCnt; i++)
				rootMenu = rootMenu.item(rootItem.getChild(XSMenuItem.MenuType.AUXILIARY, i));
			rootMenu = rootMenu.separator();
		}
		
		rootMenu = rootMenu.children(XSMenuItem.MenuType.PRIMARY, rootItem)
				.separator()
				.item(new InMemoryMenuItem("Quit", Resources.quitIcon, "Closes XShellMenu", this::closeTray));
		
		XSPopupMenu popupMenu = new XSPopupMenu(null, rootMenu, XSMenuItem.MenuType.PRIMARY);
		return popupMenu;
	}
	
	private void closeTray() {
		systemTray.remove(trayIcon);
		XShellMenuMainClass.exitApplication(0);
	}
	
	private void showMenu(MouseEvent e) {
		if (currentMenu != null) return;
		currentMenu = generateRootMenu();
		currentMenu.addWindowListener(popupWindowEventsHandler);
		currentMenu.show(e.getX(), e.getY());
	}
	
	public void go() {
		try {
			systemTray.add(trayIcon);
		} catch (AWTException e) {
			Resources.xsGuiPlatform.abortApplication("Unexpected exception!", e);
			return;
		}
	}
}
