package it.xargon.xshellmenu.app.model.filesystem;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import it.xargon.xshellmenu.XShellMenuMainClass;
import it.xargon.xshellmenu.app.misc.Utils;
import it.xargon.xshellmenu.app.model.XSMenuItem;
import it.xargon.xshellmenu.app.model.XSMenuRootProvider;

public class FileSystemRootProvider implements XSMenuRootProvider {
	@Override
	public XSMenuItem getRootItem(String... args) {
		if (args.length == 0) {
			Utils.showErrorMessage("Please provide a starting directory as first argument", true);
			XShellMenuMainClass.exitApplication(2);
			return null; //will never reach this return			
		}
		
		Path basePath = Paths.get(args[0]).normalize().toAbsolutePath();
		
		if (!Files.exists(basePath) || !Files.isDirectory(basePath)) {			
			Utils.showErrorMessage("\"" + basePath.toString() + "\" does not exist or it's not a directory on the local filesystem", true);
			XShellMenuMainClass.exitApplication(3);
			return null; //will never reach this return
		}
		
		return new FileSystemMenuItem(basePath);
	}
}
