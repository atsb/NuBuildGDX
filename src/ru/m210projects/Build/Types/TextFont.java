//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Types;

import static com.badlogic.gdx.graphics.GL20.GL_ALPHA;
import static ru.m210projects.Build.Engine.pow2char;
import static ru.m210projects.Build.Engine.textfont;
import static ru.m210projects.Build.Settings.GLSettings.glfiltermodes;

import java.nio.ByteBuffer;

import ru.m210projects.Build.Render.TextureHandle.GLTile;
import ru.m210projects.Build.Render.TextureHandle.TextureManager;
import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;

public class TextFont extends TileFont {

	public TextFont() {
		super(FontType.Bitmap, textfont, 8, 8, 16, 16);

		sizx = 128;
		sizy = 128;
	}

	@Override
	public GLTile getGL(TextureManager textureCache, PixelFormat fmt, int col) {
		if (atlas == null)
			init(textureCache);

		return atlas;
	}

	private GLTile init(TextureManager textureCache) {
		// construct a 8-bit alpha-only texture for the font glyph matrix
		TileFontData dat = new TileFontData(sizx, sizy) {
			@Override
			public ByteBuffer buildAtlas(ByteBuffer data) {
				int tptr;

				for (int h = 0; h < 256; h++) {
					tptr = (h % 16) * 8 + (h / 16) * sizx * 8;
					for (int i = 0; i < 8; i++) {
						for (int j = 0; j < 8; j++) {
							data.put(tptr + j, (textfont[h * 8 + i] & pow2char[7 - j]) != 0 ? (byte) 255 : 0);
						}
						tptr += sizx;
					}
				}
				return data;
			}

			@Override
			public int getGLInternalFormat() {
				return GL_ALPHA;
			}

			@Override
			public int getGLFormat() {
				return GL_ALPHA;
			}
		};

		atlas = textureCache.newTile(dat, 0, false);
		atlas.setupTextureFilter(glfiltermodes[0], 1);
		return atlas;
	}

}
