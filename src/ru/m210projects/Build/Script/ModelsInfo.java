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

import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.MAXUNIQHUDID;




import ru.m210projects.Build.Render.Types.Hudtyp;
import ru.m210projects.Build.Render.Types.Tile2model;

public class ModelsInfo {

	public static class Spritesmooth {
		public float smoothduration;
		public short mdcurframe;
		public short mdoldframe;
		public short mdsmooth;
	}

	public static class SpriteAnim {
		public long mdanimtims;
		public short mdanimcur;

	}

	private final Tile2model[] cache = new Tile2model[MAXTILES];
	private final Hudtyp[][] hudInfo = new Hudtyp[2][MAXTILES];
	private final Spritesmooth[] spritesmooth = new Spritesmooth[MAXSPRITES + MAXUNIQHUDID];
	private final SpriteAnim[] spriteanim = new SpriteAnim[MAXSPRITES + MAXUNIQHUDID];

	public ModelsInfo() {
		for (int i = 0; i < spritesmooth.length; i++)
			spritesmooth[i] = new Spritesmooth();

		for (int i = 0; i < spriteanim.length; i++)
			spriteanim[i] = new SpriteAnim();
	}

	public ModelsInfo(ModelsInfo src, boolean disposable) {
		for (int i = 0; i < cache.length; i++) {
			if (src.cache[i] != null)
				cache[i] = src.cache[i].clone(disposable);
		}
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < MAXTILES; j++) {
				if (src.hudInfo[i] != null && src.hudInfo[i][j] != null)
					hudInfo[i][j] = src.hudInfo[i][j].clone();
			}
		for (int i = 0; i < spritesmooth.length; i++)
			spritesmooth[i] = new Spritesmooth();

		for (int i = 0; i < spriteanim.length; i++)
			spriteanim[i] = new SpriteAnim();
	}

	public Spritesmooth getSmoothParams(int i) {
		return spritesmooth[i];
	}

	public SpriteAnim getAnimParams(int i) {
		return spriteanim[i];
	}

	public Tile2model getParams(int picnum) {
		if (cache[picnum] != null)
			return cache[picnum];

		return null;
	}

	public Hudtyp getHudInfo(int picnum, int flags) {
		if (hudInfo[(flags >> 2) & 1] != null)
			return hudInfo[(flags >> 2) & 1][picnum];

		return null;
	}

	protected Tile2model getCache(int picnum, int pal) {
		if (cache[picnum] == null) {
			cache[picnum] = new Tile2model();
			cache[picnum].palette = pal;
			return cache[picnum];
		} else {
			if (cache[picnum].palette == pal)
				return cache[picnum];
			else {
				Tile2model n = cache[picnum];
				while (n.next != null) {
					n = n.next;
					if (n.palette == pal)
						return n;
				}

				Tile2model current = n.next = new Tile2model();
				current.palette = pal;
				return current;
			}
		}
	}

	public int addHudInfo(int tilex, double xadd, double yadd, double zadd, short angadd, int flags, int fov) {
		if (tilex >= MAXTILES)
			return -2;

		if (hudInfo[(flags >> 2) & 1] == null || hudInfo[(flags >> 2) & 1][tilex] == null)
			hudInfo[(flags >> 2) & 1][tilex] = new Hudtyp();

		Hudtyp hud = hudInfo[(flags >> 2) & 1][tilex];

		hud.xadd = (float) xadd;
		hud.yadd = (float) yadd;
		hud.zadd = (float) zadd;
		hud.angadd = (short) (angadd | 2048);
		hud.flags = (short) flags;
		hud.fov = (short) fov;

		return 0;
	}

	public void dispose() {
		for (int i = MAXTILES - 1; i >= 0; i--) {
			if (cache[i] == null)
				continue;

			if (!cache[i].disposable)
				continue;

			cache[i] = null;
		}
	}
}
