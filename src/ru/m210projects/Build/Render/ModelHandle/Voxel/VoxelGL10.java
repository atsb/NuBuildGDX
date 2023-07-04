package ru.m210projects.Build.Render.ModelHandle.Voxel;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;
import static com.badlogic.gdx.graphics.GL20.GL_FLOAT;
import static com.badlogic.gdx.graphics.GL20.GL_TRIANGLES;
import static com.badlogic.gdx.graphics.GL20.GL_UNSIGNED_SHORT;
import static ru.m210projects.Build.Render.Types.GL10.GL_QUADS;
import static ru.m210projects.Build.Render.Types.GL10.GL_TEXTURE_COORD_ARRAY;
import static ru.m210projects.Build.Render.Types.GL10.GL_VERTEX_ARRAY;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.BufferUtils;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Render.ModelHandle.Voxel.VoxelBuilder.Rectangle;
import ru.m210projects.Build.Render.TextureHandle.GLTile;

public abstract class VoxelGL10 extends GLVoxel {

	protected Rectangle[] quad;
	protected int qcnt;
    protected int[] qfacind;
	private final float[] dvoxphack = { 0.0f, 1.0f / 256.0f }, dvoxclut = { 1, 1, 1, 1, 1, 1 };
	private final boolean isVertexArray;
	private ShortBuffer indices;
	private FloatBuffer vertices;
	private FloatBuffer uv;

	public VoxelGL10(VoxelData vox, int voxmip, int flags, boolean isVertexArray) {
		super(flags);

		this.isVertexArray = isVertexArray;

		VoxelBuilder builder = new VoxelBuilder(vox, voxmip);
		skinData = builder.getTexture();

		this.xsiz = builder.xsiz;
		this.ysiz = builder.ysiz;
		this.zsiz = builder.zsiz;

		this.xpiv = vox.xpiv[voxmip] / 256.0f;
		this.ypiv = vox.ypiv[voxmip] / 256.0f;
		this.zpiv = vox.zpiv[voxmip] / 256.0f;

		if (isVertexArray) {
			float[] va = builder.getVertices();
			short[] ia = builder.getIndices();

			int verSize = builder.getVertexSize();
			int size = va.length / verSize;

			vertices = BufferUtils.newFloatBuffer(size * 3);
			uv = BufferUtils.newFloatBuffer(size * 2);
			indices = BufferUtils.newShortBuffer(ia.length);

			for (int i = 0; i < va.length; i += verSize) {
				vertices.put(va[i] * 64.0f);
				vertices.put(va[i + 1] * 64.0f);
				vertices.put(va[i + 2] * 64.0f);
				uv.put(va[i + 4]);
				uv.put(va[i + 5]);
			}

			vertices.flip();
			uv.flip();
			indices.put(ia).flip();
		} else {
			quad = builder.quad;
			qcnt = builder.qcnt;
			qfacind = builder.qfacind;
		}
	}

	@Override
	public boolean render(int pal, int shade, int surfnum, int visibility, float alpha) {
		GLTile skin = getSkin(pal);
		if (skin == null)
			return false;

		if (alpha != 1.0f)
			BuildGdx.gl.glEnable(GL_BLEND);
		else
			BuildGdx.gl.glDisable(GL_BLEND);

		skin.bind(); // TODO skinBind instead of setTextureParams
		setTextureParameters(skin, pal, shade, visibility, alpha);

		if (isVertexArray) {
			BuildGdx.gl.glColor4f(color.r, color.g, color.b, color.a);
			BuildGdx.gl.glEnableClientState(GL_VERTEX_ARRAY);
			BuildGdx.gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);

			BuildGdx.gl.glVertexPointer(3, GL_FLOAT, 0, vertices);
			BuildGdx.gl.glTexCoordPointer(2, GL_FLOAT, 0, uv);
			BuildGdx.gl.glDrawElements(GL_TRIANGLES, 0, GL_UNSIGNED_SHORT, indices);

			BuildGdx.gl.glDisableClientState(GL_VERTEX_ARRAY);
			BuildGdx.gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
			return true;
		}

		float ru = 1.0f / skinData.getWidth();
		float rv = 1.0f / skinData.getHeight();

		BuildGdx.gl.glBegin(GL_QUADS);
		for (int i = 0, fi = 0; i < qcnt; i++) {
			if (i == qfacind[fi]) {
				float f = dvoxclut[fi++];
				BuildGdx.gl.glColor4f(color.r * f, color.g * f, color.b * f, color.a * f);
			}

			Rectangle rec = quad[i];

			float xx = rec.getX(0) + rec.getX(2);
			float yy = rec.getY(0) + rec.getY(2);
			float zz = rec.getZ(0) + rec.getZ(2);

			for (int j = 0; j < 4; j++) {
				BuildGdx.gl.glTexCoord2d((rec.getU(j)) * ru, (rec.getV(j)) * rv);
				float vertx = (rec.getX(j)) - dvoxphack[(xx > (rec.getX(j) * 2)) ? 1 : 0]
						+ dvoxphack[(xx < (rec.getX(j) * 2)) ? 1 : 0];
				float verty = (rec.getY(j)) - dvoxphack[(yy > (rec.getY(j) * 2)) ? 1 : 0]
						+ dvoxphack[(yy < (rec.getY(j) * 2)) ? 1 : 0];
				float vertz = (rec.getZ(j)) - dvoxphack[(zz > (rec.getZ(j) * 2)) ? 1 : 0]
						+ dvoxphack[(zz < (rec.getZ(j) * 2)) ? 1 : 0];

				BuildGdx.gl.glVertex3d(vertx, verty, vertz);
			}
		}
		BuildGdx.gl.glEnd();

		return true;
	}

	@Override
	public void dispose() {
		clearSkins();
	}

	@Override
	public ShaderProgram getShader() {
		/* do nothing */
		return null;
	}
}
