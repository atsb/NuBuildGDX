package ru.m210projects.Wang.Enemies;

import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Wang.Actor.DoActorDebris;
import static ru.m210projects.Wang.Actor.DoActorSectorDamage;
import static ru.m210projects.Wang.Actor.DoActorSlide;
import static ru.m210projects.Wang.Actor.DoBeginFall;
import static ru.m210projects.Wang.Actor.DoFall;
import static ru.m210projects.Wang.Ai.DoActorSetSpeed;
import static ru.m210projects.Wang.Ai.InitActorAlertNoise;
import static ru.m210projects.Wang.Ai.InitActorAmbientNoise;
import static ru.m210projects.Wang.Ai.InitActorAttack;
import static ru.m210projects.Wang.Ai.InitActorDecide;
import static ru.m210projects.Wang.Ai.InitActorFindPlayer;
import static ru.m210projects.Wang.Ai.InitActorMoveCloser;
import static ru.m210projects.Wang.Ai.InitActorReposition;
import static ru.m210projects.Wang.Ai.InitActorRunAway;
import static ru.m210projects.Wang.Ai.InitActorWanderAround;
import static ru.m210projects.Wang.Ai.NORM_SPEED;
import static ru.m210projects.Wang.Enemies.Coolie.EnemyDefaults;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_MISSILE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_RESTORE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_XFLIP;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_YFLIP;
import static ru.m210projects.Wang.Gameutils.DISTANCE;
import static ru.m210projects.Wang.Gameutils.FAF_ConnectArea;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.SF_QUICK_CALL;
import static ru.m210projects.Wang.Gameutils.SPRITEp_BOS;
import static ru.m210projects.Wang.Gameutils.SPR_FALLING;
import static ru.m210projects.Wang.Gameutils.SPR_NO_SCAREDZ;
import static ru.m210projects.Wang.Gameutils.SPR_SHADOW;
import static ru.m210projects.Wang.Gameutils.SPR_SLIDING;
import static ru.m210projects.Wang.Gameutils.SPR_XFLIP_TOGGLE;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Names.EEL_DEAD;
import static ru.m210projects.Wang.Names.EEL_DIE;
import static ru.m210projects.Wang.Names.EEL_FIRE_R0;
import static ru.m210projects.Wang.Names.EEL_FIRE_R1;
import static ru.m210projects.Wang.Names.EEL_FIRE_R2;
import static ru.m210projects.Wang.Names.EEL_RUN_R0;
import static ru.m210projects.Wang.Names.EEL_RUN_R1;
import static ru.m210projects.Wang.Names.EEL_RUN_R2;
import static ru.m210projects.Wang.Names.EEL_RUN_R3;
import static ru.m210projects.Wang.Names.EEL_RUN_R4;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.NewStateGroup;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Sprites.SpriteOverlap;
import static ru.m210projects.Wang.Sprites.move_sprite;
import static ru.m210projects.Wang.Track.ActorFollowTrack;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Weapon.DoFindGroundPoint;
import static ru.m210projects.Wang.Weapon.InitEelFire;
import static ru.m210projects.Wang.Type.Saveable.*;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Type.ATTRIBUTE;
import ru.m210projects.Wang.Type.Actor_Action_Set;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;

public class Eel {

	public enum EelStateGroup implements StateGroup {
		sg_EelRun(s_EelRun[0], s_EelRun[1], s_EelRun[2], s_EelRun[3], s_EelRun[4]),
		sg_EelStand(s_EelStand[0], s_EelStand[1], s_EelStand[2], s_EelStand[3], s_EelStand[4]), sg_EelDie(s_EelDie),
		sg_EelAttack(s_EelAttack[0], s_EelAttack[1], s_EelAttack[2], s_EelAttack[3], s_EelAttack[4]),
		sg_EelDead(s_EelDead);

		private final State[][] group;
		private int index = -1;

		EelStateGroup(State[]... states) {
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

	public static void InitEelStates() {
		for (EelStateGroup sg : EelStateGroup.values()) {
			for (int rot = 0; rot < sg.group.length; rot++) {
				State.InitState(sg.group[rot]);
			}
		}
	}

	private static final Decision EelBattle[] = { new Decision(649, InitActorMoveCloser),
			new Decision(650, InitActorAlertNoise), new Decision(1024, InitActorMoveCloser) };

	private static final Decision EelOffense[] = { new Decision(649, InitActorMoveCloser),
			new Decision(750, InitActorAlertNoise), new Decision(1024, InitActorMoveCloser) };

	private static final Decision EelBroadcast[] = { new Decision(3, InitActorAlertNoise),
			new Decision(6, InitActorAmbientNoise), new Decision(1024, InitActorDecide) };

	private static final Decision EelSurprised[] = { new Decision(701, InitActorMoveCloser),
			new Decision(1024, InitActorDecide) };

	private static final Decision EelEvasive[] = { new Decision(790, InitActorRunAway),
			new Decision(1024, InitActorMoveCloser), };

	private static final Decision EelLostTarget[] = { new Decision(900, InitActorFindPlayer),
			new Decision(1024, InitActorWanderAround) };

	private static final Decision EelCloseRange[] = { new Decision(950, InitActorAttack),
			new Decision(1024, InitActorReposition) };

	private static final Decision EelTouchTarget[] = { new Decision(1024, InitActorAttack), };

	private static final Personality EelPersonality = new Personality(EelBattle, EelOffense, EelBroadcast, EelSurprised,
			EelEvasive, EelLostTarget, EelCloseRange, EelTouchTarget);

	private static final ATTRIBUTE EelAttrib = new ATTRIBUTE(new short[] { 100, 110, 120, 130 }, // Speeds
			new short[] { 3, 0, -2, -3 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });

	//////////////////////
	// EEL RUN
	//////////////////////

	public static final int EEL_RUN_RATE = 20;

	private static final Animator DoEelMove = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoEelMove(SpriteNum) != 0;
		}
	};

	private static final State s_EelRun[][] = { { new State(EEL_RUN_R0 + 0, EEL_RUN_RATE, DoEelMove), // s_EelRun[0][1]},
			new State(EEL_RUN_R0 + 1, EEL_RUN_RATE, DoEelMove), // s_EelRun[0][2]},
			new State(EEL_RUN_R0 + 2, EEL_RUN_RATE, DoEelMove), // s_EelRun[0][3]},
			new State(EEL_RUN_R0 + 1, EEL_RUN_RATE, DoEelMove),// s_EelRun[0][0]},
			}, { new State(EEL_RUN_R1 + 0, EEL_RUN_RATE, DoEelMove), // s_EelRun[1][1]},
					new State(EEL_RUN_R1 + 1, EEL_RUN_RATE, DoEelMove), // s_EelRun[1][2]},
					new State(EEL_RUN_R1 + 2, EEL_RUN_RATE, DoEelMove), // s_EelRun[1][3]},
					new State(EEL_RUN_R1 + 1, EEL_RUN_RATE, DoEelMove),// s_EelRun[1][0]},
			}, { new State(EEL_RUN_R2 + 0, EEL_RUN_RATE, DoEelMove), // s_EelRun[2][1]},
					new State(EEL_RUN_R2 + 1, EEL_RUN_RATE, DoEelMove), // s_EelRun[2][2]},
					new State(EEL_RUN_R2 + 2, EEL_RUN_RATE, DoEelMove), // s_EelRun[2][3]},
					new State(EEL_RUN_R2 + 1, EEL_RUN_RATE, DoEelMove),// s_EelRun[2][0]},
			}, { new State(EEL_RUN_R3 + 0, EEL_RUN_RATE, DoEelMove), // s_EelRun[3][1]},
					new State(EEL_RUN_R3 + 1, EEL_RUN_RATE, DoEelMove), // s_EelRun[3][2]},
					new State(EEL_RUN_R3 + 2, EEL_RUN_RATE, DoEelMove), // s_EelRun[3][3]},
					new State(EEL_RUN_R3 + 1, EEL_RUN_RATE, DoEelMove),// s_EelRun[3][0]},
			}, { new State(EEL_RUN_R4 + 0, EEL_RUN_RATE, DoEelMove), // s_EelRun[4][1]},
					new State(EEL_RUN_R4 + 1, EEL_RUN_RATE, DoEelMove), // s_EelRun[4][2]},
					new State(EEL_RUN_R4 + 2, EEL_RUN_RATE, DoEelMove), // s_EelRun[4][3]},
					new State(EEL_RUN_R4 + 1, EEL_RUN_RATE, DoEelMove),// s_EelRun[4][0]},
			} };

	//////////////////////
	//
	// EEL STAND
	//
	//////////////////////

	private static final State s_EelStand[][] = { { new State(EEL_RUN_R0 + 0, EEL_RUN_RATE, DoEelMove).setNext(),// s_EelStand[0][0]},
			}, { new State(EEL_RUN_R1 + 0, EEL_RUN_RATE, DoEelMove).setNext(),// s_EelStand[1][0]},
			}, { new State(EEL_RUN_R2 + 0, EEL_RUN_RATE, DoEelMove).setNext(),// s_EelStand[2][0]},
			}, { new State(EEL_RUN_R3 + 0, EEL_RUN_RATE, DoEelMove).setNext(),// s_EelStand[3][0]},
			}, { new State(EEL_RUN_R4 + 0, EEL_RUN_RATE, DoEelMove).setNext(),// s_EelStand[4][0]},
			} };

	//////////////////////
	//
	// EEL FIRE
	//
	//////////////////////

	public static final int EEL_FIRE_RATE = 12;

	private static final Animator NullEel = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return NullEel(SpriteNum) != 0;
		}
	};

	private static final Animator InitEelFire = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitEelFire(SpriteNum) != 0;
		}
	};

	private static final State s_EelAttack[][] = { { new State(EEL_FIRE_R0 + 0, EEL_FIRE_RATE * 2, NullEel), // s_EelAttack[0][1]},
			new State(EEL_FIRE_R0 + 1, EEL_FIRE_RATE * 2, NullEel), // s_EelAttack[0][2]},
			new State(EEL_FIRE_R0 + 2, EEL_FIRE_RATE * 2, NullEel), // s_EelAttack[0][3]},
			new State(EEL_FIRE_R0 + 2, 0 | SF_QUICK_CALL, InitEelFire), // s_EelAttack[0][4]},
			new State(EEL_FIRE_R0 + 2, EEL_FIRE_RATE, NullEel), // s_EelAttack[0][5]},
			new State(EEL_FIRE_R0 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_EelAttack[0][6]},
			new State(EEL_RUN_R0 + 3, EEL_FIRE_RATE, DoEelMove).setNext(),// s_EelAttack[0][6]}
			}, { new State(EEL_FIRE_R1 + 0, EEL_FIRE_RATE * 2, NullEel), // s_EelAttack[1][1]},
					new State(EEL_FIRE_R1 + 1, EEL_FIRE_RATE * 2, NullEel), // s_EelAttack[1][2]},
					new State(EEL_FIRE_R1 + 2, EEL_FIRE_RATE * 2, NullEel), // s_EelAttack[1][3]},
					new State(EEL_FIRE_R1 + 2, 0 | SF_QUICK_CALL, InitEelFire), // s_EelAttack[1][5]},
					new State(EEL_FIRE_R1 + 2, EEL_FIRE_RATE, NullEel), // s_EelAttack[1][6]},
					new State(EEL_FIRE_R1 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_EelAttack[1][7]},
					new State(EEL_RUN_R0 + 3, EEL_FIRE_RATE, DoEelMove).setNext(),// s_EelAttack[1][7]}
			}, { new State(EEL_FIRE_R2 + 0, EEL_FIRE_RATE * 2, NullEel), // s_EelAttack[2][1]},
					new State(EEL_FIRE_R2 + 1, EEL_FIRE_RATE * 2, NullEel), // s_EelAttack[2][2]},
					new State(EEL_FIRE_R2 + 2, EEL_FIRE_RATE * 2, NullEel), // s_EelAttack[2][3]},
					new State(EEL_FIRE_R2 + 2, 0 | SF_QUICK_CALL, InitEelFire), // s_EelAttack[2][4]},
					new State(EEL_FIRE_R2 + 2, EEL_FIRE_RATE, NullEel), // s_EelAttack[2][5]},
					new State(EEL_FIRE_R2 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_EelAttack[2][6]},
					new State(EEL_RUN_R0 + 3, EEL_FIRE_RATE, DoEelMove).setNext(),// s_EelAttack[2][6]}
			}, { new State(EEL_RUN_R3 + 0, EEL_FIRE_RATE * 2, NullEel), // s_EelAttack[3][1]},
					new State(EEL_RUN_R3 + 1, EEL_FIRE_RATE * 2, NullEel), // s_EelAttack[3][2]},
					new State(EEL_RUN_R3 + 2, EEL_FIRE_RATE * 2, NullEel), // s_EelAttack[3][3]},
					new State(EEL_RUN_R3 + 2, 0 | SF_QUICK_CALL, InitEelFire), // s_EelAttack[3][4]},
					new State(EEL_RUN_R3 + 2, EEL_FIRE_RATE, NullEel), // s_EelAttack[3][5]},
					new State(EEL_RUN_R3 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_EelAttack[3][6]},
					new State(EEL_RUN_R0 + 3, EEL_FIRE_RATE, DoEelMove).setNext(),// s_EelAttack[3][6]}
			}, { new State(EEL_RUN_R4 + 0, EEL_FIRE_RATE * 2, NullEel), // s_EelAttack[4][1]},
					new State(EEL_RUN_R4 + 1, EEL_FIRE_RATE * 2, NullEel), // s_EelAttack[4][2]},
					new State(EEL_RUN_R4 + 2, EEL_FIRE_RATE * 2, NullEel), // s_EelAttack[4][3]},
					new State(EEL_RUN_R4 + 2, 0 | SF_QUICK_CALL, InitEelFire), // s_EelAttack[4][4]},
					new State(EEL_RUN_R4 + 2, EEL_FIRE_RATE, NullEel), // s_EelAttack[4][5]},
					new State(EEL_RUN_R4 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_EelAttack[4][6]},
					new State(EEL_RUN_R0 + 3, EEL_FIRE_RATE, DoEelMove).setNext(),// s_EelAttack[4][6]}
			} };

	//////////////////////
	//
	// EEL DIE
	//
	//////////////////////

	public static final int EEL_DIE_RATE = 20;

	private static final Animator DoEelDeath = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoEelDeath(SpriteNum) != 0;
		}
	};

	private static final State s_EelDie[] = { new State(EEL_DIE + 0, EEL_DIE_RATE, DoEelDeath), // s_EelDie[1]},
			new State(EEL_DIE + 0, EEL_DIE_RATE, DoEelDeath), // s_EelDie[2]},
			new State(EEL_DIE + 0, EEL_DIE_RATE, DoEelDeath), // s_EelDie[3]},
			new State(EEL_DIE + 0, EEL_DIE_RATE, DoEelDeath), // s_EelDie[4]},
			new State(EEL_DIE + 0, EEL_DIE_RATE, DoEelDeath), // s_EelDie[5]},
			new State(EEL_DIE + 0, EEL_DIE_RATE, DoEelDeath).setNext(),// s_EelDie[5]},
	};

	private static final State s_EelDead[] = { new State(EEL_DEAD, EEL_DIE_RATE, DoActorDebris).setNext(),// s_EelDead[0]},
	};

	private static final Actor_Action_Set EelActionSet = new Actor_Action_Set(EelStateGroup.sg_EelStand,
			EelStateGroup.sg_EelRun, null, null, null, EelStateGroup.sg_EelRun, null, null, EelStateGroup.sg_EelStand,
			null, null, // climb
			EelStateGroup.sg_EelStand, // pain
			EelStateGroup.sg_EelDie, null, EelStateGroup.sg_EelDead, null, null,
			new StateGroup[] { EelStateGroup.sg_EelAttack }, new short[] { 1024 },
			new StateGroup[] { EelStateGroup.sg_EelAttack }, new short[] { 1024 }, null, null, null);

	private static void EelCommon(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		sp.clipdist = (100) >> 2;
		u.floor_dist = (short) Z(16);
		u.floor_dist = (short) Z(16);
		u.ceiling_dist = (short) Z(20);

		u.sz = sp.z;

		sp.xrepeat = 35;
		sp.yrepeat = 27;
		u.Radius = 400;
	}

	public static int SetupEel(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, EEL_RUN_R0, s_EelRun[0][0]);
			u.Health = 40;
		}

		ChangeState(SpriteNum, s_EelRun[0][0]);
		u.Attrib = EelAttrib;
		DoActorSetSpeed(SpriteNum, NORM_SPEED);
		u.StateEnd = s_EelDie[0];
		u.Rot = EelStateGroup.sg_EelRun;

		EnemyDefaults(SpriteNum, EelActionSet, EelPersonality);

		u.Flags |= (SPR_NO_SCAREDZ | SPR_XFLIP_TOGGLE);

		EelCommon(SpriteNum);

		u.Flags &= ~(SPR_SHADOW); // Turn off shadows
		u.zclip = Z(8);

		return (0);
	}

	private static int NullEel(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		DoEelMatchPlayerZ(SpriteNum);

		DoActorSectorDamage(SpriteNum);

		return (0);
	}

	private static final int EEL_BOB_AMT = (Z(4));

	private static int DoEelMatchPlayerZ(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		SPRITE tsp = sprite[pUser[SpriteNum].tgt_sp];
		int zdiff, zdist;
		int loz, hiz;
		int dist;

		int bound;

		if (FAF_ConnectArea(sp.sectnum)) {
			if (u.hi_sectp != -1) {
				u.hiz = sector[sp.sectnum].ceilingz + Z(16);
				u.hi_sectp = sp.sectnum;
			} else {
				if (u.hiz < sector[sp.sectnum].ceilingz + Z(16))
					u.hiz = sector[sp.sectnum].ceilingz + Z(16);
			}
		}

		// actor does a sine wave about u.sz - this is the z mid point

		zdiff = (SPRITEp_BOS(tsp) - Z(8)) - u.sz;

		// check z diff of the player and the sprite
		zdist = Z(20 + RANDOM_RANGE(64)); // put a random amount
		if (klabs(zdiff) > zdist) {
			if (zdiff > 0)
				// manipulate the z midpoint
				u.sz += 160 * ACTORMOVETICS;
			else
				u.sz -= 160 * ACTORMOVETICS;
		}

		// save off lo and hi z
		loz = u.loz;
		hiz = u.hiz;

		// adjust loz/hiz for water depth
		if (u.lo_sectp != -1 && SectUser[u.lo_sectp] != null && SectUser[u.lo_sectp].depth != 0)
			loz -= Z(SectUser[u.lo_sectp].depth) - Z(8);

		// lower bound
		if (u.lo_sp != -1 && u.tgt_sp == u.hi_sp) {
			dist = DISTANCE(sp.x, sp.y, sprite[u.lo_sp].x, sprite[u.lo_sp].y);
			if (dist <= 300)
				bound = u.sz;
			else
				bound = loz - u.floor_dist;
		} else
			bound = loz - u.floor_dist - EEL_BOB_AMT;

		if (u.sz > bound) {
			u.sz = bound;
		}

		// upper bound
		if (u.hi_sp != -1 && u.tgt_sp == u.hi_sp) {
			dist = DISTANCE(sp.x, sp.y, sprite[u.hi_sp].x, sprite[u.hi_sp].y);
			if (dist <= 300)
				bound = u.sz;
			else
				bound = hiz + u.ceiling_dist;
		} else
			bound = hiz + u.ceiling_dist + EEL_BOB_AMT;

		if (u.sz < bound) {
			u.sz = bound;
		}

		u.sz = Math.min(u.sz, loz - u.floor_dist);
		u.sz = Math.max(u.sz, hiz + u.ceiling_dist);

		u.Counter = (short) ((u.Counter + (ACTORMOVETICS << 3) + (ACTORMOVETICS << 1)) & 2047);
		sp.z = u.sz + ((EEL_BOB_AMT * (int) sintable[u.Counter]) >> 14);

		bound = u.hiz + u.ceiling_dist + EEL_BOB_AMT;
		if (sp.z < bound) {
			// bumped something
			sp.z = u.sz = bound + EEL_BOB_AMT;
		}

		return (0);
	}

	private static int DoEelDeath(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		int nx, ny;
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
			if (RANDOM_RANGE(1000) > 500)
				sp.cstat |= (CSTAT_SPRITE_XFLIP);
			if (RANDOM_RANGE(1000) > 500)
				sp.cstat |= (CSTAT_SPRITE_YFLIP);
			NewStateGroup(SpriteNum, u.ActorActionSet.Dead);
			return (0);
		}

		return (0);
	}

	private static int DoEelMove(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (SpriteOverlap(SpriteNum, u.tgt_sp))
			NewStateGroup(SpriteNum, u.ActorActionSet.CloseAttack[0]);

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		if (u.track >= 0)
			ActorFollowTrack(SpriteNum, ACTORMOVETICS);
		else
			(u.ActorActionFunc).invoke(SpriteNum);

		DoEelMatchPlayerZ(SpriteNum);

		DoActorSectorDamage(SpriteNum);

		return (0);
	}
	
	public static void EelSaveable()
	{
		SaveData(NullEel);
		SaveData(InitEelFire);
		
		SaveData(DoEelDeath);
		SaveData(DoEelMove);
		SaveData(EelPersonality);
		SaveData(EelAttrib);

		SaveData(s_EelRun);
		SaveGroup(EelStateGroup.sg_EelRun);
		SaveData(s_EelStand);
		SaveGroup(EelStateGroup.sg_EelStand);
		SaveData(s_EelAttack);
		SaveGroup(EelStateGroup.sg_EelAttack);
		SaveData(s_EelDie);
		SaveGroup(EelStateGroup.sg_EelDie);
		SaveData(s_EelDead);
		SaveGroup(EelStateGroup.sg_EelDead);

		SaveData(EelActionSet);
	}
}
