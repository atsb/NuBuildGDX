package ru.m210projects.Wang.Enemies;

import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Wang.Actor.DoActorDeathMove;
import static ru.m210projects.Wang.Actor.DoActorDebris;
import static ru.m210projects.Wang.Actor.DoActorFall;
import static ru.m210projects.Wang.Actor.DoActorJump;
import static ru.m210projects.Wang.Actor.DoActorSectorDamage;
import static ru.m210projects.Wang.Actor.DoActorSlide;
import static ru.m210projects.Wang.Actor.KeepActorOnFloor;
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
import static ru.m210projects.Wang.Digi.DIGI_GIRLNINJAALERT;
import static ru.m210projects.Wang.Digi.DIGI_GIRLNINJAALERTT;
import static ru.m210projects.Wang.Digi.DIGI_GIRLNINJASCREAM;
import static ru.m210projects.Wang.Digi.DIGI_STAR;
import static ru.m210projects.Wang.Enemies.Coolie.EnemyDefaults;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_RESTORE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_TRANSLUCENT;
import static ru.m210projects.Wang.Gameutils.SF_QUICK_CALL;
import static ru.m210projects.Wang.Gameutils.SF_TIC_ADJUST;
import static ru.m210projects.Wang.Gameutils.SPR_CLIMBING;
import static ru.m210projects.Wang.Gameutils.SPR_FALLING;
import static ru.m210projects.Wang.Gameutils.SPR_JUMPING;
import static ru.m210projects.Wang.Gameutils.SPR_SLIDING;
import static ru.m210projects.Wang.Gameutils.SPR_XFLIP_TOGGLE;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Names.GIRLNINJA_CRAWL_R0;
import static ru.m210projects.Wang.Names.GIRLNINJA_CRAWL_R1;
import static ru.m210projects.Wang.Names.GIRLNINJA_CRAWL_R2;
import static ru.m210projects.Wang.Names.GIRLNINJA_CRAWL_R3;
import static ru.m210projects.Wang.Names.GIRLNINJA_CRAWL_R4;
import static ru.m210projects.Wang.Names.GIRLNINJA_DIE;
import static ru.m210projects.Wang.Names.GIRLNINJA_FIRE_R0;
import static ru.m210projects.Wang.Names.GIRLNINJA_FIRE_R1;
import static ru.m210projects.Wang.Names.GIRLNINJA_FIRE_R2;
import static ru.m210projects.Wang.Names.GIRLNINJA_FIRE_R3;
import static ru.m210projects.Wang.Names.GIRLNINJA_FIRE_R4;
import static ru.m210projects.Wang.Names.GIRLNINJA_JUMP_R0;
import static ru.m210projects.Wang.Names.GIRLNINJA_JUMP_R1;
import static ru.m210projects.Wang.Names.GIRLNINJA_JUMP_R2;
import static ru.m210projects.Wang.Names.GIRLNINJA_JUMP_R3;
import static ru.m210projects.Wang.Names.GIRLNINJA_JUMP_R4;
import static ru.m210projects.Wang.Names.GIRLNINJA_KNEEL_R0;
import static ru.m210projects.Wang.Names.GIRLNINJA_KNEEL_R1;
import static ru.m210projects.Wang.Names.GIRLNINJA_KNEEL_R2;
import static ru.m210projects.Wang.Names.GIRLNINJA_KNEEL_R3;
import static ru.m210projects.Wang.Names.GIRLNINJA_KNEEL_R4;
import static ru.m210projects.Wang.Names.GIRLNINJA_PAIN_R0;
import static ru.m210projects.Wang.Names.GIRLNINJA_PAIN_R1;
import static ru.m210projects.Wang.Names.GIRLNINJA_PAIN_R2;
import static ru.m210projects.Wang.Names.GIRLNINJA_PAIN_R3;
import static ru.m210projects.Wang.Names.GIRLNINJA_PAIN_R4;
import static ru.m210projects.Wang.Names.GIRLNINJA_RUN_R0;
import static ru.m210projects.Wang.Names.GIRLNINJA_RUN_R1;
import static ru.m210projects.Wang.Names.GIRLNINJA_RUN_R2;
import static ru.m210projects.Wang.Names.GIRLNINJA_RUN_R3;
import static ru.m210projects.Wang.Names.GIRLNINJA_RUN_R4;
import static ru.m210projects.Wang.Names.GIRLNINJA_STAND_R0;
import static ru.m210projects.Wang.Names.GIRLNINJA_STAND_R1;
import static ru.m210projects.Wang.Names.GIRLNINJA_STAND_R2;
import static ru.m210projects.Wang.Names.GIRLNINJA_STAND_R3;
import static ru.m210projects.Wang.Names.GIRLNINJA_STAND_R4;
import static ru.m210projects.Wang.Names.GIRLNINJA_THROW_R0;
import static ru.m210projects.Wang.Names.GIRLNINJA_THROW_R1;
import static ru.m210projects.Wang.Names.GIRLNINJA_THROW_R2;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER5;
import static ru.m210projects.Wang.Player.QueueFloorBlood;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Track.ActorFollowTrack;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.*;
import static ru.m210projects.Wang.Weapon.InitEnemyCrossbow;
import static ru.m210projects.Wang.Weapon.InitEnemyMine;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Type.ATTRIBUTE;
import ru.m210projects.Wang.Type.Actor_Action_Set;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;

public class GirlNinj {

	public enum GNinjaStateGroup implements StateGroup {
		sg_GirlNinjaStand(s_GirlNinjaStand[0], s_GirlNinjaStand[1], s_GirlNinjaStand[2], s_GirlNinjaStand[3],
				s_GirlNinjaStand[4]),
		sg_GirlNinjaRun(s_GirlNinjaRun[0], s_GirlNinjaRun[1], s_GirlNinjaRun[2], s_GirlNinjaRun[3], s_GirlNinjaRun[4]),
		sg_GirlNinjaJump(s_GirlNinjaJump[0], s_GirlNinjaJump[1], s_GirlNinjaJump[2], s_GirlNinjaJump[3],
				s_GirlNinjaJump[4]),
		sg_GirlNinjaFall(s_GirlNinjaFall[0], s_GirlNinjaFall[1], s_GirlNinjaFall[2], s_GirlNinjaFall[3],
				s_GirlNinjaFall[4]),
		sg_GirlNinjaRise(s_GirlNinjaRise[0], s_GirlNinjaRise[1], s_GirlNinjaRise[2], s_GirlNinjaRise[3],
				s_GirlNinjaRise[4]),
		sg_GirlNinjaSit(s_GirlNinjaSit[0], s_GirlNinjaSit[1], s_GirlNinjaSit[2], s_GirlNinjaSit[3], s_GirlNinjaSit[4]),
		sg_GirlNinjaPain(s_GirlNinjaPain[0], s_GirlNinjaPain[1], s_GirlNinjaPain[2], s_GirlNinjaPain[3],
				s_GirlNinjaPain[4]),
		sg_GirlNinjaDie(s_GirlNinjaDie), sg_GirlNinjaDead(s_GirlNinjaDead), sg_GirlNinjaDeathJump(s_GirlNinjaDeathJump),
		sg_GirlNinjaDeathFall(s_GirlNinjaDeathFall),
		sg_GirlNinjaCrossbow(s_GirlNinjaCrossbow[0], s_GirlNinjaCrossbow[1], s_GirlNinjaCrossbow[2],
				s_GirlNinjaCrossbow[3], s_GirlNinjaCrossbow[4]),
		sg_GirlNinjaSticky(s_GirlNinjaSticky[0], s_GirlNinjaSticky[1], s_GirlNinjaSticky[2], s_GirlNinjaSticky[3],
				s_GirlNinjaSticky[4]),
		sg_GirlNinjaDuck(s_GirlNinjaDuck[0], s_GirlNinjaDuck[1], s_GirlNinjaDuck[2], s_GirlNinjaDuck[3],
				s_GirlNinjaDuck[4]);

		private final State[][] group;
		private int index = -1;

		GNinjaStateGroup(State[]... states) {
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

	public static void InitGNinjaStates() {
		for (GNinjaStateGroup sg : GNinjaStateGroup.values()) {
			for (int rot = 0; rot < sg.group.length; rot++) {
				State.InitState(sg.group[rot]);

				if (sg == GNinjaStateGroup.sg_GirlNinjaRise)
					sg.group[rot][2].setNextGroup(GNinjaStateGroup.sg_GirlNinjaRun);
			}
		}
	}

	private static final Decision GirlNinjaBattle[] = { new Decision(499, InitActorMoveCloser),
			// new Decision(509, InitActorAmbientNoise),
			// new Decision(710, InitActorRunAway),
			new Decision(1024, InitActorAttack) };

	private static final Decision GirlNinjaOffense[] = { new Decision(499, InitActorMoveCloser),
			// new Decision(509, InitActorAmbientNoise),
			new Decision(1024, InitActorAttack) };

	private static final Decision GirlNinjaBroadcast[] = {
			// new Decision(1, InitActorAlertNoise),
			new Decision(6, InitActorAmbientNoise), new Decision(1024, InitActorDecide) };

	private static final Decision GirlNinjaSurprised[] = { new Decision(701, InitActorMoveCloser),
			new Decision(1024, InitActorDecide) };

	private static final Decision GirlNinjaEvasive[] = { new Decision(400, InitActorDuck), // 100
//	    new Decision(300,   InitActorEvade),
//	    new Decision(800,   InitActorRunAway),
			new Decision(1024, null) };

	private static final Decision GirlNinjaLostTarget[] = { new Decision(900, InitActorFindPlayer),
			new Decision(1024, InitActorWanderAround) };

	private static final Decision GirlNinjaCloseRange[] = { new Decision(900, InitActorAttack),
			new Decision(1024, InitActorReposition) };

	/*
	 * 
	 * !AIC - Collection of decision tables
	 * 
	 */

	private static final Personality GirlNinjaPersonality = new Personality(GirlNinjaBattle, GirlNinjaOffense,
			GirlNinjaBroadcast, GirlNinjaSurprised, GirlNinjaEvasive, GirlNinjaLostTarget, GirlNinjaCloseRange,
			GirlNinjaCloseRange);

	private static final ATTRIBUTE GirlNinjaAttrib = new ATTRIBUTE(new short[] { 120, 140, 160, 190 }, // Speeds
			new short[] { 4, 0, 0, -2 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { DIGI_GIRLNINJAALERT, DIGI_GIRLNINJAALERT, DIGI_STAR, DIGI_GIRLNINJAALERTT, DIGI_GIRLNINJASCREAM,
					0, 0, 0, 0, 0 });

	//////////////////////
	//
	// GIRLNINJA RUN
	//
	//////////////////////

	public static final int GIRLNINJA_RATE = 18;

	private static final Animator DoGirlNinjaMove = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoGirlNinjaMove(SpriteNum) != 0;
		}
	};

	private static final State s_GirlNinjaRun[][] = {

			{ new State(GIRLNINJA_RUN_R0 + 0, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove), // s_GirlNinjaRun[0][1]},
					new State(GIRLNINJA_RUN_R0 + 1, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove), // s_GirlNinjaRun[0][2]},
					new State(GIRLNINJA_RUN_R0 + 2, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove), // s_GirlNinjaRun[0][3]},
					new State(GIRLNINJA_RUN_R0 + 3, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove),// s_GirlNinjaRun[0][0]},
			}, { new State(GIRLNINJA_RUN_R1 + 0, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove), // s_GirlNinjaRun[1][1]},
					new State(GIRLNINJA_RUN_R1 + 1, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove), // s_GirlNinjaRun[1][2]},
					new State(GIRLNINJA_RUN_R1 + 2, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove), // s_GirlNinjaRun[1][3]},
					new State(GIRLNINJA_RUN_R1 + 3, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove),// s_GirlNinjaRun[1][0]},
			}, { new State(GIRLNINJA_RUN_R2 + 0, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove), // s_GirlNinjaRun[2][1]},
					new State(GIRLNINJA_RUN_R2 + 1, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove), // s_GirlNinjaRun[2][2]},
					new State(GIRLNINJA_RUN_R2 + 2, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove), // s_GirlNinjaRun[2][3]},
					new State(GIRLNINJA_RUN_R2 + 3, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove),// s_GirlNinjaRun[2][0]},
			}, { new State(GIRLNINJA_RUN_R3 + 0, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove), // s_GirlNinjaRun[3][1]},
					new State(GIRLNINJA_RUN_R3 + 1, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove), // s_GirlNinjaRun[3][2]},
					new State(GIRLNINJA_RUN_R3 + 2, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove), // s_GirlNinjaRun[3][3]},
					new State(GIRLNINJA_RUN_R3 + 3, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove),// s_GirlNinjaRun[3][0]},
			}, { new State(GIRLNINJA_RUN_R4 + 0, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove), // s_GirlNinjaRun[4][1]},
					new State(GIRLNINJA_RUN_R4 + 1, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove), // s_GirlNinjaRun[4][2]},
					new State(GIRLNINJA_RUN_R4 + 2, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove), // s_GirlNinjaRun[4][3]},
					new State(GIRLNINJA_RUN_R4 + 3, GIRLNINJA_RATE | SF_TIC_ADJUST, DoGirlNinjaMove),// s_GirlNinjaRun[4][0]},
			},

	};

	//////////////////////
	//
	// GIRLNINJA STAND
	//
	//////////////////////

	public static final int GIRLNINJA_STAND_RATE = 10;

	private static final State s_GirlNinjaStand[][] = {
			{ new State(GIRLNINJA_STAND_R0 + 0, GIRLNINJA_STAND_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaStand[0][0]},
			}, { new State(GIRLNINJA_STAND_R1 + 0, GIRLNINJA_STAND_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaStand[1][0]},
			}, { new State(GIRLNINJA_STAND_R2 + 0, GIRLNINJA_STAND_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaStand[2][0]},
			}, { new State(GIRLNINJA_STAND_R3 + 0, GIRLNINJA_STAND_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaStand[3][0]},
			}, { new State(GIRLNINJA_STAND_R4 + 0, GIRLNINJA_STAND_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaStand[4][0]},
			}, };

	//////////////////////
	//
	// GIRLNINJA RISE
	//
	//////////////////////

	public static final int GIRLNINJA_RISE_RATE = 10;

	private static final Animator NullGirlNinja = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return NullGirlNinja(SpriteNum) != 0;
		}
	};

	private static final State s_GirlNinjaRise[][] = {
			{ new State(GIRLNINJA_KNEEL_R0 + 0, GIRLNINJA_RISE_RATE, NullGirlNinja), // s_GirlNinjaRise[0][1]},
					new State(GIRLNINJA_STAND_R0 + 0, GIRLNINJA_STAND_RATE, NullGirlNinja), // s_GirlNinjaRise[0][2]},
					new State(0, 0, null), },
			{ new State(GIRLNINJA_KNEEL_R1 + 0, GIRLNINJA_RISE_RATE, NullGirlNinja), // s_GirlNinjaRise[1][1]},
					new State(GIRLNINJA_STAND_R1 + 0, GIRLNINJA_STAND_RATE, NullGirlNinja), // s_GirlNinjaRise[1][2]},
					new State(0, 0, null), },
			{ new State(GIRLNINJA_KNEEL_R2 + 0, GIRLNINJA_RISE_RATE, NullGirlNinja), // s_GirlNinjaRise[2][1]},
					new State(GIRLNINJA_STAND_R2 + 0, GIRLNINJA_STAND_RATE, NullGirlNinja), // s_GirlNinjaRise[2][2]},
					new State(0, 0, null), },
			{ new State(GIRLNINJA_KNEEL_R3 + 0, GIRLNINJA_RISE_RATE, NullGirlNinja), // s_GirlNinjaRise[3][1]},
					new State(GIRLNINJA_STAND_R3 + 0, GIRLNINJA_STAND_RATE, NullGirlNinja), // s_GirlNinjaRise[3][2]},
					new State(0, 0, null), },
			{ new State(GIRLNINJA_KNEEL_R4 + 0, GIRLNINJA_RISE_RATE, NullGirlNinja), // s_GirlNinjaRise[4][1]},
					new State(GIRLNINJA_STAND_R4 + 0, GIRLNINJA_STAND_RATE, NullGirlNinja), // s_GirlNinjaRise[4][2]},
					new State(0, 0, null), }, };

	//////////////////////
	//
	// GIRLNINJA DUCK
	//
	//////////////////////

	public static final int GIRLNINJA_DUCK_RATE = 10;
	public static final int GIRLNINJA_CRAWL_RATE = 14;

	private static final State s_GirlNinjaDuck[][] = {
			{ new State(GIRLNINJA_KNEEL_R0 + 0, GIRLNINJA_DUCK_RATE, NullGirlNinja), // s_GirlNinjaDuck[0][1]},
					new State(GIRLNINJA_CRAWL_R0 + 0, GIRLNINJA_CRAWL_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaDuck[0][1]},
			}, { new State(GIRLNINJA_KNEEL_R1 + 0, GIRLNINJA_DUCK_RATE, NullGirlNinja), // s_GirlNinjaDuck[1][1]},
					new State(GIRLNINJA_CRAWL_R1 + 0, GIRLNINJA_CRAWL_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaDuck[1][1]},
			}, { new State(GIRLNINJA_KNEEL_R2 + 0, GIRLNINJA_DUCK_RATE, NullGirlNinja), // s_GirlNinjaDuck[2][1]},
					new State(GIRLNINJA_CRAWL_R2 + 0, GIRLNINJA_CRAWL_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaDuck[2][1]},
			}, { new State(GIRLNINJA_KNEEL_R3 + 0, GIRLNINJA_DUCK_RATE, NullGirlNinja), // s_GirlNinjaDuck[3][1]},
					new State(GIRLNINJA_CRAWL_R3 + 0, GIRLNINJA_CRAWL_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaDuck[3][1]},
			}, { new State(GIRLNINJA_KNEEL_R4 + 0, GIRLNINJA_DUCK_RATE, NullGirlNinja), // s_GirlNinjaDuck[4][1]},
					new State(GIRLNINJA_CRAWL_R4 + 0, GIRLNINJA_CRAWL_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaDuck[4][1]},
			}, };

	//////////////////////
	//
	// GIRLNINJA SIT
	//
	//////////////////////

	private static final State s_GirlNinjaSit[][] = {
			{ new State(GIRLNINJA_KNEEL_R0 + 0, GIRLNINJA_RISE_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaSit[0][0]},
			}, { new State(GIRLNINJA_KNEEL_R1 + 0, GIRLNINJA_RISE_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaSit[1][0]},
			}, { new State(GIRLNINJA_KNEEL_R2 + 0, GIRLNINJA_RISE_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaSit[2][0]},
			}, { new State(GIRLNINJA_KNEEL_R3 + 0, GIRLNINJA_RISE_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaSit[3][0]},
			}, { new State(GIRLNINJA_KNEEL_R4 + 0, GIRLNINJA_RISE_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaSit[4][0]},
			}, };

	//////////////////////
	//
	// GIRLNINJA JUMP
	//
	//////////////////////

	public static final int GIRLNINJA_JUMP_RATE = 24;

	private static final State s_GirlNinjaJump[][] = {
			{ new State(GIRLNINJA_JUMP_R0 + 0, GIRLNINJA_JUMP_RATE, DoGirlNinjaMove), // s_GirlNinjaJump[0][1]},
					new State(GIRLNINJA_JUMP_R0 + 1, GIRLNINJA_JUMP_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaJump[0][1]},
			}, { new State(GIRLNINJA_JUMP_R1 + 0, GIRLNINJA_JUMP_RATE, DoGirlNinjaMove), // s_GirlNinjaJump[1][1]},
					new State(GIRLNINJA_JUMP_R1 + 1, GIRLNINJA_JUMP_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaJump[1][1]},
			}, { new State(GIRLNINJA_JUMP_R2 + 0, GIRLNINJA_JUMP_RATE, DoGirlNinjaMove), // s_GirlNinjaJump[2][1]},
					new State(GIRLNINJA_JUMP_R2 + 1, GIRLNINJA_JUMP_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaJump[2][1]},
			}, { new State(GIRLNINJA_JUMP_R3 + 0, GIRLNINJA_JUMP_RATE, DoGirlNinjaMove), // s_GirlNinjaJump[3][1]},
					new State(GIRLNINJA_JUMP_R3 + 1, GIRLNINJA_JUMP_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaJump[3][1]},
			}, { new State(GIRLNINJA_JUMP_R4 + 0, GIRLNINJA_JUMP_RATE, DoGirlNinjaMove), // s_GirlNinjaJump[4][1]},
					new State(GIRLNINJA_JUMP_R4 + 1, GIRLNINJA_JUMP_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaJump[4][1]},
			}, };

	//////////////////////
	//
	// GIRLNINJA FALL
	//
	//////////////////////

	public static final int GIRLNINJA_FALL_RATE = 16;

	private static final State s_GirlNinjaFall[][] = {
			{ new State(GIRLNINJA_JUMP_R0 + 1, GIRLNINJA_FALL_RATE, DoGirlNinjaMove), // s_GirlNinjaFall[0][1]},
					new State(GIRLNINJA_JUMP_R0 + 2, GIRLNINJA_FALL_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaFall[0][1]},
			}, { new State(GIRLNINJA_JUMP_R1 + 1, GIRLNINJA_FALL_RATE, DoGirlNinjaMove), // s_GirlNinjaFall[1][1]},
					new State(GIRLNINJA_JUMP_R1 + 2, GIRLNINJA_FALL_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaFall[1][1]},
			}, { new State(GIRLNINJA_JUMP_R2 + 1, GIRLNINJA_FALL_RATE, DoGirlNinjaMove), // s_GirlNinjaFall[2][1]},
					new State(GIRLNINJA_JUMP_R2 + 2, GIRLNINJA_FALL_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaFall[2][1]},
			}, { new State(GIRLNINJA_JUMP_R3 + 1, GIRLNINJA_FALL_RATE, DoGirlNinjaMove), // s_GirlNinjaFall[3][1]},
					new State(GIRLNINJA_JUMP_R3 + 2, GIRLNINJA_FALL_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaFall[3][1]},
			}, { new State(GIRLNINJA_JUMP_R4 + 1, GIRLNINJA_FALL_RATE, DoGirlNinjaMove), // s_GirlNinjaFall[4][1]},
					new State(GIRLNINJA_JUMP_R4 + 2, GIRLNINJA_FALL_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaFall[4][1]},
			}, };

	//////////////////////
	//
	// GIRLNINJA PAIN
	//
	//////////////////////

	public static final int GIRLNINJA_PAIN_RATE = 15;

	private static final Animator DoGirlNinjaPain = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoGirlNinjaPain(SpriteNum) != 0;
		}
	};

	private static final State s_GirlNinjaPain[][] = {
			{ new State(GIRLNINJA_PAIN_R0 + 0, GIRLNINJA_PAIN_RATE, DoGirlNinjaPain).setNext(),// s_GirlNinjaPain[0][0]},
			}, { new State(GIRLNINJA_PAIN_R1 + 0, GIRLNINJA_PAIN_RATE, DoGirlNinjaPain).setNext(),// s_GirlNinjaPain[1][0]},
			}, { new State(GIRLNINJA_PAIN_R2 + 0, GIRLNINJA_PAIN_RATE, DoGirlNinjaPain).setNext(),// s_GirlNinjaPain[2][0]},
			}, { new State(GIRLNINJA_PAIN_R3 + 0, GIRLNINJA_PAIN_RATE, DoGirlNinjaPain).setNext(),// s_GirlNinjaPain[3][0]},
			}, { new State(GIRLNINJA_PAIN_R4 + 0, GIRLNINJA_PAIN_RATE, DoGirlNinjaPain).setNext(),// s_GirlNinjaPain[4][0]},
			}, };

	//////////////////////
	//
	// GIRLNINJA STICKY
	//
	//////////////////////

	public static final int GIRLNINJA_STICKY_RATE = 32;

	private static final Animator InitEnemyMine = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitEnemyMine(SpriteNum) != 0;
		}
	};

	private static final State s_GirlNinjaSticky[][] = {
			{ new State(GIRLNINJA_THROW_R0 + 0, GIRLNINJA_STICKY_RATE * 2, NullGirlNinja), // s_GirlNinjaSticky[0][1]},
					new State(GIRLNINJA_THROW_R0 + 0, GIRLNINJA_STICKY_RATE, NullGirlNinja), // s_GirlNinjaSticky[0][2]},
					new State(GIRLNINJA_THROW_R0 + 1, 0 | SF_QUICK_CALL, InitEnemyMine), // s_GirlNinjaSticky[0][3]},
					new State(GIRLNINJA_THROW_R0 + 1, GIRLNINJA_STICKY_RATE * 2, NullGirlNinja), // s_GirlNinjaSticky[0][4]},
					new State(GIRLNINJA_THROW_R0 + 2, 0 | SF_QUICK_CALL, InitActorDecide), // s_GirlNinjaSticky[0][5]},
					new State(GIRLNINJA_THROW_R0 + 2, GIRLNINJA_STICKY_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaSticky[0][5]},
			}, { new State(GIRLNINJA_THROW_R1 + 0, GIRLNINJA_STICKY_RATE * 2, NullGirlNinja), // s_GirlNinjaSticky[1][1]},
					new State(GIRLNINJA_THROW_R1 + 0, GIRLNINJA_STICKY_RATE, NullGirlNinja), // s_GirlNinjaSticky[1][2]},
					new State(GIRLNINJA_THROW_R1 + 1, 0 | SF_QUICK_CALL, InitEnemyMine), // s_GirlNinjaSticky[1][3]},
					new State(GIRLNINJA_THROW_R1 + 1, GIRLNINJA_STICKY_RATE * 2, NullGirlNinja), // s_GirlNinjaSticky[1][4]},
					new State(GIRLNINJA_THROW_R1 + 2, 0 | SF_QUICK_CALL, InitActorDecide), // s_GirlNinjaSticky[1][5]},
					new State(GIRLNINJA_THROW_R1 + 2, GIRLNINJA_STICKY_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaSticky[1][5]},
			}, { new State(GIRLNINJA_THROW_R2 + 0, GIRLNINJA_STICKY_RATE * 2, NullGirlNinja), // s_GirlNinjaSticky[2][1]},
					new State(GIRLNINJA_THROW_R2 + 0, GIRLNINJA_STICKY_RATE, NullGirlNinja), // s_GirlNinjaSticky[2][2]},
					new State(GIRLNINJA_THROW_R2 + 1, 0 | SF_QUICK_CALL, InitEnemyMine), // s_GirlNinjaSticky[2][3]},
					new State(GIRLNINJA_THROW_R2 + 1, GIRLNINJA_STICKY_RATE * 2, NullGirlNinja), // s_GirlNinjaSticky[2][4]},
					new State(GIRLNINJA_THROW_R2 + 2, 0 | SF_QUICK_CALL, InitActorDecide), // s_GirlNinjaSticky[2][5]},
					new State(GIRLNINJA_THROW_R2 + 2, GIRLNINJA_STICKY_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaSticky[2][5]},
			}, { new State(GIRLNINJA_THROW_R2 + 0, GIRLNINJA_STICKY_RATE * 2, NullGirlNinja), // s_GirlNinjaSticky[3][1]},
					new State(GIRLNINJA_THROW_R2 + 0, GIRLNINJA_STICKY_RATE, NullGirlNinja), // s_GirlNinjaSticky[3][2]},
					new State(GIRLNINJA_THROW_R2 + 1, 0 | SF_QUICK_CALL, InitEnemyMine), // s_GirlNinjaSticky[3][3]},
					new State(GIRLNINJA_THROW_R2 + 1, GIRLNINJA_STICKY_RATE * 2, NullGirlNinja), // s_GirlNinjaSticky[3][4]},
					new State(GIRLNINJA_THROW_R2 + 2, 0 | SF_QUICK_CALL, InitActorDecide), // s_GirlNinjaSticky[3][5]},
					new State(GIRLNINJA_THROW_R2 + 2, GIRLNINJA_STICKY_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaSticky[3][5]},
			}, { new State(GIRLNINJA_THROW_R2 + 0, GIRLNINJA_STICKY_RATE * 2, NullGirlNinja), // s_GirlNinjaSticky[4][1]},
					new State(GIRLNINJA_THROW_R2 + 0, GIRLNINJA_STICKY_RATE, NullGirlNinja), // s_GirlNinjaSticky[4][2]},
					new State(GIRLNINJA_THROW_R2 + 1, 0 | SF_QUICK_CALL, InitEnemyMine), // s_GirlNinjaSticky[4][3]},
					new State(GIRLNINJA_THROW_R2 + 1, GIRLNINJA_STICKY_RATE * 2, NullGirlNinja), // s_GirlNinjaSticky[4][4]},
					new State(GIRLNINJA_THROW_R2 + 2, 0 | SF_QUICK_CALL, InitActorDecide), // s_GirlNinjaSticky[4][5]},
					new State(GIRLNINJA_THROW_R2 + 2, GIRLNINJA_STICKY_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaSticky[4][5]},
			}, };

	//////////////////////
	//
	// GIRLNINJA CROSSBOW
	//
	//////////////////////

	public static final int GIRLNINJA_CROSSBOW_RATE = 14;

	private static final Animator InitEnemyCrossbow = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitEnemyCrossbow(SpriteNum) != 0;
		}
	};

	private static final State s_GirlNinjaCrossbow[][] = {
			{ new State(GIRLNINJA_FIRE_R0 + 0, GIRLNINJA_CROSSBOW_RATE * 2, NullGirlNinja), // s_GirlNinjaCrossbow[0][1]},
					new State(GIRLNINJA_FIRE_R0 + 1, 0 | SF_QUICK_CALL, InitEnemyCrossbow), // s_GirlNinjaCrossbow[0][2]},
					new State(GIRLNINJA_FIRE_R0 + 1, GIRLNINJA_CROSSBOW_RATE, NullGirlNinja), // s_GirlNinjaCrossbow[0][3]},
					new State(GIRLNINJA_FIRE_R0 + 1, 0 | SF_QUICK_CALL, InitActorDecide), // s_GirlNinjaCrossbow[0][4]},
					new State(GIRLNINJA_FIRE_R0 + 1, GIRLNINJA_CROSSBOW_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaCrossbow[0][4]},
			}, { new State(GIRLNINJA_FIRE_R1 + 0, GIRLNINJA_CROSSBOW_RATE * 2, NullGirlNinja), // s_GirlNinjaCrossbow[1][1]},
					new State(GIRLNINJA_FIRE_R1 + 1, 0 | SF_QUICK_CALL, InitEnemyCrossbow), // s_GirlNinjaCrossbow[1][2]},
					new State(GIRLNINJA_FIRE_R1 + 1, GIRLNINJA_CROSSBOW_RATE, NullGirlNinja), // s_GirlNinjaCrossbow[1][3]},
					new State(GIRLNINJA_FIRE_R1 + 1, 0 | SF_QUICK_CALL, InitActorDecide), // s_GirlNinjaCrossbow[1][4]},
					new State(GIRLNINJA_FIRE_R1 + 1, GIRLNINJA_CROSSBOW_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaCrossbow[1][4]},
			}, { new State(GIRLNINJA_FIRE_R2 + 0, GIRLNINJA_CROSSBOW_RATE * 2, NullGirlNinja), // s_GirlNinjaCrossbow[2][1]},
					new State(GIRLNINJA_FIRE_R2 + 1, 0 | SF_QUICK_CALL, InitEnemyCrossbow), // s_GirlNinjaCrossbow[2][2]},
					new State(GIRLNINJA_FIRE_R2 + 1, GIRLNINJA_CROSSBOW_RATE, NullGirlNinja), // s_GirlNinjaCrossbow[2][3]},
					new State(GIRLNINJA_FIRE_R2 + 1, 0 | SF_QUICK_CALL, InitActorDecide), // s_GirlNinjaCrossbow[2][4]},
					new State(GIRLNINJA_FIRE_R2 + 1, GIRLNINJA_CROSSBOW_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaCrossbow[2][4]},
			}, { new State(GIRLNINJA_FIRE_R3 + 0, GIRLNINJA_CROSSBOW_RATE * 2, NullGirlNinja), // s_GirlNinjaCrossbow[3][1]},
					new State(GIRLNINJA_FIRE_R3 + 1, 0 | SF_QUICK_CALL, InitEnemyCrossbow), // s_GirlNinjaCrossbow[3][2]},
					new State(GIRLNINJA_FIRE_R3 + 1, GIRLNINJA_CROSSBOW_RATE, NullGirlNinja), // s_GirlNinjaCrossbow[3][3]},
					new State(GIRLNINJA_FIRE_R3 + 1, 0 | SF_QUICK_CALL, InitActorDecide), // s_GirlNinjaCrossbow[3][4]},
					new State(GIRLNINJA_FIRE_R3 + 1, GIRLNINJA_CROSSBOW_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaCrossbow[3][4]},
			}, { new State(GIRLNINJA_FIRE_R4 + 0, GIRLNINJA_CROSSBOW_RATE * 2, NullGirlNinja), // s_GirlNinjaCrossbow[4][1]},
					new State(GIRLNINJA_FIRE_R4 + 1, 0 | SF_QUICK_CALL, InitEnemyCrossbow), // s_GirlNinjaCrossbow[4][2]},
					new State(GIRLNINJA_FIRE_R4 + 1, GIRLNINJA_CROSSBOW_RATE, NullGirlNinja), // s_GirlNinjaCrossbow[4][3]},
					new State(GIRLNINJA_FIRE_R4 + 1, 0 | SF_QUICK_CALL, InitActorDecide), // s_GirlNinjaCrossbow[4][4]},
					new State(GIRLNINJA_FIRE_R4 + 1, GIRLNINJA_CROSSBOW_RATE, DoGirlNinjaMove).setNext(),// s_GirlNinjaCrossbow[4][4]},
			}, };

	//////////////////////
	//
	// GIRLNINJA DIE
	//
	//////////////////////

	public static final int GIRLNINJA_DIE_RATE = 30;

	private static final Animator DoGirlNinjaSpecial = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoGirlNinjaSpecial(SpriteNum) != 0;
		}
	};

	private static final State s_GirlNinjaDie[] = { new State(GIRLNINJA_DIE + 0, GIRLNINJA_DIE_RATE * 2, NullGirlNinja), // s_GirlNinjaDie[1]},
			new State(GIRLNINJA_DIE + 1, GIRLNINJA_DIE_RATE, NullGirlNinja), // s_GirlNinjaDie[2]},
			new State(GIRLNINJA_DIE + 2, GIRLNINJA_DIE_RATE, NullGirlNinja), // s_GirlNinjaDie[3]},
			new State(GIRLNINJA_DIE + 3, GIRLNINJA_DIE_RATE, NullGirlNinja), // s_GirlNinjaDie[4]},
			new State(GIRLNINJA_DIE + 4, GIRLNINJA_DIE_RATE, NullGirlNinja), // s_GirlNinjaDie[5]},
			new State(GIRLNINJA_DIE + 5, GIRLNINJA_DIE_RATE, NullGirlNinja), // s_GirlNinjaDie[6]},
			new State(GIRLNINJA_DIE + 6, GIRLNINJA_DIE_RATE, NullGirlNinja), // s_GirlNinjaDie[7]},
			new State(GIRLNINJA_DIE + 6, SF_QUICK_CALL, DoGirlNinjaSpecial), // s_GirlNinjaDie[8]},
			new State(GIRLNINJA_DIE + 7, GIRLNINJA_DIE_RATE, NullGirlNinja), // s_GirlNinjaDie[9]},
			new State(GIRLNINJA_DIE + 8, SF_QUICK_CALL, QueueFloorBlood), // s_GirlNinjaDie[10]},
			new State(GIRLNINJA_DIE + 8, GIRLNINJA_DIE_RATE, DoActorDebris).setNext(),// s_GirlNinjaDie[10]},
	};

	private static final State s_GirlNinjaDead[] = { new State(GIRLNINJA_DIE + 6, GIRLNINJA_DIE_RATE, DoActorDebris), // s_GirlNinjaDead[1]},
			new State(GIRLNINJA_DIE + 7, SF_QUICK_CALL, DoGirlNinjaSpecial), // s_GirlNinjaDead[2]},
			new State(GIRLNINJA_DIE + 7, GIRLNINJA_DIE_RATE, DoActorDebris), // s_GirlNinjaDead[3]},
			new State(GIRLNINJA_DIE + 8, SF_QUICK_CALL, QueueFloorBlood), // s_GirlNinjaDead[4]},
			new State(GIRLNINJA_DIE + 8, GIRLNINJA_DIE_RATE, DoActorDebris).setNext(),// s_GirlNinjaDead[4]},
	};

	private static final Animator DoActorDeathMove = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoActorDeathMove(SpriteNum) != 0;
		}
	};

	private static final State s_GirlNinjaDeathJump[] = {
			new State(GIRLNINJA_DIE + 0, GIRLNINJA_DIE_RATE, DoActorDeathMove), // s_GirlNinjaDeathJump[1]},
			new State(GIRLNINJA_DIE + 1, GIRLNINJA_DIE_RATE, DoActorDeathMove), // s_GirlNinjaDeathJump[2]},
			new State(GIRLNINJA_DIE + 2, GIRLNINJA_DIE_RATE, DoActorDeathMove).setNext(),// s_GirlNinjaDeathJump[2]},
	};

	private static final State s_GirlNinjaDeathFall[] = {
			new State(GIRLNINJA_DIE + 3, GIRLNINJA_DIE_RATE, DoActorDeathMove), // s_GirlNinjaDeathFall[1]},
			new State(GIRLNINJA_DIE + 4, GIRLNINJA_DIE_RATE, DoActorDeathMove).setNext(),// s_GirlNinjaDeathFall[1]},
	};

	/*
	 * 
	 * !AIC - Collection of private static final States that connect action to
	 * private static final States
	 * 
	 */

	private static final Actor_Action_Set GirlNinjaActionSet = new Actor_Action_Set(GNinjaStateGroup.sg_GirlNinjaStand,
			GNinjaStateGroup.sg_GirlNinjaRun, GNinjaStateGroup.sg_GirlNinjaJump, GNinjaStateGroup.sg_GirlNinjaFall,
			null, null, null, GNinjaStateGroup.sg_GirlNinjaRise, GNinjaStateGroup.sg_GirlNinjaSit, null, null,
			GNinjaStateGroup.sg_GirlNinjaPain, GNinjaStateGroup.sg_GirlNinjaDie, null,
			GNinjaStateGroup.sg_GirlNinjaDead, GNinjaStateGroup.sg_GirlNinjaDeathJump,
			GNinjaStateGroup.sg_GirlNinjaDeathFall,
			new StateGroup[] { GNinjaStateGroup.sg_GirlNinjaCrossbow, GNinjaStateGroup.sg_GirlNinjaSticky },
			new short[] { 800, 1024 },
			new StateGroup[] { GNinjaStateGroup.sg_GirlNinjaCrossbow, GNinjaStateGroup.sg_GirlNinjaSticky },
			new short[] { 800, 1024 }, null, GNinjaStateGroup.sg_GirlNinjaDuck, null);

	public static int SetupGirlNinja(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, GIRLNINJA_RUN_R0, s_GirlNinjaRun[0][0]);
			u.Health = 100;
		}

		u.StateEnd = s_GirlNinjaDie[0];
		u.Rot = GNinjaStateGroup.sg_GirlNinjaRun;
		sp.xrepeat = 51;
		sp.yrepeat = 43;

		u.Attrib = GirlNinjaAttrib;
		sp.pal = u.spal = 26;
		EnemyDefaults(SpriteNum, GirlNinjaActionSet, GirlNinjaPersonality);

		ChangeState(SpriteNum, s_GirlNinjaRun[0][0]);
		DoActorSetSpeed(SpriteNum, NORM_SPEED);

		u.Radius = 280;
		u.Flags &= ~(SPR_XFLIP_TOGGLE);

		return (0);
	}

	private static int DoGirlNinjaMove(int SpriteNum) {
		USER u = pUser[SpriteNum];

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
			(u.ActorActionFunc).invoke(SpriteNum);
		}

		// stay on floor unless doing certain things
		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING | SPR_CLIMBING)) {
			KeepActorOnFloor(SpriteNum);
		}

		// take damage from environment
		DoActorSectorDamage(SpriteNum);

		return (0);
	}

	private static int NullGirlNinja(int SpriteNum) {
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

	private static int DoGirlNinjaPain(int SpriteNum) {
		USER u = pUser[SpriteNum];

		NullGirlNinja(SpriteNum);

		if ((u.WaitTics -= ACTORMOVETICS) <= 0)
			InitActorDecide(SpriteNum);

		return (0);
	}

	private static int DoGirlNinjaSpecial(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if (u.spal == PALETTE_PLAYER5) {
			sp.cstat &= ~(CSTAT_SPRITE_TRANSLUCENT);
			sp.hitag = 0;
			sp.shade = -10;
		}

		return (0);
	}

	public static void GirlNinjSaveable() {
		
		SaveData(InitEnemyMine);
		SaveData(InitEnemyCrossbow);
		SaveData(DoActorDeathMove);

		SaveData(DoGirlNinjaMove);
		SaveData(NullGirlNinja);
		SaveData(DoGirlNinjaPain);
		SaveData(DoGirlNinjaSpecial);
		SaveData(GirlNinjaPersonality);

		SaveData(GirlNinjaAttrib);

		SaveData(s_GirlNinjaRun);
		SaveGroup(GNinjaStateGroup.sg_GirlNinjaRun);
		SaveData(s_GirlNinjaStand);
		SaveGroup(GNinjaStateGroup.sg_GirlNinjaStand);
		SaveData(s_GirlNinjaRise);
		SaveGroup(GNinjaStateGroup.sg_GirlNinjaRise);
		SaveData(s_GirlNinjaDuck);
		SaveGroup(GNinjaStateGroup.sg_GirlNinjaDuck);
		SaveData(s_GirlNinjaSit);
		SaveGroup(GNinjaStateGroup.sg_GirlNinjaSit);
		SaveData(s_GirlNinjaJump);
		SaveGroup(GNinjaStateGroup.sg_GirlNinjaJump);
		SaveData(s_GirlNinjaFall);
		SaveGroup(GNinjaStateGroup.sg_GirlNinjaFall);
		SaveData(s_GirlNinjaPain);
		SaveGroup(GNinjaStateGroup.sg_GirlNinjaPain);
		SaveData(s_GirlNinjaSticky);
		SaveGroup(GNinjaStateGroup.sg_GirlNinjaSticky);
		SaveData(s_GirlNinjaCrossbow);
		SaveGroup(GNinjaStateGroup.sg_GirlNinjaCrossbow);
		SaveData(s_GirlNinjaDie);
		SaveData(s_GirlNinjaDead);
		SaveData(s_GirlNinjaDeathJump);
		SaveData(s_GirlNinjaDeathFall);

		SaveData(GirlNinjaActionSet);
	}

}
