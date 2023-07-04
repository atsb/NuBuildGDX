package ru.m210projects.Wang.Menus;

import static ru.m210projects.Wang.Main.gDemoScreen;
import static ru.m210projects.Wang.Main.gGameScreen;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Sound.CDAudio_Stop;
import static ru.m210projects.Wang.Sound.StartAmbientSound;
import static ru.m210projects.Wang.Sound.StartMusic;
import static ru.m210projects.Wang.Sound.StopAmbientSound;
import static ru.m210projects.Wang.Sound.StopFX;
import static ru.m210projects.Wang.Sound.midRestart;
import static ru.m210projects.Wang.Sound.sndRestart;

import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuAudio;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Wang.Main;

public class MenuSound extends MenuAudio {

	public MenuSound(Main app) {
		super(app, 35, 30, 250, 10, 10, app.getFont(1));

		for (int i = 0; i < m_nItems; i++) {
			if (m_pItems[i] instanceof MenuSwitch) {
				final MenuSwitch oldSwitch = (MenuSwitch) m_pItems[i];
				m_pItems[i] = new WangSwitch(oldSwitch.text, oldSwitch.font, oldSwitch.x, oldSwitch.y, oldSwitch.width,
						oldSwitch.value, oldSwitch.callback) {
					@Override
					public void draw(MenuHandler handler) {
						oldSwitch.draw(handler);
						super.draw(handler);
					}
				};
				m_pItems[i].m_pMenu = this;
			}
		}

		this.setListener(listener);

		int pos = removeItem(sMusicDrv);
		for (int i = pos; i < m_nItems; i++)
			m_pItems[i].y -= 10;
		sMusicDrv = null;

		sSoundDrv.listFont = app.getFont(0);
		sResampler.listFont = app.getFont(0);

		mApplyChanges.font = app.getFont(1);
		mApplyChanges.x = 35;
		mApplyChanges.align = 0;
	}

	private AudioListener listener = new AudioAdapter() {
		@Override
		public void PreDrvChange(Driver drv) {
			if (drv == Driver.Sound) {
				StopFX();
				if (gs.musicType == cdaudio)
					CDAudio_Stop();
			}
			if (drv == Driver.Music)
				CDAudio_Stop();
		}

		@Override
		public void PostDrvChange() {
			StartMusic();
		}

		@Override
		public void SoundOn() {
			if (game.isCurrentScreen(gGameScreen) || game.isCurrentScreen(gDemoScreen)) {
				StartAmbientSound();
			}
		}

		@Override
		public void SoundOff() {
			StopAmbientSound();
			StopFX();
		}

		@Override
		public void MusicOn() {
			StartMusic();
		}
	};

	@Override
	public boolean SoundRestart(int voices, int resampler) {
		return sndRestart(voices, resampler);
	}

	@Override
	public boolean MusicRestart() {
		return midRestart();
	}

	@Override
	protected char[][] getMusicTypeList() {
		char[][] list = new char[1][];
		list[0] = "cd audio".toCharArray();

		return list;
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new WangTitle(text);
	}
}
