package it.xargon.xshellmenu.app;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;

import javax.swing.event.MouseInputAdapter;

import it.xargon.xshellmenu.XShellMenuMainClass;
import it.xargon.xshellmenu.app.gui.XSPopupMenu;
import it.xargon.xshellmenu.app.misc.Utils;
import it.xargon.xshellmenu.app.model.InMemoryMenuItem;
import it.xargon.xshellmenu.app.model.MixMenuItem;
import it.xargon.xshellmenu.app.model.XSMenuItem;
import it.xargon.xshellmenu.app.model.filesystem.FileSystemMenuItem;
import it.xargon.xshellmenu.app.model.filesystem.OpenFolderMenuItem;
import it.xargon.xshellmenu.app.res.Resources;

public class TrayIconManager {
	private Path rootPath;
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

	public TrayIconManager(Path rootPath) {
		this.rootPath = rootPath;
		this.systemTray = SystemTray.getSystemTray();
		
		this.trayIcon = new TrayIcon(Resources.appIconImage, "XShellMenu 0.3 - " + rootPath.toString());
		this.trayIcon.setImageAutoSize(true);
		this.trayIcon.addMouseListener(mouseInputHandler);
	}
	
	private XSPopupMenu generateRootMenu() {
		MixMenuItem rootMenu = new MixMenuItem("(root)")
				.item(new OpenFolderMenuItem(rootPath))
				.separator()
				.children(new FileSystemMenuItem(rootPath), XSMenuItem.PRIMARY_MENU)
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
