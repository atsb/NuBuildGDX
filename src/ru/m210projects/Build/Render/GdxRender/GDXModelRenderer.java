package ru.m210projects.Build.Render.GdxRender;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;
import static com.badlogic.gdx.graphics.GL20.GL_CCW;
import static com.badlogic.gdx.graphics.GL20.GL_CULL_FACE;
import static com.badlogic.gdx.graphics.GL20.GL_CW;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static ru.m210projects.Build.Engine.TRANSLUSCENT1;
import static ru.m210projects.Build.Engine.TRANSLUSCENT2;
import static ru.m210projects.Build.Engine.globalvisibility;
import static ru.m210projects.Build.Engine.numshades;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Pragmas.mulscale;

import com.badlogic.gdx.math.Matrix4;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Gameutils;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Render.GdxRender.Shaders.ShaderManager;
import ru.m210projects.Build.Render.GdxRender.Shaders.ShaderManager.Shader;
import ru.m210projects.Build.Render.ModelHandle.GLModel;
import ru.m210projects.Build.Render.ModelHandle.ModelInfo.Type;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MDModel;
import ru.m210projects.Build.Render.ModelHandle.Voxel.GLVoxel;
import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;
import ru.m210projects.Build.Render.Types.Palette;
import ru.m210projects.Build.Render.Types.Spriteext;
import ru.m210projects.Build.Render.Types.Tile2model;
import ru.m210projects.Build.Script.DefScript;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;

public class GDXModelRenderer {

	private final Matrix4 transform;
	private final GDXRenderer parent;
	private final Engine engine;

	public GDXModelRenderer(GDXRenderer parent) {
		this.transform = new Matrix4();
		this.parent = parent;
		this.engine = parent.engine;
	}

	public boolean mddraw(GLModel m, SPRITE tspr) {
		if (m == null)
			return false;

		boolean isFloorAligned = (tspr.cstat & 48) == 32;
		if (m.getType() == Type.Voxel && isFloorAligned)
			return false;

		Shader shader = Shader.RGBWorldShader;
		if (m.getType() == Type.Voxel && parent.getTexFormat() == PixelFormat.Pal8)
			shader = Shader.IndexedWorldShader;
		parent.switchShader(shader);

		DefScript defs = parent.defs;
		ShaderManager manager = parent.manager;

		Matrix4 transport = prepareTransform(m, tspr);
		if (m.getType() == Type.Voxel) {
			GLVoxel vox = (GLVoxel) m;
			transform.translate(-vox.xpiv / 64.0f, -vox.ypiv / 64.0f, -vox.zpiv / 64.0f);
			if (isFloorAligned) {
				transform.rotate(-1.0f, 0.0f, 0.0f, 90);
				transform.translate(0, -vox.ypiv / 64.0f, -vox.zpiv / 64.0f);
			}
		} else if (m instanceof MDModel) {
			((MDModel) m).updateanimation(defs.mdInfo, tspr);
			if (m.getType() == Type.Md3)
				transform.scale(1 / 64.0f, 1 / 64.0f, -1 / 64.0f);
		}

		manager.transform(transport);
		manager.frustum(null);

		int shade = tspr.shade;
		int pal = tspr.pal & 0xFF;

		float r, g, b, alpha = 1.0f;
		r = g = b = (numshades - min(max((shade), 0), numshades)) / (float) numshades;

		if (parent.defs != null) {
			Palette p = parent.defs.texInfo.getTints(pal);
			r *= p.r / 255.0f;
			g *= p.g / 255.0f;
			b *= p.b / 255.0f;
		}

		if ((tspr.cstat & 2) != 0) {
			if ((tspr.cstat & 512) == 0)
				alpha = TRANSLUSCENT1;
			else
				alpha = TRANSLUSCENT2;
		}

		manager.color(r, g, b, alpha);
		manager.textureTransform(parent.texture_transform.idt(), 0);
		int vis = getVisibility(tspr);
		if (m.getType() != Type.Voxel)
			parent.calcFog(pal, shade, vis);

		BuildGdx.gl.glEnable(GL_BLEND);
		Tile2model t2m = defs.mdInfo.getParams(tspr.picnum);
		m.render(pal, shade, t2m != null ? t2m.skinnum : 0, vis, alpha);

		BuildGdx.gl.glFrontFace(GL_CW);

		return true;
	}

	private int getVisibility(SPRITE tspr) {
		int vis = globalvisibility;
		if (sector[tspr.sectnum].visibility != 0)
			vis = mulscale(globalvisibility, (sector[tspr.sectnum].visibility + 16) & 0xFF, 4);
		return vis;
	}

	private Matrix4 prepareTransform(GLModel m, SPRITE tspr) {
		BuildCamera cam = parent.cam;

		Tile pic = engine.getTile(tspr.picnum);
		int orientation = tspr.cstat;

		boolean xflip = (orientation & 4) != 0;
		boolean yflip = (orientation & 8) != 0;
		boolean isWallAligned = (orientation & 48) == 16;
		boolean isFloorAligned = (orientation & 48) == 32;

		float xoff = tspr.xoffset;
		float yoff = yflip ? -tspr.yoffset : tspr.yoffset;
		float posx = tspr.x;
		float posy = tspr.y;
		float posz = tspr.z;

		if (m.getType() == Type.Voxel) {
			GLVoxel vox = (GLVoxel) m;
			if ((orientation & 128) == 0)
				posz -= ((vox.zsiz * tspr.yrepeat) << 1);
			if (yflip && (orientation & 16) == 0)
				posz += ((pic.getHeight() * 0.5f) - vox.zpiv) * tspr.yrepeat * 8.0f;
		} else {
			if ((orientation & 128) != 0 && !isFloorAligned)
				posz += (pic.getHeight() * tspr.yrepeat) << 1;
			if (yflip)
				posz -= (pic.getHeight() * tspr.yrepeat) << 2;
		}

		float f = (tspr.xrepeat / 32.0f) * m.getScale();
		float g = (tspr.yrepeat / 32.0f) * m.getScale();
		if (!isWallAligned && !isFloorAligned)
			f *= 0.8f;

		transform.setToTranslation(posx / cam.xscale, posy / cam.xscale, posz / cam.yscale);
		float yoffset = 0;
		if (m instanceof MDModel) {
			MDModel md = (MDModel) m;
			transform.translate(0, 0, -md.getYOffset(false) * g);
			yoffset = md.getYOffset(true);
		}
		transform.scale(f, f, yflip ? -g : g);

		Spriteext sprext = parent.defs.mapInfo.getSpriteInfo(tspr.owner);
		float ang = tspr.ang + (sprext != null ? sprext.angoff : 0);
		transform.rotate(0, 0, 1, (float) Gameutils.AngleToDegrees(ang));
		transform.scale(isFloorAligned ? -1.0f : 1.0f, xflip ^ isFloorAligned ? -1.0f : 1.0f, 1.0f);
		if (m.getType() == Type.Voxel)
			transform.rotate(0, 0, -1, 90.0f);
		transform.translate(-xoff / 64.0f, 0, (-yoff / 64.0f) - yoffset);
		if (m.isRotating())
			transform.rotate(0, 0, -1, totalclock % 360);
		if (m instanceof MDModel) {
			transform.scale(0.01f, 0.01f, 0.01f);
			transform.rotate(1, 0, 0, -90.0f);
		}

		BuildGdx.gl.glEnable(GL_CULL_FACE);
		if (yflip ^ xflip)
			BuildGdx.gl.glFrontFace(m.getType() != Type.Md2 ? GL_CCW : GL_CW);
		else
			BuildGdx.gl.glFrontFace(m.getType() != Type.Md2 ? GL_CW : GL_CCW);
		return transform;
	}

	// ---------------------------------------------------------------

}
