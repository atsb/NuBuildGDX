package ru.m210projects.Tekwar.Menus;

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

public class TekMenuJoystick extends MenuJoystick {

	public TekMenuJoystick(BuildGame app) {
		super(app, 46, 25, 240, app.getFont(0).getHeight() + 4, app.getFont(0).getHeight() + 10, 
				app.getFont(0), 12);
		
		mList.pal_left = 5;
		mList.font = app.getFont(2); 
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		MenuTitle title = new MenuTitle(app.pEngine, text, app.getFont(0), 160, 15, -1);
		title.pal = 3;
		
		return title;
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
