// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.*;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.Sector.*;
import static ru.m210projects.Duke3D.Screen.*;
import static ru.m210projects.Duke3D.View.*;
import static ru.m210projects.Duke3D.ResourceHandler.InitSpecialTextures;
import static ru.m210projects.Duke3D.ResourceHandler.checkEpisodeResources;
import static ru.m210projects.Duke3D.ResourceHandler.resetEpisodeResources;
import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Duke3D.Player.*;
import static ru.m210projects.Duke3D.Sounds.*;
import static ru.m210projects.Duke3D.Types.ANIMATION.CEILZ;
import static ru.m210projects.Duke3D.Types.ANIMATION.FLOORZ;
import static ru.m210projects.Duke3D.Types.ANIMATION.WALLX;
import static ru.m210projects.Duke3D.Types.ANIMATION.WALLY;
import static ru.m210projects.Duke3D.SoundDefs.*;
import static ru.m210projects.Duke3D.Animate.*;
import static ru.m210projects.Duke3D.Gamedef.*;
import static ru.m210projects.Duke3D.Main.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileResource;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.FileResource.Mode;
import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Pattern.Tools.SaveManager;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Duke3D.Menus.MenuCorruptGame;
import ru.m210projects.Duke3D.Types.ANIMATION;
import ru.m210projects.Duke3D.Types.GameInfo;
import ru.m210projects.Duke3D.Types.LSInfo;
import ru.m210projects.Duke3D.Types.PlayerOrig;
import ru.m210projects.Duke3D.Types.PlayerStruct;
import ru.m210projects.Duke3D.Types.SafeLoader;
import ru.m210projects.Duke3D.Types.Weaponhit;

public class LoadSave {
	public static boolean gQuickSaving;
	public static boolean gAutosaveRequest;

	public static LSInfo lsInf = new LSInfo();
	public static SafeLoader loader = new SafeLoader();

	public static final String savsign = "DUKE";

	public static final int gdxSave = 100;
	public static final int currentGdxSave = 100;
	public static final int SAVEVERSION = savsign.length() + 2; // version (2 bytes)
	public static final int SAVETIME = 8;
	public static final int SAVENAME = 32;
	public static final int SAVELEVELINFO = 16;
	public static final int SAVEHEADER = SAVEVERSION + SAVETIME + SAVENAME + SAVELEVELINFO;

	public static final int SAVESCREENSHOTSIZE = 160 * 100;
	public static final int SAVEGDXDATA = SAVESCREENSHOTSIZE + 128;

	public static String lastload;
	public static int quickslot = 0;

	public static void FindSaves() {
		FileResource fil = null;
		for (FileEntry file : BuildGdx.compat.getDirectory(Path.User).getFiles().values()) {
			if (file.getExtension().equals("sav")) {
				fil = BuildGdx.compat.open(file);
				if (fil != null) {
					String signature = fil.readString(4);
					if (signature == null) {
						fil.close();
						continue;
					}

					if (signature.equals(savsign)) {
						int nVersion = fil.readShort();
						if (nVersion == gdxSave) {
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
		FileResource fil = BuildGdx.compat.open(filename, Path.User, Mode.Read);
		if (fil != null) {
			Tile pic = engine.getTile(SaveManager.Screenshot);
			if (pic.data == null)
				engine.allocatepermanenttile(SaveManager.Screenshot, 160, 100);

			int nVersion = checkSave(fil) & 0xFFFF;
			lsInf.clear();

			if (nVersion == gdxSave) {
				fil.seek(SAVEVERSION, Whence.Set);
				lsInf.date = game.date.getDate(fil.readLong());
				fil.seek(SAVEVERSION + SAVETIME + SAVENAME, Whence.Set); // to SAVELEVELINFO

				lsInf.read(fil);
				if (fil.remaining() <= SAVESCREENSHOTSIZE) {
					fil.close();
					return -1;
				}

				fil.read(pic.data, 0, SAVESCREENSHOTSIZE);

				lsInf.addonfile = null;
				if (fil.readBoolean()) {
					String ininame;
					String fullname = fil.readString(144).trim();
					int filenameIndex = -1;
					if ((filenameIndex = fullname.indexOf(":")) != -1) {
						ininame = FileUtils.getFullName(fullname.substring(0, filenameIndex));
					} else
						ininame = FileUtils.getFullName(fullname);

					if (!ininame.isEmpty())
						lsInf.addonfile = "File: " + ininame;
				}

				engine.getrender().invalidatetile(SaveManager.Screenshot, 0, -1);
				fil.close();
				return 1;
			}
			if (!fil.isClosed())
				fil.close();
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

			addmessage("GAME SAVED");
			return 0;
		} else
			addmessage("Game not saved. Access denied!");

		return -1;
	}

	public static void MapSave(FileResource fil) {
		if (boardfilename != null)
			fil.writeBytes(boardfilename.toCharArray(), 144);
		else
			fil.writeBytes(new byte[144], 144);

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

		fil.writeBytes(bb.array(), bb.capacity());
	}

	public static void StuffSave(FileResource fil) {
		int bufsize = 2 + MAXCYCLERS * 6 * 2 + MAXPLAYERS * PlayerStruct.sizeof + MAXPLAYERS * PlayerOrig.sizeof + 2
				+ MAXANIMWALLS * 6 + 2048 * 8 + 4 + 1024 * 2 + 2 + 64 * 4 + show2dsector.length + 2 + 128 * 6;

		ByteBuffer bb = ByteBuffer.allocate(bufsize);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		bb.putShort(numcyclers);
		for (int i = 0; i < MAXCYCLERS; i++)
			for (int j = 0; j < 6; j++)
				bb.putShort(cyclers[i][j]);
		for (int i = 0; i < MAXPLAYERS; i++)
			bb.put(ps[i].getBytes());
		for (int i = 0; i < MAXPLAYERS; i++)
			bb.put(po[i].getBytes());
		bb.putShort(numanimwalls);
		for (int i = 0; i < MAXANIMWALLS; i++) {
			bb.putShort(animwall[i].wallnum);
			bb.putInt(animwall[i].tag);
		}
		for (int i = 0; i < 2048; i++)
			bb.putInt(msx[i]);
		for (int i = 0; i < 2048; i++)
			bb.putInt(msy[i]);

		bb.putShort(spriteqloc);
		bb.putShort(currentGame.getCON().spriteqamount);
		for (int i = 0; i < 1024; i++)
			bb.putShort(spriteq[i]);

		bb.putShort(mirrorcnt);
		for (int i = 0; i < 64; i++)
			bb.putShort(mirrorwall[i]);
		for (int i = 0; i < 64; i++)
			bb.putShort(mirrorsector[i]);

		bb.put(show2dsector);
		bb.putShort(numclouds);
		for (int i = 0; i < 128; i++)
			bb.putShort(clouds[i]);
		for (int i = 0; i < 128; i++)
			bb.putShort(cloudx[i]);
		for (int i = 0; i < 128; i++)
			bb.putShort(cloudy[i]);

		fil.writeBytes(bb.array(), bb.capacity());
	}

	public static void ConSave(FileResource fil) {
		int bufsiz = MAXTILES + 4 * MAXSCRIPTSIZE + 4 * MAXTILES + MAXSPRITES * Weaponhit.sizeof;

		ByteBuffer bb = ByteBuffer.allocate(bufsiz);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < MAXTILES; i++)
			bb.put((byte) currentGame.getCON().actortype[i]);
		for (int i = 0; i < MAXSCRIPTSIZE; i++)
			bb.putInt(currentGame.getCON().script[i]);
		for (int i = 0; i < MAXTILES; i++)
			bb.putInt(currentGame.getCON().actorscrptr[i]);
		for (int i = 0; i < MAXSPRITES; i++)
			bb.put(hittype[i].getBytes());

		fil.writeBytes(bb.array(), bb.capacity());
	}

	public static void Stuff2Save(FileResource fil) {
		ByteBuffer bb = ByteBuffer.allocate(1113);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putShort(pskybits);
		bb.putInt(parallaxyscale);
		for (int i = 0; i < MAXPSKYTILES; i++)
			bb.putShort(pskyoff[i]);

		bb.putShort(earthquaketime);
		bb.putShort((short) ud.from_bonus);
		bb.putShort((short) ud.secretlevel);
		bb.put(ud.respawn_monsters ? (byte) 1 : 0);
		bb.put(ud.respawn_items ? (byte) 1 : 0);
		bb.put(ud.respawn_inventory ? (byte) 1 : 0);
		bb.put(ud.god ? (byte) 1 : 0);
		bb.putInt(ud.auto_run);
		bb.putInt(ud.crosshair);
		bb.put(ud.monsters_off ? (byte) 1 : 0);
		bb.putInt(ud.last_level);
		bb.putInt(ud.eog);
		bb.putInt(ud.coop);
		bb.putInt(ud.marker);
		bb.putInt(ud.ffire);
		bb.putShort(camsprite);

		bb.putShort(connecthead);
		for (int i = 0; i < MAXPLAYERS; i++)
			bb.putShort(connectpoint2[i]);
		bb.putShort(numplayersprites);

		for (int i = 0; i < MAXPLAYERS; i++)
			for (int j = 0; j < MAXPLAYERS; j++)
				bb.putShort(frags[i][j]);
		bb.putInt(engine.getrand());
		bb.putShort(global_random);

		fil.writeBytes(bb.array(), bb.capacity());
	}

	public static void AnimationSave(FileResource fil) {
		for (int i = 0; i < MAXANIMATES; i++) {
			fil.writeShort(gAnimationData[i].id);
			fil.writeByte(gAnimationData[i].type);
			fil.writeInt(gAnimationData[i].goal);
			fil.writeInt(gAnimationData[i].vel);
			fil.writeShort(gAnimationData[i].sect);
		}
		fil.writeInt(gAnimationCount);
	}

	public static void SaveVersion(FileResource fil, int nVersion) {
		fil.writeBytes(savsign.toCharArray(), 4);
		fil.writeShort(nVersion);
	}

	public static void SaveHeader(FileResource fil, String savename, long time) {
		SaveVersion(fil, gdxSave);

		byte[] buf = new byte[8];
		LittleEndian.putLong(buf, 0, time);
		fil.writeBytes(buf, 8);
		fil.writeBytes(savename.toCharArray(), SAVENAME);

		fil.writeInt(ud.multimode);
		fil.writeInt(ud.volume_number);
		fil.writeInt(ud.level_number);
		fil.writeInt(ud.player_skill);
	}

	public static void SaveScreenshot(FileResource fil) {
		if (gGameScreen.captBuffer != null)
			fil.writeBytes(gGameScreen.captBuffer, SAVESCREENSHOTSIZE);
		else
			fil.writeBytes(new byte[SAVESCREENSHOTSIZE], SAVESCREENSHOTSIZE);
		gGameScreen.captBuffer = null;
	}

	public static void SaveGDXBlock(FileResource fil) {
		SaveScreenshot(fil);

		ByteBuffer bb = ByteBuffer.allocate(SAVEGDXDATA);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		byte warp_on = 0;
		if (mUserFlag == UserFlag.Addon)
			warp_on = 1;
		if (mUserFlag == UserFlag.UserMap)
			warp_on = 2;

		bb.put(warp_on);
		if (warp_on == 1) // user episode
		{
			byte[] name = new byte[144];
			if (currentGame != null) {
				FileEntry addon = currentGame.getFile();
				String path;
				if (addon != null && currentGame.isPackage()) {
					path = addon.getPath();
					path += ":" + currentGame.ConName;
				} else {
					path = currentGame.getDirectory().checkFile(currentGame.ConName).getPath();
				}
				System.arraycopy(path.getBytes(), 0, name, 0, Math.min(path.length(), 144));
			}
			bb.put(name);
		}
		fil.writeBytes(bb.array(), SAVEGDXDATA);
	}

	public static void save(FileResource fil, String savename, long time) {
		SaveHeader(fil, savename, time);
		SaveGDXBlock(fil);

		MapSave(fil);
		StuffSave(fil);
		ConSave(fil);
		AnimationSave(fil);
		Stuff2Save(fil);

		fil.close();

		System.gc();
	}

	public static void quicksave() {
		if (numplayers > 1 || mFakeMultiplayer)
			return;
		if (sprite[ps[myconnectindex].i].extra > 0) {
			gQuickSaving = true;
		}
	}

	public static boolean canLoad(String filename) {
		FileResource fil = BuildGdx.compat.open(filename, Path.User, Mode.Read);
		if (fil != null) {
			int nVersion = checkSave(fil) & 0xFFFF;

			if (nVersion != currentGdxSave) {
				if (nVersion >= gdxSave) {
					final GameInfo addon = loader.LoadGDXHeader(fil);

					if (loader.level_number <= nMaxMaps && loader.volume_number < nMaxEpisodes
							&& loader.player_skill >= 0 && loader.player_skill < nMaxSkills && loader.warp_on != 2) {
						MenuCorruptGame menu = (MenuCorruptGame) game.menu.mMenus[CORRUPTLOAD];
						menu.setRunnable(new Runnable() {
							@Override
							public void run() {
								GameInfo game = addon != null ? addon : defGame;
								int nEpisode = loader.volume_number;
								int nLevel = loader.level_number;
								int nSkill = loader.player_skill - 1;
								gGameScreen.newgame(false, game, nEpisode, nLevel, nSkill);
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
		final String loadname = game.pSavemgr.getLast();
		if (loadname != null) {
			if (canLoad(loadname)) {
				game.changeScreen(gLoadingScreen.setTitle(loadname));
				gLoadingScreen.init(new Runnable() {
					@Override
					public void run() {
						if (!loadgame(loadname))
							game.setPrevScreen();
					}
				});
			}
		}
	}

	public static void AnimationLoad() {
		for (int i = 0; i < MAXANIMATES; i++) {
			gAnimationData[i].id = loader.gAnimationData[i].id;
			gAnimationData[i].type = loader.gAnimationData[i].type;
			gAnimationData[i].ptr = loader.gAnimationData[i].ptr;
			gAnimationData[i].goal = loader.gAnimationData[i].goal;
			gAnimationData[i].vel = loader.gAnimationData[i].vel;
			gAnimationData[i].sect = loader.gAnimationData[i].sect;
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

	public static void ConLoad() {
		System.arraycopy(loader.actortype, 0, currentGame.getCON().actortype, 0, MAXTILES);
		System.arraycopy(loader.script, 0, currentGame.getCON().script, 0, MAXSCRIPTSIZE);
		System.arraycopy(loader.actorscrptr, 0, currentGame.getCON().actorscrptr, 0, MAXTILES);

		for (int i = 0; i < MAXSPRITES; i++)
			hittype[i].set(loader.hittype[i]);
	}

	public static void Stuff2Load() {
		pskybits = loader.pskybits;
		parallaxyscale = loader.parallaxyscale;
		System.arraycopy(loader.pskyoff, 0, pskyoff, 0, MAXPSKYTILES);
		System.arraycopy(pskyoff, 0, zeropskyoff, 0, MAXPSKYTILES);

		earthquaketime = loader.earthquaketime;
		ud.from_bonus = loader.from_bonus;
		ud.secretlevel = loader.secretlevel;
		ud.respawn_monsters = loader.respawn_monsters;
		ud.respawn_items = loader.respawn_items;
		ud.respawn_inventory = loader.respawn_inventory;
		ud.god = loader.god;
		ud.auto_run = loader.auto_run;
		ud.crosshair = loader.crosshair;
		ud.monsters_off = loader.monsters_off;
		ud.last_level = loader.last_level;
		ud.eog = loader.eog;
		ud.coop = loader.coop;
		ud.marker = loader.marker;
		ud.ffire = loader.ffire;
		camsprite = loader.camsprite;

		connecthead = loader.connecthead;
		System.arraycopy(loader.connectpoint2, 0, connectpoint2, 0, MAXPLAYERS);
		numplayersprites = loader.numplayersprites;

		for (int i = 0; i < MAXPLAYERS; i++)
			System.arraycopy(loader.frags[i], 0, frags[i], 0, MAXPLAYERS);

		engine.srand(loader.randomseed);
		global_random = loader.global_random;
	}

	public static void StuffLoad() {
		numcyclers = loader.numcyclers;
		for (int i = 0; i < MAXCYCLERS; i++)
			System.arraycopy(loader.cyclers[i], 0, cyclers[i], 0, 6);

		for (int i = 0; i < MAXPLAYERS; i++)
			ps[i].copy(loader.ps[i]);
		for (int i = 0; i < MAXPLAYERS; i++)
			po[i].copy(loader.po[i]);

		numanimwalls = loader.numanimwalls;
		for (int i = 0; i < MAXANIMWALLS; i++) {
			animwall[i].wallnum = loader.animwall[i].wallnum;
			animwall[i].tag = loader.animwall[i].tag;
		}
		System.arraycopy(loader.msx, 0, msx, 0, 2048);
		System.arraycopy(loader.msy, 0, msy, 0, 2048);

		spriteqloc = loader.spriteqloc;
		currentGame.getCON().spriteqamount = loader.spriteqamount;
		System.arraycopy(loader.spriteq, 0, spriteq, 0, 1024);

		mirrorcnt = loader.mirrorcnt;
		System.arraycopy(loader.mirrorwall, 0, mirrorwall, 0, 64);
		System.arraycopy(loader.mirrorsector, 0, mirrorsector, 0, 64);
		System.arraycopy(loader.show2dsector, 0, show2dsector, 0, (MAXSECTORS + 7) >> 3);

		numclouds = loader.numclouds;
		System.arraycopy(loader.clouds, 0, clouds, 0, 128);
		System.arraycopy(loader.cloudx, 0, cloudx, 0, 128);
		System.arraycopy(loader.cloudy, 0, cloudy, 0, 128);
	}

	public static void MapLoad() {
		boardfilename = loader.boardfilename;
		numwalls = loader.numwalls;
		for (int w = 0; w < numwalls; w++) {
			if (wall[w] == null)
				wall[w] = new WALL();
			wall[w].set(loader.wall[w]);
		}

		numsectors = loader.numsectors;
		for (int s = 0; s < numsectors; s++) {
			if (sector[s] == null)
				sector[s] = new SECTOR();
			sector[s].set(loader.sector[s]);
		}

		// Store all sprites (even holes) to preserve indeces
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

	public static void LoadGDXBlock() {
		if (loader.warp_on == 0)
			mUserFlag = UserFlag.None;
		if (loader.warp_on == 1)
			mUserFlag = UserFlag.Addon;
		if (loader.warp_on == 2)
			mUserFlag = UserFlag.UserMap;

		if (mUserFlag == UserFlag.Addon) {
			GameInfo ini = loader.addon;
			checkEpisodeResources(ini);
		} else
			resetEpisodeResources();
	}

	public static boolean load() {
		ud.multimode = loader.multimode;
		ud.volume_number = loader.volume_number;
		ud.level_number = loader.level_number;
		ud.player_skill = loader.player_skill;

		LoadGDXBlock();
		MapLoad();
		StuffLoad();
		ConLoad();
		AnimationLoad();
		Stuff2Load();

		if (over_shoulder_on != 0) {
			cameradist = 0;
			cameraclock = 0;
			over_shoulder_on = 1;
		}

		screenpeek = myconnectindex;
		if (ud.rec != null)
			ud.rec.close();
		ud.recstat = 0;

		if (ud.lockout == 0) {
			for (int x = 0; x < numanimwalls; x++)
				if (wall[animwall[x].wallnum].extra >= 0)
					wall[animwall[x].wallnum].picnum = wall[animwall[x].wallnum].extra;
		} else {
			for (int x = 0; x < numanimwalls; x++)
				switch (wall[animwall[x].wallnum].picnum) {
				case FEMPIC1:
					wall[animwall[x].wallnum].picnum = BLANKSCREEN;
					break;
				case FEMPIC2:
				case FEMPIC3:
					wall[animwall[x].wallnum].picnum = SCREENBREAK6;
					break;
				}
		}

		startofdynamicinterpolations = 0;

		int k = headspritestat[3];
		while (k >= 0) {
			switch (sprite[k].lotag) {
			case 31:
			case 32:
			case 25:
			case 17:
				game.pInt.setfheinuminterpolate(sprite[k].sectnum, sector[sprite[k].sectnum]);
				break;
			case 0:
			case 5:
			case 6:
			case 11:
			case 14:
			case 15:
			case 16:
			case 26:
			case 30:
				setsectinterpolate(k);
				break;
			}

			k = nextspritestat[k];
		}

		fta = 0;
		everyothertime = 0;

		gPrecacheScreen.init(false, new Runnable() {
			@Override
			public void run() {
				InitSpecialTextures();
				clearsoundlocks();

				userMusic = null;
				if (boardfilename != null) {
					FileEntry file = BuildGdx.compat.checkFile(boardfilename);
					if (file != null)
						sndCheckMusic(file);
				}

				musicvolume = ud.volume_number;
				musiclevel = ud.level_number;
				sndPlayMusic(currentGame.getCON().music_fn[ud.volume_number][ud.level_number]);

				if (ps[myconnectindex].jetpack_on != 0)
					spritesound(DUKE_JETPACK_IDLE, ps[myconnectindex].i);

				setpal(ps[myconnectindex]);
				vscrn(ud.screen_size);

				BuildGdx.audio.getSound().setReverb(false, 0);

				game.pInput.resetMousePos();
				game.net.predict.reset();
				game.gPaused = false;

				game.nNetMode = NetMode.Single;

				game.changeScreen(gGameScreen);
				game.pNet.ResetTimers();
				game.pNet.WaitForAllPlayers(0);
				game.pNet.ready2send = true;

				StopAllSounds();

				System.gc();
			}
		});
		game.changeScreen(gPrecacheScreen);

		return true;
	}

	public static boolean checkfile(Resource bb) {
		int nVersion = checkSave(bb);

		if (nVersion != gdxSave)
			return false;

		return loader.load(bb);
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

				if (loader.getMessage() != null)
					addmessage(loader.getMessage());

				return true;
			}

			addmessage("Incompatible version of saved game found!");
			return false;
		}

		addmessage("Can't access to file or file not found!");
		return false;
	}
}
