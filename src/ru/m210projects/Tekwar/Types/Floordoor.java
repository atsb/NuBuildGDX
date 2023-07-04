package ru.m210projects.Tekwar.Types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;

public class Floordoor {
	
	private static ByteBuffer buffer;
	public static final int sizeof = 24;

	public int  state;
	public int  sectnum;
	public short  wall1,wall2;
	public int  dist1,dist2;
	public int  dir;
	
	public void reset() {
    	state = 0;
    	sectnum = 0;
    	wall1 = 0;
    	wall2 = 0;
    	dist1 = 0;
    	dist2 = 0;
    	dir = 0;
	}
	
	public void load(Resource bb) {
    	state = bb.readInt();
    	sectnum = bb.readInt();
    	wall1 = bb.readShort();
    	wall2 = bb.readShort();
    	dist1 = bb.readInt();
    	dist2 = bb.readInt();
    	dir = bb.readInt();
	}
	
	public byte[] getBytes()
	{
		if(buffer == null) {
			buffer = ByteBuffer.allocate(sizeof); 
			buffer.order( ByteOrder.LITTLE_ENDIAN);
		}
		buffer.clear();
		
		buffer.putInt(state);
		buffer.putInt(sectnum);
		buffer.putShort(wall1);
		buffer.putShort(wall2);
		buffer.putInt(dist1);
		buffer.putInt(dist2);
		buffer.putInt(dir);

		return buffer.array();
	}
}
