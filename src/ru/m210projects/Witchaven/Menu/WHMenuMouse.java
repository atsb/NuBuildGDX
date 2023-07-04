package ru.m210projects.Witchaven.Menu;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuMouse;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Witchaven.Main;

public class WHMenuMouse  extends MenuMouse {

	public WHMenuMouse(Main app) {
		super(app, 22, 40, 280, app.getFont(0).getHeight() - 2, 5, app.getFont(0), 0);
		
		mMove.text = "Forw/Backw speed".toCharArray();
		if(!app.WH2) {
			mEnable.switchFont = app.getFont(1);
			mMenuEnab.switchFont = app.getFont(1);
			mSens.sliderNumbers = app.getFont(1);
			mTurn.sliderNumbers = app.getFont(1);
			mLook.sliderNumbers = app.getFont(1);
			mMove.sliderNumbers = app.getFont(1);
			mStrafe.sliderNumbers = app.getFont(1);
			mAiming.switchFont = app.getFont(1);
			mInvert.switchFont = app.getFont(1);
			
			mAxisUp.listFont = app.getFont(1);
			mAxisUp.y += 10;
			mAxisDown.listFont = app.getFont(1);
			mAxisDown.y += 12;
			mAxisLeft.listFont = app.getFont(1);
			mAxisLeft.y += 14;
			mAxisRight.listFont = app.getFont(1);
			mAxisRight.y += 16;	
		}
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new WHTitle(text, 90, 0);
	}
}
