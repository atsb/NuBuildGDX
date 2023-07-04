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

package ru.m210projects.Powerslave.Screens;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Powerslave.Seq.GetSeqPicnum;
import static ru.m210projects.Powerslave.Sound.PlayLogoSound;
import static ru.m210projects.Powerslave.Sound.StopAllSounds;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.LogoScreen;

public class PSLogoScreen extends LogoScreen {
	
	public PSLogoScreen(BuildGame game)
	{
		super(game, 0);
	}

	public PSLogoScreen setTime(float gShowTime)
	{
		this.gShowTime = gShowTime;
		return this;
	}
	
	@Override
	public void show()
	{
		super.show();
		StopAllSounds();
		if(nTile == GetSeqPicnum(59, 0, 0))
			PlayLogoSound();
	}
	
	@Override
	public void draw(float delta) {
		if( (gTicks += delta) >= gShowTime && callback != null)
		{
			BuildGdx.app.postRunnable(callback);
			callback = null;
		}

		engine.clearview(96);
		engine.rotatesprite(0, 0, 65536, 0, nTile, 0, 0, 10 | 16, 0, 0, xdim - 1, ydim - 1);
	}
}
