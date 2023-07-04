package ru.m210projects.Wang.Enemies;

import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Wang.Actor.DoActorDebris;
import static ru.m210projects.Wang.Actor.DoActorSectorDamage;
import static ru.m210projects.Wang.Actor.DoActorSlide;
import static ru.m210projects.Wang.Actor.KeepActorOnFloor;
import static ru.m210projects.Wang.Ai.DoActorSetSpeed;
import static ru.m210projects.Wang.Ai.InitActorAmbientNoise;
import static ru.m210projects.Wang.Ai.InitActorAttack;
import static ru.m210projects.Wang.Ai.InitActorAttackNoise;
import static ru.m210projects.Wang.Ai.InitActorDecide;
import static ru.m210projects.Wang.Ai.InitActorEvade;
import static ru.m210projects.Wang.Ai.InitActorFindPlayer;
import static ru.m210projects.Wang.Ai.InitActorMoveCloser;
import static ru.m210projects.Wang.Ai.InitActorReposition;
import static ru.m210projects.Wang.Ai.InitActorWanderAround;
import static ru.m210projects.Wang.Ai.NORM_SPEED;
import static ru.m210projects.Wang.Digi.DIGI_GRDALERT;
import static ru.m210projects.Wang.Digi.DIGI_GRDAMBIENT;
import static ru.m210projects.Wang.Digi.DIGI_GRDFIREBALL;
import static ru.m210projects.Wang.Digi.DIGI_GRDPAIN;
import static ru.m210projects.Wang.Digi.DIGI_GRDSCREAM;
import static ru.m210projects.Wang.Digi.DIGI_GRDSWINGAXE;
import static ru.m210projects.Wang.Enemies.Coolie.EnemyDefaults;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_RESTORE;
import static ru.m210projects.Wang.Gameutils.HEALTH_GORO;
import static ru.m210projects.Wang.Gameutils.SF_QUICK_CALL;
import static ru.m210projects.Wang.Gameutils.SF_TIC_ADJUST;
import static ru.m210projects.Wang.Gameutils.SPR_SLIDING;
import static ru.m210projects.Wang.Gameutils.SPR_XFLIP_TOGGLE;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Names.GORO_CHOP_R0;
import static ru.m210projects.Wang.Names.GORO_CHOP_R1;
import static ru.m210projects.Wang.Names.GORO_CHOP_R2;
import static ru.m210projects.Wang.Names.GORO_CHOP_R3;
import static ru.m210projects.Wang.Names.GORO_CHOP_R4;
import static ru.m210projects.Wang.Names.GORO_DEAD;
import static ru.m210projects.Wang.Names.GORO_DIE;
import static ru.m210projects.Wang.Names.GORO_RUN_R0;
import static ru.m210projects.Wang.Names.GORO_RUN_R1;
import static ru.m210projects.Wang.Names.GORO_RUN_R2;
import static ru.m210projects.Wang.Names.GORO_RUN_R3;
import static ru.m210projects.Wang.Names.GORO_RUN_R4;
import static ru.m210projects.Wang.Names.GORO_SPELL_R0;
import static ru.m210projects.Wang.Names.GORO_SPELL_R1;
import static ru.m210projects.Wang.Names.GORO_SPELL_R2;
import static ru.m210projects.Wang.Names.GORO_SPELL_R3;
import static ru.m210projects.Wang.Names.GORO_SPELL_R4;
import static ru.m210projects.Wang.Names.GORO_STAND_R0;
import static ru.m210projects.Wang.Names.GORO_STAND_R1;
import static ru.m210projects.Wang.Names.GORO_STAND_R2;
import static ru.m210projects.Wang.Names.GORO_STAND_R3;
import static ru.m210projects.Wang.Names.GORO_STAND_R4;
import static ru.m210projects.Wang.Player.QueueFloorBlood;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Track.ActorFollowTrack;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.*;
import static ru.m210projects.Wang.Weapon.InitEnemyFireball;
import static ru.m210projects.Wang.Weapon.InitGoroChop;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Type.ATTRIBUTE;
import ru.m210projects.Wang.Type.Actor_Action_Set;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;

public class Goro {

	public static final int GORO_PAIN_R0 = GORO_STAND_R0;
	public static final int GORO_PAIN_R1 = GORO_STAND_R1;
	public static final int GORO_PAIN_R2 = GORO_STAND_R2;
	public static final int GORO_PAIN_R3 = GORO_STAND_R3;
	public static final int GORO_PAIN_R4 = GORO_STAND_R4;

	private static final Decision GoroBattle[] = { new Decision(697, InitActorMoveCloser),
			new Decision(700, InitActorAmbientNoise), new Decision(1024, InitActorAttack), };

	private static final Decision GoroOffense[] = { new Decision(797, InitActorMoveCloser),
			new Decision(800, InitActorAttackNoise), new Decision(1024, InitActorAttack), };

	private static final Decision GoroBroadcast[] = { new Decision(3, InitActorAmbientNoise),
			new Decision(1024, InitActorDecide), };

	private static final Decision GoroSurprised[] = { new Decision(701, InitActorMoveCloser),
			new Decision(1024, InitActorDecide), };

	private static final Decision GoroEvasive[] = { new Decision(10, InitActorEvade),
			new Decision(1024, InitActorMoveCloser), };

	private static final Decision GoroLostTarget[] = { new Decision(900, InitActorFindPlayer),
			new Decision(1024, InitActorWanderAround), };

	private static final Decision GoroCloseRange[] = { new Decision(700, InitActorAttack),
			new Decision(1024, InitActorReposition), };

	private static final Personality GoroPersonality = new Personality(GoroBattle, GoroOffense, GoroBroadcast,
			GoroSurprised, GoroEvasive, GoroLostTarget, GoroCloseRange, GoroCloseRange);

	private static final ATTRIBUTE GoroAttrib = new ATTRIBUTE(new short[] { 160, 180, 200, 230 }, // Speeds
			new short[] { 3, 0, -2, -3 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { DIGI_GRDAMBIENT, DIGI_GRDALERT, 0, DIGI_GRDPAIN, DIGI_GRDSCREAM, DIGI_GRDSWINGAXE,
					DIGI_GRDFIREBALL, 0, 0, 0 });

	//////////////////////
	//
	// GORO RUN
	//
	//////////////////////

	public static final int GORO_RUN_RATE = 18;
	private static final Animator DoGoroMove = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoGoroMove(SpriteNum) != 0;
		}
	};

	private static final State s_GoroRun[][] = {
			{ new State(GORO_RUN_R0 + 0, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove), // s_GoroRun[0][1]},
					new State(GORO_RUN_R0 + 1, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove), // s_GoroRun[0][2]},
					new State(GORO_RUN_R0 + 2, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove), // s_GoroRun[0][3]},
					new State(GORO_RUN_R0 + 3, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove),// s_GoroRun[0][0]},
			}, { new State(GORO_RUN_R1 + 0, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove), // s_GoroRun[1][1]},
					new State(GORO_RUN_R1 + 1, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove), // s_GoroRun[1][2]},
					new State(GORO_RUN_R1 + 2, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove), // s_GoroRun[1][3]},
					new State(GORO_RUN_R1 + 3, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove),// s_GoroRun[1][0]},
			}, { new State(GORO_RUN_R2 + 0, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove), // s_GoroRun[2][1]},
					new State(GORO_RUN_R2 + 1, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove), // s_GoroRun[2][2]},
					new State(GORO_RUN_R2 + 2, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove), // s_GoroRun[2][3]},
					new State(GORO_RUN_R2 + 3, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove),// s_GoroRun[2][0]},
			}, { new State(GORO_RUN_R3 + 0, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove), // s_GoroRun[3][1]},
					new State(GORO_RUN_R3 + 1, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove), // s_GoroRun[3][2]},
					new State(GORO_RUN_R3 + 2, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove), // s_GoroRun[3][3]},
					new State(GORO_RUN_R3 + 3, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove),// s_GoroRun[3][0]},
			}, { new State(GORO_RUN_R4 + 0, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove), // s_GoroRun[4][1]},
					new State(GORO_RUN_R4 + 1, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove), // s_GoroRun[4][2]},
					new State(GORO_RUN_R4 + 2, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove), // s_GoroRun[4][3]},
					new State(GORO_RUN_R4 + 3, GORO_RUN_RATE | SF_TIC_ADJUST, DoGoroMove),// s_GoroRun[4][0]},
			} };

	//////////////////////
	//
	// GORO CHOP
	//
	//////////////////////

	public static final int GORO_CHOP_RATE = 14;
	private static final Animator NullGoro = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return NullGoro(SpriteNum) != 0;
		}
	};

	private static final Animator InitGoroChop = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitGoroChop(SpriteNum) != 0;
		}
	};

	private static final State s_GoroChop[][] = { { new State(GORO_CHOP_R0 + 0, GORO_CHOP_RATE, NullGoro), // s_GoroChop[0][1]},
			new State(GORO_CHOP_R0 + 1, GORO_CHOP_RATE, NullGoro), // s_GoroChop[0][2]},
			new State(GORO_CHOP_R0 + 2, GORO_CHOP_RATE, NullGoro), // s_GoroChop[0][3]},
			new State(GORO_CHOP_R0 + 2, 0 | SF_QUICK_CALL, InitGoroChop), // s_GoroChop[0][4]},
			new State(GORO_CHOP_R0 + 2, GORO_CHOP_RATE, NullGoro), // s_GoroChop[0][5]},
			new State(GORO_CHOP_R0 + 2, 0 | SF_QUICK_CALL, InitActorDecide), // s_GoroChop[0][6]},
			new State(GORO_CHOP_R0 + 2, GORO_CHOP_RATE, DoGoroMove).setNext(),// s_GoroChop[0][6]},
			}, { new State(GORO_CHOP_R1 + 0, GORO_CHOP_RATE, NullGoro), // s_GoroChop[1][1]},
					new State(GORO_CHOP_R1 + 1, GORO_CHOP_RATE, NullGoro), // s_GoroChop[1][2]},
					new State(GORO_CHOP_R1 + 2, GORO_CHOP_RATE, NullGoro), // s_GoroChop[1][3]},
					new State(GORO_CHOP_R1 + 2, 0 | SF_QUICK_CALL, InitGoroChop), // s_GoroChop[1][4]},
					new State(GORO_CHOP_R1 + 2, GORO_CHOP_RATE, NullGoro), // s_GoroChop[1][5]},
					new State(GORO_CHOP_R1 + 2, 0 | SF_QUICK_CALL, InitActorDecide), // s_GoroChop[1][6]},
					new State(GORO_CHOP_R1 + 2, GORO_CHOP_RATE, DoGoroMove).setNext(),// s_GoroChop[1][6]},
			}, { new State(GORO_CHOP_R2 + 0, GORO_CHOP_RATE, NullGoro), // s_GoroChop[2][1]},
					new State(GORO_CHOP_R2 + 1, GORO_CHOP_RATE, NullGoro), // s_GoroChop[2][2]},
					new State(GORO_CHOP_R2 + 2, GORO_CHOP_RATE, NullGoro), // s_GoroChop[2][3]},
					new State(GORO_CHOP_R2 + 2, 0 | SF_QUICK_CALL, InitGoroChop), // s_GoroChop[2][4]},
					new State(GORO_CHOP_R2 + 2, GORO_CHOP_RATE, NullGoro), // s_GoroChop[2][5]},
					new State(GORO_CHOP_R2 + 2, 0 | SF_QUICK_CALL, InitActorDecide), // s_GoroChop[2][6]},
					new State(GORO_CHOP_R2 + 2, GORO_CHOP_RATE, DoGoroMove).setNext(),// s_GoroChop[2][6]},
			}, { new State(GORO_CHOP_R3 + 0, GORO_CHOP_RATE, NullGoro), // s_GoroChop[3][1]},
					new State(GORO_CHOP_R3 + 1, GORO_CHOP_RATE, NullGoro), // s_GoroChop[3][2]},
					new State(GORO_CHOP_R3 + 2, GORO_CHOP_RATE, NullGoro), // s_GoroChop[3][3]},
					new State(GORO_CHOP_R3 + 2, 0 | SF_QUICK_CALL, InitGoroChop), // s_GoroChop[3][4]},
					new State(GORO_CHOP_R3 + 2, GORO_CHOP_RATE, NullGoro), // s_GoroChop[3][5]},
					new State(GORO_CHOP_R3 + 2, 0 | SF_QUICK_CALL, InitActorDecide), // s_GoroChop[3][6]},
					new State(GORO_CHOP_R3 + 2, GORO_CHOP_RATE, DoGoroMove).setNext(),// s_GoroChop[3][6]},
			}, { new State(GORO_CHOP_R4 + 0, GORO_CHOP_RATE, NullGoro), // s_GoroChop[4][1]},
					new State(GORO_CHOP_R4 + 1, GORO_CHOP_RATE, NullGoro), // s_GoroChop[4][2]},
					new State(GORO_CHOP_R4 + 2, GORO_CHOP_RATE, NullGoro), // s_GoroChop[4][3]},
					new State(GORO_CHOP_R4 + 2, 0 | SF_QUICK_CALL, InitGoroChop), // s_GoroChop[4][4]},
					new State(GORO_CHOP_R4 + 2, GORO_CHOP_RATE, NullGoro), // s_GoroChop[4][5]},
					new State(GORO_CHOP_R4 + 2, 0 | SF_QUICK_CALL, InitActorDecide), // s_GoroChop[4][6]},
					new State(GORO_CHOP_R4 + 2, GORO_CHOP_RATE, DoGoroMove).setNext(),// s_GoroChop[4][6]},
			} };

	//////////////////////
	//
	// GORO SPELL
	//
	//////////////////////

	public static final int GORO_SPELL_RATE = 6;
	public static final int GORO_SPELL_PAUSE = 30;

	private static final Animator InitEnemyFireball = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitEnemyFireball(SpriteNum) != 0;
		}
	};

	private static final State s_GoroSpell[][] = { { new State(GORO_SPELL_R0 + 0, GORO_SPELL_PAUSE, NullGoro), // s_GoroSpell[0][1]},
			new State(GORO_SPELL_R0 + 1, GORO_SPELL_PAUSE, NullGoro), // s_GoroSpell[0][2]},
			new State(GORO_SPELL_R0 + 1, GORO_SPELL_RATE, InitEnemyFireball), // s_GoroSpell[0][3]},
			new State(GORO_SPELL_R0 + 1, GORO_SPELL_RATE, NullGoro), // s_GoroSpell[0][4]},
			new State(GORO_SPELL_R0 + 1, GORO_SPELL_RATE, InitEnemyFireball), // s_GoroSpell[0][5]},
			new State(GORO_SPELL_R0 + 1, GORO_SPELL_RATE, NullGoro), // s_GoroSpell[0][6]},
			new State(GORO_SPELL_R0 + 1, GORO_SPELL_RATE, InitEnemyFireball), // s_GoroSpell[0][7]},
			new State(GORO_SPELL_R0 + 1, GORO_SPELL_PAUSE, NullGoro), // s_GoroSpell[0][8]},
			new State(GORO_SPELL_R0 + 1, 0 | SF_QUICK_CALL, InitActorDecide), // s_GoroSpell[0][9]},
			new State(GORO_SPELL_R0 + 1, GORO_SPELL_RATE, DoGoroMove).setNext(),// s_GoroSpell[0][9]},
			}, { new State(GORO_SPELL_R1 + 0, GORO_SPELL_PAUSE, NullGoro), // s_GoroSpell[1][1]},
					new State(GORO_SPELL_R1 + 1, GORO_SPELL_PAUSE, NullGoro), // s_GoroSpell[1][2]},
					new State(GORO_SPELL_R1 + 1, GORO_SPELL_RATE, InitEnemyFireball), // s_GoroSpell[1][3]},
					new State(GORO_SPELL_R1 + 1, GORO_SPELL_RATE, NullGoro), // s_GoroSpell[1][4]},
					new State(GORO_SPELL_R1 + 1, GORO_SPELL_RATE, InitEnemyFireball), // s_GoroSpell[1][5]},
					new State(GORO_SPELL_R1 + 1, GORO_SPELL_RATE, NullGoro), // s_GoroSpell[1][6]},
					new State(GORO_SPELL_R1 + 1, GORO_SPELL_RATE, InitEnemyFireball), // s_GoroSpell[1][7]},
					new State(GORO_SPELL_R1 + 1, GORO_SPELL_PAUSE, NullGoro), // s_GoroSpell[1][8]},
					new State(GORO_SPELL_R1 + 1, 0 | SF_QUICK_CALL, InitActorDecide), // s_GoroSpell[1][9]},
					new State(GORO_SPELL_R1 + 1, GORO_SPELL_RATE, DoGoroMove).setNext(),// s_GoroSpell[1][9]},
			}, { new State(GORO_SPELL_R2 + 0, GORO_SPELL_PAUSE, NullGoro), // s_GoroSpell[2][1]},
					new State(GORO_SPELL_R2 + 1, GORO_SPELL_PAUSE, NullGoro), // s_GoroSpell[2][2]},
					new State(GORO_SPELL_R2 + 1, GORO_SPELL_RATE, InitEnemyFireball), // s_GoroSpell[2][3]},
					new State(GORO_SPELL_R2 + 1, GORO_SPELL_RATE, NullGoro), // s_GoroSpell[2][4]},
					new State(GORO_SPELL_R2 + 1, GORO_SPELL_RATE, InitEnemyFireball), // s_GoroSpell[2][5]},
					new State(GORO_SPELL_R2 + 1, GORO_SPELL_RATE, NullGoro), // s_GoroSpell[2][6]},
					new State(GORO_SPELL_R2 + 1, GORO_SPELL_RATE, InitEnemyFireball), // s_GoroSpell[2][7]},
					new State(GORO_SPELL_R2 + 1, GORO_SPELL_PAUSE, NullGoro), // s_GoroSpell[2][8]},
					new State(GORO_SPELL_R2 + 1, 0 | SF_QUICK_CALL, InitActorDecide), // s_GoroSpell[2][9]},
					new State(GORO_SPELL_R2 + 1, GORO_SPELL_RATE, DoGoroMove).setNext(),// s_GoroSpell[2][9]},
			}, { new State(GORO_SPELL_R3 + 0, GORO_SPELL_PAUSE, NullGoro), // s_GoroSpell[3][1]},
					new State(GORO_SPELL_R3 + 1, GORO_SPELL_PAUSE, NullGoro), // s_GoroSpell[3][2]},
					new State(GORO_SPELL_R3 + 1, GORO_SPELL_RATE, InitEnemyFireball), // s_GoroSpell[3][3]},
					new State(GORO_SPELL_R3 + 1, GORO_SPELL_RATE, NullGoro), // s_GoroSpell[3][4]},
					new State(GORO_SPELL_R3 + 1, GORO_SPELL_RATE, InitEnemyFireball), // s_GoroSpell[3][5]},
					new State(GORO_SPELL_R3 + 1, GORO_SPELL_RATE, NullGoro), // s_GoroSpell[3][6]},
					new State(GORO_SPELL_R3 + 1, GORO_SPELL_RATE, InitEnemyFireball), // s_GoroSpell[3][7]},
					new State(GORO_SPELL_R3 + 1, GORO_SPELL_PAUSE, NullGoro), // s_GoroSpell[3][8]},
					new State(GORO_SPELL_R3 + 1, 0 | SF_QUICK_CALL, InitActorDecide), // s_GoroSpell[3][9]},
					new State(GORO_SPELL_R3 + 1, GORO_SPELL_RATE, DoGoroMove).setNext(),// s_GoroSpell[3][9]},
			}, { new State(GORO_SPELL_R4 + 0, GORO_SPELL_PAUSE, NullGoro), // s_GoroSpell[4][1]},
					new State(GORO_SPELL_R4 + 1, GORO_SPELL_PAUSE, NullGoro), // s_GoroSpell[4][2]},
					new State(GORO_SPELL_R4 + 1, GORO_SPELL_RATE, InitEnemyFireball), // s_GoroSpell[4][3]},
					new State(GORO_SPELL_R4 + 1, GORO_SPELL_RATE, NullGoro), // s_GoroSpell[4][4]},
					new State(GORO_SPELL_R4 + 1, GORO_SPELL_RATE, InitEnemyFireball), // s_GoroSpell[4][5]},
					new State(GORO_SPELL_R4 + 1, GORO_SPELL_RATE, NullGoro), // s_GoroSpell[4][6]},
					new State(GORO_SPELL_R4 + 1, GORO_SPELL_RATE, InitEnemyFireball), // s_GoroSpell[4][7]},
					new State(GORO_SPELL_R4 + 1, GORO_SPELL_PAUSE, NullGoro), // s_GoroSpell[4][8]},
					new State(GORO_SPELL_R4 + 1, 0 | SF_QUICK_CALL, InitActorDecide), // s_GoroSpell[4][9]},
					new State(GORO_SPELL_R4 + 1, GORO_SPELL_RATE, DoGoroMove).setNext(),// s_GoroSpell[4][9]},
			} };

	//////////////////////
	//
	// GORO STAND
	//
	//////////////////////

	public static final int GORO_STAND_RATE = 12;

	private static final State s_GoroStand[][] = {
			{ new State(GORO_STAND_R0 + 0, GORO_STAND_RATE, DoGoroMove).setNext(),// s_GoroStand[0][0]},
			}, { new State(GORO_STAND_R1 + 0, GORO_STAND_RATE, DoGoroMove).setNext(),// s_GoroStand[1][0]},
			}, { new State(GORO_STAND_R2 + 0, GORO_STAND_RATE, DoGoroMove).setNext(),// s_GoroStand[2][0]},
			}, { new State(GORO_STAND_R3 + 0, GORO_STAND_RATE, DoGoroMove).setNext(),// s_GoroStand[3][0]},
			}, { new State(GORO_STAND_R4 + 0, GORO_STAND_RATE, DoGoroMove).setNext(),// s_GoroStand[4][0]},
			}, };

	//////////////////////
	//
	// GORO PAIN
	//
	//////////////////////

	public static final int GORO_PAIN_RATE = 12;

	private static final Animator DoGoroPain = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoGoroPain(SpriteNum) != 0;
		}
	};

	private static final State s_GoroPain[][] = { { new State(GORO_PAIN_R0 + 0, GORO_PAIN_RATE, DoGoroPain).setNext(),// s_GoroPain[0][0]},
			}, { new State(GORO_PAIN_R1 + 0, GORO_PAIN_RATE, DoGoroPain).setNext(),// s_GoroPain[1][0]},
			}, { new State(GORO_PAIN_R2 + 0, GORO_PAIN_RATE, DoGoroPain).setNext(),// s_GoroPain[2][0]},
			}, { new State(GORO_PAIN_R3 + 0, GORO_PAIN_RATE, DoGoroPain).setNext(),// s_GoroPain[3][0]},
			}, { new State(GORO_PAIN_R4 + 0, GORO_PAIN_RATE, DoGoroPain).setNext(),// s_GoroPain[4][0]},
			}, };

	//////////////////////
	//
	// GORO DIE
	//
	//////////////////////

	public static final int GORO_DIE_RATE = 16;

	private static final State s_GoroDie[] = { new State(GORO_DIE + 0, GORO_DIE_RATE, NullGoro), // s_GoroDie[1]},
			new State(GORO_DIE + 1, GORO_DIE_RATE, NullGoro), // s_GoroDie[2]},
			new State(GORO_DIE + 2, GORO_DIE_RATE, NullGoro), // s_GoroDie[3]},
			new State(GORO_DIE + 3, GORO_DIE_RATE, NullGoro), // s_GoroDie[4]},
			new State(GORO_DIE + 4, GORO_DIE_RATE, NullGoro), // s_GoroDie[5]},
			new State(GORO_DIE + 5, GORO_DIE_RATE, NullGoro), // s_GoroDie[6]},
			new State(GORO_DIE + 6, GORO_DIE_RATE, NullGoro), // s_GoroDie[7]},
			new State(GORO_DIE + 7, GORO_DIE_RATE, NullGoro), // s_GoroDie[8]},
			new State(GORO_DIE + 8, GORO_DIE_RATE, NullGoro), // s_GoroDie[9]},
			new State(GORO_DEAD, SF_QUICK_CALL, QueueFloorBlood), // s_GoroDie[10]},
			new State(GORO_DEAD, GORO_DIE_RATE, DoActorDebris).setNext(),// s_GoroDie[10]},
	};

	private static final State s_GoroDead[] = { new State(GORO_DEAD, GORO_DIE_RATE, DoActorDebris).setNext(),// s_GoroDead[0]},
	};

	public enum GoroStateGroup implements StateGroup {
		sg_GoroStand(s_GoroStand[0], s_GoroStand[1], s_GoroStand[2], s_GoroStand[3], s_GoroStand[4]),
		sg_GoroRun(s_GoroRun[0], s_GoroRun[1], s_GoroRun[2], s_GoroRun[3], s_GoroRun[4]),
		sg_GoroPain(s_GoroPain[0], s_GoroPain[1], s_GoroPain[2], s_GoroPain[3], s_GoroPain[4]), sg_GoroDie(s_GoroDie),
		sg_GoroDead(s_GoroDead), sg_GoroChop(s_GoroChop[0], s_GoroChop[1], s_GoroChop[2], s_GoroChop[3], s_GoroChop[4]),
		sg_GoroSpell(s_GoroSpell[0], s_GoroSpell[1], s_GoroSpell[2], s_GoroSpell[3], s_GoroSpell[4]);

		private final State[][] group;
		private int index = -1;

		GoroStateGroup(State[]... states) {
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
	
	public static void InitGoroStates() {
		for (GoroStateGroup sg : GoroStateGroup.values()) {
			for (int rot = 0; rot < sg.group.length; rot++) {
				State.InitState(sg.group[rot]);
			}
		}
	}

	private static final Actor_Action_Set GoroActionSet = new Actor_Action_Set(GoroStateGroup.sg_GoroStand,
			GoroStateGroup.sg_GoroRun, null, // GoroStateGroup.sg_GoroJump,
			null, // GoroStateGroup.sg_GoroFall,
			null, // GoroStateGroup.sg_GoroCrawl,
			null, // GoroStateGroup.sg_GoroSwim,
			null, // GoroStateGroup.sg_GoroFly,
			null, // GoroStateGroup.sg_GoroRise,
			null, // GoroStateGroup.sg_GoroSit,
			null, // GoroStateGroup.sg_GoroLook,
			null, // climb
			GoroStateGroup.sg_GoroPain, GoroStateGroup.sg_GoroDie, null, // GoroStateGroup.sg_GoroHariKari,
			GoroStateGroup.sg_GoroDead, null, // GoroStateGroup.sg_GoroDeathJump,
			null, // GoroStateGroup.sg_GoroDeathFall,
			new StateGroup[] { GoroStateGroup.sg_GoroChop }, new short[] { 1024 },
			new StateGroup[] { GoroStateGroup.sg_GoroSpell }, new short[] { 1024 }, null, null, null);

	public static int SetupGoro(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, GORO_RUN_R0, s_GoroRun[0][0]);
			u.Health = HEALTH_GORO;
		}

		ChangeState(SpriteNum, s_GoroRun[0][0]);
		u.Attrib = GoroAttrib;
		DoActorSetSpeed(SpriteNum, NORM_SPEED);
		u.StateEnd = s_GoroDie[0];
		u.Rot = GoroStateGroup.sg_GoroRun;

		EnemyDefaults(SpriteNum, GoroActionSet, GoroPersonality);
		sp.clipdist = 512 >> 2;
		u.Flags |= (SPR_XFLIP_TOGGLE);

		return (0);
	}

	private static int NullGoro(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		KeepActorOnFloor(SpriteNum);

		DoActorSectorDamage(SpriteNum);
		return (0);
	}

	private static int DoGoroPain(int SpriteNum) {
		USER u = pUser[SpriteNum];

		NullGoro(SpriteNum);

		if ((u.WaitTics -= ACTORMOVETICS) <= 0)
			InitActorDecide(SpriteNum);
		return (0);
	}

	private static int DoGoroMove(int SpriteNum) {
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
	
	public static void GoroSaveable()
	{
		SaveData(InitGoroChop);
		SaveData(InitEnemyFireball);
		SaveData(NullGoro);
		SaveData(DoGoroPain);
		SaveData(DoGoroMove);
		SaveData(GoroPersonality);

		SaveData(GoroAttrib);

		SaveData(s_GoroRun);
		SaveGroup(GoroStateGroup.sg_GoroRun);
		SaveData(s_GoroChop);
		SaveGroup(GoroStateGroup.sg_GoroChop);
		SaveData(s_GoroSpell);
		SaveGroup(GoroStateGroup.sg_GoroSpell);
		SaveData(s_GoroStand);
		SaveGroup(GoroStateGroup.sg_GoroStand);
		SaveData(s_GoroPain);
		SaveGroup(GoroStateGroup.sg_GoroPain);
		SaveData(s_GoroDie);
		SaveData(s_GoroDead);
		SaveGroup(GoroStateGroup.sg_GoroDie);
		SaveGroup(GoroStateGroup.sg_GoroDead);

		SaveData(GoroActionSet);
	}
}
