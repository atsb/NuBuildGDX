package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.CEIL;
import static ru.m210projects.Build.Engine.FLOOR;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.clipmove_sectnum;
import static ru.m210projects.Build.Engine.clipmove_x;
import static ru.m210projects.Build.Engine.clipmove_y;
import static ru.m210projects.Build.Engine.clipmove_z;
import static ru.m210projects.Build.Engine.clipmoveboxtracenum;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Gameutils.isValidSector;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.dmulscale;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Wang.Actor.DoActorBeginSlide;
import static ru.m210projects.Wang.Actor.DoActorDie;
import static ru.m210projects.Wang.Actor.DoBeginJump;
import static ru.m210projects.Wang.Actor.DoFall;
import static ru.m210projects.Wang.Actor.DoJump;
import static ru.m210projects.Wang.Ai.CanSeePlayer;
import static ru.m210projects.Wang.Ai.DoActorSetSpeed;
import static ru.m210projects.Wang.Ai.InitActorPause;
import static ru.m210projects.Wang.Ai.InitActorReposition;
import static ru.m210projects.Wang.Ai.NORM_SPEED;
import static ru.m210projects.Wang.Break.BF_TOUGH;
import static ru.m210projects.Wang.Break.HitBreakSprite;
import static ru.m210projects.Wang.Break.HitBreakWall;
import static ru.m210projects.Wang.Break.WallBreakPosition;
import static ru.m210projects.Wang.Digi.DIGI_30MMEXPLODE;
import static ru.m210projects.Wang.Digi.DIGI_30MMFIRE;
import static ru.m210projects.Wang.Digi.DIGI_40MMBNCE;
import static ru.m210projects.Wang.Digi.DIGI_ARMORHIT;
import static ru.m210projects.Wang.Digi.DIGI_BOATFIRE;
import static ru.m210projects.Wang.Digi.DIGI_BOLTEXPLODE;
import static ru.m210projects.Wang.Digi.DIGI_BUNNYATTACK;
import static ru.m210projects.Wang.Digi.DIGI_BUNNYDIE3;
import static ru.m210projects.Wang.Digi.DIGI_CANBEONLYONE;
import static ru.m210projects.Wang.Digi.DIGI_CANNON;
import static ru.m210projects.Wang.Digi.DIGI_CGMAGIC;
import static ru.m210projects.Wang.Digi.DIGI_CGMAGICHIT;
import static ru.m210projects.Wang.Digi.DIGI_CGTHIGHBONE;
import static ru.m210projects.Wang.Digi.DIGI_COOLIEEXPLODE;
import static ru.m210projects.Wang.Digi.DIGI_DRIP;
import static ru.m210projects.Wang.Digi.DIGI_EATTHIS;
import static ru.m210projects.Wang.Digi.DIGI_FIRE1;
import static ru.m210projects.Wang.Digi.DIGI_FIREBALL1;
import static ru.m210projects.Wang.Digi.DIGI_GASHURT;
import static ru.m210projects.Wang.Digi.DIGI_GIBS1;
import static ru.m210projects.Wang.Digi.DIGI_GIBS2;
import static ru.m210projects.Wang.Digi.DIGI_GRDAXEHIT;
import static ru.m210projects.Wang.Digi.DIGI_GRDSWINGAXE;
import static ru.m210projects.Wang.Digi.DIGI_HEADFIRE;
import static ru.m210projects.Wang.Digi.DIGI_HEADSHOTHIT;
import static ru.m210projects.Wang.Digi.DIGI_HORNETSTING;
import static ru.m210projects.Wang.Digi.DIGI_HOWYOULIKEMOVE;
import static ru.m210projects.Wang.Digi.DIGI_ITEM_SPAWN;
import static ru.m210projects.Wang.Digi.DIGI_KUNGFU;
import static ru.m210projects.Wang.Digi.DIGI_LIKEFIREWORKS;
import static ru.m210projects.Wang.Digi.DIGI_LIKEHIROSHIMA;
import static ru.m210projects.Wang.Digi.DIGI_LIKENAGASAKI;
import static ru.m210projects.Wang.Digi.DIGI_LIKEPEARL;
import static ru.m210projects.Wang.Digi.DIGI_M60;
import static ru.m210projects.Wang.Digi.DIGI_MAGIC1;
import static ru.m210projects.Wang.Digi.DIGI_MEDIUMEXP;
import static ru.m210projects.Wang.Digi.DIGI_MINEBEEP;
import static ru.m210projects.Wang.Digi.DIGI_MINEBLOW;
import static ru.m210projects.Wang.Digi.DIGI_MINETHROW;
import static ru.m210projects.Wang.Digi.DIGI_MIRVFIRE;
import static ru.m210projects.Wang.Digi.DIGI_MIRVWIZ;
import static ru.m210projects.Wang.Digi.DIGI_MISSLFIRE;
import static ru.m210projects.Wang.Digi.DIGI_NAPFIRE;
import static ru.m210projects.Wang.Digi.DIGI_NAPPUFF;
import static ru.m210projects.Wang.Digi.DIGI_NAPWIZ;
import static ru.m210projects.Wang.Digi.DIGI_NINJACHOKE;
import static ru.m210projects.Wang.Digi.DIGI_NINJARIOTATTACK;
import static ru.m210projects.Wang.Digi.DIGI_NINJAUZIATTACK;
import static ru.m210projects.Wang.Digi.DIGI_NUCLEAREXP;
import static ru.m210projects.Wang.Digi.DIGI_PAINFORWEAK;
import static ru.m210projects.Wang.Digi.DIGI_PAYINGATTENTION;
import static ru.m210projects.Wang.Digi.DIGI_PROJECTILEWATERHIT;
import static ru.m210projects.Wang.Digi.DIGI_RAILFIRE;
import static ru.m210projects.Wang.Digi.DIGI_RFWIZ;
import static ru.m210projects.Wang.Digi.DIGI_RICHOCHET1;
import static ru.m210projects.Wang.Digi.DIGI_RICHOCHET2;
import static ru.m210projects.Wang.Digi.DIGI_RIOTFIRE;
import static ru.m210projects.Wang.Digi.DIGI_RIOTFIRE2;
import static ru.m210projects.Wang.Digi.DIGI_RIPPER2ATTACK;
import static ru.m210projects.Wang.Digi.DIGI_RIPPERHEARTOUT;
import static ru.m210projects.Wang.Digi.DIGI_SERPMAGICLAUNCH;
import static ru.m210projects.Wang.Digi.DIGI_SERPSUMMONHEADS;
import static ru.m210projects.Wang.Digi.DIGI_SERPSWORDATTACK;
import static ru.m210projects.Wang.Digi.DIGI_SHELL;
import static ru.m210projects.Wang.Digi.DIGI_SHOTSHELLSPENT;
import static ru.m210projects.Wang.Digi.DIGI_SMALLEXP;
import static ru.m210projects.Wang.Digi.DIGI_SONOFABITCH;
import static ru.m210projects.Wang.Digi.DIGI_SPBLADE;
import static ru.m210projects.Wang.Digi.DIGI_SPELEC;
import static ru.m210projects.Wang.Digi.DIGI_SPLASH1;
import static ru.m210projects.Wang.Digi.DIGI_STAR;
import static ru.m210projects.Wang.Digi.DIGI_STARCLINK;
import static ru.m210projects.Wang.Digi.DIGI_STEPONCALTROPS;
import static ru.m210projects.Wang.Digi.DIGI_STICKYGOTU1;
import static ru.m210projects.Wang.Digi.DIGI_STICKYGOTU2;
import static ru.m210projects.Wang.Digi.DIGI_STICKYGOTU3;
import static ru.m210projects.Wang.Digi.DIGI_STICKYGOTU4;
import static ru.m210projects.Wang.Digi.DIGI_SWORDGOTU1;
import static ru.m210projects.Wang.Digi.DIGI_SWORDGOTU2;
import static ru.m210projects.Wang.Digi.DIGI_SWORDGOTU3;
import static ru.m210projects.Wang.Digi.DIGI_TAUNTAI4;
import static ru.m210projects.Wang.Digi.DIGI_TAUNTAI5;
import static ru.m210projects.Wang.Digi.DIGI_TOILETGIRLSCREAM;
import static ru.m210projects.Wang.Digi.DIGI_TRASHLID;
import static ru.m210projects.Wang.Digi.DIGI_UZIFIRE;
import static ru.m210projects.Wang.Digi.DIGI_YOULOOKSTUPID;
import static ru.m210projects.Wang.Enemies.Bunny.Bunny_Count;
import static ru.m210projects.Wang.Enemies.Bunny.DoBunnyRipHeart;
import static ru.m210projects.Wang.Enemies.Coolie.EnemyDefaults;
import static ru.m210projects.Wang.Enemies.Ripper.DoRipperRipHeart;
import static ru.m210projects.Wang.Enemies.Ripper2.DoRipper2RipHeart;
import static ru.m210projects.Wang.Enemies.Ripper2.Ripper2Hatch;
import static ru.m210projects.Wang.Enemies.Skull.DoBettyBeginDeath;
import static ru.m210projects.Wang.Enemies.Skull.DoSkullBeginDeath;
import static ru.m210projects.Wang.Enemies.Skull.SkullAttrib;
import static ru.m210projects.Wang.Enemies.Skull.s_SkullExplode;
import static ru.m210projects.Wang.Enemies.Skull.s_SkullRing;
import static ru.m210projects.Wang.Enemies.Skull.s_SkullWait;
import static ru.m210projects.Wang.Enemies.Zombie.SpawnZombie2;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Game.ANIM_SERP;
import static ru.m210projects.Wang.Game.ANIM_SUMO;
import static ru.m210projects.Wang.Game.ANIM_ZILLA;
import static ru.m210projects.Wang.Game.Distance;
import static ru.m210projects.Wang.Game.ExitLevel;
import static ru.m210projects.Wang.Game.FinishAnim;
import static ru.m210projects.Wang.Game.FinishedLevel;
import static ru.m210projects.Wang.Game.Kills;
import static ru.m210projects.Wang.Game.Level;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.screenpeek;
import static ru.m210projects.Wang.Game.tmp_ptr;
import static ru.m210projects.Wang.Gameutils.*;
import static ru.m210projects.Wang.JTags.LUMINOUS;
import static ru.m210projects.Wang.JWeapon.InitBloodSpray;
import static ru.m210projects.Wang.JWeapon.InitChemBomb;
import static ru.m210projects.Wang.JWeapon.InitPhosphorus;
import static ru.m210projects.Wang.JWeapon.InitShell;
import static ru.m210projects.Wang.JWeapon.SpawnFloorSplash;
import static ru.m210projects.Wang.JWeapon.SpawnMidSplash;
import static ru.m210projects.Wang.JWeapon.s_NukeMushroom;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.MiscActr.s_TrashCanPain;
import static ru.m210projects.Wang.Names.BETTY_R0;
import static ru.m210projects.Wang.Names.BLADE1;
import static ru.m210projects.Wang.Names.BLADE2;
import static ru.m210projects.Wang.Names.BLADE3;
import static ru.m210projects.Wang.Names.BOLT_THINMAN_R0;
import static ru.m210projects.Wang.Names.BOLT_THINMAN_R1;
import static ru.m210projects.Wang.Names.BOLT_THINMAN_R2;
import static ru.m210projects.Wang.Names.BOLT_THINMAN_R3;
import static ru.m210projects.Wang.Names.BOLT_THINMAN_R4;
import static ru.m210projects.Wang.Names.BREAK_BARREL;
import static ru.m210projects.Wang.Names.BREAK_BOTTLE1;
import static ru.m210projects.Wang.Names.BREAK_BOTTLE2;
import static ru.m210projects.Wang.Names.BREAK_LIGHT_ANIM;
import static ru.m210projects.Wang.Names.BREAK_PEDISTAL;
import static ru.m210projects.Wang.Names.BUNNY_RUN_R0;
import static ru.m210projects.Wang.Names.CARGIRL_R0;
import static ru.m210projects.Wang.Names.COOLG_RUN_R0;
import static ru.m210projects.Wang.Names.COOLIE_RUN_R0;
import static ru.m210projects.Wang.Names.EEL_RUN_R0;
import static ru.m210projects.Wang.Names.ELECTRO;
import static ru.m210projects.Wang.Names.EXP;
import static ru.m210projects.Wang.Names.FIREBALL;
import static ru.m210projects.Wang.Names.FIREBALL1;
import static ru.m210projects.Wang.Names.FIREBALL_FLAMES;
import static ru.m210projects.Wang.Names.GIRLNINJA_RUN_R0;
import static ru.m210projects.Wang.Names.GORO_RUN_R0;
import static ru.m210projects.Wang.Names.HORNET_RUN_R0;
import static ru.m210projects.Wang.Names.LAVA_BOULDER;
import static ru.m210projects.Wang.Names.LAVA_RUN_R0;
import static ru.m210projects.Wang.Names.MECHANICGIRL_R0;
import static ru.m210projects.Wang.Names.NAPALM_MIN_AMMO;
import static ru.m210projects.Wang.Names.NINJA_CRAWL_R0;
import static ru.m210projects.Wang.Names.NINJA_RUN_R0;
import static ru.m210projects.Wang.Names.PACHINKO1;
import static ru.m210projects.Wang.Names.PACHINKO2;
import static ru.m210projects.Wang.Names.PACHINKO3;
import static ru.m210projects.Wang.Names.PACHINKO4;
import static ru.m210projects.Wang.Names.PRUNEGIRL_R0;
import static ru.m210projects.Wang.Names.RIPPER2_RUN_R0;
import static ru.m210projects.Wang.Names.RIPPER_RUN_R0;
import static ru.m210projects.Wang.Names.SAILORGIRL_R0;
import static ru.m210projects.Wang.Names.SERP_RUN_R0;
import static ru.m210projects.Wang.Names.SKEL_RUN_R0;
import static ru.m210projects.Wang.Names.SKULL_R0;
import static ru.m210projects.Wang.Names.SKULL_SERP;
import static ru.m210projects.Wang.Names.SPEAR_R0;
import static ru.m210projects.Wang.Names.SPEAR_R1;
import static ru.m210projects.Wang.Names.SPEAR_R2;
import static ru.m210projects.Wang.Names.SPEAR_R3;
import static ru.m210projects.Wang.Names.SPEAR_R4;
import static ru.m210projects.Wang.Names.STAR1;
import static ru.m210projects.Wang.Names.STAT_BREAKABLE;
import static ru.m210projects.Wang.Names.STAT_DEAD_ACTOR;
import static ru.m210projects.Wang.Names.STAT_DEFAULT;
import static ru.m210projects.Wang.Names.STAT_DIVE_AREA;
import static ru.m210projects.Wang.Names.STAT_ENEMY;
import static ru.m210projects.Wang.Names.STAT_ENEMY_SKIP4;
import static ru.m210projects.Wang.Names.STAT_GENERIC_QUEUE;
import static ru.m210projects.Wang.Names.STAT_HOLE_QUEUE;
import static ru.m210projects.Wang.Names.STAT_MINE_STUCK;
import static ru.m210projects.Wang.Names.STAT_MISSILE;
import static ru.m210projects.Wang.Names.STAT_MISSILE_SKIP4;
import static ru.m210projects.Wang.Names.STAT_NO_STATE;
import static ru.m210projects.Wang.Names.STAT_PLAYER0;
import static ru.m210projects.Wang.Names.STAT_PLAYER1;
import static ru.m210projects.Wang.Names.STAT_PLAYER2;
import static ru.m210projects.Wang.Names.STAT_PLAYER3;
import static ru.m210projects.Wang.Names.STAT_PLAYER4;
import static ru.m210projects.Wang.Names.STAT_PLAYER5;
import static ru.m210projects.Wang.Names.STAT_PLAYER6;
import static ru.m210projects.Wang.Names.STAT_PLAYER7;
import static ru.m210projects.Wang.Names.STAT_PLAYER_UNDER0;
import static ru.m210projects.Wang.Names.STAT_PLAYER_UNDER1;
import static ru.m210projects.Wang.Names.STAT_PLAYER_UNDER2;
import static ru.m210projects.Wang.Names.STAT_PLAYER_UNDER3;
import static ru.m210projects.Wang.Names.STAT_PLAYER_UNDER4;
import static ru.m210projects.Wang.Names.STAT_PLAYER_UNDER5;
import static ru.m210projects.Wang.Names.STAT_PLAYER_UNDER6;
import static ru.m210projects.Wang.Names.STAT_PLAYER_UNDER7;
import static ru.m210projects.Wang.Names.STAT_SHRAP;
import static ru.m210projects.Wang.Names.STAT_SKIP4;
import static ru.m210projects.Wang.Names.STAT_SO_SHOOT_POINT;
import static ru.m210projects.Wang.Names.STAT_SO_SP_CHILD;
import static ru.m210projects.Wang.Names.STAT_STAR_QUEUE;
import static ru.m210projects.Wang.Names.STAT_STATIC_FIRE;
import static ru.m210projects.Wang.Names.STAT_TRIGGER;
import static ru.m210projects.Wang.Names.STAT_UNDERWATER;
import static ru.m210projects.Wang.Names.STAT_WALLBLOOD_QUEUE;
import static ru.m210projects.Wang.Names.STAT_WALL_MOVE;
import static ru.m210projects.Wang.Names.SUMO_RUN_R0;
import static ru.m210projects.Wang.Names.TOILETGIRL_R0;
import static ru.m210projects.Wang.Names.TRASHCAN;
import static ru.m210projects.Wang.Names.UZI_SMOKE;
import static ru.m210projects.Wang.Names.UZI_SPARK;
import static ru.m210projects.Wang.Names.WASHGIRL_R0;
import static ru.m210projects.Wang.Names.ZILLA_RUN_R0;
import static ru.m210projects.Wang.Names.ZOMBIE_RUN_R0;
import static ru.m210projects.Wang.Palette.PALETTE_BLUE_LIGHTING;
import static ru.m210projects.Wang.Palette.PALETTE_DEFAULT;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER1;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER3;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER5;
import static ru.m210projects.Wang.Palette.PALETTE_RED_LIGHTING;
import static ru.m210projects.Wang.Palette.PAL_XLAT_LT_GREY;
import static ru.m210projects.Wang.Palette.PAL_XLAT_LT_TAN;
import static ru.m210projects.Wang.Palette.SetFadeAmt;
import static ru.m210projects.Wang.Panel.PlayerUpdateAmmo;
import static ru.m210projects.Wang.Panel.PlayerUpdateHealth;
import static ru.m210projects.Wang.Player.DoPickTarget;
import static ru.m210projects.Wang.Player.DoPlayerBeginDie;
import static ru.m210projects.Wang.Player.DoPlayerBeginRecoil;
import static ru.m210projects.Wang.Player.GetDeltaAngle;
import static ru.m210projects.Wang.Player.GetOverlapSector;
import static ru.m210projects.Wang.Player.SetVisHigh;
import static ru.m210projects.Wang.Player.TargetSort;
import static ru.m210projects.Wang.Player.TargetSortCount;
import static ru.m210projects.Wang.Quake.SetExpQuake;
import static ru.m210projects.Wang.Quake.SetGunQuake;
import static ru.m210projects.Wang.Quake.SetNuclearQuake;
import static ru.m210projects.Wang.Rooms.COVERinsertsprite;
import static ru.m210projects.Wang.Rooms.COVERupdatesector;
import static ru.m210projects.Wang.Rooms.FAFcansee;
import static ru.m210projects.Wang.Rooms.FAFgetzrange;
import static ru.m210projects.Wang.Rooms.FAFgetzrangepoint;
import static ru.m210projects.Wang.Rooms.FAFhitscan;
import static ru.m210projects.Wang.Rooms.zofslope;
import static ru.m210projects.Wang.Sector.DoMatchEverything;
import static ru.m210projects.Wang.Sector.DoSoundSpotMatch;
import static ru.m210projects.Wang.Sector.DoSpawnSpotsForDamage;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Sector.ShootableSwitch;
import static ru.m210projects.Wang.Sector.TestKillSectorObject;
import static ru.m210projects.Wang.Sector.WeaponExplodeSectorInRange;
import static ru.m210projects.Wang.Shrap.SpawnShrap;
import static ru.m210projects.Wang.Shrap.Z_MID;
import static ru.m210projects.Wang.Shrap.Z_TOP;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.PlaySpriteSound;
import static ru.m210projects.Wang.Sound.PlayerPainVocs;
import static ru.m210projects.Wang.Sound.PlayerSound;
import static ru.m210projects.Wang.Sound.Set3DSoundOwner;
import static ru.m210projects.Wang.Sound.TauntAIVocs;
import static ru.m210projects.Wang.Sound.sndCoords;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_doppler;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sound.v3df_none;
import static ru.m210projects.Wang.Sprites.ActorCoughItem;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.DoActorZrange;
import static ru.m210projects.Wang.Sprites.KillSprite;
import static ru.m210projects.Wang.Sprites.MissileWaterAdjust;
import static ru.m210projects.Wang.Sprites.MoveSkip2;
import static ru.m210projects.Wang.Sprites.NewStateGroup;
import static ru.m210projects.Wang.Sprites.SectorObject;
import static ru.m210projects.Wang.Sprites.SetAttach;
import static ru.m210projects.Wang.Sprites.SetOwner;
import static ru.m210projects.Wang.Sprites.SpawnSprite;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Sprites.SpriteOverlap;
import static ru.m210projects.Wang.Sprites.SpriteOverlapZ;
import static ru.m210projects.Wang.Sprites.change_sprite_stat;
import static ru.m210projects.Wang.Sprites.move_ground_missile;
import static ru.m210projects.Wang.Sprites.move_missile;
import static ru.m210projects.Wang.Tags.TAG_SPRITE_HIT_MATCH;
import static ru.m210projects.Wang.Tags.TAG_WALL_BREAK;
import static ru.m210projects.Wang.Track.ActorLeaveTrack;
import static ru.m210projects.Wang.Track.DetectSectorObject;
import static ru.m210projects.Wang.Track.DetectSectorObjectByWall;
import static ru.m210projects.Wang.Track.VehicleSetSmoke;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.DIV16;
import static ru.m210projects.Wang.Type.MyTypes.DIV2;
import static ru.m210projects.Wang.Type.MyTypes.DIV4;
import static ru.m210projects.Wang.Type.MyTypes.DIV8;
import static ru.m210projects.Wang.Type.MyTypes.DTEST;
import static ru.m210projects.Wang.Type.MyTypes.MAXLONG;
import static ru.m210projects.Wang.Type.MyTypes.MOD2;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Type.Saveable.SaveGroup;
import static ru.m210projects.Wang.Vis.SpawnVis;
import static ru.m210projects.Wang.WallMove.CanSeeWallMove;
import static ru.m210projects.Wang.WallMove.DoWallMove;
import static ru.m210projects.Wang.WallMove.DoWallMoveMatch;
import static ru.m210projects.Wang.Weapons.Sword.SpawnSwordSparks;

import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Wang.Ai.Attrib_Snds;
import ru.m210projects.Wang.Gameutils.FootType;
import ru.m210projects.Wang.Player.Player_Action_Func;
import ru.m210projects.Wang.Sound.SoundType;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Enemies.Ninja.NinjaStateGroup;
import ru.m210projects.Wang.Enemies.Skull.SkullStateGroup;
import ru.m210projects.Wang.Factory.WangNetwork.MultiGameTypes;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.Break_Info;
import ru.m210projects.Wang.Type.DAMAGE_DATA;
import ru.m210projects.Wang.Type.LONGp;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.SHRAP;
import ru.m210projects.Wang.Type.Sect_User;
import ru.m210projects.Wang.Type.Sector_Object;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.Target_Sort;
import ru.m210projects.Wang.Type.USER;

public class Weapon {

	public static FootType FootMode;
	public static boolean GlobalSkipZrange = false;

	public static final int NEW_ELECTRO = 1;
	public static final int HORIZ_MULT = 128;

	public static final int MAX_HOLE_QUEUE = 64;
	public static final int MAX_STAR_QUEUE = 32;
	public static final int MAX_WALLBLOOD_QUEUE = 32;
	public static final int MAX_FLOORBLOOD_QUEUE = 32;
	public static final int MAX_GENERIC_QUEUE = 32;
	public static final int MAX_LOWANGS_QUEUE = 16;

	// public static final int NUKE_RADIUS =16384;
	public static final int NUKE_RADIUS = 30000;
	public static final int RAIL_RADIUS = 3500;

	// Damage Times - takes damage after this many tics
	public static final int DAMAGE_BLADE_TIME = (10);

	// Player Missile Speeds
	public static final int STAR_VELOCITY = (1800);
	public static final int BOLT_VELOCITY = (900);
	public static final int ROCKET_VELOCITY = (1350);
	public static final int BOLT_SEEKER_VELOCITY = (820);
	public static final int FIREBALL_VELOCITY = (2000);
	public static final int ELECTRO_VELOCITY = (800);
	public static final int PLASMA_VELOCITY = (1000);
	public static final int UZI_BULLET_VELOCITY = (2500);
	public static final int TRACER_VELOCITY = (1200);
	public static final int TANK_SHELL_VELOCITY = (1200);
	public static final int GRENADE_VELOCITY = (900);
	public static final int MINE_VELOCITY = (520); // Was 420
	public static final int CHEMBOMB_VELOCITY = (420);

	// Player Spell Missile Speeds
	public static final int BLOOD_WORM_VELOCITY = (800);
	public static final int NAPALM_VELOCITY = (800);
	public static final int MIRV_VELOCITY = (600);
	public static final int SPIRAL_VELOCITY = (600);

	// Trap Speeds
	public static final int BOLT_TRAP_VELOCITY = (950);
	public static final int SPEAR_TRAP_VELOCITY = (650);
	public static final int FIREBALL_TRAP_VELOCITY = (750);

	// NPC Missile Speeds
	public static final int NINJA_STAR_VELOCITY = (1800);
	public static final int NINJA_BOLT_VELOCITY = (500);
	public static final int GORO_FIREBALL_VELOCITY = (800);
	public static final int SKEL_ELECTRO_VELOCITY = (850);
	public static final int COOLG_FIRE_VELOCITY = (400);

	public static final int GRENADE_RECOIL_AMT = (12);
	public static final int ROCKET_RECOIL_AMT = (7);
	public static final int RAIL_RECOIL_AMT = (7);
	public static final int SHOTGUN_RECOIL_AMT = (12);
	// public static final int MICRO_RECOIL_AMT =(15);

	// Damage amounts that determine the type of player death
	// The standard flip over death is default
	public static final int PLAYER_DEATH_CRUMBLE_DAMMAGE_AMT = (25);
	public static final int PLAYER_DEATH_EXPLODE_DAMMAGE_AMT = (65);

	// electro weapon
	public static final int ELECTRO_MAX_JUMP_DIST = 25000;

	public static final int RADIATION_CLOUD = 3258;
	public static final int MUSHROOM_CLOUD = 3280;
	public static final int PUFF = 1748;
	public static final int CALTROPS = 2218;
	public static final int PHOSPHORUS = 1397;
	public static final int MISSILEMOVETICS = 6;

	public static final Animator DoSuicide = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoSuicide(spr);
		}
	};

	public static short StatDamageList[] = { STAT_SO_SP_CHILD, STAT_ENEMY, STAT_ENEMY_SKIP4, STAT_PLAYER0, STAT_PLAYER1,
			STAT_PLAYER2, STAT_PLAYER3, STAT_PLAYER4, STAT_PLAYER5, STAT_PLAYER6, STAT_PLAYER7, STAT_PLAYER_UNDER0,
			STAT_PLAYER_UNDER1, STAT_PLAYER_UNDER2, STAT_PLAYER_UNDER3, STAT_PLAYER_UNDER4, STAT_PLAYER_UNDER5,
			STAT_PLAYER_UNDER6, STAT_PLAYER_UNDER7,
			// MINE MUST BE LAST
			STAT_MINE_STUCK };

	public static boolean left_foot = false;
	public static int FinishTimer = 0;

	// This is how many bullet shells have been spawned since the beginning of the
	// game.
	public static int ShellCount = 0;

	public static short StarQueueHead = 0;
	public static short StarQueue[] = new short[MAX_STAR_QUEUE];
	public static short HoleQueueHead = 0;
	public static short HoleQueue[] = new short[MAX_HOLE_QUEUE];
	public static short WallBloodQueueHead = 0;
	public static short WallBloodQueue[] = new short[MAX_WALLBLOOD_QUEUE];
	public static short FloorBloodQueueHead = 0;
	public static short FloorBloodQueue[] = new short[MAX_FLOORBLOOD_QUEUE];
	public static short GenericQueueHead = 0;
	public static short GenericQueue[] = new short[MAX_GENERIC_QUEUE];
	public static short LoWangsQueueHead = 0;
	public static short LoWangsQueue[] = new short[MAX_LOWANGS_QUEUE];

	public static DAMAGE_DATA[] DamageData = {
			new DAMAGE_DATA(WPN_FIST, Player_Action_Func.InitWeaponFist, 10, 40, 0, -1, -1, -1),
			new DAMAGE_DATA(WPN_STAR, Player_Action_Func.InitWeaponStar, 5, 10, 0, 99, 3, -1),
			new DAMAGE_DATA(WPN_SHOTGUN, Player_Action_Func.InitWeaponShotgun, 4, 4, 0, 52, 1, -1),
			new DAMAGE_DATA(WPN_UZI, Player_Action_Func.InitWeaponUzi, 5, 7, 0, 200, 1, -1),
			new DAMAGE_DATA(WPN_MICRO, Player_Action_Func.InitWeaponMicro, 15, 30, 0, 50, 1, -1),
			new DAMAGE_DATA(WPN_GRENADE, Player_Action_Func.InitWeaponGrenade, 15, 30, 0, 50, 1, -1),
			new DAMAGE_DATA(WPN_MINE, Player_Action_Func.InitWeaponMine, 5, 10, 0, 20, 1, -1),
			new DAMAGE_DATA(WPN_RAIL, Player_Action_Func.InitWeaponRail, 40, 60, 0, 20, 1, -1),
			new DAMAGE_DATA(WPN_HOTHEAD, Player_Action_Func.InitWeaponHothead, 10, 25, 0, 80, 1, -1),
			new DAMAGE_DATA(WPN_HEART, Player_Action_Func.InitWeaponHeart, 75, 100, 0, 5, 1, -1),

			new DAMAGE_DATA(WPN_NAPALM, Player_Action_Func.InitWeaponHothead, 50, 100, 0, 100, 40, WPN_HOTHEAD),
			new DAMAGE_DATA(WPN_RING, Player_Action_Func.InitWeaponHothead, 15, 50, 0, 100, 20, WPN_HOTHEAD),
			new DAMAGE_DATA(WPN_ROCKET, Player_Action_Func.InitWeaponMicro, 30, 60, 0, 100, 1, WPN_MICRO),
			new DAMAGE_DATA(WPN_SWORD, Player_Action_Func.InitWeaponSword, 50, 80, 0, -1, -1, -1),

			// extra weapons connected to other

			// spell
			new DAMAGE_DATA(DMG_NAPALM, null, 90, 150, 0, -1, -1, -1),
			new DAMAGE_DATA(DMG_MIRV_METEOR, null, 35, 65, 0, -1, -1, -1),
			new DAMAGE_DATA(DMG_SERP_METEOR, null, 7, 15, 0, -1, -1, -1),

			// radius damage
			new DAMAGE_DATA(DMG_ELECTRO_SHARD, null, 2, 6, 0, -1, -1, -1),
			new DAMAGE_DATA(DMG_SECTOR_EXP, null, 50, 100, 3200, -1, -1, -1),
			new DAMAGE_DATA(DMG_BOLT_EXP, null, 80, 160, 3200, -1, -1, -1),
			new DAMAGE_DATA(DMG_TANK_SHELL_EXP, null, 80, 200, 4500, -1, -1, -1),
			new DAMAGE_DATA(DMG_FIREBALL_EXP, null, -1, -1, 1000, -1, -1, -1),
			new DAMAGE_DATA(DMG_NAPALM_EXP, null, 60, 90, 3200, -1, -1, -1),
			new DAMAGE_DATA(DMG_SKULL_EXP, null, 40, 75, 4500, -1, -1, -1),
			new DAMAGE_DATA(DMG_BASIC_EXP, null, 10, 25, 1000, -1, -1, -1),
			new DAMAGE_DATA(DMG_GRENADE_EXP, null, 70, 140, 6500, -1, -1, -1),
			new DAMAGE_DATA(DMG_MINE_EXP, null, 85, 115, 6500, -1, -1, -1),
			new DAMAGE_DATA(DMG_MINE_SHRAP, null, 15, 30, 0, -1, -1, -1),
			new DAMAGE_DATA(DMG_MICRO_EXP, null, 50, 100, 4500, -1, -1, -1),
			new DAMAGE_DATA(DMG_NUCLEAR_EXP, null, 0, 800, 30000, -1, -1, -1),
			new DAMAGE_DATA(DMG_RADIATION_CLOUD, null, 2, 6, 5000, -1, -1, -1),
			new DAMAGE_DATA(DMG_FLASHBOMB, null, 100, 150, 16384, -1, -1, -1),

			new DAMAGE_DATA(DMG_FIREBALL_FLAMES, null, 2, 6, 300, -1, -1, -1),

			// actor
			new DAMAGE_DATA(DMG_RIPPER_SLASH, null, 10, 30, 0, -1, -1, -1),
			new DAMAGE_DATA(DMG_SKEL_SLASH, null, 10, 20, 0, -1, -1, -1),
			new DAMAGE_DATA(DMG_COOLG_BASH, null, 10, 20, 0, -1, -1, -1),
			new DAMAGE_DATA(DMG_COOLG_FIRE, null, 15, 30, 0, -1, -1, -1),
			new DAMAGE_DATA(DMG_GORO_CHOP, null, 20, 40, 0, -1, -1, -1),
			new DAMAGE_DATA(DMG_GORO_FIREBALL, null, 5, 20, 0, -1, -1, -1),
			new DAMAGE_DATA(DMG_SERP_SLASH, null, 75, 75, 0, -1, -1, -1),
			new DAMAGE_DATA(DMG_LAVA_BOULDER, null, 100, 100, 0, -1, -1, -1),
			new DAMAGE_DATA(DMG_LAVA_SHARD, null, 25, 25, 0, -1, -1, -1),
			new DAMAGE_DATA(DMG_HORNET_STING, null, 5, 10, 0, -1, -1, -1),
			new DAMAGE_DATA(DMG_EEL_ELECTRO, null, 10, 40, 3400, -1, -1, -1),

			// misc
			new DAMAGE_DATA(DMG_SPEAR_TRAP, null, 15, 20, 0, -1, -1, -1),
			new DAMAGE_DATA(DMG_VOMIT, null, 5, 15, 0, -1, -1, -1),

			// inanimate objects
			new DAMAGE_DATA(DMG_BLADE, null, 10, 20, 0, -1, -1, -1),
			new DAMAGE_DATA(MAX_WEAPONS, null, 10, 20, 0, -1, -1, -1), };

//////////////////////
//
//SPECIAL public static final StateS
//
//////////////////////

//public static final State for sprites that are not restored
	public static final State s_NotRestored[] = { new State(2323, 100, null).setNext(), };

	public static final State s_Suicide[] = { new State(1, 100, DoSuicide).setNext(), };

	public static final State s_DeadLoWang[] = { new State(1160, 100, null).setNext(), };

//////////////////////
//
//BREAKABLE public static final StateS
//
//////////////////////

	public static final Animator DoDefaultStat = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoDefaultStat(Weapon) != 0;
		}
	};

	public static final int BREAK_LIGHT_RATE = 18;
	public static final State s_BreakLight[] = { new State(BREAK_LIGHT_ANIM + 0, BREAK_LIGHT_RATE, null),
			new State(BREAK_LIGHT_ANIM + 1, BREAK_LIGHT_RATE, null),
			new State(BREAK_LIGHT_ANIM + 2, BREAK_LIGHT_RATE, null),
			new State(BREAK_LIGHT_ANIM + 3, BREAK_LIGHT_RATE, null),
			new State(BREAK_LIGHT_ANIM + 4, BREAK_LIGHT_RATE, null),
			new State(BREAK_LIGHT_ANIM + 5, BREAK_LIGHT_RATE, null).setNext(), };

	public static final int BREAK_BARREL_RATE = 18;
	public static final State s_BreakBarrel[] = { new State(BREAK_BARREL + 4, BREAK_BARREL_RATE, null),
			new State(BREAK_BARREL + 5, BREAK_BARREL_RATE, null), new State(BREAK_BARREL + 6, BREAK_BARREL_RATE, null),
			new State(BREAK_BARREL + 7, BREAK_BARREL_RATE, null), new State(BREAK_BARREL + 8, BREAK_BARREL_RATE, null),
			new State(BREAK_BARREL + 9, BREAK_BARREL_RATE, DoDefaultStat).setNext() };

	public static final int BREAK_PEDISTAL_RATE = 28;
	public static final State s_BreakPedistal[] = { new State(BREAK_PEDISTAL + 1, BREAK_PEDISTAL_RATE, null),
			new State(BREAK_PEDISTAL + 2, BREAK_PEDISTAL_RATE, null),
			new State(BREAK_PEDISTAL + 3, BREAK_PEDISTAL_RATE, null),
			new State(BREAK_PEDISTAL + 4, BREAK_PEDISTAL_RATE, null).setNext() };

	public static final int BREAK_BOTTLE1_RATE = 18;
	public static final State s_BreakBottle1[] = { new State(BREAK_BOTTLE1 + 1, BREAK_BOTTLE1_RATE, null),
			new State(BREAK_BOTTLE1 + 2, BREAK_BOTTLE1_RATE, null),
			new State(BREAK_BOTTLE1 + 3, BREAK_BOTTLE1_RATE, null),
			new State(BREAK_BOTTLE1 + 4, BREAK_BOTTLE1_RATE, null),
			new State(BREAK_BOTTLE1 + 5, BREAK_BOTTLE1_RATE, null),
			new State(BREAK_BOTTLE1 + 6, BREAK_BOTTLE1_RATE, null).setNext(), };

	public static final int BREAK_BOTTLE2_RATE = 18;
	public static final State s_BreakBottle2[] = { new State(BREAK_BOTTLE2 + 1, BREAK_BOTTLE2_RATE, null),
			new State(BREAK_BOTTLE2 + 2, BREAK_BOTTLE2_RATE, null),
			new State(BREAK_BOTTLE2 + 3, BREAK_BOTTLE2_RATE, null),
			new State(BREAK_BOTTLE2 + 4, BREAK_BOTTLE2_RATE, null),
			new State(BREAK_BOTTLE2 + 5, BREAK_BOTTLE2_RATE, null),
			new State(BREAK_BOTTLE2 + 6, BREAK_BOTTLE2_RATE, null).setNext(), };

	public static final int PUFF_RATE = 8;
	public static final Animator DoPuff = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoPuff(Weapon) != 0;
		}
	};

	public static final State s_Puff[] = { new State(PUFF + 0, PUFF_RATE, DoPuff),
			new State(PUFF + 1, PUFF_RATE, DoPuff), new State(PUFF + 2, PUFF_RATE, DoPuff),
			new State(PUFF + 3, PUFF_RATE, DoPuff), new State(PUFF + 4, PUFF_RATE, DoPuff),
			new State(PUFF + 5, PUFF_RATE, DoPuff), new State(PUFF + 5, 100, DoSuicide), };

	public static final int RAIL_PUFF_R0 = 3969;
	public static final int RAIL_PUFF_R1 = 3985;
	public static final int RAIL_PUFF_R2 = 4001;
	public static final int RAIL_PUFF_RATE = 6;

	public static final Animator DoRailPuff = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoRailPuff(Weapon) != 0;
		}
	};

	public static final State s_RailPuff[][] = { { new State(RAIL_PUFF_R0 + 0, RAIL_PUFF_RATE, DoRailPuff),
			new State(RAIL_PUFF_R0 + 1, RAIL_PUFF_RATE, DoRailPuff),
			new State(RAIL_PUFF_R0 + 2, RAIL_PUFF_RATE, DoRailPuff),
			new State(RAIL_PUFF_R0 + 3, RAIL_PUFF_RATE, DoRailPuff),
			new State(RAIL_PUFF_R0 + 4, RAIL_PUFF_RATE, DoRailPuff),
			new State(RAIL_PUFF_R0 + 5, RAIL_PUFF_RATE, DoRailPuff),
			new State(RAIL_PUFF_R0 + 6, RAIL_PUFF_RATE, DoRailPuff),
			new State(RAIL_PUFF_R0 + 7, RAIL_PUFF_RATE, DoRailPuff),
			new State(RAIL_PUFF_R0 + 8, RAIL_PUFF_RATE, DoRailPuff),
			new State(RAIL_PUFF_R0 + 9, RAIL_PUFF_RATE, DoRailPuff),
			new State(RAIL_PUFF_R0 + 10, RAIL_PUFF_RATE, DoRailPuff),
			new State(RAIL_PUFF_R0 + 11, RAIL_PUFF_RATE, DoRailPuff),
			new State(RAIL_PUFF_R0 + 12, RAIL_PUFF_RATE, DoRailPuff),
			new State(RAIL_PUFF_R0 + 13, RAIL_PUFF_RATE, DoRailPuff),
			new State(RAIL_PUFF_R0 + 14, RAIL_PUFF_RATE, DoRailPuff),
			new State(RAIL_PUFF_R0 + 15, RAIL_PUFF_RATE, DoRailPuff), new State(RAIL_PUFF_R0 + 15, 100, DoSuicide), },
			{ new State(RAIL_PUFF_R1 + 0, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R1 + 1, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R1 + 2, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R1 + 3, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R1 + 4, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R1 + 5, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R1 + 6, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R1 + 7, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R1 + 8, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R1 + 9, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R1 + 10, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R1 + 11, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R1 + 12, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R1 + 13, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R1 + 14, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R1 + 15, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R1 + 15, 100, DoSuicide), },
			{ new State(RAIL_PUFF_R2 + 0, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R2 + 1, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R2 + 2, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R2 + 3, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R2 + 4, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R2 + 5, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R2 + 6, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R2 + 7, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R2 + 8, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R2 + 9, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R2 + 10, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R2 + 11, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R2 + 12, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R2 + 13, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R2 + 14, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R2 + 15, RAIL_PUFF_RATE, DoRailPuff),
					new State(RAIL_PUFF_R2 + 15, 100, DoSuicide), } };

	public static final int LASER_PUFF = 3201;
	public static final int LASER_PUFF_RATE = 8;
	public static final State s_LaserPuff[] = { new State(LASER_PUFF + 0, LASER_PUFF_RATE, null),
			new State(LASER_PUFF + 0, 100, DoSuicide), };

	public static final int TRACER = 3201;
	public static final int TRACER_RATE = 6;
	public static final Animator DoTracer = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoTracer(Weapon);
		}
	};

	public static final State s_Tracer[] = { new State(TRACER + 0, TRACER_RATE, DoTracer),
			new State(TRACER + 1, TRACER_RATE, DoTracer), new State(TRACER + 2, TRACER_RATE, DoTracer),
			new State(TRACER + 3, TRACER_RATE, DoTracer), new State(TRACER + 4, TRACER_RATE, DoTracer),
			new State(TRACER + 5, TRACER_RATE, DoTracer), };

	public static final int EMP = 2058;
	public static final int EMP_RATE = 6;
	public static final Animator DoEMP = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoEMP(Weapon);
		}
	};

	public static final State s_EMP[] = { new State(EMP + 0, EMP_RATE, DoEMP), new State(EMP + 1, EMP_RATE, DoEMP),
			new State(EMP + 2, EMP_RATE, DoEMP), };

	public static final Animator DoEMPBurst = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoEMPBurst(Weapon);
		}
	};

	public static final State s_EMPBurst[] = { new State(EMP + 0, EMP_RATE, DoEMPBurst),
			new State(EMP + 1, EMP_RATE, DoEMPBurst), new State(EMP + 2, EMP_RATE, DoEMPBurst), };

	public static final Animator DoVomit = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoVomit(Weapon) != 0;
		}
	}, DoVomitSplash = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoVomitSplash(Weapon) != 0;
		}
	};
	public static final Animator DoFastShrapJumpFall = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoFastShrapJumpFall(Weapon) != 0;
		}
	};

	public static final State s_EMPShrap[] = { new State(EMP + 0, EMP_RATE, DoFastShrapJumpFall),
			new State(EMP + 1, EMP_RATE, DoFastShrapJumpFall), new State(EMP + 2, EMP_RATE, DoFastShrapJumpFall), };

	public static final int TANK_SHELL = 3201;
	public static final int TANK_SHELL_RATE = 6;
	public static final Animator DoTankShell = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoTankShell(Weapon);
		}
	};
	public static final State s_TankShell[] = { new State(TRACER + 0, 200, DoTankShell).setNext(), };

	public static final Animator DoVehicleSmoke = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoVehicleSmoke(Weapon);
		}
	},

			SpawnVehicleSmoke = new Animator() {
				@Override
				public boolean invoke(int Weapon) {
					return SpawnVehicleSmoke(Weapon);
				}
			};
	public static final int VEHICLE_SMOKE_RATE = 18;
	public static final State s_VehicleSmoke[] = { new State(PUFF + 0, VEHICLE_SMOKE_RATE, DoVehicleSmoke),
			new State(PUFF + 1, VEHICLE_SMOKE_RATE, DoVehicleSmoke),
			new State(PUFF + 2, VEHICLE_SMOKE_RATE, DoVehicleSmoke),
			new State(PUFF + 3, VEHICLE_SMOKE_RATE, DoVehicleSmoke),
			new State(PUFF + 4, VEHICLE_SMOKE_RATE, DoVehicleSmoke),
			new State(PUFF + 5, VEHICLE_SMOKE_RATE, DoVehicleSmoke), new State(PUFF + 5, 100, DoSuicide).setNext(), };

	public static final Animator DoWaterSmoke = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoWaterSmoke(Weapon);
		}
	};

	public static final int WATER_SMOKE_RATE = 18;
	public static final State s_WaterSmoke[] = { new State(PUFF + 0, WATER_SMOKE_RATE, DoWaterSmoke),
			new State(PUFF + 1, WATER_SMOKE_RATE, DoWaterSmoke), new State(PUFF + 2, WATER_SMOKE_RATE, DoWaterSmoke),
			new State(PUFF + 3, WATER_SMOKE_RATE, DoWaterSmoke), new State(PUFF + 4, WATER_SMOKE_RATE, DoWaterSmoke),
			new State(PUFF + 5, WATER_SMOKE_RATE, DoWaterSmoke), new State(PUFF + 5, 100, DoSuicide).setNext(), };

	private static final Animator DoUziSmoke = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoUziSmoke(Weapon) != 0;
		}
	};

	public static final int UZI_SPARK_REPEAT = 24;
	public static final int UZI_SMOKE_REPEAT = 24; // Was 32
	public static final int UZI_SMOKE_RATE = 16; // Was 9
	public static final State s_UziSmoke[] = { new State(UZI_SMOKE + 0, UZI_SMOKE_RATE, DoUziSmoke),
			new State(UZI_SMOKE + 1, UZI_SMOKE_RATE, DoUziSmoke), new State(UZI_SMOKE + 2, UZI_SMOKE_RATE, DoUziSmoke),
			new State(UZI_SMOKE + 3, UZI_SMOKE_RATE, DoUziSmoke), new State(UZI_SMOKE + 3, 100, DoSuicide), };

	public static final int SHOTGUN_SMOKE_RATE = 16;
	public static final int SHOTGUN_SMOKE_REPEAT = 18; // Was 32
	public static final int SHOTGUN_SMOKE = UZI_SMOKE + 1;
	public static final Animator DoShotgunSmoke = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoShotgunSmoke(Weapon) != 0;
		}
	};
	public static final State s_ShotgunSmoke[] = { new State(UZI_SMOKE + 0, SHOTGUN_SMOKE_RATE, DoShotgunSmoke),
			new State(UZI_SMOKE + 1, SHOTGUN_SMOKE_RATE, DoShotgunSmoke),
			new State(UZI_SMOKE + 2, SHOTGUN_SMOKE_RATE, DoShotgunSmoke),
			new State(UZI_SMOKE + 3, SHOTGUN_SMOKE_RATE, DoShotgunSmoke), new State(UZI_SMOKE + 3, 100, DoSuicide), };

	public static final int UZI_BULLET_RATE = 100;
	public static final int UZI_BULLET = 717; // actually a bubble
	public static final Animator DoUziBullet = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoUziBullet(Weapon);
		}
	};

	public static final State s_UziBullet[] = { new State(UZI_BULLET + 0, UZI_BULLET_RATE, DoUziBullet).setNext(), };

	public static final int UZI_SPARK_RATE = 8;

	public static final State s_UziSpark[] = { new State(UZI_SPARK + 0, UZI_SPARK_RATE, null),
			new State(UZI_SPARK + 1, UZI_SPARK_RATE, null), new State(UZI_SPARK + 2, UZI_SPARK_RATE, null),
			new State(UZI_SPARK + 3, UZI_SPARK_RATE, null), new State(UZI_SPARK + 4, UZI_SPARK_RATE, null),
			new State(UZI_SPARK + 4, 100, DoSuicide), };

	public static final State s_UziPowerSpark[] = { new State(UZI_SPARK + 0, UZI_SPARK_RATE, DoUziSmoke),
			new State(UZI_SPARK + 1, UZI_SPARK_RATE, DoUziSmoke), new State(UZI_SPARK + 2, UZI_SPARK_RATE, DoUziSmoke),
			new State(UZI_SPARK + 3, UZI_SPARK_RATE, DoUziSmoke), new State(UZI_SPARK + 4, UZI_SPARK_RATE, DoUziSmoke),
			new State(UZI_SPARK + 4, 100, DoSuicide), };

	public static final int BUBBLE = 716;
	public static final int BUBBLE_RATE = 100;
	public static final Animator DoBubble = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoBubble(Weapon);
		}
	};

	public static final State s_Bubble[] = { new State(BUBBLE + 0, BUBBLE_RATE, DoBubble).setNext(), };

	public static final int SPLASH = 772;
	public static final int SPLASH_RATE = 10;

	public static final State s_Splash[] = { new State(SPLASH + 0, SPLASH_RATE, null),
			new State(SPLASH + 1, SPLASH_RATE, null), new State(SPLASH + 2, SPLASH_RATE, null),
			new State(SPLASH + 3, SPLASH_RATE, null), new State(SPLASH + 4, SPLASH_RATE, null),
			new State(SPLASH + 4, 100, DoSuicide), };

	public static final int CROSSBOLT = 2230;
	public static final int CROSSBOLT_RATE = 100;

	private static final Animator DoCrossBolt = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoCrossBolt(Weapon);
		}
	};

	public static final State s_CrossBolt[][] = { { new State(CROSSBOLT + 0, CROSSBOLT_RATE, DoCrossBolt).setNext(), },
			{ new State(CROSSBOLT + 2, CROSSBOLT_RATE, DoCrossBolt).setNext(), },
			{ new State(CROSSBOLT + 3, CROSSBOLT_RATE, DoCrossBolt).setNext(), },
			{ new State(CROSSBOLT + 4, CROSSBOLT_RATE, DoCrossBolt).setNext(), },
			{ new State(CROSSBOLT + 1, CROSSBOLT_RATE, DoCrossBolt).setNext(), } };

	public static final int STAR = 2102;
	public static final int STAR_RATE = 6;

	private static final Animator DoStar = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoStar(Weapon);
		}
	};

	public static final Animator SpawnShrapX = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return SpawnShrapX(Weapon) != 0;
		}
	};
	public static final Animator DoSectorExp = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoSectorExp(Weapon) != 0;
		}
	};

	public static final State s_Star[] = { new State(STAR + 0, STAR_RATE, DoStar),
			new State(STAR + 1, STAR_RATE, DoStar), new State(STAR + 2, STAR_RATE, DoStar),
			new State(STAR + 3, STAR_RATE, DoStar), };

	public static final State s_StarStuck[] = { new State(STAR + 0, STAR_RATE, null).setNext(), };

	public static final int STAR_DOWN = 2066;
	public static final State s_StarDown[] = { new State(STAR_DOWN + 0, STAR_RATE, DoStar),
			new State(STAR_DOWN + 1, STAR_RATE, DoStar), new State(STAR_DOWN + 2, STAR_RATE, DoStar),
			new State(STAR_DOWN + 3, STAR_RATE, DoStar), };

	public static final State s_StarDownStuck[] = { new State(STAR + 0, STAR_RATE, null).setNext(), };

//////////////////////
//
//LAVA BOSS
//
//////////////////////

	public static final int LAVA_BOULDER_RATE = 6;
	public static final Animator DoLavaBoulder = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoLavaBoulder(Weapon);
		}
	},

			DoShrapDamage = new Animator() {
				@Override
				public boolean invoke(int Weapon) {
					return DoShrapDamage(Weapon) != 0;
				}
			},

			DoVulcanBoulder = new Animator() {
				@Override
				public boolean invoke(int Weapon) {
					return DoVulcanBoulder(Weapon);
				}
			};

	public static final State s_LavaBoulder[] = { new State(LAVA_BOULDER + 1, LAVA_BOULDER_RATE, DoLavaBoulder),
			new State(LAVA_BOULDER + 2, LAVA_BOULDER_RATE, DoLavaBoulder),
			new State(LAVA_BOULDER + 3, LAVA_BOULDER_RATE, DoLavaBoulder),
			new State(LAVA_BOULDER + 4, LAVA_BOULDER_RATE, DoLavaBoulder),
			new State(LAVA_BOULDER + 5, LAVA_BOULDER_RATE, DoLavaBoulder),
			new State(LAVA_BOULDER + 6, LAVA_BOULDER_RATE, DoLavaBoulder),
			new State(LAVA_BOULDER + 7, LAVA_BOULDER_RATE, DoLavaBoulder),
			new State(LAVA_BOULDER + 8, LAVA_BOULDER_RATE, DoLavaBoulder), };

	public static final int LAVA_SHARD = (LAVA_BOULDER + 1);

	public static final State s_LavaShard[] = { new State(LAVA_BOULDER + 1, LAVA_BOULDER_RATE, DoShrapDamage),
			new State(LAVA_BOULDER + 2, LAVA_BOULDER_RATE, DoShrapDamage),
			new State(LAVA_BOULDER + 3, LAVA_BOULDER_RATE, DoShrapDamage),
			new State(LAVA_BOULDER + 4, LAVA_BOULDER_RATE, DoShrapDamage),
			new State(LAVA_BOULDER + 5, LAVA_BOULDER_RATE, DoShrapDamage),
			new State(LAVA_BOULDER + 6, LAVA_BOULDER_RATE, DoShrapDamage),
			new State(LAVA_BOULDER + 7, LAVA_BOULDER_RATE, DoShrapDamage),
			new State(LAVA_BOULDER + 8, LAVA_BOULDER_RATE, DoShrapDamage), };

	public static final State s_VulcanBoulder[] = { new State(LAVA_BOULDER + 1, LAVA_BOULDER_RATE, DoVulcanBoulder),
			new State(LAVA_BOULDER + 2, LAVA_BOULDER_RATE, DoVulcanBoulder),
			new State(LAVA_BOULDER + 3, LAVA_BOULDER_RATE, DoVulcanBoulder),
			new State(LAVA_BOULDER + 4, LAVA_BOULDER_RATE, DoVulcanBoulder),
			new State(LAVA_BOULDER + 5, LAVA_BOULDER_RATE, DoVulcanBoulder),
			new State(LAVA_BOULDER + 6, LAVA_BOULDER_RATE, DoVulcanBoulder),
			new State(LAVA_BOULDER + 7, LAVA_BOULDER_RATE, DoVulcanBoulder),
			new State(LAVA_BOULDER + 8, LAVA_BOULDER_RATE, DoVulcanBoulder), };

//////////////////////
//
//GRENADE
//
//////////////////////

	public static final int GRENADE_FRAMES = 1;
	public static final int GRENADE_R0 = 2110;
	public static final int GRENADE_R1 = GRENADE_R0 + (GRENADE_FRAMES * 1);
	public static final int GRENADE_R2 = GRENADE_R0 + (GRENADE_FRAMES * 2);
	public static final int GRENADE_R3 = GRENADE_R0 + (GRENADE_FRAMES * 3);
	public static final int GRENADE_R4 = GRENADE_R0 + (GRENADE_FRAMES * 4);

	public static final int GRENADE = GRENADE_R0;
	public static final int GRENADE_RATE = 8;
	public static final Animator DoGrenade = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoGrenade(Weapon);
		}
	};

	public static final State s_Grenade[][] = { { new State(GRENADE_R0 + 0, GRENADE_RATE, DoGrenade).setNext(), },
			{ new State(GRENADE_R1 + 0, GRENADE_RATE, DoGrenade).setNext(), },
			{ new State(GRENADE_R2 + 0, GRENADE_RATE, DoGrenade).setNext(), },
			{ new State(GRENADE_R3 + 0, GRENADE_RATE, DoGrenade).setNext(), },
			{ new State(GRENADE_R4 + 0, GRENADE_RATE, DoGrenade).setNext(), } };

//////////////////////
//
//MINE
//
//////////////////////

	public static final Animator DoMine = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoMine(Weapon);
		}
	},

			DoMineStuck = new Animator() {
				@Override
				public boolean invoke(int Weapon) {
					return DoMineStuck(Weapon);
				}
			};

	public static final int MINE = 2223;
	public static final int MINE_SHRAP = 5011;
	public static final int MINE_RATE = 16;

	public static final State s_MineStuck[] = { new State(MINE + 0, MINE_RATE, DoMineStuck).setNext(), };

	public static final State s_Mine[] = { new State(MINE + 0, MINE_RATE, DoMine),
			new State(MINE + 1, MINE_RATE, DoMine), };

	public static final Animator DoMineSpark = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoMineSpark(Weapon) != 0;
		}
	};
	public static final State s_MineSpark[] = { new State(UZI_SPARK + 0, UZI_SPARK_RATE, DoMineSpark),
			new State(UZI_SPARK + 1, UZI_SPARK_RATE, DoMineSpark),
			new State(UZI_SPARK + 2, UZI_SPARK_RATE, DoMineSpark),
			new State(UZI_SPARK + 3, UZI_SPARK_RATE, DoMineSpark),
			new State(UZI_SPARK + 4, UZI_SPARK_RATE, DoMineSpark), new State(UZI_SPARK + 4, 100, DoSuicide), };

//////////////////////
//
//METEOR
//
//////////////////////

	public static final int METEOR_R0 = 2098;
	public static final int METEOR_R1 = 2090;
	public static final int METEOR_R2 = 2094;
	public static final int METEOR_R3 = 2090;
	public static final int METEOR_R4 = 2098;

	public static final int METEOR = STAR;
	public static final int METEOR_RATE = 8;
	public static final Animator DoMeteor = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoMeteor(Weapon);
		}
	};

	public static final State s_Meteor[][] = {
			{ new State(METEOR_R0 + 0, METEOR_RATE, DoMeteor), new State(METEOR_R0 + 1, METEOR_RATE, DoMeteor),
					new State(METEOR_R0 + 2, METEOR_RATE, DoMeteor), new State(METEOR_R0 + 3, METEOR_RATE, DoMeteor), },
			{ new State(METEOR_R1 + 0, METEOR_RATE, DoMeteor), new State(METEOR_R1 + 1, METEOR_RATE, DoMeteor),
					new State(METEOR_R1 + 2, METEOR_RATE, DoMeteor), new State(METEOR_R1 + 3, METEOR_RATE, DoMeteor), },
			{ new State(METEOR_R2 + 0, METEOR_RATE, DoMeteor), new State(METEOR_R2 + 1, METEOR_RATE, DoMeteor),
					new State(METEOR_R2 + 2, METEOR_RATE, DoMeteor), new State(METEOR_R2 + 3, METEOR_RATE, DoMeteor), },
			{ new State(METEOR_R3 + 0, METEOR_RATE, DoMeteor), new State(METEOR_R3 + 1, METEOR_RATE, DoMeteor),
					new State(METEOR_R3 + 2, METEOR_RATE, DoMeteor), new State(METEOR_R3 + 3, METEOR_RATE, DoMeteor), },
			{ new State(METEOR_R4 + 0, METEOR_RATE, DoMeteor), new State(METEOR_R4 + 1, METEOR_RATE, DoMeteor),
					new State(METEOR_R4 + 2, METEOR_RATE, DoMeteor),
					new State(METEOR_R4 + 3, METEOR_RATE, DoMeteor), } };

	public static final int METEOR_EXP = 2115;
	public static final int METEOR_EXP_RATE = 7;

	public static final State s_MeteorExp[] = { new State(METEOR_EXP + 0, METEOR_EXP_RATE, null),
			new State(METEOR_EXP + 1, METEOR_EXP_RATE, null), new State(METEOR_EXP + 2, METEOR_EXP_RATE, null),
			new State(METEOR_EXP + 3, METEOR_EXP_RATE, null), new State(METEOR_EXP + 4, METEOR_EXP_RATE, null),
			new State(METEOR_EXP + 5, METEOR_EXP_RATE, null),
			new State(METEOR_EXP + 5, METEOR_EXP_RATE, DoSuicide).setNext(), };

	public static final int MIRV_METEOR = METEOR_R0;
	public static final Animator DoMirvMissile = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoMirvMissile(Weapon);
		}
	};
	public static final State s_MirvMeteor[][] = { { new State(METEOR_R0 + 0, METEOR_RATE, DoMirvMissile),
			new State(METEOR_R0 + 1, METEOR_RATE, DoMirvMissile), new State(METEOR_R0 + 2, METEOR_RATE, DoMirvMissile),
			new State(METEOR_R0 + 3, METEOR_RATE, DoMirvMissile), },
			{ new State(METEOR_R1 + 0, METEOR_RATE, DoMirvMissile),
					new State(METEOR_R1 + 1, METEOR_RATE, DoMirvMissile),
					new State(METEOR_R1 + 2, METEOR_RATE, DoMirvMissile),
					new State(METEOR_R1 + 3, METEOR_RATE, DoMirvMissile), },
			{ new State(METEOR_R2 + 0, METEOR_RATE, DoMirvMissile),
					new State(METEOR_R2 + 1, METEOR_RATE, DoMirvMissile),
					new State(METEOR_R2 + 2, METEOR_RATE, DoMirvMissile),
					new State(METEOR_R2 + 3, METEOR_RATE, DoMirvMissile), },
			{ new State(METEOR_R3 + 0, METEOR_RATE, DoMirvMissile),
					new State(METEOR_R3 + 1, METEOR_RATE, DoMirvMissile),
					new State(METEOR_R3 + 2, METEOR_RATE, DoMirvMissile),
					new State(METEOR_R3 + 3, METEOR_RATE, DoMirvMissile), },
			{ new State(METEOR_R4 + 0, METEOR_RATE, DoMirvMissile),
					new State(METEOR_R4 + 1, METEOR_RATE, DoMirvMissile),
					new State(METEOR_R4 + 2, METEOR_RATE, DoMirvMissile),
					new State(METEOR_R4 + 3, METEOR_RATE, DoMirvMissile), } };

	public static final State s_MirvMeteorExp[] = { new State(METEOR_EXP + 0, METEOR_EXP_RATE, null),
			new State(METEOR_EXP + 1, METEOR_EXP_RATE, null), new State(METEOR_EXP + 2, METEOR_EXP_RATE, null),
			new State(METEOR_EXP + 3, METEOR_EXP_RATE, null), new State(METEOR_EXP + 4, METEOR_EXP_RATE, null),
			new State(METEOR_EXP + 5, METEOR_EXP_RATE, null),
			new State(METEOR_EXP + 5, METEOR_EXP_RATE, DoSuicide).setNext(), };

	public static final int SERP_METEOR = METEOR_R0 + 1;
	public static final Animator DoSerpMeteor = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoSerpMeteor(Weapon);
		}
	};
	public static final State s_SerpMeteor[][] = {
			{ new State(2031 + 0, METEOR_RATE, DoSerpMeteor), new State(2031 + 1, METEOR_RATE, DoSerpMeteor),
					new State(2031 + 2, METEOR_RATE, DoSerpMeteor), new State(2031 + 3, METEOR_RATE, DoSerpMeteor), },
			{ new State(2031 + 0, METEOR_RATE, DoSerpMeteor), new State(2031 + 1, METEOR_RATE, DoSerpMeteor),
					new State(2031 + 2, METEOR_RATE, DoSerpMeteor), new State(2031 + 3, METEOR_RATE, DoSerpMeteor), },
			{ new State(2031 + 0, METEOR_RATE, DoSerpMeteor), new State(2031 + 1, METEOR_RATE, DoSerpMeteor),
					new State(2031 + 2, METEOR_RATE, DoSerpMeteor), new State(2031 + 3, METEOR_RATE, DoSerpMeteor), },
			{ new State(2031 + 0, METEOR_RATE, DoSerpMeteor), new State(2031 + 1, METEOR_RATE, DoSerpMeteor),
					new State(2031 + 2, METEOR_RATE, DoSerpMeteor), new State(2031 + 3, METEOR_RATE, DoSerpMeteor), },
			{ new State(2031 + 0, METEOR_RATE, DoSerpMeteor), new State(2031 + 1, METEOR_RATE, DoSerpMeteor),
					new State(2031 + 2, METEOR_RATE, DoSerpMeteor), new State(2031 + 3, METEOR_RATE, DoSerpMeteor), } };

	public static final State s_SerpMeteorExp[] = { new State(METEOR_EXP + 0, METEOR_EXP_RATE, null),
			new State(METEOR_EXP + 1, METEOR_EXP_RATE, null), new State(METEOR_EXP + 2, METEOR_EXP_RATE, null),
			new State(METEOR_EXP + 3, METEOR_EXP_RATE, null), new State(METEOR_EXP + 4, METEOR_EXP_RATE, null),
			new State(METEOR_EXP + 5, METEOR_EXP_RATE, null),
			new State(METEOR_EXP + 5, METEOR_EXP_RATE, DoSuicide).setNext(), };

//////////////////////
//
//SPEAR
//
//////////////////////

	public static final int SPEAR_RATE = 8;
	public static final Animator DoSpear = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoSpear(Weapon);
		}
	};

	public static final State s_Spear[][] = { { new State(SPEAR_R0 + 0, SPEAR_RATE, DoSpear).setNext(), },
			{ new State(SPEAR_R1 + 0, SPEAR_RATE, DoSpear).setNext(), },
			{ new State(SPEAR_R2 + 0, SPEAR_RATE, DoSpear).setNext(), },
			{ new State(SPEAR_R3 + 0, SPEAR_RATE, DoSpear).setNext(), },
			{ new State(SPEAR_R4 + 0, SPEAR_RATE, DoSpear).setNext(), } };

//////////////////////
//
//ROCKET
//
//////////////////////

	public static final int ROCKET_FRAMES = 1;
	public static final int ROCKET_R0 = 2206;
	public static final int ROCKET_R1 = ROCKET_R0 + (ROCKET_FRAMES * 2);
	public static final int ROCKET_R2 = ROCKET_R0 + (ROCKET_FRAMES * 3);
	public static final int ROCKET_R3 = ROCKET_R0 + (ROCKET_FRAMES * 4);
	public static final int ROCKET_R4 = ROCKET_R0 + (ROCKET_FRAMES * 1);

	public static final Animator DoRocket = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoRocket(Weapon);
		}
	};
	public static final int ROCKET_RATE = 8;

	public static final State s_Rocket[][] = { { new State(ROCKET_R0 + 0, ROCKET_RATE, DoRocket).setNext(), },
			{ new State(ROCKET_R1 + 0, ROCKET_RATE, DoRocket).setNext(), },
			{ new State(ROCKET_R2 + 0, ROCKET_RATE, DoRocket).setNext(), },
			{ new State(ROCKET_R3 + 0, ROCKET_RATE, DoRocket).setNext(), },
			{ new State(ROCKET_R4 + 0, ROCKET_RATE, DoRocket).setNext(), } };

//////////////////////
//
//BUNNY ROCKET
//
//////////////////////

	public static final int BUNNYROCKET_FRAMES = 5;
	public static final int BUNNYROCKET_R0 = 4550;
	public static final int BUNNYROCKET_R1 = BUNNYROCKET_R0 + (BUNNYROCKET_FRAMES * 1);
	public static final int BUNNYROCKET_R2 = BUNNYROCKET_R0 + (BUNNYROCKET_FRAMES * 2);
	public static final int BUNNYROCKET_R3 = BUNNYROCKET_R0 + (BUNNYROCKET_FRAMES * 3);
	public static final int BUNNYROCKET_R4 = BUNNYROCKET_R0 + (BUNNYROCKET_FRAMES * 4);

	public static final int BUNNYROCKET_RATE = 8;

	public static final State s_BunnyRocket[][] = {
			{ new State(BUNNYROCKET_R0 + 2, BUNNYROCKET_RATE, DoRocket).setNext(), },
			{ new State(BUNNYROCKET_R1 + 2, BUNNYROCKET_RATE, DoRocket).setNext(), },
			{ new State(BUNNYROCKET_R2 + 2, BUNNYROCKET_RATE, DoRocket).setNext(), },
			{ new State(BUNNYROCKET_R3 + 2, BUNNYROCKET_RATE, DoRocket).setNext(), },
			{ new State(BUNNYROCKET_R4 + 2, BUNNYROCKET_RATE, DoRocket).setNext(), } };

	public static final Animator DoRail = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoRail(Weapon);
		}
	};
	public static final int RAIL_RATE = 8;

	public static final State s_Rail[][] = { { new State(ROCKET_R0 + 0, RAIL_RATE, DoRail).setNext(), },
			{ new State(ROCKET_R1 + 0, RAIL_RATE, DoRail).setNext(), },
			{ new State(ROCKET_R2 + 0, RAIL_RATE, DoRail).setNext(), },
			{ new State(ROCKET_R3 + 0, RAIL_RATE, DoRail).setNext(), },
			{ new State(ROCKET_R4 + 0, RAIL_RATE, DoRail).setNext(), } };

	public static final Animator DoLaser = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoLaser(Weapon);
		}
	};
	public static final int LASER_RATE = 8;

	public static final State s_Laser[] = { new State(ROCKET_R0 + 0, LASER_RATE, DoLaser).setNext(), };

//////////////////////
//
//MICRO
//
//////////////////////

	public static final int MICRO_FRAMES = 1;
	public static final int MICRO_R0 = 2206;
	public static final int MICRO_R1 = MICRO_R0 + (MICRO_FRAMES * 2);
	public static final int MICRO_R2 = MICRO_R0 + (MICRO_FRAMES * 3);
	public static final int MICRO_R3 = MICRO_R0 + (MICRO_FRAMES * 4);
	public static final int MICRO_R4 = MICRO_R0 + (MICRO_FRAMES * 1);

	public static final Animator DoMicro = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoMicro(Weapon);
		}
	};
	public static final int MICRO_RATE = 8;

	public static final State s_Micro[][] = { { new State(MICRO_R0 + 0, MICRO_RATE, DoMicro).setNext(), },
			{ new State(MICRO_R1 + 0, MICRO_RATE, DoMicro).setNext(), },
			{ new State(MICRO_R2 + 0, MICRO_RATE, DoMicro).setNext(), },
			{ new State(MICRO_R3 + 0, MICRO_RATE, DoMicro).setNext(), },
			{ new State(MICRO_R4 + 0, MICRO_RATE, DoMicro).setNext(), } };

	public static final Animator DoMicroMini = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoMicroMini(Weapon);
		}
	};
	public static final State s_MicroMini[][] = { { new State(MICRO_R0 + 0, MICRO_RATE, DoMicroMini).setNext(), },
			{ new State(MICRO_R1 + 0, MICRO_RATE, DoMicroMini).setNext(), },
			{ new State(MICRO_R2 + 0, MICRO_RATE, DoMicroMini).setNext(), },
			{ new State(MICRO_R3 + 0, MICRO_RATE, DoMicroMini).setNext(), },
			{ new State(MICRO_R4 + 0, MICRO_RATE, DoMicroMini).setNext(), } };

//////////////////////
//
//BOLT THINMAN
//
//////////////////////

	public static final int BOLT_THINMAN_RATE = 8;
	public static final Animator DoBoltThinMan = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoBoltThinMan(Weapon);
		}
	};

	public static final State s_BoltThinMan[][] = {
			{ new State(BOLT_THINMAN_R0 + 0, BOLT_THINMAN_RATE, DoBoltThinMan).setNext(), },
			{ new State(BOLT_THINMAN_R1 + 0, BOLT_THINMAN_RATE, DoBoltThinMan).setNext(), },
			{ new State(BOLT_THINMAN_R2 + 0, BOLT_THINMAN_RATE, DoBoltThinMan).setNext(), },
			{ new State(BOLT_THINMAN_R3 + 0, BOLT_THINMAN_RATE, DoBoltThinMan).setNext(), },
			{ new State(BOLT_THINMAN_R4 + 0, BOLT_THINMAN_RATE, DoBoltThinMan).setNext(), } };

	public static final int BOLT_SEEKER_RATE = 8;
	public static final Animator DoBoltSeeker = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoBoltSeeker(Weapon);
		}
	};

	public static final State s_BoltSeeker[][] = {
			{ new State(BOLT_THINMAN_R0 + 0, BOLT_SEEKER_RATE, DoBoltSeeker).setNext(), },
			{ new State(BOLT_THINMAN_R1 + 0, BOLT_SEEKER_RATE, DoBoltSeeker).setNext(), },
			{ new State(BOLT_THINMAN_R2 + 0, BOLT_SEEKER_RATE, DoBoltSeeker).setNext(), },
			{ new State(BOLT_THINMAN_R3 + 0, BOLT_SEEKER_RATE, DoBoltSeeker).setNext(), },
			{ new State(BOLT_THINMAN_R4 + 0, BOLT_SEEKER_RATE, DoBoltSeeker).setNext(), } };

	public static final int BOLT_FATMAN = STAR;
	public static final int BOLT_FATMAN_RATE = 8;
	public static final Animator DoBoltFatMan = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoBoltFatMan(Weapon) != 0;
		}
	};

	public static final State s_BoltFatMan[] = { new State(BOLT_FATMAN + 0, BOLT_FATMAN_RATE, DoBoltFatMan),
			new State(BOLT_FATMAN + 1, BOLT_FATMAN_RATE, DoBoltFatMan),
			new State(BOLT_FATMAN + 2, BOLT_FATMAN_RATE, DoBoltFatMan),
			new State(BOLT_FATMAN + 3, BOLT_FATMAN_RATE, DoBoltFatMan), };

	public static final int BOLT_SHRAPNEL = STAR;
	public static final int BOLT_SHRAPNEL_RATE = 8;
	public static final Animator DoBoltShrapnel = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoBoltShrapnel(Weapon) != 0;
		}
	};

	public static final State s_BoltShrapnel[] = { new State(BOLT_SHRAPNEL + 0, BOLT_SHRAPNEL_RATE, DoBoltShrapnel),
			new State(BOLT_SHRAPNEL + 1, BOLT_SHRAPNEL_RATE, DoBoltShrapnel),
			new State(BOLT_SHRAPNEL + 2, BOLT_SHRAPNEL_RATE, DoBoltShrapnel),
			new State(BOLT_SHRAPNEL + 3, BOLT_SHRAPNEL_RATE, DoBoltShrapnel), };

	public static final int COOLG_FIRE = 2430;
//public static final int COOLG_FIRE =1465;
	public static final int COOLG_FIRE_RATE = 8;
	public static final Animator DoCoolgFire = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoCoolgFire(Weapon);
		}
	};

	public static final State s_CoolgFire[] = { new State(2031 + 0, COOLG_FIRE_RATE, DoCoolgFire),
			new State(2031 + 1, COOLG_FIRE_RATE, DoCoolgFire), new State(2031 + 2, COOLG_FIRE_RATE, DoCoolgFire),
			new State(2031 + 3, COOLG_FIRE_RATE, DoCoolgFire), };

	public static final int COOLG_FIRE_DONE = 2410;
	public static final int COOLG_FIRE_DONE_RATE = 3;

	public static final State s_CoolgFireDone[] = { new State(COOLG_FIRE_DONE + 0, COOLG_FIRE_DONE_RATE, null),
			new State(COOLG_FIRE_DONE + 1, COOLG_FIRE_DONE_RATE, null),
			new State(COOLG_FIRE_DONE + 2, COOLG_FIRE_DONE_RATE, null),
			new State(COOLG_FIRE_DONE + 3, COOLG_FIRE_DONE_RATE, null),
			new State(COOLG_FIRE_DONE + 4, COOLG_FIRE_DONE_RATE, null),
			new State(COOLG_FIRE_DONE + 4, COOLG_FIRE_DONE_RATE, DoSuicide).setNext(), };

	public static final Animator DoCoolgDrip = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoCoolgDrip(Weapon) != 0;
		}
	};
	public static final int COOLG_DRIP = 1720;
	public static final State s_CoolgDrip[] = { new State(COOLG_DRIP + 0, 100, DoCoolgDrip).setNext(), };

	public static final int GORE_FLOOR_SPLASH_RATE = 8;
	public static final int GORE_FLOOR_SPLASH = 1710;
	public static final State s_GoreFloorSplash[] = { new State(GORE_FLOOR_SPLASH + 0, GORE_FLOOR_SPLASH_RATE, null),
			new State(GORE_FLOOR_SPLASH + 1, GORE_FLOOR_SPLASH_RATE, null),
			new State(GORE_FLOOR_SPLASH + 2, GORE_FLOOR_SPLASH_RATE, null),
			new State(GORE_FLOOR_SPLASH + 3, GORE_FLOOR_SPLASH_RATE, null),
			new State(GORE_FLOOR_SPLASH + 4, GORE_FLOOR_SPLASH_RATE, null),
			new State(GORE_FLOOR_SPLASH + 5, GORE_FLOOR_SPLASH_RATE, null),
			new State(GORE_FLOOR_SPLASH + 5, GORE_FLOOR_SPLASH_RATE, DoSuicide).setNext(), };

	public static final int GORE_SPLASH_RATE = 8;
	public static final int GORE_SPLASH = 2410;
	public static final State s_GoreSplash[] = { new State(GORE_SPLASH + 0, GORE_SPLASH_RATE, null),
			new State(GORE_SPLASH + 1, GORE_SPLASH_RATE, null), new State(GORE_SPLASH + 2, GORE_SPLASH_RATE, null),
			new State(GORE_SPLASH + 3, GORE_SPLASH_RATE, null), new State(GORE_SPLASH + 4, GORE_SPLASH_RATE, null),
			new State(GORE_SPLASH + 5, GORE_SPLASH_RATE, null),
			new State(GORE_SPLASH + 5, GORE_SPLASH_RATE, DoSuicide).setNext(), };

//////////////////////////////////////////////
//
//HEART ATTACK & PLASMA
//
//////////////////////////////////////////////

	public static final int PLASMA = 1562; // 2058
	public static final int PLASMA_FOUNTAIN = 2058 + 1;
	public static final int PLASMA_RATE = 8;
	public static final int PLASMA_FOUNTAIN_TIME = (3 * 120);;

	public static final Animator DoPlasma = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoPlasma(Weapon);
		}
	};

//regular bolt from heart
	public static final State s_Plasma[] = { new State(PLASMA + 0, PLASMA_RATE, DoPlasma),
			new State(PLASMA + 1, PLASMA_RATE, DoPlasma), new State(PLASMA + 2, PLASMA_RATE, DoPlasma), };

	public static final Animator DoPlasmaFountain = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoPlasmaFountain(Weapon) != 0;
		}
	};

//follows actor spewing blood
	public static final int PLASMA_Drip = 1562; // 2420
	public static final State s_PlasmaFountain[] = { new State(PLASMA_Drip + 0, PLASMA_RATE, DoPlasmaFountain),
			new State(PLASMA_Drip + 1, PLASMA_RATE, DoPlasmaFountain),
			new State(PLASMA_Drip + 2, PLASMA_RATE, DoPlasmaFountain),
			new State(PLASMA_Drip + 3, PLASMA_RATE, DoPlasmaFountain), };

	public static final int PLASMA_Drip_RATE = 12;

	private static final Animator DoShrapJumpFall = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoShrapJumpFall(Weapon) != 0;
		}
	};

	public static final State s_PlasmaDrip[] = { new State(PLASMA_Drip + 0, PLASMA_Drip_RATE, DoShrapJumpFall),
			new State(PLASMA_Drip + 1, PLASMA_Drip_RATE, DoShrapJumpFall),
			new State(PLASMA_Drip + 2, PLASMA_Drip_RATE, DoShrapJumpFall),
			new State(PLASMA_Drip + 3, PLASMA_Drip_RATE, DoShrapJumpFall),
			new State(PLASMA_Drip + 4, PLASMA_Drip_RATE, DoShrapJumpFall),
			new State(PLASMA_Drip + 5, PLASMA_Drip_RATE, DoShrapJumpFall),
			new State(PLASMA_Drip + 7, PLASMA_Drip_RATE, DoSuicide).setNext(), };

	public static final int PLASMA_DONE = 2061;
	public static final int PLASMA_DONE_RATE = 15;

	public static final Animator DoPlasmaDone = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoPlasmaDone(Weapon) != 0;
		}
	};

	public static final State s_PlasmaDone[] = { new State(PLASMA + 0, PLASMA_DONE_RATE, DoPlasmaDone),
			new State(PLASMA + 2, PLASMA_DONE_RATE, DoPlasmaDone),
			new State(PLASMA + 1, PLASMA_DONE_RATE, DoPlasmaDone), };

	public static final int TELEPORT_EFFECT = 3240;
	public static final int TELEPORT_EFFECT_RATE = 6;

	public static final State s_TeleportEffect[] = { new State(TELEPORT_EFFECT + 0, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 1, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 2, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 3, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 4, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 5, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 6, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 7, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 8, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 9, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 10, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 11, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 12, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 13, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 14, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 15, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 16, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 17, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 17, TELEPORT_EFFECT_RATE, DoSuicide).setNext(), };

//Spawn a RIPPER teleport effect
	public static final Animator DoTeleRipper = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			DoTeleRipper(Weapon);
			return false;
		}
	};
	public static final State s_TeleportEffect2[] = { new State(TELEPORT_EFFECT + 0, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 1, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 2, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 3, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 4, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 5, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 6, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 7, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 8, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 9, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 10, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 11, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 12, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 13, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 14, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 15, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 16, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 17, TELEPORT_EFFECT_RATE, null),
			new State(TELEPORT_EFFECT + 17, SF_QUICK_CALL, DoTeleRipper),
			new State(TELEPORT_EFFECT + 17, TELEPORT_EFFECT_RATE, DoSuicide).setNext(), };

	public static final Animator DoElectro = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoElectro(Weapon);
		}
	};
	public static final int ELECTRO_SNAKE = 2073;
	public static final int ELECTRO_PLAYER = (ELECTRO);
	public static final int ELECTRO_ENEMY = (ELECTRO + 1);
	public static final int ELECTRO_SHARD = (ELECTRO + 2);

	public static final State s_Electro[] = { new State(ELECTRO + 0, 12, DoElectro),
			new State(ELECTRO + 1, 12, DoElectro), new State(ELECTRO + 2, 12, DoElectro),
			new State(ELECTRO + 3, 12, DoElectro), };

	public static final State s_ElectroShrap[] = { new State(ELECTRO + 0, 12, DoShrapDamage),
			new State(ELECTRO + 1, 12, DoShrapDamage), new State(ELECTRO + 2, 12, DoShrapDamage),
			new State(ELECTRO + 3, 12, DoShrapDamage), };

//////////////////////
//
//EXPS
//
//////////////////////
	public static final int GRENADE_EXP = 3121;
	public static final int GRENADE_EXP_RATE = 6;

	public static final State s_GrenadeSmallExp[] = { new State(GRENADE_EXP + 0, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 1, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 2, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 3, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 4, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 5, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 6, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 7, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 8, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 9, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 10, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 11, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 12, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 13, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 14, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 15, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 16, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 17, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 18, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 19, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 20, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 21, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 21, 100, DoSuicide).setNext(), };

	public static final Animator SpawnGrenadeSmallExp = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return SpawnGrenadeSmallExp(Weapon) != 0;
		}
	};
	public static final State s_GrenadeExp[] = { new State(GRENADE_EXP + 0, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 1, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 2, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 3, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 4, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 5, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 6, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 6, SF_QUICK_CALL, SpawnGrenadeSmallExp),
			new State(GRENADE_EXP + 7, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 8, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 9, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 10, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 11, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 12, SF_QUICK_CALL, SpawnGrenadeSmallExp),
			new State(GRENADE_EXP + 12, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 13, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 14, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 15, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 16, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 17, SF_QUICK_CALL, SpawnGrenadeSmallExp),
			new State(GRENADE_EXP + 17, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 18, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 19, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 20, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 21, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 21, 100, DoSuicide).setNext(), };

	public static final int MINE_EXP = GRENADE_EXP + 1;
	public static final Animator DoMineExp = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoMineExp(Weapon) != 0;
		}
	}, DoMineExpMine = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoMineExpMine(Weapon) != 0;
		}
	};
	public static final State s_MineExp[] = { new State(GRENADE_EXP + 0, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 1, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 2, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 3, 0 | SF_QUICK_CALL, DoMineExp),
			new State(GRENADE_EXP + 3, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 4, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 5, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 6, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 7, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 8, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 9, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 10, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 11, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 12, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 13, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 14, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 15, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 16, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 17, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 17, 0 | SF_QUICK_CALL, DoMineExpMine),
			new State(GRENADE_EXP + 18, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 19, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 20, GRENADE_EXP_RATE, null), new State(GRENADE_EXP + 21, GRENADE_EXP_RATE, null),
			new State(GRENADE_EXP + 21, 100, DoSuicide).setNext(), };

	public static final int EXP_RATE = 7;
	public static final int BOLT_EXP = EXP;
	public static final int FIREBALL_EXP = EXP + 1;
	public static final int BASIC_EXP = EXP + 2;
	public static final int SECTOR_EXP = EXP + 3;
	public static final int MICRO_EXP = EXP + 5;
	public static final int TRACER_EXP = EXP + 6;
	public static final int TANK_SHELL_EXP = EXP + 7;

	public static final State s_BasicExp[] = { new State(EXP + 0, EXP_RATE, null), new State(EXP + 1, EXP_RATE, null),
			new State(EXP + 2, EXP_RATE, null), new State(EXP + 3, EXP_RATE, null), new State(EXP + 4, EXP_RATE, null),
			new State(EXP + 5, EXP_RATE, null), new State(EXP + 6, EXP_RATE, null), new State(EXP + 7, EXP_RATE, null),
			new State(EXP + 8, EXP_RATE, null), new State(EXP + 9, EXP_RATE, null), new State(EXP + 10, EXP_RATE, null),
			new State(EXP + 11, EXP_RATE, null), new State(EXP + 12, EXP_RATE, null),
			new State(EXP + 13, EXP_RATE, null), new State(EXP + 14, EXP_RATE, null),
			new State(EXP + 15, EXP_RATE, null), new State(EXP + 16, EXP_RATE, null),
			new State(EXP + 17, EXP_RATE, null), new State(EXP + 18, EXP_RATE, null),
			new State(EXP + 19, EXP_RATE, null), new State(EXP + 20, 100, DoSuicide), };

	public static final int MICRO_EXP_RATE = 3;
	public static final Animator DoExpDamageTest = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoExpDamageTest(Weapon) != 0;
		}
	};

	public static final State s_MicroExp[] = { new State(EXP + 0, MICRO_EXP_RATE, null),
			new State(EXP + 0, SF_QUICK_CALL, DoExpDamageTest), new State(EXP + 1, MICRO_EXP_RATE, null),
			new State(EXP + 2, MICRO_EXP_RATE, null), new State(EXP + 3, MICRO_EXP_RATE, null),
			new State(EXP + 4, MICRO_EXP_RATE, null), new State(EXP + 5, MICRO_EXP_RATE, null),
			new State(EXP + 6, MICRO_EXP_RATE, null), new State(EXP + 7, MICRO_EXP_RATE, null),
			new State(EXP + 8, MICRO_EXP_RATE, null), new State(EXP + 9, MICRO_EXP_RATE, null),
			new State(EXP + 10, MICRO_EXP_RATE, null), new State(EXP + 11, MICRO_EXP_RATE, null),
			new State(EXP + 12, MICRO_EXP_RATE, null), new State(EXP + 13, MICRO_EXP_RATE, null),
			new State(EXP + 14, MICRO_EXP_RATE, null), new State(EXP + 15, MICRO_EXP_RATE, null),
			new State(EXP + 16, MICRO_EXP_RATE, null), new State(EXP + 17, MICRO_EXP_RATE, null),
			new State(EXP + 18, MICRO_EXP_RATE, null), new State(EXP + 19, MICRO_EXP_RATE, null),
			new State(EXP + 20, MICRO_EXP_RATE, null), new State(EXP + 20, 100, DoSuicide).setNext(), };

	public static final int BIG_GUN_FLAME_RATE = 15;
	public static final State s_BigGunFlame[] = {
// first 3 frames
			new State(EXP + 0, BIG_GUN_FLAME_RATE, null), new State(EXP + 1, BIG_GUN_FLAME_RATE, null),
			new State(EXP + 2, BIG_GUN_FLAME_RATE, null),
// last 4 frames frames
			new State(EXP + 17, BIG_GUN_FLAME_RATE, null), new State(EXP + 18, BIG_GUN_FLAME_RATE, null),
			new State(EXP + 19, BIG_GUN_FLAME_RATE, null), new State(EXP + 20, BIG_GUN_FLAME_RATE, null),
			new State(EXP + 20, 100, DoSuicide), };

	public static final State s_BoltExp[] = { new State(EXP + 0, EXP_RATE, null),
			new State(EXP + 0, SF_QUICK_CALL, null), new State(EXP + 0, SF_QUICK_CALL, SpawnShrapX),
			new State(EXP + 1, EXP_RATE, null), new State(EXP + 2, EXP_RATE, null), new State(EXP + 3, EXP_RATE, null),
			new State(EXP + 4, EXP_RATE, null), new State(EXP + 5, EXP_RATE, null), new State(EXP + 6, EXP_RATE, null),
			new State(EXP + 7, EXP_RATE, null), new State(EXP + 7, SF_QUICK_CALL, SpawnShrapX),
			new State(EXP + 8, EXP_RATE, null), new State(EXP + 9, EXP_RATE, null), new State(EXP + 10, EXP_RATE, null),
			new State(EXP + 11, EXP_RATE, null), new State(EXP + 12, EXP_RATE, null),
			new State(EXP + 13, EXP_RATE, null), new State(EXP + 14, EXP_RATE, null),
			new State(EXP + 15, EXP_RATE, null), new State(EXP + 16, EXP_RATE, null),
			new State(EXP + 17, EXP_RATE, null), new State(EXP + 18, EXP_RATE, null),
			new State(EXP + 19, EXP_RATE, null), new State(EXP + 20, EXP_RATE, null),
			new State(EXP + 20, 100, DoSuicide), };

	public static final State s_TankShellExp[] = { new State(EXP + 0, EXP_RATE, null),
			new State(EXP + 0, SF_QUICK_CALL, null), new State(EXP + 0, SF_QUICK_CALL, SpawnShrapX),
			new State(EXP + 1, EXP_RATE, null), new State(EXP + 2, EXP_RATE, null), new State(EXP + 3, EXP_RATE, null),
			new State(EXP + 4, EXP_RATE, null), new State(EXP + 5, EXP_RATE, null), new State(EXP + 6, EXP_RATE, null),
			new State(EXP + 7, EXP_RATE, null), new State(EXP + 7, SF_QUICK_CALL, SpawnShrapX),
			new State(EXP + 8, EXP_RATE, null), new State(EXP + 9, EXP_RATE, null), new State(EXP + 10, EXP_RATE, null),
			new State(EXP + 11, EXP_RATE, null), new State(EXP + 12, EXP_RATE, null),
			new State(EXP + 13, EXP_RATE, null), new State(EXP + 14, EXP_RATE, null),
			new State(EXP + 15, EXP_RATE, null), new State(EXP + 16, EXP_RATE, null),
			new State(EXP + 17, EXP_RATE, null), new State(EXP + 18, EXP_RATE, null),
			new State(EXP + 19, EXP_RATE, null), new State(EXP + 20, EXP_RATE, null),
			new State(EXP + 20, 100, DoSuicide), };

	public static final int TRACER_EXP_RATE = 4;
	public static final State s_TracerExp[] = { new State(EXP + 0, TRACER_EXP_RATE, null),
			new State(EXP + 0, SF_QUICK_CALL, null), new State(EXP + 0, SF_QUICK_CALL, null),
			new State(EXP + 1, TRACER_EXP_RATE, null), new State(EXP + 2, TRACER_EXP_RATE, null),
			new State(EXP + 3, TRACER_EXP_RATE, null), new State(EXP + 4, TRACER_EXP_RATE, null),
			new State(EXP + 5, TRACER_EXP_RATE, null), new State(EXP + 6, TRACER_EXP_RATE, null),
			new State(EXP + 7, TRACER_EXP_RATE, null), new State(EXP + 7, SF_QUICK_CALL, null),
			new State(EXP + 8, TRACER_EXP_RATE, null), new State(EXP + 9, TRACER_EXP_RATE, null),
			new State(EXP + 10, TRACER_EXP_RATE, null), new State(EXP + 11, TRACER_EXP_RATE, null),
			new State(EXP + 12, TRACER_EXP_RATE, null), new State(EXP + 13, TRACER_EXP_RATE, null),
			new State(EXP + 14, TRACER_EXP_RATE, null), new State(EXP + 15, TRACER_EXP_RATE, null),
			new State(EXP + 16, TRACER_EXP_RATE, null), new State(EXP + 17, TRACER_EXP_RATE, null),
			new State(EXP + 18, TRACER_EXP_RATE, null), new State(EXP + 19, TRACER_EXP_RATE, null),
			new State(EXP + 20, TRACER_EXP_RATE, null), new State(EXP + 20, 100, DoSuicide), };

	public static final State s_SectorExp[] = { new State(EXP + 0, EXP_RATE, DoSectorExp),
			new State(EXP + 0, SF_QUICK_CALL, SpawnShrapX), new State(EXP + 0, SF_QUICK_CALL, DoSectorExp),
			new State(EXP + 1, EXP_RATE, DoSectorExp), new State(EXP + 2, EXP_RATE, DoSectorExp),
			new State(EXP + 3, EXP_RATE, DoSectorExp), new State(EXP + 4, EXP_RATE, DoSectorExp),
			new State(EXP + 5, EXP_RATE, DoSectorExp), new State(EXP + 6, EXP_RATE, DoSectorExp),
			new State(EXP + 7, EXP_RATE, DoSectorExp), new State(EXP + 7, SF_QUICK_CALL, DoSectorExp),
			new State(EXP + 8, EXP_RATE, DoSectorExp), new State(EXP + 9, EXP_RATE, DoSectorExp),
			new State(EXP + 10, EXP_RATE, DoSectorExp), new State(EXP + 11, EXP_RATE, DoSectorExp),
			new State(EXP + 12, EXP_RATE, DoSectorExp), new State(EXP + 13, EXP_RATE, DoSectorExp),
			new State(EXP + 14, EXP_RATE, DoSectorExp), new State(EXP + 15, EXP_RATE, DoSectorExp),
			new State(EXP + 16, EXP_RATE, DoSectorExp), new State(EXP + 17, EXP_RATE, DoSectorExp),
			new State(EXP + 18, EXP_RATE, DoSectorExp), new State(EXP + 19, EXP_RATE, DoSectorExp),
			new State(EXP + 20, EXP_RATE, DoSectorExp), new State(EXP + 20, 100, DoSuicide), };

	public static final int FIREBALL_DISS = 3196;
	public static final int FIREBALL_DISS_RATE = 8;
	public static final State s_FireballExp[] = { new State(FIREBALL_DISS + 0, FIREBALL_DISS_RATE, null),
			new State(FIREBALL_DISS + 1, FIREBALL_DISS_RATE, null),
			new State(FIREBALL_DISS + 2, FIREBALL_DISS_RATE, null),
			new State(FIREBALL_DISS + 3, FIREBALL_DISS_RATE, null),
			new State(FIREBALL_DISS + 4, FIREBALL_DISS_RATE, null), new State(FIREBALL_DISS + 4, 100, DoSuicide), };

	public static final int NAP_EXP = (3072);
	public static final int NAP_EXP_RATE = 6;

	public static final Animator DoDamageTest = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoDamageTest(Weapon) != 0;
		}
	};

	public static final State s_NapExp[] = { new State(NAP_EXP + 0, NAP_EXP_RATE, null),
			new State(NAP_EXP + 0, 0 | SF_QUICK_CALL, DoDamageTest), new State(NAP_EXP + 1, NAP_EXP_RATE, null),
			new State(NAP_EXP + 2, NAP_EXP_RATE, null), new State(NAP_EXP + 3, NAP_EXP_RATE, null),
			new State(NAP_EXP + 4, NAP_EXP_RATE, null), new State(NAP_EXP + 5, NAP_EXP_RATE, null),
			new State(NAP_EXP + 6, NAP_EXP_RATE, null), new State(NAP_EXP + 7, NAP_EXP_RATE, null),
			new State(NAP_EXP + 8, NAP_EXP_RATE, null), new State(NAP_EXP + 9, NAP_EXP_RATE, null),
			new State(NAP_EXP + 10, NAP_EXP_RATE, null), new State(NAP_EXP + 11, NAP_EXP_RATE, null),
			new State(NAP_EXP + 12, NAP_EXP_RATE, null), new State(NAP_EXP + 13, NAP_EXP_RATE, null),
			new State(NAP_EXP + 14, NAP_EXP_RATE, null), new State(NAP_EXP + 15, NAP_EXP_RATE - 2, null),
			new State(NAP_EXP + 16, NAP_EXP_RATE - 2, null), new State(NAP_EXP + 17, NAP_EXP_RATE - 2, null),
			new State(NAP_EXP + 18, NAP_EXP_RATE - 2, null), new State(NAP_EXP + 19, NAP_EXP_RATE - 2, null),
			new State(NAP_EXP + 20, NAP_EXP_RATE - 2, null), new State(NAP_EXP + 21, NAP_EXP_RATE - 2, null),
			new State(NAP_EXP + 21, NAP_EXP_RATE - 2, DoSuicide).setNext(), };

	public static final Animator DoFireballFlames = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoFireballFlames(Weapon) != 0;
		}
	};
	public static final int FLAME_RATE = 6;

	public static final State s_FireballFlames[] = { new State(FIREBALL_FLAMES + 0, FLAME_RATE, DoFireballFlames),
			new State(FIREBALL_FLAMES + 1, FLAME_RATE, DoFireballFlames),
			new State(FIREBALL_FLAMES + 2, FLAME_RATE, DoFireballFlames),
			new State(FIREBALL_FLAMES + 3, FLAME_RATE, DoFireballFlames),
			new State(FIREBALL_FLAMES + 4, FLAME_RATE, DoFireballFlames),
			new State(FIREBALL_FLAMES + 5, FLAME_RATE, DoFireballFlames),
			new State(FIREBALL_FLAMES + 6, FLAME_RATE, DoFireballFlames),
			new State(FIREBALL_FLAMES + 7, FLAME_RATE, DoFireballFlames),
			new State(FIREBALL_FLAMES + 8, FLAME_RATE, DoFireballFlames),
			new State(FIREBALL_FLAMES + 9, FLAME_RATE, DoFireballFlames),
			new State(FIREBALL_FLAMES + 10, FLAME_RATE, DoFireballFlames),
			new State(FIREBALL_FLAMES + 11, FLAME_RATE, DoFireballFlames),
			new State(FIREBALL_FLAMES + 12, FLAME_RATE, DoFireballFlames),
			new State(FIREBALL_FLAMES + 13, FLAME_RATE, DoFireballFlames), };

	public static final Animator DoBreakFlames = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoBreakFlames(Weapon) != 0;
		}
	};

	public static final State s_BreakFlames[] = { new State(FIREBALL_FLAMES + 0, FLAME_RATE, DoBreakFlames),
			new State(FIREBALL_FLAMES + 1, FLAME_RATE, DoBreakFlames),
			new State(FIREBALL_FLAMES + 2, FLAME_RATE, DoBreakFlames),
			new State(FIREBALL_FLAMES + 3, FLAME_RATE, DoBreakFlames),
			new State(FIREBALL_FLAMES + 4, FLAME_RATE, DoBreakFlames),
			new State(FIREBALL_FLAMES + 5, FLAME_RATE, DoBreakFlames),
			new State(FIREBALL_FLAMES + 6, FLAME_RATE, DoBreakFlames),
			new State(FIREBALL_FLAMES + 7, FLAME_RATE, DoBreakFlames),
			new State(FIREBALL_FLAMES + 8, FLAME_RATE, DoBreakFlames),
			new State(FIREBALL_FLAMES + 9, FLAME_RATE, DoBreakFlames),
			new State(FIREBALL_FLAMES + 10, FLAME_RATE, DoBreakFlames),
			new State(FIREBALL_FLAMES + 11, FLAME_RATE, DoBreakFlames),
			new State(FIREBALL_FLAMES + 12, FLAME_RATE, DoBreakFlames),
			new State(FIREBALL_FLAMES + 13, FLAME_RATE, DoBreakFlames), };

//////////////////////
//
//FIREBALL
//
//////////////////////

	public static final Animator DoFireball = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoFireball(Weapon);
		}
	};

	public static final int FIREBALL_RATE = 8;
	public static final int GORO_FIREBALL = FIREBALL + 1;

	public static final State s_Fireball[] = { new State(FIREBALL + 0, 12, DoFireball),
			new State(FIREBALL + 1, 12, DoFireball), new State(FIREBALL + 2, 12, DoFireball),
			new State(FIREBALL + 3, 12, DoFireball) };

    public static final Animator DoRing = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoRing(Weapon);
		}
	};

	public static final State s_Ring[] = { new State(FIREBALL + 0, 12, DoRing), new State(FIREBALL + 1, 12, DoRing),
			new State(FIREBALL + 2, 12, DoRing), new State(FIREBALL + 3, 12, DoRing), };

	public static final State s_Ring2[] = { new State(2031 + 0, 12, DoRing), new State(2031 + 1, 12, DoRing),
			new State(2031 + 2, 12, DoRing), new State(2031 + 3, 12, DoRing), };

	public static final Animator DoNapalm = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoNapalm(Weapon);
		}
	};

	public static final State s_Napalm[] = { new State(FIREBALL + 0, 12, DoNapalm),
			new State(FIREBALL + 1, 12, DoNapalm), new State(FIREBALL + 2, 12, DoNapalm),
			new State(FIREBALL + 3, 12, DoNapalm), };

	public static final Animator DoBloodWorm = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoBloodWorm(Weapon);
		}
	};

	public static final int BLOOD_WORM = FIREBALL + 5;
	public static final State s_BloodWorm[] = { new State(FIREBALL + 0, 12, DoBloodWorm),
			new State(FIREBALL + 1, 12, DoBloodWorm), new State(FIREBALL + 2, 12, DoBloodWorm),
			new State(FIREBALL + 3, 12, DoBloodWorm), };

	public static final int PLASMA_EXP = (NAP_EXP + 1);
	public static final int PLASMA_EXP_RATE = 4;

	public static final State s_PlasmaExp[] = { new State(PLASMA_EXP + 0, PLASMA_EXP_RATE, null),
			new State(PLASMA_EXP + 0, 0 | SF_QUICK_CALL, DoDamageTest),
			new State(PLASMA_EXP + 1, PLASMA_EXP_RATE, null), new State(PLASMA_EXP + 2, PLASMA_EXP_RATE, null),
			new State(PLASMA_EXP + 3, PLASMA_EXP_RATE, null), new State(PLASMA_EXP + 4, PLASMA_EXP_RATE, null),
			new State(PLASMA_EXP + 5, PLASMA_EXP_RATE, null), new State(PLASMA_EXP + 6, PLASMA_EXP_RATE, null),
			new State(PLASMA_EXP + 7, PLASMA_EXP_RATE, null), new State(PLASMA_EXP + 8, PLASMA_EXP_RATE, null),
			new State(PLASMA_EXP + 9, PLASMA_EXP_RATE, null), new State(PLASMA_EXP + 10, PLASMA_EXP_RATE, null),
			new State(PLASMA_EXP + 11, PLASMA_EXP_RATE, null), new State(PLASMA_EXP + 9, PLASMA_EXP_RATE, null),
			new State(PLASMA_EXP + 8, PLASMA_EXP_RATE, null), new State(PLASMA_EXP + 7, PLASMA_EXP_RATE, null),
			new State(PLASMA_EXP + 6, PLASMA_EXP_RATE - 2, null), new State(PLASMA_EXP + 5, PLASMA_EXP_RATE - 2, null),
			new State(PLASMA_EXP + 4, PLASMA_EXP_RATE - 2, null), new State(PLASMA_EXP + 3, PLASMA_EXP_RATE - 2, null),
			new State(PLASMA_EXP + 2, PLASMA_EXP_RATE - 2, null), new State(PLASMA_EXP + 1, PLASMA_EXP_RATE - 2, null),
			new State(PLASMA_EXP + 1, PLASMA_EXP_RATE - 2, null),
			new State(PLASMA_EXP + 1, PLASMA_EXP_RATE - 2, DoSuicide).setNext(), };

	public static final Animator DoMirv = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoMirv(Weapon);
		}
	};

	public static final State s_Mirv[] = { new State(FIREBALL + 0, 12, DoMirv), new State(FIREBALL + 1, 12, DoMirv),
			new State(FIREBALL + 2, 12, DoMirv), new State(FIREBALL + 3, 12, DoMirv), };

	public static final State s_MirvMissile[] = { new State(FIREBALL + 0, 12, DoMirvMissile),
			new State(FIREBALL + 1, 12, DoMirvMissile), new State(FIREBALL + 2, 12, DoMirvMissile),
			new State(FIREBALL + 3, 12, DoMirvMissile), };

	public static final int Vomit1 = 1719;
	public static final int Vomit2 = 1721;
	public static final int VomitSplash = 1711;
	public static final int Vomit_RATE = 16;

	public static final State s_Vomit1[] = { new State(Vomit1 + 0, Vomit_RATE, DoVomit).setNext(), };

	public static final State s_Vomit2[] = { new State(Vomit2 + 0, Vomit_RATE, DoVomit).setNext(), };

	public static final State s_VomitSplash[] = { new State(VomitSplash + 0, Vomit_RATE, DoVomitSplash).setNext(), };

	public static final int GORE_Head = 1670;
	public static final int GORE_Head_RATE = 16;

	public static final State s_GoreHead[] = { new State(GORE_Head + 0, GORE_Head_RATE, DoShrapJumpFall),
			new State(GORE_Head + 1, GORE_Head_RATE, DoShrapJumpFall),
			new State(GORE_Head + 2, GORE_Head_RATE, DoShrapJumpFall),
			new State(GORE_Head + 3, GORE_Head_RATE, DoShrapJumpFall),
			new State(GORE_Head + 4, GORE_Head_RATE, DoShrapJumpFall),
			new State(GORE_Head + 5, GORE_Head_RATE, DoShrapJumpFall),
			new State(GORE_Head + 6, GORE_Head_RATE, DoShrapJumpFall),
			new State(GORE_Head + 7, GORE_Head_RATE, DoShrapJumpFall),
			new State(GORE_Head + 8, GORE_Head_RATE, DoShrapJumpFall),
			new State(GORE_Head + 9, GORE_Head_RATE, DoShrapJumpFall),
			new State(GORE_Head + 10, GORE_Head_RATE, DoShrapJumpFall),
			new State(GORE_Head + 11, GORE_Head_RATE, DoShrapJumpFall), };

	public static final int GORE_Leg = 1689;
	public static final int GORE_Leg_RATE = 16;

	public static final State s_GoreLeg[] = { new State(GORE_Leg + 0, GORE_Leg_RATE, DoShrapJumpFall),
			new State(GORE_Leg + 1, GORE_Leg_RATE, DoShrapJumpFall),
			new State(GORE_Leg + 2, GORE_Leg_RATE, DoShrapJumpFall), };

	public static final int GORE_Eye = 1692;
	public static final int GORE_Eye_RATE = 16;

	public static final State s_GoreEye[] = { new State(GORE_Eye + 0, GORE_Eye_RATE, DoShrapJumpFall),
			new State(GORE_Eye + 1, GORE_Eye_RATE, DoShrapJumpFall),
			new State(GORE_Eye + 2, GORE_Eye_RATE, DoShrapJumpFall),
			new State(GORE_Eye + 3, GORE_Eye_RATE, DoShrapJumpFall), };

	public static final int GORE_Torso = 1696;
	public static final int GORE_Torso_RATE = 16;

	public static final State s_GoreTorso[] = { new State(GORE_Torso + 0, GORE_Torso_RATE, DoShrapJumpFall),
			new State(GORE_Torso + 1, GORE_Torso_RATE, DoShrapJumpFall),
			new State(GORE_Torso + 2, GORE_Torso_RATE, DoShrapJumpFall),
			new State(GORE_Torso + 3, GORE_Torso_RATE, DoShrapJumpFall),
			new State(GORE_Torso + 4, GORE_Torso_RATE, DoShrapJumpFall),
			new State(GORE_Torso + 5, GORE_Torso_RATE, DoShrapJumpFall),
			new State(GORE_Torso + 6, GORE_Torso_RATE, DoShrapJumpFall),
			new State(GORE_Torso + 7, GORE_Torso_RATE, DoShrapJumpFall), };

	public static final int GORE_Arm = 1550;
	public static final int GORE_Arm_RATE = 16;

	public static final State s_GoreArm[] = { new State(GORE_Arm + 0, GORE_Arm_RATE, DoShrapJumpFall),
			new State(GORE_Arm + 1, GORE_Arm_RATE, DoShrapJumpFall),
			new State(GORE_Arm + 2, GORE_Arm_RATE, DoShrapJumpFall),
			new State(GORE_Arm + 3, GORE_Arm_RATE, DoShrapJumpFall),
			new State(GORE_Arm + 4, GORE_Arm_RATE, DoShrapJumpFall),
			new State(GORE_Arm + 5, GORE_Arm_RATE, DoShrapJumpFall),
			new State(GORE_Arm + 6, GORE_Arm_RATE, DoShrapJumpFall),
			new State(GORE_Arm + 7, GORE_Arm_RATE, DoShrapJumpFall),
			new State(GORE_Arm + 8, GORE_Arm_RATE, DoShrapJumpFall),
			new State(GORE_Arm + 9, GORE_Arm_RATE, DoShrapJumpFall),
			new State(GORE_Arm + 10, GORE_Arm_RATE, DoShrapJumpFall),
			new State(GORE_Arm + 11, GORE_Arm_RATE, DoShrapJumpFall), };

	public static final int GORE_Lung = 903;
	public static final int GORE_Lung_RATE = 16;

	public static final State s_GoreLung[] = { new State(GORE_Lung + 0, GORE_Lung_RATE, DoShrapJumpFall),
			new State(GORE_Lung + 1, GORE_Lung_RATE, DoShrapJumpFall),
			new State(GORE_Lung + 2, GORE_Lung_RATE, DoShrapJumpFall),
			new State(GORE_Lung + 3, GORE_Lung_RATE, DoShrapJumpFall),
			new State(GORE_Lung + 4, GORE_Lung_RATE, DoShrapJumpFall),
			new State(GORE_Lung + 5, GORE_Lung_RATE, DoShrapJumpFall),
			new State(GORE_Lung + 6, GORE_Lung_RATE, DoShrapJumpFall),
			new State(GORE_Lung + 7, GORE_Lung_RATE, DoShrapJumpFall),
			new State(GORE_Lung + 8, GORE_Lung_RATE, DoShrapJumpFall),
			new State(GORE_Lung + 9, GORE_Lung_RATE, DoShrapJumpFall),
			new State(GORE_Lung + 10, GORE_Lung_RATE, DoShrapJumpFall),
			new State(GORE_Lung + 11, GORE_Lung_RATE, DoShrapJumpFall), };

	public static final int GORE_Liver = 918;
	public static final int GORE_Liver_RATE = 16;

	public static final State s_GoreLiver[] = { new State(GORE_Liver + 0, GORE_Liver_RATE, DoShrapJumpFall),
			new State(GORE_Liver + 1, GORE_Liver_RATE, DoShrapJumpFall),
			new State(GORE_Liver + 2, GORE_Liver_RATE, DoShrapJumpFall),
			new State(GORE_Liver + 3, GORE_Liver_RATE, DoShrapJumpFall),
			new State(GORE_Liver + 4, GORE_Liver_RATE, DoShrapJumpFall),
			new State(GORE_Liver + 5, GORE_Liver_RATE, DoShrapJumpFall),
			new State(GORE_Liver + 6, GORE_Liver_RATE, DoShrapJumpFall),
			new State(GORE_Liver + 7, GORE_Liver_RATE, DoShrapJumpFall),
			new State(GORE_Liver + 8, GORE_Liver_RATE, DoShrapJumpFall),
			new State(GORE_Liver + 9, GORE_Liver_RATE, DoShrapJumpFall),
			new State(GORE_Liver + 10, GORE_Liver_RATE, DoShrapJumpFall),
			new State(GORE_Liver + 11, GORE_Liver_RATE, DoShrapJumpFall), };

	public static final int GORE_SkullCap = 933;
	public static final int GORE_SkullCap_RATE = 16;

	public static final State s_GoreSkullCap[] = { new State(GORE_SkullCap + 0, GORE_SkullCap_RATE, DoShrapJumpFall),
			new State(GORE_SkullCap + 1, GORE_SkullCap_RATE, DoShrapJumpFall),
			new State(GORE_SkullCap + 2, GORE_SkullCap_RATE, DoShrapJumpFall),
			new State(GORE_SkullCap + 3, GORE_SkullCap_RATE, DoShrapJumpFall),
			new State(GORE_SkullCap + 4, GORE_SkullCap_RATE, DoShrapJumpFall),
			new State(GORE_SkullCap + 5, GORE_SkullCap_RATE, DoShrapJumpFall),
			new State(GORE_SkullCap + 6, GORE_SkullCap_RATE, DoShrapJumpFall),
			new State(GORE_SkullCap + 7, GORE_SkullCap_RATE, DoShrapJumpFall),
			new State(GORE_SkullCap + 8, GORE_SkullCap_RATE, DoShrapJumpFall),
			new State(GORE_SkullCap + 9, GORE_SkullCap_RATE, DoShrapJumpFall),
			new State(GORE_SkullCap + 10, GORE_SkullCap_RATE, DoShrapJumpFall),
			new State(GORE_SkullCap + 11, GORE_SkullCap_RATE, DoShrapJumpFall), };

	public static final int GORE_ChunkS = 2430;
	public static final int GORE_ChunkS_RATE = 16;

	public static final State s_GoreChunkS[] = { new State(GORE_ChunkS + 0, GORE_ChunkS_RATE, DoShrapJumpFall),
			new State(GORE_ChunkS + 1, GORE_ChunkS_RATE, DoShrapJumpFall),
			new State(GORE_ChunkS + 2, GORE_ChunkS_RATE, DoShrapJumpFall),
			new State(GORE_ChunkS + 3, GORE_ChunkS_RATE, DoShrapJumpFall), };

	public static final int GORE_Drip = 1562; // 2430
	public static final int GORE_Drip_RATE = 16;

	public static final State s_GoreDrip[] = { new State(GORE_Drip + 0, GORE_Drip_RATE, DoShrapJumpFall),
			new State(GORE_Drip + 1, GORE_Drip_RATE, DoShrapJumpFall),
			new State(GORE_Drip + 2, GORE_Drip_RATE, DoShrapJumpFall),
			new State(GORE_Drip + 3, GORE_Drip_RATE, DoShrapJumpFall), };

	public static final State s_FastGoreDrip[] = { new State(GORE_Drip + 0, GORE_Drip_RATE, DoFastShrapJumpFall),
			new State(GORE_Drip + 1, GORE_Drip_RATE, DoFastShrapJumpFall),
			new State(GORE_Drip + 2, GORE_Drip_RATE, DoFastShrapJumpFall),
			new State(GORE_Drip + 3, GORE_Drip_RATE, DoFastShrapJumpFall), };

///////////////////////////////////////////////
//
//This GORE mostly for the Accursed Heads
//
///////////////////////////////////////////////

	public static final int GORE_Flame = 847;
	public static final int GORE_Flame_RATE = 8;

	public static final State s_GoreFlame[] = { new State(GORE_Flame + 0, GORE_Flame_RATE, DoFastShrapJumpFall),
			new State(GORE_Flame + 1, GORE_Flame_RATE, DoFastShrapJumpFall),
			new State(GORE_Flame + 2, GORE_Flame_RATE, DoFastShrapJumpFall),
			new State(GORE_Flame + 3, GORE_Flame_RATE, DoFastShrapJumpFall),
			new State(GORE_Flame + 4, GORE_Flame_RATE, DoFastShrapJumpFall),
			new State(GORE_Flame + 5, GORE_Flame_RATE, DoFastShrapJumpFall),
			new State(GORE_Flame + 6, GORE_Flame_RATE, DoFastShrapJumpFall),
			new State(GORE_Flame + 7, GORE_Flame_RATE, DoFastShrapJumpFall), };

	public static final Animator DoTracerShrap = new Animator() {
		@Override
		public boolean invoke(int Weapon) {
			return DoTracerShrap(Weapon) != 0;
		}
	};
	public static final State s_TracerShrap[] = { new State(GORE_Flame + 0, GORE_Flame_RATE, DoTracerShrap),
			new State(GORE_Flame + 1, GORE_Flame_RATE, DoTracerShrap),
			new State(GORE_Flame + 2, GORE_Flame_RATE, DoTracerShrap),
			new State(GORE_Flame + 3, GORE_Flame_RATE, DoTracerShrap),
			new State(GORE_Flame + 4, GORE_Flame_RATE, DoTracerShrap),
			new State(GORE_Flame + 5, GORE_Flame_RATE, DoTracerShrap),
			new State(GORE_Flame + 6, GORE_Flame_RATE, DoTracerShrap),
			new State(GORE_Flame + 7, GORE_Flame_RATE, DoTracerShrap), };

	public static final int UZI_SHELL = 2152;
	public static final int UZISHELL_RATE = 8;
//public static final Animator DoShellShrap;
	public static final State s_UziShellShrap[] = { new State(UZI_SHELL + 0, UZISHELL_RATE, DoShrapJumpFall),
			new State(UZI_SHELL + 1, UZISHELL_RATE, DoShrapJumpFall),
			new State(UZI_SHELL + 2, UZISHELL_RATE, DoShrapJumpFall),
			new State(UZI_SHELL + 3, UZISHELL_RATE, DoShrapJumpFall),
			new State(UZI_SHELL + 4, UZISHELL_RATE, DoShrapJumpFall),
			new State(UZI_SHELL + 5, UZISHELL_RATE, DoShrapJumpFall), };

	public static final State s_UziShellShrapStill1[] = { new State(UZI_SHELL + 0, UZISHELL_RATE, null).setNext(), };
	public static final State s_UziShellShrapStill2[] = { new State(UZI_SHELL + 1, UZISHELL_RATE, null).setNext(), };
	public static final State s_UziShellShrapStill3[] = { new State(UZI_SHELL + 2, UZISHELL_RATE, null).setNext(), };
	public static final State s_UziShellShrapStill4[] = { new State(UZI_SHELL + 3, UZISHELL_RATE, null).setNext(), };
	public static final State s_UziShellShrapStill5[] = { new State(UZI_SHELL + 4, UZISHELL_RATE, null).setNext(), };
	public static final State s_UziShellShrapStill6[] = { new State(UZI_SHELL + 5, UZISHELL_RATE, null).setNext(), };

	public static final int SHOT_SHELL = 2180;
	public static final int SHOTSHELL_RATE = 8;
	public static final State s_ShotgunShellShrap[] = { new State(SHOT_SHELL + 0, SHOTSHELL_RATE, DoShrapJumpFall),
			new State(SHOT_SHELL + 1, SHOTSHELL_RATE, DoShrapJumpFall),
			new State(SHOT_SHELL + 2, SHOTSHELL_RATE, DoShrapJumpFall),
			new State(SHOT_SHELL + 3, SHOTSHELL_RATE, DoShrapJumpFall),
			new State(SHOT_SHELL + 4, SHOTSHELL_RATE, DoShrapJumpFall),
			new State(SHOT_SHELL + 5, SHOTSHELL_RATE, DoShrapJumpFall),
			new State(SHOT_SHELL + 6, SHOTSHELL_RATE, DoShrapJumpFall),
			new State(SHOT_SHELL + 7, SHOTSHELL_RATE, DoShrapJumpFall), };

	public static final State s_ShotgunShellShrapStill1[] = {
			new State(SHOT_SHELL + 1, SHOTSHELL_RATE, null).setNext(), };
	public static final State s_ShotgunShellShrapStill2[] = {
			new State(SHOT_SHELL + 3, SHOTSHELL_RATE, null).setNext(), };
	public static final State s_ShotgunShellShrapStill3[] = {
			new State(SHOT_SHELL + 7, SHOTSHELL_RATE, null).setNext(), };

	public static final int GORE_FlameChunkA = 839;
	public static final int GORE_FlameChunkA_RATE = 8;

	public static final State s_GoreFlameChunkA[] = {
			new State(GORE_FlameChunkA + 0, GORE_FlameChunkA_RATE, DoShrapJumpFall),
			new State(GORE_FlameChunkA + 1, GORE_FlameChunkA_RATE, DoShrapJumpFall),
			new State(GORE_FlameChunkA + 2, GORE_FlameChunkA_RATE, DoShrapJumpFall),
			new State(GORE_FlameChunkA + 3, GORE_FlameChunkA_RATE, DoShrapJumpFall), };

	public static final int GORE_FlameChunkB = 843;
	public static final int GORE_FlameChunkB_RATE = 8;

	public static final State s_GoreFlameChunkB[] = {
			new State(GORE_FlameChunkB + 0, GORE_FlameChunkB_RATE, DoShrapJumpFall),
			new State(GORE_FlameChunkB + 1, GORE_FlameChunkB_RATE, DoShrapJumpFall),
			new State(GORE_FlameChunkB + 2, GORE_FlameChunkB_RATE, DoShrapJumpFall),
			new State(GORE_FlameChunkB + 3, GORE_FlameChunkB_RATE, DoShrapJumpFall), };

/////////////////////////////////////////////////////////////////////
//
//General Breaking Shrapnel
//
/////////////////////////////////////////////////////////////////////

	public static final int COIN_SHRAP = 2530;
	public static final int CoinShrap_RATE = 12;

	public static final State s_CoinShrap[] = { new State(COIN_SHRAP + 0, CoinShrap_RATE, DoShrapJumpFall),
			new State(COIN_SHRAP + 1, CoinShrap_RATE, DoShrapJumpFall),
			new State(COIN_SHRAP + 2, CoinShrap_RATE, DoShrapJumpFall),
			new State(COIN_SHRAP + 3, CoinShrap_RATE, DoShrapJumpFall), };

	public static final int MARBEL = 5096;
	public static final int Marbel_RATE = 12;

	public static final State s_Marbel[] = { new State(MARBEL, Marbel_RATE, DoShrapJumpFall).setNext(), };

//
//Glass
//

	public static final int GLASS_SHRAP_A = 3864;
	public static final int GlassShrapA_RATE = 12;

	public static final State s_GlassShrapA[] = { new State(GLASS_SHRAP_A + 0, GlassShrapA_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_A + 1, GlassShrapA_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_A + 2, GlassShrapA_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_A + 3, GlassShrapA_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_A + 4, GlassShrapA_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_A + 5, GlassShrapA_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_A + 6, GlassShrapA_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_A + 7, GlassShrapA_RATE, DoShrapJumpFall), };

	public static final int GLASS_SHRAP_B = 3872;
	public static final int GlassShrapB_RATE = 12;

	public static final State s_GlassShrapB[] = { new State(GLASS_SHRAP_B + 0, GlassShrapB_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_B + 1, GlassShrapB_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_B + 2, GlassShrapB_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_B + 3, GlassShrapB_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_B + 4, GlassShrapB_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_B + 5, GlassShrapB_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_B + 6, GlassShrapB_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_B + 7, GlassShrapB_RATE, DoShrapJumpFall), };

	public static final int GLASS_SHRAP_C = 3880;
	public static final int GlassShrapC_RATE = 12;

	public static final State s_GlassShrapC[] = { new State(GLASS_SHRAP_C + 0, GlassShrapC_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_C + 1, GlassShrapC_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_C + 2, GlassShrapC_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_C + 3, GlassShrapC_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_C + 4, GlassShrapC_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_C + 5, GlassShrapC_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_C + 6, GlassShrapC_RATE, DoShrapJumpFall),
			new State(GLASS_SHRAP_C + 7, GlassShrapC_RATE, DoShrapJumpFall), };

//
//Wood
//

	public static final int WOOD_SHRAP_A = 3924;
	public static final int WoodShrapA_RATE = 12;

	public static final State s_WoodShrapA[] = { new State(WOOD_SHRAP_A + 0, WoodShrapA_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_A + 1, WoodShrapA_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_A + 2, WoodShrapA_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_A + 3, WoodShrapA_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_A + 4, WoodShrapA_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_A + 5, WoodShrapA_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_A + 6, WoodShrapA_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_A + 7, WoodShrapA_RATE, DoShrapJumpFall), };

	public static final int WOOD_SHRAP_B = 3932;
	public static final int WoodShrapB_RATE = 12;

	public static final State s_WoodShrapB[] = { new State(WOOD_SHRAP_B + 0, WoodShrapB_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_B + 1, WoodShrapB_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_B + 2, WoodShrapB_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_B + 3, WoodShrapB_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_B + 4, WoodShrapB_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_B + 5, WoodShrapB_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_B + 6, WoodShrapB_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_B + 7, WoodShrapB_RATE, DoShrapJumpFall), };

	public static final int WOOD_SHRAP_C = 3941;
	public static final int WoodShrapC_RATE = 12;

	public static final State s_WoodShrapC[] = { new State(WOOD_SHRAP_C + 0, WoodShrapC_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_C + 1, WoodShrapC_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_C + 2, WoodShrapC_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_C + 3, WoodShrapC_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_C + 4, WoodShrapC_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_C + 5, WoodShrapC_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_C + 6, WoodShrapC_RATE, DoShrapJumpFall),
			new State(WOOD_SHRAP_C + 7, WoodShrapC_RATE, DoShrapJumpFall), };

//
//Stone
//

	public static final int STONE_SHRAP_A = 3840;
	public static final int StoneShrapA_RATE = 12;

	public static final State s_StoneShrapA[] = { new State(STONE_SHRAP_A + 0, StoneShrapA_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_A + 1, StoneShrapA_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_A + 2, StoneShrapA_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_A + 3, StoneShrapA_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_A + 4, StoneShrapA_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_A + 5, StoneShrapA_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_A + 6, StoneShrapA_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_A + 7, StoneShrapA_RATE, DoShrapJumpFall), };

	public static final int STONE_SHRAP_B = 3848;
	public static final int StoneShrapB_RATE = 12;

	public static final State s_StoneShrapB[] = { new State(STONE_SHRAP_B + 0, StoneShrapB_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_B + 1, StoneShrapB_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_B + 2, StoneShrapB_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_B + 3, StoneShrapB_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_B + 4, StoneShrapB_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_B + 5, StoneShrapB_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_B + 6, StoneShrapB_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_B + 7, StoneShrapB_RATE, DoShrapJumpFall), };

	public static final int STONE_SHRAP_C = 3856;
	public static final int StoneShrapC_RATE = 12;

	public static final State s_StoneShrapC[] = { new State(STONE_SHRAP_C + 0, StoneShrapC_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_C + 1, StoneShrapC_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_C + 2, StoneShrapC_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_C + 3, StoneShrapC_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_C + 4, StoneShrapC_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_C + 5, StoneShrapC_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_C + 6, StoneShrapC_RATE, DoShrapJumpFall),
			new State(STONE_SHRAP_C + 7, StoneShrapC_RATE, DoShrapJumpFall), };

//
//Metal
//

	public static final int METAL_SHRAP_A = 3888;
	public static final int MetalShrapA_RATE = 12;

	public static final State s_MetalShrapA[] = { new State(METAL_SHRAP_A + 0, MetalShrapA_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_A + 1, MetalShrapA_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_A + 2, MetalShrapA_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_A + 3, MetalShrapA_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_A + 4, MetalShrapA_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_A + 5, MetalShrapA_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_A + 6, MetalShrapA_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_A + 7, MetalShrapA_RATE, DoShrapJumpFall), };

	public static final int METAL_SHRAP_B = 3896;
	public static final int MetalShrapB_RATE = 12;

	public static final State s_MetalShrapB[] = { new State(METAL_SHRAP_B + 0, MetalShrapB_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_B + 1, MetalShrapB_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_B + 2, MetalShrapB_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_B + 3, MetalShrapB_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_B + 4, MetalShrapB_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_B + 5, MetalShrapB_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_B + 6, MetalShrapB_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_B + 7, MetalShrapB_RATE, DoShrapJumpFall), };

	public static final int METAL_SHRAP_C = 3904;
	public static final int MetalShrapC_RATE = 12;

	public static final State s_MetalShrapC[] = { new State(METAL_SHRAP_C + 0, MetalShrapC_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_C + 1, MetalShrapC_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_C + 2, MetalShrapC_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_C + 3, MetalShrapC_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_C + 4, MetalShrapC_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_C + 5, MetalShrapC_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_C + 6, MetalShrapC_RATE, DoShrapJumpFall),
			new State(METAL_SHRAP_C + 7, MetalShrapC_RATE, DoShrapJumpFall), };

//
//Paper
//

	public static final int PAPER_SHRAP_A = 3924;
	public static final int PaperShrapA_RATE = 12;

	public static final State s_PaperShrapA[] = { new State(PAPER_SHRAP_A + 0, PaperShrapA_RATE, DoShrapJumpFall),
			new State(PAPER_SHRAP_A + 1, PaperShrapA_RATE, DoShrapJumpFall),
			new State(PAPER_SHRAP_A + 2, PaperShrapA_RATE, DoShrapJumpFall),
			new State(PAPER_SHRAP_A + 3, PaperShrapA_RATE, DoShrapJumpFall), };

	public static final int PAPER_SHRAP_B = 3932;
	public static final int PaperShrapB_RATE = 12;

	public static final State s_PaperShrapB[] = { new State(PAPER_SHRAP_B + 0, PaperShrapB_RATE, DoShrapJumpFall),
			new State(PAPER_SHRAP_B + 1, PaperShrapB_RATE, DoShrapJumpFall),
			new State(PAPER_SHRAP_B + 2, PaperShrapB_RATE, DoShrapJumpFall),
			new State(PAPER_SHRAP_B + 3, PaperShrapB_RATE, DoShrapJumpFall), };

	public static final int PAPER_SHRAP_C = 3941;
	public static final int PaperShrapC_RATE = 12;

	public static final State s_PaperShrapC[] = { new State(PAPER_SHRAP_C + 0, PaperShrapC_RATE, DoShrapJumpFall),
			new State(PAPER_SHRAP_C + 1, PaperShrapC_RATE, DoShrapJumpFall),
			new State(PAPER_SHRAP_C + 2, PaperShrapC_RATE, DoShrapJumpFall),
			new State(PAPER_SHRAP_C + 3, PaperShrapC_RATE, DoShrapJumpFall), };

	public enum WeaponStateGroup implements StateGroup {
		sg_RailPuff(s_RailPuff[0], s_RailPuff[1], s_RailPuff[2], s_RailPuff[1], s_RailPuff[0]),
		sg_CrossBolt(s_CrossBolt[0], s_CrossBolt[1], s_CrossBolt[2], s_CrossBolt[3], s_CrossBolt[4]),
		sg_Grenade(s_Grenade[0], s_Grenade[1], s_Grenade[2], s_Grenade[3], s_Grenade[4]),
		sg_Meteor(s_Meteor[0], s_Meteor[1], s_Meteor[2], s_Meteor[3], s_Meteor[4]),
		sg_MirvMeteor(s_MirvMeteor[0], s_MirvMeteor[1], s_MirvMeteor[2], s_MirvMeteor[3], s_MirvMeteor[4]),
		sg_SerpMeteor(s_SerpMeteor[0], s_SerpMeteor[1], s_SerpMeteor[2], s_SerpMeteor[3], s_SerpMeteor[4]),
		sg_Spear(s_Spear[0], s_Spear[1], s_Spear[2], s_Spear[3], s_Spear[4]),
		sg_Rocket(s_Rocket[0], s_Rocket[1], s_Rocket[2], s_Rocket[3], s_Rocket[4]),
		sg_BunnyRocket(s_BunnyRocket[0], s_BunnyRocket[1], s_BunnyRocket[2], s_BunnyRocket[3], s_BunnyRocket[4]),
		sg_Rail(s_Rail[0], s_Rail[1], s_Rail[2], s_Rail[3], s_Rail[4]),
		sg_Micro(s_Micro[0], s_Micro[1], s_Micro[2], s_Micro[3], s_Micro[4]),
		sg_MicroMini(s_MicroMini[0], s_MicroMini[1], s_MicroMini[2], s_MicroMini[3], s_MicroMini[4]),
		sg_BoltThinMan(s_BoltThinMan[0], s_BoltThinMan[1], s_BoltThinMan[2], s_BoltThinMan[3], s_BoltThinMan[4]),
		sg_BoltSeeker(s_BoltSeeker[0], s_BoltSeeker[1], s_BoltSeeker[2], s_BoltSeeker[3], s_BoltSeeker[4]);

		private final State[][] group;
		private int index = -1;

		WeaponStateGroup(State[]... states) {
			group = states;
		}

		@Override
		public State getState(int rotation, int offset) {
			return group[rotation][offset];
		}

		@Override
		public State getState(int rotation) {
			return group[rotation][0];
		}

		@Override
		public int getLength(int rotation) {
			return group[rotation].length;
		}

		@Override
		public int index() {
			return index;
		}

		@Override
		public void setIndex(int index) {
			this.index = index;
		}
	}

	public static final int FOOTPRINT1 = 2490;
	public static final int FOOTPRINT2 = 2491;
	public static final int FOOTPRINT3 = 2492;
	public static final int FOOTPRINT_RATE = 30;

	public static final State s_FootPrint1[] = { new State(FOOTPRINT1, FOOTPRINT_RATE, null).setNext(), };
	public static final State s_FootPrint2[] = { new State(FOOTPRINT2, FOOTPRINT_RATE, null).setNext(), };
	public static final State s_FootPrint3[] = { new State(FOOTPRINT3, FOOTPRINT_RATE, null).setNext(), };

	public static final int WALLBLOOD1 = 2500;
	public static final int WALLBLOOD2 = 2501;
	public static final int WALLBLOOD3 = 2502;
	public static final int WALLBLOOD4 = 2503;
	public static final int WALLBLOOD_RATE = 30;

	public static final Animator DoWallBlood = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoWallBlood(spr) != 0;
		}
	};

	public static final State s_WallBlood1[] = { new State(WALLBLOOD1, SF_QUICK_CALL, DoWallBlood),
			new State(WALLBLOOD1, WALLBLOOD_RATE, null), };
	public static final State s_WallBlood2[] = { new State(WALLBLOOD2, SF_QUICK_CALL, DoWallBlood),
			new State(WALLBLOOD2, WALLBLOOD_RATE, null), };
	public static final State s_WallBlood3[] = { new State(WALLBLOOD3, SF_QUICK_CALL, DoWallBlood),
			new State(WALLBLOOD3, WALLBLOOD_RATE, null), };
	public static final State s_WallBlood4[] = { new State(WALLBLOOD4, SF_QUICK_CALL, DoWallBlood),
			new State(WALLBLOOD4, WALLBLOOD_RATE, null), };

	public static final int FLOORBLOOD1 = 389;
	public static final int FLOORBLOOD_RATE = 30;

	public static final Animator DoFloorBlood = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoFloorBlood(spr) != 0;
		}
	};

	public static final State s_FloorBlood1[] = { new State(FLOORBLOOD1, SF_QUICK_CALL, DoFloorBlood),
			new State(FLOORBLOOD1, FLOORBLOOD_RATE, null), };

	public static void InitWeaponStates() {
		for (WeaponStateGroup sg : WeaponStateGroup.values()) {
			for (int rot = 0; rot < 5; rot++) {
				State.InitState(sg.group[rot]);
			}
		}

		State.InitState(s_BreakLight);
		State.InitState(s_BreakBarrel);
		State.InitState(s_BreakPedistal);
		State.InitState(s_BreakBottle1);
		State.InitState(s_BreakBottle2);
		State.InitState(s_Puff);
		State.InitState(s_LaserPuff);
		State.InitState(s_Tracer);
		State.InitState(s_EMP);
		State.InitState(s_EMPBurst);
		State.InitState(s_EMPShrap);
		State.InitState(s_TankShell);
		State.InitState(s_VehicleSmoke);
		State.InitState(s_WaterSmoke);
		State.InitState(s_UziSmoke);
		State.InitState(s_ShotgunSmoke);
		State.InitState(s_UziBullet);
		State.InitState(s_UziSpark);
		State.InitState(s_UziPowerSpark);
		State.InitState(s_Bubble);
		State.InitState(s_Splash);
		State.InitState(s_Star);
		State.InitState(s_StarDown);
		State.InitState(s_LavaBoulder);
		State.InitState(s_LavaShard);
		State.InitState(s_VulcanBoulder);
		State.InitState(s_Mine);
		State.InitState(s_MineSpark);
		State.InitState(s_MeteorExp);
		State.InitState(s_MirvMeteorExp);
		State.InitState(s_SerpMeteorExp);
		State.InitState(s_BoltFatMan);
		State.InitState(s_BoltShrapnel);
		State.InitState(s_CoolgFire);
		State.InitState(s_CoolgFireDone);
		State.InitState(s_GoreFloorSplash);
		State.InitState(s_GoreSplash);
		State.InitState(s_Plasma);
		State.InitState(s_PlasmaFountain);
		State.InitState(s_PlasmaDrip);
		State.InitState(s_PlasmaDone);
		State.InitState(s_TeleportEffect);
		State.InitState(s_TeleportEffect2);
		State.InitState(s_Electro);
		State.InitState(s_ElectroShrap);
		State.InitState(s_GrenadeSmallExp);
		State.InitState(s_GrenadeExp);
		State.InitState(s_MineExp);
		State.InitState(s_BasicExp);
		State.InitState(s_MicroExp);
		State.InitState(s_BigGunFlame);
		State.InitState(s_BoltExp);
		State.InitState(s_TankShellExp);
		State.InitState(s_TracerExp);
		State.InitState(s_SectorExp);
		State.InitState(s_FireballExp);
		State.InitState(s_NapExp);
		State.InitState(s_FireballFlames);
		State.InitState(s_BreakFlames);
		State.InitState(s_Fireball);
		State.InitState(s_Ring);
		State.InitState(s_Ring2);
		State.InitState(s_Napalm);
		State.InitState(s_BloodWorm);
		State.InitState(s_PlasmaExp);
		State.InitState(s_Mirv);
		State.InitState(s_MirvMissile);
		State.InitState(s_GoreHead);
		State.InitState(s_GoreLeg);
		State.InitState(s_GoreEye);
		State.InitState(s_GoreTorso);
		State.InitState(s_GoreArm);
		State.InitState(s_GoreLung);
		State.InitState(s_GoreLiver);
		State.InitState(s_GoreSkullCap);
		State.InitState(s_GoreChunkS);
		State.InitState(s_GoreDrip);
		State.InitState(s_FastGoreDrip);
		State.InitState(s_GoreFlame);
		State.InitState(s_TracerShrap);
		State.InitState(s_UziShellShrap);
		State.InitState(s_ShotgunShellShrap);
		State.InitState(s_GoreFlameChunkA);
		State.InitState(s_GoreFlameChunkB);
		State.InitState(s_CoinShrap);
		State.InitState(s_GlassShrapA);
		State.InitState(s_GlassShrapB);
		State.InitState(s_GlassShrapC);
		State.InitState(s_WoodShrapA);
		State.InitState(s_WoodShrapB);
		State.InitState(s_WoodShrapC);
		State.InitState(s_StoneShrapA);
		State.InitState(s_StoneShrapB);
		State.InitState(s_StoneShrapC);
		State.InitState(s_MetalShrapA);
		State.InitState(s_MetalShrapB);
		State.InitState(s_MetalShrapC);
		State.InitState(s_PaperShrapA);
		State.InitState(s_PaperShrapB);
		State.InitState(s_PaperShrapC);
		State.InitState(s_WallBlood1);
		State.InitState(s_WallBlood2);
		State.InitState(s_WallBlood3);
		State.InitState(s_WallBlood4);
		State.InitState(s_FloorBlood1);
	}

	public static boolean MissileHitMatch(int Weapon, int WeaponNum, int hitsprite) {
		SPRITE hsp = sprite[hitsprite];

		if (WeaponNum <= -1) {
			USER wu = pUser[Weapon];
			WeaponNum = wu.WeaponNum;

			// can be hit by SO only
			if (SP_TAG7(hsp) == 4) {
				if (TEST(wu.Flags2, SPR2_SO_MISSILE)) {
					DoMatchEverything(null, hsp.hitag, -1);
					return (true);
				} else {
					return (false);
				}
			}
		}

		if (SP_TAG7(hsp) == 0) {
			switch (WeaponNum) {
			case WPN_RAIL:
			case WPN_MICRO:
			case WPN_NAPALM:
			case WPN_ROCKET:
				DoMatchEverything(null, hsp.hitag, -1);
				return (true);
			}
		} else if (SP_TAG7(hsp) == 1) {
			switch (WeaponNum) {
			case WPN_MICRO:
			case WPN_RAIL:
			case WPN_HOTHEAD:
			case WPN_NAPALM:
			case WPN_ROCKET:
				DoMatchEverything(null, hsp.hitag, -1);
				return (true);
			}
		} else if (SP_TAG7(hsp) == 2) {
			switch (WeaponNum) {
			case WPN_MICRO:
			case WPN_RAIL:
			case WPN_HOTHEAD:
			case WPN_NAPALM:
			case WPN_ROCKET:
			case WPN_UZI:
			case WPN_SHOTGUN:
				DoMatchEverything(null, hsp.hitag, -1);
				return (true);
			}
		} else if (SP_TAG7(hsp) == 3) {
			switch (WeaponNum) {
			case WPN_STAR:
			case WPN_SWORD:
			case WPN_FIST:
			case WPN_MICRO:
			case WPN_RAIL:
			case WPN_HOTHEAD:
			case WPN_NAPALM:
			case WPN_ROCKET:
			case WPN_UZI:
			case WPN_SHOTGUN:
				DoMatchEverything(null, hsp.hitag, -1);
				return (true);
			}
		}

		return (false);
	}

	public static int SpawnShrapX(int SpriteNum) {
		// For shrap that has no Weapon to send over
		SpawnShrap(SpriteNum, -1);
		return (0);
	}

	public static final Animator DoLavaErupt = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoLavaErupt(spr) != 0;
		}
	};

	public static int DoLavaErupt(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		short i, nexti, pnum;
		PlayerStr pp;
		SPRITE tsp;
		boolean found = false;

		if (TEST_BOOL1(sp)) {
			for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
				pp = Player[pnum];
				if (pp.cursectnum != -1 && TEST(sector[pp.cursectnum].extra, SECTFX_TRIGGER)) {
					for (i = headspritesect[pp.cursectnum]; i != -1; i = nexti)

					{
						nexti = nextspritesect[i];
						tsp = sprite[i];

						if (tsp.statnum == STAT_TRIGGER && SP_TAG7(tsp) == 0 && SP_TAG5(tsp) == 1) {
							found = true;
							break;
						}
					}
					if (found)
						break;
				}
			}

			if (!found)
				return (0);
		}

		if (!TEST(u.Flags, SPR_ACTIVE)) {
			// inactive
			if ((u.WaitTics -= synctics) <= 0) {
				u.Flags |= (SPR_ACTIVE);
				u.Counter = 0;
				u.WaitTics = (short) (SP_TAG9(sp) * 120);
			}
		} else {
			// active
			if ((u.WaitTics -= synctics) <= 0) {
				// Stop for this long
				u.Flags &= ~(SPR_ACTIVE);
				u.Counter = 0;
				u.WaitTics = (short) (SP_TAG10(sp) * 120);
			}

			// Counter controls the volume of lava erupting
			// starts out slow and increases to a max
			u.Counter += synctics;
			if (u.Counter > SP_TAG2(sp))
				u.Counter = (short) SP_TAG2(sp);

			if ((RANDOM_P2(1024 << 6) >> 6) < u.Counter) {
				switch (SP_TAG3(sp)) {
				case 0:
					SpawnShrapX(SpriteNum);
					break;
				case 1:
					InitVulcanBoulder(SpriteNum);
					break;
				}
			}
		}

		return (0);
	}

	public static int DoShrapMove(int SpriteNum) {
		USER u = pUser[SpriteNum];

		u.ret = move_missile(SpriteNum, u.xchange, u.ychange, 0, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS * 2);
		return (0);
	}

	public static int DoVomit(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		u.Counter = NORM_ANGLE(u.Counter + (30 * MISSILEMOVETICS));
		sp.xrepeat = (short) (u.sx + ((12 * sintable[NORM_ANGLE(u.Counter + 512)]) >> 14));
		sp.yrepeat = (short) (u.sy + ((12 * sintable[u.Counter]) >> 14));

		if (TEST(u.Flags, SPR_JUMPING)) {
			DoJump(SpriteNum);
			DoJump(SpriteNum);
			DoShrapMove(SpriteNum);
		} else if (TEST(u.Flags, SPR_FALLING)) {
			DoFall(SpriteNum);
			DoFall(SpriteNum);
			DoShrapMove(SpriteNum);
		} else {
			ChangeState(SpriteNum, s_VomitSplash[0]);
			DoFindGroundPoint(SpriteNum);
			MissileWaterAdjust(SpriteNum);
			sp.z = u.loz;
			u.WaitTics = 60;
			u.sx = sp.xrepeat;
			u.sy = sp.yrepeat;
			return (0);
		}

		if (DTEST(u.ret, HIT_MASK) == HIT_SPRITE) {
			short hitsprite = NORM_SPRITE(u.ret);
			if (TEST(sprite[hitsprite].extra, SPRX_PLAYER_OR_ENEMY)) {
				DoDamage(hitsprite, SpriteNum);
				return (0);
			}
		}

		return (0);
	}

	public static int DoVomitSplash(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if ((u.WaitTics -= MISSILEMOVETICS) < 0) {
			KillSprite(SpriteNum);
			return (0);
		}

		return (0);
	}

	public static int DoFastShrapJumpFall(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		sp.x += u.xchange * 2;
		sp.y += u.ychange * 2;
		sp.z += u.zchange * 2;

		u.WaitTics -= MISSILEMOVETICS;
		if (u.WaitTics <= 0)
			KillSprite(SpriteNum);

		return (0);
	}

	public static int DoTracerShrap(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		sp.x += u.xchange;
		sp.y += u.ychange;
		sp.z += u.zchange;

		u.WaitTics -= MISSILEMOVETICS;
		if (u.WaitTics <= 0)
			KillSprite(SpriteNum);

		return (0);
	}

	public static int DoShrapJumpFall(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_JUMPING)) {
			DoShrapVelocity(SpriteNum);
		} else if (TEST(u.Flags, SPR_FALLING)) {
			DoShrapVelocity(SpriteNum);
		} else {
			if (!TEST(u.Flags, SPR_BOUNCE)) {
				DoShrapVelocity(SpriteNum);
				return (0);
			}

			if (u.ID == GORE_Drip)
				ChangeState(SpriteNum, s_GoreFloorSplash[0]);
			else
				ShrapKillSprite(SpriteNum);
		}

		return (0);
	}

	public static int DoShrapDamage(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_JUMPING)) {
			DoJump(SpriteNum);
			DoShrapMove(SpriteNum);
		} else if (TEST(u.Flags, SPR_FALLING)) {
			DoFall(SpriteNum);
			DoShrapMove(SpriteNum);
		} else {
			if (!TEST(u.Flags, SPR_BOUNCE)) {
				u.Flags |= (SPR_BOUNCE);
				u.jump_speed = -300;
				sp.xvel >>= 2;
				DoBeginJump(SpriteNum);
				return (0);
			}

			KillSprite(SpriteNum);
			return (0);
		}

		if (u.ret != 0) {
			switch (DTEST(u.ret, HIT_MASK)) {
			case HIT_SPRITE: {
				WeaponMoveHit(SpriteNum);
				KillSprite(SpriteNum);
				return (0);
			}
			}
		}

		return (0);
	}

	private static final SHRAP UziBlood[] = { new SHRAP(s_GoreDrip, GORE_Drip, 1, Z_MID, 100, 250, 10, 20, true, 512), // 70,200
																														// vels
	};

	private static final SHRAP SmallBlood[] = {
			new SHRAP(s_GoreDrip, GORE_Drip, 1, Z_TOP, 100, 250, 10, 20, true, 512), };

	private static final SHRAP PlasmaFountainBlood[] = {
			new SHRAP(s_PlasmaDrip, PLASMA_Drip, 1, Z_TOP, 200, 500, 100, 300, true, 16), };

	private static final SHRAP SomeBlood[] = {
			new SHRAP(s_GoreDrip, GORE_Drip, 1, Z_TOP, 100, 250, 10, 20, true, 512), };

	private static final SHRAP ExtraBlood[] = {
			new SHRAP(s_GoreDrip, GORE_Drip, 4, Z_TOP, 100, 250, 10, 20, true, 512), };

	private static final SHRAP HariKariBlood[] = {
			new SHRAP(s_FastGoreDrip, GORE_Drip, 32, Z_TOP, 200, 650, 70, 100, true, 1024), };

	public static boolean SpawnBlood(int SpriteNum, int Weapon, int hitang, int hitx, int hity, int hitz) {
		SPRITE sp = sprite[SpriteNum];
		SPRITE np;
		USER nu, u;
		short i, newsp;

		short dang = 0;

		SHRAP p = UziBlood[0];
		short shrap_shade = -15;
		short shrap_xsize = 20, shrap_ysize = 20;
		boolean retval = true;
		short shrap_pal = PALETTE_DEFAULT;
		short start_ang = 0;

		u = pUser[SpriteNum];

		switch (u.ID) {
		case TRASHCAN:
		case PACHINKO1:
		case PACHINKO2:
		case PACHINKO3:
		case PACHINKO4:
		case 623:
		case ZILLA_RUN_R0:
			return false; // Don't put blood on trashcan
		}

		// second sprite involved
		// most of the time is the weapon
		if (Weapon >= 0) {
			SPRITE wp = sprite[Weapon];
			USER wu = pUser[Weapon];

			switch (wu.ID) {
			case NINJA_RUN_R0: // sword
				// if sprite and weapon are the same it must be HARIII-KARIII
				if (sp == wp) {
					p = HariKariBlood[0];
					hitang = sp.ang;
					hitx = sp.x;
					hity = sp.y;
					hitz = SPRITEp_TOS(wp) + DIV16(SPRITEp_SIZE_Z(wp));
				} else {
					p = ExtraBlood[0];
					hitang = NORM_ANGLE(wp.ang + 1024);
					hitx = sp.x;
					hity = sp.y;
					hitz = SPRITEp_TOS(wp) + DIV4(SPRITEp_SIZE_Z(wp));

					// ASSERT(wu.PlayerP);
				}
				break;
			case SERP_RUN_R0:
				p = ExtraBlood[0];
				hitang = NORM_ANGLE(wp.ang + 1024);
				hitx = sp.x;
				hity = sp.y;
				hitz = SPRITEp_TOS(sp) + DIV4(SPRITEp_SIZE_Z(sp));
				break;
			case BLADE1:
			case BLADE2:
			case BLADE3:
			case 5011:
				p = SmallBlood[0];
				hitang = NORM_ANGLE(ANG2SPRITE(sp, wp) + 1024);
				hitx = sp.x;
				hity = sp.y;
				hitz = wp.z - DIV2(SPRITEp_SIZE_Z(wp));
				break;
			case STAR1:
			case CROSSBOLT:
				p = SomeBlood[0];
				hitang = NORM_ANGLE(wp.ang + 1024);
				hitx = sp.x;
				hity = sp.y;
				hitz = wp.z;
				break;
			case PLASMA_FOUNTAIN:
				p = PlasmaFountainBlood[0];
				hitang = wp.ang;
				hitx = sp.x;
				hity = sp.y;
				hitz = SPRITEp_TOS(sp) + DIV4(SPRITEp_SIZE_Z(sp));
				break;
			default:
				p = SomeBlood[0];
				hitang = NORM_ANGLE(wp.ang + 1024);
				hitx = sp.x;
				hity = sp.y;
				hitz = SPRITEp_TOS(wp) + DIV4(SPRITEp_SIZE_Z(wp));
				break;
			}
		} else {
			hitang = NORM_ANGLE(hitang + 1024);
		}

		if (p.state != null) {
			if (!p.random_disperse) {
				start_ang = NORM_ANGLE(hitang - DIV2(p.ang_range) + 1024);
				dang = (short) (p.ang_range / p.num);
			}

			for (i = 0; i < p.num; i++) {
				newsp = (short) SpawnSprite(STAT_SKIP4, p.id, p.state[0], sp.sectnum, hitx, hity, hitz, hitang, 0);
				if(newsp == -1)
					return false;

				np = sprite[newsp];
				nu = pUser[newsp];

				switch (nu.ID) {
				case ELECTRO_SHARD:
					shrap_xsize = 7;
					shrap_ysize = 7;
					break;

				case PLASMA_Drip:
					// Don't do central blood splats for every hitscan
					if (RANDOM_P2(1024) < 950) {
						np.xrepeat = np.yrepeat = 0;
					}
					if (RANDOM_P2(1024) < 512)
						np.cstat |= (CSTAT_SPRITE_XFLIP);
					break;
				}

				if (p.random_disperse) {
					np.ang = (short) (hitang + (RANDOM_P2(p.ang_range << 5) >> 5) - DIV2(p.ang_range));
					np.ang = NORM_ANGLE(np.ang);
				} else {
					np.ang = (short) (start_ang + (i * dang));
					np.ang = NORM_ANGLE(np.ang);
				}

				nu.Flags |= (SPR_BOUNCE);

				np.shade = (byte) shrap_shade;
				np.xrepeat = shrap_xsize;
				np.yrepeat = shrap_ysize;
				np.clipdist = 16 >> 2;

				np.pal = nu.spal = (byte) shrap_pal;

				np.xvel = p.min_vel;
				np.xvel += RANDOM_RANGE(p.max_vel - p.min_vel);

				// special case
				// blood coming off of actors should have the acceleration of the actor
				// so add it in
				np.xvel += sp.xvel;

				nu.ceiling_dist = nu.floor_dist = (short) Z(2);
				nu.jump_speed = p.min_jspeed;
				nu.jump_speed += RANDOM_RANGE(p.max_jspeed - p.min_jspeed);
				nu.jump_speed = (short) -nu.jump_speed;

				// setsprite(new, np.x, np.y, np.z);
				nu.xchange = MOVEx(np.xvel, np.ang);
				nu.ychange = MOVEy(np.xvel, np.ang);

				// for FastShrap
				nu.zchange = klabs(nu.jump_speed * 4) - RANDOM_RANGE(klabs(nu.jump_speed) * 8);
				nu.WaitTics = (short) (64 + RANDOM_P2(32));

				u.Flags |= (SPR_BOUNCE);

				DoBeginJump(newsp);
			}
		}

		return (retval);
	}

	public static boolean VehicleMoveHit(int SpriteNum) {
		USER u = pUser[SpriteNum];
		Sector_Object sop;
		Sector_Object hsop;
		int controller;

		short hitsect, hitwall;

		if (u.ret == 0)
			return (false);

		sop = SectorObject[u.sop_parent];

		// sprite controlling sop
		controller = sop.controller;

		switch (DTEST(u.ret, HIT_MASK)) {
		case HIT_SECTOR: {
			SECTOR sectp;

			hitsect = NORM_SECTOR(u.ret);

			sectp = sector[hitsect];

			if (TEST(sectp.extra, SECTFX_SECTOR_OBJECT)) {
				// shouldn't ever really happen
			}

			return (true);
		}

		case HIT_SPRITE: {
			SPRITE hsp;
			short hitsprite;

			hitsprite = NORM_SPRITE(u.ret);
			hsp = sprite[hitsprite];

			if (TEST(hsp.extra, SPRX_BREAKABLE)) {
				HitBreakSprite(hitsprite, u.ID);
				return (true);
			}

			if (TEST(hsp.extra, SPRX_PLAYER_OR_ENEMY)) {
				if (hitsprite != sprite[controller].owner) {
					DoDamage(hitsprite, controller);
					return (true);
				}
			} else {
				if (hsp.statnum == STAT_MINE_STUCK) {
					DoDamage(hitsprite, SpriteNum);
					return (true);
				}
			}

			return (true);
		}

		case HIT_WALL: {
			hitsect = -2;
			WALL wph;

			hitwall = NORM_WALL(u.ret);
			wph = wall[hitwall];

			if (TEST(wph.extra, WALLFX_SECTOR_OBJECT)) {
				// sector object collision
				if ((hsop = DetectSectorObjectByWall(wph)) != null) {
					SopDamage(hsop, sop.ram_damage);
					if (hsop.max_damage <= 0)
						SopCheckKill(hsop);
					else
						DoSpawnSpotsForDamage(hsop.match_event);

				}
			}
			return (true);
		}
		}

		return (false);
	}

	public static boolean WeaponMoveHit(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();
		// short hitsect, hitwall;

		if (u.ret == 0)
			return (false);

		switch (DTEST(u.ret, HIT_MASK)) {
		case HIT_PLAX_WALL:
			SetSuicide(SpriteNum);
			return (true);

		case HIT_SECTOR: {
			short hitsect;
			SECTOR sectp;

			hitsect = NORM_SECTOR(u.ret);
			sectp = sector[hitsect];

			// hit floor - closer to floor than ceiling
			if (sp.z > DIV2(u.hiz + u.loz)) {
				// hit a floor sprite
				if (u.lo_sp != -1) {

					if (sprite[u.lo_sp].lotag == TAG_SPRITE_HIT_MATCH) {
						if (MissileHitMatch(SpriteNum, -1, u.lo_sp))
							return (true);
                    }

					return (true);
				}

				if (SectUser[hitsect] != null && SectUser[hitsect].depth > 0) {
					SpawnSplash(SpriteNum);
					// SetSuicide(SpriteNum);
					return (true);
				}

			}
			// hit ceiling
			else {
				// hit a floor sprite
				if (u.hi_sp != -1) {
					if (sprite[u.hi_sp].lotag == TAG_SPRITE_HIT_MATCH) {
						if (MissileHitMatch(SpriteNum, -1, u.hi_sp))
							return (true);
					}
				}
			}

			int sopi = -1;
			if (TEST(sectp.extra, SECTFX_SECTOR_OBJECT)) {
				if ((sopi = DetectSectorObject(sectp)) != -1) {
					DoDamage(SectorObject[sopi].sp_child, SpriteNum);
					return (true);
				}
			}

			if (TEST(sectp.ceilingstat, CEILING_STAT_PLAX) && sectp.ceilingpicnum != FAF_MIRROR_PIC) {
				if (klabs(sp.z - sectp.ceilingz) < SPRITEp_SIZE_Z(sp)) {
					SetSuicide(SpriteNum);
					return (true);
				}
			}

			return (true);
		}

		case HIT_SPRITE: {
			SPRITE hsp;
			USER hu;
			short hitsprite;

			hitsprite = NORM_SPRITE(u.ret);
			hsp = sprite[hitsprite];
			hu = pUser[hitsprite];

			if (hsp.statnum == STAT_ENEMY) {

			}

			if (TEST(hsp.extra, SPRX_BREAKABLE)) {
				HitBreakSprite(hitsprite, u.ID);
				return (true);
			}

			if (TEST(hsp.extra, SPRX_PLAYER_OR_ENEMY)) {
				// make sure you didn't hit the owner of the missile
				// if (sp.owner != -1 && hitsprite != sp.owner)
				if (hitsprite != sp.owner) {
					if (u.ID == STAR1) {
						switch (hu.ID) {
						case TRASHCAN:
							PlaySound(DIGI_TRASHLID, sp, v3df_none);
							PlaySound(DIGI_STARCLINK, sp, v3df_none);
							if (hu.WaitTics <= 0) {
								hu.WaitTics = (short) SEC(2);
								ChangeState(hitsprite, s_TrashCanPain[0]);
							}
							break;
						case PACHINKO1:
						case PACHINKO2:
						case PACHINKO3:
						case PACHINKO4:
						case ZILLA_RUN_R0:
						case 623: {
							PlaySound(DIGI_STARCLINK, sp, v3df_none);
						}
							break;
						}
					}
					DoDamage(hitsprite, SpriteNum);
					return (true);
				}
			} else {
				if (hsp.statnum == STAT_MINE_STUCK) {
					DoDamage(hitsprite, SpriteNum);
					return (true);
				}
			}

			if (hsp.lotag == TAG_SPRITE_HIT_MATCH) {
				if (MissileHitMatch(SpriteNum, -1, hitsprite))
					return (true);
            }

			if (TEST(hsp.cstat, CSTAT_SPRITE_WALL)) {
				if (hsp.lotag != 0 || hsp.hitag != 0) {
					ShootableSwitch(hitsprite, SpriteNum);
					return (true);
				}
			}

			return (true);
		}

		case HIT_WALL: {
			WALL wph;
			Sector_Object sop;

			short hitwall = NORM_WALL(u.ret);
			wph = wall[hitwall];

			if (TEST(wph.extra, WALLFX_SECTOR_OBJECT)) {
				if ((sop = DetectSectorObjectByWall(wph)) != null) {
					if (sop.max_damage != -999)
						DoDamage(sop.sp_child, SpriteNum);
					return (true);
				}
			}

			if (wph.lotag == TAG_WALL_BREAK) {
				HitBreakWall(hitwall, sp.x, sp.y, sp.z, sp.ang, u.ID);
				u.ret = 0;
				return (true);
			}

			// clipmove does not correctly return the sprite for WALL sprites
			// on walls, so look with hitscan

			engine.hitscan(sp.x, sp.y, sp.z, sp.sectnum, // Start position
					sintable[NORM_ANGLE(sp.ang + 512)], // X vector of 3D ang
					sintable[NORM_ANGLE(sp.ang)], // Y vector of 3D ang
					sp.zvel, // Z vector of 3D ang
					pHitInfo, CLIPMASK_MISSILE);

			if (pHitInfo.hitsect < 0) {
				return (false);
			}

			if (pHitInfo.hitsprite >= 0) {
				SPRITE hsp = sprite[pHitInfo.hitsprite];

				if (hsp.lotag == TAG_SPRITE_HIT_MATCH) {
					if (MissileHitMatch(SpriteNum, -1, pHitInfo.hitsprite))
						return (true);
				}

				if (TEST(hsp.cstat, CSTAT_SPRITE_WALL)) {
					if (hsp.lotag != 0 || hsp.hitag != 0) {
						ShootableSwitch(pHitInfo.hitsprite, SpriteNum);
						return (true);
					}
				}
			}

			return (true);
		}
		}

		return (false);
	}

	public static int DoUziSmoke(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		sp.z -= 200; // !JIM! Make them float up

		return (0);
	}

	public static int DoShotgunSmoke(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		sp.z -= 200; // !JIM! Make them float up

		return (0);
	}

	public static int DoMineSpark(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		if (sp.picnum != 0) {
			DoDamageTest(SpriteNum);
		}

		return (0);
	}

	public static int DoFireballFlames(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], ap;
		USER u = pUser[SpriteNum];
		boolean jumping = false;

		// if no owner then stay where you are
		if (u.Attach >= 0) {
			ap = sprite[u.Attach];

			sp.x = ap.x;
			sp.y = ap.y;

			sp.z = DIV2(SPRITEp_TOS(ap) + SPRITEp_BOS(ap));

			if (TEST(ap.extra, SPRX_BURNABLE)) {
				if (MOD2(u.Counter2) == 0) {
					ap.shade++;
					if (ap.shade > 10)
						ap.shade = 10;
				}
			}
		} else {
			if (TEST(u.Flags, SPR_JUMPING)) {
				DoJump(SpriteNum);
				jumping = true;
            } else if (TEST(u.Flags, SPR_FALLING)) {
				DoFall(SpriteNum);
				jumping = true;
            } else {
				if (SectUser[sp.sectnum] != null && SectUser[sp.sectnum].depth > 0) {
					if (klabs(sector[sp.sectnum].floorz - sp.z) <= Z(4)) {
						KillSprite(SpriteNum);
						return (0);
					}
				}

				if (TestDontStickSector(sp.sectnum)) {
					KillSprite(SpriteNum);
					return (0);
				}
			}
		}

		if (!jumping) {
			if ((u.WaitTics += MISSILEMOVETICS) > 4 * 120) {
				// shrink and go away
				sp.xrepeat--;
				sp.yrepeat--;

				if (((byte) sp.xrepeat) == 0) {
					if (u.Attach >= 0 && pUser[u.Attach] != null)
						pUser[u.Attach].flame = -1;
					KillSprite(SpriteNum);
					return (0);
				}
			} else {
				// grow until the right size
				if (sp.xrepeat <= u.Counter) {
					sp.xrepeat += 3;
					sp.yrepeat += 3;
				}
			}
		}

		u.Counter2++;
		if (u.Counter2 > 9) {
			u.Counter2 = 0;
			DoFlamesDamageTest(SpriteNum);
		}

		return (0);
	}

	public static int DoBreakFlames(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		boolean jumping = false;

		if (TEST(u.Flags, SPR_JUMPING)) {
			DoJump(SpriteNum);
			jumping = true;
		} else if (TEST(u.Flags, SPR_FALLING)) {
			DoFall(SpriteNum);
			jumping = true;
		} else {
			if (SectUser[sp.sectnum] != null && SectUser[sp.sectnum].depth > 0) {
				if (klabs(sector[sp.sectnum].floorz - sp.z) <= Z(4)) {
					KillSprite(SpriteNum);
					return (0);
				}
			}

			if (TestDontStickSector(sp.sectnum)) {
				KillSprite(SpriteNum);
				return (0);
			}
		}

		if (!jumping) {
			if ((u.WaitTics += MISSILEMOVETICS) > 4 * 120) {
				// shrink and go away
				sp.xrepeat--;
				sp.yrepeat--;

				if (((byte) sp.xrepeat) == 0) {
					if (u.Attach >= 0)
						pUser[u.Attach].flame = -1;
					KillSprite(SpriteNum);
					return (0);
				}
			} else {
				// grow until the right size
				if (sp.xrepeat <= u.Counter) {
					sp.xrepeat += 3;
					sp.yrepeat += 3;
				}

				if (u.WaitTics + MISSILEMOVETICS > 4 * 120) {
					SpawnBreakStaticFlames(SpriteNum);
				}
			}
		}

		u.Counter2++;
		if (u.Counter2 > 9) {
			u.Counter2 = 0;
			DoFlamesDamageTest(SpriteNum);
		}

		return (0);
	}

	public static int SetSuicide(int SpriteNum) {
		USER u = pUser[SpriteNum];
		if(u == null)
			return 0;

		u.Flags |= (SPR_SUICIDE);
		u.RotNum = 0;
		ChangeState(SpriteNum, s_Suicide[0]);
		return (0);
	}

	public static int DoActorScale(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = sprite[SpriteNum];

		u.scale_speed = 70;
		u.scale_value = sp.xrepeat << 8;
		u.scale_tgt = (short) (sp.xrepeat + 25);

		if (u.scale_tgt > 256) {
			u.scale_speed = 0;
			u.scale_tgt = 256;
		}

		return (0);
	}

	public static int DoRipperGrow(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = sprite[SpriteNum];

		u.scale_speed = 70;
		u.scale_value = sp.xrepeat << 8;
		u.scale_tgt = (short) (sp.xrepeat + 20);

		if (u.scale_tgt > 128) {
			u.scale_speed = 0;
			u.scale_tgt = 128;
		}

		return (0);
	}

	public static void UpdateSinglePlayKills(int SpriteNum, PlayerStr pKiller) {
		// single play and coop kill count
		if (gNet.MultiGameType != MultiGameTypes.MULTI_GAME_COMMBAT) {

			if (TEST(pUser[SpriteNum].Flags, SPR_SUICIDE)) {
				return;
			}

			switch (pUser[SpriteNum].ID) {
			case COOLIE_RUN_R0:
			case NINJA_RUN_R0:
			case NINJA_CRAWL_R0:
			case GORO_RUN_R0:
			case 1441:
			case COOLG_RUN_R0:
			case EEL_RUN_R0:
			case SUMO_RUN_R0:
			case ZILLA_RUN_R0:
			case RIPPER_RUN_R0:
			case RIPPER2_RUN_R0:
			case SERP_RUN_R0:
			case LAVA_RUN_R0:
			case SKEL_RUN_R0:
			case HORNET_RUN_R0:
			case GIRLNINJA_RUN_R0:
			case SKULL_R0:
			case BETTY_R0:
				if(pKiller != null)
					pKiller.Kills++;
				Kills++;
				break;
			}
		}
	}

	private static PlayerStr getKiller(int Weapon)
	{
		SPRITE wp = sprite[Weapon];
		if(game.nNetMode == NetMode.Single)
			return Player[myconnectindex];

		int owner = (wp != null && wp.owner != -1) ? wp.owner : Weapon;
		for (int pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
			PlayerStr pp = Player[pnum];
			if(owner == pp.PlayerSprite) {
				return pp;
			}
		}

		return null;
	}

	public static boolean ActorChooseDeath(int SpriteNum, int Weapon) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		SPRITE wp = sprite[Weapon];
		USER wu = pUser[Weapon];

		if (u.Health > 0)
			return (false);

		UpdateSinglePlayKills(SpriteNum, getKiller(Weapon));

		if (u.Attrib != null)
			PlaySpriteSound(SpriteNum, Attrib_Snds.attr_die, v3df_follow);

		switch (u.ID) {
		case PACHINKO1:
		case PACHINKO2:
		case PACHINKO3:
		case PACHINKO4:
		case 623:
		case TOILETGIRL_R0:
		case WASHGIRL_R0:
		case CARGIRL_R0:
		case MECHANICGIRL_R0:
		case SAILORGIRL_R0:
		case PRUNEGIRL_R0:
		case TRASHCAN:
		case GORO_RUN_R0:
		case RIPPER2_RUN_R0:
			break;
		case BUNNY_RUN_R0: {
			Bunny_Count--; // Bunny died, decrease the population
		}
			break;
		default:
			ActorCoughItem(SpriteNum);
			break;
		}

		switch (u.ID) {
		case BETTY_R0: {
			DoBettyBeginDeath(SpriteNum);
			break;
		}
		case SKULL_R0: {
			DoSkullBeginDeath(SpriteNum);
			break;
		}
		case TOILETGIRL_R0:
		case WASHGIRL_R0:
		case CARGIRL_R0:
		case MECHANICGIRL_R0:
		case SAILORGIRL_R0:
		case PRUNEGIRL_R0:
		case TRASHCAN:
		case PACHINKO1:
		case PACHINKO2:
		case PACHINKO3:
		case PACHINKO4:
		case 623: {
			if (u.ID == TOILETGIRL_R0 || u.ID == CARGIRL_R0 || u.ID == MECHANICGIRL_R0 || u.ID == SAILORGIRL_R0
					|| u.ID == PRUNEGIRL_R0 || u.ID == WASHGIRL_R0 && wu.ID == NINJA_RUN_R0 && wu.PlayerP != -1) {
				if (wu.PlayerP != -1 && !TEST(Player[wu.PlayerP].Flags, PF_DIVING))
					Player[wu.PlayerP].Bloody = true;
				PlaySound(DIGI_TOILETGIRLSCREAM, sp, v3df_none);
			}
			if (SpawnShrap(SpriteNum, Weapon))
				SetSuicide(SpriteNum);
			break;
		}

		// These are player zombies
		case ZOMBIE_RUN_R0:
			InitBloodSpray(SpriteNum, true, 105);
			InitBloodSpray(SpriteNum, true, 105);
			if (SpawnShrap(SpriteNum, Weapon))
				SetSuicide(SpriteNum);
			break;

		default:

			switch (wu.ID) {
			case NINJA_RUN_R0: // sword
				if (wu.PlayerP != -1) {
					PlayerStr pp = Player[wu.PlayerP];

					if (wu.WeaponNum == WPN_FIST && STD_RANDOM_RANGE(1000) > 500 && pp == Player[myconnectindex]) {
						char choosesnd = 0;

						choosesnd = (char) STD_RANDOM_RANGE(6);

						if (choosesnd == 0)
							PlayerSound(DIGI_KUNGFU, v3df_follow | v3df_dontpan, pp);
						else if (choosesnd == 1)
							PlayerSound(DIGI_PAYINGATTENTION, v3df_follow | v3df_dontpan,
									pp);
						else if (choosesnd == 2)
							PlayerSound(DIGI_EATTHIS, v3df_follow | v3df_dontpan, pp);
						else if (choosesnd == 3)
							PlayerSound(DIGI_TAUNTAI4, v3df_follow | v3df_dontpan, pp);
						else if (choosesnd == 4)
							PlayerSound(DIGI_TAUNTAI5, v3df_follow | v3df_dontpan, pp);
						else if (choosesnd == 5)
							PlayerSound(DIGI_HOWYOULIKEMOVE, v3df_follow | v3df_dontpan, pp);
					} else if (wu.WeaponNum == WPN_SWORD && STD_RANDOM_RANGE(1000) > 500
							&& pp == Player[myconnectindex]) {
						short choose_snd;

						choose_snd = (short) STD_RANDOM_RANGE(1000);
						if (choose_snd > 750)
							PlayerSound(DIGI_SWORDGOTU1, v3df_follow | v3df_dontpan, pp);
						else if (choose_snd > 575)
							PlayerSound(DIGI_SWORDGOTU2, v3df_follow | v3df_dontpan, pp);
						else if (choose_snd > 250)
							PlayerSound(DIGI_SWORDGOTU3, v3df_follow | v3df_dontpan, pp);
						else
							PlayerSound(DIGI_CANBEONLYONE, v3df_follow | v3df_dontpan, pp);
					}
					if (!TEST(pp.Flags, PF_DIVING))
						pp.Bloody = true;
				}

				if (u.WeaponNum == WPN_FIST)
					DoActorDie(SpriteNum, Weapon);
				else if (u.ID == NINJA_RUN_R0 || RANDOM_RANGE(1000) < 500)
					DoActorDie(SpriteNum, Weapon);
				else {
					// Can't gib bosses!
					if (u.ID == SERP_RUN_R0 || u.ID == SUMO_RUN_R0 || u.ID == ZILLA_RUN_R0) {
						DoActorDie(SpriteNum, Weapon);
						break;
					}

					// Gib out the ones you can't cut in half
					// Blood fountains
					InitBloodSpray(SpriteNum, true, -1);

					if (SpawnShrap(SpriteNum, Weapon)) {
						SetSuicide(SpriteNum);
					} else
						DoActorDie(SpriteNum, Weapon);

				}
				break;

			case BOLT_THINMAN_R0:
			case BOLT_THINMAN_R1:
			case BOLT_THINMAN_R2:
			case NAP_EXP:
			case BOLT_EXP:
			case TANK_SHELL_EXP:
			case SECTOR_EXP:
			case FIREBALL_EXP:
			case GRENADE_EXP:
			case MINE_EXP:
			case SKULL_R0:
			case BETTY_R0:

			{
				char choosesnd = 0;

				// For the Nuke, do residual radiation if he gibs
				if (wu.Radius == NUKE_RADIUS)
					SpawnFireballFlames(SpriteNum, -1);

				// Random chance of taunting the AI's here
				if (RANDOM_RANGE(1000) > 400) {
					PlayerStr pp;

					if (wp != null && wp.owner >= 0 && pUser[wp.owner] != null) {
						if (pUser[wp.owner].PlayerP != -1) {

							pp = Player[pUser[wp.owner].PlayerP];

							choosesnd = (char) (STD_RANDOM_RANGE(MAX_TAUNTAI << 8) >> 8);

							if (pp != null && pp == Player[myconnectindex])
								PlayerSound(TauntAIVocs[choosesnd], v3df_dontpan | v3df_follow, pp);
						}
					}
				}

				// These guys cough items only if gibbed
				if (u.ID == GORO_RUN_R0 || u.ID == RIPPER2_RUN_R0)
					ActorCoughItem(SpriteNum);

				// Blood fountains
				InitBloodSpray(SpriteNum, true, -1);

				// Bosses do not gib
				if (u.ID == SERP_RUN_R0 || u.ID == SUMO_RUN_R0 || u.ID == ZILLA_RUN_R0) {
					DoActorDie(SpriteNum, Weapon);
					return (true);
				}

				if (SpawnShrap(SpriteNum, Weapon)) {
					SetSuicide(SpriteNum);
				} else
					DoActorDie(SpriteNum, Weapon);

			}

				break;
			default:
				DoActorDie(SpriteNum, Weapon);
				break;
			}

			break;
		}

		return (true);
	}

	public static boolean ActorHealth(int SpriteNum, int amt) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if (u.ID == TRASHCAN && amt > -75) {
			u.LastDamage = 100;
			return (true);
		}

		u.Flags |= (SPR_ATTACKED);

		u.Health += amt;

		if (u.ID == SERP_RUN_R0 && sp.pal != 16 && Level == 4) {
			if (u.Health < u.MaxHealth / 2) {
				ExitLevel = true;
				FinishAnim = ANIM_SERP;
				FinishedLevel = true;
				return (true);
			}
		}

		if (u.ID == SUMO_RUN_R0 && Level == 11) {
			if (u.Health <= 0) {
				FinishTimer = 7 * 120;
				FinishAnim = ANIM_SUMO;
			}
		}

		if (u.ID == ZILLA_RUN_R0 && Level == 20) {
			if (u.Health <= 0)
			// if (u.Health < u.MaxHealth)
			{
				FinishTimer = 15 * 120;
				FinishAnim = ANIM_ZILLA;
			}
		}

		if (u.Attrib != null && RANDOM_P2(1024) > 850)
			PlaySpriteSound(SpriteNum, Attrib_Snds.attr_pain, v3df_follow | v3df_dontpan);

		// keep track of the last damage
		if (amt < 0)
			u.LastDamage = (short) -amt;

		// Do alternate Death2 if it exists
		if (u.ActorActionSet != null && u.ActorActionSet.Death2 != null) {
			int DEATH2_HEALTH_VALUE = 15;

			if (u.Health <= DEATH2_HEALTH_VALUE) {
				// If he's dead, possibly choose a special death type

				switch (u.ID) {
				case NINJA_RUN_R0: {

					if (TEST(u.Flags2, SPR2_DYING))
						return (true);
					if (TEST(u.Flags, SPR_FALLING | SPR_JUMPING | SPR_CLIMBING))
						return (true);

					if (!TEST(u.Flags2, SPR2_DYING)) {
						short rnd;

						rnd = (short) (RANDOM_P2(1024 << 4) >> 4);
						if (rnd < 950)
							return (true);
						u.Flags2 |= (SPR2_DYING); // Only let it check this once!
						u.WaitTics = (short) (SEC(1) + SEC(RANDOM_RANGE(2)));
						u.Health = 60;
						PlaySound(DIGI_NINJACHOKE, sp, v3df_follow);
						InitPlasmaFountain(null, SpriteNum);
						InitBloodSpray(SpriteNum, false, 105);
						sp.ang = NORM_ANGLE(
								engine.getangle(sprite[u.tgt_sp].x - sp.x, sprite[u.tgt_sp].y - sp.y) + 1024);
						sp.cstat &= ~(CSTAT_SPRITE_YFLIP);
						NewStateGroup(SpriteNum, NinjaStateGroup.sg_NinjaGrabThroat);
					}
					break;
				}
				}
			}
		}

		return (true);
	}

	public static boolean SopDamage(Sector_Object sop, int amt) {

		USER u = pUser[sop.sp_child];

		// does not have damage
		if (sop.max_damage == -9999)
			return (false);

		sop.max_damage += amt;

		// keep track of the last damage
		if (amt < 0)
			u.LastDamage = (short) -amt;

		return (true);
	}

	public static boolean SopCheckKill(Sector_Object sop) {
		boolean killed = false;

		if (TEST(sop.flags, SOBJ_BROKEN))
			return (false);

		// does not have damage
		if (sop.max_damage == -9999)
			return (killed);

		if (sop.max_damage <= 0) {
			// returns whether so was collapsed(killed)
			killed = TestKillSectorObject(sop);
			if (!killed) {
				VehicleSetSmoke(sop, SpawnVehicleSmoke);
				sop.flags |= (SOBJ_BROKEN);
			}
		}

		return (killed);
	}

	public static boolean ActorPain(int SpriteNum) {
		USER u = pUser[SpriteNum];

		// if (u.LastDamage < u.PainThreshold) // This doesn't work well at all because
		// of
		// uzi/shotgun damages
		switch (u.ID) {
		case TOILETGIRL_R0:
		case WASHGIRL_R0:
		case CARGIRL_R0:
		case MECHANICGIRL_R0:
		case SAILORGIRL_R0:
		case PRUNEGIRL_R0:
			u.FlagOwner = 1;
			break;
		}

		if (RANDOM_RANGE(1000) < 875 || u.WaitTics > 0)
			return (false);

		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			if (u.ActorActionSet != null && u.ActorActionSet.Pain != null) {
				ActorLeaveTrack(SpriteNum);
				u.WaitTics = 60;
				NewStateGroup(SpriteNum, u.ActorActionSet.Pain);
				return (true);
			}
		}

		return (false);
	}

	public static boolean ActorPainPlasma(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING | SPR_ELECTRO_TOLERANT)) {
			if (u.ActorActionSet != null && u.ActorActionSet.Pain != null) {
				u.WaitTics = PLASMA_FOUNTAIN_TIME;
				NewStateGroup(SpriteNum, u.ActorActionSet.Pain);
				return (true);
			} else {
				u.Vis = PLASMA_FOUNTAIN_TIME;
				InitActorPause(SpriteNum);
			}
		}

		return (false);
	}

	public static boolean ActorStdMissile(int SpriteNum, int Weapon) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		SPRITE wp = sprite[Weapon];
		USER wu = pUser[Weapon];

		// Attack the player that is attacking you
		// Only if hes still alive
		if (wp.owner >= 0 && pUser[wp.owner] != null) {
			// Commented out line will make actors attack each other, it's been removed as
			// feature
			if (pUser[wp.owner].PlayerP != -1 && sp.owner != wp.owner) {
				u.tgt_sp = wp.owner;
			}
		}

		// Reset the weapons target before dying
		if (wu.WpnGoal >= 0) {
			// attempt to see if it was killed
			// what if the target has been killed? This will crash!
			pUser[wu.WpnGoal].Flags &= ~(SPR_TARGETED);
		}

		return false;
	}

	public static boolean ActorDamageSlide(int SpriteNum, int damage, int ang) {
		USER u = pUser[SpriteNum];
		int slide_vel, slide_dec;

		if (TEST(u.Flags, SPR_CLIMBING))
			return (false);

		damage = klabs(damage);

		if (damage == 0)
			return (false);

		if (damage <= 10) {
			DoActorBeginSlide(SpriteNum, ang, 64, 5);
			return (true);
		} else if (damage <= 20) {
			DoActorBeginSlide(SpriteNum, ang, 128, 5);
			return (true);
		} else {
			slide_vel = (damage * 6) - (u.MaxHealth);

			if (slide_vel < -1000)
				slide_vel = -1000;
			slide_dec = 5;

			DoActorBeginSlide(SpriteNum, ang, slide_vel, slide_dec);

			return (true);
		}
	}

	public static boolean PlayerDamageSlide(PlayerStr pp, int damage, int ang) {
		int slide_vel;

		damage = klabs(damage);

		if (damage == 0)
			return (false);

		if (damage <= 5) {
			// nudge
			return (false);
		} else if (damage <= 10) {
			// nudge
			pp.slide_xvect = MOVEx(16, ang) << 15;
			pp.slide_yvect = MOVEy(16, ang) << 15;
			return (true);
		} else if (damage <= 20) {
			// bigger nudge
			pp.slide_xvect = MOVEx(64, ang) << 15;
			pp.slide_yvect = MOVEy(64, ang) << 15;
			return (true);
		} else {
			slide_vel = (damage * 6);

			pp.slide_xvect = MOVEx(slide_vel, ang) << 15;
			pp.slide_yvect = MOVEy(slide_vel, ang) << 15;

			return (true);
		}
	}

	public static int GetDamage(int SpriteNum, int Weapon, int DamageNdx) {
		DAMAGE_DATA d = DamageData[DamageNdx];

		// if ndx does radius
		if (d.radius > 0) {
			SPRITE sp = sprite[SpriteNum];
			SPRITE wp = sprite[Weapon];
			int dist;
			int damage_per_pixel, damage_force, damage_amt;

			dist = DISTANCE(wp.x, wp.y, sp.x, sp.y);

			// take off the box around the player or else you'll never get
			// the max_damage;
			dist -= (sp.clipdist) << (2);

			if (dist < 0)
				dist = 0;

			if (dist < d.radius) {
				damage_per_pixel = (d.damage_hi << 16) / d.radius;

				// the closer your distance is to 0 the more damage
				damage_force = (d.radius - dist);
				damage_amt = -((damage_force * damage_per_pixel) >> 16);

				// formula: damage_amt = 75% + random(25%)
				return (DIV2(damage_amt) + DIV4(damage_amt) + RANDOM_RANGE(DIV4(damage_amt)));
			} else {
				return (0);
			}
		}

		return (-(d.damage_lo + RANDOM_RANGE(d.damage_hi - d.damage_lo)));
	}

	public static int RadiusGetDamage(short SpriteNum, short Weapon, long max_damage) {
		SPRITE sp = sprite[SpriteNum];
		SPRITE wp = sprite[Weapon];
		USER wu = pUser[Weapon];
		int dist;
		int damage_per_pixel, damage_force, damage_amt;

		max_damage = klabs(max_damage);

		dist = DISTANCE(wp.x, wp.y, sp.x, sp.y);

		// take off the box around the player or else you'll never get
		// the max_damage;
		dist -= (sp.clipdist) << (2);

		if (dist < wu.Radius) {
			damage_per_pixel = (int) ((max_damage << 16) / wu.Radius);

			// the closer your distance is to 0 the more damage
			damage_force = (wu.Radius - dist);
			damage_amt = -((damage_force * damage_per_pixel) >> 16);
		} else {
			damage_amt = 0;
		}

		return (damage_amt);
	}

	public static boolean PlayerCheckDeath(PlayerStr pp, int Weapon) {
		SPRITE sp = pp.getSprite();
		USER u = pUser[pp.PlayerSprite];

		// Store off what player was struck by
		pp.HitBy = (short) Weapon;

		if (u.Health <= 0 && !TEST(pp.Flags, PF_DEAD)) {

			// pick a death type
			if (u.LastDamage >= PLAYER_DEATH_EXPLODE_DAMMAGE_AMT)
				pp.DeathType = PLAYER_DEATH_EXPLODE;
			else if (u.LastDamage >= PLAYER_DEATH_CRUMBLE_DAMMAGE_AMT)
				pp.DeathType = PLAYER_DEATH_CRUMBLE;
			else
				pp.DeathType = PLAYER_DEATH_FLIP;

			if (Weapon < 0) {
				pp.Killer = -1;
				DoPlayerBeginDie(pp.pnum);
				return (true);
			}

			SPRITE wp = sprite[Weapon];
			USER wu = pUser[Weapon];

			if (Weapon > -1 && wu.ID == RIPPER_RUN_R0 || wu.ID == RIPPER2_RUN_R0)
				pp.DeathType = PLAYER_DEATH_RIPPER;

			if (Weapon > -1 && wu.ID == CALTROPS)
				pp.DeathType = PLAYER_DEATH_FLIP;

			if (Weapon > -1 && wu.ID == NINJA_RUN_R0 && wu.PlayerP != -1) {

				pp.DeathType = PLAYER_DEATH_FLIP;
				Player[wu.PlayerP].Bloody = true;
			}

			// keep track of who killed you for death purposes
			// need to check all Killer variables when an enemy dies
			if (pp.Killer < 0) {
				if (wp.owner >= 0)
					pp.Killer = wp.owner;
				else if (TEST(wp.extra, SPRX_PLAYER_OR_ENEMY))
					pp.Killer = (short) Weapon;
			}

			// start the death process
			DoPlayerBeginDie(pp.pnum);

			// for death direction
			// u.slide_ang = wp.ang;
			u.slide_ang = engine.getangle(sp.x - wp.x, sp.y - wp.y);
			// for death velocity
			u.slide_vel = u.LastDamage * 5;

			return (true);
		}

		return (false);
	}

	public static boolean PlayerTakeDamage(PlayerStr pp, int Weapon) {
		int pnum = pp.pnum;
		USER u = pUser[pp.PlayerSprite];

		if (Weapon < 0)
			return (true);

		SPRITE wp = sprite[Weapon];
		USER wu = pUser[Weapon];

		if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_NONE) {
			// ZOMBIE special case for single play
			if (wu.ID == ZOMBIE_RUN_R0) {
				// if weapons owner the player
				if (wp.owner == pp.PlayerSprite)
					return (false);
			}

			return (true);
		}

		if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COOPERATIVE) {
			// everything hurts you
			if (gNet.HurtTeammate)
				return (true);

			// if weapon IS the YOURSELF take damage
			if (wu.PlayerP == pnum)
				return (true);

			// if the weapons owner is YOURSELF take damage
			if (wp.owner >= 0 && pUser[wp.owner] != null && pUser[wp.owner].PlayerP != -1
					&& pUser[wp.owner].PlayerP == pnum)
				return (true);

			// if weapon IS the player no damage
			if (wu.PlayerP != -1)
				return (false);

			// if the weapons owner is a player
			if (wp.owner >= 0 && pUser[wp.owner] != null && pUser[wp.owner].PlayerP != -1)
				return (false);
		} else if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COMMBAT && gNet.TeamPlay) {
			// everything hurts you
			if (gNet.HurtTeammate)
				return (true);

			// if weapon IS the YOURSELF take damage
			if (wu.PlayerP == pnum)
				return (true);

			// if the weapons owner is YOURSELF take damage
			if (wp.owner >= 0 && pUser[wp.owner] != null && pUser[wp.owner].PlayerP != -1
					&& pUser[wp.owner].PlayerP == pnum)
				return (true);

			if (wu.PlayerP != -1) {
				// if both on the same team then no damage
				if (wu.spal == u.spal)
					return (false);
			}

			// if the weapons owner is a player
			if (wp.owner >= 0 && pUser[wp.owner] != null && pUser[wp.owner].PlayerP != -1) {
				// if both on the same team then no damage
				if (pUser[wp.owner].spal == u.spal)
					return (false);
			}
		}

		return (true);
	}

	public static void StarBlood(int SpriteNum, int Weapon) {
		USER u = pUser[SpriteNum];
		short blood_num = 1;
		short i;

		if (u.Health <= 0)
			blood_num = 4;

		for (i = 0; i < blood_num; i++)
			SpawnBlood(SpriteNum, Weapon, 0, 0, 0, 0);
	}

	public static short ANG2PLAYER(PlayerStr pp, SPRITE sp) {
		return (engine.getangle((pp).posx - (sp).x, (pp).posy - (sp).y));
	}

	public static short ANG2SPRITE(SPRITE sp, SPRITE op) {
		return (engine.getangle((sp).x - (op).x, (sp).y - (op).y));
	}

	/*
	 *
	 * !AIC KEY - This is where damage is assesed when missiles hit actors and other
	 * objects.
	 *
	 */
	public static int DoDamage(int SpriteNum, int Weapon) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		SPRITE wp;
		USER wu;
		int damage = 0;

		// don't hit a dead player
		if (u.PlayerP != -1 && TEST(Player[u.PlayerP].Flags, PF_DEAD)) {
			SpawnBlood(SpriteNum, Weapon, 0, 0, 0, 0);
			return (0);
		}

		if (TEST(u.Flags, SPR_SUICIDE))
			return (0);

		wp = sprite[Weapon];
		wu = pUser[Weapon];

		if (TEST(wu.Flags, SPR_SUICIDE))
			return (0);

		if (u.Attrib != null && RANDOM_P2(1024) > 850)
			PlaySpriteSound(SpriteNum, Attrib_Snds.attr_pain, v3df_follow);

		if (TEST(u.Flags, SPR_DEAD)) {
			SpawnBlood(SpriteNum, Weapon, 0, 0, 0, 0);
			return (0);
		}

		// special case for shooting mines
		if (sp.statnum == STAT_MINE_STUCK) {
			SpawnMineExp(SpriteNum);
			KillSprite(SpriteNum);
			return (0);
		}

		// weapon is drivable object manned by player
		if (wu.PlayerP != -1 && Player[wu.PlayerP].sop != -1) {
			switch (SectorObject[Player[wu.PlayerP].sop].track) {
			case SO_TANK:
				damage = -200;

				if (u.sop_parent != -1) {
					break;
				} else if (u.PlayerP != -1) {
					PlayerStr PlayerPP = Player[u.PlayerP];
					PlayerDamageSlide(PlayerPP, damage, wp.ang);
					if (PlayerTakeDamage(PlayerPP, Weapon)) {
						PlayerUpdateHealth(PlayerPP, damage);
						PlayerCheckDeath(PlayerPP, Weapon);
					}
				} else {
					PlayerStr pp = Player[screenpeek];

					ActorHealth(SpriteNum, damage);
					if (u.Health <= 0) {
						char choosesnd = 0;
						// Random chance of taunting the AI's here
						if (STD_RANDOM_RANGE(1024) > 512 && pp == Player[myconnectindex]) {
							choosesnd = (char) RANDOM_RANGE(MAX_TAUNTAI);
							PlayerSound(TauntAIVocs[choosesnd], v3df_dontpan | v3df_follow,
									pp);
						}
						SpawnShrap(SpriteNum, Weapon);
						SetSuicide(SpriteNum);
						return (0);
					}
				}
				break;

			case SO_SPEED_BOAT:
				damage = -100;

				if (u.sop_parent != -1) {
					break;
				} else if (u.PlayerP != -1) {
					PlayerStr PlayerPP = Player[u.PlayerP];
					PlayerDamageSlide(PlayerPP, damage, wp.ang);
					if (PlayerTakeDamage(PlayerPP, Weapon)) {
						PlayerUpdateHealth(PlayerPP, damage);
						PlayerCheckDeath(PlayerPP, Weapon);
					}
				} else {
					ActorHealth(SpriteNum, damage);
					ActorPain(SpriteNum);
					ActorChooseDeath(SpriteNum, Weapon);
				}

				SpawnBlood(SpriteNum, Weapon, 0, 0, 0, 0);
				break;
			}
		}

		if (Player[0].PlayerSprite == Weapon) {

		}

		// weapon is the actor - no missile used - example swords, axes, etc
		switch (wu.ID) {
		case NINJA_RUN_R0:
			if (wu.WeaponNum == WPN_SWORD)
				damage = GetDamage(SpriteNum, Weapon, WPN_SWORD);
			else {
				damage = GetDamage(SpriteNum, Weapon, WPN_FIST);
				// Add damage for stronger attacks!
				switch (Player[wu.PlayerP].WpnKungFuMove) {
				case 1:
					damage -= 2 - RANDOM_RANGE(7);
					break;
				case 2:
					damage -= 5 - RANDOM_RANGE(10);
					break;
				}
				PlaySound(DIGI_CGTHIGHBONE, wp, v3df_follow | v3df_dontpan);
			}

			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				// Is the player blocking?
				if (PlayerPP.WpnKungFuMove == 3)
					damage /= 3;
				PlayerDamageSlide(PlayerPP, damage, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
				if (PlayerPP.Armor != 0)
					PlaySound(DIGI_ARMORHIT, PlayerPP,
							v3df_dontpan | v3df_follow | v3df_doppler);
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			SpawnBlood(SpriteNum, Weapon, 0, 0, 0, 0);

			break;

		case SKEL_RUN_R0:
		case COOLG_RUN_R0:
		case GORO_RUN_R0:

			damage = GetDamage(SpriteNum, Weapon, DMG_SKEL_SLASH);
			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				// Is the player blocking?
				if (PlayerPP.WpnKungFuMove == 3)
					damage /= 3;
				PlayerDamageSlide(PlayerPP, damage, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			SpawnBlood(SpriteNum, Weapon, 0, 0, 0, 0);

			break;

		case HORNET_RUN_R0:
			PlaySound(DIGI_HORNETSTING, sp, v3df_follow | v3df_dontpan);
			damage = GetDamage(SpriteNum, Weapon, DMG_HORNET_STING);
			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				// Is the player blocking?
				if (PlayerPP.WpnKungFuMove == 3)
					damage /= 3;
				PlayerDamageSlide(PlayerPP, damage, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			// SpawnBlood(SpriteNum, Weapon,0, 0, 0, 0);

			break;

		case EEL_RUN_R0:
			damage = GetDamage(SpriteNum, Weapon, DMG_RIPPER_SLASH);
			damage /= 3;
			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				// Is the player blocking?
				if (PlayerPP.WpnKungFuMove == 3)
					damage /= 3;
				PlayerDamageSlide(PlayerPP, damage, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			SpawnBlood(SpriteNum, Weapon, 0, 0, 0, 0);

			break;

		case RIPPER_RUN_R0:
			damage = GetDamage(SpriteNum, Weapon, DMG_RIPPER_SLASH);
			damage /= 3; // Little rippers aren't as tough.
			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				// Is the player blocking?
				if (PlayerPP.WpnKungFuMove == 3)
					damage /= 3;
				PlayerDamageSlide(PlayerPP, damage, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					if (PlayerCheckDeath(PlayerPP, Weapon)) {
						PlaySound(DIGI_RIPPERHEARTOUT, PlayerPP,
								v3df_dontpan | v3df_doppler);

						DoRipperRipHeart(Weapon);
					}
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			SpawnBlood(SpriteNum, Weapon, 0, 0, 0, 0);

			break;

		case RIPPER2_RUN_R0:
			damage = GetDamage(SpriteNum, Weapon, DMG_RIPPER_SLASH);
			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				// Is the player blocking?
				if (PlayerPP.WpnKungFuMove == 3)
					damage /= 3;
				PlayerDamageSlide(PlayerPP, damage, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					if (PlayerCheckDeath(PlayerPP, Weapon)) {
						PlaySound(DIGI_RIPPERHEARTOUT, PlayerPP,
								v3df_dontpan | v3df_doppler);

						DoRipper2RipHeart(Weapon);
					}
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			SpawnBlood(SpriteNum, Weapon, 0, 0, 0, 0);

			break;

		case BUNNY_RUN_R0:
			damage = GetDamage(SpriteNum, Weapon, DMG_RIPPER_SLASH);
			damage /= 3;
			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				// Is the player blocking?
				if (PlayerPP.WpnKungFuMove == 3)
					damage /= 3;
				PlayerDamageSlide(PlayerPP, damage, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					if (PlayerCheckDeath(PlayerPP, Weapon)) {
						DoBunnyRipHeart(Weapon);
					}
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			SpawnBlood(SpriteNum, Weapon, 0, 0, 0, 0);

			break;

		case SERP_RUN_R0:
			damage = GetDamage(SpriteNum, Weapon, DMG_SERP_SLASH);
			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				if (PlayerPP.WpnKungFuMove == 3)
					damage /= 3;
				PlayerDamageSlide(PlayerPP, damage / 4, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			SpawnBlood(SpriteNum, Weapon, 0, 0, 0, 0);
			SpawnBlood(SpriteNum, Weapon, 0, 0, 0, 0);
			SpawnBlood(SpriteNum, Weapon, 0, 0, 0, 0);

			break;

		case BLADE1:
		case BLADE2:
		case BLADE3:
		case 5011:

			if (wu.ID == 5011)
				damage = -(3 + (RANDOM_RANGE(4 << 8) >> 8));
			else
				damage = GetDamage(SpriteNum, Weapon, DMG_BLADE);
			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				if (PlayerPP.WpnKungFuMove == 3)
					damage /= 3;
				if ((u.BladeDamageTics -= synctics) < 0) {
					u.BladeDamageTics = DAMAGE_BLADE_TIME;
					PlayerDamageSlide(PlayerPP, damage, ANG2PLAYER(PlayerPP, wp));
					if (PlayerTakeDamage(PlayerPP, Weapon)) {
						PlayerUpdateHealth(PlayerPP, damage);
						PlayerCheckDeath(PlayerPP, Weapon);
					}
				}
			} else {
				if ((u.BladeDamageTics -= ACTORMOVETICS) < 0) {
					u.BladeDamageTics = DAMAGE_BLADE_TIME;
					ActorHealth(SpriteNum, damage);
				}

				ActorChooseDeath(SpriteNum, Weapon);
			}

			SpawnBlood(SpriteNum, Weapon, 0, 0, 0, 0);

			break;

		case STAR1:
		case CROSSBOLT:
			damage = GetDamage(SpriteNum, Weapon, WPN_STAR);

			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				// Is the player blocking?
				if (PlayerPP.WpnKungFuMove == 3)
					damage /= 3;
				PlayerDamageSlide(PlayerPP, damage, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
				if (PlayerPP.Armor != 0)
					PlaySound(DIGI_ARMORHIT, PlayerPP,
							v3df_dontpan | v3df_follow | v3df_doppler);
			} else {

				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorStdMissile(SpriteNum, Weapon);
				ActorDamageSlide(SpriteNum, damage, wp.ang);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			StarBlood(SpriteNum, Weapon);

			wu.ID = 0;
			SetSuicide(Weapon);
			break;

		case SPEAR_R0:
			damage = GetDamage(SpriteNum, Weapon, DMG_SPEAR_TRAP);

			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				PlayerDamageSlide(PlayerPP, damage, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
				if (PlayerPP.Armor != 0)
					PlaySound(DIGI_ARMORHIT, PlayerPP,
							v3df_dontpan | v3df_follow | v3df_doppler);
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorStdMissile(SpriteNum, Weapon);
				ActorDamageSlide(SpriteNum, damage, wp.ang);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			SpawnBlood(SpriteNum, Weapon, 0, 0, 0, 0);

			wu.ID = 0;
			SetSuicide(Weapon);
			break;

		case LAVA_BOULDER:
			damage = GetDamage(SpriteNum, Weapon, DMG_LAVA_BOULDER);

			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				PlayerDamageSlide(PlayerPP, damage, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorStdMissile(SpriteNum, Weapon);
				ActorDamageSlide(SpriteNum, damage, wp.ang);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			SpawnBlood(SpriteNum, Weapon, 0, 0, 0, 0);

			wu.ID = 0;
			SetSuicide(Weapon);
			break;

		case LAVA_SHARD:
			damage = GetDamage(SpriteNum, Weapon, DMG_LAVA_SHARD);

			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				PlayerDamageSlide(PlayerPP, damage, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorStdMissile(SpriteNum, Weapon);
				ActorDamageSlide(SpriteNum, damage, wp.ang);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			SpawnBlood(SpriteNum, Weapon, 0, 0, 0, 0);

			wu.ID = 0;
			SetSuicide(Weapon);
			break;

		case UZI_SMOKE:
		case UZI_SMOKE + 2:
			if (wu.ID == UZI_SMOKE)
				damage = GetDamage(SpriteNum, Weapon, WPN_UZI);
			else
				damage = GetDamage(SpriteNum, Weapon, WPN_UZI) / 3; // Enemy Uzi, 1/3 damage

			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				// PlayerDamageSlide(PlayerPP, damage, ANG2PLAYER(PlayerPP, wp));
				PlayerDamageSlide(PlayerPP, damage / 2, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
				if (PlayerPP.Armor != 0)
					PlaySound(DIGI_ARMORHIT, PlayerPP,
							v3df_dontpan | v3df_follow | v3df_doppler);
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorStdMissile(SpriteNum, Weapon);
				ActorDamageSlide(SpriteNum, damage, wp.ang);
				ActorChooseDeath(SpriteNum, Weapon);
				switch (u.ID) {
				case TRASHCAN:
				case PACHINKO1:
				case PACHINKO2:
				case PACHINKO3:
				case PACHINKO4:
				case 623:
				case ZILLA_RUN_R0:
					break;
				default:
					if (RANDOM_RANGE(1000) > 900)
						InitBloodSpray(SpriteNum, false, 105);
					if (RANDOM_RANGE(1000) > 900)
						SpawnMidSplash(SpriteNum);
					break;
				}
			}

			// SpawnBlood(SpriteNum, Weapon,0, 0, 0, 0);
			// reset id so no more damage is taken
			wu.ID = 0;
			break;

		case SHOTGUN_SMOKE:
			damage = GetDamage(SpriteNum, Weapon, WPN_SHOTGUN);

			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				PlayerDamageSlide(PlayerPP, damage, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
				if (PlayerPP.Armor != 0)
					PlaySound(DIGI_ARMORHIT, PlayerPP,
							v3df_dontpan | v3df_follow | v3df_doppler);
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorStdMissile(SpriteNum, Weapon);
				ActorDamageSlide(SpriteNum, damage, wp.ang);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			// SpawnBlood(SpriteNum, Weapon,0, 0, 0, 0);
			switch (u.ID) {
			case TRASHCAN:
			case PACHINKO1:
			case PACHINKO2:
			case PACHINKO3:
			case PACHINKO4:
			case 623:
			case ZILLA_RUN_R0:
				break;
			default:
				if (RANDOM_RANGE(1000) > 950)
					SpawnMidSplash(SpriteNum);
				break;
			}

			// reset id so no more damage is taken
			wu.ID = 0;
			break;

		case MIRV_METEOR:

			// damage = -DAMAGE_MIRV_METEOR;
			damage = GetDamage(SpriteNum, Weapon, DMG_MIRV_METEOR);
			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				PlayerDamageSlide(PlayerPP, damage, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorStdMissile(SpriteNum, Weapon);
			}

			SetSuicide(Weapon);
			break;

		case SERP_METEOR:

			// damage = -DAMAGE_SERP_METEOR;
			damage = GetDamage(SpriteNum, Weapon, DMG_SERP_METEOR);
			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				PlayerDamageSlide(PlayerPP, damage, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorStdMissile(SpriteNum, Weapon);
			}

			SetSuicide(Weapon);
			break;

		case BOLT_THINMAN_R0:
			damage = GetDamage(SpriteNum, Weapon, WPN_ROCKET);

			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorStdMissile(SpriteNum, Weapon);
			}

			if (wu.Radius == NUKE_RADIUS)
				SpawnNuclearExp(Weapon);
			else
				SpawnBoltExp(Weapon);
			SetSuicide(Weapon);
			break;

		case BOLT_THINMAN_R1:
			// damage = -(2000 + (65 + RANDOM_RANGE(40))); // -2000 makes armor not count
			damage = -(65 + RANDOM_RANGE(40));

			if (u.sop_parent != -1) {
				Sector_Object sopp = SectorObject[u.sop_parent];
				if (TEST(sopp.flags, SOBJ_DIE_HARD))
					break;
				SopDamage(sopp, damage);
				SopCheckKill(sopp);
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
				if (PlayerPP.Armor != 0)
					PlaySound(DIGI_ARMORHIT, PlayerPP,
							v3df_dontpan | v3df_follow | v3df_doppler);
			} else {
				// this is special code to prevent the Zombie from taking out the Bosses to
				// quick
				// if rail gun weapon owner is not player
				if (wp.owner >= 0 && pUser[wp.owner] != null && pUser[wp.owner].PlayerP == -1) {
					// if actor is a boss
					if (u.ID == ZILLA_RUN_R0 || u.ID == SERP_RUN_R0 || u.ID == SUMO_RUN_R0)
						damage /= 2;
				}

				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorStdMissile(SpriteNum, Weapon);
				ActorDamageSlide(SpriteNum, damage >> 1, wp.ang);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			wu.ID = 0; // No more damage
			SpawnTracerExp(Weapon);
			SetSuicide(Weapon);
			break;

		case BOLT_THINMAN_R2:
			damage = (GetDamage(SpriteNum, Weapon, WPN_ROCKET) / 2);

			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorStdMissile(SpriteNum, Weapon);
			}

			if (wu.Radius == NUKE_RADIUS)
				SpawnNuclearExp(Weapon);
			else
				SpawnBoltExp(Weapon);
			SetSuicide(Weapon);
			break;

		case BOLT_THINMAN_R4:
			damage = GetDamage(SpriteNum, Weapon, DMG_GRENADE_EXP);

			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				PlayerDamageSlide(PlayerPP, damage, ANG2PLAYER(PlayerPP, wp));
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorDamageSlide(SpriteNum, damage, ANG2SPRITE(sp, wp));
				ActorChooseDeath(SpriteNum, Weapon);
			}

			SpawnBunnyExp(Weapon);
            SetSuicide(Weapon);
			break;

		case SUMO_RUN_R0:
			damage = GetDamage(SpriteNum, Weapon, DMG_FLASHBOMB);

			damage /= 3;
			if (u.sop_parent != -1) {
				Sector_Object sopp = SectorObject[u.sop_parent];
				if (TEST(sopp.flags, SOBJ_DIE_HARD))
					break;
				SopDamage(sopp, damage);
				SopCheckKill(sopp);
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				PlayerDamageSlide(PlayerPP, damage, ANG2PLAYER(PlayerPP, wp));
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorDamageSlide(SpriteNum, damage, ANG2SPRITE(sp, wp));
				ActorChooseDeath(SpriteNum, Weapon);
			}

			break;

		case BOLT_EXP:
			damage = GetDamage(SpriteNum, Weapon, DMG_BOLT_EXP);
			if (u.sop_parent != -1) {
				Sector_Object sopp = SectorObject[u.sop_parent];
				if (TEST(sopp.flags, SOBJ_DIE_HARD))
					break;
				SopDamage(sopp, damage);
				SopCheckKill(sopp);
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				PlayerDamageSlide(PlayerPP, damage, ANG2PLAYER(PlayerPP, wp));
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorDamageSlide(SpriteNum, damage, ANG2SPRITE(sp, wp));
				ActorChooseDeath(SpriteNum, Weapon);
			}

			break;

		case BLOOD_WORM:

			// Don't hurt blood worm zombies!
			if (u.ID == ZOMBIE_RUN_R0)
				break;

			damage = GetDamage(SpriteNum, Weapon, WPN_HEART);

			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				// PlayerDamageSlide(PlayerPP, damage, ANG2PLAYER(PlayerPP, wp));
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					if (PlayerCheckDeath(PlayerPP, Weapon)) {
						// degrade blood worm life
						wu.Counter3 += (4 * 120) / MISSILEMOVETICS;
					}
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorDamageSlide(SpriteNum, damage, ANG2SPRITE(sp, wp));
				ActorChooseDeath(SpriteNum, Weapon);
			}

			// degrade blood worm life
			wu.Counter3 += (2 * 120) / MISSILEMOVETICS;

			break;

		case TANK_SHELL_EXP:
			damage = GetDamage(SpriteNum, Weapon, DMG_TANK_SHELL_EXP);
			if (u.sop_parent != -1) {
				Sector_Object sopp = SectorObject[u.sop_parent];
				if (TEST(sopp.flags, SOBJ_DIE_HARD))
					break;
				SopDamage(sopp, damage);
				SopCheckKill(sopp);
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				PlayerDamageSlide(PlayerPP, damage, ANG2PLAYER(PlayerPP, wp));
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorDamageSlide(SpriteNum, damage, ANG2SPRITE(sp, wp));
				ActorChooseDeath(SpriteNum, Weapon);
			}

			break;

		case MUSHROOM_CLOUD:
		case GRENADE_EXP:
			if (wu.Radius == NUKE_RADIUS) // Special Nuke stuff
				damage = (GetDamage(SpriteNum, Weapon, DMG_NUCLEAR_EXP));
			else
				damage = GetDamage(SpriteNum, Weapon, DMG_GRENADE_EXP);

			if (u.sop_parent != -1) {
				Sector_Object sopp = SectorObject[u.sop_parent];
				if (TEST(sopp.flags, SOBJ_DIE_HARD))
					break;
				SopDamage(sopp, damage);
				SopCheckKill(sopp);
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				PlayerDamageSlide(PlayerPP, damage, ANG2PLAYER(PlayerPP, wp));
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				// Don't let it hurt the SUMO
				if (wp.owner != -1 && pUser[wp.owner] != null && pUser[wp.owner].ID == SUMO_RUN_R0)
					break;
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorDamageSlide(SpriteNum, damage, ANG2SPRITE(sp, wp));
				ActorChooseDeath(SpriteNum, Weapon);
			}

			break;

		case MICRO_EXP:

			damage = GetDamage(SpriteNum, Weapon, DMG_MINE_EXP);

			if (u.sop_parent != -1) {
				Sector_Object sopp = SectorObject[u.sop_parent];
				if (TEST(sopp.flags, SOBJ_DIE_HARD))
					break;
				SopDamage(sopp, damage);
				SopCheckKill(sopp);
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				PlayerDamageSlide(PlayerPP, damage, ANG2PLAYER(PlayerPP, wp));
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorDamageSlide(SpriteNum, damage, ANG2SPRITE(sp, wp));
				ActorChooseDeath(SpriteNum, Weapon);
			}
			break;

		case MINE_EXP:
			damage = GetDamage(SpriteNum, Weapon, DMG_MINE_EXP);
			if (wp.owner != -1 && pUser[wp.owner] != null && pUser[wp.owner].ID == SERP_RUN_R0) {
				damage /= 6;
			}

			if (u.sop_parent != -1) {
				Sector_Object sopp = SectorObject[u.sop_parent];
				if (TEST(sopp.flags, SOBJ_DIE_HARD))
					break;
				SopDamage(sopp, damage);
				SopCheckKill(sopp);
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				PlayerDamageSlide(PlayerPP, damage, ANG2PLAYER(PlayerPP, wp));
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				// Don't let serp skulls hurt the Serpent God
				if (wp.owner != -1 && pUser[wp.owner] != null && pUser[wp.owner].ID == SERP_RUN_R0)
					break;
				// Don't let it hurt the SUMO
				if (wp.owner != -1 && pUser[wp.owner] != null && pUser[wp.owner].ID == SUMO_RUN_R0)
					break;
				if (u.ID == TRASHCAN)
					ActorHealth(SpriteNum, -500);
				else
					ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorDamageSlide(SpriteNum, damage, ANG2SPRITE(sp, wp));
				ActorChooseDeath(SpriteNum, Weapon);
			}

			// reset id so no more damage is taken
			wu.ID = 0;
			break;

		case NAP_EXP:

			damage = GetDamage(SpriteNum, Weapon, DMG_NAPALM_EXP);

			// Sumo Nap does less
			if (wp.owner != -1 && pUser[wp.owner] != null && pUser[wp.owner].ID == SUMO_RUN_R0)
				damage /= 4;

			if (u.sop_parent != -1) {
				Sector_Object sopp = SectorObject[u.sop_parent];
				if (TEST(sopp.flags, SOBJ_DIE_HARD))
					break;
				SopDamage(sopp, damage);
				SopCheckKill(sopp);
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				PlayerDamageSlide(PlayerPP, damage, ANG2PLAYER(PlayerPP, wp));
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				// Don't let it hurt the SUMO
				if (wp.owner != -1 && pUser[wp.owner] != null && pUser[wp.owner].ID == SUMO_RUN_R0)
					break;
				ActorHealth(SpriteNum, damage);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			SetSuicide(Weapon);
			break;

		case Vomit1:
		case Vomit2:

			damage = GetDamage(SpriteNum, Weapon, DMG_VOMIT);
			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				PlayerDamageSlide(PlayerPP, damage, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			SetSuicide(Weapon);
			break;

		case COOLG_FIRE:
			damage = GetDamage(SpriteNum, Weapon, DMG_COOLG_FIRE);
			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				PlayerDamageSlide(PlayerPP, damage, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorChooseDeath(SpriteNum, Weapon);
			}

//			        u.ID = 0;
			SetSuicide(Weapon);
			break;

		// Skull Exp
		case SKULL_R0:
		case BETTY_R0:

			damage = GetDamage(SpriteNum, Weapon, DMG_SKULL_EXP);

			if (u.sop_parent != -1) {
				Sector_Object sopp = SectorObject[u.sop_parent];
				if (TEST(sopp.flags, SOBJ_DIE_HARD))
					break;
				SopDamage(sopp, damage);
				SopCheckKill(sopp);
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				PlayerDamageSlide(PlayerPP, damage, ANG2PLAYER(PlayerPP, wp));
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorDamageSlide(SpriteNum, damage, ANG2SPRITE(sp, wp));
				ActorChooseDeath(SpriteNum, Weapon);
			}

			break;

		// Serp ring of skull
		case SKULL_SERP:
			// DoSkullBeginDeath(Weapon);
			break;

		case FIREBALL1:

			damage = GetDamage(SpriteNum, Weapon, WPN_HOTHEAD);
			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				PlayerDamageSlide(PlayerPP, damage, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorStdMissile(SpriteNum, Weapon);
				ActorDamageSlide(SpriteNum, damage, wp.ang);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			if (wp.owner >= 0 && pUser[wp.owner] != null) // For SerpGod Ring
				pUser[wp.owner].Counter--;
			SpawnFireballFlames(Weapon, SpriteNum);
			SetSuicide(Weapon);
			break;

		case FIREBALL:
		case GORO_FIREBALL:

			damage = GetDamage(SpriteNum, Weapon, DMG_GORO_FIREBALL);
			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				PlayerDamageSlide(PlayerPP, damage, wp.ang);
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
				if (PlayerPP.Armor != 0)
					PlaySound(DIGI_ARMORHIT, PlayerPP,
							v3df_dontpan | v3df_follow | v3df_doppler);
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorStdMissile(SpriteNum, Weapon);
				ActorDamageSlide(SpriteNum, damage, wp.ang);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			SpawnGoroFireballExp(Weapon);
			SetSuicide(Weapon);
			break;

		case FIREBALL_FLAMES:

			damage = -DamageData[DMG_FIREBALL_FLAMES].damage_lo;

			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			// SpawnFireballFlames(Weapon, SpriteNum);

			break;

		case RADIATION_CLOUD:

			damage = GetDamage(SpriteNum, Weapon, DMG_RADIATION_CLOUD);

			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					PlayerStr pp = PlayerPP;

					PlayerSound(DIGI_GASHURT, v3df_dontpan | v3df_follow | v3df_doppler, pp);
					PlayerUpdateHealth(PlayerPP, damage - 1000);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				// Don't let it hurt the SUMO
				if (wp.owner != -1 && pUser[wp.owner] != null && pUser[wp.owner].ID == SUMO_RUN_R0)
					break;
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorChooseDeath(SpriteNum, Weapon);
			}

//			        u.ID = 0;
			wu.ID = 0;
			break;

		case PLASMA:
			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {

			} else {
				if (u.ID == SKULL_R0 || u.ID == BETTY_R0) {
					ActorHealth(SpriteNum, damage);
					ActorStdMissile(SpriteNum, Weapon);
					ActorChooseDeath(SpriteNum, Weapon);
					SetSuicide(Weapon);
					break;
				} else if (u.ID == RIPPER_RUN_R0) {
					DoRipperGrow(SpriteNum);
					break;
				}

				ActorPainPlasma(SpriteNum);
			}

			InitPlasmaFountain(wp, SpriteNum);

			SetSuicide(Weapon);

			break;

		case CALTROPS:
			damage = GetDamage(SpriteNum, Weapon, DMG_MINE_SHRAP);
			if (u.sop_parent != -1) {
				break;
			} else if (u.PlayerP != -1) {
				PlayerStr PlayerPP = Player[u.PlayerP];
				if (PlayerTakeDamage(PlayerPP, Weapon)) {
					if (RANDOM_P2(1024 << 4) >> 4 < 800)
						PlayerSound(DIGI_STEPONCALTROPS, v3df_follow | v3df_dontpan, PlayerPP);
					PlayerUpdateHealth(PlayerPP, damage);
					PlayerCheckDeath(PlayerPP, Weapon);
				}
			} else {
				ActorHealth(SpriteNum, damage);
				ActorPain(SpriteNum);
				ActorStdMissile(SpriteNum, Weapon);
				ActorChooseDeath(SpriteNum, Weapon);
			}

			SetSuicide(Weapon);
			break;

		}

		// If player take alot of damage, make him yell
		if (u != null && u.PlayerP != -1) {
			PlayerStr PlayerPP = Player[u.PlayerP];
			if (damage <= -40 && RANDOM_RANGE(1000) > 700)
				PlayerSound(DIGI_SONOFABITCH, v3df_dontpan | v3df_follow, PlayerPP);
			else if (damage <= -40 && RANDOM_RANGE(1000) > 700)
				PlayerSound(DIGI_PAINFORWEAK, v3df_dontpan | v3df_follow, PlayerPP);
			else if (damage <= -10)
				PlayerSound(PlayerPainVocs[RANDOM_RANGE(MAX_PAIN)], v3df_dontpan | v3df_follow,
						PlayerPP);
		}

		return (0);
	}

	// Select death text based on ID
	public static String DeathString(short SpriteNum) {
		USER ku = pUser[SpriteNum];

		switch (ku.ID) {
		case NINJA_RUN_R0:
			return (" ");
		case ZOMBIE_RUN_R0:
			return ("Zombie");
		case BLOOD_WORM:
			return ("Blood Worm");
		case SKEL_RUN_R0:
			return ("Skeletor Priest");
		case COOLG_RUN_R0:
			return ("Coolie Ghost");
		case GORO_RUN_R0:
			return ("Guardian");
		case HORNET_RUN_R0:
			return ("Hornet");
		case RIPPER_RUN_R0:
			return ("Ripper Hatchling");
		case RIPPER2_RUN_R0:
			return ("Ripper");
		case BUNNY_RUN_R0:
			return ("Killer Rabbit");
		case SERP_RUN_R0:
			return ("Serpent god");
		case GIRLNINJA_RUN_R0:
			return ("Girl Ninja");
		case BLADE1:
		case BLADE2:
		case BLADE3:
		case 5011:
			return ("blade");
		case STAR1:
			if (gs.UseDarts)
				return("dart");
			return ("shuriken");
		case CROSSBOLT:
			return ("crossbow bolt");
		case SPEAR_R0:
			return ("spear");
		case LAVA_BOULDER:
		case LAVA_SHARD:
			return ("lava boulder");
		case UZI_SMOKE:
			return ("Uzi");
		case UZI_SMOKE + 2:
			return ("Evil Ninja Uzi");
		case SHOTGUN_SMOKE:
			return ("shotgun");
		case MIRV_METEOR:
		case SERP_METEOR:
			return ("meteor");
		case BOLT_THINMAN_R0:
			return ("rocket");
		case BOLT_THINMAN_R1:
			return ("rail gun");
		case BOLT_THINMAN_R2:
			return ("enemy rocket");
		case BOLT_THINMAN_R4: // BunnyRocket
			return ("bunny rocket");
		case BOLT_EXP:
			return ("explosion");
		case TANK_SHELL_EXP:
			return ("tank shell");
		case MUSHROOM_CLOUD:
			return ("nuclear bomb");
		case GRENADE_EXP:
			return ("40mm grenade");
		case MICRO_EXP:
			return ("micro missile");
		case MINE_EXP:
			// case MINE_SHRAP:
			return ("sticky bomb");
		case NAP_EXP:
			return ("napalm");
		case Vomit1:
		case Vomit2:
			return ("vomit");
		case COOLG_FIRE:
			return ("Coolie Ghost phlem");
		case SKULL_R0:
			return ("Accursed Head");
		case BETTY_R0:
			return ("Bouncing Betty");
		case SKULL_SERP:
			return ("Serpent god Protector");
		case FIREBALL1:
		case FIREBALL:
		case GORO_FIREBALL:
		case FIREBALL_FLAMES:
			return ("flames");
		case RADIATION_CLOUD:
			return ("radiation");
		case CALTROPS:
			return ("caltrops");
		}

		return null;
	}

	public static int DoDamageTest(int Weapon) {
		SPRITE wp = sprite[Weapon];
		USER wu = pUser[Weapon];

		USER u;
		SPRITE sp;
		short i, nexti, stat;
		int dist;

		for (stat = 0; stat < StatDamageList.length; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				sp = sprite[i];
				u = pUser[i];

				dist = DISTANCE(sp.x, sp.y, wp.x, wp.y);
				if (dist > wu.Radius + u.Radius)
					continue;

				if (sp == wp)
					continue;

				if (!TEST(sp.cstat, CSTAT_SPRITE_BLOCK))
					continue;

				// !JIM! Put in a cansee so that you don't take damage through walls and such
				// For speed's sake, try limiting check only to radius weapons!
				if (wu.Radius > 200) {
					if (!FAFcansee(sp.x, sp.y, SPRITEp_UPPER(sp), sp.sectnum, wp.x, wp.y, wp.z, wp.sectnum))
						continue;
				}

				if (wp.owner != i && SpriteOverlap(Weapon, i)) {
					DoDamage(i, Weapon);
				}
			}
		}

		return (0);
	}

	public static int DoHitscanDamage(int Weapon, int hitsprite) {
		// this routine needs some sort of sprite generated from the hitscan
		// such as a smoke or spark sprite - reason is because of DoDamage()

		if (hitsprite == -1)
			return 0;

		for (short stat = 0; stat < StatDamageList.length; stat++) {
			if (sprite[hitsprite].statnum == StatDamageList[stat]) {
				DoDamage(hitsprite, Weapon);
				break;
			}
		}

		return (0);
	}

	public static int DoFlamesDamageTest(int Weapon) {
		SPRITE wp = sprite[Weapon];
		USER wu = pUser[Weapon];

		USER u;
		SPRITE sp;
		short i, nexti, stat;
		int dist;

		for (stat = 0; stat < StatDamageList.length; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];

				sp = sprite[i];
				u = pUser[i];

				switch (u.ID) {
				case TRASHCAN:
				case PACHINKO1:
				case PACHINKO2:
				case PACHINKO3:
				case PACHINKO4:
				case 623:
					continue;
				}

				dist = DISTANCE(sp.x, sp.y, wp.x, wp.y);

				if (dist > wu.Radius + u.Radius)
					continue;

				if (sp == wp)
					continue;

				if (!TEST(sp.cstat, CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN))
					continue;

				if (TEST(wp.cstat, CSTAT_SPRITE_INVISIBLE))
					continue;

				if (wu.Radius > 200) // Note: No weaps have bigger radius than 200 cept explosion stuff
				{
					if (FAFcansee(sp.x, sp.y, SPRITEp_MID(sp), sp.sectnum, wp.x, wp.y, SPRITEp_MID(wp), wp.sectnum)) {
						DoDamage(i, Weapon);
					}
				} else if (SpriteOverlap(Weapon, i)) {
					DoDamage(i, Weapon);
				}

			}
		}

		return (0);
	}

	public static short PrevWall(short wall_num) {
		short start_wall, prev_wall;

		start_wall = wall_num;

		do {
			prev_wall = wall_num;
			wall_num = wall[wall_num].point2;
		} while (wall_num != start_wall);

		return (prev_wall);
	}

	private static short[] sectlist = new short[MAXSECTORS]; // !JIM! Frank, 512 was not big enough for $dozer, was
																// asserting out!

	public static void TraverseBreakableWalls(short start_sect, int x, int y, int z, short ang, int radius) {
		int j, k;

		short sectlistplc, sectlistend, sect, startwall, endwall, nextsector;
		int xmid, ymid;
		int dist;
		short break_count;
		int hitz = 0;

		sectlist[0] = start_sect;
		sectlistplc = 0;
		sectlistend = 1;

		// limit radius
		if (radius > 2000)
			radius = 2000;

		break_count = 0;
		while (sectlistplc < sectlistend) {
			sect = sectlist[sectlistplc++];

			startwall = sector[sect].wallptr;
			endwall = (short) (startwall + sector[sect].wallnum);

			for (j = startwall; j < endwall - 1; j++) {
				// see if this wall should be broken
				if (wall[j].lotag == TAG_WALL_BREAK) {
					// find midpoint
					xmid = DIV2(wall[j].x + wall[j + 1].x);
					ymid = DIV2(wall[j].y + wall[j + 1].y);

					// don't need to go further if wall is too far out

					dist = Distance(xmid, ymid, x, y);
					if (dist > radius)
						continue;

					if (WallBreakPosition(j, tmp_ptr[0], tmp_ptr[1], tmp_ptr[2], tmp_ptr[3].set(hitz), tmp_ptr[4])) {
						short sectnum = (short) tmp_ptr[0].value;
						int hitx = tmp_ptr[1].value;
						int hity = tmp_ptr[2].value;
						hitz = tmp_ptr[3].value;
						if (hitx != MAXLONG && sectnum >= 0
								&& FAFcansee(x, y, z, start_sect, hitx, hity, hitz, sectnum)) {
							HitBreakWall(j, MAXLONG, MAXLONG, MAXLONG, ang, 0);

							break_count++;
							if (break_count > 4)
								return;
						}
					}
				}

				nextsector = wall[j].nextsector;

				if (nextsector < 0)
					continue;

				// make sure its not on the list
				for (k = sectlistend - 1; k >= 0; k--) {
					if (sectlist[k] == nextsector)
						break;
				}

				// if its not on the list add it to the end
				if (k < 0) {
					sectlist[sectlistend++] = nextsector;
				}
			}
		}
	}

	private static short StatBreakList[] = { STAT_DEFAULT, STAT_BREAKABLE, STAT_NO_STATE, STAT_DEAD_ACTOR, };

	public static int DoExpDamageTest(int Weapon) {
		SPRITE wp = sprite[Weapon];
		USER wu = pUser[Weapon];

		USER u;
		SPRITE sp;
		short i, nexti, stat;
		int dist;
		int max_stat;
		short break_count;

		SPRITE found_sp = null;
		int found_dist = 999999;

		// crack sprites
		if (wu.ID != MUSHROOM_CLOUD)
			WeaponExplodeSectorInRange(Weapon);

		// Just like DoDamageTest() except that it doesn't care about the owner

		max_stat = StatDamageList.length;
		// don't check for mines if the weapon is a mine
		if (wp.statnum == STAT_MINE_STUCK)
			max_stat--;

		for (stat = 0; stat < max_stat; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];

				sp = sprite[i];
				u = pUser[i];

				dist = DISTANCE(sp.x, sp.y, wp.x, wp.y);

				if (dist > wu.Radius + u.Radius)
					continue;

				if (sp == wp)
					continue;

				if (StatDamageList[stat] == STAT_SO_SP_CHILD) {
					DoDamage(i, Weapon);
				} else {
					if (FindDistance3D(sp.x - wp.x, sp.y - wp.y, (sp.z - wp.z) >> 4) > wu.Radius + u.Radius)
						continue;

					// added hitscan block because mines no long clip against actors/players
					if (!TEST(sp.cstat, CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN))
						continue;

					// Second parameter MUST have blocking bits set or cansee won't work
					// added second check for FAF water - hitscans were hitting ceiling
					if (!FAFcansee(wp.x, wp.y, wp.z, wp.sectnum, sp.x, sp.y, SPRITEp_UPPER(sp), sp.sectnum)
							&& !FAFcansee(wp.x, wp.y, wp.z, wp.sectnum, sp.x, sp.y, SPRITEp_LOWER(sp), sp.sectnum))
						continue;

					DoDamage(i, Weapon);
				}
			}
		}

		if (wu.ID == MUSHROOM_CLOUD)
			return (0); // Central Nuke doesn't break stuff
						// Only secondaries do that

		TraverseBreakableWalls(wp.sectnum, wp.x, wp.y, wp.z, wp.ang, wu.Radius);

		break_count = 0;
		max_stat = StatBreakList.length;
		// Breakable stuff
		for (stat = 0; stat < max_stat; stat++) {
			for (i = headspritestat[StatBreakList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				sp = sprite[i];
				u = pUser[i];

				dist = DISTANCE(sp.x, sp.y, wp.x, wp.y);
				if (dist > wu.Radius)
					continue;

				dist = FindDistance3D(sp.x - wp.x, sp.y - wp.y, (SPRITEp_MID(sp) - wp.z) >> 4);
				if (dist > wu.Radius)
					continue;

				if (!FAFcansee(sp.x, sp.y, SPRITEp_MID(sp), sp.sectnum, wp.x, wp.y, wp.z, wp.sectnum))
					continue;

				if (TEST(sp.extra, SPRX_BREAKABLE)) {
					HitBreakSprite(i, wu.ID);
					break_count++;
					if (break_count > 6)
						break;
				}
			}
		}

		if (wu.ID == BLOOD_WORM)
			return (0);

		int found_spi = -1;
		// wall damaging
		for (i = headspritestat[STAT_WALL_MOVE]; i != -1; i = nexti) {
			nexti = nextspritestat[i];

			sp = sprite[i];

			dist = DISTANCE(sp.x, sp.y, wp.x, wp.y);
			if (dist > wu.Radius / 4)
				continue;

			if (TEST_BOOL1(sp))
				continue;

			if (!CanSeeWallMove(wp, SP_TAG2(sp)))
				continue;

			if (dist < found_dist) {
				found_dist = dist;
				found_sp = sp;
				found_spi = i;
			}
		}

		if (found_sp != null) {
			if (SP_TAG2(found_sp) == 0) {
				// just do one
				DoWallMove(found_spi);
			} else {
				if (DoWallMoveMatch(SP_TAG2(found_sp))) {
					DoSpawnSpotsForDamage(SP_TAG2(found_sp));
				}
			}
		}

		return (0);
	}

	public static int DoMineExpMine(int Weapon) {
		SPRITE wp = sprite[Weapon];
		USER wu = pUser[Weapon];

		USER u;
		SPRITE sp;
		short i, nexti;
		int dist;
		int zdist;

		for (i = headspritestat[STAT_MINE_STUCK]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];
			u = pUser[i];

			dist = DISTANCE(sp.x, sp.y, wp.x, wp.y);
			if (dist > wu.Radius + u.Radius)
				continue;

			if (sp == wp)
				continue;

            if (!TEST(sp.cstat, CSTAT_SPRITE_BLOCK_HITSCAN))
				continue;

			// Explosions are spherical, not planes, so let's check that way, well
			// cylindrical at least.
			zdist = klabs(sp.z - wp.z) >> 4;
			if (SpriteOverlap(Weapon, i) || zdist < wu.Radius + u.Radius) {
				DoDamage(i, Weapon);
				// only explode one mine at a time
				break;
			}
		}

		return (0);
	}

	public static final int STAR_STICK_RNUM = 400;
	public static final int STAR_BOUNCE_RNUM = 600;

	public static boolean DoStar(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		USER su;
		int vel;

		if (TEST(u.Flags, SPR_UNDERWATER)) {
			u.motion_blur_num = 0;
			ScaleSpriteVector(Weapon, 54000);

			vel = engine.ksqrt(SQ(u.xchange) + SQ(u.ychange));

			if (vel > 100) {
				if ((RANDOM_P2(1024 << 4) >> 4) < 128)
					SpawnBubble(Weapon);
			}

			sp.z += 128 * MISSILEMOVETICS;

			DoActorZrange(Weapon);
			MissileWaterAdjust(Weapon);

			if (sp.z > u.loz) {
				KillSprite(Weapon);
				return (true);
			}
		} else {
			vel = engine.ksqrt(SQ(u.xchange) + SQ(u.ychange));

			if (vel < 800) {
				u.Counter += 50;
				u.zchange += u.Counter;
			}
		}

		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);
		// DoDamageTest(Weapon);

		if (u.ret != 0 && !TEST(u.Flags, SPR_UNDERWATER)) {
			switch (DTEST(u.ret, HIT_MASK)) {
			case HIT_PLAX_WALL:
				break;

			case HIT_WALL: {
				short hitwall, nw, wall_ang;
				WALL wph;

				hitwall = NORM_WALL(u.ret);
				wph = wall[hitwall];

				if (wph.lotag == TAG_WALL_BREAK) {
					HitBreakWall(hitwall, sp.x, sp.y, sp.z, sp.ang, u.ID);
					u.ret = 0;
					break;
				}

				// special case with MissileSetPos - don't queue star
				// from this routine
				if (TEST(u.Flags, SPR_SET_POS_DONT_KILL))
					break;

				// chance of sticking
				if (!TEST(u.Flags, SPR_BOUNCE) && RANDOM_P2(1024) < STAR_STICK_RNUM) {
					u.motion_blur_num = 0;
					ChangeState(Weapon, s_StarStuck[0]);
					sp.xrepeat -= 16;
					sp.yrepeat -= 16;
					sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
                    sp.clipdist = 16 >> 2;
					u.ceiling_dist = (short) Z(2);
					u.floor_dist = (short) Z(2);
					// treat this just like a KillSprite but don't kill
					QueueStar(Weapon);
					return false;
				}

				// chance of bouncing
				if (RANDOM_P2(1024) < STAR_BOUNCE_RNUM)
					break;

				nw = wall[hitwall].point2;
				wall_ang = NORM_ANGLE(engine.getangle(wall[nw].x - wph.x, wall[nw].y - wph.y) + 512);

				WallBounce(Weapon, wall_ang);
				ScaleSpriteVector(Weapon, 36000);
				u.Flags |= (SPR_BOUNCE);
				u.motion_blur_num = 0;
				u.ret = 0;
				break;
			}

			case HIT_SECTOR: {
				boolean hitwall;
				short hitsect = NORM_SECTOR(u.ret);

				if (sp.z > DIV2(u.hiz + u.loz)) {
					if (SectUser[hitsect] != null && SectUser[hitsect].depth > 0) {
						SpawnSplash(Weapon);
						KillSprite(Weapon);
						return (true);
						// hit water - will be taken care of in WeaponMoveHit
						// break;
					}
				}

				if (u.lo_sp != -1)
					if (sprite[u.lo_sp].lotag == TAG_SPRITE_HIT_MATCH)
						break;
				if (u.hi_sp != -1)
					if (sprite[u.hi_sp].lotag == TAG_SPRITE_HIT_MATCH)
						break;

				ScaleSpriteVector(Weapon, 58000);

				vel = engine.ksqrt(SQ(u.xchange) + SQ(u.ychange));

				if (vel < 500)
					break; // will be killed below - u.ret != 0

				// 32000 to 96000
				u.xchange = mulscale(u.xchange, 64000 + (RANDOM_RANGE(64000) - 32000), 16);
				u.ychange = mulscale(u.ychange, 64000 + (RANDOM_RANGE(64000) - 32000), 16);

				if (sp.z > DIV2(u.hiz + u.loz))
					u.zchange = mulscale(u.zchange, 50000, 16); // floor
				else
					u.zchange = mulscale(u.zchange, 40000, 16); // ceiling

				if (SlopeBounce(Weapon, tmp_ptr[0])) {
					hitwall = tmp_ptr[0].value != 0;
					if (hitwall) {
						// chance of sticking
						if (RANDOM_P2(1024) < STAR_STICK_RNUM)
							break;

						// chance of bouncing
						if (RANDOM_P2(1024) < STAR_BOUNCE_RNUM)
							break;

						u.Flags |= (SPR_BOUNCE);
						u.motion_blur_num = 0;
						u.ret = 0;

					} else {
						// hit a sloped sector < 45 degrees
						u.Flags |= (SPR_BOUNCE);
						u.motion_blur_num = 0;
						u.ret = 0;
					}

					// BREAK HERE - LOOOK !!!!!!!!!!!!!!!!!!!!!!!!
					break; // hit a slope
				}

				u.Flags |= (SPR_BOUNCE);
				u.motion_blur_num = 0;
				u.ret = 0;
				u.zchange = -u.zchange;

				// 32000 to 96000
				u.xchange = mulscale(u.xchange, 64000 + (RANDOM_RANGE(64000) - 32000), 16);
				u.ychange = mulscale(u.ychange, 64000 + (RANDOM_RANGE(64000) - 32000), 16);
				if (sp.z > DIV2(u.hiz + u.loz))
					u.zchange = mulscale(u.zchange, 50000, 16); // floor
				else
					u.zchange = mulscale(u.zchange, 40000, 16); // ceiling

				break;
			}
			}
		}

		if (u.ret != 0) {
			short hitsprite = NORM_SPRITE(u.ret);
			if (hitsprite != -1) {
				su = pUser[hitsprite];
				if (su != null && (su.ID == TRASHCAN || su.ID == ZILLA_RUN_R0))
					PlaySound(DIGI_STARCLINK, sp, v3df_none);
			}

			if (DTEST(u.ret, HIT_MASK) != HIT_SPRITE) // Don't clank on sprites
				PlaySound(DIGI_STARCLINK, sp, v3df_none);

			if (WeaponMoveHit(Weapon)) {
				KillSprite(Weapon);
				return (true);
			}
		}

		return (false);
	}

	public static boolean DoCrossBolt(int Weapon) {
		USER u = pUser[Weapon];

		u = pUser[Weapon];

		DoBlurExtend(Weapon, 0, 2);

		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, Z(16), Z(16), CLIPMASK_MISSILE, MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);

		if (u.ret != 0) {
			if (WeaponMoveHit(Weapon)) {
				switch (DTEST(u.ret, HIT_MASK)) {
				case HIT_SPRITE: {
					break;
				}
				}

				KillSprite(Weapon);

				return (true);
			}
		}

		return (false);
	}

	public static int DoPlasmaDone(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		sp.xrepeat += u.Counter;
		sp.yrepeat -= 4;
		u.Counter += 2;

		if (sp.yrepeat < 6) {
			KillSprite(Weapon);
			return (0);
		}

		return (0);
	}

	public static int PickEnemyTarget(int spi, int aware_range) {
		DoPickTarget(spi, aware_range, 0);

		for (int tsi = 0; tsi < TargetSortCount; tsi++) {
			Target_Sort ts = TargetSort[tsi];
			if (ts.sprite_num >= 0) {
				if (ts.sprite_num == sprite[spi].owner || sprite[ts.sprite_num].owner == sprite[spi].owner)
					continue;

				return (ts.sprite_num);
			}
		}

		return (-1);
	}

	public static int MissileSeek(int Weapon, int delay_tics, int aware_range, int dang_shift, int turn_limit,
			int z_limit) {

		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		int zh;
		short ang2tgt, delta_ang;

		if (u.WaitTics <= delay_tics)
			u.WaitTics += MISSILEMOVETICS;

		if (u.WpnGoal == -1) {
			if (u.WaitTics > delay_tics) {
				int hitsprite = -1;

				if (TEST(u.Flags2, SPR2_DONT_TARGET_OWNER)) {
					if ((hitsprite = PickEnemyTarget(Weapon, aware_range)) != -1) {
						USER hu = pUser[hitsprite];

						u.WpnGoal = (short) hitsprite;
						hu.Flags |= (SPR_TARGETED);
						hu.Flags |= (SPR_ATTACKED);
					}
				} else if ((hitsprite = DoPickTarget(Weapon, aware_range, 0)) != -1) {
					USER hu = pUser[hitsprite];

					u.WpnGoal = (short) hitsprite;
					hu.Flags |= (SPR_TARGETED);
					hu.Flags |= (SPR_ATTACKED);
				}
			}
		}

		if (u.WpnGoal >= 0) {
			SPRITE hp = sprite[pUser[Weapon].WpnGoal];

			// move to correct angle
			ang2tgt = engine.getangle(hp.x - sp.x, hp.y - sp.y);

			delta_ang = GetDeltaAngle(sp.ang, ang2tgt);

			if (klabs(delta_ang) > 32) {
				if (delta_ang > 0)
					delta_ang = 32;
				else
					delta_ang = -32;
			}

			sp.ang -= delta_ang;

			zh = SPRITEp_TOS(hp) + DIV4(SPRITEp_SIZE_Z(hp));

			delta_ang = (short) ((zh - sp.z) >> 1);

			if (klabs(delta_ang) > Z(16)) {
				if (delta_ang > 0)
					delta_ang = (short) Z(16);
				else
					delta_ang = (short) -Z(16);
			}

			sp.zvel = delta_ang;

			u.xchange = MOVEx(sp.xvel, sp.ang);
			u.ychange = MOVEy(sp.xvel, sp.ang);
			u.zchange = sp.zvel;
		}
		return (0);
	}

    // completely vector manipulation
	public static int VectorMissileSeek(int Weapon, int delay_tics, int turn_speed, int aware_range1,
			int aware_range2) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		int dist;
		int zh;
		if (u.WaitTics <= delay_tics)
			u.WaitTics += MISSILEMOVETICS;

		if (u.WpnGoal == -1) {
			if (u.WaitTics > delay_tics) {
				int hitsprite;

				if (TEST(u.Flags2, SPR2_DONT_TARGET_OWNER)) {
					if ((hitsprite = PickEnemyTarget(Weapon, aware_range1)) != -1) {
						USER hu = pUser[hitsprite];

						u.WpnGoal = (short) hitsprite;
						hu.Flags |= (SPR_TARGETED);
						hu.Flags |= (SPR_ATTACKED);
					} else if ((hitsprite = PickEnemyTarget(Weapon, aware_range2)) != -1) {
						USER hu = pUser[hitsprite];

						u.WpnGoal = (short) hitsprite;
						hu.Flags |= (SPR_TARGETED);
						hu.Flags |= (SPR_ATTACKED);
					}
				} else {
					if ((hitsprite = DoPickTarget(Weapon, aware_range1, 0)) != -1) {
						USER hu = pUser[hitsprite];

						u.WpnGoal = (short) hitsprite;
						hu.Flags |= (SPR_TARGETED);
						hu.Flags |= (SPR_ATTACKED);
					} else if ((hitsprite = DoPickTarget(Weapon, aware_range2, 0)) != -1) {
						USER hu = pUser[hitsprite];

						u.WpnGoal = (short) hitsprite;
						hu.Flags |= (SPR_TARGETED);
						hu.Flags |= (SPR_ATTACKED);
					}
				}
			}
		}

		if (u.WpnGoal >= 0) {
			int ox, oy, oz;
			SPRITE hp = sprite[pUser[Weapon].WpnGoal];

			if (hp == null)
				return (0);

			zh = SPRITEp_TOS(hp) + DIV4(SPRITEp_SIZE_Z(hp));

			dist = engine.ksqrt(SQ(sp.x - hp.x) + SQ(sp.y - hp.y) + (SQ(sp.z - zh) >> 8));

			ox = u.xchange;
			oy = u.ychange;
			oz = u.zchange;

			u.xchange = scale(sp.xvel, hp.x - sp.x, dist);
			u.ychange = scale(sp.xvel, hp.y - sp.y, dist);
			u.zchange = scale(sp.xvel, zh - sp.z, dist);

			// the large turn_speed is the slower the turn

			u.xchange = (u.xchange + ox * (turn_speed - 1)) / turn_speed;
			u.ychange = (u.ychange + oy * (turn_speed - 1)) / turn_speed;
			u.zchange = (u.zchange + oz * (turn_speed - 1)) / turn_speed;

			sp.ang = engine.getangle(u.xchange, u.ychange);
		}

		return (0);
	}

    public static int DoBlurExtend(int Weapon, int interval, int blur_num) {
		USER u = pUser[Weapon];

		if (u.motion_blur_num >= blur_num)
			return (0);

		u.Counter2++;
		if (u.Counter2 > interval)
			u.Counter2 = 0;

		if (u.Counter2 == 0) {
			u.motion_blur_num++;
			if (u.motion_blur_num > blur_num)
				u.motion_blur_num = (short) blur_num;
		}

		return (0);
	}

	public static int InitPlasmaFountain(SPRITE wp, int spnum) {
		SPRITE sp = sprite[spnum];
		SPRITE np;
		USER nu;
		int SpriteNum = SpawnSprite(STAT_MISSILE, PLASMA_FOUNTAIN, s_PlasmaFountain[0], sp.sectnum, sp.x, sp.y,
				SPRITEp_BOS(sp), sp.ang, 0);
		if(SpriteNum == -1)
			return 0;

		np = sprite[SpriteNum];
		nu = pUser[SpriteNum];

		np.shade = -40;
		if (wp != null)
			SetOwner(wp.owner, SpriteNum);
		SetAttach(spnum, SpriteNum);
		np.yrepeat = 0;
		np.clipdist = 8 >> 2;

		// start off on a random frame
		nu.WaitTics = 120 + 60;
		nu.Radius = 50;
		return (0);
	}

	public static int DoPlasmaFountain(int Weapon) {
		SPRITE sp = sprite[Weapon];
		SPRITE ap;
		USER u = pUser[Weapon];
		short bak_cstat;

		// if no owner then die
		if (u.Attach < 0) {
			KillSprite(Weapon);
			return (0);
		} else {
			ap = sprite[u.Attach];

			// move with sprite
			engine.setspritez(Weapon, ap.x, ap.y, ap.z);
			sp.ang = ap.ang;

			u.Counter++;
			if (u.Counter > 3)
				u.Counter = 0;

			if (u.Counter == 0) {
				SpawnBlood(u.Attach, Weapon, 0, 0, 0, 0);
				if (RANDOM_RANGE(1000) > 600)
					InitBloodSpray(u.Attach, false, 105);
			}
		}

		// kill the fountain
		if ((u.WaitTics -= MISSILEMOVETICS) <= 0) {
			u.WaitTics = 0;

			bak_cstat = sp.cstat;
			sp.cstat &= ~(CSTAT_SPRITE_BLOCK);
			// DoDamageTest(Weapon); // fountain not doing the damage an more
			sp.cstat = bak_cstat;

			KillSprite(Weapon);
		}
		return (0);
	}

	public static boolean DoPlasma(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		int dax, day, daz;
		int ox, oy, oz;

		ox = sp.x;
		oy = sp.y;
		oz = sp.z;

		// MissileSeek(SHORT Weapon, SHORT delay_tics, SHORT aware_range, SHORT
		// dang_shift, SHORT turn_limit, SHORT z_limit)
		// MissileSeek(Weapon, 20, 1024, 6, 80, 6);
		DoBlurExtend(Weapon, 0, 4);

		dax = MOVEx(sp.xvel, sp.ang);
		day = MOVEy(sp.xvel, sp.ang);
		daz = sp.zvel;

		u.ret = move_missile(Weapon, dax, day, daz, Z(16), Z(16), CLIPMASK_MISSILE, MISSILEMOVETICS);

		if (u.ret != 0) {
			// this sprite is supposed to go through players/enemys
			// if hit a player/enemy back up and do it again with blocking reset
			if (DTEST(u.ret, HIT_MASK) == HIT_SPRITE) {
				short hitsprite = NORM_SPRITE(u.ret);
				SPRITE hsp = sprite[hitsprite];
				USER hu = pUser[hitsprite];

				if (TEST(hsp.cstat, CSTAT_SPRITE_BLOCK) && !TEST(hsp.cstat, CSTAT_SPRITE_WALL)) {
					short hcstat = hsp.cstat;

					if (hu != null && hitsprite != u.WpnGoal) {
						sp.x = ox;
						sp.y = oy;
						sp.z = oz;

						hsp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
						u.ret = move_missile(Weapon, dax, day, daz, Z(16), Z(16), CLIPMASK_MISSILE, MISSILEMOVETICS);
						hsp.cstat = hcstat;
					}
				}
			}
		}

		MissileHitDiveArea(Weapon);
		if (TEST(u.Flags, SPR_UNDERWATER) && (RANDOM_P2(1024 << 4) >> 4) < 256)
			SpawnBubble(Weapon);

		if (u.ret != 0) {
			if (WeaponMoveHit(Weapon)) {
				if (TEST(u.Flags, SPR_SUICIDE)) {
					KillSprite(Weapon);
					return (true);
				} else {
					u.Counter = 4;
					ChangeState(Weapon, s_PlasmaDone[0]);
				}

				return (true);
			}
		}

		return (false);
	}

	public static boolean DoCoolgFire(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);
		if (TEST(u.Flags, SPR_UNDERWATER) && (RANDOM_P2(1024 << 4) >> 4) < 256)
			SpawnBubble(Weapon);

		if (u.ret != 0) {
			if (WeaponMoveHit(Weapon)) {
				PlaySound(DIGI_CGMAGICHIT, sp, v3df_follow);
				ChangeState(Weapon, s_CoolgFireDone[0]);
				if (sp.owner != -1 && pUser[sp.owner] != null && pUser[sp.owner].ID != RIPPER_RUN_R0)
					SpawnDemonFist(Weapon); // Just a red magic circle flash
				return (true);
			}
		}

		return (false);
	}

	public static boolean DoEelFire(int Weapon) {
		USER u = pUser[Weapon];

		if (TEST(u.Flags, SPR_UNDERWATER) && (RANDOM_P2(1024 << 4) >> 4) < 256)
			SpawnBubble(Weapon);

		return (false);
	}

	public static void ScaleSpriteVector(int SpriteNum, int scale) {
		USER u = pUser[SpriteNum];

		u.xchange = mulscale(u.xchange, scale, 16);
		u.ychange = mulscale(u.ychange, scale, 16);
		u.zchange = mulscale(u.zchange, scale, 16);
	}

	public static void WallBounce(int SpriteNum, short ang) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		int old_ang;
		int k, l;
		int dax, day;

		u.bounce++;

		// k = cos(ang) * sin(ang) * 2
		k = mulscale(sintable[NORM_ANGLE(ang + 512)], sintable[ang], 13);
		// l = cos(ang * 2)
		l = sintable[NORM_ANGLE((ang * 2) + 512)];

		dax = -u.xchange;
		day = -u.ychange;

		u.xchange = dmulscale(day, k, dax, l, 14);
		u.ychange = dmulscale(dax, k, -day, l, 14);

		old_ang = sp.ang;
		sp.ang = engine.getangle(u.xchange, u.ychange);

		// hack to prevent missile from sticking to a wall
		//
		if (old_ang == sp.ang) {
			u.xchange = -u.xchange;
			u.ychange = -u.ychange;
			sp.ang = engine.getangle(u.xchange, u.ychange);
		}
	}

	public static boolean SlopeBounce(int SpriteNum, LONGp hitwall) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		int k, l;
		int hiz, loz;
		int slope;
		int dax, day, daz;
		short hitsector;
		short daang;

		hitsector = NORM_SECTOR(u.ret);

		engine.getzsofslope(hitsector, sp.x, sp.y, zofslope);
		hiz = zofslope[CEIL];
		loz = zofslope[FLOOR];

		// detect the ceiling and the hitwall
		if (sp.z < DIV2(hiz + loz)) {
			if (!TEST(sector[hitsector].ceilingstat, CEILING_STAT_SLOPE))
				slope = 0;
			else
				slope = sector[hitsector].ceilingheinum;
		} else {
			if (!TEST(sector[hitsector].floorstat, FLOOR_STAT_SLOPE))
				slope = 0;
			else
				slope = sector[hitsector].floorheinum;
		}

		if (slope == 0)
			return (false);

		// if greater than a 45 degree angle
		if (klabs(slope) > 4096)
			hitwall.value = 1;
		else
			hitwall.value = 0;

		// get angle of the first wall of the sector
		k = sector[hitsector].wallptr;
		l = wall[k].point2;
		daang = engine.getangle(wall[l].x - wall[k].x, wall[l].y - wall[k].y);

		// k is now the slope of the ceiling or floor

		// normal vector of the slope
		dax = mulscale(slope, sintable[(daang) & 2047], 14);
		day = mulscale(slope, sintable[(daang + 1536) & 2047], 14);
		daz = 4096; // 4096 = 45 degrees

		// reflection code
		k = ((u.xchange * dax) + (u.ychange * day)) + mulscale(u.zchange, daz, 4);
		l = (dax * dax) + (day * day) + (daz * daz);

		// make sure divscale doesn't overflow
		if ((klabs(k) >> 14) < l) {
			k = divscale(k, l, 17);
			u.xchange -= mulscale(dax, k, 16);
			u.ychange -= mulscale(day, k, 16);
			u.zchange -= mulscale(daz, k, 12);

			sp.ang = engine.getangle(u.xchange, u.ychange);
		}

		return (true);
	}

	public static boolean DoGrenade(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		short i;

		if (TEST(u.Flags, SPR_UNDERWATER)) {
			ScaleSpriteVector(Weapon, 50000);

			u.Counter += 20;
			u.zchange += u.Counter;
		} else {
			u.Counter += 20;
			u.zchange += u.Counter;
		}

		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);

		if (TEST(u.Flags, SPR_UNDERWATER) && (RANDOM_P2(1024 << 4) >> 4) < 256)
			SpawnBubble(Weapon);

        if (u.ret != 0) {
			switch (DTEST(u.ret, HIT_MASK)) {
			case HIT_PLAX_WALL:
				KillSprite(Weapon);
				return (true);
			case HIT_SPRITE: {
				short wall_ang;
				short hitsprite = -2;
				SPRITE hsp;

				PlaySound(DIGI_40MMBNCE, sp, v3df_dontpan);

				hitsprite = NORM_SPRITE(u.ret);
				hsp = sprite[hitsprite];

				// special case so grenade can ring gong
				if (hsp.lotag == TAG_SPRITE_HIT_MATCH) {
					if (TEST(SP_TAG8(hsp), BIT(3)))
						DoMatchEverything(null, hsp.hitag, -1);
				}

				if (TEST(hsp.cstat, CSTAT_SPRITE_WALL)) {
					wall_ang = NORM_ANGLE(hsp.ang);
					WallBounce(Weapon, wall_ang);
					ScaleSpriteVector(Weapon, 32000);
				} else {
					if (u.Counter2 == 1) // It's a phosphorus grenade!
					{
						for (i = 0; i < 5; i++) {
							sp.ang = NORM_ANGLE(RANDOM_RANGE(2048));
							InitPhosphorus(Weapon);
						}
					}
					SpawnGrenadeExp(Weapon);
					KillSprite((short) Weapon);
					return (true);
				}

				break;
			}

			case HIT_WALL: {
				short hitwall, nw, wall_ang;
				WALL wph;

				hitwall = NORM_WALL(u.ret);
				wph = wall[hitwall];

				if (wph.lotag == TAG_WALL_BREAK) {
					HitBreakWall(hitwall, sp.x, sp.y, sp.z, sp.ang, u.ID);
					u.ret = 0;
					break;
				}

				PlaySound(DIGI_40MMBNCE, sp, v3df_dontpan);

				nw = wall[hitwall].point2;
				wall_ang = NORM_ANGLE(engine.getangle(wall[nw].x - wph.x, wall[nw].y - wph.y) + 512);

				// sp.ang = NORM_ANGLE(sp.ang + 1);
				WallBounce(Weapon, wall_ang);
				ScaleSpriteVector(Weapon, 22000);

				break;
			}

			case HIT_SECTOR: {
				boolean hitwall;
				if (SlopeBounce(Weapon, tmp_ptr[0])) {
					hitwall = tmp_ptr[0].value != 0;
					if (hitwall) {
						// hit a wall
						ScaleSpriteVector(Weapon, 22000); // 28000
						u.ret = 0;
						u.Counter = 0;
					} else {
						// hit a sector
						if (sp.z > DIV2(u.hiz + u.loz)) {
							// hit a floor
							if (!TEST(u.Flags, SPR_BOUNCE)) {
								u.Flags |= (SPR_BOUNCE);
								ScaleSpriteVector(Weapon, 40000); // 18000
								u.ret = 0;
								u.zchange /= 4;
								u.Counter = 0;
							} else {
								if (u.Counter2 == 1) // It's a phosphorus grenade!
								{
									for (i = 0; i < 5; i++) {
										sp.ang = NORM_ANGLE(RANDOM_RANGE(2048));
										InitPhosphorus(Weapon);
									}
								}
								SpawnGrenadeExp(Weapon);
								KillSprite((short) Weapon);
								return (true);
							}
						} else {
							// hit a ceiling
							ScaleSpriteVector(Weapon, 22000);
						}
					}
				} else {
					// hit floor
					if (sp.z > DIV2(u.hiz + u.loz)) {
						if (TEST(u.Flags, SPR_UNDERWATER))
							u.Flags |= (SPR_BOUNCE); // no bouncing underwater

						if (u.lo_sectp != -1 && SectUser[sp.sectnum] != null && SectUser[sp.sectnum].depth != 0)
							u.Flags |= (SPR_BOUNCE); // no bouncing on shallow water

						if (!TEST(u.Flags, SPR_BOUNCE)) {
							u.Flags |= (SPR_BOUNCE);
							u.ret = 0;
							u.Counter = 0;
							u.zchange = -u.zchange;
							ScaleSpriteVector(Weapon, 40000); // 18000
							u.zchange /= 4;
							PlaySound(DIGI_40MMBNCE, sp, v3df_dontpan);
						} else {
							if (u.Counter2 == 1) // It's a phosphorus grenade!
							{
								for (i = 0; i < 5; i++) {
									sp.ang = NORM_ANGLE(RANDOM_RANGE(2048));
									InitPhosphorus(Weapon);
								}
							}
							// WeaponMoveHit(Weapon);
							SpawnGrenadeExp(Weapon);
							KillSprite((short) Weapon);
							return (true);
						}
					} else
					// hit something above
					{
						u.zchange = -u.zchange;
						ScaleSpriteVector(Weapon, 22000);
						PlaySound(DIGI_40MMBNCE, sp, v3df_dontpan);
					}
				}
				break;
			}
			}
		}

		if (u.bounce > 10) {
			SpawnGrenadeExp(Weapon);
			KillSprite(Weapon);
			return (true);
		}

		// if you haven't bounced or your going slow do some puffs
		if (!TEST(u.Flags, SPR_BOUNCE | SPR_UNDERWATER)) {
			SPRITE np;
			USER nu;
			short newsp = (short) SpawnSprite(STAT_MISSILE, PUFF, s_Puff[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang, 100);
			if(newsp == -1)
				return false;

			np = sprite[newsp];
			nu = pUser[newsp];

			SetOwner(Weapon, newsp);
			np.shade = -40;
			np.xrepeat = 40;
			np.yrepeat = 40;
			nu.ox = u.ox;
			nu.oy = u.oy;
			nu.oz = u.oz;
			np.cstat |= (CSTAT_SPRITE_YCENTER);
			np.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

			nu.xchange = u.xchange;
			nu.ychange = u.ychange;
			nu.zchange = u.zchange;

			ScaleSpriteVector(newsp, 22000);

			if (TEST(u.Flags, SPR_UNDERWATER))
				nu.Flags |= (SPR_UNDERWATER);
		}

		return (false);
	}

	public static boolean DoVulcanBoulder(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		u.Counter += 40;
		u.zchange += u.Counter;

		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		int vel = engine.ksqrt(SQ(u.xchange) + SQ(u.ychange));

		if (vel < 30) {
			SpawnLittleExp(Weapon);
			KillSprite(Weapon);
			return (true);
		}

		if (u.ret != 0) {
			switch (DTEST(u.ret, HIT_MASK)) {
			case HIT_PLAX_WALL:
				KillSprite(Weapon);
				return (true);
			case HIT_SPRITE: {
				short wall_ang;
				short hitsprite = -2;
				SPRITE hsp;

//	                PlaySound(DIGI_DHCLUNK, sp, v3df_dontpan);

				hitsprite = NORM_SPRITE(u.ret);
				hsp = sprite[hitsprite];

				if (TEST(hsp.cstat, CSTAT_SPRITE_WALL)) {
					wall_ang = NORM_ANGLE(hsp.ang);
					WallBounce(Weapon, wall_ang);
					ScaleSpriteVector(Weapon, 40000);
				} else {
					// hit an actor
					SpawnLittleExp(Weapon);
					KillSprite((short) Weapon);
					return (true);
				}

				break;
			}

			case HIT_WALL: {
				short hitwall, nw, wall_ang;
				WALL wph;

				hitwall = NORM_WALL(u.ret);
				wph = wall[hitwall];

				if (wph.lotag == TAG_WALL_BREAK) {
					HitBreakWall(hitwall, sp.x, sp.y, sp.z, sp.ang, u.ID);
					u.ret = 0;
					break;
				}

				nw = wall[hitwall].point2;
				wall_ang = NORM_ANGLE(engine.getangle(wall[nw].x - wph.x, wall[nw].y - wph.y) + 512);

				WallBounce(Weapon, wall_ang);
				ScaleSpriteVector(Weapon, 40000);
				break;
			}

			case HIT_SECTOR: {
				boolean hitwall;

				if (SlopeBounce(Weapon, tmp_ptr[0])) {
					hitwall = tmp_ptr[0].value != 0;
					if (hitwall) {
						// hit a sloped sector - treated as a wall because of large slope
						ScaleSpriteVector(Weapon, 30000);
						u.ret = 0;
						u.Counter = 0;
					} else {
						// hit a sloped sector
						if (sp.z > DIV2(u.hiz + u.loz)) {
							// hit a floor
							u.xchange = mulscale(u.xchange, 30000, 16);
							u.ychange = mulscale(u.ychange, 30000, 16);
							u.zchange = mulscale(u.zchange, 12000, 16);
							u.ret = 0;
							u.Counter = 0;

							// limit to a reasonable bounce value
							if (u.zchange > Z(32))
								u.zchange = Z(32);
						} else {
							// hit a sloped ceiling
							u.ret = 0;
							u.Counter = 0;
							ScaleSpriteVector(Weapon, 30000);
						}
					}
				} else {
					// hit unsloped floor
					if (sp.z > DIV2(u.hiz + u.loz)) {
						u.ret = 0;
						u.Counter = 0;

						u.xchange = mulscale(u.xchange, 20000, 16);
						u.ychange = mulscale(u.ychange, 20000, 16);
						u.zchange = mulscale(u.zchange, 32000, 16);

						// limit to a reasonable bounce value
						if (u.zchange > Z(24))
							u.zchange = Z(24);

						u.zchange = -u.zchange;

					} else
					// hit unsloped ceiling
					{
						u.zchange = -u.zchange;
						ScaleSpriteVector(Weapon, 30000);
					}
				}

				break;
			}
			}
		}

		return (false);
	}

	public static boolean OwnerIsPlayer(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon], uo;

		if (u == null || sp.owner == -1)
			return (false);
		uo = pUser[sp.owner];
		if (uo != null && uo.PlayerP != -1)
			return (true);

		return (false);
	}

	public static boolean DoMineRangeTest(int Weapon, int range) {
		SPRITE wp = sprite[Weapon];

		USER u;
		SPRITE sp;
		short i, nexti, stat;
		int dist;
		boolean ownerisplayer = false;

		ownerisplayer = OwnerIsPlayer(Weapon);

		for (stat = 0; stat < StatDamageList.length; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				sp = sprite[i];
				u = pUser[i];

				dist = DISTANCE(sp.x, sp.y, wp.x, wp.y);
				if (dist > range)
					continue;

				if (sp == wp)
					continue;

				if (!TEST(sp.cstat, CSTAT_SPRITE_BLOCK))
					continue;

				if (!TEST(sp.extra, SPRX_PLAYER_OR_ENEMY))
					continue;

				if (u.ID == GIRLNINJA_RUN_R0 && !ownerisplayer)
					continue;

				dist = FindDistance3D(wp.x - sp.x, wp.y - sp.y, (wp.z - sp.z) >> 4);
				if (dist > range)
					continue;

				if (!FAFcansee(sp.x, sp.y, SPRITEp_UPPER(sp), sp.sectnum, wp.x, wp.y, wp.z, wp.sectnum))
					continue;

				return (true);
			}
		}

		return (false);
	}

	public static final int MINE_DETONATE_STATE = 99;

	public static boolean DoMineStuck(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		// if no owner then die
		if (u.Attach >= 0) {
			SPRITE ap = sprite[u.Attach];
			USER au = pUser[u.Attach];

			// Is it attached to a dead actor? Blow it up if so.
			if (TEST(au.Flags, SPR_DEAD) && u.Counter2 < MINE_DETONATE_STATE) {
				u.Counter2 = MINE_DETONATE_STATE;
				u.WaitTics = (short) (SEC(1) / 2);
			}

			engine.setspritez(Weapon, ap.x, ap.y, ap.z - u.sz);
			sp.z = ap.z - DIV2(SPRITEp_SIZE_Z(ap));
		}

		// not activated yet
		if (!TEST(u.Flags, SPR_ACTIVE)) {
			if ((u.WaitTics -= (MISSILEMOVETICS * 2)) > 0)
				return (false);

			// activate it
			// u.WaitTics = 65536;
			u.WaitTics = 32767;
			u.Counter2 = 0;
			u.Flags |= (SPR_ACTIVE);
		}

		// limit the number of times DoMineRangeTest is called
		u.Counter++;
		if (u.Counter > 1)
			u.Counter = 0;

		if (u.Counter2 != MINE_DETONATE_STATE) {
			if ((u.Counter2++) > 30) {
				PlaySound(DIGI_MINEBEEP, sp, v3df_dontpan);
				u.WaitTics = 32767; // Keep reseting tics to make it stay forever
				u.Counter2 = 0;
			}
		}

		if (u.Counter == 0) {
			// not already in detonate state
			if (u.Counter2 < MINE_DETONATE_STATE) {
				// if something came into range - detonate
				if (DoMineRangeTest(Weapon, 3000)) {
					// move directly to detonate state
					u.Counter2 = MINE_DETONATE_STATE;
					u.WaitTics = (short) (SEC(1) / 2);
				}
			}
		}

		u.WaitTics -= (MISSILEMOVETICS * 2);

		// start beeping with pauses
		// quick and dirty beep countdown code
		switch (u.Counter2) {
		case 30:
			if (u.WaitTics < SEC(6)) {
				PlaySound(DIGI_MINEBEEP, sp, v3df_dontpan);
				u.Counter2 = MINE_DETONATE_STATE;
			}
			break;
		case MINE_DETONATE_STATE:
			if (u.WaitTics < 0) {
				PlaySound(DIGI_MINEBEEP, sp, v3df_dontpan);
				SpawnMineExp(Weapon);
				KillSprite(Weapon);
				return (false);
			}
			break;
		}

		return (false);
	}

	public static int SetMineStuck(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		// stuck
		u.Flags |= (SPR_BOUNCE);
		// not yet active for 1 sec
		u.Flags &= ~(SPR_ACTIVE);
		u.WaitTics = (short) SEC(3);
		sp.cstat |= (CSTAT_SPRITE_BLOCK_HITSCAN);
		u.Counter = 0;
		change_sprite_stat(Weapon, STAT_MINE_STUCK);
		ChangeState(Weapon, s_MineStuck[0]);
		return (0);
	}

	public static boolean DoMine(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		if (TEST(u.Flags, SPR_UNDERWATER)) {
			// decrease velocity
			ScaleSpriteVector(Weapon, 50000);

			u.Counter += 20;
			u.zchange += u.Counter;
		} else {
			// u.Counter += 75;
			u.Counter += 40;
			u.zchange += u.Counter;
		}

		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);

		if (TEST(u.Flags, SPR_UNDERWATER) && (RANDOM_P2(1024 << 4) >> 4) < 256)
			SpawnBubble(Weapon);

		if (u.ret != 0) {
			// check to see if you hit a sprite
			switch (DTEST(u.ret, HIT_MASK)) {
			case HIT_PLAX_WALL:
				KillSprite(Weapon);
				return false;
			case HIT_SPRITE: {
				short hitsprite = NORM_SPRITE(u.ret);
				SPRITE hsp = sprite[hitsprite];
				USER hu = pUser[hitsprite];

				SetMineStuck(Weapon);
				// Set the Z position
				sp.z = hsp.z - DIV2(SPRITEp_SIZE_Z(hsp));

				// If it's not alive, don't stick it
				if (hu != null && hu.Health <= 0)
					return (false);

				// check to see if sprite is player or enemy
				if (TEST(hsp.extra, SPRX_PLAYER_OR_ENEMY)) {
					USER uo;
					PlayerStr pp;

					// attach weapon to sprite
					SetAttach(hitsprite, Weapon);
					u.sz = sprite[hitsprite].z - sp.z;

					if (sp.owner >= 0) {
						uo = pUser[sp.owner];

						if (uo != null && uo.PlayerP != -1) {
							pp = Player[uo.PlayerP];

							if (RANDOM_RANGE(1000) > 800)
								PlayerSound(DIGI_STICKYGOTU1, v3df_follow | v3df_dontpan,
										pp);
							else if (RANDOM_RANGE(1000) > 800)
								PlayerSound(DIGI_STICKYGOTU2, v3df_follow | v3df_dontpan,
										pp);
							else if (RANDOM_RANGE(1000) > 800)
								PlayerSound(DIGI_STICKYGOTU3, v3df_follow | v3df_dontpan,
										pp);
							else if (RANDOM_RANGE(1000) > 800)
								PlayerSound(DIGI_STICKYGOTU4, v3df_follow | v3df_dontpan,
										pp);
						}
					}
				} else {
					if (TEST(hsp.cstat, CSTAT_SPRITE_WALL)) {
						u.Flags2 |= (SPR2_ATTACH_WALL);
					} else if (TEST(hsp.cstat, CSTAT_SPRITE_FLOOR)) {
						// hit floor
						if (sp.z > DIV2(u.hiz + u.loz))
							u.Flags2 |= (SPR2_ATTACH_FLOOR);
						else
							u.Flags2 |= (SPR2_ATTACH_CEILING);
					} else {
						SpawnMineExp(Weapon);
						KillSprite(Weapon);
						return (false);
					}
				}

				break;
			}

			case HIT_WALL: {
				short hitwall = NORM_WALL(u.ret);

				if (wall[hitwall].lotag == TAG_WALL_BREAK) {
					HitBreakWall(hitwall, sp.x, sp.y, sp.z, sp.ang, u.ID);
					u.ret = 0;
					break;
				}

				SetMineStuck(Weapon);

				u.Flags2 |= (SPR2_ATTACH_WALL);

				if (TEST(wall[hitwall].extra, WALLFX_SECTOR_OBJECT)) {
				}

				if (TEST(wall[hitwall].extra, WALLFX_DONT_STICK)) {
					SpawnMineExp(Weapon);
					KillSprite(Weapon);
					return (false);
				}

				break;
			}

			case HIT_SECTOR: {
				short hitsect = NORM_SECTOR(u.ret);

				SetMineStuck(Weapon);

				// hit floor
				if (sp.z > DIV2(u.hiz + u.loz))
					u.Flags2 |= (SPR2_ATTACH_FLOOR);
				else
					u.Flags2 |= (SPR2_ATTACH_CEILING);

				if (TEST(sector[hitsect].extra, SECTFX_SECTOR_OBJECT)) {
					SpawnMineExp(Weapon);
					KillSprite(Weapon);
					return (false);
				}

				break;
			}
			}

			u.ret = 0;
		}

		return (false);
	}

	public static int DoPuff(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		sp.x += u.xchange;
		sp.y += u.ychange;
		sp.z += u.zchange;

		return (0);
	}

	public static int DoRailPuff(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];

		sp.xrepeat += 4;
		sp.yrepeat += 4;

		return (0);
	}

	public static boolean DoBoltThinMan(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		DoBlurExtend(Weapon, 0, 4);

		int dax = MOVEx(sp.xvel, sp.ang);
		int day = MOVEy(sp.xvel, sp.ang);
		int daz = sp.zvel;

		u.ret = move_missile(Weapon, dax, day, daz, CEILING_DIST, FLOOR_DIST, CLIPMASK_MISSILE, MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);

		if (TEST(u.Flags, SPR_UNDERWATER) && (RANDOM_P2(1024 << 4) >> 4) < 256)
			SpawnBubble(Weapon);

		// DoDamageTest(Weapon);

		if (TEST(u.Flags, SPR_SUICIDE))
			return (true);

		if (u.ret != 0) {
			if (WeaponMoveHit(Weapon)) {
				SpawnBoltExp(Weapon);
				KillSprite(Weapon);
				return (true);
			}
		}

		return (false);
	}

	public static boolean DoTracer(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		for (int i = 0; i < 4; i++) {
			u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist,
					CLIPMASK_MISSILE, MISSILEMOVETICS);

			MissileHitDiveArea(Weapon);

			if (u.ret != 0) {
				if (WeaponMoveHit(Weapon)) {
					KillSprite(Weapon);
					return (true);
				}
			}
		}

		sp.cstat &= ~(CSTAT_SPRITE_INVISIBLE);

		return (false);
	}

	public static boolean DoEMP(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		for (int i = 0; i < 4; i++) {
			u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist,
					CLIPMASK_MISSILE, MISSILEMOVETICS);

			MissileHitDiveArea(Weapon);

			if (RANDOM_RANGE(1000) > 500) {
				sp.xrepeat = 52;
				sp.yrepeat = 10;
			} else {
				sp.xrepeat = 8;
				sp.yrepeat = 38;
			}

			if (u.ret != 0) {
				if (WeaponMoveHit(Weapon)) {
					KillSprite(Weapon);
					return (true);
				}
			}
		}

		sp.cstat &= ~(CSTAT_SPRITE_INVISIBLE);

		return (false);
	}

	public static boolean DoEMPBurst(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		if (u.Attach >= 0) {
			SPRITE ap = sprite[u.Attach];
			engine.setspritez(Weapon, ap.x, ap.y, ap.z - u.sz);
			sp.ang = NORM_ANGLE(ap.ang + 1024);
		}

		// not activated yet
		if (!TEST(u.Flags, SPR_ACTIVE)) {
			// activate it
			u.WaitTics = (short) SEC(7);
			u.Flags |= (SPR_ACTIVE);
		}

		if (RANDOM_RANGE(1000) > 500) {
			sp.xrepeat = 52;
			sp.yrepeat = 10;
		} else {
			sp.xrepeat = 8;
			sp.yrepeat = 38;
		}

		if ((RANDOM_P2(1024 << 6) >> 6) < 700) {
			SpawnShrapX(Weapon);
		}

		u.WaitTics -= (MISSILEMOVETICS * 2);

		if (u.WaitTics < 0) {
			KillSprite(Weapon);
			return (false);
		}

		return (false);
	}

	public static boolean DoTankShell(int Weapon) {
		USER u = pUser[Weapon];
		short i;

		for (i = 0; i < 4; i++) {
			u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist,
					CLIPMASK_MISSILE, MISSILEMOVETICS);

			MissileHitDiveArea(Weapon);

			if (u.ret != 0) {
				if (WeaponMoveHit(Weapon)) {
					SpawnTankShellExp(Weapon);
					KillSprite(Weapon);
					return (true);
				}
			}
		}

		return (false);
	}

	public static final Animator DoTracerStart = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoTracerStart(spr);
		}
	};

	public static boolean DoTracerStart(int Weapon) {
		USER u = pUser[Weapon];
		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);

		if (u.ret != 0) {
			if (WeaponMoveHit(Weapon)) {
				KillSprite(Weapon);
				return (true);
			}
		}

		return (false);
	}

	public static boolean DoLaser(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		SPRITE np;
		USER nu;
		short newsp;
		short spawn_count = 0;

		while (true) {
			u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist,
					CLIPMASK_MISSILE, MISSILEMOVETICS);

			MissileHitDiveArea(Weapon);

			if (u.ret != 0) {
				if (WeaponMoveHit(Weapon)) {
					SpawnBoltExp(Weapon);
					KillSprite((short) Weapon);
					return (true);
				}
			}

			spawn_count++;
			if (spawn_count < 256) {
				newsp = (short) SpawnSprite(STAT_MISSILE, PUFF, s_LaserPuff[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang,
						0);
				if(newsp == -1)
					return false;

				np = sprite[newsp];
				nu = pUser[newsp];

				np.shade = -40;
				np.xrepeat = 16;
				np.yrepeat = 16;
				np.pal = nu.spal = PALETTE_RED_LIGHTING;

				np.cstat |= (CSTAT_SPRITE_YCENTER);
				np.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

				nu.xchange = nu.ychange = nu.zchange = 0;
			}
		}
	}

	public static final Animator DoLaserStart = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoLaserStart(spr);
		}
	};

	public static boolean DoLaserStart(int Weapon) {
		USER u = pUser[Weapon];
		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);

		if (u.ret != 0) {
			if (WeaponMoveHit(Weapon)) {
				SpawnBoltExp(Weapon);
				KillSprite((short) Weapon);
				return (true);
			}
		}

		return false;
	}

	public static boolean DoRail(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		SPRITE np;
		USER nu;
		short newsp;
		short spawn_count = 0;

		while (true) {
			u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist,
					CLIPMASK_MISSILE, MISSILEMOVETICS);

			MissileHitDiveArea(Weapon);

			if (u.ret != 0) {
				if (WeaponMoveHit(Weapon) && u.ret != 0) {
					if (DTEST(u.ret, HIT_MASK) == HIT_SPRITE) {
						short hitsprite;
						hitsprite = NORM_SPRITE(u.ret);

						if (TEST(sprite[hitsprite].extra, SPRX_PLAYER_OR_ENEMY)) {
							short cstat_save = sprite[hitsprite].cstat;

							sprite[hitsprite].cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN
									| CSTAT_SPRITE_BLOCK_MISSILE);
							DoRail(Weapon);
							sprite[hitsprite].cstat = cstat_save;
							return (true);
						} else {
							SpawnTracerExp(Weapon);
							SpawnShrapX(Weapon);
							KillSprite((short) Weapon);
							return (true);
						}
					} else {
						SpawnTracerExp(Weapon);
						SpawnShrapX(Weapon);
						KillSprite((short) Weapon);
						return (true);
					}
				}
			}

			spawn_count++;
			if (spawn_count < 128) {
				newsp = (short) SpawnSprite(STAT_MISSILE, PUFF, s_RailPuff[0][0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang,
						20);
				if(newsp == -1)
					return false;

				np = sprite[newsp];
				nu = pUser[newsp];

				np.xvel += (RANDOM_RANGE(140) - RANDOM_RANGE(140));
				np.yvel += (RANDOM_RANGE(140) - RANDOM_RANGE(140));
				np.zvel += (RANDOM_RANGE(140) - RANDOM_RANGE(140));

				nu.RotNum = 5;
				NewStateGroup(newsp, WeaponStateGroup.sg_RailPuff);

				np.shade = -40;
				np.xrepeat = 10;
				np.yrepeat = 10;
				nu.ox = u.ox;
				nu.oy = u.oy;
				nu.oz = u.oz;
				np.cstat |= (CSTAT_SPRITE_YCENTER);
				np.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

				np.hitag |= LUMINOUS; // Always full brightness

				nu.xchange = u.xchange;
				nu.ychange = u.ychange;
				nu.zchange = u.zchange;

				ScaleSpriteVector(newsp, 1500);

				if (TEST(u.Flags, SPR_UNDERWATER))
					nu.Flags |= (SPR_UNDERWATER);
			}
		}
	}

	public static final Animator DoRailStart = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoRailStart(spr);
		}
	};

	public static boolean DoRailStart(int Weapon) {
		USER u = pUser[Weapon];

		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);

		if (u.ret != 0) {
			if (WeaponMoveHit(Weapon)) {
				SpawnTracerExp(Weapon);
				SpawnShrapX(Weapon);
				KillSprite((short) Weapon);
				return (true);
			}
		}
		return false;
	}

	public static boolean DoRocket(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		if ((u.FlagOwner -= ACTORMOVETICS) <= 0 && u.spal == 20) {

			u.FlagOwner = (short) (DISTANCE(sp.x, sp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y) >> 6);
			// Special warn sound attached to each seeker spawned
			PlaySound(DIGI_MINEBEEP, sp, v3df_follow);
		}

		if (TEST(u.Flags, SPR_FIND_PLAYER)) {
			// MissileSeek(Weapon, 10, 768, 3, 48, 6);
			VectorMissileSeek(Weapon, 30, 16, 128, 768);
		}

		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);

		if (TEST(u.Flags, SPR_UNDERWATER) && (RANDOM_P2(1024 << 4) >> 4) < 256)
			SpawnBubble(Weapon);

		// DoDamageTest(Weapon);

		if (TEST(u.Flags, SPR_SUICIDE))
			return (true);

		if (u.ret != 0) {
			if (WeaponMoveHit(Weapon) && u.ret != 0) {
				if (u.ID == BOLT_THINMAN_R4) {
					SpawnBunnyExp(Weapon);
				} else if (u.Radius == NUKE_RADIUS)
					SpawnNuclearExp(Weapon);
				else
					SpawnBoltExp(Weapon);

				KillSprite((short) Weapon);
				return (true);
			}
		}

		if (u.Counter == 0) {
			SPRITE np;
			USER nu;
			short newsp;

			newsp = (short) SpawnSprite(STAT_MISSILE, PUFF, s_Puff[0], sp.sectnum, u.ox, u.oy, u.oz, sp.ang, 100);
			if(newsp == -1)
				return false;

			np = sprite[newsp];
			nu = pUser[newsp];

			SetOwner(Weapon, newsp);
			np.shade = -40;
			np.xrepeat = 40;
			np.yrepeat = 40;
			nu.ox = u.ox;
			nu.oy = u.oy;
			nu.oz = u.oz;
			np.cstat |= (CSTAT_SPRITE_YCENTER);
			np.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

			nu.xchange = u.xchange;
			nu.ychange = u.ychange;
			nu.zchange = u.zchange;

			ScaleSpriteVector(newsp, 20000);

			if (TEST(u.Flags, SPR_UNDERWATER))
				nu.Flags |= (SPR_UNDERWATER);
		}

		return (false);
	}

	public static boolean DoMicroMini(int Weapon) {
		USER u = pUser[Weapon];
		short i;

		for (i = 0; i < 3; i++) {
			u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist,
					CLIPMASK_MISSILE, MISSILEMOVETICS);

			MissileHitDiveArea(Weapon);

			if (u.ret != 0) {
				if (WeaponMoveHit(Weapon)) {
					SpawnMicroExp(Weapon);
					KillSprite((short) Weapon);
					return (true);
				}
			}
		}

		return (false);
	}

	public static int SpawnExtraMicroMini(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		SPRITE wp;
		USER wu;
		short w;

		w = (short) SpawnSprite(STAT_MISSILE, BOLT_THINMAN_R0, s_Micro[0][0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang,
				sp.xvel);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		SetOwner(sp.owner, w);
		wp.yrepeat = wp.xrepeat = sp.xrepeat;
		wp.shade = sp.shade;
		wp.clipdist = sp.clipdist;

		wu.RotNum = 5;
		NewStateGroup(w, WeaponStateGroup.sg_MicroMini);
		wu.WeaponNum = u.WeaponNum;
		wu.Radius = u.Radius;
		wu.ceiling_dist = u.ceiling_dist;
		wu.floor_dist = u.floor_dist;
		wp.cstat = sp.cstat;

		wp.ang = NORM_ANGLE(wp.ang + RANDOM_RANGE(64) - 32);
		wp.zvel = sp.zvel;
		wp.zvel += RANDOM_RANGE(Z(16)) - Z(8);

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;
		return (0);
	}

	public static boolean DoMicro(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		short newsp;

		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);

		if (TEST(u.Flags, SPR_UNDERWATER) && (RANDOM_P2(1024 << 4) >> 4) < 256)
			SpawnBubble(Weapon);

		if (u.Counter == 0) {
			SPRITE np;
			USER nu;

			newsp = (short) SpawnSprite(STAT_MISSILE, PUFF, s_Puff[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang, 100);
            if(newsp == -1)
                return false;

			np = sprite[newsp];
			nu = pUser[newsp];

			SetOwner(sp.owner, newsp);
			np.shade = -40;
			np.xrepeat = 20;
			np.yrepeat = 20;
			nu.ox = u.ox;
			nu.oy = u.oy;
			nu.oz = u.oz;
			np.zvel = sp.zvel;
			np.cstat |= (CSTAT_SPRITE_YCENTER);
			np.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

			nu.xchange = u.xchange;
			nu.ychange = u.ychange;
			nu.zchange = u.zchange;

			ScaleSpriteVector(newsp, 20000);

			if (TEST(u.Flags, SPR_UNDERWATER))
				nu.Flags |= (SPR_UNDERWATER);

			// last smoke
			if ((u.WaitTics -= MISSILEMOVETICS) <= 0) {
				engine.setspritez(newsp, np.x, np.y, np.z);
				NewStateGroup(Weapon, WeaponStateGroup.sg_MicroMini);
				sp.xrepeat = sp.yrepeat = 10;
				sp.cstat &= ~(CSTAT_SPRITE_INVISIBLE);
				SpawnExtraMicroMini(Weapon);
				return (true);
			}
		}

		// hit something
		if (u.ret != 0) {
			if (WeaponMoveHit(Weapon)) {
				SpawnMicroExp(Weapon);
				KillSprite(Weapon);
				return (true);
			}
		}

		return (false);
	}

	public static boolean DoUziBullet(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		int dax, day, daz;
		int sx, sy;
		short i;

		// call move_sprite twice for each movement
		// otherwize the moves are in too big an increment
		for (i = 0; i < 2; i++) {
			dax = MOVEx((sp.xvel >> 1), sp.ang);
			day = MOVEy((sp.xvel >> 1), sp.ang);
			daz = sp.zvel >> 1;

			sx = sp.x;
			sy = sp.y;
			u.ret = move_missile(Weapon, dax, day, daz, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
					MISSILEMOVETICS);
			u.Dist += Distance(sx, sy, sp.x, sp.y);

			MissileHitDiveArea(Weapon);

			if (TEST(u.Flags, SPR_UNDERWATER) && (RANDOM_P2(1024 << 4) >> 4) < 128)
				SpawnBubble(Weapon);

			if (u.ret != 0) {
				SPRITE wp;
				short j;

				WeaponMoveHit(Weapon);

				j = (short) SpawnSprite(STAT_MISSILE, UZI_SMOKE, s_UziSmoke[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang,
						0);
				if(j == -1)
					return false;

				wp = sprite[j];
				wp.shade = -40;
				wp.xrepeat = UZI_SMOKE_REPEAT;
				wp.yrepeat = UZI_SMOKE_REPEAT;
				SetOwner(sp.owner, j);
				wp.ang = sp.ang;
				wp.clipdist = 128 >> 2;
				wp.cstat |= (CSTAT_SPRITE_TRANSLUCENT | CSTAT_SPRITE_YCENTER);

				if (!TEST(u.Flags, SPR_UNDERWATER)) {
					j = (short) SpawnSprite(STAT_MISSILE, UZI_SPARK, s_UziSpark[0], wp.sectnum, wp.x, wp.y, wp.z, 0, 0);
					if(j == -1)
						return false;

					wp = sprite[j];
					wp.shade = -40;
					wp.xrepeat = UZI_SPARK_REPEAT;
					wp.yrepeat = UZI_SPARK_REPEAT;
					// wp.owner = sp.owner;
					SetOwner(sp.owner, j);
					wp.ang = sp.ang;
					wp.cstat |= (CSTAT_SPRITE_YCENTER);
				}

				KillSprite(Weapon);

				return (true);
			} else if (u.Dist > 8000) {
				KillSprite((short) Weapon);
				return false;
			}
		}

		return (false);
	}

	public static boolean DoBoltSeeker(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		MissileSeek(Weapon, 30, 768, 4, 48, 6);
		DoBlurExtend(Weapon, 0, 4);

		int dax = MOVEx(sp.xvel, sp.ang);
		int day = MOVEy(sp.xvel, sp.ang);
		int daz = sp.zvel;

		u.ret = move_missile(Weapon, dax, day, daz, CEILING_DIST, FLOOR_DIST, CLIPMASK_MISSILE, MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);
		if (TEST(u.Flags, SPR_UNDERWATER) && (RANDOM_P2(1024 << 4) >> 4) < 256)
			SpawnBubble(Weapon);

		if (u.ret != 0) {
			if (WeaponMoveHit(Weapon)) {
				SpawnBoltExp(Weapon);
				KillSprite((short) Weapon);
				return (true);
			}
		}

		return (false);
	}

	public static int DoBoltShrapnel(int Weapon) {
		return (0);
	}

	public static int DoBoltFatMan(int Weapon) {
		return (0);
	}

	public static boolean DoElectro(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		int dax, day, daz;

		DoBlurExtend(Weapon, 0, 4);

		// only seek on Electro's after a hit on an actor
		if (u.Counter > 0)
			MissileSeek(Weapon, 30, 512, 3, 52, 2);

		dax = MOVEx(sp.xvel, sp.ang);
		day = MOVEy(sp.xvel, sp.ang);
		daz = sp.zvel;

		u.ret = move_missile(Weapon, dax, day, daz, CEILING_DIST, FLOOR_DIST, CLIPMASK_MISSILE, MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);
		if (TEST(u.Flags, SPR_UNDERWATER) && (RANDOM_P2(1024 << 4) >> 4) < 256)
			SpawnBubble(Weapon);
		// DoDamageTest(Weapon);

		if (TEST(u.Flags, SPR_SUICIDE))
			return (true);

		if (u.ret != 0) {
			if (WeaponMoveHit(Weapon)) {
				switch (DTEST(u.ret, HIT_MASK)) {
				case HIT_SPRITE: {
					SPRITE hsp = sprite[NORM_SPRITE(u.ret)];
					USER hu = pUser[NORM_SPRITE(u.ret)];

					if (!TEST(hsp.extra, SPRX_PLAYER_OR_ENEMY) || hu.ID == SKULL_R0 || hu.ID == BETTY_R0)
						SpawnShrap(Weapon, -1);
					break;
				}

				default:
					SpawnShrap(Weapon, -1);
					break;
				}

				// SpawnShrap(Weapon, -1);
				KillSprite(Weapon);
				return (true);
			}
		}

		return (false);
	}

	public static boolean DoLavaBoulder(int Weapon) {
		USER u = pUser[Weapon];

		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);
		if (TEST(u.Flags, SPR_UNDERWATER) && (RANDOM_P2(1024 << 4) >> 4) < 256)
			SpawnBubble(Weapon);

		if (TEST(u.Flags, SPR_SUICIDE))
			return (true);

		if (u.ret != 0) {
			if (WeaponMoveHit(Weapon)) {
				SpawnShrap(Weapon, -1);
				KillSprite(Weapon);
				return (true);
			}
		}

		return (false);
	}

	public static int SpawnCoolieExp(int SpriteNum) {
		USER u = pUser[SpriteNum], eu;
		SPRITE sp = sprite[SpriteNum];

		short explosion;
		SPRITE exp;
		int zh, nx, ny;

		u.Counter = (short) RANDOM_RANGE(120); // This is the wait til birth time!

		zh = sp.z - SPRITEp_SIZE_Z(sp) + DIV4(SPRITEp_SIZE_Z(sp));
		nx = sp.x + MOVEx(64, sp.ang + 1024);
		ny = sp.y + MOVEy(64, sp.ang + 1024);

		PlaySound(DIGI_COOLIEEXPLODE, sp, v3df_none);

		explosion = (short) SpawnSprite(STAT_MISSILE, BOLT_EXP, s_BoltExp[0], sp.sectnum, nx, ny, zh, sp.ang, 0);
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.hitag = LUMINOUS; // Always full brightness
		SetOwner(SpriteNum, explosion);
		exp.shade = -40;
		exp.pal = eu.spal = u.spal;
		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		eu.Radius = DamageData[DMG_BOLT_EXP].radius;

		DoExpDamageTest(explosion);

		return (explosion);
	}

	public static int SpawnBasicExp(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		SPRITE exp;
		USER eu;
		short explosion;

		if (TEST(u.Flags, SPR_SUICIDE))
			return (-1);

		PlaySound(DIGI_MEDIUMEXP, sp, v3df_none);

		explosion = (short) SpawnSprite(STAT_MISSILE, BASIC_EXP, s_BasicExp[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang,
				0);
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.hitag = LUMINOUS; // Always full brightness
		exp.xrepeat = 24;
		exp.yrepeat = 24;
		SetOwner(sp.owner, explosion);
		exp.shade = -40;
		exp.pal = eu.spal = u.spal;
		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		eu.Radius = DamageData[DMG_BASIC_EXP].radius;

		//
		// All this stuff assures that explosions do not go into floors &
		// ceilings
		//

		SpawnExpZadjust(Weapon, explosion, Z(15), Z(15));
		DoExpDamageTest(explosion);
		SpawnVis(-1, exp.sectnum, exp.x, exp.y, exp.z, 16);

		return (explosion);
	}

	public static int SpawnBreakStaticFlames(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		SPRITE np;
		USER nu;
		short newsp;

		newsp = (short) SpawnSprite(STAT_STATIC_FIRE, FIREBALL_FLAMES, null, sp.sectnum, sp.x, sp.y, sp.z, sp.ang, 0);
		if(newsp == -1)
			return 0;

		np = sprite[newsp];
		nu = pUser[newsp];

		if (RANDOM_RANGE(1000) > 500)
			np.picnum = 3143;
		else
			np.picnum = 3157;

		np.hitag = LUMINOUS; // Always full brightness
		np.xrepeat = 32;
		np.yrepeat = 32;

		np.shade = -40;
		np.pal = nu.spal = u.spal;
		np.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		nu.Radius = 200;

		nu.floor_dist = nu.ceiling_dist = 0;

		np.z = engine.getflorzofslope(np.sectnum, np.x, np.y);

		Set3DSoundOwner(newsp, PlaySound(DIGI_FIRE1, np, v3df_dontpan | v3df_doppler));

		return (newsp);
	}

	public static int SpawnFireballExp(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		SPRITE exp;
		USER eu;
		short explosion;

		if (TEST(u.Flags, SPR_SUICIDE))
			return (-1);

		PlaySound(DIGI_SMALLEXP, sp, v3df_none);

		explosion = (short) SpawnSprite(STAT_MISSILE, FIREBALL_EXP, s_FireballExp[0], sp.sectnum, sp.x, sp.y, sp.z,
				sp.ang, 0);
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.hitag = LUMINOUS; // Always full brightness
		exp.xrepeat = 52;
		exp.yrepeat = 52;
		SetOwner(sp.owner, explosion);
		exp.shade = -40;
		exp.pal = eu.spal = u.spal;
		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		eu.Flags |= (DTEST(u.Flags, SPR_UNDERWATER));
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		//
		// All this stuff assures that explosions do not go into floors &
		// ceilings
		//

		SpawnExpZadjust(Weapon, explosion, Z(15), Z(15));

		if (RANDOM_P2(1024) < 150)
			SpawnFireballFlames(explosion, -1);
		return (explosion);
	}

	public static int SpawnGoroFireballExp(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		SPRITE exp;
		USER eu;
		short explosion;

		if (TEST(u.Flags, SPR_SUICIDE))
			return (-1);

		PlaySound(DIGI_MEDIUMEXP, sp, v3df_none);

		explosion = (short) SpawnSprite(STAT_MISSILE, 0, s_FireballExp[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang, 0);
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.hitag = LUMINOUS; // Always full brightness
		exp.xrepeat = 16;
		exp.yrepeat = 16;
		SetOwner(sp.owner, explosion);
		exp.shade = -40;
		exp.pal = eu.spal = u.spal;
		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		//
		// All this stuff assures that explosions do not go into floors &
		// ceilings
		//

		SpawnExpZadjust(Weapon, explosion, Z(15), Z(15));
		return (explosion);
	}

	public static int SpawnBoltExp(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		SPRITE exp;
		USER eu;
		short explosion;

		if (u != null && TEST(u.Flags, SPR_SUICIDE))
			return (-1);

		PlaySound(DIGI_BOLTEXPLODE, sp, v3df_none);

		explosion = (short) SpawnSprite(STAT_MISSILE, BOLT_EXP, s_BoltExp[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang, 0);
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.hitag = LUMINOUS; // Always full brightness
		SetOwner(sp.owner, explosion);
		exp.shade = -40;
		exp.xrepeat = 76;
		exp.yrepeat = 76;
		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		if (RANDOM_P2(1024) > 512)
			exp.cstat |= (CSTAT_SPRITE_XFLIP);
		eu.Radius = DamageData[DMG_BOLT_EXP].radius;

		SpawnExpZadjust(Weapon, explosion, Z(40), Z(40));

		DoExpDamageTest(explosion);

		SetExpQuake(explosion); // !JIM! made rocket launcher shake things
		SpawnVis(-1, exp.sectnum, exp.x, exp.y, exp.z, 16);

		return (explosion);
	}

	public static int SpawnBunnyExp(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		if (u != null && TEST(u.Flags, SPR_SUICIDE))
			return (-1);

		PlaySound(DIGI_BUNNYDIE3, sp, v3df_none);

		u.ID = BOLT_EXP; // Change id
		InitBloodSpray(Weapon, true, -1);
		InitBloodSpray(Weapon, true, -1);
		InitBloodSpray(Weapon, true, -1);
		DoExpDamageTest(Weapon);

		return (0);
	}

	public static int SpawnTankShellExp(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		SPRITE exp;
		USER eu;
		short explosion;

		if (u != null && TEST(u.Flags, SPR_SUICIDE))
			return (-1);

		PlaySound(DIGI_BOLTEXPLODE, sp, v3df_none);

		explosion = (short) SpawnSprite(STAT_MISSILE, TANK_SHELL_EXP, s_TankShellExp[0], sp.sectnum, sp.x, sp.y, sp.z,
				sp.ang, 0);
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.hitag = LUMINOUS; // Always full brightness
		SetOwner(sp.owner, explosion);
		exp.shade = -40;
		exp.xrepeat = 64 + 32;
		exp.yrepeat = 64 + 32;
		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		if (RANDOM_P2(1024) > 512)
			exp.cstat |= (CSTAT_SPRITE_XFLIP);
		eu.Radius = DamageData[DMG_TANK_SHELL_EXP].radius;

		SpawnExpZadjust(Weapon, explosion, Z(40), Z(40));
		DoExpDamageTest(explosion);
		SpawnVis(-1, exp.sectnum, exp.x, exp.y, exp.z, 16);

		return (explosion);
	}

	public static int SpawnNuclearSecondaryExp(int Weapon, short ang) {
		SPRITE sp = sprite[Weapon];
		SPRITE exp;
		USER eu;
		short explosion;

		explosion = (short) SpawnSprite(STAT_MISSILE, GRENADE_EXP, s_GrenadeExp[0], sp.sectnum, sp.x, sp.y, sp.z,
				sp.ang, 512);
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.hitag = LUMINOUS; // Always full brightness
		SetOwner(sp.owner, explosion);
		exp.shade = -128;
		exp.xrepeat = 218;
		exp.yrepeat = 152;
		exp.clipdist = sp.clipdist;
		eu.ceiling_dist = (short) Z(16);
		eu.floor_dist = (short) Z(16);
		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		int vel = (2048 + 128) + RANDOM_RANGE(2048);
		eu.xchange = MOVEx(vel, ang);
		eu.ychange = MOVEy(vel, ang);
		eu.Radius = 200; // was NUKE_RADIUS
		eu.ret = move_missile(explosion, eu.xchange, eu.ychange, 0, eu.ceiling_dist, eu.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		if (FindDistance3D(exp.x - sp.x, exp.y - sp.y, (exp.z - sp.z) >> 4) < 1024) {
			KillSprite(explosion);
			return (-1);
		}

		SpawnExpZadjust(Weapon, explosion, Z(50), Z(10));

		InitChemBomb(explosion);

		return (explosion);
	}

	public static int SpawnNuclearExp(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		SPRITE exp;
		USER eu;
		short explosion, ang = 0;
		PlayerStr pp = null;
		short rnd_rng;

		if (u != null && TEST(u.Flags, SPR_SUICIDE))
			return (-1);

		PlaySound(DIGI_NUCLEAREXP, sp, v3df_dontpan | v3df_doppler);

		if (sp.owner != 0 && pUser[sp.owner] != null) {
			pp = Player[pUser[sp.owner].PlayerP];
			rnd_rng = (short) RANDOM_RANGE(1000);

			if (rnd_rng > 990)
				PlayerSound(DIGI_LIKEHIROSHIMA, v3df_follow | v3df_dontpan, pp);
			else if (rnd_rng > 980)
				PlayerSound(DIGI_LIKENAGASAKI, v3df_follow | v3df_dontpan, pp);
			else if (rnd_rng > 970)
				PlayerSound(DIGI_LIKEPEARL, v3df_follow | v3df_dontpan, pp);
		}

		// Spawn big mushroom cloud
		explosion = (short) SpawnSprite(STAT_MISSILE, MUSHROOM_CLOUD, s_NukeMushroom[0], sp.sectnum, sp.x, sp.y, sp.z,
				sp.ang, 0);
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.hitag = LUMINOUS; // Always full brightness
		SetOwner(sp.owner, explosion);
		exp.shade = -128;
		exp.xrepeat = 255;
		exp.yrepeat = 255;
		exp.clipdist = sp.clipdist;
		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		eu.spal = (byte) (exp.pal = PALETTE_PLAYER1); // Set nuke puff to gray

		InitChemBomb(explosion);

		// Do central explosion
		explosion = (short) SpawnSprite(STAT_MISSILE, MUSHROOM_CLOUD, s_GrenadeExp[0], sp.sectnum, sp.x, sp.y, sp.z,
				sp.ang, 0);
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		SetOwner(sp.owner, explosion);
		exp.shade = -128;
		exp.xrepeat = 218;
		exp.yrepeat = 152;
		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		if (RANDOM_P2(1024) > 512)
			exp.cstat |= (CSTAT_SPRITE_XFLIP);

		eu.Radius = NUKE_RADIUS;

		SpawnExpZadjust(Weapon, explosion, Z(30), Z(30));

		DoExpDamageTest(explosion);

		// Nuclear effects
		SetNuclearQuake(explosion);

		SetFadeAmt(pp, -80, 1); // Nuclear flash

		// Secondary blasts
		ang = (short) RANDOM_P2(2048);
		SpawnNuclearSecondaryExp(explosion, ang);
		ang = (short) (ang + 512 + RANDOM_P2(256));
		SpawnNuclearSecondaryExp(explosion, ang);
		ang = (short) (ang + 512 + RANDOM_P2(256));
		SpawnNuclearSecondaryExp(explosion, ang);
		ang = (short) (ang + 512 + RANDOM_P2(256));
		SpawnNuclearSecondaryExp(explosion, ang);

		return (explosion);
	}

	public static int SpawnTracerExp(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		SPRITE exp;
		USER eu;
		short explosion;

		if (u != null && TEST(u.Flags, SPR_SUICIDE))
			return (-1);

		if (u.ID == BOLT_THINMAN_R1)
			explosion = (short) SpawnSprite(STAT_MISSILE, BOLT_THINMAN_R1, s_TracerExp[0], sp.sectnum, sp.x, sp.y, sp.z,
					sp.ang, 0);
		else
			explosion = (short) SpawnSprite(STAT_MISSILE, TRACER_EXP, s_TracerExp[0], sp.sectnum, sp.x, sp.y, sp.z,
					sp.ang, 0);
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.hitag = LUMINOUS; // Always full brightness
		SetOwner(sp.owner, explosion);
		exp.shade = -40;
		exp.xrepeat = 4;
		exp.yrepeat = 4;
		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		if (RANDOM_P2(1024) > 512)
			exp.cstat |= (CSTAT_SPRITE_XFLIP);

		if (u.ID == BOLT_THINMAN_R1) {
			eu.Radius = DamageData[DMG_BASIC_EXP].radius;
			DoExpDamageTest(explosion);
		} else
			eu.Radius = DamageData[DMG_BOLT_EXP].radius;

		return (explosion);
	}

	public static int AddSpriteToSectorObject(short SpriteNum, int sopi) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		short sn;

		// make sure it has a user
		if (u == null) {
			u = SpawnUser(SpriteNum, 0, null);
		}

		if (sopi == -1)
			return 0;

		Sector_Object sop = SectorObject[sopi];
		// find a free place on this list
		for (sn = 0; sn < sop.sp_num.length; sn++) {
			if (sop.sp_num[sn] == -1)
				break;
		}

		sop.sp_num[sn] = SpriteNum;

		u.Flags |= (SPR_ON_SO_SECTOR | SPR_SO_ATTACHED);

		u.sx = sop.xmid - sp.x;
		u.sy = sop.ymid - sp.y;
		u.sz = sector[sop.mid_sector].floorz - sp.z;

		u.sang = sp.ang;
		return (0);
	}

	public static int SpawnGrenadeSecondaryExp(int Weapon, short ang) {
		SPRITE sp = sprite[Weapon];
		SPRITE exp;
		USER eu;
		short explosion;
		int vel;

		explosion = (short) SpawnSprite(STAT_MISSILE, GRENADE_EXP, s_GrenadeSmallExp[0], sp.sectnum, sp.x, sp.y, sp.z,
				sp.ang, 1024);
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.hitag = LUMINOUS; // Always full brightness
		SetOwner(sp.owner, explosion);
		exp.shade = -40;
		exp.xrepeat = 32;
		exp.yrepeat = 32;
		exp.clipdist = sp.clipdist;
		eu.ceiling_dist = (short) Z(16);
		eu.floor_dist = (short) Z(16);
		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		// ang = RANDOM_P2(2048);
		vel = (1024 + 512) + RANDOM_RANGE(1024);
		eu.xchange = MOVEx(vel, ang);
		eu.ychange = MOVEy(vel, ang);

		eu.ret = move_missile(explosion, eu.xchange, eu.ychange, 0, eu.ceiling_dist, eu.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		if (FindDistance3D(exp.x - sp.x, exp.y - sp.y, (exp.z - sp.z) >> 4) < 1024) {
			KillSprite(explosion);
			return (-1);
		}

		SpawnExpZadjust(Weapon, explosion, Z(50), Z(10));

		eu.ox = exp.x;
		eu.oy = exp.y;
		eu.oz = exp.z;

		return (explosion);
	}

	public static int SpawnGrenadeSmallExp(int Weapon) {
		short ang;

		ang = (short) RANDOM_P2(2048);
		SpawnGrenadeSecondaryExp(Weapon, ang);
		return (0);
	}

	public static int SpawnGrenadeExp(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		SPRITE exp;
		USER eu;
		short explosion;
		int dx, dy, dz;

		if (u != null && TEST(u.Flags, SPR_SUICIDE))
			return (-1);

		PlaySound(DIGI_30MMEXPLODE, sp, v3df_none);

		if (RANDOM_RANGE(1000) > 990) {
			if (sp.owner >= 0 && pUser[sp.owner] != null && pUser[sp.owner].PlayerP != -1) {
				PlayerStr pp = Player[pUser[sp.owner].PlayerP];
				PlayerSound(DIGI_LIKEFIREWORKS, v3df_follow | v3df_dontpan, pp);
			}
		}

		dx = sp.x;
		dy = sp.y;
		dz = sp.z;

		if (u.ID == ZILLA_RUN_R0) {
			dx += RANDOM_RANGE(1000) - RANDOM_RANGE(1000);
			dy += RANDOM_RANGE(1000) - RANDOM_RANGE(1000);
			dz = SPRITEp_MID(sp) + RANDOM_RANGE(1000) - RANDOM_RANGE(1000);
		}

		explosion = (short) SpawnSprite(STAT_MISSILE, GRENADE_EXP, s_GrenadeExp[0], sp.sectnum, dx, dy, dz, sp.ang, 0);
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.hitag = LUMINOUS; // Always full brightness
		SetOwner(sp.owner, explosion);
		exp.shade = -40;
		exp.xrepeat = 64 + 32;
		exp.yrepeat = 64 + 32;
		exp.clipdist = sp.clipdist;
		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		eu.Radius = DamageData[DMG_GRENADE_EXP].radius;

		//
		// All this stuff assures that explosions do not go into floors &
		// ceilings
		//

		SpawnExpZadjust(Weapon, explosion, Z(100), Z(30));

		DoExpDamageTest(explosion);
		// InitMineShrap(explosion);

		SetExpQuake(explosion);
		SpawnVis(-1, exp.sectnum, exp.x, exp.y, exp.z, 0);

		return (explosion);
	}

	public static void SpawnExpZadjust(int Weapon, int expi, int upper_zsize, int lower_zsize) {
		SPRITE exp = sprite[expi];
		USER u = pUser[Weapon];
		USER eu = pUser[expi];
		int tos_z, bos_z;

		if (u != null) {
			tos_z = exp.z - upper_zsize;
			bos_z = exp.z + lower_zsize;

			if (tos_z <= u.hiz + Z(4)) {
				exp.z = u.hiz + upper_zsize;
				exp.cstat |= (CSTAT_SPRITE_YFLIP);
			} else if (bos_z > u.loz) {
				exp.z = u.loz - lower_zsize;
			}
		} else {
			int cz, fz;

			engine.getzsofslope(exp.sectnum, exp.x, exp.y, zofslope);
			cz = zofslope[CEIL];
			fz = zofslope[FLOOR];

			tos_z = exp.z - upper_zsize;
			bos_z = exp.z + lower_zsize;

			if (tos_z <= cz + Z(4)) {
				exp.z = cz + upper_zsize;
				exp.cstat |= (CSTAT_SPRITE_YFLIP);
			} else if (bos_z > fz) {
				exp.z = fz - lower_zsize;
			}
		}

		eu.oz = exp.z;
	}

	public static int SpawnMineExp(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		SPRITE exp;
		USER eu;
		short explosion;

		if (u != null && TEST(u.Flags, SPR_SUICIDE))
			return (-1);

		change_sprite_stat(Weapon, STAT_MISSILE);

		PlaySound(DIGI_MINEBLOW, sp, v3df_none);

		explosion = (short) SpawnSprite(STAT_MISSILE, MINE_EXP, s_MineExp[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang, 0);
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.hitag = LUMINOUS; // Always full brightness
		SetOwner(sp.owner, explosion);
		exp.shade = -40;
		exp.xrepeat = 64 + 44;
		exp.yrepeat = 64 + 44;
		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		eu.Radius = DamageData[DMG_MINE_EXP].radius;

		//
		// All this stuff assures that explosions do not go into floors &
		// ceilings
		//

		SpawnExpZadjust(Weapon, explosion, Z(100), Z(20));
		SpawnVis(-1, exp.sectnum, exp.x, exp.y, exp.z, 16);

		SetExpQuake(explosion);

		// DoExpDamageTest(explosion);

		return (explosion);
	}

	public static final int MINE_SHRAP_DIST_MAX = 20000;

	public static int InitMineShrap(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		SPRITE wp;
		short ang, w, i;
		int daz;

		for (i = 0; i < 18; i++) {
			ang = (short) RANDOM_P2(2048);
			daz = Z(RANDOM_P2(48)) << 3;
			daz -= DIV2(Z(48) << 3);

			FAFhitscan(sp.x, sp.y, sp.z - Z(30), sp.sectnum, // Start position
					sintable[NORM_ANGLE(ang + 512)], // X vector of 3D ang
					sintable[NORM_ANGLE(ang)], // Y vector of 3D ang
					daz, // Z vector of 3D ang
					pHitInfo, CLIPMASK_MISSILE);

			if (pHitInfo.hitsect < 0)
				continue;

			// check to see what you hit
			if (pHitInfo.hitsprite < 0 && pHitInfo.hitwall < 0) {
			}

			if (Distance(pHitInfo.hitx, pHitInfo.hity, sp.x, sp.y) > MINE_SHRAP_DIST_MAX)
				continue;

			// hit a sprite?
			if (pHitInfo.hitsprite >= 0) {

			}

			w = (short) SpawnSprite(STAT_MISSILE, MINE_SHRAP, s_MineSpark[0], pHitInfo.hitsect, pHitInfo.hitx,
					pHitInfo.hity, pHitInfo.hitz, ang, 0);
			if(w == -1)
				return 0;

			wp = sprite[w];
			wp.shade = -40;
			wp.hitag = LUMINOUS; // Always full brightness
			SetOwner(sp.owner, w);
			wp.cstat |= (CSTAT_SPRITE_TRANSLUCENT | CSTAT_SPRITE_YCENTER);

			wp.clipdist = 32 >> 2;

			HitscanSpriteAdjust(w, pHitInfo.hitwall);
		}

		return (0);
	}

	public static int SpawnBigGunFlames(int Weapon, int Operator, Sector_Object sop) {
		SPRITE sp;
		USER u;
		SPRITE exp;
		USER eu;
		short explosion;
		short sn;
		boolean smallflames = false;

		if (Weapon < 0) {
			Weapon = klabs(Weapon);
			smallflames = true;
		}

		sp = sprite[Weapon];
		u = pUser[Weapon];

		explosion = (short) SpawnSprite(STAT_MISSILE, MICRO_EXP, s_BigGunFlame[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang,
				0);
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.hitag = LUMINOUS; // Always full brightness
		SetOwner(Operator, explosion);
		exp.shade = -40;
		if (smallflames) {
			exp.xrepeat = 12;
			exp.yrepeat = 12;
		} else {
			exp.xrepeat = 34;
			exp.yrepeat = 34;
		}
		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		if (RANDOM_P2(1024) > 512)
			exp.cstat |= (CSTAT_SPRITE_XFLIP);
		if (RANDOM_P2(1024) > 512)
			exp.cstat |= (CSTAT_SPRITE_YFLIP);

		// place all sprites on list
		for (sn = 0; sn < sop.sp_num.length; sn++) {
			if (sop.sp_num[sn] == -1)
				break;
		}

		sop.sp_num[sn] = explosion;

		// Place sprite exactly where shoot point is

        eu.Flags |= (DTEST(u.Flags, SPR_ON_SO_SECTOR | SPR_SO_ATTACHED));

		if (TEST(u.Flags, SPR_ON_SO_SECTOR)) {
			// move with sector its on
			exp.z = eu.oz = sector[sp.sectnum].floorz - u.sz;
		} else {
			// move with the mid sector
			exp.z = eu.oz = sector[sop.mid_sector].floorz - u.sz;
		}

		eu.sx = u.sx;
		eu.sy = u.sy;
		eu.sz = u.sz;

		return (explosion);
	}

	public static int DoMineExp(int SpriteNum) {
		DoExpDamageTest(SpriteNum);

		return (0);
	}

	public static int DoSectorExp(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		sp.x += u.xchange;
		sp.y += u.ychange;

		return (0);
	}

	public static int SpawnSectorExp(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		SPRITE exp;
		USER eu;
		short explosion;

		if (TEST(u.Flags, SPR_SUICIDE))
			return (-1);

		PlaySound(DIGI_30MMEXPLODE, sp, v3df_none);

		explosion = (short) SpawnSprite(STAT_MISSILE, GRENADE_EXP, s_SectorExp[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang,
				0);
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.hitag = LUMINOUS; // Always full brightness
		exp.owner = -1;
		exp.shade = -40;
		exp.xrepeat = 90; // was 40,40
		exp.yrepeat = 90;
		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		eu.Radius = DamageData[DMG_SECTOR_EXP].radius;

		DoExpDamageTest(explosion);
		SetExpQuake(explosion);
		SpawnVis(-1, exp.sectnum, exp.x, exp.y, exp.z, 16);

		return (explosion);
	}

	public static int SpawnMeteorExp(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		SPRITE exp;
		USER eu;
		short explosion;

		if (TEST(u.Flags, SPR_SUICIDE))
			return (-1);

		if (u.spal == 25) // Serp ball
		{
			explosion = (short) SpawnSprite(STAT_MISSILE, METEOR_EXP, s_TeleportEffect2[0], sp.sectnum, sp.x, sp.y,
					sp.z, sp.ang, 0);
		} else {
			PlaySound(DIGI_MEDIUMEXP, sp, v3df_none);
			explosion = (short) SpawnSprite(STAT_MISSILE, METEOR_EXP, s_MeteorExp[0], sp.sectnum, sp.x, sp.y, sp.z,
					sp.ang, 0);
		}
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.hitag = LUMINOUS; // Always full brightness
		exp.owner = -1;
		exp.shade = -40;
		if (sp.yrepeat < 64) {
			// small
			exp.xrepeat = 64;
			exp.yrepeat = 64;
		} else {
			// large - boss
			exp.xrepeat = 80;
			exp.yrepeat = 80;
		}

		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		eu.Radius = DamageData[DMG_BASIC_EXP].radius;

		return (explosion);
	}

	public static int SpawnLittleExp(int Weapon) {
		SPRITE sp = sprite[Weapon];
		SPRITE exp;
		USER eu;
		short explosion;

		PlaySound(DIGI_HEADSHOTHIT, sp, v3df_none);
		explosion = (short) SpawnSprite(STAT_MISSILE, BOLT_EXP, s_SectorExp[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang,
				0);
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.hitag = LUMINOUS; // Always full brightness
		exp.owner = -1;
		exp.shade = -127;

		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		eu.Radius = DamageData[DMG_BASIC_EXP].radius;
		DoExpDamageTest(explosion);
		SpawnVis(-1, exp.sectnum, exp.x, exp.y, exp.z, 16);

		return (explosion);
	}

	public static boolean DoFireball(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		u = pUser[Weapon];

		if (TEST(u.Flags, SPR_UNDERWATER)) {
			sp.xrepeat = sp.yrepeat -= 1;
			if (sp.xrepeat <= 37) {
				SpawnSmokePuff(Weapon);
				KillSprite(Weapon);
				return (true);
			}
		}

		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);

		if (u.ret != 0) {
			boolean hit_burn = false;

			if (WeaponMoveHit(Weapon)) {
				switch (DTEST(u.ret, HIT_MASK)) {
				case HIT_SPRITE: {
					SPRITE hsp;
					USER hu;

					int hspi = NORM_SPRITE(u.ret);

					hsp = sprite[hspi];
					hu = pUser[hspi];

					if (TEST(hsp.extra, SPRX_BURNABLE)) {
						if (hu == null)
							hu = SpawnUser(hspi, hsp.picnum, null);
						SpawnFireballFlames(Weapon, hspi);
						hit_burn = true;
					}

					break;
				}
				}

				if (!hit_burn) {
					if (u.ID == GORO_FIREBALL)
						SpawnGoroFireballExp(Weapon);
					else
						SpawnFireballExp(Weapon);
				}

				KillSprite(Weapon);

				return (true);
			}
		}

		return (false);
	}

	public static boolean DoFindGround(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		int hsp = -1;
		USER u = pUser[SpriteNum];
		int florhit;
		short save_cstat;
		short bak_cstat;

		// recursive routine to find the ground - either sector or floor sprite
		// skips over enemy and other types of sprites

		save_cstat = sp.cstat;
		sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		FAFgetzrange(sp.x, sp.y, sp.z, sp.sectnum, tmp_ptr[0].set(0), tmp_ptr[1].set(0), tmp_ptr[2].set(0), tmp_ptr[3].set(0),
				((sp.clipdist) << 2) - GETZRANGE_CLIP_ADJ, CLIPMASK_PLAYER);

		u.hiz = tmp_ptr[0].value;
		u.loz = tmp_ptr[2].value;
		florhit = tmp_ptr[3].value;

		sp.cstat = save_cstat;

		switch (DTEST(florhit, HIT_MASK)) {
		case HIT_SPRITE: {
			hsp = NORM_SPRITE(florhit);

			if (TEST(sprite[hsp].cstat, CSTAT_SPRITE_FLOOR)) {
				// found a sprite floor
				u.lo_sp = hsp;
				u.lo_sectp = -1;
				return (true);
			} else {
				// reset the blocking bit of what you hit and try again -
				// recursive
				bak_cstat = sprite[hsp].cstat;
				sprite[hsp].cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
				DoFindGround(SpriteNum);
				sprite[hsp].cstat = bak_cstat;
			}

			return (false);
		}
		case HIT_SECTOR: {
			u.lo_sectp = NORM_SECTOR(florhit);
			u.lo_sp = -1;
			return (true);
		}

		default:
			break;
		}

		return (false);
	}

	public static boolean DoFindGroundPoint(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		int hsp;
		USER u = pUser[SpriteNum];
		int florhit;
		short save_cstat;
		short bak_cstat;

		// recursive routine to find the ground - either sector or floor sprite
		// skips over enemy and other types of sprites

		save_cstat = sp.cstat;
		sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		FAFgetzrangepoint(sp.x, sp.y, sp.z, sp.sectnum, tmp_ptr[0], tmp_ptr[1], tmp_ptr[2], tmp_ptr[3]); // &u.hiz,
																											// &ceilhit,
																											// &u.loz,
																											// &florhit);
		u.hiz = tmp_ptr[0].value;
		u.loz = tmp_ptr[2].value;
		florhit = tmp_ptr[3].value;

		sp.cstat = save_cstat;

		switch (DTEST(florhit, HIT_MASK)) {
		case HIT_SPRITE: {
			hsp = NORM_SPRITE(florhit);

			if (TEST(sprite[hsp].cstat, CSTAT_SPRITE_FLOOR)) {
				// found a sprite floor
				u.lo_sp = hsp;
				u.lo_sectp = -1;
				return (true);
			} else {
				// reset the blocking bit of what you hit and try again -
				// recursive
				bak_cstat = sprite[hsp].cstat;
				sprite[hsp].cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
				DoFindGroundPoint(SpriteNum);
				sprite[hsp].cstat = bak_cstat;
			}

			return (false);
		}
		case HIT_SECTOR: {
			u.lo_sectp = NORM_SECTOR(florhit);
			u.lo_sp = -1;
			return (true);
		}

		default:
			break;
		}

		return (false);
	}

	public static boolean DoNapalm(int Weapon) {
		SPRITE sp = sprite[Weapon], exp;
		USER u = pUser[Weapon];
		short explosion;
		int ox, oy, oz;

		DoBlurExtend(Weapon, 1, 7);

		u = pUser[Weapon];

		if (TEST(u.Flags, SPR_UNDERWATER)) {
			sp.xrepeat = sp.yrepeat -= 1;
			if (sp.xrepeat <= 30) {
				SpawnSmokePuff(Weapon);
				KillSprite(Weapon);
				return (true);
			}
		}

		ox = sp.x;
		oy = sp.y;
		oz = sp.z;

		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);

		if (TEST(u.Flags, SPR_UNDERWATER) && (RANDOM_P2(1024 << 4) >> 4) < 256)
			SpawnBubble(Weapon);

		if (u.ret != 0) {
			// this sprite is suPlayerosed to go through players/enemys
			// if hit a player/enemy back up and do it again with blocking reset
			if (DTEST(u.ret, HIT_MASK) == HIT_SPRITE) {
				SPRITE hsp = sprite[NORM_SPRITE(u.ret)];

				if (TEST(hsp.cstat, CSTAT_SPRITE_BLOCK) && !TEST(hsp.cstat, CSTAT_SPRITE_WALL)) {
					short hcstat = hsp.cstat;

					sp.x = ox;
					sp.y = oy;
					sp.z = oz;

					hsp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
					u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist,
							CLIPMASK_MISSILE, MISSILEMOVETICS);
					hsp.cstat = hcstat;
				}
			}
		}

		u.Counter++;
		if (u.Counter > 2)
			u.Counter = 0;

		if (u.Counter == 0) {
			USER eu;

			PlaySound(DIGI_NAPPUFF, sp, v3df_none);

			explosion = (short) SpawnSprite(STAT_MISSILE, NAP_EXP, s_NapExp[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang,
					0);
			if(explosion == -1)
				return false;

			exp = sprite[explosion];
			eu = pUser[explosion];

			exp.hitag = LUMINOUS; // Always full brightness
			// exp.owner = sp.owner;
			SetOwner(sp.owner, explosion);
			exp.shade = -40;
			exp.cstat = sp.cstat;
			exp.xrepeat = 48;
			exp.yrepeat = 64;
			exp.cstat |= (CSTAT_SPRITE_YCENTER);
			if (RANDOM_P2(1024) < 512)
				exp.cstat |= (CSTAT_SPRITE_XFLIP);
			exp.cstat &= ~(CSTAT_SPRITE_TRANSLUCENT);
			eu.Radius = 1500;

			DoFindGroundPoint(explosion);
			MissileWaterAdjust(explosion);
			exp.z = eu.loz;
			eu.oz = exp.z;

			if (TEST(u.Flags, SPR_UNDERWATER))
				eu.Flags |= (SPR_UNDERWATER);

		}

		// DoDamageTest(Weapon);

		if (u.ret != 0) {
			if (WeaponMoveHit(Weapon)) {
				KillSprite((short) Weapon);

				return (true);
			}
		}

		return (false);
	}

//called from SpawnShrap
	public static int SpawnLargeExp(int Weapon) {
		SPRITE sp = sprite[Weapon];
		SPRITE exp;
		USER eu;
		short explosion;

		PlaySound(DIGI_30MMEXPLODE, sp, v3df_none);

		explosion = (short) SpawnSprite(STAT_MISSILE, GRENADE_EXP, s_SectorExp[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang,
				0);
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.hitag = LUMINOUS; // Always full brightness
		exp.owner = -1;
		exp.shade = -40;
		exp.xrepeat = 90; // was 40,40
		exp.yrepeat = 90;
		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		eu.Radius = DamageData[DMG_SECTOR_EXP].radius;

		SpawnExpZadjust(Weapon, explosion, Z(50), Z(50));

		// Should not cause other sectors to explode
		DoExpDamageTest(explosion);
		SetExpQuake(explosion);
		SpawnVis(-1, exp.sectnum, exp.x, exp.y, exp.z, 16);

		return (explosion);
	}

	public static int SpawnMicroExp(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		SPRITE exp;
		USER eu;
		short explosion;

		if (u != null && TEST(u.Flags, SPR_SUICIDE))
			return (-1);

		explosion = (short) SpawnSprite(STAT_MISSILE, MICRO_EXP, s_MicroExp[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang,
				0);
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.hitag = LUMINOUS; // Always full brightness
		SetOwner(sp.owner, explosion);
		exp.shade = -40;
		exp.xrepeat = 32;
		exp.yrepeat = 32;
		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		if (RANDOM_P2(1024) > 512)
			exp.cstat |= (CSTAT_SPRITE_XFLIP);
		if (RANDOM_P2(1024) > 512)
			exp.cstat |= (CSTAT_SPRITE_YFLIP);
		eu.Radius = DamageData[DMG_BOLT_EXP].radius;

		//
		// All this stuff assures that explosions do not go into floors &
		// ceilings
		//

		SpawnExpZadjust(Weapon, explosion, Z(20), Z(20));
		SpawnVis(-1, exp.sectnum, exp.x, exp.y, exp.z, 16);

		return (explosion);
	}

	public static int SpawnBreakFlames(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		SPRITE np;
		USER nu;
		int newsp = SpawnSprite(STAT_MISSILE, FIREBALL_FLAMES + 1, s_BreakFlames[0], sp.sectnum, sp.x, sp.y, sp.z,
				sp.ang, 0);
		if(newsp == -1)
			return 0;

		np = sprite[newsp];
		nu = pUser[newsp];

		np.hitag = LUMINOUS; // Always full brightness

		np.xrepeat = 16;
		np.yrepeat = 16;
		nu.Counter = 48; // max flame size

		// SetOwner(sp.owner, new);
		np.shade = -40;
		if (u != null)
			np.pal = nu.spal = u.spal;
		np.cstat |= (CSTAT_SPRITE_YCENTER);
		np.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		nu.Radius = 200;

		nu.floor_dist = nu.ceiling_dist = 0;

		DoFindGround(newsp);
		nu.jump_speed = 0;
		DoBeginJump(newsp);

		Set3DSoundOwner(newsp, PlaySound(DIGI_FIRE1, np, v3df_dontpan | v3df_doppler));

		return (newsp);
	}

	public static int SpawnFireballFlames(int SpriteNum, int enemy) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		SPRITE np;
		USER nu;
		short newsp;

		if (u != null && TEST(u.Flags, SPR_UNDERWATER))
			return (-1);

		USER eu = (enemy >= 0) ? pUser[enemy] : null;
		SPRITE ep = (enemy >= 0) ? sprite[enemy] : null;

		if (enemy >= 0) {
			// test for already burned
			if (TEST(ep.extra, SPRX_BURNABLE) && ep.shade > 40)
				return (-1);

			if (eu.flame >= 0) {
				int sizez = SPRITEp_SIZE_Z(ep) + DIV4(SPRITEp_SIZE_Z(ep));
				np = sprite[eu.flame];
				nu = pUser[eu.flame];

				if (TEST(ep.extra, SPRX_BURNABLE))
					return (eu.flame);

				if (nu.Counter >= SPRITEp_SIZE_Z_2_YREPEAT(np, sizez)) {
					// keep flame only slightly bigger than the enemy itself
					nu.Counter = (short) SPRITEp_SIZE_Z_2_YREPEAT(np, sizez);
				} else {
					// increase max size
					nu.Counter += SPRITEp_SIZE_Z_2_YREPEAT(np, 8 << 8);
				}

				// Counter is max size
				if (nu.Counter >= 230) {
					// this is far too big
					nu.Counter = 230;
				}

				if (nu.WaitTics < 2 * 120)
					nu.WaitTics = 2 * 120; // allow it to grow again

				return (eu.flame);
			} else {
				if (eu.PlayerP != -1) {
				}
			}
		}

		newsp = (short) SpawnSprite(STAT_MISSILE, FIREBALL_FLAMES, s_FireballFlames[0], sp.sectnum, sp.x, sp.y, sp.z,
				sp.ang, 0);
		if(newsp == -1)
			return 0;

		np = sprite[newsp];
		nu = pUser[newsp];

		np.hitag = LUMINOUS; // Always full brightness

		if (enemy >= 0)
			eu.flame = newsp;

		np.xrepeat = 16;
		np.yrepeat = 16;
		if (enemy >= 0) {
			// large flame for trees and such
			if (TEST(ep.extra, SPRX_BURNABLE)) {
				int sizez = SPRITEp_SIZE_Z(ep) + DIV4(SPRITEp_SIZE_Z(ep));
				nu.Counter = (short) SPRITEp_SIZE_Z_2_YREPEAT(np, sizez);
			} else {
				nu.Counter = (short) SPRITEp_SIZE_Z_2_YREPEAT(np, SPRITEp_SIZE_Z(ep) >> 1);
			}
		} else {
			nu.Counter = 48; // max flame size
		}

		SetOwner(sp.owner, newsp);
		np.shade = -40;
		np.pal = nu.spal = u.spal;
		np.cstat |= (CSTAT_SPRITE_YCENTER);
		np.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		// nu.Radius = DamageData[DMG_FIREBALL_FLAMES].radius;
		nu.Radius = 200;

		if (enemy >= 0) {
			SetAttach(enemy, newsp);
		} else {
			if (TestDontStickSector(np.sectnum)) {
				KillSprite(newsp);
				return (-1);
			}

			nu.floor_dist = nu.ceiling_dist = 0;

			DoFindGround(newsp);
			nu.jump_speed = 0;
			DoBeginJump(newsp);
		}

		Set3DSoundOwner(newsp, PlaySound(DIGI_FIRE1, np, v3df_dontpan | v3df_doppler));

		return (newsp);
	}

	public static boolean DoSpear(int Weapon) {
		USER u = pUser[Weapon];

		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);

		if (TEST(u.Flags, SPR_UNDERWATER) && (RANDOM_P2(1024 << 4) >> 4) < 256)
			SpawnBubble(Weapon);

		if (TEST(u.Flags, SPR_SUICIDE))
			return (true);

		if (u.ret != 0) {
			if (WeaponMoveHit(Weapon)) {
				// SpawnShrap(Weapon, -1);
				KillSprite(Weapon);
				return (true);
			}
		}

		return (false);
	}

	public static boolean DoBloodWorm(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		short ang;
		int xvect, yvect;
		int bx, by;
		long amt;
		short sectnum;

		u = pUser[Weapon];

		u.ret = move_ground_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist,
				CLIPMASK_MISSILE, MISSILEMOVETICS);

		if (u.ret != 0) {
			u.xchange = -u.xchange;
			u.ychange = -u.ychange;
			u.ret = 0;
			sp.ang = NORM_ANGLE(sp.ang + 1024);
			return (true);
		}

		MissileHitDiveArea(Weapon);

		if (u.z_tgt == 0) {
			// stay alive for 10 seconds
			if (++u.Counter3 > 3) {
				SPRITE tsp;
				USER tu;
				int i, nexti;

				InitBloodSpray(Weapon, false, 1);
				InitBloodSpray(Weapon, false, 1);
				InitBloodSpray(Weapon, false, 1);

				// Kill any old zombies you own
				for (i = headspritestat[STAT_ENEMY]; i != -1; i = nexti) {
					nexti = nextspritestat[i];
					tsp = sprite[i];
					tu = pUser[i];

					if (tu.ID == ZOMBIE_RUN_R0 && tsp.owner == sp.owner) {
						InitBloodSpray(i, true, 105);
						InitBloodSpray(i, true, 105);
						SetSuicide(i);
						break;
					}
				}

				SpawnZombie2(Weapon);
				KillSprite(Weapon);
				return (true);
			}
		}

		ang = NORM_ANGLE(sp.ang + 512);

		xvect = sintable[NORM_ANGLE(ang + 512)];
		yvect = sintable[ang];

		bx = sp.x;
		by = sp.y;

		amt = RANDOM_P2(2048) - 1024;
		sp.x += mulscale(amt, xvect, 15);
		sp.y += mulscale(amt, yvect, 15);

		sectnum = sp.sectnum;
		sectnum = engine.updatesectorz(sp.x, sp.y, sp.z, sectnum);
		if (sectnum >= 0) {
			GlobalSkipZrange = true;
			InitBloodSpray(Weapon, false, 1);
			GlobalSkipZrange = false;
		}

		sp.x = bx;
		sp.y = by;

		return (false);
	}

	public static boolean DoMeteor(int Weapon) {
		return false;
	}

	public static boolean DoSerpMeteor(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		int ox, oy, oz;

		ox = sp.x;
		oy = sp.y;
		oz = sp.z;

		sp.xrepeat += MISSILEMOVETICS * 2;
		if (sp.xrepeat > 80)
			sp.xrepeat = 80;

		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		if (u.ret != 0) {
			// this sprite is supposed to go through players/enemys
			// if hit a player/enemy back up and do it again with blocking reset
			if (DTEST(u.ret, HIT_MASK) == HIT_SPRITE) {
				SPRITE hsp = sprite[NORM_SPRITE(u.ret)];
				USER hu = pUser[NORM_SPRITE(u.ret)];

				if (hu != null && hu.ID >= SKULL_R0 && hu.ID <= SKULL_SERP) {
					short hcstat = hsp.cstat;

					sp.x = ox;
					sp.y = oy;
					sp.z = oz;

					hsp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
					u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist,
							CLIPMASK_MISSILE, MISSILEMOVETICS);
					hsp.cstat = hcstat;
				}
			}
		}

		if (u.ret != 0) {
			if (WeaponMoveHit(Weapon)) {
				SpawnMeteorExp(Weapon);

				KillSprite((short) Weapon);

				return (true);
			}
		}

		return (false);
	}

	public static boolean DoMirvMissile(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		u = pUser[Weapon];

		sp.xrepeat += MISSILEMOVETICS * 2;
		if (sp.xrepeat > 80)
			sp.xrepeat = 80;

		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		if (TEST(u.Flags, SPR_UNDERWATER) && (RANDOM_P2(1024 << 4) >> 4) < 256)
			SpawnBubble(Weapon);

		if (u.ret != 0) {
			if (WeaponMoveHit(Weapon)) {
				SpawnMeteorExp(Weapon);

				KillSprite((short) Weapon);

				return (true);
			}
		}

		return (false);
	}

	private static final short angs[] = { 512, -512 };

	public static boolean DoMirv(int Weapon) {
		SPRITE sp = sprite[Weapon], np;
		USER u = pUser[Weapon], nu;
		short newsp;

		u = pUser[Weapon];

		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);

		if (TEST(u.Flags, SPR_UNDERWATER) && (RANDOM_P2(1024 << 4) >> 4) < 256)
			SpawnBubble(Weapon);

		u.Counter++;
		u.Counter &= 1;

		if (u.Counter == 0) {
			int i;

			for (i = 0; i < 2; i++) {
				newsp = (short) SpawnSprite(STAT_MISSILE, MIRV_METEOR, WeaponStateGroup.sg_MirvMeteor.getState(0),
						sp.sectnum, sp.x, sp.y, sp.z, NORM_ANGLE(sp.ang + angs[i]), 800);
				if(newsp == -1)
					return false;

				np = sprite[newsp];
				nu = pUser[newsp];

				nu.RotNum = 5;
				NewStateGroup(newsp, WeaponStateGroup.sg_MirvMeteor);
				nu.StateEnd = s_MirvMeteorExp[0];

				// np.owner = Weapon;
				SetOwner(Weapon, newsp);
				np.shade = -40;
				np.xrepeat = 40;
				np.yrepeat = 40;
				np.clipdist = 32 >> 2;
				np.zvel = 0;
				np.cstat |= (CSTAT_SPRITE_YCENTER);

				nu.ceiling_dist = (short) Z(16);
				nu.floor_dist = (short) Z(16);
				nu.Dist = 200;
				// nu.Dist = 0;

				nu.xchange = MOVEx(np.xvel, np.ang);
				nu.ychange = MOVEy(np.xvel, np.ang);
				nu.zchange = np.zvel;

				if (TEST(u.Flags, SPR_UNDERWATER))
					nu.Flags |= (SPR_UNDERWATER);
			}

		}

		if (u.ret != 0) {
			SpawnMeteorExp(Weapon);
			KillSprite((short) Weapon);
			return (true);
		}

		return (false);
	}

	public static boolean MissileSetPos(int Weapon, Animator DoWeapon, int dist) {
		SPRITE wp = sprite[Weapon];
		USER wu = pUser[Weapon];
		int oldvel, oldzvel;
		int oldxc, oldyc, oldzc;
		boolean retval = false;

		// backup values
		oldxc = wu.xchange;
		oldyc = wu.ychange;
		oldzc = wu.zchange;
		oldvel = wp.xvel;
		oldzvel = wp.zvel;

		// make missile move in smaller increments
		wp.xvel = (short) ((dist * 6) / MISSILEMOVETICS);
		// wp.zvel = (wp.zvel*4) / MISSILEMOVETICS;
		wp.zvel = (short) ((wp.zvel * 6) / MISSILEMOVETICS);

		// some Weapon Animators use this
		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		wu.Flags |= (SPR_SET_POS_DONT_KILL);

		if (DoWeapon.invoke(Weapon))
			retval = true;

		wu.Flags &= ~(SPR_SET_POS_DONT_KILL);

		// reset values
		wu.xchange = oldxc;
		wu.ychange = oldyc;
		wu.zchange = oldzc;
		wp.xvel = (short) oldvel;
		wp.zvel = (short) oldzvel;

		// update for interpolation
		wu.ox = wp.x;
		wu.oy = wp.y;
		wu.oz = wp.z;

		return (retval);
	}

	public static boolean TestMissileSetPos(int Weapon, Animator DoWeapon, int dist, int zvel) {
		SPRITE wp = sprite[Weapon];
		USER wu = pUser[Weapon];
		int oldvel, oldzvel;
		int oldxc, oldyc, oldzc;
		boolean retval = false;

		// backup values
		oldxc = wu.xchange;
		oldyc = wu.ychange;
		oldzc = wu.zchange;
		oldvel = wp.xvel;
		oldzvel = wp.zvel;

		// make missile move in smaller increments
		wp.xvel = (short) ((dist * 6) / MISSILEMOVETICS);
		// wp.zvel = (wp.zvel*4) / MISSILEMOVETICS;
		zvel = (zvel * 6) / MISSILEMOVETICS;

		// some Weapon Animators use this
		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = zvel;

		wu.Flags |= (SPR_SET_POS_DONT_KILL);

		if (DoWeapon.invoke(Weapon))
			retval = true;
		wu.Flags &= ~(SPR_SET_POS_DONT_KILL);

		// reset values
		wu.xchange = oldxc;
		wu.ychange = oldyc;
		wu.zchange = oldzc;
		wp.xvel = (short) oldvel;
		wp.zvel = (short) oldzvel;

		// update for interpolation
		wu.ox = wp.x;
		wu.oy = wp.y;
		wu.oz = wp.z;

		return (retval);
	}

	public static final int RINGMOVETICS = (MISSILEMOVETICS * 2);
	public static final int RING_OUTER_DIST = 3200;
	public static final int RING_INNER_DIST = 800;

	public static boolean DoRing(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		PlayerStr pp = Player[pUser[sp.owner].PlayerP];
		SPRITE so = sprite[sp.owner];

		if (TEST(u.Flags, SPR_UNDERWATER)) {
			sp.xrepeat = sp.yrepeat -= 1;
			if (sp.xrepeat <= 30) {
				SpawnSmokePuff(Weapon);
				KillSprite(Weapon);
				return (true);
			}
		}

		// move the center with the player
		sp.x = sprite[sp.owner].x;
		sp.y = sprite[sp.owner].y;
		if (pUser[sp.owner].PlayerP != -1)
			sp.z = pp.posz + Z(20);
		else
			sp.z = SPRITEp_MID(so) + Z(30);

		// go out until its time to come back in
		if (u.Counter2 == 0) {
			u.Dist += 8 * RINGMOVETICS;

			if (u.Dist > RING_OUTER_DIST) {
				u.Counter2 = 1;
			}
		} else {
			u.Dist -= 8 * RINGMOVETICS;

			if (u.Dist <= RING_INNER_DIST) {
				if (pUser[sp.owner].PlayerP == -1)
					pUser[sp.owner].Counter--;
				KillSprite(Weapon);
				return false;
			}
		}

		// sp.ang = NORM_ANGLE(sp.ang - 512);

		// rotate the ring
		sp.ang = NORM_ANGLE(sp.ang + (4 * RINGMOVETICS) + RINGMOVETICS);

		// put it out there
		sp.x += ((long) u.Dist * (long) sintable[NORM_ANGLE(sp.ang + 512)]) >> 14;
		sp.y += ((long) u.Dist * (long) sintable[sp.ang]) >> 14;
		if (pUser[sp.owner].PlayerP != -1)
			sp.z += (u.Dist * ((100 - pp.getHorizi()) * HORIZ_MULT)) >> 9;

        engine.setsprite(Weapon, sp.x, sp.y, sp.z);

		engine.getzsofslope(sp.sectnum, sp.x, sp.y, zofslope);

		// bound the sprite by the sectors ceiling and floor
		if (sp.z > zofslope[FLOOR]) {
			sp.z = zofslope[FLOOR];
		}

		if (sp.z < zofslope[CEIL] + SPRITEp_SIZE_Z(sp)) {
			sp.z = zofslope[CEIL] + SPRITEp_SIZE_Z(sp);
		}

		// Done last - check for damage
		DoDamageTest(Weapon);

		return false;
	}

	public static void InitSpellRing(PlayerStr pp) {
		short ang, ang_diff, ang_start, SpriteNum, missiles;
		SPRITE sp;
		USER u;
		short max_missiles = 16;
		short ammo;

		ammo = NAPALM_MIN_AMMO;

		if (pp.WpnAmmo[WPN_HOTHEAD] < ammo)
			return;
		else
			PlayerUpdateAmmo(pp, WPN_HOTHEAD, -ammo);

		ang_diff = (short) (2048 / max_missiles);

		ang_start = NORM_ANGLE(pp.getAnglei() - DIV2(2048));

		PlaySound(DIGI_RFWIZ, pp, v3df_none);

		for (missiles = 0, ang = ang_start; missiles < max_missiles; ang += ang_diff, missiles++) {
			SpriteNum = (short) SpawnSprite(STAT_MISSILE_SKIP4, FIREBALL1, s_Ring[0], pp.cursectnum, pp.posx, pp.posy,
					pp.posz, ang, 0);
			if(SpriteNum == -1)
				return;

			sp = sprite[SpriteNum];

			sp.hitag = LUMINOUS; // Always full brightness
			sp.xvel = 500;
			// sp.owner = pp.SpriteP - sprite;
			SetOwner(pp.PlayerSprite, SpriteNum);
			sp.shade = -40;
			sp.xrepeat = 32;
			sp.yrepeat = 32;
			sp.zvel = 0;

			u = pUser[SpriteNum];

			u.sz = Z(20);
			u.Dist = RING_INNER_DIST;
			u.Counter = max_missiles;
			u.Counter2 = 0;
			u.ceiling_dist = (short) Z(10);
			u.floor_dist = (short) Z(10);

			// put it out there
			sp.x += ((long) u.Dist * (long) sintable[NORM_ANGLE(sp.ang + 512)]) >> 14;
			sp.y += ((long) u.Dist * (long) sintable[sp.ang]) >> 14;
			sp.z = pp.posz + Z(20) + ((u.Dist * ((100 - pp.getHorizi()) * HORIZ_MULT)) >> 9);

			sp.ang = NORM_ANGLE(sp.ang + 512);

			u.ox = sp.x;
			u.oy = sp.y;
			u.oz = sp.z;

			if (TEST(pp.Flags, PF_DIVING) || SpriteInUnderwaterArea(sp))
				u.Flags |= (SPR_UNDERWATER);
		}
	}

	public static int DoSerpRing(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		USER ou = pUser[sp.owner];
		int dist;
		int cz, fz;

		// if owner does not exist or he's dead on the floor
		// kill off all of his skull children
		if (sp.owner == -1 || ou.RotNum < 5) {
			UpdateSinglePlayKills(Weapon, null);
			DoSkullBeginDeath(Weapon);
			// +2 does not spawn shrapnel
			u.ID = SKULL_SERP;
			return (0);
		}

		// move the center with the player
		sp.x = sprite[sp.owner].x;
		sp.y = sprite[sp.owner].y;

		sp.z += sp.zvel;
		if (sp.z > sprite[sp.owner].z - u.sz)
			sp.z = sprite[sp.owner].z - u.sz;

		// go out until its time to come back in
		if (u.Counter2 == 0) {
			u.Dist += 8 * RINGMOVETICS;

			if (u.Dist > u.TargetDist)
				u.Counter2 = 1;
		}

		// rotate the ring
		u.slide_ang = NORM_ANGLE(u.slide_ang + sp.yvel);

		// rotate the heads
		if (TEST(u.Flags, SPR_BOUNCE))
			sp.ang = NORM_ANGLE(sp.ang + (28 * RINGMOVETICS));
		else
			sp.ang = NORM_ANGLE(sp.ang - (28 * RINGMOVETICS));

		// put it out there
		sp.x += ((long) u.Dist * (long) sintable[NORM_ANGLE(u.slide_ang + 512)]) >> 14;
		sp.y += ((long) u.Dist * (long) sintable[u.slide_ang]) >> 14;

		engine.setsprite(Weapon, sp.x, sp.y, sp.z);

		engine.getzsofslope(sp.sectnum, sp.x, sp.y, zofslope);
		cz = zofslope[CEIL];
		fz = zofslope[FLOOR];

		// bound the sprite by the sectors ceiling and floor
		if (sp.z > fz) {
			sp.z = fz;
		}

		if (sp.z < cz + SPRITEp_SIZE_Z(sp)) {
			sp.z = cz + SPRITEp_SIZE_Z(sp);
		}

		if (u.Counter2 > 0) {
			if (pUser[ou.tgt_sp] == null || pUser[ou.tgt_sp].PlayerP == -1
					|| !TEST(Player[pUser[ou.tgt_sp].PlayerP].Flags, PF_DEAD)) {
				u.tgt_sp = ou.tgt_sp;
				dist = DISTANCE(sp.x, sp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

				// if ((dist ok and random ok) OR very few skulls left)
				if ((dist < 18000 && (RANDOM_P2(2048 << 5) >> 5) < 16) || (sp.owner != -1 && pUser[sp.owner] != null && pUser[sp.owner].Counter < 4)) {
					short sectnum = sp.sectnum;
					sectnum = COVERupdatesector(sp.x, sp.y, sectnum);

					// if (valid sector and can see target)
					if (sectnum != -1 && CanSeePlayer(Weapon)) {
						u.ID = SKULL_R0;
						sp.ang = engine.getangle(sprite[u.tgt_sp].x - sp.x, sprite[u.tgt_sp].y - sp.y);
						sp.xvel = (short) (dist >> 5);
						sp.xvel += DIV2(sp.xvel);
						sp.xvel += (RANDOM_P2(128 << 8) >> 8);
						u.jump_speed = -800;
						change_sprite_stat(Weapon, STAT_ENEMY);
						NewStateGroup(Weapon, SkullStateGroup.sg_SkullJump);
						DoBeginJump(Weapon);
						return (0);
					}
				}
			}
		}

		return (0);
	}

	public static int InitLavaFlame(int SpriteNum) {
		return (0);
	}

	public static int InitLavaThrow(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], wp;
		USER u = pUser[SpriteNum], wu;
		int nx, ny, nz, dist, nang;
		short w;

		// PlaySound(DIGI_NINJAROCKETATTACK, sp, v3df_none);

		// get angle to player and also face player when attacking
		sp.ang = (short) (nang = engine.getangle(sprite[u.tgt_sp].x - sp.x, sprite[u.tgt_sp].y - sp.y));

		nx = sp.x;
		ny = sp.y;
		nz = SPRITEp_TOS(sp) + DIV4(SPRITEp_SIZE_Z(sp));

		// Spawn a shot
		w = (short) SpawnSprite(STAT_MISSILE, LAVA_BOULDER, s_LavaBoulder[0], sp.sectnum, nx, ny, nz, nang,
				NINJA_BOLT_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		SetOwner(SpriteNum, w);
		wp.hitag = LUMINOUS; // Always full brightness
		wp.yrepeat = 72;
		wp.xrepeat = 72;
		wp.shade = -15;
		wp.zvel = 0;
		wp.ang = (short) nang;

		if (RANDOM_P2(1024) > 512)
			wp.cstat |= (CSTAT_SPRITE_XFLIP);
		if (RANDOM_P2(1024) > 512)
			wp.cstat |= (CSTAT_SPRITE_YFLIP);

		wu.Radius = 200;
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.clipdist = 256 >> 2;
		wu.ceiling_dist = (short) Z(14);
		wu.floor_dist = (short) Z(14);

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		MissileSetPos(w, DoLavaBoulder, 1200);

		// find the distance to the target (player)
		dist = Distance(wp.x, wp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

		if (dist != 0)
			wu.zchange = wp.zvel = (short) ((wp.xvel * (SPRITEp_UPPER(sprite[u.tgt_sp]) - wp.z)) / dist);

		return (w);
	}

	public static int InitVulcanBoulder(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], wp;
		USER wu;
		int nx, ny, nz, nang;
		short w;
		int zsize;
		int zvel, zvel_rand;
		short delta;
		short vel;

		nx = sp.x;
		ny = sp.y;
		nz = sp.z - Z(40);

		if (SP_TAG7(sp) != 0) {
			delta = (short) SP_TAG5(sp);
			nang = sp.ang + (RANDOM_RANGE(delta) - DIV2(delta));
			nang = NORM_ANGLE(nang);
		} else {
			nang = RANDOM_P2(2048);
		}

		if (SP_TAG6(sp) != 0)
			vel = (short) SP_TAG6(sp);
		else
			vel = 800;

		// Spawn a shot
		w = (short) SpawnSprite(STAT_MISSILE, LAVA_BOULDER, s_VulcanBoulder[0], sp.sectnum, nx, ny, nz, nang,
				(vel / 2 + vel / 4) + RANDOM_RANGE(vel / 4));
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		SetOwner(SpriteNum, w);
		wp.xrepeat = wp.yrepeat = (short) (8 + RANDOM_RANGE(72));
		wp.shade = -40;
		wp.ang = (short) nang;
		wu.Counter = 0;

		zsize = SPRITEp_SIZE_Z(wp);

		wu.Radius = 200;
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.clipdist = 256 >> 2;
		wu.ceiling_dist = (short) (zsize / 2);
		wu.floor_dist = (short) (zsize / 2);
		if (RANDOM_P2(1024) > 512)
			wp.cstat |= (CSTAT_SPRITE_XFLIP);
		if (RANDOM_P2(1024) > 512)
			wp.cstat |= (CSTAT_SPRITE_YFLIP);

		if (SP_TAG7(sp) != 0) {
			zvel = SP_TAG7(sp);
			zvel_rand = SP_TAG8(sp);
		} else {
			zvel = 50;
			zvel_rand = 40;
		}

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = -Z(zvel) + -Z(RANDOM_RANGE(zvel_rand));

		return (w);
	}

	public static final int SERP_RING_DIST = 2800; // Was 3500

	public static int InitSerpRing(int SpriteNum) {
		SPRITE sp = pUser[SpriteNum].getSprite(), np;
		USER u = pUser[SpriteNum], nu;
		short ang, ang_diff, ang_start, missiles, newsp;
		short max_missiles;

		max_missiles = 12;

		u.Counter = max_missiles;

		ang_diff = (short) (2048 / max_missiles);

		ang_start = NORM_ANGLE(sp.ang - DIV2(2048));

		PlaySound(DIGI_SERPSUMMONHEADS, sp, v3df_none);

		for (missiles = 0, ang = ang_start; missiles < max_missiles; ang += ang_diff, missiles++) {
			newsp = (short) SpawnSprite(STAT_SKIP4, SKULL_SERP, s_SkullRing[0][0], sp.sectnum, sp.x, sp.y, sp.z, ang,
					0);
			if(newsp == -1)
				return 0;

			np = sprite[newsp];
			nu = pUser[newsp];

			np.xvel = 500;
			// np.owner = SpriteNum;
			SetOwner(SpriteNum, newsp);
			np.shade = -20;
			np.xrepeat = 64;
			np.yrepeat = 64;
			np.yvel = 2 * RINGMOVETICS;
			np.zvel = (short) Z(3);
			np.pal = 0;

			np.z = SPRITEp_TOS(sp) - Z(20);
			nu.sz = Z(50);

			// ang around the serp is now slide_ang
			nu.slide_ang = np.ang;
			// randomize the head turning angle
			np.ang = (short) (RANDOM_P2(2048 << 5) >> 5);

			// control direction of spinning
			u.Flags ^= (SPR_BOUNCE);
			nu.Flags |= (DTEST(u.Flags, SPR_BOUNCE));

			nu.Dist = 600;
			nu.TargetDist = SERP_RING_DIST;
			nu.Counter2 = 0;

			nu.StateEnd = s_SkullExplode[0];
			nu.Rot = SkullStateGroup.sg_SkullRing;

			// defaults do change the statnum
			EnemyDefaults(newsp, null, null);
			change_sprite_stat(newsp, STAT_SKIP4);
			np.extra &= ~(SPRX_PLAYER_OR_ENEMY);

			np.clipdist = (128 + 64) >> 2;
			nu.Flags |= (SPR_XFLIP_TOGGLE);
			np.cstat |= (CSTAT_SPRITE_YCENTER);

			nu.Radius = 400;
		}
		return (0);
	}

	private static class MISSILE_PLACEMENT {
		int dist_over, dist_out;
		short ang;

		public MISSILE_PLACEMENT(int dist_over, int dist_out, int ang) {
			this.dist_over = dist_over;
			this.dist_out = dist_out;
			this.ang = (short) ang;
		}
	};

	private static MISSILE_PLACEMENT mp[] = { new MISSILE_PLACEMENT(600 * 6, 400, 512),
			new MISSILE_PLACEMENT(0, 1100, 0), new MISSILE_PLACEMENT(600 * 6, 400, -512), };

	public static void InitSpellNapalm(PlayerStr pp) {
		short SpriteNum;
		SPRITE sp;
		USER u;
		short i;
		short oclipdist;
		short ammo;

		ammo = NAPALM_MIN_AMMO;

		if (pp.WpnAmmo[WPN_HOTHEAD] < ammo)
			return;
		else
			PlayerUpdateAmmo(pp, WPN_HOTHEAD, -ammo);

		PlaySound(DIGI_NAPFIRE, pp, v3df_none);

		for (i = 0; i < mp.length; i++) {
			SpriteNum = (short) SpawnSprite(STAT_MISSILE, FIREBALL1, s_Napalm[0], pp.cursectnum, pp.posx, pp.posy,
					pp.posz + Z(12), pp.getAnglei(), NAPALM_VELOCITY * 2);
			if(SpriteNum == -1)
				return;

			sp = sprite[SpriteNum];
			u = pUser[SpriteNum];

			sp.hitag = LUMINOUS; // Always full brightness

			if (i == 0) // Only attach sound to first projectile
			{

				Set3DSoundOwner(SpriteNum, PlaySound(DIGI_NAPWIZ, sp, v3df_follow));
			}

			// sp.owner = pp.SpriteP - sprite;
			SetOwner(pp.PlayerSprite, SpriteNum);
			sp.shade = -40;
			sp.xrepeat = 32;
			sp.yrepeat = 32;
			sp.clipdist = 0;
			sp.zvel = (short) ((100 - pp.getHorizi()) * HORIZ_MULT);
			sp.cstat |= (CSTAT_SPRITE_TRANSLUCENT | CSTAT_SPRITE_YCENTER);
			sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
			u.Flags2 |= (SPR2_BLUR_TAPER_FAST);

			u.floor_dist = (short) Z(1);
			u.ceiling_dist = (short) Z(1);
			u.Dist = 200;

			SPRITE psp = pp.getSprite();
			oclipdist = (short) psp.clipdist;
			psp.clipdist = 1;

			if (mp[i].dist_over != 0) {
				sp.ang = NORM_ANGLE(sp.ang + mp[i].ang);
				HelpMissileLateral(SpriteNum, mp[i].dist_over);
				sp.ang = NORM_ANGLE(sp.ang - mp[i].ang);
			}

			u.xchange = MOVEx(sp.xvel, sp.ang);
			u.ychange = MOVEy(sp.xvel, sp.ang);
			u.zchange = sp.zvel;

			if (MissileSetPos(SpriteNum, DoNapalm, mp[i].dist_out)) {
				psp.clipdist = oclipdist;
				KillSprite(SpriteNum);
				continue;
			}

			if (TEST(pp.Flags, PF_DIVING) || SpriteInUnderwaterArea(sp))
				u.Flags |= (SPR_UNDERWATER);

			psp.clipdist = oclipdist;

			u.Counter = 0;
		}
	}

	public static int InitEnemyNapalm(int SpriteNum) {
		short w;
		SPRITE sp = sprite[SpriteNum], wp;
		USER u = pUser[SpriteNum], wu;
		short dist;
		short i;
		short oclipdist;

		PlaySound(DIGI_NAPFIRE, sp, v3df_none);

		for (i = 0; i < mp.length; i++) {
			w = (short) SpawnSprite(STAT_MISSILE, FIREBALL1, s_Napalm[0], sp.sectnum, sp.x, sp.y,
					SPRITEp_TOS(sp) + DIV4(SPRITEp_SIZE_Z(sp)), sp.ang, NAPALM_VELOCITY);
			if(w == -1)
				return 0;

			wp = sprite[w];
			wu = pUser[w];

			wp.hitag = LUMINOUS; // Always full brightness
			if (i == 0) // Only attach sound to first projectile
			{
				Set3DSoundOwner(w, PlaySound(DIGI_NAPWIZ, wp, v3df_follow));
			}

			if (u.ID == ZOMBIE_RUN_R0)
				SetOwner(sp.owner, w);
			else
				SetOwner(SpriteNum, w);

			wp.shade = -40;
			wp.xrepeat = 32;
			wp.yrepeat = 32;
			wp.clipdist = 0;
			wp.cstat |= (CSTAT_SPRITE_TRANSLUCENT | CSTAT_SPRITE_YCENTER);
			wp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
			wu.Flags2 |= (SPR2_BLUR_TAPER_FAST);

			wu.floor_dist = (short) Z(1);
			wu.ceiling_dist = (short) Z(1);
			wu.Dist = 200;

			oclipdist = (short) sp.clipdist;
			sp.clipdist = 1;

			if (mp[i].dist_over != 0) {
				wp.ang = NORM_ANGLE(wp.ang + mp[i].ang);
				HelpMissileLateral(w, mp[i].dist_over);
				wp.ang = NORM_ANGLE(wp.ang - mp[i].ang);
			}

			// find the distance to the target (player)
			dist = (short) Distance(wp.x, wp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

			if (dist != 0)
				wp.zvel = (short) ((wp.xvel * (SPRITEp_UPPER(sprite[u.tgt_sp]) - wp.z)) / dist);

			wu.xchange = MOVEx(wp.xvel, wp.ang);
			wu.ychange = MOVEy(wp.xvel, wp.ang);
			wu.zchange = wp.zvel;

			MissileSetPos(w, DoNapalm, mp[i].dist_out);

			sp.clipdist = oclipdist;

			u.Counter = 0;

		}
		return (0);
	}

	public static int InitSpellMirv(PlayerStr pp) {
		short SpriteNum;
		SPRITE sp;
		USER u;
		short oclipdist;

		PlaySound(DIGI_MIRVFIRE, pp, v3df_none);

		SpriteNum = (short) SpawnSprite(STAT_MISSILE, FIREBALL1, s_Mirv[0], pp.cursectnum, pp.posx, pp.posy,
				pp.posz + Z(12), pp.getAnglei(), MIRV_VELOCITY);
		if(SpriteNum == -1)
			return 0;

		sp = sprite[SpriteNum];
		u = pUser[SpriteNum];


		Set3DSoundOwner(SpriteNum, PlaySound(DIGI_MIRVWIZ, sp, v3df_follow));

		// sp.owner = pp.SpriteP - sprite;
		SetOwner(pp.PlayerSprite, SpriteNum);
		sp.shade = -40;
		sp.xrepeat = 72;
		sp.yrepeat = 72;
		sp.clipdist = 32 >> 2;
		sp.zvel = (short) ((100 - pp.getHorizi()) * HORIZ_MULT);
		sp.cstat |= (CSTAT_SPRITE_TRANSLUCENT | CSTAT_SPRITE_YCENTER);
		sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		u.floor_dist = (short) Z(16);
		u.ceiling_dist = (short) Z(16);
		u.Dist = 200;

		SPRITE psp = pp.getSprite();
		oclipdist = (short) psp.clipdist;
		psp.clipdist = 0;

		u.xchange = MOVEx(sp.xvel, sp.ang);
		u.ychange = MOVEy(sp.xvel, sp.ang);
		u.zchange = sp.zvel;

		MissileSetPos(SpriteNum, DoMirv, 600);
		psp.clipdist = oclipdist;

		u.Counter = 0;
		return (0);
	}

	public static int InitEnemyMirv(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], wp;
		USER u = pUser[SpriteNum], wu;
		short w;
		int dist;

		PlaySound(DIGI_MIRVFIRE, sp, v3df_none);

		w = (short) SpawnSprite(STAT_MISSILE, MIRV_METEOR, s_Mirv[0], sp.sectnum, sp.x, sp.y,
				SPRITEp_TOS(sp) + DIV4(SPRITEp_SIZE_Z(sp)), sp.ang, MIRV_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		Set3DSoundOwner(w, PlaySound(DIGI_MIRVWIZ, wp, v3df_follow));

		SetOwner(SpriteNum, w);
		wp.shade = -40;
		wp.xrepeat = 72;
		wp.yrepeat = 72;
		wp.clipdist = 32 >> 2;

		wp.cstat |= (CSTAT_SPRITE_TRANSLUCENT | CSTAT_SPRITE_YCENTER);
		wp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		wu.floor_dist = (short) Z(16);
		wu.ceiling_dist = (short) Z(16);
		wu.Dist = 200;

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		MissileSetPos(w, DoMirv, 600);

		// find the distance to the target (player)
		dist = Distance(wp.x, wp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

		if (dist != 0)
			wu.zchange = wp.zvel = (short) ((wp.xvel * (SPRITEp_UPPER(sprite[u.tgt_sp]) - wp.z)) / dist);
		return (0);
	}

	public static int CLOSE_RANGE_DIST_FUDGE(SPRITE sp1, SPRITE sp2, int fudge) {
		return (((sp1).clipdist << 2) + ((sp2).clipdist << 2) + (fudge));
	}

	private static short dangs2[] = { -128, 128 };

	public static int InitFistAttack(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite], tu;
		SPRITE sp = null;
		short i, nexti, stat;
		long dist;
		short reach, face;

		PlaySound(DIGI_STAR, pp, v3df_dontpan | v3df_doppler);

		if (TEST(pp.Flags, PF_DIVING)) {
			short bubble;
			SPRITE bp;
			int nx, ny;
			short random_amt;

			for (i = 0; i < dangs2.length; i++) {
				bubble = (short) SpawnBubble(pp.PlayerSprite);
				if (bubble >= 0) {
					bp = sprite[bubble];

					bp.ang = pp.getAnglei();

					random_amt = (short) ((RANDOM_P2(32 << 8) >> 8) - 16);

					// back it up a bit to get it out of your face
					nx = MOVEx((1024 + 256) * 3, NORM_ANGLE(bp.ang + dangs2[i] + random_amt));
					ny = MOVEy((1024 + 256) * 3, NORM_ANGLE(bp.ang + dangs2[i] + random_amt));

					move_missile(bubble, nx, ny, 0, u.ceiling_dist, u.floor_dist, CLIPMASK_PLAYER, 1);
				}
			}
		}

		for (stat = 0; stat < StatDamageList.length; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				sp = sprite[i];

                if (pUser[i].PlayerP == pp.pnum)
					break;

				if (!TEST(sp.extra, SPRX_PLAYER_OR_ENEMY))
					continue;

				dist = Distance(pp.posx, pp.posy, sp.x, sp.y);

				if (pp.InventoryActive[2]) // Shadow Bombs give you demon fist
				{
					face = 190;
					reach = 2300;
				} else {
					reach = 1000;
					face = 200;
				}

				SPRITE psp = pp.getSprite();
				if (dist < CLOSE_RANGE_DIST_FUDGE(sp, psp, reach) && PLAYER_FACING_RANGE(pp, sp, face)) {
					if (SpriteOverlapZ(pp.PlayerSprite, i, Z(20)) || face == 190) {
						if (FAFcansee(sp.x, sp.y, SPRITEp_MID(sp), sp.sectnum, psp.x, psp.y,
								SPRITEp_MID(psp), psp.sectnum))
							DoDamage(i, pp.PlayerSprite);
						if (face == 190) {
							SpawnDemonFist(i);
						}
					}
				}
			}
		}

		// all this is to break glass
		{
			short hitsect, hitwall, hitsprite, daang;
			int hitx, hity, hitz, daz;

			daang = pp.getAnglei();
			daz = ((100 - pp.getHorizi()) * 2000) + (RANDOM_RANGE(24000) - 12000);

			FAFhitscan(pp.posx, pp.posy, pp.posz, pp.cursectnum, // Start position
					sintable[NORM_ANGLE(daang + 512)], // X vector of 3D ang
					sintable[NORM_ANGLE(daang)], // Y vector of 3D ang
					daz, // Z vector of 3D ang
					pHitInfo, CLIPMASK_MISSILE);

			hitsect = pHitInfo.hitsect;
			hitwall = pHitInfo.hitwall;
			hitsprite = pHitInfo.hitsprite;
			hitx = pHitInfo.hitx;
			hity = pHitInfo.hity;
			hitz = pHitInfo.hitz;

			if (hitsect < 0)
				return (0);

			if (FindDistance3D(pp.posx - hitx, pp.posy - hity, (pp.posz - hitz) >> 4) < 700) {

				if (hitsprite >= 0) {
					SPRITE hsp = sprite[hitsprite];
					tu = pUser[hitsprite];

					if (tu != null) {
						switch (tu.ID) {
						case ZILLA_RUN_R0:
							SpawnSwordSparks(pp, hitsect, -1, hitx, hity, hitz, daang);
							PlaySound(DIGI_ARMORHIT, sndCoords.set(hitx, hity, hitz), v3df_none);
							break;
						case TRASHCAN:
							if (tu.WaitTics <= 0) {
								tu.WaitTics = (short) SEC(2);
								ChangeState(hitsprite, s_TrashCanPain[0]);
							}
							SpawnSwordSparks(pp, hitsect, -1, hitx, hity, hitz, daang);
							PlaySound(DIGI_ARMORHIT, sndCoords.set(hitx, hity, hitz), v3df_none);
							PlaySound(DIGI_TRASHLID, sp, v3df_none);
							break;
						case PACHINKO1:
						case PACHINKO2:
						case PACHINKO3:
						case PACHINKO4:
						case 623:
							SpawnSwordSparks(pp, hitsect, -1, hitx, hity, hitz, daang);
							PlaySound(DIGI_ARMORHIT, sndCoords.set(hitx, hity, hitz), v3df_none);
							break;
						}
					}

					if (sprite[hitsprite].lotag == TAG_SPRITE_HIT_MATCH) {
						if (MissileHitMatch(-1, WPN_STAR, hitsprite))
							return (0);
					}

					if (TEST(hsp.extra, SPRX_BREAKABLE)) {
						HitBreakSprite(hitsprite, 0);
					}

					// hit a switch?
					if (TEST(hsp.cstat, CSTAT_SPRITE_WALL) && (hsp.lotag != 0 || hsp.hitag != 0)) {
						ShootableSwitch(hitsprite, -1);
					}

					switch (hsp.picnum) {
					case 5062:
					case 5063:
					case 4947:
						SpawnSwordSparks(pp, hitsect, -1, hitx, hity, hitz, daang);
						PlaySound(DIGI_ARMORHIT, sndCoords.set(hitx, hity, hitz), v3df_none);
						if (RANDOM_RANGE(1000) > 700)
							PlayerUpdateHealth(pp, 1); // Give some health
						hsp.cstat |= (CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
						break;
					}
				}

				if (hitwall >= 0) {
					if (wall[hitwall].nextsector >= 0) {
						if (TEST(sector[wall[hitwall].nextsector].ceilingstat, CEILING_STAT_PLAX)) {
							if (hitz < sector[wall[hitwall].nextsector].ceilingz) {
								return (0);
							}
						}
					}

					if (wall[hitwall].lotag == TAG_WALL_BREAK) {
						HitBreakWall(hitwall, hitx, hity, hitz, daang, u.ID);
					}
					// hit non breakable wall - do sound and puff
					else {
						SpawnSwordSparks(pp, hitsect, hitwall, hitx, hity, hitz, daang);
						PlaySound(DIGI_ARMORHIT, sndCoords.set(hitx, hity, hitz), v3df_none);
						if (PlayerTakeDamage(pp, -1)) {
							PlayerUpdateHealth(pp, -(RANDOM_RANGE(2 << 8) >> 8));
							PlayerCheckDeath(pp, -1);
						}
					}
				}
			}

			return (0);
		}
	}

	public static int InitSumoNapalm(int SpriteNum) {
		short w;
		SPRITE sp = sprite[SpriteNum], wp;
		USER u = pUser[SpriteNum], wu;
		short dist;
		short j, ang;
		short oclipdist;

		PlaySound(DIGI_NAPFIRE, sp, v3df_none);

		ang = sp.ang;
		for (j = 0; j < 4; j++) {

			{
				w = (short) SpawnSprite(STAT_MISSILE, FIREBALL1, s_Napalm[0], sp.sectnum, sp.x, sp.y, SPRITEp_TOS(sp),
						ang, NAPALM_VELOCITY);
				if(w == -1)
					return 0;

				wp = sprite[w];
				wu = pUser[w];

				wp.hitag = LUMINOUS; // Always full brightness

				Set3DSoundOwner(w, PlaySound(DIGI_NAPWIZ, wp, v3df_follow));

				SetOwner(SpriteNum, w);
				wp.shade = -40;
				wp.xrepeat = 32;
				wp.yrepeat = 32;
				wp.clipdist = 0;
				wp.cstat |= (CSTAT_SPRITE_TRANSLUCENT | CSTAT_SPRITE_YCENTER);
				wp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
				wu.Flags2 |= (SPR2_BLUR_TAPER_FAST);

				wu.floor_dist = (short) Z(1);
				wu.ceiling_dist = (short) Z(1);
				wu.Dist = 200;

				oclipdist = (short) sp.clipdist;
				sp.clipdist = 1;

				if (mp[1].dist_over != 0) {
					wp.ang = NORM_ANGLE(wp.ang + mp[1].ang);
					HelpMissileLateral(w, mp[1].dist_over);
					wp.ang = NORM_ANGLE(wp.ang - mp[1].ang);
				}

				// find the distance to the target (player)
				dist = (short) Distance(wp.x, wp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

				if (dist != 0)
					wp.zvel = (short) ((wp.xvel * (SPRITEp_UPPER(sprite[u.tgt_sp]) - wp.z)) / dist);

				wu.xchange = MOVEx(wp.xvel, wp.ang);
				wu.ychange = MOVEy(wp.xvel, wp.ang);
				wu.zchange = wp.zvel;

				MissileSetPos(w, DoNapalm, mp[1].dist_out);

				sp.clipdist = oclipdist;

				u.Counter = 0;

			}
			ang += 512;
		}
		return (0);
	}

	public static int InitSumoSkull(int SpriteNum) {
		SPRITE sp = pUser[SpriteNum].getSprite(), np;
		USER u = pUser[SpriteNum], nu;
		short newsp;

		PlaySound(DIGI_SERPSUMMONHEADS, sp, v3df_none);

		newsp = (short) SpawnSprite(STAT_ENEMY, SKULL_R0, s_SkullWait[0][0], sp.sectnum, sp.x, sp.y, SPRITEp_MID(sp),
				sp.ang, 0);
		if(newsp == -1)
			return 0;

		np = sprite[newsp];
		nu = pUser[newsp];

		np.xvel = 500;
		SetOwner(SpriteNum, newsp);
		np.shade = -20;
		np.xrepeat = 64;
		np.yrepeat = 64;
		np.pal = 0;

		// randomize the head turning angle
		np.ang = (short) (RANDOM_P2(2048 << 5) >> 5);

		// control direction of spinning
		u.Flags ^= (SPR_BOUNCE);
		nu.Flags |= (DTEST(u.Flags, SPR_BOUNCE));

		nu.StateEnd = s_SkullExplode[0];
		nu.Rot = SkullStateGroup.sg_SkullWait;

		nu.Attrib = SkullAttrib;
		DoActorSetSpeed(SpriteNum, NORM_SPEED);
		nu.Counter = (short) RANDOM_P2(2048);
		nu.sz = np.z;
		nu.Health = 100;

		// defaults do change the statnum
		EnemyDefaults(newsp, null, null);
		// change_sprite_stat(new, STAT_SKIP4);
		np.extra |= (SPRX_PLAYER_OR_ENEMY);

		np.clipdist = (128 + 64) >> 2;
		nu.Flags |= (SPR_XFLIP_TOGGLE);
		np.cstat |= (CSTAT_SPRITE_YCENTER);

		nu.Radius = 400;
		return (0);
	}

	public static int InitSumoStompAttack(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = sprite[SpriteNum], tsp;
		short i, nexti, stat;
		long dist;
		short reach;

		PlaySound(DIGI_30MMEXPLODE, sp, v3df_dontpan | v3df_doppler);

		for (stat = 0; stat < StatDamageList.length; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				tsp = sprite[i];

				if (i != u.tgt_sp)
					break;

				if (!TEST(tsp.extra, SPRX_PLAYER_OR_ENEMY))
					continue;

				dist = Distance(sp.x, sp.y, tsp.x, tsp.y);

				reach = 16384;

				if (dist < CLOSE_RANGE_DIST_FUDGE(tsp, sp, reach)) {
					if (FAFcansee(tsp.x, tsp.y, SPRITEp_MID(tsp), tsp.sectnum, sp.x, sp.y, SPRITEp_MID(sp), sp.sectnum))
						DoDamage(i, SpriteNum);
				}
			}
		}

		return (0);
	}

	public static int InitMiniSumoClap(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		int dist;
		short reach;

		if (u.tgt_sp == -1)
			return (0);

		dist = Distance(sp.x, sp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

		reach = 10000;

		if (dist < CLOSE_RANGE_DIST_FUDGE(sprite[u.tgt_sp], sp, 1000)) {
			if (SpriteOverlapZ(SpriteNum, u.tgt_sp, Z(20))) {
				if (FAFcansee(sprite[u.tgt_sp].x, sprite[u.tgt_sp].y, SPRITEp_MID(sprite[u.tgt_sp]),
						sprite[u.tgt_sp].sectnum, sp.x, sp.y, SPRITEp_MID(sp), sp.sectnum)) {
					PlaySound(DIGI_CGTHIGHBONE, sp, v3df_follow | v3df_dontpan);
					DoDamage(u.tgt_sp, SpriteNum);
				}
			}
		} else if (dist < CLOSE_RANGE_DIST_FUDGE(sprite[u.tgt_sp], sp, reach)) {
			if (FAFcansee(sprite[u.tgt_sp].x, sprite[u.tgt_sp].y, SPRITEp_MID(sprite[u.tgt_sp]),
					sprite[u.tgt_sp].sectnum, sp.x, sp.y, SPRITEp_MID(sp), sp.sectnum)) {
				PlaySound(DIGI_30MMEXPLODE, sp, v3df_none);
				SpawnFireballFlames(SpriteNum, u.tgt_sp);
			}
		}

		return (0);
	}

	public static int WeaponAutoAim(int spnum, int Missile, int ang, int test) {
		USER wu = pUser[Missile];
		USER u = pUser[spnum];
		SPRITE wp = sprite[Missile];
		int hitsprite = -1;
		int dist;
		int zh;

		if (u != null && u.PlayerP != -1) {
			if (!TEST(Player[u.PlayerP].Flags, PF_AUTO_AIM)) {
				return (-1);
			}
		}

		if ((hitsprite = DoPickTarget(spnum, ang, test)) != -1) {
			SPRITE hp = sprite[hitsprite];
			USER hu = pUser[hitsprite];

			wu.WpnGoal = (short) hitsprite;
			hu.Flags |= (SPR_TARGETED);
			hu.Flags |= (SPR_ATTACKED);

			wp.ang = NORM_ANGLE(engine.getangle(hp.x - wp.x, hp.y - wp.y));
			dist = FindDistance2D(wp.x - hp.x, wp.y - hp.y);

			if (dist != 0) {
				int tos, diff, siz;

				tos = SPRITEp_TOS(hp);
				diff = wp.z - tos;
				siz = SPRITEp_SIZE_Z(hp);

				// hitsprite is below
				if (diff < -Z(50))
					zh = tos + DIV2(siz);
				else
				// hitsprite is above
				if (diff > Z(50))
					zh = tos + DIV8(siz);
				else
					zh = tos + DIV4(siz);

				wp.zvel = (short) ((wp.xvel * (zh - wp.z)) / dist);
			}
		}

		return (hitsprite);
	}

	public static int WeaponAutoAimZvel(int spnum, int Missile, LONGp zvel, int ang, int test) {
		USER wu = pUser[Missile];
		USER u = pUser[spnum];
		SPRITE wp = sprite[Missile];
		short hitsprite = -1;
		int dist;
		int zh;

		if (u != null && u.PlayerP != -1) {
			if (!TEST(Player[u.PlayerP].Flags, PF_AUTO_AIM)) {
				return (-1);
			}
		}

		if ((hitsprite = (short) DoPickTarget(spnum, ang, test)) != -1) {
			SPRITE hp = sprite[hitsprite];
			USER hu = pUser[hitsprite];

			wu.WpnGoal = hitsprite;
			hu.Flags |= (SPR_TARGETED);
			hu.Flags |= (SPR_ATTACKED);

			wp.ang = NORM_ANGLE(engine.getangle(hp.x - wp.x, hp.y - wp.y));
			// dist = FindDistance2D(wp.x, wp.y, hp.x, hp.y);
			dist = FindDistance2D(wp.x - hp.x, wp.y - hp.y);

			if (dist != 0) {

				int tos, diff, siz;

				tos = SPRITEp_TOS(hp);
				diff = wp.z - tos;
				siz = SPRITEp_SIZE_Z(hp);

				// hitsprite is below
				if (diff < -Z(50))
					zh = tos + DIV2(siz);
				else
				// hitsprite is above
				if (diff > Z(50))
					zh = tos + DIV8(siz);
				else
					zh = tos + DIV4(siz);

				zvel.value = (wp.xvel * (zh - wp.z)) / dist;
			}
		}

		return (hitsprite);
	}

	public static int AimHitscanToTarget(int spnum, LONGp z, LONGp ang, int z_ratio) {
		SPRITE sp = sprite[spnum];

		USER u = pUser[spnum];
		short hitsprite = -1;
		int dist;
		int zh;
		int xvect;
		int yvect;
		SPRITE hp;
		USER hu;

		if (u.tgt_sp == -1)
			return (-1);

		hitsprite = (short) u.tgt_sp;
		hp = sprite[hitsprite];
		hu = pUser[hitsprite];

		hu.Flags |= (SPR_TARGETED);
		hu.Flags |= (SPR_ATTACKED);

		// Global set by DoPickTarget
		ang.value = engine.getangle(hp.x - sp.x, hp.y - sp.y);

		// find the distance to the target
		dist = engine.ksqrt(SQ(sp.x - hp.x) + SQ(sp.y - hp.y));

		if (dist != 0) {
			zh = SPRITEp_UPPER(hp);

			xvect = sintable[NORM_ANGLE(ang.value + 512)];
			yvect = sintable[NORM_ANGLE(ang.value)];

			if (hp.x - sp.x != 0)
				// *z = xvect * ((zh - *z)/(hp.x - sp.x));
				z.value = scale(xvect, zh - z.value, hp.x - sp.x);
			else if (hp.y - sp.y != 0)
				// *z = yvect * ((zh - *z)/(hp.y - sp.y));
				z.value = scale(yvect, zh - z.value, hp.y - sp.y);
			else
				z.value = 0;

			// so actors won't shoot straight up at you
			// need to be a bit of a distance away
			// before they have a valid shot
			if (klabs(z.value / dist) > z_ratio) {
				return (-1);
			}
		}

		return (hitsprite);
	}

	public static int WeaponAutoAimHitscan(int spnum, LONGp z, LONGp ang, int test) {
		SPRITE sp = sprite[spnum];
		USER u = pUser[spnum];
		short hitsprite = -1;
		int dist;
		int zh;
		int xvect;
		int yvect;

		if (u != null && u.PlayerP != -1) {
			if (!TEST(Player[u.PlayerP].Flags, PF_AUTO_AIM)) {
				return (-1);
			}
		}

		if ((hitsprite = (short) DoPickTarget(spnum, ang.value, test)) != -1) {
			SPRITE hp = sprite[hitsprite];
			USER hu = pUser[hitsprite];

			hu.Flags |= (SPR_TARGETED);
			hu.Flags |= (SPR_ATTACKED);

			// Global set by DoPickTarget
			// *ang = target_ang;
			ang.value = NORM_ANGLE(engine.getangle(hp.x - sp.x, hp.y - sp.y));

			// find the distance to the target
			dist = engine.ksqrt(SQ(sp.x - hp.x) + SQ(sp.y - hp.y));

			if (dist != 0) {
				zh = SPRITEp_TOS(hp) + DIV4(SPRITEp_SIZE_Z(hp));

				xvect = sintable[NORM_ANGLE(ang.value + 512)];
				yvect = sintable[NORM_ANGLE(ang.value)];

				if (hp.x - sp.x != 0)
					// *z = xvect * ((zh - *z)/(hp.x - sp.x));
					z.value = scale(xvect, zh - z.value, hp.x - sp.x);
				else if (hp.y - sp.y != 0)
					// *z = yvect * ((zh - *z)/(hp.y - sp.y));
					z.value = scale(yvect, zh - z.value, hp.y - sp.y);
				else
					z.value = 0;
			}
		}

		return (hitsprite);
	}

	public static void WeaponHitscanShootFeet(SPRITE sp, SPRITE hp, LONGp zvect) {
		int dist;
		int zh;
		int xvect;
		int yvect;
		int z;
		short ang;

		// Global set by DoPickTarget
		// *ang = target_ang;
		ang = NORM_ANGLE(engine.getangle(hp.x - sp.x, hp.y - sp.y));

		// find the distance to the target
		dist = engine.ksqrt(SQ(sp.x - hp.x) + SQ(sp.y - hp.y));

		if (dist != 0) {
			zh = SPRITEp_BOS(hp) + Z(20);
			z = sp.z;

			xvect = sintable[NORM_ANGLE(ang + 512)];
			yvect = sintable[NORM_ANGLE(ang)];

			if (hp.x - sp.x != 0)
				zvect.value = scale(xvect, zh - z, hp.x - sp.x);
			else if (hp.y - sp.y != 0)
				zvect.value = scale(yvect, zh - z, hp.y - sp.y);
			else
				zvect.value = 0;
		}
	}

	public static final int STAR_REPEAT = 26;
	public static final int STAR_HORIZ_ADJ = 100;
	private static short dang3[] = { -12, 12 };

	public static int InitStar(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		USER wu;
		SPRITE wp;
		int nx, ny, nz;
		short w;
		int zvel;
		int i;
		SPRITE np;
		USER nu;
		short nw;

		PlayerUpdateAmmo(pp, u.WeaponNum, -3);

		nx = pp.posx;
		ny = pp.posy;

		nz = pp.posz + pp.bob_z + Z(8);

		PlaySound(DIGI_STAR, pp, v3df_dontpan | v3df_doppler);

		// Spawn a shot
		// Inserting and setting up variables

		w = (short) SpawnSprite(STAT_MISSILE, STAR1, s_Star[0], pp.cursectnum, nx, ny, nz, pp.getAnglei(), STAR_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];
		SetOwner(pp.PlayerSprite, w);
		wp.yrepeat = wp.xrepeat = STAR_REPEAT;
		wp.shade = -25;
		wp.clipdist = 32 >> 2;
		// wp.zvel was overflowing with this calculation - had to move to a local
		// long var
		zvel = ((100 - pp.getHorizi()) * (HORIZ_MULT + STAR_HORIZ_ADJ));

		wu.ceiling_dist = (short) Z(1);
		wu.floor_dist = (short) Z(1);
		wu.WeaponNum = u.WeaponNum;
		wu.Radius = 100;
		wu.Counter = 0;
		wp.cstat |= (CSTAT_SPRITE_YCENTER);

		// zvel had to be tweaked alot for this weapon
		// MissileSetPos seemed to be pushing the sprite too far up or down when
		// the horizon was tilted. Never figured out why.
		wp.zvel = (short) (zvel >> 1);
		if (MissileSetPos(w, DoStar, 1000)) {
			KillSprite(w);
			return (0);
		}

		if (WeaponAutoAim(pp.PlayerSprite, w, 32, 0) != -1) {
			zvel = wp.zvel;
		}

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = zvel;

		if (TEST(pp.Flags, PF_DIVING) || SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		wu.ox = wp.x;
		wu.oy = wp.y;
		wu.oz = wp.z;

		for (i = 0; i < dang3.length; i++) {
			nw = (short) SpawnSprite(STAT_MISSILE, STAR1, s_Star[0], pp.cursectnum, nx, ny, nz,
					NORM_ANGLE(wp.ang + dang3[i]), wp.xvel);
			if(nw == -1)
				return 0;

			np = sprite[nw];
			nu = pUser[nw];

			SetOwner(wp.owner, nw);
			np.yrepeat = np.xrepeat = STAR_REPEAT;
			np.shade = wp.shade;

			np.extra = wp.extra;
			np.clipdist = wp.clipdist;
			nu.WeaponNum = wu.WeaponNum;
			nu.Radius = wu.Radius;
			nu.ceiling_dist = wu.ceiling_dist;
			nu.floor_dist = wu.floor_dist;
			nu.Flags2 = wu.Flags2;

			if (TEST(pp.Flags, PF_DIVING) || SpriteInUnderwaterArea(np))
				nu.Flags |= (SPR_UNDERWATER);

			zvel = ((100 - pp.getHorizi()) * (HORIZ_MULT + STAR_HORIZ_ADJ));
			np.zvel = (short) (zvel >> 1);

			if (MissileSetPos(nw, DoStar, 1000)) {
				KillSprite(nw);
				return (0);
			}

			// move the same as middle star
			zvel = wu.zchange;

			nu.xchange = MOVEx(np.xvel, np.ang);
			nu.ychange = MOVEy(np.xvel, np.ang);
			nu.zchange = zvel;

			nu.ox = np.x;
			nu.oy = np.y;
			nu.oz = np.z;
		}

		return (0);
	}

	public static void InitHeartAttack(PlayerStr pp) {
		short SpriteNum;
		SPRITE sp;
		USER u;
		short i = 1;
		short oclipdist;

		PlayerUpdateAmmo(pp, WPN_HEART, -1);

		SpriteNum = (short) SpawnSprite(STAT_MISSILE_SKIP4, BLOOD_WORM, s_BloodWorm[0], pp.cursectnum, pp.posx, pp.posy,
				pp.posz + Z(12), pp.getAnglei(), BLOOD_WORM_VELOCITY * 2);
		if(SpriteNum == -1)
			return;

		sp = sprite[SpriteNum];
		u = pUser[SpriteNum];

		sp.hitag = LUMINOUS; // Always full brightness

		SetOwner(pp.PlayerSprite, SpriteNum);
		sp.shade = -10;
		sp.xrepeat = 52;
		sp.yrepeat = 52;
		sp.clipdist = 0;
		sp.zvel = (short) ((100 - pp.getHorizi()) * HORIZ_MULT);
		sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		u.Flags2 |= (SPR2_DONT_TARGET_OWNER);
		sp.cstat |= (CSTAT_SPRITE_INVISIBLE);

		u.floor_dist = (short) Z(1);
		u.ceiling_dist = (short) Z(1);
		u.Dist = 200;

		SPRITE psp = pp.getSprite();
		oclipdist = (short) psp.clipdist;
		psp.clipdist = 1;

		u.xchange = MOVEx(sp.xvel, sp.ang);
		u.ychange = MOVEy(sp.xvel, sp.ang);
		u.zchange = sp.zvel;

		MissileSetPos(SpriteNum, DoBloodWorm, mp[i].dist_out);

		psp.clipdist = oclipdist;
		u.Counter = 0;
		u.Counter2 = 0;
		u.Counter3 = 0;
		u.WaitTics = 0;
	}

	public static int ContinueHitscan(PlayerStr pp, short sectnum, int x, int y, int z, int ang, int xvect, int yvect,
			int zvect) {
		short j;
		short hitsect, hitwall, hitsprite;
		int hitx, hity, hitz;
		USER u = pUser[pp.PlayerSprite];

		FAFhitscan(x, y, z, sectnum, xvect, yvect, zvect, pHitInfo, CLIPMASK_MISSILE);

		hitsect = pHitInfo.hitsect;
		hitwall = pHitInfo.hitwall;
		hitsprite = pHitInfo.hitsprite;
		hitx = pHitInfo.hitx;
		hity = pHitInfo.hity;
		hitz = pHitInfo.hitz;

		if (hitsect < 0)
			return (0);

		if (hitsprite < 0 && hitwall < 0) {
			if (klabs(hitz - sector[hitsect].ceilingz) <= Z(1)) {
				hitz += Z(16);
				if (TEST(sector[hitsect].ceilingstat, CEILING_STAT_PLAX))
					return (0);
			} else if (klabs(hitz - sector[hitsect].floorz) <= Z(1)) {
			}
		}

		if (hitwall >= 0) {
			if (wall[hitwall].nextsector >= 0) {
				if (TEST(sector[wall[hitwall].nextsector].ceilingstat, CEILING_STAT_PLAX)) {
					if (hitz < sector[wall[hitwall].nextsector].ceilingz) {
						return (0);
					}
				}
			}

			if (wall[hitwall].lotag == TAG_WALL_BREAK) {
				HitBreakWall(hitwall, hitx, hity, hitz, ang, u.ID);
				return (0);
			}

			QueueHole((short) ang, hitsect, hitwall, hitx, hity, hitz);
		}

		// hit a sprite?
		if (hitsprite >= 0) {
			SPRITE hsp = sprite[hitsprite];

			if (hsp.lotag == TAG_SPRITE_HIT_MATCH) {
				if (MissileHitMatch(-1, WPN_SHOTGUN, hitsprite))
					return (0);
			}

			if (TEST(hsp.extra, SPRX_BREAKABLE)) {
				HitBreakSprite(hitsprite, 0);
				return (0);
			}

			if (BulletHitSprite(pp.PlayerSprite, hitsprite, hitsect, hitwall, hitx, hity, hitz, 0))
				return (0);

			// hit a switch?
			if (TEST(hsp.cstat, CSTAT_SPRITE_WALL) && (hsp.lotag != 0 || hsp.hitag != 0)) {
				ShootableSwitch(hitsprite, -1);
			}
		}

		j = (short) SpawnShotgunSparks(pp, hitsect, hitwall, hitx, hity, hitz, ang);
		DoHitscanDamage(j, hitsprite);

		return (0);
	}

	public static int InitShotgun(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		SPRITE hsp;
		short daang, ndaang, i, j;
		short hitsect, hitwall, hitsprite, nsect;
		int hitx, hity, hitz, daz, ndaz;
		int nx, ny, nz;
		int xvect, yvect, zvect;
		SPRITE sp;

		PlayerUpdateAmmo(pp, u.WeaponNum, -1);

		PlaySound(DIGI_RIOTFIRE2, pp, v3df_dontpan | v3df_doppler);

		// Make sprite shade brighter
		u.Vis = 128;

		if (pp.WpnShotgunAuto != 0) {
			switch (pp.WpnShotgunType) {
			case 1:
				pp.WpnShotgunAuto--;
			}
		}

		nx = pp.posx;
		ny = pp.posy;
		daz = nz = pp.posz + pp.bob_z;
		nsect = pp.cursectnum;
		sp = pp.getSprite();

		daang = 64;
		if (WeaponAutoAimHitscan(pp.PlayerSprite, tmp_ptr[0].set(daz), tmp_ptr[1].set(daang), 0) != -1) {
			daz = tmp_ptr[0].value;
			daang = (short) tmp_ptr[1].value;
		} else {
			daz = (100 - pp.getHorizi()) * 2000;
			daang = pp.getAnglei();
		}

		for (i = 0; i < 12; i++) {
			if (pp.WpnShotgunType == 0) {
				ndaz = daz + (RANDOM_RANGE(Z(120)) - Z(45));
				ndaang = NORM_ANGLE(daang + (RANDOM_RANGE(30) - 15));
			} else {
				ndaz = daz + (RANDOM_RANGE(Z(200)) - Z(65));
				ndaang = NORM_ANGLE(daang + (RANDOM_RANGE(70) - 30));
			}

			xvect = sintable[NORM_ANGLE(ndaang + 512)];
			yvect = sintable[NORM_ANGLE(ndaang)];
			zvect = ndaz;
			FAFhitscan(nx, ny, nz, nsect, // Start position
					xvect, yvect, zvect, pHitInfo, CLIPMASK_MISSILE);

			hitsect = pHitInfo.hitsect;
			hitwall = pHitInfo.hitwall;
			hitsprite = pHitInfo.hitsprite;
			hitx = pHitInfo.hitx;
			hity = pHitInfo.hity;
			hitz = pHitInfo.hitz;

			if (hitsect < 0) {
                continue;
			}

			if (hitsprite < 0 && hitwall < 0) {
				if (klabs(hitz - sector[hitsect].ceilingz) <= Z(1)) {
					hitz += Z(16);

					if (TEST(sector[hitsect].ceilingstat, CEILING_STAT_PLAX))
						continue;

					if (SectorIsUnderwaterArea(hitsect)) {
						WarpToSurface(tmp_ptr[0].set(hitsect), tmp_ptr[1].set(hitx), tmp_ptr[2].set(hity),
								tmp_ptr[3].set(hitz));
						hitsect = (short) tmp_ptr[0].value;
						hitx = tmp_ptr[1].value;
						hity = tmp_ptr[2].value;
						hitz = tmp_ptr[3].value;
						ContinueHitscan(pp, hitsect, hitx, hity, hitz, ndaang, xvect, yvect, zvect);
						continue;
					}
				} else if (klabs(hitz - sector[hitsect].floorz) <= Z(1)) {
					if (DTEST(sector[hitsect].extra, SECTFX_LIQUID_MASK) != SECTFX_LIQUID_NONE) {
						SpawnSplashXY(hitx, hity, hitz, hitsect);

						if (SectorIsDiveArea(hitsect)) {
							WarpToUnderwater(tmp_ptr[0].set(hitsect), tmp_ptr[1].set(hitx), tmp_ptr[2].set(hity),
									tmp_ptr[3].set(hitz));
							hitsect = (short) tmp_ptr[0].value;
							hitx = tmp_ptr[1].value;
							hity = tmp_ptr[2].value;
							hitz = tmp_ptr[3].value;
							ContinueHitscan(pp, hitsect, hitx, hity, hitz, ndaang, xvect, yvect, zvect);
						}

						continue;
					}
				}
			}

			if (hitwall >= 0) {
				if (wall[hitwall].nextsector >= 0) {
					if (TEST(sector[wall[hitwall].nextsector].ceilingstat, CEILING_STAT_PLAX)) {
						if (hitz < sector[wall[hitwall].nextsector].ceilingz) {
							continue;
						}
					}
				}

				if (wall[hitwall].lotag == TAG_WALL_BREAK) {
					HitBreakWall(hitwall, hitx, hity, hitz, ndaang, u.ID);
					continue;
				}

				QueueHole(ndaang, hitsect, hitwall, hitx, hity, hitz);
			}

			// hit a sprite?
			if (hitsprite >= 0) {
				hsp = sprite[hitsprite];
				USER hu = pUser[hitsprite];

				if (hu != null && hu.ID == TRASHCAN) {
					PlaySound(DIGI_TRASHLID, sp, v3df_none);
					if (hu.WaitTics <= 0) {
						hu.WaitTics = (short) SEC(2);
						ChangeState(hitsprite, s_TrashCanPain[0]);
					}
				}

				if (hsp.lotag == TAG_SPRITE_HIT_MATCH) {
					if (MissileHitMatch(-1, WPN_SHOTGUN, hitsprite))
						continue;
				}

				if (TEST(hsp.extra, SPRX_BREAKABLE)) {
					HitBreakSprite(hitsprite, 0);
					continue;
				}

				if (BulletHitSprite(pp.PlayerSprite, hitsprite, hitsect, hitwall, hitx, hity, hitz, SHOTGUN_SMOKE))
					continue;

				// hit a switch?
				if (TEST(hsp.cstat, CSTAT_SPRITE_WALL) && (hsp.lotag != 0 || hsp.hitag != 0)) {
					ShootableSwitch(hitsprite, -1);
				}
			}

			j = (short) SpawnShotgunSparks(pp, hitsect, hitwall, hitx, hity, hitz, ndaang);
			DoHitscanDamage(j, hitsprite);
		}

		DoPlayerBeginRecoil(pp, SHOTGUN_RECOIL_AMT);
		return (0);
	}

	public static int InitLaser(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		USER wu;
		SPRITE wp;
		int nx, ny, nz;
		short w;
		short oclipdist;

		DoPlayerBeginRecoil(pp, RAIL_RECOIL_AMT);

		PlayerUpdateAmmo(pp, u.WeaponNum, -1);

		PlaySound(DIGI_RIOTFIRE, pp, v3df_dontpan | v3df_doppler);

		nx = pp.posx;
		ny = pp.posy;

		nz = pp.posz + pp.bob_z + Z(8);

		// Spawn a shot
		// Inserting and setting up variables

		w = (short) SpawnSprite(STAT_MISSILE, BOLT_THINMAN_R0, s_Laser[0], pp.cursectnum, nx, ny, nz, pp.getAnglei(), 300);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		wp.hitag = LUMINOUS; // Always full brightness
		SetOwner(pp.PlayerSprite, w);
		wp.yrepeat = 52;
		wp.xrepeat = 52;
		wp.shade = -15;
		wp.clipdist = 64 >> 2;

		// the slower the missile travels the less of a zvel it needs
		wp.zvel = (short) ((100 - pp.getHorizi()) * HORIZ_MULT);
		wp.zvel /= 4;

		wu.WeaponNum = u.WeaponNum;
		wu.Radius = 200;
		wu.ceiling_dist = (short) Z(1);
		wu.floor_dist = (short) Z(1);
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.cstat |= (CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		// at certain angles the clipping box was big enough to block the
		// initial positioning of the fireball.

		SPRITE psp = pp.getSprite();

		oclipdist = (short) psp.clipdist;
		psp.clipdist = 0;

		wp.ang = NORM_ANGLE(wp.ang + 512);
		HelpMissileLateral(w, 900);
		wp.ang = NORM_ANGLE(wp.ang - 512);

		if (TEST(pp.Flags, PF_DIVING) || SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		// the slower the missile travels the less of a zvel it needs
		// move it 1200 dist in increments - works better
		if (MissileSetPos(w, DoLaserStart, 300)) {
			psp.clipdist = oclipdist;
			KillSprite(w);
			return (0);
		}
		if (MissileSetPos(w, DoLaserStart, 300)) {
			psp.clipdist = oclipdist;
			KillSprite(w);
			return (0);
		}
		if (MissileSetPos(w, DoLaserStart, 300)) {
			psp.clipdist = oclipdist;
			KillSprite(w);
			return (0);
		}
		if (MissileSetPos(w, DoLaserStart, 300)) {
			psp.clipdist = oclipdist;
			KillSprite(w);
			return (0);
		}

		psp.clipdist = oclipdist;

		if (WeaponAutoAim(pp.PlayerSprite, w, 32, 0) == -1) {
			wp.ang = NORM_ANGLE(wp.ang - 5);
		}

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		return (0);
	}

	public static int InitRail(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		USER wu;
		SPRITE wp;
		int nx, ny, nz;
		short w;
		short oclipdist;
		int zvel;

		DoPlayerBeginRecoil(pp, RAIL_RECOIL_AMT);

		PlayerUpdateAmmo(pp, u.WeaponNum, -1);

		PlaySound(DIGI_RAILFIRE, pp, v3df_dontpan | v3df_doppler);

		// Make sprite shade brighter
		u.Vis = 128;

		nx = pp.posx;
		ny = pp.posy;

		nz = pp.posz + pp.bob_z + Z(11);

		// Spawn a shot
		// Inserting and setting up variables

		w = (short) SpawnSprite(STAT_MISSILE, BOLT_THINMAN_R1, s_Rail[0][0], pp.cursectnum, nx, ny, nz, pp.getAnglei(), 1200);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		SetOwner(pp.PlayerSprite, w);
		wp.yrepeat = 52;
		wp.xrepeat = 52;
		wp.shade = -15;
		zvel = ((100 - pp.getHorizi()) * (HORIZ_MULT + 17));

		wu.RotNum = 5;
		NewStateGroup(w, WeaponStateGroup.sg_Rail);

		wu.WeaponNum = u.WeaponNum;
		wu.Radius = RAIL_RADIUS;
		wu.ceiling_dist = (short) Z(1);
		wu.floor_dist = (short) Z(1);
		wp.cstat |= (CSTAT_SPRITE_YCENTER | CSTAT_SPRITE_INVISIBLE);
		wp.cstat |= (CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		// at certain angles the clipping box was big enough to block the
		// initial positioning
		SPRITE psp = pp.getSprite();

		oclipdist = (short) psp.clipdist;
		psp.clipdist = 0;
		wp.clipdist = 32 >> 2;

		wp.ang = NORM_ANGLE(wp.ang + 512);
		HelpMissileLateral(w, 700);
		wp.ang = NORM_ANGLE(wp.ang - 512);

		if (TEST(pp.Flags, PF_DIVING) || SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		if (TestMissileSetPos(w, DoRailStart, 1200, zvel)) {
			psp.clipdist = oclipdist;
			KillSprite(w);
			return (0);
		}

		psp.clipdist = oclipdist;

		wp.zvel = (short) (zvel >> 1);
		if (WeaponAutoAim(pp.PlayerSprite, w, 32, 0) == -1) {
			wp.ang = NORM_ANGLE(wp.ang - 4);
		} else
			zvel = wp.zvel; // Let autoaiming set zvel now

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = zvel;

		return (0);
	}

	public static int InitZillaRail(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		USER wu;
		SPRITE wp;
		int nx, ny, nz;
		short w;
		short oclipdist;
		int zvel;

		PlaySound(DIGI_RAILFIRE, sp, v3df_dontpan | v3df_doppler);

		// Make sprite shade brighter
		u.Vis = 128;

		nx = sp.x;
		ny = sp.y;

		nz = SPRITEp_TOS(sp);

		// Spawn a shot
		// Inserting and setting up variables

		w = (short) SpawnSprite(STAT_MISSILE, BOLT_THINMAN_R1, s_Rail[0][0], sp.sectnum, nx, ny, nz, sp.ang, 1200);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		SetOwner(SpriteNum, w);
		wp.yrepeat = 52;
		wp.xrepeat = 52;
		wp.shade = -15;
		zvel = (100 * (HORIZ_MULT + 17));

		wu.RotNum = 5;
		NewStateGroup(w, WeaponStateGroup.sg_Rail);

		wu.WeaponNum = u.WeaponNum;
		wu.Radius = RAIL_RADIUS;
		wu.ceiling_dist = (short) Z(1);
		wu.floor_dist = (short) Z(1);
		wp.cstat |= (CSTAT_SPRITE_YCENTER | CSTAT_SPRITE_INVISIBLE);
		wp.cstat |= (CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		// at certain angles the clipping box was big enough to block the
		// initial positioning
		oclipdist = (short) sp.clipdist;
		sp.clipdist = 0;
		wp.clipdist = 32 >> 2;

		wp.ang = NORM_ANGLE(wp.ang + 512);
		HelpMissileLateral(w, 700);
		wp.ang = NORM_ANGLE(wp.ang - 512);

		if (SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		if (TestMissileSetPos(w, DoRailStart, 1200, zvel)) {
			sp.clipdist = oclipdist;
			KillSprite(w);
			return (0);
		}

		sp.clipdist = oclipdist;

		wp.zvel = (short) (zvel >> 1);
		if (WeaponAutoAim(SpriteNum, w, 32, 0) == -1) {
			wp.ang = NORM_ANGLE(wp.ang - 4);
		} else
			zvel = wp.zvel; // Let autoaiming set zvel now

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = zvel;

		return (0);
	}

	public static int InitRocket(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		USER wu;
		SPRITE wp;
		int nx, ny, nz;
		short w;
		short oclipdist;
		int zvel;

		DoPlayerBeginRecoil(pp, ROCKET_RECOIL_AMT);

		PlayerUpdateAmmo(pp, u.WeaponNum, -1);

		PlaySound(DIGI_RIOTFIRE, pp, v3df_dontpan | v3df_doppler);

		// Make sprite shade brighter
		u.Vis = 128;

		nx = pp.posx;
		ny = pp.posy;

		// Spawn a shot
		// Inserting and setting up variables
		nz = pp.posz + pp.bob_z + Z(8);
		w = (short) SpawnSprite(STAT_MISSILE, BOLT_THINMAN_R0, s_Rocket[0][0], pp.cursectnum, nx, ny, nz, pp.getAnglei(),
				ROCKET_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		// wp.owner = pp.PlayerSprite;
		SetOwner(pp.PlayerSprite, w);
		wp.yrepeat = 90;
		wp.xrepeat = 90;
		wp.shade = -15;
		zvel = ((100 - pp.getHorizi()) * (HORIZ_MULT + 35));

		wp.clipdist = 64 >> 2;

		wu.RotNum = 5;
		NewStateGroup(w, WeaponStateGroup.sg_Rocket);

		wu.WeaponNum = u.WeaponNum;
		wu.Radius = 2000;
		wu.ceiling_dist = (short) Z(3);
		wu.floor_dist = (short) Z(3);
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.cstat |= (CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		// Set default palette
		wp.pal = wu.spal = 17; // White

		if (pp.WpnRocketHeat != 0) {
			switch (pp.WpnRocketType) {
			case 1:
				pp.WpnRocketHeat--;
				wu.Flags |= (SPR_FIND_PLAYER);
				wp.pal = wu.spal = 20; // Yellow
				break;
			}
		}

		// at certain angles the clipping box was big enough to block the
		// initial positioning of the fireball.
		SPRITE psp = pp.getSprite();

		oclipdist = (short) psp.clipdist;
		psp.clipdist = 0;

		wp.ang = NORM_ANGLE(wp.ang + 512);
		HelpMissileLateral(w, 900);
		wp.ang = NORM_ANGLE(wp.ang - 512);

		if (TEST(pp.Flags, PF_DIVING) || SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		// cancel smoke trail
		wu.Counter = 1;
		if (TestMissileSetPos(w, DoRocket, 1200, zvel)) {
			psp.clipdist = oclipdist;
			KillSprite(w);
			return (0);
		}
		// inable smoke trail
		wu.Counter = 0;

		psp.clipdist = oclipdist;

		wp.zvel = (short) (zvel >> 1);
		if (WeaponAutoAim(pp.PlayerSprite, w, 32, 0) == -1) {
			wp.ang = NORM_ANGLE(wp.ang - 5);
		} else
			zvel = wp.zvel; // Let autoaiming set zvel now

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = zvel;

		return (0);
	}

	public static int InitBunnyRocket(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		USER wu;
		SPRITE wp;
		int nx, ny, nz;
		short w;
		short oclipdist;
		int zvel;

		DoPlayerBeginRecoil(pp, ROCKET_RECOIL_AMT);

		PlayerUpdateAmmo(pp, u.WeaponNum, -1);

		PlaySound(DIGI_BUNNYATTACK, pp, v3df_dontpan | v3df_doppler);

		nx = pp.posx;
		ny = pp.posy;

		// Spawn a shot
		// Inserting and setting up variables

		nz = pp.posz + pp.bob_z + Z(8);
		w = (short) SpawnSprite(STAT_MISSILE, BOLT_THINMAN_R4, s_BunnyRocket[0][0], pp.cursectnum, nx, ny, nz, pp.getAnglei(),
				ROCKET_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		// wp.owner = pp.PlayerSprite;
		SetOwner(pp.PlayerSprite, w);
		wp.yrepeat = 64;
		wp.xrepeat = 64;
		wp.shade = -15;
		zvel = ((100 - pp.getHorizi()) * (HORIZ_MULT + 35));

		wp.clipdist = 64 >> 2;

		wu.RotNum = 5;
		NewStateGroup(w, WeaponStateGroup.sg_BunnyRocket);

		wu.WeaponNum = u.WeaponNum;
		wu.Radius = 2000;
		wu.ceiling_dist = (short) Z(3);
		wu.floor_dist = (short) Z(3);
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.cstat |= (CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		wu.Flags |= (SPR_XFLIP_TOGGLE);

		if (pp.WpnRocketHeat != 0) {
			switch (pp.WpnRocketType) {
			case 1:
				pp.WpnRocketHeat--;
				wu.Flags |= (SPR_FIND_PLAYER);
			}
		}

		// at certain angles the clipping box was big enough to block the
		// initial positioning of the fireball.
		SPRITE psp = pp.getSprite();
		oclipdist = (short) psp.clipdist;
		psp.clipdist = 0;

		wp.ang = NORM_ANGLE(wp.ang + 512);
		HelpMissileLateral(w, 900);
		wp.ang = NORM_ANGLE(wp.ang - 512);

		if (TEST(pp.Flags, PF_DIVING) || SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		// cancel smoke trail
		wu.Counter = 1;
		if (TestMissileSetPos(w, DoRocket, 1200, zvel)) {
			psp.clipdist = oclipdist;
			KillSprite(w);
			return (0);
		}
		// inable smoke trail
		wu.Counter = 0;

		psp.clipdist = oclipdist;

		wp.zvel = (short) (zvel >> 1);
		if (WeaponAutoAim(pp.PlayerSprite, w, 32, 0) == -1) {
			wp.ang = NORM_ANGLE(wp.ang - 5);
		} else
			zvel = wp.zvel; // Let autoaiming set zvel now

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = zvel;
		wu.spal = (byte) (wp.pal = PALETTE_PLAYER1);

		return (0);
	}

	public static int InitNuke(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		USER wu;
		SPRITE wp;
		int nx, ny, nz;
		short w;
		short oclipdist;
		int zvel;

		if (pp.WpnRocketNuke > 0)
			pp.WpnRocketNuke = 0; // Bye Bye little nukie.
		else
			return (0);

		DoPlayerBeginRecoil(pp, ROCKET_RECOIL_AMT * 12);

		PlaySound(DIGI_RIOTFIRE, pp, v3df_dontpan | v3df_doppler);

		// Make sprite shade brighter
		u.Vis = 128;

		nx = pp.posx;
		ny = pp.posy;

		// Spawn a shot
		// Inserting and setting up variables
		// nz = pp.posz + pp.bob_z + Z(12);
		nz = pp.posz + pp.bob_z + Z(8);
		w = (short) SpawnSprite(STAT_MISSILE, BOLT_THINMAN_R0, s_Rocket[0][0], pp.cursectnum, nx, ny, nz, pp.getAnglei(), 700);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		// wp.owner = pp.PlayerSprite;
		SetOwner(pp.PlayerSprite, w);
		wp.yrepeat = 128;
		wp.xrepeat = 128;
		wp.shade = -15;
		zvel = ((100 - pp.getHorizi()) * (HORIZ_MULT - 36));
		wp.clipdist = 64 >> 2;

		// Set to red palette
		wp.pal = wu.spal = 19;

		wu.RotNum = 5;
		NewStateGroup(w, WeaponStateGroup.sg_Rocket);

		wu.WeaponNum = u.WeaponNum;
		wu.Radius = NUKE_RADIUS;
		wu.ceiling_dist = (short) Z(3);
		wu.floor_dist = (short) Z(3);
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.cstat |= (CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		// at certain angles the clipping box was big enough to block the
		// initial positioning of the fireball.
		SPRITE psp = pp.getSprite();
		oclipdist = (short) psp.clipdist;
		psp.clipdist = 0;

		wp.ang = NORM_ANGLE(wp.ang + 512);
		HelpMissileLateral(w, 900);
		wp.ang = NORM_ANGLE(wp.ang - 512);

		if (TEST(pp.Flags, PF_DIVING) || SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		// cancel smoke trail
		wu.Counter = 1;
		if (TestMissileSetPos(w, DoRocket, 1200, zvel)) {
			psp.clipdist = oclipdist;
			KillSprite(w);
			return (0);
		}
		// inable smoke trail
		wu.Counter = 0;

		psp.clipdist = oclipdist;

		wp.zvel = (short) (zvel >> 1);
		if (WeaponAutoAim(pp.PlayerSprite, w, 32, 0) == -1) {
			wp.ang = NORM_ANGLE(wp.ang - 5);
		} else
			zvel = wp.zvel; // Let autoaiming set zvel now

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = zvel;

		PlayerDamageSlide(pp, -40, NORM_ANGLE(pp.getAnglei() + 1024)); // Recoil slide

		return (0);
	}

	public static int InitEnemyNuke(short SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		USER wu;
		SPRITE wp;
		int nx, ny, nz;
		short w;
		int zvel;

		PlaySound(DIGI_RIOTFIRE, sp, v3df_dontpan | v3df_doppler);

		// Make sprite shade brighter
		u.Vis = 128;

		nx = sp.x;
		ny = sp.y;

		// Spawn a shot
		// Inserting and setting up variables
		nz = sp.z + Z(40);
		w = (short) SpawnSprite(STAT_MISSILE, BOLT_THINMAN_R0, s_Rocket[0][0], sp.sectnum, nx, ny, nz, sp.ang, 700);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		if (u.ID == ZOMBIE_RUN_R0)
			SetOwner(sp.owner, w);
		else
			SetOwner(SpriteNum, w);

		wp.yrepeat = 128;
		wp.xrepeat = 128;
		wp.shade = -15;
		zvel = (100 * (HORIZ_MULT - 36));
		wp.clipdist = 64 >> 2;

		// Set to red palette
		wp.pal = wu.spal = 19;

		wu.RotNum = 5;
		NewStateGroup(w, WeaponStateGroup.sg_Rocket);

		wu.WeaponNum = u.WeaponNum;
		wu.Radius = NUKE_RADIUS;
		wu.ceiling_dist = (short) Z(3);
		wu.floor_dist = (short) Z(3);
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.cstat |= (CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		wp.ang = NORM_ANGLE(wp.ang + 512);
		HelpMissileLateral(w, 500);
		wp.ang = NORM_ANGLE(wp.ang - 512);

		if (SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		// cancel smoke trail
		wu.Counter = 1;
		if (TestMissileSetPos(w, DoRocket, 1200, zvel)) {
			KillSprite(w);
			return (0);
		}

		// enable smoke trail
		wu.Counter = 0;

		wp.zvel = (short) (zvel >> 1);
		if (WeaponAutoAim(SpriteNum, w, 32, 0) == -1) {
			wp.ang = NORM_ANGLE(wp.ang - 5);
		} else
			zvel = wp.zvel; // Let autoaiming set zvel now

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = zvel;

		return (0);
	}

	public static final int MAX_MICRO = 1;
	public static final int MICRO_LATERAL = 5000;
	public static final int MICRO_ANG = 400;

	public static int InitMicro(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		USER wu, hu;
		SPRITE wp, hp;
		int nx, ny, nz, dist;
		short w;
		short oclipdist;
		short i, ang;

        nx = pp.posx;
		ny = pp.posy;

		DoPickTarget(pp.PlayerSprite, 256, 0);

		if (TargetSortCount > MAX_MICRO)
			TargetSortCount = MAX_MICRO;

		int tsi = 0;

		for (i = 0; i < MAX_MICRO; i++) {
			Target_Sort ts = TargetSort[tsi];
			if (tsi < TargetSortCount && ts.sprite_num >= 0) {
				hp = sprite[ts.sprite_num];
				hu = pUser[ts.sprite_num];

				ang = engine.getangle(hp.x - nx, hp.y - ny);
				tsi++;
			} else {
				hp = null;
				hu = null;
				ang = pp.getAnglei();
			}

			nz = pp.posz + pp.bob_z + Z(14);
			nz += Z(RANDOM_RANGE(20)) - Z(10);

			// Spawn a shot
			// Inserting and setting up variables

			w = (short) SpawnSprite(STAT_MISSILE, BOLT_THINMAN_R0, s_Micro[0][0], pp.cursectnum, nx, ny, nz, ang, 1200);
			if(w == -1)
				return 0;

			wp = sprite[w];
			wu = pUser[w];

			SetOwner(pp.PlayerSprite, w);
			wp.yrepeat = 24;
			wp.xrepeat = 24;
			wp.shade = -15;
			wp.zvel = (short) ((100 - pp.getHorizi()) * HORIZ_MULT);
			wp.clipdist = 64 >> 2;

			// randomize zvelocity
			wp.zvel += RANDOM_RANGE(Z(8)) - Z(5);

			wu.RotNum = 5;
			NewStateGroup(w, WeaponStateGroup.sg_Micro);

			wu.WeaponNum = u.WeaponNum;
			wu.Radius = 200;
			wu.ceiling_dist = (short) Z(2);
			wu.floor_dist = (short) Z(2);
			wp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
			wp.cstat |= (CSTAT_SPRITE_YCENTER);
			wp.cstat |= (CSTAT_SPRITE_INVISIBLE);

			wu.WaitTics = (short) (10 + RANDOM_RANGE(40));

			// at certain angles the clipping box was big enough to block the
			// initial positioning of the fireball.
			SPRITE psp = pp.getSprite();
			oclipdist = (short) psp.clipdist;
			psp.clipdist = 0;

			wp.ang = NORM_ANGLE(wp.ang + 512);

			HelpMissileLateral(w, 1000 + (RANDOM_RANGE(MICRO_LATERAL) - DIV2(MICRO_LATERAL)));
			wp.ang = NORM_ANGLE(wp.ang - 512);

			if (TEST(pp.Flags, PF_DIVING) || SpriteInUnderwaterArea(wp))
				wu.Flags |= (SPR_UNDERWATER);

			// cancel smoke trail
			wu.Counter = 1;
			if (MissileSetPos(w, DoMicro, 700)) {
				psp.clipdist = oclipdist;
				KillSprite(w);
				continue;
			}
			// inable smoke trail
			wu.Counter = 0;

			psp.clipdist = oclipdist;

			if (hp != null) {
				dist = Distance(wp.x, wp.y, hp.x, hp.y);
				if (dist != 0) {
					long zh;
					zh = SPRITEp_TOS(hp) + DIV4(SPRITEp_SIZE_Z(hp));
					wp.zvel = (short) ((wp.xvel * (zh - wp.z)) / dist);
				}

				wu.WpnGoal = (short) ts.sprite_num;
				hu.Flags |= (SPR_TARGETED);
				hu.Flags |= (SPR_ATTACKED);
			} else {
				wp.ang = NORM_ANGLE(wp.ang + (RANDOM_RANGE(MICRO_ANG) - DIV2(MICRO_ANG)) - 16);
			}

			wu.xchange = MOVEx(wp.xvel, wp.ang);
			wu.ychange = MOVEy(wp.xvel, wp.ang);
			wu.zchange = wp.zvel;
		}

		return (0);
	}

	public static int InitRipperSlash(int SpriteNum) {
		USER u = pUser[SpriteNum], hu;
		SPRITE sp = pUser[SpriteNum].getSprite();
		SPRITE hp;
		short i, nexti, stat;

		PlaySound(DIGI_RIPPER2ATTACK, sp, v3df_none);

		for (stat = 0; stat < StatDamageList.length; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				hp = sprite[i];
				hu = pUser[i];

				if (i == SpriteNum)
					break;

				if (FindDistance3D(sp.x - hp.x, sp.y - hp.y, (sp.z - hp.z) >> 4) > hu.Radius + u.Radius)
					continue;

				if (DISTANCE(hp.x, hp.y, sp.x, sp.y) < CLOSE_RANGE_DIST_FUDGE(sp, hp, 600)
						&& FACING_RANGE(hp, sp, 150)) {
					DoDamage(i, SpriteNum);
				}
			}
		}

		return (0);
	}

	public static int InitBunnySlash(int SpriteNum) {
		SPRITE sp = pUser[SpriteNum].getSprite();
		SPRITE hp;
		short i, nexti, stat;
		int dist;

		PlaySound(DIGI_BUNNYATTACK, sp, v3df_none);

		for (stat = 0; stat < StatDamageList.length; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				hp = sprite[i];

				if (i == SpriteNum)
					break;

				dist = DISTANCE(hp.x, hp.y, sp.x, sp.y);

				if (dist < CLOSE_RANGE_DIST_FUDGE(sp, hp, 600) && FACING_RANGE(hp, sp, 150)) {
					DoDamage(i, SpriteNum);
				}
			}
		}

		return (0);
	}

	public static int InitSerpSlash(int SpriteNum) {
		SPRITE sp = pUser[SpriteNum].getSprite();
		SPRITE hp;
		short i, nexti, stat;
		int dist;

		PlaySound(DIGI_SERPSWORDATTACK, sp, v3df_none);

		for (stat = 0; stat < StatDamageList.length; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				hp = sprite[i];

				if (i == SpriteNum)
					break;

				dist = DISTANCE(hp.x, hp.y, sp.x, sp.y);

				if (dist < CLOSE_RANGE_DIST_FUDGE(sp, hp, 800) && FACING_RANGE(hp, sp, 150)) {
//	                PlaySound(PlayerPainVocs[RANDOM_RANGE(MAX_PAIN)], sp.x, sp.y, sp.z, v3df_none);
					DoDamage(i, SpriteNum);
				}
			}
		}

		return (0);
	}

	public static boolean WallSpriteInsideSprite(SPRITE wsp, SPRITE sp) {

		int x1, y1, x2, y2;
		int xoff;
		int dax, day;
		int xsiz, mid_dist;

		x1 = wsp.x;
		y1 = wsp.y;

		xoff = TILE_XOFF(wsp.picnum) + wsp.xoffset;

		if (TEST(wsp.cstat, CSTAT_SPRITE_XFLIP))
			xoff = -xoff;

		// x delta
		dax = sintable[wsp.ang] * wsp.xrepeat;
		// y delta
		day = sintable[NORM_ANGLE(wsp.ang + 1024 + 512)] * wsp.xrepeat;

		xsiz = engine.getTile(wsp.picnum).getWidth();
		mid_dist = DIV2(xsiz) + xoff;

		// starting from the center find the first point
		x1 -= mulscale(dax, mid_dist, 16);
		// starting from the first point find the end point
		x2 = x1 + mulscale(dax, xsiz, 16);

		y1 -= mulscale(day, mid_dist, 16);
		y2 = y1 + mulscale(day, xsiz, 16);

		return (engine.clipinsideboxline(sp.x, sp.y, x1, y1, x2, y2, (sp.clipdist) << 2) != 0);
	}

	public static int DoBladeDamage(int SpriteNum) {
		SPRITE sp = pUser[SpriteNum].getSprite();
		SPRITE hp;
		short i, nexti, stat;
		int dist;

		for (stat = 0; stat < StatDamageList.length; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				hp = sprite[i];

				if (i == SpriteNum)
					break;

				if (!TEST(hp.extra, SPRX_PLAYER_OR_ENEMY))
					continue;

				dist = DISTANCE(hp.x, hp.y, sp.x, sp.y);

				if (dist > 2000)
					continue;

				dist = FindDistance3D(sp.x - hp.x, sp.y - hp.y, (sp.z - hp.z) >> 4);

				if (dist > 2000)
					continue;

				if (WallSpriteInsideSprite(sp, hp)) {
					DoDamage(i, SpriteNum);
//	                    PlaySound(PlayerPainVocs[RANDOM_RANGE(MAX_PAIN)], sp.x, sp.y, sp.z, v3df_none);
				}
			}
		}

		return (0);
	}

	public static int DoStaticFlamesDamage(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();
		SPRITE hp;
		short i, nexti, stat;
		int dist;

		for (stat = 0; stat < StatDamageList.length; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				hp = sprite[i];

				if (i == SpriteNum)
					break;

				if (!TEST(hp.extra, SPRX_PLAYER_OR_ENEMY))
					continue;

				dist = DISTANCE(hp.x, hp.y, sp.x, sp.y);

				if (dist > 2000)
					continue;

				dist = FindDistance3D(sp.x - hp.x, sp.y - hp.y, (sp.z - hp.z) >> 4);

				if (dist > 2000)
					continue;

				if (SpriteOverlap(SpriteNum, i)) // If sprites are overlapping, cansee will fail!
					DoDamage(i, SpriteNum);
				else if (u.Radius > 200) {
					if (FAFcansee(sp.x, sp.y, SPRITEp_MID(sp), sp.sectnum, hp.x, hp.y, SPRITEp_MID(hp), hp.sectnum))
						DoDamage(i, SpriteNum);
				}
			}
		}

		return (0);
	}

	public static int InitCoolgBash(int SpriteNum) {

		SPRITE sp = pUser[SpriteNum].getSprite();
		SPRITE hp;
		short i, nexti, stat;
		long dist;

		PlaySound(DIGI_CGTHIGHBONE, sp, v3df_none);

		for (stat = 0; stat < StatDamageList.length; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];

				hp = sprite[i];

				if (i == SpriteNum)
					break;

				// don't set off mine
				if (!TEST(hp.extra, SPRX_PLAYER_OR_ENEMY))
					continue;

				dist = DISTANCE(hp.x, hp.y, sp.x, sp.y);

				if (dist < CLOSE_RANGE_DIST_FUDGE(sp, hp, 600) && FACING_RANGE(hp, sp, 150)) {
//	                PlaySound(PlayerPainVocs[RANDOM_RANGE(MAX_PAIN)], sp.x, sp.y, sp.z, v3df_none);
					DoDamage(i, SpriteNum);
				}
			}
		}

		return (0);
	}

	public static int InitSkelSlash(int SpriteNum) {
		SPRITE sp = pUser[SpriteNum].getSprite();
		SPRITE hp;
		short i, nexti, stat;
		int dist;

		PlaySound(DIGI_SPBLADE, sp, v3df_none);

		for (stat = 0; stat < StatDamageList.length; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];

				hp = sprite[i];

				if (i == SpriteNum)
					break;

				dist = DISTANCE(hp.x, hp.y, sp.x, sp.y);

				if (dist < CLOSE_RANGE_DIST_FUDGE(sp, hp, 600) && FACING_RANGE(hp, sp, 150)) {
//	                PlaySound(PlayerPainVocs[RANDOM_RANGE(MAX_PAIN)], sp.x, sp.y, sp.z, v3df_none);
					DoDamage(i, SpriteNum);
				}
			}
		}

		return (0);
	}

	public static int InitGoroChop(int SpriteNum) {
		SPRITE sp = pUser[SpriteNum].getSprite();
		SPRITE hp;
		short i, nexti, stat;
		int dist;

		PlaySound(DIGI_GRDSWINGAXE, sp, v3df_none);

		for (stat = 0; stat < StatDamageList.length; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];

				hp = sprite[i];

				if (i == SpriteNum)
					break;

				dist = DISTANCE(hp.x, hp.y, sp.x, sp.y);

				if (dist < CLOSE_RANGE_DIST_FUDGE(sp, hp, 700) && FACING_RANGE(hp, sp, 150)) {
					PlaySound(DIGI_GRDAXEHIT, sp, v3df_none);
					DoDamage(i, SpriteNum);
				}
			}
		}

		return (0);
	}

	public static int InitHornetSting(int SpriteNum) {
		USER u = pUser[SpriteNum];
		short HitSprite = NORM_SPRITE(u.ret);

		DoDamage(HitSprite, SpriteNum);
		InitActorReposition.invoke(SpriteNum);

		return (0);
	}

	private static final short lat_ang[] = { 512, -512 };

	private static final short delta_ang[] = { -10, 10 };

	public static int InitSerpSpell(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], np;
		USER u = pUser[SpriteNum], nu;
		int dist;
		short newsp, i;
		short oclipdist;

		for (i = 0; i < 2; i++) {
			sp.ang = engine.getangle(sprite[u.tgt_sp].x - sp.x, sprite[u.tgt_sp].y - sp.y);

			newsp = (short) SpawnSprite(STAT_MISSILE, SERP_METEOR, WeaponStateGroup.sg_SerpMeteor.getState(0),
					sp.sectnum, sp.x, sp.y, sp.z, sp.ang, 1500);
			if(newsp == -1)
				return 0;

			np = sprite[newsp];
			nu = pUser[newsp];

			np.z = SPRITEp_TOS(sp);

			nu.RotNum = 5;
			NewStateGroup(newsp, WeaponStateGroup.sg_SerpMeteor);
			nu.StateEnd = s_MirvMeteorExp[0];

			// np.owner = SpriteNum;
			SetOwner(SpriteNum, newsp);
			np.shade = -40;
			PlaySound(DIGI_SERPMAGICLAUNCH, sp, v3df_none);
			nu.spal = (byte) (np.pal = 27); // Bright Green
			np.xrepeat = 64;
			np.yrepeat = 64;
			np.clipdist = 32 >> 2;
			np.zvel = 0;
			np.cstat |= (CSTAT_SPRITE_YCENTER);

			nu.ceiling_dist = (short) Z(16);
			nu.floor_dist = (short) Z(16);
			nu.Dist = 200;

			oclipdist = (short) sp.clipdist;
			sp.clipdist = 1;

			np.ang = NORM_ANGLE(np.ang + lat_ang[i]);
			HelpMissileLateral(newsp, 4200);
			np.ang = NORM_ANGLE(np.ang - lat_ang[i]);

			// find the distance to the target (player)
			dist = Distance(np.x, np.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);
			if (dist != 0)
				np.zvel = (short) ((np.xvel * (SPRITEp_UPPER(sprite[u.tgt_sp]) - np.z)) / dist);

			np.ang = NORM_ANGLE(np.ang + delta_ang[i]);

			nu.xchange = MOVEx(np.xvel, np.ang);
			nu.ychange = MOVEy(np.xvel, np.ang);
			nu.zchange = np.zvel;

			MissileSetPos(newsp, DoMirvMissile, 400);
			sp.clipdist = oclipdist;

			if (TEST(u.Flags, SPR_UNDERWATER))
				nu.Flags |= (SPR_UNDERWATER);
		}

		return (0);
	}

	public static int SpawnDemonFist(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		SPRITE exp;
		USER eu;
		short explosion;

		if (TEST(u.Flags, SPR_SUICIDE))
			return (-1);

		// PlaySound(DIGI_ITEM_SPAWN, sp, v3df_none);
		explosion = (short) SpawnSprite(STAT_MISSILE, 0, s_TeleportEffect[0], sp.sectnum, sp.x, sp.y, SPRITEp_MID(sp),
				sp.ang, 0);
		if(explosion == -1)
			return 0;

		exp = sprite[explosion];
		eu = pUser[explosion];

		exp.hitag = LUMINOUS; // Always full brightness
		exp.owner = -1;
		exp.shade = -40;
		exp.xrepeat = 32;
		exp.yrepeat = 32;
		eu.spal = (byte) (exp.pal = 25);

		exp.cstat |= (CSTAT_SPRITE_YCENTER);
		exp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		eu.Radius = DamageData[DMG_BASIC_EXP].radius;

		if (RANDOM_P2(1024 << 8) >> 8 > 600)
			exp.cstat |= (CSTAT_SPRITE_XFLIP);
		if (RANDOM_P2(1024 << 8) >> 8 > 600)
			exp.cstat |= (CSTAT_SPRITE_YFLIP);

		return (explosion);
	}

	public static int InitSerpMonstSpell(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], np;
		USER u = pUser[SpriteNum], nu;
		int dist;
		short newsp, i;
		short oclipdist;

		PlaySound(DIGI_MISSLFIRE, sp, v3df_none);

		for (i = 0; i < 1; i++) {
			sp.ang = engine.getangle(sprite[u.tgt_sp].x - sp.x, sprite[u.tgt_sp].y - sp.y);

			newsp = (short) SpawnSprite(STAT_MISSILE, SERP_METEOR, WeaponStateGroup.sg_SerpMeteor.getState(0),
					sp.sectnum, sp.x, sp.y, sp.z, sp.ang, 500);
			if(newsp == -1)
				return 0;

			np = sprite[newsp];
			nu = pUser[newsp];

			nu.spal = (byte) (np.pal = 25); // Bright Red
			np.z = SPRITEp_TOS(sp);

			nu.RotNum = 5;
			NewStateGroup(newsp, WeaponStateGroup.sg_SerpMeteor);
			nu.StateEnd = s_TeleportEffect2[0];

			SetOwner(SpriteNum, newsp);
			np.shade = -40;
			np.xrepeat = 122;
			np.yrepeat = 116;
			np.clipdist = 32 >> 2;
			np.zvel = 0;
			np.cstat |= (CSTAT_SPRITE_YCENTER);

			nu.ceiling_dist = (short) Z(16);
			nu.floor_dist = (short) Z(16);

			nu.Dist = 200;

			oclipdist = (short) sp.clipdist;
			sp.clipdist = 1;

			np.ang = NORM_ANGLE(np.ang + lat_ang[i]);
			HelpMissileLateral(newsp, 4200);
			np.ang = NORM_ANGLE(np.ang - lat_ang[i]);

			// find the distance to the target (player)
			dist = Distance(np.x, np.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);
			if (dist != 0)
				np.zvel = (short) ((np.xvel * (SPRITEp_UPPER(sprite[u.tgt_sp]) - np.z)) / dist);

			np.ang = NORM_ANGLE(np.ang + delta_ang[i]);

			nu.xchange = MOVEx(np.xvel, np.ang);
			nu.ychange = MOVEy(np.xvel, np.ang);
			nu.zchange = np.zvel;

			MissileSetPos(newsp, DoMirvMissile, 400);
			sp.clipdist = oclipdist;

			if (TEST(u.Flags, SPR_UNDERWATER))
				nu.Flags |= (SPR_UNDERWATER);
		}

		return (0);
	}

	public static void DoTeleRipper(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];

		PlaySound(DIGI_ITEM_SPAWN, sp, v3df_none);
		Ripper2Hatch(SpriteNum);
	}

	public static int InitEnemyRocket(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], wp;
		USER u = pUser[SpriteNum], wu;
		int nx, ny, nz, dist, nang;
		short w;

		PlaySound(DIGI_NINJARIOTATTACK, sp, v3df_none);

		// get angle to player and also face player when attacking
		sp.ang = (short) (nang = engine.getangle(sprite[u.tgt_sp].x - sp.x, sprite[u.tgt_sp].y - sp.y));

		nx = sp.x;
		ny = sp.y;
		nz = sp.z - DIV2(SPRITEp_SIZE_Z(sp)) - Z(8);

		// Spawn a shot
		w = (short) SpawnSprite(STAT_MISSILE, BOLT_THINMAN_R2, s_Rocket[0][0], sp.sectnum, nx, ny, nz - Z(8),
				sprite[u.tgt_sp].ang, NINJA_BOLT_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		// Set default palette
		wp.pal = wu.spal = 17; // White

		if (u.ID == ZOMBIE_RUN_R0)
			SetOwner(sp.owner, w);
		else
			SetOwner(SpriteNum, w);
		wp.yrepeat = 28;
		wp.xrepeat = 28;
		wp.shade = -15;
		wp.zvel = 0;
		wp.ang = (short) nang;
		wp.clipdist = 64 >> 2;

		wu.RotNum = 5;
		NewStateGroup(w, WeaponStateGroup.sg_Rocket);
		wu.Radius = 200;
		wp.cstat |= (CSTAT_SPRITE_YCENTER);

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		if (u.spal == PAL_XLAT_LT_TAN) {
			wu.Flags |= (SPR_FIND_PLAYER);
			wp.pal = wu.spal = 20; // Yellow
		}

		MissileSetPos(w, DoBoltThinMan, 400);

		// find the distance to the target (player)
		dist = Distance(wp.x, wp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

		if (dist != 0)
			wu.zchange = wp.zvel = (short) ((wp.xvel * (SPRITEp_UPPER(sprite[u.tgt_sp]) - wp.z)) / dist);

		return (w);
	}

	public static int InitEnemyRail(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		USER wu;
		SPRITE wp;
		int nx, ny, nz, dist;
		short w;
		short pnum = 0;

		// if co-op don't hurt teammate
		if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COOPERATIVE && u.ID == ZOMBIE_RUN_R0) {
			PlayerStr pp;

			// Check all players
			for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
				pp = Player[pnum];

				if (u.tgt_sp == pp.PlayerSprite)
					return (0);
			}
		}

		PlaySound(DIGI_RAILFIRE, sp, v3df_dontpan | v3df_doppler);

		// get angle to player and also face player when attacking
		sp.ang = (engine.getangle(sprite[u.tgt_sp].x - sp.x, sprite[u.tgt_sp].y - sp.y));

		// add a bit of randomness
		if (RANDOM_P2(1024) < 512)
			sp.ang = NORM_ANGLE(sp.ang + RANDOM_P2(128) - 64);

		nx = sp.x;
		ny = sp.y;
		nz = sp.z - DIV2(SPRITEp_SIZE_Z(sp)) - Z(8);

		// Spawn a shot
		// Inserting and setting up variables

		w = (short) SpawnSprite(STAT_MISSILE, BOLT_THINMAN_R1, s_Rail[0][0], sp.sectnum, nx, ny, nz, sp.ang, 1200);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		if (u.ID == ZOMBIE_RUN_R0)
			SetOwner(sp.owner, w);
		else
			SetOwner(SpriteNum, w);

		wp.yrepeat = 52;
		wp.xrepeat = 52;
		wp.shade = -15;
		wp.zvel = 0;

		wu.RotNum = 5;
		NewStateGroup(w, WeaponStateGroup.sg_Rail);

		wu.Radius = 200;
		wu.ceiling_dist = (short) Z(1);
		wu.floor_dist = (short) Z(1);
		wu.Flags2 |= (SPR2_SO_MISSILE);
		wp.cstat |= (CSTAT_SPRITE_YCENTER | CSTAT_SPRITE_INVISIBLE);
		wp.cstat |= (CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		wp.clipdist = 64 >> 2;

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		if (TestMissileSetPos(w, DoRailStart, 600, wp.zvel)) {
			// sprite.clipdist = oclipdist;
			KillSprite(w);
			return (0);
		}

		// find the distance to the target (player)
		dist = Distance(wp.x, wp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

		if (dist != 0)
			wu.zchange = wp.zvel = (short) ((wp.xvel * (SPRITEp_UPPER(sprite[u.tgt_sp]) - wp.z)) / dist);

		return (w);

	}

	private static MISSILE_PLACEMENT mp2[] = { new MISSILE_PLACEMENT(600 * 6, 400, 512),
			new MISSILE_PLACEMENT(900 * 6, 400, 512), new MISSILE_PLACEMENT(1100 * 6, 400, 512),
			new MISSILE_PLACEMENT(600 * 6, 400, -512), new MISSILE_PLACEMENT(900 * 6, 400, -512),
			new MISSILE_PLACEMENT(1100 * 6, 400, -512), };

	public static int InitZillaRocket(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], wp;
		USER u = pUser[SpriteNum], wu;
		int nx, ny, nz, dist, nang;
		short w = -1, i;

		PlaySound(DIGI_NINJARIOTATTACK, sp, v3df_none);

		// get angle to player and also face player when attacking
		sp.ang = (short) (nang = engine.getangle(sprite[u.tgt_sp].x - sp.x, sprite[u.tgt_sp].y - sp.y));

		for (i = 0; i < mp2.length; i++) {
			nx = sp.x;
			ny = sp.y;
			nz = sp.z - DIV2(SPRITEp_SIZE_Z(sp)) - Z(8);

			// Spawn a shot
			w = (short) SpawnSprite(STAT_MISSILE, BOLT_THINMAN_R2, s_Rocket[0][0], sp.sectnum, nx, ny, nz - Z(8),
					sprite[u.tgt_sp].ang, NINJA_BOLT_VELOCITY);
			if(w == -1)
				return 0;

			wp = sprite[w];
			wu = pUser[w];

			SetOwner(SpriteNum, w);
			wp.yrepeat = 28;
			wp.xrepeat = 28;
			wp.shade = -15;
			wp.zvel = 0;
			wp.ang = (short) nang;
			wp.clipdist = 64 >> 2;

			wu.RotNum = 5;
			NewStateGroup(w, WeaponStateGroup.sg_Rocket);
			wu.Radius = 200;
			wp.cstat |= (CSTAT_SPRITE_YCENTER);

			wu.xchange = MOVEx(wp.xvel, wp.ang);
			wu.ychange = MOVEy(wp.xvel, wp.ang);
			wu.zchange = wp.zvel;

			// Zilla has seekers!
			if (i != 1 && i != 4)
				wp.pal = wu.spal = 17; // White
			else {
				wu.Flags |= (SPR_FIND_PLAYER);
				wp.pal = wu.spal = 20; // Yellow
			}

			if (mp2[i].dist_over != 0) {
				wp.ang = NORM_ANGLE(wp.ang + mp2[i].ang);
				HelpMissileLateral(w, mp2[i].dist_over);
				wp.ang = NORM_ANGLE(wp.ang - mp2[i].ang);
			}

			MissileSetPos(w, DoBoltThinMan, mp2[i].dist_out);

			// find the distance to the target (player)
			dist = Distance(wp.x, wp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

			if (dist != 0)
				wu.zchange = wp.zvel = (short) ((wp.xvel * (SPRITEp_UPPER(sprite[u.tgt_sp]) - wp.z)) / dist);
		}

		return (w);
	}

	public static int InitEnemyStar(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], wp;
		USER u = pUser[SpriteNum], wu;
		int nx, ny, nz, dist, nang;
		short w;

		// get angle to player and also face player when attacking
		sp.ang = (short) (nang = NORM_ANGLE(engine.getangle(sprite[u.tgt_sp].x - sp.x, sprite[u.tgt_sp].y - sp.y)));

		nx = sp.x;
		ny = sp.y;
		nz = SPRITEp_MID(sp);

		// Spawn a shot
		w = (short) SpawnSprite(STAT_MISSILE, STAR1, s_Star[0], sp.sectnum, nx, ny, nz,
				sprite[u.tgt_sp].ang, NINJA_STAR_VELOCITY);
		if(w == -1)
			return 0;
		wp = sprite[w];
		wu = pUser[w];

		// wp.owner = SpriteNum;
		SetOwner(SpriteNum, w);
		wp.yrepeat = 16;
		wp.xrepeat = 16;
		wp.shade = -25;
		wp.zvel = 0;
		wp.ang = (short) nang;
		wp.clipdist = 64 >> 2;

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		MissileSetPos(w, DoStar, 400);

		// find the distance to the target (player)
		dist = Distance(wp.x, wp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

		if (dist != 0)
			wu.zchange = wp.zvel = (short) ((wp.xvel * (SPRITEp_UPPER(sprite[u.tgt_sp]) - wp.z)) / dist);

		//
		// Star Power Up Code
		//

		PlaySound(DIGI_STAR, sp, v3df_none);

		return (w);
	}

	public static int InitEnemyCrossbow(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], wp;
		USER u = pUser[SpriteNum], wu;
		int nx, ny, nz, dist, nang;
		short w;

		// get angle to player and also face player when attacking
		sp.ang = (short) (nang = NORM_ANGLE(engine.getangle(sprite[u.tgt_sp].x - sp.x, sprite[u.tgt_sp].y - sp.y)));

		nx = sp.x;
		ny = sp.y;
		nz = SPRITEp_MID(sp) - Z(14);

		// Spawn a shot
		wp = sprite[w = (short) SpawnSprite(STAT_MISSILE, CROSSBOLT, s_CrossBolt[0][0], sp.sectnum, nx, ny, nz,
				sprite[u.tgt_sp].ang, 800)];

		wu = pUser[w];

		// wp.owner = SpriteNum;
		SetOwner(SpriteNum, w);
		wp.xrepeat = 16;
		wp.yrepeat = 26;
		wp.shade = -25;
		wp.zvel = 0;
		wp.ang = (short) nang;
		wp.clipdist = 64 >> 2;

		wu.RotNum = 5;
		NewStateGroup(w, WeaponStateGroup.sg_CrossBolt);

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		wu.Flags |= (SPR_XFLIP_TOGGLE);

		MissileSetPos(w, DoStar, 400);

		// find the distance to the target (player)
		dist = Distance(wp.x, wp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

		if (dist != 0)
			wu.zchange = wp.zvel = (short) ((wp.xvel * (SPRITEp_UPPER(sprite[u.tgt_sp]) - wp.z)) / dist);

		//
		// Star Power Up Code
		//

		PlaySound(DIGI_STAR, sp, v3df_none);

		return (w);
	}

	public static int InitSkelSpell(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], wp;
		USER u = pUser[SpriteNum], wu;
		int nx, ny, nz, dist, nang;
		short w;

		PlaySound(DIGI_SPELEC, sp, v3df_none);

		// get angle to player and also face player when attacking
		sp.ang = (short) (nang = NORM_ANGLE(engine.getangle(sprite[u.tgt_sp].x - sp.x, sprite[u.tgt_sp].y - sp.y)));

		nx = sp.x;
		ny = sp.y;
		nz = sp.z - DIV2(SPRITEp_SIZE_Z(sp));

		// Spawn a shot
		w = (short) SpawnSprite(STAT_MISSILE, ELECTRO_ENEMY, s_Electro[0], sp.sectnum, nx, ny, nz, sprite[u.tgt_sp].ang,
				SKEL_ELECTRO_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		// wp.owner = SpriteNum;
		SetOwner(SpriteNum, w);
		wp.xrepeat -= 20;
		wp.yrepeat -= 20;
		wp.shade = -40;
		wp.zvel = 0;
		wp.ang = (short) nang;
		wp.clipdist = 64 >> 2;
		wp.cstat |= (CSTAT_SPRITE_YCENTER);

		// find the distance to the target (player)
		dist = Distance(nx, ny, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

		if (dist != 0)
			wp.zvel = (short) ((wp.xvel * (SPRITEp_UPPER(sprite[u.tgt_sp]) - nz)) / dist);

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		MissileSetPos(w, DoElectro, 400);

		return (w);
	}

	public static int InitCoolgFire(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], wp;
		USER u = pUser[SpriteNum], wu;
		int nx, ny, nz, dist, nang;
		short w;

		// get angle to player and also face player when attacking
		sp.ang = (short) (nang = NORM_ANGLE(engine.getangle(sprite[u.tgt_sp].x - sp.x, sprite[u.tgt_sp].y - sp.y)));

		nx = sp.x;
		ny = sp.y;

		nz = sp.z - Z(16);

		// Spawn a shot
		// Inserting and setting up variables

		PlaySound(DIGI_CGMAGIC, sp, v3df_follow);

		w = (short) SpawnSprite(STAT_MISSILE, COOLG_FIRE, s_CoolgFire[0], sp.sectnum, nx, ny, nz, sprite[u.tgt_sp].ang,
				COOLG_FIRE_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		// wp.owner = SpriteNum;
		SetOwner(SpriteNum, w);
		wp.hitag = LUMINOUS;
		wp.yrepeat = 18;
		wp.xrepeat = 18;
		wp.shade = -40;
		wp.zvel = 0;
		wp.ang = (short) nang;
		wp.clipdist = 32 >> 2;
		wu.ceiling_dist = (short) Z(4);
		wu.floor_dist = (short) Z(4);
		if (u.ID == RIPPER_RUN_R0)
			wu.spal = (byte) (wp.pal = 27); // Bright Green
		else
			wu.spal = (byte) (wp.pal = 25); // Bright Red

		PlaySound(DIGI_MAGIC1, wp, v3df_follow | v3df_doppler);

		// find the distance to the target (player)
		dist = Distance(nx, ny, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

		if (dist != 0)
			// (velocity * difference between the target and the throwing star) /
			// distance
			wp.zvel = (short) ((wp.xvel * (SPRITEp_UPPER(sprite[u.tgt_sp]) - nz)) / dist);

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		nx = (int) (((long) 728 * (long) sintable[NORM_ANGLE(nang + 512)]) >> 14);
		ny = (int) (((long) 728 * (long) sintable[nang]) >> 14);

		move_missile(w, nx, ny, 0, wu.ceiling_dist, wu.floor_dist, 0, 3);

		return (w);
	}

	public static int DoCoolgDrip(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		u.Counter += 220;
		sp.z += u.Counter;

		if (sp.z > u.loz - u.floor_dist) {
			sp.z = u.loz - u.floor_dist;
			sp.yrepeat = sp.xrepeat = 32;
			ChangeState(SpriteNum, s_GoreFloorSplash[0]);
			if (u.spal == PALETTE_BLUE_LIGHTING)
				PlaySound(DIGI_DRIP, sp, v3df_none);
		}
		return (0);
	}

	public static int InitCoolgDrip(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], wp;
		USER wu;
		int nx, ny, nz;
		short w;

		nx = sp.x;
		ny = sp.y;
		nz = sp.z;

		w = (short) SpawnSprite(STAT_MISSILE, COOLG_DRIP, s_CoolgDrip[0], sp.sectnum, nx, ny, nz, sp.ang, 0);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		// wp.owner = SpriteNum;
		SetOwner(SpriteNum, w);
		wp.yrepeat = wp.xrepeat = 20;
		wp.shade = -5;
		wp.zvel = 0;
		wp.clipdist = 16 >> 2;
		wu.ceiling_dist = (short) Z(4);
		wu.floor_dist = (short) Z(4);
		wp.cstat |= (CSTAT_SPRITE_YCENTER);

		DoFindGroundPoint(SpriteNum);

		return (w);
	}

	public static final Animator GenerateDrips = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return GenerateDrips(spr) != 0;
		}
	};

	public static int GenerateDrips(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], wp;
		USER u = pUser[SpriteNum], wu;
		int nx, ny, nz;
		short w = -1;

		if ((u.WaitTics -= ACTORMOVETICS) <= 0) {
			if (sp.lotag == 0)
				u.WaitTics = (short) (RANDOM_P2(256 << 8) >> 8);
			else
				u.WaitTics = (short) ((sp.lotag * 120) + SEC(RANDOM_RANGE(3 << 8) >> 8));

			if (TEST_BOOL2(sp)) {
				w = (short) SpawnBubble(SpriteNum);
				return (w);
			}

			nx = sp.x;
			ny = sp.y;
			nz = sp.z;

			w = (short) SpawnSprite(STAT_SHRAP, COOLG_DRIP, s_CoolgDrip[0], sp.sectnum, nx, ny, nz, sp.ang, 0);
			if(w == -1)
				return 0;

			wp = sprite[w];
			wu = pUser[w];

			// wp.owner = SpriteNum;
			SetOwner(SpriteNum, w);
			wp.yrepeat = wp.xrepeat = 20;
			wp.shade = -10;
			wp.zvel = 0;
			wp.clipdist = 16 >> 2;
			wu.ceiling_dist = (short) Z(4);
			wu.floor_dist = (short) Z(4);
			wp.cstat |= (CSTAT_SPRITE_YCENTER);
			if (TEST_BOOL1(sp))
				wu.spal = (byte) (wp.pal = PALETTE_BLUE_LIGHTING);

			DoFindGroundPoint(SpriteNum);
		}
		return (w);
	}

	public static int InitEelFire(int SpriteNum) {
		USER u = pUser[SpriteNum], hu;
		SPRITE sp = pUser[SpriteNum].getSprite();
		SPRITE hp;
		short i, nexti, stat;
		int dist;

		for (stat = 0; stat < StatDamageList.length; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				hp = sprite[i];
				hu = pUser[i];

				if (i == SpriteNum)
					continue;

				if (i != u.tgt_sp)
					continue;

				if (FindDistance3D(sp.x - hp.x, sp.y - hp.y, (sp.z - hp.z) >> 4) > hu.Radius + u.Radius)
					continue;

				dist = DISTANCE(hp.x, hp.y, sp.x, sp.y);

				if (dist < CLOSE_RANGE_DIST_FUDGE(sp, hp, 600) && FACING_RANGE(hp, sp, 150)) {
					PlaySound(DIGI_GIBS1, sp, v3df_none);
					DoDamage(i, SpriteNum);
				} else
					InitActorReposition.invoke(SpriteNum);
			}
		}

		return (0);
	}

	public static int InitFireballTrap(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], wp;
		USER wu;
		int nx, ny, nz;
		short w;

		PlaySound(DIGI_FIREBALL1, sp, v3df_none);

		nx = sp.x;
		ny = sp.y;
		nz = sp.z - SPRITEp_SIZE_Z(sp);

		// Spawn a shot
		w = (short) SpawnSprite(STAT_MISSILE, FIREBALL, s_Fireball[0], sp.sectnum, nx, ny, nz, sp.ang,
				FIREBALL_TRAP_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		// wp.owner = SpriteNum;
		wp.hitag = LUMINOUS; // Always full brightness
		SetOwner(SpriteNum, w);
		wp.xrepeat -= 20;
		wp.yrepeat -= 20;
		wp.shade = -40;
		wp.clipdist = 32 >> 2;
		wp.zvel = 0;
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wu.WeaponNum = WPN_HOTHEAD;

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		return (w);
	}

	public static int InitBoltTrap(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], wp;
		USER wu;
		int nx, ny, nz;
		short w;

		PlaySound(DIGI_RIOTFIRE, sp, v3df_none);

		nx = sp.x;
		ny = sp.y;
		nz = sp.z - SPRITEp_SIZE_Z(sp);

		// Spawn a shot
		w = (short) SpawnSprite(STAT_MISSILE, BOLT_THINMAN_R0, s_Rocket[0][0], sp.sectnum, nx, ny, nz, sp.ang,
				BOLT_TRAP_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		// wp.owner = SpriteNum;
		SetOwner(SpriteNum, w);
		wp.yrepeat = 32;
		wp.xrepeat = 32;
		wp.shade = -15;
		wp.zvel = 0;
		wp.cstat |= (CSTAT_SPRITE_YCENTER);

		wu.RotNum = 5;
		NewStateGroup(w, WeaponStateGroup.sg_Rocket);
		wu.Radius = 200;

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		return (w);
	}

	public static int InitSpearTrap(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], wp;
		USER wu;
		int nx, ny, nz;
		short w;

		nx = sp.x;
		ny = sp.y;
		nz = SPRITEp_MID(sp);

		// Spawn a shot
		w = (short) SpawnSprite(STAT_MISSILE, CROSSBOLT, s_CrossBolt[0][0], sp.sectnum, nx, ny, nz, sp.ang, 750);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		// wp.owner = SpriteNum;
		SetOwner(SpriteNum, w);
		wp.xrepeat = 16;
		wp.yrepeat = 26;
		wp.shade = -25;
		wp.clipdist = 64 >> 2;

		wu.RotNum = 5;
		NewStateGroup(w, WeaponStateGroup.sg_CrossBolt);

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		wu.Flags |= (SPR_XFLIP_TOGGLE);

		PlaySound(DIGI_STAR, sp, v3df_none);
		return (w);
	}

	public static boolean DoSuicide(int SpriteNum) {
		KillSprite(SpriteNum);
		return false;
	}

	public static int DoDefaultStat(int SpriteNum) {
		change_sprite_stat(SpriteNum, STAT_DEFAULT);
		return (0);
	}

	private static final short lat_dist[] = { 800, -800 };

	public static int InitTracerUzi(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		SPRITE wp;
		USER wu;

		int nx, ny, nz;
		short w;
		int oclipdist;

		nx = pp.posx;
		ny = pp.posy;
		nz = pp.posz + Z(8) + ((100 - pp.getHorizi()) * 72);

		// Spawn a shot
		// Inserting and setting up variables
		w = (short) SpawnSprite(STAT_MISSILE, 0, s_Tracer[0], pp.cursectnum, nx, ny, nz, pp.getAnglei(), TRACER_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		wp.hitag = LUMINOUS; // Always full brightness
		SetOwner(pp.PlayerSprite, w);
		wp.yrepeat = 10;
		wp.xrepeat = 10;
		wp.shade = -40;
		wp.zvel = 0;
		wp.clipdist = 32 >> 2;

		wu.WeaponNum = u.WeaponNum;
		wu.Radius = 50;
		wu.ceiling_dist = (short) Z(3);
		wu.floor_dist = (short) Z(3);
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.cstat |= (CSTAT_SPRITE_INVISIBLE);

		SPRITE psp = pp.getSprite();
		oclipdist = psp.clipdist;
		psp.clipdist = 0;

		wp.ang = NORM_ANGLE(wp.ang + 512);
		if (TEST(pp.Flags, PF_TWO_UZI) && pp.WpnUziType == 0)
			HelpMissileLateral(w, lat_dist[RANDOM_P2(2 << 8) >> 8]);
		else
			HelpMissileLateral(w, lat_dist[0]);
		wp.ang = NORM_ANGLE(wp.ang - 512);

		if (MissileSetPos(w, DoTracerStart, 800)) {
			psp.clipdist = oclipdist;
			KillSprite(w);
			return (0);
		}

		wp.zvel = (short) ((100 - pp.getHorizi()) * (wp.xvel / 8));

		psp.clipdist = oclipdist;

		WeaponAutoAim(pp.PlayerSprite, w, 32, 0);

		// a bit of randomness
		wp.ang = NORM_ANGLE(wp.ang + RANDOM_RANGE(30) - 15);

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		if (TEST(pp.Flags, PF_DIVING) || SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		return (0);
	}

	public static int InitTracerTurret(int SpriteNum, int Operator, int horiz) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		SPRITE wp;
		USER wu;

		int nx, ny, nz;
		short w;

		nx = sp.x;
		ny = sp.y;
		nz = sp.z + ((100 - horiz) * 72);

		// Spawn a shot
		// Inserting and setting up variables

		w = (short) SpawnSprite(STAT_MISSILE, 0, s_Tracer[0], sp.sectnum, nx, ny, nz, sp.ang, TRACER_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		wp.hitag = LUMINOUS; // Always full brightness
		if (Operator >= 0)
			SetOwner(Operator, w);
		wp.yrepeat = 10;
		wp.xrepeat = 10;
		wp.shade = -40;
		wp.zvel = 0;
		wp.clipdist = 8 >> 2;

		wu.WeaponNum = u.WeaponNum;
		wu.Radius = 50;
		wu.ceiling_dist = (short) Z(1);
		wu.floor_dist = (short) Z(1);
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.cstat |= (CSTAT_SPRITE_INVISIBLE);

		wp.zvel = (short) ((100 - horiz) * (wp.xvel / 8));

		WeaponAutoAim(SpriteNum, w, 32, 0);

		// a bit of randomness
		wp.ang = NORM_ANGLE(wp.ang + RANDOM_RANGE(30) - 15);

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		if (SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		return (0);
	}

	public static int InitTracerAutoTurret(int SpriteNum, int Operator, int xchange, int ychange, int zchange) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		SPRITE wp;
		USER wu;

		int nx, ny, nz;
		short w;

		nx = sp.x;
		ny = sp.y;
		nz = sp.z;

		// Spawn a shot
		// Inserting and setting up variables

		w = (short) SpawnSprite(STAT_MISSILE, 0, s_Tracer[0], sp.sectnum, nx, ny, nz, sp.ang, TRACER_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		wp.hitag = LUMINOUS; // Always full brightness
		if (Operator >= 0)
			SetOwner(Operator, w);
		wp.yrepeat = 10;
		wp.xrepeat = 10;
		wp.shade = -40;
		wp.zvel = 0;
		wp.clipdist = 8 >> 2;

		wu.WeaponNum = u.WeaponNum;
		wu.Radius = 50;
		wu.ceiling_dist = (short) Z(1);
		wu.floor_dist = (short) Z(1);
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.cstat |= (CSTAT_SPRITE_INVISIBLE);

		wu.xchange = xchange;
		wu.ychange = ychange;
		wu.zchange = zchange;

		if (SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		return (0);
	}

	public static boolean BulletHitSprite(int spi, int hitsprite, int hitsect, int hitwall, int hitx, int hity,
			int hitz, int ID) {
		SPRITE sp = sprite[spi];
		SPRITE hsp = sprite[hitsprite];
		USER hu = pUser[hitsprite];
		if(hu == null)
			return false;

		SPRITE wp;
		short newsp;
		short id;

		// hit a NPC or PC?
		if (TEST(hsp.extra, SPRX_PLAYER_OR_ENEMY)) {
			// spawn a red splotch
			// !FRANK! this if was incorrect - its not who is HIT, its who is SHOOTING
			// if(!hu.PlayerP)
			if (pUser[spi].PlayerP != -1)
				id = UZI_SMOKE;
			else if (TEST(pUser[spi].Flags, SPR_SO_ATTACHED))
				id = UZI_SMOKE;
			else // Spawn NPC uzi with less damage
				id = UZI_SMOKE + 2;

			if (ID > 0)
				id = (short) ID;

			newsp = (short) SpawnSprite(STAT_MISSILE, id, s_UziSmoke[0], 0, hitx, hity, hitz, sp.ang, 0);
			if(newsp == -1)
				return false;

			wp = sprite[newsp];
			wp.shade = -40;

			if (hu.PlayerP != -1)
				wp.cstat |= (CSTAT_SPRITE_INVISIBLE);

			switch (hu.ID) {
			case TRASHCAN:
			case PACHINKO1:
			case PACHINKO2:
			case PACHINKO3:
			case PACHINKO4:
			case 623:
			case ZILLA_RUN_R0:
				wp.xrepeat = UZI_SMOKE_REPEAT;
				wp.yrepeat = UZI_SMOKE_REPEAT;
				if (RANDOM_P2(1024) > 800)
					SpawnShrapX(hitsprite);
				break;
			default:
				wp.xrepeat = UZI_SMOKE_REPEAT / 3;
				wp.yrepeat = UZI_SMOKE_REPEAT / 3;
				wp.cstat |= (CSTAT_SPRITE_INVISIBLE);
				// wu.spal = wp.pal = PALETTE_RED_LIGHTING;
				break;
			}

			SetOwner(spi, newsp);
			wp.ang = sp.ang;

			engine.setspritez(newsp, hitx, hity, hitz);
			wp.cstat |= (CSTAT_SPRITE_YCENTER);
			wp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

			if ((RANDOM_P2(1024 << 5) >> 5) < 512 + 128) {
				if (hu.PlayerP == -1)
					SpawnBlood(hitsprite, -1, sp.ang, hitx, hity, hitz);
				else
					SpawnBlood(hitsprite, -1, sp.ang, hitx, hity, hitz + Z(20));

				// blood comes out the other side?
				if ((RANDOM_P2(1024 << 5) >> 5) < 256) {
					if (hu.PlayerP == -1)
						SpawnBlood(hitsprite, -1, NORM_ANGLE(sp.ang + 1024), hitx, hity, hitz);
					if (hu.ID != TRASHCAN && hu.ID != ZILLA_RUN_R0)
						QueueWallBlood(hitsprite, sp.ang); // QueueWallBlood needs bullet angle.
				}
			}

			DoHitscanDamage(newsp, hitsprite);

			return (true);
		}

		return (false);
	}

	public static int SpawnWallHole(short hitsect, short hitwall, int hitx, int hity, int hitz) {
		short w, nw, wall_ang;
		short SpriteNum;
		SPRITE sp;

		SpriteNum = COVERinsertsprite(hitsect, STAT_DEFAULT);
		sp = sprite[SpriteNum];
		sp.owner = -1;
		sp.xrepeat = sp.yrepeat = 16;
		sp.cstat = sp.pal = sp.shade = (byte) (sp.extra = (short) (sp.clipdist = sp.xoffset = sp.yoffset = 0));
		sp.x = hitx;
		sp.y = hity;
		sp.z = hitz;
		sp.picnum = 2151;

		sp.cstat |= (CSTAT_SPRITE_WALL);
		sp.cstat |= (CSTAT_SPRITE_ONE_SIDE);

		w = hitwall;
		nw = wall[w].point2;
		wall_ang = NORM_ANGLE(engine.getangle(wall[nw].x - wall[w].x, wall[nw].y - wall[w].y) - 512);

		sp.ang = NORM_ANGLE(wall_ang + 1024);

		return (SpriteNum);
	}

	public static boolean HitscanSpriteAdjust(int SpriteNum, int hitwall) {
		SPRITE sp = sprite[SpriteNum];
		short w, nw, ang = sp.ang, wall_ang;
		int xvect, yvect;
		short sectnum;

		if (hitwall >= 0) {
			w = (short) hitwall;
			nw = wall[w].point2;
			wall_ang = NORM_ANGLE(engine.getangle(wall[nw].x - wall[w].x, wall[nw].y - wall[w].y));
			ang = sp.ang = NORM_ANGLE(wall_ang + 512);
		} else
			ang = sp.ang;

		xvect = sintable[(512 + ang) & 2047] << 4;
		yvect = sintable[ang] << 4;

		clipmoveboxtracenum = 1;

		// must have this
		sectnum = sp.sectnum;
		engine.clipmove(sp.x, sp.y, sp.z, sectnum, xvect, yvect, 4, 4 << 8, 4 << 8, CLIPMASK_MISSILE);

		sp.x = clipmove_x;
		sp.y = clipmove_y;
		sp.z = clipmove_z;
		sectnum = clipmove_sectnum;

		clipmoveboxtracenum = 3;

		if (sp.sectnum != sectnum)
			engine.changespritesect(SpriteNum, sectnum);

		return (true);
	}

	private static int uziclock = 0;
	private static final int UZIFIRE_WAIT = 20;

	public static int InitUzi(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		SPRITE wp, hsp;
		USER wu;
		short daang, j;
		short hitsect, hitwall, hitsprite;
		int hitx, hity, hitz, daz, nz;
		int xvect, yvect, zvect;
		short cstat = 0;
		byte pal = 0;
		// static char alternate=0;

		int clockdiff = 0;
		boolean FireSnd = false;

		PlayerUpdateAmmo(pp, u.WeaponNum, -1);

		if (uziclock > totalclock) {
			uziclock = totalclock;
			FireSnd = true;
		}

		clockdiff = totalclock - uziclock;
		if (clockdiff > UZIFIRE_WAIT) {
			uziclock = totalclock;
			FireSnd = true;
		}

		if (FireSnd)
			PlaySound(DIGI_UZIFIRE, pp, v3df_dontpan | v3df_doppler);

		// Make sprite shade brighter
		u.Vis = 128;

		if (RANDOM_P2(1024) < 400)
			InitTracerUzi(pp);

		nz = pp.posz + pp.bob_z;
		daz = pp.posz + pp.bob_z;
		daang = 32;

		if (WeaponAutoAimHitscan(pp.PlayerSprite, tmp_ptr[0].set(daz), tmp_ptr[1].set(daang), 0) != -1) {
			daz = tmp_ptr[0].value;
			daang = (short) tmp_ptr[1].value;
			daang += RANDOM_RANGE(24) - 12;
			daang = NORM_ANGLE(daang);
			daz += RANDOM_RANGE(10000) - 5000;
		} else {
			// daang = NORM_ANGLE(pp.getAnglei() + (RANDOM_RANGE(50) - 25));
			daang = NORM_ANGLE(pp.getAnglei() + (RANDOM_RANGE(24) - 12));
			daz = ((100 - pp.getHorizi()) * 2000) + (RANDOM_RANGE(24000) - 12000);
		}

		xvect = sintable[NORM_ANGLE(daang + 512)];
		yvect = sintable[NORM_ANGLE(daang)];
		zvect = daz;
		FAFhitscan(pp.posx, pp.posy, nz, pp.cursectnum, // Start position
				xvect, yvect, zvect, pHitInfo, CLIPMASK_MISSILE);

		hitsect = pHitInfo.hitsect;
		hitwall = pHitInfo.hitwall;
		hitsprite = pHitInfo.hitsprite;
		hitx = pHitInfo.hitx;
		hity = pHitInfo.hity;
		hitz = pHitInfo.hitz;

		if (hitsect < 0) {
			return (0);
		}

		SetVisHigh();

		// check to see what you hit
		if (hitsprite < 0 && hitwall < 0) {
			if (klabs(hitz - sector[hitsect].ceilingz) <= Z(1)) {
				hitz += Z(16);
				cstat |= (CSTAT_SPRITE_YFLIP);

				if (TEST(sector[hitsect].ceilingstat, CEILING_STAT_PLAX))
					return (0);

				if (SectorIsUnderwaterArea(hitsect)) {
					WarpToSurface(tmp_ptr[0].set(hitsect), tmp_ptr[1].set(hitx), tmp_ptr[2].set(hity),
							tmp_ptr[3].set(hitz));
					hitsect = (short) tmp_ptr[0].value;
					hitx = tmp_ptr[1].value;
					hity = tmp_ptr[2].value;
					hitz = tmp_ptr[3].value;

					ContinueHitscan(pp, hitsect, hitx, hity, hitz, daang, xvect, yvect, zvect);
					return (0);
				}
			} else if (klabs(hitz - sector[hitsect].floorz) <= Z(1)) {
				if (DTEST(sector[hitsect].extra, SECTFX_LIQUID_MASK) != SECTFX_LIQUID_NONE) {
					SpawnSplashXY(hitx, hity, hitz, hitsect);

					if (SectorIsDiveArea(hitsect)) {
						WarpToUnderwater(tmp_ptr[0].set(hitsect), tmp_ptr[1].set(hitx), tmp_ptr[2].set(hity),
								tmp_ptr[3].set(hitz));
						hitsect = (short) tmp_ptr[0].value;
						hitx = tmp_ptr[1].value;
						hity = tmp_ptr[2].value;
						hitz = tmp_ptr[3].value;

						ContinueHitscan(pp, hitsect, hitx, hity, hitz, daang, xvect, yvect, zvect);
						return (0);
					}

					return (0);
				}
			}
		}

		if (hitwall >= 0) {
			if (wall[hitwall].nextsector >= 0) {
				if (TEST(sector[wall[hitwall].nextsector].ceilingstat, CEILING_STAT_PLAX)) {
					if (hitz < sector[wall[hitwall].nextsector].ceilingz) {
						return (0);
					}
				}
			}

			if (wall[hitwall].lotag == TAG_WALL_BREAK) {
				HitBreakWall(hitwall, hitx, hity, hitz, daang, u.ID);
				return (0);
			}

			QueueHole(daang, hitsect, hitwall, hitx, hity, hitz);
		}

		// hit a sprite?
		if (hitsprite >= 0) {
			USER hu = pUser[hitsprite];
			hsp = sprite[hitsprite];

			if (hu != null && hu.ID == TRASHCAN) {

				PlaySound(DIGI_TRASHLID, hsp, v3df_none);
				if (hu.WaitTics <= 0) {
					hu.WaitTics = (short) SEC(2);
					ChangeState(hitsprite, s_TrashCanPain[0]);
				}
			}

			if (hsp.lotag == TAG_SPRITE_HIT_MATCH) {
				if (MissileHitMatch(-1, WPN_UZI, hitsprite))
					return (0);
			}

			if (TEST(hsp.extra, SPRX_BREAKABLE) && HitBreakSprite(hitsprite, 0)) {
				return (0);
			}

			if (BulletHitSprite(pp.PlayerSprite, hitsprite, hitsect, hitwall, hitx, hity, hitz, 0))
				return (0);

			// hit a switch?
			if (TEST(hsp.cstat, CSTAT_SPRITE_WALL) && (hsp.lotag != 0 || hsp.hitag != 0)) {
				ShootableSwitch(hitsprite, -1);
			}
		}

		j = (short) SpawnSprite(STAT_MISSILE, UZI_SMOKE, s_UziSmoke[0], hitsect, hitx, hity, hitz, daang, 0);
		if(j == -1)
			return 0;

		wp = sprite[j];
		wp.shade = -40;
		wp.xrepeat = UZI_SMOKE_REPEAT;
		wp.yrepeat = UZI_SMOKE_REPEAT;
		SetOwner(pp.PlayerSprite, j);
		// SET(wp.cstat, cstat | CSTAT_SPRITE_TRANSLUCENT | CSTAT_SPRITE_YCENTER);
		wp.cstat |= (cstat | CSTAT_SPRITE_YCENTER);
		wp.clipdist = 8 >> 2;

		HitscanSpriteAdjust(j, hitwall);
		DoHitscanDamage(j, hitsprite);

		j = (short) SpawnSprite(STAT_MISSILE, UZI_SPARK, s_UziSpark[0], hitsect, hitx, hity, hitz, daang, 0);
		if(j == -1)
			return 0;

		wp = sprite[j];
		wu = pUser[j];
		wp.shade = -40;
		wp.xrepeat = UZI_SPARK_REPEAT;
		wp.yrepeat = UZI_SPARK_REPEAT;
		SetOwner(pp.PlayerSprite, j);
		wu.spal = (byte) (wp.pal = pal);
		wp.cstat |= (cstat | CSTAT_SPRITE_YCENTER);
		wp.clipdist = 8 >> 2;

		HitscanSpriteAdjust(j, hitwall);

		if (RANDOM_P2(1024) < 100) {
			PlaySound(DIGI_RICHOCHET1, wp, v3df_none);
		} else if (RANDOM_P2(1024) < 100)
			PlaySound(DIGI_RICHOCHET2, wp, v3df_none);

		return (0);
	}

	// Electro Magnetic Pulse gun
	public static int InitEMP(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		SPRITE wp, hsp = null;
		USER wu;
		short daang, j;
		short hitsect, hitwall, hitsprite;
		int hitx, hity, hitz, daz, nz;
		short cstat = 0;

		PlayerUpdateAmmo(pp, u.WeaponNum, -1);

		PlaySound(DIGI_RAILFIRE, pp, v3df_dontpan | v3df_doppler);

		InitTracerUzi(pp);

		daz = nz = pp.posz + pp.bob_z;
		daang = 64;
		if (WeaponAutoAimHitscan(pp.PlayerSprite, tmp_ptr[0].set(daz), tmp_ptr[1].set(daang), 0) != -1) {
			daz = tmp_ptr[0].value;
			daang = (short) tmp_ptr[1].value;
		} else {
			daz = (100 - pp.getHorizi()) * 2000;
			daang = pp.getAnglei();
		}

		FAFhitscan(pp.posx, pp.posy, nz, pp.cursectnum, // Start position
				sintable[NORM_ANGLE(daang + 512)], // X vector of 3D ang
				sintable[NORM_ANGLE(daang)], // Y vector of 3D ang
				daz, // Z vector of 3D ang
				pHitInfo, CLIPMASK_MISSILE);

		hitsect = pHitInfo.hitsect;
		hitwall = pHitInfo.hitwall;
		hitsprite = pHitInfo.hitsprite;
		hitx = pHitInfo.hitx;
		hity = pHitInfo.hity;
		hitz = pHitInfo.hitz;

		j = (short) SpawnSprite(STAT_MISSILE, EMP, s_EMPBurst[0], hitsect, hitx, hity, hitz, daang, 0);
		if(j == -1)
			return 0;

		wp = sprite[j];
		wu = pUser[j];

		wu.WaitTics = (short) SEC(7);
		wp.shade = -127;
		SetOwner(pp.PlayerSprite, j);
		wp.cstat |= (cstat | CSTAT_SPRITE_YCENTER);
		wp.clipdist = 8 >> 2;

		if (hitsect < 0) {

			return (0);
		}

		SetVisHigh();

		// check to see what you hit
		if (hitsprite < 0 && hitwall < 0) {
			if (klabs(hitz - sector[hitsect].ceilingz) <= Z(1)) {
				hitz += Z(16);
				cstat |= (CSTAT_SPRITE_YFLIP);

				if (TEST(sector[hitsect].ceilingstat, CEILING_STAT_PLAX))
					return (0);
			} else if (klabs(hitz - sector[hitsect].floorz) <= Z(1)) {
				if (DTEST(sector[hitsect].extra, SECTFX_LIQUID_MASK) != SECTFX_LIQUID_NONE) {
					SpawnSplashXY(hitx, hity, hitz, hitsect);
					return (0);
				}
			}
		}

		if (hitwall >= 0) {
			if (wall[hitwall].nextsector >= 0) {
				if (TEST(sector[wall[hitwall].nextsector].ceilingstat, CEILING_STAT_PLAX)) {
					if (hitz < sector[wall[hitwall].nextsector].ceilingz) {
						return (0);
					}
				}
			}

			HitscanSpriteAdjust(j, hitwall);
			if (wall[hitwall].lotag == TAG_WALL_BREAK) {
				HitBreakWall(hitwall, hitx, hity, hitz, daang, u.ID);
				return (0);
			}
		}

		// hit a sprite?
		if (hitsprite >= 0) {
			hsp = sprite[hitsprite];

			if (hsp.lotag == TAG_SPRITE_HIT_MATCH) {
				if (MissileHitMatch(-1, WPN_UZI, hitsprite))
					return (0);
			}

			if (TEST(hsp.extra, SPRX_BREAKABLE)) {
				HitBreakSprite(hitsprite, 0);
				// return(0);
			}

			if (BulletHitSprite(pp.PlayerSprite, hitsprite, hitsect, hitwall, hitx, hity, hitz, 0))
				// return(0);

				// hit a switch?
				if (TEST(hsp.cstat, CSTAT_SPRITE_WALL) && (hsp.lotag != 0 || hsp.hitag != 0)) {
					ShootableSwitch(hitsprite, -1);
				}

			if (TEST(hsp.extra, SPRX_PLAYER_OR_ENEMY)) {
				// attach weapon to sprite
				SetAttach(hitsprite, j);
				wu.sz = sprite[hitsprite].z - wp.z;
				if (RANDOM_RANGE(1000) > 500)
					PlayerSound(DIGI_YOULOOKSTUPID, v3df_follow | v3df_dontpan, pp);
			} else {
				if (TEST(hsp.cstat, CSTAT_SPRITE_WALL)) {
					wu.Flags2 |= (SPR2_ATTACH_WALL);
				} else if (TEST(hsp.cstat, CSTAT_SPRITE_FLOOR)) {
					// hit floor
					if (wp.z > DIV2(wu.hiz + wu.loz))
						wu.Flags2 |= (SPR2_ATTACH_FLOOR);
					else
						wu.Flags2 |= (SPR2_ATTACH_CEILING);
				} else {
					KillSprite(j);
					return (0);
				}
			}

		}
		return (0);
	}

	public static int InitTankShell(int SpriteNum, PlayerStr pp) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		SPRITE wp;
		USER wu;
		short w;

		PlaySound(DIGI_CANNON, pp, v3df_dontpan | v3df_doppler);

		w = (short) SpawnSprite(STAT_MISSILE, 0, s_TankShell[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang,
				TANK_SHELL_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		SetOwner(pp.PlayerSprite, w);
		wp.yrepeat = 8;
		wp.xrepeat = 8;
		wp.shade = -40;
		wp.zvel = 0;
		wp.clipdist = 32 >> 2;

		wu.WeaponNum = u.WeaponNum;
		wu.Radius = 50;
		wu.ceiling_dist = (short) Z(4);
		wu.floor_dist = (short) Z(4);
		wu.Flags2 |= (SPR2_SO_MISSILE);
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.cstat |= (CSTAT_SPRITE_INVISIBLE);

		wp.zvel = (short) ((100 - pp.getHorizi()) * (wp.xvel / 8));

		WeaponAutoAim(SpriteNum, w, 64, 0);
		// a bit of randomness
		wp.ang += RANDOM_RANGE(30) - 15;
		wp.ang = NORM_ANGLE(wp.ang);

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		if (SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		return (0);
	}

	public static final int MAX_TURRET_MICRO = 10;

	public static int InitTurretMicro(int SpriteNum, PlayerStr pp) {

		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();

		USER pu = pUser[pp.PlayerSprite];
		USER wu, hu;
		SPRITE wp, hp;
		int nx, ny, nz, dist;
		short w;
		short i, ang;

		nx = sp.x;
		ny = sp.y;

		DoPickTarget(pp.PlayerSprite, 256, 0);

		if (TargetSortCount > MAX_TURRET_MICRO)
			TargetSortCount = MAX_TURRET_MICRO;

		int tsi = 0;
		for (i = 0; i < MAX_TURRET_MICRO; i++) {
			Target_Sort ts = TargetSort[tsi];
			if (tsi < TargetSortCount && ts.sprite_num >= 0) {
				hp = sprite[ts.sprite_num];
				hu = pUser[ts.sprite_num];

				ang = engine.getangle(hp.x - nx, hp.y - ny);

				tsi++;
			} else {
				hp = null;
				hu = null;
				ang = sp.ang;
			}

			nz = sp.z;
			nz += Z(RANDOM_RANGE(20)) - Z(10);

			// Spawn a shot
			// Inserting and setting up variables

			w = (short) SpawnSprite(STAT_MISSILE, BOLT_THINMAN_R0, s_Micro[0][0], sp.sectnum, nx, ny, nz, ang, 1200);
			if(w == -1)
				return 0;

			wp = sprite[w];
			wu = pUser[w];

			SetOwner(pp.PlayerSprite, w);
			wp.yrepeat = 24;
			wp.xrepeat = 24;
			wp.shade = -15;
			wp.zvel = (short) ((100 - pp.getHorizi()) * HORIZ_MULT);
			wp.clipdist = 64 >> 2;

			// randomize zvelocity
			wp.zvel += RANDOM_RANGE(Z(8)) - Z(5);

			wu.RotNum = 5;
			NewStateGroup(w, WeaponStateGroup.sg_Micro);

			wu.WeaponNum = pu.WeaponNum;
			wu.Radius = 200;
			wu.ceiling_dist = (short) Z(2);
			wu.floor_dist = (short) Z(2);
			wp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
			wp.cstat |= (CSTAT_SPRITE_YCENTER);
			wp.cstat |= (CSTAT_SPRITE_INVISIBLE);

			wu.WaitTics = (short) (10 + RANDOM_RANGE(40));

			if (hp != null) {
				dist = Distance(wp.x, wp.y, hp.x, hp.y);
				if (dist != 0) {
					int zh;
					zh = SPRITEp_TOS(hp) + DIV4(SPRITEp_SIZE_Z(hp));
					wp.zvel = (short) ((wp.xvel * (zh - wp.z)) / dist);
				}

				wu.WpnGoal = (short) ts.sprite_num;
				hu.Flags |= (SPR_TARGETED);
				hu.Flags |= (SPR_ATTACKED);
			} else {
				wp.ang = NORM_ANGLE(wp.ang + (RANDOM_RANGE(MICRO_ANG) - DIV2(MICRO_ANG)) - 16);
			}

			wu.xchange = MOVEx(wp.xvel, wp.ang);
			wu.ychange = MOVEy(wp.xvel, wp.ang);
			wu.zchange = wp.zvel;
		}

		return (0);
	}

	public static int InitTurretRocket(int SpriteNum, PlayerStr pp) {

		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		SPRITE wp;
		USER wu;
		int w = SpawnSprite(STAT_MISSILE, BOLT_THINMAN_R0, s_Rocket[0][0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang,
				ROCKET_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		SetOwner(pp.PlayerSprite, w);
		wp.yrepeat = 40;
		wp.xrepeat = 40;
		wp.shade = -40;
		wp.zvel = 0;
		wp.clipdist = 32 >> 2;

		wu.WeaponNum = u.WeaponNum;
		wu.Radius = 50;
		wu.ceiling_dist = (short) Z(4);
		wu.floor_dist = (short) Z(4);
		wu.Flags2 |= (SPR2_SO_MISSILE);
		wp.cstat |= (CSTAT_SPRITE_YCENTER);

		wp.zvel = (short) ((100 - pp.getHorizi()) * (wp.xvel / 8));

		WeaponAutoAim(SpriteNum, w, 64, 0);
		// a bit of randomness
		// wp.ang += RANDOM_RANGE(30) - 15;

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		if (SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);
		return (0);
	}

	public static int InitTurretFireball(int SpriteNum, PlayerStr pp) {

		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		SPRITE wp;
		USER wu;
		short w;

		w = (short) SpawnSprite(STAT_MISSILE, FIREBALL, s_Fireball[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang,
				FIREBALL_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		SetOwner(pp.PlayerSprite, w);
		wp.yrepeat = 40;
		wp.xrepeat = 40;
		wp.shade = -40;
		wp.zvel = 0;
		wp.clipdist = 32 >> 2;

		wu.WeaponNum = u.WeaponNum;
		wu.Radius = 50;
		wu.ceiling_dist = (short) Z(4);
		wu.floor_dist = (short) Z(4);
		wu.Flags2 |= (SPR2_SO_MISSILE);
		wp.cstat |= (CSTAT_SPRITE_YCENTER);

		wp.zvel = (short) ((100 - pp.getHorizi()) * (wp.xvel / 8));

		WeaponAutoAim(SpriteNum, w, 64, 0);
		// a bit of randomness
		wp.ang += RANDOM_RANGE(30) - 15;
		wp.ang = NORM_ANGLE(wp.ang);

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		if (SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		return (0);
	}

	public static int InitTurretRail(int SpriteNum, PlayerStr pp) {

		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		USER wu;
		SPRITE wp;
		int nx, ny, nz;
		short w;

		nx = sp.x;
		ny = sp.y;
		nz = sp.z;

		// Spawn a shot
		// Inserting and setting up variables

		w = (short) SpawnSprite(STAT_MISSILE, BOLT_THINMAN_R1, s_Rail[0][0], pp.cursectnum, nx, ny, nz, sp.ang, 1200);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		SetOwner(pp.PlayerSprite, w);
		wp.yrepeat = 52;
		wp.xrepeat = 52;
		wp.shade = -15;
		wp.zvel = (short) ((100 - pp.getHorizi()) * HORIZ_MULT);

		wu.RotNum = 5;
		NewStateGroup(w, WeaponStateGroup.sg_Rail);

		wu.Radius = 200;
		wu.ceiling_dist = (short) Z(1);
		wu.floor_dist = (short) Z(1);
		wu.Flags2 |= (SPR2_SO_MISSILE);
		wp.cstat |= (CSTAT_SPRITE_YCENTER | CSTAT_SPRITE_INVISIBLE);
		wp.cstat |= (CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		wp.clipdist = 64 >> 2;

		if (WeaponAutoAim(pp.PlayerSprite, w, 32, 0) == -1) {
			wp.ang = NORM_ANGLE(wp.ang);
		}

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		return (0);
	}

	public static int InitTurretLaser(int SpriteNum, PlayerStr pp) {

		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		USER wu;
		SPRITE wp;
		int nx, ny, nz;
		short w;

		nx = sp.x;
		ny = sp.y;
		nz = sp.z;

		// Spawn a shot
		// Inserting and setting up variables

		w = (short) SpawnSprite(STAT_MISSILE, BOLT_THINMAN_R0, s_Laser[0], pp.cursectnum, nx, ny, nz, sp.ang, 300);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		SetOwner(pp.PlayerSprite, w);
		wp.yrepeat = 52;
		wp.xrepeat = 52;
		wp.shade = -15;

		// the slower the missile travels the less of a zvel it needs
		wp.zvel = (short) ((100 - pp.getHorizi()) * HORIZ_MULT);
		wp.zvel /= 4;

		wu.Radius = 200;
		wu.ceiling_dist = (short) Z(1);
		wu.floor_dist = (short) Z(1);
		wu.Flags2 |= (SPR2_SO_MISSILE);
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.cstat |= (CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		wp.clipdist = 64 >> 2;

		if (WeaponAutoAim(SpriteNum, w, 32, 0) == -1) {
			wp.ang = NORM_ANGLE(wp.ang);
		}

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		return (0);
	}

	public static int InitSobjMachineGun(int SpriteNum, PlayerStr pp) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		SPRITE hsp;
		short daang;
		short hitsect, hitwall, hitsprite, nsect;
		int hitx, hity, hitz, daz;
		int nx, ny, nz;
		short spark;

		PlaySound(DIGI_BOATFIRE, pp, v3df_dontpan | v3df_doppler);

		nx = sp.x;
		ny = sp.y;
		daz = nz = sp.z;
		nsect = sp.sectnum;

		if (RANDOM_P2(1024) < 200)
			InitTracerTurret(SpriteNum, pp.PlayerSprite, pp.getHorizi());

		daang = 64;
		if (WeaponAutoAimHitscan(SpriteNum, tmp_ptr[0].set(daz), tmp_ptr[1].set(daang), 0) != -1) {
			daz = tmp_ptr[0].value;
			daang = (short) tmp_ptr[1].value;

			daz += RANDOM_RANGE(Z(30)) - Z(15);
		} else {
			int horiz;
			horiz = pp.getHorizi();
			if (horiz < 75)
				horiz = 75;

			daz = ((100 - horiz) * 2000) + (RANDOM_RANGE(Z(80)) - Z(40));
			daang = sp.ang;
		}

		FAFhitscan(nx, ny, nz, nsect, // Start position
				sintable[NORM_ANGLE(daang + 512)], // X vector of 3D ang
				sintable[NORM_ANGLE(daang)], // Y vector of 3D ang
				daz, // Z vector of 3D ang
				pHitInfo, CLIPMASK_MISSILE);

		hitsect = pHitInfo.hitsect;
		hitwall = pHitInfo.hitwall;
		hitsprite = pHitInfo.hitsprite;
		hitx = pHitInfo.hitx;
		hity = pHitInfo.hity;
		hitz = pHitInfo.hitz;

		if (hitsect < 0) {
			return (0);
		}

		if (hitsprite < 0 && hitwall < 0) {
			if (klabs(hitz - sector[hitsect].ceilingz) <= Z(1)) {
				hitz += Z(16);

				if (TEST(sector[hitsect].ceilingstat, CEILING_STAT_PLAX))
					return (0);
			} else if (klabs(hitz - sector[hitsect].floorz) <= Z(1)) {
				if (DTEST(sector[hitsect].extra, SECTFX_LIQUID_MASK) != SECTFX_LIQUID_NONE) {
					SpawnSplashXY(hitx, hity, hitz, hitsect);
					return (0);
				}
			}

		}

		// hit a sprite?
		if (hitsprite >= 0) {
			hsp = sprite[hitsprite];

			if (sprite[hitsprite].lotag == TAG_SPRITE_HIT_MATCH) {
				// spawn sparks here and pass the sprite as SO_MISSILE
				spark = (short) SpawnBoatSparks(pp, hitsect, hitwall, hitx, hity, hitz, daang);
				pUser[spark].Flags2 |= (SPR2_SO_MISSILE);
				if (MissileHitMatch(spark, -1, hitsprite))
					return (0);
				return (0);
			}

			if (TEST(hsp.extra, SPRX_BREAKABLE)) {
				HitBreakSprite(hitsprite, 0);
				return (0);
			}

			if (BulletHitSprite(pp.PlayerSprite, hitsprite, hitsect, hitwall, hitx, hity, hitz, 0))
				return (0);

			// hit a switch?
			if (TEST(hsp.cstat, CSTAT_SPRITE_WALL) && (hsp.lotag != 0 || hsp.hitag != 0)) {
				ShootableSwitch(hitsprite, -1);
			}
		}

		spark = (short) SpawnBoatSparks(pp, hitsect, hitwall, hitx, hity, hitz, daang);
		DoHitscanDamage(spark, hitsprite);

		return (0);
	}

	public static int InitSobjGun(PlayerStr pp) {
		short i;
		SPRITE sp;
		boolean first = false;

		if (pp.sop == -1)
			return 0;

		Sector_Object sop = SectorObject[pp.sop];

		for (i = 0; sop.sp_num[i] != -1; i++) {
			if (sprite[sop.sp_num[i]].statnum == STAT_SO_SHOOT_POINT) {
				sp = sprite[sop.sp_num[i]];

				// match when firing
				if (SP_TAG2(sp) != 0) {
					DoMatchEverything(pp, SP_TAG2(sp), -1);
					if (TEST_BOOL1(sp)) {
						sp.lotag = 0;
					}
				}

				// inert shoot point
				if (SP_TAG3(sp) == 255)
					return (0);

				if (!first) {
					first = true;
					if (SP_TAG6(sp) != 0)
						DoSoundSpotMatch(SP_TAG6(sp), 1, SoundType.SOUND_OBJECT_TYPE);
				}

				switch (SP_TAG3(sp)) {
				case 32:
				case 0:
					SpawnVis(sop.sp_num[i], -1, -1, -1, -1, 8);
					SpawnBigGunFlames(sop.sp_num[i], pp.PlayerSprite, sop);
					SetGunQuake(sop.sp_num[i]);
					InitTankShell(sop.sp_num[i], pp);
					if (SP_TAG5(sp) == 0)
						pp.FirePause = 80;
					else
						pp.FirePause = (short) SP_TAG5(sp);
					break;
				case 1:
					SpawnVis(sop.sp_num[i], -1, -1, -1, -1, 32);
					SpawnBigGunFlames(-(sop.sp_num[i]), pp.PlayerSprite, sop);
					InitSobjMachineGun(sop.sp_num[i], pp);
					if (SP_TAG5(sp) == 0)
						pp.FirePause = 10;
					else
						pp.FirePause = (short) SP_TAG5(sp);
					break;

				case 2:
					SpawnVis(sop.sp_num[i], -1, -1, -1, -1, 32);
					InitTurretLaser(sop.sp_num[i], pp);
					if (SP_TAG5(sp) == 0)
						pp.FirePause = 120;
					else
						pp.FirePause = (short) SP_TAG5(sp);
					break;
				case 3:
					SpawnVis(sop.sp_num[i], -1, -1, -1, -1, 32);
					InitTurretRail(sop.sp_num[i], pp);
					if (SP_TAG5(sp) == 0)
						pp.FirePause = 120;
					else
						pp.FirePause = (short) SP_TAG5(sp);
					break;
				case 4:
					SpawnVis(sop.sp_num[i], -1, -1, -1, -1, 32);
					InitTurretFireball(sop.sp_num[i], pp);
					if (SP_TAG5(sp) == 0)
						pp.FirePause = 20;
					else
						pp.FirePause = (short) SP_TAG5(sp);
					break;
				case 5:
					SpawnVis(sop.sp_num[i], -1, -1, -1, -1, 32);
					InitTurretRocket(sop.sp_num[i], pp);
					if (SP_TAG5(sp) == 0)
						pp.FirePause = 100;
					else
						pp.FirePause = (short) SP_TAG5(sp);
					break;
				case 6:
					SpawnVis(sop.sp_num[i], -1, -1, -1, -1, 32);
					InitTurretMicro(sop.sp_num[i], pp);
					if (SP_TAG5(sp) == 0)
						pp.FirePause = 100;
					else
						pp.FirePause = (short) SP_TAG5(sp);
					break;
				}
			}
		}

		return (0);
	}

	public static int SpawnBoatSparks(PlayerStr pp, int hitsect, int hitwall, int hitx, int hity, int hitz,
			short hitang) {
		short j;
		SPRITE wp;
		USER wu;

		j = (short) SpawnSprite(STAT_MISSILE, UZI_SMOKE, s_UziSmoke[0], hitsect, hitx, hity, hitz, hitang, 0);
		if(j == -1)
			return 0;

		wp = sprite[j];
		wp.shade = -40;
		wp.xrepeat = UZI_SMOKE_REPEAT + 12;
		wp.yrepeat = UZI_SMOKE_REPEAT + 12;
		SetOwner(pp.PlayerSprite, j);
		wp.cstat |= (CSTAT_SPRITE_TRANSLUCENT | CSTAT_SPRITE_YCENTER);

		wp.hitag = LUMINOUS; // Always full brightness
		// Sprite starts out with center exactly on wall.
		// This moves it back enough to see it at all angles.

		wp.clipdist = 32 >> 2;

		HitscanSpriteAdjust(j, hitwall);

		j = (short) SpawnSprite(STAT_MISSILE, UZI_SPARK, s_UziSpark[0], hitsect, hitx, hity, hitz, hitang, 0);
		if(j == -1)
			return 0;

		wp = sprite[j];
		wu = pUser[j];
		wp.shade = -40;
		wp.xrepeat = UZI_SPARK_REPEAT + 10;
		wp.yrepeat = UZI_SPARK_REPEAT + 10;
		SetOwner(pp.PlayerSprite, j);
		wu.spal = (byte) (wp.pal = PALETTE_DEFAULT);
		wp.cstat |= (CSTAT_SPRITE_YCENTER);

		wp.clipdist = 32 >> 2;

		HitscanSpriteAdjust(j, hitwall);

		if (RANDOM_P2(1024) < 100)
			PlaySound(DIGI_RICHOCHET1, wp, v3df_none);

		return (j);
	}

	public static int SpawnTurretSparks(SPRITE sp, int hitsect, int hitwall, int hitx, int hity, int hitz, int hitang) {
		short j;
		SPRITE wp;
		USER wu;

		j = (short) SpawnSprite(STAT_MISSILE, UZI_SMOKE, s_UziSmoke[0], hitsect, hitx, hity, hitz, hitang, 0);
		if(j == -1)
			return 0;

		wp = sprite[j];
		wp.shade = -40;
		wp.xrepeat = UZI_SMOKE_REPEAT + 12;
		wp.yrepeat = UZI_SMOKE_REPEAT + 12;
		wp.cstat |= (CSTAT_SPRITE_TRANSLUCENT | CSTAT_SPRITE_YCENTER);
		wp.hitag = LUMINOUS; // Always full brightness

		// Sprite starts out with center exactly on wall.
		// This moves it back enough to see it at all angles.

		wp.clipdist = 32 >> 2;
		HitscanSpriteAdjust(j, hitwall);

		j = (short) SpawnSprite(STAT_MISSILE, UZI_SPARK, s_UziSpark[0], hitsect, hitx, hity, hitz, hitang, 0);
		if(j == -1)
			return 0;

		wp = sprite[j];
		wu = pUser[j];
		wp.shade = -40;
		wp.xrepeat = UZI_SPARK_REPEAT + 10;
		wp.yrepeat = UZI_SPARK_REPEAT + 10;
		wu.spal = (byte) (wp.pal = PALETTE_DEFAULT);
		wp.cstat |= (CSTAT_SPRITE_YCENTER);

		wp.clipdist = 32 >> 2;
		HitscanSpriteAdjust(j, hitwall);

		if (RANDOM_P2(1024) < 100)
			PlaySound(DIGI_RICHOCHET1, wp, v3df_none);

		return (j);
	}

	public static int SpawnShotgunSparks(PlayerStr pp, int hitsect, int hitwall, int hitx, int hity, int hitz,
			int hitang) {
		short j;
		SPRITE wp;
		USER wu;

		j = (short) SpawnSprite(STAT_MISSILE, UZI_SPARK, s_UziSpark[0], hitsect, hitx, hity, hitz, hitang, 0);
		if(j == -1)
			return 0;

		wp = sprite[j];
		wu = pUser[j];
		wp.shade = -40;
		wp.xrepeat = UZI_SPARK_REPEAT;
		wp.yrepeat = UZI_SPARK_REPEAT;
		SetOwner(pp.PlayerSprite, j);
		wu.spal = (byte) (wp.pal = PALETTE_DEFAULT);
		wp.cstat |= (CSTAT_SPRITE_YCENTER);

		wp.clipdist = 32 >> 2;

		HitscanSpriteAdjust(j, hitwall);

		j = (short) SpawnSprite(STAT_MISSILE, SHOTGUN_SMOKE, s_ShotgunSmoke[0], hitsect, hitx, hity, hitz, hitang, 0);
		if(j == -1)
			return 0;

		wp = sprite[j];
		wp.shade = -40;
		wp.xrepeat = SHOTGUN_SMOKE_REPEAT;
		wp.yrepeat = SHOTGUN_SMOKE_REPEAT;
		SetOwner(pp.PlayerSprite, j);
		wp.cstat |= (CSTAT_SPRITE_TRANSLUCENT | CSTAT_SPRITE_YCENTER);

		wp.hitag = LUMINOUS; // Always full brightness
		// Sprite starts out with center exactly on wall.
		// This moves it back enough to see it at all angles.

		wp.clipdist = 32 >> 2;

		HitscanSpriteAdjust(j, hitwall);

		return (j);
	}

	public static int InitTurretMgun(Sector_Object sop) {
		SPRITE hsp;
		short daang, i, j;
		short hitsect, hitwall, hitsprite, nsect;
		int hitx, hity, hitz, daz;
		int nx, ny, nz;
		short delta;
		SPRITE sp;
		int xvect, yvect, zvect;

		PlaySound(DIGI_BOATFIRE, sop, v3df_dontpan | v3df_doppler);

		for (i = 0; sop.sp_num[i] != -1; i++) {
			if (sprite[sop.sp_num[i]].statnum == STAT_SO_SHOOT_POINT) {
				sp = sprite[sop.sp_num[i]];

				nx = sp.x;
				ny = sp.y;
				daz = nz = sp.z;
				nsect = sp.sectnum;

				// if its not operated by a player
				if (sop.Animator != null) {
					// only auto aim for Z
					daang = 512;

					hitsprite = (short) WeaponAutoAimHitscan(sop.sp_num[i], tmp_ptr[0].set(daz), tmp_ptr[1].set(daang),
							0);
					daz = tmp_ptr[0].value;
					daang = (short) tmp_ptr[1].value;

					if (hitsprite != -1) {
						delta = (short) klabs(GetDeltaAngle(daang, sp.ang));
						if (delta > 128) {
							// don't shoot if greater than 128
							return (0);
						} else if (delta > 24) {
							// always shoot the ground when tracking
							// and not close
							WeaponHitscanShootFeet(sp, sprite[hitsprite], tmp_ptr[0].set(daz));
							daz = tmp_ptr[0].value;
							daang = sp.ang;
							daang = NORM_ANGLE(daang + RANDOM_P2(32) - 16);
						} else {
							// randomize the z for shots
							daz += RANDOM_RANGE(Z(120)) - Z(60);
							// never auto aim the angle
							daang = sp.ang;
							daang = NORM_ANGLE(daang + RANDOM_P2(64) - 32);
						}
					}
				} else {
					daang = 64;
					if (WeaponAutoAimHitscan(sop.sp_num[i], tmp_ptr[0].set(daz), tmp_ptr[1].set(daang), 0) != -1) {
						daz = tmp_ptr[0].value;
						daang = (short) tmp_ptr[1].value;
						daz += RANDOM_RANGE(Z(30)) - Z(15);
					}
				}

				xvect = sintable[NORM_ANGLE(daang + 512)];
				yvect = sintable[NORM_ANGLE(daang)];
				zvect = daz;

				FAFhitscan(nx, ny, nz, nsect, // Start position
						xvect, yvect, zvect, pHitInfo, CLIPMASK_MISSILE);

				hitsect = pHitInfo.hitsect;
				hitwall = pHitInfo.hitwall;
				hitsprite = pHitInfo.hitsprite;
				hitx = pHitInfo.hitx;
				hity = pHitInfo.hity;
				hitz = pHitInfo.hitz;

				if (RANDOM_P2(1024) < 400) {
					InitTracerAutoTurret(sop.sp_num[i], -1, xvect >> 4, yvect >> 4, zvect >> 4);
				}

				if (hitsect < 0)
					continue;

				if (hitsprite < 0 && hitwall < 0) {
					if (klabs(hitz - sector[hitsect].ceilingz) <= Z(1)) {
						hitz += Z(16);

						if (TEST(sector[hitsect].ceilingstat, CEILING_STAT_PLAX))
							continue;
					} else if (klabs(hitz - sector[hitsect].floorz) <= Z(1)) {
						if (DTEST(sector[hitsect].extra, SECTFX_LIQUID_MASK) != SECTFX_LIQUID_NONE) {
							SpawnSplashXY(hitx, hity, hitz, hitsect);
							continue;
						}
					}

				}

				if (hitwall >= 0) {
					if (wall[hitwall].nextsector >= 0) {
						if (TEST(sector[wall[hitwall].nextsector].ceilingstat, CEILING_STAT_PLAX)) {
							if (hitz < sector[wall[hitwall].nextsector].ceilingz) {
								return (0);
							}
						}
					}

					if (wall[hitwall].lotag == TAG_WALL_BREAK) {
						HitBreakWall(hitwall, hitx, hity, hitz, daang, 0);
						continue;
					}

					QueueHole(daang, hitsect, hitwall, hitx, hity, hitz);
				}

				// hit a sprite?
				if (hitsprite >= 0) {
					hsp = sprite[hitsprite];

					if (hsp.lotag == TAG_SPRITE_HIT_MATCH) {
						if (MissileHitMatch(-1, WPN_UZI, hitsprite))
							continue;
					}

					if (TEST(hsp.extra, SPRX_BREAKABLE)) {
						HitBreakSprite(hitsprite, 0);
						continue;
					}

					if (BulletHitSprite(sop.sp_num[i], hitsprite, hitsect, hitwall, hitx, hity, hitz, 0))
						continue;

					// hit a switch?
					if (TEST(hsp.cstat, CSTAT_SPRITE_WALL) && (hsp.lotag != 0 || hsp.hitag != 0)) {
						ShootableSwitch(hitsprite, -1);
					}
				}

				j = (short) SpawnTurretSparks(sp, hitsect, hitwall, hitx, hity, hitz, daang);
				DoHitscanDamage(j, hitsprite);
			}
		}

		return (0);
	}

	private static short alternate;

	public static int InitEnemyUzi(int SpriteNum) {
		SPRITE sp = pUser[SpriteNum].getSprite(), wp;
		USER u = pUser[SpriteNum];
		USER wu;
		short daang = 0, j;
		short hitsect = -2, hitwall = -2, hitsprite = -2;
		int hitx = -2, hity = -2, hitz = -2, daz;
		int zh;

		// Make sprite shade brighter
		u.Vis = 128;

		engine.setspritez(SpriteNum, sp.x, sp.y, sp.z);

		if (u.ID == ZILLA_RUN_R0) {
			zh = SPRITEp_TOS(sp);
			zh += Z(20);
		} else {
			zh = SPRITEp_SIZE_Z(sp);
			zh -= DIV4(zh);
		}
		daz = sp.z - zh;

		if (AimHitscanToTarget(u.SpriteNum, tmp_ptr[0].set(daz), tmp_ptr[1].set(daang), 200) != -1) {
			// set angle to player and also face player when attacking
			daz = tmp_ptr[0].value;
			daang = (short) tmp_ptr[1].value;

			sp.ang = daang;
			daang += RANDOM_RANGE(24) - 12;
			daang = NORM_ANGLE(daang);
			daz += RANDOM_RANGE(Z(40)) - Z(20);
		} else {
			daz = tmp_ptr[0].value;
			daang = (short) tmp_ptr[1].value;

			// couldn't shoot target for some reason

			// don't bother wasting processing 50% of the time
			if (RANDOM_P2(1024) < 512)
				return (0);

			daz = 0;
			daang = NORM_ANGLE(sp.ang + (RANDOM_P2(128)) - 64);
		}

		FAFhitscan(sp.x, sp.y, sp.z - zh, sp.sectnum, // Start position
				sintable[NORM_ANGLE(daang + 512)], // X vector of 3D ang
				sintable[NORM_ANGLE(daang)], // Y vector of 3D ang
				daz, // Z vector of 3D ang
				pHitInfo, CLIPMASK_MISSILE);

		hitsect = pHitInfo.hitsect;
		hitwall = pHitInfo.hitwall;
		hitsprite = pHitInfo.hitsprite;
		hitx = pHitInfo.hitx;
		hity = pHitInfo.hity;
		hitz = pHitInfo.hitz;

		if (hitsect < 0)
			return (0);

		if (RANDOM_P2(1024 << 4) >> 4 > 700) {
			if (u.ID == TOILETGIRL_R0 || u.ID == WASHGIRL_R0 || u.ID == CARGIRL_R0)
				SpawnShell(SpriteNum, -3);
			else
				SpawnShell(SpriteNum, -2); // Enemy Uzi shell
		}

		if ((alternate++) > 2)
			alternate = 0;
		if (alternate == 0) {
			if (sp.pal == PALETTE_PLAYER3 || sp.pal == PALETTE_PLAYER5 || sp.pal == PAL_XLAT_LT_GREY
					|| sp.pal == PAL_XLAT_LT_TAN)
				PlaySound(DIGI_M60, sp, v3df_none);
			else
				PlaySound(DIGI_NINJAUZIATTACK, sp, v3df_none);
		}

		if (hitwall >= 0) {
			if (wall[hitwall].nextsector >= 0) {
				if (TEST(sector[wall[hitwall].nextsector].ceilingstat, CEILING_STAT_PLAX)) {
					if (hitz < sector[wall[hitwall].nextsector].ceilingz) {
						return (0);
					}
				}
			}

			if (wall[hitwall].lotag == TAG_WALL_BREAK) {
				HitBreakWall(hitwall, hitx, hity, hitz, daang, u.ID);
				return (0);
			}

			QueueHole(daang, hitsect, hitwall, hitx, hity, hitz);
		}

		if (hitsprite >= 0) {
			if (BulletHitSprite(u.SpriteNum, hitsprite, hitsect, hitwall, hitx, hity, hitz, 0))
				return (0);
		}

		j = (short) SpawnSprite(STAT_MISSILE, UZI_SMOKE + 2, s_UziSmoke[0], hitsect, hitx, hity, hitz, daang, 0);
		if(j == -1)
			return 0;

		wp = sprite[j];
		wp.shade = -40;
		wp.xrepeat = UZI_SMOKE_REPEAT;
		wp.yrepeat = UZI_SMOKE_REPEAT;

		if (u.ID == ZOMBIE_RUN_R0)
			SetOwner(sp.owner, j);
		else
			SetOwner(SpriteNum, j);

		pUser[j].WaitTics = 63;
		wp.cstat |= (CSTAT_SPRITE_YCENTER);

		wp.clipdist = (32 >> 2);

		j = (short) SpawnSprite(STAT_MISSILE, UZI_SMOKE, s_UziSmoke[0], hitsect, hitx, hity, hitz, daang, 0);
		if(j == -1)
			return 0;

		wp = sprite[j];
		wp.shade = -40;
		wp.xrepeat = UZI_SMOKE_REPEAT;
		wp.yrepeat = UZI_SMOKE_REPEAT;
		SetOwner(SpriteNum, j);
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.clipdist = 8 >> 2;

		HitscanSpriteAdjust(j, hitwall);
		DoHitscanDamage(j, hitsprite);

		j = (short) SpawnSprite(STAT_MISSILE, UZI_SPARK, s_UziSpark[0], hitsect, hitx, hity, hitz, daang, 0);
		if(j == -1)
			return 0;

		wp = sprite[j];
		wu = pUser[j];
		wp.shade = -40;
		wp.xrepeat = UZI_SPARK_REPEAT;
		wp.yrepeat = UZI_SPARK_REPEAT;
		SetOwner(SpriteNum, j);
		wu.spal = (byte) wp.pal;
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.clipdist = 8 >> 2;

		HitscanSpriteAdjust(j, hitwall);

		if (RANDOM_P2(1024) < 100) {
			PlaySound(DIGI_RICHOCHET1, wp, v3df_none);
		} else if (RANDOM_P2(1024) < 100)
			PlaySound(DIGI_RICHOCHET2, wp, v3df_none);

		return (0);
	}

	public static int InitGrenade(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		USER wu;
		SPRITE wp;
		int nx, ny, nz;
		short w;
		short oclipdist;
		int zvel;
		boolean auto_aim = false;

		DoPlayerBeginRecoil(pp, GRENADE_RECOIL_AMT);

		PlayerUpdateAmmo(pp, u.WeaponNum, -1);

		PlaySound(DIGI_30MMFIRE, pp, v3df_dontpan | v3df_doppler);

		// Make sprite shade brighter
		u.Vis = 128;

		nx = pp.posx;
		ny = pp.posy;
		nz = pp.posz + pp.bob_z + Z(8);

		// Spawn a shot
		// Inserting and setting up variables
		w = (short) SpawnSprite(STAT_MISSILE, GRENADE, s_Grenade[0][0], pp.cursectnum, nx, ny, nz, pp.getAnglei(),
				GRENADE_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		// don't throw it as far if crawling
		if (TEST(pp.Flags, PF_CRAWLING)) {
			wp.xvel -= DIV4(wp.xvel);
		}

		wu.RotNum = 5;
		NewStateGroup(w, WeaponStateGroup.sg_Grenade);
		wu.Flags |= (SPR_XFLIP_TOGGLE);

		SetOwner(pp.PlayerSprite, w);
		wp.yrepeat = 32;
		wp.xrepeat = 32;
		wp.shade = -15;
		// wp.clipdist = 80L>>2;
		wp.clipdist = 32 >> 2;
		wu.WeaponNum = u.WeaponNum;
		wu.Radius = 200;
		wu.ceiling_dist = (short) Z(3);
		wu.floor_dist = (short) Z(3);
		wu.Counter = 0;
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.cstat |= (CSTAT_SPRITE_BLOCK);

		if (TEST(pp.Flags, PF_DIVING) || SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		wp.zvel = (short) ((100 - pp.getHorizi()) * HORIZ_MULT);

		SPRITE psp = pp.getSprite();
		oclipdist = (short) psp.clipdist;
		psp.clipdist = 0;

		wp.ang = NORM_ANGLE(wp.ang + 512);
		HelpMissileLateral(w, 800);
		wp.ang = NORM_ANGLE(wp.ang - 512);

		// don't do smoke for this movement
		wu.Flags |= (SPR_BOUNCE);
		MissileSetPos(w, DoGrenade, 1000);
		wu.Flags &= ~(SPR_BOUNCE);

		psp.clipdist = oclipdist;

        zvel = wp.zvel;
		if (WeaponAutoAim(pp.PlayerSprite, w, 32, 0) >= 0) {
			auto_aim = true;
		}
		wp.zvel = (short) zvel;

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		if (!auto_aim) {
			// adjust xvel according to player velocity
			wu.xchange += pp.xvect >> 14;
			wu.ychange += pp.yvect >> 14;
		}

		wu.Counter2 = 1; // Phosphorus Grenade

		return (0);
	}

	public static int InitSpriteGrenade(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		USER wu;
		SPRITE wp;
		int nx, ny, nz;
		short w;

		PlaySound(DIGI_30MMFIRE, sp, v3df_dontpan | v3df_doppler);

		nx = sp.x;
		ny = sp.y;
		nz = sp.z - Z(40);

		// Spawn a shot
		// Inserting and setting up variables
		w = (short) SpawnSprite(STAT_MISSILE, GRENADE, s_Grenade[0][0], sp.sectnum, nx, ny, nz, sp.ang,
				GRENADE_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		wu.RotNum = 5;
		NewStateGroup(w, WeaponStateGroup.sg_Grenade);
		wu.Flags |= (SPR_XFLIP_TOGGLE);

		if (u.ID == ZOMBIE_RUN_R0)
			SetOwner(sp.owner, w);
		else
			SetOwner(SpriteNum, w);
		wp.yrepeat = 32;
		wp.xrepeat = 32;
		wp.shade = -15;
		// wp.clipdist = 80L>>2;
		wp.clipdist = 32 >> 2;
		wu.WeaponNum = u.WeaponNum;
		wu.Radius = 200;
		wu.ceiling_dist = (short) Z(3);
		wu.floor_dist = (short) Z(3);
		wu.Counter = 0;
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.cstat |= (CSTAT_SPRITE_BLOCK);

		// wp.zvel = (-RANDOM_RANGE(100) * HORIZ_MULT);
		wp.zvel = -2000;

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		wp.ang = NORM_ANGLE(wp.ang + 512);
		HelpMissileLateral(w, 800);
		wp.ang = NORM_ANGLE(wp.ang - 512);

		// don't do smoke for this movement
		wu.Flags |= (SPR_BOUNCE);
		MissileSetPos(w, DoGrenade, 400);
		wu.Flags &= ~(SPR_BOUNCE);

		return (0);
	}

	public static int InitMine(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		USER wu;
		SPRITE wp;
		int nx, ny, nz;
		short w;
		int dot;

		PlayerUpdateAmmo(pp, u.WeaponNum, -1);

		PlaySound(DIGI_MINETHROW, pp, v3df_dontpan | v3df_doppler);

		nx = pp.posx;
		ny = pp.posy;
		nz = pp.posz + pp.bob_z + Z(8);

		// Spawn a shot
		// Inserting and setting up variables
		w = (short) SpawnSprite(STAT_MISSILE, MINE, s_Mine[0], pp.cursectnum, nx, ny, nz, pp.getAnglei(), MINE_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		SetOwner(pp.PlayerSprite, w);
		wp.yrepeat = 32;
		wp.xrepeat = 32;
		wp.shade = -15;
		wp.clipdist = 128 >> 2;
		wp.zvel = (short) ((100 - pp.getHorizi()) * HORIZ_MULT);
		wu.WeaponNum = u.WeaponNum;
		wu.Radius = 200;
		wu.ceiling_dist = (short) Z(5);
		wu.floor_dist = (short) Z(5);
		wu.Counter = 0;
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		wu.spal = (byte) (wp.pal = pUser[pp.PlayerSprite].spal); // Set sticky color

		if (TEST(pp.Flags, PF_DIVING) || SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		MissileSetPos(w, DoMine, 800);

		wu.zchange = wp.zvel >> 1;
		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);

		dot = DOT_PRODUCT_2D(pp.xvect, pp.yvect, sintable[NORM_ANGLE(pp.getAnglei() + 512)], sintable[pp.getAnglei()]);

		// don't adjust for strafing
		if (klabs(dot) > 10000) {
			// adjust xvel according to player velocity
			wu.xchange += pp.xvect >> 13;
			wu.ychange += pp.yvect >> 13;
		}

		return (0);
	}

	public static int InitEnemyMine(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		USER wu;
		SPRITE wp;
		int nx, ny, nz;
		short w;

		PlaySound(DIGI_MINETHROW, sp, v3df_dontpan | v3df_doppler);

		nx = sp.x;
		ny = sp.y;
		nz = sp.z - Z(40);

		// Spawn a shot
		// Inserting and setting up variables
		w = (short) SpawnSprite(STAT_MISSILE, MINE, s_Mine[0], sp.sectnum, nx, ny, nz, sp.ang, MINE_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		SetOwner(SpriteNum, w);
		wp.yrepeat = 32;
		wp.xrepeat = 32;
		wp.shade = -15;
		wp.clipdist = 128 >> 2;

		// (velocity * difference between the target and the object) /
		// distance

		wu.WeaponNum = u.WeaponNum;
		wu.Radius = 200;
		wu.ceiling_dist = (short) Z(5);
		wu.floor_dist = (short) Z(5);
		wu.Counter = 0;
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		wu.spal = (byte) (wp.pal = pUser[SpriteNum].spal); // Set sticky color

		if (SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		MissileSetPos(w, DoMine, 300);
		wp.ang = NORM_ANGLE(wp.ang - 512);
		MissileSetPos(w, DoMine, 300);
		wp.ang = NORM_ANGLE(wp.ang + 512);

		wu.zchange = -5000;
		// CON_Message("change = %ld",wu.zchange);
		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);

		return (0);
	}

	public static int HelpMissileLateral(int Weapon, int dist) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		int xchange, ychange;
		short old_xvel;
		short old_clipdist;

		old_xvel = sp.xvel;
		old_clipdist = (short) sp.clipdist;

		sp.xvel = (short) dist;
		xchange = MOVEx(sp.xvel, sp.ang);
		ychange = MOVEy(sp.xvel, sp.ang);

		sp.clipdist = 32 >> 2;

        u.ret = move_missile(Weapon, xchange, ychange, 0, Z(16), Z(16), 0, 1);

		sp.xvel = old_xvel;
		sp.clipdist = old_clipdist;

		u.ox = sp.x;
		u.oy = sp.y;
		u.oz = sp.z;
		return (0);
	}

	public static int InitFireball(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		SPRITE wp;
		int nx = 0, ny = 0, nz;
		short w;
		USER wu;
		short oclipdist;
		int zvel;

		PlayerUpdateAmmo(pp, WPN_HOTHEAD, -1);

		PlaySound(DIGI_HEADFIRE, pp, v3df_none);

		// Make sprite shade brighter
		u.Vis = 128;

		nx += pp.posx;
		ny += pp.posy;

		nz = pp.posz + pp.bob_z + Z(15);

		w = (short) SpawnSprite(STAT_MISSILE, FIREBALL1, s_Fireball[0], pp.cursectnum, nx, ny, nz, pp.getAnglei(),
				FIREBALL_VELOCITY);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		wp.hitag = LUMINOUS; // Always full brightness
		wp.xrepeat = 40;
		wp.yrepeat = 40;
		wp.shade = -40;
		wp.clipdist = 32 >> 2;
		SetOwner(pp.PlayerSprite, w);
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wu.Radius = 100;

		wu.ceiling_dist = (short) Z(6);
		wu.floor_dist = (short) Z(6);
		zvel = ((100 - pp.getHorizi()) * (240));

		// wu.RotNum = 5;
		// NewStateGroup(w, &sg_Fireball);
		// SET(wu.Flags, SPR_XFLIP_TOGGLE);

		// at certain angles the clipping box was big enough to block the
		// initial positioning of the fireball.
		SPRITE psp = pp.getSprite();
		oclipdist = (short) psp.clipdist;
		psp.clipdist = 0;

		wp.ang = NORM_ANGLE(wp.ang + 512);
		HelpMissileLateral(w, 2100);
		wp.ang = NORM_ANGLE(wp.ang - 512);

		if (TEST(pp.Flags, PF_DIVING) || SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		if (TestMissileSetPos(w, DoFireball, 1200, mulscale(zvel, 44000, 16))) {
			psp.clipdist = oclipdist;
			KillSprite(w);
			return (0);
		}

		psp.clipdist = oclipdist;

		wp.zvel = (short) (zvel >> 1);
		if (WeaponAutoAimZvel(pp.PlayerSprite, w, tmp_ptr[0].set(zvel), 32, 0) == -1) {
			wp.ang = NORM_ANGLE(wp.ang - 9);
		}

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = zvel;

		return (0);
	}

	public static int InitEnemyFireball(int SpriteNum) {
		SPRITE sp = pUser[SpriteNum].getSprite(), fp = null;
		USER u = pUser[SpriteNum];
		SPRITE wp;
		int nz, dist;
		int size_z;
		short w;
		USER wu;
		SPRITE tsp;
		int i, targ_z, xchange, ychange;

		tsp = sprite[u.tgt_sp];

		PlaySound(DIGI_FIREBALL1, sp, v3df_none);

		// get angle to player and also face player when attacking
		sp.ang = NORM_ANGLE(engine.getangle(tsp.x - sp.x, tsp.y - sp.y));

		size_z = Z(SPRITEp_SIZE_Y(sp));
		// nz = sp.z - size_z + DIV4(size_z) + DIV8(size_z);
		nz = sp.z - size_z + DIV4(size_z) + DIV8(size_z) + Z(4);

		xchange = MOVEx(GORO_FIREBALL_VELOCITY, sp.ang);
		ychange = MOVEy(GORO_FIREBALL_VELOCITY, sp.ang);

		for (i = 0; i < 2; i++) {
			w = (short) SpawnSprite(STAT_MISSILE, GORO_FIREBALL, s_Fireball[0], sp.sectnum, sp.x, sp.y, nz, sp.ang,
					GORO_FIREBALL_VELOCITY);
			if(w == -1)
				return 0;

			wp = sprite[w];
			wu = pUser[w];

			wp.hitag = LUMINOUS; // Always full brightness
			wp.xrepeat = 20;
			wp.yrepeat = 20;
			wp.shade = -40;
			// wp.owner = SpriteNum;
			SetOwner(SpriteNum, w);
			wp.zvel = 0;
			wp.clipdist = 16 >> 2;

			wp.ang = NORM_ANGLE(wp.ang + lat_ang[i]);
			HelpMissileLateral(w, 500);
			wp.ang = NORM_ANGLE(wp.ang - lat_ang[i]);

			wu.xchange = xchange;
			wu.ychange = ychange;

			// MissileSetPos(w, DoFireball, 700);
			MissileSetPos(w, DoFireball, 700);

			if (i == 0) {
				// back up first one
				fp = wp;

				// find the distance to the target (player)
				dist = engine.ksqrt(SQ(wp.x - tsp.x) + SQ(wp.y - tsp.y));
				// dist = Distance(wp.x, wp.y, tsp.x, tsp.y);

				// Determine target Z value
                targ_z = tsp.z - DIV2(Z(SPRITEp_SIZE_Y(sp)));

				// (velocity * difference between the target and the throwing star) /
				// distance
				if (dist != 0)
					wu.zchange = wp.zvel = (short) ((GORO_FIREBALL_VELOCITY * (targ_z - wp.z)) / dist);
			} else {
				// use the first calculations so the balls stay together
				wu.zchange = wp.zvel = fp.zvel;
			}
		}

		return (0);

	}

///////////////////////////////////////////////////////////////////////////////
//for hitscans or other uses
///////////////////////////////////////////////////////////////////////////////

	public static boolean WarpToUnderwater(LONGp sectnum, LONGp x, LONGp y, LONGp z) {
		short i, nexti;
		Sect_User sectu = SectUser[sectnum.value];
		SPRITE under_sp = null, over_sp = null;
		boolean Found = false;

		int sx, sy;

		// 0 not valid for water match tags
		if (sectu.number == 0)
			return (false);

		// search for DIVE_AREA "over" sprite for reference point
		for (i = headspritestat[STAT_DIVE_AREA]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			over_sp = sprite[i];

			if (TEST(sector[over_sp.sectnum].extra, SECTFX_DIVE_AREA) && SectUser[over_sp.sectnum] != null
					&& SectUser[over_sp.sectnum].number == sectu.number) {
				Found = true;
				break;
			}
		}

		if (!Found)
			return false;

		Found = false;

		// search for UNDERWATER "under" sprite for reference point
		for (i = headspritestat[STAT_UNDERWATER]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			under_sp = sprite[i];

			if (TEST(sector[under_sp.sectnum].extra, SECTFX_UNDERWATER) && SectUser[under_sp.sectnum] != null
					&& SectUser[under_sp.sectnum].number == sectu.number) {
				Found = true;
				break;
			}
		}

		// get the offset from the sprite
		sx = over_sp.x - x.value;
		sy = over_sp.y - y.value;

		// update to the new x y position
		x.value = under_sp.x - sx;
		y.value = under_sp.y - sy;

		if (GetOverlapSector(x.value, y.value, tmp_ptr[0].set(over_sp.sectnum),
				tmp_ptr[1].set(under_sp.sectnum)) == 2) {
			sectnum.value = tmp_ptr[1].value;
		} else {
			sectnum.value = tmp_ptr[1].value;
		}

		z.value = sector[under_sp.sectnum].ceilingz + Z(1);
		return (true);
	}

	public static boolean WarpToSurface(LONGp sectnum, LONGp x, LONGp y, LONGp z) {
		short i, nexti;
		Sect_User sectu = SectUser[sectnum.value];
		int sx, sy;

		SPRITE under_sp = null, over_sp = null;
		boolean Found = false;

		// 0 not valid for water match tags
		if (sectu.number == 0)
			return (false);

		// search for UNDERWATER "under" sprite for reference point
		for (i = headspritestat[STAT_UNDERWATER]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			under_sp = sprite[i];

			if (TEST(sector[under_sp.sectnum].extra, SECTFX_UNDERWATER) && SectUser[under_sp.sectnum] != null
					&& SectUser[under_sp.sectnum].number == sectu.number) {
				Found = true;
				break;
			}
		}

		if (!Found)
			return false;

		Found = false;

		// search for DIVE_AREA "over" sprite for reference point
		for (i = headspritestat[STAT_DIVE_AREA]; i != -1; i = nexti) {
			nexti = nextspritestat[i];

			over_sp = sprite[i];

			if (TEST(sector[over_sp.sectnum].extra, SECTFX_DIVE_AREA) && SectUser[over_sp.sectnum] != null
					&& SectUser[over_sp.sectnum].number == sectu.number) {
				Found = true;
				break;
			}
		}

		// get the offset from the under sprite
		sx = under_sp.x - x.value;
		sy = under_sp.y - y.value;

		// update to the new x y position
		x.value = over_sp.x - sx;
		y.value = over_sp.y - sy;

		if (GetOverlapSector(x.value, y.value, tmp_ptr[0].set(over_sp.sectnum),
				tmp_ptr[1].set(under_sp.sectnum)) != 0) {
			sectnum.value = tmp_ptr[0].value;
		}

		z.value = sector[over_sp.sectnum].floorz - Z(2);

		return (true);
	}

	public static boolean SpriteWarpToUnderwater(int spi) {
		SPRITE sp = sprite[spi];

		USER u = pUser[spi];
		short i, nexti;
		Sect_User sectu = SectUser[sp.sectnum];
		SPRITE under_sp = null, over_sp = null;
		boolean Found = false;
		int sx, sy;

		// 0 not valid for water match tags
		if (sectu.number == 0)
			return (false);

		// search for DIVE_AREA "over" sprite for reference point
		for (i = headspritestat[STAT_DIVE_AREA]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			over_sp = sprite[i];

			if (TEST(sector[over_sp.sectnum].extra, SECTFX_DIVE_AREA) && SectUser[over_sp.sectnum] != null
					&& SectUser[over_sp.sectnum].number == sectu.number) {
				Found = true;
				break;
			}
		}

		if (!Found)
			return false;

		Found = false;

		// search for UNDERWATER "under" sprite for reference point
		for (i = headspritestat[STAT_UNDERWATER]; i != -1; i = nexti) {
			nexti = nextspritestat[i];

			under_sp = sprite[i];

			if (TEST(sector[under_sp.sectnum].extra, SECTFX_UNDERWATER) && SectUser[under_sp.sectnum] != null
					&& SectUser[under_sp.sectnum].number == sectu.number) {
				Found = true;
				break;
			}
		}

		if(!Found)
			return false;

		// get the offset from the sprite
		sx = over_sp.x - sp.x;
		sy = over_sp.y - sp.y;

		// update to the new x y position
		sp.x = under_sp.x - sx;
		sp.y = under_sp.y - sy;

		if (GetOverlapSector(sp.x, sp.y, tmp_ptr[0].set(over_sp.sectnum), tmp_ptr[1].set(under_sp.sectnum)) == 2) {
			engine.changespritesect(spi, (short) tmp_ptr[1].value);
		} else {
			engine.changespritesect(spi, (short) tmp_ptr[0].value);
		}

		// sp.z = sector[under_sp.sectnum].ceilingz + Z(6);
		sp.z = sector[under_sp.sectnum].ceilingz + u.ceiling_dist + Z(1);

		u.ox = sp.x;
		u.oy = sp.y;
		u.oz = sp.z;

		return (true);
	}

	public static boolean SpriteWarpToSurface(int spi) {
		SPRITE sp = sprite[spi];

		USER u = pUser[spi];
		short i, nexti;
		Sect_User sectu = SectUser[sp.sectnum];

		int sx, sy;

		SPRITE under_sp = null, over_sp = null;
		boolean Found = false;

		// 0 not valid for water match tags
		if (sectu.number == 0)
			return (false);

		// search for UNDERWATER "under" sprite for reference point
		for (i = headspritestat[STAT_UNDERWATER]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			under_sp = sprite[i];

			if (TEST(sector[under_sp.sectnum].extra, SECTFX_UNDERWATER) && SectUser[under_sp.sectnum] != null
					&& SectUser[under_sp.sectnum].number == sectu.number) {
				Found = true;
				break;
			}
		}

		if (!Found)
			return false;

		if (under_sp.lotag == 0)
			return (false);

		Found = false;

		// search for DIVE_AREA "over" sprite for reference point
		for (i = headspritestat[STAT_DIVE_AREA]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			over_sp = sprite[i];

			if (TEST(sector[over_sp.sectnum].extra, SECTFX_DIVE_AREA) && SectUser[over_sp.sectnum] != null
					&& SectUser[over_sp.sectnum].number == sectu.number) {
				Found = true;
				break;
			}
		}

		// get the offset from the under sprite
		sx = under_sp.x - sp.x;
		sy = under_sp.y - sp.y;

		// update to the new x y position
		sp.x = over_sp.x - sx;
		sp.y = over_sp.y - sy;

		if (GetOverlapSector(sp.x, sp.y, tmp_ptr[0].set(over_sp.sectnum), tmp_ptr[1].set(under_sp.sectnum)) != 0) {

			engine.changespritesect(spi, (short) tmp_ptr[0].value);
		}

		sp.z = sector[over_sp.sectnum].floorz - Z(2);

		// set z range and wade depth so we know how high to set view
		DoActorZrange(spi);
		MissileWaterAdjust(spi);

		u.ox = sp.x;
		u.oy = sp.y;
		u.oz = sp.z;

		return (true);
	}

	public static int SpawnSplash(int SpriteNum) {
		USER u = pUser[SpriteNum], wu;
		SPRITE sp = pUser[SpriteNum].getSprite(), wp;
		short w;

		Sect_User sectu = SectUser[sp.sectnum];
		SECTOR sectp = sector[sp.sectnum];

		if (Prediction)
			return (0);

		if (sectu != null && (DTEST(sectp.extra, SECTFX_LIQUID_MASK) == SECTFX_LIQUID_NONE))
			return (0);

		if (sectu != null && TEST(sectp.floorstat, FLOOR_STAT_PLAX))
			return (0);

		PlaySound(DIGI_SPLASH1, sp, v3df_none);

		DoActorZrange(SpriteNum);
		MissileWaterAdjust(SpriteNum);

		w = (short) SpawnSprite(STAT_MISSILE, SPLASH, s_Splash[0], sp.sectnum, sp.x, sp.y, u.loz, sp.ang, 0);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		if (sectu != null && DTEST(sectp.extra, SECTFX_LIQUID_MASK) == SECTFX_LIQUID_LAVA)
			wu.spal = (byte) (wp.pal = PALETTE_RED_LIGHTING);

		wp.xrepeat = 45;
		wp.yrepeat = 42;
		wp.shade = (byte) (sector[sp.sectnum].floorshade - 10);

		return (0);
	}

	public static int SpawnSplashXY(int hitx, int hity, int hitz, short sectnum) {
		USER wu;
		SPRITE wp;
		short w;

		Sect_User sectu;
		SECTOR sectp;

		if (Prediction)
			return (0);

		sectu = SectUser[sectnum];
		sectp = sector[sectnum];

		if (sectu != null && (DTEST(sectp.extra, SECTFX_LIQUID_MASK) == SECTFX_LIQUID_NONE))
			return (0);

		if (sectu != null && TEST(sectp.floorstat, FLOOR_STAT_PLAX))
			return (0);

		w = (short) SpawnSprite(STAT_MISSILE, SPLASH, s_Splash[0], sectnum, hitx, hity, hitz, 0, 0);
		if(w == -1)
			return 0;

		wp = sprite[w];
		wu = pUser[w];

		if (sectu != null && DTEST(sectp.extra, SECTFX_LIQUID_MASK) == SECTFX_LIQUID_LAVA)
			wu.spal = (byte) (wp.pal = PALETTE_RED_LIGHTING);

		wp.xrepeat = 45;
		wp.yrepeat = 42;
		wp.shade = (byte) (sector[wp.sectnum].floorshade - 10);

		return (0);
	}

	public static int SpawnUnderSplash(int SpriteNum) {
		return (0);

    }

	public static boolean MissileHitDiveArea(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();
		short hitsect;

		// correctly set underwater bit for missiles
		// in Stacked water areas.
		if (FAF_ConnectArea(sp.sectnum)) {
			if (SectorIsUnderwaterArea(sp.sectnum))
				u.Flags |= (SPR_UNDERWATER);
			else
				u.Flags &= ~(SPR_UNDERWATER);
		}

		if (u.ret == 0)
			return (false);

		switch (DTEST(u.ret, HIT_MASK)) {
		case HIT_SECTOR: {
			hitsect = NORM_SECTOR(u.ret);

			if (SpriteInDiveArea(sp)) {
				// make sure you are close to the floor
				if (sp.z < DIV2(u.hiz + u.loz))
					return (false);

				// Check added by Jim because of sprite bridge over water
				if (sp.z < (sector[hitsect].floorz - Z(20)))
					return (false);

				u.Flags |= (SPR_UNDERWATER);
				SpawnSplash(u.SpriteNum);
				SpriteWarpToUnderwater(u.SpriteNum);
				u.ret = 0;
				PlaySound(DIGI_PROJECTILEWATERHIT, sp, v3df_none);
				return (true);
			} else if (SpriteInUnderwaterArea(sp)) {
				// make sure you are close to the ceiling
				if (sp.z > DIV2(u.hiz + u.loz))
					return (false);

				u.Flags &= ~(SPR_UNDERWATER);
				if (!SpriteWarpToSurface(u.SpriteNum)) {
					return (false);
				}
				SpawnSplash(u.SpriteNum);
				u.ret = 0;
				return (true);
			}

			break;
		}
		}

		return (false);
	}

	public static int SpawnBubble(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], bp;

		if (Prediction)
			return (-1);

		int b = SpawnSprite(STAT_MISSILE, BUBBLE, s_Bubble[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang, 0);
		if(b == -1)
			return 0;

		bp = sprite[b];
		USER bu = pUser[b];

		// PlaySound(DIGI_BUBBLES, &sp.x, &sp.y, &sp.z, v3df_none);

		bp.xrepeat = (short) (8 + (RANDOM_P2(8 << 8) >> 8));
		bp.yrepeat = bp.xrepeat;
		bu.sx = bp.xrepeat;
		bu.sy = bp.yrepeat;
		bu.ceiling_dist = (short) Z(1);
		bu.floor_dist = (short) Z(1);
		bp.shade = (byte) (sector[sp.sectnum].floorshade - 10);
		bu.WaitTics = 120 * 120;
		bp.zvel = 512;
		bp.clipdist = 12 >> 2;
		bp.cstat |= (CSTAT_SPRITE_YCENTER);
		bu.Flags |= (SPR_UNDERWATER);
		bp.shade = -60; // Make em brighter

		return (b);
	}

	public static boolean DoVehicleSmoke(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		sp.z -= sp.zvel;
		sp.x += u.xchange;
		sp.y += u.ychange;

		return (false);
	}

	public static boolean DoWaterSmoke(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		sp.z -= sp.zvel;

		return (false);
	}

	public static boolean SpawnVehicleSmoke(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];

		if (MoveSkip2)
			return (false);

		short newsp = (short) SpawnSprite(STAT_MISSILE, PUFF, s_VehicleSmoke[0], sp.sectnum, sp.x, sp.y,
				sp.z - RANDOM_P2(Z(8)), sp.ang, 0);
		if(newsp == -1)
			return false;

		SPRITE np = sprite[newsp];
		USER nu = pUser[newsp];

		nu.WaitTics = 1 * 120;
		np.shade = -40;
		np.xrepeat = 64;
		np.yrepeat = 64;
		np.cstat |= (CSTAT_SPRITE_YCENTER);
		np.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		if (RANDOM_P2(1024) < 512)
			np.cstat |= (CSTAT_SPRITE_XFLIP);
		if (RANDOM_P2(1024) < 512)
			np.cstat |= (CSTAT_SPRITE_YFLIP);

		np.ang = (short) RANDOM_P2(2048);
		np.xvel = (short) RANDOM_P2(32);
		nu.xchange = MOVEx(np.xvel, np.ang);
		nu.ychange = MOVEy(np.xvel, np.ang);
		np.zvel = (short) (Z(4) + RANDOM_P2(Z(4)));

		return (false);
	}

	public static boolean SpawnSmokePuff(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];

		short newsp = (short) SpawnSprite(STAT_MISSILE, PUFF, s_WaterSmoke[0], sp.sectnum, sp.x, sp.y,
				sp.z - RANDOM_P2(Z(8)), sp.ang, 0);
		if(newsp == -1)
			return false;

		SPRITE np = sprite[newsp];
		USER nu = pUser[newsp];

		nu.WaitTics = 1 * 120;
		np.shade = -40;
		np.xrepeat = 64;
		np.yrepeat = 64;
		np.cstat |= (CSTAT_SPRITE_YCENTER);
		np.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		if (RANDOM_P2(1024) < 512)
			np.cstat |= (CSTAT_SPRITE_XFLIP);
		if (RANDOM_P2(1024) < 512)
			np.cstat |= (CSTAT_SPRITE_YFLIP);

		np.ang = (short) RANDOM_P2(2048);
		np.xvel = (short) RANDOM_P2(32);
		nu.xchange = MOVEx(np.xvel, np.ang);
		nu.ychange = MOVEy(np.xvel, np.ang);
		np.zvel = (short) (Z(1) + RANDOM_P2(Z(2)));

		return (false);
	}

	public static boolean DoBubble(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		sp.z -= sp.zvel;
		sp.zvel += 32;

		if (sp.zvel > 768)
			sp.zvel = 768;

		u.sx += 1;
		u.sy += 1;

		if (u.sx > 32) {
			u.sx = 32;
			u.sy = 32;
		}

		sp.xrepeat = (short) (u.sx + (RANDOM_P2(8 << 8) >> 8) - 4);
		sp.yrepeat = (short) (u.sy + (RANDOM_P2(8 << 8) >> 8) - 4);

		if (sp.z < sector[sp.sectnum].ceilingz) {
			if (SectorIsUnderwaterArea(u.hi_sectp)) {
				if (!SpriteWarpToSurface(SpriteNum)) {
					KillSprite(SpriteNum);
					return (true);
				}

				u.Flags &= ~(SPR_UNDERWATER);
				// stick around above water for this long
				u.WaitTics = (short) (RANDOM_P2(64 << 8) >> 8);
			} else {
				KillSprite(SpriteNum);
				return (true);
			}
		}

		if (!TEST(u.Flags, SPR_UNDERWATER)) {
			if ((u.WaitTics -= MISSILEMOVETICS) <= 0) {
				KillSprite(SpriteNum);
				return (true);
			}
		} else
		// just in case its stuck somewhere kill it after a while
		{
			if ((u.WaitTics -= MISSILEMOVETICS) <= 0) {
				KillSprite(SpriteNum);
				return (true);
			}
		}

		return (false);
	}

// this needs to be called before killsprite
// whenever killing a sprite that you aren't completely sure what it is, like
// with the drivables, copy sectors, break sprites, etc
	public static void SpriteQueueDelete(int SpriteNum) {
		int i;

		for (i = 0; i < MAX_STAR_QUEUE; i++)
			if (StarQueue[i] == SpriteNum)
				StarQueue[i] = -1;

		for (i = 0; i < MAX_HOLE_QUEUE; i++)
			if (HoleQueue[i] == SpriteNum)
				HoleQueue[i] = -1;

		for (i = 0; i < MAX_WALLBLOOD_QUEUE; i++)
			if (WallBloodQueue[i] == SpriteNum)
				WallBloodQueue[i] = -1;

		for (i = 0; i < MAX_FLOORBLOOD_QUEUE; i++)
			if (FloorBloodQueue[i] == SpriteNum)
				FloorBloodQueue[i] = -1;

		for (i = 0; i < MAX_GENERIC_QUEUE; i++)
			if (GenericQueue[i] == SpriteNum)
				GenericQueue[i] = -1;

		for (i = 0; i < MAX_LOWANGS_QUEUE; i++)
			if (LoWangsQueue[i] == SpriteNum)
				LoWangsQueue[i] = -1;
	}

	public static void QueueReset() {
		short i;
		StarQueueHead = 0;
		HoleQueueHead = 0;
		WallBloodQueueHead = 0;
		FloorBloodQueueHead = 0;
		GenericQueueHead = 0;
		LoWangsQueueHead = 0;

		for (i = 0; i < MAX_STAR_QUEUE; i++)
			StarQueue[i] = -1;

		for (i = 0; i < MAX_HOLE_QUEUE; i++)
			HoleQueue[i] = -1;

		for (i = 0; i < MAX_WALLBLOOD_QUEUE; i++)
			WallBloodQueue[i] = -1;

		for (i = 0; i < MAX_FLOORBLOOD_QUEUE; i++)
			FloorBloodQueue[i] = -1;

		for (i = 0; i < MAX_GENERIC_QUEUE; i++)
			GenericQueue[i] = -1;

		for (i = 0; i < MAX_LOWANGS_QUEUE; i++)
			LoWangsQueue[i] = -1;
	}

	public static boolean TestDontStick(int SpriteNum, int hitsect, int hitwall, int hitz) {
		WALL wp;

		if (hitwall < 0) {
			SPRITE sp = sprite[SpriteNum];
			USER u = pUser[SpriteNum];

			hitwall = NORM_WALL(u.ret);
			hitsect = sp.sectnum;
		}

		wp = wall[hitwall];

		if (TEST(wp.extra, WALLFX_DONT_STICK))
			return (true);

		// if blocking red wallo
		if (TEST(wp.cstat, CSTAT_WALL_BLOCK) && wp.nextwall >= 0)
			return (true);

		return (false);
	}

	public static boolean TestDontStickSector(int hitsect) {
		if (TEST(sector[hitsect].extra, SECTFX_DYNAMIC_AREA | SECTFX_SECTOR_OBJECT))
			return (true);

		return (false);
	}

	public static int QueueStar(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		SPRITE osp;

		if (TestDontStick(SpriteNum, -1, -1, sp.z)) {
			KillSprite(SpriteNum);
			return (-1);
		}

		// can and should kill the user portion of the star
		if (StarQueue[StarQueueHead] == -1) {
			// new star
			if (pUser[SpriteNum] != null) {
				pUser[SpriteNum] = null;
			}
			change_sprite_stat(SpriteNum, STAT_STAR_QUEUE);
			StarQueue[StarQueueHead] = (short) SpriteNum;
		} else {
			// move old star to new stars place
			osp = sprite[StarQueue[StarQueueHead]];
			osp.x = sp.x;
			osp.y = sp.y;
			osp.z = sp.z;
			engine.changespritesect(StarQueue[StarQueueHead], sp.sectnum);
			KillSprite(SpriteNum);
			SpriteNum = StarQueue[StarQueueHead];

		}

		StarQueueHead = (short) ((StarQueueHead + 1) & (MAX_STAR_QUEUE - 1));

		return (SpriteNum);
	}

	public static int QueueHole(short ang, short hitsect, short hitwall, int hitx, int hity, int hitz) {
		short w, nw, wall_ang;
		short SpriteNum;
		int nx, ny;
		SPRITE sp;
		short sectnum;

		if (TestDontStick(-1, hitsect, hitwall, hitz))
			return (-1);

		if (HoleQueue[HoleQueueHead] == -1)
			HoleQueue[HoleQueueHead] = SpriteNum = COVERinsertsprite(hitsect, STAT_HOLE_QUEUE);
		else
			SpriteNum = HoleQueue[HoleQueueHead];

		HoleQueueHead = (short) ((HoleQueueHead + 1) & (MAX_HOLE_QUEUE - 1));

		sp = sprite[SpriteNum];
		sp.owner = -1;
		sp.xrepeat = sp.yrepeat = 16;
		sp.cstat = sp.pal = sp.shade = (byte) (sp.extra = (short) (sp.clipdist = sp.xoffset = sp.yoffset = 0));
		sp.x = hitx;
		sp.y = hity;
		sp.z = hitz;
		sp.picnum = 2151;
		engine.changespritesect(SpriteNum, hitsect);

		sp.cstat |= (CSTAT_SPRITE_WALL);
		sp.cstat |= (CSTAT_SPRITE_ONE_SIDE);
		sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		w = hitwall;
		nw = wall[w].point2;
		wall_ang = NORM_ANGLE(engine.getangle(wall[nw].x - wall[w].x, wall[nw].y - wall[w].y) + 512);
		sp.ang = wall_ang;

		// move it back some
		nx = sintable[(512 + sp.ang) & 2047] << 4;
		ny = sintable[sp.ang] << 4;

		sectnum = sp.sectnum;

		clipmoveboxtracenum = 1;
		engine.clipmove(sp.x, sp.y, sp.z, sectnum, nx, ny, 0, 0, 0, CLIPMASK_MISSILE);

		sp.x = clipmove_x;
		sp.y = clipmove_y;
		sp.z = clipmove_z;
		sectnum = clipmove_sectnum;

		clipmoveboxtracenum = 3;

		if (sp.sectnum != sectnum)
			engine.changespritesect(SpriteNum, sectnum);

		return (SpriteNum);
	}

	public static int QueueFloorBlood(int hitsprite) {
		SPRITE hsp = sprite[hitsprite];
		short SpriteNum;
		SPRITE sp;
		USER u = pUser[hitsprite];
		SECTOR sectp = sector[hsp.sectnum];

		if (TEST(sectp.extra, SECTFX_SINK) || TEST(sectp.extra, SECTFX_CURRENT))
			return (-1); // No blood in water or current areas

		if (TEST(u.Flags, SPR_UNDERWATER) || SpriteInUnderwaterArea(hsp) || SpriteInDiveArea(hsp))
			return (-1); // No blood underwater!

		if (DTEST(sector[hsp.sectnum].extra, SECTFX_LIQUID_MASK) == SECTFX_LIQUID_WATER)
			return (-1); // No prints liquid areas!

		if (DTEST(sector[hsp.sectnum].extra, SECTFX_LIQUID_MASK) == SECTFX_LIQUID_LAVA)
			return (-1); // Not in lave either

		if (TestDontStickSector(hsp.sectnum))
			return (-1); // Not on special sectors you don't

		if (FloorBloodQueue[FloorBloodQueueHead] != -1)
			KillSprite(FloorBloodQueue[FloorBloodQueueHead]);

		SpriteNum = (short) SpawnSprite(STAT_SKIP4, FLOORBLOOD1,
				s_FloorBlood1[0], hsp.sectnum, hsp.x, hsp.y, hsp.z, hsp.ang, 0);
		if(SpriteNum == -1)
			return 0;

		FloorBloodQueue[FloorBloodQueueHead] = SpriteNum;
		FloorBloodQueueHead = (short) ((FloorBloodQueueHead + 1) & (MAX_FLOORBLOOD_QUEUE - 1));

		sp = sprite[SpriteNum];
		sp.owner = -1;
		// Stupid hack to fix the blood under the skull to not show through
		// x,y repeat of floor blood MUST be smaller than the sprite above it or
		// clipping probs.
		if (u.ID == GORE_Head)
			sp.hitag = 9995;
		else
			sp.hitag = 0;
		sp.xrepeat = sp.yrepeat = 8;
		sp.cstat = sp.pal = sp.shade = (byte) (sp.extra = (short) (sp.clipdist = sp.xoffset = sp.yoffset = 0));
		sp.x = hsp.x;
		sp.y = hsp.y;
		sp.z = hsp.z - Z(1);
		sp.ang = (short) RANDOM_P2(2048); // Just make it any old angle
		sp.shade -= 5; // Brighten it up just a bit

		sp.cstat |= (CSTAT_SPRITE_FLOOR);
		sp.cstat |= (CSTAT_SPRITE_ONE_SIDE);
		sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		u.Flags &= ~(SPR_SHADOW);

		return (SpriteNum);
	}

	public static int QueueFootPrint(int hitsprite) {
		SPRITE hsp = sprite[hitsprite];
		short SpriteNum;
		SPRITE sp;
		USER u = pUser[hitsprite];
		USER nu;
		short rnd_num = 0;
		boolean Found = false;

		SECTOR sectp = sector[hsp.sectnum];

		if (TEST(sectp.extra, SECTFX_SINK) || TEST(sectp.extra, SECTFX_CURRENT))
			return (-1); // No blood in water or current areas

		if (u.PlayerP != -1 && isValidSector(Player[u.PlayerP].cursectnum)) {
			if (TEST(Player[u.PlayerP].Flags, PF_DIVING))
				Found = true;

			// Stupid masked floor stuff! Damn your weirdness!
			if (TEST(sector[Player[u.PlayerP].cursectnum].ceilingstat, CEILING_STAT_PLAX))
				Found = true;
			if (TEST(sector[Player[u.PlayerP].cursectnum].floorstat, CEILING_STAT_PLAX))
				Found = true;
		}

		if (TEST(u.Flags, SPR_UNDERWATER) || SpriteInUnderwaterArea(hsp) || Found || SpriteInDiveArea(hsp))
			return (-1); // No prints underwater!

		if (DTEST(sector[hsp.sectnum].extra, SECTFX_LIQUID_MASK) == SECTFX_LIQUID_WATER)
			return (-1); // No prints liquid areas!

		if (DTEST(sector[hsp.sectnum].extra, SECTFX_LIQUID_MASK) == SECTFX_LIQUID_LAVA)
			return (-1); // Not in lave either

		if (TestDontStickSector(hsp.sectnum))
			return (-1); // Not on special sectors you don't

		// So, are we like, done checking now!?
		if (FloorBloodQueue[FloorBloodQueueHead] != -1)
			KillSprite(FloorBloodQueue[FloorBloodQueueHead]);

		rnd_num = (short) RANDOM_RANGE(1024);
		State state;
		int id;
		if (rnd_num > 683) {
			id = FOOTPRINT1;
			state = s_FootPrint1[0];
		}
		else if (rnd_num > 342) {
			id = FOOTPRINT2;
			state = s_FootPrint2[0];
		}
		else {
			id = FOOTPRINT3;
			state = s_FootPrint3[0];
		}

		SpriteNum = (short) SpawnSprite(STAT_WALLBLOOD_QUEUE, id,
				state, hsp.sectnum, hsp.x, hsp.y, hsp.z, hsp.ang, 0);
		if(SpriteNum == -1)
			return -1;

		FloorBloodQueue[FloorBloodQueueHead] = SpriteNum;
		FloorBloodQueueHead = (short) ((FloorBloodQueueHead + 1) & (MAX_FLOORBLOOD_QUEUE - 1));

		// Decrease footprint count
		if (u.PlayerP != -1)
			Player[u.PlayerP].NumFootPrints--;

		sp = sprite[SpriteNum];
		nu = pUser[SpriteNum];
		sp.hitag = 0;
		sp.owner = -1;
		sp.xrepeat = 48;
		sp.yrepeat = 54;
		sp.cstat = sp.pal = sp.shade = (byte) (sp.extra = (short) (sp.clipdist = sp.xoffset = sp.yoffset = 0));
		sp.x = hsp.x;
		sp.y = hsp.y;
		sp.z = hsp.z;
		sp.ang = hsp.ang;
		nu.Flags &= ~(SPR_SHADOW);
		if(FootMode == FootType.BLOOD_FOOT)
			nu.spal = (byte) (sp.pal = PALETTE_PLAYER3); // Turn blue to blood red
		else nu.spal = (byte) (sp.pal = PALETTE_PLAYER1); // Gray water

		// Alternate the feet
		left_foot = !left_foot;
		if (left_foot)
			sp.cstat |= (CSTAT_SPRITE_XFLIP);
		sp.cstat |= (CSTAT_SPRITE_FLOOR);
		sp.cstat |= (CSTAT_SPRITE_ONE_SIDE);
		sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		return (SpriteNum);
	}

	public static final int WALLBLOOD_DIST_MAX = 2500;

	public static int QueueWallBlood(int hitsprite, int ang) {
		SPRITE hsp = sprite[hitsprite];
		short w, nw, wall_ang, dang;
		short SpriteNum;
		int nx, ny;
		SPRITE sp;
		short sectnum;
		short rndnum;
		int daz;

		USER u = pUser[hitsprite];

		if (TEST(u.Flags, SPR_UNDERWATER) || SpriteInUnderwaterArea(hsp) || SpriteInDiveArea(hsp))
			return (-1); // No blood underwater!

		daz = Z(RANDOM_P2(128)) << 3;
		daz -= DIV2(Z(128) << 3);
		dang = (short) ((ang + (RANDOM_P2(128 << 5) >> 5)) - DIV2(128));
		hitsprite = 0; // Reset hitsprite

		FAFhitscan(hsp.x, hsp.y, hsp.z - Z(30), hsp.sectnum, // Start position
				sintable[NORM_ANGLE(dang + 512)], // X vector of 3D ang
				sintable[NORM_ANGLE(dang)], // Y vector of 3D ang
				daz, // Z vector of 3D ang
				pHitInfo, CLIPMASK_MISSILE);

		hitsprite = pHitInfo.hitsprite;

		if (pHitInfo.hitsect < 0)
			return (-1);

		if (Distance(pHitInfo.hitx, pHitInfo.hity, hsp.x, hsp.y) > WALLBLOOD_DIST_MAX)
			return (-1);

		// hit a sprite?
		if (hitsprite >= 0)
			return (0); // Don't try to put blood on a sprite

		if (pHitInfo.hitwall >= 0) // Don't check if blood didn't hit a wall, otherwise the ASSERT fails!
		{
			if (TestDontStick(-1, pHitInfo.hitsect, pHitInfo.hitwall, pHitInfo.hitz))
				return (-1);
		} else
			return (-1);

		if (WallBloodQueue[WallBloodQueueHead] != -1)
			KillSprite(WallBloodQueue[WallBloodQueueHead]);

		// Randomly choose a wall blood sprite
		rndnum = (short) RANDOM_RANGE(1024);
		int id;
		State state;
		if (rndnum > 768) {
			id = WALLBLOOD1;
			state = s_WallBlood1[0];
		} else if (rndnum > 512) {
			id = WALLBLOOD2;
			state = s_WallBlood2[0];
		} else if (rndnum > 128) {
			id = WALLBLOOD3;
			state = s_WallBlood3[0];
		} else {
			id = WALLBLOOD4;
			state = s_WallBlood4[0];
		}

		SpriteNum = (short) SpawnSprite(STAT_WALLBLOOD_QUEUE, id,
				state, pHitInfo.hitsect, pHitInfo.hitx, pHitInfo.hity, pHitInfo.hitz, ang, 0);
		if(SpriteNum == -1)
			return -1;

		WallBloodQueue[WallBloodQueueHead] = SpriteNum;
		WallBloodQueueHead = (short) ((WallBloodQueueHead + 1) & (MAX_WALLBLOOD_QUEUE - 1));

		sp = sprite[SpriteNum];
		sp.owner = -1;
		sp.xrepeat = 30;
		sp.yrepeat = 40; // yrepeat will grow towards 64, it's default size
		sp.cstat = sp.pal = sp.shade = (byte) (sp.extra = (short) (sp.clipdist = sp.xoffset = sp.yoffset = 0));
		sp.x = pHitInfo.hitx;
		sp.y = pHitInfo.hity;
		sp.z = pHitInfo.hitz;
		sp.shade -= 5; // Brighten it up just a bit
		sp.yvel = pHitInfo.hitwall; // pass hitwall in yvel

		sp.cstat |= (CSTAT_SPRITE_WALL);
		sp.cstat |= (CSTAT_SPRITE_ONE_SIDE);
		sp.cstat |= (CSTAT_SPRITE_YCENTER);
		sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		w = pHitInfo.hitwall;
		nw = wall[w].point2;
		wall_ang = NORM_ANGLE(engine.getangle(wall[nw].x - wall[w].x, wall[nw].y - wall[w].y) + 512);
		sp.ang = wall_ang;

		// move it back some
		nx = sintable[(512 + sp.ang) & 2047] << 4;
		ny = sintable[sp.ang] << 4;

		sectnum = sp.sectnum;

		clipmoveboxtracenum = 1;
		engine.clipmove(sp.x, sp.y, sp.z, sectnum, nx, ny, 0, 0, 0, CLIPMASK_MISSILE);

		sp.x = clipmove_x;
		sp.y = clipmove_y;
		sp.z = clipmove_z;
		sectnum = clipmove_sectnum;

		clipmoveboxtracenum = 3;

		if (sp.sectnum != sectnum)
			engine.changespritesect(SpriteNum, sectnum);

		return (SpriteNum);
	}

	public static final int FEET_IN_BLOOD_DIST = 300;

	public static int DoFloorBlood(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];

		SPRITE psp = pUser[SpriteNum].getSprite();
		int dist, near_dist = FEET_IN_BLOOD_DIST;
		short pnum;
		PlayerStr pp;
		short xsiz, ysiz;

		if (sp.hitag == 9995) {
			xsiz = 12;
			ysiz = 12;
		} else {
			xsiz = 40;
			ysiz = 40;
		}

		// Make pool of blood seem to grow
		if (sp.xrepeat < xsiz && sp.xrepeat != 4) {
			sp.xrepeat++;
		}

		if (sp.yrepeat < ysiz && sp.xrepeat != xsiz && sp.xrepeat != 4) {
			sp.yrepeat++;
		}

		// See if any players stepped in blood
		if (sp.xrepeat != 4 && sp.yrepeat > 4) {
			for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
				pp = Player[pnum];

				dist = DISTANCE(psp.x, psp.y, pp.posx, pp.posy);

				if (dist < near_dist) {
					if (pp.NumFootPrints <= 0 || FootMode != FootType.BLOOD_FOOT) {
						pp.NumFootPrints = (short) (RANDOM_RANGE(10) + 3);
						FootMode = FootType.BLOOD_FOOT;
					}

					// If blood has already grown to max size, we can shrink it
					if (sp.xrepeat == 40 && sp.yrepeat > 10) {
						sp.yrepeat -= 10;
						if (sp.yrepeat <= 10) // Shrink it down and don't use it anymore
							sp.xrepeat = sp.yrepeat = 4;
					}
				}
			}
		}
		return (0);
	}

	public static int DoWallBlood(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		// Make blood drip down the wall
		if (sp.yrepeat < 80) {
			sp.yrepeat++;
			sp.z += 128;
		}

		return (0);
	}

	// This is the FAST queue, it doesn't call any Animator functions or states
	public static int QueueGeneric(int SpriteNum, int pic) {
		SPRITE sp = sprite[SpriteNum];
		SPRITE osp;
		short xrepeat, yrepeat;

		if (DTEST(sector[sp.sectnum].extra, SECTFX_LIQUID_MASK) == SECTFX_LIQUID_WATER) {
			KillSprite(SpriteNum);
			return (-1);
		}

		if (DTEST(sector[sp.sectnum].extra, SECTFX_LIQUID_MASK) == SECTFX_LIQUID_LAVA) {
			KillSprite(SpriteNum);
			return (-1);
		}

		if (TestDontStickSector(sp.sectnum)) {
			KillSprite(SpriteNum);
			return (-1);
		}

		xrepeat = sp.xrepeat;
		yrepeat = sp.yrepeat;

		// can and should kill the user portion
		if (GenericQueue[GenericQueueHead] == -1) {
			if (pUser[SpriteNum] != null) {
				pUser[SpriteNum] = null;
			}
			change_sprite_stat(SpriteNum, STAT_GENERIC_QUEUE);
			GenericQueue[GenericQueueHead] = (short) SpriteNum;
		} else {
			// move old sprite to new sprite's place
			osp = sprite[GenericQueue[GenericQueueHead]];
			// setsprite(GenericQueue[GenericQueueHead],sp.x,sp.y,sp.z);
			osp.x = sp.x;
			osp.y = sp.y;
			osp.z = sp.z;
			engine.changespritesect(GenericQueue[GenericQueueHead], sp.sectnum);
			KillSprite(SpriteNum);
			SpriteNum = GenericQueue[GenericQueueHead];
		}

		sp = sprite[SpriteNum];
		sp.picnum = (short) pic;
		sp.xrepeat = xrepeat;
		sp.yrepeat = yrepeat;
		sp.cstat = 0;
		switch (sp.picnum) {
		case 900:
		case 901:
		case 902:
		case 915:
		case 916:
		case 917:
		case 930:
		case 931:
		case 932:
		case GORE_Head:
			change_sprite_stat(SpriteNum, STAT_DEFAULT); // Breakable
			sp.cstat |= (CSTAT_SPRITE_BREAKABLE);
			sp.extra |= (SPRX_BREAKABLE);
			break;
		default:
			sp.cstat &= ~(CSTAT_SPRITE_BREAKABLE);
			sp.extra &= ~(SPRX_BREAKABLE);
			break;
		}

		GenericQueueHead = (short) ((GenericQueueHead + 1) & (MAX_GENERIC_QUEUE - 1));

		return (SpriteNum);
	}

	public static boolean SpawnShell(int SpriteNum, int ShellNum) {
		InitShell(SpriteNum, ShellNum);
		return (true);
	}

	public static boolean DoShrapVelocity(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_UNDERWATER) || SpriteInUnderwaterArea(sp)) {
			ScaleSpriteVector(SpriteNum, 20000);

			u.Counter += 8 * 4; // These are MoveSkip4 now
			u.zchange += u.Counter;
		} else {
			u.Counter += 60 * 4;
			u.zchange += u.Counter;
		}

		u.ret = move_missile(SpriteNum, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS * 2);

		MissileHitDiveArea(SpriteNum);

		if (u.ret != 0) {
			switch (DTEST(u.ret, HIT_MASK)) {
			case HIT_PLAX_WALL:
				KillSprite(SpriteNum);
				return (true);
			case HIT_SPRITE: {
				short wall_ang;
				short hitsprite = -2;
				SPRITE hsp;
//	                PlaySound(DIGI_DHCLUNK, sp, v3df_dontpan);

				hitsprite = NORM_SPRITE(u.ret);
				hsp = sprite[hitsprite];

				wall_ang = NORM_ANGLE(hsp.ang);
				WallBounce(SpriteNum, wall_ang);
				ScaleSpriteVector(SpriteNum, 32000);

				break;
			}

			case HIT_WALL: {
				short hitwall, nw, wall_ang;
				WALL wph;

				hitwall = NORM_WALL(u.ret);
				wph = wall[hitwall];

//	                PlaySound(DIGI_DHCLUNK, sp, v3df_dontpan);

				nw = wall[hitwall].point2;
				wall_ang = NORM_ANGLE(engine.getangle(wall[nw].x - wph.x, wall[nw].y - wph.y) + 512);

				WallBounce(SpriteNum, wall_ang);
				ScaleSpriteVector(SpriteNum, 32000);
				break;
			}

			case HIT_SECTOR: {
				boolean hitwall;

				if (SlopeBounce(SpriteNum, tmp_ptr[0])) {
					hitwall = tmp_ptr[0].value != 0;
					if (hitwall) {
						// hit a wall
						ScaleSpriteVector(SpriteNum, 28000);
						u.ret = 0;
						u.Counter = 0;
					} else {
						// hit a sector
						if (sp.z > DIV2(u.hiz + u.loz)) {
							// hit a floor
							if (!TEST(u.Flags, SPR_BOUNCE)) {
								u.Flags |= (SPR_BOUNCE);
								ScaleSpriteVector(SpriteNum, 18000);
								u.ret = 0;
								u.Counter = 0;
							} else {
								if (u.ID == GORE_Drip)
									ChangeState(SpriteNum, s_GoreFloorSplash[0]);
								else
									ShrapKillSprite(SpriteNum);
								return (true);
							}
						} else {
							// hit a ceiling
							ScaleSpriteVector(SpriteNum, 22000);
						}
					}
				} else {
					// hit floor
					if (sp.z > DIV2(u.hiz + u.loz)) {
						sp.z = u.loz;
						if (TEST(u.Flags, SPR_UNDERWATER))
							u.Flags |= (SPR_BOUNCE); // no bouncing underwater

						if (u.lo_sectp != -1 && SectUser[sp.sectnum] != null && SectUser[sp.sectnum].depth != 0)
							u.Flags |= (SPR_BOUNCE); // no bouncing on shallow water

						if (!TEST(u.Flags, SPR_BOUNCE)) {
							u.Flags |= (SPR_BOUNCE);
							u.ret = 0;
							u.Counter = 0;
							u.zchange = -u.zchange;
							ScaleSpriteVector(SpriteNum, 18000);
							switch (u.ID) {
							case UZI_SHELL:
								PlaySound(DIGI_SHELL, sp, v3df_none);
								break;
							case SHOT_SHELL:
								PlaySound(DIGI_SHOTSHELLSPENT, sp, v3df_none);
								break;
							}
						} else {
							if (u.ID == GORE_Drip)
								ChangeState(SpriteNum, s_GoreFloorSplash[0]);
							else
								ShrapKillSprite(SpriteNum);
							return (true);
						}
					} else
					// hit something above
					{
						u.zchange = -u.zchange;
						ScaleSpriteVector(SpriteNum, 22000);
					}
				}
				break;
			}
			}
		}

		// just outright kill it if its boucing around alot
		if (u.bounce > 10) {
			KillSprite(SpriteNum);
			return (true);
		}

		return (false);
	}

	public static int ShrapKillSprite(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		short rnd_num;

		rnd_num = (short) RANDOM_RANGE(1024);

		switch (u.ID) {
		case UZI_SHELL:
			if (rnd_num > 854) {
				QueueGeneric(SpriteNum, UZI_SHELL + 0);
			} else if (rnd_num > 684) {
				QueueGeneric(SpriteNum, UZI_SHELL + 1);
			} else if (rnd_num > 514) {
				QueueGeneric(SpriteNum, UZI_SHELL + 2);
			} else if (rnd_num > 344) {
				QueueGeneric(SpriteNum, UZI_SHELL + 3);
			} else if (rnd_num > 174) {
				QueueGeneric(SpriteNum, UZI_SHELL + 4);
			} else {
				QueueGeneric(SpriteNum, UZI_SHELL + 5);
			}

			return (0);
		case SHOT_SHELL:
			if (rnd_num > 683) {
				QueueGeneric(SpriteNum, SHOT_SHELL + 1);
			} else if (rnd_num > 342) {
				QueueGeneric(SpriteNum, SHOT_SHELL + 3);
			} else {
				QueueGeneric(SpriteNum, SHOT_SHELL + 7);
			}
			return (0);
		case GORE_Lung:
			if (RANDOM_RANGE(1000) > 500)
				break;
			sp.clipdist = SPRITEp_SIZE_X(sp);
			SpawnFloorSplash(SpriteNum);
			if (RANDOM_RANGE(1000) < 500)
				PlaySound(DIGI_GIBS1, sp, v3df_none);
			else
				PlaySound(DIGI_GIBS2, sp, v3df_none);
			if (rnd_num > 683) {
				QueueGeneric(SpriteNum, 900);
			} else if (rnd_num > 342) {
				QueueGeneric(SpriteNum, 901);
			} else {
				QueueGeneric(SpriteNum, 902);
			}
			return (0);
		case GORE_Liver:
			if (RANDOM_RANGE(1000) > 500)
				break;
			sp.clipdist = SPRITEp_SIZE_X(sp);
			SpawnFloorSplash(SpriteNum);
			if (RANDOM_RANGE(1000) < 500)
				PlaySound(DIGI_GIBS1, sp, v3df_none);
			else
				PlaySound(DIGI_GIBS2, sp, v3df_none);
			if (rnd_num > 683) {
				QueueGeneric(SpriteNum, 915);
			} else if (rnd_num > 342) {
				QueueGeneric(SpriteNum, 916);
			} else {
				QueueGeneric(SpriteNum, 917);
			}
			return (0);
		case GORE_SkullCap:
			if (RANDOM_RANGE(1000) > 500)
				break;
			sp.clipdist = SPRITEp_SIZE_X(sp);
			SpawnFloorSplash(SpriteNum);
			if (rnd_num > 683) {
				QueueGeneric(SpriteNum, 930);
			} else if (rnd_num > 342) {
				QueueGeneric(SpriteNum, 931);
			} else {
				QueueGeneric(SpriteNum, 932);
			}
			return (0);
		case GORE_Head:
			if (RANDOM_RANGE(1000) > 500)
				break;
			sp.clipdist = SPRITEp_SIZE_X(sp);
			QueueFloorBlood(SpriteNum);
			QueueGeneric(SpriteNum, GORE_Head);
			return (0);
		}

		// If it wasn't in the switch statement, kill it.
		KillSprite(SpriteNum);

		return (0);
	}

	public static boolean CheckBreakToughness(Break_Info break_info, int ID) {
		if (TEST(break_info.flags, BF_TOUGH)) {
			switch (ID) {
			case LAVA_BOULDER:
			case MIRV_METEOR:
			case SERP_METEOR:
			case BOLT_THINMAN_R0:
			case BOLT_THINMAN_R1:
			case BOLT_THINMAN_R2:
			case BOLT_EXP:
			case TANK_SHELL_EXP:
			case GRENADE_EXP:
			case MICRO_EXP:
			case MINE_EXP:
			case NAP_EXP:
			case SKULL_R0:
			case BETTY_R0:
			case SKULL_SERP:
			case FIREBALL1:
			case GORO_FIREBALL:
				return (true); // All the above stuff will break tough things
			}
			return (false); // False means it won't break with current weapon
		}

		return (true); // It wasn't tough, go ahead and break it
	}

	public static boolean DoItemFly(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_UNDERWATER)) {
			ScaleSpriteVector(SpriteNum, 50000);

			u.Counter += 20 * 2;
			u.zchange += u.Counter;
		} else {
			u.Counter += 60 * 2;
			u.zchange += u.Counter;
		}

		u.ret = move_missile(SpriteNum, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS * 2);

		MissileHitDiveArea(SpriteNum);

		if (u.ret != 0) {
			switch (DTEST(u.ret, HIT_MASK)) {
			case HIT_SPRITE: {
				short wall_ang;
				short hitsprite = -2;
				SPRITE hsp;

				hitsprite = NORM_SPRITE(u.ret);
				hsp = sprite[hitsprite];

				if (TEST(hsp.cstat, CSTAT_SPRITE_WALL)) {
					wall_ang = NORM_ANGLE(hsp.ang);
					WallBounce(SpriteNum, wall_ang);
					ScaleSpriteVector(SpriteNum, 32000);
				} else {
					u.xchange = -u.xchange;
					u.ychange = -u.ychange;
				}

				break;
			}

			case HIT_WALL: {
				short hitwall, nw, wall_ang;
				WALL wph;

				hitwall = NORM_WALL(u.ret);
				wph = wall[hitwall];

				nw = wall[hitwall].point2;
				wall_ang = NORM_ANGLE(engine.getangle(wall[nw].x - wph.x, wall[nw].y - wph.y) + 512);

				WallBounce(SpriteNum, wall_ang);
				ScaleSpriteVector(SpriteNum, 32000);
				break;
			}

			case HIT_SECTOR: {
				// hit floor
				if (sp.z > DIV2(u.hiz + u.loz)) {
					sp.z = u.loz;
					u.Counter = 0;
					sp.xvel = 0;
					u.zchange = u.xchange = u.ychange = 0;
					return (false);
				} else
				// hit something above
				{
					u.zchange = -u.zchange;
					ScaleSpriteVector(SpriteNum, 22000);
				}
				break;
			}
			}
		}

		return (true);
	}

//This is the FAST queue, it doesn't call any Animator functions or states
	public static int QueueLoWangs(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], ps;
		USER u;
		short NewSprite;

		if (DTEST(sector[sp.sectnum].extra, SECTFX_LIQUID_MASK) == SECTFX_LIQUID_WATER) {
			return (-1);
		}

		if (DTEST(sector[sp.sectnum].extra, SECTFX_LIQUID_MASK) == SECTFX_LIQUID_LAVA) {
			return (-1);
		}

		if (TestDontStickSector(sp.sectnum)) {
			return (-1);
		}

		if (LoWangsQueue[LoWangsQueueHead] == -1) {
			NewSprite = (short) SpawnSprite(STAT_GENERIC_QUEUE, sp.picnum,
					s_DeadLoWang[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang, 0);
			if(NewSprite == -1)
				return -1;

			LoWangsQueue[LoWangsQueueHead] = NewSprite;
		} else {
			// move old sprite to new sprite's place
			engine.setspritez(LoWangsQueue[LoWangsQueueHead], sp.x, sp.y, sp.z);
			NewSprite = LoWangsQueue[LoWangsQueueHead];
		}

		// Point passed in sprite to ps
		ps = sp;
		sp = sprite[NewSprite];
		u = pUser[NewSprite];

		sp.owner = -1;
		sp.cstat = 0;
		sp.xrepeat = ps.xrepeat;
		sp.yrepeat = ps.yrepeat;
		sp.shade = ps.shade;
		u.spal = (byte) (sp.pal = ps.pal);
		change_sprite_stat(NewSprite, STAT_DEFAULT); // Breakable
		sp.cstat |= (CSTAT_SPRITE_BREAKABLE);
		sp.extra |= (SPRX_BREAKABLE);
		sp.cstat |= (CSTAT_SPRITE_BLOCK_HITSCAN);

		LoWangsQueueHead = (short) ((LoWangsQueueHead + 1) & (MAX_LOWANGS_QUEUE - 1));

		return (SpriteNum);
	}

	public static void WeaponSaveable()
	{
		SaveData(DoCoolgDrip);
		SaveData(DoMineExp);
		SaveData(DoMineExpMine);
		SaveData(DoExpDamageTest);
		SaveData(SpawnShrapX);
		SaveData(DoLavaErupt);
		SaveData(DoVomit);
		SaveData(DoVomit);
		SaveData(DoVomitSplash);
		SaveData(DoFastShrapJumpFall);
		SaveData(DoTracerShrap);
		SaveData(DoShrapJumpFall);
		SaveData(DoShrapDamage);
		SaveData(DoUziSmoke);
		SaveData(DoShotgunSmoke);
		SaveData(DoMineSpark);
		SaveData(DoFireballFlames);
		SaveData(DoBreakFlames);
		SaveData(DoDamageTest);
		SaveData(DoStar);
		SaveData(DoCrossBolt);
		SaveData(DoPlasmaDone);
		SaveData(DoPlasmaFountain);
		SaveData(DoPlasma);
		SaveData(DoCoolgFire);
		SaveData(DoGrenade);
		SaveData(DoVulcanBoulder);
		SaveData(DoMineStuck);
		SaveData(DoMine);
		SaveData(DoPuff);
		SaveData(DoRailPuff);
		SaveData(DoBoltThinMan);
		SaveData(DoTracer);
		SaveData(DoEMP);
		SaveData(DoEMPBurst);
		SaveData(DoTankShell);
		SaveData(DoTracerStart);
		SaveData(DoLaser);
		SaveData(DoLaserStart);
		SaveData(DoRail);
		SaveData(DoRailStart);
		SaveData(DoRocket);
		SaveData(DoMicroMini);
		SaveData(DoMicro);
		SaveData(DoUziBullet);
		SaveData(DoBoltSeeker);
		SaveData(DoBoltShrapnel);
		SaveData(DoBoltFatMan);
		SaveData(DoElectro);
		SaveData(DoLavaBoulder);
		SaveData(DoSpear);
		SaveData(SpawnGrenadeSmallExp);
		SaveData(DoSectorExp);
		SaveData(DoFireball);
		SaveData(DoNapalm);
		SaveData(DoBloodWorm);
		SaveData(DoBloodWorm);
		SaveData(DoMeteor);
		SaveData(DoSerpMeteor);
		SaveData(DoMirvMissile);
		SaveData(DoMirv);
		SaveData(DoRing);
		SaveData(DoTeleRipper);
		SaveData(GenerateDrips);
		SaveData(DoSuicide);
		SaveData(DoDefaultStat);
		SaveData(DoVehicleSmoke);
		SaveData(DoWaterSmoke);
		SaveData(SpawnVehicleSmoke);
		SaveData(DoBubble);
		SaveData(DoFloorBlood);
		SaveData(DoWallBlood);
		SaveData(s_NotRestored);
		SaveData(s_Suicide);
		SaveData(s_DeadLoWang);
		SaveData(s_BreakLight);
		SaveData(s_BreakBarrel);
		SaveData(s_BreakPedistal);
		SaveData(s_BreakBottle1);
		SaveData(s_BreakBottle2);
		SaveData(s_Puff);
		SaveData(s_RailPuff);
		SaveData(s_LaserPuff);
		SaveData(s_Tracer);
		SaveData(s_EMP);
		SaveData(s_EMPBurst);
		SaveData(s_EMPShrap);
		SaveData(s_TankShell);
		SaveData(s_VehicleSmoke);
		SaveData(s_WaterSmoke);
		SaveData(s_UziSmoke);
		SaveData(s_ShotgunSmoke);
		SaveData(s_UziBullet);
		SaveData(s_UziSpark);
		SaveData(s_UziPowerSpark);
		SaveData(s_Bubble);
		SaveData(s_Splash);
		SaveData(s_CrossBolt);
		SaveGroup(WeaponStateGroup.sg_CrossBolt);
		SaveData(s_Star);
		SaveData(s_StarStuck);
		SaveData(s_StarDown);
		SaveData(s_StarDownStuck);
		SaveData(s_LavaBoulder);
		SaveData(s_LavaShard);
		SaveData(s_VulcanBoulder);
		SaveData(s_Grenade);
		SaveData(s_Grenade);
		SaveGroup(WeaponStateGroup.sg_Grenade);
		SaveData(s_MineStuck);
		SaveData(s_Mine);
		SaveData(s_MineSpark);
		SaveData(s_Meteor);
		SaveGroup(WeaponStateGroup.sg_Meteor);
		SaveData(s_MeteorExp);
		SaveData(s_MirvMeteor);
		SaveGroup(WeaponStateGroup.sg_MirvMeteor);
		SaveData(s_MirvMeteorExp);
		SaveData(s_SerpMeteor);
		SaveGroup(WeaponStateGroup.sg_SerpMeteor);
		SaveData(s_SerpMeteorExp);
		SaveData(s_Spear);
		SaveGroup(WeaponStateGroup.sg_Spear);
		SaveData(s_Rocket);
		SaveGroup(WeaponStateGroup.sg_Rocket);
		SaveData(s_BunnyRocket);
		SaveGroup(WeaponStateGroup.sg_BunnyRocket);
		SaveData(s_Rail);
		SaveGroup(WeaponStateGroup.sg_Rail);
		SaveData(s_Laser);
		SaveData(s_Micro);
		SaveGroup(WeaponStateGroup.sg_Micro);
		SaveData(s_MicroMini);
		SaveGroup(WeaponStateGroup.sg_MicroMini);
		SaveData(s_BoltThinMan);
		SaveGroup(WeaponStateGroup.sg_BoltThinMan);
		SaveData(s_BoltSeeker);
		SaveGroup(WeaponStateGroup.sg_BoltSeeker);
		SaveData(s_BoltFatMan);
		SaveData(s_BoltShrapnel);
		SaveData(s_CoolgFire);
		SaveData(s_CoolgFireDone);
		SaveData(s_CoolgDrip);
		SaveData(s_GoreFloorSplash);
		SaveData(s_GoreSplash);
		SaveData(s_Plasma);
		SaveData(s_PlasmaFountain);
		SaveData(s_PlasmaDrip);
		SaveData(s_PlasmaDone);
		SaveData(s_TeleportEffect);
		SaveData(s_TeleportEffect2);
		SaveData(s_Electro);
		SaveData(s_ElectroShrap);
		SaveData(s_GrenadeExp);
		SaveData(s_GrenadeSmallExp);
		SaveData(s_GrenadeExp);
		SaveData(s_MineExp);
		SaveData(s_BasicExp);
		SaveData(s_MicroExp);
		SaveData(s_BigGunFlame);
		SaveData(s_BoltExp);
		SaveData(s_TankShellExp);
		SaveData(s_TracerExp);
		SaveData(s_SectorExp);
		SaveData(s_FireballExp);
		SaveData(s_NapExp);
		SaveData(s_FireballFlames);
		SaveData(s_BreakFlames);
		SaveData(s_Fireball);
		SaveData(s_Fireball);
		SaveData(s_Ring);
		SaveData(s_Ring);
		SaveData(s_Ring2);
		SaveData(s_Napalm);
		SaveData(s_BloodWorm);
		SaveData(s_BloodWorm);
		SaveData(s_PlasmaExp);
		SaveData(s_PlasmaExp);
		SaveData(s_Mirv);
		SaveData(s_MirvMissile);
		SaveData(s_Vomit1);
		SaveData(s_Vomit2);
		SaveData(s_VomitSplash);
		SaveData(s_GoreHead);
		SaveData(s_GoreLeg);
		SaveData(s_GoreEye);
		SaveData(s_GoreTorso);
		SaveData(s_GoreArm);
		SaveData(s_GoreLung);
		SaveData(s_GoreLiver);
		SaveData(s_GoreSkullCap);
		SaveData(s_GoreChunkS);
		SaveData(s_GoreDrip);
		SaveData(s_FastGoreDrip);
		SaveData(s_GoreFlame);
		SaveData(s_TracerShrap);
		SaveData(s_UziShellShrap);
		SaveData(s_UziShellShrapStill1);
		SaveData(s_UziShellShrapStill2);
		SaveData(s_UziShellShrapStill3);
		SaveData(s_UziShellShrapStill4);
		SaveData(s_UziShellShrapStill5);
		SaveData(s_UziShellShrapStill6);
		SaveData(s_ShotgunShellShrap);
		SaveData(s_ShotgunShellShrapStill1);
		SaveData(s_ShotgunShellShrapStill2);
		SaveData(s_ShotgunShellShrapStill3);
		SaveData(s_GoreFlameChunkA);
		SaveData(s_GoreFlameChunkB);
		SaveData(s_CoinShrap);
		SaveData(s_Marbel);
		SaveData(s_GlassShrapA);
		SaveData(s_GlassShrapB);
		SaveData(s_GlassShrapC);
		SaveData(s_WoodShrapA);
		SaveData(s_WoodShrapB);
		SaveData(s_WoodShrapC);
		SaveData(s_StoneShrapA);
		SaveData(s_StoneShrapB);
		SaveData(s_StoneShrapC);
		SaveData(s_MetalShrapA);
		SaveData(s_MetalShrapB);
		SaveData(s_MetalShrapC);
		SaveData(s_PaperShrapA);
		SaveData(s_PaperShrapB);
		SaveData(s_PaperShrapC);
		SaveData(s_FloorBlood1);
		SaveData(s_FootPrint1);
		SaveData(s_FootPrint2);
		SaveData(s_FootPrint3);
		SaveData(s_WallBlood1);
		SaveData(s_WallBlood2);
		SaveData(s_WallBlood3);
		SaveData(s_WallBlood4);
	}
}
