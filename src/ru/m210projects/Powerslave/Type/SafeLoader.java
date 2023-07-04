// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave.Type;

import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.MAXWALLS;
import static ru.m210projects.Build.Engine.MAXWALLSV7;
import static ru.m210projects.Powerslave.Anim.MAX_LIL_ANIM;
import static ru.m210projects.Powerslave.Anim.loadAnm;
import static ru.m210projects.Powerslave.Bullet.loadBullets;
import static ru.m210projects.Powerslave.Enemies.Anubis.MAXANUBIS;
import static ru.m210projects.Powerslave.Enemies.Anubis.loadAnubis;
import static ru.m210projects.Powerslave.Enemies.Fish.MAX_FISHS;
import static ru.m210projects.Powerslave.Enemies.Fish.loadFish;
import static ru.m210projects.Powerslave.Enemies.LavaDude.MAX_LAVAS;
import static ru.m210projects.Powerslave.Enemies.LavaDude.loadLava;
import static ru.m210projects.Powerslave.Enemies.Lion.MAXLION;
import static ru.m210projects.Powerslave.Enemies.Lion.loadLion;
import static ru.m210projects.Powerslave.Enemies.Mummy.MAX_MUMMIES;
import static ru.m210projects.Powerslave.Enemies.Mummy.loadMummy;
import static ru.m210projects.Powerslave.Enemies.Queen.MAX_EGGS;
import static ru.m210projects.Powerslave.Enemies.Queen.loadQueen;
import static ru.m210projects.Powerslave.Enemies.Ra.loadRa;
import static ru.m210projects.Powerslave.Enemies.Rat.MAX_RAT;
import static ru.m210projects.Powerslave.Enemies.Rat.loadRat;
import static ru.m210projects.Powerslave.Enemies.Rex.MAX_REX;
import static ru.m210projects.Powerslave.Enemies.Rex.loadRex;
import static ru.m210projects.Powerslave.Enemies.Roach.MAXROACH;
import static ru.m210projects.Powerslave.Enemies.Roach.loadRoach;
import static ru.m210projects.Powerslave.Enemies.Scorp.MAXSCORP;
import static ru.m210projects.Powerslave.Enemies.Scorp.loadScorp;
import static ru.m210projects.Powerslave.Enemies.Set.MAX_SET;
import static ru.m210projects.Powerslave.Enemies.Set.loadSet;
import static ru.m210projects.Powerslave.Enemies.Spider.MAX_SPIDERS;
import static ru.m210projects.Powerslave.Enemies.Spider.loadSpider;
import static ru.m210projects.Powerslave.Enemies.Wasp.MAX_WASPS;
import static ru.m210projects.Powerslave.Enemies.Wasp.loadWasp;
import static ru.m210projects.Powerslave.Globals.nLocalPlayer;
import static ru.m210projects.Powerslave.Grenade.MAX_GRENADES;
import static ru.m210projects.Powerslave.Grenade.loadGrenades;
import static ru.m210projects.Powerslave.Light.loadLights;
import static ru.m210projects.Powerslave.LoadSave.SAVEGDXDATA;
import static ru.m210projects.Powerslave.LoadSave.SAVEHEADER;
import static ru.m210projects.Powerslave.LoadSave.SAVELEVELINFO;
import static ru.m210projects.Powerslave.LoadSave.SAVENAME;
import static ru.m210projects.Powerslave.LoadSave.SAVESCREENSHOTSIZE;
import static ru.m210projects.Powerslave.Object.loadBobs;
import static ru.m210projects.Powerslave.Object.loadDrips;
import static ru.m210projects.Powerslave.Object.loadElevs;
import static ru.m210projects.Powerslave.Object.loadObjects;
import static ru.m210projects.Powerslave.Object.loadTrails;
import static ru.m210projects.Powerslave.Object.loadTraps;
import static ru.m210projects.Powerslave.Object.loadWallFaces;
import static ru.m210projects.Powerslave.Random.loadRandom;
import static ru.m210projects.Powerslave.RunList.loadRunList;
import static ru.m210projects.Powerslave.Sector.loadLinks;
import static ru.m210projects.Powerslave.Sector.loadMoves;
import static ru.m210projects.Powerslave.Sector.loadPushBlocks;
import static ru.m210projects.Powerslave.Sector.loadSecExtra;
import static ru.m210projects.Powerslave.Slide.loadSlide;
import static ru.m210projects.Powerslave.Snake.MAX_SNAKES;
import static ru.m210projects.Powerslave.Snake.loadSnakes;
import static ru.m210projects.Powerslave.Sprites.loadBubbles;
import static ru.m210projects.Powerslave.Switch.MAXSWITCH;
import static ru.m210projects.Powerslave.Switch.loadSwitches;
import static ru.m210projects.Powerslave.RunList.MAXRUN;
import static ru.m210projects.Powerslave.Menus.PSMenuUserContent.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Powerslave.Slide.PointStruct;
import ru.m210projects.Powerslave.Slide.SlideStruct;
import ru.m210projects.Powerslave.Slide.SlideStruct2;
import ru.m210projects.Powerslave.Enemies.Enemy.EnemyStruct;
import ru.m210projects.Powerslave.Enemies.Lion.LionStruct;
import ru.m210projects.Powerslave.Enemies.Queen.QueenStruct;
import ru.m210projects.Powerslave.Enemies.Scorp.ScorpStruct;
import ru.m210projects.Powerslave.Enemies.Set.SetStruct;
import ru.m210projects.Powerslave.Enemies.Wasp.WaspStruct;

public class SafeLoader {
	
	private String message;
	
	//Classic mode
	
	public boolean gClassicMode;
	public String savename;
	public int level;
	public int best;
	public int lastlevel;
	
	public int warp_on;
	public String boardfilename;
	
	public short[] nPlayerWeapons = new short[8];
	public short[] nPlayerClip = new short[8];
	public short[] nPistolClip = new short[8];
	public short[] nPlayerLives = new short[8];
	public short[] nPlayerItem = new short[8];
	public PlayerStruct PlayerList[] = new PlayerStruct[8];
	
	//MapInfo
	
	public short numsectors, numwalls;

	public short[] headspritesect, headspritestat;
	public short[] prevspritesect, prevspritestat;
	public short[] nextspritesect, nextspritestat;

	public SECTOR[] sector = new SECTOR[MAXSECTORS];
	public WALL[] wall = new WALL[MAXWALLS];
	public SPRITE[] sprite = new SPRITE[MAXSPRITES];
		
	//Data
		
	public short[] nNetStartSprite = new short[8];
	public short[] nBodySprite = new short[50];
	public int nCurBodyNum, nBodyTotal;

	public short[] nChunkSprite = new short[75];
	public short[] nBodyGunSprite = new short[50];
	public int nCurChunkNum, nCurBodyGunNum, nChunkTotal;

	public int nAnimsFree;
	public short AnimsFree[] = new short[MAX_LIL_ANIM];
	public int AnimRunRec[] = new int[MAX_LIL_ANIM];
	public byte AnimFlags[] = new byte[MAX_LIL_ANIM];
	public AnimStruct AnimList[] = new AnimStruct[MAX_LIL_ANIM];

	public short initsect;
	public int initx, inity, initz, inita;
	public int nFreeze;
	
	public short connecthead, connectpoint2[] = new short[MAXPLAYERS];
	
	public int nXDamage, nYDamage;
	public short nDoppleSprite, nPlayerTorch; 
	public short nPlayerInvisible, nPlayerDouble, nPlayerViewSect, nPlayerFloorSprite, nPlayerScore, nPlayerGrenade;
	public short nPlayerSnake, nDestVertPan, dVertPan, nDeathType, nQuake;
	public boolean bTouchFloor;
	
	public int ElevCount;
	public ElevStruct Elevator[] = new ElevStruct[1024];

	public int WallFaceCount;
	public WallFaceStruct WallFace[] = new WallFaceStruct[4096];
	
	public int ObjectCount;
	public ObjectStruct ObjectList[] = new ObjectStruct[128];
	
	public int nDrips;
	public DripStruct sDrip[] = new DripStruct[50];

	public int nTrails;
	public int nTrailPoints;
	public TrailStruct sTrail[] = new TrailStruct[40];
	public TrailPointStruct sTrailPoint[] = new TrailPointStruct[100];
	public short nTrailPointPrev[] = new short[100];
	public short nTrailPointNext[] = new short[100];
	public short nTrailPointVal[] = new short[100];
	
	public int nTraps;
	public TrapStruct sTrap[] = new TrapStruct[40];
	public short[] nTrapInterval = new short[40];
	
	public int nBobs;
	public BobStruct sBob[] = new BobStruct[200];
	public short sBobID[] = new short[200];
	
	public int randA;
	public int randB;
	public int randC;
	
	public int lasthitx, lasthity, lasthitz;
	public int lasthitsect, lasthitwall, lasthitsprite;
	
	public int nBulletCount;
	public int nBulletsFree;
	public short BulletFree[] = new short[500];
	public short nBulletEnemy[] = new short[500];
	public BulletStruct BulletList[] = new BulletStruct[500];
	
	public int nMachineCount;
	public BubbleMachineStruct[] Machine = new BubbleMachineStruct[125];
	
	public int nFreeCount;
	public byte nBubblesFree[] = new byte[200];
	public BubbleStruct[] BubbleList = new BubbleStruct[200];

	public int nGrenadeCount;
	public int nGrenadesFree;
	
	public short GrenadeFree[] = new short[MAX_GRENADES];
	public short[] nGrenadePlayer = new short[MAX_GRENADES];
	public GrenadeStruct[] GrenadeList = new GrenadeStruct[MAX_GRENADES];
	
	public int nSnakesFree;
	public short SnakeFree[] = new short[MAX_SNAKES];
	public short[] nSnakePlayer = new short[MAX_SNAKES];
	public SnakeStruct[] SnakeList = new SnakeStruct[MAX_SNAKES];
	
	public int totalmoves;
	public int moveframes;

	public byte[] show2dsector = new byte[(MAXSECTORS + 7) >> 3];
	public byte[] show2dwall = new byte[(MAXWALLSV7 + 7) >> 3];
	public byte[] show2dsprite = new byte[(MAXSPRITES + 7) >> 3];
	public short nCreaturesLeft;
	public short nCreaturesMax;
	public short nSpiritSprite;
	public short nMagicCount;
	public short nRegenerates;
	public short nFirstRegenerate;
	public short nNetStartSprites;
	public short nCurStartSprite;
	public short nNetPlayerCount;
	public short nRadialSpr;
	public short nRadialDamage;
	public short nDamageRadius;
	public short nRadialOwner;
	public short nRadialBullet;
	public short nDronePitch;
	public short nFinaleSpr;
	public short lFinaleStart;
	public short nFinaleStage;
	public short nSmokeSparks;

	public int AnubisCount;
	public int nAnubisDrum;
	public EnemyStruct[] AnubisList = new EnemyStruct[MAXANUBIS];
	
	public int LionCount;
	public LionStruct LionList[] = new LionStruct[MAXLION];
	
	public int FishCount;
	public int nChunksFree;
	public int nFreeChunk[] = new int[MAX_FISHS];
	public FishChunk[] FishChunk = new FishChunk[MAX_FISHS];
	public EnemyStruct[] FishList = new EnemyStruct[MAX_FISHS];

	public int LavaCount;
	public EnemyStruct LavaList[] = new EnemyStruct[MAX_LAVAS];
	
	public int MummyCount;
	public EnemyStruct MummyList[] = new EnemyStruct[MAX_MUMMIES];
	
	public int QueenCount;
	public short[] nEggFree = new short[MAX_EGGS];
	public int nEggsFree;
	public EnemyStruct[] QueenEgg = new EnemyStruct[MAX_EGGS];
	public int nVelShift;
	public EnemyStruct QueenHead = new EnemyStruct();
	public int nHeadVel;
	public QueenStruct QueenList = new QueenStruct();
	public int QueenChan;
	public short[] tailspr = new short[7];
	public int nQHead;
	public int[] MoveQX = new int[25];
	public int[] MoveQY = new int[25];
	public int[] MoveQZ = new int[25];
	public short[] MoveQA = new short[25];
	public short[] MoveQS = new short[25];
	
	public RaStruct[] Ra = new RaStruct[8];
	
	public int nRatCount;
	public int nMinChunk;
	public int nMaxChunk, nPlayerPic;
	public EnemyStruct[] RatList = new EnemyStruct[MAX_RAT];
	
	public int RexCount;
	public EnemyStruct[] RexList = new EnemyStruct[MAX_REX];
	public short[] RexChan = new short[MAX_REX];

	public int RoachCount;
	public EnemyStruct[] RoachList = new EnemyStruct[MAXROACH];
	
	public int ScorpCount;
	public ScorpStruct[] ScorpList = new ScorpStruct[MAXSCORP];
	public short[] ScorpChan = new short[MAXSCORP];
	
	public int SetCount;
	public SetStruct[] SetList = new SetStruct[MAX_SET];
	public short[] SetChan = new short[MAX_SET];
	
	public int SpiderCount;
	public EnemyStruct SpiderList[] = new EnemyStruct[MAX_SPIDERS];
	
	public int nWaspCount;
	public int nVelShift_X_1;
	public WaspStruct[] WaspList = new WaspStruct[MAX_WASPS];
	
	public short nEnergyChan;
	public short nEnergyBlocks;
	public short nEnergyTowers;
	public short nSwitchSound;
	public short nStoneSound;
	public short nElevSound;
	public short nStopSound;
	public int lCountDown;
	public short nRedTicks;
	public short nAlarmTicks;
	public short nClockVal;
	public short nButtonColor;
	
	public int nFlickerCount;
	public int nGlowCount;
	public int nFlowCount;
	public int nFlashes;
	public int nFirstFlash = -1;
	public int nLastFlash = -1;
	public short nFreeFlash[] = new short[2000];
	public short nNextFlash[] = new short[2000];

	public FlashStruct[] sFlash = new FlashStruct[2000];
	public GrowStruct[] sGlow = new GrowStruct[50];
	public FlickerStruct[] sFlicker = new FlickerStruct[100];
	public FlowStruct[] sFlowInfo = new FlowStruct[375];
	
	public byte[] sMoveDir = new byte[50];
	public MoveSectStruct[] sMoveSect = new MoveSectStruct[50];
	public int nMoveSects;

	public int nPushBlocks;
	public BlockInfo[] sBlockInfo = new BlockInfo[100];
	
	public int PointCount;
	public short[] PointFree = new short[1024];

	public int SlideCount;
	public short[] SlideFree = new short[128];

	public PointStruct PointList[] = new PointStruct[1024];
	public SlideStruct SlideData[] = new SlideStruct[128];
	public SlideStruct2 SlideData2[] = new SlideStruct2[128];
	
	public int SwitchCount;
	public SwitchStruct[] SwitchData = new SwitchStruct[MAXSWITCH];
	
	public int LinkCount;
	public byte[][] LinkMap = new byte[1024][8];
	
	public short[] SectFlag = new short[MAXSECTORS];
	public short[] SectDepth = new short[MAXSECTORS];
	public short[] SectSpeed = new short[MAXSECTORS];
	public short[] SectDamage = new short[MAXSECTORS];
	public short[] SectAbove = new short[MAXSECTORS];
	public short[] SectBelow = new short[MAXSECTORS];
	public short[] SectSound = new short[MAXSECTORS];
	public short[] SectSoundSect = new short[MAXSECTORS];
	
	public int ChannelList;
	public int ChannelLast;
	public Channel[] channel = new Channel[4096];

	public int NewRun;
	public int RunCount = -1;
	public int RunChain;
	public short[] RunFree = new short[MAXRUN];
	public RunData[] RunData = new RunData[MAXRUN];
	public int bTorch;
	
	public EpisodeInfo addon;
	public boolean packedAddon;
	
	public SafeLoader()
	{
		for(int i = 0; i < 8; i++)
			PlayerList[i] = new PlayerStruct();

		headspritesect = new short[MAXSECTORS + 1];
		headspritestat = new short[MAXSTATUS + 1];
		prevspritesect = new short[MAXSPRITES];
		prevspritestat = new short[MAXSPRITES];
		nextspritesect = new short[MAXSPRITES];
		nextspritestat = new short[MAXSPRITES];

		for (int i = 0; i < MAXSPRITES; i++)
			sprite[i] = new SPRITE();
		for (int i = 0; i < MAXSECTORS; i++)
			sector[i] = new SECTOR();
		for (int i = 0; i < MAXWALLS; i++)
			wall[i] = new WALL();
	}
	
	public boolean loadOrig(Resource bb, int nSlot)
	{
		try {
			bb.seek(nSlot * 25, Whence.Set);
			savename = bb.readString(25).trim();
			if(savename.isEmpty())
				return false;
			
			bb.seek(125 + nSlot * 75, Whence.Set);

			level = bb.readByte() & 0xFF;
			best = level;
			nPlayerWeapons[nLocalPlayer] = bb.readShort(); //DemoWeapons;
		    PlayerList[nLocalPlayer].currentWeapon = bb.readShort(); //DemoCurrentWeapon;
		    nPlayerClip[nLocalPlayer] = bb.readShort(); //DemoClip;
		    nPlayerItem[nLocalPlayer]  = bb.readShort(); //DemoItems;
		    PlayerList[nLocalPlayer].setBytes(bb, false);
		    nPlayerLives[nLocalPlayer] = bb.readShort(); //DemoLives;

			return true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean load(Resource bb)
	{
		try {
			bb.seek(SAVEHEADER - SAVENAME - SAVELEVELINFO, Whence.Set);

			addon = null;
			message = null;
			savename = bb.readString(32).trim();
			
			gClassicMode = bb.readBoolean();
			level = bb.readShort();
			best = bb.readShort();
			LoadGDXBlock(bb);
			
			if(gClassicMode) {
				nPlayerWeapons[nLocalPlayer] = bb.readShort();
				PlayerList[nLocalPlayer].currentWeapon = bb.readShort();
				nPlayerClip[nLocalPlayer] = bb.readShort();
				nPlayerItem[nLocalPlayer] = bb.readShort();
				PlayerList[nLocalPlayer].setBytes(bb, false);
				nPlayerLives[nLocalPlayer] = bb.readShort();
			} else {
				lastlevel = bb.readInt();
				
				MapLoad(bb);
				loadAnm(this, bb);
				loadSprites(bb);
				loadSectors(bb);
				loadWallFaces(this, bb);
				loadRunList(this, bb);
				loadPlayer(bb);
				loadRandom(this, bb);
				
				totalmoves = bb.readInt();
				moveframes = bb.readInt();

				if(bb.position() != bb.size())
					return false;
			}
			
			if (warp_on == 2) { // try to find addon
				if(packedAddon)
					addon = getEpisode(BuildGdx.compat.checkFile(boardfilename));
				else addon = getEpisode(BuildGdx.compat.checkDirectory(boardfilename));
				if (addon == null) {
					message = "Can't find user episode file: " + boardfilename;
					warp_on = 1;

					lastlevel = 0;
					level = 0;
					best = 0;
				}
			}
			
			return true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public EpisodeInfo LoadGDXHeader(Resource bb) {
		gClassicMode = false;
		level = -1;
		best = -1;
		warp_on = 0;
		packedAddon = false;
		addon = null;
		boardfilename = null;
		
		try {
			bb.seek(SAVEHEADER - SAVENAME - SAVELEVELINFO, Whence.Set);

			savename = bb.readString(32).trim();
			gClassicMode = bb.readBoolean();
			level = bb.readShort();
			best = bb.readShort();
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
	
	public void LoadGDXBlock(Resource bb)
	{
		int pos = bb.position();
		bb.seek(pos + SAVESCREENSHOTSIZE + 1, Whence.Set);
		
		//reserve SAVEGDXDATA bytes for extra data
		
		warp_on = bb.readByte();
		if(warp_on == 2)
			packedAddon = bb.readBoolean();
		boardfilename = null;
		String name = bb.readString(144).trim();
		if(!name.isEmpty()) boardfilename = name;

		bb.seek(pos + SAVEGDXDATA, Whence.Set);
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
	
	
	private void loadPlayer(Resource bb)
	{
		PlayerList[nLocalPlayer].setBytes(bb, true);
		connecthead = bb.readShort();
        for(int i = 0; i < 8; i++) 
        	connectpoint2[i] = bb.readShort();

        bTorch = bb.readByte();
        nFreeze = bb.readInt();
        nXDamage = bb.readInt();
        nYDamage = bb.readInt();
        nDoppleSprite = bb.readShort();
        nPlayerClip[nLocalPlayer] = bb.readShort();
        nPistolClip[nLocalPlayer] = bb.readShort();
        nPlayerTorch = bb.readShort();
        nPlayerWeapons[nLocalPlayer] = bb.readShort();
        nPlayerLives[nLocalPlayer] = bb.readShort();
        nPlayerItem[nLocalPlayer] = bb.readShort();
        nPlayerInvisible = bb.readShort();
        nPlayerDouble = bb.readShort();
        nPlayerViewSect = bb.readShort();
        nPlayerFloorSprite = bb.readShort();
        nPlayerScore = bb.readShort();
        nPlayerGrenade = bb.readShort();
        nPlayerSnake = bb.readShort();
        nDestVertPan = bb.readShort();
        dVertPan = bb.readShort();
        nDeathType = bb.readShort();
        nQuake = bb.readShort();
        bTouchFloor = bb.readBoolean();

		initx = bb.readInt();
		inity = bb.readInt();
		initz = bb.readInt();
		inita = bb.readShort();
		initsect = bb.readShort();

		bb.read(show2dsector);
		bb.read(show2dwall);
		bb.read(show2dsprite);
	}
	
	private void loadSprites(Resource bb)
	{
		loadEnemies(bb);
		
		loadBullets(this, bb);
		loadGrenades(this, bb);
		loadBubbles(this, bb);
		loadSnakes(this, bb);
		loadObjects(this, bb);
		loadTraps(this, bb);
		loadDrips(this, bb);

		nCreaturesLeft = bb.readShort();
		nCreaturesMax = bb.readShort();
		nSpiritSprite = bb.readShort();
		nMagicCount = bb.readShort();
		nRegenerates = bb.readShort();
		nFirstRegenerate = bb.readShort();
		nNetStartSprites = bb.readShort();
		nCurStartSprite = bb.readShort();
		nNetPlayerCount = bb.readShort();

		for(int i = 0; i < 8; i++)
			nNetStartSprite[i] = bb.readShort(); 
		for(int i = 0; i < nChunkSprite.length; i++)
			nChunkSprite[i] = bb.readShort(); 
		for(int i = 0; i < nBodyGunSprite.length; i++)
			nBodyGunSprite[i] = bb.readShort(); 
		for(int i = 0; i < nBodySprite.length; i++)
			nBodySprite[i] = bb.readShort(); 
		
		nCurChunkNum = bb.readShort();
		nCurBodyNum = bb.readShort();
		nCurBodyGunNum = bb.readShort();
		nBodyTotal = bb.readShort();
		nChunkTotal = bb.readShort();
	
		nRadialSpr = bb.readShort();
		nRadialDamage = bb.readShort();
		nDamageRadius = bb.readShort();
		nRadialOwner = bb.readShort();
		nRadialBullet = bb.readShort();
		
		nDronePitch = bb.readShort();
		nFinaleSpr = bb.readShort();
		nFinaleStage = bb.readShort();
		lFinaleStart = bb.readShort();
		nSmokeSparks = bb.readShort();
	}

	private void loadSectors(Resource bb)
	{
		loadLights(this, bb);
		loadElevs(this, bb);
		loadBobs(this, bb);
		loadMoves(this, bb);
		loadTrails(this, bb);
		loadPushBlocks(this, bb);
		loadSlide(this, bb);
		loadSwitches(this, bb);
		loadLinks(this, bb);
		loadSecExtra(this, bb);

		nEnergyChan = bb.readShort();
		nEnergyBlocks = bb.readShort();
		nEnergyTowers = bb.readShort();
		
		nSwitchSound = bb.readShort();
		nStoneSound = bb.readShort();
		nElevSound = bb.readShort();
		nStopSound = bb.readShort();
		
		lCountDown = bb.readInt();
		nAlarmTicks = bb.readShort();
		nRedTicks = bb.readShort();
		nClockVal = bb.readShort();
		nButtonColor = bb.readShort();
	}
	
	private void loadEnemies(Resource bb)
	{
		loadAnubis(this, bb);
		loadFish(this, bb);
		loadLava(this, bb);
		loadLion(this, bb);
		loadMummy(this, bb);
		loadQueen(this, bb);
		loadRa(this, bb);
		loadRat(this, bb);
		loadRex(this, bb);
		loadRoach(this, bb);
		loadScorp(this, bb);
		loadSet(this, bb);
		loadSpider(this, bb);
		loadWasp(this, bb);
	}
	
	public String getMessage() {
		return message;
	}
}
