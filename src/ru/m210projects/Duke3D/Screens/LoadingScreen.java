// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D.Screens;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Strhandler.*;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Duke3D.Sounds.StopAllSounds;
import static ru.m210projects.Duke3D.Main.*;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.ScreenAdapters.LoadingAdapter;

import ru.m210projects.Duke3D.Main;
import ru.m210projects.Duke3D.Main.UserFlag;

public class LoadingScreen extends LoadingAdapter {
	
	private final Main app;
	public LoadingScreen(Main game) {
		super(game);
		this.app = game;
	}

	@Override
	public void draw(String title, float delta) {
		if(kGameCrash) {
			app.show();
			return;
		}
		
//		engine.clearview(129);
		engine.rotatesprite(320<<15,200<<15,65536,0,LOADSCREEN,0,0,2+8+64,0,0,xdim-1,ydim-1);
		
		if(title == null) {
			app.getFont(2).drawText(160, 90+16+8, toCharArray("Please wait"),  -128, 0, TextAlign.Center, 2, false);
		} else {
			if(mUserFlag == UserFlag.UserMap )
				app.getFont(2).drawText(160, 90, toCharArray("Entering usermap"),  -128, 0, TextAlign.Center, 2, false);
			else app.getFont(2).drawText(160, 90, toCharArray("Entering "),  -128, 0, TextAlign.Center, 2, false);
			app.getFont(2).drawText(160, 90+16+8, toCharArray(title),  -128, 0, TextAlign.Center, 2, false);
		}
	}

	@Override
	public void show()
	{
		super.show();
		StopAllSounds();
		engine.setbrightness(ud.brightness>>2, palette, true);
	}
	
}
