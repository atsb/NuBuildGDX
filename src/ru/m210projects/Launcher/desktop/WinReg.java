package ru.m210projects.Launcher.desktop;

import java.io.*;

public class WinReg {

	private static final String REGQUERY_UTIL = "reg query ";
	private static final String REGSTR_TOKEN = "REG_SZ";
	// private static final String REGDWORD_TOKEN = "REG_DWORD";

	public static String getRegKey(String path, String key) {
		try {
			Process process;
			if (key == "")
				process = Runtime.getRuntime().exec(REGQUERY_UTIL + "\"" + path + "\" /ve");
			else
				process = Runtime.getRuntime().exec(REGQUERY_UTIL + "\"" + path + "\" /v " + "\"" + key + "\"");
			StreamReader reader = new StreamReader(process.getInputStream());

			reader.start();
			process.waitFor();
			reader.join();

			String result = reader.getResult();
			int p = result.indexOf(REGSTR_TOKEN);

			if (p == -1)
				return null;

			return result.substring(p + REGSTR_TOKEN.length()).trim();
		} catch (Exception e) {
			return null;
		}
	}

	// public static Boolean checkRegKey(String path) {

	// }

	static class StreamReader extends Thread {
		private InputStream is;
		private StringWriter sw;

		StreamReader(InputStream is) {
			this.is = is;
			sw = new StringWriter();
		}

		@Override
		public void run() {
			try {
				int c;
				while ((c = is.read()) != -1)
					sw.write(c);
			} catch (IOException e) {
				;
			}
		}

		String getResult() {
			return sw.toString();
		}
	}
}