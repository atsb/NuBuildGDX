// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP.Menus;

import static ru.m210projects.LSP.Factory.LSPMenuHandler.AUDIOSET;
import static ru.m210projects.LSP.Main.game;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.LSP.Main;
import ru.m210projects.LSP.Factory.LSPMenuHandler;

public class MenuOptions extends BuildMenu {

	public MenuOptions(final Main app)
	{
		final LSPMenuHandler menu = (LSPMenuHandler) app.menu;

		int posy = 45;
		MenuButton mGameSet = new MenuButton("Game Setup", game.getFont(2), 0, posy += 20, 320, 1, 0, new MenuGameSet(app), -1, null, 0);
		
		MenuButton mInterface = new MenuButton("Interface Setup", game.getFont(2),0, posy += 20, 320, 1, 0, new MenuInterfaceSet(app), -1, null, 0);

		MenuButton mVideo = new MenuButton("Video Setup", game.getFont(2),0, posy += 20, 320, 1, 0, new LSPMenuVideoMode(app), -1, null, 0);

		MenuButton mAudio = new MenuButton("Audio Setup", game.getFont(2),0, posy += 20, 320, 1, 0, menu.mMenus[AUDIOSET], -1, null, 0);
		
		MenuButton mControls = new MenuButton("Controls Setup", game.getFont(2),0, posy += 20, 320, 1, 0, new MenuControls(app), -1, null, 0);

		addItem(mGameSet, true);
		addItem(mInterface, false);
		addItem(mVideo, false);
		addItem(mAudio, false);	
		addItem(mControls, false);
	}
}
