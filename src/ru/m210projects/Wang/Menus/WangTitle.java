package ru.m210projects.Wang.Menus;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Wang.Main.*;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class WangTitle extends MenuTitle {

	public WangTitle(Object text) {
		super(engine, text, game.getFont(2), 160, 10, 2427);
	}

	@Override
	public void draw(MenuHandler handler) {
		if ( text != null )
		{
			draw.rotatesprite(x << 16, y << 16, 65536, 0, nTile, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);
			font.drawText(x, y - (draw.getTile(2427).getHeight() - font.getHeight()) / 2, text, -128, 0, TextAlign.Center, 2, fontShadow);
		}
	}

}
