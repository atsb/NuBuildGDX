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

package ru.m210projects.Redneck.Screens;

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
import static ru.m210projects.Redneck.Actors.isPsychoSkill;
import static ru.m210projects.Redneck.Actors.moveactors;
import static ru.m210projects.Redneck.Actors.movecyclers;
import static ru.m210projects.Redneck.Actors.movedummyplayers;
import static ru.m210projects.Redneck.Actors.moveeffectors;
import static ru.m210projects.Redneck.Actors.moveexplosions;
import static ru.m210projects.Redneck.Actors.movefallers;
import static ru.m210projects.Redneck.Actors.movefta;
import static ru.m210projects.Redneck.Actors.movefx;
import static ru.m210projects.Redneck.Actors.moveplayers;
import static ru.m210projects.Redneck.Actors.movestandables;
import static ru.m210projects.Redneck.Actors.movetransports;
import static ru.m210projects.Redneck.Actors.sub_64EF0;
import static ru.m210projects.Redneck.Animate.doanimations;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.COLORCORR;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.GAME;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.HELP;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.LOADGAME;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.OPTIONS;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.QUIT;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.SAVEGAME;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.SOUNDSET;
import static ru.m210projects.Redneck.Globals.ALIENBLASTER_WEAPON;
import static ru.m210projects.Redneck.Globals.BOAT_WEAPON;
import static ru.m210projects.Redneck.Globals.BOWLING_WEAPON;
import static ru.m210projects.Redneck.Globals.BUZSAW_WEAPON;
import static ru.m210projects.Redneck.Globals.BYTEVERSIONRR;
import static ru.m210projects.Redneck.Globals.CHICKENBOW_WEAPON;
import static ru.m210projects.Redneck.Globals.CROSSBOW_WEAPON;
import static ru.m210projects.Redneck.Globals.DYNAMITE_WEAPON;
import static ru.m210projects.Redneck.Globals.GDXBYTEVERSION;
import static ru.m210projects.Redneck.Globals.HANDREMOTE_WEAPON;
import static ru.m210projects.Redneck.Globals.KNEE_WEAPON;
import static ru.m210projects.Redneck.Globals.MAX_WEAPONSRA;
import static ru.m210projects.Redneck.Globals.MODE_END;
import static ru.m210projects.Redneck.Globals.MODE_EOL;
import static ru.m210projects.Redneck.Globals.MODE_TYPE;
import static ru.m210projects.Redneck.Globals.MOTO_WEAPON;
import static ru.m210projects.Redneck.Globals.PISTOL_WEAPON;
import static ru.m210projects.Redneck.Globals.POWDERKEG_WEAPON;
import static ru.m210projects.Redneck.Globals.RATE_WEAPON;
import static ru.m210projects.Redneck.Globals.RIFLEGUN_WEAPON;
import static ru.m210projects.Redneck.Globals.RRRA;
import static ru.m210projects.Redneck.Globals.SHOTGUN_WEAPON;
import static ru.m210projects.Redneck.Globals.Sound;
import static ru.m210projects.Redneck.Globals.THROWSAW_WEAPON;
import static ru.m210projects.Redneck.Globals.TICSPERFRAME;
import static ru.m210projects.Redneck.Globals.TIT_WEAPON;
import static ru.m210projects.Redneck.Globals.boardfilename;
import static ru.m210projects.Redneck.Globals.buf;
import static ru.m210projects.Redneck.Globals.currentGame;
import static ru.m210projects.Redneck.Globals.defGame;
import static ru.m210projects.Redneck.Globals.display_mirror;
import static ru.m210projects.Redneck.Globals.earthquaketime;
import static ru.m210projects.Redneck.Globals.everyothertime;
import static ru.m210projects.Redneck.Globals.gVisibility;
import static ru.m210projects.Redneck.Globals.global_random;
import static ru.m210projects.Redneck.Globals.kGameCrash;
import static ru.m210projects.Redneck.Globals.lockclock;
import static ru.m210projects.Redneck.Globals.mFakeMultiplayer;
import static ru.m210projects.Redneck.Globals.musiclevel;
import static ru.m210projects.Redneck.Globals.musicvolume;
import static ru.m210projects.Redneck.Globals.nFakePlayers;
import static ru.m210projects.Redneck.Globals.nMaxEpisodes;
import static ru.m210projects.Redneck.Globals.pNetInfo;
import static ru.m210projects.Redneck.Globals.ps;
import static ru.m210projects.Redneck.Globals.screenpeek;
import static ru.m210projects.Redneck.Globals.sync;
import static ru.m210projects.Redneck.Globals.uGameFlags;
import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.LoadSave.gAutosaveRequest;
import static ru.m210projects.Redneck.LoadSave.gQuickSaving;
import static ru.m210projects.Redneck.LoadSave.lastload;
import static ru.m210projects.Redneck.LoadSave.quickload;
import static ru.m210projects.Redneck.LoadSave.quicksave;
import static ru.m210projects.Redneck.LoadSave.quickslot;
import static ru.m210projects.Redneck.LoadSave.savegame;
import static ru.m210projects.Redneck.Main.cfg;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Main.gDemoScreen;
import static ru.m210projects.Redneck.Main.gEndScreen;
import static ru.m210projects.Redneck.Main.gGameScreen;
import static ru.m210projects.Redneck.Main.gLoadingScreen;
import static ru.m210projects.Redneck.Main.gPrecacheScreen;
import static ru.m210projects.Redneck.Main.gStatisticScreen;
import static ru.m210projects.Redneck.Main.mUserFlag;
import static ru.m210projects.Redneck.Names.DYNAMITE;
import static ru.m210projects.Redneck.Names.HURTRAIL;
import static ru.m210projects.Redneck.Player.checkavailinven;
import static ru.m210projects.Redneck.Player.processinput;
import static ru.m210projects.Redneck.Player.quickkill;
import static ru.m210projects.Redneck.Player.setpal;
import static ru.m210projects.Redneck.Premap.checknextlevel;
import static ru.m210projects.Redneck.Premap.clearfrags;
import static ru.m210projects.Redneck.Premap.numtorcheffects;
import static ru.m210projects.Redneck.Premap.prelevel;
import static ru.m210projects.Redneck.Premap.resetinventory;
import static ru.m210projects.Redneck.Premap.resetpspritevars;
import static ru.m210projects.Redneck.Premap.resetweapons;
import static ru.m210projects.Redneck.Premap.rorcnt;
import static ru.m210projects.Redneck.Premap.rorsector;
import static ru.m210projects.Redneck.Premap.rortype;
import static ru.m210projects.Redneck.ResourceHandler.checkEpisodeResources;
import static ru.m210projects.Redneck.ResourceHandler.resetEpisodeResources;
import static ru.m210projects.Redneck.Screen.changepalette;
import static ru.m210projects.Redneck.Screen.palto;
import static ru.m210projects.Redneck.Screen.vscrn;
import static ru.m210projects.Redneck.Sector.allignwarpelevators;
import static ru.m210projects.Redneck.Sector.animatewalls;
import static ru.m210projects.Redneck.Sector.checksectors;
import static ru.m210projects.Redneck.Sector.torchesprocess;
import static ru.m210projects.Redneck.SoundDefs.DUKE_TAKEPILLS;
import static ru.m210projects.Redneck.SoundDefs.DUKE_USEMEDKIT;
import static ru.m210projects.Redneck.SoundDefs.GENERIC_AMBIENCE17;
import static ru.m210projects.Redneck.SoundDefs.THUD;
import static ru.m210projects.Redneck.Sounds.clearsoundlocks;
import static ru.m210projects.Redneck.Sounds.currMusic;
import static ru.m210projects.Redneck.Sounds.pan3dsound;
import static ru.m210projects.Redneck.Sounds.sndPlayMusic;
import static ru.m210projects.Redneck.Sounds.sndStopMusic;
import static ru.m210projects.Redneck.Sounds.sound;
import static ru.m210projects.Redneck.Sounds.spritesound;
import static ru.m210projects.Redneck.Types.RTS.rtsplaying;
import static ru.m210projects.Redneck.View.FTA;
import static ru.m210projects.Redneck.View.MAXUSERQUOTES;
import static ru.m210projects.Redneck.View.adduserquote;
import static ru.m210projects.Redneck.View.cameraclock;
import static ru.m210projects.Redneck.View.cameradist;
import static ru.m210projects.Redneck.View.displayrest;
import static ru.m210projects.Redneck.View.displayrooms;
import static ru.m210projects.Redneck.View.fta;
import static ru.m210projects.Redneck.View.ftq;
import static ru.m210projects.Redneck.View.gNameShowTime;
import static ru.m210projects.Redneck.View.lastvisinc;
import static ru.m210projects.Redneck.View.loogiex;
import static ru.m210projects.Redneck.View.loogiey;
import static ru.m210projects.Redneck.View.over_shoulder_on;
import static ru.m210projects.Redneck.View.quotebot;
import static ru.m210projects.Redneck.View.quotebotgoal;
import static ru.m210projects.Redneck.View.user_quote_time;
import static ru.m210projects.Redneck.View.zoom;
import static ru.m210projects.Redneck.Weapons.addweapon;
import static ru.m210projects.Redneck.Weapons.moveweapons;

import java.io.FileNotFoundException;
import java.util.Arrays;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;
import ru.m210projects.Build.Render.GLRenderer;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Build.Types.BuildPos;
import ru.m210projects.Build.Types.InvalidVersionException;
import ru.m210projects.Redneck.Config.RRKeys;
import ru.m210projects.Redneck.Input;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Main.UserFlag;
import ru.m210projects.Redneck.Sounds;
import ru.m210projects.Redneck.Factory.RRMenuHandler;
import ru.m210projects.Redneck.Types.DemoFile;
import ru.m210projects.Redneck.Types.GameInfo;
import ru.m210projects.Redneck.Types.PlayerStruct;

public class GameScreen extends GameAdapter {

	private int nonsharedtimer;

	private Main game;
	public GameScreen(Main game) {
		super(game, gLoadingScreen);
		this.game = game;
		for(int i = 0; i < MAXPLAYERS; i++)
	    	sync[i] = new Input();
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
	public void ProcessFrame(BuildNet net) {
		ud.camerasprite = -1;
	    everyothertime++;

	    for(int i=connecthead;i>=0;i=connectpoint2[i])
	    	sync[i].Copy(net.gFifoInput[net.gNetFifoTail & 0xFF][i]);
	    net.gNetFifoTail++;

	    int j = -1;
	    for(int i=connecthead;i>=0;i=connectpoint2[i])
	    {
	    	cheatkeys(i);
	          if (GameScreen.this == gDemoScreen || (sync[i].bits&(1<<26)) == 0) { j = i; continue; }

	          if(ud.rec != null)
	        	  ud.rec.close();

	          if (i == myconnectindex)
	        	  game.gExit = true;
	          if (screenpeek == i)
	          {
	                screenpeek = connectpoint2[i];
	                if (screenpeek < 0) screenpeek = connecthead;
	          }

	          if (i == connecthead) connecthead = connectpoint2[connecthead];
	          else connectpoint2[j] = connectpoint2[i];

	          numplayers--;
	          ud.multimode--;

	          if (numplayers < 2)
	              sound(GENERIC_AMBIENCE17);

	          quickkill(ps[i]);
	          engine.deletesprite(ps[i].i);

	          buildString(buf, 0, ud.user_name[i], " is history!");
	          adduserquote(buf);

	          vscrn(ud.screen_size);

	          if(j < 0)
	          {
	        	  game.show();
	        	  Console.Println( " \nThe 'MASTER/First player' just quit the game.  All\nplayers are returned from the game.");
	        	  return;
	          }
	    }

	    net.CalcChecksum();

	    lockclock += TICSPERFRAME;
	    if( game.gPaused || ud.recstat != 2 && ud.multimode < 2 && GameScreen.this != gDemoScreen && (game.menu.gShowMenu || Console.IsShown()))
	    	return;

	    if(ud.recstat == 1 && ud.rec != null) ud.rec.record();

	    if(earthquaketime > 0) earthquaketime--;
	    if(rtsplaying > 0) rtsplaying--;

	    for(int i=0;i < MAXUSERQUOTES;i++)
	    	if (user_quote_time[i] != 0)
	    		user_quote_time[i]--;

	    if ((klabs(quotebotgoal-quotebot) <= 16) && (ud.screen_size <= 3))
	         quotebot += ksgn(quotebotgoal-quotebot);
	    else quotebot = quotebotgoal;

	    if(fta > 0)
	    {
	        fta--;
	        if(fta == 0) ftq = 0;
	    }

	    if ( ps[screenpeek].fogtype == 0 ) {
		    if (totalclock < lastvisinc)
		    {
		        if (klabs(gVisibility-currentGame.getCON().const_visibility) > 8)
		        	gVisibility += (currentGame.getCON().const_visibility-gVisibility)>>2;
		    }
		    else gVisibility = currentGame.getCON().const_visibility;
	    }

	    global_random = (short) engine.krand();
	    movedummyplayers();//ST 13

	    for(int i=connecthead;i>=0;i=connectpoint2[i])
	    {
	    	ps[i].UpdatePlayerLoc();
	        processinput(i);
	        checksectors(i);
	    }

        movefta();//ST 2
        moveweapons();          //ST 5 (must be last)
        movetransports();       //ST 9

        moveplayers();          //ST 10
        movefallers();          //ST 12

        moveexplosions();       //ST 4

        moveactors();           //ST 1
        moveeffectors();        //ST 3
        movestandables();       //ST 6
        doanimations();
        movefx();               //ST 11

        if ( numtorcheffects != 0)
	    	torchesprocess();

        net.CorrectPrediction();

	    if( (everyothertime&1) == 0)
	    {
	        animatewalls();
	        movecyclers();
	        pan3dsound();
	    }

	    if((uGameFlags & MODE_EOL) == MODE_EOL)
	    {
	    	game.pNet.ready2send = false;
	    	if(GameScreen.this == gDemoScreen)
				return;

	    	if(ud.eog == 1)
	    	{
	    		ud.eog = 0;
	    		uGameFlags |= MODE_END;
	    		switch(ud.volume_number)
			    {
			   		case 0: gEndScreen.episode1(); break;
					case 1: gEndScreen.episode2(); break;
					default:
						BuildGdx.app.postRunnable(new Runnable() {
							@Override
							public void run() {
								game.show();
							}
						});
						break;
			    }
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
		displayrooms(screenpeek,(int) smooth);
	}

	@Override
	public void DrawHud(float smooth) {
		displayrest((int) smooth);

		if (game.net.bOutOfSync) {
			game.getFont(1).drawText(160, 20, toCharArray("Out of sync!"),  0, 12, TextAlign.Center, 2, false);

			switch (game.net.bOutOfSyncByte / 4) {
			case 0: // bseed
				game.getFont(1).drawText(160, 30, toCharArray("seed checksum error"), 0, 12, TextAlign.Center, 2, false);
				break;
			case 1: // player
				game.getFont(1).drawText(160, 30, toCharArray("player struct checksum error"), 0, 12, TextAlign.Center, 2, false);
				break;
			case 2: // sprite
				game.getFont(1).drawText(160, 30, toCharArray("player sprite checksum error"), 0, 12, TextAlign.Center, 2, false);
				break;
			}
		}
	}

	@Override
	public void KeyHandler() {
		RRMenuHandler menu = game.menu;
		if (menu.gShowMenu) {
			menu.mKeyHandler(game.pInput, BuildGdx.graphics.getDeltaTime());
			return;
		}

		if(Console.IsShown() || MODE_TYPE) return;

		BuildControls input = game.pInput;
		if (input.ctrlGetInputKey(GameKeys.Menu_Toggle, true))
			menu.mOpen(menu.mMenus[GAME], -1);

		if (input.ctrlGetInputKey(RRKeys.Show_Help, true))
			menu.mOpen(menu.mMenus[HELP], -1);

		if (input.ctrlGetInputKey(RRKeys.Show_Savemenu, true)) {
			if(numplayers > 1 || mFakeMultiplayer) return;
			if (sprite[ps[myconnectindex].i].extra > 0) {
				gGameScreen.capture(160, 100);
				menu.mOpen(menu.mMenus[SAVEGAME], -1);
			}
		}

		if (input.ctrlGetInputKey(RRKeys.Show_Loadmenu, true)) {
			if(numplayers > 1 || mFakeMultiplayer) return;
			menu.mOpen(menu.mMenus[LOADGAME], -1);
		}

		if ( input.ctrlGetInputKey(RRKeys.See_Chase_View, true) )
		{
			if( over_shoulder_on != 0 )
                over_shoulder_on = 0;
            else
            {
                over_shoulder_on = 1;
                cameradist = 0;
                cameraclock = totalclock;
            }
            FTA(109+over_shoulder_on,ps[myconnectindex]);
		}

		if( ud.overhead_on != 0)
		{
            int j = totalclock-nonsharedtimer; nonsharedtimer += j;
            if ( input.ctrlGetInputKey(GameKeys.Enlarge_Hud, false) )
                zoom += mulscale(j,Math.max(zoom,256), 6);
            if ( input.ctrlGetInputKey(GameKeys.Shrink_Hud, false) )
                zoom -= mulscale(j,Math.max(zoom,256), 6);

            if( (zoom > 2048) )
                zoom = 2048;
            if( (zoom < 48) )
            	zoom = 48;

            if( input.ctrlGetInputKey(RRKeys.Map_Follow_Mode, true) ) {
	   	    	 ud.scrollmode = !ud.scrollmode;

	   	    	 if(ud.scrollmode)
	   	         {
	   	             ud.folx = ps[myconnectindex].oposx;
	   	             ud.foly = ps[myconnectindex].oposy;
	   	             ud.fola = (int) ps[myconnectindex].oang;
	   	         }
	   	    	 FTA(83+(ud.scrollmode?1:0),ps[myconnectindex]);
            }
		} else {
			 if ( input.ctrlGetInputKey(GameKeys.Enlarge_Hud, true) )
			 {
				 if(ud.screen_size > 0) {
					 sound(THUD);
					 ud.screen_size--;
					 if(ud.screen_size < 0) ud.screen_size = 0;
					 vscrn(ud.screen_size);
				 }
			 }
			 if ( input.ctrlGetInputKey(GameKeys.Shrink_Hud, true) )
			 {
				 if(ud.screen_size < 4) {
					 sound(THUD);
					 ud.screen_size++;
					 if(ud.screen_size > 5) ud.screen_size = 5;
					 vscrn(ud.screen_size);
				 }
			 }
		}

		if( input.ctrlGetInputKey(GameKeys.Map_Toggle, true) )
	    {
	        if( ud.last_overhead != ud.overhead_on && ud.last_overhead != 0)
	        {
	            ud.overhead_on = ud.last_overhead;
	            ud.last_overhead = 0;
	        }
	        else
	        {
	            ud.overhead_on++;
	            if(ud.overhead_on == 3 ) ud.overhead_on = 0;
	            ud.last_overhead = ud.overhead_on;
	        }
	    }

		if( input.ctrlGetInputKey(RRKeys.AutoRun, true)  )
	    {
	        ud.auto_run ^= 1;
	        FTA(85+ud.auto_run, ps[myconnectindex]);
	    }

		if( input.ctrlGetInputKey(RRKeys.Toggle_Crosshair, true)  )
	    {
			ud.crosshair ^= 1;
	        FTA(21-ud.crosshair,ps[myconnectindex]);
	    }

		if( input.ctrlGetInputKey(RRKeys.Show_Opp_Weapon, true)  )
	    {
			ud.showweapons ^= 1;
	        FTA(82-ud.showweapons,ps[myconnectindex]);
	    }

		if (input.ctrlGetInputKey(RRKeys.Show_Sounds, true))
			menu.mOpen(menu.mMenus[SOUNDSET], -1);

		if (input.ctrlGetInputKey(RRKeys.Show_Options, true))
			menu.mOpen(menu.mMenus[OPTIONS], -1);

		if (input.ctrlGetInputKey(RRKeys.Gamma, true))
			openGamma(menu);

		if (input.ctrlGetInputKey(RRKeys.Quicksave, true)) { // quick save
			quicksave();
		}

		if (input.ctrlGetInputKey(RRKeys.Messages, true)) {
			ud.fta_on ^= 1;
			if(ud.fta_on != 0) FTA(23,ps[myconnectindex]);
			else
			{
				ud.fta_on = 1;
				FTA(24,ps[myconnectindex]);
				ud.fta_on = 0;
			}
		}

		if(input.ctrlGetInputKey(GameKeys.Send_Message, false))
    	{
			MODE_TYPE = true;
        	getInput().initMessageInput(null);
    	}

		if (input.ctrlGetInputKey(RRKeys.Quickload, true)) { // quick load
			quickload();
		}

		if(input.ctrlGetInputKey(RRKeys.See_Coop_View, true))
		{
			if(ud.coop == 1 || mFakeMultiplayer)
			{
				screenpeek = connectpoint2[screenpeek];
				if (screenpeek < 0) screenpeek = connecthead;

				changepalette = 1; //if player has other palette
			}
		}

		if (input.ctrlGetInputKey(RRKeys.Quit, true))
			menu.mOpen(menu.mMenus[QUIT], -1);

		if (input.ctrlGetInputKey(RRKeys.Screenshot, true))
			makeScreenshot();
	}

	protected void openGamma(RRMenuHandler menu)
	{
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

	    if( GameScreen.this != gDemoScreen && ud.recstat == 2)
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

	    Arrays.fill(gotpic, (byte)0);
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

	    if(ud.recstat != 2) {
	    	sndStopMusic();
	    }

	    if(ud.recstat != 2)
	    {
	    	musicvolume = ud.volume_number;
	    	musiclevel = ud.level_number;
	    	sndPlayMusic(currentGame.getCON().music_fn[musicvolume][musiclevel]);
	    }

	    if( ud.recstat == 1 )
	    	ud.rec = new DemoFile(GDXBYTEVERSION);

	    for(int i=connecthead;i>=0;i=connectpoint2[i])
	    {
	    	if(sprite[ps[i].i].sectnum == 1024) continue;
	        switch(sector[sprite[ps[i].i].sectnum].floorpicnum)
	        {
	            case HURTRAIL:
	                resetweapons(i);
	                resetinventory(i);
	                ps[i].gotweapon[PISTOL_WEAPON] = false;
	                ps[i].ammo_amount[PISTOL_WEAPON] = 0;
	                ps[i].curr_weapon = KNEE_WEAPON;
	                ps[i].kickback_pic = 0;
	                break;
	        }

	        if ( (currentGame.getCON().type == RRRA && ud.level_number == 2 && ud.volume_number == 0)
	        		|| (currentGame.getCON().type != RRRA && ud.level_number == 1 && ud.volume_number == 1))
		    {
	        	resetweapons(i);
	        	ps[i].gotweapon[PISTOL_WEAPON] = false;
	        	ps[i].ammo_amount[PISTOL_WEAPON] = 0;
	        	ps[i].curr_weapon = KNEE_WEAPON;
	        	ps[i].kickback_pic = 0;
		    }
	    }

	      //PREMAP.C - replace near the my's at the end of the file

	     ps[myconnectindex].palette = palette;

	     setpal(ps[myconnectindex]);

	     everyothertime = 0;
	     global_random = 0;

	     ud.last_level = ud.level_number+1;

	     changepalette = 1;

	     game.net.WaitForAllPlayers(0);
	     engine.sampletimer();

	     palto(0,0,0,0);
	     if(!game.menu.gShowMenu)
	    	 vscrn(ud.screen_size);
	     engine.clearview(0);

	     over_shoulder_on = 0;

	     Arrays.fill(user_quote_time, (short) 0);

	     game.net.predict.reset();
	     clearfrags();

	     GLRenderer gl = engine.glrender();
	     if(gl != null) gl.preload();
	     System.err.println("New level " + map);

	     if((uGameFlags & MODE_EOL) == MODE_EOL && game.nNetMode == NetMode.Single)
	    	 gAutosaveRequest = true;

	     uGameFlags &= ~(MODE_EOL | MODE_END);

	     return !kGameCrash;
	}

	protected void makeScreenshot()
	{
		String name = "scrxxxx.png";
		FileEntry map;
		if(mUserFlag == UserFlag.UserMap && (map = BuildGdx.compat.checkFile(boardfilename)) != null)
			name = "scr-" + map.getName() + "-xxxx.png";
		if(mUserFlag != UserFlag.UserMap && currentGame != null)
			name = "scr-e" + (ud.volume_number+1) + "m" + (ud.level_number+1) + "[" + currentGame.getFile().getName() + "]-xxxx.png";

		String filename = pEngine.screencapture(name);
		if(filename != null)
			buildString(currentGame.getCON().fta_quotes[103], 0, filename, " saved");
		else buildString(currentGame.getCON().fta_quotes[103], 0, "Screenshot not saved. Access denied!");
		FTA(103,ps[myconnectindex]);
	}

	/**
	 * @param item should be GameInfo or FileEntry (map)
	 */
	public void newgame(final boolean isMultiplayer, final Object item, final int nEpisode, final int nLevel, final int nDifficulty)
	{
		if (ud.recstat == 1 && ud.rec != null)
			ud.rec.close();

		pNet.ready2send = false;
		game.changeScreen(load); //checkEpisodeResources is slow, so we make other loading screens
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
					if(mFakeMultiplayer) {
						ud.multimode = nFakePlayers;
						connecthead = 0;
	        	       	for(short i=0;i<MAXPLAYERS;i++)
	        	       		connectpoint2[i] = (short) (i+1);
	        	       	connectpoint2[ud.multimode-1] = -1;
					}
					else ud.multimode = numplayers;

					if(GameScreen.this != gDemoScreen) {
						ud.coop = pNetInfo.nGameType;
						ud.monsters_off = pNetInfo.nMonsters == 1;
						ud.respawn_monsters = false;
						ud.respawn_inventory = true;
						if(ud.coop == 0)
							ud.respawn_items = true;
						else ud.respawn_items = false;
		                ud.marker = pNetInfo.nMarkers;
		                ud.ffire = pNetInfo.nFriendlyFire;
					}

	                ud.god = false;
					game.nNetMode = NetMode.Multiplayer;

					for(int c=connecthead;c>=0;c=connectpoint2[c])
	                {
	                    resetweapons(c);
	                    resetinventory(c);
					}
				}

				UserFlag flag = UserFlag.None;
				if(item instanceof GameInfo && !item.equals(defGame)) {
					flag = UserFlag.Addon;
					GameInfo game = (GameInfo)item;
					checkEpisodeResources(game);
					Console.Println("Start user episode: " + game.Title);
				} else resetEpisodeResources();

				if(item instanceof FileEntry) {
					flag = UserFlag.UserMap;
					boardfilename = ((FileEntry)item).getPath();
					ud.level_number = 3;
					ud.volume_number = 2;
					Console.Println("Start user map: " + ((FileEntry)item).getName());
				}
				mUserFlag = flag;

				if(GameScreen.this != gDemoScreen) {
					if(currentGame.getCON().type != RRRA)
						ud.player_skill = nDifficulty + 1;
					else ud.player_skill = nDifficulty;

					if(!isMultiplayer) {
						Source skillvoice = null;
						switch(nDifficulty) {
							case 0: skillvoice = sound(427);break;
				            case 1: skillvoice = sound(428);break;
				            case 2: skillvoice = sound(196);break;
				            case 3: skillvoice = sound(195);break;
				            case 4: skillvoice = sound(197);break;
						}

						while(skillvoice != null && skillvoice.isActive());
					}

					if(mUserFlag != UserFlag.UserMap) {
						ud.level_number = nLevel;
				        ud.volume_number = nEpisode;
					}

					if(isPsychoSkill())
	                	ud.respawn_monsters = true;
	                else ud.respawn_monsters = false;
				}

                uGameFlags = 0;
        	    ud.secretlevel = 0;
        	    ud.from_bonus = 0;
        	    parallaxyscale = 0;
        	    ud.last_level = -1;
        	    lastload = null;

        	    if(!isMultiplayer) {
        	    	PlayerStruct p = ps[myconnectindex];
        	    	if(ud.coop != 1)
	        	    {
	        	        p.curr_weapon = PISTOL_WEAPON;
	        	        p.gotweapon[PISTOL_WEAPON] = true;
	        	        p.gotweapon[KNEE_WEAPON] = true;
	        	        p.ammo_amount[PISTOL_WEAPON] = 48;
	        	        p.gotweapon[HANDREMOTE_WEAPON] = true;
	        	        if(currentGame.getCON().type == RRRA)
	        		        p.gotweapon[RATE_WEAPON] = true;
	        	        p.last_weapon = -1;
	        	    }
	        	    p.last_used_weapon = 0;
        	    }

        	    display_mirror = 0;
        	    zoom = 768;

				enterlevel(getTitle());
			}
		});
	}

	@Override
	protected void startboard(Runnable startboard)
	{
		gPrecacheScreen.init(false, startboard);
		game.changeScreen(gPrecacheScreen);
	}

	public boolean enterlevel(String title)
	{
		if(title == null) return false;
		String map;
		if(mUserFlag == UserFlag.UserMap)
			map = boardfilename;
		else map = currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number].path;

		if(GameScreen.this != gDemoScreen)
			ud.recstat = ud.m_recstat;

		loadboard(map, null).setTitle(title);
		return true;
	}

	public String getTitle()
	{
		String title = null;
		if (mUserFlag != UserFlag.UserMap) {
			if(ud.volume_number < nMaxEpisodes && currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number] != null)
				title = currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number].title;
			else {
				game.GameCrash("MapInfo not found! Episode: " + ud.volume_number + " Level: " + ud.level_number + " nMaps: " + (ud.volume_number < nMaxEpisodes ? currentGame.episodes[ud.volume_number].nMaps : 0));
				return null;
			}
		}
		else {
			FileEntry file = BuildGdx.compat.checkFile(boardfilename);
			if(file != null)
				title = file.getName();
			else {
				game.GameCrash("Map " + boardfilename + " not found!");
				return null;
			}
		}
		return title;
	}

	public void cheatkeys(int snum)
	{
		int i, k;
	    short dainv;
	    int sb_snum, j;

	    sb_snum = sync[snum].bits;
	    PlayerStruct p = ps[snum];

	    if(p.cheat_phase == 1) return;

	    i = p.aim_mode;
	    p.aim_mode = (sb_snum>>23)&1;
	    if(p.aim_mode < i && (game.nNetMode != NetMode.Single || !pMenu.gShowMenu))
	        p.return_to_center = 9;

	    if((sb_snum & 1 << 22) != 0 && p.last_pissed_time == 0 && sprite[p.i].extra > 0)
	    {
	    	p.last_pissed_time = 4000;
	    	if(ud.lockout == 0)
	    		spritesound(437, p.i);

	    	if ( sprite[p.i].extra > currentGame.getCON().max_player_health - currentGame.getCON().max_player_health / 10 )
	  	   	{
	  	        if ( sprite[p.i].extra < currentGame.getCON().max_player_health )
	  	          sprite[p.i].extra = (short) currentGame.getCON().max_player_health;
	  	    }
	  	    else
	  	    {
	  	        sprite[p.i].extra += 2;
	  	        p.last_extra = sprite[p.i].extra;
	  	    }
	    }

	    if( (sb_snum&((15<<8)|(1<<12)|(1<<15)|(1<<16)|(1<<22)|  (1<<19)| (1<<20)|(1<<21)|(1<<24)|(1<<25)|(1<<27)|(1<<28)|(1<<29)|(1<<30)|(1<<31))) == 0 )
	        p.interface_toggle_flag = 0;
	    else if(p.interface_toggle_flag == 0 && ( sb_snum&(1<<17) ) == 0)
	    {
	        p.interface_toggle_flag = 1;

	        if( (sb_snum&(1<<21)) != 0)
	        {
	        	game.gPaused = !game.gPaused;
//	            if( game.gPaused && (sb_snum&(1<<5)) != 0 ) ud.pause_on = 2;
	            if(game.gPaused)
	            {
	            	if(currMusic != null)
	            		currMusic.pause();
	                BuildGdx.audio.getSound().stopAllSounds();
	                clearsoundlocks();
	            }
	            else
	            {
	                if(!cfg.muteMusic && currMusic != null)
	                	currMusic.resume();
	            }
	        }

	        if(game.gPaused) return;

	        if(sprite[p.i].extra <= 0) return;

	        if( (sb_snum&(1<<30)) != 0 && p.newowner == -1 )
	        {
	            switch(p.inven_icon)
	            {
	                case 4: sb_snum |= (1<<25);break;
	                case 3: sb_snum |= (1<<24);break;
	                case 5: sb_snum |= (1<<15);break;
	                case 1: sb_snum |= (1<<16);break;
	                case 2: sb_snum |= (1<<12);break;
	            }
	        }

	        if( (sb_snum&(1<<12)) != 0 )
	        {
	            if(p.moonshine_amount == 400 )
	            {
	                p.moonshine_amount = 399;
	                spritesound(DUKE_TAKEPILLS,p.i);
	                p.inven_icon = 2;
	                FTA(12,p);
	            }
	            return;
	        }

	        if(p.newowner == -1)
	            if( (sb_snum&(1<<20)) != 0 || (sb_snum&(1<<27)) != 0 || p.refresh_inventory)
	        {
	            p.invdisptime = 26*2;

	            if( (sb_snum&(1<<27)) != 0) k = 1;
	            else k = 0;

	            if(p.refresh_inventory) p.refresh_inventory = false;
	            dainv = (short) p.inven_icon;

	            i = 0;

	            boolean CHECKINV;
	            do
	            {
	            	CHECKINV = false;
		            if(i < 9)
		            {
		                i++;

		                switch(dainv)
		                {
		                    case 4:
		                        if(p.cowpie_amount > 0 && i > 1)
		                            break;
		                        if(k != 0) dainv = 5;
		                        else dainv = 3;
		                        CHECKINV = true;
		                        break;
		                    case 6:
		                        if(p.snorkle_amount > 0 && i > 1)
		                            break;
		                        if(k != 0) dainv = 7;
		                        else dainv = 5;
		                        CHECKINV = true;
		                        break;
		                    case 2:
		                        if(p.moonshine_amount > 0 && i > 1)
		                            break;
		                        if(k != 0) dainv = 3;
		                        else dainv = 1;
		                        CHECKINV = true;
		                        break;
		                    case 3:
		                        if(p.beer_amount > 0 && i > 1)
		                            break;
		                        if(k != 0) dainv = 4;
		                        else dainv = 2;
		                        CHECKINV = true;
		                        break;
		                    case 0:
		                    case 1:
		                        if(p.whishkey_amount > 0 && i > 1)
		                            break;
		                        if(k != 0) dainv = 2;
		                        else dainv = 7;
		                        CHECKINV = true;
		                        break;
		                    case 5:
		                        if(p.yeehaa_amount > 0 && i > 1)
		                            break;
		                        if(k != 0) dainv = 6;
		                        else dainv = 4;
		                        CHECKINV = true;
		                        break;
		                    case 7:
		                        if(p.boot_amount > 0 && i > 1)
		                            break;
		                        if(k != 0) dainv = 1;
		                        else dainv = 6;
		                        CHECKINV = true;
		                        break;
		                }
		            }
		            else dainv = 0;
		            p.inven_icon = dainv;
	            } while(CHECKINV);

	            switch(dainv)
	            {
	                case 1: FTA(3,p);break;
	                case 2: FTA(90,p);break;
	                case 3: FTA(91,p);break;
	                case 4: FTA(88,p);break;
	                case 5: FTA(88,p);break;
	                case 6: FTA(89,p);break;
	                case 7: FTA(6,p);break;
	            }
	        }

	        j = ( (sb_snum&(15<<8))>>8 ) - 1;

//	        if( j != 1 && p.kickback_pic > 0)
//	            p.wantweaponfire = (short) j; //GDX 23.03.2020 Disable random weapon switch

	        if(p.last_pissed_time <= (26*218)
	        		&& p.show_empty_weapon == 0
	        		&& p.kickback_pic == 0
	        		&& p.quick_kick == 0 && sprite[p.i].xrepeat > 8 && p.access_incs == 0 && p.knee_incs == 0 )
	        {
	            if(!IsOriginalGame() || ( p.weapon_pos == 0 || ( p.holster_weapon != 0 && p.weapon_pos == -9 ) ) ) //quick weapon switch
	            {
	            	if(j == 12) //last used weapon
	            	{
	            		j = p.curr_weapon;
	            		if(p.last_used_weapon == 0 || p.last_used_weapon == 15)
	            			j = p.last_used_weapon;
	            		else if( p.gotweapon[p.last_used_weapon] && p.ammo_amount[p.last_used_weapon] > 0 )
                            j = p.last_used_weapon;
	            	}

	                if(j == 10 || j == 11) //next prev weapon
	                {
	                    k = p.curr_weapon;
	                    switch ( k )
	                    {
	                    case CHICKENBOW_WEAPON:
	                    	k = CROSSBOW_WEAPON;
	                    	break;
	                    case BUZSAW_WEAPON:
	                        k = THROWSAW_WEAPON;
	                        break;
	                    case RATE_WEAPON:
	                        k = KNEE_WEAPON;
	                        break;
	                    }

	                    j = ( j == 10 ? -1 : 1 );
	                    i = 0;

	                    while( ( k >= 0 && k < 10 ) /*|| ( k == BUZSAW_WEAPON && (p.subweapon&(1<<BUZSAW_WEAPON) ) != 0 )*/ )
	                    {
	                    	k += j;

	                        if(k == -1) k = 9;
	                        else if(k == 10) k = 0;

	                        if( p.gotweapon[k] && p.ammo_amount[k] > 0 )
	                        {
	                            j = k;
	                            break;
	                        }

	                        i++;
	                        if(i == 10)
	                        {
	                            addweapon( p, KNEE_WEAPON );
	                            break;
	                        }
	                    }
	                }

	                k = -1;

	                if( j == DYNAMITE_WEAPON && p.ammo_amount[DYNAMITE_WEAPON] == 0 )
	                {
	                	k = headspritestat[1];
	                    while(k >= 0)
	                    {
	                        if( sprite[k].picnum == DYNAMITE && sprite[k].owner == p.i )
	                        {
	                            p.gotweapon[DYNAMITE_WEAPON] = true;
	                            j = HANDREMOTE_WEAPON;
	                            break;
	                        }
	                        k = nextspritestat[k];
	                    }
	                }

	                if(currentGame.getCON().type == RRRA && j == CROSSBOW_WEAPON)
	                {
	                    if( p.curr_weapon != CROSSBOW_WEAPON && p.ammo_amount[CROSSBOW_WEAPON] != 0)
	                    {
                            if( (p.subweapon&4) != 0 || p.ammo_amount[CHICKENBOW_WEAPON] == 0 )
                            {
                                j = CROSSBOW_WEAPON;
                                p.subweapon = 0;
                            }
	                    }
	                    else
	                    {
	                        p.subweapon = 4;
	                        j = CHICKENBOW_WEAPON;
	                    }
	                }

	                if(j == THROWSAW_WEAPON)
	                {
	                    if( p.curr_weapon != THROWSAW_WEAPON && p.ammo_amount[THROWSAW_WEAPON] != 0) //v0.751
	                    {
                            if( (p.subweapon&(1<<BUZSAW_WEAPON)) != 0 || p.ammo_amount[BUZSAW_WEAPON] == 0 )
                            {
                                j = THROWSAW_WEAPON;
                                p.subweapon = 0;
                            }
	                    }
	                    else
	                    {
	                        p.subweapon = (1<<BUZSAW_WEAPON);
	                        j = BUZSAW_WEAPON;
	                    }
	                }

	                if(j == POWDERKEG_WEAPON)
	                {

	                	if ( p.curr_weapon != POWDERKEG_WEAPON && p.ammo_amount[POWDERKEG_WEAPON] != 0 )
	                	{
	                		if ( (p.subweapon&(1<<BOWLING_WEAPON)) != 0 || p.ammo_amount[BOWLING_WEAPON] == 0 )
	                		{
	                			j = POWDERKEG_WEAPON;
	                        	p.subweapon = 0;
	                		}
	                    }
	                    else
	                    {
	                    	j = BOWLING_WEAPON;
	                    	p.subweapon = (1<<BOWLING_WEAPON);
	                    }
	                }

	                if(currentGame.getCON().type == RRRA && j == KNEE_WEAPON)
	                {

	                	if ( p.curr_weapon != KNEE_WEAPON )
	                	{
	                		if ( (p.subweapon & 2) != 0 )
	                		{
	                			j = KNEE_WEAPON;
	                        	p.subweapon = 0;
	                		}
	                    }
	                    else
	                    {
	                    	j = RATE_WEAPON;
	                    	p.subweapon = 2;
	                    }
	                }


	                if(p.holster_weapon != 0)
	                {
	                    sb_snum |= 1<<19;
	                    p.weapon_pos = -9;
	                }
	                else if( j >= 0  && j < MAX_WEAPONSRA && p.gotweapon[j] && p.curr_weapon != j ) switch(j)
	                {
	                    case KNEE_WEAPON:
	                        addweapon( p, KNEE_WEAPON );
	                        break;
	                    case PISTOL_WEAPON:
	                        if ( p.ammo_amount[PISTOL_WEAPON] == 0 )
	                            if(p.show_empty_weapon == 0)
	                        {
	                            p.last_full_weapon = p.curr_weapon;
	                            p.show_empty_weapon = 32;
	                        }
	                        addweapon( p, PISTOL_WEAPON );
	                        break;
	                    case SHOTGUN_WEAPON:
	                        if( p.ammo_amount[SHOTGUN_WEAPON] == 0 && p.show_empty_weapon == 0)
	                        {
	                            p.last_full_weapon = p.curr_weapon;
	                            p.show_empty_weapon = 32;
	                        }
	                        addweapon( p, SHOTGUN_WEAPON);
	                        break;
	                    case RIFLEGUN_WEAPON:
	                        if( p.ammo_amount[RIFLEGUN_WEAPON] == 0 && p.show_empty_weapon == 0)
	                        {
	                            p.last_full_weapon = p.curr_weapon;
	                            p.show_empty_weapon = 32;
	                        }
	                        addweapon( p, RIFLEGUN_WEAPON);
	                        break;
	                    case DYNAMITE_WEAPON:
	                        if( p.ammo_amount[DYNAMITE_WEAPON] == 0 )
	                            if(p.show_empty_weapon == 0)
	                        {
	                            p.last_full_weapon = p.curr_weapon;
	                            p.show_empty_weapon = 32;
	                        }
	                        addweapon( p, DYNAMITE_WEAPON );
	                        break;
	                    case ALIENBLASTER_WEAPON:
	                        if( p.ammo_amount[ALIENBLASTER_WEAPON] == 0 && p.show_empty_weapon == 0 )
	                        {
	                            p.last_full_weapon = p.curr_weapon;
	                            p.show_empty_weapon = 32;
	                        }
	                        addweapon( p, ALIENBLASTER_WEAPON );
	                        break;
	                    case TIT_WEAPON:
	                        if( p.ammo_amount[TIT_WEAPON] == 0 && p.show_empty_weapon == 0)
	                        {
	                            p.last_full_weapon = p.curr_weapon;
	                            p.show_empty_weapon = 32;
	                        }
	                        addweapon( p, TIT_WEAPON );
	                        break;
	                    case BUZSAW_WEAPON:
	                    case THROWSAW_WEAPON:
	                        if( p.ammo_amount[j] == 0 && p.show_empty_weapon == 0)
	                        {
	                            p.show_empty_weapon = 32;
	                            p.last_full_weapon = p.curr_weapon;
	                        }

	                        addweapon(p, j);
	                        break;
	                    case HANDREMOTE_WEAPON:
	                        if(k >= 0) // Found in list of [1]'s
	                        {
	                            p.curr_weapon = HANDREMOTE_WEAPON;
	                            p.last_weapon = -1;
	                            p.weapon_pos = 10;
	                        }
	                        break;
	                    case CROSSBOW_WEAPON:
	                        if( p.ammo_amount[CROSSBOW_WEAPON] > 0 && p.gotweapon[CROSSBOW_WEAPON] )
	                            addweapon( p, CROSSBOW_WEAPON );
	                        break;
	                    case POWDERKEG_WEAPON:
	                    case BOWLING_WEAPON:
	                    case CHICKENBOW_WEAPON:
	                    	if( p.ammo_amount[j] == 0 && p.show_empty_weapon == 0)
	                    	{
	                    		p.show_empty_weapon = 32;
	                    		p.last_full_weapon = p.curr_weapon;
	                    	}
	                    	addweapon( p, j );
	                        break;
	                    case MOTO_WEAPON:
	                    case BOAT_WEAPON:
	                    	if ( p.ammo_amount[j] == 0 && p.show_empty_weapon == 0 )
	                    		p.show_empty_weapon = 32;
	                    		addweapon(p, j);
	                    	break;
	                    case RATE_WEAPON:
	                        spritesound(496, p.i);
	                        addweapon(p, j);
	                        break;
	                }
	            }

	            if( (sb_snum&(1<<19)) != 0 )
	            {
	                if( p.curr_weapon > KNEE_WEAPON )
	                {
	                    if(p.holster_weapon == 0 && p.weapon_pos == 0)
	                    {
	                        p.holster_weapon = 1;
	                        p.weapon_pos = -1;
	                        FTA(73,p);
	                    }
	                    else if(p.holster_weapon == 1 && p.weapon_pos == -9)
	                    {
	                        p.holster_weapon = 0;
	                        p.weapon_pos = 10;
	                        FTA(74,p);
	                    }
	                }
	            }
	        }

	        if( (sb_snum&(1<<24)) != 0 && p.beer_amount > 0 && sprite[p.i].extra < currentGame.getCON().max_player_health )
	        {
	        	p.beer_amount -= 400;
	        	sprite[p.i].extra += 5;
	            p.inven_icon = 3;

	            if ( sprite[p.i].extra > currentGame.getCON().max_player_health )
	            	sprite[p.i].extra = (short) currentGame.getCON().max_player_health;
	            p.alcohol_amount += 5;
	            if ( p.beer_amount == 0 )
	                checkavailinven(p);
	            if ( p.alcohol_amount < 99 && Sound[425].num == 0)
	                  spritesound(425, p.i);
	        }

	        if( (sb_snum&(1<<15)) != 0 )
	        {
	        	if( p.newowner == -1 && p.field_count == 0 )
	        	{
	        		p.field_count = 126;
	        		spritesound(390, p.i);
	        		p.field_290 = 0x4000;
	        		sub_64EF0(snum);
	        		if ( sector[p.cursectnum].lotag == 857 )
	        	    {
	        			if(sprite[p.i].extra < currentGame.getCON().max_player_health)
	        			{
	        				sprite[p.i].extra += 10;
	        				if(sprite[p.i].extra > currentGame.getCON().max_player_health)
	                        	sprite[p.i].extra = (short) currentGame.getCON().max_player_health;
	        			}
	        	    }
	        	    else
	        	    {
	        	    	if(sprite[p.i].extra + 1 <= currentGame.getCON().max_player_health)
	        	    		sprite[p.i].extra++;
	        	    }
	        	}
	        }

	        if( (sb_snum&(1<<16)) != 0 )
	        {
	            if( p.whishkey_amount > 0 && sprite[p.i].extra < currentGame.getCON().max_player_health )
	            {
	                if(p.whishkey_amount > 10)
	                {
	                    p.whishkey_amount -= 10;
	                    sprite[p.i].extra += 10;
	                    p.inven_icon = 1;
	                }
	                else
	                {
	                    sprite[p.i].extra += p.whishkey_amount;
	                    p.whishkey_amount = 0;
	                    checkavailinven(p);
	                }
	                if(sprite[p.i].extra > currentGame.getCON().max_player_health)
                    	sprite[p.i].extra = (short) currentGame.getCON().max_player_health;

	                p.alcohol_amount += 10;
	                if ( p.alcohol_amount <= 100 && Sound[DUKE_USEMEDKIT].num == 0)
	                	 spritesound(DUKE_USEMEDKIT,p.i);
	            }
	        }

	        if( (sb_snum&(1<<25)) != 0)
	        {
	        	if ( p.cowpie_amount > 0 )
	            {
	        		if ( sprite[p.i].extra < currentGame.getCON().max_player_health )
	        		{
	        			if ( Sound[429].num == 0 )
	        				spritesound(429, p.i);
	        			p.cowpie_amount -= 100;
	        			if ( p.alcohol_amount > 0 )
	        			{
	        				p.alcohol_amount -= 5;
	        				if(p.alcohol_amount < 0)
	        					p.alcohol_amount = 0;
	        			}
	        			if ( p.gut_amount < 100 )
	        			{
	        				p.gut_amount += 5;
	        				if ( p.gut_amount > 100 )
	        					p.gut_amount = 100;
	        			}

	        			sprite[p.i].extra += 5;
	        			if(sprite[p.i].extra > currentGame.getCON().max_player_health)
	        				sprite[p.i].extra = (short) currentGame.getCON().max_player_health;
	        			p.inven_icon = 4;
	        			if(p.cowpie_amount <= 0)
	        				checkavailinven(p);
	        		}
	            }
	        }

	        if((sb_snum&(1<<28)) != 0 && p.one_eighty_count == 0)
	            p.one_eighty_count = -1024;
	    }
	}

	public boolean IsOriginalGame() {
		return ud.recstat == 1 && ud.rec != null && ud.rec.recversion <= BYTEVERSIONRR;
	}
}
