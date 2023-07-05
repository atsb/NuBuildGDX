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

package ru.m210projects.Powerslave.Screens;

import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Map.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.View.*;
import static ru.m210projects.Powerslave.Energy.*;
import static ru.m210projects.Powerslave.Snake.*;
import static ru.m210projects.Powerslave.Palette.*;
import static ru.m210projects.Powerslave.Light.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Player.*;
import static ru.m210projects.Powerslave.Weapons.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Factory.PSInput.*;
import static ru.m210projects.Powerslave.SpiritHead.*;
import static ru.m210projects.Powerslave.Cinema.*;
import static ru.m210projects.Powerslave.Menus.PSMenuUserContent.*;

import static ru.m210projects.Powerslave.LoadSave.*;
import static ru.m210projects.Powerslave.Object.*;
import static ru.m210projects.Powerslave.Sector.*;
import static ru.m210projects.Powerslave.Sprites.*;
import static ru.m210projects.Powerslave.Factory.PSMenuHandler.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Pragmas.*;

import java.util.Arrays;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;
import ru.m210projects.Build.Pattern.Tools.Interpolation.ILoc;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Powerslave.Config.PsKeys;
import ru.m210projects.Powerslave.Main;
import ru.m210projects.Powerslave.Factory.PSMenuHandler;
import ru.m210projects.Powerslave.Main.UserFlag;
import ru.m210projects.Powerslave.Sound;
import ru.m210projects.Powerslave.Menus.MenuInterfaceSet;
import ru.m210projects.Powerslave.Type.EpisodeInfo;
import ru.m210projects.Powerslave.Type.Input;
import ru.m210projects.Powerslave.Type.PlayerStruct;

public class GameScreen extends GameAdapter {

	private Main game;
	private int nonsharedtimer;
	public int gNameShowTime;

	public GameScreen(Main game) {
		super(game, gLoadingScreen);
		this.game = game;
	}

	@Override
	public void show() {
		super.show();

		bPlayback = false;
		bInDemo = false;

		sndPlayMusic();
	}

	protected void UpdateInputs(BuildNet net) {
		for (short i = connecthead; i >= 0; i = connectpoint2[i]) {
			Input src = (Input) net.gFifoInput[net.gNetFifoTail & 0xFF][i];
			sPlayerInput[i].Copy(src);

			if ((sPlayerInput[i].bits & nPause) != 0) {
				game.gPaused = !game.gPaused;
				sndHandlePause(game.gPaused);
			}
		}
		net.gNetFifoTail++;
	}

	@Override
	public void ProcessFrame(BuildNet net) {
		FixPalette();
		UpdateInputs(net);
		nonsharedtimer = totalclock;

//		if (BuildGdx.input.isKeyPressed(Keys.H)) {
//			SPRITE pSprite = sprite[PlayerList[0].spriteId];
//			int dx = sintable[(pSprite.ang + 512) & 0x7FF];
//			int dy = sintable[pSprite.ang];
//			int dz = (nVertPan[0] - 92) << 8;
//			engine.hitscan(pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum, dx, dy, dz << 4, pHitInfo, 0xFFFF0030);
//
//			System.err.println(sector[pHitInfo.hitsect].floorpicnum);
//		}

		if (game.gPaused || (!game.isCurrentScreen(gGameScreen) && !game.isCurrentScreen(gDemoScreen)))
			return;

		if (game.isCurrentScreen(gGameScreen) && ((game.menu.gShowMenu || Console.IsShown())))
			return;

		UpdateSounds();
		if (levelnum == 20) {
			lCountDown--;
			DrawClock();
			if (nRedTicks != 0) {
				if (--nRedTicks <= 0)
					DoRedAlert(0);
			}
			nAlarmTicks--;
			nButtonColor--;
			if (nAlarmTicks <= 0)
				DoRedAlert(1);

			if (lCountDown <= 0)
				DoFailedFinalScene();
		}

		for (short i = connecthead; i >= 0; i = connectpoint2[i])
			PlayerList[i].UpdatePlayerLoc();

		UndoFlashes();
		DoLights();
		if (nFreeze != 0) {
			if (gCurrentEpisode != gOriginalEpisode) {
				if (mUserFlag == UserFlag.UserMap) {
					BuildGdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							nPlayerLives[0] = 0;
							game.EndGame();
						}
					});
				} else
					levelnew = levelnum + 1;
			} else {
				if (nFreeze == 1 || nFreeze == 2) {
					DoSpiritHead();

					if (cfg.bSubtitles && nFreeze == 2 && nHeadStage == 5 && nHeight + nCrawlY > 0
							&& totalclock >= nextclock) {
						nextclock = totalclock + 14;
						nCrawlY--;
					}
				}
			}
		} else {
			ExecObjects();
			CleanRunRecs();
		}
		MoveStatus();
		DoBubbleMachines();
		DoDrips();
		DoMovingSects();
		DoRegenerates();

		if (followmode) {
			followa += followang;

			followx += (followvel * sintable[(512 + 2048 - followa) & 2047]) >> 10;
			followy += (followvel * sintable[(512 + 1024 - 512 - followa) & 2047]) >> 10;

			followx += (followsvel * sintable[(512 + 1024 - 512 - followa) & 2047]) >> 10;
			followy -= (followsvel * sintable[(512 + 2048 - followa) & 2047]) >> 10;
		}

		if (levelnum == 20) {
			DoFinale();
			if (lCountDown < 1800 && nDronePitch < 2400 && lFinaleStart == 0) {
				nDronePitch += 64;
				BendAmbientSound();
			}
		}

		if (totalvel[nLocalPlayer] != 0) {
			bobangle += 56;
			bobangle &= 0x7FF;
		} else {
			bobangle = 0;
		}

		totalmoves++;
		moveframes--;

		checkNextMap();
	}

	@Override
	public void DrawWorld(float smooth) {
		if (PlayerList[nLocalPlayer].spriteId == -1)
			return;

		SPRITE pPlayer = sprite[PlayerList[nLocalPlayer].spriteId];
		PlayerStruct pp = PlayerList[0]; // screenpeek XXX
		short nSector = -1;

		if (nSnakeCam >= 0) {
			SPRITE pSnake = sprite[SnakeList[nSnakeCam].nSprite[0]];
			nCamerax = pSnake.x;
			nCameray = pSnake.y;
			nCameraz = pSnake.z;
			nCameraa = pSnake.ang;
			nCamerapan = 92;
			nSector = pSnake.sectnum;

			ILoc oldLoc = game.pInt.getsprinterpolate(SnakeList[nSnakeCam].nSprite[0]);
			if (oldLoc != null) {
				nCamerax = oldLoc.x + mulscale(nCamerax - oldLoc.x, (int) smooth, 16);
				nCameray = oldLoc.y + mulscale(nCameray - oldLoc.y, (int) smooth, 16);
				nCameraz = oldLoc.z + mulscale(nCameraz - oldLoc.z, (int) smooth, 16);
				nCameraa = oldLoc.ang + mulscale(((pSnake.ang - oldLoc.ang + 1024) & 0x7FF) - 1024, (int) smooth, 16);
			}

			SetGreenPal();
		} else {
			RestoreGreenPal();
			nCamerax = pPlayer.x;
			nCameray = pPlayer.y;
			nCameraz = pPlayer.z + pp.eyelevel;
			nCameraa = pp.ang;
			nCamerapan = pp.horiz;
			nSector = nPlayerViewSect[nLocalPlayer];
		}

		if (nSector != -1 && initsect != -1)
			UpdateMap();

		if (bCamera) {
			if ((!game.menu.gShowMenu && !Console.IsShown()) || game.isCurrentScreen(gDemoScreen)) {
				nCamerax = pp.prevView.x + mulscale((nCamerax - pp.prevView.x), (int) smooth, 16);
				nCameray = pp.prevView.y + mulscale((nCameray - pp.prevView.y), (int) smooth, 16);
				nCameraz = pp.prevView.z + mulscale((nCameraz - pp.prevView.z), (int) smooth, 16);
				nCameraa = pp.prevView.ang
						+ (BClampAngle(nCameraa + 1024 - pp.prevView.ang) - 1024) * smooth / 65536.0f;
			}

			engine.clipmove(nCamerax, nCameray, nCameraz, nSector, (int) (-2000 * BCosAngle(nCameraa)),
					(int) (-2000 * BSinAngle(nCameraa)), 64, 0, 0, CLIPMASK1);

			nCamerapan = 92;
			if (clipmove_sectnum != -1) {
				nCamerax = clipmove_x;
				nCameray = clipmove_y;
				nCameraz = clipmove_z;
				nSector = clipmove_sectnum;
			}
		} else {
			if (nSnakeCam == -1) {
//				if (screenpeek == myconnectindex && numplayers > 1)
//		    	{
//		       	 	PSNetwork net = game.net;
//		       		nCamerax = net.predictOld.x+mulscale((net.predict.x-net.predictOld.x),smoothratio, 16);
//		       		nCameray = net.predictOld.y+mulscale((net.predict.y-net.predictOld.y),smoothratio, 16);
//		            nCameraa = net.predictOld.ang + (BClampAngle(net.predict.ang+1024-net.predictOld.ang)-1024) * smoothratio / 65536.0f;
//		      	}
//		     	else
				{
					if ((!game.menu.gShowMenu && !Console.IsShown()) || game.isCurrentScreen(gDemoScreen)) {
						nCamerax = pp.prevView.x + mulscale((nCamerax - pp.prevView.x), (int) smooth, 16);
						nCameray = pp.prevView.y + mulscale((nCameray - pp.prevView.y), (int) smooth, 16);
						nCameraz = pp.prevView.z + mulscale((nCameraz - pp.prevView.z), (int) smooth, 16);
						nCameraa = pp.prevView.ang
								+ (BClampAngle(nCameraa + 1024 - pp.prevView.ang) - 1024) * smooth / 65536.0f;
						nCamerapan = pp.prevView.horiz + (pp.horiz - pp.prevView.horiz) * smooth / 65536.0f;
					}
				}

				nCameraz += nQuake[nLocalPlayer];
				if (nCameraz > sector[pPlayer.sectnum].floorz)
					nCameraz = sector[pPlayer.sectnum].floorz;
				nCameraa += (nQuake[nLocalPlayer] >> 7) % 31;
			}
		}

		int cz = sector[nSector].ceilingz + 256 * 8;
		if (cz > nCameraz)
			nCameraz = cz;

		int fz = sector[nSector].floorz - 256 * 8;
		if (fz < nCameraz)
			nCameraz = fz;

		nCamerapan += 8; // GDX 14.12.2019

		if (nFreeze != 0 || nOverhead != 2) {
			engine.drawrooms(nCamerax, nCameray, nCameraz, nCameraa, nCamerapan, nSector);
			analyzesprites((int) smooth);
			engine.drawmasks();
		}

		if (nFreeze == 0 && nOverhead > 0) {
			if (followmode) {
				nCamerax = followx;
				nCameray = followy;
				nCameraa = followa;
			}

			if (nOverhead == 2) {
				engine.clearview(96);
				engine.drawmapview(nCamerax, nCameray, zoom, (short) nCameraa);
			}
			engine.drawoverheadmap(nCamerax, nCameray, zoom, (short) nCameraa);

			if (followmode)
				game.getFont(0).drawText(5, 25, "Follow mode", 0, 0, TextAlign.Left, 2 | 256, true);

			if (mUserFlag == UserFlag.UserMap)
				game.getFont(0).drawText(5, 15, "User map: " + boardfilename, 0, 7, TextAlign.Left, 2 | 256, true);
			else if (levelnum > 0) {
				if (gCurrentEpisode.gMapInfo.get(levelnum - 1) != null)
					game.getFont(0).drawText(5, 15, gCurrentEpisode.gMapInfo.get(levelnum - 1).title, 0, 7,
							TextAlign.Left, 2 | 256, true);
			}
		}

		if (nPalDiff != 0 && engine.glrender() != null)
			engine.showfade();
	}

	@Override
	public void DrawHud(float smooth) {
		if (game.gPaused)
			game.getFont(1).drawText(160, 80, "PAUSE", 0, 0, TextAlign.Center, 2, true);

		if (nFreeze != 0) {
			nSnakeCam = -1;
			if (game.pInput.ctrlGetInputKey(GameKeys.Menu_Toggle, true)
					|| game.pInput.ctrlGetInputKey(GameKeys.Open, true)) {
				levelnew = levelnum + 1;
				return;
			}

			if (nFreeze == 2) {
				if (nHeadStage == 4) {
					short spr = PlayerList[nLocalPlayer].spriteId;
					sprite[spr].cstat |= 0x8000;
					short dang = (short) (nCameraa - sprite[spr].ang);
					if (klabs(dang) > 10) {
						inita -= dang >> 3;
						return;
					}

					if (cfg.bSubtitles) {
						if (levelnum == 1)
							ReadyCinemaText(1);
						else
							ReadyCinemaText(5);
					}

					nHeadStage = 5;
				} else {
					if (cfg.bSubtitles && (!AdvanceCinemaText())) {
						levelnew = levelnum + 1;
					}
				}
			}
			return;
		}

		if (nSnakeCam >= 0) {
			game.getFont(0).drawText(160, 5, "S E R P E N T C A M", -128, 0, TextAlign.Center, 2, true);
			return;
		}

		if (nOverhead != 2)
			DrawWeapons();

		if (!game.menu.gShowMenu || (game.menu.getCurrentMenu() instanceof MenuInterfaceSet)) {
			DrawStatus();

			if (game.isCurrentScreen(gGameScreen) && totalclock < gNameShowTime) {
				int transp = 0;
				if (totalclock > gNameShowTime - 20)
					transp = 1;
				if (totalclock > gNameShowTime - 10)
					transp = 33;

				if (cfg.showMapInfo != 0 && !game.menu.gShowMenu) {
					if (mUserFlag != UserFlag.UserMap && levelnum > 0) {
						if (gCurrentEpisode.gMapInfo.get(levelnum - 1) != null)
							game.getFont(1).drawText(160, 100, gCurrentEpisode.gMapInfo.get(levelnum - 1).title, -128,
									0, TextAlign.Center, 2 | transp, true);
					} else if (boardfilename != null)
						game.getFont(1).drawText(160, 100, FileUtils.getFullName(boardfilename), -128, 0,
								TextAlign.Center, 2 | transp, true);
				}
			}

			int y = 155;
			if (cfg.nScreenSize == 0)
				y = 195;

			viewDrawStats(5, y, cfg.gStatSize);
		}
	}

	@Override
	public void PostFrame(BuildNet net) {
		PSMenuHandler menu = game.menu;
		if (gAutosaveRequest) {
			if (gClassicMode) {
				if (nSaveName == null) {
					if (!menu.isOpened(menu.mMenus[SAVE]))
						menu.mOpen(menu.mMenus[SAVE], -1);
				} else {
					if (captBuffer != null) {
						savegame(nSaveName, nSaveFile);
						gAutosaveRequest = false;
					} else
						gGameScreen.capture(160, 100);
				}
			} else {
				if (captBuffer != null) {
					savegame("[autosave]", "autosave.sav");
					gAutosaveRequest = false;
				} else
					gGameScreen.capture(160, 100);
			}
		}

		if (gQuickSaving) {
			if (captBuffer != null) {
				savegame("[quicksave_" + +quickslot + "]", "quicksav" + quickslot + ".sav");
				quickslot ^= 1;
				gQuickSaving = false;
			} else
				gGameScreen.capture(160, 100);
		}
	}

	@Override
	public void KeyHandler() {
		PSMenuHandler menu = game.menu;
		if (menu.gShowMenu) {
			engine.handleevents();
			menu.mKeyHandler(game.pInput, BuildGdx.graphics.getDeltaTime());
			return;
		}

		if (Console.IsShown())
			return;

		if (nFreeze != 0)
			return;

		BuildControls input = game.pInput;
		if (input.ctrlGetInputKey(GameKeys.Menu_Toggle, true)) {
			StopAllSounds();
			menu.mOpen(menu.mMenus[GAME], -1);
		}

		if (nOverhead != 0) {
			int j = totalclock - nonsharedtimer;
			nonsharedtimer += j;
			if (input.ctrlGetInputKey(GameKeys.Enlarge_Hud, false))
				zoom += mulscale(j, Math.max(zoom, 256), 6);
			if (input.ctrlGetInputKey(GameKeys.Shrink_Hud, false))
				zoom -= mulscale(j, Math.max(zoom, 256), 6);

			if ((zoom > 2048))
				zoom = 2048;
			if ((zoom < 48))
				zoom = 48;

			if (input.ctrlGetInputKey(PsKeys.Map_Follow_Mode, true)) {
				followmode = !followmode;
				if (followmode) {
					followx = initx;
					followy = inity;
					followa = inita;
				}
				StatusMessage(500, "Follow mode " + (followmode ? "ON" : "OFF"), nLocalPlayer);
			}
		} else {
			if (input.ctrlGetInputKey(GameKeys.Enlarge_Hud, true)) {
				if (cfg.nScreenSize > 0) {
					cfg.nScreenSize = BClipLow(cfg.nScreenSize - 1, 0);
				}
			}
			if (input.ctrlGetInputKey(GameKeys.Shrink_Hud, true)) {
				if (cfg.nScreenSize < 2) {
					cfg.nScreenSize = BClipHigh(cfg.nScreenSize + 1, 2);
				}
			}
		}

		if (input.ctrlGetInputKey(GameKeys.Map_Toggle, true)) {
			setOverHead(nOverhead);
		}

		if (input.ctrlGetInputKey(PsKeys.Show_SaveMenu, true)) {
			if (numplayers > 1 || gClassicMode)
				return;
			if (PlayerList[nLocalPlayer].HealthAmount != 0) {
				gGameScreen.capture(160, 100);
				menu.mOpen(menu.mMenus[SAVE], -1);
			}
		}

		if (input.ctrlGetInputKey(PsKeys.Show_LoadMenu, true)) {
			if (numplayers > 1)
				return;
			menu.mOpen(menu.mMenus[LOAD], -1);
		}

		if (input.ctrlGetInputKey(PsKeys.Show_SoundSetup, true))
			menu.mOpen(menu.mMenus[AUDIO], -1);

		if (input.ctrlGetInputKey(PsKeys.Show_Options, true))
			menu.mOpen(menu.mMenus[OPTIONS], -1);

		if (input.ctrlGetInputKey(PsKeys.Quicksave, true)) { // quick save
			quicksave();
		}

		if (input.ctrlGetInputKey(PsKeys.Toggle_messages, true)) {
			cfg.gShowMessages = !cfg.gShowMessages;
			if (cfg.gShowMessages)
				StatusMessage(500, "Messages on", nLocalPlayer);
		}

		if (input.ctrlGetInputKey(PsKeys.Quickload, true)) { // quick load
			quickload();
		}

		if (input.ctrlGetInputKey(PsKeys.AutoRun, true)) {
			cfg.gAutoRun = !cfg.gAutoRun;
			StatusMessage(500, "Autorun " + (cfg.gAutoRun ? "ON" : "OFF"), nLocalPlayer);
		}

		if (input.ctrlGetInputKey(PsKeys.Third_View, true)) {
			bCamera = !bCamera;
			StatusMessage(500, "Third person view " + (bCamera ? "ON" : "OFF"), nLocalPlayer);
		}

		if (input.ctrlGetInputKey(PsKeys.Quit, true))
			menu.mOpen(menu.mMenus[QUIT], -1);

		if (input.ctrlGetInputKey(PsKeys.Gamma, true)) {
			menu.mOpen(menu.mMenus[COLORCORR], -1);
		}

		if (input.ctrlGetInputKey(PsKeys.Make_Screenshot, true)) {
			makeScreenshot();
		}
	}

	protected void setOverHead(int mode) {
		switch (mode) {
		case 0:
			if (cfg.gOverlayMap != 0)
				nOverhead = 1;
			else
				nOverhead = 2;
			break;
		case 1:
			if (cfg.gOverlayMap == 1)
				nOverhead = 0;
			else
				nOverhead = 2;
			break;
		default:
			nOverhead = 0;
			break;
		}
	}

	protected void makeScreenshot() {
		String name = "scrxxxx.png";
		if (mUserFlag == UserFlag.UserMap)
			name = "scr-" + FileUtils.getFullName(boardfilename) + "-xxxx.png";
		else
			name = "scr-map" + levelnum + "-xxxx.png";
		String filename = pEngine.screencapture(name);
		if (filename != null)
			StatusMessage(500, filename + " saved", nLocalPlayer);
		else
			StatusMessage(500, "Screenshot not saved. Access denied!", nLocalPlayer);
	}

	@Override
	public void sndHandlePause(boolean pause) {
		Sound.sndHandlePause(pause);
	}

	@Override
	protected void startboard(Runnable startboard) {
		gPrecacheScreen.init(false, startboard);
		game.changeScreen(gPrecacheScreen);
	}

	@Override
	protected boolean prepareboard(String map) {
		if (!LoadLevel(map, levelnew)) {
			bMapCrash = true;
			return false;
		}

		gNameShowTime = 500;
		StopAllSounds();

		if (!bPlayback)
			StopMusic();
		levelnew = -1;
		lastlevel = levelnum;
		nOverhead = 0;
		game.gPaused = false;
		Arrays.fill(show2dsector, (byte) 0);
		Arrays.fill(show2dwall, (byte) 0);
		Arrays.fill(show2dsprite, (byte) 0);

		for (int i = 0; i < numplayers; i++) {
			SetSavePoint(i, initx, inity, initz, initsect, inita);
			RestartPlayer(i);
			InitPlayerKeys(i);
		}

		UpdateScreenSize();
		InitStatus();
		game.pNet.ResetNetwork();
		game.pNet.ResetTimers();
		game.pNet.ready2send = false;
//        ResetView(v64, v65);

		totalmoves = 0;
		GrabPalette();

		moveframes = 0;
		RefreshStatus();

		System.err.println(map);
		return true;
	}

	public static void DoClockBeep() {
		for (int i = headspritestat[407]; i != -1; i = nextspritestat[i]) {
			PlayFX2(StaticSound[74], i);
		}
	}

	public static void DrawClock() {
		int nCount = lCountDown / 30;
		if (nCount != nClockVal) {
			Arrays.fill(engine.loadtile(3603), (byte) 255);

			nClockVal = nCount;
			DoClockBeep();

			int x = 49;
			while (nCount != 0) {
				int nNumber = 3606 + (nCount & 0xF);
				Tile pic = engine.getTile(nNumber);
				CopyTileToBitmap(nNumber, 3603, x - (pic.getWidth() / 2), 32 - (pic.getHeight() / 2));
				x -= 15;
				nCount /= 16;
			}
			engine.getrender().invalidatetile(3603, -1, -1);
		}

		DoEnergyTile();
	}

	private static void DoRedAlert(int a1) {
		if (a1 != 0) {
			nAlarmTicks = 69;
			nRedTicks = 30;
		}
		for (int i = headspritestat[405]; i != -1; i = nextspritestat[i]) {
			if (a1 != 0) {
				PlayFXAtXYZ(StaticSound[73], sprite[i].x, sprite[i].y, sprite[i].z, sprite[i].sectnum);
				AddFlash(sprite[i].sectnum, sprite[i].x, sprite[i].y, sprite[i].z, 192);
			}
		}
	}

	public static void CopyTileToBitmap(int nSource, int nDest, int cx, int cy) {
		Tile src = engine.getTile(nSource);
		if (src.data == null)
			engine.loadtile(nSource);

		Tile dst = engine.getTile(nDest);

		byte[] pSource = src.data;
		byte[] pDest = dst.data;

		int sptr = 0;
		int dptr = ((dst.getHeight() * cx) + cy);
		for (int i = 0, j; i < src.getWidth(); i++) {
			for (j = 0; j < src.getHeight(); j++) {
				byte col = pSource[sptr++];
				if (col != -1 && (dptr + j) < pDest.length)
					pDest[dptr + j] = col;
			}
			dptr += dst.getHeight();
		}
	}

	public void changemap(int num, Runnable prestart) {
		if (num < 1)
			return;
		boardfilename = gCurrentEpisode.gMapInfo.get(num - 1).path;
		levelnew = num;
		String name = "Loading " + gCurrentEpisode.gMapInfo.get(num - 1).title;
		loadboard(boardfilename, prestart).setTitle(name);
		game.pInput.resetMousePos();
	}

	public void training() {
		newgame(gTrainingEpisode, 1, true);
	}

	public void newgame(Object item, int nLevel, boolean classic) {
		nBestLevel = nLevel - 1;
		lastlevel = -1;
		zoom = 768;
		lastload = null;
		bCamera = false;
		pNet.ready2send = false;
		game.nNetMode = NetMode.Single;
		gClassicMode = classic;
		bPlayback = false;

		UserFlag flag = UserFlag.None;
		if (item instanceof EpisodeInfo) {
			EpisodeInfo game = (EpisodeInfo) item;
			if (!game.equals(gTrainingEpisode) && !game.equals(gOriginalEpisode)) {
				if (!checkEpisodeResources(game))
					return;

				flag = UserFlag.Addon;
				Console.Println("Start user episode: " + game.Title);
			} else
				resetEpisodeResources(game);
		} else if (item instanceof FileEntry) {
			flag = UserFlag.UserMap;
			boardfilename = ((FileEntry) item).getPath();
			levelnum = 0;
			levelnew = 0;
			resetEpisodeResources(null);
			Console.Println("Start user map: " + ((FileEntry) item).getName());
		}
		mUserFlag = flag;

		PlayerCount = 0;
		for (int i = 0; i < numplayers; i++) {
			int nPlayer = GrabPlayer();
			if (nPlayer == -1) {
				System.err.println("Can't create local player");
				return;
			}
			InitPlayerInventory(nPlayer);
		}

		if (!gClassicMode) {
			PlayerList[nLocalPlayer].HealthAmount = 800;
			if (nNetPlayerCount != 0)
				PlayerList[nLocalPlayer].HealthAmount = 1600;
		}

		if (mUserFlag == UserFlag.UserMap) {
			gGameScreen.loadboard(boardfilename, null).setTitle("Loading " + boardfilename);
			game.pInput.resetMousePos();
		} else if (gCurrentEpisode != null) {
			if (gCurrentEpisode.equals(gOriginalEpisode))
				gMap.showMap(nLevel, nLevel, nBestLevel);
			else
				changemap(nLevel, null);
		} else
			game.show(); // can't restart the level
	}

	public boolean isOriginal() {
		return false;
	}

}
