package it.xargon.xshellmenu;

import java.awt.SystemTray;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import it.xargon.xshellmenu.app.TrayIconManager;
import it.xargon.xshellmenu.app.misc.Utils;

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

		if (args.length == 0) {
			Utils.showErrorMessage("Please provide a starting directory as first argument", true);
			exitApplication(2);
			return;			
		}
		
		Path basePath = Paths.get(args[0]).normalize().toAbsolutePath();
		
		if (!Files.exists(basePath) || !Files.isDirectory(basePath)) {			
			Utils.showErrorMessage("\"" + basePath.toString() + "\" does not exist or it's not a directory on the local filesystem", true);
			exitApplication(3);
			return;			
		}

		menuManager = new TrayIconManager(basePath);
		initialized = true;
	}
	
	private void startGui() {
		if (initialized) menuManager.go();
	}
}
