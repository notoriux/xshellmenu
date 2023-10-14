package it.xargon.xshellmenu.app.model.filesystem;

import java.io.IOException;
import java.nio.file.Path;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import it.xargon.xshellmenu.app.misc.Utils;
import it.xargon.xshellmenu.app.model.XSMenuItem;
import it.xargon.xshellmenu.app.res.Resources;

public class OpenFolderMenuItem implements XSMenuItem {
	private String label;
	
	private Path folderItemPath;
	
	private FileSystemView fsv = FileSystemView.getFileSystemView();
	
	public OpenFolderMenuItem(Path folderItemPath) {
		this.folderItemPath = folderItemPath;	
		label = "Open folder " + fsv.getSystemDisplayName(folderItemPath.toFile());
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
		return Resources.folderOpenIcon;
	}

	@Override
	public Runnable getAction() {
		return () -> {		
			Resources.internalTaskScheduler.execute(() -> {
				try {
					java.awt.Desktop.getDesktop().open(folderItemPath.toFile());
				} catch (IOException e) {
					e.printStackTrace();
					Utils.showErrorMessage("Error while opening " + folderItemPath.toString() + "\n\n" + e.getMessage(), false);
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
