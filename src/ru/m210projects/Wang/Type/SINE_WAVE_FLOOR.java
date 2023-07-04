package ru.m210projects.Wang.Type;

import ru.m210projects.Build.FileHandle.BufferResource;
import ru.m210projects.Build.FileHandle.Resource;

public class SINE_WAVE_FLOOR {
	public int floor_origz, ceiling_origz, range;
	public short sector, sintable_ndx, speed_shift;
	public int flags;
	
	public void reset() {
		floor_origz = -1;
		range = -1;
		ceiling_origz = -1;
		
		sintable_ndx = -1;
		speed_shift = -1;
		sector = -1;
		
		flags = -1;
	}

	public void save(BufferResource fil) {
		fil.writeInt(floor_origz);
		fil.writeInt(ceiling_origz);
		fil.writeInt(range);
		
		fil.writeShort(sector);
		fil.writeShort(sintable_ndx);
		fil.writeShort(speed_shift);
		
		fil.writeInt(flags);
	}
	
	public void load(Resource res) {
		floor_origz = res.readInt();
		ceiling_origz = res.readInt();
		range = res.readInt();
		
		sector = res.readShort();
		sintable_ndx = res.readShort();
		speed_shift = res.readShort();
		
		flags = res.readInt();
	}
	
}
