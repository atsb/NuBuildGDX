// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP.Types;

import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.MAXPSKYTILES;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.MAXWALLS;
import static ru.m210projects.Build.Engine.MAXWALLSV7;
import static ru.m210projects.LSP.Animate.MAXANIMATES;
import static ru.m210projects.LSP.LoadSave.SAVEGDXDATA;
import static ru.m210projects.LSP.LoadSave.SAVEHEADER;
import static ru.m210projects.LSP.LoadSave.SAVELEVELINFO;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;

public class SafeLoader {

	public int gAnimationCount = 0;
	public ANIMATION[] gAnimationData = new ANIMATION[MAXANIMATES];
	public PlayerStruct plr[] = new PlayerStruct[MAXPLAYERS];
	
	public int[] gEnemyClock = new int[MAXSPRITES];
	public short gMoveStatus[] = new short[MAXSPRITES];
	public short nKills[] = new short[6];
	public short nTotalKills[] = new short[6];
	public int nEnemyKills, nEnemyMax; 
	
	public short nDiffDoor, nDiffDoorBack, nTrainWall;
	public boolean bActiveTrain, bTrainSoundSwitch;
	public int lockclock;
	public int totalmoves;
	public short nPlayerFirstWeapon = 17;
	public int oldchoose;
	public short oldpic;
	public int mapnum, skill;

	public short pskyoff[] = new short[MAXPSKYTILES], pskybits;
	public int parallaxyscale;
	public short connecthead, connectpoint2[] = new short[MAXPLAYERS];
	public int randomseed;
	public int visibility;
	public byte automapping;
	public byte[] show2dsector = new byte[(MAXSECTORS + 7) >> 3];
	public byte[] show2dwall = new byte[(MAXWALLSV7 + 7) >> 3];
	public byte[] show2dsprite = new byte[(MAXSPRITES + 7) >> 3];
	
	//MapInfo
	
	public short numsectors, numwalls;

	public short[] headspritesect, headspritestat;
	public short[] prevspritesect, prevspritestat;
	public short[] nextspritesect, nextspritestat;

	public SECTOR[] sector = new SECTOR[MAXSECTORS];
	public WALL[] wall = new WALL[MAXWALLS];
	public SPRITE[] sprite = new SPRITE[MAXSPRITES];

	public short waterfountainwall[] = new short[MAXPLAYERS], waterfountaincnt[] = new short[MAXPLAYERS];
	public short ypanningwallcnt;
	public short[] ypanningwalllist = new short[16];
	public short floorpanningcnt;
	public short[] floorpanninglist = new short[16];
	public short warpsectorcnt;
	public short[] warpsectorlist = new short[16];
	public short xpanningsectorcnt;
	public short[] xpanningsectorlist = new short[16];
	public short warpsector2cnt;
	public short[] warpsector2list = new short[32];
	public short subwaytracksector[][] = new short[5][128], subwaynumsectors[] = new short[5], subwaytrackcnt;
	public int[] subwaystop[] = new int[5][8], subwaystopcnt = new int[5];
	public int[] subwaytrackx1 = new int[5], subwaytracky1 = new int[5];
	public int[] subwaytrackx2 = new int[5], subwaytracky2 = new int[5];
	public int[] subwayx = new int[5], subwaygoalstop = new int[5], subwayvel = new int[5],
			subwaypausetime = new int[5];
	public short revolvesector[] = new short[4], revolveang[] = new short[4], revolvecnt;
	public int[][] revolvex = new int[4][48], revolvey = new int[4][48];
	public int[] revolvepivotx = new int[4], revolvepivoty = new int[4];
	
	public short swingcnt;
	public final int   MAXSWINGDOORS = 32;
	public SwingDoor[] swingdoor = new SwingDoor[MAXSWINGDOORS];

	public short dragsectorlist[] = new short[16], dragxdir[] = new short[16], dragydir[] = new short[16],
			dragsectorcnt;
	public int[] dragx1 = new int[16], dragy1 = new int[16], dragx2 = new int[16], dragy2 = new int[16],
			dragfloorz = new int[16];
	
	
	public SafeLoader()
	{
		for(int i = 0; i < MAXPLAYERS; i++)
			plr[i] = new PlayerStruct();
		
		headspritesect = new short[MAXSECTORS + 1]; 
		headspritestat = new short[MAXSTATUS + 1];
		prevspritesect = new short[MAXSPRITES]; 
		prevspritestat = new short[MAXSPRITES];
		nextspritesect = new short[MAXSPRITES]; 
		nextspritestat = new short[MAXSPRITES];

		for(int i = 0; i < MAXANIMATES; i++)
			gAnimationData[i] = new ANIMATION();
		for(int i = 0; i < MAXSPRITES; i++) 
			sprite[i] = new SPRITE();
		for(int i = 0; i < MAXSECTORS; i++)
			sector[i] = new SECTOR();
		for(int i = 0; i < MAXWALLS; i++)
			wall[i] = new WALL();
		for(int i = 0; i < MAXSWINGDOORS; i++)
			swingdoor[i] = new SwingDoor();
	}
	
	public boolean load(Resource bb)
	{
		try {
			bb.seek(SAVEHEADER - SAVELEVELINFO, Whence.Set);
			
			mapnum = bb.readInt();
			skill = bb.readInt();

			bb.seek(SAVEHEADER, Whence.Set);
	
			LoadGDXBlock(bb);
			StuffLoad(bb);
			MapLoad(bb);
			SectorLoad(bb);
			AnimationLoad(bb);
			
			if(bb.position() == bb.size())
				return true;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void SectorLoad(Resource bb)
	{
		for (int i = 0; i < MAXPLAYERS; i++)
			waterfountainwall[i] = bb.readShort();
		for (int i = 0; i < MAXPLAYERS; i++)
			waterfountaincnt[i] = bb.readShort();

		ypanningwallcnt = bb.readShort();
		for (int i = 0; i < 16; i++)
			ypanningwalllist[i] = bb.readShort();
		floorpanningcnt = bb.readShort();
		for (int i = 0; i < 16; i++)
			floorpanninglist[i] = bb.readShort();
		warpsectorcnt = bb.readShort();
		for (int i = 0; i < 16; i++)
			warpsectorlist[i] = bb.readShort();
		xpanningsectorcnt = bb.readShort();
		for (int i = 0; i < 16; i++)
			xpanningsectorlist[i] = bb.readShort();
		warpsector2cnt = bb.readShort();
		for (int i = 0; i < 32; i++)
			warpsector2list[i] = bb.readShort();

		subwaytrackcnt = bb.readShort();
		for (int a = 0; a < 5; ++a)
			for (int b = 0; b < 128; ++b)
				subwaytracksector[a][b] = bb.readShort();
		for (int i = 0; i < 5; i++)
			subwaynumsectors[i] = bb.readShort();

		for (int a = 0; a < 5; ++a)
			for (int b = 0; b < 8; ++b)
				subwaystop[a][b] = bb.readInt();
		for (int i = 0; i < 5; i++)
			subwaystopcnt[i] = bb.readInt();
		for (int i = 0; i < 5; i++)
			subwaytrackx1[i] = bb.readInt();
		for (int i = 0; i < 5; i++)
			subwaytracky1[i] = bb.readInt();
		for (int i = 0; i < 5; i++)
			subwaytrackx2[i] = bb.readInt();
		for (int i = 0; i < 5; i++)
			subwaytracky2[i] = bb.readInt();
		for (int i = 0; i < 5; i++)
			subwayx[i] = bb.readInt();
		for (int i = 0; i < 5; i++)
			subwaygoalstop[i] = bb.readInt();
		for (int i = 0; i < 5; i++)
			subwayvel[i] = bb.readInt();
		for (int i = 0; i < 5; i++)
			subwaypausetime[i] = bb.readInt();

		revolvecnt = bb.readShort();
		for (int i = 0; i < 4; i++) {
			revolvesector[i] = bb.readShort();
			revolveang[i] = bb.readShort();
			for (int j = 0; j < 48; j++) {
				revolvex[i][j] = bb.readInt();
				revolvey[i][j] = bb.readInt();
			}
			revolvepivotx[i] = bb.readInt();
			revolvepivoty[i] = bb.readInt();
		}

		swingcnt  = bb.readShort();
		for (int i = 0; i < MAXSWINGDOORS; i++) {
			for (int j = 0; j < 8; j++)
				swingdoor[i].wall[j] = bb.readShort();
			swingdoor[i].sector = bb.readShort();
			swingdoor[i].angopen = bb.readShort();
			swingdoor[i].angclosed = bb.readShort();
			swingdoor[i].angopendir = bb.readShort();
			swingdoor[i].ang = bb.readShort();
			swingdoor[i].anginc = bb.readShort();
			for (int j = 0; j < 8; j++)
				swingdoor[i].x[j] = bb.readInt();
			for (int j = 0; j < 8; j++)
				swingdoor[i].y[j] = bb.readInt();
		}

		dragsectorcnt = bb.readShort();
		for (int i = 0; i < 16; i++) {
			dragsectorlist[i] = bb.readShort();
			dragxdir[i] = bb.readShort();
			dragydir[i] = bb.readShort();
			dragx1[i] = bb.readInt();
			dragy1[i] = bb.readInt();
			dragx2[i] = bb.readInt();
			dragy2[i] = bb.readInt();
			dragfloorz[i] = bb.readInt();
		}
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
			gAnimationData[i].acc = bb.readInt();
		}	
		gAnimationCount = bb.readInt();
	}
	
	public void StuffLoad(Resource bb)
	{
		for(int i = 0; i < MAXSPRITES; i++) {
			gEnemyClock[i] = bb.readInt();
			gMoveStatus[i] = bb.readShort();
		}
		
		for(int i = 0; i < 6; i++) {
			nKills[i] = bb.readShort();
			nTotalKills[i] = bb.readShort();
		}
		nEnemyKills = bb.readInt();
		nEnemyMax = bb.readInt();
		
		nDiffDoor = bb.readShort();
		nDiffDoorBack = bb.readShort();
		nTrainWall = bb.readShort();
		bActiveTrain = bb.readBoolean();
		bTrainSoundSwitch = bb.readBoolean();
		lockclock = bb.readInt();
		totalmoves = bb.readInt();
		
		
		visibility = bb.readInt();
		randomseed = bb.readInt();

		bb.read(show2dsector);
		bb.read(show2dwall);
		bb.read(show2dsprite);
		automapping = bb.readByte();
		
		pskybits = bb.readShort();
		parallaxyscale = bb.readInt();
		for(int i = 0; i < MAXPSKYTILES; i++)
			pskyoff[i] = bb.readShort();

		connecthead = bb.readShort();
        for(int i = 0; i < MAXPLAYERS; i++) 
        	connectpoint2[i] = bb.readShort();
  
		for(int i = 0; i < MAXPLAYERS; i++)
			plr[i].set(bb);
		
		nPlayerFirstWeapon = bb.readShort();
		oldchoose = bb.readInt();
		oldpic = bb.readShort();
	}
	
	public void MapLoad(Resource bb)
	{
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
	}
	
	public void LoadGDXHeader(Resource bb) {
		mapnum = -1;
		skill = -1;

		try {
			bb.seek(SAVEHEADER - SAVELEVELINFO, Whence.Set);
			
			mapnum = bb.readInt();
			skill = bb.readInt();

			bb.seek(SAVEHEADER, Whence.Set);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void LoadGDXBlock(Resource bb)
	{
		int pos = bb.position();

		bb.seek(pos + SAVEGDXDATA, Whence.Set);
	}
}
