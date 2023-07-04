package ru.m210projects.Wang.Type;

import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.MAXPSKYTILES;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.MAXWALLS;
import static ru.m210projects.Build.Strhandler.toLowerCase;
import static ru.m210projects.Wang.Gameutils.MAXANIM;
import static ru.m210projects.Wang.Gameutils.MAX_SECTOR_OBJECTS;
import static ru.m210projects.Wang.Gameutils.MAX_SW_PLAYERS;
import static ru.m210projects.Wang.JSector.MAXMIRRORS;
import static ru.m210projects.Wang.LoadSave.SAVEGDXDATA;
import static ru.m210projects.Wang.LoadSave.SAVEHEADER;
import static ru.m210projects.Wang.LoadSave.SAVELEVELINFO;
import static ru.m210projects.Wang.LoadSave.SAVESCREENSHOTSIZE;
import static ru.m210projects.Wang.Sector.MAX_SINE_WALL;
import static ru.m210projects.Wang.Sector.MAX_SINE_WALL_POINTS;
import static ru.m210projects.Wang.Sector.MAX_SINE_WAVE;
import static ru.m210projects.Wang.Track.MAX_TRACKS;
import static ru.m210projects.Wang.Type.ResourceHandler.GetEpisode;
import static ru.m210projects.Wang.Weapon.MAX_FLOORBLOOD_QUEUE;
import static ru.m210projects.Wang.Weapon.MAX_GENERIC_QUEUE;
import static ru.m210projects.Wang.Weapon.MAX_HOLE_QUEUE;
import static ru.m210projects.Wang.Weapon.MAX_LOWANGS_QUEUE;
import static ru.m210projects.Wang.Weapon.MAX_STAR_QUEUE;
import static ru.m210projects.Wang.Weapon.MAX_WALLBLOOD_QUEUE;

import java.util.Arrays;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Wang.Factory.WangNetwork.MultiGameTypes;

public class SafeLoader {

	private String message;
	public GameInfo addon;
	private String addonFileName;
	public byte warp_on;
	private boolean isPackage;
	
	
	public int Level, Skill;
	public String boardfilename;
	public short numsectors, numwalls;
	public short[] headspritesect, headspritestat;
	public short[] prevspritesect, prevspritestat;
	public short[] nextspritesect, nextspritestat;
	public SECTOR[] sector = new SECTOR[MAXSECTORS];
	public WALL[] wall = new WALL[MAXWALLS];
	public SPRITE[] sprite = new SPRITE[MAXSPRITES];
	public int playTrack;
	public int FinishTimer;
	public boolean FinishedLevel;
	public int FinishAnim;
	
	public PlayerStr Player[] = new PlayerStr[MAX_SW_PLAYERS];
	public short connecthead, connectpoint2[] = new short[MAXPLAYERS];
	public short numplayers;
	public int CommPlayers;
	public short myconnectindex;
	
	public Sect_User[] SectUser = new Sect_User[MAXSECTORS];
	public USER[] pUser = new USER[MAXSPRITES];
	public Sector_Object[] SectorObject = new Sector_Object[MAX_SECTOR_OBJECTS];
	
	public SINE_WAVE_FLOOR SineWaveFloor[][] = new SINE_WAVE_FLOOR[MAX_SINE_WAVE][21];
	public SINE_WALL SineWall[][] = new SINE_WALL[MAX_SINE_WALL][MAX_SINE_WALL_POINTS];
	public Spring_Board SpringBoard[] = new Spring_Board[20];
	public int x_min_bound, y_min_bound, x_max_bound, y_max_bound;
	public TRACK Track[] = new TRACK[MAX_TRACKS];
	
	public int screenpeek;
	public int totalsynctics;
	
	public Anim pAnim[] = new Anim[MAXANIM];
	public int AnimCnt;
	
	public short pskyoff[] = new short[MAXPSKYTILES], pskybits;
	public int parallaxyscale;
	public int randomseed;
	public int totalclock, visibility;
	public short NormalVisibility;
	public int parallaxyoffs;
	public byte parallaxtype;
	public boolean MoveSkip2;
	public int MoveSkip4, MoveSkip8;

	public MirrorType[] mirror = new MirrorType[MAXMIRRORS];
	public boolean mirrorinview;
	public int mirrorcnt;
	
	public short StarQueueHead = 0;
	public short StarQueue[] = new short[MAX_STAR_QUEUE];
	public short HoleQueueHead = 0;
	public short HoleQueue[] = new short[MAX_HOLE_QUEUE];
	public short WallBloodQueueHead = 0;
	public short WallBloodQueue[] = new short[MAX_WALLBLOOD_QUEUE];
	public short FloorBloodQueueHead = 0;
	public short FloorBloodQueue[] = new short[MAX_FLOORBLOOD_QUEUE];
	public short GenericQueueHead = 0;
	public short GenericQueue[] = new short[MAX_GENERIC_QUEUE];
	public short LoWangsQueueHead = 0;
	public short LoWangsQueue[] = new short[MAX_LOWANGS_QUEUE];
	
	public int PlayClock;
	public int Kills, TotalKillable;
	public short LevelSecrets;
	public int[] picanm = new int[MAXTILES];
	public byte[] show2dsector = new byte[(MAXSECTORS + 7) >> 3];
	public byte[] show2dwall = new byte[(MAXWALLS + 7) >> 3];
	public byte[] show2dsprite = new byte[(MAXSPRITES + 7) >> 3];
	
	public int Bunny_Count;
	public boolean GodMode;
	public boolean serpwasseen, sumowasseen, zillawasseen;
	public short BossSpriteNum[] = new short[3];
	
	//Network settings
	public int KillLimit;
	public int TimeLimit;
	public int TimeLimitClock;
	public MultiGameTypes MultiGameType = MultiGameTypes.MULTI_GAME_NONE; // used to be a stand alone global
	public boolean TeamPlay;
	public boolean HurtTeammate;
	public boolean SpawnMarkers;
	public boolean NoRespawn;
	public boolean Nuke = true;
	
	public SafeLoader() {
		
		headspritesect = new short[MAXSECTORS + 1];
		headspritestat = new short[MAXSTATUS + 1];
		prevspritesect = new short[MAXSPRITES];
		prevspritestat = new short[MAXSPRITES];
		nextspritesect = new short[MAXSPRITES];
		nextspritestat = new short[MAXSPRITES];

		for (int i = 0; i < MAXANIM; i++)
			pAnim[i] = new Anim();
		for (int i = 0; i < MAXSPRITES; i++)
			sprite[i] = new SPRITE();
		for (int i = 0; i < MAXSECTORS; i++)
			sector[i] = new SECTOR();
		for (int i = 0; i < MAXWALLS; i++)
			wall[i] = new WALL();
		
		for (int i = 0; i < MAX_SW_PLAYERS; i++)
			Player[i] = new PlayerStr();
		
		for (int i = 0; i < MAXSECTORS; i++)
			SectUser[i] = new Sect_User();
		for (int i = 0; i < MAXSPRITES; i++)
			pUser[i] = new USER();
		for (int i = 0; i < MAX_SECTOR_OBJECTS; i++)
			SectorObject[i] = new Sector_Object();
		
		for (int i = 0; i < 20; i++)
			SpringBoard[i] = new Spring_Board();
		
		for (int i = 0; i < MAX_SINE_WALL; i++)
			for (int j = 0; j < MAX_SINE_WALL_POINTS; j++)
				SineWall[i][j] = new SINE_WALL();

		for (int i = 0; i < MAX_SINE_WAVE; i++)
			for (int j = 0; j < 21; j++)
				SineWaveFloor[i][j] = new SINE_WAVE_FLOOR();
	}
	
	public boolean load(Resource bb) {
		message = null;
		warp_on = 0;
		isPackage = false;
		addon = null;
		addonFileName = null;
		
		try {
			bb.seek(SAVEHEADER - SAVELEVELINFO, Whence.Set);
			
			Level = bb.readInt();
			Skill = bb.readInt();

			bb.seek(SAVEHEADER + SAVESCREENSHOTSIZE, Whence.Set);
			
			// Clear Sprite Extension structure
			for (int s = 0; s < MAXSECTORS; s++) 
				if (SectUser[s] != null)
					SectUser[s].reset();
			Arrays.fill(pUser, null);

			LoadGDXBlock(bb);
			LoadMap(bb);
			LoadPanelSprites(bb);
			LoadPlayers(bb);
			LoadSectorUserInfos(bb);
			LoadUserInfos(bb);
			LoadSectorObjects(bb);
			LoadSineSect(bb);

			x_min_bound = bb.readInt();
			y_min_bound = bb.readInt();
			x_max_bound = bb.readInt();
			y_max_bound = bb.readInt();
			
			LoadTracks(bb);
			
			screenpeek = bb.readInt();
			totalsynctics = bb.readInt();
			
			LoadAnims(bb);
			
			totalclock = bb.readInt();
			randomseed = bb.readInt();
			NormalVisibility = bb.readShort();
			visibility = bb.readInt();
			parallaxtype = bb.readByte();
			parallaxyoffs = bb.readInt();
			for (int i = 0; i < MAXPSKYTILES; i++)
				pskyoff[i] = bb.readShort();
			pskybits = bb.readShort();
			MoveSkip2 = bb.readBoolean();
			MoveSkip4 = bb.readInt();
			MoveSkip8 = bb.readInt();
		    
			LoadMirrors(bb);
			LoadQueues(bb);
			LoadStuff(bb);
			
			if (warp_on == 1) { // try to find addon
				addon = GetEpisode(addonFileName, isPackage);
				if (addon == null) {
					message = "Can't find user episode file: " + addonFileName;
					warp_on = 2;

					Level = 0;
				}
			}

			if (bb.position() == bb.size())
				return true;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String getMessage() {
		return message;
	}
	
	public GameInfo LoadGDXHeader(Resource fil) {
		Level = -1;
		Skill = -1;
		warp_on = 0;
		isPackage = false;
		addon = null;
		addonFileName = null;
		
		try {
			fil.seek(SAVEHEADER - SAVELEVELINFO, Whence.Set);
			
			Level = fil.readInt();
			Skill = fil.readInt();
			
			fil.seek(SAVEHEADER + SAVESCREENSHOTSIZE, Whence.Set);
			
			LoadGDXBlock(fil);
			if (warp_on == 1) // try to find addon
				addon = GetEpisode(addonFileName, isPackage);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return addon;
	}
	
	private void LoadGDXBlock(Resource bb) {
		int pos = bb.position();
		// reserve SAVEGDXDATA bytes for extra data
		
		warp_on = bb.readByte();
		if (warp_on == 1) {
			isPackage = bb.readBoolean();
			addonFileName = toLowerCase(bb.readString(144).trim());
		}

		bb.seek(pos + SAVEGDXDATA, Whence.Set);
	}
	
	private void LoadPlayers(Resource fil) {
		numplayers = fil.readShort();
		CommPlayers = fil.readShort();
		myconnectindex = fil.readShort();
		connecthead = fil.readShort();
		for (int i = 0; i < MAXPLAYERS; i++)
			connectpoint2[i] = fil.readShort();
		for (int i = 0; i < numplayers; i++)
			Player[i].load(fil);
	}

	private void LoadPanelSprites(Resource bb)
	{
		int ndx;
		for (int i = 0; i < 1; i++)
		{
			PlayerStr pp = Player[i];
			
			List.Init(pp.PanelSpriteList);
			
			while (true)
            {
				ndx = bb.readInt();
				if (ndx == -1)
	                break;
				
				Panel_Sprite psp = new Panel_Sprite();
				psp.siblingNdx = bb.readInt();
				psp.load(bb);
				
				List.InsertTrail(pp.PanelSpriteList, psp);
            }
			
			Panel_Sprite next;
			for (Panel_Sprite psp = pp.PanelSpriteList.Next; psp != pp.PanelSpriteList; psp = next) {
				next = psp.Next;
				// sibling is the only PanelSprite (malloced ptr) in the PanelSprite struct
	            psp.sibling = List.PanelNdxToSprite(pp.PanelSpriteList, psp.siblingNdx);
			} 
		}
	}
	
	private void LoadMap(Resource bb) { 
		String name = bb.readString(144).trim();
		if (!name.isEmpty())
			boardfilename = name;

		numwalls = bb.readShort();
		for (int w = 0; w < numwalls; w++) {
			if(wall[w] == null) 
				wall[w] = new WALL();
			wall[w].buildWall(bb);
		}

		numsectors = bb.readShort();
		for (int s = 0; s < numsectors; s++) {
			if(sector[s] == null) 
				sector[s] = new SECTOR();
			sector[s].buildSector(bb);
		}

		for (int i = 0; i < MAXSPRITES; i++) {
			if(sprite[i] == null) 
				sprite[i] = new SPRITE();
			sprite[i].buildSprite(bb);
		}

		for (int i = 0; i <= MAXSECTORS; i++)
			headspritesect[i] = bb.readShort();
		for (int i = 0; i <= MAXSTATUS; i++)
			headspritestat[i] = bb.readShort();
		
		for (int i = 0; i < MAXSPRITES; i++) 
			prevspritesect[i] = bb.readShort();
		for (int i = 0; i < MAXSPRITES; i++) 
			prevspritestat[i] = bb.readShort();
		for (int i = 0; i < MAXSPRITES; i++) 
			nextspritesect[i] = bb.readShort();
		for (int i = 0; i < MAXSPRITES; i++) 
			nextspritestat[i] = bb.readShort();
		
		FinishTimer = bb.readInt();
		FinishedLevel = bb.readBoolean();
		FinishAnim = bb.readInt();
		playTrack = bb.readInt();
	}
	
	private void LoadSectorUserInfos(Resource fil) {
		// Sector User information
		for (int i = 0; i < numsectors; i++) {
			int sectnum = fil.readInt();
			if(sectnum != -1) {
				if(SectUser[sectnum] != null)
					SectUser[sectnum] = new Sect_User();
				SectUser[sectnum].load(fil);
			}
		}
	}
	
	private void LoadUserInfos(Resource fil)
	{
		for (int i = 0; i < MAXSPRITES; i++)
        {
			int snum = fil.readInt();
			if(snum != -1) {
				pUser[snum] = new USER();
				pUser[snum].load(fil);
			}
        }
	}
	
	private void LoadSectorObjects(Resource fil) {
		// Sector User information
		for (int i = 0; i < MAX_SECTOR_OBJECTS; i++) 
			SectorObject[i].load(fil);
	}

	private void LoadSineSect(Resource fil)
	{
		for(int i = 0; i < MAX_SINE_WAVE; i++)
			for(int j = 0; j < 21; j++)
				SineWaveFloor[i][j].load(fil);
		for(int i = 0; i < MAX_SINE_WALL; i++)
			for(int j = 0; j < MAX_SINE_WALL_POINTS; j++)
				SineWall[i][j].load(fil);
		for(int i = 0; i < 20; i++)
			SpringBoard[i].load(fil);
	}
	
	private void LoadTracks(Resource fil)
	{
		for(int i = 0; i < MAX_TRACKS; i++)
		{
			if (Track[i] == null)
				Track[i] = new TRACK();
			else Track[i].reset();
			TRACK tr = Track[i];
			
			tr.ttflags = fil.readInt();
			tr.flags = fil.readShort();
			tr.NumPoints = fil.readShort();

			if(tr.NumPoints == 0) {
				tr.TrackPoint = new TRACK_POINT[1];
			} else {
				tr.TrackPoint = new TRACK_POINT[tr.NumPoints];
				for(int j = 0; j < tr.NumPoints; j++)
				{
					TRACK_POINT tp = tr.TrackPoint[j] = new TRACK_POINT();
					
					tp.x = fil.readInt();
					tp.y = fil.readInt();
					tp.z = fil.readInt();
					
					tp.ang = fil.readShort();
					tp.tag_low = fil.readShort();
					tp.tag_high = fil.readShort();
				}
			}
		}
	}
	
	private void LoadAnims(Resource fil)
	{
		AnimCnt = fil.readInt();
		for(int i = 0; i < MAXANIM; i++)
			pAnim[i].load(fil);
	}
	
	private void LoadMirrors(Resource fil)
	{
		mirrorcnt = fil.readInt();
		mirrorinview = fil.readBoolean();
		for (int i = 0; i < MAXMIRRORS; i++) {
			if (mirror[i] == null)
				mirror[i] = new MirrorType();
			mirror[i].load(fil);
		}
	}
	
	private void LoadQueues(Resource res)
	{
		StarQueueHead = res.readShort();
		for(int i = 0; i < MAX_STAR_QUEUE; i++)
			StarQueue[i] = res.readShort();
	    HoleQueueHead = res.readShort();
	    for(int i = 0; i < MAX_HOLE_QUEUE; i++)
	    	HoleQueue[i] = res.readShort();
	    WallBloodQueueHead = res.readShort();
	    for(int i = 0; i < MAX_WALLBLOOD_QUEUE; i++)
	    	WallBloodQueue[i] = res.readShort();
	    FloorBloodQueueHead = res.readShort();
	    for(int i = 0; i < MAX_FLOORBLOOD_QUEUE; i++)
	    	FloorBloodQueue[i] = res.readShort();
	    GenericQueueHead = res.readShort();
	    for(int i = 0; i < MAX_GENERIC_QUEUE; i++)
	    	GenericQueue[i] = res.readShort();
	    LoWangsQueueHead = res.readShort();
	    for(int i = 0; i < MAX_LOWANGS_QUEUE; i++)
	    	LoWangsQueue[i] = res.readShort();
	}
	
	private void LoadStuff(Resource fil)
	{
		PlayClock = fil.readInt();
		Kills = fil.readInt();
		TotalKillable = fil.readInt();
		
		KillLimit = fil.readInt();
		TimeLimit = fil.readInt();
		TimeLimitClock = fil.readInt();

		int i = fil.readByte();
		MultiGameType = i != -1 ? MultiGameTypes.values()[i] : null;

		TeamPlay = fil.readBoolean();
		HurtTeammate = fil.readBoolean();
		SpawnMarkers = fil.readBoolean();
		NoRespawn = fil.readBoolean();
		Nuke = fil.readBoolean();
		
		for(i = 0; i < MAXTILES; i++)
			picanm[i] = fil.readInt();
		LevelSecrets = fil.readShort();

		fil.read(show2dwall);
		fil.read(show2dsprite);
		fil.read(show2dsector);
		
		Bunny_Count = fil.readInt();
		GodMode = fil.readBoolean();
		serpwasseen = fil.readBoolean();
		sumowasseen = fil.readBoolean();
		zillawasseen = fil.readBoolean();
	    
		for(i = 0; i < 3; i++)
			BossSpriteNum[i] = fil.readShort();
	}
}
