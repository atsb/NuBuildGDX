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

package ru.m210projects.Blood.Types;

import static ru.m210projects.Blood.EVENT.getIndex;
import static ru.m210projects.Blood.EVENT.getType;
import static ru.m210projects.Blood.EVENT.kMaxChannels;
import static ru.m210projects.Blood.EVENT.kMaxID;
import static ru.m210projects.Blood.LOADSAVE.gdxSave;
import static ru.m210projects.Blood.LOADSAVE.SAVEINFO;
import static ru.m210projects.Blood.LOADSAVE.SAVESCREENSHOTSIZE;
import static ru.m210projects.Blood.LOADSAVE.SAVEGDXDATA;
import static ru.m210projects.Blood.Mirror.MAXMIRRORS;
import static ru.m210projects.Blood.Trigger.kMaxBusyArray;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.kMaxSequences;
import static ru.m210projects.Blood.DB.kMaxXSectors;
import static ru.m210projects.Blood.DB.kMaxXSprites;
import static ru.m210projects.Blood.DB.kMaxXWalls;
import static ru.m210projects.Blood.Globals.SS_CEILING;
import static ru.m210projects.Blood.Globals.SS_FLOOR;
import static ru.m210projects.Blood.Globals.SS_MASKED;
import static ru.m210projects.Blood.Globals.SS_SPRITE;
import static ru.m210projects.Blood.Globals.SS_WALL;
import static ru.m210projects.Blood.Globals.kMaxPlayers;
import static ru.m210projects.Blood.Globals.kMaxSectors;
import static ru.m210projects.Blood.Globals.kMaxSprites;
import static ru.m210projects.Blood.Globals.kMaxStatus;
import static ru.m210projects.Blood.Globals.kMaxWalls;
import static ru.m210projects.Blood.Globals.kStatFree;
import static ru.m210projects.Blood.LEVELS.kMaxMap;
import static ru.m210projects.Blood.LEVELS.levelGetEpisode;
import static ru.m210projects.Build.Engine.MAXPSKYTILES;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.MAXWALLSV7;

import java.io.FileNotFoundException;
import java.util.Arrays;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.Types.Seq.CeilingInst;
import ru.m210projects.Blood.Types.Seq.FloorInst;
import ru.m210projects.Blood.Types.Seq.MaskedWallInst;
import ru.m210projects.Blood.Types.Seq.SeqInst;
import ru.m210projects.Blood.Types.Seq.SpriteInst;
import ru.m210projects.Blood.Types.Seq.WallInst;
import ru.m210projects.Build.Strhandler;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.Types.Hitscan;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;

public class SafeLoader {

	public short[] pskyoff = new short[MAXPSKYTILES];
	public short pskybits;
	public int parallaxyscale;
	
	public short connecthead;
	public short[] connectpoint2 = new short[kMaxPlayers];
	public int randomseed;
	
	public int visibility, parallaxvisibility;
	public byte automapping;
	public byte[] show2dsector = new byte[(kMaxSectors + 7) >> 3];
	public byte[] show2dwall = new byte[(kMaxWalls + 7) >> 3];
	public byte[] show2dsprite = new byte[(kMaxSprites + 7) >> 3];
	
	public byte[] gotpic = new byte[(MAXTILES + 7) >> 3];
	public byte[] gotsector = new byte[(kMaxSectors + 7) >> 3];
	
	public Hitscan safeHitInfo;
	
	//MapInfo
	
	public short numsectors, numwalls, numsprites;

	public short[] headspritesect, headspritestat;
	public short[] prevspritesect, prevspritestat;
	public short[] nextspritesect, nextspritestat;

	public SECTOR[] sector = new SECTOR[kMaxSectors];
	public WALL[] wall = new WALL[kMaxWalls];
	public SPRITE[] sprite = new SPRITE[kMaxSprites];

	//Blood variables
	
	public Vector2[] kwall = new Vector2[MAXWALLSV7];
	public Vector3[] ksprite = new Vector3[kMaxSprites];
	
	public int[] secFloorZ = new int[kMaxSectors];
	public int[] secCeilZ = new int[kMaxSectors];
	public int[] secPath = new int[kMaxSectors];
	
	public long[] sprXVel = new long[kMaxSprites];
	public long[] sprYVel = new long[kMaxSprites];
	public long[] sprZVel = new long[kMaxSprites];

	public long[] floorVel = new long[kMaxSectors];
	public long[] ceilingVel = new long[kMaxSectors];
	
	public short[] nStatSize = new short[kMaxStatus+1];
	
	public XSPRITE[] xsprite = new XSPRITE[kMaxXSprites];
	public XWALL[] xwall = new XWALL[kMaxXWalls];
	public XSECTOR[] xsector = new XSECTOR[kMaxXSectors];

	public int[] nextXSprite = new int[kMaxXSprites];
	public int[] nextXWall = new int[kMaxXWalls];
	public int[] nextXSector = new int[kMaxXSectors];
	
	public GAMEINFO safeGameInfo = new GAMEINFO();
	
	public boolean showinvisibility;
	public boolean gNoClip, gFogMode, gFullMap, gPaused, gInfiniteAmmo, cheatsOn;
	public int gSkyCount;
	public int gFrameClock, gTicks, gFrame, gGameClock;

	public int[] cumulDamage = new int[kMaxXSprites];
	public int[] gDudeSlope = new int[kMaxXSprites];
	
	public ZONE[] gStartZone = new ZONE[kMaxPlayers];
	public int[] gUpperLink = new int[kMaxSectors], gLowerLink = new int[kMaxSectors];
	
	public int mirrorcnt;
	public int[] MirrorType = new int[MAXMIRRORS];
	public int[] MirrorX = new int[MAXMIRRORS];
	public int[] MirrorY = new int[MAXMIRRORS];
	public int[] MirrorZ = new int[MAXMIRRORS];
	public int[] MirrorLower = new int[MAXMIRRORS];
	public int[] MirrorUpper = new int[MAXMIRRORS];
	public int MirrorSector;
	public int[] MirrorWall = new int[4];
	
	public SeqInst[] siWall = new SeqInst[kMaxXWalls], siMasked = new SeqInst[kMaxXWalls];
	public SeqInst[] siCeiling = new SeqInst[kMaxXSectors], siFloor = new SeqInst[kMaxXSectors];
	public SeqInst[] siSprite = new SeqInst[kMaxXSprites];
	
	public short[] actListIndex = new short[kMaxSequences];
	public byte[] actListType = new byte[kMaxSequences];
	
	public int activeCount;
	
	public int[] rxBucketIndex = new int[kMaxChannels];
	public int[] rxBucketType = new int[kMaxChannels];
	public short[] bucketHead = new short[kMaxID + 1];
	
	public int[] qEventEvent = new int[1025];
	public int[] qEventPriority = new int[1025];
	public int fNodeCount;
	
	public int gBusyCount = 0;
	public BUSY[] gBusy = new BUSY[kMaxBusyArray];
	
	public int[] nTeamCount = new int[kMaxPlayers];
	public int gNetPlayers;
	
	public byte[] autoaim = new byte[kMaxPlayers];
	public byte[] slopetilt = new byte[kMaxPlayers];
	public byte[] skill = new byte[kMaxPlayers];
	public String[] name = new String[kMaxPlayers];

	public PLAYER[] safePlayer = new PLAYER[kMaxPlayers];
	
	public SPRITEHIT[] gSpriteHit = new SPRITEHIT[kMaxXSprites];
	public int[] gWallExp = new int[kMaxWalls];
	public int[] gSectorExp = new int[kMaxSectors];
	public byte[] gSpriteExp = new byte[(kMaxSectors + 7) >> 3];
	
	public POSTPONE[] gPost = new POSTPONE[kMaxSprites];
	public int gPostCount;
	
	public int gNextMap;
	public int foundSecret;
	public int totalSecrets;
	public int superSecrets;
	public int totalKills;
	public int kills;
	
	public int deliriumTilt = 0;
	public int deliriumTurn = 0;
	public int deliriumPitch = 0;
	
	public boolean gUserEpisode, gForceMap;
	public BloodIniFile addon;
	public String addonFileName;
	private String message;
	
	public SafeLoader()
	{
		headspritesect = new short[MAXSECTORS + 1]; 
		headspritestat = new short[MAXSTATUS + 1];
		prevspritesect = new short[MAXSPRITES]; 
		prevspritestat = new short[MAXSPRITES];
		nextspritesect = new short[MAXSPRITES]; 
		nextspritestat = new short[MAXSPRITES];

		for(int i = 0; i < MAXSPRITES; i++) 
			sprite[i] = new SPRITE();
		for(int i = 0; i < MAXSECTORS; i++)
			sector[i] = new SECTOR();
		for(int i = 0; i < kMaxWalls; i++)
			wall[i] = new WALL();
		
		for(int i = 0; i < MAXWALLSV7; i++)
			kwall[i] = new Vector2();
		for(int i = 0; i < kMaxSprites; i++) {
			ksprite[i] = new Vector3();
			gPost[i] = new POSTPONE();
		}
		
		for(int i = 0; i < kMaxXSprites; i++) {
			xsprite[i] = new XSPRITE();
			gSpriteHit[i] = new SPRITEHIT();
		}
		for(int i = 0; i < kMaxXWalls; i++)
			xwall[i] = new XWALL();
		for(int i = 0; i < kMaxXSectors; i++)
			xsector[i] = new XSECTOR();
		
		for(int i = 0; i < kMaxPlayers; i++) {
			gStartZone[i] = new ZONE();
			safePlayer[i] = new PLAYER();
		}
		
		for(int i = 0; i < kMaxXWalls; i++)
			siWall[i] = new WallInst();
		for(int i = 0; i < kMaxXWalls; i++)
			siMasked[i] = new MaskedWallInst();
		for(int i = 0; i < kMaxXSectors; i++)
			siCeiling[i] = new CeilingInst();
		for(int i = 0; i < kMaxXSectors; i++)
			siFloor[i] = new FloorInst();
		for(int i = 0; i < kMaxXSprites; i++)
			siSprite[i] = new SpriteInst();
		
		for(int i = 0; i < kMaxBusyArray; i++)
			gBusy[i] = new BUSY();
		
		safeHitInfo = new Hitscan();
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public boolean load(Resource bb, int nVersion)
	{
		addon = null;
		addonFileName = null;
		message = null;
		gUserEpisode = false;
		try {
			for(int i = 0; i < MAXWALLSV7; i++) 
				kwall[i].set(0, 0);
			for(int i = 0; i < kMaxSprites; i++) 
				ksprite[i].set(0, 0, 0);
			Arrays.fill(gotpic, (byte) 0);
			
			for (int i = 0; i < kMaxPlayers; i++) {
				autoaim[i] = -1;
				slopetilt[i] = -1;
				skill[i] = -1;
				name[i] = null;
			}
			
			if(nVersion == 0x100) { //old save file format
				bb.seek(0, Whence.Set);
				MyLoad100(bb);
				DudesLoad(bb);
				WarpLoad(bb);
				MirrorLoad(bb);
				SeqLoad(bb, nVersion);
				EventLoad(bb);
				TriggersLoad(bb);
				PlayersLoad(bb, nVersion);
				ActorsLoad(bb);
				bb.readShort(); //unknown
				GameInfoLoad(bb);
				StatsLoad(bb);
				ScreenLoad(bb);
			}
			else
			{
				if(nVersion >= gdxSave)
				{
					bb.seek(SAVEINFO + SAVESCREENSHOTSIZE, Whence.Set); //Save header + screenshot
					LoadGDXBlock(bb);
				}  
				
				MyLoad110(nVersion, bb);
				DudesLoad(bb);
				cheatsOn = bb.readBoolean();
				WarpLoad(bb); 
				MirrorLoad(bb);
				SeqLoad(bb, nVersion);
				EventLoad(bb);
				TriggersLoad(bb);
				PlayersLoad(bb, 277);
				ActorsLoad(bb);
				GameInfoLoad(bb);
				StatsLoad(bb);
				ScreenLoad(bb); 
			}
			
			if(gUserEpisode) { //try to find addon
				addon = levelGetEpisode(addonFileName);
				if(addon == null)
				{
					message = "Can't find user episode file: " + addonFileName;
					gUserEpisode = false;
					
					safeGameInfo.nEpisode = 0;
					safeGameInfo.nLevel = kMaxMap;
					gForceMap = true;
				}
			}
			
			if(bb.position() == bb.size())
				return true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void MyLoad100(Resource bb)
	{
		int kMaxTiles = 4096;
		
		LoadGameInfo(bb);

		gFrameClock = bb.readInt(); 
		gTicks = bb.readInt(); 
		gFrame = bb.readInt(); 
		gGameClock = bb.readInt(); 
		
		gPaused = bb.readBoolean();
		bb.readByte(); //unk_110D21

		for(int i = 0; i < MAXWALLSV7; i++) {
			kwall[i].x = bb.readInt();
			kwall[i].y = bb.readInt();
		}
		
		for(int i = 0; i < kMaxSprites; i++) {
			ksprite[i].x = bb.readInt();
			ksprite[i].y = bb.readInt();
			ksprite[i].z = bb.readInt();
		}

		for(int i = 0; i < kMaxSectors; i++) 
			secFloorZ[i] = bb.readInt();
		for(int i = 0; i < kMaxSectors; i++) 
			secCeilZ[i] = bb.readInt();
		for(int i = 0; i < kMaxSectors; i++) 
			floorVel[i] = bb.readInt();
		for(int i = 0; i < kMaxSectors; i++) 
			ceilingVel[i] = bb.readInt();

		safeHitInfo.hitsect =  bb.readShort();
		safeHitInfo.hitwall =  bb.readShort();
		safeHitInfo.hitsprite = bb.readShort();
		safeHitInfo.hitx =  bb.readInt(); 
		safeHitInfo.hity =  bb.readInt(); 
		safeHitInfo.hitz =  bb.readInt();

		gForceMap = bb.readBoolean();
		
		for(int i = 0; i <= kMaxStatus; i++)
			nStatSize[i] = bb.readShort();
		
		byte[] buf = new byte[XSPRITE.sizeof];
		for(int i = 0; i < kMaxXSprites; i++) {
			bb.read(buf);
			xsprite[i].init(buf);
			xsprite[i].reference = BitHandler.bsread(buf, 0, 0, 13);
		}

		buf = new byte[XWALL.sizeof];
		for(int i = 0; i < kMaxXWalls; i++) {
			bb.read(buf);
			xwall[i].init(buf);
			xwall[i].reference = BitHandler.bsread(buf, 0, 0, 13);
		}	
		buf = new byte[XSECTOR.sizeof];
		for(int i = 0; i < kMaxXSectors; i++) {
			bb.read(buf);
			xsector[i].init(buf);
			xsector[i].reference = BitHandler.bsread(buf, 0, 0, 13);
		}
		
		for(int i = 0; i < kMaxSprites; i++) 
			sprXVel[i] = bb.readInt();
		for(int i = 0; i < kMaxSprites; i++) 
			sprYVel[i] = bb.readInt();
		for(int i = 0; i < kMaxSprites; i++) 
			sprZVel[i] = bb.readInt();
		for(int i = 0; i < kMaxXSprites; i++) 
			nextXSprite[i] = bb.readShort();
		for(int i = 0; i < kMaxXWalls; i++) 
			nextXWall[i] = bb.readShort();
		for(int i = 0; i < kMaxXSectors; i++) 
			nextXSector[i] = bb.readShort();
			
		bb.readInt(); //dword_176518
		bb.readInt(); //dword_17651C
		
		gSkyCount = bb.readInt();
		gFogMode = bb.readBoolean();

		for(int i = 0; i < kMaxSectors; i++) 
			sector[i].buildSector(bb);

		for(int i = 0; i < MAXWALLSV7; i++) 
			wall[i].buildWall(bb);
		
		for(int i = 0; i < kMaxSprites; i++) 
			sprite[i].buildSprite(bb);

		numsectors = bb.readShort();
		numwalls = bb.readShort();
		randomseed = bb.readInt();
		bb.readByte(); //parallaxtype
		
		showinvisibility = bb.readBoolean();
		bb.readInt(); //parallaxyoffs
		
		parallaxyscale = bb.readInt();
		visibility = bb.readInt();
		parallaxvisibility = bb.readInt();
		
		for(int i = 0; i < MAXPSKYTILES; i++)
			pskyoff[i] = bb.readShort();
		pskybits = bb.readShort();

		for(int i = 0; i <= kMaxSectors; i++)
			headspritesect[i] = bb.readShort();
		for(int i = 0; i <= kMaxStatus; i++)
			headspritestat[i] = bb.readShort();
		for(int i = 0; i < kMaxSprites; i++)
			prevspritesect[i] = bb.readShort();
		for(int i = 0; i < kMaxSprites; i++)
			prevspritestat[i] = bb.readShort();
		for(int i = 0; i < kMaxSprites; i++)
			nextspritesect[i] = bb.readShort();
		for(int i = 0; i < kMaxSprites; i++)
			nextspritestat[i] = bb.readShort();
		for(int i = 0; i < (kMaxSectors+7)>>3; i++)
			show2dsector[i] = bb.readByte();
		for(int i = 0; i < (MAXWALLSV7+7)>>3; i++)
			show2dwall[i] = bb.readByte();
		for(int i = 0; i < (kMaxSprites+7)>>3; i++)
			show2dsprite[i] = bb.readByte();
		automapping = bb.readByte();
		for(int i = 0; i < (kMaxTiles+7)>>3; i++)
			gotpic[i] = bb.readByte();
		for(int i = 0; i < (kMaxSectors+7)>>3; i++)
			gotsector[i] = bb.readByte();
		
		safeGameInfo.nEnemyDamage = safeGameInfo.nDifficulty;
		safeGameInfo.nEnemyQuantity = safeGameInfo.nDifficulty;
		safeGameInfo.nPitchforkOnly = false;
		gInfiniteAmmo = false;
		cheatsOn = false;
	}
	
	public void MyLoad110(int nVersion, Resource bb)
	{
		int kMaxTiles = 6144;
		
		LoadGameInfo(bb);
		numsectors = bb.readShort();
		numwalls = bb.readShort();
		int tmp = bb.readInt();
		numsprites = (short) tmp;

		for(int i = 0; i < numsectors; i++) 
			sector[i].buildSector(bb);
		for(int i = 0; i < numwalls; i++) 
			wall[i].buildWall(bb);
		for(int i = 0; i < kMaxSprites; i++) 
			sprite[i].buildSprite(bb);
		randomseed = bb.readInt();
		bb.readByte(); //parallaxtype
		
		showinvisibility = bb.readBoolean();
		bb.readInt(); //parallaxyoffs
		
		parallaxyscale = bb.readInt();
		visibility = bb.readInt();
		parallaxvisibility = bb.readInt();
		
		for(int i = 0; i < MAXPSKYTILES; i++)
			pskyoff[i] = bb.readShort();
		pskybits = bb.readShort();

		for(int i = 0; i <= kMaxSectors; i++)
			headspritesect[i] = bb.readShort();
		for(int i = 0; i <= kMaxStatus; i++)
			headspritestat[i] = bb.readShort();
		for(int i = 0; i < kMaxSprites; i++)
			prevspritesect[i] = bb.readShort();
		for(int i = 0; i < kMaxSprites; i++)
			prevspritestat[i] = bb.readShort();
		for(int i = 0; i < kMaxSprites; i++)
			nextspritesect[i] = bb.readShort();
		for(int i = 0; i < kMaxSprites; i++)
			nextspritestat[i] = bb.readShort();
		for(int i = 0; i < (kMaxSectors+7)>>3; i++)
			show2dsector[i] = bb.readByte();
		for(int i = 0; i < (MAXWALLSV7+7)>>3; i++)
			show2dwall[i] = bb.readByte();
		for(int i = 0; i < (kMaxSprites+7)>>3; i++)
			show2dsprite[i] = bb.readByte();
		automapping = bb.readByte();
		for(int i = 0; i < (kMaxTiles+7)>>3; i++)
			gotpic[i] = bb.readByte();
		for(int i = 0; i < (kMaxSectors+7)>>3; i++)
			gotsector[i] = bb.readByte();
		
		gFrameClock = bb.readInt();
		gTicks = bb.readInt(); 
		gFrame = bb.readInt(); 
		gGameClock = bb.readInt(); 
		
		gPaused = bb.readBoolean();
		bb.readByte(); //unk_133921

		for(int i = 0; i < numwalls; i++) {
			kwall[i].x = bb.readInt();
			kwall[i].y = bb.readInt();
		}
		
		for(int i = 0; i < numsprites; i++) {
			int x = bb.readInt();
			int y = bb.readInt();
			int z = bb.readInt();
			if(i >= kMaxSprites)
				continue;

			ksprite[i].x = x;
			ksprite[i].y = y;
			ksprite[i].z = z;
		}
		
		for(int i = 0; i < numsectors; i++) 
			secFloorZ[i] = bb.readInt();
		for(int i = 0; i < numsectors; i++) 
			secCeilZ[i] = bb.readInt();
		for(int i = 0; i < numsectors; i++) 
			floorVel[i] = bb.readInt();
		for(int i = 0; i < numsectors; i++) 
			ceilingVel[i] = bb.readInt();
		
		safeHitInfo.hitsect =  bb.readShort();
		safeHitInfo.hitwall =  bb.readShort();
		safeHitInfo.hitsprite = bb.readShort();
		safeHitInfo.hitx =  bb.readInt(); 
		safeHitInfo.hity =  bb.readInt(); 
		safeHitInfo.hitz =  bb.readInt();
		
		gForceMap = bb.readBoolean();
		bb.readByte(); //crypted
		bb.readByte(); //crypted matt

		byte[] buf = new byte[128];
		bb.read(buf, 0, 128); //size of xsystem and copyright

		for(int i = 0; i <= kMaxStatus; i++)
			nStatSize[i] = bb.readShort();
		for(int i = 0; i < kMaxXSprites; i++) 
			nextXSprite[i] = bb.readShort();
		for(int i = 0; i < kMaxXWalls; i++) 
			nextXWall[i] = bb.readShort();
		for(int i = 0; i < kMaxXSectors; i++) 
			nextXSector[i] = bb.readShort();
		
		for(int i = 0; i < kMaxXSprites; i++) 
			xsprite[i].free();

		buf = new byte[XSPRITE.sizeof];
		for(int i = 0; i < kMaxSprites; i++) 
		{
			if ( sprite[i].statnum < kStatFree )
		    {
		    	if ( sprite[i].extra > 0 ) {
					bb.read(buf);
					xsprite[sprite[i].extra].init(buf);
					xsprite[sprite[i].extra].reference = BitHandler.bsread(buf, 0, 0, 13);
		    	}
		    }
		}
		
		for(int i = 0; i < kMaxXWalls; i++) 
			xwall[i].free();

		buf = new byte[XWALL.sizeof];
		for(int i = 0; i < numwalls; i++) 
		{
	    	if ( wall[i].extra > 0 ) {
				bb.read(buf);
				xwall[wall[i].extra].init(buf);
				xwall[wall[i].extra].reference = BitHandler.bsread(buf, 0, 0, 13);
	    	}
		}
		
		for(int i = 0; i < kMaxXSectors; i++) 
			xsector[i].free();
		
		buf = new byte[XSECTOR.sizeof];
		for(int i = 0; i < numsectors; i++) 
		{
	    	if ( sector[i].extra > 0 ) {
				bb.read(buf);
				xsector[sector[i].extra].init(buf);
				xsector[sector[i].extra].reference = BitHandler.bsread(buf, 0, 0, 13);
	    	}
		}
		
		Arrays.fill(sprXVel, 0);
		Arrays.fill(sprYVel, 0);
		Arrays.fill(sprZVel, 0);
		
		for(int i = 0; i < numsprites; i++) {
			int xvel = bb.readInt();
			if(i >= kMaxSprites) 
				continue;
			sprXVel[i] = xvel;
		}
		for(int i = 0; i < numsprites; i++) {
			int yvel = bb.readInt();
			if(i >= kMaxSprites) 
				continue;
			sprYVel[i] = yvel;
		}
		for(int i = 0; i < numsprites; i++) {
			int zvel = bb.readInt();
			if(i >= kMaxSprites) 
				continue;
			sprZVel[i] = zvel;
		}
		
		bb.readInt(); //dword_19AE34
		bb.readInt(); //dword_19AE38
		
		gSkyCount = bb.readInt();
		gFogMode = bb.readBoolean();
		
		if(numsprites >= kMaxSprites)
			numsprites = (short) (kMaxSprites - 1);
		
		gNoClip = false;
		gFullMap = false;
		
		if(nVersion < gdxSave)
		{
			safeGameInfo.nEnemyDamage = safeGameInfo.nDifficulty;
			safeGameInfo.nEnemyQuantity = safeGameInfo.nDifficulty;
			safeGameInfo.nPitchforkOnly = false;
			gInfiniteAmmo = false;
		}
	}
	
	public void LoadGameInfo(Resource bb)
	{
		safeGameInfo.nGameType = bb.readByte() & 0xFF;
		safeGameInfo.nDifficulty = bb.readByte() & 0xFF;
		safeGameInfo.nEpisode = bb.readInt();       
		safeGameInfo.nLevel = bb.readInt();  

		byte[] buf = new byte[144];
		bb.read(buf);
		String name = new String(buf).trim();
		name = Strhandler.toLowerCase(name);
		if(FileUtils.isExtension(name, "map"))
			name = name.substring(0, name.lastIndexOf('.'));
		safeGameInfo.zLevelName = name;
		bb.read(buf);
		safeGameInfo.zLevelSong = new String(buf).trim();
		safeGameInfo.nTrackNumber = bb.readInt();
		buf = new byte[16];
		bb.read(buf); //szSaveGameName - gamexxxx.sav  
		bb.read(buf); 
		bb.readShort(); //nSaveGameSlot  
		bb.readInt(); //picEntry 

		safeGameInfo.uMapCRC = bb.readInt(); //uMapCRC
		safeGameInfo.nMonsterSettings = bb.readByte() & 0xFF;             
		safeGameInfo.uGameFlags = bb.readInt();                
		safeGameInfo.uNetGameFlags = bb.readInt();      
		safeGameInfo.nWeaponSettings = bb.readByte() & 0xFF;        
		safeGameInfo.nItemSettings   = bb.readByte() & 0xFF;        
		safeGameInfo.nRespawnSettings = bb.readByte() & 0xFF;     
		safeGameInfo.nTeamSettings = bb.readByte() & 0xFF;     
		safeGameInfo.nMonsterRespawnTime = bb.readInt();               
		safeGameInfo.nWeaponRespawnTime  = bb.readInt();                   
		safeGameInfo.nItemRespawnTime  = bb.readInt();                     
		safeGameInfo.nSpecialRespawnTime  = bb.readInt();     
	}

	public void DudesLoad(Resource bb)
	{
		for(int i = 0; i < kMaxXSprites; i++) {
			cumulDamage[i] = 0;
			bb.readInt();
		}
		for(int i = 0; i < kMaxXSprites; i++) 
			gDudeSlope[i] = bb.readInt();
	}
	
	public void WarpLoad(Resource bb)
	{
		for(int i = 0; i < kMaxPlayers; i++) 
		{
			gStartZone[i].x = bb.readInt();
			gStartZone[i].y = bb.readInt();
			gStartZone[i].z = bb.readInt();
			gStartZone[i].sector = bb.readShort();
			gStartZone[i].angle = bb.readShort();
		}
		for(int i = 0; i < kMaxSectors; i++) 
			gUpperLink[i] = bb.readShort();
		for(int i = 0; i < kMaxSectors; i++) 
			gLowerLink[i] = bb.readShort();
	}
	
	public void MirrorLoad(Resource bb)
	{
		mirrorcnt = bb.readInt();
		MirrorSector = bb.readInt();
		for(int i = 0; i < MAXMIRRORS; i++)
		{
			MirrorType[i] = bb.readShort();
			bb.readShort(); //pad
			MirrorLower[i] = bb.readInt();
			MirrorX[i] = bb.readInt();
			MirrorY[i] = bb.readInt();
			MirrorZ[i] = bb.readInt();
			MirrorUpper[i] = bb.readInt();
		}
		for(int i = 0; i < 4; i++) 
			MirrorWall[i] = bb.readInt();
	}
	
	public void SeqLoad(Resource bb, int nVersion) throws FileNotFoundException
	{
		for(int i = 0; i < kMaxXWalls; i++) {
			siWall[i].load(bb);
		}
		for(int i = 0; i < kMaxXWalls; i++) {
			siMasked[i].load(bb);
		}
		for(int i = 0; i < kMaxXSectors; i++) { 
			siCeiling[i].load(bb);
		}
		for(int i = 0; i < kMaxXSectors; i++) {
			siFloor[i].load(bb);
		}
		for(int i = 0; i < kMaxXSprites; i++) {
			siSprite[i].load(bb);
		}
		
		int len = kMaxSequences;
		if(nVersion < gdxSave + 2) len = 1024;
		Arrays.fill(actListType, (byte) 0);
		Arrays.fill(actListIndex, (short) 0);
		
		for(int i = 0; i < len; i++) {
			actListType[i] = bb.readByte();
			actListIndex[i] = bb.readShort();
		}

		activeCount = bb.readInt();
		for(int i = 0; i < activeCount; i++)
		{
			SeqInst pInst = GetInstance(actListType[i], actListIndex[i]);
			if(pInst.isPlaying() && !BuildGdx.cache.contains(pInst.getSeqIndex(), "SEQ"))
				throw new FileNotFoundException("hSeq != null, id=" + pInst.getSeqIndex() + " \n\rWrong Blood.RFF version or file corrupt!");
		}
	}
	
	public SeqInst GetInstance( int type, int nXIndex )
	{
		switch ( type )
		{
			case SS_WALL:
				if(nXIndex <= 0 || nXIndex >= kMaxXWalls) 
					return null;
				return siWall[nXIndex];

			case SS_CEILING:
				if(nXIndex <= 0 || nXIndex >= kMaxXSectors) 
					return null;
				return siCeiling[nXIndex];

			case SS_FLOOR:
				if(nXIndex <= 0 || nXIndex >= kMaxXSectors) 
					return null;
				return siFloor[nXIndex];

			case SS_SPRITE:
				if(nXIndex <= 0 || nXIndex >= kMaxXSprites) 
					return null;
				return siSprite[nXIndex];

			case SS_MASKED:
				if(nXIndex <= 0 || nXIndex >= kMaxXWalls) 
					return null;
				return siMasked[nXIndex];

		}

		return null;
	}

	public void EventLoad(Resource bb)
	{
		for(int i = 0; i < 1025; i++)
		{
			qEventPriority[i] = bb.readInt();
			qEventEvent[i] = bb.readInt();
		}
		fNodeCount = bb.readInt();

		for(int i = 0; i < kMaxChannels; i++) {
			int data = bb.readInt();
			rxBucketIndex[i] = getIndex(data);
			rxBucketType[i] = getType(data);
		}
		
		for(int i = 0; i <= kMaxID; i++) 
			bucketHead[i] = bb.readShort();
	}
	
	public void TriggersLoad(Resource bb)
	{
		gBusyCount = bb.readInt();
		for(int i = 0; i < kMaxBusyArray; i++) {
			gBusy[i].nIndex = bb.readInt();
			gBusy[i].nDelta = bb.readInt();
			gBusy[i].nBusy = bb.readInt();
			gBusy[i].busyProc = bb.readByte() & 0xFF;
		}
		for(int i = 0; i < kMaxSectors; i++)
			secPath[i] = bb.readInt();
	}
	
	public void PlayersLoad(Resource bb, int nVersion)
	{
		for(int i = 0; i < kMaxPlayers; i++)
			nTeamCount[i] = bb.readInt();
		gNetPlayers = bb.readInt();

		for(int i = 0; i < gNetPlayers - 1; i++) 
			connectpoint2[i] = (short) (i+1);
		connectpoint2[gNetPlayers-1] = -1;
		
		byte[] plname = new byte[15];
		for(int i = 0; i < kMaxPlayers; i++)
		{
			autoaim[i] = bb.readByte();
			if(nVersion >= gdxSave + 2)
				slopetilt[i] = bb.readByte();
			skill[i] = bb.readByte();
			bb.read(plname);
			name[i] = new String(plname).trim();
		}
		
		byte[] input = new byte[INPUT.sizeof(nVersion)];
		for(int i = 0; i < kMaxPlayers; i++)
		{
			bb.readInt(); //sprite
			bb.readInt(); //xsprite
			bb.readInt(); //dudeinfo
			bb.read(input);
			safePlayer[i].pInput = new INPUT(input, nVersion);
			safePlayer[i].set(bb, nVersion);
		}
	}
	
	public void ActorsLoad(Resource bb)
	{
		for(int i = 0; i < kMaxXSprites; i++) {
			gSpriteHit[i].moveHit = bb.readInt();
			gSpriteHit[i].ceilHit = bb.readInt();
			gSpriteHit[i].floorHit = bb.readInt();
		}
		
		for(int i = 0; i < kMaxSectors; i++) 
			gSectorExp[i] = bb.readShort();
		for(int i = 0; i < kMaxXWalls; i++) 
			gWallExp[i] = bb.readShort();

		gPostCount = bb.readInt();
		for(int i = 0; i < kMaxSprites; i++) {
			gPost[i].nSprite = bb.readShort();
			gPost[i].nStatus = bb.readShort();
		}
	}
	
	public void GameInfoLoad(Resource bb)
	{
		gNextMap = bb.readInt();
		bb.readShort(); //unk_111330 unk_133FB4
		LoadGameInfo(bb);      
		bb.readByte(); //gPlaygame
		bb.readByte(); //byte_1A9C92
	}
	
	public void StatsLoad(Resource bb)
	{
		totalSecrets = bb.readInt();
		foundSecret = bb.readInt();
		superSecrets = bb.readInt();
		totalKills = bb.readInt();
		kills = bb.readInt();
	}
	
	public void ScreenLoad(Resource bb)
	{
		bb.readInt(); // unk_128C08
		byte[] buf = new byte[256];
		bb.read(buf); //unk_128C0C
		bb.readShort(); // unk_128E10
		bb.readShort(); // unk_128E12
		bb.readInt(); //gScreenTilt
		deliriumTilt = bb.readInt();
		deliriumTurn = bb.readInt();
		deliriumPitch = bb.readInt();
	}
	
	public void LoadUserEpisodeInfo(Resource bb)
	{
		gUserEpisode = bb.readBoolean();
		if(gUserEpisode) {
			byte[] buf = new byte[144];
			bb.read(buf);
			addonFileName = Strhandler.toLowerCase(new String(buf).trim());
		}
	}

	public void LoadGDXBlock(Resource bb)
	{
		LoadUserEpisodeInfo(bb);
		
		byte[] data = new byte[SAVEGDXDATA];
		bb.read(data);
		int pos = 0;
		
		safeGameInfo.nEnemyDamage = data[pos++];
		safeGameInfo.nEnemyQuantity = data[pos++];
		safeGameInfo.nDifficulty = data[pos++];
		safeGameInfo.nPitchforkOnly = data[pos++] == 1;
		gInfiniteAmmo = data[pos++] == 1;
	}
	
	public BloodIniFile LoadGDXHeader(Resource fil) {
		addon = null;
		addonFileName = null;
		message = null;
		gUserEpisode = false;
		safeGameInfo.nDifficulty = -1;
		safeGameInfo.nEpisode = -1;
		safeGameInfo.nLevel = -1;
		
		try {
			fil.seek(SAVEINFO + SAVESCREENSHOTSIZE, Whence.Set);
			LoadUserEpisodeInfo(fil);
			fil.seek(SAVEGDXDATA, Whence.Current);
			LoadGameInfo(fil);
			if(gUserEpisode) //try to find addon
				addon = levelGetEpisode(addonFileName);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return addon;
	}
}
