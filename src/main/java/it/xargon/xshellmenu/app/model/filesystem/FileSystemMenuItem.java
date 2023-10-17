package it.xargon.xshellmenu.app.model.filesystem;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import it.xargon.xshellmenu.XSMenuItem;
import it.xargon.xshellmenu.app.misc.Utils;
import it.xargon.xshellmenu.app.misc.Utils.OperatingSystem;
import it.xargon.xshellmenu.app.res.Resources;

class FileSystemMenuItem implements XSMenuItem {
	private String label;
	private Icon icon;
	private String tooltip = null;
	
	private boolean accessDenied = false;
	
	private Path fileItemPath;
	
	private FileSystemView fsv = FileSystemView.getFileSystemView();
	
	private Object iconLock = new Object();
		
	private Comparator<Path> pathNameComparator = new Comparator<Path>() {
		@Override
		public int compare(Path o1, Path o2) {
			return o1.getFileName().toString().compareTo(o2.getFileName().toString());
		}
	};
	
	private ArrayList<Path> lazyContainedPaths = null;
	private ArrayList<FileSystemMenuItem> lazyContainedItems = null;
	
	private ArrayList<XSMenuItem> auxMenu = null;
	
	public FileSystemMenuItem(Path fileItemPath) {
		this.fileItemPath = fileItemPath;	
		label = fsv.getSystemDisplayName(fileItemPath.toFile());
	}
	
	private ArrayList<XSMenuItem> getAuxMenu() {
		if (auxMenu == null) {
			auxMenu = new ArrayList<XSMenuItem>();
			if (Files.isRegularFile(fileItemPath)) {
				if (Utils.getOperatingSystem().equals(OperatingSystem.WINDOWS)) auxMenu.add(new RunAsAdministratorMenuItem(fileItemPath));
			} else {
				auxMenu.add(new OpenFolderMenuItem(fileItemPath));
				if (Utils.getOperatingSystem().equals(OperatingSystem.WINDOWS)) auxMenu.add(new WindowsTerminalHereMenuItem(fileItemPath));
			}
		}
		return auxMenu;
	}
	
	private ArrayList<Path> getContainedPaths() {
		try {
			if (lazyContainedPaths == null && Files.isDirectory(fileItemPath, LinkOption.NOFOLLOW_LINKS)) {
				ArrayList<Path> containedDirs = new ArrayList<>();
				try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(fileItemPath, Files::isDirectory)) {
					dirStream.forEach(containedDirs::add);
				}
				containedDirs.sort(pathNameComparator);

				ArrayList<Path> containedFiles = new ArrayList<>();
				try (DirectoryStream<Path> fileStream = Files.newDirectoryStream(fileItemPath, Files::isRegularFile)) {
					fileStream.forEach(containedFiles::add);
				}
				containedFiles.sort(pathNameComparator);

				lazyContainedPaths = new ArrayList<>();
				lazyContainedPaths.addAll(containedDirs);
				lazyContainedPaths.addAll(containedFiles);
			}
		} catch (AccessDeniedException ex) {
			accessDenied = true;
			lazyContainedPaths = new ArrayList<>();
			icon = Resources.banIcon;
			tooltip = "Access denied to " + fileItemPath.toString();
		} catch (IOException ex) {
			Utils.abortApplication("Error while inspecting path " + fileItemPath.toString(), ex);
		}
				
		return lazyContainedPaths;
	}
	
	private ArrayList<FileSystemMenuItem> getContainedItems() {
		ArrayList<Path> containedPaths=getContainedPaths();
		
		if (lazyContainedItems == null && containedPaths != null) {
			lazyContainedItems = new ArrayList<>();
			containedPaths.forEach(p -> lazyContainedItems.add(new FileSystemMenuItem(p)));
		}
		
		return lazyContainedItems;
	}

	public Path getFileItemPath() {
		return fileItemPath;
	}
	
	@Override
	public Type getType() {
		return Type.ITEM;
	}
	
	@Override
	public String getLabel() {
		return label;
	}
	
	private Icon loadIconNow() {
		return fsv.getSystemIcon(fileItemPath.toFile(), 16, 16);
	}

	@Override
	public Icon getIcon(Runnable iconReadyListener) {
		Icon resultIcon = Resources.genericIcon;
		
		synchronized (iconLock) {
			if (icon == null) {
				if (iconReadyListener == null) {
					icon = loadIconNow();
				} else  {				
					Resources.iconFetcherScheduler.execute(() -> {
						Icon loadedIcon = loadIconNow();
						synchronized (iconLock) {icon = loadedIcon;}
						iconReadyListener.run();
					});
				}
			} else {
				resultIcon = icon;
			}
		}
		
		return resultIcon;			
	}

	@Override
	public Runnable getAction() {
		if (!Files.isRegularFile(fileItemPath) || accessDenied) return null;
		return () -> {
			Resources.internalTaskScheduler.execute(() -> {
				try {
					java.awt.Desktop.getDesktop().open(fileItemPath.toFile());
				} catch (IOException e) {
					e.printStackTrace();
					Utils.showErrorMessage("Error while opening " + fileItemPath.toString() + "\n\n" + e.getMessage(), false);
				}
			});
		};
	}
	
	@Override
	public int countChildren(int menuId) {
		if (accessDenied) return 0;
		
		if (menuId == XSMenuItem.PRIMARY_MENU) {
			ArrayList<Path> containedPaths=getContainedPaths();
			return (containedPaths == null) ? 0 : containedPaths.size();
		} else if (menuId == XSMenuItem.AUXILARY_MENU) {
			return getAuxMenu().size();
		}
		
		return 0;
	}

	@Override
	public XSMenuItem getChild(int menuId, int index) {
		if (accessDenied) return null;

		if (menuId == XSMenuItem.PRIMARY_MENU) {
			ArrayList<FileSystemMenuItem> containedItems=getContainedItems();
			return (containedItems == null) ? null : containedItems.get(index);
		} else if (menuId == XSMenuItem.AUXILARY_MENU) {
			return getAuxMenu().get(index);
		}
		
		return null;
	}
	
	@Override
	public boolean isEnabled() {return !accessDenied;}
	
	@Override
	public String getTooltip() {return tooltip;}
}
