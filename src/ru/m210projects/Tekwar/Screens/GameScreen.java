package ru.m210projects.Tekwar.Screens;

import static ru.m210projects.Build.Engine.CLIPMASK0;
import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.clipmove_sectnum;
import static ru.m210projects.Build.Engine.clipmove_x;
import static ru.m210projects.Build.Engine.clipmove_y;
import static ru.m210projects.Build.Engine.clipmove_z;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.neartag;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.numwalls;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.pushmove_sectnum;
import static ru.m210projects.Build.Engine.pushmove_x;
import static ru.m210projects.Build.Engine.pushmove_y;
import static ru.m210projects.Build.Engine.pushmove_z;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.show2dsprite;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.zr_ceilz;
import static ru.m210projects.Build.Engine.zr_florhit;
import static ru.m210projects.Build.Engine.zr_florz;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Gameutils.BCosAngle;
import static ru.m210projects.Build.Gameutils.BSinAngle;
import static ru.m210projects.Build.Gameutils.isValidSprite;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Tekwar.Animate.doanimations;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.AUDIOSET;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.COLORCORR;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.HELP;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.LASTSAVE;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.LOADGAME;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.MAIN;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.OPTIONS;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.QUIT;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.SAVEGAME;
import static ru.m210projects.Tekwar.Globals.KENSPLAYERHEIGHT;
import static ru.m210projects.Tekwar.Globals.TICSPERFRAME;
import static ru.m210projects.Tekwar.Main.boardfilename;
import static ru.m210projects.Tekwar.Main.engine;
import static ru.m210projects.Tekwar.Main.fortieth;
import static ru.m210projects.Tekwar.Main.gCutsceneScreen;
import static ru.m210projects.Tekwar.Main.gGameScreen;
import static ru.m210projects.Tekwar.Main.gMissionScreen;
import static ru.m210projects.Tekwar.Main.gPrecacheScreen;
import static ru.m210projects.Tekwar.Main.hours;
import static ru.m210projects.Tekwar.Main.lockclock;
import static ru.m210projects.Tekwar.Main.minutes;
import static ru.m210projects.Tekwar.Main.screenpeek;
import static ru.m210projects.Tekwar.Main.seconds;
import static ru.m210projects.Tekwar.Names.GIFTBOX;
import static ru.m210projects.Tekwar.Names.SWITCH1ON;
import static ru.m210projects.Tekwar.Names.SWITCH2OFF;
import static ru.m210projects.Tekwar.Names.SWITCH2ON;
import static ru.m210projects.Tekwar.Names.SWITCH3OFF;
import static ru.m210projects.Tekwar.Names.SWITCH3ON;
import static ru.m210projects.Tekwar.Names.SWITCH4OFF;
import static ru.m210projects.Tekwar.Names.SWITCH4ON;
import static ru.m210projects.Tekwar.Names.USEWATERFOUNTAIN;
import static ru.m210projects.Tekwar.Names.WATERFOUNTAIN;
import static ru.m210projects.Tekwar.Player.gPlayer;
import static ru.m210projects.Tekwar.Tekchng.changehealth;
import static ru.m210projects.Tekwar.Tekchng.changescore;
import static ru.m210projects.Tekwar.Tekchng.tekchangefallz;
import static ru.m210projects.Tekwar.Tekgun.doweapanim;
import static ru.m210projects.Tekwar.Tekgun.tekfiregun;
import static ru.m210projects.Tekwar.Tekldsv.gQuickSaving;
import static ru.m210projects.Tekwar.Tekldsv.quickload;
import static ru.m210projects.Tekwar.Tekldsv.quicksave;
import static ru.m210projects.Tekwar.Tekldsv.quickslot;
import static ru.m210projects.Tekwar.Tekldsv.savegame;
import static ru.m210projects.Tekwar.Tekmap.allsymsdeposited;
import static ru.m210projects.Tekwar.Tekmap.debriefing;
import static ru.m210projects.Tekwar.Tekmap.gameover;
import static ru.m210projects.Tekwar.Tekmap.killedsonny;
import static ru.m210projects.Tekwar.Tekmap.mission;
import static ru.m210projects.Tekwar.Tekmap.missionfailed;
import static ru.m210projects.Tekwar.Tekmap.newgame;
import static ru.m210projects.Tekwar.Tekmap.symbols;
import static ru.m210projects.Tekwar.Tekmap.symbolsdeposited;
import static ru.m210projects.Tekwar.Tekprep.initplayersprite;
import static ru.m210projects.Tekwar.Tekprep.starta;
import static ru.m210projects.Tekwar.Tekprep.starts;
import static ru.m210projects.Tekwar.Tekprep.startx;
import static ru.m210projects.Tekwar.Tekprep.starty;
import static ru.m210projects.Tekwar.Tekprep.startz;
import static ru.m210projects.Tekwar.Tekprep.tekrestoreplayer;
import static ru.m210projects.Tekwar.Tekspr.checktouchsprite;
import static ru.m210projects.Tekwar.Tekstat.SECT_LOTAG_CLIMB;
import static ru.m210projects.Tekwar.Tekstat.operatesprite;
import static ru.m210projects.Tekwar.Tekstat.pickup;
import static ru.m210projects.Tekwar.Tekstat.statuslistcode;
import static ru.m210projects.Tekwar.Tekstat.toss;
import static ru.m210projects.Tekwar.Tektag.checkmapsndfx;
import static ru.m210projects.Tekwar.Tektag.headbob;
import static ru.m210projects.Tekwar.Tektag.onelev;
import static ru.m210projects.Tekwar.Tektag.operatesector;
import static ru.m210projects.Tekwar.Tektag.slimesoundcnt;
import static ru.m210projects.Tekwar.Tektag.tagcode;
import static ru.m210projects.Tekwar.Tektag.tekheadbob;
import static ru.m210projects.Tekwar.Tektag.teknewsector;
import static ru.m210projects.Tekwar.Tektag.tekswitchtrigger;
import static ru.m210projects.Tekwar.Tektag.waterfountaincnt;
import static ru.m210projects.Tekwar.Tektag.waterfountainwall;
import static ru.m210projects.Tekwar.View.dohealthscreen;
import static ru.m210projects.Tekwar.View.dorearviewscreen;
import static ru.m210projects.Tekwar.View.douprtscreen;
import static ru.m210projects.Tekwar.View.drawscreen;
import static ru.m210projects.Tekwar.View.messageon;
import static ru.m210projects.Tekwar.View.ozoom;
import static ru.m210projects.Tekwar.View.redcount;
import static ru.m210projects.Tekwar.View.tekscreenfx;
import static ru.m210projects.Tekwar.View.updatepaletteshifts;
import static ru.m210projects.Tekwar.View.whitecount;
import static ru.m210projects.Tekwar.View.zoom;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildMessage.MessageType;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Tekwar.Config.TekKeys;
import ru.m210projects.Tekwar.Main;
import ru.m210projects.Tekwar.Player;
import ru.m210projects.Tekwar.Tekprep;
import ru.m210projects.Tekwar.Teksnd;
import ru.m210projects.Tekwar.Factory.TekMenuHandler;

public class GameScreen extends GameAdapter {

	private Main game;

	public GameScreen(Main game) {
		super(game, Main.gLoadingScreen);
		this.game = game;
		for (int i = 0; i < MAXPLAYERS; i++)
			gPlayer[i] = new Player();
		show2dsprite[gPlayer[myconnectindex].playersprite >> 3] |= (1 << (gPlayer[myconnectindex].playersprite & 7));
	}

	@Override
	public void show() {
		if (mission != 8)
			pMenu.mClose();
	}

	@Override
	public void PreFrame(BuildNet net) {
		if (gameover != 0) {
			net.ready2send = false;

			if (allsymsdeposited == 3) {
				if (killedsonny != 0)
					CompleteGame();
				else
					game.gExit = true;
				return;
			}

			String debriefing = debriefing();
			if (allsymsdeposited == 1) {
				debriefing = "FINALB.SMK";
				allsymsdeposited = 2;
			}

			if (debriefing != null && gCutsceneScreen.init(debriefing)) {
				gCutsceneScreen.setCallback(new Runnable() {
					@Override
					public void run() {
						game.changeScreen(gMissionScreen);
					}
				}).escSkipping(true);
				game.changeScreen(gCutsceneScreen);
			} else
				game.changeScreen(gMissionScreen);
		}

		if (gQuickSaving) {
			if (captBuffer != null) {
				savegame("[quicksave_" + +quickslot + "]", "quicksav" + quickslot + ".sav");
				quickslot ^= 1;
				gQuickSaving = false;
			} else gGameScreen.capture(160, 100);
		}

//		if(gAutosaveRequest)
//		{
//			savegame("[autosave]", "autosave.sav");
//			gAutosaveRequest = false;
//		}

	}

	@Override
	public void ProcessFrame(BuildNet net) {
		for (short i = connecthead; i >= 0; i = connectpoint2[i])
			gPlayer[i].pInput.Copy(net.gFifoInput[net.gNetFifoTail & 0xFF][i]);
		net.gNetFifoTail++;

		if (game.gPaused || game.menu.gShowMenu || Console.IsShown())
			if(!game.menu.isOpened(game.menu.mMenus[LASTSAVE]))
				return;

		for (int i = connecthead; i >= 0; i = connectpoint2[i])
			gPlayer[i].ocursectnum = gPlayer[i].cursectnum;

//		if (recstat) {
//			for (i = connecthead; i >= 0; i = connectpoint2[i])
//				pDemoInput[reccnt][i].copy(Player.input[i]);
//			reccnt++;
//			if (reccnt > 16383)
//				reccnt = 16383;
//		}

		ozoom = zoom;
		for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
			gPlayer[i].oposx = gPlayer[i].posx;
			gPlayer[i].oposy = gPlayer[i].posy;
			gPlayer[i].oposz = gPlayer[i].posz;
			gPlayer[i].ohoriz = gPlayer[i].horiz;
			gPlayer[i].oang = gPlayer[i].ang;
		}

		for (int i = 1; i <= 8; i++) {
			for (int j = headspritestat[i]; j >= 0; j = nextspritestat[j]) {
				game.pInt.setsprinterpolate(j, sprite[j]);
			}
		}

		for (short i = connecthead; i >= 0; i = connectpoint2[i]) {
			processinput(i);
			if (!gPlayer[i].noclip && gPlayer[i].cursectnum != -1) {
				checktouchsprite(i, gPlayer[i].cursectnum);
				short startwall = sector[gPlayer[i].cursectnum].wallptr;
				int endwall = startwall + sector[gPlayer[i].cursectnum].wallnum;
				for (int j = startwall; j < endwall; j++) {
					WALL wal = wall[j];
					if (wal.nextsector >= 0)
						checktouchsprite(i, wal.nextsector);
				}
			}
		}

		doanimations();
		tagcode();
		statuslistcode();
		dorearviewscreen();
		douprtscreen();
		dohealthscreen();
		doweapanim(screenpeek);
		tektime();
		updatepaletteshifts();

		lockclock += TICSPERFRAME;
	}

	@Override
	public void DrawHud(float smooth) {
		tekscreenfx((int) smooth);

		pEngine.updateFade("DAMAGE", redcount);
		pEngine.updateFade("PICKUP", whitecount);

		if ((whitecount | redcount) != 0)
			pEngine.showfade();
	}

	@Override
	public void DrawWorld(float smooth) {
		drawscreen(screenpeek, (int) smooth);
	}

	@Override
	protected void startboard(Runnable startboard)
	{
		gPrecacheScreen.init(false, startboard);
		game.changeScreen(gPrecacheScreen);
	}

	@Override
	public void KeyHandler() {
		TekMenuHandler menu = game.menu;
		if (menu.gShowMenu) {
			engine.handleevents();
			menu.mKeyHandler(game.pInput, BuildGdx.graphics.getDeltaTime());
			return;
		}

		if(Console.IsShown())
			return;

		BuildControls input = game.pInput;
		if (input.ctrlGetInputKey(GameKeys.Menu_Toggle, true))
			menu.mOpen(menu.mMenus[MAIN], -1);

		// non mappable function keys
		if (input.ctrlGetInputKey(TekKeys.Show_HelpScreen, true))
			menu.mOpen(menu.mMenus[HELP], -1);
		else if (input.ctrlGetInputKey(TekKeys.Show_SaveMenu, true)) {
			if (game.nNetMode == NetMode.Single) {
				gGameScreen.capture(160, 100);
				menu.mOpen(menu.mMenus[SAVEGAME], -1);
			}
		} else if (input.ctrlGetInputKey(TekKeys.Show_LoadMenu, true)) {
			if (game.nNetMode == NetMode.Single) {
				menu.mOpen(menu.mMenus[LOADGAME], -1);
			}
		} else if (input.ctrlGetInputKey(TekKeys.Quit, true)) {
			menu.mOpen(menu.mMenus[QUIT], -1);
		} else if (input.ctrlGetInputKey(TekKeys.Show_Options, true)) {
			menu.mOpen(menu.mMenus[OPTIONS], -1);
		} else if (input.ctrlGetInputKey(TekKeys.Quicksave, true)) {
			quicksave();
		} else if (input.ctrlGetInputKey(TekKeys.Quickload, true)) {
			quickload();
		} else if (input.ctrlGetInputKey(TekKeys.Show_SoundSetup, true)) {
			menu.mOpen(menu.mMenus[AUDIOSET], -1);
		} else if (input.ctrlGetInputKey(TekKeys.Gamma, true)) {
			menu.mOpen(menu.mMenus[COLORCORR], -1);
		}
	}

	@Override
	protected boolean prepareboard(final String map) {
		boolean out = Tekprep.prepareboard(map);
		if(out) {
			for (short i = connecthead; i >= 0; i = connectpoint2[i])
				initplayersprite(i);
			checkmapsndfx(screenpeek);
			if (mission == 7) {
				for (int i = 0; i < MAXPLAYERS; i++)
					gPlayer[i].updategun = 7;
			}
			game.nNetMode = NetMode.Single;
		} else game.show();

		return out;
	}

	public void processinput(short snum) {
		int nexti;
		int i, j, doubvel, xvect, yvect, goalz;

		// move player snum
		if (snum < 0 || snum >= MAXPLAYERS) {
			game.ThrowError("game712: Invalid player number " + snum);
		}

		if ((gPlayer[snum].pInput.vel | gPlayer[snum].pInput.svel) != 0 && gPlayer[snum].health > 0) {
			// no run while crouching
			if (gPlayer[snum].pInput.Crouch && (mission != 7)) {
				doubvel = 1 + (gPlayer[snum].pInput.Run ? 1 : 0);
			} else {
				doubvel = (TICSPERFRAME << (gPlayer[snum].pInput.Run ? 1 : 0));
				doubvel <<= 1;
			}
			xvect = 0;
			yvect = 0;

			if (gPlayer[snum].pInput.vel != 0) {
				xvect += (int) (gPlayer[snum].pInput.vel * doubvel * BCosAngle(gPlayer[snum].ang)) / 8.0f;
				yvect += (int) (gPlayer[snum].pInput.vel * doubvel * BSinAngle(gPlayer[snum].ang)) / 8.0f;
			}
			if (gPlayer[snum].pInput.svel != 0) {
				xvect += (int) (gPlayer[snum].pInput.svel * doubvel * BCosAngle(gPlayer[snum].ang + 1536)) / 8.0f;
				yvect += (int) (gPlayer[snum].pInput.svel * doubvel * BSinAngle(gPlayer[snum].ang + 1536)) / 8.0f;
			}
			if (gPlayer[snum].noclip) {
				gPlayer[snum].posx += xvect >> 14;
				gPlayer[snum].posy += yvect >> 14;
				gPlayer[snum].cursectnum = pEngine.updatesector(gPlayer[snum].posx, gPlayer[snum].posy,
						gPlayer[snum].cursectnum);
				pEngine.changespritesect(snum, gPlayer[snum].cursectnum);
			} else {
				pEngine.clipmove(gPlayer[snum].posx, gPlayer[snum].posy, gPlayer[snum].posz, gPlayer[snum].cursectnum,
						xvect, yvect, 128, 4 << 8, 4 << 8, CLIPMASK0);
				gPlayer[snum].posx = clipmove_x;
				gPlayer[snum].posy = clipmove_y;
				gPlayer[snum].posz = clipmove_z;
				gPlayer[snum].cursectnum = clipmove_sectnum;
			}
			if (game.nNetMode != NetMode.Multiplayer)
				tekheadbob();

		} else {
			headbob = 0;
		}

		int sec = pEngine.updatesector(gPlayer[snum].posx, gPlayer[snum].posy, gPlayer[snum].cursectnum);
		if (!gPlayer[snum].noclip) {
			if (sec >= 0) {
				// push player away from walls if clipmove doesn't work

				int push = pEngine.pushmove(gPlayer[snum].posx, gPlayer[snum].posy, gPlayer[snum].posz,
						gPlayer[snum].cursectnum, 128, 4 << 8, 4 << 8, CLIPMASK0);
				gPlayer[snum].posx = pushmove_x;
				gPlayer[snum].posy = pushmove_y;
				gPlayer[snum].posz = pushmove_z;
				gPlayer[snum].cursectnum = pushmove_sectnum;

				if (push < 0) {
					changehealth(snum, -1000); // if this fails then instant
												// death
					changescore(snum, -5);
				}
			} else {
				changehealth(snum, -1000);
				changescore(snum, -5);
			}
		}

		if (!gPlayer[snum].noclip && (gPlayer[snum].cursectnum < 0 || gPlayer[snum].cursectnum >= numsectors)) {
			game.GameMessage("game718: Invalid sector for player " + snum + " @ " + gPlayer[snum].posx + " "
					+ gPlayer[snum].posy + " " + (gPlayer[snum].cursectnum));
			game.show();
			return;
		}
		if (gPlayer[snum].playersprite < 0 || gPlayer[snum].playersprite >= MAXSPRITES) {
			game.GameMessage("game751: Invalid sprite for player " + snum + " " + (gPlayer[snum].playersprite));
			game.show();
			return;
		}

		// getzrange returns the highest and lowest z's for an entire box,
		// NOT just a point. This prevents you from falling off cliffs
		// when you step only slightly over the cliff.

		sprite[gPlayer[snum].playersprite].cstat ^= 1;
		pEngine.getzrange(gPlayer[snum].posx, gPlayer[snum].posy, gPlayer[snum].posz, gPlayer[snum].cursectnum, 128,
				CLIPMASK0);
		int globhiz = zr_ceilz;
		int globloz = zr_florz;
		int globlohit = zr_florhit;
		sprite[gPlayer[snum].playersprite].cstat ^= 1;

		if (!gPlayer[snum].noclip && gPlayer[snum].cursectnum != gPlayer[snum].ocursectnum) {
			teknewsector(snum);
		}

		if (gPlayer[snum].pInput.angvel != 0) {
			doubvel = TICSPERFRAME;
			// if run key then turn faster
			if (gPlayer[snum].pInput.Run)
				doubvel += (TICSPERFRAME >> 1);

			gPlayer[snum].ang += gPlayer[snum].pInput.angvel * doubvel / 16.0f;
			gPlayer[snum].ang = BClampAngle(gPlayer[snum].ang);
		}

		if (gPlayer[snum].health < 0) {
			gPlayer[snum].health -= (TICSPERFRAME << 1);
			if (gPlayer[snum].health <= -160) {
				gPlayer[snum].hvel = 0;

				if(gPlayer[snum].pInput.Use) {
					gPlayer[snum].deaths++;
					if (game.nNetMode != NetMode.Multiplayer && (numplayers == 1)) {
						// makefadeout(32,0,0,100);
					}
					if (game.nNetMode == NetMode.Multiplayer) {
						// netstartspot(&gPlayer[snum].posx,&gPlayer[snum].posy,&gPlayer[snum].cursectnum);
						// if (gPlayer[snum].cursectnum < 0 || gPlayer[snum].cursectnum >=
						// numsectors) {
						// crash("game818: Invalid sector for player %d
						// (%d)",snum,gPlayer[snum].cursectnum);
						// }
						// placerandompic(KLIPPIC);
						// placerandompic(MEDICKITPIC);
						// gPlayer[snum].posz = sector[gPlayer[snum].cursectnum].floorz-(1<<8);
						// gPlayer[snum].ang = (krand_intercept("GAME 802")&2047);
					} else {
						gPlayer[snum].posx = startx;
						gPlayer[snum].posy = starty;
						gPlayer[snum].posz = startz;
						gPlayer[snum].ang = (short) starta;
						gPlayer[snum].cursectnum = (short) starts;
					}

					tekrestoreplayer(snum);

					if (game.nNetMode != NetMode.Multiplayer && (missionfailed() == 0))
						newgame(boardfilename, null);
					return;
				}

			} // Les 10/01/95
			else { // if 0
				sprite[gPlayer[snum].playersprite].xrepeat = (byte) Math.max(((128 + gPlayer[snum].health) >> 1), 0);
				sprite[gPlayer[snum].playersprite].yrepeat = (byte) Math.max(((128 + gPlayer[snum].health) >> 1), 0);

				gPlayer[snum].hvel += (TICSPERFRAME << 2);
				gPlayer[snum].horiz = Math.max(gPlayer[snum].horiz - 4, 0);
				gPlayer[snum].posz += gPlayer[snum].hvel;
				if (gPlayer[snum].posz > globloz - (4 << 8)) {
					gPlayer[snum].posz = globloz - (4 << 8);
					gPlayer[snum].horiz = Math.min(gPlayer[snum].horiz + 5, 200);
					gPlayer[snum].hvel = 0;
				}
			} // Les 10/01/95

			return;
		}
		if (gPlayer[snum].pInput.Center) {
			gPlayer[snum].autocenter = true;
		}
		if (gPlayer[snum].autocenter) {
			if (gPlayer[snum].horiz > 100) {
				gPlayer[snum].horiz -= 4;
				if (gPlayer[snum].horiz < 100) {
					gPlayer[snum].horiz = 100;
				}
			} else if (gPlayer[snum].horiz < 100) {
				gPlayer[snum].horiz += 4;
				if (gPlayer[snum].horiz > 100) {
					gPlayer[snum].horiz = 100;
				}
			}
			if (gPlayer[snum].horiz == 100)
				gPlayer[snum].autocenter = false;
		}

		if (gPlayer[snum].pInput.mlook > 0 && gPlayer[snum].horiz > 0) {
			gPlayer[snum].horiz -= gPlayer[snum].pInput.mlook;
		}
		if (gPlayer[snum].pInput.mlook < 0 && gPlayer[snum].horiz < 200) {
			gPlayer[snum].horiz -= gPlayer[snum].pInput.mlook;
		}

		if ((gPlayer[snum].pInput.Look_Down) && (gPlayer[snum].horiz > 0))
			gPlayer[snum].horiz -= 4; // -
		if ((gPlayer[snum].pInput.Look_Up) && (gPlayer[snum].horiz < 200))
			gPlayer[snum].horiz += 4; // +

		// 32 pixels above floor is where player should be
		goalz = globloz - (KENSPLAYERHEIGHT << 8);

		// kens slime sector
		if (isValidSector(gPlayer[snum].cursectnum) && sector[gPlayer[snum].cursectnum].lotag == 4) {
			// if not on a sprite
			if ((globlohit & 0xc000) != 49152) {
				goalz = globloz - (8 << 8);
				if (gPlayer[snum].posz >= goalz - (2 << 8)) {
					pEngine.clipmove(gPlayer[snum].posx, gPlayer[snum].posy, gPlayer[snum].posz,
							gPlayer[snum].cursectnum, -TICSPERFRAME << 14, -TICSPERFRAME << 14, 128, 4 << 8, 4 << 8,
							CLIPMASK0);
					gPlayer[snum].posx = clipmove_x;
					gPlayer[snum].posy = clipmove_y;
					gPlayer[snum].posz = clipmove_z;
					gPlayer[snum].cursectnum = clipmove_sectnum;
					if (slimesoundcnt[snum] >= 0) {
						slimesoundcnt[snum] -= TICSPERFRAME;
						while (slimesoundcnt[snum] < 0) {
							slimesoundcnt[snum] += 120;
						}
					}
				}
			}
		}

		// case where ceiling & floor are too close
		if (goalz < globhiz + 4096) {
			goalz = (globloz + globhiz) >> 1;
		}

		if (isValidSector(gPlayer[snum].cursectnum)) {
			// climb ladder or regular z movement
			if ((mission == 7) || (sector[gPlayer[snum].cursectnum].lotag == SECT_LOTAG_CLIMB)) {
				if (gPlayer[snum].pInput.Jump) {
					if (gPlayer[snum].posz > (sector[gPlayer[snum].cursectnum].ceilingz + 2048)) {
						gPlayer[snum].posz -= 64 << 2;
						if (gPlayer[snum].pInput.Run) {
							gPlayer[snum].posz -= 128 << 2;
						}
						if (mission == 7) {
							gPlayer[snum].posz -= 256 << 2;
						}
					}
				} else if (gPlayer[snum].pInput.Crouch) {
					if (gPlayer[snum].posz < (sector[gPlayer[snum].cursectnum].floorz - 2048)) {
						gPlayer[snum].posz += 64 << 2;
						if (gPlayer[snum].pInput.Run) {
							gPlayer[snum].posz += 128 << 2;
						}
						if (mission == 7) {
							gPlayer[snum].posz += 256 << 2;
						}
					}
				}
			} else {
				if (gPlayer[snum].health >= 0) {
					if (gPlayer[snum].pInput.Jump) {
						if (gPlayer[snum].posz >= globloz - (KENSPLAYERHEIGHT << 8)) {
							goalz -= (16 << 8);
							goalz -= (24 << 8);
						}
					}
					if (gPlayer[snum].pInput.Crouch) {
						goalz += (12 << 8);
						goalz += (12 << 8);
					}
				}
				// player is on a groudraw area
				if ((sector[gPlayer[snum].cursectnum].floorstat & 2) > 0) {
					Tile pic = engine.getTile(sector[gPlayer[snum].cursectnum].floorpicnum);
					if (pic.data == null) {
						pEngine.loadtile(sector[gPlayer[snum].cursectnum].floorpicnum);
					}
					goalz -= ((pic.data[0]
							+ (((gPlayer[snum].posx >> 4) & 63) << 6) + ((gPlayer[snum].posy >> 4) & 63)) << 8);
				}
				// gravity, plus check for if on an elevator
				if (gPlayer[snum].posz < goalz) {
					gPlayer[snum].hvel += (TICSPERFRAME << 5) + 1;
				} else {
					if ((globlohit & 0xC000) == 0xC000) { // on a sprite
						if ((globlohit - 49152) < 0 || (globlohit - 49152) >= MAXSPRITES) {
							game.ThrowError("game961: Invalid sprite index " + (globlohit - 49152));
						}
						if (sprite[globlohit - 49152].lotag >= 1500) {
							onelev[snum] = true;
						}
					} else if ((sector[gPlayer[snum].cursectnum].lotag == 1004
							|| sector[gPlayer[snum].cursectnum].lotag == 1005)) {
						onelev[snum] = true;
					} else {
						onelev[snum] = false;
					}
					if (onelev[snum] && !gPlayer[snum].pInput.Crouch) {
						gPlayer[snum].hvel = 0;
						gPlayer[snum].posz = globloz - (KENSPLAYERHEIGHT << 8);
					} else {
						gPlayer[snum].hvel = (((goalz - gPlayer[snum].posz) * TICSPERFRAME) >> 5);
					}
				}
				tekchangefallz(snum, globloz, globhiz);
			}
		}

		// update sprite representation of player
		// should be after movement, but before shooting code
		pEngine.setsprite(gPlayer[snum].playersprite, gPlayer[snum].posx, gPlayer[snum].posy,
				gPlayer[snum].posz + (KENSPLAYERHEIGHT << 8));
		sprite[gPlayer[snum].playersprite].ang = (short) gPlayer[snum].ang;

		// in wrong sector or is ceiling/floor smooshing player
		if (!gPlayer[snum].noclip) {
			if (!isValidSector(gPlayer[snum].cursectnum)) {
				changehealth(snum, -200);
				changescore(snum, -5);
			} else if (globhiz + (8 << 8) > globloz) {
				changehealth(snum, -200);
				changescore(snum, -5);
			}
		}

		// kens waterfountain
		if ((waterfountainwall[snum] >= 0) && (gPlayer[snum].health >= 0)) {
			if (neartag.tagwall < 0 || neartag.tagwall >= numwalls) {
				game.ThrowError("game1009: Invalid wall " + neartag.tagwall);
			}
			if ((wall[neartag.tagwall].lotag != 7) || (!gPlayer[snum].pInput.Use)) {
				i = waterfountainwall[snum];
				if (i < 0 || i >= numwalls) {
					game.ThrowError("game1014: Invalid wall index " + i);
				}
				if (wall[i].overpicnum == USEWATERFOUNTAIN) {
					wall[i].overpicnum = WATERFOUNTAIN;
				} else if (wall[i].picnum == USEWATERFOUNTAIN) {
					wall[i].picnum = WATERFOUNTAIN;
				}
				waterfountainwall[snum] = -1;
			}
		}

		// enter throw
		if ((game.nNetMode != NetMode.Multiplayer) && (pickup.picnum != 0)
				&& (game.pInput.ctrlKeyStatusOnce(pCfg.primarykeys[15])
						|| game.pInput.ctrlKeyStatusOnce(pCfg.secondkeys[15]))) {
			toss(snum);
		}

		if(isValidSector(gPlayer[snum].cursectnum)) {
			// space bar (use) code
			if (gPlayer[snum].pInput.Use && (sector[gPlayer[snum].cursectnum].lotag == 4444)) {
				depositsymbol(snum);
			} else if (gPlayer[snum].pInput.Use) {
				// continuous triggers

				pEngine.neartag(gPlayer[snum].posx, gPlayer[snum].posy, (gPlayer[snum].posz + (8 << 8)),
						gPlayer[snum].cursectnum, (short) gPlayer[snum].ang, neartag, 1024, 3);
				pEngine.hitscan(gPlayer[snum].posx, gPlayer[snum].posy, gPlayer[snum].posz, gPlayer[snum].cursectnum,
						sintable[((short) gPlayer[snum].ang + 2560) & 2047],
						sintable[((short) gPlayer[snum].ang + 2048) & 2047], (int) (100 - gPlayer[snum].horiz) * 2000,
						pHitInfo, 0xFFFF0000);

				int hitDist = (int) pEngine.qdist(gPlayer[snum].posx - pHitInfo.hitx, gPlayer[snum].posy - pHitInfo.hity);

				if (neartag.tagsector == -1) {
					i = gPlayer[snum].cursectnum;
					if ((sector[i].lotag | sector[i].hitag) != 0) {
						neartag.tagsector = (short) i;
					}
				}
				// kens water fountain
				if (neartag.tagwall >= 0) {
					if (neartag.tagwall >= numwalls) {
						game.ThrowError("game1053: Invalid wall index " + neartag.tagwall);
					}
					if (wall[neartag.tagwall].lotag == 7) {
						if (wall[neartag.tagwall].overpicnum == WATERFOUNTAIN) {
							wall[neartag.tagwall].overpicnum = USEWATERFOUNTAIN;
							waterfountainwall[snum] = neartag.tagwall;
						} else if (wall[neartag.tagwall].picnum == WATERFOUNTAIN) {
							wall[neartag.tagwall].picnum = USEWATERFOUNTAIN;
							waterfountainwall[snum] = neartag.tagwall;
						}
						if (waterfountainwall[snum] >= 0) {
							waterfountaincnt[snum] -= TICSPERFRAME;
							while (waterfountaincnt[snum] < 0) {
								waterfountaincnt[snum] += 120;
								changehealth(snum, 2);
							}
						}
					}
				}
				// 1-time triggers
				if (!gPlayer[snum].ouse) {
					if (neartag.tagsector >= 0) {
						if (neartag.tagsector >= numsectors) {
							game.ThrowError("game1070: Invalid sector index " + neartag.tagsector);
						}
						if (sector[neartag.tagsector].hitag == 0)
							operatesector(neartag.tagsector);
					}
					if (neartag.tagwall >= 0) {
						if (neartag.tagwall >= numwalls) {
							game.ThrowError("game1078: Invalid wall index " + neartag.tagwall);
						}
						if (wall[neartag.tagwall].lotag == 2) {
							for (i = 0; i < numsectors; i++) {
								if (sector[i].hitag == wall[neartag.tagwall].hitag) {
									if (sector[i].lotag != 1) {
										operatesector(i);
									}
								}
							}
							i = headspritestat[0];
							while (i != -1) {
								nexti = nextspritestat[i];
								if (sprite[i].hitag == wall[neartag.tagwall].hitag) {
									operatesprite(i);
								}
								i = nexti;
							}
							j = wall[neartag.tagwall].overpicnum;
							if (j == SWITCH1ON) {
								wall[neartag.tagwall].overpicnum = GIFTBOX;
								wall[neartag.tagwall].lotag = 0;
								wall[neartag.tagwall].hitag = 0;
							}
							if (j == GIFTBOX) {
								wall[neartag.tagwall].overpicnum = SWITCH1ON;
								wall[neartag.tagwall].lotag = 0;
								wall[neartag.tagwall].hitag = 0;
							}
							if (j == SWITCH2ON)
								wall[neartag.tagwall].overpicnum = SWITCH2OFF;
							if (j == SWITCH2OFF)
								wall[neartag.tagwall].overpicnum = SWITCH2ON;
							if (j == SWITCH3ON)
								wall[neartag.tagwall].overpicnum = SWITCH3OFF;
							if (j == SWITCH3OFF)
								wall[neartag.tagwall].overpicnum = SWITCH3ON;
							i = wall[neartag.tagwall].point2;
						}
					}

					if (hitDist < 512 && (neartag.tagsprite != -1 || pHitInfo.hitsprite != -1)) {
						int spr = -1;

						if(isValidSprite(pHitInfo.hitsprite)) {
							switch(sprite[pHitInfo.hitsprite].picnum)
							{
							case 3708:
							case 3709:
							case SWITCH2ON:
							case SWITCH2OFF:
							case SWITCH3ON:
							case SWITCH3OFF:
							case SWITCH4ON:
							case SWITCH4OFF:
								spr = pHitInfo.hitsprite;
								break;
							}
						}

						if(spr == -1) {
							if(isValidSprite(neartag.tagsprite))
								spr = neartag.tagsprite;
							else if(isValidSprite(pHitInfo.hitsprite))
								spr = pHitInfo.hitsprite;
						}

						if(spr != -1) {
							if (sprite[spr].lotag == 4) {
								tekswitchtrigger(snum, spr);
							} else {
								operatesprite(spr);
							}
						}
					}
				}
			}
		}

		if (gPlayer[snum].pInput.Fire) {
			tekfiregun(gPlayer[snum].lastgun, snum);
		}

		gPlayer[snum].ouse = gPlayer[snum].pInput.Use;
		gPlayer[snum].ofire = gPlayer[snum].pInput.Fire;
	}

	public void depositsymbol(int snum) {
		int i, findpic = 0;
		int sym = sector[gPlayer[snum].cursectnum].hitag;

		switch (sym) {
		case 0:
			findpic = 3600;
			break;
		case 1:
			findpic = 3604;
			break;
		case 2:
			findpic = 3608;
			break;
		case 3:
			findpic = 3612;
			break;
		case 4:
			findpic = 3592;
			break;
		case 5:
			findpic = 3596;
			break;
		case 6:
			findpic = 3616;
			break;
		}

		if (symbols[sym]) {
			for (i = 0; i < MAXSPRITES; i++) {
				if (sprite[i].picnum == findpic) {
					sprite[i].picnum = (short) (findpic + 1);
					symbolsdeposited[sym] = true;
					break;
				}
			}
		}

		if (symbolsdeposited[0] && symbolsdeposited[1] && symbolsdeposited[2] && symbolsdeposited[3]
				&& symbolsdeposited[4] && symbolsdeposited[5] && symbolsdeposited[6]) {
			allsymsdeposited = 1;
			gameover = 1;
//	         fadein();
		}
	}

	public void tektime() {
		fortieth++;
		if (fortieth == 40) {
			fortieth = 0;
			seconds++;
		}
		if (seconds == 60) {
			minutes++;
			seconds = 0;
		}
		if (minutes == 60) {
			hours++;
			minutes = 0;
		}
		if (hours > 99) {
			hours = 0;
		}
		if (messageon != 0) {
			messageon++;
			if (messageon == 160) {
				messageon = 0;
			}
		}
	}

	public void CompleteGame() {
		if (gCutsceneScreen.init("FINALDB.SMK")) {
			gCutsceneScreen.setCallback(new Runnable() {
				@Override
				public void run() {
					if (gCutsceneScreen.init("CREDITS.SMK")) {
						gCutsceneScreen.setCallback(new Runnable() {
							@Override
							public void run() {
								ExitWithComplete();
							}
						}).escSkipping(true);
						game.changeScreen(gCutsceneScreen);
					} else {
						ExitWithComplete();
					}
				}
			}).escSkipping(true);
			game.changeScreen(gCutsceneScreen);
		} else
			ExitWithComplete();
	}

	public void ExitWithComplete() {
		BuildGdx.message.show("Congratulation", "Game completed!", MessageType.Info);
		Console.Println("Game completed!");
		game.gExit = true;
	}

	@Override
	public void sndHandlePause(boolean pause) {
		Teksnd.sndHandlePause(pause);
	}
}
