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

package ru.m210projects.Duke3D.Screens;

import static java.lang.Math.max;
import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Input.Keymap.ANYKEY;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Duke3D.Globals.MODE_END;
import static ru.m210projects.Duke3D.Globals.boardfilename;
import static ru.m210projects.Duke3D.Globals.currentGame;
import static ru.m210projects.Duke3D.Globals.frags;
import static ru.m210projects.Duke3D.Globals.kGameCrash;
import static ru.m210projects.Duke3D.Globals.ps;
import static ru.m210projects.Duke3D.Globals.uGameFlags;
import static ru.m210projects.Duke3D.Globals.ud;
import static ru.m210projects.Duke3D.Main.cfg;
import static ru.m210projects.Duke3D.Main.engine;
import static ru.m210projects.Duke3D.Main.gGameScreen;
import static ru.m210projects.Duke3D.Main.game;
import static ru.m210projects.Duke3D.Main.mUserFlag;
import static ru.m210projects.Duke3D.Names.BONUSSCREEN;
import static ru.m210projects.Duke3D.Names.INGAMEDUKETHREEDEE;
import static ru.m210projects.Duke3D.Names.MENUSCREEN;
import static ru.m210projects.Duke3D.Names.PLUTOPAKSPRITE;
import static ru.m210projects.Duke3D.SoundDefs.BONUSMUSIC;
import static ru.m210projects.Duke3D.SoundDefs.BONUS_SPEECH1;
import static ru.m210projects.Duke3D.SoundDefs.BONUS_SPEECH2;
import static ru.m210projects.Duke3D.SoundDefs.BONUS_SPEECH3;
import static ru.m210projects.Duke3D.SoundDefs.BONUS_SPEECH4;
import static ru.m210projects.Duke3D.SoundDefs.FLY_BY;
import static ru.m210projects.Duke3D.SoundDefs.PIPEBOMB_EXPLODE;
import static ru.m210projects.Duke3D.SoundDefs.SHOTGUN_COCK;
import static ru.m210projects.Duke3D.Sounds.StopAllSounds;
import static ru.m210projects.Duke3D.Sounds.clearsoundlocks;
import static ru.m210projects.Duke3D.Sounds.sndStopMusic;
import static ru.m210projects.Duke3D.Sounds.sound;

import com.badlogic.gdx.ScreenAdapter;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Duke3D.Main;
import ru.m210projects.Duke3D.Main.UserFlag;
import ru.m210projects.Duke3D.Types.MapInfo;

public class StatisticScreen extends ScreenAdapter {

	protected char[] bonusbuf = new char[128];
	protected int bonuscnt = 0;

	protected Main app;

	public StatisticScreen(Main app) {
		this.app = app;
	}

	@Override
	public void show() {
		engine.setbrightness(ud.brightness >> 2, palette, GLInvalidateFlag.All);
		totalclock = 0;

		StopAllSounds();
		sndStopMusic();
		clearsoundlocks();

		bonuscnt = 0;
		if (!cfg.muteMusic)
			sound(BONUSMUSIC);

		game.pInput.ctrlResetKeyStatus();
	}

	@Override
	public void render(float delta) {
		engine.clearview(0);
		engine.sampletimer();

		if (kGameCrash) {
			game.show();
			return;
		}

		if (numplayers > 1)
			game.pNet.GetPackets();

		if (dobonus(false)) {
			BuildGdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					if ((uGameFlags & MODE_END) != 0 || mUserFlag == UserFlag.UserMap) {
						game.show();
					} else
						gGameScreen.enterlevel(gGameScreen.getTitle(), ud.volume_number, ud.level_number);
				}
			});
		}

		engine.nextpage();
	}

	public boolean dobonus(boolean disconnect) {
		int t, gfx_offset;
		int i, y, xfragtotal, yfragtotal;
		int clockpad = 2;

		if (ud.multimode > 1 && ud.coop != 1 || disconnect) {
			engine.rotatesprite(0, 0, 65536, 0, MENUSCREEN, 16, 0, 2 + 8 + 16 + 64, 0, 0, xdim - 1, ydim - 1);
			engine.rotatesprite(160 << 16, 34 << 16, 65536, 0, INGAMEDUKETHREEDEE, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);
			if (currentGame.getCON().type != 13) // JBF 20030804
				engine.rotatesprite((260) << 16, 36 << 16, 65536, 0, PLUTOPAKSPRITE + 2, 0, 0, 2 + 8, 0, 0, xdim - 1,
						ydim - 1);

			app.getFont(1).drawText(160, 58 + 2, "MULTIPLAYER TOTALS", 0, 0, TextAlign.Center, 2, false);
			buildString(bonusbuf, 0, currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number].title);
			app.getFont(1).drawText(160, 58 + 10, bonusbuf, 0, 0, TextAlign.Center, 2, false);

			t = 0;
			app.getFont(0).drawText(23, 80, "   NAME                                           KILLS", 0, 0,
					TextAlign.Left, 2, false);
			for (i = 0; i < numplayers; i++) {
				Bitoa(i + 1, bonusbuf);
				app.getFont(0).drawText(92 + (i * 23), 80, bonusbuf, 0, 3, TextAlign.Left, 2, false);
			}

			for (i = 0; i < numplayers; i++) {
				xfragtotal = 0;
				Bitoa(i + 1, bonusbuf);

				app.getFont(0).drawText(30, 90 + t, bonusbuf, 0, 3, TextAlign.Left, 2, false);
				app.getFont(0).drawText(38, 90 + t, ud.user_name[i], 0, ps[i].palookup, TextAlign.Left, 2, false);

				for (y = 0; y < numplayers; y++) {
					if (i == y) {
						Bitoa(ps[y].fraggedself, bonusbuf);
						app.getFont(0).drawText(92 + (y * 23), 90 + t, bonusbuf, 0, 2, TextAlign.Left, 2, false);
						xfragtotal -= ps[y].fraggedself;
					} else {
						Bitoa(frags[i][y], bonusbuf);
						app.getFont(0).drawText(92 + (y * 23), 90 + t, bonusbuf, 0, 0, TextAlign.Left, 2, false);
						xfragtotal += frags[i][y];
					}

					if (myconnectindex == connecthead) {
                    }
				}

				Bitoa(xfragtotal, bonusbuf);
				app.getFont(0).drawText(101 + (8 * 23), 90 + t, bonusbuf, 0, 2, TextAlign.Left, 2, false);

				t += 7;
			}

			for (y = 0; y < numplayers; y++) {
				yfragtotal = 0;
				for (i = 0; i < numplayers; i++) {
					if (i == y)
						yfragtotal += ps[i].fraggedself;
					yfragtotal += frags[i][y];
				}
				Bitoa(yfragtotal, bonusbuf);
				app.getFont(0).drawText(92 + (y * 23), 96 + (8 * 7), bonusbuf, 0, 2, TextAlign.Left, 2, false);
			}

			app.getFont(0).drawText(45, 96 + (8 * 7), "DEATHS", 0, 8, TextAlign.Left, 2, false);
			app.getFont(1).drawText(160, 165, "PRESS ANY KEY TO CONTINUE", 0, 2, TextAlign.Center, 2, false);

			if ((game.pInput.ctrlKeyStatusOnce(ANYKEY)) && totalclock > (60 * 2))
				return true;
		}

		if (!disconnect && (ud.multimode < 2 || ud.coop == 1)) {
			MapInfo gMapInfo = currentGame.episodes[ud.volume_number].gMapInfo[ud.last_level - 1];
			switch (ud.volume_number) {
			case 1:
				gfx_offset = 5;
				break;
			default:
				gfx_offset = 0;
				break;
			}

			engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, BONUSSCREEN + gfx_offset, 0, 0, 2 + 8 + 64, 0, 0,
					xdim - 1, ydim - 1);
			if(gMapInfo != null) {
				int ii, ij;

				for (ii = ps[myconnectindex].player_par / (26 * 60), ij = 1; ii > 9; ii /= 10, ij++);
				clockpad = max(clockpad, ij);
				for (ii = gMapInfo.partime / (26 * 60), ij = 1; ii > 9; ii /= 10, ij++);
				clockpad = max(clockpad, ij);
				for (ii = gMapInfo.designertime / (26 * 60), ij = 1; ii > 9; ii /= 10, ij++);
				clockpad = max(clockpad, ij);
			}

			if (totalclock >= (1000000000) && totalclock < (1000000320)) {
				switch ((totalclock >> 4) % 15) {
				case 0:
					if (bonuscnt == 6) {
						bonuscnt++;
						sound(SHOTGUN_COCK);
						switch (engine.rand() & 3) {
						case 0:
							sound(BONUS_SPEECH1);
							break;
						case 1:
							sound(BONUS_SPEECH2);
							break;
						case 2:
							sound(BONUS_SPEECH3);
							break;
						case 3:
							sound(BONUS_SPEECH4);
							break;
						}
					}
				case 1:
				case 4:
				case 5:
					engine.rotatesprite(199 << 16, 31 << 16, 65536, 0, BONUSSCREEN + 3 + gfx_offset, 0, 0,
							2 + 8 + 16 + 64 + 128, 0, 0, xdim - 1, ydim - 1);
					break;
				case 2:
				case 3:
					engine.rotatesprite(199 << 16, 31 << 16, 65536, 0, BONUSSCREEN + 4 + gfx_offset, 0, 0,
							2 + 8 + 16 + 64 + 128, 0, 0, xdim - 1, ydim - 1);
					break;
				}
			} else if (totalclock > (10240 + 120))
				return true;
			else {
				switch ((totalclock >> 5) & 3) {
				case 1:
				case 3:
					engine.rotatesprite(199 << 16, 31 << 16, 65536, 0, BONUSSCREEN + 1 + gfx_offset, 0, 0,
							2 + 8 + 16 + 64 + 128, 0, 0, xdim - 1, ydim - 1);
					break;
				case 2:
					engine.rotatesprite(199 << 16, 31 << 16, 65536, 0, BONUSSCREEN + 2 + gfx_offset, 0, 0,
							2 + 8 + 16 + 64 + 128, 0, 0, xdim - 1, ydim - 1);
					break;
				}
			}

			String lastmapname = null;
			if (ud.volume_number == 0 && ud.last_level == 8 && boardfilename != null) {
				FileEntry file = BuildGdx.compat.checkFile(boardfilename);
				lastmapname = file.getName();
			} else if(gMapInfo != null)
				lastmapname = gMapInfo.title;

			app.getFont(2).drawText(160, 10 - 6, lastmapname, 0, 0, TextAlign.Center, 2, false);
			app.getFont(2).drawText(160, 26 - 6, "COMPLETED", 0, 0, TextAlign.Center, 2, false);
			app.getFont(1).drawText(160, 192, "PRESS ANY KEY TO CONTINUE", 16, 0, TextAlign.Center, 2, false);

			if (totalclock > (60 * 3)) {
				app.getFont(1).drawText(10, 59 + 9, "Your Time:", 0, 0, TextAlign.Left, 2, false);
				app.getFont(1).drawText(10, 69 + 9, "Par time:", 0, 0, TextAlign.Left, 2, false);
				app.getFont(1).drawText(10, 78 + 9, "3D Realms' Time:", 0, 0, TextAlign.Left, 2, false);

				if (bonuscnt == 0)
					bonuscnt++;

				if (totalclock > (60 * 4)) {
					if (bonuscnt == 1) {
						bonuscnt++;
						sound(PIPEBOMB_EXPLODE);
					}

					int num = Bitoa(ps[myconnectindex].player_par / (26 * 60), bonusbuf, 2);
					buildString(bonusbuf, num, " : ", (ps[myconnectindex].player_par / 26) % 60, 2);
					app.getFont(1).drawText((320 >> 2) + 71, 60 + 9, bonusbuf, 0, 0, TextAlign.Left, 2, false);

					if(gMapInfo != null) {
						num = Bitoa(gMapInfo.partime / (26 * 60), bonusbuf, 2);
						buildString(bonusbuf, num, " : ", (gMapInfo.partime / 26) % 60, 2);
						app.getFont(1).drawText((320 >> 2) + 71, 69 + 9, bonusbuf, 0, 0, TextAlign.Left, 2, false);

						num = Bitoa(gMapInfo.designertime / (26 * 60), bonusbuf, 2);
						buildString(bonusbuf, num, " : ", (gMapInfo.designertime / 26) % 60, 2);
						app.getFont(1).drawText((320 >> 2) + 71, 78 + 9, bonusbuf, 0, 0, TextAlign.Left, 2, false);
					}
				}
			}
			if (totalclock > (60 * 6)) {
				app.getFont(1).drawText(10, 94 + 9, "Enemies Killed:", 0, 0, TextAlign.Left, 2, false);
				app.getFont(1).drawText(10, 99 + 4 + 9, "Enemies Left:", 0, 0, TextAlign.Left, 2, false);

				if (bonuscnt == 2) {
					bonuscnt++;
					sound(FLY_BY);
				}

				if (totalclock > (60 * 7)) {
					if (bonuscnt == 3) {
						bonuscnt++;
						sound(PIPEBOMB_EXPLODE);
					}

					Bitoa(ps[connecthead].actors_killed, bonusbuf);
					app.getFont(1).drawText((320 >> 2) + 70, 93 + 9, bonusbuf, 0, 0, TextAlign.Left, 2, false);
					if (ud.player_skill > 3) {
						buildString(bonusbuf, 0, "N/A");
						app.getFont(1).drawText((320 >> 2) + 70, 99 + 4 + 9, "N/A", 0, 0, TextAlign.Left, 2, false);
					} else {
						if ((ps[connecthead].max_actors_killed - ps[connecthead].actors_killed) < 0)
							Bitoa(0, bonusbuf);
						else
							Bitoa(ps[connecthead].max_actors_killed - ps[connecthead].actors_killed, bonusbuf);
						app.getFont(1).drawText((320 >> 2) + 70, 99 + 4 + 9, bonusbuf, 0, 0, TextAlign.Left, 2, false);
					}
				}
			}
			if (totalclock > (60 * 9)) {
				app.getFont(1).drawText(10, 120 + 9, "Secrets Found:", 0, 0, TextAlign.Left, 2, false);
				app.getFont(1).drawText(10, 130 + 9, "Secrets Missed:", 0, 0, TextAlign.Left, 2, false);

				if (bonuscnt == 4)
					bonuscnt++;

				if (totalclock > (60 * 10)) {
					if (bonuscnt == 5) {
						bonuscnt++;
						sound(PIPEBOMB_EXPLODE);
					}
					Bitoa(ps[connecthead].secret_rooms, bonusbuf);
					app.getFont(1).drawText((320 >> 2) + 70, 120 + 9, bonusbuf, 0, 0, TextAlign.Left, 2, false);

					Bitoa(ps[connecthead].max_secret_rooms - ps[connecthead].secret_rooms, bonusbuf);
					app.getFont(1).drawText((320 >> 2) + 70, 130 + 9, bonusbuf, 0, 0, TextAlign.Left, 2, false);
				}
			}

			if (totalclock > 10240 && totalclock < 10240 + 10240)
				totalclock = 1024;

			if ((game.pInput.ctrlKeyStatusOnce(ANYKEY)) && totalclock > (60 * 2)) // JBF 20030809
			{
				if (totalclock < (60 * 13))
					totalclock = (60 * 13);
				else if (totalclock < (1000000000))
					totalclock = (1000000000);
			}
		}

		return false;
	}
}
