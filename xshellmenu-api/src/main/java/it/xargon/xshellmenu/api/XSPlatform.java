package it.xargon.xshellmenu.api;

public interface XSPlatform {
	public enum OperatingSystem { WINDOWS, MACOS, LINUX, SOLARIS, NONE }
	
	public OperatingSystem getOperatingSystem();
	
	public void abortApplication(String errorMessage, Exception ex);
	
	public void showErrorMessage(String errorMessage, boolean wait);
	
	public boolean isDarkModeSystemTheme();
		
	public <T> T getPlatformResource(XSPlatformResource<T> resourceIdentifier);
}
