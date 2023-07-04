package ru.m210projects.Tekwar.Types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;

public class Mapsndfxtype {

	private static ByteBuffer buffer;
	public static final byte sizeof = 26;
	
	public int x, y;
	public short sector;
	public int snum;
	public int loops;
	public int type;
	public int id;

	public void load(Resource bb) {
		x = bb.readInt();
		y = bb.readInt();
		sector = bb.readShort();
		snum = bb.readInt();
		loops = bb.readInt();
		type = bb.readInt();
		id = bb.readInt();
	}

	public byte[] getBytes()
	{
		if(buffer == null) {
			buffer = ByteBuffer.allocate(sizeof); 
			buffer.order( ByteOrder.LITTLE_ENDIAN);
		}
		buffer.clear();
		
		buffer.putInt(x);
		buffer.putInt(y);
		buffer.putShort(sector);
		buffer.putInt(snum);
		buffer.putInt(loops);
		buffer.putInt(type);
		buffer.putInt(id);

		return buffer.array();
	}

	public void reset() {
		x = 0;
		y = 0;
		sector = 0;
		snum = 0;
		loops = 0;
		type = 0;
		id = 0;
	}
}
