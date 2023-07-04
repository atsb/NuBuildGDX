package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.CEIL;
import static ru.m210projects.Build.Engine.FLOOR;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Wang.Actor.DoBeginJump;
import static ru.m210projects.Wang.Break.HitBreakWall;
import static ru.m210projects.Wang.Break.SetupSpriteForBreak;
import static ru.m210projects.Wang.Digi.DIGI_BIGITEM;
import static ru.m210projects.Wang.Digi.DIGI_CALTROPS;
import static ru.m210projects.Wang.Digi.DIGI_CHEMBOUNCE;
import static ru.m210projects.Wang.Digi.DIGI_CHEMGAS;
import static ru.m210projects.Wang.Digi.DIGI_FIREBALL1;
import static ru.m210projects.Wang.Digi.DIGI_GASPOP;
import static ru.m210projects.Wang.Digi.DIGI_GIBS1;
import static ru.m210projects.Wang.Digi.DIGI_GIBS2;
import static ru.m210projects.Wang.Digi.DIGI_GIBS3;
import static ru.m210projects.Wang.Digi.DIGI_MINEBEEP;
import static ru.m210projects.Wang.Digi.DIGI_THROW;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.screenpeek;
import static ru.m210projects.Wang.Game.tmp_ptr;
import static ru.m210projects.Wang.Gameutils.ACTOR_GRAVITY;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_MISSILE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK_HITSCAN;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BREAKABLE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_INVISIBLE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_WALL;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_XFLIP;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_YCENTER;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_YFLIP;
import static ru.m210projects.Wang.Gameutils.DISTANCE;
import static ru.m210projects.Wang.Gameutils.DMG_FLASHBOMB;
import static ru.m210projects.Wang.Gameutils.FAF_ConnectArea;
import static ru.m210projects.Wang.Gameutils.FindDistance3D;
import static ru.m210projects.Wang.Gameutils.HIT_MASK;
import static ru.m210projects.Wang.Gameutils.HIT_PLAX_WALL;
import static ru.m210projects.Wang.Gameutils.HIT_SECTOR;
import static ru.m210projects.Wang.Gameutils.HIT_SPRITE;
import static ru.m210projects.Wang.Gameutils.HIT_WALL;
import static ru.m210projects.Wang.Gameutils.MAX_PAIN;
import static ru.m210projects.Wang.Gameutils.MOVEx;
import static ru.m210projects.Wang.Gameutils.MOVEy;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.NORM_SPRITE;
import static ru.m210projects.Wang.Gameutils.NORM_WALL;
import static ru.m210projects.Wang.Gameutils.PF_CRAWLING;
import static ru.m210projects.Wang.Gameutils.PF_DIVING;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.SEC;
import static ru.m210projects.Wang.Gameutils.SECTFU_DONT_COPY_PALETTE;
import static ru.m210projects.Wang.Gameutils.SPRITEp_BOS;
import static ru.m210projects.Wang.Gameutils.SPRITEp_MID;
import static ru.m210projects.Wang.Gameutils.SPRITEp_SIZE_Z;
import static ru.m210projects.Wang.Gameutils.SPRITEp_SIZE_Z_2_YREPEAT;
import static ru.m210projects.Wang.Gameutils.SPRITEp_TOS;
import static ru.m210projects.Wang.Gameutils.SPRX_BREAKABLE;
import static ru.m210projects.Wang.Gameutils.SPRX_BURNABLE;
import static ru.m210projects.Wang.Gameutils.SPRX_PLAYER_OR_ENEMY;
import static ru.m210projects.Wang.Gameutils.SPR_ACTIVE;
import static ru.m210projects.Wang.Gameutils.SPR_BOUNCE;
import static ru.m210projects.Wang.Gameutils.SPR_UNDERWATER;
import static ru.m210projects.Wang.Gameutils.SPR_XFLIP_TOGGLE;
import static ru.m210projects.Wang.Gameutils.SP_TAG5;
import static ru.m210projects.Wang.Gameutils.SP_TAG6;
import static ru.m210projects.Wang.Gameutils.SpriteInUnderwaterArea;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL1;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.JTags.LUMINOUS;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Names.EXP;
import static ru.m210projects.Wang.Names.FIREBALL1;
import static ru.m210projects.Wang.Names.FIREBALL_FLAMES;
import static ru.m210projects.Wang.Names.STAT_DEAD_ACTOR;
import static ru.m210projects.Wang.Names.STAT_ITEM;
import static ru.m210projects.Wang.Names.STAT_MISSILE;
import static ru.m210projects.Wang.Names.STAT_SKIP4;
import static ru.m210projects.Wang.Names.SUMO_RUN_R0;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER3;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER6;
import static ru.m210projects.Wang.Palette.SetFadeAmt;
import static ru.m210projects.Wang.Rooms.FAF_Sector;
import static ru.m210projects.Wang.Rooms.FAFcansee;
import static ru.m210projects.Wang.Rooms.zofslope;
import static ru.m210projects.Wang.Sector.DoMatchEverything;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.PlayerLowHealthPainVocs;
import static ru.m210projects.Wang.Sound.PlayerSound;
import static ru.m210projects.Wang.Sound.Set3DSoundOwner;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_doppler;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sound.v3df_none;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.DoActorZrange;
import static ru.m210projects.Wang.Sprites.KillSprite;
import static ru.m210projects.Wang.Sprites.MoveSkip4;
import static ru.m210projects.Wang.Sprites.SetAttach;
import static ru.m210projects.Wang.Sprites.SetOwner;
import static ru.m210projects.Wang.Sprites.SpawnSprite;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Sprites.change_sprite_stat;
import static ru.m210projects.Wang.Sprites.move_missile;
import static ru.m210projects.Wang.Tags.TAG_WALL_BREAK;
import static ru.m210projects.Wang.Type.MyTypes.DIV2;
import static ru.m210projects.Wang.Type.MyTypes.DIV4;
import static ru.m210projects.Wang.Type.MyTypes.DTEST;
import static ru.m210projects.Wang.Type.MyTypes.ON;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Weapon.ActorPain;
import static ru.m210projects.Wang.Weapon.CALTROPS;
import static ru.m210projects.Wang.Weapon.CHEMBOMB_VELOCITY;
import static ru.m210projects.Wang.Weapon.DoFlamesDamageTest;
import static ru.m210projects.Wang.Weapon.DoSuicide;
import static ru.m210projects.Wang.Weapon.GetDamage;
import static ru.m210projects.Wang.Weapon.GlobalSkipZrange;
import static ru.m210projects.Wang.Weapon.HORIZ_MULT;
import static ru.m210projects.Wang.Weapon.HelpMissileLateral;
import static ru.m210projects.Wang.Weapon.MISSILEMOVETICS;
import static ru.m210projects.Wang.Weapon.MUSHROOM_CLOUD;
import static ru.m210projects.Wang.Weapon.MissileHitDiveArea;
import static ru.m210projects.Wang.Weapon.MissileSetPos;
import static ru.m210projects.Wang.Weapon.PHOSPHORUS;
import static ru.m210projects.Wang.Weapon.PUFF;
import static ru.m210projects.Wang.Weapon.QueueWallBlood;
import static ru.m210projects.Wang.Weapon.RADIATION_CLOUD;
import static ru.m210projects.Wang.Weapon.ScaleSpriteVector;
import static ru.m210projects.Wang.Weapon.SetSuicide;
import static ru.m210projects.Wang.Weapon.ShellCount;
import static ru.m210projects.Wang.Weapon.SlopeBounce;
import static ru.m210projects.Wang.Weapon.SpawnBubble;
import static ru.m210projects.Wang.Weapon.SpawnFireballExp;
import static ru.m210projects.Wang.Weapon.SpawnFireballFlames;
import static ru.m210projects.Wang.Weapon.SpawnGrenadeExp;
import static ru.m210projects.Wang.Weapon.StatDamageList;
import static ru.m210projects.Wang.Weapon.WallBounce;
import static ru.m210projects.Wang.Weapon.s_FireballFlames;
import static ru.m210projects.Wang.Weapon.s_GoreFloorSplash;
import static ru.m210projects.Wang.Weapon.s_GoreSplash;
import static ru.m210projects.Wang.Weapon.s_Puff;
import static ru.m210projects.Wang.Weapon.s_ShotgunShellShrap;
import static ru.m210projects.Wang.Weapon.s_UziShellShrap;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;
import ru.m210projects.Wang.Type.VOC3D;
import static ru.m210projects.Wang.Type.Saveable.*;

public class JWeapon {

	public static final int CHEMTICS = SEC(40);

	public static final int GOREDrip = 1562; // 2430
	public static final int BLOODSPRAY_RATE = 20;

	private static final Animator BloodSprayFall = new Animator() {
		@Override
		public boolean invoke(int spr) {
			BloodSprayFall(spr);
			return false;
		}
	};

	public static final State s_BloodSpray[] = { new State(GOREDrip + 0, BLOODSPRAY_RATE, BloodSprayFall),
			new State(GOREDrip + 1, BLOODSPRAY_RATE, BloodSprayFall),
			new State(GOREDrip + 2, BLOODSPRAY_RATE, BloodSprayFall),
			new State(GOREDrip + 3, BLOODSPRAY_RATE, BloodSprayFall), new State(GOREDrip + 3, 100, DoSuicide), };

	public static final int EXP_RATE = 2;
	public static final State s_PhosphorExp[] = { new State(EXP + 0, EXP_RATE, null),
			new State(EXP + 1, EXP_RATE, null), new State(EXP + 2, EXP_RATE, null), new State(EXP + 3, EXP_RATE, null),
			new State(EXP + 4, EXP_RATE, null), new State(EXP + 5, EXP_RATE, null), new State(EXP + 6, EXP_RATE, null),
			new State(EXP + 7, EXP_RATE, null), new State(EXP + 8, EXP_RATE, null), new State(EXP + 9, EXP_RATE, null),
			new State(EXP + 10, EXP_RATE, null), new State(EXP + 11, EXP_RATE, null),
			new State(EXP + 12, EXP_RATE, null), new State(EXP + 13, EXP_RATE, null),
			new State(EXP + 14, EXP_RATE, null), new State(EXP + 15, EXP_RATE, null),
			new State(EXP + 16, EXP_RATE, null), new State(EXP + 17, EXP_RATE, null),
			new State(EXP + 18, EXP_RATE, null), new State(EXP + 19, EXP_RATE, null),
			new State(EXP + 20, 100, DoSuicide), };

	public static final int MUSHROOM_RATE = 25;

	public static final State s_NukeMushroom[] = { new State(MUSHROOM_CLOUD + 0, MUSHROOM_RATE, null),
			new State(MUSHROOM_CLOUD + 1, MUSHROOM_RATE, null), new State(MUSHROOM_CLOUD + 2, MUSHROOM_RATE, null),
			new State(MUSHROOM_CLOUD + 3, MUSHROOM_RATE, null), new State(MUSHROOM_CLOUD + 4, MUSHROOM_RATE, null),
			new State(MUSHROOM_CLOUD + 5, MUSHROOM_RATE, null), new State(MUSHROOM_CLOUD + 6, MUSHROOM_RATE, null),
			new State(MUSHROOM_CLOUD + 7, MUSHROOM_RATE, null), new State(MUSHROOM_CLOUD + 8, MUSHROOM_RATE, null),
			new State(MUSHROOM_CLOUD + 9, MUSHROOM_RATE, null), new State(MUSHROOM_CLOUD + 10, MUSHROOM_RATE, null),
			new State(MUSHROOM_CLOUD + 11, MUSHROOM_RATE, null), new State(MUSHROOM_CLOUD + 12, MUSHROOM_RATE, null),
			new State(MUSHROOM_CLOUD + 13, MUSHROOM_RATE, null), new State(MUSHROOM_CLOUD + 14, MUSHROOM_RATE, null),
			new State(MUSHROOM_CLOUD + 15, MUSHROOM_RATE, null), new State(MUSHROOM_CLOUD + 16, MUSHROOM_RATE, null),
			new State(MUSHROOM_CLOUD + 17, MUSHROOM_RATE, null), new State(MUSHROOM_CLOUD + 18, MUSHROOM_RATE, null),
			new State(MUSHROOM_CLOUD + 19, 100, DoSuicide), };

	public static final Animator DoRadiationCloud = new Animator() {
		@Override
		public boolean invoke(int spr) {
			DoRadiationCloud(spr);
			return false;
		}
	};

	public static final int RADIATION_RATE = 16;

	public static final State s_RadiationCloud[] = { new State(RADIATION_CLOUD + 0, RADIATION_RATE, DoRadiationCloud),
			new State(RADIATION_CLOUD + 1, RADIATION_RATE, DoRadiationCloud),
			new State(RADIATION_CLOUD + 2, RADIATION_RATE, DoRadiationCloud),
			new State(RADIATION_CLOUD + 3, RADIATION_RATE, DoRadiationCloud),
			new State(RADIATION_CLOUD + 4, RADIATION_RATE, DoRadiationCloud),
			new State(RADIATION_CLOUD + 5, RADIATION_RATE, DoRadiationCloud),
			new State(RADIATION_CLOUD + 6, RADIATION_RATE, DoRadiationCloud),
			new State(RADIATION_CLOUD + 7, RADIATION_RATE, DoRadiationCloud),
			new State(RADIATION_CLOUD + 8, RADIATION_RATE, DoRadiationCloud),
			new State(RADIATION_CLOUD + 9, RADIATION_RATE, DoRadiationCloud),
			new State(RADIATION_CLOUD + 10, RADIATION_RATE, DoRadiationCloud),
			new State(RADIATION_CLOUD + 11, RADIATION_RATE, DoRadiationCloud),
			new State(RADIATION_CLOUD + 12, RADIATION_RATE, DoRadiationCloud),
			new State(RADIATION_CLOUD + 13, RADIATION_RATE, DoRadiationCloud),
			new State(RADIATION_CLOUD + 14, RADIATION_RATE, DoRadiationCloud),
			new State(RADIATION_CLOUD + 15, RADIATION_RATE, DoRadiationCloud),
			new State(RADIATION_CLOUD + 16, RADIATION_RATE, DoRadiationCloud),
			new State(RADIATION_CLOUD + 17, RADIATION_RATE, DoRadiationCloud),
			new State(RADIATION_CLOUD + 18, RADIATION_RATE, DoRadiationCloud),
			new State(RADIATION_CLOUD + 19, 100, DoSuicide), };

	public static final int CHEMBOMB_FRAMES = 1;
	public static final int CHEMBOMB_R0 = 3038;
	public static final int CHEMBOMB_R1 = CHEMBOMB_R0 + (CHEMBOMB_FRAMES * 1);
	public static final int CHEMBOMB_R2 = CHEMBOMB_R0 + (CHEMBOMB_FRAMES * 2);
	public static final int CHEMBOMB_R3 = CHEMBOMB_R0 + (CHEMBOMB_FRAMES * 3);
	public static final int CHEMBOMB_R4 = CHEMBOMB_R0 + (CHEMBOMB_FRAMES * 4);

	public static final int CHEMBOMB = CHEMBOMB_R0;
	public static final int CHEMBOMB_RATE = 8;
	public static final Animator DoChemBomb = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoChemBomb(spr);
		}
	};

	public static final State[] s_ChemBomb = { new State(CHEMBOMB_R0 + 0, CHEMBOMB_RATE, DoChemBomb),
			new State(CHEMBOMB_R1 + 0, CHEMBOMB_RATE, DoChemBomb),
			new State(CHEMBOMB_R2 + 0, CHEMBOMB_RATE, DoChemBomb),
			new State(CHEMBOMB_R3 + 0, CHEMBOMB_RATE, DoChemBomb),
			new State(CHEMBOMB_R4 + 0, CHEMBOMB_RATE, DoChemBomb), };

	public static final int CALTROPS_FRAMES = 1;
	public static final int CALTROPS_R0 = CALTROPS - 1;

	public static final int CALTROPS_RATE = 8;

	public static final Animator DoCaltrops = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoCaltrops(spr);
		}
	}, DoCaltropsStick = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoCaltropsStick(spr) != 0;
		}
	};

	public static final State s_Caltrops[] = { new State(CALTROPS_R0 + 0, CALTROPS_RATE, DoCaltrops),
			new State(CALTROPS_R0 + 1, CALTROPS_RATE, DoCaltrops),
			new State(CALTROPS_R0 + 2, CALTROPS_RATE, DoCaltrops), };

	public static final State s_CaltropsStick[] = {
			new State(CALTROPS_R0 + 2, CALTROPS_RATE, DoCaltropsStick).setNext() };

	//////////////////////
	//
	// CAPTURE FLAG
	//
	//////////////////////

	public static final Animator DoFlag = new Animator() {
		@Override
		public boolean invoke(int spr) {
			DoFlag(spr);
			return false;
		}
	}, DoCarryFlag = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoCarryFlag(spr);
		}
	}, DoCarryFlagNoDet = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoCarryFlagNoDet(spr);
		}
	};

	public static final int FLAG = 2520;
	public static final int FLAG_RATE = 16;

	public static final State s_CarryFlag[] = { new State(FLAG + 0, FLAG_RATE, DoCarryFlag),
			new State(FLAG + 1, FLAG_RATE, DoCarryFlag), new State(FLAG + 2, FLAG_RATE, DoCarryFlag), };

	public static final State s_CarryFlagNoDet[] = { new State(FLAG + 0, FLAG_RATE, DoCarryFlagNoDet),
			new State(FLAG + 1, FLAG_RATE, DoCarryFlagNoDet), new State(FLAG + 2, FLAG_RATE, DoCarryFlagNoDet), };

	public static final State s_Flag[] = { new State(FLAG + 0, FLAG_RATE, DoFlag),
			new State(FLAG + 1, FLAG_RATE, DoFlag), new State(FLAG + 2, FLAG_RATE, DoFlag), };

	public static final int PHOSPHORUS_RATE = 8;
	public static final Animator DoPhosphorus = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoPhosphorus(spr);
		}
	};

	public static final State s_Phosphorus[] = { new State(PHOSPHORUS + 0, PHOSPHORUS_RATE, DoPhosphorus),
			new State(PHOSPHORUS + 1, PHOSPHORUS_RATE, DoPhosphorus), };

	public static final Animator DoBloodSpray = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoBloodSpray(spr);
		}
	};

	public static final int CHUNK1 = 1685;
	public static final State s_BloodSprayChunk[] = { new State(CHUNK1 + 0, 8, DoBloodSpray),
			new State(CHUNK1 + 1, 8, DoBloodSpray), new State(CHUNK1 + 2, 8, DoBloodSpray),
			new State(CHUNK1 + 3, 8, DoBloodSpray), new State(CHUNK1 + 4, 8, DoBloodSpray),
			new State(CHUNK1 + 5, 8, DoBloodSpray), };

	public static final Animator DoWallBloodDrip = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoWallBloodDrip(spr) != 0;
		}
	};

	public static final int DRIP = 1566;
	public static final State s_BloodSprayDrip[] = { new State(DRIP + 0, PHOSPHORUS_RATE, DoWallBloodDrip),
			new State(DRIP + 1, PHOSPHORUS_RATE, DoWallBloodDrip),
			new State(DRIP + 2, PHOSPHORUS_RATE, DoWallBloodDrip), };

	public static void InitJWeaponStates() {
		State.InitState(s_BloodSpray);
		State.InitState(s_PhosphorExp);
		State.InitState(s_NukeMushroom);
		State.InitState(s_RadiationCloud);
		State.InitState(s_ChemBomb);
		State.InitState(s_Caltrops);
		State.InitState(s_CaltropsStick);
		State.InitState(s_CarryFlag);
		State.InitState(s_CarryFlagNoDet);
		State.InitState(s_Flag);
		State.InitState(s_Phosphorus);
		State.InitState(s_BloodSprayChunk);
		State.InitState(s_BloodSprayDrip);
	}

	public static int DoWallBloodDrip(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		// sy & sz are the ceiling and floor of the sector you are sliding down
		if (u.sz != u.sy) {
			// if you are between the ceiling and floor fall fast
			if (sp.z > u.sy && sp.z < u.sz) {
				sp.zvel += 300;
				sp.z += sp.zvel;
			} else {
				sp.zvel = (short) ((300 + RANDOM_RANGE(2300)) >> 1);
				sp.z += sp.zvel;
			}
		} else {
			sp.zvel = (short) ((300 + RANDOM_RANGE(2300)) >> 1);
			sp.z += sp.zvel;
		}

		if (sp.z >= u.loz) {
			sp.z = u.loz;
			SpawnFloorSplash(SpriteNum);
			KillSprite(SpriteNum);
			return (0);
		}

		return (0);
	}

	public static void SpawnMidSplash(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		SPRITE np;
		USER nu;
		int newsp = SpawnSprite(STAT_MISSILE, GOREDrip, s_GoreSplash[0], sp.sectnum, sp.x, sp.y, SPRITEp_MID(sp),
				sp.ang, 0);

		np = sprite[newsp];
		nu = pUser[newsp];

		np.shade = -12;
		np.xrepeat = (short) (70 - RANDOM_RANGE(20));
		np.yrepeat = (short) (70 - RANDOM_RANGE(20));
		nu.ox = u.ox;
		nu.oy = u.oy;
		nu.oz = u.oz;
		np.cstat |= (CSTAT_SPRITE_YCENTER);
		np.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		if (RANDOM_P2(1024) < 512)
			np.cstat |= (CSTAT_SPRITE_XFLIP);

		nu.xchange = 0;
		nu.ychange = 0;
		nu.zchange = 0;

		if (TEST(u.Flags, SPR_UNDERWATER))
			nu.Flags |= (SPR_UNDERWATER);
	}

	public static void SpawnFloorSplash(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		SPRITE np;
		USER nu;
		int newsp = SpawnSprite(STAT_MISSILE, GOREDrip, s_GoreFloorSplash[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang, 0);

		np = sprite[newsp];
		nu = pUser[newsp];

		np.shade = -12;
		np.xrepeat = (short) (70 - RANDOM_RANGE(20));
		np.yrepeat = (short) (70 - RANDOM_RANGE(20));
		nu.ox = u.ox;
		nu.oy = u.oy;
		nu.oz = u.oz;
		np.cstat |= (CSTAT_SPRITE_YCENTER);
		np.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		if (RANDOM_P2(1024) < 512)
			np.cstat |= (CSTAT_SPRITE_XFLIP);

		nu.xchange = 0;
		nu.ychange = 0;
		nu.zchange = 0;

		if (TEST(u.Flags, SPR_UNDERWATER))
			nu.Flags |= (SPR_UNDERWATER);
	}

	public static boolean DoBloodSpray(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		int fz;

		if (TEST(u.Flags, SPR_UNDERWATER)) {
			ScaleSpriteVector(Weapon, 50000);

			u.Counter += 20; // These are STAT_SKIIP4 now, so * 2
			u.zchange += u.Counter;
		} else {
			u.Counter += 20;
			u.zchange += u.Counter;
		}

		if (sp.xvel <= 2) {
			// special stuff for blood worm
			sp.z += (u.zchange >> 1);

			engine.getzsofslope(sp.sectnum, sp.x, sp.y, zofslope);
			fz = zofslope[FLOOR];
			// pretend like we hit a sector
			if (sp.z >= fz) {
				sp.z = fz;
				SpawnFloorSplash(Weapon);
				KillSprite((short) Weapon);
				return (true);
			}
		} else {
			u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist,
					CLIPMASK_MISSILE, MISSILEMOVETICS);
		}

		MissileHitDiveArea(Weapon);

		if (u.ret != 0) {
			switch (DTEST(u.ret, HIT_MASK)) {
			case HIT_PLAX_WALL:
				KillSprite(Weapon);
				return (true);
			case HIT_SPRITE: {
				short wall_ang;
				short hitsprite = -2;
				SPRITE hsp;

				hitsprite = NORM_SPRITE(u.ret);
				hsp = sprite[hitsprite];

				if (TEST(hsp.cstat, CSTAT_SPRITE_WALL)) {
					wall_ang = (short) NORM_ANGLE(hsp.ang);
					SpawnMidSplash(Weapon);
					QueueWallBlood(Weapon, hsp.ang);
					WallBounce(Weapon, wall_ang);
					ScaleSpriteVector(Weapon, 32000);
				} else {
					u.xchange = u.ychange = 0;
					SpawnMidSplash(Weapon);
					QueueWallBlood(Weapon, hsp.ang);
					KillSprite((short) Weapon);
					return (true);
				}

				break;
			}

			case HIT_WALL: {
				short hitwall, nw, wall_ang;
				WALL wph;
				short wb;

				hitwall = NORM_WALL(u.ret);
				wph = wall[hitwall];

				if (wph.lotag == TAG_WALL_BREAK) {
					HitBreakWall(hitwall, sp.x, sp.y, sp.z, sp.ang, u.ID);
					u.ret = 0;
					break;
				}

				nw = wall[hitwall].point2;
				wall_ang = (short) NORM_ANGLE(engine.getangle(wall[nw].x - wph.x, wall[nw].y - wph.y) + 512);

				SpawnMidSplash(Weapon);
				wb = (short) QueueWallBlood(Weapon, NORM_ANGLE(wall_ang + 1024));

				if (wb < 0) {
					KillSprite(Weapon);
					return false;
				} else {
					if (FAF_Sector(sprite[wb].sectnum) || FAF_ConnectArea(sprite[wb].sectnum)) {
						KillSprite(Weapon);
						return false;
					}

					sp.xvel = sp.yvel = (short) (u.xchange = u.ychange = 0);
					sp.xrepeat = sp.yrepeat = (short) (70 - RANDOM_RANGE(25));
					sp.x = sprite[wb].x;
					sp.y = sprite[wb].y;

					// !FRANK! bit of a hack
					// yvel is the hitwall
					if (sprite[wb].yvel >= 0) {
						short wallnum = sprite[wb].yvel;

						// sy & sz are the ceiling and floor of the sector you are sliding down
						if (wall[wallnum].nextsector >= 0) {
							engine.getzsofslope(wall[wallnum].nextsector, sp.x, sp.y, zofslope);
							u.sy = zofslope[CEIL];
							u.sz = zofslope[FLOOR];
						} else
							u.sy = u.sz; // ceiling and floor are equal - white wall
					}

					sp.cstat &= ~(CSTAT_SPRITE_INVISIBLE);
					ChangeState(Weapon, s_BloodSprayDrip[0]);
				}
				break;
			}

			case HIT_SECTOR: {
				// hit floor
				if (sp.z > DIV2(u.hiz + u.loz)) {
					if (TEST(u.Flags, SPR_UNDERWATER))
						u.Flags |= (SPR_BOUNCE); // no bouncing
													// underwater

					if (u.lo_sectp != -1 && SectUser[sp.sectnum] != null && SectUser[sp.sectnum].depth != 0)
						u.Flags |= (SPR_BOUNCE); // no bouncing on
													// shallow water

					u.xchange = u.ychange = 0;
					SpawnFloorSplash(Weapon);
					KillSprite((short) Weapon);
					return (true);

				} else
				// hit something above
				{
					u.zchange = -u.zchange;
					ScaleSpriteVector(Weapon, 32000); // was 22000
				}
				break;
			}
			}
		}

		// if you haven't bounced or your going slow do some puffs
		if (!TEST(u.Flags, SPR_BOUNCE | SPR_UNDERWATER)) {
			SPRITE np;
			USER nu;
			int newsp = SpawnSprite(STAT_MISSILE, GOREDrip, s_BloodSpray[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang, 100);

			np = sprite[newsp];
			nu = pUser[newsp];

			SetOwner(Weapon, newsp);
			np.shade = -12;
			np.xrepeat = (short) (40 - RANDOM_RANGE(30));
			np.yrepeat = (short) (40 - RANDOM_RANGE(30));
			nu.ox = u.ox;
			nu.oy = u.oy;
			nu.oz = u.oz;
			np.cstat |= (CSTAT_SPRITE_YCENTER);
			np.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

			if (RANDOM_P2(1024) < 512)
				np.cstat |= (CSTAT_SPRITE_XFLIP);
			if (RANDOM_P2(1024) < 512)
				np.cstat |= (CSTAT_SPRITE_YFLIP);

			nu.xchange = u.xchange;
			nu.ychange = u.ychange;
			nu.zchange = u.zchange;

			ScaleSpriteVector(newsp, 20000);

			if (TEST(u.Flags, SPR_UNDERWATER))
				nu.Flags |= (SPR_UNDERWATER);
		}

		return (false);
	}

	public static boolean DoPhosphorus(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		if (TEST(u.Flags, SPR_UNDERWATER)) {
			ScaleSpriteVector(Weapon, 50000);

			u.Counter += 20 * 2;
			u.zchange += u.Counter;
		} else {
			u.Counter += 20 * 2;
			u.zchange += u.Counter;
		}

		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS * 2);

		MissileHitDiveArea(Weapon);

		if (TEST(u.Flags, SPR_UNDERWATER) && (RANDOM_P2(1024 << 4) >> 4) < 256)
			SpawnBubble(Weapon);

		if (u.ret != 0) {
			switch (DTEST(u.ret, HIT_MASK)) {
			case HIT_PLAX_WALL:
				KillSprite(Weapon);
				return (true);
			case HIT_SPRITE: {
				short wall_ang;
				short hitsprite = -2;
				SPRITE hsp;
				USER hu;

				hitsprite = NORM_SPRITE(u.ret);
				hsp = sprite[hitsprite];
				hu = pUser[hitsprite];

				if (TEST(hsp.cstat, CSTAT_SPRITE_WALL)) {
					wall_ang = (short) NORM_ANGLE(hsp.ang);
					WallBounce(Weapon, wall_ang);
					ScaleSpriteVector(Weapon, 32000);
				} else {
					if (TEST(hsp.extra, SPRX_BURNABLE)) {
						if (hu == null)
							hu = SpawnUser(hitsprite, hsp.picnum, null);
						SpawnFireballExp(Weapon);
						if (hu != null)
							SpawnFireballFlames(Weapon, hitsprite);
						DoFlamesDamageTest(Weapon);
					}
					u.xchange = u.ychange = 0;
					KillSprite((short) Weapon);
					return (true);
				}

				break;
			}

			case HIT_WALL: {
				short hitwall, nw, wall_ang;
				WALL wph;

				hitwall = NORM_WALL(u.ret);
				wph = wall[hitwall];

				if (wph.lotag == TAG_WALL_BREAK) {
					HitBreakWall(hitwall, sp.x, sp.y, sp.z, sp.ang, u.ID);
					u.ret = 0;
					break;
				}

				nw = wall[hitwall].point2;
				wall_ang = (short) NORM_ANGLE(engine.getangle(wall[nw].x - wph.x, wall[nw].y - wph.y) + 512);

				WallBounce(Weapon, wall_ang);
				ScaleSpriteVector(Weapon, 32000);
				break;
			}

			case HIT_SECTOR: {
				boolean hitwall;

				if (SlopeBounce(Weapon, tmp_ptr[0])) {
					hitwall = tmp_ptr[0].value != 0;
					if (hitwall) {
						// hit a wall
						ScaleSpriteVector(Weapon, 28000);
						u.ret = 0;
						u.Counter = 0;
					} else {
						// hit a sector
						if (sp.z > DIV2(u.hiz + u.loz)) {
							// hit a floor
							if (!TEST(u.Flags, SPR_BOUNCE)) {
								u.Flags |= (SPR_BOUNCE);
								ScaleSpriteVector(Weapon, 32000); // was 18000
								u.zchange /= 6;
								u.ret = 0;
								u.Counter = 0;
							} else {
								u.xchange = u.ychange = 0;
								SpawnFireballExp(Weapon);
								KillSprite((short) Weapon);
								return (true);
							}
						} else {
							// hit a ceiling
							ScaleSpriteVector(Weapon, 32000); // was 22000
						}
					}
				} else {
					// hit floor
					if (sp.z > DIV2(u.hiz + u.loz)) {
						if (TEST(u.Flags, SPR_UNDERWATER))
							u.Flags |= (SPR_BOUNCE); // no bouncing
						// underwater

						if (u.lo_sectp != -1 && SectUser[sp.sectnum] != null && SectUser[sp.sectnum].depth != 0)
							u.Flags |= (SPR_BOUNCE); // no bouncing on
						// shallow water

						if (!TEST(u.Flags, SPR_BOUNCE)) {
							u.Flags |= (SPR_BOUNCE);
							u.ret = 0;
							u.Counter = 0;
							u.zchange = -u.zchange;
							ScaleSpriteVector(Weapon, 32000); // Was 18000
							u.zchange /= 6;
						} else {
							u.xchange = u.ychange = 0;
							SpawnFireballExp(Weapon);
							KillSprite((short) Weapon);
							return (true);
						}
					} else
					// hit something above
					{
						u.zchange = -u.zchange;
						ScaleSpriteVector(Weapon, 32000); // was 22000
					}
				}
				break;
			}
			}
		}

		// if you haven't bounced or your going slow do some puffs
		if (!TEST(u.Flags, SPR_BOUNCE | SPR_UNDERWATER) && !TEST(sp.cstat, CSTAT_SPRITE_INVISIBLE)) {
			SPRITE np;
			USER nu;
			int newsp = SpawnSprite(STAT_SKIP4, PUFF, s_PhosphorExp[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang, 100);

			np = sprite[newsp];
			nu = pUser[newsp];

			np.hitag = LUMINOUS; // Always full brightness
			SetOwner(Weapon, newsp);
			np.shade = -40;
			np.xrepeat = (short) (12 + RANDOM_RANGE(10));
			np.yrepeat = (short) (12 + RANDOM_RANGE(10));
			nu.ox = u.ox;
			nu.oy = u.oy;
			nu.oz = u.oz;
			np.cstat |= (CSTAT_SPRITE_YCENTER);
			np.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

			if (RANDOM_P2(1024) < 512)
				np.cstat |= (CSTAT_SPRITE_XFLIP);
			if (RANDOM_P2(1024) < 512)
				np.cstat |= (CSTAT_SPRITE_YFLIP);

			nu.xchange = u.xchange;
			nu.ychange = u.ychange;
			nu.zchange = u.zchange;

			nu.spal = (byte) (np.pal = PALETTE_PLAYER3); // RED

			ScaleSpriteVector(newsp, 20000);

			if (TEST(u.Flags, SPR_UNDERWATER))
				nu.Flags |= (SPR_UNDERWATER);
		}

		return (false);
	}

	public static boolean DoChemBomb(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		if (TEST(u.Flags, SPR_UNDERWATER)) {
			ScaleSpriteVector(Weapon, 50000);

			u.Counter += 20;
			u.zchange += u.Counter;
		} else {
			u.Counter += 20;
			u.zchange += u.Counter;
		}

		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);

		if (TEST(u.Flags, SPR_UNDERWATER) && (RANDOM_P2(1024 << 4) >> 4) < 256)
			SpawnBubble(Weapon);

		if (u.ret != 0) {
			switch (DTEST(u.ret, HIT_MASK)) {
			case HIT_PLAX_WALL:
				KillSprite(Weapon);
				return (true);
			case HIT_SPRITE: {
				short wall_ang;
				short hitsprite = -2;
				SPRITE hsp;

				if (!TEST(sp.cstat, CSTAT_SPRITE_INVISIBLE))
					PlaySound(DIGI_CHEMBOUNCE, sp, v3df_dontpan);

				hitsprite = NORM_SPRITE(u.ret);
				hsp = sprite[hitsprite];

				if (TEST(hsp.cstat, CSTAT_SPRITE_WALL)) {
					wall_ang = (short) NORM_ANGLE(hsp.ang);
					WallBounce(Weapon, wall_ang);
					ScaleSpriteVector(Weapon, 32000);
				} else {
					// Canister pops when first smoke starts out
					if (u.WaitTics == CHEMTICS && !TEST(sp.cstat, CSTAT_SPRITE_INVISIBLE)) {
						PlaySound(DIGI_GASPOP, sp, v3df_dontpan | v3df_doppler);
						VOC3D voc = PlaySound(DIGI_CHEMGAS, sp, v3df_dontpan | v3df_doppler);
						Set3DSoundOwner(Weapon, voc);
					}
					u.xchange = u.ychange = 0;
					u.WaitTics -= (MISSILEMOVETICS * 2);
					if (u.WaitTics <= 0)
						KillSprite((short) Weapon);
					return (true);
				}

				break;
			}

			case HIT_WALL: {
				short hitwall, nw, wall_ang;
				WALL wph;

				hitwall = NORM_WALL(u.ret);
				wph = wall[hitwall];

				if (wph.lotag == TAG_WALL_BREAK) {
					HitBreakWall(hitwall, sp.x, sp.y, sp.z, sp.ang, u.ID);
					u.ret = 0;
					break;
				}

				if (!TEST(sp.cstat, CSTAT_SPRITE_INVISIBLE))
					PlaySound(DIGI_CHEMBOUNCE, sp, v3df_dontpan);

				nw = wall[hitwall].point2;
				wall_ang = (short) NORM_ANGLE(engine.getangle(wall[nw].x - wph.x, wall[nw].y - wph.y) + 512);

				WallBounce(Weapon, wall_ang);
				ScaleSpriteVector(Weapon, 32000);
				break;
			}

			case HIT_SECTOR: {
				boolean hitwall;

				if (SlopeBounce(Weapon, tmp_ptr[0])) {
					hitwall = tmp_ptr[0].value != 0;
					if (hitwall) {
						// hit a wall
						ScaleSpriteVector(Weapon, 28000);
						u.ret = 0;
						u.Counter = 0;
					} else {
						// hit a sector
						if (sp.z > DIV2(u.hiz + u.loz)) {
							// hit a floor
							if (!TEST(u.Flags, SPR_BOUNCE)) {
								if (!TEST(sp.cstat, CSTAT_SPRITE_INVISIBLE))
									PlaySound(DIGI_CHEMBOUNCE, sp, v3df_dontpan);
								u.Flags |= (SPR_BOUNCE);
								ScaleSpriteVector(Weapon, 32000); // was 18000
								u.zchange /= 6;
								u.ret = 0;
								u.Counter = 0;
							} else {
								// Canister pops when first smoke starts out
								if (u.WaitTics == CHEMTICS && !TEST(sp.cstat, CSTAT_SPRITE_INVISIBLE)) {
									PlaySound(DIGI_GASPOP, sp, v3df_dontpan | v3df_doppler);
									VOC3D voc = PlaySound(DIGI_CHEMGAS, sp, v3df_dontpan | v3df_doppler);
									Set3DSoundOwner(Weapon, voc);
								}
								SpawnRadiationCloud(Weapon);
								u.xchange = u.ychange = 0;
								u.WaitTics -= (MISSILEMOVETICS * 2);
								if (u.WaitTics <= 0)
									KillSprite((short) Weapon);
								return (true);
							}
						} else {
							// hit a ceiling
							ScaleSpriteVector(Weapon, 32000); // was 22000
						}
					}
				} else {
					// hit floor
					if (sp.z > DIV2(u.hiz + u.loz)) {
						if (TEST(u.Flags, SPR_UNDERWATER))
							u.Flags |= (SPR_BOUNCE); // no bouncing
						// underwater

						if (u.lo_sectp != -1 && SectUser[sp.sectnum] != null && SectUser[sp.sectnum].depth != 0)
							u.Flags |= (SPR_BOUNCE); // no bouncing on
						// shallow water

						if (!TEST(u.Flags, SPR_BOUNCE)) {
							if (!TEST(sp.cstat, CSTAT_SPRITE_INVISIBLE))
								PlaySound(DIGI_CHEMBOUNCE, sp, v3df_dontpan);
							u.Flags |= (SPR_BOUNCE);
							u.ret = 0;
							u.Counter = 0;
							u.zchange = -u.zchange;
							ScaleSpriteVector(Weapon, 32000); // Was 18000
							u.zchange /= 6;
						} else {
							// Canister pops when first smoke starts out
							if (u.WaitTics == CHEMTICS && !TEST(sp.cstat, CSTAT_SPRITE_INVISIBLE)) {
								PlaySound(DIGI_GASPOP, sp, v3df_dontpan | v3df_doppler);
								VOC3D voc = PlaySound(DIGI_CHEMGAS, sp, v3df_dontpan | v3df_doppler);
								Set3DSoundOwner(Weapon, voc);
							}
							// WeaponMoveHit(Weapon);
							SpawnRadiationCloud(Weapon);
							u.xchange = u.ychange = 0;
							u.WaitTics -= (MISSILEMOVETICS * 2);
							if (u.WaitTics <= 0)
								KillSprite((short) Weapon);
							return (true);
						}
					} else
					// hit something above
					{
						u.zchange = -u.zchange;
						ScaleSpriteVector(Weapon, 32000); // was 22000
					}
				}
				break;
			}
			}
		}

		// if you haven't bounced or your going slow do some puffs
		if (!TEST(u.Flags, SPR_BOUNCE | SPR_UNDERWATER) && !TEST(sp.cstat, CSTAT_SPRITE_INVISIBLE)) {
			SPRITE np;
			USER nu;
			int newsp = SpawnSprite(STAT_MISSILE, PUFF, s_Puff[0], sp.sectnum, sp.x, sp.y, sp.z, sp.ang, 100);

			np = sprite[newsp];
			nu = pUser[newsp];

			SetOwner(Weapon, newsp);
			np.shade = -40;
			np.xrepeat = 40;
			np.yrepeat = 40;
			nu.ox = u.ox;
			nu.oy = u.oy;
			nu.oz = u.oz;
			// !Frank - dont do translucent
			np.cstat |= (CSTAT_SPRITE_YCENTER);
			np.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

			nu.xchange = u.xchange;
			nu.ychange = u.ychange;
			nu.zchange = u.zchange;

			nu.spal = (byte) (np.pal = PALETTE_PLAYER6);

			ScaleSpriteVector(newsp, 20000);

			if (TEST(u.Flags, SPR_UNDERWATER))
				nu.Flags |= (SPR_UNDERWATER);
		}

		return (false);
	}

	public static int DoCaltropsStick(int Weapon) {
		USER u = pUser[Weapon];

		u.Counter = (u.Counter == 0) ? (short) 1 : (short) 0;

		if (u.Counter != 0)
			DoFlamesDamageTest(Weapon);

		return (0);
	}

	public static boolean DoCaltrops(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		if (TEST(u.Flags, SPR_UNDERWATER)) {
			ScaleSpriteVector(Weapon, 50000);

			u.Counter += 20;
			u.zchange += u.Counter;
		} else {
			u.Counter += 70;
			u.zchange += u.Counter;
		}

		u.ret = move_missile(Weapon, u.xchange, u.ychange, u.zchange, u.ceiling_dist, u.floor_dist, CLIPMASK_MISSILE,
				MISSILEMOVETICS);

		MissileHitDiveArea(Weapon);

		if (u.ret != 0) {
			switch (DTEST(u.ret, HIT_MASK)) {
			case HIT_PLAX_WALL:
				KillSprite(Weapon);
				return (true);
			case HIT_SPRITE: {
				short wall_ang;
				short hitsprite = -2;
				SPRITE hsp;

				PlaySound(DIGI_CALTROPS, sp, v3df_dontpan);

				hitsprite = NORM_SPRITE(u.ret);
				hsp = sprite[hitsprite];

				if (TEST(hsp.cstat, CSTAT_SPRITE_WALL)) {
					wall_ang = (short) NORM_ANGLE(hsp.ang);
					WallBounce(Weapon, wall_ang);
					ScaleSpriteVector(Weapon, 10000);
				} else {
					// fall to the ground
					u.xchange = u.ychange = 0;
				}

				break;
			}

			case HIT_WALL: {
				short hitwall, nw, wall_ang;
				WALL wph;

				hitwall = NORM_WALL(u.ret);
				wph = wall[hitwall];

				if (wph.lotag == TAG_WALL_BREAK) {
					HitBreakWall(hitwall, sp.x, sp.y, sp.z, sp.ang, u.ID);
					u.ret = 0;
					break;
				}

				PlaySound(DIGI_CALTROPS, sp, v3df_dontpan);

				nw = wall[hitwall].point2;
				wall_ang = (short) NORM_ANGLE(engine.getangle(wall[nw].x - wph.x, wall[nw].y - wph.y) + 512);

				WallBounce(Weapon, wall_ang);
				ScaleSpriteVector(Weapon, 1000);
				break;
			}

			case HIT_SECTOR: {
				boolean hitwall;

				if (SlopeBounce(Weapon, tmp_ptr[0])) {
					hitwall = tmp_ptr[0].value != 0;
					if (hitwall) {
						// hit a wall
						ScaleSpriteVector(Weapon, 1000);
						u.ret = 0;
						u.Counter = 0;
					} else {
						// hit a sector
						if (sp.z > DIV2(u.hiz + u.loz)) {
							// hit a floor
							if (!TEST(u.Flags, SPR_BOUNCE)) {
								PlaySound(DIGI_CALTROPS, sp, v3df_dontpan);
								u.Flags |= (SPR_BOUNCE);
								ScaleSpriteVector(Weapon, 1000); // was 18000
								u.ret = 0;
								u.Counter = 0;
							} else {
								u.xchange = u.ychange = 0;
								sp.extra |= (SPRX_BREAKABLE);
								sp.cstat |= (CSTAT_SPRITE_BREAKABLE);
								ChangeState(Weapon, s_CaltropsStick[0]);
								return (true);
							}
						} else {
							// hit a ceiling
							ScaleSpriteVector(Weapon, 1000); // was 22000
						}
					}
				} else {
					// hit floor
					if (sp.z > DIV2(u.hiz + u.loz)) {
						if (TEST(u.Flags, SPR_UNDERWATER))
							u.Flags |= (SPR_BOUNCE); // no bouncing
						// underwater

						if (u.lo_sectp != -1 && SectUser[sp.sectnum] != null && SectUser[sp.sectnum].depth != 0)
							u.Flags |= (SPR_BOUNCE); // no bouncing on
						// shallow water

						if (!TEST(u.Flags, SPR_BOUNCE)) {
							PlaySound(DIGI_CALTROPS, sp, v3df_dontpan);
							u.Flags |= (SPR_BOUNCE);
							u.ret = 0;
							u.Counter = 0;
							u.zchange = -u.zchange;
							ScaleSpriteVector(Weapon, 1000); // Was 18000
						} else {
							u.xchange = u.ychange = 0;
							sp.extra |= (SPRX_BREAKABLE);
							sp.cstat |= (CSTAT_SPRITE_BREAKABLE);
							ChangeState(Weapon, s_CaltropsStick[0]);
							return (true);
						}
					} else
					// hit something above
					{
						u.zchange = -u.zchange;
						ScaleSpriteVector(Weapon, 1000); // was 22000
					}
				}
				break;
			}
			}
		}

		return (false);
	}

	/////////////////////////////
	//
	// Deadly green gas clouds
	//
	/////////////////////////////
	public static int SpawnRadiationCloud(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], np;
		USER u = pUser[SpriteNum], nu;
		int newsp;

		if (MoveSkip4 == 0)
			return 0;

		// This basically works like a MoveSkip8, if one existed
		if (u.ID == MUSHROOM_CLOUD || u.ID == 3121) {
			if ((u.Counter2++) > 16)
				u.Counter2 = 0;
			if (u.Counter2 != 0)
				return 0;
		} else {
			if ((u.Counter2++) > 2)
				u.Counter2 = 0;
			if (u.Counter2 != 0)
				return 0;
		}

		if (TEST(u.Flags, SPR_UNDERWATER))
			return (-1);

		newsp = SpawnSprite(STAT_MISSILE, RADIATION_CLOUD, s_RadiationCloud[0], sp.sectnum, sp.x, sp.y,
				sp.z - RANDOM_P2(Z(8)), sp.ang, 0);

		np = sprite[newsp];
		nu = pUser[newsp];

		SetOwner(sp.owner, newsp);
		nu.WaitTics = 1 * 120;
		np.shade = -40;
		np.xrepeat = 32;
		np.yrepeat = 32;
		np.clipdist = sp.clipdist;
		np.cstat |= (CSTAT_SPRITE_YCENTER);
		np.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		nu.spal = (byte) (np.pal = PALETTE_PLAYER6);
		// Won't take floor palettes
		np.hitag = (short) SECTFU_DONT_COPY_PALETTE;

		if (RANDOM_P2(1024) < 512)
			np.cstat |= (CSTAT_SPRITE_XFLIP);

		np.ang = (short) RANDOM_P2(2048);
		np.xvel = (short) RANDOM_P2(32);

		nu.Counter = 0;
		nu.Counter2 = 0;

		if (u.ID == MUSHROOM_CLOUD || u.ID == 3121) {
			nu.Radius = 2000;
			nu.xchange = (MOVEx(np.xvel >> 2, np.ang));
			nu.ychange = (MOVEy(np.xvel >> 2, np.ang));
			np.zvel = (short) (Z(1) + RANDOM_P2(Z(2)));
		} else {
			nu.xchange = MOVEx(np.xvel, np.ang);
			nu.ychange = MOVEy(np.xvel, np.ang);
			np.zvel = (short) (Z(4) + RANDOM_P2(Z(4)));
			nu.Radius = 4000;
		}

		return 0;
	}

	public static void DoRadiationCloud(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		sp.z -= sp.zvel;

		sp.x += u.xchange;
		sp.y += u.ychange;

		if (u.ID != 0) {
			DoFlamesDamageTest(SpriteNum);
		}
	}

	//////////////////////////////////////////////
	//
	// Inventory Chemical Bombs
	//
	//////////////////////////////////////////////
	public static void PlayerInitChemBomb(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		USER wu;
		SPRITE wp;
		int nx, ny, nz;
		short oclipdist;

		PlaySound(DIGI_THROW, pp, v3df_dontpan | v3df_doppler);

		nx = pp.posx;
		ny = pp.posy;
		nz = pp.posz + pp.bob_z + Z(8);

		// Spawn a shot
		// Inserting and setting up variables
		int w = SpawnSprite(STAT_MISSILE, CHEMBOMB, s_ChemBomb[0], pp.cursectnum, nx, ny, nz, pp.getAnglei(),
				CHEMBOMB_VELOCITY);

		wp = sprite[w];
		wu = pUser[w];

		// don't throw it as far if crawling
		if (TEST(pp.Flags, PF_CRAWLING)) {
			wp.xvel -= DIV4(wp.xvel);
		}

		wu.Flags |= (SPR_XFLIP_TOGGLE);

		SetOwner(pp.PlayerSprite, w);
		wp.yrepeat = 32;
		wp.xrepeat = 32;
		wp.shade = -15;
		wu.WeaponNum = u.WeaponNum;
		wu.Radius = 200;
		wu.ceiling_dist = (short) Z(3);
		wu.floor_dist = (short) Z(3);
		wu.Counter = 0;
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.cstat |= (CSTAT_SPRITE_BLOCK);

		if (TEST(pp.Flags, PF_DIVING) || SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		wp.zvel = (short) ((100 - pp.getHorizi()) * HORIZ_MULT);

		SPRITE psp = pp.getSprite();

		oclipdist = (short) psp.clipdist;
		psp.clipdist = 0;
		wp.clipdist = 0;

		MissileSetPos(w, DoChemBomb, 1000);

		psp.clipdist = oclipdist;
		wp.clipdist = 80 >> 2;

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel >> 1;

		// adjust xvel according to player velocity
		wu.xchange += pp.xvect >> 14;
		wu.ychange += pp.yvect >> 14;

		// Smoke will come out for this many seconds
		wu.WaitTics = (short) CHEMTICS;
	}

	public static int InitSpriteChemBomb(int SpriteNum) {
		USER u = pUser[SpriteNum];
		USER wu;
		SPRITE sp = sprite[SpriteNum], wp;
		int nx, ny, nz;

		PlaySound(DIGI_THROW, sp, v3df_dontpan | v3df_doppler);

		nx = sp.x;
		ny = sp.y;
		nz = sp.z;

		// Spawn a shot
		// Inserting and setting up variables
		int w = SpawnSprite(STAT_MISSILE, CHEMBOMB, s_ChemBomb[0], sp.sectnum, nx, ny, nz, sp.ang, CHEMBOMB_VELOCITY);

		wp = sprite[w];
		wu = pUser[w];

		wu.Flags |= (SPR_XFLIP_TOGGLE);

		SetOwner(SpriteNum, w);
		wp.yrepeat = 32;
		wp.xrepeat = 32;
		wp.shade = -15;
		wu.WeaponNum = u.WeaponNum;
		wu.Radius = 200;
		wu.ceiling_dist = (short) Z(3);
		wu.floor_dist = (short) Z(3);
		wu.Counter = 0;
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.cstat |= (CSTAT_SPRITE_BLOCK);

		wp.zvel = (short) ((-100 - RANDOM_RANGE(100)) * HORIZ_MULT);

		wp.clipdist = 80 >> 2;

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel >> 1;

		// Smoke will come out for this many seconds
		wu.WaitTics = (short) CHEMTICS;

		return (0);
	}

	public static int InitChemBomb(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		USER wu;
		SPRITE wp;
		int nx, ny, nz;

		nx = sp.x;
		ny = sp.y;
		nz = sp.z;

		// Spawn a shot
		// Inserting and setting up variables
		int w = SpawnSprite(STAT_MISSILE, MUSHROOM_CLOUD, s_ChemBomb[0], sp.sectnum, nx, ny, nz, sp.ang,
				CHEMBOMB_VELOCITY);

		wp = sprite[w];
		wu = pUser[w];

		wu.Flags |= (SPR_XFLIP_TOGGLE);

		SetOwner(sp.owner, w); // !FRANK
		wp.yrepeat = 32;
		wp.xrepeat = 32;
		wp.shade = -15;
		wu.Radius = 200;
		wu.ceiling_dist = (short) Z(3);
		wu.floor_dist = (short) Z(3);
		wu.Counter = 0;
		wp.cstat |= (CSTAT_SPRITE_YCENTER | CSTAT_SPRITE_INVISIBLE); // Make nuke radiation
		// invis.
		wp.cstat &= ~(CSTAT_SPRITE_BLOCK);

		if (SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		wp.zvel = (short) ((-100 - RANDOM_RANGE(100)) * HORIZ_MULT);
		wp.clipdist = 0;

		if (u.ID == MUSHROOM_CLOUD || u.ID == 3121 || u.ID == SUMO_RUN_R0) // 3121 == GRENADE_EXP
		{
			wu.xchange = 0;
			wu.ychange = 0;
			wu.zchange = 0;
			wp.xvel = wp.yvel = wp.zvel = 0;
			// Smoke will come out for this many seconds
			wu.WaitTics = 40 * 120;
		} else {
			wu.xchange = MOVEx(wp.xvel, wp.ang);
			wu.ychange = MOVEy(wp.xvel, wp.ang);
			wu.zchange = wp.zvel >> 1;
			// Smoke will come out for this many seconds
			wu.WaitTics = 3 * 120;
		}

		return (0);
	}

	//////////////////////////////////////////////
	//
	// Inventory Flash Bombs
	//
	//////////////////////////////////////////////
	public static int PlayerInitFlashBomb(PlayerStr pp) {
		short i, nexti, stat;
		short damage;
		SPRITE sp = pp.getSprite(), hp;
		USER hu;

		PlaySound(DIGI_GASPOP, pp, v3df_dontpan | v3df_doppler);

		// Set it just a little to let player know what he just did
		SetFadeAmt(pp, -30, 1); // White flash

		for (stat = 0; stat < StatDamageList.length; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				hp = sprite[i];
				hu = pUser[i];

				if (i == pp.PlayerSprite)
					break;

				int dist = DISTANCE(hp.x, hp.y, sp.x, sp.y);
				if (dist > 16384) // Flash radius
					continue;

				if (!TEST(sp.cstat, CSTAT_SPRITE_BLOCK))
					continue;

				if (!FAFcansee(hp.x, hp.y, hp.z, hp.sectnum, sp.x, sp.y, sp.z - SPRITEp_SIZE_Z(sp), sp.sectnum))
					continue;

				damage = (short) GetDamage(i, pp.PlayerSprite, DMG_FLASHBOMB);

				if (hu.sop_parent != -1) {
					break;
				} else if (hu.PlayerP != -1) {

					if (damage < -70) {
						int choosesnd = 0;

						choosesnd = RANDOM_RANGE(MAX_PAIN);

						PlayerSound(PlayerLowHealthPainVocs[choosesnd], v3df_dontpan | v3df_doppler | v3df_follow, pp);
					}
					SetFadeAmt(Player[hu.PlayerP], damage, 1); // White flash
				} else {
					ActorPain(i);
					SpawnFlashBombOnActor(i);
				}
			}
		}

		return (0);
	}

	public static int InitFlashBomb(int SpriteNum) {
		short i, nexti, stat;
		int dist;
		short damage;
		SPRITE sp = sprite[SpriteNum], hp;
		USER hu;
		PlayerStr pp = Player[screenpeek];

		PlaySound(DIGI_GASPOP, sp, v3df_dontpan | v3df_doppler);

		for (stat = 0; stat < StatDamageList.length; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];

				hp = sprite[i];
				hu = pUser[i];

				dist = DISTANCE(hp.x, hp.y, sp.x, sp.y);
				if (dist > 16384) // Flash radius
					continue;

				if (!TEST(sp.cstat, CSTAT_SPRITE_BLOCK))
					continue;

				if (!FAFcansee(hp.x, hp.y, hp.z, hp.sectnum, sp.x, sp.y, sp.z - SPRITEp_SIZE_Z(sp), sp.sectnum))
					continue;

				damage = (short) GetDamage(i, SpriteNum, DMG_FLASHBOMB);

				if (hu.sop_parent != -1) {
					break;
				} else if (hu.PlayerP != -1) {
					if (damage < -70) {
						int choosesnd = 0;

						choosesnd = RANDOM_RANGE(MAX_PAIN);

						PlayerSound(PlayerLowHealthPainVocs[choosesnd], v3df_dontpan | v3df_doppler | v3df_follow, pp);
					}
					SetFadeAmt(Player[hu.PlayerP], damage, 1); // White flash
				} else {
					if (i != SpriteNum) {
						ActorPain(i);
						SpawnFlashBombOnActor(i);
					}
				}
			}
		}

		return (0);
	}

	// This is a sneaky function to make actors look blinded by flashbomb while
	// using flaming code
	public static int SpawnFlashBombOnActor(short enemy) {
		SPRITE ep = sprite[enemy];
		USER eu = pUser[enemy];
		SPRITE np;
		USER nu;
		int newsp;

		// Forget about burnable sprites
		if (TEST(ep.extra, SPRX_BURNABLE))
			return (eu.flame);

		if (enemy >= 0) {

			if (eu.flame >= 0) {
				int sizez = SPRITEp_SIZE_Z(ep) + DIV4(SPRITEp_SIZE_Z(ep));

				np = sprite[eu.flame];
				nu = pUser[eu.flame];

				if (nu.Counter >= SPRITEp_SIZE_Z_2_YREPEAT(np, sizez)) {
					// keep flame only slightly bigger than the enemy itself
					nu.Counter = (short) (SPRITEp_SIZE_Z_2_YREPEAT(np, sizez) * 2);
				} else {
					// increase max size
					nu.Counter += SPRITEp_SIZE_Z_2_YREPEAT(np, 8 << 8) * 2;
				}

				// Counter is max size
				if (nu.Counter >= 230) {
					// this is far too big
					nu.Counter = 230;
				}

				if (nu.WaitTics < 2 * 120)
					nu.WaitTics = 2 * 120; // allow it to grow again

				return (eu.flame);
			}
		}

		newsp = SpawnSprite(STAT_MISSILE, FIREBALL_FLAMES, s_FireballFlames[0], ep.sectnum, ep.x, ep.y, ep.z, ep.ang,
				0);
		np = sprite[newsp];
		nu = pUser[newsp];

		if (enemy >= 0)
			eu.flame = (short) newsp;

		np.xrepeat = 16;
		np.yrepeat = 16;

		if (enemy >= 0) {
			nu.Counter = (short) (SPRITEp_SIZE_Z_2_YREPEAT(np, SPRITEp_SIZE_Z(ep) >> 1) * 4);
		} else
			nu.Counter = 0; // max flame size

		np.shade = -40;
		np.cstat |= (CSTAT_SPRITE_YCENTER | CSTAT_SPRITE_INVISIBLE);
		np.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		nu.Radius = 200;

		if (enemy >= 0) {
			SetAttach(enemy, newsp);
		}

		return (newsp);
	}

	//////////////////////////////////////////////
	//
	// Inventory Caltrops
	//
	//////////////////////////////////////////////
	public static int PlayerInitCaltrops(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		USER wu;
		SPRITE wp;
		int nx, ny, nz;
		short oclipdist;

		PlaySound(DIGI_THROW, pp, v3df_dontpan | v3df_doppler);

		nx = pp.posx;
		ny = pp.posy;
		nz = pp.posz + pp.bob_z + Z(8);

		// Throw out several caltrops
		// for(i=0;i<3;i++)
		// {
		// Spawn a shot
		// Inserting and setting up variables
		int w = SpawnSprite(STAT_DEAD_ACTOR, CALTROPS, s_Caltrops[0], pp.cursectnum, nx, ny, nz, pp.getAnglei(),
				(CHEMBOMB_VELOCITY + RANDOM_RANGE(CHEMBOMB_VELOCITY)) / 2);

		wp = sprite[w];
		wu = pUser[w];

		// don't throw it as far if crawling
		if (TEST(pp.Flags, PF_CRAWLING)) {
			wp.xvel -= DIV4(wp.xvel);
		}

		wu.Flags |= (SPR_XFLIP_TOGGLE);

		SetOwner(pp.PlayerSprite, w);
		wp.yrepeat = 64;
		wp.xrepeat = 64;
		wp.shade = -15;
		wu.WeaponNum = u.WeaponNum;
		wu.Radius = 200;
		wu.ceiling_dist = (short) Z(3);
		wu.floor_dist = (short) Z(3);
		wu.Counter = 0;

		if (TEST(pp.Flags, PF_DIVING) || SpriteInUnderwaterArea(wp))
			wu.Flags |= (SPR_UNDERWATER);

		wp.zvel = (short) ((100 - pp.getHorizi()) * HORIZ_MULT);

		SPRITE psp = pp.getSprite();
		oclipdist = (short) psp.clipdist;
		psp.clipdist = 0;
		wp.clipdist = 0;

		MissileSetPos(w, DoCaltrops, 1000);

		psp.clipdist = oclipdist;
		wp.clipdist = 80 >> 2;

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel >> 1;

		// adjust xvel according to player velocity
		wu.xchange += pp.xvect >> 14;
		wu.ychange += pp.yvect >> 14;

		SetupSpriteForBreak(wp); // Put Caltrops in the break queue
		return (0);
	}

	public static int InitCaltrops(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		USER wu;
		SPRITE wp;
		int nx, ny, nz;

		PlaySound(DIGI_THROW, sp, v3df_dontpan | v3df_doppler);

		nx = sp.x;
		ny = sp.y;
		nz = sp.z;

		// Spawn a shot
		// Inserting and setting up variables
		int w = SpawnSprite(STAT_DEAD_ACTOR, CALTROPS, s_Caltrops[0], sp.sectnum, nx, ny, nz, sp.ang,
				CHEMBOMB_VELOCITY / 2);

		wp = sprite[w];
		wu = pUser[w];

		wu.Flags |= (SPR_XFLIP_TOGGLE);

		SetOwner(SpriteNum, w);
		wp.yrepeat = 64;
		wp.xrepeat = 64;
		wp.shade = -15;
		// !FRANK - clipbox must be <= weapon otherwise can clip thru walls
		wp.clipdist = sp.clipdist;
		wu.WeaponNum = u.WeaponNum;
		wu.Radius = 200;
		wu.ceiling_dist = (short) Z(3);
		wu.floor_dist = (short) Z(3);
		wu.Counter = 0;

		wp.zvel = (short) ((-100 - RANDOM_RANGE(100)) * HORIZ_MULT);

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel >> 1;

		SetupSpriteForBreak(wp); // Put Caltrops in the break queue
		return (0);
	}

	public static int InitPhosphorus(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		USER wu;
		SPRITE wp;
		int nx, ny, nz;
		short daang;

		PlaySound(DIGI_FIREBALL1, sp, v3df_follow);

		nx = sp.x;
		ny = sp.y;
		nz = sp.z;

		daang = (short) NORM_ANGLE(RANDOM_RANGE(2048));

		// Spawn a shot
		// Inserting and setting up variables
		int w = SpawnSprite(STAT_SKIP4, FIREBALL1, s_Phosphorus[0], sp.sectnum, nx, ny, nz, daang,
				CHEMBOMB_VELOCITY / 3);

		wp = sprite[w];
		wu = pUser[w];

		wp.hitag = LUMINOUS; // Always full brightness
		wu.Flags |= (SPR_XFLIP_TOGGLE);
		// !Frank - don't do translucent
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.shade = -128;

		wp.yrepeat = 64;
		wp.xrepeat = 64;
		wp.shade = -15;
		// !FRANK - clipbox must be <= weapon otherwise can clip thru walls
		if (sp.clipdist > 0)
			wp.clipdist = sp.clipdist - 1;
		else
			wp.clipdist = sp.clipdist;
		wu.WeaponNum = u.WeaponNum;
		wu.Radius = 600;
		wu.ceiling_dist = (short) Z(3);
		wu.floor_dist = (short) Z(3);
		wu.Counter = 0;

		wp.zvel = (short) ((-100 - RANDOM_RANGE(100)) * HORIZ_MULT);

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = (wp.zvel >> 1);

		return (0);
	}

	public static int InitBloodSpray(int SpriteNum, boolean dogib, int velocity) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		USER wu;
		SPRITE wp;
		int nx, ny, nz;
		int w;
		int i, cnt, ang, vel, rnd;

		if (dogib)
			cnt = RANDOM_RANGE(3) + 1;
		else
			cnt = 1;

		rnd = RANDOM_RANGE(1000);
		if (rnd > 650)
			PlaySound(DIGI_GIBS1, sp, v3df_none);
		else if (rnd > 350)
			PlaySound(DIGI_GIBS2, sp, v3df_none);
		else
			PlaySound(DIGI_GIBS3, sp, v3df_none);

		ang = sp.ang;
		vel = velocity;

		for (i = 0; i < cnt; i++) {

			if (velocity == -1)
				vel = 105 + RANDOM_RANGE(320);
			else if (velocity == -2)
				vel = 105 + RANDOM_RANGE(100);

			if (dogib)
				ang = NORM_ANGLE(ang + 512 + RANDOM_RANGE(200));
			else
				ang = NORM_ANGLE(ang + 1024 + 256 - RANDOM_RANGE(256));

			nx = sp.x;
			ny = sp.y;
			nz = SPRITEp_TOS(sp) - 20;

			// Spawn a shot
			// Inserting and setting up variables
			w = SpawnSprite(STAT_MISSILE, GOREDrip, s_BloodSprayChunk[0], sp.sectnum, nx, ny, nz, ang, vel * 2);

			wp = sprite[w];
			wu = pUser[w];

			wu.Flags |= (SPR_XFLIP_TOGGLE);
			if (dogib)
				wp.cstat |= (CSTAT_SPRITE_YCENTER);
			else
				wp.cstat |= (CSTAT_SPRITE_YCENTER | CSTAT_SPRITE_INVISIBLE);
			wp.shade = -12;

			SetOwner(SpriteNum, w);
			wp.yrepeat = (short) (64 - RANDOM_RANGE(35));
			wp.xrepeat = (short) (64 - RANDOM_RANGE(35));
			wp.shade = -15;
			wp.clipdist = sp.clipdist;
			wu.WeaponNum = u.WeaponNum;
			wu.Radius = 600;
			wu.ceiling_dist = (short) Z(3);
			wu.floor_dist = (short) Z(3);
			wu.Counter = 0;

			wp.zvel = (short) ((-10 - RANDOM_RANGE(50)) * HORIZ_MULT);

			wu.xchange = MOVEx(wp.xvel, wp.ang);
			wu.ychange = MOVEy(wp.xvel, wp.ang);
			wu.zchange = wp.zvel >> 1;

			if (!GlobalSkipZrange)
				DoActorZrange(w);
		}

		return (0);
	}

	public static void BloodSprayFall(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		sp.z += 1500;
	}

	////////////////// DEATHFLAG!
	////////////////// ////////////////////////////////////////////////////////////////
	// Rules: Run to an enemy flag, run over it an it will stick to you.
	// The goal is to run the enemy's flag back to your startpoint.
	// If an enemy flag touches a friendly start sector, then the opposing team
	////////////////// explodes and
	// your team wins and the level restarts.
	// Once you pick up a flag, you have 30 seconds to score, otherwise, the flag
	////////////////// detonates
	// an explosion, killing you and anyone in the vicinity, and you don't score.
	//////////////////////////////////////////////////////////////////////////////////////////////

	// Update the scoreboard for team color that just scored.
	public static void DoFlagScore(short pal) {
		SPRITE sp;
		int SpriteNum = 0, NextSprite = 0;

		for (SpriteNum = headspritestat[0]; SpriteNum != -1; SpriteNum = NextSprite) {
			NextSprite = nextspritestat[SpriteNum];
			sp = sprite[SpriteNum];

			if (sp.picnum < 1900 || sp.picnum > 1999)
				continue;

			if (sp.pal == pal)
				sp.picnum++; // Increment the counter

			if (sp.picnum > 1999)
				sp.picnum = 1900; // Roll it over if you must

		}
	}

	public static int DoFlagRangeTest(int Weapon, int range) {
		SPRITE wp = sprite[Weapon];
		SPRITE sp;
		short i, nexti, stat;
		long dist;

		for (stat = 0; stat < StatDamageList.length; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];

				sp = sprite[i];
				dist = DISTANCE(sp.x, sp.y, wp.x, wp.y);

				if (dist > range)
					continue;

				if (sp == wp)
					continue;

				if (!TEST(sp.cstat, CSTAT_SPRITE_BLOCK))
					continue;

				if (!TEST(sp.extra, SPRX_PLAYER_OR_ENEMY))
					continue;

				if (!FAFcansee(sp.x, sp.y, sp.z, sp.sectnum, wp.x, wp.y, wp.z, wp.sectnum))
					continue;

				dist = FindDistance3D(wp.x - sp.x, wp.y - sp.y, (wp.z - sp.z) >> 4);
				if (dist > range)
					continue;

				return (i); // Return the spritenum
			}
		}

		return (-1); // -1 for no sprite index. Not
						// found.
	}

	public static final int FLAG_DETONATE_STATE = 99;

	public static boolean DoCarryFlag(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		SPRITE fp = sprite[u.FlagOwner];

		// if no owner then die
		if (u.Attach >= 0) {
			SPRITE ap = sprite[u.Attach];

			engine.setspritez((short) Weapon, ap.x, ap.y, SPRITEp_MID(ap));
			sp.ang = (short) NORM_ANGLE(ap.ang + 1536);
		}

		// not activated yet
		if (!TEST(u.Flags, SPR_ACTIVE)) {
			if ((u.WaitTics -= (MISSILEMOVETICS * 2)) > 0)
				return (false);

			// activate it
			u.WaitTics = (short) SEC(30); // You have 30 seconds to get it to
			// scorebox
			u.Counter2 = 0;
			u.Flags |= (SPR_ACTIVE);
		}

		// limit the number of times DoFlagRangeTest is called
		u.Counter++;
		if (u.Counter > 1)
			u.Counter = 0;

		if (u.Counter == 0) {
			// not already in detonate state
			if (u.Counter2 < FLAG_DETONATE_STATE) {
				SPRITE ap = sprite[u.Attach];
				USER au = pUser[u.Attach];

				if (au == null || au.Health <= 0) {
					u.Counter2 = FLAG_DETONATE_STATE;
					u.WaitTics = (short) (SEC(1) / 2);
				}
				// if in score box, score.
				if (sector[ap.sectnum].hitag == 9000 && sector[ap.sectnum].lotag == ap.pal && ap.pal != sp.pal) {
					if (u.FlagOwner >= 0) {
						if (fp.lotag != 0) // Trigger everything if there is a
							// lotag
							DoMatchEverything(null, fp.lotag, ON);
					}
					if (!TEST_BOOL1(fp)) {
						PlaySound(DIGI_BIGITEM, ap, v3df_none);
						DoFlagScore(ap.pal);
						if (SP_TAG5(fp) > 0) {
							fp.detail++;
							if (fp.detail >= SP_TAG5(fp)) {
								fp.detail = 0;
								DoMatchEverything(null, SP_TAG6(fp), ON);
							}
						}
					}
					SetSuicide(Weapon); // Kill the flag, you scored!
				}
			} else {
				// Time's up! Move directly to detonate state
				u.Counter2 = FLAG_DETONATE_STATE;
				u.WaitTics = (short) (SEC(1) / 2);
			}

		}

		u.WaitTics -= (MISSILEMOVETICS * 2);

		switch (u.Counter2) {
		case 0:
			if (u.WaitTics < SEC(30)) {
				PlaySound(DIGI_MINEBEEP, sp, v3df_dontpan);
				u.Counter2++;
			}
			break;
		case 1:
			if (u.WaitTics < SEC(20)) {
				PlaySound(DIGI_MINEBEEP, sp, v3df_dontpan);
				u.Counter2++;
			}
			break;
		case 2:
			if (u.WaitTics < SEC(10)) {
				PlaySound(DIGI_MINEBEEP, sp, v3df_dontpan);
				u.Counter2++;
			}
			break;
		case 3:
			if (u.WaitTics < SEC(5)) {
				PlaySound(DIGI_MINEBEEP, sp, v3df_dontpan);
				u.Counter2++;
			}
			break;
		case 4:
			if (u.WaitTics < SEC(4)) {
				PlaySound(DIGI_MINEBEEP, sp, v3df_dontpan);
				u.Counter2++;
			}
			break;
		case 5:
			if (u.WaitTics < SEC(3)) {
				PlaySound(DIGI_MINEBEEP, sp, v3df_dontpan);
				u.Counter2++;
			}
			break;
		case 6:
			if (u.WaitTics < SEC(2)) {
				PlaySound(DIGI_MINEBEEP, sp, v3df_dontpan);
				u.Counter2 = FLAG_DETONATE_STATE;
			}
			break;
		case FLAG_DETONATE_STATE:
			// start frantic beeping
			PlaySound(DIGI_MINEBEEP, sp, v3df_dontpan);
			u.Counter2++;
			break;
		case FLAG_DETONATE_STATE + 1:
			SpawnGrenadeExp(Weapon);
			SetSuicide(Weapon);
			return (false);
		}

		return (false);
	}

	public static boolean DoCarryFlagNoDet(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		SPRITE fp = sprite[u.FlagOwner];
		USER fu = pUser[u.FlagOwner];

		if (fu == null && u.FlagOwner >= 0)
			fu.WaitTics = 30 * 120; // Keep setting respawn tics so it
									// won't respawn
		SPRITE ap = null;
		USER au = null;
		// if no owner then die
		if (u.Attach >= 0) {
			ap = sprite[u.Attach];
			au = pUser[u.Attach];
			engine.setspritez((short) Weapon, ap.x, ap.y, SPRITEp_MID(ap));
			sp.ang = (short) NORM_ANGLE(ap.ang + 1536);
			sp.z = ap.z - DIV2(SPRITEp_SIZE_Z(ap));
		}

		if (au == null || au.Health <= 0) {
			if (fu == null && u.FlagOwner >= 0)
				fu.WaitTics = 0; // Tell it to respawn
			SetSuicide(Weapon);
			return (false);
		}

		// if in score box, score.
		if (sector[ap.sectnum].hitag == 9000 && sector[ap.sectnum].lotag == ap.pal && ap.pal != sp.pal) {
			if (fu == null && u.FlagOwner >= 0) {
				if (fp.lotag != 0) // Trigger everything if there is a
					// lotag
					DoMatchEverything(null, fp.lotag, ON);
				fu.WaitTics = 0; // Tell it to respawn
			}
			if (!TEST_BOOL1(fp)) {
				PlaySound(DIGI_BIGITEM, ap, v3df_none);
				DoFlagScore(ap.pal);
				if (SP_TAG5(fp) > 0) {
					fp.detail++;
					if (fp.detail >= SP_TAG5(fp)) {
						fp.detail = 0;
						DoMatchEverything(null, SP_TAG6(fp), ON);
					}
				}
			}
			SetSuicide(Weapon); // Kill the flag, you scored!
		}

		return (false);
	}

	public static void SetCarryFlag(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];

		// stuck
		u.Flags |= (SPR_BOUNCE);
		// not yet active for 1 sec
		sp.cstat |= (CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		u.Counter = 0;
		change_sprite_stat(Weapon, STAT_ITEM);
		if (sp.hitag == 1)
			ChangeState(Weapon, s_CarryFlagNoDet[0]);
		else
			ChangeState(Weapon, s_CarryFlag[0]);

	}

	public static void DoFlag(int Weapon) {
		SPRITE sp = sprite[Weapon];
		USER u = pUser[Weapon];
		int hitsprite = -1;

		hitsprite = DoFlagRangeTest(Weapon, 1000);

		if (hitsprite != -1) {
			SPRITE hsp = sprite[hitsprite];

			SetCarryFlag(Weapon);

			// check to see if sprite is player or enemy
			if (TEST(hsp.extra, SPRX_PLAYER_OR_ENEMY)) {
				// attach weapon to sprite
				sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
				SetAttach(hitsprite, Weapon);
				u.sz = hsp.z - DIV2(SPRITEp_SIZE_Z(hsp));
			}
		}
	}

	public static final int UZI_SHELL = 2152;
	public static final int SHOT_SHELL = 2180;

	public static void InitShell(int SpriteNum, int ShellNum) {
		USER u = pUser[SpriteNum];
		USER wu;
		SPRITE sp = sprite[SpriteNum], wp;
		int nx, ny, nz;
		int w;
		short id = 0, velocity = 0;
		State p = null;

		nx = sp.x;
		ny = sp.y;
		nz = DIV2(SPRITEp_TOS(sp) + SPRITEp_BOS(sp));

		switch (ShellNum) {
		case -2:
		case -3:
			id = UZI_SHELL;
			p = s_UziShellShrap[0];
			velocity = (short) (1500 + RANDOM_RANGE(1000));
			break;
		case -4:
			id = SHOT_SHELL;
			p = s_ShotgunShellShrap[0];
			velocity = (short) (2000 + RANDOM_RANGE(1000));
			break;
		}

		w = SpawnSprite(STAT_SKIP4, id, p, sp.sectnum, nx, ny, nz, sp.ang, 64);

		wp = sprite[w];
		wu = pUser[w];

		wp.zvel = (short) -(velocity);

		if (u.PlayerP != -1) {
			wp.z += ((100 - Player[u.PlayerP].getHorizi()) * (HORIZ_MULT / 3));
		}

		switch (wu.ID) {
		case UZI_SHELL:
			wp.z -= Z(13);

			if (ShellNum == -3) {
				wp.ang = sp.ang;
				HelpMissileLateral(w, 2500);
				wp.ang = (short) NORM_ANGLE(wp.ang - 512);
				HelpMissileLateral(w, 1000); // Was 1500
				wp.ang = (short) NORM_ANGLE(wp.ang + 712);
			} else {
				wp.ang = sp.ang;
				HelpMissileLateral(w, 2500);
				wp.ang = (short) NORM_ANGLE(wp.ang + 512);
				HelpMissileLateral(w, 1500);
				wp.ang = (short) NORM_ANGLE(wp.ang - 128);
			}
			wp.ang += (RANDOM_P2(128 << 5) >> 5) - DIV2(128);
			wp.ang = (short) NORM_ANGLE(wp.ang);

			// Set the shell number
			wu.ShellNum = ShellCount;
			wp.yrepeat = wp.xrepeat = 13;
			break;
		case SHOT_SHELL:
			wp.z -= Z(13);
			wp.ang = sp.ang;
			HelpMissileLateral(w, 2500);
			wp.ang = (short) NORM_ANGLE(wp.ang + 512);
			HelpMissileLateral(w, 1300);
			wp.ang = (short) NORM_ANGLE(wp.ang - 128 - 64);
			wp.ang += (RANDOM_P2(128 << 5) >> 5) - DIV2(128);
			wp.ang = (short) NORM_ANGLE(wp.ang);

			// Set the shell number
			wu.ShellNum = ShellCount;
			wp.yrepeat = wp.xrepeat = 18;
			break;
		}

		SetOwner(SpriteNum, w);
		wp.shade = -15;
		wu.ceiling_dist = (short) Z(1);
		wu.floor_dist = (short) Z(1);
		wu.Counter = 0;
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		wp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		wu.Flags &= ~(SPR_BOUNCE | SPR_UNDERWATER); // Make em' bounce

		wu.xchange = MOVEx(wp.xvel, wp.ang);
		wu.ychange = MOVEy(wp.xvel, wp.ang);
		wu.zchange = wp.zvel;

		wu.jump_speed = 200;
		wu.jump_speed += RANDOM_RANGE(400);
		wu.jump_speed = (short) -wu.jump_speed;

		DoBeginJump(w);
		wu.jump_grav = ACTOR_GRAVITY;
	}
	
	public static void JWeaponSaveable()
	{
		SaveData(BloodSprayFall);
		SaveData(DoRadiationCloud);
		SaveData(DoChemBomb);
		SaveData(DoCaltrops);
		SaveData(DoCaltropsStick);
		SaveData(DoFlag);
		SaveData(DoCarryFlag);
		SaveData(DoCarryFlagNoDet);
		SaveData(DoPhosphorus);
		SaveData(DoBloodSpray);
		SaveData(DoWallBloodDrip);
		SaveData(s_BloodSpray);
		SaveData(s_PhosphorExp);
		SaveData(s_NukeMushroom);
		SaveData(s_RadiationCloud);
		SaveData(s_ChemBomb);
		SaveData(s_Caltrops);
		SaveData(s_CaltropsStick);
		SaveData(s_CarryFlag);
		SaveData(s_CarryFlagNoDet);
		SaveData(s_Flag);
		SaveData(s_Phosphorus);
		SaveData(s_BloodSprayChunk);
		SaveData(s_BloodSprayDrip);
	}
}
