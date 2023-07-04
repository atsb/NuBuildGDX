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

import ru.m210projects.Build.Gameutils;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.FileHandle.Resource;

public class PCXFile {

	private int width, height, pos;
	private byte[] buffer;
	private byte[] palette;

	public PCXFile(Resource fil) throws Exception {
		byte manufacturer = fil.readByte();
		byte version = fil.readByte();
		byte rle = fil.readByte();
		byte bpp = fil.readByte();

		if (manufacturer != 10 || version != 5 || rle != 1 || bpp != 8)
			throw new UnsupportedOperationException("Couldn't read the file. Unsupported version");

		fil.seek(4, Whence.Current);
		width = fil.readShort() + 1;
		height = fil.readShort() + 1;
		fil.seek(116, Whence.Current);

		pos = 0;
		buffer = new byte[width * height];
		for (int y = 0; y < height; y++)
			decodeLine(fil);

		fil.readByte();
		if (fil.remaining() == 768) {
			palette = new byte[768];
			fil.read(palette);
		}
		
		fil.close();
	}

	private void decodeLine(Resource fil) {
		int i = 0, len;
		while (i < width) {
			byte header = fil.readByte();
			switch (header & 0xC0) {
			case 0xC0:
				len = header & 0x3F;
				Gameutils.fill(buffer, pos, pos += len, fil.readByte());
				i += len;
				break;
			default:
				buffer[pos++] = header;
				i++;
				break;
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public byte[] getData() {
		return buffer;
	}

	public byte[] getPalette() {
		return palette;
	}
}
