package ru.m210projects.Wang.Screens;

import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Strhandler.toCharArray;
import static ru.m210projects.Wang.Border.SetBorder;
import static ru.m210projects.Wang.Digi.DIGI_NOFEAR;
import static ru.m210projects.Wang.Digi.DIGI_NOPAIN;
import static ru.m210projects.Wang.Digi.DIGI_TAUNTAI3;
import static ru.m210projects.Wang.Digi.DIGI_WHOWANTSWANG;
import static ru.m210projects.Wang.Draw.Follow_posx;
import static ru.m210projects.Wang.Draw.Follow_posy;
import static ru.m210projects.Wang.Draw.ScrollMode2D;
import static ru.m210projects.Wang.Draw.SetRedrawScreen;
import static ru.m210projects.Wang.Draw.dimensionmode;
import static ru.m210projects.Wang.Draw.drawhud;
import static ru.m210projects.Wang.Draw.drawscreen;
import static ru.m210projects.Wang.Draw.gNameShowTime;
import static ru.m210projects.Wang.Draw.zoom;
import static ru.m210projects.Wang.Factory.WangMenuHandler.COLORCORR;
import static ru.m210projects.Wang.Factory.WangMenuHandler.GAME;
import static ru.m210projects.Wang.Factory.WangMenuHandler.HELP;
import static ru.m210projects.Wang.Factory.WangMenuHandler.LOADGAME;
import static ru.m210projects.Wang.Factory.WangMenuHandler.OPTIONS;
import static ru.m210projects.Wang.Factory.WangMenuHandler.QUIT;
import static ru.m210projects.Wang.Factory.WangMenuHandler.SAVEGAME;
import static ru.m210projects.Wang.Factory.WangMenuHandler.SOUNDSET;
import static ru.m210projects.Wang.Factory.WangNetwork.CommPlayers;
import static ru.m210projects.Wang.Factory.WangNetwork.TimeLimitTable;
import static ru.m210projects.Wang.Game.DemoPlaying;
import static ru.m210projects.Wang.Game.DemoRecording;
import static ru.m210projects.Wang.Game.FinishAnim;
import static ru.m210projects.Wang.Game.GodMode;
import static ru.m210projects.Wang.Game.InitLevel;
import static ru.m210projects.Wang.Game.InitPlayerGameSettings;
import static ru.m210projects.Wang.Game.InitTimingVars;
import static ru.m210projects.Wang.Game.Level;
import static ru.m210projects.Wang.Game.MessageInputMode;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.Skill;
import static ru.m210projects.Wang.Game.TerminateLevel;
import static ru.m210projects.Wang.Cheats.*;
import static ru.m210projects.Wang.Game.boardfilename;
import static ru.m210projects.Wang.Game.currentGame;
import static ru.m210projects.Wang.Game.defGame;
import static ru.m210projects.Wang.Game.isDemoRecording;
import static ru.m210projects.Wang.Game.pNetInfo;
import static ru.m210projects.Wang.Game.rec;
import static ru.m210projects.Wang.Game.screenpeek;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.PF_DEAD;
import static ru.m210projects.Wang.Gameutils.PF_VIEW_FROM_OUTSIDE;
import static ru.m210projects.Wang.JPlayer.adduserquote;
import static ru.m210projects.Wang.LoadSave.gAutosaveRequest;
import static ru.m210projects.Wang.LoadSave.gQuickSaving;
import static ru.m210projects.Wang.LoadSave.quickload;
import static ru.m210projects.Wang.LoadSave.quicksave;
import static ru.m210projects.Wang.LoadSave.quickslot;
import static ru.m210projects.Wang.LoadSave.savegame;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.gAnmScreen;
import static ru.m210projects.Wang.Main.gDemoScreen;
import static ru.m210projects.Wang.Main.gGameScreen;
import static ru.m210projects.Wang.Main.gLoadingScreen;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Main.gPrecacheScreen;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Main.mUserFlag;
import static ru.m210projects.Wang.Palette.DoPlayerDivePalette;
import static ru.m210projects.Wang.Palette.DoPlayerNightVisionPalette;
import static ru.m210projects.Wang.Palette.FORCERESET;
import static ru.m210projects.Wang.Palette.ResetPalette;
import static ru.m210projects.Wang.Player.domovethings;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.StartAmbientSound;
import static ru.m210projects.Wang.Sound.StartMusic;
import static ru.m210projects.Wang.Sound.v3df_none;
import static ru.m210projects.Wang.Text.PutStringInfoLine;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.ResourceHandler.checkEpisodeResources;
import static ru.m210projects.Wang.Type.ResourceHandler.resetEpisodeResources;

import com.badlogic.gdx.Input.Keys;

import ru.m210projects.Build.CRC32;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Wang.Config.SwKeys;
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Main.UserFlag;
import ru.m210projects.Wang.Sound;
import ru.m210projects.Wang.Factory.WangMenuHandler;
import ru.m210projects.Wang.Factory.WangNetwork.MultiGameTypes;
import ru.m210projects.Wang.Type.DemoFile;
import ru.m210projects.Wang.Type.GameInfo;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.VOC3D;

public class GameScreen extends GameAdapter {

	private Main game;
	private int nonsharedtimer;
	private boolean NewGame = false;

	public GameScreen(Main game) {
		super(game, gLoadingScreen);

		this.game = game;
	}

	@Override
	public void hide() {
		if (!game.isCurrentScreen(gPrecacheScreen)) {
			BuildGdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					TerminateLevel();
				}
			});
		}
	}

	@Override
	public void ProcessFrame(BuildNet net) {
		domovethings(net);
	}

	@Override
	public void DrawWorld(float smooth) {
		game.pIntSkip2.dointerpolations(smooth);
		game.pIntSkip4.dointerpolations(smooth);
		drawscreen(Player[screenpeek], (int) smooth);
	}

	@Override
	public void DrawHud(float smooth) {
		drawhud(Player[screenpeek]);

		if (game.net.bOutOfSync) {
			game.getFont(1).drawText(160, 20, toCharArray("Out of sync!"), 0, 12, TextAlign.Center, 2, false);

			switch (game.net.bOutOfSyncByte / 4) {
			case 0: // bseed
				game.getFont(1).drawText(160, 30, toCharArray("seed checksum error"), 0, 12, TextAlign.Center, 2,
						false);
				break;
			case 1: // player
				game.getFont(1).drawText(160, 30, toCharArray("player struct checksum error"), 0, 12, TextAlign.Center,
						2, false);
				break;
			case 2: // sprite
				game.getFont(1).drawText(160, 30, toCharArray("player sprite checksum error"), 0, 12, TextAlign.Center,
						2, false);
				break;
			case 3: // missile
				game.getFont(1).drawText(160, 30, toCharArray("missile sprites checksum error"), 0, 12,
						TextAlign.Center, 2, false);
				break;
			}
		}

		game.pIntSkip2.restoreinterpolations();
		game.pIntSkip4.restoreinterpolations();
	}

	@Override
	public void PostFrame(BuildNet net) {
		if (gQuickSaving) {
			if (captBuffer != null) {
				savegame("[quicksave_" + +quickslot + "]", "quicksav" + quickslot + ".sav");
				quickslot ^= 1;
				gQuickSaving = false;
			} else
				gGameScreen.capture(160, 100);
		}

		if (gAutosaveRequest) {
			if (captBuffer != null) {
				savegame("[autosave]", "autosave.sav");
				gAutosaveRequest = false;
			} else
				gGameScreen.capture(160, 100);
		}
	}

	@Override
	public void KeyHandler() {
		PlayerStr pp = Player[myconnectindex];
		WangMenuHandler menu = game.menu;
		if (menu.gShowMenu) {
			engine.handleevents();
			menu.mKeyHandler(game.pInput, BuildGdx.graphics.getDeltaTime());
			return;
		}

		if (Console.IsShown() || MessageInputMode)
			return;

		BuildControls input = game.pInput;
		if (input.ctrlGetInputKey(GameKeys.Menu_Toggle, true))
			menu.mOpen(menu.mMenus[GAME], -1);

		if (input.ctrlGetInputKey(SwKeys.Show_Help, true))
			menu.mOpen(menu.mMenus[HELP], -1);

		if (input.ctrlGetInputKey(SwKeys.Show_Savemenu, true)) {
			if (numplayers > 1 || game.net.FakeMultiplayer)
				return;
			if (!TEST(pp.Flags, PF_DEAD)) {
				gGameScreen.capture(160, 100);
				menu.mOpen(menu.mMenus[SAVEGAME], -1);
			}
		}

		if (input.ctrlGetInputKey(SwKeys.Show_Loadmenu, true)) {
			if (numplayers > 1 || game.net.FakeMultiplayer)
				return;
			menu.mOpen(menu.mMenus[LOADGAME], -1);
		}

		if (input.ctrlGetInputKey(SwKeys.See_Chase_View, true)) {
			if (input.ctrlKeyStatus(Keys.SHIFT_LEFT) || input.ctrlKeyStatus(Keys.SHIFT_RIGHT)) {
				if (TEST(pp.Flags, PF_VIEW_FROM_OUTSIDE))
					pp.view_outside_dang = NORM_ANGLE(pp.view_outside_dang + 256);
			} else {
				if (TEST(pp.Flags, PF_VIEW_FROM_OUTSIDE)) {
					pp.Flags &= ~(PF_VIEW_FROM_OUTSIDE);
				} else {
					pp.Flags |= (PF_VIEW_FROM_OUTSIDE);
					pp.camera_dist = 0;
				}
			}
		}

		if ((dimensionmode == 5 || dimensionmode == 6)) {
			int j = totalclock - nonsharedtimer;
			nonsharedtimer += j;
			if (input.ctrlGetInputKey(GameKeys.Enlarge_Hud, false))
				zoom += mulscale(j, Math.max(zoom, 256), 6);
			if (input.ctrlGetInputKey(GameKeys.Shrink_Hud, false))
				zoom -= mulscale(j, Math.max(zoom, 256), 6);

			if ((zoom > 2048))
				zoom = 2048;
			if ((zoom < 48))
				zoom = 48;

			if (input.ctrlGetInputKey(SwKeys.Map_Follow_Mode, true)) {
				ScrollMode2D = !ScrollMode2D;

				if (ScrollMode2D) {
					Follow_posx = pp.oposx;
					Follow_posy = pp.oposy;
//	   	             ud.fola = (int) pp.oang;
				}

				PutStringInfoLine(pp, "ScrollMode " + (ScrollMode2D ? "ON" : "OFF"));
			}
		} else {
			if (input.ctrlGetInputKey(GameKeys.Enlarge_Hud, true)) {
				SetBorder(pp, gs.BorderNum + 1);
			}
			if (input.ctrlGetInputKey(GameKeys.Shrink_Hud, true)) {
				SetBorder(pp, gs.BorderNum - 1);
			}
		}

		if (input.ctrlGetInputKey(SwKeys.Toggle_Crosshair, true)) {
			gs.Crosshair = !gs.Crosshair;
			PutStringInfoLine(pp, "Crosshair " + (gs.Crosshair ? "ON" : "OFF"));
		}

        if (input.ctrlGetInputKey(SwKeys.Show_Sounds, true))
			menu.mOpen(menu.mMenus[SOUNDSET], -1);

		if (input.ctrlGetInputKey(SwKeys.Show_Options, true))
			menu.mOpen(menu.mMenus[OPTIONS], -1);

		if (input.ctrlGetInputKey(SwKeys.Gamma, true))
			openGamma(menu);

		if (input.ctrlGetInputKey(SwKeys.Quicksave, true)) { // quick save
			quicksave();
		}

		if (input.ctrlGetInputKey(SwKeys.Messages, true)) {
			gs.Messages = !gs.Messages;

			PutStringInfoLine(pp, "Messages " + (gs.Messages ? "ON" : "OFF"));
		}

		if (input.ctrlGetInputKey(GameKeys.Send_Message, false)) {
			MessageInputMode = true;
			getInput().initMessageInput(null);
		}

		if (input.ctrlGetInputKey(SwKeys.Quickload, true)) { // quick load
			quickload();
		}

		if (input.ctrlGetInputKey(SwKeys.See_Coop_View, true)) {
			if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COOPERATIVE || gNet.FakeMultiplayer) {
				screenpeek = connectpoint2[screenpeek];
				if (screenpeek < 0)
					screenpeek = connecthead;

				ResetPalette(Player[screenpeek], FORCERESET);
				DoPlayerDivePalette(Player[screenpeek]);
				DoPlayerNightVisionPalette(Player[screenpeek]);
			}
		}

		if (input.ctrlGetInputKey(SwKeys.Quit, true))
			menu.mOpen(menu.mMenus[QUIT], -1);

		if (input.ctrlGetInputKey(SwKeys.Screenshot, true))
			makeScreenshot();
	}

	@Override
	public void sndHandlePause(boolean pause) {
		Sound.sndHandlePause(pause);
	}

	@Override
	protected boolean prepareboard(String map) {
		gNameShowTime = 500;
		FinishAnim = 0;
		if (!InitLevel(map, NewGame))
			return false;

		// auto aim / auto run / etc
		if (GameScreen.this != gDemoScreen)
			InitPlayerGameSettings();
		else
			Player[myconnectindex].Flags = gDemoScreen.demfile.Flags[myconnectindex];

		// send packets with player info
		gNet.InitNetPlayerOptions();

		// Initialize Game part of network code (When ready2send != 0)
		gNet.ResetTimers();

		gNet.InitPrediction(Player[myconnectindex]);

		gNet.WaitForAllPlayers(0);
		engine.sampletimer();

		// IMPORTANT - MUST be right before game loop AFTER waitforeverybody
		InitTimingVars();

		SetRedrawScreen(Player[myconnectindex]);

		StartAmbientSound();

		StartMusic();

		if (DemoRecording)
			rec = new DemoFile(2);

		if(!NewGame && game.nNetMode == NetMode.Single && GameScreen.this != gDemoScreen)
	    	 gAutosaveRequest = true;

		NewGame = false;

		System.err.println(map);
		return true;
	}

	public void newgame(final boolean isMultiplayer, final Object item, final int nEpisode, final int nLevel,
			final int nDifficulty) {

		if (rec != null)
			rec.close();

		if (numplayers > 1 && game.pNet.bufferJitter >= 0 && myconnectindex == connecthead)
			for (int i = 0; i <= game.pNet.bufferJitter; i++)
				game.pNet.GetNetworkInput(); // wait for other player before level end

		pNet.ready2send = false;
		game.changeScreen(load);
		load.init(new Runnable() {
			@Override
			public void run() {
				InfinityAmmo = false;
				DemoPlaying = GameScreen.this == gDemoScreen;

				if (!isMultiplayer) {
					if (numplayers > 1)
						pNet.NetDisconnect(myconnectindex);

					connecthead = 0;
					connectpoint2[0] = -1;
					CommPlayers = 1;
					game.nNetMode = NetMode.Single;
					gNet.FakeMultiplayer = false;

					if (!DemoPlaying) {
						gNet.KillLimit = 0;
						gNet.TimeLimit = 0;
						gNet.TimeLimitClock = 0;
						gNet.MultiGameType = MultiGameTypes.MULTI_GAME_NONE;
						gNet.TeamPlay = false;
						gNet.HurtTeammate = false;
						gNet.SpawnMarkers = false;
						gNet.NoRespawn = false;
						gNet.Nuke = true;
					}
				} else {
					if (gNet.FakeMultiplayer) {
						connecthead = 0;
						CommPlayers = gNet.FakeMultiNumPlayers;
						for (short i = 0; i < MAXPLAYERS; i++)
							connectpoint2[i] = (short) (i + 1);
						connectpoint2[gNet.FakeMultiNumPlayers - 1] = -1;
					} else
						CommPlayers = numplayers;

					if (GameScreen.this != gDemoScreen) {
						gNet.HurtTeammate = pNetInfo.nFriendlyFire == 1;
						gNet.SpawnMarkers = pNetInfo.SpawnMarkers;
						gNet.TeamPlay = pNetInfo.TeamPlay;
						gNet.Nuke = pNetInfo.NetNuke;
						gNet.KillLimit = pNetInfo.KillLimit * 10;
						gNet.TimeLimit = TimeLimitTable[pNetInfo.TimeLimit] * 60 * 120;

						gNet.TimeLimitClock = gNet.TimeLimit;
						gNet.MultiGameType = MultiGameTypes.values()[pNetInfo.nGameType + 1];
						// settings for No Respawn Commbat mode
						if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COMMBAT_NO_RESPAWN) {
							gNet.MultiGameType = MultiGameTypes.MULTI_GAME_COMMBAT;
							gNet.NoRespawn = true;
						} else
							gNet.NoRespawn = false;
					}

					GodMode = false;
					game.nNetMode = NetMode.Multiplayer;
				}

				boardfilename = null;

				UserFlag flag = UserFlag.None;
				if (item instanceof GameInfo && !item.equals(defGame)) {
					flag = UserFlag.Addon;
					GameInfo game = (GameInfo) item;
					checkEpisodeResources(game);
					Console.Println("Start user episode: " + game.Title);
				} else {
					resetEpisodeResources();
				}

				if (item != null && item instanceof FileEntry) {
					flag = UserFlag.UserMap;
					boardfilename = ((FileEntry) item).getPath();
					Level = 0;
					Console.Println("Start user map: " + ((FileEntry) item).getName());
				}
				mUserFlag = flag;

				if (GameScreen.this != gDemoScreen) {
					if (!isMultiplayer) {
						VOC3D skillvoice = null;
						switch (nDifficulty) {
						case 0:
							skillvoice = PlaySound(DIGI_TAUNTAI3, null, v3df_none);
							break;
						case 1:
							skillvoice = PlaySound(DIGI_NOFEAR, null, v3df_none);
							break;
						case 2:
							skillvoice = PlaySound(DIGI_WHOWANTSWANG, null, v3df_none);
							break;
						case 3:
							skillvoice = PlaySound(DIGI_NOPAIN, null, v3df_none);
							break;
						}

						while (skillvoice != null && skillvoice.isActive())
							;
					}
				}

				if (nEpisode == 0)
					Level = nLevel + 1;
				else if (nEpisode == 1)
					Level = 5 + nLevel;
				else if (nEpisode == 2)
					Level = 23 + nLevel;
				Skill = nDifficulty;
				FinishAnim = 0;

				if(flag == UserFlag.Addon && game.nNetMode == NetMode.Single && GameScreen.this != gDemoScreen && nLevel == 0) {
					byte[] currentAnm = BuildGdx.cache.getBytes("sw.anm", 0);
					byte[] defAnm = BuildGdx.cache.getBytes("sw.anm", 1);

					if(currentAnm != null && defAnm != null && CRC32.getChecksum(currentAnm) != CRC32.getChecksum(defAnm)) {
						if(gAnmScreen.init(0)) {
							game.changeScreen(gAnmScreen.setCallback(new Runnable() {
								@Override
								public void run() {
									NewGame = true;
									if (!enterlevel(getTitle()))
										game.show();
								}
							}).escSkipping(true));
							return;
						}
					}
				}

				NewGame = true;
				if (!enterlevel(getTitle()))
					game.show();
			}
		});
	}

	public boolean enterlevel(String title) {
		if (title == null)
			return false;
		String map;
		if (mUserFlag == UserFlag.UserMap)
			map = boardfilename;
		else
			map = currentGame.getMapPath(Level);

		if(GameScreen.this != gDemoScreen)
			DemoRecording = isDemoRecording;

		loadboard(map, null).setTitle(title);
		return true;
	}

	@Override
	protected void startboard(Runnable startboard) {
		gPrecacheScreen.init(false, startboard);
		game.changeScreen(gPrecacheScreen);
	}

	public String getTitle() {
		String title = null;
		if (mUserFlag != UserFlag.UserMap) {
			return currentGame.getMapTitle(Level);
		} else {
			FileEntry file = BuildGdx.compat.checkFile(boardfilename);
			if (file != null)
				title = file.getName();
			else {
				game.GameCrash("Map " + boardfilename + " not found!");
				return null;
			}
		}
		return title;
	}

	protected void openGamma(WangMenuHandler menu) {
		menu.mOpen(menu.mMenus[COLORCORR], -1);
	}

	protected void makeScreenshot() {
		String name = "scrxxxx.png";
		FileEntry map;
		if (mUserFlag == UserFlag.UserMap && (map = BuildGdx.compat.checkFile(boardfilename)) != null)
			name = "scr-" + map.getName() + "-xxxx.png";
		name = "scr-" + getTitle() + "-xxxx.png";

		String filename = pEngine.screencapture(name);
		if (filename != null)
			adduserquote(filename + " saved");
		else
			adduserquote("Screenshot not saved. Access denied!");
	}
}
