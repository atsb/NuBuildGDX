package ru.m210projects.Wang.Type;

import ru.m210projects.Build.FileHandle.BufferResource;
import ru.m210projects.Build.FileHandle.Resource;

public class SINE_WALL {
	public int orig_xy, range;
	public short wall, sintable_ndx, speed_shift, type;

	public void reset() {
		orig_xy = -1;
		range = -1;
		wall = -1;
		sintable_ndx = -1;
		speed_shift = -1;
		type = -1;
	}

	public void save(BufferResource fil) {
		fil.writeInt(orig_xy);
		fil.writeInt(range);
		fil.writeShort(wall);
		fil.writeShort(sintable_ndx);
		fil.writeShort(speed_shift);
		fil.writeShort(type);
	}
	
	public void load(Resource res) {
		orig_xy = res.readInt();
		range = res.readInt();
		wall = res.readShort();
		sintable_ndx = res.readShort();
		speed_shift = res.readShort();
		type = res.readShort();
	}
}
