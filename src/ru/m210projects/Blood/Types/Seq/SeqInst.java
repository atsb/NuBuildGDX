// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood.Types.Seq;

import static ru.m210projects.Blood.Main.game;
import static ru.m210projects.Blood.VERSION.getCallback;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.callbacks;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.getIndex;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Blood.Types.CALLPROC;
import ru.m210projects.Build.FileHandle.Resource;

public abstract class SeqInst {
	
	public static final int sizeof = 20;
	private static final ByteBuffer buffer = ByteBuffer.allocate(sizeof).order( ByteOrder.LITTLE_ENDIAN);
	
	protected SeqType pSequence;
	protected CALLPROC callback;
	protected int nSeqID;
	protected short timeCounter;	// age of current frame in ticks
	protected short frameIndex;		// current frame
	protected boolean isPlaying;

	protected abstract void updateInstance(int index);
	
	protected void update( int index )
	{
		if(frameIndex >= pSequence.getFrames())
			game.dassert("frameIndex < pSequence.nFrames");
		
		this.updateInstance(index);

		if ( pSequence.getFrame(frameIndex).trigger && callback != null ) 
		{
			callback.run(index);
        }
	}
	
	public int getSeqIndex()
	{
		return nSeqID;
	}
	
	public boolean isPlaying()
	{
		return isPlaying;
	}
	
	public byte[] getBytes()
	{
		buffer.clear();
		
		buffer.putInt(0); // nSeq
		buffer.putInt(0); // pSeq
		buffer.putInt(nSeqID);
		buffer.putInt(getIndex(callback));
		buffer.putShort(timeCounter);
		buffer.put((byte) frameIndex);
		buffer.put(isPlaying ? (byte) 1 : 0);
		
		return buffer.array();
	}
	
	public void load(Resource bb)
	{
		bb.readInt(); //nSeq
		bb.readInt(); //pSeq

		this.pSequence = null;
		this.nSeqID = bb.readInt();
		int callb = bb.readInt();
		if(callb != -1) {
			if(callb > callbacks.length)
				this.callback = getCallback(callb);
			else this.callback = callbacks[callb];
		}
		this.timeCounter = bb.readShort();
		this.frameIndex = (short) (bb.readByte() & 0xFF);
		this.isPlaying = bb.readBoolean();
	}
	
	public void clear()
	{
		this.pSequence = null;
		this.callback = null;
		this.nSeqID = 0;
		this.timeCounter = 0;
		this.frameIndex = 0;
		this.isPlaying = false;
	}
	
	public void copy(SeqInst src)
	{
		this.pSequence = src.pSequence;
		this.nSeqID = src.nSeqID;
		this.timeCounter = src.timeCounter;
		this.frameIndex = src.frameIndex;
		this.isPlaying = src.isPlaying;
	}
}
