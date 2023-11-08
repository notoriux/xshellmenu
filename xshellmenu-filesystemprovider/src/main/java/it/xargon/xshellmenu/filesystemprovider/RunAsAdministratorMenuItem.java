package it.xargon.xshellmenu.filesystemprovider;


import java.io.IOException;
import java.nio.file.Path;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import it.xargon.xshellmenu.api.XSMenuItem;
import it.xargon.xshellmenu.api.XSPlatform;
import it.xargon.xshellmenu.api.XSPlatformResource;
import it.xargon.xshellmenu.filesystemprovider.res.Resources;

class RunAsAdministratorMenuItem implements XSMenuItem {
	private XSPlatform pf;
	
	private String label;
	private Path fileItemPath;
	private FileSystemView fsv = FileSystemView.getFileSystemView();
	
	public RunAsAdministratorMenuItem(XSPlatform pf, Path fileItemPath) {
		this.pf = pf;
		this.fileItemPath = fileItemPath;	
		label = "Run " + fsv.getSystemDisplayName(fileItemPath.toFile()) + " as Administrator";
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public Icon getIcon() {
		return Resources.runAsAdminIcon;
	}

	@Override
	public Runnable getAction() {
		return () -> {
			pf.getPlatformResource(XSPlatformResource.TASK_SCHEDULER).execute(() -> {
				try {
					Runtime rt = Runtime.getRuntime();
					String[] cmdSpec = {"powershell", "Start-Process", "-verb", "RunAs", "-WorkingDirectory", "'" + fileItemPath.getParent().toString() + "'", "-FilePath", "'" + fileItemPath.getFileName() + "'"};
					rt.exec(cmdSpec);
				} catch (IOException e) {
					e.printStackTrace();
					pf.showErrorMessage("Error running " + fileItemPath.toString() + " as Administrator\n\n" + e.getMessage(), false);
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
