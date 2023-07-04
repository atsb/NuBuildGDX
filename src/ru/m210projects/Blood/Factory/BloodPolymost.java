// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

import static com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_SRC_COLOR;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE;
import static ru.m210projects.Build.Engine.DETAILPAL;
import static ru.m210projects.Build.Engine.GLOWPAL;
import static ru.m210projects.Build.Engine.MAXPALOOKUPS;
import static ru.m210projects.Build.Engine.TRANSLUSCENT1;
import static ru.m210projects.Build.Engine.TRANSLUSCENT2;
import static ru.m210projects.Build.Engine.globalpal;
import static ru.m210projects.Build.Engine.globalshade;
import static ru.m210projects.Build.Engine.numshades;
import static ru.m210projects.Build.Engine.palookupfog;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.Render.Types.GL10.GL_COMBINE_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_COMBINE_RGB_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_FOG_COLOR;
import static ru.m210projects.Build.Render.Types.GL10.GL_FOG_END;
import static ru.m210projects.Build.Render.Types.GL10.GL_FOG_START;
import static ru.m210projects.Build.Render.Types.GL10.GL_INTERPOLATE_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_MODELVIEW;
import static ru.m210projects.Build.Render.Types.GL10.GL_OPERAND0_RGB_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_OPERAND1_RGB_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_OPERAND2_RGB_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_PREVIOUS_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_SOURCE0_RGB_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_SOURCE1_RGB_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_SOURCE2_RGB_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_TEXTURE0;
import static ru.m210projects.Build.Render.Types.GL10.GL_TEXTURE_ENV;
import static ru.m210projects.Build.Render.Types.GL10.GL_TEXTURE_ENV_MODE;

import com.badlogic.gdx.graphics.Color;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Render.GLFog;
import ru.m210projects.Build.Render.GLInfo;
import ru.m210projects.Build.Render.Polymost.Polymost;
import ru.m210projects.Build.Render.TextureHandle.DummyTileData;
import ru.m210projects.Build.Render.TextureHandle.GLTile;
import ru.m210projects.Build.Render.TextureHandle.TextureManager;
import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;
import ru.m210projects.Build.Render.Types.Palette;
import ru.m210projects.Build.Settings.GLSettings;

public class BloodPolymost extends Polymost {

	private GLTile dummy;
	private final float[] pal1_color = { 1.0f, 1.0f, 1.0f, 1.0f };

	public BloodPolymost(Engine engine) {
		super(engine, new BloodMapSettings());

		globalfog = new GLFog() {
			@Override
			public void calc() {
				if (combvis == 0) {
					start = FULLVIS_BEGIN;
					end = FULLVIS_END;
				} else if (shade >= numshades - 1) {
					start = -1;
					end = 0.001f;
				} else {
					start = (shade > 0) ? 0 : -(FOGDISTCONST * shade) / combvis;
					end = (FOGDISTCONST * (numshades - 1 - shade)) / combvis;
				}

				color[0] = (palookupfog[pal][0] / 63.f);
				color[1] = (palookupfog[pal][1] / 63.f);
				color[2] = (palookupfog[pal][2] / 63.f);
				color[3] = 1;

				if (pal == 1 && (GLInfo.multisample == 0 || !GLSettings.usePaletteShader.get())) { // Blood's pal 1
					start = 0;
					if (end > 2)
						end = 2;
				}

//				if (manager.getShader() != null)
//					manager.getShader().setFogParams(true, start, end, color);
				BuildGdx.gl.glFogfv(GL_FOG_COLOR, color, 0);
				BuildGdx.gl.glFogf(GL_FOG_START, start);
				BuildGdx.gl.glFogf(GL_FOG_END, end);
			}
		};
		globalfog.setFogScale(64);
	}

	@Override
	protected void calc_and_apply_fog(int shade, int vis, int pal) {
		PixelFormat fmt = textureCache.getFmt(globalpicnum);
		if (fmt == null || fmt != PixelFormat.Pal8) {
			if (rendering == Rendering.Sprite && globalpal == 5 && globalshade == 127)
				shade = 0; // Blood's shadows (for pal 1)

			if (globalpal == 1 || pal == 1) {
				if (rendering == Rendering.Model) {
					shade = tspriteptr[Rendering.Model.getIndex()].shade;
					if (shade > 0)
						shade = BClipRange((int) (2.8f * shade), 32, 52);
				} else
					shade = 0;
			}
		}

		super.calc_and_apply_fog(shade, vis, pal);
	}

	@Override
	public void uninit() {
		if (dummy != null)
			dummy.delete();
		dummy = null;
		super.uninit();
	}

	@Override
	public Color getshadefactor(int shade, int method) {
		if (Rendering.Skybox.getIndex() != 0 && globalpal == 1 && GLInfo.multisample != 0) {
			bindBloodPalette(shade);
			return super.getshadefactor(0, 0);
		}

		Color c = super.getshadefactor(shade, method);
		if (globalpal == 1)
			c.r = c.g = c.b = 1; // Blood's pal 1

		return c;
	}

	protected void bindDummyTexture() {
		if (dummy == null)
			dummy = new GLTile(new DummyTileData(PixelFormat.Rgba, 1, 1), 1, false);
		bind(dummy);
	}

	public void bindBloodPalette(int shade) {
		textureCache.activateEffect();
		bindDummyTexture();

		pal1_color[3] = ((numshades - shade) / (float) numshades) + 0.1f;
		BuildGdx.gl.glTexEnvfv(GL_TEXTURE_ENV, 8705, pal1_color, 0); // GL_TEXTURE_ENV_COLOR

		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_COMBINE_ARB);
		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_COMBINE_RGB_ARB, GL_INTERPOLATE_ARB);

		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_SOURCE0_RGB_ARB, GL_PREVIOUS_ARB);
		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_OPERAND0_RGB_ARB, GL_SRC_COLOR);

		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_SOURCE1_RGB_ARB, 34166); // GL_CONSTANT
		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_OPERAND1_RGB_ARB, GL_SRC_COLOR);

		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_SOURCE2_RGB_ARB, 34166); // GL_CONSTANT
		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_OPERAND2_RGB_ARB, GL_SRC_ALPHA);
	}

	@Override
	public TextureManager newTextureManager(Engine engine) {
		return new BloodTextureManager(engine);
	}

	@Override
	public void setTextureParameters(GLTile tile, int tilenum, int pal, int shade, int skybox, int method) {
		if (tile.getPixelFormat() == PixelFormat.Pal8) {
			if (!texshader.isBinded()) {
				BuildGdx.gl.glActiveTexture(GL_TEXTURE0);
				texshader.begin();
			}
			texshader.setTextureParams(pal, shade);

			float alpha = 1.0f;
			switch (method & 3) {
			case 2:
				alpha = TRANSLUSCENT1;
				break;
			case 3:
				alpha = TRANSLUSCENT2;
				break;
			}

			if (!engine.getTile(tilenum).isLoaded())
				alpha = 0.01f; // Hack to update Z-buffer for invalid mirror textures

			texshader.setDrawLastIndex((method & 3) == 0 || !textureCache.alphaMode(method));
			texshader.setTransparent(alpha);
		} else {
			// texture scale by parkar request
			if (tile.isHighTile() && ((tile.getHiresXScale() != 1.0f) || (tile.getHiresYScale() != 1.0f))
					&& Rendering.Skybox.getIndex() == 0) {
				BuildGdx.gl.glMatrixMode(GL_TEXTURE);
				BuildGdx.gl.glLoadIdentity();
				BuildGdx.gl.glScalef(tile.getHiresXScale(), tile.getHiresYScale(), 1.0f);
				BuildGdx.gl.glMatrixMode(GL_MODELVIEW);
			}

			if (GLInfo.multisample != 0 && GLSettings.useHighTile.get() && Rendering.Skybox.getIndex() == 0) {
				if (Console.Geti("r_detailmapping") != 0) {
					GLTile detail = textureCache.get(tile.getPixelFormat(), tilenum, DETAILPAL, 0, method);
					if (detail != null) {
						bind(detail);
						setupTextureDetail(detail);

						BuildGdx.gl.glMatrixMode(GL_TEXTURE);
						BuildGdx.gl.glLoadIdentity();
						if (detail.isHighTile() && (detail.getHiresXScale() != 1.0f)
								|| (detail.getHiresYScale() != 1.0f))
							BuildGdx.gl.glScalef(detail.getHiresXScale(), detail.getHiresYScale(), 1.0f);
						BuildGdx.gl.glMatrixMode(GL_MODELVIEW);
					}
				}

				if (Console.Geti("r_glowmapping") != 0) {
					GLTile glow = textureCache.get(tile.getPixelFormat(), tilenum, GLOWPAL, 0, method);
					if (glow != null) {
						bind(glow);
						setupTextureGlow(glow);
					}
				}
			}

			Color c = getshadefactor(shade, method);
			if (tile.isHighTile() && defs.texInfo != null) {
				if (tile.getPal() != pal) {
					// apply tinting for replaced textures

					Palette p = defs.texInfo.getTints(pal);
					c.r *= p.r / 255.0f;
					c.g *= p.g / 255.0f;
					c.b *= p.b / 255.0f;
				}

				Palette pdetail = defs.texInfo.getTints(MAXPALOOKUPS - 1);
				if (pdetail.r != 255 || pdetail.g != 255 || pdetail.b != 255) {
					c.r *= pdetail.r / 255.0f;
					c.g *= pdetail.g / 255.0f;
					c.b *= pdetail.b / 255.0f;
				}
			}

			if (!engine.getTile(tilenum).isLoaded())
				c.a = 0.01f; // Hack to update Z-buffer for invalid mirror textures
			BuildGdx.gl.glColor4f(c.r, c.g, c.b, c.a);
		}
	}

}
