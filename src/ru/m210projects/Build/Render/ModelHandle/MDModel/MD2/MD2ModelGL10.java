package ru.m210projects.Build.Render.ModelHandle.MDModel.MD2;

import static com.badlogic.gdx.graphics.GL20.GL_CULL_FACE;
import static com.badlogic.gdx.graphics.GL20.GL_FLOAT;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_2D;
import static com.badlogic.gdx.graphics.GL20.GL_TRIANGLES;
import static com.badlogic.gdx.graphics.GL20.GL_UNSIGNED_SHORT;
import static ru.m210projects.Build.Render.Types.GL10.GL_ALPHA_TEST;
import static ru.m210projects.Build.Render.Types.GL10.GL_MODELVIEW;
import static ru.m210projects.Build.Render.Types.GL10.GL_RGB_SCALE;
import static ru.m210projects.Build.Render.Types.GL10.GL_TEXTURE0;
import static ru.m210projects.Build.Render.Types.GL10.GL_TEXTURE_COORD_ARRAY;
import static ru.m210projects.Build.Render.Types.GL10.GL_TEXTURE_ENV;
import static ru.m210projects.Build.Render.Types.GL10.GL_VERTEX_ARRAY;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.BufferUtils;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Render.ModelHandle.ModelInfo.Type;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MDModel;

public abstract class MD2ModelGL10 extends MDModel {

	private final ShortBuffer indices;
	private final FloatBuffer vertices;
	private final FloatBuffer uv;
	private final MD2Frame[] frames;
	private final MD2Triangle[] tris;

	protected abstract int bindSkin(final int pal, int skinnum);

	public MD2ModelGL10(MD2Info md) {
		super(md);

		MD2Builder builder = new MD2Builder(md);

		this.frames = builder.frames;
		this.tris = builder.triangles;

		int numTriangles = builder.header.numTriangles;
		this.indices = BufferUtils.newShortBuffer(numTriangles * 3);
		for (int i = 0; i < numTriangles; i++)
			for (int j = 0; j < 3; j++)
				indices.put((short) (i * 3 + j));
		indices.flip();

		vertices = BufferUtils.newFloatBuffer(numTriangles * 3 * 3);
		uv = BufferUtils.newFloatBuffer(numTriangles * 3 * 2);

		for (int i = 0; i < numTriangles; i++)
			for (int j = 0; j < 3; j++) {
				int idx = builder.triangles[i].texCoords[j];
				uv.put(builder.texCoords[idx][0]);
				uv.put(builder.texCoords[idx][1]);
			}
		uv.flip();
	}

	@Override
	public boolean render(int pal, int pad1, int skinnum, int pad2, float pad3) {
		boolean isRendered = false;

		int texunits = bindSkin(pal, skinnum);
		if (texunits != -1) {
			MD2Frame cframe = frames[this.cframe], nframe = frames[this.nframe];

			vertices.clear();
			for (int i = 0; i < tris.length; i++) // -60fps, but it's need for animation
				for (int j = 0; j < 3; j++) {
					int idx = tris[i].vertices[j];
					float x = cframe.vertices[idx][0] * cScale.x + nframe.vertices[idx][0] * nScale.x;
					float y = cframe.vertices[idx][1] * cScale.y + nframe.vertices[idx][1] * nScale.y;
					float z = cframe.vertices[idx][2] * cScale.z + nframe.vertices[idx][2] * nScale.z;
					vertices.put(x);
					vertices.put(z);
					vertices.put(y);
				}
			vertices.flip();

			int l = GL_TEXTURE0;
			do {
				BuildGdx.gl.glClientActiveTexture(l++);
				BuildGdx.gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
				BuildGdx.gl.glTexCoordPointer(2, GL_FLOAT, 0, uv);
			} while (l <= texunits);

			BuildGdx.gl.glEnableClientState(GL_VERTEX_ARRAY);
			BuildGdx.gl.glVertexPointer(3, GL_FLOAT, 0, vertices);
			BuildGdx.gl.glDrawElements(GL_TRIANGLES, 0, GL_UNSIGNED_SHORT, indices);

			while (texunits > GL_TEXTURE0) {
				BuildGdx.gl.glMatrixMode(GL_TEXTURE);
				BuildGdx.gl.glLoadIdentity();
				BuildGdx.gl.glMatrixMode(GL_MODELVIEW);
				BuildGdx.gl.glTexEnvf(GL_TEXTURE_ENV, GL_RGB_SCALE, 1.0f);
				BuildGdx.gl.glDisable(GL_TEXTURE_2D);

				BuildGdx.gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
				BuildGdx.gl.glClientActiveTexture(texunits - 1);

				BuildGdx.gl.glActiveTexture(--texunits);
			}
			BuildGdx.gl.glDisableClientState(GL_VERTEX_ARRAY);
			isRendered = true;
		}

		if (usesalpha)
			BuildGdx.gl.glDisable(GL_ALPHA_TEST);
		BuildGdx.gl.glDisable(GL_CULL_FACE);
		BuildGdx.gl.glLoadIdentity();

		return isRendered;
	}

	@Override
	public void loadSkins(int pal, int skinnum) {
		getSkin(pal, skinnum, 0);
	}

	@Override
	public Type getType() {
		return Type.Md2;
	}

	@Override
	public ShaderProgram getShader() {
		/* do nothing */
		return null;
	}
}
