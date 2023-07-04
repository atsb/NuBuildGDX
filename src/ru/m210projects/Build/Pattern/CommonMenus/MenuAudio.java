//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Build.Pattern.CommonMenus;

import java.util.ArrayList;
import java.util.List;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.BuildAudio;
import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlider;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Settings.BuildConfig;

public abstract class MenuAudio extends BuildMenu {

	public interface AudioListener {
		void PreDrvChange(Driver drv);

		void PostDrvChange();

		void SoundVolumeChange();

		void VoicesChange();

		void SoundOn();

		void SoundOff();

		void MusicVolumeChange();

		void MusicOn();

		void MusicOff();
	}

	public class AudioAdapter implements AudioListener {

		@Override
		public void PreDrvChange(Driver drv) {
			/* nothing */ }

		@Override
		public void PostDrvChange() {
			/* nothing */ }

		@Override
		public void SoundVolumeChange() {
			/* nothing */ }

		@Override
		public void VoicesChange() {
			/* nothing */ }

		@Override
		public void SoundOn() {
			/* nothing */ }

		@Override
		public void SoundOff() {
			/* nothing */ }

		@Override
		public void MusicVolumeChange() {
			/* nothing */ }

		@Override
		public void MusicOn() {
			/* nothing */ }

		@Override
		public void MusicOff() {
			/* nothing */ }

	}

	public MenuButton mApplyChanges;
	public MenuConteiner sSoundDrv;
	public MenuConteiner sMusicDrv;
	public MenuConteiner sResampler;
	public MenuSlider sSound;
	public MenuSlider sVoices;
	public MenuSwitch sSoundSwitch;
	public MenuSlider sMusic;
	public MenuSwitch sMusicSwitch;
	public MenuConteiner sMusicType;

	public int snddriver;
	public int middriver;
	public int resampler;
	public int osnddriver;
	public int omiddriver;
	public int oresampler;
	public int voices;
	public int ovoices;
	public int cdaudio;
	public int ocdaudio;

	private AudioListener listener;

	public MenuAudio(BuildGame app, int posx, int posy, int width, int menuHeight, int separatorHeight,
			BuildFont menuItems) {
		final BuildConfig cfg = app.pCfg;
		
		addItem(getTitle(app, "Audio setup"), false);

		sSoundDrv = new MenuConteiner("Sound driver", menuItems, posx, posy += menuHeight, width, null, 0,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuConteiner item = (MenuConteiner) pItem;
						snddriver = item.num;
					}
				}) {
			@Override
			public void open() {
				if (this.list == null) {
					List<String> names = new ArrayList<String>();
					BuildAudio.getDeviceslList(Driver.Sound, names);
					this.list = new char[names.size()][];
					for (int i = 0; i < list.length; i++)
						this.list[i] = names.get(i).toCharArray();
				}
				num = snddriver = osnddriver = cfg.snddrv;
				if (BuildGdx.audio.IsInited(Driver.Sound))
					list[num] = BuildGdx.audio.getSound().getName().toCharArray();
				else
					list[num] = "initialization failed".toCharArray();
			}
		};

		sMusicDrv = new MenuConteiner("Midi driver", menuItems, posx, posy += menuHeight, width, null, 0,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuConteiner item = (MenuConteiner) pItem;
						middriver = item.num;
					}
				}) {
			@Override
			public void open() {
				if (this.list == null) {
					List<String> names = new ArrayList<String>();
					BuildAudio.getDeviceslList(Driver.Music, names);
					this.list = new char[names.size()][];
					for (int i = 0; i < list.length; i++)
						this.list[i] = names.get(i).toCharArray();
				}
				num = middriver = omiddriver = cfg.middrv;
				if (BuildGdx.audio.IsInited(Driver.Music)) {
					list[num] = BuildGdx.audio.getMusic().getName().toCharArray();
				} else
					list[num] = "initialization failed".toCharArray();
			}
		};

		sResampler = new MenuConteiner("Resampler", menuItems, posx, posy += menuHeight, width, null, 0,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuConteiner item = (MenuConteiner) pItem;
						resampler = item.num;
					}
				}) {
			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[BuildGdx.audio.getSound().getNumResamplers()][];
					for (int i = 0; i < list.length; i++)
						this.list[i] = BuildGdx.audio.getSound().getSoftResamplerName(i).toCharArray();
				}
				if (cfg.resampler_num < 0 || cfg.resampler_num >= BuildGdx.audio.getSound().getNumResamplers())
					cfg.resampler_num = 0;
				num = resampler = oresampler = cfg.resampler_num;
			}
		};

		posy += separatorHeight;
		int oposy = posy;
		posy += menuHeight;

		sSound = new MenuSlider(app.pSlider, "Sound volume", menuItems, posx, posy += menuHeight, width,
				(int) (cfg.soundVolume * 256), 0, 256, 16, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.soundVolume = slider.value / 256.0f;
						BuildGdx.audio.setVolume(Driver.Sound, cfg.soundVolume);
						if (listener != null)
							listener.SoundVolumeChange();
					}
				}, false) {

			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(!cfg.noSound && BuildGdx.audio.IsInited(Driver.Sound));
				super.draw(handler);
			}
		};

		sVoices = new MenuSlider(app.pSlider, "Voices", menuItems, posx, posy += menuHeight, width, 0, 8, 256, 8,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						voices = slider.value;
						if (listener != null)
							listener.VoicesChange();
					}
				}, true) {
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(!cfg.noSound && BuildGdx.audio.IsInited(Driver.Sound));
				super.draw(handler);
			}

			@Override
			public void open() {
				value = voices = ovoices = cfg.maxvoices;
			}
		};

		sSoundSwitch = new MenuSwitch("Sound", menuItems, posx, oposy + menuHeight, width, !cfg.noSound,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;

						cfg.noSound = !sw.value;
						sSound.mCheckEnableItem(!cfg.noSound);
						sVoices.mCheckEnableItem(!cfg.noSound);
						if (listener != null) {
							if (sw.value) {
								listener.SoundOn();
							} else {
								listener.SoundOff();
							}
						}
					}
				}, null, null);

		posy += separatorHeight;
		oposy = posy;
		posy += menuHeight;

		sMusic = new MenuSlider(app.pSlider, "Music volume", menuItems, posx, posy += menuHeight, width,
				(int) (cfg.musicVolume * 256), 0, 256, 8, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.musicVolume = slider.value / 256.0f;
						BuildGdx.audio.setVolume(Driver.Music, cfg.musicVolume);
						if (listener != null)
							listener.MusicVolumeChange();
					}
				}, false) {

			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(!cfg.muteMusic && BuildGdx.audio.IsInited(Driver.Music));
				super.draw(handler);
			}
		};

		sMusicSwitch = new MenuSwitch("Music", menuItems, posx, oposy += menuHeight, width, !cfg.muteMusic,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.muteMusic = !sw.value;
						if (cfg.muteMusic)
							BuildGdx.audio.setVolume(Driver.Music, 0);
						else
							BuildGdx.audio.setVolume(Driver.Music, cfg.musicVolume);

						sMusic.mCheckEnableItem(!cfg.muteMusic);
						if (listener != null) {
							if (sw.value) {
								listener.MusicOn();
							} else {
								listener.MusicOff();
							}
						}
					}
				}, null, null) {
			@Override
			public void draw(MenuHandler handler) {
				value = !cfg.muteMusic;
				super.draw(handler);
			}
		};

		sMusicType = new MenuConteiner("Music type", menuItems, posx, posy += menuHeight, width, null, 0,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuConteiner item = (MenuConteiner) pItem;
						cdaudio = item.num;
					}
				}) {
			@Override
			public void open() {
				if (this.list == null)
					this.list = getMusicTypeList();
				cdaudio = ocdaudio = num = cfg.musicType;
			}

			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(!cfg.muteMusic);
				super.draw(handler);
			}
		};

		MenuProc callback = new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				if (snddriver != osnddriver || voices != ovoices || resampler != oresampler) {
					if (listener != null)
						listener.PreDrvChange(Driver.Sound);

					int olddrv = BuildGdx.audio.getDriver(Driver.Sound);
					if (snddriver != osnddriver)
						BuildGdx.audio.setDriver(Driver.Sound, snddriver);
					if (voices != ovoices)
						cfg.maxvoices = voices;
					if (resampler != oresampler)
						cfg.resampler_num = resampler;

					if (SoundRestart(cfg.maxvoices, cfg.resampler_num)) {
						cfg.snddrv = osnddriver = snddriver;

						sSoundDrv.list[sSoundDrv.num] = BuildGdx.audio.getSound().getName().toCharArray();
						ovoices = voices;
						oresampler = resampler;

						sResampler.list = new char[BuildGdx.audio.getSound().getNumResamplers()][];
						for (int i = 0; i < sResampler.list.length; i++)
							sResampler.list[i] = BuildGdx.audio.getSound().getSoftResamplerName(i).toCharArray();
						if (cfg.resampler_num < 0 || cfg.resampler_num >= BuildGdx.audio.getSound().getNumResamplers())
							cfg.resampler_num = 0;
						sResampler.num = resampler = oresampler = cfg.resampler_num;

					} else {
						sSoundDrv.list[sSoundDrv.num] = "initialization failed".toCharArray();
						BuildGdx.audio.setDriver(Driver.Sound, olddrv);
					}
				} else {
					if (listener != null)
						listener.PreDrvChange(Driver.Music);

					if (middriver != omiddriver) {
						int olddrv = BuildGdx.audio.getDriver(Driver.Music);
						BuildGdx.audio.setDriver(Driver.Music, middriver);
						if (MusicRestart()) {
							cfg.middrv = omiddriver = middriver;
							sMusicDrv.list[sMusicDrv.num] = BuildGdx.audio.getMusic().getName().toCharArray();
						} else {
							sMusicDrv.list[sMusicDrv.num] = "initialization failed".toCharArray();
							BuildGdx.audio.setDriver(Driver.Music, olddrv);
						}
					}

					if (cdaudio != ocdaudio) {
						cfg.musicType = cdaudio;
						ocdaudio = cdaudio;
					}
				}
				if (listener != null)
					listener.PostDrvChange();
			}
		};

		posy += 2 * separatorHeight;
		mApplyChanges = new MenuButton("Apply changes", menuItems, 0, posy, 320, 1, 0, null, -1, callback, 0) {
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(snddriver != osnddriver || middriver != omiddriver || resampler != oresampler
						|| voices != ovoices || cdaudio != ocdaudio);
				super.draw(handler);
			}

			@Override
			public void mCheckEnableItem(boolean nEnable) {
				if (nEnable)
					flags = 3 | 4;
				else
					flags = 3;
			}
		};

		addItem(sSoundDrv, true);
		addItem(sMusicDrv, false);
		addItem(sResampler, false);
		addItem(sSoundSwitch, false);
		addItem(sSound, false);
		addItem(sVoices, false);
		addItem(sMusicSwitch, false);
		addItem(sMusic, false);
		addItem(sMusicType, false);
		addItem(mApplyChanges, false);
	}

	protected char[][] getMusicTypeList() {
		char[][] list = new char[3][];
		list[0] = "midi".toCharArray();
		list[1] = "external".toCharArray();
		list[2] = "cd audio".toCharArray();

		return list;
	}

	protected void setListener(AudioListener listener) {
		this.listener = listener;
	}

	protected void removeListener() {
		this.listener = null;
	}

	public abstract MenuTitle getTitle(BuildGame app, String text);

	public abstract boolean SoundRestart(int voices, int resampler);

	public abstract boolean MusicRestart();
}
