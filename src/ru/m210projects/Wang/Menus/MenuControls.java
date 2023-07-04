package ru.m210projects.Wang.Menus;

import static ru.m210projects.Build.Input.Keymap.MOUSE_LBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_MBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_RBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLDN;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLUP;

import java.util.Arrays;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Wang.Config;
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Config.SwKeys;

public class MenuControls extends BuildMenu {

	public MenuControls(final Main app)
	{
		addItem(new WangTitle("Controls Setup"), false);
		int pos = 39;

		MenuButton mMouseSet = new MenuButton("Mouse setup", app.getFont(1), 35, pos += 10, 320, 0, 0, new MenuMouse(app), -1, null, 0);
		MenuButton mJoySet = new MenuButton("Joystick setup", app.getFont(1), 35, pos += 10, 320, 0, 0, new MenuJoystick(app), -1, null, 0);
		MenuButton mKeySet = new MenuButton("Keyboard Setup", app.getFont(1), 35, pos += 10, 320, 0, 0, new MenuMenuKeyboard(app), -1, null, 0);
		MenuButton mKeyReset = new MenuButton("Reset to default", app.getFont(1), 35, pos += 15, 320, 0, 0, getResetDefaultMenu(app, app.getFont(1), 90, 16), -1, null,0);
		MenuButton mKeyClassic = new MenuButton("Reset to classic", app.getFont(1), 35, pos += 10, 320, 0, 0, getResetClassicMenu(app, app.getFont(1), 90, 16), -1, null,0);
		
		addItem(mMouseSet, true);
		addItem(mJoySet, false);
		addItem(mKeySet, false);
		addItem(mKeyReset, false);
		addItem(mKeyClassic, false);
	}
	
	private void mResetDefault(BuildConfig cfg, MenuHandler menu) {
		for (int i = 0; i < cfg.keymap.length; i++)
			cfg.primarykeys[i] = Config.defkeys[i];

		Arrays.fill(cfg.secondkeys, 0);
		Arrays.fill(cfg.mousekeys, 0);

		cfg.mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
		cfg.mousekeys[SwKeys.Special_Fire.getNum()] = MOUSE_RBUTTON;
		cfg.mousekeys[GameKeys.Open.getNum()] = MOUSE_MBUTTON;
		cfg.mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
		cfg.mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		
		Console.setCaptureKey(cfg.primarykeys[GameKeys.Show_Console.getNum()], 0);
		Console.setCaptureKey(cfg.secondkeys[GameKeys.Show_Console.getNum()], 1);
		Console.setCaptureKey(cfg.mousekeys[GameKeys.Show_Console.getNum()], 2);
		
		menu.mMenuBack();
	}

	private void mResetClassic(BuildConfig cfg, MenuHandler menu) {
		
		for (int i = 0; i < cfg.keymap.length; i++)
			cfg.primarykeys[i] = Config.defclassickeys[i];

		Arrays.fill(cfg.secondkeys, 0);
		Arrays.fill(cfg.mousekeys, 0);

		cfg.mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
		cfg.mousekeys[SwKeys.Special_Fire.getNum()] = MOUSE_RBUTTON;
		cfg.mousekeys[GameKeys.Open.getNum()] = MOUSE_MBUTTON;
		cfg.mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
		cfg.mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		
		Console.setCaptureKey(cfg.primarykeys[GameKeys.Show_Console.getNum()], 0);
		Console.setCaptureKey(cfg.secondkeys[GameKeys.Show_Console.getNum()], 1);
		Console.setCaptureKey(cfg.mousekeys[GameKeys.Show_Console.getNum()], 2);
		
		menu.mMenuBack();
	}
	
	protected BuildMenu getResetDefaultMenu(final BuildGame app, BuildFont style, int posy, int pal)
	{
		BuildMenu menu = new BuildMenu();
		
		MenuText QuitQuestion = new MenuText("Do you really want to reset keys?", style, 160, posy, 1);
		QuitQuestion.pal = pal;
		MenuVariants QuitVariants = new MenuVariants(app.pEngine, "[Y/N]", style, 160, posy += 2 * style.getHeight()) {
			@Override
			public void positive(MenuHandler menu) {
				mResetDefault(app.pCfg, menu);
			}
		};
		QuitVariants.pal = pal;
		
		menu.addItem(QuitQuestion, false);
		menu.addItem(QuitVariants, true);
		
		return menu;
	}
	
	protected BuildMenu getResetClassicMenu(final BuildGame app, BuildFont style, int posy, int pal)
	{
		BuildMenu menu = new BuildMenu();
		
		MenuText QuitQuestion = new MenuText("Do you really want reset to classic keys?", style, 160, posy, 1);
		QuitQuestion.pal = pal;
		MenuVariants QuitVariants = new MenuVariants(app.pEngine, "[Y/N]", style, 160, posy += 2 * style.getHeight()) {
			@Override
			public void positive(MenuHandler menu) {
				mResetClassic(app.pCfg, menu);
			}
		};
		QuitVariants.pal = pal;

		menu.addItem(QuitQuestion, false);
		menu.addItem(QuitVariants, true);
		
		return menu;
	}
}
