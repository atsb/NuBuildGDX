// "Build Engine & Tools" Copyright (c) 1993-1997 Ken Silverman
// Ken Silverman's official web site: "http://www.advsys.net/ken"
// See the included license file "BUILDLIC.TXT" for license info.
//
// This file has been ported to Java and modified by Alexander Makarov-[M210] (m210-2007@mail.ru)

package ru.m210projects.Build.FileHandle;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ru.m210projects.Build.FileHandle.FileResource.Mode;

public class LZWDecoder implements Closeable {

	private final Resource fil;
	private final int sizeof;

	private final int LZWSIZE = 16385;
	private final byte[] lzwbuf1;
	private final byte[] lzwbuf4;
	private final byte[] lzwbuf5;
	private final short[] lzwbuf2;
	private final short[] lzwbuf3;

	public LZWDecoder(Resource res, int sizeof) {
		this.fil = res;
		this.sizeof = sizeof;

		lzwbuf1 = new byte[LZWSIZE + (LZWSIZE >> 4)];
		lzwbuf2 = new short[LZWSIZE + (LZWSIZE >> 4)];
		lzwbuf3 = new short[LZWSIZE + (LZWSIZE >> 4)];
		lzwbuf4 = new byte[LZWSIZE];
		lzwbuf5 = new byte[LZWSIZE + (LZWSIZE >> 4)];
	}

	public int read(byte[] buf, int len) {
		int dasizeof = this.sizeof;

		if (dasizeof > LZWSIZE) {
			len *= dasizeof;
			dasizeof = 1;
		}

		Short leng = fil.readShort();
		if(leng == null)
			return -1;

		if (fil.read(lzwbuf5, 0, leng) != leng)
			return -1;
		int k = 0;
		int kgoal = lzwuncompress(lzwbuf5, leng, lzwbuf4);
		System.arraycopy(lzwbuf4, 0, buf, 0, dasizeof);
		k += dasizeof;

		int ptr = 0;
		for (int i = 1, j; i < len; i++) {
			if (k >= kgoal) {
				leng = fil.readShort();
				if(leng == null) //EOF reached
					return ptr + dasizeof;
				
				if (fil.read(lzwbuf5, 0, leng) != leng)
					return -1;
				k = 0;
				kgoal = lzwuncompress(lzwbuf5, leng, lzwbuf4);
			}
			for (j = 0; j < dasizeof; j++)
				buf[ptr + j + dasizeof] = (byte) ((buf[ptr + j] + lzwbuf4[j + k]) & 255);
			k += dasizeof;
			ptr += dasizeof;
		}
		return ptr + dasizeof;
	}

	public int write(byte[] buf, int len) {
		if (!(fil instanceof FileResource))
			throw new UnsupportedOperationException("unsupported, won't implement");

		FileResource fil = (FileResource) this.fil;
		
		if(fil.isClosed() || fil.getMode() != Mode.Write)
			return -1;
		
		int dasizeof = this.sizeof;

		if (dasizeof > LZWSIZE) {
			len *= dasizeof;
			dasizeof = 1;
		}
		System.arraycopy(buf, 0, lzwbuf4, 0, dasizeof);
		int k = dasizeof;

		int leng, ptr = 0;
		if (k > LZWSIZE - dasizeof) {
			leng = lzwcompress(lzwbuf4, k, lzwbuf5);
			k = 0;

			fil.writeShort(leng);
			fil.writeBytes(lzwbuf5, leng);
		}

		for (int i = 1, j; i < len; i++) {
			for (j = 0; j < dasizeof; j++)
				lzwbuf4[j + k] = (byte) ((buf[ptr + j + dasizeof] - buf[ptr + j]) & 255);
			k += dasizeof;
			if (k > LZWSIZE - dasizeof) {
				leng = lzwcompress(lzwbuf4, k, lzwbuf5);
				k = 0;
				fil.writeShort(leng);
				fil.writeBytes(lzwbuf5, leng);
			}
			ptr += dasizeof;
		}

		if (k > 0) {
			leng = lzwcompress(lzwbuf4, k, lzwbuf5);

			fil.writeShort(leng);
			fil.writeBytes(lzwbuf5, leng);
		}
		
		return len;
	}

	@Override
	public void close() {
		fil.close();
	}

	private int lzwcompress(byte[] lzwinbuf, int uncompleng, byte[] lzwoutbuf) {
		for (int i = 255; i >= 0; i--) {
			lzwbuf1[i] = (byte) i;
			lzwbuf3[i] = (short) ((i + 1) & 255);
		}
		Arrays.fill(lzwbuf2, 0, 256, (short) -1);
		Arrays.fill(lzwoutbuf, 0, ((uncompleng + 15) + 3), (byte) 0);

		ByteBuffer outbuf = ByteBuffer.wrap(lzwoutbuf);
		outbuf.order(ByteOrder.LITTLE_ENDIAN);

		short addrcnt = 256;
		int bytecnt1 = 0;
		int bitcnt = (4 << 3);
		int numbits = 8;
		int oneupnumbits = (1 << 8);
		short addr;
		do {
			addr = (short) (lzwinbuf[bytecnt1] & 0xFF);
			do {
				bytecnt1++;
				if (bytecnt1 == uncompleng)
					break;
				if (lzwbuf2[addr] < 0) {
					lzwbuf2[addr] = addrcnt;
					break;
				}
				short newaddr = lzwbuf2[addr];
				while (lzwbuf1[newaddr] != lzwinbuf[bytecnt1]) {
					short zx = lzwbuf3[newaddr];
					if (zx < 0) {
						lzwbuf3[newaddr] = addrcnt;
						break;
					}
					newaddr = zx;
				}
				if (lzwbuf3[newaddr] == addrcnt)
					break;
				addr = newaddr;
			} while (addr >= 0);
			lzwbuf1[addrcnt] = lzwinbuf[bytecnt1];
			lzwbuf2[addrcnt] = -1;
			lzwbuf3[addrcnt] = -1;

			int intptr = outbuf.getInt(bitcnt >> 3);
			outbuf.putInt(bitcnt >> 3, intptr | (addr << (bitcnt & 7)));

			bitcnt += numbits;
			if ((addr & ((oneupnumbits >> 1) - 1)) > ((addrcnt - 1) & ((oneupnumbits >> 1) - 1)))
				bitcnt--;

			addrcnt++;
			if (addrcnt > oneupnumbits) {
				numbits++;
				oneupnumbits <<= 1;
			}
		} while ((bytecnt1 < uncompleng) && (bitcnt < (uncompleng << 3)));

		int intptr = outbuf.getInt(bitcnt >> 3);
		outbuf.putInt(bitcnt >> 3, intptr | (addr << (bitcnt & 7)));

		bitcnt += numbits;
		if ((addr & ((oneupnumbits >> 1) - 1)) > ((addrcnt - 1) & ((oneupnumbits >> 1) - 1)))
			bitcnt--;

		outbuf.putShort(0, (short) uncompleng);
		if (((bitcnt + 7) >> 3) < uncompleng) {
			outbuf.putShort(2, addrcnt);
			return ((bitcnt + 7) >> 3);
		}

		outbuf.putShort(2, (short) 0);
		for (int i = 0; i < uncompleng; i++)
			outbuf.put(i + 4, lzwinbuf[i]);

		return (uncompleng + 4);
	}

	private int lzwuncompress(byte[] lzwinbuf, int compleng, byte[] lzwoutbuf) {
		ByteBuffer inbuf = ByteBuffer.wrap(lzwinbuf);
		inbuf.order(ByteOrder.LITTLE_ENDIAN);

		int strtot = inbuf.getShort(2);

		inbuf.position(4);
		if (strtot == 0) {
			inbuf.get(lzwoutbuf, 0, (compleng - 4) + 3);
			return inbuf.getShort(0); // uncompleng
		}

		for (int i = 255; i >= 0; i--) {
			lzwbuf2[i] = (short) i;
			lzwbuf3[i] = (short) i;
		}
		int currstr = 256, bitcnt = (4 << 3), outbytecnt = 0;
		int numbits = 8, oneupnumbits = (1 << 8), intptr, dat, leng;
		do {
			intptr = inbuf.getInt(bitcnt >> 3);
			dat = ((intptr >> (bitcnt & 7)) & (oneupnumbits - 1));
			bitcnt += numbits;
			if ((dat & ((oneupnumbits >> 1) - 1)) > ((currstr - 1) & ((oneupnumbits >> 1) - 1))) {
				dat &= ((oneupnumbits >> 1) - 1);
				bitcnt--;
			}

			lzwbuf3[currstr] = (short) dat;

			for (leng = 0; dat >= 256; leng++, dat = lzwbuf3[dat])
				lzwbuf1[leng] = (byte) lzwbuf2[dat];

			lzwoutbuf[outbytecnt++] = (byte) dat;
			for (int i = leng - 1; i >= 0; i--)
				lzwoutbuf[outbytecnt++] = lzwbuf1[i];

			lzwbuf2[currstr - 1] = (short) dat;
			lzwbuf2[currstr] = (short) dat;
			currstr++;
			if (currstr > oneupnumbits) {
				numbits++;
				oneupnumbits <<= 1;
			}
		} while (currstr < strtot);
		return (inbuf.getShort(0)); // uncompleng
	}
}
