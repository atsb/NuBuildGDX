package ru.m210projects.Tekwar.Types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;

public class Sectoreffect {
	
	private static ByteBuffer buffer;
	public static final int sizeof = 56;
	
	public int sectorflags;
	public int animate;
	public int  hi,lo;
	public int  delay,delayreset;
	public int  ang;
	public int  triggerable;
	public short  warpto =-1;
	public int warpx,warpy,warpz;
	public int sin,cos;
	public short damage;
	
	public void reset()
	{
		sectorflags = 0;
    	animate = 0;
    	hi = 0;
    	lo = 0;
    	
    	delay = 0;
    	delayreset = 0;
    	ang = 0;
    	triggerable = 0;
    	warpto = -1;
    	warpx = 0;
    	warpy = 0;
    	warpz = 0;
    	sin = 0;
    	cos = 0;
    	damage = 0;
	}
	
	public void load(Resource bb) {
    	sectorflags = bb.readInt();
    	animate = bb.readInt();
    	hi = bb.readInt();
    	lo = bb.readInt();
    	
    	delay = bb.readInt();
    	delayreset = bb.readInt();
    	ang = bb.readInt();
    	triggerable = bb.readInt();
    	warpto = bb.readShort();
    	warpx = bb.readInt();
    	warpy = bb.readInt();
    	warpz = bb.readInt();
    	sin = bb.readInt();
    	cos = bb.readInt();
    	damage = bb.readShort();
	}
	
	public byte[] getBytes()
	{
		if(buffer == null) {
			buffer = ByteBuffer.allocate(sizeof); 
			buffer.order( ByteOrder.LITTLE_ENDIAN);
		}
		buffer.clear();
		
		buffer.putInt(sectorflags);
		buffer.putInt(animate);
		buffer.putInt(hi);
		buffer.putInt(lo);

		buffer.putInt(delay);
		buffer.putInt(delayreset);
		buffer.putInt(ang);
		buffer.putInt(triggerable);
		buffer.putShort(warpto);
		buffer.putInt(warpx);
		buffer.putInt(warpy);
		buffer.putInt(warpz);
		buffer.putInt(sin);
		buffer.putInt(cos);
		buffer.putShort(damage);

		return buffer.array();
	}
}
