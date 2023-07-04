package ru.m210projects.Tekwar.Menus;

import static ru.m210projects.Tekwar.Factory.TekMenuHandler.GAME;
import static ru.m210projects.Tekwar.Main.boardfilename;
import static ru.m210projects.Tekwar.Main.game;
import static ru.m210projects.Tekwar.Main.mUserFlag;

import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuFileBrowser;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Tekwar.Main;
import ru.m210projects.Tekwar.Main.UserFlag;

public class MenuUserContent extends BuildMenu {
	
	public MenuUserContent(final Main app)
	{
		MenuTitle title = new MenuTitle(app.pEngine, "User content", app.getFont(0), 160, 15, -1);
		title.pal = 3;

		int width = 240;
		MenuFileBrowser list = new MenuFileBrowser(app, app.getFont(2), app.getFont(0), app.getFont(2), 40, 30, width, 1, 15, 321) {
			@Override
			public void init() {
				registerExtension("map", 0, 0);
			}
			
			@Override
			public void handleDirectory(DirectoryEntry dir) {
				/* nothing */
			}

			@Override
			public void handleFile(FileEntry file) {
				if(file.getExtension().equals("map"))
					addFile(file, file.getName());
			}

			@Override
			public void invoke(Object obj) {
				if(obj == null) return;
				
				if(obj instanceof FileEntry)  { 
					FileEntry fil = (FileEntry) obj;
					if((fil.getExtension().equals("map"))) {
						boardfilename = fil.getPath();
						mUserFlag = UserFlag.UserMap;
						app.pMenu.mOpen(game.menu.mMenus[GAME], -1);
					}
				}
			}
		};
		
		list.topPal = list.pathPal = 5;
		list.back = "back";
		addItem(title, false);
		addItem(list, true);
	}
}
