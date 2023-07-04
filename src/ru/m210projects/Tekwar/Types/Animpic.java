package ru.m210projects.Tekwar.Types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;

public class Animpic {

	public static final int sizeof = 10;
	private static ByteBuffer buffer;
	
	public short frames;
	public short pic;
	public short tics;
	public int nextclock;
	
	public void set(Animpic src) {
		frames = src.frames;
		pic = src.pic;
		tics = src.tics;
		nextclock = src.nextclock;
	}
	
	public void set(int var) {
		frames = (short) var;
		pic = (short) var;
		tics = (short) var;
		nextclock = var;
	}
	
	public void load(Resource bb) {
		frames = bb.readShort();
		pic = bb.readShort();
		tics = bb.readShort();
		nextclock = bb.readInt();
	}
	
	public byte[] getBytes()
	{
		if(buffer == null) {
			buffer = ByteBuffer.allocate(sizeof); 
			buffer.order( ByteOrder.LITTLE_ENDIAN);
		}
		buffer.clear();
		
		buffer.putShort(frames);
		buffer.putShort(pic);
		buffer.putShort(tics);
		buffer.putInt(nextclock);
		
		System.err.println("animpic " + buffer.position());
		
		return buffer.array();
	}
}
