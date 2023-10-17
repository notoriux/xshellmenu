package it.xargon.xshellmenu.model.filesystem;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import it.xargon.xshellmenu.XSMenuItem;
import it.xargon.xshellmenu.XSMenuRootProvider;

public class FileSystemRootProvider implements XSMenuRootProvider {
	@Override
	public XSMenuItem getRootItem(String... args) {
		if (args == null || args.length == 0)
			throw new IllegalArgumentException("Please provide a starting directory as first argument");
		
		Path basePath = Paths.get(args[0]).normalize().toAbsolutePath();
		
		if (!Files.exists(basePath) || !Files.isDirectory(basePath)) 			
			throw new IllegalArgumentException("\"" + basePath.toString() + "\" does not exist or it's not a directory on the local filesystem");
		
		return new FileSystemMenuItem(basePath);
	}
	
	@Override
	public String getName() {
		return "FileSystem";
	}
}
