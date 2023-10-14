package it.xargon.xshellmenu.app.misc;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import it.xargon.xshellmenu.XShellMenuMainClass;
import it.xargon.xshellmenu.app.model.InMemoryMenuItem;

public class Utils {
	private Utils() {}
	
	public static void showErrorMessage(String errorMessage, boolean wait) {
		Runnable showTask = () -> {
			JOptionPane.showMessageDialog(
					null,
					errorMessage,
					"XShellMenu",
					JOptionPane.ERROR_MESSAGE);
		};
		
		if (wait) {
			if (SwingUtilities.isEventDispatchThread()) {
				showTask.run();
			} else {
				try {
					SwingUtilities.invokeAndWait(showTask);
				} catch (InvocationTargetException | InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}
		else SwingUtilities.invokeLater(showTask);
	}
	
	
	
	public static void abortApplication(String errorMessage, Exception ex) {
		ex.printStackTrace();
		String abortMessage = errorMessage + "\n\n" + ex.getClass().getName() + ": " + ex.getMessage();
		showErrorMessage(abortMessage, true);
		XShellMenuMainClass.exitApplication(255);
	}
	
	public static InMemoryMenuItem buildSampleData() {
		InMemoryMenuItem rootItem = new InMemoryMenuItem("(root)");
		
		rootItem.addChild("Item 1");
		rootItem.addChild("Item 2");
		InMemoryMenuItem subMenuItem1 = rootItem.addChild("Submenu 3");
		subMenuItem1.addChild("Item 3.1");
		subMenuItem1.addChild("Item 3.2 with long label");
		rootItem.addChild("Item 4");
		rootItem.addSeparator();
		InMemoryMenuItem subMenuItem2 = rootItem.addChild("Submenu 5 with long label");
		subMenuItem2.addChild("Item 5.1");
		subMenuItem2.addChild("Item 5.2 with very very long label");
		
		for(int i = 6 ; i <= 40 ; i++) {
			rootItem.addChild("Item " + i);
		}
		
		return rootItem;
	}
	
	public enum OperatingSystem { WINDOWS, MACOS, LINUX, SOLARIS, NONE }
	
    public static final OperatingSystem getOperatingSystem() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("win") >= 0) {
            return OperatingSystem.WINDOWS;
        } else if (os.indexOf("mac") >= 0) {
            return OperatingSystem.MACOS;
        } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
            return OperatingSystem.LINUX;
        } else if (os.indexOf("sunos") >= 0) {
            return OperatingSystem.SOLARIS;
        } else {
            return OperatingSystem.NONE;
        }
    }

}
