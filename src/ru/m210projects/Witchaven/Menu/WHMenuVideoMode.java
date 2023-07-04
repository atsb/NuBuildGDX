package ru.m210projects.Witchaven.Menu;

import static ru.m210projects.Witchaven.WHScreen.*;
import static ru.m210projects.Witchaven.Factory.WHMenuHandler.COLORCORR;
import static ru.m210projects.Witchaven.Names.*;

import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuRendererSettings;
import ru.m210projects.Build.Pattern.CommonMenus.MenuVideoMode;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Witchaven.Main;
import ru.m210projects.Witchaven.Factory.WHMenuHandler;

public class WHMenuVideoMode extends MenuVideoMode {

	public WHMenuVideoMode(Main app) {
		super(app, 46, 40, 240, app.getFont(0).getHeight(), app.WH2 ? app.getFont(0) : app.getFont(1), 8, 200, MAINMENU);
		
		mResolution.font = app.getFont(0);
		mRenderer.font = app.getFont(0);
		mFullscreen.font = app.getFont(0);
		if(!app.WH2)
			mFullscreen.switchFont = app.getFont(1);
		mRenderSettings.font = app.getFont(0);
		mApplyChanges.font = app.getFont(0);
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new WHTitle(text, 90, 0);
	}

	@Override
	public void setMode(BuildConfig cfg) {
		setup3dscreen(choosedMode.xdim, choosedMode.ydim);
	}
	
	@Override
	public MenuRendererSettings getRenSettingsMenu(BuildGame app, int posx, int posy, int width,
			int nHeight, BuildFont style) {
		MenuRendererSettings menu = new MenuRendererSettings(app, posx, posy + 10, width, 9, style) {
			@Override
			public MenuTitle getTitle(BuildGame app, String text) {
				return new WHTitle(text, 90, 0);
			}
		};
		((WHMenuHandler) app.pMenu).mMenus[COLORCORR] = menu;
		return menu;
	}
}
