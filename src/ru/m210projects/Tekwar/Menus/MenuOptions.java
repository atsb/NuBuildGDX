package ru.m210projects.Tekwar.Menus;

import static ru.m210projects.Tekwar.Factory.TekMenuHandler.*;
import static ru.m210projects.Tekwar.Main.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Tekwar.Main;

public class MenuOptions extends BuildMenu {

	public MenuOptions(final Main app)
	{
		MenuTitle title = new MenuTitle(engine, "Options menu", app.getFont(0), 160, 15, -1);	

		int pos = 30;
		MenuButton mGame = new MenuButton("Game setup", app.getFont(0), 0, pos += 10, 320, 1, 2, new MenuGameSet(app), -1, null, 0);
		MenuButton mInterface = new MenuButton("Interface setup", app.getFont(0), 0, pos += 10, 320, 1, 2, new MenuInterfaceSet(app), -1, null, 0);
		MenuButton mVideo = new MenuButton("Video setup", app.getFont(0), 0, pos += 10, 320, 1, 2, new TekMenuVideoMode(app), -1, null, 0);
		MenuButton mAudio = new MenuButton("Audio setup", app.getFont(0), 0, pos += 10, 320, 1, 2, app.menu.mMenus[AUDIOSET] = new TekMenuAudio(app), -1, null, 0);
		MenuButton mControls = new MenuButton("Controls setup", app.getFont(0), 0, pos += 10, 320, 1, 2, new TekMenuControls(app), -1, null, 0);
		
		addItem(title, false);
		addItem(mGame, true);
		addItem(mInterface, false);
		addItem(mVideo, false);
		addItem(mAudio, false);
		addItem(mControls, false);
	}
}
