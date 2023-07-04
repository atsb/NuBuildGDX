//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Pattern.CommonMenus;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Strhandler.*;
import static ru.m210projects.Build.Render.VideoMode.strvmodes;
import static ru.m210projects.Build.Render.VideoMode.validmodes;

import java.util.ArrayList;
import java.util.List;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuList;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuResolutionList;
import ru.m210projects.Build.Pattern.MenuItems.MenuScroller;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;
import ru.m210projects.Build.Render.VideoMode;
import ru.m210projects.Build.Render.Renderer.RenderType;
import ru.m210projects.Build.Settings.BuildConfig;

public abstract class MenuVideoMode extends BuildMenu {

	protected MenuConteiner mResolution;
	protected MenuConteiner mRenderer;
	protected MenuButton mRenderSettings;
	protected MenuSwitch mFullscreen;
	protected MenuButton mApplyChanges;
	protected MenuResolutionList mSlot;
	protected MenuScroller slider;

	protected VideoMode choosedMode;
	protected VideoMode currentMode;
	protected boolean isFullscreen;
	protected RenderType currentRender;
	protected RenderType choosedRender;

	protected final BuildMenu mResList;
	protected final BuildMenu mRenSettingsMenu;


	public abstract MenuTitle getTitle(BuildGame app, String text);

	public abstract void setMode(BuildConfig cfg);

	public BuildMenu getResolutionListMenu(final MenuVideoMode parent, final BuildGame app, int posx, int posy, int width, int nListItems, BuildFont style, int nListBackground) {
		BuildMenu menu = new BuildMenu();

		menu.addItem(parent.getTitle(app, "Resolution"), false);

		List<char[]> list = new ArrayList<char[]>();
		if (strvmodes != null) {
			for (int i = 0; i < strvmodes.length; i++)
				list.add(strvmodes[i].toCharArray());
		}

		MenuProc callback = new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				final MenuList item = (MenuList) pItem;
				if (item.l_nFocus == -1)
					return;

				BuildGdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						currentMode = choosedMode = validmodes.get(item.l_nFocus);
						setMode(app.pCfg);
						parent.mLoadRes(app.pMenu, MenuOpt.Open);
						app.pMenu.mMenuBack();
					}
				});
			}
		};

		mSlot = new MenuResolutionList(app.pEngine, list, style, posx, posy, width, 1, null, callback, nListItems, nListBackground);

		slider = new MenuScroller(app.pSlider, mSlot, width + posx - app.pSlider.getScrollerWidth());

		menu.addItem(mSlot, true);
		menu.addItem(slider, false);

		return menu;
	}

	public abstract MenuRendererSettings getRenSettingsMenu(final BuildGame app, int posx, int posy, int width, int nHeight, BuildFont style);

	public MenuVideoMode(final BuildGame app, int posx, int posy, int width, int itemHeight, BuildFont style, int nListItems, int nListWidth, int nBackground) {

		addItem(getTitle(app, "Video mode"), false);

		final BuildConfig cfg = app.pCfg;
		MenuProc callback = new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				BuildGdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						cfg.fullscreen = isFullscreen ? 1 : 0;
						currentMode = choosedMode;
						if(currentRender != choosedRender) {
							app.pEngine.getrender().uninit();
							if(app.pEngine.setrendermode(app.getFactory().renderer(choosedRender))) {
								app.updateColorCorrection();
								Console.Println("The render has been changed to " + choosedRender.getName());
								cfg.renderType = app.pEngine.getrender().getType();
								currentRender = choosedRender;
							} else {
								Console.Println("The render hasn't been changed!", Console.OSDTEXT_RED);
								choosedRender = currentRender;
								app.pEngine.setrendermode(app.getFactory().renderer(choosedRender));
								mRenderer.open();
							}
						}
						setMode(cfg);  //init new renderer is doing here
					}
				});
				app.pInput.ctrlResetInput();
			}
		};

		mResList = getResolutionListMenu(this, app, posx + (width - nListWidth) / 2, posy + 2 * style.getHeight(), nListWidth, nListItems, style, nBackground);

		mRenSettingsMenu = getRenSettingsMenu(app, posx, posy, width, itemHeight, style);

		mResolution = new MenuConteiner("Resolution", style, posx,
				posy += itemHeight, width, strvmodes, 0, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuConteiner item = (MenuConteiner) pItem;
						choosedMode = validmodes.get(item.num);
					}
				}) {

			@Override
			public boolean callback(MenuHandler handler, MenuOpt opt) {
				switch(opt)
				{
				case LEFT:
				case MWDW:
					if(list == null) return false;
					if(num > 0) num--;
					else num = 0;
					if(callback != null)
						callback.run(handler, this);
					return false;
				case RIGHT:
				case MWUP:
					if(list == null) return false;
					if(num < list.length - 1) num++;
					else num = list.length - 1;
					if(callback != null)
						callback.run(handler, this);
					return false;
				case ENTER:
				case LMB:
					handler.mOpen(mResList, -1);
					return false;
				default:
					return m_pMenu.mNavigation(opt);
				}
			}

			@Override
			public void open() {
				num = -1;
				for (int m = 0; m < validmodes.size(); m++) {
					if ((validmodes.get(m).xdim == xdim)
							&& (validmodes.get(m).ydim == ydim)) {
						num = m;
						break;
					}
				}

				if (num != -1) {
					currentMode = validmodes.get(num);
					choosedMode = currentMode;
				} else {
					currentMode = new VideoMode(BuildGdx.graphics.getDisplayMode());
				}
			}

			@Override
			public void draw(MenuHandler handler) {
				int px = x, py = y;

				char[] key;
				if (num != -1 && list != null)
					key = list[num];
				else
					key = toCharArray(cfg.ScreenWidth + " x " + cfg.ScreenHeight + " *");

				int pal = handler.getPal(font, this);
				int shade = handler.getShade(this);
				font.drawText(px, py, text, shade, pal, TextAlign.Left, 2, fontShadow);

				if(key == null) return;

				listFont.drawText(x + width - 1 - listFont.getWidth(key), py, key, shade, handler.getPal(listFont, this), TextAlign.Left, 2, listShadow);

				handler.mPostDraw(this);
			}
		};

		MenuProc renderCallback = new MenuProc() {
			@Override
			public void run(MenuHandler handler, final MenuItem pItem) {
				BuildGdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						MenuConteiner item = (MenuConteiner) pItem;
						switch(item.num) {
							case 0: choosedRender = RenderType.Software; break;
							case 1: choosedRender = RenderType.Polymost; break;
							case 2: choosedRender = RenderType.PolyGDX; break;
						}
					}
				});
			}
		};

		String[] renderers = new String[] {
			RenderType.Software.getName(),
			RenderType.Polymost.getName(),
			RenderType.PolyGDX.getName()
		};
		mRenderer = new MenuConteiner("Renderer", style, posx, posy += itemHeight, width, renderers, 0, renderCallback) {
			@Override
			public void open() {
				choosedRender = currentRender = app.pEngine.getrender().getType();
				switch(currentRender) {
					case Software: num = 0; break;
					case Polymost: num = 1; break;
					case PolyGDX: num = 2; break;
				}
			}
		};

		mFullscreen = new MenuSwitch("Fullscreen", style, posx,
				posy += itemHeight, width, cfg.fullscreen == 1, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						isFullscreen = sw.value;
					}
				}, null, null) {
			@Override
			public void open() {
				value = isFullscreen = (cfg.fullscreen == 1);
			}
		};

		mRenderSettings = new MenuButton("Renderer settings", style, posx, posy += itemHeight, width, 0, 0, mRenSettingsMenu, -1, null, 0);

		mApplyChanges = new MenuButton("Apply changes", style, 0, posy += 2 * itemHeight, 320, 1, 0, null, -1, callback, 0) {
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(choosedMode != null && (choosedMode != currentMode || isFullscreen != (cfg.fullscreen == 1) || currentRender != choosedRender));
				super.draw(handler);
			}

			@Override
			public void mCheckEnableItem(boolean nEnable) {
				if (nEnable)
					flags = 3 | 4;
				else flags = 3;
			}
		};

		addItem(mResolution, true);
		addItem(mRenderer, false);
		addItem(mFullscreen, false);
		addItem(mRenderSettings, false);
		addItem(mApplyChanges, false);
	}
}
