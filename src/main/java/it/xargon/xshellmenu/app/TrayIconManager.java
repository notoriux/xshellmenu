package it.xargon.xshellmenu.app;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.event.MouseInputAdapter;

import it.xargon.xshellmenu.XShellMenuMainClass;
import it.xargon.xshellmenu.app.gui.XSPopupMenu;
import it.xargon.xshellmenu.app.misc.Utils;
import it.xargon.xshellmenu.app.model.XSMenuItem;
import it.xargon.xshellmenu.app.model.XSMenuRootProvider;
import it.xargon.xshellmenu.app.model.base.InMemoryMenuItem;
import it.xargon.xshellmenu.app.model.base.MixMenuItem;
import it.xargon.xshellmenu.app.model.filesystem.FileSystemRootProvider;
import it.xargon.xshellmenu.app.res.Resources;

public class TrayIconManager {
	private String rootPath;
	private SystemTray systemTray;
	private TrayIcon trayIcon;
	
	private XSPopupMenu currentMenu;
	
	private XSMenuRootProvider fsProvider;
	
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

	public TrayIconManager(String rootPath) {
		this.fsProvider = new FileSystemRootProvider();
		this.rootPath = rootPath;
		this.systemTray = SystemTray.getSystemTray();
		
		this.trayIcon = new TrayIcon(Resources.appIconImage, "XShellMenu 0.0.4 - " + rootPath);
		this.trayIcon.setImageAutoSize(true);
		this.trayIcon.addMouseListener(mouseInputHandler);
	}
	
	private XSPopupMenu generateRootMenu() {
		XSMenuItem fsRootItem = fsProvider.getRootItem(rootPath);
		
		MixMenuItem rootMenu = new MixMenuItem("(root)")
				.item(fsRootItem.getChild(MouseEvent.BUTTON3, 0)) //first item of a FS folder aux menu is always an "Open folder" action
				.separator()
				.children(fsRootItem, XSMenuItem.PRIMARY_MENU)
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
