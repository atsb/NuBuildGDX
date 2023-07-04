// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood.Menus;

import static ru.m210projects.Blood.SOUND.*;

import ru.m210projects.Blood.Main;

import static ru.m210projects.Blood.Main.*;

import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuAudio;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class BLMenuAudio extends MenuAudio {

	public BLMenuAudio(Main app) {
		super(app, 46,  30, 240, 10, 10, app.getFont(3));
		
		this.setListener(listener);
		
		mApplyChanges.font = app.getFont(1);
		mApplyChanges.y += 10;
		mApplyChanges.fontShadow = true;
		
		addItem(app.menu.addMenuBlood(), false);
	}
	
	private final AudioListener listener = new AudioAdapter() {
		@Override
		public void PreDrvChange(Driver drv) {
			sndStopAllSamples();
			sfxKillAll3DSounds();
			if (game.isCurrentScreen(gGameScreen) || game.isCurrentScreen(gDemoScreen))
				ambPrepare();
			
			sndStopMusic();
		}

		@Override
		public void PostDrvChange() {
			if (game.isCurrentScreen(gGameScreen) || game.isCurrentScreen(gDemoScreen)) 
				sndPlayMusic();
			if (game.isCurrentScreen(gMenuScreen))
				sndPlayMenu();
		}
		
		@Override
		public void SoundVolumeChange() {
			ambProcess();
		}

		@Override
		public void SoundOn() {
			if (game.isCurrentScreen(gGameScreen) || game.isCurrentScreen(gDemoScreen))
				ambPrepare();
		}

		@Override
		public void SoundOff() {
			sndStopAllSamples();
			sfxKillAll3DSounds();
			ambStopAll();
		}
	};

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new MenuTitle(app.pEngine, text, app.getFont(1), 160, 20, 2038);
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
