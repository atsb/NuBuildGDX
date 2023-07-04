package ru.m210projects.Wang.Enemies;

import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Wang.Actor.DoActorDebris;
import static ru.m210projects.Wang.Actor.DoActorSectorDamage;
import static ru.m210projects.Wang.Actor.DoActorSlide;
import static ru.m210projects.Wang.Actor.DoBeginFall;
import static ru.m210projects.Wang.Actor.DoFall;
import static ru.m210projects.Wang.Ai.DoActorPickClosePlayer;
import static ru.m210projects.Wang.Ai.DoActorSetSpeed;
import static ru.m210projects.Wang.Ai.FAST_SPEED;
import static ru.m210projects.Wang.Ai.InitActorAlertNoise;
import static ru.m210projects.Wang.Ai.InitActorAmbientNoise;
import static ru.m210projects.Wang.Ai.InitActorDecide;
import static ru.m210projects.Wang.Ai.InitActorFindPlayer;
import static ru.m210projects.Wang.Ai.InitActorMoveCloser;
import static ru.m210projects.Wang.Ai.InitActorReposition;
import static ru.m210projects.Wang.Ai.InitActorRunAway;
import static ru.m210projects.Wang.Ai.InitActorWanderAround;
import static ru.m210projects.Wang.Ai.NORM_SPEED;
import static ru.m210projects.Wang.Digi.DIGI_HORNETBUZZ;
import static ru.m210projects.Wang.Digi.DIGI_HORNETDEATH;
import static ru.m210projects.Wang.Digi.DIGI_HORNETSTING;
import static ru.m210projects.Wang.Enemies.Coolie.EnemyDefaults;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_RESTORE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_YCENTER;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_YFLIP;
import static ru.m210projects.Wang.Gameutils.DISTANCE;
import static ru.m210projects.Wang.Gameutils.HEALTH_HORNET;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.SPRITEp_MID;
import static ru.m210projects.Wang.Gameutils.SPR_FALLING;
import static ru.m210projects.Wang.Gameutils.SPR_NO_SCAREDZ;
import static ru.m210projects.Wang.Gameutils.SPR_SLIDING;
import static ru.m210projects.Wang.Gameutils.SPR_XFLIP_TOGGLE;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.JTags.TAG_SWARMSPOT;
import static ru.m210projects.Wang.Names.HORNET_DEAD;
import static ru.m210projects.Wang.Names.HORNET_DIE;
import static ru.m210projects.Wang.Names.HORNET_RUN_R0;
import static ru.m210projects.Wang.Names.HORNET_RUN_R1;
import static ru.m210projects.Wang.Names.HORNET_RUN_R2;
import static ru.m210projects.Wang.Names.HORNET_RUN_R3;
import static ru.m210projects.Wang.Names.HORNET_RUN_R4;
import static ru.m210projects.Wang.Names.STAT_ENEMY;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Sound.DeleteNoSoundOwner;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.Set3DSoundOwner;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sound.v3df_init;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.MoveSkip8;
import static ru.m210projects.Wang.Sprites.NewStateGroup;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Sprites.move_actor;
import static ru.m210projects.Wang.Sprites.move_sprite;
import static ru.m210projects.Wang.Track.ActorFollowTrack;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.MyTypes.TRUE;
import static ru.m210projects.Wang.Type.Saveable.*;
import static ru.m210projects.Wang.Weapon.DoFindGroundPoint;
import static ru.m210projects.Wang.Weapon.InitHornetSting;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Type.ATTRIBUTE;
import ru.m210projects.Wang.Type.Actor_Action_Set;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;
import ru.m210projects.Wang.Type.VOC3D;

public class Hornet {

	public enum HornetStateGroup implements StateGroup {
		sg_HornetStand(s_HornetStand[0], s_HornetStand[1], s_HornetStand[2], s_HornetStand[3], s_HornetStand[4]),
		sg_HornetRun(s_HornetRun[0], s_HornetRun[1], s_HornetRun[2], s_HornetRun[3], s_HornetRun[4]),
		sg_HornetDie(s_HornetDie), sg_HornetDead(s_HornetDead);

		private final State[][] group;
		private int index = -1;

		HornetStateGroup(State[]... states) {
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

	public static void InitHornetStates() {
		for (HornetStateGroup sg : HornetStateGroup.values()) {
			for (int rot = 0; rot < sg.group.length; rot++) {
				State.InitState(sg.group[rot]);
			}
		}
	}

	private static final Animator InitHornetSting = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitHornetSting(SpriteNum) != 0;
		}
	};

	private static final Animator InitHornetCircle = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitHornetCircle(SpriteNum) != 0;
		}
	};

	private static final Decision HornetBattle[] = { new Decision(50, InitHornetCircle),
			new Decision(798, InitActorMoveCloser), new Decision(800, InitActorAlertNoise),
			new Decision(1024, InitActorRunAway) };

	private static final Decision HornetOffense[] = { new Decision(1022, InitActorMoveCloser),
			new Decision(1024, InitActorAlertNoise) };

	private static final Decision HornetBroadcast[] = { new Decision(3, InitActorAlertNoise),
			new Decision(6, InitActorAmbientNoise), new Decision(1024, InitActorDecide) };

	private static final Decision HornetSurprised[] = { new Decision(100, InitHornetCircle),
			new Decision(701, InitActorMoveCloser), new Decision(1024, InitActorDecide) };

	private static final Decision HornetEvasive[] = { new Decision(20, InitHornetCircle), new Decision(1024, null), };

	private static final Decision HornetLostTarget[] = { new Decision(900, InitActorFindPlayer),
			new Decision(1024, InitActorWanderAround) };

	private static final Decision HornetCloseRange[] = { new Decision(900, InitActorMoveCloser),
			new Decision(1024, InitActorReposition) };

	private static final Decision HornetTouchTarget[] = { new Decision(500, InitHornetCircle),
			new Decision(1024, InitHornetSting) };

	private static final Personality HornetPersonality = new Personality(HornetBattle, HornetOffense, HornetBroadcast,
			HornetSurprised, HornetEvasive, HornetLostTarget, HornetCloseRange, HornetTouchTarget);

	private static final ATTRIBUTE HornetAttrib = new ATTRIBUTE(new short[] { 300, 350, 375, 400 }, // Speeds
			new short[] { 0, 0, 0, 0 }, // Tic Adjusts
			0, // MaxWeapons;
			new int[] { 0, 0, DIGI_HORNETSTING, DIGI_HORNETSTING, DIGI_HORNETDEATH, 0, 0, 0, 0, 0 });

	//////////////////////
	//
	// HORNET RUN
	//////////////////////

	public static final int HORNET_RUN_RATE = 7;

	private static final Animator DoHornetMove = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoHornetMove(SpriteNum) != 0;
		}
	};

	private static final State s_HornetRun[][] = { { new State(HORNET_RUN_R0 + 0, HORNET_RUN_RATE, DoHornetMove), // s_HornetRun[0][1]},
			new State(HORNET_RUN_R0 + 1, HORNET_RUN_RATE, DoHornetMove),// s_HornetRun[0][0]},
			}, { new State(HORNET_RUN_R1 + 0, HORNET_RUN_RATE, DoHornetMove), // s_HornetRun[1][1]},
					new State(HORNET_RUN_R1 + 1, HORNET_RUN_RATE, DoHornetMove),// s_HornetRun[1][0]},
			}, { new State(HORNET_RUN_R2 + 0, HORNET_RUN_RATE, DoHornetMove), // s_HornetRun[2][1]},
					new State(HORNET_RUN_R2 + 1, HORNET_RUN_RATE, DoHornetMove),// s_HornetRun[2][0]},
			}, { new State(HORNET_RUN_R3 + 0, HORNET_RUN_RATE, DoHornetMove), // s_HornetRun[3][1]},
					new State(HORNET_RUN_R3 + 1, HORNET_RUN_RATE, DoHornetMove),// s_HornetRun[3][0]},
			}, { new State(HORNET_RUN_R4 + 0, HORNET_RUN_RATE, DoHornetMove), // s_HornetRun[4][1]},
					new State(HORNET_RUN_R4 + 1, HORNET_RUN_RATE, DoHornetMove),// s_HornetRun[4][0]},
			} };

	//////////////////////
	//
	// HORNET STAND
	//
	//////////////////////

	public static final int HORNET_STAND_RATE = (HORNET_RUN_RATE + 5);

	private static final State s_HornetStand[][] = { { new State(HORNET_RUN_R0 + 0, HORNET_STAND_RATE, DoHornetMove), // s_HornetStand[0][1]},
			new State(HORNET_RUN_R0 + 1, HORNET_STAND_RATE, DoHornetMove),// s_HornetStand[0][0]}
			}, { new State(HORNET_RUN_R1 + 0, HORNET_STAND_RATE, DoHornetMove), // s_HornetStand[1][1]},
					new State(HORNET_RUN_R1 + 1, HORNET_STAND_RATE, DoHornetMove),// s_HornetStand[1][0]}
			}, { new State(HORNET_RUN_R2 + 0, HORNET_STAND_RATE, DoHornetMove), // s_HornetStand[2][1]},
					new State(HORNET_RUN_R2 + 1, HORNET_STAND_RATE, DoHornetMove),// s_HornetStand[2][0]}
			}, { new State(HORNET_RUN_R3 + 0, HORNET_STAND_RATE, DoHornetMove), // s_HornetStand[3][1]},
					new State(HORNET_RUN_R3 + 1, HORNET_STAND_RATE, DoHornetMove),// s_HornetStand[3][0]}
			}, { new State(HORNET_RUN_R4 + 0, HORNET_STAND_RATE, DoHornetMove), // s_HornetStand[4][1]},
					new State(HORNET_RUN_R4 + 1, HORNET_STAND_RATE, DoHornetMove),// s_HornetStand[4][0]}
			} };

	//////////////////////
	//
	// HORNET DIE
	//
	//////////////////////

	public static final int HORNET_DIE_RATE = 20;

	private static final Animator DoHornetDeath = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoHornetDeath(SpriteNum) != 0;
		}
	};

	private static final State s_HornetDie[] = { new State(HORNET_DIE + 0, HORNET_DIE_RATE, DoHornetDeath).setNext(),// s_HornetDie[0]},
	};

	private static final State s_HornetDead[] = { new State(HORNET_DEAD, HORNET_DIE_RATE, DoActorDebris).setNext(),// s_HornetDead[0]},
	};

	private static final Actor_Action_Set HornetActionSet = new Actor_Action_Set(HornetStateGroup.sg_HornetStand,
			HornetStateGroup.sg_HornetRun, null, null, null, null, null, null, null, null, null, // climb
			null, // pain
			HornetStateGroup.sg_HornetDie, null, HornetStateGroup.sg_HornetDead, null, null, null, new short[] { 0 },
			null, new short[] { 0 }, null, null, null);

	public static int SetupHornet(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, HORNET_RUN_R0, s_HornetRun[0][0]);
			u.Health = HEALTH_HORNET;
		}

		ChangeState(SpriteNum, s_HornetRun[0][0]);
		u.Attrib = HornetAttrib;
		DoActorSetSpeed(SpriteNum, NORM_SPEED);
		u.StateEnd = s_HornetDie[0];
		u.Rot = HornetStateGroup.sg_HornetRun;

		EnemyDefaults(SpriteNum, HornetActionSet, HornetPersonality);

		u.Flags |= (SPR_NO_SCAREDZ | SPR_XFLIP_TOGGLE);
		sp.cstat |= (CSTAT_SPRITE_YCENTER);

		sp.clipdist = (100) >> 2;
		u.floor_dist = (short) Z(16);
		u.ceiling_dist = (short) Z(16);

		u.sz = sp.z;

		sp.xrepeat = 37;
		sp.yrepeat = 32;

		// Special looping buzz sound attached to each hornet spawned
		VOC3D voc = PlaySound(DIGI_HORNETBUZZ, sp, v3df_follow | v3df_init);
		Set3DSoundOwner(SpriteNum, voc);

		return (0);
	}

	public static final int HORNET_BOB_AMT = (Z(16));

	private static int DoHornetMatchPlayerZ(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		SPRITE tsp = sprite[pUser[SpriteNum].tgt_sp];
		int zdiff, zdist;
		int loz, hiz;

		int bound;

		// actor does a sine wave about u.sz - this is the z mid point

		// zdiff = (SPRITEp_LOWER(tsp) - Z(8)) - u.sz;
		zdiff = (SPRITEp_MID(tsp)) - u.sz;

		// check z diff of the player and the sprite
		zdist = Z(20 + RANDOM_RANGE(200)); // put a random amount
		if (klabs(zdiff) > zdist) {
			if (zdiff > 0)
				// manipulate the z midpoint
				// u.sz += 256 * ACTORMOVETICS;
				u.sz += 1024 * ACTORMOVETICS;
			else
				u.sz -= 256 * ACTORMOVETICS;
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
			bound = loz - u.floor_dist - HORNET_BOB_AMT;

		if (u.sz > bound) {
			u.sz = bound;
		}

		// upper bound
		if (u.hi_sp != -1)
			bound = hiz + u.ceiling_dist;
		else
			bound = hiz + u.ceiling_dist + HORNET_BOB_AMT;

		if (u.sz < bound) {
			u.sz = bound;
		}

		u.sz = Math.min(u.sz, loz - u.floor_dist);
		u.sz = Math.max(u.sz, hiz + u.ceiling_dist);

		u.Counter = (short) ((u.Counter + (ACTORMOVETICS << 3) + (ACTORMOVETICS << 1)) & 2047);
		sp.z = u.sz + ((HORNET_BOB_AMT * (int) sintable[u.Counter]) >> 14);

		bound = u.hiz + u.ceiling_dist + HORNET_BOB_AMT;
		if (sp.z < bound) {
			// bumped something
			sp.z = u.sz = bound + HORNET_BOB_AMT;
		}

		return (0);
	}

	private static int InitHornetCircle(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		u.ActorActionFunc = DoHornetCircle;

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
		u.jump_speed = (short) (200 + RANDOM_P2(128));
		if (klabs(u.sz - u.hiz) < klabs(u.sz - u.loz))
			u.jump_speed = (short) -u.jump_speed;

		u.WaitTics = (short) ((RANDOM_RANGE(3) + 1) * 60);

		(u.ActorActionFunc).invoke(SpriteNum);

		return (0);
	}

	private static final Animator DoHornetCircle = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoHornetCircle(SpriteNum) != 0;
		}
	};

	private static int DoHornetCircle(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		int nx, ny, bound;

		sp.ang = NORM_ANGLE(sp.ang + u.Counter2);

		nx = sp.xvel * (int) sintable[NORM_ANGLE(sp.ang + 512)] >> 14;
		ny = sp.xvel * (int) sintable[sp.ang] >> 14;

		if (!move_actor(SpriteNum, nx, ny, 0)) {
			// ActorMoveHitReact(SpriteNum);

			// try moving in the opposite direction
			u.Counter2 = (short) -u.Counter2;
			sp.ang = NORM_ANGLE(sp.ang + 1024);
			nx = sp.xvel * (int) sintable[NORM_ANGLE(sp.ang + 512)] >> 14;
			ny = sp.xvel * (int) sintable[sp.ang] >> 14;

			if (!move_actor(SpriteNum, nx, ny, 0)) {
				InitActorReposition.invoke(SpriteNum);
				return (0);
			}
		}

		// move in the z direction
		u.sz -= u.jump_speed * ACTORMOVETICS;

		bound = u.hiz + u.ceiling_dist + HORNET_BOB_AMT;
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

	private static int DoHornetDeath(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		int nx, ny;

		if (TEST(u.Flags, SPR_FALLING)) {
			u.loz = u.zclip;
			DoFall(SpriteNum);
		} else {
			sp.cstat &= ~(CSTAT_SPRITE_YCENTER);
			u.jump_speed = 0;
			u.floor_dist = 0;
			DoBeginFall(SpriteNum);
			DoFindGroundPoint(SpriteNum);
			u.zclip = u.loz;
		}

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		// slide while falling
		nx = sp.xvel * (int) sintable[NORM_ANGLE(sp.ang + 512)] >> 14;
		ny = sp.xvel * (int) sintable[sp.ang] >> 14;

		u.ret = move_sprite(SpriteNum, nx, ny, 0, u.ceiling_dist, u.floor_dist, 1, ACTORMOVETICS);

		// on the ground
		if (sp.z >= u.loz) {
			u.Flags &= ~(SPR_FALLING | SPR_SLIDING);
			sp.cstat &= ~(CSTAT_SPRITE_YFLIP); // If upside down, reset it
			NewStateGroup(SpriteNum, u.ActorActionSet.Dead);
			DeleteNoSoundOwner(SpriteNum);
			return (0);
		}

		return (0);
	}

	// Hornets can swarm around other hornets or whatever is tagged as swarm target
	public static int DoCheckSwarm(int SpriteNum) {
		short i, nexti;
		SPRITE sp = sprite[SpriteNum], tsp;
		USER u = pUser[SpriteNum], tu;
		int dist, pdist;
		PlayerStr pp;

		if (MoveSkip8 == 0)
			return (0); // Don't over check

		if (u.tgt_sp == -1)
			return (0);

		// Who's the closest meat!?
		DoActorPickClosePlayer(SpriteNum);

		if (pUser[u.tgt_sp] != null && pUser[u.tgt_sp].PlayerP != -1) {
			pp = Player[pUser[u.tgt_sp].PlayerP];
			pdist = DISTANCE(sp.x, sp.y, pp.posx, pp.posy);
		} else
			return (0);

		// all enemys
		for (i = headspritestat[STAT_ENEMY]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			tsp = sprite[i];
			tu = pUser[i];

			if (tu == null)
				continue;

			if (tsp.hitag != TAG_SWARMSPOT || tsp.lotag != 2)
				continue;

			dist = DISTANCE(sp.x, sp.y, tsp.x, tsp.y);

			if (dist < pdist && u.ID == tu.ID) // Only flock to your own kind
			{
				u.tgt_sp = i; // Set target to swarm center
			}
		}

		return (TRUE);

	}

	private static int DoHornetMove(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		// Check for swarming
		// lotag of 1 = Swarm around lotags of 2
		// lotag of 0 is normal
		if (sp.hitag == TAG_SWARMSPOT && sp.lotag == 1)
			DoCheckSwarm(SpriteNum);

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		if (u.track >= 0)
			ActorFollowTrack(SpriteNum, ACTORMOVETICS);
		else
			(u.ActorActionFunc).invoke(SpriteNum);

		DoHornetMatchPlayerZ(SpriteNum);

		DoActorSectorDamage(SpriteNum);

		return (0);
	}
	
	public static void HornetSaveable()
	{
		SaveData(InitHornetSting);
		SaveData(InitHornetCircle);
		SaveData(DoHornetCircle);
		SaveData(DoHornetDeath);
		SaveData(DoHornetMove);

		SaveData(HornetPersonality);

		SaveData(HornetAttrib);

		SaveData(s_HornetRun);
		SaveGroup(HornetStateGroup.sg_HornetRun);
		SaveData(s_HornetStand);
		SaveGroup(HornetStateGroup.sg_HornetStand);
		SaveData(s_HornetDie);
		SaveGroup(HornetStateGroup.sg_HornetDie);
		SaveData(s_HornetDead);
		SaveGroup(HornetStateGroup.sg_HornetDead);

		SaveData(HornetActionSet);
	}
}
