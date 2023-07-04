/*
 *  Wall structure code originally written by Ken Silverman
 *	Ken Silverman's official web site: http://www.advsys.net/ken
 *
 *  See the included license file "BUILDLIC.TXT" for license info.
 *
 *  This file has been modified by Alexander Makarov-[M210] (m210-2007@mail.ru)
 */

package ru.m210projects.Build.Types;

import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXWALLS;
import static ru.m210projects.Build.Gameutils.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.DataResource;
import ru.m210projects.Build.FileHandle.Resource;

public class WALL {
	public static final int sizeof = 32;
	private static final ByteBuffer buffer = ByteBuffer.allocate(sizeof).order( ByteOrder.LITTLE_ENDIAN);

	public int x, y; //8
	public short point2, nextwall, nextsector, cstat; //8
	public short picnum, overpicnum; //4
	public byte shade; //1
	public short pal, xrepeat, yrepeat, xpanning, ypanning; //5
	public short lotag, hitag, extra; //6

	public WALL() {}

	public WALL(byte[] data) {
    	buildWall(new DataResource(data));
	}

	public WALL(Resource data) {
    	buildWall(data);
	}

	public void buildWall(Resource bb)
	{
		x = bb.readInt();
    	y = bb.readInt();
    	point2 = bb.readShort();
    	if(point2 < 0 || point2 >= MAXWALLS) point2 = 0;
    	nextwall = bb.readShort();
    	if(nextwall < 0 || nextwall >= MAXWALLS) nextwall = -1;
    	nextsector = bb.readShort();
    	if(nextsector < 0 || nextsector >= MAXSECTORS) nextsector = -1;
    	cstat = bb.readShort();
    	picnum = bb.readShort();
    	if(!isValidTile(picnum)) picnum = 0;
    	overpicnum = bb.readShort();
    	if(!isValidTile(overpicnum)) overpicnum = 0;
    	shade = bb.readByte();
    	pal = (short) (bb.readByte()&0xFF);
    	xrepeat = (short) (bb.readByte()&0xFF);
    	yrepeat = (short) (bb.readByte()&0xFF);
    	xpanning = (short) (bb.readByte()&0xFF);
    	ypanning = (short) (bb.readByte()&0xFF);
    	lotag = bb.readShort();
    	hitag = bb.readShort();
    	extra = bb.readShort();
	}

	public void set(WALL src) {
		x = src.x;
    	y = src.y;
    	point2 = src.point2;
    	nextwall = src.nextwall;
    	nextsector = src.nextsector;
    	cstat = src.cstat;
    	picnum = src.picnum;
    	overpicnum = src.overpicnum;
    	shade = src.shade;
    	pal = src.pal;
    	xrepeat = src.xrepeat;
    	yrepeat = src.yrepeat;
    	xpanning = src.xpanning;
    	ypanning = src.ypanning;
    	lotag = src.lotag;
    	hitag = src.hitag;
    	extra = src.extra;
	}

	public byte[] getBytes()
	{
		buffer.clear();

		buffer.putInt(this.x);
    	buffer.putInt(this.y);
    	buffer.putShort(this.point2);
    	buffer.putShort(this.nextwall);
    	buffer.putShort(this.nextsector);
    	buffer.putShort(this.cstat);
    	buffer.putShort(this.picnum);
    	buffer.putShort(this.overpicnum);
    	buffer.put(this.shade);
    	buffer.put((byte)this.pal);
    	buffer.put((byte)this.xrepeat);
    	buffer.put((byte)this.yrepeat);
    	buffer.put((byte)this.xpanning);
    	buffer.put((byte)this.ypanning);
    	buffer.putShort(this.lotag);
    	buffer.putShort(this.hitag);
    	buffer.putShort(this.extra);

    	return buffer.array();
	}

	public boolean isSwapped() {
		return (cstat & 2) != 0;
	}


	public boolean isBottomAligned() {
		return (cstat & 4) != 0;
	}

	public boolean isXFlip() {
		return (cstat & 8) != 0;
	}

	public boolean isYFlip() {
		return (cstat & 256) != 0;
	}

	public boolean isMasked() {
		return (cstat & 16) != 0;
	}

	public boolean isOneWay() {
		return (cstat & 32) != 0;
	}

	public boolean isTransparent() {
		return (cstat & 128) != 0;
	}

	public boolean isTransparent2() {
		return (cstat & 512) != 0;
	}

	@Override
	public String toString()
	{
		String out = "x " + x + " \r\n";
		out += "y " + y + " \r\n";
		out += "point2 " + point2 + " \r\n";
		out += "nextwall " + nextwall + " \r\n";
		out += "nextsector " + nextsector + " \r\n";
		out += "cstat " + cstat + " \r\n";
		out += "picnum " + picnum + " \r\n";
		out += "overpicnum " + overpicnum + " \r\n";
		out += "shade " + shade + " \r\n";
		out += "pal " + pal + " \r\n";
		out += "xrepeat " + xrepeat + " \r\n";
		out += "yrepeat " + yrepeat + " \r\n";
		out += "xpanning " + xpanning + " \r\n";
		out += "ypanning " + ypanning + " \r\n";
		out += "lotag " + lotag + " \r\n";
		out += "hitag " + hitag + " \r\n";
		out += "extra " + extra + " \r\n";

		return out;
	}
}
