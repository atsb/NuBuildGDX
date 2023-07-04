package ru.m210projects.Blood.Types.Seq;

import ru.m210projects.Build.FileHandle.Resource;

public class SeqFrame {
	public int nTile;
	public boolean translucent;
	public boolean translucentR;
	public boolean blocking;
	public boolean hitscan;
	public boolean pushable;
	public boolean invisible;
	
	public short xrepeat;
	public short yrepeat;

	public byte shade;
	public int pal;
	public boolean trigger;// trigger callback
	public boolean smoke;	// add smoke tsprite
	public boolean aiming;
	public boolean flipx;
	public boolean flipy;
	
	public SeqFrame(Resource bb)
	{
		int tmp = bb.readShort() & 0xFFFF;
		nTile = tmp & 0x0FFF;

		translucent = ((tmp & 0x1000) == 0x1000);
		translucentR = ((tmp & 0x2000) == 0x2000);
		blocking = ((tmp & 0x4000) == 0x4000);
		hitscan = ((tmp & 0x8000) == 0x8000);
		
		xrepeat = (short) (bb.readByte() & 0xFF); //2
		yrepeat = (short) (bb.readByte() & 0xFF); //3
		
		shade = bb.readByte(); //4
		tmp = bb.readByte() & 0xFF;
		pal = tmp & 0x1F; //5
		trigger = ((tmp & 0x20) == 0x20);
		smoke = ((tmp & 0x40) == 0x40);
		aiming = ((tmp & 0x80) == 0x80);
		
		tmp = bb.readByte() & 0xFF;
		invisible = (tmp & 4) == 4;
		pushable = (tmp & 1) == 1;
		
		flipx = ((tmp & 0x08) == 0x08);
		flipy = ((tmp & 0x10) == 0x10);
		nTile |= (tmp >> 5) << 12; //use up to 32767 tiles
		bb.readByte(); //reserved
	}
}