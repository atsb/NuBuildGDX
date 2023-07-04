// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave.Menus;

import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Sound.*;

import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuAudio;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Powerslave.Main;

public class PSMenuAudio extends MenuAudio {

	public PSMenuAudio(Main app) {
		super(app, 25,  40, 280, 10, 10, app.getFont(0));

		this.setListener(listener);
		
		removeItem(mApplyChanges);
		mApplyChanges = new PSButton("Apply changes", 0, mApplyChanges.y, 320, 1, 0, null, -1, mApplyChanges.specialCall, 0) {
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(snddriver != osnddriver || middriver != omiddriver || resampler != oresampler || voices != ovoices || cdaudio != ocdaudio);
				super.draw(handler);
			}
		};
		
		sSoundDrv.fontShadow = true;
		sSoundDrv.listShadow = true;
		int pos = removeItem(sMusicDrv);
		for(int i = pos; i < m_nItems; i++)
			m_pItems[i].y -= 10;
		sMusicDrv = null;

		sResampler.fontShadow = true;
		sResampler.listShadow = true;
		sSound.fontShadow = true;
		sVoices.fontShadow = true;
		sVoices.min = 8;
		sSoundSwitch.fontShadow = true;
		sMusic.fontShadow = true;
		sMusicSwitch.fontShadow = true;
		sMusicType.fontShadow = true;
		sMusicType.listShadow = true;

		addItem(mApplyChanges, false);
	}
	
	private AudioListener listener = new AudioAdapter() {
		@Override
		public void PreDrvChange(Driver drv) {
			if(drv == Driver.Sound) {
				StopAllSounds();
				if (cfg.musicType == cdaudio)
					StopMusic();
			}
			if (drv == Driver.Music)
			StopMusic();
		}

		@Override
		public void PostDrvChange() {
			if (game.isCurrentScreen(gGameScreen)) 
				sndPlayMusic();
			if (game.isCurrentScreen(gMenuScreen) || game.isCurrentScreen(gDemoScreen))
				playCDtrack(19, true);
		}

		@Override
		public void SoundOff() {
			StopAllSounds();
		}
	};
	
	@Override
	protected char[][] getMusicTypeList()
	{
		char[][] list = new char[1][];
		list[0] = "cd audio".toCharArray();
		
		return list;
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new PSTitle(text, 160, 15, 0);
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
