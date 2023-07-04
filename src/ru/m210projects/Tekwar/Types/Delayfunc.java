package ru.m210projects.Tekwar.Types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;

public class Delayfunc {
	
	private static ByteBuffer buffer;
	public static final int sizeof = 8;
	
	public short func;
	public int tics;
	public short parm;

	public void set(Delayfunc src) {
		func = src.func;
		tics = src.tics;
		parm = src.parm;
	}

	public void load(Resource bb) {
		func = bb.readShort();
		tics = bb.readInt();
		parm = bb.readShort();
	}

	public void set(int var) {
		func = (short) var;
		tics = var;
		parm = (short) var;
	}
	
	public byte[] getBytes()
	{
		if(buffer == null) {
			buffer = ByteBuffer.allocate(sizeof); 
			buffer.order( ByteOrder.LITTLE_ENDIAN);
		}
		buffer.clear();
		
		buffer.putShort(func);
		buffer.putInt(tics);
		buffer.putShort(parm);

		return buffer.array();
	}
}
