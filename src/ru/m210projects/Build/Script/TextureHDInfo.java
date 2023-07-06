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

package ru.m210projects.Build.Script;

import static ru.m210projects.Build.Engine.MAXPALOOKUPS;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.RESERVEDPALS;

import ru.m210projects.Build.Render.TextureHandle.Hicreplctyp;
import ru.m210projects.Build.Render.Types.Palette;

public class TextureHDInfo {

//	public static final int HICEFFECTMASK = (1 | 2);

	private final Palette[] tinting = new Palette[MAXPALOOKUPS];
	private final Hicreplctyp[] cache = new Hicreplctyp[MAXTILES];

	public TextureHDInfo() {
		for (int i = 0; i < MAXPALOOKUPS; i++) // all tints should be 100%
			tinting[i] = new Palette(0xff, 0xff, 0xff, 0);
	}

	public TextureHDInfo(TextureHDInfo src) {
		for (int i = 0; i < MAXPALOOKUPS; i++)
			this.tinting[i] = new Palette(src.tinting[i]);
		for (int i = 0; i < MAXTILES; i++) {
			if (src.cache[i] == null)
				continue;
			for (Hicreplctyp hr = src.cache[i]; hr != null; hr = hr.next)
				add(new Hicreplctyp(hr), i);
		}
	}

	public void setPaletteTint(int palnum, int r, int g, int b, int effect) {
		if (palnum >= MAXPALOOKUPS)
			return;
		tinting[palnum].update(r, g, b, effect & 3);
	}

    public Palette getTints(int palnum) {
		return tinting[palnum];
	}

	private Hicreplctyp get(int picnum, int palnum) {
		for (Hicreplctyp hr = cache[picnum]; hr != null; hr = hr.next) {
			if (hr.palnum == palnum)
				return hr;
		}

		return null;
	}

	private void add(Hicreplctyp tex, int picnum) {
		tex.next = cache[picnum];
		cache[picnum] = tex;
	}

	public Hicreplctyp remove(int picnum, int palnum) {
		Hicreplctyp tmp;
		if (cache[picnum] != null && cache[picnum].palnum == palnum) {
			tmp = cache[picnum];
			cache[picnum] = cache[picnum].next;
			return tmp;
		}

		for (Hicreplctyp hr = cache[picnum]; hr != null; hr = hr.next) {
			tmp = hr.next;
			if (tmp.palnum == palnum) {
				hr.next = tmp.next;
				return tmp;
			}
		}

		return null;
	}

	public boolean isHighTile(int picnum) {
		return cache[picnum] != null;
	}

	public boolean addTexture(int picnum, int palnum, String filen, float alphacut, float xscale, float yscale,
			float specpower, float specfactor, int flags) {
		if (filen == null || picnum >= MAXTILES || palnum >= MAXPALOOKUPS)
			return false;

		Hicreplctyp hr = get(picnum, palnum);
		if (hr == null) // no replacement yet defined
			add(hr = new Hicreplctyp(palnum), picnum);

		// store into hicreplc the details for this replacement
		hr.filename = filen;
		hr.ignore = 0;
		hr.alphacut = Math.min(alphacut, 1.0f);
		hr.xscale = xscale;
		hr.yscale = yscale;
		hr.specpower = specpower;
		hr.specfactor = specfactor;
		hr.flags = flags;

		return true;
	}

	public boolean addSkybox(int picnum, int palnum, String[] faces) {
		if (picnum >= MAXTILES || palnum >= MAXPALOOKUPS)
			return false;

		for (int i = 0; i < 6; i++)
			if (faces[i] == null)
				return false;

		Hicreplctyp hr = get(picnum, palnum);
		if (hr == null) // no replacement yet defined
			add(hr = new Hicreplctyp(palnum, true), picnum);

		System.arraycopy(faces, 0, hr.skybox.face, 0, 6);
		hr.skybox.ignore = 0;

		return true;
	}

	public Hicreplctyp findTexture(int picnum, int palnum, int skybox) {
		if (picnum >= MAXTILES)
			return null;

		do {
			Hicreplctyp hr = get(picnum, palnum);
			if (hr != null) {
				if (skybox != 0) {
					if (hr.skybox != null && hr.skybox.ignore == 0)
						return hr;
				} else if (hr.ignore == 0)
					return hr;
			}

			if (palnum == 0 || palnum >= (MAXPALOOKUPS - RESERVEDPALS))
				break;
			palnum = 0;
		} while (true);

		return null;
	}
}
