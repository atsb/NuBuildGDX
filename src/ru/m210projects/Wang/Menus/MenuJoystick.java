package ru.m210projects.Wang.Menus;

import ru.m210projects.Wang.Main;
import ru.m210projects.Build.Input.Keymap;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class MenuJoystick extends ru.m210projects.Build.Pattern.CommonMenus.MenuJoystick {

	public MenuJoystick(Main app) {
		super(app, 35, 50, 250, 10, 15, app.getFont(1), 14);
		
		mList.menupal = 19;
		
		mJoyKey.align = 0;
		mJoyKey.x = 35;
		
		mJoyDevices.listFont = app.getFont(0);
		mList.pal_left = mList.pal_right = 16;
		mText.font = mText2.font = app.getFont(1);
		mText.y -= 10;
		mText2.y -= 10;
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new WangTitle(text);
	}

	@Override
	public String keyNames(int keycode) {
		return Keymap.toString(keycode);
	}

}
