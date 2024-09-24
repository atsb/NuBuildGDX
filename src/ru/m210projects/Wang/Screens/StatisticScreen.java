package ru.m210projects.Wang.Screens;

import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Wang.Digi.DIGI_BONUS_GRAB;
import static ru.m210projects.Wang.Digi.DIGI_ENDLEV;
import static ru.m210projects.Wang.Digi.DIGI_PLAYERYELL2;
import static ru.m210projects.Wang.Digi.DIGI_PLAYERYELL3;
import static ru.m210projects.Wang.Game.ANIM_ZILLA;
import static ru.m210projects.Wang.Game.FinishAnim;
import static ru.m210projects.Wang.Game.Kills;
import static ru.m210projects.Wang.Game.Level;
import static ru.m210projects.Wang.Game.PlayClock;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.PlayingLevel;
import static ru.m210projects.Wang.Game.TotalKillable;
import static ru.m210projects.Wang.Game.currentGame;
import static ru.m210projects.Wang.Game.getMapName;
import static ru.m210projects.Wang.Gameutils.RS_SCALE;
import static ru.m210projects.Wang.Gameutils.SF_QUICK_CALL;
import static ru.m210projects.Wang.Gameutils.STD_RANDOM_RANGE;
import static ru.m210projects.Wang.Main.gCreditsScreen;
import static ru.m210projects.Wang.Main.gGameScreen;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Main.mUserFlag;
import static ru.m210projects.Wang.Sector.LevelSecrets;
import static ru.m210projects.Wang.Sound.CDAudio_Play;
import static ru.m210projects.Wang.Sound.CDAudio_Playing;
import static ru.m210projects.Wang.Sound.COVER_SetReverb;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.StopSound;
import static ru.m210projects.Wang.Sound.v3df_none;
import static ru.m210projects.Wang.Type.MyTypes.TEST;

import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.ScreenAdapters.SkippableAdapter;

import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Main.UserFlag;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.LevelInfo;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.VOC3D;

public class StatisticScreen extends SkippableAdapter {

	private final int BONUS_SCREEN_PIC = 5120;
	private final int BONUS_PUNCH = 5121;
	private final int BONUS_KICK = 5136;
	private final int BONUS_GRAB = 5151;
	private final int BONUS_REST = 5121;
	private final int BONUS_TICS = 8;
	private final int BONUS_GRAB_TICS = 20;
	private final int BONUS_REST_TICS = 50;

	private Animator BonusPunchSound = new Animator() {
		@Override
		public boolean invoke(int spr) {
			PlaySound(DIGI_PLAYERYELL3, null, v3df_none);
			return false;
		}
	};

	private State s_BonusPunch[] = { new State(BONUS_PUNCH + 0, BONUS_TICS, null), // s_BonusPunch[1]},
			new State(BONUS_PUNCH + 1, BONUS_TICS, null), // s_BonusPunch[2]},
			new State(BONUS_PUNCH + 2, BONUS_TICS, null), // s_BonusPunch[3]},
			new State(BONUS_PUNCH + 2, 0 | SF_QUICK_CALL, BonusPunchSound), // s_BonusPunch[4]},
			new State(BONUS_PUNCH + 3, BONUS_TICS, null), // s_BonusPunch[5]},
			new State(BONUS_PUNCH + 4, BONUS_TICS, null), // s_BonusPunch[6]},
			new State(BONUS_PUNCH + 5, BONUS_TICS, null), // s_BonusPunch[7]},
			new State(BONUS_PUNCH + 6, BONUS_TICS, null), // s_BonusPunch[8]},
			new State(BONUS_PUNCH + 7, BONUS_TICS, null), // s_BonusPunch[9]},
			new State(BONUS_PUNCH + 8, BONUS_TICS, null), // s_BonusPunch[10]},
			new State(BONUS_PUNCH + 9, BONUS_TICS, null), // s_BonusPunch[11]},
			new State(BONUS_PUNCH + 10, BONUS_TICS, null), // s_BonusPunch[12]},
			new State(BONUS_PUNCH + 11, BONUS_TICS, null), // s_BonusPunch[13]},
			new State(BONUS_PUNCH + 12, BONUS_TICS, null), // s_BonusPunch[14]},
			new State(BONUS_PUNCH + 14, 90, null), // s_BonusPunch[15]},
			new State(BONUS_PUNCH + 14, BONUS_TICS, null).setNext(), // s_BonusPunch[15]},
	};

	private Animator BonusKickSound = new Animator() {
		@Override
		public boolean invoke(int spr) {
			PlaySound(DIGI_PLAYERYELL2, null, v3df_none);
			return false;
		}
	};

	private State s_BonusKick[] = { new State(BONUS_KICK + 0, BONUS_TICS, null), // s_BonusKick[1]},
			new State(BONUS_KICK + 1, BONUS_TICS, null), // s_BonusKick[2]},
			new State(BONUS_KICK + 2, BONUS_TICS, null), // s_BonusKick[3]},
			new State(BONUS_KICK + 2, 0 | SF_QUICK_CALL, BonusKickSound), // s_BonusKick[4]},
			new State(BONUS_KICK + 3, BONUS_TICS, null), // s_BonusKick[5]},
			new State(BONUS_KICK + 4, BONUS_TICS, null), // s_BonusKick[6]},
			new State(BONUS_KICK + 5, BONUS_TICS, null), // s_BonusKick[7]},
			new State(BONUS_KICK + 6, BONUS_TICS, null), // s_BonusKick[8]},
			new State(BONUS_KICK + 7, BONUS_TICS, null), // s_BonusKick[9]},
			new State(BONUS_KICK + 8, BONUS_TICS, null), // s_BonusKick[10]},
			new State(BONUS_KICK + 9, BONUS_TICS, null), // s_BonusKick[11]},
			new State(BONUS_KICK + 10, BONUS_TICS, null), // s_BonusKick[12]},
			new State(BONUS_KICK + 11, BONUS_TICS, null), // s_BonusKick[13]},
			new State(BONUS_KICK + 12, BONUS_TICS, null), // s_BonusKick[14]},
			new State(BONUS_KICK + 14, 90, null), // s_BonusKick[15]},
			new State(BONUS_KICK + 14, BONUS_TICS, null).setNext(), // s_BonusKick[15]},
	};

	private Animator BonusGrabSound = new Animator() {
		@Override
		public boolean invoke(int spr) {
			PlaySound(DIGI_BONUS_GRAB, null, v3df_none);
			return false;
		}
	};

	private State s_BonusGrab[] = { new State(BONUS_GRAB + 0, BONUS_GRAB_TICS, null), // s_BonusGrab[1]},
			new State(BONUS_GRAB + 1, BONUS_GRAB_TICS, null), // s_BonusGrab[2]},
			new State(BONUS_GRAB + 2, BONUS_GRAB_TICS, null), // s_BonusGrab[3]},
			new State(BONUS_GRAB + 2, 0 | SF_QUICK_CALL, BonusGrabSound), // s_BonusGrab[4]},
			new State(BONUS_GRAB + 3, BONUS_GRAB_TICS, null), // s_BonusGrab[5]},
			new State(BONUS_GRAB + 4, BONUS_GRAB_TICS, null), // s_BonusGrab[6]},
			new State(BONUS_GRAB + 5, BONUS_GRAB_TICS, null), // s_BonusGrab[7]},
			new State(BONUS_GRAB + 6, BONUS_GRAB_TICS, null), // s_BonusGrab[8]},
			new State(BONUS_GRAB + 7, BONUS_GRAB_TICS, null), // s_BonusGrab[9]},
			new State(BONUS_GRAB + 8, BONUS_GRAB_TICS, null), // s_BonusGrab[10]},
			new State(BONUS_GRAB + 9, 90, null), // s_BonusGrab[11]},
			new State(BONUS_GRAB + 9, BONUS_GRAB_TICS, null).setNext(), // s_BonusGrab[11]},
	};

	// Turned off the standing animate because he looks like a FAG!
	private State s_BonusRest[] = { new State(BONUS_REST + 0, BONUS_REST_TICS, null), // s_BonusRest[1]},
			new State(BONUS_REST + 1, BONUS_REST_TICS, null), // s_BonusRest[2]},
			new State(BONUS_REST + 2, BONUS_REST_TICS, null), // s_BonusRest[3]},
			new State(BONUS_REST + 1, BONUS_REST_TICS, null), // s_BonusRest[0]},
	};

	private State[][] s_BonusAnim = { s_BonusPunch, s_BonusKick, s_BonusGrab };

	protected Main app;
	private State pState;
	private float Tics = 0;
	private boolean BonusDone;
	protected VOC3D handle;
	protected char[] bonusbuf = new char[32];

	public StatisticScreen(Main game) {
		super(game);
		this.app = game;

		State.InitState(s_BonusGrab);
		State.InitState(s_BonusKick);
		State.InitState(s_BonusPunch);
		State.InitState(s_BonusRest);
	}

	public void checkMusic() {
		if (handle == null || !handle.isActive() && !CDAudio_Playing())
			if (!CDAudio_Play(3, true)) {
				if (!gs.noSound) {
					if (handle == null || !handle.isActive())
						handle = PlaySound(DIGI_ENDLEV, null, v3df_none);
				}
			}
	}

	@Override
	public void draw(float delta) {
		checkMusic();
		Tics = gStateControl(pState, Tics, delta);

		engine.rotatesprite(0, 0, RS_SCALE, 0, BONUS_SCREEN_PIC, 0, 0, 26, 0, 0, xdim - 1, ydim - 1);

		BuildFont font = game.getFont(1);

		font.drawText(160, 20, getMapName(PlayingLevel), 1, 19, TextAlign.Center, 26, false);
		font.drawText(160, 30, "Completed", 1, 19, TextAlign.Center, 26, false);

		engine.rotatesprite(158 << 16, 86 << 16, RS_SCALE, 0, pState.Pic, 0, 0, 26, 0, 0, xdim - 1, ydim - 1);

		int line = 50;
		int second_tics = (PlayClock / 120);
		int minutes = (second_tics / 60);
		int seconds = (second_tics % 60);

		int num = buildString(bonusbuf, 0, "Your Time:  ", minutes);
		buildString(bonusbuf, num, " : ", seconds, 2);
		font.drawText(60, line, bonusbuf, 1, 16, TextAlign.Left, 26, false);

		if (mUserFlag != UserFlag.UserMap) {
			LevelInfo info = currentGame.getLevel(PlayingLevel);
			if (info != null) {
				buildString(bonusbuf, 0, "3D Realms Best Time:  ", info.BestTime);
				font.drawText(40, line += 20, bonusbuf, 1, 16, TextAlign.Left, 26, false);

				buildString(bonusbuf, 0, "Par Time:  ", info.ParTime);
				font.drawText(40, line += 20, bonusbuf, 1, 16, TextAlign.Left, 26, false);
			}
		}

		num = buildString(bonusbuf, 0, "Secrets:  ", Player[connecthead].SecretsFound);
		buildString(bonusbuf, num, " / ", LevelSecrets);
		font.drawText(60, line += 20, bonusbuf, 1, 16, TextAlign.Left, 26, false);

		num = buildString(bonusbuf, 0, "Kills:  ", Kills);
		buildString(bonusbuf, num, " / ", TotalKillable);
		font.drawText(60, line += 20, bonusbuf, 1, 16, TextAlign.Left, 26, false);

		font.drawText(160, 185, "Press SPACE to continue", 1, 19, TextAlign.Center, 26, false);

		if (pState == pState.getNext()) {
			if (PlayingLevel == 4 || PlayingLevel == 20 || mUserFlag == UserFlag.UserMap) {
				if(FinishAnim == ANIM_ZILLA) {
					game.changeScreen(gCreditsScreen.setSkipping(app.rMenu));
				} else app.MainMenu();
			} else if (!gGameScreen.enterlevel(gGameScreen.getTitle()))
				game.show();
		}
	}

	@Override
	public void show() {
		super.show();
		engine.setbrightness(gs.brightness, palette, true);
		
		StopSound();
		COVER_SetReverb(0);

		pState = s_BonusRest[0];
		Tics = 0;
		BonusDone = false;

		if (Level < 0)
			Level = 0;

		checkMusic();

		game.pNet.ResetTimers();
		game.pInput.ctrlResetKeyStatus();
	}

	@Override
	public void skip() {
		if (!BonusDone) {
			pState = s_BonusAnim[STD_RANDOM_RANGE(s_BonusAnim.length)][0];
			Tics = 0;
			BonusDone = true;
		}
	}

	// Generic state control
	private float gStateControl(State State, float tics, float delta) {
		tics += 120 * delta;
		// Skip states if too much time has passed
		while (tics >= State.Tics) {
			// Set Tics
			tics -= State.Tics;
			State = gNextState(State);
		}

		// Call the correct animator
		if (State.Animator != null)
			State.Animator.invoke(0);

		this.pState = State;
		return tics;
	}

	private State gNextState(State State) {
		// Transition to the next state
		State = State.getNext();

		if (TEST(State.Tics, SF_QUICK_CALL)) {
			State.Animator.invoke(0);
			State = State.getNext();
		}

		return State;
	}
}
