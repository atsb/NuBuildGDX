//Copyright (C) 1996, 2003 - 3D Realms Entertainment
//
//This file is part of Duke Nukem 3D version 1.5 - Atomic Edition
//
//Duke Nukem 3D is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
//See the GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//
//Original Source: 1996 - Todd Replogle
//Prepared for public release: 03/21/2003 - Charlie Wiederhold, 3D Realms
//This file has been modified by Jonathon Fowler (jf@jonof.id.au)
//and Alexander Makarov-[M210] (m210-2007@mail.ru)

package ru.m210projects.Duke3D;

import static java.lang.Math.max;
import static java.lang.Math.min;

import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Strhandler.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Duke3D.Gamedef.*;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.LoadSave.lastload;
import static ru.m210projects.Duke3D.Main.*;
import static ru.m210projects.Duke3D.Premap.*;
import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Duke3D.Screen.*;
import static ru.m210projects.Duke3D.Sector.*;
import static ru.m210projects.Duke3D.Actors.*;
import static ru.m210projects.Duke3D.Weapons.*;
import java.io.File;
import java.util.Arrays;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.Tools.Interpolation.ILoc;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Duke3D.Factory.DukeNetwork;
import ru.m210projects.Duke3D.Menus.InterfaceMenu;
import ru.m210projects.Duke3D.Types.PlayerOrig;
import ru.m210projects.Duke3D.Types.PlayerStruct;

public class View {

	public static final String deathMessage = "or \"ENTER\" to load last saved game";

	public static int oyrepeat = -1;
	private static final char[] buffer = new char[256];

	public static int gPlayerIndex = -1;

	public static final int MAXUSERQUOTES = 4;
	public static int quotebot, quotebotgoal;
	public static short[] user_quote_time = new short[MAXUSERQUOTES];
	public static char[][] user_quote = new char[MAXUSERQUOTES][80];
	public static short fta, ftq, zoom, over_shoulder_on;
	public static int[] loogiex = new int[64], loogiey = new int[64];

	public static int cameradist = 0, cameraclock = 0;
	public static int gNameShowTime;

	public static void adduserquote(char[] daquote) {
		for (int i = MAXUSERQUOTES - 1; i > 0; i--) {
			System.arraycopy(user_quote[i - 1], 0, user_quote[i], 0, 80);
			user_quote_time[i] = user_quote_time[i - 1];
		}
		System.arraycopy(daquote, 0, user_quote[0], 0, Math.min(daquote.length, 80));

		user_quote_time[0] = 180;
	}

	public static void displayloogie(int snum) {
		int i, a, x, y, z;

		if (ps[snum].loogcnt == 0 || snum != screenpeek)
			return;

		y = (ps[snum].loogcnt << 2);
		for (i = 0; i < ps[snum].numloogs; i++) {
			a = klabs(sintable[((ps[snum].loogcnt + i) << 5) & 2047]) >> 5;
			z = 4096 + ((ps[snum].loogcnt + i) << 9);
			x = (int) (-sync[snum].avel) + (sintable[((ps[snum].loogcnt + i) << 6) & 2047] >> 10);

			engine.rotatesprite((loogiex[i] + x) << 16, (200 + loogiey[i] - y) << 16, z - (i << 8), 256 - a, LOOGIE, 0,
					0, 2 | 8, 0, 0, xdim - 1, ydim - 1);
		}
	}

	public static void displayrest(int smoothratio) {
		int a, i, j;

		int cposx, cposy;
		float cang = 0;

		int cr = 0, cg = 0, cb = 0, cf = 0;
		boolean dotint = false;
		PlayerStruct pp = ps[screenpeek];

		if (changepalette != 0) {
			setgamepalette(pp, pp.palette, GLInvalidateFlag.All);
			changepalette = 0;
		}

		if (pp.pals_time > 0 && pp.loogcnt == 0) {
			dotint = true;
			cr = pp.pals[0];
			cg = pp.pals[1];
			cb = pp.pals[2];
			cf = pp.pals_time;

			if (engine.glrender() == null) // software render
				restorepalette = true;
		} else if (restorepalette) {
			setgamepalette(pp, pp.palette, GLInvalidateFlag.All);

			dotint = true;
			cr = cg = cb = 0;
			cf = 0;
			restorepalette = false;
		} else if (pp.loogcnt > 0) {
			dotint = true;
			cr = 0;
			cg = 64;
			cb = 0;
			cf = pp.loogcnt >> 1;
		}

		if (dotint && !game.menu.gShowMenu)
			palto(cr, cg, cb, cf | 128);

		i = pp.cursectnum;

		if (i != -1) {
			show2dsector[i >> 3] |= (1 << (i & 7));

			int startwall = sector[i].wallptr;
			int endwall = startwall + sector[i].wallnum;

			for (j = startwall; j < endwall; j++) {
				WALL wal = wall[j];
				i = wal.nextsector;
				if (i < 0)
					continue;
				if ((wal.cstat & 0x0071) != 0)
					continue;
				if ((wall[wal.nextwall].cstat & 0x0071) != 0)
					continue;
				if (sector[i].lotag == 32767)
					continue;
				if (sector[i].ceilingz >= sector[i].floorz)
					continue;
				show2dsector[i >> 3] |= (1 << (i & 7));
			}
		}

		if (ud.camerasprite == -1) {
			if (ud.overhead_on != 2) {
				if (pp.newowner >= 0) {
					cameratext(pp.newowner);
				} else {
					displayweapon(screenpeek);
					displayloogie(screenpeek);
					if (over_shoulder_on == 0)
						displaymasks(screenpeek);
				}
				moveclouds();
			}

			if (ud.overhead_on > 0) {
				if (!ud.scrollmode) {
					if (pp.newowner == -1) {
						if (screenpeek == myconnectindex && numplayers > 1) {
							DukeNetwork net = game.net;
							cposx = net.predictOld.x + mulscale((net.predict.x - net.predictOld.x), smoothratio, 16);
							cposy = net.predictOld.y + mulscale((net.predict.y - net.predictOld.y), smoothratio, 16);
							cang = net.predictOld.ang
									+ (BClampAngle(net.predict.ang + 1024 - net.predictOld.ang) - 1024) * smoothratio
											/ 65536.0f;
						} else {
							cposx = pp.prevView.x + mulscale((pp.posx - pp.prevView.x), smoothratio, 16);
							cposy = pp.prevView.y + mulscale((pp.posy - pp.prevView.y), smoothratio, 16);
							cang = pp.prevView.ang
									+ (BClampAngle(pp.ang + 1024 - pp.prevView.ang) - 1024) * smoothratio / 65536.0f;
						}
					} else {
						cposx = pp.oposx;
						cposy = pp.oposy;
						cang = pp.oang;
					}
				} else {
					ud.fola += ud.folavel / 8f;
					ud.folx += (ud.folfvel * sintable[(512 + 2048 - ud.fola) & 2047]) >> 14;
					ud.foly += (ud.folfvel * sintable[(512 + 1024 - 512 - ud.fola) & 2047]) >> 14;

					cposx = ud.folx;
					cposy = ud.foly;
					cang = ud.fola;
				}

				if (ud.overhead_on == 2) {
					engine.clearview(0);
					engine.drawmapview(cposx, cposy, zoom, (short) cang);
				}
				engine.drawoverheadmap(cposx, cposy, zoom, (short) cang);

				if (ud.overhead_on == 2) {
					if (ud.screen_size > 0)
						a = 147;
					else
						a = 182;

					Arrays.fill(buffer, (char) 0);
					buildString(buffer, 0, currentGame.episodes[ud.volume_number].Title);
					game.getFont(0).drawText(5, a, buffer, -128, 0, TextAlign.Left, 2 + 8 + 16 + 256, false);
					Arrays.fill(buffer, (char) 0);
					if (currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number] != null)
						buildString(buffer, 0, currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number].title);
					game.getFont(0).drawText(5, a + 6, buffer, -128, 0, TextAlign.Left, 2 + 8 + 16 + 256, false);

					if (cfg.gShowStat != 0) {
						int k = (int) (2.5f * game.getFont(1).getHeight() * cfg.gStatSize / 65536.0f) - 5;
						if (ud.coop != 1 && ud.screen_size > 0 && ud.multimode > 1) {
							j = 0;
							k = 8;
							for (i = connecthead; i >= 0; i = connectpoint2[i])
								if (i > j)
									j = i;

							if (j >= 4 && j <= 8)
								k += 8;
							else if (j > 8 && j <= 12)
								k += 16;
							else if (j > 12)
								k += 24;
						}
						viewDrawStats(10, 13 + k, cfg.gStatSize);
					}
				}
			}
		}

		if (game.menu.gShowMenu && !(game.menu.getCurrentMenu() instanceof InterfaceMenu))
			return;

		if (ps[myconnectindex].newowner == -1 && ud.overhead_on == 0 && ud.crosshair != 0 && ud.camerasprite == -1)
			engine.rotatesprite((160 - (ps[myconnectindex].look_ang >> 1)) << 16, 100 << 16, cfg.gCrossSize, 0,
					CROSSHAIR, 0, 0, 2 + 1, 0, 0, xdim, ydim);

		coolgaugetext(screenpeek);
		operatefta();

		if (fta > 1 && sprite[ps[myconnectindex].i].extra <= 0 && myconnectindex == screenpeek && ud.multimode < 2
				&& lastload != null && !lastload.isEmpty() && ud.recstat != 2
				&& BuildGdx.compat.checkFile(lastload, Path.User) != null) {
			int k = getftacoord();
			game.getFont(1).drawText(320 >> 1, k + 10, deathMessage, 0, 0, TextAlign.Center, 2 + 8 + 16, false);
		}

		if (ud.screen_size > 0 && cfg.gShowStat == 1 && ud.overhead_on != 2) {
			int y = 192;
			if (ud.screen_size >= 2)
				y = 162;
			if (ud.screen_size == 1)
				y = 168;
			viewDrawStats(10, y, cfg.gStatSize);
		}

		if (game.isCurrentScreen(gGameScreen) && totalclock < gNameShowTime) {
			int transp = 0;
			if (totalclock > gNameShowTime - 20)
				transp = 1;
			if (totalclock > gNameShowTime - 10)
				transp = 33;

			if (cfg.showMapInfo != 0 && !game.menu.gShowMenu) {
				if (mUserFlag != UserFlag.UserMap || boardfilename == null) {
					if (ud.volume_number < nMaxEpisodes
							&& currentGame.episodes[ud.volume_number] != null
							&& currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number] != null
							&& currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number].title != null)
						game.getFont(2).drawText(160, 114,
								currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number].title, -128, 0,
								TextAlign.Center, 2 | transp, false);
				} else {
					Arrays.fill(buffer, (char) 0);
					int index = boardfilename.lastIndexOf(File.separator);
					boardfilename.getChars(index + 1, boardfilename.length(), buffer, 0);
					game.getFont(2).drawText(160, 114, buffer, -128, 0, TextAlign.Center, 2 | transp, false);
				}
			}
		}

		if (MODE_TYPE)
			typemode();

		if (game.gPaused && !game.menu.gShowMenu) {
			game.getFont(2).drawText(160, 100, "GAME PAUSED", 0, 0, TextAlign.Center, 2 + 8 + 16, false);
		}

		if (gPlayerIndex != -1 && gPlayerIndex != myconnectindex) {
			int len = 0;
			if (ud.user_name[gPlayerIndex] == null || ud.user_name[gPlayerIndex].isEmpty())
				len = buildString(buf, 0, "Player ", gPlayerIndex + 1);
			else
				len = buildString(buf, 0, ud.user_name[gPlayerIndex]);
			len = buildString(buf, len, " (", ps[gPlayerIndex].last_extra);
			len = buildString(buf, len, "hp)");

			int shade = 16 - (totalclock & 0x3F);

			int y = scale(windowy1, 200, ydim) + 100;
			if (ud.screen_size <= 3)
				y += engine.getTile(BOTTOMSTATUSBAR).getHeight() / 2;

			game.getFont(1).drawText(160, y, buf, shade, 0, TextAlign.Center, 2 + 8 + 16, false);
		}

		if (ud.coords != 0)
			coords(screenpeek);
	}

	public static void typemode() {
		if (Console.IsShown())
			return;

		int j = 200 - 8;
		if (ud.screen_size > 0)
			j = 200 - 45;

		char[] buf = getInput().getMessageBuffer();
		int len = getInput().getMessageLength() + 1;
		if (len < buf.length)
			buf[len] = 0;
		int alignx = game.getFont(1).drawText(320 >> 1, j, getInput().getMessageBuffer(), 0, 0, TextAlign.Center,
				2 + 8 + 16, false);
		engine.rotatesprite((((320 + alignx + 16) >> 1)) << 16, (j + 3) << 16, 32768, 0,
				SPINNINGNUKEICON + (((totalclock >> 3)) % 7), 0, 0, 10, 0, 0, xdim - 1, ydim - 1);
	}

	public static int getftacoord() {
		int k = 0;

		if (ud.screen_size > 0 && ud.multimode > 1) {
			int j = 0;
			k = 8;
			for (int i = connecthead; i >= 0; i = connectpoint2[i])
				if (i > j)
					j = i;

			if (j >= 4 && j <= 8)
				k += 8;
			else if (j > 8 && j <= 12)
				k += 16;
			else if (j > 12)
				k += 24;
		} else
			k = 0;

		if (ftq == 115 || ftq == 116) {
			k = quotebot;
			for (int i = 0; i < MAXUSERQUOTES; i++) {
				if (user_quote_time[i] <= 0)
					break;
				k -= 8;
			}
			k -= 4;
		}

		return k;
	}

	public static void operatefta() {
		int i, j, k;

		if (ud.screen_size > 0)
			j = 200 - 45;
		else
			j = 200 - 8;
		quotebot = Math.min(quotebot, j);
		quotebotgoal = Math.min(quotebotgoal, j);
		if (MODE_TYPE)
			j -= 8;
		quotebotgoal = j;
		j = quotebot;
		for (i = 0; i < MAXUSERQUOTES; i++) {
			k = user_quote_time[i];
			if (k <= 0)
				break;
			if (k > 4)
				game.getFont(1).drawText(320 >> 1, j, user_quote[i], 0, 0, TextAlign.Center, 2 + 8 + 16, false);
			else if (k > 2)
				game.getFont(1).drawText(320 >> 1, j, user_quote[i], 0, 0, TextAlign.Center, 2 + 8 + 16 + 1, false);
			else
				game.getFont(1).drawText(320 >> 1, j, user_quote[i], 0, 0, TextAlign.Center, 2 + 8 + 16 + 1 + 32,
						false);
			j -= 8;
		}

		if (fta <= 1)
			return;

		k = getftacoord();

		j = fta;
		if (j > 4)
			game.getFont(1).drawText(320 >> 1, k, currentGame.getCON().fta_quotes[ftq], 0, 0, TextAlign.Center,
					2 + 8 + 16, false);
		else if (j > 2)
			game.getFont(1).drawText(320 >> 1, k, currentGame.getCON().fta_quotes[ftq], 0, 0, TextAlign.Center,
					2 + 8 + 16 + 1, false);
		else
			game.getFont(1).drawText(320 >> 1, k, currentGame.getCON().fta_quotes[ftq], 0, 0, TextAlign.Center,
					2 + 8 + 16 + 1 + 32, false);
	}

	public static void displayfragbar(int yoffset, boolean showpalette) {
		int row = (ud.multimode - 1) / 4;
		if (row >= 0) {
//			int framesx = xdim / tilesizx[BIGHOLE];
//			int framesy = mulscale(tilesizy[FRAGBAR] * (row + 1), divscale(ydim, 200, 16), 16);
//			int x = 0;
//			for(int i = 0; i <= framesx; i++) {
//		    	engine.rotatesprite(x<<16, 0, 0x10000, 0, BIGHOLE, 0, 0, 8 | 16 | 256, 0, 0, xdim-1, framesy);
//		    	x += tilesizx[BIGHOLE];
//		    }

			if (yoffset > 0)
				yoffset -= 9 * row;
			for (int r = 0; r <= row; r++)
				engine.rotatesprite(0, yoffset + (r * engine.getTile(FRAGBAR).getHeight()) << 16, 65600, 0, FRAGBAR, 0,
						0, 2 + 8 + 16 + 64, 0, 0, xdim - 1, ydim - 1);

			for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
				if (ud.user_name[i] == null || ud.user_name[i].isEmpty())
					buildString(buffer, 0, "Player ", i + 1);
				else
					buildString(buffer, 0, ud.user_name[i]);

				game.getFont(0).drawText(21 + (73 * (i & 3)), yoffset + 2 + ((i & 28) << 1), buffer, 0,
						showpalette ? sprite[ps[i].i].pal : 0, TextAlign.Left, 10 | 16, false);
				buildString(buffer, 0, "", ps[i].frag - ps[i].fraggedself);
				game.getFont(0).drawText(17 + 50 + (73 * (i & 3)), yoffset + 2 + ((i & 28) << 1), buffer, 0,
						showpalette ? sprite[ps[i].i].pal : 0, TextAlign.Left, 10 | 16, false);
			}
		}
	}

	public static void debuginfo(int x, int y) {
//		int oy = y;
		buildString(buffer, 0, "totalclock= ", totalclock);
		engine.printext256(x, y, 31, -1, buffer, 0, 1.0f);
		y += 10;

		buildString(buffer, 0, "global_random= ", global_random);
		engine.printext256(x, y, 31, -1, buffer, 0, 1.0f);
		y += 10;

		buildString(buffer, 0, "randomseed= ", engine.getrand());
		engine.printext256(x, y, 31, -1, buffer, 0, 1.0f);
		y += 10;

		buildString(buffer, 0, "posx= ", ps[0].posx);
		engine.printext256(x, y, 31, -1, buffer, 0, 1.0f);
		y += 10;

		buildString(buffer, 0, "posy= ", ps[0].posy);
		engine.printext256(x, y, 31, -1, buffer, 0, 1.0f);
		y += 10;

		buildString(buffer, 0, "posz= ", ps[0].posz);
		engine.printext256(x, y, 31, -1, buffer, 0, 1.0f);
		y += 10;

		buildString(buffer, 0, "ang= ", Float.toString(ps[0].ang));
		engine.printext256(x, y, 31, -1, buffer, 0, 1.0f);
		y += 10;

		buildString(buffer, 0, "horiz= ", Float.toString(ps[0].horiz));
		engine.printext256(x, y, 31, -1, buffer, 0, 1.0f);
		y += 10;

		buildString(buffer, 0, "xvel= ", Float.toString(ps[0].posxv));
		engine.printext256(x, y, 31, -1, buffer, 0, 1.0f);
		y += 10;

		buildString(buffer, 0, "yvel= ", Float.toString(ps[0].posyv));
		engine.printext256(x, y, 31, -1, buffer, 0, 1.0f);
		y += 10;

		buildString(buffer, 0, "ps[0].auto_aim= ", ps[0].auto_aim);
		engine.printext256(x, y, 31, -1, buffer, 0, 1.0f);
		y += 10;
		buildString(buffer, 0, "ps[1].auto_aim= ", ps[1].auto_aim);
		engine.printext256(x, y, 31, -1, buffer, 0, 1.0f);
		y += 10;

		/*
		 * buildString(buffer, 0, "movefifoplc= ", movefifoplc);
		 * engine.printext256(x,y,31,-1,buffer,0); y += 10;
		 *
		 * buildString(buffer, 0, "movefifoend= ", movefifoend[myconnectindex]);
		 * engine.printext256(x,y,31,-1,buffer,0); y += 10;
		 *
		 * buildString(buffer, 0, "bufferjitter= ", bufferjitter);
		 * engine.printext256(x,y,31,-1,buffer,0); y += 10;
		 *
		 * buildString(buffer, 0, "ps[0].auto_aim= ", ps[0].auto_aim);
		 * engine.printext256(x,y,31,-1,buffer,0); y += 10; buildString(buffer, 0,
		 * "ps[1].auto_aim= ", ps[1].auto_aim); engine.printext256(x,y,31,-1,buffer,0);
		 * y += 10;
		 *
		 *
		 * buildString(buffer, 0, "syncvalhead= ", syncvalhead[myconnectindex]);
		 * engine.printext256(x,y,31,-1,buffer,0); y += 10;
		 *
		 * buildString(buffer, 0, "syncvaltail= ", syncvaltail);
		 * engine.printext256(x,y,31,-1,buffer,0); y += 10;
		 *
		 * buildString(buffer, 0, "syncvaltottail= ", syncvaltottail);
		 * engine.printext256(x,y,31,-1,buffer,0); y += 10;
		 *
		 * buildString(buffer, 0, "syncval[myconnectindex]= ",
		 * syncval[myconnectindex][syncvaltail&(MOVEFIFOSIZ-1)]);
		 * engine.printext256(x,y,31,-1,buffer,0); y += 10;
		 *
		 * buildString(buffer, 0, "syncval[connecthead]= ",
		 * syncval[connecthead][syncvaltail&(MOVEFIFOSIZ-1)]);
		 * engine.printext256(x,y,31,-1,buffer,0); y += 10;
		 *
		 * buildString(buffer, 0, "global_random= ", global_random);
		 * engine.printext256(x,y,31,-1,buffer,0); y += 10;
		 *
		 * buildString(buffer, 0, "randomseed= ", engine.getrand());
		 * engine.printext256(x,y,31,-1,buffer,0); y += 10;
		 *
		 * buildString(buffer, 0, "syncvalhead[connecthead]= ",
		 * syncvalhead[connecthead]); engine.printext256(x,y,31,-1,buffer,0); y += 10;
		 *
		 * y = oy; x = 280; for(int i = syncvaltail - 5; i < syncvaltail + 5; i++) {
		 * buildString(buffer, 0, "syncval[connecthead]= ",
		 * syncval[connecthead][i&(MOVEFIFOSIZ-1)]); int pal = 31; if(i == syncvaltail)
		 * pal = 30; engine.printext256(x,y,pal,-1,buffer,0); y += 10; }
		 */
	}

	public static void coolgaugetext(int snum) // HUD
	{
		int i = 0, j, o, ss;

		PlayerStruct p = ps[snum];

//	    debuginfo(20, 40);

		if (p.invdisptime > 0)
			displayinventory(p);

		if (screenpeek != myconnectindex) {
			if (ud.user_name[screenpeek] == null || ud.user_name[screenpeek].isEmpty())
				buildString(buf, 0, "View from player ", screenpeek + 1);
			else
				buildString(buf, 0, "View from ", ud.user_name[screenpeek]);
			int shade = 16 - (totalclock & 0x3F);

			game.getFont(1).drawText(160, scale(windowy1, 200, ydim) + 10, buf, shade, 0, TextAlign.Center, 10 | 16,
					false);
		}

		ss = ud.screen_size;
		if (ss < 1)
			return;

		if ((ud.multimode > 1 || mFakeMultiplayer)) {
			displayfragbar(0, true);
		}

		if (ss == 1) // DRAW MINI STATUS BAR:
		{
			engine.rotatesprite(5 << 16, (200 - 28) << 16, 65536, 0, HEALTHBOX, 0, 21, 10 + 16 + 256, 0, 0, xdim - 1,
					ydim - 1);

			if (sprite[p.i].pal == 1 && p.last_extra < 2)
				digitalnumber(20, 200 - 17, 1, -16, 10 + 16 + 256);
			else
				digitalnumber(20, 200 - 17, p.last_extra, -16, 10 + 16 + 256);

			int x = 37;
			if (p.curr_weapon == HANDREMOTE_WEAPON)
				i = HANDBOMB_WEAPON;
			else
				i = p.curr_weapon;
			if (p.ammo_amount[i] != 0) {
				engine.rotatesprite(x << 16, (200 - 28) << 16, 65536, 0, AMMOBOX, 0, 21, 10 + 16 + 256, 0, 0, xdim - 1,
						ydim - 1);
				digitalnumber(x + 16, 200 - 17, p.ammo_amount[i], -16, 10 + 16 + 256);
				x += engine.getTile(AMMOBOX).getWidth() + 1;
			}

			if (p.shield_amount != 0) {
				engine.rotatesprite(x << 16, (200 - 28) << 16, 65536, 0, HUDARMOR, 0, 21, 10 + 16 + 256, 0, 0, xdim - 1,
						ydim - 1);
				digitalnumber(x + 16, 200 - 17, p.shield_amount, -16, 10 + 16 + 256);
				x += engine.getTile(HUDARMOR).getWidth() + 1;
			}

			if (p.got_access != 0) {

				engine.rotatesprite(x << 16, (200 - 28) << 16, 65536, 0, HUDKEYS, 0, 21, 10 + 16 + 256, 0, 0, xdim - 1,
						ydim - 1);
				if ((p.got_access & 4) != 0)
					engine.rotatesprite(x + 5 << 16, 182 << 16, 65536, 0, ACCESS_ICON, 0, 23, 10 + 16 + 256, 0, 0,
							xdim - 1, ydim - 1);
				if ((p.got_access & 2) != 0)
					engine.rotatesprite(x + 18 << 16, 182 << 16, 65536, 0, ACCESS_ICON, 0, 21, 10 + 16 + 256, 0, 0,
							xdim - 1, ydim - 1);
				if ((p.got_access & 1) != 0)
					engine.rotatesprite(x + 11 << 16, 189 << 16, 65536, 0, ACCESS_ICON, 0, 0, 10 + 16 + 256, 0, 0,
							xdim - 1, ydim - 1);
				x += engine.getTile(HUDKEYS).getWidth() + 1;
			}

			o = 158;
			if (p.inven_icon != 0) {
				engine.rotatesprite(x << 16, (200 - 30) << 16, 65536, 0, INVENTORYBOX, 0, 21, 10 + 16 + 256, 0, 0,
						xdim - 1, ydim - 1);

				switch (p.inven_icon) {
				case 1:
					i = FIRSTAID_ICON;
					break;
				case 2:
					i = STEROIDS_ICON;
					break;
				case 3:
					i = HOLODUKE_ICON;
					break;
				case 4:
					i = JETPACK_ICON;
					break;
				case 5:
					i = HEAT_ICON;
					break;
				case 6:
					i = AIRTANK_ICON;
					break;
				case 7:
					i = BOOT_ICON;
					break;
				default:
					i = -1;
				}

				if (i >= 0)
					engine.rotatesprite((x + 4) << 16, (200 - 21) << 16, 65536, 0, i, 0, 0, 10 + 16 + 256, 0, 0,
							xdim - 1, ydim - 1);

				game.getFont(0).drawChar((x + 35), 190, '%', 0, 6, 10 | 16 | 256, false);
				j = 0x80000000;
				switch (p.inven_icon) {
				case 1:
					i = p.firstaid_amount;
					break;
				case 2:
					i = ((p.steroids_amount + 3) >> 2);
					break;
				case 3:
					i = ((p.holoduke_amount + 15) / 24);
					j = p.holoduke_on;
					break;
				case 4:
					i = ((p.jetpack_amount + 15) >> 4);
					j = p.jetpack_on;
					break;
				case 5:
					i = p.heat_amount / 12;
					j = p.heat_on;
					break;
				case 6:
					i = ((p.scuba_amount + 63) >> 6);
					break;
				case 7:
					i = (p.boot_amount >> 1);
					break;
				}
				invennum(x + 27, 200 - 6, (char) i, 0, 10 + 256);

				if (j > 0) {
					game.getFont(0).drawText(x + 31, 180, "ON", 0, 2, TextAlign.Left, 10 | 16 | 256, false);
				} else if (j != 0x80000000) {
					game.getFont(0).drawText(x + 27, 180, "OFF", 0, 2, TextAlign.Left, 10 | 16 | 256, false);
				}

				if (p.inven_icon >= 6) {
					game.getFont(0).drawText(x + 22, 180, "AUTO", 0, 2, TextAlign.Left, 10 | 16 | 256, false);
				}
			}

			return;
		} else if (ss == 2) {
			int x = 261;
			int y = 183;

			engine.rotatesprite(x << 16, y << 16, (1 << 16), 0, ALTHUDRIGHT, 0, 0, 10 | 512, 0, 0, xdim - 1, ydim - 1);

			if (ud.multimode > 1 && ud.coop != 1) {
				digitalnumber(x + 39, y, max(p.frag - p.fraggedself, 0), -16, 10 + 16 + 512);
			} else {
				if ((p.got_access & 4) != 0)
					engine.rotatesprite(x + 26 << 16, y - 1 << 16, 65536, 0, ACCESS_ICON, 0, 23, 10 + 16 + 512, 0, 0,
							xdim - 1, ydim - 1);
				if ((p.got_access & 2) != 0)
					engine.rotatesprite(x + 39 << 16, y - 1 << 16, 65536, 0, ACCESS_ICON, 0, 21, 10 + 16 + 512, 0, 0,
							xdim - 1, ydim - 1);
				if ((p.got_access & 1) != 0)
					engine.rotatesprite(x + 32 << 16, y + 6 << 16, 65536, 0, ACCESS_ICON, 0, 0, 10 + 16 + 512, 0, 0,
							xdim - 1, ydim - 1);
			}

			if (p.inven_icon != 0) {
				o = 0;

				x += 12;
				switch (p.inven_icon) {
				case 1:
					i = FIRSTAID_ICON;
					break;
				case 2:
					i = STEROIDS_ICON;
					break;
				case 3:
					i = HOLODUKE_ICON;
					break;
				case 4:
					i = JETPACK_ICON;
					break;
				case 5:
					i = HEAT_ICON;
					break;
				case 6:
					i = AIRTANK_ICON;
					break;
				case 7:
					i = BOOT_ICON;
					break;
				}
				engine.rotatesprite((x - 30 - o) << 16, y - 4 << 16, 65536, 0, i, 0, 0, 10 + 16 + 512, 0, 0, xdim - 1,
						ydim - 1);

				game.getFont(0).drawChar(x + 1 - o, y + 7, '%', 0, 6, 10 | 16 | 512, false);
				if (p.inven_icon >= 6) {
					game.getFont(0).drawText(x - 12 - o, y - 3, "AUTO", 0, 2, TextAlign.Left, 10 | 16 | 512, false);
				}

				switch (p.inven_icon) {
				case 3:
					j = p.holoduke_on;
					break;
				case 4:
					j = p.jetpack_on;
					break;
				case 5:
					j = p.heat_on;
					break;
				default:
					j = 0x80000000;
				}
				if (j > 0) {
					game.getFont(0).drawText(x - 3 - o, y - 3, "ON", 0, 2, TextAlign.Left, 10 | 16 | 512, false);
				} else if (j != 0x80000000) {
					game.getFont(0).drawText(x - 7 - o, y - 3, "OFF", 0, 2, TextAlign.Left, 10 | 16 | 512, false);
				}

				switch (p.inven_icon) {
				case 1:
					i = p.firstaid_amount;
					break;
				case 2:
					i = ((p.steroids_amount + 3) >> 2);
					break;
				case 3:
					i = ((p.holoduke_amount + 15) / 24);
					break;
				case 4:
					i = ((p.jetpack_amount + 15) >> 4);
					break;
				case 5:
					i = p.heat_amount / 12;
					break;
				case 6:
					i = ((p.scuba_amount + 63) >> 6);
					break;
				case 7:
					i = (p.boot_amount >> 1);
					break;
				}
				invennum(x - 7 - o, y + 11, i, 0, 10 + 512);
			}

			x = 68;
			y = 183;

			engine.rotatesprite(x << 16, y << 16, (1 << 16), 0, ALTHUDLEFT, 0, 0, 10 | 256, 0, 0, xdim - 1, ydim - 1);

			if (sprite[p.i].pal == 1 && p.last_extra < 2)
				digitalnumber(x - 49, y, 1, -16, 10 + 16 + 256);
			else
				digitalnumber(x - 49, y, p.last_extra, -16, 10 + 16 + 256);

			if (p.shield_amount != 0)
				digitalnumber(x - 17, y, p.shield_amount, -16, 10 + 16 + 256);

			if (p.curr_weapon == HANDREMOTE_WEAPON)
				i = HANDBOMB_WEAPON;
			else
				i = p.curr_weapon;
			if (p.ammo_amount[i] != 0) {
				digitalnumber(x + 16, y, p.ammo_amount[i], -16, 10 + 16 + 256);
			}

			return;
		}

		// DRAW/UPDATE FULL STATUS BAR:
		patchstatusbar(0, 0, 320, 200);
		if (ud.multimode > 1 && ud.coop != 1)
			engine.rotatesprite(278 << 16, (200 - 28) << 16, 65536, 0, KILLSICON, 0, 0, 10 + 16, 0, 0, xdim - 1,
					ydim - 1);

		if (ud.multimode > 1 && ud.coop != 1) {
			digitalnumber(287, 200 - 17, max(p.frag - p.fraggedself, 0), -16, 10 + 16);
		} else {
			if ((p.got_access & 4) != 0)
				engine.rotatesprite(275 << 16, 182 << 16, 65536, 0, ACCESS_ICON, 0, 23, 10 + 16, 0, 0, xdim - 1,
						ydim - 1);
			if ((p.got_access & 2) != 0)
				engine.rotatesprite(288 << 16, 182 << 16, 65536, 0, ACCESS_ICON, 0, 21, 10 + 16, 0, 0, xdim - 1,
						ydim - 1);
			if ((p.got_access & 1) != 0)
				engine.rotatesprite(281 << 16, 189 << 16, 65536, 0, ACCESS_ICON, 0, 0, 10 + 16, 0, 0, xdim - 1,
						ydim - 1);
		}
		weapon_amounts(p, 96, 182);

		if (sprite[p.i].pal == 1 && p.last_extra < 2)
			digitalnumber(32, 200 - 17, 1, -16, 10 + 16);
		else
			digitalnumber(32, 200 - 17, p.last_extra, -16, 10 + 16);

		digitalnumber(64, 200 - 17, p.shield_amount, -16, 10 + 16);

		if (p.curr_weapon != KNEE_WEAPON) {
			if (p.curr_weapon == HANDREMOTE_WEAPON)
				i = HANDBOMB_WEAPON;
			else
				i = p.curr_weapon;
			digitalnumber(230 - 22, 200 - 17, p.ammo_amount[i], -16, 10 + 16);
		}

		if (p.inven_icon != 0) {
			o = 0;

			switch (p.inven_icon) {
			case 1:
				i = FIRSTAID_ICON;
				break;
			case 2:
				i = STEROIDS_ICON;
				break;
			case 3:
				i = HOLODUKE_ICON;
				break;
			case 4:
				i = JETPACK_ICON;
				break;
			case 5:
				i = HEAT_ICON;
				break;
			case 6:
				i = AIRTANK_ICON;
				break;
			case 7:
				i = BOOT_ICON;
				break;
			}
			engine.rotatesprite((231 - o) << 16, (200 - 21) << 16, 65536, 0, i, 0, 0, 10 + 16, 0, 0, xdim - 1,
					ydim - 1);
			game.getFont(0).drawChar(292 - 30 - o, 190, '%', 0, 6, 10 | 16, false);
			if (p.inven_icon >= 6) {
				game.getFont(0).drawText(284 - 35 - o, 180, "AUTO", 0, 2, TextAlign.Left, 10 | 16, false);
			}

			switch (p.inven_icon) {
			case 3:
				j = p.holoduke_on;
				break;
			case 4:
				j = p.jetpack_on;
				break;
			case 5:
				j = p.heat_on;
				break;
			default:
				j = 0x80000000;
			}
			if (j > 0) {
				game.getFont(0).drawText(288 - 30 - o, 180, "ON", 0, 2, TextAlign.Left, 10 | 16, false);
			} else if (j != 0x80000000) {
				game.getFont(0).drawText(284 - 30 - o, 180, "OFF", 0, 2, TextAlign.Left, 10 | 16, false);
			}

			switch (p.inven_icon) {
			case 1:
				i = p.firstaid_amount;
				break;
			case 2:
				i = ((p.steroids_amount + 3) >> 2);
				break;
			case 3:
				i = ((p.holoduke_amount + 15) / 24);
				break;
			case 4:
				i = ((p.jetpack_amount + 15) >> 4);
				break;
			case 5:
				i = p.heat_amount / 12;
				break;
			case 6:
				i = ((p.scuba_amount + 63) >> 6);
				break;
			case 7:
				i = (p.boot_amount >> 1);
				break;
			}
			invennum(284 - 30 - o, 200 - 6, (char) i, 0, 10);

		}
	}

	public static void displayrooms(int snum, int smoothratio) {
		int dst, j, fz, cz;
		short k;
		int tposx, tposy, i;
		short tang;

		PlayerStruct p = ps[snum];

		gPlayerIndex = -1;
//	    if(Gdx.input.isKeyPressed(Keys.E))
//	    {
//	    	int zvel = (int)(100-ps[0].horiz-ps[0].horizoff)<<5;
//	    	engine.hitscan(ps[0].posx,ps[0].posy,ps[0].posz,ps[0].cursectnum,
//	    			sintable[((int)ps[0].ang+512)&2047],
//	    			sintable[(int)ps[0].ang&2047],zvel<<6,
//	    			pHitInfo,CLIPMASK0);
//
//	    	if(pHitInfo.hitsprite != -1)
//	    	{
//	    		System.err.println(pHitInfo.hitsprite);
//	    	}
//	    }

		if ((!game.menu.gShowMenu && ud.overhead_on == 2) || game.menu.isOpened(game.menu.mMenus[HELP])
				|| p.cursectnum == -1)
			return;

		visibility = gVisibility;

		int cposx = p.posx;
		int cposy = p.posy;
		int cposz = p.posz;
		float cang = p.ang;
		float choriz = p.horiz + p.horizoff;
		short sect = p.cursectnum;

		if (sect < 0 || sect >= MAXSECTORS)
			return;

		animatecamsprite();

		if (ud.camerasprite >= 0) {
			SPRITE s = sprite[ud.camerasprite];

			if (s.yvel < 0)
				s.yvel = -100;
			else if (s.yvel > 199)
				s.yvel = 300;

			cang = (short) (hittype[ud.camerasprite].tempang
					+ mulscale((((s.ang + 1024 - hittype[ud.camerasprite].tempang) & 2047) - 1024), smoothratio, 16));

			se40code(s.x, s.y, s.z, cang, s.yvel, smoothratio);

			engine.drawrooms(s.x, s.y, s.z - (4 << 8), cang, s.yvel, s.sectnum);
			animatesprites(s.x, s.y, s.z - (4 << 8), (short) cang, smoothratio);
			engine.drawmasks();
		} else {
			i = divscale(1, sprite[p.i].yrepeat + 28, 22);
			if (i != oyrepeat) {
				oyrepeat = i;
				vscrn(ud.screen_size);
			}

			if ((snum == myconnectindex) && (numplayers > 1)) {
				DukeNetwork net = game.net;
				cposx = net.predictOld.x + mulscale((net.predict.x - net.predictOld.x), smoothratio, 16);
				cposy = net.predictOld.y + mulscale((net.predict.y - net.predictOld.y), smoothratio, 16);
				cposz = net.predictOld.z + mulscale((net.predict.z - net.predictOld.z), smoothratio, 16);
				cang = net.predictOld.ang
						+ (BClampAngle(net.predict.ang + 1024 - net.predictOld.ang) - 1024) * smoothratio / 65536.0f;
				cang += net.predictOld.lookang
						+ (BClampAngle(net.predict.lookang + 1024 - net.predictOld.lookang) - 1024) * smoothratio
								/ 65536.0f;
				choriz = net.predictOld.horiz + net.predictOld.horizoff
						+ (((net.predict.horiz + net.predict.horizoff - net.predictOld.horiz - net.predictOld.horizoff)
								* smoothratio) / 65536.0f);
				sect = net.predict.sectnum;

				if ((ud.screen_tilting != 0 && p.rotscrnang != 0)) {
					tang = p.rotscrnang;
					engine.getrender()
							.settiltang(net.predictOld.rotscrnang + mulscale(
									((net.predict.rotscrnang - net.predictOld.rotscrnang + 1024) & 2047) - 1024,
									smoothratio, 16));
				} else
					engine.getrender().settiltang(0);
			} else {
				cposx = p.prevView.x + mulscale((cposx - p.prevView.x), smoothratio, 16);
				cposy = p.prevView.y + mulscale((cposy - p.prevView.y), smoothratio, 16);
				cposz = p.prevView.z + mulscale((cposz - p.prevView.z), smoothratio, 16);
				cang = p.prevView.ang + (BClampAngle(cang + 1024 - p.prevView.ang) - 1024) * smoothratio / 65536.0f;
				cang += p.prevView.lookang
						+ (BClampAngle(p.look_ang + 1024 - p.prevView.lookang) - 1024) * smoothratio / 65536.0f;
				choriz = (p.prevView.horiz + p.prevView.horizoff
						+ ((choriz - p.prevView.horiz - p.prevView.horizoff) * smoothratio) / 65536.0f);

				if ((ud.screen_tilting != 0 && p.rotscrnang != 0)) {
					tang = p.rotscrnang;
					engine.getrender().settiltang(p.prevView.rotscrnang
							+ mulscale(((p.rotscrnang - p.prevView.rotscrnang + 1024) & 2047) - 1024, smoothratio, 16));
				} else
					engine.getrender().settiltang(0);
			}

			if (p.newowner >= 0) {
				cang = hittype[p.i].tempang
						+ (BClampAngle(p.ang + 1024 - hittype[p.i].tempang) - 1024) * smoothratio / 65536.0f;
				choriz = p.horiz + p.horizoff;
				cposx = p.posx;
				cposy = p.posy;
				cposz = p.posz;
				sect = sprite[p.newowner].sectnum;
			} else if (over_shoulder_on == 0) {
				if (numplayers < 2)
					cposz += p.opyoff + mulscale((p.pyoff - p.opyoff), smoothratio, 16);
			} else {
				view(p, cposx, cposy, cposz, sect, cang, choriz);

				cposx = viewout.ox;
				cposy = viewout.oy;
				cposz = viewout.oz;
				sect = viewout.os;
			}

			if (sect < 0 || sect >= MAXSECTORS)
				return;

			cz = hittype[p.i].ceilingz;
			fz = hittype[p.i].floorz;

			if (earthquaketime > 0 && p.on_ground) {
				cposz += 256 - (((earthquaketime) & 1) << 9);
				cang += (2 - ((earthquaketime) & 2)) << 2;
			}

			if (sprite[p.i].pal == 1)
				cposz -= (18 << 8);

			if (p.newowner >= 0)
				choriz = (short) (100 + sprite[p.newowner].shade);
			else if (p.spritebridge == 0) {
				if (cposz < (p.truecz + (4 << 8)))
					cposz = cz + (4 << 8);
				else if (cposz > (p.truefz - (4 << 8)))
					cposz = fz - (4 << 8);
			}

			if (sect >= 0) {
				engine.getzsofslope(sect, cposx, cposy, zofslope);
				if (cposz < zofslope[CEIL] + (4 << 8))
					cposz = zofslope[CEIL] + (4 << 8);
				if (cposz > zofslope[FLOOR] - (4 << 8))
					cposz = zofslope[FLOOR] - (4 << 8);
			}

			if (choriz > 299)
				choriz = 299;
			else if (choriz < -99)
				choriz = -99;

			se40code(cposx, cposy, cposz, cang, choriz, smoothratio);

			if ((gotpic[MIRROR >> 3] & (1 << (MIRROR & 7))) > 0) {
				dst = 0x7fffffff;
				i = 0;
				for (k = 0; k < mirrorcnt; k++) {
					j = klabs(wall[mirrorwall[k]].x - cposx);
					j += klabs(wall[mirrorwall[k]].y - cposy);
					if (j < dst) {
						dst = j;
						i = k;
					}
				}

				if (wall[mirrorwall[i]].overpicnum == MIRROR) {
					engine.preparemirror(cposx, cposy, cposz, cang, choriz, mirrorwall[i], mirrorsector[i]);

					tposx = mirrorx;
					tposy = mirrory;
					tang = (short) mirrorang;

					j = visibility;
					visibility = (j >> 1) + (j >> 2);

					engine.drawrooms(tposx, tposy, cposz, tang, choriz, (short) (mirrorsector[i] + MAXSECTORS));

					display_mirror = 1;
					animatesprites(tposx, tposy, cposz, tang, smoothratio);
					display_mirror = 0;

					engine.drawmasks();
					engine.completemirror(); // Reverse screen x-wise in this function
					visibility = j;
				}
				gotpic[MIRROR >> 3] &= ~(1 << (MIRROR & 7));
			}

			engine.drawrooms(cposx, cposy, cposz, cang, choriz, sect);
			animatesprites(cposx, cposy, cposz, (short) cang, smoothratio);
			engine.drawmasks();
		}
	}

	public static String lastmessage;

	public static void FTA(int q, PlayerStruct p) {
		if (ud.fta_on == 1 && p == ps[screenpeek]) {
			if (fta > 0 && q != 115 && q != 116)
				if (ftq == 115 || ftq == 116)
					return;

			fta = 100;

			if (ftq != q || q == 26) {
				ftq = (short) q;
			}

			if(ftq >= currentGame.getCON().fta_quotes.length) {
				Console.Println("Invalid quote " + ftq, Console.OSDTEXT_RED);
				return;
			}

			int len = 0;
			while (len < currentGame.getCON().fta_quotes[ftq].length
					&& currentGame.getCON().fta_quotes[ftq][++len] != 0)
				;

			String message = new String(currentGame.getCON().fta_quotes[ftq], 0, len);
			if (!message.equals(lastmessage)) {
				Console.Println(message);
				lastmessage = message;
			}
		}
	}

	public static void animatesprites(int x, int y, int z, short a, int smoothratio) {
		short i, j, k, p, sect;
		int l, t1, t3, t4;
		SPRITE s, t;

		for (j = 0; j < spritesortcnt; j++) {
			t = tsprite[j];
			i = t.owner;
			s = sprite[t.owner];

			if(!isValidSector(t.sectnum))
				continue;

//			if(engine.glrender() != null && ps[screenpeek].heat_on != 0) {
//				if((t.picnum >= STARTALPHANUM && t.picnum <= ENDALPHANUM)
//					|| (t.picnum >= (BIGALPHANUM - 10) && t.picnum < BIGAPPOS)
//					|| (t.picnum >= MINIFONT && t.picnum <= MINIFONT + 63)) {
//					t.pal = 6;
//					t.shade -= 10;
//				}
//			} //Nightvision hack

			switch (t.picnum) {
			case DEVELOPERCOMMENTARY:
			case DEVELOPERCOMMENTARY + 1:
				if (!cfg.bDevCommentry)
					t.xrepeat = t.yrepeat = 0;
				break;
			case BLOODPOOL:
			case PUKE:
			case FOOTPRINTS:
			case FOOTPRINTS2:
			case FOOTPRINTS3:
			case FOOTPRINTS4:
				if (t.shade == 127)
					continue;
				break;
			case RESPAWNMARKERRED:
			case RESPAWNMARKERYELLOW:
			case RESPAWNMARKERGREEN:
				if (ud.marker == 0)
					t.xrepeat = t.yrepeat = 0;
				continue;
			case CHAIR3:

				k = (short) ((((t.ang + 3072 + 128 - a) & 2047) >> 8) & 7);
				if (k > 4) {
					k = (short) (8 - k);
					t.cstat |= 4;
				} else
					t.cstat &= ~4;
				t.picnum = (short) (s.picnum + k);
				break;
			case BLOODSPLAT1:
			case BLOODSPLAT2:
			case BLOODSPLAT3:
			case BLOODSPLAT4:
				if (ud.lockout != 0)
					t.xrepeat = t.yrepeat = 0;
				else if (t.pal == 6) {
					t.shade = -127;
					continue;
				}
			case BULLETHOLE:
			case CRACK1:
			case CRACK2:
			case CRACK3:
			case CRACK4:
				t.shade = 16;
				continue;
			case NEON1:
			case NEON2:
			case NEON3:
			case NEON4:
			case NEON5:
			case NEON6:
				continue;
			case GREENSLIME:
			case GREENSLIME + 1:
			case GREENSLIME + 2:
			case GREENSLIME + 3:
			case GREENSLIME + 4:
			case GREENSLIME + 5:
			case GREENSLIME + 6:
			case GREENSLIME + 7:
				break;
			default:
				if (((t.cstat & 16) != 0) || (badguy(t) && t.extra > 0) || t.statnum == 10)
					continue;
			}

			if ((sector[t.sectnum].ceilingstat & 1) != 0)
				l = sector[t.sectnum].ceilingshade;
			else
				l = sector[t.sectnum].floorshade;

			if (l < -127)
				l = -127;
			if (l > 128)
				l = 127;
			t.shade = (byte) l;
		}

		for (j = 0; j < spritesortcnt; j++) // Between drawrooms() and drawmasks()
		{ // is the perfect time to animate sprites
			t = tsprite[j];
			i = t.owner;
			s = sprite[i];

			switch (s.picnum) {
			case SECTOREFFECTOR:
				if (t.lotag == 27 && ud.recstat == 1) {
					t.picnum = (short) (11 + ((totalclock >> 3) & 1));
					t.cstat |= 128;
				} else
					t.xrepeat = t.yrepeat = 0;
				break;
			case NATURALLIGHTNING:
				t.shade = -127;
				break;
			case FEM1:
			case FEM2:
			case FEM3:
			case FEM4:
			case FEM5:
			case FEM6:
			case FEM7:
			case FEM8:
			case FEM9:
			case FEM10:
			case MAN:
			case MAN2:
			case WOMAN:
			case NAKED1:
			case PODFEM1:
			case FEMMAG1:
			case FEMMAG2:
			case FEMPIC1:
			case FEMPIC2:
			case FEMPIC3:
			case FEMPIC4:
			case FEMPIC5:
			case FEMPIC6:
			case FEMPIC7:
			case BLOODYPOLE:
			case FEM6PAD:
			case STATUE:
			case STATUEFLASH:
			case OOZ:
			case OOZ2:
			case WALLBLOOD1:
			case WALLBLOOD2:
			case WALLBLOOD3:
			case WALLBLOOD4:
			case WALLBLOOD5:
			case WALLBLOOD7:
			case WALLBLOOD8:
			case SUSHIPLATE1:
			case SUSHIPLATE2:
			case SUSHIPLATE3:
			case SUSHIPLATE4:
			case FETUS:
			case FETUSJIB:
			case FETUSBROKE:
			case HOTMEAT:
			case FOODOBJECT16:
			case DOLPHIN1:
			case DOLPHIN2:
			case TOUGHGAL:
			case TAMPON:
			case XXXSTACY:
			case 4946:
			case 4947:
			case 693:
			case 2254:
			case 4560:
			case 4561:
			case 4562:
			case 4498:
			case 4957:
				if (ud.lockout != 0) {
					t.xrepeat = t.yrepeat = 0;
					continue;
				}
			}

			if (t.statnum == 99)
				continue;
			if (s.statnum != 1 && s.picnum == APLAYER && ps[s.yvel].newowner == -1 && s.owner >= 0) {
				t.x = ps[s.yvel].oposx + mulscale(ps[s.yvel].posx - ps[s.yvel].oposx, smoothratio, 16);
				t.y = ps[s.yvel].oposy + mulscale(ps[s.yvel].posy - ps[s.yvel].oposy, smoothratio, 16);
				t.z = ps[s.yvel].oposz + mulscale(ps[s.yvel].posz - ps[s.yvel].oposz, smoothratio, 16);
				t.z += (40 << 8);
			} else if ((s.statnum == 0) || s.statnum == 10 || s.statnum == 6 || s.statnum == 4 || s.statnum == 5
					|| s.statnum == 1) {
				// only interpolate certain moving things
				ILoc oldLoc = game.pInt.getsprinterpolate(t.owner);
				if (oldLoc != null) {
					int ox = oldLoc.x;
					int oy = oldLoc.y;
					int oz = oldLoc.z;
					short nAngle = oldLoc.ang;

					// interpolate sprite position
					ox += mulscale(t.x - oldLoc.x, smoothratio, 16);
					oy += mulscale(t.y - oldLoc.y, smoothratio, 16);
					oz += mulscale(t.z - oldLoc.z, smoothratio, 16);
					nAngle += mulscale(((t.ang - oldLoc.ang + 1024) & kAngleMask) - 1024, smoothratio, 16);

					t.x = ox;
					t.y = oy;
					t.z = oz;
					t.ang = nAngle;
				}
			}

			sect = s.sectnum;
			t1 = hittype[i].temp_data[1];
			t3 = hittype[i].temp_data[3];
			t4 = hittype[i].temp_data[4];

			switch (s.picnum) {
			case DUKELYINGDEAD:
				t.z += (24 << 8);
				break;
			case BLOODPOOL:
			case FOOTPRINTS:
			case FOOTPRINTS2:
			case FOOTPRINTS3:
			case FOOTPRINTS4:
				if (t.pal == 6)
					t.shade = -127;
			case PUKE:
			case MONEY:
			case MONEY + 1:
			case MAIL:
			case MAIL + 1:
			case PAPER:
			case PAPER + 1:
				if (ud.lockout != 0 && s.pal == 2) {
					t.xrepeat = t.yrepeat = 0;
					continue;
				}
				break;
			case TRIPBOMB:
				continue;
			case FORCESPHERE:
				if (t.statnum == 5) {
					short sqa, sqb;

					sqa = engine.getangle(sprite[s.owner].x - ps[screenpeek].posx,
							sprite[s.owner].y - ps[screenpeek].posy);
					sqb = engine.getangle(sprite[s.owner].x - t.x, sprite[s.owner].y - t.y);

					if (klabs(getincangle(sqa, sqb)) > 512)
						if (ldist(sprite[s.owner], t) < ldist(sprite[ps[screenpeek].i], sprite[s.owner]))
							t.xrepeat = t.yrepeat = 0;
				}
				continue;
			case BURNING:
			case BURNING2:
				if (sprite[s.owner].statnum == 10) {
					if (display_mirror == 0 && sprite[s.owner].yvel == screenpeek && over_shoulder_on == 0)
						t.xrepeat = 0;
					else {
						t.ang = engine.getangle(x - t.x, y - t.y);
						t.x = sprite[s.owner].x;
						t.y = sprite[s.owner].y;
						t.x += sintable[(t.ang + 512) & 2047] >> 10;
						t.y += sintable[t.ang & 2047] >> 10;
					}
				}
				break;

			case ATOMICHEALTH:
				t.z -= (4 << 8);
				break;
			case CRYSTALAMMO:
				t.shade = (byte) (sintable[(totalclock << 4) & 2047] >> 10);
				continue;
			case VIEWSCREEN:
			case VIEWSCREEN2:
				if (camsprite >= 0 && hittype[sprite[i].owner].temp_data[0] == 1) {
					t.picnum = STATIC;
					t.cstat |= (engine.rand() & 12);
					t.xrepeat += 8;
					t.yrepeat += 8;
				} else if (camsprite >= 0 && engine.getTile(TILE_VIEWSCR).data != null && VIEWSCR_Lock > 200) {
					t.picnum = (short) TILE_VIEWSCR;
				}
				break;

			case SHRINKSPARK:
				t.picnum = (short) (SHRINKSPARK + ((totalclock >> 4) & 3));
				break;
			case GROWSPARK:
				t.picnum = (short) (GROWSPARK + ((totalclock >> 4) & 3));
				break;
			case RPG:
				k = engine.getangle(s.x - x, s.y - y);
				k = (short) (((s.ang + 3072 + 128 - k) & 2047) / 170);
				if (k > 6) {
					k = (short) (12 - k);
					t.cstat |= 4;
				} else
					t.cstat &= ~4;
				t.picnum = (short) (RPG + k);
				break;

			case RECON:

				k = engine.getangle(s.x - x, s.y - y);
				k = (short) (((s.ang + 3072 + 128 - k) & 2047) / 170);

				if (k > 6) {
					k = (short) (12 - k);
					t.cstat |= 4;
				} else
					t.cstat &= ~4;

				if (klabs(t3) > 64)
					k += 7;
				t.picnum = (short) (RECON + k);

				break;

			case APLAYER:

				p = s.yvel;

				if (t.pal == 1)
					t.z -= (18 << 8);

				if (over_shoulder_on > 0 && ps[p].newowner < 0) {
					t.cstat |= 2;
					if (ps[myconnectindex] == ps[p] && numplayers >= 2) {
						DukeNetwork net = game.net;
						t.x = net.predictOld.x + mulscale((net.predict.x - net.predictOld.x), smoothratio, 16);
						t.y = net.predictOld.y + mulscale((net.predict.y - net.predictOld.y), smoothratio, 16);
						t.z = net.predictOld.z + mulscale((net.predict.z - net.predictOld.z), smoothratio, 16)
								+ (40 << 8);
						t.ang = (short) (net.predictOld.ang
								+ (BClampAngle(net.predict.ang + 1024 - net.predictOld.ang) - 1024) * smoothratio
										/ 65536.0f);
						t.sectnum = net.predict.sectnum;
					}
				}

				if ((display_mirror == 1 || screenpeek != p || s.owner == -1) && ud.multimode > 1 && ud.showweapons != 0
						&& sprite[ps[p].i].extra > 0 && ps[p].curr_weapon > 0) {
					if (tsprite[spritesortcnt] == null)
						tsprite[spritesortcnt] = new SPRITE();
					tsprite[spritesortcnt].set(t);
					tsprite[spritesortcnt].statnum = 99;

					tsprite[spritesortcnt].yrepeat = (short) (t.yrepeat >> 3);
					if (t.yrepeat < 4)
						t.yrepeat = 4;

					tsprite[spritesortcnt].shade = t.shade;
					tsprite[spritesortcnt].cstat = 0;

					switch (ps[p].curr_weapon) {
					case PISTOL_WEAPON:
						tsprite[spritesortcnt].picnum = FIRSTGUNSPRITE;
						break;
					case SHOTGUN_WEAPON:
						tsprite[spritesortcnt].picnum = SHOTGUNSPRITE;
						break;
					case CHAINGUN_WEAPON:
						tsprite[spritesortcnt].picnum = CHAINGUNSPRITE;
						break;
					case RPG_WEAPON:
						tsprite[spritesortcnt].picnum = RPGSPRITE;
						break;
					case HANDREMOTE_WEAPON:
					case HANDBOMB_WEAPON:
						tsprite[spritesortcnt].picnum = HEAVYHBOMB;
						break;
					case TRIPBOMB_WEAPON:
						tsprite[spritesortcnt].picnum = TRIPBOMBSPRITE;
						break;
					case GROW_WEAPON:
						tsprite[spritesortcnt].picnum = GROWSPRITEICON;
						break;
					case SHRINKER_WEAPON:
						tsprite[spritesortcnt].picnum = SHRINKERSPRITE;
						break;
					case FREEZE_WEAPON:
						tsprite[spritesortcnt].picnum = FREEZESPRITE;
						break;
					case FLAMETHROWER_WEAPON: // Twentieth Anniversary World Tour
						if (currentGame.getCON().type == 20)
							tsprite[spritesortcnt].picnum = FLAMETHROWERSPRITE;
						break;
					case DEVISTATOR_WEAPON:
						tsprite[spritesortcnt].picnum = DEVISTATORSPRITE;
						break;
					}

					if (s.owner >= 0)
						tsprite[spritesortcnt].z = ps[p].posz - (12 << 8);
					else
						tsprite[spritesortcnt].z = s.z - (51 << 8);
					if (ps[p].curr_weapon == HANDBOMB_WEAPON) {
						tsprite[spritesortcnt].xrepeat = 10;
						tsprite[spritesortcnt].yrepeat = 10;
					} else {
						tsprite[spritesortcnt].xrepeat = 16;
						tsprite[spritesortcnt].yrepeat = 16;
					}
					tsprite[spritesortcnt].pal = 0;
					spritesortcnt++;
				}

				if (s.owner == -1) {
					k = (short) ((((s.ang + 3072 + 128 - a) & 2047) >> 8) & 7);
					if (k > 4) {
						k = (short) (8 - k);
						t.cstat |= 4;
					} else
						t.cstat &= ~4;

					if (sector[t.sectnum].lotag == 2)
						k += 1795 - 1405;
					else if ((hittype[i].floorz - s.z) > (64 << 8))
						k += 60;

					t.picnum += k;
					t.pal = ps[p].palookup;

					if (sector[sect].floorpal != 0)
						t.pal = sector[sect].floorpal;

					continue;
				}

				if (ps[p].on_crane == -1 && (sector[s.sectnum].lotag & 0x7ff) != 1) {
					l = s.z - hittype[ps[p].i].floorz + (3 << 8);
					if (l > 1024 && s.yrepeat > 32 && s.extra > 0)
						t.yoffset = (short) (l / (t.yrepeat << 2)); // GDX 24.10.2018 multiplayer unsync
					else
						t.yoffset = 0;
				}

				if (ps[p].newowner > -1) {
					t4 = currentGame.getCON().script[currentGame.getCON().actorscrptr[APLAYER] + 1];
					t3 = 0;
					t1 = currentGame.getCON().script[currentGame.getCON().actorscrptr[APLAYER] + 2];
				}

				if (ud.camerasprite == -1 && ps[p].newowner == -1)
					if (s.owner >= 0 && display_mirror == 0 && over_shoulder_on == 0)
						if (ud.multimode < 2 || (ud.multimode > 1 && p == screenpeek)) {
							t.owner = -1;
							t.xrepeat = t.yrepeat = 0;
							continue;
						}

				if (sector[sect].floorpal != 0)
					t.pal = sector[sect].floorpal;

				if (s.owner == -1)
					continue;

				if (t.z > hittype[i].floorz && t.xrepeat < 32)
					t.z = hittype[i].floorz;

				int tx = t.x - x;
				int ty = t.y - y;
				int angle = ((1024 + engine.getangle(tx, ty) - a) & kAngleMask) - 1024;
				long dist = engine.qdist(tx, ty);

				if (klabs(mulscale(angle, dist, 14)) < 4) {
					int horizoff = (int) (100 - ps[screenpeek].horiz);
					long z1 = mulscale(dist, horizoff, 3) + z;

					int zTop = t.z;
					int zBot = zTop;
					int yoffs = engine.getTile(APLAYER).getOffsetY();
					zTop -= (yoffs + engine.getTile(APLAYER).getHeight()) * (t.yrepeat << 2);
					zBot += -yoffs * (t.yrepeat << 2);

					if ((z1 < zBot) && (z1 > zTop)) {
						if (engine.cansee(x, y, z, sprite[ps[screenpeek].i].sectnum, t.x, t.y, t.z, t.sectnum))
							gPlayerIndex = t.yvel;
					}
				}

				break;

			case JIBS1:
			case JIBS2:
			case JIBS3:
			case JIBS4:
			case JIBS5:
			case JIBS6:
			case HEADJIB1:
			case LEGJIB1:
			case ARMJIB1:
			case LIZMANHEAD1:
			case LIZMANARM1:
			case LIZMANLEG1:
			case DUKELEG:
			case DUKEGUN:
			case DUKETORSO:
				if (ud.lockout != 0) {
					t.xrepeat = t.yrepeat = 0;
					continue;
				}
				if (t.pal == 6)
					t.shade = -120;

			case SCRAP1:
			case SCRAP2:
			case SCRAP3:
			case SCRAP4:
			case SCRAP5:
			case SCRAP6:
			case SCRAP6 + 1:
			case SCRAP6 + 2:
			case SCRAP6 + 3:
			case SCRAP6 + 4:
			case SCRAP6 + 5:
			case SCRAP6 + 6:
			case SCRAP6 + 7:

				if (hittype[i].picnum == BLIMP && t.picnum == SCRAP1 && s.yvel >= 0)
					t.picnum = s.yvel;
				else
					t.picnum += hittype[i].temp_data[0];
				t.shade -= 6;

				if (sector[sect].floorpal != 0)
					t.pal = sector[sect].floorpal;
				break;

			case WATERBUBBLE:
				if (sector[t.sectnum].floorpicnum == FLOORSLIME) {
					t.pal = 7;
					break;
				}
			default:

				if (sector[sect].floorpal != 0)
					t.pal = sector[sect].floorpal;
				break;
			}

			if (currentGame.getCON().actorscrptr[s.picnum] != 0) {
				if (t4 != 0) {
					l = currentGame.getCON().script[t4 + 2];

					switch (l) {
					case 2:
						k = (short) ((((s.ang + 3072 + 128 - a) & 2047) >> 8) & 1);
						break;

					case 3:
					case 4:
						k = (short) ((((s.ang + 3072 + 128 - a) & 2047) >> 7) & 7);
						if (k > 3) {
							t.cstat |= 4;
							k = (short) (7 - k);
						} else
							t.cstat &= ~4;
						break;

					case 5:
						k = engine.getangle(s.x - x, s.y - y);
						k = (short) ((((s.ang + 3072 + 128 - k) & 2047) >> 8) & 7);
						if (k > 4) {
							k = (short) (8 - k);
							t.cstat |= 4;
						} else
							t.cstat &= ~4;
						break;
					case 7:
						k = engine.getangle(s.x - x, s.y - y);
						k = (short) (((s.ang + 3072 + 128 - k) & 2047) / 170);
						if (k > 6) {
							k = (short) (12 - k);
							t.cstat |= 4;
						} else
							t.cstat &= ~4;
						break;
					case 8:
						k = (short) ((((s.ang + 3072 + 128 - a) & 2047) >> 8) & 7);
						t.cstat &= ~4;
						break;
					default:
						k = 0;
						break;
					}

					t.picnum += (k + (currentGame.getCON().script[t4]) + l * t3);

					if (l > 0)
						while (t.picnum > 0 && t.picnum < MAXTILES && engine.getTile(t.picnum).getWidth() == 0)
							t.picnum -= l; // Hack, for actors

					if (hittype[i].dispicnum >= 0)
						hittype[i].dispicnum = t.picnum;
				} else if (display_mirror == 1)
					t.cstat |= 4;
			}

			if (s.statnum == 13 || badguy(s) || (s.picnum == APLAYER && s.owner >= 0))
				if (t.statnum != 99 && s.picnum != EXPLOSION2 && s.picnum != HANGLIGHT && s.picnum != DOMELITE)
					if (s.picnum != HOTMEAT) {
						if (hittype[i].dispicnum < 0) {
							hittype[i].dispicnum++;
							continue;
						} else if (ud.shadows != 0 && spritesortcnt < (MAXSPRITESONSCREEN - 2)) {
							int daz, xrep, yrep;

							if ((sector[sect].lotag & 0xff) > 2 || s.statnum == 4 || s.statnum == 5 || s.picnum == DRONE
									|| s.picnum == COMMANDER)
								daz = sector[sect].floorz;
							else
								daz = hittype[i].floorz;

							if ((s.z - daz) < (8 << 8))
								if (ps[screenpeek].posz < daz) {
									if (tsprite[spritesortcnt] == null)
										tsprite[spritesortcnt] = new SPRITE();
									SPRITE tspr = tsprite[spritesortcnt];
									tspr.set(t);
									int camangle = engine.getangle(x - tspr.x, y - tspr.y);
									tspr.x -= mulscale(sintable[(camangle + 512) & 2047], 100, 16);
									tspr.y += mulscale(sintable[(camangle + 1024) & 2047], 100, 16);
									tspr.statnum = 99;

									tspr.yrepeat = (short) (t.yrepeat >> 3);
									if (tspr.yrepeat < 4)
										tspr.yrepeat = 4;

									tspr.shade = 127;
									tspr.cstat |= 2;

									tspr.z = daz;
									xrep = tspr.xrepeat;
									tspr.xrepeat = (short) xrep;
									tspr.pal = 4;

									yrep = tspr.yrepeat;
									tspr.yrepeat = (short) yrep;
									spritesortcnt++;
								}
						}

						if (ps[screenpeek].heat_amount > 0 && ps[screenpeek].heat_on != 0) {
							t.pal = 6;
							t.shade = 0;
						}
					}

			switch (s.picnum) {
			case LASERLINE:
				if (sector[t.sectnum].lotag == 2)
					t.pal = 8;
				t.z = sprite[s.owner].z - (3 << 8);
				if (currentGame.getCON().lasermode == 2 && ps[screenpeek].heat_on == 0)
					t.yrepeat = 0;
			case EXPLOSION2:
			case EXPLOSION2BOT:
			case FREEZEBLAST:
			case ATOMICHEALTH:
			case FIRELASER:
			case SHRINKSPARK:
			case GROWSPARK:
			case CHAINGUN:
			case SHRINKEREXPLOSION:
			case RPG:
			case FLOORFLAME:
				if (t.picnum == EXPLOSION2) {
					gVisibility = -127;
					lastvisinc = totalclock + 32;
				}
				t.shade = -127;
				break;
			case FIRE:
			case FIRE2:
			case BURNING:
			case BURNING2:
				if (sprite[s.owner].picnum != TREE1 && sprite[s.owner].picnum != TREE2)
					t.z = sector[t.sectnum].floorz;
				t.shade = -127;
				break;
			case COOLEXPLOSION1:
				t.shade = -127;
				t.picnum += (s.shade >> 1);
				break;
			case PLAYERONWATER:

				k = (short) ((((t.ang + 3072 + 128 - a) & 2047) >> 8) & 7);
				if (k > 4) {
					k = (short) (8 - k);
					t.cstat |= 4;
				} else
					t.cstat &= ~4;

				t.picnum = (short) (s.picnum + k + ((hittype[i].temp_data[0] < 4) ? 5 : 0));
				t.shade = sprite[s.owner].shade;

				break;

			case WATERSPLASH2:
				t.picnum = (short) (WATERSPLASH2 + t1);
				break;
			case REACTOR2:
				t.picnum = (short) (s.picnum + hittype[i].temp_data[2]);
				break;
			case SHELL:
				t.picnum = (short) (s.picnum + (hittype[i].temp_data[0] & 1));
			case SHOTGUNSHELL:
				t.cstat |= 12;
				if (hittype[i].temp_data[0] > 1)
					t.cstat &= ~4;
				if (hittype[i].temp_data[0] > 2)
					t.cstat &= ~12;
				break;
			case FRAMEEFFECT1:
				if (s.owner >= 0 && sprite[s.owner].statnum < MAXSTATUS) {
					if (sprite[s.owner].picnum == APLAYER)
						if (ud.camerasprite == -1)
							if (screenpeek == sprite[s.owner].yvel && display_mirror == 0) {
								t.owner = -1;
								break;
							}
					if ((sprite[s.owner].cstat & 32768) == 0) {
						t.picnum = (short) hittype[s.owner].dispicnum;
						t.pal = sprite[s.owner].pal;
						t.shade = sprite[s.owner].shade;
						t.ang = sprite[s.owner].ang;
						t.cstat = (short) (2 | sprite[s.owner].cstat);
					}
				}
				break;

			case CAMERA1:
			case RAT:
				k = (short) ((((t.ang + 3072 + 128 - a) & 2047) >> 8) & 7);
				if (k > 4) {
					k = (short) (8 - k);
					t.cstat |= 4;
				} else
					t.cstat &= ~4;
				t.picnum = (short) (s.picnum + k);
				break;
			}

			hittype[i].dispicnum = t.picnum;
			if (sector[t.sectnum].floorpicnum == MIRROR)
				t.xrepeat = t.yrepeat = 0;
		}
	}

	public static int lastvisinc;

	public static void displaymasks(short snum) {
		if (ps[snum].cursectnum == -1)
			return;

		int p = sector[ps[snum].cursectnum].floorpal;

		if (sprite[ps[snum].i].pal == 1)
			p = 1;
		if (ps[snum].scuba_on != 0) {
			Tile pic = engine.getTile(SCUBAMASK);
			if (ud.screen_size > 2) {
				engine.rotatesprite(44 << 16, (200 - 8 - (pic.getHeight()) << 16), 65536, 0, SCUBAMASK, 0, p, 10 + 16,
						windowx1, windowy1, windowx2, windowy2);
				engine.rotatesprite((320 - 43) << 16, (200 - 8 - (pic.getHeight()) << 16), 65536, 1024, SCUBAMASK, 0, p,
						10 + 4 + 16, windowx1, windowy1, windowx2, windowy2);
			} else {
				engine.rotatesprite(44 << 16, (200 - (pic.getHeight()) << 16), 65536, 0, SCUBAMASK, 0, p, 10 + 16,
						windowx1, windowy1, windowx2, windowy2);
				engine.rotatesprite((320 - 43) << 16, (200 - (pic.getHeight()) << 16), 65536, 1024, SCUBAMASK, 0, p,
						10 + 4 + 16, windowx1, windowy1, windowx2, windowy2);
			}
		}
	}

	public static final short[] tip_y = { 0, -8, -16, -32, -64, -84, -108, -108, -108, -108, -108, -108, -108, -108,
			-108, -108, -96, -72, -64, -32, -16 };

	public static boolean animatetip(int gs, int snum) {
		int p, looking_arc;

		if (ps[snum].tipincs == 0)
			return false;

		looking_arc = klabs(ps[snum].look_ang) / 9;
		looking_arc -= (ps[snum].hard_landing << 3);

		if (sprite[ps[snum].i].pal == 1)
			p = 1;
		else
			p = sector[ps[snum].cursectnum].floorpal;

		if (ps[snum].tipincs < tip_y.length)
			myospal(170 + (int) (sync[snum].avel / 16f) - (ps[snum].look_ang >> 1),
					(tip_y[ps[snum].tipincs] >> 1) + looking_arc + 240
							- (((int) ps[snum].horiz - ps[snum].horizoff) >> 4),
					TIP + ((26 - ps[snum].tipincs) >> 4), gs, 0, p);

		return true;
	}

	public static final short[] access_y = { 0, -8, -16, -32, -64, -84, -108, -108, -108, -108, -108, -108, -108, -108,
			-108, -108, -96, -72, -64, -32, -16 };

	public static boolean animateaccess(int gs, int snum) {
		int looking_arc;
		int p = 0;

		if (ps[snum].access_incs == 0 || sprite[ps[snum].i].extra <= 0)
			return false;

		looking_arc = access_y[ps[snum].access_incs] + klabs(ps[snum].look_ang) / 9;
		looking_arc -= (ps[snum].hard_landing << 3);

		if (ps[snum].access_spritenum >= 0)
			p = sprite[ps[snum].access_spritenum].pal;

		if ((ps[snum].access_incs - 3) > 0 && ((ps[snum].access_incs - 3) >> 3) != 0)
			myospal(170 + (int) (sync[snum].avel / 16f) - (ps[snum].look_ang >> 1)
					+ (access_y[ps[snum].access_incs] >> 2),
					looking_arc + 266 - (((int) ps[snum].horiz - ps[snum].horizoff) >> 4),
					HANDHOLDINGLASER + (ps[snum].access_incs >> 3), gs, 0, p);
		else
			myospal(170 + (int) (sync[snum].avel / 16f) - (ps[snum].look_ang >> 1)
					+ (access_y[ps[snum].access_incs] >> 2),
					looking_arc + 266 - (((int) ps[snum].horiz - ps[snum].horizoff) >> 4), HANDHOLDINGACCESS, gs, 4, p);

		return true;
	}

	public static void addmessage(String message) {
		buildString(currentGame.getCON().fta_quotes[122], 0, message);
		FTA(122, ps[myconnectindex]);
	}

	public static void viewDrawStats(int x, int y, int zoom) {
		if (cfg.gShowStat == 0)
			return;

		float viewzoom = (zoom / 65536.0f);

		buildString(buffer, 0, "kills:  ");
		int alignx = game.getFont(1).getWidth(buffer);

		int yoffset = (int) (2.5f * game.getFont(1).getHeight() * viewzoom);
		y -= yoffset;

		int statx = x;
		int staty = y;

		game.getFont(1).drawText(statx, staty, buffer, zoom, 0, 10, TextAlign.Left, 10 | 256, false);

		int offs = Bitoa(ps[connecthead].actors_killed, buffer);
		offs = buildString(buffer, offs, " / ", ps[connecthead].max_actors_killed);
		game.getFont(1).drawText(statx += (alignx + 2) * viewzoom, staty, buffer, zoom, 0, 15, TextAlign.Left, 10 | 256,
				false);

		statx = x;
		staty = y + (int) (8 * viewzoom);

		buildString(buffer, 0, "secrets:  ");
		game.getFont(1).drawText(statx, staty, buffer, zoom, 0, 10, TextAlign.Left, 10 | 256, false);
		alignx = game.getFont(1).getWidth(buffer);
		offs = Bitoa(ps[connecthead].secret_rooms, buffer);
		offs = buildString(buffer, offs, " / ", ps[connecthead].max_secret_rooms);
		game.getFont(1).drawText(statx += (alignx + 2) * viewzoom, staty, buffer, zoom, 0, 15, TextAlign.Left, 10 | 256,
				false);

		statx = x;
		staty = y + (int) (16 * viewzoom);

		buildString(buffer, 0, "time:  ");
		game.getFont(1).drawText(statx, staty, buffer, zoom, 0, 10, TextAlign.Left, 10 | 256, false);
		alignx = game.getFont(1).getWidth(buffer);

		int minutes = ps[myconnectindex].player_par / (26 * 60);
		int sec = (ps[myconnectindex].player_par / 26) % 60;

		offs = Bitoa(minutes, buffer, 2);
		offs = buildString(buffer, offs, " :   ", sec, 2);
		game.getFont(1).drawText(statx += (alignx + 2) * viewzoom, staty, buffer, zoom, 0, 15, TextAlign.Left, 10 | 256,
				false);
	}

	private static final PlayerOrig viewout = new PlayerOrig();

	public static PlayerOrig view(PlayerStruct pp, int vx, int vy, int vz, short vsectnum, float ang, float horiz) {
		viewout.ox = vx;
		viewout.oy = vy;
		viewout.oz = vz;
		viewout.os = vsectnum;

		int nx = (int) (-BCosAngle(ang) / 16.0f);
		int ny = (int) (-BSinAngle(ang) / 16.0f);
		int nz = (int) ((horiz - 100) * 128 - 4096);

		SPRITE sp = sprite[pp.i];

		short bakcstat = sp.cstat;
		sp.cstat &= ~0x101;

		vsectnum = engine.updatesectorz(vx, vy, vz, vsectnum);

		engine.hitscan(vx, vy, vz, vsectnum, nx, ny, nz, pHitInfo, CLIPMASK1);
		int hitx = pHitInfo.hitx, hity = pHitInfo.hity;

		if (vsectnum < 0) {
			sp.cstat = bakcstat;
			return viewout;
		}

		int hx = hitx - (vx);
		int hy = hity - (vy);
		if ((klabs(hx) + klabs(hy)) - (klabs(nx) + klabs(ny)) < 1024) {
			int wx = 1;
			if (nx < 0)
				wx = -1;
			int wy = 1;
			if (ny < 0)
				wy = -1;

			hx -= wx << 9;
			hy -= wy << 9;

			int dist = 0;
			if (nx != 0 && ny != 0) {
				if (klabs(nx) > klabs(ny))
					dist = divscale(hx, nx, 16);
				else
					dist = divscale(hy, ny, 16);
			}

			if (dist < cameradist)
				cameradist = dist;
		}

		vx += mulscale(nx, cameradist, 16);
		vy += mulscale(ny, cameradist, 16);
		vz += mulscale(nz, cameradist, 16);

		cameradist = min(cameradist + ((totalclock - cameraclock) << 10), 65536);
		cameraclock = totalclock;

		vsectnum = engine.updatesectorz(vx, vy, vz, vsectnum);

		sp.cstat = bakcstat;

		viewout.ox = vx;
		viewout.oy = vy;
		viewout.oz = vz;
		viewout.os = vsectnum;

		return viewout;
	}

	public static void coords(short snum) {
		short y = 0;

		if (ud.coop != 1) {
			if (ud.multimode > 1 && ud.multimode < 5)
				y = 8;
			else if (ud.multimode > 4)
				y = 16;
		}

		buildString(buffer, 0, "X= ", ps[snum].posx);
		engine.printext256(250, y, 31, -1, buffer, 1, 1.0f);
		buildString(buffer, 0, "Y= ", ps[snum].posy);
		engine.printext256(250, y + 7, 31, -1, buffer, 1, 1.0f);
		buildString(buffer, 0, "Z= ", ps[snum].posz);
		engine.printext256(250, y + 14, 31, -1, buffer, 1, 1.0f);
		buildString(buffer, 0, "A= ", (int) ps[snum].ang);
		engine.printext256(250, y + 21, 31, -1, buffer, 1, 1.0f);
		buildString(buffer, 0, "ZV= ", ps[snum].poszv);
		engine.printext256(250, y + 28, 31, -1, buffer, 1, 1.0f);
		buildString(buffer, 0, "OG= ", ps[snum].on_ground ? 1 : 0);
		engine.printext256(250, y + 35, 31, -1, buffer, 1, 1.0f);
		buildString(buffer, 0, "AM= ", ps[snum].ammo_amount[GROW_WEAPON]);
		engine.printext256(250, y + 43, 31, -1, buffer, 1, 1.0f);
		buildString(buffer, 0, "LFW= ", ps[snum].last_full_weapon);
		engine.printext256(250, y + 50, 31, -1, buffer, 1, 1.0f);
		buildString(buffer, 0, "SECTL= ", sector[ps[snum].cursectnum].lotag);
		engine.printext256(250, y + 57, 31, -1, buffer, 1, 1.0f);
		buildString(buffer, 0, "SEED= ", engine.getrand());
		engine.printext256(250, y + 64, 31, -1, buffer, 1, 1.0f);
		buildString(buffer, 0, "THOLD= ", ps[snum].transporter_hold);
		engine.printext256(250, y + 64 + 7, 31, -1, buffer, 1, 1.0f);
	}

	public static byte[] oldgotsector = new byte[MAXSECTORS >> 3];

	public static void SE40_Draw(int spnum, int x, int y, int z, float a, float h, int smoothratio) {
		int rtype = 0;
		boolean drawror = false;
		for (int i = 0; i < 16; i++) {
			if (rorsector[i] != -1) {
				if ((gotsector[rorsector[i] >> 3] & (1 << (rorsector[i] & 7))) != 0
						&& ((rortype[i] == 1 && sprite[spnum].lotag == 41)
								|| (rortype[i] == 2 && sprite[spnum].lotag == 40))) {
					drawror = true;
					rtype = rortype[i];
					break;
				}
			}
		}
		if (!drawror)
			return;

		int nUpper = -1, nLower = -1;
		if (rtype == 1) // ceiling
			nLower = spnum;
		if (rtype == 2) // floor
			nUpper = spnum;

		for (int i = headspritestat[15]; i >= 0; i = nextspritestat[i]) {
			if (i != spnum && sprite[i].picnum == 1 && sprite[i].hitag == sprite[spnum].hitag) {
				if (rtype == 1)
					nUpper = i;
				if (rtype == 2)
					nLower = i;
				break;
			}
		}

		int rsect = -1, rx = 0, ry = 0, rz = 0;
		if (rtype == 1) {
			rsect = sprite[nUpper].sectnum;
			rx = sprite[nUpper].x - sprite[nLower].x;
			ry = sprite[nUpper].y - sprite[nLower].y;
			rz = sector[sprite[nUpper].sectnum].floorz - sector[sprite[nLower].sectnum].ceilingz;
			sector[rsect].floorstat |= 1024;
		}

		if (rtype == 2) {
			rsect = sprite[nLower].sectnum;
			rx = sprite[nLower].x - sprite[nUpper].x;
			ry = sprite[nLower].y - sprite[nUpper].y;
			rz = sector[sprite[nLower].sectnum].ceilingz - sector[sprite[nUpper].sectnum].floorz;
			sector[rsect].ceilingstat |= 1024;
		}

		System.arraycopy(gotsector, 0, oldgotsector, 0, gotsector.length);

		engine.drawrooms(x + rx, y + ry, z + rz, (short) a, h, (short) (rsect | MAXSECTORS));
		animatesprites(x + rx, y + ry, z + rz, (short) a, smoothratio);
		engine.drawmasks();

		System.arraycopy(oldgotsector, 0, gotsector, 0, gotsector.length);

		if (rtype == 1)
			sector[rsect].floorstat &= ~1024;
		if (rtype == 2)
			sector[rsect].ceilingstat &= ~1024;
	}

	public static void se40code(int x, int y, int z, float a, float h, int smoothratio) {
		for (int i = headspritestat[15]; i >= 0; i = nextspritestat[i]) {
			switch (sprite[i].lotag) {
			case 40: // floor
			case 41: // ceiling
				SE40_Draw(i, x, y, z, a, h, smoothratio);
				break;
			}
		}
	}

	public static void cameratext(short i) {
		if (hittype[i].temp_data[0] == 0) {
			engine.rotatesprite(24 << 16, 20 << 16, 65536, 0, CAMCORNER, 0, 0, 2 + 8 + 256, windowx1, windowy1,
					windowx2, windowy2);
			engine.rotatesprite((320 - 24) << 16, 20 << 16, 65536, 0, CAMCORNER + 1, 0, 0, 2 + 8 + 512, windowx1,
					windowy1, windowx2, windowy2);
			engine.rotatesprite(22 << 16, 143 << 16, 65536, 512, CAMCORNER + 1, 0, 0, 2 + 4 + 8 + 256, windowx1,
					windowy1, windowx2, windowy2);
			engine.rotatesprite((310 - 10) << 16, 143 << 16, 65536, 512, CAMCORNER + 1, 0, 0, 2 + 8 + 512, windowx1,
					windowy1, windowx2, windowy2);
			if ((totalclock & 16) != 0)
				engine.rotatesprite(46 << 16, 18 << 16, 65536, 0, CAMLIGHT, 0, 0, 2 + 8 + 256, windowx1, windowy1,
						windowx2, windowy2);
		} else {
			int flipbits = (totalclock << 1) & 48;
			for (int y, x = 0; x < 394; x += 64)
				for (y = 0; y < 200; y += 64)
					engine.rotatesprite(x << 16, y << 16, 65536, 0, STATIC, 0, 0, 10 + 256 + flipbits, windowx1,
							windowy1, windowx2, windowy2);
		}
	}
}
