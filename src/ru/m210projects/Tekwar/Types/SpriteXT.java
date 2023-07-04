package ru.m210projects.Tekwar.Types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;

public class SpriteXT {

	private static ByteBuffer buffer;
	public static final int sizeof = 34;

	public int sclass;
	public byte hitpoints;
	public int target;

	public int fxmask;
	public int aimask;
	public short basestat;
	public int basepic;
	public int standpic;
	public int walkpic;
	public int runpic;
	public int attackpic;
	public int deathpic;
	public int painpic;
	public int squatpic;
	public int morphpic;
	public int specialpic;
	public char lock;
	public char weapon;
	public short ext2;

	public void load(Resource bb) {
		sclass = bb.readByte();
		hitpoints = bb.readByte();
		target = bb.readShort();
		fxmask = bb.readShort();
		aimask = bb.readShort();
		basestat = bb.readShort();
		basepic = bb.readShort();
		standpic = bb.readShort();
		walkpic = bb.readShort();
		runpic = bb.readShort();
		attackpic = bb.readShort();
		deathpic = bb.readShort();
		painpic = bb.readShort();
		squatpic = bb.readShort();
		morphpic = bb.readShort();
		specialpic = bb.readShort();
		lock = (char) (bb.readByte() & 0xff);
		weapon = (char) (bb.readByte() & 0xff);
		ext2 = bb.readShort();
	}

	public void set(int var) {
		hitpoints = (byte) var;
		target = var;
		fxmask = var;
		aimask = var;
		basestat = (short) var;
		basepic = var;
		standpic = var;
		walkpic = var;
		runpic = var;
		attackpic = var;
		deathpic = var;
		painpic = var;
		squatpic = var;
		morphpic = var;
		specialpic = var;
		lock = (char) var;
		weapon = (char) var;
		ext2 = (short) var;
	}

	public byte[] getBytes() {
		if (buffer == null) {
			buffer = ByteBuffer.allocate(sizeof);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
		}
		buffer.clear();

		buffer.put((byte) sclass);
		buffer.put(hitpoints);
		buffer.putShort((short) target);
		buffer.putShort((short) fxmask);
		buffer.putShort((short) aimask);
		buffer.putShort(basestat);
		buffer.putShort((short) basepic);
		buffer.putShort((short) standpic);
		buffer.putShort((short) walkpic);
		buffer.putShort((short) runpic);
		buffer.putShort((short) attackpic);
		buffer.putShort((short) deathpic);
		buffer.putShort((short) painpic);
		buffer.putShort((short) squatpic);
		buffer.putShort((short) morphpic);
		buffer.putShort((short) specialpic);
		buffer.put((byte) lock);
		buffer.put((byte) weapon);
		buffer.putShort(ext2);

		return buffer.array();
	}
}
