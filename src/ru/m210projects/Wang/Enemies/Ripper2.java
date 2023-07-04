package ru.m210projects.Wang.Enemies;

import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Pragmas.klabs;
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
import static ru.m210projects.Wang.Ai.InitActorAmbientNoise;
import static ru.m210projects.Wang.Ai.InitActorAttack;
import static ru.m210projects.Wang.Ai.InitActorAttackNoise;
import static ru.m210projects.Wang.Ai.InitActorDecide;
import static ru.m210projects.Wang.Ai.InitActorFindPlayer;
import static ru.m210projects.Wang.Ai.InitActorMoveCloser;
import static ru.m210projects.Wang.Ai.InitActorWanderAround;
import static ru.m210projects.Wang.Ai.NORM_SPEED;
import static ru.m210projects.Wang.Digi.DIGI_RIPPER2ALERT;
import static ru.m210projects.Wang.Digi.DIGI_RIPPER2AMBIENT;
import static ru.m210projects.Wang.Digi.DIGI_RIPPER2ATTACK;
import static ru.m210projects.Wang.Digi.DIGI_RIPPER2CHEST;
import static ru.m210projects.Wang.Digi.DIGI_RIPPER2HEARTOUT;
import static ru.m210projects.Wang.Digi.DIGI_RIPPER2PAIN;
import static ru.m210projects.Wang.Digi.DIGI_RIPPER2SCREAM;
import static ru.m210projects.Wang.Enemies.Coolie.EnemyDefaults;
import static ru.m210projects.Wang.Enemies.Hornet.DoCheckSwarm;
import static ru.m210projects.Wang.Enemies.Ninja.DoActorDeathMove;
import static ru.m210projects.Wang.Enemies.Ripper.PickJumpMaxSpeed;
import static ru.m210projects.Wang.Game.Distance;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_ACTOR;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_MISSILE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_RESTORE;
import static ru.m210projects.Wang.Gameutils.HEALTH_MOMMA_RIPPER;
import static ru.m210projects.Wang.Gameutils.HEALTH_RIPPER2;
import static ru.m210projects.Wang.Gameutils.HIT_MASK;
import static ru.m210projects.Wang.Gameutils.HIT_WALL;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.NORM_WALL;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.SF_QUICK_CALL;
import static ru.m210projects.Wang.Gameutils.SF_TIC_ADJUST;
import static ru.m210projects.Wang.Gameutils.SPRITEp_SIZE_Z;
import static ru.m210projects.Wang.Gameutils.SPRX_PLAYER_OR_ENEMY;
import static ru.m210projects.Wang.Gameutils.SPR_ACTIVE;
import static ru.m210projects.Wang.Gameutils.SPR_FALLING;
import static ru.m210projects.Wang.Gameutils.SPR_JUMPING;
import static ru.m210projects.Wang.Gameutils.SPR_SLIDING;
import static ru.m210projects.Wang.Gameutils.SPR_XFLIP_TOGGLE;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.JTags.TAG_SWARMSPOT;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Names.RIPPER2_DEAD;
import static ru.m210projects.Wang.Names.RIPPER2_DIE;
import static ru.m210projects.Wang.Names.RIPPER2_FALL_R0;
import static ru.m210projects.Wang.Names.RIPPER2_FALL_R1;
import static ru.m210projects.Wang.Names.RIPPER2_FALL_R2;
import static ru.m210projects.Wang.Names.RIPPER2_FALL_R3;
import static ru.m210projects.Wang.Names.RIPPER2_FALL_R4;
import static ru.m210projects.Wang.Names.RIPPER2_HANG_R0;
import static ru.m210projects.Wang.Names.RIPPER2_HANG_R1;
import static ru.m210projects.Wang.Names.RIPPER2_HANG_R2;
import static ru.m210projects.Wang.Names.RIPPER2_HANG_R3;
import static ru.m210projects.Wang.Names.RIPPER2_HANG_R4;
import static ru.m210projects.Wang.Names.RIPPER2_HEART_R0;
import static ru.m210projects.Wang.Names.RIPPER2_HEART_R1;
import static ru.m210projects.Wang.Names.RIPPER2_HEART_R2;
import static ru.m210projects.Wang.Names.RIPPER2_HEART_R3;
import static ru.m210projects.Wang.Names.RIPPER2_HEART_R4;
import static ru.m210projects.Wang.Names.RIPPER2_JUMP_R0;
import static ru.m210projects.Wang.Names.RIPPER2_JUMP_R1;
import static ru.m210projects.Wang.Names.RIPPER2_JUMP_R2;
import static ru.m210projects.Wang.Names.RIPPER2_JUMP_R3;
import static ru.m210projects.Wang.Names.RIPPER2_JUMP_R4;
import static ru.m210projects.Wang.Names.RIPPER2_MEKONG_R0;
import static ru.m210projects.Wang.Names.RIPPER2_MEKONG_R1;
import static ru.m210projects.Wang.Names.RIPPER2_MEKONG_R2;
import static ru.m210projects.Wang.Names.RIPPER2_MEKONG_R3;
import static ru.m210projects.Wang.Names.RIPPER2_MEKONG_R4;
import static ru.m210projects.Wang.Names.RIPPER2_RUNFAST_R0;
import static ru.m210projects.Wang.Names.RIPPER2_RUNFAST_R1;
import static ru.m210projects.Wang.Names.RIPPER2_RUNFAST_R2;
import static ru.m210projects.Wang.Names.RIPPER2_RUNFAST_R3;
import static ru.m210projects.Wang.Names.RIPPER2_RUNFAST_R4;
import static ru.m210projects.Wang.Names.RIPPER2_RUN_R0;
import static ru.m210projects.Wang.Names.RIPPER2_RUN_R1;
import static ru.m210projects.Wang.Names.RIPPER2_RUN_R2;
import static ru.m210projects.Wang.Names.RIPPER2_RUN_R3;
import static ru.m210projects.Wang.Names.RIPPER2_RUN_R4;
import static ru.m210projects.Wang.Names.RIPPER2_STAND_R0;
import static ru.m210projects.Wang.Names.RIPPER2_STAND_R1;
import static ru.m210projects.Wang.Names.RIPPER2_STAND_R2;
import static ru.m210projects.Wang.Names.RIPPER2_STAND_R3;
import static ru.m210projects.Wang.Names.RIPPER2_STAND_R4;
import static ru.m210projects.Wang.Names.RIPPER2_SWIPE_R0;
import static ru.m210projects.Wang.Names.RIPPER2_SWIPE_R1;
import static ru.m210projects.Wang.Names.RIPPER2_SWIPE_R2;
import static ru.m210projects.Wang.Names.RIPPER2_SWIPE_R3;
import static ru.m210projects.Wang.Names.RIPPER2_SWIPE_R4;
import static ru.m210projects.Wang.Names.STAT_DEFAULT;
import static ru.m210projects.Wang.Palette.PALETTE_BROWN_RIPPER;
import static ru.m210projects.Wang.Player.QueueFloorBlood;
import static ru.m210projects.Wang.Rooms.COVERinsertsprite;
import static ru.m210projects.Wang.Rooms.FAFhitscan;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sound.v3df_none;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.DoActorZrange;
import static ru.m210projects.Wang.Sprites.NewStateGroup;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Sprites.move_actor;
import static ru.m210projects.Wang.Sprites.move_sprite;
import static ru.m210projects.Wang.Track.ActorFollowTrack;
import static ru.m210projects.Wang.Type.MyTypes.DTEST;
import static ru.m210projects.Wang.Type.MyTypes.FALSE;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.MyTypes.TRUE;
import static ru.m210projects.Wang.Type.Saveable.*;
import static ru.m210projects.Wang.Weapon.InitRipperSlash;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Type.ATTRIBUTE;
import ru.m210projects.Wang.Type.Actor_Action_Set;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;
import ru.m210projects.Wang.Type.VOC3D;

public class Ripper2 {

	public static final Animator InitRipper2Charge = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return InitRipper2Charge(spr) != 0;
		}
	};

	public static final Animator InitRipper2Hang = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return InitRipper2Hang(spr) != 0;
		}
	};

	private static final Decision Ripper2Battle[] = { new Decision(879, InitRipper2Charge),
			new Decision(883, InitActorAttackNoise), new Decision(900, InitRipper2Hang),
			new Decision(1024, InitActorAttack), };

	private static final Decision Ripper2Offense[] = { new Decision(789, InitActorMoveCloser),
			new Decision(790, InitActorAttackNoise), new Decision(800, InitRipper2Hang),
			new Decision(1024, InitActorAttack), };

	private static final Decision Ripper2Broadcast[] = { new Decision(3, InitActorAmbientNoise),
			new Decision(1024, InitActorDecide), };

	private static final Decision Ripper2Surprised[] = { new Decision(40, InitRipper2Hang),
			new Decision(701, InitActorMoveCloser), new Decision(1024, InitActorDecide), };

	private static final Decision Ripper2Evasive[] = { new Decision(10, InitActorMoveCloser),
			new Decision(1024, InitRipper2Charge), };

	private static final Decision Ripper2LostTarget[] = { new Decision(900, InitActorFindPlayer),
			new Decision(1024, InitActorWanderAround) };

	private static final Decision Ripper2CloseRange[] = { new Decision(1024, InitActorAttack) };

	private static final Personality Ripper2Personality = new Personality(Ripper2Battle, Ripper2Offense,
			Ripper2Broadcast, Ripper2Surprised, Ripper2Evasive, Ripper2LostTarget, Ripper2CloseRange,
			Ripper2CloseRange);

	private static final ATTRIBUTE Ripper2Attrib = new ATTRIBUTE(new short[] { 100, 120, 300, 380 }, // Speeds
			new short[] { 5, 0, -2, -4 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { DIGI_RIPPER2AMBIENT, DIGI_RIPPER2ALERT, DIGI_RIPPER2ATTACK, DIGI_RIPPER2PAIN,
					DIGI_RIPPER2SCREAM, DIGI_RIPPER2HEARTOUT, 0, 0, 0, 0 });

	public enum Ripper2StateGroup implements StateGroup {
		sg_Ripper2Stand(s_Ripper2Stand[0], s_Ripper2Stand[1], s_Ripper2Stand[2], s_Ripper2Stand[3], s_Ripper2Stand[4]),
		sg_Ripper2Run(s_Ripper2Run[0], s_Ripper2Run[1], s_Ripper2Run[2], s_Ripper2Run[3], s_Ripper2Run[4]),
		sg_Ripper2RunFast(s_Ripper2RunFast[0], s_Ripper2RunFast[1], s_Ripper2RunFast[2], s_Ripper2RunFast[3],
				s_Ripper2RunFast[4]),
		sg_Ripper2Jump(s_Ripper2Jump[0], s_Ripper2Jump[1], s_Ripper2Jump[2], s_Ripper2Jump[3], s_Ripper2Jump[4]),
		sg_Ripper2Swipe(s_Ripper2Swipe[0], s_Ripper2Swipe[1], s_Ripper2Swipe[2], s_Ripper2Swipe[3], s_Ripper2Swipe[4]),
		sg_Ripper2Kong(s_Ripper2Kong[0], s_Ripper2Kong[1], s_Ripper2Kong[2], s_Ripper2Kong[3], s_Ripper2Kong[4]),
		sg_Ripper2Heart(s_Ripper2Heart[0], s_Ripper2Heart[1], s_Ripper2Heart[2], s_Ripper2Heart[3], s_Ripper2Heart[4]),
		sg_Ripper2Pain(s_Ripper2Pain[0], s_Ripper2Pain[1], s_Ripper2Pain[2], s_Ripper2Pain[3], s_Ripper2Pain[4]),
		sg_Ripper2Fall(s_Ripper2Fall[0], s_Ripper2Fall[1], s_Ripper2Fall[2], s_Ripper2Fall[3], s_Ripper2Fall[4]),
		sg_Ripper2JumpAttack(s_Ripper2JumpAttack[0], s_Ripper2JumpAttack[1], s_Ripper2JumpAttack[2],
				s_Ripper2JumpAttack[3], s_Ripper2JumpAttack[4]),
		sg_Ripper2HangJump(s_Ripper2HangJump[0], s_Ripper2HangJump[1], s_Ripper2HangJump[2], s_Ripper2HangJump[3],
				s_Ripper2HangJump[4]),
		sg_Ripper2HangFall(s_Ripper2HangFall[0], s_Ripper2HangFall[1], s_Ripper2HangFall[2], s_Ripper2HangFall[3],
				s_Ripper2HangFall[4]),
		sg_Ripper2Hang(s_Ripper2Hang[0], s_Ripper2Hang[1], s_Ripper2Hang[2], s_Ripper2Hang[3], s_Ripper2Hang[4]),
		sg_Ripper2Die(s_Ripper2Die), sg_Ripper2Dead(s_Ripper2Dead), sg_Ripper2DeathJump(s_Ripper2DeathJump),
		sg_Ripper2DeathFall(s_Ripper2DeathFall);

		private final State[][] group;
		private int index = -1;

		Ripper2StateGroup(State[]... states) {
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

	private static final int RIPPER2_RUN_RATE = 16;

	public static final Animator DoRipper2Move = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoRipper2Move(spr) != 0;
		}
	};

	public static final Animator NullRipper2 = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return NullRipper2(spr) != 0;
		}
	};

	public static final Animator DoRipper2MoveJump = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoRipper2MoveJump(spr) != 0;
		}
	};

	private static final State s_Ripper2Run[][] = {
			{ new State(RIPPER2_RUN_R0 + 0, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[0][1]},
					new State(RIPPER2_RUN_R0 + 1, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[0][2]},
					new State(RIPPER2_RUN_R0 + 2, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[0][3]},
					new State(RIPPER2_RUN_R0 + 3, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[0][0]},
			}, { new State(RIPPER2_RUN_R1 + 0, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[1][1]},
					new State(RIPPER2_RUN_R1 + 1, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[1][2]},
					new State(RIPPER2_RUN_R1 + 2, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[1][3]},
					new State(RIPPER2_RUN_R1 + 3, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[1][0]},
			}, { new State(RIPPER2_RUN_R2 + 0, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[2][1]},
					new State(RIPPER2_RUN_R2 + 1, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[2][2]},
					new State(RIPPER2_RUN_R2 + 2, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[2][3]},
					new State(RIPPER2_RUN_R2 + 3, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[2][0]},
			}, { new State(RIPPER2_RUN_R3 + 0, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[3][1]},
					new State(RIPPER2_RUN_R3 + 1, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[3][2]},
					new State(RIPPER2_RUN_R3 + 2, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[3][3]},
					new State(RIPPER2_RUN_R3 + 3, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[3][0]},
			}, { new State(RIPPER2_RUN_R4 + 0, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[4][1]},
					new State(RIPPER2_RUN_R4 + 1, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[4][2]},
					new State(RIPPER2_RUN_R4 + 2, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[4][3]},
					new State(RIPPER2_RUN_R4 + 3, RIPPER2_RUN_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2Run[4][0]},
			} };

	private static final int RIPPER2_STAND_RATE = 12;

	private static final State s_Ripper2Stand[][] = {
			{ new State(RIPPER2_STAND_R0 + 0, RIPPER2_STAND_RATE, DoRipper2Move).setNext(), // s_Ripper2Stand[0][0]},
			}, { new State(RIPPER2_STAND_R1 + 0, RIPPER2_STAND_RATE, DoRipper2Move).setNext(), // s_Ripper2Stand[1][0]},
			}, { new State(RIPPER2_STAND_R2 + 0, RIPPER2_STAND_RATE, DoRipper2Move).setNext(), // s_Ripper2Stand[2][0]},
			}, { new State(RIPPER2_STAND_R3 + 0, RIPPER2_STAND_RATE, DoRipper2Move).setNext(), // s_Ripper2Stand[3][0]},
			}, { new State(RIPPER2_STAND_R4 + 0, RIPPER2_STAND_RATE, DoRipper2Move).setNext(), // s_Ripper2Stand[4][0]},
			}, };

	private static final int RIPPER2_RUNFAST_RATE = 14;

	private static final State s_Ripper2RunFast[][] = {
			{ new State(RIPPER2_RUNFAST_R0 + 0, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[0][1]},
					new State(RIPPER2_RUNFAST_R0 + 1, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[0][2]},
					new State(RIPPER2_RUNFAST_R0 + 2, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[0][3]},
					new State(RIPPER2_RUNFAST_R0 + 3, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[0][0]},
			}, { new State(RIPPER2_RUNFAST_R1 + 0, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[1][1]},
					new State(RIPPER2_RUNFAST_R1 + 1, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[1][2]},
					new State(RIPPER2_RUNFAST_R1 + 2, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[1][3]},
					new State(RIPPER2_RUNFAST_R1 + 3, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[1][0]},
			}, { new State(RIPPER2_RUNFAST_R2 + 0, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[2][1]},
					new State(RIPPER2_RUNFAST_R2 + 1, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[2][2]},
					new State(RIPPER2_RUNFAST_R2 + 2, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[2][3]},
					new State(RIPPER2_RUNFAST_R2 + 3, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[2][0]},
			}, { new State(RIPPER2_RUNFAST_R3 + 0, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[3][1]},
					new State(RIPPER2_RUNFAST_R3 + 1, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[3][2]},
					new State(RIPPER2_RUNFAST_R3 + 2, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[3][3]},
					new State(RIPPER2_RUNFAST_R3 + 3, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[3][0]},
			}, { new State(RIPPER2_RUNFAST_R4 + 0, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[4][1]},
					new State(RIPPER2_RUNFAST_R4 + 1, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[4][2]},
					new State(RIPPER2_RUNFAST_R4 + 2, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[4][3]},
					new State(RIPPER2_RUNFAST_R4 + 3, RIPPER2_RUNFAST_RATE | SF_TIC_ADJUST, DoRipper2Move), // s_Ripper2RunFast[4][0]},
			} };

	private static final int RIPPER2_JUMP_RATE = 25;

	private static final State s_Ripper2Jump[][] = { { new State(RIPPER2_JUMP_R0 + 0, RIPPER2_JUMP_RATE, NullRipper2), // s_Ripper2Jump[0][1]},
			new State(RIPPER2_JUMP_R0 + 1, RIPPER2_JUMP_RATE, DoRipper2MoveJump).setNext(), // s_Ripper2Jump[0][1]},
			}, { new State(RIPPER2_JUMP_R1 + 0, RIPPER2_JUMP_RATE, NullRipper2), // s_Ripper2Jump[1][1]},
					new State(RIPPER2_JUMP_R1 + 1, RIPPER2_JUMP_RATE, DoRipper2MoveJump).setNext(), // s_Ripper2Jump[1][1]},
			}, { new State(RIPPER2_JUMP_R2 + 0, RIPPER2_JUMP_RATE, NullRipper2), // s_Ripper2Jump[2][1]},
					new State(RIPPER2_JUMP_R2 + 1, RIPPER2_JUMP_RATE, DoRipper2MoveJump).setNext(), // s_Ripper2Jump[2][1]},
			}, { new State(RIPPER2_JUMP_R3 + 0, RIPPER2_JUMP_RATE, NullRipper2), // s_Ripper2Jump[3][1]},
					new State(RIPPER2_JUMP_R3 + 1, RIPPER2_JUMP_RATE, DoRipper2MoveJump).setNext(), // s_Ripper2Jump[3][1]},
			}, { new State(RIPPER2_JUMP_R4 + 0, RIPPER2_JUMP_RATE, NullRipper2), // s_Ripper2Jump[4][1]},
					new State(RIPPER2_JUMP_R4 + 1, RIPPER2_JUMP_RATE, DoRipper2MoveJump).setNext(), // s_Ripper2Jump[4][1]},
			} };

	private static final int RIPPER2_DIE_RATE = 18;

	private static final State s_Ripper2Die[] = { new State(RIPPER2_DIE + 0, RIPPER2_DIE_RATE, NullRipper2), // s_Ripper2Die[1]},
			new State(RIPPER2_DIE + 1, RIPPER2_DIE_RATE, NullRipper2), // s_Ripper2Die[2]},
			new State(RIPPER2_DIE + 2, RIPPER2_DIE_RATE, NullRipper2), // s_Ripper2Die[3]},
			new State(RIPPER2_DIE + 3, RIPPER2_DIE_RATE, NullRipper2), // s_Ripper2Die[4]},
			new State(RIPPER2_DIE + 4, RIPPER2_DIE_RATE, NullRipper2), // s_Ripper2Die[5]},
			new State(RIPPER2_DIE + 5, RIPPER2_DIE_RATE, NullRipper2), // s_Ripper2Die[6]},
			new State(RIPPER2_DIE + 6, RIPPER2_DIE_RATE, NullRipper2), // s_Ripper2Die[7]},
			new State(RIPPER2_DEAD, RIPPER2_DIE_RATE, DoActorDebris).setNext(), // s_Ripper2Die[7]},
	};

//////////////////////
//
//RIPPER2 SWIPE
//
//////////////////////

	public static final Animator InitRipperSlash = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return InitRipperSlash(spr) != 0;
		}
	};

	private static final int RIPPER2_SWIPE_RATE = 14;

	private static final State s_Ripper2Swipe[][] = {
			{ new State(RIPPER2_SWIPE_R0 + 0, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[0][1]},
					new State(RIPPER2_SWIPE_R0 + 1, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[0][2]},
					new State(RIPPER2_SWIPE_R0 + 1, 0 | SF_QUICK_CALL, InitRipperSlash), // s_Ripper2Swipe[0][3]},
					new State(RIPPER2_SWIPE_R0 + 2, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[0][4]},
					new State(RIPPER2_SWIPE_R0 + 3, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[0][5]},
					new State(RIPPER2_SWIPE_R0 + 3, 0 | SF_QUICK_CALL, InitRipperSlash), // s_Ripper2Swipe[0][6]},
					new State(RIPPER2_SWIPE_R0 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_Ripper2Swipe[0][7]},
					new State(RIPPER2_SWIPE_R0 + 3, RIPPER2_SWIPE_RATE, DoRipper2Move).setNext(), // s_Ripper2Swipe[0][7]},
			}, { new State(RIPPER2_SWIPE_R1 + 0, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[1][1]},
					new State(RIPPER2_SWIPE_R1 + 1, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[1][2]},
					new State(RIPPER2_SWIPE_R1 + 1, 0 | SF_QUICK_CALL, InitRipperSlash), // s_Ripper2Swipe[1][3]},
					new State(RIPPER2_SWIPE_R1 + 2, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[1][4]},
					new State(RIPPER2_SWIPE_R1 + 3, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[1][5]},
					new State(RIPPER2_SWIPE_R1 + 3, 0 | SF_QUICK_CALL, InitRipperSlash), // s_Ripper2Swipe[1][6]},
					new State(RIPPER2_SWIPE_R1 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_Ripper2Swipe[1][7]},
					new State(RIPPER2_SWIPE_R1 + 3, RIPPER2_SWIPE_RATE, DoRipper2Move).setNext(), // s_Ripper2Swipe[1][7]},
			}, { new State(RIPPER2_SWIPE_R2 + 0, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[2][1]},
					new State(RIPPER2_SWIPE_R2 + 1, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[2][2]},
					new State(RIPPER2_SWIPE_R2 + 1, 0 | SF_QUICK_CALL, InitRipperSlash), // s_Ripper2Swipe[2][3]},
					new State(RIPPER2_SWIPE_R2 + 2, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[2][4]},
					new State(RIPPER2_SWIPE_R2 + 3, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[2][5]},
					new State(RIPPER2_SWIPE_R2 + 3, 0 | SF_QUICK_CALL, InitRipperSlash), // s_Ripper2Swipe[2][6]},
					new State(RIPPER2_SWIPE_R2 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_Ripper2Swipe[2][7]},
					new State(RIPPER2_SWIPE_R2 + 3, RIPPER2_SWIPE_RATE, DoRipper2Move).setNext(), // s_Ripper2Swipe[2][7]},
			}, { new State(RIPPER2_SWIPE_R3 + 0, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[3][1]},
					new State(RIPPER2_SWIPE_R3 + 1, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[3][2]},
					new State(RIPPER2_SWIPE_R3 + 1, 0 | SF_QUICK_CALL, InitRipperSlash), // s_Ripper2Swipe[3][3]},
					new State(RIPPER2_SWIPE_R3 + 2, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[3][4]},
					new State(RIPPER2_SWIPE_R3 + 3, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[3][5]},
					new State(RIPPER2_SWIPE_R3 + 3, 0 | SF_QUICK_CALL, InitRipperSlash), // s_Ripper2Swipe[3][6]},
					new State(RIPPER2_SWIPE_R3 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_Ripper2Swipe[3][7]},
					new State(RIPPER2_SWIPE_R3 + 3, RIPPER2_SWIPE_RATE, DoRipper2Move).setNext(), // s_Ripper2Swipe[3][7]},
			}, { new State(RIPPER2_SWIPE_R4 + 0, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[4][1]},
					new State(RIPPER2_SWIPE_R4 + 1, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[4][2]},
					new State(RIPPER2_SWIPE_R4 + 1, 0 | SF_QUICK_CALL, InitRipperSlash), // s_Ripper2Swipe[4][3]},
					new State(RIPPER2_SWIPE_R4 + 2, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[4][4]},
					new State(RIPPER2_SWIPE_R4 + 3, RIPPER2_SWIPE_RATE, NullRipper2), // s_Ripper2Swipe[4][5]},
					new State(RIPPER2_SWIPE_R4 + 3, 0 | SF_QUICK_CALL, InitRipperSlash), // s_Ripper2Swipe[4][6]},
					new State(RIPPER2_SWIPE_R4 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_Ripper2Swipe[4][7]},
					new State(RIPPER2_SWIPE_R4 + 3, RIPPER2_SWIPE_RATE, DoRipper2Move).setNext(), // s_Ripper2Swipe[4][7]},
			} };

//////////////////////
//
//RIPPER2 KONG
//
//////////////////////

	private static final int RIPPER2_MEKONG_RATE = 18;

	public static final Animator ChestRipper2 = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return ChestRipper2(spr) != 0;
		}
	};

	private static final State s_Ripper2Kong[][] = {
			{ new State(RIPPER2_MEKONG_R0 + 0, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[0][1]},
					new State(RIPPER2_MEKONG_R0 + 0, SF_QUICK_CALL, ChestRipper2), // s_Ripper2Kong[0][2]},
					new State(RIPPER2_MEKONG_R0 + 1, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[0][3]},
					new State(RIPPER2_MEKONG_R0 + 2, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[0][4]},
					new State(RIPPER2_MEKONG_R0 + 3, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[0][5]},
					new State(RIPPER2_MEKONG_R0 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_Ripper2Kong[0][6]},
					new State(RIPPER2_MEKONG_R0 + 0, RIPPER2_MEKONG_RATE, DoRipper2Move).setNext(), // s_Ripper2Kong[0][6]},
			}, { new State(RIPPER2_MEKONG_R1 + 0, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[1][1]},
					new State(RIPPER2_MEKONG_R0 + 0, SF_QUICK_CALL, ChestRipper2), // s_Ripper2Kong[1][2]},
					new State(RIPPER2_MEKONG_R1 + 1, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[1][3]},
					new State(RIPPER2_MEKONG_R1 + 2, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[1][4]},
					new State(RIPPER2_MEKONG_R1 + 3, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[1][5]},
					new State(RIPPER2_MEKONG_R1 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_Ripper2Kong[1][6]},
					new State(RIPPER2_MEKONG_R1 + 0, RIPPER2_MEKONG_RATE, DoRipper2Move).setNext(), // s_Ripper2Kong[1][6]},
			}, { new State(RIPPER2_MEKONG_R2 + 0, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[2][1]},
					new State(RIPPER2_MEKONG_R0 + 0, SF_QUICK_CALL, ChestRipper2), // s_Ripper2Kong[2][2]},
					new State(RIPPER2_MEKONG_R2 + 1, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[2][3]},
					new State(RIPPER2_MEKONG_R2 + 2, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[2][4]},
					new State(RIPPER2_MEKONG_R2 + 3, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[2][5]},
					new State(RIPPER2_MEKONG_R2 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_Ripper2Kong[2][6]},
					new State(RIPPER2_MEKONG_R2 + 0, RIPPER2_MEKONG_RATE, DoRipper2Move).setNext(), // s_Ripper2Kong[2][6]},
			}, { new State(RIPPER2_MEKONG_R3 + 0, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[3][1]},
					new State(RIPPER2_MEKONG_R0 + 0, SF_QUICK_CALL, ChestRipper2), // s_Ripper2Kong[3][2]},
					new State(RIPPER2_MEKONG_R3 + 1, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[3][3]},
					new State(RIPPER2_MEKONG_R3 + 2, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[3][4]},
					new State(RIPPER2_MEKONG_R3 + 3, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[3][5]},
					new State(RIPPER2_MEKONG_R3 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_Ripper2Kong[3][6]},
					new State(RIPPER2_MEKONG_R3 + 0, RIPPER2_MEKONG_RATE, DoRipper2Move).setNext(), // s_Ripper2Kong[3][6]},
			}, { new State(RIPPER2_MEKONG_R4 + 0, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[4][1]},
					new State(RIPPER2_MEKONG_R0 + 0, SF_QUICK_CALL, ChestRipper2), // s_Ripper2Kong[4][2]},
					new State(RIPPER2_MEKONG_R4 + 1, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[4][3]},
					new State(RIPPER2_MEKONG_R4 + 2, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[4][4]},
					new State(RIPPER2_MEKONG_R4 + 3, RIPPER2_MEKONG_RATE, NullRipper2), // s_Ripper2Kong[4][5]},
					new State(RIPPER2_MEKONG_R4 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_Ripper2Kong[4][6]},
					new State(RIPPER2_MEKONG_R4 + 0, RIPPER2_MEKONG_RATE, DoRipper2Move).setNext(), // s_Ripper2Kong[4][6]},
			} };

//////////////////////
//
//RIPPER2 HEART - show players heart
//
//////////////////////

	private static final int RIPPER2_HEART_RATE = 20;

	public static final Animator DoRipper2StandHeart = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoRipper2StandHeart(spr) != 0;
		}
	};

	private static final State s_Ripper2Heart[][] = {
			{ new State(RIPPER2_HEART_R0 + 0, RIPPER2_HEART_RATE, DoRipper2StandHeart), // s_Ripper2Heart[0][1]},
					new State(RIPPER2_HEART_R0 + 1, RIPPER2_HEART_RATE, DoRipper2StandHeart), // s_Ripper2Heart[0][0]},
			}, { new State(RIPPER2_HEART_R1 + 0, RIPPER2_HEART_RATE, DoRipper2StandHeart), // s_Ripper2Heart[1][1]},
					new State(RIPPER2_HEART_R1 + 1, RIPPER2_HEART_RATE, DoRipper2StandHeart), // s_Ripper2Heart[1][0]},
			}, { new State(RIPPER2_HEART_R2 + 0, RIPPER2_HEART_RATE, DoRipper2StandHeart), // s_Ripper2Heart[2][1]},
					new State(RIPPER2_HEART_R2 + 1, RIPPER2_HEART_RATE, DoRipper2StandHeart), // s_Ripper2Heart[2][0]},
			}, { new State(RIPPER2_HEART_R3 + 0, RIPPER2_HEART_RATE, DoRipper2StandHeart), // s_Ripper2Heart[3][1]},
					new State(RIPPER2_HEART_R3 + 1, RIPPER2_HEART_RATE, DoRipper2StandHeart), // s_Ripper2Heart[3][0]},
			}, { new State(RIPPER2_HEART_R4 + 0, RIPPER2_HEART_RATE, DoRipper2StandHeart), // s_Ripper2Heart[4][1]},
					new State(RIPPER2_HEART_R4 + 1, RIPPER2_HEART_RATE, DoRipper2StandHeart), // s_Ripper2Heart[4][0]},
			} };

//////////////////////
//
//RIPPER2 HANG
//
//////////////////////

	private static final int RIPPER2_HANG_RATE = 14;

	public static final Animator DoRipper2Hang = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoRipper2Hang(spr) != 0;
		}
	};

	private static final State s_Ripper2Hang[][] = {
			{ new State(RIPPER2_HANG_R0, RIPPER2_HANG_RATE, DoRipper2Hang).setNext(), // s_Ripper2Hang[0][0]},
			}, { new State(RIPPER2_HANG_R1, RIPPER2_HANG_RATE, DoRipper2Hang).setNext(), // s_Ripper2Hang[1][0]},
			}, { new State(RIPPER2_HANG_R2, RIPPER2_HANG_RATE, DoRipper2Hang).setNext(), // s_Ripper2Hang[2][0]},
			}, { new State(RIPPER2_HANG_R3, RIPPER2_HANG_RATE, DoRipper2Hang).setNext(), // s_Ripper2Hang[3][0]},
			}, { new State(RIPPER2_HANG_R4, RIPPER2_HANG_RATE, DoRipper2Hang).setNext(), // s_Ripper2Hang[4][0]},
			} };

//////////////////////
//
//RIPPER2 PAIN
//
//////////////////////

	private static final int RIPPER2_PAIN_RATE = 38;

	public static final Animator DoRipper2Pain = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoRipper2Pain(spr) != 0;
		}
	};

	private static final State s_Ripper2Pain[][] = { { new State(4414 + 0, RIPPER2_PAIN_RATE, DoRipper2Pain).setNext(), // s_Ripper2Pain[0][0]},
			}, { new State(4414 + 0, RIPPER2_PAIN_RATE, DoRipper2Pain).setNext(), // s_Ripper2Pain[1][0]},
			}, { new State(4414 + 0, RIPPER2_PAIN_RATE, DoRipper2Pain).setNext(), // s_Ripper2Pain[2][0]},
			}, { new State(4414 + 0, RIPPER2_PAIN_RATE, DoRipper2Pain).setNext(), // s_Ripper2Pain[3][0]},
			}, { new State(4414 + 0, RIPPER2_PAIN_RATE, DoRipper2Pain).setNext(), // s_Ripper2Pain[4][0]},
			} };

//////////////////////
//
//RIPPER2 FALL
//
//////////////////////

	private static final int RIPPER2_FALL_RATE = 25;

	private static final State s_Ripper2Fall[][] = {
			{ new State(RIPPER2_FALL_R0 + 0, RIPPER2_FALL_RATE, DoRipper2MoveJump).setNext(), // s_Ripper2Fall[0][0]},
			}, { new State(RIPPER2_FALL_R1 + 0, RIPPER2_FALL_RATE, DoRipper2MoveJump).setNext(), // s_Ripper2Fall[1][0]},
			}, { new State(RIPPER2_FALL_R2 + 0, RIPPER2_FALL_RATE, DoRipper2MoveJump).setNext(), // s_Ripper2Fall[2][0]},
			}, { new State(RIPPER2_FALL_R3 + 0, RIPPER2_FALL_RATE, DoRipper2MoveJump).setNext(), // s_Ripper2Fall[3][0]},
			}, { new State(RIPPER2_FALL_R4 + 0, RIPPER2_FALL_RATE, DoRipper2MoveJump).setNext(), // s_Ripper2Fall[4][0]},
			} };

//////////////////////
//
//RIPPER2 JUMP ATTACK
//
//////////////////////

	private static final int RIPPER2_JUMP_ATTACK_RATE = 35;

	public static final Animator DoRipper2BeginJumpAttack = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoRipper2BeginJumpAttack(spr) != 0;
		}
	};

	private static final State s_Ripper2JumpAttack[][] = {
			{ new State(RIPPER2_JUMP_R0 + 0, RIPPER2_JUMP_ATTACK_RATE, NullRipper2), // s_Ripper2JumpAttack[0][1]},
					new State(RIPPER2_JUMP_R0 + 0, 0 | SF_QUICK_CALL, DoRipper2BeginJumpAttack), // s_Ripper2JumpAttack[0][2]},
					new State(RIPPER2_JUMP_R0 + 2, RIPPER2_JUMP_ATTACK_RATE, DoRipper2MoveJump), // s_Ripper2JumpAttack[0][3]},
					new State(RIPPER2_JUMP_R0 + 1, RIPPER2_JUMP_ATTACK_RATE, DoRipper2MoveJump).setNext(), // s_Ripper2JumpAttack[0][3]},
			}, { new State(RIPPER2_JUMP_R1 + 0, RIPPER2_JUMP_ATTACK_RATE, NullRipper2), // s_Ripper2JumpAttack[1][1]},
					new State(RIPPER2_JUMP_R1 + 0, 0 | SF_QUICK_CALL, DoRipper2BeginJumpAttack), // s_Ripper2JumpAttack[1][2]},
					new State(RIPPER2_JUMP_R1 + 2, RIPPER2_JUMP_ATTACK_RATE, DoRipper2MoveJump), // s_Ripper2JumpAttack[1][3]},
					new State(RIPPER2_JUMP_R1 + 1, RIPPER2_JUMP_ATTACK_RATE, DoRipper2MoveJump).setNext(), // s_Ripper2JumpAttack[1][3]},
			}, { new State(RIPPER2_JUMP_R2 + 0, RIPPER2_JUMP_ATTACK_RATE, NullRipper2), // s_Ripper2JumpAttack[2][1]},
					new State(RIPPER2_JUMP_R2 + 0, 0 | SF_QUICK_CALL, DoRipper2BeginJumpAttack), // s_Ripper2JumpAttack[2][2]},
					new State(RIPPER2_JUMP_R2 + 2, RIPPER2_JUMP_ATTACK_RATE, DoRipper2MoveJump), // s_Ripper2JumpAttack[2][3]},
					new State(RIPPER2_JUMP_R1 + 1, RIPPER2_JUMP_ATTACK_RATE, DoRipper2MoveJump).setNext(), // s_Ripper2JumpAttack[2][3]},
			}, { new State(RIPPER2_JUMP_R3 + 0, RIPPER2_JUMP_ATTACK_RATE, NullRipper2), // s_Ripper2JumpAttack[3][1]},
					new State(RIPPER2_JUMP_R3 + 0, 0 | SF_QUICK_CALL, DoRipper2BeginJumpAttack), // s_Ripper2JumpAttack[3][2]},
					new State(RIPPER2_JUMP_R3 + 2, RIPPER2_JUMP_ATTACK_RATE, DoRipper2MoveJump), // s_Ripper2JumpAttack[3][3]},
					new State(RIPPER2_JUMP_R1 + 1, RIPPER2_JUMP_ATTACK_RATE, DoRipper2MoveJump).setNext(), // s_Ripper2JumpAttack[3][3]},
			}, { new State(RIPPER2_JUMP_R4 + 0, RIPPER2_JUMP_ATTACK_RATE, NullRipper2), // s_Ripper2JumpAttack[4][1]},
					new State(RIPPER2_JUMP_R4 + 0, 0 | SF_QUICK_CALL, DoRipper2BeginJumpAttack), // s_Ripper2JumpAttack[4][2]},
					new State(RIPPER2_JUMP_R4 + 2, RIPPER2_JUMP_ATTACK_RATE, DoRipper2MoveJump), // s_Ripper2JumpAttack[4][3]},
					new State(RIPPER2_JUMP_R1 + 1, RIPPER2_JUMP_ATTACK_RATE, DoRipper2MoveJump).setNext(), // s_Ripper2JumpAttack[4][3]},
			} };

//////////////////////
//
//RIPPER2 HANG_JUMP
//
//////////////////////

	public static final Animator DoRipper2HangJF = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoRipper2HangJF(spr) != 0;
		}
	};

	private static final int RIPPER2_HANG_JUMP_RATE = 20;

	private static final State s_Ripper2HangJump[][] = {
			{ new State(RIPPER2_JUMP_R0 + 0, RIPPER2_HANG_JUMP_RATE, NullRipper2), // s_Ripper2HangJump[0][1]},
					new State(RIPPER2_JUMP_R0 + 1, RIPPER2_HANG_JUMP_RATE, DoRipper2HangJF).setNext(), // s_Ripper2HangJump[0][1]},
			}, { new State(RIPPER2_JUMP_R1 + 0, RIPPER2_HANG_JUMP_RATE, NullRipper2), // s_Ripper2HangJump[1][1]},
					new State(RIPPER2_JUMP_R1 + 1, RIPPER2_HANG_JUMP_RATE, DoRipper2HangJF).setNext(), // s_Ripper2HangJump[1][1]},
			}, { new State(RIPPER2_JUMP_R2 + 0, RIPPER2_HANG_JUMP_RATE, NullRipper2), // s_Ripper2HangJump[2][1]},
					new State(RIPPER2_JUMP_R2 + 1, RIPPER2_HANG_JUMP_RATE, DoRipper2HangJF).setNext(), // s_Ripper2HangJump[2][1]},
			}, { new State(RIPPER2_JUMP_R3 + 0, RIPPER2_HANG_JUMP_RATE, NullRipper2), // s_Ripper2HangJump[3][1]},
					new State(RIPPER2_JUMP_R3 + 1, RIPPER2_HANG_JUMP_RATE, DoRipper2HangJF).setNext(), // s_Ripper2HangJump[3][1]},
			}, { new State(RIPPER2_JUMP_R4 + 0, RIPPER2_HANG_JUMP_RATE, NullRipper2), // s_Ripper2HangJump[4][1]},
					new State(RIPPER2_JUMP_R4 + 1, RIPPER2_HANG_JUMP_RATE, DoRipper2HangJF).setNext(), // s_Ripper2HangJump[4][1]},
			} };

//////////////////////
//
//RIPPER2 HANG_FALL
//
//////////////////////

	private static final State s_Ripper2HangFall[][] = {
			{ new State(RIPPER2_FALL_R0 + 0, RIPPER2_FALL_RATE, DoRipper2HangJF).setNext(), // s_Ripper2HangFall[0][0]},
			}, { new State(RIPPER2_FALL_R1 + 0, RIPPER2_FALL_RATE, DoRipper2HangJF).setNext(), // s_Ripper2HangFall[1][0]},
			}, { new State(RIPPER2_FALL_R2 + 0, RIPPER2_FALL_RATE, DoRipper2HangJF).setNext(), // s_Ripper2HangFall[2][0]},
			}, { new State(RIPPER2_FALL_R3 + 0, RIPPER2_FALL_RATE, DoRipper2HangJF).setNext(), // s_Ripper2HangFall[3][0]},
			}, { new State(RIPPER2_FALL_R4 + 0, RIPPER2_FALL_RATE, DoRipper2HangJF).setNext(), // s_Ripper2HangFall[4][0]},
			} };

	private static final State s_Ripper2DeathJump[] = {
			new State(RIPPER2_DIE + 0, RIPPER2_DIE_RATE, DoActorDeathMove).setNext(), // s_Ripper2DeathJump[0]}
	};

	private static final State s_Ripper2DeathFall[] = {
			new State(RIPPER2_DIE + 1, RIPPER2_DIE_RATE, DoActorDeathMove).setNext(), // s_Ripper2DeathFall[0]}
	};

	private static final int RIPPER2_DEAD_RATE = 8;

	private static final State s_Ripper2Dead[] = { new State(RIPPER2_DIE + 0, RIPPER2_DIE_RATE, NullRipper2), // &s_Ripper2Dead[1]},
			new State(RIPPER2_DIE + 1, RIPPER2_DIE_RATE, NullRipper2), // &s_Ripper2Dead[2]},
			new State(RIPPER2_DIE + 2, RIPPER2_DIE_RATE, NullRipper2), // &s_Ripper2Dead[3]},
			new State(RIPPER2_DIE + 3, RIPPER2_DIE_RATE, NullRipper2), // &s_Ripper2Dead[4]},
			new State(RIPPER2_DIE + 4, RIPPER2_DIE_RATE, NullRipper2), // &s_Ripper2Dead[5]},
			new State(RIPPER2_DIE + 5, RIPPER2_DIE_RATE, NullRipper2), // &s_Ripper2Dead[6]},
			new State(RIPPER2_DIE + 6, RIPPER2_DIE_RATE, NullRipper2), // &s_Ripper2Dead[7]},
			new State(RIPPER2_DEAD, SF_QUICK_CALL, QueueFloorBlood), // &s_Ripper2Dead[8]},
			new State(RIPPER2_DEAD, RIPPER2_DEAD_RATE, DoActorDebris).setNext(), // &s_Ripper2Dead[8]},
	};

	private static final Actor_Action_Set Ripper2ActionSet = new Actor_Action_Set(Ripper2StateGroup.sg_Ripper2Stand,
			Ripper2StateGroup.sg_Ripper2Run, Ripper2StateGroup.sg_Ripper2Jump, Ripper2StateGroup.sg_Ripper2Fall, null, // sg_Ripper2Crawl,
			null, // sg_Ripper2Swim,
			null, // sg_Ripper2Fly,
			null, // sg_Ripper2Rise,
			null, // sg_Ripper2Sit,
			null, // sg_Ripper2Look,
			null, // climb
			Ripper2StateGroup.sg_Ripper2Pain, Ripper2StateGroup.sg_Ripper2Die, null, // sg_Ripper2HariKari,
			Ripper2StateGroup.sg_Ripper2Dead, Ripper2StateGroup.sg_Ripper2DeathJump,
			Ripper2StateGroup.sg_Ripper2DeathFall, new StateGroup[] { Ripper2StateGroup.sg_Ripper2Swipe },
			new short[] { 1024 },
			new StateGroup[] { Ripper2StateGroup.sg_Ripper2JumpAttack, Ripper2StateGroup.sg_Ripper2Kong },
			new short[] { 500, 1024 },
			new StateGroup[] { Ripper2StateGroup.sg_Ripper2Heart, Ripper2StateGroup.sg_Ripper2Hang }, null, null);

	private static final Actor_Action_Set Ripper2BrownActionSet = new Actor_Action_Set(
			Ripper2StateGroup.sg_Ripper2Stand, Ripper2StateGroup.sg_Ripper2Run, Ripper2StateGroup.sg_Ripper2Jump,
			Ripper2StateGroup.sg_Ripper2Fall, null, // Ripper2StateGroup.sg_Ripper2Crawl,
			null, // Ripper2StateGroup.sg_Ripper2Swim,
			null, // Ripper2StateGroup.sg_Ripper2Fly,
			null, // Ripper2StateGroup.sg_Ripper2Rise,
			null, // Ripper2StateGroup.sg_Ripper2Sit,
			null, // Ripper2StateGroup.sg_Ripper2Look,
			null, // climb
			Ripper2StateGroup.sg_Ripper2Pain, // pain
			Ripper2StateGroup.sg_Ripper2Die, null, // Ripper2StateGroup.sg_Ripper2HariKari,
			Ripper2StateGroup.sg_Ripper2Dead, Ripper2StateGroup.sg_Ripper2DeathJump,
			Ripper2StateGroup.sg_Ripper2DeathFall, new StateGroup[] { Ripper2StateGroup.sg_Ripper2Swipe },
			new short[] { 1024 },
			new StateGroup[] { Ripper2StateGroup.sg_Ripper2JumpAttack, Ripper2StateGroup.sg_Ripper2Kong },
			new short[] { 400, 1024 },
			new StateGroup[] { Ripper2StateGroup.sg_Ripper2Heart, Ripper2StateGroup.sg_Ripper2Hang }, null, null);

	public static void InitRipper2States() {
		for (Ripper2StateGroup sg : Ripper2StateGroup.values()) {
			for (int rot = 0; rot < sg.group.length; rot++) {
				State.InitState(sg.group[rot]);
			}
		}
	}

	public static void SetupRipper2(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, RIPPER2_RUN_R0, s_Ripper2Run[0][0]);
			u.Health = HEALTH_RIPPER2;
		}

		ChangeState(SpriteNum, s_Ripper2Run[0][0]);
		u.Attrib = Ripper2Attrib;
		DoActorSetSpeed(SpriteNum, NORM_SPEED);
		u.StateEnd = s_Ripper2Die[0];
		u.Rot = Ripper2StateGroup.sg_Ripper2Run;
		sp.clipdist = 512 >> 2; // This actor is bigger, needs bigger box.
		sp.xrepeat = sp.yrepeat = 55;

		if (sp.pal == PALETTE_BROWN_RIPPER) {
			EnemyDefaults(SpriteNum, Ripper2BrownActionSet, Ripper2Personality);
			sp.xrepeat += 40;
			sp.yrepeat += 40;

			if (!TEST(sp.cstat, CSTAT_SPRITE_RESTORE))
				u.Health = HEALTH_MOMMA_RIPPER;

			sp.clipdist += 128 >> 2;
		} else {
			EnemyDefaults(SpriteNum, Ripper2ActionSet, Ripper2Personality);
		}

		u.Flags |= (SPR_XFLIP_TOGGLE);
	}

	//
	// HANGING - Jumping/Falling/Stationary
	//

	public static int InitRipper2Hang(int SpriteNum) {
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

		NewStateGroup(SpriteNum, Ripper2StateGroup.sg_Ripper2HangJump);
		u.StateFallOverride = Ripper2StateGroup.sg_Ripper2HangFall;
		DoActorSetSpeed(SpriteNum, FAST_SPEED);

		PickJumpMaxSpeed(SpriteNum, -(RANDOM_RANGE(400) + 100));

		u.Flags |= (SPR_JUMPING);
		u.Flags &= ~(SPR_FALLING);

		// set up individual actor jump gravity
		u.jump_grav = 8;

		DoJump(SpriteNum);

		return (0);
	}

	public static int DoRipper2Hang(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if ((u.WaitTics -= ACTORMOVETICS) > 0)
			return (0);

		NewStateGroup(SpriteNum, Ripper2StateGroup.sg_Ripper2JumpAttack);
		// move to the 2nd frame - past the pause frame
		u.Tics += u.State.Tics;

		return (0);
	}

	public static int DoRipper2MoveHang(int SpriteNum) {
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

				// Don't keep clinging and going ever higher!
				if (klabs(sp.z - sprite[u.tgt_sp].z) > (4000 << 4))
					break;

				hitwall = NORM_WALL(u.ret);

				NewStateGroup(SpriteNum, u.ActorActionSet.Special[1]); //sg_Ripper2Hang
				if (RANDOM_P2(1024 << 8) >> 8 > 500)
					u.WaitTics = (short) ((RANDOM_P2(2 << 8) >> 8) * 120);
				else
					u.WaitTics = 0; // Double jump

				// hang flush with the wall
				w = hitwall;
				nw = wall[w].point2;
				sp.ang = NORM_ANGLE(engine.getangle(wall[nw].x - wall[w].x, wall[nw].y - wall[w].y) - 512);
				return (0);
			}
		}

		return (0);
	}

	public static int DoRipper2HangJF(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			if (TEST(u.Flags, SPR_JUMPING))
				DoJump(SpriteNum);
			else
				DoFall(SpriteNum);
		}

		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			if (DoRipper2QuickJump(SpriteNum) != 0)
				return (0);

			InitActorDecide(SpriteNum);
		}

		DoRipper2MoveHang(SpriteNum);

		return (0);

	}

	//
	// JUMP ATTACK
	//

	private static final int RANDOM_NEG(int x) {
		return (RANDOM_P2((x) << 1) - (x));
	}

	public static int DoRipper2BeginJumpAttack(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		SPRITE psp = sprite[pUser[SpriteNum].tgt_sp];
		short tang;

		tang = engine.getangle(psp.x - sp.x, psp.y - sp.y);

		if (move_sprite(SpriteNum, sintable[NORM_ANGLE(tang + 512)] >> 7, sintable[tang] >> 7, 0, u.ceiling_dist,
				u.floor_dist, CLIPMASK_ACTOR, ACTORMOVETICS) != 0)
			sp.ang = NORM_ANGLE((sp.ang + 1024) + (RANDOM_NEG(256 << 6) >> 6));
		else sp.ang = NORM_ANGLE(tang);
			
		DoActorSetSpeed(SpriteNum, FAST_SPEED);

		PickJumpMaxSpeed(SpriteNum, -(RANDOM_RANGE(400) + 100));

		u.Flags |= (SPR_JUMPING);
		u.Flags &= ~(SPR_FALLING);

		// set up individual actor jump gravity
		u.jump_grav = 8;

		// if I didn't do this here they get stuck in the air sometimes
		DoActorZrange(SpriteNum);

		DoJump(SpriteNum);

		return (0);
	}

	public static int DoRipper2MoveJump(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			if (TEST(u.Flags, SPR_JUMPING))
				DoJump(SpriteNum);
			else
				DoFall(SpriteNum);
		}

		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			if (DoRipper2QuickJump(SpriteNum) != 0)
				return (0);

			InitActorDecide(SpriteNum);
		}

		DoRipper2MoveHang(SpriteNum);
		return (0);
	}

	//
	// STD MOVEMENT
	//

	public static int DoRipper2QuickJump(int SpriteNum) {
		USER u = pUser[SpriteNum];

		// Tests to see if ripper2 is on top of a player/enemy and then immediatly
		// does another jump

		if (u.lo_sp != -1) {
			SPRITE tsp = sprite[u.lo_sp];

			if (TEST(tsp.extra, SPRX_PLAYER_OR_ENEMY)) {
				NewStateGroup(SpriteNum, Ripper2StateGroup.sg_Ripper2JumpAttack);
				// move past the first state
				u.Tics = 30;
				return (TRUE);
			}
		}

		return (FALSE);
	}

	public static int NullRipper2(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		DoActorSectorDamage(SpriteNum);

		return (0);
	}

	public static int DoRipper2Pain(int SpriteNum) {
		USER u = pUser[SpriteNum];

		NullRipper2(SpriteNum);

		if ((u.WaitTics -= ACTORMOVETICS) <= 0)
			InitActorDecide(SpriteNum);
		return (0);
	}

	public static int DoRipper2RipHeart(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		SPRITE tsp = sprite[u.tgt_sp];

		NewStateGroup(SpriteNum, Ripper2StateGroup.sg_Ripper2Heart);
		u.WaitTics = 6 * 120;

		// player face ripper2
		tsp.ang = engine.getangle(sp.x - tsp.x, sp.y - tsp.y);
		return (0);
	}

	private static VOC3D riphearthandle = null;

	private static int DoRipper2StandHeart(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		NullRipper2(SpriteNum);

		if (riphearthandle != null && !riphearthandle.isActive())
			riphearthandle = PlaySound(DIGI_RIPPER2HEARTOUT, sp, v3df_none);

		if ((u.WaitTics -= ACTORMOVETICS) <= 0)
			NewStateGroup(SpriteNum, Ripper2StateGroup.sg_Ripper2Run);
		return (0);
	}

	public static void Ripper2Hatch(int Weapon) {
		SPRITE wp = sprite[Weapon];
		SPRITE np;
		USER nu;

		short rip_ang = (short) RANDOM_P2(2048);

		int newsp = COVERinsertsprite(wp.sectnum, STAT_DEFAULT);
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
		np.shade = -10;
		SetupRipper2(newsp);
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

	public static int DoRipper2Move(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if (sp.hitag == TAG_SWARMSPOT && sp.lotag == 1)
			DoCheckSwarm(SpriteNum);

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
			if (DoRipper2QuickJump(SpriteNum) != 0)
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

	public static int InitRipper2Charge(int SpriteNum) {
		DoActorSetSpeed(SpriteNum, FAST_SPEED);

		InitActorMoveCloser.invoke(SpriteNum);

		NewStateGroup(SpriteNum, Ripper2StateGroup.sg_Ripper2RunFast);

		return (0);
	}

	public static int ChestRipper2(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];

		PlaySound(DIGI_RIPPER2CHEST, sp, v3df_follow);

		return 0;
	}
	
	public static void Ripper2Saveable()
	{
		SaveData(InitRipperSlash);
		SaveData(InitRipper2Hang);
		SaveData(DoRipper2Hang);
		SaveData(DoRipper2HangJF);

		SaveData(DoRipper2BeginJumpAttack);
		SaveData(DoRipper2MoveJump);
		SaveData(NullRipper2);
		SaveData(DoRipper2Pain);
		SaveData(DoRipper2StandHeart);
		SaveData(DoRipper2Move);
		SaveData(InitRipper2Charge);
		SaveData(ChestRipper2);

		SaveData(Ripper2Personality);

		SaveData(Ripper2Attrib);

		SaveData(s_Ripper2Run);
		SaveGroup(Ripper2StateGroup.sg_Ripper2Run);
		SaveData(s_Ripper2RunFast);
		SaveGroup(Ripper2StateGroup.sg_Ripper2RunFast);
		SaveData(s_Ripper2Stand);
		SaveGroup(Ripper2StateGroup.sg_Ripper2Stand);
		SaveData(s_Ripper2Swipe);
		SaveGroup(Ripper2StateGroup.sg_Ripper2Swipe);
		SaveData(s_Ripper2Kong);
		SaveGroup(Ripper2StateGroup.sg_Ripper2Kong);
		SaveData(s_Ripper2Heart);
		SaveGroup(Ripper2StateGroup.sg_Ripper2Heart);
		SaveData(s_Ripper2Hang);
		SaveGroup(Ripper2StateGroup.sg_Ripper2Hang);
		SaveData(s_Ripper2Pain);
		SaveGroup(Ripper2StateGroup.sg_Ripper2Pain);
		SaveData(s_Ripper2Jump);
		SaveGroup(Ripper2StateGroup.sg_Ripper2Jump);
		SaveData(s_Ripper2Fall);
		SaveGroup(Ripper2StateGroup.sg_Ripper2Fall);
		SaveData(s_Ripper2JumpAttack);
		SaveGroup(Ripper2StateGroup.sg_Ripper2JumpAttack);
		SaveData(s_Ripper2HangJump);
		SaveGroup(Ripper2StateGroup.sg_Ripper2HangJump);
		SaveData(s_Ripper2HangFall);
		SaveGroup(Ripper2StateGroup.sg_Ripper2HangFall);
		SaveData(s_Ripper2Die);
		SaveData(s_Ripper2Dead);
		SaveGroup(Ripper2StateGroup.sg_Ripper2Die);
		SaveGroup(Ripper2StateGroup.sg_Ripper2Dead);
		SaveData(s_Ripper2DeathJump);
		SaveData(s_Ripper2DeathFall);
		SaveGroup(Ripper2StateGroup.sg_Ripper2DeathJump);
		SaveGroup(Ripper2StateGroup.sg_Ripper2DeathFall);

		SaveData(Ripper2ActionSet);
		SaveData(Ripper2BrownActionSet);
	}
}
