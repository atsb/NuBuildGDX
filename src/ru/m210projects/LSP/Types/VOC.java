package ru.m210projects.LSP.Types;

import java.nio.ByteBuffer;

import ru.m210projects.Build.Types.LittleEndian;

public class VOC {
	public int samplerate;
	public int channels;
	public int samplesize;
	public ByteBuffer sampledata;
	
	private int datalen;
	
	public VOC(byte[] data)
	{
		if(data == null)
			return;
		
		getInfo(data);
		boolean eightbit = samplesize == 8;
		byte[] buf = convertData(data, datalen, eightbit);
		
		int samplelength = buf.length;
		if(!eightbit && (samplelength % 2) == 1)
			samplelength++;
		
		sampledata = ByteBuffer.allocateDirect(samplelength);
		sampledata.put(buf);
		sampledata.rewind();
	}
	
	private void getInfo(byte[] data)
	{
		byte blocktype=0;
		int  blocklen=0;
		
		String signature = new String(data, 0, 19);
		if(!signature.equalsIgnoreCase("Creative Voice File") && data[19] != 0x1A) return;
		
		int tptr = 22;
		short version = LittleEndian.getShort(data, tptr); tptr += 2;
		short checksum = LittleEndian.getShort(data, tptr); tptr += 2;

		if (~version + 0x1234 != checksum) return;

		datalen = 0;
		while (true) {
			if (tptr >= data.length - 4) return;	// truncated
			
			blocktype = data[tptr];
			if (blocktype == 0)
				break;

			blocklen = LittleEndian.getInt(data, tptr + 1) & 0x00FFFFFF;
			tptr += 4;

			int ptr = tptr;
			switch (blocktype) {
				case 1: /* sound data begin block */
					samplerate = 1000000 / (256 - (data[tptr++] & 0xFF));
					if (data[tptr++] != 0) {
						/* only 8-bit files please */
						return;
					}
					
					if (ptr + blocklen >= data.length) 
						blocklen = data.length - ptr - 1;
					
					channels = 1;
					samplesize = 8;
					datalen += blocklen-2;

					tptr = ptr + blocklen;
					break;

				case 2: /* sound continue */
					if (ptr + blocklen >= data.length) 
						blocklen = data.length - ptr - 1;
					
					datalen += blocklen;
					tptr = ptr + blocklen;
					break;
					
				case 8: /* sound attribute extension block */
					int rate = LittleEndian.getShort(data, tptr); tptr += 2;
					samplerate = (256000000/(65536-(rate & 0xFFFF)));
					samplesize = 8;
					
					if (data[tptr + 1] == 1) {
						samplerate >>= 1;
						channels = 2;
					} else
						channels = 1;
					
					if (data[tptr] != 0) {
						/* only 8-bit files please */
						return;
					}
					/* a block 1 follows */
					
					tptr += 2;
					break;

				case 9: /* sound data format 3 */
					samplerate = LittleEndian.getInt(data, tptr); tptr += 4;
					
					samplesize = data[tptr] & 0xFF;
					if(samplesize == 0) samplesize = 8;
					channels   = data[tptr + 1] & 0xFF;
					
					if (((data[tptr + 2] & 0xFF) | ((data[tptr + 3] & 0xFF) << 8)) != 0 &&
					    ((data[tptr + 2] & 0xFF) | ((data[tptr + 3] & 0xFF) << 8)) != 4) {
						/* only PCM please */
						return;
					}
					tptr += 4;
					
					if (ptr + blocklen >= data.length) 
						blocklen = data.length - ptr - 1;
					datalen += blocklen-12;
					tptr = ptr + blocklen;
					break;

				default:
					if (ptr + blocklen >= data.length) 
						blocklen = data.length - ptr - 1;
					
					tptr = ptr + blocklen;
					break;	
			}
		}
	}

	private byte[] convertData(byte[] data, int bufferlen, boolean eightbit)
	{
		byte blocktype = 0;
		int  blocklen = 0, br;
		
		byte[] buf = new byte[bufferlen];

		int pos = 0x14 + 2 + 4;
		int bufptr = 0;

		while (bufferlen>0) {
			blocktype = data[pos++];
			if (blocktype == 0)
				break;

			blocklen = (data[pos++] & 0xFF);
			blocklen |= (data[pos++] & 0xFF)  << 8;
			blocklen |= (data[pos++] & 0xFF) << 16;

			switch (blocktype) {
				case 1: /* sound data */
					pos += 2;
					br = Math.min(blocklen-2, bufferlen);
					break;
				case 2: /* sound continue */
					br = Math.min(blocklen, bufferlen);
					break;
				case 9: /* sound data format 3 */
					pos += 12;
					br = Math.min(blocklen-12, bufferlen);
					break;
				default:
					pos += blocklen;
					continue;
			}
			
			System.arraycopy(data, pos, buf, bufptr, br);
			bufferlen -= br;
			bufptr += br;
			pos += br;
		}

		return buf;
	}
}
