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

package ru.m210projects.LSP.Screens;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.LSP.Animate.doanimations;
import static ru.m210projects.LSP.Enemies.inienemies;
import static ru.m210projects.LSP.Globals.*;
import static ru.m210projects.LSP.Main.*;
import static ru.m210projects.LSP.Player.*;
import static ru.m210projects.LSP.Sounds.*;
import static ru.m210projects.LSP.Sectors.*;
import static ru.m210projects.LSP.Sprites.*;
import static ru.m210projects.LSP.View.*;
import static ru.m210projects.LSP.Weapons.drawweapons;
import static ru.m210projects.LSP.LoadSave.*;
import static ru.m210projects.LSP.Factory.LSPMenuHandler.*;
import static ru.m210projects.LSP.Quotes.*;

import java.io.FileNotFoundException;
import java.util.Arrays;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Build.Types.BuildPos;
import ru.m210projects.Build.Types.InvalidVersionException;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.LSP.Config.LSPKeys;
import ru.m210projects.LSP.Main;
import ru.m210projects.LSP.Sounds;
import ru.m210projects.LSP.Factory.LSPInput;
import ru.m210projects.LSP.Factory.LSPMenuHandler;
import ru.m210projects.LSP.Menus.MenuInterfaceSet;
import ru.m210projects.LSP.Types.DemoFile;
import ru.m210projects.LSP.Types.PlayerStruct;

public class GameScreen extends GameAdapter {

	private int nonsharedtimer;
	public int gNameShowTime;
	public Main game;

	public GameScreen(Main game) {
		super(game, gLoadingScreen);
		this.game = game;
	}

	@Override
	public void ProcessFrame(BuildNet net) {
		FixPalette();
		for (short i = connecthead; i >= 0; i = connectpoint2[i]) {
			gPlayer[i].pInput.Copy(net.gFifoInput[net.gNetFifoTail & 0xFF][i]);
			if ((gPlayer[i].pInput.bits & (1 << 17)) != 0) {
				game.gPaused = !game.gPaused;
				sndHandlePause(game.gPaused);
			}
		}
		net.gNetFifoTail++;

		lockclock += TICSPERFRAME;
		if (game.gPaused
				|| recstat != 2 && GameScreen.this != gDemoScreen && (game.menu.gShowMenu || Console.IsShown()))
			if(!game.menu.isOpened(game.menu.mMenus[LASTSAVE]))
				return;

		if (recstat == 1 && rec != null)
			rec.record();

		for (short i = connecthead; i >= 0; i = connectpoint2[i]) {
			gPlayer[i].UpdatePlayerLoc();
			processinput(i);
			checktouchsprite(i);
		}

		MarkSectorSeen(gPlayer[myconnectindex].sectnum);

		if (followmode) {
			followa += followang;

			followx += (followvel * sintable[(512 + 2048 - followa) & 2047]) >> 10;
			followy += (followvel * sintable[(512 + 1024 - 512 - followa) & 2047]) >> 10;

			followx += (followsvel * sintable[(512 + 1024 - 512 - followa) & 2047]) >> 10;
			followy -= (followsvel * sintable[(512 + 2048 - followa) & 2047]) >> 10;
		}

		doanimations(TICSPERFRAME);
		tagcode();
		statuslistcode();
		updatesounds();
		totalmoves++;
	}

	@Override
	public void DrawWorld(float smooth) {
		drawscreen(screenpeek, (int) smooth);
	}

	@Override
	public void DrawHud(float smooth) {
		if (pMenu.gShowMenu && !(pMenu.getCurrentMenu() instanceof MenuInterfaceSet))
			return;

		if (mapnum > 0) {
			if (gPlayer[screenpeek].gViewMode != kView2DIcon) {
				if (gPlayer[screenpeek].nHealth > 0 && !isonwater(screenpeek) && mapnum > 0)
					drawweapons(screenpeek);
			}

			int yoffs = scale(50, cfg.gHUDSize, 65536);
			drawbar(10, ydim - yoffs, cfg.gHUDSize, myconnectindex);
			viewDrawStats(10, ydim - yoffs, cfg.gHUDSize);
		}
		viewDisplayMessage();

		if (gPlayer[myconnectindex].gViewMode != kView3D) {
			int pos = 25;
			if (followmode)
				game.getFont(1).drawText(20, pos += scale(25, cfg.gHUDSize, 65536), "Follow mode", cfg.gHUDSize, 0, 4,
						TextAlign.Left, 8 | 256, true);

			game.getFont(1).drawText(20, pos += scale(25, cfg.gHUDSize, 65536),
					book + "b " + chapter + "c " + verse + "v", cfg.gHUDSize, 0, 4, TextAlign.Left, 8 | 256, true);
		}

		if (totalclock < gNameShowTime) {
			int transp = 0;
			if (totalclock > gNameShowTime - 20)
				transp = 1;
			if (totalclock > gNameShowTime - 10)
				transp = 33;

			if (cfg.showMapInfo != 0 && !game.menu.gShowMenu && mapnum > 0) {
//				if (mUserFlag != UserFlag.UserMap) {
				game.getFont(2).drawText(160, 100, book + "b " + chapter + "c " + verse + "v", -128, 70,
						TextAlign.Center, 2 | transp, true);
//				}
			}
		}

		if (game.gPaused && !game.menu.gShowMenu) {
			game.getFont(2).drawText(160, 100, "GAME PAUSED", 0, 0, TextAlign.Center, 2 + 8 + 16, false);
		}

		if (nPalDiff != 0 && engine.glrender() != null)
			engine.showfade();
	}

	@Override
	public void KeyHandler() {
		LSPMenuHandler menu = game.menu;
		if (menu.gShowMenu) {
			engine.handleevents();
			menu.mKeyHandler(game.pInput, BuildGdx.graphics.getDeltaTime());
			return;
		}

		if (Console.IsShown())
			return;

		BuildControls input = game.pInput;
		if (input.ctrlGetInputKey(GameKeys.Menu_Toggle, true))
			menu.mOpen(menu.mMenus[GAME], -1);

		// non mappable function keys
		if (input.ctrlGetInputKey(LSPKeys.Show_Savemenu, true)) {
			if (game.nNetMode == NetMode.Single) {
				gGameScreen.capture(160, 100);
				menu.mOpen(menu.mMenus[SAVEGAME], -1);
			}
		} else if (input.ctrlGetInputKey(LSPKeys.Show_Loadmenu, true)) {
			if (game.nNetMode == NetMode.Single) {
				menu.mOpen(menu.mMenus[LOADGAME], -1);
			}
		} else if (input.ctrlGetInputKey(LSPKeys.Quit, true)) {
			menu.mOpen(menu.mMenus[QUIT], -1);
		} else if (input.ctrlGetInputKey(LSPKeys.Show_Options, true)) {
			menu.mOpen(menu.mMenus[OPTIONS], -1);
		} else if (input.ctrlGetInputKey(LSPKeys.Quicksave, true)) {
			quicksave();
		} else if (input.ctrlGetInputKey(LSPKeys.Quickload, true)) {
			quickload();
		} else if (input.ctrlGetInputKey(LSPKeys.Show_Sounds, true)) {
			menu.mOpen(menu.mMenus[AUDIOSET], -1);
		} else if (input.ctrlGetInputKey(LSPKeys.Gamma, true)) {
			menu.mOpen(menu.mMenus[COLORCORR], -1);
		} else if (input.ctrlGetInputKey(LSPKeys.Make_Screenshot, true)) {
			makeScreenshot();
		} else if (input.ctrlGetInputKey(LSPKeys.AutoRun, true)) {
			cfg.gAutoRun = !cfg.gAutoRun;
			viewSetMessage("Autorun: " + (cfg.gAutoRun ? "ON" : "OFF"));
		} else if (input.ctrlGetInputKey(LSPKeys.Toggle_Crosshair, true)) {
			cfg.gCrosshair = !cfg.gCrosshair;
			viewSetMessage("Crosshair: " + (cfg.gCrosshair ? "ON" : "OFF"));
		} else if (input.ctrlGetInputKey(GameKeys.Mouse_Aiming, true)) {
			cfg.gMouseAim = !cfg.gMouseAim;
			viewSetMessage("Mouse aiming: " + (cfg.gMouseAim ? "ON" : "OFF"));
		}

		if (gPlayer[myconnectindex].gViewMode != 3) {
			int j = totalclock - nonsharedtimer;
			nonsharedtimer += j;
			if (input.ctrlGetInputKey(GameKeys.Enlarge_Screen, false))
				gPlayer[myconnectindex].zoom += mulscale(j, Math.max(gPlayer[myconnectindex].zoom, 256), 6);
			if (input.ctrlGetInputKey(GameKeys.Shrink_Screen, false))
				gPlayer[myconnectindex].zoom -= mulscale(j, Math.max(gPlayer[myconnectindex].zoom, 256), 6);

			if ((gPlayer[myconnectindex].zoom > 2048))
				gPlayer[myconnectindex].zoom = 2048;
			if ((gPlayer[myconnectindex].zoom < 48))
				gPlayer[myconnectindex].zoom = 48;

			if (input.ctrlGetInputKey(LSPKeys.Map_Follow_Mode, true)) {
				followmode = !followmode;
				if (followmode) {
					followx = gPlayer[myconnectindex].x;
					followy = gPlayer[myconnectindex].y;
					followa = (int) gPlayer[myconnectindex].ang;
				}
				viewSetMessage("Follow mode " + (followmode ? "ON" : "OFF"));
			}
		}

		if (input.ctrlGetInputKey(GameKeys.Map_Toggle, true)) {
			gPlayer[myconnectindex].gViewMode = (byte) setOverHead(gPlayer[myconnectindex].gViewMode);
		}
	}

	@Override
	public void PostFrame(BuildNet net) {
		if (gQuickSaving) {
			if (captBuffer != null) {
				savegame("[quicksave_" + +quickslot + "]", "quicksav" + quickslot + ".sav");
				quickslot ^= 1;
				gQuickSaving = false;
			} else
				gGameScreen.capture(160, 100);
		}

		if (gAutosaveRequest) {
			if (captBuffer != null) {
				savegame("[autosave]", "autosave.sav");
				gAutosaveRequest = false;
			} else
				gGameScreen.capture(160, 100);
		}
	}

	protected void makeScreenshot() {
		String name = "scrxxxx.png";

		name = "scr-map" + mapnum + "-xxxx.png";
		String filename = pEngine.screencapture(name);
		if (filename != null)
			viewSetMessage(filename + " saved");
		else
			viewSetMessage("Screenshot not saved. Access denied!");
	}

	protected int setOverHead(int mode) {
		int out = 0;
		switch (mode) {
		case kView3D:
			if (cfg.gOverlayMap != 0)
				out = kView2D;
			else
				out = kView2DIcon;
			break;
		case kView2D:
			if (cfg.gOverlayMap == 1)
				out = kView3D;
			else
				out = kView2DIcon;
			break;
		case kView2DIcon:
			out = kView3D;
			break;
		}
		return out;
	}

	@Override
	protected void startboard(Runnable startboard) {
		gPrecacheScreen.init(false, startboard);
		game.changeScreen(gPrecacheScreen);
	}

	@Override
	protected boolean prepareboard(String map) {
		if (GameScreen.this != gDemoScreen && recstat == 2)
			recstat = 0;

		stopmusic();
		stopallsounds();

		BuildPos pos = null;
		if (mapnum != -1) {
			Resource res = gMapGroup.open(mapnum, "");
			if (res == null) {
				game.GameMessage("Map " + map + " not found!");
				return false;
			}

			try {
				pos = engine.loadboard(res);
			} catch (InvalidVersionException | RuntimeException e) {
				game.GameMessage(e.getMessage());
				return false;
			}
		} else {
			try {
				pos = engine.loadboard(map);
			} catch (FileNotFoundException | InvalidVersionException | RuntimeException e) {
				game.GameMessage(e.getMessage());
				return false;
			}
		}

		for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
			PlayerStruct pPlayer = gPlayer[i];
			if (mapnum > 1)
				pPlayer.savePlayersInventory();

			pPlayer.x = pos.x;
			pPlayer.y = pos.y;
			pPlayer.z = pos.z;
			pPlayer.ang = pos.ang;
			pPlayer.osectnum = pPlayer.sectnum = pos.sectnum;
			pPlayer.horiz = 100;
			pPlayer.gViewMode = 3;
			pPlayer.zoom = 768;
			pPlayer.nSprite = -1;
			pPlayer.nWeaponState = 0;
			pPlayer.nBobCount = 0;
			pPlayer.nWeaponSeq = 0;
			pPlayer.nWeapon = nPlayerFirstWeapon;
			pPlayer.nLastChoosedWeapon = 6;
			pPlayer.nLastManaWeapon = 13;
			pPlayer.nPlayerStatus = 0;
			pPlayer.word_586FC = 0;
			switch (nPlayerFirstWeapon) {
			case 18:
				pPlayer.nFirstWeaponDamage = 9;
//				nMagicWeaponDamage = 19;
				pPlayer.nSecondWeaponDamage = 18;
				break;
			case 19:
				pPlayer.nFirstWeaponDamage = 7;
//				nMagicWeaponDamage = 15;
				pPlayer.nSecondWeaponDamage = 21;
				break;
			case 20:
				pPlayer.nFirstWeaponDamage = 9;
//				nMagicWeaponDamage = 18;
				pPlayer.nSecondWeaponDamage = 18;
				break;
			case 21:
				pPlayer.nFirstWeaponDamage = 8;
//				nMagicWeaponDamage = 18;
				pPlayer.nSecondWeaponDamage = 18;
				break;
			case 22:
				pPlayer.nFirstWeaponDamage = 6;
//				nMagicWeaponDamage = 13;
				pPlayer.nSecondWeaponDamage = 17;
				break;
			case 23:
				pPlayer.nFirstWeaponDamage = 6;
//				nMagicWeaponDamage = 15;
				pPlayer.nSecondWeaponDamage = 16;
				break;
			case 24:
				pPlayer.nFirstWeaponDamage = pPlayer.nRandDamage1;
//				nMagicWeaponDamage = pPlayer.nRandDamage1 + word_58A8E;
				pPlayer.nSecondWeaponDamage = pPlayer.nRandDamage1 + pPlayer.nRandDamage2;
				break;
			default:
				pPlayer.nFirstWeaponDamage = 9;
//				nMagicWeaponDamage = 18;
				pPlayer.nSecondWeaponDamage = 16;
				break;
			}
		}

		globalvisibility = 15;
		pskyoff[0] = 0;
		pskyoff[1] = 0;
		pskyoff[2] = 0;
		pskyoff[3] = 0;
		parallaxtype = 0;
		parallaxyoffs = 256;
		pskybits = 2;
		totalmoves = 0;
		nNextMap = 0;
		numQuotes = 0;

		for (int i = 0; i < MAXPLAYERS; i++) {
			waterfountainwall[i] = -1;
			waterfountaincnt[i] = 0;
		}

		warpsectorcnt = 0;
		warpsector2cnt = 0;
		xpanningsectorcnt = 0;
		floorpanningcnt = 0;
		swingcnt = 0;
		revolvecnt = 0;
		subwaytrackcnt = 0;
		dragsectorcnt = 0;

		short startwall, endwall, dasector;
		int j, k = 0, s, dax, day, dax2, day2;

		nKickSprite = -1;
		for (short i = 0; i < numsectors; i++) {
			SECTOR sec = sector[i];

//			if (cfgParameter1[3] != 0) {
			switch (sec.ceilingpicnum) {
			case 250:
				sec.ceilingpicnum = 252;
				break;
			case 251:
				sec.ceilingpicnum = 255;
				break;
			case 254:
				sec.ceilingpicnum = 253;
				break;
			}

			switch (sec.floorpicnum) {
			case 250:
				sec.floorpicnum = 252;
				break;
			case 251:
				sec.floorpicnum = 255;
				break;
			case 254:
				sec.floorpicnum = 253;
				break;
			}
//			}

			switch (sec.lotag) {
			case 4:
			case 5:
				floorpanninglist[floorpanningcnt++] = i;
				break;
			case 10:
				warpsectorlist[warpsectorcnt++] = i;
				break;
			case 11:
				xpanningsectorlist[xpanningsectorcnt++] = i;
				break;
			case 12:
				dax = 0x7fffffff;
				day = 0x7fffffff;
				dax2 = 0x80000000;
				day2 = 0x80000000;
				startwall = sector[i].wallptr;
				endwall = (short) (startwall + sector[i].wallnum - 1);
				for (j = startwall; j <= endwall; j++) {
					if (wall[j].x < dax)
						dax = wall[j].x;
					if (wall[j].y < day)
						day = wall[j].y;
					if (wall[j].x > dax2)
						dax2 = wall[j].x;
					if (wall[j].y > day2)
						day2 = wall[j].y;
					if (wall[j].lotag == 3)
						k = j;
				}
				if (wall[k].x == dax)
					dragxdir[dragsectorcnt] = -16;
				if (wall[k].y == day)
					dragydir[dragsectorcnt] = -16;
				if (wall[k].x == dax2)
					dragxdir[dragsectorcnt] = 16;
				if (wall[k].y == day2)
					dragydir[dragsectorcnt] = 16;

				dasector = wall[startwall].nextsector;
				dragx1[dragsectorcnt] = 0x7fffffff;
				dragy1[dragsectorcnt] = 0x7fffffff;
				dragx2[dragsectorcnt] = 0x80000000;
				dragy2[dragsectorcnt] = 0x80000000;
				startwall = sector[dasector].wallptr;
				endwall = (short) (startwall + sector[dasector].wallnum - 1);
				for (j = startwall; j <= endwall; j++) {
					if (wall[j].x < dragx1[dragsectorcnt])
						dragx1[dragsectorcnt] = wall[j].x;
					if (wall[j].y < dragy1[dragsectorcnt])
						dragy1[dragsectorcnt] = wall[j].y;
					if (wall[j].x > dragx2[dragsectorcnt])
						dragx2[dragsectorcnt] = wall[j].x;
					if (wall[j].y > dragy2[dragsectorcnt])
						dragy2[dragsectorcnt] = wall[j].y;
				}

				dragx1[dragsectorcnt] += (wall[sector[i].wallptr].x - dax);
				dragy1[dragsectorcnt] += (wall[sector[i].wallptr].y - day);
				dragx2[dragsectorcnt] -= (dax2 - wall[sector[i].wallptr].x);
				dragy2[dragsectorcnt] -= (day2 - wall[sector[i].wallptr].y);
				dragfloorz[dragsectorcnt] = sector[i].floorz;
				dragsectorlist[dragsectorcnt++] = i;
				sector[i].floorstat |= 64;
				break;
			case 13:
				startwall = sector[i].wallptr;
				endwall = (short) (startwall + sector[i].wallnum - 1);
				for (j = startwall; j <= endwall; j++) {
					if (wall[j].lotag == 4) {
						k = wall[wall[wall[wall[j].point2].point2].point2].point2;
						if ((wall[j].x == wall[k].x) && (wall[j].y == wall[k].y)) { // Door opens counterclockwise
							swingdoor[swingcnt].wall[0] = j;
							swingdoor[swingcnt].wall[1] = wall[j].point2;
							swingdoor[swingcnt].wall[2] = wall[wall[j].point2].point2;
							swingdoor[swingcnt].wall[3] = wall[wall[wall[j].point2].point2].point2;
							swingdoor[swingcnt].angopen = 1536;
							swingdoor[swingcnt].angclosed = 0;
							swingdoor[swingcnt].angopendir = -1;
						} else { // Door opens clockwise
							swingdoor[swingcnt].wall[0] = wall[j].point2;
							swingdoor[swingcnt].wall[1] = j;
							swingdoor[swingcnt].wall[2] = engine.lastwall(j);
							swingdoor[swingcnt].wall[3] = engine.lastwall(swingdoor[swingcnt].wall[2]);
							swingdoor[swingcnt].angopen = 512;
							swingdoor[swingcnt].angclosed = 0;
							swingdoor[swingcnt].angopendir = 1;
						}
						for (k = 0; k < 4; k++) {
							swingdoor[swingcnt].x[k] = wall[swingdoor[swingcnt].wall[k]].x;
							swingdoor[swingcnt].y[k] = wall[swingdoor[swingcnt].wall[k]].y;
						}

						swingdoor[swingcnt].sector = i;
						swingdoor[swingcnt].ang = swingdoor[swingcnt].angclosed;
						swingdoor[swingcnt].anginc = 0;
						swingcnt++;
					}
				}
				break;
			case 14:
				startwall = sector[i].wallptr;
				endwall = (short) (startwall + sector[i].wallnum - 1);
				dax = 0;
				day = 0;
				for (j = startwall; j <= endwall; j++) {
					dax += wall[j].x;
					day += wall[j].y;
				}
				revolvepivotx[revolvecnt] = dax / (endwall - startwall + 1);
				revolvepivoty[revolvecnt] = day / (endwall - startwall + 1);

				k = 0;
				for (j = startwall; j <= endwall; j++) {
					revolvex[revolvecnt][k] = wall[j].x;
					revolvey[revolvecnt][k] = wall[j].y;
					k++;
				}
				revolvesector[revolvecnt] = i;
				revolveang[revolvecnt] = 0;
				revolvecnt++;
				break;
			case 15:
				subwaytracksector[subwaytrackcnt][0] = i;
				subwaystopcnt[subwaytrackcnt] = 0;
				dax = 0x7fffffff;
				day = 0x7fffffff;
				dax2 = 0x80000000;
				day2 = 0x80000000;
				startwall = sector[i].wallptr;
				endwall = (short) (startwall + sector[i].wallnum - 1);
				for (j = startwall; j <= endwall; j++) {
					if (wall[j].x < dax)
						dax = wall[j].x;
					if (wall[j].y < day)
						day = wall[j].y;
					if (wall[j].x > dax2)
						dax2 = wall[j].x;
					if (wall[j].y > day2)
						day2 = wall[j].y;
				}

				for (j = startwall; j <= endwall; j++) {
					if (wall[j].lotag == 5) {
						if ((wall[j].x > dax) && (wall[j].y > day) && (wall[j].x < dax2) && (wall[j].y < day2)) {
							subwayx[subwaytrackcnt] = wall[j].x;
						} else {
							subwaystop[subwaytrackcnt][subwaystopcnt[subwaytrackcnt]] = wall[j].x;
							subwaystopcnt[subwaytrackcnt]++;
						}
					}
				}

				for (j = 1; j < subwaystopcnt[subwaytrackcnt]; j++)
					for (k = 0; k < j; k++)
						if (subwaystop[subwaytrackcnt][j] < subwaystop[subwaytrackcnt][k]) {
							s = subwaystop[subwaytrackcnt][j];
							subwaystop[subwaytrackcnt][j] = subwaystop[subwaytrackcnt][k];
							subwaystop[subwaytrackcnt][k] = s;
						}

				subwaygoalstop[subwaytrackcnt] = 0;
				for (j = 0; j < subwaystopcnt[subwaytrackcnt]; j++)
					if (Math.abs(subwaystop[subwaytrackcnt][j] - subwayx[subwaytrackcnt]) < Math
							.abs(subwaystop[subwaytrackcnt][subwaygoalstop[subwaytrackcnt]] - subwayx[subwaytrackcnt]))
						subwaygoalstop[subwaytrackcnt] = j;

				subwaytrackx1[subwaytrackcnt] = dax;
				subwaytracky1[subwaytrackcnt] = day;
				subwaytrackx2[subwaytrackcnt] = dax2;
				subwaytracky2[subwaytrackcnt] = day2;

				subwaynumsectors[subwaytrackcnt] = 1;
				for (j = 0; j < numsectors; j++)
					if (j != i) {
						startwall = sector[j].wallptr;
						if (wall[startwall].x > subwaytrackx1[subwaytrackcnt])
							if (wall[startwall].y > subwaytracky1[subwaytrackcnt])
								if (wall[startwall].x < subwaytrackx2[subwaytrackcnt])
									if (wall[startwall].y < subwaytracky2[subwaytrackcnt]) {
										if (sector[j].lotag == 16)
											sector[j].lotag = 17; // Make special subway door

										if (sector[j].floorz != sector[i].floorz) {
											sector[j].ceilingstat |= 64;
											sector[j].floorstat |= 64;
										}
										subwaytracksector[subwaytrackcnt][subwaynumsectors[subwaytrackcnt]] = (short) j;
										subwaynumsectors[subwaytrackcnt]++;
									}
					}

				subwayvel[subwaytrackcnt] = 64;
				subwaypausetime[subwaytrackcnt] = 2400;
				subwaytrackcnt++;
				break;
			case 30:
				warpsector2list[warpsector2cnt++] = i;
				break;
			case 99:
			case 98:
			case 97:
				if(cfg.bShowExit)
					show2dsector[i >> 3] |= 1 << (i & 7);
				break;
			}

			for (s = headspritesect[i]; s != -1; s = nextspritesect[s]) {

				if(!engine.getTile(sprite[s].picnum).hasSize())
					sprite[s].cstat &= ~1;

				switch (sprite[s].picnum) {
				case 51:
					if (mapnum == 0)
						nKickSprite = s;
					break;
				case 186: // fire
				case 187:
				case 188:
				case 189:
					sprite[s].cstat &= ~48;
				case 601: // umbrella
				case 602: // cookie
				case 603: // armor
				case 604: // coins
				case 605: // balls
				case 606: // pikes
				case 607: // shurikens
				case 608: // knifes
				case 609: // kunai
				case 700: // tree root1?
				case 701: // tree root2?
				case 702: // chocolate
				case 703: // flowers with roots
				case 705: // flower
				case 706: // mushroom
				case 707: // manna bottle
				case 710: // blue book
				case 711: // brown book
				case 712: // stone
				case 713: // wooden book
				case 714: // papirus
				case 720: // golden key
				case 721: // bronze key
				case 722: // silver key
				case 723: // gray key
				case 724: // green key
				case 725: // red key
				case 727: // gold coin
					sprite[s].cstat &= ~257;
					break;
				}
			}
		}

		ypanningwallcnt = 0;
		for (short w = 0; w < numwalls; w++) {
			if (wall[w].lotag == 1)
				ypanningwalllist[ypanningwallcnt++] = w;

			// GDX Blocked maskedwalls fix
			if ((wall[w].cstat & 1) != 0 && wall[w].nextwall != -1)
				wall[wall[w].nextwall].cstat |= 1;

			if (mapnum == 0) {
//				if (wall[w].lotag == 102) { //sound setup
//					dword_5AF64 = w;
//				} else if (wall[w].lotag == 103) { //music setup
//					dword_5AF60 = w;
//				} else
				if (wall[w].lotag == 121) { // train finish wall
					nTrainWall = w;
				}
//				else if (wall[w].lotag == 123) { //train start wall
//					dword_5AF6C = w;
//				}
				else if (wall[w].lotag == 124) { // door to difficult
					nDiffDoor = w;
				} else if (wall[w].lotag == 125) { // door to difficult backside
					nDiffDoorBack = w;
				}
			}
		}

		bActiveTrain = false;

		inienemies();

		if( recstat == 1 )
	    	rec = new DemoFile(100);

		for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
			InitPlayer(i, pos);
			if (mapnum > 1)
				gPlayer[i].loadPlayersInventory();
		}

		if (mapnum == 4) {
			if (sector[82].hitag == 0 && sector[82].lotag == 20) {
				sector[82].lotag = 21;
			}
		}

		if (mapnum == 11) {
			if (sector[22].floorz == -2048 && sector[22].lotag == 2 && sector[22].hitag == 3) {
				sector[22].floorz += 4096;
				for (short spr = headspritesect[22]; spr != -1; spr = nextspritesect[spr]) {
					sprite[spr].z += 4096;
				}
				sector[22].lotag = 1;
			}
		}

		if (mapnum == 21) {
			if (sector[32].ceilingz == sector[32].floorz && sector[32].lotag == 8 && sector[32].hitag == 2) {
				sector[32].ceilingz -= 8192;
				sector[32].floorz += 8192;
				sector[32].hitag = 0;
			}
		}

		if (mapnum == 32) {
			if (sector[29].floorz == -5120 && sector[29].lotag == 30) {
				sector[29].floorz += 8192;
			}
		}

		resetQuotes();
		GrabPalette();
		engine.srand(17);
		((LSPInput) game.pInput).reset();

		startmusic(maps[mapnum].music - 1);

		gNameShowTime = 500;
		game.pNet.ResetTimers();
		System.err.println(map);

		return true;
	}

	public void newgame(int num) {
		if (recstat == 1 && rec != null)
			rec.close();

		mapnum = num;
		engine.setTilesPath(num == 0 ? 0 : 1);
		String map = "lev" + num + ".map";
		game.nNetMode = NetMode.Single;

		for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
			gPlayer[i].nHealth = MAXHEALTH;
			gPlayer[i].nMana = MAXMANNA;
			Arrays.fill(gPlayer[i].nAmmo, (short) 0);
			gPlayer[i].nAmmo[1] = 100;
			gPlayer[i].nAmmo[7] = 1;
			gPlayer[i].calcRandomVariables();
		}

		updatechapter(mapnum);
		loadboard(map, null).setTitle("Loading " + map);
	}

	private void updatechapter(int map) {
		int mnum = maps[map].num & 0xFF;
		book = (mnum % 100) % 10;
		chapter = mnum / 100;
		verse = (mnum % 100) / 10;

		if(GameScreen.this != gDemoScreen)
			recstat = m_recstat;
	}

	public boolean NextMap() {
		int nextmap = maps[mapnum].nextmap[nNextMap - 1];
		nNextMap = 0;
		if (nextmap == 99) // the end
			return true;

		if (nextmap == 0)
			nextmap = 1;

		updatechapter(mapnum);

		gAutosaveRequest = true;
		changemap(nextmap);
		return false;
	}

	public void changemap(int num) {
		mapnum = num;

		updatechapter(mapnum);

		String map = "lev" + mapnum + ".map";
		loadboard(map, null).setTitle("Loading " + map);
	}

	@Override
	public void sndHandlePause(boolean pause) {
		Sounds.sndHandlePause(pause);
	}
}
