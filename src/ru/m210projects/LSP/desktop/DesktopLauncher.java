// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP.desktop;

import static ru.m210projects.Build.Render.VideoMode.setFullscreen;

import java.io.File;

import ru.m210projects.LSP.Config;
import ru.m210projects.LSP.Main;
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

public class DesktopLauncher {
	
	public static final String appname = "LSPGDX";

	public static void main(final String[] arg) {
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
		new BuildApplication(new Main(cfg, appname, "?.??", false), factory, cfg.renderType);
	}
}
