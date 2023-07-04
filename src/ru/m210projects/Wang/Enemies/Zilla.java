package ru.m210projects.Wang.Enemies;

import static ru.m210projects.Build.Engine.CEIL;
import static ru.m210projects.Build.Engine.FLOOR;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Wang.Actor.DoActorDebris;
import static ru.m210projects.Wang.Actor.DoActorSectorDamage;
import static ru.m210projects.Wang.Actor.KeepActorOnFloor;
import static ru.m210projects.Wang.Ai.DoActorSetSpeed;
import static ru.m210projects.Wang.Ai.InitActorAlertNoise;
import static ru.m210projects.Wang.Ai.InitActorAmbientNoise;
import static ru.m210projects.Wang.Ai.InitActorAttack;
import static ru.m210projects.Wang.Ai.InitActorDecide;
import static ru.m210projects.Wang.Ai.InitActorFindPlayer;
import static ru.m210projects.Wang.Ai.InitActorMoveCloser;
import static ru.m210projects.Wang.Ai.InitActorRunAway;
import static ru.m210projects.Wang.Ai.InitActorWanderAround;
import static ru.m210projects.Wang.Ai.NORM_SPEED;
import static ru.m210projects.Wang.Digi.DIGI_Z16004;
import static ru.m210projects.Wang.Digi.DIGI_Z17010;
import static ru.m210projects.Wang.Digi.DIGI_Z17025;
import static ru.m210projects.Wang.Digi.DIGI_Z17052;
import static ru.m210projects.Wang.Digi.DIGI_ZILLASTOMP;
import static ru.m210projects.Wang.Enemies.Coolie.EnemyDefaults;
import static ru.m210projects.Wang.Enemies.Ninja.InitEnemyUzi;
import static ru.m210projects.Wang.Enemies.Sumo.BossSpriteNum;
import static ru.m210projects.Wang.Game.Level;
import static ru.m210projects.Wang.Game.Skill;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_RESTORE;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.SF_QUICK_CALL;
import static ru.m210projects.Wang.Gameutils.SPR_FALLING;
import static ru.m210projects.Wang.Gameutils.SPR_JUMPING;
import static ru.m210projects.Wang.Gameutils.SPR_MOVED;
import static ru.m210projects.Wang.Gameutils.STD_RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Names.ZILLA_DEAD;
import static ru.m210projects.Wang.Names.ZILLA_DIE;
import static ru.m210projects.Wang.Names.ZILLA_PAIN_R0;
import static ru.m210projects.Wang.Names.ZILLA_PAIN_R1;
import static ru.m210projects.Wang.Names.ZILLA_PAIN_R2;
import static ru.m210projects.Wang.Names.ZILLA_PAIN_R3;
import static ru.m210projects.Wang.Names.ZILLA_PAIN_R4;
import static ru.m210projects.Wang.Names.ZILLA_RAIL_R0;
import static ru.m210projects.Wang.Names.ZILLA_RAIL_R1;
import static ru.m210projects.Wang.Names.ZILLA_RAIL_R2;
import static ru.m210projects.Wang.Names.ZILLA_RAIL_R3;
import static ru.m210projects.Wang.Names.ZILLA_RAIL_R4;
import static ru.m210projects.Wang.Names.ZILLA_ROCKET_R0;
import static ru.m210projects.Wang.Names.ZILLA_ROCKET_R1;
import static ru.m210projects.Wang.Names.ZILLA_ROCKET_R2;
import static ru.m210projects.Wang.Names.ZILLA_ROCKET_R3;
import static ru.m210projects.Wang.Names.ZILLA_ROCKET_R4;
import static ru.m210projects.Wang.Names.ZILLA_RUN_R0;
import static ru.m210projects.Wang.Names.ZILLA_RUN_R1;
import static ru.m210projects.Wang.Names.ZILLA_RUN_R2;
import static ru.m210projects.Wang.Names.ZILLA_RUN_R3;
import static ru.m210projects.Wang.Names.ZILLA_RUN_R4;
import static ru.m210projects.Wang.Names.ZILLA_SHOOT_R0;
import static ru.m210projects.Wang.Names.ZILLA_SHOOT_R1;
import static ru.m210projects.Wang.Names.ZILLA_SHOOT_R2;
import static ru.m210projects.Wang.Names.ZILLA_SHOOT_R3;
import static ru.m210projects.Wang.Names.ZILLA_SHOOT_R4;
import static ru.m210projects.Wang.Rooms.zofslope;
import static ru.m210projects.Wang.Sound.CDAudio_Play;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.RedBookSong;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sound.v3df_none;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Track.ActorFollowTrack;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.*;
import static ru.m210projects.Wang.Weapon.InitZillaRail;
import static ru.m210projects.Wang.Weapon.InitZillaRocket;
import static ru.m210projects.Wang.Weapon.SpawnGrenadeExp;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Type.ATTRIBUTE;
import ru.m210projects.Wang.Type.Actor_Action_Set;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;
import ru.m210projects.Wang.Type.VOC3D;

public class Zilla {

	private static final Decision ZillaBattle[] = { new Decision(100, InitActorRunAway),
			new Decision(690, InitActorMoveCloser), new Decision(692, InitActorAlertNoise),
			new Decision(1024, InitActorAttack) };

	private static final Decision ZillaOffense[] = { new Decision(100, InitActorRunAway),
			new Decision(690, InitActorMoveCloser), new Decision(692, InitActorAlertNoise),
			new Decision(1024, InitActorAttack) };

	private static final Decision ZillaBroadcast[] = { new Decision(2, InitActorAlertNoise),
			new Decision(4, InitActorAmbientNoise), new Decision(1024, InitActorDecide) };

	private static final Decision ZillaSurprised[] = { new Decision(700, InitActorMoveCloser),
			new Decision(703, InitActorAlertNoise), new Decision(1024, InitActorDecide) };

	private static final Decision ZillaEvasive[] = { new Decision(1024, InitActorWanderAround) };

	private static final Decision ZillaLostTarget[] = { new Decision(900, InitActorFindPlayer),
			new Decision(1024, InitActorWanderAround) };

	private static final Decision ZillaCloseRange[] = { new Decision(1024, InitActorAttack) };

	private static final Personality ZillaPersonality = new Personality(ZillaBattle, ZillaOffense, ZillaBroadcast,
			ZillaSurprised, ZillaEvasive, ZillaLostTarget, ZillaCloseRange, ZillaCloseRange);

	private static final ATTRIBUTE ZillaAttrib = new ATTRIBUTE(new short[] { 100, 100, 100, 100 }, // Speeds
			new short[] { 3, 0, 0, 0 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { DIGI_Z17010, DIGI_Z17010, DIGI_Z17025, DIGI_Z17052, DIGI_Z17025, 0, 0, 0, 0, 0 });

	//////////////////////
	//
	// ZILLA RUN
	//
	//////////////////////

	public static final int ZILLA_RATE = 48;

	private static final Animator DoZillaMove = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoZillaMove(SpriteNum) != 0;
		}
	};

	private static final Animator DoZillaStomp = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoZillaStomp(SpriteNum) != 0;
		}
	};

	private static final State s_ZillaRun[][] = { { new State(ZILLA_RUN_R0 + 0, ZILLA_RATE, DoZillaMove), // s_ZillaRun[0][1]},
			new State(ZILLA_RUN_R0 + 1, SF_QUICK_CALL, DoZillaStomp), // s_ZillaRun[0][2]},
			new State(ZILLA_RUN_R0 + 1, ZILLA_RATE, DoZillaMove), // s_ZillaRun[0][3]},
			new State(ZILLA_RUN_R0 + 2, ZILLA_RATE, DoZillaMove), // s_ZillaRun[0][4]},
			new State(ZILLA_RUN_R0 + 3, SF_QUICK_CALL, DoZillaStomp), // s_ZillaRun[0][5]},
			new State(ZILLA_RUN_R0 + 3, ZILLA_RATE, DoZillaMove),// s_ZillaRun[0][0]}
			}, { new State(ZILLA_RUN_R1 + 0, ZILLA_RATE, DoZillaMove), // s_ZillaRun[1][1]},
					new State(ZILLA_RUN_R1 + 1, SF_QUICK_CALL, DoZillaStomp), // s_ZillaRun[1][2]},
					new State(ZILLA_RUN_R1 + 1, ZILLA_RATE, DoZillaMove), // s_ZillaRun[1][3]},
					new State(ZILLA_RUN_R1 + 2, ZILLA_RATE, DoZillaMove), // s_ZillaRun[1][4]},
					new State(ZILLA_RUN_R1 + 3, SF_QUICK_CALL, DoZillaStomp), // s_ZillaRun[1][5]},
					new State(ZILLA_RUN_R1 + 3, ZILLA_RATE, DoZillaMove),// s_ZillaRun[1][0]}
			}, { new State(ZILLA_RUN_R2 + 0, ZILLA_RATE, DoZillaMove), // s_ZillaRun[2][1]},
					new State(ZILLA_RUN_R2 + 1, SF_QUICK_CALL, DoZillaStomp), // s_ZillaRun[2][2]},
					new State(ZILLA_RUN_R2 + 1, ZILLA_RATE, DoZillaMove), // s_ZillaRun[2][3]},
					new State(ZILLA_RUN_R2 + 2, ZILLA_RATE, DoZillaMove), // s_ZillaRun[2][4]},
					new State(ZILLA_RUN_R2 + 3, SF_QUICK_CALL, DoZillaStomp), // s_ZillaRun[2][5]},
					new State(ZILLA_RUN_R2 + 3, ZILLA_RATE, DoZillaMove),// s_ZillaRun[2][0]}
			}, { new State(ZILLA_RUN_R3 + 0, ZILLA_RATE, DoZillaMove), // s_ZillaRun[3][1]},
					new State(ZILLA_RUN_R3 + 1, SF_QUICK_CALL, DoZillaStomp), // s_ZillaRun[3][2]},
					new State(ZILLA_RUN_R3 + 1, ZILLA_RATE, DoZillaMove), // s_ZillaRun[3][3]},
					new State(ZILLA_RUN_R3 + 2, ZILLA_RATE, DoZillaMove), // s_ZillaRun[3][4]},
					new State(ZILLA_RUN_R3 + 3, SF_QUICK_CALL, DoZillaStomp), // s_ZillaRun[3][5]},
					new State(ZILLA_RUN_R3 + 3, ZILLA_RATE, DoZillaMove),// s_ZillaRun[3][0]}
			}, { new State(ZILLA_RUN_R4 + 0, ZILLA_RATE, DoZillaMove), // s_ZillaRun[4][1]},
					new State(ZILLA_RUN_R4 + 1, SF_QUICK_CALL, DoZillaStomp), // s_ZillaRun[4][2]},
					new State(ZILLA_RUN_R4 + 1, ZILLA_RATE, DoZillaMove), // s_ZillaRun[4][3]},
					new State(ZILLA_RUN_R4 + 2, ZILLA_RATE, DoZillaMove), // s_ZillaRun[4][4]},
					new State(ZILLA_RUN_R4 + 3, SF_QUICK_CALL, DoZillaStomp), // s_ZillaRun[4][5]},
					new State(ZILLA_RUN_R4 + 3, ZILLA_RATE, DoZillaMove),// s_ZillaRun[4][0]}
			} };

	//////////////////////
	//
	// ZILLA STAND
	//
	//////////////////////

	private static final State s_ZillaStand[][] = { { new State(ZILLA_RUN_R0 + 0, ZILLA_RATE, DoZillaMove).setNext(),// s_ZillaStand[0][0]}
			}, { new State(ZILLA_RUN_R1 + 0, ZILLA_RATE, DoZillaMove).setNext(),// s_ZillaStand[1][0]}
			}, { new State(ZILLA_RUN_R2 + 0, ZILLA_RATE, DoZillaMove).setNext(),// s_ZillaStand[2][0]}
			}, { new State(ZILLA_RUN_R3 + 0, ZILLA_RATE, DoZillaMove).setNext(),// s_ZillaStand[3][0]}
			}, { new State(ZILLA_RUN_R4 + 0, ZILLA_RATE, DoZillaMove).setNext(),// s_ZillaStand[4][0]}
			} };

	//////////////////////
	//
	// ZILLA PAIN
	//
	//////////////////////

	public static final int ZILLA_PAIN_RATE = 30;

	private static final Animator NullZilla = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return NullZilla(SpriteNum) != 0;
		}
	};

	private static final State s_ZillaPain[][] = { { new State(ZILLA_PAIN_R0 + 0, ZILLA_PAIN_RATE, NullZilla), // s_ZillaPain[0][1]},
			new State(ZILLA_PAIN_R0 + 0, 0 | SF_QUICK_CALL, InitActorDecide),// s_ZillaPain[0][0]}
			}, { new State(ZILLA_PAIN_R1 + 0, ZILLA_PAIN_RATE, NullZilla), // s_ZillaPain[1][1]},
					new State(ZILLA_PAIN_R1 + 0, 0 | SF_QUICK_CALL, InitActorDecide),// s_ZillaPain[1][0]}
			}, { new State(ZILLA_PAIN_R2 + 0, ZILLA_PAIN_RATE, NullZilla), // s_ZillaPain[2][1]},
					new State(ZILLA_PAIN_R2 + 0, 0 | SF_QUICK_CALL, InitActorDecide),// s_ZillaPain[2][0]}
			}, { new State(ZILLA_PAIN_R3 + 0, ZILLA_PAIN_RATE, NullZilla), // s_ZillaPain[3][1]},
					new State(ZILLA_PAIN_R3 + 0, 0 | SF_QUICK_CALL, InitActorDecide),// s_ZillaPain[3][0]}
			}, { new State(ZILLA_PAIN_R4 + 0, ZILLA_PAIN_RATE, NullZilla), // s_ZillaPain[4][1]},
					new State(ZILLA_PAIN_R4 + 0, 0 | SF_QUICK_CALL, InitActorDecide),// s_ZillaPain[4][0]}
			} };

	//////////////////////
	//
	// ZILLA RAIL
	//
	//////////////////////

	public static final int ZILLA_RAIL_RATE = 12;

	private static final Animator InitZillaRail = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitZillaRail(SpriteNum) != 0;
		}
	};

	private static final State s_ZillaRail[][] = { { new State(ZILLA_RAIL_R0 + 0, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[0][1]},
			new State(ZILLA_RAIL_R0 + 1, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[0][2]},
			new State(ZILLA_RAIL_R0 + 2, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[0][3]},
			new State(ZILLA_RAIL_R0 + 3, SF_QUICK_CALL, InitZillaRail), // s_ZillaRail[0][4]},
			new State(ZILLA_RAIL_R0 + 0, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[0][5]},
			new State(ZILLA_RAIL_R0 + 1, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[0][6]},
			new State(ZILLA_RAIL_R0 + 2, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[0][7]},
			new State(ZILLA_RAIL_R0 + 3, SF_QUICK_CALL, InitZillaRail), // s_ZillaRail[0][8]},
			new State(ZILLA_RAIL_R0 + 0, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[0][9]},
			new State(ZILLA_RAIL_R0 + 1, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[0][10]},
			new State(ZILLA_RAIL_R0 + 2, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[0][11]},
			new State(ZILLA_RAIL_R0 + 3, SF_QUICK_CALL, InitZillaRail), // s_ZillaRail[0][12]},
			new State(ZILLA_RAIL_R0 + 3, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[0][13]},
			new State(ZILLA_RAIL_R0 + 3, SF_QUICK_CALL, InitActorDecide),// s_ZillaRail[0][0]}
			}, { new State(ZILLA_RAIL_R1 + 0, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[1][1]},
					new State(ZILLA_RAIL_R1 + 1, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[1][2]},
					new State(ZILLA_RAIL_R1 + 2, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[1][3]},
					new State(ZILLA_RAIL_R1 + 3, SF_QUICK_CALL, InitZillaRail), // s_ZillaRail[1][4]},
					new State(ZILLA_RAIL_R1 + 0, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[1][5]},
					new State(ZILLA_RAIL_R1 + 1, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[1][6]},
					new State(ZILLA_RAIL_R1 + 2, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[1][7]},
					new State(ZILLA_RAIL_R1 + 3, SF_QUICK_CALL, InitZillaRail), // s_ZillaRail[1][8]},
					new State(ZILLA_RAIL_R1 + 0, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[1][9]},
					new State(ZILLA_RAIL_R1 + 1, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[1][10]},
					new State(ZILLA_RAIL_R1 + 2, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[1][11]},
					new State(ZILLA_RAIL_R1 + 3, SF_QUICK_CALL, InitZillaRail), // s_ZillaRail[1][12]},
					new State(ZILLA_RAIL_R1 + 3, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[1][13]},
					new State(ZILLA_RAIL_R1 + 3, SF_QUICK_CALL, InitActorDecide),// s_ZillaRail[1][0]}
			}, { new State(ZILLA_RAIL_R2 + 0, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[2][1]},
					new State(ZILLA_RAIL_R2 + 1, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[2][2]},
					new State(ZILLA_RAIL_R2 + 2, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[2][3]},
					new State(ZILLA_RAIL_R2 + 3, SF_QUICK_CALL, InitZillaRail), // s_ZillaRail[2][4]},
					new State(ZILLA_RAIL_R2 + 0, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[2][5]},
					new State(ZILLA_RAIL_R2 + 1, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[2][6]},
					new State(ZILLA_RAIL_R2 + 2, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[2][7]},
					new State(ZILLA_RAIL_R2 + 3, SF_QUICK_CALL, InitZillaRail), // s_ZillaRail[2][8]},
					new State(ZILLA_RAIL_R2 + 0, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[2][9]},
					new State(ZILLA_RAIL_R2 + 1, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[2][10]},
					new State(ZILLA_RAIL_R2 + 2, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[2][11]},
					new State(ZILLA_RAIL_R2 + 3, SF_QUICK_CALL, InitZillaRail), // s_ZillaRail[2][12]},
					new State(ZILLA_RAIL_R2 + 3, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[2][13]},
					new State(ZILLA_RAIL_R2 + 3, SF_QUICK_CALL, InitActorDecide),// s_ZillaRail[2][0]}
			}, { new State(ZILLA_RAIL_R3 + 0, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[3][1]},
					new State(ZILLA_RAIL_R3 + 1, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[3][2]},
					new State(ZILLA_RAIL_R3 + 2, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[3][3]},
					new State(ZILLA_RAIL_R3 + 3, SF_QUICK_CALL, InitZillaRail), // s_ZillaRail[3][4]},
					new State(ZILLA_RAIL_R3 + 0, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[3][5]},
					new State(ZILLA_RAIL_R3 + 1, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[3][6]},
					new State(ZILLA_RAIL_R3 + 2, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[3][7]},
					new State(ZILLA_RAIL_R3 + 3, SF_QUICK_CALL, InitZillaRail), // s_ZillaRail[3][8]},
					new State(ZILLA_RAIL_R3 + 0, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[3][9]},
					new State(ZILLA_RAIL_R3 + 1, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[3][10]},
					new State(ZILLA_RAIL_R3 + 2, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[3][11]},
					new State(ZILLA_RAIL_R3 + 3, SF_QUICK_CALL, InitZillaRail), // s_ZillaRail[3][12]},
					new State(ZILLA_RAIL_R3 + 3, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[3][13]},
					new State(ZILLA_RAIL_R3 + 3, SF_QUICK_CALL, InitActorDecide),// s_ZillaRail[3][0]}
			}, { new State(ZILLA_RAIL_R4 + 0, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[4][1]},
					new State(ZILLA_RAIL_R4 + 1, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[4][2]},
					new State(ZILLA_RAIL_R4 + 2, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[4][3]},
					new State(ZILLA_RAIL_R4 + 3, SF_QUICK_CALL, InitZillaRail), // s_ZillaRail[4][4]},
					new State(ZILLA_RAIL_R4 + 0, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[4][5]},
					new State(ZILLA_RAIL_R4 + 1, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[4][6]},
					new State(ZILLA_RAIL_R4 + 2, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[4][7]},
					new State(ZILLA_RAIL_R4 + 3, SF_QUICK_CALL, InitZillaRail), // s_ZillaRail[4][8]},
					new State(ZILLA_RAIL_R4 + 0, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[4][9]},
					new State(ZILLA_RAIL_R4 + 1, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[4][10]},
					new State(ZILLA_RAIL_R4 + 2, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[4][11]},
					new State(ZILLA_RAIL_R4 + 3, SF_QUICK_CALL, InitZillaRail), // s_ZillaRail[4][12]},
					new State(ZILLA_RAIL_R4 + 3, ZILLA_RAIL_RATE, NullZilla), // s_ZillaRail[4][13]},
					new State(ZILLA_RAIL_R4 + 3, SF_QUICK_CALL, InitActorDecide),// s_ZillaRail[4][0]}
			} };

	//////////////////////
	//
	// ZILLA ROCKET
	//
	//////////////////////

	public static final int ZILLA_ROCKET_RATE = 12;

	private static final Animator InitZillaRocket = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitZillaRocket(SpriteNum) != 0;
		}
	};

	private static final State s_ZillaRocket[][] = { { new State(ZILLA_ROCKET_R0 + 0, ZILLA_ROCKET_RATE, NullZilla), // s_ZillaRocket[0][1]},
			new State(ZILLA_ROCKET_R0 + 1, ZILLA_ROCKET_RATE, NullZilla), // s_ZillaRocket[0][2]},
			new State(ZILLA_ROCKET_R0 + 2, ZILLA_ROCKET_RATE * 4, NullZilla), // s_ZillaRocket[0][3]},
			new State(ZILLA_ROCKET_R0 + 2, SF_QUICK_CALL, InitZillaRocket), // s_ZillaRocket[0][4]},
			new State(ZILLA_ROCKET_R0 + 2, ZILLA_ROCKET_RATE * 4, NullZilla), // s_ZillaRocket[0][5]},
			new State(ZILLA_ROCKET_R0 + 3, SF_QUICK_CALL, InitActorDecide), // s_ZillaRocket[0][6]},
			new State(ZILLA_ROCKET_R0 + 3, ZILLA_ROCKET_RATE * 10, NullZilla),// s_ZillaRocket[0][5]}
			}, { new State(ZILLA_ROCKET_R1 + 0, ZILLA_ROCKET_RATE, NullZilla), // s_ZillaRocket[1][1]},
					new State(ZILLA_ROCKET_R1 + 1, ZILLA_ROCKET_RATE, NullZilla), // s_ZillaRocket[1][2]},
					new State(ZILLA_ROCKET_R1 + 2, ZILLA_ROCKET_RATE * 4, NullZilla), // s_ZillaRocket[1][3]},
					new State(ZILLA_ROCKET_R1 + 2, SF_QUICK_CALL, InitZillaRocket), // s_ZillaRocket[1][4]},
					new State(ZILLA_ROCKET_R1 + 2, ZILLA_ROCKET_RATE * 4, NullZilla), // s_ZillaRocket[1][5]},
					new State(ZILLA_ROCKET_R1 + 3, SF_QUICK_CALL, InitActorDecide), // s_ZillaRocket[1][6]},
					new State(ZILLA_ROCKET_R1 + 3, ZILLA_ROCKET_RATE * 10, NullZilla),// s_ZillaRocket[1][5]}
			}, { new State(ZILLA_ROCKET_R2 + 0, ZILLA_ROCKET_RATE, NullZilla), // s_ZillaRocket[2][1]},
					new State(ZILLA_ROCKET_R2 + 1, ZILLA_ROCKET_RATE, NullZilla), // s_ZillaRocket[2][2]},
					new State(ZILLA_ROCKET_R2 + 2, ZILLA_ROCKET_RATE * 4, NullZilla), // s_ZillaRocket[2][3]},
					new State(ZILLA_ROCKET_R2 + 2, SF_QUICK_CALL, InitZillaRocket), // s_ZillaRocket[2][4]},
					new State(ZILLA_ROCKET_R2 + 2, ZILLA_ROCKET_RATE * 4, NullZilla), // s_ZillaRocket[2][5]},
					new State(ZILLA_ROCKET_R2 + 3, SF_QUICK_CALL, InitActorDecide), // s_ZillaRocket[2][6]},
					new State(ZILLA_ROCKET_R2 + 3, ZILLA_ROCKET_RATE * 10, NullZilla),// s_ZillaRocket[2][5]}
			}, { new State(ZILLA_ROCKET_R3 + 0, ZILLA_ROCKET_RATE, NullZilla), // s_ZillaRocket[3][1]},
					new State(ZILLA_ROCKET_R3 + 1, ZILLA_ROCKET_RATE, NullZilla), // s_ZillaRocket[3][2]},
					new State(ZILLA_ROCKET_R3 + 2, ZILLA_ROCKET_RATE * 4, NullZilla), // s_ZillaRocket[3][3]},
					new State(ZILLA_ROCKET_R3 + 2, SF_QUICK_CALL, InitZillaRocket), // s_ZillaRocket[3][4]},
					new State(ZILLA_ROCKET_R3 + 2, ZILLA_ROCKET_RATE * 4, NullZilla), // s_ZillaRocket[3][5]},
					new State(ZILLA_ROCKET_R3 + 3, SF_QUICK_CALL, InitActorDecide), // s_ZillaRocket[3][6]},
					new State(ZILLA_ROCKET_R3 + 3, ZILLA_ROCKET_RATE * 10, NullZilla),// s_ZillaRocket[3][5]}
			}, { new State(ZILLA_ROCKET_R4 + 0, ZILLA_ROCKET_RATE, NullZilla), // s_ZillaRocket[4][1]},
					new State(ZILLA_ROCKET_R4 + 1, ZILLA_ROCKET_RATE, NullZilla), // s_ZillaRocket[4][2]},
					new State(ZILLA_ROCKET_R4 + 2, ZILLA_ROCKET_RATE * 4, NullZilla), // s_ZillaRocket[4][3]},
					new State(ZILLA_ROCKET_R4 + 2, SF_QUICK_CALL, InitZillaRocket), // s_ZillaRocket[4][4]},
					new State(ZILLA_ROCKET_R4 + 2, ZILLA_ROCKET_RATE * 4, NullZilla), // s_ZillaRocket[4][5]},
					new State(ZILLA_ROCKET_R4 + 3, SF_QUICK_CALL, InitActorDecide), // s_ZillaRocket[4][6]},
					new State(ZILLA_ROCKET_R4 + 3, ZILLA_ROCKET_RATE * 10, NullZilla),// s_ZillaRocket[4][5]}
			} };

	//////////////////////
	//
	// ZILLA UZI
	//
	//////////////////////

	public static final int ZILLA_UZI_RATE = 8;

	private static final State s_ZillaUzi[][] = { { new State(ZILLA_SHOOT_R0 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[0][1]},
			new State(ZILLA_SHOOT_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[0][2]},
			new State(ZILLA_SHOOT_R0 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[0][3]},
			new State(ZILLA_SHOOT_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[0][4]},
			new State(ZILLA_SHOOT_R0 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[0][5]},
			new State(ZILLA_SHOOT_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[0][6]},
			new State(ZILLA_SHOOT_R0 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[0][7]},
			new State(ZILLA_SHOOT_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[0][8]},
			new State(ZILLA_SHOOT_R0 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[0][9]},
			new State(ZILLA_SHOOT_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[0][10]},
			new State(ZILLA_SHOOT_R0 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[0][11]},
			new State(ZILLA_SHOOT_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[0][12]},
			new State(ZILLA_SHOOT_R0 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[0][13]},
			new State(ZILLA_SHOOT_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[0][14]},
			new State(ZILLA_SHOOT_R0 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[0][15]},
			new State(ZILLA_SHOOT_R0 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[0][16]},
			new State(ZILLA_SHOOT_R0 + 0, 0 | SF_QUICK_CALL, InitActorDecide).setNext(),// s_ZillaUzi[0][16]},
			}, { new State(ZILLA_SHOOT_R1 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[1][1]},
					new State(ZILLA_SHOOT_R1 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[1][2]},
					new State(ZILLA_SHOOT_R1 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[1][3]},
					new State(ZILLA_SHOOT_R1 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[1][4]},
					new State(ZILLA_SHOOT_R1 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[1][5]},
					new State(ZILLA_SHOOT_R1 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[1][6]},
					new State(ZILLA_SHOOT_R1 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[1][7]},
					new State(ZILLA_SHOOT_R1 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[1][8]},
					new State(ZILLA_SHOOT_R1 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[1][9]},
					new State(ZILLA_SHOOT_R1 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[1][10]},
					new State(ZILLA_SHOOT_R1 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[1][11]},
					new State(ZILLA_SHOOT_R1 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[1][12]},
					new State(ZILLA_SHOOT_R1 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[1][13]},
					new State(ZILLA_SHOOT_R1 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[1][14]},
					new State(ZILLA_SHOOT_R1 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[1][15]},
					new State(ZILLA_SHOOT_R1 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[1][16]},
					new State(ZILLA_SHOOT_R1 + 0, 0 | SF_QUICK_CALL, InitActorDecide).setNext(),// s_ZillaUzi[1][16]},
			}, { new State(ZILLA_SHOOT_R2 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[2][1]},
					new State(ZILLA_SHOOT_R2 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[2][2]},
					new State(ZILLA_SHOOT_R2 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[2][3]},
					new State(ZILLA_SHOOT_R2 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[2][4]},
					new State(ZILLA_SHOOT_R2 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[2][5]},
					new State(ZILLA_SHOOT_R2 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[2][6]},
					new State(ZILLA_SHOOT_R2 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[2][7]},
					new State(ZILLA_SHOOT_R2 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[2][8]},
					new State(ZILLA_SHOOT_R2 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[2][9]},
					new State(ZILLA_SHOOT_R2 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[2][10]},
					new State(ZILLA_SHOOT_R2 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[2][11]},
					new State(ZILLA_SHOOT_R2 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[2][12]},
					new State(ZILLA_SHOOT_R2 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[2][13]},
					new State(ZILLA_SHOOT_R2 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[2][14]},
					new State(ZILLA_SHOOT_R2 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[2][15]},
					new State(ZILLA_SHOOT_R2 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[2][16]},
					new State(ZILLA_SHOOT_R2 + 0, 0 | SF_QUICK_CALL, InitActorDecide).setNext(),// s_ZillaUzi[2][16]},
			}, { new State(ZILLA_SHOOT_R3 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[3][1]},
					new State(ZILLA_SHOOT_R3 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[3][2]},
					new State(ZILLA_SHOOT_R3 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[3][3]},
					new State(ZILLA_SHOOT_R3 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[3][4]},
					new State(ZILLA_SHOOT_R3 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[3][5]},
					new State(ZILLA_SHOOT_R3 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[3][6]},
					new State(ZILLA_SHOOT_R3 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[3][7]},
					new State(ZILLA_SHOOT_R3 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[3][8]},
					new State(ZILLA_SHOOT_R3 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[3][9]},
					new State(ZILLA_SHOOT_R3 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[3][10]},
					new State(ZILLA_SHOOT_R3 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[3][11]},
					new State(ZILLA_SHOOT_R3 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[3][12]},
					new State(ZILLA_SHOOT_R3 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[3][13]},
					new State(ZILLA_SHOOT_R3 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[3][14]},
					new State(ZILLA_SHOOT_R3 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[3][15]},
					new State(ZILLA_SHOOT_R3 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[3][16]},
					new State(ZILLA_SHOOT_R3 + 0, 0 | SF_QUICK_CALL, InitActorDecide).setNext(),// s_ZillaUzi[3][16]},
			}, { new State(ZILLA_SHOOT_R4 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[4][1]},
					new State(ZILLA_SHOOT_R4 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[4][2]},
					new State(ZILLA_SHOOT_R4 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[4][3]},
					new State(ZILLA_SHOOT_R4 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[4][4]},
					new State(ZILLA_SHOOT_R4 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[4][5]},
					new State(ZILLA_SHOOT_R4 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[4][6]},
					new State(ZILLA_SHOOT_R4 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[4][7]},
					new State(ZILLA_SHOOT_R4 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[4][8]},
					new State(ZILLA_SHOOT_R4 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[4][9]},
					new State(ZILLA_SHOOT_R4 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[4][10]},
					new State(ZILLA_SHOOT_R4 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[4][11]},
					new State(ZILLA_SHOOT_R4 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[4][12]},
					new State(ZILLA_SHOOT_R4 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[4][13]},
					new State(ZILLA_SHOOT_R4 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[4][14]},
					new State(ZILLA_SHOOT_R4 + 0, ZILLA_UZI_RATE, NullZilla), // s_ZillaUzi[4][15]},
					new State(ZILLA_SHOOT_R4 + 0, 0 | SF_QUICK_CALL, InitEnemyUzi), // s_ZillaUzi[4][16]},
					new State(ZILLA_SHOOT_R4 + 0, 0 | SF_QUICK_CALL, InitActorDecide).setNext(),// s_ZillaUzi[4][16]},
			}, };

	//////////////////////
	//
	// ZILLA DIE
	//
	//////////////////////

	public static final int ZILLA_DIE_RATE = 30;

	private static final Animator DoZillaDeathMelt = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoZillaDeathMelt(SpriteNum) != 0;
		}
	};

	private static final State s_ZillaDie[] = { new State(ZILLA_DIE + 0, ZILLA_DIE_RATE * 15, DoZillaDeathMelt), // s_ZillaDie[1]},
			new State(ZILLA_DIE + 1, ZILLA_DIE_RATE, NullZilla), // s_ZillaDie[2]},
			new State(ZILLA_DIE + 2, ZILLA_DIE_RATE, NullZilla), // s_ZillaDie[3]},
			new State(ZILLA_DIE + 3, ZILLA_DIE_RATE, NullZilla), // s_ZillaDie[4]},
			new State(ZILLA_DIE + 4, ZILLA_DIE_RATE, NullZilla), // s_ZillaDie[5]},
			new State(ZILLA_DIE + 5, ZILLA_DIE_RATE, NullZilla), // s_ZillaDie[6]},
			new State(ZILLA_DIE + 6, ZILLA_DIE_RATE, NullZilla), // s_ZillaDie[7]},
			new State(ZILLA_DIE + 7, ZILLA_DIE_RATE * 3, NullZilla), // s_ZillaDie[8]},
			new State(ZILLA_DEAD, ZILLA_DIE_RATE, DoActorDebris).setNext(),// s_ZillaDie[8]}
	};

	private static final State s_ZillaDead[] = { new State(ZILLA_DEAD, ZILLA_DIE_RATE, DoActorDebris).setNext(),// s_ZillaDead[0]},
	};

	public enum ZillaStateGroup implements StateGroup {
		sg_ZillaStand(s_ZillaStand[0], s_ZillaStand[1], s_ZillaStand[2], s_ZillaStand[3], s_ZillaStand[4]),
		sg_ZillaRun(s_ZillaRun[0], s_ZillaRun[1], s_ZillaRun[2], s_ZillaRun[3], s_ZillaRun[4]),
		sg_ZillaPain(s_ZillaPain[0], s_ZillaPain[1], s_ZillaPain[2], s_ZillaPain[3], s_ZillaPain[4]),
		sg_ZillaDie(s_ZillaDie), sg_ZillaDead(s_ZillaDead),
		sg_ZillaRail(s_ZillaRail[0], s_ZillaRail[1], s_ZillaRail[2], s_ZillaRail[3], s_ZillaRail[4]),
		sg_ZillaUzi(s_ZillaUzi[0], s_ZillaUzi[1], s_ZillaUzi[2], s_ZillaUzi[3], s_ZillaUzi[4]),
		sg_ZillaRocket(s_ZillaRocket[0], s_ZillaRocket[1], s_ZillaRocket[2], s_ZillaRocket[3], s_ZillaRocket[4]);

		private final State[][] group;
		private int index = -1;

		ZillaStateGroup(State[]... states) {
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

	public static void InitZillaStates() {
		for (ZillaStateGroup sg : ZillaStateGroup.values()) {
			for (int rot = 0; rot < sg.group.length; rot++) {
				State.InitState(sg.group[rot]);
				
				if(sg == ZillaStateGroup.sg_ZillaRocket)
					sg.group[rot][6].setNext(sg.group[rot][5]);
			}
		}
	}

	private static final Actor_Action_Set ZillaActionSet = new Actor_Action_Set(ZillaStateGroup.sg_ZillaStand,
			ZillaStateGroup.sg_ZillaRun, null, null, null, null, null, null, null, null, null, // climb
			ZillaStateGroup.sg_ZillaPain, // pain
			ZillaStateGroup.sg_ZillaDie, null, ZillaStateGroup.sg_ZillaDead, null, null,
			new StateGroup[] { ZillaStateGroup.sg_ZillaUzi, ZillaStateGroup.sg_ZillaRail }, new short[] { 950, 1024 },
			new StateGroup[] { ZillaStateGroup.sg_ZillaUzi, ZillaStateGroup.sg_ZillaRocket,
					ZillaStateGroup.sg_ZillaRail },
			new short[] { 400, 950, 1024 }, null, null, null);

	public static int SetupZilla(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, ZILLA_RUN_R0, s_ZillaRun[0][0]);
			u.Health = 6000;
		}

		if (Skill == 0)
			u.Health = 2000;
		if (Skill == 1)
			u.Health = 4000;

		ChangeState(SpriteNum, s_ZillaRun[0][0]);
		u.Attrib = ZillaAttrib;
		DoActorSetSpeed(SpriteNum, NORM_SPEED);
		u.StateEnd = s_ZillaDie[0];
		u.Rot = ZillaStateGroup.sg_ZillaRun;

		EnemyDefaults(SpriteNum, ZillaActionSet, ZillaPersonality);

		sp.clipdist = (512) >> 2;
		sp.xrepeat = 97;
		sp.yrepeat = 79;

		// u.Flags |=( SPR_XFLIP_TOGGLE);

		return (0);
	}

	private static int NullZilla(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		engine.getzsofslope(sp.sectnum, sp.x, sp.y, zofslope);
		u.loz = zofslope[FLOOR];
		u.hiz = zofslope[CEIL];
		u.lo_sectp = sp.sectnum;
		u.hi_sectp = sp.sectnum;
		u.lo_sp = -1;
		u.hi_sp = -1;
		sp.z = u.loz;

		DoActorSectorDamage(SpriteNum);

		return (0);
	}

	private static VOC3D handle;

	private static int DoZillaMove(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		// Random Zilla taunts
		if (handle != null && !handle.isActive()) {
			int choose = STD_RANDOM_RANGE(1000);
			if (choose > 990)
				handle = PlaySound(DIGI_Z16004, sp, v3df_none);
			else if (choose > 985)
				handle = PlaySound(DIGI_Z16004, sp, v3df_none);
			else if (choose > 980)
				handle = PlaySound(DIGI_Z16004, sp, v3df_none);
			else if (choose > 975)
				handle = PlaySound(DIGI_Z16004, sp, v3df_none);
		}

		if (u.track >= 0)
			ActorFollowTrack(SpriteNum, ACTORMOVETICS);
		else
			(u.ActorActionFunc).invoke(SpriteNum);

		KeepActorOnFloor(SpriteNum);

		if (DoActorSectorDamage(SpriteNum)) {
			return (0);
		}

		return (0);
	}

	private static int DoZillaStomp(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];

		PlaySound(DIGI_ZILLASTOMP, sp, v3df_follow);

		return 0;
	}

	private static boolean alreadydid = false;

	private static int DoZillaDeathMelt(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if (RANDOM_RANGE(1000) > 800)
			SpawnGrenadeExp(SpriteNum);

		u.ID = ZILLA_RUN_R0;
		u.Flags &= ~(SPR_JUMPING | SPR_FALLING | SPR_MOVED);

		if (!gs.muteMusic && !alreadydid) {
			CDAudio_Play(RedBookSong[Level], true);
			alreadydid = true;
		}

		engine.getzsofslope(sp.sectnum, sp.x, sp.y, zofslope);
		u.loz = zofslope[FLOOR];
		u.hiz = zofslope[CEIL];

		u.lo_sectp = sp.sectnum;
		u.hi_sectp = sp.sectnum;
		u.lo_sp = -1;
		u.hi_sp = -1;
		sp.z = u.loz;

		BossSpriteNum[2] = -2;
		return (0);
	}

	public static void ZillaSaveable()
	{
		SaveData(InitZillaRail);
		SaveData(InitZillaRocket);
		
		SaveData(NullZilla);
		SaveData(DoZillaMove);
		SaveData(DoZillaStomp);
		SaveData(DoZillaDeathMelt);

		SaveData(ZillaPersonality);

		SaveData(ZillaAttrib);

		SaveData(s_ZillaRun);
		SaveGroup(ZillaStateGroup.sg_ZillaRun);
		SaveData(s_ZillaStand);
		SaveGroup(ZillaStateGroup.sg_ZillaStand);
		SaveData(s_ZillaPain);
		SaveGroup(ZillaStateGroup.sg_ZillaPain);
		SaveData(s_ZillaRail);
		SaveGroup(ZillaStateGroup.sg_ZillaRail);
		SaveData(s_ZillaRocket);
		SaveGroup(ZillaStateGroup.sg_ZillaRocket);
		SaveData(s_ZillaUzi);
		SaveGroup(ZillaStateGroup.sg_ZillaUzi);
		SaveData(s_ZillaDie);
		SaveGroup(ZillaStateGroup.sg_ZillaDie);
		SaveData(s_ZillaDead);
		SaveGroup(ZillaStateGroup.sg_ZillaDead);

		SaveData(ZillaActionSet);
	}
}
