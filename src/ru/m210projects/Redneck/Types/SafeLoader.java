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

package ru.m210projects.Redneck.Types;

import static ru.m210projects.Build.Engine.MAXPSKYTILES;
import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.MAXWALLS;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Redneck.LoadSave.*;
import static ru.m210projects.Redneck.ResourceHandler.*;
import static ru.m210projects.Redneck.Gamedef.MAXSCRIPTSIZE;
import static ru.m210projects.Redneck.Globals.MAXANIMWALLS;
import static ru.m210projects.Redneck.Globals.MAXCYCLERS;
import static ru.m210projects.Redneck.Animate.MAXANIMATES;

import ru.m210projects.Build.Strhandler;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;

public class SafeLoader {

	public String boardfilename;
	public GameInfo addon;
	public String addonFileName;
	public byte warp_on;
	
	public short spriteq[] = new short[1024],spriteqloc,spriteqamount=64;
	public short numanimwalls;
	public int[] msx = new int[2048],msy = new int[2048];
	public short cyclers[][] = new short[MAXCYCLERS][6], numcyclers;
	
	public short mirrorwall[] = new short[64], mirrorsector[] = new short[64], mirrorcnt;
	
	public Animwalltype animwall[] = new Animwalltype[MAXANIMWALLS];
	public PlayerOrig po[] = new PlayerOrig[MAXPLAYERS];
	public PlayerStruct ps[] = new PlayerStruct[MAXPLAYERS];
	public Weaponhit[] hittype = new Weaponhit[MAXSPRITES];
	
	public byte[] show2dsector = new byte[(MAXSECTORS + 7) >> 3];
	
	public boolean shadeEffect[] = new boolean[MAXSECTORS];

	public final int MAXJAILDOORS = 32;
	public int jailspeed[] = new int[MAXJAILDOORS];
	public int jaildistance[] = new int[MAXJAILDOORS];
	public short jailsect[] = new short[MAXJAILDOORS];
	public short jaildirection[] = new short[MAXJAILDOORS];
	public short jailunique[] = new short[MAXJAILDOORS];
	public short jailsound[] = new short[MAXJAILDOORS];
	public short jailstatus[] = new short[MAXJAILDOORS];
	public int jailcount2[] = new int[MAXJAILDOORS];
	
	public final int MAXMINECARDS = 16;
	public int minespeed[] = new int[MAXMINECARDS];
	public int minefulldist[] = new int[MAXMINECARDS];
	public int minedistance[] = new int[MAXMINECARDS];
	public short minechild[] = new short[MAXMINECARDS];
	public short mineparent[] = new short[MAXMINECARDS];
	public short minedirection[] = new short[MAXMINECARDS];
	public short minesound[] = new short[MAXMINECARDS];
	public short minestatus[] = new short[MAXMINECARDS];
	
	public final int MAXTORCHES = 64;
	public short torchsector[] = new short[MAXTORCHES];
	public byte torchshade[] = new byte[MAXTORCHES];
	public short torchflags[] = new short[MAXTORCHES];
	
	public final int MAXLIGHTNINS = 64;
	public short lightninsector[] = new short[MAXLIGHTNINS];
	public short lightninshade[] = new short[MAXLIGHTNINS];
	
	public final int MAXAMBIENTS = 64;
	public short ambienttype[] = new short[MAXAMBIENTS];
	public short ambientid[] = new short[MAXAMBIENTS];
	public short ambienthitag[] = new short[MAXAMBIENTS];
	
	public final int MAXGEOMETRY = 64;
	public short geomsector[] = new short[MAXGEOMETRY];
	public short geoms1[] = new short[MAXGEOMETRY];
	public int geomx1[] = new int[MAXGEOMETRY];
	public int geomy1[] = new int[MAXGEOMETRY];
	public int geomz1[] = new int[MAXGEOMETRY];
	public short geoms2[] = new short[MAXGEOMETRY];
	public int geomx2[] = new int[MAXGEOMETRY];
	public int geomy2[] = new int[MAXGEOMETRY];
	public int geomz2[] = new int[MAXGEOMETRY];

	public boolean plantProcess = false;
	
	public int numlightnineffects, numtorcheffects, 
		numgeomeffects, numjaildoors, numminecart, numambients; 
	public int haveLigthning;

	public int UFO_SpawnCount;
	public int UFO_SpawnTime;
	public int UFO_SpawnHulk;
	
	public short gEndGame;
	public short gEndFirstEpisode;
	
	//RA
	public short BellTime;
	public int BellSound;
	public short word_119BE0;
	public int WindDir;
	public int WindTime;
	public int mamaspawn_count;
	public int fakebubba_spawn;
	public int dword_119C08;
	
	public short actortype[] = new short[MAXTILES];
	
	public int script[] = new int[MAXSCRIPTSIZE];
	public int actorscrptr[] = new int[MAXTILES];

	public int gAnimationCount = 0;
	public ANIMATION[] gAnimationData = new ANIMATION[MAXANIMATES];
	
	public short pskyoff[] = new short[MAXPSKYTILES], pskybits, earthquaketime;
	public int parallaxyscale;
	
	public short camsprite, numplayersprites;
	public short connecthead, connectpoint2[] = new short[MAXPLAYERS];
	public short[][] frags = new short[MAXPLAYERS][MAXPLAYERS];
	
	public int randomseed;
	public short global_random;
	
	//UserDef
	public int multimode;
	public int volume_number;
	public int level_number;
	public int player_skill;
	public short from_bonus;
	public short secretlevel;
	public boolean respawn_monsters,respawn_items,respawn_inventory;
	public int eog;
	public boolean god,scrollmode,clipping;
	public int auto_run, crosshair;
	public boolean monsters_off;
	public int last_level, coop, marker, ffire;
	
	public short numsectors, numwalls, numsprites;

	public short[] headspritesect, headspritestat;
	public short[] prevspritesect, prevspritestat;
	public short[] nextspritesect, nextspritestat;
	
	public short[] rorsector = new short[16];
	public byte[] rortype = new byte[16];
	public int rorcnt;
	
	public SECTOR[] sector = new SECTOR[MAXSECTORS];
	public WALL[] wall = new WALL[MAXWALLS];
	public SPRITE[] sprite = new SPRITE[MAXSPRITES];
	
	private String message;
	
	public SafeLoader()
	{
		headspritesect = new short[MAXSECTORS + 1]; 
		headspritestat = new short[MAXSTATUS + 1];
		prevspritesect = new short[MAXSPRITES]; 
		prevspritestat = new short[MAXSPRITES];
		nextspritesect = new short[MAXSPRITES]; 
		nextspritestat = new short[MAXSPRITES];
		
		for(int i = 0; i < MAXPLAYERS; i++)
		{
			ps[i] = new PlayerStruct();
			po[i] = new PlayerOrig();
		}
		for(int i = 0; i < MAXANIMATES; i++)
			gAnimationData[i] = new ANIMATION();
		for(int i = 0; i < MAXANIMWALLS; i++)
			animwall[i] = new Animwalltype();
		for(int i = 0; i < MAXSPRITES; i++) {
			hittype[i] = new Weaponhit();
			sprite[i] = new SPRITE();
		}
		for(int i = 0; i < MAXSECTORS; i++)
			sector[i] = new SECTOR();
		for(int i = 0; i < MAXWALLS; i++)
			wall[i] = new WALL();
	}
	
	public boolean load(Resource bb)
	{
		addon = null;
		addonFileName = null;
		message = null;
		warp_on = 0;
		
		try {
			bb.seek(SAVEHEADER - SAVELEVELINFO, Whence.Set);
			
			multimode = bb.readInt();
			volume_number = bb.readInt();
			level_number = bb.readInt();
			player_skill = bb.readInt();
			
			bb.seek(SAVEHEADER + SAVESCREENSHOTSIZE, Whence.Set);
	
			LoadGDXBlock(bb);
			MapLoad(bb);
			StuffLoad(bb);
			ConLoad(bb);
			AnimationLoad(bb);
			GameInfoLoad(bb);
			
			if (warp_on == 1) { // try to find addon
				addon = levelGetEpisode(addonFileName);
				if (addon == null) {
					message = "Can't find user episode file: " + addonFileName;
					warp_on = 2;

					volume_number = 0;
					level_number = 7;
				}
			}
			
			if(bb.position() == bb.size())
				return true;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void AnimationLoad(Resource bb)
	{
		for(int i = 0; i < MAXANIMATES; i++) {
			short index = bb.readShort();
			byte type = bb.readByte();
			gAnimationData[i].id = index;
			gAnimationData[i].type = type;
			gAnimationData[i].ptr = null;
			gAnimationData[i].goal = bb.readInt();
			gAnimationData[i].vel = bb.readInt();
			gAnimationData[i].sect = bb.readShort();
		}	
		gAnimationCount = bb.readInt();
	}
	
	public void ConLoad(Resource bb)
	{
		for(int i = 0; i < MAXTILES; i++)
			actortype[i] = (short) (bb.readByte() & 0xFF);
		for(int i=0;i<MAXSCRIPTSIZE;i++)
			script[i] = bb.readInt();
		for(int i=0;i<MAXTILES;i++)
			actorscrptr[i] = bb.readInt();
		for(int i=0;i<MAXSPRITES;i++) 
			hittype[i].set(bb);
	}
	
	public void GameInfoLoad(Resource bb)
	{
		pskybits = bb.readShort();
		parallaxyscale = bb.readInt();
		for(int i = 0; i < MAXPSKYTILES; i++)
			pskyoff[i] = bb.readShort();

		earthquaketime = bb.readShort();
		from_bonus = bb.readShort();
		secretlevel = bb.readShort();
		respawn_monsters = bb.readBoolean();
		respawn_items = bb.readBoolean();
		respawn_inventory = bb.readBoolean();
		god =  bb.readBoolean();
		auto_run = (bb.readInt() == 1)?1:0;
		crosshair = (bb.readInt() == 1)?1:0;
		monsters_off = bb.readBoolean();
		last_level = bb.readInt();
		eog = bb.readInt();
		coop = bb.readInt();
		marker = bb.readInt();
		ffire = bb.readInt();
		camsprite = bb.readShort();
	    
		connecthead = bb.readShort();
        for(int i = 0; i < MAXPLAYERS; i++) 
        	connectpoint2[i] = bb.readShort();
        numplayersprites = bb.readShort();
	  
        for(int i = 0; i < MAXPLAYERS; i++) 
        	for(int j = 0; j < MAXPLAYERS; j++) 
        		frags[i][j] = bb.readShort();
        
        randomseed = bb.readInt();
        global_random = bb.readShort();
	}

	public void StuffLoad(Resource bb)
	{
		numcyclers = bb.readShort();
		for(int i = 0; i < MAXCYCLERS; i++)
			for(int j = 0; j < 6; j++)
				cyclers[i][j] = bb.readShort();
		
		for(int i = 0; i < MAXPLAYERS; i++)
			ps[i].set(bb);
		for(int i = 0; i < MAXPLAYERS; i++)
			po[i].set(bb);
		
		numanimwalls = bb.readShort();
		for(int i = 0; i < MAXANIMWALLS; i++) {
			animwall[i].wallnum = bb.readShort();
			animwall[i].tag = bb.readInt();
		}
		for(int i = 0; i < 2048; i++) 
			msx[i] = bb.readInt();
		for(int i = 0; i < 2048; i++) 
			msy[i] = bb.readInt();
		
		spriteqloc = bb.readShort();
		spriteqamount = bb.readShort();
		for(int i = 0; i < 1024; i++)
			spriteq[i] = bb.readShort();
		
		mirrorcnt = bb.readShort();
		for(int i = 0; i < 64; i++)
			mirrorwall[i] = bb.readShort();
		for(int i = 0; i < 64; i++)
			mirrorsector[i] = bb.readShort();
		
		bb.read(show2dsector);
		
		for(int i = 0; i < MAXSECTORS; i++)
			shadeEffect[i] = bb.readBoolean();
		
		numjaildoors = bb.readInt();
		for(int i = 0; i < MAXJAILDOORS; i++)
		{
			jailspeed[i] = bb.readInt();
			jaildistance[i] = bb.readInt();
			jailsect[i] = bb.readShort();
			jaildirection[i] = bb.readShort();
			jailunique[i] = bb.readShort();
			jailsound[i] = bb.readShort();
			jailstatus[i] = bb.readShort();
			jailcount2[i] = bb.readInt();
		}
		
		numminecart = bb.readInt();
		for(int i = 0; i < MAXMINECARDS; i++)
		{
			minespeed[i] = bb.readInt();
			minefulldist[i] = bb.readInt();
			minedistance[i] = bb.readInt();
			minechild[i] = bb.readShort();
			mineparent[i] = bb.readShort();
			minedirection[i] = bb.readShort();
			minesound[i] = bb.readShort();
			minestatus[i] = bb.readShort();
		}

		numtorcheffects = bb.readInt();
		for(int i = 0; i < MAXTORCHES; i++)
		{
			torchsector[i] = bb.readShort();
			torchshade[i] = bb.readByte();
			torchflags[i] = bb.readShort();
		}
		
		numlightnineffects = bb.readInt();
		for(int i = 0; i < MAXLIGHTNINS; i++)
		{
			lightninsector[i] = bb.readShort();
			lightninshade[i] = bb.readShort();
		}
		
		numambients = bb.readInt();
		for(int i = 0; i < MAXAMBIENTS; i++)
		{
			ambienttype[i] = bb.readShort();
			ambientid[i] = bb.readShort();
			ambienthitag[i] = bb.readShort();
		}

		numgeomeffects = bb.readInt();
		for(int i = 0; i < MAXGEOMETRY; i++)
		{
			geomsector[i] = bb.readShort();
			geoms1[i] = bb.readShort();
			geomx1[i] = bb.readInt();
			geomy1[i] = bb.readInt();
			geomz1[i] = bb.readInt();
			
			geoms2[i] = bb.readShort();
			geomx2[i] = bb.readInt();
			geomy2[i] = bb.readInt();
			geomz2[i] = bb.readInt();
		}
		
		UFO_SpawnCount = bb.readShort();
		UFO_SpawnTime = bb.readShort();
		UFO_SpawnHulk = bb.readShort();
		
		gEndFirstEpisode = bb.readShort();
		gEndGame = bb.readShort();

		plantProcess = bb.readBoolean();
		
		
		BellTime = bb.readShort();
		BellSound = bb.readInt();
		word_119BE0 = bb.readShort();
		WindDir = bb.readInt();
		WindTime = bb.readInt();
		mamaspawn_count = bb.readInt();
		fakebubba_spawn = bb.readInt();
		dword_119C08 = bb.readInt();
	}
	
	public void MapLoad(Resource bb) throws Exception
	{
		boardfilename = null;
		String name = bb.readString(144).trim();
		if(!name.isEmpty()) boardfilename = name;
		
		numwalls = bb.readShort();
		for(int w = 0; w < numwalls; w++) 
			wall[w].buildWall(bb);
		numsectors = bb.readShort();
		for(int s = 0; s < numsectors; s++) 
			sector[s].buildSector(bb);
		for(int i = 0; i < MAXSPRITES; i++) 
			sprite[i].buildSprite(bb);
		for(int i = 0; i <= MAXSECTORS; i++)
			headspritesect[i] = bb.readShort();
		for(int i = 0; i <= MAXSTATUS; i++)
			headspritestat[i] = bb.readShort();
		
		for(int i = 0; i < MAXSPRITES; i++) {
			prevspritesect[i] = bb.readShort();
			prevspritestat[i] = bb.readShort();
			nextspritesect[i] = bb.readShort();
			nextspritestat[i] = bb.readShort();
		}
		
		rorcnt = bb.readInt();
		for(int i = 0; i < 16; i++) {
			rorsector[i] = bb.readShort();
			rortype[i] = bb.readByte();
		}
	}
	
	public String getMessage() {
		return message;
	}
	
	public GameInfo LoadGDXHeader(Resource fil) {
		volume_number = -1;
		level_number = -1;
		player_skill = -1;
		warp_on = 0;
		addon = null;
		addonFileName = null;
		
		try {
			fil.seek(SAVEHEADER - SAVELEVELINFO, Whence.Set);

			multimode = fil.readInt();
			volume_number = fil.readInt();
			level_number = fil.readInt();
			player_skill = fil.readInt();
			
			fil.seek(SAVEHEADER + SAVESCREENSHOTSIZE, Whence.Set);
			
			LoadGDXBlock(fil);
			if (warp_on == 1) // try to find addon
				addon = levelGetEpisode(addonFileName);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return addon;
	}
	
	public void LoadGDXBlock(Resource bb)
	{
		int pos = bb.position();
		//reserve SAVEGDXDATA bytes for extra data

		addon = null;
		warp_on = bb.readByte();
		if (warp_on == 1) {
			byte[] buf = new byte[144];
			bb.read(buf);
			addonFileName = Strhandler.toLowerCase(new String(buf).trim());
		}

		bb.seek(pos + SAVEGDXDATA, Whence.Set);
	}

}
