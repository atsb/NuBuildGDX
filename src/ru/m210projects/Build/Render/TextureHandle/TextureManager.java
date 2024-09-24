// This file is part of BuildGDX.
// Copyright (C) 2017-2021  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Build.Render.TextureHandle;

import static com.badlogic.gdx.graphics.GL20.GL_LUMINANCE;

import com.badlogic.gdx.graphics.Pixmap;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;

import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;

import ru.m210projects.Build.Script.TextureHDInfo;

public class TextureManager {

	protected final Engine engine;
	protected TextureHDInfo info;
	protected int texunits = 0;
	protected final ExpandTexture expand;

	public enum ExpandTexture {
		Horizontal(1), Vertical(2), Both(1 | 2);

		private final byte bit;

		ExpandTexture(int bit) {
			this.bit = (byte) bit;
		}

		public byte get() {
			return bit;
		}
	}

	public TextureManager(Engine engine, ExpandTexture opt) {
		this.engine = engine;
		this.expand = opt;
	}

	public void setTextureInfo(TextureHDInfo info) {
		this.info = info;
	}

	public int getTextureUnits() {
		return texunits;
	}

	protected TileData loadPic(PixelFormat fmt, Hicreplctyp hicr, int dapicnum, int dapalnum, boolean clamping,
			boolean alpha, int skybox) {

        if (hicr != null) {
			String fn = checkResource(hicr, dapicnum, skybox);
			byte[] data = BuildGdx.cache.getBytes(fn, 0);
			if (data != null) {
				try {
					return new PixmapTileData(new Pixmap(data, 0, data.length), clamping, expand.get());
				} catch (Throwable t) {
					t.printStackTrace();
					if (skybox != 0)
						return null;
				}
			}
		}

		if (fmt == PixelFormat.Pal8)
			return new IndexedTileData(engine.getTile(dapicnum), clamping, alpha, expand.get());
		return new RGBTileData(engine.getTile(dapicnum), dapalnum, clamping, alpha, expand.get());
	}

	protected String checkResource(Hicreplctyp hicr, int dapic, int facen) {
		if (hicr == null)
			return null;

		String fn = null;
		if (facen > 0) {
			if (hicr.skybox == null || facen > 6 || hicr.skybox.face[facen - 1] == null)
				return null;

			fn = hicr.skybox.face[facen - 1];
		} else
			fn = hicr.filename;

		if (!BuildGdx.cache.contains(fn, 0)) {
			Console.Println("Hightile[" + dapic + "]: File \"" + fn + "\" not found");
			if (facen > 0)
				hicr.skybox.ignore = 1;
			else
				hicr.ignore = 1;
			return null;
		}

		return fn;
	}

	public boolean clampingMode(int dameth) {
		return ((dameth & 4) >> 2) == 1;
	}

	public boolean alphaMode(int dameth) {
		return (dameth & 256) == 0;
	}

	// Indexed texture params and methods

	private abstract static class ShaderData extends DummyTileData {

		public ShaderData(byte[] buf, int w, int h, int bytes) {
			super(bytes != 1 ? PixelFormat.Rgb : PixelFormat.Pal8, w, h);
			data.clear();
			data.put(buf, 0, buf.length);
		}

		@Override
		public boolean hasAlpha() {
			return false;
		}
	}

	private class PaletteData extends ShaderData {
		public PaletteData(byte[] data) {
			super(data, 256, 1, 3);
		}

		@Override
		public PixelFormat getPixelFormat() {
			return PixelFormat.Pal8;
		}
	}

	private class LookupData extends ShaderData {
		public LookupData(byte[] data) {
			super(data, 256, 64, 1);
		}

		@Override
		public int getGLFormat() {
			return GL_LUMINANCE;
		}
	}
}
