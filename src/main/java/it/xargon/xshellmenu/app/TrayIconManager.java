package it.xargon.xshellmenu.app;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.event.MouseInputAdapter;

import it.xargon.xshellmenu.XSMenuItem;
import it.xargon.xshellmenu.XSMenuRootProvider;
import it.xargon.xshellmenu.XShellMenuMainClass;
import it.xargon.xshellmenu.app.gui.XSPopupMenu;
import it.xargon.xshellmenu.app.misc.Utils;
import it.xargon.xshellmenu.app.model.base.InMemoryMenuItem;
import it.xargon.xshellmenu.app.model.base.MixMenuItem;
import it.xargon.xshellmenu.app.res.Resources;

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
			Utils.abortApplication("Error while fetching root menu", new IllegalStateException("No root provider found"));
		}
		
		String provName = args[0];
		provArgs = new String[args.length-1];
		System.arraycopy(args, 1, provArgs, 0, provArgs.length);

		this.menuProvider = rootProviders.get(provName);
		this.systemTray = SystemTray.getSystemTray();
		
		this.trayIcon = new TrayIcon(Resources.appIconImage, "XShellMenu 0.0.5 - " + this.menuProvider.getName() + " - " + provArgs[0]);
		this.trayIcon.setImageAutoSize(true);
		this.trayIcon.addMouseListener(mouseInputHandler);
	}
	
	private XSPopupMenu generateRootMenu() {
		XSMenuItem rootItem = null;
		
		try {
			rootItem = menuProvider.getRootItem(provArgs);
		} catch (IllegalArgumentException ex) {
			Utils.abortApplication("Error while fetching root menu", ex);
		}
		
		MixMenuItem rootMenu = new MixMenuItem(rootItem.getLabel());
		
		int auxCnt = rootItem.countChildren(XSMenuItem.AUXILARY_MENU);
		if (auxCnt > 0) { //Brings all the aux items as header in the root menu
			for (int i = 0; i < auxCnt; i++)
				rootMenu = rootMenu.item(rootItem.getChild(XSMenuItem.AUXILARY_MENU, i));
			rootMenu = rootMenu.separator();
		}
		
		rootMenu = rootMenu.children(rootItem, XSMenuItem.PRIMARY_MENU)
				.separator()
				.item(new InMemoryMenuItem("Quit", Resources.quitIcon, "Closes XShellMenu", this::closeTay));
		
		XSPopupMenu popupMenu = new XSPopupMenu(null, rootMenu, XSMenuItem.PRIMARY_MENU);
		return popupMenu;
	}
	
	private void closeTay() {
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
			Utils.abortApplication("Unexpected exception!", e);
			return;
		}
	}
}
