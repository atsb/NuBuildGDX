package ru.m210projects.Witchaven;

import static ru.m210projects.Witchaven.Globals.*;
import static ru.m210projects.Witchaven.Main.*;
import static ru.m210projects.Witchaven.WHSND.*;
import static ru.m210projects.Witchaven.WHTAG.*;
import static ru.m210projects.Witchaven.WHPLR.*;
import static ru.m210projects.Witchaven.WHScreen.*;
import static ru.m210projects.Witchaven.WHFX.*;
import static ru.m210projects.Witchaven.Weapons.*;
import static ru.m210projects.Witchaven.Menu.WHMenuUserContent.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Witchaven.Factory.WHMenuHandler.CORRUPTLOAD;
import static ru.m210projects.Witchaven.Animate.*;
import static ru.m210projects.Witchaven.Types.ANIMATION.CEILZ;
import static ru.m210projects.Witchaven.Types.ANIMATION.FLOORZ;
import static ru.m210projects.Witchaven.Types.ANIMATION.WALLX;
import static ru.m210projects.Witchaven.Types.ANIMATION.WALLY;

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
import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Pattern.Tools.SaveManager;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Witchaven.Main.UserFlag;
import ru.m210projects.Witchaven.Factory.WHControl;
import ru.m210projects.Witchaven.Menu.MenuCorruptGame;
import ru.m210projects.Witchaven.Types.ANIMATION;
import ru.m210projects.Witchaven.Types.EpisodeInfo;
import ru.m210projects.Witchaven.Types.LSInfo;
import ru.m210projects.Witchaven.Types.PLAYER;
import ru.m210projects.Witchaven.Types.SafeLoader;

public class Whldsv {

	public static boolean gQuickSaving;
	public static boolean gAutosaveRequest;

	public static LSInfo lsInf = new LSInfo();

	public static final String savsign = "WHVN";

	public static final int gdxSave = 100;
	public static final int currentGdxSave = 104;

	public static final int SAVEVERSION = savsign.length() + 2; // version (2 bytes)
	public static final int SAVETIME = 8;
	public static final int SAVENAME = 32;
	public static final int SAVELEVELINFO = 8; // mapon + difficulty
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

				lsInf.addonfile = null;
				if (file.readByte() == 2) {
					String addon;
					String fullname = file.readString(144).trim();
					addon = FileUtils.getFullName(fullname);
					if (!addon.isEmpty())
						lsInf.addonfile = "Addon: " + addon;
				}

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
		stopallsounds();

		mapon = loader.mapon;
		difficulty = loader.skill;

		LoadGDXBlock();
		LoadStuff();
		MapLoad();
		SectorLoad();
		AnimationLoad();

		pyrn = myconnectindex;

		gPrecacheScreen.init(false, new Runnable() {
			@Override
			public void run() {
				game.nNetMode = NetMode.Single;
				if (mUserFlag != UserFlag.UserMap)
					startmusic(mapon - 1);
				else
					sndStopMusic();

				game.changeScreen(gGameScreen);
				game.pInput.resetMousePos();

				PLAYER plr = player[pyrn];
				((WHControl) game.pInput).reset();
				plr.hvel = 0;
				damage_angvel = 0;
				damage_svel = 0;
				damage_vel = 0;

				game.gPaused = false;
				totalclock = lockclock;
				game.pNet.ototalclock = lockclock;

				showmessage("Game loaded", 360);

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
			}
			return status;
		}

		return false;
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

	public static void SectorLoad() {
		System.arraycopy(loader.skypanlist, 0, skypanlist, 0, 64);
		skypancnt = loader.skypancnt;
		System.arraycopy(loader.lavadrylandsector, 0, lavadrylandsector, 0, 32);
		lavadrylandcnt = loader.lavadrylandcnt;
		System.arraycopy(loader.dragsectorlist, 0, dragsectorlist, 0, 16);
		System.arraycopy(loader.dragxdir, 0, dragxdir, 0, 16);
		System.arraycopy(loader.dragydir, 0, dragydir, 0, 16);
		System.arraycopy(loader.dragx1, 0, dragx1, 0, 16);
		System.arraycopy(loader.dragy1, 0, dragy1, 0, 16);
		System.arraycopy(loader.dragx2, 0, dragx2, 0, 16);
		System.arraycopy(loader.dragy2, 0, dragy2, 0, 16);
		System.arraycopy(loader.dragfloorz, 0, dragfloorz, 0, 16);

		dragsectorcnt = loader.dragsectorcnt;
		System.arraycopy(loader.bobbingsectorlist, 0, bobbingsectorlist, 0, 16);
		bobbingsectorcnt = loader.bobbingsectorcnt;

		System.arraycopy(loader.warpsectorlist, 0, warpsectorlist, 0, 16);

		warpsectorcnt = loader.warpsectorcnt;
		System.arraycopy(loader.xpanningsectorlist, 0, xpanningsectorlist, 0, 16);
		xpanningsectorcnt = loader.xpanningsectorcnt;
		System.arraycopy(loader.ypanningwalllist, 0, ypanningwalllist, 0, 128);
		ypanningwallcnt = loader.ypanningwallcnt;
		System.arraycopy(loader.floorpanninglist, 0, floorpanninglist, 0, 64);
		floorpanningcnt = loader.floorpanningcnt;

		swingcnt = loader.swingcnt;
		for (int i = 0; i < MAXSWINGDOORS; i++)
			swingdoor[i].copy(loader.swingdoor[i]);

		revolvecnt = loader.revolvecnt;
		System.arraycopy(loader.revolvesector, 0, revolvesector, 0, 4);
		System.arraycopy(loader.revolveang, 0, revolveang, 0, 4);
		System.arraycopy(loader.revolvepivotx, 0, revolvepivotx, 0, 4);
		System.arraycopy(loader.revolvepivoty, 0, revolvepivoty, 0, 4);
		for (int i = 0; i < 4; i++) {
			System.arraycopy(loader.revolvex[i], 0, revolvex[i], 0, 32);
			System.arraycopy(loader.revolvey[i], 0, revolvey[i], 0, 32);
		}
		System.arraycopy(loader.revolveclip, 0, revolveclip, 0, 16);

		ironbarscnt = loader.ironbarscnt;
		System.arraycopy(loader.ironbarsector, 0, ironbarsector, 0, 16);
		System.arraycopy(loader.ironbarsgoal, 0, ironbarsgoal, 0, 16);
		System.arraycopy(loader.ironbarsgoal1, 0, ironbarsgoal1, 0, 16);
		System.arraycopy(loader.ironbarsgoal2, 0, ironbarsgoal2, 0, 16);
		System.arraycopy(loader.ironbarsdone, 0, ironbarsdone, 0, 16);
		System.arraycopy(loader.ironbarsanim, 0, ironbarsanim, 0, 16);
	}

	public static void LoadStuff() {
		kills = loader.kills;
		killcnt = loader.killcnt;
		treasurescnt = loader.treasurescnt;
		treasuresfound = loader.treasuresfound;
		hours = loader.hours;
		minutes = loader.minutes;
		seconds = loader.seconds;

		visibility = loader.visibility;
		thunderflash = loader.thunderflash;
		thundertime = loader.thundertime;
		engine.srand(loader.randomseed);

		System.arraycopy(loader.show2dsector, 0, show2dsector, 0, (MAXSECTORS + 7) >> 3);
		System.arraycopy(loader.show2dwall, 0, show2dwall, 0, (MAXWALLSV7 + 7) >> 3);
		System.arraycopy(loader.show2dsprite, 0, show2dsprite, 0, (MAXSPRITES + 7) >> 3);
		automapping = loader.automapping;

		pskybits = loader.pskybits;
		parallaxyscale = loader.parallaxyscale;
		System.arraycopy(loader.pskyoff, 0, pskyoff, 0, MAXPSKYTILES);
		System.arraycopy(pskyoff, 0, zeropskyoff, 0, MAXPSKYTILES);

		connecthead = loader.connecthead;
		System.arraycopy(loader.connectpoint2, 0, connectpoint2, 0, MAXPLAYERS);

		for (int i = 0; i < MAXPLAYERS; i++)
			player[i].copy(loader.plr[i]);

		dropshieldcnt = loader.dropshieldcnt;
		droptheshield = loader.droptheshield;

		floormirrorcnt = loader.floormirrorcnt;
		System.arraycopy(loader.floormirrorsector, 0, floormirrorsector, 0, 64);
		System.arraycopy(loader.arrowsprite, 0, arrowsprite, 0, ARROWCOUNTLIMIT);
		System.arraycopy(loader.throwpikesprite, 0, throwpikesprite, 0, THROWPIKELIMIT);
		System.arraycopy(loader.ceilingshadearray, 0, ceilingshadearray, 0, MAXSECTORS);
		System.arraycopy(loader.floorshadearray, 0, floorshadearray, 0, MAXSECTORS);
		System.arraycopy(loader.wallshadearray, 0, wallshadearray, 0, MAXWALLS);
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

	public static void quickload() {
		if (numplayers > 1)
			return;
		System.err.println("load");

		final String loadname = game.pSavemgr.getLast();
		if (loadname != null && canLoad(loadname)) {
			game.changeScreen(gLoadScreen);
			gLoadScreen.init(new Runnable() {
				@Override
				public void run() {
					if (!loadgame(loadname)) {
						game.setPrevScreen();
						if (game.isCurrentScreen(gGameScreen))
							showmessage("Incompatible version of saved game found!", 360);
					}
				}
			});
		}
	}

	public static boolean canLoad(final String filename) {
		FileResource fil = BuildGdx.compat.open(filename, Path.User, Mode.Read);
		if (fil != null) {
			int nVersion = checkSave(fil) & 0xFFFF;

			if (nVersion != currentGdxSave) {
				if (nVersion >= gdxSave) {
					final EpisodeInfo addon = loader.LoadGDXHeader(fil);
					final EpisodeInfo ep = addon != null ? addon : gOriginalEpisode;
					if (ep != null && ep.getMap(loader.mapon) != null && loader.skill >= 0 && loader.skill < 5
							&& loader.warp_on != 1) { // not usermap
						MenuCorruptGame menu = (MenuCorruptGame) game.menu.mMenus[CORRUPTLOAD];
						menu.setRunnable(new Runnable() {
							@Override
							public void run() {
								gGameScreen.newgame(ep, loader.mapon, loader.skill);
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

	private static void save(FileResource fil, String savename, long time) {
		SaveHeader(fil, savename, time);
		SaveGDXBlock(fil); // ok
		StuffSave(fil);
		MapSave(fil);
		SectorSave(fil);
		AnimationSave(fil);
		fil.close();

		System.gc();
		showmessage("Game saved", 360);
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
			showmessage("Game not saved. Access denied!", 360);

		return -1;
	}

	public static void SaveHeader(FileResource fil, String savename, long time) {
		SaveVersion(fil, currentGdxSave);

		byte[] buf = new byte[8];
		LittleEndian.putLong(buf, 0, time);
		fil.writeBytes(buf, 8);
		fil.writeBytes(savename.toCharArray(), SAVENAME);

		fil.writeInt(mapon);
		fil.writeInt(difficulty);
	}

	public static void SaveGDXBlock(FileResource fil) {
		ByteBuffer bb = ByteBuffer.allocate(SAVEGDXDATA);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put(gGameScreen.captBuffer);
		gGameScreen.captBuffer = null;

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

	public static void SectorSave(FileResource fil) {
		ByteBuffer bb = ByteBuffer.allocate(5446);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		for (int i = 0; i < 64; i++)
			bb.putShort(skypanlist[i]);
		bb.putShort(skypancnt);
		for (int i = 0; i < 32; i++)
			bb.putShort(lavadrylandsector[i]);
		bb.putShort(lavadrylandcnt);
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
		bb.putShort(dragsectorcnt);
		for (int i = 0; i < 16; i++)
			bb.putShort(bobbingsectorlist[i]);
		bb.putShort(bobbingsectorcnt);
		for (int i = 0; i < 16; i++)
			bb.putShort(warpsectorlist[i]);
		bb.putShort(warpsectorcnt);
		for (int i = 0; i < 16; i++)
			bb.putShort(xpanningsectorlist[i]);
		bb.putShort(xpanningsectorcnt);
		for (int i = 0; i < 128; i++)
			bb.putShort(ypanningwalllist[i]);
		bb.putShort(ypanningwallcnt);
		for (int i = 0; i < 64; i++)
			bb.putShort(floorpanninglist[i]);
		bb.putShort(floorpanningcnt);
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

		bb.putShort(revolvecnt);
		for (int i = 0; i < 4; i++) {
			bb.putShort(revolvesector[i]);
			bb.putShort(revolveang[i]);
			for (int j = 0; j < 32; j++) {
				bb.putInt(revolvex[i][j]);
				bb.putInt(revolvey[i][j]);
			}
			bb.putInt(revolvepivotx[i]);
			bb.putInt(revolvepivoty[i]);
		}
		for (int i = 0; i < 16; i++)
			bb.putShort(revolveclip[i]);

		bb.putShort(ironbarscnt);
		for (int i = 0; i < 16; i++) {
			bb.putShort(ironbarsector[i]);
			bb.putInt(ironbarsgoal[i]);
			bb.putInt(ironbarsgoal1[i]);
			bb.putInt(ironbarsgoal2[i]);
			bb.putShort(ironbarsdone[i]);
			bb.putShort(ironbarsanim[i]);
		}

		fil.writeBytes(bb.array(), bb.capacity());
	}

	public static void StuffSave(FileResource fil) {
		int bufsize = 44 + show2dsector.length + show2dwall.length + show2dsprite.length + 7 + 2 * MAXPSKYTILES + 2
				+ 2 * MAXPLAYERS + MAXPLAYERS * PLAYER.sizeof + 4 + 128 + ARROWCOUNTLIMIT * 2 + THROWPIKELIMIT * 2
				+ 2 * MAXSECTORS + MAXWALLS + 5;

		ByteBuffer bb = ByteBuffer.allocate(bufsize);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		bb.putInt(kills);
		bb.putInt(killcnt);
		bb.putInt(treasurescnt);
		bb.putInt(treasuresfound);
		bb.putInt(hours);
		bb.putInt(minutes);
		bb.putInt(seconds);

		bb.putInt(visibility);
		bb.putInt(thunderflash);
		bb.putInt(thundertime);
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
			bb.put(player[i].getBytes());

		bb.putInt(dropshieldcnt);
		bb.put(droptheshield ? (byte) 1 : 0);

		// WH2
		bb.putInt(floormirrorcnt);
		for (int i = 0; i < 64; i++)
			bb.putShort(floormirrorsector[i]);
		for (int i = 0; i < ARROWCOUNTLIMIT; i++)
			bb.putShort(arrowsprite[i]);
		for (int i = 0; i < THROWPIKELIMIT; i++)
			bb.putShort(throwpikesprite[i]);

		for (int i = 0; i < MAXSECTORS; i++) {
			bb.put(ceilingshadearray[i]);
			bb.put(floorshadearray[i]);
		}
		for (int i = 0; i < MAXWALLS; i++)
			bb.put(wallshadearray[i]);

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
		if (player[myconnectindex].health != 0) {
			gQuickSaving = true;
		}
	}
}
