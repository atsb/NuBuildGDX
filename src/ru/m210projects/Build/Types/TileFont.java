// This file is part of BuildGDX.
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Types;

import java.nio.ByteBuffer;
import java.util.HashSet;

import ru.m210projects.Build.Render.TextureHandle.DummyTileData;
import ru.m210projects.Build.Render.TextureHandle.GLTile;
import ru.m210projects.Build.Render.TextureHandle.TextureManager;
import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;

public class TileFont {

	public abstract static class TileFontData extends DummyTileData {

		public TileFontData(int width, int height) {
			super(PixelFormat.Rgba, width, height);
			buildAtlas(data);
		}

		public abstract ByteBuffer buildAtlas(ByteBuffer data);
	}

	public static final HashSet<TileFont> managedFont = new HashSet<TileFont>();

	public enum FontType {
		Tilemap, Bitmap
	}

	public GLTile atlas;

	public Object ptr;
	public FontType type;
	public int charsizx;
	public int charsizy;
	public int cols, rows;
	public int sizx = -1, sizy = -1;

	public TileFont(FontType type, Object ptr, int charsizx, int charsizy, int cols, int rows) {
		this.ptr = ptr;
		this.type = type;
		this.charsizx = charsizx;
		this.charsizy = charsizy;
		this.cols = cols;
		this.rows = rows;

		this.sizx = charsizx * cols;
		this.sizy = charsizy * rows;

		managedFont.add(this);
	}

	public GLTile getGL(TextureManager textureCache, PixelFormat fmt, int col) {
		GLTile tile = textureCache.get(fmt, (Integer) ptr, col, 0, 0);
		if (tile != null) {
			textureCache.bind(tile);
			return tile;
		}

		return null;
	}

	public void uninit() {
		if (atlas != null) {
			atlas.delete();
			atlas = null;
		}
	}

	public void dispose() {
		uninit();
		managedFont.remove(this);
	}

}
