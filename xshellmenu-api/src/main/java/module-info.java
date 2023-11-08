module it.xargon.xshellmenu.api {
	requires transitive java.desktop;
	exports it.xargon.xshellmenu.api;
	exports it.xargon.xshellmenu.api.base;
	uses it.xargon.xshellmenu.api.XSMenuRootProvider;
}