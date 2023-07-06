//This file is part of BuildGDX.
//Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_GOLD;
import static ru.m210projects.Build.Render.VideoMode.setFullscreen;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.badlogic.gdx.Files.FileType;

import ru.m210projects.Build.Architecture.ApplicationFactory;
import ru.m210projects.Build.Architecture.BuildApplication;
import ru.m210projects.Build.Architecture.BuildConfiguration;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildApplication.Platform;
import ru.m210projects.Build.Audio.BuildAudio;
import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.FileHandle.Cache1D;
import ru.m210projects.Build.FileHandle.Compat;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.desktop.DesktopFactory;
import ru.m210projects.Build.desktop.audio.midi.MidiMusicModule;
import ru.m210projects.Launcher.desktop.GameEntries.BloodEntry;
import ru.m210projects.Launcher.desktop.GameEntries.DukeEntry;
import ru.m210projects.Launcher.desktop.GameEntries.GameEntry;
import ru.m210projects.Launcher.desktop.GameEntries.LSPEntry;
import ru.m210projects.Launcher.desktop.GameEntries.NamEntry;
import ru.m210projects.Launcher.desktop.GameEntries.PSlaveEntry;
import ru.m210projects.Launcher.desktop.GameEntries.RREntry;
import ru.m210projects.Launcher.desktop.GameEntries.RRRAEntry;
import ru.m210projects.Launcher.desktop.GameEntries.SWEntry;
import ru.m210projects.Launcher.desktop.GameEntries.TekwarEntry;
import ru.m210projects.Launcher.desktop.GameEntries.WH2Entry;
import ru.m210projects.Launcher.desktop.GameEntries.WHEntry;

public class Main {

	public static String appversion = "v1.18.1";
	public static final String headerPath = "Headers";
	public static final String iconPath = "Games";
	public static final Platform platform = getPlatform();
	public static final boolean isWindows = platform == Platform.Windows;
	public static Frame fr;

	public static void main(final String[] args) {
		DesktopFactory.InitSoundDrivers();
		String path = null;

		boolean isSilent = false;
		for(int i = 0; i < args.length; i++) {
			String s = args[i];
			if(s.equals("-path") && (i + 1) < args.length) {
				path = args[++i];
			} else if(s.equals("-silent"))
				isSilent = true;
		}

		fr = new Frame(path, !isSilent, new BloodEntry() {
			@Override
			public void startGame(String gamePath) {
				launchPort(args, gamePath, this);
			}
		}, new DukeEntry() {
			@Override
			public void startGame(String gamePath) {
				launchPort(args, gamePath, this);
			}
		}, new NamEntry() {
			@Override
			public void startGame(String gamePath) {
				launchPort(args, gamePath, this);
			}
		}, new SWEntry() {
			@Override
			public void startGame(String gamePath) {
				launchPort(args, gamePath, this);
			}
		}, new RREntry() {
			@Override
			public void startGame(String gamePath) {
				launchPort(args, gamePath, this);
			}
		}, new RRRAEntry() {
			@Override
			public void startGame(String gamePath) {
				launchPort(args, gamePath, this);
			}
		}, new PSlaveEntry() {
			@Override
			public void startGame(String gamePath) {
				launchPort(args, gamePath, this);
			}
		}, new TekwarEntry() {
			@Override
			public void startGame(String gamePath) {
				launchPort(args, gamePath, this);
			}
		}, new WHEntry() {
			@Override
			public void startGame(String gamePath) {
				launchPort(args, gamePath, this);
			}
		}, new WH2Entry() {
			@Override
			public void startGame(String gamePath) {
				launchPort(args, gamePath, this);
			}
		}, new LSPEntry() {
			@Override
			public void startGame(String gamePath) {
				launchPort(args, gamePath, this);
			}
		});
	}

	private static void launchPort(String[] args, String gamePath, GameEntry entry) {
		BuildConfiguration appcfg = new BuildConfiguration();
		BuildConfig cfg = entry.getConfig();

		BuildGdx.compat = new Compat(gamePath, (fr != null && fr.forcePortable) ? gamePath : cfg.cfgPath);
		BuildGdx.cache = new Cache1D(BuildGdx.compat);

		appcfg.fullscreen = setFullscreen(cfg.ScreenWidth, cfg.ScreenHeight, cfg.fullscreen == 1);
		appcfg.width = (cfg.ScreenWidth);
		appcfg.height = (cfg.ScreenHeight);
		appcfg.backgroundFPS = cfg.fpslimit;
		appcfg.foregroundFPS = cfg.fpslimit;
		appcfg.vsync = cfg.gVSync;
		appcfg.borderless = cfg.borderless;
		ApplicationFactory factory = new DesktopFactory(appcfg);

		String apptitle = entry.appname + " " + entry.sversion;
		String newver = null;
		if (cfg.checkVersion) {
			newver = checkVersion("buildgdx.ver", appversion);
			if (newver != null)
				apptitle += " (new BuildGDX " + newver + ")";
		}

		appcfg.title = apptitle;
		String icons[] = entry.getIcons();
		for (int i = 0; i < icons.length; i++)
			appcfg.addIcon(icons[i], FileType.Internal);

		if (cfg.midiSynth < 0 || cfg.midiSynth >= MidiMusicModule.getDevices().size())
			cfg.midiSynth = 0;

		BuildAudio.registerDriver(Driver.Music, new MidiMusicModule(cfg.midiSynth, cfg.soundBank));

		DesktopFactory.InitVideoModes();
		new BuildApplication(entry.getGame(args), factory, cfg.renderType);

		if (fr != null)
			fr.dispose();
	}

	public static String checkVersion(String filename, String version) {
		String newver = null;
		String verstd;
		try {
			verstd = getVersion(filename);
			int webver = Integer.parseInt(verstd);
			int appver = Integer.parseInt(version.replaceAll("[^0-9]", ""));

			if (webver > appver) {
				verstd = "v" + verstd.substring(0, 1) + "." + verstd.substring(1, verstd.length());
				newver = verstd;
			}
		} catch (Exception e) {
		}

		if (newver != null) {
			showMessage("Please update!", "New version available: " + newver + "\r\n" + getChangelog());
		} else
			Console.Println("You are using the latest version");

		return newver;
	}

	private static String getVersion(String filename) throws Exception {
		URL version = new URL("http://m210.ucoz.ru/Files/" + filename);
		BufferedReader in = new BufferedReader(new InputStreamReader(version.openStream()));
		String inputLine = in.readLine();
		in.close();
		return inputLine;
	}

	private static String getChangelog() {
		String msg = "";
		try {
			URL version = new URL("http://m210.ucoz.ru/Files/changelog.gdx");
			BufferedReader in = new BufferedReader(new InputStreamReader(version.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				msg += inputLine + "\r\n";
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}

	private static void showMessage(String header, String message) {
		JOptionPane frame = new JOptionPane();
		frame.setMessage(message);
		frame.setMessageType(JOptionPane.INFORMATION_MESSAGE);
		JDialog dlog = frame.createDialog(null, header);
		frame.setBackground(dlog.getBackground());
		dlog.setAlwaysOnTop(true);
		dlog.setVisible(true);

		Console.Println(message, OSDTEXT_GOLD);
	}

	private static Platform getPlatform() {
		Platform platform;
		final String osName = System.getProperty("os.name");
		if (osName.startsWith("Windows"))
			platform = Platform.Windows;
		else if (osName.startsWith("Linux") || osName.startsWith("FreeBSD") || osName.startsWith("OpenBSD")
				|| osName.startsWith("SunOS") || osName.startsWith("Unix") || osName.indexOf("aix") > 0)
			platform = Platform.Linux;
		else if (osName.startsWith("Mac OS X") || osName.startsWith("Darwin"))
			platform = Platform.MacOSX;
		else
			platform = null;

		return platform;
	}
}
