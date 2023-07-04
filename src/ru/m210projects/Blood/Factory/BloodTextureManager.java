// This file is part of BloodGDX.
// Copyright (C) 2017-2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood.Factory;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static ru.m210projects.Build.Engine.curpalette;
import static ru.m210projects.Build.Engine.globalshade;
import static ru.m210projects.Build.Engine.numshades;
import static ru.m210projects.Build.Engine.palookup;

import com.badlogic.gdx.graphics.Pixmap;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Render.TextureHandle.Hicreplctyp;
import ru.m210projects.Build.Render.TextureHandle.IndexedTileData;
import ru.m210projects.Build.Render.TextureHandle.PixmapTileData;
import ru.m210projects.Build.Render.TextureHandle.RGBTileData;
import ru.m210projects.Build.Render.TextureHandle.TextureManager;
import ru.m210projects.Build.Render.TextureHandle.TileData;
import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;

public class BloodTextureManager extends TextureManager {

	public BloodTextureManager(Engine engine) {
		super(engine, ExpandTexture.Both);
	}

	@Override
	protected TileData loadPic(PixelFormat fmt, Hicreplctyp hicr, int dapicnum, int dapalnum, boolean clamping,
			boolean alpha, int skybox) {

		int expand = 1 | 2;
		if (hicr != null) {
			String fn = checkResource(hicr, dapicnum, skybox);
			byte[] data = BuildGdx.cache.getBytes(fn, 0);
			if (data != null) {
				try {
					return new PixmapTileData(new Pixmap(data, 0, data.length), clamping, expand);
				} catch (Throwable t) {
					t.printStackTrace();
					if (skybox != 0)
						return null;
				}
			}
		}

		if (fmt == PixelFormat.Pal8)
			return new IndexedTileData(engine.getTile(dapicnum), clamping, alpha, expand);
		return new RGBTileData(engine.getTile(dapicnum), dapalnum, clamping, alpha, expand) {

			@Override
			protected int getColor(int dacol, int dapal, boolean alphaMode) {
				dacol &= 0xFF;
				if (alphaMode && dacol == 255)
					return curpalette.getRGBA(0, (byte) 0);

				if (dacol >= palookup[dapal].length)
					return 0;

				if (dapal == 1) {
					int shade = (min(max(globalshade, 0), numshades - 1));
					dacol = palookup[dapal][dacol + (shade << 8)] & 0xFF;
				} else
					dacol = palookup[dapal][dacol] & 0xFF;

				return curpalette.getRGBA(dacol, (byte) 0xFF);
			}
		};
	}
}
