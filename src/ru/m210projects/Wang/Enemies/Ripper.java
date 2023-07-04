package ru.m210projects.Wang.Enemies;

import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Wang.Actor.DoActorDeathMove;
import static ru.m210projects.Wang.Actor.DoActorDebris;
import static ru.m210projects.Wang.Actor.DoActorSectorDamage;
import static ru.m210projects.Wang.Actor.DoActorSlide;
import static ru.m210projects.Wang.Actor.DoFall;
import static ru.m210projects.Wang.Actor.DoJump;
import static ru.m210projects.Wang.Actor.DoScaleSprite;
import static ru.m210projects.Wang.Actor.KeepActorOnFloor;
import static ru.m210projects.Wang.Ai.DoActorMoveJump;
import static ru.m210projects.Wang.Ai.DoActorSetSpeed;
import static ru.m210projects.Wang.Ai.FAST_SPEED;
import static ru.m210projects.Wang.Ai.InitActorAlertNoise;
import static ru.m210projects.Wang.Ai.InitActorAmbientNoise;
import static ru.m210projects.Wang.Ai.InitActorAttack;
import static ru.m210projects.Wang.Ai.InitActorAttackNoise;
import static ru.m210projects.Wang.Ai.InitActorDecide;
import static ru.m210projects.Wang.Ai.InitActorFindPlayer;
import static ru.m210projects.Wang.Ai.InitActorMoveCloser;
import static ru.m210projects.Wang.Ai.InitActorReposition;
import static ru.m210projects.Wang.Ai.InitActorWanderAround;
import static ru.m210projects.Wang.Digi.DIGI_RIPPERALERT;
import static ru.m210projects.Wang.Digi.DIGI_RIPPERAMBIENT;
import static ru.m210projects.Wang.Digi.DIGI_RIPPERATTACK;
import static ru.m210projects.Wang.Digi.DIGI_RIPPERHEARTOUT;
import static ru.m210projects.Wang.Digi.DIGI_RIPPERPAIN;
import static ru.m210projects.Wang.Digi.DIGI_RIPPERSCREAM;
import static ru.m210projects.Wang.Enemies.Coolie.EnemyDefaults;
import static ru.m210projects.Wang.Enemies.Ripper2.InitRipperSlash;
import static ru.m210projects.Wang.Game.Distance;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_ACTOR;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_MISSILE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_RESTORE;
import static ru.m210projects.Wang.Gameutils.HEALTH_MOMMA_RIPPER;
import static ru.m210projects.Wang.Gameutils.HEALTH_RIPPER;
import static ru.m210projects.Wang.Gameutils.HIT_MASK;
import static ru.m210projects.Wang.Gameutils.HIT_WALL;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.NORM_WALL;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.SF_QUICK_CALL;
import static ru.m210projects.Wang.Gameutils.SF_TIC_ADJUST;
import static ru.m210projects.Wang.Gameutils.SPRITEp_SIZE_Z;
import static ru.m210projects.Wang.Gameutils.SPRITEp_TOS;
import static ru.m210projects.Wang.Gameutils.SPRX_PLAYER_OR_ENEMY;
import static ru.m210projects.Wang.Gameutils.SPR_ACTIVE;
import static ru.m210projects.Wang.Gameutils.SPR_FALLING;
import static ru.m210projects.Wang.Gameutils.SPR_JUMPING;
import static ru.m210projects.Wang.Gameutils.SPR_SLIDING;
import static ru.m210projects.Wang.Gameutils.SPR_XFLIP_TOGGLE;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Names.RIPPER_DEAD;
import static ru.m210projects.Wang.Names.RIPPER_DIE;
import static ru.m210projects.Wang.Names.RIPPER_FALL_R0;
import static ru.m210projects.Wang.Names.RIPPER_FALL_R1;
import static ru.m210projects.Wang.Names.RIPPER_FALL_R2;
import static ru.m210projects.Wang.Names.RIPPER_FALL_R3;
import static ru.m210projects.Wang.Names.RIPPER_FALL_R4;
import static ru.m210projects.Wang.Names.RIPPER_HANG_R0;
import static ru.m210projects.Wang.Names.RIPPER_HANG_R1;
import static ru.m210projects.Wang.Names.RIPPER_HANG_R2;
import static ru.m210projects.Wang.Names.RIPPER_HANG_R3;
import static ru.m210projects.Wang.Names.RIPPER_HANG_R4;
import static ru.m210projects.Wang.Names.RIPPER_HEART_R0;
import static ru.m210projects.Wang.Names.RIPPER_HEART_R1;
import static ru.m210projects.Wang.Names.RIPPER_HEART_R2;
import static ru.m210projects.Wang.Names.RIPPER_HEART_R3;
import static ru.m210projects.Wang.Names.RIPPER_HEART_R4;
import static ru.m210projects.Wang.Names.RIPPER_JUMP_R0;
import static ru.m210projects.Wang.Names.RIPPER_JUMP_R1;
import static ru.m210projects.Wang.Names.RIPPER_JUMP_R2;
import static ru.m210projects.Wang.Names.RIPPER_JUMP_R3;
import static ru.m210projects.Wang.Names.RIPPER_JUMP_R4;
import static ru.m210projects.Wang.Names.RIPPER_RUN_R0;
import static ru.m210projects.Wang.Names.RIPPER_RUN_R1;
import static ru.m210projects.Wang.Names.RIPPER_RUN_R2;
import static ru.m210projects.Wang.Names.RIPPER_RUN_R3;
import static ru.m210projects.Wang.Names.RIPPER_RUN_R4;
import static ru.m210projects.Wang.Names.RIPPER_STAND_R0;
import static ru.m210projects.Wang.Names.RIPPER_STAND_R1;
import static ru.m210projects.Wang.Names.RIPPER_STAND_R2;
import static ru.m210projects.Wang.Names.RIPPER_STAND_R3;
import static ru.m210projects.Wang.Names.RIPPER_STAND_R4;
import static ru.m210projects.Wang.Names.RIPPER_SWIPE_R0;
import static ru.m210projects.Wang.Names.RIPPER_SWIPE_R1;
import static ru.m210projects.Wang.Names.RIPPER_SWIPE_R2;
import static ru.m210projects.Wang.Names.RIPPER_SWIPE_R3;
import static ru.m210projects.Wang.Names.RIPPER_SWIPE_R4;
import static ru.m210projects.Wang.Names.STAT_DEFAULT;
import static ru.m210projects.Wang.Palette.PALETTE_BROWN_RIPPER;
import static ru.m210projects.Wang.Player.QueueFloorBlood;
import static ru.m210projects.Wang.Rooms.COVERinsertsprite;
import static ru.m210projects.Wang.Rooms.FAFhitscan;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.DoActorZrange;
import static ru.m210projects.Wang.Sprites.NewStateGroup;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Sprites.move_actor;
import static ru.m210projects.Wang.Sprites.move_sprite;
import static ru.m210projects.Wang.Track.ActorFollowTrack;
import static ru.m210projects.Wang.Type.MyTypes.DIV2;
import static ru.m210projects.Wang.Type.MyTypes.DIV256;
import static ru.m210projects.Wang.Type.MyTypes.DTEST;
import static ru.m210projects.Wang.Type.MyTypes.FALSE;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.MyTypes.TRUE;
import static ru.m210projects.Wang.Type.Saveable.*;
import static ru.m210projects.Wang.Weapon.InitCoolgFire;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Type.ATTRIBUTE;
import ru.m210projects.Wang.Type.Actor_Action_Set;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;

public class Ripper {

	private static final Decision RipperBattle[] = { new Decision(748, InitActorMoveCloser),
			new Decision(750, InitActorAlertNoise), new Decision(755, InitActorAttackNoise),
			new Decision(1024, InitActorAttack), };

	private static final Decision RipperOffense[] = { new Decision(700, InitActorMoveCloser),
			new Decision(710, InitActorAlertNoise), new Decision(1024, InitActorAttack), };

	private static final Decision RipperBroadcast[] = { new Decision(3, InitActorAlertNoise),
			new Decision(6, InitActorAmbientNoise), new Decision(1024, InitActorDecide), };

	private static final Animator InitRipperHang = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitRipperHang(SpriteNum) != 0;
		}
	};

	private static final Decision RipperSurprised[] = { new Decision(30, InitRipperHang),
			new Decision(701, InitActorMoveCloser), new Decision(1024, InitActorDecide), };

	private static final Decision RipperEvasive[] = { new Decision(6, InitRipperHang), new Decision(1024, null), };

	private static final Decision RipperLostTarget[] = { new Decision(980, InitActorFindPlayer),
			new Decision(1024, InitActorWanderAround), };

	private static final Decision RipperCloseRange[] = { new Decision(900, InitActorAttack),
			new Decision(1024, InitActorReposition), };

	private static final Personality RipperPersonality = new Personality(RipperBattle, RipperOffense, RipperBroadcast,
			RipperSurprised, RipperEvasive, RipperLostTarget, RipperCloseRange, RipperCloseRange);

	private static final ATTRIBUTE RipperAttrib = new ATTRIBUTE(new short[] { 200, 220, 240, 280 }, // Speeds
			new short[] { 5, 0, -2, -4 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { DIGI_RIPPERAMBIENT, DIGI_RIPPERALERT, DIGI_RIPPERATTACK, DIGI_RIPPERPAIN, DIGI_RIPPERSCREAM,
					DIGI_RIPPERHEARTOUT, 0, 0, 0, 0 });

	//////////////////////
	//
	// RIPPER RUN
	//
	//////////////////////

	public static final int RIPPER_RUN_RATE = 16;

	private static final Animator DoRipperMove = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoRipperMove(SpriteNum) != 0;
		}
	};

	private static final State s_RipperRun[][] = {
			{ new State(RIPPER_RUN_R0 + 0, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove), // s_RipperRun[0][1]},
					new State(RIPPER_RUN_R0 + 1, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove), // s_RipperRun[0][2]},
					new State(RIPPER_RUN_R0 + 2, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove), // s_RipperRun[0][3]},
					new State(RIPPER_RUN_R0 + 3, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove),// s_RipperRun[0][0]},
			}, { new State(RIPPER_RUN_R1 + 0, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove), // s_RipperRun[1][1]},
					new State(RIPPER_RUN_R1 + 1, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove), // s_RipperRun[1][2]},
					new State(RIPPER_RUN_R1 + 2, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove), // s_RipperRun[1][3]},
					new State(RIPPER_RUN_R1 + 3, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove),// s_RipperRun[1][0]},
			}, { new State(RIPPER_RUN_R2 + 0, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove), // s_RipperRun[2][1]},
					new State(RIPPER_RUN_R2 + 1, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove), // s_RipperRun[2][2]},
					new State(RIPPER_RUN_R2 + 2, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove), // s_RipperRun[2][3]},
					new State(RIPPER_RUN_R2 + 3, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove),// s_RipperRun[2][0]},
			}, { new State(RIPPER_RUN_R3 + 0, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove), // s_RipperRun[3][1]},
					new State(RIPPER_RUN_R3 + 1, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove), // s_RipperRun[3][2]},
					new State(RIPPER_RUN_R3 + 2, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove), // s_RipperRun[3][3]},
					new State(RIPPER_RUN_R3 + 3, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove),// s_RipperRun[3][0]},
			}, { new State(RIPPER_RUN_R4 + 0, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove), // s_RipperRun[4][1]},
					new State(RIPPER_RUN_R4 + 1, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove), // s_RipperRun[4][2]},
					new State(RIPPER_RUN_R4 + 2, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove), // s_RipperRun[4][3]},
					new State(RIPPER_RUN_R4 + 3, RIPPER_RUN_RATE | SF_TIC_ADJUST, DoRipperMove),// s_RipperRun[4][0]},
			} };

	//////////////////////
	//
	// RIPPER STAND
	//
	//////////////////////

	public static final int RIPPER_STAND_RATE = 12;

	private static final State s_RipperStand[][] = {
			{ new State(RIPPER_STAND_R0 + 0, RIPPER_STAND_RATE, DoRipperMove).setNext(),// s_RipperStand[0][0]},
			}, { new State(RIPPER_STAND_R1 + 0, RIPPER_STAND_RATE, DoRipperMove).setNext(),// s_RipperStand[1][0]},
			}, { new State(RIPPER_STAND_R2 + 0, RIPPER_STAND_RATE, DoRipperMove).setNext(),// s_RipperStand[2][0]},
			}, { new State(RIPPER_STAND_R3 + 0, RIPPER_STAND_RATE, DoRipperMove).setNext(),// s_RipperStand[3][0]},
			}, { new State(RIPPER_STAND_R4 + 0, RIPPER_STAND_RATE, DoRipperMove).setNext(),// s_RipperStand[4][0]},
			}, };

	//////////////////////
	//
	// RIPPER SWIPE
	//
	//////////////////////

	public static final int RIPPER_SWIPE_RATE = 8;

	private static final Animator NullRipper = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return NullRipper(SpriteNum) != 0;
		}
	};

	private static final State s_RipperSwipe[][] = { { new State(RIPPER_SWIPE_R0 + 0, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[0][1]},
			new State(RIPPER_SWIPE_R0 + 1, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[0][2]},
			new State(RIPPER_SWIPE_R0 + 1, 0 | SF_QUICK_CALL, InitRipperSlash), // s_RipperSwipe[0][3]},
			new State(RIPPER_SWIPE_R0 + 2, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[0][4]},
			new State(RIPPER_SWIPE_R0 + 3, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[0][5]},
			new State(RIPPER_SWIPE_R0 + 3, 0 | SF_QUICK_CALL, InitRipperSlash), // s_RipperSwipe[0][6]},
			new State(RIPPER_SWIPE_R0 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_RipperSwipe[0][7]},
			new State(RIPPER_SWIPE_R0 + 3, RIPPER_SWIPE_RATE, DoRipperMove).setNext(),// s_RipperSwipe[0][7]},
			}, { new State(RIPPER_SWIPE_R1 + 0, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[1][1]},
					new State(RIPPER_SWIPE_R1 + 1, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[1][2]},
					new State(RIPPER_SWIPE_R1 + 1, 0 | SF_QUICK_CALL, InitRipperSlash), // s_RipperSwipe[1][3]},
					new State(RIPPER_SWIPE_R1 + 2, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[1][4]},
					new State(RIPPER_SWIPE_R1 + 3, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[1][5]},
					new State(RIPPER_SWIPE_R1 + 3, 0 | SF_QUICK_CALL, InitRipperSlash), // s_RipperSwipe[1][6]},
					new State(RIPPER_SWIPE_R1 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_RipperSwipe[1][7]},
					new State(RIPPER_SWIPE_R1 + 3, RIPPER_SWIPE_RATE, DoRipperMove).setNext(),// s_RipperSwipe[1][7]},
			}, { new State(RIPPER_SWIPE_R2 + 0, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[2][1]},
					new State(RIPPER_SWIPE_R2 + 1, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[2][2]},
					new State(RIPPER_SWIPE_R2 + 1, 0 | SF_QUICK_CALL, InitRipperSlash), // s_RipperSwipe[2][3]},
					new State(RIPPER_SWIPE_R2 + 2, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[2][4]},
					new State(RIPPER_SWIPE_R2 + 3, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[2][5]},
					new State(RIPPER_SWIPE_R2 + 3, 0 | SF_QUICK_CALL, InitRipperSlash), // s_RipperSwipe[2][6]},
					new State(RIPPER_SWIPE_R2 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_RipperSwipe[2][7]},
					new State(RIPPER_SWIPE_R2 + 3, RIPPER_SWIPE_RATE, DoRipperMove).setNext(),// s_RipperSwipe[2][7]},
			}, { new State(RIPPER_SWIPE_R3 + 0, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[3][1]},
					new State(RIPPER_SWIPE_R3 + 1, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[3][2]},
					new State(RIPPER_SWIPE_R3 + 1, 0 | SF_QUICK_CALL, InitRipperSlash), // s_RipperSwipe[3][3]},
					new State(RIPPER_SWIPE_R3 + 2, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[3][4]},
					new State(RIPPER_SWIPE_R3 + 3, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[3][5]},
					new State(RIPPER_SWIPE_R3 + 3, 0 | SF_QUICK_CALL, InitRipperSlash), // s_RipperSwipe[3][6]},
					new State(RIPPER_SWIPE_R3 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_RipperSwipe[3][7]},
					new State(RIPPER_SWIPE_R3 + 3, RIPPER_SWIPE_RATE, DoRipperMove).setNext(),// s_RipperSwipe[3][7]},
			}, { new State(RIPPER_SWIPE_R4 + 0, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[4][1]},
					new State(RIPPER_SWIPE_R4 + 1, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[4][2]},
					new State(RIPPER_SWIPE_R4 + 1, 0 | SF_QUICK_CALL, InitRipperSlash), // s_RipperSwipe[4][3]},
					new State(RIPPER_SWIPE_R4 + 2, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[4][4]},
					new State(RIPPER_SWIPE_R4 + 3, RIPPER_SWIPE_RATE, NullRipper), // s_RipperSwipe[4][5]},
					new State(RIPPER_SWIPE_R4 + 3, 0 | SF_QUICK_CALL, InitRipperSlash), // s_RipperSwipe[4][6]},
					new State(RIPPER_SWIPE_R4 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_RipperSwipe[4][7]},
					new State(RIPPER_SWIPE_R4 + 3, RIPPER_SWIPE_RATE, DoRipperMove).setNext(),// s_RipperSwipe[4][7]},
			} };

	//////////////////////
	//
	// RIPPER SPEW
	//
	//////////////////////

	public static final int RIPPER_SPEW_RATE = 8;

	public static final Animator InitCoolgFire = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitCoolgFire(SpriteNum) != 0;
		}
	};

	private static final State s_RipperSpew[][] = { { new State(RIPPER_SWIPE_R0 + 0, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[0][1]},
			new State(RIPPER_SWIPE_R0 + 1, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[0][2]},
			new State(RIPPER_SWIPE_R0 + 1, 0 | SF_QUICK_CALL, InitCoolgFire), // s_RipperSpew[0][3]},
			new State(RIPPER_SWIPE_R0 + 2, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[0][4]},
			new State(RIPPER_SWIPE_R0 + 3, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[0][5]},
			new State(RIPPER_SWIPE_R0 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_RipperSpew[0][6]},
			new State(RIPPER_SWIPE_R0 + 3, RIPPER_SPEW_RATE, DoRipperMove).setNext(),// s_RipperSpew[0][6]},
			}, { new State(RIPPER_SWIPE_R1 + 0, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[1][1]},
					new State(RIPPER_SWIPE_R1 + 1, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[1][2]},
					new State(RIPPER_SWIPE_R1 + 1, 0 | SF_QUICK_CALL, InitCoolgFire), // s_RipperSpew[1][3]},
					new State(RIPPER_SWIPE_R1 + 2, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[1][4]},
					new State(RIPPER_SWIPE_R1 + 3, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[1][5]},
					new State(RIPPER_SWIPE_R1 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_RipperSpew[1][6]},
					new State(RIPPER_SWIPE_R1 + 3, RIPPER_SPEW_RATE, DoRipperMove).setNext(),// s_RipperSpew[1][6]},
			}, { new State(RIPPER_SWIPE_R2 + 0, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[2][1]},
					new State(RIPPER_SWIPE_R2 + 1, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[2][2]},
					new State(RIPPER_SWIPE_R2 + 1, 0 | SF_QUICK_CALL, InitCoolgFire), // s_RipperSpew[2][3]},
					new State(RIPPER_SWIPE_R2 + 2, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[2][4]},
					new State(RIPPER_SWIPE_R2 + 3, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[2][5]},
					new State(RIPPER_SWIPE_R2 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_RipperSpew[2][6]},
					new State(RIPPER_SWIPE_R2 + 3, RIPPER_SPEW_RATE, DoRipperMove).setNext(),// s_RipperSpew[2][6]},
			}, { new State(RIPPER_SWIPE_R3 + 0, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[3][1]},
					new State(RIPPER_SWIPE_R3 + 1, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[3][2]},
					new State(RIPPER_SWIPE_R3 + 1, 0 | SF_QUICK_CALL, InitCoolgFire), // s_RipperSpew[3][3]},
					new State(RIPPER_SWIPE_R3 + 2, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[3][4]},
					new State(RIPPER_SWIPE_R3 + 3, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[3][5]},
					new State(RIPPER_SWIPE_R3 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_RipperSpew[3][6]},
					new State(RIPPER_SWIPE_R3 + 3, RIPPER_SPEW_RATE, DoRipperMove).setNext(),// s_RipperSpew[3][6]},
			}, { new State(RIPPER_SWIPE_R4 + 0, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[4][1]},
					new State(RIPPER_SWIPE_R4 + 1, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[4][2]},
					new State(RIPPER_SWIPE_R4 + 1, 0 | SF_QUICK_CALL, InitCoolgFire), // s_RipperSpew[4][3]},
					new State(RIPPER_SWIPE_R4 + 2, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[4][4]},
					new State(RIPPER_SWIPE_R4 + 3, RIPPER_SPEW_RATE, NullRipper), // s_RipperSpew[4][5]},
					new State(RIPPER_SWIPE_R4 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_RipperSpew[4][6]},
					new State(RIPPER_SWIPE_R4 + 3, RIPPER_SPEW_RATE, DoRipperMove).setNext(),// s_RipperSpew[4][6]},
			} };

	//////////////////////
	//
	// RIPPER HEART - show players heart
	//
	//////////////////////

	public static final int RIPPER_HEART_RATE = 14;

	private static final Animator DoRipperStandHeart = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoRipperStandHeart(SpriteNum) != 0;
		}
	};

	private static final State s_RipperHeart[][] = {
			{ new State(RIPPER_HEART_R0 + 0, RIPPER_HEART_RATE, DoRipperStandHeart).setNext(),// s_RipperHeart[0][0]},
			}, { new State(RIPPER_HEART_R1 + 0, RIPPER_HEART_RATE, DoRipperStandHeart).setNext(),// s_RipperHeart[1][0]},
			}, { new State(RIPPER_HEART_R2 + 0, RIPPER_HEART_RATE, DoRipperStandHeart).setNext(),// s_RipperHeart[2][0]},
			}, { new State(RIPPER_HEART_R3 + 0, RIPPER_HEART_RATE, DoRipperStandHeart).setNext(),// s_RipperHeart[3][0]},
			}, { new State(RIPPER_HEART_R4 + 0, RIPPER_HEART_RATE, DoRipperStandHeart).setNext(),// s_RipperHeart[4][0]},
			} };

	//////////////////////
	//
	// RIPPER HANG
	//
	//////////////////////

	public static final int RIPPER_HANG_RATE = 14;

	private static final Animator DoRipperHang = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoRipperHang(SpriteNum) != 0;
		}
	};

	private static final State s_RipperHang[][] = {
			{ new State(RIPPER_HANG_R0 + 0, RIPPER_HANG_RATE, DoRipperHang).setNext(),// s_RipperHang[0][0]},
			}, { new State(RIPPER_HANG_R1 + 0, RIPPER_HANG_RATE, DoRipperHang).setNext(),// s_RipperHang[1][0]},
			}, { new State(RIPPER_HANG_R2 + 0, RIPPER_HANG_RATE, DoRipperHang).setNext(),// s_RipperHang[2][0]},
			}, { new State(RIPPER_HANG_R3 + 0, RIPPER_HANG_RATE, DoRipperHang).setNext(),// s_RipperHang[3][0]},
			}, { new State(RIPPER_HANG_R4 + 0, RIPPER_HANG_RATE, DoRipperHang).setNext(),// s_RipperHang[4][0]},
			} };

	//////////////////////
	//
	// RIPPER PAIN
	//
	//////////////////////

	public static final int RIPPER_PAIN_RATE = 38;

	private static final Animator DoRipperPain = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoRipperPain(SpriteNum) != 0;
		}
	};

	private static final State s_RipperPain[][] = {
			{ new State(RIPPER_JUMP_R0 + 0, RIPPER_PAIN_RATE, DoRipperPain).setNext(),// s_RipperPain[0][0]},
			}, { new State(RIPPER_JUMP_R0 + 0, RIPPER_PAIN_RATE, DoRipperPain).setNext(),// s_RipperPain[1][0]},
			}, { new State(RIPPER_JUMP_R0 + 0, RIPPER_PAIN_RATE, DoRipperPain).setNext(),// s_RipperPain[2][0]},
			}, { new State(RIPPER_JUMP_R0 + 0, RIPPER_PAIN_RATE, DoRipperPain).setNext(),// s_RipperPain[3][0]},
			}, { new State(RIPPER_JUMP_R0 + 0, RIPPER_PAIN_RATE, DoRipperPain).setNext(),// s_RipperPain[4][0]},
			} };

	//////////////////////
	//
	// RIPPER JUMP
	//
	//////////////////////

	public static final int RIPPER_JUMP_RATE = 25;

	private static final Animator DoRipperMoveJump = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoRipperMoveJump(SpriteNum) != 0;
		}
	};

	private static final State s_RipperJump[][] = { { new State(RIPPER_JUMP_R0 + 0, RIPPER_JUMP_RATE, NullRipper), // s_RipperJump[0][1]},
			new State(RIPPER_JUMP_R0 + 1, RIPPER_JUMP_RATE, DoRipperMoveJump).setNext(),// s_RipperJump[0][1]},
			}, { new State(RIPPER_JUMP_R1 + 0, RIPPER_JUMP_RATE, NullRipper), // s_RipperJump[1][1]},
					new State(RIPPER_JUMP_R1 + 1, RIPPER_JUMP_RATE, DoRipperMoveJump).setNext(),// s_RipperJump[1][1]},
			}, { new State(RIPPER_JUMP_R2 + 0, RIPPER_JUMP_RATE, NullRipper), // s_RipperJump[2][1]},
					new State(RIPPER_JUMP_R2 + 1, RIPPER_JUMP_RATE, DoRipperMoveJump).setNext(),// s_RipperJump[2][1]},
			}, { new State(RIPPER_JUMP_R3 + 0, RIPPER_JUMP_RATE, NullRipper), // s_RipperJump[3][1]},
					new State(RIPPER_JUMP_R3 + 1, RIPPER_JUMP_RATE, DoRipperMoveJump).setNext(),// s_RipperJump[3][1]},
			}, { new State(RIPPER_JUMP_R4 + 0, RIPPER_JUMP_RATE, NullRipper), // s_RipperJump[4][1]},
					new State(RIPPER_JUMP_R4 + 1, RIPPER_JUMP_RATE, DoRipperMoveJump).setNext(),// s_RipperJump[4][1]},
			} };

	//////////////////////
	//
	// RIPPER FALL
	//
	//////////////////////

	public static final int RIPPER_FALL_RATE = 25;

	private static final State s_RipperFall[][] = {
			{ new State(RIPPER_FALL_R0 + 0, RIPPER_FALL_RATE, DoRipperMoveJump).setNext(),// s_RipperFall[0][0]},
			}, { new State(RIPPER_FALL_R1 + 0, RIPPER_FALL_RATE, DoRipperMoveJump).setNext(),// s_RipperFall[1][0]},
			}, { new State(RIPPER_FALL_R2 + 0, RIPPER_FALL_RATE, DoRipperMoveJump).setNext(),// s_RipperFall[2][0]},
			}, { new State(RIPPER_FALL_R3 + 0, RIPPER_FALL_RATE, DoRipperMoveJump).setNext(),// s_RipperFall[3][0]},
			}, { new State(RIPPER_FALL_R4 + 0, RIPPER_FALL_RATE, DoRipperMoveJump).setNext(),// s_RipperFall[4][0]},
			} };

	//////////////////////
	//
	// RIPPER JUMP ATTACK
	//
	//////////////////////

	public static final int RIPPER_JUMP_ATTACK_RATE = 35;
	private static final Animator DoRipperBeginJumpAttack = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoRipperBeginJumpAttack(SpriteNum) != 0;
		}
	};

	private static final State s_RipperJumpAttack[][] = {
			{ new State(RIPPER_JUMP_R0 + 0, RIPPER_JUMP_ATTACK_RATE, NullRipper), // s_RipperJumpAttack[0][1]},
					new State(RIPPER_JUMP_R0 + 0, 0 | SF_QUICK_CALL, DoRipperBeginJumpAttack), // s_RipperJumpAttack[0][2]},
					new State(RIPPER_JUMP_R0 + 1, RIPPER_JUMP_ATTACK_RATE, DoRipperMoveJump).setNext(),// s_RipperJumpAttack[0][2]},
			}, { new State(RIPPER_JUMP_R1 + 0, RIPPER_JUMP_ATTACK_RATE, NullRipper), // s_RipperJumpAttack[1][1]},
					new State(RIPPER_JUMP_R1 + 0, 0 | SF_QUICK_CALL, DoRipperBeginJumpAttack), // s_RipperJumpAttack[1][2]},
					new State(RIPPER_JUMP_R1 + 1, RIPPER_JUMP_ATTACK_RATE, DoRipperMoveJump).setNext(),// s_RipperJumpAttack[1][2]},
			}, { new State(RIPPER_JUMP_R2 + 0, RIPPER_JUMP_ATTACK_RATE, NullRipper), // s_RipperJumpAttack[2][1]},
					new State(RIPPER_JUMP_R2 + 0, 0 | SF_QUICK_CALL, DoRipperBeginJumpAttack), // s_RipperJumpAttack[2][2]},
					new State(RIPPER_JUMP_R2 + 1, RIPPER_JUMP_ATTACK_RATE, DoRipperMoveJump).setNext(),// s_RipperJumpAttack[2][2]},
			}, { new State(RIPPER_JUMP_R3 + 0, RIPPER_JUMP_ATTACK_RATE, NullRipper), // s_RipperJumpAttack[3][1]},
					new State(RIPPER_JUMP_R3 + 0, 0 | SF_QUICK_CALL, DoRipperBeginJumpAttack), // s_RipperJumpAttack[3][2]},
					new State(RIPPER_JUMP_R3 + 1, RIPPER_JUMP_ATTACK_RATE, DoRipperMoveJump).setNext(),// s_RipperJumpAttack[3][2]},
			}, { new State(RIPPER_JUMP_R4 + 0, RIPPER_JUMP_ATTACK_RATE, NullRipper), // s_RipperJumpAttack[4][1]},
					new State(RIPPER_JUMP_R4 + 0, 0 | SF_QUICK_CALL, DoRipperBeginJumpAttack), // s_RipperJumpAttack[4][2]},
					new State(RIPPER_JUMP_R4 + 1, RIPPER_JUMP_ATTACK_RATE, DoRipperMoveJump).setNext(),// s_RipperJumpAttack[4][2]},
			} };

	//////////////////////
	//
	// RIPPER HANG_JUMP
	//
	//////////////////////

	public static final int RIPPER_HANG_JUMP_RATE = 20;

	private static final Animator DoRipperHangJF = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoRipperHangJF(SpriteNum) != 0;
		}
	};

	private static final State s_RipperHangJump[][] = {
			{ new State(RIPPER_JUMP_R0 + 0, RIPPER_HANG_JUMP_RATE, NullRipper), // s_RipperHangJump[0][1]},
					new State(RIPPER_JUMP_R0 + 1, RIPPER_HANG_JUMP_RATE, DoRipperHangJF).setNext(),// s_RipperHangJump[0][1]},
			}, { new State(RIPPER_JUMP_R1 + 0, RIPPER_HANG_JUMP_RATE, NullRipper), // s_RipperHangJump[1][1]},
					new State(RIPPER_JUMP_R1 + 1, RIPPER_HANG_JUMP_RATE, DoRipperHangJF).setNext(),// s_RipperHangJump[1][1]},
			}, { new State(RIPPER_JUMP_R2 + 0, RIPPER_HANG_JUMP_RATE, NullRipper), // s_RipperHangJump[2][1]},
					new State(RIPPER_JUMP_R2 + 1, RIPPER_HANG_JUMP_RATE, DoRipperHangJF).setNext(),// s_RipperHangJump[2][1]},
			}, { new State(RIPPER_JUMP_R3 + 0, RIPPER_HANG_JUMP_RATE, NullRipper), // s_RipperHangJump[3][1]},
					new State(RIPPER_JUMP_R3 + 1, RIPPER_HANG_JUMP_RATE, DoRipperHangJF).setNext(),// s_RipperHangJump[3][1]},
			}, { new State(RIPPER_JUMP_R4 + 0, RIPPER_HANG_JUMP_RATE, NullRipper), // s_RipperHangJump[4][1]},
					new State(RIPPER_JUMP_R4 + 1, RIPPER_HANG_JUMP_RATE, DoRipperHangJF).setNext(),// s_RipperHangJump[4][1]},
			} };

	//////////////////////
	//
	// RIPPER HANG_FALL
	//
	//////////////////////

	private static final State s_RipperHangFall[][] = {
			{ new State(RIPPER_FALL_R0 + 0, RIPPER_FALL_RATE, DoRipperHangJF).setNext(),// s_RipperHangFall[0][0]},
			}, { new State(RIPPER_FALL_R1 + 0, RIPPER_FALL_RATE, DoRipperHangJF).setNext(),// s_RipperHangFall[1][0]},
			}, { new State(RIPPER_FALL_R2 + 0, RIPPER_FALL_RATE, DoRipperHangJF).setNext(),// s_RipperHangFall[2][0]},
			}, { new State(RIPPER_FALL_R3 + 0, RIPPER_FALL_RATE, DoRipperHangJF).setNext(),// s_RipperHangFall[3][0]},
			}, { new State(RIPPER_FALL_R4 + 0, RIPPER_FALL_RATE, DoRipperHangJF).setNext(),// s_RipperHangFall[4][0]},
			} };

	//////////////////////
	//
	// RIPPER DIE
	//
	//////////////////////

	public static final int RIPPER_DIE_RATE = 16;

	private static final State s_RipperDie[] = { new State(RIPPER_DIE + 0, RIPPER_DIE_RATE, NullRipper), // s_RipperDie[1]},
			new State(RIPPER_DIE + 1, RIPPER_DIE_RATE, NullRipper), // s_RipperDie[2]},
			new State(RIPPER_DIE + 2, RIPPER_DIE_RATE, NullRipper), // s_RipperDie[3]},
			new State(RIPPER_DIE + 3, RIPPER_DIE_RATE, NullRipper), // s_RipperDie[4]},
			new State(RIPPER_DEAD, RIPPER_DIE_RATE, DoActorDebris).setNext(),// s_RipperDie[4]},
	};

	public static final int RIPPER_DEAD_RATE = 8;

	private static final State s_RipperDead[] = { new State(RIPPER_DIE + 2, RIPPER_DEAD_RATE, null), // s_RipperDead[1]},
			new State(RIPPER_DIE + 3, RIPPER_DEAD_RATE, null), // s_RipperDead[2]},
			new State(RIPPER_DEAD, SF_QUICK_CALL, QueueFloorBlood), // s_RipperDead[3]},
			new State(RIPPER_DEAD, RIPPER_DEAD_RATE, DoActorDebris).setNext(),// s_RipperDead[3]},
	};

	private static final Animator DoActorDeathMove = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoActorDeathMove(SpriteNum) != 0;
		}
	};

	private static final State s_RipperDeathJump[] = {
			new State(RIPPER_DIE + 0, RIPPER_DIE_RATE, DoActorDeathMove).setNext(),// s_RipperDeathJump[0]}
	};

	private static final State s_RipperDeathFall[] = {
			new State(RIPPER_DIE + 1, RIPPER_DIE_RATE, DoActorDeathMove).setNext(),// s_RipperDeathFall[0]}
	};

	public enum RipperStateGroup implements StateGroup {
		sg_RipperStand(s_RipperStand[0], s_RipperStand[1], s_RipperStand[2], s_RipperStand[3], s_RipperStand[4]),
		sg_RipperRun(s_RipperRun[0], s_RipperRun[1], s_RipperRun[2], s_RipperRun[3], s_RipperRun[4]),
		sg_RipperJump(s_RipperJump[0], s_RipperJump[1], s_RipperJump[2], s_RipperJump[3], s_RipperJump[4]),
		sg_RipperFall(s_RipperFall[0], s_RipperFall[1], s_RipperFall[2], s_RipperFall[3], s_RipperFall[4]),
		sg_RipperPain(s_RipperPain[0], s_RipperPain[1], s_RipperPain[2], s_RipperPain[3], s_RipperPain[4]),
		sg_RipperDie(s_RipperDie), sg_RipperDead(s_RipperDead), sg_RipperDeathJump(s_RipperDeathJump),
		sg_RipperDeathFall(s_RipperDeathFall),
		sg_RipperSpew(s_RipperSpew[0], s_RipperSpew[1], s_RipperSpew[2], s_RipperSpew[3], s_RipperSpew[4]),
		sg_RipperJumpAttack(s_RipperJumpAttack[0], s_RipperJumpAttack[1], s_RipperJumpAttack[2], s_RipperJumpAttack[3],
				s_RipperJumpAttack[4]),
		sg_RipperHeart(s_RipperHeart[0], s_RipperHeart[1], s_RipperHeart[2], s_RipperHeart[3], s_RipperHeart[4]),
		sg_RipperHang(s_RipperHang[0], s_RipperHang[1], s_RipperHang[2], s_RipperHang[3], s_RipperHang[4]),
		sg_RipperSwipe(s_RipperSwipe[0], s_RipperSwipe[1], s_RipperSwipe[2], s_RipperSwipe[3], s_RipperSwipe[4]),
		sg_RipperHangJump(s_RipperHangJump[0], s_RipperHangJump[1], s_RipperHangJump[2], s_RipperHangJump[3],
				s_RipperHangJump[4]),
		sg_RipperHangFall(s_RipperHangFall[0], s_RipperHangFall[1], s_RipperHangFall[2], s_RipperHangFall[3],
				s_RipperHangFall[4]);

		private final State[][] group;
		private int index = -1;

		RipperStateGroup(State[]... states) {
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

	public static void InitRipperStates() {
		for (RipperStateGroup sg : RipperStateGroup.values()) {
			for (int rot = 0; rot < sg.group.length; rot++) {
				State.InitState(sg.group[rot]);
			}
		}
	}

	private static final Actor_Action_Set RipperActionSet = new Actor_Action_Set(RipperStateGroup.sg_RipperStand,
			RipperStateGroup.sg_RipperRun, RipperStateGroup.sg_RipperJump, RipperStateGroup.sg_RipperFall, null, // RipperStateGroup.sg_RipperCrawl,
			null, // RipperStateGroup.sg_RipperSwim,
			null, // RipperStateGroup.sg_RipperFly,
			null, // RipperStateGroup.sg_RipperRise,
			null, // RipperStateGroup.sg_RipperSit,
			null, // RipperStateGroup.sg_RipperLook,
			null, // climb
			RipperStateGroup.sg_RipperPain, RipperStateGroup.sg_RipperDie, null, // RipperStateGroup.sg_RipperHariKari,
			RipperStateGroup.sg_RipperDead, RipperStateGroup.sg_RipperDeathJump, RipperStateGroup.sg_RipperDeathFall,
			new StateGroup[] { RipperStateGroup.sg_RipperSwipe, RipperStateGroup.sg_RipperSpew },
			new short[] { 800, 1024 },
			new StateGroup[] { RipperStateGroup.sg_RipperJumpAttack, RipperStateGroup.sg_RipperSpew },
			new short[] { 400, 1024 },
			new StateGroup[] { RipperStateGroup.sg_RipperHeart, RipperStateGroup.sg_RipperHang }, null, null);

	private static final Actor_Action_Set RipperBrownActionSet = new Actor_Action_Set(RipperStateGroup.sg_RipperStand,
			RipperStateGroup.sg_RipperRun, RipperStateGroup.sg_RipperJump, RipperStateGroup.sg_RipperFall, null, // RipperStateGroup.sg_RipperCrawl,
			null, // RipperStateGroup.sg_RipperSwim,
			null, // RipperStateGroup.sg_RipperFly,
			null, // RipperStateGroup.sg_RipperRise,
			null, // RipperStateGroup.sg_RipperSit,
			null, // RipperStateGroup.sg_RipperLook,
			null, // climb
			RipperStateGroup.sg_RipperPain, // pain
			RipperStateGroup.sg_RipperDie, null, // RipperStateGroup.sg_RipperHariKari,
			RipperStateGroup.sg_RipperDead, RipperStateGroup.sg_RipperDeathJump, RipperStateGroup.sg_RipperDeathFall,
			new StateGroup[] { RipperStateGroup.sg_RipperSwipe }, new short[] { 1024 },
			new StateGroup[] { RipperStateGroup.sg_RipperJumpAttack, RipperStateGroup.sg_RipperSwipe },
			new short[] { 800, 1024 },
			new StateGroup[] { RipperStateGroup.sg_RipperHeart, RipperStateGroup.sg_RipperHang }, null, null);

	public static int SetupRipper(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, RIPPER_RUN_R0, s_RipperRun[0][0]);
			u.Health = HEALTH_RIPPER / 2; // Baby rippers are weaker
		}

		ChangeState(SpriteNum, s_RipperRun[0][0]);
		u.Attrib = RipperAttrib;
		DoActorSetSpeed(SpriteNum, FAST_SPEED);
		u.StateEnd = s_RipperDie[0];
		u.Rot = RipperStateGroup.sg_RipperRun;
		sp.xrepeat = 64;
		sp.yrepeat = 64;

		if (sp.pal == PALETTE_BROWN_RIPPER) {
			EnemyDefaults(SpriteNum, RipperBrownActionSet, RipperPersonality);
			sp.xrepeat = 106;
			sp.yrepeat = 90;

			if (!TEST(sp.cstat, CSTAT_SPRITE_RESTORE))
				u.Health = HEALTH_MOMMA_RIPPER;

			sp.clipdist += 128 >> 2;
		} else {
			EnemyDefaults(SpriteNum, RipperActionSet, RipperPersonality);
		}

		u.Flags |= (SPR_XFLIP_TOGGLE);

		return (0);
	}

	private static int GetJumpHeight(short jump_speed, short jump_grav) {
		int jump_iterations;
		int height;

		jump_speed = (short) klabs(jump_speed);

		jump_iterations = jump_speed / (jump_grav * ACTORMOVETICS);

		height = jump_speed * jump_iterations * ACTORMOVETICS;

		height = DIV256(height);

		return (DIV2(height));
	}

	public static int PickJumpSpeed(int SpriteNum, int pix_height) {
		USER u = pUser[SpriteNum];

		u.jump_speed = -600;
		u.jump_grav = 8;

		while (true) {
			if (GetJumpHeight(u.jump_speed, u.jump_grav) > pix_height + 20)
				break;

			u.jump_speed -= 100;
		}

		return (u.jump_speed);
	}

	public static int PickJumpMaxSpeed(int SpriteNum, int max_speed) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		int zh;

		u.jump_speed = (short) max_speed;
		u.jump_grav = 8;

		zh = SPRITEp_TOS(sp);

		while (true) {
			if (zh - Z(GetJumpHeight(u.jump_speed, u.jump_grav)) - Z(16) > u.hiz)
				break;

			u.jump_speed += 100;

			if (u.jump_speed > -200)
				break;
		}

		return (u.jump_speed);
	}

//
// HANGING - Jumping/Falling/Stationary
//

	private static int InitRipperHang(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		int dist;

		short hitwall;
		short hitsect = -2;
		int hitx, hity;

		boolean Found = false;
		short dang, tang;

		for (dang = 0; dang < 2048; dang += 128) {
			tang = NORM_ANGLE(sp.ang + dang);

			FAFhitscan(sp.x, sp.y, sp.z - SPRITEp_SIZE_Z(sp), sp.sectnum, // Start position
					sintable[NORM_ANGLE(tang + 512)], // X vector of 3D ang
					sintable[tang], // Y vector of 3D ang
					0, // Z vector of 3D ang
					pHitInfo, CLIPMASK_MISSILE);

			hitsect = pHitInfo.hitsect;
			hitwall = pHitInfo.hitwall;
			hitx = pHitInfo.hitx;
			hity = pHitInfo.hity;

			if (hitsect < 0)
				continue;

			dist = Distance(sp.x, sp.y, hitx, hity);

			if (hitwall < 0 || dist < 2000 || dist > 7000) {
				continue;
			}

			Found = true;
			sp.ang = tang;
			break;
		}

		if (!Found) {
			InitActorDecide(SpriteNum);
			return (0);
		}

		NewStateGroup(SpriteNum, RipperStateGroup.sg_RipperHangJump);
		u.StateFallOverride = RipperStateGroup.sg_RipperHangFall;
		DoActorSetSpeed(SpriteNum, FAST_SPEED);

		// u.jump_speed = -800;
		PickJumpMaxSpeed(SpriteNum, -800);

		u.Flags |= (SPR_JUMPING);
		u.Flags &= ~(SPR_FALLING);

		// set up individual actor jump gravity
		u.jump_grav = 8;

		DoJump(SpriteNum);

		return (0);
	}

	private static int DoRipperHang(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if ((u.WaitTics -= ACTORMOVETICS) > 0)
			return (0);

		NewStateGroup(SpriteNum, RipperStateGroup.sg_RipperJumpAttack);
		// move to the 2nd frame - past the pause frame
		u.Tics += u.State.Tics;
		return (0);
	}

	private static int DoRipperMoveHang(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		int nx, ny;

		// Move while jumping
		nx = sp.xvel * sintable[NORM_ANGLE(sp.ang + 512)] >> 14;
		ny = sp.xvel * sintable[sp.ang] >> 14;

		// if cannot move the sprite
		if (!move_actor(SpriteNum, nx, ny, 0)) {
			switch (DTEST(u.ret, HIT_MASK)) {
			case HIT_WALL:
				short hitwall;
				short w, nw;

				hitwall = NORM_WALL(u.ret);

				NewStateGroup(SpriteNum, u.ActorActionSet.Special[1]);
				u.WaitTics = (short) (2 + ((RANDOM_P2(4 << 8) >> 8) * 120));

				// hang flush with the wall
				w = hitwall;
				nw = wall[w].point2;

				sp.ang = NORM_ANGLE(engine.getangle(wall[nw].x - wall[w].x, wall[nw].y - wall[w].y) - 512);
				return (0);
			}
		}

		return (0);
	}

	private static int DoRipperHangJF(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			if (TEST(u.Flags, SPR_JUMPING))
				DoJump(SpriteNum);
			else
				DoFall(SpriteNum);
		}

		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			if (DoRipperQuickJump(SpriteNum) != 0)
				return (0);

			InitActorDecide(SpriteNum);
		}

		DoRipperMoveHang(SpriteNum);

		return (0);

	}

//
// JUMP ATTACK
//

	private static final int RANDOM_NEG(int x) {
		return (RANDOM_P2((x) << 1) - (x));
	}

	private static int DoRipperBeginJumpAttack(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		SPRITE psp = sprite[pUser[SpriteNum].tgt_sp];

		short tang;

		tang = engine.getangle(psp.x - sp.x, psp.y - sp.y);

		if (move_sprite(SpriteNum, sintable[NORM_ANGLE(tang + 512)] >> 7, sintable[tang] >> 7, 0, u.ceiling_dist,
				u.floor_dist, CLIPMASK_ACTOR, ACTORMOVETICS) != 0)
			sp.ang = NORM_ANGLE((sp.ang + 1024) + (RANDOM_NEG(256 << 6) >> 6));
		else
			sp.ang = NORM_ANGLE(tang + (RANDOM_NEG(256 << 6) >> 6));

		DoActorSetSpeed(SpriteNum, FAST_SPEED);

		PickJumpMaxSpeed(SpriteNum, -400); // was -800

		u.Flags |= (SPR_JUMPING);
		u.Flags &= ~(SPR_FALLING);

		// set up individual actor jump gravity
		u.jump_grav = 17; // was 8

		// if I didn't do this here they get stuck in the air sometimes
		DoActorZrange(SpriteNum);

		DoJump(SpriteNum);

		return (0);
	}

	private static int DoRipperMoveJump(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			if (TEST(u.Flags, SPR_JUMPING))
				DoJump(SpriteNum);
			else
				DoFall(SpriteNum);
		}

		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			if (DoRipperQuickJump(SpriteNum) != 0)
				return (0);

			InitActorDecide(SpriteNum);
		}

		DoRipperMoveHang(SpriteNum);
		return (0);
	}

//
// STD MOVEMENT
//

	private static int DoRipperQuickJump(int SpriteNum) {
		USER u = pUser[SpriteNum];

		// Tests to see if ripper is on top of a player/enemy and then immediatly
		// does another jump

		if (u.lo_sp != -1) {
			SPRITE tsp = sprite[u.lo_sp];

			if (TEST(tsp.extra, SPRX_PLAYER_OR_ENEMY)) {
				NewStateGroup(SpriteNum, RipperStateGroup.sg_RipperJumpAttack);
				// move past the first private static final State
				u.Tics = 30;
				return (TRUE);
			}
		}

		return (FALSE);
	}

	private static int NullRipper(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		DoActorSectorDamage(SpriteNum);

		return (0);
	}

	private static int DoRipperPain(int SpriteNum) {
		USER u = pUser[SpriteNum];

		NullRipper(SpriteNum);

		if ((u.WaitTics -= ACTORMOVETICS) <= 0)
			InitActorDecide(SpriteNum);
		return (0);
	}

	public static int DoRipperRipHeart(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		SPRITE tsp = sprite[u.tgt_sp];

		NewStateGroup(SpriteNum, RipperStateGroup.sg_RipperHeart);
		u.WaitTics = 6 * 120;

		// player face ripper
		tsp.ang = engine.getangle(sp.x - tsp.x, sp.y - tsp.y);
		return (0);
	}

// CTW MODIFICATION
	private static int DoRipperStandHeart(int SpriteNum)
// CTW MODIFICATION END
	{
		USER u = pUser[SpriteNum];

		NullRipper(SpriteNum);

		if ((u.WaitTics -= ACTORMOVETICS) <= 0)
			NewStateGroup(SpriteNum, RipperStateGroup.sg_RipperRun);
		return (0);
	}

	void RipperHatch(int Weapon) {
		SPRITE wp = sprite[Weapon];

		short newsp;
		SPRITE np;
		USER nu;

		short rip_ang = (short) RANDOM_P2(2048);

		newsp = COVERinsertsprite(wp.sectnum, STAT_DEFAULT);
		np = sprite[newsp];
		np.reset();
		np.sectnum = wp.sectnum;
		np.statnum = STAT_DEFAULT;
		np.x = wp.x;
		np.y = wp.y;
		np.z = wp.z;
		np.owner = -1;
		// np.xrepeat = np.yrepeat = 36;
		np.xrepeat = np.yrepeat = 64;
		np.ang = rip_ang;
		np.pal = 0;
		SetupRipper(newsp);
		nu = pUser[newsp];

		// make immediately active
		nu.Flags |= (SPR_ACTIVE);

		NewStateGroup(newsp, nu.ActorActionSet.Jump);
		nu.ActorActionFunc = DoActorMoveJump;
		DoActorSetSpeed(newsp, FAST_SPEED);
		PickJumpMaxSpeed(newsp, -600);

		nu.Flags |= (SPR_JUMPING);
		nu.Flags &= ~(SPR_FALLING);

		nu.jump_grav = 8;

		// if I didn't do this here they get stuck in the air sometimes
		DoActorZrange(newsp);

		DoJump(newsp);
	}

	private static int DoRipperMove(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (u.scale_speed != 0) {
			DoScaleSprite(SpriteNum);
		}

		if (TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			if (TEST(u.Flags, SPR_JUMPING))
				DoJump(SpriteNum);
			else
				DoFall(SpriteNum);
		}

		// if on a player/enemy sprite jump quickly
		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			if (DoRipperQuickJump(SpriteNum) != 0)
				return (0);

			KeepActorOnFloor(SpriteNum);
		}

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		if (u.track >= 0)
			ActorFollowTrack(SpriteNum, ACTORMOVETICS);
		else
			(u.ActorActionFunc).invoke(SpriteNum);

		DoActorSectorDamage(SpriteNum);
		return (0);
	}

	public static void RipperSaveable()
	{
		SaveData(InitCoolgFire);
		SaveData(DoActorDeathMove);
		
		SaveData(InitRipperHang);
		SaveData(DoRipperHang);
		SaveData(DoRipperHangJF);

		SaveData(DoRipperBeginJumpAttack);
		SaveData(DoRipperMoveJump);

		SaveData(NullRipper);
		SaveData(DoRipperPain);
		SaveData(DoRipperStandHeart);
		SaveData(DoRipperMove);
		SaveData(RipperPersonality);

		SaveData(RipperAttrib);

		SaveData(s_RipperRun);
		SaveGroup(RipperStateGroup.sg_RipperRun);
		SaveData(s_RipperStand);
		SaveGroup(RipperStateGroup.sg_RipperStand);
		SaveData(s_RipperSwipe);
		SaveGroup(RipperStateGroup.sg_RipperSwipe);
		SaveData(s_RipperSpew);
		SaveGroup(RipperStateGroup.sg_RipperSpew);
		SaveData(s_RipperHeart);
		SaveGroup(RipperStateGroup.sg_RipperHeart);
		SaveData(s_RipperHang);
		SaveGroup(RipperStateGroup.sg_RipperHang);
		SaveData(s_RipperPain);
		SaveGroup(RipperStateGroup.sg_RipperPain);
		SaveData(s_RipperJump);
		SaveGroup(RipperStateGroup.sg_RipperJump);
		SaveData(s_RipperFall);
		SaveGroup(RipperStateGroup.sg_RipperFall);
		SaveData(s_RipperJumpAttack);
		SaveGroup(RipperStateGroup.sg_RipperJumpAttack);
		SaveData(s_RipperHangJump);
		SaveGroup(RipperStateGroup.sg_RipperHangJump);
		SaveData(s_RipperHangFall);
		SaveGroup(RipperStateGroup.sg_RipperHangFall);
		SaveData(s_RipperDie);
		SaveData(s_RipperDead);
		SaveGroup(RipperStateGroup.sg_RipperDie);
		SaveGroup(RipperStateGroup.sg_RipperDead);
		SaveData(s_RipperDeathJump);
		SaveData(s_RipperDeathFall);
		SaveGroup(RipperStateGroup.sg_RipperDeathJump);
		SaveGroup(RipperStateGroup.sg_RipperDeathFall);

		SaveData(RipperActionSet);
		SaveData(RipperBrownActionSet);
	}
}
