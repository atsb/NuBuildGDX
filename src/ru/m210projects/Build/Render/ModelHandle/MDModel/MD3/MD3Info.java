package ru.m210projects.Build.Render.ModelHandle.MDModel.MD3;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.Render.ModelHandle.MDInfo;

public class MD3Info extends MDInfo {

	public final MD3Header header;

	public MD3Info(Resource res, String file) throws Exception {
		super(file, Type.Md3);

		this.header = loadHeader(res);
		if ((header.ident != 0x33504449) || (header.version != 15))
			throw new Exception(); //"IDP3"

		res.seek(header.offsetFrames, Whence.Set);
		this.frames = new String[header.numFrames];
		this.numframes = header.numFrames;

        for(int i = 0; i < header.numFrames; i++) {
        	res.seek(40, Whence.Current);
        	frames[i] = readString(res, 16);
        }
	}

	protected MD3Header loadHeader (Resource res) {
		MD3Header header = new MD3Header();

		header.ident = res.readInt();
		header.version = res.readInt();
		header.filename = readString(res, 64);
		header.flags = res.readInt();
		header.numFrames = res.readInt();
		header.numTags = res.readInt();
		header.numSurfaces = res.readInt();
		header.numSkins = res.readInt();
		header.offsetFrames = res.readInt();
		header.offsetTags = res.readInt();
		header.offsetSurfaces = res.readInt();
		header.offsetEnd = res.readInt();

		return header;
	}
}
