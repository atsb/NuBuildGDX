package ru.m210projects.Witchaven.Menu;

import static ru.m210projects.Witchaven.Main.*;
import static ru.m210projects.Witchaven.Globals.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class WHTitle extends MenuTitle {

	public WHTitle(Object text, int x, int y) {
		super(game.pEngine, text, game.WH2 ? game.getFont(0) : game.getFont(1), x, y, WHLOGO);
	}
	
	@Override
	public void draw(MenuHandler handler) {
		if ( text != null )
		{
		    int tx = x + 64;
		    int ty = y + 24;
		    
		    engine.rotatesprite((tx - 25) << 16, ty + 15 << 16, 65536, 0, 585, 0, 0, 2|4|8, 0, 0, coordsConvertXScaled(tx, ConvertType.Normal), ydim - 1);
			engine.rotatesprite((tx + 25) << 16, ty + 15 << 16, 65536, 0, 585, 0, 0, 2|4|8, coordsConvertXScaled(tx, ConvertType.Normal), 0, xdim - 1, ydim - 1);

			font.drawText(tx, ty + 10, text, 0, game.WH2 ? 0 : 9, TextAlign.Center, 2, false);
			
			engine.rotatesprite(x << 16, y << 16, 16384, 0, nTile, 0, 0, 2|8|16, 0, 0, xdim - 1, ydim - 1);
		}
	}


}
