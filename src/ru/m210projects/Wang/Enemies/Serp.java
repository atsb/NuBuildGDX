package ru.m210projects.Wang.Enemies;

import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Wang.Actor.DoActorDebris;
import static ru.m210projects.Wang.Actor.DoActorSlide;
import static ru.m210projects.Wang.Actor.KeepActorOnFloor;
import static ru.m210projects.Wang.Ai.DoActorSetSpeed;
import static ru.m210projects.Wang.Ai.InitActorAmbientNoise;
import static ru.m210projects.Wang.Ai.InitActorAttack;
import static ru.m210projects.Wang.Ai.InitActorDecide;
import static ru.m210projects.Wang.Ai.InitActorEvade;
import static ru.m210projects.Wang.Ai.InitActorFindPlayer;
import static ru.m210projects.Wang.Ai.InitActorMoveCloser;
import static ru.m210projects.Wang.Ai.InitActorReposition;
import static ru.m210projects.Wang.Ai.InitActorRunAway;
import static ru.m210projects.Wang.Ai.InitActorWanderAround;
import static ru.m210projects.Wang.Ai.NORM_SPEED;
import static ru.m210projects.Wang.Digi.DIGI_SERPALERT;
import static ru.m210projects.Wang.Digi.DIGI_SERPAMBIENT;
import static ru.m210projects.Wang.Digi.DIGI_SERPDEATHEXPLODE;
import static ru.m210projects.Wang.Digi.DIGI_SERPMAGICLAUNCH;
import static ru.m210projects.Wang.Digi.DIGI_SERPPAIN;
import static ru.m210projects.Wang.Digi.DIGI_SERPSCREAM;
import static ru.m210projects.Wang.Digi.DIGI_SERPSUMMONHEADS;
import static ru.m210projects.Wang.Digi.DIGI_SERPSWORDATTACK;
import static ru.m210projects.Wang.Digi.DIGI_SERPTAUNTYOU;
import static ru.m210projects.Wang.Enemies.Coolie.EnemyDefaults;
import static ru.m210projects.Wang.Enemies.Sumo.BossSpriteNum;
import static ru.m210projects.Wang.Game.Level;
import static ru.m210projects.Wang.Game.Skill;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_RESTORE;
import static ru.m210projects.Wang.Gameutils.HEALTH_SERP_GOD;
import static ru.m210projects.Wang.Gameutils.SF_QUICK_CALL;
import static ru.m210projects.Wang.Gameutils.SPRITEp_SIZE_Z;
import static ru.m210projects.Wang.Gameutils.SPR_ELECTRO_TOLERANT;
import static ru.m210projects.Wang.Gameutils.SPR_SLIDING;
import static ru.m210projects.Wang.Gameutils.SPR_XFLIP_TOGGLE;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Names.SERP_DEAD;
import static ru.m210projects.Wang.Names.SERP_DIE;
import static ru.m210projects.Wang.Names.SERP_RUN_R0;
import static ru.m210projects.Wang.Names.SERP_RUN_R1;
import static ru.m210projects.Wang.Names.SERP_RUN_R2;
import static ru.m210projects.Wang.Names.SERP_RUN_R3;
import static ru.m210projects.Wang.Names.SERP_RUN_R4;
import static ru.m210projects.Wang.Names.SERP_SLASH_R0;
import static ru.m210projects.Wang.Names.SERP_SLASH_R1;
import static ru.m210projects.Wang.Names.SERP_SLASH_R2;
import static ru.m210projects.Wang.Names.SERP_SLASH_R3;
import static ru.m210projects.Wang.Names.SERP_SLASH_R4;
import static ru.m210projects.Wang.Names.SERP_SPELL_R0;
import static ru.m210projects.Wang.Names.SERP_SPELL_R1;
import static ru.m210projects.Wang.Names.SERP_SPELL_R2;
import static ru.m210projects.Wang.Names.SERP_SPELL_R3;
import static ru.m210projects.Wang.Names.SERP_SPELL_R4;
import static ru.m210projects.Wang.Sector.DoMatchEverything;
import static ru.m210projects.Wang.Sound.CDAudio_Play;
import static ru.m210projects.Wang.Sound.RedBookSong;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.NewStateGroup;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Track.ActorFollowTrack;
import static ru.m210projects.Wang.Type.MyTypes.ON;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.*;
import static ru.m210projects.Wang.Weapon.InitSerpRing;
import static ru.m210projects.Wang.Weapon.InitSerpSlash;
import static ru.m210projects.Wang.Weapon.InitSerpSpell;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Type.ATTRIBUTE;
import ru.m210projects.Wang.Type.Actor_Action_Set;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;

public class Serp {

	private static final Decision SerpBattle[] = { new Decision(670, InitActorMoveCloser),
			new Decision(700, InitActorAmbientNoise), new Decision(710, InitActorRunAway),
			new Decision(1024, InitActorAttack), };

	private static final Decision SerpOffense[] = { new Decision(775, InitActorMoveCloser),
			new Decision(800, InitActorAmbientNoise), new Decision(1024, InitActorAttack), };

	private static final Decision SerpBroadcast[] = { new Decision(10, InitActorAmbientNoise),
			new Decision(1024, InitActorDecide), };

	private static final Decision SerpSurprised[] = { new Decision(701, InitActorMoveCloser),
			new Decision(1024, InitActorDecide), };

	private static final Decision SerpEvasive[] = { new Decision(10, InitActorEvade), new Decision(1024, null), };

	private static final Decision SerpLostTarget[] = { new Decision(900, InitActorFindPlayer),
			new Decision(921, InitActorAmbientNoise), new Decision(1024, InitActorWanderAround), };

	private static final Decision SerpCloseRange[] = { new Decision(700, InitActorAttack),
			new Decision(1024, InitActorReposition), };

	private static final Personality SerpPersonality = new Personality(SerpBattle, SerpOffense, SerpBroadcast,
			SerpSurprised, SerpEvasive, SerpLostTarget, SerpCloseRange, SerpCloseRange);

	private static final ATTRIBUTE SerpAttrib = new ATTRIBUTE(new short[] { 200, 220, 240, 270 }, // Speeds
			new short[] { 3, 0, -2, -3 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { DIGI_SERPAMBIENT, DIGI_SERPALERT, DIGI_SERPSWORDATTACK, DIGI_SERPPAIN, DIGI_SERPSCREAM,
					DIGI_SERPMAGICLAUNCH, DIGI_SERPSUMMONHEADS, DIGI_SERPTAUNTYOU, DIGI_SERPDEATHEXPLODE, 0 });

	//////////////////////
	//
	// SERP RUN
	//
	//////////////////////

	private static final Animator DoSerpMove = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoSerpMove(SpriteNum) != 0;
		}
	};

	private static final int SERP_RUN_RATE = 24;

	private static final State s_SerpRun[][] = { { new State(SERP_RUN_R0 + 0, SERP_RUN_RATE, DoSerpMove), // s_SerpRun[0][1]},
			new State(SERP_RUN_R0 + 1, SERP_RUN_RATE, DoSerpMove), // s_SerpRun[0][2]},
			new State(SERP_RUN_R0 + 2, SERP_RUN_RATE, DoSerpMove), // s_SerpRun[0][3]},
			new State(SERP_RUN_R0 + 1, SERP_RUN_RATE, DoSerpMove),// s_SerpRun[0][0]},
			}, { new State(SERP_RUN_R1 + 0, SERP_RUN_RATE, DoSerpMove), // s_SerpRun[1][1]},
					new State(SERP_RUN_R1 + 1, SERP_RUN_RATE, DoSerpMove), // s_SerpRun[1][2]},
					new State(SERP_RUN_R1 + 2, SERP_RUN_RATE, DoSerpMove), // s_SerpRun[1][3]},
					new State(SERP_RUN_R1 + 1, SERP_RUN_RATE, DoSerpMove),// s_SerpRun[1][0]},
			}, { new State(SERP_RUN_R2 + 0, SERP_RUN_RATE, DoSerpMove), // s_SerpRun[2][1]},
					new State(SERP_RUN_R2 + 1, SERP_RUN_RATE, DoSerpMove), // s_SerpRun[2][2]},
					new State(SERP_RUN_R2 + 2, SERP_RUN_RATE, DoSerpMove), // s_SerpRun[2][3]},
					new State(SERP_RUN_R2 + 1, SERP_RUN_RATE, DoSerpMove),// s_SerpRun[2][0]},
			}, { new State(SERP_RUN_R3 + 0, SERP_RUN_RATE, DoSerpMove), // s_SerpRun[3][1]},
					new State(SERP_RUN_R3 + 1, SERP_RUN_RATE, DoSerpMove), // s_SerpRun[3][2]},
					new State(SERP_RUN_R3 + 2, SERP_RUN_RATE, DoSerpMove), // s_SerpRun[3][3]},
					new State(SERP_RUN_R3 + 1, SERP_RUN_RATE, DoSerpMove),// s_SerpRun[3][0]},
			}, { new State(SERP_RUN_R4 + 0, SERP_RUN_RATE, DoSerpMove), // s_SerpRun[4][1]},
					new State(SERP_RUN_R4 + 1, SERP_RUN_RATE, DoSerpMove), // s_SerpRun[4][2]},
					new State(SERP_RUN_R4 + 2, SERP_RUN_RATE, DoSerpMove), // s_SerpRun[4][3]},
					new State(SERP_RUN_R4 + 1, SERP_RUN_RATE, DoSerpMove),// s_SerpRun[4][0]},
			} };

	//////////////////////
	//
	// SERP SLASH
	//
	//////////////////////

	private static final int SERP_SLASH_RATE = 9;

	private static final Animator NullSerp = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return NullSerp(SpriteNum) != 0;
		}
	};

	private static final Animator InitSerpSlash = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitSerpSlash(SpriteNum) != 0;
		}
	};

	private static final State s_SerpSlash[][] = { { new State(SERP_SLASH_R0 + 2, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[0][1]},
			new State(SERP_SLASH_R0 + 1, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[0][2]},
			new State(SERP_SLASH_R0 + 0, SERP_SLASH_RATE * 2, NullSerp), // s_SerpSlash[0][3]},
			new State(SERP_SLASH_R0 + 1, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[0][4]},
			new State(SERP_SLASH_R0 + 2, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[0][5]},
			new State(SERP_SLASH_R0 + 3, SF_QUICK_CALL, InitSerpSlash), // s_SerpSlash[0][6]},
			new State(SERP_SLASH_R0 + 3, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[0][7]},
			new State(SERP_SLASH_R0 + 4, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[0][8]},
			new State(SERP_SLASH_R0 + 4, 0 | SF_QUICK_CALL, InitActorDecide), // s_SerpSlash[0][9]},
			new State(SERP_SLASH_R0 + 4, SERP_SLASH_RATE, DoSerpMove).setNext(),// s_SerpSlash[0][9]},
			}, { new State(SERP_SLASH_R1 + 2, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[1][1]},
					new State(SERP_SLASH_R1 + 1, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[1][2]},
					new State(SERP_SLASH_R1 + 0, SERP_SLASH_RATE * 2, NullSerp), // s_SerpSlash[1][3]},
					new State(SERP_SLASH_R1 + 1, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[1][4]},
					new State(SERP_SLASH_R1 + 2, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[1][5]},
					new State(SERP_SLASH_R1 + 3, SF_QUICK_CALL, InitSerpSlash), // s_SerpSlash[1][6]},
					new State(SERP_SLASH_R1 + 3, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[1][7]},
					new State(SERP_SLASH_R1 + 4, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[1][8]},
					new State(SERP_SLASH_R1 + 4, 0 | SF_QUICK_CALL, InitActorDecide), // s_SerpSlash[1][9]},
					new State(SERP_SLASH_R1 + 4, SERP_SLASH_RATE, DoSerpMove).setNext(),// s_SerpSlash[1][9]},
			}, { new State(SERP_SLASH_R2 + 2, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[2][1]},
					new State(SERP_SLASH_R2 + 1, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[2][2]},
					new State(SERP_SLASH_R2 + 0, SERP_SLASH_RATE * 2, NullSerp), // s_SerpSlash[2][3]},
					new State(SERP_SLASH_R2 + 1, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[2][4]},
					new State(SERP_SLASH_R2 + 2, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[2][5]},
					new State(SERP_SLASH_R2 + 3, SF_QUICK_CALL, InitSerpSlash), // s_SerpSlash[2][6]},
					new State(SERP_SLASH_R2 + 3, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[2][7]},
					new State(SERP_SLASH_R2 + 4, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[2][8]},
					new State(SERP_SLASH_R2 + 4, 0 | SF_QUICK_CALL, InitActorDecide), // s_SerpSlash[2][9]},
					new State(SERP_SLASH_R2 + 4, SERP_SLASH_RATE, DoSerpMove).setNext(),// s_SerpSlash[2][9]},
			}, { new State(SERP_SLASH_R3 + 2, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[3][1]},
					new State(SERP_SLASH_R3 + 1, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[3][2]},
					new State(SERP_SLASH_R3 + 0, SERP_SLASH_RATE * 2, NullSerp), // s_SerpSlash[3][3]},
					new State(SERP_SLASH_R3 + 1, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[3][4]},
					new State(SERP_SLASH_R3 + 2, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[3][5]},
					new State(SERP_SLASH_R3 + 3, SF_QUICK_CALL, InitSerpSlash), // s_SerpSlash[3][6]},
					new State(SERP_SLASH_R3 + 3, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[3][7]},
					new State(SERP_SLASH_R3 + 4, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[3][8]},
					new State(SERP_SLASH_R3 + 4, 0 | SF_QUICK_CALL, InitActorDecide), // s_SerpSlash[3][9]},
					new State(SERP_SLASH_R3 + 4, SERP_SLASH_RATE, DoSerpMove).setNext(),// s_SerpSlash[3][9]},
			}, { new State(SERP_SLASH_R4 + 2, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[4][1]},
					new State(SERP_SLASH_R4 + 1, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[4][2]},
					new State(SERP_SLASH_R4 + 0, SERP_SLASH_RATE * 2, NullSerp), // s_SerpSlash[4][3]},
					new State(SERP_SLASH_R4 + 1, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[4][4]},
					new State(SERP_SLASH_R4 + 2, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[4][5]},
					new State(SERP_SLASH_R4 + 3, SF_QUICK_CALL, InitSerpSlash), // s_SerpSlash[4][6]},
					new State(SERP_SLASH_R4 + 3, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[4][7]},
					new State(SERP_SLASH_R4 + 4, SERP_SLASH_RATE, NullSerp), // s_SerpSlash[4][8]},
					new State(SERP_SLASH_R4 + 4, 0 | SF_QUICK_CALL, InitActorDecide), // s_SerpSlash[4][9]},
					new State(SERP_SLASH_R4 + 4, SERP_SLASH_RATE, DoSerpMove).setNext(),// s_SerpSlash[4][9]},
			} };

	//////////////////////
	//
	// SERP SKULL SPELL
	//
	//////////////////////

	private static final int SERP_SKULL_SPELL_RATE = 18;

	private static final Animator InitSerpRing = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitSerpRing(SpriteNum) != 0;
		}
	};

	private static final State s_SerpSkullSpell[][] = {
			{ new State(SERP_SPELL_R0 + 2, SERP_SKULL_SPELL_RATE * 2, NullSerp), // s_SerpSkullSpell[0][1]},
					new State(SERP_SPELL_R0 + 1, SERP_SKULL_SPELL_RATE, NullSerp), // s_SerpSkullSpell[0][2]},
					new State(SERP_SPELL_R0 + 0, SERP_SKULL_SPELL_RATE * 2, NullSerp), // s_SerpSkullSpell[0][3]},
					new State(SERP_SPELL_R0 + 0, SF_QUICK_CALL, InitSerpRing), // s_SerpSkullSpell[0][4]},
					new State(SERP_SPELL_R0 + 0, SERP_SKULL_SPELL_RATE, NullSerp), // s_SerpSkullSpell[0][5]},
					new State(SERP_SPELL_R0 + 1, SERP_SKULL_SPELL_RATE, NullSerp), // s_SerpSkullSpell[0][6]},
					new State(SERP_SPELL_R0 + 1, SF_QUICK_CALL, InitActorDecide), // s_SerpSkullSpell[0][7]},
					new State(SERP_SPELL_R0 + 1, SERP_SKULL_SPELL_RATE, DoSerpMove).setNext(),// s_SerpSkullSpell[0][7]},
			}, { new State(SERP_SPELL_R1 + 2, SERP_SKULL_SPELL_RATE * 2, NullSerp), // s_SerpSkullSpell[1][1]},
					new State(SERP_SPELL_R1 + 1, SERP_SKULL_SPELL_RATE, NullSerp), // s_SerpSkullSpell[1][2]},
					new State(SERP_SPELL_R1 + 0, SERP_SKULL_SPELL_RATE * 2, NullSerp), // s_SerpSkullSpell[1][3]},
					new State(SERP_SPELL_R1 + 0, SF_QUICK_CALL, InitSerpRing), // s_SerpSkullSpell[1][4]},
					new State(SERP_SPELL_R1 + 0, SERP_SKULL_SPELL_RATE, NullSerp), // s_SerpSkullSpell[1][5]},
					new State(SERP_SPELL_R1 + 1, SERP_SKULL_SPELL_RATE, NullSerp), // s_SerpSkullSpell[1][6]},
					new State(SERP_SPELL_R1 + 1, SF_QUICK_CALL, InitActorDecide), // s_SerpSkullSpell[1][7]},
					new State(SERP_SPELL_R1 + 1, SERP_SKULL_SPELL_RATE, DoSerpMove).setNext(),// s_SerpSkullSpell[1][7]},
			}, { new State(SERP_SPELL_R2 + 2, SERP_SKULL_SPELL_RATE * 2, NullSerp), // s_SerpSkullSpell[2][1]},
					new State(SERP_SPELL_R2 + 1, SERP_SKULL_SPELL_RATE, NullSerp), // s_SerpSkullSpell[2][2]},
					new State(SERP_SPELL_R2 + 0, SERP_SKULL_SPELL_RATE * 2, NullSerp), // s_SerpSkullSpell[2][3]},
					new State(SERP_SPELL_R2 + 0, SF_QUICK_CALL, InitSerpRing), // s_SerpSkullSpell[2][4]},
					new State(SERP_SPELL_R2 + 0, SERP_SKULL_SPELL_RATE, NullSerp), // s_SerpSkullSpell[2][5]},
					new State(SERP_SPELL_R2 + 1, SERP_SKULL_SPELL_RATE, NullSerp), // s_SerpSkullSpell[2][6]},
					new State(SERP_SPELL_R2 + 1, SF_QUICK_CALL, InitActorDecide), // s_SerpSkullSpell[2][7]},
					new State(SERP_SPELL_R2 + 1, SERP_SKULL_SPELL_RATE, DoSerpMove).setNext(),// s_SerpSkullSpell[2][7]},
			}, { new State(SERP_SPELL_R3 + 2, SERP_SKULL_SPELL_RATE * 2, NullSerp), // s_SerpSkullSpell[3][1]},
					new State(SERP_SPELL_R3 + 1, SERP_SKULL_SPELL_RATE, NullSerp), // s_SerpSkullSpell[3][2]},
					new State(SERP_SPELL_R3 + 0, SERP_SKULL_SPELL_RATE * 2, NullSerp), // s_SerpSkullSpell[3][3]},
					new State(SERP_SPELL_R3 + 0, SF_QUICK_CALL, InitSerpRing), // s_SerpSkullSpell[3][4]},
					new State(SERP_SPELL_R3 + 0, SERP_SKULL_SPELL_RATE, NullSerp), // s_SerpSkullSpell[3][5]},
					new State(SERP_SPELL_R3 + 1, SERP_SKULL_SPELL_RATE, NullSerp), // s_SerpSkullSpell[3][6]},
					new State(SERP_SPELL_R3 + 1, SF_QUICK_CALL, InitActorDecide), // s_SerpSkullSpell[3][7]},
					new State(SERP_SPELL_R3 + 1, SERP_SKULL_SPELL_RATE, DoSerpMove).setNext(),// s_SerpSkullSpell[3][7]},
			}, { new State(SERP_SPELL_R4 + 2, SERP_SKULL_SPELL_RATE * 2, NullSerp), // s_SerpSkullSpell[4][1]},
					new State(SERP_SPELL_R4 + 1, SERP_SKULL_SPELL_RATE, NullSerp), // s_SerpSkullSpell[4][2]},
					new State(SERP_SPELL_R4 + 0, SERP_SKULL_SPELL_RATE * 2, NullSerp), // s_SerpSkullSpell[4][3]},
					new State(SERP_SPELL_R4 + 0, SF_QUICK_CALL, InitSerpRing), // s_SerpSkullSpell[4][4]},
					new State(SERP_SPELL_R4 + 0, SERP_SKULL_SPELL_RATE, NullSerp), // s_SerpSkullSpell[4][5]},
					new State(SERP_SPELL_R4 + 1, SERP_SKULL_SPELL_RATE, NullSerp), // s_SerpSkullSpell[4][6]},
					new State(SERP_SPELL_R4 + 1, SF_QUICK_CALL, InitActorDecide), // s_SerpSkullSpell[4][7]},
					new State(SERP_SPELL_R4 + 1, SERP_SKULL_SPELL_RATE, DoSerpMove).setNext(),// s_SerpSkullSpell[4][7]},
			} };

	//////////////////////
	//
	// SERP SPELL
	//
	//////////////////////

	private static final int SERP_SPELL_RATE = 18;

	private static final Animator InitSerpSpell = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitSerpSpell(SpriteNum) != 0;
		}
	};

	private static final State s_SerpSpell[][] = { { new State(SERP_SPELL_R0 + 2, SERP_SPELL_RATE * 2, NullSerp), // s_SerpSpell[0][1]},
			new State(SERP_SPELL_R0 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpSpell[0][2]},
			new State(SERP_SPELL_R0 + 0, SERP_SPELL_RATE * 2, NullSerp), // s_SerpSpell[0][3]},
			new State(SERP_SPELL_R0 + 0, SF_QUICK_CALL, InitSerpSpell), // s_SerpSpell[0][4]},
			new State(SERP_SPELL_R0 + 0, SERP_SPELL_RATE, NullSerp), // s_SerpSpell[0][5]},
			new State(SERP_SPELL_R0 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpSpell[0][6]},
			new State(SERP_SPELL_R0 + 1, SF_QUICK_CALL, InitActorDecide), // s_SerpSpell[0][7]},
			new State(SERP_SPELL_R0 + 1, SERP_SPELL_RATE, DoSerpMove).setNext(),// s_SerpSpell[0][7]},
			}, { new State(SERP_SPELL_R1 + 2, SERP_SPELL_RATE * 2, NullSerp), // s_SerpSpell[1][1]},
					new State(SERP_SPELL_R1 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpSpell[1][2]},
					new State(SERP_SPELL_R1 + 0, SERP_SPELL_RATE * 2, NullSerp), // s_SerpSpell[1][3]},
					new State(SERP_SPELL_R1 + 0, SF_QUICK_CALL, InitSerpSpell), // s_SerpSpell[1][4]},
					new State(SERP_SPELL_R1 + 0, SERP_SPELL_RATE, NullSerp), // s_SerpSpell[1][5]},
					new State(SERP_SPELL_R1 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpSpell[1][6]},
					new State(SERP_SPELL_R1 + 1, SF_QUICK_CALL, InitActorDecide), // s_SerpSpell[1][7]},
					new State(SERP_SPELL_R1 + 1, SERP_SPELL_RATE, DoSerpMove).setNext(),// s_SerpSpell[1][7]},
			}, { new State(SERP_SPELL_R2 + 2, SERP_SPELL_RATE * 2, NullSerp), // s_SerpSpell[2][1]},
					new State(SERP_SPELL_R2 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpSpell[2][2]},
					new State(SERP_SPELL_R2 + 0, SERP_SPELL_RATE * 2, NullSerp), // s_SerpSpell[2][3]},
					new State(SERP_SPELL_R2 + 0, SF_QUICK_CALL, InitSerpSpell), // s_SerpSpell[2][4]},
					new State(SERP_SPELL_R2 + 0, SERP_SPELL_RATE, NullSerp), // s_SerpSpell[2][5]},
					new State(SERP_SPELL_R2 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpSpell[2][6]},
					new State(SERP_SPELL_R2 + 1, SF_QUICK_CALL, InitActorDecide), // s_SerpSpell[2][7]},
					new State(SERP_SPELL_R2 + 1, SERP_SPELL_RATE, DoSerpMove).setNext(),// s_SerpSpell[2][7]},
			}, { new State(SERP_SPELL_R3 + 2, SERP_SPELL_RATE * 2, NullSerp), // s_SerpSpell[3][1]},
					new State(SERP_SPELL_R3 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpSpell[3][2]},
					new State(SERP_SPELL_R3 + 0, SERP_SPELL_RATE * 2, NullSerp), // s_SerpSpell[3][3]},
					new State(SERP_SPELL_R3 + 0, SF_QUICK_CALL, InitSerpSpell), // s_SerpSpell[3][4]},
					new State(SERP_SPELL_R3 + 0, SERP_SPELL_RATE, NullSerp), // s_SerpSpell[3][5]},
					new State(SERP_SPELL_R3 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpSpell[3][6]},
					new State(SERP_SPELL_R3 + 1, SF_QUICK_CALL, InitActorDecide), // s_SerpSpell[3][7]},
					new State(SERP_SPELL_R3 + 1, SERP_SPELL_RATE, DoSerpMove).setNext(),// s_SerpSpell[3][7]},
			}, { new State(SERP_SPELL_R4 + 2, SERP_SPELL_RATE * 2, NullSerp), // s_SerpSpell[4][1]},
					new State(SERP_SPELL_R4 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpSpell[4][2]},
					new State(SERP_SPELL_R4 + 0, SERP_SPELL_RATE * 2, NullSerp), // s_SerpSpell[4][3]},
					new State(SERP_SPELL_R4 + 0, SF_QUICK_CALL, InitSerpSpell), // s_SerpSpell[4][4]},
					new State(SERP_SPELL_R4 + 0, SERP_SPELL_RATE, NullSerp), // s_SerpSpell[4][5]},
					new State(SERP_SPELL_R4 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpSpell[4][6]},
					new State(SERP_SPELL_R4 + 1, SF_QUICK_CALL, InitActorDecide), // s_SerpSpell[4][7]},
					new State(SERP_SPELL_R4 + 1, SERP_SPELL_RATE, DoSerpMove).setNext(),// s_SerpSpell[4][7]},
			} };

	//////////////////////
	//
	// SERP RAPID SPELL
	//
	//////////////////////

	private static final State s_SerpRapidSpell[][] = { { new State(SERP_SPELL_R0 + 2, SERP_SPELL_RATE * 2, NullSerp), // s_SerpRapidSpell[0][1]},
			new State(SERP_SPELL_R0 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpRapidSpell[0][2]},
			new State(SERP_SPELL_R0 + 0, SERP_SPELL_RATE * 2, NullSerp), // s_SerpRapidSpell[0][3]},
			new State(SERP_SPELL_R0 + 0, SF_QUICK_CALL, InitSerpSpell), // s_SerpRapidSpell[0][4]},
			new State(SERP_SPELL_R0 + 0, SERP_SPELL_RATE * 2, NullSerp), // s_SerpRapidSpell[0][5]},
			new State(SERP_SPELL_R0 + 0, SF_QUICK_CALL, InitSerpSpell), // s_SerpRapidSpell[0][6]},
			new State(SERP_SPELL_R0 + 0, SERP_SPELL_RATE, NullSerp), // s_SerpRapidSpell[0][7]},
			new State(SERP_SPELL_R0 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpRapidSpell[0][8]},
			new State(SERP_SPELL_R0 + 1, SF_QUICK_CALL, InitActorDecide), // s_SerpRapidSpell[0][9]},
			new State(SERP_SPELL_R0 + 1, SERP_SPELL_RATE, DoSerpMove).setNext(),// s_SerpRapidSpell[0][9]},
			}, { new State(SERP_SPELL_R1 + 2, SERP_SPELL_RATE * 2, NullSerp), // s_SerpRapidSpell[1][1]},
					new State(SERP_SPELL_R1 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpRapidSpell[1][2]},
					new State(SERP_SPELL_R1 + 0, SERP_SPELL_RATE * 2, NullSerp), // s_SerpRapidSpell[1][3]},
					new State(SERP_SPELL_R1 + 0, SF_QUICK_CALL, InitSerpSpell), // s_SerpRapidSpell[1][4]},
					new State(SERP_SPELL_R1 + 0, SERP_SPELL_RATE * 2, NullSerp), // s_SerpRapidSpell[1][5]},
					new State(SERP_SPELL_R1 + 0, SF_QUICK_CALL, InitSerpSpell), // s_SerpRapidSpell[1][6]},
					new State(SERP_SPELL_R1 + 0, SERP_SPELL_RATE, NullSerp), // s_SerpRapidSpell[1][7]},
					new State(SERP_SPELL_R1 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpRapidSpell[1][8]},
					new State(SERP_SPELL_R1 + 1, SF_QUICK_CALL, InitActorDecide), // s_SerpRapidSpell[1][9]},
					new State(SERP_SPELL_R1 + 1, SERP_SPELL_RATE, DoSerpMove).setNext(),// s_SerpRapidSpell[1][9]},
			}, { new State(SERP_SPELL_R2 + 2, SERP_SPELL_RATE * 2, NullSerp), // s_SerpRapidSpell[2][1]},
					new State(SERP_SPELL_R2 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpRapidSpell[2][2]},
					new State(SERP_SPELL_R2 + 0, SERP_SPELL_RATE * 2, NullSerp), // s_SerpRapidSpell[2][3]},
					new State(SERP_SPELL_R2 + 0, SF_QUICK_CALL, InitSerpSpell), // s_SerpRapidSpell[2][4]},
					new State(SERP_SPELL_R2 + 0, SERP_SPELL_RATE * 2, NullSerp), // s_SerpRapidSpell[2][5]},
					new State(SERP_SPELL_R2 + 0, SF_QUICK_CALL, InitSerpSpell), // s_SerpRapidSpell[2][6]},
					new State(SERP_SPELL_R2 + 0, SERP_SPELL_RATE, NullSerp), // s_SerpRapidSpell[2][7]},
					new State(SERP_SPELL_R2 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpRapidSpell[2][8]},
					new State(SERP_SPELL_R2 + 1, SF_QUICK_CALL, InitActorDecide), // s_SerpRapidSpell[2][9]},
					new State(SERP_SPELL_R2 + 1, SERP_SPELL_RATE, DoSerpMove).setNext(),// s_SerpRapidSpell[2][9]},
			}, { new State(SERP_SPELL_R3 + 2, SERP_SPELL_RATE * 2, NullSerp), // s_SerpRapidSpell[3][1]},
					new State(SERP_SPELL_R3 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpRapidSpell[3][2]},
					new State(SERP_SPELL_R3 + 0, SERP_SPELL_RATE * 2, NullSerp), // s_SerpRapidSpell[3][3]},
					new State(SERP_SPELL_R3 + 0, SF_QUICK_CALL, InitSerpSpell), // s_SerpRapidSpell[3][4]},
					new State(SERP_SPELL_R3 + 0, SERP_SPELL_RATE * 2, NullSerp), // s_SerpRapidSpell[3][5]},
					new State(SERP_SPELL_R3 + 0, SF_QUICK_CALL, InitSerpSpell), // s_SerpRapidSpell[3][6]},
					new State(SERP_SPELL_R3 + 0, SERP_SPELL_RATE, NullSerp), // s_SerpRapidSpell[3][7]},
					new State(SERP_SPELL_R3 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpRapidSpell[3][8]},
					new State(SERP_SPELL_R3 + 1, SF_QUICK_CALL, InitActorDecide), // s_SerpRapidSpell[3][9]},
					new State(SERP_SPELL_R3 + 1, SERP_SPELL_RATE, DoSerpMove).setNext(),// s_SerpRapidSpell[3][9]},
			}, { new State(SERP_SPELL_R4 + 2, SERP_SPELL_RATE * 2, NullSerp), // s_SerpRapidSpell[4][1]},
					new State(SERP_SPELL_R4 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpRapidSpell[4][2]},
					new State(SERP_SPELL_R4 + 0, SERP_SPELL_RATE * 2, NullSerp), // s_SerpRapidSpell[4][3]},
					new State(SERP_SPELL_R4 + 0, SF_QUICK_CALL, InitSerpSpell), // s_SerpRapidSpell[4][4]},
					new State(SERP_SPELL_R4 + 0, SERP_SPELL_RATE * 2, NullSerp), // s_SerpRapidSpell[4][5]},
					new State(SERP_SPELL_R4 + 0, SF_QUICK_CALL, InitSerpSpell), // s_SerpRapidSpell[4][6]},
					new State(SERP_SPELL_R4 + 0, SERP_SPELL_RATE, NullSerp), // s_SerpRapidSpell[4][7]},
					new State(SERP_SPELL_R4 + 1, SERP_SPELL_RATE, NullSerp), // s_SerpRapidSpell[4][8]},
					new State(SERP_SPELL_R4 + 1, SF_QUICK_CALL, InitActorDecide), // s_SerpRapidSpell[4][9]},
					new State(SERP_SPELL_R4 + 1, SERP_SPELL_RATE, DoSerpMove).setNext(),// s_SerpRapidSpell[4][9]},
			} };

	//////////////////////
	//
	// SERP STAND
	//
	//////////////////////

	private static final int SERP_STAND_RATE = 12;

	private static final State s_SerpStand[][] = { { new State(SERP_RUN_R0 + 0, SERP_STAND_RATE, DoSerpMove).setNext(),// s_SerpStand[0][0]},
			}, { new State(SERP_RUN_R1 + 0, SERP_STAND_RATE, DoSerpMove).setNext(),// s_SerpStand[1][0]},
			}, { new State(SERP_RUN_R2 + 0, SERP_STAND_RATE, DoSerpMove).setNext(),// s_SerpStand[2][0]},
			}, { new State(SERP_RUN_R3 + 0, SERP_STAND_RATE, DoSerpMove).setNext(),// s_SerpStand[3][0]},
			}, { new State(SERP_RUN_R4 + 0, SERP_STAND_RATE, DoSerpMove).setNext(),// s_SerpStand[4][0]},
			}, };

	//////////////////////
	//
	// SERP DIE
	//
	//////////////////////

	private static final int SERP_DIE_RATE = 20;

	private static final Animator DoDeathSpecial = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoDeathSpecial(SpriteNum) != 0;
		}
	};

	private static final State s_SerpDie[] = { new State(SERP_DIE + 0, SERP_DIE_RATE, NullSerp), // s_SerpDie[1]},
			new State(SERP_DIE + 1, SERP_DIE_RATE, NullSerp), // s_SerpDie[2]},
			new State(SERP_DIE + 2, SERP_DIE_RATE, NullSerp), // s_SerpDie[3]},
			new State(SERP_DIE + 3, SERP_DIE_RATE, NullSerp), // s_SerpDie[4]},
			new State(SERP_DIE + 4, SERP_DIE_RATE, NullSerp), // s_SerpDie[5]},
			new State(SERP_DIE + 5, SERP_DIE_RATE, NullSerp), // s_SerpDie[6]},
			new State(SERP_DIE + 6, SERP_DIE_RATE, NullSerp), // s_SerpDie[7]},
			new State(SERP_DIE + 7, SERP_DIE_RATE, NullSerp), // s_SerpDie[8]},
			new State(SERP_DIE + 8, SERP_DIE_RATE, NullSerp), // s_SerpDie[9]},
			new State(SERP_DIE + 8, SF_QUICK_CALL, DoDeathSpecial), // s_SerpDie[10]},
			new State(SERP_DEAD, SERP_DIE_RATE, DoActorDebris).setNext(),// s_SerpDie[10]}
	};

	private static final State s_SerpDead[] = { new State(SERP_DEAD, SERP_DIE_RATE, DoActorDebris),// s_SerpDead[0]},
	};

	public enum SerpStateGroup implements StateGroup {
		sg_SerpStand(s_SerpStand[0], s_SerpStand[1], s_SerpStand[2], s_SerpStand[3], s_SerpStand[4]),
		sg_SerpRun(s_SerpRun[0], s_SerpRun[1], s_SerpRun[2], s_SerpRun[3], s_SerpRun[4]), sg_SerpDie(s_SerpDie),
		sg_SerpDead(s_SerpDead),
		sg_SerpSlash(s_SerpSlash[0], s_SerpSlash[1], s_SerpSlash[2], s_SerpSlash[3], s_SerpSlash[4]),
		sg_SerpSpell(s_SerpSpell[0], s_SerpSpell[1], s_SerpSpell[2], s_SerpSpell[3], s_SerpSpell[4]),
		sg_SerpRapidSpell(s_SerpRapidSpell[0], s_SerpRapidSpell[1], s_SerpRapidSpell[2], s_SerpRapidSpell[3],
				s_SerpRapidSpell[4]),
		sg_SerpSkullSpell(s_SerpSkullSpell[0], s_SerpSkullSpell[1], s_SerpSkullSpell[2], s_SerpSkullSpell[3],
				s_SerpSkullSpell[4]);

		private final State[][] group;
		private int index = -1;

		SerpStateGroup(State[]... states) {
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

	private static final Actor_Action_Set SerpActionSet = new Actor_Action_Set(SerpStateGroup.sg_SerpStand,
			SerpStateGroup.sg_SerpRun, null, // SerpStateGroup.sg_SerpJump,
			null, // SerpStateGroup.sg_SerpFall,
			null, // SerpStateGroup.sg_SerpCrawl,
			null, // SerpStateGroup.sg_SerpSwim,
			null, // SerpStateGroup.sg_SerpFly,
			null, // SerpStateGroup.sg_SerpRise,
			null, // SerpStateGroup.sg_SerpSit,
			null, // SerpStateGroup.sg_SerpLook,
			null, // climb
			null, // pain
			SerpStateGroup.sg_SerpDie, null, // SerpStateGroup.sg_SerpHariKari,
			SerpStateGroup.sg_SerpDead, null, // SerpStateGroup.sg_SerpDeathJump,
			null, // SerpStateGroup.sg_SerpDeathFall,
			new StateGroup[] { SerpStateGroup.sg_SerpSlash }, new short[] { 1024 },
			new StateGroup[] { SerpStateGroup.sg_SerpSlash, SerpStateGroup.sg_SerpSpell,
					SerpStateGroup.sg_SerpRapidSpell, SerpStateGroup.sg_SerpRapidSpell },
			new short[] { 256, 724, 900, 1024 }, null, null, null);

	public static int SetupSerp(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, SERP_RUN_R0, s_SerpRun[0][0]);
			u.Health = HEALTH_SERP_GOD;
		}

		if (Skill == 0)
			u.Health = 1100;
		if (Skill == 1)
			u.Health = 2200;

		ChangeState(SpriteNum, s_SerpRun[0][0]);
		u.Attrib = SerpAttrib;
		DoActorSetSpeed(SpriteNum, NORM_SPEED);
		u.StateEnd = s_SerpDie[0];
		u.Rot = SerpStateGroup.sg_SerpRun;

		EnemyDefaults(SpriteNum, SerpActionSet, SerpPersonality);

		// Mini-Boss Serp
		if (sp.pal == 16) {
			u.Health = 1000;
			sp.yrepeat = 74;
			sp.xrepeat = 74;
		} else {
			sp.yrepeat = 100;
			sp.xrepeat = 128;
		}

		sp.clipdist = (512) >> 2;
		u.Flags |= (SPR_XFLIP_TOGGLE | SPR_ELECTRO_TOLERANT);

		u.loz = sp.z;

		// amount to move up for clipmove
		u.zclip = Z(80);
		// size of step can walk off of
		u.lo_step = (short) Z(40);

		u.floor_dist = (short) (u.zclip - u.lo_step);
		u.ceiling_dist = (short) (SPRITEp_SIZE_Z(sp) - u.zclip);

		return (0);
	}

	private static int NullSerp(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		KeepActorOnFloor(SpriteNum);
		return (0);
	}

	private static int DoSerpMove(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		if (u.track >= 0)
			ActorFollowTrack(SpriteNum, ACTORMOVETICS);
		else
			(u.ActorActionFunc).invoke(SpriteNum);

		// serp ring
		if (sp.pal != 16) {
			switch (u.Counter2) {
			case 0:
				if (u.Health != u.MaxHealth) {
					NewStateGroup(SpriteNum, SerpStateGroup.sg_SerpSkullSpell);
					u.Counter2++;
				}
				break;

			case 1: {
				if (u.Counter <= 0)
					NewStateGroup(SpriteNum, SerpStateGroup.sg_SerpSkullSpell);
			}
				break;
			}
		}

		KeepActorOnFloor(SpriteNum);

		return (0);
	}

	private static boolean alreadydid = false;

	private static int DoDeathSpecial(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];

		DoMatchEverything(null, sp.lotag, ON);

		if (!gs.muteMusic && !alreadydid) {
			CDAudio_Play(RedBookSong[Level], true);
			alreadydid = true;
		}

		BossSpriteNum[0] = -2;
		return (0);
	}
	
	public static void InitSerpStates() {
		for (SerpStateGroup sg : SerpStateGroup.values()) {
			for (int rot = 0; rot < sg.group.length; rot++) {
				State.InitState(sg.group[rot]);
			}
		}
	}

	public static void SerpSaveable()
	{
		SaveData(InitSerpSlash);
		SaveData(InitSerpRing);
		SaveData(InitSerpSpell);
		
		SaveData(NullSerp);
		SaveData(DoSerpMove);
		SaveData(DoDeathSpecial);

		SaveData(SerpPersonality);

		SaveData(SerpAttrib);

		SaveData(s_SerpRun);
		SaveGroup(SerpStateGroup.sg_SerpRun);
		SaveData(s_SerpSlash);
		SaveGroup(SerpStateGroup.sg_SerpSlash);
		SaveData(s_SerpSkullSpell);
		SaveGroup(SerpStateGroup.sg_SerpSkullSpell);
		SaveData(s_SerpSpell);
		SaveGroup(SerpStateGroup.sg_SerpSpell);
		SaveData(s_SerpRapidSpell);
		SaveGroup(SerpStateGroup.sg_SerpRapidSpell);
		SaveData(s_SerpStand);
		SaveGroup(SerpStateGroup.sg_SerpStand);
		SaveData(s_SerpDie);
		SaveData(s_SerpDead);
		SaveGroup(SerpStateGroup.sg_SerpDie);
		SaveGroup(SerpStateGroup.sg_SerpDead);

		SaveData(SerpActionSet);
	}
	
}
