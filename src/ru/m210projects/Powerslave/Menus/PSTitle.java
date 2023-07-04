// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave.Menus;

import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Powerslave.Energy.*;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class PSTitle extends MenuTitle {

	protected int nScale;
	public PSTitle(int x, int y, int nScale) {
		super(game.pEngine, null, game.getFont(0), x, y, 0);
		this.nScale = nScale;
	}
	
	public PSTitle(String text, int x, int y, int nScale) {
		super(game.pEngine, text, game.getFont(0), x, y, 0);
		this.nScale = nScale;
		if(nScale == 0)
			font = game.getFont(1);
	}
	
	@Override
	public void draw(MenuHandler handler) {
		if(nScale != 0)
			DoPlasma(x, y, nScale);
		else game.pEngine.rotatesprite(160 << 16, (y - 4) << 16, 52000, 0, 3469, 0, 0, 78, 0, 0, xdim - 1, ydim - 1);

		if ( text != null )
		    font.drawText(x, y, text, -128, pal, TextAlign.Center, 2, true);
	}

}
