package it.xargon.xshellmenu;

import java.awt.SystemTray;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import it.xargon.xshellmenu.gui.TrayIconManager;
import it.xargon.xshellmenu.misc.Utils;

public class XShellMenuMainClass {
	private static Object exitLock = new Object();
	private static volatile AtomicInteger exitCodeRef = null;
	
	private TrayIconManager menuManager = null;
	private boolean initialized = false;
	
	public static void main(String[] args) throws Exception {
		SwingUtilities.invokeLater(() -> {
			new XShellMenuMainClass(args).startGui();
		});
		
		synchronized (exitLock) {
			while (exitCodeRef == null) {
				exitLock.wait();
			}
		}
		
		System.exit(exitCodeRef.get());
	}
	
	public static void exitApplication(int exitCode) {
		synchronized (exitLock) {
			exitCodeRef = new AtomicInteger(exitCode);
			exitLock.notify();
		}
	}	
	
	private XShellMenuMainClass(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			Utils.abortApplication("Unable to start XShellMenu: error while setting LookAndFeel", e);
			return;
		}
		
		if (!SystemTray.isSupported()) {
			Utils.showErrorMessage("System tray area is not supported on this platform", true);
			exitApplication(1);
			return;
		}

		if (args.length < 2) {
			Utils.showErrorMessage("Need a menu provider name and an initalization string", true);
			exitApplication(2);
			return;			
		}

		menuManager = new TrayIconManager(args);
		initialized = true;
	}
	
	private void startGui() {
		if (initialized) menuManager.go();
	}
}
