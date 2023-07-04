package ru.m210projects.Build.Render.ModelHandle.Voxel;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Render.TextureHandle.GLTile;

public abstract class VoxelGL20 extends GLVoxel {

	private final Mesh mesh;

	public VoxelGL20(VoxelData vox, int voxmip, int flags) {
		super(flags);

		VoxelBuilder builder = new VoxelBuilder(vox, voxmip);
		float[] vertices = builder.getVertices();
		short[] indices = builder.getIndices();

		this.xsiz = builder.xsiz;
		this.ysiz = builder.ysiz;
		this.zsiz = builder.zsiz;

		this.xpiv = vox.xpiv[voxmip] / 256.0f;
		this.ypiv = vox.ypiv[voxmip] / 256.0f;
		this.zpiv = vox.zpiv[voxmip] / 256.0f;

		int size = builder.getVertexSize();
		mesh = new Mesh(true, vertices.length / size, indices.length, builder.getAttributes());
		mesh.setVertices(vertices);
		mesh.setIndices(indices);

		skinData = builder.getTexture();
	}

	@Override
	public boolean render(int pal, int shade, int pad, int visibility, float alpha) {
		GLTile skin = getSkin(pal);
		if (skin == null)
			return false;

		if (alpha != 1.0f)
			BuildGdx.gl.glEnable(GL_BLEND);
		else
			BuildGdx.gl.glDisable(GL_BLEND);

		skin.bind();
		setTextureParameters(skin, pal, shade, visibility, alpha);

		mesh.render(getShader(), GL20.GL_TRIANGLES);
		return true;
	}

	@Override
	public void dispose() {
		mesh.dispose();
		clearSkins();
	}
}
