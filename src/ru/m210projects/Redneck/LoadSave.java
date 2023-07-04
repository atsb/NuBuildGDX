// This file is part of RedneckGDX.
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.CORRUPTLOAD;
import static ru.m210projects.Redneck.Globals.defGame;
import static ru.m210projects.Redneck.Globals.nMaxEpisodes;
import static ru.m210projects.Redneck.Globals.nMaxMaps;
import static ru.m210projects.Redneck.Globals.nMaxSkills;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Redneck.View.*;
import static ru.m210projects.Redneck.ResourceHandler.*;
import static ru.m210projects.Redneck.Player.*;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.Actors.*;
import static ru.m210projects.Redneck.Types.ANIMATION.CEILZ;
import static ru.m210projects.Redneck.Types.ANIMATION.FLOORZ;
import static ru.m210projects.Redneck.Types.ANIMATION.WALLX;
import static ru.m210projects.Redneck.Types.ANIMATION.WALLY;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.Premap.*;
import static ru.m210projects.Redneck.Animate.*;
import static ru.m210projects.Redneck.Gamedef.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Sector.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
import ru.m210projects.Redneck.Menus.MenuCorruptGame;
import ru.m210projects.Redneck.Types.ANIMATION;
import ru.m210projects.Redneck.Types.GameInfo;
import ru.m210projects.Redneck.Types.LSInfo;
import ru.m210projects.Redneck.Types.PlayerOrig;
import ru.m210projects.Redneck.Types.PlayerStruct;
import ru.m210projects.Redneck.Types.SafeLoader;
import ru.m210projects.Redneck.Types.Weaponhit;

public class LoadSave {

	public static boolean gQuickSaving;
	public static boolean gAutosaveRequest;

	public static LSInfo lsInf = new LSInfo();

	public static final String savsign = "RGDX";

	public static final int gdxSave = 100;
	public static final int currentGdxSave = 102;
	public static final int SAVEVERSION = savsign.length() + 2; // version (2 bytes)
	public static final int SAVETIME = 8;
	public static final int SAVENAME = 32;
	public static final int SAVELEVELINFO = 16;
	public static final int SAVEHEADER = SAVEVERSION + SAVETIME + SAVENAME + SAVELEVELINFO;

	public static final int SAVESCREENSHOTSIZE = 160 * 100;
	public static final int SAVEGDXDATA = SAVESCREENSHOTSIZE + 128;

	public static String lastload;
	public static int quickslot = 0;
	public static SafeLoader loader = new SafeLoader();

	public static void FindSaves() {
		FileResource fil = null;
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
		FileResource fil = BuildGdx.compat.open(filename, Path.User, Mode.Read);
		if (fil != null) {
			Tile pic = engine.getTile(SaveManager.Screenshot);
			if (pic.data == null)
				engine.allocatepermanenttile(SaveManager.Screenshot, 160, 100);

			int nVersion = checkSave(fil) & 0xFFFF;
			lsInf.clear();

			if (nVersion == currentGdxSave) {
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
						String ext = FileUtils.getExtension(fullname.substring(0, filenameIndex));
						ininame = ext + ":" + FileUtils.getFullName(fullname.substring(filenameIndex + 1));
					} else
						ininame = FileUtils.getFullName(fullname);

					if (!ininame.isEmpty())
						lsInf.addonfile = "File: " + ininame;
				}

				engine.getrender().invalidatetile(SaveManager.Screenshot, 0, -1);
				fil.close();
				return 1;
			} else
				lsInf.info = "Incompatible ver. " + nVersion + " != " + currentGdxSave;
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
		if (isPsychoSkill()) {
			FTA(53, ps[myconnectindex]);
			return -1;
		}

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
				+ (MAXSECTORS + 1) * 2 + (MAXSTATUS + 1) * 2 + MAXSPRITES * 8 + 3 * 16 + 4;

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

		bb.putInt(rorcnt);
		for (int i = 0; i < 16; i++) {
			bb.putShort(rorsector[i]);
			bb.put(rortype[i]);
		}

		fil.writeBytes(bb.array(), bb.capacity());
	}

	public static void StuffSave(FileResource fil) {
		int bufsize = 2 + MAXCYCLERS * 6 * 2 + MAXPLAYERS * PlayerStruct.sizeof + MAXPLAYERS * PlayerOrig.sizeof + 2
				+ MAXANIMWALLS * 6 + 2048 * 8 + 4 + 1024 * 2 + 2 + 64 * 4 + show2dsector.length + MAXSECTORS + 4
				+ 22 * MAXJAILDOORS + 4 + 22 * MAXMINECARDS + 4 + 5 * MAXTORCHES + 4 + 4 * MAXLIGHTNINS + 4
				+ 6 * MAXAMBIENTS + 30 * MAXGEOMETRY + 15 + 28;

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

		for (int i = 0; i < MAXSECTORS; i++)
			bb.put(shadeEffect[i] ? (byte) 1 : 0);

		bb.putInt(numjaildoors);
		for (int i = 0; i < MAXJAILDOORS; i++) {
			bb.putInt(jailspeed[i]);
			bb.putInt(jaildistance[i]);
			bb.putShort(jailsect[i]);
			bb.putShort(jaildirection[i]);
			bb.putShort(jailunique[i]);
			bb.putShort(jailsound[i]);
			bb.putShort(jailstatus[i]);
			bb.putInt(jailcount2[i]);
		}

		bb.putInt(numminecart);
		for (int i = 0; i < MAXMINECARDS; i++) {
			bb.putInt(minespeed[i]);
			bb.putInt(minefulldist[i]);
			bb.putInt(minedistance[i]);
			bb.putShort(minechild[i]);
			bb.putShort(mineparent[i]);
			bb.putShort(minedirection[i]);
			bb.putShort(minesound[i]);
			bb.putShort(minestatus[i]);
		}

		bb.putInt(numtorcheffects);
		for (int i = 0; i < MAXTORCHES; i++) {
			bb.putShort(torchsector[i]);
			bb.put(torchshade[i]);
			bb.putShort(torchflags[i]);
		}

		bb.putInt(numlightnineffects);
		for (int i = 0; i < MAXLIGHTNINS; i++) {
			bb.putShort(lightninsector[i]);
			bb.putShort(lightninshade[i]);
		}

		bb.putInt(numambients);
		for (int i = 0; i < MAXAMBIENTS; i++) {
			bb.putShort(ambienttype[i]);
			bb.putShort(ambientid[i]);
			bb.putShort(ambienthitag[i]);
		}

		bb.putInt(numgeomeffects);
		for (int i = 0; i < MAXGEOMETRY; i++) {
			bb.putShort(geomsector[i]);
			bb.putShort(geoms1[i]);
			bb.putInt(geomx1[i]);
			bb.putInt(geomy1[i]);
			bb.putInt(geomz1[i]);

			bb.putShort(geoms2[i]);
			bb.putInt(geomx2[i]);
			bb.putInt(geomy2[i]);
			bb.putInt(geomz2[i]);
		}

		bb.putShort((short) UFO_SpawnCount);
		bb.putShort((short) UFO_SpawnTime);
		bb.putShort((short) UFO_SpawnHulk);

		bb.putShort((short) 0); // gEndFirstEpisode
		bb.putShort((short) 0); // gEndGame
		bb.put((byte) (plantProcess ? 1 : 0));

		bb.putShort(BellTime);
		bb.putInt(BellSound);
		bb.putShort(word_119BE0);
		bb.putInt(WindDir);
		bb.putInt(WindTime);
		bb.putInt(mamaspawn_count);
		bb.putInt(fakebubba_spawn);
		bb.putInt(dword_119C08);

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

	public static void GameInfoSave(FileResource fil) {
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
		SaveVersion(fil, currentGdxSave);

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
		fil.writeBytes(gGameScreen.captBuffer, SAVESCREENSHOTSIZE);
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
				if (addon != null && currentGame.isPackage()) {
					String path = addon.getPath();
					path += ":" + currentGame.ConName;
					System.arraycopy(path.getBytes(), 0, name, 0, Math.min(path.length(), 144));
				} else {
					String path = currentGame.getDirectory().checkFile(currentGame.ConName).getPath();
					System.arraycopy(path.getBytes(), 0, name, 0, Math.min(path.length(), 144));
				}
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
		GameInfoSave(fil);

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
		if (numplayers > 1 || mFakeMultiplayer)
			return;
		if (isPsychoSkill()) {
			FTA(53, ps[myconnectindex]);
			return;
		}
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

	public static void AnimationLoad(SafeLoader bb) {
		for (int i = 0; i < MAXANIMATES; i++) {
			gAnimationData[i].id = bb.gAnimationData[i].id;
			gAnimationData[i].type = bb.gAnimationData[i].type;
			gAnimationData[i].ptr = bb.gAnimationData[i].ptr;
			gAnimationData[i].goal = bb.gAnimationData[i].goal;
			gAnimationData[i].vel = bb.gAnimationData[i].vel;
			gAnimationData[i].sect = bb.gAnimationData[i].sect;
		}
		gAnimationCount = bb.gAnimationCount;

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

	public static void ConLoad(SafeLoader bb) {
		System.arraycopy(bb.actortype, 0, currentGame.getCON().actortype, 0, MAXTILES);
		System.arraycopy(bb.script, 0, currentGame.getCON().script, 0, MAXSCRIPTSIZE);
		System.arraycopy(bb.actorscrptr, 0, currentGame.getCON().actorscrptr, 0, MAXTILES);

		for (int i = 0; i < MAXSPRITES; i++)
			hittype[i].copy(bb.hittype[i]);
	}

	public static void GameInfoLoad(SafeLoader bb) {
		pskybits = bb.pskybits;
		parallaxyscale = bb.parallaxyscale;
		System.arraycopy(bb.pskyoff, 0, pskyoff, 0, MAXPSKYTILES);
		System.arraycopy(pskyoff, 0, zeropskyoff, 0, MAXPSKYTILES);

		earthquaketime = bb.earthquaketime;
		ud.from_bonus = bb.from_bonus;
		ud.secretlevel = bb.secretlevel;
		ud.respawn_monsters = bb.respawn_monsters;
		ud.respawn_items = bb.respawn_items;
		ud.respawn_inventory = bb.respawn_inventory;
		ud.god = bb.god;
		ud.auto_run = bb.auto_run;
		ud.crosshair = bb.crosshair;
		ud.monsters_off = bb.monsters_off;
		ud.last_level = bb.last_level;
		ud.eog = bb.eog;
		ud.coop = bb.coop;
		ud.marker = bb.marker;
		ud.ffire = bb.ffire;
		camsprite = bb.camsprite;

		connecthead = bb.connecthead;
		System.arraycopy(bb.connectpoint2, 0, connectpoint2, 0, MAXPLAYERS);
		numplayersprites = bb.numplayersprites;

		for (int i = 0; i < MAXPLAYERS; i++)
			System.arraycopy(bb.frags[i], 0, frags[i], 0, MAXPLAYERS);

		engine.srand(bb.randomseed);
		global_random = bb.global_random;
	}

	public static void StuffLoad(SafeLoader bb) {
		numcyclers = bb.numcyclers;
		for (int i = 0; i < MAXCYCLERS; i++)
			System.arraycopy(bb.cyclers[i], 0, cyclers[i], 0, 6);
		for (int i = 0; i < MAXPLAYERS; i++)
			ps[i].copy(bb.ps[i]);
		for (int i = 0; i < MAXPLAYERS; i++)
			po[i].copy(bb.po[i]);

		numanimwalls = bb.numanimwalls;
		for (int i = 0; i < MAXANIMWALLS; i++) {
			animwall[i].wallnum = bb.animwall[i].wallnum;
			animwall[i].tag = bb.animwall[i].tag;
		}

		System.arraycopy(bb.msx, 0, msx, 0, 2048);
		System.arraycopy(bb.msy, 0, msy, 0, 2048);

		spriteqloc = bb.spriteqloc;
		currentGame.getCON().spriteqamount = bb.spriteqamount;
		System.arraycopy(bb.spriteq, 0, spriteq, 0, 1024);

		mirrorcnt = bb.mirrorcnt;
		System.arraycopy(bb.mirrorwall, 0, mirrorwall, 0, 64);
		System.arraycopy(bb.mirrorsector, 0, mirrorsector, 0, 64);
		System.arraycopy(bb.show2dsector, 0, show2dsector, 0, (MAXSECTORS + 7) >> 3);
		System.arraycopy(bb.shadeEffect, 0, shadeEffect, 0, (MAXSECTORS + 7) >> 3);

		numjaildoors = bb.numjaildoors;
		for (int i = 0; i < MAXJAILDOORS; i++) {
			jailspeed[i] = bb.jailspeed[i];
			jaildistance[i] = bb.jaildistance[i];
			jailsect[i] = bb.jailsect[i];
			jaildirection[i] = bb.jaildirection[i];
			jailunique[i] = bb.jailunique[i];
			jailsound[i] = bb.jailsound[i];
			jailstatus[i] = bb.jailstatus[i];
			jailcount2[i] = bb.jailcount2[i];
		}

		numminecart = bb.numminecart;
		for (int i = 0; i < MAXMINECARDS; i++) {
			minespeed[i] = bb.minespeed[i];
			minefulldist[i] = bb.minefulldist[i];
			minedistance[i] = bb.minedistance[i];
			minechild[i] = bb.minechild[i];
			mineparent[i] = bb.mineparent[i];
			minedirection[i] = bb.minedirection[i];
			minesound[i] = bb.minesound[i];
			minestatus[i] = bb.minestatus[i];
		}

		numtorcheffects = bb.numtorcheffects;
		for (int i = 0; i < MAXTORCHES; i++) {
			torchsector[i] = bb.torchsector[i];
			torchshade[i] = bb.torchshade[i];
			torchflags[i] = bb.torchflags[i];
		}

		numlightnineffects = bb.numlightnineffects;
		for (int i = 0; i < MAXLIGHTNINS; i++) {
			lightninsector[i] = bb.lightninsector[i];
			lightninshade[i] = bb.lightninshade[i];
		}

		numambients = bb.numambients;
		for (int i = 0; i < MAXAMBIENTS; i++) {
			ambienttype[i] = bb.ambienttype[i];
			ambientid[i] = bb.ambientid[i];
			ambienthitag[i] = bb.ambienthitag[i];
		}

		numgeomeffects = bb.numgeomeffects;
		for (int i = 0; i < MAXGEOMETRY; i++) {
			geomsector[i] = bb.geomsector[i];
			geoms1[i] = bb.geoms1[i];
			geomx1[i] = bb.geomx1[i];
			geomy1[i] = bb.geomy1[i];
			geomz1[i] = bb.geomz1[i];

			geoms2[i] = bb.geoms2[i];
			geomx2[i] = bb.geomx2[i];
			geomy2[i] = bb.geomy2[i];
			geomz2[i] = bb.geomz2[i];
		}

		UFO_SpawnCount = bb.UFO_SpawnCount;
		UFO_SpawnTime = bb.UFO_SpawnTime;
		UFO_SpawnHulk = bb.UFO_SpawnHulk;

		// gEndFirstEpisode = bb.gEndFirstEpisode;
		// gEndGame = bb.gEndGame;

		InitSpecialTextures();

		BowlReset();
		plantProcess = bb.plantProcess;

		BellTime = bb.BellTime;
		BellSound = bb.BellSound;
		word_119BE0 = bb.word_119BE0;
		WindDir = bb.WindDir;
		WindTime = bb.WindTime;
		mamaspawn_count = bb.mamaspawn_count;
		fakebubba_spawn = bb.fakebubba_spawn;
		dword_119C08 = bb.dword_119C08;
	}

	public static void MapLoad(SafeLoader bb) {
		boardfilename = bb.boardfilename;
		numwalls = bb.numwalls;
		for (int w = 0; w < numwalls; w++) {
			if (wall[w] == null)
				wall[w] = new WALL();
			wall[w].set(bb.wall[w]);
		}
		numsectors = bb.numsectors;
		for (int s = 0; s < numsectors; s++) {
			if (sector[s] == null)
				sector[s] = new SECTOR();
			sector[s].set(bb.sector[s]);
		}
		for (int i = 0; i < MAXSPRITES; i++) {
			if (sprite[i] == null)
				sprite[i] = new SPRITE();
			sprite[i].set(bb.sprite[i]);
		}

		System.arraycopy(bb.headspritesect, 0, headspritesect, 0, MAXSECTORS + 1);
		System.arraycopy(bb.headspritestat, 0, headspritestat, 0, MAXSTATUS + 1);

		System.arraycopy(bb.prevspritesect, 0, prevspritesect, 0, MAXSPRITES);
		System.arraycopy(bb.prevspritestat, 0, prevspritestat, 0, MAXSPRITES);
		System.arraycopy(bb.nextspritesect, 0, nextspritesect, 0, MAXSPRITES);
		System.arraycopy(bb.nextspritestat, 0, nextspritestat, 0, MAXSPRITES);

		rorcnt = bb.rorcnt;
		System.arraycopy(bb.rorsector, 0, rorsector, 0, 16);
		System.arraycopy(bb.rortype, 0, rortype, 0, 16);
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

	public static boolean checkfile(Resource bb) {
		int nVersion = checkSave(bb);
		if (nVersion != currentGdxSave)
			return false;

		if (!loader.load(bb))
			return false;

		return true;
	}

	public static void load() {
		ud.multimode = loader.multimode;
		ud.volume_number = loader.volume_number;
		ud.level_number = loader.level_number;
		ud.player_skill = loader.player_skill;

		LoadGDXBlock();
		MapLoad(loader);
		StuffLoad(loader);
		ConLoad(loader);
		AnimationLoad(loader);
		GameInfoLoad(loader);

		if (over_shoulder_on != 0) {
			cameradist = 0;
			cameraclock = 0;
			over_shoulder_on = 1;
		}

		screenpeek = myconnectindex;

		if (ps[myconnectindex].fogtype == 2)
			applyfog(2);
		else
			applyfog(0);

		if (ud.rec != null)
			ud.rec.close();
		ud.recstat = 0;

		if (ud.lockout == 0) {
			for (int x = 0; x < numanimwalls; x++)
				if (wall[animwall[x].wallnum].extra >= 0)
					wall[animwall[x].wallnum].picnum = wall[animwall[x].wallnum].extra;
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

				if (ps[myconnectindex].one_parallax_sectnum >= 0)
					setupbackdrop(sector[ps[myconnectindex].one_parallax_sectnum].ceilingpicnum);

				game.changeScreen(gGameScreen);
				game.pNet.ResetTimers();
				game.pNet.WaitForAllPlayers(0);
				game.pNet.ready2send = true;

				StopAllSounds();

				System.gc();
			}
		});
		game.changeScreen(gPrecacheScreen);
	}

	public static boolean loadgame(String filename) {
		if (isPsychoSkill()) {
			FTA(53, ps[myconnectindex]);
			return false;
		}

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
