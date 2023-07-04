package ru.m210projects.Build.Render.ModelHandle.MDModel.MD2;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.Render.ModelHandle.MDInfo;

public class MD2Info extends MDInfo {

	public final MD2Header header;

	public MD2Info(Resource res, String file) throws Exception {
		super(file, Type.Md2);

		this.header = loadHeader(res);
		if ((header.ident != 0x32504449) || (header.version != 8))
			throw new Exception(); // "IDP2"

		res.seek(header.offsetFrames, Whence.Set);
		this.frames = new String[header.numFrames];
		this.numframes = header.numFrames;

		for (int i = 0; i < header.numFrames; i++) {
			res.seek(24, Whence.Current);
			frames[i] = readString(res, 16);
			res.seek(header.numVertices * 4, Whence.Current);
		}
	}

	protected MD2Header loadHeader(Resource res) {
		MD2Header header = new MD2Header();

		header.ident = res.readInt();
		header.version = res.readInt();
		header.skinWidth = res.readInt();
		header.skinHeight = res.readInt();
		header.frameSize = res.readInt();
		header.numSkins = res.readInt();
		header.numVertices = res.readInt();
		header.numTexCoords = res.readInt();
		header.numTriangles = res.readInt();
		header.numGLCommands = res.readInt();
		header.numFrames = res.readInt();
		header.offsetSkin = res.readInt();
		header.offsetTexCoords = res.readInt();
		header.offsetTriangles = res.readInt();
		header.offsetFrames = res.readInt();
		header.offsetGLCommands = res.readInt();
		header.offsetEnd = res.readInt();

		return header;
	}
}
