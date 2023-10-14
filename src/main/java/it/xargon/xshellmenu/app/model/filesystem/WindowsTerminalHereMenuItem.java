package it.xargon.xshellmenu.app.model.filesystem;

import java.io.IOException;
import java.nio.file.Path;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import it.xargon.xshellmenu.app.misc.Utils;
import it.xargon.xshellmenu.app.model.XSMenuItem;
import it.xargon.xshellmenu.app.res.Resources;

public class WindowsTerminalHereMenuItem implements XSMenuItem {
	private String label;
	
	private Path folderItemPath;
	
	private FileSystemView fsv = FileSystemView.getFileSystemView();
	
	public WindowsTerminalHereMenuItem(Path folderItemPath) {
		this.folderItemPath = folderItemPath;	
		label = "Open Windows Terminal in " + fsv.getSystemDisplayName(folderItemPath.toFile());
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
		return Resources.terminalIcon;
	}

	@Override
	public Runnable getAction() {
		return () -> {		
			Resources.internalTaskScheduler.execute(() -> {
				try {
					Runtime rt = Runtime.getRuntime();
					String[] cmdSpec = {"wt","-d",folderItemPath.toString()};
					rt.exec(cmdSpec, null, folderItemPath.toFile());
				} catch (IOException e) {
					e.printStackTrace();
					Utils.showErrorMessage("Error starting Windows Terminal in " + folderItemPath.toString() + "\n\n" + e.getMessage(), false);
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
