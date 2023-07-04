package ru.m210projects.Build.Render.ModelHandle.Voxel;

import static ru.m210projects.Build.Engine.MAXPALOOKUPS;
import static ru.m210projects.Build.Render.ModelHandle.ModelInfo.MD_ROTATE;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;

import ru.m210projects.Build.Render.ModelHandle.GLModel;
import ru.m210projects.Build.Render.ModelHandle.ModelInfo.Type;
import ru.m210projects.Build.Render.TextureHandle.GLTile;
import ru.m210projects.Build.Types.Tile;

public abstract class GLVoxel implements GLModel {

	protected Tile skinData; // indexed texture data
	protected GLTile[] texid;
	public int xsiz, ysiz, zsiz;
	public float xpiv, ypiv, zpiv;
	protected final Color color = new Color();
	protected int flags;

	public GLVoxel(int flags) {
		this.texid = new GLTile[MAXPALOOKUPS];
		this.flags = flags;
	}

	public abstract GLTile getSkin(int pal);

	public abstract void setTextureParameters(GLTile tile, int pal, int shade, int visibility, float alpha);

	@Override
	public Type getType() {
		return Type.Voxel;
	}

	@Override
	public boolean isRotating() {
		return (flags & MD_ROTATE) != 0;
	}

	@Override
	public boolean isTintAffected() {
		return false;
	}

	@Override
	public float getScale() {
		return 1.0f;
	}

	public GLVoxel setColor(float r, float g, float b, float a) {
		color.set(r, g, b, a);
		return this;
	}

	public int getSkinWidth() {
		return skinData.getWidth();
	}

	public int getSkinHeight() {
		return skinData.getHeight();
	}

	@Override
	public Iterator<GLTile> getSkins() {
		ArrayList<GLTile> list = new ArrayList<GLTile>();
		for (int i = 0; i < texid.length; i++) {
			GLTile tex = texid[i];
			if (tex != null)
				list.add(tex);
		}
		return list.iterator();
	}

	@Override
	public void clearSkins() {
		for (int i = 0; i < texid.length; i++) {
			GLTile tex = texid[i];
			if (tex == null)
				continue;

			tex.delete();
			texid[i] = null;
		}
	}
}
