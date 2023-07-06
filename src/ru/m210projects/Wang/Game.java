package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.MAXWALLS;
import static ru.m210projects.Build.Engine.USERTILES;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.parallaxtype;
import static ru.m210projects.Build.Engine.parallaxyoffs;
import static ru.m210projects.Build.Engine.parallaxyscale;
import static ru.m210projects.Build.Engine.pskybits;
import static ru.m210projects.Build.Engine.pskyoff;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.show2dsector;
import static ru.m210projects.Build.Engine.show2dsprite;
import static ru.m210projects.Build.Engine.show2dwall;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.visibility;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_GOLD;
import static ru.m210projects.Wang.Border.BorderSetView;
import static ru.m210projects.Wang.Break.SortBreakInfo;
import static ru.m210projects.Wang.Draw.dimensionmode;
import static ru.m210projects.Wang.Draw.zoom;
import static ru.m210projects.Wang.Enemies.Bunny.Bunny_Count;
import static ru.m210projects.Wang.Enemies.Ninja.InitAllPlayerSprites;
import static ru.m210projects.Wang.Enemies.Ninja.PlayerPanelSetup;
import static ru.m210projects.Wang.Enemies.Sumo.BossSpriteNum;
import static ru.m210projects.Wang.Enemies.Sumo.serpwasseen;
import static ru.m210projects.Wang.Enemies.Sumo.sumowasseen;
import static ru.m210projects.Wang.Enemies.Sumo.triedplay;
import static ru.m210projects.Wang.Enemies.Sumo.zillawasseen;
import static ru.m210projects.Wang.Gameutils.MAX_LEVELS;
import static ru.m210projects.Wang.Gameutils.MAX_SW_PLAYERS;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.PF_AUTO_AIM;
import static ru.m210projects.Wang.Gameutils.PF_LOCK_RUN;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.JSector.JS_InitLockouts;
import static ru.m210projects.Wang.JSector.JS_InitMirrors;
import static ru.m210projects.Wang.JSector.JS_ToggleLockouts;
import static ru.m210projects.Wang.JSector.JS_UnInitLockouts;
import static ru.m210projects.Wang.Light.InitLighting;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.gDemoScreen;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Main.kGameCrash;
import static ru.m210projects.Wang.Main.mUserFlag;
import static ru.m210projects.Wang.Names.STAT_PLAYER0;
import static ru.m210projects.Wang.Palette.InitPalette;
import static ru.m210projects.Wang.Panel.pClearSpriteList;
import static ru.m210projects.Wang.Player.InitAllPlayers;
import static ru.m210projects.Wang.Player.InitMultiPlayerInfo;
import static ru.m210projects.Wang.Player.NormalVisibility;
import static ru.m210projects.Wang.Rooms.SetupMirrorTiles;
import static ru.m210projects.Wang.Sector.AnimCnt;
import static ru.m210projects.Wang.Sector.InitAnim;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Sector.SectorSetup;
import static ru.m210projects.Wang.Sector.initlava;
import static ru.m210projects.Wang.Sound.CDAudio_Stop;
import static ru.m210projects.Wang.Sound.MusicStartup;
import static ru.m210projects.Wang.Sound.SoundStartup;
import static ru.m210projects.Wang.Sound.StopFX;
import static ru.m210projects.Wang.Sound.Terminate3DSounds;
import static ru.m210projects.Wang.Sprites.KillSprite;
import static ru.m210projects.Wang.Sprites.MoveSkip2;
import static ru.m210projects.Wang.Sprites.MoveSkip4;
import static ru.m210projects.Wang.Sprites.MoveSkip8;
import static ru.m210projects.Wang.Sprites.PreMapCombineFloors;
import static ru.m210projects.Wang.Sprites.SpriteSetup;
import static ru.m210projects.Wang.Sprites.SpriteSetupPost;
import static ru.m210projects.Wang.Sprites.wait_active_check_offset;
import static ru.m210projects.Wang.Track.PlaceActorsOnTracks;
import static ru.m210projects.Wang.Track.PlaceSectorObjectsOnTracks;
import static ru.m210projects.Wang.Track.PostSetupSectorObject;
import static ru.m210projects.Wang.Track.Track;
import static ru.m210projects.Wang.Track.TrackSetup;
import static ru.m210projects.Wang.Type.MyTypes.DIV2;
import static ru.m210projects.Wang.Type.RTS.RTS_Init;
import static ru.m210projects.Wang.Weapon.QueueReset;
import static ru.m210projects.Wang.Weapon.left_foot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.OnSceenDisplay.OSDCOMMAND;
import ru.m210projects.Build.OnSceenDisplay.OSDCVARFUNC;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Types.BuildPos;
import ru.m210projects.Build.Types.InvalidVersionException;
import ru.m210projects.Wang.Main.UserFlag;
import ru.m210projects.Wang.Menus.Network.NetInfo;
import ru.m210projects.Wang.Type.DemoFile;
import ru.m210projects.Wang.Type.EpisodeInfo;
import ru.m210projects.Wang.Type.GameInfo;
import ru.m210projects.Wang.Type.LONGp;
import ru.m210projects.Wang.Type.LastLevelUser;
import ru.m210projects.Wang.Type.LevelInfo;
import ru.m210projects.Wang.Type.List;
import ru.m210projects.Wang.Type.PlayerStr;

public class Game {

	public static int nInterpolation = 2;
	
	public static final int kMaxTiles = MAXTILES - USERTILES;
	public static final int kUserTiles = kMaxTiles;

	public static boolean SW_SHAREWARE = false;
	public static final int BACKBUTTON = 9216;
	public static final int MOUSECURSOR = 9217;
	public static final int ALTHUDLEFT = 9218;
	public static final int ALTHUDRIGHT = 9219;

	public static final int HUDARMOR = 9220;
	public static final int HUDKEYS = 9221;

	public static String boardfilename;
	public static final int ANIM_INTRO = 0;
	public static final int ANIM_SERP = 1;
	public static final int ANIM_SUMO = 2;
	public static final int ANIM_ZILLA = 3;

	public static LastLevelUser puser[] = new LastLevelUser[MAX_SW_PLAYERS];

	public static short PlaxBits = 0;
	public static PlayerStr Player[] = new PlayerStr[MAX_SW_PLAYERS];
	public static int screenpeek;
	public static boolean Global_PLock = false;

	public static int PlayingLevel, Level, Skill;
	public static boolean QuitFlag, ExitLevel, FinishedLevel;
	public static int FinishAnim = 0;

	public static boolean GodMode = false;
	public static boolean ConPanel = false;

	public static boolean DebugActorFreeze = false;
	public static boolean DebugActor = false;
	public static boolean DebugAnim = false;
	public static boolean DebugPanel = false;
	public static boolean DebugSector = false;
	public static boolean ResCheat = false;
	public static boolean PlayerTrackingMode = false;
	public static NetInfo pNetInfo = new NetInfo();

	public static boolean MessageInputMode = false;

	public static int PlayClock;

	public static int Kills;
	public static int TotalKillable;

	public static int totalsynctics;
	public static int turn_scale = 256;
	public static int move_scale = 256;

	public static int RAND_MAX = 32767;

	public static short HelpPage = 0;
	public static short HelpPagePic[] = { 5115, 5116, 5117 };

	public static int ChopTics;
	public static int PlaxCeilGlobZadjust, PlaxFloorGlobZadjust;
	
	public static DemoFile rec;
	public static boolean DemoRecording = false;
	public static boolean isDemoRecording = false;
	public static final int RECSYNCBUFSIZ = 2520;
	public static boolean DemoPlaying = false;

	public static final EpisodeInfo pSharewareEp = new EpisodeInfo("Enter the Wang", "Four levels (Shareware Version)",
			new LevelInfo[] { new LevelInfo("$bullet.map", "e1l01.mid", "Seppuku Station", "0 : 55", "5 : 00"),
					new LevelInfo("$dozer.map", "e1l03.mid", "Zilla Construction", "4 : 59", "8 : 00"),
					new LevelInfo("$shrine.map", "e1l02.mid", "Master Leep's Temple", "3 : 16", "10 : 00"),
					new LevelInfo("$woods.map", "e1l04.mid", "Dark Woods of the Serpent", "7 : 06", "16 : 00"), });

	public static final EpisodeInfo pOriginalEp = new EpisodeInfo("Code of Honor",
			"Eighteen levels (Full Version only)",
			new LevelInfo[] { new LevelInfo("$whirl.map", "yokoha03.mid", "Rising Son", "5 : 30", "10 : 00"),
					new LevelInfo("$tank.map", "nippon34.mid", "Killing Fields", "1 : 46", "4 : 00"),
					new LevelInfo("$boat.map", "execut11.mid", "Hara-Kiri Harbor", "1 : 56", "4 : 00"),
					new LevelInfo("$garden.map", "execut11.mid", "Zilla's Villa", "1 : 06", "2 : 00"),
					new LevelInfo("$outpost.map", "sanai.mid", "Monastery", "1 : 23", "3 : 00"),
					new LevelInfo("$hidtemp.map", "kotec2.mid", "Raider of the Lost Wang", "2 : 05", "4 : 10"),
					new LevelInfo("$plax1.map", "kotec2.mid", "Sumo Sky Palace", "6 : 32", "12 : 00"),
					new LevelInfo("$bath.map", "yokoha03.mid", "Bath House", "10 : 00", "10 : 00"),
					new LevelInfo("$airport.map", "nippon34.mid", "Unfriendly Skies", "2 : 59", "6 : 00"),
					new LevelInfo("$refiner.map", "kotoki12.mid", "Crude Oil", "2 : 40", "5 : 00"),
					new LevelInfo("$newmine.map", "hoshia02.mid", "Coolie Mines", "2 : 48", "6 : 00"),
					new LevelInfo("$subbase.map", "hoshia02.mid", "Subpen 7", "2 : 02", "4 : 00"),
					new LevelInfo("$rock.map", "kotoki12.mid", "The Great Escape", "3 : 18", "6 : 00"),
					new LevelInfo("$yamato.map", "sanai.mid", "Floating Fortress", "11 : 38", "20 : 00"),
					new LevelInfo("$seabase.map", "kotec2.mid", "Water Torture", "5 : 07", "10 : 00"),
					new LevelInfo("$volcano.map", "kotec2.mid", "Stone Rain", "9 : 15", "20 : 00"),
					// Secret levels:
					new LevelInfo("$shore.map", "kotec2.mid", "Shanghai Shipwreck", "3 : 58", "8 : 00"),
					new LevelInfo("$auto.map", "kotec2.mid", "Auto Maul", "4 : 07", "8 : 00"), });
	
	public static final EpisodeInfo pWangBangEp = new EpisodeInfo("Wang Bang", "Multiplayer maps",
			new LevelInfo[] {
				new LevelInfo("tank.map", "kotec2.mid", "Heavy Metal (DM only)", "10 : 00", "10 : 00"),
				new LevelInfo("$dmwoods.map", "kotec2.mid", "Ripper Valley (DM only)", "10 : 00", "10 : 00"),
				new LevelInfo("$dmshrin.map", "kotec2.mid", "House of Wang (DM only)", "10 : 00", "10 : 00"),
				new LevelInfo("$rush.map", "kotec2.mid", "Lo Wang Rally (DM only)", "10 : 00", "10 : 00"),
				new LevelInfo("shotgun.map", "kotec2.mid", "Ruins of the Ronin (CTF)", "10 : 00", "10 : 00"),
				new LevelInfo("$dmdrop.map", "kotec2.mid", "Killing Fields (CTF)", "10 : 00", "10 : 00"),
			});
			
	
	public static final EpisodeInfo pWantonEp = new EpisodeInfo("Wanton Destr",
			"Twelve levels (Full Version only)",
			new LevelInfo[] { 
					new LevelInfo("$whirl.map", "yokoha03.mid", "Chinatown", "5 : 30", "10 : 00"),
					new LevelInfo("$tank.map", "nippon34.mid", "Monastery", "1 : 46", "4 : 00"),
					new LevelInfo("$boat.map", "execut11.mid", "Trolley Yard", "1 : 56", "4 : 00"),
					new LevelInfo("$garden.map", "execut11.mid", "Restaurant", "1 : 06", "2 : 00"),
					new LevelInfo("$outpost.map", "sanai.mid", "Skyscraper", "1 : 23", "3 : 00"),
					new LevelInfo("$hidtemp.map", "kotec2.mid", "Airplane", "2 : 05", "4 : 10"),
					new LevelInfo("$plax1.map", "kotec2.mid", "Military Base", "6 : 32", "12 : 00"),
					new LevelInfo("$bath.map", "yokoha03.mid", "Train", "10 : 00", "10 : 00"),
					new LevelInfo("$airport.map", "nippon34.mid", "Auto Factory", "2 : 59", "6 : 00"),
					
					null, //"$refiner.map"
					null, //"$newmine.map"
					null, //"$subbase.map"
					null, //"$rock.map"
					null, //"$yamato.map"
					null, //"$seabase.map"
					
					new LevelInfo("$volcano.map", "kotec2.mid", "Skyline", "9 : 15", "20 : 00"),
					// Secret levels:
					new LevelInfo("$shore.map", "kotec2.mid", "Redwood Forest", "3 : 58", "8 : 00"),
					new LevelInfo("$auto.map", "kotec2.mid", "The Docks", "4 : 07", "8 : 00"), });
	
	public static final EpisodeInfo pTwinDragonEp = new EpisodeInfo("Twin Dragon",
			"Thirteen levels (Full Version only)",
			new LevelInfo[] { 
					new LevelInfo("$whirl.map", "yokoha03.mid", "Wang's Home", "5 : 30", "10 : 00"),
					new LevelInfo("$tank.map", "nippon34.mid", "City of Despair", "1 : 46", "4 : 00"),
					new LevelInfo("$boat.map", "execut11.mid", "Emergency Room", "1 : 56", "4 : 00"),
					new LevelInfo("$garden.map", "execut11.mid", "Hide and Seek", "1 : 06", "2 : 00"),
					new LevelInfo("$outpost.map", "sanai.mid", "Warehouse", "1 : 23", "3 : 00"),
					new LevelInfo("$hidtemp.map", "kotec2.mid", "Military Research Base", "2 : 05", "4 : 10"),
					new LevelInfo("$plax1.map", "kotec2.mid", "Toxic Waste", "6 : 32", "12 : 00"),
					new LevelInfo("$bath.map", "yokoha03.mid", "Crazy Train", "10 : 00", "10 : 00"),
					new LevelInfo("$airport.map", "nippon34.mid", "Fishing Village", "2 : 59", "6 : 00"),
					new LevelInfo("$refiner.map", "kotoki12.mid", "The Garden", "2 : 40", "5 : 00"),
					new LevelInfo("$newmine.map", "hoshia02.mid", "The Fortress", "2 : 48", "6 : 00"),

					null, //"$subbase.map"
					null, //"$rock.map"
					null, //"$yamato.map"
					null, //"$seabase.map"
					
					new LevelInfo("$volcano.map", "kotec2.mid", "The Palace", "9 : 15", "20 : 00"),
					// Secret levels:
					new LevelInfo("$shore.map", "kotec2.mid", "Prison Camp", "3 : 58", "8 : 00"), });
				
	public static GameInfo defGame = new GameInfo("Default", BuildGdx.cache.getGroup(game.mainGrp), pSharewareEp, pOriginalEp, pWangBangEp) {
		@Override
		public LevelInfo getLevel(int num) {
			if (num < 1)
				return null;
			if (num < 5) 
				return episode[0] != null ? episode[0].gMapInfo[num - 1] : null;
			if (num < 23) 
				return episode[1] != null ? episode[1].gMapInfo[num - 5] : null;
			return episode[2] != null ? episode[2].gMapInfo[num - 23] : null;
		}
		
		@Override
		public int getNumEpisode(int level) {
			if (level < 5) 
				return 0;
			if (level < 23) 
				return 1;
			return 2;
		}
		
		@Override
		public int getNumLevel(int level) {
			if (level < 5)
				return level - 1;
			if (level < 23) 
				return level - 5;
			return level - 23;
		}
	};
	public static GameInfo currentGame =  defGame;
	public static HashMap<String, GameInfo> episodes = new HashMap<String, GameInfo>();

	public static LONGp[] tmp_ptr = { new LONGp(), new LONGp(), new LONGp(), new LONGp(), new LONGp(), new LONGp(),
			new LONGp(), new LONGp(), };

	public static void MapSetAll2D(byte fill) {
		int i;

		for (i = 0; i < (MAXWALLS >> 3); i++)
			show2dwall[i] = fill;
		for (i = 0; i < (MAXSPRITES >> 3); i++)
			show2dsprite[i] = fill;
		for (i = 0; i < MAXSECTORS; i++) {
			if (sector[i] != null && sector[i].ceilingpicnum != 342 && sector[i].floorpicnum != 342)
				show2dsector[i >> 3] = fill;
		}
	}

	public static boolean LoadLevel(String filename) {
		BuildPos out = null;
		try {
			out = engine.loadboard(filename);
		} catch (FileNotFoundException | InvalidVersionException | RuntimeException e) {
			game.GameCrash(e.getMessage());
			return false;
		}

		Player[0].posx = out.x;
		Player[0].posy = out.y;
		Player[0].posz = out.z;
		Player[0].pang = out.ang;
		Player[0].cursectnum = out.sectnum;

		return true;
	}

	public static void InitGame() {
		RTS_Init("sw.rts");
		
		episodes.put(defGame.getFile().getPath(), defGame);

		InitAnim();

		engine.inittimer(120);

		ConsoleInit(); // Init console command list

		SoundStartup();
		MusicStartup();

		InitPalette();

		SortBreakInfo();
		parallaxyoffs = 0;
		parallaxtype = 1;
		pskyoff[0] = 0;
		pskybits = PlaxBits;
		// Default scale value for parallax skies
		parallaxyscale = 8192;

		Arrays.fill(Track, null);

		for (int i = 0; i < MAX_SW_PLAYERS; i++) {
			Player[i] = new PlayerStr();
			puser[i] = new LastLevelUser();
		}
		
		BorderSetView(gs.BorderNum);

//		LoadPLockFromScript( "swplock.txt" ); // Get Parental Lock setup info XXX
	}

	public static void ConsoleInit() {
		Console.Println("Initializing on-screen display system");
		Console.setVersion(game.appname + " " + game.sversion, 10, OSDTEXT_GOLD);
		
		Console.RegisterCvar(new OSDCOMMAND("levelinfo", null, new OSDCVARFUNC() {
			@Override
			public void execute() {
				String name;
				if (mUserFlag != UserFlag.UserMap || boardfilename == null) {
					name = currentGame.getMapTitle(Level);
				} else name = boardfilename;
				
				Console.Println("LevelInfo:", Console.OSDTEXT_GREEN);
				Console.Println("   Mapname: " + name);
				Console.Println("   Levelnum: " + Level);
			}
		}));

		Console.RegisterCvar(new OSDCOMMAND("hitscan", null, new OSDCVARFUNC() {
			@Override
			public void execute() {
				PlayerStr pp = Player[screenpeek];
				int daang = pp.getAnglei();
				int daz = ((100 - pp.getHorizi()) * 2000);

				engine.hitscan(pp.posx, pp.posy, pp.posz, pp.cursectnum, // Start position
						sintable[NORM_ANGLE(daang + 512)], // X vector of 3D ang
						sintable[NORM_ANGLE(daang)], // Y vector of 3D ang
						daz, // Z vector of 3D ang
						pHitInfo, 0xFFFFFFFF);

				Console.Println("Sprite : " + pHitInfo.hitsprite);
				if (pHitInfo.hitsprite != -1) {
					Console.Println(sprite[pHitInfo.hitsprite].toString());
				}
			}
		}));
		
		Console.RegisterCvar(new OSDCOMMAND("interpolationtype", null, new OSDCVARFUNC() {
			@Override
			public void execute() {
				if (Console.osd_argc != 2) {
					Console.Println("interpolationtype 0: disabled, 1: default, 2: wang enagled, 3: wang disabled");
					return;
				}
				try {
					String num = Console.osd_argv[1];
					int pnum = Integer.parseInt(num);
					nInterpolation = pnum;
				} catch (Exception e) {
				}
			}
		}).setRange(0, 3));

    }

	public static int Distance(int x1, int y1, int x2, int y2) {
		int min;

		if ((x2 = x2 - x1) < 0)
			x2 = -x2;

		if ((y2 = y2 - y1) < 0)
			y2 = -y2;

		if (x2 > y2)
			min = y2;
		else
			min = x2;

		return (x2 + y2 - DIV2(min));
	}

	public static void InitLevelGlobals() {
		// A few IMPORTANT GLOBAL RESETS
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		ChopTics = 0;
		dimensionmode = 3;
		zoom = 768;
		wait_active_check_offset = 0;
		PlaxCeilGlobZadjust = PlaxFloorGlobZadjust = Z(500);
		pskyoff[0] = 0;
		pskybits = PlaxBits;
		FinishedLevel = false;
		AnimCnt = 0;
		left_foot = false;
		screenpeek = myconnectindex;

		gNet.TimeLimitClock = gNet.TimeLimit;

		serpwasseen = false;
		sumowasseen = false;
		zillawasseen = false;
		triedplay = false;
		Arrays.fill(BossSpriteNum, (short) -1);
	}

	public static void InitNewGame() {
		for (int i = 0; i < MAX_SW_PLAYERS; i++) {
			// don't jack with the playerreadyflag
			int ready_bak = Player[i].playerreadyflag;
			Player[i].reset();
			Player[i].playerreadyflag = ready_bak;
			puser[i].reset();
		}
	}

	public static void InitLevelGlobals2() {
		// GLOBAL RESETS NOT DONE for LOAD GAME
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		InitTimingVars();
		Kills = 0;
		TotalKillable = 0;
		Bunny_Count = 0;
	}

	public static boolean InitLevel(String LevelName, boolean NewGame) {
		Terminate3DSounds();

		// A few IMPORTANT GLOBAL RESETS
		InitLevelGlobals();
		if (!DemoPlaying)
			CDAudio_Stop();

		InitLevelGlobals2();

		if (Level < 0)
			Level = 0;

		if (Level > MAX_LEVELS)
			Level = 1;

		PlayingLevel = Level;

		if (NewGame)
			InitNewGame();

		if(!LoadLevel(LevelName))
			return false;

		if (sector[0].extra != -1) {
			NormalVisibility = (short) (visibility = sector[0].extra);
			sector[0].extra = 0;
		} else
			NormalVisibility = (short) visibility;

		//
		// Do Player stuff first
		//

		InitAllPlayers(NewGame);

		QueueReset();
		PreMapCombineFloors();
		InitMultiPlayerInfo();
		InitAllPlayerSprites(NewGame);

		//
		// Do setup for sprite, track, panel, sector, etc
		//

		// Set levels up
		InitTimingVars();

		if(!SpriteSetup())
			return false;
		SpriteSetupPost(); // post processing - already gone once through the loop
		InitLighting();

		TrackSetup();

		PlayerPanelSetup();
		if(!SectorSetup())
			return false;
		if(!JS_InitMirrors())
			return false;
		JS_InitLockouts(); // Setup the lockout linked lists
		JS_ToggleLockouts(); // Init lockouts on/off

		PlaceSectorObjectsOnTracks();
		PlaceActorsOnTracks();
		PostSetupSectorObject();
		SetupMirrorTiles();
		initlava();
		
		return !kGameCrash;
	}

	public static void TerminateLevel() {
		int i, nexti, stat, pnum;

		System.err.println("TerminateLevel");

//		DemoTerm(); XXX

		Arrays.fill(Track, null);

		StopFX();
		Terminate3DSounds(); // Kill the 3d sounds linked list

		for (stat = STAT_PLAYER0; stat < STAT_PLAYER0 + numplayers; stat++) {

			pnum = stat - STAT_PLAYER0;

			for (i = headspritestat[stat]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				if (pUser[i] != null)
					puser[pnum].copy(pUser[i]);
			}
		}

		// Kill User memory and delete sprites
		for (stat = 0; stat < MAXSTATUS; stat++) {
			for (i = headspritestat[stat]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				KillSprite(i);
			}
		}

		// Clear Sprite Extension structure
		for (int s = 0; s < MAXSECTORS; s++) {
			if (SectUser[s] != null)
				SectUser[s].reset();
		}

		for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
			PlayerStr pp = Player[pnum];

			// Free panel sprites for players
			pClearSpriteList(pp);

			pp.DoPlayerAction = null;

			pp.PlayerSprite = -1;
			pp.PlayerUnderSprite = -1;

			Arrays.fill(pp.HasKey, (byte) 0);

			// pp.WpnFlags = 0;
			pp.CurWpn = null;

			Arrays.fill(pp.Wpn, null);
			Arrays.fill(pp.InventorySprite, null);
			Arrays.fill(pp.InventoryTics, (short) 0);

			pp.Killer = -1;

			List.Init(pp.PanelSpriteList);
		}

		JS_UnInitLockouts();
	}

	public static void InitPlayerGameSettings() {
		if (game.nNetMode == NetMode.Single) {
			if (gs.AutoAim)
				Player[myconnectindex].Flags |= (PF_AUTO_AIM);
			else
				Player[myconnectindex].Flags &= ~(PF_AUTO_AIM);
		}

		// everyone had their own Auto Run
		if (gs.AutoRun)
			Player[myconnectindex].Flags |= (PF_LOCK_RUN);
		else
			Player[myconnectindex].Flags &= ~(PF_LOCK_RUN);
	}
	
	public static void InitTimingVars() {
		PlayClock = 0;

		totalsynctics = 0;
		engine.srand(17);
		game.pNet.ResetTimers();

		MoveSkip8 = 2;
		MoveSkip2 = false;
		MoveSkip4 = 1; // start slightly offset so these
						// don't move the same
						// as the Skip2's
		gNet.MoveThingsCount = 0;
	}

	private static final char[] buffer = new char[256];

	public static char[] getMapName(int num) {
		Arrays.fill(buffer, (char) 0);
		if (mUserFlag != UserFlag.UserMap || boardfilename == null) {
			String name = currentGame.getMapTitle(num);
			if(name != null)
				name.getChars(0, Math.min(name.length(), buffer.length), buffer, 0);
			return buffer;
		} else {
			int index = boardfilename.lastIndexOf(File.separator);
			boardfilename.getChars(index + 1, Math.min(boardfilename.length(), buffer.length), buffer, 0);
			return buffer;
		}
	}
	
	public static boolean isOriginal() {
		if((game.isCurrentScreen(gDemoScreen) && gDemoScreen.demfile != null && gDemoScreen.demfile.nVersion == 1))
			return true;

		return false;
	}
	
	public static int swGetAddon()
	{
		int addon = 0;
		if(currentGame.episode.length > 1 && currentGame.episode[1] == pWantonEp)
			addon = 1;
		if(currentGame.episode.length > 1 && currentGame.episode[1] == pTwinDragonEp)
			addon = 2;
		
		return addon;
	}
}
