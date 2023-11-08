package it.xargon.xshellmenu.filesystemprovider.res;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class Resources {
	private Resources() {}
	
	public final static ImageIcon terminalIcon;
	public final static ImageIcon runAsAdminIcon;
	public final static ImageIcon folderOpenIcon;
	public final static ImageIcon banIcon;
	
	static {
		try {
			terminalIcon = new ImageIcon(ImageIO.read(Resources.class.getResource("terminal.png")));
			runAsAdminIcon = new ImageIcon(ImageIO.read(Resources.class.getResource("run-as-admin.png")));
			folderOpenIcon = new ImageIcon(ImageIO.read(Resources.class.getResource("folder-open-icon.png")));
			banIcon = new ImageIcon(ImageIO.read(Resources.class.getResource("ban.png")));
		} catch (IOException ex) {
			throw new IllegalStateException("Unable to initialize resources", ex);
		}
	}

}
