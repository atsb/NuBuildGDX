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

import static ru.m210projects.Build.Input.Keymap.MOUSE_BUTTON11;
import static ru.m210projects.Build.Input.Keymap.MOUSE_LBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_RBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLDN;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLUP;

import com.badlogic.gdx.Input.Keys;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuKeyboardList;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;

public abstract class MenuKeyboard extends BuildMenu {
	
	public MenuKeyboardList mList;
	public MenuText mText;
	public MenuText mText2;
	
	public abstract MenuTitle getTitle(BuildGame app, String text);
	
	public abstract String keyNames(int keycode);
	
	public MenuKeyboard(final BuildGame app, int posx, int posy, int width, int list_len, BuildFont style) {
		addItem(getTitle(app, "Configure keys"), false);
		final BuildConfig cfg = app.pCfg;
		
		MenuProc callback = new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				MenuKeyboardList item = (MenuKeyboardList) pItem;
				
				if (item.l_set == 0) {
					item.l_pressedId = null;
					item.l_set = 1;
				} else if (item.l_set == 1) {
					int[] mousekeys = cfg.mousekeys;

					switch (item.l_pressedId) {
					case UP:
						cfg.setKey(item.l_nFocus, Keys.UP);
						item.l_set = 0;
						break;
					case DW:
						cfg.setKey(item.l_nFocus, Keys.DOWN);
						item.l_set = 0;
						break;
					case LEFT:
						cfg.setKey(item.l_nFocus, Keys.LEFT);
						item.l_set = 0;
						break;
					case RIGHT:
						cfg.setKey(item.l_nFocus, Keys.RIGHT);
						item.l_set = 0;
						break;
					case ENTER:
						cfg.setKey(item.l_nFocus, Keys.ENTER);
						item.l_set = 0;
						break;
					case ESC:
						if (item.l_nFocus == GameKeys.Menu_Toggle.getNum())
							cfg.setKey(item.l_nFocus, Keys.ESCAPE);
						app.pInput.ctrlResetKeyStatus();
						item.l_set = 0;
						break;
					case SPACE:
						cfg.setKey(item.l_nFocus, Keys.SPACE);
						item.l_set = 0;
						break;
					case BSPACE:
						cfg.setKey(item.l_nFocus, Keys.BACKSPACE);
						item.l_set = 0;
						break;
					case DELETE:
						if (item.l_nFocus != GameKeys.Show_Console.getNum() && item.l_nFocus != GameKeys.Menu_Toggle.getNum())
							cfg.setKey(item.l_nFocus, Keys.FORWARD_DEL);
						item.l_set = 0;
						break;
					case LMB:
						mousekeys[item.l_nFocus] = MOUSE_LBUTTON;
						for (int i = 0; i < mousekeys.length; i++) {
							if (i != item.l_nFocus && MOUSE_LBUTTON == mousekeys[i]) {
								mousekeys[i] = 0;
							}
						}
						item.l_set = 0;
						break;
					case PGUP:
						cfg.setKey(item.l_nFocus, Keys.PAGE_UP);
						item.l_set = 0;
						break;
					case PGDW:
						cfg.setKey(item.l_nFocus, Keys.PAGE_DOWN);
						item.l_set = 0;
						break;
					case HOME:
						cfg.setKey(item.l_nFocus, Keys.HOME);
						item.l_set = 0;
						break;
					case END:
						cfg.setKey(item.l_nFocus, Keys.END);
						item.l_set = 0;
						break;
					case MWUP:
						mousekeys[item.l_nFocus] = MOUSE_WHELLUP;
						for (int i = 0; i < mousekeys.length; i++) {
							if (i != item.l_nFocus && MOUSE_WHELLUP == mousekeys[i]) {
								mousekeys[i] = 0;
							}
						}
						item.l_set = 0;
						break;
					case MWDW:
						mousekeys[item.l_nFocus] = MOUSE_WHELLDN;
						for (int i = 0; i < mousekeys.length; i++) {
							if (i != item.l_nFocus && MOUSE_WHELLDN == mousekeys[i]) {
								mousekeys[i] = 0;
							}
						}
						item.l_set = 0;
						break;
					case RMB:
						mousekeys[item.l_nFocus] = MOUSE_RBUTTON;
						for (int i = 0; i < mousekeys.length; i++) {
							if (i != item.l_nFocus && MOUSE_RBUTTON == mousekeys[i]) {
								mousekeys[i] = 0;
							}
						}
						item.l_set = 0;
						break;
					default:
						for (int kb = 0; kb < 256; kb++) {
							if (kb >= MOUSE_WHELLUP && kb <= MOUSE_BUTTON11) {
								if (app.pInput.ctrlKeyStatus(kb)) {
									mousekeys[item.l_nFocus] = kb;

									for (int i = 0; i < mousekeys.length; i++) {
										if (i != item.l_nFocus && kb == mousekeys[i])
											mousekeys[i] = 0;
									}

									item.l_set = 0;
								}
							} else if (app.pInput.ctrlKeyStatus(kb)) {
								cfg.setKey(item.l_nFocus, kb);
								item.l_set = 0;
							}
						}
						break;
					}
				}

				if (item.l_nFocus == GameKeys.Show_Console.getNum()) {
					app.pInput.ctrlResetKeyStatus();
					Console.setCaptureKey(cfg.primarykeys[item.l_nFocus], 0);
					Console.setCaptureKey(cfg.secondkeys[item.l_nFocus], 1);
					Console.setCaptureKey(cfg.mousekeys[item.l_nFocus], 2);
				}
			}
		};

		mList = new MenuKeyboardList(app.pSlider, app.pCfg, style, posx, posy, width, list_len, callback) {
			@Override
			public String getKeyName(int keycode) {
				return keyNames(keycode);
			}
		};
		
		posy += mList.mFontOffset() * list_len;

		mText = new MenuText("UP/DOWN = Select action", style, 160, posy += 2 * mList.mFontOffset(), 1);
		mText2 = new MenuText("Enter = modify  Delete = clear", style, 160, posy += mList.mFontOffset(), 1);
		
		addItem(mList, true);
		addItem(mText, false);
		addItem(mText2, false);
	}

}
