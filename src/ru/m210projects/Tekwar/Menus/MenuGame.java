package ru.m210projects.Tekwar.Menus;

import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Tekwar.Main.*;
import static ru.m210projects.Tekwar.Player.*;
import static ru.m210projects.Tekwar.Tekmap.allsymsdeposited;
import static ru.m210projects.Tekwar.Tekmap.civillianskilled;
import static ru.m210projects.Tekwar.Tekmap.gameover;
import static ru.m210projects.Tekwar.Tekmap.killedsonny;
import static ru.m210projects.Tekwar.Tekmap.mission;
import static ru.m210projects.Tekwar.Tekmap.mission_accomplished;
import static ru.m210projects.Tekwar.Tekmap.newgame;
import static ru.m210projects.Tekwar.Tekmap.numlives;
import static ru.m210projects.Tekwar.Tekmap.symbols;
import static ru.m210projects.Tekwar.Teksnd.startmusic;
import static ru.m210projects.Tekwar.Globals.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Tekwar.Main;

public class MenuGame extends BuildMenu {

	public MenuGame(final Main app) {
		MenuTitle title = new MenuTitle(app.pEngine, "Difficulty", app.getFont(0), 160, 15, -1);

		MenuProc newGameProc = new MenuProc() {
			public void run(MenuHandler handler, MenuItem pItem) {
				MenuButton button = (MenuButton) pItem;
				gDifficulty = button.specialOpt;

				allsymsdeposited = 0;
				for (int i = 0; i < 7; i++)
					symbols[i] = false;

				killedsonny = 0;
				civillianskilled = 0;
				mission_accomplished = 0;
				gameover = 0;
				numlives = 0;
				mission = 0;
				for (int i = 0; i < MAXPLAYERS; i++) {
					gPlayer[i].score = 0;
					gPlayer[i].numbombs = 0;
				}
				seconds = minutes = hours = 0;

				if (mUserFlag == UserFlag.UserMap) {
					startmusic((int) (7 * Math.random()));
					newgame(boardfilename, null);
				} else
					gameover = 2;
				app.menu.mClose();
			}
		};

		int pos = 35;
		MenuButton mEasy = new MenuButton("Easy", app.getFont(0), 0, pos += 10, 320, 1, 2, null, -1, newGameProc, 1);
		MenuButton mMedium = new MenuButton("Medium", app.getFont(0), 0, pos += 10, 320, 1, 2, null, -1, newGameProc,
				2);
		MenuButton mHard = new MenuButton("Hard", app.getFont(0), 0, pos += 10, 320, 1, 2, null, -1, newGameProc, 3);

		addItem(title, false);
		addItem(mEasy, true);
		addItem(mMedium, false);
		addItem(mHard, false);

	}

}
