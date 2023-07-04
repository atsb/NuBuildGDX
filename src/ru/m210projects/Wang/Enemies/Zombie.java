package ru.m210projects.Wang.Enemies;

import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Wang.Actor.DoActorFall;
import static ru.m210projects.Wang.Actor.DoActorJump;
import static ru.m210projects.Wang.Actor.DoActorSectorDamage;
import static ru.m210projects.Wang.Actor.DoActorSlide;
import static ru.m210projects.Wang.Actor.KeepActorOnFloor;
import static ru.m210projects.Wang.Ai.DoActorPickClosePlayer;
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
import static ru.m210projects.Wang.Digi.DIGI_STAR;
import static ru.m210projects.Wang.Enemies.Coolie.EnemyDefaults;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_TRANSLUCENT;
import static ru.m210projects.Wang.Gameutils.FAF_ConnectArea;
import static ru.m210projects.Wang.Gameutils.PF_DEAD;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.SECTFX_LIQUID_MASK;
import static ru.m210projects.Wang.Gameutils.SECTFX_LIQUID_NONE;
import static ru.m210projects.Wang.Gameutils.SF_QUICK_CALL;
import static ru.m210projects.Wang.Gameutils.SF_TIC_ADJUST;
import static ru.m210projects.Wang.Gameutils.SPR2_DONT_TARGET_OWNER;
import static ru.m210projects.Wang.Gameutils.SPR_ACTIVE;
import static ru.m210projects.Wang.Gameutils.SPR_FALLING;
import static ru.m210projects.Wang.Gameutils.SPR_JUMPING;
import static ru.m210projects.Wang.Gameutils.SPR_SLIDING;
import static ru.m210projects.Wang.Gameutils.SPR_XFLIP_TOGGLE;
import static ru.m210projects.Wang.Gameutils.SectorIsUnderwaterArea;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.JWeapon.InitBloodSpray;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_JUMP_R0;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_JUMP_R1;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_JUMP_R2;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_JUMP_R3;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_JUMP_R4;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_RUN_R0;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_RUN_R1;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_RUN_R2;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_RUN_R3;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_RUN_R4;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_STAND_R0;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_STAND_R1;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_STAND_R2;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_STAND_R3;
import static ru.m210projects.Wang.Names.PLAYER_NINJA_STAND_R4;
import static ru.m210projects.Wang.Names.STAT_ENEMY;
import static ru.m210projects.Wang.Names.ZOMBIE_RUN_R0;
import static ru.m210projects.Wang.Player.PLAYER_NINJA_XREPEAT;
import static ru.m210projects.Wang.Player.PLAYER_NINJA_YREPEAT;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.DoActorZrange;
import static ru.m210projects.Wang.Sprites.SpawnSprite;
import static ru.m210projects.Wang.Track.ActorFollowTrack;
import static ru.m210projects.Wang.Type.MyTypes.DTEST;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.*;
import static ru.m210projects.Wang.Weapon.InitEnemyRail;
import static ru.m210projects.Wang.Weapon.SetSuicide;

import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Type.ATTRIBUTE;
import ru.m210projects.Wang.Type.Actor_Action_Set;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.Sect_User;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;

public class Zombie {

	private static final int ZOMBIE_RATE = 32;
	private static final int ZOMBIE_TIME_LIMIT = ((120 * 20) / ACTORMOVETICS);

	public static final Animator DoZombieMove = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];

			if (u.Counter3++ >= ZOMBIE_TIME_LIMIT) {
				InitBloodSpray(SpriteNum, true, 105);
				InitBloodSpray(SpriteNum, true, 105);
				InitBloodSpray(SpriteNum, true, 105);
				SetSuicide(SpriteNum);
				return false;
			}

			if (u.tgt_sp != -1 && pUser[u.tgt_sp] != null && TEST(pUser[u.tgt_sp].Flags, PF_DEAD))
				DoActorPickClosePlayer(SpriteNum);

			// jumping and falling
			if (TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
				if (TEST(u.Flags, SPR_JUMPING))
					DoActorJump(SpriteNum);
				else if (TEST(u.Flags, SPR_FALLING))
					DoActorFall(SpriteNum);
			}

			// sliding
			if (TEST(u.Flags, SPR_SLIDING))
				DoActorSlide(SpriteNum);

			// Do track or call current action function - such as DoActorMoveCloser()
			if (u.track >= 0)
				ActorFollowTrack(SpriteNum, ACTORMOVETICS);
			else {
				(u.ActorActionFunc).invoke(SpriteNum);
			}

			// stay on floor unless doing certain things
			if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
				KeepActorOnFloor(SpriteNum);
			}

			// take damage from environment
			DoActorSectorDamage(SpriteNum);

			return false;
		}
	};

	private static final State s_ZombieRun[][] = {
			{ new State(PLAYER_NINJA_RUN_R0 + 0, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[0][1]},
					new State(PLAYER_NINJA_RUN_R0 + 1, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[0][2]},
					new State(PLAYER_NINJA_RUN_R0 + 2, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[0][3]},
					new State(PLAYER_NINJA_RUN_R0 + 3, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[0][0]},
			}, { new State(PLAYER_NINJA_RUN_R1 + 0, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[1][1]},
					new State(PLAYER_NINJA_RUN_R1 + 1, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[1][2]},
					new State(PLAYER_NINJA_RUN_R1 + 2, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[1][3]},
					new State(PLAYER_NINJA_RUN_R1 + 3, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[1][0]},
			}, { new State(PLAYER_NINJA_RUN_R2 + 0, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[2][1]},
					new State(PLAYER_NINJA_RUN_R2 + 1, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[2][2]},
					new State(PLAYER_NINJA_RUN_R2 + 2, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[2][3]},
					new State(PLAYER_NINJA_RUN_R2 + 3, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[2][0]},
			}, { new State(PLAYER_NINJA_RUN_R3 + 0, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[3][1]},
					new State(PLAYER_NINJA_RUN_R3 + 1, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[3][2]},
					new State(PLAYER_NINJA_RUN_R3 + 2, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[3][3]},
					new State(PLAYER_NINJA_RUN_R3 + 3, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[3][0]},
			}, { new State(PLAYER_NINJA_RUN_R4 + 0, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[4][1]},
					new State(PLAYER_NINJA_RUN_R4 + 1, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[4][2]},
					new State(PLAYER_NINJA_RUN_R4 + 2, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[4][3]},
					new State(PLAYER_NINJA_RUN_R4 + 3, ZOMBIE_RATE | SF_TIC_ADJUST, DoZombieMove), // &s_ZombieRun[4][0]},
			}, };

	public static final Animator DoZombiePain = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];

			NullZombie.invoke(SpriteNum);

			if ((u.WaitTics -= ACTORMOVETICS) <= 0)
				InitActorDecide(SpriteNum);

			return false;
		}
	};

	public static final Animator NullZombie = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {

			USER u = pUser[SpriteNum];

			if (u.Counter3++ >= ZOMBIE_TIME_LIMIT) {
				InitBloodSpray(SpriteNum, true, 105);
				InitBloodSpray(SpriteNum, true, 105);
				InitBloodSpray(SpriteNum, true, 105);
				SetSuicide(SpriteNum);
				return false;
			}

			if (u.tgt_sp != -1 && pUser[u.tgt_sp] != null && TEST(pUser[u.tgt_sp].Flags, PF_DEAD))
				DoActorPickClosePlayer(SpriteNum);

			if (u.WaitTics > 0)
				u.WaitTics -= ACTORMOVETICS;

			if (TEST(u.Flags, SPR_SLIDING) && !TEST(u.Flags, SPR_JUMPING | SPR_FALLING))
				DoActorSlide(SpriteNum);

			if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING))
				KeepActorOnFloor(SpriteNum);

			DoActorSectorDamage(SpriteNum);

			return false;
		}
	};

	private static final int ZOMBIE_PAIN_RATE = 15;
	private static final State s_ZombiePain[][] = {
			{ new State(PLAYER_NINJA_STAND_R0 + 0, ZOMBIE_PAIN_RATE, DoZombiePain), // &s_ZombiePain[0][1]},
					new State(PLAYER_NINJA_STAND_R0 + 1, ZOMBIE_PAIN_RATE, DoZombiePain), // &s_ZombiePain[0][1]},
			}, { new State(PLAYER_NINJA_STAND_R1 + 0, ZOMBIE_PAIN_RATE, DoZombiePain), // &s_ZombiePain[1][1]},
					new State(PLAYER_NINJA_STAND_R1 + 0, ZOMBIE_PAIN_RATE, DoZombiePain), // &s_ZombiePain[1][1]},
			}, { new State(PLAYER_NINJA_STAND_R2 + 0, ZOMBIE_PAIN_RATE, DoZombiePain), // &s_ZombiePain[2][1]},
					new State(PLAYER_NINJA_STAND_R2 + 0, ZOMBIE_PAIN_RATE, DoZombiePain), // &s_ZombiePain[2][1]},
			}, { new State(PLAYER_NINJA_STAND_R3 + 0, ZOMBIE_PAIN_RATE, DoZombiePain), // &s_ZombiePain[3][1]},
					new State(PLAYER_NINJA_STAND_R3 + 0, ZOMBIE_PAIN_RATE, DoZombiePain), // &s_ZombiePain[3][1]},
			}, { new State(PLAYER_NINJA_STAND_R4 + 0, ZOMBIE_PAIN_RATE, DoZombiePain), // &s_ZombiePain[4][1]},
					new State(PLAYER_NINJA_STAND_R4 + 0, ZOMBIE_PAIN_RATE, DoZombiePain), // &s_ZombiePain[4][1]},
			}, };

	public static int SpawnZombie2(int Weapon) {
		SPRITE sp = sprite[Weapon];
		SPRITE np;
		USER nu;
		short owner;
		Sect_User sectu = SectUser[sp.sectnum];
		SECTOR sectp = sector[sp.sectnum];

		owner = sprite[Weapon].owner;

		if (owner < 0)
			return (-1);

		if (sectu != null && (DTEST(sectp.extra, SECTFX_LIQUID_MASK) != SECTFX_LIQUID_NONE))
			return (-1);

		if (SectorIsUnderwaterArea(sp.sectnum))
			return (-1);

		if (FAF_ConnectArea(sp.sectnum)) {
			short sectnum = engine.updatesectorz(sp.x, sp.y, sp.z + Z(10), sp.sectnum);
			if (sectnum >= 0 && SectorIsUnderwaterArea(sectnum))
				return (-1);
		}

		// Zombies++;
		int newsp = SpawnSprite(STAT_ENEMY, ZOMBIE_RUN_R0, s_ZombieRun[0][0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang, 0);
		np = sprite[newsp];
		nu = pUser[newsp];
		nu.Counter3 = 0;
		np.owner = owner;
		np.pal = nu.spal = pUser[owner].spal;
		np.ang = (short) RANDOM_P2(2048);
		SetupZombie(newsp);
		np.shade = -10;
		nu.Flags2 |= (SPR2_DONT_TARGET_OWNER);
		np.cstat |= (CSTAT_SPRITE_TRANSLUCENT);

		DoActorPickClosePlayer(newsp);

		// make immediately active
		nu.Flags |= (SPR_ACTIVE);

		nu.Flags &= ~(SPR_JUMPING);
		nu.Flags &= ~(SPR_FALLING);

		// if I didn't do this here they get stuck in the air sometimes
		DoActorZrange(newsp);

		return (newsp);
	}

	private static final ATTRIBUTE ZombieAttrib = new ATTRIBUTE(new short[] { 120, 140, 170, 200 }, // Speeds
			new short[] { 4, 0, 0, -2 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { DIGI_NINJAAMBIENT, DIGI_NINJAALERT, DIGI_STAR, DIGI_NINJAPAIN, DIGI_NINJASCREAM, 0, 0, 0, 0,
					0 });

	private static final Decision ZombieBattle[] = { new Decision(399, InitActorMoveCloser),
			new Decision(1024, InitActorAttack) };

	private static final Decision ZombieOffense[] = { new Decision(399, InitActorMoveCloser),
			new Decision(1024, InitActorAttack) };

	private static final Decision ZombieBroadcast[] = { new Decision(6, InitActorAmbientNoise),
			new Decision(1024, InitActorDecide) };

	private static final Decision ZombieSurprised[] = { new Decision(701, InitActorMoveCloser),
			new Decision(1024, InitActorDecide) };

	private static final Decision ZombieEvasive[] = { new Decision(400, InitActorDuck), new Decision(1024, null) };

	private static final Decision ZombieLostTarget[] = { new Decision(900, InitActorFindPlayer),
			new Decision(1024, InitActorWanderAround) };

	private static final Decision ZombieCloseRange[] = { new Decision(800, InitActorAttack),
			new Decision(1024, InitActorReposition) };

	private static final Personality ZombiePersonality = new Personality(ZombieBattle, ZombieOffense, ZombieBroadcast,
			ZombieSurprised, ZombieEvasive, ZombieLostTarget, ZombieCloseRange, ZombieCloseRange);

	private static final int ZOMBIE_STAND_RATE = 10;

	private static final State s_ZombieStand[][] = {
			{ new State(PLAYER_NINJA_STAND_R0 + 0, ZOMBIE_STAND_RATE, NullZombie).setNext(), },
			{ new State(PLAYER_NINJA_STAND_R1 + 0, ZOMBIE_STAND_RATE, NullZombie).setNext(), },
			{ new State(PLAYER_NINJA_STAND_R2 + 0, ZOMBIE_STAND_RATE, NullZombie).setNext(), },
			{ new State(PLAYER_NINJA_STAND_R3 + 0, ZOMBIE_STAND_RATE, NullZombie).setNext(), },
			{ new State(PLAYER_NINJA_STAND_R4 + 0, ZOMBIE_STAND_RATE, NullZombie).setNext(), }, };

	private static final int ZOMBIE_FALL_RATE = 25;

	private static final State s_ZombieFall[][] = {
			{ new State(PLAYER_NINJA_JUMP_R0 + 3, ZOMBIE_FALL_RATE, DoZombieMove).setNext(), },
			{ new State(PLAYER_NINJA_JUMP_R1 + 3, ZOMBIE_FALL_RATE, DoZombieMove).setNext(), },
			{ new State(PLAYER_NINJA_JUMP_R2 + 3, ZOMBIE_FALL_RATE, DoZombieMove).setNext(), },
			{ new State(PLAYER_NINJA_JUMP_R3 + 3, ZOMBIE_FALL_RATE, DoZombieMove).setNext(), },
			{ new State(PLAYER_NINJA_JUMP_R4 + 3, ZOMBIE_FALL_RATE, DoZombieMove).setNext(), } };

	private static final int ZOMBIE_RAIL_RATE = 14;

	public static final Animator InitEnemyRail = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			InitEnemyRail(SpriteNum);
			return false;
		}
	};

	private static final State s_ZombieRail[][] = {
			{ new State(PLAYER_NINJA_STAND_R0 + 0, ZOMBIE_RAIL_RATE * 2, NullZombie), // &s_ZombieRail[0][1]},
					new State(PLAYER_NINJA_STAND_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyRail), // &s_ZombieRail[0][2]},
					new State(PLAYER_NINJA_STAND_R0 + 0, ZOMBIE_RAIL_RATE, NullZombie), // &s_ZombieRail[0][3]},
					new State(PLAYER_NINJA_STAND_R0 + 0, 0 | SF_QUICK_CALL, InitActorDecide), // &s_ZombieRail[0][4]},
					new State(PLAYER_NINJA_STAND_R0 + 0, ZOMBIE_RAIL_RATE, DoZombieMove).setNext(), // &s_ZombieRail[0][4]},
			}, { new State(PLAYER_NINJA_STAND_R1 + 0, ZOMBIE_RAIL_RATE * 2, NullZombie), // &s_ZombieRail[1][1]},
					new State(PLAYER_NINJA_STAND_R1 + 0, 0 | SF_QUICK_CALL, InitEnemyRail), // &s_ZombieRail[1][2]},
					new State(PLAYER_NINJA_STAND_R1 + 0, ZOMBIE_RAIL_RATE, NullZombie), // &s_ZombieRail[1][3]},
					new State(PLAYER_NINJA_STAND_R1 + 0, 0 | SF_QUICK_CALL, InitActorDecide), // &s_ZombieRail[1][4]},
					new State(PLAYER_NINJA_STAND_R1 + 0, ZOMBIE_RAIL_RATE, DoZombieMove).setNext(), // &s_ZombieRail[1][4]},
			}, { new State(PLAYER_NINJA_STAND_R2 + 0, ZOMBIE_RAIL_RATE * 2, NullZombie), // &s_ZombieRail[2][1]},
					new State(PLAYER_NINJA_STAND_R2 + 0, 0 | SF_QUICK_CALL, InitEnemyRail), // &s_ZombieRail[2][2]},
					new State(PLAYER_NINJA_STAND_R2 + 0, ZOMBIE_RAIL_RATE, NullZombie), // &s_ZombieRail[2][3]},
					new State(PLAYER_NINJA_STAND_R2 + 0, 0 | SF_QUICK_CALL, InitActorDecide), // &s_ZombieRail[2][4]},
					new State(PLAYER_NINJA_STAND_R2 + 0, ZOMBIE_RAIL_RATE, DoZombieMove).setNext(), // &s_ZombieRail[2][4]},
			}, { new State(PLAYER_NINJA_STAND_R3 + 0, ZOMBIE_RAIL_RATE * 2, NullZombie), // &s_ZombieRail[3][1]},
					new State(PLAYER_NINJA_STAND_R3 + 0, 0 | SF_QUICK_CALL, InitEnemyRail), // &s_ZombieRail[3][2]},
					new State(PLAYER_NINJA_STAND_R3 + 0, ZOMBIE_RAIL_RATE, NullZombie), // &s_ZombieRail[3][3]},
					new State(PLAYER_NINJA_STAND_R3 + 0, 0 | SF_QUICK_CALL, InitActorDecide), // &s_ZombieRail[3][4]},
					new State(PLAYER_NINJA_STAND_R3 + 0, ZOMBIE_RAIL_RATE, DoZombieMove).setNext(), // &s_ZombieRail[3][4]},
			}, { new State(PLAYER_NINJA_STAND_R4 + 0, ZOMBIE_RAIL_RATE * 2, NullZombie), // &s_ZombieRail[4][1]},
					new State(PLAYER_NINJA_STAND_R4 + 0, 0 | SF_QUICK_CALL, InitEnemyRail), // &s_ZombieRail[4][2]},
					new State(PLAYER_NINJA_STAND_R4 + 0, ZOMBIE_RAIL_RATE, NullZombie), // &s_ZombieRail[4][3]},
					new State(PLAYER_NINJA_STAND_R4 + 0, 0 | SF_QUICK_CALL, InitActorDecide), // &s_ZombieRail[4][4]},
					new State(PLAYER_NINJA_STAND_R4 + 0, ZOMBIE_RAIL_RATE, DoZombieMove).setNext(), // &s_ZombieRail[4][4]},
			}, };

	private enum ZombieStateGroup implements StateGroup {
		sg_ZombieStand(s_ZombieStand[0], s_ZombieStand[1], s_ZombieStand[2], s_ZombieStand[3], s_ZombieStand[4]),
		sg_ZombieRun(s_ZombieRun[0], s_ZombieRun[1], s_ZombieRun[2], s_ZombieRun[3], s_ZombieRun[4]),
		sg_ZombieFall(s_ZombieFall[0], s_ZombieFall[1], s_ZombieFall[2], s_ZombieFall[3], s_ZombieFall[4]),
		sg_ZombiePain(s_ZombiePain[0], s_ZombiePain[1], s_ZombiePain[2], s_ZombiePain[3], s_ZombiePain[4]),
		sg_ZombieRail(s_ZombieRail[0], s_ZombieRail[1], s_ZombieRail[2], s_ZombieRail[3], s_ZombieRail[4]);

		private final State[][] group;
		private int index = -1;

		ZombieStateGroup(State[]... states) {
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

	private static final Actor_Action_Set ZombieActionSet = new Actor_Action_Set(ZombieStateGroup.sg_ZombieStand,
			ZombieStateGroup.sg_ZombieRun, null, ZombieStateGroup.sg_ZombieFall, null, null, null,
			ZombieStateGroup.sg_ZombieRun, ZombieStateGroup.sg_ZombieRun, null, null, ZombieStateGroup.sg_ZombiePain,
			ZombieStateGroup.sg_ZombieRun, null, null, null, null, new StateGroup[] { ZombieStateGroup.sg_ZombieRail },
			new short[] { 1024 }, new StateGroup[] { ZombieStateGroup.sg_ZombieRail }, new short[] { 1024 }, null, null,
			null);

	public static void InitZombieStates() {
		for (ZombieStateGroup sg : ZombieStateGroup.values()) {
			for (int rot = 0; rot < sg.group.length; rot++) {
				State.InitState(sg.group[rot]);
			}
		}
	}

	private static int SetupZombie(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		u.Health = 100;
		u.StateEnd = s_ZombiePain[0][0];
		u.Rot = ZombieStateGroup.sg_ZombieRun;
		sp.xrepeat = PLAYER_NINJA_XREPEAT;
		sp.yrepeat = PLAYER_NINJA_YREPEAT;

		u.Attrib = ZombieAttrib;
		EnemyDefaults(SpriteNum, ZombieActionSet, ZombiePersonality);

		ChangeState(SpriteNum, s_ZombieRun[0][0]);
		DoActorSetSpeed(SpriteNum, NORM_SPEED);

		u.Radius = 280;
		u.Flags |= (SPR_XFLIP_TOGGLE);

		return (0);
	}

	public static void ZombieSaveable()
	{
		SaveData(InitEnemyRail);
		
		SaveData(DoZombieMove);
		SaveData(NullZombie);
		SaveData(DoZombiePain);

		SaveData(ZombiePersonality);
		SaveData(ZombieAttrib);

		SaveData(s_ZombieRun);
		SaveGroup(ZombieStateGroup.sg_ZombieRun);
		SaveData(s_ZombieStand);
		SaveGroup(ZombieStateGroup.sg_ZombieStand);
		SaveData(s_ZombiePain);
		SaveGroup(ZombieStateGroup.sg_ZombiePain);
		SaveData(s_ZombieRail);
		SaveGroup(ZombieStateGroup.sg_ZombieRail);
		SaveData(s_ZombieFall);
		SaveGroup(ZombieStateGroup.sg_ZombieFall);

		SaveData(ZombieActionSet);
	}
}
