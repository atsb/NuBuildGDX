// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D.Menus;

import static ru.m210projects.Duke3D.Main.*;
import static ru.m210projects.Duke3D.Sounds.*;
import static ru.m210projects.Duke3D.Globals.*;

import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuAudio;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Duke3D.Main;

public class SoundMenu extends MenuAudio {

	public SoundMenu(Main app)
	{
		super(app, 20,  30, 280, 10, 10, app.getFont(1));
		
		this.setListener(listener);
		
		sSoundDrv.listFont = app.getFont(0);
		sMusicDrv.listFont = app.getFont(0);
		sResampler.listFont = app.getFont(0);
		
		mApplyChanges.font = app.getFont(2);
		mApplyChanges.y += 10;
	}
	
	private final AudioListener listener = new AudioAdapter() {
		@Override
		public void PreDrvChange(Driver drv) {
			StopAllSounds();
		}

		@Override
		public void PostDrvChange() {
			if (game.isCurrentScreen(gGameScreen) || game.isCurrentScreen(gDemoScreen) || game.isCurrentScreen(gMenuScreen)) {
				sndStopMusic();
				if(!game.isCurrentScreen(gGameScreen)) { sndPlayMusic(currentGame.getCON().env_music_fn[0]); } 
				else sndPlayMusic(currentGame.getCON().music_fn[ud.volume_number][ud.level_number]);
			}
		}

		@Override
		public void SoundVolumeChange() {
			CommentaryVolume(pCommentary, cfg.soundVolume);
		}

		@Override
		public void SoundOff() {
			StopAllSounds();
		}
	};

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new DukeTitle(text);
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
