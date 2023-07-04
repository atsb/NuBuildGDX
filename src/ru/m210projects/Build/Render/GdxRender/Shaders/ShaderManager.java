package ru.m210projects.Build.Render.GdxRender.Shaders;

import static ru.m210projects.Build.Engine.windowx1;
import static ru.m210projects.Build.Engine.windowx2;
import static ru.m210projects.Build.Engine.windowy1;
import static ru.m210projects.Build.Engine.windowy2;
import static ru.m210projects.Build.Engine.ydim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Render.GdxRender.BuildCamera;
import ru.m210projects.Build.Render.GdxRender.Shaders.ShaderManager.Shader;
import ru.m210projects.Build.Render.TextureHandle.IndexedShader;
import ru.m210projects.Build.Render.TextureHandle.TextureManager;
import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;
import ru.m210projects.Build.Render.Types.FadeEffect.FadeShader;

public class ShaderManager {

	protected IndexedSkyShaderProgram skyshader;
	protected IndexedShader texshader;
	protected ShaderProgram skyshader32;
	protected ShaderProgram texshader32;

	protected ShaderProgram bitmapShader;
	protected FadeShader fadeshader;

	protected ShaderProgram currentShaderProgram;
	protected Shader currentShader;

	private int world_projTrans;
	private int world_modelView;
	private int world_invProjectionView;
	private int world_viewport;
	private int world_mirror;
	private int world_planeClipping;
	private int world_plane0;
	private int world_plane1;
	private int world_transform;
	private int world_texture_transform;
	private int world32_texture_transform;
	private int world32_color;

	public enum Shader {
		IndexedWorldShader, RGBWorldShader, IndexedSkyShader, RGBSkyShader, BitmapShader, FadeShader;

		private ShaderProgram shader;

		public void set(ShaderProgram shader) {
			this.shader = shader;
		}

		public ShaderProgram get() {
			return shader;
		}
	}

	public Shader getShader() {
		return currentShader;
	}

	public PixelFormat getPixelFormat() {

		switch (currentShader) {
		case IndexedSkyShader:
		case IndexedWorldShader:
			return PixelFormat.Pal8;
		case RGBWorldShader:
		case RGBSkyShader:
		case BitmapShader:
		case FadeShader:
			return PixelFormat.Rgba;
		}

		return null;
	}

	public ShaderProgram getProgram() {
		return currentShaderProgram;
	}

	protected ShaderProgram check(Shader shader) {
		ShaderProgram sh = shader.get();
		if (currentShaderProgram != sh) {
			sh.begin();
			currentShader = shader;
		}
		return sh;
	}

	public void init(TextureManager textureCache) {
		skyshader = allocIndexedSkyShader(textureCache);
		Shader.IndexedSkyShader.set(skyshader);
		skyshader32 = allocRgbSkyShader();
		Shader.RGBSkyShader.set(skyshader32);
		texshader = allocIndexedShader(textureCache);
		Shader.IndexedWorldShader.set(texshader);
		texshader32 = allocRgbShader();
		Shader.RGBWorldShader.set(texshader32);
		bitmapShader = allocBitmapShader();
		Shader.BitmapShader.set(bitmapShader);
		fadeshader = allocFadeShader();
		Shader.FadeShader.set(fadeshader);
	}

	public boolean isInited() {
		return skyshader != null && skyshader32 != null && texshader != null &&
				texshader32 != null && bitmapShader != null && fadeshader != null;
	}

	public void dispose() {
		if (skyshader != null)
			skyshader.dispose();
		if (skyshader32 != null)
			skyshader32.dispose();
		if (texshader != null)
			texshader.dispose();
		if (texshader32 != null)
			texshader32.dispose();
		if (bitmapShader != null)
			bitmapShader.dispose();
		if (fadeshader != null)
			fadeshader.dispose();

		for (Shader sh : Shader.values())
			sh.set(null);
	}

	public void fog(boolean enable, float start, float end, float r, float g, float b) {
		switch (getShader()) {
		case RGBWorldShader:
			texshader32.setUniformi("u_fogEnable", enable ? 1 : 0);
			texshader32.setUniformf("u_fogStart", start);
			texshader32.setUniformf("u_fogEnd", end);
			texshader32.setUniformf("u_fogColor", r, g, b);
			break;
		}
	}

	public void fog(Shader shader, boolean enable, float start, float end, float r, float g, float b) {
		check(shader);

		switch (shader) {
		case RGBWorldShader:
			texshader32.setUniformi("u_fogEnable", enable ? 1 : 0);
			texshader32.setUniformf("u_fogStart", start);
			texshader32.setUniformf("u_fogEnd", end);
			texshader32.setUniformf("u_fogColor", r, g, b);
			break;
		}
	}

	public void mirror(boolean mirror) {
		switch (getShader()) {
		case IndexedWorldShader:
			texshader.setUniformi(world_mirror, mirror ? 1 : 0);
			break;
		case RGBWorldShader:
			texshader32.setUniformi("u_mirror", mirror ? 1 : 0); // XXX
			break;
		case IndexedSkyShader:
			skyshader.mirror(mirror);
			break;
		case RGBSkyShader:
			skyshader32.setUniformi("u_mirror", mirror ? 1 : 0); // XXX
			break;
		}
	}

	public void mirror(Shader shader, boolean mirror) {
		check(shader);

		switch (shader) {
		case IndexedWorldShader:
			texshader.setUniformi(world_mirror, mirror ? 1 : 0);
			break;
		case RGBWorldShader:
			texshader32.setUniformi("u_mirror", mirror ? 1 : 0);
			break;
		case IndexedSkyShader:
			skyshader.mirror(mirror);
			break;
		case RGBSkyShader:
			skyshader32.setUniformi("u_mirror", mirror ? 1 : 0); // XXX
			break;
		}
	}

	public void viewport(int cx1, int cy1, int cx2, int cy2) {
		Shader shader = this.getShader();

		if (shader == Shader.IndexedWorldShader) {
			if (cx1 == 0 && cy1 == 0 && cx2 == 0 && cy2 == 0) {
				texshader.setUniformi(world_planeClipping, 0);
				return;
			}

			texshader.setUniformi(world_planeClipping, 2);
			texshader.setUniformf(world_viewport, cx1, ydim - cy1, cx2 + 1, ydim - cy2 - 1);
		} else if (shader == Shader.RGBWorldShader) { //XXX
			if (cx1 == 0 && cy1 == 0 && cx2 == 0 && cy2 == 0) {
				texshader32.setUniformi("u_planeClipping", 0);
				return;
			}

			texshader32.setUniformi("u_planeClipping", 2);
			texshader32.setUniformf("u_viewport", cx1, ydim - cy1, cx2 + 1, ydim - cy2 - 1);
		}
	}

	public void viewport(Shader shader, int cx1, int cy1, int cx2, int cy2) {
		check(shader);

		if (shader == Shader.IndexedWorldShader) {
			if (cx1 == 0 && cy1 == 0 && cx2 == 0 && cy2 == 0) {
				texshader.setUniformi(world_planeClipping, 0);
				return;
			}

			texshader.setUniformi(world_planeClipping, 2);
			texshader.setUniformf(world_viewport, cx1, ydim - cy1, cx2 + 1, ydim - cy2 - 1);
		} else if (shader == Shader.RGBWorldShader) { //XXX
			if (cx1 == 0 && cy1 == 0 && cx2 == 0 && cy2 == 0) {
				texshader32.setUniformi("u_planeClipping", 0);
				return;
			}

			texshader32.setUniformi("u_planeClipping", 2);
			texshader32.setUniformf("u_viewport", cx1, ydim - cy1, cx2 + 1, ydim - cy2 - 1);
		}
	}

	public void frustum(Plane[] clipPlane) {
		Shader shader = this.getShader();
		if (shader == Shader.IndexedWorldShader) {
			if (clipPlane == null) {
				texshader.setUniformi(world_planeClipping, 0);
				return;
			}

			texshader.setUniformi(world_planeClipping, 1);
			texshader.setUniformf(world_plane0, clipPlane[0].normal.x, clipPlane[0].normal.y, clipPlane[0].normal.z,
					clipPlane[0].d);

			// XXX world_plane1 doesn't find
			texshader.setUniformf("u_plane[1]", clipPlane[1].normal.x, clipPlane[1].normal.y, clipPlane[1].normal.z,
					clipPlane[1].d);
		} else if (shader == Shader.RGBWorldShader) { //XXX
			if (clipPlane == null) {
				texshader32.setUniformi("u_planeClipping", 0);
				return;
			}

			texshader32.setUniformi("u_planeClipping", 1);
			texshader32.setUniformf("u_plane[0]", clipPlane[0].normal.x, clipPlane[0].normal.y, clipPlane[0].normal.z,
					clipPlane[0].d);
			texshader32.setUniformf("u_plane[1]", clipPlane[1].normal.x, clipPlane[1].normal.y, clipPlane[1].normal.z,
					clipPlane[1].d);
		}
	}

	public void frustum(Shader shader, Plane[] clipPlane) {
		check(shader);

		if (shader == Shader.IndexedWorldShader) {
			if (clipPlane == null) {
				texshader.setUniformi(world_planeClipping, 0);
				return;
			}

			texshader.setUniformi(world_planeClipping, 1);
			texshader.setUniformf(world_plane0, clipPlane[0].normal.x, clipPlane[0].normal.y, clipPlane[0].normal.z,
					clipPlane[0].d);

			// XXX world_plane1 doesn't find
			texshader.setUniformf("u_plane[1]", clipPlane[1].normal.x, clipPlane[1].normal.y, clipPlane[1].normal.z,
					clipPlane[1].d);
		} else if (shader == Shader.RGBWorldShader) { //XXX
			if (clipPlane == null) {
				texshader32.setUniformi("u_planeClipping", 0);
				return;
			}

			texshader32.setUniformi("u_planeClipping", 1);
			texshader32.setUniformf("u_plane[0]", clipPlane[0].normal.x, clipPlane[0].normal.y, clipPlane[0].normal.z,
					clipPlane[0].d);
			texshader32.setUniformf("u_plane[1]", clipPlane[1].normal.x, clipPlane[1].normal.y, clipPlane[1].normal.z,
					clipPlane[1].d);
		}
	}

	public ShaderManager transform(Matrix4 transform) {
		Shader shader = this.getShader();
		if (shader == null)
			return this;

		switch (shader) {
		case IndexedWorldShader:
			texshader.setUniformMatrix(world_transform, transform);
			break;
		case RGBWorldShader:
			texshader32.setUniformMatrix("u_transform", transform); // XXX
			break;
		case IndexedSkyShader:
			skyshader.transform(transform);
			break;
		case RGBSkyShader:
			skyshader32.setUniformMatrix("u_transform", transform); // XXX
			break;
		}

		return this;
	}

	public ShaderManager textureTransform(Matrix3 transform, int unit) {
		Shader shader = this.getShader();
		if (shader == null)
			return this;

		switch (shader) {
		case IndexedWorldShader:
			texshader.setUniformMatrix(world_texture_transform, transform);
			break;
		case RGBWorldShader:
			texshader32.setUniformMatrix(world32_texture_transform, transform);
			break;
		}

		return this;
	}

	public ShaderManager projection(Matrix4 projection) {
		Shader shader = this.getShader();

		switch (shader) {
		case IndexedWorldShader:
			texshader.setUniformMatrix(world_projTrans, projection);
			break;
		case RGBWorldShader:
			texshader32.setUniformMatrix("u_projTrans", projection);
			break;
		case BitmapShader:
			bitmapShader.setUniformMatrix("u_projTrans", projection); // XXX
			break;
		}

		return this;
	}

	public ShaderManager view(Matrix4 view) {
		Shader shader = this.getShader();

		switch (shader) {
		case IndexedWorldShader:
			texshader.setUniformMatrix(world_modelView, view);
			break;
		case RGBWorldShader:
			break;
		case BitmapShader:
			break;
		}

		return this;
	}

	public void transform(Shader shader, Matrix4 transform) {
		check(shader);

		switch (shader) {
		case IndexedWorldShader:
			texshader.setUniformMatrix(world_transform, transform);
			break;
		case RGBWorldShader:
			texshader32.setUniformMatrix("u_transform", transform); // XXX
			break;
		case IndexedSkyShader:
			skyshader.transform(transform);
			break;
		case RGBSkyShader:
			skyshader32.setUniformMatrix("u_transform", transform); // XXX
			break;
		}
	}

	public void textureParams8(Shader shader, int pal, int shade, float alpha, boolean lastIndex) {
		check(shader);

		switch (shader) {
		case IndexedWorldShader:
			texshader.setTextureParams(pal, shade);
			texshader.setDrawLastIndex(lastIndex);
			texshader.setTransparent(alpha);
			break;
		case IndexedSkyShader:
			skyshader.setTextureParams(pal, shade);
			skyshader.setDrawLastIndex(lastIndex);
			skyshader.setTransparent(alpha);
			break;
		}
	}

	public void textureParams8(int pal, int shade, float alpha, boolean lastIndex) {
		Shader shader = this.getShader();

		switch (shader) {
		case IndexedWorldShader:
			texshader.setTextureParams(pal, shade);
			texshader.setDrawLastIndex(lastIndex);
			texshader.setTransparent(alpha);
			break;
		case IndexedSkyShader:
			skyshader.setTextureParams(pal, shade);
			skyshader.setDrawLastIndex(lastIndex);
			skyshader.setTransparent(alpha);
			break;
		}
	}

	public void color(Shader shader, float r, float g, float b) {
		check(shader);

		switch (shader) {
		case RGBWorldShader:
			texshader32.setUniformf("u_color", r, g, b);
			break;
		}
	}

	public void color(float r, float g, float b, float a) {
		Shader shader = this.getShader();

		switch (shader) {
		case RGBWorldShader:
			texshader32.setUniformf("u_color", r, g, b, a);
			break;
		}
	}

	public void prepare(BuildCamera cam) {
		Shader shader = this.getShader();

		switch (shader) {
		case IndexedWorldShader:
			texshader.setUniformMatrix(world_projTrans, cam.combined);
			texshader.setUniformMatrix(world_modelView, cam.view);
			texshader.setUniformMatrix(world_invProjectionView, cam.invProjectionView);
			texshader.setUniformf(world_viewport, windowx1, windowy1, windowx2 - windowx1 + 1, windowy2 - windowy1 + 1);
			texshader.setUniformi(world_planeClipping, 0);
			break;
		case RGBWorldShader:
			// XXX
			texshader32.setUniformMatrix("u_projTrans", cam.combined);
			texshader32.setUniformMatrix("u_invProjectionView", cam.invProjectionView);
			texshader32.setUniformf("u_viewport", windowx1, windowy1, windowx2 - windowx1 + 1, windowy2 - windowy1 + 1);
			texshader32.setUniformi("u_planeClipping", 0);
			break;
		case IndexedSkyShader:
			skyshader.prepare(cam);
			break;
		case RGBSkyShader:
			skyshader32.setUniformf("u_camera", cam.position.x, cam.position.y, cam.position.z);
			skyshader32.setUniformMatrix("u_projTrans", cam.combined); // XXX
			break;
		}
	}

	public void prepare(Shader shader, BuildCamera cam) {
		check(shader);

		switch (shader) {
		case IndexedWorldShader:
			texshader.setUniformMatrix(world_projTrans, cam.combined);
			texshader.setUniformMatrix(world_modelView, cam.view);
			texshader.setUniformMatrix(world_invProjectionView, cam.invProjectionView);
			texshader.setUniformf(world_viewport, windowx1, windowy1, windowx2 - windowx1 + 1, windowy2 - windowy1 + 1);
			texshader.setUniformi(world_planeClipping, 0);
			break;
		case RGBWorldShader:
			// XXX
			texshader32.setUniformMatrix("u_projTrans", cam.combined);
			texshader32.setUniformMatrix("u_invProjectionView", cam.invProjectionView);
			texshader32.setUniformf("u_viewport", windowx1, windowy1, windowx2 - windowx1 + 1, windowy2 - windowy1 + 1);
			texshader32.setUniformi("u_planeClipping", 0);
			break;
		case IndexedSkyShader:
			skyshader.prepare(cam);
			break;
		case RGBSkyShader:
			skyshader32.setUniformf("u_camera", cam.position.x, cam.position.y, cam.position.z);
			skyshader32.setUniformMatrix("u_projTrans", cam.combined); // XXX
			break;
		}
	}

	public ShaderProgram get(Shader shader) {
		switch (shader) {
		case IndexedWorldShader:
			return texshader;
		case RGBWorldShader:
			return texshader32;
		case IndexedSkyShader:
			return skyshader;
		case RGBSkyShader:
			return skyshader32;
		case BitmapShader:
			return bitmapShader;
		case FadeShader:
			return fadeshader;
		}
		return null;
	}

	public ShaderProgram bind(Shader shader) {
		if (currentShaderProgram != null)
			currentShaderProgram.end();

		ShaderProgram sh = shader.get();
		if (sh != null) {
			sh.begin();
			currentShader = shader;
			return sh;
		}

		currentShaderProgram = null;
		currentShader = null;
		return sh;
	}

	public void reset() {

		for (Shader sh : Shader.values()) {
			this.textureParams8(sh, 0, 0, 0, false);
			this.fog(sh, false, 0, 0, 0, 0, 0);
			this.viewport(sh, 0, 0, 0, 0);
			this.color(sh, 0,0,0);
			this.mirror(sh, false);
		}

		unbind();
	}

	public void unbind() {
		if (currentShaderProgram != null)
			currentShaderProgram.end();
		currentShaderProgram = null;
		currentShader = null;
	}

	public FadeShader allocFadeShader() {
		return new FadeShader() {
			@Override
			public void begin() {
				super.begin();
				currentShaderProgram = this;
			}
		};
	}

	public IndexedSkyShaderProgram allocIndexedSkyShader(final TextureManager textureCache) {
		try {
			IndexedSkyShaderProgram skyshader = new IndexedSkyShaderProgram() {
				@Override
				public void begin() {
					super.begin();
					currentShaderProgram = this;
				}

				@Override
				public void bindPalette(int unit) {
					BuildGdx.gl.glActiveTexture(unit);
					textureCache.getPalette().bind(0);
				}

				@Override
				public void bindPalookup(int unit, int pal) {
					BuildGdx.gl.glActiveTexture(unit);
					textureCache.getPalookup(pal).bind(0);
				}
			};

			if (!skyshader.isCompiled())
				System.err.println("Shader compile error: " + skyshader.getLog());

			return skyshader;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public ShaderProgram allocRgbSkyShader() {
		try {
			ShaderProgram shader = new ShaderProgram(SkyShader.vertex, SkyShader.fragmentRGB) {
				@Override
				public void begin() {
					super.begin();
					currentShaderProgram = this;
				}
			};
			if (!shader.isCompiled())
				throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
			return shader;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public IndexedShader allocIndexedShader(final TextureManager textureCache) {
		try {
			IndexedShader shader = new IndexedShader(WorldShader.vertex, WorldShader.fragment) {
				@Override
				public void bindPalette(int unit) {
					BuildGdx.gl.glActiveTexture(unit);
					textureCache.getPalette().bind(0);
				}

				@Override
				public void bindPalookup(int unit, int pal) {
					BuildGdx.gl.glActiveTexture(unit);
					textureCache.getPalookup(pal).bind(0);
				}

				@Override
				public void begin() {
					super.begin();
					currentShaderProgram = this;
				}
			};

			this.world_projTrans = shader.getUniformLocation("u_projTrans");
			this.world_modelView = shader.getUniformLocation("u_modelView");
			this.world_invProjectionView = shader.getUniformLocation("u_invProjectionView");
			this.world_viewport = shader.getUniformLocation("u_viewport");
			this.world_mirror = shader.getUniformLocation("u_mirror");
			this.world_planeClipping = shader.getUniformLocation("u_planeClipping");
			this.world_plane0 = shader.getUniformLocation("u_plane[0]");
			this.world_plane1 = shader.getUniformLocation("u_plane[1]");
			this.world_transform = shader.getUniformLocation("u_transform");
			this.world_texture_transform = shader.getUniformLocation("u_texture_transform");

			return shader;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public ShaderProgram allocRgbShader() {
		try {
			ShaderProgram shader = new ShaderProgram(WorldShader.vertex, WorldShader.fragmentRGB) {
				@Override
				public void begin() {
					super.begin();
					currentShaderProgram = this;
				}
			};
			if (!shader.isCompiled())
				throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());

			this.world32_texture_transform = shader.getUniformLocation("u_texture_transform");
			this.world32_color = shader.getUniformLocation("u_color");
			return shader;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public ShaderProgram allocBitmapShader() { // OrthoShader
		ShaderProgram shader = new ShaderProgram(BitmapShader.vertex, BitmapShader.fragment) {
			@Override
			public void begin() {
				super.begin();
				currentShaderProgram = this;
			}
		};
		if (!shader.isCompiled())
			throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
		return shader;
	}

	@Override
	public String toString() {
		String out = "Current shader: ";
		Shader current = null;
		for (Shader sh : Shader.values()) {
			if (sh.get() == currentShaderProgram) {
				current = sh;
				break;
			}
		}
		if (current != null)
			out += current.name();
		else
			out += "NULL";
		return out;
	}
}
