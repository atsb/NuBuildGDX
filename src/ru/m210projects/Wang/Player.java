package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.CEIL;
import static ru.m210projects.Build.Engine.FLOOR;
import static ru.m210projects.Build.Engine.clipmove_sectnum;
import static ru.m210projects.Build.Engine.clipmove_x;
import static ru.m210projects.Build.Engine.clipmove_y;
import static ru.m210projects.Build.Engine.clipmove_z;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.neartag;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Engine.pushmove_sectnum;
import static ru.m210projects.Build.Engine.pushmove_x;
import static ru.m210projects.Build.Engine.pushmove_y;
import static ru.m210projects.Build.Engine.pushmove_z;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.BClampAngle;
import static ru.m210projects.Build.Gameutils.BCosAngle;
import static ru.m210projects.Wang.Game.MessageInputMode;
import static ru.m210projects.Build.Gameutils.BSinAngle;
import static ru.m210projects.Build.Gameutils.isValidSector;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.ksgn;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Wang.Actor.DoActorBeginSlide;
import static ru.m210projects.Wang.Actor.DoActorSlide;
import static ru.m210projects.Wang.Actor.DoBeginJump;
import static ru.m210projects.Wang.Actor.DoFall;
import static ru.m210projects.Wang.Actor.DoJump;
import static ru.m210projects.Wang.Break.HitBreakSprite;
import static ru.m210projects.Wang.Digi.DIGI_BODYCRUSHED1;
import static ru.m210projects.Wang.Digi.DIGI_BODYFALL1;
import static ru.m210projects.Wang.Digi.DIGI_BODYFALL2;
import static ru.m210projects.Wang.Digi.DIGI_BODYSQUISH1;
import static ru.m210projects.Wang.Digi.DIGI_BUBBLES;
import static ru.m210projects.Wang.Digi.DIGI_DHVOMIT;
import static ru.m210projects.Wang.Digi.DIGI_FALLSCREAM;
import static ru.m210projects.Wang.Digi.DIGI_HITGROUND;
import static ru.m210projects.Wang.Digi.DIGI_PLAYERPAIN2;
import static ru.m210projects.Wang.Digi.DIGI_SPLASH1;
import static ru.m210projects.Wang.Digi.DIGI_SURFACE;
import static ru.m210projects.Wang.Digi.DIGI_TELEPORT;
import static ru.m210projects.Wang.Digi.DIGI_USEBROKENVEHICLE;
import static ru.m210projects.Wang.Digi.DIGI_WANGDROWNING;
import static ru.m210projects.Wang.Draw.HelpInputMode;
import static ru.m210projects.Wang.Draw.NoMeters;
import static ru.m210projects.Wang.Draw.SetRedrawScreen;
import static ru.m210projects.Wang.Draw.bCopySpriteOffs;
import static ru.m210projects.Wang.Enemies.Ninja.PlayerDeathReset;
import static ru.m210projects.Wang.Enemies.Ninja.SpawnPlayerUnderSprite;
import static ru.m210projects.Wang.Factory.WangMenuHandler.LASTSAVE;
import static ru.m210projects.Wang.Factory.WangNetwork.CommPlayers;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Game.ChopTics;
import static ru.m210projects.Wang.Game.DebugActorFreeze;
import static ru.m210projects.Wang.Game.DebugAnim;
import static ru.m210projects.Wang.Game.DebugSector;
import static ru.m210projects.Wang.Game.DemoPlaying;
import static ru.m210projects.Wang.Game.DemoRecording;
import static ru.m210projects.Wang.Game.Distance;
import static ru.m210projects.Wang.Game.ExitLevel;
import static ru.m210projects.Wang.Game.FinishAnim;
import static ru.m210projects.Wang.Game.FinishedLevel;
import static ru.m210projects.Wang.Game.GodMode;
import static ru.m210projects.Wang.Game.Level;
import static ru.m210projects.Wang.Game.PlayClock;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.PlayerTrackingMode;
import static ru.m210projects.Wang.Game.isOriginal;
import static ru.m210projects.Wang.Game.rec;
import static ru.m210projects.Wang.Game.screenpeek;
import static ru.m210projects.Wang.Game.tmp_ptr;
import static ru.m210projects.Wang.Game.totalsynctics;
import static ru.m210projects.Wang.Gameutils.*;
import static ru.m210projects.Wang.Inv.INVENTORY_REPAIR_KIT;
import static ru.m210projects.Wang.Inv.InventoryTimer;
import static ru.m210projects.Wang.Inv.KillInventoryBar;
import static ru.m210projects.Wang.Inv.MAX_INVENTORY;
import static ru.m210projects.Wang.Inv.UseInventoryCaltrops;
import static ru.m210projects.Wang.Inv.UseInventoryChemBomb;
import static ru.m210projects.Wang.Inv.UseInventoryCloak;
import static ru.m210projects.Wang.Inv.UseInventoryFlashBomb;
import static ru.m210projects.Wang.Inv.UseInventoryMedkit;
import static ru.m210projects.Wang.Inv.UseInventoryNightVision;
import static ru.m210projects.Wang.Inv.UseInventoryRepairKit;
import static ru.m210projects.Wang.JPlayer.adduserquote;
import static ru.m210projects.Wang.JPlayer.quotebot;
import static ru.m210projects.Wang.JPlayer.quotebotgoal;
import static ru.m210projects.Wang.JPlayer.user_quote_time;
import static ru.m210projects.Wang.JSector.JS_ProcessEchoSpot;
import static ru.m210projects.Wang.JWeapon.InitBloodSpray;
import static ru.m210projects.Wang.LoadSave.lastload;
import static ru.m210projects.Wang.MClip.MultiClipMove;
import static ru.m210projects.Wang.MClip.MultiClipTurn;
import static ru.m210projects.Wang.MClip.RectClipMove;
import static ru.m210projects.Wang.MClip.RectClipTurn;
import static ru.m210projects.Wang.MClip.testpointinquad;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.gAnmScreen;
import static ru.m210projects.Wang.Main.gDemoScreen;
import static ru.m210projects.Wang.Main.gGameScreen;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Main.gStatisticScreen;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Names.NINJA_DEAD;
import static ru.m210projects.Wang.Names.NINJA_Head_R0;
import static ru.m210projects.Wang.Names.NINJA_Head_R1;
import static ru.m210projects.Wang.Names.NINJA_Head_R2;
import static ru.m210projects.Wang.Names.NINJA_Head_R3;
import static ru.m210projects.Wang.Names.NINJA_Head_R4;
import static ru.m210projects.Wang.Names.NINJA_RUN_R0;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_CLIMB_R0;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_CLIMB_R1;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_CLIMB_R2;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_CLIMB_R3;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_CLIMB_R4;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_CRAWL_R0;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_CRAWL_R1;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_CRAWL_R2;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_CRAWL_R3;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_CRAWL_R4;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_DIE;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_JUMP_R0;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_JUMP_R1;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_JUMP_R2;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_JUMP_R3;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_JUMP_R4;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_PUNCH_R0;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_PUNCH_R1;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_PUNCH_R2;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_PUNCH_R3;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_PUNCH_R4;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_RUN_R0;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_RUN_R1;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_RUN_R2;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_RUN_R3;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_RUN_R4;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_STAND_R0;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_STAND_R1;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_STAND_R2;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_STAND_R3;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_STAND_R4;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_SWIM_R0;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_SWIM_R1;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_SWIM_R2;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_SWIM_R3;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_SWIM_R4;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_SWORD_R0;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_SWORD_R1;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_SWORD_R2;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_SWORD_R3;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_SWORD_R4;
import static ru.m210projects.Wang.Names.ST1;
import static ru.m210projects.Wang.Names.STAT_CEILING_PAN;
import static ru.m210projects.Wang.Names.STAT_CLIMB_MARKER;
import static ru.m210projects.Wang.Names.STAT_CO_OP_START;
import static ru.m210projects.Wang.Names.STAT_DEAD_ACTOR;
import static ru.m210projects.Wang.Names.STAT_DIVE_AREA;
import static ru.m210projects.Wang.Names.STAT_DONT_DRAW;
import static ru.m210projects.Wang.Names.STAT_ENEMY;
import static ru.m210projects.Wang.Names.STAT_FLOOR_PAN;
import static ru.m210projects.Wang.Names.STAT_MISSILE;
import static ru.m210projects.Wang.Names.STAT_MULTI_START;
import static ru.m210projects.Wang.Names.STAT_PLAYER0;
import static ru.m210projects.Wang.Names.STAT_SKIP2_INTERP_END;
import static ru.m210projects.Wang.Names.STAT_SKIP2_START;
import static ru.m210projects.Wang.Names.STAT_SKIP4_INTERP_END;
import static ru.m210projects.Wang.Names.STAT_SKIP4_START;
import static ru.m210projects.Wang.Names.STAT_SOUND_SPOT;
import static ru.m210projects.Wang.Names.STAT_SO_SHOOT_POINT;
import static ru.m210projects.Wang.Names.STAT_ST1;
import static ru.m210projects.Wang.Names.STAT_TRIGGER;
import static ru.m210projects.Wang.Names.STAT_UNDERWATER;
import static ru.m210projects.Wang.Names.STAT_UNDERWATER2;
import static ru.m210projects.Wang.Names.STAT_WALL_PAN;
import static ru.m210projects.Wang.Names.ZOMBIE_RUN_R0;
import static ru.m210projects.Wang.Palette.DoPaletteFlash;
import static ru.m210projects.Wang.Palette.DoPlayerDivePalette;
import static ru.m210projects.Wang.Palette.DoPlayerNightVisionPalette;
import static ru.m210projects.Wang.Palette.FORCERESET;
import static ru.m210projects.Wang.Palette.ResetPalette;
import static ru.m210projects.Wang.Panel.BORDER_BAR;
import static ru.m210projects.Wang.Panel.PlayerUpdateHealth;
import static ru.m210projects.Wang.Panel.PlayerUpdateKills;
import static ru.m210projects.Wang.Panel.WeaponOperate;
import static ru.m210projects.Wang.Panel.pSpriteControl;
import static ru.m210projects.Wang.Panel.pWeaponForceRest;
import static ru.m210projects.Wang.Quake.ProcessQuakeOn;
import static ru.m210projects.Wang.Quake.ProcessQuakeSpot;
import static ru.m210projects.Wang.Quake.SetPlayerQuake;
import static ru.m210projects.Wang.Rooms.COVERupdatesector;
import static ru.m210projects.Wang.Rooms.FAFcansee;
import static ru.m210projects.Wang.Rooms.FAFgetzrange;
import static ru.m210projects.Wang.Rooms.FAFhitscan;
import static ru.m210projects.Wang.Rooms.GetZadjustment;
import static ru.m210projects.Wang.Rooms.zofslope;
import static ru.m210projects.Wang.Sector.DoAnim;
import static ru.m210projects.Wang.Sector.DoMatchEverything;
import static ru.m210projects.Wang.Sector.DoSector;
import static ru.m210projects.Wang.Sector.DoSoundSpotMatch;
import static ru.m210projects.Wang.Sector.DoSoundSpotStopSound;
import static ru.m210projects.Wang.Sector.GlobPlayerStr;
import static ru.m210projects.Wang.Sector.NTAG_SEARCH_LO_HI;
import static ru.m210projects.Wang.Sector.OperateTripTrigger;
import static ru.m210projects.Wang.Sector.PlayerOperateEnv;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Shrap.SpawnShrap;
import static ru.m210projects.Wang.Sound.COVER_SetReverb;
import static ru.m210projects.Wang.Sound.DoUpdateSounds3D;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.PlayerLowHealthPainVocs;
import static ru.m210projects.Wang.Sound.PlayerSound;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_doppler;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sound.v3df_none;
import static ru.m210projects.Wang.Sprites.ActorCoughItem;
import static ru.m210projects.Wang.Sprites.KillSprite;
import static ru.m210projects.Wang.Sprites.MoveSkip2;
import static ru.m210projects.Wang.Sprites.MoveSkip4;
import static ru.m210projects.Wang.Sprites.MoveSkip8;
import static ru.m210projects.Wang.Sprites.NewStateGroup;
import static ru.m210projects.Wang.Sprites.SectorObject;
import static ru.m210projects.Wang.Sprites.SpawnSprite;
import static ru.m210projects.Wang.Sprites.SpriteControl;
import static ru.m210projects.Wang.Sprites.change_sprite_stat;
import static ru.m210projects.Wang.Sprites.move_sprite;
import static ru.m210projects.Wang.Stag.FLOOR_Z_ADJUST;
import static ru.m210projects.Wang.Stag.MULTI_COOPERATIVE_START;
import static ru.m210projects.Wang.Stag.MULTI_PLAYER_START;
import static ru.m210projects.Wang.Stag.SO_DRIVABLE_ATTRIB;
import static ru.m210projects.Wang.Tags.TAG_WALL_CLIMB;
import static ru.m210projects.Wang.Text.GlobInfoStringTime;
import static ru.m210projects.Wang.Text.PutStringInfo;
import static ru.m210projects.Wang.Text.TEXT_INFO_LINE;
import static ru.m210projects.Wang.Text.pClearTextLine;
import static ru.m210projects.Wang.Track.OperateSectorObject;
import static ru.m210projects.Wang.Track.PlaceSectorObject;
import static ru.m210projects.Wang.Track.PlayerOnObject;
import static ru.m210projects.Wang.Track.VehicleSetSmoke;
import static ru.m210projects.Wang.Type.MyTypes.DIV2;
import static ru.m210projects.Wang.Type.MyTypes.DIV32;
import static ru.m210projects.Wang.Type.MyTypes.DIV4;
import static ru.m210projects.Wang.Type.MyTypes.DIV8;
import static ru.m210projects.Wang.Type.MyTypes.DTEST;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.RTS.rtsplaying;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Type.Saveable.SaveGroup;
import static ru.m210projects.Wang.Vis.ProcessVisOn;
import static ru.m210projects.Wang.Warp.WarpM;
import static ru.m210projects.Wang.Warp.WarpPlane;
import static ru.m210projects.Wang.Warp.warp;
import static ru.m210projects.Wang.Weapon.DeathString;
import static ru.m210projects.Wang.Weapon.DoFindGround;
import static ru.m210projects.Wang.Weapon.FinishTimer;
import static ru.m210projects.Wang.Weapon.FootMode;
import static ru.m210projects.Wang.Weapon.PlayerCheckDeath;
import static ru.m210projects.Wang.Weapon.PlayerDamageSlide;
import static ru.m210projects.Wang.Weapon.QueueFloorBlood;
import static ru.m210projects.Wang.Weapon.QueueFootPrint;
import static ru.m210projects.Wang.Weapon.QueueLoWangs;
import static ru.m210projects.Wang.Weapon.SetSuicide;
import static ru.m210projects.Wang.Weapon.SpawnBubble;
import static ru.m210projects.Wang.Weapon.SpawnSplash;
import static ru.m210projects.Wang.Weapon.SpriteQueueDelete;
import static ru.m210projects.Wang.Weapon.SpriteWarpToUnderwater;
import static ru.m210projects.Wang.Weapon.StatDamageList;
import static ru.m210projects.Wang.Weapon.UpdateSinglePlayKills;
import static ru.m210projects.Wang.Weapon.VehicleMoveHit;
import static ru.m210projects.Wang.Weapon.s_TeleportEffect;
import static ru.m210projects.Wang.Weapons.Chops.ChopsSetRetract;
import static ru.m210projects.Wang.Weapons.Chops.InitChops;
import static ru.m210projects.Wang.Weapons.Fist.InitWeaponFist;
import static ru.m210projects.Wang.Weapons.Grenade.InitWeaponGrenade;
import static ru.m210projects.Wang.Weapons.Heart.InitWeaponHeart;
import static ru.m210projects.Wang.Weapons.HotHead.InitWeaponHothead;
import static ru.m210projects.Wang.Weapons.Micro.InitWeaponMicro;
import static ru.m210projects.Wang.Weapons.Mine.InitWeaponMine;
import static ru.m210projects.Wang.Weapons.Rail.InitWeaponRail;
import static ru.m210projects.Wang.Weapons.Shotgun.InitWeaponShotgun;
import static ru.m210projects.Wang.Weapons.Star.InitWeaponStar;
import static ru.m210projects.Wang.Weapons.Sword.InitWeaponSword;
import static ru.m210projects.Wang.Weapons.Uzi.InitWeaponUzi;

import java.util.Arrays;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Wang.Gameutils.FootType;
import ru.m210projects.Wang.Sound.SoundType;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Factory.WangNetwork.MultiGameTypes;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.Input;
import ru.m210projects.Wang.Type.LONGp;
import ru.m210projects.Wang.Type.List;
import ru.m210projects.Wang.Type.Panel_Sprite;
import ru.m210projects.Wang.Type.Panel_Sprite_Func;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.Remote_Control;
import ru.m210projects.Wang.Type.Sect_User;
import ru.m210projects.Wang.Type.Sector_Object;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.Target_Sort;
import ru.m210projects.Wang.Type.USER;
import ru.m210projects.Wang.Type.VOC3D;

public class Player {

	public enum Player_Action_Func {
		DoPlayerCrawl {
			@Override
			public void invoke(PlayerStr pp) {
				DoPlayerCrawl(pp);
			}
		},

		DoPlayerTeleportPause {
			@Override
			public void invoke(PlayerStr pp) {
				DoPlayerTeleportPause(pp);
			}
		},

		DoPlayerJump {
			@Override
			public void invoke(PlayerStr pp) {
				DoPlayerJump(pp);
			}
		},

		DoPlayerFall {
			@Override
			public void invoke(PlayerStr pp) {
				DoPlayerFall(pp);
			}
		},

		DoPlayerForceJump {
			@Override
			public void invoke(PlayerStr pp) {
				DoPlayerForceJump(pp);
			}
		},

		DoPlayerClimb {
			@Override
			public void invoke(PlayerStr pp) {
				DoPlayerClimb(pp);
			}
		},

		DoPlayerFly {
			@Override
			public void invoke(PlayerStr pp) {
				DoPlayerFly(pp);
			}
		},

		DoPlayerDive {
			@Override
			public void invoke(PlayerStr pp) {
				DoPlayerDive(pp);
			}
		},

		DoPlayerWade {
			@Override
			public void invoke(PlayerStr pp) {
				DoPlayerWade(pp);
			}
		},

		DoPlayerOperateTurret {
			@Override
			public void invoke(PlayerStr pp) {
				DoPlayerOperateTurret(pp);
			}
		},

		DoPlayerOperateBoat {
			@Override
			public void invoke(PlayerStr pp) {
				DoPlayerOperateBoat(pp);
			}
		},

		DoPlayerOperateTank {
			@Override
			public void invoke(PlayerStr pp) {
				DoPlayerOperateTank(pp);
			}
		},

		DoPlayerRun {
			@Override
			public void invoke(PlayerStr pp) {
				DoPlayerRun(pp);
			}
		},

		DoPlayerDeathFlip {
			@Override
			public void invoke(PlayerStr pp) {
				DoPlayerDeathFlip(pp);
			}
		},

		DoPlayerDeathDrown {
			@Override
			public void invoke(PlayerStr pp) {
				DoPlayerDeathDrown(pp);
			}
		},

		DoPlayerDeathCrumble {
			@Override
			public void invoke(PlayerStr pp) {
				DoPlayerDeathCrumble(pp);
			}
		},

		DoPlayerDeathExplode {
			@Override
			public void invoke(PlayerStr pp) {
				DoPlayerDeathExplode(pp);
			}
		},

		DoPlayerBeginRun {
			@Override
			public void invoke(PlayerStr pp) {
				DoPlayerBeginRun(pp);
			}
		},

		InitWeaponSword {
			@Override
			public void invoke(PlayerStr pp) {
				InitWeaponSword(pp);
			}
		},

		InitWeaponFist {
			@Override
			public void invoke(PlayerStr pp) {
				InitWeaponFist(pp);
			}
		},

		InitWeaponStar {
			@Override
			public void invoke(PlayerStr pp) {
				InitWeaponStar(pp);
			}
		},

		InitWeaponShotgun {
			@Override
			public void invoke(PlayerStr pp) {
				InitWeaponShotgun(pp);
			}
		},

		InitWeaponUzi {
			@Override
			public void invoke(PlayerStr pp) {
				InitWeaponUzi(pp);
			}
		},

		InitWeaponMicro {
			@Override
			public void invoke(PlayerStr pp) {
				InitWeaponMicro(pp);
			}
		},

		InitWeaponGrenade {
			@Override
			public void invoke(PlayerStr pp) {
				InitWeaponGrenade(pp);
			}
		},

		InitWeaponMine {
			@Override
			public void invoke(PlayerStr pp) {
				InitWeaponMine(pp);
			}
		},

		InitWeaponRail {
			@Override
			public void invoke(PlayerStr pp) {
				InitWeaponRail(pp);
			}
		},

		InitWeaponHothead {
			@Override
			public void invoke(PlayerStr pp) {
				InitWeaponHothead(pp);
			}
		},

		InitWeaponHeart {
			@Override
			public void invoke(PlayerStr pp) {
				InitWeaponHeart(pp);
			}
		},

		UseInventoryMedkit {
			@Override
			public void invoke(PlayerStr pp) {
				UseInventoryMedkit(pp);
			}
		},

		UseInventoryCloak {
			@Override
			public void invoke(PlayerStr pp) {
				UseInventoryCloak(pp);
			}
		},

		UseInventoryNightVision {
			@Override
			public void invoke(PlayerStr pp) {
				UseInventoryNightVision(pp);
			}
		},

		UseInventoryChemBomb {
			@Override
			public void invoke(PlayerStr pp) {
				UseInventoryChemBomb(pp);
			}
		},

		UseInventoryFlashBomb {
			@Override
			public void invoke(PlayerStr pp) {
				UseInventoryFlashBomb(pp);
			}
		},

		UseInventoryCaltrops {
			@Override
			public void invoke(PlayerStr pp) {
				UseInventoryCaltrops(pp);
			}
		};

		public abstract void invoke(PlayerStr pp);

	}

	public static final int SO_DRIVE_SOUND = 2;
	public static final int SO_IDLE_SOUND = 1;

	public static short NormalVisibility;

	public static final int PLAYER_HORIZ_MAX = 299; // !JIM! was 199 and 5
	public static final int PLAYER_HORIZ_MIN = -99; // Had to make plax sky pan up/down like in Duke
	// But this is MUCH better!

	public static final int MIN_SWIM_DEPTH = 15;

	// Player view height
	public static final int PLAYER_HEIGHT = Z(58);
	public static final int PLAYER_CRAWL_HEIGHT = Z(36);
	public static final int PLAYER_SWIM_HEIGHT = Z(26);
	public static final int PLAYER_DIVE_HEIGHT = Z(26);
	public static final int PLAYER_DIE_DOWN_HEIGHT = Z(4);
	public static final int PLAYER_DIE_UP_HEIGHT = Z(8);

	// step heights - effects floor_dist's
	public static final int PLAYER_STEP_HEIGHT = Z(30);

	public static final int PLAYER_CRAWL_STEP_HEIGHT = Z(8);
	public static final int PLAYER_SWIM_STEP_HEIGHT = Z(8);
	public static final int PLAYER_DIVE_STEP_HEIGHT = Z(8);

	public static final int PLAYER_JUMP_STEP_HEIGHT = Z(48);
	public static final int PLAYER_FALL_STEP_HEIGHT = Z(24);

	// FLOOR_DIST variables are the difference in the Players view and the sector
	// floor.
	// Must be at LEAST this distance or you cannot move onto sector.
	public static final int PLAYER_RUN_FLOOR_DIST = (PLAYER_HEIGHT - PLAYER_STEP_HEIGHT);
	public static final int PLAYER_CRAWL_FLOOR_DIST = (PLAYER_CRAWL_HEIGHT - PLAYER_CRAWL_STEP_HEIGHT);
	public static final int PLAYER_WADE_FLOOR_DIST = (PLAYER_HEIGHT - PLAYER_STEP_HEIGHT);
	public static final int PLAYER_JUMP_FLOOR_DIST = (PLAYER_HEIGHT - PLAYER_JUMP_STEP_HEIGHT);
	public static final int PLAYER_FALL_FLOOR_DIST = (PLAYER_HEIGHT - PLAYER_FALL_STEP_HEIGHT);
	public static final int PLAYER_SWIM_FLOOR_DIST = (PLAYER_SWIM_HEIGHT - PLAYER_SWIM_STEP_HEIGHT);
	public static final int PLAYER_DIVE_FLOOR_DIST = (PLAYER_DIVE_HEIGHT - PLAYER_DIVE_STEP_HEIGHT);

	// FLOOR_DIST variables are the difference in the Players view and the sector
	// floor.
	// Must be at LEAST this distance or you cannot move onto sector.
	public static final int PLAYER_RUN_CEILING_DIST = Z(10);
	public static final int PLAYER_SWIM_CEILING_DIST = (Z(12));
	public static final int PLAYER_DIVE_CEILING_DIST = (Z(22));
	public static final int PLAYER_CRAWL_CEILING_DIST = (Z(12));
	public static final int PLAYER_JUMP_CEILING_DIST = Z(4);
	public static final int PLAYER_FALL_CEILING_DIST = Z(4);
	public static final int PLAYER_WADE_CEILING_DIST = Z(4);

	//
	// DIVE
	//

	public static final int PLAYER_DIVE_MAX_SPEED = (1700);
	public static final int PLAYER_DIVE_INC = (600);
	public static final int PLAYER_DIVE_BOB_AMT = (Z(8));

	public static final int PLAYER_DIVE_TIME = (12 * 120); // time before damage is taken
	public static final int PLAYER_DIVE_DAMAGE_AMOUNT = (-1); // amount of damage accessed
	public static final int PLAYER_DIVE_DAMAGE_TIME = (50); // time between damage accessment

	//
	// FLY
	//

	public static final int PLAYER_FLY_MAX_SPEED = (2560);
	public static final int PLAYER_FLY_INC = (1000);
	public static final int PLAYER_FLY_BOB_AMT = (Z(12));

	// Height from which Player will actually call DoPlayerBeginFall()
	public static final int PLAYER_FALL_HEIGHT = Z(28);
	public static final int PLAYER_FALL_DAMAGE_AMOUNT = (10);

	//
	// DEATH
	//

	// dead head height - used in DeathFall
	public static final int PLAYER_DEATH_HEIGHT = (Z(16));
	public static final int PLAYER_DEAD_HEAD_FLOORZ_OFFSET = (Z(7));

	public static final int PLAYER_NINJA_XREPEAT = (47);
	public static final int PLAYER_NINJA_YREPEAT = (33);

	public static final boolean TEST_UNDERWATER(PlayerStr pp) {
		return (TEST(sector[(pp).cursectnum].extra, SECTFX_UNDERWATER));
	}

	public static final int PLAYER_MIN_HEIGHT = (Z(20));
	public static final int PLAYER_CRAWL_WADE_DEPTH = (30);

	boolean NightVision = false;

	// public static final int PLAYER_TURN_SCALE (8)
	public static final int PLAYER_TURN_SCALE = (12);

	// the smaller the number the slower the going
	public static final int PLAYER_RUN_FRICTION = (50000);
	// public static final int PLAYER_RUN_FRICTION 0xcb00
	public static final int PLAYER_JUMP_FRICTION = PLAYER_RUN_FRICTION;
	public static final int PLAYER_FALL_FRICTION = PLAYER_RUN_FRICTION;

	public static final int PLAYER_WADE_FRICTION = PLAYER_RUN_FRICTION;
	public static final int PLAYER_FLY_FRICTION = (55808);

	public static final int PLAYER_CRAWL_FRICTION = (45056);
	public static final int PLAYER_SWIM_FRICTION = (49152);
	public static final int PLAYER_DIVE_FRICTION = (49152);

	// only for z direction climbing
	public static final int PLAYER_CLIMB_FRICTION = (45056);

	// public static final int BOAT_FRICTION 0xd000
	public static final int BOAT_FRICTION = 0xcb00;
	// public static final int TANK_FRICTION 0xcb00
	public static final int TANK_FRICTION = (53248);
	public static final int PLAYER_SLIDE_FRICTION = (53248);

	public static final int JUMP_STUFF = 4;

	public static final int PLAYER_JUMP_GRAV = 24;
	public static final int PLAYER_JUMP_AMT = (-650);
	public static final int PLAYER_CLIMB_JUMP_AMT = (-1100);
	public static final int MAX_JUMP_DURATION = 12;
	public static final int PlayerGravity = PLAYER_JUMP_GRAV;

	public enum PlayerStateGroup implements StateGroup {
		sg_PlayerNinjaRun(s_PlayerNinjaRun[0], s_PlayerNinjaRun[1], s_PlayerNinjaRun[2], s_PlayerNinjaRun[3],
				s_PlayerNinjaRun[4]),
		sg_PlayerNinjaStand(s_PlayerNinjaStand[0], s_PlayerNinjaStand[1], s_PlayerNinjaStand[2], s_PlayerNinjaStand[3],
				s_PlayerNinjaStand[4]),
		sg_PlayerNinjaJump(s_PlayerNinjaJump[0], s_PlayerNinjaJump[1], s_PlayerNinjaJump[2], s_PlayerNinjaJump[3],
				s_PlayerNinjaJump[4]),
		sg_PlayerNinjaFall(s_PlayerNinjaFall[0], s_PlayerNinjaFall[1], s_PlayerNinjaFall[2], s_PlayerNinjaFall[3],
				s_PlayerNinjaFall[4]),
		sg_PlayerNinjaClimb(s_PlayerNinjaClimb[0], s_PlayerNinjaClimb[1], s_PlayerNinjaClimb[2], s_PlayerNinjaClimb[3],
				s_PlayerNinjaClimb[4]),
		sg_PlayerNinjaCrawl(s_PlayerNinjaCrawl[0], s_PlayerNinjaCrawl[1], s_PlayerNinjaCrawl[2], s_PlayerNinjaCrawl[3],
				s_PlayerNinjaCrawl[4]),
		sg_PlayerNinjaSwim(s_PlayerNinjaSwim[0], s_PlayerNinjaSwim[1], s_PlayerNinjaSwim[2], s_PlayerNinjaSwim[3],
				s_PlayerNinjaSwim[4]),
		sg_PlayerHeadFly(s_PlayerHeadFly[0], s_PlayerHeadFly[1], s_PlayerHeadFly[2], s_PlayerHeadFly[3],
				s_PlayerHeadFly[4]),
		sg_PlayerHead(s_PlayerHead[0], s_PlayerHead[1], s_PlayerHead[2], s_PlayerHead[3], s_PlayerHead[4]),
		sg_PlayerHeadHurl(s_PlayerHeadHurl[0], s_PlayerHeadHurl[1], s_PlayerHeadHurl[2], s_PlayerHeadHurl[3],
				s_PlayerHeadHurl[4]),
		sg_PlayerDeath(s_PlayerDeath[0], s_PlayerDeath[1], s_PlayerDeath[2], s_PlayerDeath[3], s_PlayerDeath[4]),
		sg_PlayerNinjaSword(s_PlayerNinjaSword[0], s_PlayerNinjaSword[1], s_PlayerNinjaSword[2], s_PlayerNinjaSword[3],
				s_PlayerNinjaSword[4]),
		sg_PlayerNinjaPunch(s_PlayerNinjaPunch[0], s_PlayerNinjaPunch[1], s_PlayerNinjaPunch[2], s_PlayerNinjaPunch[3],
				s_PlayerNinjaPunch[4]),
		sg_PlayerNinjaFly(s_PlayerNinjaFly[0], s_PlayerNinjaFly[1], s_PlayerNinjaFly[2], s_PlayerNinjaFly[3],
				s_PlayerNinjaFly[4]);

		private final State[][] group;
		private int index = -1;

		PlayerStateGroup(State[]... states) {
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

	// Current Z position, adjust down to the floor, adjust to player height,
	// adjust for "bump head"
	public static final int PLAYER_STANDING_ROOM = Z(68);

	private static final Animator DoPlayerSpriteReset = new Animator() {
		@Override
		public boolean invoke(int spr) {
			DoPlayerSpriteReset(spr);
			return false;
		}
	};

	private static final Animator DoFootPrints = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoFootPrints(spr) != 0;
		}
	};

	public static final Animator QueueFloorBlood = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return QueueFloorBlood(spr) != 0;
		}
	};

	//////////////////////
	//
	// PLAYER SPECIFIC
	//
	//////////////////////

	public static final int PLAYER_NINJA_RATE = 14;

	private static State[][] s_PlayerNinjaRun = new State[][] {
			{ new State(PLAYER_NINJA_RUN_R0 + 0, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R0 + 1, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R0 + 1, 0 | SF_QUICK_CALL, DoFootPrints),
					new State(PLAYER_NINJA_RUN_R0 + 2, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R0 + 3, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R0 + 3, 0 | SF_QUICK_CALL, DoFootPrints), },
			{ new State(PLAYER_NINJA_RUN_R1 + 0, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R1 + 1, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R1 + 1, 0 | SF_QUICK_CALL, DoFootPrints),
					new State(PLAYER_NINJA_RUN_R1 + 2, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R1 + 3, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R1 + 3, 0 | SF_QUICK_CALL, DoFootPrints), },
			{ new State(PLAYER_NINJA_RUN_R2 + 0, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R2 + 1, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R2 + 1, 0 | SF_QUICK_CALL, DoFootPrints),
					new State(PLAYER_NINJA_RUN_R2 + 2, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R2 + 3, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R2 + 3, 0 | SF_QUICK_CALL, DoFootPrints), },
			{ new State(PLAYER_NINJA_RUN_R3 + 0, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R3 + 1, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R3 + 1, 0 | SF_QUICK_CALL, DoFootPrints),
					new State(PLAYER_NINJA_RUN_R3 + 2, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R3 + 3, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R3 + 3, 0 | SF_QUICK_CALL, DoFootPrints), },
			{ new State(PLAYER_NINJA_RUN_R4 + 0, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R4 + 1, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R4 + 1, 0 | SF_QUICK_CALL, DoFootPrints),
					new State(PLAYER_NINJA_RUN_R4 + 2, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R4 + 3, PLAYER_NINJA_RATE | SF_TIC_ADJUST, null),
					new State(PLAYER_NINJA_RUN_R4 + 3, 0 | SF_QUICK_CALL, DoFootPrints), } };

	//////////////////////
	//
	// PLAYER_NINJA STAND
	//
	//////////////////////

	public static final int PLAYER_NINJA_STAND_RATE = 10;

	private static State[][] s_PlayerNinjaStand = new State[][] {
			{ new State(PLAYER_NINJA_STAND_R0 + 0, PLAYER_NINJA_STAND_RATE, null).setNext(), },
			{ new State(PLAYER_NINJA_STAND_R1 + 0, PLAYER_NINJA_STAND_RATE, null).setNext(), },
			{ new State(PLAYER_NINJA_STAND_R2 + 0, PLAYER_NINJA_STAND_RATE, null).setNext(), },
			{ new State(PLAYER_NINJA_STAND_R3 + 0, PLAYER_NINJA_STAND_RATE, null).setNext(), },
			{ new State(PLAYER_NINJA_STAND_R4 + 0, PLAYER_NINJA_STAND_RATE, null).setNext(), }, };

	public static final int NINJA_STAR_RATE = 12;

	//////////////////////
	//
	// PLAYER_NINJA JUMP
	//
	//////////////////////

	public static final int PLAYER_NINJA_JUMP_RATE = 24;

	private static State[][] s_PlayerNinjaJump = new State[][] {
			{ new State(PLAYER_NINJA_JUMP_R0 + 0, PLAYER_NINJA_JUMP_RATE, null),
					new State(PLAYER_NINJA_JUMP_R0 + 1, PLAYER_NINJA_JUMP_RATE, null),
					new State(PLAYER_NINJA_JUMP_R0 + 2, PLAYER_NINJA_JUMP_RATE, null),
					new State(PLAYER_NINJA_JUMP_R0 + 3, PLAYER_NINJA_JUMP_RATE, null).setNext(), },
			{ new State(PLAYER_NINJA_JUMP_R1 + 0, PLAYER_NINJA_JUMP_RATE, null),
					new State(PLAYER_NINJA_JUMP_R1 + 1, PLAYER_NINJA_JUMP_RATE, null),
					new State(PLAYER_NINJA_JUMP_R1 + 2, PLAYER_NINJA_JUMP_RATE, null),
					new State(PLAYER_NINJA_JUMP_R1 + 3, PLAYER_NINJA_JUMP_RATE, null).setNext(), },
			{ new State(PLAYER_NINJA_JUMP_R2 + 0, PLAYER_NINJA_JUMP_RATE, null),
					new State(PLAYER_NINJA_JUMP_R2 + 1, PLAYER_NINJA_JUMP_RATE, null),
					new State(PLAYER_NINJA_JUMP_R2 + 2, PLAYER_NINJA_JUMP_RATE, null),
					new State(PLAYER_NINJA_JUMP_R2 + 3, PLAYER_NINJA_JUMP_RATE, null).setNext(), },
			{ new State(PLAYER_NINJA_JUMP_R3 + 0, PLAYER_NINJA_JUMP_RATE, null),
					new State(PLAYER_NINJA_JUMP_R3 + 1, PLAYER_NINJA_JUMP_RATE, null),
					new State(PLAYER_NINJA_JUMP_R3 + 2, PLAYER_NINJA_JUMP_RATE, null),
					new State(PLAYER_NINJA_JUMP_R3 + 3, PLAYER_NINJA_JUMP_RATE, null).setNext(), },
			{ new State(PLAYER_NINJA_JUMP_R4 + 0, PLAYER_NINJA_JUMP_RATE, null),
					new State(PLAYER_NINJA_JUMP_R4 + 1, PLAYER_NINJA_JUMP_RATE, null),
					new State(PLAYER_NINJA_JUMP_R4 + 2, PLAYER_NINJA_JUMP_RATE, null),
					new State(PLAYER_NINJA_JUMP_R4 + 3, PLAYER_NINJA_JUMP_RATE, null).setNext(), }, };

	//////////////////////
	//
	// PLAYER_NINJA FALL
	//
	//////////////////////

	public static final int PLAYER_NINJA_FALL_RATE = 16;

	private static State[][] s_PlayerNinjaFall = new State[][] {
			{ new State(PLAYER_NINJA_JUMP_R0 + 1, PLAYER_NINJA_FALL_RATE, null),
					new State(PLAYER_NINJA_JUMP_R0 + 2, PLAYER_NINJA_FALL_RATE, null).setNext(), },
			{ new State(PLAYER_NINJA_JUMP_R1 + 1, PLAYER_NINJA_FALL_RATE, null),
					new State(PLAYER_NINJA_JUMP_R1 + 2, PLAYER_NINJA_FALL_RATE, null).setNext(), },
			{ new State(PLAYER_NINJA_JUMP_R2 + 1, PLAYER_NINJA_FALL_RATE, null),
					new State(PLAYER_NINJA_JUMP_R2 + 2, PLAYER_NINJA_FALL_RATE, null).setNext(), },
			{ new State(PLAYER_NINJA_JUMP_R3 + 1, PLAYER_NINJA_FALL_RATE, null),
					new State(PLAYER_NINJA_JUMP_R3 + 2, PLAYER_NINJA_FALL_RATE, null).setNext(), },
			{ new State(PLAYER_NINJA_JUMP_R4 + 1, PLAYER_NINJA_FALL_RATE, null),
					new State(PLAYER_NINJA_JUMP_R4 + 2, PLAYER_NINJA_FALL_RATE, null).setNext(), }, };

	//////////////////////
	//
	// PLAYER_NINJA CLIMB
	//
	//////////////////////

	public static final int PLAYER_NINJA_CLIMB_RATE = 20;
	private static State[][] s_PlayerNinjaClimb = new State[][] {
			{ new State(PLAYER_NINJA_CLIMB_R0 + 0, PLAYER_NINJA_CLIMB_RATE, null),
					new State(PLAYER_NINJA_CLIMB_R0 + 1, PLAYER_NINJA_CLIMB_RATE, null),
					new State(PLAYER_NINJA_CLIMB_R0 + 2, PLAYER_NINJA_CLIMB_RATE, null),
					new State(PLAYER_NINJA_CLIMB_R0 + 3, PLAYER_NINJA_CLIMB_RATE, null), },
			{ new State(PLAYER_NINJA_CLIMB_R1 + 0, PLAYER_NINJA_CLIMB_RATE, null),
					new State(PLAYER_NINJA_CLIMB_R1 + 1, PLAYER_NINJA_CLIMB_RATE, null),
					new State(PLAYER_NINJA_CLIMB_R1 + 2, PLAYER_NINJA_CLIMB_RATE, null),
					new State(PLAYER_NINJA_CLIMB_R1 + 3, PLAYER_NINJA_CLIMB_RATE, null), },
			{ new State(PLAYER_NINJA_CLIMB_R2 + 0, PLAYER_NINJA_CLIMB_RATE, null),
					new State(PLAYER_NINJA_CLIMB_R2 + 1, PLAYER_NINJA_CLIMB_RATE, null),
					new State(PLAYER_NINJA_CLIMB_R2 + 2, PLAYER_NINJA_CLIMB_RATE, null),
					new State(PLAYER_NINJA_CLIMB_R2 + 3, PLAYER_NINJA_CLIMB_RATE, null), },
			{ new State(PLAYER_NINJA_CLIMB_R3 + 0, PLAYER_NINJA_CLIMB_RATE, null),
					new State(PLAYER_NINJA_CLIMB_R3 + 1, PLAYER_NINJA_CLIMB_RATE, null),
					new State(PLAYER_NINJA_CLIMB_R3 + 2, PLAYER_NINJA_CLIMB_RATE, null),
					new State(PLAYER_NINJA_CLIMB_R3 + 3, PLAYER_NINJA_CLIMB_RATE, null), },
			{ new State(PLAYER_NINJA_CLIMB_R4 + 0, PLAYER_NINJA_CLIMB_RATE, null),
					new State(PLAYER_NINJA_CLIMB_R4 + 1, PLAYER_NINJA_CLIMB_RATE, null),
					new State(PLAYER_NINJA_CLIMB_R4 + 2, PLAYER_NINJA_CLIMB_RATE, null),
					new State(PLAYER_NINJA_CLIMB_R4 + 3, PLAYER_NINJA_CLIMB_RATE, null), }, };

	//////////////////////
	//
	// PLAYER_NINJA CRAWL
	//
	//////////////////////

	public static final int PLAYER_NINJA_CRAWL_RATE = 14;
	private static State[][] s_PlayerNinjaCrawl = new State[][] {
			{ new State(PLAYER_NINJA_CRAWL_R0 + 0, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R0 + 1, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R0 + 1, 0 | SF_QUICK_CALL, DoFootPrints),
					new State(PLAYER_NINJA_CRAWL_R0 + 2, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R0 + 1, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R0 + 1, 0 | SF_QUICK_CALL, DoFootPrints), },
			{ new State(PLAYER_NINJA_CRAWL_R1 + 0, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R1 + 1, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R1 + 1, 0 | SF_QUICK_CALL, DoFootPrints),
					new State(PLAYER_NINJA_CRAWL_R1 + 2, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R1 + 1, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R1 + 1, 0 | SF_QUICK_CALL, DoFootPrints), },
			{ new State(PLAYER_NINJA_CRAWL_R2 + 0, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R2 + 1, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R2 + 1, 0 | SF_QUICK_CALL, DoFootPrints),
					new State(PLAYER_NINJA_CRAWL_R2 + 2, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R2 + 1, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R2 + 1, 0 | SF_QUICK_CALL, DoFootPrints), },
			{ new State(PLAYER_NINJA_CRAWL_R3 + 0, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R3 + 1, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R3 + 1, 0 | SF_QUICK_CALL, DoFootPrints),
					new State(PLAYER_NINJA_CRAWL_R3 + 2, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R3 + 1, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R3 + 1, 0 | SF_QUICK_CALL, DoFootPrints), },
			{ new State(PLAYER_NINJA_CRAWL_R4 + 0, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R4 + 1, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R4 + 1, 0 | SF_QUICK_CALL, DoFootPrints),
					new State(PLAYER_NINJA_CRAWL_R4 + 2, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R4 + 1, PLAYER_NINJA_CRAWL_RATE, null),
					new State(PLAYER_NINJA_CRAWL_R4 + 1, 0 | SF_QUICK_CALL, DoFootPrints), }, };

	//////////////////////
	//
	// PLAYER NINJA SWIM
	//
	//////////////////////

	public static final int PLAYER_NINJA_SWIM_RATE = 22;
	private static State[][] s_PlayerNinjaSwim = new State[][] {
			{ new State(PLAYER_NINJA_SWIM_R0 + 0, PLAYER_NINJA_SWIM_RATE, null),
					new State(PLAYER_NINJA_SWIM_R0 + 1, PLAYER_NINJA_SWIM_RATE, null),
					new State(PLAYER_NINJA_SWIM_R0 + 2, PLAYER_NINJA_SWIM_RATE, null),
					new State(PLAYER_NINJA_SWIM_R0 + 3, PLAYER_NINJA_SWIM_RATE, null), },
			{ new State(PLAYER_NINJA_SWIM_R1 + 0, PLAYER_NINJA_SWIM_RATE, null),
					new State(PLAYER_NINJA_SWIM_R1 + 1, PLAYER_NINJA_SWIM_RATE, null),
					new State(PLAYER_NINJA_SWIM_R1 + 2, PLAYER_NINJA_SWIM_RATE, null),
					new State(PLAYER_NINJA_SWIM_R1 + 3, PLAYER_NINJA_SWIM_RATE, null), },
			{ new State(PLAYER_NINJA_SWIM_R2 + 0, PLAYER_NINJA_SWIM_RATE, null),
					new State(PLAYER_NINJA_SWIM_R2 + 1, PLAYER_NINJA_SWIM_RATE, null),
					new State(PLAYER_NINJA_SWIM_R2 + 2, PLAYER_NINJA_SWIM_RATE, null),
					new State(PLAYER_NINJA_SWIM_R2 + 3, PLAYER_NINJA_SWIM_RATE, null), },
			{ new State(PLAYER_NINJA_SWIM_R3 + 0, PLAYER_NINJA_SWIM_RATE, null),
					new State(PLAYER_NINJA_SWIM_R3 + 1, PLAYER_NINJA_SWIM_RATE, null),
					new State(PLAYER_NINJA_SWIM_R3 + 2, PLAYER_NINJA_SWIM_RATE, null),
					new State(PLAYER_NINJA_SWIM_R3 + 3, PLAYER_NINJA_SWIM_RATE, null), },
			{ new State(PLAYER_NINJA_SWIM_R4 + 0, PLAYER_NINJA_SWIM_RATE, null),
					new State(PLAYER_NINJA_SWIM_R4 + 1, PLAYER_NINJA_SWIM_RATE, null),
					new State(PLAYER_NINJA_SWIM_R4 + 2, PLAYER_NINJA_SWIM_RATE, null),
					new State(PLAYER_NINJA_SWIM_R4 + 3, PLAYER_NINJA_SWIM_RATE, null), }, };

	public static final int NINJA_HeadHurl_RATE = 16;
	public static final int NINJA_Head_RATE = 16;
	public static final int NINJA_HeadFly = 1134;
	public static final int NINJA_HeadFly_RATE = 16;

	private static State[][] s_PlayerHeadFly = new State[][] {
			{ new State(NINJA_HeadFly + 0, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 1, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 2, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 3, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 4, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 5, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 6, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 7, NINJA_HeadFly_RATE, null), },
			{ new State(NINJA_HeadFly + 0, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 1, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 2, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 3, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 4, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 5, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 6, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 7, NINJA_HeadFly_RATE, null), },
			{ new State(NINJA_HeadFly + 0, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 1, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 2, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 3, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 4, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 5, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 6, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 7, NINJA_HeadFly_RATE, null), },
			{ new State(NINJA_HeadFly + 0, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 1, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 2, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 3, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 4, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 5, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 6, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 7, NINJA_HeadFly_RATE, null), },
			{ new State(NINJA_HeadFly + 0, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 1, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 2, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 3, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 4, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 5, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 6, NINJA_HeadFly_RATE, null),
					new State(NINJA_HeadFly + 7, NINJA_HeadFly_RATE, null), }, };

	private static State[][] s_PlayerHead = new State[][] {
			{ new State(NINJA_Head_R0 + 0, NINJA_Head_RATE, null).setNext(), },
			{ new State(NINJA_Head_R1 + 0, NINJA_Head_RATE, null).setNext(), },
			{ new State(NINJA_Head_R2 + 0, NINJA_Head_RATE, null).setNext(), },
			{ new State(NINJA_Head_R3 + 0, NINJA_Head_RATE, null).setNext(), },
			{ new State(NINJA_Head_R4 + 0, NINJA_Head_RATE, null).setNext(), }, };

	public static final int NINJA_HeadHurl_FRAMES = 1;
	public static final int NINJA_HeadHurl_R0 = 1147;
	public static final int NINJA_HeadHurl_R1 = NINJA_HeadHurl_R0 + (NINJA_HeadHurl_FRAMES * 1);
	public static final int NINJA_HeadHurl_R2 = NINJA_HeadHurl_R0 + (NINJA_HeadHurl_FRAMES * 2);
	public static final int NINJA_HeadHurl_R3 = NINJA_HeadHurl_R0 + (NINJA_HeadHurl_FRAMES * 3);
	public static final int NINJA_HeadHurl_R4 = NINJA_HeadHurl_R0 + (NINJA_HeadHurl_FRAMES * 4);

	private static State[][] s_PlayerHeadHurl = new State[][] {
			{ new State(NINJA_HeadHurl_R0 + 0, NINJA_HeadHurl_RATE, null).setNext(), },
			{ new State(NINJA_HeadHurl_R1 + 0, NINJA_HeadHurl_RATE, null).setNext(), },
			{ new State(NINJA_HeadHurl_R2 + 0, NINJA_HeadHurl_RATE, null).setNext(), },
			{ new State(NINJA_HeadHurl_R3 + 0, NINJA_HeadHurl_RATE, null).setNext(), },
			{ new State(NINJA_HeadHurl_R4 + 0, NINJA_HeadHurl_RATE, null).setNext(), }, };

	public static final int NINJA_DIE_RATE = 22;

	private static State[][] s_PlayerDeath = new State[][] {
			{ new State(PLAYER_NINJA_DIE + 0, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 1, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 2, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 3, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 4, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 5, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 6, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 7, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 8, 0 | SF_QUICK_CALL, QueueFloorBlood),
					new State(PLAYER_NINJA_DIE + 8, NINJA_DIE_RATE, null).setNext(), },
			{ new State(PLAYER_NINJA_DIE + 0, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 1, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 2, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 3, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 4, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 5, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 6, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 7, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 8, 0 | SF_QUICK_CALL, QueueFloorBlood),
					new State(PLAYER_NINJA_DIE + 8, NINJA_DIE_RATE, null).setNext(), },
			{ new State(PLAYER_NINJA_DIE + 0, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 1, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 2, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 3, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 4, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 5, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 6, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 7, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 8, 0 | SF_QUICK_CALL, QueueFloorBlood),
					new State(PLAYER_NINJA_DIE + 8, NINJA_DIE_RATE, null).setNext(), },
			{ new State(PLAYER_NINJA_DIE + 0, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 1, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 2, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 3, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 4, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 5, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 6, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 7, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 8, 0 | SF_QUICK_CALL, QueueFloorBlood),
					new State(PLAYER_NINJA_DIE + 8, NINJA_DIE_RATE, null).setNext(), },
			{ new State(PLAYER_NINJA_DIE + 0, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 1, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 2, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 3, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 4, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 5, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 6, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 7, NINJA_DIE_RATE, null),
					new State(PLAYER_NINJA_DIE + 8, 0 | SF_QUICK_CALL, QueueFloorBlood),
					new State(PLAYER_NINJA_DIE + 8, NINJA_DIE_RATE, null).setNext(), }, };

	//////////////////////
	//
	// PLAYER NINJA SWORD
	//
	//////////////////////

	private static final int PLAYER_NINJA_SWORD_RATE = 12;

	private static State[][] s_PlayerNinjaSword = new State[][] { { // 0 - Front view
			new State(PLAYER_NINJA_SWORD_R0 + 0, PLAYER_NINJA_SWORD_RATE, null),
			new State(PLAYER_NINJA_SWORD_R0 + 1, PLAYER_NINJA_SWORD_RATE, null),
			new State(PLAYER_NINJA_SWORD_R0 + 2, PLAYER_NINJA_SWORD_RATE, null),
			new State(PLAYER_NINJA_SWORD_R0 + 2, PLAYER_NINJA_SWORD_RATE | SF_PLAYER_FUNC, DoPlayerSpriteReset) },

			{ // 1
					new State(PLAYER_NINJA_SWORD_R1 + 0, PLAYER_NINJA_SWORD_RATE, null),
					new State(PLAYER_NINJA_SWORD_R1 + 1, PLAYER_NINJA_SWORD_RATE, null),
					new State(PLAYER_NINJA_SWORD_R1 + 2, PLAYER_NINJA_SWORD_RATE, null),
					new State(PLAYER_NINJA_SWORD_R1 + 2, PLAYER_NINJA_SWORD_RATE | SF_PLAYER_FUNC,
							DoPlayerSpriteReset), },

			{ // 2
					new State(PLAYER_NINJA_SWORD_R2 + 0, PLAYER_NINJA_SWORD_RATE, null),
					new State(PLAYER_NINJA_SWORD_R2 + 1, PLAYER_NINJA_SWORD_RATE, null),
					new State(PLAYER_NINJA_SWORD_R2 + 2, PLAYER_NINJA_SWORD_RATE, null),
					new State(PLAYER_NINJA_SWORD_R2 + 2, PLAYER_NINJA_SWORD_RATE | SF_PLAYER_FUNC,
							DoPlayerSpriteReset), },

			{ // 3
					new State(PLAYER_NINJA_SWORD_R3 + 0, PLAYER_NINJA_SWORD_RATE, null),
					new State(PLAYER_NINJA_SWORD_R3 + 1, PLAYER_NINJA_SWORD_RATE, null),
					new State(PLAYER_NINJA_SWORD_R3 + 2, PLAYER_NINJA_SWORD_RATE, null),
					new State(PLAYER_NINJA_SWORD_R3 + 2, PLAYER_NINJA_SWORD_RATE | SF_PLAYER_FUNC,
							DoPlayerSpriteReset), },

			{ // 4
					new State(PLAYER_NINJA_SWORD_R4 + 0, PLAYER_NINJA_SWORD_RATE, null),
					new State(PLAYER_NINJA_SWORD_R4 + 1, PLAYER_NINJA_SWORD_RATE, null),
					new State(PLAYER_NINJA_SWORD_R4 + 2, PLAYER_NINJA_SWORD_RATE, null),
					new State(PLAYER_NINJA_SWORD_R4 + 2, PLAYER_NINJA_SWORD_RATE | SF_PLAYER_FUNC,
							DoPlayerSpriteReset), }, };

	//////////////////////
	//
	// PLAYER NINJA PUNCH
	//
	//////////////////////

	private static final int PLAYER_NINJA_PUNCH_RATE = 15;

	private static State[][] s_PlayerNinjaPunch = new State[][] { { // 0 - Front view
			new State(PLAYER_NINJA_PUNCH_R0 + 0, PLAYER_NINJA_PUNCH_RATE, null),
			new State(PLAYER_NINJA_PUNCH_R0 + 1, PLAYER_NINJA_PUNCH_RATE, null),
			new State(PLAYER_NINJA_PUNCH_R0 + 1, PLAYER_NINJA_PUNCH_RATE | SF_PLAYER_FUNC, DoPlayerSpriteReset)
					.setNext(), },

			{ // 1
					new State(PLAYER_NINJA_PUNCH_R1 + 0, PLAYER_NINJA_PUNCH_RATE, null),
					new State(PLAYER_NINJA_PUNCH_R1 + 1, PLAYER_NINJA_PUNCH_RATE, null),
					new State(PLAYER_NINJA_PUNCH_R1 + 1, PLAYER_NINJA_PUNCH_RATE | SF_PLAYER_FUNC, DoPlayerSpriteReset)
							.setNext(), },

			{ // 2
					new State(PLAYER_NINJA_PUNCH_R2 + 0, PLAYER_NINJA_PUNCH_RATE, null),
					new State(PLAYER_NINJA_PUNCH_R2 + 1, PLAYER_NINJA_PUNCH_RATE, null),
					new State(PLAYER_NINJA_PUNCH_R2 + 1, PLAYER_NINJA_PUNCH_RATE | SF_PLAYER_FUNC, DoPlayerSpriteReset)
							.setNext(), },

			{ // 3
					new State(PLAYER_NINJA_PUNCH_R3 + 0, PLAYER_NINJA_PUNCH_RATE, null),
					new State(PLAYER_NINJA_PUNCH_R3 + 1, PLAYER_NINJA_PUNCH_RATE, null),
					new State(PLAYER_NINJA_PUNCH_R3 + 1, PLAYER_NINJA_PUNCH_RATE | SF_PLAYER_FUNC, DoPlayerSpriteReset)
							.setNext(), },

			{ // 4
					new State(PLAYER_NINJA_PUNCH_R4 + 0, PLAYER_NINJA_PUNCH_RATE, null),
					new State(PLAYER_NINJA_PUNCH_R4 + 1, PLAYER_NINJA_PUNCH_RATE, null),
					new State(PLAYER_NINJA_PUNCH_R4 + 1, PLAYER_NINJA_PUNCH_RATE | SF_PLAYER_FUNC, DoPlayerSpriteReset)
							.setNext(), }, };

	//////////////////////
	//
	// PLAYER NINJA FLY
	//
	//////////////////////

	public static final int PLAYER_NINJA_FLY_RATE = 15;
	public static final int PLAYER_NINJA_FLY_R0 = 1200;
	public static final int PLAYER_NINJA_FLY_R1 = 1200;
	public static final int PLAYER_NINJA_FLY_R2 = 1200;
	public static final int PLAYER_NINJA_FLY_R3 = 1200;
	public static final int PLAYER_NINJA_FLY_R4 = 1200;

	private static State[][] s_PlayerNinjaFly = new State[][] {
			{ new State(PLAYER_NINJA_FLY_R0 + 0, PLAYER_NINJA_FLY_RATE, null).setNext() },
			{ new State(PLAYER_NINJA_FLY_R1 + 0, PLAYER_NINJA_FLY_RATE, null).setNext() },
			{ new State(PLAYER_NINJA_FLY_R2 + 0, PLAYER_NINJA_FLY_RATE, null).setNext() },
			{ new State(PLAYER_NINJA_FLY_R3 + 0, PLAYER_NINJA_FLY_RATE, null).setNext() },
			{ new State(PLAYER_NINJA_FLY_R4 + 0, PLAYER_NINJA_FLY_RATE, null).setNext() }, };

	public static void InitPlayerStates() {
		for (PlayerStateGroup sg : PlayerStateGroup.values()) {
			for (int rot = 0; rot < 5; rot++) {
				State.InitState(sg.group[rot]);
			}
		}
	}

	/////////////////////////////////////////////////////////////////////////////

	public static void DoPlayerSpriteThrow(PlayerStr pp) {
		if (!TEST(pp.Flags, PF_DIVING | PF_FLYING | PF_CRAWLING)) {
			if (pp.CurWpn == pp.Wpn[WPN_SWORD] && pUser[pp.PlayerSprite].Rot != PlayerStateGroup.sg_PlayerNinjaSword)
				NewStateGroup(pp.PlayerSprite, PlayerStateGroup.sg_PlayerNinjaSword);
			else
				NewStateGroup(pp.PlayerSprite, PlayerStateGroup.sg_PlayerNinjaPunch);
		}
	}

	public static void DoPlayerSpriteReset(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (u.PlayerP == -1)
			return;

		PlayerStr pp = Player[u.PlayerP];

		// need to figure out what frames to put sprite into
		if (pp.DoPlayerAction == Player_Action_Func.DoPlayerCrawl)
			NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Crawl);
		else {
			if (TEST(pp.Flags, PF_PLAYER_MOVED))
				NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Run);
			else
				NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Stand);
		}
	}

	public static void SetVisHigh() {
//	    visibility = NormalVisibility>>1;
		return;
	}

	public static void SetVisNorm() {
//	    visibility = NormalVisibility;
		return;
	}

	public static final Panel_Sprite_Func pSetVisNorm = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			SetVisNorm();
		}
	};

	public static short GetDeltaAngle(int ang1, int ang2) {
		// Look at the smaller angle if > 1024 (180 degrees)
		if (klabs(ang1 - ang2) > 1024) {
			if (ang1 <= 1024)
				ang1 += 2048;

			if (ang2 <= 1024)
				ang2 += 2048;
		}
		return (short) (ang1 - ang2);
	}

	public static Target_Sort[] TargetSort = new Target_Sort[MAX_TARGET_SORT];
	public static short TargetSortCount;

	public static final int PICK_DIST = 40000;

	public static int DoPickTarget(int spnum, int max_delta_ang, int skip_targets) {

		short i, nexti, angle2, delta_ang;
		int dist, zh;
		SPRITE ep;
		SPRITE sp = sprite[spnum];
		USER eu;
		USER u = pUser[spnum];
		int ezh, ezhl, ezhm;
		short ndx;
		Target_Sort ts;
		int ang_weight, dist_weight;

		// !JIM! Watch out for max_delta_ang of zero!
		if (max_delta_ang == 0)
			max_delta_ang = 1;

		TargetSortCount = 0;
		if (TargetSort[0] == null)
			TargetSort[0] = new Target_Sort();
		TargetSort[0].sprite_num = -1;

		for (int shp = 0; shp < StatDamageList.length; shp++) {
			for (i = headspritestat[StatDamageList[shp]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				ep = sprite[i];
				eu = pUser[i];

				// don't pick yourself
				if (i == spnum)
					continue;

				if (skip_targets != 2) // Used for spriteinfo mode
				{
					if (skip_targets != 0 && TEST(eu.Flags, SPR_TARGETED))
						continue;

					// don't pick a dead player
					if (eu.PlayerP != -1 && TEST(Player[eu.PlayerP].Flags, PF_DEAD))
						continue;
				}

				// Only look at closest ones
				if ((dist = FindDistance3D(sp.x - ep.x, sp.y - ep.y, (sp.z - ep.z) >> 4)) > PICK_DIST)
					continue;

				if (skip_targets != 2) // Used for spriteinfo mode
				{
					// don't set off mine
					if (!TEST(ep.extra, SPRX_PLAYER_OR_ENEMY))
						continue;
				}

				// Get the angle to the player
				angle2 = NORM_ANGLE(engine.getangle(ep.x - sp.x, ep.y - sp.y));

				// Get the angle difference
				// delta_ang = labs(pp.pang - angle2);

				delta_ang = (short) klabs(GetDeltaAngle(sp.ang, angle2));

				// If delta_ang not in the range skip this one
				if (delta_ang > max_delta_ang)
					continue;

				if (u != null && u.PlayerP != -1)
					zh = Player[u.PlayerP].posz;
				else
					zh = SPRITEp_TOS(sp) + DIV4(SPRITEp_SIZE_Z(sp));

				ezh = SPRITEp_TOS(ep) + DIV4(SPRITEp_SIZE_Z(ep));
				ezhm = SPRITEp_TOS(ep) + DIV2(SPRITEp_SIZE_Z(ep));
				ezhl = SPRITEp_BOS(ep) - DIV4(SPRITEp_SIZE_Z(ep));

				// If you can't see 'em you can't shoot 'em
				if (!FAFcansee(sp.x, sp.y, zh, sp.sectnum, ep.x, ep.y, ezh, ep.sectnum)
						&& !FAFcansee(sp.x, sp.y, zh, sp.sectnum, ep.x, ep.y, ezhm, ep.sectnum)
						&& !FAFcansee(sp.x, sp.y, zh, sp.sectnum, ep.x, ep.y, ezhl, ep.sectnum))
					continue;

				// get ndx - there is only room for 15
				if (TargetSortCount > TargetSort.length - 1) {
					for (ndx = 0; ndx < TargetSort.length; ndx++) {
						if (dist < TargetSort[ndx].dist)
							break;
					}

					if (ndx == TargetSort.length)
						continue;
				} else {
					ndx = TargetSortCount;
				}

				if (TargetSort[ndx] == null)
					TargetSort[ndx] = new Target_Sort();
				ts = TargetSort[ndx];
				ts.sprite_num = i;
				ts.dang = delta_ang;
				ts.dist = dist;
				// gives a value between 0 and 65535
				ang_weight = ((max_delta_ang - ts.dang) << 16) / max_delta_ang;
				// gives a value between 0 and 65535
				dist_weight = ((DIV2(PICK_DIST) - DIV2(ts.dist)) << 16) / DIV2(PICK_DIST);
				// weighted average
				ts.weight = (ang_weight + dist_weight * 4) / 5;

				TargetSortCount++;
				if (TargetSortCount >= TargetSort.length)
					TargetSortCount = (short) TargetSort.length;
			}
		}

		if (TargetSortCount > 1)
			Arrays.sort(TargetSort, 0, TargetSortCount); // qsort(TargetSort, TargetSortCount);

		return (TargetSort[0].sprite_num);
	}

	public static void DoPlayerResetMovement(PlayerStr pp) {
		pp.xvect = pp.oxvect = 0;
		pp.yvect = pp.oxvect = 0;
		pp.slide_xvect = 0;
		pp.slide_yvect = 0;
		pp.drive_angvel = 0;
		pp.drive_oangvel = 0;
		pp.Flags &= ~(PF_PLAYER_MOVED);
	}

	public static void DoPlayerTeleportPause(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];

		// set this so we don't get stuck in teleporting loop
		pp.lastcursectnum = pp.cursectnum;

		if ((u.WaitTics -= synctics) <= 0) {
			pp.Flags2 &= ~(PF2_TELEPORTED);
			DoPlayerResetMovement(pp);
			DoPlayerBeginRun(pp);
			return;
		}
	}

	public static void DoPlayerTeleportToSprite(PlayerStr pp, SPRITE sp) {
		pp.pang = pp.oang = sp.ang;
		pp.posx = pp.oposx = pp.oldposx = sp.x;
		pp.posy = pp.oposy = pp.oldposy = sp.y;
		pp.posz = pp.oposz = sp.z - PLAYER_HEIGHT;

		pp.cursectnum = COVERupdatesector(pp.posx, pp.posy, pp.cursectnum);
		pp.Flags2 |= (PF2_TELEPORTED);
	}

	public static void DoPlayerTeleportToOffset(PlayerStr pp) {

		pp.oposx = pp.oldposx = pp.posx;
		pp.oposy = pp.oldposy = pp.posy;

		pp.cursectnum = COVERupdatesector(pp.posx, pp.posy, pp.cursectnum);
		// pp.lastcursectnum = pp.cursectnum;
		pp.Flags2 |= (PF2_TELEPORTED);
	}

	public static void DoSpawnTeleporterEffect(SPRITE sp) {
		int effect;
		int nx, ny;
		SPRITE ep;

		nx = MOVEx(512, sp.ang);
		ny = MOVEy(512, sp.ang);

		nx += sp.x;
		ny += sp.y;

		effect = SpawnSprite(STAT_MISSILE, 0, s_TeleportEffect[0], sp.sectnum, nx, ny, SPRITEp_TOS(sp) + Z(16), sp.ang,
				0);

		ep = sprite[effect];

		engine.setspritez(effect, ep.x, ep.y, ep.z);

		ep.shade = -40;
		ep.xrepeat = ep.yrepeat = 42;
		ep.cstat |= (CSTAT_SPRITE_YCENTER);
		ep.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		ep.cstat |= (CSTAT_SPRITE_WALL);
	}

	public static void DoSpawnTeleporterEffectPlace(SPRITE sp) {
		int effect;
		SPRITE ep;

		effect = SpawnSprite(STAT_MISSILE, 0, s_TeleportEffect[0], sp.sectnum, sp.x, sp.y, SPRITEp_TOS(sp) + Z(16),
				sp.ang, 0);

		ep = sprite[effect];

		engine.setspritez(effect, ep.x, ep.y, ep.z);

		ep.shade = -40;
		ep.xrepeat = ep.yrepeat = 42;
		ep.cstat |= (CSTAT_SPRITE_YCENTER);
		ep.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		ep.cstat |= (CSTAT_SPRITE_WALL);
	}

	public static void DoPlayerWarpTeleporter(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		SPRITE sp = pp.getSprite();
		short pnum;
		SPRITE sp_warp;

		if ((sp_warp = WarpM(pp.posx, pp.posy, pp.posz, pp.cursectnum)) == null)
			return;

		pp.posx = warp.x;
		pp.posy = warp.y;
		pp.posz = warp.z;
		pp.cursectnum = warp.sectnum;

		switch (SP_TAG3(sp_warp)) {
		case 1:
			DoPlayerTeleportToOffset(pp);
			UpdatePlayerSprite(pp);
			break;
		default:
			DoPlayerTeleportToSprite(pp, sp_warp);

			PlaySound(DIGI_TELEPORT, pp, v3df_none);

			DoPlayerResetMovement(pp);

			u.WaitTics = 30;
			DoPlayerBeginRun(pp);
			pp.DoPlayerAction = Player_Action_Func.DoPlayerTeleportPause;

			NewStateGroup(pp.PlayerSprite, pUser[pp.PlayerSprite].ActorActionSet.Stand);

			UpdatePlayerSprite(pp);
			DoSpawnTeleporterEffect(sp);

			for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
				if (Player[pnum] != pp) {
					PlayerStr npp = Player[pnum];

					// if someone already standing there
					if (npp.cursectnum == pp.cursectnum) {
						PlayerUpdateHealth(npp, -pUser[npp.PlayerSprite].Health); // Make sure he dies!
						// telefraged by teleporting player
						// PlayerCheckDeath(npp, npp.PlayerSprite);
						PlayerCheckDeath(npp, pp.PlayerSprite);
					}
				}
			}

			break;
		}

		u.ox = sp.x;
		u.oy = sp.y;
		u.oz = sp.z;
	}

	public static void DoPlayerSetWadeDepth(PlayerStr pp) {
		SECTOR sectp;

		pp.WadeDepth = 0;

		if (pp.lo_sectp != -1)
			sectp = sector[pp.lo_sectp];
		else
			return;

		if (TEST(sectp.extra, SECTFX_SINK)) {
			// make sure your even in the water
			if (pp.posz + PLAYER_HEIGHT > sectp.floorz - Z(SectUser[pp.lo_sectp].depth))
				pp.WadeDepth = SectUser[pp.lo_sectp].depth;
		}
	}

	public static void DoPlayerHeight(PlayerStr pp) {
		int diff = pp.posz - (pp.loz - PLAYER_HEIGHT);

		pp.posz = pp.posz - (DIV4(diff) + DIV8(diff));
	}

	public static void DoPlayerJumpHeight(PlayerStr pp) {
		if (pp.lo_sectp != -1 && TEST(sector[pp.lo_sectp].extra, SECTFX_DYNAMIC_AREA)) {
			if (pp.posz + PLAYER_HEIGHT > pp.loz) {
				pp.posz = pp.loz - PLAYER_HEIGHT;
				DoPlayerBeginRun(pp);
			}
		}
	}

	public static void DoPlayerCrawlHeight(PlayerStr pp) {
		int diff = pp.posz - (pp.loz - PLAYER_CRAWL_HEIGHT);
		pp.posz = pp.posz - (DIV4(diff) + DIV8(diff));
	}

	private static final int TURN_SHIFT = 2;

	public static void DoPlayerTurn(PlayerStr pp) {
		if (!TEST(pp.Flags, PF_TURN_180)) {
			if (TEST_SYNC_KEY(pp, SK_TURN_180)) {
				if (FLAG_KEY_PRESSED(pp, SK_TURN_180)) {
					short delta_ang;

					FLAG_KEY_RELEASE(pp, SK_TURN_180);

					pp.turn180_target = NORM_ANGLE(pp.getAnglei() + 1024);

					// make the first turn in the clockwise direction
					// the rest will follow
					delta_ang = GetDeltaAngle(pp.turn180_target, pp.getAnglei());
					pp.pang = NORM_ANGLE(pp.getAnglei() + (klabs(delta_ang) >> TURN_SHIFT));

					pp.Flags |= (PF_TURN_180);
				}
			} else {
				FLAG_KEY_RESET(pp, SK_TURN_180);
			}
		}

		if (TEST(pp.Flags, PF_TURN_180)) {
			short delta_ang = GetDeltaAngle(pp.turn180_target, pp.getAnglei());
			if(!isOriginal()) {
				pp.pang += (delta_ang >> TURN_SHIFT);
				pp.pang = BClampAngle(pp.pang);
			} else
				pp.pang = NORM_ANGLE(pp.getAnglei() + (delta_ang >> TURN_SHIFT));

			sprite[pp.PlayerSprite].ang = pp.getAnglei();
			if (!Prediction) {
				if (pp.PlayerUnderSprite >= 0)
					sprite[pp.PlayerUnderSprite].ang = pp.getAnglei();
			}

			// get new delta to see how close we are
			delta_ang = GetDeltaAngle(pp.turn180_target, pp.getAnglei());

			if (klabs(delta_ang) < (3 << TURN_SHIFT)) {
				pp.pang = pp.turn180_target;
				pp.Flags &= ~(PF_TURN_180);
			} else
				return;
		}

		float angvel = (pp.input.angvel * PLAYER_TURN_SCALE);

		if (angvel != 0) {
			// running is not handled here now
			if(!isOriginal()) {
				angvel += angvel / 4.0f;

				pp.pang += (angvel * synctics) / 32.0f;
				pp.pang = BClampAngle(pp.pang);
			} else {
				angvel += DIV4((int) angvel);

				pp.pang += DIV32((int) angvel * synctics);
				pp.pang = NORM_ANGLE(pp.getAnglei());
			}

			// update players sprite angle
			// NOTE: It's also updated in UpdatePlayerSprite, but needs to be
			// here to cover
			// all cases.
			sprite[pp.PlayerSprite].ang = pp.getAnglei();
			if (!Prediction) {
				if (pp.PlayerUnderSprite >= 0)
					sprite[pp.PlayerUnderSprite].ang = pp.getAnglei();
			}

		}
	}

	public static void DoPlayerTurnBoat(PlayerStr pp) {
		float angvel;
		int angslide;
		Sector_Object sop = SectorObject[pp.sop];

		if (sop.drive_angspeed != 0) {
			pp.drive_oangvel = pp.drive_angvel;
			pp.drive_angvel = mulscale((int)pp.input.angvel, sop.drive_angspeed, 16);

			angslide = sop.drive_angslide;
			pp.drive_angvel = (int) ((pp.drive_angvel + (pp.drive_oangvel * (angslide - 1))) / angslide);

			angvel = pp.drive_angvel;
		} else {
			angvel = pp.input.angvel * PLAYER_TURN_SCALE;
			if(!isOriginal()) {
				angvel += angvel - angvel / 4.0f;
				angvel = (angvel * synctics) / 32.0f;
			} else {
				angvel += angvel - DIV4((int)angvel);
				angvel = DIV32((int)angvel * synctics);
			}
		}

		if (angvel != 0) {
			if(!isOriginal()) {
				pp.pang += angvel;
				pp.pang = BClampAngle(pp.pang);
			} else pp.pang = NORM_ANGLE((int)(pp.getAnglei() + angvel));

			sprite[pp.PlayerSprite].ang = pp.getAnglei();
		}
	}

	public static void DoPlayerTurnTank(PlayerStr pp, int z, int floor_dist) {
		float angvel;
		Sector_Object sop = SectorObject[pp.sop];

		if (sop.drive_angspeed != 0) {
			long angslide;

			pp.drive_oangvel = pp.drive_angvel;
			pp.drive_angvel = mulscale((int)pp.input.angvel, sop.drive_angspeed, 16);

			angslide = sop.drive_angslide;
			pp.drive_angvel = (int) ((pp.drive_angvel + (pp.drive_oangvel * (angslide - 1))) / angslide);

			angvel = pp.drive_angvel;
		} else {
			angvel = pp.input.angvel * synctics / 8.0f;
		}

		if (angvel != 0) {
			if (MultiClipTurn(pp, NORM_ANGLE((int) (pp.getAnglef() + angvel)), z, floor_dist)) {
				if(!isOriginal()) {
					pp.pang += angvel;
					pp.pang = BClampAngle(pp.pang);
				} else pp.pang = NORM_ANGLE((int)(pp.getAnglei() + angvel));
				sprite[pp.PlayerSprite].ang = pp.getAnglei();
			}
		}
	}

	public static void DoPlayerTurnTankRect(PlayerStr pp, int[] x, int[] y, int[] ox, int[] oy) {
		float angvel;
		Sector_Object sop = SectorObject[pp.sop];

		if (sop.drive_angspeed != 0) {
			int angslide;

			pp.drive_oangvel = pp.drive_angvel;
			pp.drive_angvel = mulscale((int)pp.input.angvel, sop.drive_angspeed, 16);

			angslide = sop.drive_angslide;
			pp.drive_angvel = (pp.drive_angvel + (pp.drive_oangvel * (angslide - 1))) / angslide;

			angvel = pp.drive_angvel;
		} else {
			angvel = (pp.input.angvel * synctics) / 8.0f;
		}

		if (angvel != 0) {
			if (RectClipTurn(pp, NORM_ANGLE((int) (pp.getAnglef() + angvel)), x, y, ox, oy)) {
				if(!isOriginal()) {
					pp.pang += angvel;
					pp.pang = BClampAngle(pp.pang);
				} else pp.pang = NORM_ANGLE((int)(pp.getAnglei() + angvel));
				sprite[pp.PlayerSprite].ang = pp.getAnglei();
			}
		}
	}

	public static void DoPlayerTurnTurret(PlayerStr pp) {
		float angvel;
		short diff;
		Sector_Object sop = SectorObject[pp.sop];
		Input last_input;
		int fifo_ndx;

		if (!Prediction) {
			// this code looks at the fifo to get the last value for comparison

			fifo_ndx = (game.pNet.gNetFifoTail - 2) & (MOVEFIFOSIZ - 1);
			last_input = (Input) game.pNet.gFifoInput[fifo_ndx][pp.pnum];

			if (pp.input.angvel != 0 && last_input.angvel == 0)
				PlaySOsound(sop.mid_sector, SO_DRIVE_SOUND);
			else if (pp.input.angvel == 0 && last_input.angvel != 0)
				PlaySOsound(sop.mid_sector, SO_IDLE_SOUND);
		}

		if (sop.drive_angspeed != 0) {
			int angslide;

			pp.drive_oangvel = pp.drive_angvel;
			pp.drive_angvel = pp.input.angvel * sop.drive_angspeed / 65536.0f; //mulscale((int)pp.input.angvel, sop.drive_angspeed, 16);

			angslide = sop.drive_angslide;
			pp.drive_angvel = (pp.drive_angvel + (pp.drive_oangvel * (angslide - 1))) / angslide;

			angvel = pp.drive_angvel;
		} else {
			angvel = (pp.input.angvel * synctics) / 4.0f;
		}

		if (angvel != 0) {
			float new_ang = pp.getAnglef() + angvel;
			if(isOriginal())
				new_ang = NORM_ANGLE((int) (pp.getAnglei() + angvel));
			else new_ang= BClampAngle(new_ang);

			if (sop.limit_ang_center >= 0) {
				diff = GetDeltaAngle((int) new_ang, sop.limit_ang_center);

				if (klabs(diff) >= sop.limit_ang_delta) {
					if (diff < 0)
						new_ang = (short) (sop.limit_ang_center - sop.limit_ang_delta);
					else
						new_ang = (short) (sop.limit_ang_center + sop.limit_ang_delta);

				}
			}

			pp.pang = new_ang;
			sprite[pp.PlayerSprite].ang = pp.getAnglei();
		}
	}

	public static void SlipSlope(PlayerStr pp) {
		if (!isValidSector(pp.cursectnum))
			return;
		short wallptr = sector[pp.cursectnum].wallptr;
		short ang;
		Sect_User sectu = SectUser[pp.cursectnum];

		if (sectu == null || !TEST(sectu.flags, SECTFU_SLIDE_SECTOR)
				|| !TEST(sector[pp.cursectnum].floorstat, FLOOR_STAT_SLOPE))
			return;

		ang = engine.getangle(wall[wall[wallptr].point2].x - wall[wallptr].x,
				wall[wall[wallptr].point2].y - wall[wallptr].y);

		ang = NORM_ANGLE(ang + 512);

		pp.xvect += mulscale(sintable[NORM_ANGLE(ang + 512)], sector[pp.cursectnum].floorheinum, sectu.speed);
		pp.yvect += mulscale(sintable[ang], sector[pp.cursectnum].floorheinum, sectu.speed);
	}

	private static void PlayerAutoLook(PlayerStr pp) {
		if (!isValidSector(pp.cursectnum))
			return;

		if (!TEST(pp.Flags, PF_FLYING | PF_SWIMMING | PF_DIVING | PF_CLIMBING | PF_JUMPING | PF_FALLING)) {
			if (TEST(sector[pp.cursectnum].floorstat, FLOOR_STAT_SLOPE)) // If the floor is sloped
			{
				int x, y;
				// Get a point, 512 units ahead of player's position
				if(isOriginal()) {
					x = pp.posx + (sintable[(pp.getAnglei() + 512) & 2047] >> 5);
					y = pp.posy + (sintable[pp.getAnglei() & 2047] >> 5);
				} else {
					x = (int) (pp.posx + (BCosAngle(pp.getAnglef()) / 32.0f));
					y = (int) (pp.posy + (BSinAngle(pp.getAnglef()) / 32.0f));
				}
				short tempsect = COVERupdatesector(x, y, pp.cursectnum);

				if (tempsect >= 0) // If the new point is inside a valid
									// sector...
				{
					// Get the floorz as if the new (x,y) point was still in
					// your sector
					int j = engine.getflorzofslope(pp.cursectnum, pp.posx, pp.posy);
					int k = engine.getflorzofslope(pp.cursectnum, x, y);

					// If extended point is in same sector as you or the slopes
					// of the sector of the extended point and your sector match
					// closely (to avoid accidently looking straight out when
					// you're at the edge of a sector line) then adjust horizon
					// accordingly
					if ((pp.cursectnum == tempsect)
							|| (klabs(engine.getflorzofslope(tempsect, x, y) - k) <= (4 << 8))) {
						pp.horizoff += (((j - k) * 160) >> 16);
					}
				}
			}
		}

		if (TEST(pp.Flags, PF_CLIMBING)) {
			// tilt when climbing but you can't even really tell it
			if (pp.horizoff < 100) {
				if(isOriginal())
					pp.horizoff += (((100 - (int) pp.horizoff) >> 3) + 1);
				else pp.horizoff += (((100 - pp.horizoff) / 8.0f) + 1);
			}
		} else {
			// Make horizoff grow towards 0 since horizoff is not modified when
			// you're not on a slope
			if (pp.horizoff > 0) {
				if(isOriginal())
					pp.horizoff -= (((int) pp.horizoff >> 3) + 1);
				else pp.horizoff -= ((pp.horizoff / 8.0f) + 1);
			}
			if (pp.horizoff < 0) {
				if(isOriginal())
					pp.horizoff += (((int)(-pp.horizoff) >> 3) + 1);
				else pp.horizoff += (((-pp.horizoff) / 8.0f) + 1);
			}
		}
	}

	public static final int HORIZ_SPEED = (16);

	public static void DoPlayerHorizon(PlayerStr pp) {
		if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_NONE) {
			if (gs.SlopeTilting || !gs.gMouseAim)
				PlayerAutoLook(pp);
		}

		if (pp.input.aimvel != 0) {
			pp.horizbase += pp.input.aimvel;
			pp.Flags |= (PF_LOCK_HORIZ | PF_LOOKING);
		}

		if (TEST_SYNC_KEY(pp, SK_CENTER_VIEW)) {
			{
				pp.horiz = pp.horizbase = 100;
				pp.horizoff = 0;
			}
		}

		// this is the locked type
		if (TEST_SYNC_KEY(pp, SK_SNAP_UP) || TEST_SYNC_KEY(pp, SK_SNAP_DOWN)) {
			// set looking because player is manually looking
			pp.Flags |= (PF_LOCK_HORIZ | PF_LOOKING);

			// adjust pp.horizon negative
			if (TEST_SYNC_KEY(pp, SK_SNAP_DOWN))
				pp.horizbase -= (HORIZ_SPEED / 2);

			// adjust pp.horizon positive
			if (TEST_SYNC_KEY(pp, SK_SNAP_UP))
				pp.horizbase += (HORIZ_SPEED / 2);
		}

		// this is the unlocked type
		if (TEST_SYNC_KEY(pp, SK_LOOK_UP) || TEST_SYNC_KEY(pp, SK_LOOK_DOWN)) {
			pp.Flags &= ~(PF_LOCK_HORIZ);
			pp.Flags |= (PF_LOOKING);

			// adjust pp.horizon negative
			if (TEST_SYNC_KEY(pp, SK_LOOK_DOWN))
				pp.horizbase -= HORIZ_SPEED;

			// adjust pp.horizon positive
			if (TEST_SYNC_KEY(pp, SK_LOOK_UP))
				pp.horizbase += HORIZ_SPEED;
		}

		if (!TEST(pp.Flags, PF_LOCK_HORIZ)) {
			if (!(TEST_SYNC_KEY(pp, SK_LOOK_UP) || TEST_SYNC_KEY(pp, SK_LOOK_DOWN))) {
				// not pressing the pp.horiz keys
				if (pp.horizbase != 100) {

					// move pp.horiz back to 100
					for (int i = 1; i != 0; i--) {
						// this formula does not work for pp.horiz = 101-103
						if(isOriginal())
							pp.horizbase += 25 - ((int)pp.horizbase >> 2);
						else pp.horizbase += 25 - pp.horizbase / 4.0f;
					}
				} else {
					// not looking anymore because pp.horiz is back at 100
					pp.Flags &= ~(PF_LOOKING);
				}
			}
		}

		// bound the base
		pp.horizbase = Math.max(pp.horizbase, PLAYER_HORIZ_MIN);
		pp.horizbase = Math.min(pp.horizbase, PLAYER_HORIZ_MAX);

		// bound adjust horizoff
		if (pp.horizbase + pp.horizoff < PLAYER_HORIZ_MIN)
			pp.horizoff = PLAYER_HORIZ_MIN - pp.horizbase;
		else if (pp.horizbase + pp.horizoff > PLAYER_HORIZ_MAX)
			pp.horizoff = PLAYER_HORIZ_MAX - pp.horizbase;

		// add base and offsets
		pp.horiz = pp.horizbase + pp.horizoff;
	}

	public static void DoPlayerBob(PlayerStr pp) {
		int dist;
		int amt;

		dist = 0;

		dist = Distance(pp.posx, pp.posy, pp.oldposx, pp.oldposy);

		if (dist > 512)
			dist = 0;

		// if running make a longer stride
		if (TEST_SYNC_KEY(pp, SK_RUN) || TEST(pp.Flags, PF_LOCK_RUN)) {
			// amt = 10;
			amt = 12;
			amt = mulscale(amt, dist << 8, 16);

			dist = mulscale(dist, 26000, 16);
			// controls how fast you move through the sin table
			pp.bcnt += dist;

			// wrap bcnt
			pp.bcnt &= 2047;

			// move pp.horiz up and down from 100 using sintable
			pp.bob_z = mulscale(Z(amt), sintable[pp.bcnt], 14);
		} else {
			amt = 5;
			amt = mulscale(amt, dist << 9, 16);

			dist = mulscale(dist, 32000, 16);
			// controls how fast you move through the sin table
			pp.bcnt += dist;

			// wrap bcnt
			pp.bcnt &= 2047;

			// move pp.horiz up and down from 100 using sintable
			pp.bob_z = mulscale(Z(amt), sintable[pp.bcnt], 14);
		}
	}

	public static void DoPlayerBeginRecoil(PlayerStr pp, int pix_amt) {
		pp.Flags |= (PF_RECOIL);

		pp.recoil_amt = (short) pix_amt;
		pp.recoil_speed = 80;
		pp.recoil_ndx = 0;
		pp.recoil_horizoff = 0;
	}

	public static void DoPlayerRecoil(PlayerStr pp) {
		// controls how fast you move through the sin table
		pp.recoil_ndx += pp.recoil_speed;

		if (sintable[pp.recoil_ndx] < 0) {
			pp.Flags &= ~(PF_RECOIL);
			pp.recoil_horizoff = 0;
			return;
		}

		// move pp.horiz up and down
		pp.recoil_horizoff = (short) ((pp.recoil_amt * sintable[pp.recoil_ndx]) >> 14);
	}

	// for wading
	public static void DoPlayerSpriteBob(PlayerStr pp, int player_height, int bob_amt, int bob_speed) {
		SPRITE sp = pp.getSprite();

		pp.bob_ndx = (short) ((pp.bob_ndx + (synctics << bob_speed)) & 2047);

		pp.bob_amt = (short) ((bob_amt * (long) sintable[pp.bob_ndx]) >> 14);

		sp.z = (pp.posz + player_height) + pp.bob_amt;
	}

	public static void UpdatePlayerUnderSprite(PlayerStr pp) {
		SPRITE over_sp = pp.getSprite();
		USER over_u = pUser[pp.PlayerSprite];

		SPRITE sp;
		USER u;
		short SpriteNum;

		int water_level_z, zdiff;
		boolean above_water, in_dive_area;

		if (Prediction)
			return;

		// dont bother spawning if you ain't really in the water
		// water_level_z = sector[over_sp.sectnum].floorz - Z(pp.WadeDepth);
		water_level_z = sector[over_sp.sectnum].floorz;// - Z(pp.WadeDepth);

		// if not below water
		above_water = (SPRITEp_BOS(over_sp) <= water_level_z);
		in_dive_area = SpriteInDiveArea(over_sp);

		// if not in dive area OR (in dive area AND above the water) - Kill it
		if (!in_dive_area || (in_dive_area && above_water)) {

			// if under sprite exists and not in a dive area - Kill it
			if (pp.PlayerUnderSprite >= 0) {
				KillSprite(pp.PlayerUnderSprite);
				pp.PlayerUnderSprite = -1;
			}
			return;
		} else {
			// if in a dive area and a under sprite does not exist - create it
			if (pp.PlayerUnderSprite < 0) {
				SpawnPlayerUnderSprite(pp.pnum);
			}
		}

		sp = sprite[pp.PlayerUnderSprite];
		u = pUser[pp.PlayerUnderSprite];

		SpriteNum = pp.PlayerUnderSprite;

		sp.x = over_sp.x;
		sp.y = over_sp.y;
		sp.z = over_sp.z;
		engine.changespritesect(SpriteNum, over_sp.sectnum);

		SpriteWarpToUnderwater(SpriteNum);

		// find z water level of the top sector
		// diff between the bottom of the upper sprite and the water level
		zdiff = SPRITEp_BOS(over_sp) - water_level_z;

		// add diff to ceiling
		sp.z = sector[sp.sectnum].ceilingz + zdiff;

		u.State = over_u.State;
		u.Rot = over_u.Rot;
		u.StateStart = over_u.StateStart;

		sp.picnum = over_sp.picnum;
	}

	public static void UpdatePlayerSprite(PlayerStr pp) {
		if (!isValidSector(pp.cursectnum))
			return;

		SPRITE sp = pp.getSprite();

		// Update sprite representation of player
		game.pInt.setsprinterpolate(pp.PlayerSprite, sp);

		sp.x = pp.posx;
		sp.y = pp.posy;

		// there are multiple death functions
		if (TEST(pp.Flags, PF_DEAD)) {
			engine.changespritesect(pp.PlayerSprite, pp.cursectnum);
			sprite[pp.PlayerSprite].ang = pp.getAnglei();
			UpdatePlayerUnderSprite(pp);
			return;
		}

		if (pp.sop_control != -1) {
			sp.z = sector[pp.cursectnum].floorz;
			engine.changespritesect(pp.PlayerSprite, pp.cursectnum);
		} else if (pp.DoPlayerAction == Player_Action_Func.DoPlayerCrawl) {
			sp.z = pp.posz + PLAYER_CRAWL_HEIGHT;
			engine.changespritesect(pp.PlayerSprite, pp.cursectnum);
		} else if (pp.DoPlayerAction == Player_Action_Func.DoPlayerWade) {
			sp.z = pp.posz + PLAYER_HEIGHT;
			engine.changespritesect(pp.PlayerSprite, pp.cursectnum);

			if (pp.WadeDepth > Z(29)) {
				DoPlayerSpriteBob(pp, PLAYER_HEIGHT, Z(3), 3);
			}
		} else if (pp.DoPlayerAction == Player_Action_Func.DoPlayerDive) {
			// bobbing and sprite position taken care of in DoPlayerDive
			sp.z = pp.posz + Z(10);
			engine.changespritesect(pp.PlayerSprite, pp.cursectnum);
		} else if (pp.DoPlayerAction == Player_Action_Func.DoPlayerClimb) {
			sp.z = pp.posz + Z(17);

			engine.changespritesect(pp.PlayerSprite, pp.cursectnum);
		} else if (pp.DoPlayerAction == Player_Action_Func.DoPlayerFly) {
			DoPlayerSpriteBob(pp, PLAYER_HEIGHT, Z(6), 3);
			engine.changespritesect(pp.PlayerSprite, pp.cursectnum);
		} else if (pp.DoPlayerAction == Player_Action_Func.DoPlayerJump
				|| pp.DoPlayerAction == Player_Action_Func.DoPlayerFall
				|| pp.DoPlayerAction == Player_Action_Func.DoPlayerForceJump) {
			sp.z = pp.posz + PLAYER_HEIGHT;
			engine.changespritesect(pp.PlayerSprite, pp.cursectnum);
		} else if (pp.DoPlayerAction == Player_Action_Func.DoPlayerTeleportPause) {
			sp.z = pp.posz + PLAYER_HEIGHT;
			engine.changespritesect(pp.PlayerSprite, pp.cursectnum);
		} else {
			sp.z = pp.loz;
			engine.changespritesect(pp.PlayerSprite, pp.cursectnum);
		}

		UpdatePlayerUnderSprite(pp);

		sp.ang = (short) pp.pang;
	}

	public static void DoPlayerZrange(PlayerStr pp) {
		int ceilhit, florhit;
		short bakcstat;

		// Don't let you fall if you're just slightly over a cliff
		// This function returns the highest and lowest z's
		// for an entire box, NOT just a point. -Useful for clipping
		SPRITE psp = pp.getSprite();
		bakcstat = psp.cstat;
		psp.cstat &= ~(CSTAT_SPRITE_BLOCK);
		FAFgetzrange(pp.posx, pp.posy, pp.posz + Z(8), pp.cursectnum, tmp_ptr[0].set(0), tmp_ptr[1].set(0), tmp_ptr[2].set(0), tmp_ptr[3].set(0),
				(psp.clipdist << 2) - GETZRANGE_CLIP_ADJ, CLIPMASK_PLAYER);
		psp.cstat = bakcstat;

		pp.hiz = tmp_ptr[0].value;
		ceilhit = tmp_ptr[1].value;
		pp.loz = tmp_ptr[2].value;
		florhit = tmp_ptr[3].value;

		// 16384+sector (sector first touched) or
		// 49152+spritenum (sprite first touched)

		pp.lo_sectp = pp.hi_sectp = -1;
		pp.lo_sp = pp.hi_sp = -1;

		if (DTEST(ceilhit, 0xc000) == 49152) {
			pp.hi_sp = ceilhit & 4095;
		} else {
			pp.hi_sectp = ceilhit & 4095;
		}

		if (DTEST(florhit, 0xc000) == 49152) {
			pp.lo_sp = florhit & 4095;

			// prevent player from standing on Zombies
			if (sprite[pp.lo_sp].statnum == STAT_ENEMY && pUser[pp.lo_sp].ID == ZOMBIE_RUN_R0) {
				pp.lo_sectp = sprite[pp.lo_sp].sectnum;
				pp.loz = sprite[pp.lo_sp].z;
				pp.lo_sp = -1;
			}
		} else {
			pp.lo_sectp = florhit & 4095;
		}
	}

	private static void DoPlayerSlide(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		int push_ret;

		if ((pp.slide_xvect | pp.slide_yvect) == 0)
			return;

		if (pp.sop != -1)
			return;

		pp.slide_xvect = mulscale(pp.slide_xvect, PLAYER_SLIDE_FRICTION, 16);
		pp.slide_yvect = mulscale(pp.slide_yvect, PLAYER_SLIDE_FRICTION, 16);

		if (klabs(pp.slide_xvect) < 12800 && klabs(pp.slide_yvect) < 12800)
			pp.slide_xvect = pp.slide_yvect = 0;

		push_ret = engine.pushmove(pp.posx, pp.posy, pp.posz, pp.cursectnum, (pp.getSprite().clipdist << 2),
				pp.ceiling_dist, pp.floor_dist, CLIPMASK_PLAYER);

		if (pushmove_sectnum != -1) {
			pp.posx = pushmove_x;
			pp.posy = pushmove_y;
			pp.posz = pushmove_z;
			pp.cursectnum = pushmove_sectnum;
		}

		if (push_ret < 0) {
			if (!TEST(pp.Flags, PF_DEAD)) {
				PlayerUpdateHealth(pp, -u.Health); // Make sure he dies!
				PlayerCheckDeath(pp, -1);

				if (TEST(pp.Flags, PF_DEAD))
					return;
			}
			return;
		}
		engine.clipmove(pp.posx, pp.posy, pp.posz, pp.cursectnum, pp.slide_xvect, pp.slide_yvect,
				(pp.getSprite().clipdist << 2), pp.ceiling_dist, pp.floor_dist, CLIPMASK_PLAYER);

		if (clipmove_sectnum != -1) {
			pp.posx = clipmove_x;
			pp.posy = clipmove_y;
			pp.posz = clipmove_z;
			pp.cursectnum = clipmove_sectnum;
		}

		PlayerCheckValidMove(pp);
		push_ret = engine.pushmove(pp.posx, pp.posy, pp.posz, pp.cursectnum, (pp.getSprite().clipdist << 2),
				pp.ceiling_dist, pp.floor_dist, CLIPMASK_PLAYER);

		if (pushmove_sectnum != -1) {
			pp.posx = pushmove_x;
			pp.posy = pushmove_y;
			pp.posz = pushmove_z;
			pp.cursectnum = pushmove_sectnum;
		}

		if (push_ret < 0) {
			if (!TEST(pp.Flags, PF_DEAD)) {
				PlayerUpdateHealth(pp, -u.Health); // Make sure he dies!
				PlayerCheckDeath(pp, -1);

				if (TEST(pp.Flags, PF_DEAD))
					return;
			}
			return;
		}
	}

	private static int count = 0;

	public static void PlayerCheckValidMove(PlayerStr pp) {
		if (pp.cursectnum == -1) {
			pp.posx = pp.oldposx;
			pp.posy = pp.oldposy;
			pp.posz = pp.oldposz;
			pp.cursectnum = pp.lastcursectnum;

			// if stuck here for more than 10 seconds
			if (count++ > 40 * 10) {
				game.GameCrash("Player stuck");
				count = 0;
				game.show();
			}
		}
	}

	public static void DoPlayerMenuKeys(PlayerStr pp) {
		if (game.nNetMode == NetMode.Single) {
			if (TEST_SYNC_KEY((pp), SK_AUTO_AIM)) {
				if (FLAG_KEY_PRESSED(pp, SK_AUTO_AIM)) {
					FLAG_KEY_RELEASE(pp, SK_AUTO_AIM);
					pp.Flags ^= (PF_AUTO_AIM);
				}
			} else
				FLAG_KEY_RESET(pp, SK_AUTO_AIM);
		}
	}

	public static void PlayerSectorBound(PlayerStr pp, int amt) {
		// player should never go into a sector

		// was getting some problems with this
		// when jumping onto hight sloped sectors

		// call this routine to make sure he doesn't
		// called from DoPlayerMove() but can be called
		// from anywhere it is needed

		engine.getzsofslope(pp.cursectnum, pp.posx, pp.posy, zofslope);

		if (pp.posz > zofslope[FLOOR] - amt)
			pp.posz = zofslope[FLOOR] - amt;

		if (pp.posz < zofslope[CEIL] + amt)
			pp.posz = zofslope[CEIL] + amt;
	}

	public static final void PLAYER_RUN_LOCK(PlayerStr pp) {
		if (TEST_SYNC_KEY((pp), SK_RUN_LOCK)) {
			if (FLAG_KEY_PRESSED((pp), SK_RUN_LOCK)) {
				FLAG_KEY_RELEASE((pp), SK_RUN_LOCK);
				(pp).Flags ^= (PF_LOCK_RUN);
				gs.AutoRun = !!TEST((pp).Flags, PF_LOCK_RUN);
				PutStringInfo(pp, "Run mode " + (TEST((pp).Flags, PF_LOCK_RUN) ? "ON" : "OFF"));
			}
		} else
			FLAG_KEY_RESET((pp), SK_RUN_LOCK);
	}

	public static void DoPlayerMove(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		int friction;
		int save_cstat;
		int push_ret = 0;

		SlipSlope(pp);

		PLAYER_RUN_LOCK(pp);

		DoPlayerTurn(pp);

		pp.oldposx = pp.posx;
		pp.oldposy = pp.posy;
		pp.oldposz = pp.posz;
		pp.lastcursectnum = pp.cursectnum;

		if (PLAYER_MOVING(pp) == 0)
			pp.Flags &= ~(PF_PLAYER_MOVED);
		else
			pp.Flags |= (PF_PLAYER_MOVED);

		DoPlayerSlide(pp);

		pp.oxvect = pp.xvect;
		pp.oyvect = pp.yvect;

		pp.xvect += ((pp.input.vel * synctics * 2) << 6);
		pp.yvect += ((pp.input.svel * synctics * 2) << 6);

		friction = pp.friction;
		if (!TEST(pp.Flags, PF_SWIMMING) && pp.WadeDepth != 0) {
			friction -= pp.WadeDepth * 100;
		}

		pp.xvect = mulscale(pp.xvect, friction, 16);
		pp.yvect = mulscale(pp.yvect, friction, 16);

		if (TEST(pp.Flags, PF_FLYING)) {
			// do a bit of weighted averaging
			pp.xvect = (pp.xvect + (pp.oxvect * 1)) / 2;
			pp.yvect = (pp.yvect + (pp.oyvect * 1)) / 2;
		} else if (TEST(pp.Flags, PF_DIVING)) {
			// do a bit of weighted averaging
			pp.xvect = (pp.xvect + (pp.oxvect * 2)) / 3;
			pp.yvect = (pp.yvect + (pp.oyvect * 2)) / 3;
		}

		if (klabs(pp.xvect) < 12800 && klabs(pp.yvect) < 12800)
			pp.xvect = pp.yvect = 0;

		SPRITE psp = pp.getSprite();

		psp.xvel = (short) (FindDistance2D(pp.xvect, pp.yvect) >> 14);

		if (TEST(pp.Flags, PF_CLIP_CHEAT)) {
			short sectnum = pp.cursectnum;
			pp.posx += pp.xvect >> 14;
			pp.posy += pp.yvect >> 14;
			sectnum = COVERupdatesector(pp.posx, pp.posy, sectnum);
			if (sectnum != -1)
				pp.cursectnum = sectnum;
		} else {
			push_ret = engine.pushmove(pp.posx, pp.posy, pp.posz, pp.cursectnum, (psp.clipdist << 2),
					pp.ceiling_dist, pp.floor_dist - Z(16), CLIPMASK_PLAYER);

			if (pushmove_sectnum != -1) {
				pp.posx = pushmove_x;
				pp.posy = pushmove_y;
				pp.posz = pushmove_z;
				pp.cursectnum = pushmove_sectnum;
			}

			if (push_ret < 0) {
				if (!TEST(pp.Flags, PF_DEAD)) {
					PlayerUpdateHealth(pp, -u.Health); // Make sure he dies!
					PlayerCheckDeath(pp, -1);

					if (TEST(pp.Flags, PF_DEAD))
						return;
				}
			}

			save_cstat = psp.cstat;
			psp.cstat &= ~(CSTAT_SPRITE_BLOCK);
			pp.cursectnum = COVERupdatesector(pp.posx, pp.posy, pp.cursectnum);
			engine.clipmove(pp.posx, pp.posy, pp.posz, pp.cursectnum, pp.xvect, pp.yvect, (psp.clipdist << 2),
					pp.ceiling_dist, pp.floor_dist, CLIPMASK_PLAYER);

			if (clipmove_sectnum != -1) {
				pp.posx = clipmove_x;
				pp.posy = clipmove_y;
				pp.posz = clipmove_z;
				pp.cursectnum = clipmove_sectnum;
			}

			psp.cstat = (short) save_cstat;
			PlayerCheckValidMove(pp);

			push_ret = engine.pushmove(pp.posx, pp.posy, pp.posz, pp.cursectnum, (psp.clipdist << 2),
					pp.ceiling_dist, pp.floor_dist - Z(16), CLIPMASK_PLAYER);

			if (pushmove_sectnum != -1) {
				pp.posx = pushmove_x;
				pp.posy = pushmove_y;
				pp.posz = pushmove_z;
				pp.cursectnum = pushmove_sectnum;
			}

			if (push_ret < 0) {

				if (!TEST(pp.Flags, PF_DEAD)) {
					PlayerUpdateHealth(pp, -u.Health); // Make sure he dies!
					PlayerCheckDeath(pp, -1);

					if (TEST(pp.Flags, PF_DEAD))
						return;
				}
			}
		}

		// check for warp - probably can remove from CeilingHit
		if (WarpPlane(pp.posx, pp.posy, pp.posz, pp.cursectnum) != null) {
			pp.posx = warp.x;
			pp.posy = warp.y;
			pp.posz = warp.z;
			pp.cursectnum = warp.sectnum;
			PlayerWarpUpdatePos(pp);
		}

		DoPlayerZrange(pp);

		DoPlayerSetWadeDepth(pp);

		DoPlayerHorizon(pp);

		if (isValidSector(pp.cursectnum) && TEST(sector[pp.cursectnum].extra, SECTFX_DYNAMIC_AREA)) {
			if (TEST(pp.Flags, PF_FLYING | PF_JUMPING | PF_FALLING)) {
				if (pp.posz > pp.loz)
					pp.posz = pp.loz - PLAYER_HEIGHT;

				if (pp.posz < pp.hiz)
					pp.posz = pp.hiz + PLAYER_HEIGHT;
			} else if (TEST(pp.Flags, PF_SWIMMING | PF_DIVING)) {
				if (pp.posz > pp.loz)
					pp.posz = pp.loz - PLAYER_SWIM_HEIGHT;

				if (pp.posz < pp.hiz)
					pp.posz = pp.hiz + PLAYER_SWIM_HEIGHT;
			}
		}
	}

	public static void DoPlayerSectorUpdatePreMove(PlayerStr pp) {
		short sectnum = pp.cursectnum;
		if(!isValidSector(pp.cursectnum))
			return;

		if (TEST(sector[pp.cursectnum].extra, SECTFX_DYNAMIC_AREA)) {
			sectnum = engine.updatesectorz(pp.posx, pp.posy, pp.posz, sectnum);
			if (sectnum < 0) {
				sectnum = pp.cursectnum;
				sectnum = COVERupdatesector(pp.posx, pp.posy, sectnum);
			}
		} else if (FAF_ConnectArea(sectnum)) {
			sectnum = engine.updatesectorz(pp.posx, pp.posy, pp.posz, sectnum);
			if (sectnum < 0) {
				sectnum = pp.cursectnum;
				sectnum = COVERupdatesector(pp.posx, pp.posy, sectnum);
			}
		}

		pp.cursectnum = sectnum;
	}

	public static void DoPlayerSectorUpdatePostMove(PlayerStr pp) {
		// need to do updatesectorz if in connect area
		if (FAF_ConnectArea(pp.cursectnum)) {
			short sectnum = pp.cursectnum;
			pp.cursectnum = engine.updatesectorz(pp.posx, pp.posy, pp.posz, sectnum);

			// can mess up if below
			if (pp.cursectnum < 0) {
				pp.cursectnum = sectnum;

				// adjust the posz to be in a sector
				engine.getzsofslope(pp.cursectnum, pp.posx, pp.posy, zofslope);
				if (pp.posz > zofslope[FLOOR])
					pp.posz = zofslope[FLOOR];

				if (pp.posz < zofslope[CEIL])
					pp.posz = zofslope[CEIL];

				// try again
				pp.cursectnum = engine.updatesectorz(pp.posx, pp.posy, pp.posz, pp.cursectnum);
			}
		} else {
			PlayerSectorBound(pp, Z(1));
		}
	}

	public static void PlaySOsound(short sectnum, int sound_num) {
		short i, nexti;
		// play idle sound - sound 1
		for (i = headspritesect[sectnum]; i != -1; i = nexti) {
			nexti = nextspritesect[i];
			if (sprite[i].statnum == STAT_SOUND_SPOT) {
				DoSoundSpotStopSound(sprite[i].lotag);
				DoSoundSpotMatch(sprite[i].lotag, sound_num, SoundType.SOUND_OBJECT_TYPE);
			}
		}
	}

	public static void StopSOsound(short sectnum) {
		short i, nexti;

		// play idle sound - sound 1
		for (i = headspritesect[sectnum]; i != -1; i = nexti) {
			nexti = nextspritesect[i];
			if (sprite[i].statnum == STAT_SOUND_SPOT)
				DoSoundSpotStopSound(sprite[i].lotag);
		}
	}

	public static void DoPlayerMoveBoat(PlayerStr pp) {
		int z;
		int floor_dist;
		short save_sectnum;
		Sector_Object sop = SectorObject[pp.sop];

		Input last_input;
		int fifo_ndx;

		if (Prediction)
			return;

		if (!Prediction) {
			// this code looks at the fifo to get the last value for comparison
			fifo_ndx = (game.pNet.gNetFifoTail - 2) & (MOVEFIFOSIZ - 1);
			last_input = (Input) game.pNet.gFifoInput[fifo_ndx][pp.pnum];

			if (klabs(pp.input.vel | pp.input.svel) != 0 && klabs(last_input.vel | last_input.svel) == 0)
				PlaySOsound(sop.mid_sector, SO_DRIVE_SOUND);
			else if (klabs(pp.input.vel | pp.input.svel) == 0 && klabs(last_input.vel | last_input.svel) != 0)
				PlaySOsound(sop.mid_sector, SO_IDLE_SOUND);
		}

		PLAYER_RUN_LOCK(pp);

		DoPlayerTurnBoat(pp);

		if (PLAYER_MOVING(pp) == 0)
			pp.Flags &= ~(PF_PLAYER_MOVED);
		else
			pp.Flags |= (PF_PLAYER_MOVED);

		pp.oxvect = pp.xvect;
		pp.oyvect = pp.yvect;

		if (sop.drive_speed != 0) {
			pp.xvect = mulscale(pp.input.vel, sop.drive_speed, 6);
			pp.yvect = mulscale(pp.input.svel, sop.drive_speed, 6);

			// does sliding/momentum
			pp.xvect = (pp.xvect + (pp.oxvect * (sop.drive_slide - 1))) / sop.drive_slide;
			pp.yvect = (pp.yvect + (pp.oyvect * (sop.drive_slide - 1))) / sop.drive_slide;
		} else {
			pp.xvect += ((pp.input.vel * synctics * 2) << 6);
			pp.yvect += ((pp.input.svel * synctics * 2) << 6);

			pp.xvect = mulscale(pp.xvect, BOAT_FRICTION, 16);
			pp.yvect = mulscale(pp.yvect, BOAT_FRICTION, 16);

			// does sliding/momentum
			pp.xvect = (pp.xvect + (pp.oxvect * 5)) / 6;
			pp.yvect = (pp.yvect + (pp.oyvect * 5)) / 6;
		}

		if (klabs(pp.xvect) < 12800 && klabs(pp.yvect) < 12800)
			pp.xvect = pp.yvect = 0;

		pp.lastcursectnum = pp.cursectnum;
		z = pp.posz + Z(10);

		save_sectnum = pp.cursectnum;
		OperateSectorObject(pp.sop, pp.getAnglef(), MAXSO, MAXSO, 1);
		pp.cursectnum = sop.op_main_sector; // for speed

		floor_dist = klabs(z - sop.floor_loz);
		engine.clipmove(pp.posx, pp.posy, z, pp.cursectnum, pp.xvect, pp.yvect, sop.clipdist, Z(4), floor_dist,
				CLIPMASK_PLAYER);
		if (clipmove_sectnum != -1) {
			pp.posx = clipmove_x;
			pp.posy = clipmove_y;
			z = clipmove_z;
			pp.cursectnum = clipmove_sectnum;
		}

		OperateSectorObject(pp.sop, pp.getAnglef(), pp.posx, pp.posy, 1);
		pp.cursectnum = save_sectnum; // for speed

		DoPlayerHorizon(pp);
	}

	public static void DoTankTreads(PlayerStr pp) {
		SPRITE sp;
		short i, nexti;
		int vel;
		int j;
		int dot;
		boolean reverse = false;

		if (Prediction)
			return;

		vel = FindDistance2D(pp.xvect >> 8, pp.yvect >> 8);
		if(isOriginal())
			dot = DOT_PRODUCT_2D(pp.xvect, pp.yvect, sintable[NORM_ANGLE(pp.getAnglei() + 512)], sintable[pp.getAnglei()]);
		else dot = DOT_PRODUCT_2D(pp.xvect, pp.yvect, (int) BCosAngle(pp.getAnglef()), (int) BSinAngle(pp.getAnglef()));
		if (dot < 0)
			reverse = true;

		Sector_Object sop = SectorObject[pp.sop];

		for (j = 0; sop.sector[j] != -1; j++) {
			for (i = headspritesect[sop.sector[j]]; i != -1; i = nexti) {
				nexti = nextspritesect[i];
				sp = sprite[i];

				// BOOL1 is set only if pans with SO
				if (!TEST_BOOL1(sp))
					continue;

				if (sp.statnum == STAT_WALL_PAN) {
					if (reverse) {
						if (!TEST_BOOL2(sp)) {
							SET_BOOL2(sp);
							sp.ang = NORM_ANGLE(sp.ang + 1024);
						}
					} else {
						if (TEST_BOOL2(sp)) {
							RESET_BOOL2(sp);
							sp.ang = NORM_ANGLE(sp.ang + 1024);
						}
					}

					sp.xvel = (short) vel;
				} else if (sp.statnum == STAT_FLOOR_PAN) {
					sp = sprite[i];

					if (reverse) {
						if (!TEST_BOOL2(sp)) {
							SET_BOOL2(sp);
							sp.ang = NORM_ANGLE(sp.ang + 1024);
						}
					} else {
						if (TEST_BOOL2(sp)) {
							RESET_BOOL2(sp);
							sp.ang = NORM_ANGLE(sp.ang + 1024);
						}
					}

					sp.xvel = (short) vel;
				} else if (sp.statnum == STAT_CEILING_PAN) {
					sp = sprite[i];

					if (reverse) {
						if (!TEST_BOOL2(sp)) {
							SET_BOOL2(sp);
							sp.ang = NORM_ANGLE(sp.ang + 1024);
						}
					} else {
						if (TEST_BOOL2(sp)) {
							RESET_BOOL2(sp);
							sp.ang = NORM_ANGLE(sp.ang + 1024);
						}
					}

					sp.xvel = (short) vel;
				}
			}
		}
	}

	public static void SetupDriveCrush(PlayerStr pp, int[] x, int[] y) {
		int radius = SectorObject[pp.sop_control].clipdist;

		x[0] = pp.posx - radius;
		y[0] = pp.posy - radius;

		x[1] = pp.posx + radius;
		y[1] = pp.posy - radius;

		x[2] = pp.posx + radius;
		y[2] = pp.posy + radius;

		x[3] = pp.posx - radius;
		y[3] = pp.posy + radius;
	}

	private static int vel;

	public static void DriveCrush(PlayerStr pp, int[] x, int[] y) {
		SPRITE sp;
		USER u;
		int i, nexti;
		short stat;

		if (MoveSkip4 == 0)
			return;

		// not moving - don't crush
		if ((pp.xvect | pp.yvect) == 0 && pp.input.angvel == 0)
			return;

		Sector_Object sop = SectorObject[pp.sop_control];

		// main sector
		for (i = headspritesect[sop.op_main_sector]; i != -1; i = nexti) {
			nexti = nextspritesect[i];
			sp = sprite[i];
			u = pUser[i];

			if (testpointinquad(sp.x, sp.y, x, y) != 0) {
				if (TEST(sp.extra, SPRX_BREAKABLE) && HitBreakSprite(i, 0))
					continue;

				if (sp.statnum == STAT_MISSILE)
					continue;

				if (sp.picnum == ST1)
					continue;

				if (TEST(sp.extra, SPRX_PLAYER_OR_ENEMY)) {
					if (!TEST(u.Flags, SPR_DEAD) && !TEST(sp.extra, SPRX_BREAKABLE))
						continue;
				}

				if (TEST(sp.cstat, CSTAT_SPRITE_INVISIBLE))
					continue;

				if (sp.statnum > STAT_DONT_DRAW)
					continue;

				if (sp.z < sop.crush_z)
					continue;

				SpriteQueueDelete(i);
				KillSprite(i);
			}
		}

		// all enemys
		for (i = headspritestat[STAT_ENEMY]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (testpointinquad(sp.x, sp.y, x, y) != 0) {
				// if (sp.z < pp.posz)
				if (sp.z < sop.crush_z)
					continue;

				vel = FindDistance2D(pp.xvect >> 8, pp.yvect >> 8);
				if (vel < 9000) {
					DoActorBeginSlide(i, engine.getangle(pp.xvect, pp.yvect), vel / 8, 5);
					if (DoActorSlide(i))
						continue;
				}

				UpdateSinglePlayKills(i, pp);

				if (SpawnShrap(i, -99))
					SetSuicide(i);
				else
					KillSprite(i);
			}
		}

		// all dead actors
		for (i = headspritestat[STAT_DEAD_ACTOR]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (testpointinquad(sp.x, sp.y, x, y) != 0) {
				if (sp.z < sop.crush_z)
					continue;

				SpriteQueueDelete(i);
				KillSprite(i);
			}
		}

		// all players
		for (stat = 0; stat < MAX_SW_PLAYERS; stat++) {
			i = headspritestat[STAT_PLAYER0 + stat];

			if (i < 0)
				continue;

			sp = sprite[i];
			u = pUser[i];

			if (u.PlayerP == pp.pnum)
				continue;

			if (testpointinquad(sp.x, sp.y, x, y) != 0) {
				int damage;

				// if (sp.z < pp.posz)
				if (sp.z < sop.crush_z)
					continue;

				damage = -(u.Health + 100);
				PlayerDamageSlide(Player[u.PlayerP], damage, pp.getAnglei());
				PlayerUpdateHealth(Player[u.PlayerP], damage);
				PlayerCheckDeath(Player[u.PlayerP], pp.PlayerSprite);
			}
		}

		// if it ends up actually in the drivable sector kill it
		for (int s = 0; sop.sector[s] != -1; s++) {
			for (i = headspritesect[sop.sector[s]]; i != -1; i = nexti) {
				nexti = nextspritesect[i];
				sp = sprite[i];
				u = pUser[i];

				// give some extra buffer
				if (sp.z < sop.crush_z + Z(40))
					continue;

				if (TEST(sp.extra, SPRX_PLAYER_OR_ENEMY)) {
					if (sp.statnum == STAT_ENEMY) {
						if (SpawnShrap(i, -99))
							SetSuicide(i);
						else
							KillSprite(i);
					}
				}
			}
		}
	}

	private static int[] x = new int[4], y = new int[4], ox = new int[4], oy = new int[4];

	public static void DoPlayerMoveTank(PlayerStr pp) {
		int z;
		int floor_dist;
		boolean ret;
		short save_sectnum;

		int save_cstat;
		int wallcount;
		int count = 0;

		SECTOR sectp;
		Sector_Object sop = SectorObject[pp.sop];
		int spnum = sop.sp_child;
		USER u = pUser[spnum];
		WALL wp;
		int k;
		short startwall, endwall;

		Input last_input;
		int fifo_ndx;
		boolean RectClip = !!TEST(sop.flags, SOBJ_RECT_CLIP);

		if (Prediction)
			return;

		if (!Prediction) {
			// this code looks at the fifo to get the last value for comparison
			fifo_ndx = (game.pNet.gNetFifoTail - 2) & (MOVEFIFOSIZ - 1);
			last_input = (Input) game.pNet.gFifoInput[fifo_ndx][pp.pnum];

			if (klabs(pp.input.vel | pp.input.svel) != 0 && klabs(last_input.vel | last_input.svel) == 0)
				PlaySOsound(sop.mid_sector, SO_DRIVE_SOUND);
			else if (klabs(pp.input.vel | pp.input.svel) == 0 && klabs(last_input.vel | last_input.svel) != 0)
				PlaySOsound(sop.mid_sector, SO_IDLE_SOUND);
		}

		PLAYER_RUN_LOCK(pp);

		if (PLAYER_MOVING(pp) == 0)
			pp.Flags &= ~(PF_PLAYER_MOVED);
		else
			pp.Flags |= (PF_PLAYER_MOVED);

		pp.oxvect = pp.xvect;
		pp.oyvect = pp.yvect;

		if (sop.drive_speed != 0) {
			pp.xvect = mulscale(pp.input.vel, sop.drive_speed, 6);
			pp.yvect = mulscale(pp.input.svel, sop.drive_speed, 6);

			// does sliding/momentum
			pp.xvect = (pp.xvect + (pp.oxvect * (sop.drive_slide - 1))) / sop.drive_slide;
			pp.yvect = (pp.yvect + (pp.oyvect * (sop.drive_slide - 1))) / sop.drive_slide;
		} else {
			pp.xvect += ((pp.input.vel * synctics * 2) << 6);
			pp.yvect += ((pp.input.svel * synctics * 2) << 6);

			pp.xvect = mulscale(pp.xvect, TANK_FRICTION, 16);
			pp.yvect = mulscale(pp.yvect, TANK_FRICTION, 16);

			pp.xvect = (pp.xvect + (pp.oxvect * 1)) / 2;
			pp.yvect = (pp.yvect + (pp.oyvect * 1)) / 2;
		}

		if (klabs(pp.xvect) < 12800 && klabs(pp.yvect) < 12800)
			pp.xvect = pp.yvect = 0;

		pp.lastcursectnum = pp.cursectnum;
		z = pp.posz + Z(10);

		if (RectClip) {
			wallcount = 0;
			for (int s = 0; sop.sector[s] != -1; s++) {
				sectp = sector[sop.sector[s]];

				startwall = (sectp).wallptr;
				endwall = (short) (startwall + (sectp).wallnum - 1);

				for (k = startwall; k <= endwall; k++) {
					wp = wall[k];
					if (wp.extra != 0
							&& DTEST(wp.extra, WALLFX_LOOP_OUTER | WALLFX_LOOP_OUTER_SECONDARY) == WALLFX_LOOP_OUTER) {
						x[count] = wp.x;
						y[count] = wp.y;

						ox[count] = sop.xmid - sop.xorig[wallcount];
						oy[count] = sop.ymid - sop.yorig[wallcount];

						count++;
					}

					wallcount++;
				}
			}

		}

		save_sectnum = pp.cursectnum;
		OperateSectorObject(pp.sop, pp.getAnglef(), MAXSO, MAXSO, 1);
		pp.cursectnum = sop.op_main_sector; // for speed

		floor_dist = klabs(z - sop.floor_loz);

		if (RectClip) {
			int nx, ny;

			int vel;
			SPRITE psp = pp.getSprite();

			save_cstat = psp.cstat;
			psp.cstat &= ~(CSTAT_SPRITE_BLOCK);
			DoPlayerTurnTankRect(pp, x, y, ox, oy);

			ret = RectClipMove(pp, x, y);
			DriveCrush(pp, x, y);
			psp.cstat = (short) save_cstat;

			if (!ret) {
				vel = FindDistance2D(pp.xvect >> 8, pp.yvect >> 8);

				if (vel > 13000) {
					nx = DIV2(x[0] + x[1]);
					ny = DIV2(y[0] + y[1]);

					engine.hitscan(nx, ny, sector[pp.cursectnum].floorz - Z(10), pp.cursectnum, MOVEx(256, pp.getAnglei()),
							MOVEy(256, pp.getAnglei()), 0, pHitInfo, CLIPMASK_PLAYER);

					if (FindDistance2D(pHitInfo.hitx - nx, pHitInfo.hity - ny) < 800) {
						if (pHitInfo.hitwall >= 0)
							u.ret = pHitInfo.hitwall | HIT_WALL;
						else if (pHitInfo.hitsprite >= 0)
							u.ret = pHitInfo.hitsprite | HIT_SPRITE;
						else
							u.ret = 0;

						VehicleMoveHit(spnum);
					}

					if (!TEST(sop.flags, SOBJ_NO_QUAKE)) {
						SetPlayerQuake(pp);
					}
				}

				if (vel > 12000) {
					pp.xvect = pp.yvect = pp.oxvect = pp.oyvect = 0;
				}
			}
		} else {
			DoPlayerTurnTank(pp, z, floor_dist);
			SPRITE psp = pp.getSprite();
			save_cstat = psp.cstat;
			psp.cstat &= ~(CSTAT_SPRITE_BLOCK);
			if (sop.clipdist != 0) {
				u.ret = engine.clipmove(pp.posx, pp.posy, z, pp.cursectnum, pp.xvect, pp.yvect, sop.clipdist, Z(4),
						floor_dist, CLIPMASK_PLAYER);
				if (clipmove_sectnum != -1) {
					pp.posx = clipmove_x;
					pp.posy = clipmove_y;
					z = clipmove_z;
					pp.cursectnum = clipmove_sectnum;
				}
			} else
				u.ret = MultiClipMove(pp, z, floor_dist);
			psp.cstat = (short) save_cstat;

			if (u.ret != 0) {
				int vel;

				vel = FindDistance2D(pp.xvect >> 8, pp.yvect >> 8);

				if (vel > 13000) {
					VehicleMoveHit(spnum);
					pp.slide_xvect = -pp.xvect << 1;
					pp.slide_yvect = -pp.yvect << 1;
					if (!TEST(sop.flags, SOBJ_NO_QUAKE))
						SetPlayerQuake(pp);
				}

				if (vel > 12000) {
					pp.xvect = pp.yvect = pp.oxvect = pp.oyvect = 0;
				}
			}
		}

		OperateSectorObject(pp.sop, pp.getAnglef(), pp.posx, pp.posy, 1);
		pp.cursectnum = save_sectnum; // for speed

		DoPlayerHorizon(pp);

		DoTankTreads(pp);
	}

	public static void DoPlayerMoveTurret(PlayerStr pp) {
		PLAYER_RUN_LOCK(pp);

		DoPlayerTurnTurret(pp);

		if (PLAYER_MOVING(pp) == 0)
			pp.Flags &= ~(PF_PLAYER_MOVED);
		else
			pp.Flags |= (PF_PLAYER_MOVED);

		Sector_Object sop = SectorObject[pp.sop];

		OperateSectorObject(pp.sop, pp.getAnglef(), sop.xmid, sop.ymid, 1);

		DoPlayerHorizon(pp);
	}

	public static void DoPlayerBeginJump(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];

		pp.Flags |= (PF_JUMPING);
		pp.Flags &= ~(PF_FALLING);
		pp.Flags &= ~(PF_CRAWLING);
		pp.Flags &= ~(PF_LOCK_CRAWL);

		pp.floor_dist = PLAYER_JUMP_FLOOR_DIST;
		pp.ceiling_dist = PLAYER_JUMP_CEILING_DIST;
		pp.friction = PLAYER_JUMP_FRICTION;

		pp.jump_speed = PLAYER_JUMP_AMT + pp.WadeDepth * 4;

		if (DoPlayerWadeSuperJump(pp)) {
			pp.jump_speed = PLAYER_JUMP_AMT - pp.WadeDepth * 5;
		}

		pp.JumpDuration = MAX_JUMP_DURATION;
		pp.DoPlayerAction = Player_Action_Func.DoPlayerJump;

		NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Jump);
	}

	public static void DoPlayerBeginForceJump(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];

		pp.Flags |= (PF_JUMPING);
		pp.Flags &= ~(PF_FALLING | PF_CRAWLING | PF_CLIMBING | PF_LOCK_CRAWL);

		pp.JumpDuration = MAX_JUMP_DURATION;
		pp.DoPlayerAction = Player_Action_Func.DoPlayerForceJump;

		pp.floor_dist = PLAYER_JUMP_FLOOR_DIST;
		pp.ceiling_dist = PLAYER_JUMP_CEILING_DIST;
		pp.friction = PLAYER_JUMP_FRICTION;

		NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Jump);
	}

	public static void DoPlayerJump(PlayerStr pp) {
		short i;

		// reset flag key for double jumps
		if (!TEST_SYNC_KEY(pp, SK_JUMP)) {
			FLAG_KEY_RESET(pp, SK_JUMP);
		}

		// instead of multiplying by synctics, use a loop for greater accuracy
		for (i = 0; i < synctics; i++) {
			if (TEST_SYNC_KEY(pp, SK_JUMP)) {
				if (pp.JumpDuration > 0) {
					pp.jump_speed -= PlayerGravity;
					pp.JumpDuration--;
				}
			}

			// adjust jump speed by gravity - if jump speed greater than 0 player
			// have started falling
			if ((pp.jump_speed += PlayerGravity) > 0) {
				DoPlayerBeginFall(pp);
				DoPlayerFall(pp);
				return;
			}

			// adjust height by jump speed
			pp.posz += pp.jump_speed;

			// if player gets to close the ceiling while jumping
			if (PlayerCeilingHit(pp, pp.hiz + Z(4))) {
				// put player at the ceiling
				pp.posz = pp.hiz + Z(4);

				// reverse your speed to falling
				pp.jump_speed = -pp.jump_speed;

				// start falling
				DoPlayerBeginFall(pp);
				DoPlayerFall(pp);
				return;
			}

			// added this because jumping up to slopes or jumping on steep slopes
			// sometimes caused the view to go into the slope
			// if player gets to close the floor while jumping
			if (PlayerFloorHit(pp, pp.loz - pp.floor_dist)) {
				pp.posz = pp.loz - pp.floor_dist;

				pp.jump_speed = 0;
				PlayerSectorBound(pp, Z(1));
				DoPlayerBeginRun(pp);
				DoPlayerHeight(pp);
				return;
			}
		}

		if (PlayerFlyKey(pp)) {
			DoPlayerBeginFly(pp);
			return;
		}

		// If moving forward and tag is a ladder start climbing
		if (PlayerOnLadder(pp)) {
			DoPlayerBeginClimb(pp);
			return;
		}

		DoPlayerMove(pp);

		DoPlayerJumpHeight(pp);
	}

	public static void DoPlayerForceJump(PlayerStr pp) {
		short i;

		// instead of multiplying by synctics, use a loop for greater accuracy
		for (i = 0; i < synctics; i++) {
			// adjust jump speed by gravity - if jump speed greater than 0 player
			// have started falling
			if ((pp.jump_speed += PlayerGravity) > 0) {
				DoPlayerBeginFall(pp);
				DoPlayerFall(pp);
				return;
			}

			// adjust height by jump speed
			pp.posz += pp.jump_speed;

			// if player gets to close the ceiling while jumping
			if (PlayerCeilingHit(pp, pp.hiz + Z(4))) {
				// put player at the ceiling
				pp.posz = pp.hiz + Z(4);

				// reverse your speed to falling
				pp.jump_speed = -pp.jump_speed;

				// start falling
				DoPlayerBeginFall(pp);
				DoPlayerFall(pp);
				return;
			}
		}

		DoPlayerMove(pp);
	}

	public static void DoPlayerBeginFall(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];

		pp.Flags |= (PF_FALLING);
		pp.Flags &= ~(PF_JUMPING);
		pp.Flags &= ~(PF_CRAWLING);
		pp.Flags &= ~(PF_LOCK_CRAWL);

		pp.floor_dist = PLAYER_FALL_FLOOR_DIST;
		pp.ceiling_dist = PLAYER_FALL_CEILING_DIST;
		pp.DoPlayerAction = Player_Action_Func.DoPlayerFall;
		pp.friction = PLAYER_FALL_FRICTION;

		// Only change to falling frame if you were in the jump frame
		// Otherwise an animation may be messed up such as Running Jump Kick
		if (u.Rot == u.ActorActionSet.Jump)
			NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Fall);
	}

	public static void StackedWaterSplash(PlayerStr pp) {
		if (FAF_ConnectArea(pp.cursectnum)) {
			short sectnum = pp.cursectnum;
			sectnum = engine.updatesectorz(pp.posx, pp.posy, SPRITEp_BOS(pp.getSprite()), sectnum);

			if (SectorIsUnderwaterArea(sectnum)) {
				PlaySound(DIGI_SPLASH1, pp, v3df_dontpan);
			}
		}
	}

	private static VOC3D handle = null;

	public static void DoPlayerFall(PlayerStr pp) {
		short i;
		int recoil_amt;
		int depth;

		// reset flag key for double jumps
		if (!TEST_SYNC_KEY(pp, SK_JUMP)) {
			FLAG_KEY_RESET(pp, SK_JUMP);
		}

		if (SectorIsUnderwaterArea(pp.cursectnum)) {
			StackedWaterSplash(pp);
			DoPlayerBeginDiveNoWarp(pp);
			return;
		}

		for (i = 0; i < synctics; i++) {
			// adjust jump speed by gravity
			pp.jump_speed += PlayerGravity;
			if (pp.jump_speed > 4100)
				pp.jump_speed = 4100;

			// adjust player height by jump speed
			pp.posz += pp.jump_speed;

			if (pp.jump_speed > 2000) {
				PlayerSound(DIGI_FALLSCREAM, v3df_dontpan | v3df_doppler | v3df_follow, pp);
				handle = pp.TalkVocHandle; // Save id for later
			} else if (pp.jump_speed > 1300 && !gs.gMouseAim) {
				if (TEST(pp.Flags, PF_LOCK_HORIZ)) {
					pp.Flags &= ~(PF_LOCK_HORIZ);
					pp.Flags |= (PF_LOOKING);
				}
			}

			depth = GetZadjustment(pp.cursectnum, FLOOR_Z_ADJUST) >> 8;
			if (depth == 0)
				depth = pp.WadeDepth;

			if (depth > 20)
				recoil_amt = 0;
			else
				recoil_amt = Math.min(pp.jump_speed * 6, Z(35));

			// need a test for head hits a sloped ceiling while falling
			// if player gets to close the Ceiling while Falling
			if (PlayerCeilingHit(pp, pp.hiz + pp.ceiling_dist)) {
				// put player at the ceiling
				pp.posz = pp.hiz + pp.ceiling_dist;
				// don't return or anything - allow to fall until
				// hit floor
			}

			if (PlayerFloorHit(pp, pp.loz - PLAYER_HEIGHT + recoil_amt)) {
				Sect_User sectu = SectUser[pp.cursectnum];
				SECTOR sectp = sector[pp.cursectnum];

				PlayerSectorBound(pp, Z(1));

				if (sectu != null && (DTEST(sectp.extra, SECTFX_LIQUID_MASK) != SECTFX_LIQUID_NONE)) {
					PlaySound(DIGI_SPLASH1, pp, v3df_dontpan);
				} else {
					if (pp.jump_speed > 1020)
						// Feet hitting ground sound
						PlaySound(DIGI_HITGROUND, pp, v3df_follow | v3df_dontpan);
				}

				if (handle != null && handle.isActive()) {
					// My sound code will detect the sound has stopped and clean up
					// for you.
					handle.stop();
					pp.PlayerTalking = false;
					handle = null;
				}

				// i any kind of crawl key get rid of recoil
				if (DoPlayerTestCrawl(pp) || TEST_SYNC_KEY(pp, SK_CRAWL)) {
					pp.posz = pp.loz - PLAYER_CRAWL_HEIGHT;
				} else {
					// this was causing the z to snap immediately
					// changed it so it stays gradual

					pp.posz += recoil_amt;
					DoPlayerHeight(pp);
				}

				// do some damage
				if (pp.jump_speed > 1700 && depth == 0) {

					PlayerSound(DIGI_PLAYERPAIN2, v3df_follow | v3df_dontpan, pp);

					if (pp.jump_speed > 1700 && pp.jump_speed < 4000) {
						if (pp.jump_speed > 0)
							PlayerUpdateHealth(pp, -((pp.jump_speed - 1700) / 40));
					} else if (pp.jump_speed >= 4000) {
						USER u = pUser[pp.PlayerSprite];
						PlayerUpdateHealth(pp, -u.Health); // Make sure he dies!
						u.Health = 0;
					}

					PlayerCheckDeath(pp, -1);

					if (TEST(pp.Flags, PF_DEAD))
						return;
				}

				if (TEST_SYNC_KEY(pp, SK_CRAWL)) {
					StackedWaterSplash(pp);
					DoPlayerBeginCrawl(pp);
					return;
				}

				if (PlayerCanDiveNoWarp(pp)) {
					DoPlayerBeginDiveNoWarp(pp);
					return;
				}

				StackedWaterSplash(pp);
				DoPlayerBeginRun(pp);
				return;
			}
		}

		if (PlayerFlyKey(pp)) {
			DoPlayerBeginFly(pp);
			return;
		}

		// If moving forward and tag is a ladder start climbing
		if (PlayerOnLadder(pp)) {
			DoPlayerBeginClimb(pp);
			return;
		}

		DoPlayerMove(pp);
	}

	public static void DoPlayerBeginClimb(PlayerStr pp) {
		SPRITE sp = pp.getSprite();

		pp.Flags &= ~(PF_JUMPING | PF_FALLING);
		pp.Flags &= ~(PF_CRAWLING);
		pp.Flags &= ~(PF_LOCK_CRAWL);

		pp.DoPlayerAction = Player_Action_Func.DoPlayerClimb;

		pp.Flags |= (PF_CLIMBING | PF_WEAPON_DOWN);
		sp.cstat |= (CSTAT_SPRITE_YCENTER);

		NewStateGroup(pp.PlayerSprite, PlayerStateGroup.sg_PlayerNinjaClimb);
	}

	private static final int ADJ_AMT = 8;

	public static void DoPlayerClimb(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		int climb_amt;
		char i;
		SPRITE sp = pp.getSprite();
		int climbvel;
		int dot;
		short wal;
		boolean LadderUpdate = false;

		if (Prediction)
			return;

		pp.xvect += ((pp.input.vel * synctics * 2) << 6);
		pp.yvect += ((pp.input.svel * synctics * 2) << 6);
		pp.xvect = mulscale(pp.xvect, PLAYER_CLIMB_FRICTION, 16);
		pp.yvect = mulscale(pp.yvect, PLAYER_CLIMB_FRICTION, 16);
		if (klabs(pp.xvect) < 12800 && klabs(pp.yvect) < 12800)
			pp.xvect = pp.yvect = 0;

		climbvel = FindDistance2D(pp.xvect, pp.yvect) >> 9;
		dot = DOT_PRODUCT_2D(pp.xvect, pp.yvect, sintable[NORM_ANGLE(pp.getAnglei() + 512)], sintable[pp.getAnglei()]);
		if (dot < 0)
			climbvel = -climbvel;

		// Run lock - routine doesn't call DoPlayerMove
		PLAYER_RUN_LOCK(pp);

		// need to rewrite this for FAF stuff

		// Jump off of the ladder
		if (TEST_SYNC_KEY(pp, SK_JUMP)) {
			pp.Flags &= ~(PF_CLIMBING | PF_WEAPON_DOWN);
			sp.cstat &= ~(CSTAT_SPRITE_YCENTER);
			DoPlayerBeginJump(pp);
			return;
		}

		if (climbvel != 0) {
			// move player to center of ladder
			for (i = synctics; i != 0; i--) {

				// player
				if (pp.posx != pp.lx) {
					if (pp.posx < pp.lx)
						pp.posx += ADJ_AMT;
					else if (pp.posx > pp.lx)
						pp.posx -= ADJ_AMT;

					if (klabs(pp.posx - pp.lx) <= ADJ_AMT)
						pp.posx = pp.lx;
				}

				if (pp.posy != pp.ly) {
					if (pp.posy < pp.ly)
						pp.posy += ADJ_AMT;
					else if (pp.posy > pp.ly)
						pp.posy -= ADJ_AMT;

					if (klabs(pp.posy - pp.ly) <= ADJ_AMT)
						pp.posy = pp.ly;
				}

				// sprite
				if (sp.x != u.sx) {
					if (sp.x < u.sx)
						sp.x += ADJ_AMT;
					else if (sp.x > u.sx)
						sp.x -= ADJ_AMT;

					if (klabs(sp.x - u.sx) <= ADJ_AMT)
						sp.x = u.sx;
				}

				if (sp.y != u.sy) {
					if (sp.y < u.sy)
						sp.y += ADJ_AMT;
					else if (sp.y > u.sy)
						sp.y -= ADJ_AMT;

					if (klabs(sp.y - u.sy) <= ADJ_AMT)
						sp.y = u.sy;
				}
			}
		}

		DoPlayerZrange(pp);

		// moving UP
		if (climbvel > 0) {
			climb_amt = (climbvel >> 4) * 8;

			pp.climb_ndx &= 1023;

			pp.posz -= climb_amt;

			// if player gets to close the ceiling while climbing
			if (PlayerCeilingHit(pp, pp.hiz)) {
				// put player at the hiz
				pp.posz = pp.hiz;
				NewStateGroup(pp.PlayerSprite, PlayerStateGroup.sg_PlayerNinjaClimb);
			}

			// if player gets to close the ceiling while climbing
			if (pp.LadderSector != -1 && PlayerCeilingHit(pp, pp.hiz + Z(4))) {
				// put player at the ceiling
				pp.posz = sector[pp.LadderSector].ceilingz + Z(4);
				NewStateGroup(pp.PlayerSprite, PlayerStateGroup.sg_PlayerNinjaClimb);
			}

			// if floor is ABOVE you && your head goes above it, do a jump up to
			// terrace

			if (pp.LadderSector == -1 || pp.posz < sector[pp.LadderSector].floorz - Z(6)) {
				pp.jump_speed = PLAYER_CLIMB_JUMP_AMT;
				pp.Flags &= ~(PF_CLIMBING | PF_WEAPON_DOWN);
				sp.cstat &= ~(CSTAT_SPRITE_YCENTER);
				DoPlayerBeginForceJump(pp);
			}
		} else
		// move DOWN
		if (climbvel < 0) {
			climb_amt = -(climbvel >> 4) * 8;

			pp.climb_ndx &= 1023;
			pp.posz += climb_amt;

			// if you are touching the floor
			// if (pp.posz >= pp.loz - Z(4) - PLAYER_HEIGHT)
			if (PlayerFloorHit(pp, pp.loz - Z(4) - PLAYER_HEIGHT)) {
				// stand on floor
				pp.posz = pp.loz - Z(4) - PLAYER_HEIGHT;

				// if moving backwards start running
				if (climbvel < 0) {
					pp.Flags &= ~(PF_CLIMBING | PF_WEAPON_DOWN);
					sp.cstat &= ~(CSTAT_SPRITE_YCENTER);
					DoPlayerBeginRun(pp);
					return;
				}
			}
		} else {
			NewStateGroup(pp.PlayerSprite, PlayerStateGroup.sg_PlayerNinjaClimb);
		}

		// setsprite to players location
		sp.z = pp.posz + PLAYER_HEIGHT;
		engine.changespritesect(pp.PlayerSprite, pp.cursectnum);

		DoPlayerHorizon(pp);

		if (FAF_ConnectArea(pp.cursectnum)) {
			pp.cursectnum = engine.updatesectorz(pp.posx, pp.posy, pp.posz, pp.cursectnum);
			LadderUpdate = true;
		}

		if (WarpPlane(pp.posx, pp.posy, pp.posz, pp.cursectnum) != null) {
			pp.posx = warp.x;
			pp.posy = warp.y;
			pp.posz = warp.z;
			pp.cursectnum = warp.sectnum;
			PlayerWarpUpdatePos(pp);
			LadderUpdate = true;
		}

		if (LadderUpdate) {
			SPRITE lsp;
			int nx, ny;

			// constantly look for new ladder sector because of warping at any time
			engine.neartag(pp.posx, pp.posy, pp.posz, pp.cursectnum, pp.getAnglei(), neartag, 800, NTAG_SEARCH_LO_HI);

			wal = neartag.tagwall;

			if (wal >= 0) {
				lsp = sprite[FindNearSprite(pp.getSprite(), STAT_CLIMB_MARKER)];

				// determine where the player is supposed to be in relation to the ladder
				// move out in front of the ladder
				nx = MOVEx(100, lsp.ang);
				ny = MOVEy(100, lsp.ang);

				// set angle player is supposed to face.
				pp.LadderAngle = NORM_ANGLE(lsp.ang + 1024);
				pp.LadderSector = wall[wal].nextsector;

				// set players "view" distance from the ladder - needs to be farther than
				// the sprite

				pp.lx = lsp.x + nx * 5;
				pp.ly = lsp.y + ny * 5;

				pp.pang = pp.LadderAngle;
			}
		}
	}

	private static short angs[] = { 0, 0, 0 };

	public static boolean DoPlayerWadeSuperJump(PlayerStr pp) {
		short i;

		int zh = sector[pp.cursectnum].floorz - Z(pp.WadeDepth) - Z(2);

		if (Prediction)
			return (false); // !JIM! 8/5/97 Teleporter FAFhitscan SuperJump bug.

		for (i = 0; i < 3; i++) {
			FAFhitscan(pp.posx, pp.posy, zh, pp.cursectnum, // Start position
					sintable[NORM_ANGLE(pp.getAnglei() + angs[i] + 512)], // X vector of 3D ang
					sintable[NORM_ANGLE(pp.getAnglei() + angs[i])], // Y vector of 3D ang
					0, // Z vector of 3D ang
					pHitInfo, CLIPMASK_MISSILE);

			if (pHitInfo.hitwall >= 0 && pHitInfo.hitsect >= 0) {
				pHitInfo.hitsect = wall[pHitInfo.hitwall].nextsector;

				if (pHitInfo.hitsect != -1 && klabs(sector[pHitInfo.hitsect].floorz - pp.posz) < Z(50)) {
					if (Distance(pp.posx, pp.posy, pHitInfo.hitx, pHitInfo.hity) < (((pp.getSprite().clipdist) << 2) + 256))
						return (true);
				}
			}
		}

		return (false);
	}

	public static boolean PlayerFlyKey(PlayerStr pp) {
		if (!GodMode)
			return (false);

		if (MessageInputMode)
			return (false);

		boolean key;
		if(key = TEST_SYNC_KEY(pp, SK_FLY))
			RESET_SYNC_KEY(pp, SK_FLY);
		return (key);
	}

	public static void DoPlayerBeginCrawl(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];

		pp.Flags &= ~(PF_FALLING | PF_JUMPING);
		pp.Flags |= (PF_CRAWLING);

		pp.friction = PLAYER_CRAWL_FRICTION;
		pp.floor_dist = PLAYER_CRAWL_FLOOR_DIST;
		pp.ceiling_dist = PLAYER_CRAWL_CEILING_DIST;
		pp.DoPlayerAction = Player_Action_Func.DoPlayerCrawl;

		NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Crawl);
	}

	public static boolean PlayerFallTest(PlayerStr pp, int player_height) {
		// If the floor is far below you, fall hard instead of adjusting height
		if (klabs(pp.posz - pp.loz) > player_height + PLAYER_FALL_HEIGHT) {
			// if on a STEEP slope sector and you have not moved off of the sector
			if (pp.lo_sectp != -1 && klabs(sector[pp.lo_sectp].floorheinum) > 3000
					&& TEST(sector[pp.lo_sectp].floorstat, FLOOR_STAT_SLOPE) && pp.lo_sectp == pp.lastcursectnum) {
				return (false);
			} else {
				return (true);
			}
		}

		return (false);
	}

	public static void DoPlayerCrawl(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];

		if (SectorIsUnderwaterArea(pp.cursectnum)) {
			// if stacked water - which it should be
			if (FAF_ConnectArea(pp.cursectnum)) {
				// adjust the z
				pp.posz = sector[pp.cursectnum].ceilingz + Z(12);
			}

			DoPlayerBeginDiveNoWarp(pp);
		}

		if (TEST(pp.Flags, PF_LOCK_CRAWL)) {
			if (TEST_SYNC_KEY(pp, SK_CRAWL_LOCK)) {
				if (FLAG_KEY_PRESSED(pp, SK_CRAWL_LOCK)) {
					if (klabs(pp.loz - pp.hiz) >= PLAYER_STANDING_ROOM) {
						FLAG_KEY_RELEASE(pp, SK_CRAWL_LOCK);

						pp.Flags &= ~(PF_CRAWLING);
						DoPlayerBeginRun(pp);
						return;
					}
				}
			} else {
				FLAG_KEY_RESET(pp, SK_CRAWL_LOCK);
			}

			// Jump to get up
			if (TEST_SYNC_KEY(pp, SK_JUMP)) {
				if (klabs(pp.loz - pp.hiz) >= PLAYER_STANDING_ROOM) {
					pp.Flags &= ~(PF_CRAWLING);
					DoPlayerBeginRun(pp);
					return;
				}
			}

		} else {
			// Let off of crawl to get up
			if (!TEST_SYNC_KEY(pp, SK_CRAWL)) {
				if (klabs(pp.loz - pp.hiz) >= PLAYER_STANDING_ROOM) {
					pp.Flags &= ~(PF_CRAWLING);
					DoPlayerBeginRun(pp);
					return;
				}
			}
		}

		if (pp.lo_sectp != -1 && TEST(sector[pp.lo_sectp].extra, SECTFX_CURRENT)) {
			DoPlayerCurrent(pp);
		}

		// Move around
		DoPlayerMove(pp);

		if (pp.WadeDepth > PLAYER_CRAWL_WADE_DEPTH) {
			pp.Flags &= ~(PF_CRAWLING);
			DoPlayerBeginRun(pp);
			return;
		}

		if (!TEST(pp.Flags, PF_PLAYER_MOVED)) {

			NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Crawl);
		}

		// If the floor is far below you, fall hard instead of adjusting height
		if (PlayerFallTest(pp, PLAYER_CRAWL_HEIGHT)) {
			pp.jump_speed = Z(1);
			pp.Flags &= ~(PF_CRAWLING);
			DoPlayerBeginFall(pp);
			// call PlayerFall now seems to iron out a hitch before falling
			DoPlayerFall(pp);
			return;
		}

		if (isValidSector(pp.cursectnum) && TEST(sector[pp.cursectnum].extra, SECTFX_DYNAMIC_AREA)) {
			pp.posz = pp.loz - PLAYER_CRAWL_HEIGHT;
		}

		DoPlayerBob(pp);
		DoPlayerCrawlHeight(pp);
	}

	public static void DoPlayerBeginFly(PlayerStr pp) {
		pp.Flags &= ~(PF_FALLING | PF_JUMPING | PF_CRAWLING);
		pp.Flags |= (PF_FLYING);

		pp.friction = PLAYER_FLY_FRICTION;
		pp.floor_dist = PLAYER_RUN_FLOOR_DIST;
		pp.ceiling_dist = PLAYER_RUN_CEILING_DIST;
		pp.DoPlayerAction = Player_Action_Func.DoPlayerFly;

		pp.z_speed = -Z(10);
		pp.jump_speed = 0;
		pp.bob_amt = 0;
		pp.bob_ndx = 1024;

		NewStateGroup(pp.PlayerSprite, PlayerStateGroup.sg_PlayerNinjaFly);
	}

	public static int GetSinNdx(int range, int bob_amt) {
		int amt = Z(512) / range;

		return (bob_amt * amt);
	}

	public static void PlayerWarpUpdatePos(PlayerStr pp) {
		if (Prediction)
			return;

		pp.oposx = pp.posx;
		pp.oposy = pp.posy;
		pp.oposz = pp.posz;
		DoPlayerZrange(pp);
		UpdatePlayerSprite(pp);
	}

	public static boolean PlayerCeilingHit(PlayerStr pp, int zlimit) {
		if (pp.posz < zlimit) {
			return (true);
		}

		return (false);
	}

	public static boolean PlayerFloorHit(PlayerStr pp, int zlimit) {
		if (pp.posz > zlimit) {
			return (true);
		}

		return (false);
	}

	public static void DoPlayerFly(PlayerStr pp) {
		if (SectorIsUnderwaterArea(pp.cursectnum)) {
			DoPlayerBeginDiveNoWarp(pp);
			return;
		}

		if (TEST_SYNC_KEY(pp, SK_CRAWL)) {
			pp.z_speed += PLAYER_FLY_INC;

			if (pp.z_speed > PLAYER_FLY_MAX_SPEED)
				pp.z_speed = PLAYER_FLY_MAX_SPEED;
		}

		if (TEST_SYNC_KEY(pp, SK_JUMP)) {
			pp.z_speed -= PLAYER_FLY_INC;

			if (pp.z_speed < -PLAYER_FLY_MAX_SPEED)
				pp.z_speed = -PLAYER_FLY_MAX_SPEED;
		}

		pp.z_speed = mulscale(pp.z_speed, 58000, 16);

		pp.posz += pp.z_speed;

		// Make the min distance from the ceiling/floor match bobbing amount
		// so the player never goes into the ceiling/floor

		// Only get so close to the ceiling
		if (PlayerCeilingHit(pp, pp.hiz + PLAYER_FLY_BOB_AMT + Z(8))) {
			pp.posz = pp.hiz + PLAYER_FLY_BOB_AMT + Z(8);
			pp.z_speed = 0;
		}

		// Only get so close to the floor
		if (PlayerFloorHit(pp, pp.loz - PLAYER_HEIGHT - PLAYER_FLY_BOB_AMT)) {
			pp.posz = pp.loz - PLAYER_HEIGHT - PLAYER_FLY_BOB_AMT;
			pp.z_speed = 0;
		}

		if (PlayerFlyKey(pp)) {
			pp.Flags &= ~(PF_FLYING);
			pp.bob_amt = 0;
			pp.bob_ndx = 0;
			DoPlayerBeginFall(pp);
			DoPlayerFall(pp);
			return;
		}

		DoPlayerMove(pp);
	}

	public static int FindNearSprite(SPRITE sp, int stat) {
		short fs, next_fs;
		int dist, near_dist = 15000;
		SPRITE fp;
		int near_fp = -1;

		for (fs = headspritestat[stat]; fs != -1; fs = next_fs) {
			next_fs = nextspritestat[fs];
			fp = sprite[fs];

			dist = Distance(sp.x, sp.y, fp.x, fp.y);

			if (dist < near_dist) {
				near_dist = dist;
				near_fp = fs;
			}
		}

		return (near_fp);
	}

	private static final short angles[] = { 30, -30 };

	public static boolean PlayerOnLadder(PlayerStr pp) {
		short wal = -1;
		int dist, i, nx, ny;

		int dir;
		short neartagwall;

		if (Prediction)
			return false;

		engine.neartag(pp.posx, pp.posy, pp.posz, pp.cursectnum, pp.getAnglei(), neartag, 1024 + 768, NTAG_SEARCH_LO_HI);
		neartagwall = neartag.tagwall;

		dir = DOT_PRODUCT_2D(pp.xvect, pp.yvect, sintable[NORM_ANGLE(pp.getAnglei() + 512)], sintable[pp.getAnglei()]);

		if (dir < 0)
			return (false);

		if (neartagwall < 0 || wall[neartagwall].lotag != TAG_WALL_CLIMB)
			return (false);

		for (i = 0; i < angles.length; i++) {
			engine.neartag(pp.posx, pp.posy, pp.posz, pp.cursectnum, NORM_ANGLE(pp.getAnglei() + angles[i]), neartag, 600,
					NTAG_SEARCH_LO_HI);

			wal = neartag.tagwall;
			dist = neartag.taghitdist;

			if (wal < 0 || dist < 100 || wall[wal].lotag != TAG_WALL_CLIMB)
				return (false);

			FAFhitscan(pp.posx, pp.posy, pp.posz, pp.cursectnum, sintable[NORM_ANGLE(pp.getAnglei() + angles[i] + 512)],
					sintable[NORM_ANGLE(pp.getAnglei() + angles[i])], 0, pHitInfo, CLIPMASK_MISSILE);

			dist = DIST(pp.posx, pp.posy, pHitInfo.hitx, pHitInfo.hity);

			if (pHitInfo.hitsprite >= 0) {
				// if the sprite blocking you hit is not a wall sprite there is something
				// between
				// you and the ladder
				if (TEST(sprite[pHitInfo.hitsprite].cstat, CSTAT_SPRITE_BLOCK)
						&& !TEST(sprite[pHitInfo.hitsprite].cstat, CSTAT_SPRITE_WALL)) {
					return (false);
				}
			} else {
				// if you hit a wall and it is not a climb wall - forget it
				if (pHitInfo.hitwall >= 0 && wall[pHitInfo.hitwall].lotag != TAG_WALL_CLIMB)
					return (false);
			}
		}

		int lspi = FindNearSprite(pp.getSprite(), STAT_CLIMB_MARKER);

		if (lspi == -1)
			return (false);

		SPRITE lsp = sprite[lspi];
		// determine where the player is supposed to be in relation to the ladder
		// move out in front of the ladder
		nx = MOVEx(100, lsp.ang);
		ny = MOVEy(100, lsp.ang);

		// set angle player is supposed to face.
		pp.LadderAngle = NORM_ANGLE(lsp.ang + 1024);

		pp.LadderSector = wall[wal].nextsector;

		// set players "view" distance from the ladder - needs to be farther than
		// the sprite

		pp.lx = lsp.x + nx * 5;
		pp.ly = lsp.y + ny * 5;

		pp.pang = pp.LadderAngle;

		return (true);
	}

	public static boolean DoPlayerTestCrawl(PlayerStr pp) {
		if (klabs(pp.loz - pp.hiz) < PLAYER_STANDING_ROOM)
			return (true);

		return (false);
	}

	public static boolean PlayerInDiveArea(PlayerStr pp) {
		SECTOR sectp;
		if (pp.lo_sectp != -1) {
			// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			// Attention: This changed on 07/29/97
			// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			sectp = sector[pp.cursectnum];
		} else
			return (false);

		if (TEST(sectp.extra, SECTFX_DIVE_AREA)) {
			CheckFootPrints(pp);
			return (true);
		}

		return (false);
	}

	public static boolean PlayerCanDive(PlayerStr pp) {
		if (Prediction)
			return (false);

		// Crawl - check for diving
		if (TEST_SYNC_KEY(pp, SK_CRAWL) || TEST_SYNC_KEY(pp, SK_CRAWL_LOCK) || pp.jump_speed > 0) {
			if (PlayerInDiveArea(pp)) {
				pp.posz += Z(20);
				pp.z_speed = Z(20);
				pp.jump_speed = 0;

				if (pp.posz > pp.loz - Z(pp.WadeDepth) - Z(2)) {
					DoPlayerBeginDive(pp);
				}

				return (true);
			}
		}

		return (false);
	}

	public static boolean PlayerCanDiveNoWarp(PlayerStr pp) {
		if (Prediction)
			return (false);

		// check for diving
		if (pp.jump_speed > 1400) {
			if (FAF_ConnectArea(pp.cursectnum)) {
				short sectnum = pp.cursectnum;

				sectnum = engine.updatesectorz(pp.posx, pp.posy, SPRITEp_BOS(pp.getSprite()), sectnum);

				if (SectorIsUnderwaterArea(sectnum)) {
					pp.cursectnum = sectnum;
					pp.posz = sector[sectnum].ceilingz;

					pp.posz += Z(20);
					pp.z_speed = Z(20);
					pp.jump_speed = 0;

					PlaySound(DIGI_SPLASH1, pp, v3df_dontpan);
					DoPlayerBeginDiveNoWarp(pp);
					return (true);
				}
			}
		}

		return (false);
	}

	private static short sf[] = { 0, 0 }; // sectors found

	public static int GetOverlapSector(int x, int y, LONGp over, LONGp under) {
		int i, found = 0;
		if ((SectUser[under.value] != null && SectUser[under.value].number >= 30000)
				|| (SectUser[over.value] != null && SectUser[over.value].number >= 30000))
			return (GetOverlapSector2(x, y, over, under));

		// instead of check ALL sectors, just check the two most likely first
		if (engine.inside(x, y, (short) over.value) != 0) {
			sf[found] = (short) over.value;
			found++;
		}

		if (engine.inside(x, y, (short) under.value) != 0) {
			sf[found] = (short) under.value;
			found++;
		}

		// if nothing was found, check them all
		if (found == 0) {
			for (i = 0; i < numsectors && found < 2; i++) {
				if (engine.inside(x, y, (short) i) != 0) {
					sf[found] = (short) i;
					found++;
				}
			}
		}

		if (found == 0) {
            return 0;
		}

		// the are overlaping - check the z coord
		if (found == 2) {
			if (sector[sf[0]].floorz > sector[sf[1]].floorz) {
				under.value = sf[0];
				over.value = sf[1];
			} else {
				under.value = sf[1];
				over.value = sf[0];
			}
		} else
		// the are NOT overlaping
		{
			over.value = sf[0];
			under.value = -1;
		}

		return (found);
	}

	private static short UnderStatList[] = { STAT_UNDERWATER, STAT_UNDERWATER2 };

	private static int GetOverlapSector2(int x, int y, LONGp over, LONGp under) {
		int i, nexti, found = 0;

		short stat;

		// NOTE: For certain heavily overlapped areas in $seabase this is a better
		// method.

		// instead of check ALL sectors, just check the two most likely first
		if (engine.inside(x, y, (short) over.value) != 0) {
			sf[found] = (short) over.value;
			found++;
		}

		if (engine.inside(x, y, (short) under.value) != 0) {
			sf[found] = (short) under.value;
			found++;
		}

		// if nothing was found, check them all
		if (found == 0) {
			for (i = headspritestat[STAT_DIVE_AREA]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				if (engine.inside(x, y, sprite[i].sectnum) != 0) {
					sf[found] = sprite[i].sectnum;
					found++;
				}
			}

			for (stat = 0; stat < UnderStatList.length; stat++) {
				for (i = headspritestat[UnderStatList[stat]]; i != -1; i = nexti) {
					nexti = nextspritestat[i];
					// ignore underwater areas with lotag of 0
					if (sprite[i].lotag == 0)
						continue;

					if (engine.inside(x, y, sprite[i].sectnum) != 0) {
						sf[found] = sprite[i].sectnum;
						found++;
					}
				}
			}
		}

		if (found == 0) {
            return 0;
		}

		// the are overlaping - check the z coord
		if (found == 2) {
			if (sector[sf[0]].floorz > sector[sf[1]].floorz) {
				under.value = sf[0];
				over.value = sf[1];
			} else {
				under.value = sf[1];
				over.value = sf[0];
			}
		} else
		// the are NOT overlaping
		{
			over.value = sf[0];
			under.value = -1;
		}

		return (found);
	}

	public static void DoPlayerWarpToUnderwater(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		short i, nexti;
		Sect_User sectu = SectUser[pp.cursectnum];
		SPRITE under_sp = null, over_sp = null;
		boolean Found = false;

		if (Prediction)
			return;

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
			return;

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
			return;

		// get the offset from the sprite
		u.sx = over_sp.x - pp.posx;
		u.sy = over_sp.y - pp.posy;

		// update to the new x y position
		pp.posx = under_sp.x - u.sx;
		pp.posy = under_sp.y - u.sy;

		if (GetOverlapSector(pp.posx, pp.posy, tmp_ptr[0].set(over_sp.sectnum),
				tmp_ptr[1].set(under_sp.sectnum)) == 2) {
			pp.cursectnum = (short) tmp_ptr[1].value;
		} else
			pp.cursectnum = (short) tmp_ptr[0].value;

		pp.posz = sector[under_sp.sectnum].ceilingz + Z(6);

		pp.oposx = pp.posx;
		pp.oposy = pp.posy;
		pp.oposz = pp.posz;

		DoPlayerZrange(pp);
		return;
	}

	public static void DoPlayerWarpToSurface(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		short i, nexti;
		Sect_User sectu = SectUser[pp.cursectnum];

		SPRITE under_sp = null, over_sp = null;
		boolean Found = false;

		if (Prediction)
			return;

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
			return;

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
		u.sx = under_sp.x - pp.posx;
		u.sy = under_sp.y - pp.posy;

		// update to the new x y position
		pp.posx = over_sp.x - u.sx;
		pp.posy = over_sp.y - u.sy;

		if (GetOverlapSector(pp.posx, pp.posy, tmp_ptr[0].set(over_sp.sectnum), tmp_ptr[1].set(under_sp.sectnum)) != 0)
			pp.cursectnum = (short) tmp_ptr[0].value;

		pp.posz = sector[over_sp.sectnum].floorz - Z(2);

		// set z range and wade depth so we know how high to set view
		DoPlayerZrange(pp);
		DoPlayerSetWadeDepth(pp);

		pp.posz -= Z(pp.WadeDepth);

		pp.oposx = pp.posx;
		pp.oposy = pp.posy;
		pp.oposz = pp.posz;

		return;
	}

	public static void DoPlayerBeginDive(PlayerStr pp) {
		SPRITE sp = sprite[pp.PlayerSprite];
		USER u = pUser[pp.PlayerSprite];

		if (Prediction)
			return;

		if (pp.Bloody)
			pp.Bloody = false; // Water washes away the blood

		pp.Flags |= (PF_DIVING);
		DoPlayerDivePalette(pp);
		DoPlayerNightVisionPalette(pp);

		if (pp == Player[screenpeek]) {
			COVER_SetReverb(140); // Underwater echo
			pp.Reverb = 140;
		}

		SpawnSplash(pp.PlayerSprite);

		DoPlayerWarpToUnderwater(pp);
		OperateTripTrigger(pp);

		pp.Flags &= ~(PF_JUMPING | PF_FALLING);
		pp.Flags &= ~(PF_CRAWLING);
		pp.Flags &= ~(PF_LOCK_CRAWL);

		pp.friction = PLAYER_DIVE_FRICTION;
		pp.ceiling_dist = PLAYER_DIVE_CEILING_DIST;
		pp.floor_dist = PLAYER_DIVE_FLOOR_DIST;
		sp.cstat |= (CSTAT_SPRITE_YCENTER);
		pp.DoPlayerAction = Player_Action_Func.DoPlayerDive;

		pp.DiveTics = PLAYER_DIVE_TIME;
		pp.DiveDamageTics = 0;

		DoPlayerMove(pp); // needs to be called to reset the pp.loz/hiz variable

		NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Dive);

		DoPlayerDive(pp);
	}

	public static void DoPlayerBeginDiveNoWarp(PlayerStr pp) {
		SPRITE sp = sprite[pp.PlayerSprite];
		USER u = pUser[pp.PlayerSprite];

		if (Prediction)
			return;

		if (!SectorIsUnderwaterArea(pp.cursectnum))
			return;

		if (pp.Bloody)
			pp.Bloody = false; // Water washes away the blood

		if (pp == Player[screenpeek]) {
			COVER_SetReverb(140); // Underwater echo
			pp.Reverb = 140;
		}

		CheckFootPrints(pp);

		if (pp.lo_sectp != -1 && DTEST(sector[pp.lo_sectp].extra, SECTFX_LIQUID_MASK) == SECTFX_LIQUID_LAVA) {
			pp.Flags |= (PF_DIVING_IN_LAVA);
			u.DamageTics = 0;
		}

		pp.Flags |= (PF_DIVING);
		DoPlayerDivePalette(pp);
		DoPlayerNightVisionPalette(pp);

		pp.Flags &= ~(PF_JUMPING | PF_FALLING);

		pp.friction = PLAYER_DIVE_FRICTION;
		pp.ceiling_dist = PLAYER_DIVE_CEILING_DIST;
		pp.floor_dist = PLAYER_DIVE_FLOOR_DIST;
		sp.cstat |= (CSTAT_SPRITE_YCENTER);
		pp.DoPlayerAction = Player_Action_Func.DoPlayerDive;
		pp.z_speed = 0;
		pp.DiveTics = PLAYER_DIVE_TIME;
		pp.DiveDamageTics = 0;
		DoPlayerMove(pp); // needs to be called to reset the pp.loz/hiz variable

		NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Dive);
		DoPlayerDive(pp);
	}

	public static void DoPlayerStopDiveNoWarp(PlayerStr pp) {
		if (Prediction)
			return;

		if (!NoMeters)
			SetRedrawScreen(pp);

		if (pp.TalkVocHandle != null && pp.TalkVocHandle.isActive()) {
			pp.TalkVocHandle.stop();
			pp.TalkVocHandle = null;
			pp.TalkVocnum = -1;
			pp.PlayerTalking = false;
		}

		// stop diving no warp
		PlayerSound(DIGI_SURFACE, v3df_dontpan | v3df_follow | v3df_doppler, pp);

		pp.bob_amt = 0;

		pp.Flags &= ~(PF_DIVING | PF_DIVING_IN_LAVA);
		DoPlayerDivePalette(pp);
		DoPlayerNightVisionPalette(pp);
		pp.getSprite().cstat &= ~(CSTAT_SPRITE_YCENTER);
		if (pp == Player[screenpeek]) {
			COVER_SetReverb(0);
			pp.Reverb = 0;
		}

		DoPlayerZrange(pp);
	}

	public static void DoPlayerStopDive(PlayerStr pp) {
		SPRITE sp = sprite[pp.PlayerSprite];

		if (Prediction)
			return;

		if (!NoMeters)
			SetRedrawScreen(pp);

		if (pp.TalkVocHandle != null && pp.TalkVocHandle.isActive()) {
			pp.TalkVocHandle.stop();
		}
		pp.TalkVocnum = -1;
		pp.TalkVocHandle = null;
		pp.PlayerTalking = false;

		// stop diving with warp
		PlayerSound(DIGI_SURFACE, v3df_dontpan | v3df_follow | v3df_doppler, pp);

		pp.bob_amt = 0;
		DoPlayerWarpToSurface(pp);
		DoPlayerBeginWade(pp);
		pp.Flags &= ~(PF_DIVING | PF_DIVING_IN_LAVA);

		DoPlayerDivePalette(pp);
		DoPlayerNightVisionPalette(pp);
		sp.cstat &= ~(CSTAT_SPRITE_YCENTER);
		if (pp == Player[screenpeek]) {
			COVER_SetReverb(0);
			pp.Reverb = 0;
		}
	}

	public static void DoPlayerDiveMeter(PlayerStr pp) {
		short color = 0, metertics, meterunit;
		int y;

		if (NoMeters)
			return;

		// Don't draw bar from other players
		if (pp != Player[myconnectindex])
			return;

		if (!TEST(pp.Flags, PF_DIVING | PF_DIVING_IN_LAVA))
			return;

		meterunit = PLAYER_DIVE_TIME / 30;
		if (meterunit > 0)
			metertics = (short) (pp.DiveTics / meterunit);
		else
			return;

		if (metertics <= 0 && !TEST(pp.Flags, PF_DIVING | PF_DIVING_IN_LAVA)) {
			SetRedrawScreen(pp);
			return;
		}

		if (metertics <= 0)
			return;

		if (numplayers < 2)
			y = 10;
		else if (numplayers >= 2 && numplayers <= 4)
			y = 20;
		else
			y = 30;

		if (metertics <= 12 && metertics > 6)
			color = 20;
		else if (metertics <= 6)
			color = 25;
		else
			color = 22;

		engine.rotatesprite((200 + 8) << 16, y << 16, 65536, 0, 5408, 1, 1, (ROTATE_SPRITE_SCREEN_CLIP), 0, 0, xdim - 1,
				ydim - 1);

		engine.rotatesprite((218 + 47) << 16, y << 16, 65536, 0, 5406 - metertics, 1, color,
				(ROTATE_SPRITE_SCREEN_CLIP), 0, 0, xdim - 1, ydim - 1);
	}

	public static void DoPlayerDive(PlayerStr pp) {
		if (!isValidSector(pp.cursectnum))
				return;

		USER u = pUser[pp.PlayerSprite];
		Sect_User sectu = SectUser[pp.cursectnum];

		// whenever your view is not in a water area
		if (!SectorIsUnderwaterArea(pp.cursectnum)) {
			DoPlayerStopDiveNoWarp(pp);
			DoPlayerBeginRun(pp);
			return;
		}

		if ((pp.DiveTics -= synctics) < 0) {
			if ((pp.DiveDamageTics -= synctics) < 0) {
				pp.DiveDamageTics = PLAYER_DIVE_DAMAGE_TIME;
				// PlayerUpdateHealth(pp, PLAYER_DIVE_DAMAGE_AMOUNT);
				PlayerSound(DIGI_WANGDROWNING, v3df_dontpan | v3df_follow, pp);
				PlayerUpdateHealth(pp, -3 - (RANDOM_RANGE(7 << 8) >> 8));
				PlayerCheckDeath(pp, -1);
				if (TEST(pp.Flags, PF_DEAD))
					return;
			}
		}

		// underwater current
		if (pp.lo_sectp != -1 && TEST(sector[pp.lo_sectp].extra, SECTFX_CURRENT)) {
			DoPlayerCurrent(pp);
		}

		// while diving in lava
		// every DamageTics time take some damage
		if (TEST(pp.Flags, PF_DIVING_IN_LAVA)) {
			if ((u.DamageTics -= synctics) < 0) {
				u.DamageTics = 30; // !JIM! Was DAMAGE_TIME

				PlayerUpdateHealth(pp, -40);
			}
		}

		if (TEST_SYNC_KEY(pp, SK_CRAWL) || TEST_SYNC_KEY(pp, SK_CRAWL_LOCK)) {
			pp.z_speed += PLAYER_DIVE_INC;

			if (pp.z_speed > PLAYER_DIVE_MAX_SPEED)
				pp.z_speed = PLAYER_DIVE_MAX_SPEED;
		}

		if (TEST_SYNC_KEY(pp, SK_JUMP)) {
			pp.z_speed -= PLAYER_DIVE_INC;

			if (pp.z_speed < -PLAYER_DIVE_MAX_SPEED)
				pp.z_speed = -PLAYER_DIVE_MAX_SPEED;
		}

		pp.z_speed = mulscale(pp.z_speed, 58000, 16);

		if (klabs(pp.z_speed) < 16)
			pp.z_speed = 0;

		pp.posz += pp.z_speed;

		if (pp.z_speed < 0 && FAF_ConnectArea(pp.cursectnum)) {
			if (pp.posz < sector[pp.cursectnum].ceilingz + Z(10)) {
				short sectnum = pp.cursectnum;

				// check for sector above to see if it is an underwater sector also
				sectnum = engine.updatesectorz(pp.posx, pp.posy, sector[pp.cursectnum].ceilingz - Z(8), sectnum);

				if (sectnum >= 0 && !SectorIsUnderwaterArea(sectnum)) {
					// if not underwater sector we must surface
					// force into above sector
					pp.posz = sector[pp.cursectnum].ceilingz - Z(8);
					pp.cursectnum = sectnum;
					DoPlayerStopDiveNoWarp(pp);
					DoPlayerBeginRun(pp);
					return;
				}
			}
		}

		// Only get so close to the ceiling
		// if its a dive sector without a match or a UNDER2 sector with CANT_SURFACE set
		if (sectu != null && (sectu.number == 0 || TEST(sectu.flags, SECTFU_CANT_SURFACE))) {
			// for room over room water the hiz will be the top rooms ceiling
			if (pp.posz < pp.hiz + pp.ceiling_dist) {
				pp.posz = pp.hiz + pp.ceiling_dist;
			}
		} else {
			// close to a warping sector - stop diveing with a warp to surface
			// !JIM! FRANK - I added !pp.hi_sp so that you don't warp to surface when
			// there is a sprite above you since getzrange returns a hiz < ceiling height
			// if you are clipping into a sprite and not the ceiling.
			if (pp.posz < pp.hiz + Z(4) && pp.hi_sp == -1) {
				DoPlayerStopDive(pp);
				return;
			}
		}

		// Only get so close to the floor
		if (pp.posz >= pp.loz - PLAYER_DIVE_HEIGHT) {
			pp.posz = pp.loz - PLAYER_DIVE_HEIGHT;
		}

		// make player bob if sitting still
		if (PLAYER_MOVING(pp) == 0 && pp.z_speed == 0 && pp.up_speed == 0) {
			DoPlayerSpriteBob(pp, PLAYER_DIVE_HEIGHT, PLAYER_DIVE_BOB_AMT, 3);
		}
		// player is moving
		else {
			// if bob_amt is approx 0
			if (klabs(pp.bob_amt) < Z(1)) {
				pp.bob_amt = 0;
				pp.bob_ndx = 0;
			}
			// else keep bobbing until its back close to 0
			else {
				DoPlayerSpriteBob(pp, PLAYER_DIVE_HEIGHT, PLAYER_DIVE_BOB_AMT, 3);
			}
		}

		// Reverse bobbing when getting close to the floor
		if (pp.posz + pp.bob_amt >= pp.loz - PLAYER_DIVE_HEIGHT) {
			pp.bob_ndx = NORM_ANGLE(pp.bob_ndx + ((1024 + 512) - pp.bob_ndx) * 2);
			DoPlayerSpriteBob(pp, PLAYER_DIVE_HEIGHT, PLAYER_DIVE_BOB_AMT, 3);
		}
		// Reverse bobbing when getting close to the ceiling
		if (pp.posz + pp.bob_amt < pp.hiz + pp.ceiling_dist) {
			pp.bob_ndx = NORM_ANGLE(pp.bob_ndx + ((512) - pp.bob_ndx) * 2);
			DoPlayerSpriteBob(pp, PLAYER_DIVE_HEIGHT, PLAYER_DIVE_BOB_AMT, 3);
		}

		DoPlayerMove(pp);

		if (!Prediction && pp.z_speed != 0 && ((RANDOM_P2(1024 << 5) >> 5) < 64)
				|| (PLAYER_MOVING(pp) != 0 && (RANDOM_P2(1024 << 5) >> 5) < 64)) {
			short bubble;
			SPRITE bp;
			int nx, ny;

			PlaySound(DIGI_BUBBLES, pp, v3df_none);
			bubble = (short) SpawnBubble(pp.PlayerSprite);
			if (bubble >= 0) {
				bp = sprite[bubble];

				// back it up a bit to get it out of your face
				nx = MOVEx((128 + 64), NORM_ANGLE(bp.ang + 1024));
				ny = MOVEy((128 + 64), NORM_ANGLE(bp.ang + 1024));

				move_sprite(bubble, nx, ny, 0, u.ceiling_dist, u.floor_dist, 0, synctics);
			}
		}
	}

	public static boolean DoPlayerTestPlaxDeath(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];

		// landed on a paralax floor
		if (pp.lo_sectp != -1 && TEST(sector[pp.lo_sectp].floorstat, FLOOR_STAT_PLAX)) {
			PlayerUpdateHealth(pp, -u.Health);
			PlayerCheckDeath(pp, -1);
			return (true);
		}

		return (false);
	}

	private static void DoPlayerCurrent(PlayerStr pp) {
		if (!isValidSector(pp.cursectnum))
			return;

		int xvect, yvect;
		Sect_User sectu = SectUser[pp.cursectnum];
		int push_ret;

		if (sectu == null)
			return;

		SPRITE psp = pp.getSprite();
		xvect = sectu.speed * synctics * sintable[NORM_ANGLE(sectu.ang + 512)] >> 4;
		yvect = sectu.speed * synctics * sintable[sectu.ang] >> 4;

		push_ret = engine.pushmove(pp.posx, pp.posy, pp.posz, pp.cursectnum, (psp.clipdist << 2),
				pp.ceiling_dist, pp.floor_dist, CLIPMASK_PLAYER);

		if (pushmove_sectnum != -1) {
			pp.posx = pushmove_x;
			pp.posy = pushmove_y;
			pp.posz = pushmove_z;
			pp.cursectnum = pushmove_sectnum;
		}

		if (push_ret < 0) {
			if (!TEST(pp.Flags, PF_DEAD)) {
				USER u = pUser[pp.PlayerSprite];

				PlayerUpdateHealth(pp, -u.Health); // Make sure he dies!
				PlayerCheckDeath(pp, -1);

				if (TEST(pp.Flags, PF_DEAD))
					return;
			}
			return;
		}
		engine.clipmove(pp.posx, pp.posy, pp.posz, pp.cursectnum, xvect, yvect, (psp.clipdist << 2),
				pp.ceiling_dist, pp.floor_dist, CLIPMASK_PLAYER);

		if (clipmove_sectnum != -1) {
			pp.posx = clipmove_x;
			pp.posy = clipmove_y;
			pp.posz = clipmove_z;
			pp.cursectnum = clipmove_sectnum;
		}

		PlayerCheckValidMove(pp);
		engine.pushmove(pp.posx, pp.posy, pp.posz, pp.cursectnum, (psp.clipdist << 2), pp.ceiling_dist,
				pp.floor_dist, CLIPMASK_PLAYER);

		if (pushmove_sectnum != -1) {
			pp.posx = pushmove_x;
			pp.posy = pushmove_y;
			pp.posz = pushmove_z;
			pp.cursectnum = pushmove_sectnum;
		}

		if (push_ret < 0) {
			if (!TEST(pp.Flags, PF_DEAD)) {
				USER u = pUser[pp.PlayerSprite];

				PlayerUpdateHealth(pp, -u.Health); // Make sure he dies!
				PlayerCheckDeath(pp, -1);

				if (TEST(pp.Flags, PF_DEAD))
					return;
			}
			return;
		}
	}

	public static void DoPlayerFireOutWater(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];

		if (Prediction)
			return;

		if (pp.WadeDepth > 20) {
			if (u.flame >= 0)
				SetSuicide(u.flame);
			u.flame = -2;
		}
	}

	public static void DoPlayerFireOutDeath(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];

		if (Prediction)
			return;

		if (u.flame >= 0)
			SetSuicide(u.flame);

		u.flame = -2;
	}

	public static void DoPlayerBeginWade(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];

		// landed on a paralax floor?
		if (DoPlayerTestPlaxDeath(pp))
			return;

		pp.Flags &= ~(PF_JUMPING | PF_FALLING);
		pp.Flags &= ~(PF_CRAWLING);

		pp.friction = PLAYER_WADE_FRICTION;
		pp.floor_dist = PLAYER_WADE_FLOOR_DIST;
		pp.ceiling_dist = PLAYER_WADE_CEILING_DIST;
		pp.DoPlayerAction = Player_Action_Func.DoPlayerWade;

		DoPlayerFireOutWater(pp);

		if (pp.jump_speed > 100)
			SpawnSplash(pp.PlayerSprite);

		// fix it so that you won't go under water unless you hit the water at a
		// certain speed
		if (pp.jump_speed > 0 && pp.jump_speed < 1300)
			pp.jump_speed = 0;

		NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Run);
	}

	public static void DoPlayerWade(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];

		DoPlayerFireOutWater(pp);

		if (TEST_SYNC_KEY(pp, SK_OPERATE)) {
			if (FLAG_KEY_PRESSED(pp, SK_OPERATE)) {
				if (isValidSector(pp.cursectnum) && TEST(sector[pp.cursectnum].extra, SECTFX_OPERATIONAL)) {
					FLAG_KEY_RELEASE(pp, SK_OPERATE);
					DoPlayerBeginOperate(pp);
					pp.bob_amt = 0;
					pp.bob_ndx = 0;
					return;
				}
			}
		} else {
			FLAG_KEY_RESET(pp, SK_OPERATE);
		}

		// Crawl if in small area automatically
		if (DoPlayerTestCrawl(pp) && pp.WadeDepth <= PLAYER_CRAWL_WADE_DEPTH) {
			DoPlayerBeginCrawl(pp);
			return;
		}

		// Crawl Commanded
		if (TEST_SYNC_KEY(pp, SK_CRAWL) && pp.WadeDepth <= PLAYER_CRAWL_WADE_DEPTH) {
			DoPlayerBeginCrawl(pp);
			return;
		}

		if (TEST_SYNC_KEY(pp, SK_JUMP)) {
			if (FLAG_KEY_PRESSED(pp, SK_JUMP)) {
				FLAG_KEY_RELEASE(pp, SK_JUMP);
				DoPlayerBeginJump(pp);
				pp.bob_amt = 0;
				pp.bob_ndx = 0;
				return;
			}
		} else {
			FLAG_KEY_RESET(pp, SK_JUMP);
		}

		if (PlayerFlyKey(pp)) {
			DoPlayerBeginFly(pp);
			pp.bob_amt = 0;
			pp.bob_ndx = 0;
			return;
		}

		// If moving forward and tag is a ladder start climbing
		if (PlayerOnLadder(pp)) {
			DoPlayerBeginClimb(pp);
			return;
		}

		if (pp.lo_sectp != -1 && TEST(sector[pp.lo_sectp].extra, SECTFX_CURRENT)) {
			DoPlayerCurrent(pp);
		}

		// Move about
		DoPlayerMove(pp);

		if (TEST(pp.Flags, PF_PLAYER_MOVED)) {
			if (u.Rot != u.ActorActionSet.Run)
				NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Run);
		} else {
			if (u.Rot != u.ActorActionSet.Stand)
				NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Stand);
		}

		// If the floor is far below you, fall hard instead of adjusting height
		if (klabs(pp.posz - pp.loz) > PLAYER_HEIGHT + PLAYER_FALL_HEIGHT) {
			pp.jump_speed = Z(1);
			DoPlayerBeginFall(pp);
			// call PlayerFall now seems to iron out a hitch before falling
			DoPlayerFall(pp);
			return;
		}

		if (PlayerCanDive(pp)) {
			pp.bob_amt = 0;
			pp.bob_ndx = 0;
			return;
		}

		// If the floor is far below you, fall hard instead of adjusting height
		if (klabs(pp.posz - pp.loz) > PLAYER_HEIGHT + PLAYER_FALL_HEIGHT) {
			pp.jump_speed = Z(1);
			DoPlayerBeginFall(pp);
			// call PlayerFall now seems to iron out a hitch before falling
			DoPlayerFall(pp);
			pp.bob_amt = 0;
			pp.bob_ndx = 0;
			return;
		}

		DoPlayerBob(pp);

		// Adjust height moving up and down sectors
		DoPlayerHeight(pp);

		if (pp.WadeDepth == 0) {
			DoPlayerBeginRun(pp);
			return;
		}
	}

	public static void DoPlayerBeginOperateBoat(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];

		pp.floor_dist = PLAYER_RUN_FLOOR_DIST;
		pp.ceiling_dist = PLAYER_RUN_CEILING_DIST;
		pp.DoPlayerAction = Player_Action_Func.DoPlayerOperateBoat;

		// temporary set to get weapons down
		if (TEST(SectorObject[pp.sop].flags, SOBJ_HAS_WEAPON))
			pp.Flags |= (PF_WEAPON_DOWN);

		NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Run);
	}

	public static void DoPlayerBeginOperateTank(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];

		pp.floor_dist = PLAYER_RUN_FLOOR_DIST;
		pp.ceiling_dist = PLAYER_RUN_CEILING_DIST;
		pp.DoPlayerAction = Player_Action_Func.DoPlayerOperateTank;

		// temporary set to get weapons down
		if (TEST(SectorObject[pp.sop].flags, SOBJ_HAS_WEAPON))
			pp.Flags |= (PF_WEAPON_DOWN);

		NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Stand);
	}

	public static void DoPlayerBeginOperateTurret(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];

		pp.floor_dist = PLAYER_RUN_FLOOR_DIST;
		pp.ceiling_dist = PLAYER_RUN_CEILING_DIST;
		pp.DoPlayerAction = Player_Action_Func.DoPlayerOperateTurret;

		// temporary set to get weapons down
		if (TEST(SectorObject[pp.sop].flags, SOBJ_HAS_WEAPON))
			pp.Flags |= (PF_WEAPON_DOWN);
		NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Stand);
	}

	public static void FindMainSector(int sopi) {
		Sector_Object sop = SectorObject[sopi];

		// find the main sector - only do this once for each sector object
		if (sop.op_main_sector < 0) {
			int sx = sop.xmid;
			int sy = sop.ymid;

			PlaceSectorObject(sopi, sop.ang, MAXSO, MAXSO);

			// set it to something valid
			sop.op_main_sector = 0;

			sop.op_main_sector = engine.updatesectorz(sx, sy, sop.zmid, sop.op_main_sector);

			PlaceSectorObject(sopi, sop.ang, sx, sy);
		}
	}

	public static void DoPlayerOperateMatch(PlayerStr pp, boolean starting) {
		SPRITE sp;
		short i, nexti;

		if (pp.sop == -1)
			return;

		Sector_Object sop = SectorObject[pp.sop];
		if(sop == null)
			return;

		for (i = headspritesect[sop.mid_sector]; i != -1; i = nexti) {
			nexti = nextspritesect[i];
			sp = sprite[i];

			if (sp.statnum == STAT_ST1 && sp.hitag == SO_DRIVABLE_ATTRIB) {
				if (starting) {
					if (SP_TAG5(sp) != 0)
						DoMatchEverything(pp, SP_TAG5(sp), -1);
				} else {
					if (TEST_BOOL2(sp) && SP_TAG5(sp) != 0)
						DoMatchEverything(pp, SP_TAG5(sp) + 1, -1);
				}
				break;
			}
		}
	}

	public static void DoPlayerBeginOperate(PlayerStr pp) {

		int fz;
		int i;

		int sopi = PlayerOnObject(pp.cursectnum);

		if (sopi == -1) {
			DoPlayerBeginRun(pp);
			return;
		}

		Sector_Object sop = SectorObject[sopi];

		// if someone already controlling it
		if (sop.controller != -1)
			return;

		if (TEST(sop.flags, SOBJ_REMOTE_ONLY))
			return;

		// won't operate - broken
		if (sop.max_damage != -9999 && sop.max_damage <= 0) {
			if (pp.InventoryAmount[INVENTORY_REPAIR_KIT] != 0) {
				UseInventoryRepairKit(pp);
				sop.max_damage = pUser[sop.sp_child].MaxHealth;
				VehicleSetSmoke(sop, null);
				sop.flags &= ~(SOBJ_BROKEN);
			} else {
				PlayerSound(DIGI_USEBROKENVEHICLE, v3df_follow | v3df_dontpan, pp);
				return;
			}
		}
		pp.sop = pp.sop_control = sopi;
		sop.controller = pp.PlayerSprite;

		pp.pang = sop.ang;
		pp.posx = sop.xmid;
		pp.posy = sop.ymid;
		pp.cursectnum = COVERupdatesector(pp.posx, pp.posy, pp.cursectnum);
		engine.getzsofslope(pp.cursectnum, pp.posx, pp.posy, zofslope);
		fz = zofslope[FLOOR];
		pp.posz = fz - PLAYER_HEIGHT;

		pp.Flags &= ~(PF_CRAWLING | PF_JUMPING | PF_FALLING | PF_LOCK_CRAWL);

		DoPlayerOperateMatch(pp, true);

		// look for gun before trying to using it
		for (i = 0; sop.sp_num[i] != -1; i++) {
			if (sprite[sop.sp_num[i]].statnum == STAT_SO_SHOOT_POINT) {
				sop.flags |= (SOBJ_HAS_WEAPON);
				break;
			}
		}

		DoPlayerResetMovement(pp);

		switch (sop.track) {
		case SO_TANK:
			if ((pp.input.vel | pp.input.svel) != 0)
				PlaySOsound(sop.mid_sector, SO_DRIVE_SOUND);
			else
				PlaySOsound(sop.mid_sector, SO_IDLE_SOUND);
			pp.posz = fz - PLAYER_HEIGHT;
			DoPlayerBeginOperateTank(pp);
			break;
		case SO_TURRET_MGUN:
		case SO_TURRET:
			if (pp.input.angvel != 0)
				PlaySOsound(sop.mid_sector, SO_DRIVE_SOUND);
			else
				PlaySOsound(sop.mid_sector, SO_IDLE_SOUND);
			pp.posz = fz - PLAYER_HEIGHT;
			DoPlayerBeginOperateTurret(pp);
			break;
		case SO_SPEED_BOAT:
			if ((pp.input.vel | pp.input.svel) != 0)
				PlaySOsound(sop.mid_sector, SO_DRIVE_SOUND);
			else
				PlaySOsound(sop.mid_sector, SO_IDLE_SOUND);
			pp.posz = fz - PLAYER_HEIGHT;
			DoPlayerBeginOperateBoat(pp);
			break;
		default:
			return;
		}

	}

	public static void DoPlayerBeginRemoteOperate(PlayerStr pp, int sopi) {
		int fz;
		int i;
		short save_sectnum;

		Sector_Object sop = SectorObject[sopi];

		pp.sop = pp.sop_remote = pp.sop_control = sopi;
		sop.controller = pp.PlayerSprite;

		// won't operate - broken
		if (sop.max_damage != -9999 && sop.max_damage <= 0) {
			if (pp.InventoryAmount[INVENTORY_REPAIR_KIT] != 0) {
				UseInventoryRepairKit(pp);
				sop.max_damage = pUser[sop.sp_child].MaxHealth;
				VehicleSetSmoke(sop, null);
				sop.flags &= ~(SOBJ_BROKEN);
			} else {
				PlayerSound(DIGI_USEBROKENVEHICLE, v3df_follow | v3df_dontpan, pp);
				return;
			}
		}

		save_sectnum = pp.cursectnum;

		pp.pang = sop.ang;
		pp.posx = sop.xmid;
		pp.posy = sop.ymid;
		pp.cursectnum = COVERupdatesector(pp.posx, pp.posy, pp.cursectnum);
		engine.getzsofslope(pp.cursectnum, pp.posx, pp.posy, zofslope);
		fz = zofslope[FLOOR];
		pp.posz = fz - PLAYER_HEIGHT;

		pp.Flags &= ~(PF_CRAWLING | PF_JUMPING | PF_FALLING | PF_LOCK_CRAWL);

		DoPlayerOperateMatch(pp, true);

		// look for gun before trying to using it
		for (i = 0; sop.sp_num[i] != -1; i++) {
			if (sprite[sop.sp_num[i]].statnum == STAT_SO_SHOOT_POINT) {
				sop.flags |= (SOBJ_HAS_WEAPON);
				break;
			}
		}

		DoPlayerResetMovement(pp);

		PlayerToRemote(pp);
		PlayerRemoteInit(pp);

		switch (sop.track) {
		case SO_TANK:
			if ((pp.input.vel | pp.input.svel) != 0)
				PlaySOsound(sop.mid_sector, SO_DRIVE_SOUND);
			else
				PlaySOsound(sop.mid_sector, SO_IDLE_SOUND);
			pp.posz = fz - PLAYER_HEIGHT;
			DoPlayerBeginOperateTank(pp);
			break;
		case SO_TURRET_MGUN:
		case SO_TURRET:
			if (pp.input.angvel != 0)
				PlaySOsound(sop.mid_sector, SO_DRIVE_SOUND);
			else
				PlaySOsound(sop.mid_sector, SO_IDLE_SOUND);
			pp.posz = fz - PLAYER_HEIGHT;
			DoPlayerBeginOperateTurret(pp);
			break;
		case SO_SPEED_BOAT:
			if ((pp.input.vel | pp.input.svel) != 0)
				PlaySOsound(sop.mid_sector, SO_DRIVE_SOUND);
			else
				PlaySOsound(sop.mid_sector, SO_IDLE_SOUND);
			pp.posz = fz - PLAYER_HEIGHT;
			DoPlayerBeginOperateBoat(pp);
			break;
		default:
			return;
		}

		PlayerRemoteReset(pp, save_sectnum);
	}

	public static void PlayerRemoteReset(PlayerStr pp, short sectnum) {
		if (!isValidSector(sectnum))
			return;

		pp.cursectnum = pp.lastcursectnum = sectnum;

		pp.posx = sprite[pp.remote_sprite].x;
		pp.posy = sprite[pp.remote_sprite].y;
		pp.posz = sector[sectnum].floorz - PLAYER_HEIGHT;

		pp.xvect = pp.yvect = pp.oxvect = pp.oyvect = pp.slide_xvect = pp.slide_yvect = 0;

		UpdatePlayerSprite(pp);
	}

	public static void PlayerRemoteInit(PlayerStr pp) {
		pp.remote.xvect = 0;
		pp.remote.yvect = 0;
		pp.remote.oxvect = 0;
		pp.remote.oyvect = 0;
		pp.remote.slide_xvect = 0;
		pp.remote.slide_yvect = 0;
	}

	public static void RemoteToPlayer(PlayerStr pp) {
		pp.cursectnum = pp.remote.cursectnum;
		pp.lastcursectnum = pp.remote.lastcursectnum;

		pp.posx = pp.remote.posx;
		pp.posy = pp.remote.posy;
		pp.posz = pp.remote.posz;

		pp.xvect = pp.remote.xvect;
		pp.yvect = pp.remote.yvect;
		pp.oxvect = pp.remote.oxvect;
		pp.oyvect = pp.remote.oyvect;
		pp.slide_xvect = pp.remote.slide_xvect;
		pp.slide_yvect = pp.remote.slide_yvect;
	}

	public static void PlayerToRemote(PlayerStr pp) {
		if (pp.remote == null)
			pp.remote = new Remote_Control();
		pp.remote.cursectnum = pp.cursectnum;
		pp.remote.lastcursectnum = pp.lastcursectnum;

		pp.remote.oposx = pp.remote.posx;
		pp.remote.oposy = pp.remote.posy;
		pp.remote.oposz = pp.remote.posz;

		pp.remote.posx = pp.posx;
		pp.remote.posy = pp.posy;
		pp.remote.posz = pp.posz;

		pp.remote.xvect = pp.xvect;
		pp.remote.yvect = pp.yvect;
		pp.remote.oxvect = pp.oxvect;
		pp.remote.oyvect = pp.oyvect;
		pp.remote.slide_xvect = pp.slide_xvect;
		pp.remote.slide_yvect = pp.slide_yvect;
	}

	public static void DoPlayerStopOperate(PlayerStr pp) {
		pp.Flags &= ~(PF_WEAPON_DOWN);
		DoPlayerResetMovement(pp);
		DoTankTreads(pp);
		DoPlayerOperateMatch(pp, false);
		StopSOsound(SectorObject[pp.sop].mid_sector);

		if (pp.sop_remote != -1) {
			if (TEST_BOOL1(sprite[pp.remote_sprite]))
				pp.pang = pp.oang = sprite[pp.remote_sprite].ang;
			else
				pp.pang = pp.oang = engine.getangle(SectorObject[pp.sop_remote].xmid - pp.posx,
						SectorObject[pp.sop_remote].ymid - pp.posy);
		}

		if (pp.sop_control != -1) {
			SectorObject[pp.sop_control].controller = -1;
		}
		pp.sop_control = -1;
		pp.sop_riding = -1;
		pp.sop_remote = -1;
		pp.sop = -1;
		DoPlayerBeginRun(pp);
	}

	public static void DoPlayerOperateTurret(PlayerStr pp) {
		short save_sectnum;

		if (TEST_SYNC_KEY(pp, SK_OPERATE)) {
			if (FLAG_KEY_PRESSED(pp, SK_OPERATE)) {
				FLAG_KEY_RELEASE(pp, SK_OPERATE);
				DoPlayerStopOperate(pp);
				return;
			}
		} else {
			FLAG_KEY_RESET(pp, SK_OPERATE);
		}

		Sector_Object sop = SectorObject[pp.sop];
		if (sop.max_damage != -9999 && sop.max_damage <= 0) {
			DoPlayerStopOperate(pp);
			return;
		}

		save_sectnum = pp.cursectnum;

		if (pp.sop_remote != -1)
			RemoteToPlayer(pp);

		DoPlayerMoveTurret(pp);

		if (pp.sop_remote != -1) {
			PlayerToRemote(pp);
			PlayerRemoteReset(pp, save_sectnum);
		}
	}

	public static void DoPlayerOperateBoat(PlayerStr pp) {
		short save_sectnum;

		if (TEST_SYNC_KEY(pp, SK_OPERATE)) {
			if (FLAG_KEY_PRESSED(pp, SK_OPERATE)) {
				FLAG_KEY_RELEASE(pp, SK_OPERATE);
				DoPlayerStopOperate(pp);
				return;
			}
		} else {
			FLAG_KEY_RESET(pp, SK_OPERATE);
		}

		Sector_Object sop = SectorObject[pp.sop];
		if (sop.max_damage != -9999 && sop.max_damage <= 0) {
			DoPlayerStopOperate(pp);
			return;
		}

		save_sectnum = pp.cursectnum;

		if (pp.sop_remote != -1)
			RemoteToPlayer(pp);

		DoPlayerMoveBoat(pp);

		if (pp.sop_remote != -1) {
			PlayerToRemote(pp);
			PlayerRemoteReset(pp, save_sectnum);
		}
	}

	public static void DoPlayerOperateTank(PlayerStr pp) {
		short save_sectnum;

		if (TEST_SYNC_KEY(pp, SK_OPERATE)) {
			if (FLAG_KEY_PRESSED(pp, SK_OPERATE)) {
				FLAG_KEY_RELEASE(pp, SK_OPERATE);
				DoPlayerStopOperate(pp);
				return;
			}
		} else {
			FLAG_KEY_RESET(pp, SK_OPERATE);
		}

		Sector_Object sop = SectorObject[pp.sop];
		if (sop.max_damage != -9999 && sop.max_damage <= 0) {
			DoPlayerStopOperate(pp);
			return;
		}

		save_sectnum = pp.cursectnum;

		if (pp.sop_remote != -1)
			RemoteToPlayer(pp);

		DoPlayerMoveTank(pp);

		if (pp.sop_remote != -1) {
			PlayerToRemote(pp);
			PlayerRemoteReset(pp, save_sectnum);
		}
	}

	private static final int PLAYER_DEATH_GRAV = 8;

	public static void DoPlayerDeathJump(PlayerStr pp) {
		short i;

		// instead of multiplying by synctics, use a loop for greater accuracy
		for (i = 0; i < synctics; i++) {
			// adjust jump speed by gravity - if jump speed greater than 0 player
			// have started falling
			if ((pp.jump_speed += PLAYER_DEATH_GRAV) > 0) {
				pp.Flags &= ~(PF_JUMPING);
				pp.Flags |= (PF_FALLING);
				DoPlayerDeathFall(pp);
				return;
			}

			// adjust height by jump speed
			pp.posz += pp.jump_speed;

			// if player gets to close the ceiling while jumping
			// if (pp.posz < pp.hiz + Z(4))
			if (PlayerCeilingHit(pp, pp.hiz + Z(4))) {
				// put player at the ceiling
				pp.posz = pp.hiz + Z(4);

				// reverse your speed to falling
				pp.jump_speed = -pp.jump_speed;

				// start falling
				pp.Flags &= ~(PF_JUMPING);
				pp.Flags |= (PF_FALLING);
				DoPlayerDeathFall(pp);
				return;
			}
		}
	}

	public static void DoPlayerDeathFall(PlayerStr pp) {
		short i;
		int loz;

		for (i = 0; i < synctics; i++) {
			// adjust jump speed by gravity
			pp.jump_speed += PLAYER_DEATH_GRAV;

			// adjust player height by jump speed
			pp.posz += pp.jump_speed;

			if (pp.lo_sectp != -1 && TEST(sector[pp.lo_sectp].extra, SECTFX_SINK)) {
				loz = sector[pp.lo_sectp].floorz;
			} else
				loz = pp.loz;

			if (PlayerFloorHit(pp, loz - PLAYER_DEATH_HEIGHT)) {
				if (loz != pp.loz)
					SpawnSplash(pp.PlayerSprite);

				if (RANDOM_RANGE(1000) > 500)
					PlaySound(DIGI_BODYFALL1, pp, v3df_dontpan);
				else
					PlaySound(DIGI_BODYFALL2, pp, v3df_dontpan);

				pp.posz = loz - PLAYER_DEATH_HEIGHT;
				pp.Flags &= ~(PF_FALLING);
			}
		}
	}

	public static final int MAX_SUICIDE = 11;
	private static final String SuicideNote[] = { "decided to do the graveyard tour.", "had enough and checked out.",
			"didn't fear the Reaper.", "dialed the 1-800-CYANIDE line.", "wasted himself.", "kicked his own ass.",
			"went out in blaze of his own glory.", "killed himself before anyone else could.",
			"needs shooting lessons.", "blew his head off.", "did everyone a favor and offed himself." };
	public static final int MAX_KILL_NOTES = 16;

	public static final String KilledPlayerMessage(PlayerStr pp, PlayerStr killer) {

		int rnd = STD_RANDOM_RANGE(MAX_KILL_NOTES);
		String p1 = pp.getName();
		String p2 = killer.getName();

		if (pp.HitBy == killer.PlayerSprite) {
			return p1 + " was killed by " + p2 + ".";
		} else
			switch (rnd) {
			case 0:
				return p1 + " was wasted by " + p2 + "'s " + DeathString(pp.HitBy) + ".";
			case 1:
				return p1 + " got his ass kicked by " + p2 + "'s " + DeathString(pp.HitBy) + ".";
			case 2:
				return p1 + " bows down before the mighty power of " + p2 + ".";
			case 3:
				return p1 + " was killed by " + p2 + "'s " + DeathString(pp.HitBy) + ".";
			case 4:
				return p1 + " got slapped down hard by " + p2 + "'s " + DeathString(pp.HitBy) + ".";
			case 5:
				return p1 + " got on his knees before " + p2 + ".";
			case 6:
				return p1 + " was totally out classed by " + p2 + "'s " + DeathString(pp.HitBy) + ".";
			case 7:
				return p1 + " got chewed apart by " + p2 + "'s " + DeathString(pp.HitBy) + ".";
			case 8:
				return p1 + " was retired by " + p2 + "'s " + DeathString(pp.HitBy) + ".";
			case 9:
				return p1 + " was greased by " + p2 + "'s " + DeathString(pp.HitBy) + ".";
			case 10:
				return p1 + " was humbled lower than dirt by " + p2 + ".";
			case 11:
				return p2 + " beats " + p1 + " like a red headed step child.";
			case 12:
				return p1 + " begs for mercy as " + p2 + " terminates him with extreme prejudice.";
			case 13:
				return p1 + " falls before the superior skills of " + p2 + ".";
			case 14:
				return p2 + " gives " + p1 + " a beating he'll never forget.";
			case 15:
				return p2 + " puts the Smack Dab on " + p1 + " with his " + DeathString(pp.HitBy);
			}
		return (null);
	};

	public static void DoPlayerDeathMessage(int pp, int nkiller) {
		boolean SEND_OK = false;
		String ds = null;

		PlayerStr killer = Player[nkiller];

		killer.KilledPlayer[pp]++;

		if (Player[pp] == killer && pp == myconnectindex) {
			ds = Player[pp].getName() + " " + SuicideNote[STD_RANDOM_RANGE(MAX_SUICIDE)];
			SEND_OK = true;
		} else
		// I am being killed
		if (killer == Player[myconnectindex]) {
			ds = KilledPlayerMessage(Player[pp], killer);
			SEND_OK = true;
		}

		if (SEND_OK) {
			gNet.SendMessage(-1, ds);
			adduserquote(ds);
		}
	}

	public static final int PLAYER_DEATH_TILT_VALUE = (32);
	public static final int PLAYER_DEATH_HORIZ_UP_VALUE = (165);
	public static final int PLAYER_DEATH_HORIZ_JUMP_VALUE = (150);
	public static final int PLAYER_DEATH_HORIZ_FALL_VALUE = (50);

	public static void DoPlayerBeginDie(int ppnum) {
		short bak;
		int choosesnd = 0;
		PlayerStr pp = Player[ppnum];
		USER u = pUser[pp.PlayerSprite];

		if (Prediction)
			return;

		if (GodMode)
			return;

		// Override any previous talking, death scream has precedance
		if (pp.PlayerTalking) {
			if (pp.TalkVocHandle.isActive())
				pp.TalkVocHandle.stop();
			pp.PlayerTalking = false;
			pp.TalkVocnum = -1;
			pp.TalkVocHandle = null;
		}

		// Do the death scream
		choosesnd = RANDOM_RANGE(MAX_PAIN);

		PlayerSound(PlayerLowHealthPainVocs[choosesnd], v3df_dontpan | v3df_doppler | v3df_follow, pp);
		if (game.nNetMode == NetMode.Single && numplayers <= 1 && lastload != null && !lastload.isEmpty() && BuildGdx.compat.checkFile(lastload, Path.User) != null) {
			game.menu.mOpen(game.menu.mMenus[LASTSAVE], -1);
		} else {
			bak = GlobInfoStringTime;
			GlobInfoStringTime = 999;
			PutStringInfo(pp, "Press \"USE\" or SPACE to restart");
			GlobInfoStringTime = bak;
		}

		if (pp.sop_control != -1)
			DoPlayerStopOperate(pp);

		// if diving force death to drown type
		if (TEST(pp.Flags, PF_DIVING))
			pp.DeathType = PLAYER_DEATH_DROWN;

		pp.Flags &= ~(PF_JUMPING | PF_FALLING | PF_DIVING | PF_FLYING | PF_CLIMBING | PF_CRAWLING | PF_LOCK_CRAWL);

		pp.tilt_dest = 0;

		ActorCoughItem(pp.PlayerSprite);

		if (numplayers > 1 || gNet.FakeMultiplayer) {
			// Give kill credit to player if necessary
			if (pp.Killer >= 0) {
				USER ku = pUser[pp.Killer];

				if (ku != null && ku.PlayerP != -1) {
					if (pp.pnum == ku.PlayerP) {
						// Killed yourself
						PlayerUpdateKills(ppnum, -1);
						DoPlayerDeathMessage(ppnum, ppnum);
					} else {
						// someone else killed you
						if (gNet.TeamPlay) {
							// playing team play
							if (pUser[pp.PlayerSprite].spal == ku.spal) {
								// Killed your team member
								PlayerUpdateKills(ppnum, -1);
								DoPlayerDeathMessage(ppnum, ku.PlayerP);
							} else {
								// killed another team member
								PlayerUpdateKills(ku.PlayerP, 1);
								DoPlayerDeathMessage(ppnum, ku.PlayerP);
							}
						} else {
							// not playing team play
							PlayerUpdateKills(ku.PlayerP, 1);
							DoPlayerDeathMessage(ppnum, ku.PlayerP);
						}
					}
				}
			} else {
				// Killed by some hazard - negative frag
				PlayerUpdateKills(ppnum, -1);
				DoPlayerDeathMessage(ppnum, ppnum);
			}
		}

		// Get rid of all panel spells that are currently working
		KillInventoryBar(pp);

		pp.Flags |= (PF_LOCK_HORIZ);

		pp.friction = PLAYER_RUN_FRICTION;
		pp.slide_xvect = pp.slide_yvect = 0;
		pp.floor_dist = PLAYER_WADE_FLOOR_DIST;
		pp.ceiling_dist = PLAYER_WADE_CEILING_DIST;
		pp.DoPlayerAction = PlayerDeathFunc[pp.DeathType];
		pp.sop_control = -1;
		pp.sop_remote = -1;
		pp.sop_riding = -1;
		pp.sop = -1;
		pp.Flags &= ~(PF_TWO_UZI);

		NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Run);
		pWeaponForceRest(pp);
		if (pp.Chops != null)
			ChopsSetRetract(pp);

		SPRITE psp = pp.getSprite();
		switch (pp.DeathType) {
		case PLAYER_DEATH_DROWN: {
			pp.Flags |= (PF_JUMPING);
			u.ID = NINJA_DEAD;
			pp.jump_speed = -200;
			NewStateGroup(pp.PlayerSprite, PlayerStateGroup.sg_PlayerDeath);
			DoFindGround(pp.PlayerSprite);
			DoBeginJump(pp.PlayerSprite);
			u.jump_speed = -300;
			break;
		}
		case PLAYER_DEATH_FLIP:
		case PLAYER_DEATH_RIPPER:
			pp.Flags |= (PF_JUMPING);
			u.ID = NINJA_DEAD;
			pp.jump_speed = -300;
			NewStateGroup(pp.PlayerSprite, PlayerStateGroup.sg_PlayerDeath);
			psp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
			u.ceiling_dist = (short) Z(10);
			u.floor_dist = (short) Z(0);
			DoFindGround(pp.PlayerSprite);
			DoBeginJump(pp.PlayerSprite);
			u.jump_speed = -400;
			break;
		case PLAYER_DEATH_CRUMBLE:

			PlaySound(DIGI_BODYSQUISH1, pp, v3df_dontpan);

			pp.Flags |= (PF_DEAD_HEAD | PF_JUMPING);
			pp.jump_speed = -300;
			u.slide_vel = 0;
			SpawnShrap(pp.PlayerSprite, -1);
			psp.cstat |= (CSTAT_SPRITE_YCENTER);
			NewStateGroup(pp.PlayerSprite, PlayerStateGroup.sg_PlayerHeadFly);
			u.ID = NINJA_Head_R0;
			psp.xrepeat = 48;
			psp.yrepeat = 48;
			// Blood fountains
			InitBloodSpray(pp.PlayerSprite, true, 105);
			break;
		case PLAYER_DEATH_EXPLODE:

			PlaySound(DIGI_BODYSQUISH1, pp, v3df_dontpan);

			pp.Flags |= (PF_DEAD_HEAD | PF_JUMPING);
			pp.jump_speed = -650;
			SpawnShrap(pp.PlayerSprite, -1);
			psp.cstat |= (CSTAT_SPRITE_YCENTER);
			NewStateGroup(pp.PlayerSprite, PlayerStateGroup.sg_PlayerHeadFly);
			u.ID = NINJA_Head_R0;
			psp.xrepeat = 48;
			psp.yrepeat = 48;
			// Blood fountains
			InitBloodSpray(pp.PlayerSprite, true, -1);
			InitBloodSpray(pp.PlayerSprite, true, -1);
			InitBloodSpray(pp.PlayerSprite, true, -1);
			break;
		case PLAYER_DEATH_SQUISH:

			PlaySound(DIGI_BODYCRUSHED1, pp, v3df_dontpan);

			pp.Flags |= (PF_DEAD_HEAD | PF_JUMPING);
			pp.jump_speed = 200;
			u.slide_vel = 800;
			SpawnShrap(pp.PlayerSprite, -1);
			psp.cstat |= (CSTAT_SPRITE_YCENTER);
			NewStateGroup(pp.PlayerSprite, PlayerStateGroup.sg_PlayerHeadFly);
			u.ID = NINJA_Head_R0;
			psp.xrepeat = 48;
			psp.yrepeat = 48;
			// Blood fountains
			InitBloodSpray(pp.PlayerSprite, true, 105);
			break;
		default:
			break;
		}

		pp.Flags |= (PF_DEAD);
		u.Flags &= ~(SPR_BOUNCE);
		pp.Flags &= ~(PF_HEAD_CONTROL);
	}

	public static boolean DoPlayerDeathHoriz(PlayerStr pp, int target, int speed) {
		if (pp.horiz > target) {
			pp.horiz -= speed;
			if (pp.horiz <= target)
				pp.horiz = target;
		}

		if (pp.horiz < target) {
			pp.horiz += speed;
			if (pp.horiz >= target)
				pp.horiz = target;
		}

		return (pp.horiz == target);
	}

	public static boolean DoPlayerDeathTilt(PlayerStr pp, int target, int speed) {
		if (pp.tilt > target) {
			pp.tilt -= speed;
			if (pp.tilt <= target)
				pp.tilt = target;
		}

		if (pp.tilt < target) {
			pp.tilt += speed;
			if (pp.tilt >= target)
				pp.tilt = target;
		}

		return (pp.tilt == target);
	}

	public static void DoPlayerDeathZrange(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];

		// make sure we don't land on a regular sprite
		DoFindGround(pp.PlayerSprite);

		// update player values with results from DoFindGround
		pp.loz = u.loz;
		pp.lo_sp = u.lo_sp;
		pp.lo_sectp = u.lo_sectp;
	}

	public static void DoPlayerDeathHurl(PlayerStr pp) {
		if (numplayers > 1 || gNet.FakeMultiplayer) {
			if (TEST_SYNC_KEY(pp, SK_SHOOT)) {
				if (FLAG_KEY_PRESSED(pp, SK_SHOOT)) {

					pp.Flags |= (PF_HEAD_CONTROL);
					NewStateGroup(pp.PlayerSprite, PlayerStateGroup.sg_PlayerHeadHurl);
					if (MoveSkip4 == 0) {
						SpawnShrap(pp.PlayerSprite, -1);
						if (RANDOM_RANGE(1000) > 400)
							PlayerSound(DIGI_DHVOMIT, v3df_dontpan | v3df_follow, pp);
					}
					return;
				}
			}
		}

		if (!TEST(pp.Flags, PF_JUMPING | PF_FALLING))
			NewStateGroup(pp.PlayerSprite, PlayerStateGroup.sg_PlayerHead);
	}

	public static void DoPlayerDeathFollowKiller(PlayerStr pp) {
		// if it didn't make it to this angle because of a low ceiling or something
		// continue on to it
		DoPlayerDeathHoriz(pp, PLAYER_DEATH_HORIZ_UP_VALUE, 4);

		// allow turning
		if ((TEST(pp.Flags, PF_DEAD_HEAD) && pp.input.angvel != 0) || TEST(pp.Flags, PF_HEAD_CONTROL)) {
			// Allow them to turn fast
			PLAYER_RUN_LOCK(pp);

			DoPlayerTurn(pp);
			return;
		}

		// follow what killed you if its available
		if (pp.Killer > -1) {
			SPRITE kp = sprite[pp.Killer];
			short ang2, delta_ang;

			if (FAFcansee(kp.x, kp.y, SPRITEp_TOS(kp), kp.sectnum, pp.posx, pp.posy, pp.posz, pp.cursectnum)) {
				ang2 = engine.getangle(kp.x - pp.posx, kp.y - pp.posy);

				delta_ang = GetDeltaAngle(ang2, pp.getAnglei());

				pp.pang += (delta_ang >> 4);
				pp.pang = BClampAngle(pp.pang);
			}
		}
	}

	public static void DoPlayerDeathRestart(PlayerStr pp)
	{
		SPRITE sp = pp.getSprite();
		USER u = pUser[pp.PlayerSprite];
		// Spawn a dead LoWang body for non-head deaths
			// Hey Frank, if you think of a better check, go ahead and put it in.
			if (PlayerFloorHit(pp, pp.loz - PLAYER_HEIGHT)) {
				if (pp.DeathType == PLAYER_DEATH_FLIP || pp.DeathType == PLAYER_DEATH_RIPPER)
					QueueLoWangs(pp.PlayerSprite);
			} else { // If he's not on the floor, then gib like a mo-fo!
				InitBloodSpray(pp.PlayerSprite, true, -1);
				InitBloodSpray(pp.PlayerSprite, true, -1);
				InitBloodSpray(pp.PlayerSprite, true, -1);
			}

			pClearTextLine(pp, TEXT_INFO_LINE(0));

			PlayerSpawnPosition(pp.pnum);

			NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Stand);
			sp.picnum = u.State.Pic;
			sp.xrepeat = sp.yrepeat = PLAYER_NINJA_XREPEAT;
			sp.cstat &= ~(CSTAT_SPRITE_YCENTER);
			sp.x = pp.posx;
			sp.y = pp.posy;
			sp.z = pp.posz + PLAYER_HEIGHT;
			sp.ang = pp.getAnglei();

			DoSpawnTeleporterEffect(sp);
			PlaySound(DIGI_TELEPORT, pp, v3df_none);

			DoPlayerZrange(pp);

			pp.sop_control = -1;
			pp.sop_remote = -1;
			pp.sop_riding = -1;
			pp.sop = -1;

			pp.Flags &= ~(PF_WEAPON_DOWN | PF_WEAPON_RETRACT);
			pp.Flags &= ~(PF_DEAD);
			pp.Flags &= ~(PF_LOCK_HORIZ);
			sp.cstat &= ~(CSTAT_SPRITE_YCENTER);
			sp.cstat |= (CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
			sp.xrepeat = PLAYER_NINJA_XREPEAT;
			sp.yrepeat = PLAYER_NINJA_YREPEAT;

			pp.horiz = pp.horizbase = 100;
			DoPlayerResetMovement(pp);
			u.ID = NINJA_RUN_R0;

			PlayerDeathReset(pp);
			if (pp == Player[screenpeek])
				ResetPalette(pp, FORCERESET);

			pp.NightVision = false;
			pp.FadeAmt = 0;
			DoPlayerDivePalette(pp);
			DoPlayerNightVisionPalette(pp);

			if (numplayers > 1 || gNet.FakeMultiplayer) {
				// need to call this routine BEFORE resetting DEATH flag
				DoPlayerBeginRun(pp);
			} else {
				// restart the level in single play
				if (DemoPlaying) {
					if (rec != null)
						rec.close();
				}
				else
					ExitLevel = true;
			}

			DoPlayerFireOutDeath(pp);
	}

	private static void DoPlayerDeathCheckKeys(PlayerStr pp) {
		if (TEST_SYNC_KEY(pp, SK_SPACE_BAR)) {
			DoPlayerDeathRestart(pp);
		}
	}

	public static void DoPlayerHeadDebris(PlayerStr pp) {
		if (!isValidSector(pp.cursectnum))
			return;

		SECTOR sectp = sector[pp.cursectnum];

		if (TEST(sectp.extra, SECTFX_SINK)) {
			DoPlayerSpriteBob(pp, Z(8), Z(4), 3);
		} else {
			pp.bob_amt = 0;
		}
	}

	public static SPRITE DoPlayerDeathCheckKick(PlayerStr pp) {
		SPRITE sp = pp.getSprite(), hp;
		USER u = pUser[pp.PlayerSprite], hu;
		short stat, i, nexti;

		for (stat = 0; stat < StatDamageList.length; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				hp = sprite[i];
				hu = pUser[i];

				if (i == pp.PlayerSprite)
					break;

				// don't set off mine
				if (!TEST(hp.extra, SPRX_PLAYER_OR_ENEMY))
					continue;

				if (DISTANCE(hp.x, hp.y, sp.x, sp.y) < hu.Radius + 100) {
					pp.Killer = i;

					u.slide_ang = engine.getangle(sp.x - hp.x, sp.y - hp.y);
					u.slide_ang = NORM_ANGLE(u.slide_ang + (RANDOM_P2(128 << 5) >> 5) - 64);

					u.slide_vel = hp.xvel << 1;
					u.Flags &= ~(SPR_BOUNCE);
					pp.jump_speed = -500;
					NewStateGroup(pp.PlayerSprite, PlayerStateGroup.sg_PlayerHeadFly);
					pp.Flags |= (PF_JUMPING);
					SpawnShrap(pp.PlayerSprite, -1);
					return (hp);
				}
			}
		}

		DoPlayerZrange(pp);

		// sector stomper kick
		if (klabs(pp.loz - pp.hiz) < SPRITEp_SIZE_Z(sp) - Z(8)) {
			u.slide_ang = (short) RANDOM_P2(2048);

			u.slide_vel = 1000;
			u.Flags &= ~(SPR_BOUNCE);
			pp.jump_speed = -100;
			NewStateGroup(pp.PlayerSprite, PlayerStateGroup.sg_PlayerHeadFly);
			pp.Flags |= (PF_JUMPING);
			SpawnShrap(pp.PlayerSprite, -1);
			return (null);
		}

		return (null);
	}

	public static void DoPlayerDeathMoveHead(PlayerStr pp) {
		SPRITE sp = pp.getSprite();
		USER u = pUser[pp.PlayerSprite];
		int dax, day;
		short sectnum;

		dax = MOVEx(u.slide_vel, u.slide_ang);
		day = MOVEy(u.slide_vel, u.slide_ang);

		if ((u.ret = move_sprite(pp.PlayerSprite, dax, day, 0, Z(16), Z(16), 1, synctics)) != 0) {
			switch (DTEST(u.ret, HIT_MASK)) {
			case HIT_SPRITE: {
				short wall_ang, dang;
				short hitsprite = -2;
				SPRITE hsp;

				hitsprite = NORM_SPRITE(u.ret);
				hsp = sprite[hitsprite];

				if (!TEST(hsp.cstat, CSTAT_SPRITE_WALL))
					break;

				wall_ang = NORM_ANGLE(hsp.ang);
				dang = GetDeltaAngle(u.slide_ang, wall_ang);
				u.slide_ang = NORM_ANGLE(wall_ang + 1024 - dang);

				SpawnShrap(pp.PlayerSprite, -1);
				break;
			}
			case HIT_WALL: {
				short hitwall, w, nw, wall_ang, dang;

				hitwall = NORM_WALL(u.ret);

				w = hitwall;
				nw = wall[w].point2;
				wall_ang = NORM_ANGLE(engine.getangle(wall[nw].x - wall[w].x, wall[nw].y - wall[w].y) - 512);

				dang = GetDeltaAngle(u.slide_ang, wall_ang);
				u.slide_ang = NORM_ANGLE(wall_ang + 1024 - dang);

				SpawnShrap(pp.PlayerSprite, -1);
				break;
			}
			}
		}

		pp.posx = sp.x;
		pp.posy = sp.y;
		pp.cursectnum = sp.sectnum;

		// try to stay in valid area - death sometimes throws you out of the map
		sectnum = pp.cursectnum;
		sectnum = COVERupdatesector(pp.posx, pp.posy, sectnum);
		if (sectnum < 0) {
			pp.cursectnum = pp.lv_sectnum;
			engine.changespritesect(pp.PlayerSprite, pp.lv_sectnum);
			pp.posx = sp.x = pp.lv_x;
			pp.posy = sp.y = pp.lv_y;
		} else {
			pp.lv_sectnum = sectnum;
			pp.lv_x = pp.posx;
			pp.lv_y = pp.posy;
		}
	}

	public static void DoPlayerDeathFlip(PlayerStr pp) {
		if (Prediction)
			return;

		DoPlayerDeathZrange(pp);

		if (TEST(pp.Flags, PF_JUMPING | PF_FALLING)) {
			if (TEST(pp.Flags, PF_JUMPING)) {
				DoPlayerDeathJump(pp);
				DoPlayerDeathHoriz(pp, PLAYER_DEATH_HORIZ_UP_VALUE, 2);
				if (!MoveSkip2)
					DoJump(pp.PlayerSprite);
			}

			if (TEST(pp.Flags, PF_FALLING)) {
				DoPlayerDeathFall(pp);
				DoPlayerDeathHoriz(pp, PLAYER_DEATH_HORIZ_UP_VALUE, 4);
				if (!MoveSkip2)
					DoFall(pp.PlayerSprite);
			}
		} else {
			DoPlayerDeathFollowKiller(pp);
		}

		DoPlayerDeathCheckKeys(pp);
	}

	public static void DoPlayerDeathDrown(PlayerStr pp) {
		SPRITE sp = pp.getSprite();

		if (Prediction)
			return;

		DoPlayerDeathZrange(pp);

		if (TEST(pp.Flags, PF_JUMPING | PF_FALLING)) {
			if (TEST(pp.Flags, PF_JUMPING)) {
				DoPlayerDeathJump(pp);
				DoPlayerDeathHoriz(pp, PLAYER_DEATH_HORIZ_UP_VALUE, 2);
				if (!MoveSkip2)
					DoJump(pp.PlayerSprite);
			}

			if (TEST(pp.Flags, PF_FALLING)) {
				pp.posz += Z(2);
				if (!MoveSkip2)
					sp.z += Z(4);

				// Stick like glue when you hit the ground
				if (pp.posz > pp.loz - PLAYER_DEATH_HEIGHT) {
					pp.posz = pp.loz - PLAYER_DEATH_HEIGHT;
					pp.Flags &= ~(PF_FALLING);
				}
			}
		}

		DoPlayerDeathFollowKiller(pp);
		DoPlayerDeathCheckKeys(pp);
	}

	public static void DoPlayerDeathBounce(PlayerStr pp) {
		SPRITE sp = pp.getSprite();
		USER u = pUser[pp.PlayerSprite];

		if (Prediction)
			return;

		if (pp.lo_sectp != -1 && TEST(sector[pp.lo_sectp].extra, SECTFX_SINK)) {
			sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
			NewStateGroup(pp.PlayerSprite, PlayerStateGroup.sg_PlayerHead);
			u.slide_vel = 0;
			u.Flags |= (SPR_BOUNCE);
			return;
		}

		u.Flags |= (SPR_BOUNCE);
		pp.jump_speed = -300;
		u.slide_vel >>= 2;
		u.slide_ang = NORM_ANGLE((RANDOM_P2(64 << 8) >> 8) - 32);
		pp.Flags |= (PF_JUMPING);
		SpawnShrap(pp.PlayerSprite, -1);
	}

	public static void DoPlayerDeathCrumble(PlayerStr pp) {
		SPRITE sp = pp.getSprite();
		USER u = pUser[pp.PlayerSprite];

		if (Prediction)
			return;

		DoPlayerDeathZrange(pp);

		if (TEST(pp.Flags, PF_JUMPING | PF_FALLING)) {
			if (TEST(pp.Flags, PF_JUMPING)) {
				DoPlayerDeathJump(pp);
				DoPlayerDeathHoriz(pp, PLAYER_DEATH_HORIZ_JUMP_VALUE, 4);
			}

			if (TEST(pp.Flags, PF_FALLING)) {
				DoPlayerDeathFall(pp);
				DoPlayerDeathHoriz(pp, PLAYER_DEATH_HORIZ_FALL_VALUE, 3);
			}

			if (!TEST(pp.Flags, PF_JUMPING | PF_FALLING)) {
				if (!TEST(u.Flags, SPR_BOUNCE)) {
					DoPlayerDeathBounce(pp);
					return;
				}

				sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
				NewStateGroup(pp.PlayerSprite, PlayerStateGroup.sg_PlayerHead);
			} else {
				DoPlayerDeathMoveHead(pp);
			}
		} else {
			DoPlayerDeathCheckKick(pp);
			DoPlayerDeathHurl(pp);
			DoPlayerDeathFollowKiller(pp);
		}

		DoPlayerDeathCheckKeys(pp);
		sp.z = pp.posz + PLAYER_DEAD_HEAD_FLOORZ_OFFSET;
		DoPlayerHeadDebris(pp);
	}

	public static void DoPlayerDeathExplode(PlayerStr pp) {
		SPRITE sp = pp.getSprite();
		USER u = pUser[pp.PlayerSprite];

		if (Prediction)
			return;

		DoPlayerDeathZrange(pp);

		if (TEST(pp.Flags, PF_JUMPING | PF_FALLING)) {
			if (TEST(pp.Flags, PF_JUMPING)) {
				DoPlayerDeathJump(pp);
				DoPlayerDeathHoriz(pp, PLAYER_DEATH_HORIZ_JUMP_VALUE, 4);
			}

			if (TEST(pp.Flags, PF_FALLING)) {
				DoPlayerDeathFall(pp);
				DoPlayerDeathHoriz(pp, PLAYER_DEATH_HORIZ_JUMP_VALUE, 3);
			}

			if (!TEST(pp.Flags, PF_JUMPING | PF_FALLING)) {
				if (!TEST(u.Flags, SPR_BOUNCE)) {
					DoPlayerDeathBounce(pp);
					return;
				}

				sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
				NewStateGroup(pp.PlayerSprite, PlayerStateGroup.sg_PlayerHead);
			} else {
				DoPlayerDeathMoveHead(pp);
			}
		} else {
			// special line for amoeba

			DoPlayerDeathCheckKick(pp);
			DoPlayerDeathHurl(pp);
			DoPlayerDeathFollowKiller(pp);
		}

		DoPlayerDeathCheckKeys(pp);
		sp.z = pp.posz + PLAYER_DEAD_HEAD_FLOORZ_OFFSET;
		DoPlayerHeadDebris(pp);
	}

	public static void DoPlayerBeginRun(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];

		// Crawl if in small aread automatically
		if (DoPlayerTestCrawl(pp)) {
			DoPlayerBeginCrawl(pp);
			return;
		}

		pp.Flags &= ~(PF_CRAWLING | PF_JUMPING | PF_FALLING | PF_LOCK_CRAWL | PF_CLIMBING);

		if (pp.WadeDepth != 0) {
			DoPlayerBeginWade(pp);
			return;
		}

		pp.friction = PLAYER_RUN_FRICTION;
		pp.floor_dist = PLAYER_RUN_FLOOR_DIST;
		pp.ceiling_dist = PLAYER_RUN_CEILING_DIST;
		pp.DoPlayerAction = Player_Action_Func.DoPlayerRun;

		if (TEST(pp.Flags, PF_PLAYER_MOVED))
			NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Run);
		else
			NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Stand);
	}

	private static final Player_Action_Func[] PlayerDeathFunc = { Player_Action_Func.DoPlayerDeathFlip,
			Player_Action_Func.DoPlayerDeathCrumble, Player_Action_Func.DoPlayerDeathExplode,
			Player_Action_Func.DoPlayerDeathFlip, Player_Action_Func.DoPlayerDeathExplode,
			Player_Action_Func.DoPlayerDeathDrown, };

	public static void DoPlayerRun(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];

		if (SectorIsUnderwaterArea(pp.cursectnum)) {
			DoPlayerBeginDiveNoWarp(pp);
			return;
		}

		// Crawl if in small aread automatically
		if (DoPlayerTestCrawl(pp)) {
			DoPlayerBeginCrawl(pp);
			return;
		}

		// Crawl Commanded
		if (TEST_SYNC_KEY(pp, SK_CRAWL)) {
			DoPlayerBeginCrawl(pp);
			return;
		}

		// Jump
		if (TEST_SYNC_KEY(pp, SK_JUMP)) {
			if (FLAG_KEY_PRESSED(pp, SK_JUMP)) {
				FLAG_KEY_RELEASE(pp, SK_JUMP);
				// make sure you stand at full heights for jumps/double jumps
				pp.posz = pp.loz - PLAYER_HEIGHT;
				DoPlayerBeginJump(pp);
				return;
			}
		} else {
			FLAG_KEY_RESET(pp, SK_JUMP);
		}

		// Crawl lock
		if (TEST_SYNC_KEY(pp, SK_CRAWL_LOCK)) {
			if (FLAG_KEY_PRESSED(pp, SK_CRAWL_LOCK)) {
				FLAG_KEY_RELEASE(pp, SK_CRAWL_LOCK);
				pp.Flags |= (PF_LOCK_CRAWL);
				DoPlayerBeginCrawl(pp);
				return;
			}
		} else {
			FLAG_KEY_RESET(pp, SK_CRAWL_LOCK);
		}

		if (PlayerFlyKey(pp)) {
			DoPlayerBeginFly(pp);
			return;
		}

		if (!TEST(pp.Flags, PF_DEAD) && !Prediction) {
			if (TEST_SYNC_KEY(pp, SK_OPERATE)) {
				if (FLAG_KEY_PRESSED(pp, SK_OPERATE)) {
					if (TEST(sector[pp.cursectnum].extra, SECTFX_OPERATIONAL)) {
						FLAG_KEY_RELEASE(pp, SK_OPERATE);
						DoPlayerBeginOperate(pp);
						return;
					} else if (TEST(sector[pp.cursectnum].extra, SECTFX_TRIGGER)) {
						int sp = FindNearSprite(pp.getSprite(), STAT_TRIGGER);
						if (sp != -1 && SP_TAG5(sprite[sp]) == 0) // TRIGGER_TYPE_REMOTE_SO
						{
							pp.remote_sprite = sp;
							FLAG_KEY_RELEASE(pp, SK_OPERATE);
							DoPlayerBeginRemoteOperate(pp, SP_TAG7(sprite[pp.remote_sprite]));
							return;
						}
					}
				}
			} else {
				FLAG_KEY_RESET(pp, SK_OPERATE);
			}
		}

		DoPlayerBob(pp);

		if (pp.WadeDepth != 0) {
			DoPlayerBeginWade(pp);
			return;
		}

		// If moving forward and tag is a ladder start climbing
		if (PlayerOnLadder(pp)) {
			DoPlayerBeginClimb(pp);
			return;
		}

		// Move about
		DoPlayerMove(pp);

		if (u.Rot != PlayerStateGroup.sg_PlayerNinjaSword && u.Rot != PlayerStateGroup.sg_PlayerNinjaPunch) {
			if (TEST(pp.Flags, PF_PLAYER_MOVED)) {
				if (u.Rot != u.ActorActionSet.Run)
					NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Run);
			} else {
				if (u.Rot != u.ActorActionSet.Stand)
					NewStateGroup(pp.PlayerSprite, u.ActorActionSet.Stand);
			}
		}

		// If the floor is far below you, fall hard instead of adjusting height
		if (PlayerFallTest(pp, PLAYER_HEIGHT)) {
			pp.jump_speed = Z(1);
			DoPlayerBeginFall(pp);
			// call PlayerFall now seems to iron out a hitch before falling
			DoPlayerFall(pp);
			return;
		}

		if (isValidSector(pp.cursectnum) && TEST(sector[pp.cursectnum].extra, SECTFX_DYNAMIC_AREA)) {
			pp.posz = pp.loz - PLAYER_HEIGHT;
		}

		// Adjust height moving up and down sectors
		DoPlayerHeight(pp);
	}

	public static void PlayerStateControl(int SpriteNum) {
		USER u;

		// Convienience var
		u = pUser[SpriteNum];

		u.Tics += synctics;

		// Skip states if too much time has passed
		while (u.Tics >= DTEST(u.State.Tics, SF_TICS_MASK)) {

			// Set Tics
			u.Tics -= DTEST(u.State.Tics, SF_TICS_MASK);

			// Transition to the next state
			u.State = u.State.getNext();

			// !JIM! Added this so I can do quick calls in player states!
			// Need this in order for floor blood and footprints to not get called more than
			// once.
			while (TEST(u.State.Tics, SF_QUICK_CALL)) {
				// Call it once and go to the next state
				u.State.Animator.invoke(SpriteNum);

				// if still on the same QUICK_CALL should you
				// go to the next state.
				if (TEST(u.State.Tics, SF_QUICK_CALL))
					u.State = u.State.getNext();
			}

			if (u.State.Pic == 0) {
				NewStateGroup(SpriteNum, u.State.getNextGroup());
			}
		}

		// Set picnum to the correct pic
		if (u.RotNum > 1)
			sprite[SpriteNum].picnum = u.Rot.getState(0).Pic;
		else
			sprite[SpriteNum].picnum = u.State.Pic;

		// Call the correct animator
		if (TEST(u.State.Tics, SF_PLAYER_FUNC))
			if (u.State.Animator != null)
				u.State.Animator.invoke(SpriteNum);
	}

	public static void MoveSkipSavePos() {
		SPRITE sp;
		USER u;
		short i, nexti;
		short pnum;
		PlayerStr pp;

		MoveSkip8 = (MoveSkip8 + 1) & 7;
		MoveSkip4 = (MoveSkip4 + 1) & 3;
		MoveSkip2 = !MoveSkip2;

		// Save off player
		for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
			pp = Player[pnum];

			pp.oposx = pp.posx;
			pp.oposy = pp.posy;
			pp.oposz = pp.posz;
			pp.oang = pp.pang;
			pp.ohoriz = pp.horiz;

			pp.obob_z = pp.bob_z;
			pp.obob_amt = pp.bob_amt;
		}

		// save off stats for skip4
		if (MoveSkip4 == 0) {
			for (short stat = STAT_SKIP4_START; stat <= STAT_SKIP4_INTERP_END; stat++) {
				for (i = headspritestat[stat]; i != -1; i = nexti) {
					nexti = nextspritestat[i];
					sp = sprite[i];
					u = pUser[i];

					if (u != null) {
						u.ox = sp.x;
						u.oy = sp.y;
						u.oz = sp.z;
					}
				}
			}
		}

		// save off stats for skip2
		if (!MoveSkip2) {
			for (short stat = STAT_SKIP2_START; stat <= STAT_SKIP2_INTERP_END; stat++) {
				for (i = headspritestat[stat]; i != -1; i = nexti) {
					nexti = nextspritestat[i];
					sp = sprite[i];
					u = pUser[i];
					if (u != null) {
						u.ox = sp.x;
						u.oy = sp.y;
						u.oz = sp.z;
					}
				}
			}
		}
	}

	public static void PlayerTimers(PlayerStr pp) {
		InventoryTimer(pp);
	}

	private static final int ChopTimer = 30 * 120;
	public static void ChopsCheck(PlayerStr pp) {
		if (!game.pMenu.gShowMenu && !HelpInputMode && !TEST(pp.Flags, PF_DEAD) && pp.sop_riding == -1
				&& numplayers <= 1) {
			if (((pp.input.bits | pp.input.vel | pp.input.svel) != 0 || pp.input.angvel != 0.0f || pp.input.aimvel != 0.0f)
					|| TEST(pp.Flags, PF_CLIMBING | PF_FALLING | PF_DIVING)) {
				// Hit a input key or other reason to stop chops
				if (pp.Chops != null) {
					if (pp.sop_control == -1) // specail case
						pp.Flags &= ~(PF_WEAPON_DOWN);
					ChopsSetRetract(pp);
				}
				ChopTics = 0;
			} else {
				ChopTics += synctics;
				if (pp.Chops == null) {
					// Chops not up
					if (ChopTics > ChopTimer) {
						ChopTics = 0;
						// take weapon down
						pp.Flags |= (PF_WEAPON_DOWN);
						InitChops(pp);
					}
				} else {
					// Chops already up
					if (ChopTics > ChopTimer) {
						ChopTics = 0;
						// bring weapon back up
						pp.Flags &= ~(PF_WEAPON_DOWN);
						ChopsSetRetract(pp);
					}
				}
			}
		}
	}

	public static void PlayerGlobal(PlayerStr pp) {
		// This is the place for things that effect the player no matter what hes
		// doing
		PlayerTimers(pp);

		if (TEST(pp.Flags, PF_RECOIL))
			DoPlayerRecoil(pp);

		if (!TEST(pp.Flags, PF_CLIP_CHEAT)) {
			if (pp.hi_sectp != -1 && pp.lo_sectp != -1) {
				int min_height;

				// just adjusted min height to something small to take care of all cases
				min_height = PLAYER_MIN_HEIGHT;

				if (klabs(pp.loz - pp.hiz) < min_height) {
					if (!TEST(pp.Flags, PF_DEAD)) {
						PlayerUpdateHealth(pp, -pUser[pp.PlayerSprite].Health); // Make sure he dies!
						PlayerCheckDeath(pp, -1);

						if (TEST(pp.Flags, PF_DEAD))
							return;
					}
				}
			}
		}

		if (pp.FadeAmt > 0 && MoveSkip4 == 0) {
			DoPaletteFlash(pp);
		}

		// camera stuff that can't be done in drawscreen
		if (pp.circle_camera_dist > CIRCLE_CAMERA_DIST_MIN)
			pp.circle_camera_ang = NORM_ANGLE(pp.circle_camera_ang + 14);

		if (pp.camera_check_time_delay > 0) {
			pp.camera_check_time_delay -= synctics;
			if (pp.camera_check_time_delay <= 0)
				pp.camera_check_time_delay = 0;
		}
	}

	public static void UpdateScrollingMessages() {
		short i;

		// Update the scrolling multiplayer messages
		for (i = 0; i < MAXUSERQUOTES; i++) {
			if (user_quote_time[i] != 0) {
				user_quote_time[i]--;

				if (user_quote_time[i] <= 0) {
					SetRedrawScreen(Player[myconnectindex]);
					user_quote_time[i] = 0;
				}
			}
		}

		if (gs.BorderNum > BORDER_BAR + 1) {
			quotebot = quotebotgoal;
		} else {
			if ((klabs(quotebotgoal - quotebot) <= 16))
				quotebot += ksgn(quotebotgoal - quotebot);
			else
				quotebot = quotebotgoal;
		}
	}

    public static void MultiPlayLimits() {
		short pnum;
		PlayerStr pp;
		boolean Done = false;

		if (ExitLevel)
			return;

		if (gNet.MultiGameType != MultiGameTypes.MULTI_GAME_COMMBAT)
			return;

		if (gNet.KillLimit != 0) {
			for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
				pp = Player[pnum];
				if (pp.Kills >= gNet.KillLimit) {
					Done = true;
				}
			}
		}

		if (gNet.TimeLimit != 0) {
			gNet.TimeLimitClock -= synctics;
			if (gNet.TimeLimitClock <= 0)
				Done = true;
		}

		if (Done) {
			gNet.TimeLimitClock = gNet.TimeLimit;

			// do not increment if level is 23 thru 28
			if (Level <= 22)
				Level++;

			ExitLevel = true;
			FinishedLevel = true;
		}
	}


	public static final String MSG_GAME_PAUSED = "Game Paused";

	public static void domovethings(BuildNet net) {
		short i, pnum;
		PlayerStr pp;

		// grab values stored in the fifo and put them in the players vars
		for (i = connecthead; i != -1; i = connectpoint2[i]) {
			pp = Player[i];
			pp.input.Copy(net.gFifoInput[net.gNetFifoTail & (MOVEFIFOSIZ - 1)][i]);
		}
		net.gNetFifoTail++;
		Arrays.fill(bCopySpriteOffs, false);

		if (gNet.MyCommPlayerQuit())
			return;

		// check for pause of multi-play game
		if (game.nNetMode == NetMode.Multiplayer)
			gNet.PauseMultiPlay();

		net.CalcChecksum();

		if (game.gPaused || !DemoPlaying && CommPlayers < 2 && !game.isCurrentScreen(gDemoScreen) && (game.menu.gShowMenu || Console.IsShown()))
			if(!game.menu.isOpened(game.menu.mMenus[LASTSAVE]))
				return;

		UpdateScrollingMessages(); // Update the multiplayer type messages

		// count the number of times this loop is called and use
		// for things like sync testing
		gNet.MoveThingsCount++;

		// recording is done here
		if (DemoRecording && rec != null)
			rec.record();

		totalsynctics += synctics;

		MoveSkipSavePos();

		for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
			pp = Player[pnum];
			DoPlayerMenuKeys(pp);
		}

		if (game.gPaused) {
			return;
		}

		PlayClock += synctics;
		if(rtsplaying > 0) rtsplaying--;

		if (!DebugAnim)
			if (!DebugActorFreeze)
				DoAnim(synctics);

		// should pass pnum and use syncbits
		if (!DebugSector)
			DoSector();

		ProcessVisOn();
		if (MoveSkip4 == 0) {
			ProcessQuakeOn();
			ProcessQuakeSpot();
			JS_ProcessEchoSpot();
		}

		SpriteControl();

		for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
			pp = Player[pnum];
			GlobPlayerStr = pp;

			// auto tracking mode for single player multi-game
			if (numplayers <= 1 && PlayerTrackingMode && pnum == screenpeek && screenpeek != myconnectindex) {
				Player[screenpeek].pang = engine.getangle(Player[myconnectindex].posx - Player[screenpeek].posx,
						Player[myconnectindex].posy - Player[screenpeek].posy);
			}

			if (!TEST(pp.Flags, PF_DEAD)) {
				LookupOperate(pp);
				WeaponOperate(pp);
				PlayerOperateEnv(pp);
			}

			// do for moving sectors
			DoPlayerSectorUpdatePreMove(pp);
			ChopsCheck(pp);

			// if (!ScrollMode2D)
			pp.DoPlayerAction.invoke(pp);

			UpdatePlayerSprite(pp);
			pSpriteControl(pp);

			PlayerStateControl(pp.PlayerSprite);

			DoPlayerSectorUpdatePostMove(pp);
			PlayerGlobal(pp);
		}

		MultiPlayLimits();

		if (MoveSkip8 == 0) {// 8=5x 4=10x, 2=20x, 0=40x per second
			// This function is already only call 10x per sec, this widdles it down even
			// more!
			MoveSkip8 = (MoveSkip8 + 1) & 15;
		}
		DoUpdateSounds3D();

		net.CorrectPrediction();

		if (FinishTimer != 0) {
			if ((FinishTimer -= synctics) <= 0) {
				FinishTimer = 0;
				ExitLevel = true;
				FinishedLevel = true;
			}
		}

		if (ExitLevel) // reset level
		{
			if (rec != null)
				rec.close();

			System.err.println("LeaveMap");
			if (numplayers > 1 && game.pNet.bufferJitter >= 0 && myconnectindex == connecthead)
				for (i = 0; i <= game.pNet.bufferJitter; i++)
					game.pNet.GetNetworkInput(); // wait for other player before level end

			if (!game.pNet.WaitForAllPlayers(5000)) {
				game.pNet.NetDisconnect(myconnectindex);
				return;
			}

			game.pNet.ready2send = false;
			if (game.isCurrentScreen(gDemoScreen))
				return;
			ExitLevel = false;

			if (FinishedLevel) {
				if (FinishAnim != 0) {
					if (gAnmScreen.init(FinishAnim)) {
						game.changeScreen(gAnmScreen.setCallback(new Runnable() {
							@Override
							public void run() {
								if (Level != 4 && Level != 20)
									Level++;
								game.changeScreen(gStatisticScreen);
							}
						}).escSkipping(true));
						return;
					}
				}

				game.changeScreen(gStatisticScreen);
			} else {
				if (!gGameScreen.enterlevel(gGameScreen.getTitle()))
					game.show();
			}
		}
	}

	private static void LookupOperate(PlayerStr pp) {
		pp.lookang -= (pp.lookang>>2);
	    if (pp.lookang != 0 && (pp.lookang >> 2) == 0)
	    	pp.lookang -= ksgn(pp.lookang);

	    if(TEST_SYNC_KEY(pp, SK_TILT_LEFT))
			pp.lookang -= 152;

	    if(TEST_SYNC_KEY(pp, SK_TILT_RIGHT))
			pp.lookang += 152;
	}

	public static void InitAllPlayers(boolean NewGame) {
		PlayerStr pp;
		PlayerStr pfirst = Player[connecthead];
		int i;

		pfirst.horiz = pfirst.horizbase = 100;

		// Initialize all [MAX_SW_PLAYERS] arrays here!
		for (int ppi = 0; ppi < MAX_SW_PLAYERS; ppi++) {
			pp = Player[ppi];
			pp.posx = pp.oposx = pfirst.posx;
			pp.posy = pp.oposy = pfirst.posy;
			pp.posz = pp.oposz = pfirst.posz;
			pp.pang = pp.oang = pfirst.pang;
			pp.horiz = pp.ohoriz = pfirst.horiz;
			pp.cursectnum = pfirst.cursectnum;
			// set like this so that player can trigger something on start of the level
			pp.lastcursectnum = (short) (pfirst.cursectnum + 1);

			pp.horizbase = pfirst.horizbase;
			pp.oldposx = 0;
			pp.oldposy = 0;
			pp.climb_ndx = 10;
			pp.Killer = -1;
			pp.Kills = 0;
			pp.bcnt = 0;
			pp.UziShellLeftAlt = false;
			pp.UziShellRightAlt = false;

			pp.ceiling_dist = PLAYER_RUN_CEILING_DIST;
			pp.floor_dist = PLAYER_RUN_FLOOR_DIST;

			pp.WpnGotOnceFlags = 0;
			pp.DoPlayerAction = Player_Action_Func.DoPlayerBeginRun;
			pp.KeyPressFlags = 0xFFFFFFFF;
			Arrays.fill(pp.KilledPlayer, (short) 0);

			if (NewGame) {
				for (i = 0; i < MAX_INVENTORY; i++) {
					pp.InventoryAmount[i] = 0;
					pp.InventoryPercent[i] = 0;
				}
			}

			// My palette flashing stuff
			pp.FadeAmt = 0;
			pp.FadeTics = 0;
			pp.StartColor = 0;
			pp.horizoff = 0;
			System.arraycopy(palette, 0, pp.temp_pal, 0, 768);

			List.Init(pp.PanelSpriteList);
		}
	}

	public static int SearchSpawnPosition(PlayerStr pp) {
		PlayerStr opp; // other player
		SPRITE sp;
		int pos_num;
		short pnum, spawn_sprite;
		boolean blocked;

		do {
			// get a spawn position
			pos_num = RANDOM_RANGE(MAX_SW_PLAYERS);
			spawn_sprite = headspritestat[STAT_MULTI_START + pos_num];
			if (spawn_sprite <= -1)
				return (0);

			sp = sprite[spawn_sprite];

			blocked = false;

			// check to see if anyone else is blocking this spot
			for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
				opp = Player[pnum];

				if (opp != pp) // don't test for yourself
				{
					if (FindDistance3D(sp.x - opp.posx, sp.y - opp.posy, (sp.z - opp.posz) >> 4) < 1000) {
						blocked = true;
						break;
					}
				}
			}
		} while (blocked);

		return (pos_num);
	}

	private static boolean SpawnPositionUsed[] = new boolean[MAX_SW_PLAYERS + 1];

	public static void PlayerSpawnPosition(int pnum) {
		SPRITE sp;
		PlayerStr pp = Player[pnum];
		int spawn_sprite = -1, pos_num = pnum;
		int i;

		// find the first unused spawn position
		// garauntees that the spawn pos 0 will be used
		// Note: This code is not used if the player is DEAD and respawning

		for (i = 0; i < MAX_SW_PLAYERS; i++) {
			if (!SpawnPositionUsed[i]) {
				pos_num = i;
				break;
			}
		}

		// need to call this routine BEFORE resetting DEATH flag

		switch (gNet.MultiGameType) {
		default:
			break;
		case MULTI_GAME_NONE:
			// start from the beginning
			spawn_sprite = headspritestat[STAT_MULTI_START + 0];
			break;
		case MULTI_GAME_COMMBAT:
		case MULTI_GAME_AI_BOTS:
			// start from random position after death
			if (TEST(pp.Flags, PF_DEAD)) {
				pos_num = SearchSpawnPosition(pp);
			}

			spawn_sprite = headspritestat[STAT_MULTI_START + pos_num];
			break;
		case MULTI_GAME_COOPERATIVE:
			// start your assigned spot
			spawn_sprite = headspritestat[STAT_CO_OP_START + pos_num];
			break;
		}

		SpawnPositionUsed[pos_num] = true;

		if (spawn_sprite < 0) {
			spawn_sprite = headspritestat[STAT_MULTI_START + 0];
			if(spawn_sprite == -1)
				return;
		}

		sp = sprite[spawn_sprite];

		pp.posx = pp.oposx = sp.x;
		pp.posy = pp.oposy = sp.y;
		pp.posz = pp.oposz = sp.z;
		pp.pang = pp.oang = sp.ang;
		pp.cursectnum = sp.sectnum;

		engine.getzsofslope(pp.cursectnum, pp.posx, pp.posy, zofslope);
		// if too close to the floor - stand up
		if (pp.posz > zofslope[FLOOR] - PLAYER_HEIGHT) {
			pp.posz = pp.oposz = zofslope[FLOOR] - PLAYER_HEIGHT;
		}
	}

	private static short MultiStatList[] = { STAT_MULTI_START, STAT_CO_OP_START };

	public static void InitMultiPlayerInfo() {
		PlayerStr pp = Player[0];
		SPRITE sp;
		int pnum, start0, stat;
		short SpriteNum, NextSprite, tag;

		// this routine is called before SpriteSetup - process start positions NOW
		for (SpriteNum = headspritestat[0]; SpriteNum != -1; SpriteNum = NextSprite) {
			NextSprite = nextspritestat[SpriteNum];
			sp = sprite[SpriteNum];

			tag = sp.hitag;

			if (sp.picnum == ST1) {
				switch (tag) {
				case MULTI_PLAYER_START:
					change_sprite_stat(SpriteNum, STAT_MULTI_START + sp.lotag);
					break;
				case MULTI_COOPERATIVE_START:
					change_sprite_stat(SpriteNum, STAT_CO_OP_START + sp.lotag);
					break;
				}
			}
		}

		// set up the zero starting positions - its not saved in the map as a ST1 sprite
		// like the others

		for (stat = 0; stat < MultiStatList.length; stat++) {
			if (gNet.MultiGameType != MultiGameTypes.MULTI_GAME_NONE) {
				// if start position is physically set then don't spawn a new one
				if (headspritestat[MultiStatList[stat] + 0] >= 0)
					continue;
			}

			start0 = SpawnSprite(MultiStatList[stat], ST1, null, pp.cursectnum, pp.posx, pp.posy, pp.posz, pp.getAnglei(), 0);

			pUser[start0] = null;
			sprite[start0].picnum = ST1;
		}

		Arrays.fill(SpawnPositionUsed, false);

		// Initialize multi player positions here
		for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
			pp = Player[pnum];
			switch (gNet.MultiGameType) {
			default:
				break;
			case MULTI_GAME_NONE:
				PlayerSpawnPosition(pnum);
				break;
			case MULTI_GAME_COMMBAT:
			case MULTI_GAME_AI_BOTS:
				// there are no keys in deathmatch play
				Arrays.fill(Player[0].HasKey, (byte) -1);
				Arrays.fill(pp.HasKey, (byte) -1);

				PlayerSpawnPosition(pnum);
				break;
			case MULTI_GAME_COOPERATIVE:
				PlayerSpawnPosition(pnum);
				break;
			}
		}
	}

	// If player stepped in something gooey, track it all over the place.
	public static int DoFootPrints(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (u.PlayerP != -1) {
			if (FAF_ConnectArea(Player[u.PlayerP].cursectnum))
				return (0);

			if (Player[u.PlayerP].NumFootPrints > 0) {
				QueueFootPrint(SpriteNum);
			}
		}

		return (0);
	}

	public static void CheckFootPrints(PlayerStr pp) {
		if (pp.NumFootPrints <= 0 || FootMode != FootType.WATER_FOOT) {
			// Hey, you just got your feet wet!
			pp.NumFootPrints = (short) (RANDOM_RANGE(10) + 3);
			FootMode = FootType.WATER_FOOT;
		}
	}

	public static void PlayerSaveable()
	{
		SaveData(DoPlayerSpriteReset);
		SaveData(DoFootPrints);
		SaveData(QueueFloorBlood);
		SaveData(pSetVisNorm);

		SaveData(s_PlayerNinjaRun);
		SaveGroup(PlayerStateGroup.sg_PlayerNinjaRun);
		SaveData(s_PlayerNinjaStand);
		SaveGroup(PlayerStateGroup.sg_PlayerNinjaStand);
		SaveData(s_PlayerNinjaJump);
		SaveGroup(PlayerStateGroup.sg_PlayerNinjaJump);
		SaveData(s_PlayerNinjaFall);
		SaveGroup(PlayerStateGroup.sg_PlayerNinjaFall);
		SaveData(s_PlayerNinjaClimb);
		SaveGroup(PlayerStateGroup.sg_PlayerNinjaClimb);
		SaveData(s_PlayerNinjaCrawl);
		SaveGroup(PlayerStateGroup.sg_PlayerNinjaCrawl);
		SaveData(s_PlayerNinjaSwim);
		SaveGroup(PlayerStateGroup.sg_PlayerNinjaSwim);
		SaveData(s_PlayerHeadFly);
		SaveGroup(PlayerStateGroup.sg_PlayerHeadFly);
		SaveData(s_PlayerHead);
		SaveGroup(PlayerStateGroup.sg_PlayerHead);
		SaveData(s_PlayerHeadHurl);
		SaveGroup(PlayerStateGroup.sg_PlayerHeadHurl);
		SaveData(s_PlayerDeath);
		SaveGroup(PlayerStateGroup.sg_PlayerDeath);
		SaveData(s_PlayerNinjaSword);
		SaveGroup(PlayerStateGroup.sg_PlayerNinjaSword);
		SaveData(s_PlayerNinjaPunch);
		SaveGroup(PlayerStateGroup.sg_PlayerNinjaPunch);
		SaveData(s_PlayerNinjaFly);
		SaveGroup(PlayerStateGroup.sg_PlayerNinjaFly);
	}
}
