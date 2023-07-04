// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave;

import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Powerslave.Menus.PSMenuUserContent.*;
import static ru.m210projects.Powerslave.Factory.PSMenuHandler.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Powerslave.Anim.*;
import static ru.m210projects.Powerslave.Bullet.*;
import static ru.m210projects.Powerslave.Sprites.*;
import static ru.m210projects.Powerslave.Object.*;
import static ru.m210projects.Powerslave.Grenade.*;
import static ru.m210projects.Powerslave.SpiritHead.*;
import static ru.m210projects.Powerslave.Snake.*;
import static ru.m210projects.Powerslave.Light.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Player.*;
import static ru.m210projects.Powerslave.Palette.*;
import static ru.m210projects.Powerslave.Map.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Random.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Sector.*;
import static ru.m210projects.Powerslave.Slide.*;
import static ru.m210projects.Powerslave.View.*;
import static ru.m210projects.Powerslave.Weapons.*;
import static ru.m210projects.Powerslave.Switch.*;
import static ru.m210projects.Powerslave.Enemies.Spider.*;
import static ru.m210projects.Powerslave.Enemies.Anubis.*;
import static ru.m210projects.Powerslave.Enemies.Mummy.*;
import static ru.m210projects.Powerslave.Enemies.Fish.*;
import static ru.m210projects.Powerslave.Enemies.Rat.*;
import static ru.m210projects.Powerslave.Enemies.Lion.*;
import static ru.m210projects.Powerslave.Enemies.Roach.*;
import static ru.m210projects.Powerslave.Enemies.Wasp.*;
import static ru.m210projects.Powerslave.Energy.InitEnergyTile;
import static ru.m210projects.Powerslave.Enemies.Scorp.*;
import static ru.m210projects.Powerslave.Enemies.Rex.*;
import static ru.m210projects.Powerslave.Enemies.LavaDude.*;
import static ru.m210projects.Powerslave.Enemies.Set.*;
import static ru.m210projects.Powerslave.Enemies.Queen.*;
import static ru.m210projects.Powerslave.Enemies.Ra.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileResource;
import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.FileResource.Mode;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Pattern.Tools.SaveManager;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Powerslave.Menus.MenuCorruptGame;
import ru.m210projects.Powerslave.Type.EpisodeInfo;
import ru.m210projects.Powerslave.Type.LSInfo;
import ru.m210projects.Powerslave.Type.PlayerStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;

public class LoadSave {

	public static String nSaveFile;
	public static String nSaveName;
	public static boolean gClassicMode = false;

	public static boolean gQuickSaving;
	public static boolean gAutosaveRequest;
	public static SafeLoader loader = new SafeLoader();

	public static LSInfo lsInf = new LSInfo();
	public static int quickslot = 0;
	public static String lastload;

	public static final String savsign = "LOBO";
	public static final String origsave = "savgamea.sav";

	public static final int gdxSave = 100;
	public static final int currentGdxSave = 100;
	public static final int SAVEVERSION = savsign.length() + 2; // version (2 bytes)
	public static final int SAVETIME = 8;
	public static final int SAVENAME = 32;
	public static final int SAVELEVELINFO = LSInfo.size;
	public static final int SAVEHEADER = SAVEVERSION + SAVETIME + SAVENAME + SAVELEVELINFO;

	public static final int SAVESCREENSHOTSIZE = 160 * 100;
	public static final int SAVEGDXDATA = 1 /* hasCapture */ + SAVESCREENSHOTSIZE + 128 /* reserve */
			+ 144 /* mapname */;

	public static void FindSaves() {
		Resource origSave = BuildGdx.cache.open(origsave, 0);
		if (origSave != null) {
			for (int i = 0; i < 5; i++) {
				String filename = "savgame" + i + ".sav";
				if (BuildGdx.compat.checkFile(filename, Path.User) != null)
					continue;

				if (loader.loadOrig(origSave, i)) {
					FileResource fil = BuildGdx.compat.open("savgame" + i + ".sav", Path.User, Mode.Write);
					if (fil != null) {
						long time = game.date.getCurrentDate();

						SaveHeader(fil, loader.savename, time, loader.level, loader.best, true);
						SaveGDXBlock(fil);

						fil.writeShort(loader.nPlayerWeapons[nLocalPlayer]);
						fil.writeShort(loader.PlayerList[nLocalPlayer].currentWeapon);
						fil.writeShort(loader.nPlayerClip[nLocalPlayer]);
						fil.writeShort(loader.nPlayerItem[nLocalPlayer]);
						fil.writeBytes(loader.PlayerList[nLocalPlayer].getBytes(false), PlayerStruct.size(false));
						fil.writeShort(loader.nPlayerLives[nLocalPlayer]);

						fil.close();

						game.pSavemgr.add(loader.savename, time, filename);
					}
				}
			}
			origSave.close();
		}

		Resource fil = null;
		for (Iterator<FileEntry> it = BuildGdx.compat.getDirectory(Path.User).getFiles().values().iterator(); it
				.hasNext();) {
			FileEntry file = it.next();

			if (file.getExtension().equals("sav")) {
				fil = BuildGdx.compat.open(file);
				if (fil != null) {
					String signature = fil.readString(4);
					if (signature == null || signature.isEmpty()) {
						fil.close();
						continue;
					}
					if (signature.equals(savsign)) {
						int nVersion = fil.readShort();
						if (nVersion >= gdxSave) {
							long time = fil.readLong();
							String savname = fil.readString(SAVENAME).trim();
							game.pSavemgr.add(savname, time, file.getName());
						}
					}
					fil.close();
				}
			}
		}
		game.pSavemgr.sort();
	}

	public static int lsReadLoadData(String filename) {
		FileResource file = BuildGdx.compat.open(filename, Path.User, Mode.Read);
		if (file != null) {
			Tile pic = engine.getTile(SaveManager.Screenshot);
			if (!pic.isLoaded())
				engine.allocatepermanenttile(SaveManager.Screenshot, 160, 100);

			int nVersion = checkSave(file) & 0xFFFF;
			lsInf.clear();

			if (nVersion == currentGdxSave) {
				file.seek(SAVEVERSION, Whence.Set);
				lsInf.date = game.date.getDate(file.readLong());
				file.seek(SAVEVERSION + SAVETIME + SAVENAME, Whence.Set); // to SAVELEVELINFO

				lsInf.read(file);
				if (file.remaining() <= SAVESCREENSHOTSIZE) {
					file.close();
					return -1;
				}

				boolean hasCapt = file.readBoolean();
				if (hasCapt) {
					file.read(pic.data, 0, SAVESCREENSHOTSIZE);
					engine.getrender().invalidatetile(SaveManager.Screenshot, 0, -1);
				}

				lsInf.addonfile = null;
				if (file.readByte() == 2) {
					String addon;
					String fullname = file.readString(144).trim();
					addon = FileUtils.getFullName(fullname);
					if (!addon.isEmpty())
						lsInf.addonfile = "Addon: " + addon;
				}

				file.close();
				return hasCapt ? 1 : -1;
			} else
				lsInf.info = "Incompatible ver. " + nVersion + " != " + currentGdxSave;

			if (!file.isClosed())
				file.close();
		} else
			lsInf.clear();
		return -1;
	}

	public static int checkSave(Resource bb) {
		String signature = bb.readString(4);

		if (!signature.equals(savsign))
			return 0;

		return bb.readShort();
	}

	public static boolean checkfile(Resource bb) {
		int nVersion = checkSave(bb);
		if (nVersion != currentGdxSave)
			return false;

		if (!loader.load(bb))
			return false;

		return true;
	}

	public static boolean loadgame(String filename) {
		FileResource fil = BuildGdx.compat.open(filename, Path.User, Mode.Read);
		if (fil != null) {
			boolean status = checkfile(fil);
			fil.close();

			if (status) {
				load();
				if (lastload == null || lastload.isEmpty())
					lastload = filename;

				if (gClassicMode) {
					nSaveFile = filename;
					nSaveName = loader.savename;
				} else {
					nSaveFile = nSaveName = null;
				}
			}
			return status;
		}

		return false;
	}

	private static void load() {
		LoadGDXBlock();

		gClassicMode = loader.gClassicMode;
		nBestLevel = loader.best;

		if (gClassicMode) {
			zoom = 768;
			nPlayerWeapons[nLocalPlayer] = loader.nPlayerWeapons[nLocalPlayer];
			PlayerList[nLocalPlayer].currentWeapon = loader.PlayerList[nLocalPlayer].currentWeapon;
			nPlayerClip[nLocalPlayer] = loader.nPlayerClip[nLocalPlayer];

			short nAmmo = PlayerList[nLocalPlayer].AmmosAmount[1];
			if (nAmmo >= 6)
				nAmmo = 6;
			nPistolClip[nLocalPlayer] = nAmmo;

			nPlayerItem[nLocalPlayer] = loader.nPlayerItem[nLocalPlayer];
			PlayerList[nLocalPlayer].copy(loader.PlayerList[nLocalPlayer]);
			nPlayerLives[nLocalPlayer] = loader.nPlayerLives[nLocalPlayer];
			SetPlayerItem(nLocalPlayer, nPlayerItem[nLocalPlayer]);
			CheckClip(nLocalPlayer);
			game.nNetMode = NetMode.Single;
			gGameScreen.changemap(loader.level, null);
			StatusMessage(500, "Game loaded", nLocalPlayer);
			System.gc();
		} else {
			InitElev();
			loadFM();

			gPrecacheScreen.init(false, new Runnable() {
				@Override
				public void run() {
					game.nNetMode = NetMode.Single;

					gGameScreen.gNameShowTime = 500;
					game.changeScreen(gGameScreen);
					game.pInput.resetMousePos();
					game.gPaused = false;

					LoadTorch(bTorch);
					SetPlayerItem(nLocalPlayer, nPlayerItem[nLocalPlayer]);
					SetAirFrame();
					RefreshStatus();

					if (loader.getMessage() != null)
						StatusMessage(2000, loader.getMessage(), myconnectindex);
					else
						StatusMessage(500, "Game loaded", nLocalPlayer);
					System.gc();

					game.pNet.ResetTimers();
					game.pNet.ready2send = true;
				}
			});
			game.changeScreen(gPrecacheScreen);
		}
	}

	public static void LoadGDXBlock() {
		if (loader.warp_on == 0) {
			mUserFlag = UserFlag.None;
			resetEpisodeResources(gOriginalEpisode);
		} else if (loader.warp_on == 1) {
			mUserFlag = UserFlag.UserMap;
			boardfilename = loader.boardfilename;
			resetEpisodeResources(null);
		} else if (loader.warp_on == 2) {
			mUserFlag = UserFlag.Addon;
			checkEpisodeResources(loader.addon);
		}
	}

	public static void loadMap() {
		numsectors = loader.numsectors;
		for (int s = 0; s < numsectors; s++) {
			if (sector[s] == null)
				sector[s] = new SECTOR();
			sector[s].set(loader.sector[s]);
		}
		numwalls = loader.numwalls;
		for (int w = 0; w < numwalls; w++) {
			if (wall[w] == null)
				wall[w] = new WALL();
			wall[w].set(loader.wall[w]);
		}
		for (int i = 0; i < MAXSPRITES; i++) {
			if (sprite[i] == null)
				sprite[i] = new SPRITE();
			sprite[i].set(loader.sprite[i]);
		}

		System.arraycopy(loader.headspritesect, 0, headspritesect, 0, MAXSECTORS + 1);
		System.arraycopy(loader.headspritestat, 0, headspritestat, 0, MAXSTATUS + 1);

		System.arraycopy(loader.prevspritesect, 0, prevspritesect, 0, MAXSPRITES);
		System.arraycopy(loader.prevspritestat, 0, prevspritestat, 0, MAXSPRITES);
		System.arraycopy(loader.nextspritesect, 0, nextspritesect, 0, MAXSPRITES);
		System.arraycopy(loader.nextspritestat, 0, nextspritestat, 0, MAXSPRITES);
	}

	private static void loadPlayer() {
		PlayerCount = 1;
		PlayerList[nLocalPlayer].copy(loader.PlayerList[nLocalPlayer]);

		connecthead = loader.connecthead;
		System.arraycopy(loader.connectpoint2, 0, connectpoint2, 0, 8);

		bTorch = loader.bTorch;
		nFreeze = loader.nFreeze;
		nXDamage[nLocalPlayer] = loader.nXDamage;
		nYDamage[nLocalPlayer] = loader.nYDamage;
		nDoppleSprite[nLocalPlayer] = loader.nDoppleSprite;
		nPlayerClip[nLocalPlayer] = loader.nPlayerClip[nLocalPlayer];
		nPistolClip[nLocalPlayer] = loader.nPistolClip[nLocalPlayer];
		nPlayerTorch[nLocalPlayer] = loader.nPlayerTorch;
		nPlayerWeapons[nLocalPlayer] = loader.nPlayerWeapons[nLocalPlayer];
		nPlayerLives[nLocalPlayer] = loader.nPlayerLives[nLocalPlayer];
		nPlayerItem[nLocalPlayer] = loader.nPlayerItem[nLocalPlayer];
		nPlayerInvisible[nLocalPlayer] = loader.nPlayerInvisible;
		nPlayerDouble[nLocalPlayer] = loader.nPlayerDouble;
		nPlayerViewSect[nLocalPlayer] = loader.nPlayerViewSect;
		nPlayerFloorSprite[nLocalPlayer] = loader.nPlayerFloorSprite;
		nPlayerScore[nLocalPlayer] = loader.nPlayerScore;
		nPlayerGrenade[nLocalPlayer] = loader.nPlayerGrenade;
		nPlayerSnake[nLocalPlayer] = loader.nPlayerSnake;
		nDestVertPan[nLocalPlayer] = loader.nDestVertPan;
		dVertPan[nLocalPlayer] = loader.dVertPan;
		nDeathType[nLocalPlayer] = loader.nDeathType;
		nQuake[nLocalPlayer] = loader.nQuake;
		bTouchFloor[nLocalPlayer] = loader.bTouchFloor;

		initx = loader.initx;
		inity = loader.inity;
		initz = loader.initz;
		inita = loader.inita;
		initsect = loader.initsect;

		System.arraycopy(loader.show2dsector, 0, show2dsector, 0, (MAXSECTORS + 7) >> 3);
		System.arraycopy(loader.show2dwall, 0, show2dwall, 0, (MAXWALLSV7 + 7) >> 3);
		System.arraycopy(loader.show2dsprite, 0, show2dsprite, 0, (MAXSPRITES + 7) >> 3);

		nPlayerPushSound[nLocalPlayer] = -1;
		nTauntTimer[nLocalPlayer] = (short) (RandomSize(3) + 3);
		nPlayerPushSect[nLocalPlayer] = -1;
		nPlayerSwear[nLocalPlayer] = 4;
		nTemperature[nLocalPlayer] = 0;

		SetWeaponStatus(nLocalPlayer);
	}

	private static void loadSprites() {
		loadEnemies();

		loadBullets(loader, null);
		loadGrenades(loader, null);
		loadBubbles(loader, null);
		loadSnakes(loader, null);
		loadObjects(loader, null);
		loadTraps(loader, null);
		loadDrips(loader, null);

		nCreaturesLeft = loader.nCreaturesLeft;
		nCreaturesMax = loader.nCreaturesMax;
		nSpiritSprite = loader.nSpiritSprite;
		nMagicCount = loader.nMagicCount;
		nRegenerates = loader.nRegenerates;
		nFirstRegenerate = loader.nFirstRegenerate;
		nNetStartSprites = loader.nNetStartSprites;
		nCurStartSprite = loader.nCurStartSprite;
		nNetPlayerCount = loader.nNetPlayerCount;

		System.arraycopy(loader.nNetStartSprite, 0, nNetStartSprite, 0, 8);
		System.arraycopy(loader.nChunkSprite, 0, nChunkSprite, 0, nChunkSprite.length);
		System.arraycopy(loader.nBodyGunSprite, 0, nBodyGunSprite, 0, nBodyGunSprite.length);
		System.arraycopy(loader.nBodySprite, 0, nBodySprite, 0, nBodySprite.length);

		nCurChunkNum = loader.nCurChunkNum;
		nCurBodyNum = loader.nCurBodyNum;
		nCurBodyGunNum = loader.nCurBodyGunNum;
		nBodyTotal = loader.nBodyTotal;
		nChunkTotal = loader.nChunkTotal;

		nRadialSpr = loader.nRadialSpr;
		nRadialDamage = loader.nRadialDamage;
		nDamageRadius = loader.nDamageRadius;
		nRadialOwner = loader.nRadialOwner;
		nRadialBullet = loader.nRadialBullet;

		nDronePitch = loader.nDronePitch;
		nFinaleSpr = loader.nFinaleSpr;
		nFinaleStage = loader.nFinaleStage;
		lFinaleStart = loader.lFinaleStart;
		nSmokeSparks = loader.nSmokeSparks;
	}

	private static void loadEnemies() {
		loadAnubis(loader, null);
		loadFish(loader, null);
		loadLava(loader, null);
		loadLion(loader, null);
		loadMummy(loader, null);
		loadQueen(loader, null);
		loadRa(loader, null);
		loadRat(loader, null);
		loadRex(loader, null);
		loadRoach(loader, null);
		loadScorp(loader, null);
		loadSet(loader, null);
		loadSpider(loader, null);
		loadWasp(loader, null);
	}

	private static void loadSectors() {
		loadLights(loader, null);
		loadElevs(loader, null);
		loadBobs(loader, null);
		loadMoves(loader, null);
		loadTrails(loader, null);
		loadPushBlocks(loader, null);
		loadSlide(loader, null);
		loadSwitches(loader, null);
		loadLinks(loader, null);
		loadSecExtra(loader, null);

		nEnergyChan = loader.nEnergyChan;
		nEnergyBlocks = loader.nEnergyBlocks;
		nEnergyTowers = loader.nEnergyTowers;

		nSwitchSound = loader.nSwitchSound;
		nStoneSound = loader.nStoneSound;
		nElevSound = loader.nElevSound;
		nStopSound = loader.nStopSound;

		lCountDown = loader.lCountDown;
		nAlarmTicks = loader.nAlarmTicks;
		nRedTicks = loader.nRedTicks;
		nClockVal = loader.nClockVal;
		nButtonColor = loader.nButtonColor;

		pskyoff[0] = 0;
		pskyoff[1] = 0;
		pskyoff[2] = 0;
		pskyoff[3] = 0;
		parallaxtype = 0;
		visibility = 1024; // 2048;
		parallaxyoffs = 256;
		pskybits = 2;
	}

	private static void loadFM() {
		levelnum = loader.level;
		lastlevel = loader.lastlevel;

		loadMap();
		loadAnm(loader, null);

		loadSprites();
		loadSectors();

		loadWallFaces(loader, null);
		loadRunList(loader, null);
		loadPlayer();
		loadRandom(loader, null);

		GrabPalette();
		if (levelnum == 20) {
			InitEnergyTile();

			Arrays.fill(engine.loadtile(3603), (byte) 255);
		}

		totalmoves = loader.totalmoves;
		moveframes = loader.moveframes;
	}

	public static void quicksave() {
		if (numplayers > 1 || gClassicMode)
			return;

		if (PlayerList[nLocalPlayer].HealthAmount != 0)
			gQuickSaving = true;
	}

	public static boolean canLoad(final String filename) {
		FileResource fil = BuildGdx.compat.open(filename, Path.User, Mode.Read);
		if (fil != null) {
			int nVersion = checkSave(fil) & 0xFFFF;

			if (nVersion != currentGdxSave) {
				if (nVersion >= gdxSave) {
					final EpisodeInfo addon = loader.LoadGDXHeader(fil);
					if (loader.level <= 20 && loader.warp_on != 1) { // not usermap
						MenuCorruptGame menu = (MenuCorruptGame) game.menu.mMenus[CORRUPTLOAD];
						menu.setRunnable(new Runnable() {
							@Override
							public void run() {
								EpisodeInfo game = addon != null ? addon : gOriginalEpisode;
								levelnew = loader.level;
								if (loader.gClassicMode) {
									nSaveFile = filename;
									nSaveName = loader.savename;
								} else {
									nSaveFile = nSaveName = null;
								}
								gGameScreen.newgame(game, levelnew, loader.gClassicMode);
								nBestLevel = loader.best;
							}
						});
						game.menu.mOpen(menu, -1);
					}
				}

				fil.close();
				return false;
			}

			fil.close();
			return true;
		}
		return false;
	}

	public static void quickload() {
		if (numplayers > 1 || gClassicMode)
			return;

		final String loadname = game.pSavemgr.getLast();
		if (loadname != null) {
			if (canLoad(loadname)) {
				game.changeScreen(gLoadingScreen);
				gLoadingScreen.init(new Runnable() {
					@Override
					public void run() {
						if (!loadgame(loadname)) {
							game.setPrevScreen();
							if (game.isCurrentScreen(gGameScreen)) {
								game.pNet.ready2send = true;
								StatusMessage(500, "Incompatible version of saved game found!", nLocalPlayer);
							}
						}
					}
				});
			}
		}
	}

	public static final char[] filenum = new char[4];

	public static String makeNum(int num) {
		filenum[3] = (char) ((num % 10) + 48);
		filenum[2] = (char) (((num / 10) % 10) + 48);
		filenum[1] = (char) (((num / 100) % 10) + 48);
		filenum[0] = (char) (((num / 1000) % 10) + 48);

		return new String(filenum);
	}

	public static void SaveVersion(FileResource fil, int nVersion) {
		fil.writeBytes(savsign.toCharArray(), 4);
		fil.writeShort(nVersion);
	}

	public static void SaveHeader(FileResource fil, String savename, long time, int level, int best,
			boolean gClassicMode) {
		SaveVersion(fil, currentGdxSave);

		byte[] buf = new byte[8];
		LittleEndian.putLong(buf, 0, time);
		fil.writeBytes(buf, 8);
		fil.writeBytes(savename.toCharArray(), SAVENAME);

		fil.writeByte(gClassicMode ? 1 : 0);
		fil.writeShort(level);
		fil.writeShort(best);
	}

	public static void SaveGDXBlock(FileResource fil) {
		ByteBuffer bb = ByteBuffer.allocate(SAVEGDXDATA);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		boolean hasCapt = gGameScreen != null && gGameScreen.captBuffer != null;
		bb.put(hasCapt ? (byte) 1 : 0);
		if (hasCapt) {
			bb.put(gGameScreen.captBuffer);
			gGameScreen.captBuffer = null;
		}

		byte warp_on = 0;
		if (mUserFlag == UserFlag.UserMap)
			warp_on = 1;
		if (mUserFlag == UserFlag.Addon)
			warp_on = 2;

		bb.put(warp_on);
		byte[] name = new byte[144];
		if (warp_on == 2) // user episode
		{
			if (gCurrentEpisode != null) {
				bb.put(gCurrentEpisode.packed ? (byte) 1 : 0);
				if (gCurrentEpisode.path != null) {
					String path = gCurrentEpisode.path;
					System.arraycopy(path.getBytes(), 0, name, 0, Math.min(path.length(), 144));
				}
			}
		} else {
			if (boardfilename != null)
				System.arraycopy(boardfilename.getBytes(), 0, name, 0, Math.min(boardfilename.length(), 144));
		}

		bb.put(name);
		fil.writeBytes(bb.array(), SAVEGDXDATA);
	}

	public static int savegame(String savename, String filename) {
		File file = BuildGdx.compat.checkFile(filename, Path.User);
		if (file != null)
			file.delete();

		FileResource fil = BuildGdx.compat.open(filename, Path.User, Mode.Write);
		if (fil != null) {
			long time = game.date.getCurrentDate();
			save(fil, savename, time, levelnum, nBestLevel, gClassicMode);
			game.pSavemgr.add(savename, time, filename);
			lastload = filename;
			return 0;
		} else
			StatusMessage(500, "Game not saved. Access denied!", nLocalPlayer);

		return -1;
	}

	private static void save(FileResource fil, String savename, long time, int level, int best, boolean classic) {
		SaveHeader(fil, savename, time, level, best, classic);
		SaveGDXBlock(fil);

		if (classic) {
			fil.writeShort(nPlayerWeapons[nLocalPlayer]);
			fil.writeShort(PlayerList[nLocalPlayer].currentWeapon);
			fil.writeShort(nPlayerClip[nLocalPlayer]);
			fil.writeShort(nPlayerItem[nLocalPlayer]);
			fil.writeBytes(PlayerList[nLocalPlayer].getBytes(false), PlayerStruct.size(false));
			fil.writeShort(nPlayerLives[nLocalPlayer]);
		} else
			saveFM(fil);

		fil.close();
		System.gc();
		StatusMessage(500, "Game saved", nLocalPlayer);
	}

	private static void saveFM(FileResource fil) {
		fil.writeInt(lastlevel);
		saveMap(fil);
		fil.writeBytes(saveAnm());
		saveSprites(fil);
		saveSectors(fil);
		fil.writeBytes(saveWallFaces());
		fil.writeBytes(saveRunList());
		savePlayer(fil);
		fil.writeBytes(saveRandom());

		fil.writeInt(totalmoves);
		fil.writeInt(moveframes);
	}

	private static void saveMap(FileResource fil) {
		int bufsize = 2 + (numsectors * SECTOR.sizeof) + 2 + (numwalls * WALL.sizeof) + (MAXSPRITES * SPRITE.sizeof)
				+ (MAXSECTORS + 1) * 2 + (MAXSTATUS + 1) * 2 + MAXSPRITES * 8;

		ByteBuffer bb = ByteBuffer.allocate(bufsize);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		bb.putShort(numwalls);
		for (int w = 0; w < numwalls; w++)
			bb.put(wall[w].getBytes());

		bb.putShort(numsectors);
		for (int s = 0; s < numsectors; s++)
			bb.put(sector[s].getBytes());

		for (int i = 0; i < MAXSPRITES; i++)
			bb.put(sprite[i].getBytes());

		for (int i = 0; i <= MAXSECTORS; i++)
			bb.putShort(headspritesect[i]);

		for (int i = 0; i <= MAXSTATUS; i++)
			bb.putShort(headspritestat[i]);

		for (int i = 0; i < MAXSPRITES; i++) {
			bb.putShort(prevspritesect[i]);
			bb.putShort(prevspritestat[i]);
			bb.putShort(nextspritesect[i]);
			bb.putShort(nextspritestat[i]);
		}

		fil.writeBytes(bb);
	}

	private static void savePlayer(FileResource fil) {
		fil.writeBytes(PlayerList[nLocalPlayer].getBytes(true), PlayerStruct.size(true));

		fil.writeShort(connecthead);
		fil.writeBytes(connectpoint2, 8);

		fil.writeByte(bTorch);
		fil.writeInt(nFreeze);
		fil.writeInt(nXDamage[nLocalPlayer]);
		fil.writeInt(nYDamage[nLocalPlayer]);
		fil.writeShort(nDoppleSprite[nLocalPlayer]);
		fil.writeShort(nPlayerClip[nLocalPlayer]);
		fil.writeShort(nPistolClip[nLocalPlayer]);
		fil.writeShort(nPlayerTorch[nLocalPlayer]);
		fil.writeShort(nPlayerWeapons[nLocalPlayer]);
		fil.writeShort(nPlayerLives[nLocalPlayer]);
		fil.writeShort(nPlayerItem[nLocalPlayer]);
		fil.writeShort(nPlayerInvisible[nLocalPlayer]);
		fil.writeShort(nPlayerDouble[nLocalPlayer]);
		fil.writeShort(nPlayerViewSect[nLocalPlayer]);
		fil.writeShort(nPlayerFloorSprite[nLocalPlayer]);
		fil.writeShort(nPlayerScore[nLocalPlayer]);
		fil.writeShort(nPlayerGrenade[nLocalPlayer]);
		fil.writeShort(nPlayerSnake[nLocalPlayer]);
		fil.writeShort((short) nDestVertPan[nLocalPlayer]);
		fil.writeShort(dVertPan[nLocalPlayer]);
		fil.writeShort(nDeathType[nLocalPlayer]);
		fil.writeShort(nQuake[nLocalPlayer]);
		fil.writeByte(bTouchFloor[nLocalPlayer] ? 1 : 0);

		fil.writeInt(initx);
		fil.writeInt(inity);
		fil.writeInt(initz);
		fil.writeShort(inita);
		fil.writeShort(initsect);

		fil.writeBytes(show2dsector);
		fil.writeBytes(show2dwall);
		fil.writeBytes(show2dsprite);
	}

	private static void saveEnemies(FileResource fil) {
		fil.writeBytes(saveAnubis());
		fil.writeBytes(saveFish());
		fil.writeBytes(saveLava());
		fil.writeBytes(saveLion());
		fil.writeBytes(saveMummy());
		fil.writeBytes(saveQueen());
		fil.writeBytes(saveRa());
		fil.writeBytes(saveRat());
		fil.writeBytes(saveRex());
		fil.writeBytes(saveRoach());
		fil.writeBytes(saveScorp());
		fil.writeBytes(saveSet());
		fil.writeBytes(saveSpider());
		fil.writeBytes(saveWasp());
	}

	private static void saveSprites(FileResource fil) {
		saveEnemies(fil);

		fil.writeBytes(saveBullets());
		fil.writeBytes(saveGrenades());
		fil.writeBytes(saveBubbles());
		fil.writeBytes(saveSnakes());
		fil.writeBytes(saveObjects());
		fil.writeBytes(saveTraps());
		fil.writeBytes(saveDrips());

		fil.writeShort(nCreaturesLeft);
		fil.writeShort(nCreaturesMax);
		fil.writeShort(nSpiritSprite);
		fil.writeShort(nMagicCount);
		fil.writeShort(nRegenerates);
		fil.writeShort(nFirstRegenerate);
		fil.writeShort(nNetStartSprites);
		fil.writeShort(nCurStartSprite);
		fil.writeShort(nNetPlayerCount);
		fil.writeBytes(nNetStartSprite, 8);
		fil.writeBytes(nChunkSprite, nChunkSprite.length);
		fil.writeBytes(nBodyGunSprite, nBodyGunSprite.length);
		fil.writeBytes(nBodySprite, nBodySprite.length);
		fil.writeShort(nCurChunkNum);
		fil.writeShort(nCurBodyNum);
		fil.writeShort(nCurBodyGunNum);
		fil.writeShort(nBodyTotal);
		fil.writeShort(nChunkTotal);

		fil.writeShort(nRadialSpr);
		fil.writeShort(nRadialDamage);
		fil.writeShort(nDamageRadius);
		fil.writeShort(nRadialOwner);
		fil.writeShort(nRadialBullet);

		fil.writeShort(nDronePitch);
		fil.writeShort(nFinaleSpr);
		fil.writeShort(nFinaleStage);
		fil.writeShort(lFinaleStart);
		fil.writeShort(nSmokeSparks);
	}

	private static void saveSectors(FileResource fil) {
		fil.writeBytes(saveLights());
		fil.writeBytes(saveElevs());
		fil.writeBytes(saveBobs());
		fil.writeBytes(saveMoves());
		fil.writeBytes(saveTrails());
		fil.writeBytes(savePushBlocks());
		fil.writeBytes(saveSlide());
		fil.writeBytes(saveSwitches());
		fil.writeBytes(saveLinks());
		fil.writeBytes(saveSecExtra());

		fil.writeShort(nEnergyChan);
		fil.writeShort(nEnergyBlocks);
		fil.writeShort(nEnergyTowers);

		fil.writeShort(nSwitchSound);
		fil.writeShort(nStoneSound);
		fil.writeShort(nElevSound);
		fil.writeShort(nStopSound);

		fil.writeInt(lCountDown);
		fil.writeShort(nAlarmTicks);
		fil.writeShort(nRedTicks);
		fil.writeShort(nClockVal);
		fil.writeShort(nButtonColor);
	}

}
