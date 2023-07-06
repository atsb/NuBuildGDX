package ru.m210projects.Wang.Type;

import static ru.m210projects.Wang.Digi.voc;
import static ru.m210projects.Wang.Factory.WangMenuHandler.USERCONTENT;
import static ru.m210projects.Wang.Game.currentGame;
import static ru.m210projects.Wang.Game.defGame;
import static ru.m210projects.Wang.Game.kMaxTiles;
import static ru.m210projects.Wang.JSector.MAXMIRRORS;
import static ru.m210projects.Wang.JSector.MIRROR;
import static ru.m210projects.Wang.JSector.MIRRORLABEL;
import static ru.m210projects.Wang.Main.appdef;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.game;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Cache1D.PackageType;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Group;
import ru.m210projects.Build.FileHandle.GroupResource;
import ru.m210projects.Build.FileHandle.PackedZipGroup;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.UserGroup;
import ru.m210projects.Build.FileHandle.ZipGroup;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Script.DefScript;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Wang.Menus.MenuUserContent;

public class ResourceHandler {

	private static UserGroup usergroup;
	public static boolean usecustomarts;

	public static void resetEpisodeResources() {
		BuildGdx.cache.clearDynamicResources();

		usergroup = null;
		currentGame = defGame;

		for (int i = 0; i < voc.length; i++)
			voc[i].data = null;

		if (!usecustomarts) {
			game.setDefs(game.baseDef);
			return;
		}

		System.err.println("Reset to default resources");
		for(int i = 0; i < kMaxTiles; i++)
			engine.getTile(i).clear();

		if (engine.loadpics() == 0)
			game.dassert("ART files not found " + new File(Path.Game.getPath() + "TILES###.ART").getAbsolutePath());

		game.setDefs(game.baseDef);

		InitSpecialTextures();

		usecustomarts = false;
	}

	public static void InitSpecialTextures() {
		Tile pic = engine.getTile(MIRROR);
		pic.setWidth(0).setHeight(0);

		for (int i = 0; i < MAXMIRRORS; i++)
			engine.getTile(i + MIRRORLABEL).clear();
	}

	public static void InitGroupResources(DefScript addonScript, List<GroupResource> list) {
		for (GroupResource res : list) {
			if (!res.getFullName().equals(appdef) && res.getExtension().equals("def")) {
				addonScript.loadScript(res.getFullName() + " script", res.getBytes());
				Console.Println("Found def-script. Loading " + res.getFullName());
			} else if (res.getExtension().equals("art")) {
				engine.loadpic(res.getFullName());
				usecustomarts = true;
			} else if (res.getFullName().equals("swvoxfil.txt")) {
				VoxelScript vox = new VoxelScript(res);
				vox.apply(addonScript);
				Console.Println("Found swvoxfil.txt. Loading... ");
			}
		}
	}

	public static GameInfo GetEpisode(String path, boolean isPackage)
	{
		DirectoryEntry resDir = null;
		if(!isPackage)
			resDir = BuildGdx.compat.checkDirectory(path);
		return ((MenuUserContent) game.menu.mMenus[USERCONTENT]).getAddon(isPackage ? path : null, resDir);
	}

	public static void prepareusergroup(Group group, boolean removable) throws Exception {
		if (group.type == PackageType.Zip) // Correct path in archive (files shouldn't be in a subfolder)
			((ZipGroup) group).removeFolders();
		else if (group.type == PackageType.PackedZip)
			((PackedZipGroup) group).removeFolders();

		List<GroupResource> list = group.getList();
		for (GroupResource res : list) {
			if (res.getExtension().equals("grp") || res.getExtension().equals("zip")) {
				BuildGdx.cache.add(res, removable);
			}

        }
	}

	private static void searchEpisodeResources(DirectoryEntry cache) {
		if (cache.getDirectories().size() > 0) {
			for (Iterator<DirectoryEntry> it = cache.getDirectories().values().iterator(); it.hasNext();) {
				DirectoryEntry dir = it.next();
				dir.InitDirectory(dir.getAbsolutePath());
				if (!dir.getName().equals("<userdir>"))
					searchEpisodeResources(dir);
			}
		}

		if (usergroup == null)
			usergroup = BuildGdx.cache.add("User", true);

		for (Iterator<FileEntry> it = cache.getFiles().values().iterator(); it.hasNext();) {
			FileEntry file = it.next();
			if (!file.getExtension().equals("zip") && !file.getExtension().equals("grp"))
				usergroup.add(file, -1);
		}
	}

	public static void checkEpisodeResources(GameInfo addon) {
		resetEpisodeResources();

		DefScript addonScript = new DefScript(game.baseDef, addon.getFile());
		if (addon.isPackage()) {
			FileEntry fil = addon.getFile();
			try {
				Group gr = BuildGdx.cache.add(fil.getPath());
				gr.setFlags(true, true);

				Resource res = gr.open(appdef); // load def scripts before delete folders
				if (res != null) {
					addonScript.loadScript(gr.name + " script", res.getBytes());
					res.close();
				}

				prepareusergroup(gr, true);
			} catch (Exception e) {
				game.GameCrash("Error found in " + fil.getPath() + "\r\n" + e.getMessage());
				return;
			}
		} else if (addon.getDirectory() != BuildGdx.compat.getDirectory(Path.Game)) {

            searchEpisodeResources(addon.getDirectory());

			if (addon.getDirectory() != null) {
				FileEntry def = addon.getDirectory().checkFile(appdef);
				if (def != null) {
					addonScript.loadScript(def);
				}
			}
		}

		// Loading user package files
		InitGroupResources(addonScript, BuildGdx.cache.getDynamicResources());

		currentGame = addon;
		game.setDefs(addonScript);
	}
}
