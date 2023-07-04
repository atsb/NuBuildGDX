package ru.m210projects.Witchaven.Screens;

import static ru.m210projects.Build.Engine.CLIPMASK0;
import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.clipmove_sectnum;
import static ru.m210projects.Build.Engine.clipmove_x;
import static ru.m210projects.Build.Engine.clipmove_y;
import static ru.m210projects.Build.Engine.clipmove_z;
import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Engine.pushmove_sectnum;
import static ru.m210projects.Build.Engine.pushmove_x;
import static ru.m210projects.Build.Engine.pushmove_y;
import static ru.m210projects.Build.Engine.pushmove_z;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.zr_florhit;
import static ru.m210projects.Build.Engine.zr_florz;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Witchaven.Globals.*;
import static ru.m210projects.Witchaven.Factory.WHControl.*;
import static ru.m210projects.Witchaven.Menu.WHMenuUserContent.*;
import static ru.m210projects.Witchaven.WHPLR.*;
import static ru.m210projects.Witchaven.Main.*;
import static ru.m210projects.Witchaven.Names.*;
import static ru.m210projects.Witchaven.WH1Names.*;
import static ru.m210projects.Witchaven.WH2MUS.attacktheme;
import static ru.m210projects.Witchaven.WH2MUS.startsong;
import static ru.m210projects.Witchaven.Potions.potionchange;
import static ru.m210projects.Witchaven.Potions.usapotion;
import static ru.m210projects.Build.Gameutils.BClampAngle;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Witchaven.Factory.WHMenuHandler.*;
import static ru.m210projects.Witchaven.Animate.doanimations;
import static ru.m210projects.Witchaven.WHScreen.*;
import static ru.m210projects.Witchaven.WHSND.*;
import static ru.m210projects.Witchaven.Spellbooks.activatedaorb;
import static ru.m210projects.Witchaven.Spellbooks.bookprocess;
import static ru.m210projects.Witchaven.Spellbooks.lvlspellcheck;
import static ru.m210projects.Witchaven.Spellbooks.speelbookprocess;
import static ru.m210projects.Witchaven.WHANI.animateobjs;
import static ru.m210projects.Witchaven.WHFX.dofx;
import static ru.m210projects.Witchaven.WHFX.scarytime;
import static ru.m210projects.Witchaven.WHFX.sectorsounds;
import static ru.m210projects.Witchaven.Whldsv.*;
import static ru.m210projects.Witchaven.Whmap.*;

import com.badlogic.gdx.Input.Keys;

import static ru.m210projects.Witchaven.Config.*;
import static ru.m210projects.Witchaven.WHOBJ.newstatus;
import static ru.m210projects.Witchaven.WHOBJ.processobjs;
import static ru.m210projects.Witchaven.WHOBJ.timerprocess;
import static ru.m210projects.Witchaven.WHTAG.animatetags;
import static ru.m210projects.Witchaven.WHTAG.dodelayitems;
import static ru.m210projects.Witchaven.Weapons.autoweaponchange;
import static ru.m210projects.Witchaven.Weapons.plrfireweapon;
import static ru.m210projects.Witchaven.Weapons.weaponchange;
import static ru.m210projects.Witchaven.Weapons.weaponsprocess;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;
import ru.m210projects.Witchaven.Main;
import ru.m210projects.Witchaven.Factory.WHControl;
import ru.m210projects.Witchaven.Factory.WHMenuHandler;
import ru.m210projects.Witchaven.Main.UserFlag;
import ru.m210projects.Witchaven.WHSND;
import ru.m210projects.Witchaven.Whmap;
import ru.m210projects.Witchaven.Menu.MenuInterfaceSet;
import ru.m210projects.Witchaven.Types.EpisodeInfo;
import ru.m210projects.Witchaven.Types.PLAYER;
import ru.m210projects.Witchaven.Types.PLOCATION;

public class GameScreen extends GameAdapter {

	public int ihaveflag = 0;
	public int justplayed = 0;
	public int lopoint = 0;
	public int walktoggle = 0;
	public int runningtime = 0;
	public int oldhoriz;
	public int gNameShowTime;

	private Main game;
	public GameScreen(Main game) {
		super(game, Main.gLoadScreen);
		this.game = game;
		for (int i = 0; i < MAXPLAYERS; i++) {
			player[i] = new PLAYER();
			gPrevPlayerLoc[i] = new PLOCATION();
		}
	}

	@Override
	public void ProcessFrame(BuildNet net) {
		for (short i = connecthead; i >= 0; i = connectpoint2[i])
			player[i].pInput.Copy(net.gFifoInput[net.gNetFifoTail & 0xFF][i]);
		net.gNetFifoTail++;

		if (game.gPaused || game.menu.gShowMenu || Console.IsShown())
			return;

		for (int i = connecthead; i >= 0; i = connectpoint2[i])
			player[i].oldsector = player[i].sector;

		PLAYER plr = player[pyrn];
		viewBackupPlayerLoc(pyrn);

		processinput(pyrn);
		updateviewmap(plr);
		updatepaletteshifts();

        processobjs(plr);
        animateobjs(plr);
        animatetags(pyrn);
        doanimations();
        dodelayitems(TICSPERFRAME);
        dofx();
        speelbookprocess(plr);
        timerprocess(plr);
        weaponsprocess(pyrn);
        whtime();

        updatesounds();

        if (followmode) {
			followa += followang;

			followx += (followvel * sintable[(512 + 2048 - followa) & 2047]) >> 10;
			followy += (followvel * sintable[(512 + 1024 - 512 - followa) & 2047]) >> 10;

			followx += (followsvel * sintable[(512 + 1024 - 512 - followa) & 2047]) >> 10;
			followy -= (followsvel * sintable[(512 + 2048 - followa) & 2047]) >> 10;
		}

		lockclock += TICSPERFRAME;
	}

	private void whtime()
	{
		 fortieth++;
	     if( fortieth == 40 ) {
	          fortieth = 0;
	          seconds++;
	     }
	     if( seconds == 60 ) {
	          minutes++;
	          seconds=0;
	     }
	     if( minutes == 60 ) {
	          hours++;
	          minutes=0;
	     }
	}

	@Override
	public void DrawWorld(float smooth) {
		drawscreen(pyrn, (int) smooth);
		if (!game.gPaused && game.WH2 && songptr.handle != null && !songptr.handle.isPlaying()) {
			startsong(engine.rand() % 2);
	    	attacktheme = 0;
		}
	}

	@Override
	public void DrawHud(float smooth) {
		if(pMenu.gShowMenu && !(pMenu.getCurrentMenu() instanceof MenuInterfaceSet))
			return;

		if (!player[pyrn].dead)
			drawweapons(pyrn);
		if (player[pyrn].spiked == 1)
			spikeheart(player[pyrn]);
		if (scarytime >= 0)
			drawscary();

		drawInterface(player[pyrn]);

		if (game.isCurrentScreen(gGameScreen) && totalclock < gNameShowTime) {
			int transp = 0;
			if (totalclock > gNameShowTime - 20)
				transp = 1;
			if (totalclock > gNameShowTime - 10)
				transp = 33;

			if (whcfg.showMapInfo && !game.menu.gShowMenu) {
				if(gCurrentEpisode != null && gCurrentEpisode.getMap(mapon) != null)
					game.getFont(1).drawText(160, 114, gCurrentEpisode.getMap(mapon).title, 0, 0, TextAlign.Center, 2 | transp, true);
				else if (boardfilename != null)
					game.getFont(1).drawText(160, 114, boardfilename, 0, 0, TextAlign.Center, 2 | transp, true);
			}
		}

		pEngine.updateFade("RED",  redcount);
		pEngine.updateFade("WHITE", whitecount);
		pEngine.updateFade("GREEN", greencount);
		pEngine.updateFade("BLUE", 2 * bluecount);

//		if(FileIndicator)
//			game.getFont(1).drawText(270,5,  toCharArray("Caching"), 0, 7, TextAlign.Left, 2 | 512, false);

		if ((redcount | whitecount | greencount | bluecount) != 0)
			pEngine.showfade();
	}

	@Override
	protected void startboard(Runnable startboard)
	{
		gPrecacheScreen.init(false, startboard);
		game.changeScreen(gPrecacheScreen);
	}

	@Override
	public void KeyHandler() {
		WHMenuHandler menu = game.menu;
		if (menu.gShowMenu) {
			engine.handleevents();
			menu.mKeyHandler(game.pInput, BuildGdx.graphics.getDeltaTime());
			return;
		}

		if(Console.IsShown())
			return;

		BuildControls input = game.pInput;
		if (input.ctrlGetInputKey(GameKeys.Menu_Toggle, true))
			menu.mOpen(menu.mMenus[GAME], -1);

		// non mappable function keys
		if (input.ctrlGetInputKey(WhKeys.Show_Savemenu, true)) {
			if (game.nNetMode == NetMode.Single) {
				gGameScreen.capture(160, 100);
				menu.mOpen(menu.mMenus[SAVEGAME], -1);
			}
		} else if (input.ctrlGetInputKey(WhKeys.Show_Loadmenu, true)) {
			if (game.nNetMode == NetMode.Single) {
				menu.mOpen(menu.mMenus[LOADGAME], -1);
			}
		} else if (input.ctrlGetInputKey(WhKeys.Quit, true)) {
			menu.mOpen(menu.mMenus[QUIT], -1);
		} else if (input.ctrlGetInputKey(WhKeys.Show_Options, true)) {
			menu.mOpen(menu.mMenus[OPTIONS], -1);
		} else if (input.ctrlGetInputKey(WhKeys.Quicksave, true)) {
			quicksave();
		} else if (input.ctrlGetInputKey(WhKeys.Quickload, true)) {
			quickload();
		} else if (input.ctrlGetInputKey(WhKeys.Show_Sounds, true)) {
			menu.mOpen(menu.mMenus[AUDIOSET], -1);
		} else if (input.ctrlGetInputKey(WhKeys.Gamma, true)) {
			menu.mOpen(menu.mMenus[COLORCORR], -1);
		}
	}

	@Override
	public void PostFrame(BuildNet net) {
		if (gQuickSaving) {
			if (captBuffer != null) {
				savegame("[quicksave_" + +quickslot + "]", "quicksav" + quickslot + ".sav");
				quickslot ^= 1;
				gQuickSaving = false;
			} else gGameScreen.capture(160, 100);
		}

		if(gAutosaveRequest)
		{
			if (captBuffer != null) {
				savegame("[autosave]", "autosave.sav");
				gAutosaveRequest = false;
			} else gGameScreen.capture(160, 100);
		}
	}

	@Override
	protected boolean prepareboard(String map) {
		sndStopMusic();
		game.nNetMode = NetMode.Single;
		gNameShowTime = 500;
		followmode = false;
		((WHControl) game.pInput).reset();
		if(!Whmap.prepareboard(map)) {
			game.show();
			return false;
		}

		return true;
	}

	public void processinput(int num) {

		int goalz, lohit = 0, loz = 0, tics, xvect, yvect;

		int oldposx, oldposy;
		int dist;
		int feetoffground;
		short onsprite = -1;

		PLAYER plr = player[num];

		int bits = plr.pInput.bits;

		if((bits & (CtrlJump)) == 0)
			plr.keytoggle = false;

		boolean onground;

		if (plr.health <= 0) {
			playerdead(plr);
			if(plr.dead) {
				if (plr.horiz < 100 + (YDIM >> 1))
					plr.horiz += (TICSPERFRAME << 1);

				if(lastload != null && !lastload.isEmpty() && BuildGdx.compat.checkFile(lastload, Path.User) != null) {
					showmessage("Press \"USE\" to load last saved game or press \"ENTER\" to restart level", 320);

					if((bits & CtrlOpen) != 0)
					{
						game.changeScreen(gLoadScreen);
						gLoadScreen.init(new Runnable() {
							@Override
							public void run() {
								if(!loadgame(lastload)) {
									game.setPrevScreen();
									if(game.isCurrentScreen(gGameScreen))
										showmessage("Incompatible version of saved game found!", 360);
								}
							}
						});
					}
				} else showmessage("Press \"ENTER\" to restart level", 320);

				if (getInput().keyPressed(Keys.ENTER)) {
					if(mUserFlag == UserFlag.UserMap)
					{
						gGameScreen.loadboard(boardfilename, null);
						return;
					}

					// restart
					initplayersprite(plr);
					justteleported = true;
					lockclock = totalclock;
					if (!netgame)
						loadnewlevel(mapon);
					zoom = 256;
					displaytime = 0;
				}
			}
			return;
		}

		xvect = yvect = 0;
		tics = TICSPERFRAME;

		plr.pInput.fvel += (tics * damage_vel) << 14;
		plr.pInput.svel += (tics * damage_svel) << 14;
		plr.pInput.angvel += damage_angvel;


		if(damage_vel != 0)
			damage_vel = BClipHigh(damage_vel + (tics << 3), 0);
		if(damage_svel != 0)
			damage_svel = BClipLow(damage_svel - (tics << 3), 0);
		if(damage_angvel != 0)
			damage_angvel = BClipLow(damage_angvel - (tics << 1), 0);

		sprite[plr.spritenum].cstat ^= 1;
		pEngine.getzrange(plr.x, plr.y, plr.z, plr.sector, 128, CLIPMASK0);

		loz = zr_florz;
		lohit = zr_florhit;

		sprite[plr.spritenum].cstat ^= 1;

		if ((lohit & 0xc000) == 49152) {
			if ((sprite[lohit & 4095].z - plr.z) <= (getPlayerHeight() << 8))
				onsprite = (short) (lohit & 4095);
		} else
			onsprite = -1;

		feetoffground = (plr.sector != -1) ? (sector[plr.sector].floorz - plr.z) : 0;

		if (klabs(plr.pInput.svel|plr.pInput.fvel) > (game.WH2 ? WH2NORMALKEYMOVE : NORMALKEYMOVE)) {
			if (feetoffground > (32 << 8))
				tics += tics >> 1;
		}

		plr.horiz = BClipRange(plr.horiz + plr.pInput.horiz, -(YDIM >> 1), 100 + (YDIM >> 1));

		if ((bits & CtrlAim_Down) != 0) {
			if (plr.horiz > 100 - (YDIM >> 1)) {
				plr.horiz -= (TICSPERFRAME << 1);
				autohoriz = 0;
			}
		} else if ((bits & CtrlAim_Up) != 0) {
			if (plr.horiz < 100 + (YDIM >> 1))
				plr.horiz += (TICSPERFRAME << 1);
			autohoriz = 0;
		}

		if ((bits & CtrlEndflying) != 0)
			plr.orbactive[5] = -1;

		if ((bits & CtrlWeapon_Fire) != 0 && plr.hasshot == 0) { // Les 07/27/95
			if (plr.currweaponfired == 0)
				plrfireweapon(plr);
		}

		// cast
		if ((bits & CtrlCastspell) != 0 && plr.orbshot == 0 && plr.currweaponflip == 0 && plr.currweaponanim == 0) {
			if (plr.orb[plr.currentorb] == 1 && plr.currweapon == 0) {
				if (lvlspellcheck(plr)) {
					plr.orbshot = 1;
					activatedaorb(plr);
				}
			}
			if (plr.currweapon != 0) {
				autoweaponchange(plr, 0);
			}
		}

		if ((bits & CtrlInventory_Use) != 0) {
			if (plr.potion[plr.currentpotion] > 0) {
				usapotion(plr);
			}
		}

		if ((bits & CtrlOpen) != 0) {
			if (netgame) {
//				   netdropflag();
			} else {
				plruse(plr);
			}
		}

		if ( (bits & CtrlTurnAround) != 0 )
		{
			if ( plr.turnAround == 0 )
				plr.turnAround = -1024;
		}

		if ( plr.turnAround < 0 )
	    {
			plr.turnAround = (short) BClipHigh(plr.turnAround + 64, 0);
			plr.ang = BClampAngle(plr.ang + 64);
	    }

		if (plr.sector != -1 && ((sector[plr.sector].floorpicnum != LAVA || sector[plr.sector].floorpicnum != SLIME
				|| sector[plr.sector].floorpicnum != WATER || sector[plr.sector].floorpicnum != HEALTHWATER
				|| sector[plr.sector].floorpicnum != ANILAVA || sector[plr.sector].floorpicnum != LAVA1
				|| sector[plr.sector].floorpicnum != LAVA2) && feetoffground <= (32 << 8))) {
			plr.pInput.fvel /= 3;
			plr.pInput.svel /= 3;
		}

		if ((plr.sector != -1 && (sector[plr.sector].floorpicnum == LAVA || sector[plr.sector].floorpicnum == SLIME
				|| sector[plr.sector].floorpicnum == WATER || sector[plr.sector].floorpicnum == HEALTHWATER
				|| sector[plr.sector].floorpicnum == ANILAVA || sector[plr.sector].floorpicnum == LAVA1
				|| sector[plr.sector].floorpicnum == LAVA2)) && plr.orbactive[5] < 0 // loz
				&& plr.z >= sector[plr.sector].floorz - (plr.height << 8) - (8 << 8)) {
			goalz = loz - (32 << 8);
			switch (sector[plr.sector].floorpicnum) {
			case ANILAVA:
			case LAVA:
			case LAVA1:
			case LAVA2:
				if (plr.treasure[TYELLOWSCEPTER] == 1) {
					goalz = loz - (getPlayerHeight() << 8);
					break;
				} else {
						plr.pInput.fvel -= plr.pInput.fvel >> 3;
						plr.pInput.svel -= plr.pInput.svel >> 3;
					}

				if (plr.invincibletime > 0 || plr.manatime > 0 || plr.godMode)
					break;
				else {
					if (lavasnd == -1) {
						lavasnd = playsound(S_FIRELOOP1, 0, 0, -1);
					}
					addhealth(plr, -1);
					startredflash(10);
				}
				break;
			case WATER:
				if (plr.treasure[TBLUESCEPTER] == 1) {
					goalz = loz - (getPlayerHeight() << 8);
				} else {
					plr.pInput.fvel -= plr.pInput.fvel >> 3;
					plr.pInput.svel -= plr.pInput.svel >> 3;
				}
				break;
			case HEALTHWATER:
				if (plr.health < plr.maxhealth) {
					addhealth(plr, 1);
					startblueflash(5);
				}
				break;
			}
		} else if (plr.orbactive[5] > 0) {
			goalz = plr.z - (plr.height << 8);
			plr.hvel = 0;
		} else
			goalz = loz - (plr.height << 8);

		if (plr.orbactive[5] < 0 && (bits & CtrlJump) != 0 && !plr.keytoggle) {
			if (plr.onsomething != 0) {
				if(game.WH2)
					plr.hvel -= WH2JUMPVEL;
				else plr.hvel -= JUMPVEL;

				plr.onsomething = 0;
				plr.keytoggle = true;
			}
		}

		if ((bits & CtrlCrouch) != 0) {
			if (plr.sector != -1 && goalz < ((sector[plr.sector].floorz) - (plr.height >> 3))) {
				if (game.WH2)
					goalz += (48 << 8);
				else
					goalz += (24 << 8);
			}
		}

		onground = plr.onsomething != 0;
		int vel = (klabs(plr.pInput.fvel) + klabs(plr.pInput.svel)) >> 16;
		if(vel < 16) vel = 0;
		if ((bits & CtrlFlyup) != 0 || (bits & CtrlJump) != 0)
			dophysics(plr, goalz, 1, vel);
		else if ((bits & CtrlFlydown) != 0 || (bits & CtrlCrouch) != 0)
			dophysics(plr, goalz, -1, vel);
		else
			dophysics(plr, goalz, 0, vel);

		if (!onground && plr.onsomething != 0) {
			if (plr.fallz > 32768) {
				if ((pEngine.krand() % 2) != 0)
					playsound_loc(S_PLRPAIN1 + (pEngine.krand() % 2), plr.x, plr.y);
				else
					playsound_loc(S_PUSH1 + (pEngine.krand() % 2), plr.x, plr.y);

				addhealth(plr, -(plr.fallz >> 13));
				plr.fallz = 0;// wango
			} else if (plr.fallz > 8192) {
				playsound_loc(S_BREATH1 + (pEngine.krand() % 2), plr.x, plr.y);
			}
		}

		if (ihaveflag > 0) { // WHNET
			plr.pInput.fvel -= plr.pInput.fvel >> 2;
			plr.pInput.svel -= plr.pInput.svel >> 2;
		}

		if (plr.pInput.fvel != 0 || plr.pInput.svel != 0) {

			xvect = plr.pInput.fvel;
			yvect = plr.pInput.svel;

//			xvect = yvect = 0;
//			if (plr.pInput.fvel != 0) {
//				xvect = (int) (plr.pInput.fvel * tics * BCosAngle(plr.ang));
//				yvect = (int) (plr.pInput.fvel * tics * BSinAngle(plr.ang));
//			}
//			if (plr.pInput.svel != 0) {
//				xvect += (plr.pInput.svel * tics * BSinAngle(plr.ang));
//				yvect -= (plr.pInput.svel * tics * BCosAngle(plr.ang));
//			}

			oldposx = plr.x;
			oldposy = plr.y;

			if(plr.noclip) {
				plr.x += xvect >> 14;
				plr.y += yvect >> 14;
				short sect = engine.updatesector(plr.x, plr.y, plr.sector);
				if (sect != -1)
					plr.sector = sect;
			} else {
				pEngine.clipmove(plr.x, plr.y, plr.z, plr.sector, xvect, yvect, 128, 4 << 8, 4 << 8, CLIPMASK0);
				if (clipmove_sectnum != -1) {
					plr.x = clipmove_x;
					plr.y = clipmove_y;
					plr.z = clipmove_z;
					plr.sector = clipmove_sectnum;
				}

				pEngine.pushmove(plr.x, plr.y, plr.z, plr.sector, 128, 4 << 8, 4 << 8, CLIPMASK0);
				if(pushmove_sectnum != -1) {
					plr.x = pushmove_x;
					plr.y = pushmove_y;
					plr.z = pushmove_z;
					plr.sector = pushmove_sectnum;
				}
			}

			// JSA BLORB

			if (plr.sector != plr.oldsector) {
				if (lavasnd != -1) {
					switch (sector[plr.sector].floorpicnum) {
					case ANILAVA:
					case LAVA:
					case LAVA1:
					case LAVA2:
						break;
					default:
						stopsound(lavasnd);
						lavasnd = -1;
						break;
					}
				}
				sectorsounds();
			}

			// walking on sprite
			plr.horiz -= oldhoriz;

			dist = pEngine.ksqrt((plr.x - oldposx) * (plr.x - oldposx) + (plr.y - oldposy) * (plr.y - oldposy));

			if (klabs(plr.pInput.svel|plr.pInput.fvel) > (game.WH2 ? WH2NORMALKEYMOVE : NORMALKEYMOVE))
				dist >>= 2;

			if (dist > 0 && feetoffground <= (plr.height << 8) || onsprite != -1) {
				oldhoriz = ((dist * sintable[(totalclock << 5) & 2047]) >> 19) >> 2;
				plr.horiz += oldhoriz;
			} else
				oldhoriz = 0;

			plr.horiz = BClipRange(plr.horiz, -(YDIM >> 1), 100 + (YDIM >> 1));

			if (onsprite != -1 && dist > 50 && lopoint == 1 && justplayed == 0) {

				switch (sprite[onsprite].picnum) {
				case WALLARROW:
				case OPENCHEST:
				case GIFTBOX:
					if (walktoggle != 0)
						playsound_loc(S_WOOD1, (plr.x + 3000), plr.y);
					else
						playsound_loc(S_WOOD1, plr.x, (plr.y + 3000));
					walktoggle ^= 1;
					justplayed = 1;
					break;
				case WOODPLANK: // wood planks
					if (walktoggle != 0)
						playsound_loc(S_SOFTCHAINWALK, (plr.x + 3000), plr.y);
					else
						playsound_loc(S_SOFTCHAINWALK,plr.x,(plr.y+3000));
						walktoggle ^= 1;
					justplayed = 1;

					break;
				case SQUAREGRATE: // square grating
				case SQUAREGRATE + 1:
					if (walktoggle != 0)
						playsound_loc(S_LOUDCHAINWALK, (plr.x + 3000), plr.y);
					else
						playsound_loc(S_LOUDCHAINWALK, plr.x, (plr.y + 3000));
					walktoggle ^= 1;
					justplayed = 1;
					break;
				case SPACEPLANK: // spaced planks
					if (walktoggle != 0)
						playsound_loc(S_SOFTCREAKWALK, (plr.x + 3000), plr.y);
					else
						playsound_loc(S_SOFTCREAKWALK, plr.x, (plr.y + 3000));
					walktoggle ^= 1;
					justplayed = 1;
					break;
				case SPIDER:
					// STOMP
					playsound_loc(S_DEADSTEP, sprite[onsprite].x, sprite[onsprite].y);
					justplayed = 1;
					newstatus(onsprite, DIE);
					break;

				case FREDDEAD:
				case 1980:
				case 1981:
				case 1984:
				case 1979:
				case 1957:
				case 1955:
				case 1953:
				case 1952:
				case 1941:
				case 1940:
					playsound_loc(S_DEADSTEP, plr.x, plr.y);
					justplayed = 1;

					break;

				default:
					break;
				}

				if(sprite[onsprite].picnum == RAT)
				{
					playsound_loc(S_RATS1 + (pEngine.krand() % 2), sprite[onsprite].x, sprite[onsprite].y);
					justplayed = 1;
					pEngine.deletesprite(onsprite);
				}
			}

			if (lopoint == 0 && oldhoriz == -2 && justplayed == 0)
				lopoint = 1;

			if (lopoint == 1 && oldhoriz != -2 && justplayed == 1) {
				lopoint = 0;
				justplayed = 0;
			}

			vel = (klabs(plr.pInput.fvel) + klabs(plr.pInput.svel)) >> 16;
			if (vel > 40 && dist > 10)
				runningtime += TICSPERFRAME;
			else
				runningtime -= TICSPERFRAME;

			if (runningtime < -360)
				runningtime = 0;

			if (runningtime > 360) {
				SND_Sound(S_PLRPAIN1);
				runningtime = 0;
			}
		}
		if (plr.pInput.angvel != 0) {
			plr.ang += plr.pInput.angvel * TICSPERFRAME / 16.0f;
			plr.ang = BClampAngle(plr.ang);
		}

		game.pInt.setsprinterpolate(plr.spritenum, sprite[plr.spritenum]);
		pEngine.setsprite(plr.spritenum, plr.x, plr.y, plr.z + (plr.height << 8));
		sprite[plr.spritenum].ang = (short) plr.ang;

		if (plr.sector >= 0 && engine.getceilzofslope(plr.sector, plr.x, plr.y) > engine.getflorzofslope(plr.sector, plr.x, plr.y) - (8 << 8))
			addhealth(plr, -10);

		if ((bits & CtrlAim_Center) != 0) {
			autohoriz = 1;
		}

		if (autohoriz == 1)
			autothehoriz(plr);
		if(plr.currweaponfired != 1 && plr.currweaponfired != 6)
			plr.hasshot = 0;
		weaponchange(num);
		bookprocess(num);
		potionchange(num);
	}

	public void autothehoriz(PLAYER plr) {
		if (plr.horiz < 100)
			plr.horiz = BClipHigh(plr.horiz + (TICSPERFRAME << 2), 100);
		if (plr.horiz > 100)
			plr.horiz = BClipLow(plr.horiz - (TICSPERFRAME << 2), 100);
		if (plr.horiz == 100)
			autohoriz = 0;
	}

	public void dophysics(PLAYER plr, int goalz, int flyupdn, int v) {
		if (plr.orbactive[5] > 0) {
			if (v > 0) {
				if (plr.horiz > 125)
					plr.hvel -= (TICSPERFRAME << 8);
				else if (plr.horiz < 75)
					plr.hvel += (TICSPERFRAME << 8);
			}
			if (flyupdn > 0) {
				plr.hvel -= (TICSPERFRAME << 7);
			}
			if (flyupdn < 0) {
				plr.hvel += (TICSPERFRAME << 7);
			}
			plr.hvel += (sintable[(lockclock << 4) & 2047] >> 6);
			plr.fallz = 0;

		} else if (plr.z < goalz) {
			if (game.WH2)
				plr.hvel += (TICSPERFRAME * WH2GRAVITYCONSTANT);
			else
				plr.hvel += GRAVITYCONSTANT;
			plr.onsomething &= ~(GROUNDBIT | PLATFORMBIT);
			plr.fallz += plr.hvel;
		} else if (plr.z > goalz) {
			plr.hvel -= ((plr.z - goalz) >> 6);
			plr.onsomething |= GROUNDBIT;
			plr.fallz = 0;
		} else {
			plr.fallz = 0;
		}

		plr.z += plr.hvel;
		if (plr.hvel > 0 && plr.z > goalz) {
			plr.hvel >>= 2;
		} else if (plr.onsomething != 0) {
			if (plr.hvel < 0 && plr.z < goalz) {
				plr.hvel = 0;
				plr.z = goalz;
			}
		}

		if (plr.sector != -1) {
			if (plr.z - (plr.height >> 2) < engine.getceilzofslope(plr.sector, plr.x, plr.y)) {
				plr.z = engine.getceilzofslope(plr.sector, plr.x, plr.y) + (plr.height >> 2);
				plr.hvel = 0;
			} else {
				if (plr.orbactive[5] > 0) {
					if (plr.z + (plr.height << 7) > engine.getflorzofslope(plr.sector, plr.x, plr.y)) {
						plr.z = engine.getflorzofslope(plr.sector, plr.x, plr.y) - (plr.height << 7);
						plr.hvel = 0;
					}
				} else {
					if (plr.z + (plr.height >> 4) > engine.getflorzofslope(plr.sector, plr.x, plr.y)) {
						plr.z = engine.getflorzofslope(plr.sector, plr.x, plr.y) - (plr.height >> 4);
						plr.hvel = 0;
					}
				}
			}
		}
		plr.jumphoriz = -(plr.hvel >> 8);
	}

	@Override
	public void sndHandlePause(boolean pause) {
		WHSND.sndHandlePause(pause);
	}

	public void newgame(Object item, int nLevel, int skills) {
		pNet.ready2send = false;
		game.nNetMode = NetMode.Single;

		mapon = nLevel;
		difficulty = skills;
		justteleported = false;
		nextlevel = false;

		UserFlag flag = UserFlag.None;
		if(item instanceof EpisodeInfo) {
			EpisodeInfo game = (EpisodeInfo)item;


			if(!game.equals(gOriginalEpisode)) {
				if(!checkEpisodeResources(game))
					return;

				flag = UserFlag.Addon;
				Console.Println("Start user episode: " + game.Title);
			} else resetEpisodeResources(game);

			if(gCurrentEpisode != null && gCurrentEpisode.getMap(mapon) != null)
				boardfilename = gCurrentEpisode.getMap(mapon).path;
		} else if(item instanceof FileEntry) {
			flag = UserFlag.UserMap;
			boardfilename = ((FileEntry)item).getPath();
			mapon = 0;
			resetEpisodeResources(null);
			Console.Println("Start user map: " + ((FileEntry)item).getName());
		}
		mUserFlag = flag;

		sndStopMusic();
		if(mUserFlag == UserFlag.UserMap) {
			gGameScreen.loadboard(boardfilename, null).setTitle("Loading " + boardfilename);
			game.pInput.resetMousePos();
		} else if(gCurrentEpisode != null && boardfilename != null) {
			gGameScreen.loadboard(boardfilename, null);
		} else game.show(); //can't restart the level

		game.menu.mClose();
	}

}
