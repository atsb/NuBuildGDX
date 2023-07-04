package ru.m210projects.Tekwar.Menus;

import static ru.m210projects.Tekwar.Tekprep.*;

import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuRendererSettings;
import ru.m210projects.Build.Pattern.CommonMenus.MenuVideoMode;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Settings.BuildConfig;

public class TekMenuVideoMode extends MenuVideoMode {

	public TekMenuVideoMode(BuildGame app) {
		super(app, 46, 20, 240, app.getFont(0).getHeight() + 4, app.getFont(0), 15, 160, 321);
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		MenuTitle title = new MenuTitle(app.pEngine, text, app.getFont(0), 160, 15, -1);
		title.pal = 3;
		
		return title;
	}

	@Override
	public void setMode(BuildConfig cfg) {
		setup3dscreen(choosedMode.xdim, choosedMode.ydim);
	}
	
	@Override
	public MenuRendererSettings getRenSettingsMenu(BuildGame app, int posx, int posy, int width,
			int nHeight, BuildFont style) {
		MenuRendererSettings menu = new MenuRendererSettings(app, posx, posy, width, nHeight, style) {
			@Override
			public MenuTitle getTitle(BuildGame app, String text) {
				MenuTitle title = new MenuTitle(app.pEngine, text, app.getFont(0), 160, 15, -1);
				title.pal = 3;
				
				return title;
			}
		};
		return menu;
	}
}
