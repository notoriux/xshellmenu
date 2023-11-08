package it.xargon.xshellmenu.filesystemprovider;

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

import it.xargon.xshellmenu.api.XSMenuItem;
import it.xargon.xshellmenu.api.XSPlatform;
import it.xargon.xshellmenu.api.XSPlatform.OperatingSystem;
import it.xargon.xshellmenu.api.XSPlatformResource;
import it.xargon.xshellmenu.filesystemprovider.res.Resources;

class FileSystemMenuItem implements XSMenuItem {
	private XSPlatform pf;
	private String label;
	private Icon icon;
	private String tooltip = null;
	
	private boolean accessDenied = false;
	
	private Path fileItemPath;
	
	private FileSystemView fsv = FileSystemView.getFileSystemView();
		
	private Comparator<Path> pathNameComparator = new Comparator<Path>() {
		@Override
		public int compare(Path o1, Path o2) {
			return o1.getFileName().toString().compareTo(o2.getFileName().toString());
		}
	};
	
	private ArrayList<Path> lazyContainedPaths = null;
	private ArrayList<FileSystemMenuItem> lazyContainedItems = null;
	
	private ArrayList<XSMenuItem> auxMenu = null;
	
	public FileSystemMenuItem(XSPlatform pf, Path fileItemPath) {
		this.pf = pf;
		this.fileItemPath = fileItemPath;	
		this.label = fsv.getSystemDisplayName(fileItemPath.toFile());
	}
	
	private ArrayList<XSMenuItem> getAuxMenu() {
		if (auxMenu == null) {
			auxMenu = new ArrayList<XSMenuItem>();
			if (Files.isRegularFile(fileItemPath)) {
				if (pf.getOperatingSystem().equals(OperatingSystem.WINDOWS)) auxMenu.add(new RunAsAdministratorMenuItem(pf, fileItemPath));
			} else {
				auxMenu.add(new OpenFolderMenuItem(pf, fileItemPath));
				if (pf.getOperatingSystem().equals(OperatingSystem.WINDOWS)) auxMenu.add(new WindowsTerminalHereMenuItem(pf, fileItemPath));
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
			pf.abortApplication("Error while inspecting path " + fileItemPath.toString(), ex);
		}
				
		return lazyContainedPaths;
	}
	
	private ArrayList<FileSystemMenuItem> getContainedItems() {
		ArrayList<Path> containedPaths=getContainedPaths();
		
		if (lazyContainedItems == null && containedPaths != null) {
			lazyContainedItems = new ArrayList<>();
			containedPaths.forEach(p -> lazyContainedItems.add(new FileSystemMenuItem(pf, p)));
		}
		
		return lazyContainedItems;
	}

	public Path getFileItemPath() {
		return fileItemPath;
	}

	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	public Icon getIcon() {
		if (icon == null) {
			icon = fsv.getSystemIcon(fileItemPath.toFile(), 16, 16);
		}
		return icon;
	}

	@Override
	public Runnable getAction() {
		if (!Files.isRegularFile(fileItemPath) || accessDenied) return null;
		return () -> {
			pf.getPlatformResource(XSPlatformResource.TASK_SCHEDULER).execute(() -> {
				try {
					java.awt.Desktop.getDesktop().open(fileItemPath.toFile());
				} catch (IOException e) {
					e.printStackTrace();
					pf.showErrorMessage("Error while opening " + fileItemPath.toString() + "\n\n" + e.getMessage(), false);
				}
			});
		};
	}
	
	@Override
	public int countChildren(MenuType menuType) {
		if (accessDenied) return 0;
		
		if (XSMenuItem.MenuType.PRIMARY.equals(menuType)) {
			ArrayList<Path> containedPaths=getContainedPaths();
			return (containedPaths == null) ? 0 : containedPaths.size();
		} else if (XSMenuItem.MenuType.AUXILIARY.equals(menuType)) {
			return getAuxMenu().size();
		}
		
		return 0;
	}

	@Override
	public XSMenuItem getChild(MenuType menuType, int index) {
		if (accessDenied) return null;

		if (XSMenuItem.MenuType.PRIMARY.equals(menuType)) {
			ArrayList<FileSystemMenuItem> containedItems=getContainedItems();
			return (containedItems == null) ? null : containedItems.get(index);
		} else if (XSMenuItem.MenuType.AUXILIARY.equals(menuType)) {
			return getAuxMenu().get(index);
		}
		
		return null;
	}
	
	@Override
	public boolean isEnabled() {return !accessDenied;}
	
	@Override
	public String getTooltip() {return tooltip;}
}
