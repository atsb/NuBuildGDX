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

import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;
import ru.m210projects.Build.Render.Types.GLFilter;

public class GLTileArray {

	private final GLTile[] array;

	public GLTileArray(int size) {
		array = new GLTile[size];
	}

	public GLTile get(int picnum, int palnum, boolean clamped, int surfnum) {
		for (GLTile pth = array[picnum]; pth != null && pth.palnum <= palnum; pth = pth.next) {
			if (pth.getPixelFormat() == PixelFormat.Pal8) {
				if (pth.isClamped() == clamped)
					return pth;
				continue;
			}

			if (pth.palnum == palnum && pth.isClamped() == clamped && pth.skyface == surfnum)
				return pth;
		}
		return null;
	}

	public GLTile get(int picnum) {
		return array[picnum];
	}

	public void add(GLTile newNode, int dapicnum) {
		int p = newNode.compareTo(array[dapicnum]);
		if (p <= 0) {
			// addFirst
			newNode.next = array[dapicnum];
			array[dapicnum] = newNode;
		} else {
			GLTile prev = null;
			GLTile pth = array[dapicnum];
			do {
				if (newNode.compareTo(pth) < 0) {
					newNode.next = pth;
					if (prev != null)
						prev.next = newNode;
					return;
				}

				prev = pth;
				pth = pth.next;
			} while (pth != null);

			// addLast
			prev.next = newNode;
		}
	}

	public void dispose(int tilenum) {
		for (GLTile pth = array[tilenum]; pth != null;) {
			GLTile next = pth.next;
			pth.delete();
			pth = next;
		}
		array[tilenum] = null;
	}

	public void setFilter(int tilenum, GLFilter filter, int anisotropy) {
		for (GLTile pth = array[tilenum]; pth != null;) {
			GLTile next = pth.next;

			pth.bind();
			pth.setupTextureFilter(filter, anisotropy);
			if (!filter.retro)
				pth.setInvalidated(true);
			pth = next;
		}
	}

	public void invalidate(int tilenum) {
		for (GLTile pth = array[tilenum]; pth != null;) {
			GLTile next = pth.next;

			if (pth.hicr == null && pth.getPixelFormat() != PixelFormat.Pal8)
				pth.setInvalidated(true);
			pth = next;
		}
	}
}
