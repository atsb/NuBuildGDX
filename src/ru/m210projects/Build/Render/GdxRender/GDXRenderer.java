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

package ru.m210projects.Build.Render.GdxRender;

import static com.badlogic.gdx.graphics.GL20.GL_BACK;
import static com.badlogic.gdx.graphics.GL20.GL_BLEND;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_CULL_FACE;
import static com.badlogic.gdx.graphics.GL20.GL_CW;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_TEST;
import static com.badlogic.gdx.graphics.GL20.GL_FRONT;
import static com.badlogic.gdx.graphics.GL20.GL_LESS;
import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_PACK_ALIGNMENT;
import static com.badlogic.gdx.graphics.GL20.GL_RGB;
import static com.badlogic.gdx.graphics.GL20.GL_RGBA;
import static com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_2D;
import static com.badlogic.gdx.graphics.GL20.GL_UNSIGNED_BYTE;
import static com.badlogic.gdx.graphics.GL20.GL_VERSION;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_GOLD;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.dmulscale;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Render.ModelHandle.MDModel.MDAnimation.mdpause;
import static ru.m210projects.Build.Render.ModelHandle.MDModel.MDAnimation.mdtims;
import static ru.m210projects.Build.Render.ModelHandle.MDModel.MDAnimation.omdtims;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.utils.BufferUtils;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Gameutils;
import ru.m210projects.Build.Architecture.BuildApplication.Platform;
import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Render.GLInfo;
import ru.m210projects.Build.Render.GLRenderer;
import ru.m210projects.Build.Render.IOverheadMapSettings;
import ru.m210projects.Build.Render.GdxRender.WorldMesh.GLSurface;
import ru.m210projects.Build.Render.GdxRender.WorldMesh.Heinum;
import ru.m210projects.Build.Render.GdxRender.Scanner.SectorScanner;
import ru.m210projects.Build.Render.GdxRender.Scanner.VisibleSector;
import ru.m210projects.Build.Render.GdxRender.Shaders.ShaderManager;
import ru.m210projects.Build.Render.GdxRender.Shaders.ShaderManager.Shader;
import ru.m210projects.Build.Render.ModelHandle.GLModel;
import ru.m210projects.Build.Render.ModelHandle.ModelManager;
import ru.m210projects.Build.Render.ModelHandle.Voxel.GLVoxel;
import ru.m210projects.Build.Render.TextureHandle.GLTile;
import ru.m210projects.Build.Render.TextureHandle.GLTileArray;
import ru.m210projects.Build.Render.TextureHandle.IndexedShader;
import ru.m210projects.Build.Render.TextureHandle.IndexedTileData;
import ru.m210projects.Build.Render.TextureHandle.RGBTileData;
import ru.m210projects.Build.Render.TextureHandle.TextureManager;
import ru.m210projects.Build.Render.TextureHandle.TextureManager.ExpandTexture;
import ru.m210projects.Build.Render.TextureHandle.TileAtlas;
import ru.m210projects.Build.Render.TextureHandle.TileData;
import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;
import ru.m210projects.Build.Render.Types.FadeEffect;
import ru.m210projects.Build.Render.Types.FadeEffect.FadeShader;
import ru.m210projects.Build.Render.Types.GLFilter;
import ru.m210projects.Build.Render.Types.Palette;
import ru.m210projects.Build.Render.Types.Spriteext;
import ru.m210projects.Build.Script.DefScript;
import ru.m210projects.Build.Script.ModelsInfo.SpriteAnim;
import ru.m210projects.Build.Settings.BuildSettings;
import ru.m210projects.Build.Settings.GLSettings;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.Tile.AnimType;
import ru.m210projects.Build.Types.TileFont;
import ru.m210projects.Build.Types.WALL;

public class GDXRenderer implements GLRenderer {

//	TODO:
//  Skies panning
//  Tekwar skies bug
//	Hires detail, glow
//  Overheadmap sector visible check
//  Duke E2L2 model invisible

//	Sprite ZFighting
//	Blood drunk effect
//	Skyboxes
//  Shadow warrior sprite visible bug
//  Duke E2L7 wall vis bug (scanner bug)
//  Duke E4L11 wall vis bug (scanner bug)

	public Rendering rendering = Rendering.Nothing;

	protected TextureManager textureCache;
	protected ModelManager modelManager;
	protected final Engine engine;
	protected boolean isInited = false;
	protected GL20 gl;
	protected float defznear = 0.001f;
	protected float defzfar = 1.0f;
	protected float fov = 90;

	protected float gtang = 0.0f;

	protected WorldMesh world;
	protected SectorScanner scanner;
	protected BuildCamera cam;
	protected SpriteRenderer sprR;
	protected GDXModelRenderer mdR;
	protected GDXOrtho orphoRen; // GdxOrphoRen
	protected DefScript defs;

	protected ShaderManager manager;
	protected boolean isUseIndexedTextures;

	private ByteBuffer pix32buffer;
	private ByteBuffer pix8buffer;
	protected Matrix4 transform = new Matrix4();
	protected Matrix3 texture_transform = new Matrix3();
	protected Matrix4 identity = new Matrix4();

	private boolean clearStatus = false;
	private float glox1, gloy1, glox2, gloy2;
	private boolean drunk;
	private float drunkIntensive = 1.0f;

	private GLTile frameTexture;
	private int framew;
	private int frameh;

	protected ArrayList<VisibleSector> sectors = new ArrayList<VisibleSector>();
	private final ArrayList<GLSurface> bunchfirst = new ArrayList<GLSurface>();
	protected boolean[] mirrorTextures = new boolean[MAXTILES];

	protected int FOGDISTCONST = 48;
	protected final float FULLVIS_BEGIN = (float) 2.9e30;
	protected final float FULLVIS_END = (float) 3.0e30;

	public GDXRenderer(Engine engine, IOverheadMapSettings settings) {
		this.engine = engine;
		this.textureCache = getTextureManager();
		this.modelManager = new GDXModelManager(this);
		this.manager = new ShaderManager();

		this.sprR = new SpriteRenderer(engine, this);
		this.mdR = new GDXModelRenderer(this);
		this.orphoRen = allocOrphoRenderer(settings);
		this.scanner = new SectorScanner(engine) {
			@Override
			protected Matrix4 getSpriteMatrix(SPRITE tspr) {
				Tile pic = engine.getTile(tspr.picnum);
				return sprR.getMatrix(tspr, pic.getWidth(), pic.getHeight());
			}
		};

		Arrays.fill(mirrorTextures, false);
		int[] mirrors = getMirrorTextures();
		if (mirrors != null) {
			for (int i = 0; i < mirrors.length; i++)
				mirrorTextures[mirrors[i]] = true;
		}

		System.err.println("create");
	}

	@Override
	public void init() {
		try {
			if (BuildGdx.graphics.getFrameType() != FrameType.GL)
				BuildGdx.app.setFrame(FrameType.GL);

			GLInfo.init();
			this.gl = BuildGdx.graphics.getGL20();

			enableIndexedShader(GLSettings.usePaletteShader.get());

			gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			gl.glPixelStorei(GL_PACK_ALIGNMENT, 1);

			this.cam = new BuildCamera(fov, xdim, ydim, 512, 8192);
			this.manager.init(textureCache);
			if (!this.manager.isInited())
				return;

			this.textureCache.changePalette(curpalette.getBytes());

			Console.Println("Polygdx renderer is initialized", OSDTEXT_GOLD);
			Console.Println(BuildGdx.graphics.getGLVersion().getRendererString() + " " + gl.glGetString(GL_VERSION),
					OSDTEXT_GOLD);

			orphoRen.init();

			if (world != null && world.isInvalid()) {
				world = new WorldMesh(engine);
			}

			System.err.println("init");
			isInited = true;
		} catch (Throwable t) {
			isInited = false;
		}
	}

	@Override
	public void uninit() {
		System.err.println("uninit");
		isInited = false;
		if (world != null)
			world.dispose();
		orphoRen.uninit();
		manager.dispose();
		FadeEffect.uninit();
		texturesUninit();
		modelManager.dispose();
	}

	private void texturesUninit() {
		textureCache.uninit();
		for (int i = MAXTILES - 1; i >= 0; i--) {
			skycache.dispose(i);
		}
	}

	@Override
	public void drawrooms() {
		if (orphoRen.isDrawing())
			orphoRen.end();

		// Temporaly code (Tekwar issue)
        gl.glClear(GL_DEPTH_BUFFER_BIT);

        gl.glDisable(GL_BLEND);
		gl.glEnable(GL_TEXTURE_2D);
		gl.glEnable(GL_DEPTH_TEST);

		gl.glDepthFunc(GL_LESS);
		gl.glDepthRangef(defznear, defzfar);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CW);
		resizeglcheck();

		cam.setPosition(globalposx, globalposy, globalposz);
		cam.setDirection(globalang, globalhoriz, gtang);
		cam.update(true);

		globalvisibility = visibility << 2;
		if (globalcursectnum >= MAXSECTORS) {
			globalcursectnum -= MAXSECTORS;
		} else {
			short i = globalcursectnum;
			globalcursectnum = engine.updatesectorz(globalposx, globalposy, globalposz, globalcursectnum);
			if (globalcursectnum < 0)
				globalcursectnum = i;
		}

		sectors.clear();
		scanner.clear();
		scanner.process(sectors, cam, world, globalcursectnum);

		rendering = Rendering.Nothing;
		if (inpreparemirror)
			gl.glCullFace(GL_FRONT);
		else
			gl.glCullFace(GL_BACK);

		prerender(sectors);
		drawbackground();

		// пройтись по всем секторам с небом, создать лист отображаемых текстур
		// пройтись по листу, отрисовать все меши одной текстуры с записью в глубину
		// отрисовать скайбокс с совпадением по глубине
		// после отрисовки, отчистить буфер глубины и записать значения mirrors

		for (int i = inpreparemirror ? 1 : 0; i < sectors.size(); i++) {
			drawSector(sectors.get(i));
		}

		spritesortcnt = scanner.getSpriteCount();
		tsprite = scanner.getSprites();

		manager.unbind();
	}

	@Override
	public void drawmasks() {

        int[] maskwalls = scanner.getMaskwalls();
		int maskwallcnt = scanner.getMaskwallCount();

		sprR.sort(tsprite, spritesortcnt);

		while ((spritesortcnt > 0) && (maskwallcnt > 0)) { // While BOTH > 0
			int j = maskwalls[maskwallcnt - 1];
			if (!spritewallfront(tsprite[spritesortcnt - 1], j))
				drawsprite(--spritesortcnt);
			else {
				// Check to see if any sprites behind the masked wall...
				for (int i = spritesortcnt - 2; i >= 0; i--) {
					if (!spritewallfront(tsprite[i], j)) {
						drawsprite(i);
						tsprite[i] = null;
					}
				}
				// finally safe to draw the masked wall
				drawmaskwall(--maskwallcnt);
			}
		}

		while (spritesortcnt != 0) {
			spritesortcnt--;
			if (tsprite[spritesortcnt] != null) {
				drawsprite(spritesortcnt);
			}
		}

		while (maskwallcnt > 0)
			drawmaskwall(--maskwallcnt);

		renderDrunkEffect();
		manager.unbind();
	}

	private void drawMask(int w) {
		gl.glDepthFunc(GL20.GL_LESS);
		gl.glDepthRangef(0.0001f, 0.99999f);

		drawSurf(world.getMaskedWall(w), 0, null, null);

		gl.glDepthFunc(GL20.GL_LESS);
		gl.glDepthRangef(defznear, defzfar);
	}

	protected void renderDrunkEffect() { // TODO: to shader
		/*
		 * if (drunk) { set2dview();
		 *
		 * gl.glActiveTexture(GL_TEXTURE0); boolean hasShader = texshader != null &&
		 * texshader.isBinded(); if (hasShader) texshader.end();
		 *
		 * if (frameTexture == null || framew != xdim || frameh != ydim) { int size = 1;
		 * for (size = 1; size < Math.max(xdim, ydim); size <<= 1) ;
		 *
		 * if (frameTexture != null) frameTexture.dispose(); else frameTexture = new
		 * GLTile(PixelFormat.Rgb, size, size);
		 *
		 * frameTexture.bind(); gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB,
		 * frameTexture.getWidth(), frameTexture.getHeight(), 0, GL_RGB,
		 * GL_UNSIGNED_BYTE, null); frameTexture.unsafeSetFilter(TextureFilter.Linear,
		 * TextureFilter.Linear); framew = xdim; frameh = ydim; }
		 *
		 * textureCache.bind(frameTexture); gl.glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0,
		 * 0, 0, 0, frameTexture.getWidth(), frameTexture.getHeight());
		 *
		 * gl.glDisable(GL_DEPTH_TEST); gl.glDisable(GL_CULL_FACE);
		 *
		 * float tiltang = (drunkIntensive * 360) / 2048f; float tilt = min(max(tiltang,
		 * -MAXDRUNKANGLE), MAXDRUNKANGLE); float u = (float) xdim /
		 * frameTexture.getWidth(); float v = (float) ydim / frameTexture.getHeight();
		 *
		 * int originX = xdim / 2; int originY = ydim / 2; float width = xdim * 1.05f;
		 * float height = ydim * 1.05f;
		 *
		 * float xoffs = width / 2; float yoffs = height / 2;
		 *
		 * final float rotation = 360.0f * tiltang / 2048.0f; final float cos =
		 * MathUtils.cosDeg(rotation); final float sin = MathUtils.sinDeg(rotation);
		 *
		 * float x1 = originX + (sin * yoffs - cos * xoffs); float y1 = originY - xoffs
		 * * sin - yoffs * cos;
		 *
		 * float x4 = x1 + width * cos; float y4 = y1 + width * sin;
		 *
		 * float x2 = x1 - height * sin; float y2 = y1 + height * cos;
		 *
		 * float x3 = x2 + (x4 - x1); float y3 = y2 + (y4 - y1);
		 *
		 * orphoRen.begin(); // XXX // orphoRen.setColor(1, 1, 1, abs(tilt) / (2 *
		 * MAXDRUNKANGLE)); // orphoRen.setTexture(frameTexture); //
		 * orphoRen.addVertex(x1, ydim - y1, 0, 0); // orphoRen.addVertex(x2, ydim - y2,
		 * 0, v); // orphoRen.addVertex(x3, ydim - y3, u, v); // orphoRen.addVertex(x4,
		 * ydim - y4, u, 0); orphoRen.end();
		 *
		 * gl.glEnable(GL_DEPTH_TEST); gl.glEnable(GL_CULL_FACE);
		 *
		 * if (hasShader) texshader.begin(); }
		 */
	}

	public void drawsprite(int i) {
		SPRITE tspr = tsprite[i];
		if (tspr == null || tspr.owner == -1)
			return;

		Spriteext sprext = defs.mapInfo.getSpriteInfo(tspr.owner);
		while (sprext == null || !sprext.isNotModel()) {
			rendering = Rendering.Model.setIndex(i);

			if (GLSettings.useModels.get()) {
				GLModel md = modelManager.getModel(tspr.picnum, tspr.pal);
				if (md != null) {
					if (tspr.owner < 0 || tspr.owner >= MAXSPRITES) {
						if (mdR.mddraw(md, tspr))
							return;
						break; // else, render as flat sprite
					}

					if (mdR.mddraw(md, tspr))
						return;
					break; // else, render as flat sprite
				}
			}

			if (BuildSettings.useVoxels.get()) {
				int picnum = tspr.picnum;
				if (engine.getTile(picnum).getType() != AnimType.None) {
					picnum += engine.animateoffs(picnum, tspr.owner + 32768);
				}

				int dist = (tspr.x - globalposx) * (tspr.x - globalposx)
						+ (tspr.y - globalposy) * (tspr.y - globalposy);
				if (dist < 48000L * 48000L) {
					GLVoxel vox = (GLVoxel) modelManager.getVoxel(picnum);
					if (vox != null) {
						if ((tspr.cstat & 48) != 48) {
							if (mdR.mddraw(vox, tspr))
								return;
							break; // else, render as flat sprite
						}

						if ((tspr.cstat & 48) == 48) {
							mdR.mddraw(vox, tspr);
							return;
						}
					}
				}
			}
			break;
		}

		rendering = Rendering.Sprite.setIndex(i);
		sprR.begin(cam);
		sprR.draw(tspr);
		sprR.end();
	}

	private void drawmaskwall(int i) {
		rendering = Rendering.MaskWall.setIndex(i);
		drawMask(scanner.getMaskwalls()[i]);
	}

	protected void drawbackground() {
		rendering = Rendering.Skybox;
		drawSkyPlanes();
		for (int i = inpreparemirror ? 1 : 0; i < sectors.size(); i++)
			drawSkySector(sectors.get(i));
	}

	private void prerender(ArrayList<VisibleSector> sectors) {
		if (inpreparemirror)
			return;

		bunchfirst.clear();

		for (int i = 0; i < sectors.size(); i++) {
			VisibleSector sec = sectors.get(i);

			int sectnum = sec.index;
			if ((sec.secflags & 1) != 0)
				checkMirror(world.getFloor(sectnum));

			if ((sec.secflags & 2) != 0)
				checkMirror(world.getCeiling(sectnum));

			for (int w = 0; w < sec.walls.size; w++) {
				int z = sec.walls.get(w);
				int flags = sec.wallflags.get(w);

				checkMirror(world.getWall(z, sectnum));
				if ((flags & 1) != 0)
					checkMirror(world.getLower(z, sectnum));
				if ((flags & 2) != 0)
					checkMirror(world.getUpper(z, sectnum));
				checkMirror(world.getMaskedWall(z));
			}

			for (int w = 0; w < sec.skywalls.size; w++) {
				int z = sec.skywalls.get(w);
				checkMirror(world.getParallaxCeiling(z));
				checkMirror(world.getParallaxFloor(z));
			}
		}

		for (int i = 0; i < bunchfirst.size(); i++) {
			drawSurf(bunchfirst.get(i), 0, null, null);
		}
	}

	private void checkMirror(GLSurface surf) {
		if (surf == null)
			return;

		int picnum = surf.picnum;
		if (mirrorTextures[picnum]) {
			bunchfirst.add(surf);
		}
	}

	private void drawSkyPlanes() {
		gl.glDisable(GL_CULL_FACE);
		gl.glDepthMask(false);

		SECTOR skysector;
		if ((skysector = scanner.getLastSkySector(Heinum.SkyUpper)) != null) {
			int pal = skysector.ceilingpal;
			int shade = skysector.ceilingshade;
			int picnum = skysector.ceilingpicnum;

			drawSky(world.getQuad(), picnum, shade, pal, 0,
					transform.setToTranslation(cam.position.x, cam.position.y, cam.position.z - 100).scale(cam.far,
							cam.far, 1.0f));
		}

		if ((skysector = scanner.getLastSkySector(Heinum.SkyLower)) != null) {
			int pal = skysector.floorpal;
			int shade = skysector.floorshade;
			int picnum = skysector.floorpicnum;

			drawSky(world.getQuad(), picnum, shade, pal, 0,
					transform.setToTranslation(cam.position.x, cam.position.y, cam.position.z + 100).scale(cam.far,
							cam.far, 1.0f));
		}

		gl.glDepthMask(true);
		gl.glEnable(GL_CULL_FACE);
	}

	private void drawSector(VisibleSector sec) {
		int sectnum = sec.index;
		gotsector[sectnum >> 3] |= pow2char[sectnum & 7];

		if ((sec.secflags & 1) != 0) {
			rendering = Rendering.Floor.setIndex(sectnum);
			drawSurf(world.getFloor(sectnum), 0, null, sec.clipPlane);
		}

		if ((sec.secflags & 2) != 0) {
			rendering = Rendering.Ceiling.setIndex(sectnum);
			drawSurf(world.getCeiling(sectnum), 0, null, sec.clipPlane);
		}

		for (int w = 0; w < sec.walls.size; w++) {
			int flags = sec.wallflags.get(w);
			int z = sec.walls.get(w);
			rendering = Rendering.Wall.setIndex(z);
			drawSurf(world.getWall(z, sectnum), flags, null, sec.clipPlane);
			drawSurf(world.getUpper(z, sectnum), flags, null, sec.clipPlane);
			drawSurf(world.getLower(z, sectnum), flags, null, sec.clipPlane);
		}
	}

	public void drawSkySector(VisibleSector sec) {
		for (int w = 0; w < sec.skywalls.size; w++) {
			int z = sec.skywalls.get(w);
			GLSurface ceil = world.getParallaxCeiling(z);
			if (ceil != null) {
				drawSky(ceil, ceil.picnum, ceil.getShade(), ceil.getPal(), ceil.getMethod(), identity);
			}

			GLSurface floor = world.getParallaxFloor(z);
			if (floor != null) {
				drawSky(floor, floor.picnum, floor.getShade(), floor.getPal(), floor.getMethod(), identity);
			}
		}
	}

	private void drawSky(GLSurface surf, int picnum, int shade, int palnum, int method, Matrix4 worldTransform) {
		if (surf.count == 0)
			return;

		if (engine.getTile(picnum).getType() != AnimType.None)
			picnum += engine.animateoffs(picnum, 0);

		Tile pic = engine.getTile(picnum);
		if (!pic.isLoaded())
			engine.loadtile(picnum);

		if (!pic.isLoaded())
			method = 1; // invalid data, HOM

		engine.setgotpic(picnum);
		if (palookup[palnum] == null)
			palnum = 0;

		GLTile pth = bindSky(picnum, palnum, shade, method);
		if (pth != null) {
			Gdx.gl.glDisable(GL_BLEND);
			if ((method & 3) != 0)
				Gdx.gl.glEnable(GL_BLEND);

			manager.fog(false, 0, 0, 0, 0, 0);
			manager.transform(worldTransform);
			manager.frustum(null);

			surf.render(manager.getProgram());
		}
	}

	protected void drawSurf(GLSurface surf, int flags, Matrix4 worldTransform, Plane[] clipPlane) {
		if (surf == null)
			return;

		if (surf.count != 0 && (flags == 0 || (surf.visflag & flags) != 0)) {
			int picnum = surf.picnum;

			if (engine.getTile(picnum).getType() != AnimType.None)
				picnum += engine.animateoffs(picnum, 0);

			Tile pic = engine.getTile(picnum);
			if (!pic.isLoaded())
				engine.loadtile(picnum);

			int method = surf.getMethod();
			if (!pic.isLoaded()) {
				method = 1; // invalid data, HOM
			}

			engine.setgotpic(picnum);
			GLTile pth = bind(picnum, surf.getPal(), surf.getShade(), 0, method);
			if (pth != null) {
				int combvis = globalvisibility;
				int vis = surf.getVisibility();
				if (vis != 0)
					combvis = mulscale(globalvisibility, (vis + 16) & 0xFF, 4);

				if (pth.getPixelFormat() == PixelFormat.Pal8)
					((IndexedShader) manager.getProgram()).setVisibility((int) (-combvis / 64.0f));
				else {
					calcFog(surf.getPal(), surf.getShade(), combvis);
				}

				manager.color(1.0f, 1.0f, 1.0f, 1.0f);
				if (pth.isHighTile()) {
					int tsizy = 1;
					for (; tsizy < pic.getHeight(); tsizy += tsizy);
					if((pic.getWidth() / (float) pic.getHeight()) != (pth.getWidth() / (float) pth.getHeight())) {
						texture_transform.scale(1.0f, (tsizy * pth.getYScale()) / pth.getHeight());
					}
					manager.textureTransform(texture_transform, 0);

					if (defs != null && defs.texInfo != null) {
						float r = 1, g = 1, b = 1;
						if (pth.getPal() != surf.getPal()) {
							// apply tinting for replaced textures

							Palette p = defs.texInfo.getTints(surf.getPal());
							r *= p.r / 255.0f;
							g *= p.g / 255.0f;
							b *= p.b / 255.0f;
						}

						Palette pdetail = defs.texInfo.getTints(MAXPALOOKUPS - 1);
						if (pdetail.r != 255 || pdetail.g != 255 || pdetail.b != 255) {
							r *= pdetail.r / 255.0f;
							g *= pdetail.g / 255.0f;
							b *= pdetail.b / 255.0f;
						}
						manager.color(r, g, b, 1.0f);
					}
				}

				if (worldTransform == null)
					manager.transform(identity);
				else
					manager.transform(worldTransform);

				if (clipPlane != null && !inpreparemirror)
					manager.frustum(clipPlane);
				else
					manager.frustum(null);

				if ((method & 3) == 0)
					Gdx.gl.glDisable(GL_BLEND);
				else
					Gdx.gl.glEnable(GL_BLEND);

				surf.render(manager.getProgram());
			}
		}
	}

	protected void calcFog(int pal, int shade, float combvis) {
		float start = FULLVIS_BEGIN;
		float end = FULLVIS_END;
		if (combvis != 0) {
			if (shade >= numshades - 1) {
				start = -1;
				end = 0.001f;
			} else {
				start = (shade > 0) ? 0 : -(FOGDISTCONST * shade) / combvis;
				end = (FOGDISTCONST * (numshades - 1 - shade)) / combvis;
			}
		}

		float r = (palookupfog[pal][0] / 63.f);
		float g = (palookupfog[pal][1] / 63.f);
		float b = (palookupfog[pal][2] / 63.f);

		manager.fog(true, start, end, r, g, b);
	}

	@Override
	public void clearview(int dacol) {
		gl.glClearColor(curpalette.getRed(dacol) / 255.0f, //
				curpalette.getGreen(dacol) / 255.0f, //
				curpalette.getBlue(dacol) / 255.0f, 0); //
		gl.glClear(GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void changepalette(byte[] palette) {
		textureCache.changePalette(palette);
	}

	@Override
	public void nextpage() {
		clearStatus = false;
		if (world != null)
			world.nextpage();
		orphoRen.nextpage();
		manager.reset();
		textureCache.unbind();

		omdtims = mdtims;
		mdtims = engine.getticks();

		for (int i = 0; i < MAXSPRITES; i++) {
			if (mdpause != 0) {
				SpriteAnim sprext = defs.mdInfo.getAnimParams(i);
				if (sprext == null)
					continue;

				boolean isAnimationDisabled = false;
				Spriteext inf = defs.mapInfo.getSpriteInfo(i);
				if (inf != null)
					isAnimationDisabled = inf.isAnimationDisabled();

				if ((mdpause != 0 && sprext.mdanimtims != 0) || isAnimationDisabled)
					sprext.mdanimtims += mdtims - omdtims;
			}
		}

		beforedrawrooms = 1;

    }

	@Override
	public void setview(int x1, int y1, int x2, int y2) {
		orphoRen.resize(x2, y2);
	}

	@Override
	public void rotatesprite(int sx, int sy, int z, int a, int picnum, int dashade, int dapalnum, int dastat, int cx1,
			int cy1, int cx2, int cy2) {
		rendering = Rendering.Tile.setIndex(picnum);
		set2dview();
		orphoRen.rotatesprite(sx, sy, z, a, picnum, dashade, dapalnum, dastat, cx1, cy1, cx2, cy2);
	}

	@Override
	public void drawmapview(int dax, int day, int zoome, int ang) {
		set2dview();
		orphoRen.drawmapview(dax, day, zoome, ang);
	}

	@Override
	public void drawoverheadmap(int cposx, int cposy, int czoom, short cang) {
		set2dview();
		orphoRen.drawoverheadmap(cposx, cposy, czoom, cang);
	}

	@Override
	public void printext(TileFont font, int xpos, int ypos, char[] text, int col, int shade, Transparent bit,
			float scale) {
		rendering = Rendering.Tile.setIndex(0);
		set2dview();
		orphoRen.printext(font, xpos, ypos, text, col, shade, bit, scale);
	}

	@Override
	public void printext(int xpos, int ypos, int col, int backcol, char[] text, int fontsize, float scale) {
		rendering = Rendering.Tile.setIndex(0);
		set2dview();
		orphoRen.printext(xpos, ypos, col, backcol, text, fontsize, scale);
	}

	@Override
	public ByteBuffer getFrame(PixelFormat format, int xsiz, int ysiz) {
		if (pix32buffer != null)
			pix32buffer.clear();

		boolean reverse = false;
		if (ysiz < 0) {
			ysiz *= -1;
			reverse = true;
		}

		int byteperpixel = 3;
		int fmt = GL_RGB;
		if (BuildGdx.app.getPlatform() == Platform.Android) {
			byteperpixel = 4;
			fmt = GL_RGBA;
		}

		if (pix32buffer == null || pix32buffer.capacity() < xsiz * ysiz * byteperpixel)
			pix32buffer = BufferUtils.newByteBuffer(xsiz * ysiz * byteperpixel);
		gl.glPixelStorei(GL_PACK_ALIGNMENT, 1);
		gl.glReadPixels(0, ydim - ysiz, xsiz, ysiz, fmt, GL_UNSIGNED_BYTE, pix32buffer);

		if (format == PixelFormat.Rgb) {
			if (reverse) {
				int b1, b2 = 0;
				for (int p, x, y = 0; y < ysiz / 2; y++) {
					b1 = byteperpixel * (ysiz - y - 1) * xsiz;
					for (x = 0; x < xsiz; x++) {
						for (p = 0; p < byteperpixel; p++) {
							byte tmp = pix32buffer.get(b1 + p);
							pix32buffer.put(b1 + p, pix32buffer.get(b2 + p));
							pix32buffer.put(b2 + p, tmp);
						}
						b1 += byteperpixel;
						b2 += byteperpixel;
					}
				}
			}
			pix32buffer.rewind();
			return pix32buffer;
		} else if (format == PixelFormat.Pal8) {
			if (pix8buffer != null)
				pix8buffer.clear();
			if (pix8buffer == null || pix8buffer.capacity() < xsiz * ysiz)
				pix8buffer = BufferUtils.newByteBuffer(xsiz * ysiz);

			int base = 0, r, g, b;
			if (reverse) {
				for (int x, y = 0; y < ysiz; y++) {
					base = byteperpixel * (ysiz - y - 1) * xsiz;
					for (x = 0; x < xsiz; x++) {
						r = (pix32buffer.get(base++) & 0xFF) >> 2;
						g = (pix32buffer.get(base++) & 0xFF) >> 2;
						b = (pix32buffer.get(base++) & 0xFF) >> 2;
						pix8buffer.put(engine.getclosestcol(palette, r, g, b));
					}
				}
			} else {
				for (int i = 0; i < pix8buffer.capacity(); i++) {
					r = (pix32buffer.get(base++) & 0xFF) >> 2;
					g = (pix32buffer.get(base++) & 0xFF) >> 2;
					b = (pix32buffer.get(base++) & 0xFF) >> 2;
					if (byteperpixel == 4)
						base++; // Android
					pix8buffer.put(engine.getclosestcol(palette, r, g, b));
				}
			}

			pix8buffer.rewind();
			return pix8buffer;
		}

		return null;
	}

	@Override
	public byte[] screencapture(int newwidth, int newheight) {
		byte[] capture = new byte[newwidth * newheight];

		int xf = divscale(xdim, newwidth, 16);
		int yf = divscale(ydim, newheight, 16);

		ByteBuffer frame = getFrame(PixelFormat.Rgb, xdim, -ydim);

		int byteperpixel = 3;
		if (BuildGdx.app.getType() == ApplicationType.Android)
			byteperpixel = 4;

		int base;
		for (int fx, fy = 0; fy < newheight; fy++) {
			base = mulscale(fy, yf, 16) * xdim;
			for (fx = 0; fx < newwidth; fx++) {
				int pos = base + mulscale(fx, xf, 16);
				frame.position(byteperpixel * pos);
				int r = (frame.get() & 0xFF) >> 2;
				int g = (frame.get() & 0xFF) >> 2;
				int b = (frame.get() & 0xFF) >> 2;

				capture[newheight * fx + fy] = engine.getclosestcol(palette, r, g, b);
			}
		}

		return capture;
	}

	@Override
	public void drawline256(int x1, int y1, int x2, int y2, int col) {
		set2dview();
		orphoRen.drawline256(x1, y1, x2, y2, col);
	}

	@Override
	public void settiltang(int tilt) {
		if (tilt == 0)
			gtang = 0.0f;
		else
			gtang = (float) Gameutils.AngleToDegrees(tilt);
	}

	@Override
	public void setDefs(DefScript defs) {
		this.textureCache.setTextureInfo(defs != null ? defs.texInfo : null);
		this.modelManager.setModelsInfo(defs != null ? defs.mdInfo : null);
		if (this.defs != null)
			gltexinvalidateall(GLInvalidateFlag.Uninit, GLInvalidateFlag.All);
		this.defs = defs;
	}

	@Override
	public TextureManager getTextureManager() {
		if (textureCache == null)
			textureCache = new TextureManager(engine, ExpandTexture.Vertical);
		return textureCache;
	}

	@Override
	public void enableIndexedShader(boolean enable) {
		if (isUseIndexedTextures != enable) {
			if (isInited)
				texturesUninit();

			clearskins(false);
			this.isUseIndexedTextures = enable;
		}
	}

	public void clearskins(boolean bit8only) {
		for (int i = MAXTILES - 1; i >= 0; i--) {
			modelManager.clearSkins(i, bit8only);
		}
	}

	@Override
	public void palfade(HashMap<String, FadeEffect> fades) { // TODO: to shader?
		if (orphoRen.isDrawing())
			orphoRen.end();

		gl.glDisable(GL_DEPTH_TEST);
		gl.glDisable(GL_TEXTURE_2D);

		gl.glEnable(GL_BLEND);

		set2dview();

		FadeShader shader = (FadeShader) manager.bind(Shader.FadeShader);

		palfadergb.draw(shader);
		if (fades != null) {
			Iterator<FadeEffect> it = fades.values().iterator();
			while (it.hasNext()) {
				FadeEffect obj = it.next();
				obj.draw(shader);
			}
		}

		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void preload(GLPreloadFlag... flags) {
		System.err.println("Preload");

		for (int f = 0; f < flags.length; f++) {
			switch (flags[f]) {
			case Models:
				for (int i = MAXTILES - 1; i >= 0; i--) {
					int pal = 0;
					modelManager.preload(i, pal, false);
				}
				break;
			case Other:
				if (world != null)
					world.dispose();
				world = new WorldMesh(engine);
				scanner.init();

				for (int i = 0; i < MAXSPRITES; i++) {
					removeSpriteCorr(i);
					SPRITE spr = sprite[i];
					if (spr == null || ((spr.cstat >> 4) & 3) != 1 || spr.statnum == MAXSTATUS)
						continue;

					addSpriteCorr(i);
				}
				break;
			}
		}
	}

	@Override
	public void precache(int dapicnum, int dapalnum, int datype) {
		if ((palookup[dapalnum] == null) && (dapalnum < (MAXPALOOKUPS - RESERVEDPALS)))
			return;

		textureCache.precache(getTexFormat(), dapicnum, dapalnum, datype);

		if (datype == 0)
			return;

		modelManager.preload(dapicnum, dapalnum, true);
	}

	@Override
	public void gltexapplyprops() {
		GLFilter filter = GLSettings.textureFilter.get();
		textureCache.setFilter(filter);
		int anisotropy = GLSettings.textureAnisotropy.get();
		for (int i = MAXTILES - 1; i >= 0; i--) {
			skycache.setFilter(i, filter, anisotropy);
		}

		modelManager.setTextureFilter(filter, GLSettings.textureAnisotropy.get());
	}

	@Override
	public void gltexinvalidateall(GLInvalidateFlag... flags) {
		if (flags.length == 0) {
			textureCache.invalidateall();
			clearskins(true);
			return;
		}

		for (int i = 0; i < flags.length; i++) {
			switch (flags[i]) {
			case Uninit:
				texturesUninit();
				break;
			case SkinsOnly:
				clearskins(true);
				break;
			case TexturesOnly:
			case IndexedTexturesOnly:
			case All:
					textureCache.invalidateall();
				break;
			case Palookup:
				for (int j = 0; j < MAXPALOOKUPS; j++) {
					textureCache.invalidatepalookup(j);
				}
				break;
			}
		}
	}

	//
	// invalidatetile
	// pal: pass -1 to invalidate all palettes for the tile, or >=0 for a particular
	// palette
	// how: pass -1 to invalidate all instances of the tile in texture memory, or a
	// bitfield
	// bit 0: opaque or masked (non-translucent) texture, using repeating
	// bit 1: ignored
	// bit 2: ignored (33% translucence, using repeating)
	// bit 3: ignored (67% translucence, using repeating)
	// bit 4: opaque or masked (non-translucent) texture, using clamping
	// bit 5: ignored
	// bit 6: ignored (33% translucence, using clamping)
	// bit 7: ignored (67% translucence, using clamping)
	// clamping is for sprites, repeating is for walls
	//

	@Override
	public void invalidatetile(int tilenume, int pal, int how) { // jfBuild
		int numpal, firstpal, np;
		int hp;

		PixelFormat fmt = textureCache.getFmt(tilenume);
		if (fmt == null)
			return;

		if (fmt == PixelFormat.Pal8) {
			numpal = 1;
			firstpal = 0;
		} else {
			if (pal < 0) {
				numpal = MAXPALOOKUPS;
				firstpal = 0;
			} else {
				numpal = 1;
				firstpal = pal % MAXPALOOKUPS;
			}
		}

		for (hp = 0; hp < 8; hp += 4) {
			if ((how & pow2long[hp]) == 0)
				continue;

			for (np = firstpal; np < firstpal + numpal; np++) {
				textureCache.invalidate(tilenume, np, textureCache.clampingMode(hp));
			}
		}
	}

	@Override
	public void setdrunk(float intensive) {
		if (intensive == 0) {
			drunk = false;
			drunkIntensive = 0;
		} else {
			drunk = true;
			drunkIntensive = intensive;
		}
	}

	@Override
	public float getdrunk() {
		return drunkIntensive;
	}

	protected GLTile bind(int dapicnum, int dapalnum, int dashade, int skybox, int method) {
		if (palookup[dapalnum] == null)
			dapalnum = 0;

		GLTile pth = textureCache.get(getTexFormat(), dapicnum, dapalnum, skybox, method);
		if (pth == null)
			return null;

		textureCache.bind(pth);
		if (manager.getShader() == null || isSkyShader() || pth.getPixelFormat() != manager.getPixelFormat()) {
			switchShader(pth.getPixelFormat() != PixelFormat.Pal8 ? Shader.RGBWorldShader : Shader.IndexedWorldShader);
		}
		setTextureParameters(pth, dapicnum, dapalnum, dashade, skybox, method);

		return pth;
	}

	protected GLTile bindSky(int dapicnum, int dapalnum, int dashade, int method) {
		if (palookup[dapalnum] == null)
			dapalnum = 0;

		GLTile pth = getSkyTexture(getTexFormat(), dapicnum, dapalnum);
		if (pth == null)
			return null;

		textureCache.bind(pth);
		if (manager.getShader() == null || !isSkyShader() || pth.getPixelFormat() != manager.getPixelFormat()) {
			switchShader(pth.getPixelFormat() != PixelFormat.Pal8 ? Shader.RGBSkyShader : Shader.IndexedSkyShader);
		}
		setTextureParameters(pth, dapicnum, dapalnum, dashade, 0, 0);
		return pth;
	}

	public void setTextureParameters(GLTile tile, int tilenum, int pal, int shade, int skybox, int method) {
		float alpha = 1.0f;
		switch (method & 3) {
		case 2:
			alpha = TRANSLUSCENT1;
			break;
		case 3:
			alpha = TRANSLUSCENT2;
			break;
		}

		if (tilenum != -1 && !engine.getTile(tilenum).isLoaded())
			alpha = 0.01f; // Hack to update Z-buffer for invalid mirror textures

		if (tile.getPixelFormat() == TileData.PixelFormat.Pal8) {
			manager.textureTransform(texture_transform.idt(), 0);
			manager.textureParams8(pal, shade, alpha, (method & 3) == 0 || !textureCache.alphaMode(method));
		} else {
			texture_transform.idt();
			if (tile.isHighTile() && ((tile.getHiresXScale() != 1.0f) || (tile.getHiresYScale() != 1.0f))
					&& Rendering.Skybox.getIndex() == 0) {
				texture_transform.scale(tile.getHiresXScale(), tile.getHiresYScale());
			}
			manager.textureTransform(texture_transform, 0);

			if (GLInfo.multisample != 0 && GLSettings.useHighTile.get() && Rendering.Skybox.getIndex() == 0) {
            }

			float r, b, g;
			float fshade = min(max(shade * 1.04f, 0), numshades);
			r = g = b = (numshades - fshade) / numshades;

			if (defs != null && tile.isHighTile() && defs.texInfo != null) {
				if (tile.getPal() != pal) {
					// apply tinting for replaced textures

					Palette p = defs.texInfo.getTints(pal);
					r *= p.r / 255.0f;
					g *= p.g / 255.0f;
					b *= p.b / 255.0f;
				}

				Palette pdetail = defs.texInfo.getTints(MAXPALOOKUPS - 1);
				if (pdetail.r != 255 || pdetail.g != 255 || pdetail.b != 255) {
					r *= pdetail.r / 255.0f;
					g *= pdetail.g / 255.0f;
					b *= pdetail.b / 255.0f;
				}
			}

			manager.color(r, g, b, alpha);
		}
	}

	public void setFieldOfView(final float fov) {
		if (cam != null) {
			cam.setFieldOfView(fov);
		} else {
			BuildGdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					cam.setFieldOfView(fov);
				}
			});
		}
	}

	@Override
	public void addSpriteCorr(int snum) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSpriteCorr(int snum) {
		// TODO Auto-generated method stub

	}

	@Override
	public void completemirror() {
		inpreparemirror = false;
	}

	private boolean spritewallfront(SPRITE s, int w) {
		if (s == null)
			return false;

		WALL wal = wall[w];
		int x1 = wal.x;
		int y1 = wal.y;
		wal = wall[wal.point2];
		return (dmulscale(wal.x - x1, s.y - y1, -(s.x - x1), wal.y - y1, 32) >= 0);
	}

	protected void set2dview() {
		if (gloy1 != -1) {
			gl.glViewport(0, 0, xdim, ydim);
			orphoRen.resize(xdim, ydim);
		}
		gloy1 = -1;
	}

	protected void resizeglcheck() {
		if ((glox1 != windowx1) || (gloy1 != windowy1) || (glox2 != windowx2) || (gloy2 != windowy2)) {
			glox1 = windowx1;
			gloy1 = windowy1;
			glox2 = windowx2;
			gloy2 = windowy2;

			gl.glViewport(windowx1, ydim - (windowy2 + 1), windowx2 - windowx1 + 1, windowy2 - windowy1 + 1);

			cam.viewportWidth = windowx2;
			cam.viewportHeight = windowy2;
		}
	}

	protected GDXOrtho allocOrphoRenderer(IOverheadMapSettings settings) {
		return new GDXOrtho(this, settings);
	}

	protected int[] getMirrorTextures() {
		return null;
	}

	@Override
	public RenderType getType() {
		return RenderType.PolyGDX;
	}

	@Override
	public PixelFormat getTexFormat() {
		return isUseIndexedTextures ? PixelFormat.Pal8 : PixelFormat.Rgba;
	}

	@Override
	public boolean isInited() {
		return isInited;
	}

	protected ShaderProgram switchShader(Shader shader) {
		ShaderProgram out = manager.bind(shader);
		manager.mirror(inpreparemirror);
		manager.prepare(cam);
		// TODO: add texture transform here

		return out;
	}

	protected final GLTileArray skycache = new GLTileArray(MAXTILES);

	protected GLTile getSkyTexture(PixelFormat fmt, int picnum, int palnum) {
		if (!engine.getTile(picnum).hasSize())
			return textureCache.get(getTexFormat(), picnum, palnum, 0, 0);

		GLTile tile = skycache.get(picnum, palnum, false, 0);
		boolean useMipMaps = GLSettings.textureFilter.get().mipmaps;

		if (tile != null /* && tile.getPixelFormat() == fmt */) {
			if (tile.isInvalidated()) {
				tile.setInvalidated(false);

				TileData data = loadPic(fmt, picnum, palnum);
				tile.update(data, palnum, useMipMaps);
			}
		} else {

            TileData data = loadPic(fmt, picnum, palnum);
			if (data == null)
				return null;

			skycache.add(textureCache.newTile(data, fmt == PixelFormat.Pal8 ? 0 : palnum, useMipMaps), picnum);
		}

		return tile;
	}

	protected TileData loadPic(PixelFormat fmt, int picnum, int palnum) {
		short[] dapskyoff = zeropskyoff;
		int dapskybits = pskybits;

		if (dapskybits < 0)
			dapskybits = 0;

		Tile tile = engine.getTile(picnum);
		TileAtlas sky = new TileAtlas(fmt, tile.getWidth() * (1 << dapskybits), tile.getHeight(), tile.getWidth(),
				tile.getHeight(), false);
		for (int i = 0; i < (1 << dapskybits); i++) {
			int pic = dapskyoff[i] + picnum;
			TileData dat;
			if (fmt == PixelFormat.Pal8)
				dat = new IndexedTileData(engine.getTile(pic), false, false, 0);
			else
				dat = new RGBTileData(engine.getTile(pic), palnum, false, false, 0);
			sky.addTile(pic, dat);
		}

		return sky.atlas.get(0);
	}

	protected boolean isSkyShader() {
		return manager.getShader() == Shader.RGBSkyShader || manager.getShader() == Shader.IndexedSkyShader;
	}

	// Debug 2.5D renderer

}
