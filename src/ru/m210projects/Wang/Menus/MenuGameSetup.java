package ru.m210projects.Wang.Menus;

import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Wang.Gameutils.PF_AUTO_AIM;
import static ru.m210projects.Wang.Main.*;
import static ru.m210projects.Wang.Text.PutStringInfo;
import static ru.m210projects.Wang.Game.*;

import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;
import ru.m210projects.Wang.Main;

public class MenuGameSetup extends BuildMenu {

	public MenuGameSetup(final Main app) {
		WangTitle mTitle = new WangTitle("Game Setup");
		int pos = 40;

		WangSwitch sSlopeTilt = new WangSwitch("Screen tilting", app.getFont(1), 35, pos += 10, 240, gs.SlopeTilting,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						gs.SlopeTilting = sw.value;
					}
				});

		WangSwitch sAutoAim = new WangSwitch("AutoAim", app.getFont(1), 35, pos += 10, 240, gs.AutoAim, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				gs.AutoAim = sw.value;

				if (game.nNetMode == NetMode.Single && game.getScreen() != gDemoScreen) {
					if (gs.AutoAim)
						Player[myconnectindex].Flags |= (PF_AUTO_AIM);
					else
						Player[myconnectindex].Flags &= ~(PF_AUTO_AIM);
				}
			}
		}) {
			@Override
			public void draw(MenuHandler handler) {
				super.draw(handler);
				value = gs.AutoAim;
			}
		};
		
		WangSwitch sWeaponAutoSwitch = new WangSwitch("Weapon Auto Switch", app.getFont(1), 35, pos += 10, 240, gs.WeaponAutoSwitch, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				gs.WeaponAutoSwitch = sw.value;
			}
		}) {
			@Override
			public void draw(MenuHandler handler) {
				super.draw(handler);
				value = gs.WeaponAutoSwitch;
			}
		};
		
		MenuSwitch sUseDarts = new WangSwitch("Use Darts", app.getFont(1), 35, pos += 10, 240,
				gs.UseDarts, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						gs.UseDarts = sw.value;
					}
				});
		
		MenuSwitch sDisableHornets = new WangSwitch("Disable Hornets", app.getFont(1), 35, pos += 10, 240,
				gs.DisableHornets, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						gs.DisableHornets = sw.value;
						if(game.getScreen() instanceof GameAdapter) {
							PutStringInfo(Player[myconnectindex], "You have to restart the level");
						}
					}
				});

		MenuSwitch sStartup = new WangSwitch("Startup window", app.getFont(1), 35, pos += 10, 240, gs.startup,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						gs.startup = sw.value;
					}
				});

		MenuSwitch sCheckVersion = new WangSwitch("Check for updates", app.getFont(1), 35, pos += 10, 240,
				gs.checkVersion, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						gs.checkVersion = sw.value;
					}
				});

		MenuConteiner mPlayingDemo = new MenuConteiner("Demos playback", app.getFont(1), 35, pos += 10, 240, null, 0,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuConteiner item = (MenuConteiner) pItem;
						gs.gDemoSeq = item.num;
					}
				}) {
			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[3][];
					this.list[0] = "Off".toCharArray();
					this.list[1] = "In order".toCharArray();
					this.list[2] = "Randomly".toCharArray();
				}
				num = gs.gDemoSeq;
			}
		};

		MenuSwitch sRecord = new WangSwitch("Record demo", app.getFont(1), 35, pos += 10, 240, isDemoRecording,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						isDemoRecording = sw.value;
					}
				}) {

			@Override
			public void open() {
				value = isDemoRecording;
				mCheckEnableItem(!app.isCurrentScreen(gGameScreen));
			}
		};

		addItem(mTitle, false);
		addItem(sSlopeTilt, true);
		addItem(sAutoAim, false);
		addItem(sWeaponAutoSwitch, false);
		addItem(sUseDarts, false);
		addItem(sDisableHornets, false);
		addItem(sStartup, false);
		addItem(sCheckVersion, false);
//		addItem(mPlayingDemo, false);
//		addItem(sRecord, false);
	}
}
