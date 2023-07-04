package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.MAXPSKYTILES;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.MAXWALLS;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.numwalls;
import static ru.m210projects.Build.Engine.parallaxtype;
import static ru.m210projects.Build.Engine.parallaxyoffs;
import static ru.m210projects.Build.Engine.prevspritesect;
import static ru.m210projects.Build.Engine.prevspritestat;
import static ru.m210projects.Build.Engine.pskybits;
import static ru.m210projects.Build.Engine.pskyoff;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.show2dsector;
import static ru.m210projects.Build.Engine.show2dsprite;
import static ru.m210projects.Build.Engine.show2dwall;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.visibility;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.zeropskyoff;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Wang.Draw.SetRedrawScreen;
import static ru.m210projects.Wang.Enemies.Bunny.Bunny_Count;
import static ru.m210projects.Wang.Enemies.Sumo.BossSpriteNum;
import static ru.m210projects.Wang.Enemies.Sumo.serpwasseen;
import static ru.m210projects.Wang.Enemies.Sumo.sumowasseen;
import static ru.m210projects.Wang.Enemies.Sumo.triedplay;
import static ru.m210projects.Wang.Enemies.Sumo.zillawasseen;
import static ru.m210projects.Wang.Factory.WangMenuHandler.CORRUPTLOAD;
import static ru.m210projects.Wang.Factory.WangNetwork.CommPlayers;
import static ru.m210projects.Wang.Game.FinishAnim;
import static ru.m210projects.Wang.Game.FinishedLevel;
import static ru.m210projects.Wang.Game.GodMode;
import static ru.m210projects.Wang.Game.InitTimingVars;
import static ru.m210projects.Wang.Game.Kills;
import static ru.m210projects.Wang.Game.Level;
import static ru.m210projects.Wang.Game.PlayClock;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.PlayingLevel;
import static ru.m210projects.Wang.Game.Skill;
import static ru.m210projects.Wang.Game.TotalKillable;
import static ru.m210projects.Wang.Game.boardfilename;
import static ru.m210projects.Wang.Game.currentGame;
import static ru.m210projects.Wang.Game.defGame;
import static ru.m210projects.Wang.Game.rec;
import static ru.m210projects.Wang.Game.screenpeek;
import static ru.m210projects.Wang.Game.totalsynctics;
import static ru.m210projects.Wang.Gameutils.MAXANIM;
import static ru.m210projects.Wang.Gameutils.MAX_SECTOR_OBJECTS;
import static ru.m210projects.Wang.Gameutils.PF_DEAD;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.JSector.MAXMIRRORS;
import static ru.m210projects.Wang.JSector.mirror;
import static ru.m210projects.Wang.JSector.mirrorcnt;
import static ru.m210projects.Wang.JSector.mirrorinview;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.gGameScreen;
import static ru.m210projects.Wang.Main.gLoadingScreen;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Main.gPrecacheScreen;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Main.mUserFlag;
import static ru.m210projects.Wang.Palette.DoPlayerDivePalette;
import static ru.m210projects.Wang.Palette.DoPlayerNightVisionPalette;
import static ru.m210projects.Wang.Player.NormalVisibility;
import static ru.m210projects.Wang.Sector.AnimCnt;
import static ru.m210projects.Wang.Sector.GetAnimObject;
import static ru.m210projects.Wang.Sector.LevelSecrets;
import static ru.m210projects.Wang.Sector.MAX_SINE_WALL;
import static ru.m210projects.Wang.Sector.MAX_SINE_WALL_POINTS;
import static ru.m210projects.Wang.Sector.MAX_SINE_WAVE;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Sector.SineWall;
import static ru.m210projects.Wang.Sector.SineWaveFloor;
import static ru.m210projects.Wang.Sector.SpringBoard;
import static ru.m210projects.Wang.Sector.pAnim;
import static ru.m210projects.Wang.Sector.x_max_bound;
import static ru.m210projects.Wang.Sector.x_min_bound;
import static ru.m210projects.Wang.Sector.y_max_bound;
import static ru.m210projects.Wang.Sector.y_min_bound;
import static ru.m210projects.Wang.Sound.CDAudio_Play;
import static ru.m210projects.Wang.Sound.RedBookSong;
import static ru.m210projects.Wang.Sound.StartAmbientSound;
import static ru.m210projects.Wang.Sound.StopFX;
import static ru.m210projects.Wang.Sound.playTrack;
import static ru.m210projects.Wang.Sprites.MoveSkip2;
import static ru.m210projects.Wang.Sprites.MoveSkip4;
import static ru.m210projects.Wang.Sprites.MoveSkip8;
import static ru.m210projects.Wang.Sprites.SectorObject;
import static ru.m210projects.Wang.Text.PutStringInfo;
import static ru.m210projects.Wang.Track.MAX_TRACKS;
import static ru.m210projects.Wang.Track.Track;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.ResourceHandler.InitSpecialTextures;
import static ru.m210projects.Wang.Type.ResourceHandler.checkEpisodeResources;
import static ru.m210projects.Wang.Type.ResourceHandler.resetEpisodeResources;
import static ru.m210projects.Wang.Weapon.FinishTimer;
import static ru.m210projects.Wang.Weapon.FloorBloodQueue;
import static ru.m210projects.Wang.Weapon.FloorBloodQueueHead;
import static ru.m210projects.Wang.Weapon.GenericQueue;
import static ru.m210projects.Wang.Weapon.GenericQueueHead;
import static ru.m210projects.Wang.Weapon.HoleQueue;
import static ru.m210projects.Wang.Weapon.HoleQueueHead;
import static ru.m210projects.Wang.Weapon.LoWangsQueue;
import static ru.m210projects.Wang.Weapon.LoWangsQueueHead;
import static ru.m210projects.Wang.Weapon.MAX_FLOORBLOOD_QUEUE;
import static ru.m210projects.Wang.Weapon.MAX_GENERIC_QUEUE;
import static ru.m210projects.Wang.Weapon.MAX_HOLE_QUEUE;
import static ru.m210projects.Wang.Weapon.MAX_LOWANGS_QUEUE;
import static ru.m210projects.Wang.Weapon.MAX_STAR_QUEUE;
import static ru.m210projects.Wang.Weapon.MAX_WALLBLOOD_QUEUE;
import static ru.m210projects.Wang.Weapon.StarQueue;
import static ru.m210projects.Wang.Weapon.StarQueueHead;
import static ru.m210projects.Wang.Weapon.WallBloodQueue;
import static ru.m210projects.Wang.Weapon.WallBloodQueueHead;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.BufferResource;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileResource;
import ru.m210projects.Build.FileHandle.FileResource.Mode;
import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Pattern.Tools.SaveManager;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Wang.Main.UserFlag;
import ru.m210projects.Wang.Menus.MenuCorruptGame;
import ru.m210projects.Wang.Type.Anim;
import ru.m210projects.Wang.Type.GameInfo;
import ru.m210projects.Wang.Type.LSInfo;
import ru.m210projects.Wang.Type.List;
import ru.m210projects.Wang.Type.MirrorType;
import ru.m210projects.Wang.Type.Panel_Sprite;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.SafeLoader;
import ru.m210projects.Wang.Type.Sect_User;
import ru.m210projects.Wang.Type.TRACK;
import ru.m210projects.Wang.Type.TRACK_POINT;

public class LoadSave {

	public static boolean gQuickSaving;
	public static boolean gAutosaveRequest;

	public static LSInfo lsInf = new LSInfo();
	public static SafeLoader loader = new SafeLoader();

	public static final String savsign = "SHWR";
	public static final int gdxVersionSave = 100;
	public static final int gdxCurrentSave = 101;

	public static final int SAVEVERSION = savsign.length() + 2; // version (2 bytes)
	public static final int SAVETIME = 8;
	public static final int SAVENAME = 32;
	public static final int SAVELEVELINFO = 8;
	public static final int SAVEHEADER = SAVEVERSION + SAVETIME + SAVENAME + SAVELEVELINFO;

	public static final int SAVESCREENSHOTSIZE = 160 * 100;
	public static final int SAVEGDXDATA = SAVESCREENSHOTSIZE + 128;

	public static int quickslot = 0;
	public static String lastload;

	public static void FindSaves() {
		FileResource fil = null;
		for (Iterator<FileEntry> it = BuildGdx.compat.getDirectory(Path.User).getFiles().values().iterator(); it
				.hasNext();) {
			FileEntry file = it.next();

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
						if (nVersion >= gdxVersionSave) {
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
			if (engine.getTile(SaveManager.Screenshot).data == null)
				engine.allocatepermanenttile(SaveManager.Screenshot, 160, 100);

			int nVersion = checkSave(fil) & 0xFFFF;
			lsInf.clear();

			if (nVersion >= gdxVersionSave) {
				fil.seek(SAVEVERSION, Whence.Set);
				lsInf.date = game.date.getDate(fil.readLong());
				fil.seek(SAVEVERSION + SAVETIME + SAVENAME, Whence.Set); // to SAVELEVELINFO

				if (nVersion != gdxCurrentSave) {
					lsInf.read(fil);
					lsInf.info = "Incompatible ver. " + nVersion + " != " + gdxCurrentSave;
				} else
					lsInf.read(fil); // LoadGameInfo
				if (fil.remaining() <= SAVESCREENSHOTSIZE) {
					fil.close();
					return -1;
				}

				fil.read(engine.getTile(SaveManager.Screenshot).data, 0, SAVESCREENSHOTSIZE);

				lsInf.addonfile = null;
				if (fil.readBoolean()) {
					boolean isPackage = fil.readBoolean();

					String fullname = fil.readString(144).trim();
					if (isPackage && !fullname.isEmpty())
						lsInf.addonfile = "File: " + FileUtils.getFullName(fullname);
					else if (!fullname.isEmpty())
						lsInf.addonfile = "Addon: " + FileUtils.getFullName(fullname);
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
			BufferResource res = save(savename, time);

			fil.writeBytes(res.getBytes(), res.size());
			fil.close();

			System.gc();
			game.pSavemgr.add(savename, time, filename);
			lastload = filename;

			PutStringInfo(Player[myconnectindex], "Game saved");
			return 0;
		} else
			PutStringInfo(Player[myconnectindex], "Game not saved. Access denied!");

		return -1;
	}

	public static BufferResource save(String savename, long time) {

		BufferResource res = new BufferResource();

		SaveHeader(res, savename, time);
		SaveGDXBlock(res);
		SaveMap(res);
		SavePanelSprites(res);
		SavePlayers(res);
		SaveSectorUserInfos(res);
		SaveUserInfos(res);
		SaveSectorObjects(res);
		SaveSineSect(res);

		res.writeInt(x_min_bound);
		res.writeInt(y_min_bound);
		res.writeInt(x_max_bound);
		res.writeInt(y_max_bound);

		SaveTracks(res);

		res.writeInt(screenpeek);
		res.writeInt(totalsynctics);

		SaveAnims(res);

		res.writeInt(totalclock);
		res.writeInt(engine.getrand());

		res.writeShort(NormalVisibility);
		res.writeInt(visibility);
		res.writeByte(parallaxtype);
		res.writeInt(parallaxyoffs);
		res.writeBytes(pskyoff);
		res.writeShort(pskybits);

		res.writeBoolean(MoveSkip2);
		res.writeInt(MoveSkip4);
		res.writeInt(MoveSkip8);

		SaveMirrors(res);
		SaveQueues(res);
		SaveStuff(res);

		return res;
	}

	private static void SaveStuff(BufferResource fil) {
		fil.writeInt(PlayClock);
		fil.writeInt(Kills);
		fil.writeInt(TotalKillable);

		// game settings
		gNet.save(fil);

		for (int i = 0; i < MAXTILES; i++)
			fil.writeInt(engine.getTile(i).anm);
		fil.writeShort(LevelSecrets);

		fil.writeBytes(show2dwall);
		fil.writeBytes(show2dsprite);
		fil.writeBytes(show2dsector);
		fil.writeInt(Bunny_Count);

		fil.writeBoolean(GodMode);

		fil.writeBoolean(serpwasseen);
		fil.writeBoolean(sumowasseen);
		fil.writeBoolean(zillawasseen);
		fil.writeBytes(BossSpriteNum);
	}

	private static void SaveMirrors(BufferResource res) {
		res.writeInt(mirrorcnt);
		res.writeBoolean(mirrorinview);
		for (int i = 0; i < MAXMIRRORS; i++)
			mirror[i].save(res);
	}

	private static void SaveQueues(BufferResource res) {
		res.writeShort(StarQueueHead);
		res.writeBytes(StarQueue);
		res.writeShort(HoleQueueHead);
		res.writeBytes(HoleQueue);
		res.writeShort(WallBloodQueueHead);
		res.writeBytes(WallBloodQueue);
		res.writeShort(FloorBloodQueueHead);
		res.writeBytes(FloorBloodQueue);
		res.writeShort(GenericQueueHead);
		res.writeBytes(GenericQueue);
		res.writeShort(LoWangsQueueHead);
		res.writeBytes(LoWangsQueue);
	}

	private static void SaveVersion(BufferResource fil, int nVersion) {
		fil.writeBytes(savsign);
		fil.writeShort(nVersion);
	}

	private static void SaveScreenshot(BufferResource fil) {
		fil.writeBytes(gGameScreen.captBuffer, SAVESCREENSHOTSIZE);
		gGameScreen.captBuffer = null;
	}

	private static void SaveGDXBlock(BufferResource fil) {
		SaveScreenshot(fil);

		ByteBuffer bb = ByteBuffer.allocate(SAVEGDXDATA);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		byte warp_on = 0;
		if (mUserFlag == UserFlag.Addon)
			warp_on = (byte) ((currentGame != null) ? 1 : 2);
		if (mUserFlag == UserFlag.UserMap)
			warp_on = 2;

		bb.put(warp_on);
		if (warp_on == 1) // user episode
		{
			bb.put(currentGame.isPackage() ? (byte) 1 : 0);
			byte[] name = new byte[144];
			String path = currentGame.getPath();
			System.arraycopy(path.getBytes(), 0, name, 0, Math.min(path.length(), 144));

			bb.put(name);
		}
		fil.writeBytes(bb.array(), SAVEGDXDATA);
	}

	private static void SaveHeader(BufferResource fil, String savename, long time) {
		SaveVersion(fil, gdxCurrentSave);

		fil.writeLong(time);
		fil.writeBytes(savename.toCharArray(), SAVENAME);

		fil.writeInt(Level);
		fil.writeInt(Skill);
	}

	private static void SavePlayers(BufferResource fil) {
		fil.writeShort(numplayers);
		fil.writeShort(CommPlayers);
		fil.writeShort(myconnectindex);
		fil.writeShort(connecthead);
		fil.writeBytes(connectpoint2);

		for (int i = 0; i < numplayers; i++)
			fil.writeBytes(Player[i].getBytes());
	}

	private static void SavePanelSprites(BufferResource fil) {
		int ndx = 0;
		for (int i = 0; i < numplayers; i++) {
			PlayerStr pp = Player[i];
			ndx = 0;

			Panel_Sprite n;
			for (Panel_Sprite cur = pp.PanelSpriteList.Next; cur != pp.PanelSpriteList; cur = n) {
				n = cur.Next;

				fil.writeInt(ndx);
				fil.writeInt(List.PanelSpriteToNdx(pp.PanelSpriteList, cur.sibling));
				cur.save(fil);
				ndx++;
			}
			fil.writeInt(-1);
		}
	}

	private static void SaveMap(BufferResource fil) {
		if (boardfilename != null)
			fil.writeBytes(boardfilename.toCharArray(), 144);
		else
			fil.writeBytes(new byte[144], 144);

		fil.writeShort(numwalls);
		for (int w = 0; w < numwalls; w++)
			fil.writeBytes(wall[w].getBytes());

		fil.writeShort(numsectors);
		for (int s = 0; s < numsectors; s++)
			fil.writeBytes(sector[s].getBytes());

		for (int i = 0; i < MAXSPRITES; i++)
			fil.writeBytes(sprite[i].getBytes());

		fil.writeBytes(headspritesect);
		fil.writeBytes(headspritestat);

		fil.writeBytes(prevspritesect);
		fil.writeBytes(prevspritestat);
		fil.writeBytes(nextspritesect);
		fil.writeBytes(nextspritestat);

		fil.writeInt(FinishTimer);
		fil.writeBoolean(FinishedLevel);
		fil.writeInt(FinishAnim);
		fil.writeInt(playTrack);
	}

	private static void SaveSectorUserInfos(BufferResource fil) {
		// Sector User information
		for (int i = 0; i < numsectors; i++) {
			// write trailer
			fil.writeInt(SectUser[i] != null ? i : -1);
			if (SectUser[i] != null)
				fil.writeBytes(SectUser[i].getBytes());
		}
	}

	private static void SaveUserInfos(BufferResource fil) {
		for (int i = 0; i < MAXSPRITES; i++) {
			// write header
			fil.writeInt(pUser[i] != null ? i : -1);
			if (pUser[i] != null)
				pUser[i].save(fil);
		}
	}

	private static void SaveSectorObjects(BufferResource fil) {
		// Sector User information
		for (int i = 0; i < MAX_SECTOR_OBJECTS; i++)
			fil.writeBytes(SectorObject[i].getBytes());
	}

	private static void SaveSineSect(BufferResource fil) {
		for (int i = 0; i < MAX_SINE_WAVE; i++)
			for (int j = 0; j < 21; j++)
				SineWaveFloor[i][j].save(fil);
		for (int i = 0; i < MAX_SINE_WALL; i++)
			for (int j = 0; j < MAX_SINE_WALL_POINTS; j++)
				SineWall[i][j].save(fil);
		for (int i = 0; i < 20; i++)
			SpringBoard[i].save(fil);
	}

	private static void SaveTracks(BufferResource fil) {
		for (int i = 0; i < MAX_TRACKS; i++) {
			TRACK tr = Track[i];
			fil.writeInt(tr.ttflags);
			fil.writeShort(tr.flags);
			fil.writeShort(tr.NumPoints);
			if (tr.NumPoints != 0) {
				for (int j = 0; j < tr.NumPoints; j++) {
					TRACK_POINT tp = tr.TrackPoint[j];
					fil.writeInt(tp.x);
					fil.writeInt(tp.y);
					fil.writeInt(tp.z);

					fil.writeShort(tp.ang);
					fil.writeShort(tp.tag_low);
					fil.writeShort(tp.tag_high);
				}
			}
		}
	}

	private static void SaveAnims(BufferResource fil) {
		fil.writeInt(AnimCnt);
		for (int i = 0; i < MAXANIM; i++)
			pAnim[i].save(fil);
	}

	public static void quicksave() {
		if (numplayers > 1 || game.net.FakeMultiplayer)
			return;
		if (!TEST(Player[myconnectindex].Flags, PF_DEAD)) {
			gQuickSaving = true;
		}
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
							game.show();
					}
				});
			}
		}
	}

	public static boolean checkfile(Resource bb) {
		int nVersion = checkSave(bb);

		if (nVersion != gdxCurrentSave)
			return false;

		if (!loader.load(bb))
			return false;

		return true;
	}

	public static boolean canLoad(String filename) {
		FileResource fil = BuildGdx.compat.open(filename, Path.User, Mode.Read);
		if (fil != null) {
			int nVersion = checkSave(fil);

			if (nVersion != gdxCurrentSave) {
				if (nVersion >= gdxVersionSave) {
					final GameInfo addon = loader.LoadGDXHeader(fil);
					if (loader.Level <= 20 && loader.Skill >= 0 && loader.Skill < 4 && loader.warp_on != 2) { // if not
																												// usermap
						MenuCorruptGame menu = (MenuCorruptGame) game.menu.mMenus[CORRUPTLOAD];
						menu.setRunnable(new Runnable() {
							@Override
							public void run() {
								GameInfo game = addon != null ? addon : defGame;
								int nEpisode = game.getNumEpisode(loader.Level);
								int nLevel = game.getNumLevel(loader.Level);
								gGameScreen.newgame(false, game, nEpisode, nLevel, loader.Skill);
							}
						});
						game.menu.mOpen(menu, -1);
					}
				}
			}

			fil.close();
			return nVersion == gdxCurrentSave;
		}
		return false;
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
					PutStringInfo(Player[myconnectindex], loader.getMessage());

				return true;
			}

			PutStringInfo(Player[myconnectindex], "Incompatible version of saved game found!");
			return false;
		}

		PutStringInfo(Player[myconnectindex], "Can't access to file or file not found!");
		return false;
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

	public static void load() {
		if (rec != null)
			rec.close();

		Level = loader.Level;
		Skill = loader.Skill;

		LoadGDXBlock();
		MapLoad();
		LoadPlayers();
		LoadSectorUserInfos();
		LoadUserInfos();
		LoadSectorObjects();
		LoadSineSect();

		x_min_bound = loader.x_min_bound;
		y_min_bound = loader.y_min_bound;
		x_max_bound = loader.x_max_bound;
		y_max_bound = loader.y_max_bound;

		LoadTracks();

		screenpeek = loader.screenpeek;
		totalsynctics = loader.totalsynctics;

		LoadAnims();

		totalclock = loader.totalclock;
		engine.srand(loader.randomseed);
		NormalVisibility = loader.NormalVisibility;
		visibility = loader.visibility;
		parallaxtype = loader.parallaxtype;
		parallaxyoffs = loader.parallaxyoffs;
		System.arraycopy(loader.pskyoff, 0, pskyoff, 0, MAXPSKYTILES);
		System.arraycopy(pskyoff, 0, zeropskyoff, 0, MAXPSKYTILES);
		pskybits = loader.pskybits;
		MoveSkip2 = loader.MoveSkip2;
		MoveSkip4 = loader.MoveSkip4;
		MoveSkip8 = loader.MoveSkip8;

		LoadMirrors();
		LoadQueues();
		LoadStuff();

		StopFX();
		SetRedrawScreen(Player[myconnectindex]);

		screenpeek = myconnectindex;
		PlayingLevel = Level;

		for (int i = connecthead; i != -1; i = connectpoint2[i]) {
			Player[i].PlayerTalking = false;
			Player[i].TalkVocnum = -1;
			Player[i].TalkVocHandle = null;
			Player[i].StartColor = 0;
		}

		for (int i = AnimCnt - 1; i >= 0; i--) {
			Anim gAnm = pAnim[i];
			Object object = (gAnm.ptr = GetAnimObject(gAnm.index, gAnm.type));
			switch (gAnm.type) {
			case FloorZ:
				game.pInt.setfloorinterpolate(gAnm.index, (SECTOR) object);
				break;
			case CeilZ:
				game.pInt.setceilinterpolate(gAnm.index, (SECTOR) object);
				break;
			case SpriteZ:
				game.pInt.setsprinterpolate(gAnm.index, (SPRITE) object);
				break;
			default:
				break;
			}
		}

		gPrecacheScreen.init(false, new Runnable() {
			@Override
			public void run() {
				InitSpecialTextures();
				DoPlayerDivePalette(Player[myconnectindex]);
				DoPlayerNightVisionPalette(Player[myconnectindex]);

				gs.AutoAim = (Player[myconnectindex].Flags & Gameutils.PF_AUTO_AIM) != 0;

				BuildGdx.audio.getSound().setReverb(false, 0);

				game.pInput.resetMousePos();
				game.gPaused = false;

				game.nNetMode = NetMode.Single;

				game.changeScreen(gGameScreen);
				int SavePlayClock = PlayClock;
				InitTimingVars();
				PlayClock = SavePlayClock;

				game.pNet.WaitForAllPlayers(0);
				game.pNet.ready2send = true;

				triedplay = false;
				if (loader.playTrack != -1)
					CDAudio_Play(loader.playTrack, true); // track, loop - Level songs are looped
				else
					CDAudio_Play(RedBookSong[Level], true);
				StartAmbientSound();

				System.gc();
			}
		});
		game.changeScreen(gPrecacheScreen);
	}

	private static void MapLoad() {
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

		FinishTimer = loader.FinishTimer;
		FinishedLevel = loader.FinishedLevel;
		FinishAnim = loader.FinishAnim;
		// playTrack = loader.playTrack; don't set it here
	}

	private static void LoadPlayers() {
		numplayers = loader.numplayers;
		CommPlayers = loader.CommPlayers;
		myconnectindex = loader.myconnectindex;
		connecthead = loader.connecthead;
		System.arraycopy(loader.connectpoint2, 0, connectpoint2, 0, MAXPLAYERS);
		for (int i = 0; i < numplayers; i++)
			Player[i].copy(loader.Player[i]);
	}

	private static void LoadSectorUserInfos() {
		for (int i = 0; i < numsectors; i++) {
			if (loader.SectUser[i] == null)
				continue;

			if (SectUser[i] == null)
				SectUser[i] = new Sect_User();
			SectUser[i].copy(loader.SectUser[i]);
		}
	}

	private static void LoadUserInfos() {
		for (int i = 0; i < MAXSPRITES; i++) {
			pUser[i] = loader.pUser[i];
		}
	}

	private static void LoadSectorObjects() {
		for (int i = 0; i < MAX_SECTOR_OBJECTS; i++)
			SectorObject[i].copy(loader.SectorObject[i]);
	}

	private static void LoadSineSect() {
		for (int i = 0; i < MAX_SINE_WAVE; i++)
			System.arraycopy(loader.SineWaveFloor[i], 0, SineWaveFloor[i], 0, 21); // XXX copy method
		for (int i = 0; i < MAX_SINE_WALL; i++)
			System.arraycopy(loader.SineWall[i], 0, SineWall[i], 0, MAX_SINE_WALL_POINTS); // XXX copy method
		System.arraycopy(loader.SpringBoard, 0, SpringBoard, 0, 20); // XXX copy method
	}

	private static void LoadTracks() {
		for (int i = 0; i < MAX_TRACKS; i++) {
			if (Track[i] == null)
				Track[i] = new TRACK();
			Track[i].copy(loader.Track[i]);
		}
	}

	private static void LoadAnims() {
		AnimCnt = loader.AnimCnt;
		for (int i = 0; i < MAXANIM; i++)
			pAnim[i].copy(loader.pAnim[i]);
	}

	private static void LoadMirrors() {
		mirrorcnt = loader.mirrorcnt;
		mirrorinview = loader.mirrorinview;
		for (int i = 0; i < MAXMIRRORS; i++) {
			if (mirror[i] == null)
				mirror[i] = new MirrorType();
			mirror[i].copy(loader.mirror[i]);
		}
	}

	private static void LoadQueues() {
		StarQueueHead = loader.StarQueueHead;
		System.arraycopy(loader.StarQueue, 0, StarQueue, 0, MAX_STAR_QUEUE);

		HoleQueueHead = loader.HoleQueueHead;
		System.arraycopy(loader.HoleQueue, 0, HoleQueue, 0, MAX_HOLE_QUEUE);

		WallBloodQueueHead = loader.WallBloodQueueHead;
		System.arraycopy(loader.WallBloodQueue, 0, WallBloodQueue, 0, MAX_WALLBLOOD_QUEUE);

		FloorBloodQueueHead = loader.FloorBloodQueueHead;
		System.arraycopy(loader.FloorBloodQueue, 0, FloorBloodQueue, 0, MAX_FLOORBLOOD_QUEUE);

		GenericQueueHead = loader.GenericQueueHead;
		System.arraycopy(loader.GenericQueue, 0, GenericQueue, 0, MAX_GENERIC_QUEUE);

		LoWangsQueueHead = loader.LoWangsQueueHead;
		System.arraycopy(loader.LoWangsQueue, 0, LoWangsQueue, 0, MAX_LOWANGS_QUEUE);
	}

	private static void LoadStuff() {
		PlayClock = loader.PlayClock;
		Kills = loader.Kills;
		TotalKillable = loader.TotalKillable;

		gNet.KillLimit = loader.TotalKillable;
		gNet.TimeLimit = loader.TimeLimit;
		gNet.TimeLimitClock = loader.TimeLimitClock;
		gNet.MultiGameType = loader.MultiGameType;
		gNet.TeamPlay = loader.TeamPlay;
		gNet.HurtTeammate = loader.HurtTeammate;
		gNet.SpawnMarkers = loader.SpawnMarkers;
		gNet.NoRespawn = loader.NoRespawn;
		gNet.Nuke = loader.Nuke;

		for (int i = 0; i < MAXTILES; i++)
			engine.getTile(i).anm = loader.picanm[i];
		LevelSecrets = loader.LevelSecrets;

		System.arraycopy(loader.show2dwall, 0, show2dwall, 0, (MAXWALLS + 7) >> 3);
		System.arraycopy(loader.show2dsprite, 0, show2dsprite, 0, (MAXSPRITES + 7) >> 3);
		System.arraycopy(loader.show2dsector, 0, show2dsector, 0, (MAXSECTORS + 7) >> 3);

		Bunny_Count = loader.Bunny_Count;
		GodMode = loader.GodMode;
		serpwasseen = loader.serpwasseen;
		sumowasseen = loader.sumowasseen;
		zillawasseen = loader.zillawasseen;

		System.arraycopy(loader.BossSpriteNum, 0, BossSpriteNum, 0, 3);
	}
}
