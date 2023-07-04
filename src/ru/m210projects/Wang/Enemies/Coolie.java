package ru.m210projects.Wang.Enemies;

import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Wang.Actor.DoActorDebris;
import static ru.m210projects.Wang.Actor.DoActorDie;
import static ru.m210projects.Wang.Actor.DoActorSectorDamage;
import static ru.m210projects.Wang.Actor.DoActorSlide;
import static ru.m210projects.Wang.Actor.KeepActorOnFloor;
import static ru.m210projects.Wang.Ai.DoActorDecide;
import static ru.m210projects.Wang.Ai.DoActorSetSpeed;
import static ru.m210projects.Wang.Ai.FAST_SPEED;
import static ru.m210projects.Wang.Ai.InitActorAmbientNoise;
import static ru.m210projects.Wang.Ai.InitActorAttackNoise;
import static ru.m210projects.Wang.Ai.InitActorDecide;
import static ru.m210projects.Wang.Ai.InitActorEvade;
import static ru.m210projects.Wang.Ai.InitActorFindPlayer;
import static ru.m210projects.Wang.Ai.InitActorMoveCloser;
import static ru.m210projects.Wang.Ai.InitActorReposition;
import static ru.m210projects.Wang.Ai.InitActorRunAway;
import static ru.m210projects.Wang.Ai.InitActorWanderAround;
import static ru.m210projects.Wang.Ai.NORM_SPEED;
import static ru.m210projects.Wang.Digi.DIGI_CGMATERIALIZE;
import static ru.m210projects.Wang.Digi.DIGI_COOLIEALERT;
import static ru.m210projects.Wang.Digi.DIGI_COOLIEAMBIENT;
import static ru.m210projects.Wang.Digi.DIGI_COOLIEEXPLODE;
import static ru.m210projects.Wang.Digi.DIGI_COOLIEPAIN;
import static ru.m210projects.Wang.Digi.DIGI_COOLIESCREAM;
import static ru.m210projects.Wang.Enemies.Coolg.NewCoolg;
import static ru.m210projects.Wang.Game.Distance;
import static ru.m210projects.Wang.Game.TotalKillable;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK_HITSCAN;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_RESTORE;
import static ru.m210projects.Wang.Gameutils.HEALTH_COOLIE;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.SECTFX_SINK;
import static ru.m210projects.Wang.Gameutils.SF_QUICK_CALL;
import static ru.m210projects.Wang.Gameutils.SPRITEp_SIZE_Z;
import static ru.m210projects.Wang.Gameutils.SPRX_PLAYER_OR_ENEMY;
import static ru.m210projects.Wang.Gameutils.SPR_CLIMBING;
import static ru.m210projects.Wang.Gameutils.SPR_SLIDING;
import static ru.m210projects.Wang.Gameutils.SPR_XFLIP_TOGGLE;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Names.BUNNY_RUN_R0;
import static ru.m210projects.Wang.Names.CARGIRL_R0;
import static ru.m210projects.Wang.Names.COOLIE_CHARGE_R0;
import static ru.m210projects.Wang.Names.COOLIE_CHARGE_R1;
import static ru.m210projects.Wang.Names.COOLIE_CHARGE_R2;
import static ru.m210projects.Wang.Names.COOLIE_CHARGE_R3;
import static ru.m210projects.Wang.Names.COOLIE_CHARGE_R4;
import static ru.m210projects.Wang.Names.COOLIE_DEAD;
import static ru.m210projects.Wang.Names.COOLIE_DEAD_NOHEAD;
import static ru.m210projects.Wang.Names.COOLIE_DIE;
import static ru.m210projects.Wang.Names.COOLIE_PAIN_R0;
import static ru.m210projects.Wang.Names.COOLIE_PAIN_R1;
import static ru.m210projects.Wang.Names.COOLIE_PAIN_R2;
import static ru.m210projects.Wang.Names.COOLIE_PAIN_R3;
import static ru.m210projects.Wang.Names.COOLIE_PAIN_R4;
import static ru.m210projects.Wang.Names.COOLIE_RUN_R0;
import static ru.m210projects.Wang.Names.COOLIE_RUN_R1;
import static ru.m210projects.Wang.Names.COOLIE_RUN_R2;
import static ru.m210projects.Wang.Names.COOLIE_RUN_R3;
import static ru.m210projects.Wang.Names.COOLIE_RUN_R4;
import static ru.m210projects.Wang.Names.MECHANICGIRL_R0;
import static ru.m210projects.Wang.Names.PACHINKO1;
import static ru.m210projects.Wang.Names.PACHINKO2;
import static ru.m210projects.Wang.Names.PACHINKO3;
import static ru.m210projects.Wang.Names.PACHINKO4;
import static ru.m210projects.Wang.Names.PRUNEGIRL_R0;
import static ru.m210projects.Wang.Names.SAILORGIRL_R0;
import static ru.m210projects.Wang.Names.ST1;
import static ru.m210projects.Wang.Names.STAT_ENEMY;
import static ru.m210projects.Wang.Names.TOILETGIRL_R0;
import static ru.m210projects.Wang.Names.TRASHCAN;
import static ru.m210projects.Wang.Names.WASHGIRL_R0;
import static ru.m210projects.Wang.Player.QueueFloorBlood;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.PlaySpriteSound;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.DoActorZrange;
import static ru.m210projects.Wang.Sprites.NewStateGroup;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Sprites.change_sprite_stat;
import static ru.m210projects.Wang.Stag.SECT_SINK;
import static ru.m210projects.Wang.Track.ActorFollowTrack;
import static ru.m210projects.Wang.Type.MyTypes.DIV16;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.*;
import static ru.m210projects.Wang.Weapon.SpawnCoolieExp;
import static ru.m210projects.Wang.Weapon.UpdateSinglePlayKills;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Ai.Attrib_Snds;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Type.ATTRIBUTE;
import ru.m210projects.Wang.Type.Actor_Action_Set;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;

public class Coolie {

	public enum CoolieStateGroup implements StateGroup {
		sg_CoolieCharge(s_CoolieCharge[0], s_CoolieCharge[1], s_CoolieCharge[2], s_CoolieCharge[3], s_CoolieCharge[4]),
		sg_CoolieRun(s_CoolieRun[0], s_CoolieRun[1], s_CoolieRun[2], s_CoolieRun[3], s_CoolieRun[4]),
		sg_CooliePain(s_CooliePain[0], s_CooliePain[1], s_CooliePain[2], s_CooliePain[3], s_CooliePain[4]),
		sg_CoolieDie(s_CoolieDie),
		sg_CoolieStand(s_CoolieStand[0], s_CoolieStand[1], s_CoolieStand[2], s_CoolieStand[3], s_CoolieStand[4]),
		sg_CoolieDead(s_CoolieDead);

		private final State[][] group;
		private int index = -1;

		CoolieStateGroup(State[]... states) {
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

	public static void InitCoolieStates() {
		for (CoolieStateGroup sg : CoolieStateGroup.values()) {
			for (int rot = 0; rot < sg.group.length; rot++) {
				State.InitState(sg.group[rot]);
			}
		}
	}

	private static final Animator InitCoolieCharge = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitCoolieCharge(SpriteNum) != 0;
		}
	};

	private static final Decision CoolieBattle[] = { new Decision(700, InitCoolieCharge),
			new Decision(990, InitActorMoveCloser), new Decision(1000, InitActorAttackNoise),
			new Decision(1024, InitActorRunAway) };

	private static final Decision CoolieOffense[] = { new Decision(700, InitCoolieCharge),
			new Decision(1015, InitActorMoveCloser), new Decision(1024, InitActorAttackNoise) };

	private static final Decision CoolieBroadcast[] = {
			// new Decision(1, InitActorAlertNoise ),
			new Decision(16, InitActorAmbientNoise), new Decision(1024, InitActorDecide) };

	private static final Decision CoolieSurprised[] = { new Decision(700, InitActorMoveCloser),
			new Decision(703, InitActorAmbientNoise), new Decision(1024, InitActorDecide) };

	private static final Decision CoolieEvasive[] = { new Decision(10, InitActorEvade), new Decision(1024, null) };

	private static final Decision CoolieLostTarget[] = { new Decision(900, InitActorFindPlayer),
			new Decision(1024, InitActorWanderAround) };

	private static final Decision CoolieCloseRange[] = { new Decision(400, InitCoolieCharge),
			new Decision(1024, InitActorReposition) };

	private static final Personality CooliePersonality = new Personality(CoolieBattle, CoolieOffense, CoolieBroadcast,
			CoolieSurprised, CoolieEvasive, CoolieLostTarget, CoolieCloseRange, CoolieCloseRange);

	private static final ATTRIBUTE CoolieAttrib = new ATTRIBUTE(new short[] { 60, 80, 100, 200 }, // Speeds
			new short[] { 3, 0, -2, -3 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { DIGI_COOLIEAMBIENT, DIGI_COOLIEALERT, DIGI_COOLIEALERT, DIGI_COOLIEPAIN, 0, DIGI_CGMATERIALIZE,
					DIGI_COOLIEEXPLODE, 0, 0, 0 });

	//////////////////////
	//
	// COOLIE RUN
	//
	//////////////////////

	public static final int COOLIE_RATE = 12;

	private static final Animator DoCoolieMove = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoCoolieMove(SpriteNum) != 0;
		}
	};

	private static final State s_CoolieRun[][] = { { new State(COOLIE_RUN_R0 + 0, COOLIE_RATE, DoCoolieMove), // s_CoolieRun[0][1]},
			new State(COOLIE_RUN_R0 + 1, COOLIE_RATE, DoCoolieMove), // s_CoolieRun[0][2]},
			new State(COOLIE_RUN_R0 + 2, COOLIE_RATE, DoCoolieMove), // s_CoolieRun[0][3]},
			new State(COOLIE_RUN_R0 + 3, COOLIE_RATE, DoCoolieMove),// s_CoolieRun[0][0]}
			}, { new State(COOLIE_RUN_R1 + 0, COOLIE_RATE, DoCoolieMove), // s_CoolieRun[1][1]},
					new State(COOLIE_RUN_R1 + 1, COOLIE_RATE, DoCoolieMove), // s_CoolieRun[1][2]},
					new State(COOLIE_RUN_R1 + 2, COOLIE_RATE, DoCoolieMove), // s_CoolieRun[1][3]},
					new State(COOLIE_RUN_R1 + 3, COOLIE_RATE, DoCoolieMove),// s_CoolieRun[1][0]}
			}, { new State(COOLIE_RUN_R2 + 0, COOLIE_RATE, DoCoolieMove), // s_CoolieRun[2][1]},
					new State(COOLIE_RUN_R2 + 1, COOLIE_RATE, DoCoolieMove), // s_CoolieRun[2][2]},
					new State(COOLIE_RUN_R2 + 2, COOLIE_RATE, DoCoolieMove), // s_CoolieRun[2][3]},
					new State(COOLIE_RUN_R2 + 3, COOLIE_RATE, DoCoolieMove),// s_CoolieRun[2][0]}
			}, { new State(COOLIE_RUN_R3 + 0, COOLIE_RATE, DoCoolieMove), // s_CoolieRun[3][1]},
					new State(COOLIE_RUN_R3 + 1, COOLIE_RATE, DoCoolieMove), // s_CoolieRun[3][2]},
					new State(COOLIE_RUN_R3 + 2, COOLIE_RATE, DoCoolieMove), // s_CoolieRun[3][3]},
					new State(COOLIE_RUN_R3 + 3, COOLIE_RATE, DoCoolieMove),// s_CoolieRun[3][0]}
			}, { new State(COOLIE_RUN_R4 + 0, COOLIE_RATE, DoCoolieMove), // s_CoolieRun[4][1]},
					new State(COOLIE_RUN_R4 + 1, COOLIE_RATE, DoCoolieMove), // s_CoolieRun[4][2]},
					new State(COOLIE_RUN_R4 + 2, COOLIE_RATE, DoCoolieMove), // s_CoolieRun[4][3]},
					new State(COOLIE_RUN_R4 + 3, COOLIE_RATE, DoCoolieMove),// s_CoolieRun[4][0]},
			} };

	//////////////////////
	//
	// COOLIE CHARGE
	//
	//////////////////////

	private static final State s_CoolieCharge[][] = { { new State(COOLIE_CHARGE_R0 + 0, COOLIE_RATE, DoCoolieMove), // s_CoolieCharge[0][1]},
			new State(COOLIE_CHARGE_R0 + 1, COOLIE_RATE, DoCoolieMove), // s_CoolieCharge[0][2]},
			new State(COOLIE_CHARGE_R0 + 2, COOLIE_RATE, DoCoolieMove), // s_CoolieCharge[0][3]},
			new State(COOLIE_CHARGE_R0 + 3, COOLIE_RATE, DoCoolieMove),// s_CoolieCharge[0][0]}
			}, { new State(COOLIE_CHARGE_R1 + 0, COOLIE_RATE, DoCoolieMove), // s_CoolieCharge[1][1]},
					new State(COOLIE_CHARGE_R1 + 1, COOLIE_RATE, DoCoolieMove), // s_CoolieCharge[1][2]},
					new State(COOLIE_CHARGE_R1 + 2, COOLIE_RATE, DoCoolieMove), // s_CoolieCharge[1][3]},
					new State(COOLIE_CHARGE_R1 + 3, COOLIE_RATE, DoCoolieMove),// s_CoolieCharge[1][0]}
			}, { new State(COOLIE_CHARGE_R2 + 0, COOLIE_RATE, DoCoolieMove), // s_CoolieCharge[2][1]},
					new State(COOLIE_CHARGE_R2 + 1, COOLIE_RATE, DoCoolieMove), // s_CoolieCharge[2][2]},
					new State(COOLIE_CHARGE_R2 + 2, COOLIE_RATE, DoCoolieMove), // s_CoolieCharge[2][3]},
					new State(COOLIE_CHARGE_R2 + 3, COOLIE_RATE, DoCoolieMove),// s_CoolieCharge[2][0]}
			}, { new State(COOLIE_CHARGE_R3 + 0, COOLIE_RATE, DoCoolieMove), // s_CoolieCharge[3][1]},
					new State(COOLIE_CHARGE_R3 + 1, COOLIE_RATE, DoCoolieMove), // s_CoolieCharge[3][2]},
					new State(COOLIE_CHARGE_R3 + 2, COOLIE_RATE, DoCoolieMove), // s_CoolieCharge[3][3]},
					new State(COOLIE_CHARGE_R3 + 3, COOLIE_RATE, DoCoolieMove),// s_CoolieCharge[3][0]}
			}, { new State(COOLIE_CHARGE_R4 + 0, COOLIE_RATE, DoCoolieMove), // s_CoolieCharge[4][1]},
					new State(COOLIE_CHARGE_R4 + 1, COOLIE_RATE, DoCoolieMove), // s_CoolieCharge[4][2]},
					new State(COOLIE_CHARGE_R4 + 2, COOLIE_RATE, DoCoolieMove), // s_CoolieCharge[4][3]},
					new State(COOLIE_CHARGE_R4 + 3, COOLIE_RATE, DoCoolieMove),// s_CoolieCharge[4][0]},
			} };

	//////////////////////
	//
	// COOLIE STAND
	//
	//////////////////////

	private static final State s_CoolieStand[][] = {
			{ new State(COOLIE_RUN_R0 + 0, COOLIE_RATE, DoCoolieMove).setNext(),// s_CoolieStand[0][0]}
			}, { new State(COOLIE_RUN_R1 + 0, COOLIE_RATE, DoCoolieMove).setNext(),// s_CoolieStand[1][0]}
			}, { new State(COOLIE_RUN_R2 + 0, COOLIE_RATE, DoCoolieMove).setNext(),// s_CoolieStand[2][0]}
			}, { new State(COOLIE_RUN_R3 + 0, COOLIE_RATE, DoCoolieMove).setNext(),// s_CoolieStand[3][0]}
			}, { new State(COOLIE_RUN_R4 + 0, COOLIE_RATE, DoCoolieMove).setNext(),// s_CoolieStand[4][0]}
			} };

	//////////////////////
	//
	// COOLIE PAIN
	//
	//////////////////////

	public static final int COOLIE_PAIN_RATE = 60;

	private static final Animator CooliePain = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return CooliePain(SpriteNum) != 0;
		}
	};

	private static final State s_CooliePain[][] = {
			{ new State(COOLIE_PAIN_R0 + 0, COOLIE_PAIN_RATE, CooliePain).setNext(),// s_CooliePain[0][0]},
			}, { new State(COOLIE_PAIN_R1 + 0, COOLIE_PAIN_RATE, CooliePain).setNext(),// s_CooliePain[1][0]},
			}, { new State(COOLIE_PAIN_R2 + 0, COOLIE_PAIN_RATE, CooliePain).setNext(),// s_CooliePain[2][0]},
			}, { new State(COOLIE_PAIN_R3 + 0, COOLIE_PAIN_RATE, CooliePain).setNext(),// s_CooliePain[3][0]},
			}, { new State(COOLIE_PAIN_R4 + 0, COOLIE_PAIN_RATE, CooliePain).setNext(),// s_CooliePain[4][0]},
			} };

	//////////////////////
	//
	// COOLIE DIE
	//
	//////////////////////

	public static final int COOLIE_DIE_RATE = 30;

	private static final Animator NullCoolie = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return NullCoolie(SpriteNum) != 0;
		}
	};

	private static final Animator SpawnCoolg = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return SpawnCoolg(SpriteNum) != 0;
		}
	};

	private static final Animator SpawnCoolieExp = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return SpawnCoolieExp(SpriteNum) != 0;
		}
	};

	private static final Animator DoCoolieWaitBirth = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoCoolieWaitBirth(SpriteNum) != 0;
		}
	};

	private static final State s_CoolieDie[] = { new State(COOLIE_DIE + 0, COOLIE_DIE_RATE, NullCoolie), // s_CoolieDie[1]},

			new State(COOLIE_DIE + 0, 0 | SF_QUICK_CALL, SpawnCoolieExp), // s_CoolieDie[2]},

			new State(COOLIE_DIE + 1, COOLIE_DIE_RATE, NullCoolie), // s_CoolieDie[3]},
			new State(COOLIE_DIE + 2, COOLIE_DIE_RATE, NullCoolie), // s_CoolieDie[4]},
			new State(COOLIE_DIE + 3, COOLIE_DIE_RATE, NullCoolie), // s_CoolieDie[5]},
			new State(COOLIE_DIE + 4, COOLIE_DIE_RATE, NullCoolie), // s_CoolieDie[6]},
			new State(COOLIE_DIE + 5, COOLIE_DIE_RATE, NullCoolie), // s_CoolieDie[7]},
			new State(COOLIE_DIE + 6, COOLIE_DIE_RATE, NullCoolie), // s_CoolieDie[8]},
			new State(COOLIE_DIE + 7, COOLIE_DIE_RATE, DoCoolieWaitBirth).setNext(), // s_CoolieDie[8]},
			new State(COOLIE_DIE + 7, COOLIE_DIE_RATE * 5, DoActorDebris), // s_CoolieDie[10]},
			new State(COOLIE_DIE + 7, 0 | SF_QUICK_CALL, SpawnCoolg), // s_CoolieDie[11]},
			new State(COOLIE_DEAD_NOHEAD, SF_QUICK_CALL, QueueFloorBlood), // s_CoolieDie[12]},
			new State(COOLIE_DEAD_NOHEAD, COOLIE_DIE_RATE, DoActorDebris).setNext(),// s_CoolieDie[12]}
	};

	private static final State s_CoolieDead[] = { new State(COOLIE_DEAD, COOLIE_DIE_RATE, DoActorDebris),// s_CoolieDead[0]},
	};

	private static final Actor_Action_Set CoolieActionSet = new Actor_Action_Set(CoolieStateGroup.sg_CoolieStand,
			CoolieStateGroup.sg_CoolieRun, null, null, null, null, null, null, null, null, null, // climb
			CoolieStateGroup.sg_CooliePain, // pain
			CoolieStateGroup.sg_CoolieDie, null, CoolieStateGroup.sg_CoolieDead, null, null,
			new StateGroup[] { CoolieStateGroup.sg_CoolieCharge }, new short[] { 1024 },
			new StateGroup[] { CoolieStateGroup.sg_CoolieCharge }, new short[] { 1024 }, null, null, null);

	public static void EnemyDefaults(int SpriteNum, Actor_Action_Set action, Personality person) {
		USER u = pUser[SpriteNum];
		SPRITE sp = sprite[SpriteNum];
		int wpn;
		short wpn_cnt;
		short depth = 0;

		switch (u.ID) {
		case PACHINKO1:
		case PACHINKO2:
		case PACHINKO3:
		case PACHINKO4:
		case 623:
		case TOILETGIRL_R0:
		case WASHGIRL_R0:
		case CARGIRL_R0:
		case MECHANICGIRL_R0:
		case SAILORGIRL_R0:
		case PRUNEGIRL_R0:
		case TRASHCAN:
		case BUNNY_RUN_R0:
			break;
		default: {
			TotalKillable++;
		}

			break;
		}

		sp.cstat &= ~(CSTAT_SPRITE_RESTORE);

		u.spal = (byte) sp.pal;

		u.RotNum = 5;
		sp.clipdist = (256) >> 2;

		u.zclip = Z(48);
		u.lo_step = (short) Z(32);

		u.floor_dist = (short) (u.zclip - u.lo_step);
		u.ceiling_dist = (short) (SPRITEp_SIZE_Z(sp) - u.zclip);

		u.Radius = 400;

		u.MaxHealth = u.Health;

		u.PainThreshold = (short) (DIV16(u.Health) - 1);

		sp.cstat |= (CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		sp.extra |= (SPRX_PLAYER_OR_ENEMY);

		sprite[SpriteNum].picnum = u.State.Pic;
		change_sprite_stat(SpriteNum, STAT_ENEMY);

		u.Personality = person;
		u.ActorActionSet = action;

		DoActorZrange(SpriteNum);

		// KeepActorOnFloor(SpriteNum); // for swimming actors

		// make sure we start in the water if thats where we are
		if (u.lo_sectp != -1)// && SectpUser[u.lo_sectp - sector])
		{
			short i, nexti;
			short sectnum = u.lo_sectp;

			if (SectUser[sectnum] != null && TEST(sector[u.lo_sectp].extra, SECTFX_SINK)) {
				depth = SectUser[sectnum].depth;
			} else {
				for (i = headspritesect[sectnum]; i != -1; i = nexti) {
					nexti = nextspritesect[i];
					SPRITE np = sprite[i];
					if (np.picnum == ST1 && np.hitag == SECT_SINK) {
						depth = np.lotag;
					}
				}
			}
		}

		if (depth != 0 && klabs(sp.z - u.loz) < Z(8)) {
			sp.z += Z(depth);
			u.loz = sp.z;
			u.oz = sp.z;
		}

		if (action == null)
			return;

		NewStateGroup(SpriteNum, u.ActorActionSet.Run);

		u.ActorActionFunc = DoActorDecide;

		// find the number of int range attacks
		for (wpn = wpn_cnt = 0; u.ActorActionSet.Attack != null && wpn < u.ActorActionSet.Attack.length; wpn++) {
			if (u.ActorActionSet.Attack[wpn] != null)
				wpn_cnt++;
			else
				break;
		}

		// for actors this tells the number of weapons available
		// for player it tells the current weapon
		u.WeaponNum = wpn_cnt;
	}

	public static int SetupCoolie(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, COOLIE_RUN_R0, s_CoolieRun[0][0]);
			u.Health = HEALTH_COOLIE;
		}

		ChangeState(SpriteNum, s_CoolieRun[0][0]);
		u.Attrib = CoolieAttrib;
		DoActorSetSpeed(SpriteNum, NORM_SPEED);
		u.StateEnd = s_CoolieDie[0];
		u.Rot = CoolieStateGroup.sg_CoolieRun;

		EnemyDefaults(SpriteNum, CoolieActionSet, CooliePersonality);

		sp.xrepeat = 42;
		sp.yrepeat = 42;

		u.Flags |= (SPR_XFLIP_TOGGLE);

		return (0);
	}

	private static int SpawnCoolg(int SpriteNum) {
		// Don't do a ghost every time
		if (RANDOM_RANGE(1000) > 700)
			return (0);

		NewCoolg(SpriteNum);

		PlaySpriteSound(SpriteNum, Attrib_Snds.attr_extra1, v3df_follow);

		return (0);
	}

	private static int CooliePain(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		if (!TEST(u.Flags, SPR_CLIMBING))
			KeepActorOnFloor(SpriteNum);

		DoActorSectorDamage(SpriteNum);

		if ((u.WaitTics -= ACTORMOVETICS) <= 0)
			InitActorDecide(SpriteNum);

		return (0);
	}

	private static int NullCoolie(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		if (!TEST(u.Flags, SPR_CLIMBING))
			KeepActorOnFloor(SpriteNum);

		DoActorSectorDamage(SpriteNum);

		return (0);
	}

	private static int DoCoolieMove(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		if (u.track >= 0)
			ActorFollowTrack(SpriteNum, ACTORMOVETICS);
		else
			(u.ActorActionFunc).invoke(SpriteNum);

		KeepActorOnFloor(SpriteNum);

		if (DoActorSectorDamage(SpriteNum)) {
			return (0);
		}

		if (Distance(sp.x, sp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y) < 1200) {
			UpdateSinglePlayKills(SpriteNum, null);
			DoActorDie(SpriteNum, SpriteNum);
			return (0);
		}

		return (0);
	}

	private static int InitCoolieCharge(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];

		if (RANDOM_P2(1024) > 950)
			PlaySound(DIGI_COOLIESCREAM, sp, v3df_follow);

		DoActorSetSpeed(SpriteNum, FAST_SPEED);

		InitActorMoveCloser.invoke(SpriteNum);

		NewStateGroup(SpriteNum, CoolieStateGroup.sg_CoolieCharge);

		return (0);
	}

	private static int DoCoolieWaitBirth(int SpriteNum) {
		USER u = pUser[SpriteNum];
		
		if ((u.Counter -= ACTORMOVETICS) <= 0) {
			ChangeState(SpriteNum, s_CoolieDie[9]);
		}

		return (0);
	}
	
	public static void CoolieSaveable()
	{
		SaveData(SpawnCoolieExp);
		SaveData(SpawnCoolg);
		SaveData(CooliePain);
		SaveData(NullCoolie);
		SaveData(DoCoolieMove);
		SaveData(InitCoolieCharge);
		SaveData(DoCoolieWaitBirth);
		SaveData(CooliePersonality);

		SaveData(CoolieAttrib);
		SaveData(s_CoolieRun);
		SaveGroup(CoolieStateGroup.sg_CoolieRun);
		SaveData(s_CoolieCharge);
		SaveGroup(CoolieStateGroup.sg_CoolieCharge);
		SaveData(s_CoolieStand);
		SaveGroup(CoolieStateGroup.sg_CoolieStand);
		SaveData(s_CooliePain);
		SaveGroup(CoolieStateGroup.sg_CooliePain);
		SaveData(s_CoolieDie);
		SaveGroup(CoolieStateGroup.sg_CoolieDie);
		SaveData(s_CoolieDead);
		SaveGroup(CoolieStateGroup.sg_CoolieDead);
		SaveData(CoolieActionSet);
	}
}
