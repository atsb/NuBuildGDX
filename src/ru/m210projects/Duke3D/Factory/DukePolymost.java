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

package ru.m210projects.Duke3D.Factory;

import static ru.m210projects.Build.Engine.globalshade;
import static ru.m210projects.Duke3D.Globals.ps;
import static ru.m210projects.Duke3D.Globals.screenpeek;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Render.Polymost.Polymost;

public class DukePolymost extends Polymost {
	public DukePolymost(Engine engine) {
		super(engine, new DukeMapSettings());
	}

	@Override
	protected void calc_and_apply_fog(int shade, int vis, int pal) {
		// Nightvision hack
		if (ps[screenpeek].heat_on != 0 && rendering == Rendering.Sprite) {
			if ((tspriteptr[Rendering.Sprite.getIndex()].cstat & 2) != 0) {
				vis = 0;
				globalshade -= 4;
			}
		}
		super.calc_and_apply_fog(shade, vis, pal);
	}
}
