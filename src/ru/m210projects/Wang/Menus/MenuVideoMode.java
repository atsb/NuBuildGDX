package ru.m210projects.Wang.Menus;

import static ru.m210projects.Wang.Factory.WangMenuHandler.COLORCORR;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Names.*;
import static ru.m210projects.Wang.Palette.DoPlayerDivePalette;
import static ru.m210projects.Wang.Palette.DoPlayerNightVisionPalette;
import static ru.m210projects.Wang.Main.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuRendererSettings;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildSettings;
import ru.m210projects.Build.Types.BuildVariable;
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Factory.WangMenuHandler;

public class MenuVideoMode extends ru.m210projects.Build.Pattern.CommonMenus.MenuVideoMode {

	public MenuVideoMode(BuildGame app) {
		super(app, 35, 40, 240, 10, app.getFont(1), 10, 180, TITLE_PIC);

		for(int i = 0; i < m_nItems; i++) {
			if(m_pItems[i] instanceof MenuSwitch) {
				final MenuSwitch oldSwitch = (MenuSwitch) m_pItems[i];
				m_pItems[i] = new WangSwitch(oldSwitch.text, oldSwitch.font, oldSwitch.x, oldSwitch.y, oldSwitch.width, oldSwitch.value, oldSwitch.callback) {
					@Override
					public void open() {
						oldSwitch.open();
					}
				};
				m_pItems[i].m_pMenu = this;
			}
		}

		mApplyChanges.font = app.getFont(1);
		mApplyChanges.x = 35;
		mApplyChanges.y -= 5;
		mApplyChanges.align = 0;
		mSlot.backgroundPal = 4;
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new WangTitle(text);
	}

	@Override
	public void setMode(BuildConfig cfg) {
		if(!engine.setgamemode(cfg.fullscreen, choosedMode.xdim, choosedMode.ydim))
			cfg.fullscreen = 0;

		cfg.ScreenWidth = BuildGdx.graphics.getWidth();
		cfg.ScreenHeight = BuildGdx.graphics.getHeight();

		engine.setbrightness(gs.brightness, palette, true);
		DoPlayerDivePalette(Player[myconnectindex]);
	    DoPlayerNightVisionPalette(Player[myconnectindex]);
	}

	@Override
	public MenuRendererSettings getRenSettingsMenu(BuildGame app, int posx, int posy, int width,
			int nHeight, BuildFont style) {

		final WangMenuHandler menu = ((Main) app).menu;

		MenuRendererSettings rmenu = new MenuRendererSettings(app, posx, posy, width, nHeight, style) {
			@Override
			public MenuTitle getTitle(BuildGame app, String text) {
				return new WangTitle(text);
			}

			@Override
			public void mDraw(MenuHandler handler)
			{
				super.mDraw(handler);
				gs.brightness = BuildSettings.paletteGamma.get() << 2;
			}

			@Override
			protected MenuSwitch BuildSwitch(String text, final BuildVariable<Boolean> var) {
				MenuSwitch sw = new WangSwitch(text, style, 0, 0, width, false, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch ss = (MenuSwitch) pItem;
						var.set(ss.value);
					}
				}) {
					@Override
					public void draw(MenuHandler handler) {
						this.value = var.get();
						super.draw(handler);
					}
				};
				return sw;
			}
		};

		menu.mMenus[COLORCORR] = rmenu;
		return rmenu;
	}

}
