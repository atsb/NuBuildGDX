package ru.m210projects.Witchaven.Menu;

import static ru.m210projects.Build.Input.Keymap.MOUSE_LBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_MBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_RBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLDN;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLUP;

import java.util.Arrays;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuControls;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Witchaven.Config;
import ru.m210projects.Witchaven.Config.WhKeys;
import ru.m210projects.Witchaven.Main;

public class WHMenuControls extends MenuControls {

	public WHMenuControls(BuildGame app) {
		super(app, 50, 90, app.getFont(0).getHeight(), 8, app.getFont(0), 2, 3);
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new WHTitle(text, 90, 0);
	}

	@Override
	public BuildMenu getMouseMenu(BuildGame app) {
		return new WHMenuMouse((Main)app);
	}

	@Override
	public BuildMenu getJoystickMenu(BuildGame app) {
		return new WHMenuJoystick((Main)app);
	}

	@Override
	public BuildMenu getKeyboardMenu(BuildGame app) {
		return new WHMenuKeyboard(app);
	}

	@Override
	public void mResetDefault(BuildConfig cfg, MenuHandler menu) {
		for (int i = 0; i < cfg.keymap.length; i++)
			cfg.primarykeys[i] = Config.defkeys[i];

		Arrays.fill(cfg.secondkeys, 0);
		Arrays.fill(cfg.mousekeys, 0);

		cfg.mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
		cfg.mousekeys[WhKeys.Cast_spell.getNum()] = MOUSE_RBUTTON;
		cfg.mousekeys[GameKeys.Open.getNum()] = MOUSE_MBUTTON;
		cfg.mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
		cfg.mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		
		Console.setCaptureKey(cfg.primarykeys[GameKeys.Show_Console.getNum()], 0);
		Console.setCaptureKey(cfg.secondkeys[GameKeys.Show_Console.getNum()], 1);
		Console.setCaptureKey(cfg.mousekeys[GameKeys.Show_Console.getNum()], 2);
		
		menu.mMenuBack();
	}

	@Override
	public void mResetClassic(BuildConfig cfg, MenuHandler menu) {
		
		for (int i = 0; i < cfg.keymap.length; i++)
			cfg.primarykeys[i] = Config.defclassickeys[i];

		Arrays.fill(cfg.secondkeys, 0);
		Arrays.fill(cfg.mousekeys, 0);

		cfg.mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
		cfg.mousekeys[WhKeys.Cast_spell.getNum()] = MOUSE_RBUTTON;
		cfg.mousekeys[GameKeys.Open.getNum()] = MOUSE_MBUTTON;
		cfg.mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
		cfg.mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		
		Console.setCaptureKey(cfg.primarykeys[GameKeys.Show_Console.getNum()], 0);
		Console.setCaptureKey(cfg.secondkeys[GameKeys.Show_Console.getNum()], 1);
		Console.setCaptureKey(cfg.mousekeys[GameKeys.Show_Console.getNum()], 2);
		
		menu.mMenuBack();
	}
	
	@Override
	protected BuildMenu getResetDefaultMenu(final BuildGame app, BuildFont style, int posy, int pal)
	{
		BuildMenu menu = new BuildMenu();
		
		MenuText QuitQuestion = new MenuText("Do you really", style, 160, posy, 1);
		MenuText QuitQuestion2 = new MenuText("want to reset keys?", style, 160, posy +=10, 1);
		MenuVariants QuitVariants = new MenuVariants(app.pEngine, "[Y/N]", style, 160, posy += 20) {
			@Override
			public void positive(MenuHandler menu) {
				mResetDefault(app.pCfg, menu);
			}
		};

		menu.addItem(QuitQuestion, false);
		menu.addItem(QuitQuestion2, false);
		menu.addItem(QuitVariants, true);
		
		return menu;
	}
	
	@Override
	protected BuildMenu getResetClassicMenu(final BuildGame app, BuildFont style, int posy, int pal)
	{
		BuildMenu menu = new BuildMenu();
		
		MenuText QuitQuestion = new MenuText("Do you really want", style, 160, posy, 1);
		MenuText QuitQuestion2 = new MenuText("reset to classic?", style, 160, posy += 10, 1);
		MenuVariants QuitVariants = new MenuVariants(app.pEngine, "[Y/N]", style, 160, posy += 20) {
			@Override
			public void positive(MenuHandler menu) {
				mResetClassic(app.pCfg, menu);
			}
		};

		menu.addItem(QuitQuestion, false);
		menu.addItem(QuitQuestion2, false);
		menu.addItem(QuitVariants, true);
		
		return menu;
	}

}
