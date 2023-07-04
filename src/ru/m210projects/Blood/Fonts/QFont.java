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

package ru.m210projects.Blood.Fonts;

import static ru.m210projects.Blood.Main.*;

import java.util.Arrays;

import ru.m210projects.Blood.Types.CHARINFO;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.Tile;

public class QFont {

	public byte charSpace;
	public byte width;
	public byte height;

	private final CHARINFO[] info; // characters info
	private final byte[] data; // image data
	private byte baseline;

	public QFont(Resource bb)
	{
    	byte[] buf = new byte[12];
    	bb.read(buf, 0, 4);

    	/* String signature = new String(buf, 0, 4); */
    	/* short version = */ bb.readShort();
    	short type = bb.readShort();
    	int totalsize = bb.readInt();
    	/* byte startChar = */ bb.readByte();
    	/* byte endChar = */ bb.readByte();
    	/* byte blending = */ bb.readByte();
    	baseline = bb.readByte();
    	/*byte tcolor = */ bb.readByte();
    	charSpace = bb.readByte();
    	width = bb.readByte();
    	height = bb.readByte();
    	bb.read(buf, 0, 12); // filler to 32 bytes boundary
    	info = new CHARINFO[256];
    	for(int i = 0; i < 256; i++)
    		info[i] = new CHARINFO(bb);

    	/* PATCH: "FONTBLOD.QFN" font fix for Russian Blood Lenin version */
		if ((width == 12) && (height == 5) && (totalsize == 40674) && (type != 0)
				&& (baseline == 0) && (charSpace == 0)) {
			baseline = 8;
			height = 21;
		}
		/* PATCH: "KFONT7.QFN" font fix for Russian Blood Lenin version */
		if ((width == 6) && (height == 8) && (totalsize == 7566) && (type != 0)
				&& (baseline == 6) && (charSpace == 1)) {
			baseline = 8;
		}
		/* PATCH: "pQFN2O.QFN" font fix for Russian Blood Lenin version */
		if ((width == 15) && (height == 15) && (totalsize == 16866) && (type != 0)
				&& (baseline == 15) && (charSpace == -1)) {
			baseline = 8;
		}

    	int len = bb.size() - bb.position();
    	this.data = new byte[len];
    	bb.read(this.data);
	}

	public void buildChar(int nBase, int nChar) {
		CHARINFO pInfo = info[nChar + 32];
		short sizeX = (short) (pInfo.cols & 0xFF);
		short sizeY = (short) (pInfo.rows & 0xFF);

		int nSize = sizeX * sizeY;

		if (nChar != 0 && nSize == 0)
			return;

		int nTile = nChar + nBase;

		if (nChar == 0) // space
			sizeX = (short) (width / 2);

		Tile pic = engine.getTile(nTile);

		pic.setWidth(sizeX);
		pic.setHeight(height);

		byte[] waloff = new byte[sizeX * height];

		Arrays.fill(waloff, (byte) 0xFF);
		int voffset = baseline + pInfo.voffset;
		for (int y = 0; y < sizeY; y++) {
			if (y + voffset >= height)
				break;
			for (int x = 0; x < sizeX; x++) {
				if (voffset + y + x * height >= 0)
					waloff[voffset + y + x * height] = data[pInfo.offset + y + x * sizeY];
			}
		}

		pic.data = waloff;
	}

}
