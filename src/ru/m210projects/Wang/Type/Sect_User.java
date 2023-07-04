package ru.m210projects.Wang.Type;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;

public class Sect_User {

	public final static int sizeof = 25;
	private static final ByteBuffer buffer = ByteBuffer.allocate(sizeof).order(ByteOrder.LITTLE_ENDIAN);
	
	public int dist, flags;
	public short depth_fract, depth; // do NOT change this, doubles as a long FIXED point number
	public short stag, // ST? tag number - for certain things it helps to know it
			ang, height, speed, damage, number; // usually used for matching number
	public byte flags2;

	public Sect_User set(Sect_User src)
	{
		this.dist = src.dist;
		this.flags = src.flags;
		this.depth_fract = src.depth_fract;
		this.depth = src.depth;
		this.stag = src.stag;
		this.ang = src.ang;
		this.height = src.height;
		this.speed = src.speed;
		this.damage = src.damage;
		this.number = src.number;
		this.flags2 = src.flags2;

		return this;
	}

	public void reset() {
		this.dist = 0;
		this.flags = 0;
		this.depth_fract = 0;
		this.depth = 0;
		this.stag = 0;
		this.ang = 0;
		this.height = 0;
		this.speed = 0;
		this.damage = 0;
		this.number = 0;
		this.flags2 = 0;
	}

	public byte[] getBytes() {
		buffer.clear();

		buffer.putInt(dist);
		buffer.putInt(flags);
		buffer.putShort(depth_fract);
		buffer.putShort(depth);
		
		buffer.putShort(stag);
		buffer.putShort(ang);
		buffer.putShort(height);
		buffer.putShort(speed);
		buffer.putShort(damage);
		buffer.putShort(number);

		buffer.put(flags2);
		
		return buffer.array();
	}

	public void load(Resource res) {		
		dist = res.readInt();
		flags = res.readInt();
		depth_fract = res.readShort();
		depth = res.readShort();
		
		stag = res.readShort();
		ang = res.readShort();
		height = res.readShort();
		speed = res.readShort();
		damage = res.readShort();
		number = res.readShort();
		
		flags2 = res.readByte();
	}

	public void copy(Sect_User src) {
		dist = src.dist;
		flags = src.flags;
		depth_fract = src.depth_fract;
		depth = src.depth;
		
		stag = src.stag;
		ang = src.ang;
		height = src.height;
		speed = src.speed;
		damage = src.damage;
		number = src.number;
		
		flags2 = src.flags2;
	}

}
