// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.LSP.Factory.LSPMenuHandler.*;
import static ru.m210projects.LSP.Main.*;
import static ru.m210projects.LSP.Sounds.*;
import static ru.m210projects.LSP.Globals.*;
import static ru.m210projects.LSP.Quotes.*;
import static ru.m210projects.LSP.Animate.*;
import static ru.m210projects.LSP.Enemies.*;
import static ru.m210projects.LSP.Types.ANIMATION.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileResource;
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
import ru.m210projects.LSP.Factory.LSPInput;
import ru.m210projects.LSP.Menus.MenuCorruptGame;
import ru.m210projects.LSP.Types.ANIMATION;
import ru.m210projects.LSP.Types.LSInfo;
import ru.m210projects.LSP.Types.SafeLoader;

public class LoadSave {

	public static boolean gQuickSaving;
	public static boolean gAutosaveRequest;

	public static LSInfo lsInf = new LSInfo();

	public static final String savsign = "LSP0";

	public static final int gdxSave = 100;
	public static final int currentGdxSave = 100;

	public static final int SAVEVERSION = savsign.length() + 2; // version (2 bytes)
	public static final int SAVETIME = 8;
	public static final int SAVENAME = 32;
	public static final int SAVELEVELINFO = 8; // level + difficulty
	public static final int SAVEHEADER = SAVEVERSION + SAVETIME + SAVENAME + SAVELEVELINFO;

	public static final int SAVESCREENSHOTSIZE = 160 * 100;
	public static final int SAVEGDXDATA = SAVESCREENSHOTSIZE + 144 + 128;

	public static String lastload;
	public static int quickslot = 0;
	public static SafeLoader loader = new SafeLoader();

	public static void FindSaves() {
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
			if (pic.data == null)
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

				file.read(pic.data, 0, SAVESCREENSHOTSIZE);

				engine.getrender().invalidatetile(SaveManager.Screenshot, 0, -1);
				file.close();
				return 1;
			} else
				lsInf.info = "Incompatible ver. " + nVersion + " != " + currentGdxSave;

			if (!file.isClosed())
				file.close();
		} else
			lsInf.clear();
		return -1;
	}

	public static final char[] filenum = new char[4];

	public static String makeNum(int num) {
		filenum[3] = (char) ((num % 10) + 48);
		filenum[2] = (char) (((num / 10) % 10) + 48);
		filenum[1] = (char) (((num / 100) % 10) + 48);
		filenum[0] = (char) (((num / 1000) % 10) + 48);

		return new String(filenum);
	}

	public static int checkSave(Resource bb) {
		String signature = bb.readString(4);
		if (signature == null || !signature.equals(savsign))
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

	public static void load() {
		if (rec != null)
			rec.close();
		recstat = 0;

		stopallsounds();

		mapnum = loader.mapnum;
		nDifficult = (short) loader.skill;

		LoadGDXBlock();
		LoadStuff();
		MapLoad();
		SectorLoad();
		AnimationLoad();

		screenpeek = myconnectindex;
		engine.setTilesPath(mapnum == 0 ? 0 : 1);

		short nexti;
		for (short i = headspritestat[ATTACK]; i >= 0; i = nexti) {
			nexti = nextspritestat[i];
			engine.changespritestat(i, GUARD);
			sprite[i].picnum = sprite[i].extra;
		}

		nKickSprite = -1;
		if (mapnum == 0) {
			for (short sec = 0; sec < numsectors; sec++) {
				for (short spr = headspritesect[sec]; spr != -1; spr = nextspritesect[spr]) {
					if (sprite[spr].picnum == 51) {
						nKickSprite = spr;
					}
				}
			}
		}

		gPlayer[screenpeek].isWeaponsSwitching = 0;
		gPlayer[screenpeek].nSwitchingClock = 0;

		int mnum = maps[mapnum].num & 0xFF;
		book = (mnum % 100) % 10;
		chapter = mnum / 100;
		verse = (mnum % 100) / 10;

		gPrecacheScreen.init(false, new Runnable() {
			@Override
			public void run() {
				game.nNetMode = NetMode.Single;
				if (mUserFlag != UserFlag.UserMap)
					startmusic(maps[mapnum].music - 1);
				else
					stopmusic();

				game.changeScreen(gGameScreen);
				game.pInput.resetMousePos();
				((LSPInput) game.pInput).reset();

				game.gPaused = false;
				totalclock = lockclock;
				game.pNet.ototalclock = lockclock;

				resetQuotes();
				viewSetMessage("Game loaded");

				System.gc();

				game.pNet.ResetTimers();
				game.pNet.ready2send = true;
			}
		});
		game.changeScreen(gPrecacheScreen);
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
				return true;
			}
		}

		viewSetMessage("Incompatible version of saved game found!");
		return false;
	}

	public static void LoadGDXBlock() {
		mUserFlag = UserFlag.None;
	}

	public static void SectorLoad() {
		System.arraycopy(loader.waterfountainwall, 0, waterfountainwall, 0, loader.waterfountainwall.length);
		System.arraycopy(loader.waterfountaincnt, 0, waterfountaincnt, 0, loader.waterfountaincnt.length);

		ypanningwallcnt = loader.ypanningwallcnt;
		System.arraycopy(loader.ypanningwalllist, 0, ypanningwalllist, 0, loader.ypanningwalllist.length);
		floorpanningcnt = loader.floorpanningcnt;
		System.arraycopy(loader.floorpanninglist, 0, floorpanninglist, 0, loader.floorpanninglist.length);

		warpsectorcnt = loader.warpsectorcnt;
		System.arraycopy(loader.warpsectorlist, 0, warpsectorlist, 0, loader.warpsectorlist.length);
		xpanningsectorcnt = loader.xpanningsectorcnt;
		System.arraycopy(loader.xpanningsectorlist, 0, xpanningsectorlist, 0, loader.xpanningsectorlist.length);
		warpsector2cnt = loader.warpsector2cnt;
		System.arraycopy(loader.warpsector2list, 0, warpsector2list, 0, loader.warpsector2list.length);
		subwaytrackcnt = loader.subwaytrackcnt;
		for (int i = 0; i < 5; i++)
			System.arraycopy(loader.subwaytracksector[i], 0, subwaytracksector[i], 0,
					loader.subwaytracksector[i].length);
		System.arraycopy(loader.subwaynumsectors, 0, subwaynumsectors, 0, loader.subwaynumsectors.length);
		for (int i = 0; i < 5; i++)
			System.arraycopy(loader.subwaystop[i], 0, subwaystop[i], 0, loader.subwaystop[i].length);
		System.arraycopy(loader.subwaystopcnt, 0, subwaystopcnt, 0, loader.subwaystopcnt.length);
		System.arraycopy(loader.subwaytrackx1, 0, subwaytrackx1, 0, loader.subwaytrackx1.length);
		System.arraycopy(loader.subwaytracky1, 0, subwaytracky1, 0, loader.subwaytracky1.length);
		System.arraycopy(loader.subwaytrackx2, 0, subwaytrackx2, 0, loader.subwaytrackx2.length);
		System.arraycopy(loader.subwaytracky2, 0, subwaytracky2, 0, loader.subwaytracky2.length);
		System.arraycopy(loader.subwayx, 0, subwayx, 0, loader.subwayx.length);
		System.arraycopy(loader.subwaygoalstop, 0, subwaygoalstop, 0, loader.subwaygoalstop.length);
		System.arraycopy(loader.subwayvel, 0, subwayvel, 0, loader.subwayvel.length);
		System.arraycopy(loader.subwaypausetime, 0, subwaypausetime, 0, loader.subwaypausetime.length);

		revolvecnt = loader.revolvecnt;
		System.arraycopy(loader.revolvesector, 0, revolvesector, 0, loader.revolvesector.length);
		System.arraycopy(loader.revolveang, 0, revolveang, 0, loader.revolveang.length);
		for (int i = 0; i < 4; i++)
			System.arraycopy(loader.revolvex[i], 0, revolvex[i], 0, loader.revolvex[i].length);
		for (int i = 0; i < 4; i++)
			System.arraycopy(loader.revolvey[i], 0, revolvey[i], 0, loader.revolvey[i].length);
		System.arraycopy(loader.revolvepivotx, 0, revolvepivotx, 0, loader.revolvepivotx.length);
		System.arraycopy(loader.revolvepivoty, 0, revolvepivoty, 0, loader.revolvepivoty.length);

		swingcnt = loader.swingcnt;
		for (int i = 0; i < MAXSWINGDOORS; i++)
			swingdoor[i].copy(loader.swingdoor[i]);

		dragsectorcnt = loader.dragsectorcnt;
		System.arraycopy(loader.dragsectorlist, 0, dragsectorlist, 0, loader.dragsectorlist.length);
		System.arraycopy(loader.dragxdir, 0, dragxdir, 0, loader.dragxdir.length);
		System.arraycopy(loader.dragydir, 0, dragydir, 0, loader.dragydir.length);
		System.arraycopy(loader.dragx1, 0, dragx1, 0, loader.dragx1.length);
		System.arraycopy(loader.dragy1, 0, dragy1, 0, loader.dragy1.length);
		System.arraycopy(loader.dragx2, 0, dragx2, 0, loader.dragx2.length);
		System.arraycopy(loader.dragy2, 0, dragy2, 0, loader.dragy2.length);
		System.arraycopy(loader.dragfloorz, 0, dragfloorz, 0, loader.dragfloorz.length);
	}

	public static void LoadStuff() {
		for (int i = 0; i < MAXSPRITES; i++) {
			gEnemyClock[i] = loader.gEnemyClock[i];
			gMoveStatus[i] = loader.gMoveStatus[i];
		}

		for (int i = 0; i < 6; i++) {
			nKills[i] = loader.nKills[i];
			nTotalKills[i] = loader.nTotalKills[i];
		}

		nEnemyKills = loader.nEnemyKills;
		nEnemyMax = loader.nEnemyMax;

		nDiffDoor = loader.nDiffDoor;
		nDiffDoorBack = loader.nDiffDoorBack;
		nTrainWall = loader.nTrainWall;
		bActiveTrain = loader.bActiveTrain;
		bTrainSoundSwitch = loader.bTrainSoundSwitch;
		lockclock = loader.lockclock;
		totalmoves = loader.totalmoves;

		visibility = loader.visibility;
		engine.srand(loader.randomseed);

		System.arraycopy(loader.show2dsector, 0, show2dsector, 0, (MAXSECTORS + 7) >> 3);
		System.arraycopy(loader.show2dwall, 0, show2dwall, 0, (MAXWALLSV7 + 7) >> 3);
		System.arraycopy(loader.show2dsprite, 0, show2dsprite, 0, (MAXSPRITES + 7) >> 3);
		automapping = loader.automapping;

		pskybits = loader.pskybits;
		parallaxyscale = loader.parallaxyscale;
		System.arraycopy(loader.pskyoff, 0, pskyoff, 0, MAXPSKYTILES);
		System.arraycopy(pskyoff, 0, zeropskyoff, 0, MAXPSKYTILES);
		parallaxtype = 0;
		parallaxyoffs = 256;

		connecthead = loader.connecthead;
		System.arraycopy(loader.connectpoint2, 0, connectpoint2, 0, MAXPLAYERS);

		for (int i = 0; i < MAXPLAYERS; i++)
			gPlayer[i].copy(loader.plr[i]);

		nPlayerFirstWeapon = loader.nPlayerFirstWeapon;
		oldchoose = loader.oldchoose;
		oldpic = loader.oldpic;
	}

	public static void MapLoad() {
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

	public static void AnimationLoad() {
		for (int i = 0; i < MAXANIMATES; i++) {
			gAnimationData[i].id = loader.gAnimationData[i].id;
			gAnimationData[i].type = loader.gAnimationData[i].type;
			gAnimationData[i].ptr = loader.gAnimationData[i].ptr;
			gAnimationData[i].goal = loader.gAnimationData[i].goal;
			gAnimationData[i].vel = loader.gAnimationData[i].vel;
			gAnimationData[i].acc = loader.gAnimationData[i].acc;
		}
		gAnimationCount = loader.gAnimationCount;

		for (int i = gAnimationCount - 1; i >= 0; i--) {
			ANIMATION gAnm = gAnimationData[i];
			Object object = (gAnm.ptr = getobject(gAnm.id, gAnm.type));
			switch (gAnm.type) {
			case WALLX:
			case WALLY:
				game.pInt.setwallinterpolate(gAnm.id, (WALL) object);
				break;
			case FLOORZ:
				game.pInt.setfloorinterpolate(gAnm.id, (SECTOR) object);
				break;
			case CEILZ:
				game.pInt.setceilinterpolate(gAnm.id, (SECTOR) object);
				break;
			}
		}
	}

	public static boolean canLoad(String filename) {
		FileResource fil = BuildGdx.compat.open(filename, Path.User, Mode.Read);
		if (fil != null) {
			int nVersion = checkSave(fil) & 0xFFFF;

			if (nVersion != currentGdxSave) {
				if (nVersion >= gdxSave) {
					loader.LoadGDXHeader(fil);

					if (loader.mapnum <= maps.length && loader.skill >= 0 && loader.skill < 4) {
						MenuCorruptGame menu = (MenuCorruptGame) game.menu.mMenus[CORRUPTLOAD];
						menu.setRunnable(new Runnable() {
							@Override
							public void run() {
								nDifficult = (short) loader.skill;
								gGameScreen.newgame(loader.mapnum);
							}
						});
						game.menu.mOpen(menu, -1);
					}
				}
			}

			fil.close();
			return nVersion == currentGdxSave;
		}
		return false;
	}

	public static void quickload() {
		if (numplayers > 1)
			return;
		System.err.println("load");

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
								viewSetMessage("Incompatible version of saved game found!");
								game.pNet.ready2send = true;
							}
						}
					}
				});
			}
		}
	}

	private static void save(FileResource fil, String savename, long time) {
		SaveHeader(fil, savename, time);
		SaveGDXBlock(fil);
		StuffSave(fil);
		MapSave(fil);
		SectorSave(fil);
		AnimationSave(fil);
		fil.close();

		System.gc();
		viewSetMessage("Game saved");
	}

	public static int savegame(String savename, String filename) {
		File file = BuildGdx.compat.checkFile(filename, Path.User);
		if (file != null)
			file.delete();

		FileResource fil = BuildGdx.compat.open(filename, Path.User, Mode.Write);
		if (fil != null) {
			long time = game.date.getCurrentDate();
			save(fil, savename, time);
			game.pSavemgr.add(savename, time, filename);
			lastload = filename;
			return 0;
		} else
			viewSetMessage("Game not saved. Access denied!");

		return -1;
	}

	public static void SaveHeader(FileResource fil, String savename, long time) {
		SaveVersion(fil, currentGdxSave);

		byte[] buf = new byte[8];
		LittleEndian.putLong(buf, 0, time);
		fil.writeBytes(buf, 8);
		fil.writeBytes(savename.toCharArray(), SAVENAME);

		fil.writeInt(mapnum);
		fil.writeInt(nDifficult);
	}

	public static void SaveGDXBlock(FileResource fil) {
		ByteBuffer bb = ByteBuffer.allocate(SAVEGDXDATA);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put(gGameScreen.captBuffer);
		gGameScreen.captBuffer = null;

		fil.writeBytes(bb.array(), SAVEGDXDATA);
	}

	public static void SectorSave(FileResource fil) {
		ByteBuffer bb = ByteBuffer.allocate(6848);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		for (int i = 0; i < MAXPLAYERS; i++)
			bb.putShort(waterfountainwall[i]);
		for (int i = 0; i < MAXPLAYERS; i++)
			bb.putShort(waterfountaincnt[i]);

		bb.putShort(ypanningwallcnt);
		for (int i = 0; i < 16; i++)
			bb.putShort(ypanningwalllist[i]);
		bb.putShort(floorpanningcnt);
		for (int i = 0; i < 16; i++)
			bb.putShort(floorpanninglist[i]);
		bb.putShort(warpsectorcnt);
		for (int i = 0; i < 16; i++)
			bb.putShort(warpsectorlist[i]);
		bb.putShort(xpanningsectorcnt);
		for (int i = 0; i < 16; i++)
			bb.putShort(xpanningsectorlist[i]);
		bb.putShort(warpsector2cnt);
		for (int i = 0; i < 32; i++)
			bb.putShort(warpsector2list[i]);

		bb.putShort(subwaytrackcnt);
		for (int a = 0; a < 5; ++a)
			for (int b = 0; b < 128; ++b)
				bb.putShort(subwaytracksector[a][b]);
		for (int i = 0; i < 5; i++)
			bb.putShort(subwaynumsectors[i]);

		for (int a = 0; a < 5; ++a)
			for (int b = 0; b < 8; ++b)
				bb.putInt(subwaystop[a][b]);
		for (int i = 0; i < 5; i++)
			bb.putInt(subwaystopcnt[i]);
		for (int i = 0; i < 5; i++)
			bb.putInt(subwaytrackx1[i]);
		for (int i = 0; i < 5; i++)
			bb.putInt(subwaytracky1[i]);
		for (int i = 0; i < 5; i++)
			bb.putInt(subwaytrackx2[i]);
		for (int i = 0; i < 5; i++)
			bb.putInt(subwaytracky2[i]);
		for (int i = 0; i < 5; i++)
			bb.putInt(subwayx[i]);
		for (int i = 0; i < 5; i++)
			bb.putInt(subwaygoalstop[i]);
		for (int i = 0; i < 5; i++)
			bb.putInt(subwayvel[i]);
		for (int i = 0; i < 5; i++)
			bb.putInt(subwaypausetime[i]);

		bb.putShort(revolvecnt);
		for (int i = 0; i < 4; i++) {
			bb.putShort(revolvesector[i]);
			bb.putShort(revolveang[i]);
			for (int j = 0; j < 48; j++) {
				bb.putInt(revolvex[i][j]);
				bb.putInt(revolvey[i][j]);
			}
			bb.putInt(revolvepivotx[i]);
			bb.putInt(revolvepivoty[i]);
		}

		bb.putShort(swingcnt);
		for (int i = 0; i < MAXSWINGDOORS; i++) {
			for (int j = 0; j < 8; j++)
				bb.putShort((short) swingdoor[i].wall[j]);
			bb.putShort((short) swingdoor[i].sector);
			bb.putShort((short) swingdoor[i].angopen);
			bb.putShort((short) swingdoor[i].angclosed);
			bb.putShort((short) swingdoor[i].angopendir);
			bb.putShort((short) swingdoor[i].ang);
			bb.putShort((short) swingdoor[i].anginc);
			for (int j = 0; j < 8; j++)
				bb.putInt(swingdoor[i].x[j]);
			for (int j = 0; j < 8; j++)
				bb.putInt(swingdoor[i].y[j]);
		}

		bb.putShort(dragsectorcnt);
		for (int i = 0; i < 16; i++) {
			bb.putShort(dragsectorlist[i]);
			bb.putShort(dragxdir[i]);
			bb.putShort(dragydir[i]);
			bb.putInt(dragx1[i]);
			bb.putInt(dragy1[i]);
			bb.putInt(dragx2[i]);
			bb.putInt(dragy2[i]);
			bb.putInt(dragfloorz[i]);
		}

		fil.writeBytes(bb.array(), bb.capacity());
	}

	public static void StuffSave(FileResource fil) {
		int bufsize = 29481;

		ByteBuffer bb = ByteBuffer.allocate(bufsize);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		for (int i = 0; i < MAXSPRITES; i++) {
			bb.putInt(gEnemyClock[i]);
			bb.putShort(gMoveStatus[i]);
		}

		for (int i = 0; i < 6; i++) {
			bb.putShort(nKills[i]);
			bb.putShort(nTotalKills[i]);
		}
		bb.putInt(nEnemyKills);
		bb.putInt(nEnemyMax);

		bb.putShort(nDiffDoor);
		bb.putShort(nDiffDoorBack);
		bb.putShort(nTrainWall);
		bb.put(bActiveTrain ? (byte) 1 : 0);
		bb.put(bTrainSoundSwitch ? (byte) 1 : 0);
		bb.putInt(lockclock);
		bb.putInt(totalmoves);

		bb.putInt(visibility);
		bb.putInt(engine.getrand());

		bb.put(show2dsector);
		bb.put(show2dwall);
		bb.put(show2dsprite);
		bb.put(automapping);

		bb.putShort(pskybits);
		bb.putInt(parallaxyscale);
		for (int i = 0; i < MAXPSKYTILES; i++)
			bb.putShort(pskyoff[i]);

		bb.putShort(connecthead);
		for (int i = 0; i < MAXPLAYERS; i++)
			bb.putShort(connectpoint2[i]);

		for (int i = 0; i < MAXPLAYERS; i++)
			bb.put(gPlayer[i].getBytes());
		bb.putShort(nPlayerFirstWeapon);
		bb.putInt(oldchoose);
		bb.putShort(oldpic);

		fil.writeBytes(bb.array(), bb.capacity());
	}

	public static void MapSave(FileResource fil) {
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

		// Store all sprites (even holes) to preserve indeces
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

		fil.writeBytes(bb.array(), bb.capacity());
	}

	public static void SaveVersion(FileResource fil, int nVersion) {
		fil.writeBytes(savsign.toCharArray(), 4);
		fil.writeShort(nVersion);
	}

	public static void AnimationSave(FileResource fil) {
		for (int i = 0; i < MAXANIMATES; i++) {
			fil.writeShort(gAnimationData[i].id);
			fil.writeByte(gAnimationData[i].type);
			fil.writeInt(gAnimationData[i].goal);
			fil.writeInt(gAnimationData[i].vel);
			fil.writeInt(gAnimationData[i].acc);
		}
		fil.writeInt(gAnimationCount);
	}

	public static void quicksave() {
		if (gPlayer[myconnectindex].nHealth != 0) {
			gQuickSaving = true;
		}
	}

}
