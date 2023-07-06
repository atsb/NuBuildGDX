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
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.parallaxyscale;
import static ru.m210projects.Build.Engine.pskybits;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Gameutils.isValidSector;
import static ru.m210projects.Build.Gameutils.isValidTile;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Wang.Actor.DoActorDebris;
import static ru.m210projects.Wang.Actor.DoFireFly;
import static ru.m210projects.Wang.Ai.DoActorPickClosePlayer;
import static ru.m210projects.Wang.Break.SetupSpriteForBreak;
import static ru.m210projects.Wang.Digi.DIGI_BIGITEM;
import static ru.m210projects.Wang.Digi.DIGI_DOUBLEUZI;
import static ru.m210projects.Wang.Digi.DIGI_GOTRAILGUN;
import static ru.m210projects.Wang.Digi.DIGI_ILIKENUKES;
import static ru.m210projects.Wang.Digi.DIGI_ITEM;
import static ru.m210projects.Wang.Digi.DIGI_ITEM_SPAWN;
import static ru.m210projects.Wang.Digi.DIGI_KEY;
import static ru.m210projects.Wang.Digi.DIGI_LIKEBIGWEAPONS;
import static ru.m210projects.Wang.Enemies.Bunny.BunnyHatch2;
import static ru.m210projects.Wang.Enemies.Bunny.SetupBunny;
import static ru.m210projects.Wang.Enemies.Coolg.SetupCoolg;
import static ru.m210projects.Wang.Enemies.Coolie.SetupCoolie;
import static ru.m210projects.Wang.Enemies.Eel.SetupEel;
import static ru.m210projects.Wang.Cheats.*;
import static ru.m210projects.Wang.Enemies.GirlNinj.SetupGirlNinja;
import static ru.m210projects.Wang.Enemies.Goro.SetupGoro;
import static ru.m210projects.Wang.Enemies.Hornet.SetupHornet;
import static ru.m210projects.Wang.Enemies.Lava.SetupLava;
import static ru.m210projects.Wang.Enemies.Ninja.SetupNinja;
import static ru.m210projects.Wang.Enemies.Ripper.SetupRipper;
import static ru.m210projects.Wang.Enemies.Ripper2.SetupRipper2;
import static ru.m210projects.Wang.Enemies.Serp.SetupSerp;
import static ru.m210projects.Wang.Enemies.Skel.SetupSkel;
import static ru.m210projects.Wang.Enemies.Skull.SetupBetty;
import static ru.m210projects.Wang.Enemies.Skull.SetupSkull;
import static ru.m210projects.Wang.Enemies.Sumo.SetupSumo;
import static ru.m210projects.Wang.Enemies.Zilla.SetupZilla;
import static ru.m210projects.Wang.Game.DebugActor;
import static ru.m210projects.Wang.Game.DebugActorFreeze;
import static ru.m210projects.Wang.Game.Distance;
import static ru.m210projects.Wang.Game.Global_PLock;
import static ru.m210projects.Wang.Game.PlaxCeilGlobZadjust;
import static ru.m210projects.Wang.Game.*;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.Skill;
import static ru.m210projects.Wang.Game.tmp_ptr;
import static ru.m210projects.Wang.Gameutils.*;
import static ru.m210projects.Wang.Inv.INVENTORY_CALTROPS;
import static ru.m210projects.Wang.Inv.INVENTORY_CHEMBOMB;
import static ru.m210projects.Wang.Inv.INVENTORY_CLOAK;
import static ru.m210projects.Wang.Inv.INVENTORY_FLASHBOMB;
import static ru.m210projects.Wang.Inv.INVENTORY_MEDKIT;
import static ru.m210projects.Wang.Inv.INVENTORY_NIGHT_VISION;
import static ru.m210projects.Wang.Inv.INVENTORY_REPAIR_KIT;
import static ru.m210projects.Wang.Inv.PlayerUpdateInventory;
import static ru.m210projects.Wang.JPlayer.adduserquote;
import static ru.m210projects.Wang.JSector.JS_SpriteSetup;
import static ru.m210projects.Wang.JTags.LUMINOUS;
import static ru.m210projects.Wang.JTags.TAG_NORESPAWN_FLAG;
import static ru.m210projects.Wang.JWeapon.s_CarryFlag;
import static ru.m210projects.Wang.JWeapon.s_CarryFlagNoDet;
import static ru.m210projects.Wang.Light.LIGHT_ShadeInc;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.MiscActr.SetupCarGirl;
import static ru.m210projects.Wang.MiscActr.SetupMechanicGirl;
import static ru.m210projects.Wang.MiscActr.SetupPachinko1;
import static ru.m210projects.Wang.MiscActr.SetupPachinko2;
import static ru.m210projects.Wang.MiscActr.SetupPachinko3;
import static ru.m210projects.Wang.MiscActr.SetupPachinko4;
import static ru.m210projects.Wang.MiscActr.SetupPachinkoLight;
import static ru.m210projects.Wang.MiscActr.SetupPruneGirl;
import static ru.m210projects.Wang.MiscActr.SetupSailorGirl;
import static ru.m210projects.Wang.MiscActr.SetupToiletGirl;
import static ru.m210projects.Wang.MiscActr.SetupTrashCan;
import static ru.m210projects.Wang.MiscActr.SetupWashGirl;
import static ru.m210projects.Wang.Names.*;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER0;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER1;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER3;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER4;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER5;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER6;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER7;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER8;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER9;
import static ru.m210projects.Wang.Palette.PAL_XLAT_LT_GREY;
import static ru.m210projects.Wang.Palette.PAL_XLAT_LT_TAN;
import static ru.m210projects.Wang.Palette.SetFadeAmt;
import static ru.m210projects.Wang.Panel.MICRO_HEAT_NUM;
import static ru.m210projects.Wang.Panel.MICRO_SHOT_NUM;
import static ru.m210projects.Wang.Panel.PlayerUpdateAmmo;
import static ru.m210projects.Wang.Panel.PlayerUpdateArmor;
import static ru.m210projects.Wang.Panel.PlayerUpdateHealth;
import static ru.m210projects.Wang.Player.DoSpawnTeleporterEffectPlace;
import static ru.m210projects.Wang.Rooms.COVERinsertsprite;
import static ru.m210projects.Wang.Rooms.COVERupdatesector;
import static ru.m210projects.Wang.Rooms.FAFcansee;
import static ru.m210projects.Wang.Rooms.FAFgetzrange;
import static ru.m210projects.Wang.Rooms.FAFgetzrangepoint;
import static ru.m210projects.Wang.Rooms.zofslope;
import static ru.m210projects.Wang.Rotator.DoRotator;
import static ru.m210projects.Wang.Rotator.SetRotatorActive;
import static ru.m210projects.Wang.Sector.AnimDelete;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Sector.SetSectorWallBits;
import static ru.m210projects.Wang.Slidor.DoSlidor;
import static ru.m210projects.Wang.Slidor.DoSlidorInstantClose;
import static ru.m210projects.Wang.Slidor.SetSlidorActive;
import static ru.m210projects.Wang.Sound.DeleteNoFollowSoundOwner;
import static ru.m210projects.Wang.Sound.DeleteNoSoundOwner;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.PlayerGetItemVocs;
import static ru.m210projects.Wang.Sound.PlayerSound;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_doppler;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sound.v3df_none;
import static ru.m210projects.Wang.Spike.DoSpike;
import static ru.m210projects.Wang.Spike.DoSpikeAuto;
import static ru.m210projects.Wang.Spike.SetSpikeActive;
import static ru.m210projects.Wang.Spike.SpikeAlign;
import static ru.m210projects.Wang.Stag.BOLT_TRAP;
import static ru.m210projects.Wang.Stag.BOUND_FLOOR_BASE_OFFSET;
import static ru.m210projects.Wang.Stag.BOUND_FLOOR_OFFSET;
import static ru.m210projects.Wang.Stag.BREAKABLE;
import static ru.m210projects.Wang.Stag.CEILING_FLOOR_PIC_OVERRIDE;
import static ru.m210projects.Wang.Stag.CEILING_Z_ADJUST;
import static ru.m210projects.Wang.Stag.DELETE_SPRITE;
import static ru.m210projects.Wang.Stag.DEMO_CAMERA;
import static ru.m210projects.Wang.Stag.FIREBALL_TRAP;
import static ru.m210projects.Wang.Stag.FLOOR_SLOPE_DONT_DRAW;
import static ru.m210projects.Wang.Stag.FLOOR_Z_ADJUST;
import static ru.m210projects.Wang.Stag.LAVA_ERUPT;
import static ru.m210projects.Wang.Stag.LIGHTING;
import static ru.m210projects.Wang.Stag.LIGHTING_DIFFUSE;
import static ru.m210projects.Wang.Stag.PARALLAX_LEVEL;
import static ru.m210projects.Wang.Stag.PLAX_GLOB_Z_ADJUST;
import static ru.m210projects.Wang.Stag.QUAKE_SPOT;
import static ru.m210projects.Wang.Stag.SECT_ACTOR_BLOCK;
import static ru.m210projects.Wang.Stag.SECT_CEILING_PAN;
import static ru.m210projects.Wang.Stag.SECT_CHANGOR;
import static ru.m210projects.Wang.Stag.SECT_COPY_DEST;
import static ru.m210projects.Wang.Stag.SECT_COPY_SOURCE;
import static ru.m210projects.Wang.Stag.SECT_DAMAGE;
import static ru.m210projects.Wang.Stag.SECT_DONT_COPY_PALETTE;
import static ru.m210projects.Wang.Stag.SECT_EXPLODING_CEIL_FLOOR;
import static ru.m210projects.Wang.Stag.SECT_FLOOR_PAN;
import static ru.m210projects.Wang.Stag.SECT_LOCK_DOOR;
import static ru.m210projects.Wang.Stag.SECT_MATCH;
import static ru.m210projects.Wang.Stag.SECT_ROTATOR;
import static ru.m210projects.Wang.Stag.SECT_ROTATOR_PIVOT;
import static ru.m210projects.Wang.Stag.SECT_SLIDOR;
import static ru.m210projects.Wang.Stag.SECT_SO_CENTER;
import static ru.m210projects.Wang.Stag.SECT_SO_CLIP_DIST;
import static ru.m210projects.Wang.Stag.SECT_SO_DONT_BOB;
import static ru.m210projects.Wang.Stag.SECT_SO_DONT_ROTATE;
import static ru.m210projects.Wang.Stag.SECT_SO_DONT_SINK;
import static ru.m210projects.Wang.Stag.SECT_SO_FORM_WHIRLPOOL;
import static ru.m210projects.Wang.Stag.SECT_SO_SINK_DEST;
import static ru.m210projects.Wang.Stag.SECT_SO_SPRITE_OBJ;
import static ru.m210projects.Wang.Stag.SECT_SPIKE;
import static ru.m210projects.Wang.Stag.SECT_VATOR;
import static ru.m210projects.Wang.Stag.SECT_VATOR_DEST;
import static ru.m210projects.Wang.Stag.SECT_WALL_MOVE;
import static ru.m210projects.Wang.Stag.SECT_WALL_MOVE_CANSEE;
import static ru.m210projects.Wang.Stag.SECT_WALL_PAN_SPEED;
import static ru.m210projects.Wang.Stag.SLIDE_SECTOR;
import static ru.m210projects.Wang.Stag.SOUND_SPOT;
import static ru.m210projects.Wang.Stag.SO_AMOEBA;
import static ru.m210projects.Wang.Stag.SO_ANGLE;
import static ru.m210projects.Wang.Stag.SO_AUTO_TURRET;
import static ru.m210projects.Wang.Stag.SO_BOB_SPEED;
import static ru.m210projects.Wang.Stag.SO_BOB_START;
import static ru.m210projects.Wang.Stag.SO_DRIVABLE_ATTRIB;
import static ru.m210projects.Wang.Stag.SO_FLOOR_MORPH;
import static ru.m210projects.Wang.Stag.SO_KILLABLE;
import static ru.m210projects.Wang.Stag.SO_LIMIT_TURN;
import static ru.m210projects.Wang.Stag.SO_MATCH_EVENT;
import static ru.m210projects.Wang.Stag.SO_MAX_DAMAGE;
import static ru.m210projects.Wang.Stag.SO_RAM_DAMAGE;
import static ru.m210projects.Wang.Stag.SO_SCALE_INFO;
import static ru.m210projects.Wang.Stag.SO_SCALE_POINT_INFO;
import static ru.m210projects.Wang.Stag.SO_SCALE_XY_MULT;
import static ru.m210projects.Wang.Stag.SO_SET_SPEED;
import static ru.m210projects.Wang.Stag.SO_SLIDE;
import static ru.m210projects.Wang.Stag.SO_SLOPE_CEILING_TO_POINT;
import static ru.m210projects.Wang.Stag.SO_SLOPE_FLOOR_TO_POINT;
import static ru.m210projects.Wang.Stag.SO_SPIN;
import static ru.m210projects.Wang.Stag.SO_SPIN_REVERSE;
import static ru.m210projects.Wang.Stag.SO_SYNC1;
import static ru.m210projects.Wang.Stag.SO_SYNC2;
import static ru.m210projects.Wang.Stag.SO_TORNADO;
import static ru.m210projects.Wang.Stag.SO_TURN_SPEED;
import static ru.m210projects.Wang.Stag.SO_WALL_DONT_MOVE_LOWER;
import static ru.m210projects.Wang.Stag.SO_WALL_DONT_MOVE_UPPER;
import static ru.m210projects.Wang.Stag.SPAWN_ITEMS;
import static ru.m210projects.Wang.Stag.SPAWN_SPOT;
import static ru.m210projects.Wang.Stag.SPEAR_TRAP;
import static ru.m210projects.Wang.Stag.SPRI_CLIMB_MARKER;
import static ru.m210projects.Wang.Stag.STOP_SOUND_SPOT;
import static ru.m210projects.Wang.Stag.TRIGGER_SECTOR;
import static ru.m210projects.Wang.Stag.VIEW_LEVEL1;
import static ru.m210projects.Wang.Stag.VIEW_LEVEL2;
import static ru.m210projects.Wang.Stag.VIEW_LEVEL3;
import static ru.m210projects.Wang.Stag.VIEW_LEVEL4;
import static ru.m210projects.Wang.Stag.VIEW_LEVEL5;
import static ru.m210projects.Wang.Stag.VIEW_LEVEL6;
import static ru.m210projects.Wang.Stag.VIEW_THRU_CEILING;
import static ru.m210projects.Wang.Stag.VIEW_THRU_FLOOR;
import static ru.m210projects.Wang.Stag.WALL_DONT_STICK;
import static ru.m210projects.Wang.Stag.WARP_CEILING_PLANE;
import static ru.m210projects.Wang.Stag.WARP_COPY_SPRITE1;
import static ru.m210projects.Wang.Stag.WARP_COPY_SPRITE2;
import static ru.m210projects.Wang.Stag.WARP_FLOOR_PLANE;
import static ru.m210projects.Wang.Stag.WARP_TELEPORTER;
import static ru.m210projects.Wang.Tags.TAG_ROTATOR;
import static ru.m210projects.Wang.Tags.TAG_SLIDOR;
import static ru.m210projects.Wang.Tags.TAG_SPAWN_ACTOR;
import static ru.m210projects.Wang.Tags.TAG_SPRITE_HIT_MATCH;
import static ru.m210projects.Wang.Tags.TAG_VATOR;
import static ru.m210projects.Wang.Text.PutStringInfo;
import static ru.m210projects.Wang.Track.MAX_TRACKS;
import static ru.m210projects.Wang.Track.Track;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.DIV2;
import static ru.m210projects.Wang.Type.MyTypes.DTEST;
import static ru.m210projects.Wang.Type.MyTypes.MOD2;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Vator.DoVator;
import static ru.m210projects.Wang.Vator.DoVatorAuto;
import static ru.m210projects.Wang.Vator.MoveSpritesWithSector;
import static ru.m210projects.Wang.Vator.SetVatorActive;
import static ru.m210projects.Wang.Warp.WarpM;
import static ru.m210projects.Wang.Warp.WarpPlane;
import static ru.m210projects.Wang.Warp.warp;
import static ru.m210projects.Wang.Weapon.DamageData;
import static ru.m210projects.Wang.Weapon.DoItemFly;
import static ru.m210projects.Wang.Weapon.DoLavaErupt;
import static ru.m210projects.Wang.Weapon.DoStaticFlamesDamage;
import static ru.m210projects.Wang.Weapon.SetSuicide;
import static ru.m210projects.Wang.Weapon.s_TeleportEffect;
import static ru.m210projects.Wang.Weapons.Grenade.InitWeaponGrenade;
import static ru.m210projects.Wang.Weapons.Heart.InitWeaponHeart;
import static ru.m210projects.Wang.Weapons.HotHead.InitWeaponHothead;
import static ru.m210projects.Wang.Weapons.Micro.InitWeaponMicro;
import static ru.m210projects.Wang.Weapons.Micro.ps_MicroHeatFlash;
import static ru.m210projects.Wang.Weapons.Micro.ps_MicroNukeFlash;
import static ru.m210projects.Wang.Weapons.Mine.InitWeaponMine;
import static ru.m210projects.Wang.Weapons.Rail.InitWeaponRail;
import static ru.m210projects.Wang.Weapons.Shotgun.InitWeaponShotgun;
import static ru.m210projects.Wang.Weapons.Star.InitWeaponStar;
import static ru.m210projects.Wang.Weapons.Uzi.InitWeaponUzi;

import java.util.Arrays;

import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Wang.Factory.WangNetwork.MultiGameTypes;
import ru.m210projects.Wang.Type.Anim.AnimType;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.RotatorStr;
import ru.m210projects.Wang.Type.Sect_User;
import ru.m210projects.Wang.Type.Sector_Object;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;
import ru.m210projects.Wang.Type.Vector2i;

public class Sprites {

	public interface StateGroup {

		public int getLength(int rotation);

		public State getState(int rotation, int offset);

		public State getState(int rotation);

		public int index();

		public void setIndex(int index);

	}

	public static Sector_Object[] SectorObject = new Sector_Object[MAX_SECTOR_OBJECTS];
	public static short wait_active_check_offset;
	public static final int ACTIVE_CHECK_TIME = (3 * 120);
	public static int globhiz, globloz, globhihit, globlohit;

	public static boolean MoveSkip2;
	public static int MoveSkip4, MoveSkip8;

	public static final State s_DebrisNinja[] = { new State(NINJA_DIE + 3, 100, DoActorDebris).setNext() };

	public static final State s_DebrisRat[] = { new State(750, 100, DoActorDebris).setNext() };

	public static final State s_DebrisCrab[] = { new State(423, 100, DoActorDebris).setNext() };

	public static final State s_DebrisStarFish[] = { new State(426, 100, DoActorDebris).setNext() };

	public static final State[] Debris = { s_DebrisNinja[0], s_DebrisRat[0], s_DebrisCrab[0], s_DebrisStarFish[0] };

	public static final int DirArr[] = { NORTH, NE, EAST, SE, SOUTH, SW, WEST, NW, NORTH, NE, EAST, SE, SOUTH, SW, WEST,
			NW };

	public static final int SCROLL_RATE = 20;
	public static final int SCROLL_FIRE_RATE = 20;

	public static final Animator DoGet = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoGet(spr) != 0;
		}
	}, DoKey = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoKey(spr) != 0;
		}
	}, DoSpriteFade = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoSpriteFade(spr) != 0;
		}
	};

	// temporary
	public static final int ICON_REPAIR_KIT = 1813;
	public static final int REPAIR_KIT_RATE = 1100;
	public static final State s_RepairKit[] = { new State(ICON_REPAIR_KIT + 0, REPAIR_KIT_RATE, DoGet).setNext(), };

	public static final State s_GoldSkelKey[] = { new State(GOLD_SKELKEY, 100, DoGet).setNext(), };
	public static final State s_BlueKey[] = { new State(BLUE_KEY, 100, DoGet).setNext(), };
	public static final State s_BlueCard[] = { new State(BLUE_CARD, 100, DoGet).setNext(), };

	public static final State s_SilverSkelKey[] = { new State(SILVER_SKELKEY, 100, DoGet).setNext(), };
	public static final State s_RedKey[] = { new State(RED_KEY, 100, DoGet).setNext(), };
	public static final State s_RedCard[] = { new State(RED_CARD, 100, DoGet).setNext(), };

	public static final State s_BronzeSkelKey[] = { new State(BRONZE_SKELKEY, 100, DoGet).setNext(), };
	public static final State s_GreenKey[] = { new State(GREEN_KEY, 100, DoGet).setNext(), };
	public static final State s_GreenCard[] = { new State(GREEN_CARD, 100, DoGet).setNext(), };

	public static final State s_RedSkelKey[] = { new State(RED_SKELKEY, 100, DoGet).setNext(), };
	public static final State s_YellowKey[] = { new State(YELLOW_KEY, 100, DoGet).setNext(), };
	public static final State s_YellowCard[] = { new State(YELLOW_CARD, 100, DoGet).setNext(), };

	public static final State s_Key[][] = { s_RedKey, s_BlueKey, s_GreenKey, s_YellowKey, s_RedCard, s_BlueCard,
			s_GreenCard, s_YellowCard, s_GoldSkelKey, s_SilverSkelKey, s_BronzeSkelKey, s_RedSkelKey };

	public static final int KEY_RATE = 25;
	public static final int Red_COIN = 2440;
	public static final int Yellow_COIN = 2450;
	public static final int Green_COIN = 2460;
	public static final int RED_COIN_RATE = 10;
	public static final int YELLOW_COIN_RATE = 8;
	public static final int GREEN_COIN_RATE = 6;
	public static final Animator DoCoin = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoCoin(spr) != 0;
		}
	};

	public static final State s_RedCoin[] = { new State(Red_COIN + 0, RED_COIN_RATE, DoCoin),
			new State(Red_COIN + 1, RED_COIN_RATE, DoCoin), new State(Red_COIN + 2, RED_COIN_RATE, DoCoin),
			new State(Red_COIN + 3, RED_COIN_RATE, DoCoin), new State(Red_COIN + 4, RED_COIN_RATE, DoCoin),
			new State(Red_COIN + 5, RED_COIN_RATE, DoCoin), new State(Red_COIN + 6, RED_COIN_RATE, DoCoin),
			new State(Red_COIN + 7, RED_COIN_RATE, DoCoin), };

	// !JIM! Frank, I made coins go progressively faster
	public static final State s_YellowCoin[] = { new State(Yellow_COIN + 0, YELLOW_COIN_RATE, DoCoin),
			new State(Yellow_COIN + 1, YELLOW_COIN_RATE, DoCoin), new State(Yellow_COIN + 2, YELLOW_COIN_RATE, DoCoin),
			new State(Yellow_COIN + 3, YELLOW_COIN_RATE, DoCoin), new State(Yellow_COIN + 4, YELLOW_COIN_RATE, DoCoin),
			new State(Yellow_COIN + 5, YELLOW_COIN_RATE, DoCoin), new State(Yellow_COIN + 6, YELLOW_COIN_RATE, DoCoin),
			new State(Yellow_COIN + 7, YELLOW_COIN_RATE, DoCoin), };

	public static final State s_GreenCoin[] = { new State(Green_COIN + 0, GREEN_COIN_RATE, DoCoin),
			new State(Green_COIN + 1, GREEN_COIN_RATE, DoCoin), new State(Green_COIN + 2, GREEN_COIN_RATE, DoCoin),
			new State(Green_COIN + 3, GREEN_COIN_RATE, DoCoin), new State(Green_COIN + 4, GREEN_COIN_RATE, DoCoin),
			new State(Green_COIN + 5, GREEN_COIN_RATE, DoCoin), new State(Green_COIN + 6, GREEN_COIN_RATE, DoCoin),
			new State(Green_COIN + 7, GREEN_COIN_RATE, DoCoin), };

	public static final Animator DoFireFly = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoFireFly(spr) != 0;
		}
	};

	public static final State s_FireFly[] = { new State(FIRE_FLY0, FIRE_FLY_RATE * 4, DoFireFly).setNext(), };

	public static final State s_IconStar[] = { new State(ICON_STAR, 100, DoGet).setNext(), };

	public static final State s_IconUzi[] = { new State(ICON_UZI, 100, DoGet).setNext(), };

	public static final State s_IconLgUziAmmo[] = { new State(ICON_LG_UZI_AMMO, 100, DoGet).setNext(), };

	public static final State s_IconUziFloor[] = { new State(ICON_UZIFLOOR, 100, DoGet).setNext(), };

	public static final State s_IconRocket[] = { new State(ICON_ROCKET, 100, DoGet).setNext(), };

	public static final State s_IconLgRocket[] = { new State(ICON_LG_ROCKET, 100, DoGet).setNext(), };

	public static final State s_IconShotgun[] = { new State(ICON_SHOTGUN, 100, DoGet).setNext(), };

	public static final State s_IconLgShotshell[] = { new State(ICON_LG_SHOTSHELL, 100, DoGet).setNext(), };

	public static final State s_IconAutoRiot[] = { new State(ICON_AUTORIOT, 100, DoGet).setNext(), };

	public static final State s_IconGrenadeLauncher[] = { new State(ICON_GRENADE_LAUNCHER, 100, DoGet).setNext(), };

	public static final State s_IconLgGrenade[] = { new State(ICON_LG_GRENADE, 100, DoGet).setNext(), };

	public static final State s_IconLgMine[] = { new State(ICON_LG_MINE, 100, DoGet).setNext(), };

	public static final State s_IconGuardHead[] = { new State(ICON_GUARD_HEAD + 0, 15, DoGet).setNext(), };

	public static final int FIREBALL_LG_AMMO_RATE = 12;
	public static final State s_IconFireballLgAmmo[] = {
			new State(ICON_FIREBALL_LG_AMMO + 0, FIREBALL_LG_AMMO_RATE, DoGet),
			new State(ICON_FIREBALL_LG_AMMO + 1, FIREBALL_LG_AMMO_RATE, DoGet),
			new State(ICON_FIREBALL_LG_AMMO + 2, FIREBALL_LG_AMMO_RATE, DoGet), };

	public static final State s_IconHeart[] = { new State(ICON_HEART + 0, 25, DoGet),
			new State(ICON_HEART + 1, 25, DoGet), };

	public static final int HEART_LG_AMMO_RATE = 12;
	public static final State s_IconHeartLgAmmo[] = { new State(ICON_HEART_LG_AMMO + 0, HEART_LG_AMMO_RATE, DoGet),
			new State(ICON_HEART_LG_AMMO + 1, HEART_LG_AMMO_RATE, DoGet), };

	public static final State s_IconMicroGun[] = { new State(ICON_MICRO_GUN, 100, DoGet).setNext(), };

	public static final State s_IconMicroBattery[] = { new State(ICON_MICRO_BATTERY, 100, DoGet).setNext(), };

	// !JIM! Added rail crap
	public static final State s_IconRailGun[] = { new State(ICON_RAIL_GUN, 100, DoGet).setNext(), };

	public static final State s_IconRailAmmo[] = { new State(ICON_RAIL_AMMO, 100, DoGet).setNext(), };

	public static final State s_IconElectro[] = { new State(ICON_ELECTRO + 0, 25, DoGet),
			new State(ICON_ELECTRO + 1, 25, DoGet), };

	public static final int ICON_SPELL_RATE = 8;

	public static final State s_IconSpell[] = { new State(ICON_SPELL + 0, ICON_SPELL_RATE, DoGet),
			new State(ICON_SPELL + 1, ICON_SPELL_RATE, DoGet), new State(ICON_SPELL + 2, ICON_SPELL_RATE, DoGet),
			new State(ICON_SPELL + 3, ICON_SPELL_RATE, DoGet), new State(ICON_SPELL + 4, ICON_SPELL_RATE, DoGet),
			new State(ICON_SPELL + 5, ICON_SPELL_RATE, DoGet), new State(ICON_SPELL + 6, ICON_SPELL_RATE, DoGet),
			new State(ICON_SPELL + 7, ICON_SPELL_RATE, DoGet), new State(ICON_SPELL + 8, ICON_SPELL_RATE, DoGet),
			new State(ICON_SPELL + 9, ICON_SPELL_RATE, DoGet), new State(ICON_SPELL + 10, ICON_SPELL_RATE, DoGet),
			new State(ICON_SPELL + 11, ICON_SPELL_RATE, DoGet), new State(ICON_SPELL + 12, ICON_SPELL_RATE, DoGet),
			new State(ICON_SPELL + 13, ICON_SPELL_RATE, DoGet), new State(ICON_SPELL + 14, ICON_SPELL_RATE, DoGet),
			new State(ICON_SPELL + 15, ICON_SPELL_RATE, DoGet), };

	public static final State s_IconArmor[] = { new State(ICON_ARMOR + 0, 15, DoGet).setNext(), };

	public static final State s_IconMedkit[] = { new State(ICON_MEDKIT + 0, 15, DoGet).setNext(), };

	public static final State s_IconChemBomb[] = { new State(ICON_CHEMBOMB, 15, DoGet).setNext(), };

	public static final State s_IconFlashBomb[] = { new State(ICON_FLASHBOMB, 15, DoGet).setNext(), };

	public static final State s_IconNuke[] = { new State(ICON_NUKE, 15, DoGet).setNext(), };

	public static final State s_IconCaltrops[] = { new State(ICON_CALTROPS, 15, DoGet).setNext(), };

	public static final int ICON_SM_MEDKIT = 1802;
	public static final State s_IconSmMedkit[] = { new State(ICON_SM_MEDKIT + 0, 15, DoGet).setNext(), };

	public static final int ICON_BOOSTER = 1810;
	public static final State s_IconBooster[] = { new State(ICON_BOOSTER + 0, 15, DoGet).setNext(), };

	public static final int ICON_HEAT_CARD = 1819;
	public static final State s_IconHeatCard[] = { new State(ICON_HEAT_CARD + 0, 15, DoGet).setNext(), };

	public static final State s_IconCloak[] = { new State(ICON_CLOAK + 0, 20, DoGet).setNext(), };

	public static final State s_IconFly[] = { new State(ICON_FLY + 0, 20, DoGet), new State(ICON_FLY + 1, 20, DoGet),
			new State(ICON_FLY + 2, 20, DoGet), new State(ICON_FLY + 3, 20, DoGet), new State(ICON_FLY + 4, 20, DoGet),
			new State(ICON_FLY + 5, 20, DoGet), new State(ICON_FLY + 6, 20, DoGet),
			new State(ICON_FLY + 7, 20, DoGet), };

	public static final State s_IconNightVision[] = { new State(ICON_NIGHT_VISION + 0, 20, DoGet).setNext(), };

	public static final State s_IconFlag[] = { new State(ICON_FLAG + 0, 32, DoGet), new State(ICON_FLAG + 1, 32, DoGet),
			new State(ICON_FLAG + 2, 32, DoGet), };

	public static void InitSprStates() {
		State.InitState(s_RedCoin);
		State.InitState(s_YellowCoin);
		State.InitState(s_GreenCoin);
		State.InitState(s_IconFireballLgAmmo);
		State.InitState(s_IconHeart);
		State.InitState(s_IconHeartLgAmmo);
		State.InitState(s_IconElectro);
		State.InitState(s_IconSpell);
		State.InitState(s_IconFly);
		State.InitState(s_IconNightVision);
		State.InitState(s_IconFlag);
	}

	public static void SetOwner(int owner, int child) {
		SPRITE cp = sprite[child];

		if (owner == 0) {

		}

		if (owner >= 0) {
			pUser[owner].Flags2 |= (SPR2_CHILDREN);
		} else {

		}

		cp.owner = (short) owner;
	}

	public static void SetAttach(int owner, int child) {
		USER cu = pUser[child];

		pUser[owner].Flags2 |= (SPR2_CHILDREN);
		cu.Attach = (short) owner;
	}

	public static short MissileStats[] = { STAT_MISSILE, STAT_MISSILE_SKIP4 };

	public static void KillSprite(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		short i, nexti;
		short stat;
		short statnum, sectnum;

		//////////////////////////////////////////////
		// Check sounds list to kill attached sounds
		DeleteNoSoundOwner(SpriteNum);
		DeleteNoFollowSoundOwner(SpriteNum);
		//////////////////////////////////////////////

		if (u != null) {
			PlayerStr pp;
			short pnum;

			if (u.WallShade != null) {
				u.WallShade = null;
			}

			// doing a MissileSetPos - don't allow killing
			if (TEST(u.Flags, SPR_SET_POS_DONT_KILL))
				return;

			// for attached sprites that are getable make sure they don't have
			// any Anims attached
			AnimDelete(u, AnimType.UserZ);
			AnimDelete(sp, AnimType.SpriteZ);

			// adjust sprites attached to sector objects
			if (TEST(u.Flags, SPR_SO_ATTACHED)) {
				Sector_Object sop;
				short sn, FoundSpriteNdx = -1;

				for (int s = 0; s < MAX_SECTOR_OBJECTS; s++) {
					sop = SectorObject[s];
					for (sn = 0; sop.sp_num[sn] != -1; sn++) {
						if (sop.sp_num[sn] == SpriteNum) {
							FoundSpriteNdx = sn;
						}
					}

					if (FoundSpriteNdx >= 0) {
						// back up sn so it points to the last valid sprite num
						sn--;

						// replace the one to be deleted with the last ndx
						sop.sp_num[FoundSpriteNdx] = sop.sp_num[sn];
						// the last ndx is not -1
						sop.sp_num[sn] = -1;

						break;
					}
				}

			}

			// if a player is dead and watching this sprite
			// reset it.
			for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
				pp = Player[pnum];

				if (pp.Killer > -1) {
					if (pp.Killer == SpriteNum) {
						pp.Killer = -1;
					}
				}
			}

			// if on a track and died reset the track to non-occupied
			if (sp.statnum == STAT_ENEMY) {
				if (u.track != -1 && Track[u.track] != null) {
					if (Track[u.track].flags != 0)
						Track[u.track].flags &= ~(TF_TRACK_OCCUPIED);
				}
			}

			// if missile is heading for the sprite, the missile need to know
			// that it is already dead
			if (TEST(sp.extra, SPRX_PLAYER_OR_ENEMY)) {
				USER mu;

				for (stat = 0; stat < MissileStats.length; stat++) {
					for (i = headspritestat[MissileStats[stat]]; i != -1; i = nexti) {
						nexti = nextspritestat[i];
						mu = pUser[i];

						if (mu.WpnGoal == SpriteNum) {
							mu.WpnGoal = -1;
						}
					}
				}
			}

			// much faster
			if (TEST(u.Flags2, SPR2_CHILDREN))
			// if (TEST(sp.extra, SPRX_CHILDREN))
			{
				// check for children and allert them that the owner is dead
				// don't bother th check if you've never had children
				for (stat = 0; stat < STAT_DONT_DRAW; stat++) {
					for (i = headspritestat[stat]; i != -1; i = nexti) {
						nexti = nextspritestat[i];
						if (sprite[i].owner == SpriteNum) {
							sprite[i].owner = -1;
						}

						if (pUser[i] != null && pUser[i].Attach == SpriteNum) {
							pUser[i].Attach = -1;
						}
					}
				}
			}

			if (sp.statnum == STAT_ENEMY) {
				for (i = headspritestat[STAT_ENEMY]; i != -1; i = nexti) {
					nexti = nextspritestat[i];
					if (pUser[i].tgt_sp == SpriteNum) {
						DoActorPickClosePlayer(i);
					}
				}
			}

			if (u.flame >= 0) {
				SetSuicide(u.flame);
			}

			if (u.rotator != null) {
				if (u.rotator.orig != null)
					u.rotator.orig = null;

				u.rotator = null;
			}

			pUser[SpriteNum] = null;
		}

		engine.deletesprite((short) SpriteNum);
		// shred your garbage - but not statnum
		statnum = sp.statnum;
		sectnum = sp.sectnum;

		sp.reset();
		sp.statnum = statnum;
		sp.sectnum = sectnum;
	}

	public static void ChangeState(int SpriteNum, State statep) {
		USER u = pUser[SpriteNum];

		u.Tics = 0;
		u.State = u.StateStart = statep;
		// Just in case
		PicAnimOff(u.State.Pic);
	}

	public static void change_sprite_stat(int SpriteNum, int stat) {
		USER u = pUser[SpriteNum];

		engine.changespritestat((short) SpriteNum, (short) stat);

		if (u != null) {
			u.Flags &= ~(SPR_SKIP2 | SPR_SKIP4);

			if (stat >= STAT_SKIP4_START && stat <= STAT_SKIP4_END)
				u.Flags |= (SPR_SKIP4);

			if (stat >= STAT_SKIP2_START && stat <= STAT_SKIP2_END)
				u.Flags |= (SPR_SKIP2);

			switch (stat) {
			case STAT_ENEMY_SKIP4:
			case STAT_ENEMY:
				// for enemys - create offsets so all enemys don't call FAFcansee at once
				wait_active_check_offset += ACTORMOVETICS * 3;
				if (wait_active_check_offset > ACTIVE_CHECK_TIME)
					wait_active_check_offset = 0;
				u.wait_active_check = wait_active_check_offset;
				// don't do a break here
				u.Flags |= (SPR_SHADOW);
				break;
			}

		}
	}

	public static USER SpawnUser(int SpriteNum, int id, State state) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		pUser[SpriteNum] = u = new USER();

		// be careful State can be null
		u.State = u.StateStart = state;

		change_sprite_stat(SpriteNum, sp.statnum);

		u.ID = (short) id;
		u.Health = 100;
		u.WpnGoal = -1; // for weapons
		u.Attach = -1;
		u.track = -1;
		u.tgt_sp = Player[0].PlayerSprite;
		u.Radius = 220;
		u.Sibling = -1;
		u.flame = -1;
		u.SpriteNum = (short) SpriteNum;
		u.WaitTics = 0;
		u.OverlapZ = Z(4);
		u.WallShade = null;
		u.rotator = null;
		u.bounce = 0;

		u.motion_blur_num = 0;
		u.motion_blur_dist = 256;

		u.ox = sp.x;
		u.oy = sp.y;
		u.oz = sp.z;

		u.active_range = MIN_ACTIVE_RANGE;

		// default

		// based on clipmove z of 48 pixels off the floor
		u.floor_dist = (short) (Z(48) - Z(28));
		u.ceiling_dist = (short) Z(8);

		// Problem with sprites spawned really close to white sector walls
		// cant do a getzrange there
		// Just put in some valid starting values

		if (isValidSector(sp.sectnum)) {
			u.loz = sector[sp.sectnum].floorz;
			u.hiz = sector[sp.sectnum].ceilingz;
			u.lo_sp = -1;
			u.hi_sp = -1;
			u.lo_sectp = sp.sectnum;
			u.hi_sectp = sp.sectnum;
		}

		return (u);
	}

	public static Sect_User GetSectUser(int sectnum) {
		if (SectUser[sectnum] != null)
			return (SectUser[sectnum]);

		Sect_User sectu = SectUser[sectnum] = new Sect_User();

		return (sectu);
	}

	public static int SpawnSprite(int stat, int id, State state, int sectnum, int x, int y, int z, int init_ang,
			int vel) {
		SPRITE sp;
		USER u;

		if (!isValidSector(sectnum))
			return -1;

		short SpriteNum = COVERinsertsprite((short) sectnum, stat);

		sp = sprite[SpriteNum];

		sp.pal = 0;
		sp.x = x;
		sp.y = y;
		sp.z = z;
		sp.cstat = 0;

		pUser[SpriteNum] = u = SpawnUser(SpriteNum, id, state);

		// be careful State can be null
		if (u.State != null) {
			sp.picnum = u.State.Pic;
			PicAnimOff(sp.picnum);
		}

		sp.shade = 0;
		sp.xrepeat = 64;
		sp.yrepeat = 64;
		sp.ang = NORM_ANGLE(init_ang);

		sp.xvel = (short) vel;
		sp.zvel = 0;
		sp.owner = -1;
		sp.lotag = 0;
		sp.hitag = 0;
		sp.extra = 0;
		sp.xoffset = 0;
		sp.yoffset = 0;
		sp.clipdist = 0;

		return (SpriteNum);
	}

	public static void PicAnimOff(int picnum) {
		Tile pic = engine.getTile(picnum);
		if (!TEST(pic.anm, TILE_ANIM_TYPE))
			return;

		pic.anm &= ~(TILE_ANIM_TYPE);
	}

	public static boolean IconSpawn(SPRITE sp) {
		// if multi item and not a modem game
		if (TEST(sp.extra, SPRX_MULTI_ITEM)) {
			if ((numplayers <= 1 && !gNet.FakeMultiplayer)
					|| gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COOPERATIVE)
				return (false);
		}

		sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN | CSTAT_SPRITE_ONE_SIDE | CSTAT_SPRITE_FLOOR);

		return (true);
	}

	public static boolean ActorTestSpawn(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		if (sp.statnum == STAT_DEFAULT && sp.lotag == TAG_SPAWN_ACTOR) {
			short newsp = COVERinsertsprite(sp.sectnum, STAT_DEFAULT);
			sprite[newsp].set(sp);

			change_sprite_stat(newsp, STAT_SPAWN_TRIGGER);
			sprite[newsp].cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
			return (false);
		}

		if (DebugActor)
			return (false);

		// Skill ranges from -1 (No Monsters) to 3
		if (DTEST(sprite[SpriteNum].extra, SPRX_SKILL) > Skill) {
			if(Skill > -1) {
				// always spawn girls in addons
				if ((sp.picnum == TOILETGIRL_R0 || sp.picnum == WASHGIRL_R0 || sp.picnum == MECHANICGIRL_R0
						|| sp.picnum == CARGIRL_R0 || sp.picnum == PRUNEGIRL_R0 || sp.picnum == SAILORGIRL_R0)
						&& swGetAddon() != 0)
					return true;

				// JBF: hack to fix Wanton Destruction's missing Sumos, Serpents, and Zilla on
				// Skill < 2
				if ((((sp.picnum == SERP_RUN_R0 || sp.picnum == SUMO_RUN_R0) && sp.lotag > 0 && sp.lotag != TAG_SPAWN_ACTOR
						&& sp.extra > 0) || sp.picnum == ZILLA_RUN_R0) && swGetAddon() != 0)
					return true;
			}

			return (false);
		}

		return (true);
	}

	public static boolean ActorSpawn(int SpriteNum) {
		boolean ret = true;
		SPRITE sp = sprite[SpriteNum];

		switch (sp.picnum) {
		case COOLIE_RUN_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupCoolie(SpriteNum);

			break;
		}

		case NINJA_RUN_R0:
		case NINJA_CRAWL_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupNinja(SpriteNum);

			break;
		}

		case GORO_RUN_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupGoro(SpriteNum);
			break;
		}

		case 1441:
		case COOLG_RUN_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupCoolg(SpriteNum);
			break;
		}

		case EEL_RUN_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupEel(SpriteNum);
			break;
		}

		case SUMO_RUN_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupSumo(SpriteNum);

			break;
		}

		case ZILLA_RUN_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupZilla(SpriteNum);

			break;
		}

		case TOILETGIRL_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupToiletGirl(SpriteNum);

			break;
		}

		case WASHGIRL_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupWashGirl(SpriteNum);

			break;
		}

		case CARGIRL_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupCarGirl(SpriteNum);

			break;
		}

		case MECHANICGIRL_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupMechanicGirl(SpriteNum);

			break;
		}

		case SAILORGIRL_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupSailorGirl(SpriteNum);

			break;
		}

		case PRUNEGIRL_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupPruneGirl(SpriteNum);

			break;
		}

		case TRASHCAN: {
			PicAnimOff(sp.picnum);
			SetupTrashCan(SpriteNum);

			break;
		}

		case BUNNY_RUN_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupBunny(SpriteNum);
			break;
		}

		case RIPPER_RUN_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupRipper(SpriteNum);
			break;
		}

		case RIPPER2_RUN_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupRipper2(SpriteNum);
			break;
		}

		case SERP_RUN_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupSerp(SpriteNum);
			break;
		}

		case LAVA_RUN_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupLava(SpriteNum);
			break;
		}

		case SKEL_RUN_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupSkel(SpriteNum);
			break;
		}

		case HORNET_RUN_R0: {
			if (!ActorTestSpawn(SpriteNum) || gs.DisableHornets) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupHornet(SpriteNum);
			break;
		}

		case SKULL_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupSkull(SpriteNum);
			break;
		}

		case BETTY_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupBetty(SpriteNum);
			break;
		}

		case 623: // Pachinko win light
		{
			PicAnimOff(sp.picnum);
			SetupPachinkoLight(SpriteNum);
			break;
		}

		case PACHINKO1: {
			PicAnimOff(sp.picnum);
			SetupPachinko1(SpriteNum);
			break;
		}

		case PACHINKO2: {
			PicAnimOff(sp.picnum);
			SetupPachinko2(SpriteNum);
			break;
		}

		case PACHINKO3: {
			PicAnimOff(sp.picnum);
			SetupPachinko3(SpriteNum);
			break;
		}

		case PACHINKO4: {
			PicAnimOff(sp.picnum);
			SetupPachinko4(SpriteNum);
			break;
		}

		case GIRLNINJA_RUN_R0: {
			if (!ActorTestSpawn(SpriteNum)) {
				KillSprite(SpriteNum);
				return (false);
			}

			PicAnimOff(sp.picnum);
			SetupGirlNinja(SpriteNum);
			break;
		}

		default:
			ret = false;
			break;
		}

		return (ret);
	}

	public static void IconDefault(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		change_sprite_stat(SpriteNum, STAT_ITEM);

		sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		u.Radius = 650;

		DoActorZrange(SpriteNum);
	}

	public static final int MAX_FLOORS = 32;
	private static short[] sectlist = new short[MAXSECTORS];
	private static SPRITE[] BoundList = new SPRITE[MAX_FLOORS];

	public static void PreMapCombineFloors() {
		SPRITE sp;
		int i, j, k;
		short SpriteNum, NextSprite;
		int base_offset;
		PlayerStr pp = Player[myconnectindex];
		int dx, dy;

		short sectlistplc, sectlistend, dasect, startwall, endwall, nextsector;
		short pnum;

		Arrays.fill(BoundList, null);

		for (SpriteNum = headspritestat[0]; SpriteNum != -1; SpriteNum = NextSprite) {
			NextSprite = nextspritestat[SpriteNum];
			sp = sprite[SpriteNum];

			if (sp.picnum != ST1)
				continue;

			if (sp.hitag == BOUND_FLOOR_OFFSET || sp.hitag == BOUND_FLOOR_BASE_OFFSET) {
				BoundList[sp.lotag] = sp;
				change_sprite_stat(SpriteNum, STAT_FAF);
			}
		}

		for (i = base_offset = 0; i < MAX_FLOORS; i++) {
			// blank so continue
			if (BoundList[i] == null)
				continue;

			if (BoundList[i].hitag == BOUND_FLOOR_BASE_OFFSET) {
				base_offset = i;
				continue;
			}

			dx = BoundList[base_offset].x - BoundList[i].x;
			dy = BoundList[base_offset].y - BoundList[i].y;

			sectlist[0] = BoundList[i].sectnum;
			sectlistplc = 0;
			sectlistend = 1;
			while (sectlistplc < sectlistend) {
				dasect = sectlist[sectlistplc++];

				for (j = headspritesect[dasect]; j >= 0; j = nextspritesect[j]) {
					sprite[j].x += dx;
					sprite[j].y += dy;
				}

				startwall = sector[dasect].wallptr;
				endwall = (short) (startwall + sector[dasect].wallnum);
				for (j = startwall; j < endwall; j++) {
					wall[j].x += dx;
					wall[j].y += dy;

					nextsector = wall[j].nextsector;
					if (nextsector < 0)
						continue;

					for (k = sectlistend - 1; k >= 0; k--)
						if (sectlist[k] == nextsector)
							break;
					if (k < 0)
						sectlist[sectlistend++] = nextsector;
				}

			}

			for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
				pp = Player[pnum];
				dasect = pp.cursectnum;
				for (j = 0; j < sectlistend; j++) {
					if (sectlist[j] == dasect) {
						pp.posx += dx;
						pp.posy += dy;
						pp.oposx = pp.oldposx = pp.posx;
						pp.oposy = pp.oldposy = pp.posy;
						break;
					}
				}
			}

		}

		// get rid of the sprites used
		for (SpriteNum = headspritestat[STAT_FAF]; SpriteNum != -1; SpriteNum = NextSprite) {
			NextSprite = nextspritestat[SpriteNum];
			KillSprite(SpriteNum);
		}
	}

	public static void SpriteSetupPost() {
		SPRITE ds;
		USER u;
		short SpriteNum, NextSprite;
		short i, nexti;
		int fz;

		// Post processing of some sprites after gone through the main SpriteSetup()
		// routine

		for (SpriteNum = headspritestat[STAT_FLOOR_PAN]; SpriteNum != -1; SpriteNum = NextSprite) {
			NextSprite = nextspritestat[SpriteNum];
			for (i = headspritesect[sprite[SpriteNum].sectnum]; i != -1; i = nexti) {
				nexti = nextspritesect[i];
				ds = sprite[i];

				if (ds.picnum == ST1)
					continue;

				if (TEST(ds.cstat, CSTAT_SPRITE_WALL | CSTAT_SPRITE_FLOOR))
					continue;

				if (pUser[i] != null)
					continue;

				engine.getzsofslope(ds.sectnum, ds.x, ds.y, zofslope);
				fz = zofslope[FLOOR];
				if (klabs(ds.z - fz) > Z(4))
					continue;

				u = SpawnUser(i, 0, null);
				change_sprite_stat(i, STAT_NO_STATE);
				u.ceiling_dist = (short) Z(4);
				u.floor_dist = (short) -Z(2);

				u.ActorActionFunc = DoActorDebris;

				ds.cstat |= (CSTAT_SPRITE_BREAKABLE);
				ds.extra |= (SPRX_BREAKABLE);
			}
		}
	}

	public static boolean SpriteSetup() {
		SPRITE sp;
		short SpriteNum = 0, NextSprite;
		USER u;
		int i, num = 0;
		int nexti;
		int cz, fz;

		// special case for player
		PicAnimOff(PLAYER_NINJA_RUN_R0);

		// Clear Sprite Extension structure
		for (int s = 0; s < MAXSECTORS; s++) {
			if (SectUser[s] != null)
				SectUser[s].reset();
		}

		// Clear all extra bits - they are set by sprites
		for (i = 0; i < numsectors; i++) {
			sector[i].extra = 0;
		}

		// Call my little sprite setup routine first
		JS_SpriteSetup();

		for (SpriteNum = headspritestat[0]; SpriteNum != -1; SpriteNum = NextSprite) {
			NextSprite = nextspritestat[SpriteNum];

			sp = sprite[SpriteNum];

			// not used yetv
			engine.getzsofslope(sp.sectnum, sp.x, sp.y, zofslope);
			cz = zofslope[CEIL];
			fz = zofslope[FLOOR];

			if (sp.z > DIV2(cz + fz)) {
				// closer to a floor
				sp.cstat |= (CSTAT_SPRITE_CLOSE_FLOOR);
			}

			// CSTAT_SPIN is insupported - get rid of it
			if (DTEST(sp.cstat, CSTAT_SPRITE_SLAB) == CSTAT_SPRITE_SLAB)
				sp.cstat &= ~(CSTAT_SPRITE_SLAB);

			// if BLOCK is set set BLOCK_HITSCAN
			// Hope this doesn't screw up anything
			if (TEST(sp.cstat, CSTAT_SPRITE_BLOCK))
				sp.cstat |= (CSTAT_SPRITE_BLOCK_HITSCAN);

			////////////////////////////////////////////
			//
			// BREAKABLE CHECK
			//
			////////////////////////////////////////////

			// USER SETUP - TAGGED BY USER
			// Non ST1 sprites that are tagged like them
			if (TEST_BOOL1(sp) && sp.picnum != ST1) {
				sp.extra &= ~(SPRX_BOOL4 | SPRX_BOOL5 | SPRX_BOOL6 | SPRX_BOOL7 | SPRX_BOOL8 | SPRX_BOOL9
						| SPRX_BOOL10);

				switch (sp.hitag) {
				case BREAKABLE:
					// need something that tells missiles to hit them
					// but allows actors to move through them
					sp.clipdist = SPRITEp_SIZE_X(sp);
					sp.extra |= (SPRX_BREAKABLE);
					sp.cstat |= (CSTAT_SPRITE_BREAKABLE);
					break;
				}
			} else {
				// BREAK SETUP TABLE AUTOMATED
				SetupSpriteForBreak(sp);
			}

			if (sp.lotag == TAG_SPRITE_HIT_MATCH) {
				// if multi item and not a modem game
				if (TEST(sp.extra, SPRX_MULTI_ITEM)) {
					if ((numplayers <= 1 && !gNet.FakeMultiplayer)
							|| gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COOPERATIVE) {
						KillSprite(SpriteNum);
						continue;
					}
				}

				// crack sprite
				if (sp.picnum == 80) {
					sp.cstat &= ~(CSTAT_SPRITE_BLOCK);
					sp.cstat |= (CSTAT_SPRITE_BLOCK_HITSCAN | CSTAT_SPRITE_BLOCK_MISSILE);
				} else {
					sp.cstat &= ~(CSTAT_SPRITE_BLOCK);
					sp.cstat |= (CSTAT_SPRITE_BLOCK_HITSCAN | CSTAT_SPRITE_BLOCK_MISSILE);
					sp.cstat |= (CSTAT_SPRITE_INVISIBLE);
				}

				if (TEST(SP_TAG8(sp), BIT(0)))
					sp.cstat |= (CSTAT_SPRITE_INVISIBLE);

				if (TEST(SP_TAG8(sp), BIT(1)))
					sp.cstat &= ~(CSTAT_SPRITE_INVISIBLE);

				change_sprite_stat(SpriteNum, STAT_SPRITE_HIT_MATCH);
				continue;
			}

			if (sprite[SpriteNum].picnum >= TRACK_SPRITE && sprite[SpriteNum].picnum <= TRACK_SPRITE + MAX_TRACKS) {
				short track_num;

				// skip this sprite, just for numbering walls/sectors
				if (TEST(sprite[SpriteNum].cstat, CSTAT_SPRITE_WALL))
					continue;

				track_num = (short) (sprite[SpriteNum].picnum - TRACK_SPRITE + 0);

				change_sprite_stat(SpriteNum, STAT_TRACK + track_num);

				continue;
			}

			if (ActorSpawn(SpriteNum))
				continue;

			switch (sprite[SpriteNum].picnum) {
			case ST_QUICK_JUMP:
				change_sprite_stat(SpriteNum, STAT_QUICK_JUMP);
				break;
			case ST_QUICK_JUMP_DOWN:
				change_sprite_stat(SpriteNum, STAT_QUICK_JUMP_DOWN);
				break;
			case ST_QUICK_SUPER_JUMP:
				change_sprite_stat(SpriteNum, STAT_QUICK_SUPER_JUMP);
				break;
			case ST_QUICK_SCAN:
				change_sprite_stat(SpriteNum, STAT_QUICK_SCAN);
				break;
			case ST_QUICK_EXIT:
				change_sprite_stat(SpriteNum, STAT_QUICK_EXIT);
				break;
			case ST_QUICK_OPERATE:
				change_sprite_stat(SpriteNum, STAT_QUICK_OPERATE);
				break;
			case ST_QUICK_DUCK:
				change_sprite_stat(SpriteNum, STAT_QUICK_DUCK);
				break;
			case ST_QUICK_DEFEND:
				change_sprite_stat(SpriteNum, STAT_QUICK_DEFEND);
				break;

			case ST1: {
				sp = sprite[SpriteNum];
				Sect_User sectu;
				short tag;
				short bit;

				// get rid of defaults
				if (SP_TAG3(sp) == 32)
					sp.clipdist = 0;

				tag = sp.hitag;

				sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
				sp.cstat |= (CSTAT_SPRITE_INVISIBLE);

				// for bounding sector objects
				if (tag >= 500 && tag < 600 || tag == SECT_SO_CENTER) {
					// NOTE: These will get deleted by the sector object
					// setup code
					change_sprite_stat(SpriteNum, STAT_ST1);
					break;
				}

				if (tag < 16) {
					bit = (short) (1 << (tag));

					sector[sp.sectnum].extra |= (bit);

					if (TEST(bit, SECTFX_SINK)) {
						sectu = GetSectUser(sp.sectnum);
						sectu.depth = sp.lotag;
						KillSprite(SpriteNum);
					} else if (TEST(bit, SECTFX_OPERATIONAL)) {
						KillSprite(SpriteNum);
					} else if (TEST(bit, SECTFX_CURRENT)) {
						sectu = GetSectUser(sp.sectnum);
						sectu.speed = sp.lotag;
						sectu.ang = sp.ang;
						KillSprite(SpriteNum);
					} else if (TEST(bit, SECTFX_NO_RIDE)) {
						change_sprite_stat(SpriteNum, STAT_NO_RIDE);
					} else if (TEST(bit, SECTFX_DIVE_AREA)) {
						sectu = GetSectUser(sp.sectnum);
						sectu.number = sp.lotag;
						change_sprite_stat(SpriteNum, STAT_DIVE_AREA);
					} else if (TEST(bit, SECTFX_UNDERWATER)) {
						sectu = GetSectUser(sp.sectnum);
						sectu.number = sp.lotag;
						change_sprite_stat(SpriteNum, STAT_UNDERWATER);
					} else if (TEST(bit, SECTFX_UNDERWATER2)) {
						sectu = GetSectUser(sp.sectnum);
						sectu.number = sp.lotag;
						if (sp.clipdist == 1)
							sectu.flags |= (SECTFU_CANT_SURFACE);
						change_sprite_stat(SpriteNum, STAT_UNDERWATER2);
					}
				} else {
					switch (tag) {
					case SECT_MATCH:
						sectu = GetSectUser(sp.sectnum);

						sectu.number = sp.lotag;

						KillSprite(SpriteNum);
						break;

					case SLIDE_SECTOR:
						sectu = GetSectUser(sp.sectnum);
						sectu.flags |= (SECTFU_SLIDE_SECTOR);
						sectu.speed = (short) SP_TAG2(sp);
						KillSprite(SpriteNum);
						break;

					case SECT_DAMAGE: {
						sectu = GetSectUser(sp.sectnum);
						if (TEST_BOOL1(sp))
							sectu.flags |= (SECTFU_DAMAGE_ABOVE_SECTOR);
						sectu.damage = sp.lotag;
						KillSprite(SpriteNum);
						break;
					}

					case PARALLAX_LEVEL: {
						pskybits = sp.lotag;
						if (SP_TAG4(sp) > 2048)
							parallaxyscale = SP_TAG4(sp);
						KillSprite(SpriteNum);
						break;
					}

					case BREAKABLE:
						// used for wall info
						change_sprite_stat(SpriteNum, STAT_BREAKABLE);
						break;

					case SECT_DONT_COPY_PALETTE: {
						sectu = GetSectUser(sp.sectnum);

						sectu.flags |= (SECTFU_DONT_COPY_PALETTE);
						KillSprite(SpriteNum);
						break;
					}

					case SECT_FLOOR_PAN: {
						// if moves with SO
						if (TEST_BOOL1(sp))
							sp.xvel = 0;
						else
							sp.xvel = sp.lotag;

						change_sprite_stat(SpriteNum, STAT_FLOOR_PAN);
						break;
					}

					case SECT_CEILING_PAN: {
						// if moves with SO
						if (TEST_BOOL1(sp))
							sp.xvel = 0;
						else
							sp.xvel = sp.lotag;
						change_sprite_stat(SpriteNum, STAT_CEILING_PAN);
						break;
					}

					case SECT_WALL_PAN_SPEED: {
						short hitsect, hitwall;

						engine.hitscan(sp.x, sp.y, sp.z - Z(8), sp.sectnum, // Start position
								sintable[NORM_ANGLE(sp.ang + 512)], // X vector of 3D ang
								sintable[sp.ang], // Y vector of 3D ang
								0, // Z vector of 3D ang
								pHitInfo, CLIPMASK_MISSILE);

						hitsect = pHitInfo.hitsect;
						hitwall = pHitInfo.hitwall;

						if (hitwall == -1) {
							KillSprite(SpriteNum);
							break;
						}

						sp.owner = hitwall;
						// if moves with SO
						if (TEST_BOOL1(sp))
							sp.xvel = 0;
						else
							sp.xvel = sp.lotag;
						sp.ang = (short) SP_TAG6(sp);
						// attach to the sector that contains the wall
						engine.changespritesect(SpriteNum, hitsect);
						change_sprite_stat(SpriteNum, STAT_WALL_PAN);
						break;
					}

					case WALL_DONT_STICK: {
						short hitwall;

						engine.hitscan(sp.x, sp.y, sp.z - Z(8), sp.sectnum, // Start position
								sintable[NORM_ANGLE(sp.ang + 512)], // X vector of 3D ang
								sintable[sp.ang], // Y vector of 3D ang
								0, // Z vector of 3D ang
								pHitInfo, CLIPMASK_MISSILE);

						hitwall = pHitInfo.hitwall;
						if (hitwall == -1) {
							KillSprite(SpriteNum);
							break;
						}

						wall[hitwall].extra |= (WALLFX_DONT_STICK);
						KillSprite(SpriteNum);
						break;
					}

					case TRIGGER_SECTOR: {
						sector[sp.sectnum].extra |= (SECTFX_TRIGGER);
						change_sprite_stat(SpriteNum, STAT_TRIGGER);
						break;
					}

					case DELETE_SPRITE: {
						change_sprite_stat(SpriteNum, STAT_DELETE_SPRITE);
						break;
					}

					case SPAWN_ITEMS: {
						if (TEST(sp.extra, SPRX_MULTI_ITEM)) {
							if ((numplayers <= 1 && !gNet.FakeMultiplayer)
									|| gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COOPERATIVE) {
								KillSprite(SpriteNum);
								break;
							}
						}

						change_sprite_stat(SpriteNum, STAT_SPAWN_ITEMS);
						break;
					}

					case CEILING_FLOOR_PIC_OVERRIDE: {
						// block hitscans depending on translucency
						if (SP_TAG7(sp) == 0 || SP_TAG7(sp) == 1) {
							if (SP_TAG3(sp) == 0)
								sector[sp.sectnum].ceilingstat |= (CEILING_STAT_FAF_BLOCK_HITSCAN);
							else
								sector[sp.sectnum].floorstat |= (FLOOR_STAT_FAF_BLOCK_HITSCAN);
						} else if (TEST_BOOL1(sp)) {
							if (SP_TAG3(sp) == 0)
								sector[sp.sectnum].ceilingstat |= (CEILING_STAT_FAF_BLOCK_HITSCAN);
							else
								sector[sp.sectnum].floorstat |= (FLOOR_STAT_FAF_BLOCK_HITSCAN);
						}

						// copy tag 7 to tag 6 and pre-shift it
						sp.yvel = (short) SP_TAG7(sp);
						sp.yvel <<= 7;
						change_sprite_stat(SpriteNum, STAT_CEILING_FLOOR_PIC_OVERRIDE);
						break;
					}

					case QUAKE_SPOT: {
						change_sprite_stat(SpriteNum, STAT_QUAKE_SPOT);
						SET_SP_TAG13(sp, ((SP_TAG6(sp) * 10) * 120));
						break;
					}

					case SECT_CHANGOR: {
						change_sprite_stat(SpriteNum, STAT_CHANGOR);
						break;
					}

					case SECT_VATOR: {
						SECTOR sectp = sector[sp.sectnum];
						short speed, vel, time, type;
						boolean floor_vator;
						boolean start_on;
						u = SpawnUser(SpriteNum, 0, null);

						// vator already set - ceiling AND floor vator
						if (TEST(sectp.extra, SECTFX_VATOR)) {
							sectu = GetSectUser(sp.sectnum);
							sectu.flags |= (SECTFU_VATOR_BOTH);
						}
						sectp.extra |= (SECTFX_VATOR);
						SetSectorWallBits(sp.sectnum, WALLFX_DONT_STICK, true, true);
						sector[sp.sectnum].extra |= (SECTFX_DYNAMIC_AREA);

						// don't step on toes of other sector settings
						if (sectp.lotag == 0 && sectp.hitag == 0)
							sectp.lotag = TAG_VATOR;

						type = (short) SP_TAG3(sp);
						speed = (short) SP_TAG4(sp);
						vel = (short) SP_TAG5(sp);
						time = (short) SP_TAG9(sp);
						start_on = !!TEST_BOOL1(sp);
						floor_vator = true;
						if (TEST(sp.cstat, CSTAT_SPRITE_YFLIP))
							floor_vator = false;

						u.jump_speed = (short) (u.vel_tgt = speed);
						u.vel_rate = vel;
						u.WaitTics = (short) (time * 15); // 1/8 of a sec
						u.Tics = 0;

						u.Flags |= (SPR_ACTIVE);

						switch (type) {
						case 0:
							u.Flags &= ~(SPR_ACTIVE);
							u.ActorActionFunc = DoVator;
							break;
						case 1:
							u.Flags &= ~(SPR_ACTIVE);
							u.ActorActionFunc = DoVator;
							break;
						case 2:
							u.ActorActionFunc = DoVatorAuto;
							break;
						case 3:
							u.Flags &= ~(SPR_ACTIVE);
							u.ActorActionFunc = DoVatorAuto;
							break;
						}

						if (floor_vator) {
							// start off
							u.sz = sectp.floorz;
							u.z_tgt = sp.z;
							if (start_on) {
								int amt;
								amt = sp.z - sectp.floorz;

								// start in the on position
								// sectp.floorz = sp.z;
								sectp.floorz += amt;
								u.z_tgt = u.sz;

								MoveSpritesWithSector(sp.sectnum, amt, false); // floor
							}

							// set orig z
							u.oz = sectp.floorz;
						} else {
							// start off
							u.sz = sectp.ceilingz;
							u.z_tgt = sp.z;
							if (start_on) {
								int amt;
								amt = sp.z - sectp.ceilingz;

								// starting in the on position
								// sectp.ceilingz = sp.z;
								sectp.ceilingz += amt;
								u.z_tgt = u.sz;

								MoveSpritesWithSector(sp.sectnum, amt, true); // ceiling
							}

							// set orig z
							u.oz = sectp.ceilingz;
						}

						change_sprite_stat(SpriteNum, STAT_VATOR);
						break;
					}

					case SECT_ROTATOR_PIVOT: {
						change_sprite_stat(SpriteNum, STAT_ROTATOR_PIVOT);
						break;
					}

					case SECT_ROTATOR: {
						SECTOR sectp = sector[sp.sectnum];
						short time, type;
						short wallcount, startwall, endwall, w;
						u = SpawnUser(SpriteNum, 0, null);

						SetSectorWallBits(sp.sectnum, WALLFX_DONT_STICK, true, true);

						// need something for this
						sectp.lotag = TAG_ROTATOR;
						sectp.hitag = sp.lotag;

						type = (short) SP_TAG3(sp);
						time = (short) SP_TAG9(sp);

						u.WaitTics = (short) (time * 15); // 1/8 of a sec
						u.Tics = 0;

						startwall = sector[sp.sectnum].wallptr;
						endwall = (short) (startwall + sector[sp.sectnum].wallnum - 1);

						// count walls of sector
						for (w = startwall, wallcount = 0; w <= endwall; w++)
							wallcount++;

						u.rotator = new RotatorStr();
						u.rotator.num_walls = wallcount;
						u.rotator.open_dest = SP_TAG5(sp);
						u.rotator.speed = SP_TAG7(sp);
						u.rotator.vel = SP_TAG8(sp);
						u.rotator.pos = 0; // closed
						u.rotator.tgt = u.rotator.open_dest; // closed
						u.rotator.orig = new Vector2i[wallcount];
						for (int a = 0; a < wallcount; a++)
							u.rotator.orig[a] = new Vector2i();

						u.rotator.orig_speed = u.rotator.speed;

						for (w = startwall, wallcount = 0; w <= endwall; w++) {
							u.rotator.orig[wallcount].x = wall[w].x;
							u.rotator.orig[wallcount].y = wall[w].y;
							wallcount++;
						}

						u.Flags |= (SPR_ACTIVE);

						switch (type) {
						case 0:
							u.Flags &= ~(SPR_ACTIVE);
							u.ActorActionFunc = DoRotator;
							break;
						case 1:
							u.Flags &= ~(SPR_ACTIVE);
							u.ActorActionFunc = DoRotator;
							break;
						}

						change_sprite_stat(SpriteNum, STAT_ROTATOR);
						break;
					}

					case SECT_SLIDOR: {
						SECTOR sectp = sector[sp.sectnum];
						short time, type;

						u = SpawnUser(SpriteNum, 0, null);

						SetSectorWallBits(sp.sectnum, WALLFX_DONT_STICK, true, true);

						// need something for this
						sectp.lotag = TAG_SLIDOR;
						sectp.hitag = sp.lotag;

						type = (short) SP_TAG3(sp);
						time = (short) SP_TAG9(sp);

						u.WaitTics = (short) (time * 15); // 1/8 of a sec
						u.Tics = 0;

						u.rotator = new RotatorStr();
						u.rotator.open_dest = SP_TAG5(sp);
						u.rotator.speed = SP_TAG7(sp);
						u.rotator.vel = SP_TAG8(sp);
						u.rotator.pos = 0; // closed
						u.rotator.tgt = u.rotator.open_dest; // closed
						u.rotator.num_walls = 0;
						u.rotator.orig_speed = u.rotator.speed;

						u.Flags |= (SPR_ACTIVE);

						switch (type) {
						case 0:
							u.Flags &= ~(SPR_ACTIVE);
							u.ActorActionFunc = DoSlidor;
							break;
						case 1:
							u.Flags &= ~(SPR_ACTIVE);
							u.ActorActionFunc = DoSlidor;
							break;
						}

						if (TEST_BOOL5(sp)) {
							DoSlidorInstantClose(SpriteNum);
						}

						change_sprite_stat(SpriteNum, STAT_SLIDOR);
						break;
					}

					case SECT_SPIKE: {
						short speed, vel, time, type;
						boolean floor_vator;
						boolean start_on;
						int floorz, ceilingz;
						u = SpawnUser(SpriteNum, 0, null);

						SetSectorWallBits(sp.sectnum, WALLFX_DONT_STICK, false, true);
						sector[sp.sectnum].extra |= (SECTFX_DYNAMIC_AREA);

						type = (short) SP_TAG3(sp);
						speed = (short) SP_TAG4(sp);
						vel = (short) SP_TAG5(sp);
						time = (short) SP_TAG9(sp);
						start_on = !!TEST_BOOL1(sp);
						floor_vator = true;
						if (TEST(sp.cstat, CSTAT_SPRITE_YFLIP))
							floor_vator = false;

						u.jump_speed = (short) (u.vel_tgt = speed);
						u.vel_rate = vel;
						u.WaitTics = (short) (time * 15); // 1/8 of a sec
						u.Tics = 0;

						u.Flags |= (SPR_ACTIVE);

						switch (type) {
						case 0:
							u.Flags &= ~(SPR_ACTIVE);
							u.ActorActionFunc = DoSpike;
							break;
						case 1:
							u.Flags &= ~(SPR_ACTIVE);
							u.ActorActionFunc = DoSpike;
							break;
						case 2:
							u.ActorActionFunc = DoSpikeAuto;
							break;
						case 3:
							u.Flags &= ~(SPR_ACTIVE);
							u.ActorActionFunc = DoSpikeAuto;
							break;
						}

						engine.getzrangepoint(sp.x, sp.y, sp.z, sp.sectnum, tmp_ptr[0], tmp_ptr[1], tmp_ptr[2],
								tmp_ptr[3]);
						ceilingz = tmp_ptr[0].value;
						floorz = tmp_ptr[2].value;

						if (floor_vator) {
							u.zclip = floorz;

							// start off
							u.sz = u.zclip;
							u.z_tgt = sp.z;
							if (start_on) {
								// start in the on position
								u.zclip = sp.z;
								u.z_tgt = u.sz;
								SpikeAlign(SpriteNum);
							}

							// set orig z
							u.oz = u.zclip;
						} else {
							u.zclip = ceilingz;

							// start off
							u.sz = u.zclip;
							u.z_tgt = sp.z;
							if (start_on) {
								// starting in the on position
								u.zclip = sp.z;
								u.z_tgt = u.sz;
								SpikeAlign(SpriteNum);
							}

							// set orig z
							u.oz = u.zclip;
						}

						change_sprite_stat(SpriteNum, STAT_SPIKE);
						break;
					}

					case LIGHTING: {
						short w, startwall, endwall;
						short wallcount;

						byte[] wall_shade;

						sp.z = 0;

						if (LIGHT_ShadeInc(sp) == 0)
							SET_SP_TAG7(sp, 1);

						// save off original floor and ceil shades
						(sp).xoffset = sector[sp.sectnum].floorshade;
						(sp).yoffset = sector[sp.sectnum].ceilingshade;

						startwall = sector[sp.sectnum].wallptr;
						endwall = (short) (startwall + sector[sp.sectnum].wallnum - 1);

						// count walls of sector
						for (w = startwall, wallcount = 0; w <= endwall; w++) {
							wallcount++;
							if (TEST_BOOL5(sp)) {
								if (wall[w].nextwall >= 0)
									wallcount++;
							}
						}

						pUser[SpriteNum] = u = SpawnUser(SpriteNum, 0, null);
						u.WallCount = wallcount;
						wall_shade = u.WallShade = new byte[u.WallCount];

						// save off original wall shades
						for (w = startwall, wallcount = 0; w <= endwall; w++) {
							wall_shade[wallcount] = wall[w].shade;
							wallcount++;
							if (TEST_BOOL5(sp)) {
								if (wall[w].nextwall >= 0) {
									wall_shade[wallcount] = wall[wall[w].nextwall].shade;
									wallcount++;
								}
							}
						}

						u.spal = (byte) sp.pal;

						// DON'T USE COVER function
						engine.changespritestat(SpriteNum, STAT_LIGHTING);
						break;
					}

					case LIGHTING_DIFFUSE: {
						short w, startwall, endwall;
						short wallcount;

						byte[] wall_shade;

						sp.z = 0;

						// save off original floor and ceil shades
						(sp).xoffset = sector[sp.sectnum].floorshade;
						(sp).yoffset = sector[sp.sectnum].ceilingshade;

						startwall = sector[sp.sectnum].wallptr;
						endwall = (short) (startwall + sector[sp.sectnum].wallnum - 1);

						// count walls of sector
						for (w = startwall, wallcount = 0; w <= endwall; w++) {
							wallcount++;
							if (TEST_BOOL5(sp)) {
								if (wall[w].nextwall >= 0)
									wallcount++;
							}
						}

						// !LIGHT
						// make an wall_shade array and put it in User
						pUser[SpriteNum] = u = SpawnUser(SpriteNum, 0, null);
						u.WallCount = wallcount;
						wall_shade = u.WallShade = new byte[u.WallCount]; // CallocMem(u.WallCount *
																			// sizeof(*u.WallShade), 1);

						// save off original wall shades
						for (w = startwall, wallcount = 0; w <= endwall; w++) {
							wall_shade[wallcount] = wall[w].shade;
							wallcount++;
							if (TEST_BOOL5(sp)) {
								if (wall[w].nextwall >= 0) {
									wall_shade[wallcount] = wall[wall[w].nextwall].shade;
									wallcount++;
								}
							}
						}

						// DON'T USE COVER function
						engine.changespritestat(SpriteNum, STAT_LIGHTING_DIFFUSE);
						break;
					}

					case SECT_VATOR_DEST:
						change_sprite_stat(SpriteNum, STAT_VATOR);
						break;

					case SO_WALL_DONT_MOVE_UPPER:
						change_sprite_stat(SpriteNum, STAT_WALL_DONT_MOVE_UPPER);
						break;

					case SO_WALL_DONT_MOVE_LOWER:
						change_sprite_stat(SpriteNum, STAT_WALL_DONT_MOVE_LOWER);
						break;

					case FLOOR_SLOPE_DONT_DRAW:
						change_sprite_stat(SpriteNum, STAT_FLOOR_SLOPE_DONT_DRAW);
						break;

					case DEMO_CAMERA:
						sp.yvel = sp.zvel = 100; // attempt horiz control
						change_sprite_stat(SpriteNum, STAT_DEMO_CAMERA);
						break;

					case LAVA_ERUPT: {
						u = SpawnUser(SpriteNum, ST1, null);

						change_sprite_stat(SpriteNum, STAT_NO_STATE);
						u.ActorActionFunc = DoLavaErupt;

						// interval between erupts
						if (SP_TAG10(sp) == 0)
							SET_SP_TAG10(sp, 20);

						// interval in seconds
						u.WaitTics = (short) (RANDOM_RANGE(SP_TAG10(sp)) * 120);

						// time to erupt
						if (SP_TAG9(sp) == 0)
							SET_SP_TAG9(sp, 10);

						sp.z += Z(30);

						break;
					}

					case SECT_EXPLODING_CEIL_FLOOR: {
						SECTOR sectp = sector[sp.sectnum];

						SetSectorWallBits(sp.sectnum, WALLFX_DONT_STICK, false, true);

						if (TEST(sectp.floorstat, FLOOR_STAT_SLOPE)) {
							sp.xvel = sectp.floorheinum;
							sectp.floorstat &= ~(FLOOR_STAT_SLOPE);
							sectp.floorheinum = 0;
						}

						if (TEST(sectp.ceilingstat, CEILING_STAT_SLOPE)) {
							sp.yvel = sectp.ceilingheinum;
							sectp.ceilingstat &= ~(CEILING_STAT_SLOPE);
							sectp.ceilingheinum = 0;
						}

						sp.ang = (short) (klabs(sectp.ceilingz - sectp.floorz) >> 8);

						sectp.ceilingz = sectp.floorz;

						change_sprite_stat(SpriteNum, STAT_EXPLODING_CEIL_FLOOR);
						break;
					}

					case SECT_COPY_SOURCE:
						change_sprite_stat(SpriteNum, STAT_COPY_SOURCE);
						break;

					case SECT_COPY_DEST: {
						SetSectorWallBits(sp.sectnum, WALLFX_DONT_STICK, false, true);
						change_sprite_stat(SpriteNum, STAT_COPY_DEST);
						break;
					}

					case SECT_WALL_MOVE:
						change_sprite_stat(SpriteNum, STAT_WALL_MOVE);
						break;
					case SECT_WALL_MOVE_CANSEE:
						change_sprite_stat(SpriteNum, STAT_WALL_MOVE_CANSEE);
						break;

					case SPRI_CLIMB_MARKER: {
						short ns;
						SPRITE np;

						// setup climb marker
						change_sprite_stat(SpriteNum, STAT_CLIMB_MARKER);

						// make a QUICK_LADDER sprite automatically
						ns = COVERinsertsprite(sp.sectnum, STAT_QUICK_LADDER);
						np = sprite[ns];

						np.cstat = 0;
						np.extra = 0;
						np.x = sp.x;
						np.y = sp.y;
						np.z = sp.z;
						np.ang = NORM_ANGLE(sp.ang + 1024);
						np.picnum = sp.picnum;

						np.x += MOVEx(256 + 128, sp.ang);
						np.y += MOVEy(256 + 128, sp.ang);

						break;
					}

					case SO_AUTO_TURRET:
						change_sprite_stat(SpriteNum, STAT_ST1);
						break;

					case SO_DRIVABLE_ATTRIB:
					case SO_SCALE_XY_MULT:
					case SO_SCALE_INFO:
					case SO_SCALE_POINT_INFO:
					case SO_TORNADO:
					case SO_FLOOR_MORPH:
					case SO_AMOEBA:
					case SO_SET_SPEED:
					case SO_ANGLE:
					case SO_SPIN:
					case SO_SPIN_REVERSE:
					case SO_BOB_START:
					case SO_BOB_SPEED:
					case SO_TURN_SPEED:
					case SO_SYNC1:
					case SO_SYNC2:
					case SO_LIMIT_TURN:
					case SO_MATCH_EVENT:
					case SO_MAX_DAMAGE:
					case SO_RAM_DAMAGE:
					case SO_SLIDE:
					case SO_KILLABLE:
					case SECT_SO_SPRITE_OBJ:
					case SECT_SO_DONT_ROTATE:
					case SECT_SO_CLIP_DIST: {
						// NOTE: These will get deleted by the sector
						// object
						// setup code

						change_sprite_stat(SpriteNum, STAT_ST1);
						break;
					}

					case SOUND_SPOT:
						SET_SP_TAG13(sp, SP_TAG4(sp));
						change_sprite_stat(SpriteNum, STAT_SOUND_SPOT);
						break;

					case STOP_SOUND_SPOT:
						change_sprite_stat(SpriteNum, STAT_STOP_SOUND_SPOT);
						break;

					case SPAWN_SPOT:
						if (pUser[SpriteNum] == null)
							u = SpawnUser(SpriteNum, ST1, null);

						if (SP_TAG14(sp) == ((64 << 8) | 64))
							SET_SP_TAG14(sp, 0);

						change_sprite_stat(SpriteNum, STAT_SPAWN_SPOT);
						break;

					case VIEW_THRU_CEILING:
					case VIEW_THRU_FLOOR: {

						// make sure there is only one set per level of these
						for (i = headspritestat[STAT_FAF]; i != -1; i = nexti) {
							nexti = nextspritestat[i];
							if (sprite[i].hitag == sp.hitag && sprite[i].lotag == sp.lotag) {
								game.GameCrash("Two VIEW_THRU_ tags with same match found on level\n1: x " + sp.x
										+ ", y " + sp.y + " \n2: x " + sprite[i].x + ", y " + sprite[i].y);
								return false;
							}
						}
						change_sprite_stat(SpriteNum, STAT_FAF);
						break;
					}

					case VIEW_LEVEL1:
					case VIEW_LEVEL2:
					case VIEW_LEVEL3:
					case VIEW_LEVEL4:
					case VIEW_LEVEL5:
					case VIEW_LEVEL6: {
						change_sprite_stat(SpriteNum, STAT_FAF);
						break;
					}

					case PLAX_GLOB_Z_ADJUST: {
						sector[sp.sectnum].extra |= (SECTFX_Z_ADJUST);
						PlaxCeilGlobZadjust = SP_TAG2(sp);
						PlaxFloorGlobZadjust = SP_TAG3(sp);
						KillSprite(SpriteNum);
						break;
					}

					case CEILING_Z_ADJUST: {
						// SET(sector[sp.sectnum].ceilingstat, CEILING_STAT_FAF_BLOCK_HITSCAN);
						sector[sp.sectnum].extra |= (SECTFX_Z_ADJUST);
						change_sprite_stat(SpriteNum, STAT_ST1);
						break;
					}

					case FLOOR_Z_ADJUST: {
						// SET(sector[sp.sectnum].floorstat, FLOOR_STAT_FAF_BLOCK_HITSCAN);
						sector[sp.sectnum].extra |= (SECTFX_Z_ADJUST);
						change_sprite_stat(SpriteNum, STAT_ST1);
						break;
					}

					case WARP_TELEPORTER: {
						short start_wall, wall_num;
						short sectnum = sp.sectnum;

						sp.cstat |= (CSTAT_SPRITE_INVISIBLE);
						sector[sp.sectnum].extra |= (SECTFX_WARP_SECTOR);
						change_sprite_stat(SpriteNum, STAT_WARP);

						// if just a destination teleporter
						// don't set up flags
						if (SP_TAG10(sp) == 1)
							break;

						// move the the next wall
						wall_num = start_wall = sector[sectnum].wallptr;

						// Travel all the way around loop setting wall bits
						do {
							// DO NOT TAG WHITE WALLS!
							if (wall[wall_num].nextwall >= 0) {
								wall[wall_num].cstat |= (CSTAT_WALL_WARP_HITSCAN);
							}

							wall_num = wall[wall_num].point2;
						} while (wall_num != start_wall);

						break;
					}

					case WARP_CEILING_PLANE:
					case WARP_FLOOR_PLANE: {
						sp.cstat |= (CSTAT_SPRITE_INVISIBLE);
						sector[sp.sectnum].extra |= (SECTFX_WARP_SECTOR);
						change_sprite_stat(SpriteNum, STAT_WARP);
						break;
					}

					case WARP_COPY_SPRITE1:
						sp.cstat |= (CSTAT_SPRITE_INVISIBLE);
						sector[sp.sectnum].extra |= (SECTFX_WARP_SECTOR);
						change_sprite_stat(SpriteNum, STAT_WARP_COPY_SPRITE1);
						break;
					case WARP_COPY_SPRITE2:
						sp.cstat |= (CSTAT_SPRITE_INVISIBLE);
						sector[sp.sectnum].extra |= (SECTFX_WARP_SECTOR);
						change_sprite_stat(SpriteNum, STAT_WARP_COPY_SPRITE2);
						break;

					case FIREBALL_TRAP:
					case BOLT_TRAP:
					case SPEAR_TRAP: {
						u = SpawnUser(SpriteNum, 0, null);
						sp.owner = -1;
						change_sprite_stat(SpriteNum, STAT_TRAP);
						break;
					}

					case SECT_SO_DONT_BOB: {
						sectu = GetSectUser(sp.sectnum);
						sectu.flags |= (SECTFU_SO_DONT_BOB);
						KillSprite(SpriteNum);
						break;
					}

					case SECT_LOCK_DOOR: {
						sectu = GetSectUser(sp.sectnum);
						sectu.number = sp.lotag;
						sectu.stag = SECT_LOCK_DOOR;
						KillSprite(SpriteNum);
						break;
					}

					case SECT_SO_SINK_DEST: {
						sectu = GetSectUser(sp.sectnum);
						sectu.flags |= (SECTFU_SO_SINK_DEST);
						sectu.number = sp.lotag; // acually the offset Z
						// value
						KillSprite(SpriteNum);
						break;
					}

					case SECT_SO_DONT_SINK: {
						sectu = GetSectUser(sp.sectnum);
						sectu.flags |= (SECTFU_SO_DONT_SINK);
						KillSprite(SpriteNum);
						break;
					}

					case SO_SLOPE_FLOOR_TO_POINT: {
						sectu = GetSectUser(sp.sectnum);
						sectu.flags |= (SECTFU_SO_SLOPE_FLOOR_TO_POINT);
						sector[sp.sectnum].extra |= (SECTFX_DYNAMIC_AREA);
						KillSprite(SpriteNum);
						break;
					}

					case SO_SLOPE_CEILING_TO_POINT: {
						sectu = GetSectUser(sp.sectnum);
						sectu.flags |= (SECTFU_SO_SLOPE_CEILING_TO_POINT);
						sector[sp.sectnum].extra |= (SECTFX_DYNAMIC_AREA);
						KillSprite(SpriteNum);
						break;
					}
					case SECT_SO_FORM_WHIRLPOOL: {
						sectu = GetSectUser(sp.sectnum);
						sectu.stag = SECT_SO_FORM_WHIRLPOOL;
						sectu.height = sp.lotag;
						KillSprite(SpriteNum);
						break;
					}

					case SECT_ACTOR_BLOCK: {
						short start_wall, wall_num;
						short sectnum = sp.sectnum;

						// move the the next wall
						wall_num = start_wall = sector[sectnum].wallptr;

						// Travel all the way around loop setting wall bits
						do {
							wall[wall_num].cstat |= (CSTAT_WALL_BLOCK_ACTOR);
							if (wall[wall_num].nextwall >= 0)
								wall[wall[wall_num].nextwall].cstat |= (CSTAT_WALL_BLOCK_ACTOR);
							wall_num = wall[wall_num].point2;
						} while (wall_num != start_wall);

						KillSprite(SpriteNum);
						break;
					}
					}
				}
			}
				break;

			case RED_CARD:
			case RED_KEY:
			case BLUE_CARD:
			case BLUE_KEY:
			case GREEN_CARD:
			case GREEN_KEY:
			case YELLOW_CARD:
			case YELLOW_KEY:
			case GOLD_SKELKEY:
			case SILVER_SKELKEY:
			case BRONZE_SKELKEY:
			case RED_SKELKEY:

				switch (sprite[SpriteNum].picnum) {
				case RED_CARD:
					num = 4;
					break;
				case RED_KEY:
					num = 0;
					break;
				case BLUE_CARD:
					num = 5;
					break;
				case BLUE_KEY:
					num = 1;
					break;
				case GREEN_CARD:
					num = 6;
					break;
				case GREEN_KEY:
					num = 2;
					break;
				case YELLOW_CARD:
					num = 7;
					break;
				case YELLOW_KEY:
					num = 3;
					break;
				case GOLD_SKELKEY:
					num = 8;
					break;
				case SILVER_SKELKEY:
					num = 9;
					break;
				case BRONZE_SKELKEY:
					num = 10;
					break;
				case RED_SKELKEY:
					num = 11;
					break;
				}

			{

				if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COMMBAT
						|| gNet.MultiGameType == MultiGameTypes.MULTI_GAME_AI_BOTS) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, 0, null);

				sprite[SpriteNum].picnum = u.ID = sprite[SpriteNum].picnum;

				u.spal = (byte) sprite[SpriteNum].pal; // Set the palette from build

				ChangeState(SpriteNum, s_Key[num][0]);
				engine.getTile(sp.picnum).anm &= ~(TILE_ANIM_TYPE);
				engine.getTile(sp.picnum + 1).anm &= ~(TILE_ANIM_TYPE);
				change_sprite_stat(SpriteNum, STAT_ITEM);
				sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN | CSTAT_SPRITE_ONE_SIDE);
				u.Radius = 500;
				sp.hitag = LUMINOUS; // Set so keys over ride colored lighting

				DoActorZrange(SpriteNum);
			}
				break;

			// Used for multiplayer locks
			case 1846:
			case 1850:
			case 1852:
			case 2470:
				if (TEST(sprite[SpriteNum].extra, SPRX_MULTI_ITEM))
					if ((numplayers <= 1 && !gNet.FakeMultiplayer)
							|| gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COOPERATIVE) {
						KillSprite(SpriteNum);
					}
				break;

			case FIRE_FLY0:
				break;

			case ICON_REPAIR_KIT:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_REPAIR_KIT, s_RepairKit[0]);

				IconDefault(SpriteNum);
				break;

			case ICON_STAR:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_STAR, s_IconStar[0]);

				IconDefault(SpriteNum);
				break;

			case ICON_LG_MINE:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_LG_MINE, s_IconLgMine[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_MICRO_GUN:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_MICRO_GUN, s_IconMicroGun[0]);

				IconDefault(SpriteNum);
				break;

			case ICON_MICRO_BATTERY:
				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_MICRO_BATTERY, s_IconMicroBattery[0]);

				IconDefault(SpriteNum);
				break;

			case ICON_UZI:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_UZI, s_IconUzi[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_UZIFLOOR:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_UZIFLOOR, s_IconUziFloor[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_LG_UZI_AMMO:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_LG_UZI_AMMO, s_IconLgUziAmmo[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_GRENADE_LAUNCHER:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_GRENADE_LAUNCHER, s_IconGrenadeLauncher[0]);

				IconDefault(SpriteNum);
				break;

			case ICON_LG_GRENADE:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_LG_GRENADE, s_IconLgGrenade[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_RAIL_GUN:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_RAIL_GUN, s_IconRailGun[0]);

				IconDefault(SpriteNum);
				break;

			case ICON_RAIL_AMMO:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_RAIL_AMMO, s_IconRailAmmo[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_ROCKET:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_ROCKET, s_IconRocket[0]);

				IconDefault(SpriteNum);
				break;

			case ICON_LG_ROCKET:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_LG_ROCKET, s_IconLgRocket[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_SHOTGUN:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_SHOTGUN, s_IconShotgun[0]);

				u.Radius = 350; // Shotgun is hard to pick up for some reason.

				IconDefault(SpriteNum);
				break;

			case ICON_LG_SHOTSHELL:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_LG_SHOTSHELL, s_IconLgShotshell[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_AUTORIOT:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_AUTORIOT, s_IconAutoRiot[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_GUARD_HEAD:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_GUARD_HEAD, s_IconGuardHead[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_FIREBALL_LG_AMMO:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_FIREBALL_LG_AMMO, s_IconFireballLgAmmo[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_HEART:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_HEART, s_IconHeart[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_HEART_LG_AMMO:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_HEART_LG_AMMO, s_IconHeartLgAmmo[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_SPELL:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_SPELL, s_IconSpell[0]);
				IconDefault(SpriteNum);

				PicAnimOff(sp.picnum);
				break;

			case ICON_ARMOR:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_ARMOR, s_IconArmor[0]);
				if (sp.pal != PALETTE_PLAYER3)
					sp.pal = u.spal = PALETTE_PLAYER1;
				else
					sp.pal = u.spal = PALETTE_PLAYER3;
				IconDefault(SpriteNum);
				break;

			case ICON_MEDKIT:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_MEDKIT, s_IconMedkit[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_SM_MEDKIT:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_SM_MEDKIT, s_IconSmMedkit[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_CHEMBOMB:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_CHEMBOMB, s_IconChemBomb[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_FLASHBOMB:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_FLASHBOMB, s_IconFlashBomb[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_NUKE:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				if (gNet.MultiGameType != null) {
					if (!gNet.Nuke) {
						u = SpawnUser(SpriteNum, ICON_MICRO_BATTERY, s_IconMicroBattery[0]);

						IconDefault(SpriteNum);
						break;
					}
				}

				u = SpawnUser(SpriteNum, ICON_NUKE, s_IconNuke[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_CALTROPS:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_CALTROPS, s_IconCaltrops[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_BOOSTER:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_BOOSTER, s_IconBooster[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_HEAT_CARD:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_HEAT_CARD, s_IconHeatCard[0]);
				IconDefault(SpriteNum);
				break;

			case ICON_CLOAK:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_CLOAK, s_IconCloak[0]);
				IconDefault(SpriteNum);
				PicAnimOff(sp.picnum);
				break;

			case ICON_FLY:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_FLY, s_IconFly[0]);
				IconDefault(SpriteNum);
				PicAnimOff(sp.picnum);
				break;

			case ICON_NIGHT_VISION:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_NIGHT_VISION, s_IconNightVision[0]);
				IconDefault(SpriteNum);
				PicAnimOff(sp.picnum);
				break;

			case ICON_FLAG:

				if (!IconSpawn(sp)) {
					KillSprite(SpriteNum);
					break;
				}

				u = SpawnUser(SpriteNum, ICON_FLAG, s_IconFlag[0]);
				u.spal = (byte) sp.pal;
				sector[sp.sectnum].hitag = 9000; // Put flag's color in sect containing it
				sector[sp.sectnum].lotag = u.spal;
				IconDefault(SpriteNum);
				PicAnimOff(sp.picnum);
				break;

			case 3143:
			case 3157: {
				u = SpawnUser(SpriteNum, sp.picnum, null);

				change_sprite_stat(SpriteNum, STAT_STATIC_FIRE);

				u.ID = FIREBALL_FLAMES;
				u.Radius = 200;
				sp.cstat &= ~(CSTAT_SPRITE_BLOCK);
				sp.cstat &= ~(CSTAT_SPRITE_BLOCK_HITSCAN);

				sp.hitag = LUMINOUS; // Always full brightness
				sp.shade = -40;

				break;
			}

			// blades
			case BLADE1:
			case BLADE2:
			case BLADE3:
			case 5011: {
				u = SpawnUser(SpriteNum, sp.picnum, null);

				change_sprite_stat(SpriteNum, STAT_DEFAULT);

				sp.cstat &= ~(CSTAT_SPRITE_BLOCK);
				sp.cstat |= (CSTAT_SPRITE_BLOCK_HITSCAN);
				sp.extra |= (SPRX_BLADE);

				break;
			}

			case BREAK_LIGHT:
			case BREAK_BARREL:
			case BREAK_PEDISTAL:
			case BREAK_BOTTLE1:
			case BREAK_BOTTLE2:
			case BREAK_MUSHROOM:
				u = SpawnUser(SpriteNum, sp.picnum, null);

				sp.clipdist = SPRITEp_SIZE_X(sp);
				sp.cstat |= (CSTAT_SPRITE_BREAKABLE);
				sp.extra |= (SPRX_BREAKABLE);
				break;

			// switches
			case 581:
			case 582:
			case 558:
			case 559:
			case 560:
			case 561:
			case 562:
			case 563:
			case 564:
			case 565:
			case 566:
			case 567:
			case 568:
			case 569:
			case 570:
			case 571:
			case 572:
			case 573:
			case 574:

			case 551:
			case 552:
			case 575:
			case 576:
			case 577:
			case 578:
			case 579:
			case 589:
			case 583:
			case 584:

			case 553:
			case 554: {
				if (TEST(sp.extra, SPRX_MULTI_ITEM)) {
					if ((numplayers <= 1 && !gNet.FakeMultiplayer)
							|| gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COOPERATIVE) {
						KillSprite(SpriteNum);
						break;
					}
				}

				sp.cstat |= (CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
				break;
			}

			}
		}

		return true;
	}

	public static boolean ItemSpotClear(SPRITE sip, int statnum, int id) {
		boolean found = false;
		short i, nexti;

		if (TEST_BOOL2(sip)) {
			for (i = headspritesect[sip.sectnum]; i != -1; i = nexti) {
				nexti = nextspritesect[i];
				if (sprite[i].statnum == statnum && pUser[i].ID == id) {
					found = true;
					break;
				}
			}
		}

		return (!found);
	}

	public static void SetupItemForJump(SPRITE sip, int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		// setup item for jumping
		if (SP_TAG7(sip) != 0) {
			change_sprite_stat(SpriteNum, STAT_SKIP4);
			u.ceiling_dist = (short) Z(6);
			u.floor_dist = (short) Z(0);
			u.Counter = 0;

			sp.xvel = (short) (SP_TAG7(sip) << 2);
			sp.zvel = (short) -((SP_TAG8(sip)) << 5);

			u.xchange = MOVEx(sp.xvel, sp.ang);
			u.ychange = MOVEy(sp.xvel, sp.ang);
			u.zchange = sp.zvel;
		}
	}

	public static int ActorCoughItem(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		short newsp, choose;
		SPRITE np;

		switch (u.ID) {
		case SAILORGIRL_R0:

			newsp = COVERinsertsprite(sp.sectnum, STAT_SPAWN_ITEMS);

			np = sprite[newsp];
			np.cstat = np.extra = 0;
			np.x = sp.x;
			np.y = sp.y;
			np.z = SPRITEp_MID(sp);
			np.ang = 0;
			np.extra = 0;

			// vel | zvel
			np.zvel = 40 | (1 << 8);

			choose = (short) RANDOM_P2(1024);

			if (choose > 854)
				np.clipdist = 91; // Match number
			else if (choose > 684)
				np.clipdist = 48; // Match number
			else if (choose > 514)
				np.clipdist = 58; // Match number
			else if (choose > 344)
				np.clipdist = 60; // Match number
			else if (choose > 174)
				np.clipdist = 62; // Match number
			else
				np.clipdist = 68; // Match number

			// match
			np.lotag = -1;
			// kill
			RESET_BOOL1(np);
			SpawnItemsMatch(-1);
			break;

		case GORO_RUN_R0:
			if (RANDOM_P2(1024) < 700)
				return (0);

			newsp = COVERinsertsprite(sp.sectnum, STAT_SPAWN_ITEMS);

			np = sprite[newsp];
			np.cstat = np.extra = 0;
			np.x = sp.x;
			np.y = sp.y;
			np.z = SPRITEp_MID(sp);
			np.ang = 0;
			np.extra = 0;

			// vel | zvel
			np.zvel = 40 | (1 << 8);

			np.clipdist = 69; // Match number

			// match
			np.lotag = -1;
			// kill
			RESET_BOOL1(np);
			SpawnItemsMatch(-1);
			break;

		case RIPPER2_RUN_R0:
			if (RANDOM_P2(1024) < 700)
				return (0);

			newsp = COVERinsertsprite(sp.sectnum, STAT_SPAWN_ITEMS);
			np = sprite[newsp];
			np.cstat = np.extra = 0;
			np.x = sp.x;
			np.y = sp.y;
			np.z = SPRITEp_MID(sp);
			np.ang = 0;
			np.extra = 0;

			// vel | zvel
			np.zvel = 40 | (1 << 8);

			np.clipdist = 70; // Match number

			// match
			np.lotag = -1;
			// kill
			RESET_BOOL1(np);
			SpawnItemsMatch(-1);
			break;

		case NINJA_RUN_R0:

			if (u.PlayerP != -1) {
				if (RANDOM_P2(1024) > 200)
					return (0);

				newsp = COVERinsertsprite(sp.sectnum, STAT_SPAWN_ITEMS);
				np = sprite[newsp];
				np.cstat = 0;
				np.extra = 0;
				np.x = sp.x;
				np.y = sp.y;
				np.z = SPRITEp_MID(sp);
				np.ang = 0;
				np.extra = 0;

				// vel | zvel
				np.zvel = 40 | (1 << 8);

				switch (u.WeaponNum) {
				case WPN_UZI:
					np.clipdist = 0;
					break;
				case WPN_SHOTGUN:
					np.clipdist = 51;
					break;
				case WPN_STAR:
					if (Player[u.PlayerP].WpnAmmo[WPN_STAR] < 9)
						break;
					np.clipdist = 41;
					break;
				case WPN_MINE:
					if (Player[u.PlayerP].WpnAmmo[WPN_MINE] < 5)
						break;
					np.clipdist = 42;
					break;
				case WPN_MICRO:
				case WPN_ROCKET:
					np.clipdist = 43;
					break;
				case WPN_GRENADE:
					np.clipdist = 45;
					break;
				case WPN_RAIL:
					np.clipdist = 47;
					break;
				case WPN_HEART:
					np.clipdist = 55;
					break;
				case WPN_HOTHEAD:
					np.clipdist = 53;
					break;
				}

				// match
				np.lotag = -1;
				// kill
				RESET_BOOL1(np);
				SpawnItemsMatch(-1);
				break;
			}

			if (RANDOM_P2(1024) < 512)
				return (0);

			newsp = COVERinsertsprite(sp.sectnum, STAT_SPAWN_ITEMS);
			np = sprite[newsp];
			np.cstat = np.extra = 0;
			np.x = sp.x;
			np.y = sp.y;
			np.z = SPRITEp_MID(sp);
			np.ang = 0;
			np.extra = 0;

			// vel | zvel
			np.zvel = 40 | (1 << 8);

			if (u.spal == PAL_XLAT_LT_TAN) {
				np.clipdist = 44;
			} else if (u.spal == PAL_XLAT_LT_GREY) {
				np.clipdist = 46;
			} else if (u.spal == PALETTE_PLAYER5) // Green Ninja
			{
				if (RANDOM_P2(1024) < 700)
					np.clipdist = 61;
				else
					np.clipdist = 60;
			} else if (u.spal == PALETTE_PLAYER3) // Red Ninja
			{
				// type
				if (RANDOM_P2(1024) < 800)
					np.clipdist = 68;
				else
					np.clipdist = 44;
			} else {
				if (RANDOM_P2(1024) < 512)
					np.clipdist = 41;
				else
					np.clipdist = 68;
			}

			// match
			np.lotag = -1;
			// kill
			RESET_BOOL1(np);
			SpawnItemsMatch(-1);
			break;

		case PACHINKO1:
		case PACHINKO2:
		case PACHINKO3:
		case PACHINKO4:

			newsp = COVERinsertsprite(sp.sectnum, STAT_SPAWN_ITEMS);
			np = sprite[newsp];
			np.cstat = np.extra = 0;
			np.x = sp.x;
			np.y = sp.y;
			np.z = SPRITEp_LOWER(sp) + Z(10);
			np.ang = sp.ang;

			// vel | zvel
			np.zvel = 10 | (10 << 8);

			if (u.ID == PACHINKO1) {
				if (RANDOM_P2(1024) < 600)
					np.clipdist = 64; // Small MedKit
				else
					np.clipdist = 59; // Fortune Cookie
			} else if (u.ID == PACHINKO2) {
				if (RANDOM_P2(1024) < 600)
					np.clipdist = 52; // Lg Shot Shell
				else
					np.clipdist = 68; // Uzi clip
			} else if (u.ID == PACHINKO3) {
				if (RANDOM_P2(1024) < 600)
					np.clipdist = 57;
				else
					np.clipdist = 63;
			} else if (u.ID == PACHINKO4) {
				if (RANDOM_P2(1024) < 600)
					np.clipdist = 60;
				else
					np.clipdist = 61;
			}

			// match
			np.lotag = -1;
			// kill
			RESET_BOOL1(np);
			SpawnItemsMatch(-1);
			break;
		}

		return (0);
	}

	private static final int KeyPal[] = { PALETTE_PLAYER9, PALETTE_PLAYER7, PALETTE_PLAYER6, PALETTE_PLAYER4,
			PALETTE_PLAYER9, PALETTE_PLAYER7, PALETTE_PLAYER6, PALETTE_PLAYER4, PALETTE_PLAYER4, PALETTE_PLAYER1,
			PALETTE_PLAYER8, PALETTE_PLAYER9 };

	public static int SpawnItemsMatch(int match) {
		int SpriteNum;
		short si, nextsi;
		SPRITE sp, sip;

		for (si = headspritestat[STAT_SPAWN_ITEMS]; si != -1; si = nextsi) {
			nextsi = nextspritestat[si];
			sip = sprite[si];

			if (SP_TAG2(sip) != match)
				continue;

			switch (SP_TAG3(sip)) {
			case 90:
				SpriteNum = BunnyHatch2(si);
				sp = sprite[SpriteNum];
				pUser[SpriteNum].spal = (byte) (sp.pal = PALETTE_PLAYER8); // Boy
				sp.ang = sip.ang;
				break;
			case 91:
				SpriteNum = BunnyHatch2(si);
				sp = sprite[SpriteNum];
				pUser[SpriteNum].spal = (byte) (sp.pal = PALETTE_PLAYER0); // Girl
				sp.ang = sip.ang;
				break;
			case 92:
				SpriteNum = BunnyHatch2(si);
				sp = sprite[SpriteNum];
				sp.ang = sip.ang;
				break;

			case 40:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_REPAIR_KIT))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_REPAIR_KIT, s_RepairKit[0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 41:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_STAR))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_STAR, s_IconStar[0], sip.sectnum, sip.x, sip.y, sip.z, sip.ang,
						0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 42:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_LG_MINE))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_LG_MINE, s_IconLgMine[0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 43:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_MICRO_GUN))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_MICRO_GUN, s_IconMicroGun[0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 44:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_MICRO_BATTERY))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_MICRO_BATTERY, s_IconMicroBattery[0], sip.sectnum, sip.x, sip.y,
						sip.z, sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 45:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_GRENADE_LAUNCHER))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_GRENADE_LAUNCHER, s_IconGrenadeLauncher[0], sip.sectnum, sip.x,
						sip.y, sip.z, sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 46:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_LG_GRENADE))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_LG_GRENADE, s_IconLgGrenade[0], sip.sectnum, sip.x, sip.y,
						sip.z, sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 47:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_RAIL_GUN))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_RAIL_GUN, s_IconRailGun[0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 48:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_RAIL_AMMO))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_RAIL_AMMO, s_IconRailAmmo[0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 49:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_ROCKET))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_ROCKET, s_IconRocket[0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 51:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_SHOTGUN))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_SHOTGUN, s_IconShotgun[0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 52:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_LG_SHOTSHELL))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_LG_SHOTSHELL, s_IconLgShotshell[0], sip.sectnum, sip.x, sip.y,
						sip.z, sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 53:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_GUARD_HEAD))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_GUARD_HEAD, s_IconGuardHead[0], sip.sectnum, sip.x, sip.y,
						sip.z, sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 54:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_FIREBALL_LG_AMMO))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_FIREBALL_LG_AMMO, s_IconFireballLgAmmo[0], sip.sectnum, sip.x,
						sip.y, sip.z, sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 55:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_HEART))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_HEART, s_IconHeart[0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 56:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_HEART_LG_AMMO))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_HEART_LG_AMMO, s_IconHeartLgAmmo[0], sip.sectnum, sip.x, sip.y,
						sip.z, sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 57: {
				USER u;
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_ARMOR))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_ARMOR, s_IconArmor[0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				sp = sprite[SpriteNum];
				u = pUser[SpriteNum];
				u.Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);

				if (sp.pal != PALETTE_PLAYER3)
					sp.pal = u.spal = PALETTE_PLAYER1;
				else
					sp.pal = u.spal = PALETTE_PLAYER3;
				break;
			}

			case 58:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_MEDKIT))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_MEDKIT, s_IconMedkit[0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 59:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_SM_MEDKIT))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_SM_MEDKIT, s_IconSmMedkit[0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 60:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_CHEMBOMB))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_CHEMBOMB, s_IconChemBomb[0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 61:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_FLASHBOMB))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_FLASHBOMB, s_IconFlashBomb[0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 62:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_NUKE))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_NUKE, s_IconNuke[0], sip.sectnum, sip.x, sip.y, sip.z, sip.ang,
						0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 63:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_CALTROPS))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_CALTROPS, s_IconCaltrops[0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 64:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_BOOSTER))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_BOOSTER, s_IconBooster[0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 65:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_HEAT_CARD))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_HEAT_CARD, s_IconHeatCard[0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 66:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_CLOAK))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_CLOAK, s_IconCloak[0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 67:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_NIGHT_VISION))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_NIGHT_VISION, s_IconNightVision[0], sip.sectnum, sip.x, sip.y,
						sip.z, sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 68:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_LG_UZI_AMMO))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_LG_UZI_AMMO, s_IconLgUziAmmo[0], sip.sectnum, sip.x, sip.y,
						sip.z, sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 69:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_GUARD_HEAD))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_GUARD_HEAD, s_IconGuardHead[0], sip.sectnum, sip.x, sip.y,
						sip.z, sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 70:
				if (!ItemSpotClear(sip, STAT_ITEM, ICON_HEART))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_HEART, s_IconHeart[0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 20:

				if (!ItemSpotClear(sip, STAT_ITEM, ICON_UZIFLOOR))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_UZIFLOOR, s_IconUziFloor[0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 32:
			case 0:

				if (!ItemSpotClear(sip, STAT_ITEM, ICON_UZI))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, ICON_UZI, s_IconUzi[0], sip.sectnum, sip.x, sip.y, sip.z, sip.ang,
						0);
				pUser[SpriteNum].Flags2 |= (SPR2_NEVER_RESPAWN);
				IconDefault(SpriteNum);

				SetupItemForJump(sip, SpriteNum);
				break;

			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12: {
				short num;
				USER u;

				if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COMMBAT
						|| gNet.MultiGameType == MultiGameTypes.MULTI_GAME_AI_BOTS)
					break;

				num = (short) (SP_TAG3(sip) - 1);

				if (!ItemSpotClear(sip, STAT_ITEM, s_Key[num][0].Pic))
					break;

				SpriteNum = SpawnSprite(STAT_ITEM, s_Key[num][0].Pic, s_Key[num][0], sip.sectnum, sip.x, sip.y, sip.z,
						sip.ang, 0);
				u = pUser[SpriteNum];

				sp = sprite[SpriteNum];

				sprite[SpriteNum].picnum = u.ID = s_Key[num][0].Pic;

				// need to set the palette here - suggest table lookup
				u.spal = (byte) (sprite[SpriteNum].pal = (short) KeyPal[num]);

				ChangeState(SpriteNum, s_Key[num][0]);

				engine.getTile(sp.picnum).anm &= ~(TILE_ANIM_TYPE);
				engine.getTile(sp.picnum + 1).anm &= ~(TILE_ANIM_TYPE);

				SetupItemForJump(sip, SpriteNum);

				break;
			}
			}

			if (!TEST_BOOL1(sip))
				KillSprite(si);
		}
		return (0);
	}

	public static void NewStateGroup(int SpriteNum, StateGroup StateGroup) {
		USER u = pUser[SpriteNum];
		if (StateGroup == null)
			return;

		// Kind of a goofy check, but it should catch alot of invalid states!
		// BTW, 6144 is the max tile number allowed in editart.
		if (u.State != null && !isValidTile(u.State.Pic))
			return;

		u.Rot = StateGroup;
		u.State = u.StateStart = StateGroup.getState(0);
		u.Tics = 0;

		// turn anims off because people keep setting them in the art file
		engine.getTile(sprite[SpriteNum].picnum).anm &= ~(TILE_ANIM_TYPE);
	}

	public static boolean SpriteOverlap(int spritenum_a, int spritenum_b) {
		SPRITE spa = sprite[spritenum_a], spb = sprite[spritenum_b];

		USER ua = pUser[spritenum_a];
		USER ub = pUser[spritenum_b];

		if (ua == null || ub == null)
			return false;

		long spa_tos, spa_bos, spb_tos, spb_bos, overlap_z;

		if (Distance(spa.x, spa.y, spb.x, spb.y) > ua.Radius + ub.Radius) {
			return (false);
		}

		spa_tos = SPRITEp_TOS(spa);
		spa_bos = SPRITEp_BOS(spa);

		spb_tos = SPRITEp_TOS(spb);
		spb_bos = SPRITEp_BOS(spb);

		overlap_z = ua.OverlapZ + ub.OverlapZ;

		// if the top of sprite a is below the bottom of b
		if (spa_tos - overlap_z > spb_bos) {
			return (false);
		}

		// if the top of sprite b is is below the bottom of a
		if (spb_tos - overlap_z > spa_bos) {
			return (false);
		}

		return (true);
	}

	public static boolean SpriteOverlapZ(int spritenum_a, int spritenum_b, int z_overlap) {
		SPRITE spa = sprite[spritenum_a], spb = sprite[spritenum_b];

		int spa_tos, spa_bos, spb_tos, spb_bos;

		spa_tos = SPRITEp_TOS(spa);
		spa_bos = SPRITEp_BOS(spa);

		spb_tos = SPRITEp_TOS(spb);
		spb_bos = SPRITEp_BOS(spb);

		// if the top of sprite a is below the bottom of b
		if (spa_tos + z_overlap > spb_bos) {
			return (false);
		}

		// if the top of sprite b is is below the bottom of a
		if (spb_tos + z_overlap > spa_bos) {
			return (false);
		}

		return (true);
	}

	public static void DoActorZrange(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		int ceilhit, florhit;
		int save_cstat;

		save_cstat = DTEST(sp.cstat, CSTAT_SPRITE_BLOCK);
		sp.cstat &= ~(CSTAT_SPRITE_BLOCK);
		FAFgetzrange(sp.x, sp.y, sp.z - DIV2(SPRITEp_SIZE_Z(sp)), sp.sectnum, tmp_ptr[0].set(0), tmp_ptr[1].set(0),
				tmp_ptr[2].set(0), tmp_ptr[3].set(0), ((sp.clipdist) << 2) - GETZRANGE_CLIP_ADJ, CLIPMASK_ACTOR);
		u.hiz = tmp_ptr[0].value;
		ceilhit = tmp_ptr[1].value;
		u.loz = tmp_ptr[2].value;
		florhit = tmp_ptr[3].value;

		sp.cstat |= (save_cstat);

		u.lo_sectp = u.hi_sectp = -1;
		u.lo_sp = u.hi_sp = -1;

		switch (DTEST(ceilhit, HIT_MASK)) {
		case HIT_SPRITE:
			u.hi_sp = NORM_SPRITE(ceilhit);
			break;
		case HIT_SECTOR:
			u.hi_sectp = NORM_SECTOR(ceilhit);
			break;
		default:
			break;
		}

		switch (DTEST(florhit, HIT_MASK)) {
		case HIT_SPRITE:
			u.lo_sp = NORM_SPRITE(florhit);
			break;
		case HIT_SECTOR:
			u.lo_sectp = NORM_SECTOR(florhit);
			break;
		default:
			break;
		}
	}

	// !AIC - puts getzrange results into USER varaible u.loz, u.hiz, u.lo_sectp,
	// u.hi_sectp, etc.
	// The loz and hiz are used a lot.

	public static int DoActorGlobZ(int SpriteNum) {
		USER u = pUser[SpriteNum];

		u.loz = globloz;
		u.hiz = globhiz;

		u.lo_sectp = u.hi_sectp = -1;
		u.lo_sp = u.hi_sp = -1;

		switch (DTEST(globhihit, HIT_MASK)) {
		case HIT_SPRITE:
			u.hi_sp = globhihit & 4095;
			break;
		default:
			u.hi_sectp = (short) (globhihit & 4095);
			break;
		}

		switch (DTEST(globlohit, HIT_MASK)) {
		case HIT_SPRITE:
			u.lo_sp = globlohit & 4095;
			break;
		default:
			u.lo_sectp = (short) (globlohit & 4095);
			break;
		}

		return (0);
	}

	public static boolean ActorDrop(int SpriteNum, int x, int y, int z, short new_sector, int min_height) {
		SPRITE sp = sprite[SpriteNum];
		int ceilhit, florhit, loz;
		short save_cstat;

		// look only at the center point for a floor sprite
		save_cstat = (short) DTEST(sp.cstat, CSTAT_SPRITE_BLOCK);
		sp.cstat &= ~(CSTAT_SPRITE_BLOCK);
		FAFgetzrangepoint(x, y, z - DIV2(SPRITEp_SIZE_Z(sp)), new_sector, tmp_ptr[0].set(0), tmp_ptr[1].set(0),
				tmp_ptr[2].set(0), tmp_ptr[3].set(0)); // &hiz, &ceilhit, &loz, &florhit);

		ceilhit = tmp_ptr[1].value;
		loz = tmp_ptr[2].value;
		florhit = tmp_ptr[3].value;

		sp.cstat |= (save_cstat);

		if (florhit < 0 || ceilhit < 0)
			return (true);

		switch (DTEST(florhit, HIT_MASK)) {
		case HIT_SPRITE: {
			SPRITE hsp = sprite[florhit & 4095];
			// if its a floor sprite and not too far down
			if (TEST(hsp.cstat, CSTAT_SPRITE_FLOOR) && (klabs(loz - z) <= min_height))
				return (false);

			break;
		}

		case HIT_SECTOR: {
			if (klabs(loz - z) <= min_height)
				return (false);
			break;
		}
		default:
			break;
		}

		return (true);
	}

	// Primarily used in ai.c for now - need to get rid of
	public static boolean DropAhead(int SpriteNum, int min_height) {
		SPRITE sp = sprite[SpriteNum];
		int dax, day;
		short newsector;

		dax = sp.x + MOVEx(256, sp.ang);
		day = sp.y + MOVEy(256, sp.ang);

		newsector = sp.sectnum;
		newsector = COVERupdatesector(dax, day, newsector);

		// look straight down for a drop
		if (ActorDrop(SpriteNum, dax, day, sp.z, newsector, min_height))
			return (true);

		return (false);
	}

	/*
	 *
	 * !AIC KEY - Called by ai.c routines. Calls move_sprite which calls clipmove.
	 * This incapulates move_sprite and makes sure that actors don't walk off of
	 * ledges. If it finds itself in mid air then it restores the last good
	 * position. This is a hack because Ken had no good way of doing this from his
	 * code. ActorDrop() is called from here.
	 *
	 */

	public static boolean move_actor(int SpriteNum, int xchange, int ychange, int zchange) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		int x, y, z, loz, hiz;
		int lo_sp, hi_sp;
		short lo_sectp, hi_sectp;
		short sectnum;
		short dist;
		int cliptype = CLIPMASK_ACTOR;

		if (TEST(u.Flags, SPR_NO_SCAREDZ)) {
			// For COOLG & HORNETS
			// set to actual z before you move
			sp.z = u.sz;
		}

		// save off x,y values
		x = sp.x;
		y = sp.y;
		z = sp.z;
		loz = u.loz;
		hiz = u.hiz;
		lo_sp = u.lo_sp;
		hi_sp = u.hi_sp;
		lo_sectp = u.lo_sectp;
		hi_sectp = u.hi_sectp;
		sectnum = sp.sectnum;

		clipmoveboxtracenum = 1;
		u.ret = move_sprite(SpriteNum, xchange, ychange, zchange, u.ceiling_dist, u.floor_dist, cliptype,
				ACTORMOVETICS);
		clipmoveboxtracenum = 3;

		// try and determine whether you moved > lo_step in the z direction
		if (!TEST(u.Flags, SPR_NO_SCAREDZ | SPR_JUMPING | SPR_CLIMBING | SPR_FALLING | SPR_DEAD | SPR_SWIMMING)) {
			if (klabs(sp.z - globloz) > u.lo_step) {
				// cancel move
				sp.x = x;
				sp.y = y;
				sp.z = z;

				u.loz = loz;
				u.hiz = hiz;
				u.lo_sp = lo_sp;
				u.hi_sp = hi_sp;
				u.lo_sectp = lo_sectp;
				u.hi_sectp = hi_sectp;
				u.ret = -1;
				engine.changespritesect((short) SpriteNum, sectnum);
				return (false);
			}

			if (ActorDrop(SpriteNum, sp.x, sp.y, sp.z, sp.sectnum, u.lo_step)) {
				// cancel move
				sp.x = x;
				sp.y = y;
				sp.z = z;

				u.loz = loz;
				u.hiz = hiz;
				u.lo_sp = lo_sp;
				u.hi_sp = hi_sp;
				u.lo_sectp = lo_sectp;
				u.hi_sectp = hi_sectp;
				u.ret = -1;
				engine.changespritesect((short) SpriteNum, sectnum);
				return (false);
			}
		}

		u.Flags |= (SPR_MOVED);

		if (u.ret == 0) {
			// Keep track of how far sprite has moved
			dist = (short) Distance(x, y, sp.x, sp.y);
			u.TargetDist -= dist;
			u.Dist += dist;
			u.DistCheck += dist;
			return (true);
		} else {
			return (false);
		}
	}

	public static int DoStayOnFloor(int SpriteNum) {
		sprite[SpriteNum].z = sector[sprite[SpriteNum].sectnum].floorz;
		return (0);
	}

	public static final int GRATE_FACTOR = 3;

	public static final Animator DoGrating = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoGrating(spr) != 0;
		}
	};

	public static int DoGrating(int SpriteNum) {
		SPRITE sp = pUser[SpriteNum].getSprite();
		game.pInt.setsprinterpolate(pUser[SpriteNum].SpriteNum, sp);

		// reduce to 0 to 3 value
		int dir = sp.ang >> 9;

		if (MOD2(dir) == 0) {
			if (dir == 0)
				sp.x += 2 * GRATE_FACTOR;
			else
				sp.x -= 2 * GRATE_FACTOR;
		} else {
			if (dir == 1)
				sp.y += 2 * GRATE_FACTOR;
			else
				sp.y -= 2 * GRATE_FACTOR;
		}

		sp.hitag -= GRATE_FACTOR;

		if (sp.hitag <= 0) {
			change_sprite_stat(SpriteNum, STAT_DEFAULT);
			pUser[SpriteNum] = null;
		}

		engine.setspritez((short) SpriteNum, sp.x, sp.y, sp.z);

		return (0);
	}

	public static int DoSpriteFade(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();
		short i;

		// adjust Shade based on clock

		for (i = 0; i < ACTORMOVETICS; i++) {
			if (TEST(u.Flags, SPR_SHADE_DIR)) {
				sp.shade++;

				if (sp.shade >= 10)
					u.Flags &= ~(SPR_SHADE_DIR);
			} else {
				sp.shade--;

				if (sp.shade <= -40)
					u.Flags |= (SPR_SHADE_DIR);
			}
		}
		return (0);
	}

	public static int SpearOnFloor(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		if (!TEST(u.Flags, SPR_SO_ATTACHED)) {
			// if on a sprite bridge, stay with the sprite otherwize stay with
			// the floor
			if (u.lo_sp != -1)
				sp.z = u.loz;
			else
				sp.z = sector[sp.sectnum].floorz + u.sz;
		}
		return (0);
	}

	public static int SpearOnCeiling(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		if (!TEST(u.Flags, SPR_SO_ATTACHED)) {
			// if on a sprite bridge, stay with the sprite otherwize stay with
			// the floor
			if (u.hi_sp != -1)
				sp.z = u.hiz;
			else
				sp.z = sector[sp.sectnum].ceilingz + u.sz;
		}
		return (0);
	}

	public static int DoKey(int SpriteNum) {
		SPRITE sp = pUser[SpriteNum].getSprite();

		sp.ang = NORM_ANGLE(sp.ang + (14 * ACTORMOVETICS));

		DoGet(SpriteNum);
		return (0);
	}

	public static int DoCoin(int SpriteNum) {
		USER u = pUser[SpriteNum];
		int offset;

		u.WaitTics -= ACTORMOVETICS * 2;

		if (u.WaitTics <= 0) {
			KillSprite(SpriteNum);
			return (0);
		}

		if (u.WaitTics < 10 * 120) {
			if (u.StateStart != s_GreenCoin[0]) {
				offset = u.State.id - u.StateStart.id;
				ChangeState(SpriteNum, s_GreenCoin[0]);
				u.State = s_GreenCoin[u.StateStart.id + offset];
			}
		} else if (u.WaitTics < 20 * 120) {
			if (u.StateStart != s_YellowCoin[0]) {
				offset = u.State.id - u.StateStart.id;
				ChangeState(SpriteNum, s_YellowCoin[0]);
				u.State = s_YellowCoin[u.StateStart.id + offset];
			}
		}

		return (0);
	}

	public static int KillGet(int SpriteNum) {
		USER u = pUser[SpriteNum], nu;
		SPRITE sp = pUser[SpriteNum].getSprite(), np;

		int newsp;

		switch (gNet.MultiGameType) {
		case MULTI_GAME_NONE:
//		case MULTI_GAME_COOPERATIVE:
			KillSprite(SpriteNum);
			break;
		case MULTI_GAME_COMMBAT:
		case MULTI_GAME_AI_BOTS:
		case MULTI_GAME_COOPERATIVE:

			if (TEST(u.Flags2, SPR2_NEVER_RESPAWN)) {
				KillSprite(SpriteNum);
				break;
			}

			u.WaitTics = 30 * 120;
			sp.cstat |= (CSTAT_SPRITE_INVISIBLE);

			// respawn markers
			if (!gNet.SpawnMarkers || sp.hitag == TAG_NORESPAWN_FLAG) // No coin if it's a special flag
				break;

			newsp = SpawnSprite(STAT_ITEM, Red_COIN, s_RedCoin[0], sp.sectnum, sp.x, sp.y, sp.z, 0, 0);

			np = sprite[newsp];
			nu = pUser[newsp];

			np.shade = -20;
			nu.WaitTics = (short) (u.WaitTics - 12);

			break;
		default:
			break;
		}
		return (0);
	}

	public static int KillGetAmmo(int SpriteNum) {
		USER u = pUser[SpriteNum], nu;
		SPRITE sp = pUser[SpriteNum].getSprite(), np;

		int newsp;

		switch (gNet.MultiGameType) {
		default:
			break;
		case MULTI_GAME_NONE:
//		case MULTI_GAME_COOPERATIVE:
			KillSprite(SpriteNum);
			break;

		case MULTI_GAME_COMMBAT:
		case MULTI_GAME_AI_BOTS:
		case MULTI_GAME_COOPERATIVE:

			if (TEST(u.Flags2, SPR2_NEVER_RESPAWN)) {
				KillSprite(SpriteNum);
				break;
			}

			// No Respawn mode - all ammo goes away
			if (gNet.NoRespawn) {
				KillSprite(SpriteNum);
				break;
			}

			u.WaitTics = 30 * 120;
			sp.cstat |= (CSTAT_SPRITE_INVISIBLE);

			// respawn markers
			if (!gNet.SpawnMarkers)
				break;

			newsp = SpawnSprite(STAT_ITEM, Red_COIN, s_RedCoin[0], sp.sectnum, sp.x, sp.y, sp.z, 0, 0);

			np = sprite[newsp];
			nu = pUser[newsp];

			np.shade = -20;
			nu.WaitTics = (short) (u.WaitTics - 12);

			break;
		}
		return (0);
	}

	public static int KillGetWeapon(int SpriteNum) {
		USER u = pUser[SpriteNum], nu;
		SPRITE sp = pUser[SpriteNum].getSprite(), np;

		int newsp;

		switch (gNet.MultiGameType) {
		default:
			break;
		case MULTI_GAME_NONE:
			KillSprite(SpriteNum);
			break;

			case MULTI_GAME_COMMBAT:
		case MULTI_GAME_AI_BOTS:
		case MULTI_GAME_COOPERATIVE:

			if (TEST(u.Flags2, SPR2_NEVER_RESPAWN)) {
				KillSprite(SpriteNum);
				break;
			}

			// No Respawn mode - all weapons stay
			// but can only get once
			if (gNet.NoRespawn)
				break;

			u.WaitTics = 30 * 120;
			sp.cstat |= (CSTAT_SPRITE_INVISIBLE);

			// respawn markers
			if (!gNet.SpawnMarkers)
				break;

			newsp = SpawnSprite(STAT_ITEM, Red_COIN, s_RedCoin[0], sp.sectnum, sp.x, sp.y, sp.z, 0, 0);

			np = sprite[newsp];
			nu = pUser[newsp];

			np.shade = -20;
			nu.WaitTics = (short) (u.WaitTics - 12);

			break;
		}
		return (0);
	}

	public static int DoSpawnItemTeleporterEffect(SPRITE sp) {
		int effect = SpawnSprite(STAT_MISSILE, 0, s_TeleportEffect[0], sp.sectnum, sp.x, sp.y, sp.z - Z(12), sp.ang, 0);

		SPRITE ep = sprite[effect];

		ep.shade = -40;
		ep.xrepeat = ep.yrepeat = 36;
		ep.cstat |= (CSTAT_SPRITE_YCENTER);
		ep.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		return (0);
	}

	public static void ChoosePlayerGetSound(PlayerStr pp) {
		int choose_snd = 0;

		if (pp != Player[myconnectindex])
			return;

		choose_snd = STD_RANDOM_RANGE((MAX_GETSOUNDS - 1) << 8) >> 8;

		PlayerSound(PlayerGetItemVocs[choose_snd], v3df_follow | v3df_dontpan, pp);
	}

	public static final int MAX_FORTUNES = 16;
	// With PLOCK on, max = 11
	public static final String ReadFortune[] = { "You never going to score.", "26-31-43-82-16-29",
			"Sorry, you no win this time, try again.", "You try harder get along. Be a nice man.",
			"No man is island, except Lo Wang.", "There is much death in future.",
			"You should kill all business associates.", "(c)1997,3DRealms fortune cookie company.",
			"Your chi attracts many chicks.", "Don't you know you the scum of society!?",
			"You should not scratch yourself there.", "Man who stand on toilet, high on pot.",
			"Man who fart in church sit in own pew.", "Man trapped in pantry has ass in jam.",
			"Baseball wrong.  Man with 4 balls cannot walk.", "Man who buy drowned cat pay for wet pussy.", };

	public static boolean CanGetWeapon(PlayerStr pp, int SpriteNum, int WPN) {
		USER u = pUser[SpriteNum];
		if (InfinityAmmo)
			return true;

		switch (gNet.MultiGameType) {
		default:
			break;
		case MULTI_GAME_NONE:
			return (true);

			case MULTI_GAME_COMMBAT:
		case MULTI_GAME_AI_BOTS:
		case MULTI_GAME_COOPERATIVE:

			if (TEST(u.Flags2, SPR2_NEVER_RESPAWN))
				return (true);

			// No Respawn - can't get a weapon again if you already got it
			if (gNet.NoRespawn && TEST(pp.WpnGotOnceFlags, BIT(WPN)))
				return (false);

			return (true);
		}

		return (true);
	}

	public static final String KeyMsg[] = { "Got the RED key!", "Got the BLUE key!", "Got the GREEN key!",
			"Got the YELLOW key!", "Got the GOLD master key!", "Got the SILVER master key!",
			"Got the BRONZE master key!", "Got the RED master key!" };

	public static final int ITEMFLASHAMT = -8;
	public static final int ITEMFLASHCLR = 144;

	public static int DoGet(int SpriteNum) {
		USER u = pUser[SpriteNum], pu;
		SPRITE sp = pUser[SpriteNum].getSprite();
		PlayerStr pp;
		short pnum, key_num = 0;
		int dist;

		boolean can_see;
		short cstat_bak;

		// For flag stuff
		USER nu;
		SPRITE np;
		short newsp;

		// Invisiblility is only used for DeathMatch type games
		// Sprites stays invisible for a period of time and is un-gettable
		// then "Re-Spawns" by becomming visible. Its never actually killed.
		if (TEST(sp.cstat, CSTAT_SPRITE_INVISIBLE)) {
			u.WaitTics -= ACTORMOVETICS * 2;
			if (u.WaitTics <= 0) {
				PlaySound(DIGI_ITEM_SPAWN, sp, v3df_none);
				DoSpawnItemTeleporterEffect(sp);
				sp.cstat &= ~(CSTAT_SPRITE_INVISIBLE);
			}

			return (0);
		}

		if (sp.xvel != 0) {
			if (!DoItemFly(SpriteNum)) {
				sp.xvel = 0;
				change_sprite_stat(SpriteNum, STAT_ITEM);
			}
		}

		for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
			pp = Player[pnum];
			pu = pUser[pp.PlayerSprite];

			if (TEST(pp.Flags, PF_DEAD))
				continue;

			dist = DISTANCE(pp.posx, pp.posy, sp.x, sp.y);
			if (dist > (pu.Radius + u.Radius)) {
				continue;
			}

			if (!SpriteOverlap(SpriteNum, pp.PlayerSprite)) {
				continue;
			}

			cstat_bak = sp.cstat;
			sp.cstat |= (CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
			can_see = FAFcansee(sp.x, sp.y, sp.z, sp.sectnum, pp.posx, pp.posy, pp.posz, pp.cursectnum);
			sp.cstat = cstat_bak;

			if (!can_see) {
				continue;
			}

			switch (u.ID) {
			//
			// Keys
			//
			case RED_CARD:
			case RED_KEY:
			case BLUE_CARD:
			case BLUE_KEY:
			case GREEN_CARD:
			case GREEN_KEY:
			case YELLOW_CARD:
			case YELLOW_KEY:
			case GOLD_SKELKEY:
			case SILVER_SKELKEY:
			case BRONZE_SKELKEY:
			case RED_SKELKEY:

				switch (u.ID) {
				case RED_CARD:
				case RED_KEY:
					key_num = 0;
					break;
				case BLUE_CARD:
				case BLUE_KEY:
					key_num = 1;
					break;
				case GREEN_CARD:
				case GREEN_KEY:
					key_num = 2;
					break;
				case YELLOW_CARD:
				case YELLOW_KEY:
					key_num = 3;
					break;
				case GOLD_SKELKEY:
					key_num = 4;
					break;
				case SILVER_SKELKEY:
					key_num = 5;
					break;
				case BRONZE_SKELKEY:
					key_num = 6;
					break;
				case RED_SKELKEY:
					key_num = 7;
				}

				if (pp.HasKey[key_num] != 0)
					break;

				PutStringInfo(Player[pnum], KeyMsg[key_num]);

				pp.HasKey[key_num] = 1;
				SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
				if (pp == Player[myconnectindex])
					PlaySound(DIGI_KEY, sp, v3df_dontpan);

				// don't kill keys in coop
				if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COOPERATIVE)
					break;

				KillSprite(SpriteNum);
				break;

			case ICON_ARMOR:
				if (pp.Armor < 100) {
					if (u.spal == PALETTE_PLAYER3) {
						PlayerUpdateArmor(pp, 1100);
						PutStringInfo(Player[pnum], "Kevlar Armor Vest +100");
					} else {
						if (pp.Armor < 50) {
							PlayerUpdateArmor(pp, 1050);
							PutStringInfo(Player[pnum], "Armor Vest +50");
						} else
							break;
					}
					SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
					if (pp == Player[myconnectindex])
						PlaySound(DIGI_BIGITEM, sp, v3df_dontpan);

					// override for respawn mode
					if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COMMBAT && gNet.NoRespawn) {
						KillSprite(SpriteNum);
						break;
					}

					KillGet(SpriteNum);
				}
				break;

			//
			// Health - Instant Use
			//

			case ICON_SM_MEDKIT:
				if (pu.Health < 100) {
					boolean putbackmax = false;
					PutStringInfo(Player[pnum], "MedKit +20");

					if (pp.MaxHealth == 200) {
						pp.MaxHealth = 100;
						putbackmax = true;
					}
					PlayerUpdateHealth(pp, 20);

					if (putbackmax)
						pp.MaxHealth = 200;

					SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
					if (pp == Player[myconnectindex])
						PlaySound(DIGI_ITEM, sp, v3df_dontpan);

					// override for respawn mode
					if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COMMBAT && gNet.NoRespawn) {
						KillSprite(SpriteNum);
						break;
					}

					KillGet(SpriteNum);
				}
				break;

			case ICON_BOOSTER: // Fortune cookie
				pp.MaxHealth = 200;
				if (pu.Health < 200) {
					PutStringInfo(Player[pnum], "Fortune Cookie +50 BOOST");
					PlayerUpdateHealth(pp, 50); // This is for health
												// over 100%
					// Say something witty
					if (pp == Player[myconnectindex] && gs.Messages) {
						if (gs.ParentalLock || Global_PLock)
							adduserquote("Fortune Say: " + ReadFortune[STD_RANDOM_RANGE(10)]);
						else
							adduserquote("Fortune Say: " + ReadFortune[STD_RANDOM_RANGE(MAX_FORTUNES)]);
					}

					SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
					if (pp == Player[myconnectindex])
						PlaySound(DIGI_BIGITEM, sp, v3df_dontpan);

					// override for respawn mode
					if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COMMBAT && gNet.NoRespawn) {
						KillSprite(SpriteNum);
						break;
					}

					KillGet(SpriteNum);
				}
				break;

			//
			// Inventory
			//
			case ICON_MEDKIT:

				if (pp.InventoryAmount[INVENTORY_MEDKIT] == 0 || pp.InventoryPercent[INVENTORY_MEDKIT] < 100) {
					PutStringInfo(Player[pnum], "Portable MedKit");
					pp.InventoryPercent[INVENTORY_MEDKIT] = 100;
					pp.InventoryAmount[INVENTORY_MEDKIT] = 1;
					PlayerUpdateInventory(pp, INVENTORY_MEDKIT);
					SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
					if (pp == Player[myconnectindex])
						PlaySound(DIGI_ITEM, sp, v3df_dontpan);

					// override for respawn mode
					if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COMMBAT && gNet.NoRespawn) {
						KillSprite(SpriteNum);
						break;
					}

					KillGet(SpriteNum);
				}
				break;

			case ICON_CHEMBOMB:

				if (pp.InventoryAmount[INVENTORY_CHEMBOMB] < 1) {
					PutStringInfo(Player[pnum], "Gas Bomb");
					pp.InventoryPercent[INVENTORY_CHEMBOMB] = 0;
					pp.InventoryAmount[INVENTORY_CHEMBOMB]++;
					PlayerUpdateInventory(pp, INVENTORY_CHEMBOMB);
					SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
					if (pp == Player[myconnectindex])
						PlaySound(DIGI_ITEM, sp, v3df_dontpan);
					KillGet(SpriteNum);
				}
				break;

			case ICON_FLASHBOMB:

				if (pp.InventoryAmount[INVENTORY_FLASHBOMB] < 2) {
					PutStringInfo(Player[pnum], "Flash Bomb");
					pp.InventoryPercent[INVENTORY_FLASHBOMB] = 0;
					pp.InventoryAmount[INVENTORY_FLASHBOMB]++;
					PlayerUpdateInventory(pp, INVENTORY_FLASHBOMB);
					SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
					if (pp == Player[myconnectindex])
						PlaySound(DIGI_ITEM, sp, v3df_dontpan);
					KillGet(SpriteNum);
				}
				break;

			case ICON_CALTROPS:

				if (pp.InventoryAmount[INVENTORY_CALTROPS] < 3) {
					PutStringInfo(Player[pnum], "Caltrops");
					pp.InventoryPercent[INVENTORY_CALTROPS] = 0;
					pp.InventoryAmount[INVENTORY_CALTROPS] += 3;
					if (pp.InventoryAmount[INVENTORY_CALTROPS] > 3)
						pp.InventoryAmount[INVENTORY_CALTROPS] = 3;
					PlayerUpdateInventory(pp, INVENTORY_CALTROPS);
					SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
					if (pp == Player[myconnectindex])
						PlaySound(DIGI_ITEM, sp, v3df_dontpan);
					KillGet(SpriteNum);
				}
				break;

			case ICON_NIGHT_VISION:
				if (pp.InventoryAmount[INVENTORY_NIGHT_VISION] == 0
						|| pp.InventoryPercent[INVENTORY_NIGHT_VISION] < 100) {
					PutStringInfo(Player[pnum], "Night Vision Goggles");
					pp.InventoryPercent[INVENTORY_NIGHT_VISION] = 100;
					pp.InventoryAmount[INVENTORY_NIGHT_VISION] = 1;
					PlayerUpdateInventory(pp, INVENTORY_NIGHT_VISION);
					SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
					if (pp == Player[myconnectindex])
						PlaySound(DIGI_ITEM, sp, v3df_dontpan);
					KillGet(SpriteNum);
				}
				break;
			case ICON_REPAIR_KIT:
				if (pp.InventoryAmount[INVENTORY_REPAIR_KIT] == 0 || pp.InventoryPercent[INVENTORY_REPAIR_KIT] < 100) {
					PutStringInfo(Player[pnum], "Repair Kit");
					pp.InventoryPercent[INVENTORY_REPAIR_KIT] = 100;
					pp.InventoryAmount[INVENTORY_REPAIR_KIT] = 1;
					PlayerUpdateInventory(pp, INVENTORY_REPAIR_KIT);
					SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
					if (pp == Player[myconnectindex])
						PlaySound(DIGI_ITEM, sp, v3df_dontpan);

					// don't kill repair kit in coop
					if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COOPERATIVE)
						break;

					KillGet(SpriteNum);
				}
				break;

			case ICON_CLOAK:
				if (pp.InventoryAmount[INVENTORY_CLOAK] == 0 || pp.InventoryPercent[INVENTORY_CLOAK] < 100) {
					PutStringInfo(Player[pnum], "Smoke Bomb");
					pp.InventoryPercent[INVENTORY_CLOAK] = 100;
					pp.InventoryAmount[INVENTORY_CLOAK] = 1;
					PlayerUpdateInventory(pp, INVENTORY_CLOAK);
					SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
					if (pp == Player[myconnectindex])
						PlaySound(DIGI_ITEM, sp, v3df_dontpan);
					KillGet(SpriteNum);
				}
				break;
			//
			// Weapon
			//
			case ICON_STAR:

				if (!CanGetWeapon(pp, SpriteNum, WPN_STAR))
					break;

				pp.WpnGotOnceFlags |= (BIT(WPN_STAR));

				if (!InfinityAmmo && pp.WpnAmmo[WPN_STAR] >= DamageData[WPN_STAR].max_ammo)
					break;

				PutStringInfo(Player[pnum], gs.UseDarts ? "Darts" : "Shurikens");
				PlayerUpdateAmmo(pp, WPN_STAR, 9);
				SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
				if (pp == Player[myconnectindex])
					PlaySound(DIGI_ITEM, sp, v3df_dontpan);
				KillGetWeapon(SpriteNum);
				if (TEST(pp.WpnFlags, BIT(WPN_STAR)))
					break;
				pp.WpnFlags |= (BIT(WPN_STAR));
				if (!gs.WeaponAutoSwitch)
					break;

				if (pUser[pp.PlayerSprite].WeaponNum <= WPN_STAR && pUser[pp.PlayerSprite].WeaponNum != WPN_SWORD)
					break;
				InitWeaponStar(pp);
				break;

			case ICON_LG_MINE:

				if (!CanGetWeapon(pp, SpriteNum, WPN_MINE))
					break;

				pp.WpnGotOnceFlags |= (BIT(WPN_MINE));

				if (!InfinityAmmo && pp.WpnAmmo[WPN_MINE] >= DamageData[WPN_MINE].max_ammo)
					break;

				PutStringInfo(Player[pnum], "Sticky Bombs");
				PlayerUpdateAmmo(pp, WPN_MINE, 5);
				SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
				if (pp == Player[myconnectindex])
					PlaySound(DIGI_ITEM, sp, v3df_dontpan);
				ChoosePlayerGetSound(pp);
				KillGetWeapon(SpriteNum);
				if (TEST(pp.WpnFlags, BIT(WPN_MINE)))
					break;
				pp.WpnFlags |= (BIT(WPN_MINE));
				if (!gs.WeaponAutoSwitch)
					break;

				if (pUser[pp.PlayerSprite].WeaponNum > WPN_MINE && pUser[pp.PlayerSprite].WeaponNum != WPN_SWORD)
					break;
				InitWeaponMine(pp);
				break;

			case ICON_UZI:
			case ICON_UZIFLOOR:

				if (!CanGetWeapon(pp, SpriteNum, WPN_UZI))
					break;

				pp.WpnGotOnceFlags |= (BIT(WPN_UZI));

				if (!InfinityAmmo && TEST(pp.Flags, PF_TWO_UZI) && pp.WpnAmmo[WPN_UZI] >= DamageData[WPN_UZI].max_ammo)
					break;
				PutStringInfo(Player[pnum], "UZI Submachine Gun");
				PlayerUpdateAmmo(pp, WPN_UZI, 50);
				SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
				if (pp == Player[myconnectindex])
					PlaySound(DIGI_ITEM, sp, v3df_dontpan);
				KillGetWeapon(SpriteNum);

				if (TEST(pp.WpnFlags, BIT(WPN_UZI)) && TEST(pp.Flags, PF_TWO_UZI))
					break;
				// flag to help with double uzi powerup - simpler but kludgy
				pp.Flags |= (PF_PICKED_UP_AN_UZI);
				if (TEST(pp.WpnFlags, BIT(WPN_UZI))) {
					pp.Flags |= (PF_TWO_UZI);
					pp.WpnUziType = 0; // Let it come up
					if (pp == Player[myconnectindex])
						PlayerSound(DIGI_DOUBLEUZI, v3df_dontpan | v3df_follow, pp);
				} else {
					pp.WpnFlags |= (BIT(WPN_UZI));
					ChoosePlayerGetSound(pp);
				}

				if (!gs.WeaponAutoSwitch)
					break;

				if (pUser[pp.PlayerSprite].WeaponNum > WPN_UZI && pUser[pp.PlayerSprite].WeaponNum != WPN_SWORD)
					break;

				InitWeaponUzi(pp);
				break;

			case ICON_LG_UZI_AMMO:
				if (!InfinityAmmo && pp.WpnAmmo[WPN_UZI] >= DamageData[WPN_UZI].max_ammo)
					break;
				PutStringInfo(Player[pnum], "UZI Clip");
				PlayerUpdateAmmo(pp, WPN_UZI, 50);
				SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
				if (pp == Player[myconnectindex])
					PlaySound(DIGI_ITEM, sp, v3df_dontpan);
				KillGetAmmo(SpriteNum);
				break;

			case ICON_MICRO_GUN:

				if (!CanGetWeapon(pp, SpriteNum, WPN_MICRO))
					break;

				pp.WpnGotOnceFlags |= (BIT(WPN_MICRO));

				if (!InfinityAmmo && TEST(pp.WpnFlags, BIT(WPN_MICRO))
						&& pp.WpnAmmo[WPN_MICRO] >= DamageData[WPN_MICRO].max_ammo)
					break;
				PutStringInfo(Player[pnum], "Missile Launcher");

				PlayerUpdateAmmo(pp, WPN_MICRO, 5);
				SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
				if (pp == Player[myconnectindex])
					PlaySound(DIGI_ITEM, sp, v3df_dontpan);
				ChoosePlayerGetSound(pp);
				KillGetWeapon(SpriteNum);
				if (TEST(pp.WpnFlags, BIT(WPN_MICRO)))
					break;
				pp.WpnFlags |= (BIT(WPN_MICRO));
				if (!gs.WeaponAutoSwitch)
					break;

				if (pUser[pp.PlayerSprite].WeaponNum > WPN_MICRO && pUser[pp.PlayerSprite].WeaponNum != WPN_SWORD)
					break;
				InitWeaponMicro(pp);
				break;

			case ICON_MICRO_BATTERY:
				if (!InfinityAmmo && pp.WpnAmmo[WPN_MICRO] >= DamageData[WPN_MICRO].max_ammo)
					break;
				PutStringInfo(Player[pnum], "Missiles");
				PlayerUpdateAmmo(pp, WPN_MICRO, 5);
				SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
				if (pp == Player[myconnectindex])
					PlaySound(DIGI_ITEM, sp, v3df_dontpan);
				KillGetAmmo(SpriteNum);
				break;

			case ICON_NUKE:
				if (pp.WpnRocketNuke != 1) {
					PutStringInfo(Player[pnum], "Nuclear Warhead");
					pp.WpnRocketNuke = 1;
					SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
					if (pp == Player[myconnectindex])
						PlaySound(DIGI_ITEM, sp, v3df_dontpan);
					if (STD_RANDOM_RANGE(1000) > 800 && pp == Player[myconnectindex])
						PlayerSound(DIGI_ILIKENUKES, v3df_dontpan | v3df_doppler | v3df_follow, pp);
					if (pp.CurWpn == pp.Wpn[WPN_MICRO]) {
						if (pp.WpnRocketType != 2) {
							pp.CurWpn.over[MICRO_SHOT_NUM].tics = 0;
							pp.CurWpn.over[MICRO_SHOT_NUM].State = ps_MicroNukeFlash[0];
							// Play Nuke available sound here!
						}

					}

					KillGetAmmo(SpriteNum);
				}
				break;

			case ICON_GRENADE_LAUNCHER:
				if (!CanGetWeapon(pp, SpriteNum, WPN_GRENADE))
					break;

				pp.WpnGotOnceFlags |= (BIT(WPN_GRENADE));

				if (!InfinityAmmo && TEST(pp.WpnFlags, BIT(WPN_GRENADE))
						&& pp.WpnAmmo[WPN_GRENADE] >= DamageData[WPN_GRENADE].max_ammo)
					break;
				PutStringInfo(Player[pnum], "Grenade Launcher");

				PlayerUpdateAmmo(pp, WPN_GRENADE, 6);
				SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
				if (pp == Player[myconnectindex])
					PlaySound(DIGI_ITEM, sp, v3df_dontpan);
				// ChoosePlayerGetSound(pp);
				if (STD_RANDOM_RANGE(1000) > 800 && pp == Player[myconnectindex])
					PlayerSound(DIGI_LIKEBIGWEAPONS, v3df_dontpan | v3df_doppler | v3df_follow, pp);
				KillGetWeapon(SpriteNum);
				if (TEST(pp.WpnFlags, BIT(WPN_GRENADE)))
					break;
				pp.WpnFlags |= (BIT(WPN_GRENADE));
				if (!gs.WeaponAutoSwitch)
					break;

				if (pUser[pp.PlayerSprite].WeaponNum > WPN_GRENADE && pUser[pp.PlayerSprite].WeaponNum != WPN_SWORD)
					break;
				InitWeaponGrenade(pp);
				break;

			case ICON_LG_GRENADE:
				if (!InfinityAmmo && pp.WpnAmmo[WPN_GRENADE] >= DamageData[WPN_GRENADE].max_ammo)
					break;
				PutStringInfo(Player[pnum], "Grenade Shells");
				PlayerUpdateAmmo(pp, WPN_GRENADE, 8);
				SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
				if (pp == Player[myconnectindex])
					PlaySound(DIGI_ITEM, sp, v3df_dontpan);
				KillGetAmmo(SpriteNum);
				break;

			case ICON_RAIL_GUN:

				if (!CanGetWeapon(pp, SpriteNum, WPN_RAIL))
					break;

				pp.WpnGotOnceFlags |= (BIT(WPN_RAIL));

				if (!InfinityAmmo && TEST(pp.WpnFlags, BIT(WPN_RAIL))
						&& pp.WpnAmmo[WPN_RAIL] >= DamageData[WPN_RAIL].max_ammo)
					break;
				PutStringInfo(Player[pnum], "Rail Gun");
				PlayerUpdateAmmo(pp, WPN_RAIL, 10);
				SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
				if (pp == Player[myconnectindex])
					PlaySound(DIGI_ITEM, sp, v3df_dontpan);
				if (pp == Player[myconnectindex]) {
					if (STD_RANDOM_RANGE(1000) > 700)
						PlayerSound(DIGI_LIKEBIGWEAPONS, v3df_dontpan | v3df_doppler | v3df_follow, pp);
					else
						PlayerSound(DIGI_GOTRAILGUN, v3df_dontpan | v3df_doppler | v3df_follow, pp);
				}

				KillGetWeapon(SpriteNum);
				if (TEST(pp.WpnFlags, BIT(WPN_RAIL)))
					break;
				pp.WpnFlags |= (BIT(WPN_RAIL));
				if (!gs.WeaponAutoSwitch)
					break;

				if (pUser[pp.PlayerSprite].WeaponNum > WPN_RAIL && pUser[pp.PlayerSprite].WeaponNum != WPN_SWORD)
					break;
				InitWeaponRail(pp);
				break;

			case ICON_RAIL_AMMO:
				if (!InfinityAmmo && pp.WpnAmmo[WPN_RAIL] >= DamageData[WPN_RAIL].max_ammo)
					break;
				PutStringInfo(Player[pnum], "Rail Gun Rods");
				PlayerUpdateAmmo(pp, WPN_RAIL, 10);
				SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
				if (pp == Player[myconnectindex])
					PlaySound(DIGI_ITEM, sp, v3df_dontpan);
				KillGetAmmo(SpriteNum);
				break;

			case ICON_SHOTGUN:
				if (!CanGetWeapon(pp, SpriteNum, WPN_SHOTGUN))
					break;

				pp.WpnGotOnceFlags |= (BIT(WPN_SHOTGUN));

				if (!InfinityAmmo && TEST(pp.WpnFlags, BIT(WPN_SHOTGUN))
						&& pp.WpnAmmo[WPN_SHOTGUN] >= DamageData[WPN_SHOTGUN].max_ammo)
					break;
				PutStringInfo(Player[pnum], "Riot Gun");
				PlayerUpdateAmmo(pp, WPN_SHOTGUN, 8);
				SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
				if (pp == Player[myconnectindex])
					PlaySound(DIGI_ITEM, sp, v3df_dontpan);
				ChoosePlayerGetSound(pp);
				KillGetWeapon(SpriteNum);
				if (TEST(pp.WpnFlags, BIT(WPN_SHOTGUN)))
					break;
				pp.WpnFlags |= (BIT(WPN_SHOTGUN));
				if (!gs.WeaponAutoSwitch)
					break;

				if (pUser[pp.PlayerSprite].WeaponNum > WPN_SHOTGUN && pUser[pp.PlayerSprite].WeaponNum != WPN_SWORD)
					break;
				InitWeaponShotgun(pp);
				break;

			case ICON_LG_SHOTSHELL:
				if (!InfinityAmmo && pp.WpnAmmo[WPN_SHOTGUN] >= DamageData[WPN_SHOTGUN].max_ammo)
					break;
				PutStringInfo(Player[pnum], "Shotshells");
				PlayerUpdateAmmo(pp, WPN_SHOTGUN, 24);
				SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash on item pickup
				if (pp == Player[myconnectindex])
					PlaySound(DIGI_ITEM, sp, v3df_dontpan);
				KillGetAmmo(SpriteNum);
				break;

			case ICON_GUARD_HEAD:

				if (!CanGetWeapon(pp, SpriteNum, WPN_HOTHEAD))
					break;

				pp.WpnGotOnceFlags |= (BIT(WPN_HOTHEAD));

				if (!InfinityAmmo && TEST(pp.WpnFlags, BIT(WPN_HOTHEAD))
						&& pp.WpnAmmo[WPN_HOTHEAD] >= DamageData[WPN_HOTHEAD].max_ammo)
					break;
				PutStringInfo(Player[pnum], "Guardian Head");
				PlayerUpdateAmmo(pp, WPN_HOTHEAD, 30);
				SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
				if (pp == Player[myconnectindex])
					PlaySound(DIGI_ITEM, sp, v3df_dontpan);

				if (STD_RANDOM_RANGE(1000) > 800 && pp == Player[myconnectindex])
					PlayerSound(DIGI_LIKEBIGWEAPONS, v3df_dontpan | v3df_doppler | v3df_follow, pp);
				KillGetWeapon(SpriteNum);
				if (TEST(pp.WpnFlags, BIT(WPN_HOTHEAD)))
					break;
				pp.WpnFlags |= (BIT(WPN_NAPALM) | BIT(WPN_RING) | BIT(WPN_HOTHEAD));
				if (!gs.WeaponAutoSwitch)
					break;

				if (pUser[pp.PlayerSprite].WeaponNum > WPN_HOTHEAD && pUser[pp.PlayerSprite].WeaponNum != WPN_SWORD)
					break;
				InitWeaponHothead(pp);
				break;

			case ICON_FIREBALL_LG_AMMO:
				if (!InfinityAmmo && pp.WpnAmmo[WPN_HOTHEAD] >= DamageData[WPN_HOTHEAD].max_ammo)
					break;
				PutStringInfo(Player[pnum], "Firebursts");
				PlayerUpdateAmmo(pp, WPN_HOTHEAD, 60);
				SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
				if (pp == Player[myconnectindex])
					PlaySound(DIGI_ITEM, sp, v3df_dontpan);
				KillGetAmmo(SpriteNum);
				break;

			case ICON_HEART:

				if (!CanGetWeapon(pp, SpriteNum, WPN_HEART))
					break;

				pp.WpnGotOnceFlags |= (BIT(WPN_HEART));

				if (!InfinityAmmo && TEST(pp.WpnFlags, BIT(WPN_HEART))
						&& pp.WpnAmmo[WPN_HEART] >= DamageData[WPN_HEART].max_ammo)
					break;
				PutStringInfo(Player[pnum], "Ripper Heart");
				PlayerUpdateAmmo(pp, WPN_HEART, 1);
				SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
				if (pp == Player[myconnectindex])
					PlaySound(DIGI_ITEM, sp, v3df_dontpan);
				if (STD_RANDOM_RANGE(1000) > 800 && pp == Player[myconnectindex])
					PlayerSound(DIGI_LIKEBIGWEAPONS, v3df_dontpan | v3df_doppler | v3df_follow, pp);
				KillGetWeapon(SpriteNum);
				if (TEST(pp.WpnFlags, BIT(WPN_HEART)))
					break;
				pp.WpnFlags |= (BIT(WPN_HEART));
				if (!gs.WeaponAutoSwitch)
					break;

				if (pUser[pp.PlayerSprite].WeaponNum > WPN_HEART && pUser[pp.PlayerSprite].WeaponNum != WPN_SWORD)
					break;

				InitWeaponHeart(pp);
				break;

			case ICON_HEART_LG_AMMO:
				if (!InfinityAmmo && pp.WpnAmmo[WPN_HEART] >= DamageData[WPN_HEART].max_ammo)
					break;
				PutStringInfo(Player[pnum], "Deathcoils");
				PlayerUpdateAmmo(pp, WPN_HEART, 6);
				SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
				if (pp == Player[myconnectindex])
					PlaySound(DIGI_ITEM, sp, v3df_dontpan);
				KillGetAmmo(SpriteNum);
				break;

			case ICON_HEAT_CARD:
				if (pp.WpnRocketHeat != 5) {
					PutStringInfo(Player[pnum], "Heat Seeker Card");
					pp.WpnRocketHeat = 5;
					SetFadeAmt(pp, ITEMFLASHAMT, ITEMFLASHCLR); // Flash blue on item pickup
					if (pp == Player[myconnectindex])
						PlaySound(DIGI_ITEM, sp, v3df_dontpan);
					KillGet(SpriteNum);

					if (pp.CurWpn == pp.Wpn[WPN_MICRO]) {
						if (pp.WpnRocketType == 0) {
							pp.WpnRocketType = 1;
						} else if (pp.WpnRocketType == 2) {
							pp.CurWpn.over[MICRO_HEAT_NUM].tics = 0;
							pp.CurWpn.over[MICRO_HEAT_NUM].State = ps_MicroHeatFlash[0];
						}

					}
				}
				break;

			case ICON_FLAG:
				if (sp.pal == sprite[pp.PlayerSprite].pal)
					break; // Can't pick up your own flag!

				PlaySound(DIGI_ITEM, sp, v3df_dontpan);

				if (sp.hitag == TAG_NORESPAWN_FLAG)
					newsp = (short) SpawnSprite(STAT_ITEM, ICON_FLAG, s_CarryFlagNoDet[0], sp.sectnum, sp.x, sp.y, sp.z,
							0, 0);
				else
					newsp = (short) SpawnSprite(STAT_ITEM, ICON_FLAG, s_CarryFlag[0], sp.sectnum, sp.x, sp.y, sp.z, 0,
							0);

				np = sprite[newsp];
				nu = pUser[newsp];
				np.shade = -20;

				// Attach flag to player
				nu.Counter = 0;
				np.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
				np.cstat |= (CSTAT_SPRITE_WALL);
				SetAttach(pp.PlayerSprite, newsp);
				nu.sz = SPRITEp_MID(sprite[pp.PlayerSprite]); // Set mid way up who it hit
				nu.spal = (byte) (np.pal = sp.pal); // Set the palette of the flag

				SetOwner(pp.PlayerSprite, newsp); // Player now owns the flag
				nu.FlagOwner = (short) SpriteNum; // Tell carried flag who owns it
				KillGet(SpriteNum); // Set up for flag respawning
				break;

			default:
				KillSprite(SpriteNum);
			}
		}

		return (0);
	}

	/*
	 *
	 * !AIC KEY - Set Active and Inactive code is here. It was tough to make this
	 * fast. Just know that the main flag is SPR_ACTIVE. Should not need to be
	 * changed except for possibly the u.active_range settings in the future.
	 *
	 */

	public static void SetEnemyActive(int SpriteNum) {
		USER u = pUser[SpriteNum];

		u.Flags |= (SPR_ACTIVE);
		u.inactive_time = 0;
	}

	public static void SetEnemyInactive(int SpriteNum) {
		USER u = pUser[SpriteNum];

		u.Flags &= ~(SPR_ACTIVE);
	}

	// This function mostly only adjust the active_range field
	public static final int TIME_TILL_INACTIVE = (4 * 120);

	public static void ProcessActiveVars(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (!TEST(u.Flags, SPR_ACTIVE)) {
			// if actor has been unaware for more than a few seconds
			u.inactive_time += ACTORMOVETICS;
			if (u.inactive_time > TIME_TILL_INACTIVE) {
				// reset to min update range
				u.active_range = MIN_ACTIVE_RANGE;
				// keep time low so it doesn't roll over
				u.inactive_time = TIME_TILL_INACTIVE;
			}
		}

		u.wait_active_check += ACTORMOVETICS;
	}

	public static void AdjustActiveRange(PlayerStr pp, int SpriteNum, int dist) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		SPRITE psp = pp.getSprite();
		int look_height;

		// do no FAFcansee before it is time
		if (u.wait_active_check < ACTIVE_CHECK_TIME)
			return;

		u.wait_active_check = 0;

		// check aboslute max
		if (dist > MAX_ACTIVE_RANGE)
			return;

		// do not do a FAFcansee if your already active
		// Actor only becomes INACTIVE in DoActorDecision
		if (TEST(u.Flags, SPR_ACTIVE))
			return;

		//
		// From this point on Actor is INACTIVE
		//

		// if actor can still see the player
		look_height = SPRITEp_TOS(sp);
		if (FAFcansee(sp.x, sp.y, look_height, sp.sectnum, psp.x, psp.y, SPRITEp_UPPER(psp), psp.sectnum)) {
			// Player is visible
			// adjust update range of this sprite

			// some huge distance
			u.active_range = 75000;
			// sprite is AWARE
			SetEnemyActive(SpriteNum);
		}
	}

	/*
	 *
	 * !AIC KEY - Main processing loop for sprites. Sprites are separated and
	 * traversed by STAT lists. Note the STAT_MISC, STAT_ENEMY, STAT_VATOR below.
	 * Most everything here calls StateControl().
	 *
	 */

	public static final int INLINE_STATE = 1;

	/*
	 *
	 * !AIC KEY - Reads state tables for animation frame transitions and handles
	 * calling animators, QUICK_CALLS, etc. This is handled for many types of
	 * sprites not just actors.
	 *
	 */

	private static int StateControl(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (u == null)
			return 0;

		SPRITE sp = sprite[SpriteNum];
		int StateTics;

		if (u.State == null) {
			u.ActorActionFunc.invoke(SpriteNum);
			return (0);
		}

		if (sp.statnum >= STAT_SKIP4_START && sp.statnum <= STAT_SKIP4_END)
			u.Tics += ACTORMOVETICS * 2;
		else
			u.Tics += ACTORMOVETICS;

		// Skip states if too much time has passed
		while (u.Tics >= DTEST(u.State.Tics, SF_TICS_MASK)) {
			StateTics = DTEST(u.State.Tics, SF_TICS_MASK);

			if (TEST(u.State.Tics, SF_TIC_ADJUST)) {
				StateTics += u.Attrib.TicAdjust[u.speed];
			}

			// Set Tics
			u.Tics -= StateTics;

			// Transition to the next state
			u.State = u.State.getNext();

			// Look for flags embedded into the Tics variable
			while (TEST(u.State.Tics, SF_QUICK_CALL)) {
				// Call it once and go to the next state
				if (u.State.Animator != null)
					u.State.Animator.invoke(SpriteNum);

				// if still on the same QUICK_CALL should you
				// go to the next state.
				if (TEST(u.State.Tics, SF_QUICK_CALL))
					u.State = u.State.getNext();
			}

			if (u.State.Pic == 0)
				NewStateGroup(SpriteNum, u.State.getNextGroup());
		}

		if (u != null) {
			// Set picnum to the correct pic
			if (TEST(u.State.Tics, SF_WALL_STATE) && u.WallP != -1) {
				wall[u.WallP].picnum = u.State.Pic;
			} else {
				if (u.RotNum > 1 && u.Rot != null && u.Rot.getState(0) != null)
					sp.picnum = u.Rot.getState(0).Pic;
				else
					sp.picnum = u.State.Pic;
			}

			// Call the correct animator
			if (u.State.Animator != null)
				u.State.Animator.invoke(SpriteNum);
		}

		return (0);
	}

	public static void SpriteControl() {
		int i, nexti, stat;
		SPRITE sp;
		USER u;
		short pnum;
		boolean CloseToPlayer;
		PlayerStr pp;
		int dist;

		if (DebugActorFreeze)
			return;

		for (i = headspritestat[STAT_MISC]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			StateControl(i);
		}

		// Items and skip2 things
		if (!MoveSkip2) {
			for (stat = STAT_SKIP2_START + 1; stat <= STAT_SKIP2_END; stat++) {
				for (i = headspritestat[stat]; i != -1; i = nexti) {
					nexti = nextspritestat[i];
					StateControl(i);
				}
			}
		}

		if (!MoveSkip2) // limit to 20 times a second
		{
			// move bad guys around
			for (i = headspritestat[STAT_ENEMY]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				u = pUser[i];
				sp = u.getSprite();

				CloseToPlayer = false;

				ProcessActiveVars(i);

				for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
					pp = Player[pnum];

					// Only update the ones closest
					dist = DISTANCE(pp.posx, pp.posy, sp.x, sp.y);
					AdjustActiveRange(pp, i, dist);

					if (dist < u.active_range) {
						CloseToPlayer = true;
					}
				}

				u.Flags &= ~(SPR_MOVED);

				// Only update the ones close to ANY player
				if (CloseToPlayer) {
					StateControl(i);
				} else {
					// to far away to be attacked
					u.Flags &= ~(SPR_ATTACKED);
				}
			}
		}

		// Skip4 things
		if (MoveSkip4 == 0) // limit to 10 times a second
		{
			for (stat = STAT_SKIP4_START; stat <= STAT_SKIP4_END; stat++) {
				for (i = headspritestat[stat]; i != -1; i = nexti) {
					nexti = nextspritestat[i];
					StateControl(i);
				}
			}
		}

		for (i = headspritestat[STAT_NO_STATE]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			if (pUser[i] != null && pUser[i].ActorActionFunc != null) {
				pUser[i].ActorActionFunc.invoke(i);
			}
		}

		if (MoveSkip8 == 0) {
			for (i = headspritestat[STAT_STATIC_FIRE]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				DoStaticFlamesDamage(i);
			}
		}

		if (MoveSkip4 == 0) // limit to 10 times a second
		{
			for (i = headspritestat[STAT_WALLBLOOD_QUEUE]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				StateControl(i);
			}
		}

		// vator/rotator/spike/slidor all have some code to
		// prevent calling of the action func()
		for (i = headspritestat[STAT_VATOR]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			u = pUser[i];
			if(u == null)
				continue;

			if (u.Tics != 0) {
				if ((u.Tics -= synctics) <= 0)
					SetVatorActive(i);
				else
					continue;
			}

			if (!TEST(u.Flags, SPR_ACTIVE))
				continue;

			u.ActorActionFunc.invoke(i);
		}

		for (i = headspritestat[STAT_SPIKE]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			u = pUser[i];

			if (u.Tics != 0) {
				if ((u.Tics -= synctics) <= 0)
					SetSpikeActive(i);
				else
					continue;
			}

			if (!TEST(u.Flags, SPR_ACTIVE))
				continue;

			pUser[i].ActorActionFunc.invoke(i);
		}

		for (i = headspritestat[STAT_ROTATOR]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			u = pUser[i];

			if (u.Tics != 0) {
				if ((u.Tics -= synctics) <= 0)
					SetRotatorActive(i);
				else
					continue;
			}

			if (!TEST(u.Flags, SPR_ACTIVE))
				continue;

			pUser[i].ActorActionFunc.invoke(i);
		}

		for (i = headspritestat[STAT_SLIDOR]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			u = pUser[i];

			if (u.Tics != 0) {
				if ((u.Tics -= synctics) <= 0)
					SetSlidorActive(i);
				else
					continue;
			}

			if (!TEST(u.Flags, SPR_ACTIVE))
				continue;

			pUser[i].ActorActionFunc.invoke(i);
		}

		for (i = headspritestat[STAT_SUICIDE]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			KillSprite(i);
		}
	}

	//
	// This moves an actor about with FAFgetzrange clip adjustment
	//

	/*
	 *
	 * !AIC KEY - calls clipmove - Look through and try to understatnd Should
	 * hopefully never have to change this. Its very delicate.
	 *
	 */

	public static int move_sprite(int spritenum, int xchange, int ychange, int zchange, int ceildist, int flordist,
			int cliptype, int numtics) {
		int daz;
		int retval = 0, zh;
		short dasectnum, tempshort;
		SPRITE spr;

		USER u = pUser[spritenum];
		short lastsectnum;

		spr = sprite[spritenum];

		// Can't modify sprite sectors
		// directly becuase of linked lists
		dasectnum = lastsectnum = spr.sectnum;

		// Must do this if not using the new
		// centered centering (of course)
		daz = spr.z;

		if (TEST(spr.cstat, CSTAT_SPRITE_YCENTER)) {
			zh = 0;
		} else {
			// move the center point up for moving
			zh = u.zclip;
			daz -= zh;
		}

		clipmoveboxtracenum = 1;
		retval = engine.clipmove(spr.x, spr.y, daz, dasectnum, ((xchange * numtics) << 11), ((ychange * numtics) << 11),
				((spr.clipdist) << 2), ceildist, flordist, cliptype);

		spr.x = clipmove_x;
		spr.y = clipmove_y;
		daz = clipmove_z;
		dasectnum = clipmove_sectnum;

		clipmoveboxtracenum = 3;

		if (dasectnum < 0) {
			retval = HIT_WALL;
			return (retval);
		}

		if ((dasectnum != spr.sectnum) && (dasectnum >= 0))
			engine.changespritesect((short) spritenum, dasectnum);

		// took this out - may not be to relevant anymore
		// ASSERT(inside(spr.x,spr.y,dasectnum));

		// Set the blocking bit to 0 temporarly so FAFgetzrange doesn't pick
		// up its own sprite
		tempshort = spr.cstat;
		spr.cstat = 0;

		// I subtracted 8 from the clipdist because actors kept going up on
		// ledges they were not supposed to go up on. Did the same for the
		// player. Seems to work ok!
		FAFgetzrange(spr.x, spr.y, spr.z - zh - 1, spr.sectnum, tmp_ptr[0].set(globhiz), tmp_ptr[1].set(globhihit),
				tmp_ptr[2].set(globloz), tmp_ptr[3].set(globlohit), ((spr.clipdist) << 2) - GETZRANGE_CLIP_ADJ,
				cliptype);

		globhiz = tmp_ptr[0].value;
		globhihit = tmp_ptr[1].value;
		globloz = tmp_ptr[2].value;
		globlohit = tmp_ptr[3].value;

		spr.cstat = tempshort;

		// !AIC - puts getzrange results into USER varaible u.loz, u.hiz, u.lo_sectp,
		// u.hi_sectp, etc.
		// Takes info from global variables
		DoActorGlobZ(spritenum);

		daz = spr.z + ((zchange * numtics) >> 3);

		// test for hitting ceiling or floor
		if ((daz - zh <= globhiz) || (daz - zh > globloz)) {
			if (retval == 0) {
				if (TEST(u.Flags, SPR_CLIMBING)) {
					spr.z = daz;
					return (0);
				}

				retval = HIT_SECTOR | dasectnum;
			}
		} else {
			spr.z = daz;
		}

		// extra processing for Stacks and warping
		if (FAF_ConnectArea(spr.sectnum))
			engine.setspritez(spritenum, spr.x, spr.y, spr.z);

		if (TEST(sector[spr.sectnum].extra, SECTFX_WARP_SECTOR)) {
			SPRITE sp_warp;
			if ((sp_warp = WarpPlane(spr.x, spr.y, spr.z, dasectnum)) != null) {

				spr.x = warp.x;
				spr.y = warp.y;
				spr.z = warp.z;
				dasectnum = warp.sectnum;

				ActorWarpUpdatePos(spritenum, dasectnum);
				ActorWarpType(spr, sp_warp);
			}

			if (spr.sectnum != lastsectnum) {
				if ((sp_warp = WarpM(spr.x, spr.y, spr.z, dasectnum)) != null) {

					spr.x = warp.x;
					spr.y = warp.y;
					spr.z = warp.z;
					dasectnum = warp.sectnum;

					ActorWarpUpdatePos(spritenum, dasectnum);
					ActorWarpType(spr, sp_warp);
				}
			}
		}

		return (retval);
	}

	public static void MissileWarpUpdatePos(int SpriteNum, int sectnum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		u.ox = sp.x;
		u.oy = sp.y;
		u.oz = sp.z;
		engine.changespritesect((short) SpriteNum, (short) sectnum);
		MissileZrange(SpriteNum);
	}

	public static void ActorWarpUpdatePos(int SpriteNum, int sectnum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		u.ox = sp.x;
		u.oy = sp.y;
		u.oz = sp.z;
		engine.changespritesect((short) SpriteNum, (short) sectnum);
		DoActorZrange(SpriteNum);
	}

	public static void MissileWarpType(SPRITE sp, SPRITE sp_warp) {
		switch (SP_TAG1(sp_warp)) {
		case WARP_CEILING_PLANE:
		case WARP_FLOOR_PLANE:
			return;
		}

		switch (SP_TAG3(sp_warp)) {
		case 1:
			break;
		default:
			PlaySound(DIGI_ITEM_SPAWN, sp, v3df_none);
			DoSpawnItemTeleporterEffect(sp);
			break;
		}
	}

	public static void ActorWarpType(SPRITE sp, SPRITE sp_warp) {
		switch (SP_TAG3(sp_warp)) {
		case 1:
			break;
		default:
			PlaySound(DIGI_ITEM_SPAWN, sp, v3df_none);
			DoSpawnTeleporterEffectPlace(sp);
			break;
		}
	}

	//
	// This moves a small projectile with FAFgetzrangepoint
	//

	public static int MissileWaterAdjust(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (u.lo_sectp != -1) {
			Sect_User sectu = SectUser[u.lo_sectp];
			if (sectu != null && sectu.depth != 0)
				u.loz -= Z(sectu.depth);
		}
		return (0);
	}

	public static int MissileZrange(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		short tempshort;

		// Set the blocking bit to 0 temporarly so FAFgetzrange doesn't pick
		// up its own sprite
		tempshort = sp.cstat;
		sp.cstat &= ~(CSTAT_SPRITE_BLOCK);

		FAFgetzrangepoint(sp.x, sp.y, sp.z - 1, sp.sectnum, tmp_ptr[0].set(globhiz), tmp_ptr[1].set(globhihit),
				tmp_ptr[2].set(globloz), tmp_ptr[3].set(globlohit));

		globhiz = tmp_ptr[0].value;
		globhihit = tmp_ptr[1].value;
		globloz = tmp_ptr[2].value;
		globlohit = tmp_ptr[3].value;

		sp.cstat = tempshort;

		DoActorGlobZ(SpriteNum);
		return (0);
	}

	public static int move_missile(int spritenum, int xchange, int ychange, int zchange, int ceildist, int flordist,
			int cliptype, int numtics) {
		int daz;
		int retval, zh;
		short dasectnum, tempshort;
		SPRITE sp;
		USER u = pUser[spritenum];
		short lastsectnum;

		sp = sprite[spritenum];

		// Can't modify sprite sectors
		// directly becuase of linked lists
		dasectnum = lastsectnum = sp.sectnum;

		// Can't modify sprite sectors
		// directly becuase of linked lists
		daz = sp.z;

		if (TEST(sp.cstat, CSTAT_SPRITE_YCENTER)) {
			zh = 0;
		} else {
			zh = u.zclip;
			daz -= zh;
		}

		clipmoveboxtracenum = 1;
		retval = engine.clipmove(sp.x, sp.y, daz, dasectnum, ((xchange * numtics) << 11), ((ychange * numtics) << 11),
				((sp.clipdist) << 2), ceildist, flordist, cliptype);

		sp.x = clipmove_x;
		sp.y = clipmove_y;
		daz = clipmove_z;
		dasectnum = clipmove_sectnum;

		clipmoveboxtracenum = 3;

		if (dasectnum < 0) {
			// we've gone beyond a white wall - kill it
			retval = 0;
			retval |= (HIT_PLAX_WALL);
			return (retval);
		}

		// took this out - may not be to relevant anymore

		if ((dasectnum != sp.sectnum) && (dasectnum >= 0))
			engine.changespritesect((short) spritenum, dasectnum);

		// Set the blocking bit to 0 temporarly so FAFgetzrange doesn't pick
		// up its own sprite
		tempshort = sp.cstat;
		sp.cstat &= ~(CSTAT_SPRITE_BLOCK);

		FAFgetzrangepoint(sp.x, sp.y, sp.z - 1, sp.sectnum, tmp_ptr[0].set(globhiz), tmp_ptr[1].set(globhihit),
				tmp_ptr[2].set(globloz), tmp_ptr[3].set(globlohit));

		globhiz = tmp_ptr[0].value;
		globhihit = tmp_ptr[1].value;
		globloz = tmp_ptr[2].value;
		globlohit = tmp_ptr[3].value;

		sp.cstat = tempshort;

		DoActorGlobZ(spritenum);

		// getzrangepoint moves water down
		// missiles don't need the water to be down
		MissileWaterAdjust(spritenum);

		daz = sp.z + ((zchange * numtics) >> 3);

		// NOTE: this does not tell you when you hit a floor sprite
		// this case is currently treated like it hit a sector

		// test for hitting ceiling or floor
		if (daz - zh <= u.hiz + ceildist) {
			// normal code
			sp.z = u.hiz + zh + ceildist;
			if (retval == 0)
				retval = dasectnum | HIT_SECTOR;
		} else if (daz - zh > u.loz - flordist) {
			sp.z = u.loz + zh - flordist;
			if (retval == 0)
				retval = dasectnum | HIT_SECTOR;
		} else {
			sp.z = daz;
		}

		if (FAF_ConnectArea(sp.sectnum))
			engine.setspritez((short) spritenum, sp.x, sp.y, sp.z);

		if (TEST(sector[sp.sectnum].extra, SECTFX_WARP_SECTOR)) {
			SPRITE sp_warp;

			if ((sp_warp = WarpPlane(sp.x, sp.y, sp.z, dasectnum)) != null) {

				sp.x = warp.x;
				sp.y = warp.y;
				sp.z = warp.z;
				dasectnum = warp.sectnum;

				MissileWarpUpdatePos(spritenum, dasectnum);
				MissileWarpType(sp, sp_warp);
			}

			if (sp.sectnum != lastsectnum) {
				if ((sp_warp = WarpM(sp.x, sp.y, sp.z, dasectnum)) != null) {

					sp.x = warp.x;
					sp.y = warp.y;
					sp.z = warp.z;
					dasectnum = warp.sectnum;

					MissileWarpUpdatePos(spritenum, dasectnum);
					MissileWarpType(sp, sp_warp);
				}
			}
		}

		if (retval != 0 && TEST(sector[sp.sectnum].ceilingstat, CEILING_STAT_PLAX)) {
			if (sp.z < sector[sp.sectnum].ceilingz) {
				retval &= ~(HIT_WALL | HIT_SECTOR);
				retval |= (HIT_PLAX_WALL);
			}
		}

		if (retval != 0 && TEST(sector[sp.sectnum].floorstat, FLOOR_STAT_PLAX)) {
			if (sp.z > sector[sp.sectnum].floorz) {
				retval &= ~(HIT_WALL | HIT_SECTOR);
				retval |= (HIT_PLAX_WALL);
			}
		}

		return (retval);
	}

	public static int move_ground_missile(int spritenum, int xchange, int ychange, int zchange, int ceildist,
			int flordist, int cliptype, int numtics) {
		int daz;
		int retval = 0;
		short dasectnum;
		SPRITE sp;
		USER u = pUser[spritenum];
		short lastsectnum;
		int ox, oy;

		sp = sprite[spritenum];

		// Can't modify sprite sectors
		// directly becuase of linked lists
		dasectnum = lastsectnum = sp.sectnum;

		daz = sp.z;

		// climbing a wall
		if (u.z_tgt != -1) {
			if (klabs(u.z_tgt - sp.z) > Z(40)) {
				if (u.z_tgt > sp.z) {
					sp.z += Z(30);
					return (retval);
				} else {
					sp.z -= Z(30);
					return (retval);
				}
			} else
				u.z_tgt = 0;
		}

		ox = sp.x;
		oy = sp.y;
		sp.x += xchange / 2;
		sp.y += ychange / 2;

		dasectnum = engine.updatesector(sp.x, sp.y, dasectnum);

		if (dasectnum < 0) {
			// back up and try again
			dasectnum = lastsectnum = sp.sectnum;
			sp.x = ox;
			sp.y = oy;
			clipmoveboxtracenum = 1;
			retval = engine.clipmove(sp.x, sp.y, daz, dasectnum, ((xchange * numtics) << 11),
					((ychange * numtics) << 11), ((sp.clipdist) << 2), ceildist, flordist, cliptype);

			sp.x = clipmove_x;
			sp.y = clipmove_y;
			daz = clipmove_z;
			dasectnum = clipmove_sectnum;

			clipmoveboxtracenum = 3;
		}

		if (dasectnum < 0) {
			// we've gone beyond a white wall - kill it
			retval = 0;
			retval |= (HIT_PLAX_WALL);
			return (retval);
		}

		if (retval != 0) // ran into a white wall
		{
			return (retval);
		}

		u.z_tgt = 0;
		if ((dasectnum != sp.sectnum) && (dasectnum >= 0)) {
			int new_loz;
			engine.getzsofslope(dasectnum, sp.x, sp.y, zofslope);
			new_loz = zofslope[FLOOR];

			sp.z = new_loz;

			engine.changespritesect((short) spritenum, dasectnum);
		}

		engine.getzsofslope(sp.sectnum, sp.x, sp.y, zofslope);
		u.hiz = zofslope[CEIL];
		u.loz = zofslope[FLOOR];

		u.hi_sectp = u.lo_sectp = sp.sectnum;
		u.hi_sp = u.lo_sp = -1;
		sp.z = u.loz - Z(8);

		if (klabs(u.hiz - u.loz) < Z(12)) {
			// we've gone into a very small place - kill it
			retval = 0;
			retval |= (HIT_PLAX_WALL);
			return (retval);
		}

		// getzrangepoint moves water down
		// missiles don't need the water to be down
		// MissileWaterAdjust(spritenum);

		if (TEST(sector[sp.sectnum].extra, SECTFX_WARP_SECTOR)) {
			SPRITE sp_warp;

			if ((sp_warp = WarpPlane(sp.x, sp.y, sp.z, dasectnum)) != null) {

				sp.x = warp.x;
				sp.y = warp.y;
				sp.z = warp.z;
				dasectnum = warp.sectnum;

				MissileWarpUpdatePos(spritenum, dasectnum);
				MissileWarpType(sp, sp_warp);
			}

			if (sp.sectnum != lastsectnum) {
				if ((sp_warp = WarpM(sp.x, sp.y, sp.z, dasectnum)) != null) {

					sp.x = warp.x;
					sp.y = warp.y;
					sp.z = warp.z;
					dasectnum = warp.sectnum;

					MissileWarpUpdatePos(spritenum, dasectnum);
					MissileWarpType(sp, sp_warp);
				}
			}
		}

		return (retval);
	}

	public static void SpritesSaveable() {
		SaveData(DoSpriteFade);
		SaveData(DoFireFly);
		SaveData(DoGrating);
		SaveData(DoKey);
		SaveData(DoCoin);
		SaveData(DoGet);
		SaveData(s_DebrisNinja);
		SaveData(s_DebrisRat);
		SaveData(s_DebrisCrab);
		SaveData(s_DebrisStarFish);
		SaveData(s_RepairKit);
		SaveData(s_GoldSkelKey);
		SaveData(s_BlueKey);
		SaveData(s_BlueCard);
		SaveData(s_SilverSkelKey);
		SaveData(s_RedKey);
		SaveData(s_RedCard);
		SaveData(s_BronzeSkelKey);
		SaveData(s_GreenKey);
		SaveData(s_GreenCard);
		SaveData(s_RedSkelKey);
		SaveData(s_YellowKey);
		SaveData(s_YellowCard);
		SaveData(s_Key);
		SaveData(s_BlueKey);
		SaveData(s_RedKey);
		SaveData(s_GreenKey);
		SaveData(s_YellowKey);
		SaveData(s_Key);
		SaveData(s_RedCoin);
		SaveData(s_YellowCoin);
		SaveData(s_GreenCoin);
		SaveData(s_FireFly);
		SaveData(s_IconStar);
		SaveData(s_IconUzi);
		SaveData(s_IconLgUziAmmo);
		SaveData(s_IconUziFloor);
		SaveData(s_IconRocket);
		SaveData(s_IconLgRocket);
		SaveData(s_IconShotgun);
		SaveData(s_IconLgShotshell);
		SaveData(s_IconAutoRiot);
		SaveData(s_IconGrenadeLauncher);
		SaveData(s_IconLgGrenade);
		SaveData(s_IconLgMine);
		SaveData(s_IconGuardHead);
		SaveData(s_IconFireballLgAmmo);
		SaveData(s_IconHeart);
		SaveData(s_IconHeartLgAmmo);
		SaveData(s_IconMicroGun);
		SaveData(s_IconMicroBattery);
		SaveData(s_IconRailGun);
		SaveData(s_IconRailAmmo);
		SaveData(s_IconElectro);
		SaveData(s_IconSpell);
		SaveData(s_IconArmor);
		SaveData(s_IconMedkit);
		SaveData(s_IconChemBomb);
		SaveData(s_IconFlashBomb);
		SaveData(s_IconNuke);
		SaveData(s_IconCaltrops);
		SaveData(s_IconSmMedkit);
		SaveData(s_IconBooster);
		SaveData(s_IconHeatCard);
		SaveData(s_IconCloak);
		SaveData(s_IconFly);
		SaveData(s_IconNightVision);
		SaveData(s_IconFlag);
	}
}
