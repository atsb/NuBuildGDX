package ru.m210projects.Wang.Menus;

import static ru.m210projects.Wang.Digi.DIGI_SWORDSWOOSH;
import static ru.m210projects.Wang.Factory.WangMenuHandler.DIFFICULTY;
import static ru.m210projects.Wang.Factory.WangMenuHandler.NETWORKGAME;
import static ru.m210projects.Wang.Factory.WangMenuHandler.NEWADDON;
import static ru.m210projects.Wang.Game.*;
import static ru.m210projects.Wang.Game.pOriginalEp;
import static ru.m210projects.Wang.Game.pSharewareEp;
import static ru.m210projects.Wang.Game.pTwinDragonEp;
import static ru.m210projects.Wang.Game.pWantonEp;
import static ru.m210projects.Wang.Gameutils.MAX_LEVELS;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.v3df_dontpan;

import java.util.HashMap;
import java.util.List;

import ru.m210projects.Build.CRC32;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.FileHandle.Group;
import ru.m210projects.Build.FileHandle.GroupResource;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuFileBrowser;
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Menus.Network.MenuNetwork;
import ru.m210projects.Wang.Type.CustomScript;
import ru.m210projects.Wang.Type.EpisodeInfo;
import ru.m210projects.Wang.Type.GameInfo;
import ru.m210projects.Wang.Type.LevelInfo;

public class MenuUserContent extends BuildMenu {

	public boolean showmain;
	private Main app;
	private MenuFileBrowser list;

	private HashMap<Long, EpisodeInfo> officialAddons = new HashMap<Long, EpisodeInfo>() {
		private static final long serialVersionUID = 1L;
		{
			put(1488316004L, pWantonEp);
			put(1830650101L, pTwinDragonEp);
		}
	};

	private HashMap<String, LevelInfo> originalMaps = new HashMap<String, LevelInfo>() {
		private static final long serialVersionUID = 1L;
		{
			put("$bullet.map", pSharewareEp.gMapInfo[1]);
			put("$dozer.map", pSharewareEp.gMapInfo[2]);
			put("$shrine.map", pSharewareEp.gMapInfo[3]);
			put("$woods.map", pSharewareEp.gMapInfo[4]);

			put("$whirl.map", pOriginalEp.gMapInfo[0]);
			put("$tank.map", pOriginalEp.gMapInfo[1]);
			put("$boat.map", pOriginalEp.gMapInfo[2]);
			put("$garden.map", pOriginalEp.gMapInfo[3]);
			put("$outpost.map", pOriginalEp.gMapInfo[4]);
			put("$hidtemp.map", pOriginalEp.gMapInfo[5]);
			put("$plax1.map", pOriginalEp.gMapInfo[6]);
			put("$bath.map", pOriginalEp.gMapInfo[7]);
			put("$airport.map", pOriginalEp.gMapInfo[8]);
			put("$refiner.map", pOriginalEp.gMapInfo[9]);
			put("$newmine.map", pOriginalEp.gMapInfo[10]);
			put("$subbase.map", pOriginalEp.gMapInfo[11]);
			put("$rock.map", pOriginalEp.gMapInfo[12]);
			put("$yamato.map", pOriginalEp.gMapInfo[13]);
			put("$seabase.map", pOriginalEp.gMapInfo[14]);
			put("$volcano.map", pOriginalEp.gMapInfo[15]);
			// Secret levels:
			put("$shore.map", pOriginalEp.gMapInfo[16]);
			put("$auto.map", pOriginalEp.gMapInfo[17]);
		}
	};

	public MenuUserContent(final Main app) {
		this.app = app;
		addItem(new WangTitle("User content"), false);

		int width = 240;
		list = new MenuFileBrowser(app, app.getFont(0), app.getFont(1), app.getFont(0), 40, 40, width, 1, 14, 2324) {
			@Override
			protected void drawHeader(int x1, int x2, int y) {
				/* directories */ app.getFont(1).drawText(x1, y, dirs, 65536, -32, topPal, TextAlign.Left, 2,
						fontShadow);
				/* files */ app.getFont(1).drawText(x2, y, ffs, 65536, -32, topPal, TextAlign.Left, 2, fontShadow);
			}

			@Override
			public void init() {
				registerExtension("map", 0, 0);
				registerExtension("grp", 19, 1);
				registerExtension("zip", 19, 1);
				registerClass(GameInfo.class, 19, 2);
			}

			@Override
			public void handleDirectory(DirectoryEntry dir) {
				if (app.menu.gShowMenu)
					PlaySound(DIGI_SWORDSWOOSH, null, v3df_dontpan);
				buildAddon(this, dir);
			}

			@Override
			public void handleFile(FileEntry fil) {
				GameInfo addon = episodes.get(fil.getParent().getAbsolutePath());
				if (addon != null) { // This file has added to addon
					if (!fil.getExtension().equals("map"))
						return;

					LevelInfo map = originalMaps.get(fil.getName());
					if (map != null)
						return;
				}

				if (fil.getExtension().equals("map"))
					addFile(fil, fil.getName());
				else if (fil.getExtension().equals("grp") || fil.getExtension().equals("zip"))
					buildPackage(this, fil);
			}

			@Override
			public void invoke(Object obj) {
				if (obj == null)
					return;

				if (obj instanceof FileEntry) {
					FileEntry fil = (FileEntry) obj;
					if (fil.getExtension().equals("map"))
						launchMap(fil);
					else if (fil.getExtension().equals("grp") || fil.getExtension().equals("zip")) {
						launchEpisode(episodes.get(getFileName()));
					}
				} else if (obj instanceof GameInfo)
					launchEpisode((GameInfo) obj);
			}
		};

		list.topPal = 20;
		list.pathPal = 20;
		list.listPal = 4;
		addItem(list, true);
	}

	private void launchMap(FileEntry file) {
		if (file == null)
			return;

		if(mFromNetworkMenu())
		{
			MenuNetwork network = (MenuNetwork) app.menu.mMenus[NETWORKGAME];
			network.setMap(file);
			app.menu.mMenuBack();
			return;
		}

		MenuDifficulty next = (MenuDifficulty) app.menu.mMenus[DIFFICULTY];
		next.setMap(file);
		app.menu.mOpen(next, -1);
	}

	private void launchEpisode(GameInfo game) {
		if (game == null)
			return;

		if(mFromNetworkMenu())
		{
			MenuNetwork network = (MenuNetwork) app.menu.mMenus[NETWORKGAME];
			network.setEpisode(game);
			app.menu.mMenuBack();
			return;
		}

		MenuNewAddon next = (MenuNewAddon) app.menu.mMenus[NEWADDON];
		next.setEpisode(game);
		app.menu.mOpen(next, -1);
	}

	public void setShowMain(boolean show) {
		this.showmain = show;
		if (list.getDirectory() == BuildGdx.compat.getDirectory(Path.Game))
			list.refreshList();
	}

	private CustomScript checkScript(DirectoryEntry dir, Group res, String script) {
		FileEntry fil;
		if (dir != null && (fil = dir.checkFile(script)) != null)
			return new CustomScript(BuildGdx.compat.getBytes(fil));

		if (res != null) {
			GroupResource gres = res.open(script);
			if (gres != null) {
				CustomScript scr = new CustomScript(gres.getBytes());
				gres.close();
				return scr;
			}
		}
		return null;
	}

	private EpisodeInfo checkOfficialAddon(Group res) {
		if (res == null || (!FileUtils.getFullName(res.name).equals("wt.grp")
				&& !FileUtils.getFullName(res.name).equals("td.grp")
				&& !FileUtils.getFullName(res.name).equals("wantdest.grp")
				&& !FileUtils.getFullName(res.name).equals("twindrag.grp")))
			return null;

		EpisodeInfo inf = null;
		GroupResource gres = res.open("zfcin.anm");
		if (gres != null) {
			inf = officialAddons.get(CRC32.getChecksum(gres));
			gres.close();
		}

		return inf;
	}

	public GameInfo getAddon(String path, DirectoryEntry fromDir) {
		path = FileUtils.getCorrectPath(path);
		if(path == null && fromDir != null)
			path = fromDir.getRelativePath();

		Group fromGroup = null;
		GameInfo addon = episodes.get(path);
		if (addon == null) {
			EpisodeInfo inf = null;
			if (fromDir != null) {
				FileEntry fil; // checking for an official addon
				if ((fil = fromDir.checkFile("zfcin.anm")) != null) {
					inf = officialAddons.get(CRC32.getChecksum(fil));
					if (inf != null) {
						addon = new GameInfo(inf.Title, fromDir, null, inf);
						episodes.put(path, addon);
						Console.Print("Found official addon: " + inf.Title);
						return addon;
					}
				}
			} else {
				fromGroup = BuildGdx.cache.isGroup(path);
				inf = checkOfficialAddon(fromGroup);
				if (inf != null) {
					addon = new GameInfo(inf.Title, fromGroup, null, inf);
					episodes.put(path, addon);
					Console.Print("Found official addon: " + inf.Title);
					return addon;
				}
			}

			if (fromDir == null && fromGroup == null)
				return null;

			CustomScript scr = null;
			List<? extends Resource> list = (fromDir != null) ? fromDir.getList() : fromGroup.getList();
			for (Resource res : list) {
				if (res.getFullName().endsWith("custom.txt"))
					scr = checkScript(fromDir, fromGroup, res.getFullName());
			}

			EpisodeInfo[] NoNameEp = null;
			if (fromDir != null || scr == null) {
				boolean hasFirstEpisode = false;
				boolean hasSecondEpisode = false;
				if (fromDir != null) {
					hasFirstEpisode = fromDir.checkFile("$bullet.map") != null;
					hasSecondEpisode = fromDir.checkFile("$whirl.map") != null;
				} else {
					hasFirstEpisode = fromGroup.contains("$bullet.map");
					hasSecondEpisode = fromGroup.contains("$whirl.map");
				}

				if (hasFirstEpisode || hasSecondEpisode) {
					NoNameEp = new EpisodeInfo[hasSecondEpisode ? 2 : 1];
					for (int i = hasFirstEpisode ? 0 : 4; i < MAX_LEVELS; i++) {
						if (i > 3 && !hasSecondEpisode)
							break;
						int epnum = i < 4 ? 0 : 1;
						if (NoNameEp[epnum] == null) {
							NoNameEp[epnum] = new EpisodeInfo();
							NoNameEp[epnum].Title = "Episode " + epnum;
							NoNameEp[epnum].Description = "No description";
						}

						EpisodeInfo ep = NoNameEp[epnum];
						LevelInfo map = epnum == 0 ? pSharewareEp.gMapInfo[i] : pOriginalEp.gMapInfo[i - 4];
						if (map != null) {
							String mapname = map.LevelName;
							if ((fromDir != null && fromDir.checkFile(mapname) != null)
									|| (fromGroup != null && fromGroup.contains(mapname))) {
								ep.gMapInfo[epnum == 0 ? i : i - 4] = new LevelInfo(map.LevelName, map.SongName,
										"Map" + (i + 1), map.BestTime, map.ParTime);
								ep.nMaps++;
							}
						}
					}
				}
			}

			if (scr != null || NoNameEp != null) {
				if (fromDir != null) {
					if (NoNameEp == null)
						return null; // the directory doesn't have maps
					addon = new GameInfo(fromDir.getName(), fromDir);
				} else {
					addon = new GameInfo(FileUtils.getFullName(fromGroup.name), fromGroup);
				}

				if (scr != null)
					scr.apply(addon);
				else if (NoNameEp != null) {
					addon.episode = NoNameEp;
				}
				episodes.put(path, addon);
				Console.Println("Found addon: " + addon.Title + " ( "
						+ ((fromDir != null) ? fromDir.getRelativePath() : fromGroup.name) + " )");
			}
		}

		if (fromGroup != null)
			fromGroup.dispose();

		return addon;
	}

	private void buildPackage(MenuFileBrowser blist, FileEntry file) {
		if (!showmain && file.getParent() == BuildGdx.compat.getDirectory(Path.Game) && file.getName().equals(game.mainGrp))
			return;

		String path = file.getPath();
		GameInfo addon = getAddon(path, null);

		if (addon != null)
			blist.addFile(file, path);
	}

	private void buildAddon(MenuFileBrowser blist, DirectoryEntry dir) {
		if (dir == BuildGdx.compat.getDirectory(Path.Game))
			return;

		GameInfo addon = getAddon(null, dir);
		if (addon != null)
			blist.addFile(addon, "Addon:" + addon.Title);
	}
	
	public boolean mFromNetworkMenu() {
		return app.menu.getLastMenu() == app.menu.mMenus[NETWORKGAME];
	}
}
