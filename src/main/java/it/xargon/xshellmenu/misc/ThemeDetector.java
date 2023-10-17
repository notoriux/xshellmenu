package it.xargon.xshellmenu.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class ThemeDetector {
    private static final String REGQUERY_UTIL  = "reg query ";
    private static final String REGDWORD_TOKEN = "REG_DWORD";
    private static final String DARK_THEME_CMD = REGQUERY_UTIL + "\"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize\"" + " /v SystemUsesLightTheme";

    public static final boolean isDarkMode() {
        switch(Utils.getOperatingSystem()) {
            case WINDOWS:
            	return isWindowsDarkMode();
            case MACOS:
            	return isMacOsDarkMode();
            default:
            	return false;
        }
    }

    public static final boolean isMacOsDarkMode() {
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

    public static final boolean isWindowsDarkMode() {
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
        	Utils.showErrorMessage("Exception while invoking \"reg\" utility: " + e.getMessage(), true);
            return false;
        }
    }

    // ******************** Internal Classes **********************************
    static class StreamReader extends Thread {
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
}