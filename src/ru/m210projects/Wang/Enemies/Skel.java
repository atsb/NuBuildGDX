package ru.m210projects.Wang.Enemies;

import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Wang.Actor.DoActorSectorDamage;
import static ru.m210projects.Wang.Actor.DoActorSlide;
import static ru.m210projects.Wang.Actor.KeepActorOnFloor;
import static ru.m210projects.Wang.Ai.DoActorSetSpeed;
import static ru.m210projects.Wang.Ai.InitActorAlertNoise;
import static ru.m210projects.Wang.Ai.InitActorAmbientNoise;
import static ru.m210projects.Wang.Ai.InitActorAttack;
import static ru.m210projects.Wang.Ai.InitActorDecide;
import static ru.m210projects.Wang.Ai.InitActorDuck;
import static ru.m210projects.Wang.Ai.InitActorEvade;
import static ru.m210projects.Wang.Ai.InitActorFindPlayer;
import static ru.m210projects.Wang.Ai.InitActorMoveCloser;
import static ru.m210projects.Wang.Ai.InitActorReposition;
import static ru.m210projects.Wang.Ai.InitActorRunAway;
import static ru.m210projects.Wang.Ai.InitActorWanderAround;
import static ru.m210projects.Wang.Ai.NORM_SPEED;
import static ru.m210projects.Wang.Digi.DIGI_SPALERT;
import static ru.m210projects.Wang.Digi.DIGI_SPAMBIENT;
import static ru.m210projects.Wang.Digi.DIGI_SPBLADE;
import static ru.m210projects.Wang.Digi.DIGI_SPELEC;
import static ru.m210projects.Wang.Digi.DIGI_SPPAIN;
import static ru.m210projects.Wang.Digi.DIGI_SPSCREAM;
import static ru.m210projects.Wang.Digi.DIGI_SPTELEPORT;
import static ru.m210projects.Wang.Enemies.Coolie.EnemyDefaults;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK_HITSCAN;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_RESTORE;
import static ru.m210projects.Wang.Gameutils.HEALTH_SKEL_PRIEST;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.SF_QUICK_CALL;
import static ru.m210projects.Wang.Gameutils.SPR_SLIDING;
import static ru.m210projects.Wang.Gameutils.SPR_XFLIP_TOGGLE;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Names.SKEL_DIE;
import static ru.m210projects.Wang.Names.SKEL_PAIN_R0;
import static ru.m210projects.Wang.Names.SKEL_PAIN_R1;
import static ru.m210projects.Wang.Names.SKEL_PAIN_R2;
import static ru.m210projects.Wang.Names.SKEL_PAIN_R3;
import static ru.m210projects.Wang.Names.SKEL_PAIN_R4;
import static ru.m210projects.Wang.Names.SKEL_RUN_R0;
import static ru.m210projects.Wang.Names.SKEL_RUN_R1;
import static ru.m210projects.Wang.Names.SKEL_RUN_R2;
import static ru.m210projects.Wang.Names.SKEL_RUN_R3;
import static ru.m210projects.Wang.Names.SKEL_RUN_R4;
import static ru.m210projects.Wang.Names.SKEL_SLASH_R0;
import static ru.m210projects.Wang.Names.SKEL_SLASH_R1;
import static ru.m210projects.Wang.Names.SKEL_SLASH_R2;
import static ru.m210projects.Wang.Names.SKEL_SLASH_R3;
import static ru.m210projects.Wang.Names.SKEL_SLASH_R4;
import static ru.m210projects.Wang.Names.SKEL_SPELL_R0;
import static ru.m210projects.Wang.Names.SKEL_SPELL_R1;
import static ru.m210projects.Wang.Names.SKEL_SPELL_R2;
import static ru.m210projects.Wang.Names.SKEL_SPELL_R3;
import static ru.m210projects.Wang.Names.SKEL_SPELL_R4;
import static ru.m210projects.Wang.Names.SKEL_TELEPORT;
import static ru.m210projects.Wang.Sound.PlaySpriteSound;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Track.ActorFollowTrack;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Type.Saveable.SaveGroup;
import static ru.m210projects.Wang.Weapon.DoSuicide;
import static ru.m210projects.Wang.Weapon.InitSkelSlash;
import static ru.m210projects.Wang.Weapon.InitSkelSpell;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Ai.Attrib_Snds;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Type.ATTRIBUTE;
import ru.m210projects.Wang.Type.Actor_Action_Set;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;

public class Skel {

	public enum SkelStateGroup implements StateGroup {
		sg_SkelRun(s_SkelRun[0], s_SkelRun[1], s_SkelRun[2], s_SkelRun[3], s_SkelRun[4]),
		sg_SkelTeleport(s_SkelTeleport, s_SkelTeleport, s_SkelTeleport, s_SkelTeleport, s_SkelTeleport),
		sg_SkelSpell(s_SkelSpell[0], s_SkelSpell[1], s_SkelSpell[2], s_SkelSpell[3], s_SkelSpell[4]),
		sg_SkelDie(s_SkelDie),
		sg_SkelSlash(s_SkelSlash[0], s_SkelSlash[1], s_SkelSlash[2], s_SkelSlash[3], s_SkelSlash[4]),
		sg_SkelPain(s_SkelPain[0], s_SkelPain[1], s_SkelPain[2], s_SkelPain[3], s_SkelPain[4]),
		sg_SkelStand(s_SkelStand[0], s_SkelStand[1], s_SkelStand[2], s_SkelStand[3], s_SkelStand[4]);

		private final State[][] group;
		private int index = -1;

		SkelStateGroup(State[]... states) {
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

	public static void InitSkelStates() {
		for (SkelStateGroup sg : SkelStateGroup.values()) {
			for (int rot = 0; rot < sg.group.length; rot++) {
				State.InitState(sg.group[rot]);
			}
		}
	}

	private static final Decision SkelBattle[] = { new Decision(600, InitActorMoveCloser),
			new Decision(602, InitActorAlertNoise), new Decision(700, InitActorRunAway),
			new Decision(1024, InitActorAttack) };

	private static final Decision SkelOffense[] = { new Decision(700, InitActorMoveCloser),
			new Decision(702, InitActorAlertNoise), new Decision(1024, InitActorAttack) };

	private static final Decision SkelBroadcast[] = { new Decision(3, InitActorAlertNoise),
			new Decision(6, InitActorAmbientNoise), new Decision(1024, InitActorDecide) };

	private static final Decision SkelSurprised[] = { new Decision(701, InitActorMoveCloser),
			new Decision(1024, InitActorDecide) };

	private static final Decision SkelEvasive[] = { new Decision(22, InitActorDuck), new Decision(30, InitActorEvade),
			new Decision(1024, null), };

	private static final Decision SkelLostTarget[] = { new Decision(900, InitActorFindPlayer),
			new Decision(1024, InitActorWanderAround) };

	private static final Decision SkelCloseRange[] = { new Decision(800, InitActorAttack),
			new Decision(1024, InitActorReposition) };

	private static final Personality SkelPersonality = new Personality(SkelBattle, SkelOffense, SkelBroadcast,
			SkelSurprised, SkelEvasive, SkelLostTarget, SkelCloseRange, SkelCloseRange);

	private static final ATTRIBUTE SkelAttrib = new ATTRIBUTE(new short[] { 60, 80, 100, 130 }, // Speeds
			new short[] { 3, 0, -2, -3 }, // Tic Adjusts
			3, // MaxWeapons;
			new int[] { DIGI_SPAMBIENT, DIGI_SPALERT, 0, DIGI_SPPAIN, DIGI_SPSCREAM, DIGI_SPBLADE, DIGI_SPELEC,
					DIGI_SPTELEPORT, 0, 0 });

	//////////////////////
	//
	// SKEL RUN
	//
	//////////////////////

	public static final int SKEL_RUN_RATE = 12;

	// +4 on frame #3 to add character

	private static final Animator DoSkelMove = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoSkelMove(SpriteNum) != 0;
		}
	};

	private static final State s_SkelRun[][] = { { new State(SKEL_RUN_R0 + 0, SKEL_RUN_RATE + 4, DoSkelMove), // s_SkelRun[0][1]},
			new State(SKEL_RUN_R0 + 1, SKEL_RUN_RATE, DoSkelMove), // s_SkelRun[0][2]},
			new State(SKEL_RUN_R0 + 2, SKEL_RUN_RATE, DoSkelMove), // s_SkelRun[0][3]},
			new State(SKEL_RUN_R0 + 3, SKEL_RUN_RATE + 4, DoSkelMove), // s_SkelRun[0][4]},
			new State(SKEL_RUN_R0 + 4, SKEL_RUN_RATE, DoSkelMove), // s_SkelRun[0][5]},
			new State(SKEL_RUN_R0 + 5, SKEL_RUN_RATE, DoSkelMove),// s_SkelRun[0][0]},
			}, { new State(SKEL_RUN_R1 + 0, SKEL_RUN_RATE + 4, DoSkelMove), // s_SkelRun[1][1]},
					new State(SKEL_RUN_R1 + 1, SKEL_RUN_RATE, DoSkelMove), // s_SkelRun[1][2]},
					new State(SKEL_RUN_R1 + 2, SKEL_RUN_RATE, DoSkelMove), // s_SkelRun[1][3]},
					new State(SKEL_RUN_R1 + 3, SKEL_RUN_RATE + 4, DoSkelMove), // s_SkelRun[1][4]},
					new State(SKEL_RUN_R1 + 4, SKEL_RUN_RATE, DoSkelMove), // s_SkelRun[1][5]},
					new State(SKEL_RUN_R1 + 5, SKEL_RUN_RATE, DoSkelMove),// s_SkelRun[1][0]},
			}, { new State(SKEL_RUN_R2 + 0, SKEL_RUN_RATE + 4, DoSkelMove), // s_SkelRun[2][1]},
					new State(SKEL_RUN_R2 + 1, SKEL_RUN_RATE, DoSkelMove), // s_SkelRun[2][2]},
					new State(SKEL_RUN_R2 + 2, SKEL_RUN_RATE, DoSkelMove), // s_SkelRun[2][3]},
					new State(SKEL_RUN_R2 + 3, SKEL_RUN_RATE + 4, DoSkelMove), // s_SkelRun[2][4]},
					new State(SKEL_RUN_R2 + 4, SKEL_RUN_RATE, DoSkelMove), // s_SkelRun[2][5]},
					new State(SKEL_RUN_R2 + 5, SKEL_RUN_RATE, DoSkelMove),// s_SkelRun[2][0]},
			}, { new State(SKEL_RUN_R3 + 0, SKEL_RUN_RATE + 4, DoSkelMove), // s_SkelRun[3][1]},
					new State(SKEL_RUN_R3 + 1, SKEL_RUN_RATE, DoSkelMove), // s_SkelRun[3][2]},
					new State(SKEL_RUN_R3 + 2, SKEL_RUN_RATE, DoSkelMove), // s_SkelRun[3][3]},
					new State(SKEL_RUN_R3 + 3, SKEL_RUN_RATE + 4, DoSkelMove), // s_SkelRun[3][4]},
					new State(SKEL_RUN_R3 + 4, SKEL_RUN_RATE, DoSkelMove), // s_SkelRun[3][5]},
					new State(SKEL_RUN_R3 + 5, SKEL_RUN_RATE, DoSkelMove),// s_SkelRun[3][0]},
			}, { new State(SKEL_RUN_R4 + 0, SKEL_RUN_RATE + 4, DoSkelMove), // s_SkelRun[4][1]},
					new State(SKEL_RUN_R4 + 1, SKEL_RUN_RATE, DoSkelMove), // s_SkelRun[4][2]},
					new State(SKEL_RUN_R4 + 2, SKEL_RUN_RATE, DoSkelMove), // s_SkelRun[4][3]},
					new State(SKEL_RUN_R4 + 3, SKEL_RUN_RATE + 4, DoSkelMove), // s_SkelRun[4][4]},
					new State(SKEL_RUN_R4 + 4, SKEL_RUN_RATE, DoSkelMove), // s_SkelRun[4][5]},
					new State(SKEL_RUN_R4 + 5, SKEL_RUN_RATE, DoSkelMove),// s_SkelRun[4][0]},
			} };

	//////////////////////
	//
	// SKEL SLASH
	//
	//////////////////////

	public static final int SKEL_SLASH_RATE = 20;

	private static final Animator NullSkel = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return NullSkel(SpriteNum) != 0;
		}
	};

	private static final Animator InitSkelSlash = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitSkelSlash(SpriteNum) != 0;
		}
	};

	private static final State s_SkelSlash[][] = { { new State(SKEL_SLASH_R0 + 0, SKEL_SLASH_RATE, NullSkel), // s_SkelSlash[0][1]},
			new State(SKEL_SLASH_R0 + 1, SKEL_SLASH_RATE, NullSkel), // s_SkelSlash[0][2]},
			new State(SKEL_SLASH_R0 + 2, 0 | SF_QUICK_CALL, InitSkelSlash), // s_SkelSlash[0][3]},
			new State(SKEL_SLASH_R0 + 2, SKEL_SLASH_RATE * 2, NullSkel), // s_SkelSlash[0][4]},
			new State(SKEL_SLASH_R0 + 1, SKEL_SLASH_RATE, NullSkel), // s_SkelSlash[0][5]},
			new State(SKEL_SLASH_R0 + 1, 0 | SF_QUICK_CALL, InitActorDecide), // s_SkelSlash[0][6]},
			new State(SKEL_SLASH_R0 + 1, SKEL_SLASH_RATE, DoSkelMove).setNext(),// s_SkelSlash[0][6]},
			}, { new State(SKEL_SLASH_R1 + 0, SKEL_SLASH_RATE, NullSkel), // s_SkelSlash[1][1]},
					new State(SKEL_SLASH_R1 + 1, SKEL_SLASH_RATE, NullSkel), // s_SkelSlash[1][2]},
					new State(SKEL_SLASH_R1 + 2, 0 | SF_QUICK_CALL, InitSkelSlash), // s_SkelSlash[1][3]},
					new State(SKEL_SLASH_R1 + 2, SKEL_SLASH_RATE * 2, NullSkel), // s_SkelSlash[1][4]},
					new State(SKEL_SLASH_R1 + 1, SKEL_SLASH_RATE, NullSkel), // s_SkelSlash[1][5]},
					new State(SKEL_SLASH_R1 + 1, 0 | SF_QUICK_CALL, InitActorDecide), // s_SkelSlash[1][6]},
					new State(SKEL_SLASH_R1 + 1, SKEL_SLASH_RATE, DoSkelMove).setNext(),// s_SkelSlash[1][6]},
			}, { new State(SKEL_SLASH_R2 + 0, SKEL_SLASH_RATE, NullSkel), // s_SkelSlash[2][1]},
					new State(SKEL_SLASH_R2 + 1, SKEL_SLASH_RATE, NullSkel), // s_SkelSlash[2][2]},
					new State(SKEL_SLASH_R2 + 2, 0 | SF_QUICK_CALL, InitSkelSlash), // s_SkelSlash[2][3]},
					new State(SKEL_SLASH_R2 + 2, SKEL_SLASH_RATE * 2, NullSkel), // s_SkelSlash[2][4]},
					new State(SKEL_SLASH_R2 + 1, SKEL_SLASH_RATE, NullSkel), // s_SkelSlash[2][5]},
					new State(SKEL_SLASH_R2 + 1, 0 | SF_QUICK_CALL, InitActorDecide), // s_SkelSlash[2][6]},
					new State(SKEL_SLASH_R2 + 1, SKEL_SLASH_RATE, DoSkelMove).setNext(),// s_SkelSlash[2][6]},
			}, { new State(SKEL_SLASH_R3 + 0, SKEL_SLASH_RATE, NullSkel), // s_SkelSlash[3][1]},
					new State(SKEL_SLASH_R3 + 1, SKEL_SLASH_RATE, NullSkel), // s_SkelSlash[3][2]},
					new State(SKEL_SLASH_R3 + 2, 0 | SF_QUICK_CALL, InitSkelSlash), // s_SkelSlash[3][3]},
					new State(SKEL_SLASH_R3 + 2, SKEL_SLASH_RATE * 2, NullSkel), // s_SkelSlash[3][4]},
					new State(SKEL_SLASH_R3 + 1, SKEL_SLASH_RATE, NullSkel), // s_SkelSlash[3][5]},
					new State(SKEL_SLASH_R3 + 1, 0 | SF_QUICK_CALL, InitActorDecide), // s_SkelSlash[3][6]},
					new State(SKEL_SLASH_R3 + 1, SKEL_SLASH_RATE, DoSkelMove).setNext(),// s_SkelSlash[3][6]},
			}, { new State(SKEL_SLASH_R4 + 0, SKEL_SLASH_RATE, NullSkel), // s_SkelSlash[4][1]},
					new State(SKEL_SLASH_R4 + 1, SKEL_SLASH_RATE, NullSkel), // s_SkelSlash[4][2]},
					new State(SKEL_SLASH_R4 + 2, 0 | SF_QUICK_CALL, InitSkelSlash), // s_SkelSlash[4][3]},
					new State(SKEL_SLASH_R4 + 2, SKEL_SLASH_RATE * 2, NullSkel), // s_SkelSlash[4][4]},
					new State(SKEL_SLASH_R4 + 1, SKEL_SLASH_RATE, NullSkel), // s_SkelSlash[4][5]},
					new State(SKEL_SLASH_R4 + 1, 0 | SF_QUICK_CALL, InitActorDecide), // s_SkelSlash[4][6]},
					new State(SKEL_SLASH_R4 + 1, SKEL_SLASH_RATE, DoSkelMove).setNext(),// s_SkelSlash[4][6]},
			} };

	//////////////////////
	//
	// SKEL SPELL
	//
	//////////////////////

	public static final int SKEL_SPELL_RATE = 20;

	private static final Animator InitSkelSpell = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitSkelSpell(SpriteNum) != 0;
		}
	};

	private static final State s_SkelSpell[][] = { { new State(SKEL_SPELL_R0 + 0, SKEL_SPELL_RATE + 9, NullSkel), // s_SkelSpell[0][1]},
			new State(SKEL_SPELL_R0 + 1, SKEL_SPELL_RATE, NullSkel), // s_SkelSpell[0][2]},
			new State(SKEL_SPELL_R0 + 2, SKEL_SPELL_RATE, NullSkel), // s_SkelSpell[0][3]},
			new State(SKEL_SPELL_R0 + 3, SKEL_SPELL_RATE * 2, NullSkel), // s_SkelSpell[0][4]},
			new State(SKEL_SPELL_R0 + 3, 0 | SF_QUICK_CALL, InitSkelSpell), // s_SkelSpell[0][5]},
			new State(SKEL_SPELL_R0 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_SkelSpell[0][6]},
			new State(SKEL_SPELL_R0 + 3, SKEL_SPELL_RATE, DoSkelMove).setNext(),// s_SkelSpell[0][6]},
			}, { new State(SKEL_SPELL_R1 + 0, SKEL_SPELL_RATE + 9, NullSkel), // s_SkelSpell[1][1]},
					new State(SKEL_SPELL_R1 + 1, SKEL_SPELL_RATE, NullSkel), // s_SkelSpell[1][2]},
					new State(SKEL_SPELL_R1 + 2, SKEL_SPELL_RATE, NullSkel), // s_SkelSpell[1][3]},
					new State(SKEL_SPELL_R1 + 3, SKEL_SPELL_RATE * 2, NullSkel), // s_SkelSpell[1][4]},
					new State(SKEL_SPELL_R1 + 3, 0 | SF_QUICK_CALL, InitSkelSpell), // s_SkelSpell[1][5]},
					new State(SKEL_SPELL_R1 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_SkelSpell[1][6]},
					new State(SKEL_SPELL_R1 + 3, SKEL_SPELL_RATE, DoSkelMove).setNext(),// s_SkelSpell[1][6]},
			}, { new State(SKEL_SPELL_R2 + 0, SKEL_SPELL_RATE + 9, NullSkel), // s_SkelSpell[2][1]},
					new State(SKEL_SPELL_R2 + 1, SKEL_SPELL_RATE, NullSkel), // s_SkelSpell[2][2]},
					new State(SKEL_SPELL_R2 + 2, SKEL_SPELL_RATE, NullSkel), // s_SkelSpell[2][3]},
					new State(SKEL_SPELL_R2 + 3, SKEL_SPELL_RATE * 2, NullSkel), // s_SkelSpell[2][4]},
					new State(SKEL_SPELL_R2 + 3, 0 | SF_QUICK_CALL, InitSkelSpell), // s_SkelSpell[2][5]},
					new State(SKEL_SPELL_R2 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_SkelSpell[2][6]},
					new State(SKEL_SPELL_R2 + 3, SKEL_SPELL_RATE, DoSkelMove).setNext(),// s_SkelSpell[2][6]},
			}, { new State(SKEL_SPELL_R3 + 0, SKEL_SPELL_RATE + 9, NullSkel), // s_SkelSpell[3][1]},
					new State(SKEL_SPELL_R3 + 1, SKEL_SPELL_RATE, NullSkel), // s_SkelSpell[3][2]},
					new State(SKEL_SPELL_R3 + 2, SKEL_SPELL_RATE, NullSkel), // s_SkelSpell[3][3]},
					new State(SKEL_SPELL_R3 + 3, SKEL_SPELL_RATE * 2, NullSkel), // s_SkelSpell[3][4]},
					new State(SKEL_SPELL_R3 + 3, 0 | SF_QUICK_CALL, InitSkelSpell), // s_SkelSpell[3][5]},
					new State(SKEL_SPELL_R3 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_SkelSpell[3][6]},
					new State(SKEL_SPELL_R3 + 3, SKEL_SPELL_RATE, DoSkelMove).setNext(),// s_SkelSpell[3][6]},
			}, { new State(SKEL_SPELL_R4 + 0, SKEL_SPELL_RATE + 9, NullSkel), // s_SkelSpell[4][1]},
					new State(SKEL_SPELL_R4 + 1, SKEL_SPELL_RATE, NullSkel), // s_SkelSpell[4][2]},
					new State(SKEL_SPELL_R4 + 2, SKEL_SPELL_RATE, NullSkel), // s_SkelSpell[4][3]},
					new State(SKEL_SPELL_R4 + 3, SKEL_SPELL_RATE * 2, NullSkel), // s_SkelSpell[4][4]},
					new State(SKEL_SPELL_R4 + 3, 0 | SF_QUICK_CALL, InitSkelSpell), // s_SkelSpell[4][5]},
					new State(SKEL_SPELL_R4 + 3, 0 | SF_QUICK_CALL, InitActorDecide), // s_SkelSpell[4][6]},
					new State(SKEL_SPELL_R4 + 3, SKEL_SPELL_RATE, DoSkelMove).setNext(),// s_SkelSpell[4][6]},
			} };

	//////////////////////
	//
	// SKEL PAIN
	//
	//////////////////////

	public static final int SKEL_PAIN_RATE = 38;

	private static final Animator DoSkelPain = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoSkelPain(SpriteNum) != 0;
		}
	};

	private static final State s_SkelPain[][] = { { new State(SKEL_PAIN_R0 + 0, SKEL_PAIN_RATE, DoSkelPain).setNext(),// s_SkelPain[0][0]},
			}, { new State(SKEL_PAIN_R1 + 0, SKEL_PAIN_RATE, DoSkelPain).setNext(),// s_SkelPain[1][0]},
			}, { new State(SKEL_PAIN_R2 + 0, SKEL_PAIN_RATE, DoSkelPain).setNext(),// s_SkelPain[2][0]},
			}, { new State(SKEL_PAIN_R3 + 0, SKEL_PAIN_RATE, DoSkelPain).setNext(),// s_SkelPain[3][0]},
			}, { new State(SKEL_PAIN_R4 + 0, SKEL_PAIN_RATE, DoSkelPain).setNext(),// s_SkelPain[4][0]},
			} };

	//////////////////////
	//
	// SKEL TELEPORT
	//
	//////////////////////

	public static final int SKEL_TELEPORT_RATE = 20;

	private static final Animator DoSkelInitTeleport = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoSkelInitTeleport(SpriteNum) != 0;
		}
	};

	private static final Animator DoSkelTeleport = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoSkelTeleport(SpriteNum) != 0;
		}
	};

	private static final Animator DoSkelTermTeleport = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return DoSkelTermTeleport(SpriteNum) != 0;
		}
	};

	private static final State s_SkelTeleport[] = { new State(SKEL_TELEPORT + 0, 1, null), // s_SkelTeleport[1]},
			new State(SKEL_TELEPORT + 0, 0 | SF_QUICK_CALL, DoSkelInitTeleport), // s_SkelTeleport[2]},
			new State(SKEL_TELEPORT + 0, SKEL_TELEPORT_RATE, null), // s_SkelTeleport[3]},
			new State(SKEL_TELEPORT + 1, SKEL_TELEPORT_RATE, null), // s_SkelTeleport[4]},
			new State(SKEL_TELEPORT + 2, SKEL_TELEPORT_RATE, null), // s_SkelTeleport[5]},
			new State(SKEL_TELEPORT + 3, SKEL_TELEPORT_RATE, null), // s_SkelTeleport[6]},
			new State(SKEL_TELEPORT + 4, SKEL_TELEPORT_RATE, null), // s_SkelTeleport[7]},
			new State(SKEL_TELEPORT + 5, SKEL_TELEPORT_RATE, null), // s_SkelTeleport[8]},

			new State(SKEL_TELEPORT + 5, 0 | SF_QUICK_CALL, DoSkelTeleport), // s_SkelTeleport[9]},

			new State(SKEL_TELEPORT + 5, SKEL_TELEPORT_RATE, null), // s_SkelTeleport[10]},
			new State(SKEL_TELEPORT + 4, SKEL_TELEPORT_RATE, null), // s_SkelTeleport[11]},
			new State(SKEL_TELEPORT + 3, SKEL_TELEPORT_RATE, null), // s_SkelTeleport[12]},
			new State(SKEL_TELEPORT + 2, SKEL_TELEPORT_RATE, null), // s_SkelTeleport[13]},
			new State(SKEL_TELEPORT + 1, SKEL_TELEPORT_RATE, null), // s_SkelTeleport[14]},
			new State(SKEL_TELEPORT + 0, SKEL_TELEPORT_RATE, DoSkelTermTeleport), // s_SkelTeleport[15]},
			new State(SKEL_TELEPORT + 0, 0 | SF_QUICK_CALL, InitActorDecide), // s_SkelTeleport[16]},
			new State(SKEL_TELEPORT + 0, SKEL_TELEPORT_RATE, DoSkelMove).setNext(),// s_SkelTeleport[16]},
	};

	//////////////////////
	//
	// SKEL STAND
	//
	//////////////////////

	public static final int SKEL_STAND_RATE = 12;

	private static final State s_SkelStand[][] = { { new State(SKEL_RUN_R0 + 0, SKEL_STAND_RATE, DoSkelMove).setNext(),// s_SkelStand[0][0]},
			}, { new State(SKEL_RUN_R1 + 0, SKEL_STAND_RATE, DoSkelMove).setNext(),// s_SkelStand[1][0]},
			}, { new State(SKEL_RUN_R2 + 0, SKEL_STAND_RATE, DoSkelMove).setNext(),// s_SkelStand[2][0]},
			}, { new State(SKEL_RUN_R3 + 0, SKEL_STAND_RATE, DoSkelMove).setNext(),// s_SkelStand[3][0]},
			}, { new State(SKEL_RUN_R4 + 0, SKEL_STAND_RATE, DoSkelMove).setNext(),// s_SkelStand[4][0]},
			}, };

	//////////////////////
	//
	// SKEL DIE
	//
	//////////////////////

	public static final int SKEL_DIE_RATE = 25;

	private static final State s_SkelDie[] = { new State(SKEL_DIE + 0, SKEL_DIE_RATE, null), // s_SkelDie[1]},
			new State(SKEL_DIE + 1, SKEL_DIE_RATE, null), // s_SkelDie[2]},
			new State(SKEL_DIE + 2, SKEL_DIE_RATE, null), // s_SkelDie[3]},
			new State(SKEL_DIE + 3, SKEL_DIE_RATE, null), // s_SkelDie[4]},
			new State(SKEL_DIE + 4, SKEL_DIE_RATE, null), // s_SkelDie[5]},
			new State(SKEL_DIE + 5, SKEL_DIE_RATE, DoSuicide).setNext(),// s_SkelDie[5]},
	};

	private static final Actor_Action_Set SkelActionSet = new Actor_Action_Set(SkelStateGroup.sg_SkelStand,
			SkelStateGroup.sg_SkelRun, null, // SkelStateGroup.sg_SkelJump,
			null, // SkelStateGroup.sg_SkelFall,
			null, // SkelStateGroup.sg_SkelCrawl,
			null, // SkelStateGroup.sg_SkelSwim,
			null, // SkelStateGroup.sg_SkelFly,
			null, // SkelStateGroup.sg_SkelRise,
			null, // SkelStateGroup.sg_SkelSit,
			null, // SkelStateGroup.sg_SkelLook,
			null, // climb
			SkelStateGroup.sg_SkelPain, // pain
			SkelStateGroup.sg_SkelDie, null, // SkelStateGroup.sg_SkelHariKari,
			null, // SkelStateGroup.sg_SkelDead,
			null, // SkelStateGroup.sg_SkelDeathJump,
			null, // SkelStateGroup.sg_SkelDeathFall,
			new StateGroup[] { SkelStateGroup.sg_SkelSlash }, new short[] { 1024 },
			new StateGroup[] { SkelStateGroup.sg_SkelSpell }, new short[] { 1024 }, null,
			SkelStateGroup.sg_SkelTeleport, null);

	public static int SetupSkel(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u;

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE)) {
			u = pUser[SpriteNum];
		} else {
			pUser[SpriteNum] = u = SpawnUser(SpriteNum, SKEL_RUN_R0, s_SkelRun[0][0]);
			u.Health = HEALTH_SKEL_PRIEST;
		}

		ChangeState(SpriteNum, s_SkelRun[0][0]);
		u.Attrib = SkelAttrib;
		DoActorSetSpeed(SpriteNum, NORM_SPEED);
		u.StateEnd = s_SkelDie[0];
		u.Rot = SkelStateGroup.sg_SkelRun;

		EnemyDefaults(SpriteNum, SkelActionSet, SkelPersonality);
		u.Flags |= (SPR_XFLIP_TOGGLE);

		return (0);
	}

	private static int DoSkelInitTeleport(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		PlaySpriteSound(SpriteNum, Attrib_Snds.attr_extra3, v3df_follow);

		return (0);
	}

	private static int DoSkelTeleport(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		int x, y;

		x = sp.x;
		y = sp.y;

		while (true) {
			sp.x = x;
			sp.y = y;

			if (RANDOM_P2(1024) < 512)
				sp.x += 512 + RANDOM_P2(1024);
			else
				sp.x -= 512 + RANDOM_P2(1024);

			if (RANDOM_P2(1024) < 512)
				sp.y += 512 + RANDOM_P2(1024);
			else
				sp.y -= 512 + RANDOM_P2(1024);

			engine.setspritez(SpriteNum, sp.x, sp.y, sp.z);

			if (sp.sectnum != -1)
				break;
		}

		return (0);
	}

	private static int DoSkelTermTeleport(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];

		sp.cstat |= (CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		return (0);
	}

	private static int NullSkel(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		KeepActorOnFloor(SpriteNum);
		DoActorSectorDamage(SpriteNum);

		return (0);
	}

	private static int DoSkelPain(int SpriteNum) {
		USER u = pUser[SpriteNum];

		NullSkel(SpriteNum);

		if ((u.WaitTics -= ACTORMOVETICS) <= 0)
			InitActorDecide(SpriteNum);

		return (0);
	}

	private static int DoSkelMove(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (TEST(u.Flags, SPR_SLIDING))
			DoActorSlide(SpriteNum);

		if (u.track >= 0)
			ActorFollowTrack(SpriteNum, ACTORMOVETICS);
		else
			(u.ActorActionFunc).invoke(SpriteNum);

		KeepActorOnFloor(SpriteNum);

		DoActorSectorDamage(SpriteNum);

		return (0);
	}
	
	public static void SkelSaveable()
	{
		SaveData(InitSkelSlash);
		SaveData(InitSkelSpell);
		SaveData(DoSkelInitTeleport);
		SaveData(DoSkelTeleport);
		SaveData(DoSkelTermTeleport);
		SaveData(NullSkel);
		SaveData(DoSkelPain);
		SaveData(DoSkelMove);

		SaveData(SkelPersonality);

		SaveData(SkelAttrib);

		SaveData(s_SkelRun);
		SaveGroup(SkelStateGroup.sg_SkelRun);
		SaveData(s_SkelSlash);
		SaveGroup(SkelStateGroup.sg_SkelSlash);
		SaveData(s_SkelSpell);
		SaveGroup(SkelStateGroup.sg_SkelSpell);
		SaveData(s_SkelPain);
		SaveGroup(SkelStateGroup.sg_SkelPain);
		SaveData(s_SkelTeleport);
		SaveGroup(SkelStateGroup.sg_SkelTeleport);
		SaveData(s_SkelStand);
		SaveGroup(SkelStateGroup.sg_SkelStand);
		SaveData(s_SkelDie);
		SaveGroup(SkelStateGroup.sg_SkelDie);

		SaveData(SkelActionSet);
	}
}
