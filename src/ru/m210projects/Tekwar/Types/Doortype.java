package ru.m210projects.Tekwar.Types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;

public class Doortype {

	private static ByteBuffer buffer;
	public static final int sizeof = 84;

	public int type;
	public int state;
	public int sector;
	public int step;
	public int delay;
	public int[] goalz = new int[4];
	public int[] points = new int[4];
	public short[] walls = new short[8];
	public int centx;
	public int centy;
	public int centz;
	public int subtype; // jeff added 9-20

	public void load(Resource bb) {
		type = bb.readInt();
		state = bb.readInt();
		sector = bb.readInt();
		step = bb.readInt();
		delay = bb.readInt();
		for (int i = 0; i < 4; i++)
			goalz[i] = bb.readInt();
		for (int i = 0; i < 4; i++)
			points[i] = bb.readInt();
		for (int i = 0; i < 8; i++)
			walls[i] = bb.readShort();
		centx = bb.readInt();
		centy = bb.readInt();
		centz = bb.readInt();
		subtype = bb.readInt();
	}

	public byte[] getBytes() {
		if (buffer == null) {
			buffer = ByteBuffer.allocate(sizeof);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
		}
		buffer.clear();

		buffer.putInt(type);
		buffer.putInt(state);
		buffer.putInt(sector);
		buffer.putInt(step);
		buffer.putInt(delay);
		for (int j = 0; j < 4; j++)
			buffer.putInt(goalz[j]);
		for (int j = 0; j < 4; j++)
			buffer.putInt(points[j]);
		for (int j = 0; j < 8; j++)
			buffer.putShort(walls[j]);
		buffer.putInt(centx);
		buffer.putInt(centy);
		buffer.putInt(centz);
		buffer.putInt(subtype);

		return buffer.array();
	}

	public void reset() {
		type = 0;
		state = 0;
		sector = 0;
		step = 0;
		delay = 0;
		for (int i = 0; i < 4; i++)
			goalz[i] = 0;
		for (int i = 0; i < 4; i++)
			points[i] = 0;
		for (int i = 0; i < 8; i++)
			walls[i] = 0;
		centx = 0;
		centy = 0;
		centz = 0;
		subtype = 0;
	}
}
