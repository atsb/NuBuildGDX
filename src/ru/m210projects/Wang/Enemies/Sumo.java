package ru.m210projects.Wang.Enemies;

import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
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
import static ru.m210projects.Wang.Ai.InitActorWanderAround;
import static ru.m210projects.Wang.Ai.NORM_SPEED;
import static ru.m210projects.Wang.Digi.DIGI_SUMOALERT;
import static ru.m210projects.Wang.Digi.DIGI_SUMOAMBIENT;
import static ru.m210projects.Wang.Digi.DIGI_SUMOFART;
import static ru.m210projects.Wang.Digi.DIGI_SUMOPAIN;
import static ru.m210projects.Wang.Digi.DIGI_SUMOSCREAM;
import static ru.m210projects.Wang.Digi.DIGI_SUMOSTOMP;
import static ru.m210projects.Wang.Draw.SetRedrawScreen;
import static ru.m210projects.Wang.Enemies.Coolie.EnemyDefaults;
import static ru.m210projects.Wang.Factory.WangNetwork.CommPlayers;
import static ru.m210projects.Wang.Game.Level;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.Skill;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_RESTORE;
import static ru.m210projects.Wang.Gameutils.HEALTH_SERP_GOD;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_SCREEN_CLIP;
import static ru.m210projects.Wang.Gameutils.SF_QUICK_CALL;
import static ru.m210projects.Wang.Gameutils.SPRITEp_TOS;
import static ru.m210projects.Wang.Gameutils.SPR_CLIMBING;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.JWeapon.InitChemBomb;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Names.SERP_RUN_R0;
import static ru.m210projects.Wang.Names.STAT_ENEMY;
import static ru.m210projects.Wang.Names.SUMO_CLAP_R0;
import static ru.m210projects.Wang.Names.SUMO_CLAP_R1;
import static ru.m210projects.Wang.Names.SUMO_CLAP_R2;
import static ru.m210projects.Wang.Names.SUMO_CLAP_R3;
import static ru.m210projects.Wang.Names.SUMO_CLAP_R4;
import static ru.m210projects.Wang.Names.SUMO_DEAD;
import static ru.m210projects.Wang.Names.SUMO_DIE;
import static ru.m210projects.Wang.Names.SUMO_FART_R0;
import static ru.m210projects.Wang.Names.SUMO_FART_R1;
import static ru.m210projects.Wang.Names.SUMO_FART_R2;
import static ru.m210projects.Wang.Names.SUMO_FART_R3;
import static ru.m210projects.Wang.Names.SUMO_FART_R4;
import static ru.m210projects.Wang.Names.SUMO_PAIN_R0;
import static ru.m210projects.Wang.Names.SUMO_PAIN_R1;
import static ru.m210projects.Wang.Names.SUMO_PAIN_R2;
import static ru.m210projects.Wang.Names.SUMO_PAIN_R3;
import static ru.m210projects.Wang.Names.SUMO_PAIN_R4;
import static ru.m210projects.Wang.Names.SUMO_RUN_R0;
import static ru.m210projects.Wang.Names.SUMO_RUN_R1;
import static ru.m210projects.Wang.Names.SUMO_RUN_R2;
import static ru.m210projects.Wang.Names.SUMO_RUN_R3;
import static ru.m210projects.Wang.Names.SUMO_RUN_R4;
import static ru.m210projects.Wang.Names.SUMO_STOMP_R0;
import static ru.m210projects.Wang.Names.SUMO_STOMP_R1;
import static ru.m210projects.Wang.Names.SUMO_STOMP_R2;
import static ru.m210projects.Wang.Names.SUMO_STOMP_R3;
import static ru.m210projects.Wang.Names.SUMO_STOMP_R4;
import static ru.m210projects.Wang.Names.ZILLA_RUN_R0;
import static ru.m210projects.Wang.Player.QueueFloorBlood;
import static ru.m210projects.Wang.Quake.SetSumoFartQuake;
import static ru.m210projects.Wang.Quake.SetSumoQuake;
import static ru.m210projects.Wang.Sector.DoMatchEverything;
import static ru.m210projects.Wang.Sound.CDAudio_Play;
import static ru.m210projects.Wang.Sound.CDAudio_Playing;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.RedBookSong;
import static ru.m210projects.Wang.Sound.playTrack;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sound.v3df_none;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Track.ActorFollowTrack;
import static ru.m210projects.Wang.Type.MyTypes.ON;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Type.Saveable.SaveGroup;
import static ru.m210projects.Wang.Weapon.InitMiniSumoClap;
import static ru.m210projects.Wang.Weapon.InitSumoNapalm;
import static ru.m210projects.Wang.Weapon.InitSumoSkull;
import static ru.m210projects.Wang.Weapon.InitSumoStompAttack;

import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Type.ATTRIBUTE;
import ru.m210projects.Wang.Type.Actor_Action_Set;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;

public class Sumo {

	public static boolean serpwasseen = false;
	public static boolean sumowasseen = false;
	public static boolean zillawasseen = false;
	public static boolean triedplay = false;

	public static short BossSpriteNum[] = { -1, -1, -1 };

	private static final Decision SumoBattle[] = { new Decision(690, InitActorMoveCloser),
			new Decision(692, InitActorAlertNoise), new Decision(1024, InitActorAttack) };

	private static final Decision SumoOffense[] = { new Decision(690, InitActorMoveCloser),
			new Decision(692, InitActorAlertNoise), new Decision(1024, InitActorAttack) };

	private static final Decision SumoBroadcast[] = { new Decision(2, InitActorAlertNoise),
			new Decision(4, InitActorAmbientNoise), new Decision(1024, InitActorDecide) };

	private static final Decision SumoSurprised[] = { new Decision(700, InitActorMoveCloser),
			new Decision(703, InitActorAlertNoise), new Decision(1024, InitActorDecide) };

	private static final Decision SumoEvasive[] = { new Decision(1024, InitActorAttack) };

	private static final Decision SumoLostTarget[] = { new Decision(900, InitActorFindPlayer),
			new Decision(1024, InitActorWanderAround) };

	private static final Decision SumoCloseRange[] = { new Decision(1024, InitActorAttack) };

	private static final Personality SumoPersonality = new Personality(SumoBattle, SumoOffense, SumoBroadcast,
			SumoSurprised, SumoEvasive, SumoLostTarget, SumoCloseRange, SumoCloseRange);

	private static final ATTRIBUTE SumoAttrib = new ATTRIBUTE(new short[] { 160, 180, 180, 180 }, // Speeds
			new short[] { 3, 0, 0, 0 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { DIGI_SUMOAMBIENT, DIGI_SUMOALERT, DIGI_SUMOSCREAM, DIGI_SUMOPAIN, DIGI_SUMOSCREAM, 0, 0, 0, 0,
					0 });

	//////////////////////
	//
	// SUMO RUN
	//
	//////////////////////

	public static final int SUMO_RATE = 24;

	private static final Animator DoSumoMove = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoSumoMove(SpriteNum) != 0;
		}
	};

	private static final State s_SumoRun[][] = { { new State(SUMO_RUN_R0 + 0, SUMO_RATE, DoSumoMove), // s_SumoRun[0][1]},
			new State(SUMO_RUN_R0 + 1, SUMO_RATE, DoSumoMove), // s_SumoRun[0][2]},
			new State(SUMO_RUN_R0 + 2, SUMO_RATE, DoSumoMove), // s_SumoRun[0][3]},
			new State(SUMO_RUN_R0 + 3, SUMO_RATE, DoSumoMove),// s_SumoRun[0][0]}
			}, { new State(SUMO_RUN_R1 + 0, SUMO_RATE, DoSumoMove), // s_SumoRun[1][1]},
					new State(SUMO_RUN_R1 + 1, SUMO_RATE, DoSumoMove), // s_SumoRun[1][2]},
					new State(SUMO_RUN_R1 + 2, SUMO_RATE, DoSumoMove), // s_SumoRun[1][3]},
					new State(SUMO_RUN_R1 + 3, SUMO_RATE, DoSumoMove),// s_SumoRun[1][0]}
			}, { new State(SUMO_RUN_R2 + 0, SUMO_RATE, DoSumoMove), // s_SumoRun[2][1]},
					new State(SUMO_RUN_R2 + 1, SUMO_RATE, DoSumoMove), // s_SumoRun[2][2]},
					new State(SUMO_RUN_R2 + 2, SUMO_RATE, DoSumoMove), // s_SumoRun[2][3]},
					new State(SUMO_RUN_R2 + 3, SUMO_RATE, DoSumoMove),// s_SumoRun[2][0]}
			}, { new State(SUMO_RUN_R3 + 0, SUMO_RATE, DoSumoMove), // s_SumoRun[3][1]},
					new State(SUMO_RUN_R3 + 1, SUMO_RATE, DoSumoMove), // s_SumoRun[3][2]},
					new State(SUMO_RUN_R3 + 2, SUMO_RATE, DoSumoMove), // s_SumoRun[3][3]},
					new State(SUMO_RUN_R3 + 3, SUMO_RATE, DoSumoMove),// s_SumoRun[3][0]}
			}, { new State(SUMO_RUN_R4 + 0, SUMO_RATE, DoSumoMove), // s_SumoRun[4][1]},
					new State(SUMO_RUN_R4 + 1, SUMO_RATE, DoSumoMove), // s_SumoRun[4][2]},
					new State(SUMO_RUN_R4 + 2, SUMO_RATE, DoSumoMove), // s_SumoRun[4][3]},
					new State(SUMO_RUN_R4 + 3, SUMO_RATE, DoSumoMove),// s_SumoRun[4][0]},
			} };

	//////////////////////
	//
	// SUMO STAND
	//
	//////////////////////

	private static final State s_SumoStand[][] = { { new State(SUMO_RUN_R0 + 0, SUMO_RATE, DoSumoMove).setNext(),// s_SumoStand[0][0]}
			}, { new State(SUMO_RUN_R1 + 0, SUMO_RATE, DoSumoMove).setNext(),// s_SumoStand[1][0]}
			}, { new State(SUMO_RUN_R2 + 0, SUMO_RATE, DoSumoMove).setNext(),// s_SumoStand[2][0]}
			}, { new State(SUMO_RUN_R3 + 0, SUMO_RATE, DoSumoMove).setNext(),// s_SumoStand[3][0]}
			}, { new State(SUMO_RUN_R4 + 0, SUMO_RATE, DoSumoMove).setNext(),// s_SumoStand[4][0]}
			} };

	//////////////////////
	//
	// SUMO PAIN
	//
	//////////////////////

	public static final int SUMO_PAIN_RATE = 30;

	private static final Animator NullSumo = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return NullSumo(SpriteNum) != 0;
		}
	};

	private static final State s_SumoPain[][] = { { new State(SUMO_PAIN_R0 + 0, SUMO_PAIN_RATE, NullSumo), // s_SumoPain[0][1]},
			new State(SUMO_PAIN_R0 + 0, 0 | SF_QUICK_CALL, InitActorDecide),// s_SumoPain[0][0]}
			}, { new State(SUMO_PAIN_R1 + 0, SUMO_PAIN_RATE, NullSumo), // s_SumoPain[1][1]},
					new State(SUMO_PAIN_R1 + 0, 0 | SF_QUICK_CALL, InitActorDecide),// s_SumoPain[1][0]}
			}, { new State(SUMO_PAIN_R2 + 0, SUMO_PAIN_RATE, NullSumo), // s_SumoPain[2][1]},
					new State(SUMO_PAIN_R2 + 0, 0 | SF_QUICK_CALL, InitActorDecide),// s_SumoPain[2][0]}
			}, { new State(SUMO_PAIN_R3 + 0, SUMO_PAIN_RATE, NullSumo), // s_SumoPain[3][1]},
					new State(SUMO_PAIN_R3 + 0, 0 | SF_QUICK_CALL, InitActorDecide),// s_SumoPain[3][0]}
			}, { new State(SUMO_PAIN_R4 + 0, SUMO_PAIN_RATE, NullSumo), // s_SumoPain[4][1]},
					new State(SUMO_PAIN_R4 + 0, 0 | SF_QUICK_CALL, InitActorDecide),// s_SumoPain[4][0]}
			} };

	//////////////////////
	//
	// SUMO FART
	//
	//////////////////////

	public static final int SUMO_FART_RATE = 12;

	private static final Animator InitSumoFart = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitSumoFart(SpriteNum) != 0;
		}
	};

	private static final State s_SumoFart[][] = { { new State(SUMO_FART_R0 + 0, SUMO_FART_RATE, NullSumo), // s_SumoFart[0][1]},
			new State(SUMO_FART_R0 + 0, SF_QUICK_CALL, InitSumoFart), // s_SumoFart[0][2]},
			new State(SUMO_FART_R0 + 1, SUMO_FART_RATE, NullSumo), // s_SumoFart[0][3]},
			new State(SUMO_FART_R0 + 2, SUMO_FART_RATE, NullSumo), // s_SumoFart[0][4]},
			new State(SUMO_FART_R0 + 3, SUMO_FART_RATE * 10, NullSumo), // s_SumoFart[0][5]},
			new State(SUMO_FART_R0 + 3, SF_QUICK_CALL, InitActorDecide),// s_SumoFart[0][0]}
			}, { new State(SUMO_FART_R1 + 0, SUMO_FART_RATE, NullSumo), // s_SumoFart[1][1]},
					new State(SUMO_FART_R1 + 0, SF_QUICK_CALL, InitSumoFart), // s_SumoFart[1][2]},
					new State(SUMO_FART_R1 + 1, SUMO_FART_RATE, NullSumo), // s_SumoFart[1][3]},
					new State(SUMO_FART_R1 + 2, SUMO_FART_RATE, NullSumo), // s_SumoFart[1][4]},
					new State(SUMO_FART_R1 + 3, SUMO_FART_RATE * 10, NullSumo), // s_SumoFart[1][5]},
					new State(SUMO_FART_R1 + 0, SF_QUICK_CALL, InitActorDecide),// s_SumoFart[1][0]}
			}, { new State(SUMO_FART_R2 + 0, SUMO_FART_RATE, NullSumo), // s_SumoFart[2][1]},
					new State(SUMO_FART_R2 + 0, SF_QUICK_CALL, InitSumoFart), // s_SumoFart[2][2]},
					new State(SUMO_FART_R2 + 1, SUMO_FART_RATE, NullSumo), // s_SumoFart[2][3]},
					new State(SUMO_FART_R2 + 2, SUMO_FART_RATE, NullSumo), // s_SumoFart[2][4]},
					new State(SUMO_FART_R2 + 3, SUMO_FART_RATE * 10, NullSumo), // s_SumoFart[2][5]},
					new State(SUMO_FART_R2 + 0, SF_QUICK_CALL, InitActorDecide),// s_SumoFart[2][0]}
			}, { new State(SUMO_FART_R3 + 0, SUMO_FART_RATE, NullSumo), // s_SumoFart[3][1]},
					new State(SUMO_FART_R3 + 0, SF_QUICK_CALL, InitSumoFart), // s_SumoFart[3][2]},
					new State(SUMO_FART_R3 + 1, SUMO_FART_RATE, NullSumo), // s_SumoFart[3][3]},
					new State(SUMO_FART_R3 + 2, SUMO_FART_RATE, NullSumo), // s_SumoFart[3][4]},
					new State(SUMO_FART_R3 + 3, SUMO_FART_RATE * 10, NullSumo), // s_SumoFart[3][5]},
					new State(SUMO_FART_R3 + 0, SF_QUICK_CALL, InitActorDecide),// s_SumoFart[3][0]}
			}, { new State(SUMO_FART_R4 + 0, SUMO_FART_RATE, NullSumo), // s_SumoFart[4][1]},
					new State(SUMO_FART_R4 + 0, SF_QUICK_CALL, InitSumoFart), // s_SumoFart[4][2]},
					new State(SUMO_FART_R4 + 1, SUMO_FART_RATE, NullSumo), // s_SumoFart[4][3]},
					new State(SUMO_FART_R4 + 2, SUMO_FART_RATE, NullSumo), // s_SumoFart[4][4]},
					new State(SUMO_FART_R4 + 3, SUMO_FART_RATE * 10, NullSumo), // s_SumoFart[4][5]},
					new State(SUMO_FART_R4 + 0, SF_QUICK_CALL, InitActorDecide),// s_SumoFart[4][0]}
			} };

	//////////////////////
	//
	// SUMO CLAP
	//
	//////////////////////

	public static final int SUMO_CLAP_RATE = 12;

	private static final Animator InitSumoClap = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitSumoClap(SpriteNum) != 0;
		}
	};

	private static final State s_SumoClap[][] = { { new State(SUMO_CLAP_R0 + 0, SUMO_CLAP_RATE, NullSumo), // s_SumoClap[0][1]},
			new State(SUMO_CLAP_R0 + 1, SUMO_CLAP_RATE, NullSumo), // s_SumoClap[0][2]},
			new State(SUMO_CLAP_R0 + 2, SUMO_CLAP_RATE, NullSumo), // s_SumoClap[0][3]},
			new State(SUMO_CLAP_R0 + 2, SF_QUICK_CALL, InitSumoClap), // s_SumoClap[0][4]},
			new State(SUMO_CLAP_R0 + 3, SUMO_CLAP_RATE * 10, NullSumo), // s_SumoClap[0][5]},
			new State(SUMO_CLAP_R0 + 3, SF_QUICK_CALL, InitActorDecide).setNext(),// s_SumoClap[0][5]}
			}, { new State(SUMO_CLAP_R1 + 0, SUMO_CLAP_RATE, NullSumo), // s_SumoClap[1][1]},
					new State(SUMO_CLAP_R1 + 1, SUMO_CLAP_RATE, NullSumo), // s_SumoClap[1][2]},
					new State(SUMO_CLAP_R1 + 2, SUMO_CLAP_RATE, NullSumo), // s_SumoClap[1][3]},
					new State(SUMO_CLAP_R1 + 2, SF_QUICK_CALL, InitSumoClap), // s_SumoClap[1][4]},
					new State(SUMO_CLAP_R1 + 3, SUMO_CLAP_RATE * 10, NullSumo), // s_SumoClap[1][5]},
					new State(SUMO_CLAP_R1 + 3, SF_QUICK_CALL, InitActorDecide).setNext(),// s_SumoClap[1][5]}
			}, { new State(SUMO_CLAP_R2 + 0, SUMO_CLAP_RATE, NullSumo), // s_SumoClap[2][1]},
					new State(SUMO_CLAP_R2 + 1, SUMO_CLAP_RATE, NullSumo), // s_SumoClap[2][2]},
					new State(SUMO_CLAP_R2 + 2, SUMO_CLAP_RATE, NullSumo), // s_SumoClap[2][3]},
					new State(SUMO_CLAP_R2 + 2, SF_QUICK_CALL, InitSumoClap), // s_SumoClap[2][4]},
					new State(SUMO_CLAP_R2 + 3, SUMO_CLAP_RATE * 10, NullSumo), // s_SumoClap[2][5]},
					new State(SUMO_CLAP_R2 + 3, SF_QUICK_CALL, InitActorDecide).setNext(),// s_SumoClap[2][5]}
			}, { new State(SUMO_CLAP_R3 + 0, SUMO_CLAP_RATE, NullSumo), // s_SumoClap[3][1]},
					new State(SUMO_CLAP_R3 + 1, SUMO_CLAP_RATE, NullSumo), // s_SumoClap[3][2]},
					new State(SUMO_CLAP_R3 + 2, SUMO_CLAP_RATE, NullSumo), // s_SumoClap[3][3]},
					new State(SUMO_CLAP_R3 + 2, SF_QUICK_CALL, InitSumoClap), // s_SumoClap[3][4]},
					new State(SUMO_CLAP_R3 + 3, SUMO_CLAP_RATE * 10, NullSumo), // s_SumoClap[3][5]},
					new State(SUMO_CLAP_R3 + 3, SF_QUICK_CALL, InitActorDecide).setNext(),// s_SumoClap[3][5]}
			}, { new State(SUMO_CLAP_R4 + 0, SUMO_CLAP_RATE, NullSumo), // s_SumoClap[4][1]},
					new State(SUMO_CLAP_R4 + 1, SUMO_CLAP_RATE, NullSumo), // s_SumoClap[4][2]},
					new State(SUMO_CLAP_R4 + 2, SUMO_CLAP_RATE, NullSumo), // s_SumoClap[4][3]},
					new State(SUMO_CLAP_R4 + 2, SF_QUICK_CALL, InitSumoClap), // s_SumoClap[4][4]},
					new State(SUMO_CLAP_R4 + 3, SUMO_CLAP_RATE * 10, NullSumo), // s_SumoClap[4][5]},
					new State(SUMO_CLAP_R4 + 3, SF_QUICK_CALL, InitActorDecide).setNext(),// s_SumoClap[4][5]}
			} };

	//////////////////////
	//
	// SUMO STOMP
	//
	//////////////////////

	public static final int SUMO_STOMP_RATE = 30;

	private static final Animator InitSumoStomp = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitSumoStomp(SpriteNum) != 0;
		}
	};

	private static final State s_SumoStomp[][] = { { new State(SUMO_STOMP_R0 + 0, SUMO_STOMP_RATE, NullSumo), // s_SumoStomp[0][1]},
			new State(SUMO_STOMP_R0 + 1, SUMO_STOMP_RATE * 3, NullSumo), // s_SumoStomp[0][2]},
			new State(SUMO_STOMP_R0 + 2, SUMO_STOMP_RATE, NullSumo), // s_SumoStomp[0][3]},
			new State(SUMO_STOMP_R0 + 2, 0 | SF_QUICK_CALL, InitSumoStomp), // s_SumoStomp[0][4]},
			new State(SUMO_STOMP_R0 + 2, 8, NullSumo), // s_SumoStomp[0][5]},
			new State(SUMO_STOMP_R0 + 2, 0 | SF_QUICK_CALL, InitActorDecide).setNext(),// s_SumoStomp[0][5]}
			}, { new State(SUMO_STOMP_R1 + 0, SUMO_STOMP_RATE, NullSumo), // s_SumoStomp[1][1]},
					new State(SUMO_STOMP_R1 + 1, SUMO_STOMP_RATE * 3, NullSumo), // s_SumoStomp[1][2]},
					new State(SUMO_STOMP_R1 + 2, SUMO_STOMP_RATE, NullSumo), // s_SumoStomp[1][3]},
					new State(SUMO_STOMP_R1 + 2, 0 | SF_QUICK_CALL, InitSumoStomp), // s_SumoStomp[1][4]},
					new State(SUMO_STOMP_R1 + 2, 8, NullSumo), // s_SumoStomp[1][5]},
					new State(SUMO_STOMP_R1 + 2, 0 | SF_QUICK_CALL, InitActorDecide).setNext(),// s_SumoStomp[1][5]}
			}, { new State(SUMO_STOMP_R2 + 0, SUMO_STOMP_RATE, NullSumo), // s_SumoStomp[2][1]},
					new State(SUMO_STOMP_R2 + 1, SUMO_STOMP_RATE * 3, NullSumo), // s_SumoStomp[2][2]},
					new State(SUMO_STOMP_R2 + 2, SUMO_STOMP_RATE, NullSumo), // s_SumoStomp[2][3]},
					new State(SUMO_STOMP_R2 + 2, 0 | SF_QUICK_CALL, InitSumoStomp), // s_SumoStomp[2][4]},
					new State(SUMO_STOMP_R2 + 2, 8, NullSumo), // s_SumoStomp[2][5]},
					new State(SUMO_STOMP_R2 + 2, 0 | SF_QUICK_CALL, InitActorDecide).setNext(),// s_SumoStomp[2][5]}
			}, { new State(SUMO_STOMP_R3 + 0, SUMO_STOMP_RATE, NullSumo), // s_SumoStomp[3][1]},
					new State(SUMO_STOMP_R3 + 1, SUMO_STOMP_RATE * 3, NullSumo), // s_SumoStomp[3][2]},
					new State(SUMO_STOMP_R3 + 2, SUMO_STOMP_RATE, NullSumo), // s_SumoStomp[3][3]},
					new State(SUMO_STOMP_R3 + 2, 0 | SF_QUICK_CALL, InitSumoStomp), // s_SumoStomp[3][4]},
					new State(SUMO_STOMP_R3 + 2, 8, NullSumo), // s_SumoStomp[3][5]},
					new State(SUMO_STOMP_R3 + 2, 0 | SF_QUICK_CALL, InitActorDecide).setNext(),// s_SumoStomp[3][5]}
			}, { new State(SUMO_STOMP_R4 + 0, SUMO_STOMP_RATE, NullSumo), // s_SumoStomp[4][1]},
					new State(SUMO_STOMP_R4 + 1, SUMO_STOMP_RATE * 3, NullSumo), // s_SumoStomp[4][2]},
					new State(SUMO_STOMP_R4 + 2, SUMO_STOMP_RATE, NullSumo), // s_SumoStomp[4][3]},
					new State(SUMO_STOMP_R4 + 2, 0 | SF_QUICK_CALL, InitSumoStomp), // s_SumoStomp[4][4]},
					new State(SUMO_STOMP_R4 + 2, 8, NullSumo), // s_SumoStomp[4][5]},
					new State(SUMO_STOMP_R4 + 2, 0 | SF_QUICK_CALL, InitActorDecide).setNext(),// s_SumoStomp[4][5]}
			} };

	//////////////////////
	//
	// SUMO DIE
	//
	//////////////////////

	public static final int SUMO_DIE_RATE = 30;

	private static final Animator DoSumoDeathMelt = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoSumoDeathMelt(SpriteNum) != 0;
		}
	};

	private static final State s_SumoDie[] = { new State(SUMO_DIE + 0, SUMO_DIE_RATE * 2, NullSumo), // s_SumoDie[1]},
			new State(SUMO_DIE + 1, SUMO_DIE_RATE, NullSumo), // s_SumoDie[2]},
			new State(SUMO_DIE + 2, SUMO_DIE_RATE, NullSumo), // s_SumoDie[3]},
			new State(SUMO_DIE + 3, SUMO_DIE_RATE, NullSumo), // s_SumoDie[4]},
			new State(SUMO_DIE + 4, SUMO_DIE_RATE, NullSumo), // s_SumoDie[5]},
			new State(SUMO_DIE + 5, SUMO_DIE_RATE, NullSumo), // s_SumoDie[6]},
			new State(SUMO_DIE + 6, SUMO_DIE_RATE, NullSumo), // s_SumoDie[7]},
			new State(SUMO_DIE + 6, SUMO_DIE_RATE * 3, NullSumo), // s_SumoDie[8]},
			new State(SUMO_DIE + 7, SUMO_DIE_RATE, NullSumo), // s_SumoDie[9]},
			new State(SUMO_DIE + 6, SUMO_DIE_RATE, NullSumo), // s_SumoDie[10]},
			new State(SUMO_DIE + 7, SUMO_DIE_RATE, NullSumo), // s_SumoDie[11]},
			new State(SUMO_DIE + 6, SUMO_DIE_RATE - 8, NullSumo), // s_SumoDie[12]},
			new State(SUMO_DIE + 7, SUMO_DIE_RATE, NullSumo), // s_SumoDie[13]},
			new State(SUMO_DIE + 7, SF_QUICK_CALL, DoSumoDeathMelt), // s_SumoDie[14]},
			new State(SUMO_DIE + 6, SUMO_DIE_RATE - 15, NullSumo), // s_SumoDie[15]},
			new State(SUMO_DEAD, SF_QUICK_CALL, QueueFloorBlood), // s_SumoDie[16]},
			new State(SUMO_DEAD, SUMO_DIE_RATE, DoActorDebris).setNext(),// s_SumoDie[16]}
	};

	private static final State s_SumoDead[] = { new State(SUMO_DEAD, SUMO_DIE_RATE, DoActorDebris).setNext(),// s_SumoDead[0]},
	};

	public enum SumoStateGroup implements StateGroup {
		sg_SumoRun(s_SumoRun[0], s_SumoRun[1], s_SumoRun[2], s_SumoRun[3], s_SumoRun[4]),
		sg_SumoStand(s_SumoStand[0], s_SumoStand[1], s_SumoStand[2], s_SumoStand[3], s_SumoStand[4]),
		sg_SumoPain(s_SumoPain[0], s_SumoPain[1], s_SumoPain[2], s_SumoPain[3], s_SumoPain[4]), sg_SumoDie(s_SumoDie),
		sg_SumoDead(s_SumoDead),
		sg_SumoStomp(s_SumoStomp[0], s_SumoStomp[1], s_SumoStomp[2], s_SumoStomp[3], s_SumoStomp[4]),
		sg_SumoFart(s_SumoFart[0], s_SumoFart[1], s_SumoFart[2], s_SumoFart[3], s_SumoFart[4]),
		sg_SumoClap(s_SumoClap[0], s_SumoClap[1], s_SumoClap[2], s_SumoClap[3], s_SumoClap[4]);

		private final State[][] group;
		private int index = -1;

		SumoStateGroup(State[]... states) {
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

	public static void InitSumoStates() {
		for (SumoStateGroup sg : SumoStateGroup.values()) {
			for (int rot = 0; rot < sg.group.length; rot++) {
				State.InitState(sg.group[rot]);
			}
		}
	}

	private static final Actor_Action_Set SumoActionSet = new Actor_Action_Set(SumoStateGroup.sg_SumoStand,
			SumoStateGroup.sg_SumoRun, null, null, null, null, null, null, null, null, null, // climb
			SumoStateGroup.sg_SumoPain, // pain
			SumoStateGroup.sg_SumoDie, null, SumoStateGroup.sg_SumoDead, null, null,
			new StateGroup[] { SumoStateGroup.sg_SumoStomp, SumoStateGroup.sg_SumoFart }, new short[] { 800, 1024 },
			new StateGroup[] { SumoStateGroup.sg_SumoClap, SumoStateGroup.sg_SumoStomp, SumoStateGroup.sg_SumoFart },
			new short[] { 400, 750, 1024 }, null, null, null);

	private static final Actor_Action_Set MiniSumoActionSet = new Actor_Action_Set(SumoStateGroup.sg_SumoStand,
			SumoStateGroup.sg_SumoRun, null, null, null, null, null, null, null, null, null, // climb
			SumoStateGroup.sg_SumoPain, // pain
			SumoStateGroup.sg_SumoDie, null, SumoStateGroup.sg_SumoDead, null, null,
			new StateGroup[] { SumoStateGroup.sg_SumoClap }, new short[] { 1024 },
			new StateGroup[] { SumoStateGroup.sg_SumoClap }, new short[] { 1024 }, null, null, null);

	public static int SetupSumo(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];

		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, SUMO_RUN_R0, s_SumoRun[0][0]);
			u.Health = 6000;
		}

		if (Skill == 0)
			u.Health = 2000;
		if (Skill == 1)
			u.Health = 4000;

		ChangeState(SpriteNum, s_SumoRun[0][0]);
		u.Attrib = SumoAttrib;
		DoActorSetSpeed(SpriteNum, NORM_SPEED);
		u.StateEnd = s_SumoDie[0];
		u.Rot = SumoStateGroup.sg_SumoRun;

		EnemyDefaults(SpriteNum, SumoActionSet, SumoPersonality);

		sp.clipdist = (512) >> 2;
		if (sp.pal == 16) {
			// Mini Sumo
			sp.xrepeat = 43;
			sp.yrepeat = 29;
			u.ActorActionSet = MiniSumoActionSet;
			u.Health = 500;
		} else {
			sp.xrepeat = 115;
			sp.yrepeat = 75;
		}

		return (0);
	}

	private static int NullSumo(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (!TEST(u.Flags, SPR_CLIMBING))
			KeepActorOnFloor(SpriteNum);

		DoActorSectorDamage(SpriteNum);

		return (0);
	}

	private static int DoSumoMove(int SpriteNum) {
		USER u = pUser[SpriteNum];

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

	private static int InitSumoFart(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];

		PlaySound(DIGI_SUMOFART, sp, v3df_follow);

		InitChemBomb(SpriteNum);

		SetSumoFartQuake(SpriteNum);
		InitSumoNapalm(SpriteNum);

		return (0);
	}

	private static int InitSumoStomp(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];

		PlaySound(DIGI_SUMOSTOMP, sp, v3df_none);
		SetSumoQuake(SpriteNum);
		InitSumoStompAttack(SpriteNum);

		return (0);
	}

	private static int InitSumoClap(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];

		if (sp.pal == 16 && RANDOM_RANGE(1000) <= 800)
			InitMiniSumoClap(SpriteNum);
		else
			InitSumoSkull(SpriteNum);
		return (0);
	}

	private static boolean alreadydid = false;

	private static int DoSumoDeathMelt(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		PlaySound(DIGI_SUMOFART, sp, v3df_follow);

		u.ID = SUMO_RUN_R0;
		InitChemBomb(SpriteNum);
		u.ID = 0;

		DoMatchEverything(null, sp.lotag, ON);

		if (!gs.muteMusic && !alreadydid) {
			CDAudio_Play(RedBookSong[Level], true);
			alreadydid = true;
		}

		BossSpriteNum[1] = -2; // Sprite is gone, set it back to keep it valid!

		return (0);
	}

	public static void BossHealthMeter() {
		SPRITE sp;
		USER u;
		PlayerStr pp = Player[myconnectindex];
		int color = 0, metertics, meterunit;
		short i = 0, nexti;
		int y;

		short health;
		boolean bosswasseen;

		if (Level != 20 && Level != 4 && Level != 11 && Level != 5)
			return;

		// Don't draw bar for other players
		if (pp != Player[myconnectindex])
			return;

		// all enemys
		if ((Level == 20 && (BossSpriteNum[0] == -1 || BossSpriteNum[1] == -1 || BossSpriteNum[2] == -1))
				|| (Level == 4 && BossSpriteNum[0] == -1) || (Level == 5 && BossSpriteNum[0] == -1)
				|| (Level == 11 && BossSpriteNum[1] == -1)) {
			for (i = headspritestat[STAT_ENEMY]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				sp = sprite[i];
				u = pUser[i];

				if ((u.ID == SERP_RUN_R0 || u.ID == SUMO_RUN_R0 || u.ID == ZILLA_RUN_R0) && sp.pal != 16) {
					if (u.ID == SERP_RUN_R0)
						BossSpriteNum[0] = i;
					else if (u.ID == SUMO_RUN_R0)
						BossSpriteNum[1] = i;
					else if (u.ID == ZILLA_RUN_R0)
						BossSpriteNum[2] = i;
				}
			}
		}

		if (BossSpriteNum[0] <= -1 && BossSpriteNum[1] <= -1 && BossSpriteNum[2] <= -1)
			return;

		// Frank, good optimization for other levels, but it broke level 20. :(
		// I kept this but had to add a fix.
		bosswasseen = serpwasseen | sumowasseen | zillawasseen;

		// Only show the meter when you can see the boss
		if ((Level == 20 && (!serpwasseen || !sumowasseen || !zillawasseen)) || !bosswasseen) {
			for (i = 0; i < 3; i++) {
				if (BossSpriteNum[i] >= 0) {
					sp = sprite[BossSpriteNum[i]];
					u = pUser[BossSpriteNum[i]];

					if (engine.cansee(sp.x, sp.y, SPRITEp_TOS(sp), sp.sectnum, pp.posx, pp.posy, pp.posz - Z(40),
							pp.cursectnum)) {
						if (i == 0 && !serpwasseen) {
							serpwasseen = true;

							if (!gs.muteMusic) {
								CDAudio_Play(13, true);
							}

						} else if (i == 1 && !sumowasseen) {
							sumowasseen = true;

							if (!gs.muteMusic) {
								CDAudio_Play(13, true);
							}

						} else if (i == 2 && !zillawasseen) {
							zillawasseen = true;

							if (!gs.muteMusic) {
								CDAudio_Play(13, true);
							}

						}
					}
				}
			}
		}

		for (i = 0; i < 3; i++) {

			if (i == 0 && (!serpwasseen || BossSpriteNum[0] < 0))
				continue;
			if (i == 1 && (!sumowasseen || BossSpriteNum[1] < 0))
				continue;
			if (i == 2 && (!zillawasseen || BossSpriteNum[2] < 0))
				continue;

			// This is needed because of possible saved game situation
			if(game.getScreen() instanceof GameAdapter) {
				if ((!CDAudio_Playing() || playTrack != 13) && !triedplay) {
					CDAudio_Play(13, true);
					triedplay = true; // Only try once, then give up
				}
			}

			sp = sprite[BossSpriteNum[i]];
			u = pUser[BossSpriteNum[i]];
			
			if(u == null)
				continue;

			if (u.ID == SERP_RUN_R0 && serpwasseen) {
				if (Skill == 0)
					health = 1100;
				else if (Skill == 1)
					health = 2200;
				else
					health = HEALTH_SERP_GOD;
				meterunit = health / 30;
			} else if (u.ID == SUMO_RUN_R0 && sumowasseen) {
				if (Skill == 0)
					health = 2000;
				else if (Skill == 1)
					health = 4000;
				else
					health = 6000;
				meterunit = health / 30;
			} else if (u.ID == ZILLA_RUN_R0 && zillawasseen) {
				if (Skill == 0)
					health = 2000;
				else if (Skill == 1)
					health = 4000;
				else
					health = 6000;
				meterunit = health / 30;
			} else
				continue;

			if (meterunit > 0) {
				if (u.Health < meterunit && u.Health > 0)
					metertics = 1;
				else
					metertics = u.Health / meterunit;
			} else
				continue;

			if (metertics <= 0) {
				SetRedrawScreen(pp);
				continue;
			}

			if (CommPlayers < 2)
				y = 10;
			else if (CommPlayers >= 2 && CommPlayers <= 4)
				y = 20;
			else
				y = 30;

			if (Level == 20 && CommPlayers >= 2) {
				if (u.ID == SUMO_RUN_R0 && sumowasseen)
					y += 10;
				else if (u.ID == ZILLA_RUN_R0 && zillawasseen)
					y += 20;
			}

			if (metertics <= 12 && metertics > 6)
				color = 20;
			else if (metertics <= 6)
				color = 25;
			else
				color = 22;

			engine.rotatesprite((73 + 12) << 16, y << 16, 65536, 0, 5407, 1, 1, (ROTATE_SPRITE_SCREEN_CLIP), 0, 0,
					xdim - 1, ydim - 1);

			engine.rotatesprite((100 + 47) << 16, y << 16, 65536, 0, 5406 - metertics, 1, color,
					(ROTATE_SPRITE_SCREEN_CLIP), 0, 0, xdim - 1, ydim - 1);
		}

	}
	
	public static void SumoSaveable()
	{
		SaveData(NullSumo);
		SaveData(DoSumoMove);
		SaveData(InitSumoFart);
		SaveData(InitSumoStomp);
		SaveData(InitSumoClap);
		SaveData(DoSumoDeathMelt);

		SaveData(SumoPersonality);

		SaveData(SumoAttrib);

		SaveData(s_SumoRun);
		SaveGroup(SumoStateGroup.sg_SumoRun);
		SaveData(s_SumoStand);
		SaveGroup(SumoStateGroup.sg_SumoStand);
		SaveData(s_SumoPain);
		SaveGroup(SumoStateGroup.sg_SumoPain);
		SaveData(s_SumoFart);
		SaveGroup(SumoStateGroup.sg_SumoFart);
		SaveData(s_SumoClap);
		SaveGroup(SumoStateGroup.sg_SumoClap);
		SaveData(s_SumoStomp);
		SaveGroup(SumoStateGroup.sg_SumoStomp);
		SaveData(s_SumoDie);
		SaveGroup(SumoStateGroup.sg_SumoDie);
		SaveData(s_SumoDead);
		SaveGroup(SumoStateGroup.sg_SumoDead);

		SaveData(SumoActionSet);
		SaveData(MiniSumoActionSet);
	}
}
