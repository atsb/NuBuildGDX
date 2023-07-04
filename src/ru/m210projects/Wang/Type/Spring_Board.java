package ru.m210projects.Wang.Type;

import ru.m210projects.Build.FileHandle.BufferResource;
import ru.m210projects.Build.FileHandle.Resource;

public class Spring_Board {
	public short Sector, TimeOut;
	
	public void reset() {
		Sector = -1;
		TimeOut = -1;
	}

	public void save(BufferResource fil) {
		fil.writeShort(Sector);
		fil.writeShort(TimeOut);
	}
	
	public void load(Resource res) {
		Sector = res.readShort();
		TimeOut = res.readShort();
	}
}
