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

package ru.m210projects.Powerslave.Factory;

import static ru.m210projects.Build.Engine.globalpal;
import static ru.m210projects.Build.Engine.globalvisibility;
import static ru.m210projects.Build.Engine.palookupfog;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Powerslave.Globals.SectFlag;
import static ru.m210projects.Powerslave.Globals.nLocalPlayer;
import static ru.m210projects.Powerslave.Globals.nPlayerViewSect;
import static ru.m210projects.Powerslave.Light.bTorch;

import com.badlogic.gdx.graphics.Color;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Render.Polymost.Polymost;
import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;
import ru.m210projects.Build.Settings.GLSettings;

public class PSPolymost extends Polymost {

	public PSPolymost(Engine engine) {
		super(engine, new PSMapSettings());
		r_parallaxskypanning = 1;
		globalfog.setFogScale(64);
	}

	@Override
	public Color getshadefactor(int shade, int method) {
		PixelFormat fmt = textureCache.getFmt(globalpicnum);
		if (fmt == null || fmt != PixelFormat.Pal8) {
			switch (globalpal) {
			case 1:
			case 5:
			case 6:
				shade = 0;
				break;
			case 4:
			case 11:
				shade /= 4;
				break;
			}
		}
		return super.getshadefactor(shade, method);
	}

	@Override
	protected void calc_and_apply_fog(int shade, int vis, int pal) {
		PixelFormat fmt = textureCache.getFmt(globalpicnum);
		if (fmt == null || fmt == PixelFormat.Pal8) {
			super.calc_and_apply_fog(shade, vis, pal);
			return;
		}

		switch (globalpal) {
		case 1: // nodim
		case 8: // nodim
			shade = -128;
			break;
		}

		globalfog.shade = shade;
		palookupfog[5][0] = 63; // set redbrite to 0

		if (!GLSettings.usePaletteShader.get() && (SectFlag[nPlayerViewSect[nLocalPlayer]] & 0x2000) != 0) {
			palookupfog[0][0] = 0;
			palookupfog[0][1] = 10;
			palookupfog[0][2] = 20;
		} else {
			palookupfog[0][0] = 0;
			palookupfog[0][1] = 0;
			palookupfog[0][2] = 0;
		}

		pal = 0;
		switch (globalpal) {
		case 2: // torch
		case 3: // notorch
		case 9: // torch
		case 10: // notorch
			globalfog.shade *= 4;
			int globvis = globalvisibility;
			if (bTorch == 0 && (globalpal == 3 || globalpal == 10)) {
				globalfog.shade *= globalpal == 3 ? 3 : 1;
				globvis *= globalpal == 3 ? 10 : 30;
			} else {
				globvis *= globalpal == 3 ? 3 : 4;
			}

			globalfog.combvis = globvis;
			if (vis != 0)
				globalfog.combvis = mulscale(globvis, (vis + 16) & 0xFF, 4);
			break;
		case 1: // nodim
		case 8: // nodim
		case 6: // grnbrite
		case 4: // brite
		case 11: // brite
			globalfog.combvis = 0;
			break;
		case 5: // redbrite
			palookupfog[5][0] = 63;
			pal = 5;
		default:
			globalfog.combvis = globalvisibility;
			if (vis != 0)
				globalfog.combvis = mulscale(globalvisibility, (vis + 16) & 0xFF, 4);
			break;
		}

		globalfog.pal = pal;
		globalfog.calc();
	}
}
