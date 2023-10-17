package it.xargon.xshellmenu.res;

import java.awt.Image;
import java.awt.image.BaseMultiResolutionImage;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import it.xargon.xshellmenu.misc.ThemeDetector;

public class Resources {
	private Resources() {}
	
	public final static BaseMultiResolutionImage appIconImage;
	public final static ImageIcon appIcon;
	public final static ImageIcon quitIcon;
	public final static ImageIcon genericIcon;
	public final static ImageIcon expandIcon;
	public final static ImageIcon folderOpenIcon;
	public final static ImageIcon terminalIcon;
	public final static ImageIcon runAsAdminIcon;
	public final static ImageIcon banIcon;
	
	public final static ScheduledExecutorService internalTaskScheduler;
	public final static ExecutorService iconFetcherScheduler;
	
	static {
		try {
			String prefix = ThemeDetector.isDarkMode() ? "dark" : "light";
			
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
			folderOpenIcon = new ImageIcon(ImageIO.read(Resources.class.getResource("folder-open-icon.png")));
			terminalIcon = new ImageIcon(ImageIO.read(Resources.class.getResource("terminal.png")));
			runAsAdminIcon = new ImageIcon(ImageIO.read(Resources.class.getResource("run-as-admin.png")));
			banIcon = new ImageIcon(ImageIO.read(Resources.class.getResource("ban.png")));
		} catch (IOException ex) {
			throw new IllegalStateException("Unable to initialize resources", ex);
		}
		
		internalTaskScheduler = Executors.newSingleThreadScheduledExecutor();
		iconFetcherScheduler = Executors.newWorkStealingPool();
	}
}
