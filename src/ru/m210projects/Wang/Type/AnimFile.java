package ru.m210projects.Wang.Type;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Build.Pattern.ScreenAdapters.MovieScreen.MovieFile;

public class AnimFile implements MovieFile {
	
	int framecount;          // current frame of anim
	Header lpheader;           // file header will be loaded into this structure
	Descriptor[] LpArray; // arrays of large page structs used to find frames
	int curlpnum;               // initialize to an invalid Large page number
	Descriptor curlp;        // header of large page currently in memory
	byte[] imagebuffer; // buffer where anim frame is decoded
	byte[] buffer;
	byte[] pal;
	int currentframe;
	int thepage;
	   
	public AnimFile(byte[] data)
	{
		imagebuffer = new byte[0x10000];
		LpArray = new Descriptor[256];
		pal = new byte[768];
		
		buffer = data;
		curlpnum = 0xffff;
		currentframe = -1;
		
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.order( ByteOrder.LITTLE_ENDIAN);
		lpheader = new Header(bb);
		bb.position(bb.position() + 128);

		// load the color palette
		for (int i = 0; i < 768; i += 3)
		{
			pal[i+2] = bb.get();
			pal[i+1] = bb.get();
			pal[i] = bb.get();
			bb.get();
		}

		// read in large page descriptors
		for(int i = 0; i < 256; i++)
			LpArray[i] = new Descriptor(bb);
	}

	private void drawframe(int framenumber)
	{
		int pagenumber = findpage(framenumber);
		int offset = loadpage(pagenumber);
		try {
			renderframe(framenumber, offset);
		} catch (Throwable t) {}
	}
	
	private int findpage(int framenumber)
	{
		int i;
		for(i=0; i<lpheader.nLps; i++)
		{
			if(LpArray[i].baseRecord <= framenumber &&
					LpArray[i].baseRecord + LpArray[i].nRecords > framenumber)
				return(i);
		}
		return(i);
	}
	
	private int loadpage (int pagenumber)
	{
	   if (curlpnum != pagenumber)
	   {
	      curlpnum = pagenumber;
	      curlp = LpArray[pagenumber];
	      
	      thepage = 0xb00 + (pagenumber*0x10000) + 8;
	   }
	   
	   return thepage;
	}
	
	private void renderframe (int framenumber, int pagepointer)
	{
		int offset=0;
		int destframe = framenumber - curlp.baseRecord;
	  	for(int i = 0; i < 2 * destframe; i += 2) 
	  		offset += LittleEndian.getUShort(buffer, i+pagepointer);

	  	pagepointer += curlp.nRecords*2+offset;
	  	
	  	if(buffer[pagepointer + 1] != 0) 
	  		pagepointer += (4 + (buffer[pagepointer+1] + (buffer[pagepointer+1] & 1))); //XXX
	  	else
	  		pagepointer += 4;

	  	CPlayRunSkipDump (pagepointer, imagebuffer);
	}
	
	private void CPlayRunSkipDump(int srcPtr, byte[] dst)
	{
		int dstP = 0;
		while(true) 
		{
			if(srcPtr >= buffer.length)
				break;
			
			byte cnt = buffer[srcPtr++];
			if (cnt > 0)
			{
				do
				{
					dst[dstP++] = buffer[srcPtr++];
				} while ((--cnt) != 0);
				continue;
			}
			
			if (cnt == 0)
			{
				int wordCnt = buffer[srcPtr++] & 0xFF;
				byte pixel = buffer[srcPtr++];
				do
				{
					dst[dstP++] = pixel;
				} while ((--wordCnt) != 0);
				continue;
			}
			
			cnt -= 0x80;
			if (cnt == 0)
			{
				int wordCnt = LittleEndian.getUShort(buffer, srcPtr);
				srcPtr += 2;
				if (wordCnt == 0) break; /* all done */
				
				if ((short)wordCnt < 0) /* Do SIGNED test. */
				{
					wordCnt -= 0x8000;              /* Remove sign bit. */
					if (wordCnt >= 0x4000)
					{
						wordCnt -= 0x4000;        	 /* Clear "longRun" bit. */
						byte pixel = buffer[srcPtr++];
						do
						{
							dst[dstP++] = pixel;
						} while ((--wordCnt) != 0);
						continue;
					}
					
					/* longDump. */
					do
					{
						dst[dstP++] = buffer[srcPtr++];
					} while ((--wordCnt) != 0);
					continue;
				}
				dstP += wordCnt; 
				
				continue;
			}
			dstP += cnt; /* adding 7-bit count to 32-bit pointer */
		}
	}
	
	private static class Descriptor
	{
		int baseRecord;	   		// Number of first record in this large page.
		int nRecords;	        // Number of records in lp.
										// bit 15 of "nRecords" == "has continuation from previous lp".
		                        		// bit 14 of "nRecords" == "final record continues on next lp".
//		int nBytes;             // Total number of bytes of contents, excluding header.  
		
		public Descriptor(ByteBuffer bb)
		{
			baseRecord = bb.getShort() & 0xFFFF;
			nRecords = bb.getShort() & 0xFFFF;
			/*nBytes = */bb.getShort(); //& 0xFFFF;
		}
	}
	
	private static class Header
	{
		int nLps;            // # largePages in this file. */
		int nRecords;        // # records in this file.  65534 is current limit plus */
		                        // one for last-to-first delta for looping the animation */
		//int maxRecsPerLp; // # records permitted in an lp. 256 FOR NOW.   */
		//int lpfTableOffset; // Absolute Seek position of lpfTable.  1280 FOR NOW.
		                                         // The lpf Table is an array of 256 large page structures
		                                         // that is used to facilitate finding records in an anim
		                                         // file without having to seek through all of the Large
		                                         // Pages to find which one a specific record lives in. */
		//int contentType;  // 4 character ID == "ANIM" */
		//int width;                   // Width of screen in pixels. */
		//int height;                  // Height of screen in pixels. */
		//byte variant;              // 0==ANIM. */
		//byte version;              // 0==frame rate is multiple of 18 cycles/sec.
		//byte hasLastDelta;   // 1==Last record is a delta from last-to-first frame. */
		//byte lastDeltaValid; // 0==The last-to-first delta (if present) hasn't been
		                                  // updated to match the current first&last frames,    so it
		                                  // should be ignored. */
                                byte[] recordTypes;//      /* Not yet implemented. */
		//int nFrames;         //   /* In case future version adds other records at end of
		                                //      file, we still know how many actual frames.
										//    NOTE: DOES include last-to-first delta when present. */
		int framesPerSecond;      // Number of frames to play per second. */
		
		public Header(ByteBuffer bb)
		{
			/*id = */bb.getInt();
			/*maxLps = */bb.getShort(); // & 0xFFFF;
			
			nLps = bb.getShort() & 0xFFFF;
			nRecords = bb.getInt();
			/*maxRecsPerLp = */bb.getShort(); // & 0xFFFF;
			/*lpfTableOffset = */bb.getShort(); // & 0xFFFF;
			 
			/*contentType = */bb.getInt();
			/*width = */bb.getShort(); // & 0xFFFF;
			/*height = */bb.getShort(); // & 0xFFFF;
			 
			/*variant = */bb.get();
			/*version = */bb.get();
			/*hasLastDelta = */bb.get();
			/*lastDeltaValid = */bb.get();
			
			/*pixelType = */bb.get();
			/*CompressionType = */bb.get();
			/*otherRecsPerFrm = */bb.get();
			/*bitmaptype = */bb.get();
			
			recordTypes = new byte[32];
			bb.get(recordTypes);
		
			/*nFrames = */bb.getInt();
			framesPerSecond = bb.getShort() & 0xFFFF;
			
			bb.position(bb.position() + 58); //pad
		}
	}
	
	@Override
	public byte[] getPalette()
	{
		return pal;
	}

	@Override
	public int getFrames() {
		 return lpheader.nRecords;
	}

	@Override
	public float getRate() {
		return 1000.0f / lpheader.framesPerSecond;
	}

	@Override
	public byte[] getFrame(int framenumber) {
		if ((currentframe != -1) && (currentframe<=framenumber))
		{
			for (int cnt = currentframe; cnt < framenumber; cnt++)
				drawframe (cnt);
		}
		else
		{
			for (int cnt = 0; cnt < framenumber; cnt++)
				drawframe (cnt);
		}

		currentframe = framenumber;
		return imagebuffer;
	}

	@Override
	public short getWidth() {
		return 200;
	}

	@Override
	public short getHeight() {
		return 320;
	}

	@Override
	public void close() {
		/* nothing */
	}
	
	@Override
	public void playAudio() {
		/* nothing */
	}
}
