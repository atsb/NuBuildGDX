package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.CEIL;
import static ru.m210projects.Build.Engine.FLOOR;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.neartag;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.numwalls;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Gameutils.isValidSector;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Wang.Break.SetupWallForBreak;
import static ru.m210projects.Wang.CopySect.CopySectorMatch;
import static ru.m210projects.Wang.Digi.DIGI_ANCIENTSECRET;
import static ru.m210projects.Wang.Digi.DIGI_BIGSWITCH;
import static ru.m210projects.Wang.Digi.DIGI_DROPSOAP;
import static ru.m210projects.Wang.Digi.DIGI_HITTINGWALLS;
import static ru.m210projects.Wang.Digi.DIGI_ITEM;
import static ru.m210projects.Wang.Digi.DIGI_JG44027;
import static ru.m210projects.Wang.Digi.DIGI_JG44038;
import static ru.m210projects.Wang.Digi.DIGI_JG44039;
import static ru.m210projects.Wang.Digi.DIGI_JG44048;
import static ru.m210projects.Wang.Digi.DIGI_JG44052;
import static ru.m210projects.Wang.Digi.DIGI_JG44068;
import static ru.m210projects.Wang.Digi.DIGI_JG45010;
import static ru.m210projects.Wang.Digi.DIGI_JG45014;
import static ru.m210projects.Wang.Digi.DIGI_JG45018;
import static ru.m210projects.Wang.Digi.DIGI_JG45030;
import static ru.m210projects.Wang.Digi.DIGI_JG45033;
import static ru.m210projects.Wang.Digi.DIGI_JG45043;
import static ru.m210projects.Wang.Digi.DIGI_JG45053;
import static ru.m210projects.Wang.Digi.DIGI_JG45067;
import static ru.m210projects.Wang.Digi.DIGI_JG46005;
import static ru.m210projects.Wang.Digi.DIGI_JG46010;
import static ru.m210projects.Wang.Digi.DIGI_PFLIP;
import static ru.m210projects.Wang.Digi.DIGI_PLAYER_TELEPORT;
import static ru.m210projects.Wang.Digi.DIGI_PULLMYFINGER;
import static ru.m210projects.Wang.Digi.DIGI_REALTITS;
import static ru.m210projects.Wang.Digi.DIGI_REGULARSWITCH;
import static ru.m210projects.Wang.Digi.DIGI_SEARCHWALL;
import static ru.m210projects.Wang.Digi.DIGI_SOAPYOUGOOD;
import static ru.m210projects.Wang.Digi.DIGI_WASHWANG;
import static ru.m210projects.Wang.Digi.DIGI_WHATDIEDUPTHERE;
import static ru.m210projects.Wang.Digi.DIGI_WHATYOUEATBABY;
import static ru.m210projects.Wang.Digi.DIGI_YOUGOPOOPOO;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Game.DebugActorFreeze;
import static ru.m210projects.Wang.Game.Distance;
import static ru.m210projects.Wang.Game.ExitLevel;
import static ru.m210projects.Wang.Game.FinishedLevel;
import static ru.m210projects.Wang.Game.Level;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.swGetAddon;
import static ru.m210projects.Wang.Gameutils.CEILING_STAT_PLAX;
import static ru.m210projects.Wang.Gameutils.CEILING_STAT_SLOPE;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_MISSILE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK_HITSCAN;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_FLOOR;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_WALL;
import static ru.m210projects.Wang.Gameutils.DISTANCE;
import static ru.m210projects.Wang.Gameutils.FAF_MIRROR_PIC;
import static ru.m210projects.Wang.Gameutils.FAF_PLACE_MIRROR_PIC;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_PRESSED;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RELEASE;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RESET;
import static ru.m210projects.Wang.Gameutils.FLOOR_STAT_PLAX;
import static ru.m210projects.Wang.Gameutils.FLOOR_STAT_SLOPE;
import static ru.m210projects.Wang.Gameutils.FindDistance3D;
import static ru.m210projects.Wang.Gameutils.LOW_TAG;
import static ru.m210projects.Wang.Gameutils.LOW_TAG_WALL;
import static ru.m210projects.Wang.Gameutils.MAXANIM;
import static ru.m210projects.Wang.Gameutils.MAXSO;
import static ru.m210projects.Wang.Gameutils.MAX_SECTOR_OBJECTS;
import static ru.m210projects.Wang.Gameutils.MOVEx;
import static ru.m210projects.Wang.Gameutils.MOVEy;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.PF2_TELEPORTED;
import static ru.m210projects.Wang.Gameutils.PF_DIVING;
import static ru.m210projects.Wang.Gameutils.PIXZ;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.SEARCH_DOWN;
import static ru.m210projects.Wang.Gameutils.SEARCH_FLOOR;
import static ru.m210projects.Wang.Gameutils.SEC;
import static ru.m210projects.Wang.Gameutils.SECTFU_DAMAGE_ABOVE_SECTOR;
import static ru.m210projects.Wang.Gameutils.SECTFX_LIQUID_LAVA;
import static ru.m210projects.Wang.Gameutils.SECTFX_LIQUID_WATER;
import static ru.m210projects.Wang.Gameutils.SECTFX_SINK;
import static ru.m210projects.Wang.Gameutils.SECTFX_WARP_SECTOR;
import static ru.m210projects.Wang.Gameutils.SECTFX_Z_ADJUST;
import static ru.m210projects.Wang.Gameutils.SET_BOOL6;
import static ru.m210projects.Wang.Gameutils.SK_OPERATE;
import static ru.m210projects.Wang.Gameutils.SOBJ_KILLABLE;
import static ru.m210projects.Wang.Gameutils.SOBJ_SYNC1;
import static ru.m210projects.Wang.Gameutils.SOBJ_SYNC2;
import static ru.m210projects.Wang.Gameutils.SPRITE_TAG6;
import static ru.m210projects.Wang.Gameutils.SPRITEp_BOS;
import static ru.m210projects.Wang.Gameutils.SPRITEp_SIZE_Z;
import static ru.m210projects.Wang.Gameutils.SPR_WAIT_FOR_TRIGGER;
import static ru.m210projects.Wang.Gameutils.SP_TAG1;
import static ru.m210projects.Wang.Gameutils.SP_TAG13;
import static ru.m210projects.Wang.Gameutils.SP_TAG2;
import static ru.m210projects.Wang.Gameutils.SP_TAG3;
import static ru.m210projects.Wang.Gameutils.SP_TAG4;
import static ru.m210projects.Wang.Gameutils.SP_TAG5;
import static ru.m210projects.Wang.Gameutils.SP_TAG6;
import static ru.m210projects.Wang.Gameutils.SP_TAG7;
import static ru.m210projects.Wang.Gameutils.SP_TAG8;
import static ru.m210projects.Wang.Gameutils.SP_TAG9;
import static ru.m210projects.Wang.Gameutils.STD_RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL1;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL2;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL3;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL4;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL5;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL6;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL7;
import static ru.m210projects.Wang.Gameutils.TEST_SYNC_KEY;
import static ru.m210projects.Wang.Gameutils.WALLFX_DONT_MOVE;
import static ru.m210projects.Wang.Gameutils.WALLFX_DONT_SCALE;
import static ru.m210projects.Wang.Gameutils.WALLFX_DONT_STICK;
import static ru.m210projects.Wang.Gameutils.WALLFX_LOOP_DONT_SPIN;
import static ru.m210projects.Wang.Gameutils.WALLFX_LOOP_OUTER;
import static ru.m210projects.Wang.Gameutils.WALLFX_LOOP_OUTER_SECONDARY;
import static ru.m210projects.Wang.Gameutils.WALLFX_LOOP_REVERSE_SPIN;
import static ru.m210projects.Wang.Gameutils.WALLFX_LOOP_SPIN_2X;
import static ru.m210projects.Wang.Gameutils.WALLFX_LOOP_SPIN_4X;
import static ru.m210projects.Wang.Gameutils.WPN_ROCKET;
import static ru.m210projects.Wang.Gameutils.WPN_STAR;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.JSector.UnlockKeyLock;
import static ru.m210projects.Wang.JTags.SWITCH_LOCKED;
import static ru.m210projects.Wang.Light.DiffuseLighting;
import static ru.m210projects.Wang.Light.DoLighting;
import static ru.m210projects.Wang.Light.DoLightingMatch;
import static ru.m210projects.Wang.Light.SectorLightShade;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.MiscActr.s_Pachinko1Operate;
import static ru.m210projects.Wang.MiscActr.s_Pachinko2Operate;
import static ru.m210projects.Wang.MiscActr.s_Pachinko3Operate;
import static ru.m210projects.Wang.MiscActr.s_Pachinko4Operate;
import static ru.m210projects.Wang.Morth.DoSOevent;
import static ru.m210projects.Wang.Morth.DoSectorObjectSetScale;
import static ru.m210projects.Wang.Names.CARGIRL_R0;
import static ru.m210projects.Wang.Names.DAMAGE_TIME;
import static ru.m210projects.Wang.Names.MECHANICGIRL_R0;
import static ru.m210projects.Wang.Names.PACHINKO1;
import static ru.m210projects.Wang.Names.PACHINKO2;
import static ru.m210projects.Wang.Names.PACHINKO3;
import static ru.m210projects.Wang.Names.PACHINKO4;
import static ru.m210projects.Wang.Names.PRUNEGIRL_R0;
import static ru.m210projects.Wang.Names.SAILORGIRL_R0;
import static ru.m210projects.Wang.Names.ST1;
import static ru.m210projects.Wang.Names.STAT_AMBIENT;
import static ru.m210projects.Wang.Names.STAT_CEILING_PAN;
import static ru.m210projects.Wang.Names.STAT_CHANGOR;
import static ru.m210projects.Wang.Names.STAT_DEFAULT;
import static ru.m210projects.Wang.Names.STAT_DELETE_SPRITE;
import static ru.m210projects.Wang.Names.STAT_ENEMY;
import static ru.m210projects.Wang.Names.STAT_EXPLODING_CEIL_FLOOR;
import static ru.m210projects.Wang.Names.STAT_FAF;
import static ru.m210projects.Wang.Names.STAT_FLOOR_PAN;
import static ru.m210projects.Wang.Names.STAT_ITEM;
import static ru.m210projects.Wang.Names.STAT_LIGHTING;
import static ru.m210projects.Wang.Names.STAT_NO_STATE;
import static ru.m210projects.Wang.Names.STAT_ROTATOR;
import static ru.m210projects.Wang.Names.STAT_SLIDOR;
import static ru.m210projects.Wang.Names.STAT_SOUND_SPOT;
import static ru.m210projects.Wang.Names.STAT_SPAWN_SPOT;
import static ru.m210projects.Wang.Names.STAT_SPAWN_TRIGGER;
import static ru.m210projects.Wang.Names.STAT_SPIKE;
import static ru.m210projects.Wang.Names.STAT_SPRITE_HIT_MATCH;
import static ru.m210projects.Wang.Names.STAT_STATIC_FIRE;
import static ru.m210projects.Wang.Names.STAT_STOP_SOUND_SPOT;
import static ru.m210projects.Wang.Names.STAT_TRAP;
import static ru.m210projects.Wang.Names.STAT_VATOR;
import static ru.m210projects.Wang.Names.STAT_WALL_DONT_MOVE_LOWER;
import static ru.m210projects.Wang.Names.STAT_WALL_DONT_MOVE_UPPER;
import static ru.m210projects.Wang.Names.STAT_WALL_PAN;
import static ru.m210projects.Wang.Names.TOILETGIRL_R0;
import static ru.m210projects.Wang.Names.WASHGIRL_R0;
import static ru.m210projects.Wang.Panel.PlayerUpdateAmmo;
import static ru.m210projects.Wang.Panel.PlayerUpdateHealth;
import static ru.m210projects.Wang.Player.DoPlayerBeginForceJump;
import static ru.m210projects.Wang.Player.DoPlayerWarpTeleporter;
import static ru.m210projects.Wang.Player.DoSpawnTeleporterEffectPlace;
import static ru.m210projects.Wang.Quake.DoQuakeMatch;
import static ru.m210projects.Wang.Rooms.FAFcansee;
import static ru.m210projects.Wang.Rooms.FAFhitscan;
import static ru.m210projects.Wang.Rooms.zofslope;
import static ru.m210projects.Wang.Rotator.DoRotatorMatch;
import static ru.m210projects.Wang.Rotator.DoRotatorOperate;
import static ru.m210projects.Wang.Rotator.TestRotatorMatchActive;
import static ru.m210projects.Wang.Shrap.SpawnShrap;
import static ru.m210projects.Wang.Slidor.DoSlidorMatch;
import static ru.m210projects.Wang.Slidor.DoSlidorOperate;
import static ru.m210projects.Wang.Slidor.TestSlidorMatchActive;
import static ru.m210projects.Wang.Sound.DeleteNoSoundOwner;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.PlayerSound;
import static ru.m210projects.Wang.Sound.Set3DSoundOwner;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_doppler;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sound.v3df_init;
import static ru.m210projects.Wang.Sound.v3df_nolookup;
import static ru.m210projects.Wang.Sound.v3df_none;
import static ru.m210projects.Wang.Spike.DoSpikeMatch;
import static ru.m210projects.Wang.Spike.DoSpikeOperate;
import static ru.m210projects.Wang.Spike.TestSpikeMatchActive;
import static ru.m210projects.Wang.Sprites.ActorSpawn;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.DoGrating;
import static ru.m210projects.Wang.Sprites.GetSectUser;
import static ru.m210projects.Wang.Sprites.KillSprite;
import static ru.m210projects.Wang.Sprites.MoveSkip2;
import static ru.m210projects.Wang.Sprites.MoveSkip4;
import static ru.m210projects.Wang.Sprites.SectorObject;
import static ru.m210projects.Wang.Sprites.SpawnItemsMatch;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Sprites.change_sprite_stat;
import static ru.m210projects.Wang.Stag.BOLT_TRAP;
import static ru.m210projects.Wang.Stag.FIREBALL_TRAP;
import static ru.m210projects.Wang.Stag.SECT_LOCK_DOOR;
import static ru.m210projects.Wang.Stag.SECT_ROTATOR;
import static ru.m210projects.Wang.Stag.SECT_SLIDOR;
import static ru.m210projects.Wang.Stag.SECT_VATOR;
import static ru.m210projects.Wang.Stag.SPAWN_SPOT;
import static ru.m210projects.Wang.Stag.SPEAR_TRAP;
import static ru.m210projects.Wang.Tags.TAG_COMBO_SWITCH_EVERYTHING;
import static ru.m210projects.Wang.Tags.TAG_COMBO_SWITCH_EVERYTHING_ONCE;
import static ru.m210projects.Wang.Tags.TAG_DOOR_ROTATE;
import static ru.m210projects.Wang.Tags.TAG_DOOR_SLIDING;
import static ru.m210projects.Wang.Tags.TAG_LEVEL_EXIT_SWITCH;
import static ru.m210projects.Wang.Tags.TAG_LIGHT_SWITCH;
import static ru.m210projects.Wang.Tags.TAG_LIGHT_TRIGGER;
import static ru.m210projects.Wang.Tags.TAG_OBJECT_CENTER;
import static ru.m210projects.Wang.Tags.TAG_ROTATE_SO_SWITCH;
import static ru.m210projects.Wang.Tags.TAG_ROTATOR;
import static ru.m210projects.Wang.Tags.TAG_SECRET_AREA_TRIGGER;
import static ru.m210projects.Wang.Tags.TAG_SECTOR_TRIGGER_VATOR;
import static ru.m210projects.Wang.Tags.TAG_SINE_WAVE_BOTH;
import static ru.m210projects.Wang.Tags.TAG_SINE_WAVE_CEILING;
import static ru.m210projects.Wang.Tags.TAG_SINE_WAVE_FLOOR;
import static ru.m210projects.Wang.Tags.TAG_SLIDOR;
import static ru.m210projects.Wang.Tags.TAG_SO_EVENT_SWITCH;
import static ru.m210projects.Wang.Tags.TAG_SO_EVENT_TRIGGER;
import static ru.m210projects.Wang.Tags.TAG_SO_SCALE_ONCE_SWITCH;
import static ru.m210projects.Wang.Tags.TAG_SO_SCALE_ONCE_TRIGGER;
import static ru.m210projects.Wang.Tags.TAG_SO_SCALE_SWITCH;
import static ru.m210projects.Wang.Tags.TAG_SO_SCALE_TRIGGER;
import static ru.m210projects.Wang.Tags.TAG_SPAWN_ACTOR_TRIGGER;
import static ru.m210projects.Wang.Tags.TAG_SPRING_BOARD;
import static ru.m210projects.Wang.Tags.TAG_SPRITE_GRATING;
import static ru.m210projects.Wang.Tags.TAG_SPRITE_SWITCH_VATOR;
import static ru.m210projects.Wang.Tags.TAG_SWITCH_EVERYTHING;
import static ru.m210projects.Wang.Tags.TAG_SWITCH_EVERYTHING_ONCE;
import static ru.m210projects.Wang.Tags.TAG_TRIGGER_ACTORS;
import static ru.m210projects.Wang.Tags.TAG_TRIGGER_EVERYTHING;
import static ru.m210projects.Wang.Tags.TAG_TRIGGER_EVERYTHING_ONCE;
import static ru.m210projects.Wang.Tags.TAG_TRIGGER_EXPLODING_SECTOR;
import static ru.m210projects.Wang.Tags.TAG_TRIGGER_MISSILE_TRAP;
import static ru.m210projects.Wang.Tags.TAG_VATOR;
import static ru.m210projects.Wang.Tags.TAG_WALL_DONT_MOVE;
import static ru.m210projects.Wang.Tags.TAG_WALL_LOOP_DONT_SCALE;
import static ru.m210projects.Wang.Tags.TAG_WALL_LOOP_DONT_SPIN;
import static ru.m210projects.Wang.Tags.TAG_WALL_LOOP_OUTER;
import static ru.m210projects.Wang.Tags.TAG_WALL_LOOP_OUTER_SECONDARY;
import static ru.m210projects.Wang.Tags.TAG_WALL_LOOP_REVERSE_SPIN;
import static ru.m210projects.Wang.Tags.TAG_WALL_LOOP_SPIN_2X;
import static ru.m210projects.Wang.Tags.TAG_WALL_LOOP_SPIN_4X;
import static ru.m210projects.Wang.Tags.TAG_WALL_SINE_X_BEGIN;
import static ru.m210projects.Wang.Tags.TAG_WALL_SINE_Y_BEGIN;
import static ru.m210projects.Wang.Text.PutStringInfo;
import static ru.m210projects.Wang.Track.CollapseSectorObject;
import static ru.m210projects.Wang.Track.KillSectorObjectSprites;
import static ru.m210projects.Wang.Track.MoveSectorObjects;
import static ru.m210projects.Wang.Track.SetupSectorObject;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.DIV2;
import static ru.m210projects.Wang.Type.MyTypes.DTEST;
import static ru.m210projects.Wang.Type.MyTypes.MAXLONG;
import static ru.m210projects.Wang.Type.MyTypes.OFF;
import static ru.m210projects.Wang.Type.MyTypes.ON;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Vator.DoVatorMatch;
import static ru.m210projects.Wang.Vator.DoVatorOperate;
import static ru.m210projects.Wang.Vator.TestVatorMatchActive;
import static ru.m210projects.Wang.WallMove.DoWallMoveMatch;
import static ru.m210projects.Wang.Weapon.InitBoltTrap;
import static ru.m210projects.Wang.Weapon.InitFireballTrap;
import static ru.m210projects.Wang.Weapon.InitSpearTrap;
import static ru.m210projects.Wang.Weapon.MAX_STAR_QUEUE;
import static ru.m210projects.Wang.Weapon.MissileHitMatch;
import static ru.m210projects.Wang.Weapon.PlayerCheckDeath;
import static ru.m210projects.Wang.Weapon.SpawnSectorExp;
import static ru.m210projects.Wang.Weapon.SpriteQueueDelete;
import static ru.m210projects.Wang.Weapon.StarQueue;
import static ru.m210projects.Wang.Weapons.Star.InitWeaponStar;

import ru.m210projects.Build.Pattern.Tools.Interpolation;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Wang.Sound.SoundType;
import ru.m210projects.Wang.Type.Anim;
import ru.m210projects.Wang.Type.Anim.AnimCallback;
import ru.m210projects.Wang.Type.Anim.AnimType;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.NEAR_TAG_INFO;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.SINE_WALL;
import ru.m210projects.Wang.Type.SINE_WAVE_FLOOR;
import ru.m210projects.Wang.Type.Sect_User;
import ru.m210projects.Wang.Type.Sector_Object;
import ru.m210projects.Wang.Type.Spring_Board;
import ru.m210projects.Wang.Type.USER;
import ru.m210projects.Wang.Type.Vector3i;

public class Sector {

	public static final int NTAG_SEARCH_LO = 1;
	public static final int NTAG_SEARCH_HI = 2;
	public static final int NTAG_SEARCH_LO_HI = 3;

	public static final int LAVASIZ = 128;
	public static final int LAVALOGSIZ = 7;
	public static final int LAVAMAXDROPS = 32;
	public static final int DEFAULT_DOOR_SPEED = 800;

	public static short LevelSecrets;
	public static PlayerStr GlobPlayerStr;
	public static Sect_User[] SectUser = new Sect_User[MAXSECTORS];

	public static final int MAX_SINE_WAVE = 6;
	public static final int MAX_SINE_WALL = 10;
	public static final int MAX_SINE_WALL_POINTS = 64;

	public static Anim pAnim[] = new Anim[MAXANIM];
	public static int AnimCnt = 0;

	public static SINE_WAVE_FLOOR SineWaveFloor[][] = new SINE_WAVE_FLOOR[MAX_SINE_WAVE][21];
	public static SINE_WALL SineWall[][] = new SINE_WALL[MAX_SINE_WALL][MAX_SINE_WALL_POINTS];
	public static Spring_Board SpringBoard[] = new Spring_Board[20];
	public static int x_min_bound, y_min_bound, x_max_bound, y_max_bound;

	public static final int SO_SCALE_NONE = 0;
	public static final int SO_SCALE_HOLD = 1;
	public static final int SO_SCALE_DEST = 2;
	public static final int SO_SCALE_RANDOM = 3;
	public static final int SO_SCALE_CYCLE = 4;
	public static final int SO_SCALE_RANDOM_POINT = 5;

	public static final int SCALE_POINT_SPEED = (4 + RANDOM_RANGE(8));

	private static Vector3i midPoint = new Vector3i();

	public static final int SWITCH_LEVER = 581;
	public static final int SWITCH_FUSE = 558;
	public static final int SWITCH_FLIP = 561;
	public static final int SWITCH_RED_CHAIN = 563;
	public static final int SWITCH_GREEN_CHAIN = 565;
	public static final int SWITCH_TOUCH = 567;
	public static final int SWITCH_DRAGON = 569;

	public static final int SWITCH_LIGHT = 551;
	public static final int SWITCH_1 = 575;
	public static final int SWITCH_3 = 579;

	public static final int SWITCH_SHOOTABLE_1 = 577;
	public static final int SWITCH_4 = 571;
	public static final int SWITCH_5 = 573;
	public static final int SWITCH_6 = 583;
	public static final int EXIT_SWITCH = 2470;

	public static final int SWITCH_SKULL = 553;

	public static void InitSectorStructs() {
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
		for (int i = 0; i < 16; i++)
			nti[i] = new NEAR_TAG_INFO();
	}

	public static void SetSectorWallBits(short sectnum, int bit_mask, boolean set_sectwall, boolean set_nextwall) {
		short start_wall;
		short wall_num = start_wall = sector[sectnum].wallptr;

		do {
			if (set_sectwall)
				wall[wall_num].extra |= bit_mask;

			if (set_nextwall && wall[wall_num].nextwall >= 0)
				wall[wall[wall_num].nextwall].extra |= bit_mask;

			wall_num = wall[wall_num].point2;
		} while (wall_num != start_wall);

	}

	public static void WallSetupDontMove() {
		int i, j, nexti, nextj;
		SPRITE spu, spl;
		WALL wal;

		for (i = headspritestat[STAT_WALL_DONT_MOVE_UPPER]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			for (j = headspritestat[STAT_WALL_DONT_MOVE_LOWER]; j != -1; j = nextj) {
				nextj = nextspritestat[j];
				spu = sprite[i];
				spl = sprite[j];

				if (spu.lotag == spl.lotag) {
					for (int w = 0; w < numwalls; w++) {
						wal = wall[w];
						if (wal.x < spl.x && wal.x > spu.x && wal.y < spl.y && wal.y > spu.y) {
							wal.extra |= WALLFX_DONT_MOVE;
						}
					}
				}
			}
		}
	}

	public static void WallSetup() {
		short i = 0;
		short NextSineWall = 0;
		WALL wp;

		WallSetupDontMove();

		for (int a = 0; a < MAX_SINE_WALL; a++)
			for (int b = 0; b < MAX_SINE_WALL_POINTS; b++)
				SineWall[a][b].reset();

		x_min_bound = 999999;
		y_min_bound = 999999;
		x_max_bound = -999999;
		y_max_bound = -999999;

		for (i = 0; i < numwalls; i++) {
			wp = wall[i];
			if (wp.picnum == FAF_PLACE_MIRROR_PIC)
				wp.picnum = FAF_MIRROR_PIC;

			if (wp.picnum == FAF_PLACE_MIRROR_PIC + 1)
				wp.picnum = FAF_MIRROR_PIC + 1;

			// get map min and max coordinates
			x_min_bound = Math.min(wp.x, x_min_bound);
			y_min_bound = Math.min(wp.y, y_min_bound);
			x_max_bound = Math.max(wp.x, x_max_bound);
			y_max_bound = Math.max(wp.y, y_max_bound);

			// this overwrites the lotag so it needs to be called LAST - its down there
			// SetupWallForBreak(wp);

			switch (wp.lotag) {
			case TAG_WALL_LOOP_DONT_SPIN: {
				short wall_num, start_wall;

				// set first wall
				wp.extra |= WALLFX_LOOP_DONT_SPIN;
				wall[wp.nextwall].extra |= WALLFX_LOOP_DONT_SPIN;

				// move the the next wall
				start_wall = wp.point2;

				// Travel all the way around loop setting wall bits
				for (wall_num = start_wall; wall[wall_num].lotag != TAG_WALL_LOOP_DONT_SPIN; wall_num = wall[wall_num].point2) {
					wall[wall_num].extra |= WALLFX_LOOP_DONT_SPIN;
					if (wall[wall_num].nextwall != -1)
						wall[wall[wall_num].nextwall].extra |= WALLFX_LOOP_DONT_SPIN;
				}

				break;
			}

			case TAG_WALL_LOOP_DONT_SCALE: {
				short wall_num, start_wall;

				// set first wall
				wp.extra |= (WALLFX_DONT_SCALE);
				wall[wp.nextwall].extra |= (WALLFX_DONT_SCALE);

				// move the the next wall
				start_wall = wp.point2;

				// Travel all the way around loop setting wall bits
				for (wall_num = start_wall; wall[wall_num].lotag != TAG_WALL_LOOP_DONT_SCALE; wall_num = wall[wall_num].point2) {
					wall[wall_num].extra |= (WALLFX_DONT_SCALE);
					if (wall[wall_num].nextwall >= 0)
						wall[wall[wall_num].nextwall].extra |= (WALLFX_DONT_SCALE);
				}

				wp.lotag = 0;

				break;
			}

			case TAG_WALL_LOOP_OUTER_SECONDARY: {
				short wall_num, start_wall;

				// set first wall
				wp.extra |= (WALLFX_LOOP_OUTER | WALLFX_LOOP_OUTER_SECONDARY);
				wall[wp.nextwall].extra |= (WALLFX_LOOP_OUTER | WALLFX_LOOP_OUTER_SECONDARY);

				// move the the next wall
				start_wall = wp.point2;

				// Travel all the way around loop setting wall bits
				for (wall_num = start_wall; wall[wall_num].lotag != TAG_WALL_LOOP_OUTER_SECONDARY; wall_num = wall[wall_num].point2) {
					wall[wall_num].extra |= (WALLFX_LOOP_OUTER | WALLFX_LOOP_OUTER_SECONDARY);
					wall[wall[wall_num].nextwall].extra |= (WALLFX_LOOP_OUTER | WALLFX_LOOP_OUTER_SECONDARY);
				}

				break;
			}

			case TAG_WALL_LOOP_OUTER: {
				short wall_num, start_wall;

				// set first wall
				wp.extra |= (WALLFX_LOOP_OUTER);
				if (wp.nextwall != -1)
					wall[wp.nextwall].extra |= (WALLFX_LOOP_OUTER);

				// move the the next wall
				start_wall = wp.point2;

				// Travel all the way around loop setting wall bits
				for (wall_num = start_wall; wall[wall_num].lotag != TAG_WALL_LOOP_OUTER; wall_num = wall[wall_num].point2) {
					wall[wall_num].extra |= (WALLFX_LOOP_OUTER);
					if (wall[wall_num].nextwall != -1)
						wall[wall[wall_num].nextwall].extra |= (WALLFX_LOOP_OUTER);
				}

				wp.lotag = 0;

				break;
			}

			case TAG_WALL_DONT_MOVE: {
				// set first wall
				wp.extra |= (WALLFX_DONT_MOVE);
				break;
			}

			case TAG_WALL_LOOP_SPIN_2X: {
				short wall_num, start_wall;

				// set first wall
				wp.extra |= (WALLFX_LOOP_SPIN_2X);
				wall[wp.nextwall].extra |= (WALLFX_LOOP_SPIN_2X);

				// move the the next wall
				start_wall = wp.point2;

				// Travel all the way around loop setting wall bits
				for (wall_num = start_wall; wall[wall_num].lotag != TAG_WALL_LOOP_SPIN_2X; wall_num = wall[wall_num].point2) {
					wall[wall_num].extra |= (WALLFX_LOOP_SPIN_2X);
					wall[wall[wall_num].nextwall].extra |= (WALLFX_LOOP_SPIN_2X);
				}

				break;
			}

			case TAG_WALL_LOOP_SPIN_4X: {
				short wall_num, start_wall;

				// set first wall
				wp.extra |= (WALLFX_LOOP_SPIN_4X);
				wall[wp.nextwall].extra |= (WALLFX_LOOP_SPIN_4X);

				// move the the next wall
				start_wall = wp.point2;

				// Travel all the way around loop setting wall bits
				for (wall_num = start_wall; wall[wall_num].lotag != TAG_WALL_LOOP_SPIN_4X; wall_num = wall[wall_num].point2) {
					wall[wall_num].extra |= (WALLFX_LOOP_SPIN_4X);
					wall[wall[wall_num].nextwall].extra |= (WALLFX_LOOP_SPIN_4X);
				}

				break;
			}

			case TAG_WALL_LOOP_REVERSE_SPIN: {
				short wall_num, start_wall;

				// set first wall
				wp.extra |= (WALLFX_LOOP_REVERSE_SPIN);
				wall[wp.nextwall].extra |= (WALLFX_LOOP_REVERSE_SPIN);

				// move the the next wall
				start_wall = wp.point2;

				// Travel all the way around loop setting wall bits
				for (wall_num = start_wall; wall[wall_num].lotag != TAG_WALL_LOOP_REVERSE_SPIN; wall_num = wall[wall_num].point2) {
					wall[wall_num].extra |= (WALLFX_LOOP_REVERSE_SPIN);
					wall[wall[wall_num].nextwall].extra |= (WALLFX_LOOP_REVERSE_SPIN);
				}

				break;
			}

			case TAG_WALL_SINE_Y_BEGIN:
			case TAG_WALL_SINE_X_BEGIN: {
				short wall_num, cnt, num_points, type, tag_end;
				SINE_WALL sw;
				short range = 250, speed = 3, peak = 0;

				tag_end = (short) (wp.lotag + 2);

				type = (short) (wp.lotag - TAG_WALL_SINE_Y_BEGIN);

				// count up num_points
				for (wall_num = i, num_points = 0; num_points < MAX_SINE_WALL_POINTS
						&& wall[wall_num].lotag != tag_end; wall_num = wall[wall_num].point2, num_points++) {
					if (num_points == 0) {
						if (wall[wall_num].hitag != 0)
							range = wall[wall_num].hitag;
					} else if (num_points == 1) {
						if (wall[wall_num].hitag != 0)
							speed = wall[wall_num].hitag;
					} else if (num_points == 2) {
						if (wall[wall_num].hitag != 0)
							peak = wall[wall_num].hitag;
					}
				}

				if (peak != 0)
					num_points = peak;

				for (wall_num = i, cnt = 0; cnt < MAX_SINE_WALL_POINTS
						&& wall[wall_num].lotag != tag_end; wall_num = wall[wall_num].point2, cnt++) {
					// set the first on up
					sw = SineWall[NextSineWall][cnt];

					sw.type = type;
					sw.wall = wall_num;
					sw.speed_shift = speed;
					sw.range = range;

					// don't allow bullet holes/stars
					wall[wall_num].extra |= (WALLFX_DONT_STICK);

					if (sw.type == 0)
						sw.orig_xy = wall[wall_num].y - (sw.range >> 2);
					else
						sw.orig_xy = wall[wall_num].x - (sw.range >> 2);

					sw.sintable_ndx = (short) (cnt * (2048 / num_points));
				}

				NextSineWall++;

			}
			}

			// this overwrites the lotag so it needs to be called LAST
			SetupWallForBreak(wp);
		}
	}

	public static void SectorLiquidSet(short i) {
		Sect_User sectu;

		// ///////////////////////////////////
		//
		// CHECK for pics that mean something
		//
		// ///////////////////////////////////

		if (sector[i].floorpicnum >= 300 && sector[i].floorpicnum <= 307) {
			sectu = GetSectUser(i);

			sector[i].extra |= (SECTFX_LIQUID_WATER);
		} else if (sector[i].floorpicnum >= 320 && sector[i].floorpicnum <= 343) {
			sectu = GetSectUser(i);

			sector[i].extra |= (SECTFX_LIQUID_WATER);
		} else if (sector[i].floorpicnum >= 780 && sector[i].floorpicnum <= 794) {
			sectu = GetSectUser(i);

			sector[i].extra |= (SECTFX_LIQUID_WATER);
		} else if (sector[i].floorpicnum >= 890 && sector[i].floorpicnum <= 897) {
			sectu = GetSectUser(i);

			sector[i].extra |= (SECTFX_LIQUID_WATER);
		} else if (sector[i].floorpicnum >= 175 && sector[i].floorpicnum <= 182) {
			sectu = GetSectUser(i);

			sector[i].extra |= (SECTFX_LIQUID_LAVA);
			if (sectu.damage == 0)
				sectu.damage = 40;
		}
	}

	public static final int SINE_FLOOR = (1 << 0);
	public static final int SINE_CEILING = (1 << 1);
	public static final int SINE_SLOPED = BIT(3);

	public static boolean SectorSetup() {
		short i = 0, tag;
		short NextSineWave = 0;

		short ndx;

		WallSetup();

		for (ndx = 0; ndx < MAX_SECTOR_OBJECTS; ndx++) {
			SectorObject[ndx].reset();
			// 0 pointers

			SectorObject[ndx].xmid = MAXLONG;
		}

		for (int a = 0; a < MAX_SINE_WAVE; a++)
			for (int b = 0; b < 21; b++)
				SineWaveFloor[a][b].reset();

		for (int a = 0; a < 20; a++)
			SpringBoard[a].reset();

		LevelSecrets = 0;

		for (i = 0; i < numsectors; i++) {
			tag = (short) LOW_TAG(i);

			// Stupid Redux's fixes
			if (Level == 14 && swGetAddon() == 2 && i == 750
					&& tag == 216) {
				sector[i].lotag = 116;
			}

			// ///////////////////////////////////
			//
			// CHECK for pics that mean something
			//
			// ///////////////////////////////////

			// ///////////////////////////////////
			//
			// CHECK for flags
			//
			// ///////////////////////////////////

			if (TEST(sector[i].extra, SECTFX_SINK)) {
				SectorLiquidSet(i);
			}

			if (TEST(sector[i].floorstat, FLOOR_STAT_PLAX)) {
				// don't do a z adjust for FAF area
				if (sector[i].floorpicnum != FAF_PLACE_MIRROR_PIC) {
					sector[i].extra |= (SECTFX_Z_ADJUST);
				}
			}

			if (TEST(sector[i].ceilingstat, CEILING_STAT_PLAX)) {
				// don't do a z adjust for FAF area
				if (sector[i].ceilingpicnum != FAF_PLACE_MIRROR_PIC) {
					sector[i].extra |= (SECTFX_Z_ADJUST);
				}
			}

			// ///////////////////////////////////
			//
			// CHECK for sector/sprite objects
			//
			// ///////////////////////////////////

			if (tag >= TAG_OBJECT_CENTER && tag < TAG_OBJECT_CENTER + 100) {
				if (!SetupSectorObject(i, tag))
					return false;
			}

			// ///////////////////////////////////
			//
			// CHECK lo and hi tags
			//
			// ///////////////////////////////////

			switch (tag) {
			case TAG_SECRET_AREA_TRIGGER:
				LevelSecrets++;
				break;

			case TAG_DOOR_SLIDING:
				SetSectorWallBits(i, WALLFX_DONT_STICK, true, true);
				break;

			case TAG_SINE_WAVE_FLOOR:
			case TAG_SINE_WAVE_CEILING:
			case TAG_SINE_WAVE_BOTH: {
				SINE_WAVE_FLOOR swf;
				short near_sect = i, base_sect = i;
				short swf_ndx = 0;
				short cnt = 0, sector_cnt;
				int range;
				int range_diff = 0;
				int wave_diff = 0;
				short peak_dist = 0;
				short speed_shift = 3;
				short num;

				num = (short) ((tag - TAG_SINE_WAVE_FLOOR) / 20);

				// set the first on up
				swf = SineWaveFloor[NextSineWave][swf_ndx];

				swf.flags = 0;

				switch (num) {
				case 0:
					swf.flags |= (SINE_FLOOR);

					if (TEST(sector[base_sect].floorstat, FLOOR_STAT_SLOPE)) {
						swf.flags |= (SINE_SLOPED);
					}
					break;
				case 1:
					swf.flags |= (SINE_CEILING);
					break;
				case 2:
					swf.flags |= (SINE_FLOOR | SINE_CEILING);
					break;
				}

				swf.sector = near_sect;

				swf.range = range = Z(sector[swf.sector].hitag);
				swf.floor_origz = sector[swf.sector].floorz - (range >> 2);
				swf.ceiling_origz = sector[swf.sector].ceilingz - (range >> 2);

				// look for the rest by distance
				for (swf_ndx = 1, sector_cnt = 1; true; swf_ndx++) {
					near_sect = FindNextSectorByTag(base_sect, tag + swf_ndx);

					if (near_sect >= 0) {
						swf = SineWaveFloor[NextSineWave][swf_ndx];

						if (swf_ndx == 1 && sector[near_sect].hitag != 0)
							range_diff = sector[near_sect].hitag;
						else if (swf_ndx == 2 && sector[near_sect].hitag != 0)
							speed_shift = sector[near_sect].hitag;
						else if (swf_ndx == 3 && sector[near_sect].hitag != 0)
							peak_dist = sector[near_sect].hitag;

						swf.sector = near_sect;
						swf.floor_origz = sector[swf.sector].floorz - (range >> 2);
						swf.ceiling_origz = sector[swf.sector].ceilingz - (range >> 2);
						range -= range_diff;
						swf.range = range;

						base_sect = swf.sector;
						sector_cnt++;
					} else
						break;
				}

				// more than 6 waves and something in high tag - set up wave
				// dissapate
				if (sector_cnt > 8 && sector[base_sect].hitag != 0) {
					wave_diff = sector[base_sect].hitag;
				}

				// setup the sintable_ndx based on the actual number of
				// sectors (swf_ndx)
//	                for (swf = SineWaveFloor[NextSineWave][0], cnt = 0; swf.sector >= 0 && cnt < SineWaveFloor.length; swf++, cnt++) XXX
				for (cnt = 0; swf.sector >= 0 && cnt < SineWaveFloor[NextSineWave].length; cnt++) {
					swf = SineWaveFloor[NextSineWave][cnt];
					if (peak_dist != 0)
						swf.sintable_ndx = (short) (cnt * (2048 / peak_dist));
					else
						swf.sintable_ndx = (short) (cnt * (2048 / swf_ndx));

					swf.speed_shift = speed_shift;
				}

				// set up the a real wave that dissapates at the end
				if (wave_diff != 0) {
					for (cnt = (short) (sector_cnt - 1); cnt >= 0; cnt--) {
						// only do the last (actually the first) few for the
						// dissapate
						if (cnt > 8)
							continue;

						swf = SineWaveFloor[NextSineWave][cnt];

						swf.range -= wave_diff;

						wave_diff += wave_diff;

						if (swf.range < Z(4))
							swf.range = Z(4);

						// reset origz's based on new range
						swf.floor_origz = sector[swf.sector].floorz - (swf.range >> 2);
						swf.ceiling_origz = sector[swf.sector].ceilingz - (swf.range >> 2);
					}
				}

				NextSineWave++;

				break;
			}
			}
		}

		return true;
	}

	public static Vector3i SectorMidPoint(int sectnum) {
		short startwall, endwall;
		long xsum = 0, ysum = 0;
		WALL wp;

		startwall = sector[sectnum].wallptr;
		endwall = (short) (startwall + sector[sectnum].wallnum - 1);
		for (int k = startwall; k <= endwall; k++) {
			wp = wall[k];
			xsum += wp.x;
			ysum += wp.y;
		}

		midPoint.x = (int) (xsum / (endwall - startwall + 1));
		midPoint.y = (int) (ysum / (endwall - startwall + 1));
		midPoint.z = DIV2(sector[sectnum].floorz + sector[sectnum].ceilingz);

		return midPoint;
	}

	public static void DoSpringBoard(PlayerStr pp, short sectnum) {
		pp.jump_speed = -sector[pp.cursectnum].hitag;
		DoPlayerBeginForceJump(pp);
	}

	public static void DoSpringBoardDown() {
		int sb;
		Spring_Board sbp;

		for (sb = 0; sb < SpringBoard.length; sb++) {
			sbp = SpringBoard[sb];

			// if empty set up an entry to close the sb later
			if (sbp.Sector != -1) {
				if ((sbp.TimeOut -= synctics) <= 0) {
					int destz = sector[engine.nextsectorneighborz(sbp.Sector, sector[sbp.Sector].floorz, SEARCH_FLOOR,
							SEARCH_DOWN)].floorz;

					AnimSet(sbp.Sector, destz, 256, AnimType.FloorZ);

					sector[sbp.Sector].lotag = TAG_SPRING_BOARD;

					sbp.Sector = -1;
				}
			}
		}
	}

	public static short FindSectorByTag(int x, int y, int tag) {
		short i = 0, near_sector = -1;
		int diff, near_diff = 9999999;
		short wallnum;

		for (i = 0; i < numsectors; i++) {
			if (LOW_TAG(i) == tag) {
				// get the delta of the door/elevator
				wallnum = sector[i].wallptr;

				// diff = labs(wall[wallnum].x - x) + labs(wall[wallnum].y - y);
				diff = Distance(wall[wallnum].x, wall[wallnum].y, x, y);

				// if the door/elevator is closer than the last save it off
				if (diff < near_diff) {
					near_diff = diff;
					near_sector = i;
				}
			}
		}

		return (near_sector);

	}

	public static short FindSectorByTag_Wall(int wallnum, int tag) {
		return (FindSectorByTag(wall[wallnum].x, wall[wallnum].y, tag));
	}

	public static short FindSectorByTag_Sprite(int SpriteNum, int tag) {
		return (FindSectorByTag(sprite[SpriteNum].x, sprite[SpriteNum].y, tag));
	}

	public static short FindNextSectorByTag(int sectnum, int tag) {
		short next_sectnum, startwall, endwall, j;

		startwall = sector[sectnum].wallptr;
		endwall = (short) (startwall + sector[sectnum].wallnum - 1);

		for (j = startwall; j <= endwall; j++) {
			next_sectnum = wall[j].nextsector;

			if (next_sectnum >= 0) {
				if (sector[next_sectnum].lotag == tag) {
					return (next_sectnum);
				}
			}
		}

		return (-1);
	}

	public static int SectorDistance(int sect1, int sect2) {
		short wallnum1, wallnum2;

		if (sect1 < 0 || sect2 < 0)
			return (9999999);

		wallnum1 = sector[sect1].wallptr;
		wallnum2 = sector[sect2].wallptr;

		// return the distance between the two sectors.
		return (Distance(wall[wallnum1].x, wall[wallnum1].y, wall[wallnum2].x, wall[wallnum2].y));
	}

	public static int SectorDistanceByMid(short sect1, short sect2) {
		int sx1, sy1, sx2, sy2;

		Vector3i s1 = SectorMidPoint(sect1);
		sx1 = s1.x;
		sy1 = s1.y;
		Vector3i s2 = SectorMidPoint(sect2);
		sx2 = s2.x;
		sy2 = s2.y;

		// return the distance between the two sectors.
		return (Distance(sx1, sy1, sx2, sy2));
	}

	public static short DoSpawnActorTrigger(int match) {
		int i, nexti;
		short spawn_count = 0;
		SPRITE sp;

		for (i = headspritestat[STAT_SPAWN_TRIGGER]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (sp.hitag == match) {
				if (ActorSpawn(i)) {
					DoSpawnTeleporterEffectPlace(sp);
					PlaySound(DIGI_PLAYER_TELEPORT, sp, v3df_none);
					spawn_count++;
				}
			}
		}

		return (spawn_count);
	}

	public static boolean OperateSector(short sectnum, boolean player_is_operating) {
		PlayerStr pp = GlobPlayerStr;

		// Don't let actors operate locked or secret doors
		if (!player_is_operating) {
			SPRITE fsp;
			short i, nexti;

			if (SectUser[sectnum] != null && SectUser[sectnum].stag == SECT_LOCK_DOOR)
				return (false);

			for (i = headspritesect[sectnum]; i != -1; i = nexti) {
				nexti = nextspritesect[i];
				fsp = sprite[i];

				if (SectUser[fsp.sectnum] != null && SectUser[fsp.sectnum].stag == SECT_LOCK_DOOR)
					return (false);

				if (fsp.statnum == STAT_VATOR && SP_TAG1(fsp) == SECT_VATOR && TEST_BOOL7(fsp))
					return (false);
				if (fsp.statnum == STAT_ROTATOR && SP_TAG1(fsp) == SECT_ROTATOR && TEST_BOOL7(fsp))
					return (false);
				if (fsp.statnum == STAT_SLIDOR && SP_TAG1(fsp) == SECT_SLIDOR && TEST_BOOL7(fsp))
					return (false);

			}
		}

		switch (LOW_TAG(sectnum)) {

		case TAG_VATOR:
			DoVatorOperate(pp, sectnum);
			return (true);

		case TAG_ROTATOR:
			DoRotatorOperate(pp, sectnum);
			return (true);

		case TAG_SLIDOR:
			DoSlidorOperate(pp, sectnum);
			return (true);
		}

		return (false);
	}

	public static boolean OperateWall(short wallnum, boolean player_is_operating) {
		switch (LOW_TAG_WALL(wallnum)) {
		}

		return (false);
	}

	public static short AnimateSwitch(SPRITE sp, int tgt_value) {

		// if the value is not ON or OFF
		// then it is a straight toggle

		switch (sp.picnum) {
		// set to TRUE/ON
		case SWITCH_SKULL:
		case SWITCH_LEVER:
		case SWITCH_LIGHT:
		case SWITCH_SHOOTABLE_1:
		case SWITCH_1:
		case SWITCH_3:
		case SWITCH_FLIP:
		case SWITCH_RED_CHAIN:
		case SWITCH_GREEN_CHAIN:
		case SWITCH_TOUCH:
		case SWITCH_DRAGON:
		case SWITCH_4:
		case SWITCH_5:
		case SWITCH_6:
		case EXIT_SWITCH:

			// dont toggle - return the current state
			if (tgt_value == 999)
				return (OFF);

			sp.picnum += 1;

			// if the tgt_value should be true
			// flip it again - recursive but only once
			if (tgt_value == OFF) {
				AnimateSwitch(sp, tgt_value);
				return (OFF);
			}

			return (ON);

		// set to true
		case SWITCH_SKULL + 1:
		case SWITCH_LEVER + 1:
		case SWITCH_LIGHT + 1:
		case SWITCH_1 + 1:
		case SWITCH_3 + 1:
		case SWITCH_FLIP + 1:
		case SWITCH_RED_CHAIN + 1:
		case SWITCH_GREEN_CHAIN + 1:
		case SWITCH_TOUCH + 1:
		case SWITCH_DRAGON + 1:
		case SWITCH_SHOOTABLE_1 + 1:
		case SWITCH_4 + 1:
		case SWITCH_5 + 1:
		case SWITCH_6 + 1:
		case EXIT_SWITCH + 1:

			// dont toggle - return the current state
			if (tgt_value == 999)
				return (ON);

			sp.picnum -= 1;

			if (tgt_value == ON) {
				AnimateSwitch(sp, tgt_value);
				return (ON);
			}

			return (OFF);
		}
		return (OFF);
	}

	public static void SectorExp(short SpriteNum, short sectnum, int orig_ang, int zh) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		short explosion;
		SPRITE exp;
		USER eu;
		int x, y, z;

		sp.cstat &= ~(CSTAT_SPRITE_WALL | CSTAT_SPRITE_FLOOR);
		Vector3i p = SectorMidPoint(sectnum);
		x = p.x;
		y = p.y;
		z = p.z;

		sp.ang = (short) orig_ang;
		sp.x = x;
		sp.y = y;
		sp.z = z;

		// randomize the explosions
		sp.ang += RANDOM_P2(256) - 128;
		sp.x += RANDOM_P2(1024) - 512;
		sp.y += RANDOM_P2(1024) - 512;
		sp.z = zh;

		// setup vars needed by SectorExp
		engine.changespritesect(SpriteNum, sectnum);
		engine.getzsofslope(sp.sectnum, sp.x, sp.y, zofslope);

		u.hiz = zofslope[CEIL];
		u.loz = zofslope[FLOOR];

		// spawn explosion
		explosion = (short) SpawnSectorExp(SpriteNum);

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.xrepeat += (RANDOM_P2(32 << 8) >> 8) - 16;
		exp.yrepeat += (RANDOM_P2(32 << 8) >> 8) - 16;
		eu.xchange = MOVEx(92, exp.ang);
		eu.ychange = MOVEy(92, exp.ang);
	}

	public static void DoExplodeSector(int match) {
		short orig_ang;
		int zh;
		short cf, nextcf;

		SPRITE esp;
		SECTOR sectp;

		orig_ang = 0;// sp.ang;

		for (cf = headspritestat[STAT_EXPLODING_CEIL_FLOOR]; cf != -1; cf = nextcf) {
			nextcf = nextspritestat[cf];
			esp = sprite[cf];

			if (match != esp.lotag)
				continue;

			if (pUser[cf] == null)
				SpawnUser(cf, 0, null);

			sectp = sector[esp.sectnum];

			sectp.ceilingz -= Z(SP_TAG4(esp));

			if (SP_TAG5(esp) != 0) {
				sectp.floorheinum = (short) SP_TAG5(esp);
				sectp.floorstat |= (FLOOR_STAT_SLOPE);
			}

			if (SP_TAG6(esp) != 0) {
				sectp.ceilingheinum = (short) SP_TAG6(esp);
				sectp.ceilingstat |= (CEILING_STAT_SLOPE);
			}

			for (zh = sectp.ceilingz; zh < sectp.floorz; zh += Z(60)) {
				SectorExp(cf, esp.sectnum, orig_ang, zh + Z(RANDOM_P2(64)) - Z(32));
			}

			// don't need it any more
			KillSprite(cf);
		}
	}

	public static final Animator DoSpawnSpot = new Animator() {
		@Override
		public boolean invoke(int spr) {
			DoSpawnSpot(spr);
			return false;
		}
	};

	public static void DoSpawnSpot(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if ((u.WaitTics -= synctics) < 0) {
			change_sprite_stat(SpriteNum, STAT_SPAWN_SPOT);
			SpawnShrap(SpriteNum, -1);

			if (u.LastDamage == 1) {
				KillSprite(SpriteNum);
			}
		}
	}

	// spawns shrap when killing an object
	public static void DoSpawnSpotsForKill(int match) {
		short sn, next_sn;
		SPRITE sp;
		USER u;

		if (match < 0)
			return;

		for (sn = headspritestat[STAT_SPAWN_SPOT]; sn != -1; sn = next_sn) {
			next_sn = nextspritestat[sn];
			sp = sprite[sn];

			// change the stat num and set the delay correctly to call SpawnShrap
			if (sp.hitag == SPAWN_SPOT && sp.lotag == match) {
				u = pUser[sn];
				change_sprite_stat(sn, STAT_NO_STATE);
				u.ActorActionFunc = DoSpawnSpot;
				u.WaitTics = (short) (SP_TAG5(sp) * 15);
				engine.setspritez(sn, sp.x, sp.y, sp.z);
				// setting for Killed
				u.LastDamage = 1;
			}
		}
	}

	// spawns shrap when damaging an object
	public static void DoSpawnSpotsForDamage(int match) {
		short sn, next_sn;
		SPRITE sp;
		USER u;

		if (match < 0)
			return;

		for (sn = headspritestat[STAT_SPAWN_SPOT]; sn != -1; sn = next_sn) {
			next_sn = nextspritestat[sn];
			sp = sprite[sn];

			// change the stat num and set the delay correctly to call SpawnShrap

			if (sp.hitag == SPAWN_SPOT && sp.lotag == match) {
				u = pUser[sn];
				change_sprite_stat(sn, STAT_NO_STATE);
				u.ActorActionFunc = DoSpawnSpot;
				u.WaitTics = (short) (SP_TAG7(sp) * 15);
				// setting for Damaged
				u.LastDamage = 0;
			}
		}
	}

	private static short[] snd = new short[3];

	public static void DoSoundSpotMatch(int match, int sound_num, SoundType sound_type) {
		short sn, next_sn;
		SPRITE sp;
		int flags;
		short snd2play;

		// sound_type is not used

		sound_num--;

		for (sn = headspritestat[STAT_SOUND_SPOT]; sn != -1; sn = next_sn) {
			next_sn = nextspritestat[sn];
			sp = sprite[sn];

			if (SP_TAG2(sp) == match && !TEST_BOOL6(sp)) {

				snd[0] = (short) SP_TAG13(sp); // tag4 is copied to tag13
				snd[1] = (short) SP_TAG5(sp);
				snd[2] = (short) SP_TAG6(sp);

				snd2play = 0;
				flags = 0;

				if (TEST_BOOL2(sp))
					flags = v3df_follow | v3df_nolookup | v3df_init;
				// flags = v3df_follow|v3df_ambient|v3df_nolookup;

				// play once and only once
				if (TEST_BOOL1(sp))
					SET_BOOL6(sp);

				// don't pan
				if (TEST_BOOL4(sp))
					flags |= v3df_dontpan;
				// add doppler
				if (TEST_BOOL5(sp))
					flags |= v3df_doppler;
				// random
				if (TEST_BOOL3(sp)) {
					if (snd[0] != 0 && snd[1] != 0) {
						snd2play = snd[RANDOM_RANGE(2)];
					} else if (snd[0] != 0 && snd[1] != 0 && snd[2] != 0) {
						snd2play = snd[RANDOM_RANGE(3)];
					}
				} else if (snd[sound_num] != 0) {
					snd2play = snd[sound_num];
				}

				if (snd2play <= 0)
					continue;

				if (TEST_BOOL7(sp)) {
					PlayerStr pp = GlobPlayerStr;

					if (pp != null) {
						if (pp == Player[myconnectindex])
							PlayerSound(snd2play, v3df_dontpan | v3df_follow, pp);
					}
				} else {

					// if (TEST(flags, v3df_follow)) // Just set it anyway
					Set3DSoundOwner(sn, PlaySound(snd2play, sp, flags));
				}
			}
		}
	}

	public static void DoSoundSpotStopSound(int match) {
		short sn, next_sn;
		SPRITE sp;

		for (sn = headspritestat[STAT_SOUND_SPOT]; sn != -1; sn = next_sn) {
			next_sn = nextspritestat[sn];
			sp = sprite[sn];

			// found match and is a follow type
			if (SP_TAG2(sp) == match && TEST_BOOL2(sp)) {
				DeleteNoSoundOwner(sn);
			}
		}
	}

	public static void DoStopSoundSpotMatch(int match) {
		short sn, next_sn;
		SPRITE sp;

		for (sn = headspritestat[STAT_STOP_SOUND_SPOT]; sn != -1; sn = next_sn) {
			next_sn = nextspritestat[sn];
			sp = sprite[sn];

			if (SP_TAG2(sp) == match) {
				DoSoundSpotStopSound(SP_TAG5(sp));
			}
		}
	}

	public static boolean TestKillSectorObject(Sector_Object sop) {
		if (TEST(sop.flags, SOBJ_KILLABLE)) {
			KillMatchingCrackSprites(sop.match_event);
			// get new sectnums
			CollapseSectorObject(sop, sop.xmid, sop.ymid);
			DoSpawnSpotsForKill(sop.match_event);
			KillSectorObjectSprites(sop);
			return (true);
		}

		return (false);
	}

	public static boolean DoSectorObjectKillMatch(int match) {
		Sector_Object sop;

		for (int s = 0; s < MAX_SECTOR_OBJECTS; s++) {
			sop = SectorObject[s];

			if (sop.xmid == MAXLONG)
				continue;

			if (sop.match_event == match)
				return (TestKillSectorObject(sop));
		}

		return (false);
	}

	public static boolean SearchExplodeSectorMatch(int match) {
		short i, nexti;

		// THIS IS ONLY CALLED FROM DoMatchEverything
		for (i = headspritestat[STAT_SPRITE_HIT_MATCH]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			SPRITE sp = sprite[i];

			if (sp.hitag == match) {
				KillMatchingCrackSprites(match);
				DoExplodeSector(match);
				return (true);
			}
		}

		return (false);
	}

	public static void KillMatchingCrackSprites(int match) {
		short i, nexti;
		SPRITE sp;

		for (i = headspritestat[STAT_SPRITE_HIT_MATCH]; i != -1; i = nexti) {
			nexti = nextspritestat[i];

			sp = sprite[i];

			if (sp.hitag == match) {
				if (TEST(SP_TAG8(sp), BIT(2)))
					continue;

				KillSprite(i);
			}
		}
	}

	public static void WeaponExplodeSectorInRange(int weapon) {
		short i, nexti;
		SPRITE wp = sprite[weapon];
		USER wu = pUser[weapon];
		SPRITE sp;
		int dist;
		int radius;

		for (i = headspritestat[STAT_SPRITE_HIT_MATCH]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			// test to see if explosion is close to crack sprite
			dist = FindDistance3D(wp.x - sp.x, wp.y - sp.y, (wp.z - sp.z) >> 4);

			if (sp.clipdist == 0)
				continue;

			radius = ((sp.clipdist) << 2) * 8;

			if (dist > (wu.Radius / 2) + radius)
				continue;

			if (!FAFcansee(wp.x, wp.y, wp.z, wp.sectnum, sp.x, sp.y, sp.z, sp.sectnum))
				continue;

			// this and every other crack sprite of this type is now dead
			// don't use them

			// pass in explosion type
			MissileHitMatch(weapon, WPN_ROCKET, i);

			return;
		}
	}

	public static void ShootableSwitch(int SpriteNum, int Weapon) {
		SPRITE sp = sprite[SpriteNum];

		switch (sp.picnum) {
		case SWITCH_SHOOTABLE_1:
			OperateSprite(SpriteNum, false);
			sp.picnum = SWITCH_SHOOTABLE_1 + 1;
			break;
		case SWITCH_FUSE:
		case SWITCH_FUSE + 1:
			sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
			OperateSprite(SpriteNum, false);
			sp.picnum = SWITCH_FUSE + 2;
			break;
		}
	}

	private static final short StatList[] = { STAT_DEFAULT, STAT_VATOR, STAT_SPIKE, STAT_TRAP, STAT_ITEM, STAT_LIGHTING,
			STAT_STATIC_FIRE, STAT_AMBIENT, STAT_FAF };

	public static void DoDeleteSpriteMatch(int match) {
		int del_x = 0, del_y = 0;
		short i, nexti;
		short stat;
		short found;

		while (true) {
			found = -1;

			// search for a DELETE_SPRITE with same match tag
			for (i = headspritestat[STAT_DELETE_SPRITE]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				if (sprite[i].lotag == match) {
					found = i;
					del_x = sprite[i].x;
					del_y = sprite[i].y;
					break;
				}
			}

			if (found == -1)
				return;

			for (stat = 0; stat < StatList.length; stat++) {
				for (i = headspritestat[StatList[stat]]; i != -1; i = nexti) {
					nexti = nextspritestat[i];
					if (del_x == sprite[i].x && del_y == sprite[i].y) {
						// special case lighting delete of Fade On/off after fades
						if (StatList[stat] == STAT_LIGHTING) {
							// set shade to darkest and then kill it
							sprite[i].shade = (byte) SPRITE_TAG6(i);
							sprite[i].pal = 0;
							SectorLightShade(i, sprite[i].shade);
							DiffuseLighting(sprite[i]);
						}

						SpriteQueueDelete(i);
						KillSprite(i);
					}
				}
			}

			// kill the DELETE_SPRITE
			KillSprite(found);
		}
	}

	public static void DoChangorMatch(int match) {
		short sn, next_sn;
		SPRITE sp;
		SECTOR sectp;

		for (sn = headspritestat[STAT_CHANGOR]; sn != -1; sn = next_sn) {
			next_sn = nextspritestat[sn];

			sp = sprite[sn];
			sectp = sector[sp.sectnum];

			if (SP_TAG2(sp) != match)
				continue;

			if (TEST_BOOL1(sp)) {
				sectp.ceilingpicnum = (short) SP_TAG4(sp);
				sectp.ceilingz += Z(SP_TAG5(sp));
				sectp.ceilingheinum += SP_TAG6(sp);

				if (sectp.ceilingheinum != 0)
					sectp.ceilingstat |= (CEILING_STAT_SLOPE);
				else
					sectp.ceilingstat &= ~(CEILING_STAT_SLOPE);

				sectp.ceilingshade += SP_TAG7(sp);
				sectp.ceilingpal += SP_TAG8(sp);
			} else {
				sectp.floorpicnum = (short) SP_TAG4(sp);
				sectp.floorz += Z(SP_TAG5(sp));
				sectp.floorheinum += SP_TAG6(sp);

				if (sectp.floorheinum != 0)
					sectp.floorstat |= (FLOOR_STAT_SLOPE);
				else
					sectp.floorstat &= ~(FLOOR_STAT_SLOPE);

				sectp.floorshade += SP_TAG7(sp);
				sectp.floorpal += SP_TAG8(sp);
			}

			sectp.visibility += SP_TAG9(sp);

			// if not set then go ahead and kill it
			if (!TEST_BOOL2(sp)) {
				KillSprite(sn);
			}
		}
	}

	public static void DoMatchEverything(PlayerStr pp, int match, int state) {
		PlayerStr bak;

		bak = GlobPlayerStr;
		GlobPlayerStr = pp;
		// CAREFUL! pp == NULL is a valid case for this routine
		DoStopSoundSpotMatch(match);
		DoSoundSpotMatch(match, 1, SoundType.SOUND_EVERYTHING_TYPE);
		GlobPlayerStr = bak;

		DoLightingMatch(match, state);

		DoQuakeMatch(match);

		// make sure all vators are inactive before allowing
		// to repress switch
		if (!TestVatorMatchActive(match))
			DoVatorMatch(pp, match);

		if (!TestSpikeMatchActive(match))
			DoSpikeMatch(pp, match);

		if (!TestRotatorMatchActive(match))
			DoRotatorMatch(pp, match, false);

		if (!TestSlidorMatchActive(match))
			DoSlidorMatch(pp, match, false);

		DoSectorObjectKillMatch(match);
		DoSectorObjectSetScale(match);

		DoSOevent(match, state);
		DoSpawnActorTrigger(match);

		// this may or may not find an exploding sector
		SearchExplodeSectorMatch(match);

		CopySectorMatch(match);
		DoWallMoveMatch(match);
		DoSpawnSpotsForKill(match);

		DoTrapReset(match);
		DoTrapMatch(match);

		SpawnItemsMatch(match);
		DoChangorMatch(match);
		DoDeleteSpriteMatch(match);
	}

	public static boolean ComboSwitchTest(int combo_type, int match) {
		short i, nexti;
		SPRITE sp;
		short state;

		for (i = headspritestat[STAT_DEFAULT]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (sp.lotag == combo_type && sp.hitag == match) {
				// dont toggle - get the current state
				state = AnimateSwitch(sp, 999);

				// if any one is not set correctly then switch is not set
				if (state != SP_TAG3(sp)) {
					return (false);
				}
			}
		}

		return (true);
	}

	// NOTE: switches are always wall sprites
	public static boolean OperateSprite(int SpriteNum, boolean player_is_operating) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		PlayerStr pp = null;
		short state;
		short key_num = 0;

		if (Prediction)
			return (false);

		if (sp.picnum == ST1)
			return (false);

		if (player_is_operating) {
			pp = GlobPlayerStr;

			if (!FAFcansee(pp.posx, pp.posy, pp.posz, pp.cursectnum, sp.x, sp.y, sp.z - DIV2(SPRITEp_SIZE_Z(sp)),
					sp.sectnum))
				return (false);
		}

		switch (sp.lotag) {
		case TOILETGIRL_R0:
		case WASHGIRL_R0:
		case CARGIRL_R0:
		case MECHANICGIRL_R0:
		case SAILORGIRL_R0:
		case PRUNEGIRL_R0: {
			int choose_snd;

			u.FlagOwner = 1;
			u.WaitTics = (short) SEC(4);

			if (pp != Player[myconnectindex])
				return (true);

			choose_snd = STD_RANDOM_RANGE(1000);
			if (sp.lotag == CARGIRL_R0) {
				if (choose_snd > 700)
					PlayerSound(DIGI_JG44052, v3df_dontpan | v3df_follow, pp);
				else if (choose_snd > 500)
					PlayerSound(DIGI_JG45014, v3df_dontpan | v3df_follow, pp);
				else if (choose_snd > 250)
					PlayerSound(DIGI_JG44068, v3df_dontpan | v3df_follow, pp);
				else
					PlayerSound(DIGI_JG45010, v3df_dontpan | v3df_follow, pp);
			} else if (sp.lotag == MECHANICGIRL_R0) {
				if (choose_snd > 700)
					PlayerSound(DIGI_JG44027, v3df_dontpan | v3df_follow, pp);
				else if (choose_snd > 500)
					PlayerSound(DIGI_JG44038, v3df_dontpan | v3df_follow, pp);
				else if (choose_snd > 250)
					PlayerSound(DIGI_JG44039, v3df_dontpan | v3df_follow, pp);
				else
					PlayerSound(DIGI_JG44048, v3df_dontpan | v3df_follow, pp);
			} else if (sp.lotag == SAILORGIRL_R0) {
				if (choose_snd > 700)
					PlayerSound(DIGI_JG45018, v3df_dontpan | v3df_follow, pp);
				else if (choose_snd > 500)
					PlayerSound(DIGI_JG45030, v3df_dontpan | v3df_follow, pp);
				else if (choose_snd > 250)
					PlayerSound(DIGI_JG45033, v3df_dontpan | v3df_follow, pp);
				else
					PlayerSound(DIGI_JG45043, v3df_dontpan | v3df_follow, pp);
			} else if (sp.lotag == PRUNEGIRL_R0) {
				if (choose_snd > 700)
					PlayerSound(DIGI_JG45053, v3df_dontpan | v3df_follow, pp);
				else if (choose_snd > 500)
					PlayerSound(DIGI_JG45067, v3df_dontpan | v3df_follow, pp);
				else if (choose_snd > 250)
					PlayerSound(DIGI_JG46005, v3df_dontpan | v3df_follow, pp);
				else
					PlayerSound(DIGI_JG46010, v3df_dontpan | v3df_follow, pp);
			} else if (sp.lotag == TOILETGIRL_R0) {
				if (choose_snd > 700)
					PlayerSound(DIGI_WHATYOUEATBABY, v3df_dontpan | v3df_follow, pp);
				else if (choose_snd > 500)
					PlayerSound(DIGI_WHATDIEDUPTHERE, v3df_dontpan | v3df_follow, pp);
				else if (choose_snd > 250)
					PlayerSound(DIGI_YOUGOPOOPOO, v3df_dontpan | v3df_follow, pp);
				else
					PlayerSound(DIGI_PULLMYFINGER, v3df_dontpan | v3df_follow, pp);
			} else {
				if (choose_snd > 700)
					PlayerSound(DIGI_SOAPYOUGOOD, v3df_dontpan | v3df_follow, pp);
				else if (choose_snd > 500)
					PlayerSound(DIGI_WASHWANG, v3df_dontpan | v3df_follow, pp);
				else if (choose_snd > 250)
					PlayerSound(DIGI_DROPSOAP, v3df_dontpan | v3df_follow, pp);
				else
					PlayerSound(DIGI_REALTITS, v3df_dontpan | v3df_follow, pp);
			}
		}
			return (true);

		case PACHINKO1:
			// Don't mess with it if it's already going
			if (u.WaitTics > 0)
				return (true);

			PlaySound(DIGI_PFLIP, sp, v3df_none);
			u.WaitTics = (short) (SEC(3) + SEC(RANDOM_RANGE(10)));
			ChangeState(SpriteNum, s_Pachinko1Operate[0]);
			return (true);

		case PACHINKO2:

			// Don't mess with it if it's already going
			if (u.WaitTics > 0)
				return (true);

			PlaySound(DIGI_PFLIP, sp, v3df_none);
			u.WaitTics = (short) (SEC(3) + SEC(RANDOM_RANGE(10)));
			ChangeState(SpriteNum, s_Pachinko2Operate[0]);
			return (true);

		case PACHINKO3:

			// Don't mess with it if it's already going
			if (u.WaitTics > 0)
				return (true);

			PlaySound(DIGI_PFLIP, sp, v3df_none);
			u.WaitTics = (short) (SEC(3) + SEC(RANDOM_RANGE(10)));
			ChangeState(SpriteNum, s_Pachinko3Operate[0]);
			return (true);

		case PACHINKO4:

			// Don't mess with it if it's already going
			if (u.WaitTics > 0)
				return (true);

			PlaySound(DIGI_PFLIP, sp, v3df_none);
			u.WaitTics = (short) (SEC(3) + SEC(RANDOM_RANGE(10)));
			ChangeState(SpriteNum, s_Pachinko4Operate[0]);
			return (true);

		case SWITCH_LOCKED:
			key_num = sp.hitag;
			if (pp.HasKey[key_num - 1] != 0) {
				int i;
				for (i = 0; i < numsectors; i++) {
					if (SectUser[i] != null && SectUser[i].stag == SECT_LOCK_DOOR && SectUser[i].number == key_num)
						SectUser[i].number = 0; // unlock all doors of this type
				}
				UnlockKeyLock(key_num, SpriteNum);
			}
			return (true);

		case TAG_COMBO_SWITCH_EVERYTHING:

			// change the switch state
			AnimateSwitch(sp, -1);
			PlaySound(DIGI_REGULARSWITCH, sp, v3df_none);

			if (ComboSwitchTest(TAG_COMBO_SWITCH_EVERYTHING, sp.hitag)) {
				DoMatchEverything(pp, sp.hitag, ON);
			}
			return (true);

		case TAG_COMBO_SWITCH_EVERYTHING_ONCE:

			// change the switch state
			AnimateSwitch(sp, -1);
			PlaySound(DIGI_REGULARSWITCH, sp, v3df_none);

			if (ComboSwitchTest(TAG_COMBO_SWITCH_EVERYTHING, sp.hitag)) {
				DoMatchEverything(pp, sp.hitag, ON);
			}

			sp.lotag = 0;
			sp.hitag = 0;
			return (true);

		case TAG_SWITCH_EVERYTHING:
			state = AnimateSwitch(sp, -1);
			DoMatchEverything(pp, sp.hitag, state);
			return (true);

		case TAG_SWITCH_EVERYTHING_ONCE:
			state = AnimateSwitch(sp, -1);
			DoMatchEverything(pp, sp.hitag, state);
			sp.lotag = 0;
			sp.hitag = 0;
			return (true);

		case TAG_LIGHT_SWITCH:

			state = AnimateSwitch(sp, -1);
			DoLightingMatch(sp.hitag, state);
			return (true);

		case TAG_SPRITE_SWITCH_VATOR: {
			// make sure all vators are inactive before allowing
			// to repress switch
			if (!TestVatorMatchActive(sp.hitag))
				DoVatorMatch(pp, sp.hitag);

			if (!TestSpikeMatchActive(sp.hitag))
				DoSpikeMatch(pp, sp.hitag);

			if (!TestRotatorMatchActive(sp.hitag))
				DoRotatorMatch(pp, sp.hitag, false);

			if (!TestSlidorMatchActive(sp.hitag))
				DoSlidorMatch(pp, sp.hitag, false);
			return (true);
		}

		case TAG_LEVEL_EXIT_SWITCH: {

			AnimateSwitch(sp, -1);

			PlaySound(DIGI_BIGSWITCH, sp, v3df_none);

			if (sp.hitag != 0)
				Level = sp.hitag;
			else
				Level++;
			ExitLevel = true;
			FinishedLevel = true;
			return (true);
		}

		case TAG_SPRITE_GRATING: {
			change_sprite_stat(SpriteNum, STAT_NO_STATE);

			u = SpawnUser(SpriteNum, 0, null);

			u.ActorActionFunc = DoGrating;

			sp.lotag = 0;
			sp.hitag /= 2;
			return (true);
		}

		case TAG_SO_SCALE_SWITCH:
			AnimateSwitch(sp, -1);
			DoSectorObjectSetScale(sp.hitag);
			return (true);

		case TAG_SO_SCALE_ONCE_SWITCH:
			AnimateSwitch(sp, -1);
			DoSectorObjectSetScale(sp.hitag);
			sp.lotag = 0;
			sp.hitag = 0;
			return (true);

		case TAG_SO_EVENT_SWITCH: {
			state = AnimateSwitch(sp, -1);

			DoMatchEverything(null, sp.hitag, state);

			sp.hitag = 0;
			sp.lotag = 0;

			PlaySound(DIGI_REGULARSWITCH, sp, v3df_none);
			break;
		}

		case TAG_ROTATE_SO_SWITCH: {
			short so_num;
			Sector_Object sop;

			so_num = sp.hitag;

			AnimateSwitch(sp, -1);

			sop = SectorObject[so_num];

			sop.ang_tgt = (short) NORM_ANGLE(sop.ang_tgt + 512);

			PlaySound(DIGI_BIGSWITCH, sp, v3df_none);

			return (true);
		}
		}

		return (false);
	}

	public static int DoTrapReset(int match) {
		short i, nexti;
		SPRITE sp;
		USER u;

		for (i = headspritestat[STAT_TRAP]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];
			u = pUser[i];

			if (sp.lotag != match)
				continue;

			// if correct type and matches
			if (sp.hitag == FIREBALL_TRAP)
				u.WaitTics = 0;

			// if correct type and matches
			if (sp.hitag == BOLT_TRAP)
				u.WaitTics = 0;

			// if correct type and matches
			if (sp.hitag == SPEAR_TRAP)
				u.WaitTics = 0;
		}
		return (0);
	}

	public static int DoTrapMatch(int match) {
		short i, nexti;
		SPRITE sp;
		USER u;

		// may need to be reset to fire immediately

		for (i = headspritestat[STAT_TRAP]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];
			u = pUser[i];

			if (sp.lotag != match)
				continue;

			// if correct type and matches
			if (sp.hitag == FIREBALL_TRAP) {
				u.WaitTics -= synctics;

				if (u.WaitTics <= 0) {
					u.WaitTics = 1 * 120;
					InitFireballTrap(i);
				}
			}

			// if correct type and matches
			if (sp.hitag == BOLT_TRAP) {
				u.WaitTics -= synctics;

				if (u.WaitTics <= 0) {
					u.WaitTics = 1 * 120;
					InitBoltTrap(i);
				}
			}

			// if correct type and matches
			if (sp.hitag == SPEAR_TRAP) {
				u.WaitTics -= synctics;

				if (u.WaitTics <= 0) {
					u.WaitTics = 1 * 120;
					InitSpearTrap(i);
				}
			}
		}
		return (0);
	}

	public static void OperateTripTrigger(PlayerStr pp) {
		SECTOR sectp = sector[pp.cursectnum];

		if (Prediction)
			return;

		// old method
		switch (LOW_TAG(pp.cursectnum)) {
		// same tag for sector as for switch
		case TAG_LEVEL_EXIT_SWITCH: {
			if (sectp.hitag != 0)
				Level = sectp.hitag;
			else
				Level++;
			ExitLevel = true;
			FinishedLevel = true;
			break;
		}

		case TAG_SECRET_AREA_TRIGGER:
			if (pp == Player[myconnectindex])
				PlayerSound(DIGI_ANCIENTSECRET, v3df_dontpan | v3df_doppler | v3df_follow, pp);

			PutStringInfo(pp, "You found a secret area!");
			// always give to the first player
			Player[0].SecretsFound++;
			sectp.lotag = 0;
			sectp.hitag = 0;
			break;

		case TAG_TRIGGER_EVERYTHING:
			DoMatchEverything(pp, sectp.hitag, -1);
			break;

		case TAG_TRIGGER_EVERYTHING_ONCE:
			DoMatchEverything(pp, sectp.hitag, -1);
			sectp.lotag = 0;
			sectp.hitag = 0;
			break;

		case TAG_SECTOR_TRIGGER_VATOR:
			if (!TestVatorMatchActive(sectp.hitag))
				DoVatorMatch(pp, sectp.hitag);
			if (!TestSpikeMatchActive(sectp.hitag))
				DoSpikeMatch(pp, sectp.hitag);
			if (!TestRotatorMatchActive(sectp.hitag))
				DoRotatorMatch(pp, sectp.hitag, false);
			if (!TestSlidorMatchActive(sectp.hitag))
				DoSlidorMatch(pp, sectp.hitag, false);
			break;

		case TAG_LIGHT_TRIGGER:
			DoLightingMatch(sectp.hitag, -1);
			break;

		case TAG_SO_SCALE_TRIGGER:
			DoSectorObjectSetScale(sectp.hitag);
			break;

		case TAG_SO_SCALE_ONCE_TRIGGER:
			DoSectorObjectSetScale(sectp.hitag);
			sectp.lotag = 0;
			sectp.hitag = 0;
			break;

		case TAG_TRIGGER_ACTORS: {
			int dist;
			short i, nexti;
			SPRITE sp;
			USER u;

			dist = sectp.hitag;

			for (i = headspritestat[STAT_ENEMY]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				sp = sprite[i];
				u = pUser[i];

				if (TEST(u.Flags, SPR_WAIT_FOR_TRIGGER)) {
					if (Distance(sp.x, sp.y, pp.posx, pp.posy) < dist) {
						u.tgt_sp = pp.PlayerSprite;
						u.Flags &= ~(SPR_WAIT_FOR_TRIGGER);
					}
				}
			}

			break;
		}

		case TAG_TRIGGER_MISSILE_TRAP: {
			// reset traps so they fire immediately
			DoTrapReset(sector[pp.cursectnum].hitag);
			break;
		}

		case TAG_TRIGGER_EXPLODING_SECTOR: {
			DoMatchEverything(null, sector[pp.cursectnum].hitag, -1);
			break;
		}

		case TAG_SPAWN_ACTOR_TRIGGER: {
			DoMatchEverything(null, sector[pp.cursectnum].hitag, -1);

			sector[pp.cursectnum].hitag = 0;
			sector[pp.cursectnum].lotag = 0;
			break;
		}

		case TAG_SO_EVENT_TRIGGER: {
			DoMatchEverything(null, sector[pp.cursectnum].hitag, -1);

			sector[pp.cursectnum].hitag = 0;
			sector[pp.cursectnum].lotag = 0;

			PlaySound(DIGI_REGULARSWITCH, pp, v3df_none);
			break;
		}
		}
	}

	public static void OperateContinuousTrigger(PlayerStr pp) {
		if (Prediction)
			return;

		switch (LOW_TAG(pp.cursectnum)) {
		case TAG_TRIGGER_MISSILE_TRAP: {

			DoTrapMatch(sector[pp.cursectnum].hitag);

			short i, nexti;
			SPRITE sp;
			USER u;

			for (i = headspritestat[STAT_TRAP]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				sp = sprite[i];
				u = pUser[i];

				// if correct type and matches
				if (sp.hitag == FIREBALL_TRAP && sp.lotag == sector[pp.cursectnum].hitag) {
					u.WaitTics -= synctics;

					if (u.WaitTics <= 0) {
						u.WaitTics = 1 * 120;
						InitFireballTrap(i);
					}
				}

				// if correct type and matches
				if (sp.hitag == BOLT_TRAP && sp.lotag == sector[pp.cursectnum].hitag) {
					u.WaitTics -= synctics;

					if (u.WaitTics <= 0) {
						u.WaitTics = 1 * 120;
						InitBoltTrap(i);
					}
				}

				// if correct type and matches
				if (sp.hitag == SPEAR_TRAP && sp.lotag == sector[pp.cursectnum].hitag) {
					u.WaitTics -= synctics;

					if (u.WaitTics <= 0) {
						u.WaitTics = 1 * 120;
						InitSpearTrap(i);
					}
				}
			}

			break;
		}
		}
	}

	public static short PlayerTakeSectorDamage(PlayerStr pp) {
		Sect_User sectu = SectUser[pp.cursectnum];
		USER u = pUser[pp.PlayerSprite];

		// the calling routine must make sure sectu exists
		if ((u.DamageTics -= synctics) < 0) {
			u.DamageTics = DAMAGE_TIME;

			PlayerUpdateHealth(pp, -sectu.damage);
			PlayerCheckDeath(pp, -1);
		}
		return (0);
	}

	// Needed in order to see if Player should grunt if he can't find a wall to
	// operate on
	// If player is too far away, don't grunt
	public static final int PLAYER_SOUNDEVENT_TAG = 900;

	public static boolean NearThings(PlayerStr pp) {
		// Check player's current sector for triggered sound
		if (sector[pp.cursectnum].hitag == PLAYER_SOUNDEVENT_TAG) {
			if (pp == Player[myconnectindex])
				PlayerSound(sector[pp.cursectnum].lotag, v3df_follow | v3df_dontpan, pp);
			return (false);
		}

		engine.neartag(pp.posx, pp.posy, pp.posz, pp.cursectnum, pp.getAnglei(), neartag, 1024, NTAG_SEARCH_LO_HI);

		// hit a sprite? Check to see if it has sound info in it!
		// This can work with any sprite!
		if (neartag.tagsprite >= 0) {
			SPRITE sp = sprite[neartag.tagsprite];

			// Go through list of cases
			if (sp.hitag == PLAYER_SOUNDEVENT_TAG) {
				if (pp == Player[myconnectindex])
					PlayerSound(sp.lotag, v3df_follow | v3df_dontpan, pp);
			}
			return (false); // Return false so he doesn't grunt
		}

		if (neartag.tagwall >= 0) {
			// Check player's current sector for triggered sound
			if (wall[neartag.tagwall].hitag == PLAYER_SOUNDEVENT_TAG) {
				if (pp == Player[myconnectindex])
					PlayerSound(wall[neartag.tagwall].lotag, v3df_follow | v3df_dontpan, pp);
				return (false); // We are playing a sound so don't return true
			}
			return (true);
		}
		// This only gets called if nothing else worked, check for nearness to a wall
		{
			int dang = pp.getAnglei();

			FAFhitscan(pp.posx, pp.posy, pp.posz - Z(30), pp.cursectnum, // Start position
					sintable[NORM_ANGLE(dang + 512)], // X vector of 3D ang
					sintable[NORM_ANGLE(dang)], // Y vector of 3D ang
					0, pHitInfo, CLIPMASK_MISSILE); // Z vector of 3D ang

			if (pHitInfo.hitsect < 0)
				return (false);

			if (Distance(pHitInfo.hitx, pHitInfo.hity, pp.posx, pp.posy) > 1024) // was 1500, GDX 19.05.2020
				return (false);

			// hit a sprite?
			if (pHitInfo.hitsprite >= 0)
				return (false);

			if (neartag.tagsector >= 0)
				return (true);

			if (pHitInfo.hitwall >= 0) {
				WALL wp = wall[pHitInfo.hitwall];

				// Near a plain old vanilla wall. Can't do anything but grunt.
				if (!TEST(wp.extra, WALLFX_DONT_STICK) && pp == Player[myconnectindex]) {
					if (STD_RANDOM_RANGE(1000) > 970)
						PlayerSound(DIGI_HITTINGWALLS, v3df_follow | v3df_dontpan, pp);
					else
						PlayerSound(DIGI_SEARCHWALL, v3df_follow | v3df_dontpan, pp);
				}

				return (true);
			}

			return (false);
		}
	}

	private static short nti_cnt;

	public static void NearTagList(NEAR_TAG_INFO[] ntip, int ntipnum, PlayerStr pp, int z, int dist, int type,
			int count) {
		short save_lotag, save_hitag;
		short neartagsector, neartagwall, neartagsprite;
		int neartaghitdist;

		engine.neartag(pp.posx, pp.posy, z, pp.cursectnum, pp.getAnglei(), neartag, dist, type);

		neartagsector = neartag.tagsector;
		neartaghitdist = neartag.taghitdist;
		neartagwall = neartag.tagwall;
		neartagsprite = neartag.tagsprite;

		if (neartagsector >= 0) {
			// save off values
			save_lotag = sector[neartagsector].lotag;
			save_hitag = sector[neartagsector].hitag;

			ntip[ntipnum].dist = neartaghitdist;
			ntip[ntipnum].sectnum = neartagsector;
			ntip[ntipnum].wallnum = -1;
			ntip[ntipnum].spritenum = -1;
			nti_cnt++;
			ntipnum++;

			if (nti_cnt >= count)
				return;

			// remove them
			sector[neartagsector].lotag = 0;
			sector[neartagsector].hitag = 0;

			NearTagList(ntip, ntipnum, pp, z, dist, type, count);

			// reset off values
			sector[neartagsector].lotag = save_lotag;
			sector[neartagsector].hitag = save_hitag;
		} else if (neartagwall >= 0) {
			// save off values
			save_lotag = wall[neartagwall].lotag;
			save_hitag = wall[neartagwall].hitag;

			ntip[ntipnum].dist = neartaghitdist;
			ntip[ntipnum].sectnum = -1;
			ntip[ntipnum].wallnum = neartagwall;
			ntip[ntipnum].spritenum = -1;
			nti_cnt++;
			ntipnum++;

			if (nti_cnt >= count)
				return;

			// remove them
			wall[neartagwall].lotag = 0;
			wall[neartagwall].hitag = 0;

			NearTagList(ntip, ntipnum, pp, z, dist, type, count);

			// reset off values
			wall[neartagwall].lotag = save_lotag;
			wall[neartagwall].hitag = save_hitag;
		} else if (neartagsprite >= 0) {
			// save off values
			save_lotag = sprite[neartagsprite].lotag;
			save_hitag = sprite[neartagsprite].hitag;

			ntip[ntipnum].dist = neartaghitdist;
			ntip[ntipnum].sectnum = -1;
			ntip[ntipnum].wallnum = -1;
			ntip[ntipnum].spritenum = neartagsprite;
			nti_cnt++;
			ntipnum++;

			if (nti_cnt >= count)
				return;

			// remove them
			sprite[neartagsprite].lotag = 0;
			sprite[neartagsprite].hitag = 0;

			NearTagList(ntip, ntipnum, pp, z, dist, type, count);

			// reset off values
			sprite[neartagsprite].lotag = save_lotag;
			sprite[neartagsprite].hitag = save_hitag;
		} else {
			ntip[ntipnum].dist = -1;
			ntip[ntipnum].sectnum = -1;
			ntip[ntipnum].wallnum = -1;
			ntip[ntipnum].spritenum = -1;
			nti_cnt++;
			ntipnum++;

			return;
		}
	}

	public static void BuildNearTagList(NEAR_TAG_INFO[] ntip, int size, PlayerStr pp, int z, int dist, int type,
			int count) {
		for (int i = 0; i < size; i++)
			ntip[i].reset();
		nti_cnt = 0;
		NearTagList(ntip, 0, pp, z, dist, type, count);
	}

	public static boolean DoPlayerGrabStar(PlayerStr pp) {
		SPRITE sp = null;
		int i;

		// MUST check exact z's of each star or it will never work
		for (i = 0; i < MAX_STAR_QUEUE; i++) {
			if (StarQueue[i] >= 0) {
				sp = sprite[StarQueue[i]];

				if (FindDistance3D(sp.x - pp.posx, sp.y - pp.posy, (sp.z - pp.posz + Z(12)) >> 4) < 500) {
					break;
				}
			}
		}

		if (i < MAX_STAR_QUEUE) {
			// Pull a star out of wall and up your ammo
			PlayerUpdateAmmo(pp, WPN_STAR, 1);
			PlaySound(DIGI_ITEM, sp, v3df_none);
			KillSprite(StarQueue[i]);
			StarQueue[i] = -1;
			if (TEST(pp.WpnFlags, BIT(WPN_STAR)))
				return (true);
			pp.WpnFlags |= (BIT(WPN_STAR));
			InitWeaponStar(pp);
			return (true);
		}

		return (false);
	}

	private static NEAR_TAG_INFO[] nti = new NEAR_TAG_INFO[16];
	private static int z[] = new int[3];

	public static void PlayerOperateEnv(PlayerStr pp) {
		if (!isValidSector(pp.cursectnum))
			return;

		Sect_User sectu = SectUser[pp.cursectnum];
		SECTOR sectp = sector[pp.cursectnum];
		boolean found;

		if (Prediction)
			return;

		//
		// Switch & door activations
		//

		if (TEST_SYNC_KEY(pp, SK_OPERATE)) {
			if (FLAG_KEY_PRESSED(pp, SK_OPERATE)) {
				// if space bar pressed
				short nt_ndx;

				if (DoPlayerGrabStar(pp)) {
					FLAG_KEY_RELEASE(pp, SK_OPERATE);
				} else {
					NearThings(pp); // Check for player sound specified in a level sprite
				}

				BuildNearTagList(nti, 16, pp, pp.posz, 2048, NTAG_SEARCH_LO_HI, 8);

				found = false;

				// try and find a sprite
				for (nt_ndx = 0; nti[nt_ndx].dist >= 0; nt_ndx++) {
					if (nti[nt_ndx].spritenum >= 0 && nti[nt_ndx].dist < 1024 + 768) {
						if (OperateSprite(nti[nt_ndx].spritenum, true)) {
							FLAG_KEY_RELEASE(pp, SK_OPERATE);
							found = true;
						}
					}
				}

				// if not found look at different z positions
				if (!found) {
					int i;
					SPRITE psp = pp.getSprite();
					z[0] = psp.z - SPRITEp_SIZE_Z(psp) - Z(10);
					z[1] = psp.z;
					z[2] = DIV2(z[0] + z[1]);

					for (i = 0; i < 3; i++) {
						BuildNearTagList(nti, 16, pp, z[i], 1024 + 768, NTAG_SEARCH_LO_HI, 8);

						for (nt_ndx = 0; nti[nt_ndx].dist >= 0; nt_ndx++) {
							if (nti[nt_ndx].spritenum >= 0 && nti[nt_ndx].dist < 1024 + 768) {
								if (OperateSprite(nti[nt_ndx].spritenum, true)) {
									FLAG_KEY_RELEASE(pp, SK_OPERATE);
									break;
								}
							}
						}
					}
				}

				{
					int neartaghitdist;
					short neartagsector, neartagwall;

					neartaghitdist = nti[0].dist;
					neartagsector = nti[0].sectnum;
					neartagwall = nti[0].wallnum;

					if (neartagsector >= 0 && neartaghitdist < 1024) {
						if (OperateSector(neartagsector, true)) {
							// Release the key
							FLAG_KEY_RELEASE(pp, SK_OPERATE);

							// Hack fow wd secret area
							if (pp.cursectnum == 491 && Level == 11 && swGetAddon() == 1
									&& sector[715].lotag == TAG_SECRET_AREA_TRIGGER) {
								if (pp == Player[myconnectindex])
									PlayerSound(DIGI_ANCIENTSECRET, v3df_dontpan | v3df_doppler | v3df_follow, pp);
								PutStringInfo(pp, "You found a secret area!");
								// always give to the first player
								Player[0].SecretsFound++;
								sector[715].lotag = 0;
							}
						}
					}

					if (neartagwall >= 0 && neartaghitdist < 1024) {
						if (OperateWall(neartagwall, true)) {
							FLAG_KEY_RELEASE(pp, SK_OPERATE);
						}
					}
				}

				//
				// Trigger operations
				//

				switch (LOW_TAG(pp.cursectnum)) {
				case TAG_VATOR:
					DoVatorOperate(pp, pp.cursectnum);
					DoSpikeOperate(pp, pp.cursectnum);
					DoRotatorOperate(pp, pp.cursectnum);
					DoSlidorOperate(pp, pp.cursectnum);
					break;
				case TAG_SPRING_BOARD:
					DoSpringBoard(pp, pp.cursectnum);
					FLAG_KEY_RELEASE(pp, SK_OPERATE);
					break;
				case TAG_DOOR_ROTATE:
					if (OperateSector(pp.cursectnum, true))
						FLAG_KEY_RELEASE(pp, SK_OPERATE);
					break;
				}
			}
		} else {
			// Reset the key when syncbit key is not in use
			FLAG_KEY_RESET(pp, SK_OPERATE);
		}

		// ////////////////////////////
		//
		// Sector Damage
		//
		// ////////////////////////////

		if (sectu != null && sectu.damage != 0) {
			if (TEST(sectu.flags, SECTFU_DAMAGE_ABOVE_SECTOR)) {
				PlayerTakeSectorDamage(pp);
			} else if ((SPRITEp_BOS(pp.getSprite()) >= sectp.floorz) && !TEST(pp.Flags, PF_DIVING)) {
				PlayerTakeSectorDamage(pp);
			}
		} else {
			USER u = pUser[pp.PlayerSprite];
			u.DamageTics = 0;
		}

		// ////////////////////////////
		//
		// Trigger stuff
		//
		// ////////////////////////////

		OperateContinuousTrigger(pp);

		// just changed sectors
		if (pp.lastcursectnum != pp.cursectnum) {
			OperateTripTrigger(pp);

			if (TEST(sector[pp.cursectnum].extra, SECTFX_WARP_SECTOR)) {
				if (!TEST(pp.Flags2, PF2_TELEPORTED)) {
					DoPlayerWarpTeleporter(pp);
				}
			}

			pp.Flags2 &= ~(PF2_TELEPORTED);
		}
	}

	public static void DoSineWaveFloor() {

		int newz;
		short wave;

		for (wave = 0; wave < MAX_SINE_WAVE; wave++) {
			for (int swi = 0, flags = SineWaveFloor[wave][0].flags; swi < SineWaveFloor[wave].length
					&& SineWaveFloor[wave][swi].sector >= 0; swi++) {
				SINE_WAVE_FLOOR swf = SineWaveFloor[wave][swi];
				swf.sintable_ndx = (short) NORM_ANGLE(swf.sintable_ndx + (synctics << swf.speed_shift));

				if (TEST(flags, SINE_FLOOR)) {
					newz = swf.floor_origz + ((swf.range * sintable[swf.sintable_ndx]) >> 14);
					game.pInt.setfloorinterpolate(swf.sector, sector[swf.sector]);
					sector[swf.sector].floorz = newz;
				}

				if (TEST(flags, SINE_CEILING)) {
					newz = swf.ceiling_origz + ((swf.range * sintable[swf.sintable_ndx]) >> 14);
					game.pInt.setceilinterpolate(swf.sector, sector[swf.sector]);
					sector[swf.sector].ceilingz = newz;
				}

			}
		}

		/*
		 * SLOPED SIN-WAVE FLOORS:
		 * 
		 * It's best to program sloped sin-wave floors in 2 steps: 1. First set the
		 * floorz of the floor as the sin code normally does it. 2. Adjust the slopes by
		 * calling alignflorslope once for each sector.
		 * 
		 * Note: For this to work, the first wall of each sin-wave sector must be
		 * aligned on the same side of each sector for the entire strip.
		 */

		for (wave = 0; wave < MAX_SINE_WAVE; wave++) {
			for (int swi = 0, flags = SineWaveFloor[wave][0].flags; swi < SineWaveFloor[wave].length
					&& SineWaveFloor[wave][swi].sector >= 0; swi++) {
				SINE_WAVE_FLOOR swf = SineWaveFloor[wave][swi];

				if (!TEST(sector[swf.sector].floorstat, FLOOR_STAT_SLOPE))
					continue;

				if (TEST(flags, SINE_SLOPED)) {
					WALL wal;
					if (sector[swf.sector].wallnum == 4) {
						// Set wal to the wall on the opposite side of the sector
						wal = wall[sector[swf.sector].wallptr + 2];

						// Pass (Sector, x, y, z)
						game.pInt.setfheinuminterpolate(swf.sector, sector[swf.sector]);
						engine.alignflorslope(swf.sector, wal.x, wal.y, sector[wal.nextsector].floorz);
					}
				}
			}
		}
	}

	public static void DoSineWaveWall() {
		int newsp;
		short sw_num;

		for (sw_num = 0; sw_num < MAX_SINE_WAVE; sw_num++) {
			for (int swi = 0; swi < MAX_SINE_WALL_POINTS && SineWall[sw_num][swi].wall >= 0; swi++) {
				SINE_WALL sw = SineWall[sw_num][swi];

				// move through the sintable
				sw.sintable_ndx = (short) NORM_ANGLE(sw.sintable_ndx + (synctics << sw.speed_shift));

				if (sw.type == 0) {
					newsp = sw.orig_xy + ((sw.range * sintable[sw.sintable_ndx]) >> 14);
					// wall[sw.wall].y = new;
					engine.dragpoint(sw.wall, wall[sw.wall].x, newsp);
				} else {
					newsp = sw.orig_xy + ((sw.range * sintable[sw.sintable_ndx]) >> 14);
					// wall[sw.wall].x = new;
					engine.dragpoint(sw.wall, newsp, wall[sw.wall].y);
				}
			}
		}
	}

	public static int getAnimValue(Object obj, AnimType type) {
		int j = 0;

		switch (type) {
		case FloorZ:
			j = ((SECTOR) obj).floorz;
			break;
		case CeilZ:
			j = ((SECTOR) obj).ceilingz;
			break;
		case SpriteZ:
			j = ((SPRITE) obj).z;
			break;
		case SectorObjectZ:
			j = ((Sector_Object) obj).zmid;
			break;
		case UserZ:
			j = ((USER) obj).sz;
			break;
		case SectUserDepth:
			j = ((Sect_User) obj).depth_fract;
			break;
		}

		return j;
	}

	public static void DoAnim(int numtics) {
		for (int i = AnimCnt - 1; i >= 0; i--) {
			Interpolation gInt = game.pInt;
			Anim gAnm = pAnim[i];
			Object obj = gAnm.ptr;
			int animval = getAnimValue(gAnm.ptr, gAnm.type);

			if (animval < gAnm.goal) { // if LESS THAN goal
				// move it
				animval = Math.min(animval + (numtics * PIXZ(gAnm.vel)), gAnm.goal);
			} else { // if GREATER THAN goal
				animval = Math.max(animval - (numtics * PIXZ(gAnm.vel)), gAnm.goal);
			}
			gAnm.vel += gAnm.vel_adj * numtics;

			switch (gAnm.type) {
			case FloorZ:
				gInt.setfloorinterpolate(gAnm.index, (SECTOR) obj);
				((SECTOR) obj).floorz = animval;
				break;
			case CeilZ:
				gInt.setceilinterpolate(gAnm.index, (SECTOR) obj);
				((SECTOR) obj).ceilingz = animval;
				break;
			case SpriteZ:
				gInt.setsprinterpolate(gAnm.index, (SPRITE) obj);
				((SPRITE) obj).z = animval;
				break;
			case SectorObjectZ:
//				System.err.println("DoAnim aaa");
				((Sector_Object) obj).zmid = animval;
				break;
			case UserZ:
				// System.err.println("DoAnim() bbb");
				((USER) obj).sz = animval;
				break;
			case SectUserDepth:
				((Sect_User) obj).depth_fract = (short) animval;
				break;
			}

			// EQUAL this entry has finished
			if (animval == gAnm.goal) {
				AnimCallback acp = gAnm.callback;

				// do a callback when done if not NULL
				if (gAnm.callback != null)
					gAnm.callback.invoke(gAnm, gAnm.callbackdata);

				// only delete it if the callback has not changed
				// Logic here is that if the callback changed then something
				// else must be happening with it - dont delete it
				if (gAnm.callback == acp) {
					// decrement the count
					AnimCnt--;

					// move the last entry to the current one to free the last entry up
					gAnm.copy(pAnim[AnimCnt]);
				}
			}
		}
	}

	public static int AnimGetGoal(Object object, AnimType type) {
		int j = -1;
		for (int i = AnimCnt - 1; i >= 0; i--)
			if (object == pAnim[i].ptr && type == pAnim[i].type) {
				j = i;
				break;
			}
		return (j);
	}

	public static void AnimDelete(Object object, AnimType type) {
		int i, j;

		j = -1;
		for (i = 0; i < AnimCnt; i++) {
			if (object == pAnim[i].ptr && type == pAnim[i].type) {
				j = i;
				break;
			}
		}

		if (j == -1)
			return;

		// decrement the count
		AnimCnt--;

		// move the last entry to the current one to free the last entry up
		pAnim[j].copy(pAnim[AnimCnt]);
	}

	public static void InitAnim() {
		for (int i = 0; i < MAXANIM; i++)
			pAnim[i] = new Anim();
	}

	public static Object GetAnimObject(int index, AnimType type) {
		Object object = null;
		switch (type) {
		case FloorZ:
		case CeilZ:
			object = sector[index];
			break;
		case SpriteZ:
			object = sprite[index];
			break;
		case SectorObjectZ:
			object = SectorObject[index];
			break;
		case UserZ:
			object = pUser[index];
			break;
		case SectUserDepth:
			object = GetSectUser(index);
			break;
		}

		return object;
	}

	public static int AnimSet(int index, int thegoal, int thevel, AnimType type) {
		if (AnimCnt >= MAXANIM)
			return -1;

		Object animptr = GetAnimObject(index, type);
		if (animptr == null)
			return -1;

		// look for existing animation and reset it
		int j = AnimGetGoal(animptr, type);
		if (j == -1)
			j = AnimCnt;

		pAnim[j].ptr = animptr;
		pAnim[j].index = index;
		pAnim[j].type = type;
		pAnim[j].goal = thegoal;
		pAnim[j].vel = Z(thevel);
		pAnim[j].vel_adj = 0;
		pAnim[j].callback = null;
		pAnim[j].callbackdata = -1;

		if (j == AnimCnt)
			AnimCnt++;

		return (j);
	}

	public static int AnimSetCallback(short anim_ndx, AnimCallback call, int data) {
		if (anim_ndx >= AnimCnt)
			return -1;

		if (anim_ndx == -1)
			return (anim_ndx);

		pAnim[anim_ndx].callback = call;
		pAnim[anim_ndx].callbackdata = data;

		return (anim_ndx);
	}

	public static int AnimSetVelAdj(int anim_ndx, int vel_adj) {
		if (anim_ndx >= AnimCnt)
			return -1;

		if (anim_ndx == -1)
			return (anim_ndx);

		pAnim[anim_ndx].vel_adj = (short) vel_adj;

		return (anim_ndx);
	}

	public static void initlava() {
		/* nothing */ }

	public static void movelava(byte[] dapic) {
		/* nothing */ }

	public static void DoPanning() {
		int nx, ny;
		short i, nexti;
		SPRITE sp;
		SECTOR sectp;
		WALL WALL;

		for (i = headspritestat[STAT_FLOOR_PAN]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];
			sectp = sector[sp.sectnum];

			nx = ((sintable[NORM_ANGLE(sp.ang + 512)]) * sp.xvel) >> 20;
			ny = ((sintable[NORM_ANGLE(sp.ang)]) * sp.xvel) >> 20;

			sectp.floorxpanning += nx;
			sectp.floorypanning += ny;

			sectp.floorxpanning &= 255;
			sectp.floorypanning &= 255;
		}

		for (i = headspritestat[STAT_CEILING_PAN]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];
			sectp = sector[sp.sectnum];

			nx = ((sintable[NORM_ANGLE(sp.ang + 512)]) * sp.xvel) >> 20;
			ny = ((sintable[NORM_ANGLE(sp.ang)]) * sp.xvel) >> 20;

			sectp.ceilingxpanning += nx;
			sectp.ceilingypanning += ny;

			sectp.ceilingxpanning &= 255;
			sectp.ceilingypanning &= 255;
		}

		for (i = headspritestat[STAT_WALL_PAN]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];
			WALL = wall[sp.owner];

			nx = ((sintable[NORM_ANGLE(sp.ang + 512)]) * sp.xvel) >> 20;
			ny = ((sintable[NORM_ANGLE(sp.ang)]) * sp.xvel) >> 20;

			WALL.xpanning += nx;
			WALL.ypanning += ny;

			WALL.xpanning &= 255;
			WALL.ypanning &= 255;
		}
	}

	public static void DoSector() {
		if (DebugActorFreeze)
			return;

		for (int j = 0; j < MAX_SECTOR_OBJECTS; j++) {
			Sector_Object sop = SectorObject[j];

			if (sop.xmid == MAXLONG || sop.xmid == MAXSO)
				continue;

			boolean riding = false;
			int min_dist = 999999;
			int dist;

			for (int pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
				PlayerStr pp = Player[pnum];

				if (pp.sop_riding == j) {
					riding = true;
					pp.sop_riding = -1;
					break;
				} else {
					dist = DISTANCE(pp.posx, pp.posy, sop.xmid, sop.ymid);
					if (dist < min_dist)
						min_dist = dist;
				}
			}

			if (sop.Animator != null) {
				sop.Animator.invoke(j);
				continue;
			}

			int sync_flag;
			// force sync SOs to be updated regularly
			if ((sync_flag = DTEST(sop.flags, SOBJ_SYNC1 | SOBJ_SYNC2)) != 0) {
				if (sync_flag == SOBJ_SYNC1)
					MoveSectorObjects(j, synctics, 1);
				else {
					if (!MoveSkip2)
						MoveSectorObjects(j, synctics * 2, 2);
				}

				continue;
			}

			if (riding) {
				// if riding move smoothly
				// update every time
				MoveSectorObjects(j, synctics, 1);
			} else {
				if (min_dist < 15000) {
					// if close update every other time
					if (!MoveSkip2)
						MoveSectorObjects(j, synctics * 2, 2);
				} else {
					// if further update every 4th time
					if (MoveSkip4 == 0)
						MoveSectorObjects(j, synctics * 4, 4);
				}
			}
		}

		DoPanning();
		DoLighting();
		DoSineWaveFloor();
		DoSineWaveWall();
		DoSpringBoardDown();
	}
}
