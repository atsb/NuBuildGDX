
package ru.m210projects.Wang.Type;

import ru.m210projects.Build.Pattern.BuildNet.NetInput;
import ru.m210projects.Build.Types.LittleEndian;

public class Input implements NetInput {

	public int bits;
	public float angvel;
	public float aimvel;
	public int vel, svel;

	@Override
	public int GetInput(byte[] packbuf, int offset, NetInput oldInput) {
		int k = packbuf[offset++] & 0xFF;
		Copy(oldInput);
    
		if ((k&1) != 0) { vel = LittleEndian.getShort(packbuf, offset); offset += 2; }
		if ((k&2) != 0) { svel = LittleEndian.getShort(packbuf, offset); offset += 2; }
        if ((k&4) != 0) { angvel = LittleEndian.getFloat(packbuf, offset); offset += 4; }
        if ((k&8) != 0)  { bits = ((bits&0xffffff00)|(packbuf[offset++]&0xFF)); }
        if ((k&16) != 0) { bits = ((bits&0xffff00ff)|(packbuf[offset++]&0xFF)<<8); }
        if ((k&32) != 0) { bits = ((bits&0xff00ffff)|(packbuf[offset++]&0xFF)<<16); }
        if ((k&64) != 0) { bits = ((bits&0x00ffffff)|(packbuf[offset++]&0xFF)<<24); }
        if ((k&128) != 0) { aimvel = LittleEndian.getFloat(packbuf, offset); offset += 4; }
        
		return offset;
	}

	@Override
	public int PutInput(byte[] packbuf, int offset, NetInput oldInput) {
		int syncptr = offset;
		packbuf[offset++] = 0;
		
		Input osyn = (Input) oldInput;

        if (vel != osyn.vel)
        {
        	LittleEndian.putShort(packbuf, offset, (short) vel);
        	offset += 2;
            packbuf[syncptr] |= 1;
        }
        if (svel != osyn.svel)
        {
        	LittleEndian.putShort(packbuf, offset, (short) svel);
        	offset += 2;
            packbuf[syncptr] |= 2;
        }
        if (angvel != osyn.angvel)
        {
        	LittleEndian.putFloat(packbuf, offset, angvel);
        	offset += 4;
            packbuf[syncptr] |= 4;
        }
        if (((bits^osyn.bits)&0x000000ff) != 0) { packbuf[offset++] = (byte) (bits&255); packbuf[syncptr] |= 8; }
        if (((bits^osyn.bits)&0x0000ff00) != 0) { packbuf[offset++] = (byte) ((bits>>8)&255); packbuf[syncptr] |= 16; }
        if (((bits^osyn.bits)&0x00ff0000) != 0) { packbuf[offset++] = (byte) ((bits>>16)&255); packbuf[syncptr] |= 32; }
        if (((bits^osyn.bits)&0xff000000) != 0) { packbuf[offset++] = (byte) ((bits>>24)&255); packbuf[syncptr] |= 64; }
        if (aimvel != osyn.aimvel)
        {
            LittleEndian.putFloat(packbuf, offset, aimvel);
            offset += 4;
            packbuf[syncptr] |= 128;
        }

		return offset;
	}

	@Override
	public void Reset() {
		this.vel = 0;
		this.svel = 0;
		this.angvel = 0;
		this.bits = 0;
		this.aimvel = 0;
	}

	@Override
	public NetInput Copy(NetInput netsrc) {
		if (netsrc == null)
			return null;
		Input src = (Input) netsrc;

		this.vel = src.vel;
		this.svel = src.svel;
		this.angvel = src.angvel;
		this.bits = src.bits;
		this.aimvel = src.aimvel;
		return this;
	}

}
