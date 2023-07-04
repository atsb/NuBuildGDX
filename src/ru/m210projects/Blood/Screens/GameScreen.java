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

package ru.m210projects.Blood.Screens;

import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.Factory.BloodMenuHandler.*;
import static ru.m210projects.Blood.PLAYER.*;
import static ru.m210projects.Blood.QAV.kQFrameCorner;
import static ru.m210projects.Blood.QAV.kQFrameScale;
import static ru.m210projects.Blood.QAV.kQFrameTranslucent;
import static ru.m210projects.Blood.QAV.kQFrameUnclipped;
import static ru.m210projects.Blood.QAV.kQFrameYFlip;
import static ru.m210projects.Blood.Actor.IsUnderwaterSector;
import static ru.m210projects.Blood.Actor.actPostProcess;
import static ru.m210projects.Blood.Actor.actProcessSprites;
import static ru.m210projects.Blood.EVENT.evProcess;
import static ru.m210projects.Blood.Gameutils.ClipHigh;
import static ru.m210projects.Blood.Gameutils.ClipLow;
import static ru.m210projects.Blood.Gameutils.toCharArray;
import static ru.m210projects.Blood.LEVELS.currentEpisode;
import static ru.m210projects.Blood.LEVELS.gUserMapInfo;
import static ru.m210projects.Blood.LEVELS.loadMapInfo;
import static ru.m210projects.Blood.LOADSAVE.*;
import static ru.m210projects.Blood.SECTORFX.DoSectorPanning;
import static ru.m210projects.Blood.SOUND.*;
import static ru.m210projects.Blood.Strings.followmode;
import static ru.m210projects.Blood.Strings.paused;
import static ru.m210projects.Blood.Strings.scrollmode;
import static ru.m210projects.Blood.Trigger.trProcessBusy;
import static ru.m210projects.Blood.Types.GAMEINFO.*;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.seqKillAll;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.seqProcess;
import static ru.m210projects.Blood.View.*;
import static ru.m210projects.Blood.Weapon.WeaponDraw;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Gameutils.BClampAngle;

import ru.m210projects.Blood.Config.BloodKeys;
import ru.m210projects.Blood.Types.INPUT;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Blood.DB;

import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.LEVELS.*;
import static ru.m210projects.Blood.Cheats.*;
import static ru.m210projects.Blood.DB.kItemAsbestosArmor;
import static ru.m210projects.Blood.DB.kItemBase;
import static ru.m210projects.Blood.DB.kItemCrystalBall;
import static ru.m210projects.Blood.DB.xsector;

import ru.m210projects.Blood.Main;
import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.QAV;
import ru.m210projects.Blood.Main.UserFlag;
import ru.m210projects.Blood.Menus.MenuInterfaceSet;
import ru.m210projects.Blood.SOUND;
import ru.m210projects.Blood.Factory.BloodMenuHandler;
import ru.m210projects.Blood.Types.BloodIniFile;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;
import ru.m210projects.Build.Render.Renderer.RenderType;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Build.Types.Tile;

public class GameScreen extends GameAdapter {

	public int gNameShowTime;
	private int nonsharedtimer;

	private final Main game;

	public GameScreen(Main game) {
		super(game, Main.gLoadingScreen);
		this.game = game;
	}

	@Override
	public void ProcessFrame(BuildNet net) {
		nonsharedtimer = totalclock;
		for (short i = connecthead; i >= 0; i = connectpoint2[i]) {
			INPUT src = (INPUT) net.gFifoInput[net.gNetFifoTail & 0xFF][i];
			gPlayer[i].pInput.syncFlags = src.syncFlags;
			gPlayer[i].pInput.Forward = src.Forward;
			gPlayer[i].pInput.Turn = src.Turn;
			gPlayer[i].pInput.Strafe = src.Strafe;
			gPlayer[i].pInput.mlook = src.mlook;
			gPlayer[i].pInput.Run = src.Run;
			gPlayer[i].pInput.Jump = src.Jump;
			gPlayer[i].pInput.Crouch = src.Crouch;
			gPlayer[i].pInput.Shoot = src.Shoot;
			gPlayer[i].pInput.AltShoot = src.AltShoot;
			gPlayer[i].pInput.Lookup = src.Lookup;
			gPlayer[i].pInput.Lookdown = src.Lookdown;
			gPlayer[i].pInput.TurnAround = src.TurnAround;
			gPlayer[i].pInput.Use |= src.Use;
			gPlayer[i].pInput.InventoryLeft |= src.InventoryLeft;
			gPlayer[i].pInput.InventoryRight |= src.InventoryRight;
			gPlayer[i].pInput.InventoryUse |= src.InventoryUse;
			gPlayer[i].pInput.PrevWeapon |= src.PrevWeapon;
			gPlayer[i].pInput.NextWeapon |= src.NextWeapon;
			gPlayer[i].pInput.HolsterWeapon |= src.HolsterWeapon;
			gPlayer[i].pInput.LookCenter |= src.LookCenter;
			gPlayer[i].pInput.LookLeft = src.LookLeft;
			gPlayer[i].pInput.LookRight = src.LookRight;
			gPlayer[i].pInput.Pause |= src.Pause;
			gPlayer[i].pInput.Quit |= src.Quit;
			gPlayer[i].pInput.Restart |= src.Restart;
			gPlayer[i].pInput.CrouchMode = src.CrouchMode;
			gPlayer[i].pInput.LastWeapon = src.LastWeapon;
			gPlayer[i].pInput.UseBeastVision |= src.UseBeastVision;
			gPlayer[i].pInput.UseCrystalBall |= src.UseCrystalBall;
			gPlayer[i].pInput.UseJumpBoots |= src.UseJumpBoots;
			gPlayer[i].pInput.UseMedKit |= src.UseMedKit;
			if (src.newWeapon != 0)
				gPlayer[i].pInput.newWeapon = src.newWeapon;
		}
		net.gNetFifoTail++;
		net.CalcChecksum();

		for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
			if (gPlayer[i].pInput.Quit) {
				gPlayer[i].pInput.Quit = false;
				game.pNet.NetDisconnect(i);
				return;
			}

			if (gPlayer[i].pInput.Restart) {
				gPlayer[i].pInput.Restart = false;
				gGameScreen.loadboard(pGameInfo.zLevelName, null);
				return;
			}

			if (gPlayer[i].pInput.Pause) {
				game.gPaused = !game.gPaused;
				sndHandlePause(game.gPaused);
				if (game.gPaused && pGameInfo.nGameType > kNetModeOff && numplayers > 1) {
					viewSetMessage(game.net.gProfile[i].name + " paused the game", -1);
				}
				gPlayer[i].pInput.Pause = false;
			}
		}

		if (game.gPaused || (!game.isCurrentScreen(gGameScreen) && !game.isCurrentScreen(gDemoScreen)))
			return;

		if (game.isCurrentScreen(gGameScreen)
				&& (pGameInfo.nGameType == kNetModeOff && (game.menu.gShowMenu || Console.IsShown())))
			return;

		for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
//			if(pGameInfo.nGameType != kNetModeOff) //GDX 13/12/2018 Will make lags in multiplayer
//				viewUpdatePlayerLoc(gPlayer[i]);
//			else
			viewBackupView(i);
			playerMove(gPlayer[i]); // Move player
		}

		trProcessBusy(); // process busy triggers
		evProcess(gFrameClock); // process event queue
		seqProcess(kFrameTicks);
		DoSectorPanning();
		actProcessSprites(); // Actor code: projectiles, etc.
		actPostProcess(); // clean up deleted sprites or sprites changing status
		net.CorrectPrediction();
		sndProcess();
		ambProcess();
		sfxUpdate3DSounds();

		gFrame++;
		gFrameClock += kFrameTicks;

		if ((pGameInfo.uGameFlags & EndOfLevel) != 0) {
			game.pNet.ready2send = false;
			seqKillAll();
			if (game.isCurrentScreen(gDemoScreen))
				return;

			if ((pGameInfo.uGameFlags & EndOfGame) != 0) {
				if (pGameInfo.nGameType == kNetModeOff) {
					Runnable rEndGame = new Runnable() {
						@Override
						public void run() {
							game.resetState();
							game.changeScreen(gMenuScreen);
							game.menu.mOpen(game.menu.mMenus[CREDITS], -1);
						}
					};
					if (checkCutsceneB()) {
						game.changeScreen(gCutsceneScreen.setCallback(rEndGame).escSkipping(true));
					} else
						rEndGame.run();
				} else
					game.Disconnect();

				pGameInfo.uGameFlags &= ~(EndOfLevel | EndOfGame);
			} else
				game.changeScreen(gStatisticScreen);
		}
	}

	@Override
	public void DrawWorld(float smooth) {
		viewDrawScreen(gViewIndex, (int) smooth);
	}

	@Override
	public void DrawHud(float smooth) {
		PLAYER gView = gPlayer[gViewIndex];
		if (gView == null || gView.pSprite == null)
			return;

		if ((gViewMode == kView3D) || gViewMode == kView2D) {
			short nSector = gView.pSprite.sectnum;

			if (gViewPos == kViewPosCenter) {
				Tile pic = engine.getTile(2319);
				if ((!game.menu.gShowMenu || (game.menu.getCurrentMenu() instanceof MenuInterfaceSet))
						&& cfg.gCrosshair) {
					engine.rotatesprite((int) ((viewCrossX - (pic.getWidth() * 320 / xdim)) * 65536.0f),
							(int) ((viewCrossY + (pic.getHeight() * 240 / ydim)) * 65536.0f), viewCrossZoom, 0, 2319, 0,
							0, 8, 0, 0, xdim - 1, ydim - 1);
				}

				int nShade = 0;
				int nPLU = 0;
				if (nSector != -1) {
					nShade = sector[nSector].floorshade;
					if (sector[nSector].extra > 0 && xsector[sector[nSector].extra].color)
						nPLU = sector[nSector].floorpal;
				}

				int nZoom = 65536;
				if (SplitScreen)
					nZoom = 32768;

				WeaponDraw(gView, nShade, viewWeaponX, viewWeaponY, nPLU, nZoom);
				if (gView.pXsprite.burnTime > 60)
					viewDrawBurn(gView.pXsprite.burnTime);
			}

			if (inventoryCheck(gView, kInventoryDivingSuit)) {
				engine.rotatesprite(0, 0, 65536, 0, 2344, 0, 0, kQFrameScale | kQFrameCorner | kQFrameUnclipped | 256,
						gViewX0, gViewY0, gViewX1, gViewY1);
				engine.rotatesprite(320 << 16, 0, 65536, 1024, 2344, 0, 0,
						kQFrameScale | kQFrameCorner | kQFrameUnclipped | kQFrameYFlip | 512, gViewX0, gViewY0, gViewX1,
						gViewY1);
				engine.rotatesprite(0, 200 << 16, 65536, 0, 2344, 0, 0,
						kQFrameScale | kQFrameCorner | kQFrameUnclipped | kQFrameYFlip | 256, gViewX0, gViewY0, gViewX1,
						gViewY1);
				engine.rotatesprite(320 << 16, 200 << 16, 65536, 1024, 2344, 0, 0,
						kQFrameScale | kQFrameCorner | kQFrameUnclipped | 512, gViewX0, gViewY0, gViewX1, gViewY1);
				if (cfg.gDetail >= 4) {
					engine.rotatesprite(15 << 16, 3 << 16, 65536, 0, 2346, 32, 0,
							kQFrameScale | kQFrameCorner | kQFrameUnclipped | kQFrameTranslucent | 256, gViewX0,
							gViewY0, gViewX1, gViewY1);
					engine.rotatesprite(212 << 16, 77 << 16, 65536, 0, 2347, 32, 0,
							kQFrameScale | kQFrameCorner | kQFrameUnclipped | kQFrameTranslucent | 512, gViewX0,
							gViewY0, gViewX1, gViewY1);
				}
			}

			if (powerupCheck(gView, kItemAsbestosArmor - kItemBase) > 0) {
				engine.rotatesprite(0, 200 << 16, 65536, 0, 2358, 0, 0,
						kQFrameScale | kQFrameCorner | kQFrameUnclipped | kQFrameYFlip | 256, gViewX0, gViewY0, gViewX1,
						gViewY1);
				engine.rotatesprite(320 << 16, 200 << 16, 65536, 1024, 2358, 0, 0,
						kQFrameScale | kQFrameCorner | kQFrameUnclipped | 512, gViewX0, gViewY0, gViewX1, gViewY1);
			}

			if ((powerupCheck(gView, kItemCrystalBall - kItemBase) > 0) && (numplayers > 1
					|| (game.isCurrentScreen(gGameScreen) && kFakeMultiplayer && nFakePlayers > 1))) {
				// render screen with lens effect to BALLBUFFER2
				DoLensEffect();

				engine.setaspect(0x10000, 0xD555);

				int crysX = 280;
				int crysY = 35;

				byte nLensPLU = kPLUNormal;
				if (nSector != -1 && IsUnderwaterSector(nSector))
					nLensPLU = kPLUCold;

				// draw the warped image
				engine.rotatesprite(crysX << 16, crysY << 16, 0xD000,
						(engine.getrender().getType() == RenderType.Software) ? kAngle90 : -kAngle180, BALLBUFFER2, 0,
						nLensPLU, kRotateYFlip | kRotateScale, gViewX0, gViewY0, gViewX1, gViewY1);

				// draw frescette and highlight
				engine.rotatesprite((crysX - 1) << 16, (crysY - 1) << 16, 0xD000, 0, 1683, 0, kPLUNormal,
						kRotateTranslucent | kRotateTranslucentR | kRotateScale, gViewX0, gViewY0, gViewX1, gViewY1);

				engine.setview(gViewX0, gViewY0, gViewX1, gViewY1);
				// engine.setaspect(0x10000, (int) divscale(ydim * 320, xdim * 200, 16));
			}
		}

		if (gViewMode == kView2DIcon || gViewMode == kView2D) {
			if (gViewMode == kView2DIcon)
				engine.getrender().clearview(0);
			updateviewmap();

			int x = gView.pSprite.x;
			int y = gView.pSprite.y;
			float ang = gView.ang;

			char[] mode;
			if (gMapScrollMode) {
				x = scrollOX + mulscale(scrollX - scrollOX, smoothratio, 16);
				y = scrollOY + mulscale(scrollY - scrollOY, smoothratio, 16);
				ang = (scrollOAng
						+ ((BClampAngle(scrollAng - scrollOAng + kAngle180) - kAngle180) * smoothratio) / 65536.0f);
				mode = scrollmode;
			} else {

				x = gPrevView[gViewIndex].x + mulscale(x - gPrevView[gViewIndex].x, smoothratio, 16);
				y = gPrevView[gViewIndex].y + mulscale(y - gPrevView[gViewIndex].y, smoothratio, 16);
				ang = (gPrevView[gViewIndex].ang
						+ ((BClampAngle(ang - gPrevView[gViewIndex].ang + kAngle180) - kAngle180) * smoothratio)
								/ 65536.0f);

				mode = followmode;
			}

			int oldSize = cfg.gViewSize;
			viewResizeView(0);
			if (gViewMode == kView2DIcon)
				engine.drawmapview(x, y, kMapZoom >> 1, (short) ang);
			engine.drawoverheadmap(x, y, kMapZoom >> 1, (short) ang);

			int tx = 315;
			int ty = 16;
			String mapname = boardfilename;
			if (mUserFlag == UserFlag.UserMap && gUserMapInfo.Title != null)
				mapname = gUserMapInfo.Title;

			String info = "E" + (pGameInfo.nEpisode + 1) + "M" + (pGameInfo.nLevel + 1) + ": " + mapname;
			game.getFont(3).drawText(tx, ty, toCharArray(info), 0, 0, TextAlign.Right, 2 | 512, false);
			game.getFont(3).drawText(tx, ty + 10, mode, 0, 0, TextAlign.Right, 2 | 512, false);
			viewResizeView(oldSize);
		}

		if (!game.menu.gShowMenu || (game.menu.getCurrentMenu() instanceof MenuInterfaceSet)) {
			if (!SplitScreen)
				viewDrawHUD(gPlayer[gViewIndex]);
			else
				viewDrawSplitHUD(gPlayer[gViewIndex]);
		}

		if (gTextInput) {
			int nShade = 8;
			int x = mulscale(gViewX0 + 10, gViewX1Scaled, 16);
			int y = mulscale(gViewY0 + 10, gViewY1Scaled, 16);
			y += nextY + numQuotes * yOffset;
			if (pGameInfo.nGameType != kNetModeOff && pGameInfo.nGameType != kNetModeTeams) {
				int row = (numplayers - 1) / 4;
				y += (row + 1) * 9;
			}
			if (pGameInfo.nGameType == kNetModeTeams)
				y += 22;

//			viewDrawInputText(cfg.MessageFont, getInput()
//					.getMessageBuffer(), getInput().getMessageLength() + 1,
//					x + 1, y, 65536, nShade, 0, 0, 256, false);

			viewDrawInputText(0, getInput().getMessageBuffer(), getInput().getMessageLength() + 1, x + 1, y, 65536,
					nShade, 0, 0, 256, false);
		}

		viewDisplayMessage(0); // gViewIndex

		if (gPlayerIndex != -1 && gPlayerIndex != gViewIndex) {
			int plu = gPlayer[gPlayerIndex].pSprite.pal;
			if (plu == 13)
				plu = 4;
			game.getFont(4).drawText(160, 120, toCharArray(game.net.gProfile[gPlayerIndex].name), 0, plu,
					TextAlign.Center, 2, false);
			if (pGameInfo.nReviveMode && pGameInfo.nGameType == kNetModeCoop && gPlayer[gPlayerIndex].deathTime > 0
					&& gPlayer[gPlayerIndex].pXsprite.health <= 0) {
				PLAYER pPlayer = gPlayer[gPlayerIndex];
				int hitDist = (int) (engine.qdist(gView.pSprite.x - pPlayer.pSprite.x,
						gView.pSprite.y - pPlayer.pSprite.y) >> 4);
				if (hitDist < kPushXYDist) {
					int shade = 32 - (totalclock & 0x3F);
					game.getFont(4).drawText(160, 130, toCharArray("Press \"USE\" to revive player"), 0, shade,
							TextAlign.Center, 2, false);
				}
			}
		}

		if (pGameInfo.nReviveMode && pGameInfo.nGameType == kNetModeCoop && gPlayer[gViewIndex].deathTime > 0
				&& gPlayer[gViewIndex].pXsprite.health <= 0) {
			int shade = 32 - (totalclock & 0x3F);
			game.getFont(4).drawText(160, 130, toCharArray("Wait for revive or press \"USE\" to respawn"), 0, shade,
					TextAlign.Center, 2, false);
		}

		if (game.gPaused)
			game.getFont(1).drawText(160, 10, paused, 0, 0, TextAlign.Center, 2, false);

		if (game.isCurrentScreen(gGameScreen) && gFrameClock < gNameShowTime) {
			int transp = 0;
			if (gFrameClock > gNameShowTime - 20)
				transp = 33;
			if (gFrameClock > gNameShowTime - 10)
				transp = 1;

			if (/* mUserFlag != UserFlag.UserMap && */cfg.showMapInfo != 0 && !game.menu.gShowMenu) {
				switch (cfg.showMapInfo) {
				case 1:
					if (boardfilename != null)
						game.getFont(1).drawText(160, 60, toCharArray(boardfilename), -128, 0, TextAlign.Center,
								2 | transp, true);
					break;
				case 2:
					if (boardfilename != null)
						game.getFont(3).drawText(160, 20, toCharArray(boardfilename), 0, 0, TextAlign.Center,
								2 | transp, false);
					if (currentEpisode != null && currentEpisode.gMapInfo[pGameInfo.nLevel] != null
							&& currentEpisode.gMapInfo[pGameInfo.nLevel].Author != null)
						game.getFont(3).drawText(160, 30,
								toCharArray("by " + currentEpisode.gMapInfo[pGameInfo.nLevel].Author), 0, 0,
								TextAlign.Center, 2 | transp, false);
					break;
				}
			}
		}

//		if (fInterpolateRangeError)
//			viewDrawText(0, InterpolateRangeError, 160, 20, 65536, 0, 0, 1, 0, false);

		if (game.net.bOutOfSync) {
			game.getFont(3).drawText(160, 20, toCharArray("Out of sync!"), 0, 0, TextAlign.Center, 2, false);

			switch (game.net.bOutOfSyncByte / 4) {
			case 0: // bseed
				game.getFont(3).drawText(160, 30, toCharArray("bseed checksum error"), 0, 0, TextAlign.Center, 2,
						false);
				break;
			case 1: // player
				game.getFont(3).drawText(160, 30, toCharArray("player struct checksum error"), 0, 0, TextAlign.Center,
						2, false);
				break;
			case 2: // sprite
				game.getFont(3).drawText(160, 30, toCharArray("player sprite checksum error"), 0, 0, TextAlign.Center,
						2, false);
				break;
			case 3: // xsprite
				game.getFont(3).drawText(160, 30, toCharArray("player xsprite checksum error"), 0, 0, TextAlign.Center,
						2, false);
				break;
			}
		}

		if (gView.pSprite.statnum == kStatDude && gView.handDamage) {
			int x = 160;
			int y = ((gView.weaponAboveZ - gView.viewOffZ - 3072) >> 7) + 220;

			if (viewHandAnim.pQAV != null) {
				int oldFrameClock = gFrameClock;
				gFrameClock = totalclock;

				QAV pQAV = viewHandAnim.pQAV;
				pQAV.origin.x = x;
				pQAV.origin.y = y;

				int ticks = totalclock - viewHandAnim.clock;
				viewHandAnim.clock = totalclock;
				viewHandAnim.duration -= ticks;
				if (viewHandAnim.duration <= 0 || viewHandAnim.duration > pQAV.duration)
					viewHandAnim.duration = pQAV.duration;

				int t = pQAV.duration - viewHandAnim.duration;
				pQAV.Play(t - ticks, t, -1, null);

				int oldwx1 = windowx1;
				int oldwy1 = windowy1;
				int oldwx2 = windowx2;
				int oldwy2 = windowy2;
				windowy1 = 0;
				windowx2 = xdim - 1;
				windowx1 = 0;
				windowy2 = ydim - 1;
				pQAV.Draw(t, 0, (kQFrameUnclipped | kQFrameScale), 0, 65536);
				windowx1 = oldwx1;
				windowy1 = oldwy1;
				windowx2 = oldwx2;
				windowy2 = oldwy2;
				gFrameClock = oldFrameClock;
			}
		}
	}

	@Override
	public void PostFrame(BuildNet net) {
		if (gQuickSaving) {
			if (captBuffer != null) {
				savegame("[quicksave_" + quickslot + "]", "quicksav" + quickslot + ".sav");
				quickslot ^= 1;
				gQuickSaving = false;
			} else
				gGameScreen.capture(320, 200);
		}

		if (gAutosaveRequest) {
			if (captBuffer != null) {
				savegame("[autosave]", "autosave.sav");
				gAutosaveRequest = false;
			} else
				gGameScreen.capture(320, 200);
		}

		fire.process();

		viewPaletteHandler(gPlayer[gViewIndex]);
	}

	@Override
	public void KeyHandler() {
		BloodMenuHandler menu = game.menu;
		if (menu.gShowMenu) {
			engine.handleevents();
			menu.mKeyHandler(game.pInput, BuildGdx.graphics.getDeltaTime());
			return;
		}

		if (Console.IsShown() || gTextInput)
			return;

		BuildControls input = game.pInput;
		if (input.ctrlGetInputKey(GameKeys.Menu_Toggle, true))
			menu.mOpen(menu.mMenus[GAME], -1);

		if (input.ctrlGetInputKey(BloodKeys.Show_SaveMenu, true)) {
			if (numplayers > 1)
				return;
			if (gMe.pXsprite.health != 0) {
				gGameScreen.capture(320, 200);
				menu.mOpen(menu.mMenus[SAVEGAME], -1);
			}
		}

		if (input.ctrlGetInputKey(BloodKeys.Show_LoadMenu, true)) {
			if (numplayers > 1)
				return;
			menu.mOpen(menu.mMenus[LOADGAME], -1);
		}

		if (input.ctrlGetInputKey(BloodKeys.Show_SoundSetup, true))
			menu.mOpen(menu.mMenus[SOUNDSET], -1);

		if (input.ctrlGetInputKey(BloodKeys.Show_Options, true))
			menu.mOpen(menu.mMenus[OPTIONS], -1);

		if (input.ctrlGetInputKey(BloodKeys.Quicksave, true)) { // quick save
			quicksave();
		}

		if (input.ctrlGetInputKey(BloodKeys.Toggle_messages, true)) {
			cfg.MessageState = !cfg.MessageState;
			if (cfg.MessageState)
				viewSetMessage("Messages on", gViewIndex, 2);
		}

		if (input.ctrlGetInputKey(BloodKeys.Quickload, true)) { // quick load
			quickload();
		}

		if (input.ctrlGetInputKey(BloodKeys.Quit, true))
			menu.mOpen(menu.mMenus[QUIT], -1);

		if (input.ctrlGetInputKey(BloodKeys.Gamma, true)) {
			openGamma(menu);
		}

		if (input.ctrlGetInputKey(BloodKeys.Make_Screenshot, true)) {
			makeScreenshot();
		}

		if (gViewMode == kView3D) {
			if (input.ctrlGetInputKey(GameKeys.Shrink_Screen, true))
				viewResizeView(cfg.gViewSize + 1);
			if (input.ctrlGetInputKey(GameKeys.Enlarge_Screen, true))
				viewResizeView(cfg.gViewSize - 1);
		} else {
			int j = totalclock - nonsharedtimer;
			nonsharedtimer += j;

			if (input.ctrlGetInputKey(GameKeys.Shrink_Screen, false))
				kMapZoom = ClipLow(kMapZoom - mulscale(j, Math.max(kMapZoom, 256), 6), 16);
			if (input.ctrlGetInputKey(GameKeys.Enlarge_Screen, false))
				kMapZoom = ClipHigh(kMapZoom + mulscale(j, Math.max(kMapZoom, 256), 6), 4096);
		}
	}

	protected void makeScreenshot() {
		String name = "scrxxxx.png";
		if (mUserFlag == UserFlag.UserMap && gUserMapInfo != null)
			name = "scr-" + game.getFilename(gUserMapInfo.MapName) + "-xxxx.png";
		if (mUserFlag != UserFlag.UserMap && currentEpisode != null)
			name = "scr-e" + (pGameInfo.nEpisode + 1) + "m" + (pGameInfo.nLevel + 1) + "[" + currentEpisode.filename
					+ "]-xxxx.png";

		String filename = pEngine.screencapture(name);
		if (filename != null)
			viewSetMessage(filename + " saved", gViewIndex, 10);
		else
			viewSetMessage("Screenshot not saved. Access denied!", gViewIndex, 7);
	}

	protected void openGamma(BloodMenuHandler menu) {
//		if (cfg.gGamma++ >= gGammaLevels - 1)
//		cfg.gGamma = 0;
//	viewSetMessage("Gamma correction level " + cfg.gGamma, gViewIndex);
//	scrSetGamma(cfg.gGamma);

		menu.mOpen(menu.mMenus[COLORCORR], -1);
	}

	@Override
	public void sndHandlePause(boolean pause) {
		SOUND.sndHandlePause(pause);
	}

	// Change map methods

	@Override
	protected boolean prepareboard(String map) {
		gNameShowTime = 500;
		return DB.prepareboard(this);
	}

	/**
	 * @param item should be BloodIniFile or FileEntry (map)
	 */
	public void newgame(final boolean isMultiplayer, final Object item, final int episodeNum, final int nLevel,
			final int nGlobalDifficulty, final int nEnemyQuantity, final int nEnemyDamage,
			final boolean nPitchforkOnly) {
		pNet.ready2send = false;
		game.changeScreen(load); // checkEpisodeResources is slow, so we make other loading screens
		load.init(new Runnable() {
			@Override
			public void run() {
				if (!isMultiplayer) {
					kFakeMultiplayer = false;
					if (numplayers > 1)
						pNet.NetDisconnect(myconnectindex);
					pGameInfo.copy(defGameInfo);
					game.nNetMode = NetMode.Single;
				} else {
					pGameInfo.nGameType = pNetInfo.nGameType;
					pGameInfo.nEpisode = pNetInfo.nEpisode;
					pGameInfo.nLevel = pNetInfo.nLevel;
					pGameInfo.nDifficulty = pNetInfo.nDifficulty;
					pGameInfo.nMonsterSettings = pNetInfo.nMonsterSettings;
					pGameInfo.nWeaponSettings = pNetInfo.nWeaponSettings;
					pGameInfo.nItemSettings = pNetInfo.nItemSettings;

					pGameInfo.nFriendlyFire = pNetInfo.nFriendlyFire;
					pGameInfo.nReviveMode = pNetInfo.nReviveMode;

					pGameInfo.nEnemyQuantity = pNetInfo.nDifficulty;
					pGameInfo.nEnemyDamage = pNetInfo.nDifficulty;

					pGameInfo.nPitchforkOnly = false;
					pGameInfo.nFragLimit = pNetInfo.nFragLimit;
					game.nNetMode = NetMode.Multiplayer;
				}

				UserFlag flag = UserFlag.None;
				if (item instanceof BloodIniFile && !item.equals(MainINI)) {
					flag = UserFlag.Addon;
					BloodIniFile ini = (BloodIniFile) item;
					getEpisodeInfo(gUserEpisodeInfo, ini);
					checkEpisodeResources(ini);
					Console.Println("Start user episode: " + episodeNum);
				} else
					resetEpisodeResources();

				if (item instanceof FileEntry)
					flag = UserFlag.UserMap;
				mUserFlag = flag;

				if (flag == UserFlag.UserMap) {
					loadUserMapInfo(((FileEntry) item).getPath());
					Console.Println("Start user map: " + gUserMapInfo.Title);
				} else
					loadMapInfo(episodeNum, nLevel);

				pGameInfo.nDifficulty = nGlobalDifficulty;
				pGameInfo.nEnemyQuantity = nEnemyQuantity;
				pGameInfo.nEnemyDamage = nEnemyDamage;
				pGameInfo.nPitchforkOnly = nPitchforkOnly;
				pGameInfo.uGameFlags = 0; // new game flag

				cheatsOn = false;

				playerGodMode(gMe, 0);
				gInfiniteAmmo = false;
				gFullMap = false;
				cheatSubInventory(gMe);
				gNoClip = false;

				String title = getTitle();
				if (!checkCutsceneA(title))
					loadboard(pGameInfo.zLevelName, null).setTitle(title);
			}
		});
	}

	@Override
	protected void startboard(Runnable startboard) {
		gPrecacheScreen.init(false, startboard);
		game.changeScreen(gPrecacheScreen);
	}

	public void nextmap() {
		// setting level options from Finish
		loadMapInfo(pGameInfo.nEpisode, gNextMap);

		String title = getTitle();
		if (!checkCutsceneA(title))
			loadboard(pGameInfo.zLevelName, null).setTitle(title);
		if (pGameInfo.nGameType == 0)
			gAutosaveRequest = true;
	}

	@Override
	public GameAdapter setTitle(String title) {
		boardfilename = title;
		return super.setTitle(title);
	}

	private boolean checkCutsceneA(final String loadTitle) {
		if (pGameInfo.nGameType != kNetModeOff || kFakeMultiplayer || mUserFlag == UserFlag.UserMap
				|| !currentEpisode.hasCutsceneA(pGameInfo.nLevel) || this == gDemoScreen)
			return false;

		if (gCutsceneScreen.init(currentEpisode.CutSceneA, currentEpisode.CutWavA)) {
			pGameInfo.uGameFlags |= CutsceneA;
			gCutsceneScreen.setCallback(new Runnable() {
				@Override
				public void run() {
					pGameInfo.uGameFlags &= ~CutsceneA;
					loadboard(pGameInfo.zLevelName, null).setTitle(loadTitle);
				}
			}).escSkipping(true);
			game.changeScreen(gCutsceneScreen);
			return true;
		}

		return false;
	}

	private boolean checkCutsceneB() {
		if (pGameInfo.nGameType != kNetModeOff || kFakeMultiplayer || mUserFlag == UserFlag.UserMap
				|| !currentEpisode.hasCutsceneB(pGameInfo.nLevel) || this == gDemoScreen)
			return false;

		return (pGameInfo.uGameFlags & CutsceneB) != 0
				&& gCutsceneScreen.init(currentEpisode.CutSceneB, currentEpisode.CutWavB);
	}

	public String getTitle() {
		String title = null;
		if (mUserFlag != UserFlag.UserMap) {
			if (mUserFlag == UserFlag.None)
				title = getEpisodeTitle();
			else if (mUserFlag == UserFlag.Addon) {
				if ((title = getUserEpisodeTitle()) == null) {
					game.GameMessage("No map in user episode or wrong map filename!");
					return null;
				}
			}
		} else
			title = getMapTitle();

		return title;
	}

	public String getUserEpisodeTitle() {
		if (gUserEpisodeInfo[pGameInfo.nEpisode] != null
				&& gUserEpisodeInfo[pGameInfo.nEpisode].gMapInfo[pGameInfo.nLevel] != null)
			return gUserEpisodeInfo[pGameInfo.nEpisode].gMapInfo[pGameInfo.nLevel].Title;

		return null;
	}

	public String getEpisodeTitle() {
		if (gEpisodeInfo[pGameInfo.nEpisode].gMapInfo[pGameInfo.nLevel] != null)
			return gEpisodeInfo[pGameInfo.nEpisode].gMapInfo[pGameInfo.nLevel].Title;

		return null;
	}

	public String getMapTitle() {
		if (gUserMapInfo != null)
			return gUserMapInfo.Title;

		return null;
	}

}
