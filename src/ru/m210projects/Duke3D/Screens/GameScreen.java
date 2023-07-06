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

import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.automapping;
import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Engine.gotpic;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Engine.parallaxyscale;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.ksgn;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Build.Strhandler.toCharArray;
import static ru.m210projects.Duke3D.Actors.moveactors;
import static ru.m210projects.Duke3D.Actors.movecyclers;
import static ru.m210projects.Duke3D.Actors.movedummyplayers;
import static ru.m210projects.Duke3D.Actors.moveeffectors;
import static ru.m210projects.Duke3D.Actors.moveexplosions;
import static ru.m210projects.Duke3D.Actors.movefallers;
import static ru.m210projects.Duke3D.Actors.movefta;
import static ru.m210projects.Duke3D.Actors.movefx;
import static ru.m210projects.Duke3D.Actors.moveplayers;
import static ru.m210projects.Duke3D.Actors.movestandables;
import static ru.m210projects.Duke3D.Actors.movetransports;
import static ru.m210projects.Duke3D.Animate.doanimations;
import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.COLORCORR;
import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.GAME;
import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.HELP;
import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.LOADGAME;
import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.OPTIONS;
import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.QUIT;
import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.SAVEGAME;
import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.SOUNDSET;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.LoadSave.gAutosaveRequest;
import static ru.m210projects.Duke3D.LoadSave.gQuickSaving;
import static ru.m210projects.Duke3D.LoadSave.lastload;
import static ru.m210projects.Duke3D.LoadSave.quickload;
import static ru.m210projects.Duke3D.LoadSave.quicksave;
import static ru.m210projects.Duke3D.LoadSave.quickslot;
import static ru.m210projects.Duke3D.LoadSave.savegame;
import static ru.m210projects.Duke3D.Main.cfg;
import static ru.m210projects.Duke3D.Main.engine;
import static ru.m210projects.Duke3D.Main.gAnmScreen;
import static ru.m210projects.Duke3D.Main.gDemoScreen;
import static ru.m210projects.Duke3D.Main.gEndScreen;
import static ru.m210projects.Duke3D.Main.gGameScreen;
import static ru.m210projects.Duke3D.Main.gLoadingScreen;
import static ru.m210projects.Duke3D.Main.gPrecacheScreen;
import static ru.m210projects.Duke3D.Main.gStatisticScreen;
import static ru.m210projects.Duke3D.Main.mUserFlag;
import static ru.m210projects.Duke3D.Names.APLAYER;
import static ru.m210projects.Duke3D.Names.FLOORPLASMA;
import static ru.m210projects.Duke3D.Names.FLOORSLIME;
import static ru.m210projects.Duke3D.Names.HEAVYHBOMB;
import static ru.m210projects.Duke3D.Names.HURTRAIL;
import static ru.m210projects.Duke3D.Player.checkavailinven;
import static ru.m210projects.Duke3D.Player.processinput;
import static ru.m210projects.Duke3D.Player.quickkill;
import static ru.m210projects.Duke3D.Player.setpal;
import static ru.m210projects.Duke3D.Premap.checknextlevel;
import static ru.m210projects.Duke3D.Premap.clearfrags;
import static ru.m210projects.Duke3D.Premap.prelevel;
import static ru.m210projects.Duke3D.Premap.resetinventory;
import static ru.m210projects.Duke3D.Premap.resetpspritevars;
import static ru.m210projects.Duke3D.Premap.resetweapons;
import static ru.m210projects.Duke3D.Premap.rorcnt;
import static ru.m210projects.Duke3D.Premap.rorsector;
import static ru.m210projects.Duke3D.Premap.rortype;
import static ru.m210projects.Duke3D.ResourceHandler.checkEpisodeResources;
import static ru.m210projects.Duke3D.ResourceHandler.resetEpisodeResources;
import static ru.m210projects.Duke3D.Screen.changepalette;
import static ru.m210projects.Duke3D.Screen.palto;
import static ru.m210projects.Duke3D.Screen.vscrn;
import static ru.m210projects.Duke3D.Sector.allignwarpelevators;
import static ru.m210projects.Duke3D.Sector.animatewalls;
import static ru.m210projects.Duke3D.Sector.checksectors;
import static ru.m210projects.Duke3D.SoundDefs.BONUS_SPEECH1;
import static ru.m210projects.Duke3D.SoundDefs.DUKE_GETWEAPON2;
import static ru.m210projects.Duke3D.SoundDefs.DUKE_JETPACK_IDLE;
import static ru.m210projects.Duke3D.SoundDefs.DUKE_JETPACK_OFF;
import static ru.m210projects.Duke3D.SoundDefs.DUKE_JETPACK_ON;
import static ru.m210projects.Duke3D.SoundDefs.DUKE_TAKEPILLS;
import static ru.m210projects.Duke3D.SoundDefs.DUKE_USEMEDKIT;
import static ru.m210projects.Duke3D.SoundDefs.GENERIC_AMBIENCE17;
import static ru.m210projects.Duke3D.SoundDefs.JIBBED_ACTOR5;
import static ru.m210projects.Duke3D.SoundDefs.JIBBED_ACTOR6;
import static ru.m210projects.Duke3D.SoundDefs.NITEVISION_ONOFF;
import static ru.m210projects.Duke3D.SoundDefs.TELEPORTER;
import static ru.m210projects.Duke3D.SoundDefs.THUD;
import static ru.m210projects.Duke3D.Sounds.*;
import static ru.m210projects.Duke3D.Spawn.EGS;
import static ru.m210projects.Duke3D.Types.RTS.rtsplaying;
import static ru.m210projects.Duke3D.View.FTA;
import static ru.m210projects.Duke3D.View.MAXUSERQUOTES;
import static ru.m210projects.Duke3D.View.adduserquote;
import static ru.m210projects.Duke3D.View.cameraclock;
import static ru.m210projects.Duke3D.View.cameradist;
import static ru.m210projects.Duke3D.View.displayrest;
import static ru.m210projects.Duke3D.View.displayrooms;
import static ru.m210projects.Duke3D.View.fta;
import static ru.m210projects.Duke3D.View.ftq;
import static ru.m210projects.Duke3D.View.gNameShowTime;
import static ru.m210projects.Duke3D.View.lastvisinc;
import static ru.m210projects.Duke3D.View.loogiex;
import static ru.m210projects.Duke3D.View.loogiey;
import static ru.m210projects.Duke3D.View.over_shoulder_on;
import static ru.m210projects.Duke3D.View.quotebot;
import static ru.m210projects.Duke3D.View.quotebotgoal;
import static ru.m210projects.Duke3D.View.user_quote_time;
import static ru.m210projects.Duke3D.View.zoom;
import static ru.m210projects.Duke3D.Weapons.addweapon;
import static ru.m210projects.Duke3D.Weapons.moveweapons;

import java.io.FileNotFoundException;
import java.util.Arrays;

import ru.m210projects.Build.CRC32;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Build.Types.BuildPos;
import ru.m210projects.Build.Types.InvalidVersionException;
import ru.m210projects.Duke3D.Config.DukeKeys;
import ru.m210projects.Duke3D.Input;
import ru.m210projects.Duke3D.Main;
import ru.m210projects.Duke3D.Main.UserFlag;
import ru.m210projects.Duke3D.Sounds;
import ru.m210projects.Duke3D.Factory.DukeMenuHandler;
import ru.m210projects.Duke3D.Types.DemoFile;
import ru.m210projects.Duke3D.Types.GameInfo;
import ru.m210projects.Duke3D.Types.PlayerStruct;

public class GameScreen extends GameAdapter {

	private int nonsharedtimer;

	private final Main game;

	public GameScreen(Main game) {
		super(game, gLoadingScreen);
		this.game = game;
		for (int i = 0; i < MAXPLAYERS; i++)
			sync[i] = new Input();
	}

	@Override
	public void hide() {
		StopCommentary(pCommentary);
	}

	@Override
	public void PostFrame(BuildNet net) {
		if (gQuickSaving) {
			if (captBuffer != null) {
				savegame("[quicksave_" + quickslot + "]", "quicksav" + quickslot + ".sav");
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

	@Override
	public void ProcessFrame(BuildNet net) {
		ud.camerasprite = -1;
		everyothertime++;

		for (int i = connecthead; i >= 0; i = connectpoint2[i])
			sync[i].Copy(net.gFifoInput[net.gNetFifoTail & 0xFF][i]);
		net.gNetFifoTail++;

		int j = -1;
		for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
			cheatkeys(i);
			if (GameScreen.this == gDemoScreen || (sync[i].bits & (1 << 26)) == 0) {
				j = i;
				continue;
			}

			if (ud.rec != null)
				ud.rec.close();

			if (i == myconnectindex)
				game.gExit = true;
			if (screenpeek == i) {
				screenpeek = connectpoint2[i];
				if (screenpeek < 0)
					screenpeek = connecthead;
			}

			if (i == connecthead)
				connecthead = connectpoint2[connecthead];
			else
				connectpoint2[j] = connectpoint2[i];

			numplayers--;
			ud.multimode--;

			if (numplayers < 2)
				sound(GENERIC_AMBIENCE17);

			quickkill(ps[i]);
			engine.deletesprite(ps[i].i);

			buildString(buf, 0, ud.user_name[i], " is history!");
			adduserquote(buf);

			vscrn(ud.screen_size);

			if (j < 0) {
				game.show();
				Console.Println(
						" \nThe 'MASTER/First player' just quit the game.  All\nplayers are returned from the game.");
				return;
			}
		}

		net.CalcChecksum();

		lockclock += TICSPERFRAME;
		if (game.gPaused || ud.recstat != 2 && ud.multimode < 2 && GameScreen.this != gDemoScreen
				&& (game.menu.gShowMenu || Console.IsShown())) {
//	    	PauseCommentary(pCommentary);
			return;
		}
//	    ResumeCommentary(pCommentary);

		if (ud.recstat == 1 && ud.rec != null)
			ud.rec.record();

		if (earthquaketime > 0)
			earthquaketime--;
		if (rtsplaying > 0)
			rtsplaying--;

		for (int i = 0; i < MAXUSERQUOTES; i++)
			if (user_quote_time[i] != 0)
				user_quote_time[i]--;

		if ((klabs(quotebotgoal - quotebot) <= 16) && (ud.screen_size <= 2))
			quotebot += ksgn(quotebotgoal - quotebot);
		else
			quotebot = quotebotgoal;

		if (fta > 0) {
			fta--;
			if (fta == 0)
				ftq = 0;
		}

		if (totalclock < lastvisinc) {
			if (klabs(gVisibility - currentGame.getCON().const_visibility) > 8)
				gVisibility += (currentGame.getCON().const_visibility - gVisibility) >> 2;
		} else
			gVisibility = currentGame.getCON().const_visibility;

		global_random = (short) engine.krand();
		movedummyplayers();// ST 13

		for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
			ps[i].UpdatePlayerLoc();
			processinput(i);
			checksectors(i);
		}

		movefta();// ST 2
		moveweapons(); // ST 5 (must be last)
		movetransports(); // ST 9
		moveplayers(); // ST 10
		movefallers(); // ST 12
		moveexplosions(); // ST 4
		moveactors(); // ST 1
		moveeffectors(); // ST 3
		movestandables(); // ST 6
		doanimations();
		movefx(); // ST 11

		net.CorrectPrediction();

		MusicUpdate();

		if ((everyothertime & 1) == 0) {
			animatewalls();
			movecyclers();
			pan3dsound();
		}

		if ((uGameFlags & MODE_EOL) == MODE_EOL) {
			game.pNet.ready2send = false;
			if (GameScreen.this == gDemoScreen)
				return;

			if (ud.eog == 1) {
				ud.eog = 0;
				uGameFlags |= MODE_END;
//	    		if(ud.multimode < 2)
//                {
				switch (ud.volume_number) {
				case 0:
					gEndScreen.episode1();
					break;
				case 1:
					gEndScreen.episode2();
					break;
				case 2:
					gEndScreen.episode3();
					break;
				case 3:
					gEndScreen.episode4();
					break;
				case 4:
					gEndScreen.episode5();
					break;
				default:
					BuildGdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							game.show();
						}
					});
					break;
				}
//	            }
//	            else
			} else {
				BuildGdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						game.changeScreen(gStatisticScreen);
					}
				});
			}
		}
	}

	@Override
	public void DrawWorld(float smooth) {
		displayrooms(screenpeek, (int) smooth);
	}

	@Override
	public void DrawHud(float smooth) {
		displayrest((int) smooth);

		if (game.net.bOutOfSync) {
			game.getFont(1).drawText(160, 20, toCharArray("Out of sync!"), 0, 12, TextAlign.Center, 2, false);

			switch (game.net.bOutOfSyncByte / 4) {
			case 0: // bseed
				game.getFont(1).drawText(160, 30, toCharArray("seed checksum error"), 0, 12, TextAlign.Center, 2,
						false);
				break;
			case 1: // player
				game.getFont(1).drawText(160, 30, toCharArray("player struct checksum error"), 0, 12, TextAlign.Center,
						2, false);
				break;
			case 2: // sprite
				game.getFont(1).drawText(160, 30, toCharArray("player sprite checksum error"), 0, 12, TextAlign.Center,
						2, false);
				break;
			}
		}
	}

	@Override
	protected void startboard(Runnable startboard) {
		gPrecacheScreen.init(false, startboard);
		game.changeScreen(gPrecacheScreen);
	}

	@Override
	public void KeyHandler() {
		DukeMenuHandler menu = game.menu;
		if (menu.gShowMenu) {
			engine.handleevents();
			menu.mKeyHandler(game.pInput, BuildGdx.graphics.getDeltaTime());
			return;
		}

		if (Console.IsShown() || MODE_TYPE)
			return;

		BuildControls input = game.pInput;
		if (input.ctrlGetInputKey(GameKeys.Menu_Toggle, true))
			menu.mOpen(menu.mMenus[GAME], -1);

		if (input.ctrlGetInputKey(DukeKeys.Show_Help, true))
			menu.mOpen(menu.mMenus[HELP], -1);

		if (input.ctrlGetInputKey(DukeKeys.Show_Savemenu, true)) {
			if (numplayers > 1 || mFakeMultiplayer)
				return;
			if (sprite[ps[myconnectindex].i].extra > 0) {
				gGameScreen.capture(160, 100);
				menu.mOpen(menu.mMenus[SAVEGAME], -1);
			}
		}

		if (input.ctrlGetInputKey(DukeKeys.Show_Loadmenu, true)) {
			if (numplayers > 1 || mFakeMultiplayer)
				return;
			menu.mOpen(menu.mMenus[LOADGAME], -1);
		}

		if (input.ctrlGetInputKey(DukeKeys.See_Chase_View, true)) {
			if (over_shoulder_on != 0)
				over_shoulder_on = 0;
			else {
				over_shoulder_on = 1;
				cameradist = 0;
				cameraclock = totalclock;
			}
			FTA(109 + over_shoulder_on, ps[myconnectindex]);
		}

		if (ud.overhead_on != 0) {
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

			if (input.ctrlGetInputKey(DukeKeys.Map_Follow_Mode, true)) {
				ud.scrollmode = !ud.scrollmode;

				if (ud.scrollmode) {
					ud.folx = ps[myconnectindex].oposx;
					ud.foly = ps[myconnectindex].oposy;
					ud.fola = (int) ps[myconnectindex].oang;
				}
				FTA(83 + (ud.scrollmode ? 1 : 0), ps[myconnectindex]);
			}
		} else {
			if (input.ctrlGetInputKey(GameKeys.Shrink_Hud, true)) {
				if (ud.screen_size > 0) {
					sound(THUD);
					shrinkScreen();
				}
			}
			if (input.ctrlGetInputKey(GameKeys.Enlarge_Hud, true)) {
				if (ud.screen_size < 3) {
					sound(THUD);
					enlargeScreen();
				}
			}
		}

		if (input.ctrlGetInputKey(GameKeys.Map_Toggle, true)) {
			if (ud.last_overhead != ud.overhead_on && ud.last_overhead != 0) {
				ud.overhead_on = ud.last_overhead;
				ud.last_overhead = 0;
			} else {
				ud.overhead_on++;
				if (ud.overhead_on == 3)
					ud.overhead_on = 0;
				ud.last_overhead = ud.overhead_on;
			}
		}

		if (input.ctrlGetInputKey(DukeKeys.AutoRun, true)) {
			ud.auto_run ^= 1;
			FTA(85 + ud.auto_run, ps[myconnectindex]);
		}

		if (input.ctrlGetInputKey(DukeKeys.Toggle_Crosshair, true)) {
			ud.crosshair ^= 1;
			FTA(21 - ud.crosshair, ps[myconnectindex]);
		}

		if (input.ctrlGetInputKey(DukeKeys.Show_Opp_Weapon, true)) {
			ud.showweapons ^= 1;
			FTA(82 - ud.showweapons, ps[myconnectindex]);
		}

		if (input.ctrlGetInputKey(DukeKeys.Show_Sounds, true))
			menu.mOpen(menu.mMenus[SOUNDSET], -1);

		if (input.ctrlGetInputKey(DukeKeys.Show_Options, true))
			menu.mOpen(menu.mMenus[OPTIONS], -1);

		if (input.ctrlGetInputKey(DukeKeys.Gamma, true))
			openGamma(menu);

		if (input.ctrlGetInputKey(DukeKeys.Quicksave, true)) { // quick save
			quicksave();
		}

		if (input.ctrlGetInputKey(DukeKeys.Messages, true)) {
			ud.fta_on ^= 1;
			if (ud.fta_on != 0)
				FTA(23, ps[myconnectindex]);
			else {
				ud.fta_on = 1;
				FTA(24, ps[myconnectindex]);
				ud.fta_on = 0;
			}
		}

		if (input.ctrlGetInputKey(GameKeys.Send_Message, false)) {
			spritesound(92, ps[myconnectindex].i);
			MODE_TYPE = true;
			getInput().initMessageInput(null);
		}

		if (input.ctrlGetInputKey(DukeKeys.Quickload, true)) { // quick load
			quickload();
		}

		if (input.ctrlGetInputKey(DukeKeys.See_Coop_View, true)) {
			if (ud.coop == 1 || mFakeMultiplayer) {
				screenpeek = connectpoint2[screenpeek];
				if (screenpeek < 0)
					screenpeek = connecthead;

				changepalette = 1; // if player has other palette
			}
		}

		if (input.ctrlGetInputKey(DukeKeys.Quit, true))
			menu.mOpen(menu.mMenus[QUIT], -1);

		if (input.ctrlGetInputKey(DukeKeys.Screenshot, true))
			makeScreenshot();
	}

	public boolean enlargeScreen() {
		ud.screen_size++;
		if (!engine.getTile(ALTHUDRIGHT).isLoaded() && !engine.getTile(ALTHUDLEFT).isLoaded() && ud.screen_size == 2)
			ud.screen_size++;
		if (ud.screen_size > 3)
			ud.screen_size = 3;
		vscrn(ud.screen_size);

		return true;
	}

	public boolean shrinkScreen() {
		ud.screen_size--;
		if (!engine.getTile(ALTHUDRIGHT).isLoaded() && !engine.getTile(ALTHUDLEFT).isLoaded() && ud.screen_size == 2)
			ud.screen_size--;
		if (ud.screen_size < 0)
			ud.screen_size = 0;
		vscrn(ud.screen_size);

		return true;
	}

	protected void openGamma(DukeMenuHandler menu) {
		menu.mOpen(menu.mMenus[COLORCORR], -1);
	}

	@Override
	public void sndHandlePause(boolean pause) {
		Sounds.sndHandlePause(pause);
	}

	@Override
	protected boolean prepareboard(String map) {
		gNameShowTime = 500;

		checknextlevel();

		if (GameScreen.this != gDemoScreen && ud.recstat == 2)
			ud.recstat = 0;

		BuildPos out = null;
		try {
			out = engine.loadboard(map);
		} catch (FileNotFoundException | InvalidVersionException | RuntimeException e) {
			game.GameMessage(e.getMessage());
			return false;
		}

		ps[0].posx = out.x;
		ps[0].posy = out.y;
		ps[0].posz = out.z;
		ps[0].ang = out.ang;
		ps[0].cursectnum = out.sectnum;

		Arrays.fill(gotpic, (byte) 0);
		Arrays.fill(rorsector, (short) -1);
		Arrays.fill(rortype, (byte) -1);
		rorcnt = 0;

		prelevel();
		allignwarpelevators();
		resetpspritevars();

		automapping = 0;
		ftq = 0;
		fta = 0;
		Arrays.fill(loogiex, 0);
		Arrays.fill(loogiey, 0);

		if (ud.recstat != 2) {
			sndStopMusic();
		}

		if (ud.recstat != 2) {
			musicvolume = ud.volume_number;
			musiclevel = ud.level_number;
			sndPlayMusic(currentGame.getCON().music_fn[musicvolume][musiclevel]);
		}

		if (ud.recstat == 1)
			ud.rec = new DemoFile(GDXBYTEVERSION); // JFBYTEVERSION

		for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
			if (sprite[ps[i].i].sectnum == 1024)
				continue;
			switch (sector[sprite[ps[i].i].sectnum].floorpicnum) {
			case HURTRAIL:
			case FLOORSLIME:
			case FLOORPLASMA:
				resetweapons(i);
				resetinventory(i);
				ps[i].gotweapon[PISTOL_WEAPON] = false;
				ps[i].ammo_amount[PISTOL_WEAPON] = 0;
				ps[i].curr_weapon = KNEE_WEAPON;
				ps[i].kickback_pic = 0;
				break;
			}
		}

		// PREMAP.C - replace near the my's at the end of the file

		ps[myconnectindex].palette = palette;

		setpal(ps[myconnectindex]);

		everyothertime = 0;
		global_random = 0;

		ud.last_level = ud.level_number + 1;

		changepalette = 1;

		game.net.WaitForAllPlayers(0);
		engine.sampletimer();

		palto(0, 0, 0, 0);
		if (!game.menu.gShowMenu)
			vscrn(ud.screen_size);
		engine.clearview(0);

		over_shoulder_on = 0;

		Arrays.fill(user_quote_time, (short) 0);

		game.net.predict.reset();
		clearfrags();

		System.err.println("New level " + map);

		if ((uGameFlags & MODE_EOL) == MODE_EOL && game.nNetMode == NetMode.Single)
			gAutosaveRequest = true;

		uGameFlags &= ~(MODE_EOL | MODE_END);

		return !kGameCrash;
	}

	protected void makeScreenshot() {
		String name = "scrxxxx.png";
		FileEntry map;
		if (mUserFlag == UserFlag.UserMap && (map = BuildGdx.compat.checkFile(boardfilename)) != null)
			name = "scr-" + map.getName() + "-xxxx.png";
		if (mUserFlag != UserFlag.UserMap && currentGame != null)
			name = "scr-e" + (ud.volume_number + 1) + "m" + (ud.level_number + 1) + "["
					+ currentGame.getFile().getName() + "]-xxxx.png";

		String filename = pEngine.screencapture(name);
		if (filename != null)
			buildString(currentGame.getCON().fta_quotes[103], 0, filename, " saved");
		else
			buildString(currentGame.getCON().fta_quotes[103], 0, "Screenshot not saved. Access denied!");
		FTA(103, ps[myconnectindex]);
	}

	/**
	 * @param item should be GameInfo or FileEntry (map)
	 */
	public void newgame(final boolean isMultiplayer, final Object item, final int nEpisode, final int nLevel,
			final int nDifficulty) {
		if (ud.recstat == 1 && ud.rec != null)
			ud.rec.close();

		pNet.ready2send = false;
		game.changeScreen(load); // checkEpisodeResources is slow, so we make other loading screens
		load.init(new Runnable() {
			@Override
			public void run() {
				if (!isMultiplayer) {
					ud.multimode = 1;
					mFakeMultiplayer = false;
					if (numplayers > 1)
						pNet.NetDisconnect(myconnectindex);

					ud.monsters_off = false;
					ud.respawn_monsters = false;
					ud.respawn_items = false;
					ud.respawn_inventory = false;

					connecthead = 0;
					connectpoint2[0] = -1;

					game.nNetMode = NetMode.Single;
				} else {
					if (mFakeMultiplayer) {
						ud.multimode = nFakePlayers;
						connecthead = 0;
						for (short i = 0; i < MAXPLAYERS; i++)
							connectpoint2[i] = (short) (i + 1);
						connectpoint2[ud.multimode - 1] = -1;
					} else
						ud.multimode = numplayers;

					if (GameScreen.this != gDemoScreen) {
						if (pNetInfo.nGameType == 1)
							ud.coop = 1;
						else
							ud.coop = 0;

						ud.monsters_off = pNetInfo.nMonsters == 1;
						ud.respawn_monsters = false;
						ud.respawn_inventory = true;
						ud.respawn_items = pNetInfo.nGameType == 0;
						ud.marker = pNetInfo.nMarkers;
						ud.ffire = pNetInfo.nFriendlyFire;
					}

					ud.god = false;
					game.nNetMode = NetMode.Multiplayer;

					for (int c = connecthead; c >= 0; c = connectpoint2[c]) {
						resetweapons(c);
						resetinventory(c);
					}
				}

				UserFlag flag = UserFlag.None;
				if (item instanceof GameInfo && !item.equals(defGame)) {
					flag = UserFlag.Addon;
					GameInfo game = (GameInfo) item;
					checkEpisodeResources(game);
					Console.Println("Start user episode: " + game.Title);
				} else
					resetEpisodeResources();

				if (item instanceof FileEntry) {
					flag = UserFlag.UserMap;
					boardfilename = ((FileEntry) item).getPath();
					ud.level_number = 7;
					ud.volume_number = 0;
					Console.Println("Start user map: " + ((FileEntry) item).getName());
				}
				mUserFlag = flag;

				if (GameScreen.this != gDemoScreen) {
					if (!isMultiplayer) {
						Source skillvoice = null;
						switch (nDifficulty) {
						case 0:
							skillvoice = sound(JIBBED_ACTOR6);
							break;
						case 1:
							skillvoice = sound(BONUS_SPEECH1);
							break;
						case 2:
							skillvoice = sound(DUKE_GETWEAPON2);
							break;
						case 3:
							skillvoice = sound(JIBBED_ACTOR5);
							break;
						}

						while (skillvoice != null && skillvoice.isActive())
							;
					}

					if (mUserFlag != UserFlag.UserMap) {
						ud.level_number = nLevel;
						ud.volume_number = nEpisode;
					}
					ud.player_skill = nDifficulty + 1;
					ud.respawn_monsters = ud.player_skill == 4;
				}

				uGameFlags = 0;
				ud.secretlevel = 0;
				ud.from_bonus = 0;
				parallaxyscale = 0;
				ud.last_level = -1;
				lastload = null;

				if (!isMultiplayer) {
					PlayerStruct p = ps[myconnectindex];
					if (ud.coop != 1) {
						p.curr_weapon = PISTOL_WEAPON;
						p.gotweapon[PISTOL_WEAPON] = true;
						p.gotweapon[KNEE_WEAPON] = true;
						p.ammo_amount[PISTOL_WEAPON] = 48;
						p.gotweapon[HANDREMOTE_WEAPON] = true;
						p.last_weapon = -1;
					}
					p.last_used_weapon = 0;
				}

				display_mirror = 0;
				zoom = 768;

				if (flag == UserFlag.Addon && nEpisode != 3 && game.nNetMode == NetMode.Single
						&& GameScreen.this != gDemoScreen && nLevel == 0) {
					byte[] currentAnm = BuildGdx.cache.getBytes("logo.anm", 0);
					byte[] defAnm = BuildGdx.cache.getBytes("logo.anm", 1);

					if (currentAnm != null && defAnm != null
							&& CRC32.getChecksum(currentAnm) != CRC32.getChecksum(defAnm)) {
						if (game.nNetMode == NetMode.Single && gAnmScreen.init("logo.anm", 5)) {
							game.changeScreen(gAnmScreen.setCallback(new Runnable() {
								@Override
								public void run() {
									enterlevel(getTitle(), ud.volume_number, ud.level_number);
								}
							}).escSkipping(true));
							return;
						}
					}
				}

				String title = getTitle();
				if (!checkCutscene(title)) {
					enterlevel(title, ud.volume_number, ud.level_number);
				}
			}
		});
	}

	public boolean enterlevel(String title, int vol, int lev) {
		if (title == null)
			return false;
		String map;
		if (mUserFlag == UserFlag.UserMap)
			map = boardfilename;
		else
			map = currentGame.episodes[vol].gMapInfo[lev].path;

		if (GameScreen.this != gDemoScreen)
			ud.recstat = ud.m_recstat;

		loadboard(map, null).setTitle(title);
		return true;
	}

	private boolean checkCutscene(final String loadTitle) {
		if (game.nNetMode != NetMode.Single || mUserFlag == UserFlag.UserMap || ud.level_number != 0
				|| ud.volume_number != 3 || ud.lockout != 0 || this == gDemoScreen)
			return false;

		if (gAnmScreen.init("vol41a.anm", 6)) {
			sndPlayMusic(currentGame.getCON().env_music_fn[1]);
			gAnmScreen.setCallback(new Runnable() {
				@Override
				public void run() {
					enterlevel(loadTitle, ud.volume_number, ud.level_number);
				}
			}).escSkipping(true);
			game.changeScreen(gAnmScreen.escSkipping(true));
			return true;
		}
		return false;
	}

	public String getTitle() {
		String title = null;
		if (mUserFlag != UserFlag.UserMap) {
			if (ud.volume_number < nMaxEpisodes
					&& currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number] != null)
				title = currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number].title;
			else {
				game.GameCrash(
						"MapInfo not found! Episode: " + ud.volume_number + " Level: " + ud.level_number + " nMaps: "
								+ (ud.volume_number < nMaxEpisodes ? currentGame.episodes[ud.volume_number].nMaps : 0));
				return null;
			}
		} else {
			FileEntry file = BuildGdx.compat.checkFile(boardfilename);
			if (file != null)
				title = file.getName();
			else {
				game.GameCrash("Map " + boardfilename + " not found!");
				return null;
			}
		}
		return title;
	}

	public void cheatkeys(int snum) {
		int i, k;
		short dainv;
		int sb_snum, j;

		sb_snum = sync[snum].bits;
		PlayerStruct p = ps[snum];

		if (p.cheat_phase == 1)
			return;

		i = p.aim_mode;
		p.aim_mode = (sb_snum >> 23) & 1;
		if (p.aim_mode < i && (game.nNetMode != NetMode.Single || !pMenu.gShowMenu))
			p.return_to_center = 9;

		if (((sb_snum & (1 << 22)) != 0) && p.quick_kick == 0)
			if (p.curr_weapon != KNEE_WEAPON || p.kickback_pic == 0) {
				p.quick_kick = 14;
				FTA(80, p);
			}

		if ((sb_snum & ((15 << 8) | (1 << 12) | (1 << 15) | (1 << 16) | (1 << 22) | (1 << 19) | (1 << 20) | (1 << 21)
				| (1 << 24) | (1 << 25) | (1 << 27) | (1 << 28) | (1 << 29) | (1 << 30) | (1 << 31))) == 0)
			p.interface_toggle_flag = 0;
		else if (p.interface_toggle_flag == 0 && (sb_snum & (1 << 17)) == 0) {
			p.interface_toggle_flag = 1;

			if ((sb_snum & (1 << 21)) != 0) {
				game.gPaused = !game.gPaused;
//	            if( game.gPaused && (sb_snum&(1<<5)) != 0 ) ud.pause_on = 2;
				if (game.gPaused) {
					sndHandlePause(true);
				} else {
					if (!cfg.muteMusic && currMusic != null)
						currMusic.resume();
				}
			}

			if (game.gPaused)
				return;

			if (sprite[p.i].extra <= 0)
				return;

			if ((sb_snum & (1 << 30)) != 0 && p.newowner == -1) {
				switch (p.inven_icon) {
				case 4:
					sb_snum |= (1 << 25);
					break;
				case 3:
					sb_snum |= (1 << 24);
					break;
				case 5:
					sb_snum |= (1 << 15);
					break;
				case 1:
					sb_snum |= (1 << 16);
					break;
				case 2:
					sb_snum |= (1 << 12);
					break;
				}
			}

			if ((sb_snum & (1 << 15)) != 0 && p.heat_amount > 0) {
				p.heat_on ^= 1;
				setpal(p);
				p.inven_icon = 5;
				spritesound(NITEVISION_ONOFF, p.i);
				FTA(106 + (p.heat_on == 0 ? 1 : 0), p);
			}

			if ((sb_snum & (1 << 12)) != 0) {
				if (p.steroids_amount == 400) {
					p.steroids_amount--;
					spritesound(DUKE_TAKEPILLS, p.i);
					p.inven_icon = 2;
					FTA(12, p);
				}
				return;
			}

			if (p.newowner == -1)
				if ((sb_snum & (1 << 20)) != 0 || (sb_snum & (1 << 27)) != 0 || p.refresh_inventory) {
					p.invdisptime = 26 * 2;

					if ((sb_snum & (1 << 27)) != 0)
						k = 1;
					else
						k = 0;

					if (p.refresh_inventory)
						p.refresh_inventory = false;
					dainv = (short) p.inven_icon;

					i = 0;

					boolean CHECKINV;
					do {
						CHECKINV = false;
						if (i < 9) {
							i++;

							switch (dainv) {
							case 4:
								if (p.jetpack_amount > 0 && i > 1)
									break;
								if (k != 0)
									dainv = 5;
								else
									dainv = 3;
								CHECKINV = true;
								break;
							case 6:
								if (p.scuba_amount > 0 && i > 1)
									break;
								if (k != 0)
									dainv = 7;
								else
									dainv = 5;
								CHECKINV = true;
								break;
							case 2:
								if (p.steroids_amount > 0 && i > 1)
									break;
								if (k != 0)
									dainv = 3;
								else
									dainv = 1;
								CHECKINV = true;
								break;
							case 3:
								if (p.holoduke_amount > 0 && i > 1)
									break;
								if (k != 0)
									dainv = 4;
								else
									dainv = 2;
								CHECKINV = true;
								break;
							case 0:
							case 1:
								if (p.firstaid_amount > 0 && i > 1)
									break;
								if (k != 0)
									dainv = 2;
								else
									dainv = 7;
								CHECKINV = true;
								break;
							case 5:
								if (p.heat_amount > 0 && i > 1)
									break;
								if (k != 0)
									dainv = 6;
								else
									dainv = 4;
								CHECKINV = true;
								break;
							case 7:
								if (p.boot_amount > 0 && i > 1)
									break;
								if (k != 0)
									dainv = 1;
								else
									dainv = 6;
								CHECKINV = true;
								break;
							}
						} else
							dainv = 0;
						p.inven_icon = dainv;
					} while (CHECKINV);

					switch (dainv) {
					case 1:
						FTA(3, p);
						break;
					case 2:
						FTA(90, p);
						break;
					case 3:
						FTA(91, p);
						break;
					case 4:
						FTA(88, p);
						break;
					case 5:
						FTA(101, p);
						break;
					case 6:
						FTA(89, p);
						break;
					case 7:
						FTA(6, p);
						break;
					}
				}

			j = ((sb_snum & (15 << 8)) >> 8) - 1;
			if (j > 0 && p.kickback_pic > 0) {
//	            p.wantweaponfire = (short) j;
			}

			if (p.last_pissed_time <= (26 * 218) && p.show_empty_weapon == 0 && p.kickback_pic == 0 && p.quick_kick == 0
					&& sprite[p.i].xrepeat > 32 && p.access_incs == 0 && p.knee_incs == 0) {
				if (!IsOriginalGame() || (p.weapon_pos == 0 || (p.holster_weapon != 0 && p.weapon_pos == -9))) // quick
																												// weapon
																												// switch
				{
					if (j == 12) // last used weapon
					{
						j = p.curr_weapon;
						if (p.last_used_weapon == 0)
							j = p.last_used_weapon;
						else if (p.gotweapon[p.last_used_weapon] && p.ammo_amount[p.last_used_weapon] > 0)
							j = p.last_used_weapon;
					}

					if (j == 10 || j == 11) // prev/next
					{
						k = p.curr_weapon;
						j = (j == 10 ? -1 : 1);
						i = 0;

						while ((k >= 0 && k < 10)
								|| (currentGame.getCON().PLUTOPAK && k == EXPANDER_WEAPON
										&& (p.subweapon & (1 << EXPANDER_WEAPON)) != 0)
								|| (currentGame.getCON().type == 20 && k == INCINERATOR_WEAPON
										&& (p.subweapon & (1 << INCINERATOR_WEAPON)) != 0)) {
							switch (k) {
							case INCINERATOR_WEAPON: // Twentieth Anniversary World Tour
								if (j == -1)
									k = TRIPBOMB_WEAPON;
								else
									k = PISTOL_WEAPON;
								break;
							case EXPANDER_WEAPON:
								if (j == -1)
									k = HANDBOMB_WEAPON;
								else
									k = DEVASTATOR_WEAPON;
								break;
							default:
								k += j;
								if (k == SHRINKER_WEAPON && (p.subweapon & (1 << EXPANDER_WEAPON)) != 0)
									k = EXPANDER_WEAPON;

								if (k == FREEZE_WEAPON && (p.subweapon & (1 << INCINERATOR_WEAPON)) != 0)
									k = INCINERATOR_WEAPON;

								break;
							}

							if (k == -1)
								k = 9;
							else if (k == 10)
								k = 0;

							if (p.gotweapon[k] && p.ammo_amount[k] > 0) {
								if (currentGame.getCON().PLUTOPAK && k == SHRINKER_WEAPON
										&& (p.subweapon & (1 << EXPANDER_WEAPON)) != 0)
									k = EXPANDER_WEAPON;
								if (currentGame.getCON().type == 20 && k == FREEZE_WEAPON
										&& (p.subweapon & (1 << INCINERATOR_WEAPON)) != 0)
									k = INCINERATOR_WEAPON;
								j = k;
								break;
							} else if (currentGame.getCON().PLUTOPAK && k == EXPANDER_WEAPON
									&& p.ammo_amount[EXPANDER_WEAPON] == 0 && p.gotweapon[SHRINKER_WEAPON]
									&& p.ammo_amount[SHRINKER_WEAPON] > 0) {
								j = SHRINKER_WEAPON;
								p.subweapon &= ~(1 << EXPANDER_WEAPON);
								break;
							} else if (currentGame.getCON().PLUTOPAK && k == SHRINKER_WEAPON
									&& p.ammo_amount[SHRINKER_WEAPON] == 0 && p.gotweapon[EXPANDER_WEAPON]
									&& p.ammo_amount[EXPANDER_WEAPON] > 0) {
								j = EXPANDER_WEAPON;
								p.subweapon |= (1 << EXPANDER_WEAPON);
								break;
							} // Twentieth Anniversary World Tour
							else if (currentGame.getCON().type == 20 && k == INCINERATOR_WEAPON
									&& p.ammo_amount[INCINERATOR_WEAPON] == 0 && p.gotweapon[FREEZE_WEAPON]
									&& p.ammo_amount[FREEZE_WEAPON] > 0) {
								j = FREEZE_WEAPON;
								p.subweapon &= ~(1 << INCINERATOR_WEAPON);
								break;
							} else if (currentGame.getCON().type == 20 && k == FREEZE_WEAPON
									&& p.ammo_amount[FREEZE_WEAPON] == 0 && p.gotweapon[INCINERATOR_WEAPON]
									&& p.ammo_amount[INCINERATOR_WEAPON] > 0) {
								j = INCINERATOR_WEAPON;
								p.subweapon |= (1 << INCINERATOR_WEAPON);
								break;
							}

							i++;
							if (i == 10) {
								addweapon(p, KNEE_WEAPON);
								break;
							}
						}
					}

					k = -1;
					if (j == HANDBOMB_WEAPON && p.ammo_amount[HANDBOMB_WEAPON] == 0) {
						k = headspritestat[1];
						while (k >= 0) {
							if (sprite[k].picnum == HEAVYHBOMB && sprite[k].owner == p.i) {
								p.gotweapon[HANDBOMB_WEAPON] = true;
								j = HANDREMOTE_WEAPON;
								break;
							}
							k = nextspritestat[k];
						}
					}

					// Twentieth Anniversary World Tour
					if (j == FREEZE_WEAPON && currentGame.getCON().type == 20) {
						if (p.curr_weapon != INCINERATOR_WEAPON && p.curr_weapon != FREEZE_WEAPON) {
							if (p.ammo_amount[INCINERATOR_WEAPON] > 0) {
								if ((p.subweapon & (1 << INCINERATOR_WEAPON)) == (1 << INCINERATOR_WEAPON))
									j = INCINERATOR_WEAPON;
								else if (p.ammo_amount[FREEZE_WEAPON] == 0) {
									j = INCINERATOR_WEAPON;
									p.subweapon |= (1 << INCINERATOR_WEAPON);
								}
							} else if (p.ammo_amount[FREEZE_WEAPON] > 0)
								p.subweapon &= ~(1 << INCINERATOR_WEAPON);
						} else if (p.curr_weapon == FREEZE_WEAPON) {
							p.subweapon |= (1 << INCINERATOR_WEAPON);
							j = INCINERATOR_WEAPON;
						} else
							p.subweapon &= ~(1 << INCINERATOR_WEAPON);
					} else if (j == SHRINKER_WEAPON && currentGame.getCON().PLUTOPAK) {
						if (p.curr_weapon != EXPANDER_WEAPON && p.curr_weapon != SHRINKER_WEAPON) {
							if (p.ammo_amount[EXPANDER_WEAPON] > 0) {
								if ((p.subweapon & (1 << EXPANDER_WEAPON)) == (1 << EXPANDER_WEAPON))
									j = EXPANDER_WEAPON;
								else if (p.ammo_amount[SHRINKER_WEAPON] == 0) {
									j = EXPANDER_WEAPON;
									p.subweapon |= (1 << EXPANDER_WEAPON);
								}
							} else if (p.ammo_amount[SHRINKER_WEAPON] > 0)
								p.subweapon &= ~(1 << EXPANDER_WEAPON);
						} else if (p.curr_weapon == SHRINKER_WEAPON) {
							p.subweapon |= (1 << EXPANDER_WEAPON);
							j = EXPANDER_WEAPON;
						} else
							p.subweapon &= ~(1 << EXPANDER_WEAPON);
					}

					if (p.holster_weapon != 0) {
						sb_snum |= 1 << 19;
						p.weapon_pos = -9;
					} else if (j >= 0 && j < MAX_WEAPONS && p.gotweapon[j] && p.curr_weapon != j)
						switch (j) {
						case KNEE_WEAPON:
							addweapon(p, KNEE_WEAPON);
							break;
						case PISTOL_WEAPON:
							if (p.ammo_amount[PISTOL_WEAPON] == 0)
								if (p.show_empty_weapon == 0) {
									p.last_full_weapon = p.curr_weapon;
									p.show_empty_weapon = 32;
								}
							addweapon(p, PISTOL_WEAPON);
							break;
						case SHOTGUN_WEAPON:
							if (p.ammo_amount[SHOTGUN_WEAPON] == 0 && p.show_empty_weapon == 0) {
								p.last_full_weapon = p.curr_weapon;
								p.show_empty_weapon = 32;
							}
							addweapon(p, SHOTGUN_WEAPON);
							break;
						case CHAINGUN_WEAPON:
							if (p.ammo_amount[CHAINGUN_WEAPON] == 0 && p.show_empty_weapon == 0) {
								p.last_full_weapon = p.curr_weapon;
								p.show_empty_weapon = 32;
							}
							addweapon(p, CHAINGUN_WEAPON);
							break;
						case RPG_WEAPON:
							if (p.ammo_amount[RPG_WEAPON] == 0)
								if (p.show_empty_weapon == 0) {
									p.last_full_weapon = p.curr_weapon;
									p.show_empty_weapon = 32;
								}
							addweapon(p, RPG_WEAPON);
							break;
						case DEVASTATOR_WEAPON:
							if (p.ammo_amount[DEVASTATOR_WEAPON] == 0 && p.show_empty_weapon == 0) {
								p.last_full_weapon = p.curr_weapon;
								p.show_empty_weapon = 32;
							}
							addweapon(p, DEVASTATOR_WEAPON);
							break;
						case FREEZE_WEAPON:
						case INCINERATOR_WEAPON: // Twentieth Anniversary World Tour
							if (p.ammo_amount[j] == 0 && p.show_empty_weapon == 0) {
								p.last_full_weapon = p.curr_weapon;
								p.show_empty_weapon = 32;
							}
							addweapon(p, j);
							break;
						case EXPANDER_WEAPON:
						case SHRINKER_WEAPON:

							if (p.ammo_amount[j] == 0 && p.show_empty_weapon == 0) {
								p.show_empty_weapon = 32;
								p.last_full_weapon = p.curr_weapon;
							}

							addweapon(p, j);
							break;
						case HANDREMOTE_WEAPON:
							if (k >= 0) // Found in list of [1]'s
							{
								p.curr_weapon = HANDREMOTE_WEAPON;
								p.last_weapon = -1;
								p.weapon_pos = 10;
							}
							break;
						case HANDBOMB_WEAPON:
							if (p.ammo_amount[HANDBOMB_WEAPON] > 0 && p.gotweapon[HANDBOMB_WEAPON])
								addweapon(p, HANDBOMB_WEAPON);
							break;
						case TRIPBOMB_WEAPON:
							if (p.ammo_amount[TRIPBOMB_WEAPON] > 0 && p.gotweapon[TRIPBOMB_WEAPON])
								addweapon(p, TRIPBOMB_WEAPON);
							break;
						}
				}

				if ((sb_snum & (1 << 19)) != 0) {
					if (p.curr_weapon > KNEE_WEAPON) {
						if (p.holster_weapon == 0 && p.weapon_pos == 0) {
							p.holster_weapon = 1;
							p.weapon_pos = -1;
							FTA(73, p);
						} else if (p.holster_weapon == 1 && p.weapon_pos == -9) {
							p.holster_weapon = 0;
							p.weapon_pos = 10;
							FTA(74, p);
						}
					}
				}
			}

			if ((sb_snum & (1 << 24)) != 0 && p.newowner == -1) {
				if (p.holoduke_on == -1) {

					if (p.holoduke_amount > 0) {
						p.inven_icon = 3;

						p.holoduke_on = (short) (i = EGS(p.cursectnum, p.posx, p.posy, p.posz + (30 << 8), APLAYER, -64,
								0, 0, (short) p.ang, 0, 0, -1, (short) 10));
						hittype[i].temp_data[3] = hittype[i].temp_data[4] = 0;
						sprite[i].yvel = (short) snum;
						sprite[i].extra = 0;
						FTA(47, p);
						spritesound(TELEPORTER, p.holoduke_on);
					} else
						FTA(49, p);
				} else {
					spritesound(TELEPORTER, p.holoduke_on);
					p.holoduke_on = -1;
					FTA(48, p);
				}
			}

			if ((sb_snum & (1 << 16)) != 0) {
				if (p.firstaid_amount > 0 && sprite[p.i].extra < currentGame.getCON().max_player_health) {
					j = currentGame.getCON().max_player_health - sprite[p.i].extra;

					if (p.firstaid_amount > j) {
						p.firstaid_amount -= j;
						sprite[p.i].extra = (short) currentGame.getCON().max_player_health;
						p.inven_icon = 1;
					} else {
						sprite[p.i].extra += p.firstaid_amount;
						p.firstaid_amount = 0;
						checkavailinven(p);
					}
					spritesound(DUKE_USEMEDKIT, p.i);
				}
			}

			if ((sb_snum & (1 << 25)) != 0 && p.newowner == -1) {
				if (p.jetpack_amount > 0) {
					p.jetpack_on = p.jetpack_on != 0 ? 0 : 1;
					if (p.jetpack_on != 0) {
						p.inven_icon = 4;
						if (p.scream_voice != null) {
							p.scream_voice.dispose();
							p.scream_voice = null;
						}

						spritesound(DUKE_JETPACK_ON, p.i);

						FTA(52, p);
					} else {
						p.hard_landing = 0;
						p.poszv = 0;
						spritesound(DUKE_JETPACK_OFF, p.i);
						stopsound(DUKE_JETPACK_IDLE, p.i);
						stopsound(DUKE_JETPACK_ON, p.i);
						FTA(53, p);
					}
				} else
					FTA(50, p);
			}

			if ((sb_snum & (1 << 28)) != 0 && p.one_eighty_count == 0)
				p.one_eighty_count = -1024;
		}
	}

	public boolean IsOriginalGame() {
		return ud.recstat == 1 && ud.rec != null && ud.rec.recversion <= JFBYTEVERSION;
	}
}
