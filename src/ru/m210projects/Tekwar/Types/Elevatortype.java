package ru.m210projects.Tekwar.Types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;

public class Elevatortype {

	private static ByteBuffer buffer;
	public static final byte sizeof = 8;
	
	public int hilevel;
	public int lolevel;
	
	public void reset() {
    	hilevel = 0;
    	lolevel = 0;
	}
	
	public void load(Resource bb) {
    	hilevel = bb.readInt();
    	lolevel = bb.readInt();
	}
	
	public byte[] getBytes()
	{
		if(buffer == null) {
			buffer = ByteBuffer.allocate(sizeof); 
			buffer.order( ByteOrder.LITTLE_ENDIAN);
		}
		buffer.clear();
		
		buffer.putInt(hilevel);
		buffer.putInt(lolevel);

		return buffer.array();
	}
}
