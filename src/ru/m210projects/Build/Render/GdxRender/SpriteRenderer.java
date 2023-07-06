package ru.m210projects.Build.Render.GdxRender;

import static com.badlogic.gdx.graphics.GL20.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Engine.globalang;
import static ru.m210projects.Build.Engine.globalposx;
import static ru.m210projects.Build.Engine.globalposy;
import static ru.m210projects.Build.Engine.globalposz;
import static ru.m210projects.Build.Engine.globalvisibility;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix4;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Gameutils;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Render.GdxRender.Shaders.ShaderManager;
import ru.m210projects.Build.Render.TextureHandle.GLTile;
import ru.m210projects.Build.Render.TextureHandle.IndexedShader;
import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;
import ru.m210projects.Build.Types.QuickSort;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.Tile.AnimType;

public class SpriteRenderer {

	private final Matrix4 transform;
	private BuildCamera cam;
	private final SpriteComparator comp;
	private final GDXRenderer parent;
	private final Engine engine;

	private final int[] spritesz = new int[MAXSPRITES];

	public class SpriteComparator implements Comparator<SPRITE> {
		@Override
		public int compare(SPRITE o1, SPRITE o2) {
			if (o1 == null || o2 == null)
				return 0;

			if(o1.owner == o2.owner || o1.owner == -1 || o2.owner == -1)
				return 0;

			int len1 = getDist(o1);
			int len2 = getDist(o2);
			if(len1 != len2)
				return len1 < len2 ? -1 : 1;

			if ((o1.cstat & 2) != 0)
				return -1;

			if ((o2.cstat & 2) != 0)
				return 1;

			return (o1.statnum <= o2.statnum) ? -1 : 0;
		}

		public int getDist(SPRITE spr) {
			int dx1 = spr.x - globalposx;
			int dy1 = spr.y - globalposy;
			int dz1 = (spritesz[spr.owner] - globalposz) >> 4;

			return dx1 * dx1 + dy1 * dy1 + dz1 * dz1;
		}
	}

	public SpriteRenderer(Engine engine, GDXRenderer parent) {
		transform = new Matrix4();
		comp = new SpriteComparator();
		this.parent = parent;
		this.engine = engine;
	}

	public void sort(SPRITE[] array, int len) {
		for(int i = 0; i < len; i++) {
			SPRITE spr = array[i];
			int s = spr.owner;
			if(s == -1)
				continue;

			spritesz[s] = spr.z;
			if(!Gameutils.isValidTile(spr.picnum))
				continue;

			if ((spr.cstat & 48) != 32) {
				Tile pic = engine.getTile(spr.picnum);
				byte yoff = (byte) (pic.getOffsetY() + spr.yoffset);
				spritesz[s] -= ((yoff * spr.yrepeat) << 2);
				int yspan = (pic.getHeight() * spr.yrepeat << 2);
				if ((spr.cstat & 128) == 0)
					spritesz[s] -= (yspan >> 1);
				if (klabs(spritesz[s] - globalposz) < (yspan >> 1))
					spritesz[s] = globalposz;
			}
		}

		QuickSort.sort(array, len, comp);
	}

	public void begin(BuildCamera cam) {
		this.cam = cam;
	}

	public Matrix4 getMatrix(SPRITE tspr, int texx, int texy) {
		int picnum = tspr.picnum;
		int orientation = tspr.cstat;
		int spritenum = tspr.owner;
		Tile pic = engine.getTile(picnum);

		int xoff = 0, yoff = 0;
		if ((orientation & 48) != 48) {
			if (pic.getType() != AnimType.None) {
				picnum += engine.animateoffs(picnum, spritenum + 32768);
				pic = engine.getTile(picnum);
			}

			xoff = tspr.xoffset;
			yoff = tspr.yoffset;
			xoff += pic.getOffsetX();
			yoff += pic.getOffsetY();
		}

		int tsizx = pic.getWidth();
		int tsizy = pic.getHeight();

		if (tsizx <= 0 || tsizy <= 0)
			return null;

		boolean xflip = (orientation & 4) != 0;
		boolean yflip = (orientation & 8) != 0;

        float xspans;
		float posx = tspr.x;
		float posy = tspr.y;
		float posz = tspr.z;
		transform.setToTranslation(posx, posy, posz);

		switch ((orientation >> 4) & 3) {
		case 0: // Face sprite
			xspans = 5.0f;
			int ang = ((int) globalang - 512) & 0x7FF;
			if (xflip ^ yflip) {
				ang += 1024;
				if (!xflip)
					xoff = -xoff;
				if (yflip)
					xspans = -xspans;
			} else if (xflip) {
				xoff = -xoff;
				xspans = -xspans;
			}

			transform.translate(0, 0, (tspr.yrepeat * texy * (yflip ? 2.0f : -2.0f)));
			transform.rotate(0, 0, 1, (int) Gameutils.AngleToDegrees(ang));
			transform.translate((tspr.xrepeat * xoff) / (5.0f), 0, -((yoff * tspr.yrepeat) << 2));

			if ((tsizx & 1) == 0)
				transform.translate((tspr.xrepeat >> 1) / xspans, 0, 0);

			if ((orientation & 128) != 0) {
				float zoffs = ((tsizy * tspr.yrepeat) << 1);
				if ((tsizy & 1) != 0)
					zoffs += (tspr.yrepeat << 1); // Odd yspans
				transform.translate(0, 0, zoffs);
			}

			if (yflip) {
				transform.rotate(0, 1, 0, 180);
				transform.translate(0, 0, (tspr.yrepeat * texy) * 4.0f);
			} else
				transform.translate(0, 0, (tspr.yrepeat * (texy - tsizy)) * 4.0f);

			transform.scale((tspr.xrepeat * texx) / 5.0f, 0, 4 * tspr.yrepeat * texy);
			break;
		case 1: // Wall sprite
			if (yflip)
				yoff = -yoff;
			int wang = (int) Gameutils.AngleToDegrees((tspr.ang + ((xflip ^ yflip) ? 1536 : 512)) & 0x7FF);
			if ((orientation & 64) == 0) {
				int dang = (((tspr.ang - engine.getangle(tspr.x - globalposx, tspr.y - globalposy)) & 0x7FF) - 1024);
				if (dang > 512 || dang < -512) {
					xflip = !xflip;
				}
			}

			xspans = 4.0f;
			if (xflip ^ yflip) {
				if (!xflip)
					xoff = -xoff;
				if (yflip)
					xspans = -xspans;
			} else if (xflip) {
				xoff = -xoff;
				xspans = -xspans;
			}

			transform.translate(0, 0, (tspr.yrepeat * texy * (yflip ? 2.0f : -2.0f)));
			transform.rotate(0, 0, 1, wang);
			transform.translate((tspr.xrepeat * xoff) / 4.0f, 0, -(tspr.yrepeat * yoff) * 4.0f);
			if ((orientation & 128) != 0)
				transform.translate(0, 0, (tspr.yrepeat * tsizy) * 2.0f);

			if ((tsizx & 1) == 0)
				transform.translate((tspr.xrepeat >> 1) / xspans, 0, 0);

			if (yflip) {
				transform.rotate(0, 1, 0, 180);
				transform.translate(0, 0, (tspr.yrepeat * texy) * 4.0f);
			} else
				transform.translate(0, 0, (tspr.yrepeat * (texy - tsizy)) * 4.0f);

			transform.scale((tspr.xrepeat * texx) / 4.0f, 0, (tspr.yrepeat * texy) * 4.0f);
			break;
		case 2: // Floor sprite
			if (yflip)
				yoff = -yoff;

			if ((orientation & 64) == 0) {
				if (tspr.z < globalposz) {
					yflip = true;
				} else if (yflip)
					yflip = !yflip;
			}

			xspans = 4.0f;
			if (xflip ^ yflip) {
				if (yflip)
					xspans = -xspans;
			} else if (xflip)
				xspans = -xspans;

			transform.rotate(0, 0, 1, (int) Gameutils.AngleToDegrees((tspr.ang + (xflip ? 512 : 1536)) & 0x7FF));
			transform.rotate(1, 0, 0, xflip ? -90 : 90);

			if ((tsizx & 1) == 0)
				transform.translate((tspr.xrepeat >> 1) / xspans, 0, 0);

			transform.translate((tspr.xrepeat * xoff) / 4.0f, 0, -(tspr.yrepeat * yoff) / 4.0f);
			transform.scale((tspr.xrepeat * texx) / 4.0f, 0, (tspr.yrepeat * texy) / 4.0f);
			break;
		}

		return transform.rotate(1, 0, 0, 90);
	}

	public boolean draw(SPRITE tspr) {
		if (tspr.owner < 0 || !Gameutils.isValidTile(tspr.picnum) || !Gameutils.isValidSector(tspr.sectnum))
			return false;

        ShaderManager manager = parent.manager;

		int picnum = tspr.picnum;
		int shade = tspr.shade;
		int pal = tspr.pal & 0xFF;
		int orientation = tspr.cstat;
		int spritenum = tspr.owner;
		Tile pic = engine.getTile(picnum);
		if ((orientation & 48) != 48) {
			if (pic.getType() != AnimType.None) {
				picnum += engine.animateoffs(picnum, spritenum + 32768);
				pic = engine.getTile(picnum);
			}
		}

		if (!pic.isLoaded())
			engine.loadtile(picnum);

		int tsizx = pic.getWidth();
		int tsizy = pic.getHeight();

		if (tsizx <= 0 || tsizy <= 0)
			return false;

		int method = 1 + 4;
		if ((orientation & 2) != 0) {
			if ((orientation & 512) == 0)
				method = 2 + 4;
			else
				method = 3 + 4;
		}

		GLTile tex = parent.bind(picnum, pal, shade, 0, method);
		if (tex == null)
			return false;

		if (tex.isHighTile()) {
			for (tsizy = 1; tsizy < tex.getHeight(); tsizy += tsizy);
			tsizy /= tex.getYScale();
		} else {
			tsizx = tex.getWidth();
			tsizy = tex.getHeight();
		}

		int vis = globalvisibility;
		if (sector[tspr.sectnum].visibility != 0)
			vis = mulscale(globalvisibility, (sector[tspr.sectnum].visibility + 16) & 0xFF, 4);

		if (tex.getPixelFormat() == PixelFormat.Pal8)
			((IndexedShader) manager.getProgram()).setVisibility((int) (-vis / 64.0f));
		else parent.calcFog(pal, shade, vis);

		boolean xflip = (orientation & 4) != 0;
		boolean yflip = (orientation & 8) != 0;

		switch ((orientation >> 4) & 3) {
		case 1: // Wall sprite
			if ((orientation & 64) == 0) {
				int dang = (((tspr.ang - engine.getangle(tspr.x - globalposx, tspr.y - globalposy)) & 0x7FF) - 1024);
				if (dang > 512 || dang < -512) {
					xflip = !xflip;
				}
			}
			break;
		case 2: // Floor sprite
			if ((orientation & 64) == 0) {
				if (tspr.z < globalposz) {
					yflip = true;
				} else if (yflip)
					yflip = !yflip;
			}
			break;
		}

		if ((method & 3) == 0)
			BuildGdx.gl.glDisable(GL_BLEND);
		else
			BuildGdx.gl.glEnable(GL_BLEND);

		if (xflip ^ yflip)
			BuildGdx.gl.glFrontFace(GL_CCW);
		else
			BuildGdx.gl.glFrontFace(GL_CW);

		Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
		Gdx.gl.glDepthRangef(0.0f, 0.99999f);

		Matrix4 mat = getMatrix(tspr, tsizx, tsizy);
		float invscalex = 1.0f / cam.xscale;
		float invscaley = 1.0f / cam.yscale;

		mat.val[Matrix4.M00] *= invscalex;
		mat.val[Matrix4.M01] *= invscalex;
		mat.val[Matrix4.M02] *= invscalex;
		mat.val[Matrix4.M03] *= invscalex;

		mat.val[Matrix4.M10] *= invscalex;
		mat.val[Matrix4.M11] *= invscalex;
		mat.val[Matrix4.M12] *= invscalex;
		mat.val[Matrix4.M13] *= invscalex;

		mat.val[Matrix4.M20] *= invscaley;
		mat.val[Matrix4.M21] *= invscaley;
		mat.val[Matrix4.M22] *= invscaley;
		mat.val[Matrix4.M23] *= invscaley;

		manager.transform(mat);
		manager.frustum(null);
		parent.world.getQuad().render(manager.getProgram());

		BuildGdx.gl.glFrontFace(GL_CW);
		return true;
	}

	public void end() {
		Gdx.gl.glDepthFunc(GL20.GL_LESS);
		Gdx.gl.glDepthRangef(parent.defznear, parent.defzfar);
	}

}
