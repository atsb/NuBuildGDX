package ru.m210projects.Wang.Menus;

import static ru.m210projects.Wang.Main.gGameScreen;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Type.GameInfo;

public class MenuDifficulty extends BuildMenu {

	private GameInfo game;
	private int currentEp;
	private FileEntry map;
	
	public MenuDifficulty(final Main app)
	{
		MenuProc newGameProc = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuButton button = (MenuButton) pItem;
				final int skill = button.specialOpt;
				BuildGdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						gGameScreen.newgame(false, map != null ? map : game, currentEp, 0, skill);
					}
				});
			}
		};

		int posy = 25;
		addItem(new WangTitle("Skill"), false);
		
		addItem(new MenuButton("Tiny grasshopper", app.getFont(2), 35, posy += 16, 320, 0, 0, null, -1, newGameProc, 0), false);
		addItem(new MenuButton("I Have No Fear", app.getFont(2), 35, posy += 16, 320, 0, 0, null, -1, newGameProc, 1), false);
		addItem(new MenuButton("Who Wants Wang", app.getFont(2), 35, posy += 16, 320, 0, 0, null, -1, newGameProc, 2), true);
		addItem(new MenuButton("No Pain, No Gain", app.getFont(2), 35, posy += 16, 320, 0, 0, null, -1, newGameProc, 3), false);
	}
	
	public void setEpisode(GameInfo game, int episodeNum)
	{
		this.map = null;
		this.game = game;
		this.currentEp = episodeNum; 
	}
	
	public void setMap(FileEntry map)
	{
		this.map = map;
		this.game = null;
		this.currentEp = -1; 
	}
	
}
