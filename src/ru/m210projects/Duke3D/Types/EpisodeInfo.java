// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D.Types;

import static ru.m210projects.Duke3D.Globals.*;

public class EpisodeInfo {

	public String Title;
	public int nMaps;
	
	public MapInfo[] gMapInfo;

	public EpisodeInfo() {
		gMapInfo = new MapInfo[nMaxMaps + 1];
	}
	
	public EpisodeInfo(String title) {
		this.Title = title;
		gMapInfo = new MapInfo[nMaxMaps + 1];
	}

	public void clear()
	{
		Title = null;
		nMaps = 0;

		for(int i = 0; i <= nMaxMaps; i++)
			if(gMapInfo[i] != null) 
				gMapInfo[i].clear();
	}
}
