// This file is part of BuildSmacker.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildSmacker is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildSmacker is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildSmacker.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.BuildSmacker;

import java.nio.ByteBuffer;

public class Header {
	
	public enum Signature { SMK2, SMK4 };
	
	public enum AudioBits { 
		aud8bit(8), 
		aud16bit(16); 
		
		private int bits;
		AudioBits(int bits) { this.bits = bits; }
		public int get() { return bits; }
	};
	
	public enum AudioChannels { 
		Mono(0), 
		Stereo(1); 
		
		private int ch;
		AudioChannels(int ch) { this.ch = ch; }
		public int get() { return (ch + 1); }
	};
	
	public enum AudioCompression { PCM, DPCM, Bink };
	
	private static final int RING_FRAME = 0x01;

	private static final int AUDIO_COMPRESSED = 1 << 31;
	private static final int AUDIO_PRESENT = 1 << 30;
	private static final int AUDIO_BITDEPTH = 1 << 29;
	private static final int AUDIO_CHANNELS = 1 << 28;
	private static final int AUDIO_COMPRESSED_V2 = (1 << 26) | (1 << 27);
	private static final int AUDIO_RATE = 0x00FFFFFF;
	
	private Signature signature;
	public int Width;
	public int Height;
	public int Frames;
	public int FrameRate;
	public int Flags;
	
	public int TreesSize;
	public int MMap_Size;
	public int MClr_Size;
	public int Full_Size;
	public int Type_Size;
	
	public int[] AudioSize;
	public int[] AudioRate;
	
	public Header(ByteBuffer fp) throws Exception
	{
		if(fp == null || fp.capacity() == 0) {
			throw new NullPointerException("Buffer is null");
		}
		
		if (fp.get() != 'S' || fp.get() != 'M' || fp.get() != 'K') {
			throw new Exception("Invalid SMK signature");
		}

		switch(fp.get())
		{
		case '2':
			signature = Signature.SMK2;
			break;
		case '4':
			signature = Signature.SMK4;
			break;
		default:
			throw new Exception("Unsupported SMK signature");
		}
		
		Width = fp.getInt();
		Height  = fp.getInt();
		Frames = fp.getInt();
		
		int rate = fp.getInt();
		if (rate > 0) {
			FrameRate = rate * 1000;
		} else if (rate < 0) {
			FrameRate = rate * -10;
		} else {
			FrameRate = 100000;
		}
		
		Flags  = fp.getInt();
		if ((Flags & RING_FRAME) != 0) {
			Frames++;
		}
		
		AudioSize = new int[7];
		for(int i = 0; i < 7; i++) {
			AudioSize[i] = fp.getInt();
		}
		
		TreesSize = fp.getInt();
		MMap_Size = fp.getInt();
		MClr_Size = fp.getInt();
		Full_Size = fp.getInt();
		Type_Size = fp.getInt();

		AudioRate = new int[7];
		for(int i = 0; i < 7; i++) {
			AudioRate[i] = fp.getInt();
		}
		
		fp.getInt(); //Dummy
	}
	
	public boolean isAudioExists(int num)
	{
		return (AudioRate[num] & AUDIO_PRESENT) != 0;
	}
	
	public int getAudioRate(int num) {
		if(isAudioExists(num)) {
			return AudioRate[num] & AUDIO_RATE;
		}
		return -1;
	}
	
	public AudioBits getAudioBits(int num)
	{
		if(isAudioExists(num)) {
			return ((AudioRate[num] & AUDIO_BITDEPTH) != 0) ? AudioBits.aud16bit : AudioBits.aud8bit;
		}
		
		return null;
	}
	
	public AudioChannels getAudioChannels(int num)
	{
		if(isAudioExists(num)) {
			return ((AudioRate[num] & AUDIO_CHANNELS) != 0) ? AudioChannels.Stereo : AudioChannels.Mono;
		}
		
		return null;
	}
	
	public AudioCompression getAudioCompressionType(int num)
	{
		if(isAudioExists(num)) {
			if((AudioRate[num] & AUDIO_COMPRESSED) != 0) {
				return AudioCompression.DPCM;
			}
			if((AudioRate[num] & AUDIO_COMPRESSED_V2) != 0) {
				return AudioCompression.Bink;
			}
			return AudioCompression.PCM;
		}
		
		return null;
	}
	
	public Signature getSignature() {
		return signature;
	}

}
