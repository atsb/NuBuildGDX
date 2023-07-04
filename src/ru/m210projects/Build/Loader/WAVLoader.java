//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Loader;

import java.nio.ByteBuffer;

import ru.m210projects.Build.Audio.SoundData;
import ru.m210projects.Build.Types.LittleEndian;

public class WAVLoader extends SoundData {

	public WAVLoader(byte[] data) throws Exception
	{
		if(data.length <= 44)
			throw new Exception("Wrong file size");

		getInfo(data);
		this.data = ByteBuffer.allocateDirect(data.length - 44);
		this.data.put(data, 44, this.data.capacity());
		this.data.rewind();
	}

	private void getInfo(byte[] data) throws Exception
	{
		int ptr = 0;
		int riff_id = LittleEndian.getInt(data, ptr); ptr += 4;
		if(riff_id != 0x46464952) //RIFF
			throw new Exception("RIFF header not found");

		ptr += 4; //chunk size
		int format = LittleEndian.getInt(data, ptr); ptr += 4;
		if(format != 0x45564157) //WAVE
			throw new Exception("Invalid wave file header");
		int fmt_id = LittleEndian.getInt(data, ptr); ptr += 4;
		if(fmt_id != 0x20746d66) //fmt
			throw new Exception("Invalid wave file header");

		int pcm_id = LittleEndian.getInt(data, ptr); ptr += 4;
		if(pcm_id != 16)
			throw new Exception("WAV files must be PCM: " + pcm_id);

		ptr += 2; //audio format
		channels = LittleEndian.getShort(data, ptr); ptr += 2;
		if (channels != 1 && channels != 2)
			throw new Exception("WAV files must have 1 or 2 channels: " + channels);
		rate = LittleEndian.getInt(data, ptr); ptr += 4;
		ptr += 4; //byte rate
		ptr += 2; //block align
		bits = LittleEndian.getShort(data, ptr);
	}
}
