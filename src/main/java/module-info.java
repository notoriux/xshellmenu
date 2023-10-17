module it.xargon.xshellmenu {
	requires transitive java.desktop;
	exports it.xargon.xshellmenu;
	uses it.xargon.xshellmenu.XSMenuRootProvider;
	provides it.xargon.xshellmenu.XSMenuRootProvider with it.xargon.xshellmenu.app.model.filesystem.FileSystemRootProvider;
}