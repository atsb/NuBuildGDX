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

import static ru.m210projects.Duke3D.Sounds.*;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Duke3D.Player.*;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.LogoScreen;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;

public class LogoAScreen extends LogoScreen {
	public LogoAScreen(BuildGame game, float gShowTime)
	{
		super(game, gShowTime);
		this.setTile(DREALMS);
	}

	@Override
	public void show()
	{
		super.show();

		StopAllSounds();
		sndPlayMusic(currentGame.getCON().env_music_fn[0]);
		engine.setbrightness(ud.brightness>>2, drealms, GLInvalidateFlag.All);
	}
}
