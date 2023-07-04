package ru.m210projects.Wang.Enemies;

import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Wang.Actor.DoBeginJump;
import static ru.m210projects.Wang.Actor.DoFall;
import static ru.m210projects.Wang.Actor.DoJump;
import static ru.m210projects.Wang.Ai.DoActorPickClosePlayer;
import static ru.m210projects.Wang.Ai.DoActorSetSpeed;
import static ru.m210projects.Wang.Ai.NORM_SPEED;
import static ru.m210projects.Wang.Digi.DIGI_AHAMBIENT;
import static ru.m210projects.Wang.Digi.DIGI_AHEXPLODE;
import static ru.m210projects.Wang.Digi.DIGI_AHSCREAM;
import static ru.m210projects.Wang.Digi.DIGI_MINEBEEP;
import static ru.m210projects.Wang.Enemies.Coolie.EnemyDefaults;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_MISSILE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK_HITSCAN;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_RESTORE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_YCENTER;
import static ru.m210projects.Wang.Gameutils.DISTANCE;
import static ru.m210projects.Wang.Gameutils.DMG_SKULL_EXP;
import static ru.m210projects.Wang.Gameutils.HEALTH_SKULL;
import static ru.m210projects.Wang.Gameutils.MOVEx;
import static ru.m210projects.Wang.Gameutils.MOVEy;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.SEC;
import static ru.m210projects.Wang.Gameutils.SF_QUICK_CALL;
import static ru.m210projects.Wang.Gameutils.SPRITEp_BOS;
import static ru.m210projects.Wang.Gameutils.SPRITEp_SIZE_TOS;
import static ru.m210projects.Wang.Gameutils.SPRITEp_YOFF;
import static ru.m210projects.Wang.Gameutils.SPR_FALLING;
import static ru.m210projects.Wang.Gameutils.SPR_JUMPING;
import static ru.m210projects.Wang.Gameutils.SPR_XFLIP_TOGGLE;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.JWeapon.InitCaltrops;
import static ru.m210projects.Wang.JWeapon.InitFlashBomb;
import static ru.m210projects.Wang.JWeapon.InitPhosphorus;
import static ru.m210projects.Wang.JWeapon.InitSpriteChemBomb;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Names.BETTY_R0;
import static ru.m210projects.Wang.Names.BETTY_R1;
import static ru.m210projects.Wang.Names.BETTY_R2;
import static ru.m210projects.Wang.Names.BETTY_R3;
import static ru.m210projects.Wang.Names.BETTY_R4;
import static ru.m210projects.Wang.Names.SKULL_EXPLODE;
import static ru.m210projects.Wang.Names.SKULL_R0;
import static ru.m210projects.Wang.Names.SKULL_R1;
import static ru.m210projects.Wang.Names.SKULL_R2;
import static ru.m210projects.Wang.Names.SKULL_R3;
import static ru.m210projects.Wang.Names.SKULL_R4;
import static ru.m210projects.Wang.Names.STAT_DEAD_ACTOR;
import static ru.m210projects.Wang.Shrap.SpawnShrap;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.v3df_none;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.NewStateGroup;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Sprites.SpriteOverlapZ;
import static ru.m210projects.Wang.Sprites.change_sprite_stat;
import static ru.m210projects.Wang.Sprites.move_missile;
import static ru.m210projects.Wang.Type.MyTypes.DIV2;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.*;
import static ru.m210projects.Wang.Weapon.DamageData;
import static ru.m210projects.Wang.Weapon.DoDamageTest;
import static ru.m210projects.Wang.Weapon.DoFindGroundPoint;
import static ru.m210projects.Wang.Weapon.DoSerpRing;
import static ru.m210projects.Wang.Weapon.DoSuicide;
import static ru.m210projects.Wang.Weapon.InitSpriteGrenade;
import static ru.m210projects.Wang.Weapon.SetSuicide;
import static ru.m210projects.Wang.Weapon.SpawnLittleExp;
import static ru.m210projects.Wang.Weapon.SpawnMineExp;
import static ru.m210projects.Wang.Weapon.UpdateSinglePlayKills;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Type.ATTRIBUTE;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;

public class Skull {

	public enum SkullStateGroup implements StateGroup {
		sg_SkullWait(s_SkullWait[0], s_SkullWait[1], s_SkullWait[2], s_SkullWait[3], s_SkullWait[4]),
		sg_SkullJump(s_SkullJump[0], s_SkullJump[1], s_SkullJump[2], s_SkullJump[3], s_SkullJump[4]),
		sg_BettyWait(s_BettyWait[0], s_BettyWait[1], s_BettyWait[2], s_BettyWait[3], s_BettyWait[4]),
		sg_BettyJump(s_BettyJump[0], s_BettyJump[1], s_BettyJump[2], s_BettyJump[3], s_BettyJump[4]),
		sg_SkullRing(s_SkullRing[0], s_SkullRing[1], s_SkullRing[2], s_SkullRing[3], s_SkullRing[4]);

		private final State[][] group;
		private int index = -1;

		SkullStateGroup(State[]... states) {
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

	public static void InitSkullStates() {
		for (SkullStateGroup sg : SkullStateGroup.values()) {
			for (int rot = 0; rot < sg.group.length; rot++) {
				State.InitState(sg.group[rot]);
			}
		}
		
		State.InitState(s_SkullExplode);
		State.InitState(s_BettyExplode);
	}

	public static final int SKULL_RATE = 10;

	private static final Animator DoSkullWait = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoSkullWait(SpriteNum) != 0;
		}
	};

	public static final State s_SkullWait[][] = { { new State(SKULL_R0 + 0, SKULL_RATE, DoSkullWait).setNext(),// s_SkullWait[0][0]},
			}, { new State(SKULL_R1 + 0, SKULL_RATE, DoSkullWait).setNext(),// s_SkullWait[1][0]},
			}, { new State(SKULL_R2 + 0, SKULL_RATE, DoSkullWait).setNext(),// s_SkullWait[2][0]},
			}, { new State(SKULL_R3 + 0, SKULL_RATE, DoSkullWait).setNext(),// s_SkullWait[3][0]},
			}, { new State(SKULL_R4 + 0, SKULL_RATE, DoSkullWait).setNext(),// s_SkullWait[4][0]},
			} };

	public static final ATTRIBUTE SkullAttrib = new ATTRIBUTE(new short[] { 60, 80, 100, 130 }, // Speeds
			new short[] { 3, 0, -2, -3 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { DIGI_AHAMBIENT, 0, 0, 0, DIGI_AHSCREAM, DIGI_AHEXPLODE, 0, 0, 0, 0 });

	//////////////////////
	//
	// SKULL for Serp God
	//
	//////////////////////

	private static final Animator DoSerpRing = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoSerpRing(SpriteNum) != 0;
		}
	};

	public static final State s_SkullRing[][] = { { new State(SKULL_R0 + 0, SKULL_RATE, DoSerpRing).setNext(),// s_SkullRing[0][0]},
			}, { new State(SKULL_R1 + 0, SKULL_RATE, DoSerpRing).setNext(),// s_SkullRing[1][0]},
			}, { new State(SKULL_R2 + 0, SKULL_RATE, DoSerpRing).setNext(),// s_SkullRing[2][0]},
			}, { new State(SKULL_R3 + 0, SKULL_RATE, DoSerpRing).setNext(),// s_SkullRing[3][0]},
			}, { new State(SKULL_R4 + 0, SKULL_RATE, DoSerpRing).setNext(),// s_SkullRing[4][0]},
			} };

	//////////////////////
	//
	// SKULL Jump
	//
	//////////////////////

	private static final Animator DoSkullJump = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoSkullJump(SpriteNum) != 0;
		}
	};

	private static final State s_SkullJump[][] = { { new State(SKULL_R0 + 0, SKULL_RATE, DoSkullJump).setNext(),// s_SkullJump[0][0]},
			}, { new State(SKULL_R1 + 0, SKULL_RATE, DoSkullJump).setNext(),// s_SkullJump[1][0]},
			}, { new State(SKULL_R2 + 0, SKULL_RATE, DoSkullJump).setNext(),// s_SkullJump[2][0]},
			}, { new State(SKULL_R3 + 0, SKULL_RATE, DoSkullJump).setNext(),// s_SkullJump[3][0]},
			}, { new State(SKULL_R4 + 0, SKULL_RATE, DoSkullJump).setNext(),// s_SkullJump[4][0]},
			} };

	//////////////////////
	//
	// SKULL Explode
	//
	//////////////////////

	public static final int SKULL_EXPLODE_RATE = 11;

	private static final Animator DoSkullSpawnShrap = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoSkullSpawnShrap(SpriteNum) != 0;
		}
	};

	public static final State s_SkullExplode[] = { new State(SKULL_EXPLODE + 0, 1, null), // s_SkullExplode[1]},
			new State(SKULL_EXPLODE + 0, SF_QUICK_CALL, DoDamageTest), // s_SkullExplode[2]},
			new State(SKULL_EXPLODE + 0, SKULL_EXPLODE_RATE, null), // s_SkullExplode[3]},
			new State(SKULL_EXPLODE + 1, SKULL_EXPLODE_RATE, null), // s_SkullExplode[4]},
			new State(SKULL_EXPLODE + 2, SF_QUICK_CALL, DoSkullSpawnShrap), // s_SkullExplode[5]},
			new State(SKULL_EXPLODE + 2, SKULL_EXPLODE_RATE, null), // s_SkullExplode[6]},
			new State(SKULL_EXPLODE + 3, SKULL_EXPLODE_RATE, null), // s_SkullExplode[7]},
			new State(SKULL_EXPLODE + 4, SKULL_EXPLODE_RATE, null), // s_SkullExplode[8]},
			new State(SKULL_EXPLODE + 5, SKULL_EXPLODE_RATE, null), // s_SkullExplode[9]},
			new State(SKULL_EXPLODE + 6, SKULL_EXPLODE_RATE, null), // s_SkullExplode[10]},
			new State(SKULL_EXPLODE + 7, SKULL_EXPLODE_RATE, null), // s_SkullExplode[11]},
			new State(SKULL_EXPLODE + 8, SKULL_EXPLODE_RATE, null), // s_SkullExplode[12]},
			new State(SKULL_EXPLODE + 9, SKULL_EXPLODE_RATE, null), // s_SkullExplode[13]},
			new State(SKULL_EXPLODE + 10, SKULL_EXPLODE_RATE, null), // s_SkullExplode[14]},
			new State(SKULL_EXPLODE + 11, SKULL_EXPLODE_RATE, null), // s_SkullExplode[15]},
			new State(SKULL_EXPLODE + 12, SKULL_EXPLODE_RATE, null), // s_SkullExplode[16]},
			new State(SKULL_EXPLODE + 13, SKULL_EXPLODE_RATE, null), // s_SkullExplode[17]},
			new State(SKULL_EXPLODE + 13, SKULL_EXPLODE_RATE, DoSuicide).setNext(),// s_SkullExplode[17]}
	};

	public static int SetupSkull(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, SKULL_R0, s_SkullWait[0][0]);
			u.Health = HEALTH_SKULL;
		}

		ChangeState(SpriteNum, s_SkullWait[0][0]);
		u.Attrib = SkullAttrib;
		DoActorSetSpeed(SpriteNum, NORM_SPEED);
		u.StateEnd = s_SkullExplode[0];
		u.Rot = SkullStateGroup.sg_SkullWait;

		u.ID = SKULL_R0;

		EnemyDefaults(SpriteNum, null, null);
		sp.clipdist = (128 + 64) >> 2;
		u.Flags |= (SPR_XFLIP_TOGGLE);
		sp.cstat |= (CSTAT_SPRITE_YCENTER);

		u.Radius = 400;

		if (SPRITEp_BOS(sp) > u.loz - Z(16)) {
			sp.z = u.loz + Z(SPRITEp_YOFF(sp));

			u.loz = sp.z;
			// leave 8 pixels above the ground
			sp.z += SPRITEp_SIZE_TOS(sp) - Z(3);
			;
		} else {
			u.Counter = (short) RANDOM_P2(2048);
			u.sz = sp.z;
		}

		return (0);
	}

	private static int DoSkullMove(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		int dax, day, daz;

		dax = MOVEx(sp.xvel, sp.ang);
		day = MOVEy(sp.xvel, sp.ang);
		daz = sp.zvel;

		u.ret = move_missile(SpriteNum, dax, day, daz, Z(16), Z(16), CLIPMASK_MISSILE, ACTORMOVETICS);

		DoFindGroundPoint(SpriteNum);
		return (0);
	}

	public static int DoSkullBeginDeath(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		int i, num_ord = 0;
		
		// Decrease for Serp God
		if (sp.owner >= 0)
			pUser[sp.owner].Counter--;

		// starts the explosion that does the actual damage

		sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		
		switch (sp.hitag) {
		case 1:
			if (sp.lotag != 0)
				num_ord = sp.lotag;
			else
				num_ord = 2;
			if (num_ord > 3)
				num_ord = 3;
			for (i = 0; i < num_ord; i++) {
				sp.ang = NORM_ANGLE(sp.ang + (i * 1024));
				InitSpriteChemBomb(SpriteNum);
			}
			break;

		case 2:
			if (sp.lotag != 0)
				num_ord = sp.lotag;
			else
				num_ord = 5;
			if (num_ord > 10)
				num_ord = 10;
			for (i = 0; i < num_ord; i++) {
				sp.ang = NORM_ANGLE(RANDOM_RANGE(2048));
				InitCaltrops(SpriteNum);
			}
			break;

		case 3:
			UpdateSinglePlayKills(SpriteNum, null);
			InitFlashBomb(SpriteNum);
			break;

		case 4:
			if (sp.lotag != 0)
				num_ord = sp.lotag;
			else
				num_ord = 5;
			if (num_ord > 10)
				num_ord = 10;
			for (i = 0; i < num_ord; i++) {
				sp.ang = NORM_ANGLE(sp.ang + (i * (2048 / num_ord)));
				InitSpriteGrenade(SpriteNum);
			}
			break;
		default:
			SpawnMineExp(SpriteNum);
			for (i = 0; i < 3; i++) {
				sp.ang = NORM_ANGLE(RANDOM_RANGE(2048));
				InitPhosphorus(SpriteNum);
			}
			break;
		}

		u.RotNum = 0;
		u.Tics = 0;

		u.ID = SKULL_R0;
		u.Radius = DamageData[DMG_SKULL_EXP].radius;
		u.OverlapZ = Z(64);
		change_sprite_stat(SpriteNum, STAT_DEAD_ACTOR);
		sp.shade = -40;

		SpawnLittleExp(SpriteNum);
		SetSuicide(SpriteNum);

		return (0);
	}

	private static int DoSkullJump(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if (sp.xvel != 0)
			DoSkullMove(SpriteNum);
		else
			sp.ang = NORM_ANGLE(sp.ang + (64 * ACTORMOVETICS));

		if (TEST(u.Flags, SPR_JUMPING)) {
			DoJump(SpriteNum);
		} else if (TEST(u.Flags, SPR_FALLING)) {
			DoFall(SpriteNum);

			// jump/fall type
			if (sp.xvel != 0) {
				int dist = DISTANCE(sp.x, sp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

				if (dist < 1000 && SpriteOverlapZ(SpriteNum, u.tgt_sp, Z(32))) {
					UpdateSinglePlayKills(SpriteNum, null);
					DoSkullBeginDeath(SpriteNum);
					return (0);
				}

				if ((sp.z > u.loz - Z(36))) {
					sp.z = u.loz - Z(36);
					UpdateSinglePlayKills(SpriteNum, null);
					DoSkullBeginDeath(SpriteNum);
					return (0);
				}
			}
			// non jumping type
			else {
				if (u.jump_speed > 200) {
					UpdateSinglePlayKills(SpriteNum, null);
					DoSkullBeginDeath(SpriteNum);
				}
			}

		} else {
			UpdateSinglePlayKills(SpriteNum, null);
			DoSkullBeginDeath(SpriteNum);
		}

		return (0);
	}

	// actor does a sine wave about u.sz - this is the z mid point
	private static final int SKULL_BOB_AMT = (Z(16));

	private static int DoSkullBob(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		u.Counter = (short) ((u.Counter + (ACTORMOVETICS << 3) + (ACTORMOVETICS << 1)) & 2047);
		sp.z = u.sz + ((SKULL_BOB_AMT * (int) sintable[u.Counter]) >> 14)
				+ ((DIV2(SKULL_BOB_AMT) * (int) sintable[u.Counter]) >> 14);

		return (0);
	}

	private static int DoSkullSpawnShrap(int SpriteNum) {
		SpawnShrap(SpriteNum, -1);

		return (0);
	}

	private static int DoSkullWait(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		int dist = DISTANCE(sp.x, sp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

		DoActorPickClosePlayer(SpriteNum);

		// if (dist < u.active_range)
		// return(0);

		if ((u.WaitTics -= ACTORMOVETICS) <= 0) {
			PlaySound(DIGI_AHSCREAM, sp, v3df_none);
			u.WaitTics = (short) (SEC(3) + RANDOM_RANGE(360));
		}

		// below the floor type
		if (sp.z > u.loz) {
			// look for closest player every once in a while
			if (dist < 3500) {
				sp.xvel = 0;
				u.jump_speed = -600;
				NewStateGroup(SpriteNum, SkullStateGroup.sg_SkullJump);
				DoBeginJump(SpriteNum);
			}
		} else
		// above the floor type
		{
			sp.ang = NORM_ANGLE(sp.ang + (48 * ACTORMOVETICS));

			DoSkullBob(SpriteNum);

			if (dist < 8000) {
				sp.ang = engine.getangle(sprite[u.tgt_sp].x - sp.x, sprite[u.tgt_sp].y - sp.y);
				sp.xvel = (short) (128 + (RANDOM_P2(256 << 8) >> 8));
				u.jump_speed = -700;
				NewStateGroup(SpriteNum, SkullStateGroup.sg_SkullJump);
				DoBeginJump(SpriteNum);
			}
		}

		return (0);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////

	//////////////////////
	//
	// BETTY Wait
	//
	//////////////////////

	public static final int BETTY_RATE = 10;

	private static final Animator DoBettyWait = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoBettyWait(SpriteNum) != 0;
		}
	};

	private static final State s_BettyWait[][] = { { new State(BETTY_R0 + 0, BETTY_RATE, DoBettyWait), // s_BettyWait[0][1]},
			new State(BETTY_R0 + 1, BETTY_RATE, DoBettyWait), // s_BettyWait[0][2]},
			new State(BETTY_R0 + 2, BETTY_RATE, DoBettyWait),// s_BettyWait[0][0]},
			}, { new State(BETTY_R1 + 0, BETTY_RATE, DoBettyWait), // s_BettyWait[1][1]},
					new State(BETTY_R1 + 1, BETTY_RATE, DoBettyWait), // s_BettyWait[1][2]},
					new State(BETTY_R1 + 2, BETTY_RATE, DoBettyWait),// s_BettyWait[1][0]},
			}, { new State(BETTY_R2 + 0, BETTY_RATE, DoBettyWait), // s_BettyWait[2][1]},
					new State(BETTY_R2 + 1, BETTY_RATE, DoBettyWait), // s_BettyWait[2][2]},
					new State(BETTY_R2 + 2, BETTY_RATE, DoBettyWait),// s_BettyWait[2][0]},
			}, { new State(BETTY_R3 + 0, BETTY_RATE, DoBettyWait), // s_BettyWait[3][1]},
					new State(BETTY_R3 + 1, BETTY_RATE, DoBettyWait), // s_BettyWait[3][2]},
					new State(BETTY_R3 + 2, BETTY_RATE, DoBettyWait),// s_BettyWait[3][0]},
			}, { new State(BETTY_R4 + 0, BETTY_RATE, DoBettyWait), // s_BettyWait[4][1]},
					new State(BETTY_R4 + 1, BETTY_RATE, DoBettyWait), // s_BettyWait[4][2]},
					new State(BETTY_R4 + 2, BETTY_RATE, DoBettyWait),// s_BettyWait[4][0]},
			} };

	private static final ATTRIBUTE BettyAttrib = new ATTRIBUTE(new short[] { 60, 80, 100, 130 }, // Speeds
			new short[] { 3, 0, -2, -3 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });

	//////////////////////
	//
	// BETTY Jump
	//
	//////////////////////

	private static final Animator DoBettyJump = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoBettyJump(SpriteNum) != 0;
		}
	};

	private static final State s_BettyJump[][] = { { new State(BETTY_R0 + 0, BETTY_RATE, DoBettyJump).setNext(),// s_BettyJump[0][0]},
			}, { new State(BETTY_R1 + 0, BETTY_RATE, DoBettyJump).setNext(),// s_BettyJump[1][0]},
			}, { new State(BETTY_R2 + 0, BETTY_RATE, DoBettyJump).setNext(),// s_BettyJump[2][0]},
			}, { new State(BETTY_R3 + 0, BETTY_RATE, DoBettyJump).setNext(),// s_BettyJump[3][0]},
			}, { new State(BETTY_R4 + 0, BETTY_RATE, DoBettyJump).setNext(),// s_BettyJump[4][0]},
			} };

	//////////////////////
	//
	// BETTY Explode
	//
	//////////////////////

	public static final int BETTY_EXPLODE_RATE = 11;
	public static final int BETTY_EXPLODE = BETTY_R0;

	private static final State s_BettyExplode[] = { new State(BETTY_EXPLODE + 0, SF_QUICK_CALL, DoDamageTest), // s_BettyExplode[1]},
			new State(BETTY_EXPLODE + 0, BETTY_EXPLODE_RATE, DoSuicide),// s_BettyExplode[0]}
	};

	public static int SetupBetty(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, BETTY_R0, s_BettyWait[0][0]);
			u.Health = HEALTH_SKULL;
		}

		ChangeState(SpriteNum, s_BettyWait[0][0]);
		u.Attrib = BettyAttrib;
		DoActorSetSpeed(SpriteNum, NORM_SPEED);
		u.StateEnd = s_BettyExplode[0];
		u.Rot = SkullStateGroup.sg_BettyWait;

		u.ID = BETTY_R0;

		EnemyDefaults(SpriteNum, null, null);
		sp.clipdist = (128 + 64) >> 2;
		u.Flags |= (SPR_XFLIP_TOGGLE);
		sp.cstat |= (CSTAT_SPRITE_YCENTER);

		u.Radius = 400;

		if (SPRITEp_BOS(sp) > u.loz - Z(16)) {
			sp.z = u.loz + Z(SPRITEp_YOFF(sp));

			u.loz = sp.z;
			// leave 8 pixels above the ground
			sp.z += SPRITEp_SIZE_TOS(sp) - Z(3);
			;
		} else {
			u.Counter = (short) RANDOM_P2(2048);
			u.sz = sp.z;
		}

		return (0);
	}

	private static int DoBettyMove(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		int dax, day, daz;

		dax = MOVEx(sp.xvel, sp.ang);
		day = MOVEy(sp.xvel, sp.ang);
		daz = sp.zvel;

		u.ret = move_missile(SpriteNum, dax, day, daz, Z(16), Z(16), CLIPMASK_MISSILE, ACTORMOVETICS);

		DoFindGroundPoint(SpriteNum);
		return (0);
	}

	public static int DoBettyBeginDeath(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		int i, num_ord = 0;

		// starts the explosion that does the actual damage
		sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		
		switch (sp.hitag) {
		case 1:
			if (sp.lotag != 0)
				num_ord = sp.lotag;
			else
				num_ord = 2;
			if (num_ord > 3)
				num_ord = 3;
			for (i = 0; i < num_ord; i++) {
				sp.ang = NORM_ANGLE(sp.ang + (i * 1024));
				InitSpriteChemBomb(SpriteNum);
			}
			break;

		case 2:
			if (sp.lotag != 0)
				num_ord = sp.lotag;
			else
				num_ord = 5;
			if (num_ord > 10)
				num_ord = 10;
			for (i = 0; i < num_ord; i++) {
				sp.ang = NORM_ANGLE(RANDOM_RANGE(2048));
				InitCaltrops(SpriteNum);
			}
			break;

		case 3:
			InitFlashBomb(SpriteNum);
			break;

		case 4:
			if (sp.lotag != 0)
				num_ord = sp.lotag;
			else
				num_ord = 5;
			if (num_ord > 10)
				num_ord = 10;
			for (i = 0; i < num_ord; i++) {
				sp.ang = NORM_ANGLE(sp.ang + (i * (2048 / num_ord)));
				InitSpriteGrenade(SpriteNum);
			}
			break;
		default:
			for (i = 0; i < 5; i++) {
				sp.ang = NORM_ANGLE(RANDOM_RANGE(2048));
				InitPhosphorus(SpriteNum);
				SpawnMineExp(SpriteNum);
			}
			break;
		}

		u.RotNum = 0;
		u.Tics = 0;

		u.ID = BETTY_R0;
		u.Radius = DamageData[DMG_SKULL_EXP].radius;// *DamageRadiusBetty;
		u.OverlapZ = Z(64);
		change_sprite_stat(SpriteNum, STAT_DEAD_ACTOR);
		sp.shade = -40;

		SpawnLittleExp(SpriteNum);
		SetSuicide(SpriteNum);

		return (0);
	}

	private static int DoBettyJump(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if (sp.xvel != 0)
			DoBettyMove(SpriteNum);
		else
			sp.ang = NORM_ANGLE(sp.ang + (64 * ACTORMOVETICS));

		if (TEST(u.Flags, SPR_JUMPING)) {
			DoJump(SpriteNum);
		} else if (TEST(u.Flags, SPR_FALLING)) {
			DoFall(SpriteNum);

			// jump/fall type
			if (sp.xvel != 0) {

				int dist = DISTANCE(sp.x, sp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

				if (dist < 1000 && SpriteOverlapZ(SpriteNum, u.tgt_sp, Z(32))) {
					UpdateSinglePlayKills(SpriteNum, null);
					DoBettyBeginDeath(SpriteNum);
					return (0);
				}

				if ((sp.z > u.loz - Z(36))) {
					sp.z = u.loz - Z(36);
					UpdateSinglePlayKills(SpriteNum, null);
					DoBettyBeginDeath(SpriteNum);
					return (0);
				}
			}
			// non jumping type
			else {
				if (u.jump_speed > 200) {
					UpdateSinglePlayKills(SpriteNum, null);
					DoBettyBeginDeath(SpriteNum);
				}
			}

		} else {
			UpdateSinglePlayKills(SpriteNum, null);
			DoBettyBeginDeath(SpriteNum);
		}
		return (0);
	}

	// actor does a sine wave about u.sz - this is the z mid point
	public static final int BETTY_BOB_AMT = (Z(16));

	private static int DoBettyBob(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		u.Counter = (short) ((u.Counter + (ACTORMOVETICS << 3) + (ACTORMOVETICS << 1)) & 2047);
		sp.z = u.sz + ((BETTY_BOB_AMT * (int) sintable[u.Counter]) >> 14)
				+ ((DIV2(BETTY_BOB_AMT) * (int) sintable[u.Counter]) >> 14);

		return (0);
	}

	private static int DoBettyWait(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		int dist = DISTANCE(sp.x, sp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

		DoActorPickClosePlayer(SpriteNum);

		// if (dist < u.active_range)
		// return(0);

		if ((u.WaitTics -= ACTORMOVETICS) <= 0) {
			PlaySound(DIGI_MINEBEEP, sp, v3df_none);
			u.WaitTics = (short) SEC(3);
		}

		// below the floor type
		if (sp.z > u.loz) {
			// look for closest player every once in a while
			if (dist < 3500) {
				sp.xvel = 0;
				u.jump_speed = -600;
				NewStateGroup(SpriteNum, SkullStateGroup.sg_BettyJump);
				DoBeginJump(SpriteNum);
			}
		} else
		// above the floor type
		{
			sp.ang = NORM_ANGLE(sp.ang + (48 * ACTORMOVETICS));

			DoBettyBob(SpriteNum);

			if (dist < 8000) {
				sp.ang = engine.getangle(sprite[u.tgt_sp].x - sp.x, sprite[u.tgt_sp].y - sp.y);
				sp.xvel = (short) (128 + (RANDOM_P2(256 << 8) >> 8));
				u.jump_speed = -700;
				NewStateGroup(SpriteNum, SkullStateGroup.sg_BettyJump);
				DoBeginJump(SpriteNum);
			}
		}

		return (0);
	}
	
	public static void SkullSaveable()
	{
		SaveData(DoSerpRing);
		SaveData(DoSkullJump);
		SaveData(DoSkullSpawnShrap);
		SaveData(DoSkullWait);
		SaveData(DoBettyJump);
		SaveData(DoBettyWait);
		
		SaveData(s_SkullWait);
		SaveGroup(SkullStateGroup.sg_SkullWait);

		SaveData(SkullAttrib);

		SaveData(s_SkullRing);
		SaveGroup(SkullStateGroup.sg_SkullRing);
		SaveData(s_SkullJump);
		SaveGroup(SkullStateGroup.sg_SkullJump);
		SaveData(s_SkullExplode);
		SaveData(s_BettyWait);
		SaveGroup(SkullStateGroup.sg_BettyWait);

		SaveData(BettyAttrib);

		SaveData(s_BettyJump);
		SaveGroup(SkullStateGroup.sg_BettyJump);
		SaveData(s_BettyExplode);
	}
}
