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

import static ru.m210projects.Blood.LEVELS.kMaxMessages;

public class MapInfo {
	public String MapName;
	public String Title;
	public String Author;
	public String Song;
	public int Track;
	public int EndingA;
	public int EndingB;
	public String[] gMessage;
	public boolean Fog;
	public boolean Weather;
	
	public MapInfo() {
		gMessage = new String[kMaxMessages];
	}
	
	public void clear()
	{
		MapName = null;
		Title = null;
		Author = null;
		Song = null;
		Track = 0;
		EndingA = 0;
		EndingB = 0;
		for(int i = 0; i < kMaxMessages; i++)
			gMessage[i] = null;
		Fog = false;
		Weather = false;
	}
}
