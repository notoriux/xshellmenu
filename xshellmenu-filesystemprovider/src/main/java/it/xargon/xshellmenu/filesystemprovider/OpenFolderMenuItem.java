package it.xargon.xshellmenu.filesystemprovider;


import java.io.IOException;
import java.nio.file.Path;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import it.xargon.xshellmenu.api.XSMenuItem;
import it.xargon.xshellmenu.api.XSPlatform;
import it.xargon.xshellmenu.api.XSPlatformResource;
import it.xargon.xshellmenu.filesystemprovider.res.Resources;

class OpenFolderMenuItem implements XSMenuItem {
	private XSPlatform pf;
	
	private String label;
	private Path folderItemPath;
	private FileSystemView fsv = FileSystemView.getFileSystemView();
	
	public OpenFolderMenuItem(XSPlatform pf, Path folderItemPath) {
		this.pf = pf;
		this.folderItemPath = folderItemPath;	
		label = "Open folder " + fsv.getSystemDisplayName(folderItemPath.toFile());
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public Icon getIcon() {
		return Resources.folderOpenIcon;
	}

	@Override
	public Runnable getAction() {
		return () -> {		
			pf.getPlatformResource(XSPlatformResource.TASK_SCHEDULER).execute(() -> {
				try {
					java.awt.Desktop.getDesktop().open(folderItemPath.toFile());
				} catch (IOException e) {
					e.printStackTrace();
					pf.showErrorMessage("Error while opening " + folderItemPath.toString() + "\n\n" + e.getMessage(), false);
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
