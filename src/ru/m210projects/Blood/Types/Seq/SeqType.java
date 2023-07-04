package ru.m210projects.Blood.Types.Seq;

import static ru.m210projects.Blood.Tile.tilePreloadTile;

import java.util.HashMap;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;

public class SeqType {
	
	private static final HashMap<Integer, SeqType> pSEQs = new HashMap<Integer, SeqType>();
	public static SeqType getInstance(int nSeqId)
	{
		SeqType pSequence = pSEQs.get(nSeqId);
		if(pSequence == null)
		{
			Resource data = BuildGdx.cache.open(nSeqId, "SEQ");
			if(data != null) {
				pSequence = new SeqType(data);
				pSEQs.put(nSeqId, pSequence);
				data.close();
			}
		}
		
		return pSequence;
	}
	
	public static void flushCache()
	{
		pSEQs.clear();
	}

	private final int kSeqLoop = 1;
	private final int kSeqRemove	= 2;
	private final int kSEQSig = 0x1A514553; //SEQ\032;
	
	protected int nFrames;
	protected int ticksPerFrame;
	protected int soundId;
	protected byte flags;
	protected SeqFrame[] frame;
	
	public SeqType(Resource bb) {
		int signature = bb.readInt();
 	
    	if(signature != kSEQSig)
			System.err.println("Invalid sequence");

    	short version = bb.readShort();
    	if( (version & 0xFF00) != 0x0300 )
			System.err.println("Obsolete sequence version");
    	
    	nFrames = bb.readShort();
    	ticksPerFrame = bb.readShort();
    	soundId = bb.readShort();
    	flags = bb.readByte();
    	bb.seek(3, Whence.Current); //pad

    	frame = new SeqFrame[nFrames];
    	for(int i = 0; i < nFrames; i++) 
    		frame[i] = new SeqFrame(bb);
	}
	
	public int getFrames()
	{
		return nFrames;
	}
	
	public SeqFrame getFrame(int num)
	{
		if(num < 0 || num >= nFrames)
			return null;
		
		return frame[num];
	}
	
	public int getTicks()
	{
		return ticksPerFrame;
	}
	
	public int getSound()
	{
		return soundId;
	}
	
	public boolean isLooping()
	{
		return (flags & kSeqLoop) != 0;
	}
	
	public boolean isRemovable()
	{
		return (flags & kSeqRemove) != 0;
	}

	public void Preload() {
		if(nFrames > 0) {
			for(int i = 0; i < nFrames; i++) {
				tilePreloadTile(frame[i].nTile);
			}
		}
	}	
}
