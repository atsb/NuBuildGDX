package ru.m210projects.Wang.Menus;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Wang.Main;

public class MenuCredits extends BuildMenu {

	public MenuCredits(Main app)
	{
		MenuPage mPages[] = new MenuPage[6];
		
		mPages[0] = new MenuPage(0, 0, 5262);
		mPages[1] = new MenuPage(0, 0, 5261);
		mPages[2] = new MenuPage(0, 0, 4979);
		
		mPages[3] = new MenuPage(0, 0, 5111);
		mPages[4] = new MenuPage(0, 0, 5118);
		mPages[5] = new MenuPage(0, 0, 5113);
		
		for(int i = 0; i < mPages.length; i++) {
			mPages[i].flags |= 10;
			addItem(mPages[i], i == 0);
		}
	}
}
