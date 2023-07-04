package ru.m210projects.Wang.Menus;


import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Wang.Main.*;

import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Types.Tile;

public class WangSwitch extends MenuSwitch {

	public WangSwitch(Object text, BuildFont font, int x, int y, int width, boolean value, MenuProc callback) {
		super(text, font, x, y, width, value, callback, null, null);
	}

	@Override
	public void draw(MenuHandler handler) {
		int shade = handler.getShade(this);
		if ( text != null )
			font.drawText(x, y, text, shade, 16, TextAlign.Left, 2, fontShadow);

		Tile pic = engine.getTile(2849);
		engine.rotatesprite((x + width - 1 - pic.getWidth()) << 16, (y + (font.getHeight() - pic.getHeight()) / 2) << 16, 65536, 0, 2849, value ? 0 : 20, 0, 10, 0, 0, xdim-1, ydim-1);
		handler.mPostDraw(this);
	}

}
