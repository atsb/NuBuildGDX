// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Menus;

import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Redneck.Main.cfg;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Main.game;
import static ru.m210projects.Redneck.Names.BIGFNTCURSOR;
import static ru.m210projects.Redneck.Names.SPINNINGNUKEICON;
import static ru.m210projects.Redneck.SoundDefs.KICK_HIT;
import static ru.m210projects.Redneck.SoundDefs.PISTOL_BODYHIT;
import static ru.m210projects.Redneck.Sounds.currMusic;
import static ru.m210projects.Redneck.Sounds.currTrack;
import static ru.m210projects.Redneck.Sounds.sndPlayTrack;
import static ru.m210projects.Redneck.Sounds.sound;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlider;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;

public class TrackPlayerMenu extends BuildMenu {

	public TrackPlayerMenu() {
		this.addItem(new RRTitle("8 Track Player"), false);

		this.addItem(new TrackItem(160, 90), true);

		this.addItem(new PowerSwitch(74, 109), false);

		this.addItem(new MenuSlider(game.pSlider, "Music volume", game.getFont(1), 45, 140, 230,
				(int) (cfg.musicVolume * 256), 0, 256, 8, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.musicVolume = slider.value / 256.0f;
						BuildGdx.audio.setVolume(Driver.Music, cfg.musicVolume);
					}
				}, false) {

			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(!cfg.muteMusic && BuildGdx.audio.getSound().getDigitalMusic() != null && BuildGdx.audio.getSound().getDigitalMusic().isInited());
				super.draw(handler);
			}
		}, false);

		this.addItem(new MenuSwitch("Shuffle", game.getFont(1), 45, 155, 230, cfg.gShuffleMusic, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.gShuffleMusic = sw.value;
			}
		}, null, null) {
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(!cfg.muteMusic && BuildGdx.audio.getSound().getDigitalMusic() != null && BuildGdx.audio.getSound().getDigitalMusic().isInited());
				super.draw(handler);
			}
		}, false);
	}

	private class PowerSwitch extends MenuSwitch {

		public PowerSwitch(int x, int y) {
			super(null, null, x, y, 320, cfg.muteMusic, null, null, null);
		}

		@Override
		public void draw(MenuHandler handler) {
			if (cfg.muteMusic)
				engine.rotatesprite(x << 16, y << 16, 48000, 0, 372, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);
			else {
				engine.rotatesprite(x << 16, y << 16, 48000, 0, 373, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);
			}

			if (isFocused())
				engine.rotatesprite(x - 50 << 16, (y + engine.getTile(BIGFNTCURSOR).getHeight() - 20) << 16, 8192, 0,
						SPINNINGNUKEICON + ((totalclock >> 3) & 15), 8 - (totalclock & 0x3F), 0, 2 | 8, 0, 0, xdim - 1,
						ydim - 1);
		}

		@Override
		public boolean callback(MenuHandler handler, MenuOpt opt) {
			switch (opt) {
			case LMB:
			case ENTER:
				if ((flags & 4) == 0)
					return false;

				cfg.muteMusic = !cfg.muteMusic;

				if (cfg.muteMusic)
					BuildGdx.audio.setVolume(Driver.Music, 0);
				else
					BuildGdx.audio.setVolume(Driver.Music, cfg.musicVolume);

				sound(KICK_HIT);
				break;
			default:
				return m_pMenu.mNavigation(opt);
			}

			return false;
		}

		@Override
		public boolean mouseAction(int mx, int my) {
			if (mx > x - 15 && mx < x + 15)
				if (my > y - 15 && my < y + 10)
					return true;

			return false;
		}

	}

	private class TrackItem extends MenuItem {

		final int x;
		final int y;

		final int[] tx;
		final int[] ty;

		int TrackFocus = -1;

		public TrackItem(int x, int y) {
			super(null, null);

			this.x = x;
			this.y = y;
			this.flags = 3 | 4;

			tx = new int[] { x - 101, x - 70, x - 41, x - 12 };
			ty = new int[] { y - 33, y - 18 };
		}

		@Override
		public void draw(MenuHandler handler) {
			engine.rotatesprite(x << 16, y << 16, 48000, 0, 370, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);

			if (!cfg.muteMusic) {
				if (currMusic != null && currMusic.isPlaying()) {
					int num = currTrack;
					if (num >= 0 && num < 8) {
						engine.rotatesprite(tx[num % 4] << 16, ty[(num / 4) & 1] << 16, 48000, 0, 371, 0, 0, 10 | 16, 0,
								0, xdim - 1, ydim - 1);
					}
				}

				if (TrackFocus != -1 && (totalclock & 16) != 0 && isFocused()) {
					engine.rotatesprite(tx[TrackFocus % 4] << 16, ty[(TrackFocus / 4) & 1] << 16, 48000, 0, 371, 0, 0,
							10 | 16, 0, 0, xdim - 1, ydim - 1);
				}
			}

			if (isFocused())
				engine.rotatesprite(x - 136 << 16, (y + engine.getTile(BIGFNTCURSOR).getHeight() - 40) << 16, 8192, 0,
						SPINNINGNUKEICON + ((totalclock >> 3) & 15), 8 - (totalclock & 0x3F), 0, 2 | 8, 0, 0, xdim - 1,
						ydim - 1);
		}

		private void changeTrack(int num) {
			if (num != -1) {
				if (!sndPlayTrack(num)) {
					Console.Println("Music track not found!");
				}
			}
		}

		@Override
		public boolean callback(MenuHandler handler, MenuOpt opt) {
			int line;

			switch (opt) {
			case LMB:
			case ENTER:
				if ((flags & 4) == 0)
					return false;

				sound(PISTOL_BODYHIT);
				changeTrack(TrackFocus);
				break;
			case LEFT:
				if ((flags & 4) == 0)
					return false;

				sound(335);
				TrackFocus--;
				TrackFocus &= 7;
				break;
			case RIGHT:
				if ((flags & 4) == 0)
					return false;

				sound(335);
				TrackFocus++;
				TrackFocus &= 7;
				break;
			case UP:
				line = (TrackFocus / 4);
				if (!cfg.muteMusic && line == 1) {
					TrackFocus -= 4;
					TrackFocus &= 7;
				} else
					m_pMenu.mNavUp();
				break;
			case DW:
				line = (TrackFocus / 4);
				if (!cfg.muteMusic && line == 0) {
					TrackFocus += 4;
					TrackFocus &= 7;
				} else
					m_pMenu.mNavDown();
				break;
			default:
				return m_pMenu.mNavigation(opt);
			}

			return false;
		}

		@Override
		public boolean mouseAction(int mx, int my) {

			for (int i = 0; i < 4; i++) {
				if (mx > tx[i] && mx < tx[i] + 10) {
					for (int j = 0; j < 2; j++) {
						if (my > ty[j] && my < ty[j] + 10) {
							TrackFocus = i + 4 * j;
							return true;
						}
					}
				}
			}

			return false;
		}

		@Override
		public void open() {
			TrackFocus = currTrack;
		}

		@Override
		public void close() {
		}

	}
}
