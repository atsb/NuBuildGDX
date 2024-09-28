/*
 * "POLYMOST" code originally written by Ken Silverman
 * Ken Silverman's official web site: "http://www.advsys.net/ken"
 * See the included license file "BUILDLIC.TXT" for license info.
 *
 * This file has been modified from Ken Silverman's original release
 * by Jonathon Fowler (jf@jonof.id.au)
 * by the EDuke32 team (development@voidpoint.com)
 * by Alexander Makarov-[M210] (m210-2007@mail.ru)
 */

package ru.m210projects.Build.Render.Polymost;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;
import static com.badlogic.gdx.graphics.GL20.GL_CLAMP_TO_EDGE;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_TEST;
import static com.badlogic.gdx.graphics.GL20.GL_FASTEST;
import static com.badlogic.gdx.graphics.GL20.GL_GREATER;
import static com.badlogic.gdx.graphics.GL20.GL_LEQUAL;
import static com.badlogic.gdx.graphics.GL20.GL_NICEST;
import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_PACK_ALIGNMENT;
import static com.badlogic.gdx.graphics.GL20.GL_REPEAT;
import static com.badlogic.gdx.graphics.GL20.GL_REPLACE;
import static com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_SRC_COLOR;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_2D;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_WRAP_S;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_WRAP_T;
import static com.badlogic.gdx.graphics.GL20.GL_TRIANGLE_FAN;
import static com.badlogic.gdx.graphics.GL20.GL_UNSIGNED_BYTE;
import static com.badlogic.gdx.graphics.GL20.GL_VERSION;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.floor;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static ru.m210projects.Build.Engine.DETAILPAL;
import static ru.m210projects.Build.Engine.GLOWPAL;
import static ru.m210projects.Build.Engine.MAXDRUNKANGLE;
import static ru.m210projects.Build.Engine.MAXPALOOKUPS;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSPRITESONSCREEN;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.MAXWALLS;
import static ru.m210projects.Build.Engine.RESERVEDPALS;
import static ru.m210projects.Build.Engine.TRANSLUSCENT1;
import static ru.m210projects.Build.Engine.TRANSLUSCENT2;
import static ru.m210projects.Build.Engine.automapping;
import static ru.m210projects.Build.Engine.beforedrawrooms;
import static ru.m210projects.Build.Engine.cosglobalang;
import static ru.m210projects.Build.Engine.cosviewingrangeglobalang;
import static ru.m210projects.Build.Engine.curpalette;
import static ru.m210projects.Build.Engine.globalang;
import static ru.m210projects.Build.Engine.globalcursectnum;
import static ru.m210projects.Build.Engine.globalhoriz;
import static ru.m210projects.Build.Engine.globalpal;
import static ru.m210projects.Build.Engine.globalposx;
import static ru.m210projects.Build.Engine.globalposy;
import static ru.m210projects.Build.Engine.globalposz;
import static ru.m210projects.Build.Engine.globalshade;
import static ru.m210projects.Build.Engine.globalvisibility;
import static ru.m210projects.Build.Engine.gotsector;
import static ru.m210projects.Build.Engine.halfxdimen;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.inpreparemirror;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.numshades;
import static ru.m210projects.Build.Engine.offscreenrendering;
import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Engine.palfadergb;
import static ru.m210projects.Build.Engine.palookup;
import static ru.m210projects.Build.Engine.parallaxyoffs;
import static ru.m210projects.Build.Engine.picsiz;
import static ru.m210projects.Build.Engine.pow2char;
import static ru.m210projects.Build.Engine.pow2long;
import static ru.m210projects.Build.Engine.pskybits;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.setviewcnt;
import static ru.m210projects.Build.Engine.show2dsector;
import static ru.m210projects.Build.Engine.show2dsprite;
import static ru.m210projects.Build.Engine.showinvisibility;
import static ru.m210projects.Build.Engine.singlobalang;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sinviewingrangeglobalang;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.spritesortcnt;
import static ru.m210projects.Build.Engine.tsprite;
import static ru.m210projects.Build.Engine.viewingrange;
import static ru.m210projects.Build.Engine.visibility;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.windowx1;
import static ru.m210projects.Build.Engine.windowx2;
import static ru.m210projects.Build.Engine.windowy1;
import static ru.m210projects.Build.Engine.windowy2;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.xdimen;
import static ru.m210projects.Build.Engine.xdimenscale;
import static ru.m210projects.Build.Engine.xdimscale;
import static ru.m210projects.Build.Engine.xyaspect;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Engine.ydimen;
import static ru.m210projects.Build.Engine.yxaspect;
import static ru.m210projects.Build.Engine.zeropskyoff;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.Gameutils.isCorruptWall;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_GOLD;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.dmulscale;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Build.Render.ModelHandle.MDModel.MDAnimation.mdpause;
import static ru.m210projects.Build.Render.ModelHandle.MDModel.MDAnimation.mdtims;
import static ru.m210projects.Build.Render.ModelHandle.MDModel.MDAnimation.omdtims;
import static ru.m210projects.Build.Render.Types.GL10.GL_ALPHA_TEST;
import static ru.m210projects.Build.Render.Types.GL10.GL_COMBINE_ALPHA_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_COMBINE_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_COMBINE_RGB_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_FOG;
import static ru.m210projects.Build.Render.Types.GL10.GL_INTERPOLATE_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_LINE_SMOOTH_HINT;
import static ru.m210projects.Build.Render.Types.GL10.GL_MODELVIEW;
import static ru.m210projects.Build.Render.Types.GL10.GL_MODULATE;
import static ru.m210projects.Build.Render.Types.GL10.GL_MULTISAMPLE;
import static ru.m210projects.Build.Render.Types.GL10.GL_MULTISAMPLE_FILTER_HINT_NV;
import static ru.m210projects.Build.Render.Types.GL10.GL_OPERAND0_ALPHA_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_OPERAND0_RGB_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_OPERAND1_RGB_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_OPERAND2_RGB_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_PERSPECTIVE_CORRECTION_HINT;
import static ru.m210projects.Build.Render.Types.GL10.GL_PREVIOUS_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_PROJECTION;
import static ru.m210projects.Build.Render.Types.GL10.GL_RGB_SCALE;
import static ru.m210projects.Build.Render.Types.GL10.GL_SMOOTH;
import static ru.m210projects.Build.Render.Types.GL10.GL_SOURCE0_ALPHA_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_SOURCE0_RGB_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_SOURCE1_RGB_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_SOURCE2_RGB_ARB;
import static ru.m210projects.Build.Render.Types.GL10.GL_TEXTURE0;
import static ru.m210projects.Build.Render.Types.GL10.GL_TEXTURE_ENV;
import static ru.m210projects.Build.Render.Types.GL10.GL_TEXTURE_ENV_MODE;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Engine.Point;
import ru.m210projects.Build.Architecture.BuildApplication.Platform;
import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Render.GLFog;
import ru.m210projects.Build.Render.GLInfo;
import ru.m210projects.Build.Render.GLRenderer;
import ru.m210projects.Build.Render.IOverheadMapSettings;
import ru.m210projects.Build.Render.OrphoRenderer;
import ru.m210projects.Build.Render.ModelHandle.GLModel;
import ru.m210projects.Build.Render.ModelHandle.ModelManager;
import ru.m210projects.Build.Render.ModelHandle.Voxel.GLVoxel;
import ru.m210projects.Build.Render.TextureHandle.GLTile;
import ru.m210projects.Build.Render.TextureHandle.IndexedShader;
import ru.m210projects.Build.Render.TextureHandle.TextureManager;
import ru.m210projects.Build.Render.TextureHandle.TextureManager.ExpandTexture;
import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;
import ru.m210projects.Build.Render.Types.FadeEffect;
import ru.m210projects.Build.Render.Types.GL10;
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

public class Polymost implements GLRenderer {

	protected final Color polyColor = new Color();
	public Rendering rendering = Rendering.Nothing;
	public GLFog globalfog;

	public static long TexDebug = -1;
	public static int r_parallaxskyclamping = 1; // OSD CVAR XXX
	public static int r_parallaxskypanning = 0; // XXX
	public static int r_vertexarrays = 1; // Vertex Array model drawing cvar
	public static int r_vbos = 1; // Vertex Buffer Objects model drawing cvars

	protected short globalpicnum;
	protected int globalorientation;
	private int numscans, numbunches;
	private boolean drunk;
	private float drunkIntensive = 1.0f;

	protected SPRITE[] tspriteptr = new SPRITE[MAXSPRITESONSCREEN + 1];

	private final int[] spritesx = new int[MAXSPRITESONSCREEN + 1];
	private final int[] spritesy = new int[MAXSPRITESONSCREEN + 1];
	private final int[] spritesz = new int[MAXSPRITESONSCREEN + 1];

	protected final static int MAXWALLSB = ((MAXWALLS >> 2) + (MAXWALLS >> 3));

	private final short[] p2 = new short[MAXWALLSB];
	private final short[] thesector = new short[MAXWALLSB];
	private final short[] thewall = new short[MAXWALLSB];
	private final short[] maskwall = new short[MAXWALLSB];
	private int maskwallcnt;

	private final short[] bunchfirst = new short[MAXWALLSB];
	private final short[] bunchlast = new short[MAXWALLSB];

	private int global_cf_z;
	private float global_cf_xpanning, global_cf_ypanning, global_cf_heinum;
	private int global_cf_shade, global_cf_pal;

	protected float[] alphahackarray = new float[MAXTILES];
	protected float shadescale = 1.1f;
	protected int shadescale_unbounded = 0;

	protected float curpolygonoffset; // internal polygon offset stack for drawing flat sprites to avoid depth
										// fighting

//	public static short drawingskybox = 0;

	GLTile frameTexture;
//	IntBuffer frameTexture;
	private int framew;
	private int frameh;
//	private int framesize;

	protected float gyxscale, gviewxrange, ghalfx, grhalfxdown10, grhalfxdown10x;
	protected double gxyaspect;
	protected float ghoriz;
	protected float gcosang, gsinang, gcosang2, gsinang2;
	protected float gchang, gshang, ogshang, gctang, gstang;
	protected float gtang = 0.0f;
	protected double guo, gux; // Screen-based texture mapping parameters
	protected double guy;
	protected double gvo;
	protected double gvx;
	protected double gvy;
	protected double gdo, gdx, gdy;

	private final int[] sectorborder = new int[256];
	private final double[] dxb1 = new double[MAXWALLSB];
	private final double[] dxb2 = new double[MAXWALLSB];
	private final byte[] ptempbuf = new byte[MAXWALLSB << 1];

	protected final float[][] matrix = new float[4][4];

	private int srepeat = 0, trepeat = 0;

	private final float SCISDIST = 1.0f; // 1.0: Close plane clipping distance

	private final PolyClipper clipper;

	private int glmultisample, glnvmultisamplehint;

	protected DefScript defs;

	private final int[] h_xsize = new int[MAXTILES];
	private final int[] h_ysize = new int[MAXTILES];
	private final byte[] h_xoffs = new byte[MAXTILES];
	private final byte[] h_yoffs = new byte[MAXTILES];

	protected GL10 gl;
	protected OrphoRenderer ortho;
	protected PolymostModelRenderer mdrenderer;
	protected boolean isInited = false;

	protected TextureManager textureCache;
	protected ModelManager modelManager;
	protected IndexedShader texshader;
	protected final Engine engine;

	public Polymost(Engine engine, IOverheadMapSettings settings) {
		this.engine = engine;
		this.textureCache = getTextureManager();
		this.modelManager = new PolymostModelManager(this);

		this.clipper = new PolyClipper(this);
		this.mdrenderer = new PolymostModelRenderer(this);

		for (int i = 0; i < 16; i++)
			drawpoly[i] = new Polygon();
		for (int i = 0; i < 8; i++)
			dmaskwall[i] = new Surface();
		for (int i = 0; i < 6; i++)
			dsprite[i] = new Surface();
		for (int i = 0; i < dsin.length; i++)
			dsin[i] = new Vector2();
		for (int i = 0; i < dcoord.length; i++)
			dcoord[i] = new Vector2();
		Arrays.fill(spritewall, -1);
		this.globalfog = new GLFog();

		this.ortho = allocOrphoRenderer(settings);
	}

	@Override
	public boolean isInited() {
		return isInited;
	}

	@Override
	public void uninit() {
		// Reset if this is -1 (meaning 1st texture call ever), or > 0 (textures
		// in memory)
		if (gltexcacnum < 0) {
			gltexcacnum = 0;

			// Hack for polymost_dorotatesprite calls before 1st polymost_drawrooms()
			gcosang = gcosang2 = 16384 / 262144.0f;
			gsinang = gsinang2 = 0.0f;
		}

		gl.glDisable(GL_ALPHA_TEST);
		gl.glDisable(GL_MULTISAMPLE);
		gl.glDisable(GL_FOG);
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL_TEXTURE);
		gl.glLoadIdentity();

		textureCache.uninit();
		modelManager.dispose();
		clearskins(false);

		ortho.uninit();

        isInited = false;
	}

	@Override
	public void init() {
		try {
			if (BuildGdx.graphics.getFrameType() != FrameType.GL)
				BuildGdx.app.setFrame(FrameType.GL);
			this.gl = BuildGdx.graphics.getGL10();

			GLInfo.init();
			gl.glShadeModel(GL_SMOOTH); // GL_FLAT
			gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // Use FASTEST for ortho!
			gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

			enableIndexedShader(GLSettings.usePaletteShader.get());

			ortho.init();
			mdrenderer.init();
			globalfog.init(textureCache);

			gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			gl.glPixelStorei(GL_PACK_ALIGNMENT, 1);

			if (glmultisample > 0 && GLInfo.multisample != 0) {
				if (GLInfo.nvmultisamplehint != 0)
					gl.glHint(GL_MULTISAMPLE_FILTER_HINT_NV, glnvmultisamplehint != 0 ? GL_NICEST : GL_FASTEST);
				gl.glEnable(GL_MULTISAMPLE);
			}

			if ((GLInfo.multitex == 0 || GLInfo.envcombine == 0)) {
				if (Console.Geti("r_detailmapping") != 0) {
					Console.Println("Your OpenGL implementation doesn't support detail mapping. Disabling...", 0);
					Console.Set("r_detailmapping", 0);
				}

				if (Console.Geti("r_glowmapping") != 0) {
					Console.Println("Your OpenGL implementation doesn't support glow mapping. Disabling...", 0);
					Console.Set("r_glowmapping", 0);
				}
			}

			if (r_vbos != 0 && (GLInfo.vbos == 0)) {
				Console.Println("Your OpenGL implementation doesn't support Vertex Buffer Objects. Disabling...", 0);
				r_vbos = 0;
			}

			Console.Println("Polymost renderer is initialized", OSDTEXT_GOLD);
			Console.Println(BuildGdx.graphics.getGLVersion().getRendererString() + " " + gl.glGetString(GL_VERSION),
					OSDTEXT_GOLD);

			isInited = true;
		} catch (Throwable t) {
			isInited = false;
			Console.Println("Polymost renderer initialization error!", Console.OSDTEXT_RED);
		}
	}

	protected TextureManager newTextureManager(Engine engine) {
		return new TextureManager(engine, ExpandTexture.Both);
	}

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
						textureCache.bind(detail);
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
						textureCache.bind(glow);
						setupTextureGlow(glow);
					}
				}
			}

			Color c = getshadefactor(shade, method);
			if (defs != null && tile.isHighTile() && defs.texInfo != null) {
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
			BuildGdx.gl.glColor4f(c.r, c.g, c.b, c.a); // GL30 exception
		}
	}

	public Color getshadefactor(int shade, int method) {
		float fshade = min(max(shade * 1.04f, 0), numshades);
		float f = (numshades - fshade) / numshades;

		polyColor.r = polyColor.g = polyColor.b = f;

		switch (method & 3) {
		default:
		case 0:
		case 1:
			polyColor.a = 1.0f;
			break;
		case 2:
			polyColor.a = TRANSLUSCENT1;
			break;
		case 3:
			polyColor.a = TRANSLUSCENT2;
			break;
		}

		return polyColor;
	}

	protected GLTile bind(PixelFormat fmt, int dapicnum, int dapalnum, int dashade, int skybox, int method) {
		GLTile pth = textureCache.get(texshader != null ? PixelFormat.Pal8 : PixelFormat.Rgba, dapicnum, dapalnum,
				skybox, method);
		if (pth == null)
			return null;

		bind(pth);
		setTextureParameters(pth, dapicnum, dapalnum, dashade, skybox, method);

		return pth;
	}

	protected void bind(GLTile tile) {
		GLTile bindedTile = textureCache.getLastBinded();
		boolean res = tile != bindedTile && ((bindedTile == null
				|| (tile.getPixelFormat() == PixelFormat.Pal8 && bindedTile.getPixelFormat() != PixelFormat.Pal8)
				|| (tile.getPixelFormat() != PixelFormat.Pal8 && bindedTile.getPixelFormat() == PixelFormat.Pal8)));
		textureCache.bind(tile);

		if (res && texshader != null) {
			gl.glActiveTexture(GL_TEXTURE0);
			if (tile.getPixelFormat() != PixelFormat.Pal8)
				texshader.end();
			else
				texshader.begin();
		}
	}

	@Override
	public TextureManager getTextureManager() {
		if (textureCache == null)
			return newTextureManager(engine);
		return textureCache;
	}

	@Override
	public void enableIndexedShader(boolean enable) {
		boolean isChanged = false;
		if (enable) {
			if (texshader == null) {
				texshader = allocIndexedShader();
				if (texshader != null) {
					textureCache.changePalette(curpalette.getBytes());
					isChanged = true;
				}
			}
		} else if (texshader != null) {
			texshader.dispose();
			texshader = null;

			textureCache.disposePalette();
			isChanged = true;
		}

		if (isChanged && isInited)
			textureCache.uninit();

		clearskins(false);
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
	public RenderType getType() {
		return null;
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
	public void invalidatetile(int tilenume, int pal, int how) {
		int numpal, firstpal, np;
		int hp;

		PixelFormat fmt = textureCache.getFmt(tilenume);
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

	// Make all textures "dirty" so they reload, but not re-allocate
	// This should be much faster than polymost_glreset()
	// Use this for palette effects ... but not ones that change every frame!
	public void gltexinvalidateall() {
		textureCache.invalidateall();
		clearskins(true);
	}

	public void gltexinvalidate8() {
		textureCache.invalidateall();
	}

	@Override
	public void changepalette(final byte[] palette) {
		if (texshader != null)
			textureCache.changePalette(palette);
	}

	public void clearskins(boolean bit8only) {
		for (int i = MAXTILES - 1; i >= 0; i--) {
			modelManager.clearSkins(i, bit8only);
		}
	}

	@Override
	public void gltexapplyprops() {
		GLFilter filter = GLSettings.textureFilter.get();
		textureCache.setFilter(filter);
		modelManager.setTextureFilter(filter, GLSettings.textureAnisotropy.get());
	}

	public void setupTextureGlow(GLTile tex) {
		if (!tex.isGlowTexture())
			return;

		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_COMBINE_ARB);
		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_COMBINE_RGB_ARB, GL_INTERPOLATE_ARB);

		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_SOURCE0_RGB_ARB, GL_PREVIOUS_ARB);
		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_OPERAND0_RGB_ARB, GL_SRC_COLOR);

		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_SOURCE1_RGB_ARB, GL_TEXTURE);
		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_OPERAND1_RGB_ARB, GL_SRC_COLOR);

		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_SOURCE2_RGB_ARB, GL_TEXTURE);
		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_OPERAND2_RGB_ARB, GL_ONE_MINUS_SRC_ALPHA);

		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_COMBINE_ALPHA_ARB, GL_REPLACE);
		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_SOURCE0_ALPHA_ARB, GL_PREVIOUS_ARB);
		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_OPERAND0_ALPHA_ARB, GL_SRC_ALPHA);

		tex.setupTextureWrap(TextureWrap.Repeat);
	}

	public void setupTextureDetail(GLTile tex) {
		if (!tex.isDetailTexture())
			return;

		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_COMBINE_ARB);
		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_COMBINE_RGB_ARB, GL_MODULATE);

		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_SOURCE0_RGB_ARB, GL_PREVIOUS_ARB);
		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_OPERAND0_RGB_ARB, GL_SRC_COLOR);

		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_SOURCE1_RGB_ARB, GL_TEXTURE);
		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_OPERAND1_RGB_ARB, GL_SRC_COLOR);

		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_COMBINE_ALPHA_ARB, GL_REPLACE);
		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_SOURCE0_ALPHA_ARB, GL_PREVIOUS_ARB);
		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_OPERAND0_ALPHA_ARB, GL_SRC_ALPHA);

		BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_RGB_SCALE, 2.0f);

		tex.setupTextureWrap(TextureWrap.Repeat);
	}

	public int gltexcacnum = -1;
	float glox1, gloy1, glox2, gloy2;

	public void resizeglcheck() // Ken Build method
	{
		if ((glox1 != windowx1) || (gloy1 != windowy1) || (glox2 != windowx2) || (gloy2 != windowy2)) {
			glox1 = windowx1;
			gloy1 = windowy1;
			glox2 = windowx2;
			gloy2 = windowy2;

			gl.glViewport(windowx1, ydim - (windowy2 + 1), windowx2 - windowx1 + 1, windowy2 - windowy1 + 1);

			gl.glMatrixMode(GL_PROJECTION);
			gl.glLoadIdentity();

//			glPerspective(65, xdimen / (float) ydimen, 0.0001f, 2000);
			float ang = (87 - (24 * gshang * gshang)) * 320.0f / xdimen;
			glPerspective(ang / 256.0f, xdimen / (float) (ydimen), -ydimen, ydimen);

			gl.glMatrixMode(GL_MODELVIEW);
			gl.glLoadIdentity();

			globalfog.enable();
		}
	}

	public void glPerspective(float fovyInDegrees, float aspectRatio, float znear, float zfar) {
		float ymax = (float) (znear * Math.tan(fovyInDegrees * Math.PI / 360.0));
		float xmax = ymax * aspectRatio;
//	    gl.glFrustumf(-xmax, xmax, -ymax, ymax, znear, zfar);
		glFrustumf(-xmax, xmax, -ymax, ymax, znear, zfar);
	}

	public void glFrustumf(float left, float right, float bottom, float top, float znear, float zfar) {
		float A = (right + left) / (right - left);
		float B = (top + bottom) / (top - bottom);
		float C = -(zfar + znear) / (zfar - znear);
		float D = -(2 * zfar * znear) / (zfar - znear);

		matrix[0][0] = 2.0f * znear / (right - left); // 0
		matrix[0][1] = 0.0f; // 4
		matrix[0][2] = A; // 8
		matrix[0][3] = 0.0f; // 12
		matrix[1][0] = 0.0f; // 1
		matrix[1][1] = 2.0f * znear / (top - bottom); // 5
		matrix[1][2] = B; // 9
		matrix[1][3] = 0.0f; // 13
		matrix[2][0] = 0.0f; // 2
		matrix[2][1] = 0.0f; // 6
		matrix[2][2] = C; // 2000 * C // 10
		matrix[2][3] = D; // 14
		matrix[3][0] = 0.0f; // 3
		matrix[3][1] = 0.0f; // 7
		matrix[3][2] = -1.0f; // 1f - matrix[2][2]; // 11
		matrix[3][3] = 0.0f; // 15

		gl.glLoadMatrixf(matrix);
	}

	// (dpx,dpy) specifies an n-sided polygon. The polygon must be a convex
	// clockwise loop.
	// n must be <= 8 (assume clipping can double number of vertices)

	private int drawpoly_math(int nn, int i, int j, double ngux, double ngdx, double nguy, double ngdy, double nguo,
			double ngdo, double var) {
		double f = -(drawpoly[i].px * (ngux - ngdx * var) + drawpoly[i].py * (nguy - ngdy * var) + (nguo - ngdo * var))
				/ ((drawpoly[j].px - drawpoly[i].px) * (ngux - ngdx * var)
						+ (drawpoly[j].py - drawpoly[i].py) * (nguy - ngdy * var));
		drawpoly[nn].uu = ((drawpoly[j].px - drawpoly[i].px) * f + drawpoly[i].px);
		drawpoly[nn].vv = ((drawpoly[j].py - drawpoly[i].py) * f + drawpoly[i].py);
		++nn;

		return nn;
	}

	int pow2xsplit = 0;
	int skyclamphack = 0;

	private final Polygon[] drawpoly = new Polygon[16];

	protected void drawpoly(Surface[] dm, int n, int method) {
		double ngdx, ngdy, ngdo, ngux, nguy, nguo;
		double ngvx, ngvy, ngvo, dp, up, vp, du0 = 0.0, du1 = 0.0, dui, duj;
		double f, r, ox, oy, oz, ox2, oy2, oz2, uoffs, ix0, ix1;
		int i, j, k, nn, tsizx, tsizy, xx, yy;

		boolean dorot;

		if (method == -1)
			return;

		if (n == 3) {
			if ((dm[0].px - dm[1].px) * (dm[2].py - dm[1].py) >= (dm[2].px - dm[1].px) * (dm[0].py - dm[1].py))
				return; // for triangle
		} else {
			f = 0; // f is area of polygon / 2
			for (i = n - 2, j = n - 1, k = 0; k < n; i = j, j = k, k++) {
				if (i < 0)
					return;
				f += (dm[i].px - dm[k].px) * dm[j].py;
			}
			if (f <= 0)
				return;
		}

		// Load texture (globalpicnum)
		if (globalpicnum >= MAXTILES)
			globalpicnum = 0;

		engine.setgotpic(globalpicnum);
		Tile pic = engine.getTile(globalpicnum);
		tsizx = pic.getWidth();
		tsizy = pic.getHeight();

		if (palookup[globalpal] == null)
			globalpal = 0;

		boolean HOM = false;
		if (pic.data == null) {
			if (TexDebug != -1)
				TexDebug = System.nanoTime();

			if (engine.loadtile(globalpicnum) == null) {
				tsizx = tsizy = 1;
				HOM = true;
			}

			if (TexDebug != -1)
				System.out.println(
						"Loading tile " + globalpicnum + " [" + ((System.nanoTime() - TexDebug) / 1000000f) + " ms]");
		}

		j = 0;
		dorot = ((gchang != 1.0) || (gctang != 1.0));
		if (dorot) {
			for (i = 0; i < n; i++) {
				ox = dm[i].px - ghalfx;
				oy = dm[i].py - ghoriz;
				oz = ghalfx;

				// Up/down rotation
				ox2 = ox;
				oy2 = oy * gchang - oz * gshang;
				oz2 = oy * gshang + oz * gchang;

				// Tilt rotation
				ox = ox2 * gctang - oy2 * gstang;
				oy = ox2 * gstang + oy2 * gctang;
				oz = oz2;

				r = ghalfx / oz;
				drawpoly[j].dd = (dm[i].px * gdx + dm[i].py * gdy + gdo) * r;
				drawpoly[j].uu = (dm[i].px * gux + dm[i].py * guy + guo) * r;
				drawpoly[j].vv = (dm[i].px * gvx + dm[i].py * gvy + gvo) * r;

				drawpoly[j].px = ox * r + ghalfx;
				drawpoly[j].py = oy * r + ghoriz;
				if ((j == 0) || (drawpoly[j].px != drawpoly[j - 1].px) || (drawpoly[j].py != drawpoly[j - 1].py))
					j++;
			}
		} else {
			for (i = 0; i < n; i++) {
				drawpoly[j].px = dm[i].px;
				drawpoly[j].py = dm[i].py;
				if ((j == 0) || (drawpoly[j].px != drawpoly[j - 1].px) || (drawpoly[j].py != drawpoly[j - 1].py))
					j++;
			}
		}
		while ((j >= 3) && (drawpoly[j - 1].px == drawpoly[0].px) && (drawpoly[j - 1].py == drawpoly[0].py))
			j--;
		if (j < 3)
			return;
		n = j;

		if (skyclamphack != 0)
			method |= 4;

		GLTile pth = bind(texshader != null ? PixelFormat.Pal8 : PixelFormat.Rgba, globalpicnum, globalpal, globalshade,
				Rendering.Skybox.getIndex(), method);
		if (pth == null)
			return;

		int texunits = textureCache.getTextureUnits();

		if (srepeat != 0)
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		if (trepeat != 0)
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		float hackscx = 1.0f;
		float hackscy = 1.0f;
		if (pth.isHighTile()) {
			hackscx = pth.getXScale();
			hackscy = pth.getYScale();
			tsizx = pth.getWidth();
			tsizy = pth.getHeight();
			HOM = false;
		}

		if (GLInfo.texnpot == 0) {
			for (xx = 1; xx < tsizx; xx += xx)
				;
			ox2 = 1.0 / xx;
			for (yy = 1; yy < tsizy; yy += yy)
				;
			oy2 = 1.0 / yy;
		} else {
			xx = tsizx;
			ox2 = 1.0 / xx;
			yy = tsizy;
			oy2 = 1.0 / yy;
		}

		if (((method & 3) == 0) && !HOM) {
			gl.glDisable(GL_BLEND);
			gl.glDisable(GL_ALPHA_TEST); // alpha_test
		} else {
			float al = 0.0f; // PLAG : default alphacut was 0.32 before goodalpha
			if (pth != null && pth.getAlphaCut() >= 0.0f)
				al = pth.getAlphaCut();
			if (alphahackarray[globalpicnum] != 0)
				al = alphahackarray[globalpicnum];
			if (HOM)
				al = 0.0f; // invalid textures ignore the alpha cutoff settings

			gl.glAlphaFunc(GL_GREATER, al);
			gl.glEnable(GL_BLEND);
			gl.glEnable(GL_ALPHA_TEST);
		}

		if (!dorot) {
			for (i = n - 1; i >= 0; i--) {
				drawpoly[i].dd = drawpoly[i].px * gdx + drawpoly[i].py * gdy + gdo;
				drawpoly[i].uu = drawpoly[i].px * gux + drawpoly[i].py * guy + guo;
				drawpoly[i].vv = drawpoly[i].px * gvx + drawpoly[i].py * gvy + gvo;
			}
		}

		if (pth.getPixelFormat() == PixelFormat.Pal8)
			texshader.setVisibility((int) globalfog.combvis);
		globalfog.apply();

		// Hack for walls&masked walls which use textures that are not a power of 2
		if ((pow2xsplit != 0) && (tsizx != xx)) {
			if (!dorot) {
				ngdx = gdx;
				ngdy = gdy;
				ngdo = gdo + (ngdx + ngdy) * .5;
				ngux = gux;
				nguy = guy;
				nguo = guo + (ngux + nguy) * .5;
				ngvx = gvx;
				ngvy = gvy;
				ngvo = gvo + (ngvx + ngvy) * .5;
			} else {
				ox = drawpoly[1].py - drawpoly[2].py;
				oy = drawpoly[2].py - drawpoly[0].py;
				oz = drawpoly[0].py - drawpoly[1].py;
				r = 1.0 / (ox * drawpoly[0].px + oy * drawpoly[1].px + oz * drawpoly[2].px);
				ngdx = (ox * drawpoly[0].dd + oy * drawpoly[1].dd + oz * drawpoly[2].dd) * r;
				ngux = (ox * drawpoly[0].uu + oy * drawpoly[1].uu + oz * drawpoly[2].uu) * r;
				ngvx = (ox * drawpoly[0].vv + oy * drawpoly[1].vv + oz * drawpoly[2].vv) * r;
				ox = drawpoly[2].px - drawpoly[1].px;
				oy = drawpoly[0].px - drawpoly[2].px;
				oz = drawpoly[1].px - drawpoly[0].px;
				ngdy = (ox * drawpoly[0].dd + oy * drawpoly[1].dd + oz * drawpoly[2].dd) * r;
				nguy = (ox * drawpoly[0].uu + oy * drawpoly[1].uu + oz * drawpoly[2].uu) * r;
				ngvy = (ox * drawpoly[0].vv + oy * drawpoly[1].vv + oz * drawpoly[2].vv) * r;
				ox = drawpoly[0].px - .5;
				oy = drawpoly[0].py - .5; // .5 centers texture nicely
				ngdo = drawpoly[0].dd - ox * ngdx - oy * ngdy;
				nguo = drawpoly[0].uu - ox * ngux - oy * nguy;
				ngvo = drawpoly[0].vv - ox * ngvx - oy * ngvy;
			}

			ngux *= hackscx;
			nguy *= hackscx;
			nguo *= hackscx;
			ngvx *= hackscy;
			ngvy *= hackscy;
			ngvo *= hackscy;
			uoffs = ((xx - tsizx) * .5);
			ngux -= ngdx * uoffs;
			nguy -= ngdy * uoffs;
			nguo -= ngdo * uoffs;

			// Find min&max u coordinates (du0...du1)
			for (i = 0; i < n; i++) {
				ox = drawpoly[i].px;
				oy = drawpoly[i].py;
				f = (ox * ngux + oy * nguy + nguo) / (ox * ngdx + oy * ngdy + ngdo);

//				if (abs(f) > 2000) - GDX 17.10.2021 "Polymost wall non2power textures hack protected" in 24 June 2020 - makes bugs with hires textures
//					f = 2000;

				if (i == 0) {
					du0 = du1 = f;
					continue;
				}
				if (f < du0)
					du0 = f;
				else if (f > du1)
					du1 = f;
			}

			if (tsizx != 0) {
				f = 1.0 / tsizx;
				ix0 = floor(du0 * f);
				ix1 = floor(du1 * f);

				for (; ix0 <= ix1; ix0++) {
					du0 = (ix0) * tsizx;
					du1 = (ix0 + 1) * tsizx;

					i = 0;
					nn = 0;
					duj = (drawpoly[i].px * ngux + drawpoly[i].py * nguy + nguo)
							/ (drawpoly[i].px * ngdx + drawpoly[i].py * ngdy + ngdo);
					do {
						j = i + 1;
						if (j == n)
							j = 0;
						dui = duj;
						duj = (drawpoly[j].px * ngux + drawpoly[j].py * nguy + nguo)
								/ (drawpoly[j].px * ngdx + drawpoly[j].py * ngdy + ngdo);

						if ((du0 <= dui) && (dui <= du1)) {
							drawpoly[nn].uu = drawpoly[i].px;
							drawpoly[nn].vv = drawpoly[i].py;
							nn++;
						}

						if (duj <= dui) {
							if ((du1 < duj) != (du1 < dui))
								nn = drawpoly_math(nn, i, j, ngux, ngdx, nguy, ngdy, nguo, ngdo, du1);
							if ((du0 < duj) != (du0 < dui))
								nn = drawpoly_math(nn, i, j, ngux, ngdx, nguy, ngdy, nguo, ngdo, du0);
						} else {
							if ((du0 < duj) != (du0 < dui))
								nn = drawpoly_math(nn, i, j, ngux, ngdx, nguy, ngdy, nguo, ngdo, du0);
							if ((du1 < duj) != (du1 < dui))
								nn = drawpoly_math(nn, i, j, ngux, ngdx, nguy, ngdy, nguo, ngdo, du1);
						}
						i = j;
					} while (i != 0);
					if (nn < 3)
						continue;

					if (HOM)
						gl.glDisable(GL_TEXTURE_2D);
					gl.glBegin(GL_TRIANGLE_FAN);
					for (i = 0; i < nn; i++) {
						Polygon dpoly = drawpoly[i];
						ox = dpoly.uu;
						oy = dpoly.vv;
						dp = ox * ngdx + oy * ngdy + ngdo;
						up = ox * ngux + oy * nguy + nguo;
						vp = ox * ngvx + oy * ngvy + ngvo;
						r = 1.0 / dp;
						if (texunits > 0) {
							j = 0;
							while (j <= texunits)
								gl.glMultiTexCoord2d(GL_TEXTURE0 + j++, (up * r - du0 + uoffs) * ox2, vp * r * oy2);
						} else
							gl.glTexCoord2d((up * r - du0 + uoffs) * ox2, vp * r * oy2);
						gl.glVertex3d((ox - ghalfx) * r * grhalfxdown10x, (ghoriz - oy) * r * grhalfxdown10,
								r * (1.0 / 1024.0));
					}
					gl.glEnd();
					if (HOM)
						gl.glEnable(GL_TEXTURE_2D);
				}
			}
		} else {
			ox2 *= hackscx;
			oy2 *= hackscy;

			if (HOM)
				gl.glDisable(GL_TEXTURE_2D);
			gl.glBegin(GL_TRIANGLE_FAN);
			for (i = 0; i < n; i++) {
				Polygon dpoly = drawpoly[i];

				r = 1.0f / dpoly.dd;
				if (texunits > 0) {
					j = 0;
					while (j <= texunits)
						gl.glMultiTexCoord2d(GL_TEXTURE0 + j++, dpoly.uu * r * ox2, dpoly.vv * r * oy2);
				} else
					gl.glTexCoord2d(dpoly.uu * r * ox2, dpoly.vv * r * oy2);

				gl.glVertex3d((dpoly.px - ghalfx) * r * grhalfxdown10x, (ghoriz - dpoly.py) * r * grhalfxdown10,
						r * (1.f / 1024.f));
			}
			gl.glEnd();
			if (HOM)
				gl.glEnable(GL_TEXTURE_2D);
		}

		textureCache.deactivateEffects();

		if (srepeat != 0)
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		if (trepeat != 0)
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}

	// variables that are set to ceiling- or floor-members, depending
	// on which one is processed right now

	private final double[] nonparallaxed_ft = new double[4], nonparallaxed_px = new double[3],
			nonparallaxed_py = new double[3], nonparallaxed_dd = new double[3], nonparallaxed_uu = new double[3],
			nonparallaxed_vv = new double[3];

	private void nonparallaxed(double nx0, double ny0, double nx1, double ny1, double ryp0, double ryp1, float x0,
			float x1, float cf_y0, float cf_y1, int have_floor, int sectnum, boolean floor) {
		double fx, fy, ox, oy, oz, ox2, oy2, r;
		int i;

		SECTOR sec = sector[sectnum];

		if ((globalorientation & 64) == 0) {
			nonparallaxed_ft[0] = globalposx;
			nonparallaxed_ft[1] = globalposy;
			nonparallaxed_ft[2] = cosglobalang;
			nonparallaxed_ft[3] = singlobalang;
		} else {
			// relative alignment
			fx = wall[wall[sec.wallptr].point2].x - wall[sec.wallptr].x;
			fy = wall[wall[sec.wallptr].point2].y - wall[sec.wallptr].y;
			r = 1.0 / sqrt(fx * fx + fy * fy);
			fx *= r;
			fy *= r;

			nonparallaxed_ft[2] = cosglobalang * fx + singlobalang * fy;
			nonparallaxed_ft[3] = singlobalang * fx - cosglobalang * fy;
			nonparallaxed_ft[0] = (globalposx - wall[sec.wallptr].x) * fx + (globalposy - wall[sec.wallptr].y) * fy;
			nonparallaxed_ft[1] = (globalposy - wall[sec.wallptr].y) * fx - (globalposx - wall[sec.wallptr].x) * fy;
			if ((globalorientation & 4) == 0)
				globalorientation ^= 32;
			else
				globalorientation ^= 16;
		}

		gdx = 0;
		gdy = gxyaspect;
		if ((globalorientation & 2) == 0)
			if (global_cf_z - globalposz != 0) // PK 2012: don't allow div by zero
				gdy /= global_cf_z - globalposz;
		gdo = -ghoriz * gdy;
		if ((globalorientation & 8) != 0) {
			nonparallaxed_ft[0] /= 8;
			nonparallaxed_ft[1] /= -8;
			nonparallaxed_ft[2] /= 2097152;
			nonparallaxed_ft[3] /= 2097152;
		} else {
			nonparallaxed_ft[0] /= 16;
			nonparallaxed_ft[1] /= -16;
			nonparallaxed_ft[2] /= 4194304;
			nonparallaxed_ft[3] /= 4194304;
		}
		gux = nonparallaxed_ft[3] * (viewingrange) / -65536.0;
		gvx = nonparallaxed_ft[2] * (viewingrange) / -65536.0;
		guy = nonparallaxed_ft[0] * gdy;
		gvy = nonparallaxed_ft[1] * gdy;
		guo = nonparallaxed_ft[0] * gdo;
		gvo = nonparallaxed_ft[1] * gdo;
		guo += (nonparallaxed_ft[2] - gux) * ghalfx;
		gvo -= (nonparallaxed_ft[3] + gvx) * ghalfx;

		// Texture flipping
		if ((globalorientation & 4) != 0) {
			r = gux;
			gux = gvx;
			gvx = r;
			r = guy;
			guy = gvy;
			gvy = r;
			r = guo;
			guo = gvo;
			gvo = r;
		}
		if ((globalorientation & 16) != 0) {
			gux = -gux;
			guy = -guy;
			guo = -guo;
		}
		if ((globalorientation & 32) != 0) {
			gvx = -gvx;
			gvy = -gvy;
			gvo = -gvo;
		}

		// Texture panning
		fx = global_cf_xpanning * (1 << (picsiz[globalpicnum] & 15)) / 256.0;
		fy = global_cf_ypanning * (1 << (picsiz[globalpicnum] >> 4)) / 256.0;

		if ((globalorientation & (2 + 64)) == (2 + 64)) // Hack for panning for slopes w/ relative alignment
		{
			r = global_cf_heinum / 4096.0;
			r = 1.0 / sqrt(r * r + 1);
			if ((globalorientation & 4) == 0)
				fy *= r;
			else
				fx *= r;
		}

		guy += gdy * fx;
		guo += gdo * fx;
		gvy += gdy * fy;
		gvo += gdo * fy;

		if ((globalorientation & 2) != 0) // slopes
		{
			nonparallaxed_px[0] = x0;
			nonparallaxed_py[0] = ryp0 + ghoriz;
			nonparallaxed_px[1] = x1;
			nonparallaxed_py[1] = ryp1 + ghoriz;

			// Pick some point guaranteed to be not collinear to the 1st two
			// points
			ox = nx0 + (ny1 - ny0);
			oy = ny0 + (nx0 - nx1);
			ox2 = (oy - globalposy) * gcosang - (ox - globalposx) * gsinang;
			oy2 = (ox - globalposx) * gcosang2 + (oy - globalposy) * gsinang2;
			oy2 = 1.0 / oy2;
			nonparallaxed_px[2] = ghalfx * ox2 * oy2 + ghalfx;
			oy2 *= gyxscale;
			nonparallaxed_py[2] = oy2 + ghoriz;

			for (i = 0; i < 3; i++) {
				nonparallaxed_dd[i] = nonparallaxed_px[i] * gdx + nonparallaxed_py[i] * gdy + gdo;
				nonparallaxed_uu[i] = nonparallaxed_px[i] * gux + nonparallaxed_py[i] * guy + guo;
				nonparallaxed_vv[i] = nonparallaxed_px[i] * gvx + nonparallaxed_py[i] * gvy + gvo;
			}

			nonparallaxed_py[0] = cf_y0;
			nonparallaxed_py[1] = cf_y1;
			if (floor)
				nonparallaxed_py[2] = (polymost_getflorzofslope(sectnum, ox, oy) - globalposz) * oy2 + ghoriz;
			else
				nonparallaxed_py[2] = (polymost_getceilzofslope(sectnum, ox, oy) - globalposz) * oy2 + ghoriz;

			ox = nonparallaxed_py[1] - nonparallaxed_py[2];
			oy = nonparallaxed_py[2] - nonparallaxed_py[0];
			oz = nonparallaxed_py[0] - nonparallaxed_py[1];
			r = 1.0 / (ox * nonparallaxed_px[0] + oy * nonparallaxed_px[1] + oz * nonparallaxed_px[2]);

			gdx = (ox * nonparallaxed_dd[0] + oy * nonparallaxed_dd[1] + oz * nonparallaxed_dd[2]) * r;
			gux = (ox * nonparallaxed_uu[0] + oy * nonparallaxed_uu[1] + oz * nonparallaxed_uu[2]) * r;
			gvx = (ox * nonparallaxed_vv[0] + oy * nonparallaxed_vv[1] + oz * nonparallaxed_vv[2]) * r;
			ox = nonparallaxed_px[2] - nonparallaxed_px[1];
			oy = nonparallaxed_px[0] - nonparallaxed_px[2];
			oz = nonparallaxed_px[1] - nonparallaxed_px[0];
			gdy = (ox * nonparallaxed_dd[0] + oy * nonparallaxed_dd[1] + oz * nonparallaxed_dd[2]) * r;
			guy = (ox * nonparallaxed_uu[0] + oy * nonparallaxed_uu[1] + oz * nonparallaxed_uu[2]) * r;
			gvy = (ox * nonparallaxed_vv[0] + oy * nonparallaxed_vv[1] + oz * nonparallaxed_vv[2]) * r;
			gdo = nonparallaxed_dd[0] - nonparallaxed_px[0] * gdx - nonparallaxed_py[0] * gdy;
			guo = nonparallaxed_uu[0] - nonparallaxed_px[0] * gux - nonparallaxed_py[0] * guy;
			gvo = nonparallaxed_vv[0] - nonparallaxed_px[0] * gvx - nonparallaxed_py[0] * gvy;

			if ((globalorientation & 64) != 0) // Hack for relative alignment on slopes
			{
				r = global_cf_heinum / 4096.0;
				r = sqrt(r * r + 1);
				if ((globalorientation & 4) == 0) {
					gvx *= r;
					gvy *= r;
					gvo *= r;
				} else {
					gux *= r;
					guy *= r;
					guo *= r;
				}
			}
		}

		clipper.setMethod((globalorientation >> 7) & 3);
		if (have_floor != 0) {
			if (globalposz >= polymost_getflorzofslope(sectnum, globalposx, globalposy))
				clipper.setMethod(-1); // Back-face culling
		} else {
			if (globalposz <= polymost_getceilzofslope(sectnum, globalposx, globalposy))
				clipper.setMethod(-1); // Back-face culling
		}

		calc_and_apply_fog(global_cf_shade, sec.visibility, global_cf_pal);

		pow2xsplit = 0;
		if (have_floor != 0)
			clipper.domost(x0, cf_y0, x1, cf_y1); // flor
		else
			clipper.domost(x1, cf_y1, x0, cf_y0); // ceil

		clipper.setMethod(0);
	}

	private void calc_ypanning(int refposz, double ryp0, double ryp1, double x0, double x1, short ypan, short yrepeat,
			boolean dopancor) {
		double t0 = (refposz - globalposz) * ryp0 + ghoriz;
		double t1 = (refposz - globalposz) * ryp1 + ghoriz;
		double t = ((gdx * x0 + gdo) * yrepeat) / ((x1 - x0) * ryp0 * 2048.f);

		Tile pic = engine.getTile(globalpicnum);
		int i = (1 << (picsiz[globalpicnum] >> 4));
		if (i < pic.getHeight())
			i <<= 1;

		if (GLInfo.texnpot != 0) {
			if (!dopancor) // texture scaled, it's need to fix
				t *= (float) pic.getHeight() / i;
			i = pic.getHeight();
		}

        float fy = ypan * i / 256.0f;
		gvx = (t0 - t1) * t;
		gvy = (x1 - x0) * t;
		gvo = -gvx * x0 - gvy * t0 + fy * gdo;
		gvx += fy * gdx;
		gvy += fy * gdy;
	}

	private final double[] drawalls_dd = new double[3], drawalls_vv = new double[3], drawalls_ft = new double[4];
	private final WALL drawalls_nwal = new WALL();

	private void drawalls(int bunch) {
		SECTOR sec, nextsec;
		WALL wal, wal2;
		float x0, x1, cy0, cy1, fy0, fy1, xp0, yp0, xp1, yp1, ryp0, ryp1, nx0, ny0, nx1, ny1;
		float t, t0, t1, ocy0, ocy1, ofy0, ofy1, oxp0, oyp0, fwalxrepeat;
		double oguo, ogux, oguy;
		int i, x, y, z, wallnum, sectnum, nextsectnum;

		sectnum = thesector[bunchfirst[bunch]];
		sec = sector[sectnum];

		calc_and_apply_fog(sec.floorshade, sec.visibility, sec.floorpal);

		for (z = bunchfirst[bunch]; z >= 0; z = p2[z]) {
			// DRAW WALLS SECTION!

			wallnum = thewall[z];

			wal = wall[wallnum];
			wal2 = wall[wal.point2];
			nextsectnum = wal.nextsector;
			nextsec = nextsectnum >= 0 ? sector[nextsectnum] : null;

			fwalxrepeat = wal.xrepeat & 0xFF;

			// Offset&Rotate 3D coordinates to screen 3D space
			x = wal.x - globalposx;
			y = wal.y - globalposy;
			xp0 = y * gcosang - x * gsinang;
			yp0 = x * gcosang2 + y * gsinang2;
			x = wal2.x - globalposx;
			y = wal2.y - globalposy;
			xp1 = y * gcosang - x * gsinang;
			yp1 = x * gcosang2 + y * gsinang2;

			oxp0 = xp0;
			oyp0 = yp0;

			// Clip to close parallel-screen plane
			if (yp0 < SCISDIST) {
				if (yp1 < SCISDIST)
					continue;
				t0 = (SCISDIST - yp0) / (yp1 - yp0);
				xp0 = (xp1 - xp0) * t0 + xp0;
				yp0 = SCISDIST;
				nx0 = (wal2.x - wal.x) * t0 + wal.x;
				ny0 = (wal2.y - wal.y) * t0 + wal.y;
			} else {
				t0 = 0.f;
				nx0 = wal.x;
				ny0 = wal.y;
			}
			if (yp1 < SCISDIST) {
				t1 = (SCISDIST - oyp0) / (yp1 - oyp0);
				xp1 = (xp1 - oxp0) * t1 + oxp0;
				yp1 = SCISDIST;
				nx1 = (wal2.x - wal.x) * t1 + wal.x;
				ny1 = (wal2.y - wal.y) * t1 + wal.y;
			} else {
				t1 = 1.f;
				nx1 = wal2.x;
				ny1 = wal2.y;
			}

			ryp0 = 1.f / yp0;
			ryp1 = 1.f / yp1;

			// Generate screen coordinates for front side of wall
			x0 = ghalfx * xp0 * ryp0 + ghalfx;
			x1 = ghalfx * xp1 * ryp1 + ghalfx;
			if (x1 <= x0)
				continue;

			ryp0 *= gyxscale;
			ryp1 *= gyxscale;

			polymost_getzsofslope(sectnum, nx0, ny0);
			cy0 = (dceilzsofslope - globalposz) * ryp0 + ghoriz;
			fy0 = (dfloorzsofslope - globalposz) * ryp0 + ghoriz;
			polymost_getzsofslope(sectnum, nx1, ny1);
			cy1 = (dceilzsofslope - globalposz) * ryp1 + ghoriz;
			fy1 = (dfloorzsofslope - globalposz) * ryp1 + ghoriz;

			{ // DRAW FLOOR
				rendering = Rendering.Floor.setIndex(sectnum);
				globalpicnum = sec.floorpicnum;
				globalshade = sec.floorshade;
				globalpal = sec.floorpal;
				globalorientation = sec.floorstat;
				if (engine.getTile(globalpicnum).getType() != AnimType.None)
					globalpicnum += engine.animateoffs(globalpicnum, sectnum);

				global_cf_shade = sec.floorshade;
				global_cf_pal = sec.floorpal;
				global_cf_z = sec.floorz;
				global_cf_xpanning = sec.floorxpanning;
				global_cf_ypanning = sec.floorypanning;
				global_cf_heinum = sec.floorheinum;

				if ((globalorientation & 1) == 0) {
					nonparallaxed(nx0, ny0, nx1, ny1, ryp0, ryp1, x0, x1, fy0, fy1, 1, sectnum, true);
				} else if ((nextsectnum < 0) || ((sector[nextsectnum].floorstat & 1) == 0))
					drawbackground(sectnum, x0, x1, fy0, fy1, true);

			} // END DRAW FLOOR

			{ // DRAW CEILING
				rendering = Rendering.Ceiling.setIndex(sectnum);
				globalpicnum = sec.ceilingpicnum;
				globalshade = sec.ceilingshade;
				globalpal = sec.ceilingpal & 0xFF;
				globalorientation = sec.ceilingstat;
				if (engine.getTile(globalpicnum).getType() != AnimType.None)
					globalpicnum += engine.animateoffs(globalpicnum, sectnum);

				global_cf_shade = sec.ceilingshade;
				global_cf_pal = sec.ceilingpal;
				global_cf_z = sec.ceilingz;
				global_cf_xpanning = sec.ceilingxpanning;
				global_cf_ypanning = sec.ceilingypanning;
				global_cf_heinum = sec.ceilingheinum;

				if ((globalorientation & 1) == 0) {
					nonparallaxed(nx0, ny0, nx1, ny1, ryp0, ryp1, x0, x1, cy0, cy1, 0, sectnum, false);
				} else if ((nextsectnum < 0) || ((sector[nextsectnum].ceilingstat & 1) == 0))
					drawbackground(sectnum, x0, x1, cy0, cy1, false);

			} // END DRAW CEILING

			rendering = Rendering.Wall.setIndex(wallnum);
			gdx = (ryp0 - ryp1) * gxyaspect / (x0 - x1);
			gdy = 0;
			gdo = ryp0 * gxyaspect - gdx * x0;
			gux = (t0 * ryp0 - t1 * ryp1) * gxyaspect * fwalxrepeat * 8.0 / (x0 - x1);
			guo = t0 * ryp0 * gxyaspect * fwalxrepeat * 8.0 - gux * x0;
			guo += wal.xpanning * gdo;
			gux += wal.xpanning * gdx;
			guy = 0;
			ogux = gux;
			oguy = guy;
			oguo = guo;

			if (nextsectnum >= 0) {
				polymost_getzsofslope(nextsectnum, nx0, ny0);
				ocy0 = (dceilzsofslope - globalposz) * ryp0 + ghoriz;
				ofy0 = (dfloorzsofslope - globalposz) * ryp0 + ghoriz;
				polymost_getzsofslope(nextsectnum, nx1, ny1);
				ocy1 = (dceilzsofslope - globalposz) * ryp1 + ghoriz;
				ofy1 = (dfloorzsofslope - globalposz) * ryp1 + ghoriz;

				if ((wal.cstat & 48) == 16)
					maskwall[maskwallcnt++] = (short) z;

				if (((cy0 < ocy0) || (cy1 < ocy1))
						&& (((sec.ceilingstat & sector[nextsectnum].ceilingstat) & 1)) == 0) {
					globalpicnum = wal.picnum;
					globalshade = wal.shade;
					globalpal = wal.pal & 0xFF;
					if (engine.getTile(globalpicnum).getType() != AnimType.None)
						globalpicnum += engine.animateoffs(globalpicnum, wallnum + 16384);

					if (!wal.isBottomAligned())
						i = sector[nextsectnum].ceilingz;
					else
						i = sec.ceilingz;

					// over
					calc_ypanning(i, ryp0, ryp1, x0, x1, wal.ypanning, wal.yrepeat, wal.isBottomAligned());

					if (wal.isXFlip()) {
						t = fwalxrepeat * 8 + wal.xpanning * 2;
						gux = gdx * t - gux;
						guy = gdy * t - guy;
						guo = gdo * t - guo;
					}

					if (wal.isYFlip()) {
						gvx = -gvx;
						gvy = -gvy;
						gvo = -gvo;
					}

					int shade = wal.shade;
					calc_and_apply_fog(shade, sec.visibility, sec.floorpal);

					pow2xsplit = 1;
					clipper.domost(x1, ocy1, x0, ocy0);
					if (wal.isXFlip()) {
						gux = ogux;
						guy = oguy;
						guo = oguo;
					}
				}
				if (((ofy0 < fy0) || (ofy1 < fy1)) && (((sec.floorstat & sector[nextsectnum].floorstat) & 1)) == 0) {
					if (!wal.isSwapped()) {
						drawalls_nwal.set(wal);
					} else {
						drawalls_nwal.set(wall[wal.nextwall]);
						guo += (drawalls_nwal.xpanning - wal.xpanning) * gdo;
						gux += (drawalls_nwal.xpanning - wal.xpanning) * gdx;
						guy += (drawalls_nwal.xpanning - wal.xpanning) * gdy;
					}
					globalpicnum = drawalls_nwal.picnum;
					globalshade = drawalls_nwal.shade;
					globalpal = drawalls_nwal.pal & 0xFF;
					if (engine.getTile(globalpicnum).getType() != AnimType.None)
						globalpicnum += engine.animateoffs(globalpicnum, wallnum + 16384);

					if (!drawalls_nwal.isBottomAligned())
						i = sector[nextsectnum].floorz;
					else
						i = sec.ceilingz;

					// under
					calc_ypanning(i, ryp0, ryp1, x0, x1, drawalls_nwal.ypanning, wal.yrepeat,
							!drawalls_nwal.isBottomAligned());

					if (wal.isXFlip()) {
						t = (fwalxrepeat * 8 + drawalls_nwal.xpanning * 2);
						gux = gdx * t - gux;
						guy = gdy * t - guy;
						guo = gdo * t - guo;
					}
					if (drawalls_nwal.isYFlip()) {
						gvx = -gvx;
						gvy = -gvy;
						gvo = -gvo;
					} // yflip

					int shade = drawalls_nwal.shade;
					calc_and_apply_fog(shade, sec.visibility, sec.floorpal);

					pow2xsplit = 1;
					clipper.domost(x0, ofy0, x1, ofy1);
					if ((wal.cstat & (2 + 8)) != 0) {
						guo = oguo;
						gux = ogux;
						guy = oguy;
					}
				}
			}

			if ((nextsectnum < 0) || wal.isOneWay()) // White/1-way wall
			{
				do {
					boolean maskingOneWay = (nextsectnum >= 0 && wal.isOneWay());
					if (maskingOneWay) {
						if (getclosestpointonwall(globalposx, globalposy, wallnum, projPoint) == 0
								&& klabs(globalposx - (int) projPoint.x) + klabs(globalposy - (int) projPoint.y) <= 128)
							break;
					}

					globalpicnum = (nextsectnum < 0) ? wal.picnum : wal.overpicnum;
					globalshade = wal.shade;
					globalpal = wal.pal & 0xFF;
					if (engine.getTile(globalpicnum).getType() != AnimType.None)
						globalpicnum += engine.animateoffs(globalpicnum, wallnum + 16384);

					boolean nwcs4 = !wal.isBottomAligned();

					if (nextsectnum >= 0) {
						i = nwcs4 ? nextsec.ceilingz : sec.ceilingz;
					} else {
						i = nwcs4 ? sec.ceilingz : sec.floorz;
					}

					// white
					calc_ypanning(i, ryp0, ryp1, x0, x1, wal.ypanning, wal.yrepeat, nwcs4 && !maskingOneWay);

					if (wal.isXFlip()) {
						t = (fwalxrepeat * 8 + wal.xpanning * 2);
						gux = gdx * t - gux;
						guy = gdy * t - guy;
						guo = gdo * t - guo;
					}
					if (wal.isYFlip()) {
						gvx = -gvx;
						gvy = -gvy;
						gvo = -gvo;
					}

					int shade = wal.shade;
					calc_and_apply_fog(shade, sec.visibility, sec.floorpal);
					pow2xsplit = 1;
					clipper.domost(x0, cy0, x1, cy1);
				} while (false);

			}

			if (nextsectnum >= 0) {
				if (((gotsector[nextsectnum >> 3] & pow2char[nextsectnum & 7]) == 0)
						&& (clipper.testvisiblemost(x0, x1) != 0))
					scansector(nextsectnum);
			}
		}
	}

	private int polymost_bunchfront(int b1, int b2) {
		double x1b1, x1b2, x2b1, x2b2;
		int b1f, b2f, i;

		b1f = bunchfirst[b1];
		x1b1 = dxb1[b1f];
		x2b2 = dxb2[bunchlast[b2]];
		if (x1b1 >= x2b2)
			return (-1);
		b2f = bunchfirst[b2];
		x1b2 = dxb1[b2f];
		x2b1 = dxb2[bunchlast[b1]];
		if (x1b2 >= x2b1)
			return (-1);

		if (x1b1 >= x1b2) {
			for (i = b2f; dxb2[i] <= x1b1 && p2[i] != -1; i = p2[i])
				;
			return (wallfront(b1f, i));
		}

		for (i = b1f; dxb2[i] <= x1b2 && p2[i] != -1; i = p2[i])
			;
		return (wallfront(i, b2f));
	}

	protected int wallfront(int l1, int l2) {
		WALL wal;
		int x11, y11, x21, y21, x12, y12, x22, y22, dx, dy, t1, t2;

		wal = wall[thewall[l1]];
		x11 = wal.x;
		y11 = wal.y;
		wal = wall[wal.point2];
		x21 = wal.x;
		y21 = wal.y;
		wal = wall[thewall[l2]];
		x12 = wal.x;
		y12 = wal.y;
		wal = wall[wal.point2];
		x22 = wal.x;
		y22 = wal.y;

		dx = x21 - x11;
		dy = y21 - y11;
		t1 = dmulscale(x12 - x11, dy, -dx, y12 - y11, 2); // p1(l2) vs. l1
		t2 = dmulscale(x22 - x11, dy, -dx, y22 - y11, 2); // p2(l2) vs. l1
		if (t1 == 0) {
			t1 = t2;
			if (t1 == 0)
				return (-1);
		}
		if (t2 == 0)
			t2 = t1;
		if ((t1 ^ t2) >= 0) {
			t2 = dmulscale(globalposx - x11, dy, -dx, globalposy - y11, 2); // pos vs. l1
			return ((t2 ^ t1) >= 0 ? 1 : 0);
		}

		dx = x22 - x12;
		dy = y22 - y12;
		t1 = dmulscale(x11 - x12, dy, -dx, y11 - y12, 2); // p1(l1) vs. l2
		t2 = dmulscale(x21 - x12, dy, -dx, y21 - y12, 2); // p2(l1) vs. l2
		if (t1 == 0) {
			t1 = t2;
			if (t1 == 0)
				return (-1);
		}
		if (t2 == 0)
			t2 = t1;
		if ((t1 ^ t2) >= 0) {
			t2 = dmulscale(globalposx - x12, dy, -dx, globalposy - y12, 2); // pos vs. l2
			return ((t2 ^ t1) < 0 ? 1 : 0);
		}
		return (-2);
	}

	private void scansector(int sectnum) {
		double d, xp1, yp1, xp2, yp2;
		WALL wal, wal2;
		SPRITE spr;
		int z, zz, startwall, endwall, numscansbefore, scanfirst, bunchfrst, nextsectnum;
		int xs, ys, x1, y1, x2, y2;

		if (sectnum < 0)
			return;

		if (automapping == 1)
			show2dsector[sectnum >> 3] |= pow2char[sectnum & 7];

		sectorborder[0] = sectnum;
		int sectorbordercnt = 1;
		do {
			sectnum = sectorborder[--sectorbordercnt];

			for (z = headspritesect[sectnum]; z >= 0; z = nextspritesect[z]) {
				spr = sprite[z];
				if ((((spr.cstat & 0x8000) == 0) || showinvisibility) && (spr.xrepeat > 0) && (spr.yrepeat > 0)
						&& (spritesortcnt < MAXSPRITESONSCREEN)) {
					xs = spr.x - globalposx;
					ys = spr.y - globalposy;
					if (((spr.cstat & 48) != 0) || (xs * gcosang + ys * gsinang > 0)
							|| (GLSettings.useModels.get() && modelManager.hasModelInfo(spr.picnum))) {
						if ((spr.cstat & (64 + 48)) != (64 + 16) || dmulscale(sintable[(spr.ang + 512) & 2047], -xs,
								sintable[spr.ang & 2047], -ys, 6) > 0) {
							if (tsprite[spritesortcnt] == null)
								tsprite[spritesortcnt] = new SPRITE();
							tsprite[spritesortcnt].set(sprite[z]);

							tsprite[spritesortcnt++].owner = (short) z;
						}
					}
				}
			}

			gotsector[sectnum >> 3] |= pow2char[sectnum & 7];

			bunchfrst = numbunches;
			numscansbefore = numscans;

			if (sector[sectnum] == null)
				continue;
			startwall = sector[sectnum].wallptr;
			endwall = sector[sectnum].wallnum + startwall;
			scanfirst = numscans;
			xp2 = 0;
			yp2 = 0;
			if (startwall < 0 || endwall < 0)
				continue;
			for (z = startwall; z < endwall; z++) {
				if (isCorruptWall(z))
					continue;

				wal = wall[z];
				wal2 = wall[wal.point2];
				x1 = wal.x - globalposx;
				y1 = wal.y - globalposy;
				x2 = wal2.x - globalposx;
				y2 = wal2.y - globalposy;

				nextsectnum = wal.nextsector; // Scan close sectors

				if ((nextsectnum >= 0) && !wal.isOneWay() && sectorbordercnt < sectorborder.length
						&& ((gotsector[nextsectnum >> 3] & pow2char[nextsectnum & 7]) == 0)) {
					d = x1 * y2 - x2 * y1;
					xp1 = (x2 - x1);
					yp1 = (y2 - y1);
					if (d * d <= (xp1 * xp1 + yp1 * yp1) * (SCISDIST * SCISDIST * 260.0)) {
						sectorborder[sectorbordercnt++] = nextsectnum;
						gotsector[nextsectnum >> 3] |= pow2char[nextsectnum & 7];
					}
				}

				if ((z == startwall) || (wall[z - 1].point2 != z)) {
					xp1 = ((double) y1 * cosglobalang - (double) x1 * singlobalang) / 64.0;
					yp1 = ((double) x1 * cosviewingrangeglobalang + (double) y1 * sinviewingrangeglobalang) / 64.0;
				} else {
					xp1 = xp2;
					yp1 = yp2;
				}
				xp2 = ((double) y2 * cosglobalang - (double) x2 * singlobalang) / 64.0;
				yp2 = ((double) x2 * cosviewingrangeglobalang + (double) y2 * sinviewingrangeglobalang) / 64.0;

				if ((yp1 >= SCISDIST) || (yp2 >= SCISDIST))
					/* crossProduct */if (xp1 * yp2 < xp2 * yp1) // if wall is facing you...
					{
						if (inviewingrange(xp1, yp1, xp2, yp2)) {
							if (numscans >= MAXWALLSB - 1)
								return;

							thesector[numscans] = (short) sectnum;
							thewall[numscans] = (short) z;
							p2[numscans] = (short) (numscans + 1);
							numscans++;
						}
					}

				if ((wall[z].point2 < z) && (scanfirst < numscans)) {
					p2[numscans - 1] = (short) scanfirst;
					scanfirst = numscans;
				}
			}

			for (z = numscansbefore; z < numscans; z++) {
				if ((wall[thewall[z]].point2 != thewall[p2[z]]) || (dxb2[z] > dxb1[p2[z]])) {
					bunchfirst[numbunches++] = p2[z];
					p2[z] = -1;
				}
			}

			for (z = bunchfrst; z < numbunches; z++) {
				for (zz = bunchfirst[z]; p2[zz] >= 0; zz = p2[zz])
					;
				bunchlast[z] = (short) zz;
			}
		} while (sectorbordercnt > 0);
	}

	protected boolean inviewingrange(double xp1, double yp1, double xp2, double yp2) {
		if (yp1 >= SCISDIST) {
            dxb1[numscans] = xp1 * ghalfx / yp1 + ghalfx;
		} else
			dxb1[numscans] = -1e32;

		if (yp2 >= SCISDIST) {

            dxb2[numscans] = xp2 * ghalfx / yp2 + ghalfx;
		} else
			dxb2[numscans] = 1e32;

		return dxb1[numscans] < dxb2[numscans];
	}

	private void drawpapersky(int sectnum, double x0, double x1, double y0, double y1, boolean floor) {
		double ox, oy, t;

		short[] dapskyoff = zeropskyoff;
		int dapskybits = pskybits;

		if (dapskybits < 0)
			dapskybits = 0;
		// Use clamping for tiled sky textures
		for (int i = (1 << dapskybits) - 1; i > 0; i--)
			if (dapskyoff[i] != dapskyoff[i - 1]) {
				skyclamphack = r_parallaxskyclamping;
				break;
			}

		Tile pic = engine.getTile(globalpicnum);

		SECTOR sec = sector[sectnum];
		drawalls_dd[0] = xdimen * .0000001; // Adjust sky depth based on screen size!
		t = pic.getWidth() << dapskybits; // (1 << (picsiz[globalpicnum] & 15)) << dapskybits;

		drawalls_vv[1] = drawalls_dd[0] * (xdimscale * (double) viewingrange) / (65536.0 * 65536.0);
		drawalls_vv[0] = drawalls_dd[0]
				* ((pic.getHeight() >> 1)
						+ ((floor && r_parallaxskypanning == 1) ? parallaxyoffs - pic.getHeight() : parallaxyoffs))
				- drawalls_vv[1] * ghoriz;
		int i = (1 << (picsiz[globalpicnum] >> 4));
		if (i != pic.getHeight())
			i += i;

		// Hack to draw black rectangle below sky when looking up/down...
		gdx = 0;
		if (floor)
			gdy = gxyaspect / 262144.0;
		else
			gdy = gxyaspect / -262144.0;
		gdo = -ghoriz * gdy;
		gux = 0;
		guy = 0;
		guo = 0;
		gvx = 0;
		gvy = 0;
		gvo = 0;

		int oskyclamphack = skyclamphack;
		skyclamphack = 0;
		if (floor) {
			oy = ((pic.getHeight()) * drawalls_dd[0] - drawalls_vv[0]) / drawalls_vv[1];

			if ((oy > y0) && (oy > y1)) {
				clipper.domost((float) x0, (float) oy, (float) x1, (float) oy);
			} else if ((oy > y0) != (oy > y1)) {
				// fy0 fy1
				// \ /
				// oy---------- oy----------
				// \ /
				// fy1 fy0
				ox = (oy - y0) * (x1 - x0) / (y1 - y0) + x0;
				if (oy > y0) {
					clipper.domost((float) x0, (float) oy, (float) ox, (float) oy);
					clipper.domost((float) ox, (float) oy, (float) x1, (float) y1);
				} else {
					clipper.domost((float) x0, (float) y0, (float) ox, (float) oy);
					clipper.domost((float) ox, (float) oy, (float) x1, (float) oy);
				}
			} else
				clipper.domost((float) x0, (float) y0, (float) x1, (float) y1);
		} else {
			oy = -drawalls_vv[0] / drawalls_vv[1];

			if ((oy < y0) && (oy < y1)) {
				clipper.domost((float) x1, (float) oy, (float) x0, (float) oy);
			} else if ((oy < y0) != (oy < y1)) {
				/*
				 * cy1 cy0 // / \ //oy---------- oy--------- // / \ // cy0 cy1
				 */
				ox = (oy - y0) * (x1 - x0) / (y1 - y0) + x0;
				if (oy < y0) {
					clipper.domost((float) ox, (float) oy, (float) x0, (float) oy);
					clipper.domost((float) x1, (float) y1, (float) ox, (float) oy);
				} else {
					clipper.domost((float) ox, (float) oy, (float) x0, (float) y0);
					clipper.domost((float) x1, (float) oy, (float) ox, (float) oy);
				}
			} else
				clipper.domost((float) x1, (float) y1, (float) x0, (float) y0);
		}
		skyclamphack = oskyclamphack;

		double panning = sec.ceilingypanning;
		if (floor)
			panning = sec.floorypanning;

		if (r_parallaxskypanning != 0)
			drawalls_vv[0] += drawalls_dd[0] * panning * i / 256.0;

		gdx = 0;
		gdy = 0;
		gdo = drawalls_dd[0];
		gux = gdo * (t * xdimscale * yxaspect * viewingrange) / (16384.0 * 65536.0 * 65536.0 * 5.0 * 1024.0);
		guy = 0; // guo calculated later
		gvx = 0;
		gvy = drawalls_vv[1];
		gvo = drawalls_vv[0];

		i = globalpicnum;
		double r = (y1 - y0) / (x1 - x0); // slope of line
		oy = viewingrange / (ghalfx * 256.0);
		double oz = 1 / oy;

		int y = ((int) (((x0 - ghalfx) * oy) + globalang) >> (11 - dapskybits));

		panning = sec.ceilingxpanning;
		if (floor)
			panning = sec.floorxpanning;

		double fx = x0;
		do {
			globalpicnum = (short) (dapskyoff[y & ((1 << dapskybits) - 1)] + i);

			guo = gdo * (t * (globalang - (y << (11 - dapskybits))) / 2048.0
					+ ((r_parallaxskypanning != 0) ? panning : 0)) - gux * ghalfx;
			y++;
			ox = fx;
			fx = ((y << (11 - dapskybits)) - globalang) * oz + ghalfx;
			if (fx > x1) {
				fx = x1;
				i = -1;
			}

			pow2xsplit = 0;
			if (floor)
				clipper.domost((float) ox, (float) ((ox - x0) * r + y0), (float) fx, (float) ((fx - x0) * r + y0));
			else
				clipper.domost((float) fx, (float) ((fx - x0) * r + y0), (float) ox, (float) ((ox - x0) * r + y0));
		} while (i >= 0);

	}

	private final int[] skywalx = { -512, 512, 512, -512 }, skywaly = { -512, -512, 512, 512 };

	private void drawskybox(double x0, double x1, double y0, double y1, boolean floor) {
		double ox, oy, t;
		int x, y;
		double _xp0, _yp0, _xp1, _yp1, _oxp0, _oyp0, _t0, _t1;
		double _ryp0, _ryp1, _x0, _x1, _cy0, _fy0, _cy1, _fy1, _ox0, _ox1;

		pow2xsplit = 0;
		skyclamphack = 1;

		for (int i = 0; i < 4; i++) {
			x = skywalx[i & 3];
			y = skywaly[i & 3];
			_xp0 = (double) y * gcosang - (double) x * gsinang;
			_yp0 = (double) x * gcosang2 + (double) y * gsinang2;
			x = skywalx[(i + 1) & 3];
			y = skywaly[(i + 1) & 3];
			_xp1 = (double) y * gcosang - (double) x * gsinang;
			_yp1 = (double) x * gcosang2 + (double) y * gsinang2;

			_oxp0 = _xp0;
			_oyp0 = _yp0;

			// Clip to close parallel-screen plane
			if (_yp0 < SCISDIST) {
				if (_yp1 < SCISDIST)
					continue;
				_t0 = (SCISDIST - _yp0) / (_yp1 - _yp0);
				_xp0 = (_xp1 - _xp0) * _t0 + _xp0;
				_yp0 = SCISDIST;
			} else {
				_t0 = 0.f;
			}
			if (_yp1 < SCISDIST) {
				_t1 = (SCISDIST - _oyp0) / (_yp1 - _oyp0);
				_xp1 = (_xp1 - _oxp0) * _t1 + _oxp0;
				_yp1 = SCISDIST;
			} else {
				_t1 = 1.f;
			}

			_ryp0 = 1.f / _yp0;
			_ryp1 = 1.f / _yp1;

			// Generate screen coordinates for front side of wall
			_x0 = ghalfx * _xp0 * _ryp0 + ghalfx;
			_x1 = ghalfx * _xp1 * _ryp1 + ghalfx;
			if (_x1 <= _x0)
				continue;
			if ((_x0 >= x1) || (x0 >= _x1))
				continue;

			_ryp0 *= gyxscale;
			_ryp1 *= gyxscale;

			_cy0 = -8192.f * _ryp0 + ghoriz;
			_fy0 = 8192.f * _ryp0 + ghoriz;
			_cy1 = -8192.f * _ryp1 + ghoriz;
			_fy1 = 8192.f * _ryp1 + ghoriz;

			_ox0 = _x0;
			_ox1 = _x1;

			// Make sure: x0<=_x0<_x1<=_x1
			double ny0 = y0;
			double ny1 = y1;
			if (_x0 < x0) {
				t = (x0 - _x0) / (_x1 - _x0);
				_cy0 += (_cy1 - _cy0) * t;
				_fy0 += (_fy1 - _fy0) * t;
				_x0 = x0;
			} else if (_x0 > x0)
				ny0 += (_x0 - x0) * (y1 - y0) / (x1 - x0);
			if (_x1 > x1) {
				t = (x1 - _x1) / (_x1 - _x0);
				_cy1 += (_cy1 - _cy0) * t;
				_fy1 += (_fy1 - _fy0) * t;
				_x1 = x1;
			} else if (_x1 < x1)
				ny1 += (_x1 - x1) * (y1 - y0) / (x1 - x0);

			// floor of skybox

			drawalls_ft[0] = 512 / 16;
			drawalls_ft[1] = -512 / -16;
			if (floor)
				drawalls_ft[1] = 512 / -16;

			drawalls_ft[2] = (cosglobalang) * (1.f / 2147483648.f);
			drawalls_ft[3] = (singlobalang) * (1.f / 2147483648.f);
			gdx = 0;
			gdy = gxyaspect * -(1.0 / 4194304.0);
			if (floor)
				gdy = gxyaspect * (1.0 / 4194304.0);
			gdo = -ghoriz * gdy;
			gux = drawalls_ft[3] * (viewingrange) / -65536.0;
			gvx = drawalls_ft[2] * (viewingrange) / -65536.0;
			guy = drawalls_ft[0] * gdy;
			gvy = drawalls_ft[1] * gdy;
			guo = drawalls_ft[0] * gdo;
			gvo = drawalls_ft[1] * gdo;
			guo += (drawalls_ft[2] - gux) * ghalfx;
			gvo -= (drawalls_ft[3] + gvx) * ghalfx;

			if (floor) {
				gvx = -gvx;
				gvy = -gvy;
				gvo = -gvo; // y-flip skybox floor

				Rendering.Skybox.setIndex(6); // floor/6th texture/index 4 of skybox

				if ((_fy0 > ny0) && (_fy1 > ny1))
					clipper.domost((float) _x0, (float) _fy0, (float) _x1, (float) _fy1);
				else if ((_fy0 > ny0) != (_fy1 > ny1)) {
					t = (_fy0 - ny0) / (ny1 - ny0 - _fy1 + _fy0);
					ox = _x0 + (_x1 - _x0) * t;
					oy = _fy0 + (_fy1 - _fy0) * t;
					if (ny0 > _fy0) {
						clipper.domost((float) _x0, (float) ny0, (float) ox, (float) oy);
						clipper.domost((float) ox, (float) oy, (float) _x1, (float) _fy1);
					} else {
						clipper.domost((float) _x0, (float) _fy0, (float) ox, (float) oy);
						clipper.domost((float) ox, (float) oy, (float) _x1, (float) ny1);
					}
				} else
					clipper.domost((float) _x0, (float) ny0, (float) _x1, (float) ny1);
			} else {

				Rendering.Skybox.setIndex(5); // ceiling/5th texture/index 4 of skybox

				if ((_cy0 < ny0) && (_cy1 < ny1))
					clipper.domost((float) _x1, (float) _cy1, (float) _x0, (float) _cy0);
				else if ((_cy0 < ny0) != (_cy1 < ny1)) {
					t = (_cy0 - ny0) / (ny1 - ny0 - _cy1 + _cy0);
					ox = _x0 + (_x1 - _x0) * t;
					oy = _cy0 + (_cy1 - _cy0) * t;
					if (ny0 < _cy0) {
						clipper.domost((float) ox, (float) oy, (float) _x0, (float) ny0);
						clipper.domost((float) _x1, (float) _cy1, (float) ox, (float) oy);
					} else {
						clipper.domost((float) ox, (float) oy, (float) _x0, (float) _cy0);
						clipper.domost((float) _x1, (float) ny1, (float) ox, (float) oy);
					}
				} else
					clipper.domost((float) _x1, (float) ny1, (float) _x0, (float) ny0);

			}

			// wall of skybox
			Rendering.Skybox.setIndex(i + 1); // i+1th texture/index i of skybox

			gdx = (_ryp0 - _ryp1) * gxyaspect * (1.0 / 512.0) / (_ox0 - _ox1);
			gdy = 0;
			gdo = _ryp0 * gxyaspect * (1.0 / 512.0) - gdx * _ox0;
			gux = (_t0 * _ryp0 - _t1 * _ryp1) * gxyaspect * (64.0 / 512.0) / (_ox0 - _ox1);
			guo = _t0 * _ryp0 * gxyaspect * (64.0 / 512.0) - gux * _ox0;
			guy = 0;
			_t0 = -8192.0 * _ryp0 + ghoriz;
			_t1 = -8192.0 * _ryp1 + ghoriz;
			t = ((gdx * _ox0 + gdo) * 8.f) / ((_ox1 - _ox0) * _ryp0 * 2048.f);
			gvx = (_t0 - _t1) * t;
			gvy = (_ox1 - _ox0) * t;
			gvo = -gvx * _ox0 - gvy * _t0;

			if (floor) {
				if ((_cy0 > ny0) && (_cy1 > ny1))
					clipper.domost((float) _x0, (float) _cy0, (float) _x1, (float) _cy1);
				else if ((_cy0 > ny0) != (_cy1 > ny1)) {
					t = (_cy0 - ny0) / (ny1 - ny0 - _cy1 + _cy0);
					ox = _x0 + (_x1 - _x0) * t;
					oy = _cy0 + (_cy1 - _cy0) * t;
					if (ny0 > _cy0) {
						clipper.domost((float) _x0, (float) ny0, (float) ox, (float) oy);
						clipper.domost((float) ox, (float) oy, (float) _x1, (float) _cy1);
					} else {
						clipper.domost((float) _x0, (float) _cy0, (float) ox, (float) oy);
						clipper.domost((float) ox, (float) oy, (float) _x1, (float) ny1);
					}
				} else
					clipper.domost((float) _x0, (float) ny0, (float) _x1, (float) ny1);
			} else {
				if ((_fy0 < ny0) && (_fy1 < ny1))
					clipper.domost((float) _x1, (float) _fy1, (float) _x0, (float) _fy0);
				else if ((_fy0 < ny0) != (_fy1 < ny1)) {
					t = (_fy0 - ny0) / (ny1 - ny0 - _fy1 + _fy0);
					ox = _x0 + (_x1 - _x0) * t;
					oy = _fy0 + (_fy1 - _fy0) * t;
					if (ny0 < _fy0) {
						clipper.domost((float) ox, (float) oy, (float) _x0, (float) ny0);
						clipper.domost((float) _x1, (float) _fy1, (float) ox, (float) oy);
					} else {
						clipper.domost((float) ox, (float) oy, (float) _x0, (float) _fy0);
						clipper.domost((float) _x1, (float) ny1, (float) ox, (float) oy);
					}
				} else
					clipper.domost((float) _x1, (float) ny1, (float) _x0, (float) ny0);
			}
		}

		// Ceiling of skybox

		Rendering.Skybox.setIndex(6); // floor/6th texture/index 5 of skybox
		if (floor)
			Rendering.Skybox.setIndex(5);

		drawalls_ft[0] = 512 / 16;
		drawalls_ft[1] = 512 / -16;
		if (floor)
			drawalls_ft[1] = -512 / -16;
		drawalls_ft[2] = (cosglobalang) * (1.f / 2147483648.f);
		drawalls_ft[3] = (singlobalang) * (1.f / 2147483648.f);
		gdx = 0;
		gdy = gxyaspect * (1.0 / 4194304.0);
		if (floor)
			gdy = gxyaspect * (-1.0 / 4194304.0);
		gdo = -ghoriz * gdy;
		gux = drawalls_ft[3] * (viewingrange) / -65536.0;
		gvx = drawalls_ft[2] * (viewingrange) / -65536.0;
		guy = drawalls_ft[0] * gdy;
		gvy = drawalls_ft[1] * gdy;
		guo = drawalls_ft[0] * gdo;
		gvo = drawalls_ft[1] * gdo;
		guo += (drawalls_ft[2] - gux) * ghalfx;
		gvo -= (drawalls_ft[3] + gvx) * ghalfx;

		if (floor)
			clipper.domost((float) x0, (float) y0, (float) x1, (float) y1);
		else {
			gvx = -gvx;
			gvy = -gvy;
			gvo = -gvo; // y-flip skybox floor
			clipper.domost((float) x1, (float) y1, (float) x0, (float) y0);
		}

		skyclamphack = 0;

		Rendering.Skybox.setIndex(0);
	}

	private void drawbackground(int sectnum, double x0, double x1, double y0, double y1, boolean floor) {
		// Parallaxing sky... hacked for Ken's mountain texture;

		SECTOR sec = sector[sectnum];
		int shade = sec.floorshade;
		int pal = sec.floorpal;
		if (!floor) {
			shade = sec.ceilingshade;
			pal = sec.ceilingpal;
		}

		calc_and_apply_skyfog(shade, pal);

		if (!GLSettings.useHighTile.get() || defs == null
				|| defs.texInfo.findTexture(globalpicnum, globalpal, 1) == null)
			drawpapersky(sectnum, x0, x1, y0, y1, floor);
		else
			drawskybox(x0, x1, y0, y1, floor);

		skyclamphack = 0;
		calc_and_apply_fog(shade, sec.visibility, pal);
	}

	private final double[] drawrooms_px = new double[6], drawrooms_py = new double[6], drawrooms_pz = new double[6],
			drawrooms_px2 = new double[6], drawrooms_py2 = new double[6], drawrooms_pz2 = new double[6],
			drawrooms_sx = new double[6], drawrooms_sy = new double[6];

	public double defznear = 0.1;
	public double defzfar = 0.9;

	@Override
	public void drawrooms() // eduke32
	{
		int i, j, n, n2, closest;
		double ox, oy, oz, ox2, oy2, oz2, r;

		globalvisibility = scale(visibility << 2, xdimen, 1027);

		globalhoriz = (globalhoriz * xdimenscale / viewingrange) + (ydimen >> 1);

		if (offscreenrendering) {
			if (setviewcnt == 1)
				ogshang = gshang;
		} else if (ogshang != -1)
			gshang = ogshang;

		resizeglcheck();
		gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glDisable(GL_BLEND);
		gl.glEnable(GL_TEXTURE_2D);
		gl.glEnable(GL_DEPTH_TEST);

		gl.glDepthFunc(GL_LEQUAL); // NEVER,LESS,(,L)EQUAL,GREATER,(NOT,G)EQUAL,ALWAYS
		gl.glDepthRange(defznear, defzfar); // <- this is more widely supported than glPolygonOffset

		rendering = Rendering.Nothing;

		// Polymost supports true look up/down :) Here, we convert horizon to angle.
		// gchang&gshang are cos&sin of this angle (respectively)
		gyxscale = xdimenscale / 131072.0f;
		gxyaspect = (viewingrange / 65536.0) * xyaspect * 5.0 / 262144.0;
		gviewxrange = viewingrange * xdimen / (32768.0f * 1024.0f);
		gcosang = cosglobalang / 262144.0f;
		gsinang = singlobalang / 262144.0f;
		gcosang2 = cosviewingrangeglobalang / 262144.0f;
		gsinang2 = sinviewingrangeglobalang / 262144.0f;
		ghalfx = halfxdimen;
		grhalfxdown10 = 1.0f / (ghalfx * 1024.0f); // viewport
		// global cos/sin height angle
		ghoriz = ydimen >> 1;
		r = (ghoriz - globalhoriz);
		gshang = (float) (r / sqrt(r * r + ghalfx * ghalfx));
		gchang = (float) sqrt(1.0f - gshang * gshang);

		// global cos/sin tilt angle
		gctang = (float) cos(gtang);
		gstang = (float) sin(gtang);

		if (abs(gstang) < .001) // This hack avoids nasty precision bugs in domost()
		{
			gstang = 0;
			if (gctang > 0)
				gctang = 1.0f;
			else
				gctang = -1.0f;
		}

		if (inpreparemirror)
			gstang = -gstang;

		// Generate viewport trapezoid (for handling screen up/down)
		drawrooms_px[0] = drawrooms_px[3] = 0 - 1;
		drawrooms_px[1] = drawrooms_px[2] = windowx2 + 1 - windowx1 + 2;
		drawrooms_py[0] = drawrooms_py[1] = 0 - 1;
		drawrooms_py[2] = drawrooms_py[3] = windowy2 + 1 - windowy1 + 2;
		n = 4;

		for (i = 0; i < n; i++) {
			ox = drawrooms_px[i] - ghalfx;
			oy = drawrooms_py[i] - ghoriz;
			oz = ghalfx;

			// Tilt rotation (backwards)
			ox2 = ox * gctang + oy * gstang;
			oy2 = oy * gctang - ox * gstang;
			oz2 = oz;

			// Up/down rotation (backwards)
			drawrooms_px[i] = ox2;
			drawrooms_py[i] = oy2 * gchang + oz2 * gshang;
			drawrooms_pz[i] = oz2 * gchang - oy2 * gshang;
		}

		// Clip to SCISDIST plane
		n2 = 0;
		for (i = 0; i < n; i++) {
			j = i + 1;
			if (j >= n)
				j = 0;
			if (drawrooms_pz[i] >= SCISDIST) {
				drawrooms_px2[n2] = drawrooms_px[i];
				drawrooms_py2[n2] = drawrooms_py[i];
				drawrooms_pz2[n2] = drawrooms_pz[i];
				n2++;
			}

			if ((drawrooms_pz[i] >= SCISDIST) != (drawrooms_pz[j] >= SCISDIST)) {
				r = (SCISDIST - drawrooms_pz[i]) / (drawrooms_pz[j] - drawrooms_pz[i]);
				drawrooms_px2[n2] = (drawrooms_px[j] - drawrooms_px[i]) * r + drawrooms_px[i];
				drawrooms_py2[n2] = (drawrooms_py[j] - drawrooms_py[i]) * r + drawrooms_py[i];
				drawrooms_pz2[n2] = SCISDIST;
				n2++;
			}
		}

		if (n2 < 3) {
			return;
		}
		for (i = 0; i < n2; i++) {
			r = ghalfx / drawrooms_pz2[i];
			drawrooms_sx[i] = drawrooms_px2[i] * r + ghalfx;
			drawrooms_sy[i] = drawrooms_py2[i] * r + ghoriz;
		}

		clipper.initmosts(drawrooms_sx, drawrooms_sy, n2);

		numscans = numbunches = 0;

		// MASKWALL_BAD_ACCESS
		// Fixes access of stale maskwall[maskwallcnt] (a "scan" index, in BUILD lingo):
		maskwallcnt = 0;
		if (globalcursectnum >= MAXSECTORS) {
			globalcursectnum -= MAXSECTORS;
		} else {
			i = globalcursectnum;
			globalcursectnum = engine.updatesectorz(globalposx, globalposy, globalposz, globalcursectnum);
			if (globalcursectnum < 0)
				globalcursectnum = (short) i;
		}

		scansector(globalcursectnum);

		grhalfxdown10x = grhalfxdown10;

		if (inpreparemirror) {
			grhalfxdown10x = -grhalfxdown10;
			inpreparemirror = false;

			// see engine.c: INPREPAREMIRROR_NO_BUNCHES
			if (numbunches > 0) {
				drawalls(0);
				numbunches--;
				bunchfirst[0] = bunchfirst[numbunches];
				bunchlast[0] = bunchlast[numbunches];
			}
		}

		while (numbunches > 0) {
			Arrays.fill(ptempbuf, 0, numbunches + 3, (byte) 0);
			ptempbuf[0] = 1;
			closest = 0; // Almost works, but not quite :(

			for (i = 1; i < numbunches; ++i) {
				j = polymost_bunchfront(i, closest);
				if (j < 0)
					continue;
				ptempbuf[i] = 1;
				if (j == 0) {
					ptempbuf[closest] = 1;
					closest = i;
				}
			}

			for (i = 0; i < numbunches; ++i) // Double-check
			{
				if (ptempbuf[i] != 0)
					continue;
				j = polymost_bunchfront(i, closest);
				if (j < 0)
					continue;
				ptempbuf[i] = 1;
				if (j == 0) {
					ptempbuf[closest] = 1;
					closest = i;
					i = 0;
				}
			}

			drawalls(closest);

			numbunches--;
			bunchfirst[closest] = bunchfirst[numbunches];
			bunchlast[closest] = bunchlast[numbunches];
		}
	}

	private final Surface[] dmaskwall = new Surface[8];

	private final float[] drawmaskwall_csy = new float[4], drawmaskwall_fsy = new float[4];
	private final int[] drawmaskwall_cz = new int[4], drawmaskwall_fz = new int[4];

	public void drawmaskwall(int damaskwallcnt) {
		float x0, x1, sx0, sy0, sx1, sy1, xp0, yp0, xp1, yp1, oxp0, oyp0, ryp0, ryp1;
		float r, t, t0, t1;
		int i, j, n, n2, z, sectnum, method;

		int m0, m1;
		SECTOR sec, nsec;
		WALL wal, wal2;

		// cullcheckcnt = 0;

		z = maskwall[damaskwallcnt];
		wal = wall[thewall[z]];
		wal2 = wall[wal.point2];
		sectnum = thesector[z];

		if (sectnum == -1 || wal.nextsector == -1)
			return;

		rendering = Rendering.MaskWall.setIndex(thewall[z]);

		sec = sector[sectnum];
		nsec = sector[wal.nextsector];

		globalpicnum = wal.overpicnum;
		if (globalpicnum >= MAXTILES)
			globalpicnum = 0;

		if (engine.getTile(globalpicnum).getType() != AnimType.None)
			globalpicnum += engine.animateoffs(globalpicnum, thewall[z] + 16384);
		globalshade = wal.shade;
		globalpal = wal.pal & 0xFF;
		globalorientation = wal.cstat;

		sx0 = wal.x - globalposx;
		sx1 = wal2.x - globalposx;
		sy0 = wal.y - globalposy;
		sy1 = wal2.y - globalposy;
		yp0 = sx0 * gcosang2 + sy0 * gsinang2;
		yp1 = sx1 * gcosang2 + sy1 * gsinang2;
		if ((yp0 < SCISDIST) && (yp1 < SCISDIST))
			return;
		xp0 = sy0 * gcosang - sx0 * gsinang;
		xp1 = sy1 * gcosang - sx1 * gsinang;

		// Clip to close parallel-screen plane
		oxp0 = xp0;
		oyp0 = yp0;

		t0 = 0.f;

		if (yp0 < SCISDIST) {
			t0 = (SCISDIST - yp0) / (yp1 - yp0);
			xp0 = (xp1 - xp0) * t0 + xp0;
			yp0 = SCISDIST;
		}

		t1 = 1.f;

		if (yp1 < SCISDIST) {
			t1 = (SCISDIST - oyp0) / (yp1 - oyp0);
			xp1 = (xp1 - oxp0) * t1 + oxp0;
			yp1 = SCISDIST;
		}

		m0 = (int) ((wal2.x - wal.x) * t0 + wal.x);
		m1 = (int) ((wal2.y - wal.y) * t0 + wal.y);
		polymost_getzsofslope(sectnum, m0, m1);
		drawmaskwall_cz[0] = (int) dceilzsofslope;
		drawmaskwall_fz[0] = (int) dfloorzsofslope;
		polymost_getzsofslope(wal.nextsector, m0, m1);
		drawmaskwall_cz[1] = (int) dceilzsofslope;
		drawmaskwall_fz[1] = (int) dfloorzsofslope;
		m0 = (int) ((wal2.x - wal.x) * t1 + wal.x);
		m1 = (int) ((wal2.y - wal.y) * t1 + wal.y);
		polymost_getzsofslope(sectnum, m0, m1);
		drawmaskwall_cz[2] = (int) dceilzsofslope;
		drawmaskwall_fz[2] = (int) dfloorzsofslope;
		polymost_getzsofslope(wal.nextsector, m0, m1);
		drawmaskwall_cz[3] = (int) dceilzsofslope;
		drawmaskwall_fz[3] = (int) dfloorzsofslope;

		ryp0 = 1.f / yp0;
		ryp1 = 1.f / yp1;

		// Generate screen coordinates for front side of wall
		x0 = ghalfx * xp0 * ryp0 + ghalfx;
		x1 = ghalfx * xp1 * ryp1 + ghalfx;
		if (x1 <= x0)
			return;

		ryp0 *= gyxscale;
		ryp1 *= gyxscale;

		gdx = (ryp0 - ryp1) * gxyaspect / (x0 - x1);
		gdy = 0;
		gdo = ryp0 * gxyaspect - gdx * x0;

		gux = (t0 * ryp0 - t1 * ryp1) * gxyaspect * ((wal.xrepeat & 0xFF) * 8.0) / (x0 - x1);
		guo = t0 * ryp0 * gxyaspect * ((wal.xrepeat & 0xFF) * 8.0) - gux * x0;
		guo += wal.xpanning * gdo;
		gux += wal.xpanning * gdx;
		guy = 0;

		// mask
		calc_ypanning((!wal.isBottomAligned()) ? max(nsec.ceilingz, sec.ceilingz) : min(nsec.floorz, sec.floorz), ryp0,
				ryp1, x0, x1, wal.ypanning, wal.yrepeat, false);

		if (wal.isXFlip()) {
			t = (wal.xrepeat & 0xFF) * 8 + wal.xpanning * 2;
			gux = gdx * t - gux;
			guy = gdy * t - guy;
			guo = gdo * t - guo;
		}
		if (wal.isYFlip()) {
			gvx = -gvx;
			gvy = -gvy;
			gvo = -gvo;
		}

		method = 1;
		pow2xsplit = 1;
		if (wal.isTransparent()) {
			if (!wal.isTransparent2())
				method = 2;
			else
				method = 3;
		}

		int shade = wal.shade;
		calc_and_apply_fog(shade, sec.visibility, sec.floorpal);

		drawmaskwall_csy[0] = (drawmaskwall_cz[0] - globalposz) * ryp0 + ghoriz;
		drawmaskwall_csy[1] = (drawmaskwall_cz[1] - globalposz) * ryp0 + ghoriz;
		drawmaskwall_csy[2] = (drawmaskwall_cz[2] - globalposz) * ryp1 + ghoriz;
		drawmaskwall_csy[3] = (drawmaskwall_cz[3] - globalposz) * ryp1 + ghoriz;

		drawmaskwall_fsy[0] = (drawmaskwall_fz[0] - globalposz) * ryp0 + ghoriz;
		drawmaskwall_fsy[1] = (drawmaskwall_fz[1] - globalposz) * ryp0 + ghoriz;
		drawmaskwall_fsy[2] = (drawmaskwall_fz[2] - globalposz) * ryp1 + ghoriz;
		drawmaskwall_fsy[3] = (drawmaskwall_fz[3] - globalposz) * ryp1 + ghoriz;

		// Clip 2 quadrilaterals
		// /csy3
		// / |
		// csy0------/----csy2
		// | /xxxxxxx|
		// | /xxxxxxxxx|
		// csy1/xxxxxxxxxxx|
		// |xxxxxxxxxxx/fsy3
		// |xxxxxxxxx/ |
		// |xxxxxxx/ |
		// fsy0----/------fsy2
		// | /
		// fsy1/

		dmaskwall[0].px = x0;
		dmaskwall[0].py = drawmaskwall_csy[1];
		dmaskwall[1].px = x1;
		dmaskwall[1].py = drawmaskwall_csy[3];
		dmaskwall[2].px = x1;
		dmaskwall[2].py = drawmaskwall_fsy[3];
		dmaskwall[3].px = x0;
		dmaskwall[3].py = drawmaskwall_fsy[1];
		n = 4;

		// Clip to (x0,csy[0])-(x1,csy[2])
		n2 = 0;
		t1 = (float) -((dmaskwall[0].px - x0) * (drawmaskwall_csy[2] - drawmaskwall_csy[0])
				- (dmaskwall[0].py - drawmaskwall_csy[0]) * (x1 - x0));
		for (i = 0; i < n; i++) {
			j = i + 1;
			if (j >= n)
				j = 0;

			t0 = t1;
			t1 = (float) -((dmaskwall[j].px - x0) * (drawmaskwall_csy[2] - drawmaskwall_csy[0])
					- (dmaskwall[j].py - drawmaskwall_csy[0]) * (x1 - x0));
			if (t0 >= 0) {
				dmaskwall[n2].px2 = dmaskwall[i].px;
				dmaskwall[n2].py2 = dmaskwall[i].py;
				n2++;
			}
			if ((t0 >= 0) != (t1 >= 0)) {
				r = t0 / (t0 - t1);
				dmaskwall[n2].px2 = (dmaskwall[j].px - dmaskwall[i].px) * r + dmaskwall[i].px;
				dmaskwall[n2].py2 = (dmaskwall[j].py - dmaskwall[i].py) * r + dmaskwall[i].py;
				n2++;
			}
		}
		if (n2 < 3)
			return;

		// Clip to (x1,fsy[2])-(x0,fsy[0])
		n = 0;
		t1 = (float) -((dmaskwall[0].px2 - x1) * (drawmaskwall_fsy[0] - drawmaskwall_fsy[2])
				- (dmaskwall[0].py2 - drawmaskwall_fsy[2]) * (x0 - x1));
		for (i = 0; i < n2; i++) {
			j = i + 1;
			if (j >= n2)
				j = 0;

			t0 = t1;
			t1 = (float) -((dmaskwall[j].px2 - x1) * (drawmaskwall_fsy[0] - drawmaskwall_fsy[2])
					- (dmaskwall[j].py2 - drawmaskwall_fsy[2]) * (x0 - x1));
			if (t0 >= 0) {
				dmaskwall[n].px = dmaskwall[i].px2;
				dmaskwall[n].py = dmaskwall[i].py2;
				n++;
			}
			if ((t0 >= 0) != (t1 >= 0)) {
				r = t0 / (t0 - t1);
				dmaskwall[n].px = (dmaskwall[j].px2 - dmaskwall[i].px2) * r + dmaskwall[i].px2;
				dmaskwall[n].py = (dmaskwall[j].py2 - dmaskwall[i].py2) * r + dmaskwall[i].py2;
				n++;
			}
		}
		if (n < 3)
			return;

		gl.glDepthRange(defznear + 0.000001, defzfar - 0.00001);
		drawpoly(dmaskwall, n, method);
		gl.glDepthRange(defznear, defzfar);
	}

	private static final Vector2 projPoint = new Vector2();

	private int getclosestpointonwall(int posx, int posy, int dawall, Vector2 n) {
		WALL w = wall[dawall];
		WALL p2 = wall[wall[dawall].point2];
		int dx = p2.x - w.x;
		int dy = p2.y - w.y;

		float i = dx * (posx - w.x) + dy * (posy - w.y);

		if (i < 0)
			return 1;

		float j = dx * dx + dy * dy;

		if (i > j)
			return 1;

		i /= j;

		n.set(dx * i + w.x, dy * i + w.y);

		return 0;
	}

	private final float TSPR_OFFSET_FACTOR = 0.000008f;

	private float TSPR_OFFSET(SPRITE tspr, long dist) {
		float offset = (TSPR_OFFSET_FACTOR + ((tspr.owner != -1 ? tspr.owner & 31 : 1) * TSPR_OFFSET_FACTOR)) * dist
				* 0.025f;
		return -offset;
	}

	private final Surface[] dsprite = new Surface[6];
	private final float[] drawsprite_ft = new float[4];
	private final Vector2[] dsin = new Vector2[MAXSPRITES];
	private final Vector2[] dcoord = new Vector2[MAXSPRITES];
	private final int[] spritewall = new int[MAXSPRITES];

	private void drawsprite(int snum) {
		float f, c, s, fx, fy, sx0, sy0, sx1, xp0, yp0, xp1, yp1, oxp0, oyp0, ryp0, ryp1;
		float x0, y0, x1, y1, sc0, sf0, sc1, sf1, xv, yv, t0, t1;
		int i, j, spritenum, xoff = 0, yoff = 0, method, npoints;
		SPRITE tspr;
		int oldsizx, oldsizy;
		int tsizx, tsizy;

		tspr = tspriteptr[snum];

		if (tspr.owner < 0 || tspr.picnum < 0 || tspr.picnum >= MAXTILES || tspr.sectnum < 0)
			return;

		globalpicnum = tspr.picnum;
		globalshade = tspr.shade;
		globalpal = tspr.pal & 0xFF;
		globalorientation = tspr.cstat;
		spritenum = tspr.owner;
		Tile pic = engine.getTile(globalpicnum);

		if ((globalorientation & 48) != 48) {
			boolean flag;

			if (pic.getType() != AnimType.None) {
				globalpicnum += engine.animateoffs(globalpicnum, spritenum + 32768);
				pic = engine.getTile(globalpicnum);
			}

			flag = (GLSettings.useHighTile.get() && h_xsize[globalpicnum] != 0);
			xoff = tspr.xoffset;
			yoff = tspr.yoffset;
			xoff += flag ? h_xoffs[globalpicnum] : pic.getOffsetX();
			yoff += flag ? h_yoffs[globalpicnum] : pic.getOffsetY();
		}

		method = 1 + 4;
		if ((tspr.cstat & 2) != 0) {
			if ((tspr.cstat & 512) == 0)
				method = 2 + 4;
			else
				method = 3 + 4;
		}

		Spriteext sprext = defs.mapInfo.getSpriteInfo(tspr.owner);

		if (sprext != null) {
        }

		int posx = tspr.x;
		int posy = tspr.y;

		int shade = (int) (globalshade / 1.5f);

		while (sprext == null || !sprext.isNotModel()) {
			rendering = Rendering.Model.setIndex(snum);

			if (GLSettings.useModels.get()) {
				GLModel md = modelManager.getModel(globalpicnum, globalpal);
				if (md != null) {
					calc_and_apply_fog(shade, sector[tspr.sectnum].visibility, sector[tspr.sectnum].floorpal);

					if (tspr.owner < 0 || tspr.owner >= MAXSPRITES /* || tspr.statnum == TSPR_MIRROR */ ) {
						if (mdrenderer.mddraw(md, tspr, xoff, yoff) != 0)
							return;
						break; // else, render as flat sprite
					}

					if (mdrenderer.mddraw(md, tspr, xoff, yoff) != 0)
						return;
					break; // else, render as flat sprite
				}
			}

			if (BuildSettings.useVoxels.get()) {
				int dist = (posx - globalposx) * (posx - globalposx) + (posy - globalposy) * (posy - globalposy);
				if (dist < 48000L * 48000L) {
					GLVoxel vox = (GLVoxel) modelManager.getVoxel(globalpicnum);
					if (vox != null) {
						calc_and_apply_fog(shade, sector[tspr.sectnum].visibility, sector[tspr.sectnum].floorpal);

						if ((tspr.cstat & 48) != 48) {
							if (mdrenderer.voxdraw(vox, tspr) != 0)
								return;
							break; // else, render as flat sprite
						}

						if ((tspr.cstat & 48) == 48) {
							mdrenderer.voxdraw(vox, tspr);
							return;
						}
					}
				}
			}
			break;
		}

		rendering = Rendering.Sprite.setIndex(snum);
		calc_and_apply_fog(shade, sector[tspr.sectnum].visibility, sector[tspr.sectnum].floorpal);

        oldsizx = tsizx = pic.getWidth();
		oldsizy = tsizy = pic.getHeight();

		if (GLSettings.useHighTile.get() && h_xsize[globalpicnum] != 0) {
			tsizx = h_xsize[globalpicnum];
			tsizy = h_ysize[globalpicnum];
		}

		if (tsizx <= 0 || tsizy <= 0)
			return;

		long dist;

		float foffs, offsx, offsy;
		int ang;
		switch ((globalorientation >> 4) & 3) {
		case 0: // Face sprite
			// Project 3D to 2D
			if ((globalorientation & 4) != 0)
				xoff = -xoff;
			// NOTE: yoff not negated not for y flipping, unlike wall and floor
			// aligned sprites.

			dist = engine.qdist(globalposx - tspr.x, globalposy - tspr.y);
			ang = (engine.getangle(tspr.x - globalposx, tspr.y - globalposy) + 1024) & 2047;
			foffs = TSPR_OFFSET(tspr, dist);
			dist *= (dist >> 7);

			offsx = (sintable[(ang + 512) & 2047] >> 6) * foffs;
			offsy = (sintable[(ang) & 2047] >> 6) * foffs;

			sx0 = tspr.x - globalposx - offsx;
			sy0 = tspr.y - globalposy - offsy;
			xp0 = sy0 * gcosang - sx0 * gsinang;
			yp0 = sx0 * gcosang2 + sy0 * gsinang2;

			if (yp0 <= SCISDIST)
				return;
			ryp0 = 1.0f / yp0;
			sx0 = ghalfx * xp0 * ryp0 + ghalfx;
			sy0 = (tspr.z - globalposz) * gyxscale * ryp0 + ghoriz;

			f = ryp0 * xdimen * (1.0f / 160.f);
			fx = (tspr.xrepeat) * f;
			fy = (tspr.yrepeat) * f * (yxaspect * (1.0f / 65536.f));

			sx0 -= fx * xoff;
			if ((tsizx & 1) != 0)
				sx0 += fx * 0.5f;
			sy0 -= fy * yoff;
			if ((globalorientation & 128) != 0 && (tsizy & 1) != 0)
				sy0 += fy * 0.5f;

			fx *= (tsizx);
			fy *= (tsizy);

			dsprite[0].px = dsprite[3].px = sx0 - fx * .5f;
			dsprite[1].px = dsprite[2].px = sx0 + fx * .5f;
			if ((globalorientation & 128) == 0) {
				dsprite[0].py = dsprite[1].py = sy0 - fy;
				dsprite[2].py = dsprite[3].py = sy0;
			} else {
				dsprite[0].py = dsprite[1].py = sy0 - fy * .5f;
				dsprite[2].py = dsprite[3].py = sy0 + fy * .5f;
			}

			gdx = gdy = guy = gvx = 0;
			gdo = ryp0 * gviewxrange;
			if ((globalorientation & 4) == 0) {
				gux = tsizx * gdo / (dsprite[1].px - dsprite[0].px + .002);
				guo = -gux * (dsprite[0].px - .001);
			} else {
				gux = tsizx * gdo / (dsprite[0].px - dsprite[1].px - .002);
				guo = -gux * (dsprite[1].px + .001);
			}
			if ((globalorientation & 8) == 0) {
				gvy = tsizy * gdo / (dsprite[3].py - dsprite[0].py + .002);
				gvo = -gvy * (dsprite[0].py - .001);
			} else {
				gvy = tsizy * gdo / (dsprite[0].py - dsprite[3].py - .002);
				gvo = -gvy * (dsprite[3].py + .001);
			}

			// sprite panning
			if (sprext != null) {
				if (sprext.xpanning != 0) {
					guy -= gdy * ((sprext.xpanning) / 255.f) * tsizx;
					guo -= gdo * ((sprext.xpanning) / 255.f) * tsizx;
					srepeat = 1;
				}
				if (sprext.ypanning != 0) {
					gvy -= gdy * ((sprext.ypanning) / 255.f) * tsizy;
					gvo -= gdo * ((sprext.ypanning) / 255.f) * tsizy;
					trepeat = 1;
				}
			}

			// Clip sprites to ceilings/floors when no parallaxing and not
			// sloped
			if ((sector[tspr.sectnum].ceilingstat & 3) == 0) {
				sy0 = ((sector[tspr.sectnum].ceilingz - globalposz)) * gyxscale * ryp0 + ghoriz;
				if (dsprite[0].py < sy0)
					dsprite[0].py = dsprite[1].py = sy0;
			}
			if ((sector[tspr.sectnum].floorstat & 3) == 0) {
				sy0 = ((sector[tspr.sectnum].floorz - globalposz)) * gyxscale * ryp0 + ghoriz;
				if (dsprite[2].py > sy0)
					dsprite[2].py = dsprite[3].py = sy0;
			}

			pic.setWidth(tsizx).setHeight(tsizy);

			gl.glDepthRange(defznear, defzfar - (10f / (dist + 1)));

			pow2xsplit = 0;
			drawpoly(dsprite, 4, method);

			gl.glDepthRange(defznear, defzfar);

			srepeat = 0;
			trepeat = 0;
			break;

		case 1: // Wall sprite

			// Project 3D to 2D
			if ((globalorientation & 4) != 0)
				xoff = -xoff;
			if ((globalorientation & 8) != 0)
				yoff = -yoff;

			posx += dcoord[tspr.owner].x;
			posy += dcoord[tspr.owner].y;

			xv = tspr.xrepeat * (sintable[(tspr.ang) & 2047] * (1.0f / 65536.f) - dsin[tspr.owner].x);
			yv = tspr.xrepeat * (sintable[(tspr.ang + 1536) & 2047] * (1.0f / 65536.f) - dsin[tspr.owner].y);

			f = (float) (tsizx >> 1) + (float) xoff;
			x0 = posx - globalposx - xv * f;
			x1 = xv * tsizx + x0;
			y0 = posy - globalposy - yv * f;
			y1 = yv * tsizx + y0;

			yp0 = x0 * gcosang2 + y0 * gsinang2;
			yp1 = x1 * gcosang2 + y1 * gsinang2;
			if ((yp0 <= SCISDIST) && (yp1 <= SCISDIST))
				return;
			xp0 = y0 * gcosang - x0 * gsinang;
			xp1 = y1 * gcosang - x1 * gsinang;

			// Clip to close parallel-screen plane
			oxp0 = xp0;
			oyp0 = yp0;
			if (yp0 < SCISDIST) {
				t0 = (SCISDIST - yp0) / (yp1 - yp0);
				xp0 = (xp1 - xp0) * t0 + xp0;
				yp0 = SCISDIST;
			} else {
				t0 = 0.f;
			}
			if (yp1 < SCISDIST) {
				t1 = (SCISDIST - oyp0) / (yp1 - oyp0);
				xp1 = (xp1 - oxp0) * t1 + oxp0;
				yp1 = SCISDIST;
			} else {
				t1 = 1.f;
			}

			f = ((float) tspr.yrepeat) * (float) tsizy * 4;

			ryp0 = 1.0f / yp0;
			ryp1 = 1.0f / yp1;
			sx0 = ghalfx * xp0 * ryp0 + ghalfx;
			sx1 = ghalfx * xp1 * ryp1 + ghalfx;
			ryp0 *= gyxscale;
			ryp1 *= gyxscale;

			tspr.z -= ((yoff * tspr.yrepeat) << 2);
			if ((globalorientation & 128) != 0) {
				tspr.z += ((tsizy * tspr.yrepeat) << 1);
				if ((tsizy & 1) != 0)
					tspr.z += (tspr.yrepeat << 1); // Odd yspans
			}

			sc0 = ((tspr.z - globalposz - f)) * ryp0 + ghoriz;
			sc1 = ((tspr.z - globalposz - f)) * ryp1 + ghoriz;
			sf0 = ((tspr.z - globalposz)) * ryp0 + ghoriz;
			sf1 = ((tspr.z - globalposz)) * ryp1 + ghoriz;

			gdx = (ryp0 - ryp1) * gxyaspect / (sx0 - sx1);
			gdy = 0;
			gdo = ryp0 * gxyaspect - gdx * sx0;

			if ((globalorientation & 4) != 0) {
				t0 = 1.f - t0;
				t1 = 1.f - t1;
			}

			// sprite panning
			if (sprext != null && sprext.xpanning != 0) {
				t0 -= ((sprext.xpanning) / 255.f);
				t1 -= ((sprext.xpanning) / 255.f);
				srepeat = 1;
			}

			gux = (t0 * ryp0 - t1 * ryp1) * gxyaspect * tsizx / (sx0 - sx1);
			guy = 0;
			guo = t0 * ryp0 * gxyaspect * tsizx - gux * sx0;

			f = (float) ((tsizy) * (gdx * sx0 + gdo) / ((sx0 - sx1) * (sc0 - sf0)));
			if ((globalorientation & 8) == 0) {
				gvx = (sc0 - sc1) * f;
				gvy = (sx1 - sx0) * f;
				gvo = -gvx * sx0 - gvy * sc0;
			} else {
				gvx = (sf1 - sf0) * f;
				gvy = (sx0 - sx1) * f;
				gvo = -gvx * sx0 - gvy * sf0;
			}

			// sprite panning
			if (sprext != null && sprext.ypanning != 0) {
				gvx -= gdx * ((sprext.ypanning) / 255.f) * tsizy;
				gvy -= gdy * ((sprext.ypanning) / 255.f) * tsizy;
				gvo -= gdo * ((sprext.ypanning) / 255.f) * tsizy;
				trepeat = 1;
			}

			// Clip sprites to ceilings/floors when no parallaxing
			if (tspr.sectnum != -1 && (sector[tspr.sectnum].ceilingstat & 1) == 0) {
				f = ((float) tspr.yrepeat) * (float) tsizy * 4;
				if (sector[tspr.sectnum].ceilingz > tspr.z - f) {
					sc0 = ((sector[tspr.sectnum].ceilingz - globalposz)) * ryp0 + ghoriz;
					sc1 = ((sector[tspr.sectnum].ceilingz - globalposz)) * ryp1 + ghoriz;
				}
			}
			if (tspr.sectnum != -1 && (sector[tspr.sectnum].floorstat & 1) == 0) {
				if (sector[tspr.sectnum].floorz < tspr.z) {
					sf0 = ((sector[tspr.sectnum].floorz - globalposz)) * ryp0 + ghoriz;
					sf1 = ((sector[tspr.sectnum].floorz - globalposz)) * ryp1 + ghoriz;
				}
			}

			if (sx0 > sx1) {
				if ((globalorientation & 64) != 0)
					return; // 1-sided sprite
				f = sx0;
				sx0 = sx1;
				sx1 = f;
				f = sc0;
				sc0 = sc1;
				sc1 = f;
				f = sf0;
				sf0 = sf1;
				sf1 = f;
			}

			dsprite[0].px = sx0;
			dsprite[0].py = sc0;
			dsprite[1].px = sx1;
			dsprite[1].py = sc1;
			dsprite[2].px = sx1;
			dsprite[2].py = sf1;
			dsprite[3].px = sx0;
			dsprite[3].py = sf0;

			pic.setWidth(tsizx).setHeight(tsizy);

			if (spritewall[tspr.owner] != -1 && (tspr.cstat & 2) != 0)
				gl.glDepthMask(false);

			dist = engine.qdist(globalposx - tspr.x, globalposy - tspr.y);
			dist *= (dist >> 7);

			if (spritewall[tspr.owner] != -1 && dist > 0) {
				int dang = (((int) globalang - tspr.ang) & 2047) - 1024;
				if (dang >= -512 && dang <= 512)
					gl.glDepthRange(defznear, defzfar - (Math.min(dist / 16384f, 40) / dist));
			}

			curpolygonoffset += 0.01f;
			gl.glPolygonOffset(-curpolygonoffset, -curpolygonoffset);

			pow2xsplit = 0;
			drawpoly(dsprite, 4, method);

			gl.glPolygonOffset(0, 0);
			gl.glDepthRange(defznear, defzfar);
			if (spritewall[tspr.owner] != -1 && (tspr.cstat & 2) != 0)
				gl.glDepthMask(true);

			srepeat = 0;
			trepeat = 0;

			break;

		case 2: // Floor sprite

			if ((globalorientation & 64) != 0)
				if ((globalposz > tspr.z) == ((globalorientation & 8) == 0))
					return;
			if ((globalorientation & 4) > 0)
				xoff = -xoff;
			if ((globalorientation & 8) > 0)
				yoff = -yoff;

            i = (tspr.ang & 2047);
			c = (float) (sintable[(i + 512) & 2047] / 65536.0);
			s = (float) (sintable[i] / 65536.0);
			x0 = (float) ((tsizx >> 1) - xoff) * tspr.xrepeat;
			y0 = (float) ((tsizy >> 1) - yoff) * tspr.yrepeat;
			x1 = (float) ((tsizx >> 1) + xoff) * tspr.xrepeat;
			y1 = (float) ((tsizy >> 1) + yoff) * tspr.yrepeat;

			// Project 3D to 2D
			for (j = 0; j < 4; j++) {
				sx0 = tspr.x - globalposx;
				sy0 = tspr.y - globalposy;
				if (((j + 0) & 2) != 0) {
					sy0 -= s * y0;
					sx0 -= c * y0;
				} else {
					sy0 += s * y1;
					sx0 += c * y1;
				}
				if (((j + 1) & 2) != 0) {
					sx0 -= s * x0;
					sy0 += c * x0;
				} else {
					sx0 += s * x1;
					sy0 -= c * x1;
				}

				dsprite[j].px = sy0 * gcosang - sx0 * gsinang;
				dsprite[j].py = sx0 * gcosang2 + sy0 * gsinang2;
			}

			if (tspr.z < globalposz) // if floor sprite is above you, reverse order of points
			{
				f = (float) dsprite[0].px;
				dsprite[0].px = dsprite[1].px;
				dsprite[1].px = f;
				f = (float) dsprite[0].py;
				dsprite[0].py = dsprite[1].py;
				dsprite[1].py = f;
				f = (float) dsprite[2].px;
				dsprite[2].px = dsprite[3].px;
				dsprite[3].px = f;
				f = (float) dsprite[2].py;
				dsprite[2].py = dsprite[3].py;
				dsprite[3].py = f;
			}

			// Clip to SCISDIST plane
			npoints = 0;
			for (i = 0; i < 4; i++) {
				j = ((i + 1) & 3);
				if (dsprite[i].py >= SCISDIST) {
					dsprite[npoints].px2 = dsprite[i].px;
					dsprite[npoints].py2 = dsprite[i].py;
					npoints++;
				}
				if ((dsprite[i].py >= SCISDIST) != (dsprite[j].py >= SCISDIST)) {
					f = (float) ((SCISDIST - dsprite[i].py) / (dsprite[j].py - dsprite[i].py));
					dsprite[npoints].px2 = (float) ((dsprite[j].px - dsprite[i].px) * f + dsprite[i].px);
					dsprite[npoints].py2 = (float) ((dsprite[j].py - dsprite[i].py) * f + dsprite[i].py);
					npoints++;
				}
			}

			if (npoints < 3)
				return;

			// Project rotated 3D points to screen
			f = (tspr.z - globalposz) * gyxscale;
			for (j = 0; j < npoints; j++) {
				ryp0 = (float) (1.0 / dsprite[j].py2);
				dsprite[j].px = ghalfx * dsprite[j].px2 * ryp0 + ghalfx;
				dsprite[j].py = f * ryp0 + ghoriz;
			}

			// gd? Copied from floor rendering code
			gdx = 0;
			gdy = gxyaspect / (tspr.z - globalposz);
			gdo = -ghoriz * gdy;
			// copied&modified from relative alignment
			xv = tspr.x + s * x1 + c * y1;
			fx = -(x0 + x1) * s;
			yv = tspr.y + s * y1 - c * x1;
			fy = +(x0 + x1) * c;
			f = (float) (1.0f / sqrt(fx * fx + fy * fy));
			fx *= f;
			fy *= f;
			drawsprite_ft[2] = singlobalang * fy + cosglobalang * fx;
			drawsprite_ft[3] = singlobalang * fx - cosglobalang * fy;
			drawsprite_ft[0] = (globalposy - yv) * fy + (globalposx - xv) * fx;
			drawsprite_ft[1] = (globalposx - xv) * fy - (globalposy - yv) * fx;
			gux = (double) drawsprite_ft[3] * ((double) viewingrange) / (-65536.0 * 262144.0);
			gvx = (double) drawsprite_ft[2] * ((double) viewingrange) / (-65536.0 * 262144.0);
			guy = drawsprite_ft[0] * gdy;
			gvy = drawsprite_ft[1] * gdy;
			guo = drawsprite_ft[0] * gdo;
			gvo = drawsprite_ft[1] * gdo;
			guo += (drawsprite_ft[2] / 262144.0 - gux) * ghalfx;
			gvo -= (drawsprite_ft[3] / 262144.0 + gvx) * ghalfx;
			f = 4.0f / tspr.xrepeat;
			gux *= f;
			guy *= f;
			guo *= f;
			f = -4.0f / tspr.yrepeat;
			gvx *= f;
			gvy *= f;
			gvo *= f;
			if ((globalorientation & 4) != 0) {
				gux = (tsizx) * gdx - gux;
				guy = (tsizx) * gdy - guy;
				guo = (tsizx) * gdo - guo;
			}

			// sprite panning
			if (sprext != null) {
				if (sprext.xpanning != 0) {
					guy -= gdy * ((sprext.xpanning) / 255.f) * tsizx;
					guo -= gdo * ((sprext.xpanning) / 255.f) * tsizx;
					srepeat = 1;
				}
				if (sprext.ypanning != 0) {
					gvy -= gdy * ((sprext.ypanning) / 255.f) * tsizy;
					gvo -= gdo * ((sprext.ypanning) / 255.f) * tsizy;
					trepeat = 1;
				}
			}

			pic.setWidth(tsizx).setHeight(tsizy);

			if ((tspr.cstat & 2) != 0)
				gl.glDepthMask(false);

			gl.glDepthRange(defznear + 0.000001, defzfar - 0.00001);

			curpolygonoffset += 0.01f;
			gl.glPolygonOffset(-curpolygonoffset, -curpolygonoffset);

			pow2xsplit = 0;
			drawpoly(dsprite, npoints, method);

			gl.glDepthRange(defznear, defzfar);

			if ((tspr.cstat & 2) != 0)
				gl.glDepthMask(true);

			srepeat = 0;
			trepeat = 0;

			break;
		case 3: // Voxel sprite
			break;
		}

		pic.setWidth(oldsizx).setHeight(oldsizy);

		if (automapping == 1)
			show2dsprite[snum >> 3] |= pow2char[snum & 7];
	}

	@Override
	public void palfade(HashMap<String, FadeEffect> fades) {

		gl.glMatrixMode(GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		gl.glDisable(GL_DEPTH_TEST);
		gl.glDisable(GL_ALPHA_TEST);
		gl.glDisable(GL_TEXTURE_2D);
		globalfog.disable();

		gl.glEnable(GL_BLEND);
		boolean hasShader = texshader != null && texshader.isBinded();
		if (hasShader)
			texshader.end();

		palfadergb.draw(null);
		if (fades != null) {
			Iterator<FadeEffect> it = fades.values().iterator();
			while (it.hasNext()) {
				FadeEffect obj = it.next();
				obj.draw(null);
			}
		}

		if (hasShader)
			texshader.begin();

		gl.glMatrixMode(GL_MODELVIEW);
		gl.glPopMatrix();
		gl.glMatrixMode(GL_PROJECTION);
		gl.glPopMatrix();

		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		globalfog.enable();
	}

	@Override
	public void precache(int dapicnum, int dapalnum, int datype) {
		// dapicnum and dapalnum are like you'd expect
		// datype is 0 for a wall/floor/ceiling and 1 for a sprite
		// basically this just means walls are repeating
		// while sprites are clamped

		if ((palookup[dapalnum] == null) && (dapalnum < (MAXPALOOKUPS - RESERVEDPALS)))
			return;

//		Console.Println("precached " + dapicnum + " " + dapalnum + " type " + datype);
		textureCache.precache(texshader != null ? PixelFormat.Pal8 : PixelFormat.Rgba, dapicnum, dapalnum, datype);

		if (datype == 0)
			return;

		modelManager.preload(dapicnum, dapalnum, true);
	}

	protected void calc_and_apply_fog(int shade, int vis, int pal) {
		globalfog.shade = shade;
		// globalfog.combvis = globalvisibility * ((vis + 16) & 0xFF);

		globalfog.combvis = globalvisibility;
		if (vis != 0)
			globalfog.combvis = mulscale(globalvisibility, (vis + 16) & 0xFF, 4);

		globalfog.pal = pal;
		globalfog.calc();
	}

	protected void calc_and_apply_skyfog(int shade, int pal) {
		globalfog.shade = shade;
		globalfog.combvis = 0;
		globalfog.pal = pal;
		globalfog.calc();
	}

	public static void equation(Vector3 ret, float x1, float y1, float x2, float y2) {
		if ((x2 - x1) != 0) {
			ret.x = (y2 - y1) / (x2 - x1);
			ret.y = -1;
			ret.z = (y1 - (ret.x * x1));
		} else // vertical
		{
			ret.x = 1;
			ret.y = 0;
			ret.z = -x1;
		}
	}

	public boolean sameside(Vector3 eq, Vector2 p1, Vector2 p2) {
		float sign1, sign2;

		sign1 = eq.x * p1.x + eq.y * p1.y + eq.z;
		sign2 = eq.x * p2.x + eq.y * p2.y + eq.z;

		sign1 = sign1 * sign2;
		// OSD_Printf("SAME SIDE !\n");
		return sign1 > 0;
		// OSD_Printf("OPPOSITE SIDE !\n");
	}

	public void swapsprite(int k, int l, boolean z) {
		SPRITE stmp = tspriteptr[k];
		tspriteptr[k] = tspriteptr[l];
		tspriteptr[l] = stmp;

		int tmp = spritesx[k];
		spritesx[k] = spritesx[l];
		spritesx[l] = tmp;
		tmp = spritesy[k];
		spritesy[k] = spritesy[l];
		spritesy[l] = tmp;

		if (z) {
			tmp = spritesz[k];
			spritesz[k] = spritesz[l];
			spritesz[l] = tmp;
		}
	}

	// PLAG: sorting stuff
	private static final Vector3 drawmasks_maskeq = new Vector3();
	private static final Vector3 drawmasks_p1eq = new Vector3();
	private static final Vector3 drawmasks_p2eq = new Vector3();
	private static final Vector2 drawmasks_dot = new Vector2(), drawmasks_dot2 = new Vector2(),
			drawmasks_middle = new Vector2(), drawmasks_pos = new Vector2(), drawmasks_spr = new Vector2();

	@Override
	public void drawmasks() {
		int i, j, k, l, gap, xs, ys, xp, yp, yoff, yspan;
		boolean modelp;

		for (i = spritesortcnt - 1; i >= 0; i--) {
			tspriteptr[i] = tsprite[i];
			if (tspriteptr[i].picnum < 0 || tspriteptr[i].picnum > MAXTILES)
				continue;

			xs = tspriteptr[i].x - globalposx;
			ys = tspriteptr[i].y - globalposy;
			yp = dmulscale(xs, cosviewingrangeglobalang, ys, sinviewingrangeglobalang, 6);

			modelp = (GLSettings.useModels.get() && defs != null
					&& defs.mdInfo.getModelInfo(tspriteptr[i].picnum) != null);

			if (yp > (4 << 8)) {
				xp = dmulscale(ys, cosglobalang, -xs, singlobalang, 6);
				if (mulscale(abs(xp + yp), xdimen, 24) >= yp) {
					spritesortcnt--; // Delete face sprite if on wrong side!
					if (i == spritesortcnt)
						continue;
					tspriteptr[i] = tspriteptr[spritesortcnt];
					spritesx[i] = spritesx[spritesortcnt];
					spritesy[i] = spritesy[spritesortcnt];
					continue;
				}
				spritesx[i] = scale(xp + yp, xdimen << 7, yp);
			} else if ((tspriteptr[i].cstat & 48) == 0) {
				if (!modelp) {
					spritesortcnt--; // Delete face sprite if on wrong side!
					if (i != spritesortcnt) {
						tspriteptr[i] = tspriteptr[spritesortcnt];
						spritesx[i] = spritesx[spritesortcnt];
						spritesy[i] = spritesy[spritesortcnt];
					}
					continue;
				}
			}
			spritesy[i] = yp;
		}

		gap = 1;
		while (gap < spritesortcnt)
			gap = (gap << 1) + 1;
		for (gap >>= 1; gap > 0; gap >>= 1) // Sort sprite list
			for (i = 0; i < spritesortcnt - gap; i++)
				for (l = i; l >= 0; l -= gap) {
					if (spritesy[l] <= spritesy[l + gap])
						break;
					swapsprite(l, l + gap, false);
				}
		if (spritesortcnt > 0)
			spritesy[spritesortcnt] = (spritesy[spritesortcnt - 1] ^ 1);

		ys = spritesy[0];
		i = 0;
		for (j = 1; j <= spritesortcnt; j++) {
			if (spritesy[j] == ys)
				continue;
			ys = spritesy[j];
			if (j > i + 1) {
				for (k = i; k < j; k++) {
					spritesz[k] = tspriteptr[k].z;
					if (tspriteptr[k].picnum < 0 || tspriteptr[k].picnum > MAXTILES)
						continue;

					if ((tspriteptr[k].cstat & 48) != 32) {
						Tile pic = engine.getTile(tspriteptr[k].picnum);
						yoff = (byte) (pic.getOffsetY() + tspriteptr[k].yoffset);
						spritesz[k] -= ((yoff * tspriteptr[k].yrepeat) << 2);
						yspan = (pic.getHeight() * tspriteptr[k].yrepeat << 2);
						if ((tspriteptr[k].cstat & 128) == 0)
							spritesz[k] -= (yspan >> 1);
						if (klabs(spritesz[k] - globalposz) < (yspan >> 1))
							spritesz[k] = globalposz;
					}
				}
				for (k = i + 1; k < j; k++)
					for (l = i; l < k; l++)
						if (klabs(spritesz[k] - globalposz) < klabs(spritesz[l] - globalposz))
							swapsprite(k, l, true);
				for (k = i + 1; k < j; k++)
					for (l = i; l < k; l++) {
						if (tspriteptr[k].statnum < tspriteptr[l].statnum)
							swapsprite(k, l, false);
						if ((tspriteptr[k].cstat & 2) != 0) // transparent sort
							swapsprite(k, l, true);
					}
			}
			i = j;
		}

		curpolygonoffset = 0;

		drawmasks_pos.x = globalposx;
		drawmasks_pos.y = globalposy;

		gl.glEnable(GL10.GL_POLYGON_OFFSET_FILL);

		while (maskwallcnt != 0) {

			maskwallcnt--;

			drawmasks_dot.x = wall[thewall[maskwall[maskwallcnt]]].x;
			drawmasks_dot.y = wall[thewall[maskwall[maskwallcnt]]].y;
			drawmasks_dot2.x = wall[wall[thewall[maskwall[maskwallcnt]]].point2].x;
			drawmasks_dot2.y = wall[wall[thewall[maskwall[maskwallcnt]]].point2].y;

			equation(drawmasks_maskeq, drawmasks_dot.x, drawmasks_dot.y, drawmasks_dot2.x, drawmasks_dot2.y);
			equation(drawmasks_p1eq, drawmasks_pos.x, drawmasks_pos.y, drawmasks_dot.x, drawmasks_dot.y);
			equation(drawmasks_p2eq, drawmasks_pos.x, drawmasks_pos.y, drawmasks_dot2.x, drawmasks_dot2.y);

			drawmasks_middle.x = (drawmasks_dot.x + drawmasks_dot2.x) / 2;
			drawmasks_middle.y = (drawmasks_dot.y + drawmasks_dot2.y) / 2;

			i = spritesortcnt;
			while (i != 0) {
				i--;
				if (tspriteptr[i] != null) {
					drawmasks_spr.x = tspriteptr[i].x;
					drawmasks_spr.y = tspriteptr[i].y;

					if (!sameside(drawmasks_maskeq, drawmasks_spr, drawmasks_pos)
							&& sameside(drawmasks_p1eq, drawmasks_middle, drawmasks_spr)
							&& sameside(drawmasks_p2eq, drawmasks_middle, drawmasks_spr)) {
						drawsprite(i);
						tspriteptr[i] = null;
					}
				}
			}

			// finally safe to draw the masked wall
			drawmaskwall(maskwallcnt);
		}

		while (spritesortcnt != 0) {
			spritesortcnt--;
			if (tspriteptr[spritesortcnt] != null) {
				drawsprite(spritesortcnt);
			}
		}

		gl.glDisable(GL10.GL_POLYGON_OFFSET_FILL);
		gl.glPolygonOffset(0, 0);

		if (drunk) {
			BuildGdx.gl.glActiveTexture(GL_TEXTURE0);
			boolean hasShader = texshader != null && texshader.isBinded();
			if (hasShader)
				texshader.end();

			if (frameTexture == null || framew != xdim || frameh != ydim) {
				int size;
				for (size = 1; size < Math.max(xdim, ydim); size <<= 1)
					;

				if (frameTexture != null)
					frameTexture.dispose();
				else
					frameTexture = textureCache.newTile(PixelFormat.Rgb, size, size);

				frameTexture.bind();

				gl.glTexImage2D(GL_TEXTURE_2D, 0, GL10.GL_RGB, frameTexture.getWidth(), frameTexture.getHeight(), 0,
						GL10.GL_RGB, GL_UNSIGNED_BYTE, null);
				frameTexture.unsafeSetFilter(TextureFilter.Linear, TextureFilter.Linear);
				framew = xdim;
				frameh = ydim;
			}

			textureCache.bind(frameTexture);
			gl.glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, frameTexture.getWidth(), frameTexture.getHeight());

			gl.glDisable(GL_DEPTH_TEST);
			gl.glDisable(GL_ALPHA_TEST);
			gl.glEnable(GL_TEXTURE_2D);

			gl.glMatrixMode(GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();

			float tiltang = (drunkIntensive * 360) / 2048f;
			float tilt = min(max(tiltang, -MAXDRUNKANGLE), MAXDRUNKANGLE);

			gl.glScalef(1.05f, 1.05f, 1);
			gl.glRotatef(tilt, 0, 0, 1.0f);

			gl.glMatrixMode(GL_MODELVIEW);
			gl.glPushMatrix();
			gl.glLoadIdentity();

			float u = (float) xdim / frameTexture.getWidth();
			float v = (float) ydim / frameTexture.getHeight();

			gl.glColor4f(1, 1, 1, abs(tilt) / (2 * MAXDRUNKANGLE));
			gl.glBegin(GL10.GL_TRIANGLE_FAN);
			gl.glTexCoord2f(0, 0);
			gl.glVertex2f(-1f, -1f);

			gl.glTexCoord2f(0, v);
			gl.glVertex2f(-1f, 1f);

			gl.glTexCoord2f(u, v);
			gl.glVertex2f(1f, 1f);

			gl.glTexCoord2f(u, 0);
			gl.glVertex2f(1f, -1f);
			gl.glEnd();

			gl.glMatrixMode(GL_MODELVIEW);
			gl.glPopMatrix();
			gl.glMatrixMode(GL_PROJECTION);
			gl.glPopMatrix();

			gl.glEnable(GL_DEPTH_TEST);
			gl.glEnable(GL_ALPHA_TEST);
			gl.glDisable(GL_TEXTURE_2D);

			if (hasShader)
				texshader.begin();
		}
	}

	@Override
	public void gltexinvalidateall(GLInvalidateFlag... flags) {
		for (int i = 0; i < flags.length; i++) {
			switch (flags[i]) {
			case Uninit:
				textureCache.uninit();
				break;
			case SkinsOnly:
				clearskins(true);
				break;
			case TexturesOnly:
			case IndexedTexturesOnly:
				gltexinvalidate8();
				break;
			case Palookup:
				for (int j = 0; j < MAXPALOOKUPS; j++) {
					if (texshader != null)
						textureCache.invalidatepalookup(j);
				}
				break;
			case All:
				gltexinvalidateall();
				break;
			}
		}
	}

	@Override
	public void clearview(int dacol) {
		gl.glClearColor(curpalette.getRed(dacol) / 255.0f, curpalette.getGreen(dacol) / 255.0f,
				curpalette.getBlue(dacol) / 255.0f, 0);
		gl.glClear(GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void nextpage() {
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
		ogshang = -1;
	}

	private ByteBuffer rgbbuffer;
	private ByteBuffer indexbuffer;

	@Override
	public ByteBuffer getFrame(PixelFormat format, int xsiz, int ysiz) {
		if (rgbbuffer != null)
			rgbbuffer.clear();

		boolean reverse = false;
		if (ysiz < 0) {
			ysiz *= -1;
			reverse = true;
		}

		int byteperpixel = 3;
		int fmt = GL10.GL_RGB;
		if (BuildGdx.app.getPlatform() == Platform.Android) {
			byteperpixel = 4;
			fmt = GL10.GL_RGBA;
		}

		if (rgbbuffer == null || rgbbuffer.capacity() < xsiz * ysiz * byteperpixel)
			rgbbuffer = BufferUtils.newByteBuffer(xsiz * ysiz * byteperpixel);
		gl.glPixelStorei(GL10.GL_PACK_ALIGNMENT, 1);
		gl.glReadPixels(0, ydim - ysiz, xsiz, ysiz, fmt, GL10.GL_UNSIGNED_BYTE, rgbbuffer);

		if (format == PixelFormat.Rgb) {
			if (reverse) {
				int b1, b2 = 0;
				for (int p, x, y = 0; y < ysiz / 2; y++) {
					b1 = byteperpixel * (ysiz - y - 1) * xsiz;
					for (x = 0; x < xsiz; x++) {
						for (p = 0; p < byteperpixel; p++) {
							byte tmp = rgbbuffer.get(b1 + p);
							rgbbuffer.put(b1 + p, rgbbuffer.get(b2 + p));
							rgbbuffer.put(b2 + p, tmp);
						}
						b1 += byteperpixel;
						b2 += byteperpixel;
					}
				}
			}
			rgbbuffer.rewind();
			return rgbbuffer;
		} else if (format == PixelFormat.Pal8) {
			if (indexbuffer != null)
				indexbuffer.clear();
			if (indexbuffer == null || indexbuffer.capacity() < xsiz * ysiz)
				indexbuffer = BufferUtils.newByteBuffer(xsiz * ysiz);

			int base = 0, r, g, b;
			if (reverse) {
				for (int x, y = 0; y < ysiz; y++) {
					base = byteperpixel * (ysiz - y - 1) * xsiz;
					for (x = 0; x < xsiz; x++) {
						r = (rgbbuffer.get(base++) & 0xFF) >> 2;
						g = (rgbbuffer.get(base++) & 0xFF) >> 2;
						b = (rgbbuffer.get(base++) & 0xFF) >> 2;
						indexbuffer.put(engine.getclosestcol(palette, r, g, b));
					}
				}
			} else {
				for (int i = 0; i < indexbuffer.capacity(); i++) {
					r = (rgbbuffer.get(base++) & 0xFF) >> 2;
					g = (rgbbuffer.get(base++) & 0xFF) >> 2;
					b = (rgbbuffer.get(base++) & 0xFF) >> 2;
					if (byteperpixel == 4)
						base++; // Android
					indexbuffer.put(engine.getclosestcol(palette, r, g, b));
				}
			}

			indexbuffer.rewind();
			return indexbuffer;
		}

		return null;
	}

	@Override
	public byte[] screencapture(int dwidth, int dheigth) {
		byte[] capture = new byte[dwidth * dheigth];

		long xf = divscale(xdim, dwidth, 16);
		long yf = divscale(ydim, dheigth, 16);

		ByteBuffer frame = getFrame(PixelFormat.Rgb, xdim, -ydim);

		int byteperpixel = 3;
		if (BuildGdx.app.getType() == ApplicationType.Android)
			byteperpixel = 4;

		int base;
		for (int fx, fy = 0; fy < dheigth; fy++) {
			base = mulscale(fy, yf, 16) * xdim;
			for (fx = 0; fx < dwidth; fx++) {
				int pos = base + mulscale(fx, xf, 16);
				frame.position(byteperpixel * pos);
				int r = (frame.get() & 0xFF) >> 2;
				int g = (frame.get() & 0xFF) >> 2;
				int b = (frame.get() & 0xFF) >> 2;

				capture[dheigth * fx + fy] = engine.getclosestcol(palette, r, g, b);
			}
		}

		return capture;
	}

	public int nearwall(int i, int range) {
		SPRITE spr = sprite[i];
		short sectnum = spr.sectnum;
		int xs = spr.x;
		int ys = spr.y;

		int vx = mulscale(sintable[(spr.ang + 2560) & 2047], range, 14);
		int xe = xs + vx;
		int vy = mulscale(sintable[(spr.ang + 2048) & 2047], range, 14);
		int ye = ys + vy;

		if ((sectnum < 0) || (sectnum >= numsectors))
			return (-1);

		short startwall = sector[sectnum].wallptr;
		int endwall = (startwall + sector[sectnum].wallnum - 1);
		for (int z = startwall; z <= endwall; z++) {
			WALL wal = wall[z];
			WALL wal2 = wall[wal.point2];
			int x1 = wal.x;
			int y1 = wal.y;
			int x2 = wal2.x;
			int y2 = wal2.y;

			if ((x1 - xs) * (y2 - ys) < (x2 - xs) * (y1 - ys))
				continue;

			if (wal.nextsector != -1) {
				int daz = engine.getflorzofslope(sectnum, xs, ys);
				int daz2 = engine.getflorzofslope(wal.nextsector, xs, ys);

				Tile pic = engine.getTile(spr.picnum);
				boolean clipyou = false;
				int z1 = spr.z, z2 = spr.z;
				int yoff = pic.getOffsetY();

				if ((spr.cstat & 128) != 0) {
					z1 -= (yoff + pic.getHeight() / 2) * (spr.yrepeat << 2);
					z2 += (pic.getHeight() - (pic.getHeight() / 2 + yoff)) * (spr.yrepeat << 2);
				} else
					z1 -= (yoff + pic.getHeight()) * (spr.yrepeat << 2);

				if (daz2 < daz - (1 << 8))
					if (z2 >= daz2)
						clipyou = true;
				if (!clipyou) {
					daz = engine.getceilzofslope(sectnum, xs, ys);
					daz2 = engine.getceilzofslope(wal.nextsector, xs, ys);
					if (daz2 > daz + (1 << 8))
						if (z1 <= daz2)
							clipyou = true;
				}

				if (!clipyou)
					continue;
			}

			Point out;
			if ((out = engine.lintersect(xs, ys, 0, xe, ye, 0, x1, y1, x2, y2)) != null) {
				int dist = dmulscale(out.getX() - xs, sintable[(spr.ang + 2560) & 2047], out.getY() - ys,
						sintable[(spr.ang + 2048) & 2047], 14);
				if (klabs(dist) <= 8) {
					int wallang = engine.getangle(wall[wal.point2].x - wal.x, wall[wal.point2].y - wal.y) - 512;
					int nx = out.getX() - mulscale(sintable[(wallang + 2560) & 2047], 4, 14);
					int ny = out.getY() - mulscale(sintable[(wallang + 2048) & 2047], 4, 14);
					dcoord[i].x = nx - spr.x;
					dcoord[i].y = ny - spr.y;
				}
				return z;
			}
		}

		return -1;
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
	public void addSpriteCorr(int snum) {
		int spr_wall;
		SPRITE spr = sprite[snum];
		if ((spr_wall = nearwall(snum, -64)) == -1)
			if ((spr.cstat & 64) != 0 || (spr_wall = nearwall(snum, 64)) == -1)
				return;

		spritewall[snum] = spr_wall;
		float sang = spr.ang * 360 / 2048;
		int wdx = wall[spr_wall].x - wall[wall[spr_wall].point2].x;
		int wdy = wall[spr_wall].y - wall[wall[spr_wall].point2].y;
		float wang = new Vector2(wdx, wdy).angle() - 90;
		if (wang < 0)
			wang += 360;
		wang = BClipRange(wang, 0, 360);
		if (Math.abs(wang - sang) > 10)
			return;

		dsin[snum].x = (sintable[(spr.ang) & 2047] / 65536.0f) - (float) (Math.sin(Math.toRadians(wang)) / 4);
		dsin[snum].y = (sintable[(spr.ang + 1536) & 2047] / 65536.0f)
				- (float) (Math.sin(Math.toRadians(wang + 270)) / 4);
	}

	public IndexedShader getShader() {
		return texshader;
	}

	public PixelFormat getTextureFormat() {
		return texshader != null ? PixelFormat.Pal8 : PixelFormat.Rgba;
	}

	@Override
	public void removeSpriteCorr(int snum) {
		dsin[snum].set(0, 0);
		dcoord[snum].set(0, 0);
		spritewall[snum] = -1;
	}

	@Override
	public void settiltang(int tilt) {
		if (tilt == 0)
			gtang = 0.0f;
		else
			gtang = (float) (PI * tilt / 1024.0);
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

	public double polymost_getflorzofslope(int sectnum, double dax, double day) {
		if (sector[sectnum] == null)
			return 0;
		if ((sector[sectnum].floorstat & 2) == 0)
			return (sector[sectnum].floorz);

		WALL wal = wall[sector[sectnum].wallptr];
		int dx = wall[wal.point2].x - wal.x;
		int dy = wall[wal.point2].y - wal.y;
		long i = (engine.ksqrt(dx * dx + dy * dy) << 5);
		if (i == 0)
			return (sector[sectnum].floorz);

		double j = (dx * (day - wal.y) - dy * (dax - wal.x)) / 8;
		return sector[sectnum].floorz + sector[sectnum].floorheinum * j / i;
	}

	public double polymost_getceilzofslope(int sectnum, double dax, double day) {
		if ((sector[sectnum].ceilingstat & 2) == 0)
			return (sector[sectnum].ceilingz);

		WALL wal = wall[sector[sectnum].wallptr];
		int dx = wall[wal.point2].x - wal.x;
		int dy = wall[wal.point2].y - wal.y;
		long i = (engine.ksqrt(dx * dx + dy * dy) << 5);
		if (i == 0)
			return (sector[sectnum].ceilingz);

		double j = (dx * (day - wal.y) - dy * (dax - wal.x)) / 8;
		return sector[sectnum].ceilingz + sector[sectnum].ceilingheinum * j / i;
	}

	private static float dceilzsofslope, dfloorzsofslope;

	public void polymost_getzsofslope(int sectnum, double dax, double day) {
		SECTOR sec = sector[sectnum];
		if (sec == null)
			return;
		dceilzsofslope = sec.ceilingz;
		dfloorzsofslope = sec.floorz;
		if (((sec.ceilingstat | sec.floorstat) & 2) != 0) {
			WALL wal = wall[sec.wallptr];
			WALL wal2 = wall[wal.point2];
			int dx = wal2.x - wal.x;
			int dy = wal2.y - wal.y;
			long i = (engine.ksqrt(dx * dx + dy * dy) << 5);
			if (i == 0)
				return;
			double j = (dx * (day - wal.y) - dy * (dax - wal.x)) / 8;

			if ((sec.ceilingstat & 2) != 0)
				dceilzsofslope += sector[sectnum].ceilingheinum * j / i;
			if ((sec.floorstat & 2) != 0)
				dfloorzsofslope += sector[sectnum].floorheinum * j / i;
		}
	}

	@Override
	public void rotatesprite(int sx, int sy, int z, int a, int picnum, int dashade, int dapalnum, int dastat, int cx1,
			int cy1, int cx2, int cy2) {
		globalfog.disable();
		ortho.rotatesprite(sx, sy, z, a, picnum, dashade, dapalnum, dastat, cx1, cy1, cx2, cy2);
		globalfog.enable();
	}

	@Override
	public void drawmapview(int dax, int day, int zoome, int ang) {
		globalfog.disable();
		ortho.drawmapview(dax, day, zoome, ang);
		globalfog.enable();
	}

	@Override
	public void drawoverheadmap(int cposx, int cposy, int czoom, short cang) {
		globalfog.disable();
		ortho.drawoverheadmap(cposx, cposy, czoom, cang);
		globalfog.enable();
	}

	@Override
	public void printext(TileFont font, int xpos, int ypos, char[] text, int col, int shade, Transparent bit,
			float scale) {
		globalfog.disable();
		ortho.printext(font, xpos, ypos, text, col, shade, bit, scale);
		globalfog.enable();
	}

	@Override
	public void printext(int xpos, int ypos, int col, int backcol, char[] text, int fontsize, float scale) {
		globalfog.disable();
		ortho.printext(xpos, ypos, col, backcol, text, fontsize, scale);
		globalfog.enable();
	}

	@Override
	public void drawline256(int x1, int y1, int x2, int y2, int col) {
		globalfog.disable();
		ortho.drawline256(x1, y1, x2, y2, col);
		globalfog.enable();
	}

	private IndexedShader allocIndexedShader() {
		try {
			return new IndexedShader() {
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
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	protected Polymost2D allocOrphoRenderer(IOverheadMapSettings settings) {
		return new Polymost2D(this, settings);
	}

	@Override
	public PixelFormat getTexFormat() {
		return PixelFormat.Rgb; // textureCache.getShader() != null ? PixelFormat.Pal8 : PixelFormat.Rgb;
	}

	@Override
	public void completemirror() {
		/* nothing */ }

	@Override
	public void setview(int x1, int y1, int x2, int y2) {
		/* nothing */
	}
}
