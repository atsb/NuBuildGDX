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

package ru.m210projects.LSP.Menus;

import static ru.m210projects.LSP.Globals.mapnum;
import static ru.m210projects.LSP.Globals.maps;
import static ru.m210projects.LSP.Main.*;
import static ru.m210projects.LSP.Sounds.*;

import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuAudio;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.LSP.Main;

public class LSPMenuAudio extends MenuAudio {

	public LSPMenuAudio(Main app) {
		super(app, 23,  40, 280, 10, 10, app.getFont(0));
		
		this.setListener(listener);
		
		sVoices.min = 8;
	}
	
	private AudioListener listener = new AudioAdapter() {
		@Override
		public void PreDrvChange(Driver drv) {
			stopallsounds();
		}

		@Override
		public void PostDrvChange() {
			if (game.isCurrentScreen(gGameScreen)) {
				stopmusic();
				startmusic(maps[mapnum].music - 1);
			}
			if (game.isCurrentScreen(gMenuScreen))
				startmusic(14);
		}

		@Override
		public void SoundOff() {
			stopallsounds();
		}
	};
	
	@Override
	protected char[][] getMusicTypeList()
	{
		char[][] list = new char[2][];
		list[0] = "midi".toCharArray();
		list[1] = "external".toCharArray();
		
		return list;
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return null;
	}

	@Override
	public boolean SoundRestart(int voices, int resampler) {
		return sndRestart(voices, resampler);
	}

	@Override
	public boolean MusicRestart() {
		return midRestart();
	}
}
