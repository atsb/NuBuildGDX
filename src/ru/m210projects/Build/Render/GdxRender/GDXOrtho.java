package ru.m210projects.Build.Render.GdxRender;

import static com.badlogic.gdx.graphics.GL20.*;
import static com.badlogic.gdx.graphics.GL20.GL_CW;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_TEST;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Engine.MAXSPRITESONSCREEN;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.TRANSLUSCENT1;
import static ru.m210projects.Build.Engine.TRANSLUSCENT2;
import static ru.m210projects.Build.Engine.beforedrawrooms;
import static ru.m210projects.Build.Engine.curpalette;
import static ru.m210projects.Build.Engine.globalang;
import static ru.m210projects.Build.Engine.globalpal;
import static ru.m210projects.Build.Engine.globalposx;
import static ru.m210projects.Build.Engine.globalposy;
import static ru.m210projects.Build.Engine.globalposz;
import static ru.m210projects.Build.Engine.globalshade;
import static ru.m210projects.Build.Engine.gotsector;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.numshades;
import static ru.m210projects.Build.Engine.pSmallTextfont;
import static ru.m210projects.Build.Engine.pTextfont;
import static ru.m210projects.Build.Engine.palookup;
import static ru.m210projects.Build.Engine.pow2char;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.show2dsector;
import static ru.m210projects.Build.Engine.show2dsprite;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.tsprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.windowx1;
import static ru.m210projects.Build.Engine.windowx2;
import static ru.m210projects.Build.Engine.windowy1;
import static ru.m210projects.Build.Engine.windowy2;
import static ru.m210projects.Build.Engine.wx1;
import static ru.m210projects.Build.Engine.wx2;
import static ru.m210projects.Build.Engine.wy1;
import static ru.m210projects.Build.Engine.wy2;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.xdimen;
import static ru.m210projects.Build.Engine.xdimenscale;
import static ru.m210projects.Build.Engine.xyaspect;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Engine.ydimen;
import static ru.m210projects.Build.Engine.yxaspect;
import static ru.m210projects.Build.Gameutils.AngleToRadians;
import static ru.m210projects.Build.Gameutils.buildAngleToDegrees;
import static ru.m210projects.Build.Gameutils.buildAngleToRadians;
import static ru.m210projects.Build.Gameutils.isValidSector;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Pragmas.dmulscale;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.NumberUtils;

import ru.m210projects.Build.Gameutils;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Render.IOverheadMapSettings.MapView;
import ru.m210projects.Build.Render.IOverheadMapSettings;
import ru.m210projects.Build.Render.OrphoRenderer;
import ru.m210projects.Build.Render.Renderer.Transparent;
import ru.m210projects.Build.Render.GdxRender.WorldMesh.GLSurface;
import ru.m210projects.Build.Render.GdxRender.Shaders.ShaderManager;
import ru.m210projects.Build.Render.GdxRender.Shaders.ShaderManager.Shader;
import ru.m210projects.Build.Render.TextureHandle.DummyTileData;
import ru.m210projects.Build.Render.TextureHandle.GLTile;
import ru.m210projects.Build.Render.TextureHandle.IndexedShader;
import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;
import ru.m210projects.Build.Render.Types.GL10;
import ru.m210projects.Build.Render.Types.Hudtyp;
import ru.m210projects.Build.Render.Types.Palette;
import ru.m210projects.Build.Render.Types.Tile2model;
import ru.m210projects.Build.Settings.GLSettings;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.Tile.AnimType;
import ru.m210projects.Build.Types.TileFont;
import ru.m210projects.Build.Types.TileFont.FontType;
import ru.m210projects.Build.Types.WALL;

public class GDXOrtho extends OrphoRenderer {

	protected Mesh mesh;
	protected Mesh linesMesh;
	protected final float[] vertices;
	protected int idx = 0;
	protected GLTile lastTexture = null;
	protected GLTile lineTile = null;
	protected int lastType = GL20.GL_TRIANGLES;
	protected float invTexWidth = 0, invTexHeight = 0;
	protected boolean drawing = false;
	protected final Matrix4 projectionMatrix = new Matrix4();
	protected boolean blendingDisabled = false;
	protected float color = Color.WHITE_FLOAT_BITS;
	protected final GDXRenderer parent;
	protected int cx1, cy1, cx2, cy2;

	protected ShaderManager manager;
	private SPRITE hudsprite;

	private final int maxSpriteCount = 128;

	public GDXOrtho(GDXRenderer parent, IOverheadMapSettings settings) {
		super(parent.engine, settings);
		this.parent = parent;
		this.manager = parent.manager;

		int VERTEX_SIZE = 2 + 1 + 2;
		int SPRITE_SIZE = 4 * VERTEX_SIZE;
		this.vertices = new float[maxSpriteCount * SPRITE_SIZE];
	}

	@Override
	public void init() {
		int size = maxSpriteCount;

		this.mesh = new Mesh(false, size * 4, size * 6,
				new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
				new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
				new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

		this.linesMesh = new Mesh(false, size * 2, 0,
				new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
				new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE));

		resize(xdim, ydim);

		int len = size * 6;
		short[] indices = new short[len];
		short j = 0;
		for (int i = 0; i < len; i += 6, j += 4) {
			indices[i] = j;
			indices[i + 1] = (short) (j + 1);
			indices[i + 2] = (short) (j + 2);
			indices[i + 3] = (short) (j + 2);
			indices[i + 4] = (short) (j + 3);
			indices[i + 5] = j;
		}
		mesh.setIndices(indices);

		this.lineTile = allocLineTile();
	}

	@Override
	public void uninit() {
		mesh.dispose();
		linesMesh.dispose();
		lineTile.dispose();
//		bitmapShader.dispose();
		idx = 0;
		drawing = false;
	}

	@Override
	public void printext(TileFont font, int xpos, int ypos, char[] text, int col, int shade, Transparent bit,
			float scale) {
		if(col < 0)
			return;

		if (font.type == FontType.Tilemap) {
			if (palookup[col] == null)
				col = 0;

			int nTile = (Integer) font.ptr;
			if (!engine.getTile(nTile).isLoaded() && engine.loadtile(nTile) == null)
				return;
		}

		GLTile atlas = font.getGL(parent.textureCache, parent.getTexFormat(), col);
		if (atlas == null)
			return;

		xpos <<= 16;
		ypos <<= 16;

		BuildGdx.gl.glDisable(GL_CULL_FACE);
		BuildGdx.gl.glDisable(GL_DEPTH_TEST);
		Shader shader = ((font.type == FontType.Tilemap)
				? (atlas.getPixelFormat() != PixelFormat.Pal8 ? Shader.RGBWorldShader : Shader.IndexedWorldShader)
				: Shader.BitmapShader);
		if (!isDrawing())
			begin(shader);

		setType(GL20.GL_TRIANGLES);
		switchShader(shader);
		if (font.type == FontType.Tilemap) {
			float alpha = 1.0f;
			if (bit == Transparent.Bit1)
				alpha = TRANSLUSCENT1;
			if (bit == Transparent.Bit2)
				alpha = TRANSLUSCENT2;

			if (atlas.getPixelFormat() != PixelFormat.Pal8) {
				float sh = (numshades - min(max(shade, 0), numshades)) / (float) numshades;
				setColor(sh, sh, sh, alpha);
			} else
				switchTextureParams(col, shade, alpha, false);
		} else
			setColor(curpalette.getRed(col) / 255.0f, curpalette.getGreen(col) / 255.0f,
					curpalette.getBlue(col) / 255.0f, 1.0f);
		enableBlending();

		int oxpos = xpos;
		int c = 0, line = 0, yoffs;
		float tx, ty;
		int df = font.sizx / font.cols;
		while (c < text.length && text[c] != '\0') {
			if (text[c] == '\n') {
				text[c] = 0;
				line += 1;
				xpos = oxpos - (int) (scale * font.charsizx);
			}
			if (text[c] == '\r')
				text[c] = 0;
			yoffs = (int) (scale * line * font.charsizy);

			tx = (text[c] % font.cols) * df;
			ty = (text[c] / font.cols) * df;

			draw(atlas, xpos, ypos, font.charsizx, font.charsizy, 0, -yoffs, tx, ty, font.charsizx, font.charsizy, 0,
					(int) (scale * 65536), 8, 0, 0, xdim - 1, ydim - 1);

			xpos += scale * (font.charsizx << 16);
			c++;
		}
		BuildGdx.gl.glDepthMask(true); // re-enable writing to the z-buffer
	}

	@Override
	public void printext(int xpos, int ypos, int col, int backcol, char[] text, int fontsize, float scale) {
		printext(fontsize == 0 ? pTextfont : pSmallTextfont, xpos, ypos, text, col, 0, Transparent.None, scale);
	}

	@Override
	public void drawline256(int x1, int y1, int x2, int y2, int col) {
		float sx1 = x1 / 4096.0f;
		float sy1 = y1 / 4096.0f;
		float sx2 = x2 / 4096.0f;
		float sy2 = y2 / 4096.0f;

		if (sx1 < 0 && sx2 < 0 || sx1 > xdim && sx2 > xdim)
			return;

		if (sy1 < 0 && sy2 < 0 || sy1 > ydim && sy2 > ydim)
			return;

		col = palookup[0][col] & 0xFF;

		Gdx.gl.glDisable(GL_CULL_FACE);
		Gdx.gl.glDisable(GL_DEPTH_TEST);
		if (!isDrawing())
			begin(Shader.BitmapShader);

		setType(GL20.GL_LINES);
		switchShader(Shader.BitmapShader);
		switchTexture(lineTile);
		setColor(curpalette.getRed(col) / 255.0f, curpalette.getGreen(col) / 255.0f, curpalette.getBlue(col) / 255.0f,
				1.0f);
		disableBlending();
		if (idx >= 256)
			flush();

		float color = this.color;
		int idx = this.idx;
		float[] vertices = this.vertices;

		vertices[idx + 0] = sx1;
		vertices[idx + 1] = sy1;
		vertices[idx + 2] = color;

		vertices[idx + 3] = sx2;
		vertices[idx + 4] = sy2;
		vertices[idx + 5] = color;

		this.idx = idx + 6;
	}

	@Override
	public void rotatesprite(int sx, int sy, int z, int a, int picnum, int dashade, int dapalnum, int dastat, int cx1,
			int cy1, int cx2, int cy2) {
		if (!Gameutils.isValidTile(picnum))
			return;
		if ((cx1 > cx2) || (cy1 > cy2))
			return;
		if (z <= 16)
			return;

		if (GLSettings.useModels.get() && parent.defs != null && parent.defs.mdInfo.getHudInfo(picnum, dastat) != null
				&& parent.defs.mdInfo.getHudInfo(picnum, dastat).angadd != 0) {
			Tile2model entry = parent.defs != null ? parent.defs.mdInfo.getParams(picnum) : null;
			if (entry != null && entry.model != null && entry.framenum >= 0) {
				if(dorotatesprite3d(sx, sy, z, a, picnum, dashade, dapalnum, dastat, cx1, cy1, cx2, cy2))
					return;
			}
		}

		if (engine.getTile(picnum).getType() != AnimType.None)
			picnum += engine.animateoffs(picnum, 0xC000);

		Tile pic = engine.getTile(picnum);
		if (!pic.hasSize())
			return;

		int method = 0;
		if ((dastat & 64) == 0) {
			method = 1;
			if ((dastat & 1) != 0) {
				if ((dastat & 32) == 0)
					method = 2;
				else
					method = 3;
			}
		} else
			method |= 256; // non-transparent 255 color
		method |= 4; // Use OpenGL clamping - dorotatesprite never repeats

		int xsiz = pic.getWidth();
		int ysiz = pic.getHeight();

		int xoff = 0, yoff = 0;
		if ((dastat & 16) == 0) {
			xoff = pic.getOffsetX() + (xsiz >> 1);
			yoff = pic.getOffsetY() + (ysiz >> 1);
		}

		if ((dastat & 4) != 0)
			yoff = ysiz - yoff;

		if (picnum >= MAXTILES)
			picnum = 0;
		if (palookup[dapalnum & 0xFF] == null)
			dapalnum = 0;

		engine.setgotpic(picnum);
		if (!pic.isLoaded())
			engine.loadtile(picnum);

		GLTile pth = parent.textureCache.get(parent.getTexFormat(), picnum, dapalnum, 0, method);
		if (pth == null)
			return;

		pth.bind();
		if (((method & 3) == 0))
			disableBlending();
		else
			enableBlending();

		float alpha = 1.0f;
		switch (method & 3) {
		case 2:
			alpha = TRANSLUSCENT1;
			break;
		case 3:
			alpha = TRANSLUSCENT2;
			break;
		}

		Gdx.gl.glDisable(GL_CULL_FACE);
		Gdx.gl.glDisable(GL_DEPTH_TEST);

		Shader shader = pth.getPixelFormat() != PixelFormat.Pal8 ? Shader.RGBWorldShader : Shader.IndexedWorldShader;
		if (!isDrawing())
			begin(shader);

		switchShader(shader);
		setType(GL20.GL_TRIANGLES);
		setViewport(cx1, cy1, cx2, cy2);
		if (pth.getPixelFormat() != PixelFormat.Pal8) {
			float shade = (numshades - min(max(dashade, 0), numshades)) / (float) numshades;
			float r = shade, g = shade, b = shade;

			if (pth.isHighTile()) {
				if (parent.defs != null && parent.defs.texInfo != null) {
					if (pth.getPal() != dapalnum) {
						// apply tinting for replaced textures

						Palette p = parent.defs.texInfo.getTints(dapalnum);
						r *= p.r / 255.0f;
						g *= p.g / 255.0f;
						b *= p.b / 255.0f;
					}

					Palette pdetail = parent.defs.texInfo.getTints(MAXPALOOKUPS - 1);
					if (pdetail.r != 255 || pdetail.g != 255 || pdetail.b != 255) {
						r *= pdetail.r / 255.0f;
						g *= pdetail.g / 255.0f;
						b *= pdetail.b / 255.0f;
					}
				}
			}

			setColor(r, g, b, alpha);
		} else
			switchTextureParams(dapalnum, dashade, alpha, blendingDisabled || (method & 256) != 0);

		draw(pth, sx, sy, xsiz, ysiz, xoff, yoff, a, z, dastat, cx1, cy1, cx2, cy2);
	}

	public boolean dorotatesprite3d(int sx, int sy, int z, int a, int picnum, int dashade, int dapalnum, int dastat, int cx1,
			int cy1, int cx2, int cy2) {

		Hudtyp hudInfo;
		if (parent.defs == null
				|| ((hudInfo = parent.defs.mdInfo.getHudInfo(picnum, dastat)) != null && (hudInfo.flags & 1) != 0))
			return true; // "HIDE" is specified in DEF

		if (isDrawing())
			end();

		float yaw = (float) AngleToRadians(globalang);
		float gcosang = MathUtils.cos(yaw);
		float gsinang = MathUtils.sin(yaw);

		float x1 = hudInfo.xadd;
		float y1 = hudInfo.yadd;
		float z1 = hudInfo.zadd;

		if ((hudInfo.flags & 2) == 0) { // "NOBOB" is specified in DEF
			float fx = sx * (1.0f / 65536.0f);
			float fy = sy * (1.0f / 65536.0f);

			if ((dastat & 16) != 0) {
				Tile pic = engine.getTile(picnum);

				int xsiz = pic.getWidth();
				int ysiz = pic.getHeight();
				int xoff = pic.getOffsetX() + (xsiz >> 1);
				int yoff = pic.getOffsetY() + (ysiz >> 1);

				float d = z / (65536.0f * 16384.0f);
				float cosang, sinang;
				float cosang2 = cosang = sintable[(a + 512) & 2047] * d;
				float sinang2 = sinang = sintable[a & 2047] * d;
				if ((dastat & 2) != 0 || ((dastat & 8) == 0)) { // Don't aspect unscaled perms
					d = xyaspect / 65536.0f;
					cosang2 *= d;
					sinang2 *= d;
				}

				fx += -xoff * cosang2 + yoff * sinang2;
				fy += -xoff * sinang - yoff * cosang;
			}

			if ((dastat & 2) == 0) {
				x1 += fx / (xdim << 15) - 1.0f; // -1: left of screen, +1: right of screen
				y1 += fy / (ydim << 15) - 1.0f; // -1: top of screen, +1: bottom of screen
			} else {
				x1 += fx / 160.0f - 1.0f; // -1: left of screen, +1: right of screen
				y1 += fy / 100.0f - 1.0f; // -1: top of screen, +1: bottom of screen
			}
		}

		if ((dastat & 4) != 0) {
			x1 = -x1;
			y1 = -y1;
		}

		if (hudsprite == null)
			hudsprite = new SPRITE();
		hudsprite.reset((byte) 0);

		hudsprite.ang = (short) (hudInfo.angadd + globalang);
		hudsprite.xrepeat = hudsprite.yrepeat = 32;

		hudsprite.x = (int) ((gcosang * z1 - gsinang * x1) * 800.0f + globalposx);
		hudsprite.y = (int) ((gsinang * z1 + gcosang * x1) * 800.0f + globalposy);
		hudsprite.z = (int) (globalposz + y1 * 16384.0f * 0.8f);

		hudsprite.picnum = (short) picnum;
		hudsprite.shade = (byte) dashade;
		hudsprite.pal = (short) dapalnum;
		hudsprite.owner = (short) MAXSPRITES;
		hudsprite.cstat = (short) ((dastat & 1) + ((dastat & 32) << 4) + (dastat & 4) << 1);

		if ((dastat & 10) == 2)
			parent.resizeglcheck();
		else
			parent.set2dview();

		if ((hudInfo.flags & 8) != 0) // NODEPTH flag
			BuildGdx.gl.glDisable(GL_DEPTH_TEST);
		else {
			BuildGdx.gl.glEnable(GL_DEPTH_TEST);
			BuildGdx.gl.glClear(GL_DEPTH_BUFFER_BIT);
		}

		BuildCamera cam = parent.cam;

		float aspect = xdim / (float) (ydim);
		float f = 1.0f;
		if (hudInfo.fov != -1)
			f = hudInfo.fov / 420.0f;
		cam.projection.setToProjection(cam.near, cam.far, 90 * f, aspect);
		cam.view.setToLookAt(cam.direction.set(gcosang, gsinang, 0), cam.up.set(0,0,(dastat & 4) != 0 ? 1 : -1)).translate(-cam.position.x, -cam.position.y, -cam.position.z);
		cam.combined.set(cam.projection);
		Matrix4.mul(cam.combined.val, cam.view.val);

		return parent.mdR.mddraw(parent.modelManager.getModel(picnum, dapalnum), hudsprite);
	}

	public void draw(GLTile tex, int sx, int sy, int sizx, int sizy, int xoffset, int yoffset, int angle, int z,
			int dastat, int cx1, int cy1, int cx2, int cy2) {
		this.draw(tex, sx, sy, sizx, sizy, xoffset, yoffset, 0.0f, 0.0f, sizx, sizy, angle, z, dastat, cx1, cy1, cx2,
				cy2);
	}

	@Override
	public void nextpage() {
		if (isDrawing())
			end();
	}

	protected void drawoverheadline(int w, int cposx, int cposy, float cos, float sin, int col) {
		if (col < 0)
			return;

		WALL wal = wall[w];

		int ox = cposx - mapSettings.getWallX(w);
		int oy = cposy - mapSettings.getWallY(w);
		float x1 = ox * cos - oy * sin + xdim * 2048;
		float y1 = ox * sin + oy * cos + ydim * 2048;

		ox = cposx - mapSettings.getWallX(wal.point2);
		oy = cposy - mapSettings.getWallY(wal.point2);

		float x2 = ox * cos - oy * sin + xdim * 2048;
		float y2 = ox * sin + oy * cos + ydim * 2048;

		drawline256((int) x1, (int) y1, (int) x2, (int) y2, col);
	}

	@Override
	public void drawoverheadmap(int cposx, int cposy, int czoom, short cang) {
		float cos = (float) Math.cos((512 - cang) * buildAngleToRadians) * czoom / 4.0f;
		float sin = (float) Math.sin((512 - cang) * buildAngleToRadians) * czoom / 4.0f;

		for (int i = 0; i < numsectors; i++) {
			if ((!mapSettings.isFullMap() && (show2dsector[i >> 3] & (1 << (i & 7))) == 0)
					|| !Gameutils.isValidSector(i))
				continue;

			SECTOR sec = sector[i];
			if (!Gameutils.isValidWall(sec.wallptr) || sec.wallnum < 3)
				continue;

			int walnum = sec.wallptr;
			for (int j = 0; j < sec.wallnum; j++, walnum++) {
				if (!Gameutils.isValidWall(walnum) || !Gameutils.isValidWall(wall[walnum].point2))
					continue;

				WALL wal = wall[walnum];
				if (mapSettings.isShowRedWalls() && Gameutils.isValidWall(wal.nextwall)) {
					if (Gameutils.isValidSector(wal.nextsector)) {
						if (mapSettings.isWallVisible(walnum, i))
							drawoverheadline(walnum, cposx, cposy, cos, sin, mapSettings.getWallColor(walnum, i));
					}
				}

				if (wal.nextwall >= 0)
					continue;

				Tile pic = engine.getTile(wal.picnum);
				if (!pic.hasSize())
					continue;

				drawoverheadline(walnum, cposx, cposy, cos, sin, mapSettings.getWallColor(walnum, i));
			}
		}

		// Draw sprites
		if (mapSettings.isShowSprites(MapView.Lines)) {
			for (int i = 0; i < numsectors; i++) {
				if (!mapSettings.isFullMap() && (show2dsector[i >> 3] & (1 << (i & 7))) == 0)
					continue;

				for (int j = headspritesect[i]; j >= 0; j = nextspritesect[j]) {
					SPRITE spr = sprite[j];

					if ((spr.cstat & 0x8000) != 0 || spr.xrepeat == 0 || spr.yrepeat == 0
							|| !mapSettings.isSpriteVisible(MapView.Lines, j))
						continue;

					switch (spr.cstat & 48) {
					case 0:
						if (((gotsector[i >> 3] & (1 << (i & 7))) > 0) && (czoom > 96)) {
							int ox = cposx - mapSettings.getSpriteX(j);
							int oy = cposy - mapSettings.getSpriteY(j);
							float dx = ox * cos - oy * sin;
							float dy = ox * sin + oy * cos;
							int daang = (spr.ang - cang) & 0x7FF;
							int nZoom = czoom * spr.yrepeat;
							int sx = (int) (dx + xdim * 2048);
							int sy = (int) (dy + ydim * 2048);

							rotatesprite(sx * 16, sy * 16, nZoom, (short) daang, spr.picnum, spr.shade, spr.pal,
									(spr.cstat & 2) >> 1, wx1, wy1, wx2, wy2);
						}
						break;
					case 16: {
						Tile pic = engine.getTile(spr.picnum);
						int x1 = mapSettings.getSpriteX(j);
						int y1 = mapSettings.getSpriteY(j);
						byte xoff = (byte) (pic.getOffsetX() + spr.xoffset);
						if ((spr.cstat & 4) > 0)
							xoff = (byte) -xoff;

						int dax = sintable[spr.ang & 2047] * spr.xrepeat;
						int day = sintable[(spr.ang + 1536) & 2047] * spr.xrepeat;
						int k = (pic.getWidth() >> 1) + xoff;
						x1 -= mulscale(dax, k, 16);
						int x2 = x1 + mulscale(dax, pic.getWidth(), 16);
						y1 -= mulscale(day, k, 16);
						int y2 = y1 + mulscale(day, pic.getWidth(), 16);

						int ox = cposx - x1;
						int oy = cposy - y1;
						x1 = (int) (ox * cos - oy * sin) + (xdim << 11);
						y1 = (int) (ox * sin + oy * cos) + (ydim << 11);

						ox = cposx - x2;
						oy = cposy - y2;
						x2 = (int) (ox * cos - oy * sin) + (xdim << 11);
						y2 = (int) (ox * sin + oy * cos) + (ydim << 11);

						int col = mapSettings.getSpriteColor(j);
						if (col < 0)
							break;

						drawline256(x1, y1, x2, y2, col);
					}
						break;
					case 32: {
						Tile pic = engine.getTile(spr.picnum);
						byte xoff = (byte) (pic.getOffsetX() + spr.xoffset);
						byte yoff = (byte) (pic.getOffsetY() + spr.yoffset);
						if ((spr.cstat & 4) > 0)
							xoff = (byte) -xoff;
						if ((spr.cstat & 8) > 0)
							yoff = (byte) -yoff;

						int cosang = sintable[(spr.ang + 512) & 2047];
						int sinang = sintable[spr.ang & 2047];

						int dax = ((pic.getWidth() >> 1) + xoff) * spr.xrepeat;
						int day = ((pic.getHeight() >> 1) + yoff) * spr.yrepeat;
						int x1 = mapSettings.getSpriteX(j) + dmulscale(sinang, dax, cosang, day, 16);
						int y1 = mapSettings.getSpriteY(j) + dmulscale(sinang, day, -cosang, dax, 16);
						int l = pic.getWidth() * spr.xrepeat;
						int x2 = x1 - mulscale(sinang, l, 16);
						int y2 = y1 + mulscale(cosang, l, 16);
						l = pic.getHeight() * spr.yrepeat;
						int k = -mulscale(cosang, l, 16);
						int x3 = x2 + k;
						int x4 = x1 + k;
						k = -mulscale(sinang, l, 16);
						int y3 = y2 + k;
						int y4 = y1 + k;

						int ox = cposx - x1;
						int oy = cposy - y1;
						x1 = (int) (ox * cos - oy * sin) + (xdim << 11);
						y1 = (int) (ox * sin + oy * cos) + (ydim << 11);

						ox = cposx - x2;
						oy = cposy - y2;
						x2 = (int) (ox * cos - oy * sin) + (xdim << 11);
						y2 = (int) (ox * sin + oy * cos) + (ydim << 11);

						ox = cposx - x3;
						oy = cposy - y3;
						x3 = (int) (ox * cos - oy * sin) + (xdim << 11);
						y3 = (int) (ox * sin + oy * cos) + (ydim << 11);

						ox = cposx - x4;
						oy = cposy - y4;
						x4 = (int) (ox * cos - oy * sin) + (xdim << 11);
						y4 = (int) (ox * sin + oy * cos) + (ydim << 11);

						int col = mapSettings.getSpriteColor(j);
						if (col < 0)
							break;

						drawline256(x1, y1, x2, y2, col);
						drawline256(x2, y2, x3, y3, col);
						drawline256(x3, y3, x4, y4, col);
						drawline256(x4, y4, x1, y1, col);
					}
						break;
					}
				}
			}
		}

		// draw player
		for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
			int spr = mapSettings.getPlayerSprite(i);
			if (spr == -1 || !isValidSector(sprite[spr].sectnum))
				continue;

			SPRITE pPlayer = sprite[spr];
			int ox = cposx - mapSettings.getSpriteX(spr);
			int oy = cposy - mapSettings.getSpriteY(spr);

			float dx = ox * cos - oy * sin;
			float dy = ox * sin + oy * cos;

			int dang = (pPlayer.ang - cang) & 0x7FF;
			int viewindex = mapSettings.getViewPlayer();
			if (i == viewindex && !mapSettings.isScrollMode()) {
				dx = 0;
				dy = viewindex ^ i;
				dang = 0;
			}

			if (i == viewindex || mapSettings.isShowAllPlayers()) {
				int picnum = mapSettings.getPlayerPicnum(i);
				if (picnum == -1) { // draw it with lines
                    int x2 = 0;
					int y2 = -(mapSettings.getPlayerZoom(i, czoom) << 1);

					int col = mapSettings.getSpriteColor(spr);
					if (col < 0)
						continue;

					int sx = (int) dx;
					int sy = (int) dy;

					drawline256(sx - x2 + (xdim << 11), sy - y2 + (ydim << 11), sx + x2 + (xdim << 11),
							sy + y2 + (ydim << 11), col);
					drawline256(sx - y2 + (xdim << 11), sy + x2 + (ydim << 11), sx + x2 + (xdim << 11),
							sy + y2 + (ydim << 11), col);
					drawline256(sx + y2 + (xdim << 11), sy - x2 + (ydim << 11), sx + x2 + (xdim << 11),
							sy + y2 + (ydim << 11), col);
				} else {
					int nZoom = mapSettings.getPlayerZoom(i, czoom);

					int sx = (int) (dx + xdim * 2048);
					int sy = (int) (dy + ydim * 2048);

					rotatesprite(sx * 16, sy * 16, nZoom, (short) dang, picnum, pPlayer.shade, pPlayer.pal,
							(pPlayer.cstat & 2) >> 1, wx1, wy1, wx2, wy2);
				}
			}
		}
	}

	@Override
	public void drawmapview(int dax, int day, int zoome, int ang) {
		beforedrawrooms = 0;

		Arrays.fill(gotsector, (byte) 0);

		if (isDrawing())
			end();

//		switchShader(parent.getTexFormat() != PixelFormat.Pal8 ? Shader.RGBWorldShader : Shader.IndexedWorldShader);

		Matrix4 tmpMat = parent.transform; // Projection matrix

        BuildCamera cam = parent.cam;
		cam.projection.setToOrtho(xdim / 2, (-xdim / 2), -(ydim / 2), ydim / 2, 0, 1);
		cam.projection.scale(zoome / 32.0f, zoome / 32.0f, 0);
		cam.view.set(parent.identity);
		cam.combined.set(cam.projection);
		Matrix4.mul(cam.combined.val, cam.view.val);

		int showSprites = mapSettings.isShowFloorSprites() ? 1 : 0;
		showSprites |= (mapSettings.isShowSprites(MapView.Polygons) ? 2 : 0);

		int sortnum = 0;
		for (int s = 0; s < numsectors; s++) {
			SECTOR sec = sector[s];

			if (mapSettings.isFullMap() || (show2dsector[s >> 3] & pow2char[s & 7]) != 0) {
				if ((showSprites & 1) != 0) {
					// Collect floor sprites to draw
					for (int i = headspritesect[s]; i >= 0; i = nextspritesect[i])
						if ((sprite[i].cstat & 48) == 32) {
							if (sortnum >= MAXSPRITESONSCREEN)
								break;

							if ((sprite[i].cstat & (64 + 8)) == (64 + 8)
									|| !mapSettings.isSpriteVisible(MapView.Polygons, i))
								continue;

							if (tsprite[sortnum] == null)
								tsprite[sortnum] = new SPRITE();
							tsprite[sortnum].set(sprite[i]);
							tsprite[sortnum++].owner = (short) i;
						}
				}

				if ((showSprites & 2) != 0) {
					for (int i = headspritesect[s]; i >= 0; i = nextspritesect[i]) {
						if ((sprite[i].cstat & 48) == 32)
							continue;

						if ((show2dsprite[i >> 3] & pow2char[i & 7]) != 0) {
							if (sortnum >= MAXSPRITESONSCREEN)
								break;

							if (!mapSettings.isSpriteVisible(MapView.Polygons, i))
								continue;

							if(i == mapSettings.getPlayerSprite(mapSettings.getViewPlayer()))
								continue;

							if (tsprite[sortnum] == null)
								tsprite[sortnum] = new SPRITE();
							tsprite[sortnum].set(sprite[i]);
							tsprite[sortnum++].owner = (short) i;
						}
					}
				}

				gotsector[s >> 3] |= pow2char[s & 7];
				if (sec.isParallaxFloor())
					continue;
				globalpal = sec.floorpal;

				int globalpicnum = sec.floorpicnum;
				if (globalpicnum >= MAXTILES)
					globalpicnum = 0;
				engine.setgotpic(globalpicnum);
				Tile pic = engine.getTile(globalpicnum);

				if (!pic.hasSize())
					continue;

				if (pic.getType() != AnimType.None) {
					globalpicnum += engine.animateoffs(globalpicnum, s);
					pic = engine.getTile(globalpicnum);
				}

				if (!pic.isLoaded())
					engine.loadtile(globalpicnum);

				globalshade = max(min(sec.floorshade, numshades - 1), 0);

				GLSurface flor = parent.world.getFloor(s);
				if (flor != null) {
					tmpMat.setToRotation(0, 0, 1, (512 - ang) * buildAngleToDegrees);
					tmpMat.translate(-dax / parent.cam.xscale, -day / parent.cam.xscale,
							-sector[s].floorz / parent.cam.yscale);
					parent.drawSurf(flor, 0, tmpMat, null);
				}
			}
		}

		if (showSprites != 0) {
			float cos = (float) Math.cos((512 - ang) * buildAngleToRadians) * zoome / 4.0f;
			float sin = (float) Math.sin((512 - ang) * buildAngleToRadians) * zoome / 4.0f;

			// Sort sprite list
			int gap = 1;
			while (gap < sortnum)
				gap = (gap << 1) + 1;
			for (gap >>= 1; gap > 0; gap >>= 1)
				for (int i = 0; i < sortnum - gap; i++)
					for (int j = i; j >= 0; j -= gap) {
						if (sprite[tsprite[j].owner].z <= sprite[tsprite[j + gap].owner].z)
							break;

						short tmp = tsprite[j].owner;
						tsprite[j].owner = tsprite[j + gap].owner;
						tsprite[j + gap].owner = tmp;
					}

			for (int s = sortnum - 1; s >= 0; s--) {
				int j = tsprite[s].owner;
				SPRITE spr = sprite[j];
				if ((spr.cstat & 32768) == 0) {
					if (spr.picnum >= MAXTILES)
						spr.picnum = 0;

					int ox = dax - mapSettings.getSpriteX(j);
					int oy = day - mapSettings.getSpriteY(j);
					float dx = ox * cos - oy * sin;
					float dy = ox * sin + oy * cos;
					int daang = (spr.ang - ang) & 0x7FF;
					int nZoom = zoome * spr.yrepeat;
					int sx = (int) (dx + xdim * 2048);
					int sy = (int) (dy + ydim * 2048);

					rotatesprite(sx * 16, sy * 16, nZoom, (short) daang, mapSettings.getSpritePicnum(j), spr.shade, spr.pal,
							((spr.cstat & 2) >> 1) | 8, wx1, wy1, wx2, wy2);
				}
			}
		}

		flush();
		manager.unbind();
	}

	protected void resize(int width, int height) {
		projectionMatrix.setToOrtho(0, width - 1, height - 1, 0, 0, 1);
	}

	protected void begin(Shader shader) {
		if (drawing)
			throw new IllegalStateException("GdxBatch.end must be called before begin.");

		BuildGdx.gl20.glDepthMask(false);

		manager.bind(shader);
		setupMatrices();

		drawing = true;
	}

	public void end() {
		if (!drawing)
			throw new IllegalStateException("GdxBatch.begin must be called before end.");
		if (idx > 0)
			flush();

		lastTexture = null;
		cx1 = 0;
		cy1 = 0;
		cx2 = 0;
		cy2 = 0;
		lastType = -1;

		drawing = false;

		GL20 gl = BuildGdx.gl20;
		gl.glDepthMask(true);
		if (isBlendingEnabled())
			gl.glDisable(GL20.GL_BLEND);

		manager.unbind();
	}

	protected void flush() {
		if (idx == 0)
			return;

		int count;
		Mesh mesh;

		lastTexture.bind();
		if (lastType == GL20.GL_LINES) {
			int spritesInBatch = idx / 6;
			count = spritesInBatch * 2;

			mesh = this.linesMesh;
			mesh.setVertices(vertices, 0, idx);
		} else {
			int spritesInBatch = idx / 20;
			count = spritesInBatch * 6;

			mesh = this.mesh;
			mesh.setVertices(vertices, 0, idx);
			mesh.getIndicesBuffer().position(0);
			mesh.getIndicesBuffer().limit(count);
		}

		if (blendingDisabled) {
			BuildGdx.gl20.glDisable(GL20.GL_BLEND);
		} else {
			BuildGdx.gl20.glEnable(GL20.GL_BLEND);
		}

		manager.color(1.0f, 1.0f, 1.0f, 1.0f);
		manager.textureTransform(parent.texture_transform.idt(), 0);
		mesh.render(manager.getProgram(), lastType, 0, count);
		idx = 0;
	}

	public void draw(GLTile tex, int sx, int sy, int sizx, int sizy, int xoffset, int yoffset, float srcX, float srcY,
			float srcWidth, float srcHeight, int angle, int z, int dastat, int cx1, int cy1, int cx2, int cy2) {
		if (!drawing)
			throw new IllegalStateException("GdxBatch.begin must be called before draw.");

		switchTexture(tex);
		if (idx == vertices.length)
			flush();

		int ourxyaspect = xyaspect;
		if ((dastat & 2) == 0) {
			if ((dastat & 1024) == 0 && 4 * ydim <= 3 * xdim)
				ourxyaspect = (10 << 16) / 12;
		} else {
			// dastat&2: Auto window size scaling
			int oxdim = xdim, zoomsc;
			int xdim = oxdim; // SHADOWS global

			int ouryxaspect = yxaspect;
			ourxyaspect = xyaspect;

			// screen center to s[xy], 320<<16 coords.
			int normxofs = sx - (320 << 15), normyofs = sy - (200 << 15);
			if ((dastat & 1024) == 0 && 4 * ydim <= 3 * xdim) {
				xdim = (4 * ydim) / 3;

				ouryxaspect = (12 << 16) / 10;
				ourxyaspect = (10 << 16) / 12;
			}

			// nasty hacks go here
			if ((dastat & 8) == 0) {
				int twice_midcx = (cx1 + cx2) + 2;

				// screen x center to sx1, scaled to viewport
				int scaledxofs = scale(normxofs, scale(xdimen, xdim, oxdim), 320);
				int xbord = 0;
				if ((dastat & (256 | 512)) != 0) {
					xbord = scale(oxdim - xdim, twice_midcx, oxdim);
					if ((dastat & 512) == 0)
						xbord = -xbord;
				}

				sx = ((twice_midcx + xbord) << 15) + scaledxofs;
				zoomsc = xdimenscale;
				sy = (((cy1 + cy2) + 2) << 15) + mulscale(normyofs, zoomsc, 16);
			} else {
				// If not clipping to startmosts, & auto-scaling on, as a
				// hard-coded bonus, scale to full screen instead
				sx = (xdim << 15) + scale(normxofs, xdim, 320);
				if ((dastat & 512) != 0)
					sx += (oxdim - xdim) << 16;
				else if ((dastat & 256) == 0)
					sx += (oxdim - xdim) << 15;

				zoomsc = scale(xdim, ouryxaspect, 320);
				sy = (ydim << 15) + mulscale(normyofs, zoomsc, 16);
			}

			z = mulscale(z, zoomsc, 16);
		}

		final float aspectFix = ((dastat & 2) != 0) || ((dastat & 8) == 0) ? ourxyaspect / 65536.0f : 1.0f;
		final float scale = z / 65536.0f;

		final float xoffs = xoffset * scale;
		final float yoffs = yoffset * scale;
		final float width = scale * sizx;
		final float height = scale * sizy;

		float[] vertices = this.vertices;
		final float OriginX = sx / 65536.0f;
		final float OriginY = sy / 65536.0f;
		float x1, y1, x2, y2, x3, y3, x4, y4;

		// rotate
		if (angle != 0) {
			final float rotation = 360.0f * angle / 2048.0f;
			final float cos = MathUtils.cosDeg(rotation);
			final float sin = MathUtils.sinDeg(rotation);

			x1 = OriginX + (sin * yoffs - cos * xoffs) * aspectFix;
			y1 = OriginY - xoffs * sin - yoffs * cos;

			x4 = x1 + width * cos * aspectFix;
			y4 = y1 + width * sin;

			x2 = x1 - height * sin * aspectFix;
			y2 = y1 + height * cos;

			x3 = x2 + (x4 - x1);
			y3 = y2 + (y4 - y1);
		} else {
			x1 = x2 = OriginX - xoffs * aspectFix;
			y1 = y4 = OriginY - yoffs;

			x3 = x4 = x1 + width * aspectFix;
			y2 = y3 = y1 + height;
		}

		if ((dastat & 8) == 0) {
			float yaspect = windowy2 / (float) ydim;
			y1 *= yaspect;
			y2 *= yaspect;
			y3 *= yaspect;
			y4 *= yaspect;
		}

		if(tex.isHighTile()) {
			srcWidth = tex.getWidth();

            for (sizy = 1; sizy < tex.getHeight(); sizy += sizy);
			float scaley = (float) sizy / tex.getHeight();
			srcHeight = tex.getHeight() / scaley;
		}

		float v, u = srcX * invTexWidth;
		float v2, u2 = (srcX + srcWidth) * invTexWidth;
		if ((dastat & 4) == 0) {
			v = srcY * invTexHeight;
			v2 = (srcY + srcHeight) * invTexHeight;
		} else {
			v = (srcY + srcHeight) * invTexHeight;
			v2 = srcY * invTexHeight;
		}

		if (tex.isHighTile() && ((tex.getHiresXScale() != 1.0f) || (tex.getHiresYScale() != 1.0f))) {
			u *= tex.getHiresXScale();
			v *= tex.getHiresYScale();
			u2 *= tex.getHiresXScale();
			v2 *= tex.getHiresYScale();
		}

		float color = this.color;
		int idx = this.idx;

		vertices[idx + 0] = x1;
		vertices[idx + 1] = y1;
		vertices[idx + 2] = color;
		vertices[idx + 3] = u;
		vertices[idx + 4] = v;

		vertices[idx + 5] = x2;
		vertices[idx + 6] = y2;
		vertices[idx + 7] = color;
		vertices[idx + 8] = u;
		vertices[idx + 9] = v2;

		vertices[idx + 10] = x3;
		vertices[idx + 11] = y3;
		vertices[idx + 12] = color;
		vertices[idx + 13] = u2;
		vertices[idx + 14] = v2;

		vertices[idx + 15] = x4;
		vertices[idx + 16] = y4;
		vertices[idx + 17] = color;
		vertices[idx + 18] = u2;
		vertices[idx + 19] = v;

		this.idx = idx + 20;
	}

	protected void switchTextureParams(int pal, int shade, float alpha, boolean drawLastIndex) {
		IndexedShader shader = (IndexedShader) manager.getProgram();
		if (shader.getDrawLastIndex() == drawLastIndex && shader.getPal() == pal && shader.getShade() == shade
				&& shader.getTransparent() == alpha)
			return;

		flush();
		manager.textureParams8(pal, shade, alpha, drawLastIndex);
	}

	protected void switchTexture(GLTile texture) {
		if (texture == lastTexture)
			return;

		flush();
		lastTexture = texture;
		invTexWidth = 1.0f / texture.getWidth();
		invTexHeight = 1.0f / texture.getHeight();
	}

	protected void setColor(float r, float g, float b, float a) {
		int intBits = (int) (255 * a) << 24 | (int) (255 * b) << 16 | (int) (255 * g) << 8 | (int) (255 * r);
		color = NumberUtils.intToFloatColor(intBits);
	}

	protected boolean isBlendingEnabled() {
		return !blendingDisabled;
	}

	public boolean isDrawing() {
		return drawing;
	}

	protected void disableBlending() {
		if (blendingDisabled)
			return;
		flush();
		blendingDisabled = true;
	}

	protected void enableBlending() {
		if (!blendingDisabled)
			return;
		flush();
		blendingDisabled = false;
	}

	protected void switchShader(Shader shader) {
		if (shader == manager.getShader())
			return;

		if (isDrawing())
			flush();

		manager.bind(shader);
		setupMatrices();
	}

	private void setupMatrices() {
		manager.mirror(false);
		manager.fog(false, 0, 0, 0, 0, 0);
		if (manager.getShader() != Shader.BitmapShader) {
			manager.projection(projectionMatrix).view(parent.identity);
			manager.transform(parent.identity);
			manager.viewport(0, 0, xdim - 1, ydim - 1);
			cx1 = cy1 = 0;
			cx2 = xdim - 1;
			cy2 = ydim - 1;
		} else
			manager.projection(projectionMatrix);
	}

	protected void setType(int type) {
		if (type == lastType)
			return;

		flush();
		lastType = type;
	}

	protected GLTile allocLineTile() {
		DummyTileData data = new DummyTileData(PixelFormat.Bitmap, 1, 1);
		ByteBuffer b = data.getPixels();
		b.put((byte) 255);
		b.rewind();
		return parent.textureCache.newTile(data, 0, false);
	}

	protected void setViewport(int cx1, int cy1, int cx2, int cy2) {
		if (cx1 == this.cx1 && cx2 == this.cx2 && cy1 == this.cy1 && cy2 == this.cy2)
			return;

		flush();

		manager.viewport(cx1, cy1, cx2, cy2);

		this.cx1 = cx1;
		this.cx2 = cx2;
		this.cy1 = cy1;
		this.cy2 = cy2;
	}
}
