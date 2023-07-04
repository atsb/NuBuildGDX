package ru.m210projects.Tekwar.Types;

import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.MAXPSKYTILES;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.MAXWALLS;
import static ru.m210projects.Build.Engine.MAXWALLSV7;
import static ru.m210projects.Tekwar.Animate.MAXANIMATES;
import static ru.m210projects.Tekwar.Tekldsv.SAVEGDXDATA;
import static ru.m210projects.Tekwar.Tekldsv.SAVEHEADER;
import static ru.m210projects.Tekwar.Tekldsv.SAVELEVELINFO;
import static ru.m210projects.Tekwar.Tekldsv.SAVESCREENSHOTSIZE;
import static ru.m210projects.Tekwar.Tekmap.MAXSYMBOLS;
import static ru.m210projects.Tekwar.Tektag.MAXANIMPICS;
import static ru.m210projects.Tekwar.Tektag.MAXDELAYFUNCTIONS;
import static ru.m210projects.Tekwar.Tektag.MAXDOORS;
import static ru.m210projects.Tekwar.Tektag.MAXFLOORDOORS;
import static ru.m210projects.Tekwar.Tektag.MAXMAPSOUNDFX;
import static ru.m210projects.Tekwar.Tektag.MAXSECTORVEHICLES;
import static ru.m210projects.Tekwar.Tektag.MAXSPRITEELEVS;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Tekwar.Player;

public class SafeLoader {

	public String boardfilename;

	public int gAnimationCount = 0;
	public ANIMATION[] gAnimationData = new ANIMATION[MAXANIMATES];
	public Player[] gPlayer = new Player[MAXPLAYERS];
	public short numplayers;

	public short pskyoff[] = new short[MAXPSKYTILES], pskybits;
	public int parallaxyscale, parallaxyoffs;
	public byte parallaxtype;
	public int visibility, parallaxvisibility;

	public short connectpoint2[] = new short[MAXPLAYERS];
	public int randomseed;

	public boolean gUserMap;
	public int mission, difficulty;
	public int hours, minutes, seconds;
	public int lockclock;

	// MapInfo

	public short numsectors, numwalls;

	public short[] headspritesect, headspritestat;
	public short[] prevspritesect, prevspritestat;
	public short[] nextspritesect, nextspritestat;

	public SECTOR[] sector = new SECTOR[MAXSECTORS];
	public WALL[] wall = new WALL[MAXWALLS];
	public SPRITE[] sprite = new SPRITE[MAXSPRITES];

	public byte automapping;
	public byte[] show2dsector = new byte[(MAXSECTORS + 7) >> 3];
	public byte[] show2dwall = new byte[(MAXWALLSV7 + 7) >> 3];
	public byte[] show2dsprite = new byte[(MAXSPRITES + 7) >> 3];

	public SpriteXT[] spriteXT = new SpriteXT[MAXSPRITES];

	public boolean[] symbols = new boolean[MAXSYMBOLS];
	public boolean[] symbolsdeposited = new boolean[MAXSYMBOLS];

	public int currentmapno;
	public int allsymsdeposited;
	public char generalplay;
	public char singlemapmode;
	public int killedsonny;
	public int civillianskilled;
	public char mission_accomplished;
	public char numlives;

	public boolean goreflag;

	public Delayfunc delayfunc[] = new Delayfunc[MAXDELAYFUNCTIONS];
	public Animpic[] animpic = new Animpic[MAXANIMPICS];
	public Spriteelev[] spriteelev = new Spriteelev[MAXSPRITEELEVS];
	public Doortype[] doortype = new Doortype[MAXDOORS];
	public Floordoor[] floordoor = new Floordoor[MAXFLOORDOORS];
	public Sectoreffect[] sectoreffect = new Sectoreffect[MAXSECTORS];
	public Elevatortype[] elevator = new Elevatortype[MAXSECTORS];
	public Sectorvehicle[] sectorvehicle = new Sectorvehicle[MAXSECTORVEHICLES];
	public Mapsndfxtype[] mapsndfx = new Mapsndfxtype[MAXMAPSOUNDFX];

	public int doorxref[] = new int[MAXSECTORS];
	public int fdxref[] = new int[MAXSECTORS];
	public int sexref[] = new int[MAXSECTORS];
	public boolean[] onelev = new boolean[MAXPLAYERS];

	public int numanimates;
	public short numdelayfuncs;
	public int secnt;
	public int numdoors, numfloordoors, numvehicles;
	public int sprelevcnt;
	public int totalmapsndfx;

	// Board animation variables
	public short rotatespritelist[] = new short[16], rotatespritecnt;
	public short warpsectorlist[] = new short[64], warpsectorcnt;
	public short xpanningsectorlist[] = new short[16], xpanningsectorcnt;
	public short ypanningwalllist[] = new short[64], ypanningwallcnt;
	public short floorpanninglist[] = new short[64], floorpanningcnt;
	public short dragsectorlist[] = new short[16], dragxdir[] = new short[16], dragydir[] = new short[16],
			dragsectorcnt;
	public int[] dragx1 = new int[16], dragy1 = new int[16], dragx2 = new int[16], dragy2 = new int[16],
			dragfloorz = new int[16];
	public short swingcnt, swingwall[][] = new short[32][5], swingsector[] = new short[32];
	public short swingangopen[] = new short[32], swingangclosed[] = new short[32], swingangopendir[] = new short[32];
	public short swingang[] = new short[32], swinganginc[] = new short[32];
	public int[][] swingx = new int[32][8], swingy = new int[32][8];
	public short revolvesector[] = new short[4], revolveang[] = new short[4], revolvecnt;
	public int[][] revolvex = new int[4][32], revolvey = new int[4][32];
	public int[] revolvepivotx = new int[4], revolvepivoty = new int[4];
	public short subwaytracksector[][] = new short[4][128], subwaynumsectors[] = new short[4], subwaytrackcnt;
	public int[] subwaystop[] = new int[4][8], subwaystopcnt = new int[4];
	public int[] subwaytrackx1 = new int[4], subwaytracky1 = new int[4];
	public int[] subwaytrackx2 = new int[4], subwaytracky2 = new int[4];
	public int[] subwayx = new int[4], subwaygoalstop = new int[4], subwayvel = new int[4],
			subwaypausetime = new int[4];
	public short waterfountainwall[] = new short[MAXPLAYERS], waterfountaincnt[] = new short[MAXPLAYERS];
	public short slimesoundcnt[] = new short[MAXPLAYERS];

	public SafeLoader() {
		for (int i = 0; i < MAXPLAYERS; i++)
			gPlayer[i] = new Player();

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
		for (int i = 0; i < MAXSPRITES; i++)
			spriteXT[i] = new SpriteXT();
		for (int i = 0; i < MAXANIMPICS; i++)
			animpic[i] = new Animpic();
		for (int i = 0; i < MAXDELAYFUNCTIONS; i++)
			delayfunc[i] = new Delayfunc();
		for (int i = 0; i < MAXSPRITEELEVS; i++)
			spriteelev[i] = new Spriteelev();
		for (int i = 0; i < MAXDOORS; i++)
			doortype[i] = new Doortype();
		for (int i = 0; i < MAXFLOORDOORS; i++)
			floordoor[i] = new Floordoor();
		for (int i = 0; i < MAXSECTORS; i++)
			sectoreffect[i] = new Sectoreffect();
		for (int i = 0; i < MAXSECTORS; i++)
			elevator[i] = new Elevatortype();
		for (int i = 0; i < MAXSECTORVEHICLES; i++)
			sectorvehicle[i] = new Sectorvehicle();
		for (int i = 0; i < MAXMAPSOUNDFX; i++)
			mapsndfx[i] = new Mapsndfxtype();
	}

	public boolean load(Resource bb) {
		try {
			bb.seek(SAVEHEADER - SAVELEVELINFO, Whence.Set);

			mission = bb.readInt();
			difficulty = bb.readInt();

			bb.seek(SAVEHEADER, Whence.Set);

			LoadGDXBlock(bb);
			PlayerLoad(bb);
			MapLoad(bb);
			SectorLoad(bb);
			AnimationLoad(bb);

			randomseed = bb.readInt();

			hours = bb.readInt();
			minutes = bb.readInt();
			seconds = bb.readInt();
			lockclock = bb.readInt();

			parallaxtype = bb.readByte();
			parallaxyoffs = bb.readInt();
			for (int i = 0; i < MAXPSKYTILES; i++)
				pskyoff[i] = bb.readShort();
			pskybits = bb.readShort();
			visibility = bb.readInt();
			parallaxvisibility = bb.readInt();

			bb.read(show2dsector);
			bb.read(show2dwall);
			bb.read(show2dsprite);
			automapping = bb.readByte();

			goreflag = bb.readBoolean();

			TagLoad(bb);
			for (int i = 0; i < MAXSPRITES; i++)
				spriteXT[i].load(bb);
			MissionInfoLoad(bb);

			if (bb.position() == bb.size())
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void TagLoad(Resource bb) {
		numanimates = bb.readInt();
		for (int i = 0; i < numanimates; i++)
			animpic[i].load(bb);
		
		numdelayfuncs = bb.readShort();
		for (int i = 0; i < numdelayfuncs; i++)
			delayfunc[i].load(bb);
		
		for (int i = 0; i < MAXPLAYERS; i++)
			onelev[i] = bb.readBoolean();
		
		secnt = bb.readInt();
		for (int i = 0; i < MAXSECTORS; i++) 
			sectoreffect[i].load(bb);

		for (int i = 0; i < MAXSECTORS; i++)
			sexref[i] = bb.readInt();
		
		numdoors = bb.readInt();
		for (int i = 0; i < numdoors; i++)
			doortype[i].load(bb);

		for (int i = 0; i < MAXSECTORS; i++)
			doorxref[i] = bb.readInt();

		numfloordoors = bb.readInt();
		for (int i = 0; i < numfloordoors; i++)
			floordoor[i].load(bb);

		for (int i = 0; i < MAXSECTORS; i++)
			fdxref[i] = bb.readInt();

		numvehicles = bb.readInt();
		for (int i = 0; i < numvehicles; i++)
			sectorvehicle[i].load(bb);

		for (int i = 0; i < MAXSECTORS; i++)
			elevator[i].load(bb);
		
		sprelevcnt = bb.readInt();
		for (int i = 0; i < sprelevcnt; i++)
			spriteelev[i].load(bb);
		
		totalmapsndfx = bb.readInt();
		for (int i = 0; i < totalmapsndfx; i++)
			mapsndfx[i].load(bb);
	}

	public void MissionInfoLoad(Resource bb) {
		for (int i = 0; i < MAXSYMBOLS; i++)
			symbols[i] = bb.readBoolean();
		for (int i = 0; i < MAXSYMBOLS; i++)
			symbolsdeposited[i] = bb.readBoolean();

		currentmapno = bb.readInt();
		numlives = (char) (bb.readByte() & 0xFF);
		mission_accomplished = (char) (bb.readByte() & 0xFF);
		civillianskilled = bb.readInt();
		generalplay = (char) (bb.readByte() & 0xFF);
		singlemapmode = (char) (bb.readByte() & 0xFF);
		allsymsdeposited = bb.readInt();
		killedsonny = bb.readInt();
	}

	public void PlayerLoad(Resource bb) {
		numplayers = bb.readShort();
		for (int i = 0; i < numplayers - 1; i++)
			connectpoint2[i] = (short) (i + 1);
		connectpoint2[numplayers - 1] = -1;

		for (int i = 0; i < MAXPLAYERS; i++) {
			gPlayer[i].set(bb);
			gPlayer[i].pInput.load(bb);
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
			gAnimationData[i].acc = bb.readInt();
		}
		gAnimationCount = bb.readInt();
	}

	public void SectorLoad(Resource bb) {
		for (int i = 0; i < 16; i++)
			rotatespritelist[i] = bb.readShort();
		rotatespritecnt = bb.readShort();
		for (int i = 0; i < 16; i++)
			warpsectorlist[i] = bb.readShort();
		warpsectorcnt = bb.readShort();
		for (int i = 0; i < 16; i++)
			xpanningsectorlist[i] = bb.readShort();
		xpanningsectorcnt = bb.readShort();
		for (int i = 0; i < 64; i++)
			ypanningwalllist[i] = bb.readShort();
		ypanningwallcnt = bb.readShort();
		for (int i = 0; i < 64; i++)
			floorpanninglist[i] = bb.readShort();
		floorpanningcnt = bb.readShort();
		for (int i = 0; i < 16; i++)
			dragsectorlist[i] = bb.readShort();
		for (int i = 0; i < 16; i++)
			dragxdir[i] = bb.readShort();
		for (int i = 0; i < 16; i++)
			dragydir[i] = bb.readShort();
		dragsectorcnt = bb.readShort();
		for (int i = 0; i < 16; i++)
			dragx1[i] = bb.readInt();
		for (int i = 0; i < 16; i++)
			dragy1[i] = bb.readInt();
		for (int i = 0; i < 16; i++)
			dragx2[i] = bb.readInt();
		for (int i = 0; i < 16; i++)
			dragy2[i] = bb.readInt();
		for (int i = 0; i < 16; i++)
			dragfloorz[i] = bb.readInt();
		swingcnt = bb.readShort();
		for (int i = 0; i < 32; i++)
			for (int j = 0; j < 5; j++)
				swingwall[i][j] = bb.readShort();
		for (int i = 0; i < 32; i++)
			swingsector[i] = bb.readShort();
		for (int i = 0; i < 32; i++)
			swingangopen[i] = bb.readShort();
		for (int i = 0; i < 32; i++)
			swingangclosed[i] = bb.readShort();
		for (int i = 0; i < 32; i++)
			swingangopendir[i] = bb.readShort();
		for (int i = 0; i < 32; i++)
			swingang[i] = bb.readShort();
		for (int i = 0; i < 32; i++)
			swinganginc[i] = bb.readShort();
		for (int i = 0; i < 32; i++)
			for (int j = 0; j < 8; j++)
				swingx[i][j] = bb.readInt();
		for (int i = 0; i < 32; i++)
			for (int j = 0; j < 8; j++)
				swingy[i][j] = bb.readInt();
		for (int i = 0; i < 4; i++)
			revolvesector[i] = bb.readShort();
		for (int i = 0; i < 4; i++)
			revolveang[i] = bb.readShort();
		revolvecnt = bb.readShort();
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 16; j++)
				revolvex[i][j] = bb.readInt();
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 16; j++)
				revolvey[i][j] = bb.readInt();
		for (int i = 0; i < 4; i++)
			revolvepivotx[i] = bb.readInt();
		for (int i = 0; i < 4; i++)
			revolvepivoty[i] = bb.readInt();
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 128; j++)
				subwaytracksector[i][j] = bb.readShort();
		for (int i = 0; i < 4; i++)
			subwaynumsectors[i] = bb.readShort();
		subwaytrackcnt = bb.readShort();
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 8; j++)
				subwaystop[i][j] = bb.readInt();
		for (int i = 0; i < 4; i++)
			subwaystopcnt[i] = bb.readInt();
		for (int i = 0; i < 4; i++)
			subwaytrackx1[i] = bb.readInt();
		for (int i = 0; i < 4; i++)
			subwaytracky1[i] = bb.readInt();
		for (int i = 0; i < 4; i++)
			subwaytrackx2[i] = bb.readInt();
		for (int i = 0; i < 4; i++)
			subwaytracky2[i] = bb.readInt();
		for (int i = 0; i < 4; i++)
			subwayx[i] = bb.readInt();
		for (int i = 0; i < 4; i++)
			subwaygoalstop[i] = bb.readInt();
		for (int i = 0; i < 4; i++)
			subwayvel[i] = bb.readInt();
		for (int i = 0; i < 4; i++)
			subwaypausetime[i] = bb.readInt();
		for (int i = 0; i < MAXPLAYERS; i++)
			waterfountainwall[i] = bb.readShort();
		for (int i = 0; i < MAXPLAYERS; i++)
			waterfountaincnt[i] = bb.readShort();
		for (int i = 0; i < MAXPLAYERS; i++)
			slimesoundcnt[i] = bb.readShort();
	}
	
	public void LoadGDXHeader(Resource bb) {
		mission = -1;
		difficulty = -1;
		gUserMap = false;
		boardfilename = null;

		try {
			bb.seek(SAVEHEADER - SAVELEVELINFO, Whence.Set);

			mission = bb.readInt();
			difficulty = bb.readInt();

			bb.seek(SAVEHEADER, Whence.Set);
			LoadGDXBlock(bb);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void LoadGDXBlock(Resource bb) {
		int pos = bb.position();
		bb.seek(pos + SAVESCREENSHOTSIZE, Whence.Set);

		// reserve SAVEGDXDATA bytes for extra data
		gUserMap = bb.readBoolean();
		boardfilename = null;
		String name = bb.readString(144).trim();
		if (!name.isEmpty())
			boardfilename = name;

		bb.seek(pos + SAVEGDXDATA, Whence.Set);
	}

	public void MapLoad(Resource bb) {
		numwalls = bb.readShort();
		for (int w = 0; w < numwalls; w++)
			wall[w].buildWall(bb);
		numsectors = bb.readShort();
		for (int s = 0; s < numsectors; s++)
			sector[s].buildSector(bb);
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

}
