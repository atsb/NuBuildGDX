package ru.m210projects.Build.Render.ModelHandle.MDModel.MD2;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;

public class MD2Builder {

	public final MD2Header header;
	public MD2Triangle[] triangles;
	public float[][] texCoords;
	public MD2Frame[] frames;
	public int[] glcmds;

	public MD2Builder(MD2Info md) {
		Resource bb = BuildGdx.cache.open(md.getFilename(), 0);
		this.header = md.header;

		this.triangles = loadTriangles(bb);
		this.texCoords = loadTexCoords(bb);
		this.frames = loadFrames(bb);
		this.glcmds = loadGLCommands(bb);

		bb.close();
	}

	private MD2Frame[] loadFrames(Resource bb) {
		bb.seek(header.offsetFrames, Whence.Set);
		MD2Frame[] frames = new MD2Frame[header.numFrames];

		for (int i = 0; i < header.numFrames; i++) {
			MD2Frame frame = new MD2Frame();
			frame.vertices = new float[header.numVertices][3];

			float scaleX = bb.readFloat(), scaleY = bb.readFloat(), scaleZ = bb.readFloat();
			float transX = bb.readFloat(), transY = bb.readFloat(), transZ = bb.readFloat();
			frame.name = this.readString(bb, 16);

			for (int j = 0; j < header.numVertices; j++) {
				float x = (bb.readByte() & 0xFF) * scaleX + transX;
				float y = (bb.readByte() & 0xFF) * scaleY + transY;
				float z = (bb.readByte() & 0xFF) * scaleZ + transZ;
				bb.readByte(); // normal index

				frame.vertices[j][0] = x;
				frame.vertices[j][1] = y;
				frame.vertices[j][2] = z;
			}

			frames[i] = frame;
		}
		return frames;
	}

	private MD2Triangle[] loadTriangles(Resource bb) {
		bb.seek(header.offsetTriangles, Whence.Set);
		MD2Triangle[] triangles = new MD2Triangle[header.numTriangles];

		for (int i = 0; i < header.numTriangles; i++) {
			MD2Triangle triangle = new MD2Triangle();
			triangle.vertices[0] = bb.readShort();
			triangle.vertices[1] = bb.readShort();
			triangle.vertices[2] = bb.readShort();
			triangle.texCoords[0] = bb.readShort();
			triangle.texCoords[1] = bb.readShort();
			triangle.texCoords[2] = bb.readShort();
			triangles[i] = triangle;
		}

		return triangles;
	}

	private int[] loadGLCommands(Resource bb) {
		bb.seek(header.offsetGLCommands, Whence.Set);
		int[] glcmds = new int[header.numGLCommands];

		for (int i = 0; i < header.numGLCommands; i++)
			glcmds[i] = bb.readInt();
		return glcmds;
	}

	private float[][] loadTexCoords(Resource bb) {
		bb.seek(header.offsetTexCoords, Whence.Set);
		float[][] texCoords = new float[header.numTexCoords][2];
		float width = header.skinWidth;
		float height = header.skinHeight;

		for (int i = 0; i < header.numTexCoords; i++) {
			short u = bb.readShort();
			short v = bb.readShort();
			texCoords[i][0] = (u / width);
			texCoords[i][1] = (v / height);
		}
		return texCoords;
	}

	protected String readString(Resource bb, int len) {
		byte[] buf = new byte[len];
		bb.read(buf);

		for (int i = 0; i < buf.length; i++) {
			if (buf[i] == 0)
				return new String(buf, 0, i);
		}
		return new String(buf);
	}
}
