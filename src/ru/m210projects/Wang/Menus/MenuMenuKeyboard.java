package ru.m210projects.Wang.Menus;

import ru.m210projects.Wang.Main;

import static com.badlogic.gdx.Input.Keys.BACKSPACE;

import ru.m210projects.Build.Input.Keymap;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuKeyboard;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class MenuMenuKeyboard extends MenuKeyboard {

	public MenuMenuKeyboard(Main app) {
		super(app, 35, 40, 250, 14, app.getFont(1));
		
		mList.pal_left = 20;
		mList.pal_right = 16;

		mText.y -= 10;
		mText.pal = 16;
		mText2.y -= 10;
		mText2.pal = 16;
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new WangTitle(text);
	}

	@Override
	public String keyNames(int keycode) {
		
		if(keycode == BACKSPACE)
			return "BkSpace";
		
		return Keymap.toString(keycode);
	}

}
