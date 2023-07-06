package ru.m210projects.Wang.Enemies;

import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Wang.Actor.DoActorBeginJump;
import static ru.m210projects.Wang.Actor.DoActorDeathMove;
import static ru.m210projects.Wang.Actor.DoActorDebris;
import static ru.m210projects.Wang.Actor.DoActorDie;
import static ru.m210projects.Wang.Actor.DoActorFall;
import static ru.m210projects.Wang.Actor.DoActorJump;
import static ru.m210projects.Wang.Actor.DoActorSectorDamage;
import static ru.m210projects.Wang.Actor.DoActorSlide;
import static ru.m210projects.Wang.Actor.DoJump;
import static ru.m210projects.Wang.Actor.DoScaleSprite;
import static ru.m210projects.Wang.Actor.KeepActorOnFloor;
import static ru.m210projects.Wang.Ai.DoActorMoveJump;
import static ru.m210projects.Wang.Ai.DoActorPickClosePlayer;
import static ru.m210projects.Wang.Ai.DoActorSetSpeed;
import static ru.m210projects.Wang.Ai.FAST_SPEED;
import static ru.m210projects.Wang.Ai.InitActorAlertNoise;
import static ru.m210projects.Wang.Ai.InitActorAmbientNoise;
import static ru.m210projects.Wang.Ai.InitActorAttack;
import static ru.m210projects.Wang.Ai.InitActorAttackNoise;
import static ru.m210projects.Wang.Ai.InitActorDecide;
import static ru.m210projects.Wang.Ai.InitActorFindPlayer;
import static ru.m210projects.Wang.Ai.InitActorMoveCloser;
import static ru.m210projects.Wang.Ai.InitActorReposition;
import static ru.m210projects.Wang.Ai.InitActorRunAway;
import static ru.m210projects.Wang.Ai.InitActorWanderAround;
import static ru.m210projects.Wang.Digi.DIGI_BUNNYAMBIENT;
import static ru.m210projects.Wang.Digi.DIGI_BUNNYATTACK;
import static ru.m210projects.Wang.Digi.DIGI_BUNNYDIE2;
import static ru.m210projects.Wang.Digi.DIGI_FAGRABBIT1;
import static ru.m210projects.Wang.Digi.DIGI_FAGRABBIT2;
import static ru.m210projects.Wang.Digi.DIGI_FAGRABBIT3;
import static ru.m210projects.Wang.Digi.DIGI_RABBITHUMP1;
import static ru.m210projects.Wang.Digi.DIGI_RABBITHUMP2;
import static ru.m210projects.Wang.Digi.DIGI_RABBITHUMP3;
import static ru.m210projects.Wang.Digi.DIGI_RABBITHUMP4;
import static ru.m210projects.Wang.Enemies.Coolie.EnemyDefaults;
import static ru.m210projects.Wang.Enemies.Ripper.PickJumpMaxSpeed;
import static ru.m210projects.Wang.Game.Global_PLock;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_ACTOR;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_INVISIBLE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_RESTORE;
import static ru.m210projects.Wang.Gameutils.DISTANCE;
import static ru.m210projects.Wang.Gameutils.FACING;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.SEC;
import static ru.m210projects.Wang.Gameutils.SF_QUICK_CALL;
import static ru.m210projects.Wang.Gameutils.SF_TIC_ADJUST;
import static ru.m210projects.Wang.Gameutils.SPRITEp_TOS;
import static ru.m210projects.Wang.Gameutils.SPRITEp_UPPER;
import static ru.m210projects.Wang.Gameutils.SPRX_PLAYER_OR_ENEMY;
import static ru.m210projects.Wang.Gameutils.SPR_ACTIVE;
import static ru.m210projects.Wang.Gameutils.SPR_FALLING;
import static ru.m210projects.Wang.Gameutils.SPR_JUMPING;
import static ru.m210projects.Wang.Gameutils.SPR_SLIDING;
import static ru.m210projects.Wang.Gameutils.SPR_XFLIP_TOGGLE;
import static ru.m210projects.Wang.Gameutils.STD_RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL3;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.JWeapon.InitBloodSpray;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Names.BUNNY_DEAD;
import static ru.m210projects.Wang.Names.BUNNY_DIE;
import static ru.m210projects.Wang.Names.BUNNY_RUN_R0;
import static ru.m210projects.Wang.Names.BUNNY_RUN_R1;
import static ru.m210projects.Wang.Names.BUNNY_RUN_R2;
import static ru.m210projects.Wang.Names.BUNNY_RUN_R3;
import static ru.m210projects.Wang.Names.BUNNY_RUN_R4;
import static ru.m210projects.Wang.Names.BUNNY_STAND_R0;
import static ru.m210projects.Wang.Names.BUNNY_STAND_R1;
import static ru.m210projects.Wang.Names.BUNNY_STAND_R2;
import static ru.m210projects.Wang.Names.BUNNY_STAND_R3;
import static ru.m210projects.Wang.Names.BUNNY_STAND_R4;
import static ru.m210projects.Wang.Names.BUNNY_SWIPE_R0;
import static ru.m210projects.Wang.Names.BUNNY_SWIPE_R1;
import static ru.m210projects.Wang.Names.BUNNY_SWIPE_R2;
import static ru.m210projects.Wang.Names.BUNNY_SWIPE_R3;
import static ru.m210projects.Wang.Names.BUNNY_SWIPE_R4;
import static ru.m210projects.Wang.Names.STAT_DEFAULT;
import static ru.m210projects.Wang.Names.STAT_ENEMY;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER0;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER1;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER8;
import static ru.m210projects.Wang.Player.QueueFloorBlood;
import static ru.m210projects.Wang.Rooms.COVERinsertsprite;
import static ru.m210projects.Wang.Rooms.FAFcansee;
import static ru.m210projects.Wang.Shrap.SpawnShrap;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.PlayerSound;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_doppler;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sound.v3df_none;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.DoActorZrange;
import static ru.m210projects.Wang.Sprites.MoveSkip4;
import static ru.m210projects.Wang.Sprites.NewStateGroup;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Sprites.move_actor;
import static ru.m210projects.Wang.Sprites.move_sprite;
import static ru.m210projects.Wang.Track.ActorFollowTrack;
import static ru.m210projects.Wang.Type.MyTypes.FALSE;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.MyTypes.TRUE;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Type.Saveable.SaveGroup;
import static ru.m210projects.Wang.Weapon.HelpMissileLateral;
import static ru.m210projects.Wang.Weapon.InitBunnySlash;
import static ru.m210projects.Wang.Weapon.SetSuicide;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Type.ATTRIBUTE;
import ru.m210projects.Wang.Type.Actor_Action_Set;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;

public class Bunny {

	public static int Bunny_Count = 0;

	private static final Decision BunnyBattle[] = { new Decision(748, InitActorMoveCloser),
			new Decision(750, InitActorAlertNoise), new Decision(760, InitActorAttackNoise),
			new Decision(1024, InitActorMoveCloser), };

	private static final Decision BunnyOffense[] = { new Decision(600, InitActorMoveCloser),
			new Decision(700, InitActorAlertNoise), new Decision(1024, InitActorMoveCloser), };

	private static final Decision BunnyBroadcast[] = { new Decision(21, InitActorAlertNoise),
			new Decision(51, InitActorAmbientNoise), new Decision(1024, InitActorDecide), };

	private static final Decision BunnySurprised[] = { new Decision(500, InitActorRunAway),
			new Decision(701, InitActorMoveCloser), new Decision(1024, InitActorDecide), };

	private static final Decision BunnyEvasive[] = { new Decision(500, InitActorWanderAround),
			new Decision(1020, InitActorRunAway), new Decision(1024, InitActorAmbientNoise), };

	private static final Decision BunnyLostTarget[] = { new Decision(900, InitActorFindPlayer),
			new Decision(1024, InitActorWanderAround), };

	private static final Decision BunnyCloseRange[] = { new Decision(1024, InitActorAttack), };

	private static final Decision BunnyWander[] = { new Decision(1024, InitActorReposition), };

	private static final Personality WhiteBunnyPersonality = new Personality(BunnyBattle, BunnyOffense, BunnyBroadcast,
			BunnySurprised, BunnyEvasive, BunnyLostTarget, BunnyCloseRange, BunnyCloseRange);

	private static final Personality BunnyPersonality = new Personality(BunnyEvasive, BunnyEvasive, BunnyEvasive,
			BunnyWander, BunnyWander, BunnyWander, BunnyEvasive, BunnyEvasive);

	private static final ATTRIBUTE BunnyAttrib = new ATTRIBUTE(new short[] { 100, 120, 140, 180 }, // Speeds
			new short[] { 5, 0, -2, -4 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { DIGI_BUNNYAMBIENT, 0, DIGI_BUNNYATTACK, DIGI_BUNNYATTACK, DIGI_BUNNYDIE2, 0, 0, 0, 0, 0 });

	private static final ATTRIBUTE WhiteBunnyAttrib = new ATTRIBUTE(new short[] { 200, 220, 340, 380 }, // Speeds
			new short[] { 5, 0, -2, -4 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { DIGI_BUNNYAMBIENT, 0, DIGI_BUNNYATTACK, DIGI_BUNNYATTACK, DIGI_BUNNYDIE2, 0, 0, 0, 0, 0 });

	//////////////////////
	//
	// BUNNY RUN
	//
	//////////////////////

	public static final int BUNNY_RUN_RATE = 10;

	public static final Animator DoBunnyMove = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoBunnyMove(spr) != 0;
		}
	};

	public static final Animator DoBunnyGrowUp = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoBunnyGrowUp(spr) != 0;
		}
	};

	private static final State s_BunnyRun[][] = {
			{ new State(BUNNY_RUN_R0 + 0, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[0][1]},
					new State(BUNNY_RUN_R0 + 1, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[0][2]},
					new State(BUNNY_RUN_R0 + 2, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[0][3]},
					new State(BUNNY_RUN_R0 + 3, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[0][4]},
					new State(BUNNY_RUN_R0 + 4, SF_QUICK_CALL, DoBunnyGrowUp), // s_BunnyRun[0][5]},
					new State(BUNNY_RUN_R0 + 4, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove),// s_BunnyRun[0][0]},
			}, { new State(BUNNY_RUN_R1 + 0, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[1][1]},
					new State(BUNNY_RUN_R1 + 1, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[1][2]},
					new State(BUNNY_RUN_R1 + 2, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[1][3]},
					new State(BUNNY_RUN_R1 + 3, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[1][4]},
					new State(BUNNY_RUN_R1 + 4, SF_QUICK_CALL, DoBunnyGrowUp), // s_BunnyRun[1][5]},
					new State(BUNNY_RUN_R1 + 4, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove),// s_BunnyRun[1][0]},
			}, { new State(BUNNY_RUN_R2 + 0, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[2][1]},
					new State(BUNNY_RUN_R2 + 1, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[2][2]},
					new State(BUNNY_RUN_R2 + 2, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[2][3]},
					new State(BUNNY_RUN_R2 + 3, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[2][4]},
					new State(BUNNY_RUN_R2 + 4, SF_QUICK_CALL, DoBunnyGrowUp), // s_BunnyRun[2][5]},
					new State(BUNNY_RUN_R2 + 4, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove),// s_BunnyRun[2][0]},
			}, { new State(BUNNY_RUN_R3 + 0, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[3][1]},
					new State(BUNNY_RUN_R3 + 1, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[3][2]},
					new State(BUNNY_RUN_R3 + 2, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[3][3]},
					new State(BUNNY_RUN_R3 + 3, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[3][4]},
					new State(BUNNY_RUN_R3 + 4, SF_QUICK_CALL, DoBunnyGrowUp), // s_BunnyRun[3][5]},
					new State(BUNNY_RUN_R3 + 4, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove),// s_BunnyRun[3][0]},
			}, { new State(BUNNY_RUN_R4 + 0, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[4][1]},
					new State(BUNNY_RUN_R4 + 1, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[4][2]},
					new State(BUNNY_RUN_R4 + 2, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[4][3]},
					new State(BUNNY_RUN_R4 + 3, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove), // s_BunnyRun[4][4]},
					new State(BUNNY_RUN_R4 + 4, SF_QUICK_CALL, DoBunnyGrowUp), // s_BunnyRun[4][5]},
					new State(BUNNY_RUN_R4 + 4, BUNNY_RUN_RATE | SF_TIC_ADJUST, DoBunnyMove),// s_BunnyRun[4][0]},
			} };

	//////////////////////
	//
	// BUNNY STAND
	//
	//////////////////////

	public static final int BUNNY_STAND_RATE = 12;

	public static final Animator DoBunnyEat = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoBunnyEat(spr) != 0;
		}
	};

	private static final State s_BunnyStand[][] = { { new State(BUNNY_STAND_R0 + 0, BUNNY_STAND_RATE, DoBunnyEat), // s_BunnyStand[0][1]},
			new State(BUNNY_STAND_R0 + 4, SF_QUICK_CALL, DoBunnyGrowUp), // s_BunnyStand[0][2]},
			new State(BUNNY_STAND_R0 + 4, BUNNY_STAND_RATE, DoBunnyEat),// s_BunnyStand[0][0]},
			}, { new State(BUNNY_STAND_R1 + 0, BUNNY_STAND_RATE, DoBunnyEat), // s_BunnyStand[1][1]},
					new State(BUNNY_STAND_R1 + 4, SF_QUICK_CALL, DoBunnyGrowUp), // s_BunnyStand[1][2]},
					new State(BUNNY_STAND_R1 + 4, BUNNY_STAND_RATE, DoBunnyEat),// s_BunnyStand[1][0]},
			}, { new State(BUNNY_STAND_R2 + 0, BUNNY_STAND_RATE, DoBunnyEat), // s_BunnyStand[2][1]},
					new State(BUNNY_STAND_R2 + 4, SF_QUICK_CALL, DoBunnyGrowUp), // s_BunnyStand[2][2]},
					new State(BUNNY_STAND_R2 + 4, BUNNY_STAND_RATE, DoBunnyEat),// s_BunnyStand[2][0]},
			}, { new State(BUNNY_STAND_R3 + 0, BUNNY_STAND_RATE, DoBunnyEat), // s_BunnyStand[3][1]},
					new State(BUNNY_STAND_R3 + 4, SF_QUICK_CALL, DoBunnyGrowUp), // s_BunnyStand[3][2]},
					new State(BUNNY_STAND_R3 + 4, BUNNY_STAND_RATE, DoBunnyEat),// s_BunnyStand[3][0]},
			}, { new State(BUNNY_STAND_R4 + 0, BUNNY_STAND_RATE, DoBunnyEat), // s_BunnyStand[4][1]},
					new State(BUNNY_STAND_R4 + 4, SF_QUICK_CALL, DoBunnyGrowUp), // s_BunnyStand[4][2]},
					new State(BUNNY_STAND_R4 + 4, BUNNY_STAND_RATE, DoBunnyEat),// s_BunnyStand[4][0]},
			}, };

	//////////////////////
	//
	// BUNNY GET LAYED
	//
	//////////////////////

	public static final int BUNNY_SCREW_RATE = 16;
	public static final Animator DoBunnyScrew = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoBunnyScrew(spr) != 0;
		}
	};

	private static final State s_BunnyScrew[][] = { { new State(BUNNY_STAND_R0 + 0, BUNNY_SCREW_RATE, DoBunnyScrew), // s_BunnyScrew[0][1]},
			new State(BUNNY_STAND_R0 + 2, BUNNY_SCREW_RATE, DoBunnyScrew),// s_BunnyScrew[0][0]},
			}, { new State(BUNNY_STAND_R1 + 0, BUNNY_SCREW_RATE, DoBunnyScrew), // s_BunnyScrew[1][1]},
					new State(BUNNY_STAND_R1 + 2, BUNNY_SCREW_RATE, DoBunnyScrew),// s_BunnyScrew[1][0]},
			}, { new State(BUNNY_STAND_R2 + 0, BUNNY_SCREW_RATE, DoBunnyScrew), // s_BunnyScrew[2][1]},
					new State(BUNNY_STAND_R2 + 2, BUNNY_SCREW_RATE, DoBunnyScrew),// s_BunnyScrew[2][0]},
			}, { new State(BUNNY_STAND_R3 + 0, BUNNY_SCREW_RATE, DoBunnyScrew), // s_BunnyScrew[3][1]},
					new State(BUNNY_STAND_R3 + 2, BUNNY_SCREW_RATE, DoBunnyScrew),// s_BunnyScrew[3][0]},
			}, { new State(BUNNY_STAND_R4 + 0, BUNNY_SCREW_RATE, DoBunnyScrew), // s_BunnyScrew[4][1]},
					new State(BUNNY_STAND_R4 + 2, BUNNY_SCREW_RATE, DoBunnyScrew),// s_BunnyScrew[4][0]},
			}, };

	//////////////////////
	//
	// BUNNY SWIPE
	//
	//////////////////////

	public static final int BUNNY_SWIPE_RATE = 8;

	public static final Animator NullBunny = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return NullBunny(spr) != 0;
		}
	};

	public static final Animator InitBunnySlash = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return InitBunnySlash(spr) != 0;
		}
	};

	private static final State s_BunnySwipe[][] = { { 
		new State(BUNNY_SWIPE_R0 + 0, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[0][1]},
			new State(BUNNY_SWIPE_R0 + 1, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[0][2]},
			new State(BUNNY_SWIPE_R0 + 1, 0 | SF_QUICK_CALL, InitBunnySlash), // s_BunnySwipe[0][3]},
			new State(BUNNY_SWIPE_R0 + 2, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[0][4]},
			new State(BUNNY_SWIPE_R0 + 3, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[0][5]},
			new State(BUNNY_SWIPE_R0 + 3, 0 | SF_QUICK_CALL, InitBunnySlash), // s_BunnySwipe[0][6]},
			new State(BUNNY_SWIPE_R0 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_BunnySwipe[0][7]},
			new State(BUNNY_SWIPE_R0 + 3, BUNNY_SWIPE_RATE, DoBunnyMove).setNext(),// s_BunnySwipe[0][7]},
			}, 
			
			{ new State(BUNNY_RUN_R1 + 0, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[1][1]},
					new State(BUNNY_RUN_R1 + 1, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[1][2]},
					new State(BUNNY_RUN_R1 + 1, 0 | SF_QUICK_CALL, InitBunnySlash), // s_BunnySwipe[1][3]},
					new State(BUNNY_RUN_R1 + 2, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[1][4]},
					new State(BUNNY_RUN_R1 + 3, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[1][5]},
					new State(BUNNY_RUN_R1 + 3, 0 | SF_QUICK_CALL, InitBunnySlash), // s_BunnySwipe[1][6]},
					new State(BUNNY_RUN_R1 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_BunnySwipe[1][7]},
					new State(BUNNY_RUN_R1 + 3, BUNNY_SWIPE_RATE, DoBunnyMove).setNext(),// s_BunnySwipe[1][7]},
			}, 
			
			{ new State(BUNNY_RUN_R2 + 0, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[2][1]},
					new State(BUNNY_RUN_R2 + 1, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[2][2]},
					new State(BUNNY_RUN_R2 + 1, 0 | SF_QUICK_CALL, InitBunnySlash), // s_BunnySwipe[2][3]},
					new State(BUNNY_RUN_R2 + 2, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[2][4]},
					new State(BUNNY_RUN_R2 + 3, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[2][5]},
					new State(BUNNY_RUN_R2 + 3, 0 | SF_QUICK_CALL, InitBunnySlash), // s_BunnySwipe[2][6]},
					new State(BUNNY_RUN_R2 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_BunnySwipe[2][7]},
					new State(BUNNY_RUN_R2 + 3, BUNNY_SWIPE_RATE, DoBunnyMove).setNext(),// s_BunnySwipe[2][7]},
			}, 
			
			
			{ new State(BUNNY_RUN_R3 + 0, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[3][1]},
					new State(BUNNY_RUN_R3 + 1, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[3][2]},
					new State(BUNNY_RUN_R3 + 1, 0 | SF_QUICK_CALL, InitBunnySlash), // s_BunnySwipe[3][3]},
					new State(BUNNY_RUN_R3 + 2, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[3][4]},
					new State(BUNNY_RUN_R3 + 3, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[3][5]},
					new State(BUNNY_RUN_R3 + 3, 0 | SF_QUICK_CALL, InitBunnySlash), // s_BunnySwipe[3][6]},
					new State(BUNNY_RUN_R3 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_BunnySwipe[3][7]},
					new State(BUNNY_RUN_R3 + 3, BUNNY_SWIPE_RATE, DoBunnyMove).setNext(),// s_BunnySwipe[3][7]},
			}, 
			
			{ new State(BUNNY_RUN_R4 + 0, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[4][1]},
					new State(BUNNY_RUN_R4 + 1, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[4][2]},
					new State(BUNNY_RUN_R4 + 1, 0 | SF_QUICK_CALL, InitBunnySlash), // s_BunnySwipe[4][3]},
					new State(BUNNY_RUN_R4 + 2, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[4][4]},
					new State(BUNNY_RUN_R4 + 3, BUNNY_SWIPE_RATE, NullBunny), // s_BunnySwipe[4][5]},
					new State(BUNNY_RUN_R4 + 3, 0 | SF_QUICK_CALL, InitBunnySlash), // s_BunnySwipe[4][6]},
					new State(BUNNY_RUN_R4 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_BunnySwipe[4][7]},
					new State(BUNNY_RUN_R4 + 3, BUNNY_SWIPE_RATE, DoBunnyMove).setNext(),// s_BunnySwipe[4][7]},
			} };

	//////////////////////
	//
	// BUNNY HEART - show players heart
	//
	//////////////////////

	public static final int BUNNY_HEART_RATE = 14;

	public static final Animator DoBunnyStandKill = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoBunnyStandKill(spr) != 0;
		}
	};

	private static final State s_BunnyHeart[][] = {
			{ new State(BUNNY_SWIPE_R0 + 0, BUNNY_HEART_RATE, DoBunnyStandKill).setNext(),// s_BunnyHeart[0][0]},
			}, { new State(BUNNY_SWIPE_R1 + 0, BUNNY_HEART_RATE, DoBunnyStandKill).setNext(),// s_BunnyHeart[1][0]},
			}, { new State(BUNNY_SWIPE_R2 + 0, BUNNY_HEART_RATE, DoBunnyStandKill).setNext(),// s_BunnyHeart[2][0]},
			}, { new State(BUNNY_SWIPE_R3 + 0, BUNNY_HEART_RATE, DoBunnyStandKill).setNext(),// s_BunnyHeart[3][0]},
			}, { new State(BUNNY_SWIPE_R4 + 0, BUNNY_HEART_RATE, DoBunnyStandKill).setNext(),// s_BunnyHeart[4][0]},
			} };

	//////////////////////
	//
	// BUNNY PAIN
	//
	//////////////////////

	public static final int BUNNY_PAIN_RATE = 38;

	public static final Animator DoBunnyPain = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoBunnyPain(spr) != 0;
		}
	};

	private static final State s_BunnyPain[][] = {
			{ new State(BUNNY_SWIPE_R0 + 0, BUNNY_PAIN_RATE, DoBunnyPain).setNext(),// s_BunnyPain[0][0]},
			}, { new State(BUNNY_SWIPE_R0 + 0, BUNNY_PAIN_RATE, DoBunnyPain).setNext(),// s_BunnyPain[1][0]},
			}, { new State(BUNNY_SWIPE_R0 + 0, BUNNY_PAIN_RATE, DoBunnyPain).setNext(),// s_BunnyPain[2][0]},
			}, { new State(BUNNY_SWIPE_R0 + 0, BUNNY_PAIN_RATE, DoBunnyPain).setNext(),// s_BunnyPain[3][0]},
			}, { new State(BUNNY_SWIPE_R0 + 0, BUNNY_PAIN_RATE, DoBunnyPain).setNext(),// s_BunnyPain[4][0]},
			} };

	//////////////////////
	//
	// BUNNY JUMP
	//
	//////////////////////

	public static final int BUNNY_JUMP_RATE = 25;

	public static final Animator DoBunnyMoveJump = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoBunnyMoveJump(spr) != 0;
		}
	};

	private static final State s_BunnyJump[][] = { { new State(BUNNY_RUN_R0 + 1, BUNNY_JUMP_RATE, DoBunnyMoveJump), // s_BunnyJump[0][1]},
			new State(BUNNY_RUN_R0 + 2, BUNNY_JUMP_RATE, DoBunnyMoveJump).setNext(),// s_BunnyJump[0][1]},
			}, { new State(BUNNY_RUN_R1 + 1, BUNNY_JUMP_RATE, DoBunnyMoveJump), // s_BunnyJump[1][1]},
					new State(BUNNY_RUN_R1 + 2, BUNNY_JUMP_RATE, DoBunnyMoveJump).setNext(),// s_BunnyJump[1][1]},
			}, { new State(BUNNY_RUN_R2 + 1, BUNNY_JUMP_RATE, DoBunnyMoveJump), // s_BunnyJump[2][1]},
					new State(BUNNY_RUN_R2 + 2, BUNNY_JUMP_RATE, DoBunnyMoveJump).setNext(),// s_BunnyJump[2][1]},
			}, { new State(BUNNY_RUN_R3 + 1, BUNNY_JUMP_RATE, DoBunnyMoveJump), // s_BunnyJump[3][1]},
					new State(BUNNY_RUN_R3 + 2, BUNNY_JUMP_RATE, DoBunnyMoveJump).setNext(),// s_BunnyJump[3][1]},
			}, { new State(BUNNY_RUN_R4 + 1, BUNNY_JUMP_RATE, DoBunnyMoveJump), // s_BunnyJump[4][1]},
					new State(BUNNY_RUN_R4 + 2, BUNNY_JUMP_RATE, DoBunnyMoveJump).setNext(),// s_BunnyJump[4][1]},
			} };

	//////////////////////
	//
	// BUNNY FALL
	//
	//////////////////////

	public static final int BUNNY_FALL_RATE = 25;

	private static final State s_BunnyFall[][] = {
			{ new State(BUNNY_RUN_R0 + 3, BUNNY_FALL_RATE, DoBunnyMoveJump).setNext(),// s_BunnyFall[0][0]},
			}, { new State(BUNNY_RUN_R1 + 3, BUNNY_FALL_RATE, DoBunnyMoveJump).setNext(),// s_BunnyFall[1][0]},
			}, { new State(BUNNY_RUN_R2 + 3, BUNNY_FALL_RATE, DoBunnyMoveJump).setNext(),// s_BunnyFall[2][0]},
			}, { new State(BUNNY_RUN_R3 + 3, BUNNY_FALL_RATE, DoBunnyMoveJump).setNext(),// s_BunnyFall[3][0]},
			}, { new State(BUNNY_RUN_R4 + 3, BUNNY_FALL_RATE, DoBunnyMoveJump).setNext(),// s_BunnyFall[4][0]},
			} };

	//////////////////////
	//
	// BUNNY JUMP ATTACK
	//
	//////////////////////

	public static final int BUNNY_JUMP_ATTACK_RATE = 35;
	public static final Animator DoBunnyBeginJumpAttack = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoBunnyBeginJumpAttack(spr) != 0;
		}
	};

	//////////////////////
	//
	// BUNNY DIE
	//
	//////////////////////

	public static final int BUNNY_DIE_RATE = 16;
	public static final Animator BunnySpew = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return BunnySpew(spr) != 0;
		}
	};

	private static final State s_BunnyDie[] = { new State(BUNNY_DIE + 0, BUNNY_DIE_RATE, NullBunny), // s_BunnyDie[1]},
			new State(BUNNY_DIE + 0, SF_QUICK_CALL, BunnySpew), // s_BunnyDie[2]},
			new State(BUNNY_DIE + 1, BUNNY_DIE_RATE, NullBunny), // s_BunnyDie[3]},
			new State(BUNNY_DIE + 2, BUNNY_DIE_RATE, NullBunny), // s_BunnyDie[4]},
			new State(BUNNY_DIE + 2, BUNNY_DIE_RATE, NullBunny), // s_BunnyDie[5]},
			new State(BUNNY_DEAD, BUNNY_DIE_RATE, DoActorDebris).setNext(),// s_BunnyDie[5]},
	};

	public static final int BUNNY_DEAD_RATE = 8;

	private static final State s_BunnyDead[] = { new State(BUNNY_DIE + 0, BUNNY_DEAD_RATE, null), // s_BunnyDie[1]},
			new State(BUNNY_DIE + 0, SF_QUICK_CALL, BunnySpew), // s_BunnyDie[2]},
			new State(BUNNY_DIE + 1, BUNNY_DEAD_RATE, null), // s_BunnyDead[3]},
			new State(BUNNY_DIE + 2, BUNNY_DEAD_RATE, null), // s_BunnyDead[4]},
			new State(BUNNY_DEAD, SF_QUICK_CALL, QueueFloorBlood), // s_BunnyDead[5]},
			new State(BUNNY_DEAD, BUNNY_DEAD_RATE, DoActorDebris).setNext(),// s_BunnyDead[5]},
	};

	public static final Animator DoActorDeathMove = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoActorDeathMove(spr) != 0;
		}
	};

	private static final State s_BunnyDeathJump[] = {
			new State(BUNNY_DIE + 0, BUNNY_DIE_RATE, DoActorDeathMove).setNext(),// s_BunnyDeathJump[0]}
	};

	private static final State s_BunnyDeathFall[] = {
			new State(BUNNY_DIE + 1, BUNNY_DIE_RATE, DoActorDeathMove).setNext(),// s_BunnyDeathFall[0]}
	};

	public enum BunnyStateGroup implements StateGroup {
		sg_BunnyStand(s_BunnyStand[0], s_BunnyStand[1], s_BunnyStand[2], s_BunnyStand[3], s_BunnyStand[4]),
		sg_BunnyRun(s_BunnyRun[0], s_BunnyRun[1], s_BunnyRun[2], s_BunnyRun[3], s_BunnyRun[4]),
		sg_BunnyJump(s_BunnyJump[0], s_BunnyJump[1], s_BunnyJump[2], s_BunnyJump[3], s_BunnyJump[4]),
		sg_BunnyFall(s_BunnyFall[0], s_BunnyFall[1], s_BunnyFall[2], s_BunnyFall[3], s_BunnyFall[4]),
		sg_BunnyPain(s_BunnyPain[0], s_BunnyPain[1], s_BunnyPain[2], s_BunnyPain[3], s_BunnyPain[4]),
		sg_BunnyDie(s_BunnyDie), sg_BunnyDead(s_BunnyDead), sg_BunnyDeathJump(s_BunnyDeathJump),
		sg_BunnyDeathFall(s_BunnyDeathFall),
		sg_BunnyHeart(s_BunnyHeart[0], s_BunnyHeart[1], s_BunnyHeart[2], s_BunnyHeart[3], s_BunnyHeart[4]),
		sg_BunnySwipe(s_BunnySwipe[0], s_BunnySwipe[1], s_BunnySwipe[2], s_BunnySwipe[3], s_BunnySwipe[4]),
		sg_BunnyScrew(s_BunnyScrew[0], s_BunnyScrew[1], s_BunnyScrew[2], s_BunnyScrew[3], s_BunnyScrew[4]);

		private final State[][] group;
		private int index = -1;

		BunnyStateGroup(State[]... states) {
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

	public static void InitBunnyStates() {
		for (BunnyStateGroup sg : BunnyStateGroup.values()) {
			for (int rot = 0; rot < sg.group.length; rot++) {
				State.InitState(sg.group[rot]);
			}
		}
	}

	private static final Actor_Action_Set BunnyActionSet = new Actor_Action_Set(BunnyStateGroup.sg_BunnyStand,
			BunnyStateGroup.sg_BunnyRun, BunnyStateGroup.sg_BunnyJump, BunnyStateGroup.sg_BunnyFall, null, // BunnyStateGroup.sg_BunnyCrawl,
			null, // BunnyStateGroup.sg_BunnySwim,
			null, // BunnyStateGroup.sg_BunnyFly,
			null, // BunnyStateGroup.sg_BunnyRise,
			null, // BunnyStateGroup.sg_BunnySit,
			null, // BunnyStateGroup.sg_BunnyLook,
			null, // climb
			BunnyStateGroup.sg_BunnyPain, BunnyStateGroup.sg_BunnyDie, null, BunnyStateGroup.sg_BunnyDead,
			BunnyStateGroup.sg_BunnyDeathJump, BunnyStateGroup.sg_BunnyDeathFall, null, new short[] { 1024 }, null,
			new short[] { 1024 }, new StateGroup[] { BunnyStateGroup.sg_BunnyHeart, BunnyStateGroup.sg_BunnyRun }, null,
			null);

	private static final Actor_Action_Set BunnyWhiteActionSet = new Actor_Action_Set(BunnyStateGroup.sg_BunnyStand,
			BunnyStateGroup.sg_BunnyRun, BunnyStateGroup.sg_BunnyJump, BunnyStateGroup.sg_BunnyFall, null, // BunnyStateGroup.sg_BunnyCrawl,
			null, // BunnyStateGroup.sg_BunnySwim,
			null, // BunnyStateGroup.sg_BunnyFly,
			null, // BunnyStateGroup.sg_BunnyRise,
			null, // BunnyStateGroup.sg_BunnySit,
			null, // BunnyStateGroup.sg_BunnyLook,
			null, // climb
			BunnyStateGroup.sg_BunnyPain, // pain
			BunnyStateGroup.sg_BunnyDie, null, BunnyStateGroup.sg_BunnyDead, BunnyStateGroup.sg_BunnyDeathJump,
			BunnyStateGroup.sg_BunnyDeathFall, new StateGroup[] { BunnyStateGroup.sg_BunnySwipe }, new short[] { 1024 },
			new StateGroup[] { BunnyStateGroup.sg_BunnySwipe }, new short[] { 1024 },
			new StateGroup[] { BunnyStateGroup.sg_BunnyHeart, BunnyStateGroup.sg_BunnySwipe }, null, null);

	public static int SetupBunny(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, BUNNY_RUN_R0, s_BunnyRun[0][0]);
			u.Health = 10;
		}

		Bunny_Count++;

		ChangeState(SpriteNum, s_BunnyRun[0][0]);
		u.StateEnd = s_BunnyDie[0];
		u.Rot = BunnyStateGroup.sg_BunnyRun;
		u.ShellNum = 0; // Not Pregnant right now
		u.FlagOwner = 0;

		sp.clipdist = (150) >> 2;

		if (sp.pal == PALETTE_PLAYER1) {
			EnemyDefaults(SpriteNum, BunnyWhiteActionSet, WhiteBunnyPersonality);
			u.Attrib = WhiteBunnyAttrib;
			sp.xrepeat = 96;
			sp.yrepeat = 90;

			sp.clipdist = 200 >> 2;

			if (!TEST(sp.cstat, CSTAT_SPRITE_RESTORE))
				u.Health = 60;
		} else if (sp.pal == PALETTE_PLAYER8) // Male Rabbit
		{
			EnemyDefaults(SpriteNum, BunnyActionSet, BunnyPersonality);
			u.Attrib = BunnyAttrib;

			// sp.shade = 0; // darker
			if (!TEST(sp.cstat, CSTAT_SPRITE_RESTORE))
				u.Health = 20;
			u.Flag1 = 0;
		} else { // Female Rabbit
			EnemyDefaults(SpriteNum, BunnyActionSet, BunnyPersonality);
			u.Attrib = BunnyAttrib;
			u.spal = (byte) (sp.pal = PALETTE_PLAYER0);
			u.Flag1 = SEC(5);
			// sp.shade = 0; // darker
		}

		DoActorSetSpeed(SpriteNum, FAST_SPEED);

		u.Flags |= (SPR_XFLIP_TOGGLE);

		u.zclip = Z(16);
		u.floor_dist = (short) Z(8);
		u.ceiling_dist = (short) Z(8);
		u.lo_step = (short) Z(16);

		return (0);
	}

	//
	// JUMP ATTACK
	//

	private static final int RANDOM_NEG(int x) {
		return (RANDOM_P2((x) << 1) - (x));
	}

	private static int DoBunnyBeginJumpAttack(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		SPRITE psp = sprite[pUser[SpriteNum].tgt_sp];

		short tang;

		tang = engine.getangle(psp.x - sp.x, psp.y - sp.y);

		if (move_sprite(SpriteNum, sintable[NORM_ANGLE(tang + 512)] >> 7, sintable[tang] >> 7, 0, u.ceiling_dist,
				u.floor_dist, CLIPMASK_ACTOR, ACTORMOVETICS) != 0)
			sp.ang = (short) (NORM_ANGLE(sp.ang + 1024) + (RANDOM_NEG(256 << 6) >> 6));
		else
			sp.ang = NORM_ANGLE(tang + (RANDOM_NEG(256 << 6) >> 6));

		DoActorSetSpeed(SpriteNum, FAST_SPEED);

		PickJumpMaxSpeed(SpriteNum, -400); // was -800

		u.Flags |= (SPR_JUMPING);
		u.Flags &= ~(SPR_FALLING);

		// set up individual actor jump gravity
		u.jump_grav = 17; // was 8

		// if I didn't do this here they get stuck in the air sometimes
		DoActorZrange(SpriteNum);

		DoJump(SpriteNum);

		return (0);
	}

	private static int DoBunnyMoveJump(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			int nx, ny;

			// Move while jumping
			nx = sp.xvel * (int) sintable[NORM_ANGLE(sp.ang + 512)] >> 14;
			ny = sp.xvel * (int) sintable[sp.ang] >> 14;

			move_actor(SpriteNum, nx, ny, 0);

			if (TEST(u.Flags, SPR_JUMPING))
				DoActorJump(SpriteNum);
			else
				DoActorFall(SpriteNum);
		}

		DoActorZrange(SpriteNum);

		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {

			InitActorDecide(SpriteNum);
		}

		return (0);
	}

	private static int DoPickCloseBunny(int SpriteNum) {
		USER u = pUser[SpriteNum], tu;
		SPRITE sp = sprite[SpriteNum], tsp;
		int dist, near_dist = 1000;
		short i, nexti;

		// if actor can still see the player
		int look_height = SPRITEp_TOS(sp);
		boolean ICanSee = false;

		for (i = headspritestat[STAT_ENEMY]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			tsp = sprite[i];
			tu = pUser[i];

			if (sp == tsp)
				continue;

			if (tu.ID != BUNNY_RUN_R0)
				continue;

			dist = DISTANCE(tsp.x, tsp.y, sp.x, sp.y);

			if (dist > near_dist)
				continue;

			ICanSee = FAFcansee(sp.x, sp.y, look_height, sp.sectnum, tsp.x, tsp.y, SPRITEp_UPPER(tsp), tsp.sectnum);

			if (ICanSee && dist < near_dist && tu.ID == BUNNY_RUN_R0) {
				near_dist = dist;
				u.tgt_sp = u.lo_sp = i;
				return (i);
			}
		}
		return (-1);
	}

	private static int DoBunnyQuickJump(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if (u.spal != PALETTE_PLAYER8)
			return (FALSE);

		if (u.lo_sp == -1 && u.spal == PALETTE_PLAYER8 && MoveSkip4 != 0)
			DoPickCloseBunny(SpriteNum);

		// Random Chance of like sexes fighting
		if (u.lo_sp != -1) {
			int hitsprite = u.lo_sp;
			SPRITE tsp = sprite[u.lo_sp];
			USER tu = pUser[hitsprite];

			if (tu == null || tu.ID != BUNNY_RUN_R0)
				return (FALSE);

			// Not mature enough yet
			if (sp.xrepeat != 64 || sp.yrepeat != 64)
				return (FALSE);
			if (tsp.xrepeat != 64 || tsp.yrepeat != 64)
				return (FALSE);

			// Kill a rival
			// Only males fight
			if (tu.spal == sp.pal && RANDOM_RANGE(1000) > 995) {
				if (u.spal == PALETTE_PLAYER8 && tu.spal == PALETTE_PLAYER8) {
					PlaySound(DIGI_BUNNYATTACK, sp, v3df_follow);
					PlaySound(DIGI_BUNNYDIE2, tsp, v3df_follow);
					tu.Health = 0;

					// Blood fountains
					InitBloodSpray(hitsprite, true, -1);

					if (SpawnShrap(hitsprite, SpriteNum)) {
						SetSuicide(hitsprite);
					} else
						DoActorDie(hitsprite, SpriteNum);

					Bunny_Count--; // Bunny died

					u.lo_sp = -1;
					return (TRUE);
				}
			}
		}

		// Get layed!
		if (u.lo_sp != -1 && u.spal == PALETTE_PLAYER8) // Only males check this
		{
			int hitsprite = u.lo_sp;
			SPRITE tsp = sprite[u.lo_sp];
			USER tu = pUser[hitsprite];

			if (tu == null || tu.ID != BUNNY_RUN_R0)
				return (FALSE);

			// Not mature enough to mate yet
			if (sp.xrepeat != 64 || sp.yrepeat != 64)
				return (FALSE);
			if (tsp.xrepeat != 64 || tsp.yrepeat != 64)
				return (FALSE);

			if (tu.ShellNum <= 0 && tu.WaitTics <= 0 && u.WaitTics <= 0) {
				if (TEST(tsp.extra, SPRX_PLAYER_OR_ENEMY)) {
					PlayerStr pp = null;

					if (RANDOM_RANGE(1000) < 995 && tu.spal != PALETTE_PLAYER0)
						return (FALSE);

					DoActorPickClosePlayer(SpriteNum);

					if (pUser[u.tgt_sp].PlayerP != -1)
						pp = Player[pUser[u.tgt_sp].PlayerP];

					if (tu.spal != PALETTE_PLAYER0) {
						if (tu.Flag1 > 0)
							return (FALSE);
						tu.FlagOwner = 1; // FAG!
						tu.Flag1 = SEC(10);
						if (pp != null) {
							short choose_snd;
							int fagsnds[] = { DIGI_FAGRABBIT1, DIGI_FAGRABBIT2, DIGI_FAGRABBIT3 };

							if (pp == Player[myconnectindex]) {
								choose_snd = (short) (STD_RANDOM_RANGE(2 << 8) >> 8);
								if (FAFcansee(sp.x, sp.y, SPRITEp_TOS(sp), sp.sectnum, pp.posx, pp.posy, pp.posz,
										pp.cursectnum) && FACING(sp, sprite[u.tgt_sp]))
									PlayerSound(fagsnds[choose_snd], v3df_doppler | v3df_follow | v3df_dontpan, pp);
							}
						}
					} else {
						if (pp != null && RANDOM_RANGE(1000) > 200) {
							short choose_snd;
							int straightsnds[] = { DIGI_RABBITHUMP1, DIGI_RABBITHUMP2, DIGI_RABBITHUMP3,
									DIGI_RABBITHUMP4 };

							if (pp == Player[myconnectindex]) {
								choose_snd = (short) (STD_RANDOM_RANGE(3 << 8) >> 8);
								if (FAFcansee(sp.x, sp.y, SPRITEp_TOS(sp), sp.sectnum, pp.posx, pp.posy, pp.posz,
										pp.cursectnum) && FACING(sp, sprite[u.tgt_sp]))
									PlayerSound(straightsnds[choose_snd], v3df_doppler | v3df_follow | v3df_dontpan,
											pp);
							}
						}
					}

					sp.x = tsp.x; // Mount up little bunny
					sp.y = tsp.y;
					sp.ang = tsp.ang;
					sp.ang = NORM_ANGLE(sp.ang + 1024);
					HelpMissileLateral(SpriteNum, 2000);
					sp.ang = tsp.ang;
					u.Vis = sp.ang; // Remember angles for later
					tu.Vis = tsp.ang;

					NewStateGroup(SpriteNum, BunnyStateGroup.sg_BunnyScrew);
					NewStateGroup(hitsprite, BunnyStateGroup.sg_BunnyScrew);
					if (gs.ParentalLock || Global_PLock) {
						sp.cstat |= (CSTAT_SPRITE_INVISIBLE); // Turn em' invisible
						tsp.cstat |= (CSTAT_SPRITE_INVISIBLE); // Turn em' invisible
					}
					u.WaitTics = tu.WaitTics = (short) SEC(10); // Mate for this int
					return (TRUE);
				}
			}
		}

		return (FALSE);
	}

	private static int NullBunny(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			if (TEST(u.Flags, SPR_JUMPING))
				DoActorJump(SpriteNum);
			else
				DoActorFall(SpriteNum);
		}

		// stay on floor unless doing certain things
		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING))
			KeepActorOnFloor(SpriteNum);

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		DoActorSectorDamage(SpriteNum);

		return (0);
	}

	private static int DoBunnyPain(int SpriteNum) {
		USER u = pUser[SpriteNum];

		NullBunny(SpriteNum);

		if ((u.WaitTics -= ACTORMOVETICS) <= 0)
			InitActorDecide(SpriteNum);
		return (0);
	}

	public static int DoBunnyRipHeart(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		SPRITE tsp = sprite[u.tgt_sp];

		NewStateGroup(SpriteNum, BunnyStateGroup.sg_BunnyHeart);
		u.WaitTics = 6 * 120;

		// player face bunny
		tsp.ang = engine.getangle(sp.x - tsp.x, sp.y - tsp.y);
		return (0);
	}

	private static int DoBunnyStandKill(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		NullBunny(SpriteNum);

		// Growl like the bad ass bunny you are!
		if (RANDOM_RANGE(1000) > 800)
			PlaySound(DIGI_BUNNYATTACK, sp, v3df_none);

		if ((u.WaitTics -= ACTORMOVETICS) <= 0)
			NewStateGroup(SpriteNum, BunnyStateGroup.sg_BunnyRun);
		return (0);
	}

	private static void BunnyHatch(int Weapon) {
		SPRITE wp = sprite[Weapon];
		USER wu = pUser[Weapon];

		short newsp;
		SPRITE np;
		USER nu;

		short rip_ang;

		rip_ang = (short) RANDOM_P2(2048);

		newsp = COVERinsertsprite(wp.sectnum, STAT_DEFAULT);
		np = sprite[newsp];
		np.reset();

		np.sectnum = wp.sectnum;
		np.statnum = STAT_DEFAULT;
		np.x = wp.x;
		np.y = wp.y;
		np.z = wp.z;
		np.owner = -1;
		np.xrepeat = 30; // Baby size
		np.yrepeat = 24;
		np.ang = rip_ang;
		np.pal = 0;
		SetupBunny(newsp);
		nu = pUser[newsp];
		np.shade = wp.shade;

		// make immediately active
		nu.Flags |= (SPR_ACTIVE);
		if (RANDOM_RANGE(1000) > 500) // Boy or Girl?
			nu.spal = (byte) (np.pal = PALETTE_PLAYER0); // Girl
		else {
			nu.spal = (byte) (np.pal = PALETTE_PLAYER8); // Boy
			// Oops, mommy died giving birth to a boy
			if (RANDOM_RANGE(1000) > 500) {
				wu.Health = 0;
				Bunny_Count--; // Bunny died

				// Blood fountains
				InitBloodSpray(Weapon, true, -1);

				if (SpawnShrap(Weapon, newsp)) {
					SetSuicide(Weapon);
				} else
					DoActorDie(Weapon, newsp);
			}
		}

		nu.ShellNum = 0; // Not Pregnant right now

		NewStateGroup(newsp, nu.ActorActionSet.Jump);
		nu.ActorActionFunc = DoActorMoveJump;
		DoActorSetSpeed(newsp, FAST_SPEED);
		PickJumpMaxSpeed(newsp, -600);

		nu.Flags |= (SPR_JUMPING);
		nu.Flags &= ~(SPR_FALLING);

		nu.jump_grav = 8;

		// if I didn't do this here they get stuck in the air sometimes
		DoActorZrange(newsp);

		DoActorJump(newsp);

	}

	public static int BunnyHatch2(short Weapon) {
		SPRITE wp = sprite[Weapon];

		short newsp;
		SPRITE np;
		USER nu;

		newsp = COVERinsertsprite(wp.sectnum, STAT_DEFAULT);
		np = sprite[newsp];
		np.reset();
		np.sectnum = wp.sectnum;
		np.statnum = STAT_DEFAULT;
		np.x = wp.x;
		np.y = wp.y;
		np.z = wp.z;
		np.owner = -1;
		np.xrepeat = 30; // Baby size
		np.yrepeat = 24;
		np.ang = (short) RANDOM_P2(2048);
		np.pal = 0;
		SetupBunny(newsp);
		nu = pUser[newsp];
		np.shade = wp.shade;

		// make immediately active
		nu.Flags |= (SPR_ACTIVE);
		if (RANDOM_RANGE(1000) > 500) // Boy or Girl?
		{
			nu.spal = (byte) (np.pal = PALETTE_PLAYER0); // Girl
			nu.Flag1 = SEC(5);
		} else {
			nu.spal = (byte) (np.pal = PALETTE_PLAYER8); // Boy
			nu.Flag1 = 0;
		}

		nu.ShellNum = 0; // Not Pregnant right now

		NewStateGroup(newsp, nu.ActorActionSet.Jump);
		nu.ActorActionFunc = DoActorMoveJump;
		DoActorSetSpeed(newsp, FAST_SPEED);
		if (TEST_BOOL3(wp)) {
			PickJumpMaxSpeed(newsp, -600 - RANDOM_RANGE(600));
			np.xrepeat = np.yrepeat = 64;
			np.xvel = (short) (150 + RANDOM_RANGE(1000));
			nu.Health = 1; // Easy to pop. Like shootn' skeet.
			np.ang -= RANDOM_RANGE(128);
			np.ang += RANDOM_RANGE(128);
		} else
			PickJumpMaxSpeed(newsp, -600);

		nu.Flags |= (SPR_JUMPING);
		nu.Flags &= ~(SPR_FALLING);

		nu.jump_grav = 8;
		nu.FlagOwner = 0;

		nu.active_range = 75000; // Set it far

		// if I didn't do this here they get stuck in the air sometimes
		DoActorZrange(newsp);

		DoActorJump(newsp);

		return (newsp);
	}

	private static int DoBunnyMove(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		// Parental lock crap
		if (TEST(sp.cstat, CSTAT_SPRITE_INVISIBLE))
			sp.cstat &= ~(CSTAT_SPRITE_INVISIBLE); // Turn em' back on

		// Sometimes they just won't die!
		if (u.Health <= 0)
			SetSuicide(SpriteNum);

		if (u.scale_speed != 0) {
			DoScaleSprite(SpriteNum);
		}

		if (TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			if (TEST(u.Flags, SPR_JUMPING))
				DoActorJump(SpriteNum);
			else
				DoActorFall(SpriteNum);
		}

		// if on a player/enemy sprite jump quickly
		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			DoBunnyQuickJump(SpriteNum);
		}

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		if (u.track >= 0)
			ActorFollowTrack(SpriteNum, ACTORMOVETICS);
		else
			(u.ActorActionFunc).invoke(SpriteNum);

		// stay on floor unless doing certain things
		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING))
			KeepActorOnFloor(SpriteNum);

		DoActorSectorDamage(SpriteNum);

		if (RANDOM_RANGE(1000) > 985 && sp.pal != PALETTE_PLAYER1 && u.track < 0) {
			switch (sector[sp.sectnum].floorpicnum) {
			case 153:
			case 154:
			case 193:
			case 219:
			case 2636:
			case 2689:
			case 3561:
			case 3562:
			case 3563:
			case 3564:
				NewStateGroup(SpriteNum, BunnyStateGroup.sg_BunnyStand);
				break;
			default:
				sp.ang = NORM_ANGLE(RANDOM_RANGE(2048 << 6) >> 6);
				u.jump_speed = -350;
				DoActorBeginJump(SpriteNum);
				u.ActorActionFunc = DoActorMoveJump;
				break;
			}
		}

		return (0);
	}

	private static int BunnySpew(int SpriteNum) {
		InitBloodSpray(SpriteNum, true, -1);
		return (0);
	}

	private static int DoBunnyEat(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			if (TEST(u.Flags, SPR_JUMPING))
				DoActorJump(SpriteNum);
			else
				DoActorFall(SpriteNum);
		}

		// if on a player/enemy sprite jump quickly
		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			DoBunnyQuickJump(SpriteNum);
		}

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		// stay on floor unless doing certain things
		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING))
			KeepActorOnFloor(SpriteNum);

		DoActorSectorDamage(SpriteNum);

		switch (sector[sp.sectnum].floorpicnum) {
		case 153:
		case 154:
		case 193:
		case 219:
		case 2636:
		case 2689:
		case 3561:
		case 3562:
		case 3563:
		case 3564:
			if (RANDOM_RANGE(1000) > 970)
				NewStateGroup(SpriteNum, BunnyStateGroup.sg_BunnyRun);
			break;
		default:
			NewStateGroup(SpriteNum, BunnyStateGroup.sg_BunnyRun);
			break;
		}
		return (0);
	}

	private static int DoBunnyScrew(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			if (TEST(u.Flags, SPR_JUMPING))
				DoActorJump(SpriteNum);
			else
				DoActorFall(SpriteNum);
		}

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		// stay on floor unless doing certain things
		if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING))
			KeepActorOnFloor(SpriteNum);

		DoActorSectorDamage(SpriteNum);

		if (RANDOM_RANGE(1000) > 990) // Bunny sex sounds
		{
			if (!gs.ParentalLock && !Global_PLock)
				PlaySound(DIGI_BUNNYATTACK, sp, v3df_follow);
		}

		u.WaitTics -= ACTORMOVETICS;

		if ((u.FlagOwner != 0 || u.spal == PALETTE_PLAYER0) && u.WaitTics > 0) // Keep Girl still
			NewStateGroup(SpriteNum, BunnyStateGroup.sg_BunnyScrew);

		if (u.spal == PALETTE_PLAYER0 && u.WaitTics <= 0) // Female has baby
		{
			u.Flag1 = SEC(5); // Count down to babies
			u.ShellNum = 1; // She's pregnant now
		}

		if (u.WaitTics <= 0) {
			sp.cstat &= ~(CSTAT_SPRITE_INVISIBLE); // Turn em' back on
			u.FlagOwner = 0;
			NewStateGroup(SpriteNum, BunnyStateGroup.sg_BunnyRun);
		}

		return (0);
	}

	private static int DoBunnyGrowUp(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if (sp.pal == PALETTE_PLAYER1)
			return (0); // Don't bother white bunnies

		if ((u.Counter -= ACTORMOVETICS) <= 0) {
			if ((++sp.xrepeat) > 64)
				sp.xrepeat = 64;
			if ((++sp.yrepeat) > 64)
				sp.yrepeat = 64;
			u.Counter = 60;
		}

		// Don't go homo too much!
		if (sp.pal != PALETTE_PLAYER0 && u.Flag1 > 0)
			u.Flag1 -= ACTORMOVETICS;

		// Gestation period for female rabbits
		if (sp.pal == PALETTE_PLAYER0 && u.ShellNum > 0) {
			if ((u.Flag1 -= ACTORMOVETICS) <= 0) {
				if (Bunny_Count < 20) {
					PlaySound(DIGI_BUNNYDIE2, sp, v3df_follow);
					BunnyHatch(SpriteNum); // Baby time
				}
				u.ShellNum = 0; // Not pregnent anymore
			}
		}

		return (0);
	}

	public static void BunnySaveable() {
		SaveData(InitBunnySlash);
		SaveData(DoActorDeathMove);
		SaveData(DoBunnyBeginJumpAttack);
		SaveData(DoBunnyMoveJump);
		SaveData(NullBunny);
		SaveData(DoBunnyPain);
		SaveData(DoBunnyStandKill);
		SaveData(DoBunnyMove);
		SaveData(BunnySpew);
		SaveData(DoBunnyEat);
		SaveData(DoBunnyScrew);
		SaveData(DoBunnyGrowUp);
		SaveData(WhiteBunnyPersonality);
		SaveData(BunnyPersonality);
		SaveData(WhiteBunnyAttrib);
		SaveData(BunnyAttrib);
		SaveData(s_BunnyRun);
		SaveGroup(BunnyStateGroup.sg_BunnyRun);
		SaveData(s_BunnyStand);
		SaveGroup(BunnyStateGroup.sg_BunnyStand);
		SaveData(s_BunnyScrew);
		SaveGroup(BunnyStateGroup.sg_BunnyScrew);
		SaveData(s_BunnySwipe);
		SaveGroup(BunnyStateGroup.sg_BunnySwipe);
		SaveData(s_BunnyHeart);
		SaveGroup(BunnyStateGroup.sg_BunnyHeart);
		SaveData(s_BunnyPain);
		SaveGroup(BunnyStateGroup.sg_BunnyPain);
		SaveData(s_BunnyJump);
		SaveGroup(BunnyStateGroup.sg_BunnyJump);
		SaveData(s_BunnyFall);
		SaveGroup(BunnyStateGroup.sg_BunnyFall);
		SaveData(s_BunnyDie);
		SaveGroup(BunnyStateGroup.sg_BunnyDie);
		SaveData(s_BunnyDead);
		SaveGroup(BunnyStateGroup.sg_BunnyDead);
		SaveData(s_BunnyDeathJump);
		SaveGroup(BunnyStateGroup.sg_BunnyDeathJump);
		SaveData(s_BunnyDeathFall);
		SaveGroup(BunnyStateGroup.sg_BunnyDeathFall);
		SaveData(BunnyActionSet);
		SaveData(BunnyWhiteActionSet);
	}
}
