package ru.m210projects.Duke3D.desktop;

import static ru.m210projects.Build.Render.VideoMode.setFullscreen;

import java.io.File;

import ru.m210projects.Build.Architecture.ApplicationFactory;
import ru.m210projects.Build.Architecture.BuildApplication;
import ru.m210projects.Build.Architecture.BuildConfiguration;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.BuildAudio;
import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.FileHandle.Cache1D;
import ru.m210projects.Build.FileHandle.Compat;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.desktop.DesktopFactory;
import ru.m210projects.Build.desktop.audio.midi.MidiMusicModule;
import ru.m210projects.Duke3D.Config;
import ru.m210projects.Duke3D.Main;

public class DesktopLauncher {

	public static final String appname = "DukeGDX";

	public static void main(final String[] arg) {
		//Run configurations: "D:\Games\Duke3D"
		String filepath = arg[0] + File.separator;
		BuildGdx.compat = new Compat(filepath, filepath);
		BuildGdx.cache = new Cache1D(BuildGdx.compat);

		BuildConfig cfg = new Config(Path.Game.getPath(), appname + ".ini");
		BuildConfiguration appcfg = new BuildConfiguration();
		appcfg.fullscreen = setFullscreen(cfg.ScreenWidth, cfg.ScreenHeight, cfg.fullscreen == 1);
		appcfg.width = (cfg.ScreenWidth);
		appcfg.height = (cfg.ScreenHeight);
		appcfg.backgroundFPS = cfg.fpslimit;
		appcfg.foregroundFPS = cfg.fpslimit;
		appcfg.vsync = cfg.gVSync;
		appcfg.borderless = cfg.borderless;
		ApplicationFactory factory = new DesktopFactory(appcfg);

		DesktopFactory.InitSoundDrivers();

		int midiDevice = 0;
		BuildAudio.registerDriver(Driver.Music, new MidiMusicModule(midiDevice, null));

		DesktopFactory.InitVideoModes();
		new BuildApplication(new Main(cfg, appname, "?.??", 0, false, false), factory, cfg.renderType);
	}
}
