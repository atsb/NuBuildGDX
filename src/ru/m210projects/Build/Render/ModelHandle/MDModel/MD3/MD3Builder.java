package ru.m210projects.Build.Render.ModelHandle.MDModel.MD3;

import java.nio.FloatBuffer;
import java.util.HashMap;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;

public class MD3Builder {

	public MD3Header head;
	public MD3Frame[] frames;
	public HashMap<String, Matrix4>[] tags;
	public MD3Surface[] surfaces;

	public MD3Builder(MD3Info md) {
		Resource bb = BuildGdx.cache.open(md.getFilename(), 0);
		this.head = md.header;

		MD3Frame[] frames = loadFrames(head, bb);
		HashMap<String, Matrix4>[] tags = loadTags(head, bb);
		MD3Surface[] surfaces = loadSurfaces(head, bb);

		this.frames = frames;
		this.tags = tags;
		this.surfaces = surfaces;

		bb.close();
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

	private MD3Frame[] loadFrames(MD3Header header, Resource bb) {
		bb.seek(header.offsetFrames, Whence.Set);
		MD3Frame[] out = new MD3Frame[header.numFrames];
		for (int i = 0; i < header.numFrames; i++) {
			MD3Frame frame = new MD3Frame();
			frame.min = new Vector3(bb.readFloat(), bb.readFloat(), bb.readFloat());
			frame.max = new Vector3(bb.readFloat(), bb.readFloat(), bb.readFloat());
			frame.origin = new Vector3(bb.readFloat(), bb.readFloat(), bb.readFloat());
			frame.radius = bb.readFloat();
			frame.name = readString(bb, 16);
			out[i] = frame;
		}
		return out;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Matrix4>[] loadTags(MD3Header header, Resource bb) {
		bb.seek(header.offsetTags, Whence.Set);
		HashMap<String, Matrix4>[] out = new HashMap[header.numFrames];
		for (int k = 0; k < header.numFrames; k++) {
			out[k] = new HashMap<String, Matrix4>();
			for (int i = 0; i < header.numTags; i++) {
				String tagName = readString(bb, 64);

				Vector3 pos = new Vector3(bb.readFloat(), bb.readFloat(), bb.readFloat());
				Vector3 xAxis = new Vector3(bb.readFloat(), bb.readFloat(), bb.readFloat());
				Vector3 yAxis = new Vector3(bb.readFloat(), bb.readFloat(), bb.readFloat());
				Vector3 zAxis = new Vector3(bb.readFloat(), bb.readFloat(), bb.readFloat());
				Matrix4 mat = new Matrix4();
				mat.set(xAxis, yAxis, zAxis, pos);

				out[k].put(tagName, mat);
			}
		}
		return out;
	}

	private MD3Surface[] loadSurfaces(MD3Header header, Resource bb) {
		int offsetSurfaces = header.offsetSurfaces;
		MD3Surface[] out = new MD3Surface[header.numSurfaces];
		for (int i = 0; i < header.numSurfaces; i++) {
			bb.seek(offsetSurfaces, Whence.Set);
			MD3Surface surf = new MD3Surface();
			surf.id = bb.readInt();
			surf.nam = readString(bb, 64);
			surf.flags = bb.readInt();
			surf.numframes = bb.readInt();
			surf.numshaders = bb.readInt();
			surf.numverts = bb.readInt();
			surf.numtris = bb.readInt();
			surf.ofstris = bb.readInt();
			surf.ofsshaders = bb.readInt();
			surf.ofsuv = bb.readInt();
			surf.ofsxyzn = bb.readInt();
			surf.ofsend = bb.readInt();

			surf.tris = loadTriangles(surf, offsetSurfaces, bb);
			surf.shaders = loadShaders(surf, offsetSurfaces, bb);
			surf.uv = loadUVs(surf, offsetSurfaces, bb);
			surf.xyzn = loadVertices(surf, offsetSurfaces, bb);
			offsetSurfaces += surf.ofsend;

			out[i] = surf;
		}
		return out;
	}

	private int[][] loadTriangles(MD3Surface surf, int offsetSurfaces, Resource bb) {
		bb.seek(offsetSurfaces + surf.ofstris, Whence.Set);
		int[][] out = new int[surf.numtris][3];
		for (int i = 0; i < surf.numtris; i++) {
			out[i][0] = bb.readInt();
			out[i][1] = bb.readInt();
			out[i][2] = bb.readInt();
		}
		return out;
	}

	private FloatBuffer loadUVs(MD3Surface surf, int offsetSurfaces, Resource bb) {
		bb.seek(offsetSurfaces + surf.ofsuv, Whence.Set);
		FloatBuffer out = BufferUtils.newFloatBuffer(2 * surf.numverts);
		for (int i = 0; i < surf.numverts; i++) {
			out.put(bb.readFloat());
			out.put(bb.readFloat());
		}
		out.flip();
		return out;
	}

    private MD3Vertice[][] loadVertices(MD3Surface surf, int offsetSurfaces, Resource bb) {
		bb.seek(offsetSurfaces + surf.ofsxyzn, Whence.Set);
		MD3Vertice[][] out = new MD3Vertice[surf.numframes][surf.numverts];
		for (int i = 0; i < surf.numframes; i++) {
			for (int j = 0; j < surf.numverts; j++) {
				MD3Vertice xyzn = new MD3Vertice();
				xyzn.x = bb.readShort();
				xyzn.y = bb.readShort();
				xyzn.z = bb.readShort();
				xyzn.nlat = (short) (bb.readByte() & 0xFF);
				xyzn.nlng = (short) (bb.readByte() & 0xFF);
				out[i][j] = xyzn;
			}
		}
		return out;
	}

	private MD3Shader[] loadShaders(MD3Surface surf, int offsetSurfaces, Resource bb) {
		bb.seek(offsetSurfaces + surf.ofsshaders, Whence.Set);
		MD3Shader[] out = new MD3Shader[surf.numshaders];
		for (int i = 0; i < surf.numshaders; i++) {
			MD3Shader shader = new MD3Shader();
			shader.name = readString(bb, 64);
			shader.index = bb.readInt();
			out[i] = shader;
		}
		return out;
	}
}
