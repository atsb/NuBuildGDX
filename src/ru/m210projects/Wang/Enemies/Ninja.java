package ru.m210projects.Wang.Enemies;

import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Wang.Actor.DoActorDeathMove;
import static ru.m210projects.Wang.Actor.DoActorDebris;
import static ru.m210projects.Wang.Actor.DoActorFall;
import static ru.m210projects.Wang.Actor.DoActorJump;
import static ru.m210projects.Wang.Actor.DoActorSectorDamage;
import static ru.m210projects.Wang.Actor.DoActorSlide;
import static ru.m210projects.Wang.Actor.KeepActorOnFloor;
import static ru.m210projects.Wang.Ai.CanSeePlayer;
import static ru.m210projects.Wang.Ai.DoActorSetSpeed;
import static ru.m210projects.Wang.Ai.InitActorAmbientNoise;
import static ru.m210projects.Wang.Ai.InitActorAttack;
import static ru.m210projects.Wang.Ai.InitActorDecide;
import static ru.m210projects.Wang.Ai.InitActorDuck;
import static ru.m210projects.Wang.Ai.InitActorFindPlayer;
import static ru.m210projects.Wang.Ai.InitActorMoveCloser;
import static ru.m210projects.Wang.Ai.InitActorReposition;
import static ru.m210projects.Wang.Ai.InitActorWanderAround;
import static ru.m210projects.Wang.Ai.NORM_SPEED;
import static ru.m210projects.Wang.Digi.DIGI_NINJAALERT;
import static ru.m210projects.Wang.Digi.DIGI_NINJAAMBIENT;
import static ru.m210projects.Wang.Digi.DIGI_NINJAPAIN;
import static ru.m210projects.Wang.Digi.DIGI_NINJASCREAM;
import static ru.m210projects.Wang.Digi.DIGI_NINJAUZIATTACK;
import static ru.m210projects.Wang.Digi.DIGI_STAR;
import static ru.m210projects.Wang.Enemies.Coolie.EnemyDefaults;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.puser;
import static ru.m210projects.Wang.Game.screenpeek;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK_HITSCAN;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BREAKABLE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_RESTORE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_TRANSLUCENT;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_YCENTER;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_YFLIP;
import static ru.m210projects.Wang.Gameutils.HEALTH_NINJA;
import static ru.m210projects.Wang.Gameutils.HEALTH_RED_NINJA;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.PF_DEAD;
import static ru.m210projects.Wang.Gameutils.PF_DIVING;
import static ru.m210projects.Wang.Gameutils.PF_PICKED_UP_AN_UZI;
import static ru.m210projects.Wang.Gameutils.PF_TWO_UZI;
import static ru.m210projects.Wang.Gameutils.PF_WEAPON_DOWN;
import static ru.m210projects.Wang.Gameutils.PF_WEAPON_RETRACT;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.SF_QUICK_CALL;
import static ru.m210projects.Wang.Gameutils.SF_TIC_ADJUST;
import static ru.m210projects.Wang.Gameutils.SPR2_DYING;
import static ru.m210projects.Wang.Gameutils.SPRX_BREAKABLE;
import static ru.m210projects.Wang.Gameutils.SPRX_PLAYER_OR_ENEMY;
import static ru.m210projects.Wang.Gameutils.SPR_CLIMBING;
import static ru.m210projects.Wang.Gameutils.SPR_DEAD;
import static ru.m210projects.Wang.Gameutils.SPR_FALLING;
import static ru.m210projects.Wang.Gameutils.SPR_JUMPING;
import static ru.m210projects.Wang.Gameutils.SPR_SLIDING;
import static ru.m210projects.Wang.Gameutils.SPR_XFLIP_TOGGLE;
import static ru.m210projects.Wang.Gameutils.WPN_FIST;
import static ru.m210projects.Wang.Gameutils.WPN_STAR;
import static ru.m210projects.Wang.Gameutils.WPN_SWORD;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.JWeapon.InitBloodSpray;
import static ru.m210projects.Wang.JWeapon.InitFlashBomb;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Names.NINJA_CLIMB_R0;
import static ru.m210projects.Wang.Names.NINJA_CLIMB_R1;
import static ru.m210projects.Wang.Names.NINJA_CLIMB_R2;
import static ru.m210projects.Wang.Names.NINJA_CLIMB_R3;
import static ru.m210projects.Wang.Names.NINJA_CLIMB_R4;
import static ru.m210projects.Wang.Names.NINJA_CRAWL_R0;
import static ru.m210projects.Wang.Names.NINJA_CRAWL_R1;
import static ru.m210projects.Wang.Names.NINJA_CRAWL_R2;
import static ru.m210projects.Wang.Names.NINJA_CRAWL_R3;
import static ru.m210projects.Wang.Names.NINJA_CRAWL_R4;
import static ru.m210projects.Wang.Names.NINJA_DIE;
import static ru.m210projects.Wang.Names.NINJA_FIRE_R0;
import static ru.m210projects.Wang.Names.NINJA_FIRE_R1;
import static ru.m210projects.Wang.Names.NINJA_FIRE_R2;
import static ru.m210projects.Wang.Names.NINJA_FIRE_R3;
import static ru.m210projects.Wang.Names.NINJA_FIRE_R4;
import static ru.m210projects.Wang.Names.NINJA_FLY_R0;
import static ru.m210projects.Wang.Names.NINJA_FLY_R1;
import static ru.m210projects.Wang.Names.NINJA_FLY_R2;
import static ru.m210projects.Wang.Names.NINJA_FLY_R3;
import static ru.m210projects.Wang.Names.NINJA_FLY_R4;
import static ru.m210projects.Wang.Names.NINJA_HARI_KARI_R0;
import static ru.m210projects.Wang.Names.NINJA_JUMP_R0;
import static ru.m210projects.Wang.Names.NINJA_JUMP_R1;
import static ru.m210projects.Wang.Names.NINJA_JUMP_R2;
import static ru.m210projects.Wang.Names.NINJA_JUMP_R3;
import static ru.m210projects.Wang.Names.NINJA_JUMP_R4;
import static ru.m210projects.Wang.Names.NINJA_KNEEL_R0;
import static ru.m210projects.Wang.Names.NINJA_KNEEL_R1;
import static ru.m210projects.Wang.Names.NINJA_KNEEL_R2;
import static ru.m210projects.Wang.Names.NINJA_KNEEL_R3;
import static ru.m210projects.Wang.Names.NINJA_KNEEL_R4;
import static ru.m210projects.Wang.Names.NINJA_PAIN_R0;
import static ru.m210projects.Wang.Names.NINJA_RUN_R0;
import static ru.m210projects.Wang.Names.NINJA_RUN_R1;
import static ru.m210projects.Wang.Names.NINJA_RUN_R2;
import static ru.m210projects.Wang.Names.NINJA_RUN_R3;
import static ru.m210projects.Wang.Names.NINJA_RUN_R4;
import static ru.m210projects.Wang.Names.NINJA_SLICED;
import static ru.m210projects.Wang.Names.NINJA_STAND_R0;
import static ru.m210projects.Wang.Names.NINJA_STAND_R1;
import static ru.m210projects.Wang.Names.NINJA_STAND_R2;
import static ru.m210projects.Wang.Names.NINJA_STAND_R3;
import static ru.m210projects.Wang.Names.NINJA_STAND_R4;
import static ru.m210projects.Wang.Names.NINJA_SWIM_R0;
import static ru.m210projects.Wang.Names.NINJA_SWIM_R1;
import static ru.m210projects.Wang.Names.NINJA_SWIM_R2;
import static ru.m210projects.Wang.Names.NINJA_SWIM_R3;
import static ru.m210projects.Wang.Names.NINJA_SWIM_R4;
import static ru.m210projects.Wang.Names.NINJA_THROW_R0;
import static ru.m210projects.Wang.Names.NINJA_THROW_R1;
import static ru.m210projects.Wang.Names.NINJA_THROW_R2;
import static ru.m210projects.Wang.Names.STAT_DEAD_ACTOR;
import static ru.m210projects.Wang.Names.STAT_PLAYER0;
import static ru.m210projects.Wang.Names.STAT_PLAYER_UNDER0;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER0;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER3;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER5;
import static ru.m210projects.Wang.Palette.PAL_XLAT_LT_GREY;
import static ru.m210projects.Wang.Palette.PAL_XLAT_LT_TAN;
import static ru.m210projects.Wang.Panel.PlayerUpdateArmor;
import static ru.m210projects.Wang.Panel.PlayerUpdateHealth;
import static ru.m210projects.Wang.Panel.PlayerUpdatePanelInfo;
import static ru.m210projects.Wang.Panel.PlayerUpdateWeapon;
import static ru.m210projects.Wang.Player.DoPlayerResetMovement;
import static ru.m210projects.Wang.Player.DoPlayerStopDiveNoWarp;
import static ru.m210projects.Wang.Player.DoPlayerZrange;
import static ru.m210projects.Wang.Player.PLAYER_NINJA_XREPEAT;
import static ru.m210projects.Wang.Player.PLAYER_NINJA_YREPEAT;
import static ru.m210projects.Wang.Player.QueueFloorBlood;
import static ru.m210projects.Wang.Sound.COVER_SetReverb;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.NewStateGroup;
import static ru.m210projects.Wang.Sprites.SpawnSprite;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Sprites.change_sprite_stat;
import static ru.m210projects.Wang.Sprites.move_actor;
import static ru.m210projects.Wang.Track.ActorFollowTrack;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.*;
import static ru.m210projects.Wang.Weapon.DamageData;
import static ru.m210projects.Wang.Weapon.InitEnemyMirv;
import static ru.m210projects.Wang.Weapon.InitEnemyNapalm;
import static ru.m210projects.Wang.Weapon.InitEnemyRocket;
import static ru.m210projects.Wang.Weapon.InitEnemyStar;
import static ru.m210projects.Wang.Weapon.InitEnemyUzi;
import static ru.m210projects.Wang.Weapon.InitSpriteGrenade;
import static ru.m210projects.Wang.Weapon.SpawnBlood;
import static ru.m210projects.Wang.Weapon.UpdateSinglePlayKills;

import java.util.Arrays;

import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Player.PlayerStateGroup;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Factory.WangNetwork.MultiGameTypes;
import ru.m210projects.Wang.Type.ATTRIBUTE;
import ru.m210projects.Wang.Type.Actor_Action_Set;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;

public class Ninja {

	/*
	 * 
	 * !AIC - Decision tables used in mostly ai.c DoActorActionDecide().
	 * 
	 */

	public static final Decision NinjaBattle[] = { new Decision(499, InitActorMoveCloser),
			new Decision(1024, InitActorAttack), };

	public static final Decision NinjaOffense[] = { new Decision(499, InitActorMoveCloser),
			new Decision(1024, InitActorAttack), };

	public static final Decision NinjaBroadcast[] = { new Decision(6, InitActorAmbientNoise),
			new Decision(1024, InitActorDecide), };

	public static final Decision NinjaSurprised[] = { new Decision(701, InitActorMoveCloser),
			new Decision(1024, InitActorDecide), };

	public static final Decision NinjaEvasive[] = { new Decision(400, InitActorDuck), new Decision(1024, null), };

	public static final Decision NinjaLostTarget[] = { new Decision(900, InitActorFindPlayer),
			new Decision(1024, InitActorWanderAround), };

	public static final Decision NinjaCloseRange[] = { new Decision(700, InitActorAttack),
			new Decision(1024, InitActorReposition), };

	/*
	 * 
	 * !AIC - Collection of Decision tables
	 * 
	 */

	public static final Personality NinjaPersonality = new Personality(NinjaBattle, NinjaOffense, NinjaBroadcast,
			NinjaSurprised, NinjaEvasive, NinjaLostTarget, NinjaCloseRange, NinjaCloseRange);

	// Sniper Ninjas
	public static final Decision NinjaSniperRoam[] = { new Decision(1023, InitActorDuck),
			new Decision(1024, InitActorAmbientNoise), };

	public static final Decision NinjaSniperBattle[] = { new Decision(499, InitActorDuck),
			new Decision(500, InitActorAmbientNoise), new Decision(1024, InitActorAttack), };

	public static final Personality NinjaSniperPersonality = new Personality(NinjaSniperBattle, NinjaSniperBattle,
			NinjaSniperRoam, NinjaSniperRoam, NinjaSniperRoam, NinjaSniperRoam, NinjaSniperBattle, NinjaSniperBattle);

	/*
	 * 
	 * !AIC - Extra attributes - speeds for running, animation tic adjustments for
	 * speeeds, etc
	 * 
	 */

	private static final ATTRIBUTE NinjaAttrib = new ATTRIBUTE(new short[] { 110, 130, 150, 180 }, // Speeds
			new short[] { 4, 0, 0, -2 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { DIGI_NINJAAMBIENT, DIGI_NINJAALERT, DIGI_STAR, DIGI_NINJAPAIN, DIGI_NINJASCREAM, 0, 0, 0, 0,
					0 });

	private static final ATTRIBUTE InvisibleNinjaAttrib = new ATTRIBUTE(new short[] { 220, 240, 300, 360 }, // Speeds
			new short[] { 4, 0, 0, -2 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { DIGI_NINJAAMBIENT, DIGI_NINJAALERT, DIGI_STAR, DIGI_NINJAPAIN, DIGI_NINJASCREAM, 0, 0, 0, 0,
					0 });

	public static final Animator DoNinjaSpecial = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoNinjaSpecial(spr) != 0;
		}
	};

	public static final Animator DoNinjaMove = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoNinjaMove(spr) != 0;
		}
	};

	//////////////////////
	//
	// NINJA RUN
	//
	//////////////////////

	public static final int NINJA_RATE = 15;

	public static final State[][] s_NinjaRun = {
			{ new State(NINJA_RUN_R0 + 0, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove),
					new State(NINJA_RUN_R0 + 1, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove),
					new State(NINJA_RUN_R0 + 2, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove),
					new State(NINJA_RUN_R0 + 3, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove), },
			{ new State(NINJA_RUN_R1 + 0, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove),
					new State(NINJA_RUN_R1 + 1, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove),
					new State(NINJA_RUN_R1 + 2, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove),
					new State(NINJA_RUN_R1 + 3, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove), },
			{ new State(NINJA_RUN_R2 + 0, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove),
					new State(NINJA_RUN_R2 + 1, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove),
					new State(NINJA_RUN_R2 + 2, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove),
					new State(NINJA_RUN_R2 + 3, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove), },
			{ new State(NINJA_RUN_R3 + 0, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove),
					new State(NINJA_RUN_R3 + 1, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove),
					new State(NINJA_RUN_R3 + 2, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove),
					new State(NINJA_RUN_R3 + 3, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove), },
			{ new State(NINJA_RUN_R4 + 0, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove),
					new State(NINJA_RUN_R4 + 1, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove),
					new State(NINJA_RUN_R4 + 2, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove),
					new State(NINJA_RUN_R4 + 3, NINJA_RATE | SF_TIC_ADJUST, DoNinjaMove), }, };

	//////////////////////
	//
	// NINJA STAND
	//
	//////////////////////

	public static final int NINJA_STAND_RATE = 10;

	public static final State[][] s_NinjaStand = {
			{ new State(NINJA_STAND_R0 + 0, NINJA_STAND_RATE, DoNinjaMove).setNext(), },
			{ new State(NINJA_STAND_R1 + 0, NINJA_STAND_RATE, DoNinjaMove).setNext(), },
			{ new State(NINJA_STAND_R2 + 0, NINJA_STAND_RATE, DoNinjaMove).setNext(), },
			{ new State(NINJA_STAND_R3 + 0, NINJA_STAND_RATE, DoNinjaMove).setNext(), },
			{ new State(NINJA_STAND_R4 + 0, NINJA_STAND_RATE, DoNinjaMove).setNext(), } };

	//////////////////////
	//
	// NINJA RISE
	//
	//////////////////////

	public static final int NINJA_RISE_RATE = 10;

	public static final Animator nullNinja = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return NullNinja(spr) != 0;
		}
	};

	public static final State[][] s_NinjaRise = {
			{ new State(NINJA_KNEEL_R0 + 0, NINJA_RISE_RATE, nullNinja),
					new State(NINJA_STAND_R0 + 0, NINJA_STAND_RATE, nullNinja),
					new State(0, 0, null).setNext(s_NinjaRun[0][0]),
					new State(0, 0, null).setNext(s_NinjaRun[0][0]) },
			{ new State(NINJA_KNEEL_R1 + 0, NINJA_RISE_RATE, nullNinja),
					new State(NINJA_STAND_R1 + 0, NINJA_STAND_RATE, nullNinja),
					new State(0, 0, null).setNext(s_NinjaRun[0][0]),
					new State(0, 0, null).setNext(s_NinjaRun[0][0]) },
			{ new State(NINJA_KNEEL_R2 + 0, NINJA_RISE_RATE, nullNinja),
					new State(NINJA_STAND_R2 + 0, NINJA_STAND_RATE, nullNinja),
					new State(0, 0, null).setNext(s_NinjaRun[0][0]),
					new State(0, 0, null).setNext(s_NinjaRun[0][0]) },
			{ new State(NINJA_KNEEL_R3 + 0, NINJA_RISE_RATE, nullNinja),
					new State(NINJA_STAND_R3 + 0, NINJA_STAND_RATE, nullNinja),
					new State(0, 0, null).setNext(s_NinjaRun[0][0]),
					new State(0, 0, null).setNext(s_NinjaRun[0][0]) },
			{ new State(NINJA_KNEEL_R4 + 0, NINJA_RISE_RATE, nullNinja),
					new State(NINJA_STAND_R4 + 0, NINJA_STAND_RATE, nullNinja),
					new State(0, 0, null).setNext(s_NinjaRun[0][0]),
					new State(0, 0, null).setNext(s_NinjaRun[0][0]) }, };

	//////////////////////
	//
	// NINJA CRAWL
	//
	//////////////////////

	public static final int NINJA_CRAWL_RATE = 14;
	public static final State s_NinjaCrawl[][] = {
			{ new State(NINJA_CRAWL_R0 + 0, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R0 + 1, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R0 + 2, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R0 + 1, NINJA_CRAWL_RATE, DoNinjaMove), },
			{ new State(NINJA_CRAWL_R1 + 0, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R1 + 1, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R1 + 2, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R1 + 1, NINJA_CRAWL_RATE, DoNinjaMove), },
			{ new State(NINJA_CRAWL_R2 + 0, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R2 + 1, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R2 + 2, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R2 + 1, NINJA_CRAWL_RATE, DoNinjaMove), },
			{ new State(NINJA_CRAWL_R3 + 0, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R3 + 1, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R3 + 2, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R3 + 1, NINJA_CRAWL_RATE, DoNinjaMove), },
			{ new State(NINJA_CRAWL_R4 + 0, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R4 + 1, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R4 + 2, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R4 + 1, NINJA_CRAWL_RATE, DoNinjaMove), }, };

	//////////////////////
	//
	// NINJA KNEEL_CRAWL
	//
	//////////////////////

	public static final int NINJA_KNEEL_CRAWL_RATE = 20;

	public static final State s_NinjaKneelCrawl[][] = {
			{ new State(NINJA_KNEEL_R0 + 0, NINJA_KNEEL_CRAWL_RATE, nullNinja),
					new State(NINJA_CRAWL_R0 + 0, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R0 + 1, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R0 + 2, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R0 + 1, NINJA_CRAWL_RATE, DoNinjaMove), },
			{ new State(NINJA_KNEEL_R1 + 0, NINJA_KNEEL_CRAWL_RATE, nullNinja),
					new State(NINJA_CRAWL_R1 + 0, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R1 + 1, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R1 + 2, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R1 + 1, NINJA_CRAWL_RATE, DoNinjaMove), },
			{ new State(NINJA_KNEEL_R2 + 0, NINJA_KNEEL_CRAWL_RATE, nullNinja),
					new State(NINJA_CRAWL_R2 + 0, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R2 + 1, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R2 + 2, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R2 + 1, NINJA_CRAWL_RATE, DoNinjaMove), },
			{ new State(NINJA_KNEEL_R3 + 0, NINJA_KNEEL_CRAWL_RATE, nullNinja),
					new State(NINJA_CRAWL_R3 + 0, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R3 + 1, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R3 + 2, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R3 + 1, NINJA_CRAWL_RATE, DoNinjaMove), },
			{ new State(NINJA_KNEEL_R4 + 0, NINJA_KNEEL_CRAWL_RATE, nullNinja),
					new State(NINJA_CRAWL_R4 + 0, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R4 + 1, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R4 + 2, NINJA_CRAWL_RATE, DoNinjaMove),
					new State(NINJA_CRAWL_R4 + 1, NINJA_CRAWL_RATE, DoNinjaMove), }, };

	//////////////////////
	//
	// NINJA DUCK
	//
	//////////////////////

	public static final int NINJA_DUCK_RATE = 10;

	public static final State s_NinjaDuck[][] = {
			{ new State(NINJA_KNEEL_R0 + 0, NINJA_DUCK_RATE, nullNinja),
					new State(NINJA_CRAWL_R0 + 0, NINJA_CRAWL_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_KNEEL_R1 + 0, NINJA_DUCK_RATE, nullNinja),
					new State(NINJA_CRAWL_R1 + 0, NINJA_CRAWL_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_KNEEL_R2 + 0, NINJA_DUCK_RATE, nullNinja),
					new State(NINJA_CRAWL_R2 + 0, NINJA_CRAWL_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_KNEEL_R3 + 0, NINJA_DUCK_RATE, nullNinja),
					new State(NINJA_CRAWL_R3 + 0, NINJA_CRAWL_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_KNEEL_R4 + 0, NINJA_DUCK_RATE, nullNinja),
					new State(NINJA_CRAWL_R4 + 0, NINJA_CRAWL_RATE, DoNinjaMove).setNext() }, };

	//////////////////////
	//
	// NINJA SIT
	//
	//////////////////////

	public static final State s_NinjaSit[][] = {
			{ new State(NINJA_KNEEL_R0 + 0, NINJA_RISE_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_KNEEL_R1 + 0, NINJA_RISE_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_KNEEL_R2 + 0, NINJA_RISE_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_KNEEL_R3 + 0, NINJA_RISE_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_KNEEL_R4 + 0, NINJA_RISE_RATE, DoNinjaMove).setNext() }, };

	//////////////////////
	//
	// NINJA CEILING
	//
	//////////////////////

	private static final Animator DoNinjaCeiling = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoActorSectorDamage(spr);
		}
	};

	public static final State s_NinjaCeiling[][] = {
			{ new State(NINJA_KNEEL_R0 + 0, NINJA_RISE_RATE, DoNinjaCeiling).setNext() },
			{ new State(NINJA_KNEEL_R1 + 0, NINJA_RISE_RATE, DoNinjaCeiling).setNext() },
			{ new State(NINJA_KNEEL_R2 + 0, NINJA_RISE_RATE, DoNinjaCeiling).setNext() },
			{ new State(NINJA_KNEEL_R3 + 0, NINJA_RISE_RATE, DoNinjaCeiling).setNext() },
			{ new State(NINJA_KNEEL_R4 + 0, NINJA_RISE_RATE, DoNinjaCeiling).setNext() }, };

	//////////////////////
	//
	// NINJA JUMP
	//
	//////////////////////

	public static final int NINJA_JUMP_RATE = 24;

	public static final State s_NinjaJump[][] = {
			{ new State(NINJA_JUMP_R0 + 0, NINJA_JUMP_RATE, DoNinjaMove),
					new State(NINJA_JUMP_R0 + 1, NINJA_JUMP_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_JUMP_R1 + 0, NINJA_JUMP_RATE, DoNinjaMove),
					new State(NINJA_JUMP_R1 + 1, NINJA_JUMP_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_JUMP_R2 + 0, NINJA_JUMP_RATE, DoNinjaMove),
					new State(NINJA_JUMP_R2 + 1, NINJA_JUMP_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_JUMP_R3 + 0, NINJA_JUMP_RATE, DoNinjaMove),
					new State(NINJA_JUMP_R3 + 1, NINJA_JUMP_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_JUMP_R4 + 0, NINJA_JUMP_RATE, DoNinjaMove),
					new State(NINJA_JUMP_R4 + 1, NINJA_JUMP_RATE, DoNinjaMove).setNext() }, };

	//////////////////////
	//
	// NINJA FALL
	//
	//////////////////////

	public static final int NINJA_FALL_RATE = 16;

	public static final State s_NinjaFall[][] = {
			{ new State(NINJA_JUMP_R0 + 1, NINJA_FALL_RATE, DoNinjaMove),
					new State(NINJA_JUMP_R0 + 2, NINJA_FALL_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_JUMP_R1 + 1, NINJA_FALL_RATE, DoNinjaMove),
					new State(NINJA_JUMP_R1 + 2, NINJA_FALL_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_JUMP_R2 + 1, NINJA_FALL_RATE, DoNinjaMove),
					new State(NINJA_JUMP_R2 + 2, NINJA_FALL_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_JUMP_R3 + 1, NINJA_FALL_RATE, DoNinjaMove),
					new State(NINJA_JUMP_R3 + 2, NINJA_FALL_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_JUMP_R4 + 1, NINJA_FALL_RATE, DoNinjaMove),
					new State(NINJA_JUMP_R4 + 2, NINJA_FALL_RATE, DoNinjaMove).setNext() }, };

	//////////////////////
	//
	// NINJA SWIM
	//
	//////////////////////

	public static final int NINJA_SWIM_RATE = 18;
	public static final State s_NinjaSwim[][] = {
			{ new State(NINJA_SWIM_R0 + 1, NINJA_SWIM_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R0 + 2, NINJA_SWIM_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R0 + 3, NINJA_SWIM_RATE, DoNinjaMove), },
			{ new State(NINJA_SWIM_R1 + 1, NINJA_SWIM_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R1 + 2, NINJA_SWIM_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R1 + 3, NINJA_SWIM_RATE, DoNinjaMove), },
			{ new State(NINJA_SWIM_R2 + 1, NINJA_SWIM_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R2 + 2, NINJA_SWIM_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R2 + 3, NINJA_SWIM_RATE, DoNinjaMove), },
			{ new State(NINJA_SWIM_R3 + 1, NINJA_SWIM_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R3 + 2, NINJA_SWIM_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R3 + 3, NINJA_SWIM_RATE, DoNinjaMove), },
			{ new State(NINJA_SWIM_R4 + 1, NINJA_SWIM_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R4 + 2, NINJA_SWIM_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R4 + 3, NINJA_SWIM_RATE, DoNinjaMove), }, };

	//////////////////////
	//
	// NINJA DIVE
	//
	//////////////////////

	public static final int NINJA_DIVE_RATE = 23;

	public static final State s_NinjaDive[][] = {
			{ new State(NINJA_SWIM_R0 + 0, NINJA_DIVE_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R0 + 1, NINJA_DIVE_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R0 + 2, NINJA_DIVE_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R0 + 3, NINJA_DIVE_RATE, DoNinjaMove), },
			{ new State(NINJA_SWIM_R1 + 0, NINJA_DIVE_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R1 + 1, NINJA_DIVE_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R1 + 2, NINJA_DIVE_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R1 + 3, NINJA_DIVE_RATE, DoNinjaMove), },
			{ new State(NINJA_SWIM_R2 + 0, NINJA_DIVE_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R2 + 1, NINJA_DIVE_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R2 + 2, NINJA_DIVE_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R2 + 3, NINJA_DIVE_RATE, DoNinjaMove), },
			{ new State(NINJA_SWIM_R3 + 0, NINJA_DIVE_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R3 + 1, NINJA_DIVE_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R3 + 2, NINJA_DIVE_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R3 + 3, NINJA_DIVE_RATE, DoNinjaMove), },
			{ new State(NINJA_SWIM_R4 + 0, NINJA_DIVE_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R4 + 1, NINJA_DIVE_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R4 + 2, NINJA_DIVE_RATE, DoNinjaMove),
					new State(NINJA_SWIM_R4 + 3, NINJA_DIVE_RATE, DoNinjaMove), }, };

	//////////////////////
	//
	// NINJA CLIMB
	//
	//////////////////////

	public static final int NINJA_CLIMB_RATE = 20;
	public static final State s_NinjaClimb[][] = {
			{ new State(NINJA_CLIMB_R0 + 0, NINJA_CLIMB_RATE, DoNinjaMove),
					new State(NINJA_CLIMB_R0 + 1, NINJA_CLIMB_RATE, DoNinjaMove),
					new State(NINJA_CLIMB_R0 + 2, NINJA_CLIMB_RATE, DoNinjaMove),
					new State(NINJA_CLIMB_R0 + 3, NINJA_CLIMB_RATE, DoNinjaMove), },
			{ new State(NINJA_CLIMB_R1 + 0, NINJA_CLIMB_RATE, DoNinjaMove),
					new State(NINJA_CLIMB_R1 + 1, NINJA_CLIMB_RATE, DoNinjaMove),
					new State(NINJA_CLIMB_R1 + 2, NINJA_CLIMB_RATE, DoNinjaMove),
					new State(NINJA_CLIMB_R1 + 3, NINJA_CLIMB_RATE, DoNinjaMove), },
			{ new State(NINJA_CLIMB_R4 + 0, NINJA_CLIMB_RATE, DoNinjaMove),
					new State(NINJA_CLIMB_R4 + 1, NINJA_CLIMB_RATE, DoNinjaMove),
					new State(NINJA_CLIMB_R4 + 2, NINJA_CLIMB_RATE, DoNinjaMove),
					new State(NINJA_CLIMB_R4 + 3, NINJA_CLIMB_RATE, DoNinjaMove), },
			{ new State(NINJA_CLIMB_R3 + 0, NINJA_CLIMB_RATE, DoNinjaMove),
					new State(NINJA_CLIMB_R3 + 1, NINJA_CLIMB_RATE, DoNinjaMove),
					new State(NINJA_CLIMB_R3 + 2, NINJA_CLIMB_RATE, DoNinjaMove),
					new State(NINJA_CLIMB_R3 + 3, NINJA_CLIMB_RATE, DoNinjaMove), },
			{ new State(NINJA_CLIMB_R2 + 0, NINJA_CLIMB_RATE, DoNinjaMove),
					new State(NINJA_CLIMB_R2 + 1, NINJA_CLIMB_RATE, DoNinjaMove),
					new State(NINJA_CLIMB_R2 + 2, NINJA_CLIMB_RATE, DoNinjaMove),
					new State(NINJA_CLIMB_R2 + 3, NINJA_CLIMB_RATE, DoNinjaMove), }, };

	//////////////////////
	//
	// NINJA FLY
	//
	//////////////////////

	public static final int NINJA_FLY_RATE = 12;

	public static final State s_NinjaFly[][] = { { new State(NINJA_FLY_R0 + 0, NINJA_FLY_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_FLY_R1 + 0, NINJA_FLY_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_FLY_R2 + 0, NINJA_FLY_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_FLY_R3 + 0, NINJA_FLY_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_FLY_R4 + 0, NINJA_FLY_RATE, DoNinjaMove).setNext() }, };

	//////////////////////
	//
	// NINJA PAIN
	//
	//////////////////////

	public static final int NINJA_PAIN_RATE = 15;

	public static final Animator DoNinjaPain = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];

			NullNinja(SpriteNum);

			if (TEST(u.Flags2, SPR2_DYING)) {
				NewStateGroup(SpriteNum, NinjaStateGroup.sg_NinjaGrabThroat);
				return false;
			}

			if ((u.WaitTics -= ACTORMOVETICS) <= 0)
				InitActorDecide(SpriteNum);

			return false;
		}
	};

	public static final State s_NinjaPain[][] = {
			{ new State(NINJA_PAIN_R0 + 0, NINJA_PAIN_RATE, DoNinjaPain),
					new State(NINJA_PAIN_R0 + 1, NINJA_PAIN_RATE, DoNinjaPain).setNext() },
			{ new State(NINJA_STAND_R1 + 0, NINJA_PAIN_RATE, DoNinjaPain),
					new State(NINJA_STAND_R1 + 0, NINJA_PAIN_RATE, DoNinjaPain).setNext() },
			{ new State(NINJA_STAND_R2 + 0, NINJA_PAIN_RATE, DoNinjaPain),
					new State(NINJA_STAND_R2 + 0, NINJA_PAIN_RATE, DoNinjaPain).setNext() },
			{ new State(NINJA_STAND_R3 + 0, NINJA_PAIN_RATE, DoNinjaPain),
					new State(NINJA_STAND_R3 + 0, NINJA_PAIN_RATE, DoNinjaPain).setNext() },
			{ new State(NINJA_STAND_R4 + 0, NINJA_PAIN_RATE, DoNinjaPain),
					new State(NINJA_STAND_R4 + 0, NINJA_PAIN_RATE, DoNinjaPain).setNext() }, };

	//////////////////////
	//
	// NINJA STAR
	//
	//////////////////////

	public static final int NINJA_STAR_RATE = 18;

	public static final Animator InitEnemyStar = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return InitEnemyStar(spr) != 0;
		}
	};

	public static final State s_NinjaStar[][] = {
			{ new State(NINJA_THROW_R0 + 0, NINJA_STAR_RATE * 2, nullNinja),
					new State(NINJA_THROW_R0 + 0, NINJA_STAR_RATE, nullNinja),
					new State(NINJA_THROW_R0 + 1, 0 | SF_QUICK_CALL, InitEnemyStar),
					new State(NINJA_THROW_R0 + 1, NINJA_STAR_RATE * 2, nullNinja),
					new State(NINJA_THROW_R0 + 2, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_THROW_R0 + 2, NINJA_STAR_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_THROW_R1 + 0, NINJA_STAR_RATE * 2, nullNinja),
					new State(NINJA_THROW_R1 + 0, NINJA_STAR_RATE, nullNinja),
					new State(NINJA_THROW_R1 + 1, 0 | SF_QUICK_CALL, InitEnemyStar),
					new State(NINJA_THROW_R1 + 1, NINJA_STAR_RATE * 2, nullNinja),
					new State(NINJA_THROW_R1 + 2, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_THROW_R1 + 2, NINJA_STAR_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_THROW_R2 + 0, NINJA_STAR_RATE * 2, nullNinja),
					new State(NINJA_THROW_R2 + 0, NINJA_STAR_RATE, nullNinja),
					new State(NINJA_THROW_R2 + 1, 0 | SF_QUICK_CALL, InitEnemyStar),
					new State(NINJA_THROW_R2 + 1, NINJA_STAR_RATE * 2, nullNinja),
					new State(NINJA_THROW_R2 + 2, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_THROW_R2 + 2, NINJA_STAR_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_THROW_R2 + 0, NINJA_STAR_RATE * 2, nullNinja),
					new State(NINJA_THROW_R2 + 0, NINJA_STAR_RATE, nullNinja),
					new State(NINJA_THROW_R2 + 1, 0 | SF_QUICK_CALL, InitEnemyStar),
					new State(NINJA_THROW_R2 + 1, NINJA_STAR_RATE * 2, nullNinja),
					new State(NINJA_THROW_R2 + 2, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_THROW_R2 + 2, NINJA_STAR_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_THROW_R2 + 0, NINJA_STAR_RATE * 2, nullNinja),
					new State(NINJA_THROW_R2 + 0, NINJA_STAR_RATE, nullNinja),
					new State(NINJA_THROW_R2 + 1, 0 | SF_QUICK_CALL, InitEnemyStar),
					new State(NINJA_THROW_R2 + 1, NINJA_STAR_RATE * 2, nullNinja),
					new State(NINJA_THROW_R2 + 2, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_THROW_R2 + 2, NINJA_STAR_RATE, DoNinjaMove).setNext() }, };

	//////////////////////
	//
	// NINJA MIRV
	//
	//////////////////////

	public static final int NINJA_MIRV_RATE = 18;

	public static final Animator InitEnemyMirv = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return InitEnemyMirv(spr) != 0;
		}
	};

	public static final State s_NinjaMirv[][] = {
			{ new State(NINJA_THROW_R0 + 0, NINJA_MIRV_RATE * 2, nullNinja),
					new State(NINJA_THROW_R0 + 1, NINJA_MIRV_RATE, nullNinja),
					new State(NINJA_THROW_R0 + 2, 0 | SF_QUICK_CALL, InitEnemyMirv),
					new State(NINJA_THROW_R0 + 2, NINJA_MIRV_RATE * 2, nullNinja),
					new State(NINJA_THROW_R0 + 2, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_THROW_R0 + 2, NINJA_MIRV_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_THROW_R1 + 0, NINJA_MIRV_RATE * 2, nullNinja),
					new State(NINJA_THROW_R1 + 1, NINJA_MIRV_RATE, nullNinja),
					new State(NINJA_THROW_R1 + 2, 0 | SF_QUICK_CALL, InitEnemyMirv),
					new State(NINJA_THROW_R1 + 2, NINJA_MIRV_RATE * 2, nullNinja),
					new State(NINJA_THROW_R1 + 2, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_THROW_R1 + 2, NINJA_MIRV_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_THROW_R2 + 0, NINJA_MIRV_RATE * 2, nullNinja),
					new State(NINJA_THROW_R2 + 1, NINJA_MIRV_RATE, nullNinja),
					new State(NINJA_THROW_R2 + 2, 0 | SF_QUICK_CALL, InitEnemyMirv),
					new State(NINJA_THROW_R2 + 2, NINJA_MIRV_RATE * 2, nullNinja),
					new State(NINJA_THROW_R2 + 2, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_THROW_R2 + 2, NINJA_MIRV_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_THROW_R2 + 0, NINJA_MIRV_RATE * 2, nullNinja),
					new State(NINJA_THROW_R2 + 1, NINJA_MIRV_RATE, nullNinja),
					new State(NINJA_THROW_R2 + 2, 0 | SF_QUICK_CALL, InitEnemyMirv),
					new State(NINJA_THROW_R2 + 2, NINJA_MIRV_RATE * 2, nullNinja),
					new State(NINJA_THROW_R2 + 2, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_THROW_R2 + 2, NINJA_MIRV_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_THROW_R2 + 0, NINJA_MIRV_RATE * 2, nullNinja),
					new State(NINJA_THROW_R2 + 1, NINJA_MIRV_RATE, nullNinja),
					new State(NINJA_THROW_R2 + 2, 0 | SF_QUICK_CALL, InitEnemyMirv),
					new State(NINJA_THROW_R2 + 2, NINJA_MIRV_RATE * 2, nullNinja),
					new State(NINJA_THROW_R2 + 2, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_THROW_R2 + 2, NINJA_MIRV_RATE, DoNinjaMove).setNext() }, };

	//////////////////////
	//
	// NINJA NAPALM
	//
	//////////////////////

	public static final int NINJA_NAPALM_RATE = 18;

	public static final Animator InitEnemyNapalm = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return InitEnemyNapalm(spr) != 0;
		}
	};

	public static final State s_NinjaNapalm[][] = {
			{ new State(NINJA_THROW_R0 + 0, NINJA_NAPALM_RATE * 2, nullNinja),
					new State(NINJA_THROW_R0 + 1, NINJA_NAPALM_RATE, nullNinja),
					new State(NINJA_THROW_R0 + 2, 0 | SF_QUICK_CALL, InitEnemyNapalm),
					new State(NINJA_THROW_R0 + 2, NINJA_NAPALM_RATE * 2, nullNinja),
					new State(NINJA_THROW_R0 + 2, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_THROW_R0 + 2, NINJA_NAPALM_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_THROW_R1 + 0, NINJA_NAPALM_RATE * 2, nullNinja),
					new State(NINJA_THROW_R1 + 1, NINJA_NAPALM_RATE, nullNinja),
					new State(NINJA_THROW_R1 + 2, 0 | SF_QUICK_CALL, InitEnemyNapalm),
					new State(NINJA_THROW_R1 + 2, NINJA_NAPALM_RATE * 2, nullNinja),
					new State(NINJA_THROW_R1 + 2, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_THROW_R1 + 2, NINJA_NAPALM_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_THROW_R2 + 0, NINJA_NAPALM_RATE * 2, nullNinja),
					new State(NINJA_THROW_R2 + 1, NINJA_NAPALM_RATE, nullNinja),
					new State(NINJA_THROW_R2 + 2, 0 | SF_QUICK_CALL, InitEnemyNapalm),
					new State(NINJA_THROW_R2 + 2, NINJA_NAPALM_RATE * 2, nullNinja),
					new State(NINJA_THROW_R2 + 2, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_THROW_R2 + 2, NINJA_NAPALM_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_THROW_R2 + 0, NINJA_NAPALM_RATE * 2, nullNinja),
					new State(NINJA_THROW_R2 + 1, NINJA_NAPALM_RATE, nullNinja),
					new State(NINJA_THROW_R2 + 2, 0 | SF_QUICK_CALL, InitEnemyNapalm),
					new State(NINJA_THROW_R2 + 2, NINJA_NAPALM_RATE * 2, nullNinja),
					new State(NINJA_THROW_R2 + 2, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_THROW_R2 + 2, NINJA_NAPALM_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_THROW_R2 + 0, NINJA_NAPALM_RATE * 2, nullNinja),
					new State(NINJA_THROW_R2 + 1, NINJA_NAPALM_RATE, nullNinja),
					new State(NINJA_THROW_R2 + 2, 0 | SF_QUICK_CALL, InitEnemyNapalm),
					new State(NINJA_THROW_R2 + 2, NINJA_NAPALM_RATE * 2, nullNinja),
					new State(NINJA_THROW_R2 + 2, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_THROW_R2 + 2, NINJA_NAPALM_RATE, DoNinjaMove).setNext() }, };

	//////////////////////
	//
	// NINJA ROCKET
	//
	//////////////////////

	public static final int NINJA_ROCKET_RATE = 14;

	public static final Animator InitEnemyRocket = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return InitEnemyRocket(spr) != 0;
		}
	};

	public static final State s_NinjaRocket[][] = {
			{ new State(NINJA_STAND_R0 + 0, NINJA_ROCKET_RATE * 2, nullNinja),
					new State(NINJA_STAND_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyRocket),
					new State(NINJA_STAND_R0 + 0, NINJA_ROCKET_RATE, nullNinja),
					new State(NINJA_STAND_R0 + 0, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_STAND_R0 + 0, NINJA_ROCKET_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_STAND_R1 + 0, NINJA_ROCKET_RATE * 2, nullNinja),
					new State(NINJA_STAND_R1 + 0, 0 | SF_QUICK_CALL, InitEnemyRocket),
					new State(NINJA_STAND_R1 + 0, NINJA_ROCKET_RATE, nullNinja),
					new State(NINJA_STAND_R1 + 0, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_STAND_R1 + 0, NINJA_ROCKET_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_STAND_R2 + 0, NINJA_ROCKET_RATE * 2, nullNinja),
					new State(NINJA_STAND_R2 + 0, 0 | SF_QUICK_CALL, InitEnemyRocket),
					new State(NINJA_STAND_R2 + 0, NINJA_ROCKET_RATE, nullNinja),
					new State(NINJA_STAND_R2 + 0, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_STAND_R2 + 0, NINJA_ROCKET_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_STAND_R3 + 0, NINJA_ROCKET_RATE * 2, nullNinja),
					new State(NINJA_STAND_R3 + 0, 0 | SF_QUICK_CALL, InitEnemyRocket),
					new State(NINJA_STAND_R3 + 0, NINJA_ROCKET_RATE, nullNinja),
					new State(NINJA_STAND_R3 + 0, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_STAND_R3 + 0, NINJA_ROCKET_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_STAND_R4 + 0, NINJA_ROCKET_RATE * 2, nullNinja),
					new State(NINJA_STAND_R4 + 0, 0 | SF_QUICK_CALL, InitEnemyRocket),
					new State(NINJA_STAND_R4 + 0, NINJA_ROCKET_RATE, nullNinja),
					new State(NINJA_STAND_R4 + 0, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_STAND_R4 + 0, NINJA_ROCKET_RATE, DoNinjaMove).setNext() }, };

	//////////////////////
	//
	// NINJA ROCKET
	//
	//////////////////////

	public static final Animator InitSpriteGrenade = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return InitSpriteGrenade(spr) != 0;
		}
	};

	public static final State s_NinjaGrenade[][] = {
			{ new State(NINJA_STAND_R0 + 0, NINJA_ROCKET_RATE * 2, nullNinja),
					new State(NINJA_STAND_R0 + 0, 0 | SF_QUICK_CALL, InitSpriteGrenade),
					new State(NINJA_STAND_R0 + 0, NINJA_ROCKET_RATE, nullNinja),
					new State(NINJA_STAND_R0 + 0, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_STAND_R0 + 0, NINJA_ROCKET_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_STAND_R1 + 0, NINJA_ROCKET_RATE * 2, nullNinja),
					new State(NINJA_STAND_R1 + 0, 0 | SF_QUICK_CALL, InitSpriteGrenade),
					new State(NINJA_STAND_R1 + 0, NINJA_ROCKET_RATE, nullNinja),
					new State(NINJA_STAND_R1 + 0, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_STAND_R1 + 0, NINJA_ROCKET_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_STAND_R2 + 0, NINJA_ROCKET_RATE * 2, nullNinja),
					new State(NINJA_STAND_R2 + 0, 0 | SF_QUICK_CALL, InitSpriteGrenade),
					new State(NINJA_STAND_R2 + 0, NINJA_ROCKET_RATE, nullNinja),
					new State(NINJA_STAND_R2 + 0, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_STAND_R2 + 0, NINJA_ROCKET_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_STAND_R3 + 0, NINJA_ROCKET_RATE * 2, nullNinja),
					new State(NINJA_STAND_R3 + 0, 0 | SF_QUICK_CALL, InitSpriteGrenade),
					new State(NINJA_STAND_R3 + 0, NINJA_ROCKET_RATE, nullNinja),
					new State(NINJA_STAND_R3 + 0, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_STAND_R3 + 0, NINJA_ROCKET_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_STAND_R4 + 0, NINJA_ROCKET_RATE * 2, nullNinja),
					new State(NINJA_STAND_R4 + 0, 0 | SF_QUICK_CALL, InitSpriteGrenade),
					new State(NINJA_STAND_R4 + 0, NINJA_ROCKET_RATE, nullNinja),
					new State(NINJA_STAND_R4 + 0, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_STAND_R4 + 0, NINJA_ROCKET_RATE, DoNinjaMove).setNext() }, };

	//////////////////////
	//
	// NINJA FLASHBOMB
	//
	//////////////////////

	public static final int NINJA_FLASHBOMB_RATE = 14;

	public static final Animator InitFlashBomb = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return InitFlashBomb(spr) != 0;
		}
	};

	public static final State s_NinjaFlashBomb[][] = {
			{ new State(NINJA_STAND_R0 + 0, NINJA_FLASHBOMB_RATE * 2, nullNinja),
					new State(NINJA_STAND_R0 + 0, 0 | SF_QUICK_CALL, InitFlashBomb),
					new State(NINJA_STAND_R0 + 0, NINJA_FLASHBOMB_RATE, nullNinja),
					new State(NINJA_STAND_R0 + 0, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_STAND_R0 + 0, NINJA_FLASHBOMB_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_STAND_R1 + 0, NINJA_FLASHBOMB_RATE * 2, nullNinja),
					new State(NINJA_STAND_R1 + 0, 0 | SF_QUICK_CALL, InitFlashBomb),
					new State(NINJA_STAND_R1 + 0, NINJA_FLASHBOMB_RATE, nullNinja),
					new State(NINJA_STAND_R1 + 0, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_STAND_R1 + 0, NINJA_FLASHBOMB_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_STAND_R2 + 0, NINJA_FLASHBOMB_RATE * 2, nullNinja),
					new State(NINJA_STAND_R2 + 0, 0 | SF_QUICK_CALL, InitFlashBomb),
					new State(NINJA_STAND_R2 + 0, NINJA_FLASHBOMB_RATE, nullNinja),
					new State(NINJA_STAND_R2 + 0, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_STAND_R2 + 0, NINJA_FLASHBOMB_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_STAND_R3 + 0, NINJA_FLASHBOMB_RATE * 2, nullNinja),
					new State(NINJA_STAND_R3 + 0, 0 | SF_QUICK_CALL, InitFlashBomb),
					new State(NINJA_STAND_R3 + 0, NINJA_FLASHBOMB_RATE, nullNinja),
					new State(NINJA_STAND_R3 + 0, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_STAND_R3 + 0, NINJA_FLASHBOMB_RATE, DoNinjaMove).setNext() },
			{ new State(NINJA_STAND_R4 + 0, NINJA_FLASHBOMB_RATE * 2, nullNinja),
					new State(NINJA_STAND_R4 + 0, 0 | SF_QUICK_CALL, InitFlashBomb),
					new State(NINJA_STAND_R4 + 0, NINJA_FLASHBOMB_RATE, nullNinja),
					new State(NINJA_STAND_R4 + 0, 0 | SF_QUICK_CALL, InitActorDecide),
					new State(NINJA_STAND_R4 + 0, NINJA_FLASHBOMB_RATE, DoNinjaMove).setNext() }, };

	//////////////////////
	//
	// NINJA UZI
	//
	//////////////////////

	public static final int NINJA_UZI_RATE = 8;

	public static final Animator CheckFire = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return CheckFire(spr) != 0;
		}
	};

	public static final Animator InitEnemyUzi = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return InitEnemyUzi(spr) != 0;
		}
	};

	public static final State s_NinjaUzi[][] = {
			{ new State(NINJA_FIRE_R0 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R0 + 0, 0 | SF_QUICK_CALL, CheckFire),
					new State(NINJA_FIRE_R0 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R0 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R0 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R0 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R0 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R0 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R0 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R0 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R0 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R0 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R0 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R0 + 0, 0 | SF_QUICK_CALL, InitActorDecide).setNext() },
			{ new State(NINJA_FIRE_R1 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R1 + 0, 0 | SF_QUICK_CALL, CheckFire),
					new State(NINJA_FIRE_R1 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R1 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R1 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R1 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R1 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R1 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R1 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R1 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R1 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R1 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R1 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R1 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R1 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R1 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R1 + 0, 0 | SF_QUICK_CALL, InitActorDecide).setNext() },
			{ new State(NINJA_FIRE_R2 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R2 + 0, 0 | SF_QUICK_CALL, CheckFire),
					new State(NINJA_FIRE_R2 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R2 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R2 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R2 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R2 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R2 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R2 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R2 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R2 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R2 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R2 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R2 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R2 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R2 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R2 + 0, 0 | SF_QUICK_CALL, InitActorDecide).setNext() },
			{ new State(NINJA_FIRE_R3 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R3 + 0, 0 | SF_QUICK_CALL, CheckFire),
					new State(NINJA_FIRE_R3 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R3 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R3 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R3 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R3 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R3 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R3 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R3 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R3 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R3 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R3 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R3 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R3 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R3 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R3 + 0, 0 | SF_QUICK_CALL, InitActorDecide).setNext() },
			{ new State(NINJA_FIRE_R4 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R4 + 0, 0 | SF_QUICK_CALL, CheckFire),
					new State(NINJA_FIRE_R4 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R4 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R4 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R4 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R4 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R4 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R4 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R4 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R4 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R4 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R4 + 0, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R4 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R4 + 1, NINJA_UZI_RATE, nullNinja),
					new State(NINJA_FIRE_R4 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),
					new State(NINJA_FIRE_R4 + 0, 0 | SF_QUICK_CALL, InitActorDecide).setNext() }, };

	//////////////////////
	//
	// NINJA HARI KARI
	//
	//////////////////////

	public static final int NINJA_HARI_KARI_WAIT_RATE = 200;
	public static final int NINJA_HARI_KARI_FALL_RATE = 16;

	public static final Animator DoNinjaHariKari = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoNinjaHariKari(spr) != 0;
		}
	};

	public static final State s_NinjaHariKari[] = {
			new State(NINJA_HARI_KARI_R0 + 0, NINJA_HARI_KARI_FALL_RATE, nullNinja),
			new State(NINJA_HARI_KARI_R0 + 0, SF_QUICK_CALL, DoNinjaSpecial),
			new State(NINJA_HARI_KARI_R0 + 1, NINJA_HARI_KARI_WAIT_RATE, nullNinja),
			new State(NINJA_HARI_KARI_R0 + 2, SF_QUICK_CALL, DoNinjaHariKari),
			new State(NINJA_HARI_KARI_R0 + 2, NINJA_HARI_KARI_FALL_RATE, null),
			new State(NINJA_HARI_KARI_R0 + 3, NINJA_HARI_KARI_FALL_RATE, null),
			new State(NINJA_HARI_KARI_R0 + 4, NINJA_HARI_KARI_FALL_RATE, null),
			new State(NINJA_HARI_KARI_R0 + 5, NINJA_HARI_KARI_FALL_RATE, null),
			new State(NINJA_HARI_KARI_R0 + 6, NINJA_HARI_KARI_FALL_RATE, null),
			new State(NINJA_HARI_KARI_R0 + 7, NINJA_HARI_KARI_FALL_RATE, null),
			new State(NINJA_HARI_KARI_R0 + 7, NINJA_HARI_KARI_FALL_RATE, null).setNext() };

	//////////////////////
	//
	// NINJA GRAB THROAT
	//
	//////////////////////

	public static final int NINJA_GRAB_THROAT_RATE = 32;
	public static final int NINJA_GRAB_THROAT_R0 = 4237;

	public static final Animator DoNinjaGrabThroat = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoNinjaGrabThroat(spr) != 0;
		}
	};

	public static final State s_NinjaGrabThroat[] = {
			new State(NINJA_GRAB_THROAT_R0 + 0, NINJA_GRAB_THROAT_RATE, nullNinja),
			new State(NINJA_GRAB_THROAT_R0 + 0, SF_QUICK_CALL, DoNinjaSpecial),
			new State(NINJA_GRAB_THROAT_R0 + 1, NINJA_GRAB_THROAT_RATE, nullNinja),
			new State(NINJA_GRAB_THROAT_R0 + 2, SF_QUICK_CALL, DoNinjaGrabThroat),
			new State(NINJA_GRAB_THROAT_R0 + 2, NINJA_GRAB_THROAT_RATE, nullNinja),
			new State(NINJA_GRAB_THROAT_R0 + 1, NINJA_GRAB_THROAT_RATE, nullNinja), };

	//////////////////////
	//
	// NINJA DIE
	//
	//////////////////////

	public static final int NINJA_DIE_RATE = 14;

	public static final State s_NinjaDie[] = { new State(NINJA_DIE + 0, NINJA_DIE_RATE, nullNinja),
			new State(NINJA_DIE + 1, NINJA_DIE_RATE, nullNinja), new State(NINJA_DIE + 2, NINJA_DIE_RATE, nullNinja),
			new State(NINJA_DIE + 3, NINJA_DIE_RATE, nullNinja), new State(NINJA_DIE + 4, NINJA_DIE_RATE, nullNinja),
			new State(NINJA_DIE + 5, NINJA_DIE_RATE - 4, nullNinja),
			new State(NINJA_DIE + 6, NINJA_DIE_RATE - 6, nullNinja),
			new State(NINJA_DIE + 6, SF_QUICK_CALL, DoNinjaSpecial),
			new State(NINJA_DIE + 6, NINJA_DIE_RATE - 10, nullNinja),
			new State(NINJA_DIE + 7, SF_QUICK_CALL, QueueFloorBlood),
			new State(NINJA_DIE + 7, NINJA_DIE_RATE - 12, DoActorDebris).setNext() };

	public static final int NINJA_DIESLICED_RATE = 20;

	public static final State[] s_NinjaDieSliced = new State[] {
			new State(NINJA_SLICED + 0, NINJA_DIESLICED_RATE * 6, null),
			new State(NINJA_SLICED + 1, NINJA_DIESLICED_RATE, null),
			new State(NINJA_SLICED + 2, NINJA_DIESLICED_RATE, null),
			new State(NINJA_SLICED + 3, NINJA_DIESLICED_RATE, null),
			new State(NINJA_SLICED + 4, NINJA_DIESLICED_RATE - 1, null),
			new State(NINJA_SLICED + 5, NINJA_DIESLICED_RATE - 2, null),
			new State(NINJA_SLICED + 6, NINJA_DIESLICED_RATE - 3, null),
			new State(NINJA_SLICED + 7, NINJA_DIESLICED_RATE - 4, null),
			new State(NINJA_SLICED + 7, SF_QUICK_CALL, DoNinjaSpecial),
			new State(NINJA_SLICED + 8, NINJA_DIESLICED_RATE - 5, null),
			new State(NINJA_SLICED + 9, SF_QUICK_CALL, QueueFloorBlood),
			new State(NINJA_SLICED + 9, NINJA_DIESLICED_RATE, DoActorDebris).setNext() };

	public static final State s_NinjaDead[] = { new State(NINJA_DIE + 5, NINJA_DIE_RATE, DoActorDebris),
			new State(NINJA_DIE + 6, SF_QUICK_CALL, DoNinjaSpecial),
			new State(NINJA_DIE + 6, NINJA_DIE_RATE, DoActorDebris),
			new State(NINJA_DIE + 7, SF_QUICK_CALL, QueueFloorBlood),
			new State(NINJA_DIE + 7, NINJA_DIE_RATE, DoActorDebris).setNext() };

	public static final Animator DoActorDeathMove = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoActorDeathMove(spr) != 0;
		}
	};

	public static final State s_NinjaDeathJump[] = { new State(NINJA_DIE + 0, NINJA_DIE_RATE, DoActorDeathMove),
			new State(NINJA_DIE + 1, NINJA_DIE_RATE, DoActorDeathMove),
			new State(NINJA_DIE + 2, NINJA_DIE_RATE, DoActorDeathMove).setNext() };

	public static final State s_NinjaDeathFall[] = { new State(NINJA_DIE + 3, NINJA_DIE_RATE, DoActorDeathMove),
			new State(NINJA_DIE + 4, NINJA_DIE_RATE, DoActorDeathMove).setNext(), };

	public enum NinjaStateGroup implements StateGroup {
		sg_NinjaRun(s_NinjaRun[0], s_NinjaRun[1], s_NinjaRun[2], s_NinjaRun[3], s_NinjaRun[4]),
		sg_NinjaStand(s_NinjaStand[0], s_NinjaStand[1], s_NinjaStand[2], s_NinjaStand[3], s_NinjaStand[4]),
		sg_NinjaRise(s_NinjaRise[0], s_NinjaRise[1], s_NinjaRise[2], s_NinjaRise[3], s_NinjaRise[4]),
		sg_NinjaCrawl(s_NinjaCrawl[0], s_NinjaCrawl[1], s_NinjaCrawl[2], s_NinjaCrawl[3], s_NinjaCrawl[4]),
		sg_NinjaKneelCrawl(s_NinjaKneelCrawl[0], s_NinjaKneelCrawl[1], s_NinjaKneelCrawl[2], s_NinjaKneelCrawl[3],
				s_NinjaKneelCrawl[4]),
		sg_NinjaDuck(s_NinjaDuck[0], s_NinjaDuck[1], s_NinjaDuck[2], s_NinjaDuck[3], s_NinjaDuck[4]),
		sg_NinjaSit(s_NinjaSit[0], s_NinjaSit[1], s_NinjaSit[2], s_NinjaSit[3], s_NinjaSit[4]),
		sg_NinjaCeiling(s_NinjaCeiling[0], s_NinjaCeiling[1], s_NinjaCeiling[2], s_NinjaCeiling[3], s_NinjaCeiling[4]),
		sg_NinjaJump(s_NinjaJump[0], s_NinjaJump[1], s_NinjaJump[2], s_NinjaJump[3], s_NinjaJump[4]),
		sg_NinjaFall(s_NinjaFall[0], s_NinjaFall[1], s_NinjaFall[2], s_NinjaFall[3], s_NinjaFall[4]),
		sg_NinjaSwim(s_NinjaSwim[0], s_NinjaSwim[1], s_NinjaSwim[2], s_NinjaSwim[3], s_NinjaSwim[4]),
		sg_NinjaDive(s_NinjaDive[0], s_NinjaDive[1], s_NinjaDive[2], s_NinjaDive[3], s_NinjaDive[4]),
		sg_NinjaClimb(s_NinjaClimb[0], s_NinjaClimb[1], s_NinjaClimb[2], s_NinjaClimb[3], s_NinjaClimb[4]),
		sg_NinjaFly(s_NinjaFly[0], s_NinjaFly[1], s_NinjaFly[2], s_NinjaFly[3], s_NinjaFly[4]),
		sg_NinjaPain(s_NinjaPain[0], s_NinjaPain[1], s_NinjaPain[2], s_NinjaPain[3], s_NinjaPain[4]),
		sg_NinjaStar(s_NinjaStar[0], s_NinjaStar[1], s_NinjaStar[2], s_NinjaStar[3], s_NinjaStar[4]),
		sg_NinjaMirv(s_NinjaMirv[0], s_NinjaMirv[1], s_NinjaMirv[2], s_NinjaMirv[3], s_NinjaMirv[4]),
		sg_NinjaNapalm(s_NinjaNapalm[0], s_NinjaNapalm[1], s_NinjaNapalm[2], s_NinjaNapalm[3], s_NinjaNapalm[4]),
		sg_NinjaRocket(s_NinjaRocket[0], s_NinjaRocket[1], s_NinjaRocket[2], s_NinjaRocket[3], s_NinjaRocket[4]),
		sg_NinjaGrenade(s_NinjaGrenade[0], s_NinjaGrenade[1], s_NinjaGrenade[2], s_NinjaGrenade[3], s_NinjaGrenade[4]),
		sg_NinjaFlashBomb(s_NinjaFlashBomb[0], s_NinjaFlashBomb[1], s_NinjaFlashBomb[2], s_NinjaFlashBomb[3],
				s_NinjaFlashBomb[4]),
		sg_NinjaUzi(s_NinjaUzi[0], s_NinjaUzi[1], s_NinjaUzi[2], s_NinjaUzi[3], s_NinjaUzi[4]),
		sg_NinjaHariKari(s_NinjaHariKari, s_NinjaHariKari, s_NinjaHariKari, s_NinjaHariKari, s_NinjaHariKari),
		sg_NinjaGrabThroat(s_NinjaGrabThroat, s_NinjaGrabThroat, s_NinjaGrabThroat, s_NinjaGrabThroat,
				s_NinjaGrabThroat),
		sg_NinjaDie(s_NinjaDie), sg_NinjaDieSliced(s_NinjaDieSliced), sg_NinjaDead(s_NinjaDead),
		sg_NinjaDeathJump(s_NinjaDeathJump), sg_NinjaDeathFall(s_NinjaDeathFall);

		private final State[][] group;
		private int index = -1;

		NinjaStateGroup(State[]... states) {
			group = states;
		}

		public State getState(int rotation, int offset) {
			return group[rotation][offset];
		}

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

	public static Actor_Action_Set PlayerNinjaActionSet = new Actor_Action_Set(PlayerStateGroup.sg_PlayerNinjaStand,
			PlayerStateGroup.sg_PlayerNinjaRun, PlayerStateGroup.sg_PlayerNinjaJump,
			PlayerStateGroup.sg_PlayerNinjaFall, PlayerStateGroup.sg_PlayerNinjaCrawl,
			PlayerStateGroup.sg_PlayerNinjaSwim, NinjaStateGroup.sg_NinjaFly, NinjaStateGroup.sg_NinjaRise,
			NinjaStateGroup.sg_NinjaSit, null, PlayerStateGroup.sg_PlayerNinjaClimb, NinjaStateGroup.sg_NinjaPain,
			NinjaStateGroup.sg_NinjaDie, NinjaStateGroup.sg_NinjaHariKari, NinjaStateGroup.sg_NinjaDead,
			NinjaStateGroup.sg_NinjaDeathJump, NinjaStateGroup.sg_NinjaDeathFall,
			new StateGroup[] { NinjaStateGroup.sg_NinjaStar, NinjaStateGroup.sg_NinjaUzi }, new short[] { 1000, 1024 },
			new StateGroup[] { NinjaStateGroup.sg_NinjaStar, NinjaStateGroup.sg_NinjaUzi }, new short[] { 800, 1024 },
			null, NinjaStateGroup.sg_NinjaDuck, PlayerStateGroup.sg_PlayerNinjaSwim);

	public static Actor_Action_Set NinjaSniperActionSet = new Actor_Action_Set(NinjaStateGroup.sg_NinjaDuck,
			NinjaStateGroup.sg_NinjaCrawl, NinjaStateGroup.sg_NinjaJump, NinjaStateGroup.sg_NinjaFall,
			NinjaStateGroup.sg_NinjaKneelCrawl, NinjaStateGroup.sg_NinjaSwim, NinjaStateGroup.sg_NinjaFly,
			NinjaStateGroup.sg_NinjaUzi, NinjaStateGroup.sg_NinjaDuck, null, NinjaStateGroup.sg_NinjaClimb,
			NinjaStateGroup.sg_NinjaPain, NinjaStateGroup.sg_NinjaDie, NinjaStateGroup.sg_NinjaHariKari,
			NinjaStateGroup.sg_NinjaDead, NinjaStateGroup.sg_NinjaDeathJump, NinjaStateGroup.sg_NinjaDeathFall,
			new StateGroup[] { NinjaStateGroup.sg_NinjaUzi }, new short[] { 1024 },
			new StateGroup[] { NinjaStateGroup.sg_NinjaUzi }, new short[] { 1024 }, null, NinjaStateGroup.sg_NinjaDuck,
			NinjaStateGroup.sg_NinjaDive);

	public static Actor_Action_Set NinjaActionSet = new Actor_Action_Set(NinjaStateGroup.sg_NinjaStand,
			NinjaStateGroup.sg_NinjaRun, NinjaStateGroup.sg_NinjaJump, NinjaStateGroup.sg_NinjaFall,
			NinjaStateGroup.sg_NinjaKneelCrawl, NinjaStateGroup.sg_NinjaSwim, NinjaStateGroup.sg_NinjaFly,
			NinjaStateGroup.sg_NinjaRise, NinjaStateGroup.sg_NinjaSit, null, NinjaStateGroup.sg_NinjaClimb,
			NinjaStateGroup.sg_NinjaPain, NinjaStateGroup.sg_NinjaDie, NinjaStateGroup.sg_NinjaHariKari,
			NinjaStateGroup.sg_NinjaDead, NinjaStateGroup.sg_NinjaDeathJump, NinjaStateGroup.sg_NinjaDeathFall,
			new StateGroup[] { NinjaStateGroup.sg_NinjaUzi, NinjaStateGroup.sg_NinjaStar }, new short[] { 1000, 1024 },
			new StateGroup[] { NinjaStateGroup.sg_NinjaUzi, NinjaStateGroup.sg_NinjaStar }, new short[] { 800, 1024 },
			null, NinjaStateGroup.sg_NinjaDuck, NinjaStateGroup.sg_NinjaDive);

	public static Actor_Action_Set NinjaRedActionSet = new Actor_Action_Set(NinjaStateGroup.sg_NinjaStand,
			NinjaStateGroup.sg_NinjaRun, NinjaStateGroup.sg_NinjaJump, NinjaStateGroup.sg_NinjaFall,
			NinjaStateGroup.sg_NinjaKneelCrawl, NinjaStateGroup.sg_NinjaSwim, NinjaStateGroup.sg_NinjaFly,
			NinjaStateGroup.sg_NinjaRise, NinjaStateGroup.sg_NinjaSit, null, NinjaStateGroup.sg_NinjaClimb,
			NinjaStateGroup.sg_NinjaPain, NinjaStateGroup.sg_NinjaDie, NinjaStateGroup.sg_NinjaHariKari,
			NinjaStateGroup.sg_NinjaDead, NinjaStateGroup.sg_NinjaDeathJump, NinjaStateGroup.sg_NinjaDeathFall,
			new StateGroup[] { NinjaStateGroup.sg_NinjaUzi, NinjaStateGroup.sg_NinjaUzi }, new short[] { 812, 1024 },
			new StateGroup[] { NinjaStateGroup.sg_NinjaUzi, NinjaStateGroup.sg_NinjaRocket }, new short[] { 812, 1024 },
			null, NinjaStateGroup.sg_NinjaDuck, NinjaStateGroup.sg_NinjaDive);

	public static Actor_Action_Set NinjaSeekerActionSet = new Actor_Action_Set(NinjaStateGroup.sg_NinjaStand,
			NinjaStateGroup.sg_NinjaRun, NinjaStateGroup.sg_NinjaJump, NinjaStateGroup.sg_NinjaFall,
			NinjaStateGroup.sg_NinjaKneelCrawl, NinjaStateGroup.sg_NinjaSwim, NinjaStateGroup.sg_NinjaFly,
			NinjaStateGroup.sg_NinjaRise, NinjaStateGroup.sg_NinjaSit, null, NinjaStateGroup.sg_NinjaClimb,
			NinjaStateGroup.sg_NinjaPain, NinjaStateGroup.sg_NinjaDie, NinjaStateGroup.sg_NinjaHariKari,
			NinjaStateGroup.sg_NinjaDead, NinjaStateGroup.sg_NinjaDeathJump, NinjaStateGroup.sg_NinjaDeathFall,
			new StateGroup[] { NinjaStateGroup.sg_NinjaUzi, NinjaStateGroup.sg_NinjaStar }, new short[] { 812, 1024 },
			new StateGroup[] { NinjaStateGroup.sg_NinjaUzi, NinjaStateGroup.sg_NinjaRocket }, new short[] { 812, 1024 },
			null, NinjaStateGroup.sg_NinjaDuck, NinjaStateGroup.sg_NinjaDive);

	public static Actor_Action_Set NinjaGrenadeActionSet = new Actor_Action_Set(NinjaStateGroup.sg_NinjaStand,
			NinjaStateGroup.sg_NinjaRun, NinjaStateGroup.sg_NinjaJump, NinjaStateGroup.sg_NinjaFall,
			NinjaStateGroup.sg_NinjaKneelCrawl, NinjaStateGroup.sg_NinjaSwim, NinjaStateGroup.sg_NinjaFly,
			NinjaStateGroup.sg_NinjaRise, NinjaStateGroup.sg_NinjaSit, null, NinjaStateGroup.sg_NinjaClimb,
			NinjaStateGroup.sg_NinjaPain, NinjaStateGroup.sg_NinjaDie, NinjaStateGroup.sg_NinjaHariKari,
			NinjaStateGroup.sg_NinjaDead, NinjaStateGroup.sg_NinjaDeathJump, NinjaStateGroup.sg_NinjaDeathFall,
			new StateGroup[] { NinjaStateGroup.sg_NinjaUzi, NinjaStateGroup.sg_NinjaUzi }, new short[] { 812, 1024 },
			new StateGroup[] { NinjaStateGroup.sg_NinjaUzi, NinjaStateGroup.sg_NinjaGrenade },
			new short[] { 812, 1024 }, null, NinjaStateGroup.sg_NinjaDuck, NinjaStateGroup.sg_NinjaDive);

	public static Actor_Action_Set NinjaGreenActionSet = new Actor_Action_Set(NinjaStateGroup.sg_NinjaStand,
			NinjaStateGroup.sg_NinjaRun, NinjaStateGroup.sg_NinjaJump, NinjaStateGroup.sg_NinjaFall,
			NinjaStateGroup.sg_NinjaKneelCrawl, NinjaStateGroup.sg_NinjaSwim, NinjaStateGroup.sg_NinjaFly,
			NinjaStateGroup.sg_NinjaRise, NinjaStateGroup.sg_NinjaSit, null, NinjaStateGroup.sg_NinjaClimb,
			NinjaStateGroup.sg_NinjaPain, NinjaStateGroup.sg_NinjaDie, NinjaStateGroup.sg_NinjaHariKari,
			NinjaStateGroup.sg_NinjaDead, NinjaStateGroup.sg_NinjaDeathJump, NinjaStateGroup.sg_NinjaDeathFall,
			new StateGroup[] { NinjaStateGroup.sg_NinjaUzi, NinjaStateGroup.sg_NinjaFlashBomb },
			new short[] { 912, 1024 },
			new StateGroup[] { NinjaStateGroup.sg_NinjaFlashBomb, NinjaStateGroup.sg_NinjaUzi,
					NinjaStateGroup.sg_NinjaMirv, NinjaStateGroup.sg_NinjaNapalm },
			new short[] { 150, 500, 712, 1024 }, null, NinjaStateGroup.sg_NinjaDuck, NinjaStateGroup.sg_NinjaDive);

	public static void InitNinjaStates() {
		for (int i = 0; i < s_NinjaKneelCrawl.length; i++)
			s_NinjaKneelCrawl[i][4].setNext(s_NinjaKneelCrawl[i][1]);

		for (NinjaStateGroup sg : NinjaStateGroup.values()) {
			for (int rot = 0; rot < sg.group.length; rot++) {
				State.InitState(sg.group[rot]);
			}
		}
	}

	public static int DoHariKariBlood(int SpriteNum) {
		return (0);
	}

	/*
	 * 
	 * !AIC - Every actor has a setup where they are initialized
	 * 
	 */

	public static int SetupNinja(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;
		short pic = sp.picnum;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, NINJA_RUN_R0, s_NinjaRun[0][0]);
			u.Health = HEALTH_NINJA;
		}

		u.StateEnd = s_NinjaDie[0];
		u.Rot = NinjaStateGroup.sg_NinjaRun;
		sp.xrepeat = 46;
		sp.yrepeat = 46;

		if (sp.pal == PALETTE_PLAYER5) {
			u.Attrib = InvisibleNinjaAttrib;
			EnemyDefaults(SpriteNum, NinjaGreenActionSet, NinjaPersonality);
			if (!TEST(sp.cstat, CSTAT_SPRITE_RESTORE))
				u.Health = HEALTH_RED_NINJA;
			sp.cstat |= (CSTAT_SPRITE_TRANSLUCENT);
			sp.shade = 127;
			sp.pal = u.spal = PALETTE_PLAYER5;
			sp.hitag = 9998;
			if (pic == NINJA_CRAWL_R0) {
				if (TEST(sp.cstat, CSTAT_SPRITE_YFLIP)) {
					u.Attrib = NinjaAttrib;
					u.ActorActionSet = NinjaActionSet;
					u.Personality = NinjaPersonality;
					ChangeState(SpriteNum, s_NinjaCeiling[0][0]);
				} else {
					u.Attrib = NinjaAttrib;
					u.ActorActionSet = NinjaSniperActionSet;
					u.Personality = NinjaSniperPersonality;
					ChangeState(SpriteNum, s_NinjaDuck[0][0]);
				}
			}
		} else if (sp.pal == PALETTE_PLAYER3) {
			u.Attrib = NinjaAttrib;
			EnemyDefaults(SpriteNum, NinjaRedActionSet, NinjaPersonality);
			if (!TEST(sp.cstat, CSTAT_SPRITE_RESTORE))
				u.Health = HEALTH_RED_NINJA;
			sp.pal = u.spal = PALETTE_PLAYER3;
			if (pic == NINJA_CRAWL_R0) {
				if (TEST(sp.cstat, CSTAT_SPRITE_YFLIP)) {
					u.Attrib = NinjaAttrib;
					u.ActorActionSet = NinjaActionSet;
					u.Personality = NinjaPersonality;
					ChangeState(SpriteNum, s_NinjaCeiling[0][0]);
				} else {
					u.Attrib = NinjaAttrib;
					u.ActorActionSet = NinjaSniperActionSet;
					u.Personality = NinjaSniperPersonality;
					ChangeState(SpriteNum, s_NinjaDuck[0][0]);
				}
			}
		} else if (sp.pal == PAL_XLAT_LT_TAN) {
			u.Attrib = NinjaAttrib;
			EnemyDefaults(SpriteNum, NinjaSeekerActionSet, NinjaPersonality);
			if (!TEST(sp.cstat, CSTAT_SPRITE_RESTORE))
				u.Health = HEALTH_RED_NINJA;
			sp.pal = u.spal = PAL_XLAT_LT_TAN;
			u.Attrib = NinjaAttrib;
		} else if (sp.pal == PAL_XLAT_LT_GREY) {
			u.Attrib = NinjaAttrib;
			EnemyDefaults(SpriteNum, NinjaGrenadeActionSet, NinjaPersonality);
			if (!TEST(sp.cstat, CSTAT_SPRITE_RESTORE))
				u.Health = HEALTH_RED_NINJA;
			sp.pal = u.spal = PAL_XLAT_LT_GREY;
			u.Attrib = NinjaAttrib;
		} else {
			u.Attrib = NinjaAttrib;
			sp.pal = u.spal = PALETTE_PLAYER0;
			EnemyDefaults(SpriteNum, NinjaActionSet, NinjaPersonality);
			if (pic == NINJA_CRAWL_R0) {
				u.Attrib = NinjaAttrib;
				u.ActorActionSet = NinjaSniperActionSet;
				u.Personality = NinjaSniperPersonality;
				ChangeState(SpriteNum, s_NinjaDuck[0][0]);
			}
		}

		ChangeState(SpriteNum, s_NinjaRun[0][0]);
		DoActorSetSpeed(SpriteNum, NORM_SPEED);

		u.Radius = 280;
		u.Flags |= (SPR_XFLIP_TOGGLE);

		return (0);
	}

	public static int DoNinjaHariKari(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		short cnt, i;

		UpdateSinglePlayKills(SpriteNum, null);
		change_sprite_stat(SpriteNum, STAT_DEAD_ACTOR);
		sprite[SpriteNum].cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		u.Flags |= (SPR_DEAD);
		u.Flags &= ~(SPR_FALLING | SPR_JUMPING);
		u.floor_dist = (short) Z(40);
		u.RotNum = 0;
		u.ActorActionFunc = null;

		sp.extra |= (SPRX_BREAKABLE);
		sp.cstat |= (CSTAT_SPRITE_BREAKABLE);

		PlaySound(DIGI_NINJAUZIATTACK, sp, v3df_follow);

		SpawnBlood(SpriteNum, SpriteNum, -1, -1, -1, -1);

		cnt = (short) (RANDOM_RANGE(4) + 1);
		for (i = 0; i <= cnt; i++)
			InitBloodSpray(SpriteNum, true, -2);

		return (0);
	}

	public static int DoNinjaGrabThroat(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		if ((u.WaitTics -= ACTORMOVETICS) <= 0) {
			UpdateSinglePlayKills(SpriteNum, null);
			u.Flags2 &= ~(SPR2_DYING);
			sp.cstat &= ~(CSTAT_SPRITE_YFLIP);
			change_sprite_stat(SpriteNum, STAT_DEAD_ACTOR);
			sprite[SpriteNum].cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
			u.Flags |= (SPR_DEAD);
			u.Flags &= ~(SPR_FALLING | SPR_JUMPING);
			u.floor_dist = (short) Z(40);
			u.RotNum = 0;
			u.ActorActionFunc = null;

			sp.extra |= (SPRX_BREAKABLE);
			sp.cstat |= (CSTAT_SPRITE_BREAKABLE);

			ChangeState(SpriteNum, u.StateEnd);
			sp.xvel = 0;
			PlaySound(DIGI_NINJASCREAM, sp, v3df_follow);
		}

		return (0);
	}

	/*
	 * 
	 * !AIC - Most actors have one of these and the all look similar
	 * 
	 */

	public static int DoNinjaMove(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags2, SPR2_DYING)) {
			NewStateGroup(SpriteNum, NinjaStateGroup.sg_NinjaGrabThroat);
			return (0);
		}

		// jumping and falling
		if (TEST(u.Flags, SPR_JUMPING | SPR_FALLING) && !TEST(u.Flags, SPR_CLIMBING)) {
			if (TEST(u.Flags, SPR_JUMPING))
				DoActorJump(SpriteNum);
			else if (TEST(u.Flags, SPR_FALLING))
				DoActorFall(SpriteNum);
		}

		// sliding
		if (TEST(u.Flags, SPR_SLIDING) && !TEST(u.Flags, SPR_CLIMBING))
			DoActorSlide(SpriteNum);

		// !AIC - do track or call current action function - such as DoActorMoveCloser()
		if (u.track >= 0)
			ActorFollowTrack(SpriteNum, ACTORMOVETICS);
		else {
			u.ActorActionFunc.invoke(SpriteNum);
		}

		// stay on floor unless doing certain things
		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING | SPR_CLIMBING)) {
			KeepActorOnFloor(SpriteNum);
		}

		// take damage from environment
		DoActorSectorDamage(SpriteNum);

		return (0);
	}

	public static final Animator NinjaJumpActionFunc = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return NinjaJumpActionFunc(spr) != 0;
		}
	};

	public static int NinjaJumpActionFunc(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();
		int nx, ny;

		// Move while jumping
		nx = sp.xvel * sintable[NORM_ANGLE(sp.ang + 512)] >> 14;
		ny = sp.xvel * sintable[sp.ang] >> 14;

		// if cannot move the sprite
		if (!move_actor(SpriteNum, nx, ny, 0)) {
			return (0);
		}

		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			InitActorDecide(SpriteNum);
		}

		return (0);
	}

	/*
	 * 
	 * !AIC - Short version of DoNinjaMove without the movement code. For times when
	 * the actor is doing something but not moving.
	 * 
	 */

	public static int NullNinja(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (u.WaitTics > 0)
			u.WaitTics -= ACTORMOVETICS;

		if (TEST(u.Flags, SPR_SLIDING) && !TEST(u.Flags, SPR_CLIMBING) && !TEST(u.Flags, SPR_JUMPING | SPR_FALLING))
			DoActorSlide(SpriteNum);

		if (!TEST(u.Flags, SPR_CLIMBING) && !TEST(u.Flags, SPR_JUMPING | SPR_FALLING))
			KeepActorOnFloor(SpriteNum);

		DoActorSectorDamage(SpriteNum);

		return (0);
	}

	public static int DoNinjaSpecial(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if (u.spal == PALETTE_PLAYER5) {
			sp.cstat &= ~(CSTAT_SPRITE_TRANSLUCENT);
			sp.hitag = 0;
			sp.shade = -10;
		}

		return (0);
	}

	public static int CheckFire(int SpriteNum) {
		if (!CanSeePlayer(SpriteNum))
			InitActorDuck.invoke(SpriteNum);
		return (0);
	}

	//
	// !AIC - Stuff from here down is really Player related. Should be moved but it
	// was
	// too convienent to put it here.
	//

	public static void InitAllPlayerSprites(boolean NewGame) {
		for (int i = connecthead; i != -1; i = connectpoint2[i]) {
			InitPlayerSprite(i, NewGame);
		}
	}

	public static void PlayerLevelReset(PlayerStr pp) {
		SPRITE sp = sprite[pp.PlayerSprite];
		USER u = pUser[pp.PlayerSprite];

		if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COMMBAT) {
			PlayerDeathReset(pp);
			return;
		}

		if (TEST(pp.Flags, PF_DIVING))
			DoPlayerStopDiveNoWarp(pp);

		COVER_SetReverb(0); // Turn off any echoing that may have been going before
		pp.Reverb = 0;
		pp.SecretsFound = 0;
		pp.WpnFirstType = WPN_SWORD;
		pp.Kills = 0;
		pp.Killer = -1;
		pp.NightVision = false;
		pp.StartColor = 0;
		pp.FadeAmt = 0;
		pp.DeathType = 0;
		pp.lookang = 0;
		PlayerUpdatePanelInfo(pp);
		sp.cstat &= ~(CSTAT_SPRITE_YCENTER);
		sp.cstat &= ~(CSTAT_SPRITE_TRANSLUCENT);
		pp.Flags &= ~(PF_WEAPON_DOWN | PF_WEAPON_RETRACT);
		pp.Flags &= ~(PF_DEAD);
		pp.last_used_weapon = 0;

		pp.sop_control = -1;
		pp.sop_riding = -1;
		pp.sop_remote = -1;
		pp.sop = -1;
		DoPlayerResetMovement(pp);
		DamageData[u.WeaponNum].init.invoke(pp);
	}

	public static void PlayerDeathReset(PlayerStr pp) {
		SPRITE sp = sprite[pp.PlayerSprite];
		USER u = pUser[pp.PlayerSprite];

		if (TEST(pp.Flags, PF_DIVING))
			DoPlayerStopDiveNoWarp(pp);

		COVER_SetReverb(0); // Turn off any echoing that may have been going before
		pp.Reverb = 0;
		// second weapon - whatever it is
		u.WeaponNum = WPN_SWORD;
		pp.WpnFirstType = (byte) u.WeaponNum;
		pp.WpnRocketType = 0;
		pp.WpnRocketHeat = 0; // 5 to 0 range
		pp.WpnRocketNuke = 0; // 1, you have it, or you don't
		pp.WpnFlameType = 0; // Guardian weapons fire
		pp.WpnUziType = 2;
		pp.WpnShotgunType = 0; // Shotgun has normal or fully automatic fire
		pp.WpnShotgunAuto = 0; // 50-0 automatic shotgun rounds
		pp.WpnShotgunLastShell = 0; // Number of last shell fired
		pp.Bloody = false;
		pp.TestNukeInit = false;
		pp.InitingNuke = false;
		pp.nukevochandle = null;
		pp.NukeInitialized = false;
		pp.BunnyMode = false;

		Arrays.fill(pp.WpnAmmo, (short) 0);
		Arrays.fill(pp.InventoryTics, (short) 0);
		Arrays.fill(pp.InventoryPercent, (short) 0);
		Arrays.fill(pp.InventoryAmount, (short) 0);
		Arrays.fill(pp.InventoryActive, false);
		pp.WpnAmmo[WPN_STAR] = 30;
		pp.WpnAmmo[WPN_SWORD] = pp.WpnAmmo[WPN_FIST] = 30;
		pp.WpnFlags = 0;
		pp.WpnGotOnceFlags = 0;
		pp.WpnFlags |= (BIT(WPN_SWORD));
		pp.WpnFlags |= (BIT(WPN_FIST) | BIT(u.WeaponNum));
		pp.WpnFlags |= (BIT(WPN_STAR) | BIT(u.WeaponNum));
		pp.Flags &= ~(PF_PICKED_UP_AN_UZI);
		pp.Flags &= ~(PF_TWO_UZI);

		u.Health = 100;
		pp.MaxHealth = 100;

		puser[pp.pnum].Health = u.Health;
		pp.Armor = 0;
		PlayerUpdateArmor(pp, 0);
		pp.Killer = -1;
		pp.NightVision = false;
		pp.StartColor = 0;
		pp.FadeAmt = 0;
		pp.DeathType = 0;
		pp.lookang = 0;
		PlayerUpdatePanelInfo(pp);
		sp.cstat &= ~(CSTAT_SPRITE_TRANSLUCENT);
		pp.Flags &= ~(PF_WEAPON_DOWN | PF_WEAPON_RETRACT);
		pp.Flags &= ~(PF_DEAD);
		pp.last_used_weapon = 0;

		pp.sop_control = -1;
		pp.sop_riding = -1;
		pp.sop_remote = -1;
		pp.sop = -1;
		DoPlayerResetMovement(pp);
		DamageData[u.WeaponNum].init.invoke(pp);
	}

	public static void PlayerPanelSetup() {
		// For every player setup the panel weapon stuff
		for (short pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
			PlayerStr pp = Player[pnum];

			USER u = pUser[pp.PlayerSprite];
			PlayerUpdateWeapon(pp, u.WeaponNum);
		}
	}

	public static void PlayerGameReset(PlayerStr pp) {
		SPRITE sp = sprite[pp.PlayerSprite];
		USER u = pUser[pp.PlayerSprite];

		COVER_SetReverb(0); // Turn off any echoing that may have been going before
		pp.Reverb = 0;
		u.WeaponNum = WPN_SWORD;
		pp.WpnFirstType = (byte) u.WeaponNum;
		pp.WpnRocketType = 0;
		pp.WpnRocketHeat = 0; // 5 to 0 range
		pp.WpnRocketNuke = 0; // 1, you have it, or you don't
		pp.WpnFlameType = 0; // Guardian weapons fire
		pp.WpnUziType = 2;
		pp.WpnShotgunType = 0; // Shotgun has normal or fully automatic fire
		pp.WpnShotgunAuto = 0; // 50-0 automatic shotgun rounds
		pp.WpnShotgunLastShell = 0; // Number of last shell fired
		pp.Bloody = false;
		pp.TestNukeInit = false;
		pp.InitingNuke = false;
		pp.nukevochandle = null;
		pp.NukeInitialized = false;
		pp.BunnyMode = false;
		pp.SecretsFound = 0;

		pp.WpnAmmo[WPN_STAR] = 30;
		pp.WpnAmmo[WPN_SWORD] = pp.WpnAmmo[WPN_FIST] = 30;
		pp.WpnFlags = 0;
		pp.WpnGotOnceFlags = 0;
		pp.WpnFlags |= (BIT(WPN_SWORD));
		pp.WpnFlags |= (BIT(WPN_FIST) | BIT(u.WeaponNum));
		pp.WpnFlags |= (BIT(WPN_STAR) | BIT(u.WeaponNum));
		pp.Flags &= ~(PF_PICKED_UP_AN_UZI);
		pp.Flags &= ~(PF_TWO_UZI);
		pp.MaxHealth = 100;
		PlayerUpdateHealth(pp, 500);
		pp.Armor = 0;
		PlayerUpdateArmor(pp, 0);
		pp.Killer = -1;

		if (pp == Player[screenpeek]) {
			engine.setbrightness(gs.brightness, palette, GLInvalidateFlag.All);
			System.arraycopy(palette, 0, pp.temp_pal, 0, 768);
		}
		pp.NightVision = false;
		pp.StartColor = 0;
		pp.FadeAmt = 0;
		pp.DeathType = 0;

		PlayerUpdatePanelInfo(pp);
		sp.cstat &= ~(CSTAT_SPRITE_TRANSLUCENT);

		pp.sop_control = -1;
		pp.sop_riding = -1;
		pp.sop_remote = -1;
		pp.sop = -1;
		DoPlayerResetMovement(pp);
		DamageData[u.WeaponNum].init.invoke(pp);
	}

	public static void PlayerSpriteLoadLevel(int SpriteNum) {
		USER u = pUser[SpriteNum];

		ChangeState(SpriteNum, s_NinjaRun[0][0]);
		u.Rot = NinjaStateGroup.sg_NinjaRun;
		u.ActorActionSet = PlayerNinjaActionSet;
	}

	public static void InitPlayerSprite(int pnum, boolean NewGame) {
		short sp_num;
		SPRITE sp;
		USER u;
		PlayerStr pp = Player[pnum];

		COVER_SetReverb(0); // Turn off any echoing that may have been going before
		pp.Reverb = 0;
		sp_num = pp.PlayerSprite = (short) SpawnSprite(STAT_PLAYER0 + pnum, NINJA_RUN_R0, null, pp.cursectnum, pp.posx,
				pp.posy, pp.posz, pp.getAnglei(), 0);

		sp = sprite[sp_num];
		pp.pnum = (short) pnum;

		sp.cstat |= (CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		sp.extra |= (SPRX_PLAYER_OR_ENEMY);
		sp.cstat &= ~(CSTAT_SPRITE_TRANSLUCENT);

		u = pUser[sp_num];

		// Grouping items that need to be reset after a LoadLevel
		ChangeState(sp_num, s_NinjaRun[0][0]);
		u.Rot = NinjaStateGroup.sg_NinjaRun;
		u.ActorActionSet = PlayerNinjaActionSet;

		u.RotNum = 5;

		u.Radius = 400;
		u.PlayerP = pnum;
		u.Flags |= (SPR_XFLIP_TOGGLE);

		sp.picnum = u.State.Pic;
		sp.shade = -60; // was 15
		sp.clipdist = (char) (256L >> 2);

		sp.xrepeat = PLAYER_NINJA_XREPEAT;
		sp.yrepeat = PLAYER_NINJA_YREPEAT;
		sp.pal = (short) (PALETTE_PLAYER0 + pp.pnum);
		u.spal = (byte) sp.pal;

		NewStateGroup(sp_num, u.ActorActionSet.Run);

		pp.PlayerUnderSprite = -1;

		DoPlayerZrange(pp);

		if (NewGame) {
			PlayerGameReset(pp);
		} else {
			// save stuff from last level
			u.WeaponNum = puser[pnum].WeaponNum;
			u.LastWeaponNum = puser[pnum].LastWeaponNum;
			u.Health = puser[pnum].Health;
			PlayerLevelReset(pp);
		}

		Arrays.fill(pp.InventoryTics, (short) 0);
		if (pnum == screenpeek) {
			engine.setbrightness(gs.brightness, palette, GLInvalidateFlag.All);
			System.arraycopy(palette, 0, pp.temp_pal, 0, 768);
		}

		pp.NightVision = false;
		pp.StartColor = 0;
		pp.FadeAmt = 0;
		pp.DeathType = 0;
		PlayerUpdatePanelInfo(pp);
	}

	public static void SpawnPlayerUnderSprite(int pnum) {
		PlayerStr pp = Player[pnum];
		USER pu = pUser[pp.PlayerSprite], u;
		SPRITE psp = sprite[pp.PlayerSprite];

		int sp_num = pp.PlayerUnderSprite = (short) SpawnSprite(STAT_PLAYER_UNDER0 + pnum, NINJA_RUN_R0, null,
				pp.cursectnum, pp.posx, pp.posy, pp.posz, pp.getAnglei(), 0);

		SPRITE sp = sprite[sp_num];
		u = pUser[sp_num];

		sp.cstat |= (CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		sp.extra |= (SPRX_PLAYER_OR_ENEMY);

		u.Rot = NinjaStateGroup.sg_NinjaRun;
		u.RotNum = pu.RotNum;
		NewStateGroup(sp_num, pu.Rot);

		u.Radius = pu.Radius;
		u.PlayerP = pnum;
		u.Health = pp.MaxHealth;
		u.Flags |= (SPR_XFLIP_TOGGLE);

		u.ActorActionSet = pu.ActorActionSet;

		sp.picnum = psp.picnum;
		sp.clipdist = psp.clipdist;
		sp.xrepeat = psp.xrepeat;
		sp.yrepeat = psp.yrepeat;
	}
	
	public static void NinjaSaveable()
	{
		SaveData(nullNinja);
		SaveData(InitEnemyStar);
		SaveData(InitEnemyMirv);
		SaveData(InitEnemyNapalm);
		SaveData(InitEnemyRocket);
		SaveData(InitSpriteGrenade);
		SaveData(InitFlashBomb);
		SaveData(InitEnemyUzi);
		SaveData(DoActorDeathMove);
		
		SaveData(DoNinjaHariKari);
		SaveData(DoNinjaGrabThroat);
		SaveData(DoNinjaMove);
		SaveData(NinjaJumpActionFunc);
		SaveData(DoNinjaPain);
		SaveData(DoNinjaSpecial);
		SaveData(CheckFire);
		SaveData(DoNinjaCeiling);

		SaveData(NinjaPersonality);
		SaveData(NinjaSniperPersonality);

		SaveData(NinjaAttrib);
		SaveData(InvisibleNinjaAttrib);

		SaveData(s_NinjaRun);
		SaveGroup(NinjaStateGroup.sg_NinjaRun);
		SaveData(s_NinjaStand);
		SaveGroup(NinjaStateGroup.sg_NinjaStand);
		SaveData(s_NinjaRise);
		SaveGroup(NinjaStateGroup.sg_NinjaRise);
		SaveData(s_NinjaCrawl);
		SaveGroup(NinjaStateGroup.sg_NinjaCrawl);
		SaveData(s_NinjaKneelCrawl);
		SaveGroup(NinjaStateGroup.sg_NinjaKneelCrawl);
		SaveData(s_NinjaDuck);
		SaveGroup(NinjaStateGroup.sg_NinjaDuck);
		SaveData(s_NinjaSit);
		SaveGroup(NinjaStateGroup.sg_NinjaSit);
		SaveData(s_NinjaCeiling);
		SaveGroup(NinjaStateGroup.sg_NinjaCeiling);
		SaveData(s_NinjaJump);
		SaveGroup(NinjaStateGroup.sg_NinjaJump);
		SaveData(s_NinjaFall);
		SaveGroup(NinjaStateGroup.sg_NinjaFall);
		SaveData(s_NinjaSwim);
		SaveGroup(NinjaStateGroup.sg_NinjaSwim);
		SaveData(s_NinjaDive);
		SaveGroup(NinjaStateGroup.sg_NinjaDive);
		SaveData(s_NinjaClimb);
		SaveGroup(NinjaStateGroup.sg_NinjaClimb);
		SaveData(s_NinjaFly);
		SaveGroup(NinjaStateGroup.sg_NinjaFly);
		SaveData(s_NinjaPain);
		SaveGroup(NinjaStateGroup.sg_NinjaPain);
		SaveData(s_NinjaStar);
		SaveGroup(NinjaStateGroup.sg_NinjaStar);
		SaveData(s_NinjaMirv);
		SaveGroup(NinjaStateGroup.sg_NinjaMirv);
		SaveData(s_NinjaNapalm);
		SaveGroup(NinjaStateGroup.sg_NinjaNapalm);
		SaveData(s_NinjaRocket);
		SaveGroup(NinjaStateGroup.sg_NinjaRocket);
		SaveData(s_NinjaGrenade);
		SaveGroup(NinjaStateGroup.sg_NinjaGrenade);
		SaveData(s_NinjaFlashBomb);
		SaveGroup(NinjaStateGroup.sg_NinjaFlashBomb);
		SaveData(s_NinjaUzi);
		SaveGroup(NinjaStateGroup.sg_NinjaUzi);
		SaveData(s_NinjaHariKari);
		SaveGroup(NinjaStateGroup.sg_NinjaHariKari);
		SaveData(s_NinjaGrabThroat);
		SaveGroup(NinjaStateGroup.sg_NinjaGrabThroat);
		SaveData(s_NinjaDie);
		SaveData(s_NinjaDieSliced);
		SaveData(s_NinjaDead);
		SaveData(s_NinjaDeathJump);
		SaveData(s_NinjaDeathFall);
		SaveGroup(NinjaStateGroup.sg_NinjaDie);
		SaveGroup(NinjaStateGroup.sg_NinjaDieSliced);
		SaveGroup(NinjaStateGroup.sg_NinjaDead);
		SaveGroup(NinjaStateGroup.sg_NinjaDeathJump);
		SaveGroup(NinjaStateGroup.sg_NinjaDeathFall);

		SaveData(NinjaSniperActionSet);
		SaveData(NinjaActionSet);
		SaveData(NinjaRedActionSet);
		SaveData(NinjaSeekerActionSet);
		SaveData(NinjaGrenadeActionSet);
		SaveData(NinjaGreenActionSet);
		SaveData(PlayerNinjaActionSet);
	}
}
