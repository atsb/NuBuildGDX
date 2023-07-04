// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP.Types;

import java.io.FileNotFoundException;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;

public class MovieFile {

	private byte[] pal;
	private byte[] buffer;
	private Frame[] frames;
	private int rate;
	private Resource fil;
	
	private int width, height;

	private class Frame {
		public static final int sizeof = 16;

		int size; /* whole frame size */
		int type; /* must be 0xF1FA */
		int chunks; /* chunks in frame */
		int offset;

		public Frame(Resource fil) {
			size = fil.readInt(); /* whole frame size */
			type = fil.readShort(); /* must be 0xF1FA */
			chunks = fil.readShort(); /* chunks in frame */
			fil.seek(8, Whence.Current);
			offset = fil.position();
		}
	}

	public MovieFile(String path) throws Exception {
		fil = BuildGdx.cache.open(path, 0);
		if(fil == null)
			throw new FileNotFoundException(path + " not found!");
		int size = fil.readInt(); /* whole file size */
		if(size != fil.size())
			throw new Exception("Wrong size");
		
		int type = fil.readShort(); /* must be 0xAF11 */
		if (type != (short) 0xAF11)
			throw new Exception("Wrong type");

		int numframes = fil.readShort(); /* total frames in file */
		width = fil.readShort(); /* video frame width */
		height = fil.readShort(); /* video frame height */
		rate = fil.readShort();
		fil.readShort(); /* ? always 3 */
		fil.readShort(); /* ? always 0xF or 0xA - transparent/background color? frame time? */
		fil.seek(110, Whence.Current); /* padding to 128 bytes (0x80) */

		buffer = new byte[width * height];
		frames = new Frame[numframes];
		
		for (int i = 0; i < numframes; i++) {
			frames[i] = new Frame(fil);
			
			if(pal == null) {
				for (int j = 0; j < frames[i].chunks; j++) {
					fil.seek(4, Whence.Current);
					if(fil.readShort() == 0x0B)
					{
						pal = new byte[768];
						decode_palette(fil);
					}
				}
			}
			
			fil.seek(frames[i].offset + frames[i].size - Frame.sizeof, Whence.Set);
		}
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int numFrames()
	{
		if(frames != null)
			return frames.length;
		return 0;
	}
	
	public byte[] getPalette()
	{
		return pal;
	}
	
	public int getRate()
	{
		return rate;
	}
	
	public void close() {
		if(fil != null)
			fil.close();
		buffer = null;
		pal = null;
		frames = null;
	}
	
	public byte[] draw(int frame)
	{
		Frame fr = frames[frame];
		fil.seek(fr.offset, Whence.Set);
		
		if (fr.type != (short) 0xF1FA)
			return null;

		for (int j = 0; j < fr.chunks; j++) {
			int chunk_size = fil.readInt();
			int chunk_type = fil.readShort();
			switch (chunk_type) {
			case 0x0B:
				decode_palette(fil);
				break;
			case 0x0C:
				decode_block_0c(fil);
				break;
			case 0x0F:
				decode_block_0f(fil);
				break;
			case 0x10:
				fil.read(buffer, 0, chunk_size - 4);
				break;
			default:
				continue; // unknown chunk - skip it
			}
		}
		
		return buffer;
	}
	
	private void decode_palette(Resource fil)
	{
		fil.seek(4, Whence.Current); // (skip uint32_t - unused, always 1?)
		fil.read(pal);
		for(int p = 0; p < 768; p++)
			pal[p] <<= 2;
	}

	private void decode_block_0c(Resource fil) {
		int line = 320 * fil.readShort();
		int nextline = line;
		int width = fil.readShort();
		for (int j = 0; j < width; j++) {
			nextline += 320;
			
			int v21 = fil.readByte() & 0xFF;
			if (v21 > 0) {
				for (int i = 0; i < v21; i++) {
					line += fil.readByte() & 0xFF;
					int v20 = fil.readByte() & 0xFF;
					if (v20 < 128) {
						for(int p = 0; p < v20; p++)
							buffer[line + p] = fil.readByte();
						line += v20;
					}
					if (v20 > 128) {
						byte col = fil.readByte();
						for(int p = 0; p < 256 - v20; p++)
							buffer[line + p] = col;
						line += (256 - v20);
					}
				}
			}
			
			line = nextline;
		}
	}

	private void decode_block_0f(Resource fil) {
		int line = 0;
		for (int i = 1; i <= 200; i++) {
			int v18 = fil.readByte() & 0xFF;
			if (v18 > 0) {
				for (int j = 0; j < v18; j++) {
					int v8 = fil.readByte() & 0xFF;
					if (v8 > 128) {
						for(int p = 0; p < 256 - v8; p++)
							buffer[line + p] = fil.readByte();
						line += 256 - v8;
					}
					if (v8 < 128) {
						byte col = fil.readByte();
						for(int p = 0; p < v8; p++)
							buffer[line + p] = col;
						line += v8;
					}
				}
			}
			line = 320 * i;
		}
	}
}
