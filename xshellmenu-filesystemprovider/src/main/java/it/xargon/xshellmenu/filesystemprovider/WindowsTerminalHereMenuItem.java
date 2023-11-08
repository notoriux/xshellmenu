package it.xargon.xshellmenu.filesystemprovider;

import java.io.IOException;
import java.nio.file.Path;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import it.xargon.xshellmenu.api.XSMenuItem;
import it.xargon.xshellmenu.api.XSPlatform;
import it.xargon.xshellmenu.api.XSPlatformResource;
import it.xargon.xshellmenu.filesystemprovider.res.Resources;

class WindowsTerminalHereMenuItem implements XSMenuItem {
	private XSPlatform pf;
	
	private String label;
	private Path folderItemPath;
	private FileSystemView fsv = FileSystemView.getFileSystemView();
	
	public WindowsTerminalHereMenuItem(XSPlatform pf, Path folderItemPath) {
		this.pf = pf;
		this.folderItemPath = folderItemPath;	
		label = "Open Windows Terminal in " + fsv.getSystemDisplayName(folderItemPath.toFile());
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public Icon getIcon() {
		return Resources.terminalIcon;
	}

	@Override
	public Runnable getAction() {
		return () -> {		
			pf.getPlatformResource(XSPlatformResource.TASK_SCHEDULER).execute(() -> {
				try {
					Runtime rt = Runtime.getRuntime();
					String[] cmdSpec = {"wt","-d",folderItemPath.toString()};
					rt.exec(cmdSpec, null, folderItemPath.toFile());
				} catch (IOException e) {
					e.printStackTrace();
					pf.showErrorMessage("Error starting Windows Terminal in " + folderItemPath.toString() + "\n\n" + e.getMessage(), false);
				}
			});
		};
	}
	
	@Override
	public int countChildren(MenuType menuType) {
		return 0;
	}

	@Override
	public XSMenuItem getChild(MenuType menuType, int index) {
		return null;
	}
	
	@Override
	public boolean isEnabled() {return true;}
	
	@Override
	public String getTooltip() {return null;}
}
