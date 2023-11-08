module it.xargon.xshellmenu.filesystemprovider {
	requires transitive java.desktop;
	requires transitive it.xargon.xshellmenu.api;
	exports it.xargon.xshellmenu.filesystemprovider;
	provides it.xargon.xshellmenu.api.XSMenuRootProvider with it.xargon.xshellmenu.filesystemprovider.FileSystemRootProvider;
}