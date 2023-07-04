package ru.m210projects.Build.Render.ModelHandle.MDModel.MD3;

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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Render.ModelHandle.ModelInfo.Type;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MDModel;

public abstract class MD3ModelGL10 extends MDModel {

	private final ShortBuffer indices;
	private final FloatBuffer vertices;

	private final MD3Surface[] surfaces;
	private final int numSurfaces;

	public MD3ModelGL10(MD3Info md) {
		super(md);

		MD3Builder builder = new MD3Builder(md);

		this.surfaces = builder.surfaces;
		this.numSurfaces = builder.head.numSurfaces;

		int maxtris = 0;
		int maxverts = 0;
		for (int i = 0; i < this.numSurfaces; i++) {
			MD3Surface surf = surfaces[i];
			maxtris = Math.max(maxtris, surf.numtris);
			maxverts = Math.max(maxverts, surf.numverts);
		}

		this.indices = BufferUtils.newShortBuffer(maxtris * 3);
		this.vertices = BufferUtils.newFloatBuffer(maxverts * 3);
	}

	protected abstract int bindSkin(final int pal, int skinnum, int surfnum);

	@Override
	public boolean render(int pal, int pad1, int skinnum, int pad2, float pad3) {
		boolean isRendered = false;

		for (int surfi = 0; surfi < numSurfaces; surfi++) {
			MD3Surface s = surfaces[surfi];

			int texunits = bindSkin(pal, skinnum, surfi);
			if (texunits != -1) {

				vertices.clear();
				for (int i = 0; i < s.numverts; i++) {
					MD3Vertice v0 = s.xyzn[cframe][i];
					MD3Vertice v1 = s.xyzn[nframe][i];

					vertices.put(v0.x * cScale.x + v1.x * nScale.x);
					vertices.put(v0.z * cScale.z + v1.z * nScale.z);
					vertices.put(v0.y * cScale.y + v1.y * nScale.y);
				}
				vertices.flip();

				indices.clear();
				for (int i = s.numtris - 1; i >= 0; i--)
					for (int j = 0; j < 3; j++)
						indices.put((short) s.tris[i][j]);
				indices.flip();

				int l = GL_TEXTURE0;
				do {
					BuildGdx.gl.glClientActiveTexture(l++);
					BuildGdx.gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
					BuildGdx.gl.glTexCoordPointer(2, GL_FLOAT, 0, s.uv);
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
			} else
				break;
		}

		if (usesalpha)
			BuildGdx.gl.glDisable(GL_ALPHA_TEST);
		BuildGdx.gl.glDisable(GL_CULL_FACE);
		BuildGdx.gl.glLoadIdentity();

		return isRendered;
	}

	@Override
	public MDModel setScale(Vector3 cScale, Vector3 nScale) {
		this.cScale.set(cScale);
		this.nScale.set(nScale);

		this.cScale.scl(1 / 64.0f);
		this.nScale.scl(1 / 64.0f);

		return this;
	}

	@Override
	public void loadSkins(int pal, int skinnum) {
		for (int surfi = 0; surfi < numSurfaces; surfi++)
			getSkin(pal, skinnum, surfi);
	}

	@Override
	public Type getType() {
		return Type.Md3;
	}

	@Override
	public ShaderProgram getShader() {
		/* do nothing */
		return null;
	}
}
