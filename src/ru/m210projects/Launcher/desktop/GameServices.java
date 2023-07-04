//This file is part of BuildGDX.
//Copyright (C) 2017-2021  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Launcher.desktop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import ru.m210projects.Build.Architecture.BuildApplication.Platform;
import ru.m210projects.Launcher.desktop.GameEntries.GameEntry;

public class GameServices {

	public static class GameLabel {
		public String label;
		public String path;

		public GameLabel(String label, String path) {
			this.label = label;
			this.path = path;
		}
	}

	private ArrayList<String> steamLibraries;
	private ArrayList<String> gogLibraries;
	private HashMap<GameEntry, String[]> steamGamePaths = new HashMap<GameEntry, String[]>();
	private HashMap<GameEntry, String[]> gogGamePaths = new HashMap<GameEntry, String[]>();
	private HashMap<GameEntry, String[]> otherGamePaths = new HashMap<GameEntry, String[]>();

	private ArrayList<GameLabel> result = new ArrayList<GameLabel>();
	private Platform platform;

	public enum Service {
		Steam, GOG, Other
	}

	public GameServices(Platform platform) {
		getSteamLibraries(platform);
		getGOGLibraries(platform);

		this.platform = platform;
	}

	private String getSteamFolder(Platform platform) {
		String steamInstall = null;
		switch (platform) {
		case Linux:
			steamInstall = System.getProperty("user.home") + File.separator + ".steam" + File.separator + "steam";
			steamInstall = (steamInstall + File.separator + "steamapps" + File.separator);
			break;
		case Windows:
			steamInstall = WinReg.getRegKey("HKCU\\Software\\Valve\\Steam", "SteamPath");
			if (steamInstall != null) {
				steamInstall = (steamInstall + File.separator + "steamapps" + File.separator).replace("/",
						File.separator);
			}
			break;
		default:
			return null;
		}

		if (steamInstall != null) {
			File folder = new File(steamInstall);
			if (!folder.exists())
				return null;
		}
		return steamInstall;
	}

	private void getSteamLibraries(Platform platform) {
		String steamInstall = getSteamFolder(platform);
		if (steamInstall != null) {
			System.out.println("Found Steam folder");

			steamLibraries = new ArrayList<String>();
			steamLibraries.add(steamInstall + "common" + File.separator);
			System.out.println(steamLibraries.get(0));

			String pathToVdf = steamInstall + "libraryfolders.vdf";
			Scanner input = null;
			try {
				input = new Scanner(new File(pathToVdf));
				String message = input.nextLine();
				if (message.equals("\"LibraryFolders\"")) {
					while (input.hasNextLine()) {
						message = input.nextLine();
						if (message.startsWith("\t\"") && Character.isDigit(message.charAt(2))) {
							String[] sentences = message.split("\t");
							String s = sentences[sentences.length-1];
							s = s.replace("\"", "");
							s = s.replace("\\\\", File.separator);
							s = s + File.separator + "steamapps" + File.separator + "common" + File.separator;
							steamLibraries.add(s);
							System.out.println(s);
						}
					}
				 }
				else if (message.equals("\"libraryfolders\"")) {
					while (input.hasNextLine()) {
						message = input.nextLine();
						if (message.startsWith("\t\t\"path\"")) {
							String[] sentences = message.split("\t");
							String s = sentences[sentences.length-1];
							s = s.replace("\"", "");
							s = s.replace("\\\\", File.separator);
							s = s + File.separator + "steamapps" + File.separator + "common" + File.separator;
							steamLibraries.add(s);
							System.out.println(s);
						}
					}
				 }
				input.close();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		} else {
			System.out.println("Steam folder not found");
		}
	}

	private void getGOGLibraries(Platform platform) {
		switch (platform) {
		case Linux:
			break;
		case Windows:
			String[] gogpaths = { "HKLM\\Software\\WOW6432Node\\GOG.com", "HKLM\\Software\\GOG.com" };
			for (String path : gogpaths) {
				if (WinReg.getRegKey(path, "") != null) {
					if (gogLibraries == null)
						gogLibraries = new ArrayList<String>();
					gogLibraries.add(path);
				}
			}
			break;
		default:
			return;
		}

		if (gogLibraries != null)
			System.out.println("Found GOG folder");
	}

	public void registerGame(Service service, GameEntry entry, String... path) {
		if (service == Service.Steam)
			steamGamePaths.put(entry, path);
		else if (service == Service.GOG)
			gogGamePaths.put(entry, path);
		else if (service == Service.Other)
			otherGamePaths.put(entry, path);
	}

	public ArrayList<GameLabel> getPaths(GameEntry entry) {
		result.clear();

		if (steamLibraries != null && steamLibraries.size() > 0) {
			String[] paths = steamGamePaths.get(entry);
			if (paths != null) {
				for (int i = 0; i < steamLibraries.size(); i++) {
					for (String s : paths) {
						int index = s.indexOf(":\\");
						String label = s.substring(0, index);
						String path = steamLibraries.get(i) + s.substring(index + 2).trim() + File.separator;
						if (new File(path).exists()) {
							result.add(new GameLabel(label, path));
						}
					}
				}
			}
		}

		if (gogLibraries != null && gogGamePaths.size() > 0) {
			String[] paths = gogGamePaths.get(entry);
			if (paths != null) {
				for (int i = 0; i < gogLibraries.size(); i++) {
					for (String s : paths) {
						int index = s.indexOf(":\\");
						String label = s.substring(0, index);
						String path = s.substring(index + 2).trim();

						if (platform == Platform.Windows) {
							String pathToGame = (WinReg.getRegKey(gogLibraries.get(i) + "\\" + path, "path"));
							if (pathToGame != null && new File(pathToGame).exists()) {
								if (!pathToGame.endsWith(File.separator))
									pathToGame = pathToGame + File.separator;
//								if (pathToGame.contains("Witchaven II")) { pathToGame = pathToGame + "Enhanced\\GAME\\WHAVEN2\\"; }
//								else if (pathToGame.contains("Witchaven")) { pathToGame = pathToGame + "Enhanced\\GAME\\WHAVEN\\"; }
								result.add(new GameLabel(label, pathToGame));
							}
						}
					}
				}
			}
		}

		if (otherGamePaths.size() > 0) {
			String[] paths = otherGamePaths.get(entry);
			if (paths != null) {
				for (String s : paths) {
					int index = s.indexOf(":\\");
					String label = s.substring(0, index);
					String path = s.substring(index + 2).trim();

					if (platform == Platform.Windows) {
						String regKey = ""; // [Antology]
						if(label.equals("[Zoom]"))
							regKey = "InstallLocation";
						String pathToGame = (WinReg.getRegKey(path, regKey));
						if (pathToGame != null && new File(pathToGame).exists()) {
							if (!pathToGame.endsWith(File.separator))
								pathToGame = pathToGame + File.separator;
							result.add(new GameLabel(label, pathToGame));
						}
					}
				}
			}
		}

		return result;
	}
}
