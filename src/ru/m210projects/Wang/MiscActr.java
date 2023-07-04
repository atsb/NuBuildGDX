package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Wang.Actor.DoActorSectorDamage;
import static ru.m210projects.Wang.Actor.DoActorSlide;
import static ru.m210projects.Wang.Actor.KeepActorOnFloor;
import static ru.m210projects.Wang.Ai.DoActorPickClosePlayer;
import static ru.m210projects.Wang.Digi.DIGI_ANIMEMAD1;
import static ru.m210projects.Wang.Digi.DIGI_ANIMEMAD2;
import static ru.m210projects.Wang.Digi.DIGI_ANIMESING1;
import static ru.m210projects.Wang.Digi.DIGI_ANIMESING2;
import static ru.m210projects.Wang.Digi.DIGI_LANI049;
import static ru.m210projects.Wang.Digi.DIGI_LANI051;
import static ru.m210projects.Wang.Digi.DIGI_LANI052;
import static ru.m210projects.Wang.Digi.DIGI_LANI054;
import static ru.m210projects.Wang.Digi.DIGI_LANI060;
import static ru.m210projects.Wang.Digi.DIGI_LANI063;
import static ru.m210projects.Wang.Digi.DIGI_LANI065;
import static ru.m210projects.Wang.Digi.DIGI_LANI066;
import static ru.m210projects.Wang.Digi.DIGI_LANI073;
import static ru.m210projects.Wang.Digi.DIGI_LANI075;
import static ru.m210projects.Wang.Digi.DIGI_LANI077;
import static ru.m210projects.Wang.Digi.DIGI_LANI079;
import static ru.m210projects.Wang.Digi.DIGI_LANI089;
import static ru.m210projects.Wang.Digi.DIGI_LANI091;
import static ru.m210projects.Wang.Digi.DIGI_LANI093;
import static ru.m210projects.Wang.Digi.DIGI_LANI095;
import static ru.m210projects.Wang.Digi.DIGI_PALARM;
import static ru.m210projects.Wang.Digi.DIGI_PROLL1;
import static ru.m210projects.Wang.Digi.DIGI_PROLL2;
import static ru.m210projects.Wang.Digi.DIGI_PROLL3;
import static ru.m210projects.Wang.Digi.DIGI_PRUNECACKLE;
import static ru.m210projects.Wang.Digi.DIGI_PRUNECACKLE2;
import static ru.m210projects.Wang.Digi.DIGI_PRUNECACKLE3;
import static ru.m210projects.Wang.Digi.DIGI_TOILETGIRLFART1;
import static ru.m210projects.Wang.Digi.DIGI_TOILETGIRLFART2;
import static ru.m210projects.Wang.Digi.DIGI_TOILETGIRLFART3;
import static ru.m210projects.Wang.Digi.DIGI_TOILETGIRLPAIN;
import static ru.m210projects.Wang.Digi.DIGI_TOILETGIRLSCREAM;
import static ru.m210projects.Wang.Digi.DIGI_TRASHLID;
import static ru.m210projects.Wang.Enemies.Coolie.EnemyDefaults;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_RESTORE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_XFLIP;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.SEC;
import static ru.m210projects.Wang.Gameutils.SET_BOOL1;
import static ru.m210projects.Wang.Gameutils.SF_QUICK_CALL;
import static ru.m210projects.Wang.Gameutils.SPRITEp_MID;
import static ru.m210projects.Wang.Gameutils.SPRX_PLAYER_OR_ENEMY;
import static ru.m210projects.Wang.Gameutils.SPR_CLIMBING;
import static ru.m210projects.Wang.Gameutils.SPR_FALLING;
import static ru.m210projects.Wang.Gameutils.SPR_JUMPING;
import static ru.m210projects.Wang.Gameutils.SPR_SLIDING;
import static ru.m210projects.Wang.Gameutils.SPR_XFLIP_TOGGLE;
import static ru.m210projects.Wang.Gameutils.SP_TAG5;
import static ru.m210projects.Wang.Gameutils.STD_RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL1;
import static ru.m210projects.Wang.Type.Saveable.*;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.JTags.TAG_PACHINKOLIGHT;
import static ru.m210projects.Wang.Names.CARGIRL_R0;
import static ru.m210projects.Wang.Names.MECHANICGIRL_R0;
import static ru.m210projects.Wang.Names.PACHINKO1;
import static ru.m210projects.Wang.Names.PACHINKO2;
import static ru.m210projects.Wang.Names.PACHINKO3;
import static ru.m210projects.Wang.Names.PACHINKO4;
import static ru.m210projects.Wang.Names.PRUNEGIRL_R0;
import static ru.m210projects.Wang.Names.SAILORGIRL_R0;
import static ru.m210projects.Wang.Names.STAT_ENEMY;
import static ru.m210projects.Wang.Names.TOILETGIRL_R0;
import static ru.m210projects.Wang.Names.TRASHCAN;
import static ru.m210projects.Wang.Names.WASHGIRL_R0;
import static ru.m210projects.Wang.Rooms.FAFcansee;
import static ru.m210projects.Wang.Sector.ComboSwitchTest;
import static ru.m210projects.Wang.Sector.DoMatchEverything;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_none;
import static ru.m210projects.Wang.Sprites.ActorCoughItem;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Tags.TAG_COMBO_SWITCH_EVERYTHING;
import static ru.m210projects.Wang.Type.MyTypes.ON;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Weapon.InitEnemyUzi;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Type.ATTRIBUTE;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;
import ru.m210projects.Wang.Type.VOC3D;

public class MiscActr {

	private static final ATTRIBUTE ToiletGirlAttrib = new ATTRIBUTE(new short[] { 0, 0, 0, 0 }, // Speeds
			new short[] { 0, 0, 0, 0 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { 0, 0, DIGI_TOILETGIRLSCREAM, DIGI_TOILETGIRLPAIN, DIGI_TOILETGIRLSCREAM, 0, 0, 0, 0, 0 });

	//////////////////////
	//
	// TOILETGIRL STAND
	//
	//////////////////////
	private static final int TOILETGIRL_RATE = 60;

	private static final Animator DoToiletGirl = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoToiletGirl(SpriteNum) != 0;
		}
	};

	private static final State s_ToiletGirlStand[] = { new State(TOILETGIRL_R0 + 0, TOILETGIRL_RATE, DoToiletGirl), // s_ToiletGirlStand[1]},
			new State(TOILETGIRL_R0 + 1, TOILETGIRL_RATE, DoToiletGirl),// s_ToiletGirlStand[0]}
	};

	//////////////////////
	//
	// TOILETGIRL UZI
	//
	//////////////////////

	private static final int TOILETGIRL_UZI_RATE = 8;
	private static final int TOILETGIRL_FIRE_R0 = TOILETGIRL_R0 + 2;

	private static final Animator ToiletGirlUzi = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return ToiletGirlUzi(SpriteNum) != 0;
		}
	};

	private static final Animator InitEnemyUzi = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitEnemyUzi(SpriteNum) != 0;
		}
	};

	private static final State s_ToiletGirlUzi[] = {
			new State(TOILETGIRL_FIRE_R0 + 0, TOILETGIRL_UZI_RATE, ToiletGirlUzi), // s_ToiletGirlUzi[1]},
			new State(TOILETGIRL_FIRE_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ToiletGirlUzi[2]},
			new State(TOILETGIRL_FIRE_R0 + 1, TOILETGIRL_UZI_RATE, ToiletGirlUzi), // s_ToiletGirlUzi[3]},
			new State(TOILETGIRL_FIRE_R0 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ToiletGirlUzi[4]},
			new State(TOILETGIRL_FIRE_R0 + 0, TOILETGIRL_UZI_RATE, ToiletGirlUzi), // s_ToiletGirlUzi[5]},
			new State(TOILETGIRL_FIRE_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ToiletGirlUzi[6]},
			new State(TOILETGIRL_FIRE_R0 + 1, TOILETGIRL_UZI_RATE, ToiletGirlUzi), // s_ToiletGirlUzi[7]},
			new State(TOILETGIRL_FIRE_R0 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ToiletGirlUzi[8]},
			new State(TOILETGIRL_FIRE_R0 + 0, TOILETGIRL_UZI_RATE, ToiletGirlUzi), // s_ToiletGirlUzi[9]},
			new State(TOILETGIRL_FIRE_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ToiletGirlUzi[10]},
			new State(TOILETGIRL_FIRE_R0 + 1, TOILETGIRL_UZI_RATE, ToiletGirlUzi), // s_ToiletGirlUzi[11]},
			new State(TOILETGIRL_FIRE_R0 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ToiletGirlUzi[12]},
			new State(TOILETGIRL_FIRE_R0 + 0, TOILETGIRL_UZI_RATE, ToiletGirlUzi), // s_ToiletGirlUzi[13]},
			new State(TOILETGIRL_FIRE_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ToiletGirlUzi[14]},
			new State(TOILETGIRL_FIRE_R0 + 1, TOILETGIRL_UZI_RATE, ToiletGirlUzi), // s_ToiletGirlUzi[15]},
			new State(TOILETGIRL_FIRE_R0 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),// s_ToiletGirlUzi[0]},
	};

	public static int SetupToiletGirl(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, TOILETGIRL_R0, s_ToiletGirlStand[0]);
			u.Health = 60;
		}

		EnemyDefaults(SpriteNum, null, null);

		ChangeState(SpriteNum, s_ToiletGirlStand[0]);
		u.Attrib = ToiletGirlAttrib;
		u.StateEnd = s_ToiletGirlStand[0];
		u.Rot = null;
		u.RotNum = 0;

		sp.xrepeat = 38;
		sp.yrepeat = 32;
		sp.xvel = sp.yvel = sp.zvel = 0;
		sp.lotag = TOILETGIRL_R0;
		u.FlagOwner = 0;
		u.ID = TOILETGIRL_R0;

		u.Flags &= ~(SPR_XFLIP_TOGGLE);

		return (0);
	}

	private static VOC3D madhandle;
	private static VOC3D handle;

	private static int DoToiletGirl(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		boolean ICanSee = false;

		DoActorPickClosePlayer(SpriteNum);
		ICanSee = FAFcansee(sp.x, sp.y, SPRITEp_MID(sp), sp.sectnum, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y,
				SPRITEp_MID(sprite[u.tgt_sp]), sprite[u.tgt_sp].sectnum);

		if (u.FlagOwner != 1) {
			if (RANDOM_RANGE(1000) > 980) {
				int choose_snd = RANDOM_P2(1024 << 4) >> 4;

				if (handle == null || !handle.isActive()) {
					if (choose_snd > 750)
						handle = PlaySound(DIGI_TOILETGIRLFART1, sp, v3df_dontpan);
					else if (choose_snd > 350)
						handle = PlaySound(DIGI_TOILETGIRLFART2, sp, v3df_dontpan);
					else
						handle = PlaySound(DIGI_TOILETGIRLFART3, sp, v3df_dontpan);
				}
			}
		} else if ((u.WaitTics -= ACTORMOVETICS) <= 0 && ICanSee) {

			if (madhandle == null || !madhandle.isActive()) {
				if (RANDOM_RANGE(1000 << 8) >> 8 > 500)
					madhandle = PlaySound(DIGI_ANIMEMAD1, sp, v3df_dontpan);
				else
					madhandle = PlaySound(DIGI_ANIMEMAD2, sp, v3df_dontpan);
			}
			ChangeState(SpriteNum, s_ToiletGirlUzi[0]);
			u.WaitTics = (short) (SEC(1) + SEC(RANDOM_RANGE(3 << 8) >> 8));
			u.FlagOwner = 0;
		}

		// stay on floor unless doing certain things
		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING | SPR_CLIMBING)) {
			KeepActorOnFloor(SpriteNum);
		}

		// take damage from environment
		DoActorSectorDamage(SpriteNum);
		sp.xvel = sp.yvel = sp.zvel = 0;

		return (0);
	}

	private static int ToiletGirlUzi(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (!TEST(u.Flags, SPR_CLIMBING))
			KeepActorOnFloor(SpriteNum);

		if ((u.WaitTics -= ACTORMOVETICS) <= 0) {
			u.WaitTics = (short) (RANDOM_RANGE(240) + 120);
			ChangeState(SpriteNum, s_ToiletGirlStand[0]);
			u.FlagOwner = 0;
		}

		return (0);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////

	private static final ATTRIBUTE WashGirlAttrib = new ATTRIBUTE(new short[] { 0, 0, 0, 0 }, // Speeds
			new short[] { 0, 0, 0, 0 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { 0, 0, DIGI_TOILETGIRLSCREAM, DIGI_TOILETGIRLPAIN, DIGI_TOILETGIRLSCREAM, 0, 0, 0, 0, 0 });

	//////////////////////
	//
	// WASHGIRL STAND
	//
	//////////////////////
	private static final int WASHGIRL_RATE = 60;

	private static final Animator DoWashGirl = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoWashGirl(SpriteNum) != 0;
		}
	};

	private static final State s_WashGirlStand[] = { new State(WASHGIRL_R0 + 0, WASHGIRL_RATE, DoWashGirl), // s_WashGirlStand[1]},
			new State(WASHGIRL_R0 + 1, WASHGIRL_RATE, DoWashGirl),// s_WashGirlStand[0]}
	};

	private static final int WASHGIRL_RATE2 = 20;
	private static final State s_WashGirlStandScrub[] = { new State(WASHGIRL_R0 + 0, WASHGIRL_RATE2, DoWashGirl), // s_WashGirlStandScrub[1]},
			new State(WASHGIRL_R0 + 1, WASHGIRL_RATE2, DoWashGirl),// s_WashGirlStandScrub[0]}
	};

	//////////////////////
	//
	// WASHGIRL UZI
	//
	//////////////////////

	private static final int WASHGIRL_UZI_RATE = 8;
	private static final int WASHGIRL_FIRE_R0 = WASHGIRL_R0 + 2;

	private static final Animator WashGirlUzi = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return WashGirlUzi(SpriteNum) != 0;
		}
	};

	private static final State s_WashGirlUzi[] = { new State(WASHGIRL_FIRE_R0 + 0, WASHGIRL_UZI_RATE, WashGirlUzi), // s_WashGirlUzi[1]},
			new State(WASHGIRL_FIRE_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_WashGirlUzi[2]},
			new State(WASHGIRL_FIRE_R0 + 1, WASHGIRL_UZI_RATE, WashGirlUzi), // s_WashGirlUzi[3]},
			new State(WASHGIRL_FIRE_R0 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_WashGirlUzi[4]},
			new State(WASHGIRL_FIRE_R0 + 0, WASHGIRL_UZI_RATE, WashGirlUzi), // s_WashGirlUzi[5]},
			new State(WASHGIRL_FIRE_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_WashGirlUzi[6]},
			new State(WASHGIRL_FIRE_R0 + 1, WASHGIRL_UZI_RATE, WashGirlUzi), // s_WashGirlUzi[7]},
			new State(WASHGIRL_FIRE_R0 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_WashGirlUzi[8]},
			new State(WASHGIRL_FIRE_R0 + 0, WASHGIRL_UZI_RATE, WashGirlUzi), // s_WashGirlUzi[9]},
			new State(WASHGIRL_FIRE_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_WashGirlUzi[10]},
			new State(WASHGIRL_FIRE_R0 + 1, WASHGIRL_UZI_RATE, WashGirlUzi), // s_WashGirlUzi[11]},
			new State(WASHGIRL_FIRE_R0 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_WashGirlUzi[12]},
			new State(WASHGIRL_FIRE_R0 + 0, WASHGIRL_UZI_RATE, WashGirlUzi), // s_WashGirlUzi[13]},
			new State(WASHGIRL_FIRE_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_WashGirlUzi[14]},
			new State(WASHGIRL_FIRE_R0 + 1, WASHGIRL_UZI_RATE, WashGirlUzi), // s_WashGirlUzi[15]},
			new State(WASHGIRL_FIRE_R0 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),// s_WashGirlUzi[0]},
	};

	public static int SetupWashGirl(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, WASHGIRL_R0, s_WashGirlStand[0]);
			u.Health = 60;
		}

		EnemyDefaults(SpriteNum, null, null);

		ChangeState(SpriteNum, s_WashGirlStand[0]);
		u.Attrib = WashGirlAttrib;
		u.StateEnd = s_WashGirlStand[0];
		u.Rot = null;
		u.RotNum = 0;

		sp.xrepeat = 28;
		sp.yrepeat = 24;
		sp.xvel = sp.yvel = sp.zvel = 0;
		sp.lotag = WASHGIRL_R0;
		u.FlagOwner = 0;
		u.ID = WASHGIRL_R0;

		u.Flags &= ~(SPR_XFLIP_TOGGLE);

		return (0);
	}

	private static int DoWashGirl(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();
		boolean ICanSee = false;

		DoActorPickClosePlayer(SpriteNum);
		ICanSee = FAFcansee(sp.x, sp.y, SPRITEp_MID(sp), sp.sectnum, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y,
				SPRITEp_MID(sprite[u.tgt_sp]), sprite[u.tgt_sp].sectnum);

		if (RANDOM_RANGE(1000) > 980 && u.ShellNum <= 0) {

			if (handle == null || !handle.isActive()) {
				if (RANDOM_P2(1024 << 4) >> 4 > 500)
					handle = PlaySound(DIGI_ANIMESING1, sp, v3df_dontpan);
				else
					handle = PlaySound(DIGI_ANIMESING2, sp, v3df_dontpan);
			}

			ChangeState(SpriteNum, s_WashGirlStandScrub[0]);
			u.ShellNum = RANDOM_RANGE(2 * 120) + 240;
		} else {
			if (u.ShellNum > 0) {
				if ((u.ShellNum -= ACTORMOVETICS) < 0) {
					ChangeState(SpriteNum, s_WashGirlStand[0]);
					u.ShellNum = 0;
				}
			}
		}

		if (u.FlagOwner != 1) {
		} else if ((u.WaitTics -= ACTORMOVETICS) <= 0 && ICanSee) {

			if (madhandle == null || !madhandle.isActive()) {
				if (RANDOM_RANGE(1000 << 8) >> 8 > 500)
					madhandle = PlaySound(DIGI_ANIMEMAD1, sp, v3df_dontpan);
				else
					madhandle = PlaySound(DIGI_ANIMEMAD2, sp, v3df_dontpan);
			}
			ChangeState(SpriteNum, s_WashGirlUzi[0]);
			u.WaitTics = (short) (SEC(1) + SEC(RANDOM_RANGE(3 << 8) >> 8));
			u.FlagOwner = 0;
		}

		// stay on floor unless doing certain things
		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING | SPR_CLIMBING)) {
			KeepActorOnFloor(SpriteNum);
		}

		// take damage from environment
		DoActorSectorDamage(SpriteNum);
		sp.xvel = sp.yvel = sp.zvel = 0;

		return (0);
	}

	private static int WashGirlUzi(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (!TEST(u.Flags, SPR_CLIMBING))
			KeepActorOnFloor(SpriteNum);

		if ((u.WaitTics -= ACTORMOVETICS) <= 0) {
			u.WaitTics = (short) (RANDOM_RANGE(240) + 120);
			ChangeState(SpriteNum, s_WashGirlStand[0]);
			u.FlagOwner = 0;
		}

		return (0);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////

	private static final ATTRIBUTE TrashCanAttrib = new ATTRIBUTE(new short[] { 0, 0, 0, 0 }, // Speeds
			new short[] { 0, 0, 0, 0 }, // Tic Adjusts
			0, // MaxWeapons;
			new int[] { 0, 0, DIGI_TRASHLID, DIGI_TRASHLID, 0, 0, 0, 0, 0, 0 });

	//////////////////////
	//
	// TRASHCAN STAND
	//
	//////////////////////
	private static final int TRASHCAN_RATE = 120;
	private static final int TRASHCAN_R0 = TRASHCAN;

	private static final Animator DoTrashCan = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoTrashCan(SpriteNum) != 0;
		}
	};

	private static final State s_TrashCanStand[] = { new State(TRASHCAN_R0 + 0, TRASHCAN_RATE, DoTrashCan).setNext(),// s_TrashCanStand[0]}
	};

	//////////////////////
	//
	// TRASHCAN PAIN
	//
	//////////////////////

	private static final int TRASHCAN_PAIN_RATE = 8;
	private static final int TRASHCAN_PAIN_R0 = TRASHCAN;

	private static final Animator TrashCanPain = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return TrashCanPain(SpriteNum) != 0;
		}
	};

	public static final State s_TrashCanPain[] = { new State(TRASHCAN_PAIN_R0 + 0, TRASHCAN_PAIN_RATE, TrashCanPain), // s_TrashCanPain[1]},
			new State(TRASHCAN_PAIN_R0 + 1, TRASHCAN_PAIN_RATE, TrashCanPain), // s_TrashCanPain[2]},
			new State(TRASHCAN_PAIN_R0 + 2, TRASHCAN_PAIN_RATE, TrashCanPain), // s_TrashCanPain[3]},
			new State(TRASHCAN_PAIN_R0 + 3, TRASHCAN_PAIN_RATE, TrashCanPain), // s_TrashCanPain[4]},
			new State(TRASHCAN_PAIN_R0 + 4, TRASHCAN_PAIN_RATE, TrashCanPain), // s_TrashCanPain[5]},
			new State(TRASHCAN_PAIN_R0 + 5, TRASHCAN_PAIN_RATE, TrashCanPain), // s_TrashCanPain[6]},
			new State(TRASHCAN_PAIN_R0 + 6, TRASHCAN_PAIN_RATE, TrashCanPain),// s_TrashCanPain[0]}
	};

	public static int SetupTrashCan(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, TRASHCAN, s_TrashCanStand[0]);
			u.Health = 60;
		}

		EnemyDefaults(SpriteNum, null, null);

		ChangeState(SpriteNum, s_TrashCanStand[0]);
		u.Attrib = TrashCanAttrib;
		u.StateEnd = s_TrashCanStand[0];
		u.Rot = null;
		u.RotNum = 0;

		sp.xrepeat = 46;
		sp.yrepeat = 42;
		sp.xvel = sp.yvel = sp.zvel = 0;
		u.ID = TRASHCAN;

		u.Flags &= ~(SPR_XFLIP_TOGGLE);
		sp.extra &= ~(SPRX_PLAYER_OR_ENEMY);

		return (0);
	}

	private static int DoTrashCan(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		// stay on floor unless doing certain things
		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING | SPR_CLIMBING)) {
			KeepActorOnFloor(SpriteNum);
		}

		sp.xvel = sp.yvel = sp.zvel = 0;

		return (0);
	}

	private static int TrashCanPain(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		if (!TEST(u.Flags, SPR_CLIMBING))
			KeepActorOnFloor(SpriteNum);

		if ((u.WaitTics -= ACTORMOVETICS) <= 0)
			ChangeState(SpriteNum, s_TrashCanStand[0]);

		return (0);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////
	// PACHINKO WIN LIGHT
	////////////////////////////////////////////////////////////////////
	private static final ATTRIBUTE PachinkoLightAttrib = new ATTRIBUTE(new short[] { 0, 0, 0, 0 }, // Speeds
			new short[] { 0, 0, 0, 0 }, // Tic Adjusts
			0, // MaxWeapons;
			new int[] { 0, 0, DIGI_TRASHLID, DIGI_TRASHLID, 0, 0, 0, 0, 0, 0 });

	private static final int PACHINKOLIGHT_RATE = 120;
	private static final int PACHINKOLIGHT_R0 = 623;

	private static final State s_PachinkoLightStand[] = {
			new State(PACHINKOLIGHT_R0 + 0, PACHINKOLIGHT_RATE, null).setNext(),// s_PachinkoLightStand[0]}
	};

	private static final Animator PachinkoLightOperate = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return PachinkoLightOperate(SpriteNum) != 0;
		}
	};

	private static final State s_PachinkoLightOperate[] = { new State(PACHINKOLIGHT_R0 - 0, 12, PachinkoLightOperate), // s_PachinkoLightOperate[1]},
			new State(PACHINKOLIGHT_R0 - 1, 12, PachinkoLightOperate), // s_PachinkoLightOperate[2]},
			new State(PACHINKOLIGHT_R0 - 2, 12, PachinkoLightOperate), // s_PachinkoLightOperate[3]},
			new State(PACHINKOLIGHT_R0 - 3, 12, PachinkoLightOperate), // s_PachinkoLightOperate[4]},
			new State(PACHINKOLIGHT_R0 - 4, 12, PachinkoLightOperate), // s_PachinkoLightOperate[5]},
			new State(PACHINKOLIGHT_R0 - 5, 12, PachinkoLightOperate),// s_PachinkoLightOperate[0]},
	};

	public static int SetupPachinkoLight(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, PACHINKOLIGHT_R0, s_PachinkoLightStand[0]);
			u.Health = 1;
		}

		EnemyDefaults(SpriteNum, null, null);

		ChangeState(SpriteNum, s_PachinkoLightStand[0]);
		u.Attrib = PachinkoLightAttrib;
		u.StateEnd = s_PachinkoLightStand[0];
		u.Rot = null;
		u.RotNum = 0;
		u.ID = PACHINKOLIGHT_R0;

		sp.xvel = sp.yvel = sp.zvel = 0;
		sp.lotag = TAG_PACHINKOLIGHT;
		sp.shade = -2;
		u.spal = (byte) sp.pal;

		u.Flags &= ~(SPR_XFLIP_TOGGLE);
		sp.extra &= ~(SPRX_PLAYER_OR_ENEMY);

		return (0);
	}

	private static int PachinkoLightOperate(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if ((u.WaitTics -= ACTORMOVETICS) <= 0) {
			sp.shade = -2;
			ChangeState(SpriteNum, s_PachinkoLightStand[0]);
		}
		return (0);
	}

	////////////////////////////////////////////////////////////////////
	// PACHINKO MACHINE #1
	////////////////////////////////////////////////////////////////////

	private static boolean Pachinko_Win_Cheat = false;

	private static final ATTRIBUTE Pachinko1Attrib = new ATTRIBUTE(new short[] { 0, 0, 0, 0 }, // Speeds
			new short[] { 0, 0, 0, 0 }, // Tic Adjusts
			0, // MaxWeapons;
			new int[] { 0, 0, DIGI_TRASHLID, DIGI_TRASHLID, 0, 0, 0, 0, 0, 0 });

	private static final int PACHINKO1_RATE = 120;
	private static final int PACHINKO1_R0 = PACHINKO1;

	private static final State s_Pachinko1Stand[] = { new State(PACHINKO1_R0 + 0, PACHINKO1_RATE, null).setNext(),// s_Pachinko1Stand[0]}
	};

	private static final Animator Pachinko1Operate = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return Pachinko1Operate(SpriteNum) != 0;
		}
	};

	private static final Animator PachinkoCheckWin = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return PachinkoCheckWin(SpriteNum) != 0;
		}
	};

	public static final State s_Pachinko1Operate[] = { new State(PACHINKO1_R0 + 0, 12, Pachinko1Operate), // s_Pachinko1Operate[1]},
			new State(PACHINKO1_R0 + 1, 12, Pachinko1Operate), // s_Pachinko1Operate[2]},
			new State(PACHINKO1_R0 + 2, 12, Pachinko1Operate), // s_Pachinko1Operate[3]},
			new State(PACHINKO1_R0 + 3, 12, Pachinko1Operate), // s_Pachinko1Operate[4]},
			new State(PACHINKO1_R0 + 4, 12, Pachinko1Operate), // s_Pachinko1Operate[5]},
			new State(PACHINKO1_R0 + 5, 12, Pachinko1Operate), // s_Pachinko1Operate[6]},
			new State(PACHINKO1_R0 + 6, 12, Pachinko1Operate), // s_Pachinko1Operate[7]},
			new State(PACHINKO1_R0 + 7, 12, Pachinko1Operate), // s_Pachinko1Operate[8]},
			new State(PACHINKO1_R0 + 8, 12, Pachinko1Operate), // s_Pachinko1Operate[9]},
			new State(PACHINKO1_R0 + 9, 12, Pachinko1Operate), // s_Pachinko1Operate[10]},
			new State(PACHINKO1_R0 + 10, 12, Pachinko1Operate), // s_Pachinko1Operate[11]},
			new State(PACHINKO1_R0 + 11, 12, Pachinko1Operate), // s_Pachinko1Operate[12]},
			new State(PACHINKO1_R0 + 12, 12, Pachinko1Operate), // s_Pachinko1Operate[13]},
			new State(PACHINKO1_R0 + 13, 12, Pachinko1Operate), // s_Pachinko1Operate[14]},
			new State(PACHINKO1_R0 + 14, 12, Pachinko1Operate), // s_Pachinko1Operate[15]},
			new State(PACHINKO1_R0 + 15, 12, Pachinko1Operate), // s_Pachinko1Operate[16]},
			new State(PACHINKO1_R0 + 16, 12, Pachinko1Operate), // s_Pachinko1Operate[17]},
			new State(PACHINKO1_R0 + 17, 12, Pachinko1Operate), // s_Pachinko1Operate[18]},
			new State(PACHINKO1_R0 + 18, 12, Pachinko1Operate), // s_Pachinko1Operate[19]},
			new State(PACHINKO1_R0 + 19, 12, Pachinko1Operate), // s_Pachinko1Operate[20]},
			new State(PACHINKO1_R0 + 20, 12, Pachinko1Operate), // s_Pachinko1Operate[21]},
			new State(PACHINKO1_R0 + 21, 12, Pachinko1Operate), // s_Pachinko1Operate[22]},
			new State(PACHINKO1_R0 + 22, SF_QUICK_CALL, PachinkoCheckWin).setNext(s_Pachinko1Stand[0]),// s_Pachinko1Stand[0]}
	};

	public static int SetupPachinko1(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, PACHINKO1, s_Pachinko1Stand[0]);
			u.Health = 1;
		}

		EnemyDefaults(SpriteNum, null, null);

		ChangeState(SpriteNum, s_Pachinko1Stand[0]);
		u.Attrib = Pachinko1Attrib;
		u.StateEnd = s_Pachinko1Stand[0];
		u.Rot = null;
		u.RotNum = 0;
		u.ID = PACHINKO1;

		sp.yvel = sp.zvel = 0;
		sp.lotag = PACHINKO1;

		u.Flags &= ~(SPR_XFLIP_TOGGLE);
		sp.extra &= ~(SPRX_PLAYER_OR_ENEMY);

		return (0);
	}

	private static int PachinkoCheckWin(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		u.WaitTics = 0; // Can operate it again now

		// CON_ConMessage("bool1 = %d",TEST_BOOL1(sp));
		// You already won, no more from this machine!
		if (TEST_BOOL1(sp))
			return (0);

		// Well? Did I win????!
		RANDOM_RANGE(1000);
		if (RANDOM_RANGE(1000) > 900 || Pachinko_Win_Cheat) {
			short i, nexti;
			SPRITE tsp;
			USER tu;

			// Do a possible combo switch
			if (ComboSwitchTest(TAG_COMBO_SWITCH_EVERYTHING, sp.hitag)) {
				DoMatchEverything(Player[myconnectindex], sp.hitag, ON);
			}

			ActorCoughItem(SpriteNum); // I WON! I WON!
			PlaySound(DIGI_PALARM, sp, v3df_none);

			// Can't win any more now!
			SET_BOOL1(sp);

			// Turn on the pachinko lights
			for (i = headspritestat[STAT_ENEMY]; i != -1; i = nexti) {
				nexti = nextspritestat[i];

				tsp = sprite[i];
				tu = pUser[i];

				if (tsp.lotag == TAG_PACHINKOLIGHT) {
					if (tsp.hitag == SP_TAG5(sp)) {
						tsp.shade = -90; // Full brightness
						tu.WaitTics = (short) SEC(3); // Flash
						ChangeState(i, s_PachinkoLightOperate[0]);
					}
				}
			}

		}

		return (0);
	}

	private static int Pachinko1Operate(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];

		int rnd = RANDOM_RANGE(1000);
		if (rnd > 900) {
			rnd = RANDOM_RANGE(1000); // TEMP SOUNDS: Need pachinko sounds!
			if (rnd > 700)
				PlaySound(DIGI_PROLL1, sp, v3df_none);
			else if (rnd > 400)
				PlaySound(DIGI_PROLL2, sp, v3df_none);
			else
				PlaySound(DIGI_PROLL3, sp, v3df_none);
		}

		return (0);
	}

	////////////////////////////////////////////////////////////////////
	// PACHINKO MACHINE #2
	////////////////////////////////////////////////////////////////////

	private static final ATTRIBUTE Pachinko2Attrib = new ATTRIBUTE(new short[] { 0, 0, 0, 0 }, // Speeds
			new short[] { 0, 0, 0, 0 }, // Tic Adjusts
			0, // MaxWeapons;
			new int[] { 0, 0, DIGI_TRASHLID, DIGI_TRASHLID, 0, 0, 0, 0, 0, 0 });

	private static final int PACHINKO2_RATE = 120;
	private static final int PACHINKO2_R0 = PACHINKO2;

	private static final State s_Pachinko2Stand[] = { new State(PACHINKO2_R0 + 0, PACHINKO2_RATE, null).setNext(),// s_Pachinko2Stand[0]}
	};

	public static final State s_Pachinko2Operate[] = { new State(PACHINKO2_R0 + 0, 12, Pachinko1Operate), // s_Pachinko2Operate[1]},
			new State(PACHINKO2_R0 + 1, 12, Pachinko1Operate), // s_Pachinko2Operate[2]},
			new State(PACHINKO2_R0 + 2, 12, Pachinko1Operate), // s_Pachinko2Operate[3]},
			new State(PACHINKO2_R0 + 3, 12, Pachinko1Operate), // s_Pachinko2Operate[4]},
			new State(PACHINKO2_R0 + 4, 12, Pachinko1Operate), // s_Pachinko2Operate[5]},
			new State(PACHINKO2_R0 + 5, 12, Pachinko1Operate), // s_Pachinko2Operate[6]},
			new State(PACHINKO2_R0 + 6, 12, Pachinko1Operate), // s_Pachinko2Operate[7]},
			new State(PACHINKO2_R0 + 7, 12, Pachinko1Operate), // s_Pachinko2Operate[8]},
			new State(PACHINKO2_R0 + 8, 12, Pachinko1Operate), // s_Pachinko2Operate[9]},
			new State(PACHINKO2_R0 + 9, 12, Pachinko1Operate), // s_Pachinko2Operate[10]},
			new State(PACHINKO2_R0 + 10, 12, Pachinko1Operate), // s_Pachinko2Operate[11]},
			new State(PACHINKO2_R0 + 11, 12, Pachinko1Operate), // s_Pachinko2Operate[12]},
			new State(PACHINKO2_R0 + 12, 12, Pachinko1Operate), // s_Pachinko2Operate[13]},
			new State(PACHINKO2_R0 + 13, 12, Pachinko1Operate), // s_Pachinko2Operate[14]},
			new State(PACHINKO2_R0 + 14, 12, Pachinko1Operate), // s_Pachinko2Operate[15]},
			new State(PACHINKO2_R0 + 15, 12, Pachinko1Operate), // s_Pachinko2Operate[16]},
			new State(PACHINKO2_R0 + 16, 12, Pachinko1Operate), // s_Pachinko2Operate[17]},
			new State(PACHINKO2_R0 + 17, 12, Pachinko1Operate), // s_Pachinko2Operate[18]},
			new State(PACHINKO2_R0 + 18, 12, Pachinko1Operate), // s_Pachinko2Operate[19]},
			new State(PACHINKO2_R0 + 19, 12, Pachinko1Operate), // s_Pachinko2Operate[20]},
			new State(PACHINKO2_R0 + 20, 12, Pachinko1Operate), // s_Pachinko2Operate[21]},
			new State(PACHINKO2_R0 + 21, 12, Pachinko1Operate), // s_Pachinko2Operate[22]},
			new State(PACHINKO2_R0 + 22, SF_QUICK_CALL, PachinkoCheckWin).setNext(s_Pachinko2Stand[0]),// s_Pachinko2Stand[0]}
	};

	public static int SetupPachinko2(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, PACHINKO2, s_Pachinko2Stand[0]);
			u.Health = 1;
		}

		EnemyDefaults(SpriteNum, null, null);

		ChangeState(SpriteNum, s_Pachinko2Stand[0]);
		u.Attrib = Pachinko2Attrib;
		u.StateEnd = s_Pachinko2Stand[0];
		u.Rot = null;
		u.RotNum = 0;
		u.ID = PACHINKO2;

		sp.yvel = sp.zvel = 0;
		sp.lotag = PACHINKO2;

		u.Flags &= ~(SPR_XFLIP_TOGGLE);
		sp.extra &= ~(SPRX_PLAYER_OR_ENEMY);

		return (0);
	}

	////////////////////////////////////////////////////////////////////
	// PACHINKO MACHINE #3
	////////////////////////////////////////////////////////////////////

	private static final ATTRIBUTE Pachinko3Attrib = new ATTRIBUTE(new short[] { 0, 0, 0, 0 }, // Speeds
			new short[] { 0, 0, 0, 0 }, // Tic Adjusts
			0, // MaxWeapons;
			new int[] { 0, 0, DIGI_TRASHLID, DIGI_TRASHLID, 0, 0, 0, 0, 0, 0 });

	private static final int PACHINKO3_RATE = 120;
	private static final int PACHINKO3_R0 = PACHINKO3;

	private static final State s_Pachinko3Stand[] = { new State(PACHINKO3_R0 + 0, PACHINKO3_RATE, null).setNext(),// s_Pachinko3Stand[0]}
	};

	public static final State s_Pachinko3Operate[] = { new State(PACHINKO3_R0 + 0, 12, Pachinko1Operate), // s_Pachinko3Operate[1]},
			new State(PACHINKO3_R0 + 1, 12, Pachinko1Operate), // s_Pachinko3Operate[2]},
			new State(PACHINKO3_R0 + 2, 12, Pachinko1Operate), // s_Pachinko3Operate[3]},
			new State(PACHINKO3_R0 + 3, 12, Pachinko1Operate), // s_Pachinko3Operate[4]},
			new State(PACHINKO3_R0 + 4, 12, Pachinko1Operate), // s_Pachinko3Operate[5]},
			new State(PACHINKO3_R0 + 5, 12, Pachinko1Operate), // s_Pachinko3Operate[6]},
			new State(PACHINKO3_R0 + 6, 12, Pachinko1Operate), // s_Pachinko3Operate[7]},
			new State(PACHINKO3_R0 + 7, 12, Pachinko1Operate), // s_Pachinko3Operate[8]},
			new State(PACHINKO3_R0 + 8, 12, Pachinko1Operate), // s_Pachinko3Operate[9]},
			new State(PACHINKO3_R0 + 9, 12, Pachinko1Operate), // s_Pachinko3Operate[10]},
			new State(PACHINKO3_R0 + 10, 12, Pachinko1Operate), // s_Pachinko3Operate[11]},
			new State(PACHINKO3_R0 + 11, 12, Pachinko1Operate), // s_Pachinko3Operate[12]},
			new State(PACHINKO3_R0 + 12, 12, Pachinko1Operate), // s_Pachinko3Operate[13]},
			new State(PACHINKO3_R0 + 13, 12, Pachinko1Operate), // s_Pachinko3Operate[14]},
			new State(PACHINKO3_R0 + 14, 12, Pachinko1Operate), // s_Pachinko3Operate[15]},
			new State(PACHINKO3_R0 + 15, 12, Pachinko1Operate), // s_Pachinko3Operate[16]},
			new State(PACHINKO3_R0 + 16, 12, Pachinko1Operate), // s_Pachinko3Operate[17]},
			new State(PACHINKO3_R0 + 17, 12, Pachinko1Operate), // s_Pachinko3Operate[18]},
			new State(PACHINKO3_R0 + 18, 12, Pachinko1Operate), // s_Pachinko3Operate[19]},
			new State(PACHINKO3_R0 + 19, 12, Pachinko1Operate), // s_Pachinko3Operate[20]},
			new State(PACHINKO3_R0 + 20, 12, Pachinko1Operate), // s_Pachinko3Operate[21]},
			new State(PACHINKO3_R0 + 21, 12, Pachinko1Operate), // s_Pachinko3Operate[22]},
			new State(PACHINKO3_R0 + 22, SF_QUICK_CALL, PachinkoCheckWin).setNext(s_Pachinko3Stand[0]),// s_Pachinko3Stand[0]}
	};

	public static int SetupPachinko3(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];

		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, PACHINKO3, s_Pachinko3Stand[0]);
			u.Health = 1;
		}

		EnemyDefaults(SpriteNum, null, null);

		ChangeState(SpriteNum, s_Pachinko3Stand[0]);
		u.Attrib = Pachinko3Attrib;
		u.StateEnd = s_Pachinko3Stand[0];
		u.Rot = null;
		u.RotNum = 0;
		u.ID = PACHINKO3;

		sp.yvel = sp.zvel = 0;
		sp.lotag = PACHINKO3;

		u.Flags &= ~(SPR_XFLIP_TOGGLE);
		sp.extra &= ~(SPRX_PLAYER_OR_ENEMY);

		return (0);
	}

	////////////////////////////////////////////////////////////////////
	// PACHINKO MACHINE #4
	////////////////////////////////////////////////////////////////////

	private static final ATTRIBUTE Pachinko4Attrib = new ATTRIBUTE(new short[] { 0, 0, 0, 0 }, // Speeds
			new short[] { 0, 0, 0, 0 }, // Tic Adjusts
			0, // MaxWeapons;
			new int[] { 0, 0, DIGI_TRASHLID, DIGI_TRASHLID, 0, 0, 0, 0, 0, 0 });

	private static final int PACHINKO4_RATE = 120;
	private static final int PACHINKO4_R0 = PACHINKO4;

	private static final State s_Pachinko4Stand[] = { new State(PACHINKO4_R0 + 0, PACHINKO4_RATE, null).setNext(),// s_Pachinko4Stand[0]}
	};

	public static final State s_Pachinko4Operate[] = { new State(PACHINKO4_R0 + 0, 12, Pachinko1Operate), // s_Pachinko4Operate[1]},
			new State(PACHINKO4_R0 + 1, 12, Pachinko1Operate), // s_Pachinko4Operate[2]},
			new State(PACHINKO4_R0 + 2, 12, Pachinko1Operate), // s_Pachinko4Operate[3]},
			new State(PACHINKO4_R0 + 3, 12, Pachinko1Operate), // s_Pachinko4Operate[4]},
			new State(PACHINKO4_R0 + 4, 12, Pachinko1Operate), // s_Pachinko4Operate[5]},
			new State(PACHINKO4_R0 + 5, 12, Pachinko1Operate), // s_Pachinko4Operate[6]},
			new State(PACHINKO4_R0 + 6, 12, Pachinko1Operate), // s_Pachinko4Operate[7]},
			new State(PACHINKO4_R0 + 7, 12, Pachinko1Operate), // s_Pachinko4Operate[8]},
			new State(PACHINKO4_R0 + 8, 12, Pachinko1Operate), // s_Pachinko4Operate[9]},
			new State(PACHINKO4_R0 + 9, 12, Pachinko1Operate), // s_Pachinko4Operate[10]},
			new State(PACHINKO4_R0 + 10, 12, Pachinko1Operate), // s_Pachinko4Operate[11]},
			new State(PACHINKO4_R0 + 11, 12, Pachinko1Operate), // s_Pachinko4Operate[12]},
			new State(PACHINKO4_R0 + 12, 12, Pachinko1Operate), // s_Pachinko4Operate[13]},
			new State(PACHINKO4_R0 + 13, 12, Pachinko1Operate), // s_Pachinko4Operate[14]},
			new State(PACHINKO4_R0 + 14, 12, Pachinko1Operate), // s_Pachinko4Operate[15]},
			new State(PACHINKO4_R0 + 15, 12, Pachinko1Operate), // s_Pachinko4Operate[16]},
			new State(PACHINKO4_R0 + 16, 12, Pachinko1Operate), // s_Pachinko4Operate[17]},
			new State(PACHINKO4_R0 + 17, 12, Pachinko1Operate), // s_Pachinko4Operate[18]},
			new State(PACHINKO4_R0 + 18, 12, Pachinko1Operate), // s_Pachinko4Operate[19]},
			new State(PACHINKO4_R0 + 19, 12, Pachinko1Operate), // s_Pachinko4Operate[20]},
			new State(PACHINKO4_R0 + 20, 12, Pachinko1Operate), // s_Pachinko4Operate[21]},
			new State(PACHINKO4_R0 + 21, 12, Pachinko1Operate), // s_Pachinko4Operate[22]},
			new State(PACHINKO4_R0 + 22, SF_QUICK_CALL, PachinkoCheckWin).setNext(s_Pachinko4Stand[0]),// s_Pachinko4Stand[0]}
	};

	public static int SetupPachinko4(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];

		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, PACHINKO4, s_Pachinko4Stand[0]);
			u.Health = 1;
		}

		EnemyDefaults(SpriteNum, null, null);

		ChangeState(SpriteNum, s_Pachinko4Stand[0]);
		u.Attrib = Pachinko4Attrib;
		u.StateEnd = s_Pachinko4Stand[0];
		u.Rot = null;
		u.RotNum = 0;
		u.ID = PACHINKO4;

		sp.yvel = sp.zvel = 0;
		sp.lotag = PACHINKO4;

		u.Flags &= ~(SPR_XFLIP_TOGGLE);
		sp.extra &= ~(SPRX_PLAYER_OR_ENEMY);

		return (0);
	}

	////////////////////////////////////////////////////////////////////////////////

	private static final ATTRIBUTE CarGirlAttrib = new ATTRIBUTE(new short[] { 0, 0, 0, 0 }, // Speeds
			new short[] { 0, 0, 0, 0 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { 0, 0, DIGI_TOILETGIRLSCREAM, DIGI_TOILETGIRLPAIN, DIGI_TOILETGIRLSCREAM, 0, 0, 0, 0, 0 });

	//////////////////////
	//
	// CARGIRL STAND
	//
	//////////////////////
	private static final int CARGIRL_RATE = 60;

	private static final Animator DoCarGirl = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoCarGirl(SpriteNum) != 0;
		}
	};

	private static final State s_CarGirlStand[] = { new State(CARGIRL_R0 + 0, CARGIRL_RATE, DoCarGirl), // s_CarGirlStand[1]},
			new State(CARGIRL_R0 + 1, CARGIRL_RATE, DoCarGirl),// s_CarGirlStand[0]}
	};

	//////////////////////
	//
	// CARGIRL UZI
	//
	//////////////////////

	private static final int CARGIRL_UZI_RATE = 8;
	private static final int CARGIRL_FIRE_R0 = CARGIRL_R0 + 2;

	private static final Animator CarGirlUzi = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return CarGirlUzi(SpriteNum) != 0;
		}
	};

	private static final State s_CarGirlUzi[] = { new State(CARGIRL_FIRE_R0 + 0, 240, CarGirlUzi), // s_CarGirlUzi[1]},
			new State(CARGIRL_FIRE_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_CarGirlUzi[2]},
			new State(CARGIRL_FIRE_R0 + 1, CARGIRL_UZI_RATE, CarGirlUzi), // s_CarGirlUzi[3]},
			new State(CARGIRL_FIRE_R0 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_CarGirlUzi[4]},
			new State(CARGIRL_FIRE_R0 + 0, CARGIRL_UZI_RATE, CarGirlUzi), // s_CarGirlUzi[5]},
			new State(CARGIRL_FIRE_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_CarGirlUzi[6]},
			new State(CARGIRL_FIRE_R0 + 1, CARGIRL_UZI_RATE, CarGirlUzi), // s_CarGirlUzi[7]},
			new State(CARGIRL_FIRE_R0 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_CarGirlUzi[8]},
			new State(CARGIRL_FIRE_R0 + 0, CARGIRL_UZI_RATE, CarGirlUzi), // s_CarGirlUzi[9]},
			new State(CARGIRL_FIRE_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_CarGirlUzi[10]},
			new State(CARGIRL_FIRE_R0 + 1, CARGIRL_UZI_RATE, CarGirlUzi), // s_CarGirlUzi[11]},
			new State(CARGIRL_FIRE_R0 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_CarGirlUzi[12]},
			new State(CARGIRL_FIRE_R0 + 0, CARGIRL_UZI_RATE, CarGirlUzi), // s_CarGirlUzi[13]},
			new State(CARGIRL_FIRE_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_CarGirlUzi[14]},
			new State(CARGIRL_FIRE_R0 + 1, CARGIRL_UZI_RATE, CarGirlUzi), // s_CarGirlUzi[15]},
			new State(CARGIRL_FIRE_R0 + 1, 0 | SF_QUICK_CALL, InitEnemyUzi),// s_CarGirlUzi[0]},
	};

	public static int SetupCarGirl(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, CARGIRL_R0, s_CarGirlStand[0]);
			u.Health = 60;
		}

		EnemyDefaults(SpriteNum, null, null);

		ChangeState(SpriteNum, s_CarGirlStand[0]);
		u.Attrib = CarGirlAttrib;
		u.StateEnd = s_CarGirlStand[0];
		u.Rot = null;
		u.RotNum = 0;

		sp.xrepeat = 29;
		sp.yrepeat = 25;
		sp.xvel = sp.yvel = sp.zvel = 0;
		sp.lotag = CARGIRL_R0;
		u.FlagOwner = 0;
		u.ID = CARGIRL_R0;

		u.Flags &= ~(SPR_XFLIP_TOGGLE);
		sp.cstat |= (CSTAT_SPRITE_XFLIP);

		return (0);
	}

	private static int DoCarGirl(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		boolean ICanSee = false;

		DoActorPickClosePlayer(SpriteNum);
		ICanSee = FAFcansee(sp.x, sp.y, SPRITEp_MID(sp), sp.sectnum, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y,
				SPRITEp_MID(sprite[u.tgt_sp]), sprite[u.tgt_sp].sectnum);

		if (u.FlagOwner == 1) {
			if ((u.WaitTics -= ACTORMOVETICS) <= 0 && ICanSee) {

				if (madhandle == null || !madhandle.isActive()) {
					int choose = RANDOM_RANGE(1000);

					if (choose > 750)
						madhandle = PlaySound(DIGI_LANI049, sp, v3df_dontpan);
					else if (choose > 500)
						madhandle = PlaySound(DIGI_LANI051, sp, v3df_dontpan);
					else if (choose > 250)
						madhandle = PlaySound(DIGI_LANI052, sp, v3df_dontpan);
					else
						madhandle = PlaySound(DIGI_LANI054, sp, v3df_dontpan);
				}
				ChangeState(SpriteNum, s_CarGirlUzi[0]);
				u.WaitTics = (short) (SEC(3) + SEC(RANDOM_RANGE(2 << 8) >> 8));
				u.FlagOwner = 0;
			}
		}

		// stay on floor unless doing certain things
		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING | SPR_CLIMBING)) {
			KeepActorOnFloor(SpriteNum);
		}

		// take damage from environment
		DoActorSectorDamage(SpriteNum);
		sp.xvel = sp.yvel = sp.zvel = 0;

		return (0);
	}

	private static int CarGirlUzi(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (!TEST(u.Flags, SPR_CLIMBING))
			KeepActorOnFloor(SpriteNum);

		if ((u.WaitTics -= ACTORMOVETICS) <= 0) {
			u.WaitTics = (short) (RANDOM_RANGE(240) + 120);
			ChangeState(SpriteNum, s_CarGirlStand[0]);
			u.FlagOwner = 0;
		}

		return (0);
	}

	////////////////////////////////////////////////////////////////////////////////

	private static final ATTRIBUTE MechanicGirlAttrib = new ATTRIBUTE(new short[] { 0, 0, 0, 0 }, // Speeds
			new short[] { 0, 0, 0, 0 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { 0, 0, DIGI_TOILETGIRLSCREAM, DIGI_TOILETGIRLPAIN, DIGI_TOILETGIRLSCREAM, 0, 0, 0, 0, 0 });

	//////////////////////
	//
	// MECHANICGIRL STAND
	//
	//////////////////////
	private static final int MECHANICGIRL_RATE = 60;

	private static final Animator DoMechanicGirl = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoMechanicGirl(SpriteNum) != 0;
		}
	};

	private static final State s_MechanicGirlStand[] = {
			new State(MECHANICGIRL_R0 + 0, MECHANICGIRL_RATE, DoMechanicGirl), // s_MechanicGirlStand[1]},
			new State(MECHANICGIRL_R0 + 1, MECHANICGIRL_RATE, DoMechanicGirl),// s_MechanicGirlStand[0]}
	};

	//////////////////////
	//
	// MECHANICGIRL DRILL
	//
	//////////////////////

	private static final int MECHANICGIRL_DRILL_RATE = 32;
	private static final int MECHANICGIRL_DRILL_R0 = MECHANICGIRL_R0 + 2;

	private static final Animator MechanicGirlDrill = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return MechanicGirlDrill(SpriteNum) != 0;
		}
	};

	private static final State s_MechanicGirlDrill[] = {
			new State(MECHANICGIRL_DRILL_R0 + 0, MECHANICGIRL_DRILL_RATE, MechanicGirlDrill), // s_MechanicGirlDrill[1]},
			new State(MECHANICGIRL_DRILL_R0 + 1, MECHANICGIRL_DRILL_RATE, MechanicGirlDrill),// s_MechanicGirlDrill[0]},
	};

	public static int SetupMechanicGirl(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, MECHANICGIRL_R0, s_MechanicGirlStand[0]);
			u.Health = 60;
		}

		EnemyDefaults(SpriteNum, null, null);

		ChangeState(SpriteNum, s_MechanicGirlStand[0]);
		u.Attrib = MechanicGirlAttrib;
		u.StateEnd = s_MechanicGirlStand[0];
		u.Rot = null;
		u.RotNum = 0;

		sp.xrepeat = 27;
		sp.yrepeat = 26;
		sp.xvel = sp.yvel = sp.zvel = 0;
		sp.lotag = MECHANICGIRL_R0;
		u.FlagOwner = 0;
		u.ID = MECHANICGIRL_R0;

		u.Flags &= ~(SPR_XFLIP_TOGGLE);

		return (0);
	}

	private static int DoMechanicGirl(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		boolean ICanSee = false;

		DoActorPickClosePlayer(SpriteNum);
		ICanSee = FAFcansee(sp.x, sp.y, SPRITEp_MID(sp), sp.sectnum, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y,
				SPRITEp_MID(sprite[u.tgt_sp]), sprite[u.tgt_sp].sectnum);

		if (u.FlagOwner == 1) {
			if ((u.WaitTics -= ACTORMOVETICS) <= 0 && ICanSee) {

				if (madhandle == null || !madhandle.isActive()) {
					int choose = RANDOM_RANGE(1000);

					if (choose > 750)
						madhandle = PlaySound(DIGI_LANI073, sp, v3df_dontpan);
					else if (choose > 500)
						madhandle = PlaySound(DIGI_LANI075, sp, v3df_dontpan);
					else if (choose > 250)
						madhandle = PlaySound(DIGI_LANI077, sp, v3df_dontpan);
					else
						madhandle = PlaySound(DIGI_LANI079, sp, v3df_dontpan);
				}
				ChangeState(SpriteNum, s_MechanicGirlDrill[0]);
				u.WaitTics = (short) (SEC(1) + SEC(RANDOM_RANGE(2 << 8) >> 8));
				u.FlagOwner = 0;
			}
		}

		// stay on floor unless doing certain things
		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING | SPR_CLIMBING)) {
			KeepActorOnFloor(SpriteNum);
		}

		// take damage from environment
		DoActorSectorDamage(SpriteNum);
		sp.xvel = sp.yvel = sp.zvel = 0;

		return (0);
	}

	private static int MechanicGirlDrill(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (!TEST(u.Flags, SPR_CLIMBING))
			KeepActorOnFloor(SpriteNum);

		if ((u.WaitTics -= ACTORMOVETICS) <= 0) {
			u.WaitTics = (short) (RANDOM_RANGE(240) + 120);
			ChangeState(SpriteNum, s_MechanicGirlStand[0]);
			u.FlagOwner = 0;
		}

		return (0);
	}

	////////////////////////////////////////////////////////////////////////////////

	private static final ATTRIBUTE SailorGirlAttrib = new ATTRIBUTE(new short[] { 0, 0, 0, 0 }, // Speeds
			new short[] { 0, 0, 0, 0 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { 0, 0, DIGI_TOILETGIRLSCREAM, DIGI_TOILETGIRLPAIN, DIGI_TOILETGIRLSCREAM, 0, 0, 0, 0, 0 });

	//////////////////////
	//
	// SAILORGIRL STAND
	//
	//////////////////////
	private static final int SAILORGIRL_RATE = 60;

	private static final Animator DoSailorGirl = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoSailorGirl(SpriteNum) != 0;
		}
	};

	private static final State s_SailorGirlStand[] = { new State(SAILORGIRL_R0 + 0, SAILORGIRL_RATE, DoSailorGirl), // s_SailorGirlStand[1]},
			new State(SAILORGIRL_R0 + 1, SAILORGIRL_RATE, DoSailorGirl),// s_SailorGirlStand[0]}
	};

	//////////////////////
	//
	// SAILORGIRL UZI
	//
	//////////////////////

	private static final int SAILORGIRL_UZI_RATE = 128;
	private static final int SAILORGIRL_FIRE_R0 = SAILORGIRL_R0 + 2;

	private static final Animator SailorGirlThrow = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return SailorGirlThrow(SpriteNum) != 0;
		}
	};

	private static final State s_SailorGirlThrow[] = {
			new State(SAILORGIRL_FIRE_R0 + 0, SAILORGIRL_UZI_RATE, SailorGirlThrow).setNext(),// s_SailorGirlThrow[0]},
	};

	private static short alreadythrew;

	public static int SetupSailorGirl(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, SAILORGIRL_R0, s_SailorGirlStand[0]);
			u.Health = 60;
		}

		EnemyDefaults(SpriteNum, null, null);

		ChangeState(SpriteNum, s_SailorGirlStand[0]);
		u.Attrib = SailorGirlAttrib;
		u.StateEnd = s_SailorGirlStand[0];
		u.Rot = null;
		u.RotNum = 0;

		sp.xrepeat = 28;
		sp.yrepeat = 26;
		sp.xvel = sp.yvel = sp.zvel = 0;
		sp.lotag = SAILORGIRL_R0;
		u.FlagOwner = 0;
		u.ID = SAILORGIRL_R0;

		u.Flags &= ~(SPR_XFLIP_TOGGLE);
		alreadythrew = 0;

		return (0);
	}

	private static int DoSailorGirl(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		boolean ICanSee = false;

		DoActorPickClosePlayer(SpriteNum);
		ICanSee = FAFcansee(sp.x, sp.y, SPRITEp_MID(sp), sp.sectnum, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y,
				SPRITEp_MID(sprite[u.tgt_sp]), sprite[u.tgt_sp].sectnum);

		if (u.FlagOwner == 1) {
			if ((u.WaitTics -= ACTORMOVETICS) <= 0 && ICanSee) {

				if (madhandle == null || !madhandle.isActive()) {

					int choose = RANDOM_RANGE(1000);

					if (choose > 750 && alreadythrew < 3) {
						ActorCoughItem(SpriteNum);
						alreadythrew++;
						madhandle = PlaySound(DIGI_LANI060, sp, v3df_dontpan);
					} else if (choose > 500)
						madhandle = PlaySound(DIGI_LANI063, sp, v3df_dontpan);
					else if (choose > 250)
						madhandle = PlaySound(DIGI_LANI065, sp, v3df_dontpan);
					else
						madhandle = PlaySound(DIGI_LANI066, sp, v3df_dontpan);
				}
				ChangeState(SpriteNum, s_SailorGirlThrow[0]);
				u.WaitTics = (short) (SEC(1) + SEC(RANDOM_RANGE(3 << 8) >> 8));
				u.FlagOwner = 0;
			}
		}

		// stay on floor unless doing certain things
		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING | SPR_CLIMBING)) {
			KeepActorOnFloor(SpriteNum);
		}

		// take damage from environment
		DoActorSectorDamage(SpriteNum);
		sp.xvel = sp.yvel = sp.zvel = 0;

		return (0);
	}

	private static int SailorGirlThrow(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (!TEST(u.Flags, SPR_CLIMBING))
			KeepActorOnFloor(SpriteNum);

		if ((u.WaitTics -= ACTORMOVETICS) <= 0) {
			u.WaitTics = (short) (RANDOM_RANGE(240) + 120);
			ChangeState(SpriteNum, s_SailorGirlStand[0]);
			u.FlagOwner = 0;
		}

		return (0);
	}

	////////////////////////////////////////////////////////////////////////////////

	private static final ATTRIBUTE PruneGirlAttrib = new ATTRIBUTE(new short[] { 450, 450, 450, 450 }, // Speeds
			new short[] { 0, 0, 0, 0 }, // Tic Adjusts
			3, // MaxWeapons;
				// {DIGI_PRUNECACKLE3, DIGI_PRUNECACKLE, DIGI_TOILETGIRLSCREAM,
			new int[] { 0, 0, DIGI_TOILETGIRLSCREAM, DIGI_TOILETGIRLPAIN, DIGI_TOILETGIRLSCREAM, 0, 0, 0, 0, 0 });

	//////////////////////
	//
	// PRUNEGIRL STAND
	//
	//////////////////////
	private static final int PRUNEGIRL_RATE = 60;

	private static final Animator DoPruneGirl = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoPruneGirl(SpriteNum) != 0;
		}
	};

	private static final State s_PruneGirlStand[] = { new State(PRUNEGIRL_R0 + 0, PRUNEGIRL_RATE, DoPruneGirl), // s_PruneGirlStand[1]},
			new State(PRUNEGIRL_R0 + 1, PRUNEGIRL_RATE, DoPruneGirl),// s_PruneGirlStand[0]}
	};

	public static int SetupPruneGirl(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];

		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, PRUNEGIRL_R0, s_PruneGirlStand[0]);
			u.Health = 60;
		}

		EnemyDefaults(SpriteNum, null, null);

		ChangeState(SpriteNum, s_PruneGirlStand[0]);
		u.Attrib = PruneGirlAttrib;
		u.StateEnd = s_PruneGirlStand[0];
		u.Rot = null;
		u.RotNum = 0;

		sp.xrepeat = 33;
		sp.yrepeat = 28;
		sp.xvel = sp.yvel = sp.zvel = 0;
		sp.lotag = PRUNEGIRL_R0;
		u.FlagOwner = 0;
		u.ID = PRUNEGIRL_R0;

		u.Flags &= ~(SPR_XFLIP_TOGGLE);

		return (0);
	}

	private static VOC3D coyhandle;

	private static int DoPruneGirl(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		boolean ICanSee = false;

		DoActorPickClosePlayer(SpriteNum);
		ICanSee = FAFcansee(sp.x, sp.y, SPRITEp_MID(sp), sp.sectnum, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y,
				SPRITEp_MID(sprite[u.tgt_sp]), sprite[u.tgt_sp].sectnum);

		if (u.FlagOwner == 1) {
			if ((u.WaitTics -= ACTORMOVETICS) <= 0 && ICanSee) {
				if (madhandle == null || !madhandle.isActive()) {

					int choose = STD_RANDOM_RANGE(1000);

					if (choose > 750)
						madhandle = PlaySound(DIGI_LANI089, sp, v3df_dontpan);
					else if (choose > 500)
						madhandle = PlaySound(DIGI_LANI091, sp, v3df_dontpan);
					else if (choose > 250)
						madhandle = PlaySound(DIGI_LANI093, sp, v3df_dontpan);
					else
						madhandle = PlaySound(DIGI_LANI095, sp, v3df_dontpan);
				}
				u.WaitTics = (short) (SEC(1) + SEC(RANDOM_RANGE(3 << 8) >> 8));
				u.FlagOwner = 0;
			}
		} else {
			if (coyhandle == null || !coyhandle.isActive()) {
				int choose = STD_RANDOM_RANGE(1000);

				if (choose > 990)
					coyhandle = PlaySound(DIGI_PRUNECACKLE, sp, v3df_dontpan);
				else if (choose > 985)
					coyhandle = PlaySound(DIGI_PRUNECACKLE2, sp, v3df_dontpan);
				else if (choose > 980)
					coyhandle = PlaySound(DIGI_PRUNECACKLE3, sp, v3df_dontpan);
				else if (choose > 975)
					coyhandle = PlaySound(DIGI_LANI091, sp, v3df_dontpan);
			}
		}

		// stay on floor unless doing certain things
		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING | SPR_CLIMBING)) {
			KeepActorOnFloor(SpriteNum);
		}

		// take damage from environment
		DoActorSectorDamage(SpriteNum);
		sp.xvel = sp.yvel = sp.zvel = 0;

		return (0);
	}

	public static void InitMiscStates() {
		State.InitState(s_ToiletGirlStand);
		State.InitState(s_ToiletGirlUzi);
		State.InitState(s_WashGirlStand);
		State.InitState(s_WashGirlStandScrub);
		State.InitState(s_WashGirlUzi);
		State.InitState(s_TrashCanPain);
		State.InitState(s_PachinkoLightOperate);
		State.InitState(s_Pachinko1Operate);
		State.InitState(s_Pachinko2Operate);
		State.InitState(s_Pachinko3Operate);
		State.InitState(s_Pachinko4Operate);
		State.InitState(s_CarGirlStand);
		State.InitState(s_CarGirlUzi);
		State.InitState(s_MechanicGirlStand);
		State.InitState(s_MechanicGirlDrill);
		State.InitState(s_SailorGirlStand);
		State.InitState(s_PruneGirlStand);
	}
	
	public static void MiscSaveable()
	{
		SaveData(InitEnemyUzi);
		SaveData(DoToiletGirl);
		SaveData(ToiletGirlUzi);

		SaveData(DoWashGirl);
		SaveData(WashGirlUzi);
		SaveData(DoTrashCan);
		SaveData(TrashCanPain);
		SaveData(PachinkoLightOperate);
		SaveData(PachinkoCheckWin);
		SaveData(Pachinko1Operate);

		SaveData(DoCarGirl);
		SaveData(CarGirlUzi);
		SaveData(DoMechanicGirl);
		SaveData(MechanicGirlDrill);
		SaveData(DoSailorGirl);
		SaveData(SailorGirlThrow);
		SaveData(DoPruneGirl);
		
		SaveData(ToiletGirlAttrib);
		SaveData(WashGirlAttrib);
		SaveData(TrashCanAttrib);
		SaveData(PachinkoLightAttrib);
		SaveData(Pachinko1Attrib);
		SaveData(Pachinko2Attrib);
		SaveData(Pachinko3Attrib);
		SaveData(Pachinko4Attrib);
		SaveData(CarGirlAttrib);
		SaveData(MechanicGirlAttrib);
		SaveData(SailorGirlAttrib);
		SaveData(PruneGirlAttrib);

		SaveData(s_ToiletGirlStand);
		SaveData(s_ToiletGirlUzi);
		SaveData(s_WashGirlStand);
		SaveData(s_WashGirlStandScrub);
		SaveData(s_WashGirlUzi);
		SaveData(s_TrashCanStand);
		SaveData(s_TrashCanPain);
		SaveData(s_PachinkoLightStand);
		SaveData(s_PachinkoLightOperate);
		SaveData(s_Pachinko1Stand);
		SaveData(s_Pachinko1Operate);
		SaveData(s_Pachinko2Stand);
		SaveData(s_Pachinko2Operate);
		SaveData(s_Pachinko3Stand);
		SaveData(s_Pachinko3Operate);
		SaveData(s_Pachinko4Stand);
		SaveData(s_Pachinko4Operate);
		SaveData(s_CarGirlStand);
		SaveData(s_CarGirlUzi);
		SaveData(s_MechanicGirlStand);
		SaveData(s_MechanicGirlDrill);
		SaveData(s_SailorGirlStand);
		SaveData(s_SailorGirlThrow);
		SaveData(s_PruneGirlStand);
	}
}
