package ru.m210projects.Witchaven.Types;

import static ru.m210projects.Build.Engine.MAXPSKYTILES;
import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.MAXWALLS;
import static ru.m210projects.Build.Engine.MAXWALLSV7;
import static ru.m210projects.Witchaven.Menu.WHMenuUserContent.getEpisode;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Witchaven.Whldsv.*;
import static ru.m210projects.Witchaven.Animate.MAXANIMATES;
import static ru.m210projects.Witchaven.Globals.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;

public class SafeLoader {

	public String boardfilename;

	public int gAnimationCount = 0;
	public ANIMATION[] gAnimationData = new ANIMATION[MAXANIMATES];
	public PLAYER plr[] = new PLAYER[MAXPLAYERS];
	
	public short pskyoff[] = new short[MAXPSKYTILES], pskybits;
	public int parallaxyscale;
	
	public short connecthead, connectpoint2[] = new short[MAXPLAYERS];
	
	public int randomseed;

	public boolean gUserMap;
	public int mapon, skill;
	
	public int thunderflash;
	public int thundertime;
	
	public int kills;
	public int killcnt;
	public int treasurescnt;
	public int treasuresfound;
	public int hours, minutes, seconds;
	
	public int victor = 0;
	public int autohoriz = 0;

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
	
	//WHFX
	
	public short skypanlist[] = new short[64], skypancnt;	
	public short[] lavadrylandsector = new short[32];
	public short lavadrylandcnt;
	public short bobbingsectorlist[] = new short[16], bobbingsectorcnt;

	public short revolveclip[]= new short[16];
	public short revolvesector[]= new short[4], revolveang[]= new short[4], revolvecnt;
	public int[][] revolvex= new int[4][32], revolvey= new int[4][32];
	public int[] revolvepivotx= new int[4], revolvepivoty= new int[4];
	
	
	//WHTAG
	
	public short warpsectorlist[] = new short[64], warpsectorcnt;
	public short xpanningsectorlist[] = new short[16], xpanningsectorcnt;
	public short ypanningwalllist[] = new short[128], ypanningwallcnt;
	public short floorpanninglist[] = new short[64], floorpanningcnt;
	public SWINGDOOR[] swingdoor = new SWINGDOOR[MAXSWINGDOORS];
	public short swingcnt;

	public short dragsectorlist[] = new short[16], dragxdir[] = new short[16], dragydir[] = new short[16], dragsectorcnt;
	public int[] dragx1 = new int[16], dragy1 = new int[16], dragx2 = new int[16], dragy2 = new int[16], dragfloorz = new int[16];
	
	public short ironbarsector[] = new short[16];
	public short ironbarscnt;
	public int[] ironbarsgoal1 = new int[16], ironbarsgoal2 = new int[16];
	public short[] ironbarsdone = new short[16], ironbarsanim = new short[16];
	public int[] ironbarsgoal = new int[16];
	
	public int floormirrorcnt;
	public short[] floormirrorsector = new short[64];
	public short[] arrowsprite = new short[ARROWCOUNTLIMIT], throwpikesprite = new short[THROWPIKELIMIT];
	public byte[] ceilingshadearray = new byte[MAXSECTORS];
	public byte[] floorshadearray = new byte[MAXSECTORS];
	public byte[] wallshadearray = new byte[MAXWALLS];

	public int dropshieldcnt;
	public boolean droptheshield;

	public int warp_on;

	public EpisodeInfo addon;
	public boolean packedAddon;
	private String message;
	
	public SafeLoader()
	{
		for(int i = 0; i < MAXPLAYERS; i++)
			plr[i] = new PLAYER();
		
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
			swingdoor[i] = new SWINGDOOR();
	}
	
	public boolean load(Resource bb)
	{
		try {
			bb.seek(SAVEHEADER - SAVELEVELINFO, Whence.Set);
			
			addon = null;
			message = null;
			
			mapon = bb.readInt();
			skill = bb.readInt();

			bb.seek(SAVEHEADER, Whence.Set);
	
			LoadGDXBlock(bb);
			StuffLoad(bb);
			MapLoad(bb);
			SectorLoad(bb);
			AnimationLoad(bb);
			
			if(bb.position() == bb.size()) {
				if (warp_on == 2) { // try to find addon
					if(packedAddon)
						addon = getEpisode(BuildGdx.compat.checkFile(boardfilename));
					else addon = getEpisode(BuildGdx.compat.checkDirectory(boardfilename));
					if (addon == null) {
						message = "Can't find user episode file: " + boardfilename;
						warp_on = 1;

						mapon = 0;
					}
				}
				
				return true;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void SectorLoad(Resource bb)
	{
		for(int i = 0; i < 64; i++)
			skypanlist[i] = bb.readShort();
		skypancnt = bb.readShort();
		for(int i = 0; i < 32; i++)
			lavadrylandsector[i] = bb.readShort();
		lavadrylandcnt = bb.readShort();
		for(int i = 0; i < 16; i++) 
		{
			dragsectorlist[i] = bb.readShort();
			dragxdir[i] = bb.readShort();
			dragydir[i] = bb.readShort();
			dragx1[i] = bb.readInt();
			dragy1[i] = bb.readInt();
	        dragx2[i] = bb.readInt();
	        dragy2[i] = bb.readInt();
	        dragfloorz[i] = bb.readInt();
		}
		dragsectorcnt = bb.readShort();
		for(int i = 0; i < 16; i++) 
			bobbingsectorlist[i] = bb.readShort(); 
        bobbingsectorcnt = bb.readShort();
		for(int i = 0; i < 16; i++)
			warpsectorlist[i] = bb.readShort();
		warpsectorcnt = bb.readShort();
		for(int i = 0; i < 16; i++)
			xpanningsectorlist[i] = bb.readShort();
		xpanningsectorcnt = bb.readShort();
		for(int i = 0; i < 128; i++)
			ypanningwalllist[i] = bb.readShort();
		ypanningwallcnt = bb.readShort();
		for(int i = 0; i < 64; i++)
			floorpanninglist[i] = bb.readShort();
		floorpanningcnt = bb.readShort();
		
		swingcnt = bb.readShort();
		for(int i = 0; i < MAXSWINGDOORS; i++)
			swingdoor[i].set(bb);
		
		revolvecnt = bb.readShort();
		for(int i = 0; i < 4; i++) {
			revolvesector[i] = bb.readShort();
			revolveang[i] = bb.readShort();
			for(int j = 0; j < 32; j++) {
				revolvex[i][j] = bb.readInt();
				revolvey[i][j] = bb.readInt();
			}
			revolvepivotx[i] = bb.readInt();
			revolvepivoty[i] = bb.readInt();
		}
		for(int i = 0; i < 16; i++) 
			revolveclip[i] = bb.readShort();
		
		ironbarscnt = bb.readShort();
		for(int i = 0; i < 16; i++) {
			ironbarsector[i] = bb.readShort(); 
			ironbarsgoal[i] = bb.readInt(); 
			ironbarsgoal1[i] = bb.readInt(); 
			ironbarsgoal2[i] = bb.readInt(); 
			ironbarsdone[i] = bb.readShort(); 
			ironbarsanim[i] = bb.readShort(); 
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
		kills = bb.readInt();
		killcnt = bb.readInt();
		treasurescnt = bb.readInt();
		treasuresfound = bb.readInt();
		hours = bb.readInt();
		minutes = bb.readInt();
		seconds = bb.readInt();
		
		visibility = bb.readInt();
		thunderflash = bb.readInt();
		thundertime = bb.readInt();
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
		
		dropshieldcnt = bb.readInt();
		droptheshield = bb.readByte() == 1;

		//WH2
		floormirrorcnt = bb.readInt();
		for(int i = 0; i < 64; i++)
			floormirrorsector[i] = bb.readShort();
		for(int i = 0; i < ARROWCOUNTLIMIT; i++)
			arrowsprite[i] = bb.readShort();
		for(int i = 0; i < THROWPIKELIMIT; i++)
			throwpikesprite[i] = bb.readShort();
		for(int i = 0; i < MAXSECTORS; i++) {
			ceilingshadearray[i] = bb.readByte();
			floorshadearray[i] = bb.readByte();
		}
		for(int i = 0; i < MAXWALLS; i++)
			wallshadearray[i] = bb.readByte();
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
	
	public void LoadGDXBlock(Resource bb)
	{
		int pos = bb.position();
		bb.seek(pos + SAVESCREENSHOTSIZE, Whence.Set);
		
		//reserve SAVEGDXDATA bytes for extra data
		warp_on = bb.readByte();
		if(warp_on == 2)
			packedAddon = bb.readBoolean();
		boardfilename = null;
		String name = bb.readString(144).trim();
		if(!name.isEmpty()) boardfilename = name;

		bb.seek(pos + SAVEGDXDATA, Whence.Set);
	}
	
	public EpisodeInfo LoadGDXHeader(Resource bb) {
		warp_on = 0;
		packedAddon = false;
		addon = null;
		boardfilename = null;
		
		try {
			bb.seek(SAVEHEADER - SAVELEVELINFO, Whence.Set);
			
			addon = null;
			message = null;
			
			mapon = bb.readInt();
			skill = bb.readInt();

			bb.seek(SAVEHEADER, Whence.Set);
	
			LoadGDXBlock(bb);

			if (warp_on == 2) { // try to find addon
				if(packedAddon)
					addon = getEpisode(BuildGdx.compat.checkFile(boardfilename));
				else addon = getEpisode(BuildGdx.compat.checkDirectory(boardfilename));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return addon;
	}
	
	public String getMessage() {
		return message;
	}
}
