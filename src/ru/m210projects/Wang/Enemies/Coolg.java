package ru.m210projects.Wang.Enemies;

import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Wang.Actor.DoActorDebris;
import static ru.m210projects.Wang.Actor.DoActorSectorDamage;
import static ru.m210projects.Wang.Actor.DoActorSlide;
import static ru.m210projects.Wang.Actor.DoBeginFall;
import static ru.m210projects.Wang.Actor.DoFall;
import static ru.m210projects.Wang.Ai.DoActorSetSpeed;
import static ru.m210projects.Wang.Ai.FAST_SPEED;
import static ru.m210projects.Wang.Ai.InitActorAmbientNoise;
import static ru.m210projects.Wang.Ai.InitActorAttack;
import static ru.m210projects.Wang.Ai.InitActorDecide;
import static ru.m210projects.Wang.Ai.InitActorFindPlayer;
import static ru.m210projects.Wang.Ai.InitActorMoveCloser;
import static ru.m210projects.Wang.Ai.InitActorReposition;
import static ru.m210projects.Wang.Ai.InitActorRunAway;
import static ru.m210projects.Wang.Ai.InitActorWanderAround;
import static ru.m210projects.Wang.Ai.NORM_SPEED;
import static ru.m210projects.Wang.Digi.DIGI_CGALERT;
import static ru.m210projects.Wang.Digi.DIGI_CGAMBIENT;
import static ru.m210projects.Wang.Digi.DIGI_CGMAGIC;
import static ru.m210projects.Wang.Digi.DIGI_CGMAGICHIT;
import static ru.m210projects.Wang.Digi.DIGI_CGMATERIALIZE;
import static ru.m210projects.Wang.Digi.DIGI_CGPAIN;
import static ru.m210projects.Wang.Digi.DIGI_CGSCREAM;
import static ru.m210projects.Wang.Digi.DIGI_CGTHIGHBONE;
import static ru.m210projects.Wang.Digi.DIGI_VOID3;
import static ru.m210projects.Wang.Enemies.Coolie.EnemyDefaults;
import static ru.m210projects.Wang.Enemies.Ripper.InitCoolgFire;
import static ru.m210projects.Wang.Game.TotalKillable;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_MISSILE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK_HITSCAN;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_INVISIBLE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_RESTORE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_TRANSLUCENT;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_YFLIP;
import static ru.m210projects.Wang.Gameutils.HEALTH_COOLIE_GHOST;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.SEC;
import static ru.m210projects.Wang.Gameutils.SF_QUICK_CALL;
import static ru.m210projects.Wang.Gameutils.SPRITEp_MID;
import static ru.m210projects.Wang.Gameutils.SPRX_PLAYER_OR_ENEMY;
import static ru.m210projects.Wang.Gameutils.SPR_FALLING;
import static ru.m210projects.Wang.Gameutils.SPR_NO_SCAREDZ;
import static ru.m210projects.Wang.Gameutils.SPR_SLIDING;
import static ru.m210projects.Wang.Gameutils.SPR_XFLIP_TOGGLE;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.JWeapon.InitBloodSpray;
import static ru.m210projects.Wang.Names.COOLG_FIRE_R0;
import static ru.m210projects.Wang.Names.COOLG_FIRE_R1;
import static ru.m210projects.Wang.Names.COOLG_FIRE_R2;
import static ru.m210projects.Wang.Names.COOLG_PAIN_R0;
import static ru.m210projects.Wang.Names.COOLG_RUN_R0;
import static ru.m210projects.Wang.Names.COOLG_RUN_R1;
import static ru.m210projects.Wang.Names.COOLG_RUN_R2;
import static ru.m210projects.Wang.Names.COOLG_RUN_R3;
import static ru.m210projects.Wang.Names.COOLG_RUN_R4;
import static ru.m210projects.Wang.Names.STAT_ENEMY;
import static ru.m210projects.Wang.Player.QueueFloorBlood;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.NewStateGroup;
import static ru.m210projects.Wang.Sprites.SpawnSprite;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Sprites.move_actor;
import static ru.m210projects.Wang.Sprites.move_sprite;
import static ru.m210projects.Wang.Track.ActorFollowTrack;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Type.Saveable.SaveGroup;
import static ru.m210projects.Wang.Weapon.DoFindGroundPoint;
import static ru.m210projects.Wang.Weapon.InitCoolgDrip;
import static ru.m210projects.Wang.Weapon.SetSuicide;
import static ru.m210projects.Wang.Weapon.UpdateSinglePlayKills;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Type.ATTRIBUTE;
import ru.m210projects.Wang.Type.Actor_Action_Set;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;

public class Coolg {

	public enum CoolgStateGroup implements StateGroup {
		sg_CoolgStand(s_CoolgStand[0], s_CoolgStand[1], s_CoolgStand[2], s_CoolgStand[3], s_CoolgStand[4]),
		sg_CoolgRun(s_CoolgRun[0], s_CoolgRun[1], s_CoolgRun[2], s_CoolgRun[3], s_CoolgRun[4]),
		sg_CoolgPain(s_CoolgPain[0], s_CoolgPain[1], s_CoolgPain[2], s_CoolgPain[3], s_CoolgPain[4]),
		sg_CoolgDie(s_CoolgDie), sg_CoolgDead(s_CoolgDead),
		sg_CoolgAttack(s_CoolgAttack[0], s_CoolgAttack[1], s_CoolgAttack[2], s_CoolgAttack[3], s_CoolgAttack[4]);

		private final State[][] group;
		private int index = -1;

		CoolgStateGroup(State[]... states) {
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

	public static void InitCoolgStates() {
		for (CoolgStateGroup sg : CoolgStateGroup.values()) {
			for (int rot = 0; rot < sg.group.length; rot++) {
				State.InitState(sg.group[rot]);
			}
		}
		
		State.InitState(s_CoolgBirth);
	}

	private static final Animator InitCoolgCircle = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitCoolgCircle(SpriteNum) != 0;
		}
	};

	private static final Decision CoolgBattle[] = { new Decision(50, InitCoolgCircle),
			new Decision(450, InitActorMoveCloser),
			// new Decision(456, InitActorAmbientNoise ),
			// new Decision(760, InitActorRunAway ),
			new Decision(1024, InitActorAttack) };

	private static final Decision CoolgOffense[] = { new Decision(449, InitActorMoveCloser),
			// new Decision(554, InitActorAmbientNoise ),
			new Decision(1024, InitActorAttack) };

	private static final Decision CoolgBroadcast[] = {
			// new Decision(1, InitActorAlertNoise ),
			new Decision(1, InitActorAmbientNoise), new Decision(1024, InitActorDecide) };

	private static final Decision CoolgSurprised[] = { new Decision(100, InitCoolgCircle),
			new Decision(701, InitActorMoveCloser), new Decision(1024, InitActorDecide) };

	private static final Decision CoolgEvasive[] = { new Decision(20, InitCoolgCircle),
			new Decision(1024, InitActorRunAway), };

	private static final Decision CoolgLostTarget[] = { new Decision(900, InitActorFindPlayer),
			new Decision(1024, InitActorWanderAround) };

	private static final Decision CoolgCloseRange[] = { new Decision(800, InitActorAttack),
			new Decision(1024, InitActorReposition) };

	private static final Decision CoolgTouchTarget[] = {
			// new Decision(50, InitCoolgCircle ),
			new Decision(1024, InitActorAttack), };

	private static final Personality CoolgPersonality = new Personality(CoolgBattle, CoolgOffense, CoolgBroadcast,
			CoolgSurprised, CoolgEvasive, CoolgLostTarget, CoolgCloseRange, CoolgTouchTarget);

	private static final ATTRIBUTE CoolgAttrib = new ATTRIBUTE(new short[] { 60, 80, 150, 190 }, // Speeds
			new short[] { 3, 0, -2, -3 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { DIGI_CGAMBIENT, DIGI_CGALERT, 0, DIGI_CGPAIN, DIGI_CGSCREAM, DIGI_CGMATERIALIZE,
					DIGI_CGTHIGHBONE, DIGI_CGMAGIC, DIGI_CGMAGICHIT, 0 });

	//////////////////////
	//
	// COOLG RUN
	//////////////////////

	public static final int COOLG_RUN_RATE = 40;

	private static final Animator DoCoolgMove = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoCoolgMove(SpriteNum) != 0;
		}
	};

	private static final State s_CoolgRun[][] = { { new State(COOLG_RUN_R0 + 0, COOLG_RUN_RATE, DoCoolgMove), // s_CoolgRun[0][1]},
			new State(COOLG_RUN_R0 + 1, COOLG_RUN_RATE, DoCoolgMove), // s_CoolgRun[0][2]},
			new State(COOLG_RUN_R0 + 2, COOLG_RUN_RATE, DoCoolgMove), // s_CoolgRun[0][3]},
			new State(COOLG_RUN_R0 + 3, COOLG_RUN_RATE, DoCoolgMove),// s_CoolgRun[0][0]},
			}, { new State(COOLG_RUN_R1 + 0, COOLG_RUN_RATE, DoCoolgMove), // s_CoolgRun[1][1]},
					new State(COOLG_RUN_R1 + 1, COOLG_RUN_RATE, DoCoolgMove), // s_CoolgRun[1][2]},
					new State(COOLG_RUN_R1 + 2, COOLG_RUN_RATE, DoCoolgMove), // s_CoolgRun[1][3]},
					new State(COOLG_RUN_R1 + 3, COOLG_RUN_RATE, DoCoolgMove),// s_CoolgRun[1][0]},
			}, { new State(COOLG_RUN_R2 + 0, COOLG_RUN_RATE, DoCoolgMove), // s_CoolgRun[2][1]},
					new State(COOLG_RUN_R2 + 1, COOLG_RUN_RATE, DoCoolgMove), // s_CoolgRun[2][2]},
					new State(COOLG_RUN_R2 + 2, COOLG_RUN_RATE, DoCoolgMove), // s_CoolgRun[2][3]},
					new State(COOLG_RUN_R2 + 3, COOLG_RUN_RATE, DoCoolgMove),// s_CoolgRun[2][0]},
			}, { new State(COOLG_RUN_R3 + 0, COOLG_RUN_RATE, DoCoolgMove), // s_CoolgRun[3][1]},
					new State(COOLG_RUN_R3 + 1, COOLG_RUN_RATE, DoCoolgMove), // s_CoolgRun[3][2]},
					new State(COOLG_RUN_R3 + 2, COOLG_RUN_RATE, DoCoolgMove), // s_CoolgRun[3][3]},
					new State(COOLG_RUN_R3 + 3, COOLG_RUN_RATE, DoCoolgMove),// s_CoolgRun[3][0]},
			}, { new State(COOLG_RUN_R4 + 0, COOLG_RUN_RATE, DoCoolgMove), // s_CoolgRun[4][1]},
					new State(COOLG_RUN_R4 + 1, COOLG_RUN_RATE, DoCoolgMove), // s_CoolgRun[4][2]},
					new State(COOLG_RUN_R4 + 2, COOLG_RUN_RATE, DoCoolgMove), // s_CoolgRun[4][3]},
					new State(COOLG_RUN_R4 + 3, COOLG_RUN_RATE, DoCoolgMove),// s_CoolgRun[4][0]},
			} };

	//////////////////////
	//
	// COOLG STAND
	//
	//////////////////////

	private static final State s_CoolgStand[][] = {
			{ new State(COOLG_RUN_R0 + 0, COOLG_RUN_RATE, DoCoolgMove).setNext(),// s_CoolgStand[0][0]},
			}, { new State(COOLG_RUN_R1 + 0, COOLG_RUN_RATE, DoCoolgMove).setNext(),// s_CoolgStand[1][0]},
			}, { new State(COOLG_RUN_R2 + 0, COOLG_RUN_RATE, DoCoolgMove).setNext(),// s_CoolgStand[2][0]},
			}, { new State(COOLG_RUN_R3 + 0, COOLG_RUN_RATE, DoCoolgMove).setNext(),// s_CoolgStand[3][0]},
			}, { new State(COOLG_RUN_R4 + 0, COOLG_RUN_RATE, DoCoolgMove).setNext(),// s_CoolgStand[4][0]},
			} };

	public static final int COOLG_RATE = 16;

	private static final Animator NullCoolg = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return NullCoolg(SpriteNum) != 0;
		}
	};

	//////////////////////
	//
	// COOLG FIRE
	//
	//////////////////////

	public static final int COOLG_FIRE_RATE = 12;

	private static final State s_CoolgAttack[][] = { { new State(COOLG_FIRE_R0 + 0, COOLG_FIRE_RATE * 2, NullCoolg), // s_CoolgAttack[0][1]},
			new State(COOLG_FIRE_R0 + 1, COOLG_FIRE_RATE * 2, NullCoolg), // s_CoolgAttack[0][2]},
			new State(COOLG_FIRE_R0 + 2, COOLG_FIRE_RATE * 2, NullCoolg), // s_CoolgAttack[0][3]},
			new State(COOLG_FIRE_R0 + 2, 0 | SF_QUICK_CALL, InitCoolgFire), // s_CoolgAttack[0][4]},
			new State(COOLG_FIRE_R0 + 2, COOLG_FIRE_RATE, NullCoolg), // s_CoolgAttack[0][5]},
			new State(COOLG_FIRE_R0 + 2, 0 | SF_QUICK_CALL, InitActorDecide), // s_CoolgAttack[0][6]},
			new State(COOLG_RUN_R0 + 2, COOLG_FIRE_RATE, DoCoolgMove).setNext(),// s_CoolgAttack[0][6]}
			}, { new State(COOLG_FIRE_R1 + 0, COOLG_FIRE_RATE * 2, NullCoolg), // s_CoolgAttack[1][1]},
					new State(COOLG_FIRE_R1 + 1, COOLG_FIRE_RATE * 2, NullCoolg), // s_CoolgAttack[1][2]},
					new State(COOLG_FIRE_R1 + 2, COOLG_FIRE_RATE * 2, NullCoolg), // s_CoolgAttack[1][3]},
					new State(COOLG_FIRE_R1 + 2, 0 | SF_QUICK_CALL, InitCoolgFire), // s_CoolgAttack[1][4]},
					new State(COOLG_FIRE_R1 + 2, COOLG_FIRE_RATE, NullCoolg), // s_CoolgAttack[1][5]},
					new State(COOLG_FIRE_R1 + 2, 0 | SF_QUICK_CALL, InitActorDecide), // s_CoolgAttack[1][6]},
					new State(COOLG_RUN_R0 + 2, COOLG_FIRE_RATE, DoCoolgMove).setNext(),// s_CoolgAttack[1][6]}
			}, { new State(COOLG_FIRE_R2 + 0, COOLG_FIRE_RATE * 2, NullCoolg), // s_CoolgAttack[2][1]},
					new State(COOLG_FIRE_R2 + 1, COOLG_FIRE_RATE * 2, NullCoolg), // s_CoolgAttack[2][2]},
					new State(COOLG_FIRE_R2 + 2, COOLG_FIRE_RATE * 2, NullCoolg), // s_CoolgAttack[2][3]},
					new State(COOLG_FIRE_R2 + 2, 0 | SF_QUICK_CALL, InitCoolgFire), // s_CoolgAttack[2][4]},
					new State(COOLG_FIRE_R2 + 2, COOLG_FIRE_RATE, NullCoolg), // s_CoolgAttack[2][5]},
					new State(COOLG_FIRE_R2 + 2, 0 | SF_QUICK_CALL, InitActorDecide), // s_CoolgAttack[2][6]},
					new State(COOLG_RUN_R0 + 2, COOLG_FIRE_RATE, DoCoolgMove).setNext(),// s_CoolgAttack[2][6]}
			}, { new State(COOLG_RUN_R3 + 0, COOLG_FIRE_RATE * 2, NullCoolg), // s_CoolgAttack[3][1]},
					new State(COOLG_RUN_R3 + 1, COOLG_FIRE_RATE * 2, NullCoolg), // s_CoolgAttack[3][2]},
					new State(COOLG_RUN_R3 + 2, COOLG_FIRE_RATE * 2, NullCoolg), // s_CoolgAttack[3][3]},
					new State(COOLG_RUN_R3 + 2, 0 | SF_QUICK_CALL, InitCoolgFire), // s_CoolgAttack[3][4]},
					new State(COOLG_RUN_R3 + 2, COOLG_FIRE_RATE, NullCoolg), // s_CoolgAttack[3][5]},
					new State(COOLG_RUN_R3 + 2, 0 | SF_QUICK_CALL, InitActorDecide), // s_CoolgAttack[3][6]},
					new State(COOLG_RUN_R0 + 2, COOLG_FIRE_RATE, DoCoolgMove).setNext(),// s_CoolgAttack[3][6]}
			}, { new State(COOLG_RUN_R4 + 0, COOLG_FIRE_RATE * 2, NullCoolg), // s_CoolgAttack[4][1]},
					new State(COOLG_RUN_R4 + 1, COOLG_FIRE_RATE * 2, NullCoolg), // s_CoolgAttack[4][2]},
					new State(COOLG_RUN_R4 + 2, COOLG_FIRE_RATE * 2, NullCoolg), // s_CoolgAttack[4][3]},
					new State(COOLG_RUN_R4 + 2, 0 | SF_QUICK_CALL, InitCoolgFire), // s_CoolgAttack[4][4]},
					new State(COOLG_RUN_R4 + 2, COOLG_FIRE_RATE, NullCoolg), // s_CoolgAttack[4][5]},
					new State(COOLG_RUN_R4 + 2, 0 | SF_QUICK_CALL, InitActorDecide), // s_CoolgAttack[4][6]},
					new State(COOLG_RUN_R0 + 2, COOLG_FIRE_RATE, DoCoolgMove).setNext(),// s_CoolgAttack[4][6]}
			} };

	//////////////////////
	//
	// COOLG PAIN
	//
	//////////////////////

	public static final int COOLG_PAIN_RATE = 15;

	private static final Animator DoCoolgPain = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoCoolgPain(SpriteNum) != 0;
		}
	};

	private static final State s_CoolgPain[][] = { { new State(COOLG_PAIN_R0 + 0, COOLG_PAIN_RATE, DoCoolgPain), // s_CoolgPain[0][1]},
			new State(COOLG_PAIN_R0 + 0, COOLG_PAIN_RATE, DoCoolgPain).setNext(),// s_CoolgPain[0][1]},
			}, { new State(COOLG_RUN_R1 + 0, COOLG_PAIN_RATE, DoCoolgPain), // s_CoolgPain[1][1]},
					new State(COOLG_RUN_R1 + 0, COOLG_PAIN_RATE, DoCoolgPain).setNext(),// s_CoolgPain[1][1]},
			}, { new State(COOLG_RUN_R2 + 0, COOLG_PAIN_RATE, DoCoolgPain), // s_CoolgPain[2][1]},
					new State(COOLG_RUN_R2 + 0, COOLG_PAIN_RATE, DoCoolgPain).setNext(),// s_CoolgPain[2][1]},
			}, { new State(COOLG_RUN_R3 + 0, COOLG_PAIN_RATE, DoCoolgPain), // s_CoolgPain[3][1]},
					new State(COOLG_RUN_R3 + 0, COOLG_PAIN_RATE, DoCoolgPain).setNext(),// s_CoolgPain[3][1]},
			}, { new State(COOLG_RUN_R4 + 0, COOLG_PAIN_RATE, DoCoolgPain), // s_CoolgPain[4][1]},
					new State(COOLG_RUN_R4 + 0, COOLG_PAIN_RATE, DoCoolgPain).setNext(),// s_CoolgPain[4][1]},
			}, };

	//////////////////////
	//
	// COOLG DIE
	//
	//////////////////////

	public static final int COOLG_DIE_RATE = 20;

	public static final int COOLG_DIE = 4307;
	public static final int COOLG_DEAD = 4307 + 5;

	private static final Animator DoCoolgDeath = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoCoolgDeath(SpriteNum) != 0;
		}
	};

	private static final State s_CoolgDie[] = { new State(COOLG_DIE + 0, COOLG_DIE_RATE, DoCoolgDeath), // s_CoolgDie[1]},
			new State(COOLG_DIE + 1, COOLG_DIE_RATE, DoCoolgDeath), // s_CoolgDie[2]},
			new State(COOLG_DIE + 2, COOLG_DIE_RATE, DoCoolgDeath), // s_CoolgDie[3]},
			new State(COOLG_DIE + 3, COOLG_DIE_RATE, DoCoolgDeath), // s_CoolgDie[4]},
			new State(COOLG_DIE + 4, COOLG_DIE_RATE, DoCoolgDeath), // s_CoolgDie[5]},
			new State(COOLG_DIE + 5, COOLG_DIE_RATE, DoCoolgDeath).setNext(),// s_CoolgDie[5]},
	};

	private static final State s_CoolgDead[] = { new State(COOLG_DEAD, SF_QUICK_CALL, QueueFloorBlood), // s_CoolgDead[1]},
			new State(COOLG_DEAD, COOLG_DIE_RATE, DoActorDebris).setNext(),// s_CoolgDead[1]},
	};

	//////////////////////
	//
	// COOLG BIRTH
	//
	//////////////////////

	public static final int COOLG_BIRTH_RATE = 20;
	public static final int COOLG_BIRTH = 4268;

	private static final Animator DoCoolgBirth = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoCoolgBirth(SpriteNum) != 0;
		}
	};

	private static final State s_CoolgBirth[] = { new State(COOLG_BIRTH + 0, COOLG_BIRTH_RATE, null), // s_CoolgBirth[1]},
			new State(COOLG_BIRTH + 1, COOLG_BIRTH_RATE, null), // s_CoolgBirth[2]},
			new State(COOLG_BIRTH + 2, COOLG_BIRTH_RATE, null), // s_CoolgBirth[3]},
			new State(COOLG_BIRTH + 3, COOLG_BIRTH_RATE, null), // s_CoolgBirth[4]},
			new State(COOLG_BIRTH + 4, COOLG_BIRTH_RATE, null), // s_CoolgBirth[5]},
			new State(COOLG_BIRTH + 5, COOLG_BIRTH_RATE, null), // s_CoolgBirth[6]},
			new State(COOLG_BIRTH + 6, COOLG_BIRTH_RATE, null), // s_CoolgBirth[7]},
			new State(COOLG_BIRTH + 7, COOLG_BIRTH_RATE, null), // s_CoolgBirth[8]},
			new State(COOLG_BIRTH + 8, COOLG_BIRTH_RATE, null), // s_CoolgBirth[9]},
			new State(COOLG_BIRTH + 8, COOLG_BIRTH_RATE, null), // s_CoolgBirth[10]},
			new State(COOLG_BIRTH + 8, 0 | SF_QUICK_CALL, DoCoolgBirth).setNext(),// s_CoolgBirth[10]}
	};

	private static final Actor_Action_Set CoolgActionSet = new Actor_Action_Set(CoolgStateGroup.sg_CoolgStand,
			CoolgStateGroup.sg_CoolgRun, null, null, null, null, null, null, null, null, null, // climb
			CoolgStateGroup.sg_CoolgPain, // pain
			CoolgStateGroup.sg_CoolgDie, null, CoolgStateGroup.sg_CoolgDead, null, null,
			new StateGroup[] { CoolgStateGroup.sg_CoolgAttack }, new short[] { 1024 },
			new StateGroup[] { CoolgStateGroup.sg_CoolgAttack }, new short[] { 1024 }, null, null, null);

	private static void CoolgCommon(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		sp.clipdist = (200) >> 2;
		u.floor_dist = (short) Z(16);
		u.ceiling_dist = (short) Z(20);

		u.sz = sp.z;

		sp.xrepeat = 42;
		sp.yrepeat = 42;
		sp.extra |= (SPRX_PLAYER_OR_ENEMY);
	}

	public static int SetupCoolg(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, COOLG_RUN_R0, s_CoolgRun[0][0]);
			u.Health = HEALTH_COOLIE_GHOST;
		}

		ChangeState(SpriteNum, s_CoolgRun[0][0]);
		u.Attrib = CoolgAttrib;
		DoActorSetSpeed(SpriteNum, NORM_SPEED);
		u.StateEnd = s_CoolgDie[0];
		u.Rot = CoolgStateGroup.sg_CoolgRun;

		EnemyDefaults(SpriteNum, CoolgActionSet, CoolgPersonality);

		u.Flags |= (SPR_NO_SCAREDZ | SPR_XFLIP_TOGGLE);

		CoolgCommon(SpriteNum);

		return (0);
	}

	public static int NewCoolg(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();
		USER nu;
		SPRITE np;

		int newsp = SpawnSprite(STAT_ENEMY, COOLG_RUN_R0, s_CoolgBirth[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang, 50);

		nu = pUser[newsp];
		np = sprite[newsp];

		ChangeState(newsp, s_CoolgBirth[0]);
		nu.StateEnd = s_CoolgDie[0];
		nu.Rot = CoolgStateGroup.sg_CoolgRun;
		np.pal = nu.spal = u.spal;

		nu.ActorActionSet = CoolgActionSet;

		np.shade = sp.shade;
		nu.Personality = CoolgPersonality;
		nu.Attrib = CoolgAttrib;

		// special case
		TotalKillable++;
		CoolgCommon(newsp);

		return (0);
	}

	private static int DoCoolgBirth(int newsp) {
		USER u = pUser[newsp];
		u.Health = HEALTH_COOLIE_GHOST;
		u.Attrib = CoolgAttrib;
		DoActorSetSpeed(newsp, NORM_SPEED);

		ChangeState(newsp, s_CoolgRun[0][0]);
		u.StateEnd = s_CoolgDie[0];
		u.Rot = CoolgStateGroup.sg_CoolgRun;

		EnemyDefaults(newsp, CoolgActionSet, CoolgPersonality);
		// special case
		TotalKillable--;

		u.Flags |= (SPR_NO_SCAREDZ | SPR_XFLIP_TOGGLE);
		CoolgCommon(newsp);

		return (0);
	}

	private static int NullCoolg(int SpriteNum) {
		USER u = pUser[SpriteNum];

		u.ShellNum -= ACTORMOVETICS;

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		DoCoolgMatchPlayerZ(SpriteNum);

		DoActorSectorDamage(SpriteNum);

		return (0);
	}

	private static final int COOLG_BOB_AMT = (Z(8));

	private static int DoCoolgMatchPlayerZ(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		SPRITE tsp = sprite[pUser[SpriteNum].tgt_sp];
		int zdiff, zdist;
		int loz, hiz;

		int bound;

		// If blocking bits get unset, just die
		if (!TEST(sp.cstat, CSTAT_SPRITE_BLOCK) || !TEST(sp.cstat, CSTAT_SPRITE_BLOCK_HITSCAN)) {
			InitBloodSpray(SpriteNum, true, 105);
			InitBloodSpray(SpriteNum, true, 105);
			UpdateSinglePlayKills(SpriteNum, null);
			SetSuicide(SpriteNum);
		}

		// actor does a sine wave about u.sz - this is the z mid point

		zdiff = (SPRITEp_MID(tsp)) - u.sz;

		// check z diff of the player and the sprite
		zdist = Z(20 + RANDOM_RANGE(100)); // put a random amount

		if (klabs(zdiff) > zdist) {
			if (zdiff > 0)
				u.sz += 170 * ACTORMOVETICS;
			else
				u.sz -= 170 * ACTORMOVETICS;
		}

		// save off lo and hi z
		loz = u.loz;
		hiz = u.hiz;

		// adjust loz/hiz for water depth
		if (u.lo_sectp != -1 && SectUser[u.lo_sectp] != null && SectUser[u.lo_sectp].depth != 0)
			loz -= Z(SectUser[u.lo_sectp].depth) - Z(8);

		// lower bound
		if (u.lo_sp != -1)
			bound = loz - u.floor_dist;
		else
			bound = loz - u.floor_dist - COOLG_BOB_AMT;

		if (u.sz > bound) {
			u.sz = bound;
		}

		// upper bound
		if (u.hi_sp != -1)
			bound = hiz + u.ceiling_dist;
		else
			bound = hiz + u.ceiling_dist + COOLG_BOB_AMT;

		if (u.sz < bound) {
			u.sz = bound;
		}

		u.sz = Math.min(u.sz, loz - u.floor_dist);
		u.sz = Math.max(u.sz, hiz + u.ceiling_dist);

		u.Counter = (short) ((u.Counter + (ACTORMOVETICS << 3)) & 2047);
		sp.z = u.sz + ((COOLG_BOB_AMT * sintable[u.Counter]) >> 14);

		bound = u.hiz + u.ceiling_dist + COOLG_BOB_AMT;
		if (sp.z < bound) {
			// bumped something
			sp.z = u.sz = bound + COOLG_BOB_AMT;
		}

		return (0);
	}

	private static final Animator DoCoolgCircle = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoCoolgCircle(SpriteNum) != 0;
		}
	};

	private static int InitCoolgCircle(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		u.ActorActionFunc = DoCoolgCircle;

		NewStateGroup(SpriteNum, u.ActorActionSet.Run);

		// set it close
		DoActorSetSpeed(SpriteNum, FAST_SPEED);

		// set to really fast
		sp.xvel = 400;
		// angle adjuster
		u.Counter2 = (short) (sp.xvel / 3);
		// random angle direction
		if (RANDOM_P2(1024) < 512)
			u.Counter2 = (short) -u.Counter2;

		// z velocity
		u.jump_speed = (short) (400 + RANDOM_P2(256));
		if (klabs(u.sz - u.hiz) < klabs(u.sz - u.loz))
			u.jump_speed = (short) -u.jump_speed;

		u.WaitTics = (short) ((RANDOM_RANGE(3) + 1) * 120);

		(u.ActorActionFunc).invoke(SpriteNum);

		return (0);
	}

	private static int DoCoolgCircle(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		int nx, ny, bound;

		sp.ang = NORM_ANGLE(sp.ang + u.Counter2);

		nx = sp.xvel * (int) sintable[NORM_ANGLE(sp.ang + 512)] >> 14;
		ny = sp.xvel * (int) sintable[sp.ang] >> 14;

		if (!move_actor(SpriteNum, nx, ny, 0)) {
			InitActorReposition.invoke(SpriteNum);
			return (0);
		}

		// move in the z direction
		u.sz -= u.jump_speed * ACTORMOVETICS;

		bound = u.hiz + u.ceiling_dist + COOLG_BOB_AMT;
		if (u.sz < bound) {
			// bumped something
			u.sz = bound;
			InitActorReposition.invoke(SpriteNum);
			return (0);
		}

		// time out
		if ((u.WaitTics -= ACTORMOVETICS) < 0) {
			InitActorReposition.invoke(SpriteNum);
			u.WaitTics = 0;
			return (0);
		}

		return (0);
	}

	private static int DoCoolgDeath(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		int nx, ny;

		sp.cstat &= ~(CSTAT_SPRITE_TRANSLUCENT);
		sp.cstat &= ~(CSTAT_SPRITE_INVISIBLE);
		sp.xrepeat = 42;
		sp.shade = -10;

		if (TEST(u.Flags, SPR_FALLING)) {
			DoFall(SpriteNum);
		} else {
			DoFindGroundPoint(SpriteNum);
			u.floor_dist = 0;
			DoBeginFall(SpriteNum);
		}

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		// slide while falling
		nx = sp.xvel * (int) sintable[NORM_ANGLE(sp.ang + 512)] >> 14;
		ny = sp.xvel * (int) sintable[sp.ang] >> 14;

		u.ret = move_sprite(SpriteNum, nx, ny, 0, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE, ACTORMOVETICS);
		DoFindGroundPoint(SpriteNum);

		// on the ground
		if (sp.z >= u.loz) {
			u.Flags &= ~(SPR_FALLING | SPR_SLIDING);
			sp.cstat &= ~(CSTAT_SPRITE_YFLIP); // If upside down, reset it
			NewStateGroup(SpriteNum, u.ActorActionSet.Dead);
			return (0);
		}

		return (0);
	}

	private static int DoCoolgMove(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if ((u.ShellNum -= ACTORMOVETICS) <= 0) {
			switch (u.FlagOwner) {
			case 0:
				sp.cstat |= (CSTAT_SPRITE_TRANSLUCENT);
				u.ShellNum = SEC(2);
				break;
			case 1:
				PlaySound(DIGI_VOID3, sp, v3df_follow);
				sp.cstat &= ~(CSTAT_SPRITE_TRANSLUCENT);
				sp.cstat |= (CSTAT_SPRITE_INVISIBLE);
				u.ShellNum = SEC(1) + SEC(RANDOM_RANGE(2));
				break;
			case 2:
				sp.cstat |= (CSTAT_SPRITE_TRANSLUCENT);
				sp.cstat &= ~(CSTAT_SPRITE_INVISIBLE);
				u.ShellNum = SEC(2);
				break;
			case 3:
				PlaySound(DIGI_VOID3, sp, v3df_follow);
				sp.cstat &= ~(CSTAT_SPRITE_TRANSLUCENT);
				sp.cstat &= ~(CSTAT_SPRITE_INVISIBLE);
				u.ShellNum = SEC(2) + SEC(RANDOM_RANGE(3));
				break;
			default:
				u.FlagOwner = 0;
				break;
			}
			u.FlagOwner++;
			if (u.FlagOwner > 3)
				u.FlagOwner = 0;
		}

		if (u.FlagOwner - 1 == 0) {
			sp.xrepeat--;
			sp.shade++;
			if (sp.xrepeat < 4)
				sp.xrepeat = 4;
			if (sp.shade > 126) {
				sp.shade = 127;
				sp.hitag = 9998;
			}
		} else if (u.FlagOwner - 1 == 2) {
			sp.hitag = 0;
			sp.xrepeat++;
			sp.shade--;
			if (sp.xrepeat > 42)
				sp.xrepeat = 42;
			if (sp.shade < -10)
				sp.shade = -10;
		} else if (u.FlagOwner == 0) {
			sp.xrepeat = 42;
			sp.shade = -10;
			sp.hitag = 0;
		}

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		if (u.track >= 0)
			ActorFollowTrack(SpriteNum, ACTORMOVETICS);
		else {
			(u.ActorActionFunc).invoke(SpriteNum);
		}

		if (RANDOM_P2(1024) < 32 && !TEST(sp.cstat, CSTAT_SPRITE_INVISIBLE))
			InitCoolgDrip(SpriteNum);

		DoCoolgMatchPlayerZ(SpriteNum);

		DoActorSectorDamage(SpriteNum);

		return (0);

	}

	private static int DoCoolgPain(int SpriteNum) {
		USER u = pUser[SpriteNum];

		NullCoolg(SpriteNum);

		if ((u.WaitTics -= ACTORMOVETICS) <= 0)
			InitActorDecide(SpriteNum);

		return (0);
	}
	
	public static void CoolgSaveable()
	{
		SaveData(DoCoolgBirth);
		SaveData(NullCoolg);
		SaveData(InitCoolgCircle);
		SaveData(DoCoolgCircle);
		SaveData(DoCoolgDeath);
		SaveData(DoCoolgMove);
		SaveData(DoCoolgPain);
		SaveData(CoolgPersonality);
		SaveData(CoolgAttrib);
		SaveData(s_CoolgRun);
		SaveGroup(CoolgStateGroup.sg_CoolgRun);
		SaveData(s_CoolgStand);
		SaveGroup(CoolgStateGroup.sg_CoolgStand);
		SaveData(s_CoolgAttack);
		SaveGroup(CoolgStateGroup.sg_CoolgAttack);
		SaveData(s_CoolgPain);
		SaveGroup(CoolgStateGroup.sg_CoolgPain);
		SaveData(s_CoolgDie);
		SaveGroup(CoolgStateGroup.sg_CoolgDie);
		SaveData(s_CoolgDead);
		SaveGroup(CoolgStateGroup.sg_CoolgDead);
		SaveData(s_CoolgBirth);

		SaveData(CoolgActionSet);
	}
}
