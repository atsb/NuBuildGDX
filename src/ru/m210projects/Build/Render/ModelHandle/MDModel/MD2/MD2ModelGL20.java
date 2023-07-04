package ru.m210projects.Build.Render.ModelHandle.MDModel.MD2;

import java.nio.FloatBuffer;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.utils.ShortArray;

import ru.m210projects.Build.Render.ModelHandle.ModelInfo.Type;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MDModel;

public abstract class MD2ModelGL20 extends MDModel {

	private final Mesh mesh;
	private final MD2Frame[] frames;
	private final MD2Triangle[] tris;
	private float oldinterpol;

	public MD2ModelGL20(MD2Info md) {
		super(md);

		MD2Builder builder = new MD2Builder(md);

		this.frames = builder.frames;
		this.tris = builder.triangles;

		int numTriangles = builder.header.numTriangles;
		ShortArray indices = new ShortArray(numTriangles * 3);
		FloatArray vertices = new FloatArray(numTriangles * 3 * 6);

		for (int i = 0; i < numTriangles; i++)
			for (int j = 0; j < 3; j++)
				indices.add((short) (i * 3 + j));

		MD2Frame cframe = frames[this.cframe];
		for (int i = 0; i < tris.length; i++) {
			for (int j = 0; j < 3; j++) {
				int idx = tris[i].vertices[j];
				float x = cframe.vertices[idx][0];
				float y = cframe.vertices[idx][1];
				float z = cframe.vertices[idx][2];
				vertices.add(x);
				vertices.add(z);
				vertices.add(y);
				vertices.add(NumberUtils.intToFloatColor(-1));
				idx = tris[i].texCoords[j];
				vertices.add(builder.texCoords[idx][0]);
				vertices.add(builder.texCoords[idx][1]);
			}
		}

		float[] va = vertices.toArray();
		short[] ia = indices.toArray();

		int size = 6;
		mesh = new Mesh(numframes < 2, va.length / size, ia.length, VertexAttribute.Position(), VertexAttribute.ColorPacked(), VertexAttribute.TexCoords(0));
		mesh.setVertices(va);
		mesh.setIndices(ia);
	}

	protected abstract int bindSkin(final int pal, int skinnum);

	@Override
	public boolean render(int pal, int pad1, int skinnum, int pad2, float pad3) {
		boolean isRendered = false;

		if(numframes > 2 && interpol != oldinterpol) { // mesh update
			int pos = 0;
			oldinterpol = interpol;
			float f = interpol;
			float g = 1 - f;

			FloatBuffer vertices = mesh.getVerticesBuffer();
			MD2Frame cframe = frames[this.cframe], nframe = frames[this.nframe];
			for (int i = 0; i < tris.length; i++) {
				for (int j = 0; j < 3; j++) {
					int idx = tris[i].vertices[j];
					float x = cframe.vertices[idx][0] * g + nframe.vertices[idx][0] * f;
					float y = cframe.vertices[idx][1] * g + nframe.vertices[idx][1] * f;
					float z = cframe.vertices[idx][2] * g + nframe.vertices[idx][2] * f;
					vertices.put(pos++, x);
					vertices.put(pos++, z);
					vertices.put(pos++, y);
					pos += 3;
				}
			}
		}

		int texunits = bindSkin(pal, skinnum);
		if (texunits != -1) {
			mesh.render(getShader(), GL20.GL_TRIANGLES);
			isRendered = true;
		}

		return isRendered;
	}

	@Override
	public Type getType() {
		return Type.Md2;
	}

	@Override
	public void loadSkins(int pal, int skinnum) {
		getSkin(pal, skinnum, 0);
	}

}
