package ru.m210projects.Wang.Type;

import ru.m210projects.Build.FileHandle.BufferResource;
import ru.m210projects.Build.FileHandle.Resource;

public class PANEL_SPRITE_OVERLAY {
	public Panel_State State;
	public int flags;
	public short tics;
	public short pic;
	public short xoff; // from panel sprite center x
	public short yoff; // from panel sprite center y
	
	public void copy(PANEL_SPRITE_OVERLAY src)
	{
		this.State = src.State;
		this.flags = src.flags;
		
		this.tics = src.tics;
		this.pic = src.pic;
		this.xoff = src.xoff;
		this.yoff = src.yoff;
	}
	
	public void save(BufferResource fil)
	{
		fil.writeInt(State != null ? State.ordinal() : -1);
		fil.writeInt(flags);
		fil.writeShort(tics);
		fil.writeShort(pic);
		fil.writeShort(xoff);
		fil.writeShort(yoff);
	}
	
	public void load(Resource res)
	{
		State = (Panel_State) Saveable.valueOf(res.readInt());
		flags = res.readInt();
		
		tics = res.readShort();
		pic = res.readShort();
		xoff = res.readShort();
		yoff = res.readShort();
	}
}
