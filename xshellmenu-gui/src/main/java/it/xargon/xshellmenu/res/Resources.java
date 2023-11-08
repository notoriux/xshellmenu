package it.xargon.xshellmenu.res;

import java.awt.Image;
import java.awt.image.BaseMultiResolutionImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import it.xargon.xshellmenu.api.XSPlatform;
import it.xargon.xshellmenu.misc.XSGuiPlatform;

public class Resources {
	private Resources() {}
	
	public final static XSPlatform xsGuiPlatform;
	
	public final static BaseMultiResolutionImage appIconImage;
	public final static ImageIcon appIcon;
	public final static ImageIcon quitIcon;
	public final static ImageIcon genericIcon;
	public final static ImageIcon expandIcon;
	
	static {
		xsGuiPlatform = new XSGuiPlatform();
		
		try {
			String prefix = xsGuiPlatform.isDarkModeSystemTheme() ? "dark" : "light";
			
			Image[] allSizesAppIcon = new Image[] {
					ImageIO.read(Resources.class.getResource(prefix + "-app-icon-16.png")),
					ImageIO.read(Resources.class.getResource(prefix + "-app-icon-32.png")),
					ImageIO.read(Resources.class.getResource(prefix + "-app-icon-48.png")),
					ImageIO.read(Resources.class.getResource(prefix + "-app-icon-64.png")),
					ImageIO.read(Resources.class.getResource(prefix + "-app-icon-128.png")),
					ImageIO.read(Resources.class.getResource(prefix + "-app-icon-256.png")),
					ImageIO.read(Resources.class.getResource(prefix + "-app-icon-512.png"))					
				};
				
			appIconImage = new BaseMultiResolutionImage(0, allSizesAppIcon);
			appIcon = new ImageIcon(appIconImage);
			
			genericIcon = new ImageIcon(ImageIO.read(Resources.class.getResource("generic-icon.png")));
			expandIcon = new ImageIcon(ImageIO.read(Resources.class.getResource("expand-icon.png")));
			quitIcon = new ImageIcon(ImageIO.read(Resources.class.getResource("quit-icon.png")));
		} catch (IOException ex) {
			throw new IllegalStateException("Unable to initialize resources", ex);
		}
	}
}
