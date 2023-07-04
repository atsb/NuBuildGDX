package ru.m210projects.Wang.Enemies;

import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Wang.Actor.DoActorDebris;
import static ru.m210projects.Wang.Actor.DoActorSectorDamage;
import static ru.m210projects.Wang.Actor.DoActorSlide;
import static ru.m210projects.Wang.Actor.KeepActorOnFloor;
import static ru.m210projects.Wang.Ai.DoActorSetSpeed;
import static ru.m210projects.Wang.Ai.InitActorAlertNoise;
import static ru.m210projects.Wang.Ai.InitActorAmbientNoise;
import static ru.m210projects.Wang.Ai.InitActorAttack;
import static ru.m210projects.Wang.Ai.InitActorDecide;
import static ru.m210projects.Wang.Ai.InitActorEvade;
import static ru.m210projects.Wang.Ai.InitActorFindPlayer;
import static ru.m210projects.Wang.Ai.InitActorMoveCloser;
import static ru.m210projects.Wang.Ai.InitActorReposition;
import static ru.m210projects.Wang.Ai.InitActorRunAway;
import static ru.m210projects.Wang.Ai.InitActorWanderAround;
import static ru.m210projects.Wang.Ai.NORM_SPEED;
import static ru.m210projects.Wang.Digi.DIGI_LAVABOSSALERT;
import static ru.m210projects.Wang.Digi.DIGI_LAVABOSSAMBIENT;
import static ru.m210projects.Wang.Digi.DIGI_LAVABOSSEXPLODE;
import static ru.m210projects.Wang.Digi.DIGI_LAVABOSSFLAME;
import static ru.m210projects.Wang.Digi.DIGI_LAVABOSSMETEOR;
import static ru.m210projects.Wang.Digi.DIGI_LAVABOSSMETEXP;
import static ru.m210projects.Wang.Digi.DIGI_LAVABOSSPAIN;
import static ru.m210projects.Wang.Digi.DIGI_LAVABOSSRISE;
import static ru.m210projects.Wang.Digi.DIGI_LAVABOSSSIZZLE;
import static ru.m210projects.Wang.Digi.DIGI_LAVABOSSSWIM;
import static ru.m210projects.Wang.Enemies.Coolie.EnemyDefaults;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_RESTORE;
import static ru.m210projects.Wang.Gameutils.SF_QUICK_CALL;
import static ru.m210projects.Wang.Gameutils.SPR_ELECTRO_TOLERANT;
import static ru.m210projects.Wang.Gameutils.SPR_SLIDING;
import static ru.m210projects.Wang.Gameutils.SPR_XFLIP_TOGGLE;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Names.LAVA_DEAD;
import static ru.m210projects.Wang.Names.LAVA_DIE;
import static ru.m210projects.Wang.Names.LAVA_FLAME_R0;
import static ru.m210projects.Wang.Names.LAVA_FLAME_R1;
import static ru.m210projects.Wang.Names.LAVA_FLAME_R2;
import static ru.m210projects.Wang.Names.LAVA_FLAME_R3;
import static ru.m210projects.Wang.Names.LAVA_FLAME_R4;
import static ru.m210projects.Wang.Names.LAVA_RUN_R0;
import static ru.m210projects.Wang.Names.LAVA_RUN_R1;
import static ru.m210projects.Wang.Names.LAVA_RUN_R2;
import static ru.m210projects.Wang.Names.LAVA_RUN_R3;
import static ru.m210projects.Wang.Names.LAVA_RUN_R4;
import static ru.m210projects.Wang.Names.LAVA_THROW_R0;
import static ru.m210projects.Wang.Names.LAVA_THROW_R1;
import static ru.m210projects.Wang.Names.LAVA_THROW_R2;
import static ru.m210projects.Wang.Names.LAVA_THROW_R3;
import static ru.m210projects.Wang.Names.LAVA_THROW_R4;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Track.ActorFollowTrack;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.*;
import static ru.m210projects.Wang.Weapon.InitLavaFlame;
import static ru.m210projects.Wang.Weapon.InitLavaThrow;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Type.ATTRIBUTE;
import ru.m210projects.Wang.Type.Actor_Action_Set;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;

public class Lava {

	public enum LavaStateGroup implements StateGroup {
		sg_LavaStand(s_LavaStand[0], s_LavaStand[1], s_LavaStand[2], s_LavaStand[3], s_LavaStand[4]),
		sg_LavaRun(s_LavaRun[0], s_LavaRun[1], s_LavaRun[2], s_LavaRun[3], s_LavaRun[4]), sg_LavaDie(s_LavaDie),
		sg_LavaDead(s_LavaDead),
		sg_LavaFlame(s_LavaFlame[0], s_LavaFlame[1], s_LavaFlame[2], s_LavaFlame[3], s_LavaFlame[4]),
		sg_LavaThrow(s_LavaThrow[0], s_LavaThrow[1], s_LavaThrow[2], s_LavaThrow[3], s_LavaThrow[4]);

		private final State[][] group;
		private int index = -1;

		LavaStateGroup(State[]... states) {
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
	
	public static void InitLavaStates() {
		for (LavaStateGroup sg : LavaStateGroup.values()) {
			for (int rot = 0; rot < sg.group.length; rot++) {
				State.InitState(sg.group[rot]);
			}
		}
	}

	private static final Decision LavaBattle[] = { new Decision(600, InitActorMoveCloser),
			new Decision(700, InitActorAlertNoise), new Decision(710, InitActorRunAway),
			new Decision(1024, InitActorAttack) };

	private static final Decision LavaOffense[] = { new Decision(700, InitActorMoveCloser),
			new Decision(800, InitActorAlertNoise), new Decision(1024, InitActorAttack) };

	private static final Decision LavaBroadcast[] = { new Decision(21, InitActorAlertNoise),
			new Decision(51, InitActorAmbientNoise), new Decision(1024, InitActorDecide) };

	private static final Decision LavaSurprised[] = { new Decision(701, InitActorMoveCloser),
			new Decision(1024, InitActorDecide) };

	private static final Decision LavaEvasive[] = { new Decision(10, InitActorEvade), new Decision(1024, null) };

	private static final Decision LavaLostTarget[] = { new Decision(900, InitActorFindPlayer),
			new Decision(1024, InitActorWanderAround) };

	private static final Decision LavaCloseRange[] = { new Decision(700, InitActorAttack),
			new Decision(1024, InitActorReposition) };

	private static final Personality LavaPersonality = new Personality(LavaBattle, LavaOffense, LavaBroadcast,
			LavaSurprised, LavaEvasive, LavaLostTarget, LavaCloseRange, LavaCloseRange);

	private static final ATTRIBUTE LavaAttrib = new ATTRIBUTE(new short[] { 200, 220, 240, 270 }, // Speeds
			new short[] { 3, 0, -2, -3 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { DIGI_LAVABOSSAMBIENT, DIGI_LAVABOSSALERT, DIGI_LAVABOSSMETEOR, DIGI_LAVABOSSPAIN,
					DIGI_LAVABOSSEXPLODE, DIGI_LAVABOSSSWIM, DIGI_LAVABOSSRISE, DIGI_LAVABOSSFLAME, DIGI_LAVABOSSMETEXP,
					DIGI_LAVABOSSSIZZLE });

	//////////////////////
	//
	// LAVA STAND
	//
	//////////////////////

	public static final int LAVA_STAND_RATE = 12;

	private static final Animator DoLavaMove = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoLavaMove(SpriteNum) != 0;
		}
	};

	private static final State s_LavaStand[][] = { { new State(LAVA_RUN_R0 + 0, LAVA_STAND_RATE, DoLavaMove).setNext(),// s_LavaStand[0][0]},
			}, { new State(LAVA_RUN_R1 + 0, LAVA_STAND_RATE, DoLavaMove).setNext(),// s_LavaStand[1][0]},
			}, { new State(LAVA_RUN_R2 + 0, LAVA_STAND_RATE, DoLavaMove).setNext(),// s_LavaStand[2][0]},
			}, { new State(LAVA_RUN_R3 + 0, LAVA_STAND_RATE, DoLavaMove).setNext(),// s_LavaStand[3][0]},
			}, { new State(LAVA_RUN_R4 + 0, LAVA_STAND_RATE, DoLavaMove).setNext(),// s_LavaStand[4][0]},
			}, };

	//////////////////////
	//
	// LAVA RUN
	//
	//////////////////////

	public static final int LAVA_RUN_RATE = 24;

	private static final State s_LavaRun[][] = { { new State(LAVA_RUN_R0 + 0, LAVA_RUN_RATE, DoLavaMove), // s_LavaRun[0][1]},
			new State(LAVA_RUN_R0 + 0, LAVA_RUN_RATE, DoLavaMove), // s_LavaRun[0][2]},
			new State(LAVA_RUN_R0 + 0, LAVA_RUN_RATE, DoLavaMove), // s_LavaRun[0][3]},
			new State(LAVA_RUN_R0 + 0, LAVA_RUN_RATE, DoLavaMove),// s_LavaRun[0][0]},
			}, { new State(LAVA_RUN_R1 + 0, LAVA_RUN_RATE, DoLavaMove), // s_LavaRun[1][1]},
					new State(LAVA_RUN_R1 + 0, LAVA_RUN_RATE, DoLavaMove), // s_LavaRun[1][2]},
					new State(LAVA_RUN_R1 + 0, LAVA_RUN_RATE, DoLavaMove), // s_LavaRun[1][3]},
					new State(LAVA_RUN_R1 + 0, LAVA_RUN_RATE, DoLavaMove),// s_LavaRun[1][0]},
			}, { new State(LAVA_RUN_R2 + 0, LAVA_RUN_RATE, DoLavaMove), // s_LavaRun[2][1]},
					new State(LAVA_RUN_R2 + 0, LAVA_RUN_RATE, DoLavaMove), // s_LavaRun[2][2]},
					new State(LAVA_RUN_R2 + 0, LAVA_RUN_RATE, DoLavaMove), // s_LavaRun[2][3]},
					new State(LAVA_RUN_R2 + 0, LAVA_RUN_RATE, DoLavaMove),// s_LavaRun[2][0]},
			}, { new State(LAVA_RUN_R3 + 0, LAVA_RUN_RATE, DoLavaMove), // s_LavaRun[3][1]},
					new State(LAVA_RUN_R3 + 0, LAVA_RUN_RATE, DoLavaMove), // s_LavaRun[3][2]},
					new State(LAVA_RUN_R3 + 0, LAVA_RUN_RATE, DoLavaMove), // s_LavaRun[3][3]},
					new State(LAVA_RUN_R3 + 0, LAVA_RUN_RATE, DoLavaMove),// s_LavaRun[3][0]},
			}, { new State(LAVA_RUN_R4 + 0, LAVA_RUN_RATE, DoLavaMove), // s_LavaRun[4][1]},
					new State(LAVA_RUN_R4 + 0, LAVA_RUN_RATE, DoLavaMove), // s_LavaRun[4][2]},
					new State(LAVA_RUN_R4 + 0, LAVA_RUN_RATE, DoLavaMove), // s_LavaRun[4][3]},
					new State(LAVA_RUN_R4 + 0, LAVA_RUN_RATE, DoLavaMove),// s_LavaRun[4][0]},
			} };

	//////////////////////
	//
	// LAVA THROW
	//
	//////////////////////

	public static final int LAVA_THROW_RATE = 9;

	private static final Animator NullLava = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return NullLava(SpriteNum) != 0;
		}
	};

	private static final Animator InitLavaThrow = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitLavaThrow(SpriteNum) != 0;
		}
	};

	private static final State s_LavaThrow[][] = { { new State(LAVA_THROW_R0 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[0][1]},
			new State(LAVA_THROW_R0 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[0][2]},
			new State(LAVA_THROW_R0 + 0, LAVA_THROW_RATE * 2, NullLava), // s_LavaThrow[0][3]},
			new State(LAVA_THROW_R0 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[0][4]},
			new State(LAVA_THROW_R0 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[0][5]},
			new State(LAVA_THROW_R0 + 0, SF_QUICK_CALL, InitLavaThrow), // s_LavaThrow[0][6]},
			new State(LAVA_THROW_R0 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[0][7]},
			new State(LAVA_THROW_R0 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[0][8]},
			new State(LAVA_THROW_R0 + 0, 0 | SF_QUICK_CALL, InitActorDecide), // s_LavaThrow[0][9]},
			new State(LAVA_THROW_R0 + 0, LAVA_THROW_RATE, DoLavaMove).setNext(),// s_LavaThrow[0][9]},
			}, { new State(LAVA_THROW_R1 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[1][1]},
					new State(LAVA_THROW_R1 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[1][2]},
					new State(LAVA_THROW_R1 + 0, LAVA_THROW_RATE * 2, NullLava), // s_LavaThrow[1][3]},
					new State(LAVA_THROW_R1 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[1][4]},
					new State(LAVA_THROW_R1 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[1][5]},
					new State(LAVA_THROW_R1 + 0, SF_QUICK_CALL, InitLavaThrow), // s_LavaThrow[1][6]},
					new State(LAVA_THROW_R1 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[1][7]},
					new State(LAVA_THROW_R1 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[1][8]},
					new State(LAVA_THROW_R1 + 0, 0 | SF_QUICK_CALL, InitActorDecide), // s_LavaThrow[1][9]},
					new State(LAVA_THROW_R1 + 0, LAVA_THROW_RATE, DoLavaMove).setNext(),// s_LavaThrow[1][9]},
			}, { new State(LAVA_THROW_R2 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[2][1]},
					new State(LAVA_THROW_R2 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[2][2]},
					new State(LAVA_THROW_R2 + 0, LAVA_THROW_RATE * 2, NullLava), // s_LavaThrow[2][3]},
					new State(LAVA_THROW_R2 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[2][4]},
					new State(LAVA_THROW_R2 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[2][5]},
					new State(LAVA_THROW_R2 + 0, SF_QUICK_CALL, InitLavaThrow), // s_LavaThrow[2][6]},
					new State(LAVA_THROW_R2 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[2][7]},
					new State(LAVA_THROW_R2 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[2][8]},
					new State(LAVA_THROW_R2 + 0, 0 | SF_QUICK_CALL, InitActorDecide), // s_LavaThrow[2][9]},
					new State(LAVA_THROW_R2 + 0, LAVA_THROW_RATE, DoLavaMove).setNext(),// s_LavaThrow[2][9]},
			}, { new State(LAVA_THROW_R3 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[3][1]},
					new State(LAVA_THROW_R3 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[3][2]},
					new State(LAVA_THROW_R3 + 0, LAVA_THROW_RATE * 2, NullLava), // s_LavaThrow[3][3]},
					new State(LAVA_THROW_R3 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[3][4]},
					new State(LAVA_THROW_R3 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[3][5]},
					new State(LAVA_THROW_R3 + 0, SF_QUICK_CALL, InitLavaThrow), // s_LavaThrow[3][6]},
					new State(LAVA_THROW_R3 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[3][7]},
					new State(LAVA_THROW_R3 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[3][8]},
					new State(LAVA_THROW_R3 + 0, 0 | SF_QUICK_CALL, InitActorDecide), // s_LavaThrow[3][9]},
					new State(LAVA_THROW_R3 + 0, LAVA_THROW_RATE, DoLavaMove).setNext(),// s_LavaThrow[3][9]},
			}, { new State(LAVA_THROW_R4 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[4][1]},
					new State(LAVA_THROW_R4 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[4][2]},
					new State(LAVA_THROW_R4 + 0, LAVA_THROW_RATE * 2, NullLava), // s_LavaThrow[4][3]},
					new State(LAVA_THROW_R4 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[4][4]},
					new State(LAVA_THROW_R4 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[4][5]},
					new State(LAVA_THROW_R4 + 0, SF_QUICK_CALL, InitLavaThrow), // s_LavaThrow[4][6]},
					new State(LAVA_THROW_R4 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[4][7]},
					new State(LAVA_THROW_R4 + 0, LAVA_THROW_RATE, NullLava), // s_LavaThrow[4][8]},
					new State(LAVA_THROW_R4 + 0, 0 | SF_QUICK_CALL, InitActorDecide), // s_LavaThrow[4][9]},
					new State(LAVA_THROW_R4 + 0, LAVA_THROW_RATE, DoLavaMove).setNext(),// s_LavaThrow[4][9]},
			} };

	//////////////////////
	//
	// LAVA FLAME
	//
	//////////////////////

	public static final int LAVA_FLAME_RATE = 18;

	private static final Animator InitLavaFlame = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitLavaFlame(SpriteNum) != 0;
		}
	};

	private static final State s_LavaFlame[][] = { { new State(LAVA_FLAME_R0 + 0, LAVA_FLAME_RATE * 2, NullLava), // s_LavaFlame[0][1]},
			new State(LAVA_FLAME_R0 + 0, LAVA_FLAME_RATE, NullLava), // s_LavaFlame[0][2]},
			new State(LAVA_FLAME_R0 + 0, LAVA_FLAME_RATE * 2, NullLava), // s_LavaFlame[0][3]},
			new State(LAVA_FLAME_R0 + 0, SF_QUICK_CALL, InitLavaFlame), // s_LavaFlame[0][4]},
			new State(LAVA_FLAME_R0 + 0, LAVA_FLAME_RATE, NullLava), // s_LavaFlame[0][5]},
			new State(LAVA_FLAME_R0 + 0, LAVA_FLAME_RATE, NullLava), // s_LavaFlame[0][6]},
			new State(LAVA_FLAME_R0 + 0, SF_QUICK_CALL, InitActorDecide), // s_LavaFlame[0][7]},
			new State(LAVA_FLAME_R0 + 0, LAVA_FLAME_RATE, DoLavaMove).setNext(),// s_LavaFlame[0][7]},
			}, { new State(LAVA_FLAME_R1 + 0, LAVA_FLAME_RATE * 2, NullLava), // s_LavaFlame[1][1]},
					new State(LAVA_FLAME_R1 + 0, LAVA_FLAME_RATE, NullLava), // s_LavaFlame[1][2]},
					new State(LAVA_FLAME_R1 + 0, LAVA_FLAME_RATE * 2, NullLava), // s_LavaFlame[1][3]},
					new State(LAVA_FLAME_R1 + 0, SF_QUICK_CALL, InitLavaFlame), // s_LavaFlame[1][4]},
					new State(LAVA_FLAME_R1 + 0, LAVA_FLAME_RATE, NullLava), // s_LavaFlame[1][5]},
					new State(LAVA_FLAME_R1 + 0, LAVA_FLAME_RATE, NullLava), // s_LavaFlame[1][6]},
					new State(LAVA_FLAME_R1 + 0, SF_QUICK_CALL, InitActorDecide), // s_LavaFlame[1][7]},
					new State(LAVA_FLAME_R1 + 0, LAVA_FLAME_RATE, DoLavaMove).setNext(),// s_LavaFlame[1][7]},
			}, { new State(LAVA_FLAME_R2 + 0, LAVA_FLAME_RATE * 2, NullLava), // s_LavaFlame[2][1]},
					new State(LAVA_FLAME_R2 + 0, LAVA_FLAME_RATE, NullLava), // s_LavaFlame[2][2]},
					new State(LAVA_FLAME_R2 + 0, LAVA_FLAME_RATE * 2, NullLava), // s_LavaFlame[2][3]},
					new State(LAVA_FLAME_R2 + 0, SF_QUICK_CALL, InitLavaFlame), // s_LavaFlame[2][4]},
					new State(LAVA_FLAME_R2 + 0, LAVA_FLAME_RATE, NullLava), // s_LavaFlame[2][5]},
					new State(LAVA_FLAME_R2 + 0, LAVA_FLAME_RATE, NullLava), // s_LavaFlame[2][6]},
					new State(LAVA_FLAME_R2 + 0, SF_QUICK_CALL, InitActorDecide), // s_LavaFlame[2][7]},
					new State(LAVA_FLAME_R2 + 0, LAVA_FLAME_RATE, DoLavaMove).setNext(),// s_LavaFlame[2][7]},
			}, { new State(LAVA_FLAME_R3 + 0, LAVA_FLAME_RATE * 2, NullLava), // s_LavaFlame[3][1]},
					new State(LAVA_FLAME_R3 + 0, LAVA_FLAME_RATE, NullLava), // s_LavaFlame[3][2]},
					new State(LAVA_FLAME_R3 + 0, LAVA_FLAME_RATE * 2, NullLava), // s_LavaFlame[3][3]},
					new State(LAVA_FLAME_R3 + 0, SF_QUICK_CALL, InitLavaFlame), // s_LavaFlame[3][4]},
					new State(LAVA_FLAME_R3 + 0, LAVA_FLAME_RATE, NullLava), // s_LavaFlame[3][5]},
					new State(LAVA_FLAME_R3 + 0, LAVA_FLAME_RATE, NullLava), // s_LavaFlame[3][6]},
					new State(LAVA_FLAME_R3 + 0, SF_QUICK_CALL, InitActorDecide), // s_LavaFlame[3][7]},
					new State(LAVA_FLAME_R3 + 0, LAVA_FLAME_RATE, DoLavaMove).setNext(),// s_LavaFlame[3][7]},
			}, { new State(LAVA_FLAME_R4 + 0, LAVA_FLAME_RATE * 2, NullLava), // s_LavaFlame[4][1]},
					new State(LAVA_FLAME_R4 + 0, LAVA_FLAME_RATE, NullLava), // s_LavaFlame[4][2]},
					new State(LAVA_FLAME_R4 + 0, LAVA_FLAME_RATE * 2, NullLava), // s_LavaFlame[4][3]},
					new State(LAVA_FLAME_R4 + 0, SF_QUICK_CALL, InitLavaFlame), // s_LavaFlame[4][4]},
					new State(LAVA_FLAME_R4 + 0, LAVA_FLAME_RATE, NullLava), // s_LavaFlame[4][5]},
					new State(LAVA_FLAME_R4 + 0, LAVA_FLAME_RATE, NullLava), // s_LavaFlame[4][6]},
					new State(LAVA_FLAME_R4 + 0, SF_QUICK_CALL, InitActorDecide), // s_LavaFlame[4][7]},
					new State(LAVA_FLAME_R4 + 0, LAVA_FLAME_RATE, DoLavaMove).setNext(),// s_LavaFlame[4][7]},
			} };

	//////////////////////
	//
	// LAVA DIE
	//
	//////////////////////

	public static final int LAVA_DIE_RATE = 20;

	private static final State s_LavaDie[] = { new State(LAVA_DIE + 0, LAVA_DIE_RATE, NullLava), // s_LavaDie[1]},
			new State(LAVA_DEAD, LAVA_DIE_RATE, DoActorDebris).setNext(),// s_LavaDie[1]}
	};

	private static final State s_LavaDead[] = { new State(LAVA_DEAD, LAVA_DIE_RATE, DoActorDebris).setNext(),// s_LavaDead[0]},
	};

	private static final Actor_Action_Set LavaActionSet = new Actor_Action_Set(LavaStateGroup.sg_LavaStand,
			LavaStateGroup.sg_LavaRun, null, // LavaStateGroup.sg_LavaJump,
			null, // LavaStateGroup.sg_LavaFall,
			null, // LavaStateGroup.sg_LavaCrawl,
			null, // LavaStateGroup.sg_LavaSwim,
			null, // LavaStateGroup.sg_LavaFly,
			null, // LavaStateGroup.sg_LavaRise,
			null, // LavaStateGroup.sg_LavaSit,
			null, // LavaStateGroup.sg_LavaLook,
			null, // climb
			null, // pain
			LavaStateGroup.sg_LavaDie, null, // LavaStateGroup.sg_LavaHariKari,
			LavaStateGroup.sg_LavaDead, null, // LavaStateGroup.sg_LavaDeathJump,
			null, // LavaStateGroup.sg_LavaDeathFall,
			new StateGroup[] { LavaStateGroup.sg_LavaFlame }, new short[] { 1024 },
			new StateGroup[] { LavaStateGroup.sg_LavaFlame, LavaStateGroup.sg_LavaThrow, LavaStateGroup.sg_LavaThrow,
					LavaStateGroup.sg_LavaThrow },
			new short[] { 256, 512, 768, 1024 }, null, null, null);

	public static int SetupLava(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, LAVA_RUN_R0, s_LavaRun[0][0]);
			u.Health = 100;
		}

		ChangeState(SpriteNum, s_LavaRun[0][0]);
		u.Attrib = LavaAttrib;
		DoActorSetSpeed(SpriteNum, NORM_SPEED);
		u.StateEnd = s_LavaDie[0];
		u.Rot = LavaStateGroup.sg_LavaRun;

		EnemyDefaults(SpriteNum, LavaActionSet, LavaPersonality);
		sp.xrepeat = sp.yrepeat = 110;
		sp.clipdist = (512) >> 2;
		u.Flags |= (SPR_XFLIP_TOGGLE | SPR_ELECTRO_TOLERANT);

		u.loz = sp.z;

		return (0);
	}

	private static int NullLava(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		KeepActorOnFloor(SpriteNum);

		DoActorSectorDamage(SpriteNum);
		return (0);
	}

	private static int DoLavaMove(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		if (u.track >= 0)
			ActorFollowTrack(SpriteNum, ACTORMOVETICS);
		else
			(u.ActorActionFunc).invoke(SpriteNum);

		KeepActorOnFloor(SpriteNum);

		DoActorSectorDamage(SpriteNum);
		return (0);
	}
	
	public static void LavaSaveable()
	{
		SaveData(InitLavaThrow);
		SaveData(InitLavaFlame);
		
		SaveData(NullLava);
		SaveData(DoLavaMove);
		SaveData(LavaPersonality);

		SaveData(LavaAttrib);
		SaveData(s_LavaStand);
		SaveGroup(LavaStateGroup.sg_LavaStand);
		SaveData(s_LavaRun);
		SaveGroup(LavaStateGroup.sg_LavaRun);
		SaveData(s_LavaThrow);
		SaveGroup(LavaStateGroup.sg_LavaThrow);
		SaveData(s_LavaFlame);
		SaveGroup(LavaStateGroup.sg_LavaFlame);
		SaveData(s_LavaDie);
		SaveData(s_LavaDead);
		SaveGroup(LavaStateGroup.sg_LavaDie);
		SaveGroup(LavaStateGroup.sg_LavaDead);

		SaveData(LavaActionSet);
	}
}
