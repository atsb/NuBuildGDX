package ru.m210projects.Witchaven.Menu;

import static ru.m210projects.Witchaven.Main.*;
import static ru.m210projects.Witchaven.Names.*;
import static ru.m210projects.Witchaven.Globals.*;

import java.util.Arrays;
import java.util.Comparator;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.FileHandle.Group;
import ru.m210projects.Build.FileHandle.GroupResource;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuFileBrowser;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Pattern.Tools.NaturalComparator;
import ru.m210projects.Witchaven.Main;
import ru.m210projects.Witchaven.Types.EpisodeInfo;
import ru.m210projects.Witchaven.Types.MapInfo;

public class WHMenuUserContent extends BuildMenu {
	
	private static Comparator<GroupResource> rescomp = new Comparator<GroupResource>() {
		@Override
		public int compare(GroupResource o1, GroupResource o2) {
			String s1 = o1.getName();
			String s2 = o2.getName();

			return NaturalComparator.compare(s1, s2);
		}
	};

	public int skills;
	
	public WHMenuUserContent(final Main app)
	{
		MenuTitle title = new WHTitle("User content", 90, 0);

		int width = 240;
		MenuFileBrowser list = new MenuFileBrowser(app, app.WH2 ? app.getFont(0) : app.getFont(1), app.getFont(0), app.WH2 ? app.getFont(0) : app.getFont(1), 40, 55, width, 1, app.WH2 ? 8 : 10, MAINMENU) {
			@Override
			public void init() {
				registerExtension("map", 0, 0);
				registerExtension("zip", 5, 1);
				registerClass(EpisodeInfo.class, 5, 1);
			}
			
			@Override
			public void handleDirectory(DirectoryEntry dir) {
				EpisodeInfo ep = getEpisode(dir);
				if(ep != null) 
					addFile(ep, ep.Title);
			}

			@Override
			public void handleFile(FileEntry file) {
				if(file.getExtension().equals("map")) {
					EpisodeInfo ep = episodes.get(file.getParent().getAbsolutePath());
					if(ep != null) {
						for(int j = 0; j < ep.maps(); j++) {
							if(file.getPath().equals(ep.gMapInfo.get(j).path)) 
								return;
						}
					}
					addFile(file, file.getName());
				}
				else if(file.getExtension().equals("zip")) {
					EpisodeInfo ep = getEpisode(file);
					if(ep != null)
						addFile(ep, file.getName());
				}
			}

			@Override
			public void invoke(Object fil) {
				if(fil == null) return;
				
				if(fil instanceof FileEntry) { 
					if(((FileEntry)fil).getExtension().equals("map")) {
						launchMap((FileEntry)fil, skills);
						app.pMenu.mClose();
					}
				} else if(fil instanceof EpisodeInfo) 
					launchEpisode((EpisodeInfo) fil, skills);
			}
		};
		list.pathPal = 7;
		addItem(title, false);
		addItem(list, true);
	}
	
	public static void resetEpisodeResources(EpisodeInfo def)
	{
		BuildGdx.cache.clearDynamicResources();
		gCurrentEpisode = def;
	}

	public static boolean checkEpisodeResources(EpisodeInfo addon)
	{
		resetEpisodeResources(gOriginalEpisode);

		if(addon.packed)
		{
			FileEntry fil = BuildGdx.compat.checkFile(addon.path);
			if(fil != null) 
			{
				Group gr = BuildGdx.cache.add(fil.getPath());
				gr.setFlags(true, true);
			} else {
				game.GameMessage("Can't load addon: " + addon.path + "\r\n"); 
				return false;
			}
		} 
		gCurrentEpisode = addon;
		return true;
	}
	
	private void launchEpisode(EpisodeInfo ep, int skill)
	{
		if(ep == null) return;

		gGameScreen.newgame(ep, 1, skill);
	}
	
	private void launchMap(final FileEntry fil, int skill)
	{
		if(fil == null) return;
		
		gGameScreen.newgame(fil, 0, skill);
	}
	
	public static EpisodeInfo getEpisode(DirectoryEntry dir)
	{
		if(dir == null) return null;
		
		EpisodeInfo ep = episodes.get(dir.getAbsolutePath());
		if(ep == null) {
			FileEntry[] list = new FileEntry[dir.getFiles().size()];
			dir.getFiles().values().toArray(list);
			Arrays.sort(list);

			int nCount = 0;
			
			char[] mapname = "level1.map".toCharArray();
			if(dir != BuildGdx.compat.getDirectory(Path.Game)) {
				for(int i = 0; i < list.length; i++) {
					FileEntry file = list[i];
					String name = file.getName();
					if(file.getExtension().equals("map")) {
						if(String.copyValueOf(mapname).equals(name)) {
							if(ep == null) {
								ep = new EpisodeInfo(dir.getName());
								ep.path = dir.getRelativePath();
								episodes.put(dir.getAbsolutePath(), ep);
								Console.Println("Addon found: " + ep.Title);
							}
							ep.gMapInfo.add(new MapInfo(file.getPath(), FileUtils.getFullName(dir.getName()) + ": Map" + (nCount + 1)));
							mapname = ("level" + (1 + ++nCount) + ".map").toCharArray();
						} 
					}
				}
			}
		}
		
		return ep;
	}
	
	public static EpisodeInfo getEpisode(FileEntry pack)
	{
		if(pack == null) return null;
		
		EpisodeInfo ep = episodes.get(pack.getPath());
		if(ep == null) {
			Group gr = BuildGdx.cache.isGroup(pack.getPath());
			if(gr != null) {
				GroupResource[] list = new GroupResource[gr.numfiles];
				gr.getList().toArray(list);
				Arrays.sort(list, rescomp);
				
				int nCount = 0;
				char[] mapname = "level1.map".toCharArray();
				for(int i = 0; i < list.length; i++) {
					GroupResource file = list[i];
					String name = file.getFullName();
					if(file.getExtension().equals("map")) {
						if(String.copyValueOf(mapname).equals(name)) {
							if(ep == null) {
								ep = new EpisodeInfo(pack.getName());
								ep.path = pack.getPath();
								ep.packed = true;
								episodes.put(ep.path, ep);
								Console.Println("Addon found: " + ep.Title);
							}
							ep.gMapInfo.add(new MapInfo(name, pack.getName() + ": Map" + (nCount + 1)));
							mapname = ("level" + (1 + ++nCount) + ".map").toCharArray();
						} 
					}
				}
			}
		}
		return ep;
	}
}
