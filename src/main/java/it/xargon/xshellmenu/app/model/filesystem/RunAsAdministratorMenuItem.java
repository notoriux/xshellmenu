package it.xargon.xshellmenu.app.model.filesystem;

import java.io.IOException;
import java.nio.file.Path;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import it.xargon.xshellmenu.app.misc.Utils;
import it.xargon.xshellmenu.app.model.XSMenuItem;
import it.xargon.xshellmenu.app.res.Resources;

class RunAsAdministratorMenuItem implements XSMenuItem {
	private String label;
	
	private Path fileItemPath;
	
	private FileSystemView fsv = FileSystemView.getFileSystemView();
	
	public RunAsAdministratorMenuItem(Path fileItemPath) {
		this.fileItemPath = fileItemPath;	
		label = "Run " + fsv.getSystemDisplayName(fileItemPath.toFile()) + " as Administrator";
	}

	@Override
	public Type getType() {
		return Type.ITEM;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public Icon getIcon(Runnable iconReadyListener) {
		return Resources.runAsAdminIcon;
	}

	@Override
	public Runnable getAction() {
		return () -> {
			Resources.internalTaskScheduler.execute(() -> {
				try {
					Runtime rt = Runtime.getRuntime();
					String[] cmdSpec = {"powershell", "Start-Process", "-verb", "RunAs", "-WorkingDirectory", "'" + fileItemPath.getParent().toString() + "'", "-FilePath", "'" + fileItemPath.getFileName() + "'"};
					rt.exec(cmdSpec);
				} catch (IOException e) {
					e.printStackTrace();
					Utils.showErrorMessage("Error running " + fileItemPath.toString() + " as Administrator\n\n" + e.getMessage(), false);
				}
			});
		};
	}
	
	@Override
	public int countChildren(int menuId) {
		return 0;
	}

	@Override
	public XSMenuItem getChild(int menuId, int index) {
		return null;
	}
	
	@Override
	public boolean isEnabled() {return true;}
	
	@Override
	public String getTooltip() {return null;}
}
