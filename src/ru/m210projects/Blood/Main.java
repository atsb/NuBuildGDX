// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood;

import com.badlogic.gdx.utils.ByteArray;
import ru.m210projects.Blood.Factory.BloodEngine;
import ru.m210projects.Blood.Factory.BloodFactory;
import ru.m210projects.Blood.Factory.BloodMenuHandler;
import ru.m210projects.Blood.Factory.BloodNetwork;
import ru.m210projects.Blood.Menus.*;
import ru.m210projects.Blood.Screens.*;
import ru.m210projects.Blood.Types.BloodDef;
import ru.m210projects.Blood.Types.BloodIniFile;
import ru.m210projects.Blood.Types.EpisodeInfo;
import ru.m210projects.Blood.Types.PLOCATION;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildMessage.MessageType;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.*;
import ru.m210projects.Build.FileHandle.FileResource.Mode;
import ru.m210projects.Build.FileHandle.UserGroup.UserResource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.OnSceenDisplay.OSDCOMMAND;
import ru.m210projects.Build.OnSceenDisplay.OSDCVARFUNC;
import ru.m210projects.Build.Pattern.BuildFactory;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Script.Maphack;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Types.SPRITE;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import static ru.m210projects.Blood.AI.Ai.aiInit;
import static ru.m210projects.Blood.Actor.actSpawnDude;
import static ru.m210projects.Blood.Actor.gNoEnemies;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.kDudeMax;
import static ru.m210projects.Blood.Factory.BloodMenuHandler.*;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.LEVELS.*;
import static ru.m210projects.Blood.LOADSAVE.*;
import static ru.m210projects.Blood.Mirror.MIRROR;
import static ru.m210projects.Blood.PLAYER.gPrevView;
import static ru.m210projects.Blood.SOUND.sndInit;
import static ru.m210projects.Blood.Screen.scrCreateStdColors;
import static ru.m210projects.Blood.Screen.scrReset;
import static ru.m210projects.Blood.Tile.tileInit;
import static ru.m210projects.Blood.Trig.trigInit;
import static ru.m210projects.Blood.Types.DEMO.*;
import static ru.m210projects.Blood.Types.GAMEINFO.EndOfGame;
import static ru.m210projects.Blood.Types.ScreenEffect.FadeInit;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.SeqInit;
import static ru.m210projects.Blood.VERSION.SHAREWARE;
import static ru.m210projects.Blood.View.*;
import static ru.m210projects.Blood.Weapon.WeaponInit;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.loadGdxDef;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.OnSceenDisplay.Console.*;

public class Main extends BuildGame {

	public static final String appdef = "bloodgdx.def";

	/*
	 * Changelog:
	 * Statistics screen in overheadmap moved down like in 3d view
	 * Resetting inventory bug fixed
	 * Multiply "nocapinmyass" fix
	 * Version v0.99 protect (BloodGDX doesn't support it)
	 * Demofiles with wrong version don't show in a user content
	 * Keys don't disappear  after death in multiplayer
	 *
	 * TODO:
	 * A bug I found was that if you quicksave next to a cultist when it is about to say something, the pitch of the voice clip is lowered every time you quickload. Then, it goes back to the correct pitch after quickloading too many times
	 * Fanatics have blue pixels on skin when using my Cultists addon.
	 * you can't switch weapons with mousewheel or "next/previous" weapons with the voodoodoll still
	 * Far future:
	 * user palette
	 * настройки консоли в меню
	 * При быстром переключении на несуществующее оружие, другое не выбирается
	 *
	 *
	 * Not from original:
	 * 1. это тип 456. это электрический спрайт, который можно вкл и выкл, но дело в том, что когда его выключаешь, он становится блочным, даже если до этого не был таковым
	 * 2. сочитание клавиш для рук зверя
	 * 3. плескания лавы у цербера
	 * 4. церберу давку как у чернобога
	 * 5. культистам присядать если потолок низкий
	 * 6. не забудь добавить Dudelockout для этого флага
	 * 7. в порте динамит основной огонь - он тоже должен взрываться в зависимости от того, сколько осталось времени
	 * 8. headspritestat[kStatMissile] вернуть hitscan fix
	 * 9. Ракетница под водой жарит
	 * 10. Кровь и пули на поворотных секторах
	 * 11. Не стреляет томмиган из воды отключить
	 * 12. nearsectors искать проекцию на стену сектора
	 * 13. Квоты для итемов
	 *
	 * БАГИ ОРИГИНАЛЬНОЙ ИГРЫ: БАГИ КАРТ (вряд ли можно пофиксить без редактирования
	 * карты): In E2M4: The Overlooked Hotel, the player can pass through the gate
	 * before the end of the level, by crawling, without the moon key. In E2M5: The
	 * Haunting, there is a fireplace that contains a secret. Each time you visit
	 * the secret, it credits you, artificially inflating the total at the end of
	 * the level. In E5M2: Old Opera House, there is a secret which will not
	 * increase the secrets count, possibly due to a bug, so your best possible
	 * score will be a total of 3/4 secrets at the end of the level.
	 */

	public static MenuScreen gMenuScreen;
	public static GameScreen gGameScreen;
	public static DemoScreen gDemoScreen;
	public static LoadingScreen gLoadingScreen;
	public static LogoScreen gLogoScreen;
	public static CutsceneScreen gCutsceneScreen;
	public static NetScreen gNetScreen;
	public static StatisticScreen gStatisticScreen;
	public static DisconnectScreen gDisconnectScreen;
	public static PrecacheScreen gPrecacheScreen;

	public enum UserFlag {
		None, UserMap, Addon
	}

	public static UserFlag mUserFlag = UserFlag.None;
	public static Main game;
	public static BloodEngine engine;
	public static Config cfg;
	public BloodMenuHandler menu;
	public BloodNetwork net;

	public Main(BuildConfig bcfg, String appname, String sversion, boolean isDemo, boolean isRelease) {
		super(bcfg, appname, sversion, isRelease);
		game = this;
		cfg = (Config) bcfg;
		SHAREWARE = isDemo;
	}

	public Main(BuildConfig bcfg, String appname, String sversion, boolean isDemo) {
		this(bcfg, appname, sversion, isDemo, true);
	}

	@Override
	public BuildFactory getFactory() {
		return new BloodFactory(this);
	}

	@Override
	public boolean init() throws Exception {
		net = (BloodNetwork) pNet;
		Console.Println("Initializing timer");
		engine.inittimer(kTimerRate);

		ConsoleInit();
		Console.Println("Initializing sound system");
		sndInit();

		Console.Println("Loading INI file");
		String main = "blood.ini";
		byte[] data = BuildGdx.cache.getBytes(main, 0);
		MainINI = new BloodIniFile(data, main, BuildGdx.compat.checkFile(main));
		pGameInfo.copy(defGameInfo);

		Console.Println("Creating standard color lookups");
		scrCreateStdColors();
		tileInit();

		try {
			fire = new Fire();
		} catch (Exception e) {
			e.printStackTrace();
			game.GameMessage(e.getMessage());
			return false;
		}

		Console.Println("Loading cosine table");
		trigInit();
		Console.Println("Initializing view subsystem");
		viewInit();

		demoscan();

		net.ResetNetwork();

		for (int i = 0; i < kMaxPlayers; i++) {
			gPlayer[i] = new PLAYER();
			gPrevView[i] = new PLOCATION();
		}

		gMe = gPlayer[myconnectindex];
		gViewIndex = myconnectindex;

		SeqInit();
		FadeInit();

		initEpisodeInfo(MainINI);
		getEpisodeInfo(gEpisodeInfo, MainINI);
		InitCutscenes();

		engine.getTile(MIRROR).clear();

		Console.Println("Initializing def-scripts...");
		loadGdxDef(baseDef, appdef, "bloodgdx.dat");

		if(cfg.autoloadFolder) {
			Console.Println("Initializing autoload folder");
			DirectoryEntry autoload;
			if((autoload = BuildGdx.compat.checkDirectory("autoload")) != null)
			{
				for (final FileEntry file : autoload.getFiles().values()) {
					switch (file.getExtension()) {
						case "zip": {
							Group group = BuildGdx.cache.add(file.getPath());
							if (group == null) continue;

							GroupResource def = group.open(appdef);
							if (def != null) {
								byte[] buf = def.getBytes();
								baseDef.loadScript(file.getName(), buf);
								def.close();
							} else { //load it as a game resource
								group.setFlags(true, false);
								try {
									prepareusergroup(group);
								} catch (Exception e) {
									GameWarning("Error in \"" + file.getName() + "\" \r\n" + e.getMessage());
									continue;
								}
								InitGroupResources(getBaseDef(), group.getList());
							}
							break;
						}
						case "rff":
						case "grp": {
							Group group = BuildGdx.cache.add(file.getPath());
							if (group == null) continue;

							group.setFlags(true, false);
							InitGroupResources(getBaseDef(), group.getList());
							break;
						}
						case "def":
							baseDef.loadScript(file);
							break;
					}
				}
			}
		}

		FileEntry filgdx = BuildGdx.compat.checkFile(appdef);
		if(filgdx != null)
			baseDef.loadScript(filgdx);
		this.setDefs(baseDef);

		viewHandInit();
		Console.Println("Initializing weapon animations");
		WeaponInit();

		FindSaves();

		menu.mMenus[MAIN] = new MainMenu(this);
		menu.mMenus[GAME] = new MenuGame(this);
		menu.mMenus[DIFFICULTY] = new MenuDifficulty(this);
		menu.mMenus[NEWADDON] = new MenuNewAddon(this);
		menu.mMenus[MULTIPLAYER] = new BLMenuMultiplayer(this);
		menu.mMenus[NETWORKGAME] = new MenuNetwork(this);
		menu.mMenus[CORRUPTLOAD] = new MenuCorruptGame(this);

		gCutsceneScreen = new CutsceneScreen(this);
		gLoadingScreen = new LoadingScreen(this);
		gLogoScreen = new LogoScreen(this, 2.0f);
		gMenuScreen = new MenuScreen(this);
		gGameScreen = new GameScreen(this);
		gDemoScreen = new DemoScreen(this);
		gNetScreen = new NetScreen(this);
		gStatisticScreen = new StatisticScreen(this, 1.0f);
		gDisconnectScreen = new DisconnectScreen(this, 1.0f);
		gPrecacheScreen = new PrecacheScreen(this);

		Runnable nextLogo = new Runnable() {
			@Override
			public void run() {
				if (cfg.showCutscenes && gCutsceneScreen.init("gti.smk", "gti.wav"))
					changeScreen(gCutsceneScreen.setCallback(rMenu));
				else changeScreen(gLogoScreen.setTile(2052).setCallback(rMenu));
			}
		};

		gLogoScreen.setTile(2050).setCallback(nextLogo).setSkipping(rMenu);
		gCutsceneScreen.setCallback(nextLogo).setSkipping(rMenu).escSkipping(false);

		return true;
	}

	public BloodDef getBaseDef() {
		return (BloodDef) baseDef;
	}

	public BloodDef getCurrentDef() {
		return (BloodDef) currentDef;
	}

	private void ConsoleInit()
	{
		Console.Println("Initializing on-screen display system");
		Console.setParameters(0, 0, 0, 12, 2, 12);
		Console.setVersion(appname + " " + sversion, 10, OSDTEXT_GOLD);
		Console.RegisterCvar(new OSDCOMMAND("initgroupfile",
				"initgroupfile", new OSDCVARFUNC() {
					@Override
					public void execute() {
						if (Console.osd_argc != 2) {
							Console.Println("initgroupfile: <path to [zip/grp/rff]>");
							return;
						}

						if(!isCurrentScreen(gMenuScreen)) {
							Console.Println("initgroupfile: Back to menu at first");
							return;
						}

						String filename = osd_argv[1];
						FileEntry file = BuildGdx.compat.checkFile(filename);
						if (file != null) {
							if(file.getExtension().equals("zip")
									|| file.getExtension().equals("grp")
									|| file.getExtension().equals("rff"))
							{
								try {
									Group gr = BuildGdx.cache.add(file.getPath());
									gr.setFlags(true, false);
									prepareusergroup(gr);
									InitGroupResources(getCurrentDef(), gr.getList());
									scrReset();

									Console.Println("initgroupfile: " + filename + " successfuly added to game resources");
								} catch(Exception e) {
									Console.Println("Error to load " + file.getName(), OSDTEXT_RED);
								}
							}
						} else Console.Println("initgroupfile: File not found");
					}
		}));

		Console.RegisterCvar(new OSDCOMMAND("changeteam",
			"changeteam to 0 or 1", new OSDCVARFUNC() {
				@Override
				public void execute() {
					if (Console.osd_argc != 2) {
						Console.Println("changeteam: " + net.gProfile[myconnectindex].team + " ( 0 - auto, 1 - blue, 2 - red )");
						return;
					}

					int team = Integer.parseInt(osd_argv[1]);
					if(team >= 0 && team < 3) {
						if(net.gProfile[myconnectindex].team == team) {
							Console.Println("Already changed");
							return;
						}

						net.ChangeTeam(myconnectindex, team);
						String message = "Your team will change to ";

						if(team == 0)
							team = myconnectindex & 1;
						else team = (team - 1);

						switch(team) {
							case 0: message += "blue after respawn"; break;
							case 1: message += "red after respawn"; break;
						}
						Console.Println(message);
					} else Console.Println("changeteam: out of range");
				}
		}));

		Console.RegisterCvar(new OSDCOMMAND("noenemies",
				"show enemies", new OSDCVARFUNC() {
					@Override
					public void execute() {
						if ( isCurrentScreen(gDemoScreen) || pGameInfo.nGameType != kNetModeOff || numplayers > 1) {
							Console.Println("noenemies: Single player only");
							return;
						}

						if (Console.osd_argc != 2) {
							Console.Println("noenemies: " + gNoEnemies + " ( 0 - disabled, 1 - ai disable, 2 - enabled )");
							return;
						}

						try {
							int type = Integer.parseInt(osd_argv[1]);
							if(type >= 0 && type < 3) {
								gNoEnemies = type;
								switch(type) {
								case 0:
								case 1:
									if(type == 0) Console.Println("noenemies disabled");
									else Console.Println("noenemies: ai disable");

									for (int nSprite = headspritestat[kStatDude]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
										if(!IsOriginalDemo() && pGameInfo.nGameType == kNetModeOff && numplayers < 2)
										{
											SPRITE pSprite = sprite[nSprite];
											pSprite.cstat &= ~kSpriteInvisible;
											pSprite.cstat |= kSpriteBlocking | kSpriteHitscan | kSpritePushable;
											aiInit(IsOriginalDemo());
										}
									}
								break;
								case 2: Console.Println("noenemies enabled"); break;
								}

							}
							else Console.Println("noenemies: out of range");
						} catch(Exception e) {
							Console.Println("noenemies: out of range");
						}
					}
		}));

		Console.RegisterCvar(new OSDCOMMAND("playback",
				"playback \"filename.dem\"", new OSDCVARFUNC() {
					@Override
					public void execute() {
						if ( pGameInfo.nGameType != kNetModeOff || numplayers > 1) {
							Console.Println("playback: Single player only");
							return;
						}

						if (Console.osd_argc < 2) {
							Console.Println("playback \"demoname.dem\" [\"addon.ini\" or \"zipfile.zip:addon.ini\"]");
							return;
						}

						final String name = osd_argv[1];
						if (BuildGdx.cache.contains(name, 0)) {
							gDemoScreen.showDemo(name, osd_argv[2]);
						} else {
							Console.Println("\"" + name + "\" " + "not found!");
						}
					}
				}));

		Console.RegisterCvar(new OSDCOMMAND("quicksave",
			"quicksave: performs a quick save", new OSDCVARFUNC() {
				@Override
				public void execute() {
					if ( pGameInfo.nGameType != kNetModeOff || numplayers > 1) {
						Console.Println("quicksave: Single player only");
						return;
					}

					if (isCurrentScreen(gGameScreen)) {
						quicksave();
					} else
						Console.Println("quicksave: not in a game");
				}
			}));

		Console.RegisterCvar(new OSDCOMMAND("quickload",
			"quickload: performs a quick load", new OSDCVARFUNC() {
				@Override
				public void execute() {
					if (isCurrentScreen(gGameScreen)) {
						quickload();
					} else
						Console.Println("quickload: not in a game");
				}
			}));

		Console.RegisterCvar(new OSDCOMMAND("spawndude",
			null, new OSDCVARFUNC() {
				@Override
				public void execute() {
					if ( pGameInfo.nGameType != kNetModeOff || numplayers > 1) {
						Console.Println("spawndude: Single player only");
						return;
					}

					if (Console.osd_argc != 2) {
						Console.Println("spawndude \"enemy type number\"");
						return;
					}

					if (isCurrentScreen(gGameScreen)) {
						try {
							int type = Integer.parseInt(osd_argv[1]);
							if(type > kDudeBase && type < kDudeMax)
								actSpawnDude(gMe.pSprite, type, -1);
							else Console.Println("spawndude: type out of range");
						} catch(Exception e)
						{
							Console.Println("spawndude: type out of range");
						}
					} else
						Console.Println("spawndude: not in a game");
				}
			}));

		Console.RegisterCvar(new OSDCOMMAND("showcoords",
			null, new OSDCVARFUNC() {
				@Override
				public void execute() {
					SPRITE spr = gMe.pSprite;
					Console.Println("Player x: " + spr.x);
					Console.Println("Player y: " + spr.y);
					Console.Println("Player z: " + spr.z);
					Console.Println("Player sect: " + spr.sectnum);

				}
			}));

		Console.RegisterCvar(new OSDCOMMAND("maphack_angoff",
				null, new OSDCVARFUNC() {
					@Override
					public void execute() {

						if (Console.osd_argc != 2) {
							Console.Println("maphack_angoff value");
							return;
						}

						try {
							int angoff = Integer.parseInt(osd_argv[1]);
							int nSprite = getSprite();
							if(nSprite != -1) {
								if(!currentDef.mapInfo.isLoaded())
									currentDef.mapInfo.load(new Maphack());
								currentDef.mapInfo.getSpriteInfo(nSprite).angoff = (short) angoff;
								Console.Println("sprite " + nSprite + " angoff " + angoff);
							}
						}
						catch(Exception e) { Console.Println("type out of range"); }
					}
				}));

		Console.RegisterCvar(new OSDCOMMAND("maphack_notmd",
				null, new OSDCVARFUNC() {
					@Override
					public void execute() {

						if (Console.osd_argc != 2) {
							Console.Println("maphack_notmd value");
							return;
						}

						try {
							int value = Integer.parseInt(osd_argv[1]);
							int nSprite = getSprite();
							if(nSprite != -1) {
								if(!currentDef.mapInfo.isLoaded())
									currentDef.mapInfo.load(new Maphack());

								if(value == 1)
									currentDef.mapInfo.getSpriteInfo(nSprite).flags |= 1;
								else currentDef.mapInfo.getSpriteInfo(nSprite).flags &= ~1;

								Console.Println("sprite " + nSprite + (value == 1 ? " notmd" : " notmd removed"));
							}
						}
						catch(Exception e) { Console.Println("type out of range"); }
					}
				}));

		Console.RegisterCvar(new OSDCOMMAND("maphack_mdposxoff",
				null, new OSDCVARFUNC() {
					@Override
					public void execute() {

						if (Console.osd_argc != 2) {
							Console.Println("maphack_mdposxoff value");
							return;
						}

						try {
							int angoff = Integer.parseInt(osd_argv[1]);
							int nSprite = getSprite();
							if(nSprite != -1) {
								if(!currentDef.mapInfo.isLoaded())
									currentDef.mapInfo.load(new Maphack());

								currentDef.mapInfo.getSpriteInfo(nSprite).xoff = angoff;
								Console.Println("sprite " + nSprite + " mdposxoff " + angoff);
							}
						}
						catch(Exception e) { Console.Println("type out of range"); }
					}
				}));

		Console.RegisterCvar(new OSDCOMMAND("maphack_mdposyoff",
				null, new OSDCVARFUNC() {
					@Override
					public void execute() {

						if (Console.osd_argc != 2) {
							Console.Println("maphack_mdposyoff value");
							return;
						}

						try {
							int angoff = Integer.parseInt(osd_argv[1]);
							int nSprite = getSprite();
							if(nSprite != -1) {
								if(!currentDef.mapInfo.isLoaded())
									currentDef.mapInfo.load(new Maphack());

								currentDef.mapInfo.getSpriteInfo(nSprite).yoff = angoff;
								Console.Println("sprite " + nSprite + " mdposyoff " + angoff);
							}
						}
						catch(Exception e) { Console.Println("type out of range"); }
					}
				}));

		Console.RegisterCvar(new OSDCOMMAND("maphack_mdposzoff",
				null, new OSDCVARFUNC() {
					@Override
					public void execute() {

						if (Console.osd_argc != 2) {
							Console.Println("maphack_mdposzoff value");
							return;
						}

						try {
							int angoff = Integer.parseInt(osd_argv[1]);
							int nSprite = getSprite();
							if(nSprite != -1) {
								if(!currentDef.mapInfo.isLoaded())
									currentDef.mapInfo.load(new Maphack());
								currentDef.mapInfo.getSpriteInfo(nSprite).zoff = angoff;
								Console.Println("sprite " + nSprite + " mdposzoff " + angoff);
							}
						}
						catch(Exception e) { Console.Println("type out of range"); }
					}
				}));

		Console.RegisterCvar(new OSDCOMMAND("maphack_save",
				null, new OSDCVARFUNC() {
					@Override
					public void execute() {

						if(!currentDef.mapInfo.isLoaded()) {
							Console.Println("These is nothing to save");
							return;
						}

						String name = "unknown";
						if(mUserFlag == UserFlag.UserMap && gUserMapInfo != null)
							name = getFilename(gUserMapInfo.MapName);
						if(mUserFlag != UserFlag.UserMap && currentEpisode != null)
							name = "e" + (pGameInfo.nEpisode+1) + "m" + (pGameInfo.nLevel+1);

						String filename = name + ".mhk";
						FileResource fil = BuildGdx.compat.open(filename, Path.User, Mode.Write);
						String checksum = "crc32 " + pGameInfo.uMapCRC + "\r\n";
						fil.writeBytes(checksum.getBytes(), checksum.length());



						for(int i = 0; i < MAXSPRITES; i++)
						{
							if(currentDef.mapInfo.getSpriteInfo(i).angoff != 0)
							{
								String msg = "sprite " + i + " angoff " + currentDef.mapInfo.getSpriteInfo(i).angoff + "\r\n";
								fil.writeBytes(msg.getBytes(), msg.length());
							}

							if(currentDef.mapInfo.getSpriteInfo(i).xoff != 0)
							{
								String msg = "sprite " + i + " mdposxoff " + currentDef.mapInfo.getSpriteInfo(i).xoff + "\r\n";
								fil.writeBytes(msg.getBytes(), msg.length());
							}

							if(currentDef.mapInfo.getSpriteInfo(i).yoff != 0)
							{
								String msg = "sprite " + i + " mdposyoff " + currentDef.mapInfo.getSpriteInfo(i).yoff + "\r\n";
								fil.writeBytes(msg.getBytes(), msg.length());
							}

							if(currentDef.mapInfo.getSpriteInfo(i).zoff != 0)
							{
								String msg = "sprite " + i + " mdposzoff " + currentDef.mapInfo.getSpriteInfo(i).zoff + "\r\n";
								fil.writeBytes(msg.getBytes(), msg.length());
							}
							if((currentDef.mapInfo.getSpriteInfo(i).flags & 1) != 0)
							{
								String msg = "sprite " + i + " notmd\r\n";
								fil.writeBytes(msg.getBytes(), msg.length());
							}
						}

						Console.Println("Saved to " + Path.User.getPath() + filename);
						fil.close();
					}
				}));

		Console.RegisterCvar(new OSDCOMMAND("maphack_load",
				null, new OSDCVARFUNC() {
					@Override
					public void execute() {
						if (Console.osd_argc != 2) {
							Console.Println("maphack_load filename");
							return;
						}
						String filename = osd_argv[1];
						if(BuildGdx.cache.contains(filename, 0)) {
							Maphack map = new Maphack(filename);
							if(map.getMapCRC() == pGameInfo.uMapCRC) {
								currentDef.mapInfo.load(map);
								Console.Println("Maphack is loaded");
							} else Console.Println("Maphack isn't loaded. Wrong checksum");
						} else Console.Println("maphack_load: file not found");
					}
				}));

		Console.RegisterCvar(new OSDCOMMAND("maphack_highlight",
				null, new OSDCVARFUNC() {
					@Override
					public void execute() {
						maphack_highlight = !maphack_highlight;
						Console.Println("Maphack highlight is " + (maphack_highlight ? "on" : "off"));
					}
				}));

		Console.RegisterCvar(new OSDCOMMAND("fastdemo",
				null, new OSDCVARFUNC() {
					@Override
					public void execute() {
						if (Console.osd_argc != 2) {
							Console.Println("fastdemo \"demo speed\"");
							return;
						}

						try {
							engine.fastDemo.set(Integer.parseInt(osd_argv[1]));
						} catch(Exception e) {
							Console.Println("fastdemo: type out of range");
						}
					}
				}));
	}

	private void InitCutscenes()
	{
		Console.Println("Initializing cutscenes");
		UserGroup group = BuildGdx.cache.add("Cutscenes", false);

		FileEntry entry;
		String[] smknames;
		HashSet<String> paths = new HashSet<String>();
		for(int i = 0; i < kMaxEpisode; i++)
		{
			EpisodeInfo ep = gEpisodeInfo[i];
			smknames = new String[] { ep.CutSceneA, ep.CutWavA, ep.CutSceneB, ep.CutWavB };

			for(int j = 0, index; j < smknames.length; j++) {
				if(smknames[j] == null) continue;

				File res = BuildGdx.compat.checkFile(smknames[j], Path.Absolute);
				if(res != null)
				{
					paths.add(res.getParent() + File.separator);
					group.add(smknames[j], -1);
					continue;
				}

				index = -1;
				if((index = smknames[j].indexOf(":" + File.separator)) != -1) { //CDROM path hack
					smknames[j] = smknames[j].substring(index + 2);

					if(j == 0) ep.CutSceneA = smknames[j];
					else if(j == 1) ep.CutWavA = smknames[j];
					else if(j == 2) ep.CutSceneB = smknames[j];
					else if(j == 3) ep.CutWavB = smknames[j];
				}

				if((entry = BuildGdx.compat.checkFile(smknames[j])) != null) {
					paths.add(entry.getFile().getParent() + File.separator);
					group.add(entry, -1);
				}
			}
		}

		smknames = new String[] { "logo.smk", "logo811m.wav", "gti.smk", "gti.wav" };
		for (String smkname : smknames) {
			for (String path : paths) {
				File res = BuildGdx.compat.checkFile(path + smkname, Path.Absolute);
				if (res != null) {
					UserResource ures = group.add(smkname, -1);
					if (ures != null)
						ures.absolutePath = res.getAbsolutePath();
					break;
				}
			}
		}

		if(group.numfiles == 0)
			BuildGdx.cache.remove(group);
	}

	public int getSprite()
	{
		int dx = Trig.Cos(gMe.pSprite.ang) >> 16;
		int dy = Trig.Sin(gMe.pSprite.ang) >> 16;
		int dz = (int) gMe.horizOff;

		Gameutils.HitScan(gMe.pSprite, gMe.viewOffZ, dx, dy, dz, pHitInfo, 0xFFFF0030, 0);

		return pHitInfo.hitsprite;
	}

	public void resetState()
	{
		kGameCrash = false;
		mUserFlag = UserFlag.None;
		currentEpisode = null;
		lastload = null;
		kFakeMultiplayer = false;
		if(usecustomarts || usecustomqavs)
			resetEpisodeResources();

		menu.mClose();
		menu.mOpen(menu.mMenus[MAIN], -1);

		pInput.ctrlResetKeyStatus();
	}

	public Runnable rMenu = new Runnable() {
		@Override
		public void run() {
			resetState();

			if (demofiles.size() == 0 || cfg.gDemoSeq == 0 || !gDemoScreen.showDemo())
				changeScreen(gMenuScreen);
		}
	};

	public void EndGame()
	{
		changeScreen(gLogoScreen.setTile(2050).setCallback(rMenu));
	}

	public void Disconnect()
	{
		changeScreen(gDisconnectScreen.setSkipping(rMenu));
	}

	public void GameWarning(String text) {
		BuildGdx.message.show("Warning: ", text, MessageType.Info);
		Console.Println("Warning: " + text, OSDTEXT_YELLOW);
	}

	public void dassert(String msg) {
		if (kGameCrash)
			return;

		ThrowError(msg);
		System.exit(0);
	}

	@Override
	public void GameMessage(String errorText) {
		BuildGdx.message.show("Error: ", errorText, MessageType.Info);
		Console.Println("Game error: " + errorText, OSDTEXT_RED);

		pGameInfo.uGameFlags = EndOfGame;
		pGameInfo.nLevel = 0;
		kGameCrash = true;
	}

	@Override
	public void show() {
		if (cfg.showCutscenes && gCutsceneScreen.init("logo.smk", "logo811m.wav"))
			changeScreen(gCutsceneScreen);
		else changeScreen(gLogoScreen);
	}

	@Override
	protected byte[] reportData() {
		byte[] out = null;

		String text = "Mapname: " + boardfilename;
		text += "\r\n";
		text += "UserFlag: " + mUserFlag;
		text += "\r\n";

		if (mUserFlag != UserFlag.UserMap && LEVELS.currentEpisode != null) {
			EpisodeInfo currentEpisode = LEVELS.currentEpisode;
			text += "Episode filename: " + currentEpisode.filename;
			text += "\r\n";
			text += "Episode title: " + currentEpisode.Title;
			text += "\r\n";
		}
		if (pGameInfo != null) {
			text += "nDifficulty: " + pGameInfo.nDifficulty;
			text += "\r\n";
		} else {
			text += "it's shouldn't be null \r\n";
		}

		if (gMe != null && gMe.pSprite != null) {
			text += "PlayerX: " + gMe.pSprite.x;
			text += "\r\n";
			text += "PlayerY: " + gMe.pSprite.y;
			text += "\r\n";
			text += "PlayerZ: " + gMe.pSprite.z;
			text += "\r\n";
			text += "PlayerAng: " + gMe.pSprite.ang;
			text += "\r\n";
			text += "PlayerHoriz: " + gMe.horiz;
			text += "\r\n";
			text += "PlayerSect: " + gMe.pSprite.sectnum;
			text += "\r\n";
		}

		if (mUserFlag == UserFlag.UserMap && gUserMapInfo != null && gUserMapInfo.MapName != null) {
			ByteArray array = new ByteArray();
			byte[] data = BuildGdx.cache.getBytes(gUserMapInfo.MapName, 0);
			if (data != null) {
				text += "\r\n<------Start Map data------->\r\n";
				array.addAll(text.getBytes());
				array.addAll(data);
				array.addAll("\r\n<------End Map data------->\r\n".getBytes());

				out = Arrays.copyOf(array.items, array.size);
			}
		} else out = text.getBytes();

		return out;
	}

	public String getFilename(String path)
	{
		if(path != null) {
			int index = path.lastIndexOf("\\");
			if(index != -1)
				path = path.substring(index + 1);
			else {
				index = path.lastIndexOf("/");
				if(index != -1)
					path = path.substring(index + 1);
			}
		}

		return path;
	}

}
