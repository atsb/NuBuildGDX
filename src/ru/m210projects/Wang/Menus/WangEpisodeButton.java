package ru.m210projects.Wang.Menus;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Type.GameInfo;

public class WangEpisodeButton extends MenuButton {
	
	private Main app;
	public GameInfo game;
	private String description;

	public WangEpisodeButton(Main app, int x, int y, GameInfo info, MenuProc newEpProc, int epnum)
	{
		super(info.episode[epnum].Title, app.getFont(2), x, y, 320, 0, 0, null, -1, newEpProc, epnum);
		
		this.app = app;
		this.description = info.episode[epnum].Description;
		game = info;
	}
	
	@Override
	public void draw(MenuHandler handler) {
		super.draw(handler);
		
		app.getFont(1).drawText(x, y + 17, description, 0, 4, TextAlign.Left, 2, false);
	}
}
