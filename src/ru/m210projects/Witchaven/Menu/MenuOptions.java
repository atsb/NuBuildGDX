package ru.m210projects.Witchaven.Menu;

import static ru.m210projects.Witchaven.Factory.WHMenuHandler.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Witchaven.Main;

public class MenuOptions extends BuildMenu {

	public MenuOptions(final Main app)
	{
		MenuTitle title = new WHTitle("Options menu", 90, 0);	

		int pos = 50;
		MenuButton mGame = new MenuButton("Game setup", app.getFont(0), 0, pos += 15, 320, 1, 2, new MenuGameSetup(app), -1, null, 0);
		MenuButton mInterface = new MenuButton("Interface setup", app.getFont(0), 0, pos += 15, 320, 1, 2, new MenuInterfaceSet(app), -1, null, 0);
		MenuButton mVideo = new MenuButton("Video setup", app.getFont(0), 0, pos += 15, 320, 1, 2, new WHMenuVideoMode(app), -1, null, 0);
		MenuButton mAudio = new MenuButton("Audio setup", app.getFont(0), 0, pos += 15, 320, 1, 2, app.menu.mMenus[AUDIOSET] = new WHMenuAudio(app), -1, null, 0);
		MenuButton mControls = new MenuButton("Controls setup", app.getFont(0), 0, pos += 15, 320, 1, 2, new WHMenuControls(app), -1, null, 0);
		
		addItem(title, false);
		addItem(mGame, true);
		addItem(mInterface, false);
		addItem(mVideo, false);
		addItem(mAudio, false);
		addItem(mControls, false);
	}
}
