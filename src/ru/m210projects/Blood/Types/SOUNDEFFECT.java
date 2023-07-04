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

package ru.m210projects.Blood.Types;

import java.nio.ByteBuffer;

import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.Types.SPRITE;

public class SOUNDEFFECT {
	
	public Source hVoice;
	public ByteBuffer hResource;
	public int left; 
	public int right;
	public int soundId; 
	public SPRITE pSprite;
	public int nPitch;
	public int relVol;
	public int x;
	public int y;
	public int z;
	public int oldX;
	public int oldY;
	public int oldZ;
	public int nSector;
	public int format;
	public int channel;
	
	public SOUNDEFFECT()
	{
		this.hVoice = null;
		this.soundId = -1; 
	}
	
	public void clear()
	{
		hVoice = null;
		left = 0; 
		right = 0; 
		soundId = -1; 
		pSprite = null;
		hResource = null;
		channel = 0;
		nPitch = 0;
		relVol = 0;
		x = 0;
		y = 0;
		z = 0;
		oldX = 0;
		oldY = 0;
		oldZ = 0;
		nSector = 0;
		format = 0;
	}
}
