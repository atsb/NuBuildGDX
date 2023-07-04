// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D.Menus;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.*;
import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.Main.gDemoScreen;
import static ru.m210projects.Build.Strhandler.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuPicnum;
import ru.m210projects.Duke3D.Main;
import ru.m210projects.Duke3D.Factory.DukeMenuHandler;

public class MainMenu extends BuildMenu {

	public MainMenu(final Main app)
	{
		final DukeMenuHandler menu = app.menu;
		
		menu.mMenus[USERCONTENT] = new DUserContent(app);
		menu.mMenus[QUIT] = new QuitMenu(app);
		menu.mMenus[NEWGAME] = new NewGameMenu(app);
		menu.mMenus[DIFFICULTY] = new DifficultyMenu(app);
		menu.mMenus[NEWADDON] = new NewAddonMenu(app);
		menu.mMenus[MULTIPLAYER] = new DMenuMultiplayer(app);
		menu.mMenus[NETWORKGAME] = new NetworkMenu(app);
		menu.mMenus[HELP] = new HelpMenu(app);
		menu.mMenus[QUITTITLE] = new QTitleMenu(app);
		menu.mMenus[LOADGAME] = new DMenuLoad(app);
		menu.mMenus[SAVEGAME] = new DMenuSave(app);
		menu.mMenus[SOUNDSET] = new SoundMenu(app);
		menu.mMenus[OPTIONS] = new OptionsMenu(app);

		MenuPicnum bLogo = new MenuPicnum(app.pEngine, 160, 28, INGAMEDUKETHREEDEE, INGAMEDUKETHREEDEE, 65536) {
			@Override
			public void draw(MenuHandler handler) {
				draw.rotatesprite(x << 16, y << 16, 65536, 0, nTile, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);
				if (currentGame.getCON().PLUTOPAK)
					draw.rotatesprite((x + 100)<<16,(y + 8)<<16,65536,0,PLUTOPAKSPRITE+2,(sintable[(totalclock<<4)&2047]>>11),0,2+8,0,0,xdim-1,ydim-1);
			}
		};
		
		int posy = 40;

		MenuButton NewGame = new MenuButton("New Game", app.getFont(2), 0, posy += 16, 320, 1, 0, menu.mMenus[NEWGAME], -1, null, 0);
		MenuButton Multiplayer = new MenuButton("Multiplayer", app.getFont(2), 0, posy += 16, 320, 1, 0, null, -1, null, 0) {
			@Override
			public void draw(MenuHandler handler) {
				if (!app.isCurrentScreen(gDemoScreen) && (numplayers > 1 || mFakeMultiplayer))
					nextMenu = menu.mMenus[NETWORKGAME];
				else
					nextMenu = menu.mMenus[MULTIPLAYER];
				super.draw(handler);
			}
		};
		MenuButton Options = new MenuButton("Options", app.getFont(2), 0, posy += 16, 320, 1, 0, menu.mMenus[OPTIONS], -1, null, 0);
		MenuButton Load = new MenuButton("Load Game", app.getFont(2), 0, posy += 16, 320, 1, 0, menu.mMenus[LOADGAME], -1, null, 0) {
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(app.isCurrentScreen(gDemoScreen) || numplayers < 2 && !mFakeMultiplayer);
				super.draw(handler);
			}
		};

		MenuButton Help = new MenuButton("Help", app.getFont(2), 0, posy += 16, 320, 1, 0, menu.mMenus[HELP], -1, null, 0);
		MenuButton Credits = new MenuButton("Credits", app.getFont(2), 0, posy += 16, 320, 1, 0, new CreditsMenu(app), -1, null, 0);
		MenuButton Quit = new MenuButton("Quit game", app.getFont(2), 0, posy += 16, 320, 1, 0, menu.mMenus[QUIT], -1, null, 0) {
			@Override
			public void draw(MenuHandler handler) {
				if (!app.isCurrentScreen(gDemoScreen) && (numplayers > 1 || mFakeMultiplayer)) {
					text = toCharArray("Disconnect");
					nextMenu = menu.mMenus[QUITTITLE];
				} else {
					text = toCharArray("Quit game");
					nextMenu = menu.mMenus[QUIT];
				}
				super.draw(handler);
			}
		};
	
		addItem(bLogo, false);
		addItem(NewGame, true);
		addItem(Multiplayer, false);
		addItem(Options, false);
		addItem(Load, false);
		addItem(Help, false);
		addItem(Credits, false);
		addItem(Quit, false);
	}
}
