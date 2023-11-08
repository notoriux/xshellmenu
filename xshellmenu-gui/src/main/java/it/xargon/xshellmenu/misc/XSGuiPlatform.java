package it.xargon.xshellmenu.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import it.xargon.xshellmenu.XShellMenuMainClass;
import it.xargon.xshellmenu.api.XSPlatform;
import it.xargon.xshellmenu.api.XSPlatformResource;
import it.xargon.xshellmenu.res.Resources;

public class XSGuiPlatform implements XSPlatform {
    private static final String REGQUERY_UTIL  = "reg query ";
    private static final String REGDWORD_TOKEN = "REG_DWORD";
    private static final String DARK_THEME_CMD = REGQUERY_UTIL + "\"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize\"" + " /v SystemUsesLightTheme";

    private static class StreamReader extends Thread {
        private InputStream is;
        private StringWriter sw;

        StreamReader(InputStream is) {
            this.is = is;
            sw = new StringWriter();
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.write(c);
            } catch (IOException e) { ; }
        }

        String getResult() { return sw.toString(); }
    }
    
	private ScheduledExecutorService internalTaskScheduler;

	private ExecutorService iconFetcherScheduler;

	
	public XSGuiPlatform() {
		internalTaskScheduler = Executors.newSingleThreadScheduledExecutor();
		iconFetcherScheduler = Executors.newWorkStealingPool();
	}
	
	@Override
	public OperatingSystem getOperatingSystem() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("win") >= 0) {
            return OperatingSystem.WINDOWS;
        } else if (os.indexOf("mac") >= 0) {
            return OperatingSystem.MACOS;
        } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
            return OperatingSystem.LINUX;
        } else if (os.indexOf("sunos") >= 0) {
            return OperatingSystem.SOLARIS;
        } else {
            return OperatingSystem.NONE;
        }
	}

	@Override
	public void abortApplication(String errorMessage, Exception ex) {
		ex.printStackTrace();
		String abortMessage = errorMessage + "\n\n" + ex.getClass().getName() + ": " + ex.getMessage();
		showErrorMessage(abortMessage, true);
		XShellMenuMainClass.exitApplication(255);
	}

	@Override
	public void showErrorMessage(String errorMessage, boolean wait) {
		Runnable showTask = () -> {
			JOptionPane.showMessageDialog(
					null,
					errorMessage,
					"XShellMenu",
					JOptionPane.ERROR_MESSAGE);
		};
		
		if (wait) {
			if (SwingUtilities.isEventDispatchThread()) {
				showTask.run();
			} else {
				try {
					SwingUtilities.invokeAndWait(showTask);
				} catch (InvocationTargetException | InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}
		else SwingUtilities.invokeLater(showTask);
	}

	@Override
	public boolean isDarkModeSystemTheme() {
        switch(getOperatingSystem()) {
	        case WINDOWS:
	        	return isWindowsDarkMode();
	        case MACOS:
	        	return isMacOsDarkMode();
	        default:
	        	return false;
        }
	}
	
    private boolean isMacOsDarkMode() {
        try {
            boolean isDarkMode = false;
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("defaults read -g AppleInterfaceStyle");
            InputStreamReader is = new InputStreamReader(process.getInputStream());
            BufferedReader rd = new BufferedReader(is);
            String line;
            while((line = rd.readLine()) != null) {
                if (line.equals("Dark")) { isDarkMode = true; }
            }
            int rc = process.waitFor();
            return 0 == rc && isDarkMode;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

   private boolean isWindowsDarkMode() {
        try {
            Process process = Runtime.getRuntime().exec(DARK_THEME_CMD);
            StreamReader reader = new StreamReader(process.getInputStream());

            reader.start();
            process.waitFor();
            reader.join();

            String result = reader.getResult();
            int p = result.indexOf(REGDWORD_TOKEN);

            if (p == -1) { return false; }

            // 1 == Light Mode, 0 == Dark Mode
            String temp = result.substring(p + REGDWORD_TOKEN.length()).trim();
            return ((Integer.parseInt(temp.substring("0x".length()), 16))) == 0;
        } catch (Exception e) {
        	e.printStackTrace();
        	showErrorMessage("Exception while invoking \"reg\" utility: " + e.getMessage(), true);
            return false;
        }
    }
	
	@Override
	public <T> T getPlatformResource(XSPlatformResource<T> resourceIdentifier) {
		if (resourceIdentifier.equals(XSPlatformResource.GENERIC_ICON))
			return resourceIdentifier.cast(Resources.genericIcon);
		
		if (resourceIdentifier.equals(XSPlatformResource.APPLICATION_ICON))
			return resourceIdentifier.cast(Resources.appIcon);

		if (resourceIdentifier.equals(XSPlatformResource.TASK_SCHEDULER))
			return resourceIdentifier.cast(internalTaskScheduler);

		if (resourceIdentifier.equals(XSPlatformResource.ICONFETCHER_SCHEDULER))
			return resourceIdentifier.cast(iconFetcherScheduler);

		return null;
	}
}
