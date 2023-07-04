package ru.m210projects.Witchaven.Menu;

import static com.badlogic.gdx.Input.Keys.APOSTROPHE;
import static com.badlogic.gdx.Input.Keys.AT;
import static com.badlogic.gdx.Input.Keys.BACKSLASH;
import static com.badlogic.gdx.Input.Keys.COLON;
import static com.badlogic.gdx.Input.Keys.COMMA;
import static com.badlogic.gdx.Input.Keys.EQUALS;
import static com.badlogic.gdx.Input.Keys.LEFT_BRACKET;
import static com.badlogic.gdx.Input.Keys.MINUS;
import static com.badlogic.gdx.Input.Keys.PERIOD;
import static com.badlogic.gdx.Input.Keys.RIGHT_BRACKET;
import static com.badlogic.gdx.Input.Keys.SEMICOLON;
import static com.badlogic.gdx.Input.Keys.SLASH;

import ru.m210projects.Build.Input.Keymap;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuJoystick;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Witchaven.Main;

public class WHMenuJoystick extends MenuJoystick {

	public WHMenuJoystick(Main app) {
		super(app, 46, 50, 240, app.getFont(0).getHeight() - 2, 15, 
				app.getFont(0),
				8);
		
		if(!app.WH2) {
			mList.pal_left = 7;
			mList.font = app.getFont(1); 
			
			mJoyDevices.listFont = app.getFont(1); 
			mJoyTurn.listFont = app.getFont(1);
			mJoyLook.listFont = app.getFont(1);
			mJoyStrafe.listFont = app.getFont(1);
			mJoyMove.listFont = app.getFont(1);
			mDeadZone.sliderNumbers = app.getFont(1);
			mLookSpeed.sliderNumbers = app.getFont(1);
			mTurnSpeed.sliderNumbers = app.getFont(1);
			mInvert.switchFont = app.getFont(1);
		}
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new WHTitle(text, 90, 0);
	}

	@Override
	public String keyNames(int keycode) {
		if(keycode == 0)
			return "None";
		
		switch(keycode)
		{
			case COMMA:
				return "Comma";
			case PERIOD:
				return "Period";
			case MINUS:
				return "Minus";
			case EQUALS:
				return "Equals";
			case LEFT_BRACKET:
				return "L_bracket";
			case RIGHT_BRACKET:
				return "R_bracket";
			case BACKSLASH:
				return "Backslash";
			case SEMICOLON:
				return "Semicolon";
			case APOSTROPHE:
				return "Apostrophe";
			case SLASH:
				return "Slash";
			case AT:
				return "At";
			case COLON:
				return "Colon";
			default:
				return Keymap.toString(keycode);
		}
	}
}
