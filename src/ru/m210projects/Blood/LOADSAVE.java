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

import static ru.m210projects.Blood.AI.Ai.cumulDamage;
import static ru.m210projects.Blood.AI.Ai.gDudeSlope;
import static ru.m210projects.Blood.Actor.actInit;
import static ru.m210projects.Blood.Actor.actInitStruct;
import static ru.m210projects.Blood.Actor.ceilingVel;
import static ru.m210projects.Blood.Actor.floorVel;
import static ru.m210projects.Blood.Actor.gPost;
import static ru.m210projects.Blood.Actor.gPostCount;
import static ru.m210projects.Blood.Actor.gSectorExp;
import static ru.m210projects.Blood.Actor.gSpriteHit;
import static ru.m210projects.Blood.Actor.gWallExp;
import static ru.m210projects.Blood.Actor.sprXVel;
import static ru.m210projects.Blood.Actor.sprYVel;
import static ru.m210projects.Blood.Actor.sprZVel;
import static ru.m210projects.Blood.DB.dbInit;
import static ru.m210projects.Blood.DB.gSkyCount;
import static ru.m210projects.Blood.DB.gVisibility;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.kItemBase;
import static ru.m210projects.Blood.DB.kItemDivingSuit;
import static ru.m210projects.Blood.DB.kItemReflectiveShots;
import static ru.m210projects.Blood.DB.kMaxXSectors;
import static ru.m210projects.Blood.DB.kMaxXSprites;
import static ru.m210projects.Blood.DB.kMaxXWalls;
import static ru.m210projects.Blood.DB.nStatSize;
import static ru.m210projects.Blood.DB.nextXSector;
import static ru.m210projects.Blood.DB.nextXSprite;
import static ru.m210projects.Blood.DB.nextXWall;
import static ru.m210projects.Blood.DB.xsector;
import static ru.m210projects.Blood.DB.xsprite;
import static ru.m210projects.Blood.DB.xwall;
import static ru.m210projects.Blood.EVENT.bucketHead;
import static ru.m210projects.Blood.EVENT.eventQ;
import static ru.m210projects.Blood.EVENT.gdxEventQ;
import static ru.m210projects.Blood.EVENT.getEvent;
import static ru.m210projects.Blood.EVENT.kMaxChannels;
import static ru.m210projects.Blood.EVENT.kMaxID;
import static ru.m210projects.Blood.EVENT.kPQueueSize;
import static ru.m210projects.Blood.EVENT.rxBucket;
import static ru.m210projects.Blood.Gameutils.bseed;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.LEVELS.*;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.Mirror.InitMirrorTiles;
import static ru.m210projects.Blood.Mirror.MAXMIRRORS;
import static ru.m210projects.Blood.Mirror.MirrorLower;
import static ru.m210projects.Blood.Mirror.MirrorSector;
import static ru.m210projects.Blood.Mirror.MirrorType;
import static ru.m210projects.Blood.Mirror.MirrorUpper;
import static ru.m210projects.Blood.Mirror.MirrorWall;
import static ru.m210projects.Blood.Mirror.MirrorX;
import static ru.m210projects.Blood.Mirror.MirrorY;
import static ru.m210projects.Blood.Mirror.MirrorZ;
import static ru.m210projects.Blood.Mirror.initMirrorWall;
import static ru.m210projects.Blood.Mirror.mirrorcnt;
import static ru.m210projects.Blood.PLAYER.playerSetRace;
import static ru.m210projects.Blood.PLAYER.powerupCheck;
import static ru.m210projects.Blood.SECTORFX.InitSectorFX;
import static ru.m210projects.Blood.SOUND.ambPrepare;
import static ru.m210projects.Blood.SOUND.ambStopAll;
import static ru.m210projects.Blood.SOUND.sfxKillAll3DSounds;
import static ru.m210projects.Blood.SOUND.sndPlayMusic;
import static ru.m210projects.Blood.SOUND.sndStopAllSamples;
import static ru.m210projects.Blood.Screen.scrLoadPLUs;
import static ru.m210projects.Blood.Screen.scrReset;
import static ru.m210projects.Blood.Screen.scrSetPalette;
import static ru.m210projects.Blood.Trigger.gBusy;
import static ru.m210projects.Blood.Trigger.gBusyCount;
import static ru.m210projects.Blood.Trigger.kMaxBusyArray;
import static ru.m210projects.Blood.Trigger.ksprite;
import static ru.m210projects.Blood.Trigger.kwall;
import static ru.m210projects.Blood.Trigger.secCeilZ;
import static ru.m210projects.Blood.Trigger.secFloorZ;
import static ru.m210projects.Blood.Trigger.secPath;
import static ru.m210projects.Blood.Trigger.trInitStructs;
import static ru.m210projects.Blood.Types.DEMO.IsOriginalDemo;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.activeList;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.kMaxSequences;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.seqKillAll;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.siCeiling;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.siFloor;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.siMasked;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.siSprite;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.siWall;
import static ru.m210projects.Blood.View.PaletteView;
import static ru.m210projects.Blood.View.deliriumPitch;
import static ru.m210projects.Blood.View.deliriumTilt;
import static ru.m210projects.Blood.View.deliriumTurn;
import static ru.m210projects.Blood.View.gViewIndex;
import static ru.m210projects.Blood.View.gViewMode;
import static ru.m210projects.Blood.View.gViewPos;
import static ru.m210projects.Blood.View.kView3D;
import static ru.m210projects.Blood.View.resetQuotes;
import static ru.m210projects.Blood.View.viewSetMessage;
import static ru.m210projects.Blood.Warp.gLowerLink;
import static ru.m210projects.Blood.Warp.gStartZone;
import static ru.m210projects.Blood.Warp.gUpperLink;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_BLUE;
import static ru.m210projects.Blood.Factory.BloodMenuHandler.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;

import ru.m210projects.Blood.Main.UserFlag;
import ru.m210projects.Blood.Factory.BloodNetwork;
import ru.m210projects.Blood.Menus.MenuCorruptGame;
import ru.m210projects.Blood.PriorityQueue.BPriorityQueue;
import ru.m210projects.Blood.PriorityQueue.JPriorityQueue;
import ru.m210projects.Blood.PriorityQueue.PriorityItem;
import ru.m210projects.Blood.Types.BitHandler;
import ru.m210projects.Blood.Types.BloodIniFile;
import ru.m210projects.Blood.Types.LSInfo;
import ru.m210projects.Blood.Types.SafeLoader;
import ru.m210projects.Blood.Types.XSECTOR;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Blood.Types.XWALL;
import ru.m210projects.Blood.Types.Seq.CeilingInst;
import ru.m210projects.Blood.Types.Seq.FloorInst;
import ru.m210projects.Blood.Types.Seq.MaskedWallInst;
import ru.m210projects.Blood.Types.Seq.SeqInst;
import ru.m210projects.Blood.Types.Seq.SpriteInst;
import ru.m210projects.Blood.Types.Seq.WallInst;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileResource;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.FileResource.Mode;
import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Pattern.Tools.SaveManager;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.WALL;

public class LOADSAVE {

	private static final SafeLoader loader = new SafeLoader();

	public static boolean gQuickSaving;
	public static boolean gAutosaveRequest;

	public static LSInfo lsInf = new LSInfo();
	public static int quickslot = 0;
	public static String lastload;

	public static final String savsign = "BLUD";
	public static final int gdxSave = 300;
	public static final int currentGdxSave = 302;
	public static final int SAVEHEADER = 10;
	public static final int SAVETIME = 8;
	public static final int SAVENAME = 16;
	public static final int SAVELEVELINFO = 9;
	public static final int SAVESCREENSHOTSIZE = 320 * 200;
	public static final int SAVEINFO = SAVEHEADER + SAVETIME + SAVENAME + SAVELEVELINFO;
	public static final int SAVEGDXDATA = 128;

	public static void FindSaves() {
		FileResource fil = null;
		for (FileEntry file : BuildGdx.compat.getDirectory(Path.User).getFiles().values()) {
			if (file.getExtension().equals("sav")) {
				String name = file.getFile().getName();
				fil = BuildGdx.compat.open(file);
				if (fil != null) {
					try {
						String signature = fil.readString(4);
						if (signature == null)
							continue;

						int nVersion = 0;
						if (signature.equals(savsign)) {
							nVersion = fil.readShort();
							if (nVersion >= gdxSave)
								fil.seek(SAVEHEADER, Whence.Set);
							else
								fil.seek(328, Whence.Set);
						} else
							fil.seek(318, Whence.Set);

						long time = 0;
						if (nVersion >= 301) {
							time = fil.readLong();
						} else {
							String num = name.replaceAll("[^0-9]", "");
							if (!num.isEmpty())
								time = Long.parseLong(num);
						}
						String savname = fil.readString(SAVENAME).trim();

						game.pSavemgr.add(savname, time, file.getName());

					} catch(Exception ignored) {
					} finally {
						fil.close();
					}
				}
			}
		}
		game.pSavemgr.sort();
	}

	public static final char[] filenum = new char[4];

	public static String makeNum(int num) {
		filenum[3] = (char) ((num % 10) + 48);
		filenum[2] = (char) (((num / 10) % 10) + 48);
		filenum[1] = (char) (((num / 100) % 10) + 48);
		filenum[0] = (char) (((num / 1000) % 10) + 48);

		return new String(filenum);
	}

	public static void MapSave(FileResource fil) {
		int mapversion = 7;
		fil.writeInt(mapversion);

		fil.writeInt(0);
		fil.writeInt(0);
		fil.writeInt(0);
		fil.writeShort(0);
		fil.writeShort(0);

		fil.writeShort(numsectors);
		ByteBuffer buffer = ByteBuffer.allocate(numsectors * SECTOR.sizeof);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < numsectors; i++) {
			if (sector[i] == null)
				continue;
			buffer.putShort(sector[i].wallptr);
			buffer.putShort(sector[i].wallnum);
			buffer.putInt(sector[i].ceilingz);
			buffer.putInt(sector[i].floorz);
			buffer.putShort(sector[i].ceilingstat);
			buffer.putShort(sector[i].floorstat);
			buffer.putShort(sector[i].ceilingpicnum);
			buffer.putShort(sector[i].ceilingheinum);
			buffer.put(sector[i].ceilingshade);
			buffer.put((byte) sector[i].ceilingpal);
			buffer.put((byte) sector[i].ceilingxpanning);
			buffer.put((byte) sector[i].ceilingypanning);
			buffer.putShort(sector[i].floorpicnum);
			buffer.putShort(sector[i].floorheinum);
			buffer.put(sector[i].floorshade);
			buffer.put((byte) sector[i].floorpal);
			buffer.put((byte) sector[i].floorxpanning);
			buffer.put((byte) sector[i].floorypanning);
			buffer.put((byte) sector[i].visibility);
			buffer.put((byte) sector[i].filler);
			buffer.putShort(sector[i].lotag);
			buffer.putShort(sector[i].hitag);
			buffer.putShort(sector[i].extra);
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		fil.writeShort(numwalls);
		buffer = ByteBuffer.allocate(numwalls * WALL.sizeof);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < numwalls; i++) {
			if (wall[i] == null)
				continue;
			buffer.putInt(wall[i].x);
			buffer.putInt(wall[i].y);
			buffer.putShort(wall[i].point2);
			buffer.putShort(wall[i].nextwall);
			buffer.putShort(wall[i].nextsector);
			buffer.putShort(wall[i].cstat);
			buffer.putShort(wall[i].picnum);
			buffer.putShort(wall[i].overpicnum);
			buffer.put(wall[i].shade);
			buffer.put((byte) wall[i].pal);
			buffer.put((byte) wall[i].xrepeat);
			buffer.put((byte) wall[i].yrepeat);
			buffer.put((byte) wall[i].xpanning);
			buffer.put((byte) wall[i].ypanning);
			buffer.putShort(wall[i].lotag);
			buffer.putShort(wall[i].hitag);
			buffer.putShort(wall[i].extra);
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		int pos = fil.position();
		fil.writeShort(0);
		buffer = ByteBuffer.allocate(kMaxSprites * SPRITE.sizeof);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		int spritenum = 0;
		for (int i = 0; i < kMaxSprites; i++) {
			if (sprite[i].statnum == MAXSTATUS)
				continue;
			buffer.putInt(sprite[i].x);
			buffer.putInt(sprite[i].y);
			buffer.putInt(sprite[i].z);
			buffer.putShort(sprite[i].cstat);
			buffer.putShort(sprite[i].picnum);
			buffer.put(sprite[i].shade);
			buffer.put((byte) sprite[i].pal);
			buffer.put((byte) sprite[i].clipdist);
			buffer.put((byte) sprite[i].detail);
			buffer.put((byte) sprite[i].xrepeat);
			buffer.put((byte) sprite[i].yrepeat);
			buffer.put((byte) sprite[i].xoffset);
			buffer.put((byte) sprite[i].yoffset);
			buffer.putShort(sprite[i].sectnum);
			buffer.putShort(sprite[i].statnum);
			buffer.putShort(sprite[i].ang);
			buffer.putShort(sprite[i].owner);
			buffer.putShort(sprite[i].xvel);
			buffer.putShort(sprite[i].yvel);
			buffer.putShort(sprite[i].zvel);
			buffer.putShort(sprite[i].lotag);
			buffer.putShort(sprite[i].hitag);
			buffer.putShort(sprite[i].extra);
			spritenum++;
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		fil.seek(pos, Whence.Set);
		fil.writeShort(spritenum);

		fil.close();
	}

	public static int savegame(String savename, String filename) {
		File file = BuildGdx.compat.checkFile(filename, Path.User);

		if (file != null) {
			if (!file.delete()) {
				viewSetMessage("Game not saved. Access denied!", -1, 7);
				return -1;
			}
		}

		FileResource fil = BuildGdx.compat.open(filename, Path.User, Mode.Write);
		if (fil != null) {
			long time = game.date.getCurrentDate();
			try {
				save(fil, savename, time);
				game.pSavemgr.add(savename, time, filename);
				lastload = filename;
				viewSetMessage("Game saved", -1, 10);
			} catch (Exception e) {
				viewSetMessage("Game not saved! " + e.getMessage(), -1, 7);
			} finally {
				fil.close();
			}
			return 0;
		} else
			viewSetMessage("Game not saved. Access denied!", -1, 7);

		return -1;
	}

	private static void save(FileResource fil, String savename, long time) throws Exception {
		SaveVersion(fil, currentGdxSave);
		SaveInfo(fil, savename, time);
		SaveGDXBlock(fil);

		// Original save
		MySave110(fil, savename, 0);
		DudesSave(fil);
		fil.writeByte(cheatsOn ? 1 : 0);
		WarpSave(fil);
		MirrorSave(fil);
		SeqSave(fil);
		EventSave(fil);
		TriggersSave(fil);
		PlayersSave(fil, 277);
		ActorsSave(fil);
		GameInfoSave(fil, savename, 0);
		StatsSave(fil);
		ScreenSave(fil);

		System.gc();
	}

	public static void SaveInfo(FileResource fil, String savename, long time) {
		byte[] buf = new byte[8];
		LittleEndian.putLong(buf, 0, time);
		fil.writeBytes(buf, 8);
		fil.writeBytes(savename.toCharArray(), SAVENAME);
		fil.writeByte(pGameInfo.nDifficulty);
		fil.writeInt(pGameInfo.nEpisode);
		fil.writeInt(pGameInfo.nLevel);
		SaveScreenshot(fil);
		SaveUserEpisodeInfo(fil);
	}

	public static void SaveGDXBlock(FileResource fil) {
		ByteBuffer buffer = ByteBuffer.allocate(SAVEGDXDATA);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put((byte) pGameInfo.nEnemyDamage);
		buffer.put((byte) pGameInfo.nEnemyQuantity);
		buffer.put((byte) pGameInfo.nDifficulty); // XXX
		buffer.put((byte) (pGameInfo.nPitchforkOnly ? 1 : 0));
		buffer.put((byte) (gInfiniteAmmo ? 1 : 0));

		fil.writeBytes(buffer.array(), SAVEGDXDATA);
	}

	public static void SaveUserEpisodeInfo(FileResource fil) {
		fil.writeByte(mUserFlag == UserFlag.Addon ? 1 : 0);
		if (mUserFlag == UserFlag.Addon) {
			if (currentEpisode != null && currentEpisode.filename != null) {
				String dir = currentEpisode.getDirectory().getRelativePath();
				String filename = currentEpisode.filename;
				if (dir != null && !dir.isEmpty())
					filename = dir + File.separator + filename;
				fil.writeBytes(filename.toCharArray(), 144);
			} else
				fil.writeBytes(new byte[144], 144);
		}
	}

	public static void SaveScreenshot(FileResource fil) {
		fil.writeBytes(gGameScreen.captBuffer, SAVESCREENSHOTSIZE);
		gGameScreen.captBuffer = null;
	}

	public static void SaveVersion(FileResource fil, int nVersion) {
		fil.writeBytes("BLUD".toCharArray(), 4);
		fil.writeShort(nVersion); // nVersion
		fil.writeInt(4); // nBuild 4 - plasma
	}

	public static void MySave110(FileResource fil, String pName, int nSaveGameSlot) {
		int kMaxTiles = 6144;

		fil.writeByte(pGameInfo.nGameType);
		fil.writeByte(pGameInfo.nDifficulty);
		fil.writeInt(pGameInfo.nEpisode);
		fil.writeInt(pGameInfo.nLevel);
		fil.writeBytes(pGameInfo.zLevelName.toCharArray(), 144); // e1m1.map
		if (pGameInfo.zLevelSong != null)
			fil.writeBytes(pGameInfo.zLevelSong.toCharArray(), 144);
		else
			fil.writeBytes(new byte[144], 144);
		fil.writeInt(pGameInfo.nTrackNumber);
		fil.writeBytes(pName.toCharArray(), 16); // file name
		fil.writeBytes(pName.toCharArray(), 16); // savename
		fil.writeShort(nSaveGameSlot);
		fil.writeInt(0); // picEntry 1059947
		fil.writeInt((int) pGameInfo.uMapCRC);
		fil.writeByte(pGameInfo.nMonsterSettings);
		fil.writeInt(pGameInfo.uGameFlags);
		fil.writeInt(pGameInfo.uNetGameFlags);
		fil.writeByte(pGameInfo.nWeaponSettings);
		fil.writeByte(pGameInfo.nItemSettings);
		fil.writeByte(pGameInfo.nRespawnSettings);
		fil.writeByte(pGameInfo.nTeamSettings);
		fil.writeInt(pGameInfo.nMonsterRespawnTime);
		fil.writeInt(pGameInfo.nWeaponRespawnTime);
		fil.writeInt(pGameInfo.nItemRespawnTime);
		fil.writeInt(pGameInfo.nSpecialRespawnTime);

		fil.writeShort(numsectors);
		fil.writeShort(numwalls);
		fil.writeInt(numsprites);

		ByteBuffer buffer = ByteBuffer.allocate(numsectors * SECTOR.sizeof);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < numsectors; i++) {
			if (sector[i] == null)
				continue;
			buffer.putShort(sector[i].wallptr);
			buffer.putShort(sector[i].wallnum);
			buffer.putInt(sector[i].ceilingz);
			buffer.putInt(sector[i].floorz);
			buffer.putShort(sector[i].ceilingstat);
			buffer.putShort(sector[i].floorstat);
			buffer.putShort(sector[i].ceilingpicnum);
			buffer.putShort(sector[i].ceilingheinum);
			buffer.put(sector[i].ceilingshade);
			buffer.put((byte) sector[i].ceilingpal);
			buffer.put((byte) sector[i].ceilingxpanning);
			buffer.put((byte) sector[i].ceilingypanning);
			buffer.putShort(sector[i].floorpicnum);
			buffer.putShort(sector[i].floorheinum);
			buffer.put(sector[i].floorshade);
			buffer.put((byte) sector[i].floorpal);
			buffer.put((byte) sector[i].floorxpanning);
			buffer.put((byte) sector[i].floorypanning);
			buffer.put((byte) sector[i].visibility);
			buffer.put((byte) sector[i].filler);
			buffer.putShort(sector[i].lotag);
			buffer.putShort(sector[i].hitag);
			buffer.putShort(sector[i].extra);
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(numwalls * WALL.sizeof);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < numwalls; i++) {
			if (wall[i] == null)
				continue;
			buffer.putInt(wall[i].x);
			buffer.putInt(wall[i].y);
			buffer.putShort(wall[i].point2);
			buffer.putShort(wall[i].nextwall);
			buffer.putShort(wall[i].nextsector);
			buffer.putShort(wall[i].cstat);
			buffer.putShort(wall[i].picnum);
			buffer.putShort(wall[i].overpicnum);
			buffer.put(wall[i].shade);
			buffer.put((byte) wall[i].pal);
			buffer.put((byte) wall[i].xrepeat);
			buffer.put((byte) wall[i].yrepeat);
			buffer.put((byte) wall[i].xpanning);
			buffer.put((byte) wall[i].ypanning);
			buffer.putShort(wall[i].lotag);
			buffer.putShort(wall[i].hitag);
			buffer.putShort(wall[i].extra);
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(kMaxSprites * SPRITE.sizeof);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxSprites; i++) {
			buffer.putInt(sprite[i].x);
			buffer.putInt(sprite[i].y);
			buffer.putInt(sprite[i].z);
			buffer.putShort(sprite[i].cstat);
			buffer.putShort(sprite[i].picnum);
			buffer.put(sprite[i].shade);
			buffer.put((byte) sprite[i].pal);
			buffer.put((byte) sprite[i].clipdist);
			buffer.put((byte) sprite[i].detail);
			buffer.put((byte) sprite[i].xrepeat);
			buffer.put((byte) sprite[i].yrepeat);
			buffer.put((byte) sprite[i].xoffset);
			buffer.put((byte) sprite[i].yoffset);
			buffer.putShort(sprite[i].sectnum);
			buffer.putShort(sprite[i].statnum);
			buffer.putShort(sprite[i].ang);
			buffer.putShort(sprite[i].owner);
			buffer.putShort(sprite[i].xvel);
			buffer.putShort(sprite[i].yvel);
			buffer.putShort(sprite[i].zvel);
			buffer.putShort(sprite[i].lotag);
			buffer.putShort(sprite[i].hitag);
			buffer.putShort(sprite[i].extra);
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		fil.writeInt(bseed);
		fil.writeByte(0); // parallaxtype

		fil.writeByte(showinvisibility ? 1 : 0);
		fil.writeInt(0); // parallaxyoffs

		fil.writeInt(parallaxyscale);
		fil.writeInt(visibility);
		fil.writeInt(parallaxvisibility);

		buffer = ByteBuffer.allocate(MAXPSKYTILES * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < MAXPSKYTILES; i++)
			buffer.putShort(pskyoff[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());
		fil.writeShort(pskybits);

		buffer = ByteBuffer.allocate((kMaxSectors + 1) * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i <= kMaxSectors; i++)
			buffer.putShort(headspritesect[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate((kMaxStatus + 1) * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i <= kMaxStatus; i++)
			buffer.putShort(headspritestat[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(kMaxSprites * 8);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxSprites; i++)
			buffer.putShort(prevspritesect[i]);
		for (int i = 0; i < kMaxSprites; i++)
			buffer.putShort(prevspritestat[i]);
		for (int i = 0; i < kMaxSprites; i++)
			buffer.putShort(nextspritesect[i]);
		for (int i = 0; i < kMaxSprites; i++)
			buffer.putShort(nextspritestat[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());

		fil.writeBytes(show2dsector, show2dsector.length);
		fil.writeBytes(show2dwall, (MAXWALLSV7 + 7) >> 3);
		fil.writeBytes(show2dsprite, show2dsprite.length);

		fil.writeByte(automapping);
		fil.writeBytes(gotpic, (kMaxTiles + 7) >> 3);
		fil.writeBytes(gotsector, gotsector.length);

		fil.writeInt(gFrameClock);
		fil.writeInt(gTicks);
		fil.writeInt(gFrame);
		fil.writeInt(totalclock);
		fil.writeByte(game.gPaused ? 1 : 0);
		fil.writeByte(0);

		buffer = ByteBuffer.allocate(numwalls * 8);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < numwalls; i++) {
			buffer.putInt((int) kwall[i].x);
			buffer.putInt((int) kwall[i].y);
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(numsprites * 12);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < numsprites; i++) {
			buffer.putInt((int) ksprite[i].x);
			buffer.putInt((int) ksprite[i].y);
			buffer.putInt((int) ksprite[i].z);
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(numsectors * 16);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < numsectors; i++)
			buffer.putInt(secFloorZ[i]);
		for (int i = 0; i < numsectors; i++)
			buffer.putInt(secCeilZ[i]);
		for (int i = 0; i < numsectors; i++)
			buffer.putInt((int) floorVel[i]);
		for (int i = 0; i < numsectors; i++)
			buffer.putInt((int) ceilingVel[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());

		fil.writeShort(pHitInfo.hitsect);
		fil.writeShort(pHitInfo.hitwall);
		fil.writeShort(pHitInfo.hitsprite);
		fil.writeInt(pHitInfo.hitx);
		fil.writeInt(pHitInfo.hity);
		fil.writeInt(pHitInfo.hitz);

		fil.writeByte(mUserFlag == UserFlag.UserMap ? 1 : 0); // byte_182DAA

		fil.writeByte(1); // crypted
		fil.writeByte(1); // crypted matt

		buffer = ByteBuffer.allocate(128);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		String copyright = "Copyright 1997 Monolith Productions.";
		buffer.put(copyright.getBytes());
		fil.writeBytes(buffer.array(), 128); // size of xsystem and copyright

		buffer = ByteBuffer.allocate((kMaxStatus + 1) * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i <= kMaxStatus; i++)
			buffer.putShort(nStatSize[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(kMaxXSprites * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxXSprites; i++)
			buffer.putShort((short) nextXSprite[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(kMaxXWalls * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxXWalls; i++)
			buffer.putShort((short) nextXWall[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(kMaxXSectors * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxXSectors; i++)
			buffer.putShort((short) nextXSector[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());

		byte[] data = new byte[kMaxSprites * XSPRITE.sizeof];
		int nXSprite = 0;
		for (int i = 0; i < kMaxSprites; i++) {
			if (sprite[i].statnum < kStatFree) {
				if (sprite[i].extra > 0) {
					BitHandler.bput(data, 0 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].reference, 0, 13);
					BitHandler.bput(data, 1 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].state, 6, 6);
					BitHandler.bput(data, 1 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].busy, 7, 23);
					BitHandler.bput(data, 4 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].txID, 0, 9);
					BitHandler.bput(data, 5 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].rxID, 2, 11);
					BitHandler.bput(data, 6 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].command, 4, 11);
					BitHandler.bput(data, 7 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].triggerOn ? 1 : 0, 4,
							4);
					BitHandler.bput(data, 7 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].triggerOff ? 1 : 0, 5,
							5);
					BitHandler.bput(data, 7 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].wave, 6, 7);
					BitHandler.bput(data, 8 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].busyTime, 0, 11);
					BitHandler.bput(data, 9 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].waitTime, 4, 15);
					BitHandler.bput(data, 11 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].restState, 0, 0);
					BitHandler.bput(data, 11 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].Interrutable ? 1 : 0,
							1, 1);
					BitHandler.bput(data, 11 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].respawnPending, 4,
							6);
					BitHandler.bput(data, 11 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].lT ? 1 : 0, 7, 7);
					BitHandler.bput(data, 12 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].dropMsg, 0, 7);
					BitHandler.bput(data, 13 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].Decoupled ? 1 : 0, 0,
							0);
					BitHandler.bput(data, 13 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].triggerOnce ? 1 : 0,
							1, 1);
					BitHandler.bput(data, 13 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].isTriggered ? 1 : 0,
							2, 2);
					BitHandler.bput(data, 13 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].key, 3, 5);
					BitHandler.bput(data, 13 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].Push ? 1 : 0, 6, 6);
					BitHandler.bput(data, 13 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].Vector ? 1 : 0, 7,
							7);
					BitHandler.bput(data, 14 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].Impact ? 1 : 0, 0,
							0);
					BitHandler.bput(data, 14 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].Pickup ? 1 : 0, 1,
							1);
					BitHandler.bput(data, 14 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].Touch ? 1 : 0, 2, 2);
					BitHandler.bput(data, 14 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].Sight ? 1 : 0, 3, 3);
					BitHandler.bput(data, 14 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].Proximity ? 1 : 0, 4,
							4);
					BitHandler.bput(data, 14 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].l1, 7, 7);
					BitHandler.bput(data, 15 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].l2, 0, 0);
					BitHandler.bput(data, 15 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].l3, 1, 1);
					BitHandler.bput(data, 15 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].l4, 2, 2);
					BitHandler.bput(data, 15 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].l5, 3, 3);
					BitHandler.bput(data, 15 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].lS ? 1 : 0, 4, 4);
					BitHandler.bput(data, 15 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].lB ? 1 : 0, 5, 5);
					BitHandler.bput(data, 15 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].lC ? 1 : 0, 6, 6);
					BitHandler.bput(data, 15 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].DudeLockout ? 1 : 0,
							7, 7);
					BitHandler.bput(data, 16 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].data1, 0, 15);
					BitHandler.bput(data, 18 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].data2, 0, 15);
					BitHandler.bput(data, 20 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].data3, 0, 15);
					BitHandler.bput(data, 22 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].goalAng, 0, 10);
					BitHandler.bput(data, 23 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].dodgeDir, 3, 4);
					BitHandler.bput(data, 23 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].Locked, 5, 5);
					BitHandler.bput(data, 23 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].palette, 6, 7);
					BitHandler.bput(data, 24 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].respawn, 0, 1);
					BitHandler.bput(data, 24 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].data4, 2, 17);
					BitHandler.bput(data, 26 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].moveState, 2, 7);
					BitHandler.bput(data, 27 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].lockMsg, 0, 7);
					BitHandler.bput(data, 28 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].health, 0, 11);
					BitHandler.bput(data, 29 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].dudeDeaf ? 1 : 0, 4,
							4);
					BitHandler.bput(data, 29 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].dudeAmbush ? 1 : 0,
							5, 5);
					BitHandler.bput(data, 29 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].dudeGuard ? 1 : 0, 6,
							6);
					BitHandler.bput(data, 29 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].dudeFlag4 ? 1 : 0, 7,
							7);
					BitHandler.bput(data, 30 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].target, 0, 15);
					BitHandler.bput(data, 32 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].targetX, 0, 31);
					BitHandler.bput(data, 36 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].targetY, 0, 31);
					BitHandler.bput(data, 40 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].targetZ, 0, 31);
					BitHandler.bput(data, 44 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].burnTime, 0, 15);
					BitHandler.bput(data, 46 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].burnSource, 0, 15);
					BitHandler.bput(data, 48 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].height, 0, 15);
					BitHandler.bput(data, 50 + nXSprite * XSPRITE.sizeof, xsprite[sprite[i].extra].stateTimer, 0, 15);
					BitHandler.bput(data, 52 + nXSprite * XSPRITE.sizeof, 0, 0, 31);
					nXSprite++;
				}
			}
		}
		fil.writeBytes(data, nXSprite * XSPRITE.sizeof);

		data = new byte[numwalls * XWALL.sizeof];
		int nXWall = 0;
		for (int i = 0; i < numwalls; i++) {
			if (wall[i].extra > 0) {
				BitHandler.bput(data, 0 + nXWall * XWALL.sizeof, xwall[wall[i].extra].reference, 0, 13);
				BitHandler.bput(data, 1 + nXWall * XWALL.sizeof, xwall[wall[i].extra].state, 6, 6);
				BitHandler.bput(data, 1 + nXWall * XWALL.sizeof, xwall[wall[i].extra].busy, 7, 23);
				BitHandler.bput(data, 4 + nXWall * XWALL.sizeof, xwall[wall[i].extra].data, 0, 15);
				BitHandler.bput(data, 6 + nXWall * XWALL.sizeof, xwall[wall[i].extra].txID, 0, 9);
				BitHandler.bput(data, 8 + nXWall * XWALL.sizeof, xwall[wall[i].extra].rxID, 0, 9);
				BitHandler.bput(data, 9 + nXWall * XWALL.sizeof, xwall[wall[i].extra].command, 2, 9);
				BitHandler.bput(data, 10 + nXWall * XWALL.sizeof, xwall[wall[i].extra].triggerOn ? 1 : 0, 2, 2);
				BitHandler.bput(data, 10 + nXWall * XWALL.sizeof, xwall[wall[i].extra].triggerOff ? 1 : 0, 3, 3);
				BitHandler.bput(data, 10 + nXWall * XWALL.sizeof, xwall[wall[i].extra].busyTime, 4, 15);
				BitHandler.bput(data, 12 + nXWall * XWALL.sizeof, xwall[wall[i].extra].waitTime, 0, 11);
				BitHandler.bput(data, 13 + nXWall * XWALL.sizeof, xwall[wall[i].extra].restState, 4, 4);
				BitHandler.bput(data, 13 + nXWall * XWALL.sizeof, xwall[wall[i].extra].interruptable ? 1 : 0, 5, 5);
				BitHandler.bput(data, 13 + nXWall * XWALL.sizeof, xwall[wall[i].extra].panAlways ? 1 : 0, 6, 6);
				BitHandler.bput(data, 13 + nXWall * XWALL.sizeof, xwall[wall[i].extra].panXVel, 7, 14);
				BitHandler.bput(data, 14 + nXWall * XWALL.sizeof, xwall[wall[i].extra].panYVel, 7, 14);
				BitHandler.bput(data, 15 + nXWall * XWALL.sizeof, xwall[wall[i].extra].decoupled ? 1 : 0, 7, 7);
				BitHandler.bput(data, 16 + nXWall * XWALL.sizeof, xwall[wall[i].extra].triggerOnce ? 1 : 0, 0, 0);
				BitHandler.bput(data, 16 + nXWall * XWALL.sizeof, xwall[wall[i].extra].isTriggered ? 1 : 0, 1, 1);
				BitHandler.bput(data, 16 + nXWall * XWALL.sizeof, xwall[wall[i].extra].key, 2, 4);
				BitHandler.bput(data, 16 + nXWall * XWALL.sizeof, xwall[wall[i].extra].triggerPush ? 1 : 0, 5, 5);
				BitHandler.bput(data, 16 + nXWall * XWALL.sizeof, xwall[wall[i].extra].triggerVector ? 1 : 0, 6, 6);
				BitHandler.bput(data, 16 + nXWall * XWALL.sizeof, xwall[wall[i].extra].triggerReserved ? 1 : 0, 7, 7);
				BitHandler.bput(data, 17 + nXWall * XWALL.sizeof, xwall[wall[i].extra].xpanFrac, 0, 7);
				BitHandler.bput(data, 18 + nXWall * XWALL.sizeof, xwall[wall[i].extra].ypanFrac, 0, 7);
				BitHandler.bput(data, 19 + nXWall * XWALL.sizeof, xwall[wall[i].extra].locked, 2, 2);
				BitHandler.bput(data, 19 + nXWall * XWALL.sizeof, xwall[wall[i].extra].dudeLockout ? 1 : 0, 3, 3);
				nXWall++;
			}
		}
		fil.writeBytes(data, nXWall * XWALL.sizeof);

		data = new byte[numsectors * XSECTOR.sizeof];
		int nXSector = 0;
		for (int i = 0; i < numsectors; i++) {
			if (sector[i].extra > 0) {
				BitHandler.bput(data, 0 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].reference, 0, 13);
				BitHandler.bput(data, 1 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].state, 6, 6);
				BitHandler.bput(data, 1 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].busy, 7, 23);
				BitHandler.bput(data, 4 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].data, 0, 15);
				BitHandler.bput(data, 6 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].txID, 0, 9);
				BitHandler.bput(data, 7 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].waveTime[1], 2, 3);
				BitHandler.bput(data, 7 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].waveTime[0], 5, 6);
				BitHandler.bput(data, 8 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].rxID, 0, 9);
				BitHandler.bput(data, 9 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].command, 2, 9);
				BitHandler.bput(data, 10 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].triggerOn ? 1 : 0, 2, 2);
				BitHandler.bput(data, 10 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].triggerOff ? 1 : 0, 3,
						3);
				BitHandler.bput(data, 10 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].busyTime[1], 4, 15);
				BitHandler.bput(data, 12 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].waitTime[1], 0, 11);
				// BitHandler.bput(data, 13 + nXSector * XSECTOR.sizeof,
				// xsector[sector[i].extra].restState, 4, 4);
				BitHandler.bput(data, 13 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].interruptable ? 1 : 0, 5,
						5);
				BitHandler.bput(data, 13 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].amplitude, 6, 13);
				BitHandler.bput(data, 14 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].freq, 6, 13);
				BitHandler.bput(data, 15 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].waitFlag[1] ? 1 : 0, 6,
						6);
				BitHandler.bput(data, 15 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].waitFlag[0] ? 1 : 0, 7,
						7);
				BitHandler.bput(data, 16 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].phase, 0, 7);
				BitHandler.bput(data, 17 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].wave, 0, 3);
				BitHandler.bput(data, 17 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].shadeAlways ? 1 : 0, 4,
						4);
				BitHandler.bput(data, 17 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].shadeFloor ? 1 : 0, 5,
						5);
				BitHandler.bput(data, 17 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].shadeCeiling ? 1 : 0, 6,
						6);
				BitHandler.bput(data, 17 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].shadeWalls ? 1 : 0, 7,
						7);
				BitHandler.bput(data, 18 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].shade, 0, 7);
				BitHandler.bput(data, 19 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].panAlways ? 1 : 0, 0, 0);
				BitHandler.bput(data, 19 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].panFloor ? 1 : 0, 1, 1);
				BitHandler.bput(data, 19 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].panCeiling ? 1 : 0, 2,
						2);
				BitHandler.bput(data, 19 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].Drag ? 1 : 0, 3, 3);
				BitHandler.bput(data, 19 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].Underwater ? 1 : 0, 4,
						4);
				BitHandler.bput(data, 19 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].Depth, 5, 7);
				BitHandler.bput(data, 20 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].panVel, 0, 7);
				BitHandler.bput(data, 21 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].panAngle, 0, 10);
				BitHandler.bput(data, 22 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].wind ? 1 : 0, 3, 3);
				BitHandler.bput(data, 22 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].decoupled ? 1 : 0, 4, 4);
				BitHandler.bput(data, 22 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].triggerOnce ? 1 : 0, 5,
						5);
				BitHandler.bput(data, 22 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].isTriggered ? 1 : 0, 6,
						6);
				BitHandler.bput(data, 22 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].Key, 7, 9);
				BitHandler.bput(data, 23 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].Push ? 1 : 0, 2, 2);
				BitHandler.bput(data, 23 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].Vector ? 1 : 0, 3, 3);
				BitHandler.bput(data, 23 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].Reserved ? 1 : 0, 4, 4);
				BitHandler.bput(data, 23 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].Enter ? 1 : 0, 5, 5);
				BitHandler.bput(data, 23 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].Exit ? 1 : 0, 6, 6);
				BitHandler.bput(data, 23 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].Wallpush ? 1 : 0, 7, 7);
				BitHandler.bput(data, 24 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].color ? 1 : 0, 0, 0);
				BitHandler.bput(data, 24 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].busyTime[0], 2, 9);
				BitHandler.bput(data, 25 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].waitTime[0], 6, 13);
				BitHandler.bput(data, 27 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].ceilpal, 4, 7);
				BitHandler.bput(data, 28 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].offCeilZ, 0, 31);
				BitHandler.bput(data, 32 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].onCeilZ, 0, 31);
				BitHandler.bput(data, 36 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].offFloorZ, 0, 31);
				BitHandler.bput(data, 40 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].onFloorZ, 0, 31);
				BitHandler.bput(data, 44 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].marker0, 0, 15);
				BitHandler.bput(data, 46 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].marker1, 0, 15);
				BitHandler.bput(data, 48 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].Crush ? 1 : 0, 0, 0);
				BitHandler.bput(data, 48 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].ceilxpanFrac, 1, 8);
				BitHandler.bput(data, 49 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].ceilypanFrac, 1, 8);
				BitHandler.bput(data, 50 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].floorxpanFrac, 1, 8);
				BitHandler.bput(data, 51 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].damageType, 1, 3);
				BitHandler.bput(data, 51 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].floorpal, 4, 7);
				BitHandler.bput(data, 52 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].floorypanFrac, 0, 7);
				BitHandler.bput(data, 53 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].locked, 0, 0);
				BitHandler.bput(data, 53 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].windVel, 1, 10);
				BitHandler.bput(data, 54 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].windAng, 3, 13);
				BitHandler.bput(data, 55 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].windAlways ? 1 : 0, 6,
						6);
				BitHandler.bput(data, 55 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].dudelockout ? 1 : 0, 7,
						7);
				BitHandler.bput(data, 56 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].bobTheta, 0, 10);
				BitHandler.bput(data, 57 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].bobZRange, 3, 7);
				BitHandler.bput(data, 58 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].bobSpeed, 0, 11);
				BitHandler.bput(data, 59 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].bobAlways ? 1 : 0, 4, 4);
				BitHandler.bput(data, 59 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].bobFloor ? 1 : 0, 5, 5);
				BitHandler.bput(data, 59 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].bobCeiling ? 1 : 0, 6,
						6);
				BitHandler.bput(data, 59 + nXSector * XSECTOR.sizeof, xsector[sector[i].extra].bobRotate ? 1 : 0, 7, 7);
				nXSector++;
			}
		}
		fil.writeBytes(data, nXSector * XSECTOR.sizeof);

		buffer = ByteBuffer.allocate(numsprites * 12);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < numsprites; i++)
			buffer.putInt((int) sprXVel[i]);
		for (int i = 0; i < numsprites; i++)
			buffer.putInt((int) sprYVel[i]);
		for (int i = 0; i < numsprites; i++)
			buffer.putInt((int) sprZVel[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());

		fil.writeInt(0); // dword_176518
		fil.writeInt(0); // dword_17651C

		fil.writeInt(gSkyCount);
		fil.writeByte(gFogMode ? 1 : 0);
	}

	public static void MySave100(FileResource fil, String pName, int nSaveGameSlot) {
		fil.writeByte(pGameInfo.nGameType);
		fil.writeByte(pGameInfo.nDifficulty);
		fil.writeInt(pGameInfo.nEpisode);
		fil.writeInt(pGameInfo.nLevel);
		fil.writeBytes(pGameInfo.zLevelName.toCharArray(), 144); // e1m1.map
		if (pGameInfo.zLevelSong != null)
			fil.writeBytes(pGameInfo.zLevelSong.toCharArray(), 144);
		else
			fil.writeBytes(new byte[144], 144);
		fil.writeInt(pGameInfo.nTrackNumber);
		fil.writeBytes(pName.toCharArray(), 16); // file name
		fil.writeBytes(pName.toCharArray(), 16); // savename
		fil.writeShort(nSaveGameSlot);
		fil.writeInt(0); // picEntry 1059947
		fil.writeInt((int) pGameInfo.uMapCRC);
		fil.writeByte(pGameInfo.nMonsterSettings);
		fil.writeInt(pGameInfo.uGameFlags);
		fil.writeInt(pGameInfo.uNetGameFlags);
		fil.writeByte(pGameInfo.nWeaponSettings);
		fil.writeByte(pGameInfo.nItemSettings);
		fil.writeByte(pGameInfo.nRespawnSettings);
		fil.writeByte(pGameInfo.nTeamSettings);
		fil.writeInt(pGameInfo.nMonsterRespawnTime);
		fil.writeInt(pGameInfo.nWeaponRespawnTime);
		fil.writeInt(pGameInfo.nItemRespawnTime);
		fil.writeInt(pGameInfo.nSpecialRespawnTime);

		fil.writeInt(gFrameClock);
		fil.writeInt(gTicks);
		fil.writeInt(gFrame);
		fil.writeInt(totalclock);
		fil.writeByte(game.gPaused ? 1 : 0);
		fil.writeByte(0); // unk_110D21

		ByteBuffer buffer = ByteBuffer.allocate(MAXWALLSV7 * 8);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < MAXWALLSV7; i++) {
			buffer.putInt((int) kwall[i].x);
			buffer.putInt((int) kwall[i].y);
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(kMaxSprites * 12);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxSprites; i++) {
			buffer.putInt((int) ksprite[i].x);
			buffer.putInt((int) ksprite[i].y);
			buffer.putInt((int) ksprite[i].z);
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(kMaxSectors * 16);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxSectors; i++)
			buffer.putInt(secFloorZ[i]);
		for (int i = 0; i < kMaxSectors; i++)
			buffer.putInt(secCeilZ[i]);
		for (int i = 0; i < kMaxSectors; i++)
			buffer.putInt((int) floorVel[i]);
		for (int i = 0; i < kMaxSectors; i++)
			buffer.putInt((int) ceilingVel[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());

		fil.writeShort(pHitInfo.hitsect);
		fil.writeShort(pHitInfo.hitwall);
		fil.writeShort(pHitInfo.hitsprite);
		fil.writeInt(pHitInfo.hitx);
		fil.writeInt(pHitInfo.hity);
		fil.writeInt(pHitInfo.hitz);

		fil.writeByte(mUserFlag == UserFlag.UserMap ? 1 : 0); // byte_182DAA

		buffer = ByteBuffer.allocate((kMaxStatus + 1) * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i <= kMaxStatus; i++)
			buffer.putShort(nStatSize[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());

		byte[] data = new byte[kMaxXSprites * XSPRITE.sizeof];
		for (int i = 0; i < kMaxXSprites; i++) {
			if (xsprite[i] == null)
				continue;

			BitHandler.bput(data, 0 + i * XSPRITE.sizeof, xsprite[i].reference, 0, 13);
			BitHandler.bput(data, 1 + i * XSPRITE.sizeof, xsprite[i].state, 6, 6);
			BitHandler.bput(data, 1 + i * XSPRITE.sizeof, xsprite[i].busy, 7, 23);
			BitHandler.bput(data, 4 + i * XSPRITE.sizeof, xsprite[i].txID, 0, 9);
			BitHandler.bput(data, 5 + i * XSPRITE.sizeof, xsprite[i].rxID, 2, 11);
			BitHandler.bput(data, 6 + i * XSPRITE.sizeof, xsprite[i].command, 4, 11);
			BitHandler.bput(data, 7 + i * XSPRITE.sizeof, xsprite[i].triggerOn ? 1 : 0, 4, 4);
			BitHandler.bput(data, 7 + i * XSPRITE.sizeof, xsprite[i].triggerOff ? 1 : 0, 5, 5);
			BitHandler.bput(data, 7 + i * XSPRITE.sizeof, xsprite[i].wave, 6, 7);
			BitHandler.bput(data, 8 + i * XSPRITE.sizeof, xsprite[i].busyTime, 0, 11);
			BitHandler.bput(data, 9 + i * XSPRITE.sizeof, xsprite[i].waitTime, 4, 15);
			BitHandler.bput(data, 11 + i * XSPRITE.sizeof, xsprite[i].restState, 0, 0);
			BitHandler.bput(data, 11 + i * XSPRITE.sizeof, xsprite[i].Interrutable ? 1 : 0, 1, 1);
			BitHandler.bput(data, 11 + i * XSPRITE.sizeof, xsprite[i].respawnPending, 4, 6);
			BitHandler.bput(data, 11 + i * XSPRITE.sizeof, xsprite[i].lT ? 1 : 0, 7, 7);
			BitHandler.bput(data, 12 + i * XSPRITE.sizeof, xsprite[i].dropMsg, 0, 7);
			BitHandler.bput(data, 13 + i * XSPRITE.sizeof, xsprite[i].Decoupled ? 1 : 0, 0, 0);
			BitHandler.bput(data, 13 + i * XSPRITE.sizeof, xsprite[i].triggerOnce ? 1 : 0, 1, 1);
			BitHandler.bput(data, 13 + i * XSPRITE.sizeof, xsprite[i].isTriggered ? 1 : 0, 2, 2);
			BitHandler.bput(data, 13 + i * XSPRITE.sizeof, xsprite[i].key, 3, 5);
			BitHandler.bput(data, 13 + i * XSPRITE.sizeof, xsprite[i].Push ? 1 : 0, 6, 6);
			BitHandler.bput(data, 13 + i * XSPRITE.sizeof, xsprite[i].Vector ? 1 : 0, 7, 7);
			BitHandler.bput(data, 14 + i * XSPRITE.sizeof, xsprite[i].Impact ? 1 : 0, 0, 0);
			BitHandler.bput(data, 14 + i * XSPRITE.sizeof, xsprite[i].Pickup ? 1 : 0, 1, 1);
			BitHandler.bput(data, 14 + i * XSPRITE.sizeof, xsprite[i].Touch ? 1 : 0, 2, 2);
			BitHandler.bput(data, 14 + i * XSPRITE.sizeof, xsprite[i].Sight ? 1 : 0, 3, 3);
			BitHandler.bput(data, 14 + i * XSPRITE.sizeof, xsprite[i].Proximity ? 1 : 0, 4, 4);
			BitHandler.bput(data, 14 + i * XSPRITE.sizeof, xsprite[i].l1, 7, 7);
			BitHandler.bput(data, 15 + i * XSPRITE.sizeof, xsprite[i].l2, 0, 0);
			BitHandler.bput(data, 15 + i * XSPRITE.sizeof, xsprite[i].l3, 1, 1);
			BitHandler.bput(data, 15 + i * XSPRITE.sizeof, xsprite[i].l4, 2, 2);
			BitHandler.bput(data, 15 + i * XSPRITE.sizeof, xsprite[i].l5, 3, 3);
			BitHandler.bput(data, 15 + i * XSPRITE.sizeof, xsprite[i].lS ? 1 : 0, 4, 4);
			BitHandler.bput(data, 15 + i * XSPRITE.sizeof, xsprite[i].lB ? 1 : 0, 5, 5);
			BitHandler.bput(data, 15 + i * XSPRITE.sizeof, xsprite[i].lC ? 1 : 0, 6, 6);
			BitHandler.bput(data, 15 + i * XSPRITE.sizeof, xsprite[i].DudeLockout ? 1 : 0, 7, 7);
			BitHandler.bput(data, 16 + i * XSPRITE.sizeof, xsprite[i].data1, 0, 15);
			BitHandler.bput(data, 18 + i * XSPRITE.sizeof, xsprite[i].data2, 0, 15);
			BitHandler.bput(data, 20 + i * XSPRITE.sizeof, xsprite[i].data3, 0, 15);
			BitHandler.bput(data, 22 + i * XSPRITE.sizeof, xsprite[i].goalAng, 0, 10);
			BitHandler.bput(data, 23 + i * XSPRITE.sizeof, xsprite[i].dodgeDir, 3, 4);
			BitHandler.bput(data, 23 + i * XSPRITE.sizeof, xsprite[i].Locked, 5, 5);
			BitHandler.bput(data, 23 + i * XSPRITE.sizeof, xsprite[i].palette, 6, 7);
			BitHandler.bput(data, 24 + i * XSPRITE.sizeof, xsprite[i].respawn, 0, 1);
			BitHandler.bput(data, 24 + i * XSPRITE.sizeof, xsprite[i].data4, 2, 17);
			BitHandler.bput(data, 26 + i * XSPRITE.sizeof, xsprite[i].moveState, 2, 7);
			BitHandler.bput(data, 27 + i * XSPRITE.sizeof, xsprite[i].lockMsg, 0, 7);
			BitHandler.bput(data, 28 + i * XSPRITE.sizeof, xsprite[i].health, 0, 11);
			BitHandler.bput(data, 29 + i * XSPRITE.sizeof, xsprite[i].dudeDeaf ? 1 : 0, 4, 4);
			BitHandler.bput(data, 29 + i * XSPRITE.sizeof, xsprite[i].dudeAmbush ? 1 : 0, 5, 5);
			BitHandler.bput(data, 29 + i * XSPRITE.sizeof, xsprite[i].dudeGuard ? 1 : 0, 6, 6);
			BitHandler.bput(data, 29 + i * XSPRITE.sizeof, xsprite[i].dudeFlag4 ? 1 : 0, 7, 7);
			BitHandler.bput(data, 30 + i * XSPRITE.sizeof, xsprite[i].target, 0, 15);
			BitHandler.bput(data, 32 + i * XSPRITE.sizeof, xsprite[i].targetX, 0, 31);
			BitHandler.bput(data, 36 + i * XSPRITE.sizeof, xsprite[i].targetY, 0, 31);
			BitHandler.bput(data, 40 + i * XSPRITE.sizeof, xsprite[i].targetZ, 0, 31);
			BitHandler.bput(data, 44 + i * XSPRITE.sizeof, xsprite[i].burnTime, 0, 15);
			BitHandler.bput(data, 46 + i * XSPRITE.sizeof, xsprite[i].burnSource, 0, 15);
			BitHandler.bput(data, 48 + i * XSPRITE.sizeof, xsprite[i].height, 0, 15);
			BitHandler.bput(data, 50 + i * XSPRITE.sizeof, xsprite[i].stateTimer, 0, 15);
			BitHandler.bput(data, 52 + i * XSPRITE.sizeof, 0, 0, 31);
		}
		fil.writeBytes(data, data.length);

		data = new byte[kMaxXWalls * XWALL.sizeof];
		for (int i = 0; i < kMaxXWalls; i++) {
			if (xwall[i] == null)
				continue;

			BitHandler.bput(data, 0 + i * XWALL.sizeof, xwall[i].reference, 0, 13);
			BitHandler.bput(data, 1 + i * XWALL.sizeof, xwall[i].state, 6, 6);
			BitHandler.bput(data, 1 + i * XWALL.sizeof, xwall[i].busy, 7, 23);
			BitHandler.bput(data, 4 + i * XWALL.sizeof, xwall[i].data, 0, 15);
			BitHandler.bput(data, 6 + i * XWALL.sizeof, xwall[i].txID, 0, 9);
			BitHandler.bput(data, 8 + i * XWALL.sizeof, xwall[i].rxID, 0, 9);
			BitHandler.bput(data, 9 + i * XWALL.sizeof, xwall[i].command, 2, 9);
			BitHandler.bput(data, 10 + i * XWALL.sizeof, xwall[i].triggerOn ? 1 : 0, 2, 2);
			BitHandler.bput(data, 10 + i * XWALL.sizeof, xwall[i].triggerOff ? 1 : 0, 3, 3);
			BitHandler.bput(data, 10 + i * XWALL.sizeof, xwall[i].busyTime, 4, 15);
			BitHandler.bput(data, 12 + i * XWALL.sizeof, xwall[i].waitTime, 0, 11);
			BitHandler.bput(data, 13 + i * XWALL.sizeof, xwall[i].restState, 4, 4);
			BitHandler.bput(data, 13 + i * XWALL.sizeof, xwall[i].interruptable ? 1 : 0, 5, 5);
			BitHandler.bput(data, 13 + i * XWALL.sizeof, xwall[i].panAlways ? 1 : 0, 6, 6);
			BitHandler.bput(data, 13 + i * XWALL.sizeof, xwall[i].panXVel, 7, 14);
			BitHandler.bput(data, 14 + i * XWALL.sizeof, xwall[i].panYVel, 7, 14);
			BitHandler.bput(data, 15 + i * XWALL.sizeof, xwall[i].decoupled ? 1 : 0, 7, 7);
			BitHandler.bput(data, 16 + i * XWALL.sizeof, xwall[i].triggerOnce ? 1 : 0, 0, 0);
			BitHandler.bput(data, 16 + i * XWALL.sizeof, xwall[i].isTriggered ? 1 : 0, 1, 1);
			BitHandler.bput(data, 16 + i * XWALL.sizeof, xwall[i].key, 2, 4);
			BitHandler.bput(data, 16 + i * XWALL.sizeof, xwall[i].triggerPush ? 1 : 0, 5, 5);
			BitHandler.bput(data, 16 + i * XWALL.sizeof, xwall[i].triggerVector ? 1 : 0, 6, 6);
			BitHandler.bput(data, 16 + i * XWALL.sizeof, xwall[i].triggerReserved ? 1 : 0, 7, 7);
			BitHandler.bput(data, 17 + i * XWALL.sizeof, xwall[i].xpanFrac, 0, 7);
			BitHandler.bput(data, 18 + i * XWALL.sizeof, xwall[i].ypanFrac, 0, 7);
			BitHandler.bput(data, 19 + i * XWALL.sizeof, xwall[i].locked, 2, 2);
			BitHandler.bput(data, 19 + i * XWALL.sizeof, xwall[i].dudeLockout ? 1 : 0, 3, 3);
		}
		fil.writeBytes(data, data.length);

		data = new byte[kMaxXSectors * XSECTOR.sizeof];
		for (int i = 0; i < kMaxXSectors; i++) {
			if (xsector[i] == null)
				continue;

			BitHandler.bput(data, 0 + i * XSECTOR.sizeof, xsector[i].reference, 0, 13);
			BitHandler.bput(data, 1 + i * XSECTOR.sizeof, xsector[i].state, 6, 6);
			BitHandler.bput(data, 1 + i * XSECTOR.sizeof, xsector[i].busy, 7, 23);
			BitHandler.bput(data, 4 + i * XSECTOR.sizeof, xsector[i].data, 0, 15);
			BitHandler.bput(data, 6 + i * XSECTOR.sizeof, xsector[i].txID, 0, 9);
			BitHandler.bput(data, 7 + i * XSECTOR.sizeof, xsector[i].waveTime[1], 2, 3);
			BitHandler.bput(data, 7 + i * XSECTOR.sizeof, xsector[i].waveTime[0], 5, 6);
			BitHandler.bput(data, 8 + i * XSECTOR.sizeof, xsector[i].rxID, 0, 9);
			BitHandler.bput(data, 9 + i * XSECTOR.sizeof, xsector[i].command, 2, 9);
			BitHandler.bput(data, 10 + i * XSECTOR.sizeof, xsector[i].triggerOn ? 1 : 0, 2, 2);
			BitHandler.bput(data, 10 + i * XSECTOR.sizeof, xsector[i].triggerOff ? 1 : 0, 3, 3);
			BitHandler.bput(data, 10 + i * XSECTOR.sizeof, xsector[i].busyTime[1], 4, 15);
			BitHandler.bput(data, 12 + i * XSECTOR.sizeof, xsector[i].waitTime[1], 0, 11);
			// BitHandler.bput(data, 13 + i * XSECTOR.sizeof, xsector[i].restState, 4, 4);
			BitHandler.bput(data, 13 + i * XSECTOR.sizeof, xsector[i].interruptable ? 1 : 0, 5, 5);
			BitHandler.bput(data, 13 + i * XSECTOR.sizeof, xsector[i].amplitude, 6, 13);
			BitHandler.bput(data, 14 + i * XSECTOR.sizeof, xsector[i].freq, 6, 13);
			BitHandler.bput(data, 15 + i * XSECTOR.sizeof, xsector[i].waitFlag[1] ? 1 : 0, 6, 6);
			BitHandler.bput(data, 15 + i * XSECTOR.sizeof, xsector[i].waitFlag[0] ? 1 : 0, 7, 7);
			BitHandler.bput(data, 16 + i * XSECTOR.sizeof, xsector[i].phase, 0, 7);
			BitHandler.bput(data, 17 + i * XSECTOR.sizeof, xsector[i].wave, 0, 3);
			BitHandler.bput(data, 17 + i * XSECTOR.sizeof, xsector[i].shadeAlways ? 1 : 0, 4, 4);
			BitHandler.bput(data, 17 + i * XSECTOR.sizeof, xsector[i].shadeFloor ? 1 : 0, 5, 5);
			BitHandler.bput(data, 17 + i * XSECTOR.sizeof, xsector[i].shadeCeiling ? 1 : 0, 6, 6);
			BitHandler.bput(data, 17 + i * XSECTOR.sizeof, xsector[i].shadeWalls ? 1 : 0, 7, 7);
			BitHandler.bput(data, 18 + i * XSECTOR.sizeof, xsector[i].shade, 0, 7);
			BitHandler.bput(data, 19 + i * XSECTOR.sizeof, xsector[i].panAlways ? 1 : 0, 0, 0);
			BitHandler.bput(data, 19 + i * XSECTOR.sizeof, xsector[i].panFloor ? 1 : 0, 1, 1);
			BitHandler.bput(data, 19 + i * XSECTOR.sizeof, xsector[i].panCeiling ? 1 : 0, 2, 2);
			BitHandler.bput(data, 19 + i * XSECTOR.sizeof, xsector[i].Drag ? 1 : 0, 3, 3);
			BitHandler.bput(data, 19 + i * XSECTOR.sizeof, xsector[i].Underwater ? 1 : 0, 4, 4);
			BitHandler.bput(data, 19 + i * XSECTOR.sizeof, xsector[i].Depth, 5, 7);
			BitHandler.bput(data, 20 + i * XSECTOR.sizeof, xsector[i].panVel, 0, 7);
			BitHandler.bput(data, 21 + i * XSECTOR.sizeof, xsector[i].panAngle, 0, 10);
			BitHandler.bput(data, 22 + i * XSECTOR.sizeof, xsector[i].wind ? 1 : 0, 3, 3);
			BitHandler.bput(data, 22 + i * XSECTOR.sizeof, xsector[i].decoupled ? 1 : 0, 4, 4);
			BitHandler.bput(data, 22 + i * XSECTOR.sizeof, xsector[i].triggerOnce ? 1 : 0, 5, 5);
			BitHandler.bput(data, 22 + i * XSECTOR.sizeof, xsector[i].isTriggered ? 1 : 0, 6, 6);
			BitHandler.bput(data, 22 + i * XSECTOR.sizeof, xsector[i].Key, 7, 9);
			BitHandler.bput(data, 23 + i * XSECTOR.sizeof, xsector[i].Push ? 1 : 0, 2, 2);
			BitHandler.bput(data, 23 + i * XSECTOR.sizeof, xsector[i].Vector ? 1 : 0, 3, 3);
			BitHandler.bput(data, 23 + i * XSECTOR.sizeof, xsector[i].Reserved ? 1 : 0, 4, 4);
			BitHandler.bput(data, 23 + i * XSECTOR.sizeof, xsector[i].Enter ? 1 : 0, 5, 5);
			BitHandler.bput(data, 23 + i * XSECTOR.sizeof, xsector[i].Exit ? 1 : 0, 6, 6);
			BitHandler.bput(data, 23 + i * XSECTOR.sizeof, xsector[i].Wallpush ? 1 : 0, 7, 7);
			BitHandler.bput(data, 24 + i * XSECTOR.sizeof, xsector[i].color ? 1 : 0, 0, 0);
			BitHandler.bput(data, 24 + i * XSECTOR.sizeof, xsector[i].busyTime[0], 2, 9);
			BitHandler.bput(data, 25 + i * XSECTOR.sizeof, xsector[i].waitTime[0], 6, 13);
			BitHandler.bput(data, 27 + i * XSECTOR.sizeof, xsector[i].ceilpal, 4, 7);
			BitHandler.bput(data, 28 + i * XSECTOR.sizeof, xsector[i].offCeilZ, 0, 31);
			BitHandler.bput(data, 32 + i * XSECTOR.sizeof, xsector[i].onCeilZ, 0, 31);
			BitHandler.bput(data, 36 + i * XSECTOR.sizeof, xsector[i].offFloorZ, 0, 31);
			BitHandler.bput(data, 40 + i * XSECTOR.sizeof, xsector[i].onFloorZ, 0, 31);
			BitHandler.bput(data, 44 + i * XSECTOR.sizeof, xsector[i].marker0, 0, 15);
			BitHandler.bput(data, 46 + i * XSECTOR.sizeof, xsector[i].marker1, 0, 15);
			BitHandler.bput(data, 48 + i * XSECTOR.sizeof, xsector[i].Crush ? 1 : 0, 0, 0);
			BitHandler.bput(data, 48 + i * XSECTOR.sizeof, xsector[i].ceilxpanFrac, 1, 8);
			BitHandler.bput(data, 49 + i * XSECTOR.sizeof, xsector[i].ceilypanFrac, 1, 8);
			BitHandler.bput(data, 50 + i * XSECTOR.sizeof, xsector[i].floorxpanFrac, 1, 8);
			BitHandler.bput(data, 51 + i * XSECTOR.sizeof, xsector[i].damageType, 1, 3);
			BitHandler.bput(data, 51 + i * XSECTOR.sizeof, xsector[i].floorpal, 4, 7);
			BitHandler.bput(data, 52 + i * XSECTOR.sizeof, xsector[i].floorypanFrac, 0, 7);
			BitHandler.bput(data, 53 + i * XSECTOR.sizeof, xsector[i].locked, 0, 0);
			BitHandler.bput(data, 53 + i * XSECTOR.sizeof, xsector[i].windVel, 1, 10);
			BitHandler.bput(data, 54 + i * XSECTOR.sizeof, xsector[i].windAng, 3, 13);
			BitHandler.bput(data, 55 + i * XSECTOR.sizeof, xsector[i].windAlways ? 1 : 0, 6, 6);
			BitHandler.bput(data, 55 + i * XSECTOR.sizeof, xsector[i].dudelockout ? 1 : 0, 7, 7);
			BitHandler.bput(data, 56 + i * XSECTOR.sizeof, xsector[i].bobTheta, 0, 10);
			BitHandler.bput(data, 57 + i * XSECTOR.sizeof, xsector[i].bobZRange, 3, 7);
			BitHandler.bput(data, 58 + i * XSECTOR.sizeof, xsector[i].bobSpeed, 0, 11);
			BitHandler.bput(data, 59 + i * XSECTOR.sizeof, xsector[i].bobAlways ? 1 : 0, 4, 4);
			BitHandler.bput(data, 59 + i * XSECTOR.sizeof, xsector[i].bobFloor ? 1 : 0, 5, 5);
			BitHandler.bput(data, 59 + i * XSECTOR.sizeof, xsector[i].bobCeiling ? 1 : 0, 6, 6);
			BitHandler.bput(data, 59 + i * XSECTOR.sizeof, xsector[i].bobRotate ? 1 : 0, 7, 7);
		}
		fil.writeBytes(data, data.length);

		buffer = ByteBuffer.allocate(kMaxSprites * 12);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxSprites; i++)
			buffer.putInt((int) sprXVel[i]);
		for (int i = 0; i < kMaxSprites; i++)
			buffer.putInt((int) sprYVel[i]);
		for (int i = 0; i < kMaxSprites; i++)
			buffer.putInt((int) sprZVel[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(kMaxXSprites * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxXSprites; i++)
			buffer.putShort((short) nextXSprite[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(kMaxXWalls * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxXWalls; i++)
			buffer.putShort((short) nextXWall[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(kMaxXSectors * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxXSectors; i++)
			buffer.putShort((short) nextXSector[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());

		fil.writeInt(0); // dword_176518
		fil.writeInt(0); // dword_17651C

		fil.writeInt(gSkyCount);
		fil.writeByte(gFogMode ? 1 : 0);

		buffer = ByteBuffer.allocate(kMaxSectors * SECTOR.sizeof);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxSectors; i++) {
			if (sector[i] == null)
				continue;
			buffer.putShort(sector[i].wallptr);
			buffer.putShort(sector[i].wallnum);
			buffer.putInt(sector[i].ceilingz);
			buffer.putInt(sector[i].floorz);
			buffer.putShort(sector[i].ceilingstat);
			buffer.putShort(sector[i].floorstat);
			buffer.putShort(sector[i].ceilingpicnum);
			buffer.putShort(sector[i].ceilingheinum);
			buffer.put(sector[i].ceilingshade);
			buffer.put((byte) sector[i].ceilingpal);
			buffer.put((byte) sector[i].ceilingxpanning);
			buffer.put((byte) sector[i].ceilingypanning);
			buffer.putShort(sector[i].floorpicnum);
			buffer.putShort(sector[i].floorheinum);
			buffer.put(sector[i].floorshade);
			buffer.put((byte) sector[i].floorpal);
			buffer.put((byte) sector[i].floorxpanning);
			buffer.put((byte) sector[i].floorypanning);
			buffer.put((byte) sector[i].visibility);
			buffer.put((byte) sector[i].filler);
			buffer.putShort(sector[i].lotag);
			buffer.putShort(sector[i].hitag);
			buffer.putShort(sector[i].extra);
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(MAXWALLSV7 * WALL.sizeof);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < MAXWALLSV7; i++) {
			if (wall[i] == null)
				continue;
			buffer.putInt(wall[i].x);
			buffer.putInt(wall[i].y);
			buffer.putShort(wall[i].point2);
			buffer.putShort(wall[i].nextwall);
			buffer.putShort(wall[i].nextsector);
			buffer.putShort(wall[i].cstat);
			buffer.putShort(wall[i].picnum);
			buffer.putShort(wall[i].overpicnum);
			buffer.put(wall[i].shade);
			buffer.put((byte) wall[i].pal);
			buffer.put((byte) wall[i].xrepeat);
			buffer.put((byte) wall[i].yrepeat);
			buffer.put((byte) wall[i].xpanning);
			buffer.put((byte) wall[i].ypanning);
			buffer.putShort(wall[i].lotag);
			buffer.putShort(wall[i].hitag);
			buffer.putShort(wall[i].extra);
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(kMaxSprites * SPRITE.sizeof);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxSprites; i++) {
			buffer.putInt(sprite[i].x);
			buffer.putInt(sprite[i].y);
			buffer.putInt(sprite[i].z);
			buffer.putShort(sprite[i].cstat);
			buffer.putShort(sprite[i].picnum);
			buffer.put(sprite[i].shade);
			buffer.put((byte) sprite[i].pal);
			buffer.put((byte) sprite[i].clipdist);
			buffer.put((byte) sprite[i].detail);
			buffer.put((byte) sprite[i].xrepeat);
			buffer.put((byte) sprite[i].yrepeat);
			buffer.put((byte) sprite[i].xoffset);
			buffer.put((byte) sprite[i].yoffset);
			buffer.putShort(sprite[i].sectnum);
			buffer.putShort(sprite[i].statnum);
			buffer.putShort(sprite[i].ang);
			buffer.putShort(sprite[i].owner);
			buffer.putShort(sprite[i].xvel);
			buffer.putShort(sprite[i].yvel);
			buffer.putShort(sprite[i].zvel);
			buffer.putShort(sprite[i].lotag);
			buffer.putShort(sprite[i].hitag);
			buffer.putShort(sprite[i].extra);
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		fil.writeShort(numsectors);
		fil.writeShort(numwalls);
		fil.writeInt(bseed);
		fil.writeByte(0); // parallaxtype

		fil.writeByte(showinvisibility ? 1 : 0);
		fil.writeInt(0); // parallaxyoffs

		fil.writeInt(parallaxyscale);
		fil.writeInt(visibility);
		fil.writeInt(parallaxvisibility);

		buffer = ByteBuffer.allocate(MAXPSKYTILES * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < MAXPSKYTILES; i++)
			buffer.putShort(pskyoff[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());
		fil.writeShort(pskybits);

		buffer = ByteBuffer.allocate((kMaxSectors + 1) * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i <= kMaxSectors; i++)
			buffer.putShort(headspritesect[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate((kMaxStatus + 1) * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i <= kMaxStatus; i++)
			buffer.putShort(headspritestat[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(kMaxSprites * 8);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxSprites; i++)
			buffer.putShort(prevspritesect[i]);
		for (int i = 0; i < kMaxSprites; i++)
			buffer.putShort(prevspritestat[i]);
		for (int i = 0; i < kMaxSprites; i++)
			buffer.putShort(nextspritesect[i]);
		for (int i = 0; i < kMaxSprites; i++)
			buffer.putShort(nextspritestat[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());

		fil.writeBytes(show2dsector, show2dsector.length);
		fil.writeBytes(show2dwall, (MAXWALLSV7 + 7) >> 3);
		fil.writeBytes(show2dsprite, show2dsprite.length);

		fil.writeByte(automapping);
		fil.writeBytes(gotpic, (4096 + 7) >> 3);
		fil.writeBytes(gotsector, gotsector.length);
	}

	public static void DudesSave(FileResource fil) {
		ByteBuffer buffer = ByteBuffer.allocate(kMaxXSprites * 8);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxXSprites; i++)
			buffer.putInt(cumulDamage[i]);
		for (int i = 0; i < kMaxXSprites; i++)
			buffer.putInt(gDudeSlope[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());
	}

	public static void WarpSave(FileResource fil) {
		ByteBuffer buffer = ByteBuffer.allocate(kMaxPlayers * 16);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxPlayers; i++) {
			buffer.putInt((int) gStartZone[i].x);
			buffer.putInt((int) gStartZone[i].y);
			buffer.putInt((int) gStartZone[i].z);
			buffer.putShort(gStartZone[i].sector);
			buffer.putShort(gStartZone[i].angle);
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(kMaxSectors * 4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxSectors; i++)
			buffer.putShort((short) gUpperLink[i]);
		for (int i = 0; i < kMaxSectors; i++)
			buffer.putShort((short) gLowerLink[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());
	}

	public static void MirrorSave(FileResource fil) {
		fil.writeInt(mirrorcnt);
		fil.writeInt(MirrorSector);

		ByteBuffer buffer = ByteBuffer.allocate(MAXMIRRORS * 24);
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		for (int i = 0; i < MAXMIRRORS; i++) {
			buffer.putShort((short) MirrorType[i]);
			buffer.putShort((short) 0);
			buffer.putInt(MirrorLower[i]);
			buffer.putInt(MirrorX[i]);
			buffer.putInt(MirrorY[i]);
			buffer.putInt(MirrorZ[i]);
			buffer.putInt(MirrorUpper[i]);
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(16);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < 4; i++)
			buffer.putInt(MirrorWall[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());
	}

	public static void SeqSave(FileResource fil) {
		ByteBuffer buffer = ByteBuffer.allocate(2 * kMaxXWalls * SeqInst.sizeof);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxXWalls; i++) {
			buffer.put(siWall[i].getBytes());
		}
		for (int i = 0; i < kMaxXWalls; i++) {
			buffer.put(siMasked[i].getBytes());
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(2 * kMaxXSectors * SeqInst.sizeof);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxXSectors; i++) {
			buffer.put(siCeiling[i].getBytes());
		}
		for (int i = 0; i < kMaxXSectors; i++) {
			buffer.put(siFloor[i].getBytes());
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(kMaxXSprites * SeqInst.sizeof);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxXSprites; i++) {
			buffer.put(siSprite[i].getBytes());
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(kMaxSequences * 3);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxSequences; i++) {
			SeqInst pInst = activeList.getInst(i);
			if (pInst instanceof WallInst)
				buffer.put(SS_WALL);
			else if (pInst instanceof MaskedWallInst)
				buffer.put(SS_MASKED);
			else if (pInst instanceof FloorInst)
				buffer.put(SS_FLOOR);
			else if (pInst instanceof CeilingInst)
				buffer.put(SS_CEILING);
			else if (pInst instanceof SpriteInst)
				buffer.put(SS_SPRITE);
			else
				buffer.put((byte) -1);
			buffer.putShort(activeList.getIndex(i));
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		fil.writeInt(activeList.getSize());
	}

	public static void EventSave(FileResource fil) {
		ByteBuffer buffer = ByteBuffer.allocate(1024 * 8);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(0); // priority[0]
		buffer.putInt(0); // event[0]
		for (int i = 0; i < 1023; i++) {
			if (i < eventQ.getSize()) {
				PriorityItem item = eventQ.getItem(i);
				buffer.putInt((int) item.priority); // priority[0]
				buffer.putInt(item.event); // event[0]
			} else {
				buffer.putInt(0); // priority
				buffer.putInt(0); // event
			}
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		fil.writeInt(0);
		fil.writeInt(0);
		fil.writeInt(eventQ.getSize());
		buffer = ByteBuffer.allocate(kMaxChannels * 4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxChannels; i++)
			buffer.putInt(getEvent(rxBucket[i].index, rxBucket[i].type, 0, 0));
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate((kMaxID + 1) * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i <= kMaxID; i++)
			buffer.putShort(bucketHead[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());
	}

	public static void TriggersSave(FileResource fil) {
		fil.writeInt(gBusyCount);

		ByteBuffer buffer = ByteBuffer.allocate(kMaxBusyArray * 13);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxBusyArray; i++) {

			buffer.putInt(gBusy[i].nIndex);
			buffer.putInt(gBusy[i].nDelta);
			buffer.putInt(gBusy[i].nBusy);
			buffer.put((byte) gBusy[i].busyProc);
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(kMaxSectors * 4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxSectors; i++)
			buffer.putInt(secPath[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());
	}

	public static void PlayersSave(FileResource fil, int nVersion) {
		for (int i = 0; i < kMaxPlayers; i++)
			fil.writeInt(nTeamCount[i]);
		fil.writeInt(numplayers);

		for (int i = 0; i < kMaxPlayers; i++) {
			fil.writeByte(game.net.gProfile[i].autoaim ? 1 : 0);
			if (nVersion >= gdxSave + 2)
				fil.writeByte(game.net.gProfile[i].slopetilt ? 1 : 0);
			fil.writeByte(game.net.gProfile[i].skill);
			fil.writeBytes(game.net.gProfile[i].name.toCharArray(), 15);
		}

		for (int i = 0; i < kMaxPlayers; i++) {
			fil.writeInt(0); // sprite
			fil.writeInt(0); // xsprite
			fil.writeInt(0); // dudeinfo

			byte[] input = gPlayer[i].pInput.getBytes(nVersion);
			fil.writeBytes(input, input.length);
			byte[] data = gPlayer[i].getBytes(nVersion);
			fil.writeBytes(data, data.length);
		}
	}

	public static void ActorsSave(FileResource fil) {
		ByteBuffer buffer = ByteBuffer.allocate(kMaxXSprites * 12);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxXSprites; i++) {
			buffer.putInt(gSpriteHit[i].moveHit);
			buffer.putInt(gSpriteHit[i].ceilHit);
			buffer.putInt(gSpriteHit[i].floorHit);
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(kMaxSectors * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxSectors; i++) {
			buffer.putShort((short) gSectorExp[i]);
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		buffer = ByteBuffer.allocate(kMaxXWalls * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxXWalls; i++) {
			buffer.putShort((short) gWallExp[i]);
		}
		fil.writeBytes(buffer.array(), buffer.capacity());

		fil.writeInt(gPostCount);
		buffer = ByteBuffer.allocate(kMaxSprites * 4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < kMaxSprites; i++) {
			buffer.putShort(gPost[i].nSprite);
			buffer.putShort(gPost[i].nStatus);
		}
		fil.writeBytes(buffer.array(), buffer.capacity());
	}

	public static void UnkSave(FileResource fil) {
		fil.writeShort(0);
	}

	public static void GameInfoSave(FileResource fil, String pName, int nSaveGameSlot) {
		fil.writeInt(gNextMap);
		fil.writeShort(0); // unk_111330

		fil.writeByte(pGameInfo.nGameType);
		fil.writeByte(pGameInfo.nDifficulty);
		fil.writeInt(pGameInfo.nEpisode);
		fil.writeInt(pGameInfo.nLevel);
		fil.writeBytes(pGameInfo.zLevelName.toCharArray(), 144);
		if (pGameInfo.zLevelSong != null)
			fil.writeBytes(pGameInfo.zLevelSong.toCharArray(), 144);
		else
			fil.writeBytes(new byte[144], 144);
		fil.writeInt(pGameInfo.nTrackNumber);
		fil.writeBytes(pName.toCharArray(), 16);
		fil.writeBytes(pName.toCharArray(), 16);
		fil.writeShort(nSaveGameSlot);
		fil.writeInt(0);

		fil.writeInt((int) pGameInfo.uMapCRC);
		fil.writeByte(pGameInfo.nMonsterSettings);
		fil.writeInt(pGameInfo.uGameFlags);
		fil.writeInt(pGameInfo.uNetGameFlags);
		fil.writeByte(pGameInfo.nWeaponSettings);
		fil.writeByte(pGameInfo.nItemSettings);
		fil.writeByte(pGameInfo.nRespawnSettings);
		fil.writeByte(pGameInfo.nTeamSettings);
		fil.writeInt(pGameInfo.nMonsterRespawnTime);
		fil.writeInt(pGameInfo.nWeaponRespawnTime);
		fil.writeInt(pGameInfo.nItemRespawnTime);
		fil.writeInt(pGameInfo.nSpecialRespawnTime);

		fil.writeByte(0); // gPlaygame
		fil.writeByte(0); // byte_1A9C92
	}

	public static void StatsSave(FileResource fil) {
		fil.writeInt(totalSecrets);
		fil.writeInt(foundSecret);
		fil.writeInt(superSecrets);
		fil.writeInt(totalKills);
		fil.writeInt(kills);
	}

	public static void ScreenSave(FileResource fil) {
		fil.writeInt(123); // unk_128C08
		fil.writeBytes(new byte[256], 256); // unk_128C0C
		fil.writeShort(0); // unk_128E10
		fil.writeShort(0); // unk_128E12
		fil.writeInt(0); // gScreenTilt
		fil.writeInt(deliriumTilt);
		fil.writeInt(deliriumTurn);
		fil.writeInt(deliriumPitch);
	}

	public static void quicksave() {
		if (numplayers > 1 || kFakeMultiplayer)
			return;
		if (gMe.pXsprite.health != 0) {
			gQuickSaving = true;
		}
	}

	// LOAD GAME XXX

	public static boolean checkfile(Resource bb) {
		/*
		 * 266 - v1.10 267 - v1.11 277 - v1.21
		 */
		int saveHeader = checkSave(bb);
		int nVersion = (saveHeader & 0xFFFF);

		// !277 - Incompatible version of saved game found!
		// int nBuild = saveHeader >> 16;
		// !4 - Saved game is from another release of Blood

		if (nVersion < gdxSave)
			Console.Println("Dos saved game found, version: " + nVersion);
		if (nVersion != currentGdxSave && nVersion != 0x115 && nVersion != 0x100)
			return false;

		return loader.load(bb, nVersion);
	}

	public static boolean canLoad(String filename) {
		FileResource fil = BuildGdx.compat.open(filename, Path.User, Mode.Read);
		if (fil != null) {
			int nVersion = checkSave(fil) & 0xFFFF;

			if (nVersion != currentGdxSave) {
				if (nVersion >= gdxSave) {
					final BloodIniFile addon = loader.LoadGDXHeader(fil);

					if (loader.safeGameInfo.nLevel <= kMaxMap && loader.safeGameInfo.nEpisode < kMaxEpisode
							&& loader.safeGameInfo.nDifficulty >= 0 && loader.safeGameInfo.nDifficulty < 5
							&& !loader.gForceMap) {
						MenuCorruptGame menu = (MenuCorruptGame) game.menu.mMenus[CORRUPTLOAD];
						menu.setRunnable(new Runnable() {
							@Override
							public void run() {
								int nEpisode = loader.safeGameInfo.nEpisode;
								int nLevel = loader.safeGameInfo.nLevel;
								int nSkill = loader.safeGameInfo.nDifficulty;
								gGameScreen.newgame(false, addon, nEpisode, nLevel, nSkill, nSkill, nSkill, false);
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
						if (!loadgame(loadname)) {
							game.setPrevScreen();
							game.pNet.ready2send = true;
						}
					}
				});
			}
		}
	}

	public static int lsReadLoadData(String filename) {
		FileResource file = BuildGdx.compat.open(filename, Path.User, Mode.Read);

		if (file != null) {
			Tile pic = engine.getTile(SaveManager.Screenshot);

			if (pic.data == null)
				engine.allocatepermanenttile(SaveManager.Screenshot, 320, 200);

			int nVersion = checkSave(file) & 0xFFFF;
			lsInf.clear();

			switch (nVersion) {
			case currentGdxSave: // gdx format:
				file.seek(SAVEHEADER, Whence.Set);
				lsInf.date = game.date.getDate(file.readLong());
				file.seek(SAVEHEADER + SAVENAME + SAVETIME, Whence.Set);

				lsInf.read(file); // LoadGameInfo
				if (file.remaining() <= SAVESCREENSHOTSIZE) {
					file.close();
					return -1;
				}

				file.read(pic.data, 0, SAVESCREENSHOTSIZE);
				int gUserEpisode = file.readByte();

				if (gUserEpisode == 1) {
					byte[] buf = new byte[144];
					file.read(buf, 0, 144);

					String ininame;
					String fullname = new String(buf).trim();

					int filenameIndex = -1;
					if ((filenameIndex = fullname.indexOf(":")) != -1) {
						String ext = FileUtils.getExtension(fullname.substring(0, filenameIndex));
						ininame = ext + ":" + game.getFilename(fullname.substring(filenameIndex + 1));
					} else
						ininame = game.getFilename(fullname);

					if (!ininame.isEmpty())
						lsInf.iniName = "File: " + ininame;
				}

				int nEnemyDamage = file.readByte() + 1;
				int nEnemyQuantity = file.readByte() + 1;
				int nDifficulty = file.readByte() + 1;
				boolean nPitchforkOnly = file.readBoolean();

				if (lsInf.skill != nEnemyDamage || lsInf.skill != nEnemyQuantity || lsInf.skill != nDifficulty
						|| nPitchforkOnly)
					lsInf.skill = 6;

				lsInf.update();
				engine.getrender().invalidatetile(SaveManager.Screenshot, 0, -1);

				file.close();
				return 1;
			case 0x100: // old format
				file.seek(1, Whence.Set);
				lsInf.read(file); // LoadGameInfo

				file.close();

				return -1; // def tile
			default: // with BLUD header
				file.seek(11, Whence.Set);
				if (nVersion >= gdxSave)
					lsInf.info = "Incompatible ver. " + nVersion + " != " + currentGdxSave;
				else
					lsInf.read(file); // LoadGameInfo

				file.close();
				return -1; // def tile
			}
		} else
			lsInf.clear();
		return -1;
	}

	public static void LoadGameInfo() {
		pGameInfo.copy(loader.safeGameInfo);
	}

	public static int checkSave(Resource bb) {
		String signature = bb.readString(4);

		if (signature == null || !signature.equals("BLUD")) {
			Console.Println("Old saved game found");
			return 0x100;
		}
		int nVersion = bb.readShort();
		int nBuild = bb.readInt();
		return nVersion | nBuild << 16;
	}

	public static boolean loadgame(String filename) { // XXX
		FileResource fil = BuildGdx.compat.open(filename, Path.User, Mode.Read);
		if (fil != null) {
			Console.Println("debug: start loadgame()", OSDTEXT_BLUE);

			boolean status = checkfile(fil);

			fil.close();
			if (status) {
				load();
				if (lastload == null || lastload.isEmpty())
					lastload = filename;

				if (loader.getMessage() != null)
					viewSetMessage(loader.getMessage(), -1, 7);

				return true;
			}

			viewSetMessage("Incompatible version of saved game found!", -1, 7);
			return false;
		}

		viewSetMessage("Can't access to file or file not found!", -1, 7);
		return false;
	}

	public static boolean load() {
		sndStopAllSamples();
		sfxKillAll3DSounds();
		ambStopAll();
		seqKillAll();

		resetQuotes();

		dbInit();
		trInitStructs();
		actInitStruct();

		LoadGDXBlock();
		MyLoad();
		DudesLoad();
		WarpLoad();
		MirrorLoad();
		SeqLoad();
		EventLoad();
		TriggersLoad();
		PlayersLoad();
		ActorsLoad();
		GameInfoLoad();
		StatsLoad();
		ScreenLoad();
		LoadUserEpisodeInfo();

		cheatsOn = loader.cheatsOn;
		gInfiniteAmmo = loader.gInfiniteAmmo;

		InitMirrorTiles();

		loadMapInfo(pGameInfo.nEpisode, pGameInfo.nLevel);

		gVisibility = visibility;
		if (mUserFlag == UserFlag.UserMap || currentEpisode.gMapInfo[pGameInfo.nLevel] == null
				|| currentEpisode.gMapInfo[pGameInfo.nLevel].Title == null) {
			boardfilename = gUserMapInfo.MapName = gUserMapInfo.Title = pGameInfo.zLevelName + ".map";
			gUserMapInfo.Song = null;
			gUserMapInfo.Track = 0;
		} else
			boardfilename = currentEpisode.gMapInfo[pGameInfo.nLevel].Title;

		if (!game.isCurrentScreen(gGameScreen) && !game.isCurrentScreen(gDemoScreen))
			scrLoadPLUs();
		InitSectorFX();
		((BloodNetwork) game.pNet).PredictReset();

		gPrecacheScreen.init(false, new Runnable() {
			@Override
			public void run() {
				ambPrepare();

				sndPlayMusic();

				viewSetMessage("Game loaded", -1, 10);
				gTicks = 0;
				gFrame = 0;

				scrReset();
				gViewPos = 0;
				gViewMode = kView3D;

				totalclock = 0;
				game.gPaused = false;

				game.changeScreen(gGameScreen);
				game.pNet.ResetTimers();
				game.pNet.WaitForAllPlayers(0);
				game.pNet.ready2send = true;

				game.nNetMode = NetMode.Single;

				BuildGdx.audio.getSound().setReverb(false, 0);

				PaletteView = kPalNormal;
				scrSetPalette(PaletteView);
				if (powerupCheck(gMe, kItemDivingSuit - kItemBase) != 0) {
					BuildGdx.audio.getSound().setReverb(true, 0.2f);
				}

				if (powerupCheck(gMe, kItemReflectiveShots - kItemBase) != 0) {
					BuildGdx.audio.getSound().setReverb(true, 0.4f);
				}

				game.pInput.resetMousePos();

				System.gc();
				Console.Println("debug: end loadgame()", OSDTEXT_BLUE);
			}
		});
		game.changeScreen(gPrecacheScreen);

		return true;
	}

	public static void LoadGDXBlock() {
		pGameInfo.nEnemyDamage = loader.safeGameInfo.nEnemyDamage;
		pGameInfo.nEnemyQuantity = loader.safeGameInfo.nEnemyQuantity;
		pGameInfo.nDifficulty = loader.safeGameInfo.nDifficulty;
		pGameInfo.nPitchforkOnly = loader.safeGameInfo.nPitchforkOnly;
		gInfiniteAmmo = loader.gInfiniteAmmo;
	}

	public static void MyLoad() {
		LoadGameInfo();
		numsectors = loader.numsectors;
		numwalls = loader.numwalls;
		numsprites = loader.numsprites;

		for (int i = 0; i < numsectors; i++) {
			if (sector[i] == null)
				sector[i] = new SECTOR();
			sector[i].set(loader.sector[i]);
		}
		for (int i = 0; i < numwalls; i++) {
			if (wall[i] == null)
				wall[i] = new WALL();
			wall[i].set(loader.wall[i]);
		}

		for (int i = 0; i < kMaxSprites; i++) {
			sprite[i].set(loader.sprite[i]);
		}

		bseed = loader.randomseed;
		showinvisibility = loader.showinvisibility;

		parallaxyscale = loader.parallaxyscale;
		visibility = loader.visibility;
		parallaxvisibility = loader.parallaxvisibility;

		System.arraycopy(loader.pskyoff, 0, pskyoff, 0, MAXPSKYTILES);
		pskybits = loader.pskybits;

		Arrays.fill(zeropskyoff, (short) 0);
		System.arraycopy(pskyoff, 0, zeropskyoff, 0, MAXPSKYTILES);

		if (kMaxSectors + 1 >= 0) System.arraycopy(loader.headspritesect, 0, headspritesect, 0, kMaxSectors + 1);
		System.arraycopy(loader.headspritestat, 0, headspritestat, 0, kMaxStatus + 1);
		if (kMaxSprites >= 0) System.arraycopy(loader.prevspritesect, 0, prevspritesect, 0, kMaxSprites);
		if (kMaxSprites >= 0) System.arraycopy(loader.prevspritestat, 0, prevspritestat, 0, kMaxSprites);
		if (kMaxSprites >= 0) System.arraycopy(loader.nextspritesect, 0, nextspritesect, 0, kMaxSprites);
		if (kMaxSprites >= 0) System.arraycopy(loader.nextspritestat, 0, nextspritestat, 0, kMaxSprites);
		if (((kMaxSectors + 7) >> 3) >= 0)
			System.arraycopy(loader.show2dsector, 0, show2dsector, 0, ((kMaxSectors + 7) >> 3));
		System.arraycopy(loader.show2dwall, 0, show2dwall, 0, ((MAXWALLSV7 + 7) >> 3));
		if (((kMaxSprites + 7) >> 3) >= 0)
			System.arraycopy(loader.show2dsprite, 0, show2dsprite, 0, ((kMaxSprites + 7) >> 3));
		automapping = loader.automapping;
		if (((kMaxTiles + 7) >> 3) >= 0) System.arraycopy(loader.gotpic, 0, gotpic, 0, ((kMaxTiles + 7) >> 3));
		if (((kMaxSectors + 7) >> 3) >= 0)
			System.arraycopy(loader.gotsector, 0, gotsector, 0, ((kMaxSectors + 7) >> 3));

		gFrameClock = loader.gFrameClock;
		gTicks = loader.gTicks;
		gFrame = loader.gFrame;
		totalclock = loader.gGameClock;

		game.gPaused = loader.gPaused;

		for (int i = 0; i < loader.kwall.length; i++)
			kwall[i].set(loader.kwall[i]);
		for (int i = 0; i < loader.ksprite.length; i++)
			ksprite[i].set(loader.ksprite[i]);

		if (numsectors >= 0) System.arraycopy(loader.secFloorZ, 0, secFloorZ, 0, numsectors);
		if (numsectors >= 0) System.arraycopy(loader.secCeilZ, 0, secCeilZ, 0, numsectors);
		if (numsectors >= 0) System.arraycopy(loader.floorVel, 0, floorVel, 0, numsectors);
		if (numsectors >= 0) System.arraycopy(loader.ceilingVel, 0, ceilingVel, 0, numsectors);

		pHitInfo.hitsect = loader.safeHitInfo.hitsect;
		pHitInfo.hitwall = loader.safeHitInfo.hitwall;
		pHitInfo.hitsprite = loader.safeHitInfo.hitsprite;
		pHitInfo.hitx = loader.safeHitInfo.hitx;
		pHitInfo.hity = loader.safeHitInfo.hity;
		pHitInfo.hitz = loader.safeHitInfo.hitz;

		mUserFlag = UserFlag.None;
		if (loader.gForceMap)
			mUserFlag = UserFlag.UserMap;

		System.arraycopy(loader.nStatSize, 0, nStatSize, 0, kMaxStatus + 1);
		System.arraycopy(loader.nextXSprite, 0, nextXSprite, 0, kMaxXSprites);
		System.arraycopy(loader.nextXWall, 0, nextXWall, 0, kMaxXWalls);
		System.arraycopy(loader.nextXSector, 0, nextXSector, 0, kMaxXSectors);

		for (int i = 0; i < kMaxXSprites; i++) {
			xsprite[i].copy(loader.xsprite[i]);
		}
		for (int i = 0; i < kMaxXWalls; i++) {
			xwall[i].copy(loader.xwall[i]);
		}
		for (int i = 0; i < kMaxXSectors; i++) {
			xsector[i].copy(loader.xsector[i]);
		}

		System.arraycopy(loader.sprXVel, 0, sprXVel, 0, kMaxSprites);
		System.arraycopy(loader.sprYVel, 0, sprYVel, 0, kMaxSprites);
		System.arraycopy(loader.sprZVel, 0, sprZVel, 0, kMaxSprites);

		gSkyCount = loader.gSkyCount;
		gFogMode = loader.gFogMode;

		gNoClip = loader.gNoClip;
		gFullMap = loader.gFullMap;
	}

	public static void DudesLoad() {
		Arrays.fill(cumulDamage, 0);
		System.arraycopy(loader.gDudeSlope, 0, gDudeSlope, 0, kMaxXSprites);
	}

	public static void WarpLoad() {
		for (int i = 0; i < kMaxPlayers; i++) {
			gStartZone[i].x = loader.gStartZone[i].x;
			gStartZone[i].y = loader.gStartZone[i].y;
			gStartZone[i].z = loader.gStartZone[i].z;
			gStartZone[i].sector = loader.gStartZone[i].sector;
			gStartZone[i].angle = loader.gStartZone[i].angle;
		}
		if (kMaxSectors >= 0) System.arraycopy(loader.gUpperLink, 0, gUpperLink, 0, kMaxSectors);
		if (kMaxSectors >= 0) System.arraycopy(loader.gLowerLink, 0, gLowerLink, 0, kMaxSectors);
	}

	public static void MirrorLoad() {
		mirrorcnt = loader.mirrorcnt;
		MirrorSector = loader.MirrorSector;
		for (int i = 0; i < MAXMIRRORS; i++) {
			MirrorType[i] = loader.MirrorType[i];
			MirrorLower[i] = loader.MirrorLower[i];
			MirrorX[i] = loader.MirrorX[i];
			MirrorY[i] = loader.MirrorY[i];
			MirrorZ[i] = loader.MirrorZ[i];
			MirrorUpper[i] = loader.MirrorUpper[i];
		}
		System.arraycopy(loader.MirrorWall, 0, MirrorWall, 0, 4);

		initMirrorWall();
	}

	public static void SeqLoad() {
		for (int i = 0; i < kMaxXWalls; i++) {
			siWall[i].copy(loader.siWall[i]);
		}
		for (int i = 0; i < kMaxXWalls; i++) {
			siMasked[i].copy(loader.siMasked[i]);
		}
		for (int i = 0; i < kMaxXSectors; i++) {
			siCeiling[i].copy(loader.siCeiling[i]);
		}
		for (int i = 0; i < kMaxXSectors; i++) {
			siFloor[i].copy(loader.siFloor[i]);
		}
		for (int i = 0; i < kMaxXSprites; i++) {
			siSprite[i].copy(loader.siSprite[i]);
		}

		activeList.set(loader.actListType, loader.actListIndex, loader.activeCount);
	}

	public static void EventLoad() {
		if (eventQ == null || eventQ instanceof BPriorityQueue) {
			if (gdxEventQ == null)
				gdxEventQ = new JPriorityQueue(kPQueueSize);
			eventQ = gdxEventQ;
			for (int i = 0; i < kMaxChannels; i++) {
				if (rxBucket[i] == null)
					rxBucket[i] = new RXBUCKET();
				else
					rxBucket[i].flush();
			}
		}

		eventQ.flush();
		for (int i = 0; i <= loader.fNodeCount; i++)
			eventQ.Insert(loader.qEventPriority[i], loader.qEventEvent[i]);

		for (int i = 0; i < kMaxChannels; i++) {
			rxBucket[i].index = loader.rxBucketIndex[i];
			rxBucket[i].type = loader.rxBucketType[i];
		}

		System.arraycopy(loader.bucketHead, 0, bucketHead, 0, kMaxID + 1);
	}

	public static void TriggersLoad() {
		gBusyCount = loader.gBusyCount;
		for (int i = 0; i < kMaxBusyArray; i++) {
			gBusy[i].nIndex = loader.gBusy[i].nIndex;
			gBusy[i].nDelta = loader.gBusy[i].nDelta;
			gBusy[i].nBusy = loader.gBusy[i].nBusy;
			gBusy[i].busyProc = loader.gBusy[i].busyProc;
		}
		if (kMaxSectors >= 0) System.arraycopy(loader.secPath, 0, secPath, 0, kMaxSectors);
	}

	public static void PlayersLoad() {
		System.arraycopy(loader.nTeamCount, 0, nTeamCount, 0, kMaxPlayers);
		numplayers = (short) loader.gNetPlayers;

		if (numplayers >= 0) System.arraycopy(loader.connectpoint2, 0, connectpoint2, 0, numplayers);

		for (int i = 0; i < kMaxPlayers; i++) {
			if (loader.autoaim[i] != -1)
				game.net.gProfile[i].autoaim = loader.autoaim[i] == 1;
			if (loader.slopetilt[i] != -1)
				game.net.gProfile[i].slopetilt = loader.slopetilt[i] == 1;
			if (loader.skill[i] != -1)
				game.net.gProfile[i].skill = loader.skill[i];
			if (loader.name[i] != null)
				game.net.gProfile[i].name = loader.name[i];
		}

		for (int i = 0; i < kMaxPlayers; i++) {
			loader.safePlayer[i].pInput.Copy(gPlayer[i].pInput);
			gPlayer[i].copy(loader.safePlayer[i]);
		}

		for (int i = 0; i < numplayers; i++) {
			gPlayer[i].pSprite = sprite[gPlayer[i].nSprite];
			gPlayer[i].pXsprite = xsprite[gPlayer[i].pSprite.extra];
			gPlayer[i].pDudeInfo = dudeInfo[gPlayer[i].pSprite.lotag - kDudeBase];
		}
		gViewIndex = myconnectindex;
	}

	public static void ActorsLoad() {
//		for(int i = 0; i < kMaxXSprites; i++) {
//			gSpriteHit[i].moveHit = loader.gSpriteHit[i].moveHit;
//			gSpriteHit[i].ceilHit = loader.gSpriteHit[i].ceilHit;
//			gSpriteHit[i].floorHit = loader.gSpriteHit[i].floorHit;
//		}

		if (kMaxSectors >= 0) System.arraycopy(loader.gSectorExp, 0, gSectorExp, 0, kMaxSectors);

		System.arraycopy(loader.gWallExp, 0, gWallExp, 0, kMaxXWalls);

		gPostCount = loader.gPostCount;
//		for(int i = 0; i < kMaxSprites; i++) {
//			gPost[i].nSprite = loader.gPost[i].nSprite;
//			gPost[i].nStatus = loader.gPost[i].nStatus;
//		}
		actInit(true, IsOriginalDemo());
		for (int p = 0; p < numplayers; p++) {
			playerSetRace(gPlayer[p], gPlayer[p].nLifeMode);
			gPlayer[p].ang = gPlayer[p].pSprite.ang;
		}
	}

	public static void GameInfoLoad() {
		gNextMap = loader.gNextMap;
		LoadGameInfo();
	}

	public static void StatsLoad() {
		totalSecrets = loader.totalSecrets;
		foundSecret = loader.foundSecret;
		superSecrets = loader.superSecrets;
		totalKills = loader.totalKills;
		kills = loader.kills;
	}

	public static void ScreenLoad() {
		deliriumTilt = loader.deliriumTilt;
		deliriumTurn = loader.deliriumTurn;
		deliriumPitch = loader.deliriumPitch;
	}

	public static void LoadUserEpisodeInfo() {
		if (loader.gUserEpisode)
			mUserFlag = UserFlag.Addon;

		if (mUserFlag == UserFlag.Addon) {
			BloodIniFile ini = loader.addon;
			getEpisodeInfo(gUserEpisodeInfo, ini);
			checkEpisodeResources(ini);
		} else
			resetEpisodeResources();
	}
}
