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

package ru.m210projects.Duke3D.Types;

import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.MAXPSKYTILES;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.MAXWALLS;
import static ru.m210projects.Duke3D.Animate.MAXANIMATES;
import static ru.m210projects.Duke3D.Gamedef.MAXSCRIPTSIZE;
import static ru.m210projects.Duke3D.Globals.MAXANIMWALLS;
import static ru.m210projects.Duke3D.Globals.MAXCYCLERS;
import static ru.m210projects.Duke3D.LoadSave.SAVEGDXDATA;
import static ru.m210projects.Duke3D.LoadSave.SAVEHEADER;
import static ru.m210projects.Duke3D.LoadSave.SAVELEVELINFO;
import static ru.m210projects.Duke3D.LoadSave.SAVESCREENSHOTSIZE;
import static ru.m210projects.Duke3D.ResourceHandler.levelGetEpisode;

import static ru.m210projects.Build.Strhandler.*;

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
	private String message;

	public int gAnimationCount = 0;
	public ANIMATION[] gAnimationData = new ANIMATION[MAXANIMATES];

	public short[] pskyoff = new short[MAXPSKYTILES];
	public short pskybits;
	public int parallaxyscale;
	public short connecthead;
	public short[] connectpoint2 = new short[MAXPLAYERS];

	public int randomseed;
	public int visibility;
	public byte automapping;
	public byte[] show2dsector = new byte[(MAXSECTORS + 7) >> 3];

	// MapInfo

	public short numsectors, numwalls;

	public short[] headspritesect, headspritestat;
	public short[] prevspritesect, prevspritestat;
	public short[] nextspritesect, nextspritestat;

	public SECTOR[] sector = new SECTOR[MAXSECTORS];
	public WALL[] wall = new WALL[MAXWALLS];
	public SPRITE[] sprite = new SPRITE[MAXSPRITES];

	// GameInfo

	public PlayerStruct[] ps = new PlayerStruct[MAXPLAYERS];
	public PlayerOrig[] po = new PlayerOrig[MAXPLAYERS];
	public Weaponhit[] hittype = new Weaponhit[MAXSPRITES];

	public int multimode, volume_number, level_number, player_skill;
	public int from_bonus, secretlevel;
	public boolean respawn_monsters, respawn_items, respawn_inventory, god, monsters_off;
	public int auto_run, crosshair, last_level, eog, coop, marker, ffire;
	public short numplayersprites, global_random, earthquaketime, camsprite;
	public short[][] frags = new short[MAXPLAYERS][MAXPLAYERS];

	public int[] msx = new int[2048], msy = new int[2048];
	public Animwalltype[] animwall = new Animwalltype[MAXANIMWALLS];
	public short[][] cyclers = new short[MAXCYCLERS][6];
	public short numcyclers;
	public short numanimwalls;
	public short[] spriteq = new short[1024];
	public short spriteqloc, spriteqamount;
	public short[] mirrorwall = new short[64];
	public short[] mirrorsector = new short[64];
	public short mirrorcnt;
	public short numclouds;
	public short[] clouds = new short[128];
	public short[] cloudx = new short[128];
	public short[] cloudy = new short[128];

	public int[] actorscrptr = new int[MAXTILES];
	public short[] actortype = new short[MAXTILES];
	public int[] script = new int[MAXSCRIPTSIZE];

	public SafeLoader() {
		for (int i = 0; i < MAXPLAYERS; i++) {
			ps[i] = new PlayerStruct();
			po[i] = new PlayerOrig();
		}

		for (int i = 0; i < MAXANIMWALLS; i++)
			animwall[i] = new Animwalltype();
		for (int i = 0; i < MAXSPRITES; i++)
			hittype[i] = new Weaponhit();

		headspritesect = new short[MAXSECTORS + 1];
		headspritestat = new short[MAXSTATUS + 1];
		prevspritesect = new short[MAXSPRITES];
		prevspritestat = new short[MAXSPRITES];
		nextspritesect = new short[MAXSPRITES];
		nextspritestat = new short[MAXSPRITES];

		for (int i = 0; i < MAXANIMATES; i++)
			gAnimationData[i] = new ANIMATION();
		for (int i = 0; i < MAXSPRITES; i++)
			sprite[i] = new SPRITE();
		for (int i = 0; i < MAXSECTORS; i++)
			sector[i] = new SECTOR();
		for (int i = 0; i < MAXWALLS; i++)
			wall[i] = new WALL();
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

	public void LoadGDXBlock(Resource bb) {
		int pos = bb.position();
		// reserve SAVEGDXDATA bytes for extra data

		warp_on = bb.readByte();
		if (warp_on == 1) {
			addonFileName = toLowerCase(bb.readString(144).trim());
		}

		bb.seek(pos + SAVEGDXDATA, Whence.Set);
	}

	public void MapLoad(Resource bb) {
		boardfilename = null;
		String name = bb.readString(144).trim();
		if (!name.isEmpty())
			boardfilename = name;

		numwalls = bb.readShort();
		for (int w = 0; w < numwalls; w++) 
			wall[w].buildWall(bb);
		numsectors = bb.readShort();
		for (int s = 0; s < numsectors; s++) 
			sector[s].buildSector(bb);
		// Store all sprites (even holes) to preserve indeces
		for (int i = 0; i < MAXSPRITES; i++) 
			sprite[i].buildSprite(bb);

		for (int i = 0; i <= MAXSECTORS; i++)
			headspritesect[i] = bb.readShort();
		for (int i = 0; i <= MAXSTATUS; i++)
			headspritestat[i] = bb.readShort();

		for (int i = 0; i < MAXSPRITES; i++) {
			prevspritesect[i] = bb.readShort();
			prevspritestat[i] = bb.readShort();
			nextspritesect[i] = bb.readShort();
			nextspritestat[i] = bb.readShort();
		}
	}

	public void AnimationLoad(Resource bb) {
		for (int i = 0; i < MAXANIMATES; i++) {
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

	public void Stuff2Load(Resource bb) {
		pskybits = bb.readShort();
		parallaxyscale = bb.readInt();
		for (int i = 0; i < MAXPSKYTILES; i++)
			pskyoff[i] = bb.readShort();

		earthquaketime = bb.readShort();
		from_bonus = bb.readShort();
		secretlevel = bb.readShort();
		respawn_monsters = bb.readBoolean();
		respawn_items = bb.readBoolean();
		respawn_inventory = bb.readBoolean();
		god = bb.readBoolean();
		auto_run = (bb.readInt() == 1) ? 1 : 0;
		crosshair = (bb.readInt() == 1) ? 1 : 0;
		monsters_off = bb.readBoolean();
		last_level = bb.readInt();
		eog = bb.readInt();
		coop = bb.readInt();
		marker = bb.readInt();
		ffire = bb.readInt();
		camsprite = bb.readShort();

		connecthead = bb.readShort();
		for (int i = 0; i < MAXPLAYERS; i++)
			connectpoint2[i] = bb.readShort();
		numplayersprites = bb.readShort();

		for (int i = 0; i < MAXPLAYERS; i++)
			for (int j = 0; j < MAXPLAYERS; j++)
				frags[i][j] = bb.readShort();

		randomseed = bb.readInt();
		global_random = bb.readShort();
	}

	public void StuffLoad(Resource bb) {
		numcyclers = bb.readShort();
		for (int i = 0; i < MAXCYCLERS; i++)
			for (int j = 0; j < 6; j++)
				cyclers[i][j] = bb.readShort();

		for (int i = 0; i < MAXPLAYERS; i++)
			ps[i].set(bb);
		for (int i = 0; i < MAXPLAYERS; i++)
			po[i].set(bb);

		numanimwalls = bb.readShort();
		for (int i = 0; i < MAXANIMWALLS; i++) {
			animwall[i].wallnum = bb.readShort();
			animwall[i].tag = bb.readInt();
		}
		for (int i = 0; i < 2048; i++)
			msx[i] = bb.readInt();
		for (int i = 0; i < 2048; i++)
			msy[i] = bb.readInt();

		spriteqloc = bb.readShort();
		spriteqamount = bb.readShort();
		for (int i = 0; i < 1024; i++)
			spriteq[i] = bb.readShort();

		mirrorcnt = bb.readShort();
		for (int i = 0; i < 64; i++)
			mirrorwall[i] = bb.readShort();
		for (int i = 0; i < 64; i++)
			mirrorsector[i] = bb.readShort();

		bb.read(show2dsector);
		numclouds = bb.readShort();
		for (int i = 0; i < 128; i++)
			clouds[i] = bb.readShort();
		for (int i = 0; i < 128; i++)
			cloudx[i] = bb.readShort();
		for (int i = 0; i < 128; i++)
			cloudy[i] = bb.readShort();
	}

	public void ConLoad(Resource bb) {
		for (int i = 0; i < MAXTILES; i++)
			actortype[i] = (short) (bb.readByte() & 0xFF);
		for (int i = 0; i < MAXSCRIPTSIZE; i++)
			script[i] = bb.readInt();
		for (int i = 0; i < MAXTILES; i++)
			actorscrptr[i] = bb.readInt();
		for (int i = 0; i < MAXSPRITES; i++)
			hittype[i].set(bb);
	}

	public String getMessage() {
		return message;
	}

	public boolean load(Resource bb) {
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
			Stuff2Load(bb);

			if (warp_on == 1) { // try to find addon
				addon = levelGetEpisode(addonFileName);
				if (addon == null) {
					message = "Can't find user episode file: " + addonFileName;
					warp_on = 2;

					volume_number = 0;
					level_number = 7;
				}
			}

			if (bb.position() == bb.size())
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
