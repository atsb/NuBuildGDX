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

package ru.m210projects.Build.Render;

import java.util.HashMap;

import ru.m210projects.Build.Render.TextureHandle.TextureManager;
import ru.m210projects.Build.Render.Types.FadeEffect;

public interface GLRenderer extends Renderer {

	enum Rendering {
		Nothing, Sprite, Wall, MaskWall, Floor, Ceiling, Skybox, Model, Tile;

		private int index;

		public int getIndex() {
			return index;
		}

		public Rendering setIndex(int i) {
			this.index = i;
			return this;
		}
	}

	enum GLPreloadFlag {
		Models, Other
	}

	enum GLInvalidateFlag {
		Uninit, SkinsOnly, TexturesOnly, IndexedTexturesOnly, Palookup, All
	}

	TextureManager getTextureManager();

	void enableIndexedShader(boolean enable);

	void palfade(HashMap<String, FadeEffect> fades);

	void preload(GLPreloadFlag... flags);

	void precache(int dapicnum, int dapalnum, int datype);

	void gltexapplyprops();

	void gltexinvalidateall(GLInvalidateFlag... flags);

	void setdrunk(float intensive);

	float getdrunk();

	void addSpriteCorr(int snum);

	void removeSpriteCorr(int snum);

}
