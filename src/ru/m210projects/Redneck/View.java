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

package ru.m210projects.Redneck;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static ru.m210projects.Build.Engine.CEIL;
import static ru.m210projects.Build.Engine.CLIPMASK1;
import static ru.m210projects.Build.Engine.FLOOR;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXSPRITESONSCREEN;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Engine.gotpic;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.mirrorang;
import static ru.m210projects.Build.Engine.mirrorx;
import static ru.m210projects.Build.Engine.mirrory;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.show2dsector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.spritesortcnt;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.tsprite;
import static ru.m210projects.Build.Engine.viewingrange;
import static ru.m210projects.Build.Engine.visibility;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.windowx1;
import static ru.m210projects.Build.Engine.windowx2;
import static ru.m210projects.Build.Engine.windowy1;
import static ru.m210projects.Build.Engine.windowy2;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Engine.yxaspect;
import static ru.m210projects.Build.Gameutils.BClampAngle;
import static ru.m210projects.Build.Gameutils.BCosAngle;
import static ru.m210projects.Build.Gameutils.BSinAngle;
import static ru.m210projects.Build.Gameutils.isValidSector;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Redneck.Actors.badguy;
import static ru.m210projects.Redneck.Actors.isPsychoSkill;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.HELP;
import static ru.m210projects.Redneck.Gamedef.getincangle;
import static ru.m210projects.Redneck.Globals.CHICKENBOW_WEAPON;
import static ru.m210projects.Redneck.Globals.CROSSBOW_WEAPON;
import static ru.m210projects.Redneck.Globals.GUTSMETTER;
import static ru.m210projects.Redneck.Globals.HANDREMOTE_WEAPON;
import static ru.m210projects.Redneck.Globals.KEYSIGN;
import static ru.m210projects.Redneck.Globals.KILLSSIGN;
import static ru.m210projects.Redneck.Globals.KNEE_WEAPON;
import static ru.m210projects.Redneck.Globals.MODE_TYPE;
import static ru.m210projects.Redneck.Globals.WIDEHUD_PART1;
import static ru.m210projects.Redneck.Globals.WIDEHUD_PART2;
import static ru.m210projects.Redneck.Globals.boardfilename;
import static ru.m210projects.Redneck.Globals.buf;
import static ru.m210projects.Redneck.Globals.currentGame;
import static ru.m210projects.Redneck.Globals.display_mirror;
import static ru.m210projects.Redneck.Globals.earthquaketime;
import static ru.m210projects.Redneck.Globals.gVisibility;
import static ru.m210projects.Redneck.Globals.global_random;
import static ru.m210projects.Redneck.Globals.hittype;
import static ru.m210projects.Redneck.Globals.kAngleMask;
import static ru.m210projects.Redneck.Globals.mFakeMultiplayer;
import static ru.m210projects.Redneck.Globals.mirrorcnt;
import static ru.m210projects.Redneck.Globals.mirrorsector;
import static ru.m210projects.Redneck.Globals.mirrorwall;
import static ru.m210projects.Redneck.Globals.nMaxEpisodes;
import static ru.m210projects.Redneck.Globals.ps;
import static ru.m210projects.Redneck.Globals.screenpeek;
import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.Globals.zofslope;
import static ru.m210projects.Redneck.LoadSave.lastload;
import static ru.m210projects.Redneck.Main.cfg;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Main.gGameScreen;
import static ru.m210projects.Redneck.Main.game;
import static ru.m210projects.Redneck.Main.mUserFlag;
import static ru.m210projects.Redneck.Names.AMMOBOX;
import static ru.m210projects.Redneck.Names.APLAYER;
import static ru.m210projects.Redneck.Names.ARROW;
import static ru.m210projects.Redneck.Names.BEER_ICON;
import static ru.m210projects.Redneck.Names.BIKERSTAND;
import static ru.m210projects.Redneck.Names.BILLYRAY;
import static ru.m210projects.Redneck.Names.BILLYRAYSTAYPUT;
import static ru.m210projects.Redneck.Names.BLOODPOOL;
import static ru.m210projects.Redneck.Names.BLOODSPLAT1;
import static ru.m210projects.Redneck.Names.BLOODSPLAT2;
import static ru.m210projects.Redneck.Names.BLOODSPLAT3;
import static ru.m210projects.Redneck.Names.BLOODSPLAT4;
import static ru.m210projects.Redneck.Names.BOOT_ICON;
import static ru.m210projects.Redneck.Names.BOTTOMSTATUSBAR;
import static ru.m210projects.Redneck.Names.BULLETHOLE;
import static ru.m210projects.Redneck.Names.BURNING;
import static ru.m210projects.Redneck.Names.CAMERA1;
import static ru.m210projects.Redneck.Names.CHIKENCROSSBOW;
import static ru.m210projects.Redneck.Names.CIRCLESAW;
import static ru.m210projects.Redneck.Names.COWPIE_ICON;
import static ru.m210projects.Redneck.Names.CRACK1;
import static ru.m210projects.Redneck.Names.CRACK2;
import static ru.m210projects.Redneck.Names.CRACK3;
import static ru.m210projects.Redneck.Names.CRACK4;
import static ru.m210projects.Redneck.Names.CROSSBOW;
import static ru.m210projects.Redneck.Names.CROSSHAIR;
import static ru.m210projects.Redneck.Names.DAISYMAE;
import static ru.m210projects.Redneck.Names.DOORKEY;
import static ru.m210projects.Redneck.Names.ECLAIRHEALTH;
import static ru.m210projects.Redneck.Names.EMPTY_ICON;
import static ru.m210projects.Redneck.Names.EXPLOSION2;
import static ru.m210projects.Redneck.Names.EXPLOSION3;
import static ru.m210projects.Redneck.Names.FEATHERS;
import static ru.m210projects.Redneck.Names.FIRE;
import static ru.m210projects.Redneck.Names.FIRELASER;
import static ru.m210projects.Redneck.Names.FLOORSLIME;
import static ru.m210projects.Redneck.Names.FOOTPRINTS;
import static ru.m210projects.Redneck.Names.FOOTPRINTS2;
import static ru.m210projects.Redneck.Names.FOOTPRINTS3;
import static ru.m210projects.Redneck.Names.FOOTPRINTS4;
import static ru.m210projects.Redneck.Names.FORCESPHERE;
import static ru.m210projects.Redneck.Names.FRAGBAR;
import static ru.m210projects.Redneck.Names.FRAMEEFFECT1;
import static ru.m210projects.Redneck.Names.HEALTHBOX;
import static ru.m210projects.Redneck.Names.INVENTORYBOX;
import static ru.m210projects.Redneck.Names.JIBS1;
import static ru.m210projects.Redneck.Names.JIBS2;
import static ru.m210projects.Redneck.Names.JIBS3;
import static ru.m210projects.Redneck.Names.JIBS4;
import static ru.m210projects.Redneck.Names.JIBS5;
import static ru.m210projects.Redneck.Names.JIBS6;
import static ru.m210projects.Redneck.Names.KILLSICON;
import static ru.m210projects.Redneck.Names.LNRDLYINGDEAD;
import static ru.m210projects.Redneck.Names.MINION;
import static ru.m210projects.Redneck.Names.MIRROR;
import static ru.m210projects.Redneck.Names.MOONSHINE_ICON;
import static ru.m210projects.Redneck.Names.MOSQUITO;
import static ru.m210projects.Redneck.Names.MOTORCYCLE;
import static ru.m210projects.Redneck.Names.MUD;
import static ru.m210projects.Redneck.Names.PLAYERONWATER;
import static ru.m210projects.Redneck.Names.RAT;
import static ru.m210projects.Redneck.Names.RESPAWNMARKERRED;
import static ru.m210projects.Redneck.Names.SCRAP1;
import static ru.m210projects.Redneck.Names.SCRAP2;
import static ru.m210projects.Redneck.Names.SCRAP3;
import static ru.m210projects.Redneck.Names.SCRAP4;
import static ru.m210projects.Redneck.Names.SCRAP5;
import static ru.m210projects.Redneck.Names.SCRAP6;
import static ru.m210projects.Redneck.Names.SECTOREFFECTOR;
import static ru.m210projects.Redneck.Names.SHELL;
import static ru.m210projects.Redneck.Names.SHITBALL;
import static ru.m210projects.Redneck.Names.SHOTGUNSHELL;
import static ru.m210projects.Redneck.Names.SNORKLE_ICON;
import static ru.m210projects.Redneck.Names.SPINNINGNUKEICON;
import static ru.m210projects.Redneck.Names.SWAMPBUGGY;
import static ru.m210projects.Redneck.Names.TORNADO;
import static ru.m210projects.Redneck.Names.UFOBEAM;
import static ru.m210projects.Redneck.Names.WATERBUBBLE;
import static ru.m210projects.Redneck.Names.WATERSPLASH2;
import static ru.m210projects.Redneck.Names.WHISHKEY_ICON;
import static ru.m210projects.Redneck.Premap.geoms1;
import static ru.m210projects.Redneck.Premap.geoms2;
import static ru.m210projects.Redneck.Premap.geomsector;
import static ru.m210projects.Redneck.Premap.geomx1;
import static ru.m210projects.Redneck.Premap.geomx2;
import static ru.m210projects.Redneck.Premap.geomy1;
import static ru.m210projects.Redneck.Premap.geomy2;
import static ru.m210projects.Redneck.Premap.numgeomeffects;
import static ru.m210projects.Redneck.Premap.shadeEffect;
import static ru.m210projects.Redneck.Screen.changepalette;
import static ru.m210projects.Redneck.Screen.digitalnumber;
import static ru.m210projects.Redneck.Screen.invennum;
import static ru.m210projects.Redneck.Screen.palto;
import static ru.m210projects.Redneck.Screen.patchstatusbar;
import static ru.m210projects.Redneck.Screen.restorepalette;
import static ru.m210projects.Redneck.Screen.setgamepalette;
import static ru.m210projects.Redneck.Screen.vscrn;
import static ru.m210projects.Redneck.Sector.ldist;
import static ru.m210projects.Redneck.Weapons.displayweapon;

import java.io.File;
import java.util.Arrays;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.Tools.Interpolation.ILoc;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Redneck.Main.UserFlag;
import ru.m210projects.Redneck.Factory.RRNetwork;
import ru.m210projects.Redneck.Menus.InterfaceMenu;
import ru.m210projects.Redneck.Types.PlayerOrig;
import ru.m210projects.Redneck.Types.PlayerStruct;

public class View {

	public static final String deathMessage = "or \"ENTER\" to load last saved game";

	public static int oyrepeat=-1;
	private static final char[] buffer = new char[256];

	public static int gPlayerIndex = -1;

	public static final int MAXUSERQUOTES = 4;
	public static int quotebot, quotebotgoal;
	public static short user_quote_time[] = new short[MAXUSERQUOTES];
	public static char user_quote[][] = new char[MAXUSERQUOTES][80];

	public static short fta,ftq, zoom, over_shoulder_on;
	public static int loogiex[] = new int[64],loogiey[] = new int[64];

	public static int cameradist = 0, cameraclock = 0;
	public static int gNameShowTime;
	public static int oviewingrange;

	public static void adduserquote(char[] daquote)
	{
	    for(int i=MAXUSERQUOTES-1;i>0;i--)
	    {
	    	System.arraycopy(user_quote[i-1], 0, user_quote[i], 0, 80);
	        user_quote_time[i] = user_quote_time[i-1];
	    }
	    System.arraycopy(daquote, 0, user_quote[0], 0, Math.min(daquote.length, 80));

	    user_quote_time[0] = 180;
	}

	public static void displayrest(int smoothratio)
	{
	    int a, i, j;

	    int cposx,cposy;
	    PlayerStruct pp = ps[screenpeek];

	    float cang = 0;

	    int cr = 0, cg = 0, cb = 0, cf = 0;
	    boolean dotint = false;

        if( changepalette != 0 )
	    {
	    	setgamepalette(pp,pp.palette, true);
	        changepalette = 0;
	    }

	    if( pp.pals_time > 0 && pp.loogcnt == 0)
	    {
	        dotint = true;
	    	cr = pp.pals[0];
	    	cg = pp.pals[1];
	    	cb = pp.pals[2];
	    	cf = pp.pals_time;

			restorepalette = true;
	    }
	    else if( restorepalette )
	    {
	    	setgamepalette(pp, pp.palette, true);

			dotint = true;
			cr = cg = cb = 0;
			cf = 0;
	        restorepalette = false;
	    }

	    if (dotint && !game.menu.gShowMenu)
		    palto(cr,cg,cb,cf|128);

	    i = pp.cursectnum;

	    if(i != -1) {
	    	show2dsector[i>>3] |= (1<<(i&7));

		    int startwall = sector[i].wallptr;
		    int endwall = startwall + sector[i].wallnum;

		    for(j = startwall; j < endwall; j++)
		    {
		    	WALL wal = wall[j];
		        i = wal.nextsector;
		        if (i < 0) continue;
		        if ((wal.cstat&0x0071) != 0) continue;
		        if ((wall[wal.nextwall].cstat&0x0071) != 0) continue;
		        if (sector[i].lotag == 32767) continue;
		        if (sector[i].ceilingz >= sector[i].floorz) continue;
		        show2dsector[i>>3] |= (1<<(i&7));
		    }
	    }

	    if(ud.camerasprite == -1)
	    {
	        if( ud.overhead_on != 2 && pp.newowner < 0)
	        {
                displayweapon(screenpeek);
                if(over_shoulder_on == 0 )
                    displaymasks(screenpeek);
	        }

	        if( ud.overhead_on > 0 )
	        {
                if( !ud.scrollmode )
                {
                     if(pp.newowner == -1)
                     {
                         if (screenpeek == myconnectindex && numplayers > 1)
                         {
                        	 RRNetwork net = game.net;
                             cposx = net.predictOld.x+mulscale((net.predict.x-net.predictOld.x),smoothratio, 16);
                             cposy = net.predictOld.y+mulscale((net.predict.y-net.predictOld.y),smoothratio, 16);
                             cang = net.predictOld.ang + (BClampAngle(net.predict.ang+1024-net.predictOld.ang)-1024) * smoothratio / 65536.0f;
                         }
                         else
                         {
                              cposx = pp.oposx+mulscale((pp.posx-pp.oposx),smoothratio, 16);
                              cposy = pp.oposy+mulscale((pp.posy-pp.oposy),smoothratio, 16);
                              cang = pp.oang + (BClampAngle(pp.ang+1024-pp.oang)-1024) * smoothratio / 65536.0f;
                         }
                    }
                    else
                    {
                        cposx = pp.oposx;
                        cposy = pp.oposy;
                        cang = pp.oang;
                    }
                }
                else
                {

                     ud.fola += ud.folavel / 8f;
                     ud.folx += (ud.folfvel*sintable[(512+2048-ud.fola)&2047])>>14;
                     ud.foly += (ud.folfvel*sintable[(512+1024-512-ud.fola)&2047])>>14;

                     cposx = ud.folx;
                     cposy = ud.foly;
                     cang = ud.fola;
                }

                if(ud.overhead_on == 2)
                {
                    engine.clearview(0);
                    engine.drawmapview(cposx,cposy,zoom, (short)cang);
                }
                engine.drawoverheadmap( cposx,cposy,zoom, (short)cang);

                if(ud.overhead_on == 2)
                {
                    if(ud.screen_size > 0) a = 145;
                    else a = 182;

                	Arrays.fill(buffer, (char)0);
                	buildString(buffer, 0, currentGame.episodes[ud.volume_number].Title);
                	game.getFont(0).drawText(5,a, buffer, -128, 0, TextAlign.Left, 2+8+16+256, false);
                    Arrays.fill(buffer, (char)0);
                    if(currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number] != null)
                    	buildString(buffer, 0, currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number].title);
                    game.getFont(0).drawText(5,a+6, buffer, -128, 0, TextAlign.Left, 2+8+16+256, false);

                    if ( cfg.gShowStat != 0 ) {
                    	int k = 0;
                    	if (ud.screen_size > 0 && ud.multimode > 1)
                    	{
	               	         j = 0; k = 20;
	               	         for(i=connecthead;i>=0;i=connectpoint2[i])
	               	             if (i > j) j = i;

	               	         if (j >= 4 && j <= 8) k += 20;
	               	         else if (j > 8 && j <= 12) k += 40;
	               	         else if (j > 12) k += 60;
                    	}
            	    	viewDrawStats(5, 5+k, cfg.gStatSize, true);
                    }
                }
	        }
	    }

	    if(game.menu.gShowMenu && !(game.menu.getCurrentMenu() instanceof InterfaceMenu))
    		return;

	    if(ps[myconnectindex].newowner == -1 && ud.overhead_on == 0 && ud.crosshair != 0 && ud.camerasprite == -1) {
	    	engine.rotatesprite((160-(ps[myconnectindex].look_ang>>1))<<16,100<<16,cfg.gCrossSize,0,CROSSHAIR,0,0,2+1,0,0,xdim,ydim);
	    }

	    coolgaugetext(screenpeek);
	    operatefta();

	    if(!isPsychoSkill()) {
		    if( fta > 1 && sprite[ps[myconnectindex].i].extra <= 0 && myconnectindex == screenpeek && ud.multimode < 2
		    		&& lastload != null && !lastload.isEmpty() && ud.recstat != 2 && BuildGdx.compat.checkFile(lastload, Path.User) != null) {
	        	int k = getftacoord();
	        	game.getFont(1).drawText(320>>1,k + 10, deathMessage, 0, 0, TextAlign.Center, 2+8+16, false);
	        }
	    }

	    if ( ud.screen_size > 0 && cfg.gShowStat == 1 && ud.overhead_on != 2) {
	    	int y = 202;
	    	if(ud.screen_size == 1) y = 162;
	    	if(ud.screen_size == 2 || ud.screen_size == 3) y = 158;
	    	if(ud.screen_size >= 4) y = 148;
	    	viewDrawStats(10, y, cfg.gStatSize, false);
	    }

	    if(game.isCurrentScreen(gGameScreen) && totalclock < gNameShowTime)
		{
	    	int transp = 0;
			if(totalclock > gNameShowTime - 20) transp = 1;
			if(totalclock > gNameShowTime - 10) transp = 33;

			if(cfg.showMapInfo != 0 && !game.menu.gShowMenu)
			{
				if(mUserFlag != UserFlag.UserMap || boardfilename == null) {
					if(ud.volume_number < nMaxEpisodes && currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number] != null && currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number].title != null)
						game.getFont(2).drawText(160,114, currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number].title, -128, 0, TextAlign.Center, 2 | transp, false);
				}
				else {
					Arrays.fill(buffer, (char)0);
					int index = boardfilename.lastIndexOf(File.separator);
					boardfilename.getChars(index+1, boardfilename.length(), buffer, 0);
					game.getFont(2).drawText(160,114, buffer, -128, 0, TextAlign.Center, 2 | transp, false);
				}
			}
		}

	    if(MODE_TYPE)
	    	typemode();

	    if( game.gPaused && !game.menu.gShowMenu )
	    {
	    	game.getFont(2).drawText(160, 100, "GAME PAUSED", 0, 0, TextAlign.Center, 2+8+16, false);
	    }

	    if(gPlayerIndex != -1 && gPlayerIndex != myconnectindex)
	    {
	    	int len = 0;
	    	if(ud.user_name[gPlayerIndex] == null || ud.user_name[gPlayerIndex].isEmpty())
	    		len = buildString(buf, 0, "Player ", gPlayerIndex+1);
	    	else len = buildString(buf, 0, ud.user_name[gPlayerIndex]);
	    	len = buildString(buf, len, " (", ps[gPlayerIndex].last_extra);
	    	len = buildString(buf, len, "hp)");
        	int shade = 16 - (totalclock & 0x3F);

        	int y = scale(windowy1, 200, ydim)+100;
        	if(ud.screen_size <= 4) //XXX
        		y += (engine.getTile(BOTTOMSTATUSBAR).getHeight() + engine.getTile(1649).getHeight()) / 4;

        	game.getFont(1).drawText(160,y, buf, shade, 0, TextAlign.Center, 2+8+16, false);
	    }

	    if(ud.coords != 0)
	    	coords(screenpeek);
	}

	public static void typemode()
	{
		if(Console.IsShown()) return;

		int j = 200-63;
		if(ud.screen_size <= 2) j = 200-20;
		else if(ud.screen_size == 3) j = 200-55;

		char[] buf = getInput().getMessageBuffer();
		int len = getInput().getMessageLength() + 1;
		if(len < buf.length)
			buf[len] = 0;
		int alignx = game.getFont(1).drawText(320>>1,j, getInput().getMessageBuffer(), 0, 0, TextAlign.Center, 2+8+16, false);
		engine.rotatesprite((((320+alignx+16)>>1))<<16,(j+6)<<16,4096,0,SPINNINGNUKEICON+(((totalclock>>3))&15),0,0,10,0,0,xdim-1,ydim-1);
	}

	public static int getftacoord()
	{
		int k = 0;

		 if (ud.screen_size > 0 && ud.multimode > 1)
	     {
	         int j = 0; k = 8;
	         for(int i=connecthead;i>=0;i=connectpoint2[i])
	             if (i > j) j = i;

	         if (j >= 4 && j <= 8) k += 8;
	         else if (j > 8 && j <= 12) k += 16;
	         else if (j > 12) k += 24;
	     }
	     else k = 0;

	     if (ftq == 115 || ftq == 116)
	     {
	         k = quotebot;
	         for(int i=0;i<MAXUSERQUOTES;i++)
	         {
	             if (user_quote_time[i] <= 0) break;
	             k -= 8;
	         }
	         k -= 4;
	     }

	     return k;
	}

	public static void operatefta()
	{
	     int i, j, k;

	     if(ud.screen_size <= 2) j = 200-20;
	     else if(ud.screen_size == 3) j = 200-55;
	     else j = 200-63;

	     quotebot = Math.min(quotebot,j);
	     quotebotgoal = Math.min(quotebotgoal,j);
	     if(MODE_TYPE) j -= 10;
	     quotebotgoal = j; j = quotebot;
	     for(i=0;i<MAXUSERQUOTES;i++)
	     {
	    	 k = user_quote_time[i]; if (k <= 0) break;
	         if (k > 4)
	        	 game.getFont(1).drawText(320>>1,j, user_quote[i], 0, 0, TextAlign.Center, 2+8+16, false);
	         else if (k > 2) game.getFont(1).drawText(320>>1,j, user_quote[i], 0, 0, TextAlign.Center, 2+8+16+1, false);
	             else game.getFont(1).drawText(320>>1,j, user_quote[i], 0, 0, TextAlign.Center, 2+8+16+1+32, false);
	         j -= 10;
	     }

	     if (fta <= 1) return;

	     k = getftacoord();

	     j = fta;
	     if (j > 4)
	    	 game.getFont(1).drawText(320>>1,k, currentGame.getCON().fta_quotes[ftq], 0, 0, TextAlign.Center, 2+8+16, false);
	     else if (j > 2)
	    	 game.getFont(1).drawText(320>>1,k, currentGame.getCON().fta_quotes[ftq], 0, 0, TextAlign.Center, 2+8+16+1, false);
	     else
	    	 game.getFont(1).drawText(320>>1,k, currentGame.getCON().fta_quotes[ftq], 0, 0, TextAlign.Center, 2+8+16+1+32, false);
	}

	public static void displayfragbar(int yoffset, boolean showpalette)
	{
		int row = (ud.multimode - 1) / 4;
		if(row >= 0)
		{

            if(yoffset > 0) yoffset -= 9 * row;
			for(int r = 0; r <= row; r++)
				engine.rotatesprite(0,yoffset +(r * engine.getTile(FRAGBAR).getHeight()) << 16,34000,0,FRAGBAR,0,0,2+8+16+64,0,0,xdim-1,ydim-1);

			for(int i=connecthead;i>=0;i=connectpoint2[i])
		    {
				if(ud.user_name[i] == null || ud.user_name[i].isEmpty())
					buildString(buffer, 0, "Player ", i+1);
				else buildString(buffer, 0, ud.user_name[i]);

				game.getFont(0).drawText(26+(73*(i&3)),yoffset + 2+((i&28)<<1), buffer, 0, showpalette ? sprite[ps[i].i].pal : 0, TextAlign.Left, 10 | 16, false);
		        buildString(buffer, 0, "", ps[i].frag-ps[i].fraggedself);
		        game.getFont(0).drawText(23+50+(73*(i&3)),yoffset + 2+((i&28)<<1), buffer, 0, showpalette ? sprite[ps[i].i].pal : 0, TextAlign.Left, 10 | 16, false);
		    }
		}
	}

	public static void displaymeters(int snum, int flags)
	{
		PlayerStruct p = ps[snum];

		engine.rotatesprite(257 << 16, 181 << 16, 0x8000, p.alcohol_meter, 62, 0, 0, 10 | flags, 0, 0, xdim - 1, ydim - 1);
		engine.rotatesprite(293 << 16, 181 << 16, 0x8000, p.gut_meter, 62, 0, 0, 10 | flags, 0, 0, xdim - 1, ydim - 1);

		int x, pic;
		if(p.alcohol_amount >= 0 && p.alcohol_amount <= 30)
		{
			x = 239 << 16;
			pic = 920;
		}
		else if(p.alcohol_amount >= 31 && p.alcohol_amount <= 65)
		{
			x = 248 << 16;
			pic = 921;
		}
		else if(p.alcohol_amount >= 66 && p.alcohol_amount <= 87)
		{
			x = 256 << 16;
			pic = 922;
		}
		else
		{
			x = 265 << 16;
			pic = 923;
		}
		engine.rotatesprite(x, 190 << 16, 0x8000, 0, pic, 0, 0, 10 | 16 | flags, 0, 0, xdim - 1, ydim - 1);

		if(p.gut_amount >= 0 && p.gut_amount <= 30)
		{
			x = 276 << 16;
			pic = 920;
		}
		else if(p.gut_amount >= 31 && p.gut_amount <= 65)
		{
			x = 285 << 16;
			pic = 921;
		}
		else if(p.gut_amount >= 66 && p.gut_amount <= 87)
		{
			x = 294 << 16;
			pic = 922;
		}
		else
		{
			x = 302 << 16;
			pic = 923;
		}
		engine.rotatesprite(x, 190 << 16, 0x8000, 0, pic, 0, 0, 10 | 16 | flags, 0, 0, xdim - 1, ydim - 1);
	}

	public static void debuginfo(int x, int y)
	{
		buildString(buffer, 0, "totalclock= ", totalclock);
		engine.printext256(x,y,31,-1,buffer,0, 1.0f); y += 10;

		buildString(buffer, 0, "global_random= ", global_random);
		engine.printext256(x,y,31,-1,buffer,0, 1.0f); y += 10;

		buildString(buffer, 0, "randomseed= ", engine.getrand());
		engine.printext256(x,y,31,-1,buffer,0, 1.0f); y += 10;

		buildString(buffer, 0, "posx= ", ps[0].posx);
		engine.printext256(x,y,31,-1,buffer,0, 1.0f); y += 10;

		buildString(buffer, 0, "posy= ", ps[0].posy);
		engine.printext256(x,y,31,-1,buffer,0, 1.0f); y += 10;

		buildString(buffer, 0, "posz= ", ps[0].posz);
		engine.printext256(x,y,31,-1,buffer,0, 1.0f); y += 10;

		buildString(buffer, 0, "ang= ", Float.toString(ps[0].ang));
		engine.printext256(x,y,31,-1,buffer,0, 1.0f); y += 10;

		buildString(buffer, 0, "horiz= ", Float.toString(ps[0].horiz));
		engine.printext256(x,y,31,-1,buffer,0, 1.0f); y += 10;

		buildString(buffer, 0, "xvel= ", ps[0].posxv);
		engine.printext256(x,y,31,-1,buffer,0, 1.0f); y += 10;

		buildString(buffer, 0, "yvel= ", ps[0].posyv);
		engine.printext256(x,y,31,-1,buffer,0, 1.0f); y += 10;

		buildString(buffer, 0, "CarSpeed= ", ps[0].CarSpeed);
		engine.printext256(x,y,31,-1,buffer,0, 1.0f); y += 10;

		buildString(buffer, 0, "VBumpTarget= ", ps[0].VBumpTarget);
		engine.printext256(x,y,31,-1,buffer,0, 1.0f); y += 10;

		buildString(buffer, 0, "VBumpNow= ", ps[0].VBumpNow);
		engine.printext256(x,y,31,-1,buffer,0, 1.0f); y += 10;

		buildString(buffer, 0, "CarVar1= ", ps[0].CarVar1);
		engine.printext256(x,y,31,-1,buffer,0, 1.0f); y += 10;

		buildString(buffer, 0, "CarVar2= ", ps[0].CarVar2);
		engine.printext256(x,y,31,-1,buffer,0, 1.0f); y += 10;

		buildString(buffer, 0, "CarVar3= ", ps[0].CarVar3);
		engine.printext256(x,y,31,-1,buffer,0, 1.0f); y += 10;

		buildString(buffer, 0, "CarVar4= ", ps[0].CarVar4);
		engine.printext256(x,y,31,-1,buffer,0, 1.0f); y += 10;

		buildString(buffer, 0, "CarVar5= ", ps[0].CarVar5);
		engine.printext256(x,y,31,-1,buffer,0, 1.0f); y += 10;

		buildString(buffer, 0, "CarVar6= ", ps[0].CarVar6);
		engine.printext256(x,y,31,-1,buffer,0, 1.0f); y += 10;
	}

	public static void coolgaugetext(int snum)
	{
	    int i, o, ss;

	    PlayerStruct p = ps[snum];

//	    debuginfo(20, 40);

	    if (p.invdisptime > 0)
	    	displayinventory(p);

	    if(screenpeek != myconnectindex)
		{
        	if(ud.user_name[screenpeek] == null || ud.user_name[screenpeek].isEmpty())
        		buildString(buf, 0, "View from player ", screenpeek+1);
        	else buildString(buf, 0, "View from ", ud.user_name[screenpeek]);
        	int shade = 16 - (totalclock & 0x3F);

        	game.getFont(1).drawText(160, scale(windowy1, 200, ydim) + 10, buf, shade, 0, TextAlign.Center, 10 | 16, false);
		}

	    ss = ud.screen_size; if (ss < 1) return;

	    if ( ud.multimode > 1 || mFakeMultiplayer )
	    {
	    	displayfragbar(0, true);
	    }

	    if (ss == 1)   //DRAW MINI STATUS BAR:
	    {
	    	engine.rotatesprite(0x20000, 11272192, 0x8000, 0, HEALTHBOX, 0, 21, 26 | 256, 0, 0, xdim - 1, ydim - 1);

	        if(sprite[p.i].pal == 1 && p.last_extra < 2)
	            digitalnumber(20,200-17,1,-16,10+16+256);
	        else digitalnumber(20,200-17,p.last_extra,-16,10+16+256);


	        int x = 41;
	        if (p.curr_weapon == HANDREMOTE_WEAPON) i = CROSSBOW_WEAPON; else i = p.curr_weapon;
	        if(p.ammo_amount[i] != 0) {
		        engine.rotatesprite(x<<16,(200-28)<<16,0x8000,0,AMMOBOX,0,21,26|256,0,0,xdim-1,ydim-1);
		        digitalnumber(x+16,200-17,p.ammo_amount[i],-16,10+16+256);
		        x += engine.getTile(AMMOBOX).getWidth() / 2 + 2;
	        }

	        if((p.gotkey[1]|p.gotkey[2]|p.gotkey[3]) != 0) {
	        	engine.rotatesprite(x<<16,(200-28)<<16,0x8000,0,AMMOBOX,0,21,26|256,0,0,xdim-1,ydim-1);
		        engine.rotatesprite(x<<16,(200-28)<<16,0x8000,0,KEYSIGN,0,21,10+16+256,0,0,xdim-1,ydim-1);

		        if ( p.gotkey[3] != 0 ) {
		        	int pal = 23;
		        	if(cfg.gColoredKeys) pal = 7;
		    		engine.rotatesprite(x+5<<16,182<<16, 0x8000, 0, 1656, 0, pal, 10+16+256, 0, 0, xdim - 1, ydim - 1);
		        }
		    	if ( p.gotkey[2] != 0  ) {
		    		int pal = 21;
		        	if(cfg.gColoredKeys) pal = 2;
		    		engine.rotatesprite(x+18<<16,182<<16, 0x8000, 0, 1656, 0, pal, 10+16+256, 0, 0, xdim - 1, ydim - 1);
		    	}
		    	if ( p.gotkey[1] != 0  ) {
		    		int pal = 0;
		        	if(cfg.gColoredKeys) pal = 1;
		    		engine.rotatesprite(x+11<<16,189<<16, 0x8000, 0, 1656, 0, pal, 10+16+256, 0, 0, xdim - 1, ydim - 1);
		    	}
		        x += engine.getTile(KEYSIGN).getWidth() / 2 + 2;
	        }

	        { //my gutmeter
		        engine.rotatesprite(225 << 16, 172 << 16, 0x9000, 0, GUTSMETTER, 0, 21, 26 | 512, 0, 0, xdim - 1, ydim - 1);
				engine.rotatesprite(251 << 16, 189 << 16, 0x9000, p.alcohol_meter, 62, 0, 0, 10 | 512, 0, 0, xdim - 1, ydim - 1);
				engine.rotatesprite(293 << 16, 189 << 16, 0x9000, p.gut_meter, 62, 0, 0, 10 | 512, 0, 0, xdim - 1, ydim - 1);
	        }

	        if (p.inven_icon != 0)
	        {
	        	engine.rotatesprite(x<<16, (200-30)<<16, 0x8000, 0, INVENTORYBOX, 0, 21, 26 | 256, 0, 0, xdim - 1, ydim - 1);
	        	buf[0] = '%';
	        	buf[1] = 0;
	            switch(p.inven_icon)
	            {
	                case 1: i = 1645; game.getFont(0).drawChar((x+37),190,'%', 0, 6, 2 | 8 | 16 | 256, false); break;
	                case 2: i = 1654; game.getFont(0).drawChar((x+37),190,'%', 0, 6, 2 | 8 | 16 | 256, false); break;
	                case 3: i = 1655; break;
	                case 4: i = 1652; break;
	                case 5: i = 1646; break;
	                case 6: i = 1653; break;
	                case 7: i = BOOT_ICON; break;
	                default: i = -1;
	            }
	            if (i >= 0) engine.rotatesprite((x+6)<<16, (200-21)<<16, 0x8000, 0, i, 0, 0, 26 | 256, 0, 0, xdim - 1, ydim - 1);

	            if (p.inven_icon >= 6)
	            	game.getFont(0).drawText(x+22,180,"AUTO", 0, 2, TextAlign.Left, 2 | 8 | 16 | 256, false);
	            switch(p.inven_icon)
	            {
	            case 1: i = p.whishkey_amount; break;
                case 2: i = ((p.moonshine_amount+3)>>2); break;
                case 3: i = ((p.beer_amount)/400); break;
                case 4: i = ((p.cowpie_amount)/100); break;
                case 5: i = p.yeehaa_amount/12; break;
                case 6: i = ((p.snorkle_amount+63)>>6); break;
                case 7: i = (p.boot_amount / 10 >> 1); break;
	            }
	            invennum(x+27, 194, i, 0, 8 | 256);
	        }

	        return;
	    }

	    if(ss == 2) //GDX'S STATUS BAR
	    {
	    	//left part
	    	engine.rotatesprite(81 << 16, 183 << 16, 0x8000, 0, WIDEHUD_PART1, 0, 0, 10 | 256, 0, 0, xdim - 1, ydim - 1);

	    	//health
	    	if(sprite[p.i].pal == 1 && p.last_extra < 2)
	            digitalnumber(59,200-20,1,-16,10+16+256);
	        else digitalnumber(59,200-20,p.last_extra,-16,10+16+256);

	    	//ammo
	        if (p.curr_weapon == HANDREMOTE_WEAPON) i = CROSSBOW_WEAPON; else i = p.curr_weapon;
	        if(p.ammo_amount[i] != 0)
		        digitalnumber(96,200-20,p.ammo_amount[i],-16,10+16+256);

	        if (ud.multimode > 1 && ud.coop != 1) {
	        	if(engine.getTile(KILLSSIGN).isLoaded())
	        		engine.rotatesprite(118<<16,(168) << 16,32768,0,KILLSSIGN,0,0,10+16,0,0,xdim-1,ydim-1);
	        	else engine.rotatesprite(126<<16,(169) << 16,65536,0,KILLSICON,0,0,10+16,0,0,xdim-1,ydim-1);
	        }

	        if (ud.multimode > 1 && ud.coop != 1)
		    {
	            digitalnumber(135,200-20,max(p.frag-p.fraggedself,0),-16,10+16);
		    } else {
		        //keys
		        int x = 117;
		        if ( p.gotkey[3] != 0 ) {
		        	int pal = 23;
		        	if(cfg.gColoredKeys) pal = 7;
		    		engine.rotatesprite(x+5<<16,180<<16, 0x8000, 0, 1656, 0, pal, 10+16+256, 0, 0, xdim - 1, ydim - 1);
		        }
		    	if ( p.gotkey[2] != 0  ) {
		    		int pal = 21;
		        	if(cfg.gColoredKeys) pal = 2;
		    		engine.rotatesprite(x+18<<16,180<<16, 0x8000, 0, 1656, 0, pal, 10+16+256, 0, 0, xdim - 1, ydim - 1);
		    	}
		    	if ( p.gotkey[1] != 0  ) {
		    		int pal = 0;
		        	if(cfg.gColoredKeys) pal = 1;
		    		engine.rotatesprite(x+11<<16,187<<16, 0x8000, 0, 1656, 0, pal, 10+16+256, 0, 0, xdim - 1, ydim - 1);
		    	}
		    }

	        //right part
	        engine.rotatesprite(244 << 16, 183 << 16, 0x8000, 0, WIDEHUD_PART2, 0, 0, 10 | 512, 0, 0, xdim - 1, ydim - 1);

	    	//inventory
	    	int x = 185;
	    	if (p.inven_icon != 0)
	        {
	    		o = 179 << 16;
	        	buf[0] = '%';
	        	buf[1] = 0;
	            switch(p.inven_icon)
	            {
	                case 1: i = 1645; o = 178 << 16; game.getFont(0).drawChar((x+37),189,'%', 0, 6, 2 | 8 | 16 | 512, false); break;
	                case 2: i = 1654; o = 178 << 16; game.getFont(0).drawChar((x+37),189,'%', 0, 6, 2 | 8 | 16 | 512, false); break;
	                case 3: i = 1655; break;
	                case 4: i = 1652; break;
	                case 5: i = 1646; break;
	                case 6: i = 1653; o = 176 << 16; break;
	                case 7: i = BOOT_ICON; o = 178 << 16; break;
	                default: i = -1;
	            }
	            if (i >= 0) engine.rotatesprite((x+6)<<16, o, 0x8000, 0, i, 0, 0, 26 | 512, 0, 0, xdim - 1, ydim - 1);

	            if (p.inven_icon >= 6)
	            	game.getFont(0).drawText(x+22,180,"AUTO", 0, 2, TextAlign.Left, 2 | 8 | 16 | 512, false);
	            switch(p.inven_icon)
	            {
	            case 1: i = p.whishkey_amount; break;
                case 2: i = ((p.moonshine_amount+3)>>2); break;
                case 3: i = ((p.beer_amount)/400); break;
                case 4: i = ((p.cowpie_amount)/100); break;
                case 5: i = p.yeehaa_amount/12; break;
                case 6: i = ((p.snorkle_amount+63)>>6); break;
                case 7: i = (p.boot_amount / 10 >> 1); break;
	            }
	            invennum(x+27, 192, i, 0, 8 | 512);
	        }

	    	displaymeters(screenpeek, 512);
	    	return;
	    }

	    //DRAW/UPDATE FULL STATUS BAR:

        patchstatusbar(0,0,320,200);

        if(ss > 3) {
	        engine.rotatesprite(0, 10354688, 0x8020, 0, 1649, 0, 0, 10+16, 0, 0, xdim - 1, ydim - 1);
	        int wpic = 930;
	        for(int w = 0; w < 9; w++, wpic++)
	        {
	        	if(w == 4 && p.curr_weapon == CHICKENBOW_WEAPON)
	        	{
	        		engine.rotatesprite((32 * w + 18) << 16, 10485760, 0x8020, 0, 940, 0, 0, 10+16, 0, 0, xdim - 1, ydim - 1);
	        		invennum(32 * w + 38, 160, p.ammo_amount[CHICKENBOW_WEAPON], 0, 8+16);
	        	} else {
	        		if(p.gotweapon[w+1])
	        			engine.rotatesprite((32 * w + 18) << 16, 10485760, 0x8020, 0, wpic, 0, 0, 10+16, 0, 0, xdim - 1, ydim - 1);
	        		invennum(32 * w + 38, 160, p.ammo_amount[w+1], 0, 8+16);
	        	}
	        }
        }

        if (ud.multimode > 1 && ud.coop != 1) {
        	if(engine.getTile(KILLSSIGN).isLoaded())
        		engine.rotatesprite(133<<16,(168) << 16,32768,0,KILLSSIGN,0,0,10+16,0,0,xdim-1,ydim-1);
        	else engine.rotatesprite(142<<16,(169) << 16,65536,0,KILLSICON,0,0,10+16,0,0,xdim-1,ydim-1);
        }

	    if (ud.multimode > 1 && ud.coop != 1)
	    {
            digitalnumber(150,180,max(p.frag-p.fraggedself,0),-16,10+16);
	    }
	    else
	    {
	    	int x = 134;
	    	if ( p.gotkey[3] != 0 ) {
	    		int pal = 23;
	        	if(cfg.gColoredKeys) pal = 7;
	    		engine.rotatesprite(x+5<<16,180<<16, 0x8000, 0, 1656, 0, pal, 10+16, 0, 0, xdim - 1, ydim - 1);
	    	}
	    	if ( p.gotkey[2] != 0  ) {
	    		int pal = 21;
	        	if(cfg.gColoredKeys) pal = 2;
	    		engine.rotatesprite(x+18<<16,180<<16, 0x8000, 0, 1656, 0, pal, 10+16, 0, 0, xdim - 1, ydim - 1);
	    	}
	    	if ( p.gotkey[1] != 0  ) {
	    		int pal = 0;
	        	if(cfg.gColoredKeys) pal = 1;
	    		engine.rotatesprite(x+11<<16,187<<16, 0x8000, 0, 1656, 0, pal, 10+16, 0, 0, xdim - 1, ydim - 1);
	    	}
	    }

        if(sprite[p.i].pal == 1 && p.last_extra < 2)
            digitalnumber(64,200-20,1,-16,10+16);
        else digitalnumber(64,200-20,p.last_extra,-16,10+16);

        if (p.curr_weapon != KNEE_WEAPON)
        {
            if (p.curr_weapon == HANDREMOTE_WEAPON) i = CROSSBOW_WEAPON; else i = p.curr_weapon;
            digitalnumber(107,200-20,p.ammo_amount[i],-16,10+16);
        }

        i = 0;
        if (p.inven_icon != 0)
        {
        	o = 179 << 16;
        	buf[0] = '%';
        	buf[1] = 0;
            switch(p.inven_icon)
            {
                case 1: i = 1645; o = 178 << 16; game.getFont(0).drawChar(216,190, '%', 0, 6, 2 | 8 | 16, false); break;
                case 2: i = 1654; o = 178 << 16;  game.getFont(0).drawChar(216,190, '%', 0, 6, 2 | 8 | 16, false); break;
                case 3: i = 1655; break;
                case 4: i = 1652; break;
                case 5: i = 1646; break;
                case 6: i = 1653; o = 176 << 16; break;
                case 7: i = BOOT_ICON; o = 178 << 16; break;
            }
            engine.rotatesprite(11993088,o,32768,0,i,0,0,10+16,0,0,xdim-1,ydim-1);
            if (p.inven_icon >= 6)
            	game.getFont(0).drawText(201,180,  "AUTO", 0, 2, TextAlign.Left, 2 | 8 | 16, false);

            switch(p.inven_icon)
            {
                case 1: i = p.whishkey_amount; break;
                case 2: i = ((p.moonshine_amount+3)>>2); break;
                case 3: i = ((p.beer_amount)/400); break;
                case 4: i = ((p.cowpie_amount)/100); break;
                case 5: i = p.yeehaa_amount/12; break;
                case 6: i = ((p.snorkle_amount+63)>>6); break;
                case 7: i = (p.boot_amount / 10 >> 1); break;
            }
            invennum(206, 192, i, 0, 8);

        }
        displaymeters(screenpeek, 0);
	}

	public static void displayrooms(int snum,int smoothratio)
	{
	    int dst,j,fz,cz;
	    short k;
	    int tposx,tposy,i;
	    short tang;

	    PlayerStruct p = ps[snum];

	    gPlayerIndex = -1;

	    if( (!game.menu.gShowMenu
	    		&& ud.overhead_on == 2)
	    		|| game.menu.isOpened(game.menu.mMenus[HELP])
	    		|| p.cursectnum == -1)
	    	return;

	    if ( p.fogtype != 0)
	    	gVisibility = currentGame.getCON().const_visibility;
	    visibility = gVisibility;

	    int cposx = p.posx;
	    int cposy = p.posy;
	    int cposz = p.posz;
        float cang = p.ang;
        float choriz = p.horiz + p.horizoff;
        short sect = p.cursectnum;

	    if(sect < 0 || sect >= MAXSECTORS) return;

	    if(ud.camerasprite >= 0)
	    {
	        SPRITE s = sprite[ud.camerasprite];

	        if(s.yvel < 0) s.yvel = -100;
	        else if(s.yvel > 199) s.yvel = 300;

	        cang = (hittype[ud.camerasprite].tempang+mulscale((((s.ang+1024-hittype[ud.camerasprite].tempang)&2047)-1024),smoothratio, 16));

	        se40code(s.x,s.y,s.z,cang,s.yvel,smoothratio);

	        engine.drawrooms(s.x,s.y,s.z-(4<<8),cang,s.yvel,s.sectnum);
	        animatesprites(s.x,s.y,s.z-(4<<8),(short)cang,smoothratio);
	        engine.drawmasks();
	    }
	    else
	    {
	        i = divscale(1,sprite[p.i].yrepeat+28, 22);
	        if (i != oyrepeat)
	        {
	        	oyrepeat = i;
	            vscrn(ud.screen_size);
	        }

	        if ( oviewingrange != viewingrange && p.DrugMode == 0 ) {
	            engine.setaspect_new();
	            oviewingrange = viewingrange;
	        }

	        if ( p.DrugMode > 0 && !MODE_TYPE && !game.gPaused)
	        	engine.setaspect(p.drug_aspect, yxaspect);

	        if ( (snum == myconnectindex) && (numplayers > 1) )
	        {
	        	RRNetwork net = game.net;
                cposx = net.predictOld.x+mulscale((net.predict.x-net.predictOld.x),smoothratio,16);
                cposy = net.predictOld.y+mulscale((net.predict.y-net.predictOld.y),smoothratio,16);
                cposz = net.predictOld.z+mulscale((net.predict.z-net.predictOld.z),smoothratio,16);
                cang = net.predictOld.ang + (BClampAngle(net.predict.ang+1024-net.predictOld.ang)-1024) * smoothratio / 65536.0f;
                cang += net.predictOld.lookang + (BClampAngle(net.predict.lookang+1024-net.predictOld.lookang)-1024) * smoothratio / 65536.0f;
                choriz = net.predictOld.horiz+net.predictOld.horizoff+(((net.predict.horiz+net.predict.horizoff-net.predictOld.horiz-net.predictOld.horizoff) * smoothratio) / 65536.0f);
                sect = net.predict.sectnum;

                if( ( ud.screen_tilting != 0 && p.rotscrnang != 0 ) )
    	        {
                    tang = p.rotscrnang;
                    engine.getrender().settiltang(net.predictOld.rotscrnang + mulscale(((net.predict.rotscrnang - net.predictOld.rotscrnang + 1024)&2047)-1024,smoothratio, 16));
    	        } else engine.getrender().settiltang(0);
	        }
	        else
	        {
	        	cposx = p.prevView.x + mulscale((cposx-p.prevView.x),smoothratio,16);
                cposy = p.prevView.y + mulscale((cposy-p.prevView.y),smoothratio,16);
                cposz = p.prevView.z + mulscale((cposz-p.prevView.z),smoothratio,16);
                cang = p.prevView.ang + (BClampAngle(cang+1024-p.prevView.ang)-1024) * smoothratio / 65536.0f;
                cang += p.prevView.lookang + (BClampAngle(p.look_ang+1024-p.prevView.lookang)-1024) * smoothratio / 65536.0f;
                choriz = (p.prevView.horiz +p.prevView.horizoff+((choriz-p.prevView.horiz-p.prevView.horizoff) * smoothratio) / 65536.0f);

                if( ( ud.screen_tilting != 0 && p.rotscrnang != 0 ) )
    	        {
                    tang = p.rotscrnang;
                    engine.getrender().settiltang(p.prevView.rotscrnang + mulscale(((p.rotscrnang - p.prevView.rotscrnang + 1024)&2047)-1024,smoothratio, 16));
    	        } else engine.getrender().settiltang(0);
	        }

	        if (p.newowner >= 0)
	        {
                cang = (short) (p.ang+p.look_ang);
                choriz = p.horiz+p.horizoff;
                cposx = p.posx;
                cposy = p.posy;
                cposz = p.posz;
                sect = sprite[p.newowner].sectnum;
                smoothratio = 65536;
	        }
	        else if( over_shoulder_on == 0 )
	        	cposz += p.opyoff+mulscale((p.pyoff-p.opyoff),smoothratio, 16);
	        else {
	        	view(p,cposx,cposy,cposz,sect,cang,choriz);

	        	cposx = viewout.ox;
	        	cposy = viewout.oy;
        	  	cposz = viewout.oz;
        	  	sect = viewout.os;
         	}

	        cz = hittype[p.i].ceilingz;
	        fz = hittype[p.i].floorz;

	        if(earthquaketime > 0 && p.on_ground)
	        {
	            cposz += 256-(((earthquaketime)&1)<<9);
	            cang += (2-((earthquaketime)&2))<<2;
	        }

	        if(sprite[p.i].pal == 1) cposz -= (18<<8);

	        if(p.newowner >= 0)
	            choriz = (short) (100+sprite[p.newowner].shade);
	        else if(p.spritebridge == 0)
	        {
	            if( cposz < ( p.truecz + (4<<8) ) ) cposz = cz + (4<<8);
	            else if( cposz > ( p.truefz - (4<<8) ) ) cposz = fz - (4<<8);
	        }

	        if (sect >= 0)
	        {
	            engine.getzsofslope(sect,cposx,cposy, zofslope);
	            if (cposz < zofslope[CEIL]+(4<<8)) cposz = zofslope[CEIL]+(4<<8);
	            if (cposz > zofslope[FLOOR]-(4<<8)) cposz = zofslope[FLOOR]-(4<<8);
	        }

	        if(choriz > 299) choriz = 299;
	        else if(choriz < -99) choriz = -99;

	        se40code(cposx,cposy,cposz,cang,choriz,smoothratio);

	        if ((gotpic[MIRROR>>3]&(1<<(MIRROR&7))) > 0)
	        {
	            dst = 0x7fffffff; i = 0;
	            for(k=0;k<mirrorcnt;k++)
	            {
	                j = klabs(wall[mirrorwall[k]].x-cposx);
	                j += klabs(wall[mirrorwall[k]].y-cposy);
	                if (j < dst) { dst = j; i = k; }
	            }

	            if( wall[mirrorwall[i]].overpicnum == MIRROR )
	            {
	                engine.preparemirror(cposx,cposy,cposz,cang,choriz,mirrorwall[i],mirrorsector[i]);

	                tposx = mirrorx;
	                tposy = mirrory;
	        		tang = (short) mirrorang;

	                j = visibility;
	                visibility = (j>>1) + (j>>2);

	                engine.drawrooms(tposx,tposy,cposz,tang,choriz,(short) (mirrorsector[i]+MAXSECTORS));

	                display_mirror = 1;
	                animatesprites(tposx,tposy,cposz,tang,smoothratio);
	                display_mirror = 0;

	                engine.drawmasks();
	                engine.completemirror();   //Reverse screen x-wise in this function
	                visibility = j;
	            }
	            gotpic[MIRROR>>3] &= ~(1<<(MIRROR&7));
	        }

	        engine.drawrooms(cposx,cposy,cposz,cang,choriz,sect);
	        animatesprites(cposx,cposy,cposz,(short)cang,smoothratio);
	        engine.drawmasks();

	        displaygeom3d(sect, cposx, cposy, cposz, choriz, cang, sect, smoothratio);
	    }
	}

	public static String lastmessage;
	public static void FTA(int q, PlayerStruct p )
	{
		if( ud.fta_on == 1 && p == ps[screenpeek])
	    {
	        if( fta > 0 && q != 115 && q != 116 )
	            if( ftq == 115 || ftq == 116 ) return;

	        fta = 100;

	        if( ftq != q || q == 26 )
	        {
	            ftq = (short) q;
	        }

			if(ftq >= currentGame.getCON().fta_quotes.length) {
				Console.Println("Invalid quote " + ftq, Console.OSDTEXT_RED);
				return;
			}

	        int len = 0;
	        while(len < currentGame.getCON().fta_quotes[ftq].length && currentGame.getCON().fta_quotes[ftq][++len] != 0);

        	String message = new String(currentGame.getCON().fta_quotes[ftq], 0, len);
        	if(!message.equals(lastmessage)) {
	        	Console.Println(message);
	        	lastmessage = message;
        	}
	    }
	}

	public static void animatesprites(int x,int y,int z, short a,int smoothratio)
	{
	    short i, j, k, p, sect;
	    int l, t1,t3,t4;
	    SPRITE s, t;

	    for(j=0;j < spritesortcnt; j++)
	    {
	        t = tsprite[j];
	        i = t.owner;
	        s = sprite[t.owner];

			if(!isValidSector(t.sectnum))
				continue;

	        switch(t.picnum)
	        {
	        	case DOORKEY:
	        		if(cfg.gColoredKeys)
	        		{
		        		switch(t.lotag)
		        		{
			        		case 100: //got1
			        			t.pal = 1;
			        			break;
			        		case 101: //got2
			        			t.pal = 2;
			        			break;
			        		case 102: //got3
			        			t.pal = 7;
			        			break;
			        		default:
			        			t.pal = 6;
			        			break;
		        		}
	        		}
	        		t.shade = -128;
	        		break;
	            case BLOODPOOL:
	            case FOOTPRINTS:
	            case FOOTPRINTS2:
	            case FOOTPRINTS3:
	            case FOOTPRINTS4:
	                if(t.shade == 127) continue;
	                break;

	            case BLOODSPLAT1:
	            case BLOODSPLAT2:
	            case BLOODSPLAT3:
	            case BLOODSPLAT4:
	                if(ud.lockout != 0) t.xrepeat = t.yrepeat = 0;
	                else if(t.pal == 6)
	                {
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
	            case 1152:
	            	break;
	            default:
	                if( ( (t.cstat&16) != 0 ) || ( badguy(t) && t.extra > 0) || t.statnum == 10)
	                    continue;
	        }

	        if(t.sectnum < 0 || t.sectnum >= MAXSECTORS)
	        	continue;

	        if ((sector[t.sectnum].ceilingstat&1) != 0)
	            l = sector[t.sectnum].ceilingshade;
	        else
	            l = sector[t.sectnum].floorshade;

	        if(l < -127) l = -127;
	        if(l > 128) l =  127;
	        t.shade = (byte) l;
	    }


	    for(j=0;j < spritesortcnt; j++ )  //Between drawrooms() and drawmasks()
	    {                             //is the perfect time to animate sprites
	        t = tsprite[j];
	        i = t.owner;
	        s = sprite[i];

	        if(t.sectnum < 0 || t.sectnum >= MAXSECTORS)
	        	continue;

	        switch(s.picnum)
	        {
	            case SECTOREFFECTOR:
	                if(t.lotag == 27 && ud.recstat == 1)
	                {
	                    t.picnum = (short) (11+((totalclock>>3)&1));
	                    t.cstat |= 128;
	                }
	                else
	                    t.xrepeat = t.yrepeat = 0;
	                break;
	            case 1097:
	            case 1106:
	            case 1115:
	            case 1168:
	            case 1174:
	            case 1175:
	            case 1176:
	            case 1178:
	            case 1225:
	            case 1226:
	            case 1529:
	            case 1530:
	            case 1531:
	            case 1532:
	            case 1533:
	            case 1534:
	            case 2231:
	            case 5581:
	            case 5583:
	                if(ud.lockout != 0)
	                {
	                    t.xrepeat = t.yrepeat = 0;
	                    continue;
	                }
	        }

	        if( t.statnum == 99 ) continue;
	        if( s.statnum != 1 && s.picnum == APLAYER && ps[s.yvel].newowner == -1 && s.owner >= 0 )
	        {
	            t.x -= mulscale(65536-smoothratio,ps[s.yvel].posx-ps[s.yvel].oposx, 16);
	            t.y -= mulscale(65536-smoothratio,ps[s.yvel].posy-ps[s.yvel].oposy, 16);
	            t.z = ps[s.yvel].oposz + mulscale(smoothratio,ps[s.yvel].posz-ps[s.yvel].oposz, 16);
	            t.z += (40<<8);

	            s.xrepeat = 24;
	            s.yrepeat = 17;
	        }
	        else if( ( s.statnum == 0 && s.picnum != 1298) || s.statnum == 10 || s.statnum == 6
	        		|| s.statnum == 4 || s.statnum == 5 || s.statnum == 1
	        		|| s.statnum == 116 || s.statnum == 117 || s.statnum == 118 || s.statnum == 121) //GDX 21.04.2019 - RA airplane interpolation
	        {
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

	        switch(s.picnum)
	        {
	        case BURNING:
                if( sprite[s.owner].statnum == 10 )
                {
                    if( display_mirror == 0 && sprite[s.owner].yvel == screenpeek && over_shoulder_on == 0 )
                        t.xrepeat = 0;
                    else
                    {
                        t.ang = engine.getangle(x-t.x,y-t.y);
                        t.x = sprite[s.owner].x;
                        t.y = sprite[s.owner].y;
                        t.x += sintable[(t.ang+512)&2047]>>10;
                        t.y += sintable[t.ang&2047]>>10;
                    }
                }
                break;

	        case 5300:
            case 5295:
            case 5290:
            	if ( t.pal == 19 )
                    t.shade = -127;
	        case 4041:
            case 4046:
            case 4055:
            case 4235:
            case 4244:
            case 4748:
            case 4753:
            case 4758:
            case 5602:
            case 5607:
            case 5616:
	        case JIBS1:
            case JIBS2:
            case JIBS3:
            case JIBS4:
            case JIBS5:
            case JIBS6:
            	//RA
            case 2460:
	        case 2465:
	        case 5872: //RA motowheel
        	case 5877: //RA mototank
        	case 5882: //RA boarddebris
        	case 6112: //RA bikenbody
        	case 6117: //RA bikenhead
        	case 6121: //RA bikerhead2
        	case 6127: //RA bikerhand
        	case 7000: //RA babahead
        	case 7005: //RA bababody
        	case 7010: //RA babafoot
        	case 7015: //RA babahand
        	case 7020: //RA debris
        	case 7025: //RA debbris
        	case 7387: //RA deepjibs
        	case 7392: //RA deepjibs2
        	case 7397: //RA deepjibs3
        	case 8890: //RA deep2jibs
        	case 8895: //RA deep2jibs2
                if(ud.lockout != 0)
                {
                    t.xrepeat = t.yrepeat = 0;
                    continue;
                }
                if(t.pal == 6) t.shade = -120;
                if ( shadeEffect[s.sectnum] )
                    t.shade = 16;

            case SCRAP1: //464
            case SCRAP2:
            case SCRAP3:
            case SCRAP4:
            case SCRAP5:
            case SCRAP6:
            case SCRAP6+1:
            case SCRAP6+2:
            case SCRAP6+3:
            case SCRAP6+4:
            case SCRAP6+5:
            case SCRAP6+6:
            case SCRAP6+7:
                if(t.picnum == SCRAP1 && s.yvel > 0)
                    t.picnum = s.yvel;
                else t.picnum += hittype[i].temp_data[0];

                if( sector[sect].floorpal != 0 )
                    t.pal = sector[sect].floorpal;
                break;

            case CHIKENCROSSBOW:
            	k = engine.getangle(s.x-x,s.y-y);
                k = (short) (((s.ang+3072+128-k)&2047)/170);
                if(k>6)
                {
                    k = (short) (12-k);
                    t.cstat |= 4;
                }
                else t.cstat &= ~4;

                t.picnum = (short) (CHIKENCROSSBOW + k);
                break;
            case 3464:
            	t.picnum = (short) (((totalclock >> 4) & 3) + 3464);
            	break;

            case BLOODPOOL:
            case FOOTPRINTS:
            case FOOTPRINTS2:
            case FOOTPRINTS3:
            case FOOTPRINTS4:
                if(t.pal == 6)
                    t.shade = -127;
            case FEATHERS:
            case 1311:
            	if(ud.lockout != 0 && s.pal == 2)
                {
                    t.xrepeat = t.yrepeat = 0;
                    continue;
                }
                break;
            case RESPAWNMARKERRED:
            case 876:
            case 886:
            	t.picnum = (short) (((totalclock >> 4) & 0xD) + 861);
                if ( s.picnum == RESPAWNMARKERRED )
                	t.pal = 0;
                else if ( s.picnum == 876 )
                	t.pal = 1;
                else
                	t.pal = 2;
                if ( ud.marker == 0 )
                	t.xrepeat = t.yrepeat = 0;
            	break;
            case 46:
                t.shade = (byte) (sintable[(totalclock<<4)&2047]>>10);
                continue;
            case WATERBUBBLE:
                if(sector[t.sectnum].floorpicnum == FLOORSLIME)
                {
                    t.pal = 7;
                    break;
                }
            default:
            	if( sector[sect].floorpal != 0 )
            		t.pal = sector[sect].floorpal;
            	break;
            case 4989:
            	k = engine.getangle(s.x-x,s.y-y);
                if( hittype[i].temp_data[0] < 4 )
                    k = (short) (((s.ang+3072+128-k)&2047)/170);
                else k = (short) (((s.ang+3072+128-k)&2047)/170);

                if(k>6)
                {
                    k = (short) (12-k);
                    t.cstat |= 4;
                }
                else t.cstat &= ~4;

                if( klabs(t3) > 64 ) k += 7;
                t.picnum = (short) (4989+k);
            	break;
            case ECLAIRHEALTH:
                t.z -= (4<<8);
                break;
            case CROSSBOW:
                k = engine.getangle(s.x-x,s.y-y);
                k = (short) (((s.ang+3072+128-k)&2047)/170);
                if(k > 6)
                {
                   k = (short) (12-k);
                   t.cstat |= 4;
                }
                else t.cstat &= ~4;
                t.picnum = (short) (CROSSBOW+k);
                break;
            case SHITBALL:
            	if ( sprite[s.owner].picnum == 5120 && sprite[s.owner].pal == 8 )
            		t.picnum = (short) ((totalclock >> 4) % 6 + 3500);
                else if ( sprite[s.owner].picnum == 5120 && sprite[s.owner].pal == 19 )
                {
                	t.picnum = (short) (((totalclock >> 4) & 3) + 5090);
                	t.shade = -127;
                }
                else if ( sprite[s.owner].picnum == 8705 )
                {
                	k = (short) ((((s.ang+3072+128-a)&2047)>>8)&7);
                    if(k>4)
                    {
                        k = (short) (8-k);
                        t.cstat |= 4;
                    }
                    else t.cstat &= ~4;

                    if(sector[t.sectnum].lotag == 2) k += 1795-1405;
                    else if( (hittype[i].floorz-s.z) > (64<<8) ) k += 60;

                    t.picnum = (short) (7274 + k);
                }
                else
                	t.picnum = (short) (((totalclock >> 4) & 3) + SHITBALL);
            	break;
            case CIRCLESAW:
            	if ( sprite[s.owner].picnum != DAISYMAE && sprite[s.owner].picnum != DAISYMAE+1 )
            		t.picnum = (short) (((totalclock >> 4) & 7) + CIRCLESAW);
                else
                {
                	t.picnum = (short) (((totalclock >> 4) & 3) + 3460);
                	t.shade = -127;
                }

            	break;
            case FORCESPHERE:
                if(t.statnum == 5)
                {
                    short sqa,sqb;

                    sqa =
                        engine.getangle(
                            sprite[s.owner].x-ps[screenpeek].posx,
                            sprite[s.owner].y-ps[screenpeek].posy);
                    sqb =
                    		engine.getangle(
                            sprite[s.owner].x-t.x,
                            sprite[s.owner].y-t.y);

                    if( klabs(getincangle(sqa,sqb)) > 512 )
                        if( ldist(sprite[s.owner],t) < ldist(sprite[ps[screenpeek].i],sprite[s.owner]) )
                            t.xrepeat = t.yrepeat = 0;
                }
                continue;
            case LNRDLYINGDEAD:
            	s.xrepeat = 24;
            	s.yrepeat = 17;
            	if ( s.extra > 0 )
            		t.z += 1536;
            	break;

            case APLAYER:

                p = s.yvel;

                if(t.pal == 1) t.z -= (18<<8);

                if(over_shoulder_on > 0 && ps[p].newowner < 0 )
                {
                	t.cstat |= 2;
                    if ( ps[myconnectindex] == ps[p] && numplayers >= 2 )
                    {
                    	 RRNetwork net = game.net;
                        t.x = net.predictOld.x+mulscale((net.predict.x-net.predictOld.x),smoothratio, 16);
                        t.y = net.predictOld.y+mulscale((net.predict.y-net.predictOld.y),smoothratio, 16);
                        t.z = net.predictOld.z+mulscale((net.predict.z-net.predictOld.z),smoothratio, 16)+(40<<8);
                        t.ang = (short) (net.predictOld.ang + (BClampAngle(net.predict.ang+1024-net.predictOld.ang)-1024) * smoothratio / 65536.0f);
                        t.sectnum = net.predict.sectnum;
                    }
                }

                if( ( display_mirror == 1 || screenpeek != p || s.owner == -1 ) && ud.multimode > 1 && ud.showweapons != 0 && sprite[ps[p].i].extra > 0 && ps[p].curr_weapon > 0 )
                {
                	if( tsprite[spritesortcnt] == null )
                		tsprite[spritesortcnt] = new SPRITE();

                    tsprite[spritesortcnt].set(t);
                    tsprite[spritesortcnt].statnum = 99;

                    tsprite[spritesortcnt].yrepeat = (short) ( t.yrepeat>>3 );
                    if(t.yrepeat < 4) t.yrepeat = 4;

                    tsprite[spritesortcnt].shade = t.shade;
                    tsprite[spritesortcnt].cstat = 0;

                    switch(ps[p].curr_weapon)
                    {
                        case 1:  tsprite[spritesortcnt].picnum = 21;      break;
                        case 2:  tsprite[spritesortcnt].picnum = 28;      break;
                        case 3:  tsprite[spritesortcnt].picnum = 22;      break;
                        case 10:
                        case 4:  tsprite[spritesortcnt].picnum = 26;      break;
                        case 5:	 tsprite[spritesortcnt].picnum = 23;      break;
                        case 11:
                        case 6:  tsprite[spritesortcnt].picnum = 3400;    break;
                        case 7:  tsprite[spritesortcnt].picnum = 29;      break;
                        case 8:  tsprite[spritesortcnt].picnum = 27;      break;
                        case 9:  tsprite[spritesortcnt].picnum = 24;      break;
                        case 12: tsprite[spritesortcnt].picnum = 3437;    break;
                    }

                    if(s.owner >= 0)
                        tsprite[spritesortcnt].z = ps[p].posz-(12<<8);
                    else tsprite[spritesortcnt].z = s.z-(51<<8);
                    if(ps[p].curr_weapon == 4)
                    {
                        tsprite[spritesortcnt].xrepeat = 10;
                        tsprite[spritesortcnt].yrepeat = 10;
                    }
                    else
                    {
                        tsprite[spritesortcnt].xrepeat = 16;
                        tsprite[spritesortcnt].yrepeat = 16;
                    }
                    tsprite[spritesortcnt].pal = 0;
                    spritesortcnt++;
                }

                if(s.owner == -1)
                {
                    k = (short) ((((s.ang+3072+128-a)&2047)>>8)&7);
                    if(k>4)
                    {
                        k = (short) (8-k);
                        t.cstat |= 4;
                    }
                    else t.cstat &= ~4;

                    if(sector[t.sectnum].lotag == 2) k += 1795-1405;
                    else if( (hittype[i].floorz-s.z) > (64<<8) ) k += 60;

                    t.picnum += k;
                    t.pal = ps[p].palookup;

                    if( sector[sect].floorpal != 0 )
	                    t.pal = sector[sect].floorpal;

                    continue;
                }

                if( ps[p].on_crane == -1 && (sector[s.sectnum].lotag&0x7ff) != 1 )
                {
                    l = s.z-hittype[ps[p].i].floorz+(3<<8);
                    if( l > 1024 && s.yrepeat > 32 && s.extra > 0 )
                    	t.yoffset = (short) (l/(t.yrepeat<<2)); //GDX 24.10.2018 multiplayer unsync
                    else t.yoffset=0;
                }

                if(ps[p].newowner > -1)
                {
                    t4 = currentGame.getCON().script[currentGame.getCON().actorscrptr[APLAYER]+1];
                    t3 = 0;
                    t1 = currentGame.getCON().script[currentGame.getCON().actorscrptr[APLAYER]+2];
                }

                if(ud.camerasprite == -1 && ps[p].newowner == -1)
                    if(s.owner >= 0 && display_mirror == 0 && over_shoulder_on == 0 )
                        if( ud.multimode < 2 || ( ud.multimode > 1 && p == screenpeek ) )
                {
                    t.owner = -1;
                    t.xrepeat = t.yrepeat = 0;
                    continue;
                }

                if( sector[sect].floorpal != 0 )
                    t.pal = sector[sect].floorpal;

                if(s.owner == -1) continue;

                if( t.z > hittype[i].floorz && t.xrepeat < 32 )
                    t.z = hittype[i].floorz;

                /*if ( ps[p].OnMotorcycle && p == screenpeek )
                {
                	t.picnum = 7219;
                	t.xrepeat = 18;
                	t.yrepeat = 18;
                	t4 = 0;
                	t3 = 0;
                	t1 = 0;
                }
                else */if ( ps[p].OnMotorcycle )
                {
                	k = engine.getangle(s.x-x,s.y-y);
	                k = (short) (((s.ang+3072+128-k)&2047)/170);
	                if(k>6)
	                {
	                    k = (short) (12-k);
	                    t.cstat |= 4;
	                }
	                else t.cstat &= ~4;

	                t.picnum = (short) (7213 + k);
	                t.xrepeat = 18;
                	t.yrepeat = 18;
                	t4 = 0;
                	t3 = 0;
                	t1 = 0;
                }
                /*else if ( ps[p].OnBoat && p == screenpeek )
                {
                	t.picnum = 7190;
                	t.xrepeat = 32;
                	t.yrepeat = 32;
                	t4 = 0;
                	t3 = 0;
                	t1 = 0;
                }
                else */if ( ps[p].OnBoat )
                {
                	k = engine.getangle(s.x-x,s.y-y);
	                k = (short) (((s.ang+3072+128-k)&2047)/170);
	                if(k>6)
	                {
	                    k = (short) (12-k);
	                    t.cstat |= 4;
	                }
	                else t.cstat &= ~4;

	                t.picnum = (short) (7184 + k);
	                t.xrepeat = 32;
                	t.yrepeat = 32;
                	t4 = 0;
                	t3 = 0;
                	t1 = 0;
                }

                int tx = t.x - x;
				int ty = t.y - y;
				int angle = ((1024 + engine.getangle(tx, ty) - a) & kAngleMask) - 1024;
				long dist = engine.qdist(tx, ty);

				if(klabs(mulscale(angle, dist, 14)) < 4) {
					int horizoff = (int) (100-ps[screenpeek].horiz);
					long z1 = mulscale(dist, horizoff, 3) + z;

					int zTop = t.z;
					int zBot = zTop;
					Tile pic = engine.getTile(APLAYER);

					int yoffs = pic.getOffsetY();
					zTop -= (yoffs + pic.getHeight()) * (t.yrepeat << 2);
					zBot += -yoffs * (t.yrepeat << 2);

					if ((z1 < zBot) && (z1 > zTop))
					{
						if(engine.cansee(x, y, z, sprite[ps[screenpeek].i].sectnum, t.x, t.y, t.z, t.sectnum))
							gPlayerIndex = t.yvel;
					}
				}

                break;
	        case 27:
	        	continue;
	        case MOTORCYCLE:
	        	k = engine.getangle(s.x-x,s.y-y);
                k = (short) (((s.ang+3072+128-k)&2047)/170);
                if(k>6)
                {
                    k = (short) (12-k);
                    t.cstat |= 4;
                }
                else t.cstat &= ~4;

                t.picnum = (short) (MOTORCYCLE + k);
                break;
	        case SWAMPBUGGY:
	        	k = engine.getangle(s.x-x,s.y-y);
                k = (short) (((s.ang+3072+128-k)&2047)/170);
                if(k>6)
                {
                    k = (short) (12-k);
                    t.cstat |= 4;
                }
                else t.cstat &= ~4;
                t.picnum = (short) (SWAMPBUGGY + k);
                break;
	        }

	        if( currentGame.getCON().actorscrptr[s.picnum] != 0 && (t.cstat & 0x30) != 48 )
	        {
	            if(t4 != 0)
	            {
	                l = currentGame.getCON().script[t4+2];
	                switch( l )
	                {
	                    case 2:
	                        k = (short) ((((s.ang+3072+128-a)&2047)>>8)&1);
	                        break;

	                    case 3:
	                    case 4:
	                        k = (short) ((((s.ang+3072+128-a)&2047)>>7)&7);
	                        if(k > 3)
	                        {
	                            t.cstat |= 4;
	                            k = (short) (7-k);
	                        }
	                        else t.cstat &= ~4;
	                        break;

	                    case 5:
	                        k = engine.getangle(s.x-x,s.y-y);
	                        k = (short) ((((s.ang+3072+128-k)&2047)>>8)&7);
	                        if(k>4)
	                        {
	                            k = (short) (8-k);
	                            t.cstat |= 4;
	                        }
	                        else t.cstat &= ~4;
	                        break;
	                    case 7:
	                        k = engine.getangle(s.x-x,s.y-y);
	                        k = (short) (((s.ang+3072+128-k)&2047)/170);
	                        if(k>6)
	                        {
	                            k = (short) (12-k);
	                            t.cstat |= 4;
	                        }
	                        else t.cstat &= ~4;
	                        break;
	                    case 8:
	                        k = (short) ((((s.ang+3072+128-a)&2047)>>8)&7);
	                        t.cstat &= ~4;
	                        break;
	                    default:
	                        if(badguy(s) && s.statnum == 2 && s.extra > 0)
	                        {
	                        	k = engine.getangle(s.x-x,s.y-y);
		                        k = (short) ((((s.ang+3072+128-k)&2047)>>8)&7);
		                        if(k>4)
		                        {
		                            k = (short) (8-k);
		                            t.cstat |= 4;
		                        }
		                        else t.cstat &= ~4;
	                        } else {
	                        	k = 0;
	                        }
	                        break;
	                }

	                t.picnum += (k + ( currentGame.getCON().script[t4] ) + l * t3);

	                if(l > 0) while(t.picnum > 0 && t.picnum < MAXTILES && engine.getTile(t.picnum).getWidth() == 0)
	                    t.picnum -= l;       //Hack, for actors

	                if( hittype[i].dispicnum >= 0)
	                    hittype[i].dispicnum = t.picnum;
	            }
	            else if(display_mirror == 1)
	                t.cstat |= 4;
	        }
	        if ( s.picnum == 5015 )
	            t.shade = -127;

	        if( s.statnum == 13 || badguy(s) || (s.picnum == APLAYER && s.owner >= 0) )
	            if((s.cstat & 0x30) == 0 && t.statnum != 99 && s.picnum != EXPLOSION2 && s.picnum != 1080 && s.picnum != TORNADO && s.picnum != EXPLOSION3 && s.picnum != 5015)
	        {
	            if( hittype[i].dispicnum < 0 )
	            {
	                hittype[i].dispicnum++;
	                continue;
	            }
	            else if( ud.shadows != 0 && spritesortcnt < (MAXSPRITESONSCREEN-2))
	            {
	                int daz,xrep,yrep;
	                if ( sector[sect].lotag == 160 )
	                    continue;

	                if( (sector[sect].lotag&0xff) > 2 || s.statnum == 4 || s.statnum == 5 || s.picnum == MOSQUITO )
	                    daz = sector[sect].floorz;
	                else
	                    daz = hittype[i].floorz;

	                if( (s.z-daz) < (8<<8) )
	                    if( ps[screenpeek].posz < daz )
	                {
                    	if(tsprite[spritesortcnt] == null)
	                    	tsprite[spritesortcnt] = new SPRITE();
	                    SPRITE tspr = tsprite[spritesortcnt];
	                    tspr.set(t);
	                    int camangle = engine.getangle(x - tspr.x, y - tspr.y);
	                    tspr.x -= mulscale(sintable[(camangle + 512)& 2047], 10, 16);
	                    tspr.y += mulscale(sintable[(camangle + 1024) & 2047], 10, 16);
	                    tspr.statnum = 99;

	                    tspr.yrepeat = (short) ( t.yrepeat>>3 );
	                    if(t.yrepeat < 4) t.yrepeat = 4;

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
	        }

	        switch(s.picnum)
	        {
	        	case 2944:
	        		t.shade = -127;
	        		t.picnum = (short) (((totalclock >> 2) & 4) + 2944);
	        		break;
	        	case MUD:
	        		t.picnum = (short) (t1 + MUD);
	        		break;
	        	case FRAMEEFFECT1:
	                if(s.owner >= 0 && sprite[s.owner].statnum < MAXSTATUS)
	                {
	                    if(sprite[s.owner].picnum == APLAYER)
	                        if(ud.camerasprite == -1)
	                            if(screenpeek == sprite[s.owner].yvel && display_mirror == 0)
	                    {
	                        t.owner = -1;
	                        break;
	                    }
	                    if( (sprite[s.owner].cstat&32768) == 0 )
	                    {
	                    	if ( sprite[s.owner].picnum == APLAYER )
	                            t.picnum = 1554;
	                        else t.picnum = (short) hittype[s.owner].dispicnum;
	                        t.pal = sprite[s.owner].pal;
	                        t.shade = sprite[s.owner].shade;
	                        t.ang = sprite[s.owner].ang;
	                        t.cstat = (short) (2|sprite[s.owner].cstat);
	                    }
	                }
	                break;
	        	 case PLAYERONWATER:
		                k = (short) ((((t.ang+3072+128-a)&2047)>>8)&7);
		                if(k>4)
		                {
		                    k = (short) (8-k);
		                    t.cstat |= 4;
		                }
		                else t.cstat &= ~4;

		                t.picnum = (short) (s.picnum+k+((hittype[i].temp_data[0]<4)?5:0));
		                t.shade = sprite[s.owner].shade;

		                break;
	        	 case 1409:
	        	 case EXPLOSION2:
	        	 case 1442:
	        	 case CROSSBOW:
	        	 case 2095:
	        	 case 3380:
	        	 case CIRCLESAW:
	        	 case FIRELASER:
	        	 case 3471:
	        	 case 3475:
	        	 case 5595:
	        		 if(t.picnum == EXPLOSION2)
	        		 {
	        			 gVisibility = -127;
	        			 lastvisinc = totalclock+32;
	        			 t.pal = 0;
	        		 }
	        		 else if(t.picnum == FIRELASER)
	        			 t.picnum = (short) (((totalclock >> 2) & 5) + FIRELASER);

	                t.shade = -127;
	                break;
	        	 case 1878:
	        	 case 1952:
	        	 case 1953:
	        	 case 1990:
	        	 case 2050:
	        	 case 2056:
	        	 case 2072:
	        	 case 2075:
	        	 case 2083:
	        	 case 2097:
	        	 case 2156:
	        	 case 2157:
	        	 case 2158:
	        	 case 2159:
	        	 case 2160:
	        	 case 2161:
	        	 case 2175:
	        	 case 2176:
	        	 case 2357:
	        	 case 2564:
	        	 case 2573:
	        	 case 2574:
	        	 case 2583:
	        	 case 2604:
	        	 case 2689:
	        	 case 2893:
	        	 case 2894:
	        	 case 2915:
	        	 case 2945:
	        	 case 2946:
	        	 case 2947:
	        	 case 2948:
	        	 case 2949:
	        	 case 2977:
	        	 case 2978:
	        	 case 3116:
	        	 case 3171:
	        	 case 3216:
	        	 case 3720:

	        		 //RA
	        	 case 3668:
	        	 case 3795:
	        	 case 5035:
	        	 case 7505:
	        	 case 7506:
	        	 case 7533:
	        	 case 8216:
	        	 case 8218:
	        	 case 8220:
	        		 t.shade = -127;
	        		 break;
	        	 case UFOBEAM:
	        	 case 297: //GDX 23.04.2019 UFO TELE B
	        	 case 3586:
	        	 case 3587:
	        		 t.cstat |= 32768;
	        		 s.cstat |= 32768;
	        		 break;
	        	 case 36:
	        		 t.cstat |= 32768;
	        		 break;
	        	 case 1107:
	        		 t.picnum = (short) (s.picnum + hittype[i].temp_data[2]);
	        		 break;
	        	 case CAMERA1:
	        	 case RAT:
	        		 k = (short) ((((t.ang+3072+128-a)&2047)>>8)&7);
	        		 if(k>4)
	        		 {
	        			 k = (short) (8-k);
	        			 t.cstat |= 4;
	        		 }
	        		 else t.cstat &= ~4;
	        		 t.picnum = (short) (s.picnum+k);
	        		 break;
	        	 case 2034:
	                 t.picnum = (short) ((totalclock & 1) + 2034);
	        		 break;
	        	 case WATERSPLASH2:
	        		 t.picnum = (short) (WATERSPLASH2+t1);
	        		 break;
	        	 case BURNING:
	        	 case FIRE:
	        		 if( sprite[s.owner].picnum != 1191 && sprite[s.owner].picnum != 1193 )
	        			 t.z = sector[t.sectnum].floorz;
	        		 t.shade = -127;
	        		 break;
	        	 case SHELL:
	        	 case SHOTGUNSHELL:
	        		 t.picnum = (short) (s.picnum+(hittype[i].temp_data[0]&1));
	        		 break;

	        		 //RA
	        	 case BILLYRAY:
	        	 case BILLYRAYSTAYPUT:
	        		 if ( t.picnum >= 4167 && t.picnum <= 4171 )
	        			 t.shade = -127;
	        		 break;
	        	 case MINION:
	        		 if ( t.pal == 19 )
	                     t.shade = -127;
	        		 break;
	        	 case BIKERSTAND:
	        		 if ( t.picnum >= 6049 && t.picnum <= 6053 )
	                    t.shade = -127;
	        		 else if ( t.picnum >= 6079 && t.picnum <= 6083 )
	                    t.shade = -127;
	        		 break;
	        	 case DAISYMAE:
	        		 if(t.picnum >= 6760 && t.picnum <= 6809 )
	        			 t.shade = -127;
	        		 break;
	        }

	        hittype[i].dispicnum = t.picnum;
	        if(sector[t.sectnum].floorpicnum == MIRROR)
	            t.xrepeat = t.yrepeat = 0;
	    }
	}

	public static void se40code(int x,int y,int z,float a,float h, int smoothratio)
	{
	    for( int i = headspritestat[15]; i >= 0;  i = nextspritestat[i])
	    {
	        switch(sprite[i].lotag)
	        {
	        	case 150: //floor
	        	case 151: //ceiling
	        		SE40_Draw(i,x,y,z,a,h,smoothratio);
	        		break;
	        }
	    }
	}

	private static int[] tempsectorz = new int[MAXSECTORS];
	private static short[] tempsectorpicnum = new short[MAXSECTORS];

	private static void SE40_Old(int spnum,int x,int y,int z,float a,float h,int smoothratio)
	{
		int i=0,j=0,k=0;
		int floor1=0,floor2=0,fofmode=0;
		long offx,offy;

		if(sprite[spnum].ang!=512) return;

		i = 13;    //Effect TILE
		if ((gotpic[i>>3]&(1<<(i&7))) == 0) return;
		gotpic[i>>3] &= ~(1<<(i&7));

		floor1=spnum;

		if(sprite[spnum].lotag==152) fofmode=150;
		if(sprite[spnum].lotag==153) fofmode=151;

        for( j = headspritestat[15]; j >= 0;  j = nextspritestat[j])
		{
			if(sprite[j].picnum==1 &&
					sprite[j].lotag==fofmode &&
					sprite[j].hitag==sprite[floor1].hitag)
			{ floor1=j; fofmode=sprite[j].lotag; break;}
		}

		if(fofmode==150) k=151; else k=150;

		for( j = headspritestat[15]; j >= 0;  j = nextspritestat[j])
		{
			if(sprite[j].picnum==1 &&
					sprite[j].lotag==k &&
					sprite[j].hitag==sprite[floor1].hitag)
			{floor2=j; break;}
		}

		for( j = headspritestat[15]; j >= 0;  j = nextspritestat[j])  // raise ceiling or floor
		{
			if(sprite[j].picnum==1 &&
					sprite[j].lotag==k+2 &&
					sprite[j].hitag==sprite[floor1].hitag)
			{
				if(k==150)
				{
					tempsectorz[sprite[j].sectnum]=sector[sprite[j].sectnum].floorz;
					sector[sprite[j].sectnum].floorz+=(((z-sector[sprite[j].sectnum].floorz)/32768)+1)*32768;
					tempsectorpicnum[sprite[j].sectnum]=sector[sprite[j].sectnum].floorpicnum;
					sector[sprite[j].sectnum].floorpicnum=13;
				}
				if(k==151)
				{
					tempsectorz[sprite[j].sectnum]=sector[sprite[j].sectnum].ceilingz;
					sector[sprite[j].sectnum].ceilingz+=(((z-sector[sprite[j].sectnum].ceilingz)/32768)-1)*32768;
					tempsectorpicnum[sprite[j].sectnum]=sector[sprite[j].sectnum].ceilingpicnum;
					sector[sprite[j].sectnum].ceilingpicnum=13;
				}
			}
		}

		i=floor1;
		offx=x-sprite[i].x;
		offy=y-sprite[i].y;
		i=floor2;
		engine.drawrooms(offx+sprite[i].x,offy+sprite[i].y,z,a,h,sprite[i].sectnum);
		animatesprites(x,y,z,(short)a,smoothratio);
		engine.drawmasks();

		for( j = headspritestat[15]; j >= 0;  j = nextspritestat[j])  // restore ceiling or floor
		{
			if(sprite[j].picnum==1 &&
					sprite[j].lotag==k+2 &&
					sprite[j].hitag==sprite[floor1].hitag)
			{
				if(k==150)
				{
					sector[sprite[j].sectnum].floorz=tempsectorz[sprite[j].sectnum];
					sector[sprite[j].sectnum].floorpicnum=tempsectorpicnum[sprite[j].sectnum];
				}
				if(k==151)
				{
					sector[sprite[j].sectnum].ceilingz=tempsectorz[sprite[j].sectnum];
					sector[sprite[j].sectnum].ceilingpicnum=tempsectorpicnum[sprite[j].sectnum];
				}
			}// end if
		}// end for
	} // end SE40

	public static byte[] oldgotsector = new byte[MAXSECTORS>>3];
	public static void SE40_Draw(int spnum,int x,int y,int z,float a,float h,int smoothratio)
	{
	    int i = headspritestat[15];
	    while(i >= 0)
	    {
	        switch(sprite[i].lotag)
	        {
	            case 152:
	            case 153:
	                if(ps[screenpeek].cursectnum == sprite[i].sectnum)
	                    SE40_Old(i,x,y,z,a,h,smoothratio);
	                break;
	        }
	        i = nextspritestat[i];
	    }

    }

	public static void addmessage(String message) {
		buildString(currentGame.getCON().fta_quotes[122], 0, message);
		FTA(122,ps[myconnectindex]);
	}

	public static void viewDrawStats(int x, int y, int zoom, boolean topAligned)
	{
		if(cfg.gShowStat == 0)
			return;

		float viewzoom = (zoom / 65536.0f);

		buildString(buffer, 0, "kills:   ");
		int alignx = game.getFont(1).getWidth(buffer);

		if(!topAligned) {
			int yoffset = (int) (3f * game.getFont(1).getHeight() * viewzoom);
			y -= yoffset;
		}

		int statx = x;
		int staty = y;

		game.getFont(1).drawText(statx, staty, buffer, zoom, 0, 2, TextAlign.Left, 10 | 256, false);

		int offs = Bitoa(ps[connecthead].actors_killed, buffer);
		offs = buildString(buffer, offs, " /   ", ps[connecthead].max_actors_killed);
		game.getFont(1).drawText(statx += (alignx + 6) * viewzoom, staty, buffer, zoom, 0, 15, TextAlign.Left, 10 | 256, false);

		statx = x;
		staty = y + (int) (12 * viewzoom);

		buildString(buffer, 0, "secrets:    ");
		game.getFont(1).drawText(statx, staty, buffer, zoom, 0, 2, TextAlign.Left, 10 | 256, false);
		alignx = game.getFont(1).getWidth(buffer);
		offs = Bitoa(ps[connecthead].secret_rooms, buffer);
		offs = buildString(buffer, offs, " /   ", ps[connecthead].max_secret_rooms);
		game.getFont(1).drawText(statx += (alignx + 6) * viewzoom, staty, buffer, zoom, 0, 15, TextAlign.Left, 10 | 256, false);

		statx = x;
		staty = y + (int) (22 * viewzoom);

		buildString(buffer, 0, "time:    ");
		game.getFont(1).drawText(statx, staty, buffer, zoom, 0, 2, TextAlign.Left, 10 | 256, false);
		alignx = game.getFont(1).getWidth(buffer);

		int minutes = ps[myconnectindex].player_par/(26*60);
		int sec = (ps[myconnectindex].player_par/26)%60;

		offs = Bitoa(minutes, buffer, 2);
		offs = buildString(buffer, offs, " :   ", sec, 2);

		game.getFont(1).drawText(statx += (alignx + 6) * viewzoom, staty, buffer, zoom, 0, 15, TextAlign.Left, 10 | 256, false);
	}

	private static PlayerOrig viewout = new PlayerOrig();
	public static PlayerOrig view(PlayerStruct pp, int vx, int vy,int vz,short vsectnum, float ang, float horiz)
	{
	     viewout.ox = vx;
	     viewout.oy = vy;
	     viewout.oz = vz;
	     viewout.os = vsectnum;

	     int nx = (int) (-BCosAngle(ang) / 16.0f);
	     int ny = (int) (-BSinAngle(ang) / 16.0f);
	     int nz = (int) ((horiz-100)*128 - 4096);

	     SPRITE sp = sprite[pp.i];

	     short bakcstat = sp.cstat;
	     sp.cstat &= ~0x101;

	     vsectnum = engine.updatesectorz(vx,vy,vz,vsectnum);

	     engine.hitscan(vx,vy,vz,vsectnum,nx,ny,nz,pHitInfo,CLIPMASK1);
	     int hitx = pHitInfo.hitx, hity = pHitInfo.hity;

	     if(vsectnum < 0)
	     {
	        sp.cstat = bakcstat;
	        return viewout;
	     }

	     int hx = hitx-(vx);
	     int hy = hity-(vy);
	     if( (klabs(hx) + klabs(hy)) - (klabs(nx) + klabs(ny)) < 1024)
	     {
			int wx = 1; if(nx < 0) wx = -1;
			int wy = 1; if(ny < 0) wy = -1;

			hx -= wx << 9;
			hy -= wy << 9;

			int dist = 0;
			if(nx != 0 && ny != 0) {
				if(klabs(nx) > klabs(ny))
					dist = divscale(hx,nx,16);
				else dist = divscale(hy,ny,16);
			}

			if (dist < cameradist) cameradist = dist;
	     }

	     vx += mulscale(nx,cameradist,16);
	     vy += mulscale(ny,cameradist,16);
	     vz += mulscale(nz,cameradist,16);

	     cameradist = min(cameradist+((totalclock-cameraclock)<<10),65536);
	     cameraclock = totalclock;

	     vsectnum = engine.updatesectorz(vx,vy,vz,vsectnum);

	     sp.cstat = bakcstat;

	     viewout.ox = vx;
	     viewout.oy = vy;
	     viewout.oz = vz;
	     viewout.os = vsectnum;

	     return viewout;
	}

	public static void coords(short snum)
	{
	    short y = 0;

	    if(ud.coop != 1)
	    {
	        if(ud.multimode > 1 && ud.multimode < 5)
	            y = 8;
	        else if(ud.multimode > 4)
	            y = 16;
	    }

	    buildString(buffer, 0, "X= ", ps[snum].posx);
	    engine.printext256(250,y,31,-1,buffer,1, 1.0f);
	    buildString(buffer, 0, "Y= ", ps[snum].posy);
	    engine.printext256(250,y+7,31,-1,buffer,1, 1.0f);
	    buildString(buffer, 0, "Z= ", ps[snum].posz);
	    engine.printext256(250,y+14,31,-1,buffer,1, 1.0f);
	    buildString(buffer, 0, "A= ", (int)ps[snum].ang);
	    engine.printext256(250,y+21,31,-1,buffer,1, 1.0f);
	    buildString(buffer, 0, "ZV= ", ps[snum].poszv);
	    engine.printext256(250,y+28,31,-1,buffer,1, 1.0f);
	    buildString(buffer, 0, "OG= ", ps[snum].on_ground?1:0);
	    engine.printext256(250,y+35,31,-1,buffer,1, 1.0f);
	    buildString(buffer, 0, "LFW= ", ps[snum].last_full_weapon);
	    engine.printext256(250,y+50,31,-1,buffer,1, 1.0f);
	    buildString(buffer, 0, "SECTL= ", sector[ps[snum].cursectnum].lotag);
	    engine.printext256(250,y+57,31,-1,buffer,1, 1.0f);
	    buildString(buffer, 0, "SEED= ", engine.getrand());
	    engine.printext256(250,y+64,31,-1,buffer,1, 1.0f);
	    buildString(buffer, 0, "THOLD= ", ps[snum].transporter_hold);
	    engine.printext256(250,y+64+7,31,-1,buffer,1, 1.0f);
	}

	public static void displaygeom3d(int sectnum, int cposx, int cposy, int cposz,  float choriz, float cang, int csect, int smoothratio)
	{
		if(isValidSector(sectnum)  && sector[sectnum].lotag == 848)
		{
	        short geomsect = 0;

	        for(short i = 0; i < numgeomeffects; i++)
	        {
	        	short k = headspritesect[geomsector[i]];
	        	while(k != -1)
	        	{
	        		short nextk = nextspritesect[k];
	        		engine.changespritesect(k, geoms1[i]);
	        		engine.setsprite(k, sprite[k].x + geomx1[i], sprite[k].y + geomy1[i], sprite[k].z);
	        		k = nextk;
	        	}
	        	if ( csect == geomsector[i] )
	        		geomsect = i;
	        }

	        engine.drawrooms(cposx - geomx1[geomsect], cposy - geomy1[geomsect], cposz, cang, choriz, geomsect);

	        for(short i = 0; i < numgeomeffects; i++)
	        {
	        	short k = headspritesect[geoms1[i]];
	        	while(k != -1)
	        	{
	        		short nextk = nextspritesect[k];
	        		engine.changespritesect(k, geomsector[i]);
	        		engine.setsprite(k, sprite[k].x - geomx1[i], sprite[k].y - geomy1[i], sprite[k].z);
	        		k = nextk;
	        	}
	        }

	        animatesprites(cposx, cposy, cposz, (short) cang, smoothratio);
	        engine.drawmasks();

	        for(short i = 0; i < numgeomeffects; i++)
	        {
	        	short k = headspritesect[geomsector[i]];
	        	while(k != -1)
	        	{
	        		short nextk = nextspritesect[k];
	        		engine.changespritesect(k, geoms2[i]);
	        		engine.setsprite(k, sprite[k].x + geomx2[i], sprite[k].y + geomy2[i], sprite[k].z);
	        		k = nextk;
	        	}
	        	if ( csect == geomsector[i] )
	        		geomsect = i;
	        }

	        engine.drawrooms(cposx - geomx2[geomsect], cposy - geomy2[geomsect], cposz, cang, choriz, geomsect);

	        for(short i = 0; i < numgeomeffects; i++)
	        {
	        	short k = headspritesect[geoms2[i]];
	        	while(k != -1)
	        	{
	        		short nextk = nextspritesect[k];
	        		engine.changespritesect(k, geomsector[i]);
	        		engine.setsprite(k, sprite[k].x - geomx2[i], sprite[k].y - geomy2[i], sprite[k].z);
	        		k = nextk;
	        	}
	        }

	        animatesprites(cposx, cposy, cposz, (short) cang, smoothratio);
	        engine.drawmasks();
		}
	}

	public static void displayinventory(PlayerStruct p)
	{
	    int n, j, xoff, y;

	    j = xoff = 0;

	    n = (p.cowpie_amount > 0)?1<<3:0; if((n&8) != 0) j++;
	    n |= ( p.snorkle_amount > 0 )?1<<5:0; if((n&32) != 0) j++;
	    n |= (p.moonshine_amount > 0)?1<<1:0; if((n&2) != 0) j++;
	    n |= ( p.beer_amount > 0)?1<<2:0; if((n&4) != 0) j++;
	    n |= (p.whishkey_amount > 0)?1:0; if((n&1) != 0) j++;
	    n |= (p.yeehaa_amount > 0)?1<<4:0; if((n&16) != 0) j++;
	    n |= (p.boot_amount > 0)?1<<6:0; if((n&64) != 0) j++;

	    xoff = 160-(j*11);

	    j = 0;

	    y = 134;
//	    if(ud.screen_size > 2)
//	        y = 134;
//	    else y = 178;

//	    if(ud.screen_size == 1) GDX 17.04.2019 - disabled, because has a gutmeter

        if((p.gotkey[0]|p.gotkey[1]|p.gotkey[2]) != 0)
	        xoff += engine.getTile(KEYSIGN).getWidth() / 4;

	    while( j <= 9 )
	    {
	        if( (n&(1<<j)) != 0 )
	        {
	            switch( n&(1<<j) )
	            {
	                case   1:
	                	engine.rotatesprite((xoff+4)<<16,y<<16,32768,0,WHISHKEY_ICON,0,0,10+16,windowx1,windowy1,windowx2,windowy2);break;
	                case   2:
	                	engine.rotatesprite((xoff+4)<<16,y<<16,32768,0,MOONSHINE_ICON,0,0,10+16,windowx1,windowy1,windowx2,windowy2);break;
	                case   4:
	                	engine.rotatesprite((xoff)<<16,(y+2)<<16,32768,0, BEER_ICON,0,0,10+16,windowx1,windowy1,windowx2,windowy2);break;
	                case   8:
	                	engine.rotatesprite((xoff-2)<<16,(y+5)<<16,32768,0,COWPIE_ICON,0,0,10+16,windowx1,windowy1,windowx2,windowy2);break;
	                case  16:
	                	engine.rotatesprite((xoff-2)<<16,y<<16,32768,0, EMPTY_ICON,0,0,10+16,windowx1,windowy1,windowx2,windowy2);break;
	                case  32:
	                	engine.rotatesprite((xoff)<<16,y<<16,32768,0,SNORKLE_ICON,0,0,10+16,windowx1,windowy1,windowx2,windowy2);break;
	                case 64:
	                	engine.rotatesprite((xoff+4)<<16,(y-1)<<16,32768,0,BOOT_ICON,0,0,10+16,windowx1,windowy1,windowx2,windowy2);break;
	            }

	            xoff += 22;

	            if(p.inven_icon == j+1)
	            	engine.rotatesprite((xoff)<<16,(y+20)<<16,32768,1024,ARROW,-32,0,10+16,windowx1,windowy1,windowx2,windowy2);
	        }

	        j++;
	    }
	}

	public static int lastvisinc;
	public static void displaymasks(short snum)
	{
	    int p = sector[ps[snum].cursectnum].floorpal;

	    if(sprite[ps[snum].i].pal == 1)
	        p = 1;
	    if(ps[snum].scuba_on != 0)
		{
	    	Tile pic = engine.getTile(3374);
        	engine.rotatesprite(
    	            (320 - (pic.getWidth() >> 1) - 15) << 16,
    	            (200 - (pic.getHeight() >> 1) + (sintable[totalclock & 0x7FF] >> 10)) << 16,
    	            49152,0,3374,0,p,10 | 16 |512,
    	            windowx1, windowy1, windowx2, windowy2);

        	pic = engine.getTile(3377);
        	int framesx = xdim / pic.getWidth();

			int x = -pic.getWidth()/2;
			for(int i = 0; i <= framesx; i++) {
				engine.rotatesprite(x<<16, (-1)<<16, 65536, 0, 3377, 0, p, 10 | 16, 0, 0, xdim-1, ydim-1);
	        	engine.rotatesprite(x<<16, 200<<16, 65536, 0, 3377, 0, p, 4 | 10| 16, 0, 0, xdim-1, ydim-1);
		    	x += pic.getWidth() - 1;
		    }

			pic = engine.getTile(3378);
    	    engine.rotatesprite(
    	            (320 - (pic.getWidth())) << 16,
    	            (200 - (pic.getHeight())) << 16,
    	            65536, 0, 3378, 0, p, 10 | 16 |512,
    	            windowx1, windowy1, windowx2, windowy2);
    	    engine.rotatesprite(
    	    		pic.getWidth() << 16,
    	            (200 - (pic.getHeight())) << 16,
    	            65536,1024,3378,0,p, 4 | 10 | 16 |256,
    	            windowx1, windowy1, windowx2, windowy2);

		}
	}
}
